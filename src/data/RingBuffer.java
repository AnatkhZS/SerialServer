package data;

public class RingBuffer<T> {
	private int bufferSize;
	private T[] buffer;
	private int head = 0;
	private int tail = 0;
	
	public RingBuffer(int bufferSize){
		this.bufferSize = bufferSize;
		this.buffer = (T[]) new Object[bufferSize];
	}
	
	public boolean isFull(){
		return head == (tail+1)% bufferSize;
	}
	
	public boolean isEmpty(){
		return head == tail;
	}
	
	public void put(T t){
		buffer[tail] = t;
		if(isFull()) head = (head+1) % bufferSize;
		tail = (tail+1) % bufferSize;
	}
	
	public T get(){
		if(isEmpty()) return null;
		T result = buffer[head];
		head = (head+1) % bufferSize;
		return result;
	}
	
}
