/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package zht.color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * 
 * @author zhangtao
 *
 * Msn & Mail: zht_dream@hotmail.com
 */
public class ColorPickerDemo extends JPanel {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setContentPane(new ColorPickerDemo());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private ZHTColorPicker picker = new ZHTColorPicker();
	private JPopupMenu menu = new JPopupMenu();

	public ColorPickerDemo() {
		menu.setLayout(new BorderLayout());
		menu.add(picker);
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					menu.show(ColorPickerDemo.this, e.getX(), e.getY());
				}
			}
		});
		picker.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName() == ZHTColorPicker.SELECTEDCOLORCHANGE) {
					menu.setVisible(false);
					Color color = picker.getSelectedColor();
					setBackground(color);
				}
				if (evt.getPropertyName() == ZHTColorPicker.MORECOLORSELECTION) {
					menu.setVisible(false);
				}
			}
		});
		JComboBox comboBox = new ColorPickerCombobox();
		this.add(comboBox);
	}
}