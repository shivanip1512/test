package com.cannontech.common.login;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.cannontech.clientutils.ClientApplicationRememberMe;

class LoginPanel extends JPanel {
	
	private final JLabel hostLabel = new JLabel("Yukon server:");
	private final JLabel usernameLabel = new JLabel("Username:");
	private final JLabel passwordLabel = new JLabel("Password:");
	private final JTextField hostField = new JTextField(15);
	private final JTextField usernameField = new JTextField(15);
	private final JPasswordField passwordField = new JPasswordField(15);
	private final JCheckBox rememberCheckBox = new JCheckBox("Remember me");
	private ClientApplicationRememberMe rememberMeSetting;

    public LoginPanel(String host, String username, String password, boolean rememberPassword,
                      boolean localLogin, ClientApplicationRememberMe rememberMeSetting) {
				
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

		hostField.setEditable(true);
		setYukonHost(host);

        if(rememberMeSetting != ClientApplicationRememberMe.NONE) {
            add(rememberCheckBox, rememberCheckBoxCons);
        }

        if(rememberMeSetting == ClientApplicationRememberMe.USERNAME_AND_PASSWORD) {
            setPassword(password); // No need to prepopulate the password unless we remembered it
        }

        // always set the username (even if we don't 'remember' it)
        //JWS clients pre-populate this and are not allowed to edit it
        setUsername(username);
		setRememberMe(rememberPassword);

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
	
	public boolean isRememberMe() {
		return rememberMeSetting != ClientApplicationRememberMe.NONE 
		        && rememberCheckBox.isSelected();
	}

	public void setUsername(String username) {
		usernameField.setText(username);
	}
	
	public void setPassword(String password) {
		passwordField.setText(password);
	}
	
	public void setRememberMe(boolean b) {
		rememberCheckBox.setSelected(b);
	}
	
	public void setHostEditable(boolean hostEditable) {
	    hostField.setEnabled(hostEditable);
    }

    public void setUserEditable(boolean userEditable) {
        usernameField.setEnabled(userEditable);
    }

}
