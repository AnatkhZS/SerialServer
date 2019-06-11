package bl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import data.SerialDataHandler;

public class Test {
	public static void main(String args[]) throws InterruptedException {
		Set<Integer> set = new HashSet<Integer>();
		set.add(1);
		set.add(1000);
		set.add(8888888);
		System.out.println(set.toString());
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
