package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

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
			ivjDataIntegrityPanel = new javax.swing.JPanel();
			ivjDataIntegrityPanel.setName("DataIntegrityPanel");
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

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Point Limits");
			ivjDataIntegrityPanel.setBorder(ivjLocalBorder);
			
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
			ivjJCheckBoxReasonLow.setText("Low Limit:");
			ivjJCheckBoxReasonLow.setMaximumSize(new java.awt.Dimension(90, 27));
			ivjJCheckBoxReasonLow.setPreferredSize(new java.awt.Dimension(90, 27));
			ivjJCheckBoxReasonLow.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxReasonLow.setMinimumSize(new java.awt.Dimension(90, 27));
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
			ivjJPanelReasonability = new javax.swing.JPanel();
			ivjJPanelReasonability.setName("JPanelReasonability");
			ivjJPanelReasonability.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTextFieldHighReasonLimit = new java.awt.GridBagConstraints();
			constraintsJTextFieldHighReasonLimit.gridx = 2; constraintsJTextFieldHighReasonLimit.gridy = 1;
			constraintsJTextFieldHighReasonLimit.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldHighReasonLimit.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldHighReasonLimit.weightx = 1.0;
			constraintsJTextFieldHighReasonLimit.ipadx = 91;
			constraintsJTextFieldHighReasonLimit.insets = new java.awt.Insets(5, 4, 1, 169);
			getJPanelReasonability().add(getJTextFieldHighReasonLimit(), constraintsJTextFieldHighReasonLimit);

			java.awt.GridBagConstraints constraintsJTextFieldLowReasonLimit = new java.awt.GridBagConstraints();
			constraintsJTextFieldLowReasonLimit.gridx = 2; constraintsJTextFieldLowReasonLimit.gridy = 2;
			constraintsJTextFieldLowReasonLimit.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLowReasonLimit.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldLowReasonLimit.weightx = 1.0;
			constraintsJTextFieldLowReasonLimit.ipadx = 91;
			constraintsJTextFieldLowReasonLimit.insets = new java.awt.Insets(2, 4, 16, 169);
			getJPanelReasonability().add(getJTextFieldLowReasonLimit(), constraintsJTextFieldLowReasonLimit);

			java.awt.GridBagConstraints constraintsJCheckBoxReasonHigh = new java.awt.GridBagConstraints();
			constraintsJCheckBoxReasonHigh.gridx = 1; constraintsJCheckBoxReasonHigh.gridy = 1;
			constraintsJCheckBoxReasonHigh.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxReasonHigh.ipadx = 18;
			constraintsJCheckBoxReasonHigh.ipady = -5;
			constraintsJCheckBoxReasonHigh.insets = new java.awt.Insets(4, 20, 0, 3);
			getJPanelReasonability().add(getJCheckBoxReasonHigh(), constraintsJCheckBoxReasonHigh);

			java.awt.GridBagConstraints constraintsJCheckBoxReasonLow = new java.awt.GridBagConstraints();
			constraintsJCheckBoxReasonLow.gridx = 1; constraintsJCheckBoxReasonLow.gridy = 2;
			constraintsJCheckBoxReasonLow.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxReasonLow.ipadx = 18;
			constraintsJCheckBoxReasonLow.ipady = -5;
			constraintsJCheckBoxReasonLow.insets = new java.awt.Insets(1, 20, 15, 3);
			getJPanelReasonability().add(getJCheckBoxReasonLow(), constraintsJCheckBoxReasonLow);
			// user code begin {1}

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Reasonability Limits");
			ivjJPanelReasonability.setBorder(ivjLocalBorder1);
			
			
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
			com.cannontech.clientutils.CTILogger.error( n2.getMessage(), n2 );
		}
	
		try
		{
			limit1Low = new Double( getLimit1LowTextField().getText() );
		}
		catch( NumberFormatException n3 )
		{
			com.cannontech.clientutils.CTILogger.error( n3.getMessage(), n3 );
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
			com.cannontech.clientutils.CTILogger.error( n4.getMessage(), n4 );
		}

		try
		{
			limit2Low = new Double( getLimit2LowTextField().getText() );
		}
		catch( NumberFormatException n5 )
		{
			com.cannontech.clientutils.CTILogger.error( n5.getMessage(), n5 );
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
		com.cannontech.clientutils.CTILogger.error( n5.getMessage(), n5 );
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
		com.cannontech.clientutils.CTILogger.error( n5.getMessage(), n5 );
	}

	return point;
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
		constraintsDataIntegrityPanel.ipady = 59;
		constraintsDataIntegrityPanel.insets = new java.awt.Insets(6, 4, 6, 8);
		add(getDataIntegrityPanel(), constraintsDataIntegrityPanel);

		java.awt.GridBagConstraints constraintsJPanelReasonability = new java.awt.GridBagConstraints();
		constraintsJPanelReasonability.gridx = 1; constraintsJPanelReasonability.gridy = 2;
		constraintsJPanelReasonability.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelReasonability.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelReasonability.weightx = 1.0;
		constraintsJPanelReasonability.weighty = 1.0;
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
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
				com.cannontech.clientutils.CTILogger.info("****** UNKNOWN LimitNumber( " + pLimit.getLimitNumber().intValue() + ") found in database table PointLimits for pointid = " + pLimit.getPointID().intValue() + " ******");
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
	D0CB838494G88G88G19CCA2AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8DDCD4571937F957C65FB766DD531806E6EDFE565816D41AB5AD5B18ACCDED566C520D3BA5DB523A3B3CA9E9ECCB7B33A97B566D5A54444B170072A59F028AC894896AB0B0B8FC0A4A178614C494D4B4C498198B0C8EB3634CC5474444FD4EB977B9774E1DBB83581ACD797D9E6FBD7FF31E0F731C6F0FBB8A793B9625A4A4668BC2C20AA07FB5A488426CDB02107A9F9766A216A6C4268A2A3F3700
	368A4F55A44333886242BF0ACC4D91AEE424C158G61F8FBE4EAA13C678917B2165B61A5001E7990A74DF9BDBE389E3B99752452F074018CF8E68155G568F34C8A07A579EA812F1AF043364F7C2890902B018C0523CDAD82442574869EAA02E93C8C8A4E9D28D45F27C9240C81A88F8C6A53034F170CC2756FD2E39D1CEB778C028306E9F8D0919C417CABED2D7A263354B4FB2A11CBAA2C91092DB615952FBF56C4808F342B8E2F1B81C8EC99C99B7DAAF0BD61B64F49B65074BE9F3C84D4D3D6451E31B30C947
	A85EEBF1087698EAF7FAD19A1589F6C3F8BC09E0779491DF1541FBA3D0EF925A7EE2E60BEC5B022B21C26CE33B825A9E1644EC0ECAD2ECDFED9FCA1915CC645798B2E43E9C88F781B1DB3C8634E5E5B25A72E296C93B814279A91129069444C332895E8961C1A0A985EDBC7CB65AB87051D3027000ABB1BB000D73D218ED61A90A0DBDC3C3A95BE9FDD9DFC74ABC9E6272B7C526D68275G590166816D247574DB667D70EC3BACF5B8A7A61C0EB6172B5DEEF53F6B340AF6F8EFEE069899BBA30EB85DD6E89FD4F6
	D611C1479E24A51BFC6BA1A116377382561D30B3CF8871C72FA5266864AD7CA5E362F2D9462ACDCA5E4AD2095F76EDA7FEDB8BB861ABA0F194EF2138B5D541D9D94D21F50104C3A02EB38D7DBC10053674A4AF96F2CC2793D3F5EC89F96EF8E2FC9A33A1ADDD31057978DF9A080F4D0077820D834D82DA8434A29DFD7C067960D4BE6E063A6FF6D86CFD1317DD963754BEE933DBC5376C6BFAC3B0DF6B91636B1CB19F73CB68C1F59945FC2BD757AFB121F509B132BF56297C42F4FF9CD0F77EE67F3A11454B7098
	ED93151BD999A6F17C94459BD5B8AB5B9F5336C542DD9097864452376690BCE70B360E74D13CF30B360EBC1A0DF5448BF1659911292CFCDDCD24CF0C0270DA20644C4854E220962081A0FBA616F1D7DD75B45BD12F2AFC99EFC27DB6F80ADE49ED69F6F8A40BE3C4BCE7F15BAC176D22C71053EC2EA7FE3D6C4317DDCF6A5E8868F8C7F408EE0BE4F3BAC03AE5C2044A64E1E90A6A3712B4EE1146765BA6C4D0B06192D0EED9FD9EBC6D960FF456E535C822120245D75613BAF15DF28EA402GF80F48A47E5ACB7B
	5CF9709E1A093E7AD2B341042C485422AC744BE3753970BC8915DB34B7B7F75AC608999677CD2C23555BAB61E90074E1C006ECF88299F2C0AED079D6888319F3A3D363F2315D3EB6007533B3739B026125D33A75135042BC39FF4C53564FF62AB786708DC04579D026C003C037009682C5G45GED484771777E22C5B3ECCF0108648B5FC95EE87E33D91E43367A378F6A1B9CDF1C788A5F09EFFC5B7E72BF192D592A77BC392D11F24824FA7C53137EFC0B86CBD53D97285ED93D5952C25ADEF1810C55C08385D8
	F7DE2EAFE3ED2A5D629131951DF2C383902DCACDE2E97F3D0158A9DE979DD22768929DD6F87148F578278D69CA7B9AF5723A9FC7F1525FF6C08370F07C35062D8C87996A560A713F237196C9F25BAECFCAE21FA8C9B64718427FA63545635315FB643CE69EA6F9F5C19E678205703C4EE94CD335139E27736A242B1F74GF2FBDBC06795E110FEE58156471359E4FE984743E7E9D8097FFFD983F27C1F229E680FB13F23BA126E02FCC547711CCA39DE254AF50B543DC2397E3CA557512DF27DE4BC97A8CF46B668
	BB008601E6830581C583EDGAAAF04799010D4087EDA123DC8D30F315E22CE351EAC158DEA1B5F3ACB3E7075498FFCECCD126D4578F4151D585EB08F015A765DA0D637BFEC8A5CB66FE952EF1BF31A745B667DCD415B5EC3CDFAED0F45ADECA2BEF25EF0285A72171B4878B462F4B8C45A21F096012DD3DE6BA77930B9EC52F91BF5CC147AEC9F0A50468A69FC130E018B61FDD9A10ECDB7766058D47E6163420A217D3A735B102FF4A7269531F6D9DC240CCD4C26454D240F906D6284B82A43B96910989ECA47D7
	9977A4D12CFA17C4F8AB694A4854E59B9B6F87E79E3709F62BE184FAB049C87FE503FB65954BF50B57683981AEB35272B0F61058E364F5DFD9D55511B25DE516E15466963B9D2EC914336F5C3EDA26FF324B23525F633C71A929BFA8B07D8FBAD47AEBBFBD7DF5327E7FAAD4793F76D374FFBD53FF325D2572FF4738B8F2355D69B558A4F142C87E6913A0FD473845B1A6DA29FA236506A46B6E0678BBD1EDB2B15D6FCF28F457FECA3A65FAB7FF7BC7D477896EEE627BB3224563F4D0B9F7577B26FA397C976C39
	2931836A6027E6C6035C776789BFBA73B5E68796833301B845E021BD1B11BDFAC5772853BD61DFA2EDB4FACAAB2E78AB98EA53C86E89AC7A90D85DA85B1C167395BD1B4101F755E410BFD30B1B841C2B369EA1FDF38B74593DC063C5B8FFABBFC462F17EA6CFD2BB1CF6BB9BD6A0EED59199B3EEED4FD70D83330A58BEDE960F8F1DA2BEFA0AF8E1C770F843947183D3444F1EA27E5BFB03477FF40A782C0AC0714C4FDBDBF24878AEDE1B94E119CCA648A7451B3856ED4955441D3358A715B5F4D98B198359D5F3
	EB06679D569FEF379FA673692BDCFE4961CD82D98BB319ECFE304CC2E43A5C3689D80A1045023C5F3A54205EEF4D5F9E19CA76E67B61B98E44F81B3DE45DCF6BAC6DAABD2ACA8BBCBB081C87F3F0CFF6967005GAD17E52D5B4E7C42521D5B0A6906A16C5D91A85D66DCCC978261E58153BD4B53C5C1B8AEE03A0F73B0DD5A4E4854B220561DFA69DA73B15DAD08DF90B05DBA1EAE9C62D7812D55CD77D481262BAC86D9450152DD5D467D82E93CC0A145AC5D6AE2F53A6AC2AE2F846415A86906CBD83AE5A5F47F
	086FB5D7F36C8A45FE88936F0713F18F6D5D9C759EF958439BF567010456497253CA35FB0B13C7095CCA409BCB311FF97830FA1D586671B8C7ECF4EBC72E4B470F2867088CBBF9E4B3BCE17870C85DB07976E2FBB8FD0458CA76FEE4D82E53CB8F9C967C7732D86544E5F8478D5AB976BBBB1CD6318B1AD4871FC21F3309B78474434747557B6D91598D09DB827821C74E3FDD6507B51706D218CDDFECA6FB28FA6785A354C86358244DB8A95948A613C53250F112B5A436AFDFD015A667AF2C94775F632BC93BDF
	83618DC0793BA2D359DEE0845DA3A4E98D00D901D85F7417996A3EE9A160AB01620058F836E7AB2993B39C70E51F2F5C174B4AC8B344239F9767354AFAD7BD31D8E839DD24FBA6B0B7FBF76206DD4CFF65E50A9FD73D78C662E05A30FCF6F1FBAB664F85E9C24BB0FFE98DFA790BADC33F9C4CC53EF500995EC23EEFE46B71D95FC23E7F4F793C00AD63FC334C3A7E64FCDF4DC33E8D0019F7A3DFE2261A4F3E1B71AD580D651019CF4AE10653D0F99F58160FBAC3C1DEAC57799739FABA0B76202DA5853C4EGE6
	5D03FC9FF5E873389062D66CC1DBE7975CC91DE1BAE3394EE81017D10EBA1FAB565359D90E36AE5806FC83004D67FC759D5ABC923E48F234758F5BEA61B973299976ABFD396D707C63A71038E7BFD0087908819B7377E2BE86477472513397475D178E34795BB173D9A16F634E572A7B7A953175817B7A613D2CCDCAFB35FD7D05B652BF4D87FC49DE6C6BD79D592A6A6B4FC06F8AF598E6ADFCDF65A8A997181F4861CF0E923DF4FEA260FC758B6D44974BC1E6B450DA20640A4854728A541152BA655954B1EF10
	33299DD55AF31E4E8AE53FF12042FF6F111D43D8EBC85959AB58B9CC8A471D941F2542D9D97F2E9A4BFA81442D2F40BD3B67F2347A875F765569550499DF149F1FD193DE2861E3F5E7EFA519A750F1C855C41B1B15759F1B7FFD2DC6BD7F8BFF1B6D5B2E06675A37712C786F693D81520FA7D762994A5A8EA2FF9C4233019682AD870A81DA8FD43C8F4B2D3FD5EF0C76AD37C08334321F4DFC71392DF76643877D7834BEEC59674B4772FD2281735D3F8F4B31A9774EECF8470FCFEB43F2DD9B56E4238DD15C0675
	F7E843263C29ECB06F573361A9B35A50339FED887313B5BD9B1E195206E57B756A7311F3BB756BB3760E322DCD1944D632179C49EDF5665F192D9F7B71E9EDB59C507357B339E8C3678134E1CF411D59E07263535A902A3101356B519DFA6DFA85240DBE006D7A1B9DB857DC4B6D7C799D5A39EECABB4B8DFA3E7AFDB15A50E2C09B9E59F6E7B6047A7115EA42122E8D93548FC446BCEE432542BB33416E472735E1252E8D8F0E919B4839C70C817BD843B10235D6C52676D6E1FF7AE4ABC903E3ADDD138BB84EDE
	EAA36BFE275BB6E6F3A0B6C23191271B9EA6B06C517640679CDFE957BF67D85A2EFF4E71E4FB70B348275B831D033073C293DD6F8CC37EA4A006759B89968261E5D5D89ED7EA88968D61F59C1B2458CAF95E48308D55839AFF4F14088EFCDFD725D74685B5FF2C8E75B9E40AA62E0446FFFFDBB6752B47A3FA68GC9D60BF25CAF2482C167AC68BD4B04A860BE6B6FF35265B447A4E747C1817B515F6EA37D02CEBF2A3A2776139A2C0B79C6DC03FEF3BF1957F8A1BC9FA854087EFEA1A318AFD698FD7D97AD0763
	6039C19317D38D73BD2046EA85174F31BB825757B7BA746BEBD2A7315F631CF40F085ABA3939330847A9338D3244A2AE14775AA83FE4F10F09FEF5FA8765E7F1017877F4862A772CFCA2362A4B474C6B562906E07E6C29767559309C1660B9D7933790424B00D62AF0265B152B56DD4FF57FB2BB186E559A79EB6530D083EB1A9A5F38CA8837G7556E8F5D76729F5B7F05D9166E03A2D9A79DEB9BC8F1E0BB4F161900E840A71537DEA5EE615EED3AD6ABE122927BBDE66CFB631E711C911E70077D620FE132F6E
	F1885F821A8F34C40EE33A3F1A6F237BA06A7EEE2E1E6E65B26FAA79992B52338E5E53EAE10EDD6B2B3B86421DC083C076DA356E0B85EA1F1B849CF39E5D01635E2CDAEC67AB5AC9BBDF8E61A82038DAEC67CFF5843B23A79C04BAF050579E263BE41BDAF7ADAF6FB70B03492B84D9AD3A72DE7411D7476539FD6C6B15F92D72532B112548FB28D0AD2F114B8B9953AB17F9321CC5BA72E85D83BCD293978361F88D467A8853B132F67472F09D8D5F6436F5D511315BEE39691C04C1FE40CC4AE728CABD2FCB864C
	G54EAE672D99F7F648D32F7B96A36CCC017536792C784F39D5DE3B6A26FA2C89F8E948D348EA83F8E77CE84016C6F312DC9C99CF35B241B72BEBF0B3FE0A0E958E841B6A4AC17EDF6C8A57BED0D4F58F88B644E2FD3723E24CE5FEF4BF570D50045EAF036760E27F71A57553135F78947D32B685D4BFA85E7EB7265F46CA261DB90B75E00730CDF53F5EDFFA33417C644FED6CF30D90D6C1E84439268BAF081E04BF83A7B683AECA50423B97678G41629A19FE06BDCF318D0065B7E13A1359B83F296458358634
	454B311EECFFDBC6325116F9BC5D658634E591476CA6FFDB7E4204360473F40B6B5116C80EED49C1DB0A1A91FBD647AFAF5523AD861E6EC6865A524A315F1C7537E553B934251F271B1701360CF36CE5B35A321CE377E5785B32B883EDD94553459CC4DBE2B976B5233FAD4F9A5116F5BC5D174CE8CB5AA14406695AF2G42639CDB1709765D6258EF295E79908E635899AAEF45A135DE1799ED0E61693E4B7D974F31ED54D749ADD09EAD0845E7204D659C4B24729ADB587EA1438CBC9F83BC5DAB39180FD00EDD
	4B43FCC4F06C01A64CC79447FE59641F8F219973310627BB170B79D84F31FBAF7A67632FAFE2BEB28EA3F64B641F0F87CD180F3243B8A6BD36C35D673BG1FFDD869AFC28E7B77A92C1FB0F7107EA06CB04BC3BA475BA89E2942D97F7177F4CCA461B59057F5846D7C678E34A90CE323F4BD3B8142B9C791ABA841F4659C7BC9BB2613B8763F293C39908E6158ED6A47B09266581ADC5EE6B9768CDDBF9635C21F520A582EF10219A15C43B123C057DB903E45311473341CA13C08E33F2EC19D619C3BCD5BFBF29B
	4CBD5AF00F5AB46C334FC8B6211B1B5FF15B2C6D16319EBAF23139466768BA0F7835268D679A5F227D75B804E7810534615DB4EF851E4505FDF53150B32BD7775B17587E0644E5ED2C0C235B14FD6A612121942673B7C65419538E6556CE4FBF2A55FB6EE4CCB3A5323D8A96FF4AA71E4C61778A6A78FBFC62093D8DC96A78E7FD62090F76497CACEFAF6C453CEDF8E119E07F1BA3813F6B195FAE5F89171FA1E77740BCE6B8C56EBFE9FBD88BF1C287164F11C384AB07F0A3473E53026DFA10E3AF98303D3AB8B6
	D40D65BD1BE3B1B730EDC6F7E01B7B1D014C1162A0ACF4C2BB86EA849AG728205G0583C5814583E59C03BA8F5489B4FC8CE5E4D69199DE88079CE3F7853C7299BFB93FAF134F5D17C35CEA2075C0C547C18F5040F114716C7ED8F8929DEA1FC52942C4B71F1749F8E3277E7C8329DC6691B469C22660C3AA1AA29D711BBA5CF94CB70CFEA46F0A3FFECF73DA20111527FAAF5454D92C1B41EC417B405A7D07695E35CD1DA2DEFB57FA27A62C3E67DBA2607CB21A5EFD1930F8ED93139374926B7263F48C24632C
	975EE79F47396A867A2D490853BEB96170487CBFB59019AE37B8AA3A5D2215DD0315E56C2773248D701E768E4A18FB025C1D99F5BAA416A61C4E2F4C905F4A532C98A0FAC487390AE315757C164E31E1022D3173B5BA5F1300F796D06CBBF4FFB13BF38ABFA5497EA44FB060D981D4A017CDB43CBFAF30BE0269F804662332ABB2558C4430F9546EF188CB9C3B0F26DB8461B00E0551FEA39642EB3B503E276B2732AFC5E5DFD1372FFD06EE2DFDE6BAFFEC843C339B753EC9478EAF04E7F3EC911D43AC03F08447
	2A69DCA79E429B3A513E47B2025917AE603A07D16589DF7BDACEE86DCB2DA3BA3AG6FBF01FADF20364C0570820E2530B99B04D7F16C97F4CC91CEC25FF6926D8BB7CFE5DF120A9ACF7A5A57F3D2310F552D9B1924CECA22D72E137733F9A724331EC4BBCA3288F68B42F3CFE2BD5DECA6F51F9DF81363B60B434A733C470CB242F9FA9BCDFF59C9AE8176DAEC305ED352FF1126DF85E9D7F31DF154C749BD705E03B22A4EED226D55CD771CDBFB487741AF7688DC4EF6EA7BEC4067F31EF2B3693B461DEE5B0750
	6EAC76B63BED4CC16E81894C1F9DC17D19A47B8A6B638AD5FB21F3D250E372A96F4DF4EE9B89F8F48F6665910A2D0570FA0EBD5304694A5FC5EC9F1D5B36C0380BE3ECCEA6C1F89647D653F96CB288C73C0B75626B26296A851639DEBE62C08E5E1D1167F8F43FE69D60E96F61985DC4671B868837F2EC104FB72D6F6198525CC16EA306C3B886C8B885636CA90CFB074611BBEC2D725DB3F22F0C452DEA1F69DEB7119FACBE5CCF7F4FE62CC3BBE66955917C3E0415454FBA0215053A7FA0548A3675836D116346
	617DA91FB67AF131FACCE0F5E0C1B14EB725D3D8878CA5D369D56B1E2B513BD0251769C86BC09D212730BDAD6A709D433ECD53C4C0FCA4CF7305DC6CD3D89BDD772E40FD73075C690CCFC87DAA9B099D6B611D4C117FEB63460D729E5D182F2E42D38277DB7D387E787E9EDD2BA47742FBAF5AFCCC88BC16DF226B1896C85B4553971F7775C3ADDD57C890BF2B974BA3B9E81B4431AED0DE1775EA737A05FA7DF94093F43C0B0074113DD84F7F0F5F9D1429082DCB5FB6527D2EDE36AECD60F88D4505538A4E562B
	CFD761B77F999067BA0D67092F1FA27BFF51900E87CABB83ED902895E8708C1EAFBEF32CC618623EC63F5355A3DE976D2A63BE798C58F8E75FF1FFAF9557C4614FFCDDD8FE6F1B89016EDE3A4E303EE53E7C2C695FC3F1163F1053A4FFABA0AE86C86803F583108128338F7317F85C4072C72E0936C912E5E41C5EC0454C11CB218ACCB6CF65FDEC93996758D809FB318DCD381FBAG72DDC021C091FD6C4E9D63BBFB48FF8F57FE887922A1AD5943DD8BCF211F71317A33162E5B0AGAB6FC76C8FF45DB68CE1FB
	BF56676B55B3E93BA14097564F6AB39DF3601DB44E9B3CDDED7152F19F70557D1807B9BB485945099E4BE551CE2E41E1DB7E6EC4BA47FB1CB7F89BE7247325970F4F65716AAF21107F1DB49EDF2B973FE68717DF2BA75FA1E5F1797CFA089C772B0FF9DC2DB6EE32A61F67274DEEF1CFF4CE320F5565EFF99E5D1B45637B449127436A2965F2CF929F7539EC8EEA89472FB565694B146363D3B235B2399F9B69B76DDC26005F9E1C2CD26FB1A41FE5752672AC5EAB7C3BBA3D78969E7F1BA63D78DE9E7F4DC3FA71
	D69E7F03775471DEB9BE0C479F2B5063DF416367EF510B0F667125E6BD79F170E463491CBE017BE8F5FD85BC9D620DB2FE87325386CD177C88C0E52D495CE0EEBCD8EFEEB01455566C437EE6518D75BD48464DAD816FC1EEB84B7A191CF372D9601CBD729C77BF7A959BE6E517863641EC16F5B33E5FDCD078968E6C55729D07412BF237476270F8C437EDF437212C46D0E6B213BDA4461F7B3E425FBAAD3D3B64B6C757AD74AF7E28BF06726B8649BB7353AB9958AF97AFFDCC687C711B89B88EEA7D54A57BA764
	1C328FD774656194A6B36E8345662359B3A9AF463FE5C861BF5FB0FDDFBFB522708D19E66E6BFF33AA7C8F575F292F192CB7C4454765E30A5CA7E7D4FF997F19F1055F1EB1FDFFBCF1C5617B6959197BA3662A42FF4F3474867647C63B620F8A07A277130C197B235B29703FF8F07A7EA8F4ABFC0F98E76E0FE60F427FF9739F670F8FA4459F77CDAAF2DF4BA4EDF1B79D304ACC3BF91AA72EABE9620265393E91F4576F4334BDE6FDF9673C0A3C70E0E54B65B13E6FFC2870EDBE37DB5F07D61B456E9CD3B91075
	AFBFF8DF5D8F67BCD2110873D8ED7F92A577ABB12A7E05695FFBCB511F982C8CB4F677FD2C787B7AA70A0C2BF9AA7F54AA7E7962EDA54D3DCD5377772BCD7A72A284C55E495C695BDD142470FDF8F13A7EE63C0712955EEB26696B3C1422EADFBB7C6741F17C2EE94EF9B68F2E04E74BF9F6AF0F7175967B4F03470A35F7997A01E77C3CFC67054EEFBDC1574F5A755AAC60DDF0DE195F061ED756C64C8EF38ED9AB847C9A1B27FB1B7ED6DD202F25D91A7423F4EE677F1D885EAD2173DC7F2F06E7BAA6BF30D971
	7F52E2125FD10B5DC38614407DC9789605E7D7071A07457FD516926F491DD9BDFAB6DB31EDED0EA2E70F9024CEB5010C5A86AEA3BBD761FB7D5C74792E67ABFC2F544C4C6EAF96A8F6C7ECD36404E7CDC7BFDB63D699891F7CFBE72A776FD67D31F7C2039153317A2E6A106FCD76863EEB3939D77F2EE5CE2F7E5D60ED3D41FF832534B770EF206C6DD57F868ADB5F4FABA4059F8EEDBA8AE89DD05AC0E4EA71G2E6D1F6DA575017C02197F3A7E18F50C5C75A43FDAA6715FD4B99DB82F2127277E2D33A7CF1306
	F959A9C94E89A29F7127CE1372121CAE8AB2AC62B471119BBA8709A5157167CE1336EB97C7A94C76EF061A7037629AA1EF2E81BCEB7FC58919FB68FDDB2AFCD45AEF136CE23B5CBD4B777CBEE866FB0683F876FE9F7D6D16D8882F874A39403F39BB04FBC715008DDF40EF85E28E210C108BA8E3B37DBD2CA88847818997F1FD7676F625EDC5980C015B164FFC0E3F7376FAE207A2273F3AAC301C037524214308B53DFE957B4889D284322E79450A2E0776CFC717FC1FFAB440FDEA3A22E73E7BDF746E83D96797
	DDC4FF3EDE0758AD0ED550F2A2F5A716E33FBC04D8517BD88FBE21EB86DE8F2E5A470CD7C6C84BB30E12210AFC94235AB4E0F2B61DCA1373577BBE4A3ED5013267F26C9F36A03602E3E74D483B7EFD5CCBBCCC4F98EA86A3D3FB8679F700FD8760F94EF2457FCBECGFB5DCE176816EEB2147E62015C2EEF1612B15E8EF2668205812D84DA8D948F147CC1E4EA9110812893E8904885B49BA884A894A882A86A8334634341FDC16C10AF27504E40EDE15FCFB0BBFEC22A8A7E5661C7FB8349005E64849475294BD7
	4491295FC296F32C6D646C44F63B86ECA93E047E9B5809ED277F92361DFBE95AF990DE829441535E4F4F9022F95A3FADA6E9B306A2D34B01DA00E6D64FCF3C2564EBC1E160BE6F2F8A276E7316949279A274D57DB7DD72DD77478B69F7A723239E11772DCF9466F02C5B536B74584826979B47F6B6CF35CF5CBF24775D453EA979345F5AB03E4BD372E9096D2FEF24FD24EBC83B6F1EC67B48DBC35AFD77871A316F1C8BF1EB077877CC076E6CF7D1C36F106F15997331FCFDC27B735EE1EDFE67D0FCF0D81B5F1C
	964C6FB8442D9A467C4E511D2754D8D819F6DA744F8787G37EB62E6C1F801C52FAEEC2C535391AA270D8820A38A70B51A3875904E382C276300CF3DA913531865E757E5FF9D0C6FB59F7F8F4A69DC72F3F6C03EAD270215DBC8C03EFBAA54FCE1F23A9572B3BAA05F2AADEA3EB8B95D867919B392086F22B9181D65BAFC2CCFE8AFA16FFCBE4806GB92E3B044C6D486F57415C5D76A15DA26F03040EB1797774CA8377DB8BCA276E373ED42ABFD7FB2C54FF2E96DA2ABFD7FB22D4BDD713FF173A140C49E3B026
	8C70F44BB9F601E35F21B289F65EE61546B97EF754AE02F709C4A1625F27F65D2058000C7D338A3BA0E3BFACA5FDAFE4F95C493F6FDBDDCA4FC1EDF6BB8B4750B4B687F4455C9F2FD09BEDAE0B4A6E1FF36C82AF6BEDBB6F2C2FAE1F12CF3F2F7EE0CABE7D3E6BF5BA1EB60EE87B2EA4BAF6F60EE87B2EF9BB316F6A053870916C3BBE9F743B14407A2BCB083CA22BD6FFA3454B2DDA7D5FABC67DB59037G085D01DB1117C94B4AEEF3089D6C971339EFD28E897CBDCB757EF0735DDFFBAE7B94F44CEED172F43F
	6AFDDB4B5D57DDFDD7F5485F0750F540FAA86BB4916F6A349871FEEEB14786CA70FEEE8D472E1D407B741D9CDB406F6C8EF02C02FF97904A31376AD1EF84471655235EA80EDD4A627778B9763DBAFE0F1FE377713B619923087DCDBD6A9D64D8BBDD4F38A0BC0BE3DFCF24BAA03C04E359C9A8EF9947D2687937B0866FE3087D0A26AB073019E32712113715E33BCFA1B61FE3A1F42D938B61F80EBDCD6F4CA70FC726660FA3D6BA4E3FB360582BDB10574531AF5FC65E599CCB9438DE0EDD635FDF440EE39FA394
	ABF5BBDD757E791DF73F1EAF7D94F45415A8F9BA22FA7F07623B2FBB762E6A106F83D7795E4F7C91ED57B5B6D81B59702E06C85BF5AF0487B9F68FED57F3A13C00E30F52F63D82422BECF827E3E950BB9D6ABB03FA7786B758146FD7180ED334CFC83B82757C8A6ABDC15BEBAB04FBB8762F949B06301DE3F3E81F908261A59C7B956D9322A09CF385EDDEDCB3154DA9C1EC4E382A3579E5EACB916065D7718C61C2336F7DAEA76DDFFAA1FE102779DE226F5D338932A5A81FA5D4A4CDE5237AAE315646F9D7597E
	8B46678945AD1A7BE6344F8A01F4CB38BD89493E764C5F2358B31A9C4C9E3C3F9848E75132BDEFEC5C381149EBBAB555DD43E072E2C11E8D1EEF737C7DF505EF7E1E27FD68DAC8371E67AFCAF3576FDB65CA7E6A462772F7EA90FFD75A190E94DEA7B2E8BDB6835EEA473D469F6DD574ED972672A75E9956536732EB7DB9B768BD6414A9645D32AB7EE436D612D759378DC6B2A75FCDCE0BF6E37C49CAA57E5D12A0E792350D06B213F2BE675827700D1E18BE5FCB87943E3955B3BBCBD9CF2649727BCE23A2672B
	F55357FF24DA61DBDACF7C393B47F9C3B9A33554A871FD414EE8EA6BFC644EB6A9FC5F8EEA0FAF9F2BCB0C6F768C4F16FEDF217863BFD565688EF6162E51BF28AA0FC793E7267F362ABCFE2EAA0F171326A327164BC92FD16498CC4A7B5C030A4CEF0C4D4C3625E6C5CEEE03A267215BB3332D35D111B3C2FE6AD8E07F07598F8FAC07869976EBA145639632392CEE3A600D3DFE30B969554712920564EEE1935BG68961249AA3EDBC8210B7CEEA1C9F23A3C64C7429355A489DBC87BAC23C73E4D123099EA279C
	GC5397E855A76FF7F7547E1C31D7909C2A2689A971970149BE445CF45675F064E45F3CD78C4A03DA65971B00024CC1F6CBF7F653A1F2FFA791CA7C95854ADECA73DEF1F68261BA22215FC17AF7F8FB74705F47ABF0E11BB47109F8FFD24F8241BF631C7489FBDC3FEDD58A30956B65CEE71C8C2817D2140EAF2D2D45DE5710CCBE40B64D00F106CA07F970F104B226D9647D8B55BFBB9C42DCEE89BA64E263D1EG7666D1FB0994A31712773759FB5FBA31E6F91210F8DC58ECF7DA2C47ADA312535DAD24CB63EE
	51B36E34DB3D2E232DD4D09A7D30101CD0A701381DBC7BA433C6F2644E0D3E6A7D523C4C384C63D09EDE37D0CDD312414648D3D8BB46ADEED0A53A3BA572FF8BB95D65BC6E1ED229621F5EA99CCC949264CC925727FBDD1230E954B2E1335F1404F4FE410716CBA6A0129BA072530AE0DC1946B80C62BA0C8FFE3F662147DF3FC8F5A82C12300BF993D82B47C467C4F55B68A82C6939GF0AD08AF6162C99A23A6CD2E77010293AFED3C8CD521D3C81A2829217F1A683F3564DF93C5CC94B15502FF797F54277F27
	0D77E21EE97D612D0754G63F1A8C4695FFE7D7D457F5E923F96F2F7DCC821BF598865102324FAEFF26232683EB76C2112AD8BE26FA34564A23545CB64EE75EA2ABA89E173CF9658F1C52212C627F110D02ADF95A468967F3AC460ECF50992A017264E277F964E58BF04EBBB655D4933D59EA8DB092AECA57D59E54B982C3488C28EF47E7CCA2BB1D03690A159CA1004D479CA9CCFCD8E1FBBF5C2B34D60A6B61A5D1D5CF5F8F5AC44BFB21DA0338F01672E764FABF76F854BDDB3D09537107C11D9B7C97BB8D08A
	C7F3D4E8621D653173AE66F1406B13ABD2A3E97748F3CE7666AF6A04BED33930A83948D665A2C1AF9724637069BC00AFE11C7313F591C1097D1B822449D42711042D2CF6632795E5354DE4C493C70C7ED1A6A5AA19CC6E3882D2CD0AF5C21F2986B12938B9D156CB2ED8A5795AAD89C9B0D37541366B04BED3B91B2564ACC156CBAAD1721F426E3BD97167AAF6EF1775123F9415254C2E0C7FA9CBDB6570DFAA161648DEA69ECE63688E1504144F1E279FD66C477F3A1164E173E74F530FAA1662C75E445319BEDD
	9C7EEDE6F761775DD65F05DF7F3912005FA42D7F081D37E65C527EB67B5374379D3AG375E423BD069A6B2C6863A4754B7E1315BA50B7DEA1B43CA6EB4355F14C465FE6633A6A25F03E964C896773C0958FDC545735FD0CB87889EBB1CEB64A4GG2CFCGGD0CB818294G94G88G88G19CCA2AE9EBB1CEB64A4GG2CFCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9EA5GGGG
**end of data**/
}

}
