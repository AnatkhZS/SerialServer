package ui;

import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

public class ConnectionFrame extends JFrame implements ActionListener{
	private ConnectionFrame currentFrame;
	private JTextField logPathField;
	
	public ConnectionFrame() {
		this.currentFrame=this;
		this.init();
		this.setTitle("Options");
		this.setSize(400,300);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void init() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill=GridBagConstraints.BOTH;
		
		JLabel portLabel = new JLabel("Port:");
		JLabel buadrateLabel = new JLabel("Buadrate:");
		JLabel logLabel = new JLabel("Log:");
		JComboBox<String> portBox = new JComboBox<String>();
		JComboBox<Integer> buadrateBox = new JComboBox<Integer>();
		logPathField = new JTextField();
		JButton openFileButton = new JButton("..");
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				currentFrame.dispose();
				currentFrame=null;
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
//				fatherFrame.setSerialPort((String) portBox.getSelectedItem());
//				fatherFrame.setBuadrate((int) buadrateBox.getSelectedItem());
//				fatherFrame.setLogPath(logPathField.getText());
				currentFrame.dispose();
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
        
        
        this.add(portLabel);
        this.add(portBox);
        this.add(buadrateLabel);
        this.add(buadrateBox);
        this.add(logLabel);
        this.add(logPathField);
        this.add(openFileButton);
		this.add(cancelButton);
		this.add(confirmButton);
		
		openFileButton.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		FileDialog fileDialog = new FileDialog(this);                 
		fileDialog.setVisible(true);
		String filePath = fileDialog.getDirectory();		
		String fileName = fileDialog.getFile();		
		if(filePath == null  || fileName == null){			
		}else{
			logPathField.setText(filePath + fileName);
		}
		
	}
}
