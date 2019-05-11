package data;

import bl.LogRecorder;

public class Driver {
	public static void main(String args[]) {
		SerialHandler sh = new SerialHandler("cu.SLAB_USBtoUART", 115200);
		LogRecorder lr = new LogRecorder(sh, "/Users/zhusong/Documents/code/log.txt", true, true);
		lr.startRecord();
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sh.setStop();
	}
}
