package com.cannontech.dbeditor.editor.user;
/**
 * This type was created in VisualAge.
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.i18n.DisplayableEnumCellRenderer;
import com.cannontech.common.login.ClientSession;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.web.EnergyCompanyOperatorLoginList;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IDatabaseCache;

//import com.cannontech.database.db.user.YukonUser;

public class UserLoginBasePanel extends com.cannontech.common.gui.util.DataInputPanel {
    private AuthenticationService authenticationService = (AuthenticationService) YukonSpringHook.getBean("authenticationService");
	private javax.swing.JLabel ivjJLabelUserName = null;
	private javax.swing.JPanel ivjJPanelLoginPanel = null;
	private javax.swing.JTextField ivjJTextFieldUserID = null;
	private javax.swing.JCheckBox ivjJCheckBoxEnableLogin = null;
	private javax.swing.JButton ivjJButtonChangePassword = null;
	private javax.swing.JComboBox ivjJListAuthType = null;
	private javax.swing.JLabel ivjJLabelAuthType = null;
	private javax.swing.JLabel ivjJLabelErrorMessage = null;
	private javax.swing.JPanel ivjJPanelError = null;
    private AuthType initialAuthType = AuthType.PLAIN;
    private String newPasswordValue = null;
    private boolean passwordRequiresChanging = false;
	private long oldEnergyCompanyID = -1;
	private javax.swing.JCheckBox ivjJCheckBoxEnableEC = null;
	private javax.swing.JComboBox ivjJComboBoxEnergyCompany = null;
	private javax.swing.JPanel ivjJPanelEC = null;
	private String initialUsername;

    private ActionListener enableEcActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            jCheckBoxEnableEC_ActionPerformed(e);
        }
    };
    
    private CaretListener inputUpdateCaretListener = new CaretListener() {
        public void caretUpdate(CaretEvent e) {
            fireInputUpdate();
        }
    };
    
    private ActionListener inputUpdateActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            fireInputUpdate();
        }
    };

/**
 * Constructor
 */
public UserLoginBasePanel() {
	super();
	initialize();
}

/**
 * Return the JCheckBoxEnableEC property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getJCheckBoxEnableEC() {
	if (ivjJCheckBoxEnableEC == null) {
		try {
			ivjJCheckBoxEnableEC = new javax.swing.JCheckBox();
			ivjJCheckBoxEnableEC.setName("JCheckBoxEnableEC");
			ivjJCheckBoxEnableEC.setPreferredSize(new java.awt.Dimension(174, 22));
			ivjJCheckBoxEnableEC.setText("Link to Energy Company: ");
			ivjJCheckBoxEnableEC.setMaximumSize(new java.awt.Dimension(174, 22));
			ivjJCheckBoxEnableEC.setMinimumSize(new java.awt.Dimension(174, 22));
			ivjJCheckBoxEnableEC.setToolTipText("Only for use with STARS.  Verify that STARS and its operator groups have been properly configured.");
		} catch (Exception ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnableEC;
}


/**
 * Return the JCheckBoxEnableLogin property value.
 * @return javax.swing.JCheckBox
 */
private javax.swing.JCheckBox getJCheckBoxEnableLogin() {
	if (ivjJCheckBoxEnableLogin == null) {
		try {
			ivjJCheckBoxEnableLogin = new javax.swing.JCheckBox();
			ivjJCheckBoxEnableLogin.setName("JCheckBoxEnableLogin");
			ivjJCheckBoxEnableLogin.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJCheckBoxEnableLogin.setText("Login Enabled");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnableLogin;
}


/**
 * Return the JComboBoxEnergyCompany property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getJComboBoxEnergyCompany() {
	if (ivjJComboBoxEnergyCompany == null) {
		try {
			ivjJComboBoxEnergyCompany = new javax.swing.JComboBox();
			ivjJComboBoxEnergyCompany.setName("JComboBoxEnergyCompany");
			ivjJComboBoxEnergyCompany.setPreferredSize(new java.awt.Dimension(215, 23));
			ivjJComboBoxEnergyCompany.setMinimumSize(new java.awt.Dimension(215, 23));
			// user code begin {1}
			ivjJComboBoxEnergyCompany.setEnabled(false);
			ivjJComboBoxEnergyCompany.setToolTipText("Only for use with STARS.  Verify that STARS and its operator groups have been properly configured.");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxEnergyCompany;
}


/**
 * Return the JLabelPhone1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JButton getJButtonChangePassword() {
    if (ivjJButtonChangePassword == null) {
        try {
            ivjJButtonChangePassword = new javax.swing.JButton();
            ivjJButtonChangePassword.setName("JButtonChangePassword");
            ivjJButtonChangePassword.setText("Change Password");
            ivjJButtonChangePassword.setEnabled(true);
            ivjJButtonChangePassword.setSelected(false);
            final JPanel that = this;
            ivjJButtonChangePassword.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ChangePasswordDialog dialog = 
                        ChangePasswordDialog.create(that, "Enter a new password for user");
                    dialog.setVisible(true);
                    if (dialog.getNewPassword() != null) {
                        fireInputUpdate();
                        newPasswordValue = dialog.getNewPassword();
                    }
                }
            });
        } catch (Exception ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjJButtonChangePassword;
}
/**
 * Return the JLabelPhone1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelAuthType() {
    if (ivjJLabelAuthType == null) {
        try {
            ivjJLabelUserName = new javax.swing.JLabel();
            ivjJLabelUserName.setName("JLabelAuthType");
            ivjJLabelUserName.setText("Authentication:");
            ivjJLabelUserName.setMaximumSize(new java.awt.Dimension(122, 17));
            ivjJLabelUserName.setPreferredSize(new java.awt.Dimension(122, 17));
            ivjJLabelUserName.setFont(new java.awt.Font("dialog", 0, 14));
            ivjJLabelUserName.setMinimumSize(new java.awt.Dimension(122, 17));
        } catch (Exception ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjJLabelUserName;
}


/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JComboBox getJListAuthType() {
    if (ivjJListAuthType == null) {
        ivjJListAuthType = new javax.swing.JComboBox(AuthType.values());
        ivjJListAuthType.setRenderer(new DisplayableEnumCellRenderer());
        ivjJListAuthType.setName("JListAuthType");
        java.awt.Dimension dimension = new java.awt.Dimension(122, 17);
        ivjJListAuthType.setMaximumSize(dimension);
        ivjJListAuthType.setPreferredSize(dimension);
        ivjJListAuthType.setMinimumSize(dimension);
        ivjJListAuthType.setFont(new java.awt.Font("dialog", 0, 12));
        ivjJListAuthType.setEnabled(true);
        ivjJListAuthType.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) return;
                AuthType type = (AuthType) ivjJListAuthType.getSelectedItem();
                boolean shouldSetPass = authenticationService.supportsPasswordSet(type);
                if (type != initialAuthType) {
                    fireInputUpdate();
                }
                getJButtonChangePassword().setEnabled(shouldSetPass);
            }
        });
    }
    return ivjJListAuthType;
}

/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelUserName() {
	if (ivjJLabelUserName == null) {
		try {
			ivjJLabelUserName = new javax.swing.JLabel();
			ivjJLabelUserName.setName("JLabelUserName");
			ivjJLabelUserName.setText("Username:");
			ivjJLabelUserName.setMaximumSize(new java.awt.Dimension(122, 17));
			ivjJLabelUserName.setPreferredSize(new java.awt.Dimension(122, 17));
			ivjJLabelUserName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelUserName.setEnabled(true);
			ivjJLabelUserName.setMinimumSize(new java.awt.Dimension(122, 17));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelUserName;
}

private javax.swing.JPanel getJPanelError() {
    if (ivjJPanelError == null) {
        try {
            ivjJPanelError = new javax.swing.JPanel();
            ivjJPanelError.setName("JPanelError");
            ivjJPanelError.setPreferredSize(new java.awt.Dimension(342, 10));
            ivjJPanelError.setLayout(new java.awt.FlowLayout());
            ivjJPanelError.setMaximumSize(new java.awt.Dimension(342, 10));
            ivjJPanelError.setMinimumSize(new java.awt.Dimension(342, 10));
            getJPanelError().add(getJLabelErrorMessage(), getJLabelErrorMessage().getName());
            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjJPanelError;
}

private javax.swing.JLabel getJLabelErrorMessage() {
    if (ivjJLabelErrorMessage == null) {
        try {
            ivjJLabelErrorMessage = new javax.swing.JLabel();
            ivjJLabelErrorMessage.setName("JLabelRange");
            ivjJLabelErrorMessage.setOpaque(false);
            ivjJLabelErrorMessage.setText("...RANGE TEXT...");
            ivjJLabelErrorMessage.setVisible(true);
            ivjJLabelErrorMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            ivjJLabelErrorMessage.setFont(new java.awt.Font("Arial", 1, 10));
            ivjJLabelErrorMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            ivjJLabelErrorMessage.setVisible( false );

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjJLabelErrorMessage;
}

/**
 * Return the JPanelEC property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelEC() {
	if (ivjJPanelEC == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Energy Company");
			ivjJPanelEC = new javax.swing.JPanel();
			ivjJPanelEC.setName("JPanelEC");
			ivjJPanelEC.setBorder(ivjLocalBorder1);
			ivjJPanelEC.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxEnableEC = new java.awt.GridBagConstraints();
			constraintsJCheckBoxEnableEC.gridx = 1; constraintsJCheckBoxEnableEC.gridy = 1;
			constraintsJCheckBoxEnableEC.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxEnableEC.ipadx = -4;
			constraintsJCheckBoxEnableEC.insets = new java.awt.Insets(20, 11, 29, 6);
			getJPanelEC().add(getJCheckBoxEnableEC(), constraintsJCheckBoxEnableEC);

			java.awt.GridBagConstraints constraintsJComboBoxEnergyCompany = new java.awt.GridBagConstraints();
			constraintsJComboBoxEnergyCompany.gridx = 2; constraintsJComboBoxEnergyCompany.gridy = 1;
			constraintsJComboBoxEnergyCompany.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxEnergyCompany.weightx = 1.0;
			constraintsJComboBoxEnergyCompany.ipadx = -20;
			constraintsJComboBoxEnergyCompany.insets = new java.awt.Insets(20, 6, 28, 12);
			getJPanelEC().add(getJComboBoxEnergyCompany(), constraintsJComboBoxEnergyCompany);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelEC;
}

/**
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelLoginPanel() {
	if (ivjJPanelLoginPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Login Information");
			ivjJPanelLoginPanel = new javax.swing.JPanel();
			ivjJPanelLoginPanel.setName("JPanelLoginPanel");
			ivjJPanelLoginPanel.setBorder(ivjLocalBorder);
			ivjJPanelLoginPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelUserName = new java.awt.GridBagConstraints();
			constraintsJLabelUserName.gridx = 1; constraintsJLabelUserName.gridy = 1;
			constraintsJLabelUserName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelUserName.insets = new java.awt.Insets(2, 20, 2, 3);
			getJPanelLoginPanel().add(getJLabelUserName(), constraintsJLabelUserName);

			java.awt.GridBagConstraints constraintsJTextFieldUserID = new java.awt.GridBagConstraints();
			constraintsJTextFieldUserID.gridx = 2; constraintsJTextFieldUserID.gridy = 1;
			constraintsJTextFieldUserID.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldUserID.weightx = 1.0;
			constraintsJTextFieldUserID.insets = new java.awt.Insets(0, 4, 1, 22);
			getJPanelLoginPanel().add(getJTextFieldUserID(), constraintsJTextFieldUserID);
			
			java.awt.GridBagConstraints constraintsJLabelAuthType = new java.awt.GridBagConstraints();
			constraintsJLabelAuthType.gridx = 1; constraintsJLabelAuthType.gridy = 2;
			constraintsJLabelAuthType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAuthType.insets = new java.awt.Insets(2, 20, 2, 3);
			getJPanelLoginPanel().add(getJLabelAuthType(), constraintsJLabelAuthType);
			
			java.awt.GridBagConstraints constraintsJListAuthType = new java.awt.GridBagConstraints();
			constraintsJListAuthType.gridx = 2; constraintsJListAuthType.gridy = 2;
			constraintsJListAuthType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJListAuthType.insets = new java.awt.Insets(0, 4, 1, 22);
			getJPanelLoginPanel().add(getJListAuthType(), constraintsJListAuthType);
			
			java.awt.GridBagConstraints constraintsJButtonChangePassword = new java.awt.GridBagConstraints();
			constraintsJButtonChangePassword.gridx = 1; constraintsJButtonChangePassword.gridy = 3;
            constraintsJButtonChangePassword.gridwidth = 2;
			constraintsJButtonChangePassword.anchor = java.awt.GridBagConstraints.CENTER;
			constraintsJButtonChangePassword.insets = new java.awt.Insets(15, 15, 2, 15);
			getJPanelLoginPanel().add(getJButtonChangePassword(), constraintsJButtonChangePassword);
			
		} catch (Exception ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoginPanel;
}


/**
 * Return the JTextFieldFirstName property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getJTextFieldUserID() {
	if (ivjJTextFieldUserID == null) {
		try {
			ivjJTextFieldUserID = new javax.swing.JTextField();
			ivjJTextFieldUserID.setName("JTextFieldUserID");
			ivjJTextFieldUserID.setPreferredSize(new java.awt.Dimension(229, 20));
			ivjJTextFieldUserID.setEnabled(true);
			ivjJTextFieldUserID.setMinimumSize(new java.awt.Dimension(229, 20));
			ivjJTextFieldUserID.setDocument( new TextFieldDocument(64));
		} catch (Exception ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldUserID;
}

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	YukonUser login = null;
	
	if( o == null )
	{
		login = new YukonUser();
		/* addDefaultGroups( login ); */
	}
	else
		login = (YukonUser)o;

	if( getJTextFieldUserID().getText() != null && getJTextFieldUserID().getText().length() > 0 )
		login.getYukonUser().setUsername( getJTextFieldUserID().getText() );

    AuthType type = (AuthType) getJListAuthType().getSelectedItem();
    login.getYukonUser().setAuthType(type);
    
    
    boolean shouldSetPass = authenticationService.supportsPasswordSet(type);
    if (type != initialAuthType) {
        if (shouldSetPass) {
            passwordRequiresChanging = true;
        } else {
            passwordRequiresChanging = false;
            newPasswordValue = null;
        }
    }
    
    if (passwordRequiresChanging && newPasswordValue == null) {
        // prompt for new password
        
        ChangePasswordDialog dialog = 
            ChangePasswordDialog.create(this, "A new password must be entered for this user");
        dialog.setVisible(true);
        if (dialog.getNewPassword() == null) {
            throw new RuntimeException("Must enter new password to change user.");
        }
        newPasswordValue = dialog.getNewPassword();
    }

    if (getJCheckBoxEnableLogin().isSelected()) {
        login.getYukonUser().setLoginStatus(LoginStatusEnum.ENABLED);
    } else {
        login.getYukonUser().setLoginStatus(LoginStatusEnum.DISABLED);
    }

	if(getJCheckBoxEnableEC().isSelected())
	{
		LiteEnergyCompany co = (LiteEnergyCompany)getJComboBoxEnergyCompany().getSelectedItem();
		
		EnergyCompanyOperatorLoginList ecop = new EnergyCompanyOperatorLoginList(new Integer(co.getLiteID()), login.getUserID());
		
		//if this is the same energy company that it was before, don't bother updating
		if(co.getLiteID() != oldEnergyCompanyID)	
			login.setEnergyCompany(ecop);
	}
	
	//there was an existing energy company link, but we want it no longer
	else if((! getJCheckBoxEnableEC().isSelected()) && oldEnergyCompanyID != -1)
	{
		EnergyCompanyOperatorLoginList ecop = new EnergyCompanyOperatorLoginList();
		ecop.setOperatorLoginID(login.getUserID());
		login.setEnergyCompany(ecop);
	}

	return login;
}

@Override
public void postSave(DBPersistent o) {
    super.postSave(o);
    if (newPasswordValue != null) {
        // change password
        LiteYukonUser liteYukonuser = (LiteYukonUser) LiteFactory.createLite(o);
        AuthType authType = liteYukonuser.getAuthType();
        boolean supportsSetPassword = authenticationService.supportsPasswordSet(authType);
        if (supportsSetPassword) {
            authenticationService.setPassword(liteYukonuser, newPasswordValue);
        }
        
        newPasswordValue = null;
        passwordRequiresChanging = false;
    }
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {
    /* Uncomment the following lines to print uncaught exceptions to stdout */
    com.cannontech.clientutils.CTILogger.error( "Uncaught exception in Spring code", exception );;
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	getJTextFieldUserID().addCaretListener(inputUpdateCaretListener);
	getJCheckBoxEnableLogin().addActionListener(inputUpdateActionListener);
	getJComboBoxEnergyCompany().addActionListener(inputUpdateActionListener);
	getJCheckBoxEnableEC().addActionListener(enableEcActionListener);
}


/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("CustomerContactLoginPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJPanelLoginPanel = new java.awt.GridBagConstraints();
		constraintsJPanelLoginPanel.gridx = 0; constraintsJPanelLoginPanel.gridy = 1;
		constraintsJPanelLoginPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLoginPanel.weightx = 1.0;
		constraintsJPanelLoginPanel.weighty = 1.0;
		constraintsJPanelLoginPanel.ipadx = -10;
		constraintsJPanelLoginPanel.ipady = -26;
		constraintsJPanelLoginPanel.insets = new java.awt.Insets(3, 8, 3, 8);
		add(getJPanelLoginPanel(), constraintsJPanelLoginPanel);

		java.awt.GridBagConstraints constraintsJCheckBoxEnableLogin = new java.awt.GridBagConstraints();
		constraintsJCheckBoxEnableLogin.gridx = 0; constraintsJCheckBoxEnableLogin.gridy = 0;
		constraintsJCheckBoxEnableLogin.ipadx = 32;
		constraintsJCheckBoxEnableLogin.ipady = -2;
		constraintsJCheckBoxEnableLogin.insets = new java.awt.Insets(10, 8, 2, 251);
		add(getJCheckBoxEnableLogin(), constraintsJCheckBoxEnableLogin);

		java.awt.GridBagConstraints constraintsJPanelEC = new java.awt.GridBagConstraints();
		constraintsJPanelEC.gridx = 0; constraintsJPanelEC.gridy = 2;
		constraintsJPanelEC.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelEC.weightx = 1.0;
		constraintsJPanelEC.weighty = 1.0;
		constraintsJPanelEC.ipadx = -10;
		constraintsJPanelEC.ipady = -26;
		constraintsJPanelEC.insets = new java.awt.Insets(4, 8, 4, 8);
		add(getJPanelEC(), constraintsJPanelEC);
		
		java.awt.GridBagConstraints constraintsJPanelError = new java.awt.GridBagConstraints();
		constraintsJPanelError.gridx = 0; constraintsJPanelLoginPanel.gridy = 3;
		constraintsJPanelError.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelError.weightx = 1.0;
		constraintsJPanelError.weighty = 1.0;
		constraintsJPanelError.ipadx = -20;
		constraintsJPanelError.ipady = -36;
		constraintsJPanelError.insets = new java.awt.Insets(6, 8, 6, 8);
        add(getJPanelError(), constraintsJPanelError);
		
		initConnections();
	} catch (Exception ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
    String userName = getJTextFieldUserID().getText();
	if( StringUtils.isBlank( userName ))
	{
		setErrorString("The Userid text field must be filled in");
		return false;
	}
	
	LiteYukonUser liteYukonUser = DaoFactory.getYukonUserDao().findUserByUsername( userName );
	
    //if we are trying to enter a username to one that already exists
	if ( !userName.equals( initialUsername ) && liteYukonUser != null ) {
	    setErrorString("Invalid username selection, please try a different username");
        getJLabelErrorMessage().setText( "(" + getErrorString() + ")" );
        getJLabelErrorMessage().setToolTipText( "(" + getErrorString() + ")" );
        getJLabelErrorMessage().setVisible( true );
        return false;
	}
	
	initialUsername = getJTextFieldUserID().getText();
    getJLabelErrorMessage().setText( "" );
    getJLabelErrorMessage().setToolTipText( "" );
    getJLabelErrorMessage().setVisible( false );
	return true;
}

/**
 * Comment
 */
public void jCheckBoxEnableEC_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	getJComboBoxEnergyCompany().setEnabled(getJCheckBoxEnableEC().isSelected());
	
	if(getJCheckBoxEnableEC().isSelected())
	{
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		List companies = new ArrayList(cache.getAllEnergyCompanies());
		java.util.Collections.sort( companies, LiteComparators.liteStringComparator );
		
		getJComboBoxEnergyCompany().removeAllItems();
		for(int j = 0; j < companies.size(); j++)
		{
			//weed out the default energy company
			if(((LiteEnergyCompany)companies.get(j)).getEnergyCompanyID() != -1)
				getJComboBoxEnergyCompany().addItem(companies.get(j));
		}
		
	}
	
	
	fireInputUpdate();
	
	return;
}


/**
 * Comment
 */
public void jCheckBoxEnableLogin_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJTextFieldUserID().setEnabled( getJCheckBoxEnableLogin().isSelected() );
    getJButtonChangePassword().setEnabled(getJCheckBoxEnableLogin().isSelected());
	getJLabelUserName().setEnabled( getJCheckBoxEnableLogin().isSelected() );

	fireInputUpdate();
	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		UserLoginBasePanel aUserLoginBasePanel;
		aUserLoginBasePanel = new UserLoginBasePanel();
		frame.setContentPane(aUserLoginBasePanel);
		frame.setSize(aUserLoginBasePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}


/**
 * setValue method comment.
 */
@Override
public void setValue(Object o) 
{
	if (o == null) {
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        initialAuthType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, AuthType.class, null );
        
        getJListAuthType().setSelectedItem(initialAuthType);
        boolean supportsPasswordSet = authenticationService.supportsPasswordSet(initialAuthType);
        passwordRequiresChanging = supportsPasswordSet;

        return;
    }

	YukonUser login = (YukonUser)o;
	
    if (!login.getYukonUser().getLoginStatus().isDisabled()) {
        getJCheckBoxEnableLogin().doClick();
    }

	getJTextFieldUserID().setText( login.getYukonUser().getUsername() );
	initialUsername = getJTextFieldUserID().getText();
    initialAuthType = login.getYukonUser().getAuthType();
    getJListAuthType().setSelectedItem(initialAuthType);
    boolean authSupportsPasswordSet = authenticationService.supportsPasswordSet(initialAuthType);
    getJButtonChangePassword().setEnabled(authSupportsPasswordSet);
	
	if(login.getUserID().intValue() == UserUtils.USER_ADMIN_ID)
	{
		getJTextFieldUserID().setEnabled(false);
		getJCheckBoxEnableLogin().setEnabled(false);
		getJCheckBoxEnableLogin().setSelected(true);
		boolean loggedInAsAdmin = ClientSession.getInstance().getUser().getUserID() == UserUtils.USER_ADMIN_ID;
		getJButtonChangePassword().setEnabled(loggedInAsAdmin);
	}

	
	long company = EnergyCompanyOperatorLoginList.getEnergyCompanyID(login.getUserID().longValue());
		
	if(company != -1)
	{
		getJCheckBoxEnableEC().doClick();
		for(int d = 0; d < getJComboBoxEnergyCompany().getModel().getSize(); d++)
		{
			LiteEnergyCompany ceo = (LiteEnergyCompany)getJComboBoxEnergyCompany().getModel().getElementAt(d);
			if(ceo.getLiteID() == company)
			{
				getJComboBoxEnergyCompany().setSelectedItem(ceo);
				oldEnergyCompanyID = company;
				break;
			}
		}
	}

}
}