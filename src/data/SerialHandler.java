package data;

import bl.SerialSession;

public class SerialHandler {
	private int readBufferSize = 1024*1024;
	private int writeBufferSize = 1024;
	private RingBuffer<byte[]> readBuffer;
	private RingBuffer<String> displayBuffer;
	private RingBuffer<String> logBuffer;
	private RingBuffer<String> serverBuffer;
	private RingBuffer<Character> writeBuffer;
	private SerialOpener serial;
	private String serialName;
	private int buadrate;
	private boolean isStop = false;
	
	public SerialHandler(String serialPort, int buadrate){
		this.readBuffer = new RingBuffer<byte[]>(readBufferSize);
		this.displayBuffer = new RingBuffer<String>(readBufferSize);
		this.logBuffer = new RingBuffer<String>(readBufferSize);
		this.serverBuffer = new RingBuffer<String>(readBufferSize);
		this.writeBuffer = new RingBuffer<Character>(writeBufferSize);
		this.serialName = serialPort;
		this.buadrate = buadrate;
		initial();
	}
	
	public String getSerialName() {
		return this.serialName;
	}
	
	public boolean isStop() {
		return isStop;
	}
	
	public void setStop() {
		this.isStop = true;
	}
	
	private void initial(){
		try {
			this.serial=new SerialOpener(serialName, buadrate);
		}catch(SerialException e) {
			String warning = "Unable to open serial port "+serialName+":\r\n" + "  Resource busy";
			displayBuffer.put(warning);
			logBuffer.put(warning);
			serverBuffer.put(warning);
			return;
		}
		
		this.isStop = false;
		Fetch fetch = new Fetch();
		Thread fetchThread = new Thread(fetch);
		Feed feed = new Feed();
		Thread feedThread = new Thread(feed);
		WriteCMD writeCmd = new WriteCMD();
		Thread writeCmdThread = new Thread(writeCmd);
		Thread destroyThread = new Thread(new Destroy());
		fetchThread.start();
		feedThread.start();
		writeCmdThread.start();
		destroyThread.start();
	}
	
	public void reconnect() {
		initial();
	}
	
	public String readLine(int type){
		switch(type){
		case 0:
			return displayBuffer.get();
		case 1:
			return logBuffer.get();
		case 2:
			return serverBuffer.get();
		default:
			return null;
		}
	}
	
	public void write(char c){
		this.writeBuffer.put(c);
	}
	
	//put data into readBuffer
	private class Fetch implements Runnable{

		@Override
		public void run() {
			while(!isStop) {
				byte[] result=serial.read();
				if(result != null) {
					readBuffer.put(result);
				}
			}
		}
		
	}
	
	//read data from readBuffer, and put them into three UP buffer
	private class Feed implements Runnable{

		@Override
		public void run() {
			while(!isStop) {
				byte[] content;
				if((content=readBuffer.get())!=null) {
					String line = rim(content);
					displayBuffer.put(line);
					logBuffer.put(line);
					serverBuffer.put(line);
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private String rim(byte[] content) {
			String result = new String(content);
			result.replaceAll("\r\r\n", "\r\n");
			return result;
		}
		
	}
	
	private class WriteCMD implements Runnable{

		@Override
		public void run() {
			char[] charList = new char[1024];
			int i=0;
			while(!isStop) {
				Character c;
				if((c=writeBuffer.get())!=null) {
					//handle \t etc..
					charList[i] = c;
					i++;
					if(c=='\n') {
						String cmd = String.valueOf(charList).split("\n")[0]+"\n";
						i = 0;
						charList = new char[1024];
						serial.write(cmd);
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class Destroy implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(!isStop) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			serial.close();
		}
	}
}
