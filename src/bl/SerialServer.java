package bl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Set;

import bl.session.Session;
import bl.session.SessionManager;

public class SerialServer {
	private final int SERVER_PORT = 9428;
	private final String REQUEST_SPLITER = "@@";
	private final String REQUEST_SESSIONID_LIST = "getSessionIdList";
	private DatagramSocket server;
	private SessionManager sessionManager = SessionManager.getSessionManager();
	
	public void createServer() {
		CreateServer cs = new CreateServer();
		Thread createServerThread = new Thread(cs);
		createServerThread.start();
	}
	
	public void destroyServer() {
		server.close();
	}

	private class CreateServer implements Runnable{

		@Override
		public void run() {
			try {
				server = new DatagramSocket(SERVER_PORT);
				while(true) {
					byte[] container = new byte[1024];
					DatagramPacket packet = new DatagramPacket(container, container.length);
					int port;
					InetAddress address;
					try {
						server.receive(packet);
						port = packet.getPort();
						address = packet.getAddress();
					}catch(java.net.SocketException e) {
						break;
					}
					byte[] data = packet.getData();
					int len = packet.getLength();
					String request = new String(data, 0, len);
					if(request.equals(REQUEST_SESSIONID_LIST)) {
						Set<Integer> sessionIdSet = sessionManager.getSessionList();
						String retStr = sessionIdSet.toString();
						DatagramPacket sendPacket = new DatagramPacket(retStr.getBytes(), retStr.getBytes().length, address, port);
						server.send(sendPacket);
					}else {
						//request format: "sessionid@cmd"
						int sessionId = Integer.valueOf(request.split(REQUEST_SPLITER)[0]);
						String cmd = request.split(REQUEST_SPLITER)[1];
						Session session = sessionManager.getSession(sessionId);
						if(session != null) {
							for(char c:cmd.toCharArray())
								session.write(c);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
