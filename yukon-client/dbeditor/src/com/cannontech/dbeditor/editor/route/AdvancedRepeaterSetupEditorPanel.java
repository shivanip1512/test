package com.cannontech.dbeditor.editor.route;

import java.awt.Dimension;

import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class AdvancedRepeaterSetupEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjCCUFixedBitsLabel = null;
	private javax.swing.JLabel ivjCCUVariableBitsLabel = null;
	private javax.swing.JLabel ivjRepeaterLabel = null;
	private javax.swing.JLabel ivjVariableBitsLabel = null;
	private com.klg.jclass.field.JCSpinField ivjCCUFixedBitsField = null;
	private javax.swing.JSeparator ivjJSeparator1 = null;
	private com.klg.jclass.field.JCSpinField ivjCCUVariableBitsField = null;
	private com.klg.jclass.field.JCSpinField ivjRepeaterVariableBits1 = null;
	private com.klg.jclass.field.JCSpinField ivjRepeaterVariableBits2 = null;
	private com.klg.jclass.field.JCSpinField ivjRepeaterVariableBits3 = null;
	private com.klg.jclass.field.JCSpinField ivjRepeaterVariableBits4 = null;
	private com.klg.jclass.field.JCSpinField ivjRepeaterVariableBits5 = null;
	private com.klg.jclass.field.JCSpinField ivjRepeaterVariableBits6 = null;
	private com.klg.jclass.field.JCSpinField ivjRepeaterVariableBits7 = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjAdvancedRepeaterLabel1 = null;
	private javax.swing.JLabel ivjAdvancedRepeaterLabel2 = null;
	private javax.swing.JLabel ivjAdvancedRepeaterLabel3 = null;
	private javax.swing.JLabel ivjAdvancedRepeaterLabel4 = null;
	private javax.swing.JLabel ivjAdvancedRepeaterLabel5 = null;
	private javax.swing.JLabel ivjAdvancedRepeaterLabel6 = null;
	private javax.swing.JLabel ivjAdvancedRepeaterLabel7 = null;
	private javax.swing.JCheckBox ivjJCheckBoxResetRptSettings = null;
	private javax.swing.JCheckBox ivjJCheckBoxUserLocked = null;
	private javax.swing.JPanel ivjJPanelAdvanced = null;
	
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public AdvancedRepeaterSetupEditorPanel() {
	super();
	initialize();
}
/**
 * Return the AdvancedRepeaterLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAdvancedRepeaterLabel1() {
	if (ivjAdvancedRepeaterLabel1 == null) {
		try {
			ivjAdvancedRepeaterLabel1 = new javax.swing.JLabel();
			ivjAdvancedRepeaterLabel1.setName("AdvancedRepeaterLabel1");
			ivjAdvancedRepeaterLabel1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAdvancedRepeaterLabel1.setText("------------");
			ivjAdvancedRepeaterLabel1.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedRepeaterLabel1;
}
/**
 * Return the AdvancedRepeaterLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAdvancedRepeaterLabel2() {
	if (ivjAdvancedRepeaterLabel2 == null) {
		try {
			ivjAdvancedRepeaterLabel2 = new javax.swing.JLabel();
			ivjAdvancedRepeaterLabel2.setName("AdvancedRepeaterLabel2");
			ivjAdvancedRepeaterLabel2.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAdvancedRepeaterLabel2.setText("------------");
			ivjAdvancedRepeaterLabel2.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedRepeaterLabel2;
}
/**
 * Return the AdvancedRepeaterLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAdvancedRepeaterLabel3() {
	if (ivjAdvancedRepeaterLabel3 == null) {
		try {
			ivjAdvancedRepeaterLabel3 = new javax.swing.JLabel();
			ivjAdvancedRepeaterLabel3.setName("AdvancedRepeaterLabel3");
			ivjAdvancedRepeaterLabel3.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAdvancedRepeaterLabel3.setText("------------");
			ivjAdvancedRepeaterLabel3.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedRepeaterLabel3;
}
/**
 * Return the AdvancedRepeaterLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAdvancedRepeaterLabel4() {
	if (ivjAdvancedRepeaterLabel4 == null) {
		try {
			ivjAdvancedRepeaterLabel4 = new javax.swing.JLabel();
			ivjAdvancedRepeaterLabel4.setName("AdvancedRepeaterLabel4");
			ivjAdvancedRepeaterLabel4.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAdvancedRepeaterLabel4.setText("------------");
			ivjAdvancedRepeaterLabel4.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedRepeaterLabel4;
}
/**
 * Return the AdvancedRepeaterLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAdvancedRepeaterLabel5() {
	if (ivjAdvancedRepeaterLabel5 == null) {
		try {
			ivjAdvancedRepeaterLabel5 = new javax.swing.JLabel();
			ivjAdvancedRepeaterLabel5.setName("AdvancedRepeaterLabel5");
			ivjAdvancedRepeaterLabel5.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAdvancedRepeaterLabel5.setText("------------");
			ivjAdvancedRepeaterLabel5.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedRepeaterLabel5;
}
/**
 * Return the AdvancedRepeaterLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAdvancedRepeaterLabel6() {
	if (ivjAdvancedRepeaterLabel6 == null) {
		try {
			ivjAdvancedRepeaterLabel6 = new javax.swing.JLabel();
			ivjAdvancedRepeaterLabel6.setName("AdvancedRepeaterLabel6");
			ivjAdvancedRepeaterLabel6.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAdvancedRepeaterLabel6.setText("------------");
			ivjAdvancedRepeaterLabel6.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedRepeaterLabel6;
}
/**
 * Return the AdvancedRepeaterLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAdvancedRepeaterLabel7() {
	if (ivjAdvancedRepeaterLabel7 == null) {
		try {
			ivjAdvancedRepeaterLabel7 = new javax.swing.JLabel();
			ivjAdvancedRepeaterLabel7.setName("AdvancedRepeaterLabel7");
			ivjAdvancedRepeaterLabel7.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAdvancedRepeaterLabel7.setText("------------");
			ivjAdvancedRepeaterLabel7.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedRepeaterLabel7;
}
/**
 * Return the CCUFixedBitsField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getCCUFixedBitsField() {
	if (ivjCCUFixedBitsField == null) {
		try {
			ivjCCUFixedBitsField = new com.klg.jclass.field.JCSpinField();
			ivjCCUFixedBitsField.setName("CCUFixedBitsField");
			ivjCCUFixedBitsField.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjCCUFixedBitsField.setBackground(java.awt.Color.white);
			// user code begin {1}
			ivjCCUFixedBitsField.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(31), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCCUFixedBitsField;
}
/**
 * Return the CCUFixedBitsLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCCUFixedBitsLabel() {
	if (ivjCCUFixedBitsLabel == null) {
		try {
			ivjCCUFixedBitsLabel = new javax.swing.JLabel();
			ivjCCUFixedBitsLabel.setName("CCUFixedBitsLabel");
			ivjCCUFixedBitsLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjCCUFixedBitsLabel.setText("CCU Fixed Bits:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCCUFixedBitsLabel;
}
/**
 * Return the CCUVariableBitsField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getCCUVariableBitsField() {
	if (ivjCCUVariableBitsField == null) {
		try {
			ivjCCUVariableBitsField = new com.klg.jclass.field.JCSpinField();
			ivjCCUVariableBitsField.setName("CCUVariableBitsField");
			ivjCCUVariableBitsField.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjCCUVariableBitsField.setBackground(java.awt.Color.white);
			// user code begin {1}
			ivjCCUVariableBitsField.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 4, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCCUVariableBitsField;
}
/**
 * Return the CCUVariableBitsLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCCUVariableBitsLabel() {
	if (ivjCCUVariableBitsLabel == null) {
		try {
			ivjCCUVariableBitsLabel = new javax.swing.JLabel();
			ivjCCUVariableBitsLabel.setName("CCUVariableBitsLabel");
			ivjCCUVariableBitsLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjCCUVariableBitsLabel.setText("CCU Variable Bits:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCCUVariableBitsLabel;
}
/**
 * Return the JCheckBoxResetRptSettings property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxResetRptSettings() {
	if (ivjJCheckBoxResetRptSettings == null) {
		try {
			ivjJCheckBoxResetRptSettings = new javax.swing.JCheckBox();
			ivjJCheckBoxResetRptSettings.setName("JCheckBoxResetRptSettings");
			ivjJCheckBoxResetRptSettings.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxResetRptSettings.setText("Recalculate fix/var bits (Reset)");
			ivjJCheckBoxResetRptSettings.setActionCommand("JCheckBoxUserLocked");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxResetRptSettings;
}
/**
 * Return the JCheckBoxUserLocked property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxUserLocked() {
	if (ivjJCheckBoxUserLocked == null) {
		try {
			ivjJCheckBoxUserLocked = new javax.swing.JCheckBox();
			ivjJCheckBoxUserLocked.setName("JCheckBoxUserLocked");
			ivjJCheckBoxUserLocked.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxUserLocked.setText("Do not allow auto bit change (Lock)");
			ivjJCheckBoxUserLocked.setActionCommand("JCheckBoxUserLocked");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxUserLocked;
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
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRepeaterLabel = new java.awt.GridBagConstraints();
			constraintsRepeaterLabel.gridx = 0; constraintsRepeaterLabel.gridy = 0;
			constraintsRepeaterLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRepeaterLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRepeaterLabel.insets = new java.awt.Insets(0, 0, 0, 15);
			getJPanel1().add(getRepeaterLabel(), constraintsRepeaterLabel);

			java.awt.GridBagConstraints constraintsVariableBitsLabel = new java.awt.GridBagConstraints();
			constraintsVariableBitsLabel.gridx = 1; constraintsVariableBitsLabel.gridy = 0;
			constraintsVariableBitsLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsVariableBitsLabel.insets = new java.awt.Insets(0, 15, 0, 0);
			getJPanel1().add(getVariableBitsLabel(), constraintsVariableBitsLabel);

			java.awt.GridBagConstraints constraintsRepeaterVariableBits1 = new java.awt.GridBagConstraints();
			constraintsRepeaterVariableBits1.gridx = 1; constraintsRepeaterVariableBits1.gridy = 1;
			constraintsRepeaterVariableBits1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRepeaterVariableBits1.insets = new java.awt.Insets(5, 15, 5, 0);
			getJPanel1().add(getRepeaterVariableBits1(), constraintsRepeaterVariableBits1);

			java.awt.GridBagConstraints constraintsRepeaterVariableBits2 = new java.awt.GridBagConstraints();
			constraintsRepeaterVariableBits2.gridx = 1; constraintsRepeaterVariableBits2.gridy = 2;
			constraintsRepeaterVariableBits2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRepeaterVariableBits2.insets = new java.awt.Insets(5, 15, 5, 0);
			getJPanel1().add(getRepeaterVariableBits2(), constraintsRepeaterVariableBits2);

			java.awt.GridBagConstraints constraintsRepeaterVariableBits3 = new java.awt.GridBagConstraints();
			constraintsRepeaterVariableBits3.gridx = 1; constraintsRepeaterVariableBits3.gridy = 3;
			constraintsRepeaterVariableBits3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRepeaterVariableBits3.insets = new java.awt.Insets(5, 15, 5, 0);
			getJPanel1().add(getRepeaterVariableBits3(), constraintsRepeaterVariableBits3);

			java.awt.GridBagConstraints constraintsRepeaterVariableBits4 = new java.awt.GridBagConstraints();
			constraintsRepeaterVariableBits4.gridx = 1; constraintsRepeaterVariableBits4.gridy = 4;
			constraintsRepeaterVariableBits4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRepeaterVariableBits4.insets = new java.awt.Insets(5, 15, 5, 0);
			getJPanel1().add(getRepeaterVariableBits4(), constraintsRepeaterVariableBits4);

			java.awt.GridBagConstraints constraintsRepeaterVariableBits5 = new java.awt.GridBagConstraints();
			constraintsRepeaterVariableBits5.gridx = 1; constraintsRepeaterVariableBits5.gridy = 5;
			constraintsRepeaterVariableBits5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRepeaterVariableBits5.insets = new java.awt.Insets(5, 15, 5, 0);
			getJPanel1().add(getRepeaterVariableBits5(), constraintsRepeaterVariableBits5);

			java.awt.GridBagConstraints constraintsRepeaterVariableBits6 = new java.awt.GridBagConstraints();
			constraintsRepeaterVariableBits6.gridx = 1; constraintsRepeaterVariableBits6.gridy = 6;
			constraintsRepeaterVariableBits6.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRepeaterVariableBits6.insets = new java.awt.Insets(5, 15, 5, 0);
			getJPanel1().add(getRepeaterVariableBits6(), constraintsRepeaterVariableBits6);

			java.awt.GridBagConstraints constraintsRepeaterVariableBits7 = new java.awt.GridBagConstraints();
			constraintsRepeaterVariableBits7.gridx = 1; constraintsRepeaterVariableBits7.gridy = 7;
			constraintsRepeaterVariableBits7.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRepeaterVariableBits7.insets = new java.awt.Insets(5, 15, 5, 0);
			getJPanel1().add(getRepeaterVariableBits7(), constraintsRepeaterVariableBits7);

			java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel1 = new java.awt.GridBagConstraints();
			constraintsAdvancedRepeaterLabel1.gridx = 0; constraintsAdvancedRepeaterLabel1.gridy = 1;
			constraintsAdvancedRepeaterLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAdvancedRepeaterLabel1.insets = new java.awt.Insets(5, 0, 5, 0);
			getJPanel1().add(getAdvancedRepeaterLabel1(), constraintsAdvancedRepeaterLabel1);

			java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel2 = new java.awt.GridBagConstraints();
			constraintsAdvancedRepeaterLabel2.gridx = 0; constraintsAdvancedRepeaterLabel2.gridy = 2;
			constraintsAdvancedRepeaterLabel2.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getAdvancedRepeaterLabel2(), constraintsAdvancedRepeaterLabel2);

			java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel3 = new java.awt.GridBagConstraints();
			constraintsAdvancedRepeaterLabel3.gridx = 0; constraintsAdvancedRepeaterLabel3.gridy = 3;
			constraintsAdvancedRepeaterLabel3.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getAdvancedRepeaterLabel3(), constraintsAdvancedRepeaterLabel3);

			java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel4 = new java.awt.GridBagConstraints();
			constraintsAdvancedRepeaterLabel4.gridx = 0; constraintsAdvancedRepeaterLabel4.gridy = 4;
			constraintsAdvancedRepeaterLabel4.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getAdvancedRepeaterLabel4(), constraintsAdvancedRepeaterLabel4);

			java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel5 = new java.awt.GridBagConstraints();
			constraintsAdvancedRepeaterLabel5.gridx = 0; constraintsAdvancedRepeaterLabel5.gridy = 5;
			constraintsAdvancedRepeaterLabel5.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getAdvancedRepeaterLabel5(), constraintsAdvancedRepeaterLabel5);

			java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel6 = new java.awt.GridBagConstraints();
			constraintsAdvancedRepeaterLabel6.gridx = 0; constraintsAdvancedRepeaterLabel6.gridy = 6;
			constraintsAdvancedRepeaterLabel6.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getAdvancedRepeaterLabel6(), constraintsAdvancedRepeaterLabel6);

			java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel7 = new java.awt.GridBagConstraints();
			constraintsAdvancedRepeaterLabel7.gridx = 0; constraintsAdvancedRepeaterLabel7.gridy = 7;
			constraintsAdvancedRepeaterLabel7.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getAdvancedRepeaterLabel7(), constraintsAdvancedRepeaterLabel7);
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
 * Return the JPanelAdvanced property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelAdvanced() {
	if (ivjJPanelAdvanced == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Advanced Repeater Setup");
			ivjJPanelAdvanced = new javax.swing.JPanel();
			ivjJPanelAdvanced.setName("JPanelAdvanced");
			ivjJPanelAdvanced.setBorder(ivjLocalBorder);
			ivjJPanelAdvanced.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxUserLocked = new java.awt.GridBagConstraints();
			constraintsJCheckBoxUserLocked.gridx = 1; constraintsJCheckBoxUserLocked.gridy = 1;
			constraintsJCheckBoxUserLocked.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxUserLocked.ipadx = 11;
			constraintsJCheckBoxUserLocked.ipady = -5;
			constraintsJCheckBoxUserLocked.insets = new java.awt.Insets(4, 10, 5, 104);
			getJPanelAdvanced().add(getJCheckBoxUserLocked(), constraintsJCheckBoxUserLocked);

			java.awt.GridBagConstraints constraintsJCheckBoxResetRptSettings = new java.awt.GridBagConstraints();
			constraintsJCheckBoxResetRptSettings.gridx = 1; constraintsJCheckBoxResetRptSettings.gridy = 2;
			constraintsJCheckBoxResetRptSettings.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxResetRptSettings.ipadx = 42;
			constraintsJCheckBoxResetRptSettings.ipady = -5;
			constraintsJCheckBoxResetRptSettings.insets = new java.awt.Insets(6, 10, 19, 104);
			getJPanelAdvanced().add(getJCheckBoxResetRptSettings(), constraintsJCheckBoxResetRptSettings);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelAdvanced;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 491, 318 );
}
/**
 * Return the RepeaterLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRepeaterLabel() {
	if (ivjRepeaterLabel == null) {
		try {
			ivjRepeaterLabel = new javax.swing.JLabel();
			ivjRepeaterLabel.setName("RepeaterLabel");
			ivjRepeaterLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjRepeaterLabel.setText("Repeater Name");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeaterLabel;
}
/**
 * Return the RepeaterVariableBits1 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getRepeaterVariableBits1() {
	if (ivjRepeaterVariableBits1 == null) {
		try {
			ivjRepeaterVariableBits1 = new com.klg.jclass.field.JCSpinField();
			ivjRepeaterVariableBits1.setName("RepeaterVariableBits1");
			ivjRepeaterVariableBits1.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjRepeaterVariableBits1.setBackground(java.awt.Color.white);
			ivjRepeaterVariableBits1.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjRepeaterVariableBits1.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 4, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			//ivjRepeaterVariableBits1.setEditable(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeaterVariableBits1;
}
/**
 * Return the RepeaterVariableBits2 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getRepeaterVariableBits2() {
	if (ivjRepeaterVariableBits2 == null) {
		try {
			ivjRepeaterVariableBits2 = new com.klg.jclass.field.JCSpinField();
			ivjRepeaterVariableBits2.setName("RepeaterVariableBits2");
			ivjRepeaterVariableBits2.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjRepeaterVariableBits2.setBackground(java.awt.Color.white);
			ivjRepeaterVariableBits2.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjRepeaterVariableBits2.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 4, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeaterVariableBits2;
}
/**
 * Return the RepeaterVariableBits3 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getRepeaterVariableBits3() {
	if (ivjRepeaterVariableBits3 == null) {
		try {
			ivjRepeaterVariableBits3 = new com.klg.jclass.field.JCSpinField();
			ivjRepeaterVariableBits3.setName("RepeaterVariableBits3");
			ivjRepeaterVariableBits3.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjRepeaterVariableBits3.setBackground(java.awt.Color.white);
			ivjRepeaterVariableBits3.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjRepeaterVariableBits3.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 4, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeaterVariableBits3;
}
/**
 * Return the RepeaterVariableBits4 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getRepeaterVariableBits4() {
	if (ivjRepeaterVariableBits4 == null) {
		try {
			ivjRepeaterVariableBits4 = new com.klg.jclass.field.JCSpinField();
			ivjRepeaterVariableBits4.setName("RepeaterVariableBits4");
			ivjRepeaterVariableBits4.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjRepeaterVariableBits4.setBackground(java.awt.Color.white);
			ivjRepeaterVariableBits4.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjRepeaterVariableBits4.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 4, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeaterVariableBits4;
}
/**
 * Return the RepeaterVariableBits5 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getRepeaterVariableBits5() {
	if (ivjRepeaterVariableBits5 == null) {
		try {
			ivjRepeaterVariableBits5 = new com.klg.jclass.field.JCSpinField();
			ivjRepeaterVariableBits5.setName("RepeaterVariableBits5");
			ivjRepeaterVariableBits5.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjRepeaterVariableBits5.setBackground(java.awt.Color.white);
			ivjRepeaterVariableBits5.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjRepeaterVariableBits5.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 4, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeaterVariableBits5;
}
/**
 * Return the RepeaterVariableBits6 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getRepeaterVariableBits6() {
	if (ivjRepeaterVariableBits6 == null) {
		try {
			ivjRepeaterVariableBits6 = new com.klg.jclass.field.JCSpinField();
			ivjRepeaterVariableBits6.setName("RepeaterVariableBits6");
			ivjRepeaterVariableBits6.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjRepeaterVariableBits6.setBackground(java.awt.Color.white);
			ivjRepeaterVariableBits6.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjRepeaterVariableBits6.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 4, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeaterVariableBits6;
}
/**
 * Return the RepeaterVariableBits7 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getRepeaterVariableBits7() {
	if (ivjRepeaterVariableBits7 == null) {
		try {
			ivjRepeaterVariableBits7 = new com.klg.jclass.field.JCSpinField();
			ivjRepeaterVariableBits7.setName("RepeaterVariableBits7");
			ivjRepeaterVariableBits7.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjRepeaterVariableBits7.setBackground(java.awt.Color.white);
			ivjRepeaterVariableBits7.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjRepeaterVariableBits7.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 4, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeaterVariableBits7;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{

	com.cannontech.database.data.route.CCURoute ccuRoute = (com.cannontech.database.data.route.CCURoute) val;
	
	if( getJCheckBoxResetRptSettings().isSelected() )
		ccuRoute.getCarrierRoute().setResetRptSettings( "Y" );
	else
		ccuRoute.getCarrierRoute().setResetRptSettings( "N" );

	if( getJCheckBoxUserLocked().isSelected() )
		ccuRoute.getCarrierRoute().setUserLocked( "Y" );
	else
		ccuRoute.getCarrierRoute().setUserLocked( "N" );
	
	Object ccuFixedSpinVal = getCCUFixedBitsField().getValue();
	if( ccuFixedSpinVal instanceof Long )
		ccuRoute.getCarrierRoute().setCcuFixBits(new Integer( ((Long)ccuFixedSpinVal).byteValue() ));
	else if( ccuFixedSpinVal instanceof Integer )
		ccuRoute.getCarrierRoute().setCcuFixBits(new Integer( ((Integer)ccuFixedSpinVal).byteValue() ));

	Object ccuVariableSpinVal = getCCUVariableBitsField().getValue();
	if( ccuVariableSpinVal instanceof Long )
		ccuRoute.getCarrierRoute().setCcuVariableBits(new Integer( ((Long)ccuVariableSpinVal).byteValue() ));
	else if( ccuVariableSpinVal instanceof Integer )
		ccuRoute.getCarrierRoute().setCcuVariableBits(new Integer( ((Integer)ccuVariableSpinVal).byteValue() ));

	javax.swing.JLabel repeaterLabels[] =
	{
		getAdvancedRepeaterLabel1(),
		getAdvancedRepeaterLabel2(),
		getAdvancedRepeaterLabel3(),
		getAdvancedRepeaterLabel4(),
		getAdvancedRepeaterLabel5(),
		getAdvancedRepeaterLabel6(),
		getAdvancedRepeaterLabel7()
	};

	com.klg.jclass.field.JCSpinField repeaterFields[] =
	{
		getRepeaterVariableBits1(),
		getRepeaterVariableBits2(),
		getRepeaterVariableBits3(),
		getRepeaterVariableBits4(),
		getRepeaterVariableBits5(),
		getRepeaterVariableBits6(),
		getRepeaterVariableBits7()
	};

	//Grab all the repeater route(s) from the route
	java.util.Vector repeaterRoutes = ccuRoute.getRepeaterVector();
	//Get Devices which contain all repeaters
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List devices = cache.getAllDevices();

		//Fill in the labels for every repeater in label
		int repeaterRouteID;
		for( int i = 0; i < ccuRoute.getRepeaterVector().size(); i++ )
		{
			repeaterLabels[i].setVisible(true);
			repeaterRouteID = ((com.cannontech.database.db.route.RepeaterRoute)repeaterRoutes.elementAt(i)).getDeviceID().intValue();
			for (int j=0;j<devices.size();j++)
			{
				if ( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(j)).getYukonID() == repeaterRouteID )
				{
					Object repeaterISpinVal = repeaterFields[i].getValue();
					if(repeaterISpinVal instanceof Long)
						((com.cannontech.database.db.route.RepeaterRoute)ccuRoute.getRepeaterVector().elementAt(i)).setVariableBits(new Integer( ((Long)repeaterISpinVal).intValue() ));
					else if(repeaterISpinVal instanceof Integer)
						((com.cannontech.database.db.route.RepeaterRoute)ccuRoute.getRepeaterVector().elementAt(i)).setVariableBits(new Integer( ((Integer)repeaterISpinVal).intValue() ));
					repeaterLabels[i].setText(((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(j)).getPaoName());
					break;
				}
			}
			repeaterFields[i].setVisible(true);
			repeaterFields[i].setValue( ((com.cannontech.database.db.route.RepeaterRoute)ccuRoute.getRepeaterVector().elementAt(i)).getVariableBits() );
		}
	}
	return val;
}
/**
 * Return the VariableBitsLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getVariableBitsLabel() {
	if (ivjVariableBitsLabel == null) {
		try {
			ivjVariableBitsLabel = new javax.swing.JLabel();
			ivjVariableBitsLabel.setName("VariableBitsLabel");
			ivjVariableBitsLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjVariableBitsLabel.setText("Variable Bits");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVariableBitsLabel;
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AdvancedRepeaterSetupEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(430, 400);

		java.awt.GridBagConstraints constraintsCCUFixedBitsLabel = new java.awt.GridBagConstraints();
		constraintsCCUFixedBitsLabel.gridx = 1; constraintsCCUFixedBitsLabel.gridy = 1;
		constraintsCCUFixedBitsLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCCUFixedBitsLabel.insets = new java.awt.Insets(14, 10, 5, 5);
		add(getCCUFixedBitsLabel(), constraintsCCUFixedBitsLabel);

		java.awt.GridBagConstraints constraintsCCUVariableBitsLabel = new java.awt.GridBagConstraints();
		constraintsCCUVariableBitsLabel.gridx = 3; constraintsCCUVariableBitsLabel.gridy = 1;
		constraintsCCUVariableBitsLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCCUVariableBitsLabel.insets = new java.awt.Insets(14, 20, 5, 14);
		add(getCCUVariableBitsLabel(), constraintsCCUVariableBitsLabel);

		java.awt.GridBagConstraints constraintsCCUFixedBitsField = new java.awt.GridBagConstraints();
		constraintsCCUFixedBitsField.gridx = 2; constraintsCCUFixedBitsField.gridy = 1;
		constraintsCCUFixedBitsField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCCUFixedBitsField.ipadx = 49;
		constraintsCCUFixedBitsField.ipady = 21;
		constraintsCCUFixedBitsField.insets = new java.awt.Insets(11, 5, 2, 19);
		add(getCCUFixedBitsField(), constraintsCCUFixedBitsField);

		java.awt.GridBagConstraints constraintsCCUVariableBitsField = new java.awt.GridBagConstraints();
		constraintsCCUVariableBitsField.gridx = 4; constraintsCCUVariableBitsField.gridy = 1;
		constraintsCCUVariableBitsField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCCUVariableBitsField.ipadx = 49;
		constraintsCCUVariableBitsField.ipady = 21;
		constraintsCCUVariableBitsField.insets = new java.awt.Insets(11, 15, 2, 8);
		add(getCCUVariableBitsField(), constraintsCCUVariableBitsField);

		java.awt.GridBagConstraints constraintsJSeparator1 = new java.awt.GridBagConstraints();
		constraintsJSeparator1.gridx = 1; constraintsJSeparator1.gridy = 2;
		constraintsJSeparator1.gridwidth = 4;
		constraintsJSeparator1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJSeparator1.ipadx = 411;
		constraintsJSeparator1.ipady = 1;
		constraintsJSeparator1.insets = new java.awt.Insets(3, 10, 1, 8);
		add(getJSeparator1(), constraintsJSeparator1);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 3;
		constraintsJPanel1.gridwidth = 3;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.ipadx = 8;
		constraintsJPanel1.insets = new java.awt.Insets(2, 99, 2, 24);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanelAdvanced = new java.awt.GridBagConstraints();
		constraintsJPanelAdvanced.gridx = 1; constraintsJPanelAdvanced.gridy = 4;
		constraintsJPanelAdvanced.gridwidth = 4;
		constraintsJPanelAdvanced.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelAdvanced.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelAdvanced.weightx = 1.0;
		constraintsJPanelAdvanced.weighty = 1.0;
		constraintsJPanelAdvanced.insets = new java.awt.Insets(3, 25, 8, 22);
		add(getJPanelAdvanced(), constraintsJPanelAdvanced);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * isDataComplete method comment.
 */
public boolean isDataCorrect(Object aRoute) 
{
	
	com.cannontech.database.data.route.CCURoute ccuRoute = (com.cannontech.database.data.route.CCURoute) aRoute;
	int finalUsedBitField = ccuRoute.getRepeaterVector().size();
	
	com.klg.jclass.field.JCSpinField bitFields[] =
	{
		getCCUVariableBitsField(),
		getRepeaterVariableBits1(),
		getRepeaterVariableBits2(),
		getRepeaterVariableBits3(),
		getRepeaterVariableBits4(),
		getRepeaterVariableBits5(),
		getRepeaterVariableBits6(),
		getRepeaterVariableBits7()
	};
	
	//verify that the last used bit field has a value of 7
	if(((Number)bitFields[finalUsedBitField].getValue()).intValue() != 7)
	{
		bitFields[finalUsedBitField].setValue(new Integer(7));
		StringBuffer message = new StringBuffer("The last variable bit is required to be a 7. \n" + 
												 "Advanced Setup is changing variable bit " + finalUsedBitField + " to contain a value of 7.");
		javax.swing.JOptionPane.showMessageDialog(this, message, "IMPROPER FINAL BIT VALUE", javax.swing.JOptionPane.ERROR_MESSAGE); 
	}
	
	//Verify there are no duplicate bit field values
	//behold the crapsort algorithm, tremble in its presence
	for(int j = 0; j <= finalUsedBitField; j++)
	{
		for(int x = finalUsedBitField; x > j; x-- )
		{
			if(((Number)bitFields[j].getValue()).intValue() == (((Number)bitFields[x].getValue()).intValue())) 
			{
				return false;
			}
		}
	}
	return true;
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
		AdvancedRepeaterSetupEditorPanel aAdvancedRepeaterSetupEditorPanel;
		aAdvancedRepeaterSetupEditorPanel = new AdvancedRepeaterSetupEditorPanel();
		frame.add("Center", aAdvancedRepeaterSetupEditorPanel);
		frame.setSize(aAdvancedRepeaterSetupEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
	
	//Must be some flavor of CCU Route to have repeaters
	com.cannontech.database.data.route.CCURoute ccuRoute = (com.cannontech.database.data.route.CCURoute) val;


	if( ccuRoute.getCarrierRoute().getUserLocked() != null )
		getJCheckBoxUserLocked().setSelected(
				ccuRoute.getCarrierRoute().getUserLocked().equalsIgnoreCase("Y")
				|| ccuRoute.getCarrierRoute().getUserLocked().equalsIgnoreCase("Y") );

	if( ccuRoute.getCarrierRoute().getResetRptSettings() != null )
		getJCheckBoxResetRptSettings().setSelected(
				ccuRoute.getCarrierRoute().getResetRptSettings().equalsIgnoreCase("Y")
				|| ccuRoute.getCarrierRoute().getResetRptSettings().equalsIgnoreCase("Y") );
	

	Integer ccuFixBits = ccuRoute.getCarrierRoute().getCcuFixBits();
	Integer ccuVarBits = ccuRoute.getCarrierRoute().getCcuVariableBits();

	if( ccuFixBits != null )
		getCCUFixedBitsField().setValue( ccuFixBits );

	if( ccuVarBits != null )
		getCCUVariableBitsField().setValue( ccuVarBits );
		
	//Grab all the repeater route(s) from the route
	java.util.Vector repeaterRoutes = ccuRoute.getRepeaterVector();

	//Use this array to loop though the combo boxes in order
	javax.swing.JLabel repeaterLabels[] =
	{ 	
		getAdvancedRepeaterLabel1(),
		getAdvancedRepeaterLabel2(),
		getAdvancedRepeaterLabel3(),
		getAdvancedRepeaterLabel4(),
		getAdvancedRepeaterLabel5(),
		getAdvancedRepeaterLabel6(),
		getAdvancedRepeaterLabel7()
	};

	com.klg.jclass.field.JCSpinField repeaterFields[] =
	{
		getRepeaterVariableBits1(),
		getRepeaterVariableBits2(),
		getRepeaterVariableBits3(),
		getRepeaterVariableBits4(),
		getRepeaterVariableBits5(),
		getRepeaterVariableBits6(),
		getRepeaterVariableBits7()
	};

	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{	
		java.util.List devices = cache.getAllDevices();
		//Fill in the labels for every repeater in label
		int repeaterRouteDeviceID;
		for( int i = 0; i < ccuRoute.getRepeaterVector().size(); i++ )
		{
			repeaterLabels[i].setVisible(true);
			repeaterRouteDeviceID = ((com.cannontech.database.db.route.RepeaterRoute)repeaterRoutes.elementAt(i)).getDeviceID().intValue();
			for (int j=0;j<devices.size();j++)
			{
				if ( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(j)).getYukonID() == repeaterRouteDeviceID )
				{
					repeaterLabels[i].setText(((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(j)).getPaoName());
					break;
				}
			}
			repeaterFields[i].setVisible(true);
			repeaterFields[i].setValue( ((com.cannontech.database.db.route.RepeaterRoute)ccuRoute.getRepeaterVector().elementAt(i)).getVariableBits() );
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
	D0CB838494G88G88G6AF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DDC8DD8D4D55E27A2174A8AD75AD833F2B7EA594DDAABAAB2EB09FC5F254DDA5BECF377F55FE537AF3625220D273516FD3BFC63870ACA0989B8AA9AA2A1888A912EFC05E8A824E86C669683F3E1C60719F1668E8C08767E4FB977FF6E1D19BB4360733ACF7BBCAF0FFF6F1C5F79FF7C4E39676B1EF3E7047CCD33B2D3537285A1B5D3107FEAD385A1E9C8902A6E3DE59831A0B1B6CBD07DED86D9A354F7
	E540B5947242FF911B15A724D464C0BA8252090F44E62D074F6B0454DD8DA761030FB8791077DA475A927FF1E6EC44B859B47DC047CA382E8209840CDC13C5927F290E8299DFA063A829E902707CG51C96CA81431BAC8F7014C82099AE07A8B603A0CC63E3C3ECE56BBB9ED16107C338F52F2099F15CF1472C1E65BA5DFF5420FD3178E12D86F9EDEA95B04C0BA860494BEDD487F22992E8DDD27877F56EF9D2A6857DBACD60BA4769BABC403C9325AAB6CD627A45657B799067516FE51F0D43409FAC9349F93A5
	276D932A52253708E6C1100CA44EBF8F67D03F6093520209E07587C86CE8C8AF84C960F8EABB41DB6173C9904904655D77C103DC1E1E33BF96D21E35242F76D15E29A6D64EA813D25EB0675B599B683D79F5A13987C91017FFAAB62B942483448CB295E4A3ED933F5BD8865726BE2945BAB4E435B459EC4DE6037D3055A01A61B3143BCF123123E23F55EE9084F65FB79EAE576063CF185D0EC95B31BA092C46B6B07B68ED4212FBBECD4B562813A51115E99127D8DD4CBB2554890B5D67B37612D36EED54BD7667
	02BAF66294EBFA26C66C28E7F624A549FEB2CEAB31EBCE13FBFDA7ED83A570B9D5EE9F8C0F2638CB0533765AC8F1128E013C445358B6FAD6A1170E0C99C2DED507E6BD0447B625F51999872359130B13363BB0B30B194A391C2778AC954E38E437E35F198DF9F9C331D92CFDBDD7CC7A2F8469D010C8109810F8109410BC8B36313242DD813631AE3DDDEAF61A4C8651AEB05B678A5F032B68126C7A039607C47A60893D5D246FB30B8EC156C9ACA4F55067E677BAED7B7D906300E8916DFA49E43500F77D10881D
	5741F45EAEDCC3F46CA24D6DB68D0990E048A6205F3422F5F0B56B9D52F11B81BA3D224172179711FBB22CBF819E4181FCE6751227A375D58965EF87696175F0B9757729E80771223E3E43EABD6D34F51312881196D24F23F46CB04367E08B56714A4D84CB04F406957D4CABDA2B7253EA6AA7747576D16CE3D1874898DDE045B673EA8F3619565CDB0572E78EE936991AEFE5ED652455336FBCCCFD92DEAC5DF620E7127DF8154CA97031622B8B0A119E209E2B5F31AD64DE85832FD9A0B13C0E7FDC24E3ED36D9
	6F903115F65A210109862546994F336D82679C2C7A1C5BFEA17CA7B71EEE3F28B1D8DD3CD2CC669FF1D834C82DB07DD98C7041A167A59713B6A6774F81AB37F91B62B6684FAD50019C9C97B611BE2717A43B298FA6D618C6A513E5D0494F59C4E2B91CFDB6E5A840D82BB711F157BA827D196B67EFA2FD215FEA31087426F093B97FBD5A97C533B88424DB2CCE0B4470C29DE94F2745D1F9AC506948E2E7D8EFF612F1052D8D6668D710B258CDC3F02B4950A4B458C8BF8DBDDC202A234550E6489AA78F72CAEC58
	67C636E21FAB997B1E90735536B42DB9A37C565634E09B6B9361B6254FB1CEEBB6EBF3DFCF478499F7B02E73BF246DF502D58AEB7377EC256B910837G64F99B3679691BC9BE0EABF2E3EF311A4D2CEAF91D87ED49A377C8BC6394A1989940BE6575782DADEBBD72CE48F54A722F5DC26A542C9A7B987F6C2F485C2C358E9BF41AAA1C12090CF4FA899A194DA951323075565BF44CA4BED2EC382E4A7F0C142F668C2CF7C08CE7701EBC150B6B2A3053330478DB5E77390E9CBF436E452CB34ABD51DD066B2A3F16
	1018711017F4866BEE79D675585E64F0D87BCDF4DA114B7D40D675BDE35843DB1743F540E4F7C88721D339300E66EFA5FC493CA343F2DFFF47EB7D12E1D74653823B77584A6675F0DA9F65F6B62F2F66F8A4453BD5B81B6F5F27B8C90FC35ED2BB4EC5755BC93B179CB05F03C40244004403889256C3C7C935B041DCFF906E2B5D22B79FF36B64B867EECBE77DD2CEE7ED4B44FE89D7B6CF3EF70454E5A1442C84E1586FBA89568DE93304F354CD1F1175C9A8246700AC84C98249F342DA98244109FAF7D23D9EC8
	3BC042C122C19603AC85A99CC6BD5DE7B6599F7161D9578D9E98463B10F11770AD157D31587BE9ECD2B7ABB474D7FB24094D328B083BCA6ECB3902FA7CEF596EFB7CBF34DDFB7C6F5A2EBD7E1F583E066160C33D5A437CAF377B1F9F8636EB4D8F69B46F43A3D9345F0C42A39B2CD507C93BE8BD02E3F5A324FBC002C04206D95D3231672E91D2278330B804D047ECE23F90B2C2EC17F622EDB26867GD68A528EE284E1B15FFD0F70EDE9B95EE6F20906E613646850771147C119D36B2779AC9FD72DDC05658F16
	F95A3719C433015BDF35DE431E2A307CD78F906E070E09D09FFA98662BE43B577711B6F0080E1408FDFEE49547F02690049C97A96B8B1F652B5B6E88DB03A64315656FAD564A6FF2E1FE74F2F53E41457243F97EFD55DA7933F97EDB12BABFC64ECF00AB1BF327D52B675C94B9DFB79A1BD5B30ABA85ADDABA9D105F8B4246B67391FC16F181B6E79477812AFA4998C0671FD37AE1FD05FEC42A2FBFE0B7991A75039D34C53179A60A0EBD44BEE194671B5FD31F3A31582CBA102EB11CEFDC5BF13E19FD5B8C21A3
	68D34D79A63EFBF71AF10C354729E34AFC93894F71ACE6F197460C03FC61AC41BA3A5475C56E6FE6782F02042579675D72E91F925479F35C72895FFF3A597F5EAD1F54513532BDAB5B635B30ECA90FC78A66FBBF72B917361FE5E5B24B572E93DB0512AB7F124D6A7D0D0182F53D3BAA9CA350896B6B8F51BE424ADFF814141F34670833D837675A54F3793C553B53D678083FC40E1BF2D6295BF2DE37C345683BE19C5B42793D5B8555DA6434F93062D4BF9945AA86C84783F2AD47ECA60B5CD1191F5716239F89
	7CB06C15EA44E6F35F5324C26D72729E4CECAB1D380F74BC37FDE3070F3AD22D079E2B4675D061B9EC1F3FF152F99E52E61060F3482F2B85794D643AA1EDC4F7912493C1B24E635CE7BFF2E17BBDC197E87740246D581AA62A1370AFBC6F3937719045CB4FFB6EEDA47071208672424F63DAC7AAA5F51D8269022FE11D81528EE284897E9A57BAFF59DEA9CC305669365ABAE062B42B26BAD63E2DDD97368F76C8B6367B28395F9722273C156A2B5D07FF4D5AFB34FCCD685ECAF1D63E6CB2D23E84484B8802F98D
	2491249B449C0465EB79201C150FBCC1B4C9123E5FC8D72FD83893A6F1C4014944494A3679283AECC1C1ACFE18FC0D8C72DE93B1CEE7F612B1E79E64AF86C98249398438DD02FC2E7EE0423565A7AE4D35255C060B553C5AAF1177BA642B6D92EFDE4CAEF9B95A85FB6804C3FA36CF3BBFD523DD0C074EA2C8A778345B637457B6D2FC5ABDD96DE917F72922DBF2292F6703A706C99F283B14750D4D9CFF116241AA1C75196768F8C2528BA1EF49E5B8C69CEFD147CF880E4DCA89D6E266DD6EABFE8A9DF3CAAEE7
	F1B2B91ECD77C06BD4B80BFF8D55A7698E481BF3B9461F5BA9703AF8D4757955A397FF9FA63B73626F431CAB753D8E8FAE55DE07DFD92A3D8E8FAD753F4E8EAF753D8FF3F329FA9FC65ED3A8A7FDB7FF8A0C15A05DA0C61071A958F7BF5C31C520FDD7E39C7944B0A836D8696E2E0471734AFC17F5FD5944FB423AB272EC5DE715A46B90710F7876B2BAC7DBED94E4584EB2D2C7F65320D1E22AB2DED5C677E345818A33674CBA3A968907F2C5CE41E74FACBD4166C1FA8147CE6B71F9F4B12F03511D3E4B9434D3
	7B7ECD5929FD7F266E747F9C953653577DE57C2EA846F214FE8BB9CFA943F2B4F06C25B2ACC7BB47D62C2036100E6258AF6A89968769F80EFD65C25BE40EAD2BC15B56900EB9513607E366DA7EBC4F318EA7E273B87682DD6F2C3C821E852EC04C308775DA2F4031616961C63806C1FA8E48C210E41002AB212CA03DA041A011A08BC012C0722F0275BC48C910A010082B50D7A27595876984101C29D007A06DA0C6109010C810B81084101C2BA19F249D4488928292F9B57A7A0313CC5E6A3ECE78285385D77A5E
	C308D46511322973C8B9FB7D589271D7D654D928721BFC05EFFF4193441AF88CAD9F762C0BDC8FBD2D3D8F14F52A4F1EE7GE43C725CA37114F7C148B97986398F9501EC9258BC2766091F7D6D2F2C3940FA20771435FF3AB60F03F63264EAEC57B752BEABDC83776B9A448AF558566B2E41B615F600E0A1C407E33994DB8869A40E0535CDB4E7917BC0B807E8683198F7CC98A3B340FA51BA57E1E35D03653E4755587209670A474B695EAC0C715D23B611E18B4B49DE12F5E040A172B9691972BC0E9DF4F4D99D
	A632414B76D6BA0F127304812BC56A38165C97114E93F9703970DA5CABAA52939D894804DECB5E2D381DACE705E42AFF293EF502322BE591589059AA48FB9157AA4FB5AC7E5D342DB4044238834262BDD723447B1FB2FF7132A708678AD5623132EDD9C17A2A4DAE8E08F63BE8B8E69A93B9170F69FC91CCF804E2DD7C211E4E4F7A7E5303F62B53E2C05D540F699C84FAF1A00C6B304B9F574C096A867C7443F5A767331B4EC149008B53E233E40C4EAD65102E1B06F1AF2B7597F75984F1FB26A9F1193FFE67C4
	77B8D36557531F817C5526AB715689A9555E7B1D6BEAF08F4CB60D6DF70640F566B436174DF81C5DB3910FECD59B08825B38E90C87C9AF014FEB5C6E7D5DDD645E6319137B7EE79C5D4F755A7C6479AB075ED5592A752A384ECF777B52594DF57E247725D34DF56E5865CB27066BAC6C772533076B34ED762533176BBCB360CB27166BE48D123149734C0E56CF956AFCFA44274EEE54A96F7429D30DBA7F697029D303BA0747FD6A6CC11D6AE3BEF576224E1FC51FBA35385FDCBE62391FCC5AE742B45C8F1ED620
	151F4273CDA31E7B49A4BF6F5B183FEAD4AB3F0467D7E85A5771FC1DC3AB3F03673F2F195F4B732BB573A51EFF3DCBABBF04672F2B55AA7FCC1EDF7511D67E9C1EBF35C7ABFF3EE23FCFABFFB14F6FEF564ACF64795BDA34720569183FC7B3BFFFBA0E7F173510312B4FEA278FADEC6C1A41776FCBC12F914485B2F5BABB03E3F6D1455EE35E6345E897B11D0DF973603AD036E3E3DE35EE22B1EF19200C3D89E01BB2DD99737242BC673B63A749FA73D00BD16CBF5DECF59DF7C08337761F968DF27E9C4386F57E
	D191568CC7ED126AB90C55537E836AFAAA8CE31CDB43309E751A79BDE1B827BF761140B93D5DCB1E31AD62088E0FB0AA5AE0C1D23A45E0521B2D03DB4AF5D5702FE6876EA54E9AD13FD7D9373CD6739D85A266B0363E8B122F733E35D5767173AF54BE72E6ECCECB7561A3DA361D9F266C43EF92583A1D2EEB65BFD62E0D7B8481F97EEEA56EF9965EFE03507A6CDB29DBFD44D8AA7BEE3FCE0911F0AAA51B79DC55285455D8D920F545EC2B1A94EEE14D0A1F1F5697FB7AE9B58D099672C2D8691E4AF25D6E5D5C
	475BAD0A5D67347FF31B962B59EA2F98B11AA4116996FD2244EBECD36C7EC78ABC5EED8794BBC32D7FF80F35AB71928F124FEC2F6D639A5C9F8F02BA0D3E8E4F8EAE2B41B329246B706C60AE2ADB7A9D683BA0BDA05E77B7E93F9646623E7FA9C9B17F7DA3F8B6E17B8E460CABC0AC12E333289EE9A7719C7BE49431026B919339DE9747BEF7A09642314FB8964331D39CDB4A31A7DD08D506A3F62896B1A3470C9FA1B60BE30F76A036D8515B07D85EF7911B510CD8BB47CE36A09644B1B347223F0B671C31AD78
	7EE892E025B3708C6925A2E89E933CE753ED12E029A28F4D727DD85E0A67CA3DB370FE3FDCC4E204C3FA8EC85C8C0C7DC6B13619F8408AEEC01E9FEF42B653F5837AB0EDA23AA1101E8592FD43E45B4C3E8EC140E7003F1D58A8E81E0F61A8AC17E578G5657C24EAD1CE3B9B3718C68658E02B5C27A64CC5C43DBD5CF5673A7742764538776F2955D4984304BEE3509F6E91421744DA7F9FF74054304A77351DD3F4D170F26BE6990D06F6CBBA576CB5D7AC1013FD35AC9662FF160918EB28FE491C88208F0A34C
	2BA025A08DA05DA0E610A010B010C810E81085A071B7E2B9FA776D70D38E79601BEE4A5A75A632ADABBF4FB755DC58F9DE67855A05CC5A0E1D9DEC5BC35AD5720D1E6700959497EE72BC873C1F0FF3ABA14FF6936E1198C75471975DA41F254957141BFC6DFBBCD120364B3B19691548573A1BFD595D4A63F5F86874C2DA82897140195D21D1341B6911BF875273C196EB5A1D627192BD7205D9502EC0CAE7E9591DF420DD03C7FE97248DA0AECD3B935CEE2AC77EACC8C70144E95AD9395D920F7CA4C8E7FC8F46
	196FE959BD6DC23BF20F7CC6C8F70398B56DBAEB5577EFDC5689152F91F00D56349B7AC8ED37C05609172F49F04D793E16DDDC0F5A2ED05629142F2DF06D51349B5A2736B34BBAC172B58C2E111AF6B7B72B6D6649BA8B656B73F0DD2AE927F7BB63DBF90B3C4E122FB5F0ED3FC54B6E0C1B5DC9D947A6DF03619A2EE99757B2D17F1F2DE9774B896D0ABD524CEEF5AB310B737069CB16009E9BD73E24F3DE52AD1E634DA045D3EE719CEF120AF03C49090071BA82471B370AAFEC1C4C5EF4E1F60D1336E3657A8D
	1D3797C6F81677C50AAF0970AC2FF59316B78172BAEE45721EBD200EBF67D67999C13EAE162F0937AA7703595D68E6A75C46727365EB29FCED38CD31E3735EEB9D64B3BF97E3D3341C77E687D9A31BACA6891E81CCE374755DE320E899E479A51D3E7719F7F6CE3C4FDC5329FDA6D95F69FDA6393FD37BCC7213CE7519247C8EE6A7D97F8C421C5D4375BAB976F70EF5D31F847B5CE4100C9C7F8765C570F61184C43C17729A21D80F0C09AA6C6FB2F62A13BC13C3110DD6BB5ADABB69FE3849ECE6E98755B1D960
	B11857070BF2B45974AA5E67B876F7FE2FF7122DB779F30D6A73C3B5977FDC7F1797B586BB4379FA844FE23BEE43F31DB90DF896EB60581AA6BC6B31F16C7982340D61D89137DD4431730DE89B4F31F9B4EE658F20BF7EG3160A3F8965B4B31C74651D66258CF68334EFCC8AF6118FE947DA5F1ECDAAD7A4B08C4ECAD77D740311C3D684F4831CBB83FF10E4557203FD00E350E213F880E7D3E8A7DA5F34C61C0FFB9BFC42C338A7D95F26C29E174D74931A86EAF08E3C35CDF98473E4B7DC5EA781B2761EF650F
	7879BB77276358E16E2F06E3F3393FF60E7D0C7B8B67584BAE74B71BE3F1FB51DF8C4736703376C59C4B9DC5FFA537A3B6C0756AA05D4E31A27A1E43C9C81BB9F6A9FF77414531DF56513810DE4231E07AEC1C84E9E1B6E25F264FF87910AE61581F85FEBFB83637877D85F16C37948B03F48447EECFA3D8B42463B816174E4B4B31058D682F648E4E0FBE1357C13A1DE3AF2C2165053419E307567272F2AC37997D45F16CBE0AAD01F4A2479EFD0FBE535E891F6FC42CF4BD7AAB64584F39BFB347DC7439BF8852
	219CFB025AC6C0BA0AE33B8B50DF8C47FE4B7D657D9831906A2F8452B59C7BB575578E699E0E6D677E8C9C0B657E22GA3E76769C24C3ADCBA661BCD963105FD93130FF95B69FEC29C6846F39F76A2FAAE87691CB938BEB094AB637D106A731BC597FF6CDFDDFC71E3BC37C9A9D3226A33F353450FFD4DC50DA177E73A2F128F7733E48E5E678D74BE57C13A1DE3670B30CDF5F36C25B65A16A19D4331A86AEF812417F06C49C2BA0EC0DA388B31EA6EEFA547E2283F1310F65D05673E11F40E8906F468DD38271E
	58C56E83BBB7A8ED42F303183B3C1F8954E782744C96F408107DE9320EC9384B73DCBEF783ED63008BF7E33C55C715F857B5E23C4A3B3D63257B09C770563BBD4FA91E9E71FDAE1FC84B5E85B6A76F467A38034E7FA1108E3B9B4FCE960FA327A89F1C527DF00A7362744BDA729E4F90AC34071CC314910B7DAA1DDF9601FEBC67F3A51D07D65E83EB05FB901BCB75DAA15DF58FF25C41B95A6E5166186907E370BD1E77290A4E1321004F64F1FFC46BAB8E520BB8360C3EEBA6C4C1BB0BC2AEDF0CA11772A8EDAE
	59FE38B4C6F9F2D9311B4468G3CA78A634E24EB05E0C807F24CC557G73A09D4739A48CA317849FDC16796112624565B7B4C64E3D31D97977E25C6FD0AE0DA44D31D82A6702F4703D5EDCA26E556632428F1768FB35394C87FCA10F7BE34AA58552B977A1B6CC75EAA05DF89FF2F90AF3693DCF1BCB2E9FAE52FD1EDC5E20B102G1F4A632E23EDB79A5273B9767ADEFC4FA409F379083FE712F73FB61755FE38145C6F49E5AFDB07835EF0BF1E1B1E2BF5FFBFE9BA6DC3C6481738CE0D477BC93B0F11264DB8FE
	173F1F942141D1BDA69109869DA248F1417D1EE340ABF5248F8C1B9CA6729DE046A9032E87C97EE24E69060F5CF95FC5F592A0BF196B7CD0F067FD3CDB613DAA95F917C4EB734E74433BAE5A33EE3F6AC10E2D51987F4D9EF70E2F516F7AF6C3FEAF5709CDF367F87DF10563876948B1548747ECBF9CA33CB83E55009C677078B76C7328C73A360D017C85DCA7E50DBB4739A7940E7A3548B1458747E5FEB866BD6055C61B11E36183987F09E6F70E4FD11DF2482F63BA3FFE4F1D6347BD8A477D6B11237181ED0E
	AB7CF09C77623814F38C61712FEDF167F8ABDDB307C3FE8457F9FE3DBB4728FE056341826438488747DCBF9C933CB86634A047E49E7FE68F0E77D21D0C3970F9AE6A3C6E4171D9034271846758B8D71B63EABF9C3B67FAF27CA32F47DE9EFFDFABE9AF92B9E9A4676EDC77B8FDAE30015EB8573D22A893F585BC235EEA92849CCBFEF8403D3CBF21EB4459E09F4DFDD4963917376D14D25E6B0AA6736EE58278A4426748396C77357E7C4EBB6F48E774ED649E6A690FA6102FAA69496B0DB2EF53E90577639E3CFF
	C7F967BC88F36B03487B05C2F75EEB06945E07A6641D6907F7AB44B86E467BD9BF3CEBAC827FFC03CDA9C3C4E3206FD130F3563FB761B9F25703DAE73D986361B34A67ED1292EF4DA463CDED41F8A18F62FA7B18D371BDBC2C789EBD92783B95CBC6943B8746FD3DDB41B8ACBB079CE2B807F7DC8A07ADE30A2F57DD01F378642CE2B725B6F03B5B47953B6593F07F47B8F2DF4A39BFF9CE617E7AF9553D99891C434D02E277CCCD60F6F9E90ADD4318FF6ED34710FB65BC64DE1D2EF0BF112178CAF7844EE1B4D3
	31EB2D8A5C6E65AC456E31E17F5CB70CA0F7A3671E1B23F02FDC26787A2BBDF08EDFAED76C2E19847779ABAE047BAC4E7D25158A77652B94DFABA6D16F9F2ED66C1AA741FDFA1EE2F777845C53B877451C7BFDEB946E3FDA2B78CA1EC43DE767ABF68F6D8D440E693E6F733DAD795D24DA641B779072ADFBD761FB78BDA56E1B8145E5F60B8BD47DDABF5118DB69B6667ECC0F1C5A9F72B7467FE50392E3A3DD67CBF6277CBE5A0CED2A31CD18DC7C7D2998BF085749C35B95FF43BD6E31B66CD072EECE1BDC2C1E
	F40C954DE39DA8D315EB1FFB2CC7EB143CDF2D19DC2CEF2F45D8C9BC560BFB94FF071B5DE39D2ED3726EFFEFF231EC6BB1D66943984B26FA57F4F50BFB2CE7BFD6729ED9BF39D8E385982B1747FA3BC1713749A356097DCA5E13130C75AD9EAB1C479AEDD47C25F9440AD73D6BB9D034D9BBD6139D0229169EF27BEE78C4314DAD8A14276F7EF22ACD797CD43B62BB36B0D05F4CB6193E5F4976003FECD21F23AF04BAF97EE17F6FBEB03BE9EE6FD7AC851B15BF8944EE55B9351D8EECEA82327BEA5C6D3B7BE0F3
	B2A03BE9E3EABB9B588487E4D76C765ED7B8584C3EG3B9830D99410DD361BDD82582484E4E7F5FBCFA9AFA6B62BA4A6903B1F6B55F6F5E0539110DDEB2A5A2E97EC24006C7E192E368B811B198159DD3F566D7D9030199F101DF33D5AEEB158A486E477355BFBFB42A370AC76C8A0F6D35D6CCA4126E182BBF6A661244FC3DD20BB4231B10A99815B4C314896543B19E3F734F86BEDBB07FA299CABBB07FA8AB6BC6E2DF7F65CDBAFF24CDB6F0EB1EF3DBDA35EFA75A3972ED72C2137D5C3EFCA2D375ED5355EFA
	AF6A3D757E2877567BA255DB2FB755DBCFCA77569BCD77563BF32D375EBDEB3D75262E77561B365EDB6FBB855EFAB794F86B5D2221976926477686745B4942DCFE67093E964470C2D72E0263EBC1827B9D71278E5D84AB6A592F8819922C83DA0E6B68EF176948175B6AA5E1ADC1705DA786BA6CC22E5B54749F5F5B7362027FBE61C897328E8A9B488E48B151CEDF3A928D64D79E65DF54ED9316592CA648847C2010612017CC07B4EA96BB047C012364D7A69D12E8E84257399C1270EE8709340B3C493E2BDD6F
	B0CA0446479DC206057C762F3016E51B751641DD6C5D2E0FDDE4BBA335291774F75AB3D3016FBA4A17F82960DE56BD38FA5B66C34FC7278BE9ED42F233D5EFE85377CBD67BC1E119E4340B8E2355ECF05976B7D2C7B934311227268CF03711971F943622DBF4C91C74E9570DD3F393F25B604956E597F6D1CD3261D341B58CADC63D9DC209760312C8FFA42A0467DDD2ACEDFF62407A13E9C22ADC48B342F9A143E513042C817D1049BC8A9F58C240C1F2F2A049DE5F82DA2BE9B0D29995EDD09E69572FBC767D7F
	EAC8CA84E7EDC2A67D55C5201427E89DF18E750976A9332FABDA9196FF95E1ECA345C54F4BA43B5E6298305A0740394E234C184529D7CCFF2C723A9F3E7D8FCADDB11504CD6CA601692EC151BA342BE9E040E491398338E3603E083BA7BA959EBAEBDD535EBD744CBBFD5042DA0574214ACA7AFF957DFFB7793F0AA2D594295A8D37ADC9707B67196F42B253E6C9932BC99BF211CFABDD3CF289410374353778616A7A7467EEC9CF93B28E8AD96CB5C0E85564DDBEE85574D53F03C23AE4355186C93A61F2793A0A
	3ACF73EA26844D72EEEF84DE4E6FBAC965127F56107F56D223FC7A7F7AC91719D8ED70A82B0E55C4214C15FFDD8DEAE02D77F3E653B53FFAF676D72D79E9C29AD402D1E4254E9CA1A38FADB7F9DE5D8D362B5C1FF9D3EFDDFFD95E35554EB4A1270DB4EC334D28BFA8B7518EE1C59F2DBE518E3A121027B49FEA8B3DBF2D9F06060D8A5ECFF016FB4C266FA78344B2035EFE1AFAA045A97DC2D8B6E09F6C2381A4A16D0B36AFA0ACCD116C328E28E1A379559A52D3A5E13D623A0BFCDFC24EE0E30B708E7C59689F
	2F2AB4393A48CAFF85C5377142135487FE6C48B6C88E37A3E82A5CD868BE3B1BAEB9FAA00F72E99CA5BA9B64D86912100B5F4F559D2183E42E5B508DDD9A732BD41FF72BBED72BBE5728BE6FD1FD5E2B7ADC8B35055F8D56716074170AA5E1EB2BD5E731CABA3D59EC9D51691D12D557E712F47DC698FCC4DDA9790A658EC9D827713DCBC9GE77DFAF33F538C8EF583A6572EE13D1DD8BBF425746B17BE8C3F49FD2BD569DB78C777403E491CBFF5673C9A15A75148B3D5F6199AF67F6F65DF67A5908B4F16A878
	E809695EAD717F3E7D8D394FC77817794DE3AAF977EE01F37AC6B1BD7BEF437452FF9B26D77C5BB08D7D37E1FA7DE418CEB8DF367ECB664B1BDC97D60F2D97319EA35C47D03234B4121A7966B13DDDE13ACE45B48BFFBBE247576A5F27D8FAF9014F5F38D8728B36DF17748B6537A358F72F2BBE236FA1BD9D1B557EB4FEFF7A122E8F855F5F61BEB6848BD8C9EFBE5DE4B1106FF3B70FCA22725B0DFBBFA37E9D28A3E7323CFD1F915E27D4B67F8BD0CB87880EC296B90DA0GG98EEGGD0CB818294G94G88
	G88G6AF854AC0EC296B90DA0GG98EEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG47A0GGGG
**end of data**/
}
}
