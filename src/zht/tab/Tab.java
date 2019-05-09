/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package zht.tab;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;

public class Tab {
	private Component component;
	private boolean closeAble = true;
	private Icon icon;
	private String title;

	public Tab(Component component, boolean closeAble, Icon icon, String title) {
		super();
		this.component = component;
		this.closeAble = closeAble;
		this.icon = icon;
		this.title = title;
	}

	public JLabel getTabLabel() {
		JLabel label = new JLabel();
		if (this.title != null) {
			label.setText(this.title);
		}
		if (this.icon != null) {
			label.setIcon(icon);
		}
		return label;
	}

	public Component getComponent() {
		return component;
	}

	public boolean isCloseAble() {
		return closeAble;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}
}