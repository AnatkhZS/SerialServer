package bl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import data.SerialHandler;

public class LogRecorder {
	private SerialHandler serialHandler;
	private String fileName;
	private boolean startOnNewDate;
	private boolean overwriteFile;
	private boolean isStop = false;
	
	public LogRecorder(SerialHandler serialHandler, String fileName, boolean startOnNewDate, boolean overwriteFile) {
		this.serialHandler = serialHandler;
		this.fileName = fileName;
		this.startOnNewDate = startOnNewDate;
		this.overwriteFile = overwriteFile;
	}
	
	public File fileInit(String fileName) {
		File logFile = new File(fileName);
		if(overwriteFile && logFile.exists())
			logFile.delete();
		if(!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return logFile;
	}
	
	public void setStop() {
		this.isStop = true;
	}
	
	public void startRecord() {
		Recorde r = new Recorde();
		Thread recordeThread = new Thread(r);
		recordeThread.start();
	}
	
	private class Recorde implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			File logFile = fileInit(fileName);
			try {
				FileWriter writer = new FileWriter(logFile, true);
				while(!isStop) {
					String line = serialHandler.readLine(1);
					if(line!=null) {
						writer.write(line);
						writer.flush();
					}
					Thread.sleep(10);
				}
				writer.close();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
