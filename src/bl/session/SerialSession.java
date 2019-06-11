package bl.session;

import data.SerialDataHandler;

public class SerialSession extends Session{
	private String serialPort;
	private int buadrate;
	
	public SerialSession(int sessionId, String serialPort, int buadrate, String logPath, boolean isRecord, boolean isStartAtMidnight, boolean isAppendToFile, SerialDataHandler handler) {
		super(sessionId, serialPort, logPath, isRecord, isStartAtMidnight, isAppendToFile, handler);
		this.serialPort = serialPort;
		this.buadrate = buadrate;
	}

	public String getSerialPort() {
		return this.serialPort;
	}
	
	public int getBuadrate() {
		return this.buadrate;
	}
	
	
	public void setSerialPort(String serialPort) {
		this.serialPort = serialPort;
	}
	
	public void setBuadrate(int buadrate) {
		this.buadrate = buadrate;
	}
	
}
