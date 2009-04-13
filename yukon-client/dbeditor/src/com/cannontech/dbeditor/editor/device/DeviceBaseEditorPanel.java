package com.cannontech.dbeditor.editor.device;

import java.awt.GridBagConstraints;

import javax.swing.JOptionPane;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.AdvancedPropertiesDialog;
import com.cannontech.common.gui.util.JTextFieldComboEditor;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.config.ConfigTwoWay;
import com.cannontech.database.data.device.CCU721;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.GridAdvisorBase;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.data.device.KV;
import com.cannontech.database.data.device.MCT210;
import com.cannontech.database.data.device.MCT213;
import com.cannontech.database.data.device.MCT240;
import com.cannontech.database.data.device.MCT248;
import com.cannontech.database.data.device.MCT250;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.PagingTapTerminal;
import com.cannontech.database.data.device.RTCBase;
import com.cannontech.database.data.device.RTM;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.data.device.Repeater921;
import com.cannontech.database.data.device.SNPPTerminal;
import com.cannontech.database.data.device.Schlumberger;
import com.cannontech.database.data.device.Series5Base;
import com.cannontech.database.data.device.Sixnet;
import com.cannontech.database.data.device.TwoWayLCR;
import com.cannontech.database.data.device.WCTPTerminal;
import com.cannontech.database.data.lite.LiteConfig;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDialupSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceIDLCRemote;
import com.cannontech.dbeditor.DatabaseEditorOptionPane;
import com.cannontech.device.range.DeviceAddressRange;
import com.cannontech.yukon.IDatabaseCache;


public class DeviceBaseEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, javax.swing.event.CaretListener {

	private DeviceBase deviceBase = null;
	private DeviceAdvancedDialupEditorPanel advancedPanel = null;  //  @jve:visual-info  decl-index=0 visual-constraint="428,10"
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
	private javax.swing.JPanel communicationPanel = null;
	private javax.swing.JButton ivjJButtonAdvanced = null;
	private javax.swing.JComboBox ivjJComboBoxAmpUseType = null;
	private javax.swing.JLabel ivjJLabelCCUAmpUseType = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjConfigLabel = null;
	private javax.swing.JComboBox ivjConfigComboBox = null;
	private javax.swing.JComboBox ivjTOUComboBox = null;
	private javax.swing.JLabel ivjTOULabel = null;
	private javax.swing.JLabel ivjSecurityCodeLabel = null;
	private javax.swing.JTextField ivjSecurityCodeTextField = null;
	private javax.swing.JLabel ivjSenderLabel = null;
	private javax.swing.JTextField ivjSenderTextField = null;

	private javax.swing.JPanel jPanelMCTSettings = null;
class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DeviceBaseEditorPanel.this.getDisableFlagCheckBox()) 
				connEtoC3(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getControlInhibitCheckBox()) 
				connEtoC5(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getRouteComboBox()) 
				connEtoC4(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getPortComboBox()) 
				connEtoC6(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getSlaveAddressComboBox()) 
				connEtoC14(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getJButtonAdvanced()) 
				connEtoC7(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getJComboBoxAmpUseType()) 
				connEtoC9(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getConfigComboBox()) 
				connEtoC10(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getTOUComboBox()) 
				connEtoC11(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == DeviceBaseEditorPanel.this.getNameTextField()) 
				connEtoC1(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getPhysicalAddressTextField()) 
				connEtoC2(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getPhoneNumberTextField()) 
				connEtoC8(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getPasswordTextField()) 
				connEtoC12(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getSenderTextField()) 
				connEtoC13(e);
			if (e.getSource() == DeviceBaseEditorPanel.this.getSecurityCodeTextField()) 
				connEtoC15(e);
		};
	};
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
     * Helper method to check for duplicate address
     * @param address - Address in question
     * @param portID - id of port to check addresses for
     * @return True if address is unique
     */
    private boolean checkForDuplicateAddresses(int address, Integer paobjectId, Integer portId) {
        String[] devices = DeviceIDLCRemote.isAddressUnique(address, paobjectId, portId);

        if (devices.length > 0) {
            String devStr = new String();
            for (int i = 0; i < devices.length; i++)
                devStr += "          " + devices[i] + "\n";

            JOptionPane.showMessageDialog(this,
                                          "The address '"
                                                  + address
                                                  + "' is already in use by the following CCUs or RTUs: \n"
                                                  + devStr
                                                  + "\nCCUs and/or RTUs cannot have duplicate addresses on a dedicated comm channel.",
                                          "Address Already Used",
                                          JOptionPane.WARNING_MESSAGE);

            setErrorString(null);
            return false;
        }

        return true;
    }

    /**
     * Helper method to check that an address for an mct is unique
     * @param address - Address to check
     * @return True if unique
     */
    private boolean checkMCTAddresses(int address, Integer paobjectId) {
        String[] devices = DeviceCarrierSettings.isAddressUnique(address, paobjectId);

        if (devices.length > 0) {

            String message = "The address '"
                + address
                + "' is already used by the following devices,\n"
                + "are you sure you want to use it again?\n";
            
            int res = DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(this,
                                                                            message,
                                                                            "Address Already Used",
                                                                            devices);
            if (res == JOptionPane.NO_OPTION) {
                setErrorString(null);
                return false;
            }
        }

        return true;
    }
    
/**
 * Comment
 */
public void configComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	this.fireInputUpdate();
	return;
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
 * connEtoC10:  (ConfigComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceBaseEditorPanel.configComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC11:  (TOUComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
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
 * connEtoC12:  (PasswordTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC15:  (SecurityCodeTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(javax.swing.event.CaretEvent arg1) {
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

private javax.swing.JPanel getCommunicationPanel() {
	if (communicationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
			ivjLocalBorder1.setTitle("Communication");
			communicationPanel = new javax.swing.JPanel();
			
			communicationPanel.setMaximumSize(new java.awt.Dimension(394,252));
            communicationPanel.setPreferredSize(new java.awt.Dimension(394,265));
            communicationPanel.setMinimumSize(new java.awt.Dimension(0,0));
            
            communicationPanel.setName("CommunicationPanel");
            communicationPanel.setBorder(ivjLocalBorder1);
            communicationPanel.setLayout(new java.awt.GridBagLayout());
			
			GridBagConstraints routeComboBoxConstraint = new GridBagConstraints();
			GridBagConstraints portLabelConstraint = new GridBagConstraints();
			GridBagConstraints routeLabelConstraint = new GridBagConstraints();
			GridBagConstraints portComboBoxConstraint = new GridBagConstraints();
			GridBagConstraints postCommWaitSpinnerConstraint = new GridBagConstraints();
			GridBagConstraints postCommWaitLabelConstraint = new GridBagConstraints();
			GridBagConstraints waitLabelConstraint = new GridBagConstraints();
			GridBagConstraints passwordLabelConstraint = new GridBagConstraints();
			GridBagConstraints passwordTextFieldConstraint = new GridBagConstraints();
			GridBagConstraints dialupSettingsPanelConstraint = new GridBagConstraints();
			GridBagConstraints slaveAddressComboBoxConstraint = new GridBagConstraints();
			GridBagConstraints slaveAddressLabelConstraint = new GridBagConstraints();
			GridBagConstraints ampUseTypeComboBoxConstraint = new GridBagConstraints();
			GridBagConstraints senderLabelConstraint = new GridBagConstraints();
			GridBagConstraints ccuAmpUseTypeLabelConstraint = new GridBagConstraints();
			GridBagConstraints senderTextFieldConstraint = new GridBagConstraints();
			GridBagConstraints securityCodeTextFieldConstraint = new GridBagConstraints();
			GridBagConstraints securityCodeLabelConstraint = new GridBagConstraints();
			
			waitLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
			waitLabelConstraint.gridy = 2;
			waitLabelConstraint.gridx = 2;
			communicationPanel.add(getWaitLabel(), waitLabelConstraint);
			
			passwordLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
			passwordLabelConstraint.gridy = 3;
			passwordLabelConstraint.gridx = 0;
			communicationPanel.add(getPasswordLabel(), passwordLabelConstraint);
			
			portLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
			portLabelConstraint.gridy = 1;
			portLabelConstraint.gridx = 0;
			portLabelConstraint.anchor = GridBagConstraints.NORTHWEST;
			communicationPanel.add(getPortLabel(), portLabelConstraint);
			
			portComboBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
			portComboBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
			portComboBoxConstraint.weightx = 1.0;
			portComboBoxConstraint.gridwidth = 2;
			portComboBoxConstraint.gridy = 1;
			portComboBoxConstraint.gridx = 1;
			portComboBoxConstraint.anchor = GridBagConstraints.NORTHWEST;
			communicationPanel.add(getPortComboBox(), portComboBoxConstraint);
			
			postCommWaitLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
			postCommWaitLabelConstraint.gridy = 2;
			postCommWaitLabelConstraint.gridx = 0;
			communicationPanel.add(getPostCommWaitLabel(), postCommWaitLabelConstraint);
			
			postCommWaitSpinnerConstraint.insets = new java.awt.Insets(5,5,5,5);
			postCommWaitSpinnerConstraint.gridy = 2;
			postCommWaitSpinnerConstraint.gridx = 1;
			communicationPanel.add(getPostCommWaitSpinner(), postCommWaitSpinnerConstraint);
			
			routeLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
			routeLabelConstraint.anchor = GridBagConstraints.NORTHWEST;
			routeLabelConstraint.gridy = 0;
			routeLabelConstraint.gridx = 0;
			communicationPanel.add(getRouteLabel(), routeLabelConstraint);
			
	        routeComboBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
            routeComboBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            routeComboBoxConstraint.weightx = 1.0;
            routeComboBoxConstraint.gridwidth = 2;
            routeComboBoxConstraint.gridy = 1;
            routeComboBoxConstraint.gridx = 0;
            routeComboBoxConstraint.anchor = java.awt.GridBagConstraints.NORTHWEST;
            communicationPanel.add(getRouteComboBox(), routeComboBoxConstraint);
			
			passwordTextFieldConstraint.insets = new java.awt.Insets(5,5,5,5);
			passwordTextFieldConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
			passwordTextFieldConstraint.weightx = 1.0;
			passwordTextFieldConstraint.gridwidth = 2;
			passwordTextFieldConstraint.gridy = 3;
			passwordTextFieldConstraint.gridx = 1;
			communicationPanel.add(getPasswordTextField(), passwordTextFieldConstraint);
			
			slaveAddressComboBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
			slaveAddressComboBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
			slaveAddressComboBoxConstraint.weightx = 1.0;
			slaveAddressComboBoxConstraint.gridwidth = 2;
			slaveAddressComboBoxConstraint.gridy = 5;
			slaveAddressComboBoxConstraint.gridx = 1;
			communicationPanel.add(getSlaveAddressComboBox(), slaveAddressComboBoxConstraint);
			
			senderLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
			senderLabelConstraint.gridy = 6;
			senderLabelConstraint.gridx = 0;
			senderLabelConstraint.anchor = java.awt.GridBagConstraints.NORTHWEST;
            communicationPanel.add(getSenderLabel(), senderLabelConstraint);
			
			dialupSettingsPanelConstraint.insets = new java.awt.Insets(5,5,5,5);
			dialupSettingsPanelConstraint.gridwidth = 3;
			dialupSettingsPanelConstraint.gridy = 8;
			dialupSettingsPanelConstraint.gridx = 0;
			communicationPanel.add(getDialupSettingsPanel(), dialupSettingsPanelConstraint);
			
			slaveAddressLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
			slaveAddressLabelConstraint.gridy = 5;
			slaveAddressLabelConstraint.gridx = 0;
			communicationPanel.add(getSlaveAddressLabel(), slaveAddressLabelConstraint);
			
			ampUseTypeComboBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
			ampUseTypeComboBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
			ampUseTypeComboBoxConstraint.weightx = 1.0;
			ampUseTypeComboBoxConstraint.gridwidth = 2;
			ampUseTypeComboBoxConstraint.gridy = 4;
			ampUseTypeComboBoxConstraint.gridx = 1;
			communicationPanel.add(getJComboBoxAmpUseType(), ampUseTypeComboBoxConstraint);
			
			senderTextFieldConstraint.insets = new java.awt.Insets(5,5,5,5);
			senderTextFieldConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
			senderTextFieldConstraint.weightx = 1.0;
			senderTextFieldConstraint.gridwidth = 2;
			senderTextFieldConstraint.gridy = 6;
			senderTextFieldConstraint.gridx = 1;
			senderTextFieldConstraint.anchor = java.awt.GridBagConstraints.NORTHWEST;
			communicationPanel.add(getSenderTextField(), senderTextFieldConstraint);
			
			
			securityCodeTextFieldConstraint.insets = new java.awt.Insets(5,5,5,5);
			securityCodeTextFieldConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
			securityCodeTextFieldConstraint.weightx = 1.0;
			securityCodeTextFieldConstraint.gridwidth = 2;
			securityCodeTextFieldConstraint.gridy = 7;
			securityCodeTextFieldConstraint.gridx = 1;
			securityCodeTextFieldConstraint.anchor = java.awt.GridBagConstraints.NORTHWEST;
			communicationPanel.add(getSecurityCodeTextField(), securityCodeTextFieldConstraint);
			
			securityCodeLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
			securityCodeLabelConstraint.gridy = 7;
			securityCodeLabelConstraint.gridx = 0;
			securityCodeLabelConstraint.anchor = java.awt.GridBagConstraints.NORTHWEST;
			communicationPanel.add(getSecurityCodeLabel(), securityCodeLabelConstraint);
			
			ccuAmpUseTypeLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
			ccuAmpUseTypeLabelConstraint.gridy = 4;
			ccuAmpUseTypeLabelConstraint.gridx = 0;
			communicationPanel.add(getJLabelCCUAmpUseType(), ccuAmpUseTypeLabelConstraint);
			
			
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return communicationPanel;
}

/**
 * Return the ConfigComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getConfigComboBox() {
	if (ivjConfigComboBox == null) {
		try {
			ivjConfigComboBox = new javax.swing.JComboBox();
			ivjConfigComboBox.setName("ConfigComboBox");
			ivjConfigComboBox.setMaximumSize(new java.awt.Dimension(162,20));
			ivjConfigComboBox.setVisible(true);
			ivjConfigComboBox.setPreferredSize(new java.awt.Dimension(162,20));
			ivjConfigComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjConfigComboBox.setMinimumSize(new java.awt.Dimension(162,20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfigComboBox;
}

/**
 * Return the ConfigLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getConfigLabel() {
	if (ivjConfigLabel == null) {
		try {
			ivjConfigLabel = new javax.swing.JLabel();
			ivjConfigLabel.setName("ConfigLabel");
			ivjConfigLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjConfigLabel.setText("MCT Config: ");
			ivjConfigLabel.setVisible(true);
			// user code begin {1}
			ivjConfigLabel.setPreferredSize(new java.awt.Dimension(172,19));
			ivjConfigLabel.setMaximumSize(new java.awt.Dimension(172,19));
			ivjConfigLabel.setMinimumSize(new java.awt.Dimension(172,19));
			ivjConfigLabel.setFont(new java.awt.Font("Arial", 0, 14));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfigLabel;
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
			ivjControlInhibitCheckBox.setText("Disable Code Verification");
			ivjControlInhibitCheckBox.setMaximumSize(new java.awt.Dimension(200, 23));
			ivjControlInhibitCheckBox.setActionCommand("Control Inhibit");
			ivjControlInhibitCheckBox.setBorderPainted(false);
			ivjControlInhibitCheckBox.setPreferredSize(new java.awt.Dimension(200, 23));
			ivjControlInhibitCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlInhibitCheckBox.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjControlInhibitCheckBox.setMinimumSize(new java.awt.Dimension(200, 23));
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
 * Return the DialupSettingsPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDialupSettingsPanel() {
	if (ivjDialupSettingsPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder2.setTitle("Dialing");
			ivjDialupSettingsPanel = new javax.swing.JPanel();
			ivjDialupSettingsPanel.setName("DialupSettingsPanel");
			ivjDialupSettingsPanel.setBorder(ivjLocalBorder2);
			ivjDialupSettingsPanel.setLayout(new java.awt.GridBagLayout());
			ivjDialupSettingsPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjDialupSettingsPanel.setVisible(true);
			ivjDialupSettingsPanel.setPreferredSize(new java.awt.Dimension(332, 70));
			ivjDialupSettingsPanel.setMinimumSize(new java.awt.Dimension(332, 70));

			java.awt.GridBagConstraints constraintsPhoneNumberTextField = new java.awt.GridBagConstraints();
			constraintsPhoneNumberTextField.gridx = 2; constraintsPhoneNumberTextField.gridy = 1;
			constraintsPhoneNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPhoneNumberTextField.weightx = 1.0;
			constraintsPhoneNumberTextField.insets = new java.awt.Insets(11, 21, 1, 11);
			getDialupSettingsPanel().add(getPhoneNumberTextField(), constraintsPhoneNumberTextField);

			java.awt.GridBagConstraints constraintsPhoneNumberLabel = new java.awt.GridBagConstraints();
			constraintsPhoneNumberLabel.gridx = 1; constraintsPhoneNumberLabel.gridy = 1;
			constraintsPhoneNumberLabel.ipadx = 24;
			constraintsPhoneNumberLabel.ipady = 7;
			constraintsPhoneNumberLabel.insets = new java.awt.Insets(11, 15, 1, 21);
			getDialupSettingsPanel().add(getPhoneNumberLabel(), constraintsPhoneNumberLabel);

			java.awt.GridBagConstraints constraintsJButtonAdvanced = new java.awt.GridBagConstraints();
			constraintsJButtonAdvanced.gridx = 1; constraintsJButtonAdvanced.gridy = 2;
			constraintsJButtonAdvanced.insets = new java.awt.Insets(2, 15, 10, 21);
			getDialupSettingsPanel().add(getJButtonAdvanced(), constraintsJButtonAdvanced);
			// user code begin {1}
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
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Identification");
			ivjIdentificationPanel = new javax.swing.JPanel();
			ivjIdentificationPanel.setName("IdentificationPanel");
			ivjIdentificationPanel.setBorder(ivjLocalBorder);
			ivjIdentificationPanel.setLayout(new java.awt.GridBagLayout());
			ivjIdentificationPanel.setMaximumSize(new java.awt.Dimension(394, 143));
			ivjIdentificationPanel.setPreferredSize(new java.awt.Dimension(394, 143));
			ivjIdentificationPanel.setMinimumSize(new java.awt.Dimension(0,0));

			java.awt.GridBagConstraints constraintsTypeTextField = new java.awt.GridBagConstraints();
			constraintsTypeTextField.gridx = 2; constraintsTypeTextField.gridy = 1;
			constraintsTypeTextField.insets = new java.awt.Insets(14, 5, 3, 56);
			java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
			constraintsTypeLabel.gridx = 1; constraintsTypeLabel.gridy = 1;
			constraintsTypeLabel.ipadx = 37;
			constraintsTypeLabel.ipady = 3;
			constraintsTypeLabel.insets = new java.awt.Insets(14, 8, 3, 5);
			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 2;
			constraintsNameLabel.ipadx = 33;
			constraintsNameLabel.ipady = 7;
			constraintsNameLabel.insets = new java.awt.Insets(3, 8, 4, 5);
			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 2;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.insets = new java.awt.Insets(3, 5, 4, 56);
			java.awt.GridBagConstraints constraintsPhysicalAddressLabel = new java.awt.GridBagConstraints();
			constraintsPhysicalAddressLabel.gridx = 1; constraintsPhysicalAddressLabel.gridy = 3;
			constraintsPhysicalAddressLabel.ipadx = 8;
			constraintsPhysicalAddressLabel.ipady = 7;
			constraintsPhysicalAddressLabel.insets = new java.awt.Insets(5, 8, 5, 5);
			java.awt.GridBagConstraints constraintsPhysicalAddressTextField = new java.awt.GridBagConstraints();
			constraintsPhysicalAddressTextField.gridx = 2; constraintsPhysicalAddressTextField.gridy = 3;
			constraintsPhysicalAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPhysicalAddressTextField.weightx = 1.0;
			constraintsPhysicalAddressTextField.insets = new java.awt.Insets(5, 5, 5, 56);
			java.awt.GridBagConstraints constraintsDisableFlagCheckBox = new java.awt.GridBagConstraints();
			constraintsDisableFlagCheckBox.gridx = 1; constraintsDisableFlagCheckBox.gridy = 4;
			constraintsDisableFlagCheckBox.ipadx = -1;
			constraintsDisableFlagCheckBox.ipady = -3;
			constraintsDisableFlagCheckBox.insets = new java.awt.Insets(5, 8, 12, 5);
			getIdentificationPanel().add(getDisableFlagCheckBox(), constraintsDisableFlagCheckBox);

			java.awt.GridBagConstraints constraintsControlInhibitCheckBox = new java.awt.GridBagConstraints();
			constraintsPhysicalAddressTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsTypeLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsTypeTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsNameTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsPhysicalAddressLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsControlInhibitCheckBox.gridx = 2; constraintsControlInhibitCheckBox.gridy = 4;
			constraintsControlInhibitCheckBox.insets = new java.awt.Insets(5, 5, 12, 56);
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			ivjIdentificationPanel.add(getTypeTextField(), constraintsTypeTextField);
			ivjIdentificationPanel.add(getTypeLabel(), constraintsTypeLabel);
			ivjIdentificationPanel.add(getNameLabel(), constraintsNameLabel);
			ivjIdentificationPanel.add(getNameTextField(), constraintsNameTextField);
			ivjIdentificationPanel.add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);
			ivjIdentificationPanel.add(getPhysicalAddressTextField(), constraintsPhysicalAddressTextField);
			getIdentificationPanel().add(getControlInhibitCheckBox(), constraintsControlInhibitCheckBox);
			// user code begin {1}
			getControlInhibitCheckBox().setVisible(false);
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
			ivjJButtonAdvanced.setPreferredSize(new java.awt.Dimension(122, 23));
			ivjJButtonAdvanced.setText("Advanced...");
			ivjJButtonAdvanced.setMaximumSize(new java.awt.Dimension(122, 23));
			ivjJButtonAdvanced.setMinimumSize(new java.awt.Dimension(122, 23));
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
			ivjJComboBoxAmpUseType.setMaximumSize(new java.awt.Dimension(162,20));
			ivjJComboBoxAmpUseType.setPreferredSize(new java.awt.Dimension(162,20));
			ivjJComboBoxAmpUseType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxAmpUseType.setMinimumSize(new java.awt.Dimension(162,20));
			// user code begin {1}

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
			ivjJLabelCCUAmpUseType.setMaximumSize(new java.awt.Dimension(172,19));
			ivjJLabelCCUAmpUseType.setVisible(true);
			ivjJLabelCCUAmpUseType.setPreferredSize(new java.awt.Dimension(172,19));
			ivjJLabelCCUAmpUseType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCCUAmpUseType.setMinimumSize(new java.awt.Dimension(172,19));
			// user code begin {1}
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
			ivjNameTextField.setMaximumSize(new java.awt.Dimension(200, 23));
			ivjNameTextField.setColumns(12);
			ivjNameTextField.setPreferredSize(new java.awt.Dimension(200, 23));
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setMinimumSize(new java.awt.Dimension(200, 23));
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
			ivjPasswordLabel.setMaximumSize(new java.awt.Dimension(172,19));
			ivjPasswordLabel.setPreferredSize(new java.awt.Dimension(172,19));
			ivjPasswordLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPasswordLabel.setMinimumSize(new java.awt.Dimension(172,19));
			// user code begin {1}
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
			ivjPasswordTextField.setMaximumSize(new java.awt.Dimension(162,24));
			ivjPasswordTextField.setColumns(0);
			ivjPasswordTextField.setPreferredSize(new java.awt.Dimension(162,24));
			ivjPasswordTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPasswordTextField.setMinimumSize(new java.awt.Dimension(162,24));
			// user code begin {1}
			
			ivjPasswordTextField.setDocument(
				new TextFieldDocument(TextFieldDocument.MAX_IED_PASSWORD_LENGTH));

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
			ivjPhoneNumberTextField.setPreferredSize(new java.awt.Dimension(142, 23));
			ivjPhoneNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPhoneNumberTextField.setEnabled(true);
			ivjPhoneNumberTextField.setMinimumSize(new java.awt.Dimension(142, 23));
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
			ivjPhysicalAddressTextField.setMaximumSize(new java.awt.Dimension(200, 23));
			//ivjPhysicalAddressTextField.setColumns(15);
			ivjPhysicalAddressTextField.setPreferredSize(new java.awt.Dimension(200, 23));
			ivjPhysicalAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPhysicalAddressTextField.setMinimumSize(new java.awt.Dimension(200, 23));
			// user code begin {1}

			ivjPhysicalAddressTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument() );
			
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
			ivjPortComboBox.setMaximumSize(new java.awt.Dimension(162,20));
			ivjPortComboBox.setPreferredSize(new java.awt.Dimension(162,20));
			ivjPortComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPortComboBox.setMinimumSize(new java.awt.Dimension(162,20));
			// user code begin {1}
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
			ivjPortLabel.setMaximumSize(new java.awt.Dimension(172,19));
			ivjPortLabel.setPreferredSize(new java.awt.Dimension(172,19));
			ivjPortLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPortLabel.setMinimumSize(new java.awt.Dimension(172,19));
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
			ivjPostCommWaitLabel.setPreferredSize(new java.awt.Dimension(172,19));
			ivjPostCommWaitLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPostCommWaitLabel.setMinimumSize(new java.awt.Dimension(172, 16));
			// user code begin {1}
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
			ivjRouteComboBox.setPreferredSize(new java.awt.Dimension(200,20));
			ivjRouteComboBox.setMinimumSize(new java.awt.Dimension(200,20));
			// user code begin {1}
			ivjRouteComboBox.setMaximumSize(new java.awt.Dimension(200,20));
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
			ivjRouteLabel.setPreferredSize(new java.awt.Dimension(150,19));
			ivjRouteLabel.setMinimumSize(new java.awt.Dimension(150,19));
			ivjRouteLabel.setMaximumSize(new java.awt.Dimension(150,19));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRouteLabel;
}

/**
 * Return the SecurityCodeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSecurityCodeLabel() {
	if (ivjSecurityCodeLabel == null) {
		try {
			ivjSecurityCodeLabel = new javax.swing.JLabel();
			ivjSecurityCodeLabel.setName("SecurityCodeLabel");
			ivjSecurityCodeLabel.setText("Security Code:");
			ivjSecurityCodeLabel.setMaximumSize(new java.awt.Dimension(172,19));
			ivjSecurityCodeLabel.setPreferredSize(new java.awt.Dimension(172,19));
			ivjSecurityCodeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSecurityCodeLabel.setMinimumSize(new java.awt.Dimension(172,19));
			ivjSecurityCodeLabel.setVisible(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSecurityCodeLabel;
}

/**
 * Return the SecurityCodeTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getSecurityCodeTextField() {
	if (ivjSecurityCodeTextField == null) {
		try {
			ivjSecurityCodeTextField = new javax.swing.JTextField();
			ivjSecurityCodeTextField.setName("SecurityCodeTextField");
			ivjSecurityCodeTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjSecurityCodeTextField.setColumns(0);
			ivjSecurityCodeTextField.setPreferredSize(new java.awt.Dimension(157,24));
			ivjSecurityCodeTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjSecurityCodeTextField.setMinimumSize(new java.awt.Dimension(120, 20));
			ivjSecurityCodeTextField.setVisible(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSecurityCodeTextField;
}

/**
 * Return the SenderLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSenderLabel() {
	if (ivjSenderLabel == null) {
		try {
			ivjSenderLabel = new javax.swing.JLabel();
			ivjSenderLabel.setName("SenderLabel");
			ivjSenderLabel.setText("Sender:");
			ivjSenderLabel.setMaximumSize(new java.awt.Dimension(172,19));
			ivjSenderLabel.setPreferredSize(new java.awt.Dimension(172,19));
			ivjSenderLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSenderLabel.setMinimumSize(new java.awt.Dimension(172,19));
			ivjSenderLabel.setVisible(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSenderLabel;
}

/**
 * Return the SenderTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getSenderTextField() {
	if (ivjSenderTextField == null) {
		try {
			ivjSenderTextField = new javax.swing.JTextField();
			ivjSenderTextField.setName("SenderTextField");
			ivjSenderTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjSenderTextField.setColumns(0);
			ivjSenderTextField.setPreferredSize(new java.awt.Dimension(120, 20));
			ivjSenderTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjSenderTextField.setMinimumSize(new java.awt.Dimension(120, 20));
			ivjSenderTextField.setVisible(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSenderTextField;
}

/**
 * Return the SlaveAddressComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getSlaveAddressComboBox() {
	if (ivjSlaveAddressComboBox == null) {
		try {
			ivjSlaveAddressComboBox = new javax.swing.JComboBox();
			ivjSlaveAddressComboBox.setName("SlaveAddressComboBox");
			ivjSlaveAddressComboBox.setMaximumSize(new java.awt.Dimension(162,20));
			ivjSlaveAddressComboBox.setPreferredSize(new java.awt.Dimension(162,20));
			ivjSlaveAddressComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSlaveAddressComboBox.setMinimumSize(new java.awt.Dimension(162,20));
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_STAND_ALONE );
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_MASTER );
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_SLAVE1 );
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_SLAVE2 );
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_SLAVE3 );
			ivjSlaveAddressComboBox.addItem( IEDBase.SLAVE_SLAVE4 );
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSlaveAddressComboBox;
}

/**
 * Return the SlaveAddressLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSlaveAddressLabel() {
	if (ivjSlaveAddressLabel == null) {
		try {
			ivjSlaveAddressLabel = new javax.swing.JLabel();
			ivjSlaveAddressLabel.setName("SlaveAddressLabel");
			ivjSlaveAddressLabel.setText("Slave Address:");
			ivjSlaveAddressLabel.setMaximumSize(new java.awt.Dimension(172,19));
			ivjSlaveAddressLabel.setPreferredSize(new java.awt.Dimension(172,19));
			ivjSlaveAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSlaveAddressLabel.setMinimumSize(new java.awt.Dimension(172,19));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSlaveAddressLabel;
}

/**
 * Return the TOUComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getTOUComboBox() {
	if (ivjTOUComboBox == null) {
		try {
			ivjTOUComboBox = new javax.swing.JComboBox();
			ivjTOUComboBox.setName("TOUComboBox");
			ivjTOUComboBox.setMaximumSize(new java.awt.Dimension(162,20));
			ivjTOUComboBox.setVisible(false);
			ivjTOUComboBox.setPreferredSize(new java.awt.Dimension(162,20));
			ivjTOUComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTOUComboBox.setMinimumSize(new java.awt.Dimension(162,20));
			ivjTOUComboBox.setEnabled(false);
			ivjTOUComboBox.setVisible(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTOUComboBox;
}

/**
 * Return the TOULabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getTOULabel() {
	if (ivjTOULabel == null) {
		try {
			ivjTOULabel = new javax.swing.JLabel();
			ivjTOULabel.setName("TOULabel");
			ivjTOULabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTOULabel.setText("TOU Schedule: ");
			ivjTOULabel.setVisible(false);
			ivjTOULabel.setEnabled(false);
			ivjTOULabel.setPreferredSize(new java.awt.Dimension(172,19));
			ivjTOULabel.setMaximumSize(new java.awt.Dimension(172,19));
			ivjTOULabel.setMinimumSize(new java.awt.Dimension(172,19));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTOULabel;
}

/**
 * Return the TypeLabel property value.
 * @return javax.swing.JLabel
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTypeLabel;
}
/**
 * Return the DeviceTypeTextField property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getTypeTextField() {
	if (ivjTypeTextField == null) {
		try {
			ivjTypeTextField = new javax.swing.JLabel();
			ivjTypeTextField.setName("TypeTextField");
			ivjTypeTextField.setOpaque(true);
			ivjTypeTextField.setText("");
			ivjTypeTextField.setMaximumSize(new java.awt.Dimension(200, 23));
			ivjTypeTextField.setPreferredSize(new java.awt.Dimension(200, 23));
			ivjTypeTextField.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjTypeTextField.setMinimumSize(new java.awt.Dimension(200, 23));
		} catch (java.lang.Throwable ivjExc) {
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
	deviceBase = (com.cannontech.database.data.device.DeviceBase)val;

	deviceBase.setPAOName( getNameTextField().getText() );
    int devType = PAOGroups.getDeviceType( deviceBase.getPAOType() );

    //just in case, set our String type data to the exact String type expected
    // used to ensure the type string in the DB is the same as the code
    deviceBase.setDeviceType( PAOGroups.getPAOTypeString(devType) );

	if( getDisableFlagCheckBox().isSelected() )
		deviceBase.setDisableFlag( new Character('Y') );
	else
		deviceBase.setDisableFlag( new Character('N') );

	/*if( getControlInhibitCheckBox().isSelected() )
		d.getDevice().setControlInhibit( new Character( 'Y' ) );
	else
		d.getDevice().setControlInhibit( new Character( 'N' ) );*/

	//This is a little bit ugly
	//The address could be coming from three distinct
	//types of devices - yet all devices have an address
	//eeck.
	if( getPhysicalAddressTextField().isVisible() ) {
		try {
			Integer address = new Integer( getPhysicalAddressTextField().getText() );

			if( val instanceof CarrierBase ) {
				CarrierBase carrierBase = (CarrierBase)val;
				if( devType == DeviceTypes.REPEATER ) { //val instanceof Repeater900
					carrierBase.getDeviceCarrierSettings().setAddress( new Integer(address.intValue() + Repeater900.ADDRESS_OFFSET) );
				} else if( devType == DeviceTypes.REPEATER_921 ) {//val instanceof Repeater921
					carrierBase.getDeviceCarrierSettings().setAddress( new Integer(address.intValue() + Repeater921.ADDRESS_OFFSET) );
                } else {
                	carrierBase.getDeviceCarrierSettings().setAddress( address );
				}
			} else if( val instanceof IDLCBase ) {
				IDLCBase idlcBase = (IDLCBase)val;
				idlcBase.getDeviceIDLCRemote().setAddress(address);
			}
		} catch(NumberFormatException n ) {
			CTILogger.error( n.getMessage(), n );
		}
	}
    
    if( val instanceof GridAdvisorBase ) {
    	GridAdvisorBase gridAdvisorBase = (GridAdvisorBase)val;
        LiteYukonPAObject litePort = null;
        
        litePort = (LiteYukonPAObject)getPortComboBox().getSelectedItem();
        //get new port from combo box and set it.
        gridAdvisorBase.getDeviceDirectCommSettings().setPortID(litePort.getLiteID());
        try {
        	gridAdvisorBase.getDeviceAddress().setMasterAddress( new Integer(getPhysicalAddressTextField().getText()) );
        } catch( NumberFormatException e ) {
        	gridAdvisorBase.getDeviceAddress().setMasterAddress( new Integer(0) );
        }        
    }
    
	if( val instanceof RemoteBase ) {
		RemoteBase remoteBase = (RemoteBase)val;
		DeviceDirectCommSettings dDirect = remoteBase.getDeviceDirectCommSettings();

		Integer portID = null;
		Integer postCommWait = null;

		LiteYukonPAObject port = ((LiteYukonPAObject)getPortComboBox().getSelectedItem());

		portID = new Integer(port.getYukonID());
		dDirect.setPortID( portID );

		Object postCommWaitSpinVal = getPostCommWaitSpinner().getValue();
		if( postCommWaitSpinVal instanceof Long ) {
			postCommWait = new Integer( ((Long)postCommWaitSpinVal).intValue() );
		} else if( postCommWaitSpinVal instanceof Integer ) {
			postCommWait = new Integer( ((Integer)postCommWaitSpinVal).intValue() );
		}

		if( val instanceof IDLCBase ) {
			IDLCBase idlcBase = (IDLCBase)val;
			idlcBase.getDeviceIDLCRemote().setPostCommWait( postCommWait );
			idlcBase.getDeviceIDLCRemote().setCcuAmpUseType( getJComboBoxAmpUseType().getSelectedItem().toString() );
		}
		
		if( PAOGroups.isDialupPort(port.getType()) ) {
			DeviceDialupSettings dDialup = remoteBase.getDeviceDialupSettings();

			getAdvancedPanel().getValue( dDialup );
			
			dDialup.setPhoneNumber( getPhoneNumberTextField().getText().trim() );
			if( val instanceof PagingTapTerminal ) {
				dDialup.setLineSettings( "7E1" );
			} else {
				dDialup.setLineSettings( "8N1" );
			}
		}
		else {
			remoteBase.getDeviceDialupSettings().setPhoneNumber(null);
		}

      if( val instanceof DNPBase ) { //DeviceTypesFuncs.hasMasterAddress(devType) ) 
         DNPBase dnpBase = (DNPBase)val;
         try {
            dnpBase.getDeviceAddress().setMasterAddress( new Integer(getPhysicalAddressTextField().getText()) );
         } catch( NumberFormatException e ) {
            dnpBase.getDeviceAddress().setMasterAddress( new Integer(0) );
         }
            
         try {         
            dnpBase.getDeviceAddress().setSlaveAddress( new Integer(getSlaveAddressComboBox().getSelectedItem().toString() ) );
         } catch( NumberFormatException e ) {
            dnpBase.getDeviceAddress().setSlaveAddress( new Integer(0) );
         }
   
         try {
            dnpBase.getDeviceAddress().setPostCommWait( new Integer(getPostCommWaitSpinner().getValue().toString()) );
         } catch( NumberFormatException e ) {
            dnpBase.getDeviceAddress().setPostCommWait( new Integer(0) );
         }
   	
      } else if( val instanceof Series5Base ) {
		Series5Base series5Base= (Series5Base)val;
		
		try {
			series5Base.getSeries5().setSlaveAddress( new Integer(getPhysicalAddressTextField().getText()) );
		} catch( NumberFormatException e ) {
			series5Base.getSeries5().setSlaveAddress( new Integer(0) );
		}
		
		try {
			series5Base.getSeries5().setPostCommWait( new Integer(getPostCommWaitSpinner().getValue().toString()) );
		} catch( NumberFormatException e ) {
			series5Base.getSeries5().setPostCommWait( new Integer(0) );
		}
		
		if(getControlInhibitCheckBox().isSelected()) {
			series5Base.getVerification().setDisable("Y");
		}
		else {
			series5Base.getVerification().setDisable("N");
		}
      } else if( val instanceof RTCBase) {
		RTCBase rtcBase = (RTCBase)val;
		try {
			rtcBase.getDeviceRTC().setRTCAddress( new Integer(getPhysicalAddressTextField().getText()) );
		} catch( NumberFormatException e ) {
			rtcBase.getDeviceRTC().setRTCAddress( new Integer(0) );
		}
            
		try {         
			rtcBase.setLBTMode( getSlaveAddressComboBox().getSelectedItem().toString() );
		} catch( NumberFormatException e ) {
			rtcBase.getDeviceRTC().setLBTMode( new Integer(0) );
		}
		
		if(getControlInhibitCheckBox().isSelected()) {
			rtcBase.getDeviceRTC().setDisableVerifies("Y");
		} else {
			rtcBase.getDeviceRTC().setDisableVerifies("N");
		}
			
      } else if( val instanceof RTM ) {
			RTM rtm = (RTM)val;
			rtm.getDeviceIED().setSlaveAddress( getPhysicalAddressTextField().getText() );
      } else if( val instanceof IEDBase ) {
    	  IEDBase iedBase = (IEDBase)val;
      		String password = getPasswordTextField().getText();
			if( password.length() > 0 ) {
				if(val instanceof WCTPTerminal) {
					((WCTPTerminal)val).getDeviceTapPagingSettings().setPOSTPath(password);
				} else {
					iedBase.getDeviceIED().setPassword(password);
				}
			} else {
				if(val instanceof PagingTapTerminal) {
					((PagingTapTerminal)val).getDeviceTapPagingSettings().setPOSTPath(com.cannontech.common.util.CtiUtilities.STRING_NONE);
				} else {
					iedBase.getDeviceIED().setPassword(com.cannontech.common.util.CtiUtilities.STRING_NONE);
				}
			}

			if(val instanceof PagingTapTerminal) {
				PagingTapTerminal pagingTapTerminal = (PagingTapTerminal)val;
				if(getSenderTextField().isVisible() && getSenderTextField().getText().length() > 0) {
					pagingTapTerminal.getDeviceTapPagingSettings().setSender(getSenderTextField().getText());
				}
				if(getSecurityCodeTextField().isVisible() && getSecurityCodeTextField().getText().length() > 0) {
					pagingTapTerminal.getDeviceTapPagingSettings().setSecurityCode(getSecurityCodeTextField().getText());
				}
			}			
	
			if( getSlaveAddressComboBox().isVisible() ) {
				String slaveAddress = null;

				/**** START SUPER HACK ****/
				if( getSlaveAddressComboBox().isEditable() ) {
					slaveAddress = getSlaveAddressComboBox().getEditor().getItem().toString();
				} else {/**** END SUPER HACK ****/
					slaveAddress = new String( getSlaveAddressComboBox().getSelectedItem() != null ?
						  		getSlaveAddressComboBox().getSelectedItem().toString() : "" );
				}

				iedBase.getDeviceIED().setSlaveAddress(slaveAddress);
			}
	  	} else if (val instanceof CCU721) {
	         CCU721 ccu721 = (CCU721)val;
	         try {
	        	 ccu721.getDeviceAddress().setMasterAddress( new Integer(getPhysicalAddressTextField().getText()) );
	         } catch( NumberFormatException e ) {
	        	 ccu721.getDeviceAddress().setMasterAddress( new Integer(0) );
	         }
	            
	         try {         
	        	 ccu721.getDeviceAddress().setSlaveAddress( new Integer(getSlaveAddressComboBox().getSelectedItem().toString() ) );
	         } catch( NumberFormatException e ) {
	        	 ccu721.getDeviceAddress().setSlaveAddress( new Integer(0) );
	         }	      
	  	}

	} else {
		if( val instanceof CarrierBase ) {
			CarrierBase carrierBase = (CarrierBase)val;
			int routeId = (((LiteYukonPAObject)getRouteComboBox().getSelectedItem()).getYukonID());
			carrierBase.getDeviceRoutes().setRouteID( routeId);
		}
	}
	
	if( val instanceof MCTBase ) {
		MCTBase mctBase = (MCTBase) val;
		if(getConfigComboBox().getSelectedItem().equals(CtiUtilities.STRING_NONE)) {
			mctBase.getConfigMapping().deleteAMapping(mctBase.getDevice().getDeviceID());
			mctBase.setHasConfig(false);
		} else {
			int configId = ((LiteConfig)getConfigComboBox().getSelectedItem()).getConfigID();
			mctBase.setConfigMapping(configId, mctBase.getDevice().getDeviceID());
			mctBase.setHasConfig(true);
		}
		 
		/*if(getTOUComboBox().getSelectedItem().equals(CtiUtilities.STRING_NONE)) {
		   mctBase.getTOUDeviceMapping().deleteAMapping(mctBase.getDevice().getDeviceID());
		   mctBase.setHasTOUSchedule(false);
		} else {
		   mctBase.setTOUDeviceMapping(new Integer(((com.cannontech.database.data.lite.LiteTOUSchedule)getTOUComboBox().getSelectedItem()).getScheduleID()), mctBase.getDevice().getDeviceID());
		   mctBase.setHasTOUSchedule(true);
		}*/
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
			ivjWaitLabel.setText("(msec.)");
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
	getNameTextField().addCaretListener(ivjEventHandler);
	getPhysicalAddressTextField().addCaretListener(ivjEventHandler);
	getDisableFlagCheckBox().addActionListener(ivjEventHandler);
	getControlInhibitCheckBox().addActionListener(ivjEventHandler);
	getRouteComboBox().addActionListener(ivjEventHandler);
	getPortComboBox().addActionListener(ivjEventHandler);
	getPhoneNumberTextField().addCaretListener(ivjEventHandler);
	getSlaveAddressComboBox().addActionListener(ivjEventHandler);
	getJButtonAdvanced().addActionListener(ivjEventHandler);
	getJComboBoxAmpUseType().addActionListener(ivjEventHandler);
	getConfigComboBox().addActionListener(ivjEventHandler);
	getTOUComboBox().addActionListener(ivjEventHandler);
	getPasswordTextField().addCaretListener(ivjEventHandler);
	getSenderTextField().addCaretListener(ivjEventHandler);
	getSecurityCodeTextField().addCaretListener(ivjEventHandler);
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
		setPreferredSize(new java.awt.Dimension(407, 497));
		java.awt.GridBagConstraints consGridBagConstraints151 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints152 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints54 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints153 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints55 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints56 = new java.awt.GridBagConstraints();
		consGridBagConstraints54.insets = new java.awt.Insets(5,6,1,7);
		consGridBagConstraints54.gridy = 0;
		consGridBagConstraints54.gridx = 0;
		consGridBagConstraints54.anchor = java.awt.GridBagConstraints.NORTHWEST;
		consGridBagConstraints153.insets = new java.awt.Insets(2,6,1,7);
		consGridBagConstraints153.gridy = 1;
		consGridBagConstraints153.gridx = 0;
		consGridBagConstraints153.anchor = java.awt.GridBagConstraints.NORTHWEST;
		consGridBagConstraints153.fill = java.awt.GridBagConstraints.VERTICAL;
		consGridBagConstraints153.weighty = 0.0D;
		consGridBagConstraints153.ipady = -5;
		consGridBagConstraints56.insets = new java.awt.Insets(1,6,2,7);
		consGridBagConstraints56.ipady = -120;
		consGridBagConstraints56.gridy = 1;
		consGridBagConstraints56.gridx = 0;
		consGridBagConstraints56.anchor = java.awt.GridBagConstraints.NORTHWEST;
		consGridBagConstraints56.fill = java.awt.GridBagConstraints.VERTICAL;
		consGridBagConstraints56.weighty = 1.0D;
		consGridBagConstraints151.insets = new java.awt.Insets(7,6,2,7);
		consGridBagConstraints151.ipady = -7;
		consGridBagConstraints151.gridy = 0;
		consGridBagConstraints151.gridx = 0;
		consGridBagConstraints151.anchor = java.awt.GridBagConstraints.NORTHWEST;
		consGridBagConstraints152.insets = new java.awt.Insets(1,6,8,7);
		consGridBagConstraints152.gridy = 2;
		consGridBagConstraints152.gridx = 0;
		consGridBagConstraints152.anchor = java.awt.GridBagConstraints.NORTHWEST;
		consGridBagConstraints152.fill = java.awt.GridBagConstraints.VERTICAL;
		consGridBagConstraints152.weighty = 0.0D;
		consGridBagConstraints152.ipady = -3;
		consGridBagConstraints55.insets = new java.awt.Insets(3,6,3,7);
		consGridBagConstraints55.ipady = -7;
		consGridBagConstraints55.gridy = 2;
		consGridBagConstraints55.gridx = 0;
		consGridBagConstraints55.anchor = java.awt.GridBagConstraints.NORTHWEST;
		consGridBagConstraints151.fill = java.awt.GridBagConstraints.VERTICAL;
		consGridBagConstraints151.weighty = 0.0D;
		setLayout(new java.awt.GridBagLayout());
		this.add(getIdentificationPanel(), consGridBagConstraints54);
		this.add(getJPanelMCTSettings(), consGridBagConstraints55);
		this.add(getCommunicationPanel(), consGridBagConstraints56);
		setSize(407, 379);
		setMinimumSize(new java.awt.Dimension(407,503));
		setMaximumSize(new java.awt.Dimension(407,503));

		this.setDoubleBuffered(true);
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

	if( getPhysicalAddressTextField().isVisible()
		 && (getPhysicalAddressTextField().getText() == null
		     || getPhysicalAddressTextField().getText().length() < 1) )
	{
		setErrorString("The Address text field must be filled in");
		return false;
	}


	if( getPhysicalAddressTextField().isVisible() ) {
		int deviceType = PAOGroups.getDeviceType( deviceBase.getPAOType() );
        String error = DeviceAddressRange.getRangeMessage( deviceType );
        try{
            int address = Integer.parseInt( getPhysicalAddressTextField().getText() );
            
            // Verify Address is within range
            if( !DeviceAddressRange.isValidRange( deviceType, address ) ) {
               setErrorString(error);
               return false;
            }
            
            //Check address is not already used, user can override and continue
            if( DeviceTypesFuncs.isMCT(deviceType) || DeviceTypesFuncs.isRepeater(deviceType) ) {
              	if (!checkMCTAddresses( address, deviceBase.getPAObjectID() )) {
              		return false;
              	}
            }
              
        	//verify that there are no duplicate physical address for CCUs or RTUs on a dedicated channel
        	LiteYukonPAObject port = ((LiteYukonPAObject)getPortComboBox().getSelectedItem());
        	if(port != null && (! PAOGroups.isDialupPort(port.getType())) && 
        			(DeviceTypesFuncs.isCCU(deviceType) || DeviceTypesFuncs.isRTU(deviceType) )) {
        		if (!checkForDuplicateAddresses(address, deviceBase.getPAObjectID(), port.getLiteID() ) ){
        			return false;
        		}
        	}
        	
        } catch (NumberFormatException nfe) {
            setErrorString(error);
            return false;
        }
    }
	
	String deviceName = getNameTextField().getText();
	if( !isUniquePao(deviceName, deviceBase.getPAOCategory(), deviceBase.getPAOClass(), deviceBase.getPAObjectID())) {
		setErrorString("Name '" + deviceName + "' is already in use.  Device Name must be unique for Category(" + deviceBase.getPAOCategory() + ") and PAOClass(" + deviceBase.getPAOClass() + ")");
		return false;
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

	if( cBase instanceof Repeater900)
		address = new Integer( address.intValue() - Repeater900.ADDRESS_OFFSET );
	else if( cBase instanceof Repeater921)
        address = new Integer( address.intValue() - Repeater921.ADDRESS_OFFSET );
      
   if( cBase instanceof com.cannontech.database.data.device.MCT_Broadcast )
      getPhysicalAddressLabel().setText("Lead Address:");
   else if ( cBase instanceof TwoWayLCR)
	   getPhysicalAddressLabel().setText("Serial Number:");
		
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
    
   if( base instanceof MCTBase )
   {
	   getJPanelMCTSettings().setVisible(true);
	   if(base instanceof MCT400SeriesBase)
	   {
	   		getTOUComboBox().setVisible(false);
	   		getTOULabel().setVisible(false);		
	   }
   }
	
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

	int assignedConfigID = 0;
	if( getConfigComboBox().getModel().getSize() > 0)
		getConfigComboBox().removeAllItems();
		
	getConfigComboBox().addItem( CtiUtilities.STRING_NONE );
	getTOUComboBox().addItem(CtiUtilities.STRING_NONE );
	
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List routes = cache.getAllRoutes();
		java.util.List configs = cache.getAllConfigs();
		
		Integer mctSeriesType = ConfigTwoWay.SERIES_300_TYPE;;
		if(base instanceof MCTBase)
		{					
			for(int j = 0; j < configs.size(); j++)
			{
				//this is a tad disgusting
				if(base instanceof MCT210 || base instanceof MCT213 ||
					base instanceof MCT240 || base instanceof MCT248 ||
					base instanceof MCT250)
						mctSeriesType = ConfigTwoWay.SERIES_200_TYPE;
						
				if(base instanceof MCT400SeriesBase)
						mctSeriesType = ConfigTwoWay.SERIES_400_TYPE;
				
				if(mctSeriesType.compareTo(((com.cannontech.database.data.lite.LiteConfig)configs.get(j)).getConfigType()) == 0)
					getConfigComboBox().addItem( configs.get(j) );
				
				if(((MCTBase) base).hasMappedConfig())
				{
					assignedConfigID = ((MCTBase) base).getConfigID().intValue();
					if( ((com.cannontech.database.data.lite.LiteConfig)configs.get(j)).getConfigID() == assignedConfigID )
						getConfigComboBox().setSelectedItem((com.cannontech.database.data.lite.LiteConfig)configs.get(j));
				}
			}
			if(! ((MCTBase) base).hasMappedConfig())
			{
				getConfigComboBox().setSelectedItem(CtiUtilities.STRING_NONE);
			}
			
			if(base instanceof MCT400SeriesBase)
			{
				/*for(int x = 0; x < tous.size(); x++)
				{
					getTOUComboBox().addItem( tous.get(x) );
					if(((MCTBase) base).hasTOUSchedule())
					{
						if(((MCTBase) base).getTOUScheduleID().intValue() == ((com.cannontech.database.data.lite.LiteTOUSchedule)tous.get(x)).getLiteID())
							getTOUComboBox().setSelectedItem(tous.get(x));
					}
				}*/
			}
		}
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

private void setGridBaseValue ( GridAdvisorBase gBase, int intType )
{
    getRouteLabel().setVisible(false);
    getRouteComboBox().setVisible(false);
    getJLabelCCUAmpUseType().setVisible(false);
    getJComboBoxAmpUseType().setVisible(false);

    getPortLabel().setVisible(true);
    getPortComboBox().setVisible(true);
    getPostCommWaitLabel().setVisible(false);
    getPostCommWaitSpinner().setVisible(false);
    getDialupSettingsPanel().setVisible(false);
    getWaitLabel().setVisible(false);

    if( getRouteComboBox().getModel().getSize() > 0 )
        getRouteComboBox().removeAllItems();
    
    int portID = 0;
    if( gBase.getDeviceDirectCommSettings().getPortID() != null )
        portID = gBase.getDeviceDirectCommSettings().getPortID().intValue();
    
    //Load the combo box
    IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
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
            }
        }
    }
    
    getPhysicalAddressLabel().setVisible(true);
    getPhysicalAddressLabel().setText("Serial Number:");
    getPhysicalAddressTextField().setVisible(true);
    getPhysicalAddressTextField().setText( gBase.getDeviceAddress().getMasterAddress().toString() );
    getPasswordLabel().setVisible(false);
    getPasswordTextField().setVisible(false);
    getSlaveAddressLabel().setVisible(false);
    getSlaveAddressComboBox().setVisible(false);
    
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
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
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
	

	//regardless of our type, we should set the advanced settings of the port
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
		
		if(rBase instanceof WCTPTerminal)
		{
			getSenderLabel().setVisible(true);
			getSenderTextField().setVisible(true);
			getSecurityCodeLabel().setVisible(true);
			getSecurityCodeTextField().setVisible(true);
			
			getSenderTextField().setText(((PagingTapTerminal)rBase).getDeviceTapPagingSettings().getSender());
			getSecurityCodeTextField().setText(((PagingTapTerminal)rBase).getDeviceTapPagingSettings().getSecurityCode());
		}
		else if(rBase instanceof SNPPTerminal)
		{
			getSenderLabel().setText("Login: ");
			getSenderLabel().setVisible(true);
			getSenderTextField().setVisible(true);
			getSecurityCodeLabel().setText("Password: ");
			getSecurityCodeLabel().setVisible(true);
			getSecurityCodeTextField().setVisible(true);
			getPasswordLabel().setVisible(false);
			getPasswordTextField().setVisible(false);
			
			getSenderTextField().setText(((PagingTapTerminal)rBase).getDeviceTapPagingSettings().getSender());
			getSecurityCodeTextField().setText(((PagingTapTerminal)rBase).getDeviceTapPagingSettings().getSecurityCode());
		}
		
		String password;
		if(rBase instanceof WCTPTerminal)
			password = ((PagingTapTerminal)rBase).getDeviceTapPagingSettings().getPOSTPath();
		else
			password = ((IEDBase)rBase).getDeviceIED().getPassword();
      
		if( CtiUtilities.STRING_NONE.equalsIgnoreCase(password)
          || "None".equalsIgnoreCase(password) //keep the old (none) value valid
          || "0".equalsIgnoreCase(password) )  //keep the old '0' value valid
      {
			getPasswordTextField().setText( "" );
      }
		else
			getPasswordTextField().setText( password );


		if( rBase instanceof Schlumberger 
			 || intType == PAOGroups.ALPHA_PPLUS
			 || intType == PAOGroups.TRANSDATA_MARKV
			 || rBase instanceof KV)
		{
			getSlaveAddressLabel().setVisible(true);
			getSlaveAddressComboBox().setVisible(true);

			String slaveAddress = ((IEDBase)rBase).getDeviceIED().getSlaveAddress();
			getSlaveAddressComboBox().setSelectedItem(slaveAddress);
		}
        else if( rBase instanceof RTM )
		{
			getPhysicalAddressLabel().setVisible(true);
			//getPhysicalAddressLabel().setText("RTM Address:");
            getPhysicalAddressLabel().setText("Physical Address:");
			getPhysicalAddressTextField().setVisible(true);
			ivjPhysicalAddressTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 15) );
			getPhysicalAddressTextField().setText( ((IEDBase)rBase).getDeviceIED().getSlaveAddress() );
			
			getSlaveAddressLabel().setVisible(false);
			getSlaveAddressComboBox().setVisible(false);
			
			getPasswordLabel().setVisible(false);
			getPasswordTextField().setVisible(false);

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
      getPhysicalAddressTextField().setText( ((DNPBase)rBase).getDeviceAddress().getMasterAddress().toString() );
      
      getSlaveAddressLabel().setVisible(true);
      getSlaveAddressComboBox().setVisible(true);
      
      //create a new editor for our combobox so we can set the document
      getSlaveAddressComboBox().setEditable( true );
      getSlaveAddressComboBox().removeAllItems();
      com.cannontech.common.gui.util.JTextFieldComboEditor editor = new com.cannontech.common.gui.util.JTextFieldComboEditor();
      editor.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(-999999999, 999999999) );
      editor.addCaretListener(this);  //be sure to fireInputUpdate() messages!

      getSlaveAddressComboBox().setEditor( editor );
      getSlaveAddressComboBox().addItem( ((DNPBase)rBase).getDeviceAddress().getSlaveAddress() );

      
      getPostCommWaitSpinner().setValue( ((DNPBase)rBase).getDeviceAddress().getPostCommWait() );
      
      getPasswordLabel().setVisible(false);
      getPasswordTextField().setVisible(false);
   }
   else if( rBase instanceof Series5Base )
	{
		getPhysicalAddressLabel().setVisible(true);
		getPhysicalAddressLabel().setText("Address:");
		getPhysicalAddressTextField().setVisible(true);
		getPhysicalAddressTextField().setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(1, 127) );
		getPhysicalAddressTextField().setText( ((Series5Base)rBase).getSeries5().getSlaveAddress().toString() );
      
		getSlaveAddressLabel().setVisible(false);
		getSlaveAddressComboBox().setVisible(false);
		
		getControlInhibitCheckBox().setVisible(true);
		ivjControlInhibitCheckBox.setText("Disable Verification");
		if(((Series5Base)rBase).getVerification().getDisable().compareTo("Y") == 0)
			getControlInhibitCheckBox().setSelected(true);
		else
			getControlInhibitCheckBox().setSelected(false);
      
		getPostCommWaitSpinner().setValue( ((Series5Base)rBase).getSeries5().getPostCommWait() );
      
      	getPasswordLabel().setVisible(false);
		getPasswordTextField().setVisible(false);
	}
	else if( rBase instanceof RTCBase )
	{
		getPhysicalAddressLabel().setVisible(true);
		getPhysicalAddressLabel().setText("Physical Address:");
		getPhysicalAddressTextField().setVisible(true);
		getPhysicalAddressTextField().setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 15) );
		getPhysicalAddressTextField().setText( ((RTCBase)rBase).getDeviceRTC().getRTCAddress().toString() );
			
      	getSlaveAddressLabel().setText("Listen Before Talk: ");
		getSlaveAddressLabel().setVisible(true);
	  	getSlaveAddressComboBox().setVisible(true);
      
	  	//create a new editor for our combobox so we can set the document
	  	getSlaveAddressComboBox().setEditable( false );
	  	getSlaveAddressComboBox().removeAllItems();
	  	getSlaveAddressComboBox().addItem( RTCBase.LBT3 );
		getSlaveAddressComboBox().addItem( RTCBase.LBT2 );
		getSlaveAddressComboBox().addItem( RTCBase.LBT1 );
		getSlaveAddressComboBox().addItem( RTCBase.LBT0 );
		getSlaveAddressComboBox().setSelectedItem(RTCBase.getLBTModeString(((RTCBase)rBase).getDeviceRTC().getLBTMode()));

		getPostCommWaitSpinner().setVisible(false);
		getPostCommWaitLabel().setVisible(false);
		//getPostCommWaitSpinner().setValue( ((RTCBase)rBase).getDeviceRTC().getPostCommWait() );
		getWaitLabel().setVisible(false);
		getPasswordLabel().setVisible(false);
		getPasswordTextField().setVisible(false);
		
		getControlInhibitCheckBox().setVisible(true);
		ivjControlInhibitCheckBox.setText("Disable Code Verification");
		if(((RTCBase)rBase).getDeviceRTC().getDisableVerifies().compareTo("Y") == 0)
			getControlInhibitCheckBox().setSelected(true);
		else
			getControlInhibitCheckBox().setSelected(false);
		
	} else if( rBase instanceof CCU721) {
		getPhysicalAddressLabel().setVisible(false);
		getPhysicalAddressLabel().setText("Master Address:");
		getPhysicalAddressTextField().setVisible(false);
		getPhysicalAddressTextField().setText( ((CCU721)rBase).getDeviceAddress().getMasterAddress().toString() );

		getSlaveAddressLabel().setVisible(true);
		getSlaveAddressComboBox().setVisible(true);
      
		//create a new editor for our combobox so we can set the document
		getSlaveAddressComboBox().setEditable( true );
		getSlaveAddressComboBox().removeAllItems();
		JTextFieldComboEditor editor = new JTextFieldComboEditor();
		editor.setDocument( new LongRangeDocument(0L, 128L));
      	editor.addCaretListener(this);  //be sure to fireInputUpdate() messages!
      	getSlaveAddressComboBox().setEditor( editor );
      	getSlaveAddressComboBox().addItem( ((CCU721)rBase).getDeviceAddress().getSlaveAddress() );

		getPostCommWaitSpinner().setVisible(false);
		getPostCommWaitLabel().setVisible(false);
		getWaitLabel().setVisible(false);
		getPasswordLabel().setVisible(false);
		getPasswordTextField().setVisible(false);
		
	}else {
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
	deviceBase = (DeviceBase)val;

	int deviceType = PAOGroups.getDeviceType( deviceBase.getPAOType() );
	String typeStr = deviceBase.getPAOType();
	//Override defalut type string for TapTerminal
	if (deviceType == PAOGroups.TAPTERMINAL) {
		typeStr = PAOGroups.STRING_TAP_TERMINAL[2];
	}
	getTypeTextField().setText(typeStr);
	getNameTextField().setText(deviceBase.getPAOName());
	
	CtiUtilities.setCheckBoxState(getDisableFlagCheckBox(), deviceBase.getPAODisableFlag());
	CtiUtilities.setCheckBoxState( getControlInhibitCheckBox(), deviceBase.getDevice().getControlInhibit());

	//	CCU's cannot have addresses larger than 128
	if (DeviceTypesFuncs.isCCU(deviceType)) {
		getPhysicalAddressTextField().setDocument( new LongRangeDocument(0L, 128L) );
	}

	//This is a bit ugly
	//The address could come from one of three different types of
	//devices even though they all have one
	//Note also getValue(DBPersistent)
	
	if (val instanceof CarrierBase ) {
		setCarrierBaseValue( (CarrierBase) val );
	}
	else if (val instanceof IDLCBase ) {
		setIDLCBaseValue( (IDLCBase) val );
	}
	else {
		if (deviceBase.getPAOClass().equalsIgnoreCase(DeviceClasses.STRING_CLASS_VIRTUAL) ) {
			getCommunicationPanel().setVisible( false );
		}

		getPhysicalAddressLabel().setVisible(false);
		getPhysicalAddressTextField().setVisible(false);
	}

	if( deviceBase.getPAOClass().equalsIgnoreCase(DeviceClasses.STRING_CLASS_GROUP) ) {
		getDisableFlagCheckBox().setVisible(false);
	}
	else {
		getDisableFlagCheckBox().setVisible(true);
	}

	getDialupSettingsPanel().setVisible(false);
	
	if (val instanceof RemoteBase ) {
		setRemoteBaseValue( (RemoteBase)val, deviceType );		
	} else if (val instanceof GridAdvisorBase ) {
        setGridBaseValue( (GridAdvisorBase)val, deviceType );
    } else {
		setNonRemBaseValue( val );		
	}

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
	 * This method initializes jPanelMCTSettings
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanelMCTSettings() {
		if(jPanelMCTSettings == null) {
			jPanelMCTSettings = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints51 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints50 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints52 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints53 = new java.awt.GridBagConstraints();
			consGridBagConstraints52.insets = new java.awt.Insets(4,3,0,21);
			consGridBagConstraints52.ipadx = -9;
			consGridBagConstraints52.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints52.weightx = 1.0;
			consGridBagConstraints52.gridy = 1;
			consGridBagConstraints52.gridx = 1;
			consGridBagConstraints52.anchor = java.awt.GridBagConstraints.NORTHWEST;
			consGridBagConstraints51.insets = new java.awt.Insets(4,33,1,2);
			consGridBagConstraints51.gridy = 1;
			consGridBagConstraints51.gridx = 0;
			consGridBagConstraints53.insets = new java.awt.Insets(1,3,4,21);
			consGridBagConstraints53.ipadx = -9;
			consGridBagConstraints53.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints53.weightx = 1.0;
			consGridBagConstraints53.gridy = 0;
			consGridBagConstraints53.gridx = 1;
			consGridBagConstraints53.anchor = java.awt.GridBagConstraints.NORTHWEST;
			consGridBagConstraints51.anchor = java.awt.GridBagConstraints.NORTHWEST;
			consGridBagConstraints50.insets = new java.awt.Insets(1,33,5,2);
			consGridBagConstraints50.gridy = 0;
			consGridBagConstraints50.gridx = 0;
			consGridBagConstraints50.anchor = java.awt.GridBagConstraints.NORTHWEST;
			jPanelMCTSettings.setLayout(new java.awt.GridBagLayout());
			jPanelMCTSettings.add(getConfigLabel(), consGridBagConstraints50);
			jPanelMCTSettings.add(getTOULabel(), consGridBagConstraints51);
			jPanelMCTSettings.add(getTOUComboBox(), consGridBagConstraints52);
			jPanelMCTSettings.add(getConfigComboBox(), consGridBagConstraints53);
			jPanelMCTSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "MCT Additional Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), java.awt.Color.black));
			jPanelMCTSettings.setName("JPanelMCTSettings");
			jPanelMCTSettings.setPreferredSize(new java.awt.Dimension(394,81));
			jPanelMCTSettings.setMinimumSize(new java.awt.Dimension(0,0));
			jPanelMCTSettings.setMaximumSize(new java.awt.Dimension(394,81));
			jPanelMCTSettings.setVisible(false);
		}
		return jPanelMCTSettings;
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
