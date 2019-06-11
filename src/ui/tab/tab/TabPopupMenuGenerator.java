package ui.tab.tab;

import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public interface TabPopupMenuGenerator {
	public JPopupMenu generate(TabbedPane zhtTabbedPane, MouseEvent mouseEvent);
}