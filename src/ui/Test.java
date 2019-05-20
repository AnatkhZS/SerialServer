package ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

public class Test{
	public static void main(String args[]) {
		new Test().init();
	}
	
	public void init() {
		JFrame connectionFrame = new JFrame("Options");
		
//		connectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		connectionFrame.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Test"));
		panel.add(new JButton("button"));

		connectionFrame.add(panel);
		
		connectionFrame.setSize(400,300);
		connectionFrame.setVisible(true);
	}
	

}
