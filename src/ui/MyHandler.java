package ui;

import java.util.EventListener;

public abstract class MyHandler implements EventListener{
	public abstract void removeTab(RemoveTabEvent e);
	public abstract void selectTab(SelectTabEvent e);
}
