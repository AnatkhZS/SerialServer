package bl.session;

import java.util.EventObject;

public class SessionEvent extends EventObject{
	private int type;
	private int sessionId;
	
	public SessionEvent(Object source, int type, int sessionId) {
		super(source);
		this.type = type;
		this.sessionId = sessionId;
	}
	
	public int getType() {
		return this.type;
	}
	
	public int getSessionId() {
		return this.sessionId;
	}

}
