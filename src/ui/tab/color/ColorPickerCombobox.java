package ui.tab.color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

public class ColorPickerCombobox extends JComboBox {
	public static final String SELECTEDCOLOR = "selectedcolor";

	public ColorPickerCombobox() {
		this.setEditable(false);
		this.setRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				ColorTextField textField = new ColorTextField();
				textField.setColor(getSelectedColor());
				return textField;
			}
		});
		this.setPreferredSize(new Dimension(150, 22));
	}

	public void updateUI() {
		this.setUI(new MetalDateComboBoxUI());
	}

	class MetalDateComboBoxUI extends MetalComboBoxUI {
		protected ComboPopup createPopup() {
			return new ColorPopup(comboBox);
		}
	}

	class ColorPopup extends BasicComboPopup implements PropertyChangeListener {
		private ColorPicker picker;

		public ColorPopup(JComboBox box) {
			super(box);
			picker = new ColorPicker();
			picker.addPropertyChangeListener(this);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(picker, BorderLayout.CENTER);
			this.setLayout(new BorderLayout());
			this.add(panel, BorderLayout.CENTER);
			this.setBorder(BorderFactory.createEmptyBorder());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName() == ColorPicker.SELECTEDCOLORCHANGE) {
				comboBox.putClientProperty(SELECTEDCOLOR, picker.getSelectedColor());
				comboBox.setPopupVisible(false);
			}
			if (evt.getPropertyName() == ColorPicker.MORECOLORSELECTION) {
				comboBox.setPopupVisible(false);
			}
		}
	}

	public Color getSelectedColor() {
		Object obj = getClientProperty(SELECTEDCOLOR);
		if (obj != null && obj instanceof Color) {
			return (Color) obj;
		} else {
			return null;
		}
	}

	public void setSelectedColor(Color selectedColor) {
		putClientProperty(SELECTEDCOLOR, selectedColor);
	}
}