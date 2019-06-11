/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package ui.tab.tab;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

public class ChromeTabbedPane extends TabbedPane {
	private int overGap = 20;

	protected void calculateTab() {
		int size = this.tabList.size();
		if (size == 0) {
			return;
		}
		double tabWidth = calculateTabWidth();
		JLabel closeLabel = new JLabel(closeIcon);
		Dimension closeSize = closeLabel.getPreferredSize();
		double w = tabWidth;
		double h = this.tabHeight;
		double x = 0;
		for (int i = 0; i < size; i++) {
			Tab tab = (Tab) this.tabList.get(i);
			if (this.useActualWidth) {
				w = getTabWidth(tab) + this.overGap * 2;
			}
			double y = this.topGap;
			Rectangle2D.Double bounds = new Rectangle2D.Double(x, y, w, h);
			Rectangle2D.Double contentBounds = new Rectangle2D.Double(x + overGap, y, w - 2 * overGap, h);
			x += w - overGap;

			Shape shape = createTabShape(bounds);
			Rectangle2D.Double closeIconBound = null;

			if (tab.isCloseAble()) {
				if (closeSize.getHeight() < this.tabHeight) {
					y = contentBounds.getCenterY() - closeSize.getHeight() / 2.0;
				}
				closeIconBound = new Rectangle2D.Double(contentBounds.x + contentBounds.width - closeSize.width, y, closeSize.width, closeSize.getHeight());
				if (closeSize.getWidth() > contentBounds.getWidth()) {
					closeIconBound = null;
				}
			}
			TabView view = new TabView(tab, bounds, shape, contentBounds, closeIconBound);
			tabViewMap.put(tab, view);
		}
	}

	protected double calculateTabWidth() {
		int size = tabList.size();
		double width = this.getWidth();
		double tabW = tabWidth;
		if (((size - 1) * (tabW - overGap) + tabW) > width) {
			tabW = (width - overGap + size * overGap) / size;
		}
		if (tabW < overGap * 2) {
			tabW = overGap * 2;
		}
		return tabW;
	}

	public int getOverGap() {
		return overGap;
	}

	public void setOverGap(int overGap) {
		this.overGap = overGap;
		this.invalidateTab();
	}

	protected Shape createTabShape(Rectangle2D rect) {
		GeneralPath path = new GeneralPath();
		double gap = overGap / 3.0;
		path.moveTo(rect.getX(), rect.getY() + rect.getHeight());
		path.curveTo(rect.getX() + gap, rect.getY() + rect.getHeight(), rect.getX() + gap * 2, rect.getY(), rect.getX() + overGap, rect.getY());
		path.lineTo(rect.getX() + rect.getWidth() - overGap, rect.getY());
		path.curveTo(rect.getX() + rect.getWidth() - overGap + gap, rect.getY(), rect.getX() + rect.getWidth() - overGap + gap * 2, rect.getY() + rect.getHeight(), rect.getX()
				+ rect.getWidth(), rect.getY() + rect.getHeight());
		path.closePath();
		return path;
	}

}