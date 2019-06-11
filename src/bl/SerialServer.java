package bl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import data.SerialDataHandler;

public class SerialServer {
	private int serverPort = 9428;
	private SerialDataHandler sh;
	private boolean isStop = false;
	
	public SerialServer(SerialDataHandler sh) {
		this.sh = sh;
	}
	
	public void createServer() {
		CreateServer cs = new CreateServer();
		Thread createServerThread = new Thread(cs);
		createServerThread.start();
	}
	
	public void setStop() {
		this.isStop = true;
	}
	
	private class CreateServer implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				DatagramSocket server = new DatagramSocket(serverPort);
				while(!isStop) {
					byte[] container = new byte[1024];
					DatagramPacket packet = new DatagramPacket(container, container.length);
					server.receive(packet);
					
					byte[] data = packet.getData();
					int len = packet.getLength();
					String request = new String(data, 0, len);
					for(char c:request.toCharArray())
						sh.write(c);
				}
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
