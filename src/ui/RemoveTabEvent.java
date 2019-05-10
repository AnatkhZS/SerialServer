package ui;

import java.util.EventObject;

public class RemoveTabEvent extends EventObject{
	private String tabName;

	public RemoveTabEvent(Object source, String tabName) {
		super(source);
		this.tabName = tabName;
	}
	
	public String getTabName() {
		return this.tabName;
	}

}
