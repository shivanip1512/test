package com.cannontech.dbeditor.wizard.device.lmgroup;


import com.cannontech.common.gui.unchanging.StringRangeDocument;
import com.cannontech.database.data.device.lm.LMGroupSA305;
/**
 * Insert the type's description here.
 * Creation date: (2/23/2004 5:33:06 PM)
 * @author: 
 */
public class SA305EditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjDivisionAddressLabel = null;
	private javax.swing.JLabel ivjGroupAddressLabel = null;
	private javax.swing.JCheckBox ivjJCheckBoxSerial = null;
	private javax.swing.JTextField ivjJTextDivisionAddress = null;
	private javax.swing.JTextField ivjJTextFieldIndividualAddress = null;
	private javax.swing.JTextField ivjJTextFieldRateFamily = null;
	private javax.swing.JTextField ivjJTextFieldRateMember = null;
	private javax.swing.JTextField ivjJTextFieldSubAddress = null;
	private javax.swing.JTextField ivjJTextFieldUtilityAddress = null;
	private javax.swing.JLabel ivjRateFamilyLabel = null;
	private javax.swing.JLabel ivjRateHierachyLabel = null;
	private javax.swing.JLabel ivjRateMemberLabel = null;
	private javax.swing.JLabel ivjSubAddressLabel = null;
	private javax.swing.JLabel ivjUtilityAddressLabel = null;
	private javax.swing.JPanel ivjAddressPanel = null;
	private javax.swing.JPanel ivjAddressUsagePanel = null;
	private javax.swing.JCheckBox ivjJCheckBoxDivisionUsage = null;
	private javax.swing.JCheckBox ivjJCheckBoxGroupUsage = null;
	private javax.swing.JCheckBox ivjJCheckBoxRateUsage = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay1 = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay2 = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay3 = null;
	private javax.swing.JCheckBox ivjJCheckBoxRelay4 = null;
	private javax.swing.JCheckBox ivjJCheckBoxSubUsage = null;
	private javax.swing.JCheckBox ivjJCheckBoxUtilityUsage = null;
	private javax.swing.JPanel ivjRelayUsagePanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextField ivjJTextFieldGroupAddress = null;
	private javax.swing.JTextField ivjJTextFieldRateHierarchy = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SA305EditorPanel.this.getJCheckBoxRelay1()) 
				connEtoC1(e);
			if (e.getSource() == SA305EditorPanel.this.getJCheckBoxRelay2()) 
				connEtoC2(e);
			if (e.getSource() == SA305EditorPanel.this.getJCheckBoxRelay3()) 
				connEtoC3(e);
			if (e.getSource() == SA305EditorPanel.this.getJCheckBoxRelay4()) 
				connEtoC4(e);
			if (e.getSource() == SA305EditorPanel.this.getJCheckBoxDivisionUsage()) 
				connEtoC5(e);
			if (e.getSource() == SA305EditorPanel.this.getJCheckBoxRateUsage()) 
				connEtoC6(e);
			if (e.getSource() == SA305EditorPanel.this.getJCheckBoxGroupUsage()) 
				connEtoC7(e);
			if (e.getSource() == SA305EditorPanel.this.getJCheckBoxSubUsage()) 
				connEtoC8(e);
			if (e.getSource() == SA305EditorPanel.this.getJTextFieldRateMember()) 
				connEtoC12(e);
			if (e.getSource() == SA305EditorPanel.this.getJTextFieldSubAddress()) 
				connEtoC13(e);
			if (e.getSource() == SA305EditorPanel.this.getJCheckBoxSerial()) 
				connEtoC9(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == SA305EditorPanel.this.getJTextFieldIndividualAddress()) 
				connEtoC10(e);
			if (e.getSource() == SA305EditorPanel.this.getJTextFieldRateFamily()) 
				connEtoC11(e);
			if (e.getSource() == SA305EditorPanel.this.getJTextDivisionAddress()) 
				connEtoC14(e);
			if (e.getSource() == SA305EditorPanel.this.getJTextFieldGroupAddress()) 
				connEtoC15(e);
			if (e.getSource() == SA305EditorPanel.this.getJTextFieldUtilityAddress()) 
				connEtoC16(e);
			if (e.getSource() == SA305EditorPanel.this.getJTextFieldRateHierarchy()) 
				connEtoC17(e);
		};
	};
	private javax.swing.JPanel ivjRateAddressPanel = null;
/**
 * SA305EditorPanel constructor comment.
 */
public SA305EditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JCheckBoxRelay1.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
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
 * connEtoC10:  (JTextFieldIndividualAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> SA305EditorPanel.fireInputUpdate()V)
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
 * connEtoC11:  (JTextFieldRateFamily.caret.caretUpdate(javax.swing.event.CaretEvent) --> SA305EditorPanel.fireInputUpdate()V)
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
 * connEtoC12:  (JTextFieldRateMember.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
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
 * connEtoC13:  (JTextSubAddress.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
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
 * connEtoC13:  (JTextSubAddress.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
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
 * connEtoC14:  (JTextDivisionAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> SA305EditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC15:  (JTextFieldSubAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> SA305EditorPanel.fireInputUpdate()V)
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
 * connEtoC16:  (JTextFieldUtilityAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> SA305EditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC17:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> SA305EditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (JCheckBoxRelay2.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JCheckBoxRelay3.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JCheckBoxRelay4.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JCheckBoxDivisionUsage.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
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
 * connEtoC6:  (JCheckBoxRateUsage.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
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
 * connEtoC7:  (JCheckBoxGroupUsage.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
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
 * connEtoC8:  (JCheckBoxSubUsage.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
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
 * connEtoC9:  (JCheckBoxSerial.action.actionPerformed(java.awt.event.ActionEvent) --> SA305EditorPanel.jCheckBoxSerial_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
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
 * Return the JPanelAddress property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressPanel() {
	if (ivjAddressPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Addressing");
			ivjAddressPanel = new javax.swing.JPanel();
			ivjAddressPanel.setName("AddressPanel");
			ivjAddressPanel.setPreferredSize(new java.awt.Dimension(338, 237));
			ivjAddressPanel.setBorder(ivjLocalBorder);
			ivjAddressPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTextFieldUtilityAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldUtilityAddress.gridx = 2; constraintsJTextFieldUtilityAddress.gridy = 1;
			constraintsJTextFieldUtilityAddress.gridwidth = 2;
			constraintsJTextFieldUtilityAddress.weightx = 1.0;
			constraintsJTextFieldUtilityAddress.ipadx = 30;
			constraintsJTextFieldUtilityAddress.ipady = -1;
			constraintsJTextFieldUtilityAddress.insets = new java.awt.Insets(34, 0, 8, 5);
			getAddressPanel().add(getJTextFieldUtilityAddress(), constraintsJTextFieldUtilityAddress);

			java.awt.GridBagConstraints constraintsUtilityAddressLabel = new java.awt.GridBagConstraints();
			constraintsUtilityAddressLabel.gridx = 1; constraintsUtilityAddressLabel.gridy = 1;
			constraintsUtilityAddressLabel.ipadx = 32;
			constraintsUtilityAddressLabel.ipady = 4;
			constraintsUtilityAddressLabel.insets = new java.awt.Insets(36, 12, 8, 1);
			getAddressPanel().add(getUtilityAddressLabel(), constraintsUtilityAddressLabel);

			java.awt.GridBagConstraints constraintsGroupAddressLabel = new java.awt.GridBagConstraints();
			constraintsGroupAddressLabel.gridx = 1; constraintsGroupAddressLabel.gridy = 2;
			constraintsGroupAddressLabel.ipadx = 29;
			constraintsGroupAddressLabel.ipady = 4;
			constraintsGroupAddressLabel.insets = new java.awt.Insets(10, 12, 5, 1);
			getAddressPanel().add(getGroupAddressLabel(), constraintsGroupAddressLabel);

			java.awt.GridBagConstraints constraintsJTextFieldGroupAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldGroupAddress.gridx = 2; constraintsJTextFieldGroupAddress.gridy = 2;
			constraintsJTextFieldGroupAddress.gridwidth = 2;
			constraintsJTextFieldGroupAddress.weightx = 1.0;
			constraintsJTextFieldGroupAddress.ipadx = 30;
			constraintsJTextFieldGroupAddress.ipady = -1;
			constraintsJTextFieldGroupAddress.insets = new java.awt.Insets(9, 0, 5, 5);
			getAddressPanel().add(getJTextFieldGroupAddress(), constraintsJTextFieldGroupAddress);

			java.awt.GridBagConstraints constraintsDivisionAddressLabel = new java.awt.GridBagConstraints();
			constraintsDivisionAddressLabel.gridx = 1; constraintsDivisionAddressLabel.gridy = 3;
			constraintsDivisionAddressLabel.ipadx = 18;
			constraintsDivisionAddressLabel.ipady = 4;
			constraintsDivisionAddressLabel.insets = new java.awt.Insets(3, 12, 9, 1);
			getAddressPanel().add(getDivisionAddressLabel(), constraintsDivisionAddressLabel);

			java.awt.GridBagConstraints constraintsJTextDivisionAddress = new java.awt.GridBagConstraints();
			constraintsJTextDivisionAddress.gridx = 2; constraintsJTextDivisionAddress.gridy = 3;
			constraintsJTextDivisionAddress.gridwidth = 2;
			constraintsJTextDivisionAddress.weightx = 1.0;
			constraintsJTextDivisionAddress.ipadx = 30;
			constraintsJTextDivisionAddress.ipady = -1;
			constraintsJTextDivisionAddress.insets = new java.awt.Insets(3, 0, 8, 5);
			getAddressPanel().add(getJTextDivisionAddress(), constraintsJTextDivisionAddress);

			java.awt.GridBagConstraints constraintsSubAddressLabel = new java.awt.GridBagConstraints();
			constraintsSubAddressLabel.gridx = 1; constraintsSubAddressLabel.gridy = 4;
			constraintsSubAddressLabel.ipadx = 7;
			constraintsSubAddressLabel.ipady = 4;
			constraintsSubAddressLabel.insets = new java.awt.Insets(0, 12, 15, 1);
			getAddressPanel().add(getSubAddressLabel(), constraintsSubAddressLabel);

			java.awt.GridBagConstraints constraintsJTextFieldSubAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldSubAddress.gridx = 2; constraintsJTextFieldSubAddress.gridy = 4;
			constraintsJTextFieldSubAddress.gridwidth = 2;
			constraintsJTextFieldSubAddress.weightx = 1.0;
			constraintsJTextFieldSubAddress.ipadx = 30;
			constraintsJTextFieldSubAddress.ipady = -1;
			constraintsJTextFieldSubAddress.insets = new java.awt.Insets(0, 0, 13, 5);
			getAddressPanel().add(getJTextFieldSubAddress(), constraintsJTextFieldSubAddress);

			java.awt.GridBagConstraints constraintsJTextFieldIndividualAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldIndividualAddress.gridx = 3; constraintsJTextFieldIndividualAddress.gridy = 5;
			constraintsJTextFieldIndividualAddress.gridwidth = 2;
			constraintsJTextFieldIndividualAddress.weightx = 1.0;
			constraintsJTextFieldIndividualAddress.ipadx = 116;
			constraintsJTextFieldIndividualAddress.ipady = -1;
			constraintsJTextFieldIndividualAddress.insets = new java.awt.Insets(11, 5, 40, 76);
			getAddressPanel().add(getJTextFieldIndividualAddress(), constraintsJTextFieldIndividualAddress);

			java.awt.GridBagConstraints constraintsJCheckBoxSerial = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSerial.gridx = 1; constraintsJCheckBoxSerial.gridy = 5;
			constraintsJCheckBoxSerial.gridwidth = 2;
			constraintsJCheckBoxSerial.ipady = -4;
			constraintsJCheckBoxSerial.insets = new java.awt.Insets(11, 14, 40, 5);
			getAddressPanel().add(getJCheckBoxSerial(), constraintsJCheckBoxSerial);

			java.awt.GridBagConstraints constraintsRateAddressPanel = new java.awt.GridBagConstraints();
			constraintsRateAddressPanel.gridx = 4; constraintsRateAddressPanel.gridy = 1;
constraintsRateAddressPanel.gridheight = 3;
			constraintsRateAddressPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRateAddressPanel.ipadx = -61;
			constraintsRateAddressPanel.insets = new java.awt.Insets(35, 12, 11, 5);
			getAddressPanel().add(getRateAddressPanel(), constraintsRateAddressPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressPanel;
}
/**
 * Return the AddressUsagePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressUsagePanel() {
	if (ivjAddressUsagePanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder2.setTitle("Address Usage");
			ivjAddressUsagePanel = new javax.swing.JPanel();
			ivjAddressUsagePanel.setName("AddressUsagePanel");
			ivjAddressUsagePanel.setBorder(ivjLocalBorder2);
			ivjAddressUsagePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxRateUsage = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRateUsage.gridx = 2; constraintsJCheckBoxRateUsage.gridy = 2;
			constraintsJCheckBoxRateUsage.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCheckBoxRateUsage.ipadx = 16;
			constraintsJCheckBoxRateUsage.ipady = -4;
			constraintsJCheckBoxRateUsage.insets = new java.awt.Insets(0, 1, 1, 16);
			getAddressUsagePanel().add(getJCheckBoxRateUsage(), constraintsJCheckBoxRateUsage);

			java.awt.GridBagConstraints constraintsJCheckBoxGroupUsage = new java.awt.GridBagConstraints();
			constraintsJCheckBoxGroupUsage.gridx = 1; constraintsJCheckBoxGroupUsage.gridy = 1;
			constraintsJCheckBoxGroupUsage.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCheckBoxGroupUsage.ipadx = 34;
			constraintsJCheckBoxGroupUsage.ipady = -4;
			constraintsJCheckBoxGroupUsage.insets = new java.awt.Insets(5, 3, 1, 0);
			getAddressUsagePanel().add(getJCheckBoxGroupUsage(), constraintsJCheckBoxGroupUsage);

			java.awt.GridBagConstraints constraintsJCheckBoxSubUsage = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSubUsage.gridx = 1; constraintsJCheckBoxSubUsage.gridy = 2;
			constraintsJCheckBoxSubUsage.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCheckBoxSubUsage.ipadx = 34;
			constraintsJCheckBoxSubUsage.ipady = -4;
			constraintsJCheckBoxSubUsage.insets = new java.awt.Insets(0, 3, 1, 0);
			getAddressUsagePanel().add(getJCheckBoxSubUsage(), constraintsJCheckBoxSubUsage);

			java.awt.GridBagConstraints constraintsJCheckBoxDivisionUsage = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDivisionUsage.gridx = 2; constraintsJCheckBoxDivisionUsage.gridy = 1;
			constraintsJCheckBoxDivisionUsage.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCheckBoxDivisionUsage.ipadx = 16;
			constraintsJCheckBoxDivisionUsage.ipady = -4;
			constraintsJCheckBoxDivisionUsage.insets = new java.awt.Insets(5, 1, 1, 16);
			getAddressUsagePanel().add(getJCheckBoxDivisionUsage(), constraintsJCheckBoxDivisionUsage);

			java.awt.GridBagConstraints constraintsJCheckBoxUtilityUsage = new java.awt.GridBagConstraints();
			constraintsJCheckBoxUtilityUsage.gridx = 1; constraintsJCheckBoxUtilityUsage.gridy = 3;
			constraintsJCheckBoxUtilityUsage.gridwidth = 2;
			constraintsJCheckBoxUtilityUsage.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCheckBoxUtilityUsage.ipadx = 62;
			constraintsJCheckBoxUtilityUsage.ipady = -4;
			constraintsJCheckBoxUtilityUsage.insets = new java.awt.Insets(1, 3, 19, 58);
			getAddressUsagePanel().add(getJCheckBoxUtilityUsage(), constraintsJCheckBoxUtilityUsage);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressUsagePanel;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G36F4D9B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8FDC155579B86BE74DAD86760DB65B5CC6C90D3E3AE24596EB54F059C60B9696ADDC342C28282858F25F51E27395AE02DEC590CC94D4D4D090C47EAAA2AAA8AA9695AA9A8AAA976E95EEDE6E45FBAFF9B5355FF34EF91F733E773D6F3D7CA97AB8BF1F47771E67BC7F4FF34EFB5E77BD67A0642C75CF1D1F1CA38873D385715F767902B065370210A41CB80238101F7AE885593FF5GD9026122862E
	61D0F779342FB6D370E9CA07F28C14F3777BEAD7406FEC6166260ABB60079BBD69D0774F6FCE5968D9CFFF9E6AC923652BA79643B583E08E60B47EBE5AB600CC617BA7F2C5FCB114B3445F8466A78BC266ED0446561FAB437B09F431F0CD848826B43FB811AF56F7C019508C42F542ED0CF68ADC97D26B2E2F2F93693AEE8A94667EB1AFF98951A5130F10A472965CAEDE0529B490A942F3FB1B615A50F62EEFF72FE5203CD7E7B6DB4CF6C3EFFF393E4720B75AAD56728B46CBBA2B3EDCEF785C58EBA8B78D74D9
	ADC3037575C71B2AABEA77D312B61D59E01AF4BB317D567EF4AA97EC07F27ED4024BB212F8C5C1B99EC0B3956D1F35248163F0AED018B3B5432D7D5DD3195D9326CA767B1D1816DC93C0647F053687611B8975C9G4C1675A75196C7G5A3264A4218D02F2B8409C0E0F2278C4A26F8E9FED719DE8632ECDE8E34797778B09BD3B13173A3131718EE6DB7F9D120DF3F7C7EAD6521C191BCF5ABDG6A4281E2G04BBFD3485G0DGF9B4D76F495B8C57269E7BBE4B4000455CB4B838572437FEE4519BCC703B3E9E
	EAC45C91C32F452A9784160FBF69AFD331872163CE67DCA440FC7BD4407C19FE64EE41EE2FC9CED571AD78695549A6D106571D12EFC9F71238D5FEC662668F3F89DF8949B50A7F18629BFFA661D9DB2D2039C04AD320AEAE90635C11013634EAEE97B2AB9B1353D4EC197C3B5A641A7FE5B6347E2FE48B0B716CB5A4468E407B81CC871889908B201906B1FEB72FFA38989F027C371AF52623C3BD03BA2BFD6F105124B7D845D84FB961A956EA40785E9AB59F0BCBE0353C0DE26E5B239A171840426402E9AC9EF5
	532438B05D1BCE3A535DB14DB5B7B2F89BFE0B768953B45606A99CFFA345DF1661D95BE6D3BCA9FBC3DD82G230F6CA47876690CFE831773BA45874870E2FF26F2C8F9AE54E55E6D23E56DFB21000C0BE57778E8DBG3A819CG13810281426F41B6EE4E5FBA42FE54A6EBDF467B6F7C7761EAF0582D3AC3E61BDDE76EB59C57D90D3A9E1341A608B48BD6133876B871E9D7135C6B859D878DE603D5E7B7DA4CA0DDB7E000E432B11A1C55D90446EA20356D468183A8989894D0EE7E6AEC92EF1D4DFEECD02F339B
	A48AD6DF381A6444673A63A09184406F5CA0922FDF52B1B78E7C5784E12C825691DC97146DC198171F2EDE86578FA1398D267A7AE6E3AFB1C3E73D08B93A7D33B638CE84FA4DAFFC34DEGC9G0EFB618AF079BE3882B802618AF07917F085F07C8A6EDDG0E7B218CF0B9842EG033F067B7083583F5F6A40BCEEDE72F3216C695D2AF9CC7B7883ACA7039ED066F1BB35AF9A7009G393F71515681F4GB8GA68304GC483A4826406C2BDC0870083E0B2C088C0B4C0A2C06E03D07FA05E53D79B9B678F5FA7C9
	BC5470A4C672B2090717C8CBE2C46241E5842B4B20F244BA92BB44650AF1A031A7B127F4773B4A5038310DGE9BBA7BFC47EA4195D245D5D7207382F63367F8AE3B9B822D8AEF40357AA4A2B85161F64B7496DA2D95DF28F7233456BA6992EC43C12FE38C27C3DD605B755035C250A72BA713AC406CB71403FE11838904893FD4D9216EDA0E3E18764299D406FC16C4BAF2FAE90689837D7E7B3602876099586A403DE6A590C7645B544E6436786333D59B0E8B06B6107CD9CD3DEDBC35AC19C6F4EDA849C0B5E26
	F8F27F5B8783120D637F31A60B61C106FC74447AFF53FA1D5DEEB5768C598DC78DF63B515CA771272EA536581CEE2DB651571CC2626BF470F1A6C0947755E7ADE9CB9C37DAAD16F3C3036DE4C496473F5DF41E97877442C318035FBEC9666C053CFC93ADCB75ABCFF6F808BF619B2E0DBC8153D165D107279C99390EBFF71279BB5E07D3C5BCE9BB2D1813CB855697C8DFD9A1669509AB69D7EB457C2D657292283CC6703F9FE092C088409C00745F7AE8ABGBAG3CG8281A28192818A42601E8CB088B099A0AC
	8C63BC67E4667C616D4B98010DA309C96DD70C69B785623C2C134446D3DC323E46580CE45C3996007579EF95389F33A696280FD9BE856AE316DF0167B1E9CA015A18446A2696103631DCB04B46384082B20F6A35184D86BA50F29601BDD33F55CE7CB01A0D76CF0D7ABE037D287112811E7FE71031AA024D5520FFA405615C6842861C9B95DE3ACB88BB33D275B9EC729DF5496DB344E74C99525C0859F457BAB2F69ACC06818854BE4B1059AEBED352F920083725D05CC77B4C04377803FD7D065EF3FBAD0EA386
	136EE2E5190EFAD34EAEED866BD90BF54020AF7ECC77392EDCF741DECEDB233C09D66FA73FCB8A36EC59DD20FC91D0F656E8B59CB28F8E095358621263A595AC4F33057FB7DFCDF755F86A2EC45DCFDFD553DDBD1E3A2BD0773C13EA3AEB46D3F7B56A9E78F048D9F73371F3A3F902B8E65375994653049A81ED48ADB92F709FC846DDFFAD576F35466A2C7FA0F90BB56E86ECC37D1BD2861D759F9D6A99F76D5B196E62668BCCF7BB0C19870C86137E10D98FA9209F52191A74FA2B41E6AB6B05991D3D1C7EAF72
	93839C65368BB0D40AB66CA33523B4E107687F1E0E738A9BC8829C508D98CD9747CDF9E585EF7F172F58D574FFE49868B1D847F57029E47ABFAD31C87A216DB16E63293ACA74BD31DE96FB6C7B635D701555BC76A73ED4363DACG6326DF9AFB42D68EA9749F339BCDC67B45F137C19AFF320FAB736F1BCA82267F6ABE1BC2BF9DG3F01D6E07A1FB9512F50DFB9FECA37B11DB768FF2498F38DD6234E7435057BB3E739E71A9482DB5DCB5CC966865B44583C5E27B6B635988DD61D3537FF9C0747ED6C397BE091
	19BBCE04B96594006099785CDD3A1E5463F33778F2E11F45E4E253DE284B7D9D1953CE7EACC7B6CF0D18413E09E5707A194354A78CD31FFF4EF3FD4BB07576E16A7D07290F9826BEF1187AC213677A36E16AAF8FD39FB8CCFD54B0754200677A32E16ABB06291FB0CCFD10EAFDB754317ACA33BBFE1627399B408741E6B8BFE4B0779A480B218F8D9745F76EEBB7ACD3549D57190624F7791BB61067ED136C1D92432FAAD4FF1EAAA8A46F114EF17905058B8472CE1E49E44FFFC1BAA2F350EA9C50D9AF1217E462
	375FE3A7655FFE6D609B79CE9C8C57F0G469B380278226C3D0E07E99C3A703B709C20CFA471F9184968F8D8FCAFCC697E4169E6C2B966E1F7F4F71DC23AA4A8674CF4C7F76AB45255GCD2BDB3A2CB3C8E7821A89EE699E61F481C0936A166EE2B75245G4DDC37F4DBF4C81773080F366C91F7F4AFF03AD6205183CCF8C40DAE2087694220BE5AADDDC7AF52A5C2FD666FFD34953FD7234B57A35DA054CFF4CB9749698221BE6C776E74CEB070BCG1AC437F4F5E711AE778F20738F6E6862B9DD9B507483CC7C
	039A5DBDFDBC2F20BE86A0C924EB88E7F49343895DCBFC8D40D60E7B9745BD1BA68857EB705B66C71972758BFADB256AFB8F5A67C579F342155FFCAFEFA4F205C7C14723F85F7AC9217CFDF1134DE669B552CFEEE25F7E28C87ECE04610E94AD02AB5CA4EDF6F8B8B2B8F0FC68A8A23612EFF2A2DA6C6353CAF7892E5F98D9174557717BAE509BF23BE51FC5EFE801A1E69FFFE5F45CE8A04FE4AC8ED78E4857C104AE5D182CF5930742C7197F4D0FCAF188599D29E16B9DDE9D4038CFF8944713DB6B49BCDCEDFD
	C7AFB53C3CEF48D8BE8453617A7AE61DDDC7E788F44891184C5F52F76BC4D6682338D6620DD3E4EC1C8B6545FF74519682306F36A73B960B348D00337F91476F9FDF110F5F53819F8510884066BFB33A2A60BA7AD99936454C12E52C6D94939C0C6F6D590D18C7F75FAEB4FC19263A06E362522C64450F313856BCA64557AB66A10D9FG73F14DA974518BE8C29EC39F9F69D273B161B10C4D131CCF93817DA5827996297259A3106F065348B791F0E11C6F49CE5518F23E7AB3483778F19FED73634877C40A126F
	B25479BF0EFCEFF13EE04045F33E7B962871957E8979EE6FC63EBA408D7E8979CE3978C77882B95FF19D720582EEAE67FBFA1F9ADF599348372887799A8167F88279EE6C13738581BE9AA069894C3573BDE4CC9A650CDADC1770B85719996923ED09C41D3F28D56A74023A00C83475DB3D48978238844E57F7D80D2FF8965A9A57BB56FE41746E57737E88B26D33D06FF7CB55E2B4FD965A7BAE678B83DCA2677BDB0B9ADF45134877F38372B5836E721348B7E51D9ADFB067531DC53EF04089CFA1DF4CCEB53E3A
	2710EFF99F723582EE82678B3C2296575027B02E177B461E8351DCE7A448CB0FC21D47321579CAECED0CC21DFB7A3F5A98774B433B617A551EB8793A2FAA79FDB7ECCE115B7BEEFB14783DA1CAF95F6DAEA6770589001F9C05775D270A48F7963C6F9E01BB1D4E5C87B3EA7ECDE7A309854C1D4572759B09DEBAF79670FB770FCBC89C27034CF000B9G09A446CF230E451B06DD3F355F61E17DD67C40E856E155BD0D5FB1E73A2C21EAFDDA7AC65CAD7B0D4056B81D2163377DE93646A90D634FD13C378C4F726B
	EF74BEC14A81D0177EE7545F5D2554EF7A33334E090AB263B33B7015AB4A5395FCAC5FF69FA771267386D937282F676FD244E71A509372E71A19FFE65F6063601A74E7DC0799F505648A792619738C2ECFFAE7B319CBBA206C87B09DA09CA09EA0BD9A4679E8EC6F4ECDEA73AC6776F6B7491256A630D8941E9ADB8C2BDC7814B1EC0DF666E3FEA7F5215F7AE8EC4768B15A704A30B6042B5A5044ED1843ED70BEBDB69BFE6842273421F9361A8D8B298DE44E55B59BEDE8BCB3B69BBEF661D35A902630016529F1
	07DA1EC683ED7CEC4C53859A34D3739734F36E986DCC6E9E4E4E7E3F28456A75854486727C6245ED88F411B5B29BFEB52C8D312AB6787176CA62B6F46946E643E7AEFCCA9B3A1FD533E1F1B35A60F896ED486C991B8DEBDC7814B644BC2BB686969513B1CAE58C646FE9D9EE851CD64B2D39A0B3BD86F36BF7FD588F8BE2501F2746684F8B437AB3A1C62D8F1457A86DA4F314A9C09B446D0C2BCD977504F3BB3F5DBBB6BBEFF161D35AA9BC275676BFB90CB664BE07B6FC289F1B8DA7DC7814B634295AB0231458
	C066CB7D5C06F9E334E109E1B89B7CD5EDA8BE04B604F09B6EF311B5B29BE68CEBC3022A8DCF2DC39B527F0AB6741D9D1B8DC3AEFCCA9B9A7F2AE64345DD448672CE24035B1057B7B69BCADC7814B6CCD2356179AB789EA4105B7057B15A906F4237C6D11E23EA03F7B646A1712FB80F2AAEA3380A6761D963F91CB305ECA2638C4E435BACC6B3793E446A8C4564FD3545EA6CB31A9177994575DA2CF41193435DD162FE7D5574927575D77716282F3F8AA9713CE6B42C445D7AAC363EB345CCFCEC837FCCG8C77
	ACFDAF6585E5BF0E8B21772D90A8C7BE0FEDE420736C39D0DEBC876615F390FF1E624978C75EAB10F7A78C7F4FD372B51B04CF5E8E04DFDED64262E16A457B58E9398E65BA50B48F7C990267354984D6D3190F3E3F14465CE6B3586884193C5D936BDE35AF97D4562FFD2C9BB088783D7A87DD8BC51A7DF64B3ECA816F096F1410FB084ABDD13687EC6E899C3FDB6660BB4397AF13F8FBC1F9B2C090EF0759D714EBE44321AEC60CE30207F8EADEF02E4B05F21982A72EC95E6CBE17B5256A393C281474A11BE548
	5AEBD066EBD6E9AE2F131EB68E598D83A4046277A34AEF57D97B8CAE793E0E7233BAF77C1BCB5D758956BE1D27646DD345F3EB7EA9E5BC9BA1A66DE2DC7ADFF09F4F4B0A3AC9D08ED060186E2527653A2B396E83DDCA5DA1409FA14A1863C177DCC5DD7A0B10B7AF2A69FE58C9F78D571D61223B82781BC5999D625574222B8DDE8A1C9F1483D5F5DFB8A357CD56F630FC3FED8766FB680B186F0F51F761F3213C78A59FED61CB186FDA0D524E8628EBFB0969533F249EA387603D95F57ED08EFEC94D4E52EE391D
	5BF80C5EDE60525700BFC614112022DB938BE3ABC0D92CF3DDA3145B817AE5F8267BF9A75D5B396EDBDD5A67B270CE927983E2557D8E81FC0422EE8E1467462A79FD33CE2E3B0E6B4EEAD66ACEFF995A69E5A62362E5F55D4D006FD05419206C75321A6E03BD0BE43A4942B3169BB127F17E6477B26646D375A4B7A2209C8B1074B266469F7A14F6663C6279FE02D08CF48DB25AB6782D87F03C22EC1FC53DCEF6D6221DE7EB70F94B7B9534F3160D5819G6574D7FD34852F221DEF564A67AFF500EFFDD576AE6C
	D5D71B196E071CF5D7E17BCCBDAC17E7877E09B299D35C4A3BCE6FA42F9A657D3ED4AEAF8878E782C40BF2625D4A5BE6F012D70372369C124B9362FC34B9G45F1CCCEC31CBBF92FBB4B2BC5F9516B64725A00DF8F609065F83B15771333CE7236213C2B3B64727C01BF98A09CA086A0412D3CA3FDCE7236233C58AB4A792966B5C9C66EEB6E64AD6F17773F9D3C7F5D14AD17D7867C0DG6D221CFE38DED648E477403AB272EE54414B8D34FCD14039082109668B3D0F6EB76B4509AE7B5E3C0A4E99ED9AC128CC
	C67B7EFE124CF3CD3A0B16A120CBFA0D3E1B6A44E762C9D08E84088408FD0DBD43B3BD3F38C0665CE72D3A8138859F9DB4748A512F53EFE9DD485B8C658EG8740E4001057713D7F0FABC99F92175951B912388E7EC21604278B0159D7D734753BC7C8DF240B44E535AC1E6FBA6DA5AEF843C7DB7306943F16B7DC5B073DF3BEFE0558527586FB673C1063FBA95EA14333F751F1F44EC647AE28CBFC8367596F51E7212EF89F6DE0BC621A2F526F81D07663383BA84E9F4AA19C476E1591D00E61387A2BB8EFCF60
	38D8CA5778A64475CD44EDB805FACD9C57F78A75FAF1DC78A9546B4DF19DDC2FBF472D65FA03B96EDE2ED77396627A3BD0EFAE47CD6FC23DE59C37046B2D63387F2BC33D2D9C377B8A6A6D66389B37235EA00E2B60FAE7F21C1D6222219C47F1333BD06FDC0E0B5EC6F069EF435C63ED44B5EEC7DBAAB86E1EAF5016E60E3B0E5B6277B60E79F76E1067DCA060C35F1672AC72EDF7B93707FE4F08FD1B651616639D8B88DE13A061D94E4D2273A6D2AE043A0989E853967AEEB69E4A425F90F71E62F220DC48F1CF
	D2DC8514DBB86E871DB40ED0F6F01CD9CFF05ED01E42F17515980BA00EEBCB276BCB7E8E653FA3AEAB8DEDA9633813E9E8CBA3477DB68DEDE965386F5279C0B7146D9C7754E934EFA2477556F37BB86EA24F57A00E3B0B52896F40BD699D443DD30FB697F35CEF3A5066860E1B5705B637F1DC77A934390B636E5901768DF2DCE7975AB701636E644F5F13B9EED7975A9247F1554DE8CB9247DDE8C6DBB267729CEEC6DB72B9AE470036D4F05C34A634251963A6E720AD9D9CF7D3A95A92B897737A857ECDA394F0
	11G31F325DCCE1C6BAE2F3F3D13646962FF307CDD4171BF2278E6991E6575ECBA67A3654BD0D777FFE853EB743D70DCA8AF7EA762EA68DA2982A8D7F05CEC5A1F1B215C45F1495B5147C10E7B45B2028B03F294477D0B6262201C74CF747BDC0DFC2E191988FD8820AE5173FC33956A3B95B4F6A8CFD460D8AC8E59094FD392D9AC16F0FC8745074970ACC64F5079A6A947C2DD5B3BE83FE69B46A314637A4AB0EE119C77E09946A316639EB80C3CB57FC25C0FBB1137056362BA11370B631EABC55E699C975D60
	5A8E73F7213CB0CE573A8B65C5F1DC73A114D7789E6F179D2E72E2BBD0DE9D275BFA826535F25C5B6BD05E940E2B3E64AA2F6B924A8B62F4BF3B0472E6F25CAD3BD0DECE9262E6743A4A4B26E3E7B15054F1BA079EF5B4F35C23FA5E87B90E4D9D88DDA0479D6063DAA84785E623AD0B7F0D38D75A103700636EBFCEF0B5D0EE66387CCFD1DE9B47E968F78DFFA8077C9B570FD4F66239C19100537C8767FB155DCE5FFD48FA0A7A7A03D623FE2F2E2F154E9E457752B4E788FF43FF701969E7D4269D4A5EG817F
	41BD1E0E0D3846EF7A1D378B2DDEFBD44FBA1A533EB1B974BF627B127FC8EBAE5277609A3FFFF4214E7CF9B0071E4766C57235A7E47EB8AB193D57E675471C6A493C131C03A055FF5B291E587B20937F2F1D6AD3E57C4C37470A50372447028453AF3738BD47E94ABC66D318F8F5BC31CA087B1F5FEAD89BE552F73A09D037D870955B4DD0C86F81D0EE663858A2EC5FFE40095FA72F10F553A1D00E82C8GD8BC9F6B0C340E2C170E9157B913B54C6239BC17F19CA4741E46BEA590BD9922CE44A5884EB2180EB4
	7E0E535D19976EG5F7526A8E43ABEFBAD23FDE840EC9830180D3DF57349F9E8A59457CB7AAA14457706D7497AF53B41E13F8CB838EB1257F738CF6AAA19F78D689C460121813AE919490926F24260F7841793320348B9EBB15B994D1F689CA191FEEB12FD356D4984F778EA33875FD294D083BC84160B3953823F49FB67F7674D1B47F43E37035AE7B4AB6C4B26731DA9C09F8840F02DF49EC35EE7C7810E79FA8FF9DDA8727C0D5647C2DD8267799675A1BF45D7DB1C02BEEC9B5607D48FBEF424C8BEB09D4FD2
	9DFA408F26205E1BE81F6EA3BE24202D2F56CA36EE2336CE073A904E33164A0905F2823775581561ECCD73E0EB01C6E9EBB01DDF14813ECE03FACFD0DB0D44878D66420DDD4EB935E83BE4FBB2355D8E34DE1C7E6DEDE4FCE00B6048329E1DD98FE546FB26CE62B5D27D5301AFC403BE7E7E0B61FCDC684147B8979F27D19DF381AF24627872A00DED99149BB9EE8FFD2E50C35941F18F2562B8B4A595471AC13ACEB69E4A0B9778EA4BGDA818697E07DD5DA9FAA6EE7A0FB9548BE89BFDE7F0C4BFBD2B990F999
	22FCBEA79365E3F97182776318F84607C79DB83E48479CBC17C2B9AE39EB03EC21B84DF5BCBACEE378A8C070824C0573E94E39F39DF9EDA44A78F31A7AF8749E15B393E4C4F2B92F1FA6B9B5E8B51CB5D82D86BDBBF2C1248F237769A420CDCFC37ACB754E63578F68DC25866A9B537878D569A957647D1E40A07091A892F3ED827CDEA748472FC5BB55472F0B7499F39250CFC943FE3607466FE50070B49C936E28177A46DB34BE9A6AE239CF375167C81B016C4FB36899DD8845117A4CF4141DF2064E2D20DC46
	F11368985A81E5FDBA7ABF496358172A080152FF3FF4257FE175A4378CE6324DCAAF7A71CCBD4EE77C39AD6BF792DCB01443B8AE160EBBCF105CCF47F8046F1462719DBA4EA4C1DD7AC26409267D38864A0D9CB7014AB1C15931907DFC30EBB477A9251F01A0E73FA09FD34ED29D41008F63FA3B68336CA3447E05E83F30CB32FF7EFEEABF5425AFC21EDD74D937864A0D9C97C51FF5CDD0F6ACC27B6F49984DBD4A457EC5CA7B9F204FBD41008FDB04E3DDA24D23F8A827EB91370DBE3B66C3390263FEF314605A
	215C4FF17547F01C745662B8773D1D6439BB864A42E2DFED81C0A3009EE082C0GC098409C0074A53E5A42A548F79B65A3FB199A81578DC076BFF941156C177207AB59DFC576F6117DC9E45DB763FB3EBE882EC49E11250CD100284753DC0C58C56CF5D7CFFC98EEBE279F818D0903A07AAFFE5B5C99A430B501ABD46849FBE2CF672091D8A2F8BEEFEA38B587841467322941C2B15732849C43FD69B79A1D4958E7A63B2DCEE03EAF236FB842A066D1CBF07C1A3E4BF9CCBEC5E9048C3817E6A0CDF42F133C8E14
	17C01FD39B002EAD837B454A6D1E7AC51A6893768DAF60734B10668A01707BB5384E6363D778B257716B79E5B83E86F39B336833ABB9BD17D0B23A62EC241B19017D62FB741BDB941463B8EE559602DB3C947A44D2445DDA4E1F2DB9AE369C7BE387478D9ADD1F55EFB5E25FB3F1BA8DFDA7928A65282518E31F5849370CB8A8EBB2C1B7C0A3C0B700D7A6521C26B481D01E8990AD6EF90C055FE44FA023F94E56CDE2A972ABE35EAD6208FC224BD3AEE9D47865ED17A5A4D2DDCB847567AA2D0CF6383356BC4DE7
	D87BED216F20B4D970BB8B47629F15E18EB7E6E14EC5150DE64EE082BE2FAC59BD93FE6F92F30E69F8093615BF6027F33D3F6FC43DB1DC6F3C4E515C2B5317B96B4DDF26543B007AD68C781AE528F773AE542BDF06FAFBF78D27F721CC7724E54EFDAC40C56FGFDC7968478D02EF74989549B4F759EB8B11C5EB4195E5CECE73DE559CA3D396B090EBA40B7E7235E6BAF21DEFBB66AFD6852C8620CFA7D95FA03DD747E0E4594709159B8FEEC56BB4F09B7102DBFE29E7ECC0FB6A5F11B6668C712F3F8AD4EF136
	29A1C7E9D361D554511203BACE8DBB9714BFDB5AF31C73EB222863BD2EE3969D8F7DG6F1F0371DEFA9875C666E0AC7EF45CB996376DA131E0B6D5FFBA5C3CC7BE3E7BA94677A450C12E527CB42F166E8136D88D6408F372D04A6C2A266F8E53177BEAF31663D8383DF3EC67C06F9FA35F55D17331F76D976977341A654C57799CFF9D45B74A70629EBE7ECE34956A02G58BA53890DE46C4FF99F66FAGADG5DGDEGD3G58777999E5643CA50FFBF55AAD032D064F8DA659B2D0665F3FDC76A98D876C5D630C
	B4FC77987C407F8AA1B73CB95F5D5E692077597DA4D23CE67EE99545B37F566FA57E89ABFC3579G0DG9DGA60089AB503F05DB4A18FFE41BF7135D2E6B6D27BB4851B93229DBC21305826259DFF45DC10F45CA978132F5873B79D99013C1FE90C0BC00106BCB57220A7B948BDD57AB5CDC08FC39C0CB56ABD44035D964E37D623D22515C8B3A00F7B017758B3AF79CFE13F5224F71FEF169B819E3AACE8B9467F719C32EF5F86ED4F63AEBDD35D8372E4A358E4F8AEA3CCA2E1BAAB709ED6CFFC13EDF31EED129
	5B7D0A13F2D95B860857D8A1CF1471C427DCC6668F17A74FF7A3A3C6640D932F647DBB9BFB8E12300AB6FEFF87D933EDB6DCA860DBAE1B0D506C64E82D626D95E585D5D5A578CE2B7A1044D7FEF53D126FGCCF80B576B0DBA1325EFFDD98170D6D6143033BD491C1F4E0145FFCC5E718F841E7FAFAC467C4F1FB6D5E8FE664D799B5C78153F1279E3DAA979955AB5AD19495436CAB686F60D44B7C65BFB0A36D555A6747593C94EC72308116FE109AFFB44B1E23C779E11E27374D1C94E1B1DA377A3300B7AD10D
	FEA436CBF25E289B399F6D1FCAFCD5D7C66747058E490F3BCFC8F2FEB98ABF86199FB568472C13121C47370D5C0F9BCFC9FCD33F981D9F55E7E4395AAD49C9B8B1F2BF22F6380E19EF70BDFC81AB5918998A57C8G32061F719D57380E19839A640BDB4946CCE1955C4BD7B1BEB6E6BE1DBA1AF7A8E5405B38CA9AB35B61771BB0DE3E47474C50ED64CC8A5753AE513F05EB32657552E914E2FD0BADCBDE0F27C572D8EEF23CAF2FF7BA5193E9EE2BDCAE27918F3DC3B9EC5FFACE1E5AB98B5D2BF07EB520217924
	47FC3A28135A535BD3DE56D6BB6553149E092FD7BFF23EC43D44D7DE7955474E3C33526F5DFD126CFF2E9A09CDE20E255118D8B1A626FEC94E1727C7AE670953F4EEEB9DB208E320D11253D1BF72983DF1CE629B98655819B1A0713ED5CFF27C2C4EE4B37098D51A257AC6CF778505EF6F31F1288FE354EE116484F83AAFA8FC3BEC17785AC6241F7175DE1078FE58B53A185C6010F2642E0B121CFBBD5D87947E5B1A297FBD68FF44A5C94EC243487DF87B8B094F3FE964FC9FDE1575530C5179EF7ED2725FF715
	4C7F14117B3F740C7B6FB145747B039D4698AFBE4EFC5467BA5E6B7BF05C1E3E0A0D77B3619ACD001F677264D157717E35234817300A0D7769F9B02F4FE3FC62371DE3A3F9670FE3FE8D703664C963FD977C1E85E37DA3FC3C5FC367736ECE98E5B4BFBAC746A4671AD6DD0F41A4466C52CEB2664B4F65E43598D34B65771DF448A911667C86B21EAB8EF925E244F1EEC98E4AD090317ADBBEA173F269ECD239FD1D0DB9CE7AA5AAD67F0F02BC27FA17D306C5B92F9CF61A7B3333CC703B61CE32D6035CAE65EB26
	593DEA2B53B9BB7DE2FBFA43D5BC07F23DDA3DBF2F2FBE29D69F4C6B272FD42B8F072B5A7D0459DB52DFGD75E9F6049F750E2A6C72F34FF52F626297550410FBF5A7FF13BB8CE67CBFC0FEF7BAA77B2A6A3CD3FCEDDF76B7E836D12F2C1CDC6429E7777482AB5120E7FDB0677A8B14F331D4B81B52E7D76B7FC1FF9CC9E6B37F3611A1E6FCB3F47B03E4AAD2E7D36E58B72956433FEDB8757D611CFFCDFD64E5FC166737703DB6977D3A8CF494777D5C1C6CF7DDB3EC6006EF5873E18FC29FFA7406FEFC35F1E4F
	6310F7D87D5BEDB5FDE7158474692BF1AC7B470225E28E33732565FD68BB39D8474E3E1657352DF66EDF92952B7F6208EBBD53406AE777E409326959313D7D971D470FDF572EF06167274C0A73C1BF3B328F1058572C46BE22BB28D65F426B5F6CD22B6FDA0D5FBCEFAD10F22B33EC24FDC03C7F140D242F0DAE57C357C93FF38BA55BEE6A9C1DED4FF50E55B626FBE711E44714E2490E0806515951346B2B59713D4D129D2F15C9F65C53B1BABB8ACFFCB5BB9617CBF674D7C8F6EC3AB4BABB26DC7AEAF65CD8A9
	59712AEC2D5CAFFBC7E747C37A2FE64722DAC9774E2BDFCD5616ED12CF3754C9F27F5236CE5D27A6F2038617AAC517C47A0D07C7E287539332C35239E62724B327A647D90649E2AD6FB169FA4F317742DFF691DA76FB226C77FD1D1E56B0FDD5E0BAA246D587FB277C3DC677FB537D9A5577267F30D17D1C857FC64FFF67A630517D5F393927D17EF7EE58BB71F76979FB03B0F6FB8384814C8408DE0D6F43DB1AC89FA0FFCD5175DD78FEFD9F599BCF7E02229D75176EF56FEB555E617F6E624E3D647BCC0F45EE
	378C907908EF5ACB5A4BEE9924C806BB30174448EA6C6B37B3D2915F3A172E1FB11C25E8764D63AC7F3B15F3413732B53E62BA7087971101E7C4E727369B6DA643DEBA8BE26B346F6667266AC19EDB9BFEAF7DDBEED3209C8C908140747F26903F3944925A353E62D9BCA9C538B62AFDAD4A2824E735FBC3B9GA0F42D5A5CD46DFE07FD7FA63254C7EC5F5D87687A83101130965B74393D25F0BD2E7B4C75C4DBC036D9AD03862B7DA243521334C5D9BB8E8ABC469B76953913811971817475CFFABEB3745A5BF5
	FD5C96FD8B192FE596C07C815AG7A81AE83788184820482C4824481886BFC35B9G45G8DG2DG5DG76F568470FBEA871600738181FA628D5474EC7917F06E9BD6642C41095368E5B22339E5BB3F19D3605F78A212DD88F4F06G5D6B31BDEF51E0FBBAG97349EE5BC2741760CDB0FB234290436E0834CE5815AB60C36BD9B6937GA67F433CF4112EFF834E37FABF94849CEBCB7B8A84374F6F5555F570GDF3D3D0479605D07F108GD91AC274A12A8F6350D008BE249F25FB9E206C8D90D008F148EA47B8
	0482EEAE17F1229D63D0D604B23ECDE975D03E8C60D7B45AB85CC03FDF08E7141CF5F3C6891D8E0B7B5D69B7DDB29ECDAFC23B1E20EBG48BCBFF3A362BEBB0938960EFBE9A572FAEDC4DFDF2A41F84582AEFF93521656E23C5AB7212FFDE5348FC3B9GA0F4934A985C02710A825C62E2149132856355DC0CB2DE213417216C87B03D981FBDE61713B69E59BD792E0F245CB098573A4F0D2AAA184C6C28A9D1FD96F5BF370E78D87A7D366C3BD030EDA43A186F0F5909DC72AEE9A60F47640308ABAEC1DCA1DD7F
	C946FC6F929C8366B7383F5F24B78CFF3F49EAE06B387A8C6D978745F3D95EEF2067A71DBDEBB3707BDADEC3A6479D3235D9ECC632321D4DA1CCEE7F76B2424C9235BDA3170665D31E99A54E0BA85F1C12116DE5198BF4ECBD40C6FA8FCA5F2CDCA7301562F3B6AB578904EC40DCAD003A891BF1DD440C42312DEFF8E20CFC69C5E3DB97F1873D0F463978FBB7454FF571F7FB917AAB147AEA752518E37B8E9019FC4E45EE09E2DEFCF0004C1F485F810469337192BD027EA8901A7BD83D2345FDFEFE59B2FCFECE
	ECD11F8F7934384E07EEEED11F8F5D5AA21F8F0967673710712A8F6E119D1CEEAA471D6038C0AA1360BEB56A6D7D9CFFB7350B60DB8CC4A1620329DD97A82EC3447DDA06BBA1629EEAA1FD8CDC6E377073489EEE21EBC30DA693AB7F0152984D5065F8BCA2280D46C11D4C6ED99CF702674C4A7A317549CD4372297749EE4A97DEBA32BE99D30AFDD220730804D2E50EAE247824D2E50E7ECF8A66E8FA192F56D406FD72C71A31752D3B46487762287918DFDF5239D0E419525F09949FD32674B7AB957D0D033A56
	AD68EFDB1E7239AD886AE6EEE1710F5E625A8E7100934AE1CED96EDCD78C658600B6991E69789F17B37CC616C37E4372296750EC4A27AF9FD98EB94AB107AE533919F739B22657D37C64F2E5CCB30EE2CC8320AEFDAB46F4C57B58F221E80CFC27C74D477CFA0F4EA3CDDB157EEA0E51B3CE36AA7D7D9E1F23CE003AB86E6F39DAE58E35D6409C3FC2BD7EF699FE2278FB0AF88D026B4C8A35DC08DAAF77B19AE8628104CAC61B835762CAB5BE0B535C2081E85AG74A22D832E5E2AFCF1AB15BE7983DD3002B63C
	5255479885AE814A1AAADFEDEE1532DF3CD6B336FE714FE179547BC585BD0B2A2CEAE47D22318A7B4533F48E5EDE254C13D80A6F2ED26649C0996609896AA22BB0CFBE5FB2367C1EB0C63E87C74D477CBAD9CE5701D6AB7D6D257846EA253F717CD924956A022A515FC83B4B99E5B51027B543473FC1C15386E5BD0023C619C3BFBE2854618DB47EA350912C208907F28CC0020B0E92277354B535104BGE5350C2E3156D597BBBBB3AC1BBCF7984DA62359C0CE1CBFE8B08BF8CE699F6933E2BB701AEA795E278D
	846785E53FDA0CE1997D4E40FE574AFEBF34E17C5FE9BE738D6818D6A47994AC7B3D2BE87CF59F9FD79D62D9B574DD10BF34E788EF671C94020B00729C5E4E8BB4126FCBE53FA7270CFF9CFE718D6838BED57249C776FBC56A786B2E98D79D2CCD2D74FDDABC3427300D6FEF24389CA897F2DCC1BE41D5C0390563FEE7A538AEA88FF21C56C6F093206C4FF15F277B0A0221BC1363747CCC2EE80E7BA95D27DE3C9D66235B91779A4535C1391F6392687B394BD0766338C9C788AE904A215BB1B72FBBAA35570D32
	5FB9C7473F6D36FC83BA669F13FCDAA47BFDF37B786B1EBE2EBA443D033538A7B01C37F3B44545C0B91E63F6517CCAA465BA445D444FD3AA63387BA9EFA3143BB9CE3B8D73504EF13B68BB5219D00E6638B756233CF80E0BADC67B049D085BDAC27B8E148BB96EA4BF332902631A6839C6DED07663389BCE220EC00E3364210ED00EFB0062A221BC07635666230EF80E7BBEFD6F51D08FF367FA44ADDA0572749CF70C629CD01E48F18FD01C9F14C3B8EEE99E5A97A1519DC13DF3B8AEE6B39DABF67AEA1BF7A22E
	086E376E003203638E52767006F2G473DCD69C2209C31937B718BE5D2EE3FAE7BEDAB9B7FBC777E86F4F417CBBE99E53F7F3EE57CF5EB4B473F9F7F5D0EB9974D5BF957903D6FC0B90963EE9F427CCAEFC05CFA3E37B71F636E235F74CCD0766238A40A7303F2B0477D76A09FBFB8EE916D93B1D00E67B8CB8B6ACD64389772D06F62DDFC2C20795A8565C10EEB2FC53D93B82E113E271F8C65C00E7BCF915AA21D6B35097EDD4FD0200960F45E9B79FDF197FE6FF861A4F987E9A71FDF49DA858157183ECB470B
	625D3E5A1A5DC8FBF8C3AA528A78DDE219EC5F09CF3D3A2CBB69FCCF8FF286392C5294D7D935C7A4D95F21B1F2BE7F441F621E857067F21E4CF35E5F58A15B3F32BDDF7D7C13CF68F89684B2C2391C59F43E61FC76C78245117AB8CE375E66FEDD6ECEBABFC984DA4D9E247F79B667BD798F53FBC28554B76C4135F6DF8CFBA60FFCAF2E727C89934839A24877E17EEA3B6BBE7B473763B7B08737CD28D3DF1317CB4FDC75863A491CB62DDF3DED5752B386E682DDA4277D599157367D026E87E2BC3F2A45B508C2
	A3BF532856399DFFF2CCEA4776ED64BB4067862B5D582BB31165F7780D0849B3EF635FC39BF1CD638394578565FE2E63CF35D9B2B9CDF88682370B7D6D9EA2E7F2A3364D7AED23B95BC439C7B69C64B48952FAC970E53DEB5B1CDB0FF996456D4F214F3D31D0CE6038A5A5983702A67469C009F35CE69CA7F1E3361F773817DE39A7C7E9FBAB685869E4FB47BAD75BAD6B50760EA634739EBAF674C3594EF113735076GEE7BACC55F0D7DD432FDDB7EE84E62D25A9E59447A07E47B4FDBDCED1F59023647F0BB2B
	D7211D715CCE53AAE7BB53E4FB4BE62DA25F197AADD663A568964E5909F9953F8A73AAF7AF6A292371E801F27BDE4C35D9C7C6F3B617526789A0E729134F6FECF675F9D1A97ABCE9AF7A57D01A6D640354B344F327CA719B3CBF67B9DD2BBE9EFCCE7BD08450C5F35A85E52E6341661352F810B704B1CF5F07BC6DC34EB17F28D322FF0C5257GED63BE0C5D8E0F6B1515396E25B8B7E2F09F7B9EA9456E0F87DCE37742810C5DE5EEE776C14F315BF290E3673D8F5BBE6EA07A9B42652CEBF176F730CB72F7B24D
	53D820CD607EA6660DE6DCD27ADB506C2BADF172F7FD2D2B3FBBEA515F32E6347D8ADF0F51500C36FFFA4A799EB7C067A87DD0EF67B43BB7BA7BF773A9292FCF5DB81A3E2E74A5GF4E8C59AF12D449929FF16EF98411A4C1A924E7B1DDE09B7BDE5243C0C7E7EBCF25DB400FB16264B765E5DEF9D795E1FB41B733E36A78C121C9BBD6DC1D56CE1AA6D1778BAC6398775E359DE3A586D2E7B5A4EFFA6557F54533ED405CDB74B764BCDB8B23A78BEDEDBA08C3FB60751DE3F8D77F660DFA4BECA7E47A5153672FD
	C03A7F887727FAD032EBDA6D6856714E2B9D1D2E6367A5DD572DF7EE6BFBEDD2DDF471686CA8AC99C9FC44FD5E32FD01B72CF33621D236772FB7EFF4B65C13B7F29B5A9D121EE7DB1C7745FC30EA64ED1D12B73A78874976925EBD129C2C10460727E47B895715BA5BBCD03A56339DE4DF11A8E72BECFF61CB25236B87D5C3A30F7124E4C94F7B465179FADF0A441BF84059576603A371D56C9F87C7EE6F2A85124E27C71CFB0CF7D71A445BD66BEC2FDF6D485B665605121C3F970D4E06D032F4071EB3874F9B67
	BBD642DD60CDA155AEACBAB0E426873696D44A8BD572C2353CD0E3975268299E64D7BA6EB5348B9A326B48AE24524D0B40F0F42887BC3053BA41AEE4CB1B868B449D0075B62B304CF5E4EE72197D4C74B34DB949C272A1E1C1BFDDC1F4C8C83DC016A2B948E8C8A6368700F70913670F5CD67BCA44DF0F5BD2048507041564264F769F4263231E7C0D3C6386AB11FEC0D8B8C87E1E8CF92CBCA4E8EC7412EA33DFB499DA051C33C78C67078CB6F852EE4215DFB63B301C7E79752DE4C57856961D2D1F9E7F3633D5
	5018F5836072B2D6ED52197B3632E5E0BB291D731BC8FA89820DBC581BCD6DA5D24A39146C5FACADDA77C1ECC80A10FCC0D8E432687487F43DF60B751030505E8FC16A3718740E41BD0DD4D07A85DC331A826272387B4459F2B24167C61FF37C58FBC95C128350499CD6E1AB25A4F31CF2CE215F572F3302AA037590008ED496723AEF2D31EFFC7C600A2EE4E13E6864F9612A3054814DCA83864A17D21144657283E0357DAFEFBEFA7B338D897140F2C0C825FF7A9D94E7CAD49F8F117DECB7CC3FE53556EF4E0D
	442EC1929E9B111CED97961C257B6160C7B33D6FA3FE215D2AB35B4EDA2C832034C061B1D6F1434BEFFE3462163B7EF5129AAE315A05352C0900F5EB1F41B23035696CD923594085C0FB017855DCBC21A9D750ACF35C347C03276775C0FEB58BA9839595747FCA7AFF95793F12E2AAA926328A9AADC17078CFD96FF0901F739DBC3A4412C3F4A15C1C4F2B6BD3DE1A1A12ACE820CB300501103CE4F59FA42F157510943BE510651D5851494B09D49AC8AD6FB52E25A5AE19CA30D92EA9C7504BE9658AC2CEFE2C62
	6DC5CA2BE965DA7A7F7A31F9DF6A205C05A2923DC87E6F7222C672C25E9642FF17973BA4AF70CF1C8C579635AA25EB4A27034EADC300AC71CE9D560BEB5827436ABE897F4DBEF5BA7B14A2D67ED73713C15DA735F63A266C9E106CAE9658AB9632A7C7FB6DD9EA17ACCD96AB48A9A7AB2FBDCB3FD037B4795A3374BA4952A5E2C5AA37691A3274BB122538E5093EDA3D76AC1DA4D91AAA33F46135E7698F54E33A1EE3B708D8123D6A111E6F46FE0DC77B79F3815BC1424808082F49311FBA8FEA9922E3DA18FCE7
	B8EFDA1FFF470A7F1769DBBD14AC249FA00FA92641FE5DA1719127D55076505937418A34F6A1D3FA4C203C700C185C8B8F10F9923E1760D96D51C1DDAFF90CDC2857D94FD18944241FF58A8B4FDA7BFA28823B105CF92093545292293E2BD5D8E46FA7DB3049731CDDD8A109EEA39BDE458A7684AA4C8398247F383B649D825EFFD21DDA97BF86C9F759B781F144A9D7891097989ACEF39AA00653904CE211EE03B38DB4A6FDEFG71D8A23E81A820E7565905B4716CA43B30C82C0107E888270866AF8C6CC286FD
	3DA0C3E4619B8899AEDBFA77A043AEA6AFA830EC97525989AB7585201E9D4682B4DAFEECCAFD01AC1B3BE966E40CB41B3B55FA23BF796F762FA9FDFF6738162D8BF77CF74D4CA2254100FCB4A3BD231EC7D34D6EEBAE62518E55280EED6C7D5A2D7B2B73DD84B7A094DE4BB63F6CFC0FC61B97DD4BB647CBB6AF14597CF55E173FF61B5F11ECDEAA33D93816FB5B3F2E655EB6DF10A2CA6E54AB45CA75E70BEB4466B4D71B6975DA36B9C332F923E8B36DED6C7C6E12AF656712A7DE3F586D5966E127591E242853
	52795D6C6C947677DDB34F78EA4B4E603E74699737886E4FE0B9BA20B3196CBA5339A6331E1C473277225DA01D93243BC4645B10C62CE4F57D17085D1F49F87EBFD0CB878834810CD9EFB4GG443FGGD0CB818294G94G88G88G36F4D9B034810CD9EFB4GG443FGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG29B4GGGG
**end of data**/
}
/**
 * Return the DivisionAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDivisionAddressLabel() {
	if (ivjDivisionAddressLabel == null) {
		try {
			ivjDivisionAddressLabel = new javax.swing.JLabel();
			ivjDivisionAddressLabel.setName("DivisionAddressLabel");
			ivjDivisionAddressLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjDivisionAddressLabel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjDivisionAddressLabel.setText("Division Address:");
			ivjDivisionAddressLabel.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDivisionAddressLabel;
}
/**
 * Return the GroupAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGroupAddressLabel() {
	if (ivjGroupAddressLabel == null) {
		try {
			ivjGroupAddressLabel = new javax.swing.JLabel();
			ivjGroupAddressLabel.setName("GroupAddressLabel");
			ivjGroupAddressLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjGroupAddressLabel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjGroupAddressLabel.setText("Group Address:");
			ivjGroupAddressLabel.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupAddressLabel;
}
/**
 * Return the JCheckBoxDivisionUsage property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDivisionUsage() {
	if (ivjJCheckBoxDivisionUsage == null) {
		try {
			ivjJCheckBoxDivisionUsage = new javax.swing.JCheckBox();
			ivjJCheckBoxDivisionUsage.setName("JCheckBoxDivisionUsage");
			ivjJCheckBoxDivisionUsage.setMnemonic('d');
			ivjJCheckBoxDivisionUsage.setText("Division");
			ivjJCheckBoxDivisionUsage.setMaximumSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxDivisionUsage.setPreferredSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxDivisionUsage.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxDivisionUsage.setMinimumSize(new java.awt.Dimension(53, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDivisionUsage;
}
/**
 * Return the JCheckBoxGroupUsage property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxGroupUsage() {
	if (ivjJCheckBoxGroupUsage == null) {
		try {
			ivjJCheckBoxGroupUsage = new javax.swing.JCheckBox();
			ivjJCheckBoxGroupUsage.setName("JCheckBoxGroupUsage");
			ivjJCheckBoxGroupUsage.setMnemonic('g');
			ivjJCheckBoxGroupUsage.setText("Group");
			ivjJCheckBoxGroupUsage.setMaximumSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxGroupUsage.setPreferredSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxGroupUsage.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxGroupUsage.setMinimumSize(new java.awt.Dimension(53, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxGroupUsage;
}
/**
 * Return the JCheckBoxRateUsage property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRateUsage() {
	if (ivjJCheckBoxRateUsage == null) {
		try {
			ivjJCheckBoxRateUsage = new javax.swing.JCheckBox();
			ivjJCheckBoxRateUsage.setName("JCheckBoxRateUsage");
			ivjJCheckBoxRateUsage.setMnemonic('r');
			ivjJCheckBoxRateUsage.setText("Rate");
			ivjJCheckBoxRateUsage.setMaximumSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxRateUsage.setSelected(true);
			ivjJCheckBoxRateUsage.setPreferredSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxRateUsage.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxRateUsage.setEnabled(false);
			ivjJCheckBoxRateUsage.setMinimumSize(new java.awt.Dimension(53, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRateUsage;
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
			ivjJCheckBoxRelay1.setText("Function 1");
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
			ivjJCheckBoxRelay2.setText("Function 2");
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
			ivjJCheckBoxRelay3.setText("Function 3");
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
			ivjJCheckBoxRelay4.setText("Function 4");
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
 * Return the JCheckBoxSerial property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSerial() {
	if (ivjJCheckBoxSerial == null) {
		try {
			ivjJCheckBoxSerial = new javax.swing.JCheckBox();
			ivjJCheckBoxSerial.setName("JCheckBoxSerial");
			ivjJCheckBoxSerial.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxSerial.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCheckBoxSerial.setText("Individual Address:");
			ivjJCheckBoxSerial.setForeground(java.awt.Color.black);
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
 * Return the JCheckBoxSubUsage property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSubUsage() {
	if (ivjJCheckBoxSubUsage == null) {
		try {
			ivjJCheckBoxSubUsage = new javax.swing.JCheckBox();
			ivjJCheckBoxSubUsage.setName("JCheckBoxSubUsage");
			ivjJCheckBoxSubUsage.setMnemonic('b');
			ivjJCheckBoxSubUsage.setText("Substation");
			ivjJCheckBoxSubUsage.setMaximumSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxSubUsage.setPreferredSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxSubUsage.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxSubUsage.setMinimumSize(new java.awt.Dimension(53, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSubUsage;
}
/**
 * Return the JCheckBoxUtilityUsage property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxUtilityUsage() {
	if (ivjJCheckBoxUtilityUsage == null) {
		try {
			ivjJCheckBoxUtilityUsage = new javax.swing.JCheckBox();
			ivjJCheckBoxUtilityUsage.setName("JCheckBoxUtilityUsage");
			ivjJCheckBoxUtilityUsage.setMnemonic('m');
			ivjJCheckBoxUtilityUsage.setText("Utility");
			ivjJCheckBoxUtilityUsage.setMaximumSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxUtilityUsage.setSelected(true);
			ivjJCheckBoxUtilityUsage.setPreferredSize(new java.awt.Dimension(53, 22));
			ivjJCheckBoxUtilityUsage.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJCheckBoxUtilityUsage.setEnabled(false);
			ivjJCheckBoxUtilityUsage.setMinimumSize(new java.awt.Dimension(53, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxUtilityUsage;
}
/**
 * Return the JTextDivisionAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextDivisionAddress() {
	if (ivjJTextDivisionAddress == null) {
		try {
			ivjJTextDivisionAddress = new javax.swing.JTextField();
			ivjJTextDivisionAddress.setName("JTextDivisionAddress");
			ivjJTextDivisionAddress.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextDivisionAddress.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJTextDivisionAddress.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			ivjJTextDivisionAddress.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 63) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextDivisionAddress;
}
/**
 * Return the JTextFieldSubAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldGroupAddress() {
	if (ivjJTextFieldGroupAddress == null) {
		try {
			ivjJTextFieldGroupAddress = new javax.swing.JTextField();
			ivjJTextFieldGroupAddress.setName("JTextFieldGroupAddress");
			ivjJTextFieldGroupAddress.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldGroupAddress.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJTextFieldGroupAddress.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			ivjJTextFieldGroupAddress.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 63) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldGroupAddress;
}
/**
 * Return the JTextFieldIndividualAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldIndividualAddress() {
	if (ivjJTextFieldIndividualAddress == null) {
		try {
			ivjJTextFieldIndividualAddress = new javax.swing.JTextField();
			ivjJTextFieldIndividualAddress.setName("JTextFieldIndividualAddress");
			ivjJTextFieldIndividualAddress.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJTextFieldIndividualAddress.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldIndividualAddress.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJTextFieldIndividualAddress.setEditable(true);
			// user code begin {1}
			ivjJTextFieldIndividualAddress.setEnabled(false);
			ivjJTextDivisionAddress.setDocument( new StringRangeDocument(15) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldIndividualAddress;
}
/**
 * Return the JTextFieldRateFamily property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldRateFamily() {
	if (ivjJTextFieldRateFamily == null) {
		try {
			ivjJTextFieldRateFamily = new javax.swing.JTextField();
			ivjJTextFieldRateFamily.setName("JTextFieldRateFamily");
			ivjJTextFieldRateFamily.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldRateFamily.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			ivjJTextFieldRateFamily.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 7) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldRateFamily;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldRateHierarchy() {
	if (ivjJTextFieldRateHierarchy == null) {
		try {
			ivjJTextFieldRateHierarchy = new javax.swing.JTextField();
			ivjJTextFieldRateHierarchy.setName("JTextFieldRateHierarchy");
			ivjJTextFieldRateHierarchy.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			ivjJTextFieldRateHierarchy.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 1) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldRateHierarchy;
}
/**
 * Return the JTextFieldRateMember property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldRateMember() {
	if (ivjJTextFieldRateMember == null) {
		try {
			ivjJTextFieldRateMember = new javax.swing.JTextField();
			ivjJTextFieldRateMember.setName("JTextFieldRateMember");
			ivjJTextFieldRateMember.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldRateMember.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			ivjJTextFieldRateMember.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 15) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldRateMember;
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/2004 3:56:49 PM)
 * @return javax.swing.JTextField
 */
public javax.swing.JTextField getJTextFieldSubAddress() {
	if (ivjJTextFieldSubAddress == null) {
		try {
			ivjJTextFieldSubAddress = new javax.swing.JTextField();
			ivjJTextFieldSubAddress.setName("JTextFieldSubAddress");
			ivjJTextFieldSubAddress.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldSubAddress.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJTextFieldSubAddress.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			ivjJTextFieldSubAddress.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 1023) );
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
 * Return the JTextFieldUtilityAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldUtilityAddress() {
	if (ivjJTextFieldUtilityAddress == null) {
		try {
			ivjJTextFieldUtilityAddress = new javax.swing.JTextField();
			ivjJTextFieldUtilityAddress.setName("JTextFieldUtilityAddress");
			ivjJTextFieldUtilityAddress.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldUtilityAddress.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJTextFieldUtilityAddress.setText("");
			ivjJTextFieldUtilityAddress.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			ivjJTextFieldUtilityAddress.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 15) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldUtilityAddress;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRateAddressPanel() {
	if (ivjRateAddressPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Rate Address");
			ivjRateAddressPanel = new javax.swing.JPanel();
			ivjRateAddressPanel.setName("RateAddressPanel");
			ivjRateAddressPanel.setBorder(ivjLocalBorder1);
			ivjRateAddressPanel.setLayout(new java.awt.GridBagLayout());
			ivjRateAddressPanel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjRateAddressPanel.setPreferredSize(new java.awt.Dimension(220, 94));
			ivjRateAddressPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjRateAddressPanel.setMinimumSize(new java.awt.Dimension(220, 94));

			java.awt.GridBagConstraints constraintsRateMemberLabel = new java.awt.GridBagConstraints();
			constraintsRateMemberLabel.gridx = 1; constraintsRateMemberLabel.gridy = 2;
			constraintsRateMemberLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsRateMemberLabel.ipadx = 13;
			constraintsRateMemberLabel.ipady = 4;
			constraintsRateMemberLabel.insets = new java.awt.Insets(2, 25, 4, 0);
			getRateAddressPanel().add(getRateMemberLabel(), constraintsRateMemberLabel);

			java.awt.GridBagConstraints constraintsRateFamilyLabel = new java.awt.GridBagConstraints();
			constraintsRateFamilyLabel.gridx = 1; constraintsRateFamilyLabel.gridy = 1;
			constraintsRateFamilyLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsRateFamilyLabel.ipadx = 20;
			constraintsRateFamilyLabel.ipady = 4;
			constraintsRateFamilyLabel.insets = new java.awt.Insets(25, 25, 4, 4);
			getRateAddressPanel().add(getRateFamilyLabel(), constraintsRateFamilyLabel);

			java.awt.GridBagConstraints constraintsJTextFieldRateFamily = new java.awt.GridBagConstraints();
			constraintsJTextFieldRateFamily.gridx = 2; constraintsJTextFieldRateFamily.gridy = 1;
			constraintsJTextFieldRateFamily.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJTextFieldRateFamily.weightx = 1.0;
			constraintsJTextFieldRateFamily.ipadx = 30;
			constraintsJTextFieldRateFamily.ipady = -1;
			constraintsJTextFieldRateFamily.insets = new java.awt.Insets(25, 0, 2, 26);
			getRateAddressPanel().add(getJTextFieldRateFamily(), constraintsJTextFieldRateFamily);

			java.awt.GridBagConstraints constraintsJTextFieldRateMember = new java.awt.GridBagConstraints();
			constraintsJTextFieldRateMember.gridx = 2; constraintsJTextFieldRateMember.gridy = 2;
			constraintsJTextFieldRateMember.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJTextFieldRateMember.weightx = 1.0;
			constraintsJTextFieldRateMember.ipadx = 30;
			constraintsJTextFieldRateMember.ipady = -1;
			constraintsJTextFieldRateMember.insets = new java.awt.Insets(2, 0, 2, 26);
			getRateAddressPanel().add(getJTextFieldRateMember(), constraintsJTextFieldRateMember);

			java.awt.GridBagConstraints constraintsRateHierachyLabel = new java.awt.GridBagConstraints();
			constraintsRateHierachyLabel.gridx = 1; constraintsRateHierachyLabel.gridy = 3;
			constraintsRateHierachyLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsRateHierachyLabel.ipadx = 5;
			constraintsRateHierachyLabel.ipady = 3;
			constraintsRateHierachyLabel.insets = new java.awt.Insets(3, 25, 38, 1);
			getRateAddressPanel().add(getRateHierachyLabel(), constraintsRateHierachyLabel);

			java.awt.GridBagConstraints constraintsJTextFieldRateHierarchy = new java.awt.GridBagConstraints();
			constraintsJTextFieldRateHierarchy.gridx = 2; constraintsJTextFieldRateHierarchy.gridy = 3;
			constraintsJTextFieldRateHierarchy.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJTextFieldRateHierarchy.weightx = 1.0;
			constraintsJTextFieldRateHierarchy.ipadx = 30;
			constraintsJTextFieldRateHierarchy.ipady = -3;
			constraintsJTextFieldRateHierarchy.insets = new java.awt.Insets(3, 0, 35, 26);
			getRateAddressPanel().add(getJTextFieldRateHierarchy(), constraintsJTextFieldRateHierarchy);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRateAddressPanel;
}
/**
 * Return the RateFamilyLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRateFamilyLabel() {
	if (ivjRateFamilyLabel == null) {
		try {
			ivjRateFamilyLabel = new javax.swing.JLabel();
			ivjRateFamilyLabel.setName("RateFamilyLabel");
			ivjRateFamilyLabel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjRateFamilyLabel.setText("Family:");
			ivjRateFamilyLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjRateFamilyLabel.setEnabled(true);
			ivjRateFamilyLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRateFamilyLabel;
}
/**
 * Return the RateHierachyLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRateHierachyLabel() {
	if (ivjRateHierachyLabel == null) {
		try {
			ivjRateHierachyLabel = new javax.swing.JLabel();
			ivjRateHierachyLabel.setName("RateHierachyLabel");
			ivjRateHierachyLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjRateHierachyLabel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjRateHierachyLabel.setText("Hierarchy: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRateHierachyLabel;
}
/**
 * Return the RateMemberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRateMemberLabel() {
	if (ivjRateMemberLabel == null) {
		try {
			ivjRateMemberLabel = new javax.swing.JLabel();
			ivjRateMemberLabel.setName("RateMemberLabel");
			ivjRateMemberLabel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjRateMemberLabel.setText("Member: ");
			ivjRateMemberLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjRateMemberLabel.setEnabled(true);
			ivjRateMemberLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRateMemberLabel;
}
/**
 * Return the RelayUsagePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRelayUsagePanel() {
	if (ivjRelayUsagePanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder3;
			ivjLocalBorder3 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder3.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder3.setTitle("Relays");
			ivjRelayUsagePanel = new javax.swing.JPanel();
			ivjRelayUsagePanel.setName("RelayUsagePanel");
			ivjRelayUsagePanel.setBorder(ivjLocalBorder3);
			ivjRelayUsagePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxRelay1 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay1.gridx = 1; constraintsJCheckBoxRelay1.gridy = 1;
			constraintsJCheckBoxRelay1.ipadx = 24;
			constraintsJCheckBoxRelay1.ipady = -4;
			constraintsJCheckBoxRelay1.insets = new java.awt.Insets(5, 23, 1, 20);
			getRelayUsagePanel().add(getJCheckBoxRelay1(), constraintsJCheckBoxRelay1);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay2 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay2.gridx = 1; constraintsJCheckBoxRelay2.gridy = 2;
			constraintsJCheckBoxRelay2.ipadx = 24;
			constraintsJCheckBoxRelay2.ipady = -4;
			constraintsJCheckBoxRelay2.insets = new java.awt.Insets(2, 23, 0, 20);
			getRelayUsagePanel().add(getJCheckBoxRelay2(), constraintsJCheckBoxRelay2);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay3 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay3.gridx = 1; constraintsJCheckBoxRelay3.gridy = 3;
			constraintsJCheckBoxRelay3.ipadx = 24;
			constraintsJCheckBoxRelay3.ipady = -4;
			constraintsJCheckBoxRelay3.insets = new java.awt.Insets(0, 23, 1, 20);
			getRelayUsagePanel().add(getJCheckBoxRelay3(), constraintsJCheckBoxRelay3);

			java.awt.GridBagConstraints constraintsJCheckBoxRelay4 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRelay4.gridx = 1; constraintsJCheckBoxRelay4.gridy = 4;
			constraintsJCheckBoxRelay4.ipadx = 24;
			constraintsJCheckBoxRelay4.ipady = -4;
			constraintsJCheckBoxRelay4.insets = new java.awt.Insets(2, 23, 12, 20);
			getRelayUsagePanel().add(getJCheckBoxRelay4(), constraintsJCheckBoxRelay4);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelayUsagePanel;
}
/**
 * Return the SubAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSubAddressLabel() {
	if (ivjSubAddressLabel == null) {
		try {
			ivjSubAddressLabel = new javax.swing.JLabel();
			ivjSubAddressLabel.setName("SubAddressLabel");
			ivjSubAddressLabel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjSubAddressLabel.setText("Substation Address:");
			ivjSubAddressLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjSubAddressLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjSubAddressLabel.setEnabled(true);
			ivjSubAddressLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSubAddressLabel;
}
/**
 * Return the UtilityAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUtilityAddressLabel() {
	if (ivjUtilityAddressLabel == null) {
		try {
			ivjUtilityAddressLabel = new javax.swing.JLabel();
			ivjUtilityAddressLabel.setName("UtilityAddressLabel");
			ivjUtilityAddressLabel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjUtilityAddressLabel.setText("Utility Address:");
			ivjUtilityAddressLabel.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjUtilityAddressLabel.setFont(new java.awt.Font("Arial", 1, 10));
			ivjUtilityAddressLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjUtilityAddressLabel.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUtilityAddressLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	LMGroupSA305 threeOhFive = null;
	
	if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		threeOhFive = (LMGroupSA305)
			com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
			LMGroupSA305.class,
			(com.cannontech.database.data.multi.MultiDBPersistent)o );
	}
	else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		threeOhFive = (LMGroupSA305)
			((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
		
	if( o instanceof LMGroupSA305 || threeOhFive != null )
	{
		if( threeOhFive == null )
			threeOhFive = (LMGroupSA305) o;
		
		String utilityAddress = getJTextFieldUtilityAddress().getText();
		String groupAddress = getJTextFieldGroupAddress().getText();
		String divisionAddress = getJTextDivisionAddress().getText();
		String subAddress = getJTextFieldSubAddress().getText();
		String rateFamily = getJTextFieldRateFamily().getText();
		String rateMember = getJTextFieldRateMember().getText();
		String rateHierarchy = getJTextFieldRateHierarchy().getText();
		 	
		threeOhFive.getLMGroupSA305().setUtilityAddress(new Integer(utilityAddress));
		if(groupAddress.compareTo("") != 0)
			threeOhFive.getLMGroupSA305().setGroupAddress(new Integer(groupAddress));
		if(divisionAddress.compareTo("") != 0)
			threeOhFive.getLMGroupSA305().setDivisionAddress(new Integer(divisionAddress));
		if(subAddress.compareTo("") != 0)
			threeOhFive.getLMGroupSA305().setSubstationAddress(new Integer(subAddress));
		threeOhFive.getLMGroupSA305().setIndividualAddress(getJTextFieldIndividualAddress().getText());
		if(rateFamily.compareTo("") != 0)
			threeOhFive.getLMGroupSA305().setRateFamily(new Integer(rateFamily));
		if(rateMember.compareTo("") != 0)
			threeOhFive.getLMGroupSA305().setRateMember(new Integer(rateMember));
		if(rateHierarchy.compareTo("") != 0)
			threeOhFive.getLMGroupSA305().setRateHierarchy(new Integer(rateHierarchy));
		
		
		StringBuffer addressUsage = new StringBuffer();
		addressUsage.append('U');
		if(getJCheckBoxGroupUsage().isSelected())
		{
			addressUsage.append('G');
		}
		if(getJCheckBoxDivisionUsage().isSelected())
		{
			addressUsage.append('D');
		}
		if(getJCheckBoxSubUsage().isSelected())
		{
			addressUsage.append('S');
		}
		if(getJCheckBoxRateUsage().isSelected())
		{
			addressUsage.append('R');
		}
		threeOhFive.getLMGroupSA305().setAddressUsage(addressUsage.toString());	
		
		StringBuffer relayUsage = new StringBuffer();
		if(getJCheckBoxRelay1().isSelected())
			relayUsage.append('1');	
		if(getJCheckBoxRelay2().isSelected())
			relayUsage.append('2');	
		if(getJCheckBoxRelay3().isSelected())
			relayUsage.append('3');	
		if(getJCheckBoxRelay4().isSelected())
			relayUsage.append('4');
		threeOhFive.getLMGroupSA305().setLoadNumber(relayUsage.toString());		
			

	}
	
	return threeOhFive;
	
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJCheckBoxRelay1().addActionListener(ivjEventHandler);
	getJCheckBoxRelay2().addActionListener(ivjEventHandler);
	getJCheckBoxRelay3().addActionListener(ivjEventHandler);
	getJCheckBoxRelay4().addActionListener(ivjEventHandler);
	getJCheckBoxDivisionUsage().addActionListener(ivjEventHandler);
	getJCheckBoxRateUsage().addActionListener(ivjEventHandler);
	getJCheckBoxGroupUsage().addActionListener(ivjEventHandler);
	getJCheckBoxSubUsage().addActionListener(ivjEventHandler);
	getJTextFieldIndividualAddress().addCaretListener(ivjEventHandler);
	getJTextFieldRateFamily().addCaretListener(ivjEventHandler);
	getJTextFieldRateMember().addActionListener(ivjEventHandler);
	getJTextFieldSubAddress().addActionListener(ivjEventHandler);
	getJTextDivisionAddress().addCaretListener(ivjEventHandler);
	getJTextFieldGroupAddress().addCaretListener(ivjEventHandler);
	getJTextFieldUtilityAddress().addCaretListener(ivjEventHandler);
	getJTextFieldRateHierarchy().addCaretListener(ivjEventHandler);
	getJCheckBoxSerial().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SA305EditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsAddressPanel = new java.awt.GridBagConstraints();
		constraintsAddressPanel.gridx = 1; constraintsAddressPanel.gridy = 1;
		constraintsAddressPanel.gridwidth = 2;
		constraintsAddressPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddressPanel.weightx = 1.0;
		constraintsAddressPanel.weighty = 1.0;
		constraintsAddressPanel.ipadx = -24;
		constraintsAddressPanel.ipady = -30;
		constraintsAddressPanel.insets = new java.awt.Insets(4, 5, 5, 4);
		add(getAddressPanel(), constraintsAddressPanel);

		java.awt.GridBagConstraints constraintsAddressUsagePanel = new java.awt.GridBagConstraints();
		constraintsAddressUsagePanel.gridx = 1; constraintsAddressUsagePanel.gridy = 2;
		constraintsAddressUsagePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddressUsagePanel.weightx = 1.0;
		constraintsAddressUsagePanel.weighty = 1.0;
		constraintsAddressUsagePanel.ipadx = -10;
		constraintsAddressUsagePanel.ipady = -3;
		constraintsAddressUsagePanel.insets = new java.awt.Insets(5, 8, 8, 1);
		add(getAddressUsagePanel(), constraintsAddressUsagePanel);

		java.awt.GridBagConstraints constraintsRelayUsagePanel = new java.awt.GridBagConstraints();
		constraintsRelayUsagePanel.gridx = 2; constraintsRelayUsagePanel.gridy = 2;
		constraintsRelayUsagePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsRelayUsagePanel.weightx = 1.0;
		constraintsRelayUsagePanel.weighty = 1.0;
		constraintsRelayUsagePanel.ipadx = 14;
		constraintsRelayUsagePanel.ipady = -18;
		constraintsRelayUsagePanel.insets = new java.awt.Insets(5, 1, 9, 3);
		add(getRelayUsagePanel(), constraintsRelayUsagePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jCheckBoxSerial_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if(getJCheckBoxSerial().isSelected())
	{
		getJTextFieldIndividualAddress().setEnabled(true);
		
		getJTextFieldGroupAddress().setEnabled(false);
		getJTextFieldSubAddress().setEnabled(false);
		getJTextDivisionAddress().setEnabled(false);
		getJTextFieldRateFamily().setEnabled(false);
		getJTextFieldRateHierarchy().setEnabled(false);
		getJTextFieldRateMember().setEnabled(false);
		getJCheckBoxGroupUsage().setEnabled(false);
		getJCheckBoxDivisionUsage().setEnabled(false);
		getJCheckBoxSubUsage().setEnabled(false);
		getJCheckBoxRateUsage().setEnabled(false);
		
		getJCheckBoxGroupUsage().setSelected(false);
		getJCheckBoxDivisionUsage().setSelected(false);
		getJCheckBoxSubUsage().setSelected(false);
		getJCheckBoxRateUsage().setSelected(false);
		
	}
	else
	{
		getJTextFieldIndividualAddress().setEnabled(false);
		
		getJTextFieldGroupAddress().setEnabled(true);
		getJTextFieldSubAddress().setEnabled(true);
		getJTextDivisionAddress().setEnabled(true);
		getJTextFieldRateFamily().setEnabled(true);
		getJTextFieldRateHierarchy().setEnabled(true);
		getJTextFieldRateMember().setEnabled(true);
		getJCheckBoxGroupUsage().setEnabled(true);
		getJCheckBoxDivisionUsage().setEnabled(true);
		getJCheckBoxSubUsage().setEnabled(true);
		getJCheckBoxRateUsage().setEnabled(false);
		
		getJCheckBoxRateUsage().setSelected(true);
	}
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SA305EditorPanel aSA305EditorPanel;
		aSA305EditorPanel = new SA305EditorPanel();
		frame.setContentPane(aSA305EditorPanel);
		frame.setSize(aSA305EditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/24/2004 3:56:49 PM)
 * @param newIvjJTextFieldSubAddress javax.swing.JTextField
 */
public void setJTextFieldSubAddress(javax.swing.JTextField newIvjJTextFieldSubAddress) {
	ivjJTextFieldSubAddress = newIvjJTextFieldSubAddress;
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	if(o instanceof LMGroupSA305)
	{
		LMGroupSA305 threeOhFive = (LMGroupSA305) o;
		
		getJTextFieldUtilityAddress().setText(threeOhFive.getLMGroupSA305().getUtilityAddress().toString());	
		getJTextFieldGroupAddress().setText(threeOhFive.getLMGroupSA305().getGroupAddress().toString());
		getJTextDivisionAddress().setText(threeOhFive.getLMGroupSA305().getDivisionAddress().toString());
		getJTextFieldSubAddress().setText(threeOhFive.getLMGroupSA305().getSubstationAddress().toString());
		getJTextFieldIndividualAddress().setText(threeOhFive.getLMGroupSA305().getIndividualAddress());
		getJTextFieldRateFamily().setText(threeOhFive.getLMGroupSA305().getRateFamily().toString());
		getJTextFieldRateMember().setText(threeOhFive.getLMGroupSA305().getRateMember().toString());
		getJTextFieldRateHierarchy().setText(threeOhFive.getLMGroupSA305().getRateHierarchy().toString());
		
		String addressUsage = threeOhFive.getLMGroupSA305().getAddressUsage();
		if(addressUsage.indexOf('G') != -1 )
			getJCheckBoxGroupUsage().setSelected(true);
		if(addressUsage.indexOf('D') != -1 )
			getJCheckBoxDivisionUsage().setSelected(true);
		if(addressUsage.indexOf('S') != -1 )
			getJCheckBoxSubUsage().setSelected(true);
		if(addressUsage.indexOf('R') != -1 )
			getJCheckBoxRateUsage().setSelected(true);
			
		String loadNumber = threeOhFive.getLMGroupSA305().getLoadNumber();
		if(loadNumber.indexOf('1') != -1 )
			getJCheckBoxRelay1().setSelected(true);
		if(loadNumber.indexOf('2') != -1 )
			getJCheckBoxRelay2().setSelected(true);
		if(loadNumber.indexOf('3') != -1 )
			getJCheckBoxRelay3().setSelected(true);
		if(loadNumber.indexOf('4') != -1 )
			getJCheckBoxRelay4().setSelected(true);	
			
		
	}

}

public boolean isInputValid() 
{
	String utilityAddress = getJTextFieldUtilityAddress().getText();
	if(utilityAddress.compareTo("") == 0)
		return false;

	return true;
}

}
