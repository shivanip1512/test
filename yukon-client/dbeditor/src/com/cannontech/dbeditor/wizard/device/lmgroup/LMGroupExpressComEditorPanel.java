package com.cannontech.dbeditor.wizard.device.lmgroup;

import javax.swing.JCheckBox;
import com.cannontech.database.db.device.lm.IlmDefines;
import javax.swing.JOptionPane;
import java.awt.Color;
import javax.swing.JDialog;
import com.cannontech.database.db.device.lm.LMGroupExpressComAddress;
import com.cannontech.common.util.CtiUtilities;

/**
 * This type was created in VisualAge.
 */

public class LMGroupExpressComEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	public static final String STRING_NEW = "(new)";
	public static final String STRING_MODIFY = "Modify";
	public static final String STRING_CREATE = "Create";
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
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJComboBoxZIP()) 
				connEtoC5(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJComboBoxUSER()) 
				connEtoC7(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJComboBoxSPLINTER()) 
				connEtoC8(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJButtonSPIDModify()) 
				connEtoC9(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJButtonGEOModify()) 
				connEtoC10(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJButtonSUBModify()) 
				connEtoC29(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJButtonZIPModify()) 
				connEtoC30(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJButtonUSERModify()) 
				connEtoC31(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJButtonPROGModify()) 
				connEtoC32(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJButtonSplintModify()) 
				connEtoC33(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJTextFieldSerialAddress()) 
				connEtoC11(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJTextFieldSPIDAddress()) 
				connEtoC34(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJTextFieldGeoAddress()) 
				connEtoC35(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJTextFieldSubAddress()) 
				connEtoC36(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJTextFieldZipAddress()) 
				connEtoC37(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJTextFieldUserAddress()) 
				connEtoC38(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJTextFieldSerialAddress()) 
				connEtoC39(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJTextFieldProgAddress()) 
				connEtoC40(e);
			if (e.getSource() == LMGroupExpressComEditorPanel.this.getJTextFieldSplinterAddress()) 
				connEtoC41(e);
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
 * connEtoC10:  (JButtonGEOModify.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jButtonGEOModify_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonGEOModify_ActionPerformed(arg1);
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
 * connEtoC29:  (JButtonSUBModify.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jButtonSUBModify_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC29(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonSUBModify_ActionPerformed(arg1);
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
 * connEtoC30:  (JButtonZIPModify.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jButtonZIPModify_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC30(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonZIPModify_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC31:  (JButtonUSERModify.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jButtonUSERModify_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC31(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonUSERModify_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC32:  (JButtonPROGModify.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jButtonPROGModify_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC32(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonPROGModify_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC33:  (JButtonSplintModify.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jButtonSplintModify_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC33(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonSplintModify_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC34:  (JTextFieldSPIDAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC34(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC35:  (JTextFieldGeoAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC35(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC36:  (JTextFieldSubAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC36(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC37:  (JTextFieldZipAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC37(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC38:  (JTextFieldUserAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC38(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC39:  (JTextFieldSerialAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC39(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC40:  (JTextFieldProgAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC40(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC41:  (JTextFieldSplinterAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupExpressComEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC41(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC5:  (JComboBoxFEED.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jComboBoxFEED_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxZIP_ActionPerformed(arg1);
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
 * connEtoC7:  (JComboBoxUSER.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jComboBoxUSER_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxUSER_ActionPerformed(arg1);
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
 * connEtoC8:  (JComboBoxSPLINTER.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jComboBoxSPLINTER_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxSPLINTER_ActionPerformed(arg1);
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
 * connEtoC9:  (JButtonSPIDModify.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jButtonSPIDModify_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonSPIDModify_ActionPerformed(arg1);
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
			
			if( item instanceof LMGroupExpressComAddress )
			{
				if(((LMGroupExpressComAddress)item).getAddressName().compareTo(CtiUtilities.STRING_NONE) != 0)
					address = (com.cannontech.database.db.device.lm.LMGroupExpressComAddress)item;
				else
					address = new com.cannontech.database.db.device.lm.LMGroupExpressComAddress(type);
			}
			else //new address is created
			{
				address = new com.cannontech.database.db.device.lm.LMGroupExpressComAddress(type);
			}
			
			Integer addValue = new Integer(textField.getText());
			if(item instanceof String)
				address.setAddressName((String)item);
			
			if( address.getAddressName().compareTo(CtiUtilities.STRING_NONE) == 0)
			{
				if( !addValue.equals(LMGroupExpressComAddress.NONE_ADDRESS.getAddress()) )
				{
					//we have a none address selected but have a non zero id typed
					com.cannontech.database.db.device.lm.LMGroupExpressComAddress 
							addressTemp = new com.cannontech.database.db.device.lm.LMGroupExpressComAddress(type);
							
					addressTemp.setAddress( new Integer(textField.getText()) );
					addressTemp.setAddressType(CtiUtilities.STRING_NONE );
					addressTemp.setAddressName( CtiUtilities.STRING_NONE ); 
					
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
			addressTemp.setAddressName( CtiUtilities.STRING_NONE ); 

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
	D0CB838494G88G88GCBDB8DB1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FDCD5C55ABF953D6B166DD25146366C2E9515EDD4ECEB65166DD26B961695959515959515959595EDD6D6C73CAAA0AAA666BFB4D2D4D4C2D4D4C4D2D4D0A8542B202222F261DE3897AFDC040B22D97E1E19B94FF37E5CF3010B2B2F7DBEAF1F4FF06EFCE71E6779BEF3E6664C19B3E70E147EF53FB1239253A5E95498C97EDBBDCA12CA0E0E126E2A297ABBE22D1149A6C975778D0449521BB39968
	87E943CA471B522419C5C9908FE4714963CD53607794E9D0EE5186D06845CEBA243D78557C462E6DBC1409F64671783B47D361B891C290E003A0A4BD168C76471D18AE63B100CD14FF33B0AAD1128673BCFDDB262B700A47C43EA0B806C2987CB84BB377C4061C1E8A71AC88FE90829F97F9C361184C59DDD8D8A0672B39343F1470603644894C16CABF0654A704EC1EFC4C141E6285B1DA3A382A980EC566430D6B6ADD4D3975161696D70B5BD6EF4F35D99DEED7DB2E55F654D1EF4BF5B69796D6EC5D52666AE8
	5DEDEBEB37C066D21E41ECE931B97BBD41F85F561244F5CA129D62B9D18CDB6CE2E5958B7111905223107BE3930AE4EE01077BCBB157EE704A3DB5CAF08E0CD238C70D7D5CD472A4537F4309D4D9AE8A52D38193FEB5EFB3FB714BDAA7FBF64CF14252E6D59CECE44E1117251D2DED367676CD86FEDEFE827D6C73A47AF97FD646A39C62519062893FE49B43131ECAB6E5C2A8F88A7D7FEEA17ADF7D631D52083AA309133C78EFFECA785D791462FF6A2F270F1F416B621E392CBE8D0034E888F190521ECEB665C1
	B0C318455B40175FAC066306BAB7B8536CEA59505A3A51E9ED5B6A325A1C703B3090D2E4EC272D5E55E615A4D14F6FEC49B16003417E34360E33A0FC5BA3E13D8C5DF90B64F6B7A60EB170ED4013664413320E0027955F4222D9392DEAE3659686B8137B117D6678C90EA7C4AB382887F93C1E31F842B349264867311C2BA7A2178A53B5D25A327644F186DC026E33A7063EA4B88CFEC961A24AF8D996AB63F840D3DF0676882188C28D0413AFE39957FC33223BB2AE077A5756E2F1D6F65435DA5A5C9BBB9CCE2B
	2DCDAE6B9DA73AAAEB23A0648E79ACA74A257F8A75B99A7637B643F29956FFCFE2504B22BC42DED64AC5588E72EABB72E54F3AB1114EE1A1EF9331AF0BF3B81A70920EA728F0F1EE077236456226D712CDF688A27F0DE6068FFDC5642FA4BD4FF17CE42C028BBD17F37DAC1E73EA32A96275E413B83F0F4EE77D2D75CD68E7A184C188039089A18E0274961E639B3ED95A43F6E4D61DDFA1DBBB77ABB85ABA5DED167216F63725255E365B52663054B9ED6D121C47B11715EB1DC62E05376FFA30314556E2EB3338
	9D2E9650EEE936C1E5EA97F90E4D1D4C723459F8EA15235986861ADBA554FBEA6E94B8BAAD6D6EDD2DD60B5B266490693F1A4B6A44D18B6B58C1817CB63F454A6B915E1FE7426F2237302CC6AE6057D80807D239A44E7B920EBFC06536B98B8BCB9C750C06256DB85651223684B806C37E32B8A8D788B5EF43B59AC24DBBF004D07BAE740F712093027D3DE453747761DA882175834093G07D07BE132E9B804567FA41BC6C0707B882E3BA312CD819026FF1CEC6A87A16713E453G88C51FA61B42A1140DCCB68D
	05E07E8C7AC788811FA71B0A3F007E99C2BFA945D4BBAA45141118E29A8A61646894D30EA945B48CC25FB1A9262231A926B888A163D2CCB5C9A92674E4488741BF855246271892A00425426F89A9268888D593D3CCB1901C13D2CCA3A078272548FD50BB555856CAA65CAC65BCF944302D3110BEB905371B9C7928343573F999D6816E069088E1G04A8888990523FCCB165C3280260069088E1G04A88889905227C0BA04AA88EE88019086C0080210GA1BD9D52A1D4C1F0C38804B0GC29404848869D3A19DC2
	9584B704C08883A0C4C1C800107E9524C3280260069088E1G04A88889905227C1BA04AA88EE88019086C0080210GA1FDBA24C3280260069088E1G04A888427FD52EC868G58F9E775263BBE0E558BD67E5E52D9BD7116466A20914E6A94DD3BC01E559BD6F73B62416A18914E6A34663CC33D4A74E2372B10A357D5DF650C82EBC7BD4D4B5A25BA4E5AAAEB5B2C7EF7A74B5A21BA4E5AA8EF0F50FED9BB6A8D7770CF3B674E7A945626D9DB667EC2DF405A29BA8F6B07D89F435AB26BDF0C74307E009DD95B57
	2731FE049DD99F62D1FF468B0C759F2CAFE37D846B8B7579EA64FE117DE6FDD5F7FE054AF50975017AB4563F51B97B0FC8AF6A220CD97F063FD9BF4C7ACE5687323EDB1DAFC176457C1927CD560F76743C09367DEDABEB5B0B6077789E48FCAB09F11E377415DD24CD75022FD17DDE8DE1BB04C95D7078418B7E35AEDEAF31EB246FF5FA97043D3D10B38ACBFD48BBDB97E757F8B6FE6F68016CA6DD7CBB886590585878C8AF392F6DC19EB6B6598F41A247CBA19C5765E1E3247990E038A5AD71226710FCECB6C8
	5BA19F779824B964A31BC818876118A46A36BE9F9B8FCC10FFEF6901DF5F4847E586E9DBD53F974AC7D7973AEC2A5FECEEE4BD04831298D72B739D160F9B8CF4D47AF05E44F831A91B0F6961BA1EB6A3451487C10C993F1B17A9713174C6CB3B8DC74F5B5BE060EB33AAA3C81137B00B35C55BD1DB0B3B4456EAEB31420FF6F96C3AA10B1529BC2EEEE07E8BFC8B4759FD56A6987836933EB5EB3240C107FA140E693BF83A456DEEF354F538ED15B6375B515228489F60DC5AB537F06D322F4EC54C57B2703196C2
	AB797ADA96EB7FB8BE2EF0398EF734D631113FBC4E3E154FA778C37E6099B85EA95E46661C42A97EBD0FAB690FEF2D6E226C19DCEF5A9A86E1632DAEED686BCD6FEC5CEA8E11F50D11B1F68D18AA575199F23933BE09752B2CBD3076455AB16B9758B50335E5F6CDE0FD9CEBA72C2FE1570055F29DE6FD926B8FD8FBE3FD916B37D9FF4F7AF856BF31F6407ADA562ED9FF4B7A9156AED8DF405A986B3FF7905F17B85F61D03EE9B3D3CC8590EAA174019086A19A02B4AB45148D418C2193C2BF88919062A1CC4FC8
	B195C330C38800B090C28C04245950B6A0D4C37003509FC2A4049190B26724184AA034C28802908EE1B804342F41BE04DA88FDA004C10806A0E5027DCC2CA73F5F9610281CAFAC630912713536A765BE3187653EC6BEBFD83E5E4E43D679BCE05F6A6DBCCC144F436954377F8B67CE107B386D5E6AE632018C2B2F6C5AB9C99257F256276A6BAB4B272F2F6C5A282E2F2CDE32FA0D7552DB7D457E812FF95E6AAFCB437A7BFF61179F44B57B19795E479CB1730D479C2F4DB79EF3444D6FFACC11B05FE8CCA152BE
	1D4F5A056BD80BEA0CA2ADE073ED752E16969B9FA81108A41EFD3CD3457CF034B85CFB9C56C61B3B52F18286FF1DF0CDC813DE65F3FAE678ED4D447929E373F0FEAA6B440DD278218A43797A206B8EA78E4A94630A48CCE5FECAF09A35100DFDECCEDBB39454A6D7C70BDB606396A6A9F87BE80EDD7A75C9A61BDDA968EF30DA7913963B2D7E70C6D7E70E43EDEB4EE57FAA5D9637ED135D5252E833E6B7D90EDAF2AD475C397C1C6416C3FAA97B35A8735BEF57E5FAFFB4155360E833153734F648131F590BF6AF
	DAA6F8142DBF41F97033F576D9AC977D5E94691B221FB80F9247D1C77B7FCA012C1010CB720FEE4EE5274DE9B93E6CACD24897760F0FD559DFFE966D2F9476AFAFD059DFF1966D2F9276FFD55B21584FBF1BED22006A40668537F18E5F2379CADB1B4362142BE50E05375ADCF1B05B5A9ADCED4D1EE4B670641E51E9B2B4F4E8034EC205F793EB41035548FF55B1511E3E2F02BEFA33436634EA9D28073BBFF7AE7FAF7B4F94F766369F03EED9563D0925763AB015322C9EF70B36AC4BDB2C504C2D9D9667E99624
	6183EA25A895BB3E16E39EF2CFC93BC6666EEA2EF33153E3AEAFB9C32CD596FC653B8C79FEBA2A1F166F16526DE716AE9870156DF24175AA1B0EEB652E0DE738E8F7ED74156B8A4135FC1E2E9E18F7EE5FF2E649B28B3E32DD0975A05A6DF7265A3FB1ED236EB4CFF0F9FE698DC6FD6919ED7B42064F6DE819DCFE379C7BD14B19DF0CD61E914EC6C537DC6631E81E910FD5E70D478A194761B4A39E2B4F9A0FFC3C16CCB362D1F056F82C14F93C54EC44A37F2C71D8A573F8D1B26231622C71D8AD73E8C8B26231
	7C2C71A810F9A4249A71D8F636F8ACDFA373282F7FC9576719AB4A37D5156EBCEBCC72E87C75594F4735DC4E58354B0847B262E1AE567158D5F9964BE3B971787AC82716473EF273D923A10FA71E49541545665252B3B32EB4A211CFE571E21A2EAC4E54984C0806FC3F591C2E63F026462CC69C64FB4EAB0E6B4EC745768DE76FFC2C267361F05DF4967B0DA63D951FC772F9B26FB1D9A1E7290FE9D2DB5073DD56ADE1F95EEB1E695A33F43E1B5496FCAEDF3C3E7ED0FB6D39D03E5D972F4CD7AA7A1B6E7EE333
	4DE67D6FCC1FF861EDE449673BBD9CF79F7DA9CC705F5861F63BDAFEA16485F7570F571E256BEC135A024FF5854B7A03422B4F4E7538C9E540E7364A7C703669571E25EBF7135A024F0C711E69D86D55E7679A5F24B260B35B15D43EAF65DEFDF64683CDAA83BE73DDC5FC6316A1DFF966664C55DF45004F1D03D2FF17B7DE2B65FB867B8715851F99E37DCD365E2465FB0647C4BAABBE7346B902A7565F243E0630F95D2DAE2B23617819612D3762B3EF1CD370DBFA231AB7B40EB3CF1B0C784CDA99BBAFB2EBF8
	C3A7F4960A9B0D785CFFA84FC7E626E9F8C363BE733C49084F653D06F87FEE0326FE33B6FE6609ABD6FCE60E77AA4F4E5170E6635CB34FDB3162B3EF9CD3B7B75F2A295F2DCEC70B7BACD4F1159DDF39676398CF1A70337EF984F4D4E773E9C47E8A2C370D3B744FC63758DCE7150AB27FB2A05163B9EDC75DD965220C3B1EDC2067324F51FAD639A8E32AB9A97A7A322B5D56F6D64950337493337F771F252DA890DC1E1B25AF97F31B2B712C164B9AAC17365D676B4B05F795E779BC6561BA7BBFAFE66B2F62B3
	D3CC26EFD2CC995F605A738B9632F4DCFBAEAF305F64F2BA455AABC88B7C062D8BFB37ADDDD256FA65834676B5700323C8EFEDB7CA9FC66913F6982547D3FA00E13AE9AE26CF5EE914BE1D52AFB3CC4F2174A915C66945149EE818DEC569D32B0C526D147EBB43741314BEED17D1FAG2587992607D07A0C5DC66983A97DAA43742114BEEB0FD1FA8C25879B26A7D07A6CEA237424F9187EA743748CCA7FFA2FD1FA9E2577B3CCAF2174EF769925D7D37AB50669CECA1F375F307ED2FA08E1FAA0254F2FB1CA1FC8
	6DE3C7B769BF992677A77D1F9B268F2274D3870C52A3A9DDBAE8149ECB696799260F207444C3DD734FB5CCCF4DC279DB3B11FF4EB0BD1364EBEA0D5273A97DF94374B2CABFE8695A7EC55D245F51CD7A4B5D24CF2C6BBA3D301B748643749A726FF54374D6CA37579B257BD37A9B06690A7D29562E53B7F4135E5CCD7A6F3BC97FF7B7696FF513BE5B56F5FAF9B7699D5D24DF5DCD7A435D24FF5CCD7A7C06EE7A972F6962BA5E1A8DFD0054EEBB52E1EB2937311747FE309D477D9F323F54256D36B8BB14FDA5CE
	E533F7A71C2A774E845E3648F84DF667A2762E59E152FFF251D8096D8FA1F40AB566E1962633354D51ECE9BB4EDE241377B73A7F04FAFF23A0281FECAF24E8B846C190E32748CDEC3F0D2E46BA036773B6358377C1B2C1BC83C251FC216F64FC317EDC647B1C7225ADC8B1E5AFD07215ADD0673BEDA7668BG3C3FAADF04A6DFC3A569DB887A962A74ADD4671BD9C57AG6F8FA1E22148E74AD667FB1E72E581DE8421A6DB648B56643BFA97669B8E3834C849275537EBB7664B04BC85DE73A56F41FC55102755EB
	3EFBA8DFG64696FB55F11EA4C9781F9E23C661B3F9773C90BD3CC5397FB4B778C65AB00BCE62F79AE590779DAA1CF9F2F79B66C47FC7DA1CF38AADF4CE2F53E8FA9DF4E1294D34992A51FF509BA5F0DB518AF9470412AFC311AFC878E103E9C5017235217234EB779A06983FC30AADF2CA65F3FA9DF4E37206FDB153EEF55790E9FA2FD008FD6650B55647B3616746502BE8856DC11AFA4D71D6FBD4A978738E9E90AA9EB29ECF729BA5F6D964CB7G7021906264FCB9F99AFEF5184F8DF85FBCA5DF28A65F3775
	18AF817054E5AAFD4B5479DE22FC1D0087C088DBA67245EB72DDE745FCC54BD3CCD54B15FC6E656AFCFBED18AF9C70E8D53E911AFC939AB0DF498AA86395CA3E13AB54796E25FC9100472872C9796AFC9D0D182F82F0EB3E124FDF13EF119D73C582BEDC15AFE92526FF21FC550037C288D8E954BE7AB9B05FD0C80F751A2F2A097A5DD5506F2E7216AF69B015B36429721AEF9065F3C31E3EDE7335BA29BE2F927D29F13E2CE64CB798728D732AAF1A720D04BC692BBD73652FE679C6523EF5CB891B62E2583B63
	A4690FA65CB3EBEB1AFA3FBDEB3B5570BDA9DEBFD78B7DFDD7AB6FC9097B7428A5CCEFA86003D663BD7AFF96295F8F5F505E6E2AF7702D1C646B74F50B556FD0896CAF0BD3605860E8EBF71737D8ED1DF82D8FDB4C38323D1EE4D83EDE5F34043D4B285F3BCADCF271757BCD4778D4C415EB134BEAAB036142A6FA45EC37434676B4907B5FADE847720ADC0D6F107D34D93D1F5F20C9FB9347FBA91B2482D1A65985CA59C47619BEDE683E01F45BA1DDE01BF7A0560F3087880BA62CECA7E2E9EB909BC2D899E15F
	D7A266C758C3048DA42C228A31F8429EA6AC3B9031ED3B902BA56CD1420288B36F26F2A36CB1421256A236F38FE24504BDC1D8A7E1D55508059176A4E143895B3D97314CA2441EA62C1A306AFD088590768CE191046D5B0F18749DE24F91D6C058009A44DA89DBF9G317E045DC1D88CE12B8FA2B6FD9DE28389B393E6BBC4FBB19276E5ADE26104DDCE58884252AD08652DC76C0FF5D47F881BD3CF750F30BF93968D18E8E33FAFE44F190D761C2C670DAE373143115B61F630CD52ACEE8B1F4B6343DD39DD657D
	48F4327614D61CA26F9F19301E6FA98171DA881D9044DEF675BBD265369DGD8C4B15E873E55283E0F10B6407895C245869CFBBF38032D85757D098976A3155F30F6FEBA6F6B087E621AA945B2472137DCA3951DAAB45C7F324F2412445A8D221F6833C169AF1CFF9CB75E8F02A80B05BB31AC22A0CF6AC6AC0B8F8D4B22E4A31E2F826BDA35AF3EAF04107795FC0E6CD7036EG32FF0D5D48FE84593F31C3E35F7705A3327D5FD6227DE120BBFD9355851B5EFE852435EEC27B37CF571C4BDE2CF99376B7D521FD
	FF50BD086C3F5E24361F30C9781FD102F571AFD56CFA6B63BC39DC8F87AFE2773C1ED36238576358DD48A98F6C59CB1053122DEACE7DG0F0090C71C1AF71DDE7B38FD47BADE262773A68E7A70A079A01542FDDEA97AF03F078F2DAC2D947B061FF723DCFF40E2C86EE1C339691BD1EE799E144B814C3A19660E8D6502C96EB5128B83ECB849DDEFA8173985652E2AC639FC401CDB3A168BA139EDFBD1EEA0E071A4D73C5DC8AE3B8C65927721DC91E06EB2147B1B21DCA8495DC1F261000DA0B9BF3311DCDEB94A
	B56DC7399240CE16235C45EB74F283A0AD3A9C5BE55D4AE2CD9D6A5DDA9C41E1CE8DF208837DD95FA307CD4DC69CEA3EC78ED3D79529B9743AEFF81C6C37026E7EE47F688C23F698C3763FDF29315F0BB5D4427E7F9CC07B8920BB6787347F2047B9E7762DBF207D64A3EB5576FDDFF4A637614203E83F93F407117DA7B30C6C8FA77BB7D9B57E77E28D24307F8E599F893A73AB50FE698EA37B4E8A34DF542C713F575726EB8E21FDBF50BD106CA7FA3481E6BF1E6C4FD921315F6BEB736EDA34EF5A1AE2AA5A0A
	766F7668E3D87DF7EF456B40BD3527F79DF8FE1D667C75E27D3B60BF53027C7BG37F062FF1B267E0C58AA2E2D195B107FF91653639F3D006D0D536431825C67CB19FC9EDEA0BF720123F39B7AD158E4B4C68AA1BFCA6BCE4F0F3D8E4D982B576D781ABA3A66G37F8625FECB8464B5E0E753821C2B3466AF5BF363B1E2EDD205B3D9D6DCF100C46F821E47F5EDD4512E63CFE9A571294ABDD87C17F886270355D28AE6619719C9E311E5EB9FC5FD12C5445DE2F7194BEBCC2BE14G3F13E674E1CE13118F834CD8
	0EEF952ED7B874F64126DC174E37A1078850AF6DC08EC5069C8AF6A007F5DBD59CFA3B78D266302A81B9D400FEFF62D0EA48E190F1083229B874F6A1254C61CD6290897A13F6A2074D069C0AF7A20704AAD5FD68751AC84161CF0D48218A7477A58EFB2A759CD83B9ACC9C26FC2362506B750D0243CEBBF208867DE91548614525C69C4AAA11C3682A0D2A7A507B150AF44F64C09EB50C8371B8EEB63A0F9BDAC977CC0E5E5C47891BC348E68C681BDE05B6D79DB432E92E423175CFCDA8E785AC1864D69A5EF3C6
	115C3243A8B79C304CDDA87710210F553BD06ED512F38296C2F26FB49A498DA3395FBBD1AE9E306C5D08EFEDC63C9630205D28AFE936DE9FBB6711A4B72A8565E2814B5803F27F1AE2A4D7358767E777ACD74FDD0747D4FB1D3B366F9173ACA77768672E87FE4B662702G8F5D03F357CB16303D4DF06EFA272D5EEDE9E9F4323DE665FD541630BAF258F6DC0EBF3F0459654F12A55C87F0C93BAF5F3088875B024F07394C67A80847C3C82A06738921389A39D9F33A7DA6C7E9E7975F64887509D7EDB56E397943
	8EDF645CA4B754A739BEA437F527AFF241A47708CFF2E1A4373D52973941A4976913DCB4496D2872C5AE0E649E77C9CE5A0BF2153BFC11CBA739A81F6432C9EE57EEDF640AC86EA91F644CA437E70FAFF2D6120B76C92E136476D67BA25717641E75C92E9F496D5F6B0B5CG129B6613DC84499D58670B5CB012FB41A739F812BB345F97B953BE140B71C9EEBA497D3D4697399C12ABBC600BDCB1495D6D13DC9549959D74C54ECEF27778A4F712641A8E79A297C0F253EAFD118BA139A01F6486125C8C0BAFF2C3
	C96E1ABADF64E2C8EEDE3DAFF289A4976213DC52FE14DB79A34AE56C97E3163C7D4A7E67A5703BDA95E7C1FCA74ADE4C46D2CEC8E3632071049F65F8DF95AE46DE1F70E7F1AC9E8AE9E9B5E85F61F15DF657E86D856862C26E18075C9CDDFC00CECE0C7B9B1DECFC431FB32A06580505740E283C962F4F8C75DA3C21B5E2FF79F8B81A8E60F34C3976A419CF46819CD77D104756CDD4C13C95C29F88A190A2A044C2C8BA08632B3F65982DA9500E2F3CADA8D0765D173F8162B19669D9992E75105317E176C12D1C
	70BB077CAEBE08677125DEF2F837DB8E1D069C1EA18E8144618A0FB1D24FB8DC6BA12767902163A06A485BF623BA9283F9938ED29DE9C01E290710E73947582AE7BC77F848656A62A5070C6A7C7C6C8C63BACF6F5F484F6A8FB32E2C0D569057D40FF1D94F384E74105317E920A157F53B3D35CFF9B6D8AE7B3F969B15FDA8688CBF04E5DF37955784C413BF113D7467858FB9FD5927559A5591D70B914FEC48DBD40BBCBF63BC597E6CD53548535FE3FC5AB31E17F94869F98E2CB5EACF26ED48A112B894F80CD9
	FB46E133071C1EC33E45084393D48E9596649057CB8E1FF44BA14C1043731C83DBCF91C19C7AF90C25FB4661968FB9BD071C3A2EB91454A107AA0F71F54FB854F94869B974B764F0ECBB1E0BF0621064B1666E99072FBC64749C326A0DB83CC99C0A6A114320DEF2F830DB8E7D8CB95CE9C68E8309C32B473DC04FB87C6CA1276F93B32CC6FDC855CC23BEA49F7216D9318F39EF8D5619DAAB724C72387768994F659EF2FA1E032DC6E5F52299B98CA38E513D6470DA379C0AEDC69CEE1E019C2AED48212F47BDD1
	4FB8DC65A12767B0481043A8B3F208A28E459E77C9BD63304DC3CE4F21204108C36AEC64E0EEC08E893D641068F12F66B156B664F0F287F298CA9C7AFB686A9907BB3A651057E844E18D350B32C664D063F1AF58B38ECD9EF2FA8E21069C5E277EFCB0F14874382F6C9907DC8FB90FB13B5DE8DCB529125D1F980C2B449390627AC6860E190A6D48B53617DC5F6F16EB275D283C0EB451581EB87477381F6EE13D711073985B9BF288217B0B9862D063F10F5D43FA63A12767B05DE14421C4C28EF98E6410E66D9D
	07B99EF2FA8EF6C38E3B6DD89F7C08C3D4AFB93C5CAD0701069CF6B5A107C862504756BB8E3F731053F3C8EAB26250CE9CB21BE84E2C21F79C3E771053F328B664F012B834920778DEF2783CDB8E7D8DB97CC49C4209C3C8E36FB85C66A12767B0421043EF2AE92D77E164D0ED6F9D07868FB9BD073243C69C3E4AC38E3544A155514BFBE58F39DCDDBC4810C318997B07B06270E8AFB9BC5FAD0761069C1A79FB85FC1D2D93B9DC5054BB8E17FA4869B994B80DB8B81B100319B82CBE5CBB8E259EF2FA8EFD8CB9
	A46EC08E7D0843DB3D6470F1379C228DB94C37A307F86270E7E76FB85C6CA12767105DECA4D7595CBBFB968FB93D3D62E6A31F574FC61F2B1B5167F1AD3D63B055C3CE4FA1401043A7D310C37FE61C873EF89943E2A09E5F0CF33EE95F327BC8FC5EEFF6B9DA587712C45ABD5F327744DDED0EC6C78BE277F12C5E5546BFE0A63EF73A113F63A5352418B2DAD06FE7395E3F37E6DAEA7C3D351425465FDBCBDB5A75B7DE27AF7576BDB6416FDF44AF8F02406AF6A0D6C258AB04D593E65D09181330D78973A32C21
	9231C042DEA72CBFE176AA4486917686E11104B56DC2AC163037889BC118F3B7E229AE445EA6AC133016BD08659376AEE1E50435D6A3D6C358FB043592563697B1FF42BEA0AC08B077BE44C2897B1030F0420E6EC7AC0A300F889BCED8E48DE2A3895BF1G3134D6449EA7AC0B304A0308959096C5D885E1679DC22C16305CDA445C045DCAD89F4272AC0885937677BA444288AB2CC7ECB0E1F79396CDD885FF07AC8E62C9C7900BF1B2AC8362F9045D435FF92D023813309778BB2F81906FFF847B2116E23646E6
	9044E3A0185A200EC0A8EE437413BCBDC2DEBF1D0D6F9815E23AD45C55B70429DFEC93FD125A06373C0C0B364F1B51A39B783DDB7C4E2851B7C6B1283FEDBC1B4707FCB50977A96D6D36F67ED00B3D41A7273D6EE65FA175789E63B6CB33CD427D427A6DC81673143ADD1B16CBF8CF7B41A2B6AFE6F0CF4BDF8C94FDF3788C1C3F2BE94377825D5B583348F008C7C308277331D0B396CCEA5716E3864473F4187C5D6BBC6F7DEFCD1EF17FDB1747B8353BBA5A6AED7ABE5611B71D521427FE6C8BF52CD8647DC738
	3C5B5256E87368230FF3F91166CD7E7CBCEF7D38A8F36BCEF5192F207A32CCD3C6A53A72280638D30789FD53AB557A72C95F8B9AFDFEBA59C00877B754B7D4230F6D7FA70F019C788CEBD0BB1E6FABB35879CE00F82A9B7AA8B71DEF4DBDD011DBEB478CF12B5B4836DFD50A4A7632E5684BBC4D382D93E47B2A647B416F8190A2D4185037E917C65FF21A0BA9C6DF0639511775854C17748E08C3A869C0DF9A372AED57F4889B2DF0740790546159A7885B63F6EBEC2FC05BB734206D508E34FD75F6E6BB9662A3
	A1249FC55B095B74FDC88E24959F95B62B0EFA6F0F6C3A3413908FB8EAD466F7EBF966E319A7FB588E817901320E21DD580E5125A5C0BC691811ED579E0D6D15E8BB5A639B6F99A01FA76BA8B9665DF635AE4D89F1BFC35B732AB536D751DC0A076DC0106FAF6B98A49FA38DB8446A309190CF6DB4327D345676EA6F36B3C1BEDF56D1A69FEBBABDED376AB0FF0887995A3EF82F46F6815ABE39DDEFBB946443E59DD186B6B18C57250D04F85AF1A35B6B77E9EC2F21358A9E3633C03EC056D1F15C3B6DDADD1A9B
	62FD8CEDFF20314DBE46A4BF77B26BED8703FC182CE3F0973623F5E9F19017CE98593EE13F4676B26C8B7A4E443EA07D84768597713559959037C2B8F9827B028756F854CBC86BAF5B9BF44218E7A460313A349190CF7D5108677E9A8D4F65D8C6271ABD6AA548674BBA4AFEB436DD83F82BAE4D9F62C10636A79D5058DEC1F3D9B3BC6AA548074BBA223C589E8E78C8DDDA5AC938C61DB4327DAF2DED6A8347FA540D82102F10F5541EB4366D863C0FAEAD9862E106360F9D545826BEF072EC3D6D41A09FAD6B08
	73E2DB7A892E9FBFE95332A1DE7413116DA507B436298FBCEF075E3699642D320E4E1F0CED77853C1FAEEDG44A38CED3F2835CDFD60BA0FFABE8C6463E59D261F0DEDCF87BCC717D68C712A1F0DECDFDE2B31CDFD60C79E7D2F9D64CF4ABA823C588E81FC20AEEDA844E38CED6F312847D76CDBEC4276BB996AF1C602AC1BF4CA9CB364E35EA97D98E7D61DDA5FCA52F7D4B367D7F2CA4B259A624ED30667C523EFB5693BD1BB1E544986C23C3F213E4B6B557A8AC8DF392456B7C8A79B8971D8C3FDDB2CEAFDEB
	C85FFE4EEF04AC136AA70E1972B15F4F5BB8ED3CCDF3CD226B42BEDEFEE532DC0DFCEC150F7EDE757DD323EFB9DD63BA393EA0D9AED4BE064B47A82F7A5A9B547AD6903F73383E61325CC87918F61EB8E61D67CD5F42C60DBE9AE31F4F759548F2957231D6BE3A3D6AFBCE2B0F2E991773B5BAFDE439E07998A69F87FB5577DB3BC69FDD87E665B1FD5132DC1CFC144E974774733D695B645068233E7DF6333A7EE54B72C572516CD55F58A60DBE6A2F5BB4EBG2CBA79CE2F7A6E526A23BE7808267D7655497773
	2A2F6530C69F752B63B46BFA876864A33C6A1B6B5468233EF23126BF9826130F772A6FA92DBE5A3B7C22E6B51ECF753CE4365A0E69822D5E6997F833332EC5230F6A7997D3547AF2F47245867A44FDFD40B2366E3A136201BC4E3ED1A866CCEC9BF8BB61F38325ADD6F992DA6C5B36174F5D5EE312A4765E3740C670E7CC07C1F88DE147795C6D0C31EA6C930E4D866C8242FE64F33733GFB0C3011BBB19FA3A6301F785C6D4DE0F7FBA2E21FF36CB148735EE844CE71395B2BA13E1E74C9FC1E76DB107DBD69BB
	0FE315E0638F14AF114F53AE83EC9E653B00E3DBG3B18B0931F27F5013EF94465C20EDD87582B14EFECB56ADBC2B6FE45313FC3D92DA459A4BECF7BE7D6AE047D1AE3178236163094BECFFBA3E0C3883B18E3AF013EA788CB657334BFC13EA9D4AE176CC7FD0D04CD64F337378267BF126C6B9C7B8872CCA5BF6CFC6E76BB30F1ADE1EFF02C9C30C304B571395B37C15FF7646FDB9CDBB4CA6C23AF30A0BE1F3B8938BCCF3225FC6EF69C681BC0587D9C7B83683B1330ADFCBEF79A64F3103EC8BE1F3B9530AB49
	379DFC6E76EE10FD123047B9766DE8D1DFC45B2A7F114D113536599AECEDEDB6EB2563841BD36CFB814BFF124F75D6416F5A8B70DD5C0F1C6CF90B53F25C5581ED22481F65F3B6635A3BC848B71C49821E65AF566009B67557E36C5E28214D526CE8E92CEC3555CB53AFE432D13476579F7287C19888A19A4288FF3479210B49022023392319B3947C7E554A7855029E670518775FBF3239D2B13B29FCF9E4D73B25D119AB9DFA62AB4AD36132D8351964391403E986FA64EF280D1AECA4CF7A7FB6C711651FC866
	1AC51F75EF4DBDD090708E3DD0692742AF746C33781842G9F8E58C89DAE5EAF114C6C3EB36DFF447BA5EB89DF4071G95AE5EBBB94167FAD9BC6DD7A92638BEB8DF3F0EBFAB0D38884E47C5745CE6994E6B4791F6D5A3430AAE0678450815F1D9AB445D046512EC9F423670FC51900FA36CC30EC9FD619C77C5EC63F2144DA2AC065BF5C33CCFDF3C37DECC4F6803818B03B0382FD2C65170BB2E2FD1192D1F4D4AC03AC4144DD442F7F2BCDF050BB27BA31FAFE5F1BB24257F064A0C3F4B908E71C8425EACC26E
	31045DF60AE1793FCDB1157D161E4170F78BEAA06EA4ECE7BE437CA09EC458357928AF74375A729E897134G44AE63D896448B8230FC5EA9467229G2C9602BBC0A90FBE70BBB84028FC6E64F32AE181229CA690FEBF4723D438A81FD20E337888C833DE0A1CFE5E0ABEC69236F5AD43E2A11EC058ED9C13AECBB1E5DE065867DB516F7C4B501FD0FA9E6D8FD89004504BD46D89FEC7DDE6644FB25E9606DFA6F8CFA6FCA34753AFD7F0614F8DFCDE17450BA1ED686548A90B3F93D09D8867AC101E29F24C8F6281
	04BD4C31E0088FA06CDA7E5CA582625104EDF2D17BA22C114FAD95DD817740D7A036046C5A89F3115D1304C5125DBE0485115DE04236135DB0425612DD69F708FD32956D2693360E6CE69376E7321BCF58A132DBC6D881592DA16C8D329BCA18097C8DA72C0CE3D1900FA56CE6329BCFD89FEE57F4A55425AB910BA7AEB904EDA6AE45046DA1AE81D7FAF28939D24BE5A044A32E746492F525164BF0081B02BC392485F9F2498A726462A66CCF5B11CB9F425E66D8B044C3895BE6C6AE8389BBFF965A9DCA5850A3
	E8B70630D2321B77FB44A61B51EE89E13B4CE8371A30BB482E15302999E83713300DE437AFE133B2506EB042DEA03B7104AD5C01F6CDD7D1B96FC03BE904ADA43BD904DDCDF68B880BA33BFD2E42FE665219F4DD81AC8C42602BD45795789DF715D1BF73B91F83167EA07A13F10427707DB9262BF0514F84707CFCBDAB24857E015A84DF23BD8262294108FDDB4830CC086791F6D9A17ADDC4589B3C9E1AA16EA4ACFAA616059FE1FE2DD89601042D1F05E591CB58554D48E584E12FF2AC750FA9260CBFD29DEEC1
	AE59045D1709DC0A887BD595F2B193F6559C64E2A56C79BA64B20030289948A5023059B3114BB0424E1F09DC0693163595390CA44C223445BFA1B6D7E90B04BDB4035AA2E1593CEE7601F8B0E1CE0E05C1BC1C304728CD8CA56CD0B6F209A1CC3C5B4338A490D6329639A47D195634ECC5AE55042D180DDC1C049D1B0D6D440F30E80E7505F888E1C336A3170104FD2A749504BD237495046D62EDA72F9F74717D68BA378339549076BA3559D642447E5A0C4BC9425E4BC0AE8104E5D6A21790429EDE0FDC869216
	378339A4DD0DD86D9A6412C1580DEB10CB9EE1938B11CB91E1179CC6AEE642FEDA00DC2C046DEBC7AE1D043DB69B39C490B603374BE1900FA72C0EE326EB607AFA8DE28FECC3AE19049D1C01DC72890B67794AA0DECD180D6A0B13B0712E8563B20830B133E9DCC658AD9DFCDC86718442269CC5FD5235D46FF99918A16EA66C923E5E348F440388CBDFC963D6423E9463D6082705A096AE462D904FA76CA00D7F4B889B4C477FC1908FA52C163F8F908E71C8C5DF850D3F8973DF4A475F57013E6B90EB63F6EBA0
	6EA6CC3CC36D8771G42FE350AE1A1909FC05870D5E8A31CB0710EC65A75507FDDCF6D0DE37990AFA16C32557C1A86F1BBE19F2DC6FDEE42FE4BE587C3FC98E17F6158F0080FA06CB3AE1BFA8354719B28DE95203EEC420238ECA74483887B02E341908FA5EC8C178D07F8A4E1CD24EF98E13F6732B97D41377E08C9224DC03C1630E9DC568DF1FF42CE123EG427E42670AE2A0BE023049FCDE48F4A3340F9B29BEFF4530EC08979036E79A6AABA1ECB9DF0F9E8871D042FE4F3101900FA06C63D45EAEA19EC758
	418928EF84E1C34D58E64A7EC2FD53BA0EC15CC958056B28BFA06C5787D1B6023089CB28CC896B530479F2EE22BAC9ED211830B9BBB0DFGE1FFD90479C2885BE047FC8904D551FDF9D2A8E27B29BF28A6AC619475A504FD150E798691366B84660B84AC73E668AB24D36CF111BCBF7224B3930EAD36E31934EDDC09235956526EF035E4672F484F494CDF31E61158D72C5FB1753EE285A9873D6EABE63DD9FE26A99F1DEF4D1347D401FC2C45E442A0ED784DB8A7352CD6734EBFA3D2D838254DE15DE8E92C60B3
	DC62597EB0BE36E172193760337D943ECFDAB544DBA174398577C16BDC005F29883D7E9A29422FADF102815F182A3D097DEE111F1F48C7368FDA662F71BB951F3420CD69D628573732792EEC17FA8FB6D6162E51E27D35C85F27C9E7772A0BA4F5FADFCDBADFF327117F07A6FD0CCADE787650FC74ED64C3E1127376DAC35FD8703BD57894A29F253B66C9D115C963C5DD78F321D2971EDE5F133AA00F9978B5EF98680C3F1546C3258CCB8F03FEAD8C31FFEDE6D885442D041D627539936201043D378D6BE9FF42
	9279B8E78444537E0AE37F31E63686BD8B62E5906C907CA104C088079083C13A8D6AB904A288B590CEC26887E19004A888F190CCFFCBB1CD0710832198C2B504D688FDA104C20800908BC19AG3AA0948FC07BB95CBE7B6E0E95B07F81625BBAA1834437F3868F905FD20A9DA03E11E33ADDFC8307FD4FACDB7E8ED996E0A5902C907CA1045CAE3E2B443E19443EF3D4A5FF1F28C67E2E50605B4577F104FD3FED112344F9E53C78B32B5B84BF761BF1E6C766333E9E30B20B781B711CE611C17E5E06D0839B6CBC
	296355F29ED63EB5BD344D4EABBB3F6CBCC5F91171176BC1DC97BAB365735F931B2CEE14495C8799688C11537A4A7A30CE79DAE672BA89BA37EBA4659959DA7977CA79B843C01E355F95DEF4CF7DAF1DD79616F9612E0EAF110FEC0D43349E6AE57DA3DB137CAD042F3D64E17D9E6B633E69C28F7B36523C9E5AE47D69987977EC03F4DCC33ED4BE4E07303097E5A67A56DDF6761D4586D70BDBBCAFB94C2F7D31503663A1D45D411F5BADAB69260E0C1275716F7B00CCBE0429329FE5707B03AF3E7862B3884266
	DF79185CE6F5385907A0055D880E317460BB70B94DA19D378E4EAD9452875E015CCA3B65B6DA95066B380D7460F6506E49CD58655FC73A933925BA345C6672755253A1BD6BCE6436F7F9F75C462838D55D2965E63FD34F6DA303F21BC85C5CF752780DF3E16FDF7893B6162F018E02F8A8E17770772DA2A19ECBD851D49A678DC4ECA3FFF7033D13D1BD907B7B525959F0F4C23CAF047E90A2A1A4C0C87DBBDCD3A014C030C2707FBB3DDB4AE5C2A0BE786F62DBE86C3BE931705BF4177846A57B6E5EF079FBF4C2
	665A0CFE2AB2334A3A3D15A963430E118315BC7D655F29AAB946533F8BBD6A407CAB69A62F607A0806ABEB73133AE9FF58FF687B2C89AA0C35771E7615E3A451E775205DB7685ABDFFA6158567A16FAE2C3F233E6B2A7EA66B5AD66DDD5A7A6B3ECB3495257E8EB965D9FFE3CEE17D753B8B5B5695DB355C6A797DE1G2487D39EC7BEDB6FE1F1BA9ADB589769766273687378F32F2110AF067C38AB3FABBFC64B650DFDC56A5D0A9FFCBC8F717570638B7263101D653535B0AF2C325D23F69C0B675C0D9CEFB468
	CB42293D965FCD774E9B303D1A899B41671B2C906FA46C52057C3E9B62E1049DDD0C6DB50A308B8EE3FB4D9AC47BCB9630E7688590B7C330C37007908CE1A065994A738C163F77B9D87E0EA87B66A87BDE2C4873C3C12DDCEECC073A9C8B86F93691FF83EC204CA158A0CD5B1E9A48167EDABE5AC04668DD2A89F2DD9C6F458EEF5F7C3C5C44673B2200D34CA0BCFF56352C9E3AED1DEEF1CFF29F9B72486DE748DAF69FD46FEAEEF535C07D5B5E66007F7C0309C25FABFCCEAD6D9E98E35F0375F06A362E6A6198
	C9FBBD483FC3DB8F2BC08F3B66FF8475D03E07E941FAD3FB8F4DD54C44FA63A6EC0B8D6BCD90E177B6E03D99C4D844F7D8EFD27FC16FFF1BB3D99B00F89104EA88EE888190C2A1C4C008253CE5BCEF9F79BB3A6C1B3B6C7B3C127CFD60D6793B3E72FB515B7D6098A16BE2BA54E591AA472BE53BBD697BB23B4967B6C897DCA2B917C9F2BD51771F93D4F947740007377A59DDDD1557317079226696DB332B45D1AF6A52D6FE8FFA18D53EFFE25DBC35CDDBB75FAFD26A66F0B3DF8BE369542D05798F571386BAB2
	C8CF6082EDDFBB145FBF17C1FA55BF315E6EF379B2465257DB7FFF0A67D79FD37FF971D66335BA813C6E86C07EE088822B621C1BD9DD79A74E93CDE51FB713E5BE6669511096CBB277F29F5242414FF0742121DB9F3AEAFB95610A8F420615F32D865C9A0EF667F1AE6C5D4400F064DA30DE619A41537BC1DAA84908E73D51900FA52E5B3B642A3F6E6A39265F2B677AA51F2F4884BC67DE347BAD67F20479F0AF0D37F5F5EAFF31427DA91E5F8EF95D147F4FB3D85B909FD3E15B2FDADA2C4C8E170D5E20484E60
	E5558F64C26FC59F8FF46BE3D767E318070FFE5C46F04093F4BE32B5F01977F97371530DFA9F73A0EF51FDBD71118FABB4BE5AC14EFDDF4FFC6C2E3F8F394F5847B040875D07BEBE401F43B5B25F09F3789A2D0FFBCA951E47F87FB282721A7E05BAEE6275A38F62C57FC26E2F9C690EFBD7F55079AFBD77964E3D93F0FF32DB483930B5F7E17F4276F2E2334275C51E9E8EE9C3C9E69DFF56B782622641F41F55A557515D14F37EE0BD5792EE2398700A41E8B7154F1D57B39F86A357BFEED1387ED5BC3F0434C0
	12B1F3BD61909FCADC87F64B352BF29D6941F5AA1F33CC7AB75CCF7D9B2F637DE762382018301B79F353AA085B894B67E3513E906FC7585F9CB8AE98CC583DD3F1DC10FEBFDE3FCDFC3EBC9B62A5902C0427F11CFD4FBEFC31784E3B3CDF1433A76F47CF14F562EF2B6AF7497D5D0D97EF6D310D51BAD9F53D6ECAE60A74568C4F6B73177CB9702B907C6E47B6D8D228ED0361E5CADD391B678F003C4114BF643077B536DF0AE77C10B76AFE2CDB23F81B159E00315A83D8372EECBD1DF6D074003EEE59390DB226
	7F81347BA0FF4E7192737581641EBFD36B6BAB650A2FAEBEC68D003C412463AD3E3EA38262D10405097DC4A2401F8874672FF3FA5AD6469879932177E7ACDF5BDC4674C7205DC45ED65EE27EC4E03B5E7503427FA6B1A60134E012594A6F95A2A09EC558C53CDEC8C3007F90643F265B7E3FCB7EC3747C9344580C699F02E5FFEC0DE7FD5C417B4D8F19CF146F8233769CF5D6A8BECA3C6F8A003C41C3E8DC42571AC4C0BC0A30A9DC2F74A07878A07A38FF6D69DCA70A9E547BF8ADAF5BB2267FC134DB437B3807
	19BF8F62B97A43D6095A647CC263716EA83EBEA8GE402C9577B22ADC1BC0A30AC5ECFCB8F01DF8F21DF7127F5CDA982BDD3A4751877335946E35E1F396DB2E677A164B3141F0FFFB3FF9FC2FF7F31CDB9DFC7F8DF9EGE941A4B31317D184442388AB97FB798C853F0622DF316BCF2BCD011EB51A733517F3ADE37A07225D5F70BA79A863BF94793F34DD617F1D687FA0AD18E46E61F39691900FA26CAFFC4D0874B070FF98799FE9BF1D6BE7514362392E6A7A496BC899537FB036153CED1EEDEA8B471EE1BED1
	3E633A3124E9076223D8E793GF9039FC6FFFEA5560DC1BC0AF40C5B2E3D7F1A4D6B277488787C887AFC324BB6562D4F0F68FD1EA556GB17D0FA037DCDEFF7CD87C91FACF22C36BDF68CEC6CDF0DAFC34BBCE63A4655ED04FE99058E04FCBBE227631FE81FB5F426EEAF31C003230B859167AE6D73B030D3B31CC77717A403FD9C89CC31A3465F797DF8B11G695223D8FECBD6F6F76F273E274AFBD4BB0FD97228FE9EFE11413CDEB14D6B199FC5EECF6B1EDF3C4D7354C23A1338F5F47B0CE03C0ADB300EDB18
	8737678C1E913CCF4FD6869137E312165BA5FCCD54D0C80FA6EECFD778F20F169429651691291F7F7D43D2CFEE37ADC5EE5911486D57F666335B65F2D6B9DAD9C5106F37F22AA4897B7C6BF4E51B48472D8520230474DC35CADBFF376FD26A6F972B3A73CD7DCCCBDFFF5D11E24FB8E5CE38CF13B167BF6DD6B83F2B2B2BE3F81F67873A7A92679BD7EBB95F3BC761BC3160F4668586F9F03E5E8B6758EA05730443DA4E73C4BF8D3A2208F3FA01167314FD8A67635DF26E6ABAEAFACC4F79D6AF1C5756A81C3F52
	F14E611C53C0D746E348B9C3477960810573F95DF2CE6E02F3058767B0AF1C4FBB28F01E26637CAD67DC8D3A2C44B9D747790DC38A674B3B65BC3E8B4E019E1C6F2DB6661CEAD1B8E76F55F2DE47575D7583DD2144B9F91A167352BA05F34B342EB82B799A4E81F9F0AE4FB3663C33DE619C162765FCBFDF5BB79CF4A590673489DA4E9DD605F347046EB8F7D51F339ED7B88B7BE23EB78F702247517E6AF5DA7BC1B645FE703A6E2E3B6AFE4B793836CF760378810D7D5B789A433E0087BD0E7DFE5692DF469B91
	BA9B43BCEC7CD52CBF85BC01EC44F7F95D530F2F331E505AA8F8C2EFE38B9F0716GEEFE82ED5C3B28BB9B6AF66C27339168E12309DF3F7B819ECAB68EF7397EE0342E9E8F5359087730614F47E1A381CF0AC29B63A5DF6E318A2234B6AAA05E2C31311E2F472C865CCAB61CA7FC192B8C5459682F32A15681BE3ADF3DA6B36D2A3244D11271BA4081D1725E98D14A9AC731776BC57C99D58260E9CF629E2D038B58BA1E5D16A679CBDF25B01AF03571CFFD81E8EEF3355A5A5C47853A1BFF61D54C8DDD6531AFEE
	F7C148756F255CCBBE4B09F7FEFE66EBC6731E94774523887F95470BD438D8371A56026BD64BA0AD74C95CB7776EC676ACB76D2994D38E049288B590CEC288FA8A4BF2421ABC291BEF27D539DAABECC7EDCE559636723E8A9EFB7CF697C43D38F79C2E8F9DF057DF24017F1319B85ECB3D88FDCA54072172B1E860BC0E8B7F36B918FFA3A1ED7A5350F7C22802E0076077B47A37EBCD0E700FD5078DEE37255E4E568A3423F33B9D36E38A4CDEB8977D6F67ECCB86294E5546B703946F2FCF25EF4784027ED08843
	A10CFCDA6CB7AA647617FA3E775ED60AF26951623D779CB896C72B651E5DECBCFF71BEDF23DA8DF92D511877F7333D4F935E4A7B0EG489B9C0D6DBAE9FBCF7AD9EC5B91A0B7ACDA34EB960F0FD66A1B30BFE31671BC46B2BED7B89272A7BD03DC2F59465F33272F23D51A4BCB746F41EF5C425F132F3054591CC66977EFCC2374AD255BB56F5877DB1E2E512FFF873FFD12A2DB59D127C99B152F1555275F5B26705AEC33F93C3F1FB06ECB16AE3E7A2149A00F2DA7AB3677B9DAB53ADFDC24352DCF7FA6D1313D
	2B5D5626377D528CED399A657970902BA774B5C6D63414F6D7FB2ABA8D0AD5607F592741ABF7ED94786C432C6DB2951C926979003F4F82629C96D8A51F5B81D19576A52F177B4A4DAA2C5F9226F3D7E569CE2D4E07D2B49CC0C86073A6E87CE102A4736D78494A39E0B4459768A93DECD71A128EF4C532C8BBEF32AA8DE82B53C6DBD4E940C41DB666044AA6E32337D9BDC529FB36B6074509E7C8AE937BB4ED7D5364113F433EC33D56BE7F9979FB704F607756C7369925BBA95DE1B7CA7723F4D307D1FAA0257F
	E601D1FAFFCABF54E014BE0852579A5A0F247462A62374D8CADFE018BE0252B79A2627BE0B69AB8D53B3A93D59B0BD7FD91C330CEB1168FC56AD1BAB695EC1588CB79159F32DF05ADC0DF3F3B273F2B21765AD926B0459396773F87CAFFE3DA7064F10A735325F625A78D2AADE9BB3EE3AD6AAF9BAB3F11EE4FCEDACFBD63EA6BE2B0C19C6A6FFAE773BAFB6727B07368E1B30313ADD71E36EE979A1746DF3AB7A7A5A35366EB82A242DDAFE7A361EB926681BCF366471B81F730904B29876ACDEF37EF45C735AFD
	7BF13C86A7BDA72E5D19F04CFFCE6C93AE642226F8DE3B5F1A02F2954F09EB37950E1D321C383ED6CE6D49BCA8DEDF83C0B664B9657ABA70B9E5CF8CF99DA6DFEBBB98704867105B2B594A75403CF37B96FDDFDFBEF1A225D736BA9DADEE663BC81BD34B46CB2AFEC6CE576B785E253D2640C83AD11FA7444E6AA9E3206D33FFBA4578D71AAB4A37D5697B7343FBB47DB69326340B17CF16B457A8210052EFCE65FD285CB7F362F2AFAB52570DD57541CCB3754122FDDFB6DB5F3E59F99B799C367F69BF9A25278D
	4374A7B20C5233A83DF80ED1FA41B06CBFD61ED06A6E8BC5275F968E1CD274FDF4CA5B6E580EDF1836B93F7736D0C7D0FEA69CC9FE93AEF44C2D5AEEBE342122FC4B362D255B2A3CF3ED9B2D683A444E6AFF0345592E6C2F74342EAD6772B53095D0F65543305ECFAA74EC4B8B8AE9AF28E122AD8743B18C825B7FDF4875DF6859167F399065A20609369C834784D9CE3465CB967BB26F117ABC1C676715361C7F3C722DEE61D3A81F43AB863C62F964B633C20CEF642F705258EB40968DCEE32FB7E6CDD0631BCB
	CB15F15C23BFEAE4E82C7451C90D0CE62C745A0EC99ABBFC58A1DF0B9E99231C338DEBFBDA07046CFBE39559782DCB8C6B0EEA11ECEEC569662AC3D55B8F6DAC5FD2D6A56B605392F2594571F5831DD0EE7E9044B93C0F6E41421E9767BE820E431E975FFA90F9DAED18A761F9F11ED3DF0073740248A34EF3F4C3CF6605304FAEG590A971473DC7B02F2CFA4383E47574DBA816FFC814F73095D1AF266DBFCAD134BA72D43B3ED391CB6A549B3ED051C764DF24F34FCB9EDCD36E75ACAB9AD65A74F34D5B89EC8
	75CCDBAD275D3858B32DC0CE33EEE34705AB974A67EEF5322A6F18B1C7DFF71477B5D7C33DD93E1C6A4D6563953955AE5F6A5C9DFC5B94F91CB0C151933635A776656B1370E3957AB1F2222A5F72410FDFCDD664B6796847DF3ED47CF8F80A2267148FFE948ABFD6239F71690A1EAA9F7C2818264865786807EB3A6247E5B394BD3F76410F9261C7817AF17BCCC54FCFBE7891BBC711AB74510FF1190A9FAB3FD1745C683B9F7968C7755C5E79B1A02B77FEBCBDDF716313853D73638DFEFDDA38827D602FF049E9
	47E6755C0F630B953970A33E79913CC471631FB93D2BD7BB7835F161F2746365EF95BDA999BD77E3DD1EA2374AC7BFAC4B94BF7C17ABFA66ED6F399F73F6F0BF16219FB72CD074E478604768D50A5C95BE7A31E03562C739EA4F065F75488FF14DFC0C2F2B8B016BE598DDB3876F749C6FBD37932F6743DE90577C84B8A63DA83E75A464FE30FB0E776A6CA81779229887944031C216936300879D3EBCD73102EC670B4AB8206F0B7A715E1BFC1DEC9060A1AFA23777FE560C45B477BF35BBA62B53BC6EFF7EB1DF
	A3DB31FD03B276DBD4486BC19B5683679A65BC3C302D0B73D030DCD38FFE2B5AE7616119BD170B34ABF26ED65F6A4F878E257EFCF5D85173272E5AB3591767C01B397F2D687F9A2722674E4C1E7BD1532C481D2C6A395CC017A2771BB93E797FDC2B62FFF21B2227E5CD4F7D1FB1137B5F017E672866F8E674289C657BC655DC4E6E2E2ECBBA391BD473BCDF79F8DD9AF5DC71BFCBF50FBC510773EF977D798974FF4B0F0A1E2A6C1E7B51F4D211FB312B7ECBA7F77F4F0ADC010F77CAEF1CD27CCF9D2568595B23
	7297F95BE7F37F9B507F252A7B7FFFF465474A959ABFF62A6E53633B3A1E6B64EED15DABBE6C63756331310A7F896394BDB7770837487B0A388E56217F53D4779F2FF5F59D5479D1182248CD2D6C395C2B290A5C3D6BFD73BFE922627F12C90A1E01BD62AD72DE37067B5F087EEFCBD3741CFF3867FE9CD15D3734AD68391C7ABEE5DB3BEF7E0BFB93F92D58D70A1E6C9DBD777F2098DFBB517FD52A7B164A2E5A114E0F3D539539D73B3AEE6A6442D577A5FBFDEC7F2F4CD27C9FBBDB51F358875E0D2A7B12EBBA
	FC33FF116ADE644882B607277A6CE3EEA57B3F4955526E3634385B79C40DAC771A6ADEA4E965694FE7A62B6EC976681EA358D47799BB7F8B4F917AACD274A56BEC45AED15286D41C3E2DF12AFB0BA1CB3536DCCB15349FD7FDE3ECEB831B1483D34BE4D3F8CF1D27541BF7D4778F7F335AB7BD137395BD2BD5637777FC5433EF0D22678E553B719F7B286719A2C5CF71FAC54F78825F745497ABFA7E39D151735AD73E69F9F91322E75DE6C54F5B293E6931ECD1745CD32E68C9DFF77AF5AC66FBC5F7116ADD18D1
	CBCEDF7721ED0A6E3B4D0A6E077F8B6D70950B227BFB2B227BCF0BCEDFB77F542D2CFBC823223B6C3F704CB34E2E68AED35D338CBFF57A3A8FBB955D8F281EB9171FB8FD5D9F727B9431E66A9AFE5FA156C68D333149A143F5F49B6A5C5F033D6DF5CD36FAF7152511E4FE5B4086AE742DDB313CCECE7B9DDF8B60E8F138011B63841F642F040CAD0DE23D18BF77CB7C3EC4757B2B161E3E33591BA0ECACBD23B664F5090D5E3FDD7CD02371370B9F63386737631FEE7476CDE1117EA237555ED1572A2C4AC3DB2F
	B53201396B987F543DFC5EEFBD424ECD640BA926B888E9B1A926AC8885B138CE4EEDE7758CCE3B419A39D2EB238D8783E8FF0F433B2F879DDFA93EB229FA28A750BD56B9ED6DA85F483F51D567F23BDD4DCCBF624D8EF63E5C2ED68E4A5FE7F630B2EAF3B45A5DA22B0CF772E77FCEDB830745DA486FE6264AED278AFC6B9B03FBF85E1342AE1CFC8F4FFAD7F3EE3D2525851A112D5E1EDB4F97BE66B6F6B8F2BB5C8EE7E1E115436D34ED64B3D0E20F4D8BE762BBC703E3F0DF4FCBCB181D1190CFFF89B088427E
	D52538963384301390040E17797E138C9F70926AC85B4C7246C15C74F20AA963E5FCE63CD1F59F6F1E1D29EF7F2AFDAF173381E1FE7EA2A1972A3A7E3E362DCB39B5A017173728E76BD4309FF9D4F50DFE7FBB45561E9E59123FB35E6065BB63FC4900A82B75FC9DB7BB6779AFE3792DE8C74CC9D8BFBBE2A1042D6DC0EC98E17F666F0C31F93474D7907B9DDFD74C666DBA89937BBA733D7789BB560458F0428E9016990B58F1422A89F39296C0D8C8935A0D0845F53AB1FCBD3F74AA54818845902AA1B8A178C3
	8802908AA19CC2B404F8882657D2CC53A164C0A802E006E0FD8D5BF2D5AB9BFFF8DB9FADEFDC4B9BF41B05FF32CCBE27030A15F37AA92F87DD1D5355790B6475474F9F47B657899C42DEC3DF671F40F6B37CB55A8FF78A4B1B79BA74BD904C2FE33B19120E6D468ED83F57D1477EF4ECB7512F230EDF733CE943D3CC59900AA178D60FDF5C20546344CD0A4FA7B2FA623360B5E4B61E476A6148F57B0FD84F8288CB190DF5A00130E3F3904BF9835B7C7C9205475BEB3A6031BC9FAE782BD7E159EFAB443237022E
	60B728FE97E259C73D016576428216B775CD687BA1943D09E53FF59116FD95E0FD5FC49DD7AC42329F7CA66A78974FBB926269EF8176162FE5DFD62A147DE15538FAEEC1CFFC96F23F5922487DE6E6F767EC955553B7695AD0823CCF3E05FEAE1F05E5B560AD74B3504A7BE6081B6220DD46E1D98559302C7281F346210E989B16D5C89C6A185840728E03F882045437FDAD2B3F16A9E595253AF7705F5E931F055CC75FABF2576C6CC2EED941EAF53D9ADD09E515853CEB5EC6BFFF2844328AFC9B7DBC2F11659D
	8A71D888A35F46323A480EE5157ECE0A29629D5471B86D13677F8E6A109CAC6FA008C7C108FB4757321AD9211455F72AFB21E6E7CFFC967D661DC73C0FE97EF1247BB14D83C7443B2D0D362A632DB6018DBD42A6BADD8D8D6DB69ABBBDF6A40D3072F6FC43D90CD3B75177E33C85533B5ABDD0045C0EEE65666862C26EA2AE1779EE4F76D74B07FC62DD14B1FC1CD3762E7E9D15099C2FFAD77F0E4A15A5D897EAA1ED703B38A7CC3F525E3DDBF3CBAF65DE77D9CE78654F47EA5971FAFFAF61F8FE3C5E5F2FB623
	3F451096920F7E16E83E491A741E7C2DE77918874712770C4EEFC13BDA2EDA4E63140FFEF08CB4143BCE734D567EF21EC172B1920E3106F2459DEA3991F21E5477653546F04CFF5FC86E4185C677D9E53A3CB56FFB5637D69D668F71A08821AADC5878C3031ADB381C9EA59F0743F1A404348F74F247B4E511A5279748470A8FBCB98939131A6F72564A795C72310FD739BA0DDC301CAFCCBE8E76AA7733C6AEDA4E97A79F2584EFF25A6F87274B793265E311D7399BB5F2E6B91FD5BEF64231AF04FEAAF9510FA5
	2C735EDFFE3A2E7B7E52345E789EB0E53D67BD602475467700D3572B6F8185B6F3BD3BBEB542B8371A72FDCD58DE423238CE066DF1D85DF64297F3DE8CAF33B1030867F1DE47B8D6ADE3ABD558DE99ABDC4F7AFCF0596EEAC359756B599CF00343699471CDBC0F2385AE81D49EE51C23235522623D15303DF43EDEB95E3BEB44FB5D4A99DFA3961FE0F2838C6A0FD108C840EB444FFC2CBFAC4163BDC60E8FCF70F80FF18A76198910D67DA17619B352FB5757AF6C25DC2D4FF2422F4F78FD49208F757E0E1D4A70
	088F757E7E166EF922A02D60BF686FA31A6FDF877FC76EB76463E0B8C6C3087B0F3EFF2B7A51286F15BE521E37740FBC4FE3368EAB0238990275A33D0DC99AEE1DF2FADF79584FC0375C074E51FC0BDC4E97A19F07F9152BAA6CDDDD2F6BD64E382E7F014B459B70B134BD826B7A4E057CBBFDA37467FEBF4733C6684FFD6C82BC77F91096B8824FFD65225E55D9CBAF652E72D9CE7835FAB163BF42435FE20E1BBE567BBB046EB153A04D7DB17A9B5F4B716E673D9C6F2E1E456422BF6E59790D7B984FEF923F6F
	13BE517BBB1963291F687D3D4A0A7ECE0734131F203FFF32756EBC0576D26ED51F6504DF6773FB57988FFFAF66F81C073F698D686F88C82B79947DCD2E6C5D791D562D1C7179B5F33970CFFBF6FE23BE45737BA13F079D7E295E5F4FB91E70295E5FDFB5223F52C868A7C7223F81765E1D27BF75D26EE91F6504DFEEFE3F9DB1D26F6FCF9C0F9A2977F72C837D0D0134124FD2CCEC5142E8A9FC8A1B6FF234B89DAD36CDAE2BED0B2D8562E24F502B793CF9FF489B7E99FD4B3004E1D1909F7E991659F8BEA7A4FE
	272BFE7F31644CBF973A63AC5838E8336253652A5F991B4F3C6D15E754067C8DB87A26DC821DE7B1271674F90AE97A6708DD49B17EFDD4422EEEE3189B62FD885B5E0E7A02897B006F11988671F0422AE7233E2104BDF700E199DF2418723EC0EC90FD4B2E04308562DB0A903793F63172ADC5421E654F1886C3BC1A30BD9D28AF0E3005A5E2CEEABC7C9EAFE3FDCBD0DF86E1FB389FCE087B9196471FCF84C2BC04309D7CBB8FE1900FA26C7A05E8F7B8E1D9E9E8E3A4E13379FEC29523461BEAC7A15640B1B744
	83893B2D817575A76C6D958C9B847121046D246F48C793D64C77134BCE9CEFAACAC46C579CB3C33C15305F50F7B17D893B003E0B99C41872DD4C9042AE977B78C15CB41A4A0F1F2369904FA6EC901D37FC42447398FF088792F608676B8F7101048DA6594104DD41E553CD63CDD9A6449CBCDF81444B88FB04E42B88935FE18C0178G420AF93E8808C793F69D49469296AE3E79B9E63C29E48C55A1D1CFA1EEA76CBD251E9236C6FC73936251041D4F31B808270EC5EC83FFA613897182429E67672D8262359064
	E775D314FE6A92556F2F261C05E775E7414668290ACF292A5FD7241FF95B371CD19B621C3ECA5F65F452F9360B368871G429E36E3DD8AA66CE5BE279287F1E99CE2F71F40361DCE58D17E3CAB9B628504BD77A376DBA504ADBC09DC2A899B5804F61D044D665F229A8471C8429A26E03F95CB58826E5B8808A7A5A1E69A4D7BFD08679066980DB68A883B1CBFFB6B8371A042BE667AC2A1BE08300374DD718842EE634F5426A70FB765A593BF5EF717C03CBA995BCE75C225BED9D43F5FB41C9B7DAF576B05E75E
	46F70B951FB62BFEBF3A684C5B9EFEC6ED08F33A0D0FD32CF0BECF52F94E30B2ACG62A1F41EA75A945F2729FE77331E79F2787BD9305127C17169D2556F198DE75E760AB3EAC31C53C67EACB38C4EE7841D679F66B2EC98446369BCFF5A2878EE32AB3FAFE9BC7365F07DD930F1422178F4016AF72A634C5B1EFFC6ED486FD44CE4737CEE362C0B2D1D1470DD3DC8FEFFBA924EF3D24AF85C773BE48C661570F9F26DB609E47C793DCE8164AFA11972D9FCFDB76D55F9815F67DC3CDFF84B2C2E5EAFD46F2B4602
	479E43E003AD30CED6FC61FB6FE93FBB77A83F07E269FE10DFE01D7C9EAAG624144B3E536166775E60527EBF6F7BCC7F741F3A858D82F61797E81CF1E498711E7B471FC150FBF06C3BC01F8FE275B7FF8488E0567B52B3B6359655E2B63471BA6E9F83E677064195404BC7346A3CFBFFE4FD78C710A7148F3DD0916E71CEAEF74EBCB3A63B92E8B1EA74146928D4F168CCF1E97E6A04FBE44E974C2AD27C92A7D326D8B3B5FDF7D020538B6A308FCFF14E391900F02A07CDB1B561DFFDD6D0FECCA9DEFAA577817
	EF76746F87B37A17160A7E7DE41E2271E183FE7DD11639520C6BD3B2D3117FF37CBE378262552447142FAD27652AFD419DDDFEAB925F777566DB9F3031C1635B1EC6CF5FDAE8BF76C062542F31EB5F6EEAC45FC248373BF97B090478B0884217DD3B95DF3A5E63FC0C54F57F13BAE13CE9322A2ECE117E52441FE3525E49621EF5BA644B1A00FE34685AEF27EA7F72A73AEC3F5DFDAB244A03CF000E4F751CCFAD64F3921F23BABEFF28D678BC57AD1F2E7A13E08FBE21BABE77F1BE7DA15FG62632763730F3D8A
	1FD73B6553D53B0A756073E79D1F5BB91FF848B712786C5171F9C935F77B20EE79F45596F2A66A79BC2D6373B667D3G79CAA6A21FEBF5FCCCB58A1FA9DD6EF10E7B28F93D1E020DE92ABABFC5B2B7F9364DC66A7F7D08536F1B3AEE1BFFEB423699C0B255A70C479D2D7CDEFA90640B203CEFB4FA0EBB0A78FE74C2E6A23F9F9D8179CD13D06614DDDBCE078EAA6594EB6F2A1CC6CBDD3733FC30B1C3D277E19F2F77AC27FEA7301C0AA7E1FF94CBEBFE4D4473129F35BC4FBF2478E5BA09571B1324434C31D008
	8F1C04571BC71B3A73272BF6BA9C744C537813BF4560FAB3857DC9A0AE3F1D027E1852E8FCB2DBDB07AB797CC19E2497D11ED92335BEDFD72B1C9B3F51DD79229E97987962CC73681367687AE4DED7BAA11FBF71C91B236593E1D178F4F4790D2DF15D146DG8FBEC38A0D6B7DCBFC7EA49C728FA5DEBFAD702C77EF716F8308F45BACE3DDBF093D06C1CF52E45435426A29EB226A7D321FAB0DF57D0E5F8796001E9252D5B757D357E6553BE5B72B5EAD3B273D876F8466AB6F84A6B7A9328BE67976BEE10E6A3D
	33575A346F1576D16DE371FA06EFFAEFEAD1E4B38FEA750EBB22244D74713D4E452A3DAD3EEE526A3DD3352F672C925F74BE5B294886E720DE913FEAA15355457B4C1BCA65BDD6042E8D2AFDA326277946432A5ABB224E2C65F10B39676FD5072B7612F8E905EF9CDED16D89F9E1231643E30DBD6730DE35FF44B19F5FCB2E9B2D340DA12AFDA1FE70313E44297605A8D36D67D069231E43A90A1E87D47BB49C70D14F1B9394BD1BD57BAB647B2847B1D95173EF553E89FEBE6A992E5A536D67A66D39FE30296767
	3AFC1622677EC65F7A2F6BD47B955458FD639F215A136D4E93DA7E1F1E6889FF393FB869DB9B4FD26D15905A649B67AD2A3D926A26E8B9DFBF2567E5FEED1622E7C422EF9C6ED76D63B6B7DB517376EC5F7414AED274DC2D5A73E05F825F6AC0D22EA21BE675CD562A5ABFED7D5C1E4ACE63792FCEFDFF850C8D5F16463437C9DFFA3E533161B74FBC9DFA28A4BDD1CAAC17465A793A5CF2E94CB13640373395C6ACF7C2382D7D08447BDB91F3CBE3459E28702342E531E62E449F2B7047EA7CD160164671CDB0B3
	73914A7704D6600F6578E319DBCA42FD17A1BF5BDA394D526C16CCEC63FE37B4FADF399932320F8830E8CAE5C7DD3B58A595523614EE070CCC0DDB1AA632E44A7BB2E7620B41EEE975A627237EF0265B1559EEB9EA4BF45B9D6D1996B957D1761AB6CBF25BED196DB6272D5EED33E6BA59AB1739E0D66C197CFFAA4EBD95925472891A16716F2BD73EB674055D6D23253165528CB66897F557F142E6AD3138AD3BEDED2C9DEC1612DBDDD05B5867B94AA5D3BBBF0CE9F79FF75AAA2474061D36A39D36F6B039815F
	B0079AB43502D9DA4A5E02DEDAE6E93773CF742D2D10CCAD16E61BDB7AD2A4BBADAD0DCB45EB7DEBD90B12C6ED48E4EDC962D7DC60BB0573E5DAF2C94B143BA74D7F667B58012325444DD20A932A72E6CB3D5B55D6AEA53B6DD0A0F617535A593A3E18ABCABA464663FCDE81544DA277193339EC5212C89F6E7CE35F8943A7EC0601CBE71B341467E477B83914433A49EEE983D33636F28896B019C5E9674DF1AFF8F84B341AC4E9146CA46B9632BBDB6184B4D81A9DCEB881E3CB78BE8B3C4348F1835BB6CBCBFB
	032B2D9918E56A18E19299483DFCC85E95B7FE320F9BD0C45D525722A8C1F4E9234D553CF4C3C3032345C68A20DCC17DECD24F72646A72FC59F969546F1F7C8246DDE3CB24514DF9F97C7FB27EFFB97B3F0CA34BB832ECB994EE3C54651FBE3D93FD66158778CC62FC58694F5D8CE7507D6C5BC32EF92EA8BE8E3C5BAC0D61959ACEC21A12EBDBC7F31D2D6DFFC22F18BDBEB066E2F60EDAD9D569E4FA73BAC9A373301C3F8891F3F4C56168D72E9D1DA819207396AF06C065E5EFF7C065ED93FD79E8372BD554BB
	19A81FA066E497F854C606AE762CD68C4E25134BE24BF856957C7FAA1F0B0AC995F059C272F003A8406FF4CCC71DFBCC4B94260967B6536D46E5BA795CE33ADBE1BAC3E63B1259BE77189E685474B5C6BB355F7F668D25D787578F0516DEAE0DF7A9893C9D0EA5CF59F4919B9EAFA4147D3107CE1B4FBD3F9B15B3447EA6E1B060FD8EFB512AF8112A72E254AF4B0B631E67028D79FFE16762FC4FF3413C78051D0B0B7F3F28D1017FDF346EBFEA4F451752AF72DCDC2FBD9768C5D767E227C16C1C72A9CC71094D
	9032998D36D0E7EC37DE1C43BE5D2578142172E94EAF592741DA1F5868641BDF3ACF0FA8BE0DD579B4298BAFD69A444EA91F22356DE93E4CF4EC37DE1C43BE3D2C2DFB4CA7F637B267176CD31C366EB11F585300C93FE41F7E23750975FBD5DEFCBA27F8FF2E3D9BE6776EECD75F44F30E6928B13F143910D1293F143910D1D334BDE657CCC7F9E1EA52B26539F2A0AAB6B49196194C19F1E126363099FD76C84AF8B22F07AEB03D33583F59E706F316B16753AA76B34DF9094299D7711C731C177F824BF94DAF30
	1C0BBD4B19FD5640F8CA759C613CC5610C2B7FE6FAAD677F55CEFA1B67A51AAD0EC9BF77186EB2E6BA665CE3DA232D3167B053FAE32626F30FE9133667BA07199E716C2F4E5196556959CBA9E54A7E92006C8D940B00584D1AB6485F14115453FDCA9E7677FD8FA43E57C830CDD263C9C2D2C90DB648437E0AB5B64628ECA49A64C1793BBDEC18BCEC5C6D1626ED3131E5B02DF6C73D4509CBDA9CAD0DEEE9CC25393C2450AD193614EE0743F8E5BD8C444609B5B0ACF9DF39998EE358A29A1640B791AF4C6C792F
	A431F822B0333DCDDA48B606F754B567F69CED4E355439F3EDD6077CD45FE9612B98442A8774AAD19A33CF1A52403E143E4DF6545606B9ED6552E40E6E34353BBA5A6A79EE309552B48D363123456A34ED43B58795D21AA679875BF1991F224185831E441F3DC9D2EB2BDB1A441798294BAFC9DEE08405120C4B0E3C9450CC231310392B5D52E8F3CBE91BDCAD8D0E460EB61BB51399E9E727F054D5E479CB35E5A124EA0835BC5FC469FFFD60D0ABB7499BBA4F65E14D4D5ACE18ED144F96559BF7ED678867811E
	1C3DDF0C4F914EF7AB1CCDB2E769DC67FC1FA7E7F6B43EC01FA31C07F8F29EF72EF30ED4B80F79251473535AF29EA367BD277B0D970CB91F53ED70D5ED5D78C5F00EB366FCCE57678F3473713F884E1FA81C598B812C5FE0EF0B2857FB55F269F3E7B414B86ADC9E8DA50E537690B36463395C2BA5CED4B827491C592868DC2E3909D30DFB08F33A1CB3944E13A5639E2216A367D0EB1B57F9AE33DBF4CE33DB5AA95E5D1624C5278AE4EAB5177617C6DC38BE9167A4746FADE7EE9E4F77G4E170F1952D3234477
	36DE626F8E37825E970258E7FF59AC7641D6EF5FC22CEC36B81DEE0B73700696AB7BAE624663EE1B72AD3E57E6B17D6D18C7CE94E9EF7377171BD4B27F8FD0CB8788CB7DC33814D7GGECF681GD0CB818294G94G88G88GCBDB8DB1CB7DC33814D7GGECF681G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4ED7GGGG
**end of data**/
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
			ivjJButtonGEOModify.setText(STRING_MODIFY);
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
			ivjJButtonPROGModify.setText(STRING_MODIFY);
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
			ivjJButtonSPIDModify.setText(STRING_MODIFY);
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
			ivjJButtonSplintModify.setText(STRING_MODIFY);
			// user code begin {1}
			ivjJButtonSplintModify.setEnabled(false);
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
			ivjJButtonSUBModify.setText(STRING_MODIFY);
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
			ivjJButtonUSERModify.setText(STRING_MODIFY);
			// user code begin {1}
			ivjJButtonUSERModify.setEnabled(false);
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
			ivjJButtonZIPModify.setText(STRING_MODIFY);
			// user code begin {1}
			ivjJButtonZIPModify.setEnabled(false);
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
			// user code begin {1}
			ivjJComboBoxGEO.setEditable(false);
			ivjJComboBoxGEO.addItem( STRING_NEW );
			ivjJComboBoxGEO.addItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxGEO.setSelectedItem( CtiUtilities.STRING_NONE );
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
			// user code begin {1}
			ivjJComboBoxPROG.setEditable(false);
			ivjJComboBoxPROG.addItem( STRING_NEW );
			ivjJComboBoxPROG.addItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxPROG.setSelectedItem( CtiUtilities.STRING_NONE );
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
			// user code begin {1}
			ivjJComboBoxSPID.setEditable(false);
			ivjJComboBoxSPID.addItem( STRING_NEW );
			ivjJComboBoxSPID.addItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxSPID.setSelectedItem( CtiUtilities.STRING_NONE );
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
			// user code begin {1}
			ivjJComboBoxSPLINTER.setEditable(false);
			ivjJComboBoxSPLINTER.addItem( STRING_NEW );
			ivjJComboBoxSPLINTER.addItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxSPLINTER.setSelectedItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxSPLINTER.setEnabled(false);
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
			// user code begin {1}
			ivjJComboBoxSUB.setEditable(false);
			ivjJComboBoxSUB.addItem( STRING_NEW );
			ivjJComboBoxSUB.addItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxSUB.setSelectedItem( CtiUtilities.STRING_NONE );
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
			// user code begin {1}
			ivjJComboBoxUSER.setEditable(false);
			ivjJComboBoxUSER.addItem( STRING_NEW );
			ivjJComboBoxUSER.addItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxUSER.setSelectedItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxUSER.setEnabled(false);
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
			// user code begin {1}
			ivjJComboBoxZIP.setEditable(false);
			ivjJComboBoxZIP.addItem( STRING_NEW );
			ivjJComboBoxZIP.addItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxZIP.setSelectedItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxZIP.setEnabled(false);
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
			ivjSPLINTERLabel.setEnabled(false);
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
			ivjUSERLabel.setEnabled(false);
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

		group.setFeederAddress( createAddress(
				((LMGroupExpressComFeederAddressPanel)getFeederBitToggleDialog().getContentPane()).getFeederComboBox(), 
				((LMGroupExpressComFeederAddressPanel)getFeederBitToggleDialog().getContentPane()).getFeederAddressTextField(), 
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
			ivjZIPLabel.setEnabled(false);
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
	{
		//if( addresses[i].getAddressName().equalsIgnoreCase(CtiUtilities.STRING_NONE) );		
		if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_SERVICE) )
			getJComboBoxSPID().addItem( addresses[i] );
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_GEO) )
			getJComboBoxGEO().addItem( addresses[i] );
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_SUBSTATION) )
			getJComboBoxSUB().addItem( addresses[i] );
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_FEEDER) ) 
			((LMGroupExpressComFeederAddressPanel)getFeederBitToggleDialog().getContentPane()).getFeederComboBox().addItem(addresses[i]); 
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_PROGRAM) )
			getJComboBoxPROG().addItem( addresses[i] );
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_SPLINTER) )
			getJComboBoxSPLINTER().addItem( addresses[i] );
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_USER) )
			getJComboBoxUSER().addItem( addresses[i] );
		else if( addresses[i].getAddressType().equalsIgnoreCase(IlmDefines.TYPE_ZIP) )
			getJComboBoxZIP().addItem( addresses[i] );
		else
		{
			com.cannontech.clientutils.CTILogger.info("********************************");

			com.cannontech.clientutils.CTILogger.info("*** Found an ExpressCommAddress that is not recognized '" 
						+ addresses[i].getAddressType() + "' in " + this.getClass().getName() );
			
			com.cannontech.clientutils.CTILogger.info("********************************");
		}
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
	getJComboBoxZIP().addActionListener(ivjEventHandler);
	getJComboBoxUSER().addActionListener(ivjEventHandler);
	getJComboBoxSPLINTER().addActionListener(ivjEventHandler);
	getJButtonSPIDModify().addActionListener(ivjEventHandler);
	getJButtonGEOModify().addActionListener(ivjEventHandler);
	getJButtonSUBModify().addActionListener(ivjEventHandler);
	getJButtonZIPModify().addActionListener(ivjEventHandler);
	getJButtonUSERModify().addActionListener(ivjEventHandler);
	getJButtonPROGModify().addActionListener(ivjEventHandler);
	getJButtonSplintModify().addActionListener(ivjEventHandler);
	getJTextFieldSPIDAddress().addCaretListener(ivjEventHandler);
	getJTextFieldGeoAddress().addCaretListener(ivjEventHandler);
	getJTextFieldSubAddress().addCaretListener(ivjEventHandler);
	getJTextFieldZipAddress().addCaretListener(ivjEventHandler);
	getJTextFieldUserAddress().addCaretListener(ivjEventHandler);
	getJTextFieldProgAddress().addCaretListener(ivjEventHandler);
	getJTextFieldSplinterAddress().addCaretListener(ivjEventHandler);
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
public void jButtonFeedAddress_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	
	LMGroupExpressComFeederAddressPanel panel = 
			(LMGroupExpressComFeederAddressPanel)getFeederBitToggleDialog().getContentPane();

	getFeederBitToggleDialog().setTitle("Feeder Address Bitmasking");

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
 * Comment
 */
public void jButtonGEOModify_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if(getJButtonGEOModify().getText().compareTo(STRING_CREATE) == 0)
	{
		if(((String)getJComboBoxGEO().getSelectedItem()).compareTo(STRING_NEW) == 0
			|| !(((String)getJComboBoxGEO().getSelectedItem()).length() > 0)
			|| ((String)getJComboBoxGEO().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			javax.swing.JOptionPane.showMessageDialog(
						this,
						"You have not specified a label name.  Please type a name in the combo box \n" +
						"and click Create again.",
						"Label name not filled in.",
						JOptionPane.WARNING_MESSAGE);	
		}
		else
		{
			getJButtonGEOModify().setText(STRING_MODIFY);
			getJComboBoxGEO().setEditable(false);
			getJComboBoxGEO().addItem(getJComboBoxGEO().getSelectedItem());
		}
	}		
	else
	{
		if(getJComboBoxGEO().getSelectedItem() instanceof LMGroupExpressComAddress)
		{
			int confirm = javax.swing.JOptionPane.showConfirmDialog(
						this,
						"By clicking Modify, you will be allowed to change the address value assigned to  \n" +
						"this label name.  ALL OTHER LOAD GROUPS THAT USE THIS ADDRESS LABEL will receive \n" +
						"this new value once you click Apply and save your changes to the database. \n" +
						"Are you sure you want do this?",
						"Change may effect other load groups!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
			
			if(confirm == JOptionPane.YES_OPTION)
				getJTextFieldGeoAddress().setEnabled(true);
			else
			{
				getJTextFieldGeoAddress().setEnabled(false);
			}
		}
	}
	return;
}
/**
 * Comment
 */
public void jButtonPROGModify_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if(getJButtonPROGModify().getText().compareTo(STRING_CREATE) == 0)
	{
		if(((String)getJComboBoxPROG().getSelectedItem()).compareTo(STRING_NEW) == 0
			|| !(((String)getJComboBoxPROG().getSelectedItem()).length() > 0)
			|| ((String)getJComboBoxPROG().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			javax.swing.JOptionPane.showMessageDialog(
						this,
						"You have not specified a label name.  Please type a name in the combo box \n" +
						"and click Create again.",
						"Label name not filled in.",
						JOptionPane.WARNING_MESSAGE);	
		}
		else
		{
			getJButtonPROGModify().setText(STRING_MODIFY);
			getJComboBoxPROG().setEditable(false);
			getJComboBoxPROG().addItem(getJComboBoxPROG().getSelectedItem());
		}
	}		
	else
	{
		if(getJComboBoxPROG().getSelectedItem() instanceof LMGroupExpressComAddress)
		{
			int confirm = javax.swing.JOptionPane.showConfirmDialog(
						this,
						"By clicking Modify, you are attempting to change the address value assigned to  \n" +
						"this label name.  ALL OTHER LOAD GROUPS THAT USE THIS ADDRESS LABEL will receive \n" +
						"this new value once you click Apply and save your changes to the database. \n" +
						"Are you sure you want do this?",
						"Change may effect other load groups!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
			
			if(confirm == JOptionPane.YES_OPTION)
				getJTextFieldProgAddress().setEnabled(true);
			else
			{
				getJTextFieldProgAddress().setEnabled(false);
			}
		}
	}
	return;
}
/**
 * Comment
 */
public void jButtonSPIDModify_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if(getJButtonSPIDModify().getText().compareTo(STRING_CREATE) == 0)
	{
		if(((String)getJComboBoxSPID().getSelectedItem()).compareTo(STRING_NEW) == 0
			|| !(((String)getJComboBoxSPID().getSelectedItem()).length() > 0)
			|| ((String)getJComboBoxSPID().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			javax.swing.JOptionPane.showMessageDialog(
						this,
						"You have not specified a label name.  Please type a name in the combo box \n" +
						"and click Create again.",
						"Label name not filled in.",
						JOptionPane.WARNING_MESSAGE);	
		}
		else
		{
			getJButtonSPIDModify().setText(STRING_MODIFY);
			getJComboBoxSPID().setEditable(false);
			getJComboBoxSPID().addItem(getJComboBoxSPID().getSelectedItem());
		}
	}		
	else
	{
		if(getJComboBoxSPID().getSelectedItem() instanceof LMGroupExpressComAddress)
		{
			int confirm = javax.swing.JOptionPane.showConfirmDialog(
						this,
						"By clicking Modify, you are attempting to change the address value assigned to  \n" +
						"this label name.  ALL OTHER LOAD GROUPS THAT USE THIS ADDRESS LABEL will receive \n" +
						"this new value once you click Apply and save your changes to the database. \n" +
						"Are you sure you want do this?",
						"Change may effect other load groups!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
			
			if(confirm == JOptionPane.YES_OPTION)
				getJTextFieldSPIDAddress().setEnabled(true);
			else
			{
				getJTextFieldSPIDAddress().setEnabled(false);
			}
		}
	}
	return;
}
/**
 * Comment
 */
public void jButtonSplintModify_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if(getJButtonSplintModify().getText().compareTo(STRING_CREATE) == 0)
	{
		if(((String)getJComboBoxSPLINTER().getSelectedItem()).compareTo(STRING_NEW) == 0
			|| !(((String)getJComboBoxSPLINTER().getSelectedItem()).length() > 0)
			|| ((String)getJComboBoxSPLINTER().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			javax.swing.JOptionPane.showMessageDialog(
						this,
						"You have not specified a label name.  Please type a name in the combo box \n" +
						"and click Create again.",
						"Label name not filled in.",
						JOptionPane.WARNING_MESSAGE);	
		}
		else
		{
			getJButtonSplintModify().setText(STRING_MODIFY);
			getJComboBoxSPLINTER().setEditable(false);
			getJComboBoxSPLINTER().addItem(getJComboBoxSPLINTER().getSelectedItem());
		}
	}		
	else
	{
		if(getJComboBoxSPLINTER().getSelectedItem() instanceof LMGroupExpressComAddress)
		{
			int confirm = javax.swing.JOptionPane.showConfirmDialog(
						this,
						"By clicking Modify, you are attempting to change the address value assigned to  \n" +
						"this label name.  ALL OTHER LOAD GROUPS THAT USE THIS ADDRESS LABEL will receive \n" +
						"this new value once you click Apply and save your changes to the database. \n" +
						"Are you sure you want do this?",
						"Change may effect other load groups!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
			
			if(confirm == JOptionPane.YES_OPTION)
				getJTextFieldSplinterAddress().setEnabled(true);
			else
			{
				getJTextFieldSplinterAddress().setEnabled(false);
			}
		}
	}
	return;
}
/**
 * Comment
 */
public void jButtonSUBModify_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if(getJButtonSUBModify().getText().compareTo(STRING_CREATE) == 0)
	{
		if(((String)getJComboBoxSUB().getSelectedItem()).compareTo(STRING_NEW) == 0
			|| !(((String)getJComboBoxSUB().getSelectedItem()).length() > 0)
			|| ((String)getJComboBoxSUB().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			javax.swing.JOptionPane.showMessageDialog(
						this,
						"You have not specified a label name.  Please type a name in the combo box \n" +
						"and click Create again.",
						"Label name not filled in.",
						JOptionPane.WARNING_MESSAGE);	
		}
		else
		{
			getJButtonSUBModify().setText(STRING_MODIFY);
			getJComboBoxSUB().setEditable(false);
			getJComboBoxSUB().addItem(getJComboBoxSUB().getSelectedItem());
		}
	}		
	else
	{
		if(getJComboBoxSUB().getSelectedItem() instanceof LMGroupExpressComAddress)
		{
			int confirm = javax.swing.JOptionPane.showConfirmDialog(
						this,
						"By clicking Modify, you are attempting to change the address value assigned to  \n" +
						"this label name.  ALL OTHER LOAD GROUPS THAT USE THIS ADDRESS LABEL will receive \n" +
						"this new value once you click Apply and save your changes to the database. \n" +
						"Are you sure you want do this?",
						"Change may effect other load groups!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
			
			if(confirm == JOptionPane.YES_OPTION)
				getJTextFieldSubAddress().setEnabled(true);
			else
			{
				getJTextFieldSubAddress().setEnabled(false);
			}
		}
	}
	return;
}
/**
 * Comment
 */
public void jButtonUSERModify_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if(getJButtonUSERModify().getText().compareTo(STRING_CREATE) == 0)
	{
		if(((String)getJComboBoxUSER().getSelectedItem()).compareTo(STRING_NEW) == 0
			|| !(((String)getJComboBoxUSER().getSelectedItem()).length() > 0)
			|| ((String)getJComboBoxUSER().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			javax.swing.JOptionPane.showMessageDialog(
						this,
						"You have not specified a label name.  Please type a name in the combo box \n" +
						"and click Create again.",
						"Label name not filled in.",
						JOptionPane.WARNING_MESSAGE);	
		}
		else
		{
			getJButtonUSERModify().setText(STRING_MODIFY);
			getJComboBoxUSER().setEditable(false);
			getJComboBoxUSER().addItem(getJComboBoxUSER().getSelectedItem());
		}
	}		
	else
	{
		if(getJComboBoxUSER().getSelectedItem() instanceof LMGroupExpressComAddress)
		{
			int confirm = javax.swing.JOptionPane.showConfirmDialog(
						this,
						"By clicking Modify, you are attempting to change the address value assigned to  \n" +
						"this label name.  ALL OTHER LOAD GROUPS THAT USE THIS ADDRESS LABEL will receive \n" +
						"this new value once you click Apply and save your changes to the database. \n" +
						"Are you sure you want do this?",
						"Change may effect other load groups!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
			
			if(confirm == JOptionPane.YES_OPTION)
				getJTextFieldUserAddress().setEnabled(true);
			else
			{
				getJTextFieldUserAddress().setEnabled(false);
			}
		}
	}
	return;
}
/**
 * Comment
 */
public void jButtonZIPModify_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if(getJButtonZIPModify().getText().compareTo(STRING_CREATE) == 0)
	{
		if(((String)getJComboBoxZIP().getSelectedItem()).compareTo(STRING_NEW) == 0
			|| !(((String)getJComboBoxZIP().getSelectedItem()).length() > 0)
			|| ((String)getJComboBoxZIP().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			javax.swing.JOptionPane.showMessageDialog(
						this,
						"You have not specified a label name.  Please type a name in the combo box \n" +
						"and click Create again.",
						"Label name not filled in.",
						JOptionPane.WARNING_MESSAGE);	
		}
		else
		{
			getJButtonZIPModify().setText(STRING_MODIFY);
			getJComboBoxZIP().setEditable(false);
			getJComboBoxZIP().addItem(getJComboBoxZIP().getSelectedItem());
		}
	}		
	else
	{
		if(getJComboBoxZIP().getSelectedItem() instanceof LMGroupExpressComAddress)
		{
			int confirm = javax.swing.JOptionPane.showConfirmDialog(
						this,
						"By clicking Modify, you are attempting to change the address value assigned to  \n" +
						"this label name.  ALL OTHER LOAD GROUPS THAT USE THIS ADDRESS LABEL will receive \n" +
						"this new value once you click Apply and save your changes to the database. \n" +
						"Are you sure you want do this?",
						"Change may effect other load groups!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
			
			if(confirm == JOptionPane.YES_OPTION)
				getJTextFieldZipAddress().setEnabled(true);
			else
			{
				getJTextFieldZipAddress().setEnabled(false);
			}
		}
	}
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
	
	//this is only for 3.0
	getSPLINTERLabel().setEnabled(false);
	getUSERLabel().setEnabled(false);
	getZIPLabel().setEnabled(false);
	getJButtonSplintModify().setEnabled(false);
	getJButtonUSERModify().setEnabled(false);
	getJButtonZIPModify().setEnabled(false);
	getJComboBoxSPLINTER().setEnabled(false);
	getJComboBoxUSER().setEnabled(false);
	getJComboBoxZIP().setEnabled(false);
	
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
		//make sure they don't change this label's assigned value until Modify is pressed
		getJTextFieldGeoAddress().setEnabled(false);
		
		getJComboBoxGEO().setEditable(false);
		getJButtonGEOModify().setText(STRING_MODIFY);
	}
	else if(getJComboBoxGEO().getSelectedItem() instanceof String)
	{
		if(((String)getJComboBoxGEO().getSelectedItem()).compareTo(STRING_NEW) == 0)
		{
			getJButtonGEOModify().setText(STRING_CREATE);
			getJComboBoxGEO().setEditable(true);
			getJComboBoxGEO().getEditor().selectAll();
			getJTextFieldGeoAddress().setEnabled(true);
		}
		else if(((String)getJComboBoxGEO().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			getJButtonGEOModify().setText(STRING_MODIFY);
			getJComboBoxGEO().setEditable(false);
			getJTextFieldGeoAddress().setEnabled(true);
		}
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
		//make sure they don't change this label's assigned value until Modify is pressed
		getJTextFieldProgAddress().setEnabled(false);
		
		getJComboBoxPROG().setEditable(false);
		getJButtonPROGModify().setText(STRING_MODIFY);
	}
	else if(getJComboBoxPROG().getSelectedItem() instanceof String)
	{
		if(((String)getJComboBoxPROG().getSelectedItem()).compareTo(STRING_NEW) == 0)
		{
			getJButtonPROGModify().setText(STRING_CREATE);
			getJComboBoxPROG().setEditable(true);
			getJComboBoxPROG().getEditor().selectAll();
			getJTextFieldProgAddress().setEnabled(true);
		}
		else if(((String)getJComboBoxPROG().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			getJButtonPROGModify().setText(STRING_MODIFY);
			getJComboBoxPROG().setEditable(false);
			getJTextFieldProgAddress().setEnabled(true);
		}
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
		//make sure they don't change this label's assigned value until Modify is pressed
		getJTextFieldSPIDAddress().setEnabled(false);
		
		getJComboBoxSPID().setEditable(false);
		getJButtonSPIDModify().setText(STRING_MODIFY);
	}
	else if(getJComboBoxSPID().getSelectedItem() instanceof String)
	{
		if(((String)getJComboBoxSPID().getSelectedItem()).compareTo(STRING_NEW) == 0)
		{
			getJButtonSPIDModify().setText(STRING_CREATE);
			getJComboBoxSPID().setEditable(true);
			getJComboBoxSPID().getEditor().selectAll();
			getJTextFieldSPIDAddress().setEnabled(true);
		}
		else if(((String)getJComboBoxSPID().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			getJButtonSPIDModify().setText(STRING_MODIFY);
			getJComboBoxSPID().setEditable(false);
			getJTextFieldSPIDAddress().setEnabled(true);
		}
	}
	
	fireInputUpdate();

	return;
}
/**
 * Comment
 */
public void jComboBoxSPLINTER_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxSPLINTER().getSelectedItem() instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
	{
		com.cannontech.database.db.device.lm.LMGroupExpressComAddress selected =
				(com.cannontech.database.db.device.lm.LMGroupExpressComAddress)getJComboBoxSPLINTER().getSelectedItem();

		getJTextFieldSplinterAddress().setText( selected.getAddress().toString() );
		//make sure they don't change this label's assigned value until Modify is pressed
		getJTextFieldSplinterAddress().setEnabled(false);
		
		getJComboBoxSPLINTER().setEditable(false);
		getJButtonSplintModify().setText(STRING_MODIFY);
	}
	else if(getJComboBoxSPLINTER().getSelectedItem() instanceof String)
	{
		if(((String)getJComboBoxSPLINTER().getSelectedItem()).compareTo(STRING_NEW) == 0)
		{
			getJButtonSplintModify().setText(STRING_CREATE);
			getJComboBoxSPLINTER().setEditable(true);
			getJComboBoxSPLINTER().getEditor().selectAll();
			getJTextFieldSplinterAddress().setEnabled(true);
		}
		else if(((String)getJComboBoxSPLINTER().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			getJButtonSplintModify().setText(STRING_MODIFY);
			getJComboBoxSPLINTER().setEditable(false);
			getJTextFieldSplinterAddress().setEnabled(true);
		}
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
		//make sure they don't change this label's assigned value until Modify is pressed
		getJTextFieldSubAddress().setEnabled(false);
		
		getJComboBoxSUB().setEditable(false);
		getJButtonSUBModify().setText(STRING_MODIFY);
	}
	else if(getJComboBoxSUB().getSelectedItem() instanceof String)
	{
		if(((String)getJComboBoxSUB().getSelectedItem()).compareTo(STRING_NEW) == 0)
		{
			getJButtonSUBModify().setText(STRING_CREATE);
			getJComboBoxSUB().setEditable(true);
			getJComboBoxSUB().getEditor().selectAll();
			getJTextFieldSubAddress().setEnabled(true);
		}
		else if(((String)getJComboBoxSUB().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			getJButtonSUBModify().setText(STRING_MODIFY);
			getJComboBoxSUB().setEditable(false);
			getJTextFieldSubAddress().setEnabled(true);
		}
	}
	
	fireInputUpdate();

	return;
}
/**
 * Comment
 */
public void jComboBoxUSER_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxUSER().getSelectedItem() instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
	{
		com.cannontech.database.db.device.lm.LMGroupExpressComAddress selected =
				(com.cannontech.database.db.device.lm.LMGroupExpressComAddress)getJComboBoxUSER().getSelectedItem();

		getJTextFieldUserAddress().setText( selected.getAddress().toString() );
		//make sure they don't change this label's assigned value until Modify is pressed
		getJTextFieldUserAddress().setEnabled(false);
		
		getJComboBoxUSER().setEditable(false);
		getJButtonUSERModify().setText(STRING_MODIFY);
	}
	else if(getJComboBoxUSER().getSelectedItem() instanceof String)
	{
		if(((String)getJComboBoxUSER().getSelectedItem()).compareTo(STRING_NEW) == 0)
		{
			getJButtonUSERModify().setText(STRING_CREATE);
			getJComboBoxUSER().setEditable(true);
			getJComboBoxUSER().getEditor().selectAll();
			getJTextFieldUserAddress().setEnabled(true);
		}
		else if(((String)getJComboBoxUSER().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			getJButtonUSERModify().setText(STRING_MODIFY);
			getJComboBoxUSER().setEditable(false);
			getJTextFieldUserAddress().setEnabled(true);
		}
	}
	
	fireInputUpdate();

	return;
}
/**
 * Comment
 */
public void jComboBoxZIP_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxZIP().getSelectedItem() instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
	{
		com.cannontech.database.db.device.lm.LMGroupExpressComAddress selected =
				(com.cannontech.database.db.device.lm.LMGroupExpressComAddress)getJComboBoxZIP().getSelectedItem();

		getJTextFieldZipAddress().setText( selected.getAddress().toString() );
		//make sure they don't change this label's assigned value until Modify is pressed
		getJTextFieldZipAddress().setEnabled(false);
		
		getJComboBoxZIP().setEditable(false);
		getJButtonZIPModify().setText(STRING_MODIFY);
	}
	else if(getJComboBoxZIP().getSelectedItem() instanceof String)
	{
		if(((String)getJComboBoxZIP().getSelectedItem()).compareTo(STRING_NEW) == 0)
		{
			getJButtonZIPModify().setText(STRING_CREATE);
			getJComboBoxZIP().setEditable(true);
			getJComboBoxZIP().getEditor().selectAll();
			getJTextFieldZipAddress().setEnabled(true);
		}
		else if(((String)getJComboBoxZIP().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			getJButtonZIPModify().setText(STRING_MODIFY);
			getJComboBoxZIP().setEditable(false);
			getJTextFieldZipAddress().setEnabled(true);
		}
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

		getJTextFieldSPIDAddress().setText( group.getServiceProviderAddress().getAddress().toString() );
		if(group.getServiceProviderAddress().getAddressName().compareTo(CtiUtilities.STRING_NONE) != 0)
		{
			getJComboBoxSPID().setSelectedItem( group.getServiceProviderAddress() );
			getJTextFieldSPIDAddress().setEnabled(false);
		}

		getJTextFieldGeoAddress().setText( group.getGeoAddress().getAddress().toString() );
		if(group.getGeoAddress().getAddressName().compareTo(CtiUtilities.STRING_NONE) != 0)
		{
			getJComboBoxGEO().setSelectedItem( group.getGeoAddress() );
			getJTextFieldGeoAddress().setEnabled(false);
		}

		((LMGroupExpressComFeederAddressPanel)getFeederBitToggleDialog().getContentPane()).setFeederAddressBitMask(group.getFeederAddress().getAddress()); 
		if(group.getFeederAddress().getAddressName().compareTo(CtiUtilities.STRING_NONE) != 0)
		{
			((LMGroupExpressComFeederAddressPanel)getFeederBitToggleDialog().getContentPane()).getFeederComboBox().setSelectedItem( group.getFeederAddress() ); 
			((LMGroupExpressComFeederAddressPanel)getFeederBitToggleDialog().getContentPane()).getFeederAddress16BitTogglePanel().setEnabled(false);
		}

		getJTextFieldSubAddress().setText( group.getSubstationAddress().getAddress().toString() );
		if(group.getSubstationAddress().getAddressName().compareTo(CtiUtilities.STRING_NONE) != 0)
		{
			getJComboBoxSUB().setSelectedItem( group.getSubstationAddress() );
			getJTextFieldSubAddress().setEnabled(false);
		}
		
		getJTextFieldProgAddress().setText( group.getProgramAddress().getAddress().toString() );
		if(group.getProgramAddress().getAddressName().compareTo(CtiUtilities.STRING_NONE) != 0)
		{
			getJComboBoxPROG().setSelectedItem( group.getProgramAddress() );
			getJTextFieldProgAddress().setEnabled(false);
		}
		
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

		if(getJCheckBoxGEO().isSelected())
			getJTextFieldGeoAddress().setBackground(Color.CYAN);
		if(getJCheckBoxSUB().isSelected())
			getJTextFieldSubAddress().setBackground(Color.CYAN);
		if(getJCheckBoxFEED().isSelected())
			getJButtonFeedAddress().setBackground(Color.CYAN);
		if(getJCheckBoxZIP().isSelected())
			getJTextFieldZipAddress().setBackground(Color.CYAN);
		if(getJCheckBoxUSER().isSelected())
			getJTextFieldUserAddress().setBackground(Color.CYAN);
		if(getJCheckBoxPROG().isSelected())
			getJTextFieldProgAddress().setBackground(Color.CYAN);
		if(getJCheckBoxSPLINTER().isSelected())
			getJTextFieldSplinterAddress().setBackground(Color.CYAN);

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
}
