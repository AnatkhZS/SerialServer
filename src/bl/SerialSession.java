package bl;

import data.SerialHandler;

public class SerialSession {
	private String serialPort;
	private int buadrate;
	private String logPath;
	private boolean isRecord;
	private boolean isStartAtMidnight;
	private boolean isAppendToFile;
	private SerialHandler serialHandler;
	
	public SerialSession(String serialPort, int buadrate, String logPath, boolean isRecord, boolean isStartAtMidnight, boolean isAppendToFile) {
		this.serialPort = serialPort;
		this.buadrate = buadrate;
		this.logPath = logPath;
		this.isRecord = isRecord;
		this.isStartAtMidnight = isStartAtMidnight;
		this.isAppendToFile = isAppendToFile;
		serialHandler = new SerialHandler(serialPort, buadrate);
	}
	
	public boolean isStop() {
		return serialHandler.isStop();
	}
	
	public void setStop() {
		serialHandler.setStop();
	}
	
	public void reconnect() {
		serialHandler.reconnect();
	}
	
	public String readLine(int type){
		return serialHandler.readLine(type);
	}
	
	public void write(char c){
		serialHandler.write(c);
	}
	
	public String getSerialPort() {
		return this.serialPort;
	}
	
	public int getBuadrate() {
		return this.buadrate;
	}
	
	public String getLogPath() {
		return this.logPath;
	}
	
	public boolean isRecord() {
		return this.isRecord;
	}
	
	public boolean isStartAtMidnight() {
		return this.isStartAtMidnight;
	}
	
	public boolean isAppendToFile() {
		return this.isAppendToFile;
	}
	
	public void setSerialPort(String serialPort) {
		this.serialPort = serialPort;
	}
	
	public void setBuadrate(int buadrate) {
		this.buadrate = buadrate;
	}
	
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	
	public void setRecord(boolean isRecord) {
		this.isRecord = isRecord;
	}
	
	public void setStartAtMidnight(boolean isStartAtMidnight) {
		this.isStartAtMidnight = isStartAtMidnight;
	}
	
	public void setAppendToFile(boolean isAppendToFile) {
		this.isAppendToFile = isAppendToFile;
	}
}
