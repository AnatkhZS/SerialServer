/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package zht.tab.demo;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import zht.tab.Tab;
import zht.tab.TabConfigDialog;
import zht.tab.TabPopupMenuGenerator;
import zht.tab.ZHTChromeTabbedPane;
import zht.tab.ZHTTabbedPane;
import zht.utils.ZHTUtils;

public class TabDemo extends JPanel {

	public static void main(String[] args) {
		ZHTUtils.showComponentInFrame(new TabDemo());
	}

	public TabDemo() {
		initGUI();
	}

	private ZHTTabbedPane pane = new ZHTChromeTabbedPane();
	private TabConfigDialog configDialog = new TabConfigDialog(pane);

	private void initGUI() {
		pane.setTabWidth(180);
		initTabPane();
		initTabPopupMenu();
		this.setLayout(new BorderLayout());
		this.add(pane, BorderLayout.CENTER);
	}

	private void initTabPopupMenu() {
		pane.setPopMenuGenerator(new TabPopupMenuGenerator() {
			public JPopupMenu generate(ZHTTabbedPane zhtTabbedPane, MouseEvent mouseEvent) {
				JPopupMenu menu = new JPopupMenu();
				final Tab currentTab = pane.getMouseOverTab();
				final List tabList = new ArrayList(pane.getTabList());
				final int index = tabList.indexOf(currentTab);
				final int size = tabList.size();
				JMenuItem item;
				if (currentTab != null) {

					item = createMenuItem("Close Tab", new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							pane.closeTab(currentTab);
						}
					});
					menu.add(item);

					item = createMenuItem("Close other tabs", new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							List removeList = new ArrayList();
							for (int i = 0; i < size; i++) {
								Tab tab = (Tab) tabList.get(i);
								if (tab != currentTab) {
									removeList.add(tab);
								}
							}
							int rsize = removeList.size();
							for (int i = 0; i < rsize; i++) {
								Tab tab = (Tab) removeList.get(i);
								pane.closeTab(tab);
							}
						}
					});
					if (size == 1) {
						item.setEnabled(false);
					}
					menu.add(item);

					item = createMenuItem("Close tabs to the left", new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							List removeList = new ArrayList();
							for (int i = index - 1; i >= 0; i--) {
								Tab tab = (Tab) tabList.get(i);
								removeList.add(tab);
							}
							int rsize = removeList.size();
							for (int i = 0; i < rsize; i++) {
								Tab tab = (Tab) removeList.get(i);
								pane.closeTab(tab);
							}
						}
					});
					if (index == 0) {
						item.setEnabled(false);
					}
					menu.add(item);
					item = createMenuItem("Close tabs to the right", new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							for (int i = size - 1; i > index; i--) {
								pane.closeTab(i);
							}
						}
					});
					if (index == tabList.size() - 1) {
						item.setEnabled(false);
					}
					menu.add(item);
					menu.addSeparator();
				}
				item = createMenuItem("Config", new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						configDialog.setVisible(true);
					}
				});
				menu.add(item);

				menu.addSeparator();
				item = createMenuItem("About", new ActionListener() {
					public void actionPerformed(ActionEvent e) {

					}
				});
				menu.add(item);
				return menu;
			}
		});
	}

	private JMenuItem createMenuItem(String label, ActionListener listener) {
		final JMenuItem item = new JMenuItem(label);
		if (listener != null) {
			item.addActionListener(listener);
		}
		item.setOpaque(false);
		return item;
	}

	private void initTabPane() {
		ImagePanel panel = new ImagePanel(new ImageIcon(this.getClass().getResource("google.png")));
		Icon icon = new ImageIcon(this.getClass().getResource("1.png"));
		Tab tab = new Tab(panel, false, icon, "GoogleZH");
		pane.addTab(tab, true);

		panel = new ImagePanel(new ImageIcon(this.getClass().getResource("googleen.png")));
		icon = new ImageIcon(this.getClass().getResource("2.png"));
		tab = new Tab(panel, true, icon, "GoogleEN");
		pane.addTab(tab);

		panel = new ImagePanel(new ImageIcon(this.getClass().getResource("googlenews.png")));
		icon = new ImageIcon(this.getClass().getResource("3.png"));
		tab = new Tab(panel, true, icon, "GoogleNews");
		pane.addTab(tab);

		panel = new ImagePanel(new ImageIcon(this.getClass().getResource("micro.png")));
		icon = new ImageIcon(this.getClass().getResource("1.png"));
		tab = new Tab(panel, true, icon, "Micro");
		pane.addTab(tab);

		panel = new ImagePanel(new ImageIcon(this.getClass().getResource("msn.png")));
		icon = new ImageIcon(this.getClass().getResource("2.png"));
		tab = new Tab(panel, true, icon, "MSN");
		pane.addTab(tab);

		pane.setBackgroundImage(new ImageIcon(this.getClass().getResource("back.jpg")).getImage());
	}

}

class ImagePanel extends JComponent {
	ImageIcon image;

	public ImagePanel(ImageIcon image) {
		this.image = image;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image.getImage(), 0, 0, this);
	}
}