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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bl.LogRecorder;
import bl.SerialServer;
import data.SerialHandler;
import zht.tab.Tab;
import zht.tab.ZHTChromeTabbedPane;
import zht.tab.ZHTTabbedPane;

public class GUI {
	private final int MAX_LINE_COUNT = 1000;
	private JTextArea inputTextArea;
	private SerialHandler currentSerialHandler;
	private boolean isScrollBarClicked = false;
	private JButton reConnectButton;
	private JButton disConnectButton;
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
		frame.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
//		showTextArea = new JTextArea();
//		showTextArea.setLineWrap(true);
//		showTextArea.setEditable(false);
		tabPane = new ZHTChromeTabbedPane();
		tabPane.addListener(new MyHandler() {

			@Override
			public void removeTab(RemoveTabEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void selectTab(SelectTabEvent e) {
				// TODO Auto-generated method stub
				currentSerialHandler = serialMap.get(e.getTabName());
				setButtonStatus();
			}});
		
		inputTextArea = new JTextArea();
		inputTextArea.setLineWrap(true);
		inputTextArea.addKeyListener(new KeyListener() {
		    @Override
		    public void keyPressed(KeyEvent e) {
		    	int key = e.getKeyCode();
		    	if(key == '\n'){
		        	String line  = inputTextArea.getText();
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
			public void mouseClicked(MouseEvent e) {
				createConnectionFrame();
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		reConnectButton = new JButton("Reconnect");
		reConnectButton.setEnabled(false);
		disConnectButton = new JButton("Disconnect");
		disConnectButton.setEnabled(false);
		reConnectButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				currentSerialHandler.reconnect();
				setButtonStatus();
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		disConnectButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				currentSerialHandler.setStop();
				setButtonStatus();
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
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
	
	private void setButtonStatus() {
		if(currentSerialHandler.isStop()) {
			reConnectButton.setEnabled(true);
			disConnectButton.setEnabled(false);
		}else {
			reConnectButton.setEnabled(false);
			disConnectButton.setEnabled(true);
		}
	}
	
	private void createConnectionFrame() {
		JFrame connectionFrame = new JFrame("Options");
		connectionFrame.setSize(400,300);
		connectionFrame.setVisible(true);
		connectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connectionFrame.setLocationRelativeTo(null);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		connectionFrame.setLayout(gridBagLayout);
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill=GridBagConstraints.BOTH;
		
		JLabel portLabel = new JLabel("Port:");
		JLabel buadrateLabel = new JLabel("Buadrate:");
		JLabel logLabel = new JLabel("Log:");
		String[] serialPortList = getSerialPortList();
		JComboBox<String> portBox = new JComboBox<String>(serialPortList);
		Integer[] buadrateList = {57600, 115200};
		JComboBox<Integer> buadrateBox = new JComboBox<Integer>(buadrateList);
		logPathField = new JTextField();
		JButton openFileButton = new JButton("..");
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				connectionFrame.dispose();
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		JButton confirmButton = new JButton("Confirm");
		confirmButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				serialPort = (String) portBox.getSelectedItem();
				buadrate = (int) buadrateBox.getSelectedItem();
				logPath = logPathField.getText();
				if(serialMap.containsKey(serialPort)) {
					JOptionPane.showMessageDialog(null, "请勿重复添加！", "Attention", JOptionPane.ERROR_MESSAGE);
				}else {
					connectionFrame.dispose();
//					SerialHandler sh = new SerialHandler(serialPort, buadrate);
//					SwingUtilities.invokeLater(new SessionCreator(sh));
					SwingUtilities.invokeLater(new SessionCreator(serialPort, buadrate, logPath));
//					new Thread(new SessionCreator(serialPort, buadrate, logPath)).start();
				}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
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
	
	public String[] getSerialPortList() {
		Process p = null;
        List<String> result = new ArrayList<String>();
        try {
        	//according to os type
            p = new ProcessBuilder("ls", "/dev").start();
        } catch (Exception e) {
            return null;
        }
        //读取进程输出值
        InputStream inputStream = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String s = "";
        try {
            while ((s = br.readLine()) != null) {
            	if(s.startsWith("cu"))
                	result.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toArray(result);
	}
	
	public String[] toArray(List<String> lst) {
		String[] result = new String[lst.size()];
		int i = 0;
		for(String s:lst) {
			result[i] = s;
			i++;
		}
		return result;
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
		private JTextArea showTextArea;
		
		public DisplayLog(SerialHandler sh, JTextArea showTextArea) {
			this.sh = sh;
			this.showTextArea = showTextArea;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			showTextArea.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void insertUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(showTextArea.getLineCount()>=MAX_LINE_COUNT) {
								int end = 0;
								try {
									end = showTextArea.getLineEndOffset(MAX_LINE_COUNT/10);
								} catch (Exception e) {
								}
								showTextArea.replaceRange("", 0, end);
							}
						}});
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub
					
				}});
			while(true) {
				if(!sh.isStop()) {
					String line = sh.readLine(0);
					if(line!=null) {
						showTextArea.append(line);
						if(!isScrollBarClicked)
							showTextArea.setCaretPosition(showTextArea.getText().length());
					}
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
		private SerialHandler sh;
		
		public SessionCreator(String serialPort, int buadrate, String logPath) {
			this.serialPort = serialPort;
			this.buadrate = buadrate;
			this.logPath = logPath;
			this.sh = new SerialHandler(serialPort, buadrate);
			serialMap.put(sh.getSerialName(), sh);
			currentSerialHandler = sh;
		}

		@Override
		public void run() {
			reConnectButton.setEnabled(false);
			disConnectButton.setEnabled(true);
			JTextArea showTextArea = new JTextArea();	
			showTextArea = new JTextArea();
			showTextArea.setLineWrap(true);
			showTextArea.setEditable(false);
			JScrollPane[] toDeleteArray = new JScrollPane[1];
			JScrollPane showScrollPane = new JScrollPane(showTextArea);
			toDeleteArray[0] = showScrollPane;
			JScrollBar scrollBar = showScrollPane.getVerticalScrollBar();
			scrollBar.addMouseListener(new MouseListener() { 
				public void mouseClicked(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {
					isScrollBarClicked = true;
				}
				public void mouseReleased(MouseEvent e) {
					isScrollBarClicked = false;
				}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
			
			Tab tab = new Tab(showScrollPane, true, null, sh.getSerialName());
			tab.addListener(new MyHandler() {
				public void removeTab(RemoveTabEvent e) {
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
				}

				@Override
				public void selectTab(SelectTabEvent e) {
					// TODO Auto-generated method stub
					
				}});
			tabPane.addTab(tab);
			DisplayLog dl = new DisplayLog(sh, showTextArea);
			new Thread(dl).start();
		}	
	}
}
