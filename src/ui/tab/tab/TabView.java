package ui.tab.tab;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class TabView {
	private Tab tab;
	private Rectangle2D bound;
	private Shape shape;
	private Rectangle2D contentBound;
	private Rectangle2D closeIconBound;

	public TabView(Tab tab, Rectangle2D bound, Shape shape, Rectangle2D contentBound, Rectangle2D closeIconBound) {
		super();
		this.tab = tab;
		this.bound = bound;
		this.shape = shape;
		this.contentBound = contentBound;
		this.closeIconBound = closeIconBound;
	}

	public Rectangle2D getBound() {
		return bound;
	}

	public Shape getShape() {
		return shape;
	}

	public Rectangle2D getContentBound() {
		return contentBound;
	}

	public Rectangle2D getCloseIconBound() {
		return closeIconBound;
	}

	public void setCloseIconBound(Rectangle2D iconBound) {
		this.closeIconBound = iconBound;
	}

	public Tab getTab() {
		return tab;
	}

}