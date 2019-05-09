/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package zht.tab.interaction;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import zht.tab.Tab;
import zht.tab.ZHTTabbedPane;

public class DefaultInputHanlder extends AbstractInputHanlder {
	public DefaultInputHanlder(ZHTTabbedPane pane) {
		super(pane);
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (pane.getMouseOverCloseTab() != null) {
				pane.closeTab(pane.getMouseOverCloseTab());
				pane.setMouseOverTabByPoint(e.getPoint());
			} else {
				Tab tab = pane.getTabByPoint(e.getPoint());
				pane.setSelectedTab(tab);
			}
		}

		if (SwingUtilities.isRightMouseButton(e)) {
			if (SwingUtilities.isRightMouseButton(e) && pane.isSelectedOnRightPressed()) {
				Tab tab = pane.getTabByPoint(e.getPoint());
				pane.setSelectedTab(tab);
			}
		}

	}

	public void mouseExited(MouseEvent e) {
		pane.setMouseOverTab(null);
		pane.setMouseOverCloseTab(null);
	}

	public void mouseEntered(MouseEvent e) {
		pane.setMouseOverTabByPoint(e.getPoint());
	}

	public void mouseMoved(MouseEvent e) {
		pane.setMouseOverTabByPoint(e.getPoint());
	}

}