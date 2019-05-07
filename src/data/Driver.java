package data;

public class Driver {
	private static RingBuffer<Integer> buffer;
	public static void main(String args[]){
//		new Driver().runThread();
	}
	
	public void runThread() {
		Feed feed=new Feed();
		Fetch fetch=new Fetch();
		Thread feedThread=new Thread(feed);
		Thread fetchThread=new Thread(fetch);
		fetchThread.start();
		feedThread.start();
	}
	
	private class Feed implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}
		
	}
	
	private class Fetch implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}
		
	}
}
