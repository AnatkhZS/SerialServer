/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package ui.tab.tab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ui.tab.color.ColorPickerCombobox;
import ui.tab.utils.layout.TableLayout;

public class TabConfigPanel extends JPanel {
	private TabbedPane pane;
	private RectangleTabbedPane configPane;

	public TabConfigPanel(TabbedPane pane) {
		this.pane = pane;
		configPane = new RectangleTabbedPane();
		initGUI();
	}

	private void initGUI() {
		JPanel panel = new ColorPanel(pane);
		Tab tab = new Tab(panel, false, null, -1, "Color"); //-1 is compatible with sessionId
		configPane.addTab(tab);
		panel = new BooleanPanel(pane);
		tab = new Tab(panel, false, null, -1, "Boolean"); //-1 is compatible with sessionId
		configPane.addTab(tab);
		panel = new NumberPanel(pane);
		tab = new Tab(panel, false, null, -1, "Number"); //-1 is compatible with sessionId
		configPane.addTab(tab);
		this.setLayout(new BorderLayout());
		this.add(configPane, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		configPane.setTabHeight(30);
	}

}

class ColorPanel extends JPanel {
	private TabbedPane pane;

	public ColorPanel(TabbedPane pane) {
		this.pane = pane;
		initGUI();
	}

	private ColorPickerCombobox tabFillBox;
	private ColorPickerCombobox tabOverBox;
	private ColorPickerCombobox tabGradientBox;
	private ColorPickerCombobox selectedTabGradientBox;
	private ColorPickerCombobox selectedTabFillColorBox;

	private void initGUI() {
		double[] rows = { TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED,
				TableLayout.PREFERRED };
		double[] cols = { TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL };
		double[][] lays = { cols, rows };
		this.setLayout(new TableLayout(lays));
		int row = 0;
		this.add(new JLabel("Tab Fill Color"), "0," + row);
		tabFillBox = createColorPickerCombobox(pane.getTabFillColor(), new Thread() {
			public void run() {
				pane.setTabFillColor(tabFillBox.getSelectedColor());
			}
		});
		this.add(tabFillBox, "1," + row);
		row++;

		this.add(new JLabel("Tab Over Color"), "0," + row);
		tabOverBox = createColorPickerCombobox(pane.getTabOverColor(), new Thread() {
			public void run() {
				pane.setTabOverColor(tabOverBox.getSelectedColor());
			}
		});
		this.add(tabOverBox, "1," + row);
		row++;

		this.add(new JLabel("Selected Tab Fill Color"), "0," + row);
		selectedTabFillColorBox = createColorPickerCombobox(pane.getSelectedTabFillColor(), new Thread() {
			public void run() {
				pane.setSelectedTabFillColor(selectedTabFillColorBox.getSelectedColor());
			}
		});
		new ColorPickerCombobox();
		this.add(selectedTabFillColorBox, "1," + row);
		row++;

		final JCheckBox check = new JCheckBox("Gradient");
		check.setSelected(pane.isTabGradient());
		check.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pane.setTabGradient(check.isSelected());
				tabGradientBox.setEnabled(check.isSelected());
				selectedTabGradientBox.setEnabled(check.isSelected());
			}
		});
		JTitle title = new JTitle(check);
		this.add(title, "0," + row + ",2," + row);
		row++;

		this.add(new JLabel("Tab Gradient Color"), "0," + row);
		tabGradientBox = createColorPickerCombobox(pane.getTabGradientColor(), new Thread() {
			public void run() {
				pane.setTabGradientColor(tabGradientBox.getSelectedColor());
			}
		});
		this.add(tabGradientBox, "1," + row);
		row++;

		this.add(new JLabel("Selected Tab Gradient Color"), "0," + row);
		selectedTabGradientBox = createColorPickerCombobox(pane.getSelectedTabGradientColor(), new Thread() {
			public void run() {
				pane.setSelectedTabGradientColor(selectedTabGradientBox.getSelectedColor());
			}
		});
		this.add(selectedTabGradientBox, "1," + row);
		row++;

		title = new JTitle(new JLabel());
		this.add(title, "0," + row + ",2," + row);
		row++;

	}

	private ColorPickerCombobox createColorPickerCombobox(Color color, final Thread thread) {
		ColorPickerCombobox comboBox = new ColorPickerCombobox();
		comboBox.setSelectedColor(color);
		comboBox.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(ColorPickerCombobox.SELECTEDCOLOR)) {
					thread.run();
				}
			}
		});
		return comboBox;
	}

}

class BooleanPanel extends JPanel {
	private TabbedPane pane;

	public BooleanPanel(TabbedPane pane) {
		this.pane = pane;
		initGUI();
	}

	private void initGUI() {
		double[] rows = { TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED };
		double[] cols = { TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL };
		double[][] lays = { cols, rows };
		this.setLayout(new TableLayout(lays));
		final JCheckBox selectedCheckBox = new JCheckBox("Selected On Right Pressed");
		selectedCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pane.setSelectedOnRightPressed(selectedCheckBox.isSelected());
			}
		});
		selectedCheckBox.setSelected(pane.isSelectedOnRightPressed());
		int index = 0;
		this.add(selectedCheckBox, "0,"+index);
		index++;
		final JCheckBox titleInCenterCheckBox = new JCheckBox("Title In Center");
		titleInCenterCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pane.setTitleInCenter(titleInCenterCheckBox.isSelected());
			}
		});
		titleInCenterCheckBox.setSelected(pane.isTitleInCenter());
		this.add(titleInCenterCheckBox, "0,"+index);
		index++;
		
		final JCheckBox useActualWidthCheckBox = new JCheckBox("Use Actual Width");
		useActualWidthCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pane.setUseActualWidth(useActualWidthCheckBox.isSelected());
			}
		});
		useActualWidthCheckBox.setSelected(pane.isUseActualWidth());
		this.add(useActualWidthCheckBox, "0,"+index);
		index++;
		
		final JCheckBox enableSwitchTabWidthKeyBox = new JCheckBox("Enable Switch Tab Width Key");
		enableSwitchTabWidthKeyBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pane.setEnableSwitchTabWidthKey(enableSwitchTabWidthKeyBox.isSelected());
			}
		});
		enableSwitchTabWidthKeyBox.setSelected(pane.isEnableSwitchTabWidthKey());
		this.add(enableSwitchTabWidthKeyBox, "0,"+index);
		index++;
		
	}
}

class NumberPanel extends JPanel {
	private TabbedPane pane;

	public NumberPanel(TabbedPane pane) {
		this.pane = pane;
		initGUI();
	}

	SliderPanel tabHeightSlider;
	SliderPanel tabWidthtSlider;
	SliderPanel borderHeightSlider;
	SliderPanel topGapSlider;

	private void initGUI() {
		double[] rows = { TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED,
				TableLayout.PREFERRED };
		double[] cols = { TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL };
		double[][] lays = { cols, rows };
		this.setLayout(new TableLayout(lays));
		int row = 0;
		this.add(new JLabel("Tab Height"), "0," + row);
		tabHeightSlider = createNumberSlider(10, 50, pane.getTabHeight(), new Runnable() {
			public void run() {
				pane.setTabHeight(tabHeightSlider.getValue());
			}
		});
		this.add(tabHeightSlider, "1," + row);
		row++;

		this.add(new JLabel("Tab Width"), "0," + row);
		tabWidthtSlider = createNumberSlider(60, 200, pane.getTabWidth(), new Runnable() {
			public void run() {
				pane.setTabWidth(tabWidthtSlider.getValue());
			}
		});
		this.add(tabWidthtSlider, "1," + row);
		row++;

		this.add(new JLabel("Base Border Height"), "0," + row);
		borderHeightSlider = createNumberSlider(1, 10, pane.getBaseBorderHeight(), new Runnable() {
			public void run() {
				pane.setBaseBorderHeight(borderHeightSlider.getValue());
			}
		});
		this.add(borderHeightSlider, "1," + row);
		row++;

		this.add(new JLabel("Top Gap"), "0," + row);
		topGapSlider = createNumberSlider(1, 5, pane.getTopGap(), new Runnable() {
			public void run() {
				pane.setTopGap(topGapSlider.getValue());
			}
		});
		this.add(topGapSlider, "1," + row);
		row++;
	}

	private SliderPanel createNumberSlider(int min, int max, int value, final Runnable thread) {
		return new SliderPanel(min, max, value, thread);
	}
}

class SliderPanel extends JPanel {
	JSlider slider;
	JTextField field = new JTextField(3);

	public SliderPanel(int min, int max, int value, final Runnable thread) {
		slider = new JSlider(min, max);
		field.setText("" + value);
		field.setEditable(false);
		slider.setValue(value);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				thread.run();
				field.setText(slider.getValue() + "");
			}
		});
		this.add(slider, BorderLayout.CENTER);
		this.add(field, BorderLayout.SOUTH);
	}

	public int getValue() {
		return slider.getValue();
	}

	public JSlider getSlider() {
		return slider;
	}
}

class JTitle extends JPanel {
	public JTitle(Component comp) {
		setLayout(new GridBagLayout());
		add(comp, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(new JSeparator(), new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0, 0), 0, 0));
	}
}