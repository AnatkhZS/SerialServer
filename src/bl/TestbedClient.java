package bl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import bl.session.Session;
import bl.session.SessionManager;

public class TestbedClient {
	private String host;
	private int port;
	private boolean isConnected;
	private int requestId = 0;
	private long updateTime = 0;
	private Selector selector;
	private SocketChannel clientSocketChannel;
	private SelectionKey selectionKey;
	private SessionManager sessionManager = SessionManager.getSessionManager();
	private Map<Integer, Object> messageMap = new HashMap<Integer, Object>();
	private static TestbedClient testbedClient = new TestbedClient();
	private Thread heartbeatThread, updateRemoteIdThread, keepAliveDetectThread;
	private static String pcName = TestbedClient.getPCName();
	
//	public TestbedClient(String host, int port) {
//		this.host = host;
//		this.port = port;
//		this.isConnected = false;
//		updateTime();
//	}
	
	private TestbedClient() {
		updateTime();
	}
	
	public static TestbedClient getTestbecClient() {
		return testbedClient;
	}
	
	public void init(String host, int port) {
//		if(isConnected()) {
		disconnect("Init");
//		}
		this.host = host;
		this.port = port;
		this.isConnected = false;
		updateTime();
	}
	
	private void updateTime() {
		Calendar cal = Calendar.getInstance();
		updateTime = cal.getTimeInMillis();
	}
	
	private class KeepAliveDetectThread implements Runnable{
		@Override
		public void run() {
			long count = 0;
			while(true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				if(count++%100==0) {
					Calendar cal = Calendar.getInstance();
					long currentTime = cal.getTimeInMillis();
					synchronized(clientSocketChannel) {
						if(isConnected()) {
							if((currentTime-updateTime)>60*1000) {
								disconnectV2("Keep alive time out");
								break;
							}
						}else {
							break;
						}
					}
				}else {
					if(!isConnected())
						break;
				}
			}
		}
	}
	
	public boolean isConnected() {
		if(clientSocketChannel!=null)
			return clientSocketChannel.isOpen();
		return false;
	}
	
	private void setConnected(boolean status, String reason) {
		System.out.println("Set connection status: "+status+" for reason: "+reason);
		this.isConnected = status;
	}
	
	private byte[] slice(byte[] array, int offset, int length) {
		byte[] result = new byte[length];
		for(int i=offset;i<length+offset;i++) {
			result[i-offset] = array[i];
		}
		return result;
	}
	
	public void start() {
		new Thread(new Runnable() {
			public void run() {
				connect();
			}
		}).start();
	}
	
	public void connect() {
		try {
			clientSocketChannel = SocketChannel.open();
			clientSocketChannel.configureBlocking(false);
	        clientSocketChannel.connect(new InetSocketAddress(host, port));
	        selector = Selector.open();
	        
	        clientSocketChannel.register(selector, SelectionKey.OP_CONNECT);
	        while(true) {
        		selector.select();
	        	Set<SelectionKey> selectionKeys = selector.selectedKeys();
	        	SocketChannel client = null;
	        	
	        	Iterator<SelectionKey> it = selectionKeys.iterator();
	        	while(it.hasNext()){
		        	synchronized(clientSocketChannel) {
		        		selectionKey = it.next();
		        		it.remove();
		        		if(selectionKey.isConnectable()) {
		        			System.out.println("Connectable");
		        			if(clientSocketChannel.isConnectionPending()) {
		        				try {
		        					if(clientSocketChannel.finishConnect()) {
			        					selectionKey.interestOps(SelectionKey.OP_READ);
			        					setConnected(true, null);
			        					register();
			        					heartbeatThread = new Thread(new HeartBeat());
			        					heartbeatThread.start();
			        					keepAliveDetectThread = new Thread(new KeepAliveDetectThread());
			        					keepAliveDetectThread.start();
			        					updateRemoteIdThread = new Thread(sessionManager.new UpdateRemoteIdThread(-1)); //请求更新所有session的remote id
			        					updateRemoteIdThread.start();
			        				}
		        				}catch(java.net.ConnectException e) {
		        					disconnect("Connect fail");
		                        	break;
		        				}
		        			}
		        		}
		        		if(selectionKey.isReadable()) {
		        			client = (SocketChannel) selectionKey.channel();
		        			ByteBuffer buffer = ByteBuffer.allocate(4096);
	                        int count = 0;
	                        try {
	                            count = client.read(buffer);
	                        } catch (IOException e) {
	                            client.close();
	                        }
	                        if(count>0) {
	                        	buffer.flip();
	                        	byte[] requestBytes = buffer.array();
	                        	byte[] preHeader = slice(requestBytes, 0, 2);
	                        	int headerLength = (preHeader[1] & 0xFF) | (preHeader[0] & 0xFF) << 8;
	                        	byte[] header = slice(requestBytes, 2, headerLength);
	                        	String headerStr = new String(header, StandardCharsets.UTF_8);
	                        	JSONObject headerDict = new JSONObject(headerStr);
	                        	int bodyLength = headerDict.getInt("content-length");
	                        	
	                        	byte[] body = slice(requestBytes, headerLength+2, bodyLength);
	                        	String bodyStr = new String(body, StandardCharsets.UTF_8);
	                        	JSONObject bodyDict = new JSONObject(bodyStr);
	                        	System.out.println("Receive: "+headerStr+bodyStr);
	                        	String action = headerDict.getString("action");
	                        	if(action.equals("cmd")) {
	                        		int remoteId = bodyDict.getInt("remoteId");
	                        		String cmd = bodyDict.getString("cmd");
	                        		System.out.println("Receive cmd: remoteId: "+remoteId+", cmd: "+cmd);
	                        		int sessionId = sessionManager.getSessionId(remoteId);
	                        		Session session = sessionManager.getSession(sessionId);
	                        		session.writeStr(cmd+"\n");
//	                        		for(char c: (cmd+"\n").toCharArray()) {
//	                        			session.write(c);
//	                        		}
	                        	}else if(action.equals("requestRemoteId")) {
	                        		int requestId = headerDict.getInt("requestId");
	                        		JSONArray remoteIdJsonList = bodyDict.getJSONArray("remoteIdList");
	                        		List<Integer> remoteIdList = new ArrayList<Integer>();
	                        		for(int i=0;i<remoteIdJsonList.length();i++) {
	                        			remoteIdList.add(remoteIdJsonList.getInt(i));
	                        		}
	                        		synchronized(messageMap) {
	                        			messageMap.put(requestId, remoteIdList);
	                        		}
	                        	}else if(action.equals("keepAlive")) {
	                        		updateTime();
	                        	}
	                        }else {
	                        	disconnect("Recv data error");
	                        	break;
	                        }
		        		}
		        	}
	        	}
	        	if(!isConnected()) {
	        		break;
	        	}
	        }

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect(String reason) {
		setConnected(false, reason);
		try {
			if(clientSocketChannel!=null) {
				synchronized(clientSocketChannel) {
	    			clientSocketChannel.close();
	    		}
			}
			if(heartbeatThread!=null)
				heartbeatThread.join();
			if(updateRemoteIdThread!=null)
				updateRemoteIdThread.join();
			if(keepAliveDetectThread!=null)
				keepAliveDetectThread.join();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnectV2(String reason) {
		setConnected(false, reason);
    	try {
    		synchronized(clientSocketChannel) {
    			clientSocketChannel.close();
    		}
			if(heartbeatThread!=null)
				heartbeatThread.join();
			if(updateRemoteIdThread!=null)
				updateRemoteIdThread.join();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void write(byte[] content) {
		synchronized(clientSocketChannel) {
			ByteBuffer writeBuffer = ByteBuffer.wrap(content);
			try {
				clientSocketChannel.write(writeBuffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private byte[] mergeRequest(byte[] preHeader, byte[] header, byte[] body) {
		int totalLength = preHeader.length+header.length+body.length;
		byte[] request = new byte[totalLength];
		System.arraycopy(preHeader, 0, request, 0, preHeader.length);
		System.arraycopy(header, 0, request, preHeader.length, header.length);
		System.arraycopy(body, 0, request, preHeader.length+header.length, body.length);
		return request;
	}
	
	private int getRequestId() {
		return ++requestId;
	}
	
	private Object getResponse(int reuqestId) {
		int count = 0;
		while(count<50) {
			synchronized(messageMap) {
				if(messageMap.containsKey(requestId)) {
					return messageMap.get(requestId);
				}
			}
			count++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void sendRequest(byte[] body, String action, int requestId) {
		int bodyLength = body.length;
		JSONObject header = new JSONObject();
		header.put("content-length", bodyLength);
		header.put("content-type", "json");
		header.put("action", action);
		header.put("requestId", requestId);
		System.out.println("Request: "+header.toString()+new String(body));
		byte[] headerBytes = header.toString().getBytes();
		int headerLength = headerBytes.length;
		byte[] preHeader = new byte[2];
		preHeader[0] = (byte) (headerLength >> 8 & 0xFF);
		preHeader[1] = (byte) (headerLength & 0xFF);
		if(isConnected()) {
			write(mergeRequest(preHeader, headerBytes, body));
		}
	}
	
	public void register() {
		JSONObject request = new JSONObject();
		request.put("pcName", pcName);
		sendRequest(request.toString().getBytes(), "register", getRequestId());
	}
	
	public void update() {
		JSONObject request = new JSONObject();
		request.put("pcName", pcName);
		sendRequest(request.toString().getBytes(), "update", getRequestId());
	}
	
	public List<Integer> requestRemoteId(int count, int[] historyRemoteIdList) {
		JSONObject request = new JSONObject();
		request.put("count", count);
		request.put("historyRemoteIdList", historyRemoteIdList);
		int requestId = getRequestId();
		sendRequest(request.toString().getBytes(), "requestRemoteId", requestId);
		Object response = getResponse(requestId);
		if(response!=null) {
			@SuppressWarnings("unchecked")
			List<Integer> remoteIdList = (List<Integer>)response;
			if(remoteIdList.size()==count) {
				return remoteIdList;
			}else {
				// to deal with
			}
		}
		return null;
	}
	
	public List<Integer> requestRemoteId(int count, Integer[] historyRemoteIdList) {
		int[] idList = new int[historyRemoteIdList.length];
		for(int i=0;i<historyRemoteIdList.length;i++) {
			idList[i] = historyRemoteIdList[i];
		}
		return requestRemoteId(count, idList);
	}
	
	public static String getPCName() {
		InetAddress addr;
		String macString = "";
		String hostname = "";
        try {
			addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
			hostname = hostname.split("\\.")[0];
			
			NetworkInterface ni = NetworkInterface.getByInetAddress(addr);
			byte[] mac = ni.getHardwareAddress();
            Formatter formatter = new Formatter();  
            for (int i = 0; i < mac.length; i++) {  
            	macString = formatter.format(Locale.getDefault(), "%02X%s", mac[i],  
                        (i < mac.length - 1) ? "-" : "").toString();  
  
            }  
            formatter.close();
		} catch (UnknownHostException e) {
			hostname = "UnknownHost";
		} catch (SocketException e) {
			macString = "UnknownMac";
		}
		return hostname+"_"+macString;
	}
	
	public void keepAlive() {
		JSONObject request = new JSONObject();
		sendRequest(request.toString().getBytes(), "keepAlive", 0);
	}
	
	private class HeartBeat implements Runnable{

		@Override
		public void run() {
			long count = 0;
			while(isConnected()) {
				try {
					Thread.sleep(100);
					if(count++%100==0) {
						if(isConnected())
							keepAlive();
						else
							break;
					}else {
						if(!isConnected) 
							break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		TestbedClient client = TestbedClient.getTestbecClient();
		client.init("127.0.0.1", 12345);
		client.connect();
	}
}
