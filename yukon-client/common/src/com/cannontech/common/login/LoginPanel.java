/*
 * Created on Jun 19, 2003
 */
package com.cannontech.common.login;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Basic login panel.
 * @author aaron
 */
class LoginPanel extends JPanel {
	
	private final JLabel hostLabel = new JLabel("Yukon server:");
	private final JLabel usernameLabel = new JLabel("User name:");
	private final JLabel passwordLabel = new JLabel("Password:");
	private final JTextField hostField = new JTextField();
	private final JTextField usernameField = new JTextField();
	private final JPasswordField passwordField = new JPasswordField();
	private final JCheckBox rememberCheckBox = new JCheckBox("Remember my password");
	
	public LoginPanel(String host, String username, String password, boolean rememberPassword, boolean localLogin) {
				
		setLayout(new GridBagLayout());
		
		GridBagConstraints hostLabelCons = 
			new GridBagConstraints(0,1,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);	
 	
		GridBagConstraints usernameLabelCons = 
			new GridBagConstraints(0,2,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
		GridBagConstraints passwordLabelCons = 
			new GridBagConstraints(0,3,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
			
		GridBagConstraints hostComboCons = 
					new GridBagConstraints(1,1,2,1,1.0,0.0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
					
		GridBagConstraints usernameFieldCons = 
			new GridBagConstraints(1,2,3,1,1.0,0.0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
		GridBagConstraints passwordFieldCons = 
			new GridBagConstraints(1,3,3,1,1.0,0.0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
		GridBagConstraints rememberCheckBoxCons =
			new GridBagConstraints(1,4,3,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(4,4,4,4),0,0);
	
		if (!localLogin) {
            add(hostLabel, hostLabelCons);
        }
        add(usernameLabel, usernameLabelCons);
        add(passwordLabel, passwordLabelCons);
        add(usernameField, usernameFieldCons);
        add(passwordField, passwordFieldCons);

        if (!localLogin) {
            add(hostField, hostComboCons);
        }
        add(rememberCheckBox, rememberCheckBoxCons);

		hostField.setEditable(true);
		setYukonHost(host);
		
		setUsername(username);
		
		setPassword(password);
		setRememberPassword(rememberPassword);	
		
		setHostEditable(true);
		setUserEditable(true);
		
	}
	
	public String getYukonHost() {
		return hostField.getText();
	}
	
	public void setYukonHost(String host) {
	    hostField.setText(host);
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
	
	public void setUsername(String username) {
		usernameField.setText(username);
	}
	
	public void setPassword(String password) {
		passwordField.setText(password);
	}
	
	public void setRememberPassword(boolean b) {
		rememberCheckBox.setSelected(b);
	}
	
	public void setHostEditable(boolean hostEditable) {
	    hostField.setEnabled(hostEditable);
    }

    public void setUserEditable(boolean userEditable) {
        usernameField.setEnabled(userEditable);
    }

}
