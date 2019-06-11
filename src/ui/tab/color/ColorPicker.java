/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package ui.tab.color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 * @author zhangtao
 *
 * Msn & Mail: zht_dream@hotmail.com
 */
public class ColorPicker extends JComponent {

	public static final String SELECTEDCOLORCHANGE = "selectedcolorchange";
	public static final String OVERCOLORCHANGE = "overcolorchange";
	public static final String MORECOLORSELECTION = "morecolorselection";

	private JButton noneButton = new ColorButton("None");
	private JButton otherButton = new ColorButton("more Colors");
	private ColorPanel colorPanel = new ColorPanel();
	private ColorTextField colorField = new ColorTextField();
	private Color selectedColor = null;

	public ColorPicker() {
		initGUI();
		installListener();
	}

	public Dimension getPreferredSize() {
		return new Dimension(200, 200);
	}

	public Insets getInsets() {
		return new Insets(1, 1, 1, 1);
	}

	private void installListener() {
		otherButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				firePropertyChange(MORECOLORSELECTION, true, false);
				Color selectedColor = JColorChooser.showDialog(ColorPicker.this, "Colors", null);
				setSelectedColor(selectedColor);

			}
		});

		noneButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				setSelectedColor(null);
			}
		});

		colorPanel.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName() == SELECTEDCOLORCHANGE) {
					Object newValue = evt.getNewValue();
					if (newValue == null) {
						setSelectedColor(null);
					} else {
						setSelectedColor((Color) newValue);
					}
				}
				if (evt.getPropertyName() == OVERCOLORCHANGE) {
					Object newValue = evt.getNewValue();
					if (newValue == null) {
						colorField.setColor(null);
					} else {
						colorField.setColor((Color) newValue);
					}
				}
			}
		});
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		topPanel.add(colorField, BorderLayout.WEST);
		topPanel.add(noneButton, BorderLayout.CENTER);

		this.add(topPanel, BorderLayout.NORTH);
		this.add(otherButton, BorderLayout.SOUTH);
		this.add(colorPanel, BorderLayout.CENTER);
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		Color old = this.selectedColor;
		this.selectedColor = selectedColor;
		this.firePropertyChange(SELECTEDCOLORCHANGE, old, selectedColor);
	}

	public void setTextColor(Color color) {
		colorField.setColor(color);
	}

	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
		JLabel label = new JLabel("Powered By zht");
		label.setForeground(Color.BLUE);
		label.setFont(new Font("Dialog", Font.PLAIN, 8));
		int width = this.getWidth();
		int height = this.getHeight();
		double w = label.getPreferredSize().getWidth();
		double h = label.getPreferredSize().getHeight();
		double x = width - w - 5;
		double y = height - h - 5;
		SwingUtilities.paintComponent(g, label, this, new Rectangle((int) x, (int) y, (int) w, (int) h));
	}

	public JButton getNoneButton() {
		return noneButton;
	}

	public JButton getOtherButton() {
		return otherButton;
	}

	public ColorTextField getColorField() {
		return colorField;
	}
}

class ColorButton extends JButton {
	public ColorButton(String text) {
		super(text);
		this.setContentAreaFilled(false);
	}

	protected void paintComponent(Graphics g) {
		if (this.getModel().isRollover()) {
			Graphics2D g2 = (Graphics2D) g;
			GradientPaint p = new GradientPaint(0, 0, new Color(0xFFFFFF), 0, getHeight(), new Color(0xC8D2DE));
			Paint oldPaint = g2.getPaint();
			g2.setPaint(p);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.setPaint(oldPaint);
		}
		super.paintComponent(g);

	}

}