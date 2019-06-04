package ui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bl.ConfigHandler;
import bl.LogRecorder;
import bl.SerialSession;
import info.clearthought.layout.TableLayout;
import zht.tab.Tab;
import zht.tab.ZHTChromeTabbedPane;
import zht.tab.ZHTTabbedPane;

public class GUI {
	private final int MAX_LINE_COUNT = 1000;
	private int SERVER_PORT = 9428;
	private String CONFIG_PATH = "config.json";
	private String REQUEST_SPLITER = "@";
	private DatagramSocket server;
	private RecordTextArea inputTextArea;
	private SerialSession currentSerialSession;
	private ConfigHandler configHandler;
	private boolean isScrollBarClicked = false;
	private JButton reConnectButton;
	private JButton disConnectButton;
	private JTextField logPathField = new JTextField();
	private ZHTTabbedPane tabPane;
	
	private String serialPort;
	private int buadrate;
	private String logPath;
	
	private Map<String, SerialSession> serialMap = new HashMap<String, SerialSession>();
	
	public static void main(String args[]) {
		new GUI().run();
	}
	
	private void run() {
		
//		SerialHandler sh = new SerialHandler("cu.SLAB_USBtoUART", 115200);
//		currentSerialHandler = sh;
		configHandler = new ConfigHandler(CONFIG_PATH);
		GUICreator c = new GUICreator();
		Thread guiThread = new Thread(c);
		guiThread.start();
		Server s = new Server();
		Thread serverThread = new Thread(s);
		serverThread.start();
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
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				configHandler.save();
				server.close();
				System.exit(0);
			}
		});
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
				currentSerialSession = serialMap.get(e.getTabName());
				setButtonStatus();
			}});
		
		inputTextArea = new RecordTextArea();
		inputTextArea.setLineWrap(true);
		inputTextArea.addKeyListener(new KeyListener() {
		    @Override
		    public void keyPressed(KeyEvent e) {
		    	int keyCode = e.getKeyCode();
		    	if(keyCode == '\n'){
		        	String line  = inputTextArea.getText();
		        	if(line.length() > 0) {
		        		inputTextArea.push(line);
		        	}
		        	if(currentSerialSession!=null) {
		        		for(char c:(line+"\n").toCharArray())
		        			currentSerialSession.write(c);
		        	}
		        }else if(keyCode == KeyEvent.VK_UP) {
		        	inputTextArea.setText(inputTextArea.backward());
		        }else if(keyCode == KeyEvent.VK_DOWN) {
		        	inputTextArea.setText(inputTextArea.forward());
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
				createConnectionFrame(true);
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
				currentSerialSession.reconnect();
				setButtonStatus();
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		disConnectButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				currentSerialSession.setStop();
				setButtonStatus();
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		JButton optonsButton = new JButton("Options");
		optonsButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				createConnectionFrame(false);
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
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
		if(currentSerialSession.isStop()) {
			reConnectButton.setEnabled(true);
			disConnectButton.setEnabled(false);
		}else {
			reConnectButton.setEnabled(false);
			disConnectButton.setEnabled(true);
		}
	}
	
	private void createConnectionFrame(boolean isConnect) {
		JFrame connectionFrame = new JFrame("Options");
		connectionFrame.setSize(420,300);
		connectionFrame.setVisible(true);
//		connectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connectionFrame.setLocationRelativeTo(null);
		double size[][] = {{TableLayout.FILL}, {0.3, 0.4, 0.4}};
		TableLayout mainTableLayout = new TableLayout(size);
		connectionFrame.setLayout(mainTableLayout);

		JPanel serialSettingPanel = new JPanel();
		serialSettingPanel.setBorder(BorderFactory.createTitledBorder("Serial Setting"));
		GridBagLayout serialSettingLayout = new GridBagLayout();
		GridBagConstraints serialSettingConstraints = new GridBagConstraints();
		serialSettingConstraints.fill=GridBagConstraints.BOTH;
		serialSettingPanel.setLayout(serialSettingLayout);
		JComboBox<String> portBox;
		String[] serialPortList = getSerialPortList();
		Integer[] buadrateList = {57600, 115200};
		JComboBox<Integer> buadrateBox = new JComboBox<Integer>(buadrateList);
		JCheckBox startOnConnectCheckBox = new JCheckBox("Start log upon connect");
		JCheckBox newLogAtMidnightCheckBox = new JCheckBox("Start new log at midnight(use %D)");
		JRadioButton overwriteRadioButton = new JRadioButton("Overwrite file");
		JRadioButton appendRadioButton = new JRadioButton("Append to file");
		appendRadioButton.setSelected(true);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(overwriteRadioButton);
		buttonGroup.add(appendRadioButton);
		{
			JLabel portLabel = new JLabel("Port:");
			serialSettingConstraints.gridx=0;
	        serialSettingConstraints.gridy=0;
	        serialSettingConstraints.gridwidth=2;                                             
	        serialSettingConstraints.gridheight=1;            
	        serialSettingLayout.setConstraints(portLabel, serialSettingConstraints);
	        serialSettingPanel.add(portLabel);
		}
		{
			portBox = new JComboBox<String>(serialPortList);
			portBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						String serialPort = (String)e.getItem();
						if(configHandler.contain(serialPort)) {
							int buadrate = (int)configHandler.query(serialPort, "buadrate");
							String logPath = (String)configHandler.query(serialPort, "logPath");
							boolean isRecord = (boolean)configHandler.query(serialPort, "isRecord");
							boolean isStartAtMidnight = (boolean)configHandler.query(serialPort, "isStartAtMidnight");
							boolean isAppendToFile = (boolean)configHandler.query(serialPort, "isAppendToFile");
							buadrateBox.setSelectedIndex(index(buadrateList, buadrate));
							logPathField.setText(logPath);
							startOnConnectCheckBox.setSelected(isRecord);
							newLogAtMidnightCheckBox.setSelected(isStartAtMidnight);
							if(isAppendToFile)
								appendRadioButton.setSelected(true);
							else
								overwriteRadioButton.setSelected(true);
						}else {
							buadrateBox.setSelectedIndex(0);
							logPathField.setText("");
							startOnConnectCheckBox.setSelected(false);
							newLogAtMidnightCheckBox.setSelected(false);
							appendRadioButton.setSelected(true);
						}
					}
				}});
			serialSettingConstraints.gridx=2;
	        serialSettingConstraints.gridy=0;
	        serialSettingConstraints.gridwidth=12;                                             
	        serialSettingConstraints.gridheight=1;            
	        serialSettingLayout.setConstraints(portBox, serialSettingConstraints);
	        serialSettingPanel.add(portBox);
		}
		{
			JLabel buadrateLabel = new JLabel("Buadrate:");
			serialSettingConstraints.gridx=0;
	        serialSettingConstraints.gridy=1;
	        serialSettingConstraints.gridwidth=2;                                             
	        serialSettingConstraints.gridheight=1;            
	        serialSettingLayout.setConstraints(buadrateLabel, serialSettingConstraints);
	        serialSettingPanel.add(buadrateLabel);
		}
		{
			serialSettingConstraints.gridx=2;
	        serialSettingConstraints.gridy=1;
	        serialSettingConstraints.gridwidth=12;                                             
	        serialSettingConstraints.gridheight=1;            
	        serialSettingLayout.setConstraints(buadrateBox, serialSettingConstraints);
	        serialSettingPanel.add(buadrateBox);
		}

		JPanel logSettingPanel = new JPanel();
		logSettingPanel.setBorder(BorderFactory.createTitledBorder("Log Setting"));
		double size1[][] = {{0.1, 0.4, 0.2, 0.2, 0.1}, {0.3, 0.3, 0.3, TableLayout.FILL}};
		TableLayout tableLayout = new TableLayout(size1);
		logSettingPanel.setLayout(tableLayout);
		JLabel logLabel = new JLabel("Log:");
		JButton openFileButton = new JButton("..");
		openFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDialog fileDialog = new FileDialog(connectionFrame);                 
				fileDialog.setVisible(true);
				String filePath = fileDialog.getDirectory();		
				String fileName = fileDialog.getFile();		
				if(filePath == null  || fileName == null){			
				}else{
					logPathField.setText(filePath + fileName);
				}
			}});

		logSettingPanel.add(logLabel, "0, 0");
		logSettingPanel.add(logPathField, "1, 0, 3, 0");
		logSettingPanel.add(openFileButton, "4, 0, c, c");
		logSettingPanel.add(startOnConnectCheckBox, "0, 1, 1, 1");
		logSettingPanel.add(overwriteRadioButton, "3, 1, 4, 1");
		logSettingPanel.add(newLogAtMidnightCheckBox, "0, 2, 2, 2");
		logSettingPanel.add(appendRadioButton, "3, 2, 4, 2");
		
		JPanel confirmPanel = new JPanel();
		double size3[][] = {{0.2, 0.2, 0.2, 0.2, 0.2}, {TableLayout.FILL, 0.3, 0.3, 0.3}};
		TableLayout comfirmTableLayout = new TableLayout(size3);
		confirmPanel.setLayout(comfirmTableLayout);
		
		if(!isConnect) {
			String serialPort = currentSerialSession.getSerialPort();
			int buadrate = currentSerialSession.getBuadrate();
			String logPath = currentSerialSession.getLogPath();
			boolean isRecord = currentSerialSession.isRecord();
			boolean isStartAtMidnight = currentSerialSession.isStartAtMidnight();
			boolean isAppendToFile = currentSerialSession.isAppendToFile();
			portBox.setSelectedIndex(index(serialPortList, serialPort));
			buadrateBox.setSelectedIndex(index(buadrateList, buadrate));
			logPathField.setText(logPath);
			startOnConnectCheckBox.setSelected(isRecord);
			newLogAtMidnightCheckBox.setSelected(isStartAtMidnight);
			if(isAppendToFile)
				appendRadioButton.setSelected(true);
			else
				overwriteRadioButton.setSelected(true);
		}
		
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
				boolean isRecord = startOnConnectCheckBox.isSelected();
				boolean isStartAtMidnight = newLogAtMidnightCheckBox.isSelected();
				boolean isAppendToFile = appendRadioButton.isSelected();
				if(isConnect) {
					if(serialMap.containsKey(serialPort)) {
						JOptionPane.showMessageDialog(null, "请勿重复添加！", "Attention", JOptionPane.ERROR_MESSAGE);
					}else if(isStartAtMidnight && !logPath.contains("%D")) {
						JOptionPane.showMessageDialog(null, "文件名必须包含\"%D\"！", "Attention", JOptionPane.ERROR_MESSAGE);
					}else {
						if(!logPath.equals("") && logPath!=null && !logPath.endsWith(".log")){
							logPath = logPath+".log";
						}
						if(!configHandler.contain(serialPort))
							configHandler.addSerial(serialPort);
						configHandler.setValue(serialPort, "name", serialPort);
						configHandler.setValue(serialPort, "buadrate", buadrate);
						configHandler.setValue(serialPort, "logPath", logPath);
						configHandler.setValue(serialPort, "isRecord", isRecord);
						configHandler.setValue(serialPort, "isStartAtMidnight", isStartAtMidnight);
						configHandler.setValue(serialPort, "isAppendToFile", isAppendToFile);
						connectionFrame.dispose();
						SerialSession ss = new SerialSession(serialPort, buadrate, logPath, isRecord, isStartAtMidnight, isAppendToFile);
						SwingUtilities.invokeLater(new SessionCreator(ss));
					}
				}else {
					if(serialMap.containsKey(serialPort) && !serialPort.equals(currentSerialSession.getSerialPort())) {
						JOptionPane.showMessageDialog(null, "请勿重复添加！", "Attention", JOptionPane.ERROR_MESSAGE);
					}else if(isStartAtMidnight && !logPath.contains("%D")) {
						JOptionPane.showMessageDialog(null, "文件名必须包含\"%D\"！", "Attention", JOptionPane.ERROR_MESSAGE);
					}else {
						String currentSerialPort = currentSerialSession.getSerialPort();
						serialMap.get(currentSerialPort).setStop();
						serialMap.remove(currentSerialPort);
						//TODO remove ui
						tabPane.removeTab(currentSerialPort);
						if(!logPath.equals("") && logPath!=null && !logPath.endsWith(".log")){
							logPath = logPath+".log";
						}
						if(!configHandler.contain(serialPort))
							configHandler.addSerial(serialPort);
						configHandler.setValue(serialPort, "name", serialPort);
						configHandler.setValue(serialPort, "buadrate", buadrate);
						configHandler.setValue(serialPort, "logPath", logPath);
						configHandler.setValue(serialPort, "isRecord", isRecord);
						configHandler.setValue(serialPort, "isStartAtMidnight", isStartAtMidnight);
						configHandler.setValue(serialPort, "isAppendToFile", isAppendToFile);
						connectionFrame.dispose();
						SerialSession ss = new SerialSession(serialPort, buadrate, logPath, isRecord, isStartAtMidnight, isAppendToFile);
						SwingUtilities.invokeLater(new SessionCreator(ss));
					}
				}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		confirmPanel.add(cancelButton, "3, 2");
		confirmPanel.add(confirmButton, "4, 2");
		
        connectionFrame.add(serialSettingPanel, "0, 0");
        connectionFrame.add(logSettingPanel, "0, 1");
        connectionFrame.add(confirmPanel, "0, 2");
	}
	
	private int index(Object[] lst, Object value) {
		for(int i=0;i<lst.length;i++) {
			if(lst[i].equals(value)) {
				return i;
			}
		}
		return -1;
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
		private SerialSession serialSession;
		private JTextArea showTextArea;
		
		public DisplayLog(SerialSession serialSession, JTextArea showTextArea) {
			this.serialSession = serialSession;
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
				if(!serialSession.isStop()) {
					String line = serialSession.readLine(0);
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
		private SerialSession serialSession;
		
		public SessionCreator(SerialSession serialSession) {
			this.serialSession = serialSession;
			serialMap.put(serialSession.getSerialPort(), serialSession);
			currentSerialSession = serialSession;
		}

		@Override
		public void run() {
			reConnectButton.setEnabled(false);
			disConnectButton.setEnabled(true);
			RecordTextArea showTextArea = new RecordTextArea();	
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
			
			Tab tab = new Tab(showScrollPane, true, null, serialSession.getSerialPort());
			tab.addListener(new MyHandler() {
				public void removeTab(RemoveTabEvent e) {
					String serialPort = e.getTabName();
					try {
						serialMap.get(serialPort).setStop();
						Thread.sleep(100);
					} catch(java.lang.NullPointerException e1) {
						System.out.println("Map size: " + serialMap.size());
					}catch (InterruptedException e2) {
						e2.printStackTrace();
					}
					toDeleteArray[0].setVisible(false);
					toDeleteArray[0] = null;
					tabPane.removeTab(serialSession.getSerialPort());
					//shutdown
					serialMap.remove(serialPort);
				}

				@Override
				public void selectTab(SelectTabEvent e) {
					// TODO Auto-generated method stub
					String serialPort = e.getTabName();
					currentSerialSession = serialMap.get(serialPort);
				}});
			tabPane.addTab(tab);
			DisplayLog dl = new DisplayLog(serialSession, showTextArea);
			new Thread(dl).start();
			//start log thread
			if(serialSession.isRecord()) {
				LogRecorder lr = new LogRecorder(serialSession);
				lr.startRecord();
			}
		}	
	}
	
	private class Server implements Runnable{

		@Override
		public void run() {
			try {
				server = new DatagramSocket(SERVER_PORT);
				while(true) {
					byte[] container = new byte[1024];
					DatagramPacket packet = new DatagramPacket(container, container.length);
					try {
						server.receive(packet);
					}catch(java.net.SocketException e) {}
					byte[] data = packet.getData();
					int len = packet.getLength();
					String request = new String(data, 0, len);
					//request format: "port@cmd"
					System.out.println(request);
					String portName = request.split(REQUEST_SPLITER)[0];
					String cmd = request.split(REQUEST_SPLITER)[1];
					SerialSession ss = serialMap.get(portName);
					if(ss != null) {
						for(char c:cmd.toCharArray())
							ss.write(c);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
