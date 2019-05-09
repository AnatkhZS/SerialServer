/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package zht.tab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import zht.utils.ZHTUtils;
import zht.utils.ZhtShortcutkeyUtils;

public class TabConfigDialog extends JDialog {

	private TabConfigPanel configPanel;

	public TabConfigDialog(ZHTTabbedPane pane) {
		super(ZHTUtils.getWindowForComponent(pane));
		this.setTitle("Config");
		configPanel = new TabConfigPanel(pane);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(configPanel, BorderLayout.CENTER);

		JButton button = new JButton("Close");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(button);
		panel.add(buttonPanel, BorderLayout.SOUTH);

		ZhtShortcutkeyUtils.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setVisible(false);
				}
			}
		});

		this.setContentPane(panel);
		this.setSize(400, 300);
		this.setLocationRelativeTo(pane);
		this.setModal(true);
	}

}