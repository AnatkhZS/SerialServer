package data;
import com.fazecast.jSerialComm.*;

public class SerialOpener {
	private int buadrate;
	private String serialName;
	private SerialPort ser;
	
	public SerialOpener(String serialName, int buadrate) throws SerialException {
		this.buadrate = buadrate;
		this.serialName = serialName;
		this.ser = SerialPort.getCommPort(serialName);
		if(ser.isOpen()) {
			System.out.println("Serial opened");
			throw new SerialException();
		}
		this.ser.openPort();
		this.ser.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);
		this.ser.setBaudRate(buadrate);
	}
	
	public int getBuadrate() {
		return this.buadrate;
	}
	
	public String getSerialName() {
		return this.serialName;
	}
	
	public void resetSerial(String serialName, int buadrate) {
		close();
		this.buadrate = buadrate;
		this.serialName = serialName;
		this.ser = SerialPort.getCommPort(serialName);
		this.ser.openPort();
		this.ser.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);
		this.ser.setBaudRate(buadrate);
	}
	
	public void write(String cmd) {
		byte[] cmdArray = cmd.getBytes();
		ser.writeBytes(cmdArray, cmdArray.length);
	}
	
	public byte[] read() {
		byte[] readBuffer = new byte[1024];
		int numRead = ser.readBytes(readBuffer, readBuffer.length);
		if(numRead>0)
			return java.util.Arrays.copyOf(readBuffer, numRead);
		else
			return null;
	}
	
	public void close() {
		ser.closePort();
	}
	
}
