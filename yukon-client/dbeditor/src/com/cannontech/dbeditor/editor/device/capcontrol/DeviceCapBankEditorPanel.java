package com.cannontech.dbeditor.editor.device.capcontrol;

import com.cannontech.database.db.*;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.dbeditor.defines.CommonDefines;
/**
 * This type was created in VisualAge.
 */
public class DeviceCapBankEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjBankAddressLabel = null;
	private javax.swing.JLabel ivjBankSizeLabel = null;
	private javax.swing.JPanel ivjConfigurationPanel = null;
	private javax.swing.JCheckBox ivjControlInhibitCheckBox = null;
	private javax.swing.JComboBox ivjControlPointComboBox = null;
	private javax.swing.JLabel ivjControlPointLabel = null;
	private javax.swing.JCheckBox ivjDisableFlagCheckBox = null;
	private javax.swing.JPanel ivjIdentificationPanel = null;
	private javax.swing.JLabel ivjTypeLabel = null;
	private javax.swing.JLabel ivjTypeTextField = null;
	private javax.swing.JComboBox ivjControlDeviceComboBox = null;
	private javax.swing.JLabel ivjControlDeviceLabel = null;
	private java.util.List points = null;
	private javax.swing.JLabel ivjJLabelKVAR = null;
	private javax.swing.JComboBox ivjJComboBankSize = null;
	private javax.swing.JComboBox ivjJComboBoxBankOperation = null;
	private javax.swing.JLabel ivjJLabelBankName = null;
	private javax.swing.JLabel ivjJLabelBankOperation = null;
	private javax.swing.JTextField ivjJTextFieldAddress = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
   
   private class NewComboBoxEditor extends javax.swing.plaf.basic.BasicComboBoxEditor
   {
      public javax.swing.JTextField getJTextField()
      {
         //create this method so we don't have to cast the getEditorComponent() call
         return editor;
      }
      
   };         
   
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceCapBankEditorPanel() {
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
	if (e.getSource() == getControlPointComboBox()) 
		connEtoC6(e);
	if (e.getSource() == getControlDeviceComboBox()) 
		connEtoC8(e);
	if (e.getSource() == getJComboBoxBankOperation()) 
		connEtoC9(e);
	if (e.getSource() == getJComboBankSize()) 
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
	if (e.getSource() == getJTextFieldName()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldAddress()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
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
 * connEtoC5:  (JComboBankSize.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceCapBankEditorPanel.fireInputUpdate()V)
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
 * connEtoC6:  (ControlPointComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceCapBankControllerEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
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
 * connEtoC8:  (ControlDeviceComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceCapBankEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if( getControlPointComboBox().getModel().getSize() > 0 )
			getControlPointComboBox().removeAllItems();

		int deviceID = ((com.cannontech.database.data.lite.LiteYukonPAObject)getControlDeviceComboBox().getSelectedItem()).getYukonID();
		com.cannontech.database.data.lite.LitePoint litePoint = null;
		for(int i=0;i<points.size();i++)
		{
			litePoint = (com.cannontech.database.data.lite.LitePoint)points.get(i);
			if( litePoint.getPaobjectID() == deviceID &&
					litePoint.getPointType() == com.cannontech.database.data.point.PointTypes.STATUS_POINT)
			{
				getControlPointComboBox().addItem(litePoint);
			}
		}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (OperationalStateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceCapBankEditorPanel.operationalStateComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.operationalStateComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the BankAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBankAddressLabel() {
	if (ivjBankAddressLabel == null) {
		try {
			ivjBankAddressLabel = new javax.swing.JLabel();
			ivjBankAddressLabel.setName("BankAddressLabel");
			ivjBankAddressLabel.setText("Street Location:");
			ivjBankAddressLabel.setMaximumSize(new java.awt.Dimension(112, 16));
			ivjBankAddressLabel.setPreferredSize(new java.awt.Dimension(112, 16));
			ivjBankAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBankAddressLabel.setMinimumSize(new java.awt.Dimension(112, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBankAddressLabel;
}
/**
 * Return the BankSizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBankSizeLabel() {
	if (ivjBankSizeLabel == null) {
		try {
			ivjBankSizeLabel = new javax.swing.JLabel();
			ivjBankSizeLabel.setName("BankSizeLabel");
			ivjBankSizeLabel.setText("Bank Size:");
			ivjBankSizeLabel.setMaximumSize(new java.awt.Dimension(138, 16));
			ivjBankSizeLabel.setPreferredSize(new java.awt.Dimension(138, 16));
			ivjBankSizeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBankSizeLabel.setMinimumSize(new java.awt.Dimension(138, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBankSizeLabel;
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
			constraintsControlInhibitCheckBox.gridx = 1; constraintsControlInhibitCheckBox.gridy = 1;
			constraintsControlInhibitCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlInhibitCheckBox.insets = new java.awt.Insets(3, 13, 3, 16);
			getConfigurationPanel().add(getControlInhibitCheckBox(), constraintsControlInhibitCheckBox);

			java.awt.GridBagConstraints constraintsJComboBoxBankOperation = new java.awt.GridBagConstraints();
			constraintsJComboBoxBankOperation.gridx = 2; constraintsJComboBoxBankOperation.gridy = 2;
			constraintsJComboBoxBankOperation.gridwidth = 2;
			constraintsJComboBoxBankOperation.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxBankOperation.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxBankOperation.weightx = 1.0;
			constraintsJComboBoxBankOperation.ipadx = 49;
			constraintsJComboBoxBankOperation.insets = new java.awt.Insets(3, 3, 3, 77);
			getConfigurationPanel().add(getJComboBoxBankOperation(), constraintsJComboBoxBankOperation);

			java.awt.GridBagConstraints constraintsJLabelBankOperation = new java.awt.GridBagConstraints();
			constraintsJLabelBankOperation.gridx = 1; constraintsJLabelBankOperation.gridy = 2;
			constraintsJLabelBankOperation.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelBankOperation.insets = new java.awt.Insets(5, 13, 7, 37);
			getConfigurationPanel().add(getJLabelBankOperation(), constraintsJLabelBankOperation);

			java.awt.GridBagConstraints constraintsControlPointLabel = new java.awt.GridBagConstraints();
			constraintsControlPointLabel.gridx = 1; constraintsControlPointLabel.gridy = 4;
			constraintsControlPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlPointLabel.insets = new java.awt.Insets(8, 13, 8, 2);
			getConfigurationPanel().add(getControlPointLabel(), constraintsControlPointLabel);

			java.awt.GridBagConstraints constraintsControlPointComboBox = new java.awt.GridBagConstraints();
			constraintsControlPointComboBox.gridx = 2; constraintsControlPointComboBox.gridy = 4;
			constraintsControlPointComboBox.gridwidth = 2;
			constraintsControlPointComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsControlPointComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlPointComboBox.weightx = 1.0;
			constraintsControlPointComboBox.ipadx = 49;
			constraintsControlPointComboBox.insets = new java.awt.Insets(4, 3, 3, 77);
			getConfigurationPanel().add(getControlPointComboBox(), constraintsControlPointComboBox);

			java.awt.GridBagConstraints constraintsBankSizeLabel = new java.awt.GridBagConstraints();
			constraintsBankSizeLabel.gridx = 1; constraintsBankSizeLabel.gridy = 5;
			constraintsBankSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBankSizeLabel.insets = new java.awt.Insets(5, 13, 24, 2);
			getConfigurationPanel().add(getBankSizeLabel(), constraintsBankSizeLabel);

			java.awt.GridBagConstraints constraintsJComboBankSize = new java.awt.GridBagConstraints();
			constraintsJComboBankSize.gridx = 2; constraintsJComboBankSize.gridy = 5;
			constraintsJComboBankSize.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBankSize.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBankSize.weightx = 1.0;
			constraintsJComboBankSize.ipadx = 126;
			constraintsJComboBankSize.insets = new java.awt.Insets(3, 3, 22, 2);
			getConfigurationPanel().add(getJComboBankSize(), constraintsJComboBankSize);

			java.awt.GridBagConstraints constraintsControlDeviceLabel = new java.awt.GridBagConstraints();
			constraintsControlDeviceLabel.gridx = 1; constraintsControlDeviceLabel.gridy = 3;
			constraintsControlDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlDeviceLabel.ipadx = 41;
			constraintsControlDeviceLabel.insets = new java.awt.Insets(6, 13, 6, 2);
			getConfigurationPanel().add(getControlDeviceLabel(), constraintsControlDeviceLabel);

			java.awt.GridBagConstraints constraintsControlDeviceComboBox = new java.awt.GridBagConstraints();
			constraintsControlDeviceComboBox.gridx = 2; constraintsControlDeviceComboBox.gridy = 3;
			constraintsControlDeviceComboBox.gridwidth = 2;
			constraintsControlDeviceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsControlDeviceComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlDeviceComboBox.weightx = 1.0;
			constraintsControlDeviceComboBox.ipadx = 73;
			constraintsControlDeviceComboBox.insets = new java.awt.Insets(4, 3, 4, 77);
			getConfigurationPanel().add(getControlDeviceComboBox(), constraintsControlDeviceComboBox);

			java.awt.GridBagConstraints constraintsJLabelKVAR = new java.awt.GridBagConstraints();
			constraintsJLabelKVAR.gridx = 3; constraintsJLabelKVAR.gridy = 5;
			constraintsJLabelKVAR.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelKVAR.ipadx = 8;
			constraintsJLabelKVAR.ipady = -3;
			constraintsJLabelKVAR.insets = new java.awt.Insets(6, 2, 23, 68);
			getConfigurationPanel().add(getJLabelKVAR(), constraintsJLabelKVAR);
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
 * Return the ControlDeviceComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getControlDeviceComboBox() {
	if (ivjControlDeviceComboBox == null) {
		try {
			ivjControlDeviceComboBox = new javax.swing.JComboBox();
			ivjControlDeviceComboBox.setName("ControlDeviceComboBox");
			ivjControlDeviceComboBox.setFont(new java.awt.Font("dialog", 0, 12));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlDeviceComboBox;
}
/**
 * Return the ControlDeviceLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getControlDeviceLabel() {
	if (ivjControlDeviceLabel == null) {
		try {
			ivjControlDeviceLabel = new javax.swing.JLabel();
			ivjControlDeviceLabel.setName("ControlDeviceLabel");
			ivjControlDeviceLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlDeviceLabel.setText("Control Device:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlDeviceLabel;
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
			ivjControlInhibitCheckBox.setMnemonic('d');
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
 * Return the ControlPointComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getControlPointComboBox() {
	if (ivjControlPointComboBox == null) {
		try {
			ivjControlPointComboBox = new javax.swing.JComboBox();
			ivjControlPointComboBox.setName("ControlPointComboBox");
			ivjControlPointComboBox.setMaximumSize(new java.awt.Dimension(32767, 25));
			ivjControlPointComboBox.setPreferredSize(new java.awt.Dimension(150, 25));
			ivjControlPointComboBox.setFont(new java.awt.Font("dialog", 0, 12));
			ivjControlPointComboBox.setMinimumSize(new java.awt.Dimension(150, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlPointComboBox;
}
/**
 * Return the ControlPointLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getControlPointLabel() {
	if (ivjControlPointLabel == null) {
		try {
			ivjControlPointLabel = new javax.swing.JLabel();
			ivjControlPointLabel.setName("ControlPointLabel");
			ivjControlPointLabel.setText("Control Point:");
			ivjControlPointLabel.setMaximumSize(new java.awt.Dimension(138, 16));
			ivjControlPointLabel.setPreferredSize(new java.awt.Dimension(138, 16));
			ivjControlPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlPointLabel.setMinimumSize(new java.awt.Dimension(138, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlPointLabel;
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
			ivjDisableFlagCheckBox.setMnemonic('b');
			ivjDisableFlagCheckBox.setText("Disable Bank");
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
			constraintsTypeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTypeTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTypeTextField.ipadx = 251;
			constraintsTypeTextField.ipady = 20;
			constraintsTypeTextField.insets = new java.awt.Insets(22, 5, 2, 19);
			getIdentificationPanel().add(getTypeTextField(), constraintsTypeTextField);

			java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
			constraintsTypeLabel.gridx = 1; constraintsTypeLabel.gridy = 1;
			constraintsTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTypeLabel.insets = new java.awt.Insets(24, 8, 4, 34);
			getIdentificationPanel().add(getTypeLabel(), constraintsTypeLabel);

			java.awt.GridBagConstraints constraintsJLabelBankName = new java.awt.GridBagConstraints();
			constraintsJLabelBankName.gridx = 1; constraintsJLabelBankName.gridy = 2;
			constraintsJLabelBankName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelBankName.insets = new java.awt.Insets(4, 8, 5, 30);
			getIdentificationPanel().add(getJLabelBankName(), constraintsJLabelBankName);

			java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
			constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 2;
			constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldName.weightx = 1.0;
			constraintsJTextFieldName.ipadx = 119;
			constraintsJTextFieldName.insets = new java.awt.Insets(2, 5, 3, 19);
			getIdentificationPanel().add(getJTextFieldName(), constraintsJTextFieldName);

			java.awt.GridBagConstraints constraintsBankAddressLabel = new java.awt.GridBagConstraints();
			constraintsBankAddressLabel.gridx = 1; constraintsBankAddressLabel.gridy = 3;
			constraintsBankAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBankAddressLabel.insets = new java.awt.Insets(6, 8, 28, 5);
			getIdentificationPanel().add(getBankAddressLabel(), constraintsBankAddressLabel);

			java.awt.GridBagConstraints constraintsJTextFieldAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldAddress.gridx = 2; constraintsJTextFieldAddress.gridy = 3;
			constraintsJTextFieldAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldAddress.weightx = 1.0;
			constraintsJTextFieldAddress.ipadx = 218;
			constraintsJTextFieldAddress.insets = new java.awt.Insets(4, 5, 26, 19);
			getIdentificationPanel().add(getJTextFieldAddress(), constraintsJTextFieldAddress);
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
 * Return the JComboBankSize property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBankSize() {
	if (ivjJComboBankSize == null) {
		try {
			ivjJComboBankSize = new javax.swing.JComboBox();
			ivjJComboBankSize.setName("JComboBankSize");
			ivjJComboBankSize.setPreferredSize(new java.awt.Dimension(33, 20));
			ivjJComboBankSize.setFont(new java.awt.Font("sansserif", 0, 12));
			ivjJComboBankSize.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJComboBankSize.setMinimumSize(new java.awt.Dimension(33, 20));
			// user code begin {1}

         ivjJComboBankSize.addItem( new Integer(50) );
         ivjJComboBankSize.addItem( new Integer(100) );
         ivjJComboBankSize.addItem( new Integer(150) );
         ivjJComboBankSize.addItem( new Integer(275) );
         ivjJComboBankSize.addItem( new Integer(300) );
         ivjJComboBankSize.addItem( new Integer(450) );
         ivjJComboBankSize.addItem( new Integer(550) );         
         ivjJComboBankSize.addItem( new Integer(600) );
         ivjJComboBankSize.addItem( new Integer(825) );         
         ivjJComboBankSize.addItem( new Integer(900) );
         ivjJComboBankSize.addItem( new Integer(1100) );         
         ivjJComboBankSize.addItem( new Integer(1200) );
			
         
         ivjJComboBankSize.setEditable( true );
         
         NewComboBoxEditor ncb = new NewComboBoxEditor();
         ncb.getJTextField().setDocument( 
               new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 10000) );
         
         ivjJComboBankSize.setEditor( ncb );
         
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBankSize;
}
/**
 * Return the OperationStateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxBankOperation() {
	if (ivjJComboBoxBankOperation == null) {
		try {
			ivjJComboBoxBankOperation = new javax.swing.JComboBox();
			ivjJComboBoxBankOperation.setName("JComboBoxBankOperation");
			ivjJComboBoxBankOperation.setPreferredSize(new java.awt.Dimension(150, 25));
			ivjJComboBoxBankOperation.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJComboBoxBankOperation.setMinimumSize(new java.awt.Dimension(150, 25));
			// user code begin {1}

			ivjJComboBoxBankOperation.addItem( com.cannontech.database.data.capcontrol.CapBank.SWITCHED_OPSTATE );
			ivjJComboBoxBankOperation.addItem( com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxBankOperation;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelBankName() {
	if (ivjJLabelBankName == null) {
		try {
			ivjJLabelBankName = new javax.swing.JLabel();
			ivjJLabelBankName.setName("JLabelBankName");
			ivjJLabelBankName.setText("Bank Name:");
			ivjJLabelBankName.setMaximumSize(new java.awt.Dimension(87, 16));
			ivjJLabelBankName.setPreferredSize(new java.awt.Dimension(87, 16));
			ivjJLabelBankName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelBankName.setMinimumSize(new java.awt.Dimension(87, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelBankName;
}
/**
 * Return the OperationalStateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelBankOperation() {
	if (ivjJLabelBankOperation == null) {
		try {
			ivjJLabelBankOperation = new javax.swing.JLabel();
			ivjJLabelBankOperation.setName("JLabelBankOperation");
			ivjJLabelBankOperation.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelBankOperation.setText("Bank Operation:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelBankOperation;
}
/**
 * Return the JLabelKVAR property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKVAR() {
	if (ivjJLabelKVAR == null) {
		try {
			ivjJLabelKVAR = new javax.swing.JLabel();
			ivjJLabelKVAR.setName("JLabelKVAR");
			ivjJLabelKVAR.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelKVAR.setText("KVAR");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelKVAR;
}
/**
 * Return the BankAddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAddress() {
	if (ivjJTextFieldAddress == null) {
		try {
			ivjJTextFieldAddress = new javax.swing.JTextField();
			ivjJTextFieldAddress.setName("JTextFieldAddress");
			ivjJTextFieldAddress.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJTextFieldAddress.setColumns(15);
			ivjJTextFieldAddress.setPreferredSize(new java.awt.Dimension(33, 20));
			ivjJTextFieldAddress.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldAddress.setMinimumSize(new java.awt.Dimension(33, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAddress;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJTextFieldName.setColumns(15);
			ivjJTextFieldName.setPreferredSize(new java.awt.Dimension(132, 20));
			ivjJTextFieldName.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldName.setMinimumSize(new java.awt.Dimension(132, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
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
public Object getValue(Object val) 
{
	com.cannontech.database.data.capcontrol.CapBank capBank = (com.cannontech.database.data.capcontrol.CapBank)val;

	Integer controlPointID = (getControlPointComboBox().getSelectedItem() == null ? new Integer(com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM) :
			new Integer(((com.cannontech.database.data.lite.LitePoint)getControlPointComboBox().getSelectedItem()).getPointID()) );

	Integer controlDeviceID = (getControlDeviceComboBox().getSelectedItem() == null ? new Integer(com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID) : 
			new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getControlDeviceComboBox().getSelectedItem()).getYukonID()) );

	capBank.setPAOName( getJTextFieldName().getText() );
	capBank.setLocation( getJTextFieldAddress().getText() );
	capBank.getCapBank().setOperationalState( getJComboBoxBankOperation().getSelectedItem().toString() );

	if( getControlPointComboBox().isVisible() )
		capBank.getCapBank().setControlPointID( controlPointID );
	else
		capBank.getCapBank().setControlPointID( new Integer(com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM) );

	if( getControlDeviceComboBox().isVisible() )
		capBank.getCapBank().setControlDeviceID( controlDeviceID );
	else
		capBank.getCapBank().setControlDeviceID( new Integer(com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID) );
		
	if( getControlInhibitCheckBox().isSelected() )
		capBank.getDevice().setControlInhibit( com.cannontech.common.util.CtiUtilities.getTrueCharacter() );
	else
		capBank.getDevice().setControlInhibit( com.cannontech.common.util.CtiUtilities.getFalseCharacter() );
	if( getDisableFlagCheckBox().isSelected() )
		capBank.setDisableFlag( com.cannontech.common.util.CtiUtilities.getTrueCharacter() );
	else
		capBank.setDisableFlag( com.cannontech.common.util.CtiUtilities.getFalseCharacter() );

   capBank.getCapBank().setBankSize( 
      (getJComboBankSize().getSelectedItem().toString().length() <= 0
      ? new Integer(150)
      : (Integer)getJComboBankSize().getSelectedItem()) );



	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getDisableFlagCheckBox().addActionListener(this);
	getJTextFieldName().addCaretListener(this);
	getJTextFieldAddress().addCaretListener(this);
	getControlInhibitCheckBox().addActionListener(this);
	getControlPointComboBox().addActionListener(this);
	getControlDeviceComboBox().addActionListener(this);
	getJComboBoxBankOperation().addActionListener(this);
	getJComboBankSize().addActionListener(this);
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
		constraintsIdentificationPanel.ipadx = 22;
		constraintsIdentificationPanel.ipady = -26;
		constraintsIdentificationPanel.insets = new java.awt.Insets(2, 8, 3, 9);
		add(getIdentificationPanel(), constraintsIdentificationPanel);

		java.awt.GridBagConstraints constraintsDisableFlagCheckBox = new java.awt.GridBagConstraints();
		constraintsDisableFlagCheckBox.gridx = 1; constraintsDisableFlagCheckBox.gridy = 3;
		constraintsDisableFlagCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDisableFlagCheckBox.ipadx = 40;
		constraintsDisableFlagCheckBox.insets = new java.awt.Insets(2, 8, 32, 280);
		add(getDisableFlagCheckBox(), constraintsDisableFlagCheckBox);

		java.awt.GridBagConstraints constraintsConfigurationPanel = new java.awt.GridBagConstraints();
		constraintsConfigurationPanel.gridx = 1; constraintsConfigurationPanel.gridy = 2;
		constraintsConfigurationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsConfigurationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsConfigurationPanel.weightx = 1.0;
		constraintsConfigurationPanel.weighty = 1.0;
		constraintsConfigurationPanel.ipadx = -10;
		constraintsConfigurationPanel.ipady = -7;
		constraintsConfigurationPanel.insets = new java.awt.Insets(4, 8, 1, 9);
		add(getConfigurationPanel(), constraintsConfigurationPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceCapBankEditorPanel aDeviceCapBankEditorPanel;
		aDeviceCapBankEditorPanel = new DeviceCapBankEditorPanel();
		frame.setContentPane(aDeviceCapBankEditorPanel);
		frame.setSize(aDeviceCapBankEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void operationalStateComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	fireInputUpdate();

	if( getJComboBoxBankOperation().getSelectedItem().toString().equalsIgnoreCase(
					com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE) )
	{
		setDevicePointComboVisible( false );
	}
	else
		setDevicePointComboVisible( true );
		
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/00 3:35:21 PM)
 * @param visible boolean
 */
private void setDevicePointComboVisible(boolean visible) 
{
	getControlDeviceComboBox().setVisible( visible );
	getControlDeviceLabel().setVisible( visible );
	getControlPointComboBox().setVisible( visible );
	getControlPointLabel().setVisible( visible );

	getConfigurationPanel().revalidate();
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	com.cannontech.database.data.capcontrol.CapBank capBank = (com.cannontech.database.data.capcontrol.CapBank)val;

	getTypeTextField().setText( capBank.getPAOType() );
	getJTextFieldName().setText( capBank.getPAOName() );
	getJTextFieldAddress().setText( capBank.getLocation() );
   
   //this code was never here until 7-18-2002!!!
   getJComboBoxBankOperation().setSelectedItem( capBank.getCapBank().getOperationalState() );   
   
   //look for the bank size, if not found add it and set it selected
   boolean found = false;
   Integer bankSize = capBank.getCapBank().getBankSize();
   for( int i = 0; i < getJComboBankSize().getItemCount(); i++ )
   {
      if( getJComboBankSize().getItemAt(i).equals(bankSize) )
      {
         found = true;
         getJComboBankSize().setSelectedItem( capBank.getCapBank().getBankSize() );
         break;
      }
   }
   
   if( !found )
   {
      getJComboBankSize().addItem( bankSize );
      getJComboBankSize().setSelectedItem( capBank.getCapBank().getBankSize() );
   }


	Integer controlPointID = capBank.getCapBank().getControlPointID();
	Integer controlDeviceID = capBank.getCapBank().getControlDeviceID();
	
	if( Character.toUpperCase(capBank.getPAODisableFlag().charValue() )
		                       == com.cannontech.common.util.CtiUtilities.getTrueCharacter().charValue() )
	{
		getDisableFlagCheckBox().doClick();
	}

	if( Character.toUpperCase(capBank.getDevice().getControlInhibit().charValue() )
		                       == com.cannontech.common.util.CtiUtilities.getTrueCharacter().charValue() )
	{
		getControlInhibitCheckBox().doClick();
	}

		
	if( getControlPointComboBox().getModel().getSize() > 0 )
		getControlPointComboBox().removeAllItems();

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		//java.util.List devices = cache.getAllDevices();
		java.util.List devices = cache.getAllUnusedCCDevices();
		points = cache.getAllPoints();

		java.util.Collections.sort(devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
		java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);

		int deviceID;
		boolean foundDevice = false;
		com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;
		com.cannontech.database.data.lite.LitePoint litePoint = null;
		for(int i=0;i<devices.size();i++)
		{
			liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i);
			deviceID = liteDevice.getYukonID();
			for(int j=0;j<points.size();j++)
			{
				litePoint = (com.cannontech.database.data.lite.LitePoint)points.get(j);
				if( litePoint.getPaobjectID() == deviceID )
				{
					//The possible point types and device types for a Control Device on a CapBank
					if( litePoint.getPointType() == com.cannontech.database.data.point.PointTypes.STATUS_POINT )
					{
						getControlDeviceComboBox().addItem(liteDevice);						
						break;
					}
				}
				else if( litePoint.getPaobjectID() > deviceID )
					break;
			}
		}

		
		//we will not have the selected device found, so we must add it manually
		com.cannontech.database.data.lite.LiteYukonPAObject usedDevice = com.cannontech.database.cache.functions.DeviceFuncs.getLiteDevice(controlDeviceID.intValue());

		//usedDevice is null for all Fixed CapBanks
		if( usedDevice != null )
		{
			getControlDeviceComboBox().insertItemAt( usedDevice, 0 ); //put the selected device in the first index
			getControlDeviceComboBox().setSelectedItem( usedDevice );

			java.util.ArrayList pts = (java.util.ArrayList)
				com.cannontech.database.cache.functions.DeviceFuncs.getAllLiteDevicesWithPoints().get(usedDevice);

			for( int i = 0; i < pts.size(); i++ )
				if( ((com.cannontech.database.data.lite.LitePoint)pts.get(i)).getPointType() == com.cannontech.database.data.point.PointTypes.STATUS_POINT )
					if( ((com.cannontech.database.data.lite.LitePoint)pts.get(i)).getPointID() == controlPointID.intValue() )
						getControlPointComboBox().setSelectedItem( pts.get(i) );			
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
	D0CB838494G88G88GB9F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BD8FDCD4D53A14E6E6D4782222228D93DDDAE9F58B0D4ADAC3AC5CF577393BD676567647EE3E376EBEEDF91B7B564A1717FF83A6AA82A2383A91E1E13108880A08FF78238232C5BAA2EDD68C4C85C60719E96682C3E23E6F1CF33FF36FDC6E8CFFFA3E676B777B3CF33EF33E3F67BB5FB9775CF3C848FABBA4B9BEA1CB90621385793F8371023052AD885B36763D07B8BFB14AA0287EFB87E00B703CBB
	891E81D09778D214A1DD68B226C2B9944AF9BF0EB2EC035F1902556890610797B975D0571D7F661FFC4BA929C2B9A9341C31A58D1E1BG42GC71E2BC5A27F03ADB9B2FE110CC708CF9004951D244D71ADBBE4DC85141B81C281A2BBD97B4570DCCFA55FD0D9A137BBBFB9C4D87D237793B692BEAA1E88A5DD0C56A8BF73041971EB3B08AC9B55135078C3B99A00981FA844EEB8814F2A660BDDC7BA6CBD459DA61B4DEE13440E6EE251EC116C4EE23358E769902142518195CE3B3532320E22EACD0E9A136D62F1
	5A2C59E4932D02606EA63266ECC9253C8561981413AC84770AC0644F05729200159CBFBB1E606B61F79B005B02B69FFE3FCA36297552CCA16285DB62E6AFB687D8182D11964566405E57D3F2E97FB88E13FED88DF5B997228CC5G4DG8E00G003F5038682F7AG1E556DD22D3D2747EE2BF6B8EA2CE6675F6DE6518A3FABAB21C6461D96BB6CCE33A0303E1736946968638B985D752326E3BE895B07F190FEFA3A306C11D389A9BABED996D61290F60179E25E8545A7CC7693DEE547DE700CD3CF596784356C50
	897644E49D59914F6DCED8AD73C93D28482E38C87A3A1D76F5A17C0E176303612D94BF28423318DDCDE306147D21AE6EA246C66BA65425A9699EA1FDEF132E9F02222A930CD62603552A5565F882619DE4E5B237F0DDDAA8BEDA05E73ABCC1F5A465B8289B6C09B230789AB8CA46F02AAD4AD0GD085E08410G82GC2ED98E3732A760CB0461ACDCE292657E2B50BCE01518E9C4E062768161C26861BCBB259BA44961353E2EA370AAEC1EEF37DE16203F68F3A9BE96CF7000C1322CDF41AA40B5D865CCDBD22A4
	BADD2CCD40E1E2D307D32435C6CB0F8882FA9C82728DBA1CC97CE7F2C9E79DE613A4AAADD8FD48E152A7FD269660888C60B7734B74E3540FE07FB200155C8FFFB9C2781D921D10AFAAAB1B6C760B3D8EA331C4283293BF7F1C668E81FE675851478FD4931C9F14036C486774118C951FBACB87D15F649C40B1569DCF72F4389DE36615D60C193A0D530422679AF5E3064092BB0B1515F66D58F1D21EC4AFD6FEDDE89D65B85EA46BB472DC1199BF969909A3EC477C1BDECB7AAA4B91E5A881E8F6200FEFB91247E2
	36466492B1CABFF6C20009E64563CC4FD389F932BF096CE4D97F8DG1B65F173AE3769BCED5B8DF282GA281E281D23F8279G2DGFEDF210F1F0E7753613BC9663DC945FFB8DB5F9D039FBD7BE2B955C52D47FA0DAE9BE5FD0663B792FD3D816B1BF1C7099C31CF34C9F522C334196107CB2E1BF41468A5670BCEBB278920F8876417DA9850AE0E3F73A85147A4C9CECBFB2FA41E91A549E26BD26A3FC3E539FA5B9DCAEAC2D9530E92DF587BA13F70765FBBCA46A6AC97ECA28D92CEA25707535CA0DA459ED03A
	565EEB13987E61E3E4FCDD9487645CB46798D90075192C3DA44F31754AAC93713543E9690150A329D2C8F3123C71BB3E5EA2908671C356DD3170DC7D9566007EF7B18794FCBDC51877652E843DB9ACE8DADD02514946E837D34981CC276FD66B6B7E904DD1B25E45F4951C240E2D167ECEF26EC9273D5721DEB0317157D0C7F8CE84D941G91CE9CFF3BEBC8BD66B8F960555A2DD666D66E6F6C5AF452BF62D73DA2CC8DA4191E62BE5CD91B2129EB117D496AF75592FFDAD5F9586CA27E8CCC403569A250E7B9C0
	120B79344E45FC425A35A6E23BD9D00E81D8215B6EAFC9582EC002F9D272566E1FF93B7350C6123C499D170C6D8221CDB840A2C92F5DC98336CB6B05B99A202ED72F5D0694ECB79E6A0381664A6D5676FA68475BB577C5993A81A676F972E3FD1F72A5D9E7692D2B3BFAAD453D12054CDAA689862823D7224140564EE7B2D2E59EB1FD38C66E591AC6439766C40012FE0C671FED44B5F2604590A1F66AF62F6F85757D72BA28DF09673CF138C63E7DB811998EF5F37BB176C2EA5573F4354BE56F3050A5029CB79F
	5729631D610CF5EF41335362F4C98D10105C98E37F28A37A12B5040C1657D4CF52359631F5B148CDF5433A4ACDE32AB4CB257BB1B7DB8BF443B31172ABC8ACA5790A8E02E276E8961D1DF6E70FE8AE3CE06AB3951B7A25E21A9B0B2BE975F172FBB776CD746E8C2627C83D4F96B6053BDBF02D7BC066B10C7D6F5FA3D4DD49CC50FB771838F9E7C234DBDEAB38953F8EBEECC8DC79459BA94C466B9251460A81F86F98C09B1F6FD35B98867845G2B8750469509E43D58E1F20AD2B17DD756121867AEF675C30216
	AD2CA5353201CCE6DD924ACC7DBA4AF06CEB1419AE6949F4C3BD23DB43697C81974D6966D4EB7BE3AD5465DDC23A70E424AB831C759252ED5D27268B3D446896DDC29BB38DE36DC7A657E1C0393140B3E790653EAA6949ED9EC439AFFCCB39E5A9A8578CBC03395C682DFAF217F0397726FC3B38BD5DFA921EF6073C14B7D94FC8909270C251EE2F313B3F2C56F0EC724E7290843B5C675C169520E75EE53445DA2AE7CB6BE534654B54315A4256D22D995A77D06BE5E5DD6CF7F9689A193D1F7D1A6642404B6C7D
	AC0563D7D1FC388A4F6CF3D1BCA9C7C3DD61B778CE11FF122C977C219C86B08FE0A9409A001CAB1883FFD337CF98661D2D81F23A1374044762885F1D0E93DF41FAC6AE879FA7FE266B99817DB0BFC16B0732AB1E36B7EB4A0C6EE7C3680AB5E5C9C347E268C88B51054ECB2AD4D2D959D85BADF6DC04B01277F0EEABA5791D6C1B79DF417966728975FCB3F7F3E942861D3CC8A0CE161D6E276445A23EA733281E4C214DD0E785988F9082908D908B10F49D7A7F3D93070773BF8DAA233D96F075300629552EB939
	3FEECC9C1B1F6F98C22775F341F5FAFE5ED538C35F4FC6D8171F3008D6B3DB373E172DDE37D6DD47663AB6F8CAG2CAFB676133EA0F33B7FF578DE5418B4B6FBDA0750E96D09535843F4B8A9218E2957238EEF0ED107346461F430DE6FC947FCE529507355F8E89BF4BD7AEAD5B5466DAC2E6743C3640DCC4FF9436A19B5CE2F6F3B2C5E46181C2AE51FB66DA33A12B1D4B48EF56DB30CCD5771C368343A860C536B578FFAF13D9946F528CC991B8E75C3687E2AA9476A6A7046D674439A2E431BE354A1ADF5B8BF
	140C5753E1EAA96AD08F75ECAF4ACE5762FED08E64384AAC020B04726271286BEF699C339A4AD9B7C4994AEEC07C2B94CF56D8647D094C1F8CBF30F538BD1D5563074EFF9EF1F703D2DFF6031E9F7EE9D899436D91A5EBF82A6BD8FE7E702477BD0852137AFB9095A77577A00E1C5C427040C33D830A75A7CE7A5E237868245E9E45CEEA67426C4D14374BA53A6804C1DE3C643A5FCADB851D3D0A0FCCBD226C23CC61750475F21B63123DF6AF77F1E0295647C6680BEE39BF8665E7002AFFC261F7A440A2CD1F45
	C2F9B5C06A04A8430E892C0E4D15E79A49BA2C84F0CDGE600C1006089B8C7AEBFB96C9A65385B479A6579FA6FFD78D23DFE9F3EDCCF6681173D5759A1EA7B697775B93CCE190D9BA43107381435F91552CBA6E717B8241FDF27742C4E9BFDE20337D8E07D75D4223A3FCA854C4B2F6EA63CF572326A7D7B0B6CD439FF67CE40776F00F7687806F25A0DD106029B79373FFEDFE32C6AC6CD3E124BE6F83AB5F57ED08E516018AD57A529ED5947E32FC972A5BBC243ABC6AEAF03E71C26AEE9A24C239375E417A72B
	E5178918AB27D4200F0AA6220F1E20FBFAE6A88F8284CEC49F7D31DA2BE7B8544D1365AD111FABD47257426F741B404FB7F96AD48165A600B6GC92EE3FA7E4EC34F7D5CC71F6C73652309B7696758E0404F5254C5C3F969CDFABEFA482016DD41E56F69D54FC3ABB53442242132993F2FD2547C8EF2FEC2B6724B825AC200AA950FE678ED1E245B0729EAFE651CDFD8A972F3831D3F06B6844A91GB1AABC63B7A1036C49DACD8376DE89664A1BC95F5F17016B32E550FEB5408E40175C2C5E8F7BC1BF19DBBB1D
	269E98F2E79CE207D060CFE8BF49C25AGE89F8A908D309CA0499F5FB51FBCCB76229AC8EA32F4DABA646FD8E47F1455B3283338487E65892B290B3F4B307AC7BFA67BBD10CDBAADDD3DCE95397C4DE98E1D7B2DA04F4F9F6779F594978865D00EB353755AACA847F05C1FDD84378C4AEBB86EB63AD6CE3B053D0BB25CC394D788383ADB305DB7D4C6AB143BB9AE1B661FC1A887F05CB45AAE984A919C17C679455C423E63B35C07922ECDD65C027343E70D5E73FBFB23FEFE6FEE541F23ED0D3E67683EC6EFF919
	45DCC736FACCD45C8A6379D6A53E5AEE9DBAA6583BFDEABE49AD522D4C1F9BB9FE9B458729706C1D7FCE1A2FC9F9AE54955E06BEAB6B477E8B6438574B88AE8C4AF3B92E369C7BEAB147CD6D433EDA4EF1874E213F57F05CFB7D58D775810833C9A877BC47B51622DC87475DDA0AF247F3DC2C8B6586F1DC2B916506F35C9189652E8D405CBB3DC26D6F744910D3A7ABBE2E186C4D5F9D749BC753E466572D9C5FC771FEAABC737753B47713F28454357D936A14D50363AA6DF62EFB8D0E2BBC0EFB2C867DD344F1
	2F646238BA46F1FE5B070E2B3B36234F0C3CDDF0A59FD79C7753242163AAA199FDB6485BCDBAB2F4DCCDB9027E8E606DDCB49744C2F98D47BD4E46FDA044DCA062D651BE2D02F293473D170D7C5AB84E48F28B14E3B86E84FDB7DB8665159C17F096E905BB90370D72EB06321563F6D19F78C1B910632C1548AF1463A6A6A33FC80E6BBDC8F029F7C29F5D09389FD2BB4A205C4CF14BE9EC1A21ACF11C134666C4A807F01CBF4FCB919C97C679A55D85F345DD380F3CA0834FA815G2E7BAEBC2334374DE3AF056C
	ACD6D61EF4DA4CB5262EA6BA93497BA41929B2FD48DD38AEB914C9F8AE05F29CC0EA906EE5395F433D2C70697788CDFE27F43F1D449ACB93720258B8298BD2763242FADFCFE1B27B33D0269F54C78651FC1D255EBBA5F3501F9358FBA82BFF41231E644C5D023AFE3D47BEB251F7DC223A7E10C7FD320A1E597663DDE85B5A9F07895659873DFE97926EE6B695484F6616F70502C9EF2430F973A7340FFAECA23CD8D8BA2CF753BE273885G676F4679B9FB9F79EEAB41C4B26BEEF2E66EDFBF9184DCAF34EEA773
	FE0F49ED69696DB9E379DA94981FDBE83C2FA66D6FC1BE1BA422A75BCCA62FB3A61B19648F5A7EFD9AC3C5503602371FCE5B375B490751E61345A609E6AE7716EAD267F00A1D2253A91A29E46475BC65E584BEED1C5740A0312153EE13D81B6FD2BB7D21BE083779DB36015A626C3258E02CD2DBBFBA23587A84DD634082C7E36BCFA92F9860330473FAD7A2EB16EE3B5372B548B4D92B2D16AE9B7916A6645DCBE8EA24BA1D7ED247D40A7CA4A0G8D01F765FE4E025F1F483F59F87B1A6608EE408F5E0BE3F0C5
	834105C0B902633ACE61D8DDFAAF4EA31B72499A2DB09856AEG67815CG01G3302314DCE5AE6B11463G526FE35F02CB6EE35FFAD91B60F75A3C58D3A773A3FC3D590CD02853E6160CBB4F75F9542BACB2DEB6884A598DDF2037C732D1FE2670F87A1C4E794DE06B797B30FF3714137A8E3B35374766125B9552F5455BG81500E612C94978265F00EDBC4579ACB213C9C004543C767FC45C332006BEC86B95F013974BBAC9E887E987C7E039C8FCC4647D467A64037FE87655EC54745F8A887F01C2B94F50E66
	38C7A9EE8914E3B9EEBD1B7B6E07F7497BD1E73BF1B81D55B15CF43F274EEDD01E9D2F56390B4A30825EFDBF466B13959857219CF7B18563BA0663FE110AF13D72FE0C91CBF5A1999FA11087G46830482C483AC879026405C84D08550B685E986A98DB917C04E8838819FB4051DBF081C424E83AC1D424E992C61B44F9E8B11EDA63C75FCD1A56309ACB5BED4533ECDD39E6F051F9EACD73595E41B303C036B7AC72E6B86AFFCF0CCE4EA705A71142CA9EB7790FD41C655EF727E32D5D55E426361B0DDB7AEA471
	7EG64A21A0F3FB28A3C7EB7343E906A4A9E40B85DC6570BED244CF19B4CB457C3B90863965035D2B41497F35C07F42DA4CC05756CD40C7148A34365E9B5E873F445D436164573ED1942877D7A73E5B01DAF6B20FD73D44CB1997B3C4FF1AF53316287ED7DF97B67A44FB9AE1B720C047AE8EE4F616C61466CFAC149B52B27AA7650738621D1068E8FFBAAFA7567444BD4F696344F8BC57D46D5FA6AE7276B5FA628EF8DC57DF24F0EA42720FE93C3BD758B06726D9E7A95E7FB7761C9BAE70681CD8457312A122C
	81581AC390D60612F6E7285E312198A7F75358A9198639E39A6AFDFF72C872B76AED1D6629375FB42DDF7D8F6A7BB5142E5D7D21FD50B4547979D26FB67E016672E8E83B1837FF4365598FDBE8AC2D057A54695866A623FE1CCE216B7FAAE8D7BF9DED1F6AF3AD0373B10E1141691E3687C0793FBCEC7FE1293E6DAF30FD92E89FB69D73590F071CB3988E985D0BE3244B489CAD9DFB375E18C55ED316CCD7728143E7D3FC2C8A4F5E152AB371DDE9A554B5FF974FB5984F12F5D8841497832CG9042209FG2A42
	F0CFA86DD409B04CB783235D51A47609D655B60FBC7FE50D6D6E4253A978CE92717883C2640457623D1D9DE88EE37312C3FE2EB03ECB714C3E5F36907BC220EEAEC0AC409A003487E14EFD906DB31EAAE2761123165512E46A682627545038960B582F2049E6249C77256479FE7B7B324EA17D6AFBB795EF9D50BDAFCA7B70C126EB137C1CFB633BB20FFF7ECC4DA37D1EF792623D7098AF5386BC283CE732B92D67D301477ECA97D90B59447EBCFE48264E82E610437705FBCB779665156E5FAD3F1B847CC3211B
	A811B3227972A6E99EBB141A4F5ABD7219524ED5BAF27E3FEFD3685E4A4E57521D0084D218EF36182C762E7C223C12223C3DE53B713DA927DD210D17C6A273ED012DB9689CAC7F47F8791154A87B7B57E998E7BBE65CAB543D70DABC4E35DAFFC74BFE4E7A1E626F9597564A6F3E133355F91E6DF30771339ACDC0E38688861875BDF60E0E512DEB983ABF1E5D08F4B15FE37B633164A953319C794D29512CA352E6G5D8C16A3C9398CFE7F8D1EBFD937EE9D53A3210064FC6300C36487CE383F192FC8DD13291D
	EC034B78C59F91D90D94C92EF6310F20F27C38E99DE7262E1B360144962128B61B1D224BC5B9303AC7AEEE712013DBG0E1DB3BB136FF136F0867309F9861EC37BA8D72F5E4D6BFFD92ED76F4F6B376A56074C407562ABE6A58ED7153D67BD8ECB77169DA82B58373F6CG04E349EE24B9DA1EG4F3D65980BBBBA95FEC1FD3E62FADFA9E34468A23B953A8F4E6D529FCBAE134D65921D16CE55F0529997F1C73D0F15DFDA145FEFDED0E42EA99B392E3B2C8A5D6152517BECD229274F6AFB94FE4F397E6A1D5F3E
	FDB29FC6D764D468CE993F2D4F984F0FDD0AFF1C12427FD069C874E2637D5F78B9250899ACCFAC02E76C8CF686043589CC41B6EBE6301C109E8EF3D8381C0BE8CE78FD6AF0B9C17D4EDF85344D61CACEB0436FD5108FDE65B9C19048BD32DAF60D334156EDE937C87CD319EC675F2BC89BFED2098C6A0F7140A93629591D6911A714862CFEBE7DBEAF4BE936DBEC12E71E790F53D91AFA94476AA3AAB74A7904AC6EBCE92F6B5E2468A737403AC6F30642174DF0B2B12BB71B37EA6B7909ACECA3FE7196376DD4CB
	75E94CD31F26EB738869CBC7B8661966BE3D7A713CDE10746A03F87DDB3A75613C7EB55D7AF961B86F98EB685A457C3E9C3BF774AA317BF22E4F7935CCBD27C776A9F46E6D249F73E507654991132F6412FE256D644A114B0878DA210BCA9A5D3A61B717945AABC7C6AEB37932B22E9F7AC66171FD699D9F633A7440FE123A4AF7F35A45D7945A5F78DCAB1517B0CAB9D7A5A8F45BC734D6AA5D6DB3CFA52A72D4122A2FFD2E8FBDF5FA5120507D27715B6B34B6C551696D75AAFBFD46866A445ADAAA891DFC87DC
	D42F1DCBAE45A710B63AF7B6653C39BCDCD9FFB3BD96EED07498CC7E76B676EFD4EC1C36C9613DB7FD64FE0F592C503DB80A792EA0C32193C7BA5F15AA31FBAAD3B5F624514C5B5E7DE14BD27CB1F92BEAEC0C48AE790EAB6541FE6FD07DBE19B9163B2F23FBDFEF3B2AB2587B6867E73D1F41B01F55BF03F1612C7E998C47D95F67A05DE73D5F553CF2D6FDD7133DCF1B320943339E0AB214G98813A81869F42F7690DADE44C105B69C35F230F1B3B4899BFF2A3DDC279F1AD5EED7DF34B70775805F3E47EED37
	CB123D0770C73C619C69AF496E20C8065BF00E7848E9696A16D8D3993F051E4B300A1D944D6C7CE69B418501DD915C36874EFD884F965385F987045D4C2456813259C9EE5AC8838CCBEFCD4ABE9BA0CBB7D9568A3AD65765819EEA04587F387D0258A199CDDD5C672139E4DD3784740883487AFE1421886098C0AB0095408FA090A09CA09AE0A940CAGE1A62C85G8AGAAE6229D8BBAF67B30C37ED0CEBB4AC93E128A3807B4E19B7EFD05A660B5FEA6FEEF7F6DCE424F775DC523C5320AB5726B31FCAE70275B
	70AC795C19787D3D3C011E1D04F25AAC50FB96FE6BBF52087BD7D50013E661797D890D78BDBDFC96720820ED1793DA002C9FC499C6764E03B94A0F38894F00F6FAB983CAD7FCCC07C9790487092D229F20DEB764627D87B347E56CC7DC8C47B515A32E68E134F553C174F3G60E29E4636AD8751DF42A3E8EBA43D53D68165E6006EC710475C9474179F60A29FC19EC929682FB84E23B4157E1D0D8808A9005608517AEBAAB9EEA177E9C0BA1E75F4C4204C9B7B5046B00EBB406F07244DC6DC3544E9B92E2497F1
	E133F14F72331DF47FFCB646F37B7AFC9F71CC6E7A1C0174E76B42B1E849761EF39C5943671C813A4FAFC22EA0FBA28C778D5D7F31F7F63AC41E5B46E527F3DC032B596E3210179E39EE9B4977BCAF33F401777137119CEB31C1F3135572B5FDD5E296307A48DC6F7A7FB0F7F87DE3F275F366CFF307664CDF646A674C97F355B91361FE15CBE2338B72C7ABEF77B247FD4AF1ABA8CF02BBE7B1CB5D9C7F8A550B606BC5A290712B29DE7D9457AA63DED361BE15F1EF6612BEG133B6DCE24CD5CCE3FCDD82CD6D6
	CE59EE203E05AE617EC85BCEF434B8CCAA3D37F05C27824E7D7E5B065B4BDEBDDB6FEE45BD4352E96F3DB03ADFD03A74B9A37BE6DBG6D58B70087E98E2D18237DB670B045574D51FE9BB854003922996AA2666037103A4631FD53B8BDC63A1BC7CD476CAABCC5744FF9D4EB6FDE0AAFF8D4EB6F63FCAEA9013A40C7515E5B72D17E9A4025C54AFBAB7233ACF2E8BFB03AC939FAEB42FA397DF99DBA828E400F575485C1B9BCD2AF06366D5713B1CFEE3B440B0C9500DF2B29CBFF8C66554774E4FCD22EA723C2EE
	5B74183E0CB640CB1A3A09D08E561511F5F0ECE3A8FFD8BA7DB17499251B65C5F7ADC4BF06E328084E1FCB9F5346D4B945AFFFCC9BD3CF99B02662204E78B846D45410FBE9A39B8B0B47C837FE48FD36110D2169F48D907938565E19949F7D38565E0AD434F7B15415BD01765E112E169F748473FB387C1C770437B1645F2797FBCB6476ABF468882C85FC7ADC4F3A82A8D74C550B21AA5DBBB0CDF25B36397AB2A440CF545485C3F9162E0C93125A7EE8394DD27939D2C78623AB77387FA1BCA9FFEB121F05CF8E
	25E3735417BB49FC661257B76C0E5A3CCC322E32582C961BD84B7E1A1700677AFECF575CD5404F78A4BF93D5CF4FC4C1F970C96C47198D8257ADD2753B3A616A3FE37F63FFC1C671A94526832A5F739A2F3E6C972F2A8C56275369BB49C4684FE05E4FE78B6819F1A84F6338AB746C4992A8AF67386515F876AD0E634269FA6F58D3701E7194622A3763B9FCB34775D56379FAB747358F6279FAFF0E7BD38B4A886138D0FE97A20E638E6EC739C9BFC45CBD7BD1EEBA472D6AC539859CB7218C65D6F05C6C9C145B
	44F19F6EC739419C67AAC73933B8EEC9B94A1D4BF15F6FC3390BB9CEA8C7394BB96EDEAEF70DE2873F27D3BF0F1F07276FF86721ACF1DC390176A51403676178ACCDD1E276106AF734616A476FCB7F8BB242D3951BE62BFE9FC93D7A324FDDD59972F8226F49E1501F113C1FB751FD28C5D0DE46F1AEFEE7E845BC4F182BF89A6266E944E576637D92A3473DD10A77CB3AB96EA9896F178CF25CAAFEBFA800635E69457BA5319C778525DD8D6524A8444D2738F4A897F25CB1F607854A2D9C37025E7D30C2F91063
	8EABF2B96EA065978B65B59C77B9BBABBCBF4A30E3BE3F6BC2E9CB20FC0C63E6F0FECD9C57C46D8806F2B8476550BBB173203C1863AE73FBB24B6663B732789C721EAA111DAB32D7AE609938DCBA160B22A12FC4E35BA5754958D6407D6D274912D45E5FFEB21F6E8FB8CCDF1177FC46E7894D51747C9E67E3AD70BCCBF7BD4D5903D0BF1137393352737E44F232B5A3FF83FE23F23833F36AF3A1C51AF35FF3C1461F63597D90795BECAE4AE27A30777E98E837A49A4FAFBA68BC908765359CB71D4D838BE0ED38
	GF13751F9200D149760B916B41F67543567D8547A1235E77082E5DD4AE47C020E03B040C7AC40B39BA964131DDCBF1F667AD828DB49754A2663A9679918BB1EC15CCCBAF7B4C3797CB3286B221C51FCDF576A6A7F0CD657AF29AEC1008FFD86F57DA0DF51753F68BC32986A16F13DDE203A26BE9BE548FA96F13B59DC856526E7D157070755B545072E034FEAF51DCF4774C44087BE4B4FF49E549F9FD3689CB68F5AAD62ED1B8CC347C7CD21B2BE6627FB467D0BB4BF8AB1709E9E03BCF61D730CFBF137927713
	060DA3F55FE84FD5D745E84FD5AF687314B5718325DFEAE80C55830DB1061F752667137D206C4FF17F4E6E31C1B9BA867BA54967F9E4A2FB3D8FBDD747E87B251966B7E1A178E9A17A29D77254FD4E070A1F7FC552BF077BC72AEB85702863FC4E79B8876C2071600036033C7DBA4DB9602D14E79854C7ACC47B0DBE6FF890BE9BFC583FFC21361F1AA57D333D5DD4F69C34DF4B7513B46759A731BB56BF029C75A3EC73395153F7AF96A9B196B52C6E3E6ED819C146998F5DBBFABD759920FD6900F603DC1FBDE7
	BD75D97BB7C51FC01F3104FFCF571BBE61A003FC9BFCE35D3AF50C776DA5CA1C4C2AFFDF70716D3B385DEEB56797656DE507AD984D3F67D3DD1D3DA2637707D2051F33F2B47C98FD86799392726F4A7D8A2F4A6D443EFCF6A2A70F6C41D766E33B6E83CA3B0B55BE3EA9D76C83D17B7937721BAB943AF303A3271BFDC8217BDD4B684E26AC2DD26CB39CD178BCD5BEF2793B0FAAF4AF747A22BB60C157F4CC213BD4B6F23AC7EA943A17370E4E5E67EB95FBDF2BD378BC56B7F279BBCEA8F4D3FD1E175CDB4A8899
	5D51138A5D1FFC1EDD74243B37C1213BADE7F476BE5528583B72144267D51FE7EEBC6557FF2450959AC6BF06E6B6AB748967C6277F4FBFD1747F53E905CF1C4FF3CC7BAA547A6798953AC7FC1E5B76243B5222507DFAC4E70D943D9FB827683D20D56133E694FA2FD01D21AF9F855D865559781F7B6CE7CF3A2E4F953A3D2334F752970A3D11DFAAFCBE9E055ED92A7375152314FF20C3117FB355F96634332363331A9CF990587FFB658D271881F354AAA1D99252709682B94069F28A1BB93B867A9DAF36EFDFE5
	624BD3939304248641403EEBB688C96463E40310CC3FDDB68809125D61A67F1F0284B5C8C2BAB2170FAD3AC0C046D0FFD55F7A628B61DF56E5A58889A023DBE4CC137B4987CC4AF6BB4000A4EC723CB1617ACA78C6C842EAE03D5143958B26147F5B22DF35389285C30310CBD682E7C4A77D68AB1A499F9D137FAF89A704758EF26C152CEF40B097FDA43B24812B58A4E4F51EA6FF2D5BA50966EA7C1C6C12042D744F396EA127AF76541BDC5D92516AD01310E4A37FBF87A103D5DBCD362EBD6C5B72A1EAC6FCF5
	9B69843A0290C05FCC2AAF61D24C39E4BE31F957BB0D4BA39305049342DBD63B49FC4254A1591D8D42FA295BA93A3AA18F399DC7698CA3245245B2DD08G3B3FF0730931456418BFD77A227BBE7F0DABB61E000EF1BB05BD34A5D978947396665AEE1393C4094E86099CA136BB8BF85DF5FF155E7B4949ED679304F859C8627A375C8EC9B0F41AFAACD6689C838BBC5AA91BA45056E93239485F20854D72B41AE19597D0FC7B4212BB9EFC7D9FD400C2AA89EFB3D782691EAE515E33273A3353E293B98370AB305F
	495913B6451AB6996E49DB9B1FDB578EF1D0A7A476141450FF7752FFCB493FFBA9E6AF456CAD85674689BE7F53563B51E69ABCFC8C116EAFBE81BDA87D4B2A058F7C32AAEEA5D8F7C2C826FFF396BAA1DDE975D1EFCF3B681C90FE474E8D0131B713BEF210D0F1933E99EECD1C1392D9E36689743CC8BCB9C313405BE4DD01A527D28218C6CB29C3620FE053068692C127C37F71330B8411C9DBEF257F660C5ABF04EABB255D414D5A656606608BE42627A1G637F5F1935C7B1EB3B4A2CA48F555F716851C1C958
	2CB9566DD9BFA6C36561F8F52C2CF46BE808DDC95EEB49591CC45EA697169C6B54A561CE15CD4C02341158D4F795ECBAA6176BBC8312405FBDFA6E9A553BC951BB697F135EC6FDFFA76A680D6326E6CCD62CBF83C37E4C7F7C10C7C3BED78603122508D95932D9899EE6A989GCD47830E899C9B2F42A6413A47734F6DC902A089EF1175DA9E3979D7896BACD8E508221457E4E7ADG355163CF6CC10A216D796DA0E83189138E3D75D309AC49BA28D2151258DA8BB0C9648A0EEEAE0A775283C91EF1045324011D
	ACE3EEA4B4D7A7C9B95D5FC6D9D222ADFB2F0EF6171470A7710193C5B6D9112B76DEF858100CFAC627F4CD4D8BB7F866A2A21B3C25647830621CCE691A3269B64526E459A632231264438A07CE691A3269EE45269D32CD44B64388743EC6064F947D417DFFB22321CE5FD53CCA760749BA085CC7C876706AA41D52B5959B3394AB7094B9C9D02943DAF18D5B7438E2D30E4AA63D1E3926740ED274B62874B6DCFB313FD051F41B2CE51D2CB53BBF387B0A7A7E601A9BF23C7E7D0E25AF496779DED27E96843B1B70
	13817ACD69D7D1063ADF617D041D8364230D37BBBFE7FACCD62BE432DE2C3619497D1F1A81C9D46EE77DFC0070F7E19B39D2BE03BFC0743E202279EFD0CB87881DFBB84F4AA4GG94F9GGD0CB818294G94G88G88GB9F954AC1DFBB84F4AA4GG94F9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG84A5GGGG
**end of data**/
}
}
