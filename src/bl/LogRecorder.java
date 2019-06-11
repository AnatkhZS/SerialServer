package bl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import bl.session.SerialSession;
import bl.session.Session;
import data.SerialDataHandler;

public class LogRecorder {
	private Session session;
	private String fileName;
	private boolean isStartAtMidnight;
	private boolean isAppendToFile;
	
	public LogRecorder(Session session) {
		this.session = session;
		this.fileName = session.getLogPath();
		this.isStartAtMidnight = session.isStartAtMidnight();
		this.isAppendToFile = session.isAppendToFile();
	}
	
	public File fileInit(String fileName) {
		File logFile = new File(fileName);
		if((!isAppendToFile) && logFile.exists())
			logFile.delete();
		if(!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return logFile;
	}
	
	public void startRecord() {
		Record r = new Record();
		Thread recordeThread = new Thread(r);
		recordeThread.start();
	}
	
	private String getDate() {
		Calendar cal = Calendar.getInstance();
		Date time = cal.getTime();
		SimpleDateFormat df = new SimpleDateFormat("MM-dd");
		String timeStr = df.format(time);
		return timeStr;
	}
	
	private class Record implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String date = getDate();
			String monthStr = date.split("-")[0];
			String dayStr = date.split("-")[1];
			File logFile = fileInit(fileName.replace("%M", monthStr).replace("%D", dayStr));
			try {
				FileWriter writer = new FileWriter(logFile, true);
				while(!session.isStop()) {
					if(isStartAtMidnight) {
						String currenDate;
						if((currenDate=getDate())!=date) {
							writer.close();
							monthStr = currenDate.split("-")[0];
							dayStr = currenDate.split("-")[1];
							logFile = fileInit(fileName.replace("%M", monthStr).replace("%D", dayStr));
							writer = new FileWriter(logFile, true);
						}
					}
					String line = session.readLine(1);
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
