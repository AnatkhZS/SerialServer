package data;

public class SerialDataHandler implements DataHandler{
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
	private boolean isDisconnected = false;
	
	public SerialDataHandler(String serialPort, int buadrate){
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
		case UI_READER:
			return displayBuffer.get();
		case LOG_FILE_READER:
			return logBuffer.get();
		case NETWORK_READER:
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
			boolean isFirstRead = true;
			while(!isStop) {
				byte[] result;
				try {
					result = serial.read();
					if(isFirstRead) {  //首次读取数据可能会乱码
						isFirstRead = false;
						continue;
					}
					if(result != null) {
						readBuffer.put(result);
					}
				} catch (SerialPortDisconnectException e) {
					String disconnectNotation = "\n\nSerial Port Disconnect!";
					readBuffer.put(disconnectNotation.getBytes());
					isDisconnected = true;
					break;
				}
				
			}
		}
		
	}
	
	//read data from readBuffer, and put them into three UP buffer
	private class Feed implements Runnable{

		@Override
		public void run() {
			while(!isStop&&!isDisconnected) {
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
			while(!readBuffer.isEmpty()) {
				byte[] content;
				if((content=readBuffer.get())!=null) {
					String line = rim(content);
					displayBuffer.put(line);
					logBuffer.put(line);
					serverBuffer.put(line);
				}
			}
			while(!displayBuffer.isEmpty()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			isStop = true;
		}
		
		private String rim(byte[] content) {
			String result = new String(content);
			String spliter = "&()H&^%";
			result.replaceAll("\r\r\n", spliter);
			result.replaceAll("\r\n", spliter);
			result.replaceAll("\r", spliter);
			result.replaceAll("\n", spliter);
			result.replaceAll(spliter, "\r\n");
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
