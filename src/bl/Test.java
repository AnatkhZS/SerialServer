package bl;

import data.SerialHandler;

public class Test {
	public static void main(String args[]) throws InterruptedException {
		SerialHandler sh = new SerialHandler("cu.SLAB_USBtoUART", 115200);
		LogRecorder lr = new LogRecorder(sh, "/Users/zhusong/Documents/code/log.txt", true, true);
		lr.startRecord();
		SerialServer server = new SerialServer(sh);
		server.createServer();
		System.out.println("11111");
//		while(true) {
//			String line = sh.readLine(0);
//			if(line!=null)
//				System.out.println(line);
//			Thread.sleep(10);
//		}
	}
}
