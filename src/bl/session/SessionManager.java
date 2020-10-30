package bl.session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.*;

import com.fazecast.jSerialComm.SerialPort;

import bl.TestbedClient;
import data.SerialDataHandler;

public class SessionManager {
	public final static int CREATE_SESSION_EVENT = 0;
	public final static int DESTROY_SESSION_EVENT = 1;
	public final static int UPDATE_SESSION_EVENT = 2;
	private static SessionManager manager = new SessionManager();
	private int currentSessionId = 0;
	private TestbedClient testbedClient = null;
	private Map<Integer, Session> sessionMap;
	private Set<SessionEventHandler> handlerList;
	
	private SessionManager() {
		sessionMap = new HashMap<Integer, Session>();
		handlerList = new HashSet<SessionEventHandler>();
	}
	
	public void setTestbedClient(TestbedClient testbedClient) {
		this.testbedClient = testbedClient;
	}
	
	private boolean isTestbedClientConnected() {
		if(testbedClient!=null) {
			return testbedClient.isConnected();
		}
		return false;
	}
	
	public static SessionManager getSessionManager() {
		return manager;
	}
	
	public void addListener(SessionEventHandler h) {
		handlerList.add(h);
	}
	
	public void removeListener(SessionEventHandler h) {
		handlerList.remove(h);
	}
	
	public void trigger(SessionEvent e) {
		if(handlerList == null) return;
		for(SessionEventHandler h:handlerList)
			h.handle(e);
	}
	
	public class UpdateRemoteIdThread implements Runnable{
		private int sessionId;
		public UpdateRemoteIdThread(int sessionId) {
			this.sessionId = sessionId;
		}

		@Override
		public void run() {
			if(isTestbedClientConnected()) {
				if(this.sessionId>=0) {
					int[] historyRemoteIdList = {-1};
					List<Integer> remoteIdList = testbedClient.requestRemoteId(1, historyRemoteIdList);
					if(remoteIdList!=null) {
						getSession(this.sessionId).setRemoteId(remoteIdList.get(0));
						trigger(new SessionEvent(this, UPDATE_SESSION_EVENT, this.sessionId));
					}
				}else {
					Set<Integer> currentSessionList;
					Integer[] currentRemoteIdList;
					synchronized(manager) {
						currentSessionList = getSessionList();
						currentRemoteIdList = getRemoteIdList();
					}
					List<Integer> remoteIdList = testbedClient.requestRemoteId(currentSessionList.size(), currentRemoteIdList);
					if(remoteIdList!=null) {
						List<Integer> noRemoteIdSessionList = new ArrayList<Integer>();
						for(Integer sessionId:currentSessionList) {
							Integer preRemoteId = getSession(sessionId).getRemoteId();
							if(remoteIdList.contains(preRemoteId)) {
								remoteIdList.remove(preRemoteId);
							}else {
								noRemoteIdSessionList.add(sessionId);
							}
						}
						System.out.println("Remote id list size: "+remoteIdList.size());
						for(int i=0;i<remoteIdList.size();i++) {
							int sessionId = noRemoteIdSessionList.get(i);
							getSession(sessionId).setRemoteId(remoteIdList.get(i));
							trigger(new SessionEvent(this, UPDATE_SESSION_EVENT, sessionId));
						}
					}
				}
			}
		}
	}
	
	public int createSession(String serialPort, int buadrate, String logPath, boolean isRecord, boolean isStartAtMidnight, boolean isAppendToFile, int remoteId) {
		synchronized(manager) {
			SerialDataHandler handler = new SerialDataHandler(serialPort, buadrate);
			SerialSession ss = new SerialSession(currentSessionId, serialPort, buadrate, logPath, isRecord, isStartAtMidnight, isAppendToFile, handler, remoteId);
			sessionMap.put(currentSessionId, ss);
			this.trigger(new SessionEvent(this, CREATE_SESSION_EVENT, currentSessionId));
		}
		if(remoteId<0) {
			Thread updateRemoteIdThread = new Thread(new UpdateRemoteIdThread(currentSessionId));
			updateRemoteIdThread.start();
		}
		return currentSessionId++;
	}
	
	public boolean destroySession(int id) {
		synchronized(manager) {
			sessionMap.remove(id);
			this.trigger(new SessionEvent(this, DESTROY_SESSION_EVENT, id));
		}
		return true;
	}
	
	public Session getSession(int id) {
		return sessionMap.get(id);
	}
	
	public int size() {
		return sessionMap.size();
	}
	
	public boolean contains(String name) {
		for(Session s:sessionMap.values()) {
			if(s.getName().equals(name))
				return true;
		}
		return false;
	}
	
	public boolean contains(int serialPort) {
		for(int key:sessionMap.keySet()) {
			if(key==serialPort)
				return true;
		}
		return false;
	}
	
	public int getSessionId(String name) {
		for(Session s:sessionMap.values()) {
			if(s.getName().equals(name))
				return s.getSesionId();
		}
		return -1;
	}
	
	public int getSessionId(int remoteId) {
		for(Session s:sessionMap.values()) {
			if(s.getRemoteId()==remoteId)
				return s.getSesionId();
		}
		return -1;
	}
	
	public String[] getSessionNameList() {
		String[] nameList = new String[sessionMap.values().size()];
		int i = 0;
		for(Session s: sessionMap.values()) {
			nameList[i] = s.getName();
			i++;
		}
		return nameList;
	}
	
	public Set<Integer> getSessionList() {
		return sessionMap.keySet();
	}
	
	public static String[] getSerialPortList() {
		Properties props = System.getProperties();
		String osName = props.getProperty("os.name");
        List<String> result = new ArrayList<String>();
        if(osName.startsWith("Windows")){
        	SerialPort[] portList = SerialPort.getCommPorts();
        	for(SerialPort port:portList){
        		String portDes = port.getDescriptivePortName();
        		result.add((portDes.split("\\(")[1]).split("\\)")[0]);
        	}
        }else if(osName.startsWith("Mac")){
        	Process p = null;
            try {
                p = new ProcessBuilder("ls", "/dev").start();
            } catch (Exception e) {
                return null;
            }
            InputStream inputStream = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String s = "";
            try {
                while ((s = br.readLine()) != null) {
                	if(s.startsWith("cu"))
                    	result.add(s);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return toArray(result, String.class);
	}
	
	public Integer[] getRemoteIdList() {
		List<Integer> remoteIdList = new ArrayList<Integer>();
		for(Session s: sessionMap.values()) {
			int remoteId = s.getRemoteId();
			if(remoteId>=0) {
				remoteIdList.add(remoteId);
			}
		}
		return toArray(remoteIdList, Integer.class);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T[] toArray(List<T> lst, Class<T> type) {
		T[] result = (T[])Array.newInstance(type, lst.size());
		int i = 0;
		for(T s:lst) {
			result[i] = s;
			i++;
		}
		return result;
	}
	
}
