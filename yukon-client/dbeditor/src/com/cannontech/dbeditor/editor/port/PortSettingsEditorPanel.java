package com.cannontech.dbeditor.editor.port;

/**
 * This type was created in VisualAge.
 */

 import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.LocalDirectPort;
import com.cannontech.database.data.port.PooledPort;
import com.cannontech.database.data.port.TcpPort;
import com.cannontech.database.data.port.TerminalServerDirectPort;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.database.db.port.PortSettings;
import com.cannontech.database.db.port.PortTerminalServer;
import com.cannontech.database.db.port.PortTerminalServer.EncodingType;
import com.cannontech.util.Validator;
 
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
	private javax.swing.JCheckBox encodingCheckBox = null;
	private javax.swing.JTextField encodingTextField = null;
	private javax.swing.JLabel encodingLabel = null;
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
    if (e.getSource() == getEncodingCheckBox()) 
        encryptionClicked(e);
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
    if (e.getSource() == getEncodingTextField()) 
        connEtoC7(e);//reused connEtoC7 on purpose
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

private void encryptionClicked(java.awt.event.ActionEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.fireInputUpdate();
        // user code begin {2}

        if( getEncodingCheckBox().isSelected() )
        {
            getEncryptionLabel().setEnabled(true);
            getEncodingTextField().setEnabled(true);
        }
        else
        {
            getEncryptionLabel().setEnabled(false);
            getEncodingTextField().setEnabled(false);
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
			ivjBaudRateComboBox.setSelectedItem("300");
			ivjBaudRateComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBaudRateComboBox.setMinimumSize(new java.awt.Dimension(76, 25));
			ivjBaudRateComboBox.setPreferredSize(new java.awt.Dimension(80, 25));
	
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_300 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_1200 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_2400 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_4800 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_9600 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_14400 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_38400 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_57600 );
			ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_115200 );
		} catch (java.lang.Throwable ivjExc) {
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
			ivjBaudRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjCarrierDetectWaitLabel.setVisible(true);
			ivjCarrierDetectWaitLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjCarrierDetectWaitLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCarrierDetectWaitLabel.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjCarrierDetectWaitTextField.setColumns(3);
			ivjCarrierDetectWaitTextField.setPreferredSize(new java.awt.Dimension(33, 20));
			ivjCarrierDetectWaitTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjCarrierDetectWaitTextField.setEnabled(false);
			ivjCarrierDetectWaitTextField.setMinimumSize(new java.awt.Dimension(33, 20));
			ivjCarrierDetectWaitTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
			ivjCarrierDetectWaitTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 9999) );
		} catch (java.lang.Throwable ivjExc) {
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
			ivjCommonProtocolComboBox.setSelectedItem("IDLC");
			ivjCommonProtocolComboBox.setPreferredSize(new java.awt.Dimension(65, 25));
			ivjCommonProtocolComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCommonProtocolComboBox.setMinimumSize(new java.awt.Dimension(61, 25));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjCommonProtocolLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
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
			constraintsPhysicalPortLabel.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getPhysicalPortLabel(), constraintsPhysicalPortLabel);

			java.awt.GridBagConstraints constraintsBaudRateLabel = new java.awt.GridBagConstraints();
			constraintsBaudRateLabel.gridx = 1; constraintsBaudRateLabel.gridy = 4;
			constraintsBaudRateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBaudRateLabel.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getBaudRateLabel(), constraintsBaudRateLabel);

			java.awt.GridBagConstraints constraintsPhysicalPortComboBox = new java.awt.GridBagConstraints();
			constraintsPhysicalPortComboBox.gridx = 2; constraintsPhysicalPortComboBox.gridy = 3;
			constraintsPhysicalPortComboBox.gridwidth = 2;
			constraintsPhysicalPortComboBox.weightx = 1.0;
			constraintsPhysicalPortComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPhysicalPortComboBox.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getPhysicalPortComboBox(), constraintsPhysicalPortComboBox);

			java.awt.GridBagConstraints constraintsBaudRateComboBox = new java.awt.GridBagConstraints();
			constraintsBaudRateComboBox.gridx = 2; constraintsBaudRateComboBox.gridy = 4;
			constraintsBaudRateComboBox.gridwidth = 2;
			constraintsBaudRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBaudRateComboBox.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getBaudRateComboBox(), constraintsBaudRateComboBox);

			java.awt.GridBagConstraints constraintsCarrierDetectWaitLabel = new java.awt.GridBagConstraints();
			constraintsCarrierDetectWaitLabel.gridx = 1; constraintsCarrierDetectWaitLabel.gridy = 7;
			constraintsCarrierDetectWaitLabel.gridwidth = 1;
			constraintsCarrierDetectWaitLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCarrierDetectWaitLabel.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getCarrierDetectWaitLabel(), constraintsCarrierDetectWaitLabel);

			java.awt.GridBagConstraints constraintsCarrierDetectWaitTextField = new java.awt.GridBagConstraints();
			constraintsCarrierDetectWaitTextField.gridx = 2; constraintsCarrierDetectWaitTextField.gridy = 7;
			constraintsCarrierDetectWaitTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCarrierDetectWaitTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCarrierDetectWaitTextField.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getCarrierDetectWaitTextField(), constraintsCarrierDetectWaitTextField);

			java.awt.GridBagConstraints constraintsMsecsLabel = new java.awt.GridBagConstraints();
			constraintsMsecsLabel.gridx = 3; constraintsMsecsLabel.gridy = 7;
			constraintsMsecsLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMsecsLabel.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getMsecsLabel(), constraintsMsecsLabel);

			java.awt.GridBagConstraints constraintsCarrierDetectCheckBox = new java.awt.GridBagConstraints();
			constraintsCarrierDetectCheckBox.gridx = 1; constraintsCarrierDetectCheckBox.gridy = 6;
			constraintsCarrierDetectCheckBox.gridwidth = 3;
			constraintsCarrierDetectCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCarrierDetectCheckBox.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getCarrierDetectCheckBox(), constraintsCarrierDetectCheckBox);

	        java.awt.GridBagConstraints constraintsEncryptionCheckBox = new java.awt.GridBagConstraints();
	        constraintsEncryptionCheckBox.gridx = 1; constraintsEncryptionCheckBox.gridy = 8;
	        constraintsEncryptionCheckBox.gridwidth = 3;
	        constraintsEncryptionCheckBox.anchor = java.awt.GridBagConstraints.WEST;
	        constraintsEncryptionCheckBox.insets = new java.awt.Insets(2,5,2,2);
	        ivjConfigurationPanel.add(getEncodingCheckBox(), constraintsEncryptionCheckBox);
			
            java.awt.GridBagConstraints constraintsEncryptionTextField = new java.awt.GridBagConstraints();
            constraintsEncryptionTextField.gridx = 2; constraintsEncryptionTextField.gridy = 9;
            constraintsEncryptionTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsEncryptionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsEncryptionTextField.gridwidth = 2;
            constraintsEncryptionTextField.insets = new java.awt.Insets(2,5,2,2);
            ivjConfigurationPanel.add(getEncodingTextField(), constraintsEncryptionTextField);
            
            java.awt.GridBagConstraints constraintsEncryptionLabel = new java.awt.GridBagConstraints();
            constraintsEncryptionLabel.gridx = 1; constraintsEncryptionLabel.gridy = 9;
            constraintsEncryptionLabel.gridwidth = 1;
            constraintsEncryptionLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsEncryptionLabel.insets = new java.awt.Insets(2,5,2,2);
            ivjConfigurationPanel.add(getEncryptionLabel(), constraintsEncryptionLabel);
            
			java.awt.GridBagConstraints constraintsCommonProtocolComboBox = new java.awt.GridBagConstraints();
			constraintsCommonProtocolComboBox.gridx = 2; constraintsCommonProtocolComboBox.gridy = 5;
			constraintsCommonProtocolComboBox.gridwidth = 2;
			constraintsCommonProtocolComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommonProtocolComboBox.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getCommonProtocolComboBox(), constraintsCommonProtocolComboBox);

			java.awt.GridBagConstraints constraintsCommonProtocolLabel = new java.awt.GridBagConstraints();
			constraintsCommonProtocolLabel.gridx = 1; constraintsCommonProtocolLabel.gridy = 5;
			constraintsCommonProtocolLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommonProtocolLabel.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getCommonProtocolLabel(), constraintsCommonProtocolLabel);

			java.awt.GridBagConstraints constraintsIPAddressLabel = new java.awt.GridBagConstraints();
			constraintsIPAddressLabel.gridx = 1; constraintsIPAddressLabel.gridy = 1;
			constraintsIPAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsIPAddressLabel.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getIPAddressLabel(), constraintsIPAddressLabel);

			java.awt.GridBagConstraints constraintsIPAddressTextField = new java.awt.GridBagConstraints();
			constraintsIPAddressTextField.gridx = 2; constraintsIPAddressTextField.gridy = 1;
			constraintsIPAddressTextField.gridwidth = 2;
			constraintsIPAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsIPAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsIPAddressTextField.weightx = 1.0;
			constraintsIPAddressTextField.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getIPAddressTextField(), constraintsIPAddressTextField);

			java.awt.GridBagConstraints constraintsPortNumberTextField = new java.awt.GridBagConstraints();
			constraintsPortNumberTextField.gridx = 2; constraintsPortNumberTextField.gridy = 2;
			constraintsPortNumberTextField.gridwidth = 2;
			constraintsPortNumberTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPortNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPortNumberTextField.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getPortNumberTextField(), constraintsPortNumberTextField);

			java.awt.GridBagConstraints constraintsPortNumberLabel = new java.awt.GridBagConstraints();
			constraintsPortNumberLabel.gridx = 1; constraintsPortNumberLabel.gridy = 2;
			constraintsPortNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPortNumberLabel.insets = new java.awt.Insets(2,5,2,2);
			ivjConfigurationPanel.add(getPortNumberLabel(), constraintsPortNumberLabel);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjDescriptionLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjDisableCheckBox.setActionCommand("Disable");
			ivjDisableCheckBox.setBorderPainted(false);
			ivjDisableCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDisableCheckBox.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjDisableCheckBox.setHorizontalAlignment(2);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjIPAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjIPAddressTextField.setColumns(12);
			ivjIPAddressTextField.setPreferredSize(new java.awt.Dimension(132, 20));
			ivjIPAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjIPAddressTextField.setMinimumSize(new java.awt.Dimension(132, 20));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjPhysicalPortComboBox.setSelectedItem("com1");
			ivjPhysicalPortComboBox.setPreferredSize(new java.awt.Dimension(66, 25));
			ivjPhysicalPortComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhysicalPortComboBox.setMinimumSize(new java.awt.Dimension(62, 25));
			ivjPhysicalPortComboBox.setRequestFocusEnabled(true);
			ivjPhysicalPortComboBox.setToolTipText("Click inside the box to type if channel is not in list.");
			ivjPhysicalPortComboBox.setEditable(true);
		} catch (java.lang.Throwable ivjExc) {
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
			ivjPhysicalPortLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjPortDescriptionTextField.setColumns(13);
			ivjPortDescriptionTextField.setPreferredSize(new java.awt.Dimension(155, 20));
			ivjPortDescriptionTextField.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPortDescriptionTextField.setMinimumSize(new java.awt.Dimension(135, 20));
			ivjPortDescriptionTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_PORT_DESCRIPTION_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjPortNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjPortNumberTextField.setColumns(5);
			ivjPortNumberTextField.setPreferredSize(new java.awt.Dimension(55, 20));
			ivjPortNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPortNumberTextField.setMinimumSize(new java.awt.Dimension(55, 20));
			ivjPortNumberTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 999999 ) );
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPortNumberTextField;
}

private javax.swing.JTextField getEncodingTextField() {
    if (encodingTextField == null) {
        try {
            encodingTextField = new javax.swing.JTextField();
            encodingTextField.setName("PortNumberTextField");
            encodingTextField.setFont(new java.awt.Font("sansserif", 0, 12));
            encodingTextField.setPreferredSize(new java.awt.Dimension(150, 20));
            encodingTextField.setMinimumSize(new java.awt.Dimension(150, 20));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return encodingTextField;
}

private javax.swing.JCheckBox getEncodingCheckBox() {
    if (encodingCheckBox == null) {
        try {
            encodingCheckBox = new javax.swing.JCheckBox();
            encodingCheckBox.setName("EncryptionCheckBox");
            encodingCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
            encodingCheckBox.setText("Enable Encryption");
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return encodingCheckBox;
}

private javax.swing.JLabel getEncryptionLabel() {
    if (encodingLabel == null) {
        try {
            encodingLabel = new javax.swing.JLabel();
            encodingLabel.setName("EncryptionLabel");
            encodingLabel.setText("Key (Hex)");
            encodingLabel.setVisible(true);
            encodingLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
            encodingLabel.setFont(new java.awt.Font("dialog", 0, 14));
            encodingLabel.setEnabled(true);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return encodingLabel;
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
			ivjTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjTypeLabelField.setFont(new java.awt.Font("dialog.bold", 1, 14));
		} catch (java.lang.Throwable ivjExc) {
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
		String encryptionKey = getEncodingTextField().getText();
		boolean encryptionEnabled = getEncodingCheckBox().isSelected();
		
		PortTerminalServer pts = ((TerminalServerDirectPort)dp).getPortTerminalServer();
		pts.setIpAddress(ipAddress);
		pts.setSocketPortNumber(portNumber);
		if(encryptionEnabled) {
		    pts.setEncodingType(EncodingType.AES);
		} else {
		    pts.setEncodingType(EncodingType.NONE);
		}
		pts.setEncodingKey(encryptionKey);
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
	getEncodingCheckBox().addActionListener(this);
	getEncodingTextField().addCaretListener(this);
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
	getPhysicalPortComboBox().addItem("com50");
	getPhysicalPortComboBox().addItem("com99");
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
		SwingUtil.setSelectedInComboBox(getPhysicalPortComboBox(),
			((LocalDirectPort) dp).getPortLocalSerial().getPhysicalPort());
		SwingUtil.setSelectedInComboBox(getCommonProtocolComboBox(), cp.getCommonProtocol());
	}
	else if (val instanceof TerminalServerDirectPort)
	{
        PortTerminalServer pts = ((TerminalServerDirectPort) dp).getPortTerminalServer();
	    
        boolean udpPort = pts.getIpAddress().equalsIgnoreCase("UDP");
		
        getCarrierDetectWaitTextField().setText("0");
		getIPAddressLabel().setVisible(true);
		getIPAddressTextField().setVisible(true);
		getPortNumberLabel().setVisible(true);
		getPortNumberTextField().setVisible(true);
		getPhysicalPortLabel().setVisible(false);
		getPhysicalPortComboBox().setVisible(false);

		getCommonProtocolLabel().setVisible(true);
		getCommonProtocolComboBox().setVisible(true);
		SwingUtil.setSelectedInComboBox(getCommonProtocolComboBox(), cp.getCommonProtocol());

		getIPAddressTextField().setText(pts.getIpAddress());
		getPortNumberTextField().setText(pts.getSocketPortNumber().toString());
		
		getEncodingCheckBox().setSelected(pts.getEncodingType() != EncodingType.NONE);
		getEncodingTextField().setText(pts.getEncodingKey());
		getEncodingTextField().setEnabled(pts.getEncodingType() != EncodingType.NONE && udpPort);
		getEncryptionLabel().setEnabled(pts.getEncodingType() != EncodingType.NONE && udpPort);
		
		getEncodingCheckBox().setEnabled(udpPort);
		getIPAddressTextField().setEnabled(!udpPort);
		
	}
	else if( val instanceof PooledPort )
	{
		getConfigurationPanel().setVisible( false );
	}
	else if (val instanceof TcpPort)
	{
        TcpPort pts = (TcpPort) dp;
       
        getCarrierDetectWaitTextField().setText("0");
        getCarrierDetectWaitTextField().setVisible(false);
        getIPAddressLabel().setVisible(false);
        getIPAddressTextField().setVisible(false);
        getPortNumberLabel().setVisible(false);
        getPortNumberTextField().setVisible(false);
        getPhysicalPortLabel().setVisible(false);
        getPhysicalPortComboBox().setVisible(false);

        getCommonProtocolLabel().setVisible(false);
        getCommonProtocolComboBox().setVisible(false);
        getCarrierDetectCheckBox().setVisible(false);
        getEncodingCheckBox().setVisible(false);
        getEncodingTextField().setVisible(false);
        getCarrierDetectWaitLabel().setVisible(false);
        getMsecsLabel().setVisible(false);
        getEncryptionLabel().setVisible(false);
	}

	getTypeLabelField().setText(portType);
	getPortDescriptionTextField().setText( name );
	SwingUtil.setCheckBoxState(getDisableCheckBox(), disableFlag);
	SwingUtil.setSelectedInComboBox(getBaudRateComboBox(), baudRate.toString());

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

public boolean isInputValid() {
    
    boolean ret = true;
    boolean udpPort = getIPAddressTextField().getText().equalsIgnoreCase("UDP");
    boolean encodingEnabled = getEncodingCheckBox().isSelected();
    
    if (udpPort && encodingEnabled) {
	    if (!Validator.isHex(getEncodingTextField().getText())) {
	        ret = false;
	        setErrorString("Encryption key must be in Hex format and 16 bytes long. (32 hex values)");
	    }
	    
	    if (getEncodingTextField().getText().length() != 32) {
	        ret = false;
	        setErrorString("Encryption key must be 16 bytes long. (32 hex values)");
	    }
    }
    return ret;
}

}
