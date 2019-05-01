package data;

import bl.Reader;

public class SerialHandler {
	private static volatile SerialHandler sh = new SerialHandler();
	private int readBufferSize = 1024*1024;
	private int writeBufferSize = 1024;
	private RingBuffer<String> displayBuffer;
	private RingBuffer<String> logBuffer;
	private RingBuffer<String> serverBuffer;
	private RingBuffer<Character> writeBuffer;
	
	private SerialHandler(){
		this.displayBuffer = new RingBuffer<String>(readBufferSize);
		this.logBuffer = new RingBuffer<String>(readBufferSize);
		this.serverBuffer = new RingBuffer<String>(readBufferSize);
		this.writeBuffer = new RingBuffer<Character>(writeBufferSize);
		//initial();
	}
	
	private void initial(){
		Fetch fetch = new Fetch();
		Thread fetchThread = new Thread(fetch);
		Feed feed = new Feed();
		Thread feedThread = new Thread(feed);
		fetchThread.start();
		feedThread.start();
	}
	
	public static SerialHandler getSerialHanler(){
		return sh;
	}
	
	public String readLine(Reader reader){
		switch(reader.getType()){
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
		this.writeBuffer.put(c);;
	}
	
	private class Fetch implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class Feed implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
