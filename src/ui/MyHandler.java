package ui;

import java.util.EventListener;

public abstract class MyHandler implements EventListener{
	public abstract void doHandler(RemoveTabEvent e);
}
