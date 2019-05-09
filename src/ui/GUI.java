package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import bl.LogRecorder;
import bl.SerialServer;
import data.SerialHandler;
import zht.tab.Tab;
import zht.tab.ZHTChromeTabbedPane;
import zht.tab.ZHTTabbedPane;

public class GUI {
	private JTextArea showTextArea;
	private JTextArea inputTextArea;
	private SerialHandler currentSerialHandler;
	private boolean isScrollBarClicked = false;
	
	public static void main(String args[]) {
		new GUI().run();
	}
	
	private void run() {
		
//		SerialHandler sh = new SerialHandler("cu.SLAB_USBtoUART", 115200);
//		currentSerialHandler = sh;
		GUICreator c = new GUICreator();
		Thread guiThread = new Thread(c);
		guiThread.start();
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		LogRecorder lr = new LogRecorder(sh, "/Users/zhusong/Documents/code/log.txt", true, true);
//		lr.startRecord();
//		SerialServer server = new SerialServer(sh);
//		server.createServer();
//		DisplayLog dl = new DisplayLog(sh);
//		Thread displayThread = new Thread(dl);
//		displayThread.start();
	}
	
	private void createGUI() {
		//JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("SerialServer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);
		
		JPanel panel = new JPanel();
		showTextArea = new JTextArea();
		showTextArea.setLineWrap(true);
		showTextArea.setEditable(false);
		ZHTTabbedPane tabPane = new ZHTChromeTabbedPane();
		
		
		JScrollPane showScrollPane = new JScrollPane(showTextArea);
		JScrollBar scrollBar = showScrollPane.getVerticalScrollBar();
		scrollBar.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
		
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				isScrollBarClicked = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				isScrollBarClicked = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}});
		
		inputTextArea = new JTextArea();
		inputTextArea.setLineWrap(true);
		inputTextArea.addKeyListener(new KeyListener() {
		    @Override
		    public void keyPressed(KeyEvent e) {
		    	int key = e.getKeyCode();
		    	if(key == '\n'){
		        	String line  = inputTextArea.getText();
		        	System.out.println(line); 
		        	for(char c:(line+"\n").toCharArray())
		        		currentSerialHandler.write(c);
		        }
		    }
		 
		    @Override
		    public void keyReleased(KeyEvent e) {
		    	int key = e.getKeyCode();
		        if(key == '\n'){
		        	inputTextArea.setText(null);
		        }
		    }
		 
		    @Override
		    public void keyTyped(KeyEvent e) {
		    	
		    }

		});
		JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
		
		Tab tab = new Tab(showScrollPane, true, null, "Test0");
		tabPane.addTab(tab);
		Tab tab1 = new Tab(new JPanel(), true, null, "Test1");
		tabPane.addTab(tab1);
		//tabPane.addTab("Test1", new JPanel());
		JPanel wtfPane = new JPanel();
		wtfPane.setLayout(new BorderLayout());
		wtfPane.add(tabPane, BorderLayout.CENTER);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, wtfPane, inputScrollPane);
		splitPane.setDividerSize(3);
		
		JPanel toolBoxPanel = new JPanel();
		GridLayout toolBoxGrid = new GridLayout(1, 6);
		toolBoxPanel.setLayout(toolBoxGrid);
		JButton connectButton = new JButton("Connect");
		connectButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				establishConnection();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}});
		JButton reConnectButton = new JButton("Reconnect");
		JButton disConnectButton = new JButton("Disconnect");
		JButton optonsButton = new JButton("Optons");
		toolBoxPanel.add(connectButton);
		toolBoxPanel.add(reConnectButton);
		toolBoxPanel.add(disConnectButton);
		toolBoxPanel.add(optonsButton);
		
		JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toolBoxPanel, splitPane);
		mainPane.setDividerSize(3);
		
		panel.setLayout(new BorderLayout());
		panel.add(mainPane);
		
		frame.add(panel);
		frame.setVisible(true);
		
		splitPane.setDividerLocation(0.7);
		mainPane.setDividerLocation(0.1);
	}
	
	private void establishConnection() {
		JFrame connectFrme = new JFrame();
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4,2));
		JLabel portLabel = new JLabel("Port:");
		JLabel buadrateLabel = new JLabel("Buadrate:");
		JLabel logLabel = new JLabel("Log:");
		JComboBox portBox = new JComboBox();
		JComboBox buadrateBox = new JComboBox();
		JTextField logPathField = new JTextField();
		
		panel.add(portLabel);
		panel.add(portBox);
		panel.add(buadrateLabel);
		panel.add(buadrateBox);
		panel.add(logLabel);
		panel.add(logPathField);
		
		connectFrme.setSize(200,300);
		connectFrme.add(panel);
		connectFrme.setVisible(true);
	}
	
	private class GUICreator implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			createGUI();
		}
		
	}
	
	private class DisplayLog implements Runnable{
		private SerialHandler sh;
		
		public DisplayLog(SerialHandler sh) {
			this.sh = sh;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				String line = sh.readLine(0);
				if(line!=null) {
					showTextArea.append(line);
					if(!isScrollBarClicked)
						showTextArea.setCaretPosition(showTextArea.getText().length());
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
