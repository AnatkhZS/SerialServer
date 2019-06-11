package data;

public interface DataHandler {
	public String getSerialName();
	public boolean isStop();
	public void setStop();
	public void reconnect();
	public void write(char c);
	public String readLine(int type);
}
