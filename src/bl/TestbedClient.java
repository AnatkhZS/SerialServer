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
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import bl.session.Session;
import bl.session.SessionManager;

public class TestbedClient {
	private String host;
	private int port;
	private boolean isConnected;
	private Selector selector;
	private SocketChannel clientSocketChannel;
	private SelectionKey selectionKey;
	private SessionManager sessionManager = SessionManager.getSessionManager();
	
	public TestbedClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.isConnected = false;
	}
	
	public boolean isConnected() {
		return this.isConnected;
	}
	
	private void setConnected(boolean status) {
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
			        					setConnected(true);
			        					new Thread(new HeartBeat()).start();
			        					register();
			        				}
		        				}catch(java.net.ConnectException e) {
		        					setConnected(false);
		                        	clientSocketChannel.close();
		                        	break;
		        				}
		        			}
		        		}
		        		if(selectionKey.isReadable()) {
//		        			System.out.println("Readable");
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
	                        	System.out.println("Header string: "+headerStr);
	                        	JSONObject headerDict = new JSONObject(headerStr);
	                        	int bodyLength = headerDict.getInt("content-length");
	                        	
	                        	byte[] body = slice(requestBytes, headerLength+2, bodyLength);
	                        	String bodyStr = new String(body, StandardCharsets.UTF_8);
	                        	JSONObject bodyDict = new JSONObject(bodyStr);
	                        	
	                        	if(headerDict.getString("action").equals("cmd")) {
	                        		String serialPort = bodyDict.getString("serialPort");
	                        		String cmd = bodyDict.getString("cmd");
	                        		System.out.println("Receive cmd: serialPort: "+serialPort+", cmd: "+cmd);
	                        		int sessionId = sessionManager.getSessionId(serialPort);
	                        		Session session = sessionManager.getSession(sessionId);
	                        		session.writeStr(cmd+"\n");
//	                        		for(char c: (cmd+"\n").toCharArray()) {
//	                        			session.write(c);
//	                        		}
	                        	}
	                        }else {
	                        	setConnected(false);
	                        	clientSocketChannel.close();
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
	
	public void disconnect() {
		synchronized(clientSocketChannel) {
			setConnected(false);
	    	try {
				clientSocketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	public void sendRequest(byte[] body, String action) {
		int bodyLength = body.length;
		JSONObject header = new JSONObject();
		header.put("content-length", bodyLength);
		header.put("content-type", "json");
		header.put("action", action);
		System.out.println("Header: "+header.toString());
		byte[] headerBytes = header.toString().getBytes();
		int headerLength = headerBytes.length;
		byte[] preHeader = new byte[2];
		preHeader[0] = (byte) (headerLength >> 8 & 0xFF);
		preHeader[1] = (byte) (headerLength & 0xFF);
		if(isConnected()) {
			write(mergeRequest(preHeader, headerBytes, body));
			System.out.println("Put "+action+" to request queue");
		}
	}
	
	public void register() {
		JSONObject request = new JSONObject();
		request.put("pcName", getPCName());
		request.put("serialPortList", sessionManager.getSessionNameList());
		sendRequest(request.toString().getBytes(), "register");
	}
	
	public void update() {
		JSONObject request = new JSONObject();
		request.put("pcName", getPCName());
		request.put("serialPortList", sessionManager.getSessionNameList());
		sendRequest(request.toString().getBytes(), "update");
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
		sendRequest(request.toString().getBytes(), "keepAlive");
	}
	
	private class HeartBeat implements Runnable{

		@Override
		public void run() {
			while(isConnected()) {
				try {
					Thread.sleep(5*1000);
					keepAlive();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		TestbedClient client = new TestbedClient("127.0.0.1", 12345);
		client.connect();
	}
}
