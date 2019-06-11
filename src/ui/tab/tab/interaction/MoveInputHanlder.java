/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package ui.tab.tab.interaction;

import java.awt.Point;
import java.awt.event.MouseEvent;

import ui.tab.tab.TabView;
import ui.tab.tab.TabbedPane;

public class MoveInputHanlder extends AbstractInputHanlder {

	public MoveInputHanlder(TabbedPane pane) {
		super(pane);
	}

	private Point startPoint;
	private double pressOffset = 0;

	public void mousePressed(MouseEvent e) {
		startPoint = e.getPoint();
		try {
			TabView selectedTabView = pane.getTabView(pane.getSelectedTab());
			pressOffset = e.getX() - selectedTabView.getContentBound().getX();
		}catch(java.lang.NullPointerException ne) {
			return;
		}
			
	}

	public void mouseDragged(MouseEvent e) {
		if (startPoint == null || pane.getTabCount() <= 1) {
			return;
		}
		pane.setDragOffset(e.getX() - startPoint.x);
		int offset = e.getX() - startPoint.x;
		int index = pane.getTabIndex(pane.getSelectedTab());
		if (offset > 0) {
			if (index == pane.getTabCount() - 1) {
				return;
			} else {
				TabView tabView = pane.getTabView(index + 1);
				TabView selectedTabView = pane.getTabView(pane.getSelectedTab());
				if ((selectedTabView.getContentBound().getX() + selectedTabView.getContentBound().getWidth() + offset) > tabView.getContentBound().getCenterX()) {
					pane.moveTab(pane.getSelectedTab(), index + 1);
					selectedTabView = pane.getTabView(pane.getSelectedTab());
					startPoint = new Point((int) (selectedTabView.getContentBound().getX() + pressOffset), startPoint.y);
					pane.setDragOffset(e.getX() - startPoint.x);
				}
			}
		} else {
			if (index == 0) {
				return;
			} else {
				TabView tabView = pane.getTabView(index - 1);
				TabView selectedTabView = pane.getTabView(pane.getSelectedTab());
				if ((selectedTabView.getContentBound().getX() + offset) < (tabView.getContentBound().getCenterX())) {
					pane.moveTab(pane.getSelectedTab(), index - 1);
					selectedTabView = pane.getTabView(pane.getSelectedTab());
					startPoint = new Point((int) (selectedTabView.getContentBound().getX() + pressOffset), startPoint.y);
					pane.setDragOffset(e.getX() - startPoint.x);
				}

			}
		}

	}

	public void mouseReleased(MouseEvent e) {
		startPoint = null;
		pressOffset = 0;
		pane.setDragOffset(0);
	}

}