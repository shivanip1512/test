package com.cannontech.dbeditor.wizard.device.lmgroup;

import javax.swing.JCheckBox;
import com.cannontech.database.db.device.lm.IlmDefines;
import javax.swing.JOptionPane;
import java.awt.Color;
import javax.swing.JDialog;

/**
 * This type was created in VisualAge.
 */

public class LMGroupExpressComEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	public static final String STRING_NEW = "(new)";
	private javax.swing.JLabel ivjJLabelFeedAddress = null;
	private javax.swing.JLabel ivjJLabelGEOAddress = null;
	private javax.swing.JLabel ivjJLabelPROGAddress = null;
	private javax.swing.JLabel ivjJLabelSplinter = null;
	private javax.swing.JLabel ivjJLabelSubAddress = null;
	private javax.swing.JLabel ivjJLabelUserAddress = null;
	private javax.swing.JLabel ivjJLabelZipAddress = null;
	private javax.swing.JTextField ivjJTextFieldGeoAddress = null;
	private javax.swing.JTextField ivjJTextFieldProgAddress = null;
	private javax.swing.JTextField ivjJTextFieldSPIDAddress = null;
	private javax.swing.JTextField ivjJTextFieldSubAddress = null;
	private javax.swing.JTextField ivjJTextFieldUserAddress = null;
	private javax.swing.JTextField ivjJTextFieldZipAddress = null;
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
	private javax.swing.JCheckBox ivjJCheckBoxSPLINTER = null;
	private javax.swing.JCheckBox ivjJCheckBoxSUB = null;
	private javax.swing.JCheckBox ivjJCheckBoxUSER = null;
	private javax.swing.JCheckBox ivjJCheckBoxZIP = null;
	private javax.swing.JPanel ivjJPanelRelayUsage = null;
	private javax.swing.JCheckBox ivjJCheckBoxSerial = null;
	private javax.swing.JTextField ivjJTextFieldSerialAddress = null;
	private javax.swing.JCheckBox ivjJCheckBoxLOAD = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjSPIDLabel = null;
	private javax.swing.JLabel ivjGEOLabel = null;
	private javax.swing.JComboBox ivjJComboBoxSPLINTER = null;
	private javax.swing.JComboBox ivjJComboBoxUSER = null;
	private javax.swing.JComboBox ivjJComboBoxZIP = null;
	private javax.swing.JLabel ivjPROGLabel = null;
	private javax.swing.JLabel ivjSPLINTERLabel = null;
	private javax.swing.JLabel ivjSUBLabel = null;
	private javax.swing.JLabel ivjUSERLabel = null;
	private javax.swing.JLabel ivjZIPLabel = null;
	private javax.swing.JButton ivjJButtonSPIDModify = null;
	private javax.swing.JButton ivjJButtonGEOModify = null;
	private javax.swing.JButton ivjJButtonPROGModify = null;
	private javax.swing.JButton ivjJButtonSplintModify = null;
	private javax.swing.JButton ivjJButtonSUBModify = null;
	private javax.swing.JButton ivjJButtonUSERModify = null;
	private javax.swing.JButton ivjJButtonZIPModify = null;
	private javax.swing.JPanel ivjJPanelGeoAddress = null;
	private javax.swing.JPanel ivjJPanelLoadAddress = null;
	private javax.swing.JLabel ivjJLabelSerialAddress = null;
	private javax.swing.JPanel ivjJPanelGeoAddressUsage = null;
	private javax.swing.JPanel ivjJPanelLoadAddressUsage = null;
	private javax.swing.JButton ivjJButtonFeedAddress = null;
	private javax.swing.JLabel ivjJLabelSPIDAddress = null;
	private javax.swing.JTextField ivjJTextFieldSplinterAddress = null;
	
	private JDialog feederBitToggleDialog = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJComboBoxSPID()) 
				connEtoC2(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJComboBoxGEO()) 
				connEtoC3(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJComboBoxSUB()) 
				connEtoC4(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJComboBoxPROG()) 
				connEtoC6(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxSerial()) 
				connEtoC12(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxRelay5()) 
				connEtoC13(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxRelay6()) 
				connEtoC14(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxRelay7()) 
				connEtoC15(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxRelay8()) 
				connEtoC16(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxRelay4()) 
				connEtoC17(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxRelay3()) 
				connEtoC18(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxRelay2()) 
				connEtoC19(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxRelay1()) 
				connEtoC20(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxSPLINTER()) 
				connEtoC21(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxPROG()) 
				connEtoC22(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxUSER()) 
				connEtoC23(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxZIP()) 
				connEtoC24(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxFEED()) 
				connEtoC25(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxSUB()) 
				connEtoC26(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxGEO()) 
				connEtoC27(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJCheckBoxLOAD()) 
				connEtoC28(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJButtonFeedAddress()) 
				connEtoC1(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJTextFieldSerialAddress()) 
				connEtoC11(e);
		};
	};
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
	if (e.getSource() == getJButtonFeedAddress()) 
		connEtoC8(e);
	if (e.getSource() == getJTextFieldProgAddress()) 
		connEtoC9(e);
	if (e.getSource() == getJTextFieldSerialAddress()) 
		connEtoC11(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldFeedAddress.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jTextFieldFeedAddress_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonFeedAddress_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
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
		// user code end
		this.jCheckBoxSPLINTER_ActionPerformed(arg1);
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
		this.jCheckBoxPROG_ActionPerformed(arg1);
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
		this.jCheckBoxUSER_ActionPerformed(arg1);
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
		this.jCheckBoxZIP_ActionPerformed(arg1);
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
		this.jCheckBoxFEED_ActionPerformed(arg1);
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
		this.jCheckBoxSUB_ActionPerformed(arg1);
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
		this.jCheckBoxGEO_ActionPerformed(arg1);
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
		this.jCheckBoxLOAD_ActionPerformed(arg1);
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
	D0CB838494G88G88G03038AB1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FDCD4D55ABFEDECD9393BEA34D9D1D1D9D1D9319B95ED6C2EDB56DA31E5C5E5E5ADEDD654D24B96EFD1CB6B15BF0A020AC92BED24242828000028E414A020202312222222B2B003CC8E8C8E0302227D1EF34EFD1E7BE76EG0369EB6F67674707BB677B1C67BC4FF37E5FF36FBDC7CA1F9F10B4A9BEDD12A6A5C9723FB513A4A9F253A4E9443D5563914B3FE2E80A247A77A550ACE9C7C5A2DCCB01
	D77A6A3014B4A9BDA1994235ACFC673014B970FB36B425A962D3C850031E9060ADBDB074573D6BF9F7816A194243B90D29F01D81D487D88F509820B42922F12E0C87C0F8067C1B5124F8C9723D124519EF1F2B42D32F94716A60EA87723D0A45B95818A17343G1BGD48AF86DD5A22E9D2ED339F5BFDFDBAC472B9F9AA845BC3AB5FEBA5325CA9FA9CC160D162F195233BCA39224CBEACB61DAD2F3785037CD0E563CA6F3DB1B234DE5EDEA4933DAECAE07B34FE2BDEAEB326659DB572EB5ED5968F4F4366F32BA
	BB4C903912C728B137D96D8D7E4C6ECAFBB2CFD31232A19C45315F5BD9DE0D005F21C0617EE87B1353CBE45B7C8E87CA91B714F934BD7FEAE1F36D550A6DD2414B8909573074877132E0F2FE408F8392FE35EE2D71601725D1766C186D3859E9D19CBC441CA3AFAB3B5A1D560E0EF283BF47B6221FD557201FB9FB189DBE909E8E94C478FB9C9F8361882058EB507FEF16207FF5A76E15E29B0F444F7460FF5A35426F62EB957F47F5FD1B74992F0B2732D8FDF281EFB850C82031C051C0E981C3D3BE67ED602B05
	4B60DA566882E7DA9DEDE56D6D6B6D9667960745EA075FEB5782C74636DB1B9CCE0BA409FA3E491EE3E08FD2F600360EB3923E6D1630DE0E5CFE0764F29D0ACFB270AD78191A7892B90D1AG4537746BD83E5D5D46722D9DF0A63785C86093B8DEF71D020BFA70A02FE7AC9C873C1691184FF5B35096D36275D2DAC1C77C9483DB06BF50929FFE1330A16AA645961147F50BD99E4F3DF9E8CA9110894882648BB462E64C6373972E6CAB0F2B217EB95B4C769D1D0D6DE627EBFD274DEE31BA653C7EE5E3EFF9EDC4
	C24E5FEBB911AF01AB55E5B47EAE27E13E0C8F5C9DBF6AE6119F63EED672C568FE46236E581B5D6B468CAA43G5EA6D283C599A690FEAB47B3D438A85BC20E33F08E7006G0978EB76B1BCC60E3F0252594D717ADB94DC2473A1CF0705C7832F743621A922FC5FDF427A5B5011C3D34681C583A55FBEB4A59B2882286EF6AC6347972E68E7BB2AD1152F103DB06BBFF035F6391C666A368E17392D493A4B6C34199B6D568EC90EB3A40B65EB23C66EB25E3E1BC047C6EB1B55E9F6599CED103A39558A1529C34439
	A6EB960B6334F2EE2D2D558A8ADA5BA5CC77262C59F0351BBBDCBB5BADE617D509A178F7E431BAF1544CBAF6C800550E5BD9FE19F9FF5E853F7DEE473CDA3814E1D190CE3C8373E56922CF603A99AA37553EF6ED052D0919E1F6F6E39D3D3FEDB8EBBF90BFGA87B4E21A941C0C5C1F085AA7A4D501490202E5FC21AF78DCD9985549EBCB4A563EE98D7013A6E817CDE0883D49A8298D04DFDD0D6C08D3F9B1A928954FEBF742DC03E21C3D3D2FF8F76824D7D8368834A7A635014C0207CD19097E8781F0626D4BD
	G7101C29E44FE621DBAEC8F9553EF17F21EB9E2589E9839C60B3ABDF8343EBD3C457D8C86BC9CA886A87DA1708F289648856487948C948E9483147EB07001EA01DCC0FEC041C061C0B1C069FF86BED0AD108B488FA898A89CA886A8FD8C7001EA01DCC0FEC041C061C0B1C0690FG9F2876911C3BBCE2EF08577A434A047747F70AFCD673C26EF5EF472C1CD8FE6AF1D6F6AC3F75B8ABCF7C4D4A07277B9BF11D2B0B4F4A0E152F9AE3F581FF33BA412E2CCCD9799975B12CAE98612C7CFB6B97D9FDE4750255A7
	A3BE2B2B5E74333D91EB83C53F797152E37538B7BE2B7378BBD2158F2CED3136026139F2DDA8155B986F776FD378E36E566AE9B048AB568EC5DD33346A6BDA12240CC1DF6A64969A584D7A2DB2837CD320EF8C70B92A5F2B646BAC797A35AE6E9A20A5BA6CB3556F4F656BBA204D9E72354083DE5EC7F9311B0CEAC90CC5C67C69BFD23DE0444AE2660F185E16BE789BD53FABD53F6783FD258AA74857C5C0CB655F4BD57CAFF469669A68DAA91759ACE968B2B6363B201F9B829474880ECD16C519929F33571BBB
	2CB8CAEFF342GEB35A8A31508EBDE4C7249FA545A662A3036DB5BAC7023C39EA3DB9633BAAB0F5F4D8E894756B60E3379DCB98C309D043B964F92B824211E8DA03F07734DAE1753565869326E303ADC3636C30A3CAF3725C3B3D56C10FD3DF4B93F27G9FA30022495769CBD8FB41F15864F09C6EEC2FE5B38CF9BC0F6277AD090F42587AA836511B7670FBDB8A87713042AF5ED357CBD9B339532D1B33246FFA5511F09A69ABBA3E59D7AF2715A4E32C8D32FEE40EDC07BE176B9C2B272CEFDAA8574D2F657A39
	DC2E032C4D9648F50F75B12C9FA9A3BD7B391E964817A1C0A1C091C0490F417C83288E4887A890E8ACD0ACD0E6980CBBC06DC04301C681C582257DE5E8CAB1D08350A020A020F1FF41F2193827FD1212BF689376AD7ABC680F1FB37A61678CC95B0F9A754D7F17C9647D013D1E72FE2A018CAB8F561F4F1444D8B45B20BCE698144792DDF9687BC4CFE3477FD592FD1C146D390F1E12ED5CC7CF4FB66E23E7E7775E87FF1EED54878B5E02ECD66E0EE3ED2ABEFDC9B63B8FEAF23435D97940C2A212D813FA271679
	E1EB3339F65BAC072C2E9D3663D6F068F156B7F771FBAD89FE27BF0E77A4479662BDC95671DB24D187CD066BA843EFBC9CEFFFDC7443BE0FAB77A442266C25EC2C305A2D2D10D1650E4EB617407378FD1C0CF7A4F0EC687C9EA61B3DC318DFE63170953096EB5361750E2E9C1B4B5A1A477E6CF041CDE4F90B396D1055127D3D7928B94FFC4C15474BA42F9A78156C57524C5C5CEFB3BDAF9966B45B1C566A3676CE7926B4FB692E2585420E2AF547399D3C344E3E950522DD4F163EACB9496D28309D35F57C2FE4
	48CA89ED19FA4245ED59EE351B3B8B4E2289C5C2FF77E4157E423328FF1550FFE931CA7F4A3328FF3550FFE1C327223F68EC3609E22A839B967F165B3089556F30BAEDE63BDCADF34C3C556609CB0D55596CF0363A9BD346597DB367FBC3C5874BF49ACC1ED5FC8D9E2CC17BD79F936DE9D3AD74519BECD63BC56BC0934C16DDF97C2F6CBFCB38AB2F6398F44BF25A650CBB604CD47232EE4A9D5A3C2CEE33C0B337F41A6D271911068F8E142CD474F81B0F79E87B34346BE55B9D2D0D8ED6BCB55595E748EA1586
	EF6DADC0FB7FB5A9C0EB6F464AEDE756DCD0602D35054256AB2DBADBF76CDCFF0633F667FAEFEDDDA9EC2DDE282B87B55B37EDBC3346B28D5EDA3B8A6B41B8174F19EA7F46E69BF52779421617D65CEC54171E5936AFF4F85D0E8A647C3B6358892D4DFCB0DAF5C6ECB64A3AC2590E258B0D6CD8FD566CD8A95BF1B84D480EB5E74D0EA29CCB669859D1FC566CD8A55B71CA2B119DC5E74D0E55329DFF150C6CD8F9566CD8A35B511CECE4C761D93323D836A3A655480E0233E5C761D7329DCDCDA7F5FDDE0D29FA
	EBED657633E6C9BE4D3FBEBE552D35650C0DDDC6F694109DB525BABBF66EB80B79D1C8F64CBF522535E3EFF54DD9B3C31ECFBC1F294B0B8D1515E7E6DEE9E4C4916545DF53F4F9F12666E0C6E648771B2D69BA9B4E541C554886791E7332EEDDF91836151D3D72D8C365E1F35CF6967B0D6F75DA3C1E49674BF6A7E50DB8CBFD4C77EA8DFAFB8B7AB4D8DE77DA18F843D9AA6F6F559A3C4EDF9CDFB7B75CF0AE64EF5F59AB5BAB155C253B7F58E035DAFE1C65938FF69BE9727AEE8F675DC7CF86897B57F73ADC0E
	361F087162196543B9EC9DBA607121A941C0E10F63B34BDB17B2BEBE33149F4C16BB6CF631868D3C0AA7587AF829A3DD3566BD61F1715EEDBE70847FCF067C4144DF5FEE448FA07E03067CE062D79CB1620FA17EC3067C71445F60B462C7937F4F067C4431482F6AB0624FA57EA3067C9C62EFF29971CB097F18A13F167826CEA3FE8B717FE2486FA17E5623C67CA144FF4210BF0278B5470C78A144FF52109FC67C6DDDC67C8862BFED480FA1FEED3791BF79C964BFE3484FA07E2E63067517784F9972AB08DFF7
	4208BF035A07EF9F7CA0C3FE9D25DF50E34437937F3706FC9F622FBCE94477A3FE30A1BF10782BCE75EE3F59109FCA72FF6BC3FE468FC67C31A4EF19ED441FC07C578C793144EFCE7724DF74F317AFCB03EB0775C827352D494ADE4A58EC6D4677B717FD22636DB25BBB1577C2EFDA461EA15BD56FF3887C0A65464F762EDD4E5E61B8CC695F30FC32445E6F94E90AE711C1E616E63B5356EAF6F63397D4646F93921AB45FA7BC85FDA478E102EB8310980746163377E5FB9BBB0A1FE6E90D58075FB1F4016C9020
	A0B93D682764F75AF93C16FA0C678BE17F2715F821CF2B63E56C47F8096150364215F8A5616AF8E3A91EAF607E2AF8211AF83E87A83DE7A0BD2012E7C43C2EE75471B69E44F843818F829AAB47CBFBD69DAF25816355836EFAD60927CD6F0F944F8F620C74982F43CC5F01C01C899E63AD216FC5921F83BF1E73946FC50AD782F1EABD467BD59346F3C11C419E6315DBB05EC808B35AE33C8FA85E84089367B15EEDD60C17B1EEE8CA51B8A5DE6DB8F53C034D98AFG7090D53CF11AF8338FE13C4C67072694BF2F
	442BFBDE9D6F4F94AFG7090D53CF11AF8A7DAA83D97A03D97D4693D200E3742C669819E220AB7CE936FD50A1779A2247722AA3D975571AE7F1E5283BC84E85C0BA25E5C716AF87B8EE33C86403B00060F17754A7132DF6265CB5F692CA06CA60E7DE30AA4DD1D085F88ECC9D3FFDFE46930983EFF40487E127C7E41CB7AEFA7A66731F483G8FFA8967FCDF64285FD3AB6B68F0B459782B6BF23F961623FEB7C1E0CF66CC03AB5C27F6382A5BAC56AE6C9B1F4BE13632F75BE5D86E5FEEDB4E5E8F553F2BAF3AA8
	FC8D307C981F2656BA4A9D96EB95F42F65746A46AE1B75984BC77E8D4FB18766576817705D0C139B545FAF054E5C931F62A1EF6264BC49F8D9499B7F2EEF13C45A3114F6BD7085565D0E189FE19F9196C65809A308A57E9531B842CA88BB69C44CC5583F899BC9588F9D08CDA0CCF2A116910158F9045592965F0958E0424EA7ECB4E109C7910BA36C6704953D025864E30859893B10309104A5F7A1B61E300B88CB7F9BE2533A91B391F689E13E04259EC7AC1430DF9096C3580C9308653C0A589B04D988EB69C1
	4C1F30B7898BA76C7B130825CEC06C6F04D590B67C94E2BD047DF7B6E24104DDC5D894E1996908E53D065F49DD31163DE3E2741DDC93EFB8F907BAEDF91DAE9B7B30436CB273C5A2BE444BED23350925495AC47DEB784D5BED87593C448F42A301C683096FEF7E423F4BE1FA4783167EBA4E1DFED82E1EBB19GF7818DFF9D679BC575EC1D547BDBDC6C8BF6FC495A6A69BC4B96ED7E7A5925320DE1F7DCAF157C3056701B3DC1B3AB62C73EAE5AFA786BCA1BAF7EC5EE1289C8644550FA4C0B4CB7203FFF837342
	E91897C35E4072AA36FC2D76457B875D7237D0157BD17F88C8FBBC698FB2541F9E097AEF695468777E61B5FE0BC57A33A16D06C8543F7708DE7FF06005127E3B67EA4AF2G6BC1C27F6D87D07FD8C8BB79CD3A4F4DD36BAFFDD3785F72A656457403EC4C7472C935DC8FC7AFE573FC7735D67CBE2D7DA05A54837A0249263F552BED0A82BC7DAD18AF3C05B6BDF770745A475D5FFD4B7374F41ED2238FC58D6803896C73FD8BFDD831C76FC3A870A25E423EA11264E2G4B79BB4AFDE6A8E779BB4A791BD12E8B30
	A0127B54D0AE1264EA9AD1AE8E3022A8144BB41433C721DCE2934A79G96C2F28906F251A497C2F209EF43FD465BA8F74FDEA3B9575BA8E737205CA040C6115CB77B0C64E2C9AE530AF2296F004CBBA8B7452DAEB1391EF7D0AE1C6406GB60664DE6C524BC97F981A12758FEC170D2BCAB5F5E8E0EB7772B75D4DE8C3B1245F7E8FEA8796A39B824906CFD71728ED98F05FD0F2887507C25AB1247F6ABA23F618930D7AB72D5268E7CF928724BF1A7417C25ADD5128BF2751C8FF90691FFA64EB357E3D55B5836B
	1BC734207E5110F69C69AFB06C070A7E097AEF33E87CE7CF8F8764FF1D8D75D7C15ABE7FC47D0F1FB0529FC27ACBDAB57E8FF8ECCA7D9E7507C15A096F227EF5F9C67ACB5EC57D73D6EA748FF8EC9EC57AEBA06DC1243FF63FD17D9F75AE0E83CB8E1F5EB87052371A729B4033A1E13F6FE134BF9CECCBFD0F4600C2357D956F0931557E9E5A7F7753347F75D5954C7E9F6131053C0FC1217A7EF7D4C41D477BDF1F7744BC4F6FBD7D7D6FDBAB587CB88870516F617D6F76DC765D815E7FEE07BBCEF35BA13BD542
	FB5A14BC169F072D5DF2F8E69E534B576FA47C862547614DF77F635E436F37179430B20802F0F24C5014CC20E2203A98346D57F9FD6EE3D059554BBE869FFBE5D7D78CFE4FF42A5D9B3941A4B751AB39G1273B9620DDCB049CDF2FAA3B70664FE66155CF812CB6870C6AE1A64FC3D12CB7C9F14CBF2F9A3B71764AE70CAAE076426F4FAA3D7CAF2033C122BA53929C73D11EBA1390B3D126BA13914E35E488DA13941DE490DA03969DD5E4805105CAF3D128BA339195D5E48C5105C902F64E2C8EE56F1EF64125F
	C739E1DE49E5105C6C935E486513DC14D7F295A4575A630DDC9D493D63151C1D649CA73D1173A139E82F647CC86E6AD35E4885125C7C595E480512DC00D7F2E3C96E4BF4EF64A6101C338965E25F97F301548F14EF24B361F711AA4CC86CD9B31F5F7FD6FDA0762CC9A1BC1763ADAADC4C31FE43578AD8F8907022BEC07DCF6F535BDD932B55E75705055CABEEF273F461A1BAB9B167BAE4E773863EFE28DA82D93B163EC9101FAB8656291FAB86460AEF5043609A910B6B1377ACCF166D090D45794A49C276CCA3
	7F4321A9A6A08B108F508820D1C093BE44F94BEBF9C66B7D5AF90B2745FE655BFC119717578FAC8FEFF4135367E162C73A7DB3385F37105F999FE1B9EE5EBFB09B76384969ED28B534618D3241CEB6CC9C208D538F74E543881D8D220EFC13E3D4C7C2A16E580F300E8CBD02F6C6121DF739696B1F1DFFF2134B5305B37FE5D46797E5E79857F93A73101FFF70E7DF2C0D967F8BEDBDFAF0E0367A3A496973345D5056EFF7F9EA1F72DD1E1C77AD8DC6F9BF8852747F97667DE77BF12DBF187CD95BB0B0FF2A5C64
	74F99F75AF23BA925DEDE4E7E25C501439F1E8E7739EE6A77B66BEBF0E4647815A9957271DFEF1C66D2906EC98C9B6DCEF9E188DC1EEF2FA9B52BEB63241C4B6E4FF0CB66CF25B332DFFB6D85C6474B68CB634E1B73701BDA799C1B6CCEB9A188DF35D6474B6A47F5B480635E4C3663F5106BF8C500630BEED70B534616D3DD8967EE40353B2B09BFC5C6474B6C8930DECF06DC39B66CEC49B96DB87E6C3111B1C5E069EC39B3E2DC79B7C48069786E8C3241B1C3ECF0C1DE85407FC3557288FC9130625E48189BB
	BF6946BAD38218FC0F54BCB0BB7D5D6474F686008E773C7A4C0AB604108D6B8F8D4C069AB7B93D8D99130CEC882FC39B0AA6218D6F8F500644163EEC98EEE8C3D3A35A90C4B65C6A16D67FEC8869530674F8A39BDA6A51061CF8346100EDE0B6585D645C665A06B6A475208D01E443A75F8F4C06CCB7B93D8D2989C6B69C4DC39B329250060787E8C3F81FB68CB23461127DE8C3G59F071610159B05CCD4EED4E1E2835C14C2BE66EE077A78673AA71EEBE595ADC08F3268CC8C75C537CFEA55F279A4296A0F17F
	32FE856B93F1CD3846E1EBE37BDD885EBFD63077B79DCE5BA1DB9BE2B19CEBF2B87986B4E2FF2FCB793BC4AEC8F388253B224073FEB92B8B0C774BA9A9B05EAFE7FDC16FFB1AEDAA70341F0E30AF3D9D6D9B81A4304B888BA16CBFC7908BA36CF242A2881B6BC4AC0630AB88CBCEC26C3F9D08E590F695E17904E538902BA06CEA426A881B5709181D30EB8973A1AC73A8E2FE04DDC7D8A0E18B0EA196CA588D040DA5AC2B8B3189045DC8D8ACE10B3B91CB1D0C584D04E59216FD9C31A242EEA12C0A30E5A7902B
	A7AC218731F642FC8973A5AC69A4E243893B00301104CDB90558A84202898BA7EC75EC44A2893B1730B8420A5391CB1B0258C57CDD25AC881792B60BE33590EEA16C9D8B4386C3F884E1B31B99B6864291D3304FD954401E2544C0F8EE7230149220BA202EE4642F64FC760C10BD27F3613BACEF613C675E033D6DFB074452E3D7358ECFF119AD5A7EEDEC3FF4601EF138171951BEE6C86A7D783E6061C77EB313778B9D9D568E3E48425E9413F9EF38BE158C76445AEAEE35CA341FC97DD4B9CE254BD1DEC8FD6C
	FBCB679A77312A3D58A36AE8BE8979A25EBF1B4A5F6D0905F05A54E1A959D331BC6EDF2E6E6BCB27EA73319642ADBACC5E3B3950F39F7A4BC263BE542F1059546168F4B6D9757D64D505F309272CC231DD02D83608B8A3383C4B6CBCE4F56BE7EF63720267C9BE3850D3DFAC727CF1CD1E2F247AF207A60FFAF479B18442A3F418C86FE77B556995D1FAD11A74C2F432E1900EB0CCEF7581F5FA2BA59CCFBBF3F0CDA5E6AA1677EBFCADB4E79A745BC07553303CFF2DF946671A2655B388427E530CF43FF5E01ACA
	F7C1817AB2A8CF1DDE90480ED6490F035FD1C0D20A020974EEBF28C92F90FD89E8C0DF52D350171EDC660B894296201E9474E51EE6AD4ECF56918857D02031A96EFD0250FD24C123FBA56A96FDBF53BD01F4E77377B3B3D2E1BCGB225226E43EE7BF3DA005715AAF48ECE755C9F8568F841909E13EA14670B4C9ABB0BB04FF73A699E8F7251F29A0953BD6B1E2B6365C038F43A116E67353AD7F95E17349664DB64B4FAFA51BDC4479B816190C35D3FE85468DE0D3A6B5DF40701FC041CC60CFCCD1E61EEC3068E
	4B07F0458CA35DE5CD9A5DEBD077BAB75DF5A0EF175370112FFE863A83F5D8A804479A6A7E9F2D6EE2547D6EDE3D6E89A09FAB2711BA53F31EE76AF8C5902E1AE9247B960BC677D7283BFB1FDEF7BD48374BE9787622FB380EB79242238CF56F372AF5338DB1046E72FA3D6EF0100F1453086BC5F7DA1A1617856162B4A35D331AB53A8B30AFB832977B82D39A7685CB79FB073E9076878ACE433EE0CE37DB3D84DE042CAFA64D584E64D9B0771E252B17902E18E5E467C3DABB8BB10F66D95D6AA5485B65B4FCE6
	996B7683BCD0478B0570D8C35D5D07B43AD7226E676B5C6AA548474AE924FEE22CBB9370A29D2F8A42751F98694EED516826BE3025D12F3B9D64FD65B406FB50BD9270D1BADEB804A38DF53F2255CDFDE02BDB3D0C83793459A20D2C59463A0B81B769F88D90F64DB652FD19CD231B7A40E9BDFA5D03C05EDFCEA348036E51000F537122A0AC259B692E7EDE231B7A4093F9FA5D69A01FAD27D112EE2C3B86F00B0E57856141063ABF566A26BEF048FE3D6EG108F16539863C177F84023F53C44CF615E64D3A35D
	FFBA2C1EDF317DF064313ED0BD4F4811E5CB65EB2DFCEDD125A9661B8F2CE44F273BA87CB08F337DCB44DC5EDA46620AB9EBE51BC5DE60905F1E5D4A57ADFE1FA876169658F23EAEF0B8C96C99AF30119C7BEC329A4B656B82DFGF6BEE1F7F06CF3401EA4EC05936395CCC2AC08E337035EED71089570F501A7A14EBF93907BAD472ECB90E78188ECA5DF97488559ABA83DE00E6DG9DD7D13CD5FCDD20G3005946F9E0EED846C92425670F58187243710EC8961580D003DC6713EBA0669ADA79D3F6358FD10D7
	2BC876EB3EAEF0AD4B9742C2B9B694302F897B062F8B5C8258A3047D01E32FC0FACF93360E4F97CFC23C5914AF230EE3FA0788AB63EB85F7024DD7136CFF793A40879067D37263AA0EFD83BAEEA0AC032F8BD483F610302BB976F622B86FC1E073783A4052C9E27FEC01BD4631F23065A5125D42578526C0FA5389FB1CE3D7C1FA7792360D2F954C01F8B6EA9F81CD6C7E2B5DE9ED36BA1DD60B582AD98A7E141DB3F79FDFB368015F0300C47C6CA6369EE7B7F7BBBA21DEDB7E4362DDCD5FD247B2B9400A01CCC0
	6CF903E8973FB946465DE6273995EE4FF634DB1B2452B9CCB6490A32A3A17EA82088A089F819F3D06762E6A68B0236564ED6EE2130AF062FD58C02F8FE947721936CBEDC5CB9EF34BA6457E2F7F6188FA97761E1477FC3F1CC8E33C59BC944397FC01AC1BA7298B2E91611BC25FF57BCC516EF0146D3967D4EEB1A71F594589DBEC769E3A2679877F9F186F85A5CE1A9D9F33538F81766B2FE1FD88CBC56B7FDCD787D9C8FD161629D1B19FC9D01050BBF9B16127EB92E85BD455F15084E00727882318FCBF04DA8
	1D30B9BC1E8542DD04BD4431419076A72C6A9B148DA2ECA70FA74D037466511A9B3F774D06F0B1E1A1A4EBA2EC83177507F050BC1C379D4F41FEFDB4E063002266A9F9A44D87BD730D726C977C7D236C79A2EFBEA5BC006375AADC64595FF9DE3270906015E422CD3773BEB9924231049D28C25BD33FC4AC391CE17590EEA76CDA7E4C419742FE047DF99D4382A1BC0A30392518DEB8E143F9DEE4AD00BCDB00585FB8E602F043824C1F9B69B93F8B30C1C07E8B147C88025F23979865CF8C3FDF9F37C06443F442
	9372989E2742C57E7C0C47E7614C05B0AFD808B62D5A0FBE46923617772F29D950DFE4A176C70EE5C3380A30C17B516F7AAC74A7175EF79C8958A82070ACD5FB025FF1D9C67E0470B50034C5426ED904BF42719295AE7C4963B88B37GAFE6915A748B7E4C5DE7B11459E2441E64D8A004C388DB4D6FBDC6C3F8BCE1B17CD9DBB40425A554660ED37BA26CC96E378542BD049DA33DC3883B1F740EA0AC03748691963F9F750EB650BB0E30E0521B1D0DD88769ADA16CDE52DBC358DC52DBCF583364EFBBE15713DE
	DF423A8F225EF042CE115EC8429E60D89C04D317A23604744EA56CD52EB78742D504ED26BC28DD2A2DF34C169642EE24BC88A14CCC3604A971B8968161E8422611ADF10479737452168DCB49D906D86281342518309B499653B2777270A7ACFD9F5A92C4D89D47C6C3B81C307B7621AD91043DC9FAE3885BF18275A6AFC76CD252DBC758D52457CE58DFC92F8FE1257528F7B0E1760328B7003011BD28B71830AB289EA466A0E62DC73DF3893B239E756690B6F3BF6AADA6EC9869B591A666E2CCEF83E1DFB7205E
	209C6CE70E6E25F18530F1C0D1B92AF1A5975AEB2ED1BFF3B5DFDF484E957D499442EF61F8298A977D4C8A0E33F09D70C2F351262F78DCB5B38F6E337390BB4931AA8857917637EE745BC2588F3C1C3BA06CC7581BFBB0AF82891B910FF991CA58313D189729AB28CC2DE8CBA6E13E9CAB02F085E12FD951169A42A28F22AD964256D95016AE422A73501641044DDB00360CA76CDFF5E8CBB4E195FB501644FCFA5E3787EDC9A3AC329EED49A26C17F5549689FB6A9435C5421E2E2336C858457C7D0DA0880FA66C
	F10E0D03F0A4E1AB1B50169842B66FC3DB128B48962A2F9904DDC07DC2BEE16554BEFD887BD5BD5A62C7585354CE8289AB623241908EA36C1F8DE8CB84E1976F233E02B07FC954D794A2E662BA6AA05CCED8CE8F5A62CBD8FB8F5AB21C30A7FB5016910485D0990590B62191ED89A3ACEEAA5A92C1D83C99ED49DF095855F9E8CB85E12F6721ADF504EDDE0136D888CB49C4DB3A88FB6EA85AB2183028DC34A5003018AC34A51A305F723CCFAC023E2C086AB347F2A0DCC2D8438134250A301F5521AD75040D62F9
	558EE19F42AE257A62C758307DE84B84A53DC21A179116D84367E52B207E2DC24CD60369E593F6DC5882E1FF20A0A0C93AE9AD6B96457D506426CC3836D90FE55247089536D6EB9BBBEBA63BE8E5D1CEE6514A2F160AEF91830E293FC5AC1EF640633708B12B64E7832B65F56B37974AFD68C37C9BF4A6D38A3CAEA0F18FDA5020F91F199932F66DC6274D325EFC48446FE845BA71F45E9FB179205538CE7CB56F17A3A1AC2D0172829276F6AD467DF1C65EF43DE472F19A1E6F9DD13BA7BEFF0D3C36A4DF593713
	D134BF4EF2AB6A9C827CB1EB587D6D9E2B7ABBD216170E84717E2160DF22613339A9BBC7CB617FDE43674FEFB5727F507013D4724237479621EFF10F85C9763B9BBC1EDD1ED6ACFCAA122FF3C397CA8165F9C922AEBCDC225405DF9E6CCFDD9075EA8A6F336D1026CFB1563566C4060D00F088E12FA5B1ECBC04E3883B1E3705642F20EF7F0A669387306E9693B6030FEF03A09C70950E75D79DE46FA485C3F89CD0ACD07ADA98F701EA005A0106G85810581C58125FE8DFD8C10894882548364879488948AB496
	A892C8AA013684D482D48764829A829484B43684757F096B0F01F07AB72097289648856487948C44763C8A07EB8CD07A379087086DBB477614E37B4555C258856487948C948E9483143E8E6282558239007C0002014201583EF0C27F2787464A673EB13B581579B6D67E6D122F4CE7FDBDE0F9D6D7E23C0641724A889F88159868E065248EC74A76317C0D6A276EA1F27932F23298784767F5F2BD687140677936D614FFFFF4323AB1CE365DE4A0D3A45B1EA17B0CF54A5BBC133F37243245B3CA59BAB61EF508E7
	49FEE6A04F5A6FCA8FE9FF7AA315ABA32333E6276842F8FEAA5B3BFBCEBF53E57DA3FB3F051DB1B85FC39C566F31BECEFFE62F1A581EEE8B7B29137527F896307E1CD7C678BE921EF10867A1FA1BE722EFFD136F07596CE8F309755109FC7DA7865A36D48A753914E1E95FD474D1C770CCFAC679A0D38174296CC79D7CFEEF6244099F83891DF9DC0755E2F3318DBB055ECA0E71BD57CAF1DDF63C4E36F7F91CD0600FA15B8E76E9DB020AE2F536251669ED339B58F6BE59B6378CED7BEF0E5636FC317E887C22B2
	346D09BEEDCBD259E6A9535A5665E65BFE835BDCE41BEF990ECFC339AD6CDDBEBF42267371AE904221040D1C4F579FA09CC3586AAFF13C1B3B9E31927EEC043D5F57329E471BCB9B32615A83E1FF209020882044F29893000A01EA01DCC0FE65A8F393178906F0B8D08CD0FA856497D0AD905B0B5485D7BF20608A14796741GD51E396434BD65A93307DDA356ABF1C2645F99AA39C4592E7E349366DFED9FF1052D998DEADBD91B6F6DECE9FCD7F02AAE9DF6D51FF10DE771764756A4C974D9FD377B51C7347577
	853E869D8E799ED1017537332A377ABBD5573652AA3575B72BD23495257E4EAFF72F3F2B4B317E66D7E25BB26D575AB6065FEB15825FC4F1DEDE471E8F1B6D36C3EDECA74CBD787C69FD3E765D837106D4229FABCAFB73A3C14EEF6CABC2D5FE30FEEDAC0457410F0964471D4BD9DCEB9B7342A26BFDF8B94E4547130D9F9A3457349C74B73A925BD7E88336D7E983E2E3CD8CCB07F0B6E1DD7C992F89428D046D6F40766ACB58112F303D06EF40F674D1AEFBE69689E1E9A334ED20A2A093D04BC60CB3B50F45E1
	FB2D0648FB380648FB438651BCEFD0EE031CEFAC8DF5BE323475F9DB34519DE37AA4D9F76F6D698652259F4B938CE40C5E4B1DAE57C523F31F11C439B40AE78CE053100DD8FEC70E33FA6832F63944BD491B6CB12B5CFE1E65FB12B7B9DA5B9DEDD07F36B9ED7017EF54AA520B66775023A02D300DD88F2F5D5FDBBDCC123463C1C529369EA6D68DCBE1E37E87D08F65B52B63D8EF522A306CBFBD0075A60B30DB32315ED49136F099569BBBE11113305E04D6E11D78433ECC5686A09C8514DC8D698215825583F5
	8179D5E35CA7F85CB8F93F634172BE4AAEF99FE736C7B35BFFD97EC6EE1F8FDC3B6434D89A6A3C281743CC9F535B1F3EEFEC9F71322A5D7942165D5C1619F2BD51771F53D5F1137AE107277A59D7DD95752A075F033636D9DB9DED36A6D1176E6177340701022A31EEC66E51564D4F56A9F5F3D393FF76ED6E52BD7BBE486FDBC7C19AE1144EBBC735FD6D973C9D4482BFF193565BB70FFBB34752575B22CDE23D7AC36ABF776EB7FEB67F9157DD8A71CDC0828B603E37323A32895709CACA192F723CD15C0B834F
	0FE41673F4C6C1B84C8B9FFAEBFBF1AA9F040E17380E644DB007590CFA7D392D6CBD77524DE86B41B24556B2EEEB8D706AC9E6A2AF6FC190765B0C365E572B2D7AF1D7EF6B684DFADB2F67EB42E3819FCFFAEF66369EE1BE1830AEBC2F2BD3BFDB2F58DE43ED1F8BF133A87EC7752CED084D4858569266B68B2B23DCF6E239A26B62F9D583F275A6747159BEFD6C2DBC861B74BEBE4EF58C87FC0489FD3C187B485EDB99CBB6076AFCDCD0295839127B9881F123A87EDD7BBD7B583CC1114D657D6E5CAD10BFDB50
	4750DEFD6C2B3F2F5DE2DC679AG37EFC19F3F61EB6C07186FDB50660F7334BEDE34D13173EFBCFEA01327B4BE6175A38242D1E47B77A77A323D37BA1831D5EFFBA837BD9B70222D28F7B437053DE35330955B4B48AA45D61BD81B86DE8F49DC46ED8DE4612DE86B03BDFD4D6FFA4B6789EE36BEA01E21G9EC7FAAF6536B4B19F36212DCFD4AB3696F37F0A01D7318DE56E676934C338E79B5A3A72E4DF3676162FA33769ED357039E38860233761B87E0B3DB8AF08A6EC8FFF7E11D883F33F9A4486ADE2D88904
	EB881B32846785AE42E61C42F941689A9C6BEF60EB5B63A09C83147E9D62F7F2BCC4BE23006D47AF70D5167EFCEBB5C3CE93FF33F471F7CCCDDF73452D7D56112013D5576B5EE4E6CB1F6CF19F1F9B7973535700723F43B668572DED036FEED26ACA8A1F3715C2DC9345DF1069791D3AC071AC96627AFEC7ED1617F19004C33F433A35A87EF45AC154F77A3A9540F544821E389D755A78B307D7182F5B5176FD3ABE75D313626B8C7E3EDDA944B5D19A334433C2887B9256421F1F84C1B8F4BB7A5310575F3612E4
	644FF63DBF9B793360D84093F7205E253CBC7F4E7C59016D3AE70BE27FD31CDF8ABC93494C62ED3D8B423E045D4B1FE785C1B8F4874DB9CE75378FB534FF075E7EB82EA3967044DA4C7B60BC777AF897EF6B7F43FC22F82FB5EA4B685A2D0A0F0D7C59F2A944B55522BFE2BF0CAE887B92F6AF6F4F02A09CDA0BBEDE5D679857DBFF96D52B7751A9661F00A76EC43DA13C6F7D8B73E7A71651835BA4EA130DAB0C673B7B45FC92E4CC1456FF79735DAE887B92F69777B5884221BB512F3BA61DD6DB02F4E6CB6AB9
	EF4AA263B9EFA777AD9662A76EC2FBF672E75A8FB1FFF7213F2F55A86575916717824FC4B20F08E75E9076A5ECBAAF57A088076EC23FE6CFBB2DB68569FC25A92F65BCBFE381CF5C0DFA9F63F57289E67FEE34BFF93BE27FF17E1C3C94F8A61279DABCA70730AFE1097C5D6DA088076EC67B937A5DA798164BEE71DCD731FFA19F83E381CF2C43367248FE77B69541316719CF946F13835AB635FC076263C53C4FAE053826BA7467B6714C9B423E1446F3755A7B2F3745BB98408F2DC31FEF6D350D7569F31D5E67
	7BC59B83BCF18F59461F6567C338E48F5A76F90D563F27EB19FF42A61F1A3EEC1AA2A97706FA1B6C2003BDAF79005A4750BA76FEF50B43E9BB8EF9E136336D42EA9C9DB6F1502130F1A44FBF3EB63E871F25BF31CF7D6CBF6D4A2A78C91271736691FB64EFE376A84F52451E938F6DE469CEGDC5A0BFBC37CFDADFBDE344B7C3D7B69B8G56B89D6DD6272BDB207CC4B2F98E72185B9EC8FD117C8D55G65BEF15B6F28AF926F1209F9DA66DE517FCEA2BC0463B9AADC3C9FD1E64177A30A01673F97776BF8FF97
	DBB314768DCB49GAA82B2815901866D433CAC7A1A1DED5D6B7E03350EF6137528552E5ABAC378176F360FD1DFA46A451F2660FB88413F3BD58A39A0536399C47E7BC4FD88152FC13FDB48F1615FC5FCEF46A860A5578FCB49812AG2A83EA2FC77FFA3E4E917E316AD066F2191BDA7869CC689CBBB0C90159078C224E4F5B433E0BE8F4B879876C623B08D5FB705B86DFC85F9FE89CD094100B4EFCB919687EBD05DFA24AA56E975FD3E440B5FF3FDAAEAE49F81CCC1F4C6AFF95442D5D0FED7F3EFA4F73512744
	7B1490F770FE6C23028E74E73C44B1B3846442760B7E0005A376AB75CD68DF3457F83C7C064FC923A0FEAC59FA7DD67E7D866DB0488E3151FFDF31FEA37F7E42E4EE345A0D788F2FCFA37E464AED1AEFB782785E6CCA7A7AEFBBBAE6AA32BBBA9BB53CC9C5DAD9BD7FCFCE45AE551E6CE4D74C94F6AE0A7B2936483FE91622FB2F2DDD1376DF17EAF56B79DF46AB3AF7F6D81DFA5D2FFC264DD723B87FF3105513CD6A730308773B0ED4B58F32D5606F6F5560BBF62E9778977C3BBB1684B70952F9EFB5ABF39067
	3040F62CE598082A30CF3EE0585E6A9A9596301C25490E1B50267958B40D8DA0A4700553B57EB0C11249CD19251485B3933AB0DBF3B7712BF626A9FCB0D730856F3CD9AA9E182D66A518D5BC30C44DCBBA2E52492C516B2C1B2D54BDFEB88D16101CA7ADF33475CF93C7BE7F334168DC1F2C834364338906E9784587C41FD2CF7CAB630D786D447FED12D17A3E44BFE5187EF0625F95EFA4BF1278FEF6A379D1440F28B36207AB762DB6CABF12784F2FB1120FA37EEC43F311528EA27F3A65C672D9447749B56297
	935FE548B791FF08A13F0178C38D79AE62DF2E616339D60308FF216139D77E446F5070716C0AA06237649A71C793FFF31E91FF9C71079B4AC7917FEEC33E54007C7B8D7DCFEF40FB137377CB541E5EAED9A0695EB55CG93526C8596E8B60EC38BF2B273F3B28B72170A7701D85BE3FD0E78973D4E9D4335625B9A58EFB1B7F9A59567A699375DA0D5BC17993FD0B21E1BE4B708B9C9D703B2E74DAEFAB9C158DA4F5F0FF1B9BB2DC24756A6450F12EFCE478F115E910B125E15BADD6B8EA93C11BF022E4696A53D
	CBC8179873FF471F1987C19E04B660181F1564BEF77ABA8967C0118DE26EA41907252483313D25045C03735D67CEAF4CC7399C3318BBD54035CE1693731BADDF7667FE8767B76DA06BEBD666B7434D4A37EE42A7ABFF27E68460C1E6DA2349D646E3F6C013FE2C2D1EB10378BB5A6D36B69773DD1ECBB630792A2A1F17797AB4B6B934E3BA5C499C52479951426AA933C0BBE61E7C01590F47D7E9F907F7EB46CDA62C14A75FEFC9B5C79089907F76D4BE0649E3A3B7DC9E65847F5B492AB11025CCE3205C3F3B0D
	9F2C5C461B317D8F1AEC440FA67EAD73747DB763A7B6A23FA95B08BF17786B320D524FA17E62E5C67CD2A57DF223F37DEA093F22C35F3FB1FE8B7173ACC67C1EC66C5F4A8FABEDEBFB55693755E63B125E16F2ED3F70C01B423BFF5D40F5E19A53CBB361CA72657842C5DE6D361A03E5266A0DDB37D4EE2D75EC6BEB8EA52D1716337659EC36F7A85FF5AF495176B52F713569A110F77E0D58AE5FB03977B57FB6E11FB126D174B591F00D81CA273387FF556E5E575C540EF229CD222F4902EBF1131093FD0D2B43
	1B751E9A1035B4A9FDCDD753B0BA8FC178F4A5FF9F489770A1CD747E22C94CFF1573386475640D9A1C6666EFFEBEDD0D33734BA88F1FB8211121397487BD9A994DDC7A756FE6EA7470E929BCD61A1CCA19EDBD5E5FBAA4E49D9D0A6C717D4B8D6B0E6AE51DBCD365065A03355B8EEE2F5ED8D5AB27B10C3DCAAF67DDABFFD6938A79B60665B3AF43EF8FE09946B40932CF358CCB49846267AF72B8CFE5E31CE20BA8679A38DAB08EAF67354B7AAAE775F3709E109DE2D14AF904C539E7161FCB70EF7102188E8B16
	7371DD1AFC66DB8B9448791356694EAB14F933135DF9ABE55E17056E3CA21977D536BBEF154C1BF6521D37DA66ADC8F5672D11F937ACF367954B3C3F6CE157A52B1648E5F7F727D27EEF564F53579D653B11B5D0EF8A8B295EDC572348BD6CE51D9BFD12490A5FEF1CD252092B6B0FFE9177DE6147EA74E31A2A8F7C408BBF1A63876E4729AC450F110B14F4E23D7063AE61479A74630945CABA0FF9614772250A5C2F3D74E36BB2450FA34B15F49276745F0FBF88BF0A510F109CA51DF1DE78113EC2113B4BCBBF
	5664ABFED88B14F4E6FA6147F5420FA274E3D021124E507D7D77E3C211A2F70517FE242ED17CD8D12C24730997FE546E63FE2CC4BF36FF25247372017E7B51237A2EEC63896F7C38769B450F873ED552F95D0B7671227023907DF8CD755E1E79E07F7DA8DB2F488574F8670725DC7163428AD5396E6D3F9F3756F3BF8A500FDBAB15F4AE74420F49D50ADC5AC96F7CD8DA2D78E15A242413392FBF7A65F7AB79FAF2A40C17B1B4E636B73A4F77AEEC4471BC53AA467CE2381A00587E45C26E5AC56E733DFB96211C
	45AA6681BDF09DA24B09F9403CA55EBCC38F8459D02BB28F98EB554F77567077F546839EE9C51F7EF9CAB397535C1FB5FCB7CB4DF33BBF7B43A20DACBB7B14F85DFC1DF209936B01E333D28E2E5E5AE5F121269E8CB3295AF3EF7D02CE6E1D2D0ADCFC3CF775E7D60DD2FF0A3ED352D953DB3FC87AC5DC9FAB773F9D7D5F3DDDC967255E5A014E0F63BB9439A2CB7F651E5D2948FD13671D7F9F6ED27C4F28D35279C3D77F7DAF9563CBA77A3FEE0F4A7F5E46C91D9F0DFB953973FB6BCFF5F28F2A56289EBE651D
	7F2FDB957F272A56339E6A175DA26E230D5C7F63687F9A55BAD6E9EF7D104E0F3DB6157FDE541BFBD47776473D9C171F37AB7E4735AA697874AB7F65FBBFB18EB4237F73D5775B2FB774124E2A159ABF5637ABF2BF7416EFBA39A7D577FBC3A6F967FF0CCB71FF41D1A51D190D7D51AF625AFB387F0D68FF65B1A51D4BFA2BC7BABFDA3A9439677B25DF3E77B82E48C5CF754E7FB7CEA87ECFBB2924135EAF3BC55C3F717318179CC27FF3D577BFD2E67F7D38A9CF118BBB5AFF398D2AF9F1C42EF77E5B8A957F87
	2FD452794D097E7B7F41FE6E3F9D7D3FD3B5CFBE56DBBB52797154AAC56EEEAF6ACD1EEABEFD21176D7F3B2F947FDD2A3DB7AE71426E312A79F46CB66F743F277A1E6A41BA360627BA4AA0EF877BDB6EE86BF0195BDC9DFC21C6FEA7A3004F3D453B97F3F6329BD54377F44A9ADD1BA03DED0D5FDB1BDC3566C3A47368CEB62F227D7F456BBBB26F491DECAD5556E6F3014D3663FC11E887C4ECBBA45EC7391B0FB77237D12A5F9B7B7D6E71C0C868E8B823BA047F93F7F9BE4FE172EE63739CD2F79B1F097349EE
	CF67AC887EFFF7B3DD9D1A2352D157173B593C57F10C9F61A3177B6D6B7819A14D43D27201EA01DA00FA1A71BD1CBBF7337A8745EE708ECE25651095AB936A9FD6675957AB787BE8322FCC2A896A095346BE646CC0796B6B58AACD234365F2343274918F2CE36565F234F3D0E0375731BCF25A8E3538C4D4993F0B3F53E637B6F3D83CEB35FFCF2A5CAE0603EFE31BF1AF1A5FCFE38B82FCAF1AA6C7EBDE13392D8D1A173529A52F093FD815F728531657693259572E2D35396C56757C8EC66C95A3761AE369A49F
	427DE96ECA607B09C1388E289DC8688FCD44F73D8681B66A901E7B1618045F3846D29A05C9ACEEFE4B3014AA200696FC66D0DA26346D4B6BB3BD77A7C5052CC3A9AADAAA641AD477353FBC502B5CD7A0171F3F34FF4FE171F940852AFB5FDBD4773ED17B7B23CB64C3A5FFC6404A382B854B663DE68F67317047E4C26E91AE476AC1D08B6669137108C59276E69216D5268D313BC9360E303762B15E90429E33E33CB1044D2133AE256FC947EA14359016D64CB0F69F69CF5826BC0C97CE58B8BA8FB775B0E27766
	A2D6C158ED04759076E74202899BC3D894E10F9316E5C76C8F24371E30DB17233FFE04DD1B0BD898E1BF4BC3AC319531A7A8DE89E1D1043988FBB79773E5E4AB3E77F8B9FF7EB18142D25B3014AC2092209A2096209E20A1C0A300C2014201A2016200529C43D2B2010A002A9C58F7C5B73085CFCF6F1B4A9BCE718E4CE996671F09BA5C31D1294371C9737B2843EB0A164A6FF3B6A5E19FD38FB678B9284C286E05B9300DFFB80F45CDEC9F161281D4540E7D4474CC6CA7CC007936E39ABBB3311F88ED47B44E67
	F1E3A01CFA84728B483BF67B6BAA255D5ED72D78BC66E0FFFC165B01994B3164885A7A474958B6DC04C54C43BC98C75815590825B991BB1A0D75310A307D4B9073A16C4AF244C288EB6E40FA96CD185502D8F687762363D56BDA8F19FA71353088A6D7EBD6E379AE5E02655BGE98D6FC09DEDDB500F319DD8B68FB9D85CE49754CB20E29716EF11934B378630C1AECC23470965BB4A05E95C4A6346C2B82DF3D8CAF6273765BBF133D23E8B36A8BE9F4C6D0F4F723D17EAADE85A013E6A45EAEA8B4D87B02FCA41
	6E2ECE74730E8318D7C11D68677BCB781EEE90160E8ECB19FB94736A23EC4C2BFC40DA0EE29A6B17E2DE859C45B45A1772EF67A09C8D14FC4C5B3CEA592664559055FAD34A3E7E78AC646ED22DB7EDED6CC52E20F80D3ADE5D5908F91589F6579DC3BFBFE8243168987A79CC960BBB864291C03147B02FDEDE04F9155685FDE0972631F8B16615CF9726D13B18FFCF886131C011DD5E66553335CADEFD28DAABCB357447E751B75F3D4E73BCB1F4DD5F734407781EA4D6185F55F6374BE710BD3A0E1FDB595C5CE1
	257968936B52882B6E402F9F445C3FE9CFDF6F41CB5D5A6FA3055C11BE6574E7FA4AE748F2390C6E7E6D3D108F71447783CB785C3122DB7FDDC1BE47EB3A755F955C1700F5219EF8233A71BB5287928776BD4458814ACDCD9A58F794B771F1B46B385E5FBB453C78385E5F62A4743784F88147515FCD1A335493CF48FBA24A571C931E762EF8DCA3D7AA472B152FADF06DB9E1D4AF1E0DD74B8D11630C102FA1F08DB314FBC713CF913AB8B19E6D7C1D464E649EF9CFA6791A8F570A9EA3FD7F5058D9270BE36F71
	24EF2CDDAD67A34773132F01F08DB5541761F63E9723313A38938C74466A305413B01E82951DD4F0B98FD72BED2BBA29152BBF6949274CE235DC3B9C4FD73E8E076BC820D1EE7A36E94E668B57259F69D15FCB1A73896364F8E92764730DCEF9128B4DD54B954B71CC72354123DC10C64EA5479BA4DF7DBD4A05E9640264F823656BB80FF2FF5148C54971249F44B57D87CFF2CFEB6432F571CAA0DC7303D15DFAD0131F96DD1CAE888FB6143B134B85483CE079BAC63E0E77E86788EEE7344CCF74117B94791A63
	63C96EC2DEDFCAE5FE2DFCED112FBD9E6542393EA1B2FF04FC8D112FE19E657E4965A2E4FE0CFCCDBECF6EA34E73A47701269C7265F89572358E2EF6A09F153CFCC6E82567717C3715FD0F6777D79A2F7B7C2952FD5D6761CA63F51F30CA753A0F3C76D84966CF0760DE2F0E62BDCB589E42DE64E9B2EC374D62EAA17C95EE97432B2CCCA162EFF03B0EF12CCE4622D4589E997BE7A51B13004BAD8EA74A3EDF493FCF35596DA27C910FE3EB03A98A6547C4EE232D5D2C32BBF183E2FB283CECFD0E3146F318E3FD
	4A994FE102381C1FC17DB1224073F08E53BA1F0D512167694774239C9FF31EFECC1FB88F4774F060957F8C47745919831B0BFCB9C0393DDE4B893FBE6077667EBF537BABFD4970401F697D3D086E7B0301B777FC74B7432CB99F77FCF9AFDD79BA822EA1C0E1676B472751130D465C0873356596F33EFBB9A67BEA318C886783D5786AF53CB6CFB30F504959FDBD75B157E6EB66913AF8FE9E65CEE8648265F82172F52CC73946E5EA3989F23CD8791A7AF30FF6162B65B2F5710AA0DC75F32336F5380349554B3C
	F6796AABDF07FB5477BD7F6EF8244C9FA5DF4365EB24C7391CAD836B835676A9E75C87F4F039B883FB0CA86D826C833EEA67737A8B74EDE2BD4773AF503709479CB42F87DE408558A656BA8756162B86A8F71E57F2422FF97CDB4D448B757EAE61F85A05FAFF03E8EDA883F8BE97223F768383AB5F63FD4A99176FDDDCEE42057DAB5F588B31FC3F676BB52903747EF6F0FC6EA03D3FF1CB505FAC608D9A047ECACB87D6CEB386A83751EBB96157843E669465666F5B9C0FF5737768D274B77122E1A9AD97213FF7
	B78E2CFC9F6CD34E38FC6338DC58C57DAB5F080B30FC2B799AD34CC5FAFF37F3DC3AD86F6F0BD968EFAA705AAFC6FFA7AC9AD8B93DBBC039650B87D63EE378BAD97845FAFF1F64F8041B3FFB97233FD1402B39E4D88A5BEBBCC19AB51B2D055B5A6C36B6EB3943E25DE8ED033058876CCF7CB9D2B0448D3B044EBDCAE0D88404E3AE2173555814D176E7356A779F934E7CB372F1E7C14748A445273BD53F3FCDBA733AF71FD19D72D9E57CD9A3BB57C59A0C657CB1BF73AA9D42D904B55299C779043DC632BE0405
	73E7A2FE909EC1D845F2148DA2EC9DFF1EA97D825A70AF90FBBB9E534BA66C81AEDB82E193E1CBF2B03DBA42F2F95D8C04F0A8E1C913B13D31043DCFFECCA06CC9722368170845F2BF2AA0DCC758FE7243C2D89D37B99842E3881B1C00690DA72C104E910BA66C8DBB46AB7D951D23444F312C05300530FF5059166D040597B0EC940443897B163759C8884792F683479207C01E8EA15F76205E9242AEABC69DB504A572674681908EA6EC8B4746C0B8123024B518DE9CE1BBB6E3FAE9C391DBD30C719A887B3C02
	E1AE887B9266D30132C3883372673291900EA16C8E0EA58F835F065179AFF4C6E689E1F7FECB3E9196434BB2G42C1047D2D0CE123A19CCE584675A89BC15853DCEF7E25D0EE1752B9DEBCDEAD04AD042DAC27F2A3CCBC939F856131045D47E5A7C0B80630F9A4AB79A176AA172D01F083E1CFF1D99704FD89DBC332C3881BA0728F4251040D65320917C19B3C8C31D5A41BC958DBDC369D42BE043DC8F96AC7D88E4F5389900EA56C850E257EFAD8CA462F35F51C77A5044D6571FCA06CC71858E7BD904223884B
	4D41F28BA36CF5AA3708DF3B17DB94E177F3BFCAAE9F16D2FBB9E28BF8FDE900F07B655AFC7105305F656E7992C058B5DCB69A42494329AD707D4E52A11CC5585F39ECB1042B882B21BADE2B4463760D00F0A8E15B390EB1909EC758A01EDE94046388FB1B6CCB3E8231E4AEDB8761F6429C9C6B0170E042AE643281908EA2AC0A528BA5EC8617CD3D924A72CA321967D5BE04CB892BA1D993E157F05F7CA19CC4589B9C9B8D6189047D193F13908B61342B284F91E757C23878AA1CF7F430C7FA72B859237A1DB4
	7F4C0F4B734E020E0FB3951FA6FF297C7ED56619579DF8C6F548FD23997B698AAA67D13C1C6BA05CC258A29A6BDD04AD67636BE8880FD33049B896C49176BA47A4FF28CB7E08ED1E0263E186E117CFC1DB72893B104E682BA0AC013FAB62474204196661F898CAD85CBC1AF790F6A51D0BBD0130FB32E95EF1B5E25954F62A889B46638DE7E142F6EFE0D8A80443888B5D003263886B6673186CEB206DDC03D8B94FFB93046B898BB423EC8BE177F0DBC2A0BC063012BA0CB71E30F07E0EC84E355087DC0B584F78
	BBAF3590369376A6473AA0BC08306D7C3CC0FF880FA46CDEBAC3B0043073787BB24981B0AE855099869C4B01F089E11DFC5F579A88B790764282CC4FCE58457C5D18D088079336454167CF900E8B40FEE6DD3B52762AD43F1FF41C79F67856D95011552178142B7AFD0F734C6BFE620C6A90E57AA8FF2FA779BA28B757E1B91F627BDE97C1386ABAAC6776EC4577EE556F786CB31F8F1F1F859DEFADD5FCFACF753BE7691957FD599955A14AF4B65F7F36964A53CE657C961F0B79C0580F4AF95BA24577BA556FD7
	324EFCBEFCF496F4BC3ED87169B9556F0645E7DEF7779955A1FF0F3F073F2F4BDEF9E75F1BC8789DECB43F278D00F29EF99DFE873BBDA18963CA746D63EE09E4AE66777D63A1FEA449241F526E033B0EFFABA93E5D6D6EF50FEFA62FBE9BEF6E755AB332F2A04CB69F1BC8FB734E60E3EEB10BF7BD6AFFFF19D6FF639EC57F3E653D691F24532F5F0757F53D584F994F9419AD6D0AE76526BD5B2A152FE7B03E4F7538DF70FF78FCEB88047D494E6717EB6D7C79DE454EEFF37A32B32197BB43C047BA0D1D95896E
	F66ECBC0BB47111DF9FC3E978961186BF12F5269BFF8333FCE568D5AB2AB3EC1DFE69F71BAD381F84D8D1897B3F5F99138CF498BD71FE5565B5E62BE20E326A6AF9C86E5F6811559609BB0AFD6707C7107F0608D189755FD1EC72136E51CAEAF225C72A22D155F2B811EB88273623AEF34F971D83D1297133F6DABAF26741297C520E339A6AF7EDE601E977105189725E453CE5D19856F2876G7C5D1E3E7722FE0677AD7C5D6891183FDF723D84FCA0BCF884666F48623E7C1B5A0BFF2110CE35463F4FD73B7BD7
	389A7D9BC37ED9D64F5678D006A76549B227687B0FF1E4FF9EDF33CB3C91FE5FC86740EC6E4F9EC5E8FF690D5A7AD1FB233EFEB470753E86406DB7222D05955A3218F5D0299F17D77415FFA93D641FBF68A85364DFCF2EFB7EDD1A0B7997C8B6BD145BFB7E7DB59773AF64C64C3FDF71351089900E267CFB38577A1D2033BF73A6ED7E955D24641F7C0E376E5C51AF793DF0A944B35DC4678E16E9733328C1494F836B7B5AEB2237712287F44CD235495952DB39DAFBA41E8703A01E9F59735AFA2DBD3B4D0ABD1B
	4ACF677C0851EE763C2C33A7061753D808B71E6C992B3367F823E24F2ABE6D692DEFCE3DD9EF4FC7BAFB52F87E4C05F8D9B723BDEF6A6C89ECD26C595A27BD3D759F35EE767CCBE74FAC315689716CE44F9BBAFB9E33A876187A3427577668E64F24BC2DBDF338BD0190AF78E6ECC36F765A06266872E0424D5AB694FB337E9C5DF203F3B97752B93A090134E70A6E7C61A93C2D2583BFB390ED3B2E4FB3FE5579D19328354D922837ED0501ED9B4836F6326D2565ACDD1743E12F353533818A77C361FBDA8839DD
	5CDE9F10999C085F934DBA2414E56FFD41D429773EA0B4D0FC3B2674CDCF659A5BD454A2D1F9FF25AB6FEDE2AD9B528A8F74D47FF65B7ADB7F7ABA9BAF71963D4DF767995BFC7CFB45660C95DA1B8B79BDE49A2415F18B5A9C2F9B432EBE2C58FC2857B12C2F336ECCEEB647FB30F914EADFCE1BAE1FFF906B7E101605EC3E36DCEB732B2D0A4D09FD5A5CDB3F6367E673FC8FB6CFD66D97F2014EE6FFEEF3G24B512EC3EC6E77352F6456604BEED6E2DEF9A6FE673A21DBDDFF3FBA2A1DE0CD26EBAFBCCC7D465
	5EEBDD457D00BD5913F56B3014B92AB688F555E06E92C6F3177CDB5126FF77B1F719CB5F7416124C4BC94677506F73E7AFF60857C5F1AD4B5D6F21FD791E1DC2261E5F4386C27CE012F9CEF7BFF4F30712CFA53D5E9B6A6F475C4E6F3BD5FC132B4C71569B5C9B6E21FB4368DBF13ED6BB9973C13A0D4ECE1C2C3573D155DE202EA9B8674F398D53984531FA88375C06E342B53927F3F654F0C8E721461F7F4EF37727E09E7AB302EC69246FD60389DBA44EF807F09459771B92EF66B899A335E3D6BE04DBA5751C
	7EAD7EAC2284702A1128E31DD7733AAE1D0E41AA9DA28FEE49563645BF0A679F90EF44C8AC3B368D5A32FB3BD329E3CFEFB81DB3EC43C76A67ADC316E96D3909BFD30900F8D1E44FDE33561E34E30ABDFFB41F4EF93499376B6D79CB39561ED779F31AEC08D7F4BB16490EBAEF46A14B6D5AB2693ADDFF1E7B5F1D6EF315788E2C173E37E3BEDC51E15C772FD46DF9A43934767F47451F2BC19A81140ECDF79E552EEEA5BF9F6F75BC2A14BE72F36C6D7A3EFF38455866EE55FECB93ADDA1BBF65E7A30E0734A249
	66418B34B67B1FD0EC7E67023EEC6E2DCE4E3DC3EF73F5DB0C7B707B78F32DAC081FFF8775618E77BE7C0F2ABDF0DFB9E01C56077C59C98324E32734AEDB621E565FD47BC9ADEBB2CEEBBDDF1F7707F482A92DC9796EE97D778709FE1B9214F477A7300760867B8D15314D249764E496407F2225A4DB235A0B751F7D5EEB527337459D2A7DD91FD06D2BBAE6D9BF6C4AD76C7AD8357714EF3CF6BF75BD2B955EBD4B3DCB37E70DA26B1F24CDF71CEA7F54377A3DEF1E673C7857DAA5AFF62B76F50A72B2AF0E2B76
	D53DDA17974F16AA3C45BFC279FDD826583CD335974C29EF3C3339CB357769E705E8336C7F9E16D6AF7BDA15D74AFBED4AFB726DD6523A2858BBBB62D4FBB8D62F565AF1596A7E6C2FA552295B272433F17369677309FAA51F1FBB2024BD3342BB7FBEBA28485E1A2B756F7D5C7E7B372BD1C96706EFCF5F3F6EA6453FE72CCA5A4F16F967DFEC3312CE2DEABF488756FB174E316F15F4DED65D935E6EE5BA135A14F4762B76E7FC584BF4FC0EA8690CD76D57B85A4BF4A62A76EB5C275AE771099F218CCF2966A2
	C7F4E75D3C209A73CF7D88E75DFCFCC2C9EF3FCE17C535FF628BDE561FE6D59F7C97553EFC5E66F334EA1F6B1365CABAFFAD77AE1D008AA51D388DCABABDDE26B33FCAC96704EABF1C93DE26F30DEA0F190FD4FB4C7C554B7C79C2F576702B3AFEE8C5DE7F7B21EE551E43E57D995BD5F319E7D47B3784FBB9AF082DD5E417CC565AFFF8F2FF6C97F1AF19625D58D62B9A5BCE64FA59272946362AF9DA1B874F6BFF1EBF2D5AE7781727F55617C86F7DFDCA1D5A2E9A5BB24ACFBF6D4E03CA5A2F2846154D9B3C4B
	3B44C3CABA8D2AF1E50D593BF44ED70DABFFD50DAB79F5276FEBFC1B125EE79D5ABE7100EA6C59D0631D4D6729461E17D4E30FED01F769C81DCABA752A31274C61DD3BFD30DB11F5AE71CEF63E6A7E6C65957D151D4363FF3A7ADD384B98793614546114BEF15F73286C174FBFB772E0C5FA3C94DFADCDEE617B82D4CBC947588683DD107F136E857AED4791090FCFA26412A60BF3FB600749E136E42E429F2B71479A7CD16C12267003DBB20B90AAF207D6620FC27CD160121271ACB3084F0EABF31ADBDDD2A2BB
	0C54A5A56C2D2E01286CE0D4961C36233331C31C6C833C0D155BA0A2CB46A54D91D1B265334EB2F1B3D2480D691A9CFC683AD52F073D3C2BA3C11ADCADFD46EE6E050F3663D6CB0559E55EE5F5327C5AA0CDEDF700D56CE85AEAA93103DF12BADC5DF62BC9CAEF5EEEBD52E96DF0D9ADE5387BE907CB7A5444B42DE0BBF42E28B2F73470E5132FCDD2E21B39556A12BE91EC3B396D508A31656C57AC6725C9E5193C2970A3E1405E595CDE16CA9E25B27B7E190B3E5CB4A1A4C10A5FA0CD33034B9B4CCDAE0733DA
	1A6AEA8107DB9CF6CBD77B3AD21ED0B2BFD71D2FAFC0F21F137B4C59BC3656C1C69F6E3AFA707448699B6261A647A92D60B159DAC69E45301437181D204A6A2C86B2034AAC621DB74F3578AF9B675447CB13E4A7D975792C2B5DA5B28C144F64C9B21773B600552E975EFE647A97CB2223C0E40314F4146D4C8B0A5314D8DBBBDB9B2D4E8BC6DE76C50ADF44A54C2EF616BD3CE2FE81B521595CEA33F7430F8A5E77A0BE5565B437F5B4BB1C2D20B4D367B13248703CCB9F493F6C160F76F24395D117B4DF949108
	2EB8E4F5342EA8EBEE3635D9A981A8AFC87E8BCA1E454953457924EB68271B1E195888752BC2CAE84D4F67FF8B785FC2763700A3859CA9A804C20B16FA7D2767F7F5B11F97F7D16EB2CB2A79069D91C7D72ECDF8750604F8A9919A39584084AAAF5B0584AA2FD33479841723DD54BBB9C1566124710C1C63D69B997A39FB35E270FC4E7C12C5E7BF3228CCD8E889E7AE65FFF38666614AAEAE1D2733F45239E769DA455278F35B52B263BC1DF56ED9DA25D87A19ECAD5BC1BD615C33F4DB1726CFB0BA056E6137EE
	2E3C4E3FA98C7A1BEAA94521B0F8BB1CCC1E32E5E1B6B5DAC2A87B47DED65BF06E793DC7A9A176EFA612015D6730970D0A9729AAAFA67D343CB86CDE96ECEA7893AB8B17FBD9B0AFFEE2E5F1727FC40D3A607FC46B7E1536ACBE11FE12E5B1DCDB9668C5EFE5315DA0F4CE79F43D6293FBC906BDE9E3CF2CA6776945B96C53C845278C15CF73FE4ABE5D2375094DCE3E7C297BB4CA71E9324A27193DF8314AA0F4CE79742836BDAD12AD1D5C27976730CF615A3A47FCE2F7AB73FE4ABE3D2C2DFB4CA736B1534C1F
	32CF11DA1FD83FD76B4127F34A6EE86D5DB03BF7E727FB45135DB1403819C2E3A0F43BC6023FF2AC296793CA9C76EFD3BFA4B6E9A458A919511A511EBE71B308437E15EAF4A429F4449B44C1797B5DF4A43A69385FA54D59E8E56B316DAD36A6339D57D6EDED07DC12641212F654D4D72CF5C9099BAB3741A5C5D91B055094319E4B58FB2BEB60124496F499039F1833B6337F3F56145BEDCD07B3DD0E4C8E73D1EB262B455611E91617F9798AA2E339DA2C199DD63B3549E535E45A19E89ED417AD8E0B2D397B7F
	A721CEA2D92CF72F4D6CF0CACB58B9D33646563C4E232DF966C6D79EFB59CFAC185A4DFC61D9ACD4E3BD09171276CA331BEDD63BE52B75285509B12D5552AC0EEE37F6B8BA1DCD7CA08413B4C7032D6FEC33582DDBF11958A425E9581B2D5DB2BEDB038B8BB80BAF17C8D2FB3BCB1A491F9E28EBE4327C6CG6A58D4FC2260213E1D9B8571DF234615393343FC486A12524A9DED4D36C31DCE10E42EF63026B9A9037C7FC46D3F90D2F5308DBC5E8C6AE85CBB58C4F5875B206FE0B94E165B458652A29ACB624769
	F9A765F51DF3234124B56E23816BCD538DAD6DD7369AD9BAE9857BD370A3597C2DE2B31B9931A34A261C6BB6D73A5B4CDFC9BF77EA044938C698DBFA0E646E8E4566C4B9F725F3B3F777EA1F1A7CA4F2576C1E3B6C1AF8AE5B5C62EE73940FB67F2F568807F10DB827F37728E2F31254D70D78DF4D5D135AFE77A7113B3E5A3A1BA4B34F69716D92E31B4F69FEED1836967FA4EC3E425866F33ABE87E8D7CAFA3779FF353738792772764524BB3CF97B629C2987F7EBEDE676AE17BC3D05F30E589CEAEC73B91D4F
	8FEA473C1F044D0F7E847379491FE0BE0FD3ECE62FAD33199BBB1ED37D06D1830FBE70E5149F5D663F9A2F1B1CA3B97A1AF6667319FCBD27E7EBD10A4DE9324D2C761E03A372FB0A256AED864EC1CBBFF26F414E75153478C9463349F35A66E44566D95239BC1B0C1F29FDBA4D7E1D6BF334787F3837377F1E6BB6FF615E72184D6A77FB9BF874F3E7CC0B4F6ABA172DDBF6CEDB175FA57675102425BF944B26558F8D14E2FF3EAE9E9F856B771D70FDD5BE7BCF3EE6CA4FCEE2435EACE993BF33B49C7068D7455E
	933325ABABF2613A4B7CFDD9FB7BFA3B45D9E931399CCE882CDDDB56685A51EA365BDDE67B6132B64BA673D1737AEE97BBC6D5BEA3F4DBA5CB3F836348CC415B454FB87DDEA5737FGD0CB8788FF9D71CC17D0GG90CB81GD0CB818294G94G88G88G03038AB1FF9D71CC17D0GG90CB81G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG51D0GGGG
**end of data**/
}



/**
 * Return the SPIDLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGEOLabel() {
	if (ivjGEOLabel == null) {
		try {
			ivjGEOLabel = new javax.swing.JLabel();
			ivjGEOLabel.setName("GEOLabel");
			ivjGEOLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjGEOLabel.setText("Label: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGEOLabel;
}

private javax.swing.JDialog getFeederBitToggleDialog() 
{
	if( feederBitToggleDialog == null )
	{
		feederBitToggleDialog = new javax.swing.JDialog( 
			com.cannontech.common.util.CtiUtilities.getParentFrame(this),
			true );

		LMGroupExpressComFeederAddressPanel panel = new LMGroupExpressComFeederAddressPanel()
		{
			public void disposePanel()
			{
				feederBitToggleDialog.setVisible(false);
				super.disposePanel();
			}

		};

		feederBitToggleDialog.setContentPane( panel );
	}
		
	return feederBitToggleDialog;
}
/**
 * Return the JTextFieldFeedAddress property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonFeedAddress() {
	if (ivjJButtonFeedAddress == null) {
		try {
			ivjJButtonFeedAddress = new javax.swing.JButton();
			ivjJButtonFeedAddress.setName("JButtonFeedAddress");
			ivjJButtonFeedAddress.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJButtonFeedAddress.setText("Set Feeder Address");
			ivjJButtonFeedAddress.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			ivjJButtonFeedAddress.setBackground(Color.LIGHT_GRAY);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonFeedAddress;
}
/**
 * Return the JButtonSPIDModify1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonGEOModify() {
	if (ivjJButtonGEOModify == null) {
		try {
			ivjJButtonGEOModify = new javax.swing.JButton();
			ivjJButtonGEOModify.setName("JButtonGEOModify");
			ivjJButtonGEOModify.setToolTipText("Click to save this address value to the selected label.");
			ivjJButtonGEOModify.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonGEOModify.setText("Modify");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonGEOModify;
}
/**
 * Return the JButtonSPIDModify6 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonPROGModify() {
	if (ivjJButtonPROGModify == null) {
		try {
			ivjJButtonPROGModify = new javax.swing.JButton();
			ivjJButtonPROGModify.setName("JButtonPROGModify");
			ivjJButtonPROGModify.setToolTipText("Click to save this address value to the selected label.");
			ivjJButtonPROGModify.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonPROGModify.setText("Modify");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonPROGModify;
}
/**
 * Return the JButtonSPIDModify property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSPIDModify() {
	if (ivjJButtonSPIDModify == null) {
		try {
			ivjJButtonSPIDModify = new javax.swing.JButton();
			ivjJButtonSPIDModify.setName("JButtonSPIDModify");
			ivjJButtonSPIDModify.setToolTipText("Click to save this address value to the selected label.");
			ivjJButtonSPIDModify.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonSPIDModify.setText("Modify");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSPIDModify;
}
/**
 * Return the JButtonSPIDModify7 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSplintModify() {
	if (ivjJButtonSplintModify == null) {
		try {
			ivjJButtonSplintModify = new javax.swing.JButton();
			ivjJButtonSplintModify.setName("JButtonSplintModify");
			ivjJButtonSplintModify.setToolTipText("Click to save this address value to the selected label.");
			ivjJButtonSplintModify.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonSplintModify.setText("Modify");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSplintModify;
}
/**
 * Return the JButtonSPIDModify2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSUBModify() {
	if (ivjJButtonSUBModify == null) {
		try {
			ivjJButtonSUBModify = new javax.swing.JButton();
			ivjJButtonSUBModify.setName("JButtonSUBModify");
			ivjJButtonSUBModify.setToolTipText("Click to save this address value to the selected label.");
			ivjJButtonSUBModify.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonSUBModify.setText("Modify");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSUBModify;
}
/**
 * Return the JButtonSPIDModify5 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonUSERModify() {
	if (ivjJButtonUSERModify == null) {
		try {
			ivjJButtonUSERModify = new javax.swing.JButton();
			ivjJButtonUSERModify.setName("JButtonUSERModify");
			ivjJButtonUSERModify.setToolTipText("Click to save this address value to the selected label.");
			ivjJButtonUSERModify.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonUSERModify.setText("Modify");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonUSERModify;
}
/**
 * Return the JButtonSPIDModify4 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonZIPModify() {
	if (ivjJButtonZIPModify == null) {
		try {
			ivjJButtonZIPModify = new javax.swing.JButton();
			ivjJButtonZIPModify.setName("JButtonZIPModify");
			ivjJButtonZIPModify.setToolTipText("Click to save this address value to the selected label.");
			ivjJButtonZIPModify.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonZIPModify.setText("Modify");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonZIPModify;
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
			ivjJCheckBoxFEED.setText("Feeder");
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
			ivjJCheckBoxLOAD.setText("Load");
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
			ivjJCheckBoxPROG.setText("Program");
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
			ivjJCheckBoxSerial.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxSerial.setText("Serial");
			ivjJCheckBoxSerial.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
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
			ivjJCheckBoxSPLINTER.setText("Splinter");
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
			ivjJCheckBoxSUB.setText("Substation");
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
			ivjJCheckBoxUSER.setText("User");
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
 * Return the JComboBoxFEED3 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSPLINTER() {
	if (ivjJComboBoxSPLINTER == null) {
		try {
			ivjJComboBoxSPLINTER = new javax.swing.JComboBox();
			ivjJComboBoxSPLINTER.setName("JComboBoxSPLINTER");
			ivjJComboBoxSPLINTER.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJComboBoxSPLINTER.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSPLINTER;
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
 * Return the JComboBoxFEED2 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxUSER() {
	if (ivjJComboBoxUSER == null) {
		try {
			ivjJComboBoxUSER = new javax.swing.JComboBox();
			ivjJComboBoxUSER.setName("JComboBoxUSER");
			ivjJComboBoxUSER.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJComboBoxUSER.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxUSER;
}
/**
 * Return the JComboBoxFEED1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxZIP() {
	if (ivjJComboBoxZIP == null) {
		try {
			ivjJComboBoxZIP = new javax.swing.JComboBox();
			ivjJComboBoxZIP.setName("JComboBoxZIP");
			ivjJComboBoxZIP.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJComboBoxZIP.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxZIP;
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
			ivjJLabelFeedAddress.setText("Feeder:");
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
			ivjJLabelGEOAddress.setText("GEO:");
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
			ivjJLabelPROGAddress.setText("Program:");
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
 * Return the JLabelSerialAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSerialAddress() {
	if (ivjJLabelSerialAddress == null) {
		try {
			ivjJLabelSerialAddress = new javax.swing.JLabel();
			ivjJLabelSerialAddress.setName("JLabelSerialAddress");
			ivjJLabelSerialAddress.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelSerialAddress.setText("Serial: ");
			ivjJLabelSerialAddress.setEnabled(true);
			ivjJLabelSerialAddress.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSerialAddress;
}

/**
 * Return the JLabelSPID property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSPIDAddress() {
	if (ivjJLabelSPIDAddress == null) {
		try {
			ivjJLabelSPIDAddress = new javax.swing.JLabel();
			ivjJLabelSPIDAddress.setName("JLabelSPIDAddress");
			ivjJLabelSPIDAddress.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelSPIDAddress.setText("SPID:");
			ivjJLabelSPIDAddress.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSPIDAddress;
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
			ivjJLabelSplinter.setText("Splinter:");
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
			ivjJLabelSubAddress.setText("Substation:");
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
			ivjJLabelUserAddress.setText("User:");
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
			ivjJLabelZipAddress.setText("ZIP:");
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
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelGeoAddress() {
	if (ivjJPanelGeoAddress == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder2.setTitle("Geographical Addressing");
			ivjJPanelGeoAddress = new javax.swing.JPanel();
			ivjJPanelGeoAddress.setName("JPanelGeoAddress");
			ivjJPanelGeoAddress.setBorder(ivjLocalBorder2);
			ivjJPanelGeoAddress.setLayout(new java.awt.GridBagLayout());
			ivjJPanelGeoAddress.setMaximumSize(new java.awt.Dimension(423, 244));
			ivjJPanelGeoAddress.setPreferredSize(new java.awt.Dimension(429, 200));
			ivjJPanelGeoAddress.setMinimumSize(new java.awt.Dimension(429, 200));

			java.awt.GridBagConstraints constraintsJTextFieldSPIDAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldSPIDAddress.gridx = 1; constraintsJTextFieldSPIDAddress.gridy = 0;
			constraintsJTextFieldSPIDAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSPIDAddress.weightx = 1.0;
			constraintsJTextFieldSPIDAddress.ipadx = 46;
			constraintsJTextFieldSPIDAddress.ipady = -1;
			constraintsJTextFieldSPIDAddress.insets = new java.awt.Insets(20, 2, 1, 5);
			getJPanelGeoAddress().add(getJTextFieldSPIDAddress(), constraintsJTextFieldSPIDAddress);

			java.awt.GridBagConstraints constraintsJLabelSPIDAddress = new java.awt.GridBagConstraints();
			constraintsJLabelSPIDAddress.gridx = 0; constraintsJLabelSPIDAddress.gridy = 0;
			constraintsJLabelSPIDAddress.ipadx = 28;
			constraintsJLabelSPIDAddress.ipady = 4;
			constraintsJLabelSPIDAddress.insets = new java.awt.Insets(20, 24, 3, 7);
			getJPanelGeoAddress().add(getJLabelSPIDAddress(), constraintsJLabelSPIDAddress);

			java.awt.GridBagConstraints constraintsJLabelGEOAddress = new java.awt.GridBagConstraints();
			constraintsJLabelGEOAddress.gridx = 0; constraintsJLabelGEOAddress.gridy = 1;
			constraintsJLabelGEOAddress.ipadx = 31;
			constraintsJLabelGEOAddress.ipady = 4;
			constraintsJLabelGEOAddress.insets = new java.awt.Insets(5, 24, 3, 5);
			getJPanelGeoAddress().add(getJLabelGEOAddress(), constraintsJLabelGEOAddress);

			java.awt.GridBagConstraints constraintsJTextFieldGeoAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldGeoAddress.gridx = 1; constraintsJTextFieldGeoAddress.gridy = 1;
			constraintsJTextFieldGeoAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldGeoAddress.weightx = 1.0;
			constraintsJTextFieldGeoAddress.ipadx = 46;
			constraintsJTextFieldGeoAddress.ipady = -1;
			constraintsJTextFieldGeoAddress.insets = new java.awt.Insets(5, 2, 1, 5);
			getJPanelGeoAddress().add(getJTextFieldGeoAddress(), constraintsJTextFieldGeoAddress);

			java.awt.GridBagConstraints constraintsJLabelSubAddress = new java.awt.GridBagConstraints();
			constraintsJLabelSubAddress.gridx = 0; constraintsJLabelSubAddress.gridy = 2;
			constraintsJLabelSubAddress.ipadx = 7;
			constraintsJLabelSubAddress.ipady = 4;
			constraintsJLabelSubAddress.insets = new java.awt.Insets(5, 24, 4, 0);
			getJPanelGeoAddress().add(getJLabelSubAddress(), constraintsJLabelSubAddress);

			java.awt.GridBagConstraints constraintsJTextFieldSubAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldSubAddress.gridx = 1; constraintsJTextFieldSubAddress.gridy = 2;
			constraintsJTextFieldSubAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSubAddress.weightx = 1.0;
			constraintsJTextFieldSubAddress.ipadx = 46;
			constraintsJTextFieldSubAddress.ipady = -1;
			constraintsJTextFieldSubAddress.insets = new java.awt.Insets(5, 2, 2, 5);
			getJPanelGeoAddress().add(getJTextFieldSubAddress(), constraintsJTextFieldSubAddress);

			java.awt.GridBagConstraints constraintsJLabelFeedAddress = new java.awt.GridBagConstraints();
			constraintsJLabelFeedAddress.gridx = 0; constraintsJLabelFeedAddress.gridy = 3;
			constraintsJLabelFeedAddress.ipadx = 21;
			constraintsJLabelFeedAddress.ipady = 4;
			constraintsJLabelFeedAddress.insets = new java.awt.Insets(4, 24, 4, 4);
			getJPanelGeoAddress().add(getJLabelFeedAddress(), constraintsJLabelFeedAddress);

			java.awt.GridBagConstraints constraintsJButtonFeedAddress = new java.awt.GridBagConstraints();
			constraintsJButtonFeedAddress.gridx = 1; constraintsJButtonFeedAddress.gridy = 3;
			constraintsJButtonFeedAddress.gridwidth = 3;
			constraintsJButtonFeedAddress.ipadx = 16;
			constraintsJButtonFeedAddress.ipady = -5;
			constraintsJButtonFeedAddress.insets = new java.awt.Insets(2, 2, 1, 52);
			getJPanelGeoAddress().add(getJButtonFeedAddress(), constraintsJButtonFeedAddress);

			java.awt.GridBagConstraints constraintsJLabelZipAddress = new java.awt.GridBagConstraints();
			constraintsJLabelZipAddress.gridx = 0; constraintsJLabelZipAddress.gridy = 4;
			constraintsJLabelZipAddress.ipadx = 36;
			constraintsJLabelZipAddress.ipady = 4;
			constraintsJLabelZipAddress.insets = new java.awt.Insets(4, 24, 4, 7);
			getJPanelGeoAddress().add(getJLabelZipAddress(), constraintsJLabelZipAddress);

			java.awt.GridBagConstraints constraintsJTextFieldZipAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldZipAddress.gridx = 1; constraintsJTextFieldZipAddress.gridy = 4;
			constraintsJTextFieldZipAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldZipAddress.weightx = 1.0;
			constraintsJTextFieldZipAddress.ipadx = 46;
			constraintsJTextFieldZipAddress.ipady = -1;
			constraintsJTextFieldZipAddress.insets = new java.awt.Insets(4, 2, 2, 5);
			getJPanelGeoAddress().add(getJTextFieldZipAddress(), constraintsJTextFieldZipAddress);

			java.awt.GridBagConstraints constraintsJLabelUserAddress = new java.awt.GridBagConstraints();
			constraintsJLabelUserAddress.gridx = 0; constraintsJLabelUserAddress.gridy = 5;
			constraintsJLabelUserAddress.ipadx = 32;
			constraintsJLabelUserAddress.ipady = 4;
			constraintsJLabelUserAddress.insets = new java.awt.Insets(4, 24, 5, 4);
			getJPanelGeoAddress().add(getJLabelUserAddress(), constraintsJLabelUserAddress);

			java.awt.GridBagConstraints constraintsJTextFieldUserAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldUserAddress.gridx = 1; constraintsJTextFieldUserAddress.gridy = 5;
			constraintsJTextFieldUserAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldUserAddress.weightx = 1.0;
			constraintsJTextFieldUserAddress.ipadx = 46;
			constraintsJTextFieldUserAddress.ipady = -1;
			constraintsJTextFieldUserAddress.insets = new java.awt.Insets(4, 2, 3, 5);
			getJPanelGeoAddress().add(getJTextFieldUserAddress(), constraintsJTextFieldUserAddress);

			java.awt.GridBagConstraints constraintsJComboBoxSPID = new java.awt.GridBagConstraints();
			constraintsJComboBoxSPID.gridx = 3; constraintsJComboBoxSPID.gridy = 0;
			constraintsJComboBoxSPID.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxSPID.weightx = 1.0;
			constraintsJComboBoxSPID.ipadx = -7;
			constraintsJComboBoxSPID.ipady = -2;
			constraintsJComboBoxSPID.insets = new java.awt.Insets(16, 0, 1, 3);
			getJPanelGeoAddress().add(getJComboBoxSPID(), constraintsJComboBoxSPID);

			java.awt.GridBagConstraints constraintsJComboBoxGEO = new java.awt.GridBagConstraints();
			constraintsJComboBoxGEO.gridx = 3; constraintsJComboBoxGEO.gridy = 1;
			constraintsJComboBoxGEO.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxGEO.weightx = 1.0;
			constraintsJComboBoxGEO.ipadx = -7;
			constraintsJComboBoxGEO.ipady = -2;
			constraintsJComboBoxGEO.insets = new java.awt.Insets(1, 0, 1, 3);
			getJPanelGeoAddress().add(getJComboBoxGEO(), constraintsJComboBoxGEO);

			java.awt.GridBagConstraints constraintsJComboBoxSUB = new java.awt.GridBagConstraints();
			constraintsJComboBoxSUB.gridx = 3; constraintsJComboBoxSUB.gridy = 2;
			constraintsJComboBoxSUB.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxSUB.weightx = 1.0;
			constraintsJComboBoxSUB.ipadx = -7;
			constraintsJComboBoxSUB.ipady = -2;
			constraintsJComboBoxSUB.insets = new java.awt.Insets(1, 0, 2, 3);
			getJPanelGeoAddress().add(getJComboBoxSUB(), constraintsJComboBoxSUB);

			java.awt.GridBagConstraints constraintsSPIDLabel = new java.awt.GridBagConstraints();
			constraintsSPIDLabel.gridx = 2; constraintsSPIDLabel.gridy = 0;
			constraintsSPIDLabel.ipadx = 8;
			constraintsSPIDLabel.ipady = 3;
			constraintsSPIDLabel.insets = new java.awt.Insets(20, 5, 4, 0);
			getJPanelGeoAddress().add(getSPIDLabel(), constraintsSPIDLabel);

			java.awt.GridBagConstraints constraintsGEOLabel = new java.awt.GridBagConstraints();
			constraintsGEOLabel.gridx = 2; constraintsGEOLabel.gridy = 1;
			constraintsGEOLabel.ipadx = 8;
			constraintsGEOLabel.ipady = 3;
			constraintsGEOLabel.insets = new java.awt.Insets(5, 5, 4, 0);
			getJPanelGeoAddress().add(getGEOLabel(), constraintsGEOLabel);

			java.awt.GridBagConstraints constraintsSUBLabel = new java.awt.GridBagConstraints();
			constraintsSUBLabel.gridx = 2; constraintsSUBLabel.gridy = 2;
			constraintsSUBLabel.ipadx = 8;
			constraintsSUBLabel.ipady = 3;
			constraintsSUBLabel.insets = new java.awt.Insets(5, 5, 5, 0);
			getJPanelGeoAddress().add(getSUBLabel(), constraintsSUBLabel);

			java.awt.GridBagConstraints constraintsZIPLabel = new java.awt.GridBagConstraints();
			constraintsZIPLabel.gridx = 2; constraintsZIPLabel.gridy = 4;
			constraintsZIPLabel.ipadx = 8;
			constraintsZIPLabel.ipady = 3;
			constraintsZIPLabel.insets = new java.awt.Insets(4, 5, 5, 0);
			getJPanelGeoAddress().add(getZIPLabel(), constraintsZIPLabel);

			java.awt.GridBagConstraints constraintsUSERLabel = new java.awt.GridBagConstraints();
			constraintsUSERLabel.gridx = 2; constraintsUSERLabel.gridy = 5;
			constraintsUSERLabel.ipadx = 8;
			constraintsUSERLabel.ipady = 3;
			constraintsUSERLabel.insets = new java.awt.Insets(4, 5, 6, 0);
			getJPanelGeoAddress().add(getUSERLabel(), constraintsUSERLabel);

			java.awt.GridBagConstraints constraintsJComboBoxZIP = new java.awt.GridBagConstraints();
			constraintsJComboBoxZIP.gridx = 3; constraintsJComboBoxZIP.gridy = 4;
			constraintsJComboBoxZIP.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxZIP.weightx = 1.0;
			constraintsJComboBoxZIP.ipadx = -7;
			constraintsJComboBoxZIP.ipady = -2;
			constraintsJComboBoxZIP.insets = new java.awt.Insets(1, 0, 1, 3);
			getJPanelGeoAddress().add(getJComboBoxZIP(), constraintsJComboBoxZIP);

			java.awt.GridBagConstraints constraintsJComboBoxUSER = new java.awt.GridBagConstraints();
			constraintsJComboBoxUSER.gridx = 3; constraintsJComboBoxUSER.gridy = 5;
			constraintsJComboBoxUSER.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxUSER.weightx = 1.0;
			constraintsJComboBoxUSER.ipadx = -7;
			constraintsJComboBoxUSER.ipady = -2;
			constraintsJComboBoxUSER.insets = new java.awt.Insets(1, 0, 2, 3);
			getJPanelGeoAddress().add(getJComboBoxUSER(), constraintsJComboBoxUSER);

			java.awt.GridBagConstraints constraintsJButtonSPIDModify = new java.awt.GridBagConstraints();
			constraintsJButtonSPIDModify.gridx = 4; constraintsJButtonSPIDModify.gridy = 0;
			constraintsJButtonSPIDModify.ipadx = 13;
			constraintsJButtonSPIDModify.ipady = -1;
			constraintsJButtonSPIDModify.insets = new java.awt.Insets(16, 3, 2, 43);
			getJPanelGeoAddress().add(getJButtonSPIDModify(), constraintsJButtonSPIDModify);

			java.awt.GridBagConstraints constraintsJButtonGEOModify = new java.awt.GridBagConstraints();
			constraintsJButtonGEOModify.gridx = 4; constraintsJButtonGEOModify.gridy = 1;
			constraintsJButtonGEOModify.ipadx = 13;
			constraintsJButtonGEOModify.ipady = -1;
			constraintsJButtonGEOModify.insets = new java.awt.Insets(1, 3, 2, 43);
			getJPanelGeoAddress().add(getJButtonGEOModify(), constraintsJButtonGEOModify);

			java.awt.GridBagConstraints constraintsJButtonSUBModify = new java.awt.GridBagConstraints();
			constraintsJButtonSUBModify.gridx = 4; constraintsJButtonSUBModify.gridy = 2;
			constraintsJButtonSUBModify.ipadx = 13;
			constraintsJButtonSUBModify.ipady = -1;
			constraintsJButtonSUBModify.insets = new java.awt.Insets(1, 3, 3, 43);
			getJPanelGeoAddress().add(getJButtonSUBModify(), constraintsJButtonSUBModify);

			java.awt.GridBagConstraints constraintsJButtonZIPModify = new java.awt.GridBagConstraints();
			constraintsJButtonZIPModify.gridx = 4; constraintsJButtonZIPModify.gridy = 4;
			constraintsJButtonZIPModify.ipadx = 13;
			constraintsJButtonZIPModify.ipady = -1;
			constraintsJButtonZIPModify.insets = new java.awt.Insets(1, 3, 2, 43);
			getJPanelGeoAddress().add(getJButtonZIPModify(), constraintsJButtonZIPModify);

			java.awt.GridBagConstraints constraintsJButtonUSERModify = new java.awt.GridBagConstraints();
			constraintsJButtonUSERModify.gridx = 4; constraintsJButtonUSERModify.gridy = 5;
			constraintsJButtonUSERModify.ipadx = 13;
			constraintsJButtonUSERModify.ipady = -1;
			constraintsJButtonUSERModify.insets = new java.awt.Insets(1, 3, 3, 43);
			getJPanelGeoAddress().add(getJButtonUSERModify(), constraintsJButtonUSERModify);

			java.awt.GridBagConstraints constraintsJLabelSerialAddress = new java.awt.GridBagConstraints();
			constraintsJLabelSerialAddress.gridx = 0; constraintsJLabelSerialAddress.gridy = 6;
			constraintsJLabelSerialAddress.ipadx = 11;
			constraintsJLabelSerialAddress.insets = new java.awt.Insets(3, 24, 31, 16);
			getJPanelGeoAddress().add(getJLabelSerialAddress(), constraintsJLabelSerialAddress);

			java.awt.GridBagConstraints constraintsJTextFieldSerialAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldSerialAddress.gridx = 1; constraintsJTextFieldSerialAddress.gridy = 6;
			constraintsJTextFieldSerialAddress.gridwidth = 3;
			constraintsJTextFieldSerialAddress.weightx = 1.0;
			constraintsJTextFieldSerialAddress.ipadx = 159;
			constraintsJTextFieldSerialAddress.ipady = -1;
			constraintsJTextFieldSerialAddress.insets = new java.awt.Insets(3, 1, 25, 53);
			getJPanelGeoAddress().add(getJTextFieldSerialAddress(), constraintsJTextFieldSerialAddress);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelGeoAddress;
}


/**
 * Return the JPanelAddressUsage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelGeoAddressUsage() {
	if (ivjJPanelGeoAddressUsage == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Geographical Address Usage");
			ivjJPanelGeoAddressUsage = new javax.swing.JPanel();
			ivjJPanelGeoAddressUsage.setName("JPanelGeoAddressUsage");
			ivjJPanelGeoAddressUsage.setPreferredSize(new java.awt.Dimension(240, 76));
			ivjJPanelGeoAddressUsage.setBorder(ivjLocalBorder);
			ivjJPanelGeoAddressUsage.setLayout(new java.awt.GridBagLayout());
			ivjJPanelGeoAddressUsage.setMinimumSize(new java.awt.Dimension(240, 76));

			java.awt.GridBagConstraints constraintsJCheckBoxGEO = new java.awt.GridBagConstraints();
			constraintsJCheckBoxGEO.gridx = 1; constraintsJCheckBoxGEO.gridy = 1;
			constraintsJCheckBoxGEO.ipadx = 2;
			constraintsJCheckBoxGEO.ipady = -4;
			constraintsJCheckBoxGEO.insets = new java.awt.Insets(15, 37, 6, 29);
			getJPanelGeoAddressUsage().add(getJCheckBoxGEO(), constraintsJCheckBoxGEO);

			java.awt.GridBagConstraints constraintsJCheckBoxSUB = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSUB.gridx = 1; constraintsJCheckBoxSUB.gridy = 1;
constraintsJCheckBoxSUB.gridheight = 2;
			constraintsJCheckBoxSUB.ipadx = 28;
			constraintsJCheckBoxSUB.ipady = -4;
			constraintsJCheckBoxSUB.insets = new java.awt.Insets(30, 37, 28, 3);
			getJPanelGeoAddressUsage().add(getJCheckBoxSUB(), constraintsJCheckBoxSUB);

			java.awt.GridBagConstraints constraintsJCheckBoxFEED = new java.awt.GridBagConstraints();
			constraintsJCheckBoxFEED.gridx = 1; constraintsJCheckBoxFEED.gridy = 2;
			constraintsJCheckBoxFEED.ipadx = 9;
			constraintsJCheckBoxFEED.ipady = -4;
			constraintsJCheckBoxFEED.insets = new java.awt.Insets(6, 37, 13, 22);
			getJPanelGeoAddressUsage().add(getJCheckBoxFEED(), constraintsJCheckBoxFEED);

			java.awt.GridBagConstraints constraintsJCheckBoxZIP = new java.awt.GridBagConstraints();
			constraintsJCheckBoxZIP.gridx = 2; constraintsJCheckBoxZIP.gridy = 1;
			constraintsJCheckBoxZIP.ipady = -4;
			constraintsJCheckBoxZIP.insets = new java.awt.Insets(15, 4, 6, 33);
			getJPanelGeoAddressUsage().add(getJCheckBoxZIP(), constraintsJCheckBoxZIP);

			java.awt.GridBagConstraints constraintsJCheckBoxUSER = new java.awt.GridBagConstraints();
			constraintsJCheckBoxUSER.gridx = 2; constraintsJCheckBoxUSER.gridy = 1;
constraintsJCheckBoxUSER.gridheight = 2;
			constraintsJCheckBoxUSER.ipady = -4;
			constraintsJCheckBoxUSER.insets = new java.awt.Insets(30, 4, 28, 33);
			getJPanelGeoAddressUsage().add(getJCheckBoxUSER(), constraintsJCheckBoxUSER);

			java.awt.GridBagConstraints constraintsJCheckBoxSerial = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSerial.gridx = 2; constraintsJCheckBoxSerial.gridy = 2;
			constraintsJCheckBoxSerial.ipadx = 23;
			constraintsJCheckBoxSerial.ipady = -5;
			constraintsJCheckBoxSerial.insets = new java.awt.Insets(6, 4, 14, 39);
			getJPanelGeoAddressUsage().add(getJCheckBoxSerial(), constraintsJCheckBoxSerial);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelGeoAddressUsage;
}


/**
 * Return the JPanelLoadAddress property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLoadAddress() {
	if (ivjJPanelLoadAddress == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder3;
			ivjLocalBorder3 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder3.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder3.setTitle("Load Addressing");
			ivjJPanelLoadAddress = new javax.swing.JPanel();
			ivjJPanelLoadAddress.setName("JPanelLoadAddress");
			ivjJPanelLoadAddress.setPreferredSize(new java.awt.Dimension(429, 74));
			ivjJPanelLoadAddress.setBorder(ivjLocalBorder3);
			ivjJPanelLoadAddress.setLayout(new java.awt.GridBagLayout());
			ivjJPanelLoadAddress.setMinimumSize(new java.awt.Dimension(429, 74));

			java.awt.GridBagConstraints constraintsJLabelPROGAddress = new java.awt.GridBagConstraints();
			constraintsJLabelPROGAddress.gridx = 1; constraintsJLabelPROGAddress.gridy = 1;
			constraintsJLabelPROGAddress.ipadx = 12;
			constraintsJLabelPROGAddress.ipady = 4;
			constraintsJLabelPROGAddress.insets = new java.awt.Insets(20, 25, 4, 4);
			getJPanelLoadAddress().add(getJLabelPROGAddress(), constraintsJLabelPROGAddress);

			java.awt.GridBagConstraints constraintsJLabelSplinter = new java.awt.GridBagConstraints();
			constraintsJLabelSplinter.gridx = 1; constraintsJLabelSplinter.gridy = 2;
			constraintsJLabelSplinter.ipadx = 17;
			constraintsJLabelSplinter.ipady = 4;
			constraintsJLabelSplinter.insets = new java.awt.Insets(4, 25, 16, 3);
			getJPanelLoadAddress().add(getJLabelSplinter(), constraintsJLabelSplinter);

			java.awt.GridBagConstraints constraintsJTextFieldSplinterAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldSplinterAddress.gridx = 2; constraintsJTextFieldSplinterAddress.gridy = 2;
			constraintsJTextFieldSplinterAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSplinterAddress.weightx = 1.0;
			constraintsJTextFieldSplinterAddress.ipadx = 54;
			constraintsJTextFieldSplinterAddress.ipady = -1;
			constraintsJTextFieldSplinterAddress.insets = new java.awt.Insets(4, 3, 14, 5);
			getJPanelLoadAddress().add(getJTextFieldSplinterAddress(), constraintsJTextFieldSplinterAddress);

			java.awt.GridBagConstraints constraintsJTextFieldProgAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldProgAddress.gridx = 2; constraintsJTextFieldProgAddress.gridy = 1;
			constraintsJTextFieldProgAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldProgAddress.weightx = 1.0;
			constraintsJTextFieldProgAddress.ipadx = 54;
			constraintsJTextFieldProgAddress.ipady = -1;
			constraintsJTextFieldProgAddress.insets = new java.awt.Insets(20, 3, 2, 5);
			getJPanelLoadAddress().add(getJTextFieldProgAddress(), constraintsJTextFieldProgAddress);

			java.awt.GridBagConstraints constraintsPROGLabel = new java.awt.GridBagConstraints();
			constraintsPROGLabel.gridx = 3; constraintsPROGLabel.gridy = 1;
			constraintsPROGLabel.ipadx = 8;
			constraintsPROGLabel.ipady = 3;
			constraintsPROGLabel.insets = new java.awt.Insets(20, 5, 5, 0);
			getJPanelLoadAddress().add(getPROGLabel(), constraintsPROGLabel);

			java.awt.GridBagConstraints constraintsSPLINTERLabel = new java.awt.GridBagConstraints();
			constraintsSPLINTERLabel.gridx = 3; constraintsSPLINTERLabel.gridy = 2;
			constraintsSPLINTERLabel.ipadx = 8;
			constraintsSPLINTERLabel.ipady = 3;
			constraintsSPLINTERLabel.insets = new java.awt.Insets(4, 5, 17, 0);
			getJPanelLoadAddress().add(getSPLINTERLabel(), constraintsSPLINTERLabel);

			java.awt.GridBagConstraints constraintsJComboBoxPROG = new java.awt.GridBagConstraints();
			constraintsJComboBoxPROG.gridx = 4; constraintsJComboBoxPROG.gridy = 1;
			constraintsJComboBoxPROG.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxPROG.weightx = 1.0;
			constraintsJComboBoxPROG.ipadx = 1;
			constraintsJComboBoxPROG.ipady = -2;
			constraintsJComboBoxPROG.insets = new java.awt.Insets(17, 0, 1, 3);
			getJPanelLoadAddress().add(getJComboBoxPROG(), constraintsJComboBoxPROG);

			java.awt.GridBagConstraints constraintsJComboBoxSPLINTER = new java.awt.GridBagConstraints();
			constraintsJComboBoxSPLINTER.gridx = 4; constraintsJComboBoxSPLINTER.gridy = 2;
			constraintsJComboBoxSPLINTER.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxSPLINTER.weightx = 1.0;
			constraintsJComboBoxSPLINTER.ipadx = 1;
			constraintsJComboBoxSPLINTER.ipady = -2;
			constraintsJComboBoxSPLINTER.insets = new java.awt.Insets(1, 0, 13, 3);
			getJPanelLoadAddress().add(getJComboBoxSPLINTER(), constraintsJComboBoxSPLINTER);

			java.awt.GridBagConstraints constraintsJButtonSplintModify = new java.awt.GridBagConstraints();
			constraintsJButtonSplintModify.gridx = 5; constraintsJButtonSplintModify.gridy = 2;
			constraintsJButtonSplintModify.ipadx = 13;
			constraintsJButtonSplintModify.ipady = -1;
			constraintsJButtonSplintModify.insets = new java.awt.Insets(1, 3, 14, 26);
			getJPanelLoadAddress().add(getJButtonSplintModify(), constraintsJButtonSplintModify);

			java.awt.GridBagConstraints constraintsJButtonPROGModify = new java.awt.GridBagConstraints();
			constraintsJButtonPROGModify.gridx = 5; constraintsJButtonPROGModify.gridy = 1;
			constraintsJButtonPROGModify.ipadx = 13;
			constraintsJButtonPROGModify.ipady = -1;
			constraintsJButtonPROGModify.insets = new java.awt.Insets(17, 3, 2, 26);
			getJPanelLoadAddress().add(getJButtonPROGModify(), constraintsJButtonPROGModify);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoadAddress;
}


/**
 * Return the JPanelLoadAddressUsage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLoadAddressUsage() {
	if (ivjJPanelLoadAddressUsage == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Load Address Usage");
			ivjJPanelLoadAddressUsage = new javax.swing.JPanel();
			ivjJPanelLoadAddressUsage.setName("JPanelLoadAddressUsage");
			ivjJPanelLoadAddressUsage.setPreferredSize(new java.awt.Dimension(187, 76));
			ivjJPanelLoadAddressUsage.setBorder(ivjLocalBorder1);
			ivjJPanelLoadAddressUsage.setLayout(new java.awt.GridBagLayout());
			ivjJPanelLoadAddressUsage.setMinimumSize(new java.awt.Dimension(187, 76));

			java.awt.GridBagConstraints constraintsJCheckBoxPROG = new java.awt.GridBagConstraints();
			constraintsJCheckBoxPROG.gridx = 1; constraintsJCheckBoxPROG.gridy = 1;
constraintsJCheckBoxPROG.gridheight = 2;
			constraintsJCheckBoxPROG.ipady = -4;
			constraintsJCheckBoxPROG.insets = new java.awt.Insets(30, 48, 28, 57);
			getJPanelLoadAddressUsage().add(getJCheckBoxPROG(), constraintsJCheckBoxPROG);

			java.awt.GridBagConstraints constraintsJCheckBoxSPLINTER = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSPLINTER.gridx = 1; constraintsJCheckBoxSPLINTER.gridy = 2;
			constraintsJCheckBoxSPLINTER.ipady = -4;
			constraintsJCheckBoxSPLINTER.insets = new java.awt.Insets(6, 48, 13, 57);
			getJPanelLoadAddressUsage().add(getJCheckBoxSPLINTER(), constraintsJCheckBoxSPLINTER);

			java.awt.GridBagConstraints constraintsJCheckBoxLOAD = new java.awt.GridBagConstraints();
			constraintsJCheckBoxLOAD.gridx = 1; constraintsJCheckBoxLOAD.gridy = 1;
			constraintsJCheckBoxLOAD.ipadx = 2;
			constraintsJCheckBoxLOAD.ipady = -4;
			constraintsJCheckBoxLOAD.insets = new java.awt.Insets(15, 48, 6, 84);
			getJPanelLoadAddressUsage().add(getJCheckBoxLOAD(), constraintsJCheckBoxLOAD);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoadAddressUsage;
}


/**
 * Return the JPanelRelayUsage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelRelayUsage() {
	if (ivjJPanelRelayUsage == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder4;
			ivjLocalBorder4 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder4.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder4.setTitle("Configured Loads");
			ivjJPanelRelayUsage = new javax.swing.JPanel();
			ivjJPanelRelayUsage.setName("JPanelRelayUsage");
			ivjJPanelRelayUsage.setPreferredSize(new java.awt.Dimension(429, 64));
			ivjJPanelRelayUsage.setBorder(ivjLocalBorder4);
			ivjJPanelRelayUsage.setLayout(new java.awt.GridBagLayout());
			ivjJPanelRelayUsage.setMinimumSize(new java.awt.Dimension(429, 64));

			java.awt.GridBagConstraints constraintsJCheckBoxRelay1 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay1.gridx = 1; constraintsJCheckBoxRelay1.gridy = 1;
			constraintsJCheckBoxRelay1.ipady = -4;
			constraintsJCheckBoxRelay1.insets = new java.awt.Insets(15, 18, 0, 15);
			getJPanelRelayUsage().add(getJCheckBoxRelay1(), constraintsJCheckBoxRelay1);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay2 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay2.gridx = 1; constraintsJCheckBoxRelay2.gridy = 2;
			constraintsJCheckBoxRelay2.ipady = -4;
			constraintsJCheckBoxRelay2.insets = new java.awt.Insets(1, 18, 12, 15);
			getJPanelRelayUsage().add(getJCheckBoxRelay2(), constraintsJCheckBoxRelay2);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay3 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay3.gridx = 2; constraintsJCheckBoxRelay3.gridy = 1;
			constraintsJCheckBoxRelay3.ipady = -4;
			constraintsJCheckBoxRelay3.insets = new java.awt.Insets(15, 15, 0, 15);
			getJPanelRelayUsage().add(getJCheckBoxRelay3(), constraintsJCheckBoxRelay3);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay4 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay4.gridx = 2; constraintsJCheckBoxRelay4.gridy = 2;
			constraintsJCheckBoxRelay4.ipady = -4;
			constraintsJCheckBoxRelay4.insets = new java.awt.Insets(1, 15, 12, 15);
			getJPanelRelayUsage().add(getJCheckBoxRelay4(), constraintsJCheckBoxRelay4);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay5 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay5.gridx = 3; constraintsJCheckBoxRelay5.gridy = 1;
			constraintsJCheckBoxRelay5.ipady = -4;
			constraintsJCheckBoxRelay5.insets = new java.awt.Insets(15, 15, 0, 15);
			getJPanelRelayUsage().add(getJCheckBoxRelay5(), constraintsJCheckBoxRelay5);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay6 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay6.gridx = 3; constraintsJCheckBoxRelay6.gridy = 2;
			constraintsJCheckBoxRelay6.ipady = -4;
			constraintsJCheckBoxRelay6.insets = new java.awt.Insets(1, 15, 12, 15);
			getJPanelRelayUsage().add(getJCheckBoxRelay6(), constraintsJCheckBoxRelay6);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay7 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay7.gridx = 4; constraintsJCheckBoxRelay7.gridy = 1;
			constraintsJCheckBoxRelay7.ipady = -4;
			constraintsJCheckBoxRelay7.insets = new java.awt.Insets(15, 15, 0, 41);
			getJPanelRelayUsage().add(getJCheckBoxRelay7(), constraintsJCheckBoxRelay7);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay8 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay8.gridx = 4; constraintsJCheckBoxRelay8.gridy = 2;
			constraintsJCheckBoxRelay8.ipady = -4;
			constraintsJCheckBoxRelay8.insets = new java.awt.Insets(1, 15, 12, 41);
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
			ivjJTextFieldSerialAddress.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJTextFieldSPIDAddress.setText("");
			// user code begin {1}

			ivjJTextFieldSPIDAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(1, 65534) );
			ivjJTextFieldSPIDAddress.setText("1");
			ivjJTextFieldSPIDAddress.setBackground(Color.CYAN);
			
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
private javax.swing.JTextField getJTextFieldSplinterAddress() {
	if (ivjJTextFieldSplinterAddress == null) {
		try {
			ivjJTextFieldSplinterAddress = new javax.swing.JTextField();
			ivjJTextFieldSplinterAddress.setName("JTextFieldSplinterAddress");
			ivjJTextFieldSplinterAddress.setFont(new java.awt.Font("dialog", 0, 10));
			// user code begin {1}

			ivjJTextFieldSplinterAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 254) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSplinterAddress;
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
 * Return the SPIDLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPROGLabel() {
	if (ivjPROGLabel == null) {
		try {
			ivjPROGLabel = new javax.swing.JLabel();
			ivjPROGLabel.setName("PROGLabel");
			ivjPROGLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjPROGLabel.setText("Label: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPROGLabel;
}
/**
 * Return the SPIDLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSPIDLabel() {
	if (ivjSPIDLabel == null) {
		try {
			ivjSPIDLabel = new javax.swing.JLabel();
			ivjSPIDLabel.setName("SPIDLabel");
			ivjSPIDLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjSPIDLabel.setText("Label: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSPIDLabel;
}
/**
 * Return the SPIDLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSPLINTERLabel() {
	if (ivjSPLINTERLabel == null) {
		try {
			ivjSPLINTERLabel = new javax.swing.JLabel();
			ivjSPLINTERLabel.setName("SPLINTERLabel");
			ivjSPLINTERLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjSPLINTERLabel.setText("Label: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSPLINTERLabel;
}
/**
 * Return the SPIDLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSUBLabel() {
	if (ivjSUBLabel == null) {
		try {
			ivjSUBLabel = new javax.swing.JLabel();
			ivjSUBLabel.setName("SUBLabel");
			ivjSUBLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjSUBLabel.setText("Label: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSUBLabel;
}
/**
 * Return the SPIDLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUSERLabel() {
	if (ivjUSERLabel == null) {
		try {
			ivjUSERLabel = new javax.swing.JLabel();
			ivjUSERLabel.setName("USERLabel");
			ivjUSERLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjUSERLabel.setText("Label: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUSERLabel;
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

		/*group.setFeederAddress( createAddress(
				getJComboBoxFEED(), 
				getJTextFieldFeedAddress(), 
				IlmDefines.TYPE_FEEDER) );*/
		
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


		if( getJTextFieldSplinterAddress().getText() != null && getJTextFieldSplinterAddress().getText().length() > 0 )
			group.getLMGroupExpressComm().setSplinterAddress( new Integer(getJTextFieldSplinterAddress().getText()) );
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
      //if( getJCheckBoxSPID().isSelected() )
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
 * Return the SPIDLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getZIPLabel() {
	if (ivjZIPLabel == null) {
		try {
			ivjZIPLabel = new javax.swing.JLabel();
			ivjZIPLabel.setName("ZIPLabel");
			ivjZIPLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjZIPLabel.setText("Label: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjZIPLabel;
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
		/*else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_FEEDER) )
			getJComboBoxFEED().addItem( addresses[i] );*/
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
	getJComboBoxSPID().addActionListener(ivjEventHandler);
	getJComboBoxGEO().addActionListener(ivjEventHandler);
	getJComboBoxSUB().addActionListener(ivjEventHandler);
	getJComboBoxPROG().addActionListener(ivjEventHandler);
	getJTextFieldSerialAddress().addCaretListener(ivjEventHandler);
	getJCheckBoxSerial().addActionListener(ivjEventHandler);
	getJCheckBoxRelay5().addActionListener(ivjEventHandler);
	getJCheckBoxRelay6().addActionListener(ivjEventHandler);
	getJCheckBoxRelay7().addActionListener(ivjEventHandler);
	getJCheckBoxRelay8().addActionListener(ivjEventHandler);
	getJCheckBoxRelay4().addActionListener(ivjEventHandler);
	getJCheckBoxRelay3().addActionListener(ivjEventHandler);
	getJCheckBoxRelay2().addActionListener(ivjEventHandler);
	getJCheckBoxRelay1().addActionListener(ivjEventHandler);
	getJCheckBoxSPLINTER().addActionListener(ivjEventHandler);
	getJCheckBoxPROG().addActionListener(ivjEventHandler);
	getJCheckBoxUSER().addActionListener(ivjEventHandler);
	getJCheckBoxZIP().addActionListener(ivjEventHandler);
	getJCheckBoxFEED().addActionListener(ivjEventHandler);
	getJCheckBoxSUB().addActionListener(ivjEventHandler);
	getJCheckBoxGEO().addActionListener(ivjEventHandler);
	getJCheckBoxLOAD().addActionListener(ivjEventHandler);
	getJButtonFeedAddress().addActionListener(ivjEventHandler);
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
		setPreferredSize(new java.awt.Dimension(434, 439));
		setLayout(new java.awt.GridBagLayout());
		setSize(434, 439);
		setMinimumSize(new java.awt.Dimension(434, 439));

		java.awt.GridBagConstraints constraintsJPanelGeoAddressUsage = new java.awt.GridBagConstraints();
		constraintsJPanelGeoAddressUsage.gridx = 1; constraintsJPanelGeoAddressUsage.gridy = 1;
		constraintsJPanelGeoAddressUsage.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelGeoAddressUsage.weightx = 1.0;
		constraintsJPanelGeoAddressUsage.weighty = 1.0;
		constraintsJPanelGeoAddressUsage.ipady = 5;
		constraintsJPanelGeoAddressUsage.insets = new java.awt.Insets(3, 1, 0, 1);
		add(getJPanelGeoAddressUsage(), constraintsJPanelGeoAddressUsage);

		java.awt.GridBagConstraints constraintsJPanelLoadAddressUsage = new java.awt.GridBagConstraints();
		constraintsJPanelLoadAddressUsage.gridx = 2; constraintsJPanelLoadAddressUsage.gridy = 1;
		constraintsJPanelLoadAddressUsage.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLoadAddressUsage.weightx = 1.0;
		constraintsJPanelLoadAddressUsage.weighty = 1.0;
		constraintsJPanelLoadAddressUsage.ipady = 5;
		constraintsJPanelLoadAddressUsage.insets = new java.awt.Insets(3, 1, 0, 4);
		add(getJPanelLoadAddressUsage(), constraintsJPanelLoadAddressUsage);

		java.awt.GridBagConstraints constraintsJPanelGeoAddress = new java.awt.GridBagConstraints();
		constraintsJPanelGeoAddress.gridx = 1; constraintsJPanelGeoAddress.gridy = 2;
		constraintsJPanelGeoAddress.gridwidth = 2;
		constraintsJPanelGeoAddress.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelGeoAddress.weightx = 1.0;
		constraintsJPanelGeoAddress.weighty = 1.0;
		constraintsJPanelGeoAddress.insets = new java.awt.Insets(1, 1, 1, 4);
		add(getJPanelGeoAddress(), constraintsJPanelGeoAddress);

		java.awt.GridBagConstraints constraintsJPanelLoadAddress = new java.awt.GridBagConstraints();
		constraintsJPanelLoadAddress.gridx = 1; constraintsJPanelLoadAddress.gridy = 3;
		constraintsJPanelLoadAddress.gridwidth = 2;
		constraintsJPanelLoadAddress.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLoadAddress.weightx = 1.0;
		constraintsJPanelLoadAddress.weighty = 1.0;
		constraintsJPanelLoadAddress.ipady = 6;
		constraintsJPanelLoadAddress.insets = new java.awt.Insets(1, 1, 1, 4);
		add(getJPanelLoadAddress(), constraintsJPanelLoadAddress);

		java.awt.GridBagConstraints constraintsJPanelRelayUsage = new java.awt.GridBagConstraints();
		constraintsJPanelRelayUsage.gridx = 1; constraintsJPanelRelayUsage.gridy = 4;
		constraintsJPanelRelayUsage.gridwidth = 2;
		constraintsJPanelRelayUsage.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelRelayUsage.weightx = 1.0;
		constraintsJPanelRelayUsage.weighty = 1.0;
		constraintsJPanelRelayUsage.insets = new java.awt.Insets(2, 1, 5, 4);
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

	if(!(getJCheckBoxPROG().isSelected() || getJCheckBoxSPLINTER().isSelected() || getJCheckBoxLOAD().isSelected()))
	{
		setErrorString("A load level address (program, splinter, or load) must be checked in order to continue.");
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
public void jCheckBoxLOAD_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(getJCheckBoxLOAD().isSelected() && (getJCheckBoxPROG().isSelected() || getJCheckBoxSPLINTER().isSelected()))
		showLoadWarning();
	
	if(getJCheckBoxLOAD().isSelected())
	{
		com.cannontech.common.gui.util.TitleBorder alteredBorder = new com.cannontech.common.gui.util.TitleBorder();
		alteredBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		alteredBorder.setTitle("Targeted Loads");
		getJPanelRelayUsage().setBorder(alteredBorder);
	}
	
	else
	{
		com.cannontech.common.gui.util.TitleBorder alteredBorder = new com.cannontech.common.gui.util.TitleBorder();
		alteredBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		alteredBorder.setTitle("Configured Loads");
		getJPanelRelayUsage().setBorder(alteredBorder);
	}

	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jCheckBoxPROG_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(getJCheckBoxLOAD().isSelected() && getJCheckBoxPROG().isSelected())
		showLoadWarning();
	
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jCheckBoxSerial_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	for( int i = 0; i < getJPanelGeoAddress().getComponentCount(); i++ )
	{
		java.awt.Component c = getJPanelGeoAddress().getComponent(i);

		if( c.equals( getJTextFieldSerialAddress() ) )
		{
			c.setEnabled( getJCheckBoxSerial().isSelected() );
			
		}
		else if( c.equals( getJLabelSerialAddress()))
		{
			c.setEnabled( getJCheckBoxSerial().isSelected() );
		}
		else
			c.setEnabled( !(getJCheckBoxSerial().isSelected()) );
	}

	getJCheckBoxGEO().setEnabled(!(getJCheckBoxSerial().isSelected()));
	getJCheckBoxPROG().setEnabled(!(getJCheckBoxSerial().isSelected()));
	getJCheckBoxSPLINTER().setEnabled(!(getJCheckBoxSerial().isSelected()));
	getJCheckBoxGEO().setEnabled(!(getJCheckBoxSerial().isSelected()));
	getJCheckBoxSUB().setEnabled(!(getJCheckBoxSerial().isSelected()));
	getJCheckBoxUSER().setEnabled(!(getJCheckBoxSerial().isSelected()));
	getJCheckBoxZIP().setEnabled(!(getJCheckBoxSerial().isSelected()));
	getJCheckBoxFEED().setEnabled(!(getJCheckBoxSerial().isSelected()));
	
	if(getJCheckBoxSerial().isSelected())
		getJCheckBoxLOAD().setSelected(true);
		
	if(getJCheckBoxSerial().isSelected())
	{
		getJTextFieldSerialAddress().setBackground(Color.CYAN);
		getJTextFieldSPIDAddress().setBackground(Color.WHITE);
		getJTextFieldGeoAddress().setBackground(Color.WHITE);
		getJTextFieldProgAddress().setBackground(Color.WHITE);
		getJTextFieldSplinterAddress().setBackground(Color.WHITE);
		getJTextFieldSubAddress().setBackground(Color.WHITE);
		getJTextFieldUserAddress().setBackground(Color.WHITE);
		getJTextFieldZipAddress().setBackground(Color.WHITE);
		getJButtonFeedAddress().setBackground(Color.LIGHT_GRAY);
	}		
	else
	{
		getJTextFieldSerialAddress().setBackground(Color.WHITE);
		getJTextFieldSPIDAddress().setBackground(Color.CYAN);
		if(getJCheckBoxGEO().isSelected())
			getJTextFieldGeoAddress().setBackground(Color.CYAN);
		if(getJCheckBoxPROG().isSelected())
			getJTextFieldProgAddress().setBackground(Color.CYAN);
		if(getJCheckBoxSPLINTER().isSelected())
			getJTextFieldSplinterAddress().setBackground(Color.CYAN);
		if(getJCheckBoxSUB().isSelected())
			getJTextFieldSubAddress().setBackground(Color.CYAN);
		if(getJCheckBoxUSER().isSelected())
			getJTextFieldUserAddress().setBackground(Color.CYAN);
		if(getJCheckBoxZIP().isSelected())
			getJTextFieldZipAddress().setBackground(Color.CYAN);
		if(getJCheckBoxFEED().isSelected())
			getJButtonFeedAddress().setBackground(Color.CYAN);
	}
	fireInputUpdate();

	return;
}
/**
 * Comment
 */
public void jCheckBoxSPLINTER_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(getJCheckBoxLOAD().isSelected() && getJCheckBoxSPLINTER().isSelected())
		showLoadWarning();
	
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jComboBoxFEED_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	/*if( getJComboBoxFEED().getSelectedItem() instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
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
	}*/

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
 * Comment
 */
public void jButtonFeedAddress_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	
	LMGroupExpressComFeederAddressPanel panel = 
			(LMGroupExpressComFeederAddressPanel)getFeederBitToggleDialog().getContentPane();

	getFeederBitToggleDialog().setTitle("Feeder Address Bitmasking");
	panel.resetValues();

	getFeederBitToggleDialog().pack();
	getFeederBitToggleDialog().setLocationRelativeTo(this);
	getFeederBitToggleDialog().show();

	if( panel.getResponse() == LMGroupExpressComFeederAddressPanel.PRESSED_OK )
	{
		//getJTableModel().addRow( panel.getDateOfHoliday() );

		fireInputUpdate();
	}

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

		/*getJComboBoxFEED().setSelectedItem( group.getFeederAddress() );
		getJTextFieldFeedAddress().setText( group.getFeederAddress().getAddress().toString() );*/

		getJComboBoxSUB().setSelectedItem( group.getSubstationAddress() );
		getJTextFieldSubAddress().setText( group.getSubstationAddress().getAddress().toString() );

		getJComboBoxPROG().setSelectedItem( group.getProgramAddress() );
		getJTextFieldProgAddress().setText( group.getProgramAddress().getAddress().toString() );


		getJTextFieldZipAddress().setText( group.getLMGroupExpressComm().getZipCodeAddress().toString() );

		getJTextFieldUserAddress().setText( group.getLMGroupExpressComm().getUdAddress().toString() );

		getJTextFieldSplinterAddress().setText( group.getLMGroupExpressComm().getSplinterAddress().toString() );

		
		Integer serial = new Integer(group.getLMGroupExpressComm().getSerialNumber());
		if( serial.intValue() > IlmDefines.NONE_ADDRESS_ID.intValue() )
		{
			getJCheckBoxSerial().doClick();
			getJTextFieldSerialAddress().setText( serial.toString() );
		}

		//set our address usage
		String addUsage = group.getLMGroupExpressComm().getAddressUsage();
		//getJCheckBoxSPID().setSelected( addUsage.indexOf("S") >= 0 );
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

public void showLoadWarning()
{
	int confirm = javax.swing.JOptionPane.showConfirmDialog(
			this,
			"By including load in your address usage, you are requiring the load to be configured \n" +
			"with the specified program address for it to respond to the request for control. \n" +
			"Are you sure you want do this?",
			"Load level group addressing vs. load number addressing",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.WARNING_MESSAGE);
			
	getJCheckBoxLOAD().setSelected(! (confirm == JOptionPane.NO_OPTION && getJCheckBoxLOAD().isSelected()));
	getJCheckBoxPROG().setSelected(! (confirm == JOptionPane.NO_OPTION && getJCheckBoxPROG().isSelected()));
	getJCheckBoxSPLINTER().setSelected(! (confirm == JOptionPane.NO_OPTION && getJCheckBoxSPLINTER().isSelected()));
			
	return;
						
}

/**
 * Comment
 */
public void jCheckBoxFEED_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(getJCheckBoxFEED().isSelected())
		getJButtonFeedAddress().setBackground(Color.CYAN);
	else
		getJButtonFeedAddress().setBackground(Color.LIGHT_GRAY);

	fireInputUpdate();
	return;
}

/**
 * Comment
 */
public void jCheckBoxGEO_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	if(getJCheckBoxGEO().isSelected())
		getJTextFieldGeoAddress().setBackground(Color.CYAN);
	else
		getJTextFieldGeoAddress().setBackground(Color.WHITE);

	fireInputUpdate();
	return;
}

/**
 * Comment
 */
public void jCheckBoxSUB_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(getJCheckBoxSUB().isSelected())
		getJTextFieldSubAddress().setBackground(Color.CYAN);
	else
		getJTextFieldSubAddress().setBackground(Color.WHITE);

	fireInputUpdate();
	return;
}

/**
 * Comment
 */
public void jCheckBoxUSER_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(getJCheckBoxUSER().isSelected())
		getJTextFieldUserAddress().setBackground(Color.CYAN);
	else
		getJTextFieldUserAddress().setBackground(Color.WHITE);

	fireInputUpdate();
		
	return;
}

/**
 * Comment
 */
public void jCheckBoxZIP_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(getJCheckBoxZIP().isSelected())
		getJTextFieldZipAddress().setBackground(Color.CYAN);
	else
		getJTextFieldZipAddress().setBackground(Color.WHITE);

	fireInputUpdate();
	return;
}
}
