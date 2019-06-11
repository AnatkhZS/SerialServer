package bl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import data.SerialDataHandler;

public class Test {
	public static void main(String args[]) throws InterruptedException {
		Properties props = System.getProperties();
		String osName = props.getProperty("os.name");
		System.out.println(osName);
	}
	
	private int index(Object[] buadrateList, Object value) {
		for(int i=0;i<buadrateList.length;i++) {
			if(buadrateList[i]==value) {
				return i;
			}
		}
		return -1;
	}
}
