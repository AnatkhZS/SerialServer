package ui.tab.tab;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class ComponetBorder implements Border, MouseListener {
	private int offset = 5;

	private JComponent component;
	private JComponent container;
	private Border border;
	private Rectangle rect;

	public ComponetBorder(JComponent component, JComponent container, Border border) {
		this.component = component;
		this.container = container;
		this.border = border;
		container.addMouseListener(this);
	}

	public Insets getBorderInsets(Component c) {
		Dimension size = component.getPreferredSize();
		Insets insets = border.getBorderInsets(c);
		insets.top = Math.max(insets.top, size.height);
		return insets;
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Insets borderInsets = border.getBorderInsets(c);
		Insets insets = getBorderInsets(c);
		int temp = (insets.top - borderInsets.top) / 2;
		border.paintBorder(c, g, x, y + temp, width, height - temp);
		Dimension size = component.getPreferredSize();
		rect = new Rectangle(offset, 0, size.width, size.height);
		SwingUtilities.paintComponent(g, component, (Container) c, rect);
	}

	private void dispatchEvent(MouseEvent me) {
		if (rect != null && rect.contains(me.getX(), me.getY())) {
			Point pt = me.getPoint();
			pt.translate(-offset, 0);
			component.setBounds(rect);
			component.dispatchEvent(new MouseEvent(component, me.getID(), me.getWhen(), me.getModifiers(), pt.x, pt.y, me.getClickCount(), me.isPopupTrigger(), me.getButton()));
			if (!component.isValid())
				container.repaint();
		}
	}

	public void mouseClicked(MouseEvent e) {
		dispatchEvent(e);
	}

	public void mouseEntered(MouseEvent e) {
		dispatchEvent(e);
	}

	public void mouseExited(MouseEvent e) {
		dispatchEvent(e);
	}

	public void mousePressed(MouseEvent e) {
		dispatchEvent(e);
	}

	public void mouseReleased(MouseEvent e) {
		dispatchEvent(e);
	}
}