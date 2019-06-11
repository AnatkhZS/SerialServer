package ui.tab.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.JFrame;

public class Utils {
	public static JFrame showComponentInFrame(Container contentPane) {
		return showComponentInFrame(contentPane, "www.wiui.net   _by ZHT");
	}

	public static JFrame showComponentInFrame(Container contentPane, String title) {
		JFrame frame = new JFrame();
		frame.setTitle(title);
		frame.setContentPane(contentPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		return frame;
	}

	public static String getColorHexString(Color c, boolean isAlpha) {
		String rgb = Integer.toHexString(c.getRGB() & 0xffffff);
		String alpha = Integer.toHexString(c.getAlpha());
		String result = "#000000".substring(0, 7 - rgb.length()).concat(rgb);
		if (isAlpha) {
			return (result + "00".substring(0, 2 - alpha.length()).concat(alpha)).toUpperCase();
		} else {
			return result.toUpperCase();
		}
	}

	public static Window getWindowForComponent(Component component) {
		if (component == null) {
			return null;
		}
		if (component instanceof Frame || component instanceof Dialog) {
			return (Window) component;
		}
		return getWindowForComponent(component.getParent());
	}
}