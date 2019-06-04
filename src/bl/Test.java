package bl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import data.SerialHandler;

public class Test {
	public static void main(String args[]) throws InterruptedException {
		Integer[] intArray = {1,2,3};
		int a = 3;
		System.out.println(new Test().index(intArray, a));
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
