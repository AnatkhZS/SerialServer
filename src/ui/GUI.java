package ui;

import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import bl.ConfigHandler;
import bl.SerialServer;
import bl.TestbedClient;
import bl.session.SerialSession;
import bl.session.Session;           
import bl.session.SessionEvent;
import bl.session.SessionEventHandler;
import bl.session.SessionManager;
import info.clearthought.layout.TableLayout;
import ui.tab.tab.Tab;
import ui.tab.event.MyHandler;
import ui.tab.event.RemoveTabEvent;
import ui.tab.event.SelectTabEvent;
import ui.tab.tab.ChromeTabbedPane;
import ui.tab.tab.TabbedPane;

public class GUI {
	private final int MAX_LINE_COUNT = 200;
	private final String CONFIG_PATH = "config.json";
	private SerialServer serialServer;
	private RecordTextArea inputTextArea;
	private Session currentSession;
	private ConfigHandler configHandler;
	private boolean isScrollBarClicked = false;
	private JButton reConnectButton;
	private JButton disConnectButton;
	private JButton optionsButton;
	private JTextField logPathField = new JTextField();
	private TabbedPane tabPane;
	private JLabel statusLabel;
	private JFrame mainFrame;
	private JTextField hostTextField = null;
	private JTextField portTextField = null;
	private boolean needListenTestbedConnection = true;
	private Thread testbedConnectionListener = null;
	private int formatStringLength = 40;
	private JButton connectButton = null;
	private JButton disconnectButton = null;
	
	private String serialPort;
	private int buadrate;
	private String logPath;
	
	private SessionManager sessionManager = SessionManager.getSessionManager();
	private TestbedClient testbedClient;
	
	public static void main(String args[]) {
		new GUI().run();
	}
	
	private void run() {
		sessionManager.addListener(new SessionEventHandler() {
			public void handle(SessionEvent e) {
				setButtonStatus();
				if(testbedClient!=null)
					testbedClient.update();
			}});
		configHandler = new ConfigHandler(CONFIG_PATH);
		serialServer = new SerialServer();
		serialServer.createServer();
		GUICreator c = new GUICreator();
		Thread guiThread = new Thread(c);
		guiThread.start();
	}
	
	private void createGUI() {
//		JFrame.setDefaultLookAndFeelDecorated(true);
		mainFrame = new JFrame("SerialServer-Disconnected");
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					configHandler.save();
					serialServer.destroyServer();
				}catch(Exception e1) {}
				System.exit(0);
			}
		});
		mainFrame.setSize(900, 600);
		mainFrame.setLocationRelativeTo(null);
		
		JMenuBar menubar = new JMenuBar();
		mainFrame.setJMenuBar(menubar);
		JMenu networkMenu = new JMenu("Network");
		menubar.add(networkMenu);
		JMenuItem connectItem = new JMenuItem("Connect");
		networkMenu.add(connectItem);
		connectItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNetworkFrame();
			}});
		
		JPanel panel = new JPanel();
		tabPane = new ChromeTabbedPane();
		tabPane.addListener(new MyHandler() {
			public void removeTab(RemoveTabEvent e) {}
			public void selectTab(SelectTabEvent e) {
				currentSession = sessionManager.getSession(e.getSessionId());
				setButtonStatus();
			}});
		
		inputTextArea = new RecordTextArea();
		inputTextArea.setLineWrap(true);
		inputTextArea.addKeyListener(new KeyListener() {
		    public void keyPressed(KeyEvent e) {
		    	int keyCode = e.getKeyCode();
		    	if(keyCode == '\n'){
		        	String line  = inputTextArea.getText();
		        	if(line.length() > 0) {
		        		inputTextArea.push(line);
		        	}
		        	if(currentSession!=null) {
		        		currentSession.writeStr(line+"\n");
//		        		for(char c:(line+"\n").toCharArray())
//		        			currentSession.write(c);
		        	}
		        }else if(keyCode == KeyEvent.VK_UP) {
		        	inputTextArea.setText(inputTextArea.backward());
		        }else if(keyCode == KeyEvent.VK_DOWN) {
		        	inputTextArea.setText(inputTextArea.forward());
		        }
		    }
		    public void keyReleased(KeyEvent e) {
		    	int key = e.getKeyCode();
		        if(key == '\n'){
		        	inputTextArea.setText(null);
		        }
		    }
		    public void keyTyped(KeyEvent e) {}
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
		//set transparent
		connectButton.setContentAreaFilled(false); 
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
		reConnectButton.setContentAreaFilled(false);
		reConnectButton.setEnabled(false);
		disConnectButton = new JButton("Disconnect");
		disConnectButton.setContentAreaFilled(false);
		disConnectButton.setEnabled(false);
		reConnectButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if(reConnectButton.isEnabled()) {
					if(currentSession.isStop()) {
						String serialPort = ((SerialSession)currentSession).getSerialPort();
						int buadrate = ((SerialSession)currentSession).getBuadrate();
						String logPath = currentSession.getLogPath();
						boolean isRecord = currentSession.isRecord();
						boolean isStartAtMidnight = currentSession.isStartAtMidnight();
						boolean isAppendToFile = currentSession.isAppendToFile();
						tabPane.removeTab(serialPort);
						sessionManager.destroySession(currentSession.getSesionId());
						
						int sessionId = sessionManager.createSession(serialPort, buadrate, logPath, isRecord, isStartAtMidnight, isAppendToFile);
						SerialSession ss = (SerialSession)sessionManager.getSession(sessionId);
						SwingUtilities.invokeLater(new SessionCreator(ss));
						
					}else {
						currentSession.reconnect();
					}
					setButtonStatus();
				}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		disConnectButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if(disConnectButton.isEnabled()) {
					currentSession.setStop();
					setButtonStatus();
				}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		optionsButton = new JButton("Options");
		optionsButton.setContentAreaFilled(false);
		optionsButton.setEnabled(false);
		optionsButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if(optionsButton.isEnabled())
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
		toolBoxPanel.add(optionsButton);
		
		JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toolBoxPanel, splitPane);
		mainPane.setDividerSize(3);
		
		panel.setLayout(new BorderLayout());
		panel.add(mainPane);
		
		mainFrame.add(panel);
		mainFrame.setVisible(true);
		
		splitPane.setDividerLocation(0.7);
		mainPane.setDividerLocation(0.1);
	}
	
	private void setButtonStatus() {
		if(sessionManager.size()>0) {
			optionsButton.setEnabled(true);
			if(currentSession!=null) {
				if(currentSession.isStop()) {
					reConnectButton.setEnabled(true);
					disConnectButton.setEnabled(false);
				}else {
					reConnectButton.setEnabled(false);
					disConnectButton.setEnabled(true);
				}
			}
		}else{
			reConnectButton.setEnabled(false);
			disConnectButton.setEnabled(false);
			optionsButton.setEnabled(false);
		}
	}
	
	private String formatString(String content, int length) {
		content = " "+content;
		while(content.length()<length) {
			content = content+" ";
		}
		return content;
	}
	
	private boolean isTestbedConnected() {
		if(testbedClient==null) {
			return false;
		}
		return testbedClient.isConnected();
	}
	
	private void updateConnectionStatus() {
		if(isTestbedConnected()) {
			statusLabel.setText(formatString("Connected", formatStringLength));
			statusLabel.setForeground(Color.BLACK);
			mainFrame.setTitle("SerialServer-Connected");
			disconnectButton.setEnabled(true);
			connectButton.setEnabled(false);
		}else {
			statusLabel.setText(formatString("Disconnected", formatStringLength));
			statusLabel.setForeground(Color.RED);
			mainFrame.setTitle("SerialServer-Disconnected");
			disconnectButton.setEnabled(false);
			connectButton.setEnabled(true);
		}
	}
	
	private void createNetworkFrame() {
		JFrame networkFrame = new JFrame("Network");
		networkFrame.setSize(480,180);
		networkFrame.setVisible(true);
		networkFrame.setLocationRelativeTo(null);
		double size[][] = {{TableLayout.FILL}, {0.8, 0.2}};
		TableLayout networkFrameLayout = new TableLayout(size);
		networkFrame.setLayout(networkFrameLayout);
		
		JPanel infoPanel = new JPanel();
		GridBagLayout infoPanelLayout = new GridBagLayout();
		infoPanel.setLayout(infoPanelLayout);
		GridBagConstraints infoConstraints = new GridBagConstraints();
		infoConstraints.fill = GridBagConstraints.BOTH;
		{
			JLabel statusLabel = new JLabel("Status: ", JLabel.RIGHT);
			infoConstraints.gridx=0;
			infoConstraints.gridy=0;
			infoConstraints.gridwidth=2;                                             
			infoConstraints.gridheight=1;            
			infoPanelLayout.setConstraints(statusLabel, infoConstraints);
			infoPanel.add(statusLabel);
		}
		{
			statusLabel = new JLabel();
			infoConstraints.gridx=2;
			infoConstraints.gridy=0;
			infoConstraints.gridwidth=12;                                             
			infoConstraints.gridheight=1;            
			infoPanelLayout.setConstraints(statusLabel, infoConstraints);
			infoPanel.add(statusLabel);
		}
		{
			JLabel hostLabel = new JLabel("Host: ", JLabel.RIGHT);
			infoConstraints.gridx=0;
			infoConstraints.gridy=1;
			infoConstraints.gridwidth=2;                                             
			infoConstraints.gridheight=1;            
			infoPanelLayout.setConstraints(hostLabel, infoConstraints);
			infoPanel.add(hostLabel);
		}
		{
			hostTextField = new JTextField("172.16.209.57");
			infoConstraints.gridx=2;
			infoConstraints.gridy=1;
			infoConstraints.gridwidth=12;                                             
			infoConstraints.gridheight=1;            
			infoPanelLayout.setConstraints(hostTextField, infoConstraints);
			infoPanel.add(hostTextField);
		}
		{
			JLabel portLabel = new JLabel("Port: ", JLabel.RIGHT);
			infoConstraints.gridx=0;
			infoConstraints.gridy=2;
			infoConstraints.gridwidth=2;                                             
			infoConstraints.gridheight=1;            
			infoPanelLayout.setConstraints(portLabel, infoConstraints);
			infoPanel.add(portLabel);
		}
		{
			portTextField = new JTextField("54321");
			infoConstraints.gridx=2;
			infoConstraints.gridy=2;
			infoConstraints.gridwidth=12;                                             
			infoConstraints.gridheight=1;            
			infoPanelLayout.setConstraints(portTextField, infoConstraints);
			infoPanel.add(portTextField);
		}
		
		JPanel confirmPanel = new JPanel();
		double size2[][] = {{0.2, 0.2, 0.2, 0.2, 0.2}, {TableLayout.FILL}};
		TableLayout comfirmTableLayout = new TableLayout(size2);
		confirmPanel.setLayout(comfirmTableLayout);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				networkFrame.dispose();
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {} 
			public void mouseExited(MouseEvent e) {}
		});
		disconnectButton = new JButton("Disconnect");
		disconnectButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				testbedClient.disconnect();
				needListenTestbedConnection = false;
				try {
					testbedConnectionListener.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {} 
			public void mouseExited(MouseEvent e) {}
		});
		connectButton = new JButton("Connect");
		connectButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				String host = hostTextField.getText();
				int port = Integer.valueOf(portTextField.getText());
				testbedClient = new TestbedClient(host, port);
				testbedClient.start();
				sessionManager.setTestbedClient(testbedClient);
				new Thread(new Runnable() {
					public void run() {
						statusLabel.setText(formatString("connecting...", formatStringLength));
						statusLabel.setForeground(Color.BLACK);
					}
				}).start();
				needListenTestbedConnection = true;
				testbedConnectionListener = new Thread(new TestbedConnectionListener());
				testbedConnectionListener.start();
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {} 
			public void mouseExited(MouseEvent e) {}
		});
		updateConnectionStatus();
		confirmPanel.add(cancelButton, "2, 0");
		confirmPanel.add(disconnectButton, "3, 0");
		confirmPanel.add(connectButton, "4, 0");
		
		networkFrame.add(infoPanel, "0, 0");
		networkFrame.add(confirmPanel, "0, 1");
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
		String[] serialPortList = SessionManager.getSerialPortList();
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
			String serialPort = ((SerialSession)currentSession).getSerialPort();
			int buadrate = ((SerialSession)currentSession).getBuadrate();
			String logPath = currentSession.getLogPath();
			boolean isRecord = currentSession.isRecord();
			boolean isStartAtMidnight = currentSession.isStartAtMidnight();
			boolean isAppendToFile = currentSession.isAppendToFile();
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
					if(sessionManager.contains(serialPort)) {
						JOptionPane.showMessageDialog(null, "Can't add port repeatly!", "Attention", JOptionPane.ERROR_MESSAGE);
					}else if(isStartAtMidnight && !logPath.contains("%D")) {
						JOptionPane.showMessageDialog(null, "Use %D in your log path!", "Attention", JOptionPane.ERROR_MESSAGE);
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
						int sessionId = sessionManager.createSession(serialPort, buadrate, logPath, isRecord, isStartAtMidnight, isAppendToFile);
						SerialSession ss = (SerialSession)sessionManager.getSession(sessionId);
						SwingUtilities.invokeLater(new SessionCreator(ss));
					}
				}else {
					if(sessionManager.contains(serialPort) && !serialPort.equals(currentSession.getName())) {
						JOptionPane.showMessageDialog(null, "Can't add port repeatly!", "Attention", JOptionPane.ERROR_MESSAGE);
					}else if(isStartAtMidnight && !logPath.contains("%D")) {
						JOptionPane.showMessageDialog(null, "Use %D in your log path!", "Attention", JOptionPane.ERROR_MESSAGE);
					}else {
						String currentSerialPort = currentSession.getName();
						int currentSessionId = currentSession.getSesionId();
						sessionManager.getSession(currentSessionId).setStop();
						sessionManager.destroySession(currentSessionId);
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
						int sessionId = sessionManager.createSession(serialPort, buadrate, logPath, isRecord, isStartAtMidnight, isAppendToFile);
						SerialSession ss = (SerialSession)sessionManager.getSession(sessionId);
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
	
	private class GUICreator implements Runnable{

		@Override
		public void run() {
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
			showTextArea.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void insertUpdate(DocumentEvent e) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
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
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
				}});
			while(true) {
				if(!serialSession.isStop()) {
					String line = serialSession.readLine(0);
					if(line!=null) {
						showTextArea.append(line);
						if(!isScrollBarClicked)
							showTextArea.setCaretPosition(showTextArea.getDocument().getLength());
//							showTextArea.setCaretPosition(showTextArea.getText().length());
					}
				}else {
					setButtonStatus();
					break;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class SessionCreator implements Runnable{
		private SerialSession serialSession;
		
		public SessionCreator(SerialSession serialSession) {
			this.serialSession = serialSession;
			currentSession = serialSession;
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
			
			Tab tab = new Tab(showScrollPane, true, null, serialSession.getSesionId(), serialSession.getSerialPort());
			tab.addListener(new MyHandler() {
				public void removeTab(RemoveTabEvent e) {
					int sessionId = e.getSessionId();
					try {
						sessionManager.getSession(sessionId).setStop();
						Thread.sleep(100);
						toDeleteArray[0].setVisible(false);
						toDeleteArray[0] = null;
						tabPane.removeTab(serialSession.getSerialPort());
						sessionManager.destroySession(sessionId);
						Tab currentTab = tabPane.getSelectedTab();
						currentSession = sessionManager.getSession(currentTab.getId());
					} catch(java.lang.NullPointerException e1) {
						System.out.println("ERROR, Map size: " + sessionManager.size());
					}catch (InterruptedException e2) {
						e2.printStackTrace();
					}
				}

				@Override
				public void selectTab(SelectTabEvent e) {
					int sessionId = e.getSessionId();
					currentSession = sessionManager.getSession(sessionId);
				}});
			tabPane.addTab(tab);
			DisplayLog dl = new DisplayLog(serialSession, showTextArea);
			new Thread(dl).start();
		}	
	}
	
	private class TestbedConnectionListener implements Runnable {
		public void run() {
			while(needListenTestbedConnection) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				updateConnectionStatus();
			}
			updateConnectionStatus();
		}
	}
}
