/*
*
* @author zhangtao
*
* Msn & Mail: zht_dream@hotmail.com
*/
package ui.tab.about;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

public class AboutDialog extends javax.swing.JDialog implements ActionListener {
	private static AboutDialog instance;
	private JEditorPane contentEditorPane;

	public static AboutDialog getInstance() {
		if (instance == null) {
			instance = new AboutDialog();
		}
		return instance;
	}

	public AboutDialog() {
		super();
		this.setModal(true);
		initGUI();
	}

	private void initGUI() {
		this.setTitle("\u5173\u4e8e");
		JScrollPane contentPane = new JScrollPane();
		getContentPane().add(contentPane, BorderLayout.CENTER);
		contentEditorPane = new JEditorPane();
		contentPane.setViewportView(contentEditorPane);
		try {
			contentEditorPane.setPage(AboutDialog.class.getResource("SwingCurve.html"));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		contentEditorPane.setEditable(false);
		contentEditorPane.addHyperlinkListener(createHyperLinkListener());
		JPanel panel = new JPanel();
		FlowLayout btn_JPLayout = new FlowLayout();
		btn_JPLayout.setAlignment(FlowLayout.RIGHT);
		btn_JPLayout.setHgap(50);
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setPreferredSize(new java.awt.Dimension(356, 35));
		panel.setLayout(btn_JPLayout);
		JButton okButton = new JButton();
		panel.add(okButton);
		okButton.setText("\u786e\u5b9a");
		okButton.addActionListener(this);

		this.setSize(500, 400);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
	}

	private HyperlinkListener createHyperLinkListener() {
		return new HyperlinkListener() {

			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (hle.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (hle instanceof HTMLFrameHyperlinkEvent) {
						((HTMLDocument) contentEditorPane.getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) hle);
					} else {
						URLOpener.openURL(hle.getURL().toString());
					}

				}
			}

		};
	}

	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
}