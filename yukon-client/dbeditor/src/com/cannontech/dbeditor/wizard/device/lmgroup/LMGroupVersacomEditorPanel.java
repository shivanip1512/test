package com.cannontech.dbeditor.wizard.device.lmgroup;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.roles.application.DBEditorRole;

/**
 * This type was created in VisualAge.
 */
public class LMGroupVersacomEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjAddressUsagePanel = null;
	private javax.swing.JCheckBox ivjClassAddressCheckBox = null;
	private com.cannontech.common.gui.util.SingleLine16BitTogglePanel ivjClassAddressSingleLineBitTogglePanel = null;
	private javax.swing.JCheckBox ivjDivisionAddressCheckBox = null;
	private com.cannontech.common.gui.util.SingleLine16BitTogglePanel ivjDivisionAddressSingleLineBitTogglePanel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JCheckBox ivjRelay1CheckBox = null;
	private javax.swing.JCheckBox ivjRelay2CheckBox = null;
	private javax.swing.JCheckBox ivjRelay3CheckBox = null;
	private javax.swing.JPanel ivjRelayUsagePanel = null;
	private javax.swing.JCheckBox ivjSectionAddressCheckBox = null;
	private javax.swing.JLabel ivjSectionAddressLabel = null;
	private com.klg.jclass.field.JCSpinField ivjSectionAddressSpinner = null;
	private javax.swing.JCheckBox ivjUtilityAddressCheckBox = null;
	private javax.swing.JLabel ivjUtilityAddressLabel = null;
	private com.klg.jclass.field.JCSpinField ivjUtilityAddressSpinner = null;
	private com.cannontech.common.gui.util.TitleBorder ivjAddressUsagePanelTitleBorder = null;
	private com.cannontech.common.gui.util.TitleBorder ivjClassAddressSingleLineBitTogglePanelTitleBorder = null;
	private com.cannontech.common.gui.util.TitleBorder ivjDivisionAddressSingleLineBitTogglePanelTitleBorder = null;
	private com.cannontech.common.gui.util.TitleBorder ivjJPanel1TitleBorder = null;
	private com.cannontech.common.gui.util.TitleBorder ivjRelayUsagePanelTitleBorder = null;
	private javax.swing.JCheckBox ivjRelay4CheckBox = null;
	private javax.swing.JCheckBox ivjJCheckBoxSerialAddress = null;
	private javax.swing.JLabel ivjJLabelSerialAddress = null;
	private javax.swing.JTextField ivjJTextFieldSerialAddress = null;
	private javax.swing.JPanel ivjJPanelUtilSec = null;
	private javax.swing.JTextField ivjJtextFieldUtilRange = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMGroupVersacomEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJCheckBoxSerialAddress()) 
		connEtoC8(e);
	// user code begin {2}

	if( e.getSource() == getDivisionAddressSingleLineBitTogglePanel() )
		fireInputUpdate();
		
	if( e.getSource() == getClassAddressSingleLineBitTogglePanel() )
		fireInputUpdate();
		
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
	if (e.getSource() == getJTextFieldSerialAddress()) 
		connEtoC9(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (Relay4CheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
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
 * connEtoC2:  (SectionAddressCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
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
 * connEtoC3:  (ClassAddressCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
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
 * connEtoC4:  (DivisionAddressCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ItemEvent arg1) {
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
 * connEtoC5:  (Relay1CheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
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
 * connEtoC6:  (Relay2CheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ItemEvent arg1) {
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
 * connEtoC7:  (Relay3CheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ItemEvent arg1) {
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
 * connEtoC8:  (JCheckBoxSerialAddress.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupVersacomEditorPanel.jCheckBoxSerialAddress_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxSerialAddress_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (JTextFieldSerialAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
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
 * Return the AddressUsagePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressUsagePanel() {
	if (ivjAddressUsagePanel == null) {
		try {
			ivjAddressUsagePanel = new javax.swing.JPanel();
			ivjAddressUsagePanel.setName("AddressUsagePanel");
			ivjAddressUsagePanel.setBorder(getAddressUsagePanelTitleBorder());
			ivjAddressUsagePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsUtilityAddressCheckBox = new java.awt.GridBagConstraints();
			constraintsUtilityAddressCheckBox.gridx = 1; constraintsUtilityAddressCheckBox.gridy = 1;
			constraintsUtilityAddressCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUtilityAddressCheckBox.ipadx = 10;
			constraintsUtilityAddressCheckBox.insets = new java.awt.Insets(5, 16, 0, 60);
			getAddressUsagePanel().add(getUtilityAddressCheckBox(), constraintsUtilityAddressCheckBox);

			java.awt.GridBagConstraints constraintsSectionAddressCheckBox = new java.awt.GridBagConstraints();
			constraintsSectionAddressCheckBox.gridx = 1; constraintsSectionAddressCheckBox.gridy = 2;
			constraintsSectionAddressCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSectionAddressCheckBox.ipadx = -2;
			constraintsSectionAddressCheckBox.insets = new java.awt.Insets(0, 16, 0, 60);
			getAddressUsagePanel().add(getSectionAddressCheckBox(), constraintsSectionAddressCheckBox);

			java.awt.GridBagConstraints constraintsClassAddressCheckBox = new java.awt.GridBagConstraints();
			constraintsClassAddressCheckBox.gridx = 1; constraintsClassAddressCheckBox.gridy = 3;
			constraintsClassAddressCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsClassAddressCheckBox.ipadx = 9;
			constraintsClassAddressCheckBox.insets = new java.awt.Insets(0, 16, 0, 60);
			getAddressUsagePanel().add(getClassAddressCheckBox(), constraintsClassAddressCheckBox);

			java.awt.GridBagConstraints constraintsDivisionAddressCheckBox = new java.awt.GridBagConstraints();
			constraintsDivisionAddressCheckBox.gridx = 1; constraintsDivisionAddressCheckBox.gridy = 4;
			constraintsDivisionAddressCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDivisionAddressCheckBox.ipadx = -3;
			constraintsDivisionAddressCheckBox.insets = new java.awt.Insets(0, 16, 1, 60);
			getAddressUsagePanel().add(getDivisionAddressCheckBox(), constraintsDivisionAddressCheckBox);

			java.awt.GridBagConstraints constraintsJCheckBoxSerialAddress = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSerialAddress.gridx = 1; constraintsJCheckBoxSerialAddress.gridy = 5;
			constraintsJCheckBoxSerialAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxSerialAddress.ipadx = 8;
			constraintsJCheckBoxSerialAddress.insets = new java.awt.Insets(1, 16, 20, 60);
			getAddressUsagePanel().add(getJCheckBoxSerialAddress(), constraintsJCheckBoxSerialAddress);
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
 * Return the AddressUsagePanelTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getAddressUsagePanelTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjAddressUsagePanelTitleBorder = null;
	try {
		/* Create part */
		ivjAddressUsagePanelTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjAddressUsagePanelTitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjAddressUsagePanelTitleBorder.setTitle("Address Usage");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjAddressUsagePanelTitleBorder;
}
/**
 * Return the ClassAddressCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getClassAddressCheckBox() {
	if (ivjClassAddressCheckBox == null) {
		try {
			ivjClassAddressCheckBox = new javax.swing.JCheckBox();
			ivjClassAddressCheckBox.setName("ClassAddressCheckBox");
			ivjClassAddressCheckBox.setText("Class Address");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClassAddressCheckBox;
}
/**
 * Return the ClassAddressSingleLineBitTogglePanel property value.
 * @return com.cannontech.common.gui.util.SingleLine16BitTogglePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.SingleLine16BitTogglePanel getClassAddressSingleLineBitTogglePanel() {
	if (ivjClassAddressSingleLineBitTogglePanel == null) {
		try {
			ivjClassAddressSingleLineBitTogglePanel = new com.cannontech.common.gui.util.SingleLine16BitTogglePanel();
			ivjClassAddressSingleLineBitTogglePanel.setName("ClassAddressSingleLineBitTogglePanel");
			ivjClassAddressSingleLineBitTogglePanel.setBorder(getClassAddressSingleLineBitTogglePanelTitleBorder());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClassAddressSingleLineBitTogglePanel;
}
/**
 * Return the ClassAddressSingleLineBitTogglePanelTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getClassAddressSingleLineBitTogglePanelTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjClassAddressSingleLineBitTogglePanelTitleBorder = null;
	try {
		/* Create part */
		ivjClassAddressSingleLineBitTogglePanelTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjClassAddressSingleLineBitTogglePanelTitleBorder.setTitle("Class Address");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjClassAddressSingleLineBitTogglePanelTitleBorder;
}
/**
 * Return the DivisionAddressCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getDivisionAddressCheckBox() {
	if (ivjDivisionAddressCheckBox == null) {
		try {
			ivjDivisionAddressCheckBox = new javax.swing.JCheckBox();
			ivjDivisionAddressCheckBox.setName("DivisionAddressCheckBox");
			ivjDivisionAddressCheckBox.setText("Division Address");
			// user code begin {1}
			
			ivjDivisionAddressCheckBox.setPreferredSize(new java.awt.Dimension(139, 22));
			ivjDivisionAddressCheckBox.setMinimumSize(new java.awt.Dimension(139, 22));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDivisionAddressCheckBox;
}
/**
 * Return the DivisionAddressSingleLineBitTogglePanel property value.
 * @return com.cannontech.common.gui.util.SingleLine16BitTogglePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.SingleLine16BitTogglePanel getDivisionAddressSingleLineBitTogglePanel() {
	if (ivjDivisionAddressSingleLineBitTogglePanel == null) {
		try {
			ivjDivisionAddressSingleLineBitTogglePanel = new com.cannontech.common.gui.util.SingleLine16BitTogglePanel();
			ivjDivisionAddressSingleLineBitTogglePanel.setName("DivisionAddressSingleLineBitTogglePanel");
			ivjDivisionAddressSingleLineBitTogglePanel.setBorder(getDivisionAddressSingleLineBitTogglePanelTitleBorder());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDivisionAddressSingleLineBitTogglePanel;
}
/**
 * Return the DivisionAddressSingleLineBitTogglePanelTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getDivisionAddressSingleLineBitTogglePanelTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjDivisionAddressSingleLineBitTogglePanelTitleBorder = null;
	try {
		/* Create part */
		ivjDivisionAddressSingleLineBitTogglePanelTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjDivisionAddressSingleLineBitTogglePanelTitleBorder.setTitle("Division Address");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjDivisionAddressSingleLineBitTogglePanelTitleBorder;
}
/**
 * Return the JCheckBoxSerialAddress property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSerialAddress() {
	if (ivjJCheckBoxSerialAddress == null) {
		try {
			ivjJCheckBoxSerialAddress = new javax.swing.JCheckBox();
			ivjJCheckBoxSerialAddress.setName("JCheckBoxSerialAddress");
			ivjJCheckBoxSerialAddress.setText("Serial Address");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSerialAddress;
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
			ivjJLabelSerialAddress.setText("Serial Address:");
			ivjJLabelSerialAddress.setEnabled(false);
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
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setBorder(getJPanel1TitleBorder());
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsClassAddressSingleLineBitTogglePanel = new java.awt.GridBagConstraints();
			constraintsClassAddressSingleLineBitTogglePanel.gridx = 1; constraintsClassAddressSingleLineBitTogglePanel.gridy = 2;
			constraintsClassAddressSingleLineBitTogglePanel.gridwidth = 2;
			constraintsClassAddressSingleLineBitTogglePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsClassAddressSingleLineBitTogglePanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsClassAddressSingleLineBitTogglePanel.weightx = 1.0;
			constraintsClassAddressSingleLineBitTogglePanel.weighty = 1.0;
			constraintsClassAddressSingleLineBitTogglePanel.ipadx = -323;
			constraintsClassAddressSingleLineBitTogglePanel.ipady = -6;
			constraintsClassAddressSingleLineBitTogglePanel.insets = new java.awt.Insets(2, 8, 5, 10);
			getJPanel1().add(getClassAddressSingleLineBitTogglePanel(), constraintsClassAddressSingleLineBitTogglePanel);

			java.awt.GridBagConstraints constraintsDivisionAddressSingleLineBitTogglePanel = new java.awt.GridBagConstraints();
			constraintsDivisionAddressSingleLineBitTogglePanel.gridx = 1; constraintsDivisionAddressSingleLineBitTogglePanel.gridy = 3;
			constraintsDivisionAddressSingleLineBitTogglePanel.gridwidth = 2;
			constraintsDivisionAddressSingleLineBitTogglePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDivisionAddressSingleLineBitTogglePanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDivisionAddressSingleLineBitTogglePanel.weightx = 1.0;
			constraintsDivisionAddressSingleLineBitTogglePanel.weighty = 1.0;
			constraintsDivisionAddressSingleLineBitTogglePanel.ipadx = -323;
			constraintsDivisionAddressSingleLineBitTogglePanel.ipady = -6;
			constraintsDivisionAddressSingleLineBitTogglePanel.insets = new java.awt.Insets(2, 8, 3, 10);
			getJPanel1().add(getDivisionAddressSingleLineBitTogglePanel(), constraintsDivisionAddressSingleLineBitTogglePanel);

			java.awt.GridBagConstraints constraintsJLabelSerialAddress = new java.awt.GridBagConstraints();
			constraintsJLabelSerialAddress.gridx = 1; constraintsJLabelSerialAddress.gridy = 4;
			constraintsJLabelSerialAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelSerialAddress.ipadx = 10;
			constraintsJLabelSerialAddress.insets = new java.awt.Insets(6, 8, 19, 1);
			getJPanel1().add(getJLabelSerialAddress(), constraintsJLabelSerialAddress);

			java.awt.GridBagConstraints constraintsJTextFieldSerialAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldSerialAddress.gridx = 2; constraintsJTextFieldSerialAddress.gridy = 4;
			constraintsJTextFieldSerialAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSerialAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldSerialAddress.weightx = 1.0;
			constraintsJTextFieldSerialAddress.ipadx = 172;
			constraintsJTextFieldSerialAddress.insets = new java.awt.Insets(3, 2, 16, 38);
			getJPanel1().add(getJTextFieldSerialAddress(), constraintsJTextFieldSerialAddress);

			java.awt.GridBagConstraints constraintsJPanelUtilSec = new java.awt.GridBagConstraints();
			constraintsJPanelUtilSec.gridx = 1; constraintsJPanelUtilSec.gridy = 1;
			constraintsJPanelUtilSec.gridwidth = 2;
			constraintsJPanelUtilSec.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelUtilSec.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelUtilSec.weightx = 1.0;
			constraintsJPanelUtilSec.weighty = 1.0;
			constraintsJPanelUtilSec.insets = new java.awt.Insets(0, 8, 2, 10);
			getJPanel1().add(getJPanelUtilSec(), constraintsJPanelUtilSec);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel1TitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getJPanel1TitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjJPanel1TitleBorder = null;
	try {
		/* Create part */
		ivjJPanel1TitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjJPanel1TitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjJPanel1TitleBorder.setTitle("Addressing");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel1TitleBorder;
}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelUtilSec() {
	if (ivjJPanelUtilSec == null) {
		try {
			ivjJPanelUtilSec = new javax.swing.JPanel();
			ivjJPanelUtilSec.setName("JPanelUtilSec");
			ivjJPanelUtilSec.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsUtilityAddressLabel = new java.awt.GridBagConstraints();
			constraintsUtilityAddressLabel.gridx = 1; constraintsUtilityAddressLabel.gridy = 1;
			constraintsUtilityAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUtilityAddressLabel.insets = new java.awt.Insets(6, 6, 3, 2);
			getJPanelUtilSec().add(getUtilityAddressLabel(), constraintsUtilityAddressLabel);

			java.awt.GridBagConstraints constraintsUtilityAddressSpinner = new java.awt.GridBagConstraints();
			constraintsUtilityAddressSpinner.gridx = 2; constraintsUtilityAddressSpinner.gridy = 1;
			constraintsUtilityAddressSpinner.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUtilityAddressSpinner.insets = new java.awt.Insets(3, 3, 0, 4);
			getJPanelUtilSec().add(getUtilityAddressSpinner(), constraintsUtilityAddressSpinner);

			java.awt.GridBagConstraints constraintsSectionAddressLabel = new java.awt.GridBagConstraints();
			constraintsSectionAddressLabel.gridx = 3; constraintsSectionAddressLabel.gridy = 1;
			constraintsSectionAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSectionAddressLabel.insets = new java.awt.Insets(6, 4, 3, 2);
			getJPanelUtilSec().add(getSectionAddressLabel(), constraintsSectionAddressLabel);

			java.awt.GridBagConstraints constraintsSectionAddressSpinner = new java.awt.GridBagConstraints();
			constraintsSectionAddressSpinner.gridx = 4; constraintsSectionAddressSpinner.gridy = 1;
			constraintsSectionAddressSpinner.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSectionAddressSpinner.ipadx = -5;
			constraintsSectionAddressSpinner.insets = new java.awt.Insets(3, 3, 0, 3);
			getJPanelUtilSec().add(getSectionAddressSpinner(), constraintsSectionAddressSpinner);

			java.awt.GridBagConstraints constraintsJtextFieldUtilRange = new java.awt.GridBagConstraints();
			constraintsJtextFieldUtilRange.gridx = 1; constraintsJtextFieldUtilRange.gridy = 2;
			constraintsJtextFieldUtilRange.gridwidth = 4;
			constraintsJtextFieldUtilRange.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJtextFieldUtilRange.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJtextFieldUtilRange.weightx = 1.0;
			constraintsJtextFieldUtilRange.ipadx = 285;
			constraintsJtextFieldUtilRange.ipady = -2;
			constraintsJtextFieldUtilRange.insets = new java.awt.Insets(1, 6, 2, 1);
			getJPanelUtilSec().add(getJtextFieldUtilRange(), constraintsJtextFieldUtilRange);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelUtilSec;
}
/**
 * Return the JTextFieldSerialAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSerialAddress() {
	if (ivjJTextFieldSerialAddress == null) {
		try {
			ivjJTextFieldSerialAddress = new javax.swing.JTextField();
			ivjJTextFieldSerialAddress.setName("JTextFieldSerialAddress");
			ivjJTextFieldSerialAddress.setText("0");
			ivjJTextFieldSerialAddress.setEnabled(false);
			// user code begin {1}

			ivjJTextFieldSerialAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 9999999999l) );

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
 * Return the JLabelUtilRange property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJtextFieldUtilRange() {
	if (ivjJtextFieldUtilRange == null) {
		try {
			ivjJtextFieldUtilRange = new javax.swing.JTextField();
			ivjJtextFieldUtilRange.setName("JtextFieldUtilRange");
			ivjJtextFieldUtilRange.setText("(Util range:),,,");
			ivjJtextFieldUtilRange.setDisabledTextColor(new java.awt.Color(0,0,0));
			ivjJtextFieldUtilRange.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJtextFieldUtilRange.setEditable(true);
			ivjJtextFieldUtilRange.setEnabled(false);
			// user code begin {1}

			ivjJtextFieldUtilRange.setText("(Util range: " + 
				ClientSession.getInstance().getRolePropertyValue(
				DBEditorRole.UTILITY_ID_RANGE, 
				"1-" + CtiUtilities.MAX_UTILITY_ID ) +")" );

			ivjJtextFieldUtilRange.setToolTipText( ivjJtextFieldUtilRange.getText() );
			ivjJtextFieldUtilRange.setBackground( getJPanelUtilSec().getBackground() );
			ivjJtextFieldUtilRange.setBorder( javax.swing.BorderFactory.createEmptyBorder() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJtextFieldUtilRange;
}
/**
 * Return the Relay1CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getRelay1CheckBox() {
	if (ivjRelay1CheckBox == null) {
		try {
			ivjRelay1CheckBox = new javax.swing.JCheckBox();
			ivjRelay1CheckBox.setName("Relay1CheckBox");
			ivjRelay1CheckBox.setText("Relay 1");
			// user code begin {1}

			ivjRelay1CheckBox.setSelected(true);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelay1CheckBox;
}
/**
 * Return the Relay2CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getRelay2CheckBox() {
	if (ivjRelay2CheckBox == null) {
		try {
			ivjRelay2CheckBox = new javax.swing.JCheckBox();
			ivjRelay2CheckBox.setName("Relay2CheckBox");
			ivjRelay2CheckBox.setText("Relay 2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelay2CheckBox;
}
/**
 * Return the Relay3CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getRelay3CheckBox() {
	if (ivjRelay3CheckBox == null) {
		try {
			ivjRelay3CheckBox = new javax.swing.JCheckBox();
			ivjRelay3CheckBox.setName("Relay3CheckBox");
			ivjRelay3CheckBox.setText("Relay 3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelay3CheckBox;
}
/**
 * Return the Relay4CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getRelay4CheckBox() {
	if (ivjRelay4CheckBox == null) {
		try {
			ivjRelay4CheckBox = new javax.swing.JCheckBox();
			ivjRelay4CheckBox.setName("Relay4CheckBox");
			ivjRelay4CheckBox.setText("Relay 4");
			ivjRelay4CheckBox.setActionCommand("Relay 4");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelay4CheckBox;
}
/**
 * Return the RelayUsagePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRelayUsagePanel() {
	if (ivjRelayUsagePanel == null) {
		try {
			ivjRelayUsagePanel = new javax.swing.JPanel();
			ivjRelayUsagePanel.setName("RelayUsagePanel");
			ivjRelayUsagePanel.setBorder(getRelayUsagePanelTitleBorder());
			ivjRelayUsagePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRelay1CheckBox = new java.awt.GridBagConstraints();
			constraintsRelay1CheckBox.gridx = 1; constraintsRelay1CheckBox.gridy = 1;
			constraintsRelay1CheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRelay1CheckBox.insets = new java.awt.Insets(32, 37, 0, 37);
			getRelayUsagePanel().add(getRelay1CheckBox(), constraintsRelay1CheckBox);

			java.awt.GridBagConstraints constraintsRelay2CheckBox = new java.awt.GridBagConstraints();
			constraintsRelay2CheckBox.gridx = 1; constraintsRelay2CheckBox.gridy = 2;
			constraintsRelay2CheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRelay2CheckBox.insets = new java.awt.Insets(0, 37, 0, 37);
			getRelayUsagePanel().add(getRelay2CheckBox(), constraintsRelay2CheckBox);

			java.awt.GridBagConstraints constraintsRelay3CheckBox = new java.awt.GridBagConstraints();
			constraintsRelay3CheckBox.gridx = 1; constraintsRelay3CheckBox.gridy = 3;
			constraintsRelay3CheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRelay3CheckBox.insets = new java.awt.Insets(0, 37, 1, 37);
			getRelayUsagePanel().add(getRelay3CheckBox(), constraintsRelay3CheckBox);

			java.awt.GridBagConstraints constraintsRelay4CheckBox = new java.awt.GridBagConstraints();
			constraintsRelay4CheckBox.gridx = 1; constraintsRelay4CheckBox.gridy = 4;
			constraintsRelay4CheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRelay4CheckBox.insets = new java.awt.Insets(2, 37, 33, 37);
			getRelayUsagePanel().add(getRelay4CheckBox(), constraintsRelay4CheckBox);
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
 * Return the RelayUsagePanelTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getRelayUsagePanelTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjRelayUsagePanelTitleBorder = null;
	try {
		/* Create part */
		ivjRelayUsagePanelTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjRelayUsagePanelTitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjRelayUsagePanelTitleBorder.setTitle("Relay Usage");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjRelayUsagePanelTitleBorder;
}
/**
 * Return the SectionAddressCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getSectionAddressCheckBox() {
	if (ivjSectionAddressCheckBox == null) {
		try {
			ivjSectionAddressCheckBox = new javax.swing.JCheckBox();
			ivjSectionAddressCheckBox.setName("SectionAddressCheckBox");
			ivjSectionAddressCheckBox.setText("Section Address");
			// user code begin {1}

			ivjSectionAddressCheckBox.setPreferredSize(new java.awt.Dimension(139, 22));
			ivjSectionAddressCheckBox.setMinimumSize(new java.awt.Dimension(139, 22));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSectionAddressCheckBox;
}
/**
 * Return the SectionAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSectionAddressLabel() {
	if (ivjSectionAddressLabel == null) {
		try {
			ivjSectionAddressLabel = new javax.swing.JLabel();
			ivjSectionAddressLabel.setName("SectionAddressLabel");
			ivjSectionAddressLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjSectionAddressLabel.setText("Section Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSectionAddressLabel;
}
/**
 * Return the SectionAddressSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getSectionAddressSpinner() {
	if (ivjSectionAddressSpinner == null) {
		try {
			ivjSectionAddressSpinner = new com.klg.jclass.field.JCSpinField();
			ivjSectionAddressSpinner.setName("SectionAddressSpinner");
			ivjSectionAddressSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjSectionAddressSpinner.setBackground(java.awt.Color.white);
			ivjSectionAddressSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjSectionAddressSpinner.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(null, 
						new Integer(0), new Integer(256), null, true, null, 
						new Integer(1), "#,##0.###;-#,##0.###", false, false, 
						false, null, new Integer(1)), new com.klg.jclass.util.value.MutableValueModel(
							java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(
								true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSectionAddressSpinner;
}
/**
 * Return the UtilityAddressCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getUtilityAddressCheckBox() {
	if (ivjUtilityAddressCheckBox == null) {
		try {
			ivjUtilityAddressCheckBox = new javax.swing.JCheckBox();
			ivjUtilityAddressCheckBox.setName("UtilityAddressCheckBox");
			ivjUtilityAddressCheckBox.setSelected(true);
			ivjUtilityAddressCheckBox.setText("Utility Address");
			ivjUtilityAddressCheckBox.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUtilityAddressCheckBox;
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
			ivjUtilityAddressLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjUtilityAddressLabel.setText("Utility Address:");
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
 * Return the UtilityAddressSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getUtilityAddressSpinner() {
	if (ivjUtilityAddressSpinner == null) {
		try {
			ivjUtilityAddressSpinner = new com.klg.jclass.field.JCSpinField();
			ivjUtilityAddressSpinner.setName("UtilityAddressSpinner");
			ivjUtilityAddressSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjUtilityAddressSpinner.setBackground(java.awt.Color.white);
			ivjUtilityAddressSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjUtilityAddressSpinner.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(1), new Integer(255), null, true, null, 
						new Integer(1), "#,##0.###;-#,##0.###", false, false, false, 
						null, new Integer(1)), new com.klg.jclass.util.value.MutableValueModel
					(java.lang.Integer.class, new Integer(1)), 
					new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUtilityAddressSpinner;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMGroupVersacom group = null;
	
	if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		group = (com.cannontech.database.data.device.lm.LMGroupVersacom)
					com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
								com.cannontech.database.data.device.lm.LMGroupVersacom.class,
								(com.cannontech.database.data.multi.MultiDBPersistent)o );
	}
	else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		group = (com.cannontech.database.data.device.lm.LMGroupVersacom)
					((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	
	
	if( o instanceof com.cannontech.database.data.device.lm.LMGroupVersacom || group != null )
	{
		if( group == null )
			group = (com.cannontech.database.data.device.lm.LMGroupVersacom) o;
		
		Integer utilityAddr = null;
		Object utilityAddressSpinVal = getUtilityAddressSpinner().getValue();
		if( utilityAddressSpinVal instanceof Long )
			utilityAddr = new Integer( ((Long)utilityAddressSpinVal).intValue() );
		else if( utilityAddressSpinVal instanceof Integer )
			utilityAddr = new Integer( ((Integer)utilityAddressSpinVal).intValue() );

		Integer sectionAddr = null;
		Object sectionAddressSpinVal = getSectionAddressSpinner().getValue();
		if( sectionAddressSpinVal instanceof Long )
			sectionAddr = new Integer( ((Long)sectionAddressSpinVal).intValue() );
		else if( sectionAddressSpinVal instanceof Integer )
			sectionAddr = new Integer( ((Integer)sectionAddressSpinVal).intValue() );
		
		Integer classAddr =  new Integer( getClassAddressSingleLineBitTogglePanel().getValue() );
		Integer divisionAddr = new Integer( getDivisionAddressSingleLineBitTogglePanel().getValue() );

		StringBuffer addressUsage = new StringBuffer();

		if( getUtilityAddressCheckBox().isSelected() )
			addressUsage.append('U');
		
		if( getSectionAddressCheckBox().isSelected() )
			addressUsage.append('S');
		
		if( getClassAddressCheckBox().isSelected() )
			addressUsage.append('C');
		
		if( getDivisionAddressCheckBox().isSelected() )
			addressUsage.append('D');			

		StringBuffer relayUsage = new StringBuffer();

		if( getRelay1CheckBox().isSelected() )
			relayUsage.append('1');
		
		if( getRelay2CheckBox().isSelected() )
			relayUsage.append('2');
		
		if( getRelay3CheckBox().isSelected() )
			relayUsage.append('3');

		if( getRelay4CheckBox().isSelected() )
			relayUsage.append('4');


		group.getLmGroupVersacom().setUtilityAddress(utilityAddr);
		group.getLmGroupVersacom().setSectionAddress(sectionAddr);
		group.getLmGroupVersacom().setClassAddress(classAddr);
		group.getLmGroupVersacom().setDivisionAddress(divisionAddr);
		
		group.getLmGroupVersacom().setAddressUsage(addressUsage.toString());
		group.getLmGroupVersacom().setRelayUsage(relayUsage.toString());

		if( getJCheckBoxSerialAddress().isSelected() )
			group.getLmGroupVersacom().setSerialAddress( getJTextFieldSerialAddress().getText() );
		else
			group.getLmGroupVersacom().setSerialAddress("0");
		
	}

	return o;
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

	getSectionAddressSpinner().addValueListener(this);
	getUtilityAddressSpinner().addValueListener(this);
	
	getSectionAddressCheckBox().addActionListener(this);
	getClassAddressCheckBox().addActionListener(this);

	getClassAddressSingleLineBitTogglePanel().addActionListener(this);
	getDivisionAddressSingleLineBitTogglePanel().addActionListener(this);

	// user code end
	getSectionAddressCheckBox().addItemListener(this);
	getClassAddressCheckBox().addItemListener(this);
	getDivisionAddressCheckBox().addItemListener(this);
	getRelay1CheckBox().addItemListener(this);
	getRelay2CheckBox().addItemListener(this);
	getRelay3CheckBox().addItemListener(this);
	getRelay4CheckBox().addItemListener(this);
	getJCheckBoxSerialAddress().addActionListener(this);
	getJTextFieldSerialAddress().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMGroupVersacomEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(342, 371);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.gridwidth = 2;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.ipadx = -10;
		constraintsJPanel1.ipady = -3;
		constraintsJPanel1.insets = new java.awt.Insets(4, 10, 2, 10);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsAddressUsagePanel = new java.awt.GridBagConstraints();
		constraintsAddressUsagePanel.gridx = 1; constraintsAddressUsagePanel.gridy = 2;
		constraintsAddressUsagePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddressUsagePanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsAddressUsagePanel.weightx = 1.0;
		constraintsAddressUsagePanel.weighty = 1.0;
		constraintsAddressUsagePanel.ipadx = -10;
		constraintsAddressUsagePanel.ipady = -17;
		constraintsAddressUsagePanel.insets = new java.awt.Insets(2, 10, 5, 5);
		add(getAddressUsagePanel(), constraintsAddressUsagePanel);

		java.awt.GridBagConstraints constraintsRelayUsagePanel = new java.awt.GridBagConstraints();
		constraintsRelayUsagePanel.gridx = 2; constraintsRelayUsagePanel.gridy = 2;
		constraintsRelayUsagePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsRelayUsagePanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsRelayUsagePanel.weightx = 1.0;
		constraintsRelayUsagePanel.weighty = 1.0;
		constraintsRelayUsagePanel.ipadx = -31;
		constraintsRelayUsagePanel.ipady = -36;
		constraintsRelayUsagePanel.insets = new java.awt.Insets(2, 5, 5, 10);
		add(getRelayUsagePanel(), constraintsRelayUsagePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getClassAddressSingleLineBitTogglePanel().addActionListener(this);
	getDivisionAddressSingleLineBitTogglePanel().addActionListener(this);
	// user code end
}
/**
 * This method must be implemented if a notion of data validity needs to be supported.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJCheckBoxSerialAddress().isSelected() )
		if( getJTextFieldSerialAddress().getText() == null 
			 || getJTextFieldSerialAddress().getText().length() <= 0 )
		{
			setErrorString("A value for the Serial Address text field must be filled in");
			return false;
		}

	//only check the UtilAddress if it's parent panel is visible
	if( getJPanelUtilSec().isVisible() )
	{
		String idRange = 
			ClientSession.getInstance().getRolePropertyValue(
				DBEditorRole.UTILITY_ID_RANGE, 
				"1-" + CtiUtilities.MAX_UTILITY_ID );

		int res = java.util.Arrays.binarySearch( 
					com.cannontech.common.util.CtiUtilities.decodeRangeIDString( idRange, com.cannontech.common.util.CtiUtilities.MAX_UTILITY_ID ),
					((Number)getUtilityAddressSpinner().getValue()).intValue() );

		if( res < 0 )
		{
			setErrorString("An invalid Utility ID was entered, the valid Utility ID range is: " + idRange );
			return false;
		}
	}
	
	return true;
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ItemEvent
 */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getSectionAddressCheckBox()) 
		connEtoC2(e);
	if (e.getSource() == getClassAddressCheckBox()) 
		connEtoC3(e);
	if (e.getSource() == getDivisionAddressCheckBox()) 
		connEtoC4(e);
	if (e.getSource() == getRelay1CheckBox()) 
		connEtoC5(e);
	if (e.getSource() == getRelay2CheckBox()) 
		connEtoC6(e);
	if (e.getSource() == getRelay3CheckBox()) 
		connEtoC7(e);
	if (e.getSource() == getRelay4CheckBox()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jCheckBoxSerialAddress_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getUtilityAddressCheckBox().setVisible( !getJCheckBoxSerialAddress().isSelected() );
	getSectionAddressCheckBox().setVisible( !getJCheckBoxSerialAddress().isSelected() );
	getClassAddressCheckBox().setVisible( !getJCheckBoxSerialAddress().isSelected() );
	getDivisionAddressCheckBox().setVisible( !getJCheckBoxSerialAddress().isSelected() );

	getClassAddressSingleLineBitTogglePanel().setVisible( !getJCheckBoxSerialAddress().isSelected() );
	getDivisionAddressSingleLineBitTogglePanel().setVisible( !getJCheckBoxSerialAddress().isSelected() );
	getJPanelUtilSec().setVisible( !getJCheckBoxSerialAddress().isSelected() );

	getJTextFieldSerialAddress().setEnabled( getJCheckBoxSerialAddress().isSelected() );
	getJLabelSerialAddress().setEnabled( getJCheckBoxSerialAddress().isSelected() );

	fireInputUpdate();
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 1:20:28 PM)
 */
public void setAddresses(Integer util, Integer sect, Integer clas, Integer divi)
{
	getUtilityAddressSpinner().setValue(util);
	getSectionAddressSpinner().setValue(sect);
	getClassAddressSingleLineBitTogglePanel().setValue(clas.intValue());
	getDivisionAddressSingleLineBitTogglePanel().setValue(divi.intValue());

}
/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 10:41:08 AM)
 */
public void setRelay(String r)
{

	if (r.charAt(0) == '1')
	{
		if (!getRelay1CheckBox().isSelected())
			getRelay1CheckBox().setSelected(true);
	}

	if (r.charAt(1) == '2')
	{
		if (!getRelay2CheckBox().isSelected())
			getRelay2CheckBox().setSelected(true);
	}

	if (r.charAt(2) == '3')
	{
		if (!getRelay3CheckBox().isSelected())
			getRelay3CheckBox().setSelected(true);
	}

	if (r.charAt(3) == '4')
	{
		if (!getRelay4CheckBox().isSelected())
			getRelay4CheckBox().setSelected(true);
	}

}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o instanceof com.cannontech.database.data.device.lm.LMGroupVersacom )
	{
		com.cannontech.database.data.device.lm.LMGroupVersacom group = (com.cannontech.database.data.device.lm.LMGroupVersacom) o;

		Integer utilityAddr = group.getLmGroupVersacom().getUtilityAddress();
		Integer sectionAddr = group.getLmGroupVersacom().getSectionAddress();
		Integer classAddr = group.getLmGroupVersacom().getClassAddress();
		Integer divisionAddr = group.getLmGroupVersacom().getDivisionAddress();

		String addressUsage = group.getLmGroupVersacom().getAddressUsage();
		String relayUsage = group.getLmGroupVersacom().getRelayUsage();

		getUtilityAddressSpinner().setValue(utilityAddr);
		getSectionAddressSpinner().setValue(sectionAddr);
		getClassAddressSingleLineBitTogglePanel().setValue(classAddr.intValue());
		getDivisionAddressSingleLineBitTogglePanel().setValue(divisionAddr.intValue());

		getUtilityAddressCheckBox().setSelected( (addressUsage.indexOf('U') >= 0) );
		getSectionAddressCheckBox().setSelected( (addressUsage.indexOf('S') >= 0) );
		getClassAddressCheckBox().setSelected( (addressUsage.indexOf('C') >= 0) );
		getDivisionAddressCheckBox().setSelected( (addressUsage.indexOf('D') >= 0) );
		getRelay1CheckBox().setSelected( (relayUsage.indexOf('1') >= 0) );
		getRelay2CheckBox().setSelected( (relayUsage.indexOf('2') >= 0) );
		getRelay3CheckBox().setSelected( (relayUsage.indexOf('3') >= 0) );
		getRelay4CheckBox().setSelected( (relayUsage.indexOf('4') >= 0) );

		if( !group.getLmGroupVersacom().getSerialAddress().equalsIgnoreCase("0") )
		{
			getJCheckBoxSerialAddress().doClick();
			getJTextFieldSerialAddress().setText( group.getLmGroupVersacom().getSerialAddress() );
		}

	} 
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
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
	D0CB838494G88G88G77F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8BD8D45735B0094658E89A12E2B58693E2501004C423B4A5CA0C22ED09B1FF4CF7CDE2DAF32FED6C8D6DB52D37716FB50DAD076129209245CA929F28A028DCC4C4A500B0A0AA9AE2B1A022B16A489CE0F418194C9CE070F9575E7B2CFD4E1CB9436B2F792F6D621C3D565E6B3557FE6FB391523784A5449A5285A1B6C1107F9D0C9504326F84A129A2E190627C4448C4C175EFAB40A7427BDB636169
	8F34C5EFC6A6268967E213A19D8C691AD7A393B7407BBA61E26CA891DEFC48F1G2DE04642381E650C5B04F212E87A1D7AD4F82E8688819CF9AE95097C7F2C4F1071F3E4BCC22CC19022DBC81E54060DB22E9852F5G41G61ADAC7FDCF82E20126FA9A916731DFFB0C8D8BABB47300A70D171C4A8E8E5E59B64E726B0A1F6F9AB11353FA1D5AEB39C5233G0871F1420B79C760D9D6F735353C595E1E5FEC32596CB6C9ECEE4B6F32DCB3B94D79E6315352AC66DB5BDB1D768EC7C9C96DD74748CB2368F4192044
	9133C532BB6BCCB6518AFC5CEDC4564D06E44ADB900C100E37905C1292A2BF825273G96F37CCFCB893E865EAF8138ADE873211CB25926266B930465EF5B62567830595F42EC8D37A8B687F47CA569D3DABF3F5DCC6AE1A950B22EC4A666815481B8G7C81BE27F131F853AEF8D6DE168E5B5B5B6D36CA07234AEAF6FEE5B70BD6F8AFA9810A0C7BDAEC36BB4D900F14F7E9C31E0EBEBD81ABF73C5F6518CFC276E19C04FEBDDED8B079CBC3120ECF96049498C22EB0DFCC3F227804493E5B276C05D7BC6354D376
	99C1ADFB7190FBDC020E6C30B7F79A164AFC122FAA320B2F123AB6533A4E0577D8B9BE985EC671B7D4F89633FF26B1C35243013664AA46C653EA5425B6FE34103637D6578F23A2AB8D8DD62603552A55253E0C709EE5E5B257F1DDC40A0FD26119AEAFD19DC9FA9950825AA393D9FC7DA113346182DBE4E2AD00996086C0A0C0B8409C9B46587E4DFB7A98E3F5A627D455E1311AC527404A7EE1735F61A93AA52769384DA519EC4DE22349E9B1DD360AAEC14E73DF1B098FAEFB14DBCEE33F99E49C93ED2253A4D9
	6CB660EEEA97A5E83DACCF4216CFC89E27C8298D16F6918434BB84643BEA8B3159EAF2C9279DE613A4AAB998FD7D96D2A71DA6529FG83F8E7FE79FC9B7157E230BF5E9E1918EEC7BF78D3FEDF0ACE68B020B3315B2FF6B89A08A50259CE7C7CB06DBBF2615DE8C79FEF58CEF0A1101E4E797CE24BDA951FEACBB3D15F646C46B6F625147453736C98B3FFE8421829DEB5CE48FB7304EE4C90D8EEE731126E50369D0972A4FA31747212A6CD59F9F67DB6B490E0B25617EAE5447D13782F66F5F5CF96292BB23037
	89406DC09F9BB2B3D94CD619DCA2C669BF1C90E022D971B85373ECD9162CDB022CBB71FB8AG992B32815650FCE857D51A5F9FE40583AC84C87EAEB231G2089408FA084E06EF7686B19257EAA7B75E4A07F3E581D7DCF2D235F772A5BA7826BBB7A42F34DBFC9B7D6074BB289BF31D334C95522C334196145A553E2B2090EF23F52E267E59236923C837A21435070DD9C1F321558E112A42765F207A4560B12E43135AA74F5DB09ACD747E5075205212C4F36923F583B209F627937ECA5ED3859EE3309B418F891
	193E1576A122D5EC87258F5BBBEC924367ECA36D702A58AD77E13B371109DA27495AC17AC3B62F19E8A23EF7B8AD6D902224CB954EBBC97FB231A1C36523E590DFE4FED6G34EAA77695DD5B312F483A7638B07DE236C1EF2C9BB52E5A906C14679ECE252FE0BAED5C2E2F7BB65A1749F89753F564754F605953440A3553E9590467FC103598A05E056D74516D040EFD215CC08F5B2DD666D66E6FE1D9E924FE446FBAC498C2C8277BA57761C3D9EBB534C6591F0CBEBA0B78532A6A2FA73A08BF034BF08E3B9152
	C5G8DAE66D3FF9773894B57FA8873AD07F43A64AB5F6765182F8872547A4C77BA4FE705BC03FD669BD4017902A1CF044FFC870D18EF8164D96AB35FFF70FC699DB01F84286D504B77DCA5669B8A74A00069F2BE21D31D4FD905799AGEF85985E69490F45C852C5B2EF531B2737F6D872BBA48B9985CD92B4E4C707C423064D45BFBB1DAC73086A44B9F7D1D3AA8DF318938394F4E15C3F318A675C81D7030405CFFC66F31DD153A54F2B3A14384F9C04F36EF5B9C4E6A850A23AB0C657E72B477DCA174B5EEC21
	D38EB93EE6E52B5B85433D123D921EAD9627CBBA8E9D179BE37175EC22AF191348E8F90E769A1D3B91DB67025CE4B7785DCD706F17262BF4B7822E8DE0B8C09C65D7F4084C4DAC12581ECF7E54CBB04FB95CE632350A665CAB26CED33E29CB4A27DDE87EF1209FA1EFBB65FA1935F3AD53D1241EE713245C1D0DB8EF9E3B4EA84B1D735CE8216C76BA035EBAE6681ACD06E8B773E5DA3762539B13926396DF78B8095967FF886DBB8FF9823A513E87EF6859B787682CDCF5B916DB88380CEBD86E37A5FA656A2EE1
	39E53C1C99F0013CDCCA0D365CBC20AD6365C2AB30DC6AF568D72FE3393502DE399B57315CA5A3167387DC94AFA7B4691593EEE03974CAAC178E385A9BD8EE49CD3DF203EFE03DDF2F9CC83DB319EFF31923005FDCAEB3C437AE12EF224C722AC2F81AE8471C4F9EF52233456EEC7796DAC94932D8B95EAEF411757A1543EDE27355AA3B3BDE04F5003552ECF60AAE5745CA8D3FDA5F8CCBC9084A36BCD20536EC84BD9BEE22AD7B5DEADB0683BE8CE0BE3745F018782F59649425FC7AD70EFEA2551D6F6A024901
	AC78B0214A0630B95316535AF568621B4A5CD93865BD0FE66B33905AF7255FE26B33A40E8F2378BC951E59D6C271A4DD8D3410DB3826981BCB66812937A393F3810CGCDGEE00D137314FBA33E31F504B1A6DB87441CE1B49DA6FB169117B290C9C62A7182748690DB94447F41EA2605C71DF4A34FE883B6DE97BDCCD1A157B35D739FCCDFA29269C0B1F72C6228B9DC7D455D4D2F282A3CA1EA7DD6CD04F13D27D58FA2CG1E55GECEC59DC06E34BF9BF9CC3EE9ED50FA191EB8A8DA9BA7D9D257BB17D427C14
	7EAE0F6F5B546EA26362D220255D85F584D08D5086B098A064AE2C2371B907FA2BA39AF78D7643002B01794BE16DFC137B74470786D69741DE6534F591F517E7B9663F289B683F05F761BA67C47940F4E872AA2755A163EEBD9DB616228E85F7238EFF9B208E29953D695026510145DAC629DE2C79C15E003BB1562AEAD04FD02E67A4AFF9FD53F3FA2FFAAE527555F181F5D84EF5E8B18ECC87492B1CD60722C1FABA9CECC29DEA87218E1FD58ECC07DC2FF2DA9D866B6A10F3ABD9669148F5F8F700BA7C07D739
	4D1A74BCDD9D7E0D371D45DC07472B862643842FF2DABFE48D566BCB33F3C99F255317B6401A7A28C5341AD9FCFF5024975FE54033EEB04637550DBEED839C5B8BBBC0676EC1108E633858B384B79F52CB86235D4F31B1719E68A781EA6EC17C940AA7E3F61BBC77E1F8E313F6CFC98D69B28FDF745C9EE80ACF4F76A8A3CE4E670B1E50830DG6B7B1F49753D37B1A9D7FFEFA3BCD7FFEFE3DA6EA78C8FBC54BB30C87FF9EE4FFB9F7FA7D7EF6FE3933575153F2F213CDDAE51C587A332D013E96FCB6B851DBD10
	1326F6D17653BAE1652195F21EA3127DF0A10F3F8FF7E668471FEA7DB73283634AFC8F2E7F5E5BC062A29052E1GF3F83CEC771AD3503ED02E6FE5BA754E466AFF59CD660929C3A07EG0CGCDG6EA1B8C61BF776BA0FBA626EE19E55384BF7BDFF3BCB3F1E1BF711352B4B5E61EC9635F5F9E5D78627A93381B245A7EEE7F9DC343CE4F2360ADE31F00D16E7B4DF65876D7295AF2CCEDF732853FD3C5D3CF6C32F8EC68DE13E8F9D62DD870C5FDD65EAFEC51C5F56D2BDFE53E5BE73FC725BDF216637DF40FE37
	2E9463A9FA8846D35DB192CF1977C6A69683545D0B71F42CC6CFF65B3DCC660DFBFD497E1D076C835C1613029EBFFF19CF30CFFEE30CEAFE87B93F32A6BDFE61B21FB9BE791D2AD4735B4B79655D526337D06633D4FEA68FD578B2FE69D5EAFE451C5FA25DD858A817AF986ACB3F799E7C3E60F517FD966B2EFAA8565D5AA4FA768669G005021D8F75F3975E44F12E54E0767E20DFCA67B2B53E4AF56EA6A36F7C8707C81BD77BB0D631FG698CGA3C0438F547BDB4FF711F5C20B53548E4D245EA1B68BB57751
	396199AC9B867923G2281120116FB9F2EC3FFC14FA0D82F37D75643F00034BFF9E1FC5AE5EA95710C1A51B71610BE65EB91B4D5D1196D019DEA5B7DC1CE70FD0A2D6177F97B1D2DC14FE490DF4E390F2DC1D3B8DE247845AABCDB1B7E0E7643A41DBAACB251B18C4778923A2E1A8769C59C77C8999F7707E13F7A2B5D3E7B455F6C566F9723F76B0FFFFF585D737877675D3E7AB3265F229B2833F1B86AFC64A66ADC4BF1452518AF10639EAE45FCA143B17ECEFB74DB4BG1FFA3F62774C7BFD5541AC5A97955D
	4FFC3D02635F2078BA951E55C1A345537DC4204D3D9FF53AD003FA967D90F1AF57201EC60E93854C674FF1BF97B0DFA04755B7E13E459C9756047916F15CE1BA372EF9G4650879097F28B7319B92E33046042A0BD1763BE26F530G520BB96E922F03E58F20EFF71DD57B3646BFB2713C3F62CF073FAF5F8E21FD45E0FFEDFC87D0FC303FB63EB312B03EE781AD77C154E9A81D6B8E07F4A047FD6066B140F1EF5218DF86695407F02FEF78E93C471289382607709E4B5ECB9EF3F13269D5D2F24CE9B1D719DAEB
	E9EF447A40F9F4CECD4A7BBF04FD2021111E79C3FAA1403207F0AF433D8377B2C2470F96EA7D3E545D8FDF58D0E8C87B9173D9510F143D0C100E3FA4B11965E7D0268368A1BF22737DB36A3DB552A73DEFE06B8EC6FF5403CE5A5ECEC1CD1F6BC1A77ABE6BD15E6031E71A20AA4FECFBB59BEDDB7EEA08E07D4997BE777A1748B6258730E7DD63F6A16B3E0F13D89DCD26734BDA20DD8A40FAAB2CA03841109E45F121C62C5F08G0C43EFB3683CF1C4E4E281C0830084B0EA84522FD2FA4488F62E14AE1F871133
	94C67F63863DB58661C567CCB2BFADDD8D29AAFA03A6AF13111C21A78357A7A932BFFB1291276169BDE64EBEC24EB3A5682C970CA0F74EACE4BAA898FFCC7CB6292C3A975E897213C0A614A130DD2E4BA2F87FEBCCCC8CF9E73C9FAFD2F81F3F599B6F249EF88F0577F572BB63BDEB3F42FBE2E9EF3CD77440BBG5EBF1675E671E3AD45B88B7AB146546FD2B04EE6F1DC73AA0C33689FE31C4CBEC64EBD0BC7C20C81B8G060FC45A5B14B6D8BE9BA467FEF9BC3E4E9FA3F5CF72FB7405A375E32A41879EE178C8
	3D786A20B27462C20FC7EF6BE2EDAC32FAC9BD28544BDBB53D550B9AC2C0E782D8AF91A3D99F2F54F9D931427BDD21BF312A659D65457B62978A6FD71B7A932BDA5E73D43CD91BBBF74B334D5DDD2248FA79D6FFE2D7ABEB21172C7BCBC85F4A8EFB489258E4B3F39B47158ABC5F5A1B1EBA3DDB26685455EBDBCD91FC3727452053A772BBEBA715E731BDAD9B09EDE7F29636270D23901739034FF9C6E13B981EC47AE5FFC8CF87D88A107EB02C7D9EC67AE8CAEF00349BA070E1F62E9D41691B131A6472249C5A
	C0F875945BFAD07C30E71925DC4703D48656D5DF5B0F9E24E85298E76B04475C1E75A514AB75955F5DD3FDA5E8E2E8A1583174E1D6DF0416BCBAB271EC2CBA0616F49139220DDC3DB08B3923090C3F15613CA2FDB45A7D105739DAEF404A858D305CBB7DAE47660C73681C4BBADAE9AB8C7FAE453BD5F8B66FCBEE44F95FE020C50F46F33C774A497A3076114844B6GBF00D1G61G739E41F552703C8221177D27863B239696D2D655520759F7784C406E6A4EC84279D5581431C278100FE2FD1D03C5BFA26F8B
	8432E7F443F60AE776F59A09FD45C0EBGF08378838483C48422FD73727218FD644ED025A4191A5B68AD8AB42E51A2F6A9E83238E6B155F416846DE53B13AE7458DAFD81BF771B8B7C2381B247407CEB8C5BC31667ABD95EEB7CEFAA30DC6D983646B74353AD17E3EDE22631BF6303BF148D9E435A84C907437B87B0A778B7G264716A4F2AF7634E431DA24EEF9294F0FB165F673A7116429E7DBF4BE7254527DCB3A9E5664E0748DF5647B01EACB27452543046539E9A7F2CE681E4B8B82BBCF8F4A503B9BB0EB
	8C2EB7DE7FC6FD4FBFED74D68336812D2F660FE13132E80C325E58A230BB064902720F75CBD32A849E173FCE45385C784CA3C2755B9F45EE77A1A363D1463B6DD1C5C6741565C90CE7D635427F983912A0474188FE169774A80B1FF0F84EF9141DBF30BCEBD3B14F22C7D92CAC07E75AE3AC8F0B15EB2BFA6BBF5531128BE54B9ED3E2258E5EE7C21C3C48E3652E5B249C5D6F592B2D7FE0B32795EAE90F2F6134FDDA5A698ACEABD2E83E6B610FC71577CDB54A7B60634A7B13A7147759DFAAFE351FC51F1DFF0C
	79D502675047581D8C166757D95C770FB13F0643F34EE32C7E18DF8F6E684B7C8FFD3B884AAED379B5B50835410F39DF7F34B1CB53F66AAD36D62BD8EB3109D59668D7DBA1C1B757D87E28269C6F36642B086C67488B441FA7EACD17C52BE743E274716524FDAA27EEFAF9264512B9985BA3A4BD867488821EBF3F1E24BE1F4B88E2BEBD8FCF87G4BF36E02DE1E21708C8C427BB265B96A769DA667194FF9649D521E83927AE2208B0FC7A6FA474D128A5F31342CD690B08E579FA27555E2323A481991EB67C1DD
	6A3E24F865C15DBB2C541647D9FC64494F08FB374BE37BAF4995A1797DBD557B6A461E4E767EDF0149483923B2646FFE4AFD6F1FFED82E3FFF7AD1397E7EE9CCF94F6703C9953E6FC62F2ED05F0DE6637E1A13A4164CD0A7FEG41G61GF39E47B1BF30E21BC047FC1D717E0839151CEB11AFC5A414FF5B685B56A1465E3FAFF940C86EE2DE36CB92391A5CAAA2FE04115417E4F7D024FCD7181E37B8AD2DED924BAA6303689E10D5EC21E8B62F0B6F44F9DDB45816B99677735E5FC4762BFA3E6F5AE0112CE215
	BCFD114FB8EABB71DEC053D85C5F3BCD6FB17AC3BA84E07AD85CCB9CD401326783AE6D89BC0BFF35826755B5CFA00F0FE8DEBFC80F821878C4DF5B6B128A56DE477EC360BA3E1DC16844C7D1CF60BC769FF58277D3C1A579BE2151F4C51E29322B60D83BA7CC1D26FFDC3E82B31A86D3AB0F17EB2768009C8CEBB7GA3C0930095E0B040A800D00069G738116G440F03B182A08F208C208E40BC8E63AB26EA27AF9DGA9EF2C52A0F31AACA44CBC3E13A23EF3832F09635027897C2ED874B874690194FAEEB89E
	560EGF56331DECAD211C79B600246A30F3BD331DE660FC79E93E95E34A7A393F3810CCF7637DE9A6B852E7BD767D0EE937071FF92650EB8476FD0BF09F2857A4D0C9082BE84A888C15DD7E4A30F9A407905A00FD35928FBF8880FCB1AF78924131F0258FF2A3F3AF7B788820E556F4697775ADE14E1F66F814F01166918140CF696BF05BABE79AD5151816921GC1CF21BD8FDD403C61BC6F309D549EC8A7BF8D76BC0DED7943969F779468084E78F5646059E47153A843FC88E5B81EC699055DC4C6882427834C
	FF9A5B4FDE3A2F3F8452294FE08C9F7AEAF78FB1DC6734BBC427544D3074EB87397DEDB8693B2F5CFC32773EB267A43D4B8EFDDCC337C33E8F10F7122C196CADADAE117749FBCF26F15CF1D71D5DE5A10B94B67E2D6C9C58FABCE30065CE51F2F94F74ED8F46887958FA7DC35A37B6BC23DD47AF2778CB4FE857717714E37B3082ED6EB338EF71832FBBF9FD53FB64G4B4D6DF7B9E6D7BB3D23DE942A353713620D21DAFBE37898D38B340921E86F028CA2BF7DD9CFFF674A69B2F856E9E8ACE6FEF84AF7EC0EB8
	55FBECBEFACAFF9C8FBE65BD0EBFF5CAFF9C1FF0CABD0EB3DC58A952CEDBE1DCE86279DE6438339CB70D72A438B396335446713328DE84DFA39201081FCD756A2238A67C7EC305BBAB63661DA26D8BCCEE33BB316CBB27487EDC0B45EAE569F7E99E0B8D1A9B7747EF280E9607C925777B9CF716474BCC5DFBB43D47599B83AC37125E45B2BF5B377668FE965B6353F4CC9D7E2CB6BEA7D37C28E73571D9160271998C346467789D41540169FDF200656E6DF7B9E6D756AA3A0F781C565E7F26F877F3DAFBDF60F3
	0B41C00B6676561F9B185E2681169BCD4B55CE68DB7DDE1A0075AB51F907B4C1EB6FCD0A771B2035B7B18B6D9D8E34A593505EB5598353FB53G4BB5763B1CBC3E50B9D243C42F710562AFCD749ADF787C4B8A343993515E54E4357C2267BD7DDE733C2FBB8F1FDFA07C4EBF2F151FC571ED4FEB65BF7BAD4A1700B67FF914BF7202DAFE59A4CFF9F513FC490F21F3AA73A42D7C649CBAAF1B24157F608E14FF83E88BA7217CEBB9FA7E2F1E4C64B64153BAD96FFC526275ED031FAADF40E4DFFA0FE77334495A3B
	A2E1949FB5D9FBD724389B6F0AC4832DB68C67F087CE931EFCDD4D26E9F2BFFD68B4D9A3DBECB0EDB2D9AD57686704751051564A4E2E5A68DAF0A2700A8A43732C24F2FAC78652CB4250B79F51F39B76EE28D05E07155F79FD0E606FC1064B2858F4C3751EE03C7332B75FD1992CCEBF246B6965D01FE9BF41FAAE7B946FDBE5F15CB5B31EE596F05CB0FA2F408F52819C37152ED5C3A0BD1D63262D247CA09D7D930C1B09A90AAFDFD43DD7255CF93FFE73BD4820D7206577FD2A770854BBAF7B4DBBAA03556973
	F4DD3F946AB37585FEB70DFE1F1F89692297F85F192558BEC8751E1AF5677D107DBD4878A8DB314930C3F99F167DBD74FFF7D4862B536D49F85741486B5912426FD472FA5EFAC131FD176AFD7237F75E8FF36E288CE673F397508FE15C8FBF3804FE184F7D101023583EDA75BEF2471D774344BBAA03597CA05DEB4A7DA94C29FF0AFE78F38E7A211A635E69427B27819C67F7886F4804F0DCF4B9161D73D374E1E8B7191BEC8C0FCCAC8338846087909C4E3F4D2274D910DE84907FA20CD3G55AFA23D3C3BB7BF
	A0CFDF74B29D5AC68F9D4E74AA03646B69FE235EFD9A75DD24F5426DCED2DEA2BB75C76DF41F185DDD99C967E6E6305771A25EAB58DC1E00F985BC679ACB4F1D646F2068186E92496F1A08E646E76B2778DBF3C1400715795999D2061DF7BEE86E4FFDB76D3D290940F33D203E07B336547B9E4E0B2538379B41EDA9AE53373B0E4EAD6692DDF95EB7D7FA5B7D05D931BB6A1CBE2F05F47CCA1D921918B185F9BD365D1B975D3A114F11EB13718C32E68ABBC73E84CF89005C57E2BE73CB694BBD8DBCA39E8EE503
	26A8E744E170FE7D2FB1B17FB7A6A60649FC1B1F1DCE17E54E076762A96CFE981399F429AF777BD0E67CD430F92AA2B3EF2AA21379A1A44773EE56832DCACC2C4A6D4F7D3E252A3BD9F47DB5D50989D68FC9EE2C7F6A29F8967F94BF6BED1A4A4E629D709CBCD5FD969FD35E1F3378C0A8BBD1E5732C295AFB06E52D641C4073BE8C5DE21769AF9856FB516B9D969BBBF7E4F9321C040767FD99B50FE96757FB5135BCDEBFC04F6BA5BC0FA7923FA6BF2BA15F0379E337761C1C5C8D1CBB157F3E48F5B5FD51D4C6
	4F08C07A915D72F91CFEEF37DEF9A327DFAB547E2E8521B7C4E07F71B72B5276BEBE34951EB631AB137F1686E903395BAA499D03EDF919FB617FC5BBF97E62CFE95CB9BBC446EB2BCD61B537D4FDCE4F68FFF1A874C9AB7BAA0B157D15CBA9FBE3FBDF4B327536144C4F5808DFA2704CA5A2997B537991F8667242CAFA4676D2E4E2AE0071257E1ED32D6890385C188B7C0C8D78783F04F20FDDC439D1AF215C37AE52B336E9D077G85537AABB72FD3603EBA572578EA33DBDD8FCC2FB78E71F3BB1063B78D757A
	341C1F5BCDC33D0E51B30CA510CEFE99460B177B2B57CD7213C572D9700EEB7C5C6BE5BCAF3BF500CC4279D95ED5EBEB7E15E6F25BA03F05B4A772E39D2449513605FC2A2E63193263E5547DC6B7728E6138E95D18EFA147BDDFC8F0E40117B11D2FF91287364FB7F40065F2D676275C0D69389F3A0B6EDF7BCF576E0F6D237840695A7D310815D84FA1C0CB1D41673C978726773A81162B6DD7B9478C347744A57A8D568C2D3D75946FBFC3EB6F020BE8EFA05004C87E5D03573EDE5F747E6DG4BED767ACD115E
	0059B51536B5F324565ED9946F08545A7B95BFDF3C8134055C5E7F74781636FA263C6FA9BF2DB33D674A72B74C57311C1FA6CFG24C3GA2E66A6D1B5E552D16B7D74E33C8FEAE73A9EFA6AF17BA4BB3CFA6240BGEAE66949FB21D0EFAEFFFE166F7501C3268D5664998569D00069AABCDB671433BDB040C74FC23F9628763D3ED03DC72E3C73EB45DF5DD1997277C4975166255C663ACB0A1DE7D56F3F39F867ED7E78FB10B1C3359FBEDB75DE7FBD6CD5DB6F288CD62749741BD861E7707EB37E6D4B913EDF4C
	F199C779FEB147DD20632C9F2483B94E78B5BDE700F494476D665F6CAE6038B78E92DC5E4FE11D7FF3440D6AC6DD1AB8EE867FD6584AF1955B101F1B63FE590D3AC4A97C8E20AE8BB86ECF877837639C77DA976AE27C85626653F9C083C9F35C21FD84670034BF47A53AD167E00EEB625FB207F35C9409601223A293B7C6A1AE0EFEAFDAG696AA8DC331E56D97F86F58B7CFD66B572AE5F79275FC8351029ADDDFB6D27F559C478C7617C7D5F2F137CEC0D3567E8FF567F5A35DEC014F60D65F71D4C139CCE31C5
	F4BAC5F33D659A51196972B00D0DA0A8934AEDBB73B53DEFE1EA3ECAFEBF1FFC7845725AE94C4C07FC0B22F08DDC5563F7E7893D689A7F0AD657680324C3EE3758AC6D9D6DD4D3A6DBA8A63253A07FC6G061BC1E32986528D2F209FD75CD47C58382DA75D12FA51ED30CAB7A6AF0E4EFB7D819F2868F0GFD92758A7AE445411E642E68C5EEB47079AF79BBD2A6634FB46E1782DE180DF57447C24F3D023CDBCA3CB56CA377FA4D96975D70A1D74C8F5B2D640A094C4FC65BC79E70AA1E0DF66C216D6392242D33
	518EFB2F5FAF2A3F898B1C6D79BD58C4C8179A84612FFCFF638D095DB133C8C4B1A6B7DA42B5D484376D338E6F7D2A3D9D382619B39B6BFA2820587C6491F237D8E7ADE90625243D959613853018AC5C096D7305D8252D3EE3D0786C7FFA3316CF3525DD34111B7F397B01C721426375E4255C76DBE4FD4E4BD00767F735D9A4F9CD7D27950A3C4C9425DCEEF75F655D3BCAA97713267E597B74EAC57EEBEB94BE779F680BFC16777787FA36F1C91AA2E35DDAC5463705FD3731FB1DD2EEF957275AF2D4E6EEC19E
	7CCF59D3F8F33DD22636298FFB8AC41C4EB7AF3856DD76F705DF0C04FBA3ACDD5321DE07337FC60BE1738350E106FEA0A4C8429AF91BA9D35EE71204D574523D12CE438FC994D4927D9EA7F3AFFFAB64EF7B78DB117AEDB57BDEC4E191DB60F28AEB384D47690531051D7BCA625EFBA24EA0449F979259853A63C2BC3985F7DCC82017640E8BF1125D61F648AEA0B5C83F60F5FDA75C92D67A44467A4C1BADC326AE561303B7F62B8AEB6058F41534D80EA59852F24946C39C47126A412741830B9485EB10B18462
	2126E4EFE652DF57013A14379D95AF27308A1069ABE53C4556CAABDEDDA3A5D09D2BBCC270E70F6F7F77B97F5A680A93920F8B1F1259GAB81E31C197C381A7CDF23B8AA2CF058ADC084BC54150BBE92DCD237D52C9552DB3EA63FF66E12C4F3A5DEAF813556539F4D5DC3EECC6F29B1395AE87FDADAAB445B48FFB7C3D84B48D6132DF58F3BEBD2CAABBC3672922D1C55542DCEE19D5517F04967DC56CDDD133D75442270B841F0D4D8E9351B4CC7CD4D125DF9DCD8A1350121EDF62B596D28B0D2C649F420A23D
	DBBC307B1C1BCF0C4DA76390D77A2AFB4C70D5512B0EC28D381D429E1A138CE679BC0779F01B498922C467F1097CF7C06C4EACCE3BEB33346335E39B4E9B04D859C8D2231B5D8EC9C8ECB135DB2C5D70D2CDBBC2DA575BA0492EDE72C6C21C11FF946C11FE7941ABE3FFD536E4B1B0BBAAA450DF3A85155214DCA7BB5AAF0B4EA121A3B62584AC9CC6B4F698B1E601738A49E93239482F978373CC0D4DC8622A677F68150291CF7D659CD5DDA9AA89DBD8A5C151BD2D223DFDCFE5CB0B45A6F286D0E340FE93E7CF
	7264EB722CF5BF387E441BB117A1422A05387602827AF7AF7DDBC87E6E25183D943337902AED095063BFAD5D0DB6533024091DA4065C64ED07DB374FC820580DDE61C630195EF1C350D93C76C8AA077C59CD7E6451C279746F5EFE6BCECA6D53687C057BFF33F695EE0F7E0BE86159FF4569E2F1D0A3BDEC22C7DF07F905BE0DCAE91A6E2F84D01A510D20F2C936CCA7B9ABA9591917172772A8739F5D90776151B8EF0F7E737CE81453A75D9E9ECB143D11888DEA2D770C2472076F3C9DFA31BA5DA098E050EC93
	59A81950C52EFE53EE134C8E8A216CEA4FD9C96C388D03529E5857E190120F12EE486AE8B39D17BB14DAA165B29DEEC5A7648527A90D1D16053E5A508CAE7FDC41B793BC2356BBCC4D24BBDFE1B6B92FD28E441C46EF0495AD4E564BD400A4983EB97A8D0825A9C2EE2A95D6CAED641BBA522FCA428605F59D797AC9A63011C000B9FA0C037E73E844745CD24061351E9B7F3F2E0AAFFAD6F1BC2FE2EF3D55364579306DB47589004AA7DAFB7FBFF792E8799537C793A7414DD62D6C1B5A1D37555F54AE3BA74367
	B73539EF495FF13C25FCBB4F3ED37AD73A0F6386FC40DBF847B5FF5BFF8B3E3F772BEFB7D92D1249FA3552E6A65F7ED5F5CB2272DD69FB5B88FF976611090C767BEDC46FAB2AB27F83D0CB8788653367BCC7A3GGE4F1GGD0CB818294G94G88G88G77F854AC653367BCC7A3GGE4F1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG01A3GGGG
**end of data**/
}
}
