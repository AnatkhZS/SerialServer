package bl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.*;

import org.json.JSONObject;
import org.json.JSONArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import bl.session.Session;
import bl.session.SessionManager;

@SuppressWarnings("restriction")
public class SerialServer {
	private final int CMD_SERVER_PORT = 9428;
	private final int DATA_SERVER_PORT = 9429;
	private final String REQUEST_SPLITER = "@@";
//	private final String REQUEST_SESSIONID_LIST = "getSessionIdList";
	private final String ESTABLISH_CONNECTION = "establishConnection";
	private final String ESTABLISHED = "established";
	private final static String SERIALCONFIG = "serialConfig.json";
//	private DatagramSocket cmdServer;
	private DatagramSocket dataServer;
	private HttpServer cmdServer;
	private SessionManager sessionManager = SessionManager.getSessionManager();
	
	public void createServer() {
//		new Thread(new CreateCMDServer()).start();
		new Thread(new CreateDataServer()).start();
		HttpServer cmdServer = null;
		try {
			cmdServer = HttpServer.create(new InetSocketAddress(CMD_SERVER_PORT), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		cmdServer.createContext("/ipc/WriteCMD", new WriteCMDHandler());
		cmdServer.createContext("/ipc/GetSerialList", new GetSerialListHandler());
		cmdServer.createContext("/ipc/resetDevice", new ResetDeviceHandler());
		cmdServer.start();
	}
	
	class WriteCMDHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
        	String queryString =  exchange.getRequestURI().getQuery();
        	Map<String, String> queryDict = formData2Dic(queryString);
    		String serialPort = queryDict.get("serialPort");
    		String content = queryDict.get("content");
    		Session session = sessionManager.getSession(Integer.valueOf(serialPort));
			if(session != null) {
				for(char c:content.toCharArray())
					session.write(c);
				session.write('\n');
			}
			exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            JSONObject response = formatResponse(true, 1, "", null);
            os.write(response.toString().getBytes());
            os.close();
        }
    }
	
	class GetSerialListHandler implements HttpHandler{

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			Set<Integer> sessionList = sessionManager.getSessionList();
			JSONArray datas = new JSONArray();
			
			for(int sessionId:sessionList) {
				JSONObject data = new JSONObject();
				String sessionName = sessionManager.getSession(sessionId).getName();
				data.put("sessionId", sessionId);
				data.put("sessionName", sessionName);
				datas.put(data);
			}
			JSONObject response = formatResponse(true, 1, "", datas);
			exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
		}
	}
	
	class ResetDeviceHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
        	String queryString =  exchange.getRequestURI().getQuery();
        	Map<String, String> queryDict = formData2Dic(queryString);
    		String pid = queryDict.get("pid");
    		try {
    			JSONObject sessionInfo = getSessionInfoByPid(pid);
    			String sessionName = (String) sessionInfo.get("serialPort");
    			int sessionId = sessionManager.getSessionId(sessionName);
        		String username = (String) sessionInfo.get("username");
        		String password = (String) sessionInfo.get("password");
        		JSONArray resetCmds = sessionInfo.getJSONArray("resetCmds");
        		Session session = sessionManager.getSession(sessionId);
    			if(session != null) {
    				session.write('\n');
    				for(char c:username.toCharArray())
    					session.write(c);
    				session.write('\n');
    				Thread.sleep(1000);
    				for(char c:password.toCharArray())
    					session.write(c);
    				session.write('\n');
    				Thread.sleep(1000);
    				session.write('\n');
    				Thread.sleep(1000);
    				for(int i=0;i<resetCmds.length();i++) {
    					String cmd = resetCmds.getString(i);
    					for(char c:cmd.toCharArray())
    						session.write(c);
    					session.write('\n');
    					Thread.sleep(1000);
    				}
    			}
    			exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                JSONObject response = formatResponse(true, 1, "", null);
                os.write(response.toString().getBytes());
                os.close();
    		}catch(Exception e) {
    			e.printStackTrace();
    			exchange.sendResponseHeaders(200, 0);
                OutputStream os = exchange.getResponseBody();
                JSONObject response = formatResponse(false, -1, "Reset device fail", null);
                os.write(response.toString().getBytes());
                os.close();
    		}
        }
    }
	
	public static JSONObject getSessionInfoByPid(String pid) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(SERIALCONFIG));
		String data = "";
		String str = null;
		while((str=br.readLine())!=null)
			data = data+str+"\n";
		br.close();
		JSONObject root = new JSONObject(data);
		return root.getJSONObject(pid);
	}
	
	public static JSONObject formatResponse(boolean success, int errorCode, String errorMsg, JSONArray data) {
		JSONObject response = new JSONObject();
		response.put("success", success);
		response.put("errorCode", errorCode);
		response.put("errorMsg", errorMsg);
		response.put("datas", data);
		return response;
	}
	
	
	
	public static Map<String,String> formData2Dic(String formData ) {
        Map<String,String> result = new HashMap<>();
        if(formData== null || formData.trim().length() == 0) {
            return result;
        }
        final String[] items = formData.split("&");
        for(String item:items) {
        	char[] charArray = item.toCharArray();
        	for(int i=0;i<charArray.length;i++) {
        		if(charArray[i]=='=') {
        			try {
        				String key = URLDecoder.decode(new String(Arrays.copyOfRange(charArray, 0, i)),"utf8");
        				String val = URLDecoder.decode(new String(Arrays.copyOfRange(charArray, i+1, charArray.length)),"utf8");
        				result.put(key,val);
        				break;
        			}catch (UnsupportedEncodingException e) {}
        		}
        	}
        }
        return result;
//        Arrays.stream(items).forEach(item ->{
//            final String[] keyAndVal = item.split("=");
//            if( keyAndVal.length == 2) {
//                try{
//                    final String key = URLDecoder.decode( keyAndVal[0],"utf8");
//                    final String val = URLDecoder.decode( keyAndVal[1],"utf8");
//                    result.put(key,val);
//                }catch (UnsupportedEncodingException e) {}
//            }
//        });
//        return result;
    }
	
	public void destroyServer() {
//		cmdServer.close();
		cmdServer.stop(1);
		dataServer.close();
	}

//	private class CreateCMDServer implements Runnable{
//		@Override
//		public void run() {
//			try {
//				cmdServer = new DatagramSocket(CMD_SERVER_PORT);
//				while(true) {
//					byte[] container = new byte[1024];
//					DatagramPacket packet = new DatagramPacket(container, container.length);
//					int port;
//					InetAddress address;
//					try {
//						cmdServer.receive(packet);
//						port = packet.getPort();
//						address = packet.getAddress();
//					}catch(java.net.SocketException e) {
//						break;
//					}
//					byte[] data = packet.getData();
//					int len = packet.getLength();
//					String request = new String(data, 0, len);
//					if(request.equals(REQUEST_SESSIONID_LIST)) {
//						Set<Integer> sessionIdSet = sessionManager.getSessionList();
//						String retStr = sessionIdSet.toString();
//						DatagramPacket sendPacket = new DatagramPacket(retStr.getBytes(), retStr.getBytes().length, address, port);
//						cmdServer.send(sendPacket);
//					}else {
//						//request format: "sessionid@cmd"
//						int sessionId = Integer.valueOf(request.split(REQUEST_SPLITER)[0]);
//						String cmd = request.split(REQUEST_SPLITER)[1];
//						Session session = sessionManager.getSession(sessionId);
//						if(session != null) {
//							for(char c:cmd.toCharArray())
//								session.write(c);
//						}
//					}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	private class CreateDataServer implements Runnable{
		private List<HashMap<String, Object>> clientEndpointList;
		private List<Integer> querySessionList;
		
		public CreateDataServer() {
			clientEndpointList = new ArrayList<HashMap<String, Object>>();
			querySessionList = new ArrayList<Integer>();
			try {
				dataServer = new DatagramSocket(DATA_SERVER_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			new Thread(new ConnectionListener()).start();
			HashMap<Integer, String> msgMap = new HashMap<Integer, String>();
			while(true) {
				for(Integer sessionId:querySessionList) {
					Session session = sessionManager.getSession(sessionId);
					msgMap.put(sessionId, session.readLine(2));
				}
				for(HashMap<String, Object> endpoint:clientEndpointList) {
					int sessionId = (int)endpoint.get("sessionId");
					InetAddress address = (InetAddress)endpoint.get("address");
					int port = (int)endpoint.get("port");
					String msg = msgMap.get(sessionId);
					DatagramPacket sendPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, address, port);
					try {
						dataServer.send(sendPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		private class ConnectionListener implements Runnable{
			public void run() {
				try {
					int port;
					InetAddress address;
					while(true) {
						byte[] container = new byte[1024];
						DatagramPacket packet = new DatagramPacket(container, container.length);
						try {
							dataServer.receive(packet);
							port = packet.getPort();
							address = packet.getAddress();
						}catch(java.net.SocketException e) {
							break;
						}
						byte[] data = packet.getData();
						int len = packet.getLength();
						String request = new String(data, 0, len);
						//request format: "sessionid@@cmd"
						int sessionId = Integer.valueOf(request.split(REQUEST_SPLITER)[0]);
						String cmd = request.split(REQUEST_SPLITER)[1];
						if(cmd.equals(ESTABLISH_CONNECTION)) {
							HashMap<String, Object> endpoint = new HashMap<String, Object>();
							endpoint.put("addr", address);
							endpoint.put("port", port);
							endpoint.put("session", sessionId);
							clientEndpointList.add(endpoint);
							querySessionList.add(sessionId);
							DatagramPacket sendPacket = new DatagramPacket(ESTABLISHED.getBytes(), ESTABLISHED.getBytes().length, address, port);
							dataServer.send(sendPacket);
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
