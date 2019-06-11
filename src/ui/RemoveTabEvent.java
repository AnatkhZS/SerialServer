package ui;

import java.util.EventObject;

public class RemoveTabEvent extends EventObject{
	private String tabName;
	private int sessionId;

	public RemoveTabEvent(Object source, int id, String tabName) {
		super(source);
		this.sessionId = id;
		this.tabName = tabName;
	}
	
	public int getSessionId() {
		return this.sessionId;
	}
	
	public String getTabName() {
		return this.tabName;
	}

}
