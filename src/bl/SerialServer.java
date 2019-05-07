package bl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import data.SerialHandler;

public class SerialServer {
	private int serverPort = 9428;
	private SerialHandler sh;
	private boolean isStop = false;
	
	public SerialServer(SerialHandler sh) {
		this.sh = sh;
	}
	
	public void createServer() {
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
	
	public void setStop() {
		this.isStop = true;
	}
	
}
