/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package zht.tab;

import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public interface TabPopupMenuGenerator {
	public JPopupMenu generate(ZHTTabbedPane zhtTabbedPane, MouseEvent mouseEvent);
}