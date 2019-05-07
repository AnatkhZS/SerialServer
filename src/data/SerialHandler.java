package data;

public class SerialHandler {
	private int readBufferSize = 1024*1024;
	private int writeBufferSize = 1024;
	private RingBuffer<byte[]> readBuffer;
	private RingBuffer<String> displayBuffer;
	private RingBuffer<String> logBuffer;
	private RingBuffer<String> serverBuffer;
	private RingBuffer<Character> writeBuffer;
	private SerialOpener serial;
	
	public SerialHandler(String serialName, int buadrate){
		this.readBuffer = new RingBuffer<byte[]>(readBufferSize);
		this.displayBuffer = new RingBuffer<String>(readBufferSize);
		this.logBuffer = new RingBuffer<String>(readBufferSize);
		this.serverBuffer = new RingBuffer<String>(readBufferSize);
		this.writeBuffer = new RingBuffer<Character>(writeBufferSize);
		this.serial=new SerialOpener(serialName, buadrate);
		initial();
	}
	
	private void initial(){
		Fetch fetch = new Fetch();
		Thread fetchThread = new Thread(fetch);
		Feed feed = new Feed();
		Thread feedThread = new Thread(feed);
		WriteCMD writeCmd = new WriteCMD();
		Thread writeCmdThread = new Thread(writeCmd);
		fetchThread.start();
		feedThread.start();
		writeCmdThread.start();
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
			while(true) {
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
			while(true) {
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
			while(true) {
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
	
}
