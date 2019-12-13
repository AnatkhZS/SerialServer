package bl.session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import com.fazecast.jSerialComm.SerialPort;

import data.SerialDataHandler;

public class SessionManager {
	private static SessionManager manager = new SessionManager();
	private int currentSessionId = 0;
	private Map<Integer, Session> sessionMap;
	private Set<SessionEventHandler> handlerList;
	
	private SessionManager() {
		sessionMap = new HashMap<Integer, Session>();
		handlerList = new HashSet<SessionEventHandler>();
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
	
	public int createSession(String serialPort, int buadrate, String logPath, boolean isRecord, boolean isStartAtMidnight, boolean isAppendToFile) {
		SerialDataHandler handler = new SerialDataHandler(serialPort, buadrate);
		SerialSession ss = new SerialSession(currentSessionId, serialPort, buadrate, logPath, isRecord, isStartAtMidnight, isAppendToFile, handler);
		sessionMap.put(currentSessionId, ss);
		this.trigger(new SessionEvent(this, 0));
		return currentSessionId++;
	}
	
	public boolean destroySession(int id) {
		sessionMap.remove(id);
		this.trigger(new SessionEvent(this, 1));
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
        return toArray(result);
	}
	
	private static String[] toArray(List<String> lst) {
		String[] result = new String[lst.size()];
		int i = 0;
		for(String s:lst) {
			result[i] = s;
			i++;
		}
		return result;
	}
	
}
