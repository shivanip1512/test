package com.cannontech.dbeditor.editor.device;

import com.cannontech.common.gui.util.AdvancedPropertiesDialog;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.data.device.PagingTapTerminal;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.data.device.Schlumberger;
import com.cannontech.database.data.device.Sixnet;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDialupSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceIDLCRemote;

public class DeviceBaseEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener, com.klg.jclass.util.value.JCValueListener
{
	private int deviceType = -1;
	private int paoID = -1;
	
	private DeviceAdvancedDialupEditorPanel advancedPanel = null;
	private javax.swing.JCheckBox ivjControlInhibitCheckBox = null;
	private javax.swing.JCheckBox ivjDisableFlagCheckBox = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjTypeLabel = null;
	private javax.swing.JLabel ivjPhysicalAddressLabel = null;
	private javax.swing.JTextField ivjPhysicalAddressTextField = null;
	private javax.swing.JLabel ivjTypeTextField = null;
	private javax.swing.JPanel ivjIdentificationPanel = null;
	private javax.swing.JComboBox ivjRouteComboBox = null;
	private javax.swing.JPanel ivjDialupSettingsPanel = null;
	private javax.swing.JLabel ivjPhoneNumberLabel = null;
	private javax.swing.JTextField ivjPhoneNumberTextField = null;
	private javax.swing.JComboBox ivjPortComboBox = null;
	private javax.swing.JLabel ivjPortLabel = null;
	private javax.swing.JLabel ivjPostCommWaitLabel = null;
	private com.klg.jclass.field.JCSpinField ivjPostCommWaitSpinner = null;
	private javax.swing.JLabel ivjWaitLabel = null;
	private javax.swing.JLabel ivjRouteLabel = null;
	private javax.swing.JLabel ivjPasswordLabel = null;
	private javax.swing.JTextField ivjPasswordTextField = null;
	private javax.swing.JComboBox ivjSlaveAddressComboBox = null;
	private javax.swing.JLabel ivjSlaveAddressLabel = null;
	private javax.swing.JPanel ivjCommunicationPanel = null;
	private javax.swing.JButton ivjJButtonAdvanced = null;
	private javax.swing.JComboBox ivjJComboBoxAmpUseType = null;
	private javax.swing.JLabel ivjJLabelCCUAmpUseType = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceBaseEditorPanel() {
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
		connEtoC3(e);
	if (e.getSource() == getControlInhibitCheckBox()) 
		connEtoC5(e);
	if (e.getSource() == getRouteComboBox()) 
		connEtoC4(e);
	if (e.getSource() == getPortComboBox()) 
		connEtoC6(e);
	if (e.getSource() == getSlaveAddressComboBox()) 
		connEtoC14(e);
	if (e.getSource() == getJButtonAdvanced()) 
		connEtoC7(e);
	if (e.getSource() == getJComboBoxAmpUseType()) 
		connEtoC9(e);
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
		connEtoC1(e);
	if (e.getSource() == getPhysicalAddressTextField()) 
		connEtoC2(e);
	if (e.getSource() == getPhoneNumberTextField()) 
		connEtoC8(e);
	if (e.getSource() == getPasswordTextField()) 
		connEtoC13(e);
	// user code begin {2}

	if( e.getSource() instanceof com.cannontech.common.gui.util.JTextFieldComboEditor )
		fireInputUpdate();

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 1:16:35 PM)
 */
private boolean checkMCTAddresses( int address )
{
	try
	{
		String[] devices = DeviceCarrierSettings.isAddressUnique( address, new Integer(paoID) );

		if( devices != null )
		{
			String devStr = new String();
			for( int i = 0; i < devices.length; i++ )
				devStr += "          " + devices[i] + "\n";

			int res = javax.swing.JOptionPane.showConfirmDialog(
							this, 
							"The address '" + address + "' is already used by the following devices,\n" + 
							"are you sure you want to use it again?\n" +
							devStr,
							"Address Already Used",
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
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC13:  (PasswordTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC14:  (SlaveAddressComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (PhysicalAddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (DisableFlagCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (RouteComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (ControlInhibitCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC6:  (PortComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		getDialupSettingsPanel().setVisible(
			com.cannontech.database.data.pao.PAOGroups.isDialupPort( ((com.cannontech.database.data.lite.LiteYukonPAObject)getPortComboBox().getSelectedItem()).getType()) );
		
		revalidate();
		repaint();
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JButtonAdvanced.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceBaseEditorPanel.jButtonAdvanced_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdvanced_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (PhoneNumberTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC9:  (JComboBoxAmpUseType.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
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
 * Insert the method's description here.
 * Creation date: (9/18/2001 4:29:58 PM)
 * @return com.cannontech.dbeditor.editor.device.DeviceAdvancedDialupEditorPanel
 */
private DeviceAdvancedDialupEditorPanel getAdvancedPanel() 
{
	if( advancedPanel == null )
		advancedPanel = new DeviceAdvancedDialupEditorPanel();

	return advancedPanel;
}
/**
 * Return the ConfigurationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCommunicationPanel() {
	if (ivjCommunicationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder1.setTitle("Communication");
			ivjCommunicationPanel = new javax.swing.JPanel();
			ivjCommunicationPanel.setName("CommunicationPanel");
			ivjCommunicationPanel.setBorder(ivjLocalBorder1);
			ivjCommunicationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRouteComboBox = new java.awt.GridBagConstraints();
			constraintsRouteComboBox.gridx = 2; constraintsRouteComboBox.gridy = 1;
			constraintsRouteComboBox.gridwidth = 2;
			constraintsRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRouteComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRouteComboBox.weightx = 1.0;
			constraintsRouteComboBox.ipadx = -17;
			constraintsRouteComboBox.ipady = -5;
			constraintsRouteComboBox.insets = new java.awt.Insets(0, 3, 3, 37);
			getCommunicationPanel().add(getRouteComboBox(), constraintsRouteComboBox);

			java.awt.GridBagConstraints constraintsRouteLabel = new java.awt.GridBagConstraints();
			constraintsRouteLabel.gridx = 1; constraintsRouteLabel.gridy = 1;
			constraintsRouteLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRouteLabel.insets = new java.awt.Insets(0, 15, 3, 31);
			getCommunicationPanel().add(getRouteLabel(), constraintsRouteLabel);

			java.awt.GridBagConstraints constraintsPortLabel = new java.awt.GridBagConstraints();
			constraintsPortLabel.gridx = 1; constraintsPortLabel.gridy = 2;
			constraintsPortLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPortLabel.ipadx = 34;
			constraintsPortLabel.insets = new java.awt.Insets(6, 15, 5, 2);
			getCommunicationPanel().add(getPortLabel(), constraintsPortLabel);

			java.awt.GridBagConstraints constraintsPortComboBox = new java.awt.GridBagConstraints();
			constraintsPortComboBox.gridx = 2; constraintsPortComboBox.gridy = 2;
			constraintsPortComboBox.gridwidth = 2;
			constraintsPortComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPortComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPortComboBox.weightx = 1.0;
			constraintsPortComboBox.ipadx = -17;
			constraintsPortComboBox.ipady = -5;
			constraintsPortComboBox.insets = new java.awt.Insets(4, 3, 3, 37);
			getCommunicationPanel().add(getPortComboBox(), constraintsPortComboBox);

			java.awt.GridBagConstraints constraintsPostCommWaitLabel = new java.awt.GridBagConstraints();
			constraintsPostCommWaitLabel.gridx = 1; constraintsPostCommWaitLabel.gridy = 3;
			constraintsPostCommWaitLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPostCommWaitLabel.insets = new java.awt.Insets(6, 15, 5, 2);
			getCommunicationPanel().add(getPostCommWaitLabel(), constraintsPostCommWaitLabel);

			java.awt.GridBagConstraints constraintsPostCommWaitSpinner = new java.awt.GridBagConstraints();
			constraintsPostCommWaitSpinner.gridx = 2; constraintsPostCommWaitSpinner.gridy = 3;
			constraintsPostCommWaitSpinner.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPostCommWaitSpinner.insets = new java.awt.Insets(3, 3, 2, 3);
			getCommunicationPanel().add(getPostCommWaitSpinner(), constraintsPostCommWaitSpinner);

			java.awt.GridBagConstraints constraintsWaitLabel = new java.awt.GridBagConstraints();
			constraintsWaitLabel.gridx = 3; constraintsWaitLabel.gridy = 3;
			constraintsWaitLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsWaitLabel.ipadx = -10;
			constraintsWaitLabel.insets = new java.awt.Insets(5, 4, 6, 10);
			getCommunicationPanel().add(getWaitLabel(), constraintsWaitLabel);

			java.awt.GridBagConstraints constraintsDialupSettingsPanel = new java.awt.GridBagConstraints();
			constraintsDialupSettingsPanel.gridx = 1; constraintsDialupSettingsPanel.gridy = 7;
			constraintsDialupSettingsPanel.gridwidth = 3;
			constraintsDialupSettingsPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDialupSettingsPanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDialupSettingsPanel.weightx = 1.0;
			constraintsDialupSettingsPanel.weighty = 1.0;
			constraintsDialupSettingsPanel.ipadx = 8;
			constraintsDialupSettingsPanel.ipady = -38;
			constraintsDialupSettingsPanel.insets = new java.awt.Insets(2, 15, 5, 15);
			getCommunicationPanel().add(getDialupSettingsPanel(), constraintsDialupSettingsPanel);

			java.awt.GridBagConstraints constraintsPasswordLabel = new java.awt.GridBagConstraints();
			constraintsPasswordLabel.gridx = 1; constraintsPasswordLabel.gridy = 4;
			constraintsPasswordLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPasswordLabel.insets = new java.awt.Insets(3, 15, 2, 36);
			getCommunicationPanel().add(getPasswordLabel(), constraintsPasswordLabel);

			java.awt.GridBagConstraints constraintsPasswordTextField = new java.awt.GridBagConstraints();
			constraintsPasswordTextField.gridx = 2; constraintsPasswordTextField.gridy = 4;
			constraintsPasswordTextField.gridwidth = 2;
			constraintsPasswordTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPasswordTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPasswordTextField.weightx = 1.0;
			constraintsPasswordTextField.ipadx = -50;
			constraintsPasswordTextField.insets = new java.awt.Insets(3, 3, 2, 160);
			getCommunicationPanel().add(getPasswordTextField(), constraintsPasswordTextField);

			java.awt.GridBagConstraints constraintsSlaveAddressLabel = new java.awt.GridBagConstraints();
			constraintsSlaveAddressLabel.gridx = 1; constraintsSlaveAddressLabel.gridy = 6;
			constraintsSlaveAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSlaveAddressLabel.insets = new java.awt.Insets(4, 15, 3, 36);
			getCommunicationPanel().add(getSlaveAddressLabel(), constraintsSlaveAddressLabel);

			java.awt.GridBagConstraints constraintsSlaveAddressComboBox = new java.awt.GridBagConstraints();
			constraintsSlaveAddressComboBox.gridx = 2; constraintsSlaveAddressComboBox.gridy = 6;
			constraintsSlaveAddressComboBox.gridwidth = 2;
			constraintsSlaveAddressComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSlaveAddressComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSlaveAddressComboBox.weightx = 1.0;
			constraintsSlaveAddressComboBox.ipadx = 73;
			constraintsSlaveAddressComboBox.ipady = -5;
			constraintsSlaveAddressComboBox.insets = new java.awt.Insets(2, 3, 1, 37);
			getCommunicationPanel().add(getSlaveAddressComboBox(), constraintsSlaveAddressComboBox);

			java.awt.GridBagConstraints constraintsJLabelCCUAmpUseType = new java.awt.GridBagConstraints();
			constraintsJLabelCCUAmpUseType.gridx = 1; constraintsJLabelCCUAmpUseType.gridy = 5;
			constraintsJLabelCCUAmpUseType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCCUAmpUseType.insets = new java.awt.Insets(5, 15, 4, 36);
			getCommunicationPanel().add(getJLabelCCUAmpUseType(), constraintsJLabelCCUAmpUseType);

			java.awt.GridBagConstraints constraintsJComboBoxAmpUseType = new java.awt.GridBagConstraints();
			constraintsJComboBoxAmpUseType.gridx = 2; constraintsJComboBoxAmpUseType.gridy = 5;
			constraintsJComboBoxAmpUseType.gridwidth = 2;
			constraintsJComboBoxAmpUseType.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxAmpUseType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxAmpUseType.weightx = 1.0;
			constraintsJComboBoxAmpUseType.ipadx = 40;
			constraintsJComboBoxAmpUseType.ipady = -5;
			constraintsJComboBoxAmpUseType.insets = new java.awt.Insets(3, 3, 2, 70);
			getCommunicationPanel().add(getJComboBoxAmpUseType(), constraintsJComboBoxAmpUseType);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommunicationPanel;
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
 * Insert the method's description here.
 * Creation date: (4/30/2002 1:27:35 PM)
 * @return int
 */
private int getDeviceType() {
	return deviceType;
}
/**
 * Return the DialupSettingsPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDialupSettingsPanel() {
	if (ivjDialupSettingsPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder2.setTitle("Dialup Properties");
			ivjDialupSettingsPanel = new javax.swing.JPanel();
			ivjDialupSettingsPanel.setName("DialupSettingsPanel");
			ivjDialupSettingsPanel.setBorder(ivjLocalBorder2);
			ivjDialupSettingsPanel.setLayout(new java.awt.GridBagLayout());
			ivjDialupSettingsPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjDialupSettingsPanel.setVisible(true);
			ivjDialupSettingsPanel.setPreferredSize(new java.awt.Dimension(384, 110));
			ivjDialupSettingsPanel.setMinimumSize(new java.awt.Dimension(384, 110));

			java.awt.GridBagConstraints constraintsPhoneNumberTextField = new java.awt.GridBagConstraints();
			constraintsPhoneNumberTextField.gridx = 2; constraintsPhoneNumberTextField.gridy = 1;
			constraintsPhoneNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPhoneNumberTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPhoneNumberTextField.weightx = 1.0;
			constraintsPhoneNumberTextField.ipadx = 157;
			constraintsPhoneNumberTextField.insets = new java.awt.Insets(0, 31, 4, 24);
			getDialupSettingsPanel().add(getPhoneNumberTextField(), constraintsPhoneNumberTextField);

			java.awt.GridBagConstraints constraintsPhoneNumberLabel = new java.awt.GridBagConstraints();
			constraintsPhoneNumberLabel.gridx = 1; constraintsPhoneNumberLabel.gridy = 1;
			constraintsPhoneNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPhoneNumberLabel.insets = new java.awt.Insets(1, 22, 7, 30);
			getDialupSettingsPanel().add(getPhoneNumberLabel(), constraintsPhoneNumberLabel);

			java.awt.GridBagConstraints constraintsJButtonAdvanced = new java.awt.GridBagConstraints();
			constraintsJButtonAdvanced.gridx = 2; constraintsJButtonAdvanced.gridy = 2;
			constraintsJButtonAdvanced.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonAdvanced.ipadx = 28;
			constraintsJButtonAdvanced.insets = new java.awt.Insets(5, 73, 12, 24);
			getDialupSettingsPanel().add(getJButtonAdvanced(), constraintsJButtonAdvanced);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDialupSettingsPanel;
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
			constraintsTypeTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTypeTextField.ipadx = 233;
			constraintsTypeTextField.ipady = 20;
			constraintsTypeTextField.insets = new java.awt.Insets(1, 2, 1, 47);
			getIdentificationPanel().add(getTypeTextField(), constraintsTypeTextField);

			java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
			constraintsTypeLabel.gridx = 1; constraintsTypeLabel.gridy = 1;
			constraintsTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTypeLabel.insets = new java.awt.Insets(3, 5, 3, 43);
			getIdentificationPanel().add(getTypeLabel(), constraintsTypeLabel);

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 2;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameLabel.insets = new java.awt.Insets(4, 5, 3, 39);
			getIdentificationPanel().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 2;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.ipadx = 101;
			constraintsNameTextField.insets = new java.awt.Insets(2, 2, 1, 47);
			getIdentificationPanel().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsPhysicalAddressLabel = new java.awt.GridBagConstraints();
			constraintsPhysicalAddressLabel.gridx = 1; constraintsPhysicalAddressLabel.gridy = 3;
			constraintsPhysicalAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPhysicalAddressLabel.insets = new java.awt.Insets(4, 5, 3, 14);
			getIdentificationPanel().add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);

			java.awt.GridBagConstraints constraintsPhysicalAddressTextField = new java.awt.GridBagConstraints();
			constraintsPhysicalAddressTextField.gridx = 2; constraintsPhysicalAddressTextField.gridy = 3;
			constraintsPhysicalAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPhysicalAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPhysicalAddressTextField.weightx = 1.0;
			constraintsPhysicalAddressTextField.ipadx = 74;
			constraintsPhysicalAddressTextField.insets = new java.awt.Insets(2, 2, 1, 173);
			getIdentificationPanel().add(getPhysicalAddressTextField(), constraintsPhysicalAddressTextField);

			java.awt.GridBagConstraints constraintsDisableFlagCheckBox = new java.awt.GridBagConstraints();
			constraintsDisableFlagCheckBox.gridx = 1; constraintsDisableFlagCheckBox.gridy = 4;
			constraintsDisableFlagCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDisableFlagCheckBox.ipadx = 4;
			constraintsDisableFlagCheckBox.ipady = -9;
			constraintsDisableFlagCheckBox.insets = new java.awt.Insets(2, 5, 9, 1);
			getIdentificationPanel().add(getDisableFlagCheckBox(), constraintsDisableFlagCheckBox);

			java.awt.GridBagConstraints constraintsControlInhibitCheckBox = new java.awt.GridBagConstraints();
			constraintsControlInhibitCheckBox.gridx = 2; constraintsControlInhibitCheckBox.gridy = 4;
			constraintsControlInhibitCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlInhibitCheckBox.ipadx = 38;
			constraintsControlInhibitCheckBox.ipady = -9;
			constraintsControlInhibitCheckBox.insets = new java.awt.Insets(2, 2, 9, 118);
			getIdentificationPanel().add(getControlInhibitCheckBox(), constraintsControlInhibitCheckBox);
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
 * Return the JButtonAdvanced property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAdvanced() {
	if (ivjJButtonAdvanced == null) {
		try {
			ivjJButtonAdvanced = new javax.swing.JButton();
			ivjJButtonAdvanced.setName("JButtonAdvanced");
			ivjJButtonAdvanced.setText("Advanced...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAdvanced;
}
/**
 * Return the JComboBoxAmpUseType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxAmpUseType() {
	if (ivjJComboBoxAmpUseType == null) {
		try {
			ivjJComboBoxAmpUseType = new javax.swing.JComboBox();
			ivjJComboBoxAmpUseType.setName("JComboBoxAmpUseType");
			ivjJComboBoxAmpUseType.setMaximumSize(new java.awt.Dimension(32767, 25));
			ivjJComboBoxAmpUseType.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjJComboBoxAmpUseType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxAmpUseType.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}

			//CCU-710 has only Amp1 & Amp2
			ivjJComboBoxAmpUseType.addItem( com.cannontech.database.db.device.DeviceIDLCRemote.AMPUSE_AMP1 );
			ivjJComboBoxAmpUseType.addItem( com.cannontech.database.db.device.DeviceIDLCRemote.AMPUSE_AMP2 );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxAmpUseType;
}
/**
 * Return the JLabelCCUAmpUseType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCCUAmpUseType() {
	if (ivjJLabelCCUAmpUseType == null) {
		try {
			ivjJLabelCCUAmpUseType = new javax.swing.JLabel();
			ivjJLabelCCUAmpUseType.setName("JLabelCCUAmpUseType");
			ivjJLabelCCUAmpUseType.setText("CCU Amp Use Type:");
			ivjJLabelCCUAmpUseType.setMaximumSize(new java.awt.Dimension(138, 16));
			ivjJLabelCCUAmpUseType.setPreferredSize(new java.awt.Dimension(138, 16));
			ivjJLabelCCUAmpUseType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCCUAmpUseType.setMinimumSize(new java.awt.Dimension(138, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCCUAmpUseType;
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
			ivjNameTextField.setColumns(12);
			ivjNameTextField.setPreferredSize(new java.awt.Dimension(132, 20));
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setMinimumSize(new java.awt.Dimension(132, 20));
			// user code begin {1}

			ivjNameTextField.setDocument(
					new TextFieldDocument(
					TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
					TextFieldDocument.INVALID_CHARS_PAO));

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
 * Return the PasswordLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPasswordLabel() {
	if (ivjPasswordLabel == null) {
		try {
			ivjPasswordLabel = new javax.swing.JLabel();
			ivjPasswordLabel.setName("PasswordLabel");
			ivjPasswordLabel.setText("Password:");
			ivjPasswordLabel.setMaximumSize(new java.awt.Dimension(138, 20));
			ivjPasswordLabel.setPreferredSize(new java.awt.Dimension(138, 20));
			ivjPasswordLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPasswordLabel.setMinimumSize(new java.awt.Dimension(138, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordLabel;
}
/**
 * Return the PhysicalAddressTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPasswordTextField() {
	if (ivjPasswordTextField == null) {
		try {
			ivjPasswordTextField = new javax.swing.JTextField();
			ivjPasswordTextField.setName("PasswordTextField");
			ivjPasswordTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjPasswordTextField.setColumns(0);
			ivjPasswordTextField.setPreferredSize(new java.awt.Dimension(120, 20));
			ivjPasswordTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPasswordTextField.setMinimumSize(new java.awt.Dimension(120, 20));
			// user code begin {1}
			
			ivjPasswordTextField.setDocument(
				new TextFieldDocument(TextFieldDocument.MAX_IED_PASSWORD_LENGTH));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordTextField;
}
/**
 * Return the PhoneNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPhoneNumberLabel() {
	if (ivjPhoneNumberLabel == null) {
		try {
			ivjPhoneNumberLabel = new javax.swing.JLabel();
			ivjPhoneNumberLabel.setName("PhoneNumberLabel");
			ivjPhoneNumberLabel.setText("Phone Number:");
			ivjPhoneNumberLabel.setMaximumSize(new java.awt.Dimension(98, 16));
			ivjPhoneNumberLabel.setVisible(true);
			ivjPhoneNumberLabel.setPreferredSize(new java.awt.Dimension(98, 16));
			ivjPhoneNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhoneNumberLabel.setEnabled(true);
			ivjPhoneNumberLabel.setMinimumSize(new java.awt.Dimension(98, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhoneNumberLabel;
}
/**
 * Return the PhoneNumberTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPhoneNumberTextField() {
	if (ivjPhoneNumberTextField == null) {
		try {
			ivjPhoneNumberTextField = new javax.swing.JTextField();
			ivjPhoneNumberTextField.setName("PhoneNumberTextField");
			ivjPhoneNumberTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjPhoneNumberTextField.setVisible(true);
			ivjPhoneNumberTextField.setColumns(13);
			ivjPhoneNumberTextField.setPreferredSize(new java.awt.Dimension(12, 20));
			ivjPhoneNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPhoneNumberTextField.setEnabled(true);
			ivjPhoneNumberTextField.setMinimumSize(new java.awt.Dimension(12, 20));
			// user code begin {1}
			
			ivjPhoneNumberTextField.setDocument(
				new TextFieldDocument(TextFieldDocument.MAX_PHONE_NUMBER_LENGTH));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhoneNumberTextField;
}
/**
 * Return the PhysicalAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPhysicalAddressLabel() {
	if (ivjPhysicalAddressLabel == null) {
		try {
			ivjPhysicalAddressLabel = new javax.swing.JLabel();
			ivjPhysicalAddressLabel.setName("PhysicalAddressLabel");
			ivjPhysicalAddressLabel.setText("Physical Address:");
			ivjPhysicalAddressLabel.setMaximumSize(new java.awt.Dimension(112, 16));
			ivjPhysicalAddressLabel.setPreferredSize(new java.awt.Dimension(112, 16));
			ivjPhysicalAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhysicalAddressLabel.setMinimumSize(new java.awt.Dimension(112, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalAddressLabel;
}
/**
 * Return the PhysicalAddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPhysicalAddressTextField() {
	if (ivjPhysicalAddressTextField == null) {
		try {
			ivjPhysicalAddressTextField = new javax.swing.JTextField();
			ivjPhysicalAddressTextField.setName("PhysicalAddressTextField");
			ivjPhysicalAddressTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjPhysicalAddressTextField.setColumns(10);
			ivjPhysicalAddressTextField.setPreferredSize(new java.awt.Dimension(33, 20));
			ivjPhysicalAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPhysicalAddressTextField.setMinimumSize(new java.awt.Dimension(33, 20));
			// user code begin {1}

			ivjPhysicalAddressTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(-9999999999L, 9999999999L) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalAddressTextField;
}
/**
 * Return the PortComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPortComboBox() {
	if (ivjPortComboBox == null) {
		try {
			ivjPortComboBox = new javax.swing.JComboBox();
			ivjPortComboBox.setName("PortComboBox");
			ivjPortComboBox.setMaximumSize(new java.awt.Dimension(32767, 25));
			ivjPortComboBox.setPreferredSize(new java.awt.Dimension(210, 25));
			ivjPortComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPortComboBox.setMinimumSize(new java.awt.Dimension(210, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPortComboBox;
}
/**
 * Return the PortLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPortLabel() {
	if (ivjPortLabel == null) {
		try {
			ivjPortLabel = new javax.swing.JLabel();
			ivjPortLabel.setName("PortLabel");
			ivjPortLabel.setText("Communication Channel:");
			ivjPortLabel.setMaximumSize(new java.awt.Dimension(138, 16));
			ivjPortLabel.setPreferredSize(new java.awt.Dimension(138, 16));
			ivjPortLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPortLabel.setMinimumSize(new java.awt.Dimension(138, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPortLabel;
}
/**
 * Return the PostCommWaitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPostCommWaitLabel() {
	if (ivjPostCommWaitLabel == null) {
		try {
			ivjPostCommWaitLabel = new javax.swing.JLabel();
			ivjPostCommWaitLabel.setName("PostCommWaitLabel");
			ivjPostCommWaitLabel.setText("Post Communication Wait:");
			ivjPostCommWaitLabel.setMaximumSize(new java.awt.Dimension(172, 16));
			ivjPostCommWaitLabel.setPreferredSize(new java.awt.Dimension(172, 16));
			ivjPostCommWaitLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPostCommWaitLabel.setMinimumSize(new java.awt.Dimension(172, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPostCommWaitLabel;
}
/**
 * Return the PostCommWaitSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPostCommWaitSpinner() {
	if (ivjPostCommWaitSpinner == null) {
		try {
			ivjPostCommWaitSpinner = new com.klg.jclass.field.JCSpinField();
			ivjPostCommWaitSpinner.setName("PostCommWaitSpinner");
			ivjPostCommWaitSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjPostCommWaitSpinner.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPostCommWaitSpinner.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjPostCommWaitSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjPostCommWaitSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(1000000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPostCommWaitSpinner;
}
/**
 * Return the RouteComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getRouteComboBox() {
	if (ivjRouteComboBox == null) {
		try {
			ivjRouteComboBox = new javax.swing.JComboBox();
			ivjRouteComboBox.setName("RouteComboBox");
			ivjRouteComboBox.setPreferredSize(new java.awt.Dimension(210, 25));
			ivjRouteComboBox.setMinimumSize(new java.awt.Dimension(210, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteComboBox;
}
/**
 * Return the CommPathLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRouteLabel() {
	if (ivjRouteLabel == null) {
		try {
			ivjRouteLabel = new javax.swing.JLabel();
			ivjRouteLabel.setName("RouteLabel");
			ivjRouteLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRouteLabel.setText("Communication Route:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteLabel;
}
/**
 * Return the SlaveAddressComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getSlaveAddressComboBox() {
	if (ivjSlaveAddressComboBox == null) {
		try {
			ivjSlaveAddressComboBox = new javax.swing.JComboBox();
			ivjSlaveAddressComboBox.setName("SlaveAddressComboBox");
			ivjSlaveAddressComboBox.setMaximumSize(new java.awt.Dimension(32767, 25));
			ivjSlaveAddressComboBox.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjSlaveAddressComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSlaveAddressComboBox.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}

			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_STAND_ALONE );
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_MASTER );
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_SLAVE1 );
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_SLAVE2 );
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_SLAVE3 );
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_SLAVE4 );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSlaveAddressComboBox;
}
/**
 * Return the SlaveAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSlaveAddressLabel() {
	if (ivjSlaveAddressLabel == null) {
		try {
			ivjSlaveAddressLabel = new javax.swing.JLabel();
			ivjSlaveAddressLabel.setName("SlaveAddressLabel");
			ivjSlaveAddressLabel.setText("Slave Address:");
			ivjSlaveAddressLabel.setMaximumSize(new java.awt.Dimension(138, 16));
			ivjSlaveAddressLabel.setPreferredSize(new java.awt.Dimension(138, 16));
			ivjSlaveAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSlaveAddressLabel.setMinimumSize(new java.awt.Dimension(138, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSlaveAddressLabel;
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
			ivjTypeLabel.setMaximumSize(new java.awt.Dimension(83, 20));
			ivjTypeLabel.setPreferredSize(new java.awt.Dimension(83, 20));
			ivjTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTypeLabel.setMinimumSize(new java.awt.Dimension(83, 20));
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
 * Return the DeviceTypeTextField property value.
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
	com.cannontech.database.data.device.DeviceBase d = (com.cannontech.database.data.device.DeviceBase)val;

	d.setPAOName( getNameTextField().getText() );
   int devType = PAOGroups.getDeviceType( d.getPAOType() );

	if( getDisableFlagCheckBox().isSelected() )
		d.setDisableFlag( new Character('Y') );
	else
		d.setDisableFlag( new Character('N') );

	if( getControlInhibitCheckBox().isSelected() )
		d.getDevice().setControlInhibit( new Character( 'Y' ) );
	else
		d.getDevice().setControlInhibit( new Character( 'N' ) );

	//This is a little bit ugly
	//The address could be coming from three distinct
	//types of devices - yet all devices have an address
	//eeck.
	if( getPhysicalAddressTextField().isVisible() )
	{
		try
		{
			Integer address = new Integer( getPhysicalAddressTextField().getText() );

			if( val instanceof com.cannontech.database.data.device.CarrierBase )
			{

				if( devType == PAOGroups.REPEATER ) //val instanceof Repeater900
				{
					((CarrierBase) val).getDeviceCarrierSettings().setAddress( new Integer(address.intValue() + 4190000) );
				}
				else
				{
					((CarrierBase) val).getDeviceCarrierSettings().setAddress( address );
				}
			}
			else if( val instanceof com.cannontech.database.data.device.IDLCBase )
				((com.cannontech.database.data.device.IDLCBase) val).getDeviceIDLCRemote().setAddress(address);
		}
		catch(NumberFormatException n )
		{
			com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
		}
	}

	if( val instanceof RemoteBase )
	{
		DeviceDirectCommSettings dDirect = ((RemoteBase) val).getDeviceDirectCommSettings();

		Integer portID = null;
		Integer address = null;
		Integer postCommWait = null;

		com.cannontech.database.data.lite.LiteYukonPAObject port = ((com.cannontech.database.data.lite.LiteYukonPAObject)getPortComboBox().getSelectedItem());

		portID = new Integer(port.getYukonID());
		dDirect.setPortID( portID );

		Object postCommWaitSpinVal = getPostCommWaitSpinner().getValue();
		if( postCommWaitSpinVal instanceof Long )
			postCommWait = new Integer( ((Long)postCommWaitSpinVal).intValue() );
		else if( postCommWaitSpinVal instanceof Integer )
			postCommWait = new Integer( ((Integer)postCommWaitSpinVal).intValue() );

		if( val instanceof IDLCBase )
		{
			((IDLCBase)val).getDeviceIDLCRemote().setPostCommWait( postCommWait );
			((IDLCBase)val).getDeviceIDLCRemote().setCcuAmpUseType( getJComboBoxAmpUseType().getSelectedItem().toString() );
		}
		
		if( PAOGroups.isDialupPort(port.getType()) )
		{
			DeviceDialupSettings dDialup = ((RemoteBase) val).getDeviceDialupSettings();

			getAdvancedPanel().getValue( dDialup );
			
			dDialup.setPhoneNumber( getPhoneNumberTextField().getText().trim() );
			if( val instanceof PagingTapTerminal )
				dDialup.setLineSettings( "7E1" );
			else
				dDialup.setLineSettings( "8N1" );
		}
		else
			((RemoteBase)val).getDeviceDialupSettings().setPhoneNumber(null);

      if( val instanceof DNPBase ) //DeviceTypesFuncs.hasMasterAddress(devType) ) 
      {
         DNPBase dnp = (DNPBase)val;
         try
         {
            dnp.getDeviceDNP().setMasterAddress( new Integer(getPhysicalAddressTextField().getText()) );
         }
         catch( NumberFormatException e )
         {
            dnp.getDeviceDNP().setMasterAddress( new Integer(0) );
         }
            
         try
         {         
            dnp.getDeviceDNP().setSlaveAddress( new Integer(getSlaveAddressComboBox().getSelectedItem().toString() ) );
         }
         catch( NumberFormatException e )
         {
            dnp.getDeviceDNP().setSlaveAddress( new Integer(0) );
         }
   
         try
         {
            dnp.getDeviceDNP().setPostCommWait( new Integer(getPostCommWaitSpinner().getValue().toString()) );
         }
         catch( NumberFormatException e )
         {
            dnp.getDeviceDNP().setPostCommWait( new Integer(0) );
         }
   
      }
		else if( val instanceof IEDBase )
		{
			String password = getPasswordTextField().getText();
			if( password.length() > 0 )
				((IEDBase)val).getDeviceIED().setPassword(password);
			else
				((IEDBase)val).getDeviceIED().setPassword("0");
				
			if( getSlaveAddressComboBox().isVisible() )
			{
				String slaveAddress = null;

				/**** START SUPER HACK ****/
				if( getSlaveAddressComboBox().isEditable() )
					slaveAddress = getSlaveAddressComboBox().getEditor().getItem().toString();
				else /**** END SUPER HACK ****/
					slaveAddress = new String( getSlaveAddressComboBox().getSelectedItem() != null ?
							  getSlaveAddressComboBox().getSelectedItem().toString() : "" );

	
				((IEDBase)val).getDeviceIED().setSlaveAddress(slaveAddress);
			}
		}

	}
	else
	{
		if( val instanceof CarrierBase )
			((CarrierBase) val).getDeviceRoutes().setRouteID( new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRouteComboBox().getSelectedItem()).getYukonID()) );
			
	}

	return val;
}
/**
 * Return the WaitLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getWaitLabel() {
	if (ivjWaitLabel == null) {
		try {
			ivjWaitLabel = new javax.swing.JLabel();
			ivjWaitLabel.setName("WaitLabel");
			ivjWaitLabel.setText("sec");
			ivjWaitLabel.setMaximumSize(new java.awt.Dimension(65, 16));
			ivjWaitLabel.setPreferredSize(new java.awt.Dimension(65, 16));
			ivjWaitLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjWaitLabel.setMinimumSize(new java.awt.Dimension(65, 16));
			// user code begin {1}
         
         ivjWaitLabel.setText("(msec.)");
         
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjWaitLabel;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	
	getPostCommWaitSpinner().addValueListener(this);
	
	// user code end
	getNameTextField().addCaretListener(this);
	getPhysicalAddressTextField().addCaretListener(this);
	getDisableFlagCheckBox().addActionListener(this);
	getControlInhibitCheckBox().addActionListener(this);
	getRouteComboBox().addActionListener(this);
	getPortComboBox().addActionListener(this);
	getPhoneNumberTextField().addCaretListener(this);
	getPasswordTextField().addCaretListener(this);
	getSlaveAddressComboBox().addActionListener(this);
	getJButtonAdvanced().addActionListener(this);
	getJComboBoxAmpUseType().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceBaseEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(432, 416);
		setMinimumSize(new java.awt.Dimension(509, 472));

		java.awt.GridBagConstraints constraintsIdentificationPanel = new java.awt.GridBagConstraints();
		constraintsIdentificationPanel.gridx = 1; constraintsIdentificationPanel.gridy = 1;
		constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsIdentificationPanel.weightx = 1.0;
		constraintsIdentificationPanel.weighty = 1.0;
		constraintsIdentificationPanel.ipadx = -1;
		constraintsIdentificationPanel.ipady = -2;
		constraintsIdentificationPanel.insets = new java.awt.Insets(14, 6, 2, 4);
		add(getIdentificationPanel(), constraintsIdentificationPanel);

		java.awt.GridBagConstraints constraintsCommunicationPanel = new java.awt.GridBagConstraints();
		constraintsCommunicationPanel.gridx = 1; constraintsCommunicationPanel.gridy = 2;
		constraintsCommunicationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCommunicationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCommunicationPanel.weightx = 1.0;
		constraintsCommunicationPanel.weighty = 1.0;
		constraintsCommunicationPanel.ipadx = -10;
		constraintsCommunicationPanel.ipady = 7;
		constraintsCommunicationPanel.insets = new java.awt.Insets(3, 6, 11, 4);
		add(getCommunicationPanel(), constraintsCommunicationPanel);
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
	if( getNameTextField().getText() == null
		 || getNameTextField().getText().length() < 1 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	int address = -1;
	
	if( getPhysicalAddressTextField().isVisible()
		 && (getPhysicalAddressTextField().getText() == null
		     || getPhysicalAddressTextField().getText().length() < 1) )
	{
		setErrorString("The Address text field must be filled in");
		return false;
	}


	if( getPhysicalAddressTextField().isVisible() )
		address = Integer.parseInt( getPhysicalAddressTextField().getText() );

   	if( !com.cannontech.dbeditor.range.DeviceAddressRange.isValidRange( getDeviceType(), address ) )
   	{
      	setErrorString( com.cannontech.dbeditor.range.DeviceAddressRange.getRangeMessage( getDeviceType() ) );
      	return false;
   	}

   	if( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(getDeviceType()) )
      	return checkMCTAddresses( address );
      
	//verify that there are no duplicate physical address for CCUs or RTUs on a dedicated channel
	com.cannontech.database.data.lite.LiteYukonPAObject port = ((com.cannontech.database.data.lite.LiteYukonPAObject)getPortComboBox().getSelectedItem());
	if(com.cannontech.database.data.device.DeviceTypesFuncs.isCarrier(getDeviceType()) || com.cannontech.database.data.device.DeviceTypesFuncs.isVirtualDevice(getDeviceType()))
		return true;
	else if((! PAOGroups.isDialupPort(port.getType())) && (com.cannontech.database.data.device.DeviceTypesFuncs.isCCU(getDeviceType()) || com.cannontech.database.data.device.DeviceTypesFuncs.isRTU(getDeviceType()) ))
	{
		address = Integer.parseInt( getPhysicalAddressTextField().getText() );
		return checkForDuplicateAddresses(address, port.getLiteID() );   	
	}
	
	return true;
}
/**
 * Comment
 */
public void jButtonAdvanced_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	AdvancedPropertiesDialog dialog = new AdvancedPropertiesDialog( 
						getAdvancedPanel(), "Advanced Dialup Properties");

	int result = dialog.showPanel( com.cannontech.common.util.CtiUtilities.getParentFrame(this) );

	if( result == AdvancedPropertiesDialog.RESPONSE_ACCEPT )
		fireInputUpdate(); //there has been a change!!!!

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		DeviceBaseEditorPanel aDeviceBaseEditorPanel;
		aDeviceBaseEditorPanel = new DeviceBaseEditorPanel();
		frame.add("Center", aDeviceBaseEditorPanel);
		frame.setSize(aDeviceBaseEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 1:58:37 PM)
 */
private void setCarrierBaseValue( CarrierBase cBase )
{
	Integer address = cBase.getDeviceCarrierSettings().getAddress();

	if( cBase instanceof Repeater900 )
		address = new Integer( address.intValue() - 4190000 );
      
   if( cBase instanceof com.cannontech.database.data.device.MCT_Broadcast )
      getPhysicalAddressLabel().setText("Lead Address:");
		
	getPhysicalAddressLabel().setVisible(true);
	getPhysicalAddressTextField().setVisible(true);
	
	getPhysicalAddressTextField().setText( address.toString() );
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 1:58:37 PM)
 */
private void setIDLCBaseValue( IDLCBase idlcBase )
{
	Integer address = idlcBase.getDeviceIDLCRemote().getAddress();
	getPhysicalAddressTextField().setText( address.toString() );
	
	getPhysicalAddressLabel().setVisible(true);
	getPhysicalAddressTextField().setVisible(true);

}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 1:58:37 PM)
 */
private void setNonRemBaseValue( Object base )
{  
   getJLabelCCUAmpUseType().setVisible(false);
   getJComboBoxAmpUseType().setVisible(false);
   getPortLabel().setVisible(false);
   getPortComboBox().setVisible(false);
   getPasswordLabel().setVisible(false);
   getPasswordTextField().setVisible(false);
    
	getRouteLabel().setVisible(true);
	getRouteComboBox().setVisible(true);
   
	getPostCommWaitLabel().setVisible(false);
	getPostCommWaitSpinner().setVisible(false);
	getWaitLabel().setVisible(false);
	getSlaveAddressLabel().setVisible(false);
	getSlaveAddressComboBox().setVisible(false);   

	int assignedRouteID = 0;
	if( getRouteComboBox().getModel().getSize() > 0 )
		getRouteComboBox().removeAllItems();

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List routes = cache.getAllRoutes();
		if( base instanceof CarrierBase )
		{
			int routeType = 0;
			assignedRouteID = ((CarrierBase) base).getDeviceRoutes().getRouteID().intValue();
			
			for( int i = 0 ; i < routes.size(); i++ )
			{
				routeType = ((com.cannontech.database.data.lite.LiteYukonPAObject)routes.get(i)).getType();
				
				if( routeType == com.cannontech.database.data.pao.RouteTypes.ROUTE_CCU ||
						routeType == com.cannontech.database.data.pao.RouteTypes.ROUTE_MACRO )
				{
					getRouteComboBox().addItem( routes.get(i) );
					if( ((com.cannontech.database.data.lite.LiteYukonPAObject)routes.get(i)).getYukonID() == assignedRouteID )
						getRouteComboBox().setSelectedItem((com.cannontech.database.data.lite.LiteYukonPAObject)routes.get(i));
				}
			}
		}
		else
		{
			if( base instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon )
				assignedRouteID = ((com.cannontech.database.data.device.lm.LMGroupEmetcon) base).getLmGroupEmetcon().getRouteID().intValue();
			else if( base instanceof com.cannontech.database.data.device.lm.LMGroupVersacom )
				assignedRouteID = ((com.cannontech.database.data.device.lm.LMGroupVersacom) base).getLmGroupVersacom().getRouteID().intValue();
			else if (base instanceof com.cannontech.database.data.device.lm.LMGroupRipple) 
				assignedRouteID = ((com.cannontech.database.data.device.lm.LMGroupRipple)base).getLmGroupRipple().getRouteID().intValue();
				for( int i = 0 ; i < routes.size(); i++ )
			{
				getRouteComboBox().addItem( routes.get(i) );
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)routes.get(i)).getYukonID() == assignedRouteID )
					getRouteComboBox().setSelectedItem((com.cannontech.database.data.lite.LiteYukonPAObject)routes.get(i));
			}
		}
	}
   
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 1:58:37 PM)
 */
private void setRemoteBaseValue( RemoteBase rBase, int intType )
{
	getRouteLabel().setVisible(false);
	getRouteComboBox().setVisible(false);
	getJLabelCCUAmpUseType().setVisible(false);
	getJComboBoxAmpUseType().setVisible(false);

	getPortLabel().setVisible(true);
	getPortComboBox().setVisible(true);
	getPostCommWaitLabel().setVisible(true);
	getPostCommWaitSpinner().setVisible(true);
	getWaitLabel().setVisible(true);

	if( getRouteComboBox().getModel().getSize() > 0 )
		getRouteComboBox().removeAllItems();

	int portID = rBase.getDeviceDirectCommSettings().getPortID().intValue();
	//Load the combo box
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List ports = cache.getAllPorts();
		if( getPortComboBox().getModel().getSize() > 0 )
			getPortComboBox().removeAllItems();
			
		com.cannontech.database.data.lite.LiteYukonPAObject litePort = null;
		for( int i = 0; i < ports.size(); i++ )
		{
			litePort = (com.cannontech.database.data.lite.LiteYukonPAObject)ports.get(i);
			getPortComboBox().addItem(litePort);
			
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)ports.get(i)).getYukonID() == portID )
			{
				getPortComboBox().setSelectedItem(litePort);
				
				if( com.cannontech.database.data.pao.PAOGroups.isDialupPort(litePort.getType()) )
					getDialupSettingsPanel().setVisible(true);
			}
		}
	}

	Integer postCommWait = null;
	String ampUse = null;
	if( rBase instanceof IDLCBase )
	{
		postCommWait = ((IDLCBase)rBase).getDeviceIDLCRemote().getPostCommWait();

		//only show CCUAmpUse when its a CCU-711 or CCU-710A
		if( com.cannontech.database.data.pao.PAOGroups.getPAOType( rBase.getPAOCategory(), rBase.getPAOType() )
			 == PAOGroups.CCU711
			 || com.cannontech.database.data.pao.PAOGroups.getPAOType( rBase.getPAOCategory(), rBase.getPAOType() )
			 == PAOGroups.CCU710A )
		{
			ampUse = ((IDLCBase)rBase).getDeviceIDLCRemote().getCcuAmpUseType();
			getJLabelCCUAmpUseType().setVisible(true);
			getJComboBoxAmpUseType().setVisible(true);

			//add the extra options for CCU-711's only!
			if( com.cannontech.database.data.pao.PAOGroups.getPAOType( rBase.getPAOCategory(), rBase.getPAOType() )
			 	 == PAOGroups.CCU711 )
			{
				getJComboBoxAmpUseType().addItem( com.cannontech.database.db.device.DeviceIDLCRemote.AMPUSE_ALTERNATING );
				getJComboBoxAmpUseType().addItem( com.cannontech.database.db.device.DeviceIDLCRemote.AMPUSE_DEF_1_FAIL_2 );
				getJComboBoxAmpUseType().addItem( com.cannontech.database.db.device.DeviceIDLCRemote.AMPUSE_DEF_2_FAIL_1 );
				getJComboBoxAmpUseType().addItem( com.cannontech.database.db.device.DeviceIDLCRemote.AMPUSE_ALT_FAILOVER );				
			}
			
		}
		
	}
	

	//regardless of our type, we shuld set the advanced settings of the port
	getAdvancedPanel().setValue( rBase );
	
	if( getDialupSettingsPanel().isVisible() )
	{
		DeviceDialupSettings dDialup = rBase.getDeviceDialupSettings();

		if( dDialup != null )
		{
		
			String phoneNumber = dDialup.getPhoneNumber();

			if( phoneNumber != null )
				getPhoneNumberTextField().setText( phoneNumber );
		}
	}

	if( rBase instanceof IEDBase )
	{
		//do not show the PostCommWait Items
		getPostCommWaitLabel().setVisible(false);
		getPostCommWaitSpinner().setVisible(false);
		getWaitLabel().setVisible(false);
		
		getPasswordLabel().setVisible(true);
		getPasswordTextField().setVisible(true);
		String password = ((IEDBase)rBase).getDeviceIED().getPassword();
      
		if( CtiUtilities.STRING_NONE.equalsIgnoreCase(password)
          || "None".equalsIgnoreCase(password) //keep the old (none) value valid
          || "0".equalsIgnoreCase(password) )  //keep the old '0' value valid
      {
			getPasswordTextField().setText( "" );
      }
		else
			getPasswordTextField().setText( password );


		if( rBase instanceof Schlumberger 
			 || intType == PAOGroups.ALPHA_PPLUS )
		{
			getSlaveAddressLabel().setVisible(true);
			getSlaveAddressComboBox().setVisible(true);

			String slaveAddress = ((IEDBase)rBase).getDeviceIED().getSlaveAddress();
			getSlaveAddressComboBox().setSelectedItem(slaveAddress);
		}
		else if( rBase instanceof Sixnet )
		{
			/**** BEGIN SUPER HACK --- Special case for Sixnet Devices!! ****/
			getSlaveAddressLabel().setText("Station Address:");
			getSlaveAddressLabel().setVisible(true);
			getSlaveAddressComboBox().setVisible(true);
			getSlaveAddressComboBox().setEditable(true);
			getSlaveAddressComboBox().removeAllItems();				

			com.cannontech.common.gui.util.JTextFieldComboEditor e = new com.cannontech.common.gui.util.JTextFieldComboEditor();
			e.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 16000) );
			e.addCaretListener(this);
			getSlaveAddressComboBox().setEditor( e );
			

			String slaveAddress = ((IEDBase)rBase).getDeviceIED().getSlaveAddress();
			getSlaveAddressComboBox().addItem(slaveAddress);
			getSlaveAddressComboBox().setSelectedItem(slaveAddress);

			getPasswordLabel().setText("Log File:");
			/**** END SUPER HACK --- Special case for Sixnet Devices!! ****/
		}
		else
		{
			getSlaveAddressLabel().setVisible(false);
			getSlaveAddressComboBox().setVisible(false);
		}
	}
   else if( rBase instanceof DNPBase )
   {
      getPhysicalAddressLabel().setVisible(true);
      getPhysicalAddressLabel().setText("Master Address:");
      getPhysicalAddressTextField().setVisible(true);
      getPhysicalAddressTextField().setText( ((DNPBase)rBase).getDeviceDNP().getMasterAddress().toString() );
      
      getSlaveAddressLabel().setVisible(true);
      getSlaveAddressComboBox().setVisible(true);
      
      //create a new editor for our combobox so we can set the document
      getSlaveAddressComboBox().setEditable( true );
      getSlaveAddressComboBox().removeAllItems();
      com.cannontech.common.gui.util.JTextFieldComboEditor editor = new com.cannontech.common.gui.util.JTextFieldComboEditor();
      editor.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(-999999999, 999999999) );
      editor.addCaretListener(this);  //be sure to fireInputUpdate() messages!

      getSlaveAddressComboBox().setEditor( editor );
      getSlaveAddressComboBox().addItem( ((DNPBase)rBase).getDeviceDNP().getSlaveAddress() );

      
      getPostCommWaitSpinner().setValue( ((DNPBase)rBase).getDeviceDNP().getPostCommWait() );
      
      getPasswordLabel().setVisible(false);
      getPasswordTextField().setVisible(false);
   }
	else
	{
		getPasswordLabel().setVisible(false);
		getPasswordTextField().setVisible(false);
		getSlaveAddressLabel().setVisible(false);
		getSlaveAddressComboBox().setVisible(false);
	}

   
   if( postCommWait != null )
      getPostCommWaitSpinner().setValue( postCommWait );

   if( ampUse != null )
      getJComboBoxAmpUseType().setSelectedItem( ampUse );
   
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)  
{
	DeviceBase d = (DeviceBase)val;

	String name = d.getPAOName();
	paoID = d.getPAObjectID().intValue();
	
	deviceType = com.cannontech.database.data.pao.PAOGroups.getDeviceType( d.getPAOType() );
	String type = null;
	
	if (deviceType == PAOGroups.TAPTERMINAL)
		type = PAOGroups.STRING_TAP_TERMINAL[2];
	else
		type = d.getPAOType();

	Character disableFlag = d.getPAODisableFlag();
	Character controlInhibit = d.getDevice().getControlInhibit();

	getNameTextField().setText(name);
	getTypeTextField().setText(type);
	CtiUtilities.setCheckBoxState(getDisableFlagCheckBox(), disableFlag);
	CtiUtilities.setCheckBoxState( getControlInhibitCheckBox(), controlInhibit );

	//This is a bit ugly
	//The address could come from one of three differnet types of
	//devices even though they all have one
	//Note also getValue(DBPersistent)
	
	if( val instanceof com.cannontech.database.data.device.CarrierBase )
	{
		setCarrierBaseValue( (com.cannontech.database.data.device.CarrierBase) val );
	}
	else if( val instanceof com.cannontech.database.data.device.IDLCBase )
	{
		setIDLCBaseValue( (com.cannontech.database.data.device.IDLCBase) val );
	}
	else
	{
		if( d.getPAOClass().equalsIgnoreCase(DeviceClasses.STRING_CLASS_VIRTUAL) )
			getCommunicationPanel().setVisible( false );

		getPhysicalAddressLabel().setVisible(false);
		getPhysicalAddressTextField().setVisible(false);
	}


	if( d.getPAOClass().equalsIgnoreCase(DeviceClasses.STRING_CLASS_GROUP) )
		getDisableFlagCheckBox().setVisible(false);
	else
		getDisableFlagCheckBox().setVisible(true);

	getDialupSettingsPanel().setVisible(false);
	
	if( val instanceof RemoteBase )
	{
		setRemoteBaseValue( (RemoteBase)val, deviceType );		
	}
	else
	{
		setNonRemBaseValue( val );		
	}

}

//verify that there are no duplicate addresses for any CCUs or RTUs on a dedicated Comm Channel
private boolean checkForDuplicateAddresses( int address, int portID )
{
	try
	{
		String[] devices = DeviceIDLCRemote.isAddressUnique( address, new Integer(paoID), portID );

		if( devices != null )
		{
			String devStr = new String();
			for( int i = 0; i < devices.length; i++ )
				devStr += "          " + devices[i] + "\n";
			 	  
			javax.swing.JOptionPane.showMessageDialog(
				this, 
				"The address '" + address + "' is already in use by the following CCUs or RTUs: \n" + devStr + 
				"\nCCUs and/or RTUs cannot have duplicate addresses on a dedicated comm channel.",
				"Address Already Used",
				javax.swing.JOptionPane.WARNING_MESSAGE );

			setErrorString(null);
			return false;
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
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	if (arg1.getSource() == getPostCommWaitSpinner()) 
		fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G71F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8FDCD4D55ABFDBD4541AD9D131AEEDD4D434D1CBC69B1BD4C434D9D9E911D16BEE34D1D1E1592E7B66EE767372C7C0C4C5A123C41B508ADB16E0GC12484C5C52322A4E5DB5C98618223434CB8F301C1A9FF4FB967BE675E39F3E7E05C757D787A799C6FBC5FF31E737CB9FF6F39679C042277421624E79489C27A92C17ED7178E7FDDA388D167FFFC8BE281E2FC36207A77BE04D5C26A6AACF8CE01
	38082577E49788CF18F2018E87BAEDFEFC765B703BD018EBEA7CA97C70A22788623EFA7001AA5FF24EDF02F2F2A8DDD61B8F4F9590A2GA34F8570AC901AEA0BE5FC260CE3C84F900424BE1266DF35A5B2D68FF4BB04E08891FDACFD9CBC17D249E7B5544B693AAE8C9396BC70D146F2120FAACF8C06FE465BA2BF8D422F52537A092CC02AA7619983BA9A82B1BED3B08F103C9B5B0F74FF56E39B2C68B1D92DB62BA4768CD4083DE64966286895074DBDE2C3C38BFDEEB7B9451DB4225DE495AD8383A46F193539
	B4AFC12884FA81453EB092F91170BB8142DC0E3FD7CD70FAB3588BE1400CB6FE7AD123ECC36771DF890B1E30E62E74E2E300195996E1D6EC8C9E7ADB4EBB34BC9E4AA6FECF01385C437159A5901AA0F4C388003016560344A59F433379103443B6B8E833B65B6D5BAD3D0EAFEC3D2285FEB7B4C00C0CFDA376589C3D02404A7A3C5AF29DFDFC85469762B79F73C9C4B516FB64B7578AF3EF79BCA3C747A7F3A3AAB3C28EB3DFC49DD6FC42E44F76AAFB56E177FA69AE7B0020161DF636ADF3090E6C6847B7E52448
	79ACD449AEBBC24A7AEEDA870A61F73ADCBF987EA045ADAA1C5551FD94A7749844A59F413A513982F5E94B1AAA94D435697AA1A43EB9234542F46832E8F5F90C563BA08B1379B65765850A4729F0264B8794A774C2080B9E0C4FE675EBCEAEE933F6EBFC76A4886190E2A14C053090C23E8D6B583A6CCFA6D8475ACD8EE97B105952ABBA8446BBA7FB8DBCC51764B06D31BAA51335C75CEFF218CD07AC22D31053BCCD7B1FC3EEFCA9346E77000C5D22D5F418A4334D8A391B86C5C9F4B8D91AF9592BC89A07C8E3
	BB4C03A28898348B186FDF328BC959181C523776DE13A4AAA9D87C1B34CC06CD7BA1C748GFEB33FE46691FF15037DAD90BA389FDCB9A43F4FC58774968D8DEDB65B11A1FB8731C4883791BF27523EE3GFE875850470B7388168A34E047FCFE1533DA15CF0B390728EFF20CE29B3B4FC87A65A2BB5619D7BA314E34AC3FC6A8FFFC2FEE1D21FD2F1D5515F63BC75B21F9923D987DCFE3270A2F482E5FFECEB6B0998EB799197F457CD770329A4CA7E565827D03A1C4F39F3F14E3E0F5167472D8CB3FF2C08593FB
	950FB3BD1F2CAE177DC9F4A4FD83717BBA884BA02C14E531FC8561ADEE5FBC4AB793E4AE00D0F2946A8B848B04E0883190D2A064BB6233EBA0F4B95077FF2F0E5248433EEA197CFB257CC4F9ABD4BA2C7BAF7B513FB27D0E6A0EFA6728F4DF2E511D687D367CFB15EC4B327FD007138D2C6C12F309EE6230E815DAC43BE86D059FCEB96E05DCE2035CCF7559B84FBC0A5B21DF5B819D091363FF4EA5B619A449E1BEB4A4097BC4C9B2DB7B1578B728AC6750A13B52A5222C0C2544F736916857F87A1C2524CF6831
	D92DA22D1C1CC50EDFCA7BA451A28E0252BBECC3D60961AB73C83BBEA20E4AFDE2E19E19688D1BACC324FFE57322A893713B5DE19E04AACF3AE8A155C17A2B45FCDEC7C2A0E0E4FE978B4FD98E6CFBC6B6E05F33715815C25C779FE4680D1DA15734E4D4BA65791AD369FB18CED94B74F54FDB162B60CE266BE7DF11FA23BBC193304DDF3B1C64578BF2DC9026B8314D1F3D0C44E33FAAB7769DB60B0539147B5A19DFC04AC6BCBAA442F0C4BA704F397F0E652F56446D17FD49624F48A73E34287A7EC60978322C
	86673F9120CF9C04E4A773E709447C41523D4C5385819DA6F9CBF7851FCF47811D84E12124172EE7B31FF78F41BCF448DB3A376BB01D9D5284C1089852CB77BB1EAE956205E1EF69AE2847F486C8D3BF6C4D0E9DDBB0DDA72431FBCD77BACFB7054874AA770686CCB7036485C198E1691AC65469ECDBB15DA44043A1A40C3867476ADB4E77248F57FBE7689FB2D78CC9E6B2C21BA468946CC3126ABD413C1954DD527FE53970FDE0C58B29170DC0F7C03038308D4DDE0E6F8341C74204642B5F757A4E9368125F87
	DCCA9BB21C096F8373D750398444ADF4E11DFFB3DFBDA7E9F6BAEDBDE6BA9D126BEB64F2F59BE3584D4B736059E7F6B825BD50893A30EE5F361C68CB66CBB2AC4F9F3F2E49177D15BF8AE39C04392384B7549629F49F83AC9C428C8819B43F189AB2EF6CB1B9C429027E4F26E1E507CD43A6D705F384BA4C8A5A9BD76CA031BB494FCDF239A4ECDA4DF4942967E54ECD7BF1CEFFD5E113ACF766CDD30546930599FA6FD8C1AB57E5ECBC46FC59F6CC716958346C4C34FF3F11436CAB2AC57BC2A04D2CE3E8DF3FD1
	4F3E5C63709ECE79E6F03E9240BA0EA35FE2271AEF52F14697FD9C7D7249E6A3BCCD34D32AE00FF65151E7F38C0A3D54B9952691C976CCB30D16DDA31747244DA8F78664A90C215C3923FAF26B47D05F26BA64EB83AC10737D21D94D978BF88A045C9FD05F6B6A7EB3FD336A391FA04F0E9FD06E9BB5FAF2A77D007A46F03EB04012B8DFDE3A36DC8AFE04B9560F48E75902FC2D00857C08FCBD12160F480B61FC0686641B89D8568964AB127474ECBC01FC091C2F9D30A04E771B55EA3E3893CC4F549368572DDB
	CF562F2C0DCCDB7A99BC8FEF9F12A4F8AB6B9DA6AFD73D5FB7EBB2EA731E53D6521E188DA1DB5106C520DFCDG5A102EEB3BA5GEDD8FC52B6307924F533761DBEA0C0F99789065F91816E6FA66CDD37FEA751B7B6003D6BAE61788E0A4FD261F29BEFC17BD2A02E75A778FEF622004C01A2011E81A19942C28845E7C03BB9837B58EDAB2A05F15EFF77401861301AAC7B5CA6FC789E3A02788A66E8B27D5795C48FBAC793508F87EA34FE68B8435DEE3B06E6FC2287DF05068E5670313A73C199999F6938276A0E
	9B9A76F6409CF817D9347432B962711D6AB9E264996C5DF696BCD3A0303170GFD6FA5E35A22B3F04C7BE117FA4C0BDDE94CA073A63DB12F7BCC26DF4019CA7FDC4E5740B62DA6FA46C15CDC888BA09484C27D04508EC18A44B2BAF365276315912DF89D369D0035427CED07F62E4DFD7A2E47BA53444A224C034F23AC825D79187FFEE1C47FC58662BB6243A729435CF1F5A8B8CB2FBEFC36FF2DFEFD58B1A0769C59EEF3493AEE16082EE40EDEF6966AFA11C79B1E182EE19EFCDADDA71D25672F42E328C3B857
	E1D75D49695061412755A177ECBDFF75DB3C794BB6F848C67C455ACFEF3DDE7BB1C01EB5E7E37BF9EBBB3107341B3633511E3C7A1333275803CFEBCF6059FABEB552B6CC5AE8A857614E1354E16638BA88673873B1DFFDE5547A0A0CB54510367C9C74D5F3FA2E405693DA4EC1BD9DDBCECE4FG8FBE2D1E91676879EA2A046DB50E6B70E1434969D063412755E1E3101E8E1FCBD8DE0DC12843D3A729C32A875FFA8D9D96245786D66EA3FD34CE9BE0B3922E6B7C5539B2CF8C577597DBCFCE57C88FBE2D3F666B
	7A2B1E7BAB77DC189753755A3368BB1C8568G0E9D2A275F5A000E655819DB88168C7442F3D17F4FF692AC7FBC281B67A13607E261723CD29ECBDA489AA8C933C223A706FC9FF16385A6231A4AD862A35DFF32764A465720826FEBE79794682F1DDDD2203FF6767302D58C07BC54DF8CB07E6A825FEBEBB794682D2D2D23F65E3FE6A54D5B69941DF440A7AF6FF25C8B9299B3BC56583EB48D0A320F8A0549B5CB65B4BBA55B0EAADE577F3C29D83F2E2B5684EE5909EB82ED67611A403FC7493CB39068D088513C
	2EF4552A574635E12626CEA44B7482F8667E54BD2E8468CA8D4666CED105E46E5E82F15D90DC9026C0087AA94E191AD70DBB2F5D6972B12F2DDE6D3DCEB42C562F93CD2BC93F69348DB9FAC4ED396FDEDD4C631459599EC99CA4C5445234D3FE49646897BD6A4D814A4F623C71F72F76D637D879FFDD2BAEFFA3EFE3D79BFD1557AC0D7FD3E4FAA1BC73A7394799002E51E0CC76124DEA5955DC76D6C9CFF62B1CC7177C342B728C045FA190A2B5F26200CE04108AE1119C47E45F6FA63B164BAEBE26A73B60FC46
	3BD1FE561F2F48E9035F5D9024735DE587819D8AA18AC24279EA59BF5429E5578858E7DFD70F6DA969FCECCFB7B41176D4B0B9BE3B8CC253E45E4FEE47F12193B04BE4F75981C087CF5673F9F33DDA76E6EEF71D4F7699B1D93F4F0E85FC16A6AE8568053A327F62A63B1E5BFD8EFD9FA0EDAB7F82347BEB0158ED87BA88C25885FC5E1B6ECB4F98C8B77382C5F6327CFB81BCF327E87A9120ABA13428F0266775DB72D4FAD6D5230F2E502D1798BAB579DBE4BAG1E411A388820E327687968E0031B6C9A145D6E
	D3762CA97A65138278C2CDDC7E05B09F3FD0CF762A2D6A722963F563AF2B7576A15448F934DE68A91B657738DBFEDBF8FE1F7A3425CBA7BF5A5E818F544405G9D29EBCB68E672DD48E29A358DC9B09F39080ED71BF13E9D87BCC99084400BAEE26FA8EC5C3BF9044C197A9C26C168A67759459EA177E242BB4A5FEBA4C89F84A182428C882997615A5054AD0497984D03C3037B4C47C4A10872CE237329F6785DF5B1263D63DB320E3D078CA166BEF30F7C3D1FFC75E171EDBDE4BD188CD4C3D637E85907B1BB55
	E5B29372CD3ED87143020BBDFD48560BDE2163F3EEB0DBAFDA457145942FD161EC9D291762046E063834E01CA37EC85F1FDC174098FB89E20F8D5379A050B19CAB99A158CC20D3B976BE65DD84F47E2508BDD50C73D08347A41A4E8EF45025B8064794FA9F036FAA549F036FAD541F17BDD868FBDE76D82137311369F72F91FD904AF5FE4309BE086258B689FD1040316DC674C19247F257208F52B876EBA37A2071B2448E527C5A016E66581FA454C562586714B708509C3B01E2B11731F2E6587DC654AF1127AB
	12D03FD40E0DF4D08C680290DEEE2328DF89475651F12CB20435A906B572770CF61E2E61985A9146311F9FC3BBA2B976DC9D5A91473111FACFBBA657F3BBF83A69235C8E0E3D5C6CE9C751F634E391479EAF7634A30D5753021F61189469767E5F8D3874B3257D857D4CDBDBBC3319342D501FB19B56F1FCAA4513D5B8EB0BB75339G21B7CE857A6728E79555FD9150452108953EC330F2201BB8765DBB3C8CB876935A4642000E6158BFA9B69368940EF58C91ECA150C517A356CD6BCD9950759CB372BA5C4631
	66EA0205829DFDB97AEC125B7CF28660F3AFD77CB49FFEE77D4253EF45BAD8B9E0CD9A1C7971DEBA0768788573E38E47E7D35C224219FF8FD01C500190177A8B54FFA09D7D51F685E2F55B88568D34443163948B82BA14E3895B50EFD19CFBCDC03FA5F02C321DE0C9C027F1EC349D7DA604A176CD9E7A3708E3F753365A8B7498472E21E5B305509CFB0535FD2093B9F6087226823DD0492F83E564DF09582DB4DD975092479C948B82BA0CE347290CE820E7F16C995AA726G3D00E35FF19939D7717E0A665789
	341DE37905848B84BA18E3C78B11B70CE35FD35E79C067DE0DD8A955258468FA0E25D1DD5A006E6258D62E0B1DE3DF507C6200CE3A9A3FFB7DEEB36E43CD83ECE3B84E992A3A5D3EAD10AFC08D8D3B9D665E6D267EB6BA83E1735C8D349FA37CDD61FC1ECB3FD186839D89A1A19C3FA738BE446F891157CE955A82BE577D061E5CE14C189B4E6A3EF00D72BDA1E268EFB9CC669575A833916225EB8876D03D7A9B9719EFB4E732F5A5963F58AD1E0C011B84F5FC5596F5BC515764465F63963FC4454FECFB708334
	ED510391026556ADDE7787444AB6A54B4F767D9B040D673D11D3FCAD595F7DAD9926657952EBBB8C70340AA3867E912E45BCA8DA49C647325A4AAD65061ABA63A6A6BFECC43DA73BBE2FCEF70F8F0D3FD6FE57141F3167EC104701C7685AC784603157E21D89AF216BE6C0AF60580675D8370A2F433EEF41A851F5G68C088219022A14C02B09FC2D1843CAFC7E05AA547C85ACE20DD90C2A258DE1B18883617066C13A1FBE0D85A432381BAB6E88311C15F992E1B78BADC201C1668B5A06BCB7454268B65B61EBF
	A1DDF08F1DBF7B64F05DEF15DC7624CCD76A244B61F5634286BAAFB739B473726B68B81784F624C4603C7C25E14257E3338C8DDA1DF23AF4BA07A5EBCC4557E33A0891223B5DA176098E0758CBF3956474BBE97AB2C8DB435357EEA67B567AECD6092559CBE5F7C33C85C26275F4DCA8EE99476EA532CDA40481CFB4048DB2BE83FEBFA8A87BAC493E8B7D77114B296C12DF46E717C1E0989B9BBA006E6658C67E6DAB18E383948B87BA0AE3C7A86FDC20D3FF09363C37E6BCDBF2D436145C60EECBA550174836B0
	99CFD3990D00375E00BE15A462D389A67CAC4DF9D4B7896283EEC05D5652F9CBA850911C6F1011149D7BF8CF5E30CC56DEAE070DDFA40FC41EBE1E26BFE4A39B6C5ACDE62BA4AA697FC4534F07340B384CCF299EE511D07E111847640EECDA879D7DE6AB54A35AFF5510291C1CCFA61D7776C2FABB672929A76F0883B6077998549B132559E26E3712FDCD8273713EFADFBE463601BE0ED65597625750C8B6563C3AF871E226CF6FFAC51F842ACF8424090EC43BD65031A49968B40E5DC6317C9B21FF3B913194BA
	67EE813A1DE31F52B9EAG5013EE44BA12794EF8F544177EB1104F97327E2C2F3D279E7B64049B317F5D1303FDF2AA476E4965731B1B303FBA37190C89DDC03BA0CC019081A18EC2B28461D7E09F04FA889D90A488419022A1A47E8A738823F9103D068BGAB0AE27B89EB605981C102C0764384C731FD0151D1EC5FDFE2945B2F4772382DA9D9361F48A732A65AAF2A83310168CA74D76312A63F62F1728F9EA7BED915FF176CB7C176C9348FDEE66B1F382D241C5F72A1E73C71C0EF2F755B9AFA55B8793CB3CE
	7CF2556FCC5986B26E10C63C56879FEB4B2D5B3D0F97A65AB696107AF2B3367D0F5FA17D856BF384217EE67A9E758E696F4AEF4676F3F45C761329FA0E815F141B15BEB69CFEBF8DED67F588CCE6C82D7EF891C5470BA8C89F4B75EBF5690F29E750342910EE81CFFBF12DF75BFF207D7346E97159155378BE4AE1773172DC5A47F6C17C40B434FD4F38630BFA3C8C1966EEFB64B42D6D8B0775EDBFCC5FDDE3A0FD42B46CBFEEE2FDBE50D937A056EE26FDBE500D9CFB64B03EB7F6F3ECFAB3FFEF64D856F6FCC7
	8C62583F846C23C2B9767AE778BE18F68B76978F52F7D6A1BABE5BG21B19A71A7A8CE76A9F7C633FD438CBFA8E857DE891F56FF0DB2A66844798ACC4630078C3DEF35F816425FEF352CDCAE246F64AE50AFB09A6B4D234DA44FE13353CC762933F45D747D1B444770F497ED731C63DDCF5311F6BCA39A472CF994CB82BA05E36FD239853746E7175C0A796DEEA77909D632BD3ED74E6F5F6DA8377DD6643D0AE25DC0DBEE45FAEC4E1BC89B469082FC1137AA75B88EFEEFCDD74F7912A47DF6B91F2DC1C27AA42E
	F72C51538F0F9971FDB17F5718EEC93577763B1E360F92C8DB46530FCA6E6D7782AA3B83623BFF0DF6A7F5CCE46CC77B03FF6D5EFEA3003E9C5A2EE277CCC93F7D26D0595110BE0E6B57DD2D6F231F5334E910EEA1CF7B8F493B6D61EC7D6BB69863EF437441C6F75B73E81E5D90EF390D775B635A3EDCE5FB68ED6E36C781FDB1BC153EEB1ED15FF6094A0E0574B338FE9B8AF14E4C66FC673CC71AB953AB28501F770FC8D5DDA46530GE4EC9154F571A3AFFE8E22FA89B1B07F0AC13DDEF7FA77B3DB47EE0434
	2DBC3DC5636737585C9F6283E2504F5F0C6B67E5AA1B22E25C7D1CG7457EEFE3E510B1F07296CD910FEEE8C76334FF86C751BD85F796AC97215F96C6B9BAF3035508DF44FF96EEF58DAE8BA474BA9DE244265B552BADC83B3C0DC60EFF0DF71E18379BE168AF4566D50B7C22801508141FEBBFE3BB197D58A636C3F6830595B44E151227A9C436C8B7758E3B7DEE0EBBDF76760DAD3746DD789B1E77FB55D5B3E5C405B59D897AEBFD3BBB6D01C5977667B443EC4081B8F21E8FAFCF6B904A6881D53513E1B0B4A
	19FD6490D733A419FA8668A994B4EE3FD99CD1E072F1116978483F546BD185D35F4FC8772223E5BA534DB5DDD96BE37A099BC89D0C84FC8657E76A5A3F43F33F69306CE9F6360CEA83E03B43E6979D52A8C36929AF395DBE3FD3BD77E45FCE5F647B22D3A1FFA1965EAFA0344532B3970CEFFA0967B757C7CB102FB716FDF39D036794190F35530675633D472AC38470464632F6CA68D9709B1A6862B7F9BB4D58C87A120ED13B48B7F78B58A731FAC1625ACC076899BBB5CE764DE87165B22E1797D967EC9F98F5
	1AFB606D3C3757A1BA1D149D630BBD63F9B6180604963313CC9EF6D9CC7DFC7FB146137364BB201FF358ACFB2C8366C3E609A791581905CF0F2957DB674A3E956E40B38D67EC510BAF6271B70E6845177178BB0675629BA11E0DA14FED9438CF268FFFA8F8DD07B5D655545554D7EF2E29ABB798ABB7A1CF71C886BC2B0C38AE127B21125F1A5A773C67D7D5EDE499B13E6C3FABFC8F95FF20655B85CEACABF51A2CCE2768B07715169BAA4B8DD5B5E4ED7ABD5957F4DC227F66EF7344D03FCD24194B7D4BB37958
	3F145C70F32165093F2663F8282D476DF730B69BF60752F6D38FAF125732137E21689EB6BCF11B97D7A8FC2FB87D777DD6097A3E92EDAB2DD4722BB32E771EDFF5351C8F633B27C661FB632D7F5477AC4F57EA951F9755A9797F51B9F13D6A6A953E17A5F28E36D4EEEC86F6683614257BEE0B122E49684B7F46AA9030F9131CB65C68BD4F9F9A14BC17CA9349137125B4AAFCC7BACA75FD59EBB6D9EC7DAAC7B25E1C4F945E557593177971B64557DFB4A9F9BCD567B30F9AF59E76E6054FD6CF56DBCA650E4BA0
	77DC25DC46346DCA5A2851094B306CD4781EEE764FB7976CD2F89FA81E384C3AD642474633B43E8FA8698EB68E4E07E7561DEC8FB4CB634841B4A5F732B12F861E2DF29AB666BD15BB5E18271EEBF7832FF427B26685416FF9B05E2D6476BC72BE99273E0111D764A7B2B0EE541407F172604670AB7BC89F56EEF3C86EE35EEF7A72E59C33E2F8E67FAA0ABB69846E00492C611B5DD728095FE7B7DB4929FF397DD1F91EFC6B7A599888D390BBEE32D16F055AC64760F61353B982F392F75E9962AAD51C67987DFB
	97095FE7B18D0B6A811A47DF3939C8936FEEF341C6225BDE4A34E347374D0376EF1DA219BE487C06CFE9BCB229625958596DB1F612BA90FCA70E2DD60F311744C77078571CFA7131BC3E5C6D4C6EAC390EE55D0571CF6A729773786CEA3D78F29EFF36D1AF3E0947FF219B5FF1974E8D3E5B2334395CF7FC34B9E395B4D6E3AD762B874834D69EE3BF955573634A636999A40D5E98BBF017BCBF3ECB79DE6B5F589E37D7797D4A670A6EBBFC0D49DCF746376A8B052FF568FD6FFC55463A4DF50C13FF937AD2617D
	5657B82411B925DD617BF04DC47BC56FE36F1DDFABFEF8669BA56FC467444B70DE2367FB44DCFE66A962AE56FF4641B371AE76ED0471751BBD5FA3FEB4A3DF5ADD2CCF4D0A03BA96477858FB78C23A5EDA89D8E39CE2AFB663BA1C0BE37F206B2DC1C08747E1FF6C90466B0FC9DF0B5FAC6388DF1C529F2742EF524F5C8AFDB213E120EB078BG976EC63B70FD41E6953F9C9ABCA4BABC5E858A5C63D56F926F513387FB5D4FBE4BFD4C1D3B35ED1078247CEEEC03399DCA79FD3C4D7FB96248B63AE6649892999D
	370D4E992B31FE7C7DDBA57F67859FF333AA235B5C6C32FFA9FC9B5A7D19B3B21E21F6540B65F7FB17125F6E3C096861DF5F707B034A6F8B3ED7E405987D7769CBC652FFD5727C26F5AB799D70BDD72CD44F7F5F69D17822BB7EBB736E46DE454E4322123F59773C58CD2F737B94BE1B513FBEB02ADF611D632B3FE1B2F99F18FFD8617BF24273E66FFEE8B822786120C5493BF2FCBFF01D828615BC2E36AAF92C7153A73F35A93C4F7861132F9D8A5FA77F851FC8CE451E4BA4FFFD425672B651F7B2765B287AFD
	304E5739007FB4485F26CE298C793EA50377BD6A478C7AFB54838C7AFB544FB678BE9BB859607D5E2DCB8C6AFB3758BAE17F9099AB1AE0EC680290GA198C2445D38E63861FDD2C7488D071E6B17BBFB7B49B9B5F22B21047233CB3D5B3A3CF47C3B90571412F713C3B6985686C97E081714127212ECF68AB26C7DD262A30739FFC0E2C9E57C23D2D2CFADE29F05191D0143820F3F2D14740B3A6B20E055DE282FDF9DBAAC76C89D26FE4EF3F8438A355F5972289CF7F483712159EA16205D180F5143997BA02135
	DF3EFFF2975E0519GFEDDF4B76E93FDE19DE94F3E6FDE6AB0CB96F13BBCE5124FC78D6EA27611BCEA62F15F68EBEB699EE62083A10446639E553F15607AFCB44162719C7976F578DDEF63BD1807FD9DC95B853484E19204090D075897240C8882BFAF5A67653CA81D43B09D1EBACE72A0E598FE8F6AF5A7BD9BC35E6F66F2ACE58431268444968CA3961E005F9C9EF511B9DB6E3D71599BA134C26806A0C11884A18CC2B404999012A1AC04107F5B78EC83049A88AD90BAA1D8FE0BF55FDA3E49DBBD81D05E50CB
	9B00036C829378DDABDB303C83A02F185F224EDDDB305C667F96FDFEE9B6C9DBF69F3CC3C1683C8F4BEDEA8E161B9D30087BB00FBF66E039A55F07F99464523BD9E744E7D7C2E81D61EF395DF4CC60F56BB26AD752563AE7204CEB387FC3B8D6466FGD8B4836B730F1BBFD45767A3167E0A43BD24772A68A313DAF2B984D9G10DF48D9BEBD74DB9E798653FDBF66DD40738E61582755DC9E476E607706F4BF00D8A047929E40FA712F63E4DED876A0166B65FBCAFD14AB399F0535DEFEFFD3B516E7A364A1BD08
	325A2A31BCA39F4432386CA0C91B8274A288C5B331BC2FB5E3F9168356BB9373F8450C6599BA9373A8B9CC52A692DE88D98F613C7D7EB1C14071B53219AC74CEE40E49FF733926305DFD1E1C760312EF3BE0701EEFF5FD8DCC882AAAB779D73FD61EA0947BED9494D9B59312457C74175D39825B33D572901623ED487BD8B3BCB47ED8F3026EB592E10CA08BB48C8B9CA26F1A363EBE27484734F3078AB8364759EEF31AC98FAF5FE53AEB3C6F10818F695D6330E5DCBE6D9D9472DD9F14AF7421096D430902F46C
	7B2DC847010407345FF52D941F7510763B6E6BEB313E4E0538260771BB36306E643EC72FB8C93E5DFE73B13BD268D8967E30565EF9940FFAD8EB6F70BA34B796628C0F203DCF9FC779010005BC427C9EA9BF639E71AC8746F74F963DF9E5221CBED5070F04C500974CF20F5B88F47DAC3DBA7462081E0CB6B9ED77ACFD999260C11A38D02023F4E57CF5D8CFC6021CB6490B0CB44005596EF1C5C017E9B0563E1EF7F9EF4773DC6337637FF1694F999739BC670C69237AF3461CD1751C11E1792324EF6F07F13E13
	27DB4531FFF2EC8D4D13E0874C3D52G47CBC673E43CD5A48291AF2577AE0FD02C93672CAA6C1FB2D6BECA7AA2B0F94066C05E2AD13A4F45EC31B03A1626B1DB21EB627EE820BA1A6DA6155E5BB876CFDE366FFA5C7FB3313E2BECDCBE7D3E4BC4799AE7CF2C6FEA1F0DFDD79E1D8B754E563665C20A5BE7EB5BF2E8B63665B108CB1E4D77E3671CDC9FF453C9723D60B79F33AB104E67DA9E555AFBBE455B9F555A3BA6976D6D023838C7515E50E1357C0D0F49F7D5484F36473C75DD3F546DD73A6574128E9F89
	C10007EA622200CEF8CC2F8E7DC3774CFF121CB64D0B8CA19166D4091A331DC0B7A66A4998B0227D6D905F2BC96382FA9204B0954E785E5255ADDACEB7A3D1DF3739004F5744E5BD8EF3374775F45BDE2DA723DCCE5B74383E0C8E4087B4F1E3C0CF5115912FFBE7CB381CB6460B0C1900A7EB6296G1DBBC7CF46393AB2CA643415F374E534G5E290933G9DB0C7EFFC787EB869631C721CDC3E9F2E7A647A2E62F179747B2E2FA9DF3097FB34A1E28E76DD2B683CBFF60E36AD3FCB7199F334ED797203581693
	A12E7E89FE265CFCF2FD506DA74977E73F7918DDD3683BCB6893DAFBFFC67108A73476169E666B9390D71C04767EED37DE3D9ACBE27E1F824F70A44F320881EC2686CF86FA81045CA7951C5D0795D7C85E934DD60B59AA6EE0FFE9C340F37D6752B516926029F9121F3FDECB30D6203B1EC4BD0F10233E324FA4556FB7571E7A354BB5A527DE46CB6B941B7E2C7ABD366E544B3E64144AE0E57ABFF48DEAG4AF30C177336B1FAD69E68B00E2DE17B4601CE6058F1894FE44C6258F3F40D4770D4FCF665D3089DEB
	20E793AE094FEE6158057C9E14CE0E2D277771113BA9AC9C3B115FAF914031E21A0E5CCDB10BE34BDCA8B705E321AE143B10E3FB6AD0EE7E6F903BFA84659AB83666G4A2D615873A3A8B710E3EBC7D0EE8847EE9CC139119CDBDF0BF263B87697A74ACD64D8B7BFFB1F4AB1F667AA11DBBF97B19775F39B503D9CDB1FCD6582BDE9AE364F029C254E3E237AFDD576292F3F537F97E41C172B58F4316AF7C96E2917DDFBCAE530B23D052EC706C0F9C670F21E3D1F1EE3GFAA647B6737A1A4431196C5C756F215E
	7C1E5F0D6242B3D8AD9C9B2B4573D61D9C3B124AA5F7F2D8B87664FE5E96B9764CB01E411A45312FA4141B4231A889654E6758E6A34A4D7A83E24FBAD1EEB1478C3CCD14F36CBD89650EF16C844FEF8A4762F8FE619C7B3883F50961D8B76F4BE6F26C2891E514BC4D6FB92169AA01EE62D8A8455A006E65D8953B8B8968E00EBDCFF509GBA06E35B38EDB3B9D6C173ABC9063EA79931DE3A565A8AF4A7476E27D8AF50E39CE3675127G9D49314B28AEF1C04F62D8B53F17A605E3A5B43F1AE762335B1EC1AC05
	72F683ED67589E1A0E5CF3B246B1F9FCGEC8A47FE203C641E13F00E55F139B19C1BC35311FBCEF27F08983B17298468F20E95D03964CE13FA0EA5D31F12BBCD5AB876AD65A5F71AF4F36CF3AED76258254CAF10AE01E383D4C6925073B9765EC102E5BD8BF572D96CD75FB6ABFD0DC1757B6A03273E5F097DDF10F169E14526ABD43F4B8E1FFA594D27D406DC57DB310F5A78AC9F37DB31DF286758F0862D7FD7C07D62586D99584FF4F3ECCDAD76B39247AE24F79311BB0C02B876F89E76A9219C3BE6AB564D28
	E7F1EFD458AE3226A6112F1FE4CF0200E73FE2683CDCC80179CB8A261D32F689269570BBC3B7F925103FB3AC9F23EB7AF653D1724DD63E4BE28C7701B571FC760F391FA13350B46D905F15221C7FF3BFAFF7A6597AA41F173BC872676C26767E02A9A06331F4F6B78E1335E80B7BFD962B69BCB29452C524605E356BA916G74AC0E5D4D66F3C067BE075867F44ED883F453F33847AD41677DA25AF3B6DAFD6D4FA96FF2CC46EEAAE30C4864F2EB68F89B8DF49C476E2558DC20D3B9662073522267E18E7ABCE2EC
	7CEE833A73F954799F877C59A72D55B968F92D4EAB29AE410007F139ADECEE8EF4924766D0ECA150D9AFA0361B5679CA209BB9361E0E8383C0CBAF204EA3BE4F9DE68A6EF71FE8F58EFFC12BB33B5BAA8A70589730DEDE30C53F3DDCC767492910EE81CFDB1B6D59DE2E4DD45A4B831A737649ECAE148A63F1AA66113A5F3D9DBC3CC4E987DF0CDBC66AB0C5F58F813131B7D5311749FAF644DD56EB74EF794A6739E9795901E7AC954BA01216CB8450519CFB185A108CF4DAAA164B7C7D7E54A52D1EA5AF46E76F
	F4AB1782B65F843C66C57CE6DB142B687AA4BB7B8BF11697D12F6F69989C82F4B847725979D120673E083A5679D507343A663F242DC37B280CE240B73E04F2A328AE6DC0F7F16CC5BA3F8882BA18E37B681CED865009AF71BABEEE5F67CB3F5C17357ACD253A9481EEF8996B5C171A3E17DE1BAC5711DB3C1CF3FF104DCDA10FCE1ECF2B51475D9BD4EEA0241D42533F69F4EF8F056C9CB5444FF8996D5F6F33FCF24631FF614B6C7C06D267F7FAB93F4E6E7A4CCA035FE9285F71EAF73FFC172778A5594B196B05
	F41E5788F93472FCCE78B85B9E424ED3C35AG1E7EA64D196B846A3BC8080FC9C33F6C75F9AF8749E7298F3F2426E97B026AEAFD3FFCCE6DD9G6905F9285F9BC6F73F6C4FD75A220B26AF0734757330DE6FE277A682ED6758AF59BB905091735026AF4735E9398F1B1266E96B7A922ACB8A6073396E719A5DC717AB3AEFE3F7AC3C921FDD768A6A49764CF4G5D4D31B3286E13008EF985F5BFB42E6E4BFC68BE63952D6E6C4E5BC44013DF617528DAFFAC3A0C6A1D7FAA740D2FE25A4D87BD472250950A2DBB69
	1C348D52F73E4A47E20AC9C087F06C0A8C1C4FC5F26CD90A4582BD03E35B685830GE8E1BEE23767A1EFA5477E9F451A00EE1B0F3E0B5D6A4F38263D8F41B5DF711D3CAFE61552CEADEBBF92FC6C452DB8E4337416169B2A5806DC4633F54CFD9F4F11C2A53F2512BF79B17E49E49B237C7B6637143C1C8D443ED2F61651C0764AB414E23A44B5CA3A33C7FC6C312FF7BF07797A5B8ADF616609737D7BDD056F4AE27F4E7B7DE42DE2DFF809124F94BF743EEF1D42F709CB6D97F2A6DC714BCB6B15F42E9ADF7957
	010E1BF97E4B8D8ADF08CF3D5C7968D1CA19AF6F00FFFE39EC0362175BD5E737B3EBA7AEBF7583056F1A91D291CB71203BC1BEC8874EE1E917FD24AAFB1F673949410CAAFE6E3222CC61FB49671EF7F73E73BED678BA7CBCBFBB2DDA714D632AB34DFBFD6EEFF717DF25BA2B3CAA5B7FF69922BA0BFC6FFE7F741F5E20687F072DCABEC7FD1F232FD76B1F7F2942F7296F3368EEFC57B6A9FC8F7929777D4D0A5EE92A3341FF2819387C15AD8A5F5FFD16D71DDBF999D5E70A13FC56B3F73E493B953EA2BF6B594D
	2D0A3D092AB312AB7D507B753D8A5F2D3E4E4457126397C6AE7BAA557944F5FE1D99124B6ACB05FF1F644F59A739FC3FD2ECDF793512D72C9FB6FC2ABA1378212FFB8AB6573A75DB5D7B943E5F791227613BCFF5462DFAC2E52D687A52FE455E651DCABEC6BF741EF3C061BB6873FC98E9934AB92C3F2A64CD72D9375C793ED51D4D7BE84267C9992F2BCB31775A03CABE3B7C507B7EFFABFC4B4665D35AC41A6ADC1EE3DCFB953E56C32A3E43CFFB0F0B0A3D3FD01DFBFB5DE7DF62AE7FEE551937477D60FBF5E0
	22E314BB5F67C7943EEDFE5AEB35A876DEB828641362075E73D46759D69F74FF0CACB82A70DF5BCA76131652033386F6F2164FBD2AD5675F0EE478E8EFDBE47564343F4EF01FEB6FF72A6A232FF9924F07715DB124701D15B7D1BF337433735CF5C89ED6721A3AF562BAE4122BA068FD280502FD59CD45702EB2CFD862F4882BBDF91BA7BF79C46477ADC599C2469EA1FB006ECF5DA3AC99A19BDDDDF6F81BC90270A6702E643CFB68E65464616A064CE72F4E4C90320011ED4E5DA3E4119D36406FE079E4CAB6BB
	49C548D087C9A8407BD3647BC21C12308C8FB9D7D4D4B80F8ABF92930422930B97AFD6A88801204AF2B7F75EFBE56D73B37F305F19A9E46F915EA12B957BE098A3873544DE724771760B8EE268AEE1295DE606C840C1CFA7FDACF1CA239631CDA86A7BC6BCBAA4BAA53137990F0001CEEF513FAD7D89B92975C92B49B9A0911D373689D9D6F897100455AC5AE23276FF42CE14EC254626B7F7D3FD69EBAE68DBC875A539D470DC8A272F7C607D3DA9B119C246AEA14FE2B3756EB275C8B6479EF8339B005974G34
	8117FDDB934DA817AEBC5297F748EEADB71F98DBC18E15F3250F38AE1F34BCF579AE70334BA1FCC2D312C5068A1E22F74700498122C4479E88A6903911477DE43D7461C33B5F6E4A9052E5A3096B73DCF6C9486EB38D1AAD23123014CF94E8392CGC4F2GC47EA4B6A8E750A807D1DCC6454577D7DEF27D5F3E23B294D6C9F80FF993D8BF6997ED031FB47775192DA24FGDC8B592F635913B4951AB42BDD973E357771450720AA348819031515747FAA7A3F117CDFC511AA0AD499413F73851F7F3471AE341956
	9F5EAAC88D28588505A8BDB56F7E2BFE57B8BF8D2C5BA5AC21FF829B4A21C0C9453A2B33A3AFD937ACB8792724186C243638C83E2BD976240D244BED65937254B4G6F0D43CA29DC0FAAC850FC4F3AC46082F50992201026FE0B7EDF6C37FF88573B1437041B750127D9B7FA18753EA42CF03FD409C7139501D19D4ADFBB33D937FBEA0C2CF469E8C8CC45EBCD535DCCA6E946A4E125663EA47778534F4A2DAE9D8D51CA729CF633129C4FFB14274105338F38956EE9E46A44E21D38534A8B2DAEDD1B323C586459
	CEAFB8AD5B69B7AEB7A5733911E4D6C2E6FA45DCC9FD33DC2725D9DDCAE9E1090511270E6A641FE78DBCB933CEE98D6CF36FD23186E66868CD52106FA2E44D019460127FDB169E75ACBDF22BDB2697CBC9A0DF0FC915DD4151E5AAD466A415572D82DB4168G4EC1FA6C15F248F0CF8383157B1FF114E04EAEACF77B530D12D064C69B68E5EC8D1230469D06391855AADAA02218DC23E6F00FA5B727C1D426D36C01E602F71E8132025EDF4657DDA5E1550E9D5F9A1A876D06EF1D7282BEE06C02B503FC6F2B99D2
	6A15EE3A17524D4252ED21250B931FECF6040D159261B9B545FE1EFED51C2032047AA9796F7CD3235D457A9335F4C948D7FFC4F16BE17669D027D5CB3B5CFDD4A3AD005C2F6A5ECF6ED3CC245FC3F8A4B9C1FDC007BA2DCC0C70B4112CC4E4396B8DCD47635306DB8A3B8EF5DA59F90B271DABBC6C54577BB4E9E153CFF2A8EAB905DE3D5B5D2BE4E8F80F6B04DEBDCF07BA2D6A46830A95B84851C153079511BA54E9E5D362C4ECD22CA01F340D2765DC75772AA9B81F1D2867BEB4689802C6B66BD04CC8E6D27E
	840CBCA5C555A45329CAD1610C0C6CE6DA62458AB2661C27C31DD6556FCF6E73CFD27D48D776CCAFD67C1F68A696F9F6F644262CF12DB8FDEDCA4FD6EC4AD25934E482FA1F9E43E27A8A973CE938FD77F0537D108EF5DA15405BAE9D8DC9F9104D1324B7A39FB952719E60CDA75477GAFBC2B586B5FEDCDDBFA8F7B7BD5F977E87ECE465374EF87B7825E89011DB77F2410DC3E6F6DFE38FD03A60BC5B2D90EB4DBFB49DDF15BC7A5D139FFAF2510646F44B4F2A40BFB197E5D7943AA1E7F8FD0CB8788CE99F6B2
	F6AFGGA825GGD0CB818294G94G88G88G71F854ACCE99F6B2F6AFGGA825GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG30AFGGGG
**end of data**/
}
}
