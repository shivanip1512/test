package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMGroupExpressComAddress;

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

private javax.swing.JDialog getFeederBitToggleDialog() 
{
	if( feederBitToggleDialog == null )
	{
		feederBitToggleDialog = new JDialog(SwingUtil.getParentFrame(this), true);

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
			constraintsJButtonFeedAddress.anchor = java.awt.GridBagConstraints.WEST;
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
			constraintsJTextFieldSerialAddress.anchor = java.awt.GridBagConstraints.WEST;
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

		group.setSplinterAddress( createAddress(
				getJComboBoxSPLINTER(), 
				getJTextFieldSplinterAddress(), 
				IlmDefines.TYPE_SPLINTER) );
				
		group.setUserAddress( createAddress(
				getJComboBoxUSER(), 
				getJTextFieldUserAddress(), 
				IlmDefines.TYPE_USER) );
				
		group.setZipCodeAddress( createAddress(
				getJComboBoxZIP(), 
				getJTextFieldZipAddress(), 
				IlmDefines.TYPE_ZIP) );

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
	if( !isTextValid(getJTextFieldSPIDAddress().getText())) {
		setErrorString("The SPID address must be 1 or greater");
		return false;
	}
	
	if( getJCheckBoxGEO().isSelected() && !isTextValid(getJTextFieldGeoAddress().getText())) {
        setErrorString("When selected the GEO address must be 1 or greater");
        return false;
    }
	
	if( getJCheckBoxSUB().isSelected() && !isTextValid(getJTextFieldSubAddress().getText())) {
        setErrorString("When selected the Substation address must be 1 or greater");
        return false;
    }
	
	if( getJCheckBoxFEED().isSelected() &&
	    ((LMGroupExpressComFeederAddressPanel)getFeederBitToggleDialog().getContentPane()).getFeederAddressBitMask() <= 0 ) {
        setErrorString("When selected the Feeder address must have at least one feeder selected");
        return false;
    }
	
	if( getJCheckBoxZIP().isSelected() && !isTextValid(getJTextFieldZipAddress().getText())) {
        setErrorString("When selected the ZIP address must be 1 or greater");
        return false;
    }
	
	if( getJCheckBoxUSER().isSelected() && !isTextValid(getJTextFieldUserAddress().getText())) {
        setErrorString("When selected the User address must be 1 or greater");
        return false;
    }
	
	if( getJCheckBoxSerial().isSelected() && !isTextValid(getJTextFieldSerialAddress().getText())) {
        setErrorString("When selected the Serial address must be 1 or greater");
        return false;
    }
	
	if( getJCheckBoxPROG().isSelected() && !isTextValid(getJTextFieldProgAddress().getText())) {
        setErrorString("When selected the Program address must be 1 or greater");
        return false;
    }
	
	if( getJCheckBoxSPLINTER().isSelected() && !isTextValid(getJTextFieldSplinterAddress().getText())) {
	    setErrorString("When selected the Splinter address must be 1 or greater");
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

private boolean isTextValid(String text) {
	return (StringUtils.isNotBlank(text) && Integer.parseInt(text) > 0);
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
			getJComboBoxPROG().addItem(getJComboBoxPROG().getSelectedItem());
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
		getJCheckBoxRelay1().setBackground(Color.CYAN);
		getJCheckBoxRelay2().setBackground(Color.CYAN);
		getJCheckBoxRelay3().setBackground(Color.CYAN);
		getJCheckBoxRelay4().setBackground(Color.CYAN);
		getJCheckBoxRelay5().setBackground(Color.CYAN);
		getJCheckBoxRelay6().setBackground(Color.CYAN);
		getJCheckBoxRelay7().setBackground(Color.CYAN);
		getJCheckBoxRelay8().setBackground(Color.CYAN);
	}
	
	else
	{
		com.cannontech.common.gui.util.TitleBorder alteredBorder = new com.cannontech.common.gui.util.TitleBorder();
		alteredBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		alteredBorder.setTitle("Configured Loads");
		getJPanelRelayUsage().setBorder(alteredBorder);
		getJCheckBoxRelay1().setBackground(null);
		getJCheckBoxRelay2().setBackground(null);
		getJCheckBoxRelay3().setBackground(null);
		getJCheckBoxRelay4().setBackground(null);
		getJCheckBoxRelay5().setBackground(null);
		getJCheckBoxRelay6().setBackground(null);
		getJCheckBoxRelay7().setBackground(null);
		getJCheckBoxRelay8().setBackground(null);
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
	
	if(getJCheckBoxPROG().isSelected())
		getJTextFieldProgAddress().setBackground(Color.CYAN);
	else
		getJTextFieldProgAddress().setBackground(Color.WHITE);
		
	if(getJCheckBoxSPLINTER().isSelected())
		getJTextFieldSplinterAddress().setBackground(Color.CYAN);
	else
		getJTextFieldSplinterAddress().setBackground(Color.WHITE);
	
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
	{
		if(! getJCheckBoxLOAD().isSelected())
			getJCheckBoxLOAD().doClick();
		getJCheckBoxPROG().setSelected(false);
		getJCheckBoxSPLINTER().setSelected(false);
	}
		
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
	
	if(getJCheckBoxSPLINTER().isSelected())
		getJTextFieldSplinterAddress().setBackground(Color.CYAN);
	else
		getJTextFieldSplinterAddress().setBackground(Color.WHITE);
		
	if(getJCheckBoxPROG().isSelected())
		getJTextFieldProgAddress().setBackground(Color.CYAN);
	else
		getJTextFieldProgAddress().setBackground(Color.WHITE);
	
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
		
		getJTextFieldSplinterAddress().setText( group.getSplinterAddress().getAddress().toString() );
		if(group.getSplinterAddress().getAddressName().compareTo(CtiUtilities.STRING_NONE) != 0)
		{
			getJComboBoxSPLINTER().setSelectedItem( group.getSplinterAddress() );
			getJTextFieldSplinterAddress().setEnabled(false);
		}
		
		getJTextFieldUserAddress().setText( group.getUserAddress().getAddress().toString() );
		if(group.getUserAddress().getAddressName().compareTo(CtiUtilities.STRING_NONE) != 0)
		{
			getJComboBoxUSER().setSelectedItem( group.getUserAddress() );
			getJTextFieldUserAddress().setEnabled(false);
		}		
		
		getJTextFieldZipAddress().setText( group.getZipCodeAddress().getAddress().toString() );
		if(group.getZipCodeAddress().getAddressName().compareTo(CtiUtilities.STRING_NONE) != 0)
		{
			getJComboBoxZIP().setSelectedItem( group.getZipCodeAddress() );
			getJTextFieldZipAddress().setEnabled(false);
		}
		
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
		getJCheckBoxLOAD().setSelected(addUsage.indexOf("L") >= 0 );

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

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
        { 
            getJCheckBoxGEO().requestFocus();
        } 
    });    
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
	//getJCheckBoxPROG().setSelected(! (confirm == JOptionPane.NO_OPTION && getJCheckBoxPROG().isSelected()));
	//getJCheckBoxSPLINTER().setSelected(! (confirm == JOptionPane.NO_OPTION && getJCheckBoxSPLINTER().isSelected()));
			
	return;
						
}
}
