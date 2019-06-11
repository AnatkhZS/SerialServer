/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package ui.tab.utils;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventListener;

import javax.swing.event.EventListenerList;


public class ShortcutkeyUtils {
	static {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent ae) {
				if (ae instanceof KeyEvent) {
					KeyEvent e = (KeyEvent) ae;
					EventListener[] listeners = keyListeners.getListeners(KeyListener.class);
					int size = listeners.length;
					for (int i = 0; i < size; i++) {
						KeyListener l = (KeyListener) listeners[i];
						if (e.getID() == KeyEvent.KEY_TYPED) {
							l.keyTyped(e);
						} else if (e.getID() == KeyEvent.KEY_PRESSED) {
							l.keyPressed(e);
						} else if (e.getID() == KeyEvent.KEY_RELEASED) {
							l.keyReleased(e);
						}
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);
	}

	private static EventListenerList keyListeners = new EventListenerList();

	public static void addKeyListener(KeyListener l) {
		if (l == null) {
			return;
		}
		keyListeners.add(KeyListener.class, l);
	}

	public static void addKeyEvent(int type, Runnable run) {

	}

	public static void removeKeyListener(KeyListener l) {
		if (l == null) {
			return;
		}
		keyListeners.remove(KeyListener.class, l);
	}
}