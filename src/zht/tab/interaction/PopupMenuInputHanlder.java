/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package zht.tab.interaction;

import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import zht.tab.ZHTTabbedPane;

public class PopupMenuInputHanlder extends AbstractInputHanlder {

	public PopupMenuInputHanlder(ZHTTabbedPane pane) {
		super(pane);
	}

	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			//popumenu
			if (pane.getPopMenuGenerator() != null) {
				int size = pane.getTabList().size();
				if (size == 0) {
					return;
				}
				JPopupMenu menu = pane.getPopMenuGenerator().generate(pane, e);
				menu.show(pane, e.getX(), e.getY());
			}
		}
	}
}