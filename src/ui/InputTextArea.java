package ui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class InputTextArea extends JTextArea{
	public InputTextArea() {
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				int keyCode = e.getKeyCode();
				if(keyCode == KeyEvent.VK_DOWN) {
					System.out.println("down");
					return;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}});
	}
	public static void main(String args[]) {
		JFrame frame = new JFrame();
		frame.setSize(400, 300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		InputTextArea textArea = new InputTextArea();
		textArea.setText("Hi\nHi\nHi");
		panel.setLayout(new BorderLayout());
		panel.add(textArea);
		frame.add(panel);
		
		frame.setVisible(true);
	}
}
