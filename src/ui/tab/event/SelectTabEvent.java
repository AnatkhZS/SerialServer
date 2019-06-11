package ui;

import java.util.EventObject;

public class SelectTabEvent extends EventObject{
	private String tabName;
	private int sessionId;

	public SelectTabEvent(Object source, int sessionId, String tabName) {
		super(source);
		this.sessionId = sessionId;
		this.tabName = tabName;
	}
	
	public int getSessionId() {
		return this.sessionId;
	}
	
	public String getTabName() {
		return this.tabName;
	}

}
