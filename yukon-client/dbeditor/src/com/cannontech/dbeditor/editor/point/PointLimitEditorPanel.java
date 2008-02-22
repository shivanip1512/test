package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import java.util.HashMap;
import java.util.Iterator;

import com.cannontech.common.gui.util.DataInputPanelListener;
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
	private StatusPointLimitEditorPanel panel = null;
	
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
	
	panel.actionPerformed(e);
	
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
	panel.caretUpdate(e);
	
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

			ivjLimit1HighTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-1000000.999999, 1000000.999999) );
			
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

			ivjLimit1LowTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-1000000.999999, 1000000.999999) );
			
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
			
			ivjLimit2HighTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-1000000.999999, 1000000.999999) );
			
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

			ivjLimit2LowTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-1000000.999999, 1000000.999999) );

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
	panel.getValue(val);
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
	//java.util.Vector limits = new java.util.Vector(2);
	HashMap limitHash = new HashMap(8);
	
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
			limit1Low = new Double(CtiUtilities.INVALID_MIN_DOUBLE);
		}

		limitHash.put( new Integer(1),
			new com.cannontech.database.db.point.PointLimit( point.getPoint().getPointID(), 
				new Integer(1), limit1High, limit1Low, (Integer)alarmDuration1) );
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
			limit2Low = new Double(CtiUtilities.INVALID_MIN_DOUBLE);
		}
		
		limitHash.put( new Integer(2),
			new com.cannontech.database.db.point.PointLimit( point.getPoint().getPointID(), 
				new Integer(2), limit2High, limit2Low, (Integer)alarmDuration2 ) );			
	}

	point.setPointLimitsMap( limitHash );


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
		panel = new StatusPointLimitEditorPanel();
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

		java.awt.GridBagConstraints constraintsStaleDataPanel = new java.awt.GridBagConstraints();
		constraintsStaleDataPanel.gridx = 1; constraintsDataIntegrityPanel.gridy = 2;
		constraintsStaleDataPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsStaleDataPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStaleDataPanel.weightx = 1.0;
		constraintsStaleDataPanel.weighty = 1.0;
		constraintsStaleDataPanel.ipadx = 39;
		constraintsStaleDataPanel.ipady = 59;
		constraintsStaleDataPanel.insets = new java.awt.Insets(6, 4, 6, 8);
		add(panel.getJPanelStaleData(), constraintsStaleDataPanel);
		
		java.awt.GridBagConstraints constraintsJPanelReasonability = new java.awt.GridBagConstraints();
		constraintsJPanelReasonability.gridx = 1; constraintsJPanelReasonability.gridy = 3;
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
	
	if (!panel.isInputValid()) {
		return false;
	}
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
			
		}
		catch( NumberFormatException e )
		{
			setErrorString("High limit 1 must have a numeric value");
			return false;
		}
	}
	
	if( getLimit1CheckBox().isSelected() )
	{
		try
		{
			low = new Double(getLimit1LowTextField().getText());
		}
		catch( NumberFormatException e )
		{
			//do nothing
			//low, unlike the high limit, can be unspecified.
		}
	}
		
	if( high.doubleValue() < low.doubleValue() )
	{
		setErrorString("High limit 1 can not be less than the low limit 1");
		return false;
	}
	
	//Check limit 2 range
	if( getLimit2CheckBox().isSelected() )
	{
		try
		{
			high = new Double(getLimit2HighTextField().getText());
		}
		catch( NumberFormatException e )
		{
			setErrorString("High limit 2 must have a numeric value");
			return false;
		}
	}
	
	if( getLimit2CheckBox().isSelected() )
	{
		try
		{
			low = new Double(getLimit2LowTextField().getText());
		}
		
		catch( NumberFormatException e )
		{
			//do nothing
			//low, unlike the high limit, can be unspecified.
		}
	}
	
	if( high.doubleValue() < low.doubleValue() )
		{
			setErrorString("High limit 2 can not be less than the low limit 2");
			return false;
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
	panel.setValue(val);
	//Consider defaultObject to be an instance of com.cannontech.database.data.point.ScalarPoint
	com.cannontech.database.data.point.ScalarPoint point = (com.cannontech.database.data.point.ScalarPoint) val;

	//java.util.Vector limits = point.getPointLimitsVector();
	Iterator limitIt = point.getPointLimitsMap().values().iterator();

	Double noSpecifiedLow = new Double(CtiUtilities.INVALID_MIN_DOUBLE);

	while( limitIt.hasNext() )
	{			
		com.cannontech.database.db.point.PointLimit pLimit =
			(com.cannontech.database.db.point.PointLimit)limitIt.next();

		//Handle Limit 1
		if( pLimit.getLimitNumber().intValue() == 1 )
		{	//Handle Limit 1
			Double highLimit = pLimit.getHighLimit();
			Double lowLimit = pLimit.getLowLimit();
			

			if( highLimit != null )
				getLimit1HighTextField().setText( highLimit.toString());

			if( lowLimit != null && !lowLimit.equals(noSpecifiedLow))
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

			if( lowLimit != null && !lowLimit.equals(noSpecifiedLow))
				getLimit2LowTextField().setText( lowLimit.toString() );

			getLimit2CheckBox().setSelected(true);

			getSpinField2().setValue( new Integer( pLimit.getLimitDuration().intValue()) );
		}
		else
			com.cannontech.clientutils.CTILogger.info("****** UNKNOWN LimitNumber( " + pLimit.getLimitNumber().intValue() + ") found in database table PointLimits for pointid = " + pLimit.getPointID().intValue() + " ******");
	}


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
	panel.valueChanged(arg1);
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

private static void getBuilderData() {

}

public void addDataInputPanelListener(DataInputPanelListener listener) {
	//Had to override this to add the Input listener to the Stale Panel as well as this panel.
	//This is because this panel is being created inside this panel and is not touched by the main list.
	panel.addDataInputPanelListener(listener);
	super.addDataInputPanelListener(listener);
}
}
