package ui.tab.tab;

import java.awt.Component;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JLabel;

import ui.tab.event.MyHandler;
import ui.tab.event.RemoveTabEvent;

public class Tab {
	private Component component;
	private boolean closeAble = true;
	private Icon icon;
	private String title;
	private int id;
	
	private Set<MyHandler> eventListeners = new LinkedHashSet<MyHandler>();
	
	public void addListener(MyHandler h) {
		this.eventListeners.add(h);
	}
	
	public void removeListener(MyHandler h) {
		this.eventListeners.remove(h);
	}
	
	public void trigger(RemoveTabEvent e) {
		if(eventListeners == null) return;
		notifies(e);
	}
	
	protected void notifies(RemoveTabEvent e) {
		if(eventListeners.size()>0){
			for(MyHandler h:eventListeners)
				h.removeTab(e);
		}
	}

	public Tab(Component component, boolean closeAble, Icon icon, int id, String title) {
		super();
		this.component = component;
		this.closeAble = closeAble;
		this.icon = icon;
		this.id = id;
		this.title = title;
	}

	public JLabel getTabLabel() {
		JLabel label = new JLabel();
		if (this.title != null) {
			label.setText(this.title);
		}
		if (this.icon != null) {
			label.setIcon(icon);
		}
		return label;
	}

	public Component getComponent() {
		return component;
	}

	public boolean isCloseAble() {
		return closeAble;
	}

	public Icon getIcon() {
		return icon;
	}
	
	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void destroy() {
		
	}
}