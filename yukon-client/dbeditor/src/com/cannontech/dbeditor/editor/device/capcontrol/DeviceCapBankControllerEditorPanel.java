package com.cannontech.dbeditor.editor.device.capcontrol;

import com.cannontech.database.db.*;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class DeviceCapBankControllerEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener 
{
	private int paoID = -1;

	private javax.swing.JPanel ivjConfigurationPanel = null;
	private javax.swing.JCheckBox ivjControlInhibitCheckBox = null;
	private javax.swing.JCheckBox ivjDisableFlagCheckBox = null;
	private javax.swing.JPanel ivjIdentificationPanel = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjTypeLabel = null;
	private javax.swing.JLabel ivjTypeTextField = null;
	private javax.swing.JComboBox ivjCommunicationRouteComboBox = null;
	private javax.swing.JLabel ivjCommunicationRouteLabel = null;
	private javax.swing.JLabel ivjSerialNumberLabel = null;
	private javax.swing.JTextField ivjSerialNumberTextField = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceCapBankControllerEditorPanel() {
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
	if (e.getSource() == getDisableFlagCheckBox()) 
		connEtoC1(e);
	if (e.getSource() == getControlInhibitCheckBox()) 
		connEtoC4(e);
	if (e.getSource() == getCommunicationRouteComboBox()) 
		connEtoC5(e);
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
	if (e.getSource() == getNameTextField()) 
		connEtoC2(e);
	if (e.getSource() == getSerialNumberTextField()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 1:16:35 PM)
 */
private boolean checkCBCSerialNumbers( int serialNumber_ )
{
	try
	{
		String[] devices = com.cannontech.database.db.capcontrol.DeviceCBC.isSerialNumberUnique(
					serialNumber_, new Integer(paoID));

		if( devices != null )
		{
			String devStr = new String();
			for( int i = 0; i < devices.length; i++ )
				devStr += "          " + devices[i] + "\n";

			int res = javax.swing.JOptionPane.showConfirmDialog(
							this, 
							"The serial number '" + serialNumber_ + "' is already used by the following devices,\n" + 
							"are you sure you want to use it again?\n" +
							devStr,
							"Serial Number Already Used",
							javax.swing.JOptionPane.YES_NO_OPTION,
							javax.swing.JOptionPane.WARNING_MESSAGE );

			if( res == javax.swing.JOptionPane.NO_OPTION )
			{
				setErrorString(null);
				return false;
			}
			

		}
		
	}
	catch( java.sql.SQLException sq )
	{
		com.cannontech.clientutils.CTILogger.error( sq.getMessage(), sq );
		return false;
	}

	return true;
}
/**
 * connEtoC1:  (DisableFlagCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceCapBankControllerEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (PasswordTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceTapTerminalEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (BankAddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCapBankControllerEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (ControlInhibitCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceCapBankControllerEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (OperationStateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceCapBankControllerEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * Return the OperationStateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getCommunicationRouteComboBox() {
	if (ivjCommunicationRouteComboBox == null) {
		try {
			ivjCommunicationRouteComboBox = new javax.swing.JComboBox();
			ivjCommunicationRouteComboBox.setName("CommunicationRouteComboBox");
			ivjCommunicationRouteComboBox.setPreferredSize(new java.awt.Dimension(150, 25));
			ivjCommunicationRouteComboBox.setMinimumSize(new java.awt.Dimension(100, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommunicationRouteComboBox;
}
/**
 * Return the OperationalStateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCommunicationRouteLabel() {
	if (ivjCommunicationRouteLabel == null) {
		try {
			ivjCommunicationRouteLabel = new javax.swing.JLabel();
			ivjCommunicationRouteLabel.setName("CommunicationRouteLabel");
			ivjCommunicationRouteLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCommunicationRouteLabel.setText("Communication Route:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommunicationRouteLabel;
}
/**
 * Return the ConfigurationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getConfigurationPanel() {
	if (ivjConfigurationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder1.setTitle("Configuration");
			ivjConfigurationPanel = new javax.swing.JPanel();
			ivjConfigurationPanel.setName("ConfigurationPanel");
			ivjConfigurationPanel.setBorder(ivjLocalBorder1);
			ivjConfigurationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsControlInhibitCheckBox = new java.awt.GridBagConstraints();
			constraintsControlInhibitCheckBox.gridx = 0; constraintsControlInhibitCheckBox.gridy = 0;
			constraintsControlInhibitCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlInhibitCheckBox.insets = new java.awt.Insets(3, 3, 3, 0);
			getConfigurationPanel().add(getControlInhibitCheckBox(), constraintsControlInhibitCheckBox);

			java.awt.GridBagConstraints constraintsCommunicationRouteComboBox = new java.awt.GridBagConstraints();
			constraintsCommunicationRouteComboBox.gridx = 1; constraintsCommunicationRouteComboBox.gridy = 2;
			constraintsCommunicationRouteComboBox.gridwidth = 3;
			constraintsCommunicationRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCommunicationRouteComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommunicationRouteComboBox.insets = new java.awt.Insets(5, 10, 5, 3);
			getConfigurationPanel().add(getCommunicationRouteComboBox(), constraintsCommunicationRouteComboBox);

			java.awt.GridBagConstraints constraintsCommunicationRouteLabel = new java.awt.GridBagConstraints();
			constraintsCommunicationRouteLabel.gridx = 0; constraintsCommunicationRouteLabel.gridy = 2;
			constraintsCommunicationRouteLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommunicationRouteLabel.insets = new java.awt.Insets(5, 3, 5, 0);
			getConfigurationPanel().add(getCommunicationRouteLabel(), constraintsCommunicationRouteLabel);

			java.awt.GridBagConstraints constraintsSerialNumberLabel = new java.awt.GridBagConstraints();
			constraintsSerialNumberLabel.gridx = 0; constraintsSerialNumberLabel.gridy = 1;
			constraintsSerialNumberLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSerialNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSerialNumberLabel.insets = new java.awt.Insets(5, 3, 5, 0);
			getConfigurationPanel().add(getSerialNumberLabel(), constraintsSerialNumberLabel);

			java.awt.GridBagConstraints constraintsSerialNumberTextField = new java.awt.GridBagConstraints();
			constraintsSerialNumberTextField.gridx = 2; constraintsSerialNumberTextField.gridy = 1;
			constraintsSerialNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSerialNumberTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSerialNumberTextField.insets = new java.awt.Insets(5, 10, 5, 3);
			getConfigurationPanel().add(getSerialNumberTextField(), constraintsSerialNumberTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfigurationPanel;
}
/**
 * Return the ControlInhibitCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getControlInhibitCheckBox() {
	if (ivjControlInhibitCheckBox == null) {
		try {
			ivjControlInhibitCheckBox = new javax.swing.JCheckBox();
			ivjControlInhibitCheckBox.setName("ControlInhibitCheckBox");
			ivjControlInhibitCheckBox.setText("Disable Controls");
			ivjControlInhibitCheckBox.setMaximumSize(new java.awt.Dimension(124, 26));
			ivjControlInhibitCheckBox.setActionCommand("Control Inhibit");
			ivjControlInhibitCheckBox.setBorderPainted(false);
			ivjControlInhibitCheckBox.setPreferredSize(new java.awt.Dimension(124, 26));
			ivjControlInhibitCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlInhibitCheckBox.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjControlInhibitCheckBox.setMinimumSize(new java.awt.Dimension(124, 26));
			ivjControlInhibitCheckBox.setHorizontalAlignment(2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlInhibitCheckBox;
}
/**
 * Return the DisableFlagCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getDisableFlagCheckBox() {
	if (ivjDisableFlagCheckBox == null) {
		try {
			ivjDisableFlagCheckBox = new javax.swing.JCheckBox();
			ivjDisableFlagCheckBox.setName("DisableFlagCheckBox");
			ivjDisableFlagCheckBox.setText("Disable Device");
			ivjDisableFlagCheckBox.setMaximumSize(new java.awt.Dimension(121, 26));
			ivjDisableFlagCheckBox.setActionCommand("Disable Device");
			ivjDisableFlagCheckBox.setBorderPainted(false);
			ivjDisableFlagCheckBox.setPreferredSize(new java.awt.Dimension(121, 26));
			ivjDisableFlagCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDisableFlagCheckBox.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjDisableFlagCheckBox.setMinimumSize(new java.awt.Dimension(121, 26));
			ivjDisableFlagCheckBox.setHorizontalAlignment(2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisableFlagCheckBox;
}
/**
 * Return the IdentificationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getIdentificationPanel() {
	if (ivjIdentificationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Identification");
			ivjIdentificationPanel = new javax.swing.JPanel();
			ivjIdentificationPanel.setName("IdentificationPanel");
			ivjIdentificationPanel.setBorder(ivjLocalBorder);
			ivjIdentificationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTypeTextField = new java.awt.GridBagConstraints();
			constraintsTypeTextField.gridx = 2; constraintsTypeTextField.gridy = 1;
			constraintsTypeTextField.ipadx = 220;
			constraintsTypeTextField.ipady = 20;
			constraintsTypeTextField.insets = new java.awt.Insets(3, 5, 3, 24);
			getIdentificationPanel().add(getTypeTextField(), constraintsTypeTextField);

			java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
			constraintsTypeLabel.gridx = 1; constraintsTypeLabel.gridy = 1;
			constraintsTypeLabel.insets = new java.awt.Insets(3, 23, 3, 9);
			getIdentificationPanel().add(getTypeLabel(), constraintsTypeLabel);

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 2;
			constraintsNameLabel.insets = new java.awt.Insets(3, 23, 12, 5);
			getIdentificationPanel().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 2;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.ipadx = 88;
			constraintsNameTextField.insets = new java.awt.Insets(3, 5, 10, 24);
			getIdentificationPanel().add(getNameTextField(), constraintsNameTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIdentificationPanel;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setText("Device Name:");
			ivjNameLabel.setMaximumSize(new java.awt.Dimension(87, 16));
			ivjNameLabel.setPreferredSize(new java.awt.Dimension(87, 16));
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setMinimumSize(new java.awt.Dimension(87, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjNameTextField.setColumns(15);
			ivjNameTextField.setPreferredSize(new java.awt.Dimension(132, 20));
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setMinimumSize(new java.awt.Dimension(132, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * Return the BankAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSerialNumberLabel() {
	if (ivjSerialNumberLabel == null) {
		try {
			ivjSerialNumberLabel = new javax.swing.JLabel();
			ivjSerialNumberLabel.setName("SerialNumberLabel");
			ivjSerialNumberLabel.setText("Serial Number:");
			ivjSerialNumberLabel.setMaximumSize(new java.awt.Dimension(112, 16));
			ivjSerialNumberLabel.setPreferredSize(new java.awt.Dimension(112, 16));
			ivjSerialNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSerialNumberLabel.setMinimumSize(new java.awt.Dimension(112, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSerialNumberLabel;
}
/**
 * Return the BankAddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSerialNumberTextField() {
	if (ivjSerialNumberTextField == null) {
		try {
			ivjSerialNumberTextField = new javax.swing.JTextField();
			ivjSerialNumberTextField.setName("SerialNumberTextField");
			ivjSerialNumberTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjSerialNumberTextField.setColumns(15);
			ivjSerialNumberTextField.setPreferredSize(new java.awt.Dimension(150, 20));
			ivjSerialNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjSerialNumberTextField.setMinimumSize(new java.awt.Dimension(100, 20));
			// user code begin {1}

			ivjSerialNumberTextField.setDocument(new com.cannontech.common.gui.unchanging.LongRangeDocument(-999999999, 999999999));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSerialNumberTextField;
}
/**
 * Return the TypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTypeLabel() {
	if (ivjTypeLabel == null) {
		try {
			ivjTypeLabel = new javax.swing.JLabel();
			ivjTypeLabel.setName("TypeLabel");
			ivjTypeLabel.setText("Device Type:");
			ivjTypeLabel.setMaximumSize(new java.awt.Dimension(83, 16));
			ivjTypeLabel.setPreferredSize(new java.awt.Dimension(83, 16));
			ivjTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTypeLabel.setMinimumSize(new java.awt.Dimension(83, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTypeLabel;
}
/**
 * Return the TypeTextField property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTypeTextField() {
	if (ivjTypeTextField == null) {
		try {
			ivjTypeTextField = new javax.swing.JLabel();
			ivjTypeTextField.setName("TypeTextField");
			ivjTypeTextField.setOpaque(true);
			ivjTypeTextField.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjTypeTextField.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTypeTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	com.cannontech.database.data.capcontrol.CapBankController capBankController = (com.cannontech.database.data.capcontrol.CapBankController)val;

	String deviceName = getNameTextField().getText();
	Integer serialNumber = new Integer(getSerialNumberTextField().getText());
	Integer routeID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getCommunicationRouteComboBox().getSelectedItem()).getYukonID());

	capBankController.setPAOName( deviceName );
	capBankController.getDeviceCBC().setSerialNumber( serialNumber );
	capBankController.getDeviceCBC().setRouteID( routeID );

	if( getControlInhibitCheckBox().isSelected() )
		capBankController.getDevice().setControlInhibit( com.cannontech.common.util.CtiUtilities.getTrueCharacter() );
	else
		capBankController.getDevice().setControlInhibit( com.cannontech.common.util.CtiUtilities.getFalseCharacter() );
	if( getDisableFlagCheckBox().isSelected() )
		capBankController.setDisableFlag( com.cannontech.common.util.CtiUtilities.getTrueCharacter() );
	else
		capBankController.setDisableFlag( com.cannontech.common.util.CtiUtilities.getFalseCharacter() );

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getDisableFlagCheckBox().addActionListener(this);
	getNameTextField().addCaretListener(this);
	getSerialNumberTextField().addCaretListener(this);
	getControlInhibitCheckBox().addActionListener(this);
	getCommunicationRouteComboBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceMeterGroupEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(449, 379);

		java.awt.GridBagConstraints constraintsIdentificationPanel = new java.awt.GridBagConstraints();
		constraintsIdentificationPanel.gridx = 1; constraintsIdentificationPanel.gridy = 1;
		constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsIdentificationPanel.weightx = 1.0;
		constraintsIdentificationPanel.weighty = 1.0;
		constraintsIdentificationPanel.ipadx = -10;
		constraintsIdentificationPanel.ipady = 10;
		constraintsIdentificationPanel.insets = new java.awt.Insets(50, 19, 6, 66);
		add(getIdentificationPanel(), constraintsIdentificationPanel);

		java.awt.GridBagConstraints constraintsDisableFlagCheckBox = new java.awt.GridBagConstraints();
		constraintsDisableFlagCheckBox.gridx = 1; constraintsDisableFlagCheckBox.gridy = 3;
		constraintsDisableFlagCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDisableFlagCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDisableFlagCheckBox.ipadx = 48;
		constraintsDisableFlagCheckBox.insets = new java.awt.Insets(1, 20, 55, 260);
		add(getDisableFlagCheckBox(), constraintsDisableFlagCheckBox);

		java.awt.GridBagConstraints constraintsConfigurationPanel = new java.awt.GridBagConstraints();
		constraintsConfigurationPanel.gridx = 1; constraintsConfigurationPanel.gridy = 2;
		constraintsConfigurationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsConfigurationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsConfigurationPanel.weightx = 1.0;
		constraintsConfigurationPanel.weighty = 1.0;
		constraintsConfigurationPanel.ipadx = 95;
		constraintsConfigurationPanel.ipady = 10;
		constraintsConfigurationPanel.insets = new java.awt.Insets(6, 20, 1, 65);
		add(getConfigurationPanel(), constraintsConfigurationPanel);
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
	if( getSerialNumberTextField().getText() == null
		 || getSerialNumberTextField().getText().length() < 1 )
	{
		setErrorString("The serial number text field must be filled in");
		return false;
	}

	return checkCBCSerialNumbers(
			Integer.parseInt(getSerialNumberTextField().getText()) );

	//return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceCapBankControllerEditorPanel aDeviceCapBankControllerEditorPanel;
		aDeviceCapBankControllerEditorPanel = new DeviceCapBankControllerEditorPanel();
		frame.setContentPane(aDeviceCapBankControllerEditorPanel);
		frame.setSize(aDeviceCapBankControllerEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	com.cannontech.database.data.capcontrol.CapBankController capBankController = (com.cannontech.database.data.capcontrol.CapBankController)val;

	paoID = capBankController.getPAObjectID().intValue();
	String deviceType = capBankController.getPAOType();
	String deviceName = capBankController.getPAOName();
	Integer serialNumber = capBankController.getDeviceCBC().getSerialNumber();
	Integer routeID = capBankController.getDeviceCBC().getRouteID();

	getTypeTextField().setText( deviceType );
	getNameTextField().setText( deviceName );
	getSerialNumberTextField().setText( serialNumber.toString() );

	if( Character.toUpperCase(capBankController.getPAODisableFlag().charValue() )
		                       == com.cannontech.common.util.CtiUtilities.getTrueCharacter().charValue() )
	{
		getDisableFlagCheckBox().doClick();
	}
	
	
	if( getCommunicationRouteComboBox().getModel().getSize() > 0 )
		getCommunicationRouteComboBox().removeAllItems();

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List routes = cache.getAllRoutes();
		for(int i=0;i<routes.size();i++)
		{
			getCommunicationRouteComboBox().addItem(routes.get(i));
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)routes.get(i)).getYukonID() == routeID.intValue() )
				getCommunicationRouteComboBox().setSelectedIndex(i);
		}
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCCF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8DD8D5D5361864CFD9D12111E911E2D1D219E1B173FC4590731EB3590C3D31C90B57B0AF276CBDEB78DE4C9B1BE14643BF9A788B0AE60AA80693A1A29A91CEA8C8E02428C424E49C388738FA3977FA6E39F0914A37565EE76FF36E65DC7E5E73FDBD3EEFF14EDEFB2F3DFE765AEB7F1EABE46E8CCC0ECF48940478E4C17D7BA8DE906A3F95044C8F439EE3B89FA9A2C5507D6D8258A0BC1A1E84CFBF
	48DB79F4C44AFA61434F52A09D846910A7A2D236407BA621549EF79ADE3C70D98CF9EBAF5D755C60FC0E1DE3FCD2C97A6A49F4F82E8588869CBEE3A564FF4749EC953FD045B308CF900418F6ACB37764F695D78A69FA00900070F6DAFE89BCD7934E5717152A651AEF8D94E2FF7EFEC28656232B13C1E98725EDD61FB942A371C91D486BA5A2A75278C35EC2GD4BED1F8AF259E1E657517BA7E5EE66BAAEC932DD61BD5115ABA8BA513D9314905A6295B5CA6C1063D8DB2E41B252C2C0A20AAC57BB151FA2912E2
	AD12FC1C90540BD64962C3F86EBE15C6F888C28D24534D088BD5D00EC8C8C78144F2FC1F937175705E8960E3E63A9FF93FDC552D296F91A17485EB62BAAF3A8718294E61E6CDF7FF671FD23712F63A6EBC36C79C6465DC0CC8A981E882F08184G3CCB7CE362393F4173E82BD2E96B6A32D90F5A6D47ACA6798B1BC93240FBD999642838B3D21BCDB6898275015F1EACB010E7B020F43F9FB19D35C970C1668FA1E76E93229E7BBCA15540A6D141C589F3AED2DBAC38285904724EBF650D77720B6E7E6A4E7BABC1
	4FBBE81CADB15900F768737B926254FA56DF52F8D7DC42364EA7EDDD846F712AFFD0FCB1417B6A7054F7FFCCFC86537E10377292730D2635CC163A2429427A83F506F68808B81A50EC21B2A896CFD9FECFF886DAA84FF738ACFFA178F09D1E4AB20660B19D8DF9D5DD91A9543FEE7C867BF2B024A38122GE281B22D91A9C5GD5D666E37FF2EE7FB0FD2CDE1415E3CE3345A44982253D717CE6F8CAAEC596EB2D8EC53436C90D22EC96DBAD12C3D04B781FC79B343A515DC9FC3F8DF8FCA6D9A5D9D44CB6AB54AE
	F6C90AA4BBE8199967B7E099D9A2398D66AE8998F45985D66F0367B74153A2BA14AF6DA6D1113492B4FF6EF9EC13EE3191EA048A601D5AA56C825A4B847A7783CC62F67830996B7BDC12A1DA14155559EC171C768654C40832221DBFA531A3905E432C4C46215FA2AE479611D2EAE375181AB76A6A29B2372178225C4B7AD8219363F51D0D794C1BCD4CE72AB2E689854F1FB0749962ABB66AAB13EC1EFD27145409F251F43852B442FE3CD615E97831E26928F8A48E339C35EF2E08ED958AF2AD820861B63E27B9
	077A6CB151A1B1AFBDA50303C9A64D62D44EBFB8B3D5FBA26FE4D57E55DC0F44EE4CCF33C33FGE88270818836B3DB0EF7B6F9219F296E5EED99C8F8F8567F4E7F928FEA33495F60B8A5F5CBD625CA32CBD6933CB8543CE95FA0BF359F375BB84D8C0237C33F2F040E66607860EF30EF0A0AA21BDB1D0AF4D6D2943335C34B1FC3F8B91C2DF6ADE4B0DECFFC03BA5AFA205F7372733F41BE834339D5A20D47C9547CA7C91F15ACD29788DDE9F3DA950A0F3C00FEFFC96AD5E346028BB8C16A96ADCE0CBFF4BEB1C7
	C4BB5AE5F397388406B02164B2766746D359BA9BAD027646F9D19A64ED3F4C7AE64FEE56B777DC39D7887FB6AF41E8EC8918D51550FF19769D3F4BDA5F24B27D68DBE3591FA231C345BB282C020CF9F4B6738546424FE41B532E1B46287D62FCAB56B987F8C582C4DDE67D6213964CE731C76D9015B897A2E665764E9757E37BC8171D9204EC8CF21FF39B6E97B7FA64B52A7624799F91FBDAF471F1120C76AC6DE6F347D810A79DF085B23529C5D66396A997474BAD01F48C629CC6651E6AE1656AA05F6470D64E
	61E265FCA1FF1A439B5F77FBD93979D0E6B1C02CC7FD348DD23F45F908517C3343E9AEF4AAE60C6A22829D456ED4F4F34BF0B2BEE09DA58A1BC33EF8865B4982E9DF00E98A732B67B2589C527FD22090BDF31B5779F328224E3D954D2FF24632B9642CB664196D0CC849F7B29F18AB6A4731238E072D4DCC06D0357D44D63D5FD1DCFB6B9AF8361BE507D28B0141455A5A520A7262982B2255B907581D2E6ADA8EFC950065CE4407ED4D54499E6224E365E2F8A6527E59DD0CF103B8E3A1FD54CBF23BCD6E12CC79
	9745EE31D06CD18AC90CAABCCA320F637BBE56B6117BB6D2B9A5E2FDBA70676FEBE4F341991BAAD45E8B9F1EAA14DF5D14E0B4B71F306E3D0422EEEA4F26EE4D2E7DF3D392D7DC78F3AA55B12F07699884E5223A190E63FB74BAE676C42414833474B09DA77560FC2ACD14A525107CD725C475DC050E9E88142A0615182BAAC8F9AE67BC9528AF240771FC29581367B200B4D7C48A251B66E2F45B815760E2F493FABD5B8369A6F13A7ADEC6978838A51C6E09B2BD5D7ADECAD75A4BF4FC22F7346DC8473B1F8E18
	6B5775EAF30F165E017390BA87BEF99665D5FA699CB815634F917CA49D1E6A97CD7A9F2603A0AF6D8A1B3715C8987BAD101EG908890863084E06595560FE2DB8F8AC34C0BEBA1AE48D651F256ED20E37353B634950CCDEA7A61B634B3991B84E6070EEECFBBE476396BDE641126F44E81F4059E69868FBA5AFF7E5E78AE0BEDBAF7ACABBBD159A935DDBAE6F389B4167DFEAB4608AC34D19F0BD95FD56BE3567CF545896FF809D90BD45EB1FDDA5FAA606B5E175BB18E9775C724548118GFCG02G42811675B3
	7B171A0E8CE5FF62D48D36CA405540F8D469B9FF60767A20E7F4F67EF8G1D271D133EB332F3DE7EF6E3BBB7409C2B5AACD9CCF48EF27BD97D9CE47BF7B4DED640338100364513BD5896B8BED83EE3F352B7C729CF5C107AAC7250074A39D7B112F3B9BC63381C9B8FB2B9B33FE7F24EF00DCE4EB98368BC65F4FD6FCEC7E5F82A17792D9F172131F7F4B2188650F94A907B3DD15BF7D83C75B1DBD72BCD6BE3EF16212C5837522F4278C25618D1E48ED18569A60E3B7FB4627AA16DFF156954C062DD982417G2C
	6478960247718867DF9833A97E55B3C32D55302E4172176872D7DEB532572AA1F98C3576CB7628D31FC7E34291497B9A26CAB2DE431C100C57B07552860A07BA74BB9BAC3FC99AFC0D53A2992DF15EA3FA7EEC73BAD23743A1B9C8104289231A771A027EB9E02DF3D26C12D49BED926E6EDE2D16B92E58AA8FF09B47EEB532F1368FED0F8275D9612335CF833CF782747B3837199F2403G42G962AF9B4BECB1DB876C783AE8EA0F38C4C27816A4630387CDF52106362F157A0636272F66FED787BF663B67CC3BB
	4695074DA937C91E6D94571E4D73349120D6113A502434CCF28752AB225CA18DE867740EEC1E670DBE2B431BAF5076EA6D5137D7B10F85EF6D4BB60E853AF543B3E7535476350CE16B0637B3319F87C1FABE4062B12C9FBF5DB3D89FDBB1463D0D056BE8BA931E7957396715C33A5E83C7F549F369F5413626F2DED0181C266B181C4F10F967B4C80782AC3C0E49396560E0F2AE73603BD2CD270F0DC849996BB17EC03AE62C111CCB5D64AC61FDA4325708F733DA07DDFD7A6A6A8C00779000F08FBE0BA13D9CA0
	8EE03DAF4D233CEF6A5573BE44F93FD546460CBDD03E54573D3EBAC837789A697256A95C3730083DB62782FD4F97ED3B261E0D398A24A7810482AC70552FD59F6D41B9D83BACF601DB1D35CBED427C6B491C61B4234D07F4B9C0B3C0BF40346B599C7E9F3F44F5E2ADF6BFF33B39CD5D0345BD861ACF214A6C40B5FE35C56C60F3C4F58DF08A57E250E35A4D9DCED9C72E6E17EE5E067E12B48EE248B8B6164DA638FCC8D7F05C529244B5C05A4EF19F9FC21C6FB8BA1F27382F88AE90F073F9B9DF81F10BA09D4D
	F12BC8FF0A05F45AF8065BC9FCB79352859C372B98F1956369399345D594333136E5BC0BF7DD1D5E631552E99C2F2EF49A0FB9BE1D030FB993BA3D45996ADF0B4E6A67A4F3C04648711ABFAD99BFF08C276B232EAC6C27AB46D3FB2E6678AB841FB6C14353F553AA92FFB03D8772A6CCE0B67BDD8FEB3F959C77B5E997E1A23473C4061B415BAA1F631E6FE6EDD54EF177B7B3FB57F35C398456D60193D9BCE9D374FA0782FE41C4CD5728095E747E142CEFE3A6FA1E25FCC1706BEF70BCCBF94E454ED20AA04F7F
	866E0F87195EABB92ECBE1FA275F48F00FAACC6F9C0E3B251869DD42F1251B195EB59CB776A053BB0063DE6DE5FDA80463BE3B42E4896338F33C8FAD643895A5837BD0A2EF17E8DEEE93EF17D80EFB2FF1E09F2AEEE47225CDE2381A7E01FD685BFE265BF6DEEE4DA70833C0FA82477575A1EE9A2467F0DCC49F238D67B8B319A72F3F89FC69A636EFD1DC4F4E0E4B81275C444E0E8F34382D3BF097222C6CB359ECBAA6F6541168CA4732AB75E9AAFD48CDECAC8BA6F5AE03F49CC0664DEC5D6B5A4B563DA177CD
	956AFCBEB75C2B0BEEA8CEA838197AE17D4D5A3AB798563D146732530C67A4480F3C192C194E6877D9B02E7EB1012EB3E8FE15DBBE46065D02BE3F5BAD9F659D1B284F8FF64BC79B3D2F52D35D1E49E33A45BD93ACD866FD6CF59FB27D962AD3117A2CEF5CAD6C39614F29F4AC78F09B6E43AB90887DEEC13E15A44EF7423BF28B9BEF2AD6E33F6A92DD66AEE757D97395C920E59713320B61BD1A170DA9C1B9684692CEB3C52B8952347C6AC32C6EF4BFD63E7E9016EF356106F73DE836AA12C9E0E36B15F24C33
	4BD23BA44B1209F0E6F579103A72211E92DED745A654255DE6D5F5F9080CB1AD10EF61E5B6B72490DD648E3395FC588F6FE57C8D0FA6D51E7713F19986E58FDDEBC8FC080672ABF8DD2545B84EF65AE4739560A9DA0EDA4C9DD65C1B95CAEFA563DEF115C1FB68FDA6D5FDA2E4838D42EE353D8B60DDD45B1476275748786883F87FDBD99F732BC5DC982497F25CDFCE303E98F3AB0B430EACBCB728388D6693G8A40245BD81E8F498B067482006537513DFD1A171C65B9A7C3BAB2AF03E745ED836337E2007386
	14470E81BC58991DFA36E2C8B7DC9E347D563A50A6E937C3FC381D351F7D905653E633B83B2C8E355CF862D7BB81EA209C45BDC3F0CD10B6F15CDFC96C750374B4G5A5ECFB68F565E49BAFD90965DCE5BBBCB45AF037423EAFBD39EE349F89A8378385BD95BDED1188F647BB3DCE68E7301BA0E3BF99773813BBFEB4B63E4CFB99C524BG52A7C724948154CFE679E7C97EB4750CC5813C7FE4FAFEC373DFF0695B89698733FB7DA079483FC8179F4EE50CF163B1D4BB8FF64E1AAC381F75A6F23B361271BA9478
	07CFE66DB9C5E16D9E4BF1AF92DC5294F81F42F0E1E4CED08E691AA92C5DCFEDE66D5EBFE5204E7AB6A7B2C01920A9DABF8F1BC247F92D5DC308FC1100DF84C0E3549717851EEFA77308B848CB3B0349B5164C99CAA0DD41F18544EFDBA0ED6138F2623781108E62B89B199FAC06F4749DCC276EFE26D3FA40C01D183CCC279CA8D392C0F5C2FCCD0026936DEB3BBE719E4B4B489C2381E8DA82D85F3C2D8F63B49D97C05EGAC77C09F4677C981CC4625FDCC46A8839975E772789E23139171C9F7FA4A68D3E69C
	7B8368BC864AEF3F1379E9E97D686EE9D50F126E6A086954F542E91C9B5568F4257831845F204353795943FCFE5682F911F732F317A299572579D3A3D22AG1A8194GFF00B9D359FAEB0739C898E2FF2941E62F038519C53704227A3DFEFAF47757FE124A66C121BF1EA1040DFBBB5E5B59C664D46AA34B546772065D84CF75FB5A017AE55E85F18920814082608390F097532F5DDCC07543E37D230AA236F51213D826DC23D96A5150382827FD3759A9F09F8B5A06E3ED2E3AB84F219786F229AE01BD7A3B1925
	EBBEB23C330090F217FABFD1FD4E9F3FDB1D4BE477E83CFED102714CAA756470033DAAB30806172A728F949FA848A9BE3CCFADDBFC48C8AEDA67A7AE2D4EDEE1B8F5D23A426FB43A374E66FA52D54324A9BF57E496AD360E5C021C22021C83A57BD83B7E723C5E966B276ECA0877E20B152A8D1226E9F3F74A7F5E6FB57E1B0B07A377CE016E1D1075217AC76B0A3CAAF01F7BEDBA73396D336F922ADEF8BBFE379759CAD519A668E4DBFEB1CE1DFB7F752CBE46513D232C86367F938EB4CBG56CF0FC859B31D1E
	D52BF3545A01FBCECF56B23A7269F44F29811E1D53A9DF9AAFB3BF9FEABE2A879F20751FCE63A52603617D0DD52BD6E5F11BA66C41185E50EB177861984F2336423CBA319537165C7038BF6B094FD0717A3A6819F6E816FEAD942A5AE249F4F666DD16E6143F0267BFFF48A8DF381B0D2DBD711A1F549D5A6B5DCF0A8F14FCD4D2FA70F049C760AEC57B781E7FA1D87A89878EB1DF191A205577736E417C6EE0B12DC86DE7299A5D5466BC637E62902D8E07A41B5BF5DD4640EFE3BE25F5DEC853FCF84CEA2D7E3B
	368EC7AE6ADBB37999E2665D54A78B60D9F1B7BD2722E50E6430B2CDF7D37F334353D7ADC37DEF562E217CCFBFD71A8634F36E567CAF925E1F926899A173993C432ADE9A2F35F61ADB4D8A5F6AD44B3C5B3F0D14696AF2DA552D52B350EDA4FE2427169B1F3145309C75D2DAC67E9B2E634F0271C54BC9E7D72BA43B7B70DAD316C77EC0DF7EC51F27AF22FD965D4DFC7557D70C7217F1DF8DDE2335E10BB2F2DFFDC8F17755E75E516A739DB4C69EAA527B6A7BEBB53A8FB60F44D7691E0A4FE6F667EAA568E694
	F73D4762936B34772E751A9CF7A80349DF64A63F5F860D6E546063E409FECCFBEC23C6372C84AB49D59DB0C77540DCAE5B2FB7E9E553860D836EBC02B6EBF49B9A07BBEED25AFF5E22519E699F8ECF5A9E77773376A83AE7246D716F595AFB46B60D3F3DEFC8FFBA44669FCF75A11D7A1D06249F7B0B7A6293300C51785AF98F9D5FDC77E873A02A4F9A2ECF3097FD283C569C6D7D56DD1A6C0F8EAAFB11AA3B3AFF02DF82296F93F56F21752339E7BD32F541335714871D77FEACFBBFC729100D4FD12AE563F314
	BAF970337986597B7D6373327E7EB11D370B39644C9B5AB88EA0A79056F8GB501EC4E7E3A0C7D873F0498B8DFBFEE6A40F3E77C7AC1E17C97BA3C6B7AAB47505FCCC4B9B0C0375A9445560575B37C3FB9303D941B1DA0A96EB5875AC8B6F7F4AA34280AFF43C1765C25F60226FAFE3F9BF1A6504B076BF62F7383F8B60A975515963DEDCC3483E43DEC33CB3252CB3164A630EA335EBD8267A5FF17672D8E305089707DD32D9725B625C16C60B68F4A43F1BC9064188F908D3092A07DDE1827821482548134G38
	G7CG02G42G9682C48144GA44DE0FA2C96768D2207FA88C09ACA469DE2012DD55FEAE05F72E4C3DD75B358D942EB6FE1FD035F83EEB0AB966918BA75D6E346A7DF3218B1E186BBDB78579A726D8E2417G444CE067982F5632F5F25ACC188B4DE4F7298ACF30338257CCD6C77389F2679B528BG22E79A452318CF3D4F61FCF06B1F5DCBE877F2AF01CCC5288C7F11456EA72D64F2DDCEE3F73C0A0298AE7DB0437987B13D1E615FC72D84DC51ACD6F613135926F39653CB5CC93EF702F4984022D92C8EA907
	59E6B960F26EE3F5BC1443EC53F49F2B63A5D2568F5241G61770D54B6B373855EFE497D6C1E43924EB3230F69387EFE36F7F37DE67DDE03827889778F3E17C969C27A75F45300E64E90F4343F546F43FD2B487B193F57EF4B9D445F71CE64D9880F568E46F71D9B5FA5D04F0AA178D2BE66DC6F314C1EBBF4AC6B4DA5774DA18661BA0E623E4F4535272D3D5DA17118B9B6F7BD4755BA6AED8EB3CE1F553C5DA88F0F77B48C29F4B76C4658ED36C2F151E23EC2A65DD4F31A9F1667DD7E27720616FFC11EF1AC7E
	C55E40D87C2BBC63D83CB4CF9F0BA96E65BC6C879D90171AF839D7B86EEB0EDBCE6AC45CD7E613524971EF923990DFA3A1C3060FA5F27590DC130AFBDB077BDA457DB58F5BGD46E34490CB6F1AFB97BB2DBACB41D3AB70558961A045BA3FDAF4AE8360BBA39B7F05C575C4F5FEE98EAAFCE88B63AC7187A65D0F4BB8C7BD585215B9EBC3CF323A2A8C777B0CF1398DC956C3937F90E606B03BD77B6DF29E1F12999724203D9BCD8D1BB3ABD59BF0D126E6009516D65AEA263481E87BC75DDCA70C58FF86A5BFA02
	1F35C3DE60837C0CA50B71CFFAB0A2A57BC1758E227A2CF8F0E0BBD03A6EB42339E603DA3E53008E219F70FE9EF9C1108EFB504807B69CB66231D0AD9B6D05C7AC6053E63B67ED07F451ECA39E4FA923736F5F8CC9E76C5F59CE242B1ABDBC7FEE1A4D7C7BF9B23EDAE6FB36774B846F1A6D595E9DBBD9FB7BC25E72592C3DAF650C4ECFFBC7C9B7F744F4D42FD33BD07E3A90CFFD1B883EA944D35F5F72F90689729604B0FD57380D17858F29FE2DBE9B9E72665F9B7B0CFC2FD3AD5FEFC007608778A00F3CB0C8
	AF744051F359E9E45FA37AA17D5D1EFB89EE259B6E697E016596746B4B296B25FD98679D6AFC01165BEBC07B019BAD3D4F9C3E8967C5E62B45EC15AA69975D3C7CD7E4AE1C79F0C4CA41436CCCD92AA1F706A05D70B03375D7358237DF0B6E7D751AEB3F76CD283D76BCFEFDC25369B73A774E93571E7718EB4A0336E9AA713D96E8CF05377363DB90B78152819C97CD3ED78C0174FC0EBBEEE2F78C96F0DCGD9E7E4BF9211127F8843FD381A5DFBAB673836F2F61F2D1E6376EDE27759CC9C77F4A3636162B83F
	9EF61FED81479DBC4C78C6F1DC64E146B706632677B03EC9F398AE67B0631B4DF13EDB995F820E5BF918716D67387DE4DD648769E00EFBFD876266C3FA519C56CFDE5A29794EB25DFB574EEB6FC7637E8FF8FC39CB5369026E7D351CEB4FDB58F56D7BC981091FD1501EABF8BBAFBFCE56230F427B238C77C5B53FC349F1A5FD4CB7AD9CB7390F79260B63EA4EB05F1C44F1749EA97AE6A047ADBD46F804F25C16ED4CB7136632FD90F1AF0E0F6A7A0AACC1A8FE3BABC34333A5083A6FE97D9257AC8A6E0E60BE33
	406EE80CA363C1B6543DE7AEBB076C2FC9E6E58536077AB87E6C0DDA570E4DE42DE897AF631A0F56F308449B4CEF6275BC3B457D5E5EABA47EF4C23E424B7C310D5DD324FC16625617FA2E59EC9A6A5E157EDC135D3343339A9C8B0301470F60E59F177B11B477BB11BFA5B1AD944A054FE5F7E6BAC8CC0B0274F20E3B1D44344C47A2D2F29EE338C692536AA05D7498BB87FB31F1A467301E72CEF84C733E50D3A4267983BE1073EDA631EAA12417F05C9B84978BE9A114617E095096C03A1463EAC9B993246D21
	CC663B360E646C4ED36640D0CF19EB880F9040070532B62EF69A7B5E8592E317C13998DED658B95077628AB45F2B6CF777292FC97F5DB38F568473D89D2B0E3B7B544EC24D27CC55CC5F16F946771F747AFA5EFFD266E97A523A0F6127652ABECF123E6F83E5A64DE3B63FCC7AFE9824A3B9EE9D396795836938F92C9DE20EB1398A9EB716CB6FBBC19EF74DAA9E77EC078A92B76AG5F74B8BBE31CF7C033E3D38DEEBEFABDFFAAEC35D9CC3985B98768A114FA063E1958D5F6CA343EDD25DAFD3FEA9BC9FD147E
	179F8B5C0E7FD9265535F7B56A1CCB6F2A64609ED5D9AEAB17D52E5321FC07A7CF6DAC2774A030BA4C4F4FCA0FE8F4BBB68D1F4E7FD30D6E09911E59BDD121697762D12D1E30C343677F17E39A5DBF9E565B856FD4E8F64929524A5D56B3D87D9F3955DFF9DC234BBABCFC3AFBBE5368DCDBC7E617089A4DAE3F2B556A39B8823925BA0DAEEA4748FD6FA7A7F5744707ABBF5DFB1EDC4D764CFDF95FDAF6CA53A97534D67711B3031DB79EA826CA2977554FE8F4AF758E1F6E46AFB53AE70F0DCC17DD15CC1779DC
	17079BB5DD6EE9526AFEF250FB84CCA6BAF71BDA2DDF2B47C25DE9CF8CE7AFFEEF251EEEBB50948DC127FE67D3BDF0ED7CF875407577114A01650ED56A4B515F01ECC97B94A6D9A1EF88498ED9D847752DA59B2F515D874B92DF191918A0A4558AA9F4A33AD6C8425D64DAA119ECB6578A090A4D6E42AF90927420886B59DDBC75FE01C391563ADF4F830E9B87DA78684DCBDF887936AAB3815647C2CA27C439A477607C0F7041937ADE47E561FBA10927E4A1434D8FFEFA6F21D7973E5C68C894D2EA052DB80C52
	EBAD66AB12893F08D6FF1A2DDAD8ED3719A19370201803BC129DCA2F857ADDE67B997CA9A207A2190E327DFFD0A22B8EB96D47E3387DB52223D3C10DBE2993122C78A3F242C61AED912D9D7B69E140A7C46A7823AD68E124A98510F7931197EBA96435EC7A07F5F93BCEAC8BCB94922A05B5961BE82A965B941BDCAB2CD6BAE54951899D5CE57F1404DBA10D4CFC707ACBA2D477AED79F15AD440BBBDC68CB2E6913B216E7D4C34338E4E1BFA9093306C2DE42D459A94A40CA12EB957CD1BC1B3C07670D59216CFD
	7633AD4D89C23C2AA41AFE354B2E88A96DE21759528BAF345711C649D0C0DAD934BA70C7D9C0329C8F49D896E7D0F87B4F0AA6BF7027F30401C62A88BB29A901F4FF07E46B5AFF343D5DEC15F885E0D7287EBDDEBD16A974A83351F5EB560967D735029FD48909DDC5C5647F81723F987F9FA0188384F320980C9BA38C7A67196FE2BA936761FD881B3F309ADAD0F9710D1F4DF829BCE685E8D7ADA4139FE101C6D82F1522B72D4605CCFE6F9D7F689B310D6C68AAAE2CF7034B434FB105FF9F81602F36E2CEBCAF
	13F9F5552AD5DA8AE096C9258E70BF443EB35011903D8E5A0FDFFCC244C6D2BA137C5FB2E27BA055D6C27BAED7EB0F0BAB02A7C9981533099F6BC5777DFF27D62126D6322A9AD9D83809EEF2EB51FEC5D867F17F4ABDFFD40A2A5D715AE879314BC0C2C150A66D641D1741A364A9C358E5AB378B7E61229B8F4BF1C6D84C31712A7DF0FE1982C3097B67633058D2048C37CF4295A1536DBAE48E398FD98624F4A848219D2F4C504271DEAC1C44ACDC659670D26881A7359452DC9B53FF6E7A1F880B29BA7CF7725A
	C85760F2F3855E2B95A1DD3F44612D0E076EE78DD2BFA8EF3F602E94CE1AF0111F68AE3726A2D92D38E5FEE5107AC129E856D4CCD7D5CC9E2022315CBF904F3BEC5CAF0634EA55B534EA0F7BE04082E3321BD56D86299F14EF788CCB8B755E6B3E2B7AFB2FAB2F4F767A7DCFF9247AADF72476DD893DEB941C017ACD82FCF0A43BEF34B27DC3417B9D3F33DD2245220816CBC72DA63C6FF72CD711347B188FE5E07D8ED6C64D24F92199A877C59D4DFF83D0CB878898B25E917F9EGGF8DFGGD0CB818294G94
	G88G88GCCF954AC98B25E917F9EGGF8DFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB99FGGGG
**end of data**/
}
}
