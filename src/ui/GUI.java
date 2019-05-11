package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

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
	private JTextField logPathField;
	private ZHTTabbedPane tabPane;
	
	private String serialPort;
	private int buadrate;
	private String logPath;
	
	private Map<String, SerialHandler> serialMap = new HashMap<String, SerialHandler>();
	
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
		tabPane = new ZHTChromeTabbedPane();
		
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
		
		
//		Tab tab = new Tab(new JPanel(), true, null, "1");
//		tabPane.addTab(tab);
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
				createConnectionFrame();
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
		JButton optonsButton = new JButton("Options");
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
	
	private void createConnectionFrame() {
		JFrame connectionFrame = new JFrame("Options");
		connectionFrame.setSize(400,300);
		connectionFrame.setVisible(true);
		connectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		connectionFrame.setLayout(gridBagLayout);
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill=GridBagConstraints.BOTH;
		
		JLabel portLabel = new JLabel("Port:");
		JLabel buadrateLabel = new JLabel("Buadrate:");
		JLabel logLabel = new JLabel("Log:");
		String[] serialPortList = {"cu.SLAB_USBtoUART"};
		JComboBox<String> portBox = new JComboBox<String>(serialPortList);
		Integer[] buadrateList = {57600, 115200};
		JComboBox<Integer> buadrateBox = new JComboBox<Integer>(buadrateList);
		logPathField = new JTextField();
		JButton openFileButton = new JButton("..");
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				connectionFrame.dispose();
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
		
		JButton confirmButton = new JButton("Confirm");
		confirmButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				serialPort = (String) portBox.getSelectedItem();
				buadrate = (int) buadrateBox.getSelectedItem();
				logPath = logPathField.getText();
				connectionFrame.dispose();
				new Thread(new SessionCreator(serialPort, buadrate, logPath)).start();
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
		
		gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        gridBagConstraints.gridwidth=1;                                             
        gridBagConstraints.gridheight=1;            
        gridBagLayout.setConstraints(portLabel, gridBagConstraints);
        
        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=0;
        gridBagConstraints.gridwidth=6;                                             
        gridBagConstraints.gridheight=1;            
        gridBagLayout.setConstraints(portBox, gridBagConstraints);
        
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=1;
        gridBagConstraints.gridwidth=1;                                             
        gridBagConstraints.gridheight=1;            
        gridBagLayout.setConstraints(buadrateLabel, gridBagConstraints);
        
        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=1;
        gridBagConstraints.gridwidth=6;                                             
        gridBagConstraints.gridheight=1;            
        gridBagLayout.setConstraints(buadrateBox, gridBagConstraints);
        
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=2;
        gridBagConstraints.gridwidth=1;                                             
        gridBagConstraints.gridheight=1;            
        gridBagLayout.setConstraints(logLabel, gridBagConstraints);
        
        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=2;
        gridBagConstraints.gridwidth=6;                                             
        gridBagConstraints.gridheight=1;            
        gridBagLayout.setConstraints(logPathField, gridBagConstraints);
        
        gridBagConstraints.gridx=7;
        gridBagConstraints.gridy=2;
        gridBagConstraints.gridwidth=1;                                             
        gridBagConstraints.gridheight=1;            
        gridBagLayout.setConstraints(openFileButton, gridBagConstraints);
        
        gridBagConstraints.gridx=5;
        gridBagConstraints.gridy=3;
        gridBagConstraints.gridwidth=1;                                             
        gridBagConstraints.gridheight=1;            
        gridBagLayout.setConstraints(cancelButton, gridBagConstraints);
        
        gridBagConstraints.gridx=6;
        gridBagConstraints.gridy=3;
        gridBagConstraints.gridwidth=1;                                             
        gridBagConstraints.gridheight=1;            
        gridBagLayout.setConstraints(confirmButton, gridBagConstraints);
        
        
        connectionFrame.add(portLabel);
        connectionFrame.add(portBox);
        connectionFrame.add(buadrateLabel);
        connectionFrame.add(buadrateBox);
        connectionFrame.add(logLabel);
        connectionFrame.add(logPathField);
        connectionFrame.add(openFileButton);
        connectionFrame.add(cancelButton);
        connectionFrame.add(confirmButton);
		
		openFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				FileDialog fileDialog = new FileDialog(connectionFrame);                 
				fileDialog.setVisible(true);
				String filePath = fileDialog.getDirectory();		
				String fileName = fileDialog.getFile();		
				if(filePath == null  || fileName == null){			
				}else{
					logPathField.setText(filePath + fileName);
				}
			}});
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
			while(!sh.isStop()) {
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
	
	private class SessionCreator implements Runnable{
		private String serialPort;
		private int buadrate;
		private String logPath;
		
		public SessionCreator(String serialPort, int buadrate, String logPath) {
			this.serialPort = serialPort;
			this.buadrate = buadrate;
			this.logPath = logPath;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			SerialHandler sh = new SerialHandler(serialPort, buadrate);
			serialMap.put(serialPort, sh);
			currentSerialHandler = sh;
			
			JScrollPane[] toDeleteArray = new JScrollPane[1];
			JScrollPane showScrollPane = new JScrollPane(showTextArea);
			toDeleteArray[0] = showScrollPane;
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
			
			Tab tab = new Tab(showScrollPane, true, null, serialPort);
			tab.addListener(new MyHandler() {

				@Override
				public void doHandler(RemoveTabEvent e) {
					// TODO Auto-generated method stub
					String serialPort = e.getTabName();
					SerialHandler toDelete = serialMap.get(serialPort);
					toDelete.setStop();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					toDeleteArray[0].setVisible(false);
					toDeleteArray[0] = null;
					tabPane.removeTab(tab);
					//shutdown
					serialMap.remove(serialPort);
				}});
			tabPane.addTab(tab);
			DisplayLog dl = new DisplayLog(sh);
			new Thread(dl).start();
		}
		
	}
	
	
}
