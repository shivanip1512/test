/*
 * Created on Jun 19, 2003
 */
package com.cannontech.common.login;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.BlockingGlassPane;
import com.cannontech.common.gui.util.SwingWorker;
import com.cannontech.common.util.CtiUtilities;

/**
 * Basic login panel.
 * @author aaron
 */
class LoginPanel extends JPanel implements CaretListener, ActionListener {
	
	private final JLabel messageLabel = new JLabel("Enter your Yukon username and password:");
	//private final JLabel messageLabel2 = new JLabel("")
	private final JLabel hostLabel = new JLabel("Yukon server:");
	private final JLabel portLabel = new JLabel("Port:");
	private final JLabel usernameLabel = new JLabel("User name:");
	private final JLabel passwordLabel = new JLabel("Password:");
	private final JComboBox hostComboBox = new JComboBox();
	private final JTextField portField = new JTextField();
	{
		portField.setColumns(5);
	}
	private final JTextField usernameField = new JTextField();
	private final JPasswordField passwordField = new JPasswordField();
	private final JCheckBox rememberCheckBox = new JCheckBox("Remember my password");
	
	//private final JButton loginButton = new JButton("Ok");
	//private final JButton cancelButton = new JButton("Cancel");
	
	// After logging in these will be set
	private String sessionID = null;
	
	/* This adapter is added to all the components above
	 * so the login button is pressed whenever the enter button is hit */
	/*private final KeyAdapter myKeyAdapter = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				loginButton.doClick();
			}
		}
	};
	*/
	public LoginPanel() {
		this(
			LoginPrefs.getInstance().getCurrentYukonHost(),
			LoginPrefs.getInstance().getAvailableYukonHosts(),
			LoginPrefs.getInstance().getCurrentYukonPort(),
			LoginPrefs.getInstance().getDefaultUsername(), 
			LoginPrefs.getInstance().getDefaultPassword(), 
			LoginPrefs.getInstance().getDefaultRememberPassword(),
			true);
	}
	
	public LoginPanel(String host, String[] hosts, int port, String username, String password, boolean rememberPassword, boolean localLogin) {
				
		setLayout(new GridBagLayout());
		
		GridBagConstraints messageLabelCons = 
			new GridBagConstraints(0,0,3,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(8,4,8,52),0,0);
			
		GridBagConstraints hostLabelCons = 
			new GridBagConstraints(0,1,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);	
 	
		GridBagConstraints usernameLabelCons = 
			new GridBagConstraints(0,2,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
		GridBagConstraints passwordLabelCons = 
			new GridBagConstraints(0,3,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
			
		GridBagConstraints hostComboCons = 
					new GridBagConstraints(1,1,1,1,1.0,0.0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
					
		GridBagConstraints usernameFieldCons = 
			new GridBagConstraints(1,2,3,1,1.0,0.0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
		GridBagConstraints passwordFieldCons = 
			new GridBagConstraints(1,3,3,1,1.0,0.0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
		GridBagConstraints rememberCheckBoxCons =
			new GridBagConstraints(1,4,3,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
		//GridBagConstraints loginButtonCons = 
		//	new GridBagConstraints(1,5,1,1,1.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,4,4,4),0,0);
		//GridBagConstraints cancelButtonCons = 
		//	new GridBagConstraints(2,5,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(4,4,4,4),0,0);
		
	
		GridBagConstraints portLabelCons = 
			new GridBagConstraints(2,1,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
				
		GridBagConstraints portFieldCons = 					
			new GridBagConstraints(3,1,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
										
	//	add(messageLabel, messageLabelCons);
		if(!localLogin)
			add(hostLabel, hostLabelCons);
		add(usernameLabel, usernameLabelCons);
		add(passwordLabel, passwordLabelCons);
		add(usernameField, usernameFieldCons);
		add(passwordField, passwordFieldCons);
		
		if(!localLogin)
			add(hostComboBox, hostComboCons);
		add(rememberCheckBox, rememberCheckBoxCons);
		
		if(!localLogin) {
			add(portLabel, portLabelCons);
			add(portField, portFieldCons);
		}
			
		//add(loginButton, loginButtonCons);			
		//add(cancelButton, cancelButtonCons);
		
		//((JTextField) hostComboBox.getEditor().getEditorComponent()).addKeyListener(myKeyAdapter);
		//hostComboBox.addActionListener(this);
		usernameField.addCaretListener(this);
		passwordField.addCaretListener(this);
		
		//hostComboBox.addKeyListener(myKeyAdapter);
		//usernameField.addKeyListener(myKeyAdapter);
		//passwordField.addKeyListener(myKeyAdapter);
		//rememberCheckBox.addKeyListener(myKeyAdapter);
		
		hostComboBox.setEditable(true);
		for(int i = 0; i < hosts.length; i++) {
			hostComboBox.addItem(hosts[i]);
		}
		hostComboBox.setSelectedItem(host);
		setPort(port);
		setUsername(username);
		setPassword(password);
		setRememberPassword(rememberPassword);	
	}

	public String getYukonHost() {
		return hostComboBox.getSelectedItem().toString();
	}
	
	public int getYukonPort() {
		return Integer.parseInt(portField.getText().trim());
	}
	
	public String getUsername() {
		return usernameField.getText();
	}
	
	public String getPassword() { 
		return new String(passwordField.getPassword());
	}
	
	public boolean isRememberPassword() {
		return rememberCheckBox.isSelected();
	}
	
	public void setPort(int port) {
		portField.setText(Integer.toString(port));
	}
	
	public void setUsername(String username) {
		usernameField.setText(username);
	}
	
	public void setPassword(String password) {
		passwordField.setText(password);
	}
	
	public void setRememberPassword(boolean b) {
		rememberCheckBox.setSelected(b);
	}
	
	void addLoginActionListener(ActionListener l) {
	//	loginButton.addActionListener(l);
	}
	
	void addCancelActionListener(ActionListener l) {
	//	cancelButton.addActionListener(l);
	}
	
	public void caretUpdate(CaretEvent e) {
	//	loginButton.setEnabled( getYukonHost().length() >0 &&
	//							getUsername().length() > 0 && 
	//							getPassword().length() > 0);
	}
	
	/**
	 * Displays a modal login dialog and returns either a LiteYukonUser
	 * if a successful login occcured or null otherwise.
	 * @return
	 */
	public String showLoginDialog() {
		 return showLoginDialog(null);
	}
	
	/**
	 * Displays a modal login dialog and returns either a sessionid
	 * if a successful login occcured or null otherwise.
	 * @param owner
	 * @return
	 */
	public String showLoginDialog(Frame owner) {		
		final JDialog d = new JDialog(owner); 
		final BlockingGlassPane gp = new BlockingGlassPane();
		d.setModal(true);
		d.setTitle("Yukon Login");
		final LoginPanel lp = this;
		
		/* Catch the login event and try to find this user */
		lp.addLoginActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* Don't allow anymore events to get through */	
				gp.block(true);			
				d.getRootPane().getGlassPane().setVisible(true);
								
				/* Do this in another thread */
				SwingWorker worker = new SwingWorker() {
					String sessionID;
					String errMsg;
					
					public Object construct() {
						try {	
							sessionID = LoginSupport.getSessionID(lp.getYukonHost(), lp.getYukonPort(), lp.getUsername(), lp.getPassword());				
//							user = AuthFuncs.login(lp.getUsername(), lp.getPassword());
						}
						catch(RuntimeException re) {
							errMsg = re.getMessage();
						}
						finally {
							return sessionID;
						}
					}
					public void finished() {
						if(sessionID == null) { 
							// Why did this fail?
							if(errMsg == null) {							
								errMsg = "Unableto log in as " + lp.getUsername();
							}		
							JOptionPane.showMessageDialog(d, errMsg, "Login Error", JOptionPane.ERROR_MESSAGE);
							gp.block(false);
							d.getRootPane().getGlassPane().setVisible(false);						
						}
						else { //success set the logged in user and get out of town
							lp.sessionID = sessionID;
							d.dispose();
						}
					}
				};
				
				worker.start();						
			}
		});
		
		/* Catch the cancel event */
		lp.addCancelActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d.dispose();
			}
		});
		
		d.setContentPane(lp);		
		d.getRootPane().setGlassPane(gp);
		d.pack();
		d.setLocationRelativeTo(owner);
		d.show();
		return lp.sessionID;
	}
	
	public static void main(String[] args) {
		CtiUtilities.setLaF();
		LoginPanel lp = new LoginPanel();
		String sessionID = lp.showLoginDialog();
		if(sessionID != null) {
			System.out.println("Obtained sessionID: " + sessionID);
			System.exit(0);
		}
		else {
			System.out.println("Login failed");
			System.exit(-1);
		}
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		//loginButton.setEnabled( getYukonHost().length() >0 &&
		//								getUsername().length() > 0 && 
		//								getPassword().length() > 0);

	}

}
