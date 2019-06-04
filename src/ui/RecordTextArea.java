package ui;

import java.util.*;

import javax.swing.JTextArea;

public class RecordTextArea extends JTextArea{
	private MyStack<String> stack;
	
	public RecordTextArea() {
		stack = new MyStack<String>();
	}
	
	public void push(String content) {
		stack.push(content);
	}
	
	public String backward() {
		String result = stack.backward();
		if(result == null)
			return "";
		return result;
	}
	
	public String forward() {
		String result = stack.forward();
		if(result == null)
			return "";
		return result;
	}
}

class MyStack<E>{
	private List<E> contentList;
	private int top = -1;
	
	public MyStack() {
		contentList = new ArrayList<E>();
	}
	
	public void push(E e) {
		contentList.add(e);
		top = contentList.size()-1;
	}
	
	public E backward() {
		if(top>=0) {
			E result = contentList.get(top);
			top--;
			return result;
		}else{
			return null;
		}
	}
	
	public E forward() {
		int buffPoint = top+1;
		if(buffPoint>contentList.size()-1)
			return null;
		else {
			E result = contentList.get(buffPoint);
			top = buffPoint==contentList.size()-1 ? contentList.size()-1 : top+1;
			return result;
		}
	}
}
