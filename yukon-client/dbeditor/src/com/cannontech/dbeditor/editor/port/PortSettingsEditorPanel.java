package com.cannontech.dbeditor.editor.port;

/**
 * This type was created in VisualAge.
 */

 import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.LocalDirectPort;
import com.cannontech.database.data.port.PooledPort;
import com.cannontech.database.data.port.TerminalServerDirectPort;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.database.db.port.PortSettings;
import com.cannontech.database.db.port.PortTerminalServer;
 
public class PortSettingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JComboBox ivjCommonProtocolComboBox = null;
	private javax.swing.JLabel ivjCommonProtocolLabel = null;
	private javax.swing.JLabel ivjDescriptionLabel = null;
	private javax.swing.JCheckBox ivjDisableCheckBox = null;
	private javax.swing.JTextField ivjPortDescriptionTextField = null;
	private javax.swing.JLabel ivjTypeLabel = null;
	private javax.swing.JLabel ivjTypeLabelField = null;
	private javax.swing.JComboBox ivjBaudRateComboBox = null;
	private javax.swing.JLabel ivjBaudRateLabel = null;
	private javax.swing.JCheckBox ivjCarrierDetectCheckBox = null;
	private javax.swing.JPanel ivjIdentificationPanel = null;
	private javax.swing.JLabel ivjMsecsLabel = null;
	private javax.swing.JComboBox ivjPhysicalPortComboBox = null;
	private javax.swing.JLabel ivjPhysicalPortLabel = null;
	private javax.swing.JLabel ivjCarrierDetectWaitLabel = null;
	private javax.swing.JTextField ivjCarrierDetectWaitTextField = null;
	private javax.swing.JPanel ivjConfigurationPanel = null;
	private javax.swing.JLabel ivjIPAddressLabel = null;
	private javax.swing.JTextField ivjIPAddressTextField = null;
	private javax.swing.JLabel ivjPortNumberLabel = null;
	private javax.swing.JTextField ivjPortNumberTextField = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PortSettingsEditorPanel() {
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
	if (e.getSource() == getDisableCheckBox()) 
		connEtoC4(e);
	if (e.getSource() == getBaudRateComboBox()) 
		connEtoC5(e);
	if (e.getSource() == getCommonProtocolComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getCarrierDetectCheckBox()) 
		connEtoC6(e);
	if (e.getSource() == getPhysicalPortComboBox()) 
		connEtoC3(e);
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
	if (e.getSource() == getPortDescriptionTextField()) 
		connEtoC1(e);
	if (e.getSource() == getCarrierDetectWaitTextField()) 
		connEtoC7(e);
	if (e.getSource() == getIPAddressTextField()) 
		connEtoC8(e);
	if (e.getSource() == getPortNumberTextField()) 
		connEtoC9(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (PortDescriptionTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PortSettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (CommonProtocolComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PortSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (PhysicalPortComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PortSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (DisableCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> PortSettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (BaudRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PortSettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC6:  (CarrierDetectCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> PortSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}

		if( getCarrierDetectCheckBox().isSelected() )
		{
			getCarrierDetectWaitLabel().setEnabled(true);
			getCarrierDetectWaitTextField().setEnabled(true);
			getMsecsLabel().setEnabled(true);
		}
		else
		{
			getCarrierDetectWaitLabel().setEnabled(false);
			getCarrierDetectWaitTextField().setEnabled(false);
			getMsecsLabel().setEnabled(false);
		}
	
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (CarrierDetectWaitTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PortSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC8:  (IPAddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PortSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC9:  (PortNumberTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PortSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(javax.swing.event.CaretEvent arg1) {
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
 * Return the BaudRateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBaudRateComboBox() {
	if (ivjBaudRateComboBox == null) {
		try {
			ivjBaudRateComboBox = new javax.swing.JComboBox();
			ivjBaudRateComboBox.setName("BaudRateComboBox");
			ivjBaudRateComboBox.setMaximumSize(new java.awt.Dimension(32767, 25));
			ivjBaudRateComboBox.setSelectedItem("300");
			ivjBaudRateComboBox.setPreferredSize(new java.awt.Dimension(80, 25));
			ivjBaudRateComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBaudRateComboBox.setMinimumSize(new java.awt.Dimension(76, 25));
			// user code begin {1}
	
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_300 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_1200 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_2400 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_4800 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_9600 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_14400 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_38400 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_57600 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_115200 );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBaudRateComboBox;
}
/**
 * Return the BaudRateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBaudRateLabel() {
	if (ivjBaudRateLabel == null) {
		try {
			ivjBaudRateLabel = new javax.swing.JLabel();
			ivjBaudRateLabel.setName("BaudRateLabel");
			ivjBaudRateLabel.setText("Baud Rate:");
			ivjBaudRateLabel.setMaximumSize(new java.awt.Dimension(71, 16));
			ivjBaudRateLabel.setPreferredSize(new java.awt.Dimension(71, 16));
			ivjBaudRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBaudRateLabel.setMinimumSize(new java.awt.Dimension(71, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBaudRateLabel;
}
/**
 * Return the CarrierDetectCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getCarrierDetectCheckBox() {
	if (ivjCarrierDetectCheckBox == null) {
		try {
			ivjCarrierDetectCheckBox = new javax.swing.JCheckBox();
			ivjCarrierDetectCheckBox.setName("CarrierDetectCheckBox");
			ivjCarrierDetectCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCarrierDetectCheckBox.setText("Carrier Detect Wait");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCarrierDetectCheckBox;
}
/**
 * Return the CarrierDetectWaitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCarrierDetectWaitLabel() {
	if (ivjCarrierDetectWaitLabel == null) {
		try {
			ivjCarrierDetectWaitLabel = new javax.swing.JLabel();
			ivjCarrierDetectWaitLabel.setName("CarrierDetectWaitLabel");
			ivjCarrierDetectWaitLabel.setText("Wait for carrier detect (DCD)");
			ivjCarrierDetectWaitLabel.setMaximumSize(new java.awt.Dimension(185, 16));
			ivjCarrierDetectWaitLabel.setVisible(true);
			ivjCarrierDetectWaitLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjCarrierDetectWaitLabel.setPreferredSize(new java.awt.Dimension(185, 16));
			ivjCarrierDetectWaitLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCarrierDetectWaitLabel.setEnabled(false);
			ivjCarrierDetectWaitLabel.setMinimumSize(new java.awt.Dimension(185, 16));
			ivjCarrierDetectWaitLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCarrierDetectWaitLabel;
}
/**
 * Return the CarrierDetectWaitTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getCarrierDetectWaitTextField() {
	if (ivjCarrierDetectWaitTextField == null) {
		try {
			ivjCarrierDetectWaitTextField = new javax.swing.JTextField();
			ivjCarrierDetectWaitTextField.setName("CarrierDetectWaitTextField");
			ivjCarrierDetectWaitTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjCarrierDetectWaitTextField.setVisible(true);
			ivjCarrierDetectWaitTextField.setColumns(3);
			ivjCarrierDetectWaitTextField.setPreferredSize(new java.awt.Dimension(33, 20));
			ivjCarrierDetectWaitTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjCarrierDetectWaitTextField.setEnabled(false);
			ivjCarrierDetectWaitTextField.setMinimumSize(new java.awt.Dimension(33, 20));
			ivjCarrierDetectWaitTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
			// user code begin {1}

			ivjCarrierDetectWaitTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 9999999) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCarrierDetectWaitTextField;
}
/**
 * Return the CommonProtocolComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getCommonProtocolComboBox() {
	if (ivjCommonProtocolComboBox == null) {
		try {
			ivjCommonProtocolComboBox = new javax.swing.JComboBox();
			ivjCommonProtocolComboBox.setName("CommonProtocolComboBox");
			ivjCommonProtocolComboBox.setMaximumSize(new java.awt.Dimension(32767, 25));
			ivjCommonProtocolComboBox.setSelectedItem("IDLC");
			ivjCommonProtocolComboBox.setPreferredSize(new java.awt.Dimension(65, 25));
			ivjCommonProtocolComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCommonProtocolComboBox.setMinimumSize(new java.awt.Dimension(61, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommonProtocolComboBox;
}
/**
 * Return the CommonProtocolLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCommonProtocolLabel() {
	if (ivjCommonProtocolLabel == null) {
		try {
			ivjCommonProtocolLabel = new javax.swing.JLabel();
			ivjCommonProtocolLabel.setName("CommonProtocolLabel");
			ivjCommonProtocolLabel.setText("Protocol Wrap:");
			ivjCommonProtocolLabel.setMaximumSize(new java.awt.Dimension(94, 16));
			ivjCommonProtocolLabel.setPreferredSize(new java.awt.Dimension(94, 16));
			ivjCommonProtocolLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCommonProtocolLabel.setMinimumSize(new java.awt.Dimension(94, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommonProtocolLabel;
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

			java.awt.GridBagConstraints constraintsPhysicalPortLabel = new java.awt.GridBagConstraints();
			constraintsPhysicalPortLabel.gridx = 1; constraintsPhysicalPortLabel.gridy = 3;
			constraintsPhysicalPortLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPhysicalPortLabel.ipadx = 8;
			constraintsPhysicalPortLabel.insets = new java.awt.Insets(9, 32, 10, 5);
			getConfigurationPanel().add(getPhysicalPortLabel(), constraintsPhysicalPortLabel);

			java.awt.GridBagConstraints constraintsBaudRateLabel = new java.awt.GridBagConstraints();
			constraintsBaudRateLabel.gridx = 1; constraintsBaudRateLabel.gridy = 4;
			constraintsBaudRateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBaudRateLabel.ipadx = 23;
			constraintsBaudRateLabel.insets = new java.awt.Insets(9, 32, 10, 5);
			getConfigurationPanel().add(getBaudRateLabel(), constraintsBaudRateLabel);

			java.awt.GridBagConstraints constraintsPhysicalPortComboBox = new java.awt.GridBagConstraints();
			constraintsPhysicalPortComboBox.gridx = 2; constraintsPhysicalPortComboBox.gridy = 3;
			constraintsPhysicalPortComboBox.gridwidth = 3;
			constraintsPhysicalPortComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPhysicalPortComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPhysicalPortComboBox.weightx = 1.0;
			constraintsPhysicalPortComboBox.ipadx = 86;
			constraintsPhysicalPortComboBox.insets = new java.awt.Insets(5, 5, 5, 45);
			getConfigurationPanel().add(getPhysicalPortComboBox(), constraintsPhysicalPortComboBox);

			java.awt.GridBagConstraints constraintsBaudRateComboBox = new java.awt.GridBagConstraints();
			constraintsBaudRateComboBox.gridx = 2; constraintsBaudRateComboBox.gridy = 4;
			constraintsBaudRateComboBox.gridwidth = 3;
			constraintsBaudRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBaudRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBaudRateComboBox.weightx = 1.0;
			constraintsBaudRateComboBox.ipadx = 72;
			constraintsBaudRateComboBox.insets = new java.awt.Insets(5, 5, 5, 45);
			getConfigurationPanel().add(getBaudRateComboBox(), constraintsBaudRateComboBox);

			java.awt.GridBagConstraints constraintsCarrierDetectWaitLabel = new java.awt.GridBagConstraints();
			constraintsCarrierDetectWaitLabel.gridx = 1; constraintsCarrierDetectWaitLabel.gridy = 7;
			constraintsCarrierDetectWaitLabel.gridwidth = 2;
			constraintsCarrierDetectWaitLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCarrierDetectWaitLabel.ipadx = 3;
			constraintsCarrierDetectWaitLabel.ipady = 3;
			constraintsCarrierDetectWaitLabel.insets = new java.awt.Insets(3, 32, 15, 1);
			getConfigurationPanel().add(getCarrierDetectWaitLabel(), constraintsCarrierDetectWaitLabel);

			java.awt.GridBagConstraints constraintsCarrierDetectWaitTextField = new java.awt.GridBagConstraints();
			constraintsCarrierDetectWaitTextField.gridx = 3; constraintsCarrierDetectWaitTextField.gridy = 7;
			constraintsCarrierDetectWaitTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCarrierDetectWaitTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCarrierDetectWaitTextField.weightx = 1.0;
			constraintsCarrierDetectWaitTextField.ipadx = 6;
			constraintsCarrierDetectWaitTextField.insets = new java.awt.Insets(3, 1, 14, 3);
			getConfigurationPanel().add(getCarrierDetectWaitTextField(), constraintsCarrierDetectWaitTextField);

			java.awt.GridBagConstraints constraintsMsecsLabel = new java.awt.GridBagConstraints();
			constraintsMsecsLabel.gridx = 4; constraintsMsecsLabel.gridy = 7;
			constraintsMsecsLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMsecsLabel.insets = new java.awt.Insets(1, 3, 17, 22);
			getConfigurationPanel().add(getMsecsLabel(), constraintsMsecsLabel);

			java.awt.GridBagConstraints constraintsCarrierDetectCheckBox = new java.awt.GridBagConstraints();
			constraintsCarrierDetectCheckBox.gridx = 1; constraintsCarrierDetectCheckBox.gridy = 6;
			constraintsCarrierDetectCheckBox.gridwidth = 3;
			constraintsCarrierDetectCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCarrierDetectCheckBox.ipadx = 74;
			constraintsCarrierDetectCheckBox.insets = new java.awt.Insets(5, 31, 1, 13);
			getConfigurationPanel().add(getCarrierDetectCheckBox(), constraintsCarrierDetectCheckBox);

			java.awt.GridBagConstraints constraintsCommonProtocolComboBox = new java.awt.GridBagConstraints();
			constraintsCommonProtocolComboBox.gridx = 2; constraintsCommonProtocolComboBox.gridy = 5;
			constraintsCommonProtocolComboBox.gridwidth = 3;
			constraintsCommonProtocolComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCommonProtocolComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommonProtocolComboBox.weightx = 1.0;
			constraintsCommonProtocolComboBox.ipadx = 87;
			constraintsCommonProtocolComboBox.insets = new java.awt.Insets(5, 5, 5, 45);
			getConfigurationPanel().add(getCommonProtocolComboBox(), constraintsCommonProtocolComboBox);

			java.awt.GridBagConstraints constraintsCommonProtocolLabel = new java.awt.GridBagConstraints();
			constraintsCommonProtocolLabel.gridx = 1; constraintsCommonProtocolLabel.gridy = 5;
			constraintsCommonProtocolLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommonProtocolLabel.insets = new java.awt.Insets(9, 32, 10, 5);
			getConfigurationPanel().add(getCommonProtocolLabel(), constraintsCommonProtocolLabel);

			java.awt.GridBagConstraints constraintsIPAddressLabel = new java.awt.GridBagConstraints();
			constraintsIPAddressLabel.gridx = 1; constraintsIPAddressLabel.gridy = 1;
			constraintsIPAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsIPAddressLabel.ipadx = 22;
			constraintsIPAddressLabel.insets = new java.awt.Insets(0, 32, 9, 5);
			getConfigurationPanel().add(getIPAddressLabel(), constraintsIPAddressLabel);

			java.awt.GridBagConstraints constraintsIPAddressTextField = new java.awt.GridBagConstraints();
			constraintsIPAddressTextField.gridx = 2; constraintsIPAddressTextField.gridy = 1;
			constraintsIPAddressTextField.gridwidth = 3;
			constraintsIPAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsIPAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsIPAddressTextField.weightx = 1.0;
			constraintsIPAddressTextField.ipadx = 16;
			constraintsIPAddressTextField.insets = new java.awt.Insets(0, 5, 5, 45);
			getConfigurationPanel().add(getIPAddressTextField(), constraintsIPAddressTextField);

			java.awt.GridBagConstraints constraintsPortNumberTextField = new java.awt.GridBagConstraints();
			constraintsPortNumberTextField.gridx = 2; constraintsPortNumberTextField.gridy = 2;
			constraintsPortNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPortNumberTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPortNumberTextField.weightx = 1.0;
			constraintsPortNumberTextField.ipadx = 3;
			constraintsPortNumberTextField.insets = new java.awt.Insets(5, 5, 5, 27);
			getConfigurationPanel().add(getPortNumberTextField(), constraintsPortNumberTextField);

			java.awt.GridBagConstraints constraintsPortNumberLabel = new java.awt.GridBagConstraints();
			constraintsPortNumberLabel.gridx = 1; constraintsPortNumberLabel.gridy = 2;
			constraintsPortNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPortNumberLabel.ipadx = 11;
			constraintsPortNumberLabel.insets = new java.awt.Insets(7, 32, 7, 5);
			getConfigurationPanel().add(getPortNumberLabel(), constraintsPortNumberLabel);
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
 * Return the DescriptionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDescriptionLabel() {
	if (ivjDescriptionLabel == null) {
		try {
			ivjDescriptionLabel = new javax.swing.JLabel();
			ivjDescriptionLabel.setName("DescriptionLabel");
			ivjDescriptionLabel.setText("Description:");
			ivjDescriptionLabel.setMaximumSize(new java.awt.Dimension(95, 16));
			ivjDescriptionLabel.setPreferredSize(new java.awt.Dimension(95, 16));
			ivjDescriptionLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDescriptionLabel.setMinimumSize(new java.awt.Dimension(95, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDescriptionLabel;
}
/**
 * Return the DisableCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getDisableCheckBox() {
	if (ivjDisableCheckBox == null) {
		try {
			ivjDisableCheckBox = new javax.swing.JCheckBox();
			ivjDisableCheckBox.setName("DisableCheckBox");
			ivjDisableCheckBox.setText("Disable Channel");
			ivjDisableCheckBox.setMaximumSize(new java.awt.Dimension(125, 26));
			ivjDisableCheckBox.setActionCommand("Disable");
			ivjDisableCheckBox.setBorderPainted(false);
			ivjDisableCheckBox.setPreferredSize(new java.awt.Dimension(125, 26));
			ivjDisableCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDisableCheckBox.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjDisableCheckBox.setMinimumSize(new java.awt.Dimension(125, 26));
			ivjDisableCheckBox.setHorizontalAlignment(2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisableCheckBox;
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
			ivjIdentificationPanel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjIdentificationPanel.setLayout(new java.awt.GridBagLayout());
			ivjIdentificationPanel.setBorder(ivjLocalBorder);

			java.awt.GridBagConstraints constraintsTypeLabelField = new java.awt.GridBagConstraints();
			constraintsTypeLabelField.gridx = 2; constraintsTypeLabelField.gridy = 1;
			constraintsTypeLabelField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTypeLabelField.ipadx = 74;
			constraintsTypeLabelField.insets = new java.awt.Insets(0, 3, 5, 56);
			getIdentificationPanel().add(getTypeLabelField(), constraintsTypeLabelField);

			java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
			constraintsTypeLabel.gridx = 1; constraintsTypeLabel.gridy = 1;
			constraintsTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTypeLabel.insets = new java.awt.Insets(0, 32, 5, 2);
			getIdentificationPanel().add(getTypeLabel(), constraintsTypeLabel);

			java.awt.GridBagConstraints constraintsDescriptionLabel = new java.awt.GridBagConstraints();
			constraintsDescriptionLabel.gridx = 1; constraintsDescriptionLabel.gridy = 2;
			constraintsDescriptionLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDescriptionLabel.insets = new java.awt.Insets(6, 32, 20, 2);
			getIdentificationPanel().add(getDescriptionLabel(), constraintsDescriptionLabel);

			java.awt.GridBagConstraints constraintsPortDescriptionTextField = new java.awt.GridBagConstraints();
			constraintsPortDescriptionTextField.gridx = 2; constraintsPortDescriptionTextField.gridy = 2;
			constraintsPortDescriptionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPortDescriptionTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPortDescriptionTextField.weightx = 1.0;
			constraintsPortDescriptionTextField.ipadx = 47;
			constraintsPortDescriptionTextField.insets = new java.awt.Insets(5, 3, 17, 53);
			getIdentificationPanel().add(getPortDescriptionTextField(), constraintsPortDescriptionTextField);
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
 * Return the IPAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getIPAddressLabel() {
	if (ivjIPAddressLabel == null) {
		try {
			ivjIPAddressLabel = new javax.swing.JLabel();
			ivjIPAddressLabel.setName("IPAddressLabel");
			ivjIPAddressLabel.setText("IP Address:");
			ivjIPAddressLabel.setMaximumSize(new java.awt.Dimension(72, 16));
			ivjIPAddressLabel.setPreferredSize(new java.awt.Dimension(72, 16));
			ivjIPAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjIPAddressLabel.setMinimumSize(new java.awt.Dimension(72, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIPAddressLabel;
}
/**
 * Return the IPAddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getIPAddressTextField() {
	if (ivjIPAddressTextField == null) {
		try {
			ivjIPAddressTextField = new javax.swing.JTextField();
			ivjIPAddressTextField.setName("IPAddressTextField");
			ivjIPAddressTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjIPAddressTextField.setColumns(12);
			ivjIPAddressTextField.setPreferredSize(new java.awt.Dimension(132, 20));
			ivjIPAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjIPAddressTextField.setMinimumSize(new java.awt.Dimension(132, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIPAddressTextField;
}
/**
 * Return the MsecsLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMsecsLabel() {
	if (ivjMsecsLabel == null) {
		try {
			ivjMsecsLabel = new javax.swing.JLabel();
			ivjMsecsLabel.setName("MsecsLabel");
			ivjMsecsLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMsecsLabel.setText("msec.");
			ivjMsecsLabel.setEnabled(false);
			ivjMsecsLabel.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMsecsLabel;
}
/**
 * Return the PhysicalPortComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPhysicalPortComboBox() {
	if (ivjPhysicalPortComboBox == null) {
		try {
			ivjPhysicalPortComboBox = new javax.swing.JComboBox();
			ivjPhysicalPortComboBox.setName("PhysicalPortComboBox");
			ivjPhysicalPortComboBox.setMaximumSize(new java.awt.Dimension(32767, 25));
			ivjPhysicalPortComboBox.setSelectedItem("com1");
			ivjPhysicalPortComboBox.setPreferredSize(new java.awt.Dimension(66, 25));
			ivjPhysicalPortComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhysicalPortComboBox.setMinimumSize(new java.awt.Dimension(62, 25));
			ivjPhysicalPortComboBox.setRequestFocusEnabled(true);
			// user code begin {1}
			ivjPhysicalPortComboBox.setEditable(true);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalPortComboBox;
}
/**
 * Return the PhysicalPortLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPhysicalPortLabel() {
	if (ivjPhysicalPortLabel == null) {
		try {
			ivjPhysicalPortLabel = new javax.swing.JLabel();
			ivjPhysicalPortLabel.setName("PhysicalPortLabel");
			ivjPhysicalPortLabel.setText("Physical Port:");
			ivjPhysicalPortLabel.setMaximumSize(new java.awt.Dimension(86, 16));
			ivjPhysicalPortLabel.setPreferredSize(new java.awt.Dimension(86, 16));
			ivjPhysicalPortLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhysicalPortLabel.setMinimumSize(new java.awt.Dimension(86, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalPortLabel;
}
/**
 * Return the PortDescriptionTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPortDescriptionTextField() {
	if (ivjPortDescriptionTextField == null) {
		try {
			ivjPortDescriptionTextField = new javax.swing.JTextField();
			ivjPortDescriptionTextField.setName("PortDescriptionTextField");
			ivjPortDescriptionTextField.setMaximumSize(new java.awt.Dimension(170, 20));
			ivjPortDescriptionTextField.setColumns(13);
			ivjPortDescriptionTextField.setPreferredSize(new java.awt.Dimension(155, 20));
			ivjPortDescriptionTextField.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPortDescriptionTextField.setMinimumSize(new java.awt.Dimension(135, 20));
			// user code begin {1}
			ivjPortDescriptionTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_PORT_DESCRIPTION_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPortDescriptionTextField;
}
/**
 * Return the PortNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPortNumberLabel() {
	if (ivjPortNumberLabel == null) {
		try {
			ivjPortNumberLabel = new javax.swing.JLabel();
			ivjPortNumberLabel.setName("PortNumberLabel");
			ivjPortNumberLabel.setText("Port Number:");
			ivjPortNumberLabel.setMaximumSize(new java.awt.Dimension(83, 16));
			ivjPortNumberLabel.setPreferredSize(new java.awt.Dimension(83, 16));
			ivjPortNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPortNumberLabel.setMinimumSize(new java.awt.Dimension(83, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPortNumberLabel;
}
/**
 * Return the PortNumberTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPortNumberTextField() {
	if (ivjPortNumberTextField == null) {
		try {
			ivjPortNumberTextField = new javax.swing.JTextField();
			ivjPortNumberTextField.setName("PortNumberTextField");
			ivjPortNumberTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjPortNumberTextField.setColumns(5);
			ivjPortNumberTextField.setPreferredSize(new java.awt.Dimension(55, 20));
			ivjPortNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPortNumberTextField.setMinimumSize(new java.awt.Dimension(55, 20));
			// user code begin {1}

			ivjPortNumberTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 999999 ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPortNumberTextField;
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
			ivjTypeLabel.setText("Channel Type:");
			ivjTypeLabel.setMaximumSize(new java.awt.Dimension(95, 16));
			ivjTypeLabel.setPreferredSize(new java.awt.Dimension(95, 16));
			ivjTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTypeLabel.setMinimumSize(new java.awt.Dimension(95, 16));
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
 * Return the TypeLabelField property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTypeLabelField() {
	if (ivjTypeLabelField == null) {
		try {
			ivjTypeLabelField = new javax.swing.JLabel();
			ivjTypeLabelField.setName("TypeLabelField");
			ivjTypeLabelField.setText("");
			ivjTypeLabelField.setMaximumSize(new java.awt.Dimension(105, 16));
			ivjTypeLabelField.setPreferredSize(new java.awt.Dimension(105, 16));
			ivjTypeLabelField.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjTypeLabelField.setMinimumSize(new java.awt.Dimension(105, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTypeLabelField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	DirectPort dp = ((DirectPort) val);
	CommPort cp = dp.getCommPort();
	PortSettings ps = dp.getPortSettings();

	String name = getPortDescriptionTextField().getText().trim();
	Character disableFlag = null;
	if( getDisableCheckBox().isSelected() )
		disableFlag = new Character('Y');
	else
		disableFlag = new Character('N');
	Integer baudRate = new Integer( ((String)getBaudRateComboBox().getSelectedItem()).trim() );

	String commonProtocol = ((String) getCommonProtocolComboBox().getSelectedItem()).trim();
	cp.setCommonProtocol( commonProtocol );

	if( val instanceof LocalDirectPort )
	{
		String physicalPort = ((String) getPhysicalPortComboBox().getSelectedItem()).trim();
		((LocalDirectPort)dp).getPortLocalSerial().setPhysicalPort( physicalPort );
	}
	else if( val instanceof TerminalServerDirectPort )
	{
		String ipAddress = getIPAddressTextField().getText();
		Integer portNumber = new Integer(getPortNumberTextField().getText());
		PortTerminalServer pts = ((TerminalServerDirectPort)dp).getPortTerminalServer();
		pts.setIpAddress(ipAddress);
		pts.setSocketPortNumber(portNumber);
	}
	
	dp.setPortName( name );
	dp.setDisableFlag( disableFlag );
	ps.setBaudRate( baudRate );

	try
	{
		if( getCarrierDetectCheckBox().isSelected() )
			ps.setCdWait( new Integer( Integer.parseInt(getCarrierDetectWaitTextField().getText()) ) );
		else
			ps.setCdWait( new Integer(0) );
	}
	catch( NumberFormatException e )
	{
		ps.setCdWait( new Integer(0) );
	}
	
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
	getPortDescriptionTextField().addCaretListener(this);
	getDisableCheckBox().addActionListener(this);
	getBaudRateComboBox().addActionListener(this);
	getCommonProtocolComboBox().addActionListener(this);
	getCarrierDetectWaitTextField().addCaretListener(this);
	getCarrierDetectCheckBox().addActionListener(this);
	getPhysicalPortComboBox().addActionListener(this);
	getIPAddressTextField().addCaretListener(this);
	getPortNumberTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PortSettingsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(385, 450);

		java.awt.GridBagConstraints constraintsIdentificationPanel = new java.awt.GridBagConstraints();
		constraintsIdentificationPanel.gridx = 1; constraintsIdentificationPanel.gridy = 1;
		constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsIdentificationPanel.weightx = 1.0;
		constraintsIdentificationPanel.weighty = 1.0;
		constraintsIdentificationPanel.ipadx = -10;
		constraintsIdentificationPanel.ipady = -3;
		constraintsIdentificationPanel.insets = new java.awt.Insets(8, 8, 3, 10);
		add(getIdentificationPanel(), constraintsIdentificationPanel);

		java.awt.GridBagConstraints constraintsDisableCheckBox = new java.awt.GridBagConstraints();
		constraintsDisableCheckBox.gridx = 1; constraintsDisableCheckBox.gridy = 3;
		constraintsDisableCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDisableCheckBox.ipadx = 69;
		constraintsDisableCheckBox.insets = new java.awt.Insets(3, 8, 33, 183);
		add(getDisableCheckBox(), constraintsDisableCheckBox);

		java.awt.GridBagConstraints constraintsConfigurationPanel = new java.awt.GridBagConstraints();
		constraintsConfigurationPanel.gridx = 1; constraintsConfigurationPanel.gridy = 2;
		constraintsConfigurationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsConfigurationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsConfigurationPanel.weightx = 1.0;
		constraintsConfigurationPanel.weighty = 1.0;
		constraintsConfigurationPanel.ipadx = 28;
		constraintsConfigurationPanel.ipady = 29;
		constraintsConfigurationPanel.insets = new java.awt.Insets(4, 8, 2, 10);
		add(getConfigurationPanel(), constraintsConfigurationPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getCommonProtocolComboBox().addItem("IDLC");
	getCommonProtocolComboBox().addItem("None");


	getPhysicalPortComboBox().addItem("com1");
	getPhysicalPortComboBox().addItem("com2");
	getPhysicalPortComboBox().addItem("com3");
	getPhysicalPortComboBox().addItem("com4");
	getPhysicalPortComboBox().addItem("com5");
	getPhysicalPortComboBox().addItem("com6");
	getPhysicalPortComboBox().addItem("com7");
	getPhysicalPortComboBox().addItem("com8");
	// user code end
}
/**
 * isDataComplete method comment.
 */
public boolean isDataComplete() {
	return false;
}

/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)
{

	DirectPort dp = ((DirectPort) val);
	CommPort cp = dp.getCommPort();
	PortSettings ps = dp.getPortSettings();

	String portType = dp.getPAOType();
	String name = dp.getPortName();
	Character disableFlag = dp.getPAODisableFlag();
	Integer baudRate = ps.getBaudRate();
	Integer cdWait = ps.getCdWait();

	if( val instanceof LocalDirectPort )
	{
		
		getIPAddressLabel().setVisible(false);
		getIPAddressTextField().setVisible(false);
		getPortNumberLabel().setVisible(false);
		getPortNumberTextField().setVisible(false);
		getPhysicalPortLabel().setVisible(true);
		getPhysicalPortComboBox().setVisible(true);
		getCommonProtocolLabel().setVisible(true);
		getCommonProtocolComboBox().setVisible(true);
		CtiUtilities.setSelectedInComboBox(
			getPhysicalPortComboBox(),
			((LocalDirectPort) dp).getPortLocalSerial().getPhysicalPort());
		CtiUtilities.setSelectedInComboBox(getCommonProtocolComboBox(), cp.getCommonProtocol());
	}
	else if (val instanceof TerminalServerDirectPort)
	{
		getCarrierDetectWaitTextField().setText("0");
		getIPAddressLabel().setVisible(true);
		getIPAddressTextField().setVisible(true);
		getPortNumberLabel().setVisible(true);
		getPortNumberTextField().setVisible(true);
		getPhysicalPortLabel().setVisible(false);
		getPhysicalPortComboBox().setVisible(false);

		getCommonProtocolLabel().setVisible(true);
		getCommonProtocolComboBox().setVisible(true);
		CtiUtilities.setSelectedInComboBox(getCommonProtocolComboBox(), cp.getCommonProtocol());

		PortTerminalServer pts = ((TerminalServerDirectPort) dp).getPortTerminalServer();
		getIPAddressTextField().setText(pts.getIpAddress());
		getPortNumberTextField().setText(pts.getSocketPortNumber().toString());
	}
	else if( val instanceof PooledPort )
	{
		getConfigurationPanel().setVisible( false );
	}

	getTypeLabelField().setText(portType);
	getPortDescriptionTextField().setText( name );
	CtiUtilities.setCheckBoxState(getDisableCheckBox(), disableFlag);
	CtiUtilities.setSelectedInComboBox(getBaudRateComboBox(), baudRate.toString());

	if (cdWait.intValue() > 0)
	{
		getCarrierDetectCheckBox().doClick();
		//getCarrierDetectCheckBox().setSelected(true);
		//connEtoC6(null);
		getCarrierDetectWaitTextField().setText(cdWait.toString());
	}
	else
	{
		getCarrierDetectWaitTextField().setText( new Integer(0).toString() );
	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC8F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBD8DD814D53AD6D8E4D43451C666969696A5959695959531546A2EDBD614D456655E584DDBF6AFDB6E2EDB5E6BA77FFF669FAAAAAAA29694A14C70A761AF23C2111122D2CB6948FC4068B0B34EFC4000035E771C731D737D704DG365EC71F67B8733E67FD4F7BF35EF33EF34E771E014B5D94143AB8A917639627F2623F1A459CD7FAAD472DBBD7FD1862467151E91C6C5FE6A84B393FD7264027BF54
	457EA5BAED99B7ADAB8360E0008DF32253564077155CA3D981FD7045039C8B547D78FC61A66FF20CB6AAA79D43DF24644067A7D0C2G07BE6773C8FEC3CA1E081FA962E9D91C44F1F11D08666714FC91D78BF08B14GA8A11D04BE8ABEB3316471F535A2DD7B8DC15C7C5FFF16348435A3EB131602AE42EB90BF8B3887964F6BC2322E42FAA29EB74061D01071495C7E83286D7A1653DDBBBBECBDE59DC62B55E695780E6EB25EE496EC0EB23B4DA1545535407F07F9C1B0DB3B1C7BF0C50B514ADB3A3BD15BAF24
	E46036B82E9C607998D723C372C261FB8C14B98C7F3F98DFEB86FB21F41B290DBBBE2B97EDE8BB7BG97BE5B1A3C54030D634C44369033E4E3C06FC769EBF1FFCC36A33FA7C0DD46296834FCA83B211C00B28E4AFA9C87A1364F61F34FC921415653E3336E315B77DACC0EEFEDA65E825F6B6A20C6449D62BBEC8E9347113E3EB625D4C39FEF05705DBAE6BE621390BD6D7750C3F7F1F39E7AA6A9DD43A7F3C24A1382CF91DF041D12FCC2E447FA14BD6B14B2AE15320FF1F25941D75A12D3B5E4073FD21214A036
	33C0A63B78B46A6B29382F73607BE2B1BE887EE10C374870A4C6F7E0BC025DD097FF1A46C65BA7D4176614095C320AE6CDBF84C66FC9B2D8088E6D9635AE937528EDDF8B11391269920A7191B2BC51A5856391BC976AFCFA2253C8FC5D6BC0E336409A1DD68F25950A850A9F1490A811D69AE3FF31ED9BE50C35989D425EDE334544BBB842FB2FFDB5FC72AE41E1EC32BA85233503BFEAF4180DA7AD3C1393E99E32A39F1CD470C560586FG9987F8AB6FB08AE61B95DAB77670826FF0921AE76C4B910D0347352D
	669E9E847458B95A6E8CBB3259E2F48AC76CA62340CB94247EC5BB6A13BE63D1E8919A006F44AF331D48DFF3407E79D0D2EC548F4DE7D0FB5F708E18AD6A6A1AED36533D76D6E48957EAC3FEFE8F4F9DF9703D5CC6FD7C3200F001G0731F62EB833C2560E415C0154B7BA8668981B26C373F20C0D464CFF355118B1AC1942153E7235E64C20B257C6E205332B474E9338CD2497019BF4EDE39C47A315981BB486090C76FF390CE459F7625FBDFD282F0A41DE8394131D7A7829B385A4E6779A1DBC0D526F9C90E0
	3CC972B851B3C8DFA47A934119229C74DC4A96E3E9A92623F6BD08695DA0AB984ACCA87321641F819D20D82084C009BCC3FD1D2577975B277AD3B959229C5A3E5A5E757FE27F8D1F4FAB30EE4922BE881FAA56D17B17F3E46ED073AF3D083A11BE8CF7A099FC9FEF958C3C1D371A600BD32CFB420174956715CE9B63F986636DB08FB54040F7B27C6F9C488E23A0B84CA7FB851EBE7FD97D8BD816337724DD1A42282CD79D48A736FE1807987D9B8EB406BBECD6AB0F0309310875EF62B9043770BD20F4032D57AA
	907C5BCEB48ECF7383629C760E93AD4C7A0C16DEB49F12F5CC1891795D6EB077C00822A915EBF42079A5A9B5CF66238408AF349E4B073AF2871DAB7A37503922E8F0B297F5FCEB125633AEF00AA1495FC146EB30C31AAB08CEF102366EEF6139CC44BB092E46DE349E75302492476878FE54DE8C480903126820E37447DED4CF67C1F1F0B658AC9662D2666B83FD4BD05F70E7FAF9F8FC2089779B663F03FDABD4F5C7C5DF127A23FD48179659DCBD490967EABDDD2FE6GDCGE5371378536DA47EA0F477D7D23A
	39GF382508ADAF40EAACAF7826ADDD082B5692A2BA9DD9C54A7C2D9562BC5779123EB017A6EDE0F7A55D0BADF2089029225591E231652F1FD51E9F9FD1E747BF4BBDB4783CDCB1FA7392FB3BABB5078EA501178C8BF0E56ECDAEB722EDEF3D92FE0C6CFC023G03585EAB48566116548C310D10BE3A5E8E4CC0F1948F7082A84B7AE94C3F38042E3783CE87F171F72E733827A86EA73AB577CBB1DFF085DDEF3F60C2B27D20EED2BF0D51B9FD72E77E9E275356E1464B8DB13E2E69170F8902736F4F024FCE3343
	A9B44124652231786BFE24AFDA0F08E8F1FD16DB19A35A9A86F267C29967C278EF2AF2E53A97G2E910A85CAB2EE6FECA5DA17F5989D3CD0067FA74B1C62D346BE232B4C598F23318C4F1EE58D28F69F7ADAA276CBCC498A22A30FBDAFF2169C25EB66BBD66E96654E3CFFA2D7FFFEE512569E46F769062469AE624BC41764D37703E94973FE7EB81D58B71B59D7BF901DA68CD07B1E6E135B97BAC056CD3183543E1DD5BA78B46249200CFC34700ECE1B2307B7E1A34B0C7D02E8619EDCAD1AA87A7556AAAAF7AE
	34D9B0C8653E20134BEDC3F8A84103D4EED275AF13FB3C1A4A0D00B693195C5B9B3564961F25F21F7B05F23754B0BFC31B42D9AAF71CDE9DC7C8EEA8540B7D437822G37007195D72A7B8771153BD97F547E427E2925F28D50265BCD652ED9256527F0B755773B6D14EFBA6038A14A772D20E5E76D9065CB2E23FC4D0073E1FC02CE0BAFF2082E5F4E242A771D33062435EE4250703DA7590F65E4A2F98B0648FEAC1D6173B0FE59B989CF743B8D4FEF188E6A7C4E51BD444F0368391F88F04E796834A2A875D05A
	21384F53F9EA23CB4F0D30C7EB02F957E1B5DA8EAB96B9625C946BC2FD8A6B92910EF3A1BF61F589C7570A6BAA55FE88BC2F34BDDC85933E62E1FCE5AAB8CE45C766282D456899031F9D32A92D2E6E6BD6D8776DB7739693D997FD1FA9DF97A51EA773C86EB898DBD048736472AAB4CF2367426EF17439B134DF7E5C08DC2ACB4A561863709AE99C51AFE61CB4471532F31A8CB752B365B218E32054C2E905620292GA562B25AC7A787F60C54C7F800345A9AG5788EB1686757A1279744D8B6C0B77C66E0B4B34
	7AE26751755AFD51504DF71C5EEBF3F1444FFF6BC3FEDE057A61B2BAFEA6D7DD182E8F8C63D36B5AFA39162EDD96CF3A5AFACE5A102EA4EEAA842D3831C01B63AE27F1132627F193F8B935072F3EB0FB1C43785476ACD459C3F4F835116A10FB855521246642F4289946275641F505168E0FE2BF203510BF53614D8B54613D91F5182F2943714A8C310D9C9F96DB359798DB437854BA98FC34F4B8388A690056C26DCC07A35BAFCC070EE1FCEA9D22B4F57091E8BC44B19DB26BAECC07B5437854BA540F57526191
	9D5521956A497917AFDE33C7GBC136178F404CB84B863CA2A6B8A7C7CAB853899CAB743EF44F83446C57BAC748CA678608C2DF3A444CB3F17DEB97C996C29F4DF296507A7B5E5244A3EA70F32FD5A269CA673FF572067738C6B2076F906B028FD1EB1B8381C60218D7929AB2D3FFC507BF94704C12D730E8D584E992B1762361DCE5E099FC8E803A6563DA32039FD583947C1E38FAF7AE8A577FDE526C833CF30B5D4F0F4DE7E30844D379A7332EC5FF7F5A60A97446FFBA55D77C51EC4F19185F09C14C496AFD7
	D5F9BBDF4C38CA594F79A2DC8E1F86D5DD9B4096950EBC5B2F9FC2EB43F1D0B789CAB81419D066DDC51F6985E7C7DCF76DF3F9D9F72DF0FB0E0935EE6D185864C6634EE96BF5F4706AFE7F541D476A2455C313407720AEA2B45BB03FE0F4F4714362269A7313BACF7C3B5C1EE20B74FFEE153C7FAB59987B38CF2B3FF2FC45B51578D969AB75C3A3FCEF07E277D5760D8F4001D0C221C409F5C4F61CC2F695C767AB33C01F9D313EB42E3EF621384A399A5654D06A2F26F1152D7796D72DD793F95DD7ABF5F2836C
	2F4285839C214291BDA7D54B7554B19F3D5168CD76CC312DF8D51B73814E18200AF7004BA7E849BED1A317DD43E4C7FA355BGED35495A33085F4741E700CACE884011D0E609780489F44F18DBAB17DD4DE41F5AA5FF8EAD9079F22E91E3631A61733B585F0A76742C3D432B3C59D2A9365728EA379DE03B8A6783F020088B3DC69EEBFE5B6532EB196C2B85EF3223D46D470A70DC78DC282ADB668731692755073B6B643237B359CFF8FD67D22BEA2BD904CF4027202A7385F812266C5BD351193545B8E06B95A0
	4E2E456B81F6B69D86BC5321A4C2C939162C29497CBF2D9F6D2FBA9D469E18AE8E5B798EEE8166F527D3DE9340AEA801D0A22044DDCB63A66C9B3476ECC2532339535CA13E8FC4E74C241E2C498CE6A7BA63E5FB92D2775292F4568533D927392B57A1E395FDF6DD26BC76F6DF87EB97A85D57915B5D57F95A5F17F72079437FBA320F4FE4789D989FAA43137DFDB4FE0EA1F8BA5415DECF57C877603D29BF40A18CB79D6F23A2811E45F04B71D9E382401CBF4595E35EDC000B986E79BC3A662AE5B8DFCC678BF0
	10BFFDF65DBC647959F35B107633A7F8C8FBBDF26F1077754803C31E1E99C43F8D5836F050AD0669FCF9BF754BAC06BB0C571D89GF3B7D05C3B9817FB8371B741E568292F4A995DD2017A4A40F0DF35A2DC9B409606EB96282FDC8C77851661F78309E5027BDAC77D9CFA83FDF6D885F9ACAD847C32DFC971D374ABCF31D4B800F3C9FEC5EC5840705BB15EAD4313D8EA414FAE84C7C1DD680DD44FAF315D018151E9E1819437865B9383709C068B4538C400F3EE22B8973627G60F2064BDFCDEDB4B0DC831691
	GF0A8437D8F5E23C6819C47F006A4041B87F04A2FA96EB50C4B83381261966CC638C6005B98AEE0A315EBE1387C03F8CCG9C4BF0EFE1DD66824C5DCCF183241F81AEE638E80C2B3F197413480BF1AD0033B03AD9588763G76E7387D588741GC7B01CBF464D84B801617E1345438206FB8F4BE88C04F5CCA045DD368B613A81F6B1DCD98E0EAF00A7B15C0DB9343D90066B457B4F85GAF3B05628EE139C5G57B35CBB98578230056136E0DD46816C4FF04ED6AAA31861D2FB91EEBE40B993A96EB6929B9349
	FCCCF0D3F1FB3B81574668A6E3BA4BC4F2F6C9F0CF921F82EE9223AB413CE1GC7B15C4DCC17D806DB01F5A93A95EC3B15625E54D139AD8C7732134A4DE6F25FF5D239A6C637CDC7653A98AEF295156B47F0B76B285CB93752736337D2E95ED9A2600AA75167DA458945B9A5BA752E2BBB60B01B769A3B1A71D3122CC1BFCD4B9079CFCC22EB5056B454E6A040E1D026CF22E7132ECF6959E468DD933966F15FE83E530AEF55A545CFA2F3CA4AEF243349105E0F52094CBB52294C5DD06F7A8D0E71F479F9B76A4F
	7149E45FCF6A13B26475E85EC632257ACAC5BD4E17D070F7AA6A110FCAC4FEE25B9F36D25B967EA10433BC3C5D637B3AA851268471336568962EE8424769240F26603DE27EED70AC3D0D765BFA1CAB399B418CB7B19B61DCG7BB1DC7A925A67E13751393B3A833DE766EE0FCE4B07D2892565F616AF036B84008320045FCE5E99133A69A7BD2D39D0BB0AF5676D4367FFDAB8D11E9ACFE47C3BC799299A741EE44873BC062F6B7C87505869B13A4CBD3DBD074D03BC179304FC7592DEBB4402EE71375375583F75
	A1B99DB6CBEF0F55A9521133DF743CEF8922F465D528EF6D8E3E13F7B8F893EE1593696B70737D84FC3784C96D227C08CE1BD5A0B45FE3590170BD98CA46E4047BD31E410B0D299CBDEFA0A5967866C259A262974277631C140F3312B338305DE62B4A6EE9D8F68B48EC1FCC752B6CC77A8930B0721D0CF2EA57613CDBC2BF955BB39370F10C7E148E59CFDE0E21A382235544682F40F3CC4A9D707D8ECA3FD80F68CF5AD0B2C40B51EC95F8093E82BF6B0A0136928A415562F97484409656462156B45C0F0EAE33
	9556B1D84F71E9121E3D18E7925007B01E3DBA34066E36B94C0360FB23E50F455CE5C56F34B962F3234E1B4F134538E3BE07F6D159A27AF9AEFC3FC67CCE465FF8010E4979F750B1B9E393DE9B5C896379CE0AEB5CCC47E94B1DF43C958F203DD69040D1D066C0D988A5AF981E91D0CCD05CD0022044C0C90042CD01718AE577945A46AE5CC6BB406EA9A49F8265ECCC1AC2F2ADA22710DC8A141FB1873EAF1CC2F2A1D01EC37EDD340D232EF8198F10ACCF7E71D4106EDAF8E4DB02E88B5E0384937DE9FD1EE89F
	3227DE34EF2C3223C43F0DC4C7EC3DC1E16B2AB14A72B6AF21F8C1B9C4B41F8CBDEF64F9EF599C595F2135AE9A9BEBAFC0B69A24DA731E76BCE850EB4F036DF87C55C37FB75EC547CD994E15F272A8E709B721F3B2C23B1F44B75079B05A5F8A1E6742B9A4CF94E803997D75BA65DCB8854F973320FE4EDDF4AE542F9EE9AE4C64243119F337F2AEACG7886CEBE977E28571E8B9D5876D2202F3D1B1D65B4E97B293489511A004E4EE87F19246DA7A75E8F8481DDA823CDCC726C274CE43C4FG5A190CFE699E25
	1F366E41EBE428CF8921FE1A3AF1A4BFE54B7CD4992274D3A3408FA87C347320361F0E62BDCAAB501F88217A7D55C3BCAD416F337C00AE10517A8BC807BE33530CF255885D5D823D13B013513DA6481F3F229CAF717589DEC74FC1F631B65ED145577F10FD74BDB0575CC3E9FEDA0D6470D6F42CE2925B92D653B5E5AB233BFB3536BF1E46345DC0A75CC37B0253FB6B0BF4F1AE204F1220FB14FD918EB0DDAF0A79BA3D686156516BF05A9C74F9378109956B83F7A96D3C9F6F3F22201D19CC7FFDB94A677AEE3C
	D4A57A5E1BB35233EF09ACF6664A569BC84705A063A6053EFE2B3463A1986FC3B26E0DCE4B3D176AE556E90F1B4BF0DF99002E05517E2F4EF37F2F4163578434F6C6DFA7A87D52C25685D09FF2AF6D2B892DA30D1BF4196DF177AA7BEA9E4077A9EC4F6F550E13CA9C1F8B00BEE5AA556FE13CA6F1205CCE27305F56516B5CA746A129DFF2CC5B0F31580EF2E8271E3595219BBEDE863D0C17BFE37F5A015F4D5AF8D4B5DE8EE1B9A1D09FBE157A6B7E917DA5DFAB4D1D2ADA1B823CD9612FE08F7371D758DF9921
	90AB21F4ADC8F295F6835C4CF0AB491E9FE0BF06EB4336C5G9C93CA75FEF955D856548BC215FA675CC756C40B197F22300CBC40975DC757CB89437228C63776786082793EC89BAB9FB9F37BB49D0D39167B48195BE2065F06716DB2BCB99778B10D1E8BF4C35D4C7BE85E5616ACF42EDDFEBF7487949394B714C9D0A26E27E7300D674A39915E9F365A6C4DFC9FEF119D2B927B6ECE3F303BD84F24537DF77863F7F091D77EE331273C271977137561BC71F3EE6B960CA776BD120D6C4BFFG5684D05A2188D0FC
	2184BDC06D73BFD7CA6CC3E961FB844158510DB3E529F1C74DFC3F04C6AF85C85CC7E6AA475ACBF82F9F8E6DC6BDC04723AEEB78FB0826AC7AAEA17181729EA2A38C566CE1A4F71670CD4BC6FCE40FC57926E7D33ECA20ED066283A5D064A3E3E46312111E837292863CB1E1E40C6039913E3F3FE851220F2190BDEEBB0278DA876CFC3371A4EF4199EDF45C7FDC12A92F63A81E6350B90401F7F6B84CF634294455348E5900F271E575ACD70E64BA76F7484FE69228FD53E8AE6469E3DA75792CBE31D72B3E9C6A
	49F3530426D4D157A77B5085D4AB5FDF4012C18DE668E6F4592A3822263C34C0D7DEA246E4D03F7CDEE8EDD60D663D85D48C53C8AC36091F11D7ED214F037EA4782C5053B540731FC83AEC287226CBD5955105706565CAFC51F905EA3E7D90144505A63351E26BAAACADGB3AAAACBB8EE135887F861017FA56E9A0E233A0DC3DBA85176FF4F2163B1FF6A2D1CE176BF965335035AF6FF516678E952D95F5CD38B53C91B6F2E16743E432B6F75D5F25F3F70CF396F17CD5C1C3458037C3C87095C2287A579C47621
	BCC9F6234E1BEC9D12DDCDFD0D2F0008FC89BA24CF2178922F1079AAF01DC413A20C3E6D4A8D925F36565176A361BD36D162AD9815BD1E7BFA905F43A6734C7DAC67306DC1B24F58615367C1126FC46445EF9ABE2F7DFD93651B74A0195742512748C76627631BC71A1F644F70F8601D7F20B4BFE5BC941D065EA3FF45C9F38D5A0B37F48FB84D9DC68B1AD754F3919A6CFB0D3D26C3C6E158BC152B6225491772F90C72AA6B2A8DA8109B0C8E0719F798F881163E470CE6AADCFC6F1F36CE0B064DF6146E454368
	AE4637CE3E4329607FBEF7151A1F3DB516EC40FA6087F30B43A658E07948D2C8994D4AE1B4C4902DC73EEFEA59E3B2B9F827D2870EDBA12F9B26BBA948F787FBFBCE728E09C07E4EDB2AA70D13F9FAE8C0BDCF23B849FD084E638D2DDA75452C3EC6502A2FE7759F6A346ADBD83DDF1AD63D0955A79F552AF7317A371DDA75FE8F51674CEF8A247119ADEC76BCBE753A1A6A9A9821D5A5ECBCC6ED16F86F965018A8541717CB734D5B05D2FD0FEE236736EB4B450689DF6CE7925F32553F6439C15A7BFB31B4072C
	7BDCEAFB1D60C5271AEA05CE5FFFA17155A60D4DCF78D536489B1B04662442A6C3F3C3A12D7F6B17D27D7B3B3D68D4DD25506955F2094FF763AF7753BFAAA4BF656B2536EDE95EF42AD068045354C4BE0B7029E7BEDDC5E5CDE52D3E3A3206BCC2C51E2770DEDAF07472243D4EAA293DE73D4510DEAF36C37822EBA43E6D2B37EA7B46E934BA1D3C435CA9F30F48F34DEA944F1DC60B13E7BE79D32D5466DB235245332FB36AA4DF5B6A25F68BF34676FC7D558E291D07F7CA6D6C570DADC663F6C93C7F2143B15A
	E16B29E0B17A71EE29BE4F5B3829D60E1B7FE810783AC63DF6706C3754FD123D457B2536EF74569F55BA05CEBF9E107882F4E309D142BF54A871EF743A0E526B646B61573E1678A6340EA5A6BD7BE361B712BFBE7FD6EA7F18573516D22F6F8FCAFC4FB9476E8F61BB093F48DBBF506D01483776184477402AFF0DBF6A5BA4FF7C33D7EE57E874A267812568C37CDEA57BFEA26DDF7DBB8C434FDA86AE2A8CF216D01A65B9DFCD1F251D2F36BDCBBBDFEDD716777C7883D91EFF8F60E0167C77G48D9489778AEEB
	902CCFA22144C3198FA5A31C5DA34EC2F3887A251461E7A07BCCDDA8D79B7DBA0AC065BF1D6D59566732C77ECD159732513A7724CD90ECBD28FD0AFFA59B7517E033E3A4413D1E0DFC64B0F7F58B04D44447633C898B5F0951621A44A54566522C514E0B641C6CCC96BDA7AB823F340753BBD60FE55177898161B4AF669DCCBB936084A88B43E98ECEE2B67DDD1D5C07E17F74B04D41F9E7839ADF5E6F19371A858B3FD7BC769173429BF00E95EA4367E116131303E4C4829C8BE59E14516D6D69D8DEB748F16C4E
	C32707BB8FF831CEF44865D19BE8AF15728875499D47A82E1B615EF7D15C4CC76899636F8671D97723600BC7E92C5D3384AD7A0E9ACF092709649799F0348192F6A9F65EA18C90AC7E3584B106135DE87FE801F6FC2104C209019287A591CACAC4F4DA9E14D2A886A86DD06CD0FC2084C289019289E5D684556359D5A5DE74909324F060BBD026839BCB66A10E45E0591A2D1E5A001173B538743B1327E07356EA6C62E81C3DAA5098C9GDD729F237E5B58CB7B3A75B15A572BB7A1DABF000321C4BEC663ECD381
	0D55D84065BCCE5B68AA2031EAF81C36F19326F5839CGA5747131464B0CF39C0B4770815A47B1CC66E0AB4595BFC1F1D6FC2E0F7630FE8C77B9BE7FC7E739098C775BB4469BC9F1F5C7A94E0F615631BBE50911347F74FD1E67203A3E116720861C8F44435C014EB145736EBE34C633F5F6BAF9B657FD0BFF5F03601A1CADB627999DDC1267CAFF56051D7D0F3FC03E27464CC75E8D7C934FCB2DCF2A5F9918B07E44136AF7866F33395082F5331E246FC87E16BD127C4A27346E2F25640C4437D1535EC64C5772
	546872C7CCC0C76C1A0E67C857D3EAFBDF44F81F27063DA34921767AC35D7C27283DEBF92DF5C35B53C49F4B535AFA0D83FC002AAE8460482735FCF377B1AD9933C45A848FB296GBEA7CAD9D7GF0E514160C8FDCDAB29AC55A76A8ED99F6407B286A82818ED56148B819B9083E33B58B193A453A1787517A43EC0521E334188771A50743C0E86DA275EB3C5CB35B689EF9ACFF6656DECFFC699E3E1E503935579335EE79FA0260F63851FC5C854F02B6C6371761FEE0388338CD04BBE6B6895D8C7FAD568B619B
	F9A410628FE33D7AB12ECD443549F0BF08381F5CE8BE82133BD16E00F8CF5F0D5FBD19AD9682F7E29A339526A7660F53D8C7335DA8535B4EF0BF303818A3DC58F8FEF7C4BE6D713C259771C5F908B7F5090D2263F982FE8E4E0DD20F679BB1FEFE14FABC67EF226319FBA6BA4D748C9D4FDB8BAEEC5E55DFA01FE54CFC442E4C4DC87F694F286DDD0171314F286D3D152D8762214E904D72884FEB0D7B10E8715DD434F6BF4C82FC022AEE8140B93F550A05732DDAB28AC45A4A5FEA4BE884FC3B2A4E8E300F268C
	27661D35C011B694BE2320442AF867823CD005DB96931DD6842596CAF38C29A3B2366965FDF5C22C9344CFDF781C84A5ACC6EACF4C2F565CDB4608F4F1B15A764F83BC772C32AE976062E7356C5FF5D4CBC63DC85B722C368C9360DD2ABABF0003B4E5EC5274F138C8BB5D030CB900CFD45525BC87EB716734E4601F0E23F7AAE55FBF4C3A78E7GB95997D38639FF9E35922DD14DD60B594AB710DF8765E8CE71F13C17AB853F981E237943F3709E368DE04BF3F44CBE1FA379E5366C7B091C7F07F3120BAAC33C
	53067713AE30571779A1886382G8EE53836921C8787709C06ABDC06732D8166FEC7F10B3E23B958398CF78B5E4718GF6B35C8C3CE77587B808614A5A90AE946098063B9B67494781BC1F612E4039109953A12E27D31C1E5DE3ACE538DBB01D9B60G064B45FB269000A398EEG63505D419806DB0B7379511D40B806A377AC511D40F98C7759B1AA171B416E55613DB73A935842F0F5F8BFE982584DF0EFE3FB7D818E1EC1634DFF1394E337483E97EE3A787176557F030C251BA51B72E45FA797DCFC590FDFD4
	9924CF0D67711DB1684FA8564FFF4338D80067B1DCC39D5E5B7FBEBAAD7F77947771D9FCBE8370EE064BB8C0632B156104A1048B86B81261A663FD7FAC0063992E6A883E1B87F04E9FA8EEAE16D1GF0BD4335E3DDDAGBE41F05529D42E40F04DBB91AE8660B98CF793399B8AB0B71362BE42389C000B98EEBC4E712B853819611E473893G7BB0DCA139138AF0B843ED476DCD87B80E61A6E0FB67814C7D1162E2B1AE9760D206AB93289D3B99EEBD16618FF098436D47389800E3992E9867A34F85F88143DD4B
	6E3826BC4F66A34C5B8AF0B7437D91635CG7BB35C3B78FCA59860F006BB0F6F5CCD87B81E610A8C385FGDE768245FD007B3C8860CA064BE7F7A09B996ED39CEB01G07B11C911F6144G9C4BF0FF43B8F4AFF2AE43F5107CCF40ADE438A0F687F259ACF69F13792008612E457622BB1082436D9128DCDF06FB095D831D44F0FB591D4FB0060BF7D239B18CD7416C0DE3384F85AA3778C51A9353170376FF82BA41C5E74C9C4DB33D8EBF736B012E1151FE181DCAE9B97ACEE55EF9B4B4C97DA1DEF507EB31D477
	A82F1DD37C82FEFE3AC106AF13E3AB711CBF6C0B1F2D61C09B456837AFD366B4FE071F357320FE410BB407E7F54BE87207E9D1671A173E244EB58F63347307276367F7AD509BDEE2F9C74714FE5911A479E5DF1B36DFFE424FBBBB3461E66DBC556F59AF337133BF84E843997D44CA25DF4270B3BE9E6A673DC47D723557BBFF6A5CCB35DF72DF1676F322DFAABD78854BAE867A4A1729FEDF8CA87DF28221C57A903C4EB881341617E9FEF593393388F0B0437D952FB3E681BC67E5EA534EE3E34957D25B1493AB
	59A45ECF42EB14BC409745D25D3F6F559EB7E63CF6E9813AF6C67B70466163660A94A986DC67299F44310767A5FF601F445AD842E94749D69CEF91C09743E8FF2C739CA7CE7C7CCA845A050CFE05DB99A74578BC297895682BD728CFB79D984BBDC535CFCD504EF2C51CDCB3249DA7F762395B8E746ED7589D03D625FF1E47F3DB885407B31A66A35A7EB161677ACC200BE3340B4EFA76CF9E36BD65D5787EAA25F756A97DF3B576E1BD54B73ECA7D73ED6AC87E4974629F973433CD611F0E1D5A7EB90B5795BEC0
	6F4F748B951463E8EA1A94DB4BFB357DD20263B4925A184E5A19249BFEC76AA17C3CC17573995D1FF468DCD33A530A7267685B856AC7CECF5F89254CE673345E335F8FE17AF2202DE774AD2AFBB41D7879558D758223195CBA7C9ED5C4ABFD17944C685EEB5576E7B20E0F88200B19CD7BF21FB0D2DFAE154DA16A3ECC1CCD76A7F4BC2C64F4FACFF700098E4D4CD78B193EBF691536BB499D65B89813F114E657EAE53FBF0FF393088D41DE6FAFE6F24A3BE04346EB1CFADE1F289F6E67E9FA6AE7BB53A9C1BFBC
	06BE56539872E1F40B5315E3FA8DDE5B85C0FD902379F53AF66C5E0FE9A301EEBA237D4A60B936DA499A91E83957A8FD66B925FFB71D43E79CD0DF7B9A3B836A754E91EAA75B0B8F3BDFD37BF047F96D98BC0267FB816847B17DDCC7D54F843C868D057A08576833EFAB5ECF47829C4FF0B7127D446B305FFF1D627EC86E34815C72BA35ED375778986939BF8E5A791213BFA3A3F19F7B81BE1049BDC55634G4FE2380FB1EE9E408B986EB479CD10B722534A5F2038CCCC578EF0779BD46746917B435BF3BD708D
	72B73EA41D8FE39941008FFB03663FAE45E7FB248FDE48C609279E73B04ACE5AAC264252028A120CC1787135C2117F5B92EFB9D765D54047F26A0F486EF72CD9B6FA3EE9CBA53E8F3E9B3B3E317827B9449C1465D2DB870F0DDE07F5ABA53EC97DDE722A360BECA25F8E59FD90D7656879EE115D4778EC5477C888EF649A495EFF135D2DF839F7747213E477A6BE299ABD5F5675925F13FD23673BCCF65FE27D3131599B3CD13277F71B24F64A46207796D95E762BEDE30F317DDBA47E7FBE0F822E10DEE0A8C0D9
	76F505044EDCA4515556F94B8F2FD6686737D562CBBCBBFA3E8FE5F9658B770F4D2F2BCBA43F9AE4B9641F360EDEFE17AC07FC5B91B4F194220B9B85686686754984D92E781B5EEC2B29D034FD1FAC077CFBEF3ED471E54AF248BFCF9D1BCFCAE57962BF6A24F6BE4A9D3D7CA1D91E7920571C55DAB1C8896D6FF5B4CFDC1CC37508D77E97BB8E237FE9BA20332CF95F7ED6D1E610ACC73D4CEB7EEE2DE25E5BA64B7FBEB76ADCE6715952BABC1F7C50F6293DADE3502337CE626B98035DAF4A724C73C7FD2F0270
	4E5FA171CE574BED917714154A3C7DB532FCF2D7BA198344EBBD85479CC6BB0D773A5D925DDA0337B92AD291B7BF6D11785E999ABD5FE38D925F32B15EE5FB47A0457BC1D96E791F0F0CDE7E99D96E79FF9FC50B00422616827166D21DF4F7A620D122EB73FACFC527E87F51A6096FA6EF3958AA3E154D925F06B1464659E312DFEEEB135AA99F035E4F7CA0717DC17884478BBCAA8A480D2CBAF68FA161C7092E466B1DA23D227D956D925FAB5EF241D5FC561FA53E2DE30C179B0ECBFE89BFA13533EED4FA133F
	CFFB246C32D5308FF81FCB953825620D4E0206EE23554ADB1C8EEEA9EB218927554477696B125F3EB3B909CBE96252C81AD1931702F2051A38D41CCA5444A58BB63B8B7DF947A4F9D1345600FF8444F6AA3D7F6FAB9687A5F1C9500800AED1F3A9D67437A339954D08F61B45E86D5AC672233E42CDF2F601DBA9DE87ACA077818B5005C2E8FD45F0EB77DC775A6C506306DCACA02D1BA77A25762354A45C9CDAB15E6BBC431D435BEB84DD8FADADD1787F5949D5FF1E7956D1E7B21756442DC5DB3C433C83E7EF71
	A6740B6B621F255C4FE55AEDE62884BCD863449F29CEE14082D665F69EA2BF0F411B76503CB027402DA2D6223467ED0DC6E737005C7F15688424D58A56F2719E5C3BF8DB4201FEAB31FE082B0CF12DFCE269564DDFA7C4A4F3C97B39AC0B4DE85AEF6C90EC0EA6587AF443606F0605144B3E8BCF07DCC6BF4DC0CD066656B3F311F1E5687805A9F95A759B3FA5F3176C07AEF4B938ED18926DA24B98058922458122F8C713007EG284DD1446AAE5BA8FC7A4783EB5A133845CA2384AE2D5358E3368C4097830EEE
	5C89CB855056E134BA519FA7824D8AD41A51AAA6206C469965B75D73510FD80044AAF01B08E701F5DB97EF6B5936273353EC65D98390CC507C8656BC22A9D3512CF05D306A6BD7961D04FEB7F049BD6565787F8A7C3F8E7DDF01B19598D3218367A6F2DE7F296BDD54E69CACECFC2018AF5B8FBDA83C7E7E0CBB5E28CF1C87566D67D2719F7901CED8A6D111596B4A501BB6E48744DF037A480EC245055ADDC91AC75B53656254006E3A25AA227DAA8621BD481040250BF300920F212CE1110830CB060794C2AF07
	0EA5539602F2B169EA7C7F5AB13B89F12D473C9B18F51F39B4B4C43622775AC813C7F1F0D31A359CF7FE1140A56B4B4B65B82E655233CCA7759B7DB917AF957D0674EED6DB1602EEFCAAC876DCFA266D980DE9BB24GBD3FE851A2C538EE3EA01BB2599D2F0BE3568117061274E79968F1DD7205F50761A2F647F7D2F72002967ACB545D01E38B5D54D474515ECBAF36FE98CDEC6D7492DBBE17E4ECF5E81B45E9282EEDD64DA5E9D64FF03356F36A07530529FED107CC2F24B79A54C8B652AF03E113E45894017B
	C4791790845842F2CB4465F5813AEDD3A7F0B932DF812AC35572BF00A0F09B507236G96A4859D623257C4163D450686C309402576B8790EB2CDD7AD76602A94252B68B2A40D245C9313914F4571210FB4DD22FD3BD9833AC0C390B49E7DF75545517CFAE554228840C7B6DE6CB92E81DDD27366ADCA1B8AB892435C08D6DC42B65DA95914AB33A9ED94FADFA211B65585F30772D0DB217CD18D68126A02F0298B3244AE40BFF22B54DB60322493EAC555818D6812B270695198283462B18D681232E906CBC3C3E4
	61D251C27C5C3B7474FEF178D6973D52DB0C461062D0D8E15E4F9A50A5E5569B2389311F85AEDBF6422B28EB5400AEA98B7FACD918A933D07970E904A5127C2CD6D1F9D8833A24CC7C0BE4E23A4C44F4255E9C7D0D411273725F78DBB0BE4F636FAB66FDC06E9515FEA07D469F39177FDFB82F319B70BE9F50BB62BB2B503641537D7C43BDC60BC5B0DACE6F311A50DD7D3D8382AF7D464607D528FDA72591ABC95DC7786F781D12717C9FD0CB87882D3A970B69A9GGD40DGGD0CB818294G94G88G88GC8
	F954AC2D3A970B69A9GGD40DGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA3AAGGGG
**end of data**/
}
}
