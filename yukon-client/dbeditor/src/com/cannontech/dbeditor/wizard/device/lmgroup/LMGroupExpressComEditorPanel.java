package com.cannontech.dbeditor.wizard.device.lmgroup;
import javax.swing.JCheckBox;

import com.cannontech.database.db.device.lm.IlmDefines;

/**
 * This type was created in VisualAge.
 */

public class LMGroupExpressComEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	public static final String STRING_NEW = "(new)";
	private javax.swing.JLabel ivjJLabelFeedAddress = null;
	private javax.swing.JLabel ivjJLabelGEOAddress = null;
	private javax.swing.JLabel ivjJLabelPROGAddress = null;
	private javax.swing.JLabel ivjJLabelSPID = null;
	private javax.swing.JLabel ivjJLabelSplinter = null;
	private javax.swing.JLabel ivjJLabelSubAddress = null;
	private javax.swing.JLabel ivjJLabelUserAddress = null;
	private javax.swing.JLabel ivjJLabelZipAddress = null;
	private javax.swing.JPanel ivjJPanelAddress = null;
	private javax.swing.JTextField ivjJTextFieldFeedAddress = null;
	private javax.swing.JTextField ivjJTextFieldGeoAddress = null;
	private javax.swing.JTextField ivjJTextFieldProgAddress = null;
	private javax.swing.JTextField ivjJTextFieldSPIDAddress = null;
	private javax.swing.JTextField ivjJTextFieldSplinter = null;
	private javax.swing.JTextField ivjJTextFieldSubAddress = null;
	private javax.swing.JTextField ivjJTextFieldUserAddress = null;
	private javax.swing.JTextField ivjJTextFieldZipAddress = null;
	private javax.swing.JComboBox ivjJComboBoxFEED = null;
	private javax.swing.JComboBox ivjJComboBoxGEO = null;
	private javax.swing.JComboBox ivjJComboBoxPROG = null;
	private javax.swing.JComboBox ivjJComboBoxSPID = null;
	private javax.swing.JComboBox ivjJComboBoxSUB = null;
	private javax.swing.JCheckBox ivjJCheckBoxFEED = null;
	private javax.swing.JCheckBox ivjJCheckBoxGEO = null;
	private javax.swing.JCheckBox ivjJCheckBoxPROG = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay1 = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay2 = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay3 = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay4 = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay5 = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay6 = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay7 = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay8 = null;
	private javax.swing.JCheckBox ivjJCheckBoxSPID = null;
	private javax.swing.JCheckBox ivjJCheckBoxSPLINTER = null;
	private javax.swing.JCheckBox ivjJCheckBoxSUB = null;
	private javax.swing.JCheckBox ivjJCheckBoxUSER = null;
	private javax.swing.JCheckBox ivjJCheckBoxZIP = null;
	private javax.swing.JPanel ivjJPanelAddressUsage = null;
	private javax.swing.JPanel ivjJPanelRelayUsage = null;
	private javax.swing.JCheckBox ivjJCheckBoxSerial = null;
	private javax.swing.JTextField ivjJTextFieldSerialAddress = null;
	private javax.swing.JCheckBox ivjJCheckBoxLOAD = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMGroupExpressComEditorPanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJComboBoxSPID()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxGEO()) 
		connEtoC3(e);
	if (e.getSource() == getJComboBoxSUB()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxFEED()) 
		connEtoC5(e);
	if (e.getSource() == getJComboBoxPROG()) 
		connEtoC6(e);
	if (e.getSource() == getJCheckBoxSerial()) 
		connEtoC12(e);
	if (e.getSource() == getJCheckBoxRelay5()) 
		connEtoC13(e);
	if (e.getSource() == getJCheckBoxRelay6()) 
		connEtoC14(e);
	if (e.getSource() == getJCheckBoxRelay7()) 
		connEtoC15(e);
	if (e.getSource() == getJCheckBoxRelay8()) 
		connEtoC16(e);
	if (e.getSource() == getJCheckBoxRelay4()) 
		connEtoC17(e);
	if (e.getSource() == getJCheckBoxRelay3()) 
		connEtoC18(e);
	if (e.getSource() == getJCheckBoxRelay2()) 
		connEtoC19(e);
	if (e.getSource() == getJCheckBoxRelay1()) 
		connEtoC20(e);
	if (e.getSource() == getJCheckBoxSPLINTER()) 
		connEtoC21(e);
	if (e.getSource() == getJCheckBoxPROG()) 
		connEtoC22(e);
	if (e.getSource() == getJCheckBoxUSER()) 
		connEtoC23(e);
	if (e.getSource() == getJCheckBoxZIP()) 
		connEtoC24(e);
	if (e.getSource() == getJCheckBoxFEED()) 
		connEtoC25(e);
	if (e.getSource() == getJCheckBoxSUB()) 
		connEtoC26(e);
	if (e.getSource() == getJCheckBoxGEO()) 
		connEtoC27(e);
	if (e.getSource() == getJCheckBoxLOAD()) 
		connEtoC28(e);
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
	if (e.getSource() == getJTextFieldZipAddress()) 
		connEtoC10(e);
	if (e.getSource() == getJTextFieldSPIDAddress()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldGeoAddress()) 
		connEtoC7(e);
	if (e.getSource() == getJTextFieldFeedAddress()) 
		connEtoC8(e);
	if (e.getSource() == getJTextFieldProgAddress()) 
		connEtoC9(e);
	if (e.getSource() == getJTextFieldSerialAddress()) 
		connEtoC11(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JTextFieldSPIDAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
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
 * connEtoC10:  (JTextFieldZipAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC11:  (JTextFieldSerialAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC12:  (JCheckBoxSerial.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jCheckBoxSerial_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxSerial_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (JCheckBoxRelay5.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
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
 * connEtoC14:  (JCheckBoxRelay6.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
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
 * connEtoC15:  (JCheckBoxRelay7.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
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
 * connEtoC16:  (JCheckBoxRelay8.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
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
 * connEtoC17:  (JCheckBoxRelay4.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.ActionEvent arg1) {
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
 * connEtoC18:  (JCheckBoxRelay3.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.ActionEvent arg1) {
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
 * connEtoC19:  (JCheckBoxRelay2.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JComboBoxSPID.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jComboBoxSPID_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxSPID_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC20:  (JCheckBoxRelay1.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.awt.event.ActionEvent arg1) {
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
 * connEtoC21:  (JCheckBoxSPLINTER.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		if(getJCheckBoxSPLINTER().isSelected())
		{
			getJCheckBoxLOAD().setSelected(false);
			
		}
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
 * connEtoC22:  (JCheckBoxPROG.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC22(java.awt.event.ActionEvent arg1) {
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
 * connEtoC23:  (JCheckBoxUSER.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC23(java.awt.event.ActionEvent arg1) {
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
 * connEtoC24:  (JCheckBoxZIP.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC24(java.awt.event.ActionEvent arg1) {
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
 * connEtoC25:  (JCheckBoxFEED.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC25(java.awt.event.ActionEvent arg1) {
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
 * connEtoC26:  (JCheckBoxSUB.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC26(java.awt.event.ActionEvent arg1) {
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
 * connEtoC27:  (JCheckBoxGEO.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC27(java.awt.event.ActionEvent arg1) {
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
 * connEtoC28:  (JCheckBoxLOAD.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC28(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JComboBoxGEO.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jComboBoxGEO_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxGEO_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JComboBoxSUB.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jComboBoxSUB_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxSUB_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JComboBoxFEED.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jComboBoxFEED_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxFEED_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JComboBoxPROG.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jComboBoxPROG_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxPROG_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (JTextFieldGeoAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
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
 * connEtoC8:  (JTextFieldFeedAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
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
 * connEtoC9:  (JTextFieldProgAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
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
 * Insert the method's description here.
 * Creation date: (6/5/2002 10:46:06 AM)
 * @return com.cannontech.database.db.device.lm.LMGroupExpressComAddress
 * @param combo javax.swing.JComboBox
 * @param textField javax.swing.JTextField
 * @param type java.lang.String
 */
private com.cannontech.database.db.device.lm.LMGroupExpressComAddress createAddress(javax.swing.JComboBox combo, javax.swing.JTextField textField, String type) 
{
		
	if( textField.getText() == null || textField.getText().length() <= 0 )
	{
		return com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS;
	}
	else
	{
		Object item = combo.getSelectedItem();
		
		if( item != null )
		{
			com.cannontech.database.db.device.lm.LMGroupExpressComAddress address = null;
			
			if( item instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
			{
				address = (com.cannontech.database.db.device.lm.LMGroupExpressComAddress)item;
			}
			else   //a new address is created				
				address = new com.cannontech.database.db.device.lm.LMGroupExpressComAddress(type);
			
			Integer addValue = new Integer(textField.getText());
			
			if( address.equals(com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS)
				 || addValue.equals(com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS.getAddress()) )				
			{
				

				if( !addValue.equals(com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS.getAddress()) )
				{
					//we have a none address selected but have a non zero id typed
					com.cannontech.database.db.device.lm.LMGroupExpressComAddress 
							addressTemp = new com.cannontech.database.db.device.lm.LMGroupExpressComAddress(type);
							
					addressTemp.setAddress( new Integer(textField.getText()) );
					addressTemp.setAddressName( textField.getText() ); //just use the address as the name
					
					return addressTemp;
				}
				else
					return com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS;				
				
			}
			else
			{
				address.setAddress( new Integer(textField.getText()) );

				address.setAddressName( 
						(item.toString().length() <= 0 || item.toString().equalsIgnoreCase(STRING_NEW) 
							? address.getAddress().toString() 
							: item.toString()) );
				
				return address;
			}
			
		}
		else
		{		
			com.cannontech.database.db.device.lm.LMGroupExpressComAddress addressTemp = new com.cannontech.database.db.device.lm.LMGroupExpressComAddress(type);
			addressTemp.setAddress( new Integer(textField.getText()) );
			addressTemp.setAddressName( textField.getText() ); //just use the address as the name

			return addressTemp;
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
	D0CB838494G88G88GAC0A09AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16C3D8FDCD5C57ABF4E9695EDEC59469695EDEC59C645EED436D1D1CBC5C5E5C5C51B15DBD4D4D4D4D4D4D4D4D77EF4D1C1A0D1B1B1D1D1B1918179A722222222D1DC730AE8E820DC053C408DAE0AC679FDE666BC4F79F34F3DF069E33F4F6B777A78FABDDE66BD4F5F19B9F3664C1919E34819671FBAA9A947E01814EA107ED5CD027F2E1EE4E8FB5BF4B1E2E921CDC603625FFC20EC4369DF2540EFAE64
	15F4EDB7E699FA9626C13A885251CB369BE7415F538D679D8CBC848A1D5871043C17BBCE19675ACE71C234E364694D7D6970BB8D2884B0B310BFD016E1DFFF2E048FC2FA1A74B723C9C986C36B9D0C670A2FF395F83444D782F975C02DF7B21EE37DF9D27ED840C200F281AF3AD37056436FE46E5DE9B55592DF6B398106787B0B12B218AD05FE243112EC38741BEF98478BA259F016398EFEEBCD078F2DBD505BDDFC203D27273747EEB950D9EC696832777695F7D80EF49D3094DB3BEBEA9A3F595257BBE0EB36
	7475378373A64EE0EA6F31D82B6FE2FE5F5B1546F542BFC807F1EC5D0F2C2C0660EF9F20403B5077C7B2EAA55FFC8E879822AE1F67547734B061F3D1186CFB44FD9D299343187E4BF8DDB0B9AB24478209383A3FB5B9092BE33F9459512E636DFD9DF20007D8F0946526C1DB1F253FFF23CE1C2F76E31C79F7E31C89654C0F96C85B003C887FF785437DA19D8C94F1B7463FE689465F7273CD8643138F25E4BA09BF66EE91F756BDF27C75A19D2933F9DB3C25003527C6483381F98285G058345G4D6157404042
	2F60F77DFEBB84535D5B335EE65BE06D687B263743E2053FEBEAA0C7423E339C686D6BB098C4BB9F5FD524638F1261DEF59BE7A4E25BE540F699785DBF8DF1E7BC1C122A935B38470B13F2A49D05774A31455E4B4A2D4A4A4A2D817EE6F25FG897CA047CB42E5DC3403CD3C1D31F4A46455BD00655CB28DFDE9CC394C10D53ABD4928630B6F1D1B13829F94BE04BDA87BA24A58E7B1AB63B840539FEAB296G558235828DBD04E57C6F05AB07AB632D507E7AFA5A2DBB86765B5A7B6C9B863A2C9D16BE292C837A
	DD15359E8939FF39ADA74AA5E025320EA65EE052AD17098195C93E8F097288FAC8AE97E17BCD2736A39EF2EC9B53288E2D7C1A08FEC854E1B261C7B89E2F40C55DFE422FAD16CEF938495889A47857D5B1FC7C4302FF95696960F8C3040C8BBDF7F37DAC9D86F9E39EEDB20A7AADABE47DEDEBE413F190E88CD0GD0B8D0B4D0C2A45671140295A33C0ECC0A7A9532F7954C04DF4B203D2FFDEBCF3F3D3D670025393D2F2BFD3F5552EF10F8469730F25D2F12FB04DF5F87404696CB0F252F5D5E555B835A5B3BAD
	50187A854FD385590C274F42F34DDD5D96B050EDB3205E678B26432F353D5F3E535651EE37489CA27F558256A60E34330E9D943032FB1C15578B3CBF4F053F4B9F47327AE1A9433CA1BD76F1AC974B224F6177EBE85C96EBCDCDFD578166C6FB5FB1EC23EB2C3E709B8C7CF57F01F285B2BF51E40C84B2CF00DF2016A7619E77946884EAFB9AECCF047B9ED067FF81FF86E400CC4FB699A782F5C4B5996300061EEBB226BD8FFCC075AFB4994783192301872863C5E8A3C076171A0C09C0DEAF034E18A623AFD05E
	ABE0872870D5389F3D0A7D449BADF8BD54E77C43D074F8136E7540287CB551369BDF53DE8FD69E279DF09F20F1C011C071C0B931D006C0E6A0BB108F50B820C820F8201C57A19F488CE48772819A879489948F147386648319016CC0BEC06300A2016201F25E04FCA0B3109D4887E89CD0A4D0BCD0CE9C0EDD3E7E31ADC99D8F2B93764B6A041527B2AF64F9476B185593ABB707B146D322DCD9DDA9F1D61F78B72B87FE4FFBD27C323AD07232BAE2752842A64A6990696FC29D7BC82C4EDD75FF2C9D69612CBD72
	BE6AC57DFC56D65D69E79931F62A0733EBA052CDFD6CDAF0573EAB6A102EA1F6CDA0462ED1FE8D3CA02EB53C1E286EB5F55B21D3D66CFA93ED6DA5073616EA106FC14BB5F2ABF4FCE47D56B7BA788C202D86B1BED7623394FF2F17FE33255FAD9A5E0DC0ABB558EC455FF3245F2FDD14611AE14AF82193FC2A743B5AC9FE46A86AF329931C55C5261B3A660E423EABDA82348CE81382CB16FECB019681D5GD5A97267E9F42C52513BCE2A33EC4303DF31FBFBB974F3A6A0DB9C5E1B7C97659B78BDFBC3FB3F856F
	52CDFDF00335F448F7AA41FB49E2D6CE16A3169EFB3D45E6696900BF7A25FB6458452C4DCA776F033D863C37DE4DF1B61E5B88B758FE420396E78B9CF4A8C783189F4C735B6D763E2E7D83F64B8E0B5D5E55F3C8168F653E742B060A7DD22C8FAFE331FAC10C7EC0C1946B56A56CFA417BF0E3EF6F61811B190DB0247B79E7F6F66F8E837E09F4BF682BE04F360637B0BD4453F2FECAC50B0BFAE5F23F25DD889BF3DD5AC87E8D7AE59B1B2AEC122ED489E375B655A07A98F63DE59AC4DF415AAA6BDFE6C9ED0B15
	6DDC29BD327EE62154DE59353BD4EA572CAFDBA13565150A36ACEC7F48ED97C2D9190086017C0142016200F25F06F191D0A7509820E020A82034F71A0CA5C0ADC09EC081C091C089C07971CD4686A09B10AFD0A8D08CD0563BCD46EA2036F7317E9E28881DA41723B6F6E55C53C798771291446D2AEF7BBF9AED9BDEDB692AAE157518B142FA1CB1CCBDE21F6B6C5E72FF64BE093E3A3650793DE6E3217E3DE6EB217E3D262950753DE4D7215E3DC4646DABE46D2877E80F625EF42010BD4F9D686D6931709BA409
	98445C5A9BE696C7D7CF17FDD7D747A10BFDC757F10B419074AE3B47FC481F993D60EF3FF771596A68C2FC36AAB8FE15A174072F12746603FC2F581A947F2E381F243DA7BFDB891FBA1632FB1E45EA6906025A58BB50E397780F7CF9D4427B13B9F66E3CA1A6DB38C3383F3E23034F64F5DA8E9C5E50BBD854E537F497337FF65861E1F8E3E7FB4FA1CBC7610F6DC75A0B5B0F5A0BF91D94EF057CCD6C2F25794B172F4DF7BE75D9F4302B4F323547B6A0BDDC97AEEDDEDAAA7CE8D8F71C7B41EB6B777722CC7493
	538D73EBFF61FE54F79D696A7F7F24C0D69A501749BF5B39AF5FD92C6D47CAFFC7974A057DE31FA96C177D0E76AB047D732A9576D77E0E76AB057DB35A86E47B653F67B5D1CDEDE073626B398F5B507C8ECBDFD73BD5EA16C56D7C2AAD96BFA6CB5F415E3EEEC7E756736C113973232E219F56EBACB4BAB7319AA228C27FAB0F0A6BE91B997A684DDD96EB07BA0083B06A3797737F2578196241627E2350ADCB3AB7325CD19726DC16AD46FF2A4BF2EBCF87DC669D836D565FD8103AAFC06422146D38DB0EA5687B
	142C4BA45FFB3B7777326AB1ED2DBFC9DEABAC386BEFA97A7B51A4FF353FDBB6B51DDCF741003B5E1689DFAF32E8FC5D31F343C9AE5A1D9B5C75F52570755386CDBB583CE953C9EE874C023B5E16E3BB583AD0632F693B26ADA757DFE641DDFFAB505FA5DB06B47D551EAE5B7FC0E7256F33CEF735CA78F15BF107FE93AE475F5311CA6AB7DF7CD9DBA6DBAC3D3F27ABD5420FB5C6ED19EC36D8BAFECFC764FB49E3ABFE5578E26A6BBD74FB7AD23A8AFD19E077B8D9775811FBD3A2FC796F0AAB7546ABA7777EAA
	EC38FD2FAA154A6F1FC7FFD67B4C87FC95A745E73D22AB137CD83AD04F0F4A5F4D0F15129F0733747C287A5D7CA847715AACBDBF2AFFB7BFAAA4BF1E6B5673237CF77323D27263D9031E9FABFFB7BF2AA4BF8E266979D1763B79D1AD79911F2E67C7696F65C759AA490F8387FE5174F926462D5F1AB7FD773BF9D2C277250FFFBD267665240D37747CA8A5BFCCF59ABFF66E789D4B230C7C187753205A0FBDDBCD3F1B9B52187D49FCCDD91C34B13B1E9365D4964FE6E94A62E4BD676839A14D69F467E8FCB8D94F
	05FABEC873BAFFB926290F4626753FDFFD30F785625DE452F2B6CFDB78DE133196487C9E3E1BFCE9A94B47F713528B580D3DD62B1823053C716F317963BB2DB90AB9E16BFBE2FDEDB07C0A7C0D5DFA79B96FE3FE18EEFEA165EF6A514B2F257CFBF473CD143F25D7AF3F037243F57387A9FF2BCDAF5F1B726F574D77277C2FFF524B9FC77963F57343A97F1BBE3D7C09147F10EEFE9C65B7756B6527A4E0FE04EEFEAE65EF376B6597D17E233A795328FDFCB2CC7ED7832E731B0749BFC5B73F0E7CCBBE2217EF26
	FCCF5DFCD9FF50B079930749CFBB6ABA3FFC187C36E1723D07498F9EA6BFFA187C2CC15779354364F78E136FB3CCFE68B079314364679EF316AF7A39473E4A025FFE4BCF83161E8396367862EB4BB1DC277955671A3C66F66B003C7E73792F58FBDC2BE25D06409FDF267F6E6B69E5EC2D46E1527F6C324F8CEC9D2750A95E558535B31D363E2E6E763EE3ECA10A348FE12EDD398F21935AAF5B3390G3FA1C0621E9231112D0BF5F5B3707900697AD7956ED7088459D820748F043E468FC459883E635548978569
	048FE43E5C8F15FC452B102F8D70418FE5BEDF955F73449785F80202AF77A3A55F79B5248F70410F947AD4FC5BD613BE409300F293851FB9D14917D10BFCF6403D931D715DC1FC010007B9659BD803FC5100A7BA65DB3A967972BEEEB216FF6C0C6F3F44E7869E2BD33EF357A11FB7700C858A7BD87025FC22645BD907FCA640BBBF1179547AA62FC7BEAF6071F74AF79B7105824F8427FCBFEDC03E8460497A54995F220D48D782BC8DCE791EA43ECE6071F04A7727FA6473871EE027FC6BB7A15F846009F34A77
	AE71E599CC46C2A0FD3E2BB6A3DF837034B9655B3B8579BC004757A9DFF683728583CF04D33E3B09AF8EF852A6B963BB3695798A01274EA95F72EDF43D814F20D33E67084F97F80200A2983F02AFAB4944627891775B2CA02C10E36FC007B7A79577B4FC13255C8F95165D72195EBA83FEDD008E5E8E24DFF92F47F745FC7D82603EC022DF5DDD24DCD7373E3F3F77C097DFEAAF754FE94B15EB90841631FC8A7C42D8395F3E352743B208FD7C74654CD73696DF0225FE7A1AE5ECAD29F6EF0168EAF159624623FC
	0CEE6E5D585BE1E9005B44C6DA22515CE5B98A72E2FF5287D6AC2FC008C1144DAF1B157B2DC2B22B1226BAA91BE829CCD212653219F8EBC72A50FD3C9BF557C33E40BEA2EC0830219E44469176B1E131047D5A0BD8C18AE21F9256CA18078DB19F42A67D0458F842CEA1ACA5953164BE44EA8973A44CCED8AA6D6B8AA46CF4422289B35A914B7B8CB1AF424C045DBC0018B7E1B5C7908B83CC547D05B5ECFD045EDE35833CB1949F9A68AA9E30F7314D956D76F67E7045EF3FD2FD1F41EDB3DD0920D36CBB7B5BD1
	3E9E56E8B2B682F5G09BDB04F723DB10CF78830F1C0E2DC7364946538A696701CB47099C8FAE7DF4D669F5CFF1E44763DE3BEEBFF3F65BD2CE84717CD2F13FC9C7F4F4B8C35A7F21374764DF9E5AEC8EACC936D571EA637633646664FEC7BAC525E414B2B31ACC201A7A18D4BE2C70ADED914CC467A2A6ED82D0C457D173452FE24DD2B50FEBD689E1A0C766313746C0FA37BD78D286C3B7F62D532BF35866D0703EE439434FF5BF25D36B0856DCF7BC9D51723F817AA6C7F1B6CB702EECF327FFE151E7D9032FF
	E32E4A7EA85E0D8A7B7D2B51FE8468CE1B0A766F4FD45A2F1BAA6CF7CE45EB6129556CBE64669BA869BA88DB7A3938AFE866D0F00FDAE5ADDD1FE0EF9C79B4502C67D36CD43C2EDFA5B9C3BA54D5BA4ADD1927A7E7CBC7393F2EC1B9CF40C2C86E8F6DFAF271A4775DDA14CB4B0031C0864A7DE50A1E5CE0864AFD368E653C818BA339BBF46592C96E96124B1AE6B2B6CCC339AF27AA65BC26893900E9D8D7736BFEDB3FF56376353C2D7D1637F2D8373F56E18CA160DFBC453021C29BC3D12649E84A44584B5623
	DC87E0FE19A81727AB97C9F2AF11DC8CE079D9A837D6D72EA58B65AE5A00F2D6404612DC312E5CC4127BF6A34A4581D6180DF24D3AF2ED59A817D40FF2F64082C9AEE2151EDCB4497D0B6492GAB799C654E2C51136B7C9C65FE5CC45798E063C86EC3DD39D8127BF2B3DDE353619A1B0EF28F26EB65EC1067B39D659EA539G4022C86E2EF2BD395C9C14BBFD8B4A9581561103F2EF6A5A73A3391A86148B82AC0664E61B756472E7205C9BA4D78E18F5864ACDCF56139BCBF217EDC539E04062C86E6575FAF205
	B3D12EF99B4A5582E61F09F2FED9FAF201A4B765EB3AEF8316C0F23BF44B33E4967685BFFDBD5A3EC05A57DE26FC3E880D2A54DD474C28FE1698F3344C52BEDFD41630311A9DF02FD978FCF176F236FE9B1FAF3E0391FDFB4FA12B45C06B370B197F07AD47247477454CAE1F67B1605E00CF2C6E6C8377835B421F7ED2D6E6C1109E8F948D148814170B3EBDDFBC6C3E76CD03AE76351F68F6472FDA30AB648CBD6E4819C86E8FEE49F510DCD22FBBF203A4F72ADBF25EA417E2F3C74E1F64CEF3CBEE9C49FD7613
	BBF261A4F706DBF293C9AE2D4F9D39B812BB53AD391459A8B7452DF390F2C96EAC37640AC8AE5D6E0EDC9D497D49ADB9B3495DBE600EDCA7492DBB620E5C9049F9BB78B9E63609767A0DD57C0DA44EDE18B1487A66E048E77D221170799C0FD06022AF3F12BF2732F48C64B5FE0176DF2C525A8F7AC2EDF33CA6AD645EF1107BD25B56B4F2E21CF7484A7ABB7E8C2E98465754509ACFE95EFC4F20F25EBCED0E096F25AC025F3AB9780C1FB5B5CD7227E58E7633771531392EB1108E848A830A82CA49037BAFD0FD
	9E763725AB746601547D2D33C9A0F90F29A80B2B2AC7D706B7B948E94B30A3CFADA7627EE38A46BD14077538F75568FCB86CA02775E11C2E8F4B1251077164C3F64D68FC1867A02775A1E52E1E8F714B51073C3968435D23746131E1FDB06B7AD0DC0DBED84907E32BC76743998EF2451A745839FA5754A2FE2E0A4EB5C5EBBA25F93CAC1C3F8AA1DFEBEBC7676BB687B92D2F713A6595580C65157EA57A90B7CA9FBE9956077AAF75FCF8A68F4B2115FC983BE6F4BEDC6FA0277541C7570760F674A110FCE8D9BB
	BA9F8EBA48E9FD085675E17C941A4FA49F52570D4E07AF9C6434BE144F5373A1017B40467D0D73500750D17A7010031C769A761C27F77D2CEDF6F6CF12E6A3A4DFF7527D460FFC752C9B1D2FFFF6105316D72446D7F1DFFCB4DD6F3E988B3C06FC3CAF9E2BE0FE323D49397968E7757A51793959C1CE6B67D03EDE3D6EA09FFC480758D17A706130BEA4687AB0C0BEE44DC79F7CB70C4E07FFBA48E9FD304F57734142FDE0F3675E6403F96368FC586FA02775A1CE5707BF118FE98B683921FEF4BE4CF410537AE0
	DD20670331866B4213FC8899258F778F6BC30C2E8F216403E1A17AE05BB4BA9FFEF510537A5031D04F07525568439079D030F9F4BE14B948E9FD08DA2857075CDF255707A4GEFFA8176A1A99958E68A8A504F8923747325E17D74AA50AB2B8F4B5187FF7241FB4B68FC3848C1CE6BC34AA2BD9F96D3B964ADC29F6A9AC66743378EF2DA9FBCF4FDB0195187DF72A1FE14BEA4ED9D4E07C4DD9F36A5238FB90B5107G87DDA37361DF437AB038D84F07AFB7208FE3480756ED237361C787B92D8F713ABEBC1605BE
	24AFC19F323E9E1D8FDFBA48E9FD30AD517361EC3AAE3C1660F36F755B98968B6944A5B45F390275B3B85FE96A6D6AE1FB62C55E6A9236062D372F6BD0D78FE2F59CBB505B478F2990E7994D60EB8E528BCD4642C2547BF32973B3B5CEA955BFD3432BD47FCC0D33CBDD1F5FF4FE2933B3B704FF6713FF35C0821B55031809B0DF42BA881B5D0B58A0E1979166CD589C9BE27E047991B60E3039BFA196CE58DF891BC8583CBE44628873A7ACE5A9E2737B914BA56CB2420A88DBE8C72C0E3031041989FBFCG31CE
	421A0FA0B6C45865DCF68C24479256437942A19D35945B7D9CBE5714721549188FD4779562CBB84E5FFFCE976FF6847E0F232E4E1FC2E2FA74F03463CCCE5838E7C4B60CCEF0BC6F47195CDC4EF35F4CCC5EE67B7BAD7DFC12082D961172DE364FB0681C6972EDFB374500FBA9722AA7CBBC1B6C3D9B4B8C789C724ED2F6DF56F98ED11C097C67C1FC86E97D8A57200419D9F97BC0BA10E1D48FB326AA7B17091A320B03F44AB2B5A62D2DADF3FEFDBFD526FFFDC715313E263FF7206F00C5FB8D3FDC16CBF972AC
	9ABB6502950B6009637276763EC39607BE60FDAEAF721C499BD6BA6BA7C419C7294AFCA53517F3D214E5142BA90FA2C857E9B0214FE715D2DFB969ABC9D66AB3EBE4BBA1BD242B2F21C6292F0274FD30DC29EF0CC6F6AC2403F575A52A74D5123E152AB947719A59A8C8476B6A8BDABDC5212F348475FD16255417D624164DABF23CDE053E7EDA25FF25645FF54DCAFDA512FC3D745B62D45F12B5CAFD7219F0AF64A975D9B5729ECE75BD2D52D7CD7AEEEBD76A73514887B855F74EDA253ED5246F11A9CAFDA19A
	7988277AB62ED355C7A96A7BD825AFDAA31FG697465500FAE17F1216FFD353EB25437C7550714CBF28D52EF6BF2E77E5DDA2752379247AED3B2307F32AD477EEB8EDF1F94G69D020894B317F7A55613C42B848CBA996B6F30B1D5F8B0AB4F9F510B6976B356B5E75AABF4BB16E96875B1DA0BFA46998634276D8CDDEB024476B5ADE38C1E53B82ED9FF7309D857271120E34954EED67E972CAA0DD3FC24F7693EA5B15B4AEF6305D8272D6C90707746B2363C300868B01F4042E6D33B62AECD7511C18036DE810
	CF10F42417085F7C92C75B659A2C81522DA5FA3657552BECD7236D749A2DED9B48FBCABAFCF5ECA285EA72C2A19D29EB7B9D356DD5E87BEE875BB1A01FA86948AAF5EE3BC013D78D6946D2BD5BD7EED25AAE237E3BF23556F69B485BA59DDEAEEC7BE97202A09D26EB7B7B4DAA5B255897A4513B13892558971C779D6B8B324A60F983282E8C7B0249998E6D92722CE5D23BAC5377538770GCDDE8824A34A747C4C5C2272137A40CF4B9D5AA548A7C8BA52D76A5B4E873CDC1357G695615FA366FD25BDE093617
	B944ED83F9CFC9072F935B010007EA72A2A19D23EB7BE8034AB67501BB4DDA5B09A01FD5AEF494146B5B2E863CD1135786E9FB391E6DE5DBD536298FECCA565A7682F9BFC9C710935BE100CF506445C25AD021E77BD935ED6A8397EE505A4E8179C2C9C7ED053EED93609D1A3CC1C8FB6B5ABEEF1B4AB67501933234367DC1FE1C24A35C096D090047E972D2AA613931D24F76162FD536298FBC4F211D9701FC1D2443DC29EF3B9370A1CD5E98C80F55E0E21CFE53B636AEE110523772B45BD3AD1E01AC6B992F98
	6BEF6A6910A6AD44DE14A27E2CFFDB0AB873D6E07F605861D4F1062A400A79337E6C4F1458359C1B8B5829041570E77DB900BDC258F53D48D7BA8931B27E2C7F8F305B1404588D9CFB84F85ECAC62C1CBF6B7FADD91C17AD309BB9369CE4AFA4FD157CD97F61E4F1CE3740EE625853001DC3D8B5FF56FFB9D9695F4D9CFB8230B9242F06BF2FE7020DDD045D4A31F3016FEF044572677F4781FB176268664F75EBC0F6A0895B47ADF6B68FE4EDBF56BBGE5DFD045789E21FD0C41D02F91C089C069D5220EC55DDD
	F7146DF5B958575E8D0FDEBBEC16835099B059530E202C9578BD007C01C201222AF04DE041A6767CA71EF4253307283D6EBA7C39B6EFE7FF7BA18B665F70E5966573E3BCF82EE853978CAA472F8D60CFEB355CAEED550EEDDA2C23C9EBE677A14FEA310EE68561059C0FD560E2FD4D7D7C991C254D2B20FCEAF05E24083FB3EC2C853B35082D9F4039949BE189FCFEB5945211047D1D2F730801F482E171FDA81B3E86318B1398568AE99BE1DB38BECFC87B90360CE483885B4F57D0A4C0BAFDADE2D9FCEFD6BE24
	CB881BD50632750479D5B14C87528104ED63777490C80FA72C14E423886B6532A56BC05FBA1ABF63F32BAD106EA46C0DDDA8BBC4D8F8B3720DA76C4FFC1FDA94246388FB7FC714CDA46C43BC14ED2CC36C5CF464EBA3AC329D790288BBE79E72059196B38579F256A31679B372959216B39579868973BC0EFC5E049D637AA6C2BA0E303BB81632817A738D087DC47A0A88FB73D70655C15AC41879845AE85D00E3294C8C65B59188F8688679BA085C606C1A3806BF2745EC90EDBF1B70900E67EC14F1F1CD4C67B8
	CB57C15E780DB457495FC7365443782D9E31E80EF9C0FA8CE1DBAB99668769F104DD4E1F9F43A1BD0130C74C98E3ACE1314B98D63B891E7BB6A1F61C6CF692F6BB599DA2EC9E5975A24C0B6C7A9156C0EDB308B05FE9E85730192E93321BC3D89459ADA4ECBD59ADA76CCB325BC0D8BA59EDA5EC86598DA44C1B6C0692964131C8C8C793D6C1F66388AB63F6D336180C79DB282FA0DF0A88BB2B99FD29A36C43CC74E58CE16348173104A5F0AC985261046DA5DFA289337258E2A01D52C0E5BA8DFD49A2AC0EFCA9
	A02C0A4A45CED8F18D7A62C5D89F477CA09DC858E3B568CBB0E1DBBFC73B63895BC77591C558448C34DB3295311749EEBDE14BD6235D96423CD7235D8E42BE180EF687897B66FB6ABF88EBACC33B9389EBD90DF663887B9B59CD5906D8D2AD5A4DA2EC9159ADA04C076CD693F6A959755A067D0CD1B5576887F850B639EF895B662C1FD93103759B9336097EE4BA616BB91E76350C0BFE66C27E4C485255109779B515AD2F83DBA3BCAFB5A2E66718AF248388FBB2037A99426E676F5842A19DCD585519D8968904
	E5D35D27FF0318115ADCABE173AB50979BE1E2ED08A724FD883B3F82FD71A74C1C013E0CA36C42F274A51C30E5AB5017099ABE66CB513754EE28DC6A88BBB783FDB19376B615CB9BE175D4AEF6421ECBC7DF3C88CB20F271A32C1C2E09D8420E723E5B5084FDE393E237F22C90525504656FC4DF6A899B1F0E3E3490362882FD314AFC45680B87E14D9568CB84E1BFA523AF51045D180C57C982E1B37938AC45E4B26619907BF8977AD2C258149568CBBDE137DB50179642468EB1ACG522104ED5A003EC492F666
	8674A50630D7B720AF710499E623AFE95BE93C358EFD49A3ECCB867AD2C2D8D29B7A62C15807D9680B8FE1EB335117G42AE4DC6DF469176C0957A92CE5887F49DCDA46C790D68CB9CE1EF4CC5DF6A3E237A606D4F8C69CE42BE615890243D89BBD0013E789296CD6DA510309AAE9B8A698842BE207692CD58B96568CB798E5C9F75011D75BB2CAFEAG4CE3876E1BADEDD32D47E53B1DEAEA3674F5F5ECE8BF54481F2A447C17F88EE07241BBF07E2BF700690C03F41A996E3DE6BCD7E0F0B11EAB9078774B8C0D
	9EA65D6F38C6192B122A4D525C3C744B76C43504603982579CC11B3E109FE1E64FE7CF2A4EC2E24FD4CFA4093588A2FF2A6A2CA1B61EE35FA91173572872193F8F2864FBD579298AF9915B830BB0364487028C569B57BA7DC6ED6ECEE98EC67AEDB9325060FD26A5D55451BF793BD1BB64F96D243E040F1D83A19DCAD88D0D7723F7623DEADAF3AB7C16B743F589E48372868A848A870A81CA5B85FDA8D083D0A71097D0A050F820982034964887B281D9013C007C010201A2DB50CEB137434E320983ACEB37091F
	D7D3823FA6A09B90BB17E68C7C32F3F10260B782A88EA8EB8FDC27C06552D9B36C9C99F68690BB5FE7D0BA17071D39A36CC418ED139C4BCFC961D2CC4CD7BE6EEFF1B1473DCB44261A7759A54A097D6DAD7D3232D14DDD6968E4E56C6542963757AC6737B4BB67CDF32147A465858FE3EB24D4A45579F8A94698C52CB1120F226CD76C9C2E6CB3246BE80914567BA69F921BA753FE5FEE16C1DE43B0D97A1D2161D926230BDD5B43FDE3E82A62EFD7EBA1345F84D452A269D77B6D4151929B5B31F5CE783DBC65B7
	6B701BFB52FB51296C2C2E033DBDF66FBD2C6F594D1F13CD70F7ABD04A778CEB9928F7E1EB32248BA98264220196CA651130C72C391AC4B6B379F800FD6D1A9DA6A66CAE66984BEF7C9E6738EECBD17B769867E9057C4E6F5137297D4379166A42B75F6F353E85A5B97AF6EF927AB616FC5B172C764D4E47BD63A0BF14FC5B50B71CEFC6973E45B978F6EC3923EFE31623EF09645B12E235EFEB787DB93D55E44CEDC55F0A4B0673ED328B5F9ADA353E5DD065685B7DD5681B3995FDEB2FD67BF6144FFFF5C03E0D
	FCEB985637CC973E79BB78F63A0EEF1710EF01645B334DEA5FB673F1DC88640713EF29AD2EFCE37627AA5ADCC22B5A37743DA66335707BA97C93B65E654F603900976CC5BF2E7A1179E1378C5A4D00998C9FEF0724C1581FF6F8B87B490ADF5F3DEA7B018E769772B1E3B060E1FBB146007461EC24AAEC44EBEC246D535A78184F8D66G1E3F8FED7CFD5EF0B614572EE91F5AC607030DEC5EEF59G9FA29B0BFF9E4EC62642C62046C628030DB55C46F840A7100D0B0F3B32E1B4083E98ED247D20361187696EC9
	4206287BB1D3180C250735548E0330FB65949C03967EC0E73DCCF1EC53EFCD41B6DD8BFC823BC84C27C33A05E4BFBB21EEDF95BB6476F578042BF812B571C4AB2EBD661F9758E077308FA09EB1F6946B41FDG8F788147135BF81B9B8F69A842423E44B1E6CA9B0E7D2E2B4907DFB324EDC05EC081C0E1C0D1C009C0396D48FBA7670D134E4573104E5B6B144E7B1BA01D53A72D372C71005FC4C99753210C1169E63F5E125D115CEF4D4370856864CB7B8239AF2C9DCEB508B10E12A7C3416BEAEC212DA3D77913
	1D48CCB77472B92E6E9ECBF7EFCF578151FE6E634F0507014A5B317DFCD421EEBFADE68369285A496268EE9F6C6A9E68669F6093FA7A78BCE5BD68B0111E984D3DE0BCEFABC3106F550E57D70CF9A47D9DD2B048B14276B83EDD4CFDFDC057D7F785772F2BC7635F0CA2E6FB8270C7376335F3B90F3D9BA8EB3F49A8E29D331345AA729BF8CC7910D7349FE52E63B134C03AE3BF4670432E5F92034FFEB986E1638EBEDF668FF8A0593D1B7B4A5617CEA0DFC31BE5DFFF643E46C0DEBC4908790C7C83607F8174F5
	61303E263A703575005657355C57CE406D87506E0D5C571FD88C8730AD3C2CE9D3AF6C12FD5F47FD8F825E90625F1A412E8DF1D88A5B125E5E5381E9A17B59EED976AC9EE38C4845D30C17B58F972351C50C859D5A989772F1C28960359D9863FFF90CDDAC768E74B933D29D6337FBE4BFCF67B18E822FA7711FBA4DF90C27FFAF4B5E4D4BB88864C2BAB0C6234BFACC9EA646D807985B380D84265F02B16E62737307D86C9674F92DD99D63D52D321F091C3F8EF89BC9C79FFFBFB088E9CF8B7ABEB02C6F2E5A60
	B80B5677FC6EFBA8606349EE966F6B589A73848BDEAF4F6F15FD3D1167279DB499F38E224C029C6EBF249B8F222F9D5F8F672B2BEBFB68205657817EDE408BF09F32FBBA7765G0B61A07A1A7A036C6BB27E9EA28272A6124C377CBDC45AA1707F907AFA4DEF72357E105657F06E2B897056C3747E2D9A6F77DE043D160C777BC042DAD271FE9FF1886F6195BB8A60B73F932EA3A0B31095488B489FA894E8A2D0C2A772EF617CB9DD62ACD7F66EABBBA3B6C2BA4B36CDBA03161D5FAA78AF7DEE0CA6E626976DB1
	5DA3393F0E042C8ADDE69D3D21D2DCF2AC77B8781621E86B5AF141EFA1657DDFEFDF870EC5269B701E3EA143F1DC301BE3AF8215F76135FFD6067A5AAFEC135BE8923F866B0157C47CC9C5ACC6DB1F6520252F4F52A1BE57AB713FA3662801572B8B5B74E3FC9EFC9C24C33B30CDEF9C76FE25FC0E0D83B9C6B324B61D722336CD17F0BF33G4F7B916DEE63FD79F3C08DBF226FC31A7B55576DF22C6F08F737405BC1BAFE6165B58652FE045DA95E8DC1BA72C71AB3D8614E3D49A11E435AF8040DAC40738E235D
	A35C1757D8BC0731BF39E13F6C7F9CDE86AD1057C1B25DDC4F98C87B91A6562607C3BA72B07A1FB96C73F12AAB7F2DDA7F77F29BD90067D9316C5F5D61589E7D79BB30F7D9CC44772866DE741F83F20CCD7C7DD88B70F6D8B11EF779BC7798C87B91762DF8BF8669C8AB4678C931BB63B907983B35B11E46EDE4811E57CD4FED3C4FFC1045530DF53406DD9E52B5F939D9FF1C3D0E4F113500CC8769DA5C426302349FE1AB453BB6C8C7F6E3DC0FDB063BBF383C16FA447B9CF92C7D58AE7D3176037C7DDD967067
	7520BFBD7C3D5C5DAC5E9E0C376D20DCDF53797B2E96486BA019257C3D5D98C87B9166AB5E5BC1BA3287635A17614ED848A12EDEB9AE69FE4B5F79E5811E570BF69772F7F98FB37FFB517FD38F497E07737C96486BA019F8BE87B58652FE04AD60E349F0C8C776227F9FECF4E7BC64603FCD6B7FAC7E2C1D85F81E8D2F1571650E57548B9CFB1245C4FCB3B47D5E5FBB65982575B3405BE143F8B2C43F87E9BF52313DDC7D5CF7C87403109FE9431833DC7603CC4FD4D7B17F240DB9D8740300677D04775CCF5DBA
	738C6F1759068C374EBC1365B63BADA72D63B8425EF1B67CA47A0DC904EF6338D9010B772C8383781E358D7242FE42BD4FE7F631F52505FDA6E3BDD0AB109D4887A8288F77AA57FD4D3EEF66721CAFF32F2D51F244E2D5ECBF96718539F5269B0EC58C06BB0C78BEF35CADD79B02CF7FE41233B360437A44D8AADA7A0D33AF643808AF6FB00BAF275FE4AC81B283D901BC00FC7BB1BEAF3E071C1DD65651E5DFEF37379F68642763E3F06C40FA99E60BF2C51B3DFB102D715D5F5B47B7DA0AB53E4F9AF01DEEA068
	8F83B258E13CED97E77DCA7720A90EEB03DF1902F2C5F631B6389EFEDBA4B9517E373B1C0345F60F573D8DE4BD6DE22C4E67FD616F57216D3FCBFD71E55F32EBC3778B37863C276C1B415639EFEBEC5FEF3132FC446F5910C57816CDCD12184873AF4BD169153FD6AB727BB3E5599D837BD5F9134A55325A7CBB7A32C9D67175D97257F72ABAA6BD1E3FE74B76652F8BCB737DCB557635797313E47BBB7BADFDDA5D4F4DD65B5763F931D056418E845766EF1DB6CDAE9F1B352B474E5A17483BE276676A725164EF
	6BD55BD7FC951774FF5EB9CB2DC37935DA0967256DEC4C2F790A2C14776E8FFC1D3972FCFC52FDCBFF3AB28F1A06405F5B2342F76C5CA0704EA615AEF6BEBB69BA3CCB15470A4A005FB6FAE51DF2BD44D869BA893763793AE3F573A7D27E145DFA79F114FF3D6E9966A983183F7887BD79DC4AB7346B6597D1FEE309DEFE9D656FAE5333EF267C8FB37564BBA9FF43C13D7CA14AEFC95553BFE6G47A947ED862A070A0185705BE3B91ACF878FEF069BF561028EE8933D0796946517946517165059437EC715E78F
	D7CF29F5FA76705881E9FD01749BF665C27EDE1ABDE77275D27CDF5CBAC78C673C22591421E47739F43CE764DDF339217E09CFA661BC0556EE1CE42F6E083C3EE75CFB16D4916F9A7E0E595EB7E091B63C0F48E59153B752329032579E15E51FC9D26B5DF6DC4E1B67265E87FE16E52FDC2E569B782B1CB7354CBD3D8F1F10E51F2BD26BDD16A467ADF2D3EFD732ACBBCE23776ED4B96F79DDA3552B57CE043E184F64767143E4D97769BF3A570E9F7EDE590E33AEDA1C04E3AAEDFB9A145A112722BDB9776F5EA9
	32FF4FE44B7E1D167E5BE31FB4DD563DF7062C7B54F93FDD3747CCD97744DCD9F7724F3FDD772733E55D9765493A870F7FF65D374D15755DBE05B536036D56FE79AC1134296A7638A8DF66FF6157797A7657339BA218AF154CCB3E774F17E5AB2A9D4707CD9934876C08989FC640EFB4D062911A83CBF19C9F764FC5397C23E2FCD88D3F0DC74559B0E2FC7874176E4C33F4006C60D1F9FC688DFF3F89E3C3A379FF3CD9B58690DFF7167232869C7370BB6A53539C73705B66734B9C7370FB632B8A9D73709B60D3
	FEF14C436FF2AFC8F74C43EFE5DF7515E39EFE3FBA0AFF93F7C94592296E4EDFA0571DEF46175AFA2F6F0261FFFFD7EFCFE195343932B26A5B5EDFA44B5DED9EFDFBC5FDB1CB643F27AE15F5FFDABE921F24F118082D92E3AB7ECA56730B9B317D31C8162B9DF15FACE47F31DC0EA322D85673D945486318A8622842B85ED9A16B196EC69CCDE5321C414DB87AD64AF17C25DC56136DC69C63C59C5598C7F0052CE7099BF14C2B1165CEB43B97470655F29C16DAD94F53EE342BF891C7B9466135C65653BEED64F1
	C42F1365225D0C4338DE0E632B8D321EC5EE54C7CF8D0FE3A546714DC6D94F5A4FC79E47D51BE4396F5CECD763B74BF144B749FAD63991471255BC0EB20CE356F6D94F9B53C79EC77F8ED9EE7D776E45B151AC477169CED94F8D2BC69E47A5A20ED20CE341AED94F97EE44716BEED94E4B4DB87E36C70E636FA5329EEB4DC86C0B7BF4E3B55E27FD0F0A7BFB907BBDAA4EF193BC33D310E722F4AF0F075F34C1B1F69477725ED4F766F773C136FCD03E17B7405F2F413D7CAD1A6BE94D974F5278BC2FFCE67F22B1
	DD19C74F6C633628F0FAE6FFE50ECA176A197D611FD5B2FB361A845E36BDDBED3FF16B3766CD5F115CDB3F2AF4CA4F7AA26F7DA1D55E4E9D8A395B9729725817648DB8C76031C36F9BBB66C19CE759B2F81B6B43B6F7DF29DC677FF655A7D617295A1CF125ACF7E96648654E2F1465D2FF43F38F6A3B39CA7EFBE2352C7B6FA30AC56A43C57FEE43B279F4152CE72B2B7B14A636B52BE539335DA84B8B957713F9AB5C3BFEC3564A71CF291375BC1ABE72787FA45A44G463FFC3D2CE7159BF1189477257F0C483E
	100B2B1765E25D3C1FCCD75CCFAA3748FAEA5C287F1B4CBC7E63987F6E86D94F9DEE44B134D5167B42557DCCA3B761EBD9AE34583D78E7FEA3473F6ADBD9CF109B75FFDDB20F7FA0465F2A38AF3F3D4B051E0A152AB8CE18E439CF0BC7AE7774F7325C759677627FE4079C7FFC45FDB8F147C86CCBF3903C0FDF329F632FEF16753C1BBB72B88EA96E5F9D6BC6AEF717627E3DBA433D78DFEB15631F36CF5673DE5248632F4966719F4278CBFFD034472A1147E1EE13655EF0F5CF5048856D1765CAF73BD97F8794
	752FD897F07F7A11477FE1B90F5FCA75FFD05673242B6BC8DB7F07E439952E7A9FED7DF749F22F3BF97D3F74239C7FE42B2C67469179AD2D9F63F33D62EF13626F1F87C6F2A66EE8C95870B929B6443B4EBFF7B9BF177642AE7DF3E9AF6D52BFF77A0AAEE767450A7CFFF0DB7D2ACF6D212DF1DDEC7C5AFB149F13AD3D2BFD524A46EB1DB0BE73848AG8A810A984477349B787C6D7A7DFA6FE8B7F59CE26762765B5B59D9D702FF51E16731969D1EA9474A248ED81A5B7B3A584609FE142FB84CA6EA77775A6D3D
	5DCCBF1D7BFD1855173D5746C1699D79E1D6C6FDDD07BA6D02D542B79FE663B42B65A007453B785BA625CBEDB396E2ABB806FB1EEF1B429EBAC674EDEDF3175DEA5940E796256F01737DE87CFBBC47F09FF45A94FE26B1248399F68C77DCE7CF4535G93814BB90E5FAB907B7C985EF89CF51C41F9BDA16D87B48EE8E46FE7F06E555AE3B050594C871D1C4D4C5FF48A9F726B188ED61663492F2757A11672B3E26BF7A3D6CB58AB4DD886F642767E00FC0104D5713DC76C39A11A30D37937AE599A0306A1444614
	21BE8F42CA3310AF1830E387D1DF9CE16724222C7997DCEBA14ECA7706F440AF58267FEDE3FBCC1B5BFF14D6D530A5883DFD3CD583E86A6B35D97A6C47845A4C3F22A9F46D343101E2B8680986CA7B55E44C87AA87EA84EA833283F981798385830D870A820A87CAB9E1B26682951DC0BF1676ADF5610734711EDFE0FD6D628CFC61472D7C7D1B50F17964C54EF440953A8D2A3CE97F0F1683F6F3BB3BB90836F8B4832F05BA70E57084165F3FB3307C02CEE0DB4C59454FCE023441837C87925F6238F71776DB9C
	A510E7866A7440B6FF71DCEC739E0085FB201D9766E21B0F73C0BB59DFB25E42BF180CF5C0ADFFF0374D4FE1DD20941F3D9A6333011E00BF205D10EA0CAF6A8FE837BA1971661E827E83B51C023E2FCF2135BB00791E02BA4ECCC15FA3CEC19DB7F15E34D321BDGD51F6A2E6FEBD955CAE70B5E4457D4B2BE532958CE1E33BA6FD3E32C437729EFD9459E35C39673B11BF46E78FBD67E9D0503877BAD54F7FFE44DA2ECEB3F29373F0B6DF89077493BA68D3726E85C296A7555C26E11E165345F3A92F2D31218DC
	4429A3DB8B9E8DFCE2AD56906FFF93CE552E51BA0D63A91E5AB5DA5326E0BDE7C11E5D9357244D1ABA3A35E50BC6A9374FEDB99157C77CDEB141D39BEFCABA43239D627D935D0762A04FFC9A463BC875DD2F6053C4390F17FE2360B77EB43D7A0DD2FD3BAC6DF44113A77D1640EF7D69FAF21BF7AB65DAA49E2B746B813FBE3AF2EF284ED98F5070041C6EECFF5F7E9F14F29192DF34741BG3F69E768592BED579B9F66EBF84B4FF0345B2041DAA1ED8372D4604206D70952B7DFA9BFD07A8D055FC8209887393F
	14A96592B576323C1C15C5256A6C7B82092FDA7AED045FB6A03B17565E2F87754A42CBE347CF47EE10868B03748420D8879BBE29CA5F8CE72A65F24EF454AD7A2EAFFB595FB4AE9637ED9C137632B1EED78FF4E76D562E636D2CD75B810CBD07C4FE174DF97FEA338D5F3F9E33690FD9CF589C472C1EB67DB16B9FED4AB12B404E3131FB55A1988734901F8FE13B893B106BE4582E2E8EFBA76117F23F985EE0E1866999027BF514E3AD92F635825BADE1575A58BD82C26E6C6DC359F1B63EE6384BEA9569E04E53
	5583378CAA0F5B380FDD36F6055FF792361B6A7A170C515DD34E9AD6CE7F1E9246658AF55A109E551E097794AFBE86B21D296DE34F65F86B195ABE760BDD5847F6C25E78B3F14C743CEA4C541950E4AC79A30CF5002CFF447145873352A4FFBD7F08630B5767B0FDA1100E840A7DA37659FF1BBB3AFB4D3523140BF1DBCE3A777231DD4AD95A72BB1B63D9E7E94BEF5617D8FEF91067F19646FB5C613B43A3EBAFDE434A693717BB38DC74D9A3EBAF89E7E1FB8964E34AF4EFED3CB70A312637B65E354998EF8164
	F9F9533E3A145155534ED14A1D6D361C08EBD9AABFDB58A15EF20EA7B844FB9B0D2DD37E844F8BFF226FAA6E505EDF46C3DE541FC479477F49319E524EB6997300CA4ED66755C33A85482A4045F70EC22733E7462E9EEBD70F05FDA1FB0B2547005F641A4F1F67BDC04677EC5C9FB9E6B24382A19DFAB67AF98ADFCBA675C50A3FF3261C7C792A653F038DBED50263F0455F67CDBD793683CF2A8DD12735FC3E031F43CC757C85BF53AE8A52F1041D20732E93895B4A67C59A4F01B152B9083D3F8E7559897B3515
	6F9F0634AFE1AF34223E3104B572F31D63A11DB6064E125D0D7A72884B674F1DA5102EA36C34945457C858AEBE9F638769A042D2F7213EB0426AE7236C84420E7D00FC0567A2963910E13510EEA4ECD5814A369036184F6F84C0BA0430FD9C0B00F494E1F7F1AC9652D9FFC6ACF7BF5AADA0ECECA95A28A6AC0F6FBB7502349FE1960E85C1BA0230CBCBD0DFB4E10F70B2CD00F45AF908D56EC19BB904DD436DF6C0FA1030F80EF9C3BA103040B23491CA58C25297C958B7F4B6F8018FE233B8DFB5CB93F6A5491A
	89CB61B646C2BA183097AD684BF8427C79DCD894246388CBBD04651CC85876C368CB56DF903B0E4E75ACA06CFA5E2E6C1076A26C2394ACE7BF422EC9C5FDC104F98C223EB0421E6673CAB9671B0CC567A366475BE99D244D67E33FFA76AE392F39C071F77E2E135F6F2CBA29B6DA6FE4B1FF3CABCFCA37C14CA1B0A69C04DF6F7345BDEA6C79B8867CD38E4ED3051C0FE340CFE7320EB89E526997C0DDDD00E57649AC65BD3B96F0538572BD2F638247FB2638B77F1247748DDE20BD937DEC0E0F3DC0FBA67ABBF3
	70CC748848EB75C57BE7FDA957130F626F39DF1E7CBA2BBC29B6C4BB1D40678CE3A0DE430558F6C379B8A187520597E2B9DC1FAC47FE2B626FFA076F417C4F17435E5F41C6E92A9CD30D626F3BD2CE3E6D09A755062853E7783CABBBAB381C6AF9895F4B4E4E8AEEA04C1BE36CFCDEB3E11771B37BBBA1BDC458EB9CE3E7760EA16C915E16587941E3892BB5E0FF99CCD89F3FDF27DD84777F0B908B6198BB473764A2DCCBD51D441E7B6D6C3594FBC7EB407D3FA65EBE6B014FC43CD7CECEC5DE833E2FED9FB010
	0C050F09BC005F1BE4CA8754FB0CAF656FE645DA3973DD1EC5192CA16D79E4A1E0034DF163BE76690636F52C2E55672D9D6563AA169FFE916EE3966F2AA6C0BA1A7C7CCB2B5A4FBB0F4AFEFE586A4E1ABE2D1FB9970B3D4C321F87729C7DBC1E07FE66DFCCE7457071C68924EBAFC6BF2BD254FE3EB8A87B6917624E5EF82D1F560B457BAE594F1EE6C7BFCFEBC6BF87494FE25E2E3CA06DF3B16EE978FE76C84E3E40F28B3BD8FD165E040B356775DD464715510047D1D93C36D0DD96E9476532B09538F37E0136
	AC727DCC468CD5D92CE9F7AC8BF3BB16C5119F7AB4502E76E919E23F57D36DEC9E3033372F6BB8DCEA6DD63611935F1060B574EEBB3E9B2A754372DD4F4736D6C88F7AE1795EDA6A4E1EFAEDFC81FE62EFB93E15190E71ED4B447846D1FCC31953D5B12C37F69D6AE1EF74A51973B3F15C91C67E077331F49C2493C9CF4CEE4D79D9C3F25DFD33E73858B2DC44D6F40949B8D5955B1D250E31BDD90A31D5DF02BECDADF39D5BA2FA2F5CF0894676B8476C107638846B2646257F29127F587603AED1377D30CB34ED
	7F889F7FC7GBE11FCAD5E23AE3F1FFE114B6F76E14BCFF9662A367C527FAA46A3F2794DA9F3AC3F32B2AC3F5C3FE2D9CCBB0865D378D774732603EABFFD789EC7295FB804ED5DCCBA0EF34C9352E37E0A6599BE6CD9A659AE628985BD2BD4715C176298CF941DD9BC1EFCA92035G93890B64D85A25703CF7A97AF74160F0751D2D286F7ACB55755DF229DC5F52FA458C7DFB7294FE16CF87705BAE45723D2159711EFC935F8BAA465C0BDBF01E4C9FE4A6DC0A4F87BDAD98EF16BFBE9F5C40F99BA05D86B4C878
	715D8C9F8369G20D0FF9C2F7EB9C7BB57B741DFFED60875F7765C50BF0BCF887DCDBC9F9889BF4171DC85AEE2C865B8CB17C01E4F5FF0BCB3A317BF0FC3BA7DB2446A781CC489246B893B658B9C9FF5DE063E1F1A17EF9869F924BE1C579BE483014683458225DF8E4F22C00DC0564B117703B99E067FAD67116665B1DF7EF71CC77AE635FE1BB62E62F391D0FE3E17E31BFEA24931CDBF45DA0CA4134E474FAF33BA3D9C773DFFBCC94E975FD019GF9512473E6BE166E37D8AD876C168E4197310C2E0131F8FD
	4F61E70766C33A0830CF78183B85529DE3711A5FE1F0E76C2ABD5346E72C76FC25B3F7B976C97E3B30FF74A7DFDE6358B8C8070E4558AFE5DD2CD44E1E13C7306F35DC5E77FAF71AACBBED403DBD33B10ABD5AE72FD36F452DCF17737ADA5D53FBA8C316BDB7CF2D7719CCB9AFA745BD3D1394FB367D1A55FA77CE17732AE77F763D4B57305745384FE4162CBBFC21FBBEFF25582FFD389DFD9669747DCC17E2C9DE718E76BFAE276CAF5E38C95A2BA3F41D7E052C4B3754BDBF2E1AA34B36E72A7D78C766BC57FE
	B4EE5AEC167424FEA96B39FB37FBBEEC1CA74B46152AFD28AD9B398F17AA76FA4F5A735B6B7A3F8B643ADE2558D3BCF50FFB711D31D816FD4E220EEF51411144A7F8B79DF22FDDB4AB56DCDFE1F14F67E35D326C7BA9EA1FD7270E3CCE62FAE5BD76415FDEA753FF126B2437DF56BD26F9247D23380796350C6EBDE84DA8657A1D4A450E559F8708315392BEC6B3DCA146CE490417F2BCCB010BB1554DAD383EB19F72BC2F40774E9F7273FEE2A47ED524E75E778C6F783B0C8BBD9EDCBFCBC7C1DEE0G0E392E62
	734F03D743FD6BEA7A96598C9C0785DD0D6325E3FCEC17721549188FD477956227F1BC9278922E96E7AC88FC6A2C114C63B1BDFAB85AF1DD8785A332E1F40263FC102B3141F443FFAB9D4F18D81B2BBDE3A26F9A6D999309A79C4F18681A0DF2A55708FD298D705BFA0D10935FA1FE648B6431836E8594G948E94F38D1EAD14D0215DF749645382B1FFCB25DEFE9E65BF2D1BDFC279776B6A2F277C3CBA3D7C964A77DA2517EF257C3FD56B65FBD07E573A7E798462B86A81BE17A94AF9DC52C87B89413FD06C3F
	6B477DB7EFA84E9E3AE0594877713C3CCCBDAE499E14755CD3BD727DB85D0AB30ACA8C6E6D47992318333AE917725C9511FF43101CFFA2EBA4BE096B7F0F7C19B3AC2E092F731656A270F9A043DB78BD07AA1E16737F5D626ADAE4F26E74317A577B8417B606FB6699198D4377B6C55F0073ABF8B6B39B70E69A646FE2316784B6E8E47331EC6045561D30FB9DEB13EC5E008DB258C6DD567E58BE84F6E087BBE003ED04E61B1A59A195D5E4FB91379DG6DBC82A881A87F9F50C7G5900FC01C201E200327EE9B2
	D6833581F9818581CDGB2DCEBB29682190086017C0142016200F203205F866A849A83948C94950475F77A6E50C96AFED1993BB26E69A30CFB4988621EA6154FEF29377FA3ED9B7E40E5DDAA6BB1E30475B8E318FADCA559DF7A3F208C7E7FC2225F8E4DF19CC7BC11235A0FF31D49D88BE43ACE0C1744B86205994853897890109FD08850446BF00D6A64D63DFDBD09101FF5BD72EC295463A9047C5A6BF10E72C8AE6D453A9E77541CBB9B67B33D818BA35EA7E763DCC96275B8EFE97C0271165FE0B2B682F55C
	003C6FFF013CC3B760F7C0974D51FE8774AD0372BB1E3BD4792C1DBFBBC919FF26AA1FDD93BF2972EFD56533181A24FC61530ACA5C0BB60E7C7C15305CF108FDDE055890E113B89F3B7E22885BD107FC75B7525E2CD5088590A656A6B0AC7DDF081DC27AEC0079FC40301F3E47F5A711004582255FE462EB0F9BBF9077E041F7D79572B5C2FE47CDB2DF6E07CA3E235548678979FE8ABEDF955F32D54897827911CAFD9FA9791EA53EF846932C502762BB2F86790A00279E28A3D870199315FC4D2B114F9F72031D
	72CD29C53E89109F67146FF6624B3A995A794D4E787A56A0DF837034B965DB3C9679BC004757A95FD344978CBC91C0F1B78B3E14CF14FCFFDD07FCA5370839ED645344CB655CF00B187F5667DBC8755BF60B18A35767FB30827986EF9173687AFC0D44B76696B17F2E4F3737927982EE91F3757AFC77113E50DB443C3EBE5FF1621BGFC5ECE792EA53EB860CB3B55995F691948D788BCF5CE79EA32287C00E750A95F9B44678BBCC1CE79AE4BC63E886009F14A577CB972250540B8B144E9BB180EFCF540536214
	6FF6629B841EB1C0C1A1EABE71DE6C5082B6A620BD54DD7B3B0B870EF497376F3797338F635554FCBBE035B6CA37AB294F7CB8877B7138906C73174D60FBC0EF033285EA398D7B3449B31157F69B7679D5B3DDFF7B39EB26364FE76390B97F62195ABEDF7D6D6787E7EA7BFC141776C23762BE0D005B507FCD654C7FA8C8A7GE55D0E7E1F28C45E425B1137B9097136C1FA90E84C6DB8667EB9174DA1855CBE3277CBA1A4F7416C514DD9DDB1CA39E75C1693F3D1BF737BFA446D5A3D821E9C1FF83BF62FC04697
	B8E608013C46FFE33C09DF0C4E6F72CA6D73E290688C7B37A86F897FF6DE6E311ABCC3284918932A375F64D4879B0540D7AB711AC21D5B6850648DC25ADB57462CAA2D8DFF609BA771063B30B1D11397876914BB74ECFC6690C7AE7095C93CF5522F798EC7DB1D9AEC8852E3F4ED3454E9ED0C853EE009F73C0EEE24A8CDDEBC24536E54339136CAEBA38F78CAA45E7ABB1D5BE85164D9A16D21EB633F55DA9BBE4097A0710638309121490B06F4022E0DB39C6AA37DAE68676F923C6552EF43DD0E36DAB5188D52
	1E9ACCDCEF977139E27ECDE6480F388B675F5E7CDE599F469AE1BCF7B514F7984EED1ED3458E7B945F48AB96BFA6CB5F415E3EEECBC7A1CDAF71BD16456BF976A67677D2349BDB05F673C027B98C6DCE7DC64F2EF798BED378D7231CBFE093C86E6CC6BD392C3BD14E3C8A658AGEB3D9B65522B7464FCC8AE2D866582G0BA23957AA7564F26FC1399012AB822C639E140BEB521E894F647C6E41F2DD381ABDDB9FE86F33580B797FBBED9D6DF6CB21724D0CA8550DACD7AAD4E17344EA3499847AE24866FB95FA
	B6736FC5DFD75622DCB9E056FBD1EE62CCBD3931A477B249858396C7F2D3F76949950653B991EBD02E96B0FBB84A5D5C2AA797C8F2CDEBD1AE94308412FBEA011EDC49FDA8173C8E656A819B3A8F657C67E86718195C387B30AEFEDC775B5A783DE4B79CF49A6E27722636D88D180D70C6EA5B8100C5917EDE8562398F40B37E83088F906E85D8A86127D7A21EGD849F8445FA67ECE407C893F1C708900E5BD087884429BG73F8904BED39435CBCFB6F93CCF257E6225CF840D29EC2393DE5FAF2358F215C01AC
	14B38166C5F25FCE53138BA539D959A89789D87A43A8F70ED1CF2E7EE114FB0064DAG9BC3F273F56542C96E4467A8B791301C88143BBDD5CF2EB1026AFDBA4A358166CBF29F256849C5105C4BA4978DD85EA3A877278CBDB973A358FE3F4D99ED7B95771F2499239B73DD4A47751D0FE847FC819C37BF229D73154E20F3E3A0AF7A919C73CD1BB93AB15F66D625DC4323505FBFAA5F7BEC0FBA5EAF05FFE772766E792870EF8561D7F1BCD6018B3F3F6663FF16B6BF86759509FEFBD6AA6DB77E47E4EC7B0FEC53
	7E9FE7765F664F88DE7F91F626931E42778B87ABF0E13F157333F48C64F9BE016FEE7F1C6B387F6A56DC5CFF15BF817D7C83DFE3A27E7E23626F69DF1C7CBD9EC5A75506083919BF0795C13CF593309CAE621899529D04FD44B19B243D887B975F43668769C042AEED41F762210475086FD6BE897737A7913B026C5692768A59B591362D8A6D369216BD8D6D5A881BC3F6BD897B93590DA4ECD5955A0DA16C17AA341BC85873E4B76DA944BEA33BF90495135D92422EA53B9E0495D23CBE04D9B996G69E0429E21
	F28EA3ACF7B1DF7F876914865AD33898FD09A73E4EDD68CB5A5334D6B493FDE9A16CF89D7AE2A56CAE31578552E388ABDC0F3E7891F6E7B65A8DA26CA9AA03B0426615205D1C09083D348A6D969276D58D5A2DA56C2C9A345BC0589059EDA56C865A9FEAA36C4F59E8B71430CF2B51EEA4E19BD7215D9842462EC23B710475105D347FD2FD105DBC42BA294E2D04DDC5F56EC1586B9C730174D8423E233696C458F37C5A8A03F47E965AE7CD6DAF0A78EE23BA0FA76CF372257E996C477C3F575EA7023ED0773163
	3F50FB3E7A27031C76DC0816E754F2E2FEA72B8A67F7EC64435BD523734160F0D6055607GDD9FB6FC03BE04128FFFF750B5B29F468D6BC3022E8FF7B7228FD94F228FFBD60D4E07CE87B92D8F8D4F6A7930116A220DFC18D6B3BA9F729C6434BE786A7A3000FC88A29F6E9C258F918EF2451AF40C2E8FCBCC787E4D90C56B32D60F4E07739C6434BED4C76979301C66BDCD64432A5A51793045C1CE6B03172E8F53E7E1B978138F2F0F52070F0675E1022E8F9BF6218FF164435F560C4E07EB9D6434BE94BE2767
	C3DCAB7AD0779C7A30F36D68FCB860A02775E1D05707ECFE16841BEB9FC3BECCDEB7BA9FE6B948E92F4DF0DD9F1E1803EDB20AFCF8F014BEBC6DA027AD075C677564D60D32CF5C62A02735D722EB6F3BD15E875A9C6434769AF46DA5D60C4E5E144A616C35695A7BF914764EF410F3E8473A763C9D7C9C193D0B060DEF0C2E3D04D1467759307682F46DDDB5CAFBB78DDB1E213A761E99253D58E16DCDF8DE2F8F38FB9A2E1B08FB9E7B009BB3C76743DD8EF2DA9F4CAF687960D306BED8DFC09F3A32C66743D187
	B92D8F913ABE1CC66590C3BE4C4D9E1D8F4B9C6434BEB4C66B79F00F91FD6808C69F9E99258F4F8E6BC3382E8FADC63CAFC5118F27FDBE4A3112031C56075C9775FCF8B795FDA8F91146CA53C7F91FF010537A50296BC3997960C1BE3CBECA9FBE9A5607E0DD9F42B250078872618A7ECDCD6999CD71F7550C13BFAFE5BA29B624733E7858AC6DA5F846FE891FEB4F63FB5C6BA0EDA66C7315787CBB7892164DB9956C839C654DA6E38B108D489BA890A89CA886A8ED1749D88454G548964859488B49EA886A82D
	857201CCC0D6A0AFA0FF20E020489634F3AD374356D279016D101744FA2909701B487CFED92C0B2AFDD92C4BEA055FA1A0BF20902009AF0B75D03E52BAA636862DC3DABB96A92D7992F636175BA639AE33F0A9A666ABCBC736B8FF374A62AC526433B2E065447E7616FED959A8F97CF5F432B276F2E10B5BEB1673DB1A1D73263950E31272020731B5D2AA126AFC3C94E30CA2569849C7D176C14316FD06C1DE674E52ABDC703235438BB558AC033C7786779B4F5070AC5351455EDF8C370EF82A62EFD7FBF8663B
	48DBA47DAE7F9FA8F7C6ECAE1E2D231EAB2567A972B28D4ABE601ECA5C8F1F78B21D75F682774367475099F313308F280F41EBA6A129GFE73BB617A83B283D9013C007C01C201A682A5F4A27F14E4465F99A356FE31F5E2B1527A33B4E9DD9BDB334656FB897E7DC9E3B4F1B13DE80F697E1FA8ABC6D605AE330E5ED0A9AEB916B39DFC4373C5706C067FA95F147B6375760F61DEFDD667E22F4DFF78399E52DEE8BE675C5DE3696E6D69BAA076030BF5E187003CE2709D1DCF157A4C0D8F945F95DF371E696FEE
	9F6C6A9E685E51F55CA26DB5AF6073CD3E20E3AC69F9B21B1F535C5BE397BC4F727BEA8464CF0C41FD707FEAF1E79FFCF0BB5C935A95E70D3CA25EBF497B60ABCB38FFDDBD9A7FAE60634CDC60AFF8857742BF44E3E77B12CC2F609EAE7E89EAC9261F475486F9D6122961FAFCA1BD76950C216CB745902908C1587897379185F8AC595D4FFDB944E2F895FDDD27B833E0AA1FF3AF043C72D7D1E685575386E96B2B686B26E1FDCDF5612B5F2BDADFA7F09B81000FA33B5BF939328FBAC53D0AED6154CAF51B5A1F
	A47B7EA1670F835EC462DF3118DDC3628571465E6E6E761E8E623F0A3F0BA9FC8DE2FD8D63BA314B1D332B34F1F53C260D6B857E9E47867850EB98D7G3FFE8E33F8DFC3BF1F2AD347F5DA329C57AE7EBEA798F843C8471B7C7DC69C2493497737DC560932BE74FCAF0A557A7EADFF97D48DF8FDAC5A4D646FF858E6EBDBAC361F00945957F1623D9063A719AB382F41108E0BC5DF7FDA73DBFC0DF77075D6EE4370BA0C3FDEC73B057C3D905B50DD7DBA6D2DCC15FDFD1067B730BC1239006B71E0695751579543
	1E23642A2D073C2E7575DC5E5643810FA43B0F72364BB70B3F01EDA2C553FF3E71196C7B4F7CBA4E825EBC62CF48F05E567B384D8660B53F01F1C53AEC6B382F4AD9DC1EEFE863DA4DED0C815C0FFCFA62FBF62FB9505B5333495E3B314C006333992AF7CA61EFE8566FC1BACE0389FDCB2B157AD6123EAD5FA875253C29164D05F4511BFA7A7E2B52D7CE7A6EEFD46A2B53481AA15D292B6F5CD5CAFD95242FC1956F10C6F68C24476A6A5BD42354D7C97A16287485EBE447C3BACAD75F87B5D3947ACACBE84D02
	C929AFDEA31B9667B8FE917AC2D6AB7DABA57F4AD473F5F912FC0974DB6FD41F2DD629CF5EBB1A2BBA9B36C5A3EFF52A2FE00DD2DF355CDEF6A975F9E864FD1C6A1B2052370A743D2BDA179320118FF12A4FFB2D2ABECAD15F4C05CAFD919A79E8C8A7G253FA563C2DF5DBA153EB2BAF7F40ED2DF3EA4D7AE7DB63C654C3F14BA153E15D4CF2BD4F8B962E7D62B708ABA572FD205D7A2FEFD058A2FC27CAEB5FFB562AB5478AA4423D4FA4ACA901F2162AF23723DFF1AF6AE24954A40A6150327CEF9A079EA7282
	A19D7A165E35961B2932CDF5F1E11956F6A44847C8BA92DD584EFADB1DD7G696A3775EC7BE729EC2F2435D08EF1B702FC1B24437E36F35BDE1ABCBFC887695AB6E72BEC17236D870CDA5BE1A0BFC152916B4236619DF5DE8E248B5F51331D76394AF6851DABECD4367FDA10B581F5C8BA865FF15A3F2975D5223E8FD55F767186F9FF20F1121EF0277AEC53D57A2AE87ECF25EFA248478125448BBD3971CE7B379C153EEA54F71F6A3BB2C5A0DF87E41674F4BA55E7EFF5BC8F64AC5AC7BB94AF4E8371015F00F8
	3126D848DDDD6A380F770ED2148B0D976701CC005FD8C9CE6C63DD3A92F9D25EB599F3012A01DA00865F45352077F6AB57020E358BF9DF4A3FFB3EDEFEA0659F2C504B8F257C14963DB55611147FF9151EFC8C65AF544DCF14733F574B4FFA8F73E76C5333DFC079F76A4AD7D37E56FDFA790D145F232B3F8D72C5BD45539EE87BFB22FE3D61577FBD3166D670CCB7A877C9333A8C667AC5BB92F5D9B389F92219ACD06E7B60A7D04B7B684B46E43DF53D036FE3BBBA3F121FDFD20A671708E13D483B465577632B
	CA54670ECC11654EEBF1735C9176680FE70C244BFAFADCFD071C6C8B5E97C59CE59847FF94E749FDB6ED64F16C4D1265B25D0C43A3DB0E63724FE5BD2B5C0863D8950FE3A546F10F62FC3A0F5C280F85B3E53985EE4631E9169CC727629CBAAFB762689475D10EF11CB5DB561331F864F1A46549F22D3B5C0BE351DCB90E864579F3F915A30F635FF5BC0E8A0C633062AC3A1B32C79E474185325CC3EE5647198B65B81EA810755C676ADCA0CD9C0BEBF89C1598471505321E1F5C086375E5325C15A3BEC3CF48E6
	AD1763D8D9AC6BF95E55F75AB5F12CDB45632842B81AD748FA8E389147032532DC3A1BF1441549F1CCAFD714239B5747AB627A2846B8AAABE4BD7FF163BA7FC715ACF71D1B57C7C4359C4717B5321E29A30AC33CD3A86273A25EF0BF71FB9F5FB31CD2066F990288AB67FC068413B1A7013E7143571D96C23A1A305A9C14EDA4ECA5DF9B6A8769A0421E63D89824A389FBF2854A4690F69D1F67297E001DD701588FE2FE9852B642B6134F1E040D613211100EA1ECAD17CD04F47A07089550770672893BDA2CCF07
	34A7E19B793ACADFC8879076D8B64A06903635927952BFC22C1BFFA7A09F52F16BE87FC19E2D29A73E9533D05603302753104F0730FD3B10AF1A30E20AA30130743D4857100858694457CA5872054897C858273FA0DFA8E19F71B3D832BE06F14E4708ED626B6A2AA15DC858BBF3D0DF9BE19BF9D959A1ED5A00180D4A4F1378E2793CD8A424E388AB61734209108EE8C4ECEFA62EA7CF7F84319D9538F6BC1F30CB29DD159376178ADCBB6EC9582DE5E8571730947E5EBD9052BE5FA0D63A926D06905F6315E8B7
	0230D7CA50EEB4E11BAA51EE79270885D23C8D0445CDC33B66EF911BCD713692DF8545EBA3AC166275A46C990AB7123057795A1798C8A792D64A5BDA16E13B313A095ADA9A5A4D87DCE0FF5F09F64B89BBF7A65AEDA0EC4577E85717B03B91E583897B679174A5143037B946762BA410AFF30E220DC8624B7E8C7595CCC26C2E8154D7CD181174451828DD9D217E0A783ECDC5FDFE041DE5C7FDC104BDECC7FD815BD1DF029D750591DF147886D652F6E3C192E2EF70EF04D4C33A1130AA3E5F228D525E5F213E8E
	F96F8760E22F578D2D69D2DF9F8AD88210D8BF73912D73ADC95EAE1DB591D542775FC15A8348B799F53CD20A7B6C02G0BC9C69D257C9A605F52C8C19DBF14B15E96C85B003CD21037E9A5727A83A64E4E78E52576EC0E07D567A9DD2B4AE783EEB6BFA867BF274AE7F9B72A4E6618DE2EBD1BE32AC1F9B647A75D3806E8827919B89F312CD444AE29C02C013055ADD8FE9E04ED2DC23EE0427650D9CEF1B2465BB973457C99E20D7BD01FB7E19F90DF98E19E7BD0DFA2E1D710EC1B916BAC2F8A6B5D973088A372
	F6D4A1EFDA9A56D9598946DB876996A0DB9A721A8DD8E7DE44FB0A017106C2FA82D09C7136CDC25E3449D83FE7CFF2FD5ED668A4ED7D2A4F5BFAFB1236FE5728728B12DC576F71E4AC2BBA70C90CF7B6D6295FF76F6665B196720300443B1B8B4BDC3D3BD1FED306D1449E75379E22C04FEC29AC044D2F398DF6448DBB79DD792E18654712EF835F28FDBB0B0F1992A1BFED8A7A36A3E7B85FD2DD78D6BDC56B5B36C6C75F5A9B51377AA968DBC2235A37E95CB7B3643711EFF1AB06734D684237B18E3E05D7BA7A
	76ECA57A66C73E9D515429B7AF5BC0488FA65F768EDB2713DD789663605B15BA3E5DCE3E4513EFBBB53E9D663E25CC5DEE4C1A0A3E2DF6791D9F66D7268B5F6A26EAFD3BC347378964DB63D4742D2DD26D1B150FD9DBA13F13FCFBB5FB387718D395EDAE21D56D1BAF6851FC67067BB19670A07243B38F5F458A1B57650D647DB57E7A6E55DCFF8EB6DB78B8B99670840A6B6D34113CF7C69B719A9B0569DA9BE770B1F6B960F569E8E3134BFACDB5282FD753BE358D1B030D8ABEBE9F825C0BECDCB22C0DCC050D
	C00D0DE0879B4DFCEC9F86F8845918792BAB9BC603D84B04B652FED05BB0E4ECB7F6CFD2FE7B6673B9CCC67D9D0C25F3707E111E016D22F50EE3BB6E1F036DB89F788416471F0DCAA0DDCB32FB3255ED6A8C176DB8D9930343F753C0AF1B501057B0BD4A47BE6AF5DAB378187BC520C172E3CBA67AA1FD6FCDFC1B9672FD487FA1BE3E880574788CAC73FF396CCB316DE01F207D76C8A268D12F9F381D5BC81B365D18B38D6D3E42E3F88E28EE9A7ABBE7255A5F0F79B759CC105FCAF21F70EB588B52BE5350DF43
	306D5068425F30E9DAFFFB380D8840A7125D4779F34EEBAC0ECC44B670724C05F481E17BF82CA6C837E622FF49A56E2CB3517A671D29754FA81E5B819F1B09E5971C6C58A6E6726721F7D9AC44571026AE63B3B8CF946447D28C8F73E7265CAC08AB8BE38818694E3A9EED8C66ACED8CADFCFC598638B58B6DDE421F37FCA1BD1630A778F34F03C01164CB59D1F73E3D2775A59A74242AFC29614FE5F100A7125DD87EDCF6B7D0D1B65A5DF2441DB5E4DA3B3520A7C3E577A8FFFEEBG5C1C0DF6477167320701BC
	49EE66305F31F455FEFC94F6C59BF8BFC3FD6F4D60ED5B9F788233311D246EF1ECCFF99CFB92E8A271FD3BC75D577DBC6C3761263A70B5D6524BFC956FBBEFEE556E51505238D37576F65CBB2C1CF6AF2610B36CE5F2896007ABD924744F37CB5F35B07367547C4F37EB4E0F594B71A285AE1E11DEAA41675AEA48738392765FAC9D5D79B19F0FD22E32ECF46765BC461F358D533571CE64F87AF4ED3C16B20CB797720626E33C66152373BB39C7A997B5DD54D73C741B16335D181733DD27DDCCDA2014AB11F86A
	255F9678356A4ADD265A4767A17178C83F81709B22ABD7277A5ECA0406A7BA47311D8939EFD56B4092A43E749962B79FFE4BE768597B41614CC5C68D9A5E56990EF6ED9A4C93523EC0018ADC3221BA03A2D44A0F14FEE360B791A8EB26D6EE7BBE25DC41CC353D6A194E4A6293153DC6092FCD7A35432F97101F033D53766915C51046CE180E5D899AAC965206D9B0FE192535F135AA2642D9EA395AD94EE2DA60F0760616747B3512E165747B35AEAEE75271C70FBAE6E13F76F7BEBFB3B8CBFB1DDF4BF14FD95A
	6B3C7A84DE67E3A0AFFE96DE673B8D236BE75E63F3BEAD39DA7B1FF23CA3D7EB5F4B0076ED1097190B76AD13C6574F1CF078EEF9756C6D4646594E4B31CD4A33EBF83CA06D274144BB3163357027749CF0244C3DF7EB172C11E50FD5293F6D7241BAB92FA447BD3DF96BE55941C6355E410DF25E03AB5C53FBF13DAC7BDE25DA6F67DB643C9AB74B2122C116FDDC23777CEF653C5CD277745E5CA44B3E2951DB721D1CF7EF36FBFAF76CB0D07DBF35D356531E671E1E0F1BE5BD3BF74BFA9ECEF3CF4F509ED94FA9
	0A3ADF60E6F9DD31C156B3E9132C67381BFA56ED1675DC2C285BF7FEF1CF4FABDBE5593AB94AEF8D896C0FF354F53A29D1662F4AF66FDBF51D5F4832C5197C3DFB9F2DCBD1344186D76F3D2B4B546BD2364BF26739F92DAD7ACEAE43069D321ECD19A331AF4DCB2C64F158E8DD0AD956F33F2B75AD1AB81EEF16657EDB62DE9C29ADF29C050AB6DE6AEA3D15A60E58E9BC0E810C636BBD321E2F5C280FBBDBE539A8B76B63453DF29CE9BF48FAEC2E56E1E862D81E4663B8CE6BD25AE4BD976F9CF99C576F1765CE
	1B69DE9C0F9F1063C868107594170D5C3E7910ACF75911115A977CEF9C61E5F0904B60E8276C4F9BBF4AFAEF9AF0CFEF32503B1F56A8DAE53D5DBD325E93F67774BEE067FA8F215EB13D32DEE31F2C77795D6E69DD39076B35225E2F78F7A8457B58D9FC3EB688469B6133713D2DBFFF4FBF915221E75359E7FCFD12787B00626F581213FFEEC1EA69493771E4199C53730A3F8FBB2C557F1F376DF9D2ED08BA7D2287572F44D1BD4F647303A9DFECB7461DCDEBDAAA717DFC4E974877A3FF5F5C8169C142F29720
	BEEF428E9D6773C31076BD8775799CC7FDC14477873E2E42B0871E1366A0F6F3856AABA42C063F5B2D05F45D9854F748C06B9B086FAA7EDE438F52C1046DEAC1FDE1046D2575AB938833D4115DBC44BA79DE75DAC8B71ECB6BEBCE10DD628BEA66F6A19DC4589F383EB0C8C7116CBFB99689D88271A5D7235D7439088DA5230DFC42B6B47135AA1076A46CAC0E79C2BA1830A35F233E7104CD1B4B67ECA11DF69E7A32FAAE5A08A33EC33C8F287BF23B517CA5454631CEC8FB9156398FED7891F6539C2ADF42E634
	A21FE19E71F13BB9AC6D033EC413AF8544F7B937EB073497E131640B9FE1D7F2DF02A09DC77AA277E1B90792DF7E8F68CBA4E19D7351EE8CE1615CC6F5BE54F9BE2D435907655CC6589DD486F642FAE95D1B97E19E3C4EA7C0BA0E3037FF65575B7C6D46428B31DF8DBCA177B5B7AA7EDEFB626477BB3BFE879B0986B92654C9725FE79BCE3E6DABCF2A8DD127FFCB46F5D43973311EDFCD46F5D48D97E25B1C1E4230A26029A33ED60E31F5D9E6427E510FED291330CBB9DFB024474F477974623D6C5D053D4BEE
	3530FB3F817779EE666DB88A78E209772612D464A53E0D7CFE1037G5A628264DBDE291E235FD3694EF9845A7711AD20173D2F44F7AC538DFF186F78CE55F7BE3EAB6DD8006FB65673FB0B9D529E645B2F47553EDDF35C1DF79DDA5F02C06F8C15EF3EB39DFD3BF6A67A96C23EBD4F6FD363A1BD01FC5BEED07B762B411D77BFDA5F52960A359232EF213B9CFDFBE2977A163390FD7360773C82C817AC44F7C276E15FA99A95E595F6317A3DF85BC26D3B772BF83FE685FCF0A146FF6204BA7E40936E3CF753469F
	30D03C0712633F3759B17EE71AB17EF16407AF6D1797693B2B5826134E5E3E2E63F00934DB4D10E36A6D6FE27B6371BA082A42F77AE1D40EC57C5E9C8FE9C3811663F9496E3C2F5246D482FA962AE25A5D6498132D89E32AAD4018AEEB1A2E0AE13D356BD08FBCAB59A51910A6DCAB57D8007EDF4A4782031076A4BD0B662A6B683B3943451361A21EF120F72AAA1EDF77BA4673173D98CFA879F16FDE5771BC3797630920F84E65775CC4C827AD427AF8E84EF06DBAC3512E03AE512CB1DB24ED57EB7918259EF0
	53A27475A1CD19FDB4EC19A957C0E94B4CE31118E3164B6C961DB2FB044A4CFB9146BF0356CE7A11EF376F532C0158076DB81C64367231CF82AB3345D8EEBF4F9FAE06EC97B114031ED52A981E550961BD0A21EEB17AD2C3B11888FB15E3C31076A27FCE9FF6DDD53622DE6BAF552C95DAAC572B706F02AA7D7B6435E21C8E7C910B31CCA71FF03CCFBE4C6F65AF839916A05FD77CFE5CEF31DA8E58AD9D52BDAB85E34EDA02711D4631FCC897AD4178427A5D3927EA5FFB37AE91EDC8FEC77F2F944772FFB0855B
	C2A7700BE77E6B945F1477A99969B76505EC06625B6FD3AA5D1B6BACB9A64BDEBBDFBD076A2D783E7B77475D53FB2D625B6F774CD46B1DFAC24EABB638273758A04B3E31CB2D770AA4B9EF3B1BF31361CCCC7A3B60B3D9CF6B89777C5BEF14E55FEDC67FC47A7DAA26CB79B52D9D6C7F0D3DBD7D76769EFBFF7146CD5F1AB7FDC73AEE1FAC6B32A53B6747F30A7D35E7B4297DF82CE91EEBBF9AB7EDB6CBFA3A32E5BDC9F35D7341DB3127B5F02F5A07043DA377A1A6C756F34D9C776AF56ACC39DECFD16CCD3D46
	4DD82ED06CC70D5144D234EFA431885E1C9F5CEB8313947BD9DB663B67F341DCD9B6E31F5A676DA372D9FAEF235843FA404D77DA37674B653FD351A6DF7AFF5DDDEFEC94C59497928D28B192A4A6C6CD1018E8521898B59ACD0CFEB00AD163870CC193021198BEC8A4FEB046E81C1D3B2B2535818B0800C0C9905046BBDAE8A1AD862EDE2F9CB63676D28C04E8375E766EE06F8F3BF3E50BC11DB7373337FB37D01AC81839A637B77F67771B77E6E7F65E3B6DBF57FA0F334B87589929390F0FC550BB779FF57D8E
	FC31B9BB0C4BDC3F7DDE95F45B16363034AD7B0FC791EAD803C284A91D84FD1154GE4AFC10DC385B5555E4D83BDBCF018870E5052231A2ECE379F62C907EA138E72C0978F50DE1A12A3C38950850202A323430374EBAC8931401944880D448786E2B4129C6B27574101E11AB4B49C0D1986EA7550FEE1F177FB2F2CB8E586D0E382ED85EF31B27C6CA5AD95D309FACAB360E0BF0E56958BD91AC95393A8E8322F10C926F5ED88ED1A9C55AE14B413AE27D1C20C6CC40950885ABC84BDC5CAA42BC7CE28E6067958
	768D21E0DE4DE984351533F5B51F0EA4E92DFC3A4FAA5291D522CCA990D3870AF7A3438B2D041DD6B6BE337E3B5DA7D7BD95C0B80E1AF5BAA6F175AFD2B092E89D49981A19A968A92BF86CB8EB28097DC80BED70E9F35B9D7AC0B68CCF958E687356FD37372EEE0D531542B2D0041504CDDA58A9117AB9239A34AB4DC8500FCA3B5C63644D59C9763EBA3865ACC60ACD7282FD187C5AAAD231CD2A392CBECD83B126E4A664ECA3942D2166CD783FE794D9FB95B21E65F490DE3874402207BFB943BA28D4A5E8D7F9
	A8E955C8DAAB64A2514949ECDEF39A2063CA1B5F61B48FE542D5E55A2C851BCF3E7179845503988A648E9CE057CEF66D02EBA7CB69E4A91DDDF4F057222B7ED567DB1CB3D39E0AE7BD4383628F47298449DBEB16BE707651356FD3F6F1941AD27512C6053021D26A57D2EEC2B3EEE9D83423652E1537010C0A20AA96343B53F2DA8406093C2991F36554415E403BCB8298859328F122C0C8A1C715D757A689D5DEA31B4E506FGA9944BFAE74F72EDCCD12051CDB55A88295F542A95A4FF6B8897E23BD8515D6C3A
	E756C3853576323A7B9D061172GF6D4A14562A16D71C72A080774A7E9104624C17A0B97A9C7ABA052D1FF24824E7D537EC8851C7B6352C8BFAD8D52FC85E933DD6E4AF3DF3982522097A9DF0C9BAFEA308C16FB04BA5707C249CB2145AE2B50C53B2D560BA4FA4772B79B7E0CED428853F5BB2315976A5045EC0AE458D28DCFEDF17184B52742121BCE5811A2CB3D6C416D0D894562568A8B64E231D5AE968B3D531B33C0F231383742027F0098D8E039D8BC68E5817DB6494762912FC6F19612E954135E594DD9
	C8B63B1F735EE9B90B2B5DE9C7FDE2C2F1FA59AB19GAFBCA38B01B9AD736724484CE9C56D2398F2B0C94AE9353F1C70D5D8F47B44046274211793FFD98D1A1105401CBE756724C84CC9E9744E27FA101332214229E986CED086AC13E0D8G4BE250C98D5AB9100ABD29A1BB8793549CCD258C4DB43379B4C12D4911C42C5DCE69A5684B4101E1F7B4B9566F0A36020D401DFDA6B1624E86FB02BB8EE686D7FC9D37C050F0B3B3CB0C19EADA23856DB2BC9E84D4AE990D33F1830379B5BE2CC2EC8AAE567FAB2DF1
	DBDA7BACDF8D3A416838AE7DD851A56E2844569D11B19F72476CFFE0A58866BEFF4C629D88A97D33B93A92E4F4933399DDC1B00FC80879778AE6BCA3669B2A45FFF877B2D2E831A621C66852E804B933C60C339C01768E17AC11515DE44935EF1E677FFC63FD8E6DA628256CA2E3EFB0ED0719CEA75465048E3A5DE96CD037936AF1C2071D5091CFDF621DFDA9774C7E6CCBC096CB6A619CD2E92807B3E16509FAB81FD71E2D876B1B72E2BDD85F14576B417A26AC3FF66B1BD0385F71F796924F31C5D96D0FD4BC
	47966583D99C30140F24C17A19ACC8F1D096E7A15CA20BD39BEE1345298DEF15C67A6D52A05DF79D1C5AA0F69DCFD2F078CADEEC6E7D00F856DD5C635DD302C6049CCC32582871B1FF96B37B92884562C46D1AA721ACFE7317051F3F00D038CF571EAFE18713D0C847EB110265D740BBF915A33540E36AF22466D679EA24632C36B8A73838A432ED9A7FAD1FED9A4F154FB60D674BE21B46F74AE71B46F74BE7554377CB08596587AE36A592B7F8D7DFA93478B189B562E9E9B462F979EC5378A57F3D438D9D4757
	7C7735183D4C84FD7F6F91FB884FAEF888FDFC738E9C32873B7ADDBBA4DBFE477EBC7B3B9DBD2E40CBB83EC217BB583B01E87ACA7A590E587B6BFF007F2DF5CABD97AD967B7514B1104A1202C1A33D3D5189124C293ACED47DFCB41FBA29CE297D53C403176C166B4E6D0076CDDE464EAC674D6FG5C67DCF57E83D0CB8788DACABB15D1E4GGA82A81GD0CB818294G94G88G88GAC0A09AEDACABB15D1E4GGA82A81G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB85
	86GGGG81G81GBAGGG0BE4GGGG
**end of data**/
}

/**
 * Return the JCheckBoxFEED property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxFEED() {
	if (ivjJCheckBoxFEED == null) {
		try {
			ivjJCheckBoxFEED = new javax.swing.JCheckBox();
			ivjJCheckBoxFEED.setName("JCheckBoxFEED");
			ivjJCheckBoxFEED.setMnemonic('f');
			ivjJCheckBoxFEED.setText("FEED");
			ivjJCheckBoxFEED.setMaximumSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxFEED.setPreferredSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxFEED.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxFEED.setMinimumSize(new java.awt.Dimension(53, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxFEED;
}

/**
 * Return the JCheckBoxGEO property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxGEO() {
	if (ivjJCheckBoxGEO == null) {
		try {
			ivjJCheckBoxGEO = new javax.swing.JCheckBox();
			ivjJCheckBoxGEO.setName("JCheckBoxGEO");
			ivjJCheckBoxGEO.setMnemonic('g');
			ivjJCheckBoxGEO.setText("GEO");
			ivjJCheckBoxGEO.setMaximumSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxGEO.setPreferredSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxGEO.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxGEO.setMinimumSize(new java.awt.Dimension(53, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxGEO;
}

/**
 * Return the JCheckBoxLOAD property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxLOAD() {
	if (ivjJCheckBoxLOAD == null) {
		try {
			ivjJCheckBoxLOAD = new javax.swing.JCheckBox();
			ivjJCheckBoxLOAD.setName("JCheckBoxLOAD");
			ivjJCheckBoxLOAD.setMnemonic('l');
			ivjJCheckBoxLOAD.setText("LOAD");
			ivjJCheckBoxLOAD.setMaximumSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxLOAD.setPreferredSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxLOAD.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxLOAD.setMinimumSize(new java.awt.Dimension(53, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxLOAD;
}

/**
 * Return the JCheckBoxPROG property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxPROG() {
	if (ivjJCheckBoxPROG == null) {
		try {
			ivjJCheckBoxPROG = new javax.swing.JCheckBox();
			ivjJCheckBoxPROG.setName("JCheckBoxPROG");
			ivjJCheckBoxPROG.setMnemonic('p');
			ivjJCheckBoxPROG.setText("PROG");
			ivjJCheckBoxPROG.setMaximumSize(new java.awt.Dimension(82, 22));
			ivjJCheckBoxPROG.setPreferredSize(new java.awt.Dimension(82, 22));
			ivjJCheckBoxPROG.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxPROG.setMinimumSize(new java.awt.Dimension(82, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxPROG;
}

/**
 * Return the JCheckBoxRelay1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRelay1() {
	if (ivjJCheckBoxRelay1 == null) {
		try {
			ivjJCheckBoxRelay1 = new javax.swing.JCheckBox();
			ivjJCheckBoxRelay1.setName("JCheckBoxRelay1");
			ivjJCheckBoxRelay1.setMnemonic('1');
			ivjJCheckBoxRelay1.setText("Load 1");
			ivjJCheckBoxRelay1.setMaximumSize(new java.awt.Dimension(70, 22));
			ivjJCheckBoxRelay1.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxRelay1.setMinimumSize(new java.awt.Dimension(70, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRelay1;
}

/**
 * Return the JCheckBoxRelay2 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRelay2() {
	if (ivjJCheckBoxRelay2 == null) {
		try {
			ivjJCheckBoxRelay2 = new javax.swing.JCheckBox();
			ivjJCheckBoxRelay2.setName("JCheckBoxRelay2");
			ivjJCheckBoxRelay2.setMnemonic('2');
			ivjJCheckBoxRelay2.setText("Load 2");
			ivjJCheckBoxRelay2.setMaximumSize(new java.awt.Dimension(70, 22));
			ivjJCheckBoxRelay2.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxRelay2.setMinimumSize(new java.awt.Dimension(70, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRelay2;
}

/**
 * Return the JCheckBoxRelay3 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRelay3() {
	if (ivjJCheckBoxRelay3 == null) {
		try {
			ivjJCheckBoxRelay3 = new javax.swing.JCheckBox();
			ivjJCheckBoxRelay3.setName("JCheckBoxRelay3");
			ivjJCheckBoxRelay3.setMnemonic('3');
			ivjJCheckBoxRelay3.setText("Load 3");
			ivjJCheckBoxRelay3.setMaximumSize(new java.awt.Dimension(70, 22));
			ivjJCheckBoxRelay3.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxRelay3.setMinimumSize(new java.awt.Dimension(70, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRelay3;
}

/**
 * Return the JCheckBoxRelay4 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRelay4() {
	if (ivjJCheckBoxRelay4 == null) {
		try {
			ivjJCheckBoxRelay4 = new javax.swing.JCheckBox();
			ivjJCheckBoxRelay4.setName("JCheckBoxRelay4");
			ivjJCheckBoxRelay4.setMnemonic('4');
			ivjJCheckBoxRelay4.setText("Load 4");
			ivjJCheckBoxRelay4.setMaximumSize(new java.awt.Dimension(70, 22));
			ivjJCheckBoxRelay4.setActionCommand("Relay 4");
			ivjJCheckBoxRelay4.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxRelay4.setMinimumSize(new java.awt.Dimension(70, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRelay4;
}

/**
 * Return the JCheckBoxRelay5 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRelay5() {
	if (ivjJCheckBoxRelay5 == null) {
		try {
			ivjJCheckBoxRelay5 = new javax.swing.JCheckBox();
			ivjJCheckBoxRelay5.setName("JCheckBoxRelay5");
			ivjJCheckBoxRelay5.setMnemonic('5');
			ivjJCheckBoxRelay5.setText("Load 5");
			ivjJCheckBoxRelay5.setMaximumSize(new java.awt.Dimension(70, 22));
			ivjJCheckBoxRelay5.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxRelay5.setMinimumSize(new java.awt.Dimension(70, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRelay5;
}

/**
 * Return the JCheckBoxRelay6 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRelay6() {
	if (ivjJCheckBoxRelay6 == null) {
		try {
			ivjJCheckBoxRelay6 = new javax.swing.JCheckBox();
			ivjJCheckBoxRelay6.setName("JCheckBoxRelay6");
			ivjJCheckBoxRelay6.setMnemonic('6');
			ivjJCheckBoxRelay6.setText("Load 6");
			ivjJCheckBoxRelay6.setMaximumSize(new java.awt.Dimension(70, 22));
			ivjJCheckBoxRelay6.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxRelay6.setMinimumSize(new java.awt.Dimension(70, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRelay6;
}

/**
 * Return the JCheckBoxRelay7 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRelay7() {
	if (ivjJCheckBoxRelay7 == null) {
		try {
			ivjJCheckBoxRelay7 = new javax.swing.JCheckBox();
			ivjJCheckBoxRelay7.setName("JCheckBoxRelay7");
			ivjJCheckBoxRelay7.setMnemonic('7');
			ivjJCheckBoxRelay7.setText("Load 7");
			ivjJCheckBoxRelay7.setMaximumSize(new java.awt.Dimension(70, 22));
			ivjJCheckBoxRelay7.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxRelay7.setMinimumSize(new java.awt.Dimension(70, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRelay7;
}

/**
 * Return the JCheckBoxRelay8 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRelay8() {
	if (ivjJCheckBoxRelay8 == null) {
		try {
			ivjJCheckBoxRelay8 = new javax.swing.JCheckBox();
			ivjJCheckBoxRelay8.setName("JCheckBoxRelay8");
			ivjJCheckBoxRelay8.setMnemonic('8');
			ivjJCheckBoxRelay8.setText("Load 8");
			ivjJCheckBoxRelay8.setMaximumSize(new java.awt.Dimension(70, 22));
			ivjJCheckBoxRelay8.setActionCommand("Relay 4");
			ivjJCheckBoxRelay8.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxRelay8.setMinimumSize(new java.awt.Dimension(70, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRelay8;
}

/**
 * Return the JCheckBoxIndividual property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSerial() {
	if (ivjJCheckBoxSerial == null) {
		try {
			ivjJCheckBoxSerial = new javax.swing.JCheckBox();
			ivjJCheckBoxSerial.setName("JCheckBoxSerial");
			ivjJCheckBoxSerial.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxSerial.setText("Serial:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSerial;
}


/**
 * Return the JCheckBoxSPID property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSPID() {
	if (ivjJCheckBoxSPID == null) {
		try {
			ivjJCheckBoxSPID = new javax.swing.JCheckBox();
			ivjJCheckBoxSPID.setName("JCheckBoxSPID");
			ivjJCheckBoxSPID.setMnemonic('s');
			ivjJCheckBoxSPID.setText("SPID");
			ivjJCheckBoxSPID.setMaximumSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxSPID.setSelected(true);
			ivjJCheckBoxSPID.setPreferredSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxSPID.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxSPID.setEnabled(false);
			ivjJCheckBoxSPID.setMinimumSize(new java.awt.Dimension(53, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSPID;
}

/**
 * Return the JCheckBoxSPLINTER property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSPLINTER() {
	if (ivjJCheckBoxSPLINTER == null) {
		try {
			ivjJCheckBoxSPLINTER = new javax.swing.JCheckBox();
			ivjJCheckBoxSPLINTER.setName("JCheckBoxSPLINTER");
			ivjJCheckBoxSPLINTER.setMnemonic('r');
			ivjJCheckBoxSPLINTER.setText("SPLINTER");
			ivjJCheckBoxSPLINTER.setMaximumSize(new java.awt.Dimension(82, 22));
			ivjJCheckBoxSPLINTER.setPreferredSize(new java.awt.Dimension(82, 22));
			ivjJCheckBoxSPLINTER.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxSPLINTER.setMinimumSize(new java.awt.Dimension(82, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSPLINTER;
}

/**
 * Return the JCheckBoxSUB property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSUB() {
	if (ivjJCheckBoxSUB == null) {
		try {
			ivjJCheckBoxSUB = new javax.swing.JCheckBox();
			ivjJCheckBoxSUB.setName("JCheckBoxSUB");
			ivjJCheckBoxSUB.setMnemonic('b');
			ivjJCheckBoxSUB.setText("SUB");
			ivjJCheckBoxSUB.setMaximumSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxSUB.setPreferredSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxSUB.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxSUB.setMinimumSize(new java.awt.Dimension(53, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSUB;
}

/**
 * Return the JCheckBoxUSER property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxUSER() {
	if (ivjJCheckBoxUSER == null) {
		try {
			ivjJCheckBoxUSER = new javax.swing.JCheckBox();
			ivjJCheckBoxUSER.setName("JCheckBoxUSER");
			ivjJCheckBoxUSER.setMnemonic('u');
			ivjJCheckBoxUSER.setText("USER");
			ivjJCheckBoxUSER.setMaximumSize(new java.awt.Dimension(82, 22));
			ivjJCheckBoxUSER.setPreferredSize(new java.awt.Dimension(82, 22));
			ivjJCheckBoxUSER.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxUSER.setMinimumSize(new java.awt.Dimension(82, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxUSER;
}

/**
 * Return the JCheckBoxZIP property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxZIP() {
	if (ivjJCheckBoxZIP == null) {
		try {
			ivjJCheckBoxZIP = new javax.swing.JCheckBox();
			ivjJCheckBoxZIP.setName("JCheckBoxZIP");
			ivjJCheckBoxZIP.setMnemonic('z');
			ivjJCheckBoxZIP.setText("ZIP");
			ivjJCheckBoxZIP.setMaximumSize(new java.awt.Dimension(82, 22));
			ivjJCheckBoxZIP.setPreferredSize(new java.awt.Dimension(82, 22));
			ivjJCheckBoxZIP.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxZIP.setMinimumSize(new java.awt.Dimension(82, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxZIP;
}

/**
 * Return the JComboBoxFEED property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxFEED() {
	if (ivjJComboBoxFEED == null) {
		try {
			ivjJComboBoxFEED = new javax.swing.JComboBox();
			ivjJComboBoxFEED.setName("JComboBoxFEED");
			ivjJComboBoxFEED.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJComboBoxFEED.setEditable(true);
			// user code begin {1}

			ivjJComboBoxFEED.addItem( STRING_NEW );
			ivjJComboBoxFEED.addItem( com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxFEED;
}


/**
 * Return the JComboBoxGEO property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGEO() {
	if (ivjJComboBoxGEO == null) {
		try {
			ivjJComboBoxGEO = new javax.swing.JComboBox();
			ivjJComboBoxGEO.setName("JComboBoxGEO");
			ivjJComboBoxGEO.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJComboBoxGEO.setEditable(true);
			// user code begin {1}

			ivjJComboBoxGEO.addItem( STRING_NEW );
			ivjJComboBoxGEO.addItem( com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGEO;
}


/**
 * Return the JComboBoxPROG property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxPROG() {
	if (ivjJComboBoxPROG == null) {
		try {
			ivjJComboBoxPROG = new javax.swing.JComboBox();
			ivjJComboBoxPROG.setName("JComboBoxPROG");
			ivjJComboBoxPROG.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJComboBoxPROG.setEditable(true);
			// user code begin {1}

			ivjJComboBoxPROG.addItem( STRING_NEW );
			ivjJComboBoxPROG.addItem( com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxPROG;
}


/**
 * Return the JComboBoxSPID property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSPID() {
	if (ivjJComboBoxSPID == null) {
		try {
			ivjJComboBoxSPID = new javax.swing.JComboBox();
			ivjJComboBoxSPID.setName("JComboBoxSPID");
			ivjJComboBoxSPID.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJComboBoxSPID.setEditable(true);
			// user code begin {1}

			ivjJComboBoxSPID.addItem( STRING_NEW );
			//ivjJComboBoxSPID.addItem( com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSPID;
}


/**
 * Return the JComboBoxSUB property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSUB() {
	if (ivjJComboBoxSUB == null) {
		try {
			ivjJComboBoxSUB = new javax.swing.JComboBox();
			ivjJComboBoxSUB.setName("JComboBoxSUB");
			ivjJComboBoxSUB.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJComboBoxSUB.setEditable(true);
			// user code begin {1}

			ivjJComboBoxSUB.addItem( STRING_NEW );
			ivjJComboBoxSUB.addItem( com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSUB;
}


/**
 * Return the JLabelFeedAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFeedAddress() {
	if (ivjJLabelFeedAddress == null) {
		try {
			ivjJLabelFeedAddress = new javax.swing.JLabel();
			ivjJLabelFeedAddress.setName("JLabelFeedAddress");
			ivjJLabelFeedAddress.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelFeedAddress.setText("FEED Address:");
			ivjJLabelFeedAddress.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFeedAddress;
}


/**
 * Return the JLabelGEOAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGEOAddress() {
	if (ivjJLabelGEOAddress == null) {
		try {
			ivjJLabelGEOAddress = new javax.swing.JLabel();
			ivjJLabelGEOAddress.setName("JLabelGEOAddress");
			ivjJLabelGEOAddress.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelGEOAddress.setText("GEO Address:");
			ivjJLabelGEOAddress.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGEOAddress;
}


/**
 * Return the JLabelPROGAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPROGAddress() {
	if (ivjJLabelPROGAddress == null) {
		try {
			ivjJLabelPROGAddress = new javax.swing.JLabel();
			ivjJLabelPROGAddress.setName("JLabelPROGAddress");
			ivjJLabelPROGAddress.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelPROGAddress.setText("PROG Address:");
			ivjJLabelPROGAddress.setEnabled(true);
			ivjJLabelPROGAddress.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPROGAddress;
}


/**
 * Return the JLabelSPID property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSPID() {
	if (ivjJLabelSPID == null) {
		try {
			ivjJLabelSPID = new javax.swing.JLabel();
			ivjJLabelSPID.setName("JLabelSPID");
			ivjJLabelSPID.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelSPID.setText("SPID Address:");
			ivjJLabelSPID.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSPID;
}


/**
 * Return the JLabelSplinter property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSplinter() {
	if (ivjJLabelSplinter == null) {
		try {
			ivjJLabelSplinter = new javax.swing.JLabel();
			ivjJLabelSplinter.setName("JLabelSplinter");
			ivjJLabelSplinter.setText("SPLINTER:");
			ivjJLabelSplinter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelSplinter.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelSplinter.setEnabled(true);
			ivjJLabelSplinter.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSplinter;
}


/**
 * Return the JLabelSubAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSubAddress() {
	if (ivjJLabelSubAddress == null) {
		try {
			ivjJLabelSubAddress = new javax.swing.JLabel();
			ivjJLabelSubAddress.setName("JLabelSubAddress");
			ivjJLabelSubAddress.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelSubAddress.setText("SUB Address:");
			ivjJLabelSubAddress.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSubAddress;
}


/**
 * Return the JLabelUserAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelUserAddress() {
	if (ivjJLabelUserAddress == null) {
		try {
			ivjJLabelUserAddress = new javax.swing.JLabel();
			ivjJLabelUserAddress.setName("JLabelUserAddress");
			ivjJLabelUserAddress.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelUserAddress.setText("USER Address:");
			ivjJLabelUserAddress.setEnabled(true);
			ivjJLabelUserAddress.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelUserAddress;
}


/**
 * Return the JLabelZipAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelZipAddress() {
	if (ivjJLabelZipAddress == null) {
		try {
			ivjJLabelZipAddress = new javax.swing.JLabel();
			ivjJLabelZipAddress.setName("JLabelZipAddress");
			ivjJLabelZipAddress.setText("ZIP Address:");
			ivjJLabelZipAddress.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelZipAddress.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelZipAddress.setEnabled(true);
			ivjJLabelZipAddress.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelZipAddress;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelAddress() {
	if (ivjJPanelAddress == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Addressing");
			ivjJPanelAddress = new javax.swing.JPanel();
			ivjJPanelAddress.setName("JPanelAddress");
			ivjJPanelAddress.setBorder(ivjLocalBorder);
			ivjJPanelAddress.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTextFieldSPIDAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldSPIDAddress.gridx = 2; constraintsJTextFieldSPIDAddress.gridy = 1;
			constraintsJTextFieldSPIDAddress.gridwidth = 2;
			constraintsJTextFieldSPIDAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSPIDAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldSPIDAddress.weightx = 1.0;
			constraintsJTextFieldSPIDAddress.ipadx = 87;
			constraintsJTextFieldSPIDAddress.ipady = -1;
			constraintsJTextFieldSPIDAddress.insets = new java.awt.Insets(3, 1, 1, 3);
			getJPanelAddress().add(getJTextFieldSPIDAddress(), constraintsJTextFieldSPIDAddress);

			java.awt.GridBagConstraints constraintsJLabelSPID = new java.awt.GridBagConstraints();
			constraintsJLabelSPID.gridx = 1; constraintsJLabelSPID.gridy = 1;
			constraintsJLabelSPID.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelSPID.ipadx = 9;
			constraintsJLabelSPID.ipady = 4;
			constraintsJLabelSPID.insets = new java.awt.Insets(3, 11, 3, 1);
			getJPanelAddress().add(getJLabelSPID(), constraintsJLabelSPID);

			java.awt.GridBagConstraints constraintsJLabelGEOAddress = new java.awt.GridBagConstraints();
			constraintsJLabelGEOAddress.gridx = 1; constraintsJLabelGEOAddress.gridy = 2;
			constraintsJLabelGEOAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGEOAddress.ipadx = 10;
			constraintsJLabelGEOAddress.ipady = 4;
			constraintsJLabelGEOAddress.insets = new java.awt.Insets(2, 11, 3, 1);
			getJPanelAddress().add(getJLabelGEOAddress(), constraintsJLabelGEOAddress);

			java.awt.GridBagConstraints constraintsJTextFieldGeoAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldGeoAddress.gridx = 2; constraintsJTextFieldGeoAddress.gridy = 2;
			constraintsJTextFieldGeoAddress.gridwidth = 2;
			constraintsJTextFieldGeoAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldGeoAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldGeoAddress.weightx = 1.0;
			constraintsJTextFieldGeoAddress.ipadx = 87;
			constraintsJTextFieldGeoAddress.ipady = -1;
			constraintsJTextFieldGeoAddress.insets = new java.awt.Insets(2, 1, 1, 3);
			getJPanelAddress().add(getJTextFieldGeoAddress(), constraintsJTextFieldGeoAddress);

			java.awt.GridBagConstraints constraintsJLabelSubAddress = new java.awt.GridBagConstraints();
			constraintsJLabelSubAddress.gridx = 1; constraintsJLabelSubAddress.gridy = 3;
			constraintsJLabelSubAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelSubAddress.ipadx = 12;
			constraintsJLabelSubAddress.ipady = 4;
			constraintsJLabelSubAddress.insets = new java.awt.Insets(1, 11, 3, 1);
			getJPanelAddress().add(getJLabelSubAddress(), constraintsJLabelSubAddress);

			java.awt.GridBagConstraints constraintsJTextFieldSubAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldSubAddress.gridx = 2; constraintsJTextFieldSubAddress.gridy = 3;
			constraintsJTextFieldSubAddress.gridwidth = 2;
			constraintsJTextFieldSubAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSubAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldSubAddress.weightx = 1.0;
			constraintsJTextFieldSubAddress.ipadx = 87;
			constraintsJTextFieldSubAddress.ipady = -1;
			constraintsJTextFieldSubAddress.insets = new java.awt.Insets(1, 1, 1, 3);
			getJPanelAddress().add(getJTextFieldSubAddress(), constraintsJTextFieldSubAddress);

			java.awt.GridBagConstraints constraintsJLabelFeedAddress = new java.awt.GridBagConstraints();
			constraintsJLabelFeedAddress.gridx = 1; constraintsJLabelFeedAddress.gridy = 4;
			constraintsJLabelFeedAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFeedAddress.ipadx = 6;
			constraintsJLabelFeedAddress.ipady = 4;
			constraintsJLabelFeedAddress.insets = new java.awt.Insets(1, 11, 3, 1);
			getJPanelAddress().add(getJLabelFeedAddress(), constraintsJLabelFeedAddress);

			java.awt.GridBagConstraints constraintsJTextFieldFeedAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldFeedAddress.gridx = 2; constraintsJTextFieldFeedAddress.gridy = 4;
			constraintsJTextFieldFeedAddress.gridwidth = 2;
			constraintsJTextFieldFeedAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldFeedAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldFeedAddress.weightx = 1.0;
			constraintsJTextFieldFeedAddress.ipadx = 87;
			constraintsJTextFieldFeedAddress.ipady = -1;
			constraintsJTextFieldFeedAddress.insets = new java.awt.Insets(1, 1, 1, 3);
			getJPanelAddress().add(getJTextFieldFeedAddress(), constraintsJTextFieldFeedAddress);

			java.awt.GridBagConstraints constraintsJLabelZipAddress = new java.awt.GridBagConstraints();
			constraintsJLabelZipAddress.gridx = 1; constraintsJLabelZipAddress.gridy = 5;
			constraintsJLabelZipAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelZipAddress.ipadx = 17;
			constraintsJLabelZipAddress.ipady = 4;
			constraintsJLabelZipAddress.insets = new java.awt.Insets(1, 11, 3, 1);
			getJPanelAddress().add(getJLabelZipAddress(), constraintsJLabelZipAddress);

			java.awt.GridBagConstraints constraintsJTextFieldZipAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldZipAddress.gridx = 2; constraintsJTextFieldZipAddress.gridy = 5;
			constraintsJTextFieldZipAddress.gridwidth = 2;
			constraintsJTextFieldZipAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldZipAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldZipAddress.weightx = 1.0;
			constraintsJTextFieldZipAddress.ipadx = 87;
			constraintsJTextFieldZipAddress.ipady = -1;
			constraintsJTextFieldZipAddress.insets = new java.awt.Insets(1, 1, 1, 3);
			getJPanelAddress().add(getJTextFieldZipAddress(), constraintsJTextFieldZipAddress);

			java.awt.GridBagConstraints constraintsJLabelUserAddress = new java.awt.GridBagConstraints();
			constraintsJLabelUserAddress.gridx = 1; constraintsJLabelUserAddress.gridy = 6;
			constraintsJLabelUserAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelUserAddress.ipadx = 5;
			constraintsJLabelUserAddress.ipady = 4;
			constraintsJLabelUserAddress.insets = new java.awt.Insets(1, 11, 2, 1);
			getJPanelAddress().add(getJLabelUserAddress(), constraintsJLabelUserAddress);

			java.awt.GridBagConstraints constraintsJTextFieldUserAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldUserAddress.gridx = 2; constraintsJTextFieldUserAddress.gridy = 6;
			constraintsJTextFieldUserAddress.gridwidth = 2;
			constraintsJTextFieldUserAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldUserAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldUserAddress.weightx = 1.0;
			constraintsJTextFieldUserAddress.ipadx = 87;
			constraintsJTextFieldUserAddress.ipady = -1;
			constraintsJTextFieldUserAddress.insets = new java.awt.Insets(1, 1, 0, 3);
			getJPanelAddress().add(getJTextFieldUserAddress(), constraintsJTextFieldUserAddress);

			java.awt.GridBagConstraints constraintsJLabelPROGAddress = new java.awt.GridBagConstraints();
			constraintsJLabelPROGAddress.gridx = 1; constraintsJLabelPROGAddress.gridy = 7;
			constraintsJLabelPROGAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPROGAddress.ipadx = 3;
			constraintsJLabelPROGAddress.ipady = 4;
			constraintsJLabelPROGAddress.insets = new java.awt.Insets(1, 11, 3, 1);
			getJPanelAddress().add(getJLabelPROGAddress(), constraintsJLabelPROGAddress);

			java.awt.GridBagConstraints constraintsJLabelSplinter = new java.awt.GridBagConstraints();
			constraintsJLabelSplinter.gridx = 1; constraintsJLabelSplinter.gridy = 8;
			constraintsJLabelSplinter.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelSplinter.ipadx = 28;
			constraintsJLabelSplinter.ipady = 4;
			constraintsJLabelSplinter.insets = new java.awt.Insets(1, 11, 2, 1);
			getJPanelAddress().add(getJLabelSplinter(), constraintsJLabelSplinter);

			java.awt.GridBagConstraints constraintsJTextFieldSplinter = new java.awt.GridBagConstraints();
			constraintsJTextFieldSplinter.gridx = 2; constraintsJTextFieldSplinter.gridy = 8;
			constraintsJTextFieldSplinter.gridwidth = 2;
			constraintsJTextFieldSplinter.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSplinter.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldSplinter.weightx = 1.0;
			constraintsJTextFieldSplinter.ipadx = 87;
			constraintsJTextFieldSplinter.ipady = -1;
			constraintsJTextFieldSplinter.insets = new java.awt.Insets(1, 1, 0, 3);
			getJPanelAddress().add(getJTextFieldSplinter(), constraintsJTextFieldSplinter);

			java.awt.GridBagConstraints constraintsJTextFieldProgAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldProgAddress.gridx = 2; constraintsJTextFieldProgAddress.gridy = 7;
			constraintsJTextFieldProgAddress.gridwidth = 2;
			constraintsJTextFieldProgAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldProgAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldProgAddress.weightx = 1.0;
			constraintsJTextFieldProgAddress.ipadx = 87;
			constraintsJTextFieldProgAddress.ipady = -1;
			constraintsJTextFieldProgAddress.insets = new java.awt.Insets(1, 1, 1, 3);
			getJPanelAddress().add(getJTextFieldProgAddress(), constraintsJTextFieldProgAddress);

			java.awt.GridBagConstraints constraintsJTextFieldSerialAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldSerialAddress.gridx = 3; constraintsJTextFieldSerialAddress.gridy = 9;
			constraintsJTextFieldSerialAddress.gridwidth = 2;
			constraintsJTextFieldSerialAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSerialAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldSerialAddress.weightx = 1.0;
			constraintsJTextFieldSerialAddress.ipadx = 182;
			constraintsJTextFieldSerialAddress.ipady = -1;
			constraintsJTextFieldSerialAddress.insets = new java.awt.Insets(1, 0, 10, 26);
			getJPanelAddress().add(getJTextFieldSerialAddress(), constraintsJTextFieldSerialAddress);

			java.awt.GridBagConstraints constraintsJCheckBoxSerial = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSerial.gridx = 1; constraintsJCheckBoxSerial.gridy = 9;
			constraintsJCheckBoxSerial.gridwidth = 2;
			constraintsJCheckBoxSerial.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxSerial.ipadx = 22;
			constraintsJCheckBoxSerial.ipady = -4;
			constraintsJCheckBoxSerial.insets = new java.awt.Insets(1, 11, 10, 0);
			getJPanelAddress().add(getJCheckBoxSerial(), constraintsJCheckBoxSerial);

			java.awt.GridBagConstraints constraintsJComboBoxSPID = new java.awt.GridBagConstraints();
			constraintsJComboBoxSPID.gridx = 4; constraintsJComboBoxSPID.gridy = 1;
			constraintsJComboBoxSPID.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxSPID.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxSPID.weightx = 1.0;
			constraintsJComboBoxSPID.ipadx = 12;
			constraintsJComboBoxSPID.insets = new java.awt.Insets(3, 3, 1, 26);
			getJPanelAddress().add(getJComboBoxSPID(), constraintsJComboBoxSPID);

			java.awt.GridBagConstraints constraintsJComboBoxGEO = new java.awt.GridBagConstraints();
			constraintsJComboBoxGEO.gridx = 4; constraintsJComboBoxGEO.gridy = 2;
			constraintsJComboBoxGEO.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxGEO.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxGEO.weightx = 1.0;
			constraintsJComboBoxGEO.ipadx = 12;
			constraintsJComboBoxGEO.insets = new java.awt.Insets(2, 3, 1, 26);
			getJPanelAddress().add(getJComboBoxGEO(), constraintsJComboBoxGEO);

			java.awt.GridBagConstraints constraintsJComboBoxSUB = new java.awt.GridBagConstraints();
			constraintsJComboBoxSUB.gridx = 4; constraintsJComboBoxSUB.gridy = 3;
			constraintsJComboBoxSUB.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxSUB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxSUB.weightx = 1.0;
			constraintsJComboBoxSUB.ipadx = 12;
			constraintsJComboBoxSUB.insets = new java.awt.Insets(1, 3, 1, 26);
			getJPanelAddress().add(getJComboBoxSUB(), constraintsJComboBoxSUB);

			java.awt.GridBagConstraints constraintsJComboBoxFEED = new java.awt.GridBagConstraints();
			constraintsJComboBoxFEED.gridx = 4; constraintsJComboBoxFEED.gridy = 4;
			constraintsJComboBoxFEED.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxFEED.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxFEED.weightx = 1.0;
			constraintsJComboBoxFEED.ipadx = 12;
			constraintsJComboBoxFEED.insets = new java.awt.Insets(1, 3, 1, 26);
			getJPanelAddress().add(getJComboBoxFEED(), constraintsJComboBoxFEED);

			java.awt.GridBagConstraints constraintsJComboBoxPROG = new java.awt.GridBagConstraints();
			constraintsJComboBoxPROG.gridx = 4; constraintsJComboBoxPROG.gridy = 7;
			constraintsJComboBoxPROG.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxPROG.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxPROG.weightx = 1.0;
			constraintsJComboBoxPROG.ipadx = 12;
			constraintsJComboBoxPROG.insets = new java.awt.Insets(1, 3, 1, 26);
			getJPanelAddress().add(getJComboBoxPROG(), constraintsJComboBoxPROG);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelAddress;
}


/**
 * Return the JPanelAddressTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getJPanelAddressTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjJPanelAddressTitleBorder = null;
	try {
		/* Create part */
		ivjJPanelAddressTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjJPanelAddressTitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjJPanelAddressTitleBorder.setTitle("Addressing");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelAddressTitleBorder;
}


/**
 * Return the JPanelAddressUsage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelAddressUsage() {
	if (ivjJPanelAddressUsage == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Address Usage");
			ivjJPanelAddressUsage = new javax.swing.JPanel();
			ivjJPanelAddressUsage.setName("JPanelAddressUsage");
			ivjJPanelAddressUsage.setBorder(ivjLocalBorder1);
			ivjJPanelAddressUsage.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxSPID = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSPID.gridx = 1; constraintsJCheckBoxSPID.gridy = 1;
			constraintsJCheckBoxSPID.ipadx = 2;
			constraintsJCheckBoxSPID.ipady = -4;
			constraintsJCheckBoxSPID.insets = new java.awt.Insets(2, 12, 0, 3);
			getJPanelAddressUsage().add(getJCheckBoxSPID(), constraintsJCheckBoxSPID);

			java.awt.GridBagConstraints constraintsJCheckBoxGEO = new java.awt.GridBagConstraints();
			constraintsJCheckBoxGEO.gridx = 1; constraintsJCheckBoxGEO.gridy = 2;
			constraintsJCheckBoxGEO.ipadx = 2;
			constraintsJCheckBoxGEO.ipady = -4;
			constraintsJCheckBoxGEO.insets = new java.awt.Insets(0, 12, 1, 3);
			getJPanelAddressUsage().add(getJCheckBoxGEO(), constraintsJCheckBoxGEO);

			java.awt.GridBagConstraints constraintsJCheckBoxSUB = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSUB.gridx = 1; constraintsJCheckBoxSUB.gridy = 3;
			constraintsJCheckBoxSUB.ipadx = 2;
			constraintsJCheckBoxSUB.ipady = -4;
			constraintsJCheckBoxSUB.insets = new java.awt.Insets(1, 12, 0, 3);
			getJPanelAddressUsage().add(getJCheckBoxSUB(), constraintsJCheckBoxSUB);

			java.awt.GridBagConstraints constraintsJCheckBoxFEED = new java.awt.GridBagConstraints();
			constraintsJCheckBoxFEED.gridx = 1; constraintsJCheckBoxFEED.gridy = 4;
			constraintsJCheckBoxFEED.ipadx = 2;
			constraintsJCheckBoxFEED.ipady = -4;
			constraintsJCheckBoxFEED.insets = new java.awt.Insets(0, 12, 0, 3);
			getJPanelAddressUsage().add(getJCheckBoxFEED(), constraintsJCheckBoxFEED);

			java.awt.GridBagConstraints constraintsJCheckBoxZIP = new java.awt.GridBagConstraints();
			constraintsJCheckBoxZIP.gridx = 2; constraintsJCheckBoxZIP.gridy = 1;
			constraintsJCheckBoxZIP.ipady = -4;
			constraintsJCheckBoxZIP.insets = new java.awt.Insets(2, 3, 0, 0);
			getJPanelAddressUsage().add(getJCheckBoxZIP(), constraintsJCheckBoxZIP);

			java.awt.GridBagConstraints constraintsJCheckBoxSPLINTER = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSPLINTER.gridx = 2; constraintsJCheckBoxSPLINTER.gridy = 4;
			constraintsJCheckBoxSPLINTER.ipady = -4;
			constraintsJCheckBoxSPLINTER.insets = new java.awt.Insets(0, 3, 0, 0);
			getJPanelAddressUsage().add(getJCheckBoxSPLINTER(), constraintsJCheckBoxSPLINTER);

			java.awt.GridBagConstraints constraintsJCheckBoxPROG = new java.awt.GridBagConstraints();
			constraintsJCheckBoxPROG.gridx = 2; constraintsJCheckBoxPROG.gridy = 3;
			constraintsJCheckBoxPROG.ipady = -4;
			constraintsJCheckBoxPROG.insets = new java.awt.Insets(1, 3, 0, 0);
			getJPanelAddressUsage().add(getJCheckBoxPROG(), constraintsJCheckBoxPROG);

			java.awt.GridBagConstraints constraintsJCheckBoxUSER = new java.awt.GridBagConstraints();
			constraintsJCheckBoxUSER.gridx = 2; constraintsJCheckBoxUSER.gridy = 2;
			constraintsJCheckBoxUSER.ipady = -4;
			constraintsJCheckBoxUSER.insets = new java.awt.Insets(0, 3, 1, 0);
			getJPanelAddressUsage().add(getJCheckBoxUSER(), constraintsJCheckBoxUSER);

			java.awt.GridBagConstraints constraintsJCheckBoxLOAD = new java.awt.GridBagConstraints();
			constraintsJCheckBoxLOAD.gridx = 1; constraintsJCheckBoxLOAD.gridy = 5;
			constraintsJCheckBoxLOAD.ipadx = 2;
			constraintsJCheckBoxLOAD.ipady = -4;
			constraintsJCheckBoxLOAD.insets = new java.awt.Insets(0, 12, 8, 3);
			getJPanelAddressUsage().add(getJCheckBoxLOAD(), constraintsJCheckBoxLOAD);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelAddressUsage;
}

/**
 * Return the JPanelAddressUsageTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getJPanelAddressUsageTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjJPanelAddressUsageTitleBorder = null;
	try {
		/* Create part */
		ivjJPanelAddressUsageTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjJPanelAddressUsageTitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjJPanelAddressUsageTitleBorder.setTitle("Address Usage");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelAddressUsageTitleBorder;
}


/**
 * Return the JPanelRelayUsage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelRelayUsage() {
	if (ivjJPanelRelayUsage == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder2.setTitle("Load Usage");
			ivjJPanelRelayUsage = new javax.swing.JPanel();
			ivjJPanelRelayUsage.setName("JPanelRelayUsage");
			ivjJPanelRelayUsage.setBorder(ivjLocalBorder2);
			ivjJPanelRelayUsage.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxRelay1 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay1.gridx = 1; constraintsJCheckBoxRelay1.gridy = 1;
			constraintsJCheckBoxRelay1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRelay1.ipady = -4;
			constraintsJCheckBoxRelay1.insets = new java.awt.Insets(3, 11, 19, 8);
			getJPanelRelayUsage().add(getJCheckBoxRelay1(), constraintsJCheckBoxRelay1);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay2 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay2.gridx = 1; constraintsJCheckBoxRelay2.gridy = 1;
			constraintsJCheckBoxRelay2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRelay2.ipady = -4;
			constraintsJCheckBoxRelay2.insets = new java.awt.Insets(25, 11, 0, 8);
			getJPanelRelayUsage().add(getJCheckBoxRelay2(), constraintsJCheckBoxRelay2);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay3 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay3.gridx = 1; constraintsJCheckBoxRelay3.gridy = 2;
			constraintsJCheckBoxRelay3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRelay3.ipady = -4;
			constraintsJCheckBoxRelay3.insets = new java.awt.Insets(0, 11, 1, 8);
			getJPanelRelayUsage().add(getJCheckBoxRelay3(), constraintsJCheckBoxRelay3);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay4 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay4.gridx = 1; constraintsJCheckBoxRelay4.gridy = 3;
			constraintsJCheckBoxRelay4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRelay4.ipady = -4;
			constraintsJCheckBoxRelay4.insets = new java.awt.Insets(2, 11, 9, 8);
			getJPanelRelayUsage().add(getJCheckBoxRelay4(), constraintsJCheckBoxRelay4);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay5 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay5.gridx = 2; constraintsJCheckBoxRelay5.gridy = 1;
			constraintsJCheckBoxRelay5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRelay5.ipady = -4;
			constraintsJCheckBoxRelay5.insets = new java.awt.Insets(3, 9, 19, 15);
			getJPanelRelayUsage().add(getJCheckBoxRelay5(), constraintsJCheckBoxRelay5);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay6 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay6.gridx = 2; constraintsJCheckBoxRelay6.gridy = 1;
			constraintsJCheckBoxRelay6.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRelay6.ipady = -4;
			constraintsJCheckBoxRelay6.insets = new java.awt.Insets(25, 9, 0, 15);
			getJPanelRelayUsage().add(getJCheckBoxRelay6(), constraintsJCheckBoxRelay6);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay7 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay7.gridx = 2; constraintsJCheckBoxRelay7.gridy = 2;
			constraintsJCheckBoxRelay7.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRelay7.ipady = -4;
			constraintsJCheckBoxRelay7.insets = new java.awt.Insets(0, 9, 1, 15);
			getJPanelRelayUsage().add(getJCheckBoxRelay7(), constraintsJCheckBoxRelay7);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay8 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay8.gridx = 2; constraintsJCheckBoxRelay8.gridy = 3;
			constraintsJCheckBoxRelay8.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRelay8.ipady = -4;
			constraintsJCheckBoxRelay8.insets = new java.awt.Insets(2, 9, 9, 15);
			getJPanelRelayUsage().add(getJCheckBoxRelay8(), constraintsJCheckBoxRelay8);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelRelayUsage;
}

/**
 * Return the JPanelRelayUsageTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getJPanelRelayUsageTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjJPanelRelayUsageTitleBorder = null;
	try {
		/* Create part */
		ivjJPanelRelayUsageTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjJPanelRelayUsageTitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjJPanelRelayUsageTitleBorder.setTitle("Relay Usage");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelRelayUsageTitleBorder;
}


/**
 * Return the JTextFieldFeedAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFeedAddress() {
	if (ivjJTextFieldFeedAddress == null) {
		try {
			ivjJTextFieldFeedAddress = new javax.swing.JTextField();
			ivjJTextFieldFeedAddress.setName("JTextFieldFeedAddress");
			ivjJTextFieldFeedAddress.setFont(new java.awt.Font("dialog", 0, 10));
			// user code begin {1}

			ivjJTextFieldFeedAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 65534) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFeedAddress;
}


/**
 * Return the JTextFieldGeoAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldGeoAddress() {
	if (ivjJTextFieldGeoAddress == null) {
		try {
			ivjJTextFieldGeoAddress = new javax.swing.JTextField();
			ivjJTextFieldGeoAddress.setName("JTextFieldGeoAddress");
			ivjJTextFieldGeoAddress.setFont(new java.awt.Font("dialog", 0, 10));
			// user code begin {1}

			ivjJTextFieldGeoAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 65534) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldGeoAddress;
}


/**
 * Return the JTextFieldProgAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldProgAddress() {
	if (ivjJTextFieldProgAddress == null) {
		try {
			ivjJTextFieldProgAddress = new javax.swing.JTextField();
			ivjJTextFieldProgAddress.setName("JTextFieldProgAddress");
			ivjJTextFieldProgAddress.setFont(new java.awt.Font("dialog", 0, 10));
			// user code begin {1}

			ivjJTextFieldProgAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 254) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldProgAddress;
}


/**
 * Return the JTextFieldSPID511 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSerialAddress() {
	if (ivjJTextFieldSerialAddress == null) {
		try {
			ivjJTextFieldSerialAddress = new javax.swing.JTextField();
			ivjJTextFieldSerialAddress.setName("JTextFieldSerialAddress");
			ivjJTextFieldSerialAddress.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldSerialAddress.setEnabled(false);
			ivjJTextFieldSerialAddress.setEditable(true);
			// user code begin {1}

			ivjJTextFieldSerialAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 999999999) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSerialAddress;
}


/**
 * Return the JTextFieldSPIDAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSPIDAddress() {
	if (ivjJTextFieldSPIDAddress == null) {
		try {
			ivjJTextFieldSPIDAddress = new javax.swing.JTextField();
			ivjJTextFieldSPIDAddress.setName("JTextFieldSPIDAddress");
			ivjJTextFieldSPIDAddress.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldSPIDAddress.setText("1");
			// user code begin {1}

			ivjJTextFieldSPIDAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(1, 65534) );
			ivjJTextFieldSPIDAddress.setText("1");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSPIDAddress;
}


/**
 * Return the JTextFieldSplinter property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSplinter() {
	if (ivjJTextFieldSplinter == null) {
		try {
			ivjJTextFieldSplinter = new javax.swing.JTextField();
			ivjJTextFieldSplinter.setName("JTextFieldSplinter");
			ivjJTextFieldSplinter.setFont(new java.awt.Font("dialog", 0, 10));
			// user code begin {1}

			ivjJTextFieldSplinter.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 254) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSplinter;
}


/**
 * Return the JTextFieldSubAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSubAddress() {
	if (ivjJTextFieldSubAddress == null) {
		try {
			ivjJTextFieldSubAddress = new javax.swing.JTextField();
			ivjJTextFieldSubAddress.setName("JTextFieldSubAddress");
			ivjJTextFieldSubAddress.setFont(new java.awt.Font("dialog", 0, 10));
			// user code begin {1}

			ivjJTextFieldSubAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 65534) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSubAddress;
}


/**
 * Return the JTextFieldUserAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldUserAddress() {
	if (ivjJTextFieldUserAddress == null) {
		try {
			ivjJTextFieldUserAddress = new javax.swing.JTextField();
			ivjJTextFieldUserAddress.setName("JTextFieldUserAddress");
			ivjJTextFieldUserAddress.setFont(new java.awt.Font("dialog", 0, 10));
			// user code begin {1}

			ivjJTextFieldUserAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 65534) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldUserAddress;
}


/**
 * Return the JTextFieldZipAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldZipAddress() {
	if (ivjJTextFieldZipAddress == null) {
		try {
			ivjJTextFieldZipAddress = new javax.swing.JTextField();
			ivjJTextFieldZipAddress.setName("JTextFieldZipAddress");
			ivjJTextFieldZipAddress.setFont(new java.awt.Font("dialog", 0, 10));
			// user code begin {1}

			ivjJTextFieldZipAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 16777214) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldZipAddress;
}


/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMGroupExpressCom group = null;
	
	if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		group = (com.cannontech.database.data.device.lm.LMGroupExpressCom)
					com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
								com.cannontech.database.data.device.lm.LMGroupExpressCom.class,
								(com.cannontech.database.data.multi.MultiDBPersistent)o );
	}
	else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		group = (com.cannontech.database.data.device.lm.LMGroupExpressCom)
					((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	
	
	if( o instanceof com.cannontech.database.data.device.lm.LMGroupExpressCom || group != null )
	{
		if( group == null )
			group = (com.cannontech.database.data.device.lm.LMGroupExpressCom) o;

		group.setServiceProviderAddress( createAddress(
				getJComboBoxSPID(), 
				getJTextFieldSPIDAddress(), 
				IlmDefines.TYPE_SERVICE) );

		group.setFeederAddress( createAddress(
				getJComboBoxFEED(), 
				getJTextFieldFeedAddress(), 
				IlmDefines.TYPE_FEEDER) );
		
		group.setSubstationAddress( createAddress(
				getJComboBoxSUB(), 
				getJTextFieldSubAddress(), 
				IlmDefines.TYPE_SUBSTATION) );

		group.setGeoAddress( createAddress(
				getJComboBoxGEO(), 
				getJTextFieldGeoAddress(), 
				IlmDefines.TYPE_GEO) );

		group.setProgramAddress( createAddress(
				getJComboBoxPROG(), 
				getJTextFieldProgAddress(), 
				IlmDefines.TYPE_PROGRAM) );


		if( getJTextFieldSplinter().getText() != null && getJTextFieldSplinter().getText().length() > 0 )
			group.getLMGroupExpressComm().setSplinterAddress( new Integer(getJTextFieldSplinter().getText()) );
      else
         group.getLMGroupExpressComm().setSplinterAddress( IlmDefines.NONE_ADDRESS_ID );

		if( getJTextFieldUserAddress().getText() != null && getJTextFieldUserAddress().getText().length() > 0 )			
			group.getLMGroupExpressComm().setUdAddress( new Integer(getJTextFieldUserAddress().getText()) );
      else
         group.getLMGroupExpressComm().setUdAddress( IlmDefines.NONE_ADDRESS_ID );

		if( getJTextFieldZipAddress().getText() != null && getJTextFieldZipAddress().getText().length() > 0 )		
			group.getLMGroupExpressComm().setZipCodeAddress( new Integer(getJTextFieldZipAddress().getText()) );
      else
         group.getLMGroupExpressComm().setZipCodeAddress( IlmDefines.NONE_ADDRESS_ID );

		if( getJCheckBoxSerial().isSelected() 
			 && getJTextFieldSerialAddress().getText() != null 
			 && getJTextFieldSerialAddress().getText().length() > 0 )
		{
			group.getLMGroupExpressComm().setSerialNumber( getJTextFieldSerialAddress().getText() );
		}
      else
         group.getLMGroupExpressComm().setSerialNumber( IlmDefines.NONE_ADDRESS_ID.toString() );
         
		StringBuffer addressUsage = new StringBuffer();
		StringBuffer relayUsage = new StringBuffer();

		//get our address usage
      if( getJCheckBoxSPID().isSelected() )
         addressUsage.append( 'S' );
       
      if( getJCheckBoxGEO().isSelected() )
         addressUsage.append( 'G' );

      if( getJCheckBoxSUB().isSelected() )
         addressUsage.append( 'B' );

      if( getJCheckBoxFEED().isSelected() )
         addressUsage.append( 'F' );

      if( getJCheckBoxZIP().isSelected() )
         addressUsage.append( 'Z' );

      if( getJCheckBoxUSER().isSelected() )
         addressUsage.append( 'U' );

      if( getJCheckBoxPROG().isSelected() )
         addressUsage.append( 'P' );

      if( getJCheckBoxSPLINTER().isSelected() )
         addressUsage.append( 'R' );

      if( getJCheckBoxLOAD().isSelected() )
         addressUsage.append( 'L' );

		//get our relay usage
      if( getJCheckBoxRelay1().isSelected() )
         relayUsage.append( '1' );

      if( getJCheckBoxRelay2().isSelected() )
         relayUsage.append( '2' );

      if( getJCheckBoxRelay3().isSelected() )
         relayUsage.append( '3' );

      if( getJCheckBoxRelay4().isSelected() )
         relayUsage.append( '4' );

      if( getJCheckBoxRelay5().isSelected() )
         relayUsage.append( '5' );

      if( getJCheckBoxRelay6().isSelected() )
         relayUsage.append( '6' );

      if( getJCheckBoxRelay7().isSelected() )
         relayUsage.append( '7' );

      if( getJCheckBoxRelay8().isSelected() )
         relayUsage.append( '8' );


		group.getLMGroupExpressComm().setAddressUsage( addressUsage.toString() );
		group.getLMGroupExpressComm().setRelayUsage( relayUsage.toString() );
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
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Insert the method's description here.
 * Creation date: (6/5/2002 11:40:07 AM)
 */
private void initAddressJComboBoxes() 
{
	//init SPIDS
	com.cannontech.database.db.device.lm.LMGroupExpressComAddress[] addresses = 
		com.cannontech.database.db.device.lm.LMGroupExpressComAddress.getAllExpressCommAddressWithNames();

	for( int i = 0; i < addresses.length; i++ )
		if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_SERVICE) )
			getJComboBoxSPID().addItem( addresses[i] );
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_GEO) )
			getJComboBoxGEO().addItem( addresses[i] );
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_SUBSTATION) )
			getJComboBoxSUB().addItem( addresses[i] );
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_FEEDER) )
			getJComboBoxFEED().addItem( addresses[i] );
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_PROGRAM) )
			getJComboBoxPROG().addItem( addresses[i] );
		else
		{
			com.cannontech.clientutils.CTILogger.info("********************************");

			com.cannontech.clientutils.CTILogger.info("*** Found an ExpressCommAddress that is not recognized '" 
						+ addresses[i].getAddressType() + "' in " + this.getClass().getName() );
			
			com.cannontech.clientutils.CTILogger.info("********************************");
		}

}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	// user code end
	getJComboBoxSPID().addActionListener(this);
	getJComboBoxGEO().addActionListener(this);
	getJComboBoxSUB().addActionListener(this);
	getJComboBoxFEED().addActionListener(this);
	getJComboBoxPROG().addActionListener(this);
	getJTextFieldZipAddress().addCaretListener(this);
	getJTextFieldSPIDAddress().addCaretListener(this);
	getJTextFieldGeoAddress().addCaretListener(this);
	getJTextFieldFeedAddress().addCaretListener(this);
	getJTextFieldProgAddress().addCaretListener(this);
	getJTextFieldSerialAddress().addCaretListener(this);
	getJCheckBoxSerial().addActionListener(this);
	getJCheckBoxRelay5().addActionListener(this);
	getJCheckBoxRelay6().addActionListener(this);
	getJCheckBoxRelay7().addActionListener(this);
	getJCheckBoxRelay8().addActionListener(this);
	getJCheckBoxRelay4().addActionListener(this);
	getJCheckBoxRelay3().addActionListener(this);
	getJCheckBoxRelay2().addActionListener(this);
	getJCheckBoxRelay1().addActionListener(this);
	getJCheckBoxSPLINTER().addActionListener(this);
	getJCheckBoxPROG().addActionListener(this);
	getJCheckBoxUSER().addActionListener(this);
	getJCheckBoxZIP().addActionListener(this);
	getJCheckBoxFEED().addActionListener(this);
	getJCheckBoxSUB().addActionListener(this);
	getJCheckBoxGEO().addActionListener(this);
	getJCheckBoxLOAD().addActionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMGroupExpressComEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(357, 363);

		java.awt.GridBagConstraints constraintsJPanelAddress = new java.awt.GridBagConstraints();
		constraintsJPanelAddress.gridx = 1; constraintsJPanelAddress.gridy = 1;
		constraintsJPanelAddress.gridwidth = 2;
		constraintsJPanelAddress.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelAddress.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelAddress.weightx = 1.0;
		constraintsJPanelAddress.weighty = 1.0;
		constraintsJPanelAddress.ipadx = -10;
		constraintsJPanelAddress.ipady = -6;
		constraintsJPanelAddress.insets = new java.awt.Insets(4, 5, 2, 6);
		add(getJPanelAddress(), constraintsJPanelAddress);

		java.awt.GridBagConstraints constraintsJPanelAddressUsage = new java.awt.GridBagConstraints();
		constraintsJPanelAddressUsage.gridx = 1; constraintsJPanelAddressUsage.gridy = 2;
		constraintsJPanelAddressUsage.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelAddressUsage.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelAddressUsage.weightx = 1.0;
		constraintsJPanelAddressUsage.weighty = 1.0;
		constraintsJPanelAddressUsage.ipadx = -11;
		constraintsJPanelAddressUsage.ipady = -8;
		constraintsJPanelAddressUsage.insets = new java.awt.Insets(2, 5, 4, 2);
		add(getJPanelAddressUsage(), constraintsJPanelAddressUsage);

		java.awt.GridBagConstraints constraintsJPanelRelayUsage = new java.awt.GridBagConstraints();
		constraintsJPanelRelayUsage.gridx = 2; constraintsJPanelRelayUsage.gridy = 2;
		constraintsJPanelRelayUsage.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelRelayUsage.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelRelayUsage.weightx = 1.0;
		constraintsJPanelRelayUsage.weighty = 1.0;
		constraintsJPanelRelayUsage.ipadx = -9;
		constraintsJPanelRelayUsage.ipady = 3;
		constraintsJPanelRelayUsage.insets = new java.awt.Insets(2, 2, 4, 6);
		add(getJPanelRelayUsage(), constraintsJPanelRelayUsage);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initAddressJComboBoxes();
	
	// user code end
}

/**
 * This method must be implemented if a notion of data validity needs to be supported.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldSPIDAddress().getText() == null 
		 || getJTextFieldSPIDAddress().getText().length() <= 0
		 || Integer.parseInt(getJTextFieldSPIDAddress().getText()) <= 0 )
	{
		setErrorString("The SPID address must be 1 or greater");
		return false;
	}

	if( getJCheckBoxLOAD().isSelected()
		 && !isRelaySelected() )
	{
		setErrorString("Since the LOAD usage is checked, you must select at least 1 load group");
		return false;
	}

	return true;
}

public boolean isRelaySelected()
{
	for( int i = 0; i < getJPanelRelayUsage().getComponentCount(); i++ )
		if( getJPanelRelayUsage().getComponent(i) instanceof JCheckBox )
		{
			JCheckBox bx = (JCheckBox)getJPanelRelayUsage().getComponent(i);
			
			if( bx.isSelected() )
				return true;
		}
		
	return false;
}

/**
 * Comment
 */
public void jCheckBoxSerial_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	for( int i = 0; i < getJPanelAddress().getComponentCount(); i++ )
	{
		java.awt.Component c = getJPanelAddress().getComponent(i);

		if( c.equals( getJTextFieldSerialAddress() ) )
		{
			c.setEnabled( getJCheckBoxSerial().isSelected() );
		}
		else if ( c.equals( getJCheckBoxSerial() ) )
		{
			//do nothing
		}
		else
			c.setEnabled( !(getJCheckBoxSerial().isSelected()) );
	}

	fireInputUpdate();

	return;
}


/**
 * Comment
 */
public void jComboBoxFEED_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( getJComboBoxFEED().getSelectedItem() instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
	{
		com.cannontech.database.db.device.lm.LMGroupExpressComAddress selected =
				(com.cannontech.database.db.device.lm.LMGroupExpressComAddress)getJComboBoxFEED().getSelectedItem();

		getJTextFieldFeedAddress().setText( selected.getAddress().toString() );

		getJComboBoxFEED().setEditable(false);
	}
	else
	{
		getJComboBoxFEED().setEditable(true);
		getJComboBoxFEED().getEditor().selectAll();
	}

	fireInputUpdate();

	return;
}


/**
 * Comment
 */
public void jComboBoxGEO_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( getJComboBoxGEO().getSelectedItem() instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
	{
		com.cannontech.database.db.device.lm.LMGroupExpressComAddress selected =
				(com.cannontech.database.db.device.lm.LMGroupExpressComAddress)getJComboBoxGEO().getSelectedItem();

		getJTextFieldGeoAddress().setText( selected.getAddress().toString() );
		
		getJComboBoxGEO().setEditable(false);
	}
	else
	{
		getJComboBoxGEO().setEditable(true);
		getJComboBoxGEO().getEditor().selectAll();
	}

	fireInputUpdate();

	return;
}


/**
 * Comment
 */
public void jComboBoxPROG_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( getJComboBoxPROG().getSelectedItem() instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
	{
		com.cannontech.database.db.device.lm.LMGroupExpressComAddress selected =
				(com.cannontech.database.db.device.lm.LMGroupExpressComAddress)getJComboBoxPROG().getSelectedItem();

		getJTextFieldProgAddress().setText( selected.getAddress().toString() );

		getJComboBoxPROG().setEditable(false);
	}
	else
	{
		getJComboBoxPROG().setEditable(true);
		getJComboBoxPROG().getEditor().selectAll();
	}

	fireInputUpdate();

	return;
}


/**
 * Comment
 */
public void jComboBoxSPID_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( getJComboBoxSPID().getSelectedItem() instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
	{
		com.cannontech.database.db.device.lm.LMGroupExpressComAddress selected =
				(com.cannontech.database.db.device.lm.LMGroupExpressComAddress)getJComboBoxSPID().getSelectedItem();

		getJTextFieldSPIDAddress().setText( selected.getAddress().toString() );
		
		getJComboBoxSPID().setEditable(false);
	}
	else
	{
		getJComboBoxSPID().setEditable(true);
		getJComboBoxSPID().getEditor().selectAll();
	}

	fireInputUpdate();

	return;
}


/**
 * Comment
 */
public void jComboBoxSUB_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( getJComboBoxSUB().getSelectedItem() instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
	{
		com.cannontech.database.db.device.lm.LMGroupExpressComAddress selected =
				(com.cannontech.database.db.device.lm.LMGroupExpressComAddress)getJComboBoxSUB().getSelectedItem();

		getJTextFieldSubAddress().setText( selected.getAddress().toString() );

		getJComboBoxSUB().setEditable(false);
	}
	else
	{
		getJComboBoxSUB().setEditable(true);
		getJComboBoxSUB().getEditor().selectAll();
	}

	fireInputUpdate();

	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		LMGroupExpressComEditorPanel aGroupTypePanel;
		aGroupTypePanel = new LMGroupExpressComEditorPanel();
		frame.add("Center", aGroupTypePanel);
		frame.setSize(aGroupTypePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main()");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}


/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o instanceof com.cannontech.database.data.device.lm.LMGroupExpressCom )
	{
		com.cannontech.database.data.device.lm.LMGroupExpressCom group = (com.cannontech.database.data.device.lm.LMGroupExpressCom) o;

		getJComboBoxSPID().setSelectedItem( group.getServiceProviderAddress() );
		getJTextFieldSPIDAddress().setText( group.getServiceProviderAddress().getAddress().toString() );

		getJComboBoxGEO().setSelectedItem( group.getGeoAddress() );
		getJTextFieldGeoAddress().setText( group.getGeoAddress().getAddress().toString() );

		getJComboBoxFEED().setSelectedItem( group.getFeederAddress() );
		getJTextFieldFeedAddress().setText( group.getFeederAddress().getAddress().toString() );

		getJComboBoxSUB().setSelectedItem( group.getSubstationAddress() );
		getJTextFieldSubAddress().setText( group.getSubstationAddress().getAddress().toString() );

		getJComboBoxPROG().setSelectedItem( group.getProgramAddress() );
		getJTextFieldProgAddress().setText( group.getProgramAddress().getAddress().toString() );


		getJTextFieldZipAddress().setText( group.getLMGroupExpressComm().getZipCodeAddress().toString() );

		getJTextFieldUserAddress().setText( group.getLMGroupExpressComm().getUdAddress().toString() );

		getJTextFieldSplinter().setText( group.getLMGroupExpressComm().getSplinterAddress().toString() );

		
		Integer serial = new Integer(group.getLMGroupExpressComm().getSerialNumber());
		if( serial.intValue() > IlmDefines.NONE_ADDRESS_ID.intValue() )
		{
			getJCheckBoxSerial().doClick();
			getJTextFieldSerialAddress().setText( serial.toString() );
		}

		//set our address usage
		String addUsage = group.getLMGroupExpressComm().getAddressUsage();
		getJCheckBoxSPID().setSelected( addUsage.indexOf("S") >= 0 );
		getJCheckBoxGEO().setSelected( addUsage.indexOf("G") >= 0 );
		getJCheckBoxSUB().setSelected( addUsage.indexOf("B") >= 0 );
		getJCheckBoxFEED().setSelected( addUsage.indexOf("F") >= 0 );
		getJCheckBoxZIP().setSelected( addUsage.indexOf("Z") >= 0 );
		getJCheckBoxUSER().setSelected( addUsage.indexOf("U") >= 0 );
		getJCheckBoxPROG().setSelected( addUsage.indexOf("P") >= 0 );
		getJCheckBoxSPLINTER().setSelected( addUsage.indexOf("R") >= 0 );
		getJCheckBoxLOAD().setSelected( addUsage.indexOf("L") >= 0 );


		//set our relay usage
		String relayUsage = group.getLMGroupExpressComm().getRelayUsage();
		getJCheckBoxRelay1().setSelected( relayUsage.indexOf("1") >= 0 );
		getJCheckBoxRelay2().setSelected( relayUsage.indexOf("2") >= 0 );
		getJCheckBoxRelay3().setSelected( relayUsage.indexOf("3") >= 0 );
		getJCheckBoxRelay4().setSelected( relayUsage.indexOf("4") >= 0 );
		getJCheckBoxRelay5().setSelected( relayUsage.indexOf("5") >= 0 );
		getJCheckBoxRelay6().setSelected( relayUsage.indexOf("6") >= 0 );
		getJCheckBoxRelay7().setSelected( relayUsage.indexOf("7") >= 0 );
		getJCheckBoxRelay8().setSelected( relayUsage.indexOf("8") >= 0 );
	}


}
}