package ui;

import java.util.EventObject;

public class SelectTabEvent extends EventObject{
	private String tabName;

	public SelectTabEvent(Object source, String tabName) {
		super(source);
		this.tabName = tabName;
	}
	
	public String getTabName() {
		return this.tabName;
	}

}
