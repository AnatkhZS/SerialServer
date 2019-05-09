/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package zht.tab;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

public class ZHTRectangleTabbedPane extends ZHTTabbedPane {

	private int tabGap = 3;

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
		for (int i = 0; i < size; i++) {
			Tab tab = (Tab) this.tabList.get(i);
			double x = i * tabWidth;
			double y = this.topGap;
			if (i > 0) {
				x += tabGap * i;
			}
			Rectangle2D.Double bounds = new Rectangle2D.Double(x, y, w, h);
			Rectangle2D.Double contentBounds = bounds;

			GeneralPath path = new GeneralPath();
			path.moveTo(x, y);
			path.lineTo(x + w - w / 10, y);
			path.lineTo(x + w, y + h / 3);
			path.lineTo(x + w, y + h);
			path.lineTo(x, y + h);
			path.closePath();
			Shape shape = path;
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
		if ((size - 1) * tabGap + size * tabW > width) {
			tabW = ((width) - (size - 1) * tabGap) / size;
		}
		if (tabW < 2) {
			tabW = 2;
		}
		return tabW;
	}

	protected Shape createTabShape(Rectangle2D bounds) {
		return null;
	}

}