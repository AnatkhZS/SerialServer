/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package zht.tab;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ui.MyHandler;
import ui.RemoveTabEvent;
import zht.about.AboutDialog;
import zht.tab.interaction.DefaultInputHanlder;
import zht.tab.interaction.MoveInputHanlder;
import zht.tab.interaction.PopupMenuInputHanlder;
import zht.tab.interaction.TabInputHanlder;
import zht.utils.ZHTUtils;
import zht.utils.ZhtShortcutkeyUtils;

public abstract class ZHTTabbedPane extends JLayeredPane {

	public static final String MODE_AUTO_RESIZE = "mode.auto.resize";
	public static final String MODE_SHOW_MORE_IN_POPUPMENU = "mode.show.in.popupmenu";

	private JPanel contentPanel = new JPanel(new BorderLayout());
	public List tabList = new ArrayList();
	protected Map tabViewMap = new HashMap();
	protected int tabHeight = 23;
	protected int tabWidth = 120;
	protected int baseBorderHeight = 3;
	protected int topGap = 1;
	private int labelGap = 2;

	private boolean tabGradient = true;
	private Color tabFillColor = Color.WHITE;
	private Color tabOverColor = Color.WHITE.darker();
	private Color tabGradientColor = Color.WHITE;
	private Color selectedTabFillColor = Color.WHITE.darker();
	private Color selectedTabGradientColor = Color.WHITE;

	private boolean selectedOnRightPressed = true;

	private Font titleFont = new Font("Dialog", Font.BOLD, 12);
	private boolean isTitleInCenter = true;;

	private TabPopupMenuGenerator popMenuGenerator;

	protected Icon closeIcon;
	protected Icon closingIcon;

	protected Tab selectedTab = null;
	private Tab mouseOverCloseTab = null;
	protected Tab mouseOverTab = null;

	protected boolean useActualWidth = false;
	private boolean enableSwitchTabWidthKey = true;

	private boolean _invalidateTabFlag = false;

	private int dragOffset = 0;

	private Image backgroundImage;
	private TexturePaint backgroundPaint;

	public ZHTTabbedPane() {
		closeIcon = new ImageIcon(this.getClass().getResource("close.png"));
		closingIcon = new ImageIcon(this.getClass().getResource("closing.png"));
		this.setLayout(null);
		this.add(contentPanel, new Integer(0));
		installListener();
		this.setFocusable(true);
		this.setToolTipText("");
	}
	
	public int detect() {
		return tabViewMap.size();
	}

	protected void installInteractionListener() {
		addInteractionListener(new DefaultInputHanlder(this));
		addInteractionListener(new PopupMenuInputHanlder(this));
		addInteractionListener(new MoveInputHanlder(this));
	}

	protected void addInteractionListener(TabInputHanlder hanlder) {
		this.addMouseListener(hanlder);
		this.addMouseMotionListener(hanlder);
		this.addKeyListener(hanlder);
	}

	protected void installListener() {
		installInteractionListener();

		ZhtShortcutkeyUtils.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (ZHTUtils.getWindowForComponent(ZHTTabbedPane.this).isActive()) {
					if (e.getKeyCode() == KeyEvent.VK_L && e.isControlDown() && e.isShiftDown()) {
						AboutDialog.getInstance().setVisible(true);
					}
					if (e.getKeyCode() == KeyEvent.VK_W && e.isControlDown()) {
						if (e.isShiftDown()) {
							int size = tabList.size();
							List removeList = new ArrayList();
							for (int i = 0; i < size; i++) {
								Tab tab = (Tab) tabList.get(i);
								if (tab.isCloseAble()) {
									removeList.add(tab);
								}
							}
							int rsize = removeList.size();
							for (int i = 0; i < rsize; i++) {
								Tab tab = (Tab) removeList.get(i);
								closeTab(tab);
							}
						} else {
							closeTab(getSelectedTab());
						}
					}
					if (e.isControlDown()&&isEnableSwitchTabWidthKey()) {
						try {
							int index = Integer.parseInt(KeyEvent.getKeyText(e.getKeyCode()) + "");
							setSelectedTab(index - 1);
						} catch (Exception e2) {
						}
					}
				}
			}
		});

	}

	private void setMouseOverCloseTabByPoint(Point point) {
		Tab tab = getTabByPoint(point);
		if (tab != null && tab.isCloseAble()) {
			TabView view = (TabView) tabViewMap.get(tab);
			if (view.getCloseIconBound().contains(point)) {
				setMouseOverCloseTab(tab);
			} else {
				setMouseOverCloseTab(null);
			}
		}
	}

	public void setMouseOverTabByPoint(Point point) {
		Tab tab = getTabByPoint(point);
		setMouseOverTab(tab);
		setMouseOverCloseTabByPoint(point);
	}

	public void setMouseOverTab(Tab tab) {
		if (tab != mouseOverTab) {
			mouseOverTab = tab;
			this.repaint();
		}
	}

	public void closeTab(int index) {
		closeTab((Tab) tabList.get(index));
		System.out.println("Close Tab index");
	}

	public void closeTab(Tab tab) {
		tab.trigger(new RemoveTabEvent(this, tab.getTitle()));
		if (tab.isCloseAble()) {
			int index = tabList.indexOf(tab);
			tabViewMap.remove(tab);
			tabList.remove(tab);
			if (tab == selectedTab) {
				if (index >= tabList.size()) {
					index = tabList.size() - 1;
				}
				this.setSelectedTab(index);
			}
			this.invalidateTab();
			this.validateTab();
		}
		System.out.println("Close Tab tab");
	}

	public void setMouseOverCloseTab(Tab tab) {
		if (mouseOverCloseTab != tab) {
			mouseOverCloseTab = tab;
			this.repaint();
		}
	}

	public Tab getTabByPoint(Point point) {
		int size = tabList.size();
		TabView view = (TabView) tabViewMap.get(selectedTab);
		if (view == null) {
			return null;
		}
		if (view.getShape().contains(point)) {
			return selectedTab;
		}
		for (int i = 0; i < size; i++) {
			Tab tab = (Tab) tabList.get(i);
			view = (TabView) tabViewMap.get(tab);
			if (view.getShape().contains(point)) {
				return tab;
			}
		}
		return null;
	}

	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		setContentBounds();
	}

	private void setContentBounds() {
		contentPanel.setBounds(this.getX(), this.getY() + tabHeight + topGap + baseBorderHeight, this.getWidth(), this.getHeight() - tabHeight);
		this.invalidateTab();
	}

	public String getToolTipText(MouseEvent event) {
		Tab tab = getTabByPoint(event.getPoint());
		if (tab == null) {
			return null;
		}
		return tab.getTitle();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		validateTab();
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (backgroundPaint != null) {
			g2d.setPaint(backgroundPaint);
			g2d.fill(new Rectangle2D.Double(0, 0, this.getWidth(), tabHeight + tabHeight + topGap + baseBorderHeight));
		}
		int size = tabList.size();
		if (size == 0) {
			g2d.dispose();
			return;
		}
		for (int i = 0; i < size; i++) {
			Tab tab = (Tab) tabList.get(i);
			if (tab != selectedTab) {
				paintTab(g2d, tab);
			}
		}
		if (selectedTab != null) {
			paintTab(g2d, selectedTab);
		}
		g2d.setColor(selectedTabFillColor);
		g2d.fillRect(0, tabHeight + topGap, this.getWidth(), baseBorderHeight);
		g2d.dispose();
	}

	protected void paintTab(Graphics2D g, Tab tab) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		System.out.println("Painting tab, size:"+detect());
		TabView view = (TabView) tabViewMap.get(tab);
		Color color = tabFillColor;
		Color gradientColor = tabGradientColor;
		if (tab == mouseOverTab) {
			color = tabOverColor;
		}
		int offset = 0;
		if (tab == selectedTab) {
			color = selectedTabFillColor;
			gradientColor = selectedTabGradientColor;
			offset = dragOffset;
			if (offset != 0) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
			}
		}
		g2d.translate(offset, 0);
		if (color != null) {
			Paint paint = color;
			if (tabGradient) {
				paint = new GradientPaint(0, topGap, gradientColor, 0, topGap + tabHeight, color);
			}
			g2d.setPaint(paint);
			Shape shape = view.getShape();
			g2d.fill(shape);

			g2d.setColor(color.darker());
			g2d.draw(shape);
		}
		double sumWidth = 0;
		JLabel label;

		Icon tabCloseIcon = closeIcon;
		if (getMouseOverCloseTab() == tab) {
			tabCloseIcon = closingIcon;
		}

		Rectangle2D contentBound = view.getContentBound();
		double iconWidth = 0;
		if (tab != selectedTab) {
			//icon > close icon > label
			double labelX = 0;
			if (tab.getIcon() != null) {
				Icon icon = tab.getIcon();
				label = new JLabel(icon);
				Dimension size = label.getPreferredSize();
				double x = contentBound.getX();
				double y = contentBound.getY();
				iconWidth = size.width;
				double iconHeight = size.height;
				if (size.width > contentBound.getWidth()) {
					iconWidth = contentBound.getWidth();
				}
				if (iconHeight >= contentBound.getHeight()) {
					iconHeight = contentBound.getHeight();
				} else {
					y = contentBound.getCenterY() - size.height / 2.0;
				}
				sumWidth += iconWidth;
				SwingUtilities.paintComponent(g2d, label, this, (int) x, (int) y, (int) iconWidth, (int) iconHeight);
			}
			if (sumWidth >= contentBound.getWidth()) {
				g2d.dispose();
				return;
			}
			labelX = contentBound.getX() + sumWidth;
			if (tab.isCloseAble() && view.getCloseIconBound() != null) {
				label = new JLabel(tabCloseIcon);
				Rectangle2D closeIconBound = view.getCloseIconBound();
				double x = closeIconBound.getX();
				if (x >= contentBound.getX() + sumWidth) {
					double y = closeIconBound.getY();
					double closeIconWidth = closeIconBound.getWidth();
					double closeIconHeight = closeIconBound.getHeight();
					sumWidth += closeIconWidth;
					SwingUtilities.paintComponent(g2d, label, this, (int) x, (int) y, (int) closeIconWidth, (int) closeIconHeight);
				} else {
					g2d.dispose();
					return;
				}
			}
			if (sumWidth >= contentBound.getWidth()) {
				g2d.dispose();
				return;
			}
			if (tab.getTitle() != null && tab.getTitle().trim().length() > 0) {
				sumWidth += labelGap;
				if (sumWidth < contentBound.getWidth()) {
					label = new JLabel(tab.getTitle());
					if (titleFont != null) {
						label.setFont(titleFont);
					}
					Dimension size = label.getPreferredSize();
					double y = contentBound.getY();
					double labelWidth = size.getWidth();
					double labelHeight = size.getHeight();
					if (labelHeight < contentBound.getHeight()) {
						y = contentBound.getCenterY() - size.getHeight() / 2.0;
					}
					if (labelWidth > contentBound.getWidth() - sumWidth) {
						labelWidth = contentBound.getWidth() - sumWidth;
					}
					if (isTitleInCenter) {
						double x = contentBound.getX() + contentBound.getWidth() / 2 - labelWidth / 2;
						if (x < (contentBound.getX() + iconWidth + labelGap)) {
							x = contentBound.getX() + iconWidth + labelGap;
						}
						SwingUtilities.paintComponent(g2d, label, this, (int) x, (int) y, (int) labelWidth, (int) labelHeight);
					} else {
						SwingUtilities.paintComponent(g2d, label, this, (int) (labelX + labelGap), (int) y, (int) labelWidth, (int) labelHeight);
					}
				}
			}
		} else {
			// close > icon >label
			double labelX = 0;
			if (tab.isCloseAble() && view.getCloseIconBound() != null) {
				label = new JLabel(tabCloseIcon);
				Rectangle2D closeIconBound = view.getCloseIconBound();
				double x = closeIconBound.getX();
				double y = closeIconBound.getY();
				double closeIconWidth = closeIconBound.getWidth();
				double closeIconHeight = closeIconBound.getHeight();
				sumWidth += closeIconWidth;
				SwingUtilities.paintComponent(g2d, label, this, (int) x, (int) y, (int) closeIconWidth, (int) closeIconHeight);
			}
			if (tab.isCloseAble() && view.getCloseIconBound() == null) {
				g2d.dispose();
				return;
			}
			if (sumWidth >= contentBound.getWidth()) {
				g2d.dispose();
				return;
			}
			if (tab.getIcon() != null) {
				Icon icon = tab.getIcon();
				label = new JLabel(icon);
				Dimension size = label.getPreferredSize();
				double x = contentBound.getX();
				double y = contentBound.getY();
				iconWidth = size.width;
				double iconHeight = size.height;

				if ((contentBound.getWidth() - sumWidth) < iconWidth) {
					g2d.dispose();
					return;
				}
				if (size.width > contentBound.getWidth()) {
					iconWidth = contentBound.getWidth();
				}
				if (iconHeight >= contentBound.getHeight()) {
					iconHeight = contentBound.getHeight();
				} else {
					y = contentBound.getCenterY() - size.height / 2.0;
				}
				sumWidth += iconWidth;
				SwingUtilities.paintComponent(g2d, label, this, (int) x, (int) y, (int) iconWidth, (int) iconHeight);
			}
			labelX = contentBound.getX() + iconWidth;
			if (tab.getTitle() != null && tab.getTitle().trim().length() > 0) {
				sumWidth += labelGap;
				if (sumWidth < contentBound.getWidth()) {
					label = new JLabel(tab.getTitle());
					if (titleFont != null) {
						label.setFont(titleFont);
					}
					Dimension size = label.getPreferredSize();
					double y = contentBound.getY();
					double labelWidth = size.getWidth();
					double labelHeight = size.getHeight();
					if (labelHeight < contentBound.getHeight()) {
						y = contentBound.getCenterY() - size.getHeight() / 2.0;
					}
					if (labelWidth > contentBound.getWidth() - sumWidth) {
						labelWidth = contentBound.getWidth() - sumWidth;
					}
					if (isTitleInCenter) {
						double x = contentBound.getX() + contentBound.getWidth() / 2 - labelWidth / 2;
						if (x < (contentBound.getX() + iconWidth + labelGap)) {
							x = contentBound.getX() + iconWidth + labelGap;
						}
						SwingUtilities.paintComponent(g2d, label, this, (int) x, (int) y, (int) labelWidth, (int) labelHeight);
					} else {
						SwingUtilities.paintComponent(g2d, label, this, (int) (labelX + labelGap), (int) y, (int) labelWidth, (int) labelHeight);
					}
				}
			}
		}

		if (color != null) {
			Shape shape = view.getShape();
			if (tab == selectedTab && offset != 0) {
				g2d.setColor(color);
				g2d.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 2, 2 }, 0));
				g2d.translate(-offset, 0);
				g2d.draw(shape);
			}
		}
		g2d.dispose();
	}

	public void invalidateTab() {
		_invalidateTabFlag = true;
		this.repaint();
	}

	private void validateTab() {
		if (_invalidateTabFlag) {
			_invalidateTabFlag = false;
			tabViewMap.clear();
			calculateTab();
		}
	}

	protected abstract void calculateTab();

	protected abstract Shape createTabShape(Rectangle2D bounds);

	public void addTab(Tab tab) {
		this.addTab(tab, true);
	}

	public void addTab(Tab tab, boolean selected) {
		tabList.add(tab);
		if (selected || this.selectedTab == null) {
			setSelectedTab(tab);
		}
		invalidateTab();
	}

	public void insertTab(Tab tab, int index) {
		if (tabList.contains(tab)) {
			moveTab(tab, index);
		} else {
			tabList.add(index, tab);
		}
		invalidateTab();
	}

	public void removeTab(Tab tab) {
		if (tabList.contains(tab)) {
			removeTab(tabList.indexOf(tab));
			System.out.println("Removed");
		}
		System.out.println("Remove Tab tab");
	}

	public void removeTab(int index) {
		if (index >= 0 && index < tabList.size()) {
			Tab tab = (Tab) tabList.get(index);
			tabList.remove(tab);
			tabViewMap.remove(tab);
			if (tab == selectedTab) {
				if (index >= tabList.size()) {
					this.setSelectedTab(tabList.size() - 1);
				} else {
					this.setSelectedTab(index);
				}
			}
			this.invalidateTab();
		}
		System.out.println("Remove Tab index");
	}

	public void moveTab(Tab tab, int index) {
		int old = tabList.indexOf(tab);
		if (old != index) {
			tabList.remove(tab);
			tabList.add(index, tab);
		}
		invalidateTab();
		validateTab();
	}

	public void setSelectedTab(int index) {
		if (index < 0 || index >= tabList.size()) {
			return;
		}
		setSelectedTab((Tab) tabList.get(index));
	}

	public void setSelectedTab(Tab tab) {
		if (tab == null) {
			return;
		}
		if (this.selectedTab != tab) {
			this.selectedTab = tab;
			this.contentPanel.removeAll();
			this.contentPanel.add(tab.getComponent(), BorderLayout.CENTER);
			this.contentPanel.validate();
			this.contentPanel.repaint();
			this.repaint();
		}
	}

	protected double getTabWidth(Tab tab) {
		double w = 0;
		JLabel label;
		if (tab.getIcon() != null) {
			label = new JLabel(tab.getIcon());
			w += label.getPreferredSize().getWidth();
		}
		if (tab.getTitle() != null) {
			label = new JLabel(tab.getTitle());
			label.setFont(titleFont);
			w += labelGap + label.getPreferredSize().getWidth();
		}
		if (tab.isCloseAble()) {
			label = new JLabel(closeIcon);
			w += label.getPreferredSize().getWidth();
		}
		return w;
	}

	public TabPopupMenuGenerator getPopMenuGenerator() {
		return popMenuGenerator;
	}

	public void setPopMenuGenerator(TabPopupMenuGenerator popMenuGenerator) {
		this.popMenuGenerator = popMenuGenerator;
	}

	public int getTabHeight() {
		return tabHeight;
	}

	public void setTabHeight(int tabHeight) {
		this.tabHeight = tabHeight;
		setContentBounds();
	}

	public int getTabWidth() {
		return tabWidth;
	}

	public void setTabWidth(int tabWidth) {
		this.tabWidth = tabWidth;
		this.invalidateTab();
	}

	public int getBaseBorderHeight() {
		return baseBorderHeight;
	}

	public void setBaseBorderHeight(int baseBorderHeight) {
		this.baseBorderHeight = baseBorderHeight;
		setContentBounds();
	}

	public int getTopGap() {
		return topGap;
	}

	public void setTopGap(int topGap) {
		this.topGap = topGap;
		setContentBounds();
	}

	public boolean isTabGradient() {
		return tabGradient;
	}

	public void setTabGradient(boolean tabGradient) {
		this.tabGradient = tabGradient;
		this.repaint();
	}

	public Color getTabFillColor() {
		return tabFillColor;
	}

	public void setTabFillColor(Color tabFillColor) {
		this.tabFillColor = tabFillColor;
		this.repaint();
	}

	public Color getTabOverColor() {
		return tabOverColor;
	}

	public void setTabOverColor(Color tabOverColor) {
		this.tabOverColor = tabOverColor;
		this.repaint();
	}

	public Color getTabGradientColor() {
		return tabGradientColor;
	}

	public void setTabGradientColor(Color tabGradientColor) {
		this.tabGradientColor = tabGradientColor;
		this.repaint();
	}

	public Color getSelectedTabFillColor() {
		return selectedTabFillColor;
	}

	public void setSelectedTabFillColor(Color selectedTabFillColor) {
		this.selectedTabFillColor = selectedTabFillColor;
		this.repaint();
	}

	public Color getSelectedTabGradientColor() {
		return selectedTabGradientColor;
	}

	public void setSelectedTabGradientColor(Color selectedTabGradientColor) {
		this.selectedTabGradientColor = selectedTabGradientColor;
		this.repaint();
	}

	public Font getTitleFont() {
		return titleFont;
	}

	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
		this.repaint();
	}

	public Icon getCloseIcon() {
		return closeIcon;
	}

	public void setCloseIcon(Icon closeIcon) {
		this.closeIcon = closeIcon;
		this.invalidateTab();
	}

	public Icon getClosingIcon() {
		return closingIcon;
	}

	public void setClosingIcon(Icon closingIcon) {
		this.closingIcon = closingIcon;
		this.repaint();
	}

	public List getTabList() {
		return tabList;
	}

	public int getTabCount() {
		return tabList.size();
	}

	public int getTabIndex(Tab tab) {
		return tabList.indexOf(tab);
	}

	public Tab getTab(int index) {
		return (Tab) tabList.get(index);
	}

	public TabView getTabView(Tab tab) {
		return (TabView) tabViewMap.get(tab);
	}

	public TabView getTabView(int index) {
		Tab tab = getTab(index);
		return getTabView(tab);
	}

	public Tab getSelectedTab() {
		return selectedTab;
	}

	public Tab getMouseOverTab() {
		return mouseOverTab;
	}

	public boolean isSelectedOnRightPressed() {
		return selectedOnRightPressed;
	}

	public void setSelectedOnRightPressed(boolean selectedOnRightPressed) {
		this.selectedOnRightPressed = selectedOnRightPressed;
	}

	public boolean isTitleInCenter() {
		return isTitleInCenter;
	}

	public void setTitleInCenter(boolean isTitleInCenter) {
		this.isTitleInCenter = isTitleInCenter;
		this.repaint();
	}

	public boolean isUseActualWidth() {
		return useActualWidth;
	}

	public void setUseActualWidth(boolean useActualWidth) {
		if (this.useActualWidth != useActualWidth) {
			this.useActualWidth = useActualWidth;
			this.invalidateTab();
		}
	}

	public boolean isEnableSwitchTabWidthKey() {
		return enableSwitchTabWidthKey;
	}

	public void setEnableSwitchTabWidthKey(boolean enableSwitchTabWidthKey) {
		this.enableSwitchTabWidthKey = enableSwitchTabWidthKey;
	}

	public Tab getMouseOverCloseTab() {
		return mouseOverCloseTab;
	}

	public int getDragOffset() {
		return dragOffset;
	}

	public void setDragOffset(int dragOffset) {
		this.dragOffset = dragOffset;
		this.repaint();
	}

	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(Image backgroundImage) {
		this.backgroundImage = backgroundImage;
		if (backgroundImage != null) {
			BufferedImage backgroundBufferedImage = new BufferedImage(backgroundImage.getWidth(null), backgroundImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics ig = backgroundBufferedImage.getGraphics();
			ig.drawImage(backgroundImage, 0, 0, null);
			ig.dispose();
			backgroundPaint = new TexturePaint(backgroundBufferedImage, new Rectangle(0, 0, backgroundBufferedImage.getWidth(null), backgroundBufferedImage.getHeight(null)));
		} else {
			backgroundImage = null;
		}
		this.repaint();
	}
}