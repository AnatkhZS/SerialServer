package data;

public interface DataHandler {
	public static int UI_READER = 0;
	public static int LOG_FILE_READER = 1;
	public static int NETWORK_READER = 2;
	public String getSerialName();
	public boolean isStop();
	public void setStop();
	public void reconnect();
	public void write(char c);
	public String readLine(int type);
}
