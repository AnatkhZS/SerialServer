package bl.session;

import bl.LogRecorder;
import data.DataHandler;

public class Session {
	private int sessionId;
	private String name;
	private String logPath;
	private boolean isRecord;
	private boolean isStartAtMidnight;
	private boolean isAppendToFile;
	private DataHandler handler;
	
	public Session(int sessionId, String name, String logPath, boolean isRecord, boolean isStartAtMidnight, boolean isAppendToFile, DataHandler handler) {
		this.sessionId = sessionId;
		this.name = name;
		this.logPath = logPath;
		this.isRecord = isRecord;
		this.isStartAtMidnight = isStartAtMidnight;
		this.isAppendToFile = isAppendToFile;
		this.handler = handler;
		this.record();
	}
	
	private void record() {
		if(this.isRecord()) {
			LogRecorder lr = new LogRecorder(this);
			lr.startRecord();
		}
	}
	
	public boolean isStop() {
		return handler.isStop();
	}
	
	public void setStop() {
		handler.setStop();
	}
	
	public void reconnect() {
		handler.reconnect();
	}
	
	public String readLine(int type){
		return handler.readLine(type);
	}
	
	public void write(char c){
		handler.write(c);
	}
	
	public synchronized void writeStr(String cmd) {
		for(char c: cmd.toCharArray())
			handler.write(c);
	}
	
	public int getSesionId() {
		return this.sessionId;
	}
	
	public String getName() {
		return this.name;
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
