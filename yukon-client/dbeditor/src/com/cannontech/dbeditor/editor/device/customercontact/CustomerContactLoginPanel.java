package com.cannontech.dbeditor.editor.device.customercontact;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.database.db.customer.CustomerLogin;
import com.cannontech.database.data.customer.CustomerContact;

public class CustomerContactLoginPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelNormalPassword = null;
	private javax.swing.JLabel ivjJLabelRetypePassword = null;
	private javax.swing.JLabel ivjJLabelUserName = null;
	private javax.swing.JPasswordField ivjJPasswordFieldPassword = null;
	private javax.swing.JPasswordField ivjJPasswordFieldRetypePassword = null;
	private javax.swing.JPanel ivjJPanelLoginPanel = null;
	private javax.swing.JTextField ivjJTextFieldUserID = null;
	private javax.swing.JCheckBox ivjJCheckBoxEnableLogin = null;
	private javax.swing.JCheckBox ivjJCheckBoxCurtailment = null;
	private javax.swing.JCheckBox ivjJCheckBoxLoadControl = null;
	private javax.swing.JCheckBox ivjJCheckBoxReadmeter = null;
	private javax.swing.JComboBox ivjJComboBoxCurtailmentRights = null;
	private javax.swing.JComboBox ivjJComboBoxLoadControlRights = null;
	private javax.swing.JComboBox ivjJComboBoxReadmeterRights = null;
	private javax.swing.JPanel ivjJPanelLoginType = null;
	private javax.swing.JCheckBox ivjJCheckBoxEnergyExchange = null;
	private javax.swing.JComboBox ivjJComboBoxEnergyExchangeRights = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerContactLoginPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJCheckBoxEnableLogin()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxReadmeterRights()) 
		connEtoC8(e);
	if (e.getSource() == getJComboBoxCurtailmentRights()) 
		connEtoC9(e);
	if (e.getSource() == getJComboBoxLoadControlRights()) 
		connEtoC10(e);
	if (e.getSource() == getJCheckBoxReadmeter()) 
		connEtoC5(e);
	if (e.getSource() == getJCheckBoxCurtailment()) 
		connEtoC6(e);
	if (e.getSource() == getJCheckBoxLoadControl()) 
		connEtoC7(e);
	if (e.getSource() == getJCheckBoxEnergyExchange()) 
		connEtoC11(e);
	if (e.getSource() == getJComboBoxEnergyExchangeRights()) 
		connEtoC12(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldUserID()) 
		connEtoC1(e);
	if (e.getSource() == getJPasswordFieldPassword()) 
		connEtoC2(e);
	if (e.getSource() == getJPasswordFieldRetypePassword()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldFirstName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (JComboBoxLoadControlRights.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (JCheckBoxEnergyExchange.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.jCheckBoxEnergyExchange_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxEnergyExchange_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC12:  (JComboBoxEnergyExchange.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JTextFieldLastName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JTextFieldPhone1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JCheckBoxEnableLogin.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.jCheckBoxEnableLogin_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxEnableLogin_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JComboBoxRecipient.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxReadmeter_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JCheckBoxCurtailment.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxCurtailment_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JCheckBoxReadmeter.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxLoadControl_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (JComboBoxReadmeterRights.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (JComboBoxCurtailmentRights.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JCheckBoxCurtailment property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxCurtailment() {
	if (ivjJCheckBoxCurtailment == null) {
		try {
			ivjJCheckBoxCurtailment = new javax.swing.JCheckBox();
			ivjJCheckBoxCurtailment.setName("JCheckBoxCurtailment");
			ivjJCheckBoxCurtailment.setText("Curtailment");
			ivjJCheckBoxCurtailment.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxCurtailment;
}
/**
 * Return the JCheckBoxEnableLogin property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
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
 * Return the JCheckBoxEnergyExchange property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxEnergyExchange() {
	if (ivjJCheckBoxEnergyExchange == null) {
		try {
			ivjJCheckBoxEnergyExchange = new javax.swing.JCheckBox();
			ivjJCheckBoxEnergyExchange.setName("JCheckBoxEnergyExchange");
			ivjJCheckBoxEnergyExchange.setText("EnergyExchange");
			ivjJCheckBoxEnergyExchange.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnergyExchange;
}
/**
 * Return the JCheckBoxLoadControl property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxLoadControl() {
	if (ivjJCheckBoxLoadControl == null) {
		try {
			ivjJCheckBoxLoadControl = new javax.swing.JCheckBox();
			ivjJCheckBoxLoadControl.setName("JCheckBoxLoadControl");
			ivjJCheckBoxLoadControl.setText("LoadControl");
			ivjJCheckBoxLoadControl.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxLoadControl;
}
/**
 * Return the JCheckBoxReadmeter property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxReadmeter() {
	if (ivjJCheckBoxReadmeter == null) {
		try {
			ivjJCheckBoxReadmeter = new javax.swing.JCheckBox();
			ivjJCheckBoxReadmeter.setName("JCheckBoxReadmeter");
			ivjJCheckBoxReadmeter.setText("Readmeter");
			ivjJCheckBoxReadmeter.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxReadmeter;
}
/**
 * Return the JComboBoxCurtailmentRights property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCurtailmentRights() {
	if (ivjJComboBoxCurtailmentRights == null) {
		try {
			ivjJComboBoxCurtailmentRights = new javax.swing.JComboBox();
			ivjJComboBoxCurtailmentRights.setName("JComboBoxCurtailmentRights");
			ivjJComboBoxCurtailmentRights.setEnabled(false);
			// user code begin {1}

			ivjJComboBoxCurtailmentRights.addItem( CustomerLogin.RIGHTS_ALL_CONTROL );
			ivjJComboBoxCurtailmentRights.addItem( CustomerLogin.RIGHTS_VIEW_ONLY );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxCurtailmentRights;
}
/**
 * Return the JComboBoxEnergyExchange property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxEnergyExchangeRights() {
	if (ivjJComboBoxEnergyExchangeRights == null) {
		try {
			ivjJComboBoxEnergyExchangeRights = new javax.swing.JComboBox();
			ivjJComboBoxEnergyExchangeRights.setName("JComboBoxEnergyExchangeRights");
			ivjJComboBoxEnergyExchangeRights.setEnabled(false);
			// user code begin {1}

			ivjJComboBoxEnergyExchangeRights.addItem( CustomerLogin.RIGHTS_ALL_CONTROL );
			ivjJComboBoxEnergyExchangeRights.addItem( CustomerLogin.RIGHTS_VIEW_ONLY );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxEnergyExchangeRights;
}
/**
 * Return the JComboBoxLoadControlRights property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxLoadControlRights() {
	if (ivjJComboBoxLoadControlRights == null) {
		try {
			ivjJComboBoxLoadControlRights = new javax.swing.JComboBox();
			ivjJComboBoxLoadControlRights.setName("JComboBoxLoadControlRights");
			ivjJComboBoxLoadControlRights.setEnabled(false);
			// user code begin {1}
			
			ivjJComboBoxLoadControlRights.addItem( CustomerLogin.RIGHTS_ALL_CONTROL );
			ivjJComboBoxLoadControlRights.addItem( CustomerLogin.RIGHTS_VIEW_ONLY );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxLoadControlRights;
}
/**
 * Return the JComboBoxReadmeterRights property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxReadmeterRights() {
	if (ivjJComboBoxReadmeterRights == null) {
		try {
			ivjJComboBoxReadmeterRights = new javax.swing.JComboBox();
			ivjJComboBoxReadmeterRights.setName("JComboBoxReadmeterRights");
			ivjJComboBoxReadmeterRights.setEnabled(false);
			// user code begin {1}

			ivjJComboBoxReadmeterRights.addItem( CustomerLogin.RIGHTS_ALL_CONTROL );
			ivjJComboBoxReadmeterRights.addItem( CustomerLogin.RIGHTS_VIEW_ONLY );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxReadmeterRights;
}
/**
 * Return the JLabelNormalStateAndThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNormalPassword() {
	if (ivjJLabelNormalPassword == null) {
		try {
			ivjJLabelNormalPassword = new javax.swing.JLabel();
			ivjJLabelNormalPassword.setName("JLabelNormalPassword");
			ivjJLabelNormalPassword.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNormalPassword.setText("Password:");
			ivjJLabelNormalPassword.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNormalPassword;
}
/**
 * Return the JLabelPhone1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRetypePassword() {
	if (ivjJLabelRetypePassword == null) {
		try {
			ivjJLabelRetypePassword = new javax.swing.JLabel();
			ivjJLabelRetypePassword.setName("JLabelRetypePassword");
			ivjJLabelRetypePassword.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelRetypePassword.setText("Retype Password:");
			ivjJLabelRetypePassword.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRetypePassword;
}
/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelUserName() {
	if (ivjJLabelUserName == null) {
		try {
			ivjJLabelUserName = new javax.swing.JLabel();
			ivjJLabelUserName.setName("JLabelUserName");
			ivjJLabelUserName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelUserName.setText("User ID:");
			ivjJLabelUserName.setEnabled(false);
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
/**
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
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
			constraintsJLabelUserName.ipadx = 27;
			constraintsJLabelUserName.ipady = -2;
			constraintsJLabelUserName.insets = new java.awt.Insets(5, 22, 8, 47);
			getJPanelLoginPanel().add(getJLabelUserName(), constraintsJLabelUserName);

			java.awt.GridBagConstraints constraintsJLabelNormalPassword = new java.awt.GridBagConstraints();
			constraintsJLabelNormalPassword.gridx = 1; constraintsJLabelNormalPassword.gridy = 2;
			constraintsJLabelNormalPassword.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNormalPassword.ipadx = 11;
			constraintsJLabelNormalPassword.ipady = -2;
			constraintsJLabelNormalPassword.insets = new java.awt.Insets(9, 22, 6, 48);
			getJPanelLoginPanel().add(getJLabelNormalPassword(), constraintsJLabelNormalPassword);

			java.awt.GridBagConstraints constraintsJTextFieldUserID = new java.awt.GridBagConstraints();
			constraintsJTextFieldUserID.gridx = 2; constraintsJTextFieldUserID.gridy = 1;
			constraintsJTextFieldUserID.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldUserID.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldUserID.weightx = 1.0;
			constraintsJTextFieldUserID.ipadx = 230;
			constraintsJTextFieldUserID.insets = new java.awt.Insets(3, 3, 7, 17);
			getJPanelLoginPanel().add(getJTextFieldUserID(), constraintsJTextFieldUserID);

			java.awt.GridBagConstraints constraintsJLabelRetypePassword = new java.awt.GridBagConstraints();
			constraintsJLabelRetypePassword.gridx = 1; constraintsJLabelRetypePassword.gridy = 3;
			constraintsJLabelRetypePassword.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelRetypePassword.ipadx = 8;
			constraintsJLabelRetypePassword.ipady = -2;
			constraintsJLabelRetypePassword.insets = new java.awt.Insets(7, 22, 7, 2);
			getJPanelLoginPanel().add(getJLabelRetypePassword(), constraintsJLabelRetypePassword);

			java.awt.GridBagConstraints constraintsJPasswordFieldPassword = new java.awt.GridBagConstraints();
			constraintsJPasswordFieldPassword.gridx = 2; constraintsJPasswordFieldPassword.gridy = 2;
			constraintsJPasswordFieldPassword.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPasswordFieldPassword.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPasswordFieldPassword.weightx = 1.0;
			constraintsJPasswordFieldPassword.ipadx = 168;
			constraintsJPasswordFieldPassword.insets = new java.awt.Insets(7, 3, 5, 79);
			getJPanelLoginPanel().add(getJPasswordFieldPassword(), constraintsJPasswordFieldPassword);

			java.awt.GridBagConstraints constraintsJPasswordFieldRetypePassword = new java.awt.GridBagConstraints();
			constraintsJPasswordFieldRetypePassword.gridx = 2; constraintsJPasswordFieldRetypePassword.gridy = 3;
			constraintsJPasswordFieldRetypePassword.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPasswordFieldRetypePassword.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPasswordFieldRetypePassword.weightx = 1.0;
			constraintsJPasswordFieldRetypePassword.ipadx = 168;
			constraintsJPasswordFieldRetypePassword.insets = new java.awt.Insets(5, 3, 6, 79);
			getJPanelLoginPanel().add(getJPasswordFieldRetypePassword(), constraintsJPasswordFieldRetypePassword);

			java.awt.GridBagConstraints constraintsJPanelLoginType = new java.awt.GridBagConstraints();
			constraintsJPanelLoginType.gridx = 1; constraintsJPanelLoginType.gridy = 4;
			constraintsJPanelLoginType.gridwidth = 2;
			constraintsJPanelLoginType.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelLoginType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelLoginType.weightx = 1.0;
			constraintsJPanelLoginType.weighty = 1.0;
			constraintsJPanelLoginType.ipadx = -10;
			constraintsJPanelLoginType.ipady = -26;
			constraintsJPanelLoginType.insets = new java.awt.Insets(6, 16, 13, 26);
			getJPanelLoginPanel().add(getJPanelLoginType(), constraintsJPanelLoginType);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoginPanel;
}
/**
 * Return the JPanelLoginType property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLoginType() {
	if (ivjJPanelLoginType == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Type");
			ivjJPanelLoginType = new javax.swing.JPanel();
			ivjJPanelLoginType.setName("JPanelLoginType");
			ivjJPanelLoginType.setBorder(ivjLocalBorder1);
			ivjJPanelLoginType.setLayout(new java.awt.GridBagLayout());
			ivjJPanelLoginType.setEnabled(true);

			java.awt.GridBagConstraints constraintsJCheckBoxReadmeter = new java.awt.GridBagConstraints();
			constraintsJCheckBoxReadmeter.gridx = 1; constraintsJCheckBoxReadmeter.gridy = 1;
			constraintsJCheckBoxReadmeter.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxReadmeter.ipadx = 25;
			constraintsJCheckBoxReadmeter.insets = new java.awt.Insets(28, 14, 6, 1);
			getJPanelLoginType().add(getJCheckBoxReadmeter(), constraintsJCheckBoxReadmeter);

			java.awt.GridBagConstraints constraintsJCheckBoxCurtailment = new java.awt.GridBagConstraints();
			constraintsJCheckBoxCurtailment.gridx = 1; constraintsJCheckBoxCurtailment.gridy = 2;
			constraintsJCheckBoxCurtailment.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxCurtailment.ipadx = 22;
			constraintsJCheckBoxCurtailment.insets = new java.awt.Insets(5, 14, 4, 1);
			getJPanelLoginType().add(getJCheckBoxCurtailment(), constraintsJCheckBoxCurtailment);

			java.awt.GridBagConstraints constraintsJCheckBoxLoadControl = new java.awt.GridBagConstraints();
			constraintsJCheckBoxLoadControl.gridx = 1; constraintsJCheckBoxLoadControl.gridy = 3;
			constraintsJCheckBoxLoadControl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxLoadControl.ipadx = 19;
			constraintsJCheckBoxLoadControl.insets = new java.awt.Insets(5, 14, 6, 1);
			getJPanelLoginType().add(getJCheckBoxLoadControl(), constraintsJCheckBoxLoadControl);

			java.awt.GridBagConstraints constraintsJComboBoxReadmeterRights = new java.awt.GridBagConstraints();
			constraintsJComboBoxReadmeterRights.gridx = 2; constraintsJComboBoxReadmeterRights.gridy = 1;
			constraintsJComboBoxReadmeterRights.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxReadmeterRights.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxReadmeterRights.weightx = 1.0;
			constraintsJComboBoxReadmeterRights.ipadx = 43;
			constraintsJComboBoxReadmeterRights.insets = new java.awt.Insets(29, 2, 4, 59);
			getJPanelLoginType().add(getJComboBoxReadmeterRights(), constraintsJComboBoxReadmeterRights);

			java.awt.GridBagConstraints constraintsJComboBoxLoadControlRights = new java.awt.GridBagConstraints();
			constraintsJComboBoxLoadControlRights.gridx = 2; constraintsJComboBoxLoadControlRights.gridy = 3;
			constraintsJComboBoxLoadControlRights.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxLoadControlRights.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxLoadControlRights.weightx = 1.0;
			constraintsJComboBoxLoadControlRights.ipadx = 43;
			constraintsJComboBoxLoadControlRights.insets = new java.awt.Insets(6, 2, 4, 59);
			getJPanelLoginType().add(getJComboBoxLoadControlRights(), constraintsJComboBoxLoadControlRights);

			java.awt.GridBagConstraints constraintsJComboBoxCurtailmentRights = new java.awt.GridBagConstraints();
			constraintsJComboBoxCurtailmentRights.gridx = 2; constraintsJComboBoxCurtailmentRights.gridy = 2;
			constraintsJComboBoxCurtailmentRights.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxCurtailmentRights.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxCurtailmentRights.weightx = 1.0;
			constraintsJComboBoxCurtailmentRights.ipadx = 43;
			constraintsJComboBoxCurtailmentRights.insets = new java.awt.Insets(4, 2, 4, 59);
			getJPanelLoginType().add(getJComboBoxCurtailmentRights(), constraintsJComboBoxCurtailmentRights);

			java.awt.GridBagConstraints constraintsJCheckBoxEnergyExchange = new java.awt.GridBagConstraints();
			constraintsJCheckBoxEnergyExchange.gridx = 1; constraintsJCheckBoxEnergyExchange.gridy = 4;
			constraintsJCheckBoxEnergyExchange.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxEnergyExchange.ipadx = -7;
			constraintsJCheckBoxEnergyExchange.insets = new java.awt.Insets(4, 14, 25, 1);
			getJPanelLoginType().add(getJCheckBoxEnergyExchange(), constraintsJCheckBoxEnergyExchange);

			java.awt.GridBagConstraints constraintsJComboBoxEnergyExchangeRights = new java.awt.GridBagConstraints();
			constraintsJComboBoxEnergyExchangeRights.gridx = 2; constraintsJComboBoxEnergyExchangeRights.gridy = 4;
			constraintsJComboBoxEnergyExchangeRights.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxEnergyExchangeRights.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxEnergyExchangeRights.weightx = 1.0;
			constraintsJComboBoxEnergyExchangeRights.ipadx = 43;
			constraintsJComboBoxEnergyExchangeRights.insets = new java.awt.Insets(5, 2, 23, 59);
			getJPanelLoginType().add(getJComboBoxEnergyExchangeRights(), constraintsJComboBoxEnergyExchangeRights);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoginType;
}
/**
 * Return the JPasswordFieldPassword property value.
 * @return javax.swing.JPasswordField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPasswordField getJPasswordFieldPassword() {
	if (ivjJPasswordFieldPassword == null) {
		try {
			ivjJPasswordFieldPassword = new javax.swing.JPasswordField();
			ivjJPasswordFieldPassword.setName("JPasswordFieldPassword");
			ivjJPasswordFieldPassword.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPasswordFieldPassword;
}
/**
 * Return the JPasswordFieldRetypePassword property value.
 * @return javax.swing.JPasswordField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPasswordField getJPasswordFieldRetypePassword() {
	if (ivjJPasswordFieldRetypePassword == null) {
		try {
			ivjJPasswordFieldRetypePassword = new javax.swing.JPasswordField();
			ivjJPasswordFieldRetypePassword.setName("JPasswordFieldRetypePassword");
			ivjJPasswordFieldRetypePassword.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPasswordFieldRetypePassword;
}
/**
 * Return the JTextFieldFirstName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldUserID() {
	if (ivjJTextFieldUserID == null) {
		try {
			ivjJTextFieldUserID = new javax.swing.JTextField();
			ivjJTextFieldUserID.setName("JTextFieldUserID");
			ivjJTextFieldUserID.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldUserID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 11:48:42 AM)
 * @return java.lang.String
 */
private String getLoginTypeString()
{
	StringBuffer retValue = new StringBuffer("");
	
	//check for the curtailment login
	if( getJCheckBoxCurtailment().isSelected() )
	{
		if( getJComboBoxCurtailmentRights().getSelectedItem().toString().equalsIgnoreCase(CustomerLogin.RIGHTS_VIEW_ONLY) )
			retValue.append( "V" + CustomerLogin.CURTAILMENT );
		else
			retValue.append( CustomerLogin.CURTAILMENT );
	}

	//check for the loadcontrol login
	if( getJCheckBoxLoadControl().isSelected() )
	{
		if( retValue.length() > 0 )
			retValue.append(",");
			
		if( getJComboBoxLoadControlRights().getSelectedItem().toString().equalsIgnoreCase(CustomerLogin.RIGHTS_VIEW_ONLY) )
			retValue.append( "V" + CustomerLogin.LOADCONTROL );
		else
			retValue.append( CustomerLogin.LOADCONTROL );
	}


	//check for the readmeter login
	if( getJCheckBoxReadmeter().isSelected() )
	{
		if( retValue.length() > 0 )
			retValue.append(",");
			
		if( getJComboBoxReadmeterRights().getSelectedItem().toString().equalsIgnoreCase(CustomerLogin.RIGHTS_VIEW_ONLY) )
			retValue.append( "V" + CustomerLogin.READMETER );
		else
			retValue.append( CustomerLogin.READMETER );
	}

	//check for the energyexchange login
	if( getJCheckBoxEnergyExchange().isSelected() )
	{
		if( retValue.length() > 0 )
			retValue.append(",");
			
		if( getJComboBoxEnergyExchangeRights().getSelectedItem().toString().equalsIgnoreCase(CustomerLogin.RIGHTS_VIEW_ONLY) )
			retValue.append( "V" + CustomerLogin.ENERGYEXCHANGE );
		else
			retValue.append( CustomerLogin.ENERGYEXCHANGE );
	}
	
		
	if( retValue.length() <= 0 )
		return "(none)";
	else	
		return retValue.toString();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return null;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	CustomerLogin login = ((CustomerContact)o).getCustomerLogin();

	if( getJCheckBoxEnableLogin().isSelected() )
	{
		
		if( ((CustomerContact)o).getCustomerLogin().getLoginID().intValue() == CustomerLogin.NONE_LOGIN_ID )
			((CustomerContact)o).setLogInID( login.getNextLoginID() );
		
		login.setStatus( CustomerLogin.STATUS_ENABLED );
		
		if( getJTextFieldUserID().getText() != null && getJTextFieldUserID().getText().length() > 0 )
			login.setUserName( getJTextFieldUserID().getText() );

		if( getJPasswordFieldPassword().getPassword() != null && getJPasswordFieldPassword().getPassword().length > 0 )
			login.setUserPassword( new String(getJPasswordFieldPassword().getPassword()) );

		login.setLoginType( getLoginTypeString() );
	}
	else
	{
		//if we have a valid LoginID, just disable the login and save any changes made
		if( ((CustomerContact)o).getCustomerLogin().getLoginID().intValue() != CustomerLogin.NONE_LOGIN_ID )
		{
			login.setStatus( CustomerLogin.STATUS_DISABLED );
			
			if( getJTextFieldUserID().getText() != null && getJTextFieldUserID().getText().length() > 0 )
				login.setUserName( getJTextFieldUserID().getText() );

			if( getJPasswordFieldPassword().getPassword() != null && getJPasswordFieldPassword().getPassword().length > 0 )
				login.setUserPassword( new String(getJPasswordFieldPassword().getPassword()) );

			login.setLoginType( getLoginTypeString() );
		}
	}
	
	return o;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	// user code end
	getJTextFieldUserID().addCaretListener(this);
	getJPasswordFieldPassword().addCaretListener(this);
	getJPasswordFieldRetypePassword().addCaretListener(this);
	getJCheckBoxEnableLogin().addActionListener(this);
	getJComboBoxReadmeterRights().addActionListener(this);
	getJComboBoxCurtailmentRights().addActionListener(this);
	getJComboBoxLoadControlRights().addActionListener(this);
	getJCheckBoxReadmeter().addActionListener(this);
	getJCheckBoxCurtailment().addActionListener(this);
	getJCheckBoxLoadControl().addActionListener(this);
	getJCheckBoxEnergyExchange().addActionListener(this);
	getJComboBoxEnergyExchangeRights().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerContactLoginPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJPanelLoginPanel = new java.awt.GridBagConstraints();
		constraintsJPanelLoginPanel.gridx = 1; constraintsJPanelLoginPanel.gridy = 2;
		constraintsJPanelLoginPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLoginPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelLoginPanel.weightx = 1.0;
		constraintsJPanelLoginPanel.weighty = 1.0;
		constraintsJPanelLoginPanel.ipadx = -10;
		constraintsJPanelLoginPanel.ipady = -8;
		constraintsJPanelLoginPanel.insets = new java.awt.Insets(3, 8, 10, 8);
		add(getJPanelLoginPanel(), constraintsJPanelLoginPanel);

		java.awt.GridBagConstraints constraintsJCheckBoxEnableLogin = new java.awt.GridBagConstraints();
		constraintsJCheckBoxEnableLogin.gridx = 1; constraintsJCheckBoxEnableLogin.gridy = 1;
		constraintsJCheckBoxEnableLogin.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxEnableLogin.ipadx = 32;
		constraintsJCheckBoxEnableLogin.ipady = -2;
		constraintsJCheckBoxEnableLogin.insets = new java.awt.Insets(10, 8, 2, 251);
		add(getJCheckBoxEnableLogin(), constraintsJCheckBoxEnableLogin);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJCheckBoxEnableLogin().isSelected() )
	{

		if( (getJTextFieldUserID().getText() == null || getJTextFieldUserID().getText().length() <= 0)
			 || (getJPasswordFieldPassword().getPassword() == null || getJPasswordFieldPassword().getPassword().length <= 0)
			 || (getJPasswordFieldRetypePassword().getPassword() == null || getJPasswordFieldRetypePassword().getPassword().length <= 0) )
		{
			setErrorString("The Userid text field, Password text field and Retype Password text field must be filled in");
			return false;
		}

		if( getJPasswordFieldPassword().getPassword().length == getJPasswordFieldRetypePassword().getPassword().length )
		{
			for( int i = 0; i < getJPasswordFieldPassword().getPassword().length; i++)
			{
				if( getJPasswordFieldPassword().getPassword()[i] == getJPasswordFieldRetypePassword().getPassword()[i] )
					continue;
				else
				{
					setErrorString("The Retyped Password text field must be the same as the Password text field");
					return false;
				}
				
			}
		}
		else
		{
			setErrorString("The Retyped Password text field must be the same as the Password text field");
			return false;
		}

	}
	
	return true;
}
/**
 * Comment
 */
public void jCheckBoxCurtailment_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJComboBoxCurtailmentRights().setEnabled( getJCheckBoxCurtailment().isSelected() );
	
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jCheckBoxEnableLogin_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJTextFieldUserID().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJPasswordFieldPassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJPasswordFieldRetypePassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJLabelNormalPassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJLabelRetypePassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJLabelUserName().setEnabled( getJCheckBoxEnableLogin().isSelected() );


	getJCheckBoxCurtailment().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJCheckBoxLoadControl().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJCheckBoxReadmeter().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJCheckBoxEnergyExchange().setEnabled( getJCheckBoxEnableLogin().isSelected() );

	
	
	getJComboBoxCurtailmentRights().setEnabled( 
				getJCheckBoxEnableLogin().isSelected() && getJCheckBoxCurtailment().isSelected() );
	
	getJComboBoxLoadControlRights().setEnabled( 
				getJCheckBoxEnableLogin().isSelected() && getJCheckBoxLoadControl().isSelected() );
	
	getJComboBoxReadmeterRights().setEnabled( 
				getJCheckBoxEnableLogin().isSelected() && getJCheckBoxReadmeter().isSelected() );
	
	getJComboBoxEnergyExchangeRights().setEnabled( 
				getJCheckBoxEnableLogin().isSelected() && getJCheckBoxEnergyExchange().isSelected() );
	
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jCheckBoxEnergyExchange_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJComboBoxEnergyExchangeRights().setEnabled( getJCheckBoxEnergyExchange().isSelected() );
	
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jCheckBoxLoadControl_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJComboBoxLoadControlRights().setEnabled( getJCheckBoxLoadControl().isSelected() );
	
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jCheckBoxReadmeter_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJComboBoxReadmeterRights().setEnabled( getJCheckBoxReadmeter().isSelected() );
	
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
		CustomerContactBasePanel aCustomerContactBasePanel;
		aCustomerContactBasePanel = new CustomerContactBasePanel();
		frame.setContentPane(aCustomerContactBasePanel);
		frame.setSize(aCustomerContactBasePanel.getSize());
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
public void setValue(Object o) 
{
	if( o == null )
		return;

	CustomerLogin login = ((CustomerContact)o).getCustomerLogin();

	if( login.getLoginID().intValue() > CustomerLogin.NONE_LOGIN_ID )
	{
		if( !login.getStatus().equalsIgnoreCase(CustomerLogin.STATUS_DISABLED) )
			getJCheckBoxEnableLogin().doClick();

		getJTextFieldUserID().setText( login.getUserName() );
		getJPasswordFieldPassword().setText( login.getUserPassword() );
		getJPasswordFieldRetypePassword().setText( login.getUserPassword() );

		int pos = 0;
		if( (pos = login.getLoginType().indexOf(CustomerLogin.READMETER)) != -1 )
		{
			if( getJCheckBoxReadmeter().isEnabled() )
				getJCheckBoxReadmeter().doClick();
			else
				getJCheckBoxReadmeter().setSelected(true);
			
			if( pos > 0 )
				if( login.getLoginType().charAt( pos - 1 ) == 'V' )
					getJComboBoxReadmeterRights().setSelectedItem( CustomerLogin.RIGHTS_VIEW_ONLY );
		}

		if( (pos = login.getLoginType().indexOf(CustomerLogin.CURTAILMENT)) != -1 )
		{
			if( getJCheckBoxCurtailment().isEnabled() )
				getJCheckBoxCurtailment().doClick();
			else
				getJCheckBoxCurtailment().setSelected(true);

			if( pos > 0 )
				if( login.getLoginType().charAt( pos - 1 ) == 'V' )
					getJComboBoxCurtailmentRights().setSelectedItem( CustomerLogin.RIGHTS_VIEW_ONLY );
		}

		if( (pos = login.getLoginType().indexOf(CustomerLogin.LOADCONTROL)) != -1 )
		{
			
			if( getJCheckBoxLoadControl().isEnabled() )
				getJCheckBoxLoadControl().doClick();
			else
				getJCheckBoxLoadControl().setSelected(true);
				
			if( pos > 0 )
				if( login.getLoginType().charAt( pos - 1 ) == 'V' )
					getJComboBoxLoadControlRights().setSelectedItem( CustomerLogin.RIGHTS_VIEW_ONLY );
		}

		if( (pos = login.getLoginType().indexOf(CustomerLogin.ENERGYEXCHANGE)) != -1 )
		{
			
			if( getJCheckBoxEnergyExchange().isEnabled() )
				getJCheckBoxEnergyExchange().doClick();
			else
				getJCheckBoxEnergyExchange().setSelected(true);
				
			if( pos > 0 )
				if( login.getLoginType().charAt( pos - 1 ) == 'V' )
					getJComboBoxEnergyExchangeRights().setSelectedItem( CustomerLogin.RIGHTS_VIEW_ONLY );
		}		
	
		//getJComboBoxLoginType().setSelectedItem( login.getLoginType() );
	}
	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GECF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BF4D45535D1C1D1D103C6C50C9AA1E25048CF2A345296D26AC30B563671C97B28CDABFD5277C03126FDD4D1AF7987798488BF858990A0E08C81837910047C492F9AA58D3186491039C986A6B3634C8D190428EF6FF36EBE774E4C4D8422F45565665E334F7E1E3D4F3967B711D2370744AF0FC915246571127ADF6EF2C9DA7839A4D5677E61AD4285489189126E3F9DGEB25183AB8F886C2DD604C
	080494A9A2B9894A21D0EE1C9E1130895E57CBAD59211F424BGFA7A21AEA3E862077E755C381D74A432728BC7D642F3B5C098607099AD237E570FECD671F3D5BC417298C9DA540EB49BF3372A38BCA8B7G04814CE867741170CCE61A07976429F4AD23C22468A777462CC2B9BA1984B99D1C37D9FD26C91316CB1D286BD86ECA15A7906A66G207331D2FAC6BDBC8B9B4EF59437593B325ACCB61B5D2648ED1DD9325922581DD9E6793C25CD4EEA6BF6A976AE5959863526B6252020DCC514F3C43D3D43E2EBB0
	59E4EBBF53F7CDEE9213AFC995D0CE32A06E3AD234E1A6146781AC9178CF4A90DF8F6FE600FE8B79DD3437D0752B7942A42965B9DB6C1A817C8E32F0FFE7D8B43F033ADFCB5C42E274F49A46E2A954EDBD9B11108D508820G84813C4BF263596D7B61D9FAC6A937F7F559ED258EC719556C7C3B5DACDB613D20GEAD45C27F21B5DE916A49E7F4A5CCC83FB7C8167BBF149FC3CCD42BE24DC887FF43CB47F613A18C403B6199F169D93FE1637456C33DA1BF05DA387543D602CE72EFA6ABEA969F557DFED0F0DB7
	50BD6D59FDB1CBD5B9AB4FE93A8B4FE12C5FE4B910896F4B557C6078A4168381BABC4F5BC0C60F65C0280BBEC739513C1AEC290F9BA3259C2CB7EC07519125B14DD6EE03436AED4B5AF2149DEC65BA3788DB76B07CBC9D1E5BF212590865542E0804A8G1EDF4F6D44FE9CEE03FE8590853094E033BDA2A187205ACEB976DE5A010B4C318613D3A96B36D84D32D3623C4F25ED04276CD61C269A1BCBB1595A64A61353E2BAE315DD12CA931506EDF046036F0FAC775BC047F159A6BBCD0A458E5D54E96A12955969
	62B4AF252DC59A274CEA9BADDDB2A868F2C8A4772FE96B61E9B53914FFB84CA6C556A8F87D1BE998137326A610888254B6DAAB4525E3FBF5027F81G01229DFAF7203CBA598923C5C1C13D5DFE2E5B51081EC8D1F6EC67F1EC6C88057799F6EAE3E9B762529D9189058E12B3F947BA1D1C8ACB9B1AEFF276D29F0BAB43313A41C1B973D2B365CC452A7B244CE7EB8DF386416D60399268706EBB2B19CC340B17EFABED5671B5B80C7B4FD0016B186421A376BB143FDA444A3687E3B59D6C0F840896ED7C3FBB52F8
	4E1619DCB2E56947CEC8B0592C35B837F3D3F9365A1E712A6D5BG567078C99873DDG3B817682ECG58A4FC4CE43CAB3F04BE835082B08CA09CA092E099C03AB3A2219EC0818886186D24981CAA1B290BA369567B28370174D25DB65D3B5E3E6F320DAFBE56014C970A3111AFEF832CD2FD5A2446CFBFCE533B5E5FFF35AFBCF7FE3F93ED144F4BB625C2F648B6B33C38543A05BB51D6F5BCEB378B1E17985E8163DFB98CB8AE010F5E097E189445E9B9532D48A7E4C53158BA347AE5CC172B7B0CC39BBAC9D76C
	AEECC7FB8F0CFF02BEE9970E9DB01B3149AC09850BDA3F020DDD32D56E82234B6D5DB60563D352317F1F13FB5531B3B59DA70967CD56EE9C07791CEA3289E364F0DA3A20EB60D0AEB538F0DC7BC16EE6DD9BAD027C4539E1BA546539E80C6A59C5E3D4FA5F3D524C533BE30C3E31236F2B08996D62E3CD38CB9B2338CD713B0DEDFF1B0D21AA5E954BF06FDC407C99F0B2A75178F07FDE14B997F4C581ACF55178F04DEE2C27B1D89D984A6DD6ABEFD6515E4A1E940C0F7CE5378C1FAE9C6C6BC49BDE58334E2B2E
	C9EDCFDEFF559EECCF2B6EBB912AE0FBF616537CB9854A99G558AEF53E18AEF93CE77FE8551AD01F2D2B750F69B513DA4684CD05FBFA05D58CA228B067AE9G11AADD6AF9BDDDFB15108778FE006073C672769FA73A79D09FBDA05D7302AE35A7A2A133E7A03A1B2B092E9EE84CG43FAB85D6C9EBDDDD30D106786F9EE0D2E5E2D27DBD1AB7C857CB49DDD24875D0F84DDFAAFFC2781DAFAB9DDF02F1E6E4ABA918F40A775C1BB77F97A417BD162E91C279A2DCDBA3AADD95D0A853F7AA6858690C73722DBFB24
	67E11E63385959C7EB0C0823183FA1D01E8E70CC9F7537DF2C22B5C6503990A9EA5CBB832E2D967629F35A8BDAFFCB3B0A56982FE4204EFA28EB39C0FD634DBD7AF9CE294BE5EF3330A9161A578F6D5577C70E1B3AF785BC5BADCE17D28383261B7A40743DE8AF4E41D434BAA79D45662A58DE8E509B86D0520F7861DBD2F536C781BE897019G3C5FAFAD4739E81B49A9ABD96CDFBE354BB8EBBAEFF2E739FAE0F04DE2A3F7D6B956D6626BBEB5AE33772D63B64A2C65D54EFDCD34CE983B3EC455BBF762982970
	1B75B1C66B3691EB36450C6867EDB939DFEB537E2989310B3EF8BD117B57D8C17EAD831A1C2F483FAFF2757ED93F627E0D7E0A7CFB2CE2A87EF11D1BAAC9E7B848DBA0F47E3D4FC867562FA392B85F4F84DFB660BA3FA63E9F9E567305FC4D6735F33EA6DB8FD59D0227098D18D97C51A0BB5B6D4EAE594C8C4EB275A82A35252C1A4C6571F838369C1EE74BBB6536F3E5F6F7258D47C8B6061FAE7512D6BF30382398C06647CDD56447FC30B1759B72A361025E0FFA40ABG233FA1BF7EBAE4BF385E9347C53B03
	4C85C26F75FDC6FA3786D03BA7D60BF687DCE7G713D1CE74497A2789E93FC53G37C870C596F9478B79522FA0BFF756FC3BF84DC9AF55476BD359E4E64B31CB0E165AEE5F54109FF9E023638A72A3F623119FE1D7505E4F7A9C0F3CA96FF6AAA60B95872321654D0735E4478C5091AD6CE84DB732A373CA3263891B079D75F61399A7A2CE3BF5E8F63CA86CA8819D6EAB490E9B730C6C18FCA54575233AEF9757AFBF29726C0732332337525D56E932F548179EDC664B03F5644BEC30F31970E53BE11F48390AFC
	D9D7BFD4DF78BAE4DF9EFEFB2B2F525633AD3AF747D53E6BDC3EF7F224906D9DF6955FBB099778D2069F2D43F37F669C95FD9E6AD20651FABFE0BF4E1595A8878204814C8408821006537735A483B7EB7D6E275440FC41E9B3D9CFF8AC8CE8DFA3835B8A6672EA79358C34034D65A5EA075B4B3D774A368E7774BD4F2B4C796E73614B72AA37F871719C5A1D01F3A3B66751FD2A8A8AEA9BE12DD4E5112DE63E1668AD542FA514617C1BB29A1E61GFC9EB4E3CB92654EF01A6FFCD5251F6F4CD8F3A8866756C673
	1DE52AFDDB2F563E4D19E2CFF5FFA65A5982F5EE00C000F000E7G968124DCC3B19A362FE830983144EB3417832E9A6678655EEBB251267F2C98DAACBAFD783CE311FD0DD1AC9EF9AF4DB8968D30B26D31BB4DAC9E3C2DEF4D25362E3E0676A536D58E4D5E77FD783C6D8D7032175BB02C0FEC98ADEC784590ED781D8F5FFBDE65E78C5B2C3869DD63B623D1D135752D43B4C7DFA8EC3D35EAE8360E736173EE2F3491C636F6D88732555EF5460E36723EF6F52EF7DF43F9DF9E482C9FC1FD2D6882353DF9847973
	59712179536143676DCF70882378C70878CF93B62C2A9E1A8D6F7A70F95B30485006BD79E4C35C35E4432CA15A70F39FBE6F9CAC3C564806370BA9379A058D43EB06E643ADBEFC5EB60CB43421E0A35990AAEC482F9D1A8D47FD783CED18EFE8436D79E4C33430E17190EDF8FDD09BB22FB3326127E2ADDEF19D59B025EEE8B67C58074FBBA77B8DEDE8927DA2886A7999C38933EBAE1497885C7D8C37844AF15793EED88162B220DCF2BD59DFD808B8B31483846EB8436138007BC19C777451E6AF7B3EAD20CEFC
	EAFADF7C4EF568771E79B77C5A7D836F63866EB75E473DED3F71BEEE707E359C8FB27427DCD49F3A5F7FBEEF78FE23FD5EED4C6EA7B62EE132DDAE5945A69538B924563D2860BE384FFE6FC726AED975F93D74F4F932CAD329584B8FCA741DF8F59F0E7F0673G3167B4251072BC64FA5AF31A3F0B1DA3C2F98962C60A71F00B3F335FB4204B99691947FC4EB565831C73D7C31D99209FA088E05AC81AEB153DBF68FC38526DE7BE1C13B9F01C0FE69A4739B493774BDC76EEE71B6C9D4B6A4C4D22CE1B5555A8F2
	97B6BB27790471ABA6E7076C138B4D0C1F578D44EF4A9CA8DFF8CCC7D568E3FAC82231E40C5F7377C8E85305DEB160729AAB7572BE94722E6D73A7EF9948CA3941C8DED215DEDE0E1097F7C4AFAFDD654DD31F75DE32B4F9F3BD6461D9824F4F9F3179A956355EC07979973EA60372E400B9B7D0FE16DD7067CB9450C59B7A92F8DC2FBBDF78729FFE5BA6694608042DB79A492B28DE216F0F59E2FE1C6FCFDEB6482AB0143734DAEF5FE1E1DFCA31113CE6D506951E810672E65468659D91720EEDB41297244A88
	03678CC3F90EDA3D3CDCA1AF58505FE7D4998B603954D0DEFA1DC77B9DA4F9CF669949DBF9133A4E03E74ECDC67296FA4ABBC472BE779B5FEA1055E2A86FE49E2EBD953B5D5AE8F1607AD6F25C04F72EC2309B28B4BF4A4373B52B29575E2D00ED0118374F6651FCFC9A504F850886C88A64EBE0BEE6CF69C1596DCED3978C87A79CF21B340C715ED9C03C1DD06E8788C6BE00790134F7B2C54633AABE3AEBC7E13CAEB3914760DA033DD65E9656A2FDDBD40C0AC8E89E2579EE9D655BEF797EC8ABBBEF8C984577
	C756883C036143F4F83EEF728776ED4172B3D0D7FFB34DD766ED21794A9C01FB778862B65F921110F98BFDAB3E7F4140E37D4C8F0C477A47BFB07E264F7D407FB7BD72030146E8EE5F14DCB1473A05EC4E6EA11B8385AE240F68C2856EC0AE51C55F6269EFD2D0C44266A0429DE27781B321DCA8F037A7133C6AA09A7BFE6271DD988D7870A0ADE6B361BDB248B306BCCE430BB09E8B03F81C568A7CED8C1FFA2B066771FB1D0D3DD8AE013A393712CD1FDDA01B1AEFA35C8DECDE9A86659982773CE80BE784AED7
	50255DCE3832FC224B91381CBE229BA6F0FB591AE9B4144384EEE4B171CE9338708D449BB71AF0CD9B09F73340BD3A11F85385AEB91FF89501FBB81FF8C788DC2A18F387895CA4E17382017BBD73F7A914136EA0DC1570B7D560FE524B666BD0F68B1CD3345FC801EBBFCCF4736F207D321FFF2E3FFB19B2E6C78C4651E83FEC499DBCAF165D61FD77720DBC3AFB19BDA6A221F38C0DD187DBBD767AF0C73620603853E2AEB3F55433910FFF3B47B11F10BFE48CFD3BFF114F6616D0DE0238BB49DE779E5A5F8B9F
	BFC62A8F28B3BC4F0CEABC9413F6A7373362CE4D5E306E5792394ED98524B3G6A275F494ED38A74FB4EB89EFEBF062F41F87D0E237AFA4CED1CFBEB75A7BC6A515E52D8FDFD20C7BD3651DE151F7B76335D645B321F05C9566F658FF8D6AB85F31F5255E7C353AEA97D3A5793F91C2FAAC25FDA204E91CC31FF65946202209CA6F097CED30E4C8D267121BD9DE336752E0804BC00E6G85A088E0BAC054DDC4F71E51AD0372563B795D04423B795D833CD700FB751CEE61AE07017DEC2E274AC3592BD51DECDEF2
	177FBBE60AEA0BBF9A2EBBB1FDA05D587678FDF8DB97EB6273A7976334EA901A403BD9AEED2970C3932F6AA4E8819E8740AE35EE983CBF351C471EFF17EF68C15BE476B9B6CB0B180E0AA3144373204C754E499DCC2FDEF776BD1EFAAB207C092A176B782C07F4B45CC3BA9E6D9BCCC722CE0759CB075BC7C7F3AE6998F60FB8B3BE0277105A6DB60553BCFD140D23D09FAAE8EA1211C6018FB91FD7BD0AD3D4357DA7A67B33B1568B9C3A76C7BBA2C1871EE13E7156DBEF713E729BE6E39460236F217EF3DB9C7B
	B607403CA304F09B9228CFB5885C18C29A5BC3C228DFDCDF14864FE9D00E840886C83D977A9CC0AB40880070FB09B6185162BD17CC75FE8A5E35417BB1F81726D03DE342E917978528BE362AB2D8DFD31F288B1FA36EB56EBF53C2FC7129AAED34C11D7F7E17AEECD9A57171925F294F452B4FB5FEE2E4547FAE261F7A76C74705CB690F736E754C59C5D0BEA9697B6314C25F7EB8AB0F72F8693D54D77EB2E8DF516BDEB956D3EF5AD8CDAF5751AAF4E40EA59D1F65DFCAFFAC715251682363D49F69E89DCBFD2D
	3858332F3D0C4BB735ED4795FF1B3E26000E8FBD74BF3BC1FC1B51D626EFDD3E266F690D03690B7723AF88E4FE60216F10B67F907A4A8AB4FD6F978C26AF510F3EE9A0F32F073EF922FDE70A76DD1A67593E579D55741F729BDF64CF7623BF92F43C8A6318163B3F69754D5DD7FA4926A811D7578E1A3BFA3D51DEF915B44EBB2FEEBACCBAD24751B9F8AECEF954F554029C3C1B5CE4BA2B6E6CD532DF685056DEADCCA1BFBEF3D6EED39ACD9D920DB1A63FFBA5830FCD76A17205DDB29FDFA7D59660FCB6E39CDF
	A7AD97780F98BEC70767F36A6772E9CEDD82F521223DCE96617ABEA994569DG05G0DG8AC0E0A82DFDB38F63DDF33F7B240DF6C7BDAC0E2D3A65AC776F7102217DEEE7D6A24DDD277DE02CB47D6A3FAD9F685CBDB4D47D5D0E7ADC58380B6139FF7FD30C7EC5C3DD4AFD3026832887E885F05FC77EA907B339FF18A0250AE2EA6BE4B78C4939A60B5C2321F1F304677D18A25CEBB9E3F73245BB5FEB19D2C47BA5A3C1FEA840FC00A57771B3D0F58F738B5FBD1A6DDF90DF52F83EC713864F1C711C0F771F1BCF
	DF4AB8D58F3C2D63F97F4132826F0B215F3E82406D68DA07FC357526B332759FAE5A5CD7FD7BE72A287B486E6CB2D969901F57FF321C6D7908DB9648DFD3215EEBBB2971FEAAAB3D8E5913770DEE7CED0E672DGAA107E8758DDDBCF9AA3D9F55F2C11BC761E9A01C462F7FE9ECA572F0F8254368C9DCFF702FE59E3D4BFDD54BB0F98554F0567F6097FD6A0C9227F169C7345C96A7E5C2FCAA411573FDFC9F93D7541BB250A677E361C46D56FF8C60D67791CFD3F365EDCF8F6D9A2777B73A3986BF61355A58B59
	A1BD7A35F75E0ADC433B70881D2A4CFE75B9631ADD1211CF5C7EBB4AA4C96C397668758E4CB323DC6331676AF9F87DD355DA7D1EE47C2D28CD6EC993972D2AE0221E313354E9B1D9F7E6269D047FF376895EA5B59A6FAB393EFDB0C95B6F3C1F774139700C3A1F2FA3B95FC3F13EFDF0F69C71AD3D1F774114B0989743B89F6F03198997736D24FED8883C8DE1DA9FB40771BE7894G7F064DA9245FFDBA42483667CE61FD61DA1F7B0E64734D159EFB2A3AFB083C3EBD4E23DEF7BF1057072E5B4C6A55DB9BC27E
	27160ECE7A1D43632D6F68E9F4B2B8955912F74E03CEE70BA75D3E34CD1247BE307E2E1FCA737119EDFAD91EB4A44ECF4E2D2F5372E2DE5E45656979FA0D2767A2F96E7ED863D17AFCF3FBD603D63FE803EF7D02CF347A1AFC5F7AF5A7347A178D649FEE546A137AAE4E6666FFE8BC638F6BF978F73A0E4DCF79FB236E7D37795F75F9396F9C682F17D5877F4637968D3CFF5FD1E43CFF5FD5E43CFF6FAA72FF66FE21E8605FD6DDD9247FED951F23E4B4E1FF8D007E9F8CB093A092E0E1984DCFD20AF7CAECFEE2
	B0B729B4F760D9B17E42D5A17D2F968F6C6BEB45037FAEB676981EDB1D31AB0A3D8B6593BE799846CB31BB981263D69F43B6F2E2676424AABE75986620D5EE67E8B6877DF5917DB6B9EE824CB5A7503E6E0B5BF0EC777F3B15C60BE2154B54E1DEBD235DD1CC77D41489344FDBFC8AF504C3F9B6407C89F4F77D7897B47F0D86DC6683F48FED4C973406B0BFC0B266B05AE0A8CF83187B4045FE6F292FBD064B943A8B51BE40DD88B6BB62B6ACC827336685422EE4B76136BEC83889C7885778A04D5FA7B46377E8
	A41443G6682ACGD88A10928E5FAE00BC00FAGB3C0BFC0A0C0A840F400B9G73811604D32EEDAE45D8982E19G29EE363304F31AAC18F2EA8EBF55093645BD84735607C8566952777D48EAF05A9D32D3696558A6FE0B1879191DC7F9D282325C8F117F6EBC0A776407A8D6AF4432F3A4A8CB93A392B6CF24F8AF0A93E788006B1CC8B2F262455AFCA249B8934F4E86203C84F113AEB55E45E7A50946565F9532F53733DB3DEF115DB4F0DF4CED9A3CAF16B4315FBCC19F4209AD47D5B6615C455E5E6E12C51F2F
	EDCA91389AD7035DE5413B9F6A5D54224156E0E9130C6E0FAD9E144F7BCE9B675B436E8794CE3A387D3D862063EB57292C6F1AA7F92FE99FE3F847A46FB5ED45A90AF1BF54C5CD22BD4B66AF063696EF9FA25FBD174C477DAABC0D76D7CC76763712619BA6FB7B7B349843DA20EE66E47277CF699EF7DF2628FB346A339E1E2DD30C623B5A2D67D3D41A916AB3981E138D79A6FA5C35192D524CD31F0B60A9CD756463B97A4366017B426C66417B42D34D465F25DFB57BFE1766B59BFF171EEF56FF17B86E05E69C
	D3BAE0ECEB96F48B856EB301DB4CE4A26E24452CF48A7C2B4CAE44D74B28D05C3D6BC03BFA982ED9454D5061BED3F133BA30BF034B1DF62738BB50414EABACD6ABAFBF49E8ACB6685E22BDFE5E01B6DA9CA61D5D118227597D4BCE0C01182F28C3AD2F7BF5E7AA138B430549EA69E3BF37BC8104368E9E6BA306F7DF869FBBAA8665B39EBB3EE2FC29D3AFEE6C4818CAE347C9B65E97CE756ECB2D8CDFBD553BAF7DA9167AD2A354CD1FCAFD694FF1C39B832478217165DFB29F776BBF59B7EB6B435E7E7E0E61B3
	9E767637B31E7C4D013A51G7C1E674C752BD87C2D961BDC4E7F6A09C477CC0A587CEA995026CE23332747DB58F994148B27D11BCDBFA589FFE6695EEBCFDD7E35C35B3FC0C759E94D27BA5DFB649717DF77624B2A0347147F2D18EA08E7330873C2767BECAB147B856E6F7B68ACF204401D97770E66895CEEB749DBA4F013CBC91E74BD422DADA3F9AB85AEEB8B49EB91381F32F92F834A8182F7B3238B02F228407D1F384BB5D9609245FDA76991428531F9E4AA14B3856EDCAC62CA205C780878BBA9F1DA2C5B
	F56FAF47DE7E38A745DDFE9D7363B51F7E28FB3745DFFE5D57DCD69D6A5D63E27CDEAB38F2447DC4094E0D2CECDCEB05B8DB9F21BD375F3644932DC47BAC26F3121035E937677D01DB76200CE0608F93B25E4A70BCF39B06FF8EC4BDF3B36F7BB6E71A73C0879E103EA1768DC345BD0A85D047F19F31FE37944AF10F5239637DDD1A8D99E55F661CB367D17EF781B59BCEEEA19BCA9E25B6186FF54F624F2C6FB6C2FD2B20193045331D1E34E9B62EFE675B1C0D86020E359EB6E60B3BA041C27F03F946F97193B6
	AECC863A9902764AD85F3CD80CDBC46A7BA92716A3BFE83934FD6CCD5D9A6F7C0C1D463CE6E035F7GF3364A4CF9FFFCDE63FD2C47F70FF0CF2FD69FDAFAE932EB7AB4DE5B96DF597775EB75D7EF39B459F33E52F80F9B1C8B6444E97527E3AF36BD795F2B7C5E71AC88E078E2A99EE22AC82B58F9D09A3FF2ECD624E431512DC8EF6BB62A21245B5ED624551E9B4FAE2734C6682FE1937028739F96443EB0AEB6C60A2B1192780224C60A43D5C50D944F969DB5D2ACACC85C8EDD42E2E2463A3E143E1692F4DD8D
	77129385B6C1070D874D2BBCBC7F693D077FFB6E73CD2ED8A921C65A021D6C046CE473F6590C3F5DD17F20DC15146C30DB209270E0160BBD62DDCA2FD52E17D25BBF45BF9C64D2E4F3A92D88DC0A34017D3C7EGEE369D28B639BA95EC342375D21C8D7F741D340ED7DB21C18E706541D1665B7252D60C976B1C4B415E754CDE1412A5242CFFEC4D6E9D358B2647CAB1D5528AABB4F51529CD31BBEB24E42553A93BBA6DD6335BF12C0489CAE2639A1E75460138F7057B68EC961EB68A234F396F9A39EA612A2A98
	C9F2BB25830C924714ACC1E1AE6FB4B9C1156C2CD170CF79591D69226E0A7714BDCF9D5F5492A3ADD71D44902CF4BB94A9215D54E5317642CB854BE1961C94A872D51B4887EC0C2CAA70C7794D62A7467ED76112C5A02CCA0AE7BF088713D2B42A0F3A3B4E484E2B43EF5D76F6D05475E831835DA54949306A3539700F8D0070B4AF1F29CA181EF54B935937CEF86D1F4CF40DD5113673A0816B018E595EF520343D9DE675C2GC48C44EF936211A64B0BE61DFB54065AE75FBA8399D6A145F6E5E733FF8F32FF8F
	613F879966A0439CBC84E1DBA2797D4F3B5ECDBE3334E405CD18C3EEFCCBF50B0E90A3711D365781D688EC1CDA0317D93699E2638C31899E9D0CA5AB40D68FDA42469B52EA58ADBA2C26ED13CE1BC63BC275A249236B630A8BF76B7064ACC1115672F18A0F9E1CDDEC812EC8C9F81E1ED6D3D1G49C1875E701E420F4053F428B85C97E422D77874C7442E766DD808DEA7529BCB9B701FAD784FBB0C09374C76CB8EAEF26D708A6A913721F552G56457ADA775D5BD428A59A6A452D2783A2F07FD616163BBD5291
	AD454BB8497FFE1656EA16A62A16E207C87CF74E4DCF5CF4F7E25FB77A3B93CB07EF9E706EC464CC751E51CC6DDE86BF8379DB9D3A11BEABA221E4961D015454615D40016E201D68B2D92D0A49FA2E54E6467BE8E53D0A2C1DA33ED90772DDC423D62AFF33309E6DBE2B6379FFD0CB87887D767F5EAB9DGG90DCGGD0CB818294G94G88G88GECF954AC7D767F5EAB9DGG90DCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE5
	9DGGGG
**end of data**/
}
}
