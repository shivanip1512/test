package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.database.data.point.PointTypes;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.CtiUtilities;

public class PointLimitEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjDataIntegrityPanel = null;
	private javax.swing.JTextField ivjLimit1HighTextField = null;
	private javax.swing.JTextField ivjLimit1LowTextField = null;
	private javax.swing.JTextField ivjLimit2HighTextField = null;
	private javax.swing.JTextField ivjLimit2LowTextField = null;
	private javax.swing.JCheckBox ivjLimit1CheckBox = null;
	private javax.swing.JCheckBox ivjLimit2CheckBox = null;
	private javax.swing.JLabel ivjJLabelAlarmDuration = null;
	private javax.swing.JLabel ivjJLabelSeconds = null;
	private com.klg.jclass.field.JCSpinField ivjSpinField = null;
	private javax.swing.JLabel ivjJLabelHigh = null;
	private javax.swing.JLabel ivjJLabelLow = null;
	private javax.swing.JLabel ivjJLabelAlarmDuration2 = null;
	private javax.swing.JLabel ivjJLabelSeconds2 = null;
	private com.klg.jclass.field.JCSpinField ivjSpinField2 = null;
	private javax.swing.JPanel ivjJPanelReasonability = null;
	private javax.swing.JTextField ivjJTextFieldHighReasonLimit = null;
	private javax.swing.JTextField ivjJTextFieldLowReasonLimit = null;
	private javax.swing.JCheckBox ivjJCheckBoxReasonHigh = null;
	private javax.swing.JCheckBox ivjJCheckBoxReasonLow = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointLimitEditorPanel() {
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
	if (e.getSource() == getJCheckBoxReasonHigh()) 
		connEtoC5(e);
	if (e.getSource() == getJCheckBoxReasonLow()) 
		connEtoC8(e);
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
	if (e.getSource() == getLimit1HighTextField()) 
		connEtoC3(e);
	if (e.getSource() == getLimit1LowTextField()) 
		connEtoC4(e);
	if (e.getSource() == getLimit2LowTextField()) 
		connEtoC6(e);
	if (e.getSource() == getLimit2HighTextField()) 
		connEtoC7(e);
	if (e.getSource() == getJTextFieldHighReasonLimit()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldLowReasonLimit()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldHighReasonLimit.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointStatesEditorPanel.fireInputUpdate()V)
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
 * connEtoC12:  (Limit1CheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointStatesEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		boolean val = getLimit1CheckBox().isSelected();

		getLimit1HighTextField().setEnabled(val);
		getLimit1LowTextField().setEnabled(val);
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (Limit2CheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointStatesEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		boolean val = getLimit2CheckBox().isSelected();

		getLimit2HighTextField().setEnabled(val);
		getLimit2LowTextField().setEnabled(val);
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JTextFieldLowReasonLimit.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointStatesEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (Limit1HighTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointStatesEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (Limit1LowTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointStatesEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC5:  (JCheckBoxReasonLimit.action.actionPerformed(java.awt.event.ActionEvent) --> PointLimitEditorPanel.jCheckBoxReasonLimit_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxReasonHigh_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (Limit2HighTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointStatesEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC7:  (Limit2LowTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointStatesEditorPanel.fireInputUpdate()V)
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
 * connEtoC8:  (JCheckBoxReasonLow.action.actionPerformed(java.awt.event.ActionEvent) --> PointLimitEditorPanel.jCheckBoxReasonLow_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxReasonLow_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDataIntegrityPanel() {
	if (ivjDataIntegrityPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Point Limits");
			ivjDataIntegrityPanel = new javax.swing.JPanel();
			ivjDataIntegrityPanel.setName("DataIntegrityPanel");
			ivjDataIntegrityPanel.setBorder(ivjLocalBorder);
			ivjDataIntegrityPanel.setLayout(new java.awt.GridBagLayout());
			ivjDataIntegrityPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjDataIntegrityPanel.setPreferredSize(new java.awt.Dimension(360, 126));
			ivjDataIntegrityPanel.setMinimumSize(new java.awt.Dimension(360, 126));

			java.awt.GridBagConstraints constraintsJLabelHigh = new java.awt.GridBagConstraints();
			constraintsJLabelHigh.gridx = 0; constraintsJLabelHigh.gridy = 1;
			constraintsJLabelHigh.ipady = 4;
			constraintsJLabelHigh.insets = new java.awt.Insets(3, 11, 2, 17);
			getDataIntegrityPanel().add(getJLabelHigh(), constraintsJLabelHigh);

			java.awt.GridBagConstraints constraintsJLabelLow = new java.awt.GridBagConstraints();
			constraintsJLabelLow.gridx = 0; constraintsJLabelLow.gridy = 2;
			constraintsJLabelLow.ipady = 4;
			constraintsJLabelLow.insets = new java.awt.Insets(4, 11, 1, 20);
			getDataIntegrityPanel().add(getJLabelLow(), constraintsJLabelLow);

			java.awt.GridBagConstraints constraintsLimit1HighTextField = new java.awt.GridBagConstraints();
			constraintsLimit1HighTextField.gridx = 1; constraintsLimit1HighTextField.gridy = 1;
			constraintsLimit1HighTextField.gridwidth = 3;
			constraintsLimit1HighTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLimit1HighTextField.weightx = 1.0;
			constraintsLimit1HighTextField.ipadx = 98;
			constraintsLimit1HighTextField.insets = new java.awt.Insets(2, 4, 3, 19);
			getDataIntegrityPanel().add(getLimit1HighTextField(), constraintsLimit1HighTextField);

			java.awt.GridBagConstraints constraintsLimit1LowTextField = new java.awt.GridBagConstraints();
			constraintsLimit1LowTextField.gridx = 1; constraintsLimit1LowTextField.gridy = 2;
			constraintsLimit1LowTextField.gridwidth = 3;
			constraintsLimit1LowTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLimit1LowTextField.weightx = 1.0;
			constraintsLimit1LowTextField.ipadx = 98;
			constraintsLimit1LowTextField.insets = new java.awt.Insets(3, 4, 2, 19);
			getDataIntegrityPanel().add(getLimit1LowTextField(), constraintsLimit1LowTextField);

			java.awt.GridBagConstraints constraintsLimit2LowTextField = new java.awt.GridBagConstraints();
			constraintsLimit2LowTextField.gridx = 4; constraintsLimit2LowTextField.gridy = 2;
			constraintsLimit2LowTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLimit2LowTextField.weightx = 1.0;
			constraintsLimit2LowTextField.ipadx = 98;
			constraintsLimit2LowTextField.insets = new java.awt.Insets(2, 20, 3, 16);
			getDataIntegrityPanel().add(getLimit2LowTextField(), constraintsLimit2LowTextField);

			java.awt.GridBagConstraints constraintsLimit2HighTextField = new java.awt.GridBagConstraints();
			constraintsLimit2HighTextField.gridx = 4; constraintsLimit2HighTextField.gridy = 1;
			constraintsLimit2HighTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLimit2HighTextField.weightx = 1.0;
			constraintsLimit2HighTextField.ipadx = 98;
			constraintsLimit2HighTextField.insets = new java.awt.Insets(1, 20, 4, 16);
			getDataIntegrityPanel().add(getLimit2HighTextField(), constraintsLimit2HighTextField);

			java.awt.GridBagConstraints constraintsLimit1CheckBox = new java.awt.GridBagConstraints();
			constraintsLimit1CheckBox.gridx = 1; constraintsLimit1CheckBox.gridy = 0;
			constraintsLimit1CheckBox.gridwidth = 3;
			constraintsLimit1CheckBox.ipadx = 24;
			constraintsLimit1CheckBox.insets = new java.awt.Insets(0, 43, 0, 30);
			getDataIntegrityPanel().add(getLimit1CheckBox(), constraintsLimit1CheckBox);

			java.awt.GridBagConstraints constraintsLimit2CheckBox = new java.awt.GridBagConstraints();
			constraintsLimit2CheckBox.gridx = 4; constraintsLimit2CheckBox.gridy = 0;
			constraintsLimit2CheckBox.ipadx = 7;
			constraintsLimit2CheckBox.insets = new java.awt.Insets(0, 58, 0, 45);
			getDataIntegrityPanel().add(getLimit2CheckBox(), constraintsLimit2CheckBox);

			java.awt.GridBagConstraints constraintsJLabelAlarmDuration2 = new java.awt.GridBagConstraints();
			constraintsJLabelAlarmDuration2.gridx = 0; constraintsJLabelAlarmDuration2.gridy = 4;
			constraintsJLabelAlarmDuration2.gridwidth = 2;
			constraintsJLabelAlarmDuration2.ipadx = 2;
			constraintsJLabelAlarmDuration2.ipady = 2;
			constraintsJLabelAlarmDuration2.insets = new java.awt.Insets(5, 11, 25, 0);
			getDataIntegrityPanel().add(getJLabelAlarmDuration2(), constraintsJLabelAlarmDuration2);

			java.awt.GridBagConstraints constraintsJLabelSeconds2 = new java.awt.GridBagConstraints();
			constraintsJLabelSeconds2.gridx = 3; constraintsJLabelSeconds2.gridy = 4;
			constraintsJLabelSeconds2.ipadx = 2;
			constraintsJLabelSeconds2.ipady = -2;
			constraintsJLabelSeconds2.insets = new java.awt.Insets(8, 1, 29, 20);
			getDataIntegrityPanel().add(getJLabelSeconds2(), constraintsJLabelSeconds2);

			java.awt.GridBagConstraints constraintsSpinField2 = new java.awt.GridBagConstraints();
			constraintsSpinField2.gridx = 2; constraintsSpinField2.gridy = 4;
			constraintsSpinField2.ipadx = 52;
			constraintsSpinField2.ipady = 19;
			constraintsSpinField2.insets = new java.awt.Insets(5, 0, 26, 0);
			getDataIntegrityPanel().add(getSpinField2(), constraintsSpinField2);

			java.awt.GridBagConstraints constraintsJLabelAlarmDuration = new java.awt.GridBagConstraints();
			constraintsJLabelAlarmDuration.gridx = 0; constraintsJLabelAlarmDuration.gridy = 3;
			constraintsJLabelAlarmDuration.gridwidth = 2;
			constraintsJLabelAlarmDuration.ipadx = 1;
			constraintsJLabelAlarmDuration.ipady = 2;
			constraintsJLabelAlarmDuration.insets = new java.awt.Insets(10, 11, 5, 1);
			getDataIntegrityPanel().add(getJLabelAlarmDuration(), constraintsJLabelAlarmDuration);

			java.awt.GridBagConstraints constraintsJLabelSeconds = new java.awt.GridBagConstraints();
			constraintsJLabelSeconds.gridx = 3; constraintsJLabelSeconds.gridy = 3;
			constraintsJLabelSeconds.ipadx = 2;
			constraintsJLabelSeconds.ipady = -2;
			constraintsJLabelSeconds.insets = new java.awt.Insets(13, 1, 9, 20);
			getDataIntegrityPanel().add(getJLabelSeconds(), constraintsJLabelSeconds);

			java.awt.GridBagConstraints constraintsSpinField = new java.awt.GridBagConstraints();
			constraintsSpinField.gridx = 2; constraintsSpinField.gridy = 3;
			constraintsSpinField.ipadx = 52;
			constraintsSpinField.ipady = 19;
			constraintsSpinField.insets = new java.awt.Insets(10, 0, 6, 0);
			getDataIntegrityPanel().add(getSpinField(), constraintsSpinField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataIntegrityPanel;
}
/**
 * Return the JCheckBoxReasonLimit property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxReasonHigh() {
	if (ivjJCheckBoxReasonHigh == null) {
		try {
			ivjJCheckBoxReasonHigh = new javax.swing.JCheckBox();
			ivjJCheckBoxReasonHigh.setName("JCheckBoxReasonHigh");
			ivjJCheckBoxReasonHigh.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxReasonHigh.setText("High Limit:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxReasonHigh;
}
/**
 * Return the JCheckBoxReasonLow property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxReasonLow() {
	if (ivjJCheckBoxReasonLow == null) {
		try {
			ivjJCheckBoxReasonLow = new javax.swing.JCheckBox();
			ivjJCheckBoxReasonLow.setName("JCheckBoxReasonLow");
			ivjJCheckBoxReasonLow.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxReasonLow.setText("Low Limit:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxReasonLow;
}
/**
 * Return the JLabelAlarmDuration property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAlarmDuration() {
	if (ivjJLabelAlarmDuration == null) {
		try {
			ivjJLabelAlarmDuration = new javax.swing.JLabel();
			ivjJLabelAlarmDuration.setName("JLabelAlarmDuration");
			ivjJLabelAlarmDuration.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAlarmDuration.setText("Limit Duration 1:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAlarmDuration;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAlarmDuration2() {
	if (ivjJLabelAlarmDuration2 == null) {
		try {
			ivjJLabelAlarmDuration2 = new javax.swing.JLabel();
			ivjJLabelAlarmDuration2.setName("JLabelAlarmDuration2");
			ivjJLabelAlarmDuration2.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAlarmDuration2.setText("Limit Duration 2:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAlarmDuration2;
}
/**
 * Return the HighLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHigh() {
	if (ivjJLabelHigh == null) {
		try {
			ivjJLabelHigh = new javax.swing.JLabel();
			ivjJLabelHigh.setName("JLabelHigh");
			ivjJLabelHigh.setText("High");
			ivjJLabelHigh.setMaximumSize(new java.awt.Dimension(28, 16));
			ivjJLabelHigh.setPreferredSize(new java.awt.Dimension(28, 16));
			ivjJLabelHigh.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelHigh.setMinimumSize(new java.awt.Dimension(28, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHigh;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLow() {
	if (ivjJLabelLow == null) {
		try {
			ivjJLabelLow = new javax.swing.JLabel();
			ivjJLabelLow.setName("JLabelLow");
			ivjJLabelLow.setText("Low");
			ivjJLabelLow.setMaximumSize(new java.awt.Dimension(25, 16));
			ivjJLabelLow.setPreferredSize(new java.awt.Dimension(25, 16));
			ivjJLabelLow.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelLow.setMinimumSize(new java.awt.Dimension(25, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLow;
}
/**
 * Return the JLabelSeconds property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds() {
	if (ivjJLabelSeconds == null) {
		try {
			ivjJLabelSeconds = new javax.swing.JLabel();
			ivjJLabelSeconds.setName("JLabelSeconds");
			ivjJLabelSeconds.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSeconds.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds2() {
	if (ivjJLabelSeconds2 == null) {
		try {
			ivjJLabelSeconds2 = new javax.swing.JLabel();
			ivjJLabelSeconds2.setName("JLabelSeconds2");
			ivjJLabelSeconds2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSeconds2.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds2;
}
/**
 * Return the JPanelReasonability property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelReasonability() {
	if (ivjJPanelReasonability == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Reasonability Limits");
			ivjJPanelReasonability = new javax.swing.JPanel();
			ivjJPanelReasonability.setName("JPanelReasonability");
			ivjJPanelReasonability.setBorder(ivjLocalBorder1);
			ivjJPanelReasonability.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTextFieldHighReasonLimit = new java.awt.GridBagConstraints();
			constraintsJTextFieldHighReasonLimit.gridx = 2; constraintsJTextFieldHighReasonLimit.gridy = 1;
			constraintsJTextFieldHighReasonLimit.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldHighReasonLimit.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldHighReasonLimit.weightx = 1.0;
			constraintsJTextFieldHighReasonLimit.ipadx = 91;
			constraintsJTextFieldHighReasonLimit.insets = new java.awt.Insets(5, 3, 0, 190);
			getJPanelReasonability().add(getJTextFieldHighReasonLimit(), constraintsJTextFieldHighReasonLimit);

			java.awt.GridBagConstraints constraintsJTextFieldLowReasonLimit = new java.awt.GridBagConstraints();
			constraintsJTextFieldLowReasonLimit.gridx = 2; constraintsJTextFieldLowReasonLimit.gridy = 2;
			constraintsJTextFieldLowReasonLimit.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLowReasonLimit.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldLowReasonLimit.weightx = 1.0;
			constraintsJTextFieldLowReasonLimit.ipadx = 91;
			constraintsJTextFieldLowReasonLimit.insets = new java.awt.Insets(3, 3, 12, 190);
			getJPanelReasonability().add(getJTextFieldLowReasonLimit(), constraintsJTextFieldLowReasonLimit);

			java.awt.GridBagConstraints constraintsJCheckBoxReasonHigh = new java.awt.GridBagConstraints();
			constraintsJCheckBoxReasonHigh.gridx = 1; constraintsJCheckBoxReasonHigh.gridy = 1;
			constraintsJCheckBoxReasonHigh.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxReasonHigh.ipadx = -2;
			constraintsJCheckBoxReasonHigh.ipady = -5;
			constraintsJCheckBoxReasonHigh.insets = new java.awt.Insets(2, 20, 1, 3);
			getJPanelReasonability().add(getJCheckBoxReasonHigh(), constraintsJCheckBoxReasonHigh);

			java.awt.GridBagConstraints constraintsJCheckBoxReasonLow = new java.awt.GridBagConstraints();
			constraintsJCheckBoxReasonLow.gridx = 1; constraintsJCheckBoxReasonLow.gridy = 2;
			constraintsJCheckBoxReasonLow.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxReasonLow.ipadx = 1;
			constraintsJCheckBoxReasonLow.ipady = -5;
			constraintsJCheckBoxReasonLow.insets = new java.awt.Insets(0, 20, 13, 3);
			getJPanelReasonability().add(getJCheckBoxReasonLow(), constraintsJCheckBoxReasonLow);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelReasonability;
}
/**
 * Return the JTextFieldHighReasonLimit property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldHighReasonLimit() {
	if (ivjJTextFieldHighReasonLimit == null) {
		try {
			ivjJTextFieldHighReasonLimit = new javax.swing.JTextField();
			ivjJTextFieldHighReasonLimit.setName("JTextFieldHighReasonLimit");
			ivjJTextFieldHighReasonLimit.setEnabled(false);
			// user code begin {1}

			ivjJTextFieldHighReasonLimit.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999, 999999.999999) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldHighReasonLimit;
}
/**
 * Return the JTextFieldLowReasonLimit property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLowReasonLimit() {
	if (ivjJTextFieldLowReasonLimit == null) {
		try {
			ivjJTextFieldLowReasonLimit = new javax.swing.JTextField();
			ivjJTextFieldLowReasonLimit.setName("JTextFieldLowReasonLimit");
			ivjJTextFieldLowReasonLimit.setEnabled(false);
			// user code begin {1}

			ivjJTextFieldLowReasonLimit.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999, 999999.999999) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLowReasonLimit;
}
/**
 * Return the Limit1RadioButton property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getLimit1CheckBox() {
	if (ivjLimit1CheckBox == null) {
		try {
			ivjLimit1CheckBox = new javax.swing.JCheckBox();
			ivjLimit1CheckBox.setName("Limit1CheckBox");
			ivjLimit1CheckBox.setText("Limit 1");
			ivjLimit1CheckBox.setMaximumSize(new java.awt.Dimension(68, 26));
			ivjLimit1CheckBox.setActionCommand("Limit 1");
			ivjLimit1CheckBox.setBorderPainted(false);
			ivjLimit1CheckBox.setPreferredSize(new java.awt.Dimension(68, 26));
			ivjLimit1CheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjLimit1CheckBox.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjLimit1CheckBox.setMinimumSize(new java.awt.Dimension(68, 26));
			ivjLimit1CheckBox.setHorizontalAlignment(2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLimit1CheckBox;
}
/**
 * Return the Limit1HighTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getLimit1HighTextField() {
	if (ivjLimit1HighTextField == null) {
		try {
			ivjLimit1HighTextField = new javax.swing.JTextField();
			ivjLimit1HighTextField.setName("Limit1HighTextField");
			ivjLimit1HighTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjLimit1HighTextField.setColumns(4);
			ivjLimit1HighTextField.setPreferredSize(new java.awt.Dimension(44, 20));
			ivjLimit1HighTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjLimit1HighTextField.setEnabled(false);
			ivjLimit1HighTextField.setMinimumSize(new java.awt.Dimension(44, 20));
			// user code begin {1}

			ivjLimit1HighTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999, 999999.999999) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLimit1HighTextField;
}
/**
 * Return the Limit1LowTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getLimit1LowTextField() {
	if (ivjLimit1LowTextField == null) {
		try {
			ivjLimit1LowTextField = new javax.swing.JTextField();
			ivjLimit1LowTextField.setName("Limit1LowTextField");
			ivjLimit1LowTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjLimit1LowTextField.setColumns(4);
			ivjLimit1LowTextField.setPreferredSize(new java.awt.Dimension(44, 20));
			ivjLimit1LowTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjLimit1LowTextField.setEnabled(false);
			ivjLimit1LowTextField.setMinimumSize(new java.awt.Dimension(44, 20));
			// user code begin {1}

			ivjLimit1LowTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999, 999999.999999) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLimit1LowTextField;
}
/**
 * Return the Limit2RadioButton property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getLimit2CheckBox() {
	if (ivjLimit2CheckBox == null) {
		try {
			ivjLimit2CheckBox = new javax.swing.JCheckBox();
			ivjLimit2CheckBox.setName("Limit2CheckBox");
			ivjLimit2CheckBox.setText("Limit 2");
			ivjLimit2CheckBox.setMaximumSize(new java.awt.Dimension(68, 26));
			ivjLimit2CheckBox.setActionCommand("Limit 2");
			ivjLimit2CheckBox.setBorderPainted(false);
			ivjLimit2CheckBox.setPreferredSize(new java.awt.Dimension(68, 26));
			ivjLimit2CheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjLimit2CheckBox.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjLimit2CheckBox.setMinimumSize(new java.awt.Dimension(68, 26));
			ivjLimit2CheckBox.setHorizontalAlignment(2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLimit2CheckBox;
}
/**
 * Return the Limit2HighTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getLimit2HighTextField() {
	if (ivjLimit2HighTextField == null) {
		try {
			ivjLimit2HighTextField = new javax.swing.JTextField();
			ivjLimit2HighTextField.setName("Limit2HighTextField");
			ivjLimit2HighTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjLimit2HighTextField.setColumns(4);
			ivjLimit2HighTextField.setPreferredSize(new java.awt.Dimension(44, 20));
			ivjLimit2HighTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjLimit2HighTextField.setEnabled(false);
			ivjLimit2HighTextField.setMinimumSize(new java.awt.Dimension(44, 20));
			// user code begin {1}
			
			ivjLimit2HighTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999, 999999.999999) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLimit2HighTextField;
}
/**
 * Return the Limit2LowTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getLimit2LowTextField() {
	if (ivjLimit2LowTextField == null) {
		try {
			ivjLimit2LowTextField = new javax.swing.JTextField();
			ivjLimit2LowTextField.setName("Limit2LowTextField");
			ivjLimit2LowTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjLimit2LowTextField.setColumns(4);
			ivjLimit2LowTextField.setPreferredSize(new java.awt.Dimension(44, 20));
			ivjLimit2LowTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjLimit2LowTextField.setEnabled(false);
			ivjLimit2LowTextField.setMinimumSize(new java.awt.Dimension(44, 20));
			// user code begin {1}

			ivjLimit2LowTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999, 999999.999999) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLimit2LowTextField;
}
/**
 * Return the SpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getSpinField() {
	if (ivjSpinField == null) {
		try {
			ivjSpinField = new com.klg.jclass.field.JCSpinField();
			ivjSpinField.setName("SpinField");
			// user code begin {1}
			ivjSpinField.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(999999), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpinField;
}
/**
 * Return the JCSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getSpinField2() {
	if (ivjSpinField2 == null) {
		try {
			ivjSpinField2 = new com.klg.jclass.field.JCSpinField();
			ivjSpinField2.setName("SpinField2");
			// user code begin {1}
			ivjSpinField2.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(999999), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpinField2;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	//Consider commonObject to be an instance of com.cannontech.database.data.point.ScalarPoint
	com.cannontech.database.data.point.ScalarPoint point = (com.cannontech.database.data.point.ScalarPoint) val;

	//Make sure the following are in the correct format for a Double
	//since they come from JTextFields
	
	
	Object alarmDuration1 = getSpinField().getValue();
	Object alarmDuration2 = getSpinField2().getValue();
	if( alarmDuration1 instanceof Long )
		alarmDuration1 = new Integer( ((Long)alarmDuration1).intValue() );
	else if( alarmDuration1 instanceof Integer )
		alarmDuration1 = new Integer( ((Integer)alarmDuration1).intValue() );
	if( alarmDuration2 instanceof Long )
		alarmDuration2 = new Integer( ((Long)alarmDuration2).intValue() );
	else if( alarmDuration2 instanceof Integer )
		alarmDuration2 = new Integer( ((Integer)alarmDuration2).intValue() );
	
	
	//Vector for possibly store point limits
	java.util.Vector limits = new java.util.Vector(2);
	
	if( getLimit1CheckBox().isSelected() )
	{
		Double limit1High = null;
		Double limit1Low = null;
	
		try
		{
			limit1High = new Double( getLimit1HighTextField().getText() );
		}
		catch( NumberFormatException n2 )
		{
			n2.printStackTrace();
		}
	
		try
		{
			limit1Low = new Double( getLimit1LowTextField().getText() );
		}
		catch( NumberFormatException n3 )
		{
			n3.printStackTrace();
		}

		limits.addElement( new com.cannontech.database.db.point.PointLimit( point.getPoint().getPointID(), 
				new Integer(1), limit1High, limit1Low, (Integer)alarmDuration1 ) );
	}

	
	if( getLimit2CheckBox().isSelected() )
	{
		Double limit2High= null;
		Double limit2Low = null;

		try
		{
			limit2High = new Double( getLimit2HighTextField().getText());
		}
		catch( NumberFormatException n4 )
		{
			n4.printStackTrace();
		}

		try
		{
			limit2Low = new Double( getLimit2LowTextField().getText() );
		}
		catch( NumberFormatException n5 )
		{
			n5.printStackTrace();
		}
		
		limits.addElement( new com.cannontech.database.db.point.PointLimit( point.getPoint().getPointID(), 
				new Integer(2), limit2High, limit2Low, (Integer)alarmDuration2 ) );			
	}

	point.setPointLimitsVector( limits );


	try
	{
		if( getJTextFieldHighReasonLimit().isEnabled() )
			point.getPointUnit().setHighReasonabilityLimit( new Double(getJTextFieldHighReasonLimit().getText()) );
		else
			point.getPointUnit().setHighReasonabilityLimit( new Double(com.cannontech.common.util.CtiUtilities.INVALID_MAX_DOUBLE) );
	}
	catch( NumberFormatException n5 )
	{
		n5.printStackTrace();
	}

	try
	{	
		if( getJTextFieldLowReasonLimit().isEnabled() )
			point.getPointUnit().setLowReasonabilityLimit( new Double(getJTextFieldLowReasonLimit().getText()) );
		else
			point.getPointUnit().setLowReasonabilityLimit( new Double(com.cannontech.common.util.CtiUtilities.INVALID_MIN_DOUBLE) );
	}
	catch( NumberFormatException n5 )
	{
		n5.printStackTrace();
	}

	return point;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getSpinField().addValueListener(this);
	getSpinField2().addValueListener(this);
	
	// user code end
	getLimit1HighTextField().addCaretListener(this);
	getLimit1LowTextField().addCaretListener(this);
	getLimit2LowTextField().addCaretListener(this);
	getLimit2HighTextField().addCaretListener(this);
	getLimit1CheckBox().addItemListener(this);
	getLimit2CheckBox().addItemListener(this);
	getJTextFieldHighReasonLimit().addCaretListener(this);
	getJTextFieldLowReasonLimit().addCaretListener(this);
	getJCheckBoxReasonHigh().addActionListener(this);
	getJCheckBoxReasonLow().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointStatesEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(411, 280);

		java.awt.GridBagConstraints constraintsDataIntegrityPanel = new java.awt.GridBagConstraints();
		constraintsDataIntegrityPanel.gridx = 1; constraintsDataIntegrityPanel.gridy = 1;
		constraintsDataIntegrityPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsDataIntegrityPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDataIntegrityPanel.weightx = 1.0;
		constraintsDataIntegrityPanel.weighty = 1.0;
		constraintsDataIntegrityPanel.ipadx = 39;
		constraintsDataIntegrityPanel.ipady = 46;
		constraintsDataIntegrityPanel.insets = new java.awt.Insets(6, 4, 6, 8);
		add(getDataIntegrityPanel(), constraintsDataIntegrityPanel);

		java.awt.GridBagConstraints constraintsJPanelReasonability = new java.awt.GridBagConstraints();
		constraintsJPanelReasonability.gridx = 1; constraintsJPanelReasonability.gridy = 2;
		constraintsJPanelReasonability.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelReasonability.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelReasonability.weightx = 1.0;
		constraintsJPanelReasonability.weighty = 1.0;
		constraintsJPanelReasonability.ipadx = -10;
		constraintsJPanelReasonability.ipady = -9;
		constraintsJPanelReasonability.insets = new java.awt.Insets(6, 4, 13, 8);
		add(getJPanelReasonability(), constraintsJPanelReasonability);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	Double high = new Double( com.cannontech.common.util.CtiUtilities.INVALID_MAX_DOUBLE );
	Double low = new Double( com.cannontech.common.util.CtiUtilities.INVALID_MIN_DOUBLE );

	//check the reasonability limits
	try
	{
		if( getJCheckBoxReasonHigh().isSelected() )
			high = new Double(getJTextFieldHighReasonLimit().getText());

		if( getJCheckBoxReasonLow().isSelected() )
			low = new Double(getJTextFieldLowReasonLimit().getText());

		if( high.doubleValue() < low.doubleValue() )
		{
			setErrorString("High reasonable limit can not be less than the low reasonable limit");
			return false;
		}
		
	}
	catch( NumberFormatException e )
	{
		setErrorString("The high reasonable limit and the low reasonable limit must be numbers");
		return false;
	}


	//Check limit 1 range
	if( getLimit1CheckBox().isSelected() )
	{
		try
		{
			high = new Double(getLimit1HighTextField().getText());
			low = new Double(getLimit1LowTextField().getText());

			if( high.doubleValue() < low.doubleValue() )
			{
				setErrorString("High limit 1 can not be less than the low limit 1");
				return false;
			}
			
		}
		catch( NumberFormatException e )
		{
			setErrorString("The high limit 1 and the low limit 1 must be numbers");
			return false;
		}
	}
	

	//Check limit 2 range
	if( getLimit2CheckBox().isSelected() )
	{
		try
		{
			high = new Double(getLimit2HighTextField().getText());
			low = new Double(getLimit2LowTextField().getText());

			if( high.doubleValue() < low.doubleValue() )
			{
				setErrorString("High limit 2 can not be less than the low limit 2");
				return false;
			}
			
		}
		catch( NumberFormatException e )
		{
			setErrorString("The high limit 2 and the low limit 2 must be numbers");
			return false;
		}
	}
		
	return true;
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getLimit1CheckBox()) 
		connEtoC12(e);
	if (e.getSource() == getLimit2CheckBox()) 
		connEtoC13(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jCheckBoxReasonHigh_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJTextFieldHighReasonLimit().setEnabled( getJCheckBoxReasonHigh().isSelected() );
	fireInputUpdate();

	return;
}
/**
 * Comment
 */
public void jCheckBoxReasonLow_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJTextFieldLowReasonLimit().setEnabled( getJCheckBoxReasonLow().isSelected() );
	fireInputUpdate();

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PointLimitEditorPanel aPointLimitEditorPanel;
		aPointLimitEditorPanel = new PointLimitEditorPanel();
		frame.setContentPane(aPointLimitEditorPanel);
		frame.setSize(aPointLimitEditorPanel.getSize());
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)
{
	//Consider defaultObject to be an instance of com.cannontech.database.data.point.ScalarPoint
	com.cannontech.database.data.point.ScalarPoint point = (com.cannontech.database.data.point.ScalarPoint) val;

	java.util.Vector limits = point.getPointLimitsVector();

	//If point limits exist fill in the fields
	if( limits != null )
	{		
		//Handle Limit 1
		for( int i = 0; i < limits.size(); i++ )
		{			
			com.cannontech.database.db.point.PointLimit pLimit = (com.cannontech.database.db.point.PointLimit) limits.elementAt(i);

			if( pLimit.getLimitNumber().intValue() == 1 )
			{	//Handle Limit 1
				Double highLimit = pLimit.getHighLimit();
				Double lowLimit = pLimit.getLowLimit();

				if( highLimit != null )
					getLimit1HighTextField().setText( highLimit.toString());

				if( lowLimit != null )
					getLimit1LowTextField().setText( lowLimit.toString() );

				getLimit1CheckBox().setSelected(true);

				getSpinField().setValue( new Integer( pLimit.getLimitDuration().intValue()) );
			}		
			else if( pLimit.getLimitNumber().intValue() == 2 )
			{	//Handle Limit 2
				Double highLimit = pLimit.getHighLimit();
				Double lowLimit = pLimit.getLowLimit();
				
				if( highLimit != null )
					getLimit2HighTextField().setText( highLimit.toString());

				if( lowLimit != null )
					getLimit2LowTextField().setText( lowLimit.toString() );

				getLimit2CheckBox().setSelected(true);

				getSpinField2().setValue( new Integer( pLimit.getLimitDuration().intValue()) );
			}
			else
				System.out.println("****** UNKNOWN LimitNumber( " + pLimit.getLimitNumber().intValue() + ") found in database table PointLimits for pointid = " + pLimit.getPointID().intValue() + " ******");
		}
	} //Finished handling limits vector


	if( point.getPointUnit().getHighReasonabilityLimit().doubleValue() < CtiUtilities.INVALID_MAX_DOUBLE )
	{
		getJCheckBoxReasonHigh().doClick();
		getJTextFieldHighReasonLimit().setText( point.getPointUnit().getHighReasonabilityLimit().toString() );
	}

	if( point.getPointUnit().getLowReasonabilityLimit().doubleValue() > CtiUtilities.INVALID_MIN_DOUBLE )
	{
		getJCheckBoxReasonLow().doClick();
		getJTextFieldLowReasonLimit().setText( point.getPointUnit().getLowReasonabilityLimit().toString() );
	}
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1)
{
	if (arg1.getSource() == getSpinField())
	{
		this.fireInputUpdate();
	}
	else if (arg1.getSource() == getSpinField2())
	{
		this.fireInputUpdate();
	}
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
	D0CB838494G88G88G6DF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DD8BFC94C51A0F3BC12366B4AAAED9840DBCA4E254A83822629AE3D0DC71163D45B56EF1EEEE175D653C2C324AAAAAFBF41EA498A0C0B081F9848810ACB1A4C39EC308C8C80000D132E48871B61E8CC9A799B21999E6BA4984825CD7D57DD5777474CC9EF75C39797DBE3A6BDF75BD6A2B2A2F2A2B2B87A1EBEBD8CAC2E216A0A4248872DFC502A0F43A84E13C23B98D31GB1BAD5D07DED83DAA35C5E
	188C5790484B0C859236149006C910CE1E9D1D1A8D776B049DA526F3F063C3CFA164AD7B4D34377D6B895C02FA165374EF3BB3603A8AA89CB0F2DDCCEC907E5C1DA363F3E49CA9A1D190E25BC9199C5BA699B3C23A81E8B4D0F8BBAB9F8557F42AF9D425D1AE57F2DB183078A73B93D792B9AA19C8399D0C37D63E668A8FA744F790DD55368C19E7G525301C86513046FF5D540352A212B63E02B3D3B2855EC3359ED12585AD9A436D9A43B3348E13758244A4A86F22937F4DB24A394EFB05BC42B5059C9E45FE8
	CB2332842198520BA9F6321C680B007B9820799C7F380260C68B5497285302F57CECF715DC0766CB8F8BCBDF35A52D76D147G8B2BDB38C52963681E8F16EF206D91331578FD8164251D0FCE5D84F48868ACD0G50A7349F3C3465AFF02DBEA755583B3B6D36EA076330354D79253DCD3442FDE5A5644858A931556EEC9384565647EC05BA7678A346F7FA58FC4CA7617B305DA3CE5DA74C1F76C562F29D1F4C8FAFCE8CBD4FFC91F9DE718953FD0BCF5DF34FFB76CBCF5DE784356E786B6DC9A9BA3A273FD21038
	C01633C425BB3F0B3475F3348F6440FD025CBF987EB2452DAA1C75515394A76981480B6B423E513C8AED29CF9EABE416546B7AA1B43ABA31564AECE831EAED79B16DF7C1D626F3BD3765E7941F294219AD1BA8CE520BA0EFDCF7F4AA6BDFAFEFA3E3364E961D5A86B4GB49AE8BA50DC2085B66CE3D9DB778E310FB5181D52619E0B35CDF48A0C77652D9F43D5F4CBCE73F11BCBB25BDA45A63353E2BEE795A19632B23F58CAFCF04E036F1FE95FEF859D47C41B68B4CB963B8D241B3BC5C9F43AD81985DB5710B2
	CE11661AAC5DA2A868F688A8B7FE6BBA52B6E617F45A51E616C425844BDF3C1534C92F3989A402G38E7FE79E0BB71D7329D469CD0219D7DE04FA5723E901D90AEAAAB6B6D762E9E070954C4983093BFCF263163905CB75B51476F6EA0D89424E3391C7BF2572A6454DADA0979E6E7BF0E31974BC9DC0E37E31FF933997BCC6D4A49C261ABA7F47B8C21CC876BAB798E6D5879B915C96CE2693F15B74BBA06B3160797ABA42AA3699A48DF455B2AEDA7E92B86282F95A85801BE7E575CDC56E78F1BDDA2765213CE
	68E0E21B62F1E6E7CCC5BEDCD3E43BBF82A2F3D2362C034C152BE9B9A2976B76AA6519847A66GAD865AF4A1BA3596488AB49AE8A65082208CA77AFCFBC5184AFE5437CA25D32DE7154A86F53D37DDA3FFFA365D179E36A64976E2FE3A4A4E751ABA28E52D3E46363236FEF59B51A3760AB629D6F40836B638F149F92FEFA3364A71275D4EF996D05C81712A8682040B63EFECA375B2CB1253F22EC7929BC5C93258BA147C3729AED74FB907926AD0577B5B080F6CFD902FF8797F58C646FA2B5DE693E92763ACF2
	3E300374DF51AAF60351B5769E1B4470549DE43CF6097DF22CDB3103AC60FA4D569E92B759FAA752CCFC6EF0DA3A21AB1350AB04BAC99C1AE94BD1792894B032EE3342B54009B125EF8746143C0B778AD15F6ECC541B93C3A757A646B9653520D309A94C268FFD581E10172660AEE6EB251B4C83F46555A8C1ACF62917DE72D88E5FC564E53922D373018E39F0ACDF4EA37998AF65C1DCE337DA19CB392F4F6F4CA4EDA3DE6891E11AA101798B6E3F8BBB57EA721AE4DF327C7E1D4417D6D5CC0FF791DF2EB1623A
	3699528E20909773679C977387ABB70F17B3CA309E14FC158B5A0F659CD0A648E7392ACAAC978EE522FC16DB444B45C119A5C0B9BDFA65269D40F2A648377678AA57D1056502A1FF92D00CEE393FFC066504DE10556B2B5C6B3C1C914AB4GB9FAD93948DEF539878FF2F9FDA02FCFA9E76CE365D8BFDA7EADD9836A2D71BBFAACC5BD12054C28E689863B23C7223D0B2D6BF75953E499C1FD38FEBF64A47DAD8A5231C00B7AF0EC7CECA52E5FC7F70589F193B77AFCC6C9F3B35B8A5D4A58487DBE2E5F5F5ECDF4
	DAA1EF400DFDF959CE759A225A653237DA6872C56E078F6DD20F9D06CD5B35822E6D9627CBBA8E414D0DFD7609DD44DE323E11E1F93DD7E94C106B9A82FAE7831D6DA778443EAC156D82E079C075C0C9D45E3DC6324EEBB5BBC529087E4B16CD79674D3DE6F7112B8F82E1910D32C5B5A4778839AD105BA526E0AD33D1241E17B98B1AF08DBEE15DA1D96F1C07468AD5D757A56ABD93852D5E1C985E4FFCB937DF7169402329C9717F7561F2D63FBF737AED3A981DEA3A08753BDA26D73FE048E7FC0F6CC73EB040
	E2B91FD552634B3C04FCE2A572658156F28979A6987478C6F33E8D8710AF9C30B84E576821AFE700719D9A40F688B8C05A41A2095DC5649F9AAFEBBA4D368E310D36C51139CF129B62B8644B6D40F44E67BACDA0AFF8GF546756B690C99C0DBC32A10AF96304C4B4837700C360EF510E73D0C363ED7B512BE43F4B6FE06BA83C05E8C2E536856533968B25A3A76A072A5DF09CE2D3A02FC6F14E96BC87824ABE8EB7FC183DC4DF452A8E217865159EEF7F6FBBB361AE64B664AFD3CF16DE1381E2F6994DB3B8E5B
	5D27C4334BEE2B33F4F4FEDB2D91D66FDB5A81B2A6D8BDFE416B9184B6C671FAD84B756A31648A6EF33C32275A5B0EFAFB5F304D1057FAFB6C5A676A0C2B4AF3C16ED56FE784763CB91D464F522B6CF9B305634FD03CCE05337A9EF5E2FDDBA0AF6AAABEA3DDD7C056ABF981B086001A019CC0A1C0118198B72BF76F9386F986BD8E730053E636B6FAAC4E64D8795EEE5287E0BDA527BF5CCD6C206BA96E07C3C62D9F6684F856FD21A64D783E7262AB527A73BACFBE560EBB73491CC767B255F0292CBCE102756A
	D10BE8EDE36B39771C6A75DC5EF56C79328E2EADC0ECFE7BE59F19B7C8CC152E43F96C72D175BCB6F335A1112CF1746631D8593EA557A9B137106FC36D59C36CB4FE8FE2AD1095A888A882E8B6D07C77300DC665FFB6D89B513EE73257GD687EB2D9A6D3A187BB4E97F485AE20D971FD7DBFC5F130F796FC7E5683F3C6F63735A63A334E15620B684695AB0D1C29B42388DDD15A333215F0BCFEBC3F2201E8D2F995006CD01E8435683A33361D3AFBE2D8D8101FA6362E053A77A6382A31EEC6BCD54DF6479B314
	5B7A6B915A3AE8D0DB176A7AAB7BA25A10B58AED78C15548EC186445273541BD4A130F45076ED23D789082E5A70D4278303599573FB3391DDFFFB6B2BBBB3C7834F6AE9E2567AB3B9BED48389EED48B9B8B29BF6F971ED5124EB2F573361AF65E9320C96EE437C915A704620B60C5635E1A237A18272595E61E17AFC32G52CBB836728241B65D901DDAFA835A7A2413E04D10F6F3EC96457265F592436A1D4D9ADB06CBC4876FF7B76855DF7C9F6BD06F8D26E872581CF2D3016F3D1D5B8A7477F64694686F6D0C
	AFD843F010215E29467C49857E77FE9EAC505B7B59CC6B7162472B29EC17CBF451C90EBC044AF93F17C8FC705A837A4A5CAD4ABED8A774995365B2C7A4FB4DBE81E361BBF4ED24938BD54F36FBDD247D49B3EA688D78EC5B7FB1D9EBAD05F4D6D0F4EAE190760BDF7579EB335AA04FF6EE1653D63886E872C6C3BA9CE8268AE76B05C78B497AF5AE6071C049B7C627EE822A3A915789B5FF99F4ADF7446DE7ADD73647F71F285A235FA78E6FA17EF159FB1C2D22365D0F6F4961F94A0A04BCDA11A6E2E5CED1FE49
	6C6C903D7A4D57141F657962BF3747D75FE26DDF39DF5D7E25FC0CBDD5662F3DDAEE74EC93071C8E04EB28A6AF8252D1C073D4B8537D26076EF22EBBDC7227FB21C67ED2B91DF9D3F4EA5ECD1EF9C6C857831D3DC92BFBC225DAF785577D3A411FEEC9A3BFC8CE0F03EB24A6AF865231C071DE3A1B8E2CD069AEB1206EDBF56BAD5C4C783364EB7E4D0A3CAA38EFGEA3B59D3379B5241C0E1C05365BC26FBC315076EFD28FB53C5BD5D33E55E7972F511CACFF2F0F4EA8ED0E130276EC310B681F5828D842BF53F
	DC2576F90900735264D21C17C202B10EFCFA06441159100E83DA920CF1A42F595FFB604CFF00BE708F1E76B05DA11F29F59BF8FBCBEEFF720CA02BDED75E4903EAF9E5DC5E5EF2353C33B22FA4DF03B43294F96BBED74BB3AAE341C35EB819BFD23E46781477351D6C5BDB4D7D769E89626BAD44278EBB2E57E201EF91D08E6005372877B99F69A34FEA6DCEF3B70409C60758AA645ECAF873AEA0EFB0148F830A820A8392EE45FD8EC1A0FBF1EC97D492BB1C9629DF5EE717EDAAA4E5588C42B68F4C67ACD6A8A5
	573554256E031DA0F760D625FEA137FA571550A49DFC86E0F3B4B85BA378EBB669D7F13732BD024D9C6F24F8DE0802333D032BF4BEA369CE48B35D06EB24843A26BCF4BB2CF9EFC7AC09BE4BD9EFE74F418C5BCA31G404671F2113D840B04F49447D61EA1583C5B19FE0695D0ECA1E0429DD8EEDAAF2E4D326E40F96D49C25F735233057A73528B057AEB15178B7D2FD5DEA974B51FB07B7E258C7D5246ED1E5E6B6D1717FA51AF6EBBBC7DC77C924C3147F33C7D720FB96817B0DEEEE3997AE5BA47FEA221AD49
	23917B2B415B16CE835A12434B7D49053694F26C24495B16F6935AF208177B258BEDB1F1CC580736CC6258DBAEEFDBD2A534E586AFF75C00364C61585B6B3DEDC97F98ED096365BEB320AD0BB9768855D1FBA758F7A7E2C7FA503ECE0E1D223C83108E6558A3940B38D32D77275CFF33F93935925ABC1FE37574CC42A2C8A77FG31260BE8F3A647FED5C1303C9F30FDC7067DC78556231617BBF8916BB11AE37FF8916B914E3157AB309EB3B9D6EE742E4717C62C47DCDE6E31FE2C47820EAD2E762E476643D80F
	A59C1B1363DD0F5F64E0BDB246601CB635D49D63DBGF70CD1E2D760986F7846E2563DF40E8C9D436A104E7107A8BEDD0533D8360F62A4BD9772CAC3514E834DE8D3A8476653FD3D0510DE4A311B5DD8AE7307080DAB43F2E7B9F614BEE7CA108E6458A79B88968A69F00E85EC60E316E34FD05E6431905F46A216C14B65C35A48318B1C37F6AC6E4F075A71FC5EE840660C45FD6B12339E7B1DE4E33A3272185352F6585CD1CFE7C8368E09234F4204BF79AEDC072CF29019D510B681D96F42BDCE77AE5C630C38
	EF2CD09F70056E3B3AB813A1B170AE792C5DDD4A9EE7F84F874B194E9397D06782485FB40EE06E8B6AFDF8B2F716A4323D8E16BF55A31F44631D02BAFF3EC7BE3137A2C91D3F4A294EA7BEAA1079D95DDE5A09F5DB7AD238E0FDEC3F4F7710ED6364F50DFCEDE85AA1645D7461F2F9EC53789685F97346E13B5DCD636592C8E70CC7AC3F8947ECB147CC5BF1AC56F26CE09736F9B347ACDF62388B9F0F6369CCB6D9BF4D0474BC20F8200C3BA10E835583D9010200A68145G4581AD814A39874644BDA843C6E554
	C35AFA8FFB7F5EA03FB70F125FF713F75941109781B487A89EA8A38C7404210CD19F4741156870D82F2852F12A7BA079BE63EE7DF58E5292B91F54CD0DDB8761C30A9F6F3F5CCCCDBE710BBA0DFEA4770A3F22E8DD57EBE42DD35DE7EB7AAC76CDFF366099A46D9E4DD04F77AC9FA4DFFBC66C93CDDAFD36E833006B584F1B885EEDF6DB3AFB3A9BAD97C5A1B80C6E715178D88F774DE138A65ED8C6ECE835DBFB3AEDAE193FEBBB1169F00A6D2253A936D18902AC230E465339F0BF1F4B38FC091C49E9375BA4D6
	66B53AF6CA3B97E2643DD8A66F8C51A35A489112B6D9CF401754CE0BCDE367A93A2E2C83DE935074FB69DA2937F690BFA5497EA457G60998D34DEEE1BF0381FA5309841F42452B83E94703489B0CEA836185ADD8569BA0E95527ACA108E6058FBF40E1C8E692889E8DFC959E07625286C0B1F60E91FB0D1EB5FBBF4FD1281F84EC454FB9BDD4754C33A19E3DBE839C0C807F06CD69A43E2A0BDF7A25A77C717BF7B5285FC3EE234F42227FD1913347695507562A64073A7215E3D541696C8F7F24CC9ED9E8D69B0
	0E9968BAF09E2463A621FDA643E076A529A8E33227FD3913957BD85F1AAD11BEA94103855379BA352D904A99A7239DB63A26EC00F44BE46C279683697B6CA5B85906B35B5AF81D3FE7C099C13C7CECDA7E1C1D9C3EEAB0DB6039D2A93F04168F033291DC6781EAC7AC2497F0992D748CDB375959E13189C256FD641B04BE3255A54B39CA570C4D00375D07BC6FD6105855E9F7DAAE4238B3DB2B2D168E9BB99BA3B0FF2EB78E569F93D47DF134EA3C90BC847414F834F78735E19C60617771F1C031A8C84F6158F2
	A316DB4231DE0AE5CE0175439444DE276B56BAC81BB8F6142ED183A19DB2857B4587B903758BECF33DFA4C84B9F81E064D474F523D1C59004F1F02F3F444957A61C9B8442BF044B688B8179B43F18E19F2061C238B02F4A450BC2045BCEFBA4DA36742F26473DC642C964B5BFFE6386F8308FCFF79C1DE7ACF8DDB07F64E526BA3F82615354529E6FFED210E9F04326E0758G34C34E2B027B47BD4668BBFD6AB901750195EEDCEF565D0FFDE002FBB03DEA5DAD9A3D8E15DE26E3D6996A98389F4753B7651EF318
	0B4E8FA110BF0E17B1EC4018424668D8322DA67BE64C06214CCFC879AA9B099DF3607E4F4B16ADFB8F08697A05461EF8EACFAC14DB406D5921316769F2451E20C16DC971E3CF56D4260358437A575DDEE7E1063236D9A3C40C106F374366E34F16F174F9A8F7AAFB36CC60783FD13CD00533E732AC87BE1399A1EF5CD4BCFB93D6C176930587A0E68115825583D901029E40FD2D2ECF0B05C15E57186C0EFA31D7342A362AD87DCEDE985977C24FAE47E71F69CFCE90E6DC7FFE022F33AB639EE0B1E426FCDDE85A
	C1F1D63FFD7BC97D62A1AFAD821E8900EA011A019C91D83FE91F96327A11B3495512E4EE6D2427AF31F2CD9631CF0149E6AC6B175DAEB21F31B99177F683DD38BF9B887247814D83DA98414E5BB13E47CC5EFB422F1A10CFF8D05E9306EB61037299C6BAB64E506733DA409A9EC4AC10BE1F85C3BA74C10CA3273B06B3C6E300AF76C1B6A6681A8D6ECF42759B8163C8A8FD36DB82F872C3D8079BB6127797A76A4D67C4ABB98207316E196EF40E575B7B844499510F6ECAC8F9FE4409672F65794027E44B794752
	F83EC1AFFF41C6AE5F20A75FA62D627279B192B96F0D4BBC4F204D6BA94E627529361A1D5D35BD6CE3A4834B3FFB67AA1E5FA8365AEDEDAE83177B8571D12343E22316F07C427EF57AB2657CC5A9AB35B2399F0D6569EA19F04F4E8CC6E42B77920A9FE27D4674901EA96CAA534B6F6479F623DE7EG4FF7B56B6507707CA7772973A7497931BC3F36C70FBF1E67BF2E1BAFBC0C79ABFA7564E7C1BE1B93369F90380F5EAD5D85D71B58174B4FBF56DAE068120F3C728DA525952546FD65259505390662820CB7E1
	FD6A6FD20DABAAF43F07207177E196E72A646B4C9BF648EB59B1D50A8D13FB3778362134D4564D786E2CD67804B3BB35FCC7E1F24C5F6EB25BDCAE51E9E95FDE18DBDC18DBD2CA760A987F0C438AFF781074EE15479CDD8F523FC51FFBE3A87FF7B5641E7969DFB3B0AEEFFA60AE2176557793F0AE557AC9127D937330325F36707C5265CC668F0FA8B6CF699DCEFBB17E19C7957EA525C37775B7DFA8FCD3F3066F6B00FA05FFF559C8FD4DE4CD7CD27171CF3ED264569A066F0FBF1CD4785FF08D5D9F26D38A5F
	E153707D515728707FF4C8FAFD7BE32CC97147332795393FF68D5F9FEFB5A97C878DC37747628E056F376B076F0FEC0B42EF9812DE5F7E28BC2F78631BAEC56E67BDE4ACEE2793D6EE49F625EFDA15B2477C69AEB7026E7282AC7B00C1DFDE3ECD1137DC9A0ABC469768D0783EEF5A2E6F43B60B59EA6FD0B91045171F7ECD9D07B347EECB44F52CB63EAC1563CADA2492DF187E788B0A7EF995C337FB35CB7137C1D2E44C3E28720FC1714F699E254C4F07240715ADB66A4BF3772AE2D37F506DFE432D70AD2C9E
	2A3F996F4A0B8A6F33B9C357D9FCC9610BAD75DE873F454F24D6C532F530892E1D116C0CA263FB6E0A77BA7817D710AFA0122D03C3619AA137B1DB5F968B43F9660B815E58C8557A966E7FG4FFBEF71E75052CC722C201C3AA76BC779238A52C43C1E3B69B9AF259C2CC3D545D819744F695A4E7B9B91D9461DF41DFB426B5B0D614E494D02627FEDEED25FF63355C5762FFD4713CB898A4F7CB2B58F4BBF172C642F5EB03CFEB4AAC531ADFE39A2C798261C55B48C30E7475FB9147B37D4779B9D7F53F32703BF
	176F3B26BA58F3677D953E4F0BC4D6681F9779D1057EF91119957E4F2E46D4787EEE79258A75F74B6C3979D769E43D3D8446CA56A35129C620FA20330F60B37357C6522E64179F3C1F170F34F510F31464579EA454DF355FF7DDEB768F7E9B91757B493354B93BA4593B09FC44CF6DA76DA5599D94E45899FA1E518903CEE2C5E57C1B7D741C0758CEE136AFF2CE623F5381F51B7E883E2B7E7DE6B2277B7F4E54E4112C62E1B96C49E76826CAF846BD7E91FCF77D6F0D74CC53235129D5C026C771BD79B2936E49
	D8811B74A81E55BFF69A776D66BF0AB2FACE53EF2327C527968255CE43671E0B9FA9E36D4876A26DD87331CE6277FC5C05E7A8F2DE36647A16332F1C8CD8188906962FB0763CC49EE9E5DDEF2ED2F435FCBC94DD72D968F69FE7216913B2735DEF3271ACF44BB4746704B24422B8F61F914FBA66CDC7ECEAB3E2AD53319FDCA95F256E87DD560E22732DE464953513A900FC1422FA98E7F27EC85F9951FDFAAE7BD28F4A1E4331B7B9167C98E2553D48DB75986E515D351A3E1B03F448E3B87EBA8E6C01EB13793C
	3C9B47BE2727A3904086275DA1BA25FE0652AF5D65F17D6BCCB2F7C6021CB9C00B0052FE941D3A892894288E2885488194849486B49DE8B650FC20F8A061F168FB0F239DFBEA8B7C58A19F6E204140C95E9A7178D2C726C1F9DC4EF66C74A583224989E86A13674E0B2D1249CC9667EC6CE41761384D87DB4EBE0E7E7B368C47DF6863B8F65E3FCC4A4E0574C2A0E1860E3F442BB87EF2GEB1801B2CEDC457197B483E538E959A8C84782AD829ADE7F8FDF27547777693EE3611B69034742775209FC91E218295F
	A11FAFDF1ACE56D27676F61748E3EE420ACC0E9DF7B558DD963249446637CEE930FD5934A7743E79689D14CF7B8D8C63FB187265BDB1343391C6A84776333F25B1327E896DBEF7A7451B1F506EF37F31915B348D72E6BF016F0DD2CE0FECFFFE5D88796A074DA76F5F53B8DF7824363EEFD35C7824363E17CEE3FDEBA1AF7CC92C6F42EC3D75CB46D3723352D37A6DD08AF81DA62F85520E27747A42945DEF9382653221BEF4C4G9E25491B8769053ABA9E707836F629DCA6F3A63B664D7456A177B70F7369C639
	DC3DFCBD6B136F5DFD7E5ACD7249975023668B124B0D132F11BE795E71600B114B454A57F81FFCC7FB7D59A9BC6D4D47E2C258EAF24F5709ECEA1073262CA6EBBE0B8D420459EA39C837249B21202D03653F1B69BBEE7DB9F37038153AC6FF8D173146FB8D37E60D7E9AAEFB0DFA8D4730CDEBC83C6F00392619175B46312FB9360B4AA45899CB1B544971C2EA97416BC42290F183352B0FE24DB2D621423E46356D9A92FB214A1DF6A772D62F216FB5ADD6ABCB9F21E5ACB688455C9FA7280D9607D9E577C90EFD
	4D5BFAF35948E2751EC17974E3F59B654B5269C33AF16BE90C555DD768994D2735312B1762F5CFEBE357324B983BCC10B763E90CDDE9D7C796F3BF9EA15F49E17331FA4553B541269FEB6B7BA70A677FD8DB5FABFC3DD18AF921C06CACDB543A1534EFD8ADB631067D621D0067B81E274F93CB21EC56B3F8E64355C84F61C23A6A997419C877CE65B3132A7B779A2F7D33F226695A6BF86B34D227A52A7B6BCEDFFB5DE32F298E5626AB68D93BBAE84FE65E4E63F370BC2E15E3EB8F61F95CG0E35DF42337123B9
	96520467FA42B9D65C474F46F36C45B2541B9105D8EBB96A4D6558249E54DB4A31AD2528370EE31159283705E3AD6528B702E3517479AB8A52F3B9B61F2E1B97C0FAB1472A93D01E70ACE24F53673486C81F655883749C3A8469C00EE5EEC45E900E2DACC5AC0EE3E3E85D0A2323D38FC5A3F608BE031BA05D4631492711574131953D489B45311DCD48BB0FE3577376086358B77C9B226267903B443ED302F4A8472E3BCC3F1100F454F3B85EDDD714B1B0203ACF39F26D4743167F839DEFDFD56A74216ABE706A
	3557BD7E1A6AE07B9E7FA2113508C4F61048FE2800E7AD7EC4637D9CE8675867F01F7C264694ACAB609E4ACBE429067B3FB91EE7C0ABE85CC80B01B9A3864F09BCCF31FCC817F26CG0DA5E7A1ED65589DB41604C2FAD28C1EA7195264EF3FDDFDAED16FEC623C98657B972663EB9A0762G0F67FA5F267DBEE7D6F4EA5EAC4402E95CB0C23A16E3AF53316A0074G470E51B19D8E696933506697328733B9450F4D8BE6E9ED9EC5EDD9847852D958AE2F99BD4F2E3DC5E3DA7E73606367314CBB02675935B7098A
	79BDC6F342E0B62A4FABEBEDECFB1E6DD1E1BE7429E6CFFB5E21F15281658638BD511A33F4EB3295FBF6EF74E78F1E1174653388599EF23E0F494BAB9D6CBC23BFF9B3C09EF9193A1B576F219E4F7A45503E90836566727A1DB96DD9BF7A9ACA2E5F4C530379FB399FFF8BAFB09DA93CCF6C2371BB83701C97F0BF764C86C55FF83F63E639201CCB5653D77702561F125FEF83D28611E7FAC16B4F5B4B74E350D4BA964FC2F96B8B6857504B5EB16862C6015F3FCB3681E5399D0D64B7EAF55E79D4438321F5FBE1
	EE0967BB738DFB945E509C721C341D3CB15B0E7974E7F170BD74A1BF6FA88DC6902C3C23BC7F29427737CBC3677BC531429758B43C77D76F17A8BE5934CF1173153F33A29A7D1F17AAFC7FD5CE7C3E3D5E5E273CA7BED726647F30471F5CB28F390D958A5FBAFFE7E6B4FC8F6FD778A6E58F4F9F7FD42978C3D01D0F3A3EEF687ADB8EAAFCF30561693F7A39227FF72AF3D6778C693D24014BC92FD1649456AA77F7284EA1151E9E1EED4F9ED364641FD064EC98D21FD3EC7B628BC50E2DDE390F7BCA1159F3F928
	E3127D7F92FF7DEA59EA0869FF90D2DCCEE1B567B9CEB7B062FA77D5A67DE6E2D22210FCDCC8E59BBA4705E432ABF3DCC8211BB64705A4496EF013373609EA12040F48584E252F4CABA5E18574ECB9810A56FAF7066ADBDEFBB5625B5A2CC4A191F4F50ACCF8CA9F59412162332EC200F4DD902E501916AC0B8AC152CA0FEA4E3A376CF7F3DEEFF2A5892947058DE486E9941DF413CBECA33F13A07F827CD1A11D7E9791649BAD280F0BDED2DCD23FD52C97325ACF11DF8FF6C9E2DBB5EE1F39A4E1BD7D11453D64
	0D605EBA332BD3A2DBDE876A05E49B792DFAE1AD4B361AED9DFB59DE5A81EAF5C275D952B8B4F28BE06FBAEAAF11D244252CFBEA754EEDA7964CC892920F8AAB2CF6F35BD1F32BE4F79E975225CE27686A34DB5B5C0E4F8FD1C1E9F4D1C55E86A60138CFF875C9E50B481185EEF417FBFC704A05AB0FC2FB381D42DEDA12CC18C53CC4DBCD2759892AC467F1097C763E5D1947732E5BA26DFA79D8F6CB2210A0D71238BE5D6D100454F6F337455AAF89697C0094ED171500C8CE00484FD202F1399A63B00B6BA83A
	634562BB6F7F60BF298E05D59236B2EF826B5E8E515E3D373A3D5DE29339GF0AD085F4C4513B2C51AB2EB5D372DBF714A32F35095EA05246E62E27AEF897D57C07EAD21C889C5CA8C605FC5025FBFED3E9B6BCC7B8F9FBD2487949D05C614FE790797A77CF35522F8285DD1A1057E84A534C326D26A2B1E6EF322737A08BBB7FFB4BA6EE652CC8E52DB5CC46E9A37262B13948E73E4013DFECAD44A688C8E12DA6E5585897A11F7DFA270EAF50B92E0ADAD1DC57F4D9E36FF8857864A7B892FD61E2FEAA52A2A15
	74F7D72DA2FF2DC59072026E6F2F350C3E2A058829D602A4AC170F947252E441D82B139AEE85D359CCF8EDEAD7635631907F48D2152CDC84DEBB435FD76D3E72D7BB32D87B14DB08FF24337E98A8052399AAB4E9E4F52C3D06F5ECF6F3CB69D7DE3235095FBDCB3FD5ACDD2932145914CC4201C7C80012891D1C03AC2989CAACCE75D1E6253A0CA4E4507F0101AD38E0713206F5E17C7EA457D0C926AD313548BB2BC4494A727851A8D9182EEF93FC78B6595337B8E3263277EA4CC704675AB87D029B2AC2D6215C
	E7A4DEED52C9FD27C66EE5252FA44AFA49193FA4CF3BA5A18916549E58861D54F72AE6C11E631568A5A3A179FF436EEBB9FAC3943BB748FA49DF0A4AD2E6D7467F17250764749845524D02929553B83AD1A5A1653B6769FB947BD3E43D248EAB3EFB1E3ECF31943F5AA71EDE69913571EF052E270798B5C94AC97E112E0D4F9FF24B5FE494DCD5FF1331E4D40E4FEF63CC316C4CC0E72C72CD86BB6FF7A37D2D0EC900C745625941579A49DA4357393F46EE3355AA192DDD5536B6F28670F03FA4AA6719EFB19179
	AEACA3E7323CBBCD446E73AA1E7F86D0CB87882FE22E838DA3GG10F3GGD0CB818294G94G88G88G6DF854AC2FE22E838DA3GG10F3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC7A3GGGG
**end of data**/
}
}
