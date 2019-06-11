package bl.session;

import java.util.EventObject;

public class SessionEvent extends EventObject{
	private int type;
	
	public SessionEvent(Object source, int type) {
		super(source);
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}

}
