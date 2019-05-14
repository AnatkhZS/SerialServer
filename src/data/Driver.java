package data;

import bl.LogRecorder;

public class Driver {
	public static void main(String args[]) {
		SerialHandler sh = new SerialHandler("/dev/cu.SLAB_USBtoUART", 57600);
		LogRecorder lr = new LogRecorder(sh, "/Users/zhusong/Documents/code/log.txt", true, true);
		lr.startRecord();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sh.setStop();
	}
}
