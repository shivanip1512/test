package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.PointTypes;

public class PointAnalogPhysicalSettingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener, com.klg.jclass.util.value.JCValueListener
{
	private javax.swing.JLabel ivjPointOffsetLabel = null;
	private javax.swing.JComboBox ivjTransducerTypeComboBox = null;
	private javax.swing.JLabel ivjTransducerTypeLabel = null;
	private javax.swing.JCheckBox ivjPhysicalPointOffsetCheckBox = null;
	private com.klg.jclass.field.JCSpinField ivjDeadbandSpinner = null;
	private javax.swing.JCheckBox ivjDeadbandCheckBox = null;
	private com.klg.jclass.field.JCSpinField ivjPointOffsetSpinner = null;
	private java.util.Vector usedPointOffsetsVector = null;
	private javax.swing.JPanel ivjDeadbandPanel = null;
	private javax.swing.JLabel ivjUsedPointOffsetLabel = null;
	private javax.swing.JLabel ivjDataOffsetLabel = null;
	private javax.swing.JTextField ivjDataOffsetTextField = null;
	private javax.swing.JLabel ivjMultiplierLabel = null;
	private javax.swing.JTextField ivjMultiplierTextField = null;
	private javax.swing.JPanel ivjRawValuePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointAnalogPhysicalSettingsEditorPanel() {
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
	if (e.getSource() == getTransducerTypeComboBox()) 
		connEtoC2(e);
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
	if (e.getSource() == getMultiplierTextField()) 
		connEtoC6(e);
	if (e.getSource() == getDataOffsetTextField()) 
		connEtoC7(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC2:  (TransducerTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (PhysicalPointOffsetCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if ( getPhysicalPointOffsetCheckBox().isSelected() )
		{
			getPointOffsetSpinner().setEnabled(true);
			getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
			getPointOffsetSpinner().setValue(new Integer(1));
			int temp = 2;
			while( getUsedPointOffsetLabel().getText() != "" )
			{
				getPointOffsetSpinner().setValue(new Integer(temp));
				temp++;
			}
			getPointOffsetLabel().setEnabled(true);
			getTransducerTypeLabel().setEnabled(true);
			getTransducerTypeComboBox().setSelectedItem("None");
			getTransducerTypeComboBox().setEnabled(true);
		}
		else
		{
			getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(0), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
			getPointOffsetSpinner().setValue(new Integer(0));
			getPointOffsetLabel().setEnabled(false);
			getDeadbandCheckBox().setSelected(false);
			getDeadbandSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(-1), new Integer(-1), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
			getDeadbandSpinner().setValue(new Integer(-1));
			getTransducerTypeLabel().setEnabled(false);
			getTransducerTypeComboBox().setSelectedItem("Pseudo");
			getTransducerTypeComboBox().setEnabled(false);
			getPointOffsetSpinner().setEnabled(false);
		}

		revalidate();
		repaint();
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (DeadbandCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if ( getDeadbandCheckBox().isSelected() )
		{
			getDeadbandSpinner().setEnabled(true);			
		}
		else
		{
			getDeadbandSpinner().setEnabled(false);
		}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (MultiplierTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC7:  (DataOffsetTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
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
 * Return the DataOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDataOffsetLabel() {
	if (ivjDataOffsetLabel == null) {
		try {
			ivjDataOffsetLabel = new javax.swing.JLabel();
			ivjDataOffsetLabel.setName("DataOffsetLabel");
			ivjDataOffsetLabel.setText("Data Offset:");
			ivjDataOffsetLabel.setMaximumSize(new java.awt.Dimension(90, 16));
			ivjDataOffsetLabel.setPreferredSize(new java.awt.Dimension(80, 16));
			ivjDataOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDataOffsetLabel.setMinimumSize(new java.awt.Dimension(80, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataOffsetLabel;
}
/**
 * Return the DataOffsetTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDataOffsetTextField() {
	if (ivjDataOffsetTextField == null) {
		try {
			ivjDataOffsetTextField = new javax.swing.JTextField();
			ivjDataOffsetTextField.setName("DataOffsetTextField");
			ivjDataOffsetTextField.setMinimumSize(new java.awt.Dimension(55, 21));
			ivjDataOffsetTextField.setColumns(0);
			// user code begin {1}

			ivjDataOffsetTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.9999999, 999999.999999999, 9) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataOffsetTextField;
}
/**
 * Return the DeadbandCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getDeadbandCheckBox() {
	if (ivjDeadbandCheckBox == null) {
		try {
			ivjDeadbandCheckBox = new javax.swing.JCheckBox();
			ivjDeadbandCheckBox.setName("DeadbandCheckBox");
			ivjDeadbandCheckBox.setSelected(true);
			ivjDeadbandCheckBox.setText("Enable");
			ivjDeadbandCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeadbandCheckBox;
}
/**
 * Return the DeadbandPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDeadbandPanel() {
	if (ivjDeadbandPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Deadband");
			ivjDeadbandPanel = new javax.swing.JPanel();
			ivjDeadbandPanel.setName("DeadbandPanel");
			ivjDeadbandPanel.setBorder(ivjLocalBorder);
			ivjDeadbandPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDeadbandCheckBox = new java.awt.GridBagConstraints();
			constraintsDeadbandCheckBox.gridx = 1; constraintsDeadbandCheckBox.gridy = 1;
			constraintsDeadbandCheckBox.insets = new java.awt.Insets(3, 14, 14, 4);
			getDeadbandPanel().add(getDeadbandCheckBox(), constraintsDeadbandCheckBox);

			java.awt.GridBagConstraints constraintsDeadbandSpinner = new java.awt.GridBagConstraints();
			constraintsDeadbandSpinner.gridx = 2; constraintsDeadbandSpinner.gridy = 1;
			constraintsDeadbandSpinner.ipadx = 42;
			constraintsDeadbandSpinner.insets = new java.awt.Insets(3, 4, 14, 122);
			getDeadbandPanel().add(getDeadbandSpinner(), constraintsDeadbandSpinner);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeadbandPanel;
}
/**
 * Return the DeadbandSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getDeadbandSpinner() {
	if (ivjDeadbandSpinner == null) {
		try {
			ivjDeadbandSpinner = new com.klg.jclass.field.JCSpinField();
			ivjDeadbandSpinner.setName("DeadbandSpinner");
			ivjDeadbandSpinner.setPreferredSize(new java.awt.Dimension(60, 22));
			ivjDeadbandSpinner.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeadbandSpinner.setBackground(java.awt.Color.white);
			ivjDeadbandSpinner.setMinimumSize(new java.awt.Dimension(60, 22));
			// user code begin {1}
			ivjDeadbandSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(-1), new Integer(1000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			ivjDeadbandSpinner.setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(100000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));

			//ivjDeadbandSpinner.setValue( new Integer(-1) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeadbandSpinner;
}
/**
 * Return the MultiplierLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMultiplierLabel() {
	if (ivjMultiplierLabel == null) {
		try {
			ivjMultiplierLabel = new javax.swing.JLabel();
			ivjMultiplierLabel.setName("MultiplierLabel");
			ivjMultiplierLabel.setText("Multiplier:");
			ivjMultiplierLabel.setMaximumSize(new java.awt.Dimension(70, 16));
			ivjMultiplierLabel.setPreferredSize(new java.awt.Dimension(70, 16));
			ivjMultiplierLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMultiplierLabel.setMinimumSize(new java.awt.Dimension(70, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierLabel;
}
/**
 * Return the MultiplierTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMultiplierTextField() {
	if (ivjMultiplierTextField == null) {
		try {
			ivjMultiplierTextField = new javax.swing.JTextField();
			ivjMultiplierTextField.setName("MultiplierTextField");
			ivjMultiplierTextField.setMinimumSize(new java.awt.Dimension(55, 21));
			ivjMultiplierTextField.setColumns(0);
			// user code begin {1}

			ivjMultiplierTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999999, 999999.999999999, 9) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierTextField;
}
/**
 * Return the PhysicalPointOffsetCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPhysicalPointOffsetCheckBox() {
	if (ivjPhysicalPointOffsetCheckBox == null) {
		try {
			ivjPhysicalPointOffsetCheckBox = new javax.swing.JCheckBox();
			ivjPhysicalPointOffsetCheckBox.setName("PhysicalPointOffsetCheckBox");
			ivjPhysicalPointOffsetCheckBox.setSelected(true);
			ivjPhysicalPointOffsetCheckBox.setText("Physical Point Offset");
			ivjPhysicalPointOffsetCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalPointOffsetCheckBox;
}
/**
 * Return the PointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointOffsetLabel() {
	if (ivjPointOffsetLabel == null) {
		try {
			ivjPointOffsetLabel = new javax.swing.JLabel();
			ivjPointOffsetLabel.setName("PointOffsetLabel");
			ivjPointOffsetLabel.setText("Point Offset:");
			ivjPointOffsetLabel.setMaximumSize(new java.awt.Dimension(78, 16));
			ivjPointOffsetLabel.setPreferredSize(new java.awt.Dimension(78, 16));
			ivjPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointOffsetLabel.setMinimumSize(new java.awt.Dimension(78, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointOffsetLabel;
}
/**
 * Return the PointOffsetField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPointOffsetSpinner() {
	if (ivjPointOffsetSpinner == null) {
		try {
			ivjPointOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjPointOffsetSpinner.setName("PointOffsetSpinner");
			ivjPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(60, 22));
			ivjPointOffsetSpinner.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointOffsetSpinner.setBackground(java.awt.Color.white);
			ivjPointOffsetSpinner.setMinimumSize(new java.awt.Dimension(60, 22));
			// user code begin {1}
			ivjPointOffsetSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointOffsetSpinner;
}
/**
 * Return the RawValuePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRawValuePanel() {
	if (ivjRawValuePanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder1.setTitle("Raw Value");
			ivjRawValuePanel = new javax.swing.JPanel();
			ivjRawValuePanel.setName("RawValuePanel");
			ivjRawValuePanel.setPreferredSize(new java.awt.Dimension(180, 88));
			ivjRawValuePanel.setBorder(ivjLocalBorder1);
			ivjRawValuePanel.setLayout(new java.awt.GridBagLayout());
			ivjRawValuePanel.setMinimumSize(new java.awt.Dimension(180, 88));

			java.awt.GridBagConstraints constraintsMultiplierLabel = new java.awt.GridBagConstraints();
			constraintsMultiplierLabel.gridx = 1; constraintsMultiplierLabel.gridy = 1;
			constraintsMultiplierLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMultiplierLabel.ipadx = 10;
			constraintsMultiplierLabel.insets = new java.awt.Insets(3, 12, 7, 2);
			getRawValuePanel().add(getMultiplierLabel(), constraintsMultiplierLabel);

			java.awt.GridBagConstraints constraintsMultiplierTextField = new java.awt.GridBagConstraints();
			constraintsMultiplierTextField.gridx = 2; constraintsMultiplierTextField.gridy = 1;
			constraintsMultiplierTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMultiplierTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMultiplierTextField.weightx = 1.0;
			constraintsMultiplierTextField.ipadx = 108;
			constraintsMultiplierTextField.ipady = -1;
			constraintsMultiplierTextField.insets = new java.awt.Insets(1, 3, 5, 49);
			getRawValuePanel().add(getMultiplierTextField(), constraintsMultiplierTextField);

			java.awt.GridBagConstraints constraintsDataOffsetLabel = new java.awt.GridBagConstraints();
			constraintsDataOffsetLabel.gridx = 1; constraintsDataOffsetLabel.gridy = 2;
			constraintsDataOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDataOffsetLabel.insets = new java.awt.Insets(7, 12, 17, 2);
			getRawValuePanel().add(getDataOffsetLabel(), constraintsDataOffsetLabel);

			java.awt.GridBagConstraints constraintsDataOffsetTextField = new java.awt.GridBagConstraints();
			constraintsDataOffsetTextField.gridx = 2; constraintsDataOffsetTextField.gridy = 2;
			constraintsDataOffsetTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDataOffsetTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDataOffsetTextField.weightx = 1.0;
			constraintsDataOffsetTextField.ipadx = 108;
			constraintsDataOffsetTextField.ipady = -1;
			constraintsDataOffsetTextField.insets = new java.awt.Insets(5, 3, 15, 49);
			getRawValuePanel().add(getDataOffsetTextField(), constraintsDataOffsetTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawValuePanel;
}
/**
 * Return the TransducerTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getTransducerTypeComboBox() {
	if (ivjTransducerTypeComboBox == null) {
		try {
			ivjTransducerTypeComboBox = new javax.swing.JComboBox();
			ivjTransducerTypeComboBox.setName("TransducerTypeComboBox");
			// user code begin {1}
			ivjTransducerTypeComboBox.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTransducerTypeComboBox;
}
/**
 * Return the TransducerTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTransducerTypeLabel() {
	if (ivjTransducerTypeLabel == null) {
		try {
			ivjTransducerTypeLabel = new javax.swing.JLabel();
			ivjTransducerTypeLabel.setName("TransducerTypeLabel");
			ivjTransducerTypeLabel.setText("Transducer Type:");
			ivjTransducerTypeLabel.setMaximumSize(new java.awt.Dimension(113, 16));
			ivjTransducerTypeLabel.setPreferredSize(new java.awt.Dimension(113, 16));
			ivjTransducerTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTransducerTypeLabel.setMinimumSize(new java.awt.Dimension(113, 16));
			// user code begin {1}
			ivjTransducerTypeLabel.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTransducerTypeLabel;
}
/**
 * Return the InvalidPointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUsedPointOffsetLabel() {
	if (ivjUsedPointOffsetLabel == null) {
		try {
			ivjUsedPointOffsetLabel = new javax.swing.JLabel();
			ivjUsedPointOffsetLabel.setName("UsedPointOffsetLabel");
			ivjUsedPointOffsetLabel.setText("Offset Used");
			ivjUsedPointOffsetLabel.setMaximumSize(new java.awt.Dimension(180, 20));
			ivjUsedPointOffsetLabel.setPreferredSize(new java.awt.Dimension(180, 20));
			ivjUsedPointOffsetLabel.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjUsedPointOffsetLabel.setMinimumSize(new java.awt.Dimension(180, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsedPointOffsetLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	Integer pointOffset = null;
	Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
	if( pointOffsetSpinVal instanceof Long )
		pointOffset = new Integer( ((Long)pointOffsetSpinVal).intValue() );
	else if( pointOffsetSpinVal instanceof Integer )
		pointOffset = new Integer( ((Integer)pointOffsetSpinVal).intValue() );

	Double deadband = null;
	Object deadbandSpinVal = getDeadbandSpinner().getValue();
	if( deadbandSpinVal instanceof Long )
		deadband = new Double( ((Long)deadbandSpinVal).intValue() );
	else if( deadbandSpinVal instanceof Integer )
		deadband = new Double( ((Integer)deadbandSpinVal).intValue() );
	
	String transducerType = (String) getTransducerTypeComboBox().getSelectedItem();

	Double multiplier = null;
	Double dataOffset = null;

	
	//set all the values below	
	com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;
	
	try
	{
		multiplier = new Double( getMultiplierTextField().getText() );
		point.getPointAnalog().setMultiplier(multiplier);
	}
	catch( NumberFormatException n )
	{
		com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
		point.getPointAnalog().setMultiplier(new Double(1.0));
	}

	try
	{
		dataOffset = new Double( getDataOffsetTextField().getText() );	
		point.getPointAnalog().setDataOffset(dataOffset);
	}
	catch( NumberFormatException n )
	{
		com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
		point.getPointAnalog().setMultiplier(new Double(0.0));
	}

	if ( (getUsedPointOffsetLabel().getText()) == "" )
		point.getPoint().setPointOffset( pointOffset );
	else
		point.getPoint().setPointOffset( null );

	if( getDeadbandCheckBox().isSelected() )
		point.getPointAnalog().setDeadband( deadband );
	else
		point.getPointAnalog().setDeadband( new Double(-1.0) );
	
	if( point.getPoint().getPseudoFlag().equals(com.cannontech.database.db.point.Point.PSEUDOFLAG_PSEUDO) )
	{
		//point.getPoint().setPseudoFlag( new Character('P') );
		point.getPointAnalog().setTransducerType("Pseudo");
	}
	else
	{
		//point.getPoint().setPseudoFlag( new Character('R') );
		point.getPointAnalog().setTransducerType("None");
	}


	return point;
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

	getDeadbandSpinner().addValueListener( this );
	getPointOffsetSpinner().addValueListener( this );
	
	// user code end
	getTransducerTypeComboBox().addActionListener(this);
	getPhysicalPointOffsetCheckBox().addItemListener(this);
	getDeadbandCheckBox().addItemListener(this);
	getMultiplierTextField().addCaretListener(this);
	getDataOffsetTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointAnalogPhysicalSettingsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(399, 305);

		java.awt.GridBagConstraints constraintsPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsPointOffsetLabel.gridx = 1; constraintsPointOffsetLabel.gridy = 2;
		constraintsPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetLabel.ipadx = 35;
		constraintsPointOffsetLabel.insets = new java.awt.Insets(8, 13, 8, 7);
		add(getPointOffsetLabel(), constraintsPointOffsetLabel);

		java.awt.GridBagConstraints constraintsTransducerTypeLabel = new java.awt.GridBagConstraints();
		constraintsTransducerTypeLabel.gridx = 1; constraintsTransducerTypeLabel.gridy = 3;
		constraintsTransducerTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsTransducerTypeLabel.insets = new java.awt.Insets(8, 13, 9, 7);
		add(getTransducerTypeLabel(), constraintsTransducerTypeLabel);

		java.awt.GridBagConstraints constraintsTransducerTypeComboBox = new java.awt.GridBagConstraints();
		constraintsTransducerTypeComboBox.gridx = 2; constraintsTransducerTypeComboBox.gridy = 3;
		constraintsTransducerTypeComboBox.gridwidth = 2;
		constraintsTransducerTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsTransducerTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsTransducerTypeComboBox.weightx = 1.0;
		constraintsTransducerTypeComboBox.ipadx = 57;
		constraintsTransducerTypeComboBox.insets = new java.awt.Insets(5, 8, 5, 75);
		add(getTransducerTypeComboBox(), constraintsTransducerTypeComboBox);

		java.awt.GridBagConstraints constraintsPhysicalPointOffsetCheckBox = new java.awt.GridBagConstraints();
		constraintsPhysicalPointOffsetCheckBox.gridx = 1; constraintsPhysicalPointOffsetCheckBox.gridy = 1;
		constraintsPhysicalPointOffsetCheckBox.gridwidth = 2;
		constraintsPhysicalPointOffsetCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPhysicalPointOffsetCheckBox.insets = new java.awt.Insets(39, 13, 5, 47);
		add(getPhysicalPointOffsetCheckBox(), constraintsPhysicalPointOffsetCheckBox);

		java.awt.GridBagConstraints constraintsPointOffsetSpinner = new java.awt.GridBagConstraints();
		constraintsPointOffsetSpinner.gridx = 2; constraintsPointOffsetSpinner.gridy = 2;
		constraintsPointOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetSpinner.insets = new java.awt.Insets(5, 8, 5, 2);
		add(getPointOffsetSpinner(), constraintsPointOffsetSpinner);

		java.awt.GridBagConstraints constraintsUsedPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsUsedPointOffsetLabel.gridx = 3; constraintsUsedPointOffsetLabel.gridy = 2;
		constraintsUsedPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUsedPointOffsetLabel.insets = new java.awt.Insets(6, 3, 6, 13);
		add(getUsedPointOffsetLabel(), constraintsUsedPointOffsetLabel);

		java.awt.GridBagConstraints constraintsDeadbandPanel = new java.awt.GridBagConstraints();
		constraintsDeadbandPanel.gridx = 1; constraintsDeadbandPanel.gridy = 4;
		constraintsDeadbandPanel.gridwidth = 3;
		constraintsDeadbandPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsDeadbandPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDeadbandPanel.weightx = 1.0;
		constraintsDeadbandPanel.weighty = 1.0;
		constraintsDeadbandPanel.ipadx = -10;
		constraintsDeadbandPanel.ipady = -9;
		constraintsDeadbandPanel.insets = new java.awt.Insets(5, 13, 5, 77);
		add(getDeadbandPanel(), constraintsDeadbandPanel);

		java.awt.GridBagConstraints constraintsRawValuePanel = new java.awt.GridBagConstraints();
		constraintsRawValuePanel.gridx = 1; constraintsRawValuePanel.gridy = 5;
		constraintsRawValuePanel.gridwidth = 3;
		constraintsRawValuePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsRawValuePanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsRawValuePanel.weightx = 1.0;
		constraintsRawValuePanel.weighty = 1.0;
		constraintsRawValuePanel.ipadx = 129;
		constraintsRawValuePanel.insets = new java.awt.Insets(6, 13, 14, 77);
		add(getRawValuePanel(), constraintsRawValuePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	//Load the transducer types combo box
	getTransducerTypeComboBox().addItem( PointTypes.getType( PointTypes.TRANSDUCER_NONE) );
	getTransducerTypeComboBox().addItem( PointTypes.getType( PointTypes.TRANSDUCER_PSEUDO) );
	getTransducerTypeComboBox().addItem( PointTypes.getType( PointTypes.TRANSDUCER_DIGITAL) );
	getTransducerTypeComboBox().addItem( PointTypes.getType( PointTypes.TRANSDUCER_01MA) );
	getTransducerTypeComboBox().addItem( PointTypes.getType( PointTypes.TRANSDUCER_420MA) );
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getMultiplierTextField().getText() != null
		 && getMultiplierTextField().getText().length() >= 1 )
	{
		try
		{
			Double.parseDouble( getMultiplierTextField().getText() );
		}
		catch( NumberFormatException e )
		{
			setErrorString("The Multiplier text field must contain a valid number");
			return false;
		}

	}
	else
	{
		setErrorString("The Multiplier text field must be filled in");
		return false;
	}


	if( getDataOffsetTextField().getText() != null
		 && getDataOffsetTextField().getText().length() >= 1 )
	{
		try
		{
			Double.parseDouble( getDataOffsetTextField().getText() );
		}
		catch( NumberFormatException e )
		{
			setErrorString("The Data Offset text field must contain a valid number");
			return false;
		}

	}
	else
	{
		setErrorString("The Data Offset text field must be filled in");
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
	if (e.getSource() == getPhysicalPointOffsetCheckBox()) 
		connEtoC3(e);
	if (e.getSource() == getDeadbandCheckBox()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
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
		PointAnalogPhysicalSettingsEditorPanel aPointAnalogPhysicalSettingsEditorPanel;
		aPointAnalogPhysicalSettingsEditorPanel = new PointAnalogPhysicalSettingsEditorPanel();
		frame.add("Center", aPointAnalogPhysicalSettingsEditorPanel);
		frame.setSize(aPointAnalogPhysicalSettingsEditorPanel.getSize());
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
	com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;
	if ( point.getPointAnalog().getMultiplier() != null )
		getMultiplierTextField().setText( point.getPointAnalog().getMultiplier().toString() );
	if ( point.getPointAnalog().getDataOffset() != null )
		getDataOffsetTextField().setText( point.getPointAnalog().getDataOffset().toString() );
	
	getUsedPointOffsetLabel().setText("");
	usedPointOffsetsVector = new java.util.Vector();
	Integer pointOffset = point.getPoint().getPointOffset();
	Double deadband = point.getPointAnalog().getDeadband();
	String transducerType = point.getPointAnalog().getTransducerType();

	if( pointOffset != null )
	{
		getPhysicalPointOffsetCheckBox().setSelected(pointOffset.intValue() != 0);
		getPointOffsetSpinner().setValue( pointOffset );
	}
	else
	{
		getPhysicalPointOffsetCheckBox().setSelected(false);
		getPointOffsetSpinner().setValue( new Integer(0) );
	}

	if( deadband != null )
	{
		getDeadbandCheckBox().setSelected( deadband.intValue() > -1 );
		getDeadbandSpinner().setValue( new Integer(deadband.intValue()) );
	}

	if( transducerType != null )
		CtiUtilities.setSelectedInComboBox( getTransducerTypeComboBox(), transducerType );

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List points = cache.getAllPoints();
		java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
		int pointDeviceID = point.getPoint().getPaoID().intValue();
		int pointPointType = PointTypes.getType(point.getPoint().getPointType());
		int pointPointOffset = point.getPoint().getPointOffset().intValue();
		com.cannontech.database.data.lite.LitePoint litePoint = null;
		for (int i=0; i<points.size(); i++)
		{
			litePoint = ((com.cannontech.database.data.lite.LitePoint)points.get(i));
			if( pointDeviceID == litePoint.getPaobjectID() && pointPointType == litePoint.getPointType() )
			{
				if( (pointPointOffset != litePoint.getPointOffset()) && (pointPointOffset > 0) )
					usedPointOffsetsVector.addElement(litePoint);
			}
			else if( litePoint.getPaobjectID() > pointDeviceID )
			{
				break;
			}
		}
	}
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	if (arg1.getSource() == getDeadbandSpinner())
	{
		this.fireInputUpdate();
	}

	if (arg1.getSource() == getPointOffsetSpinner())
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
	D0CB838494G88G88GE5F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BF4D4C516286123C68D9215CF1C0998A4E2905460308E4EE0E4F6F04499F4637A196CCC76480E4C0AB319B3599DD45629F4C292C810709398D0A2FF1C981210C08891490718C0A051044F88881D74CB3A13CEF7537DBA5D01GFBEF552BFA2F3BDF670307B9F24E653D3AD5376E272AEE5D3A2FBAE475264845A906550424ACA64A3FFDA9044C7E16102D4F2D7883478D11625209665FA7GAB4830
	6CB4F806C11D635938749C724B1ACCA8C7C1B96FC7F169EB61FD95A9F474FE85AFC178B4C3DD781FDA26744D674BEFB81F8CDA5ED11B8D4F8FG2281074FE48979FFD13BCE414FD1709CD28C044CEF43B65F54EED4F025D0EE840882185E465A4F0567924AF9E8D929526E6C48C8127C0B9D06E5580F26CF8E856D0C36D9F96612A9A90B5A1157ADD4CE24890572ACGD4BE15CCDBDB8B4F72464E76032D762E7CD6234DE63749D22BB9DFB2D9E43BB35FE13758643232C6FCD4580CD6FBFB233947E5E9B5DA1BA4
	D936585ADD07E943C623CD32FA4D486B795ACC5AB7A107201CE6C15CF999794F00F2BC4082015F6EC6FCAD3CDFG70DA384E1F6FA8D7F4BAFDF98A89F945163ABC084EE196266BF40B2AF3387B5D0C75F4FC061F41F1C8063AF59DB097G9A819CGE1G9B683C38731BCF61D951A2D7593B3A6C368A072352EAF29E331BA4AB3C1715C10D027BDAEA35BBCD0430313F35B6CFC71E3E00515DB7E8BAE61368BDFC9E44FC7D90C9F86228A1C347A689518506688EE60B199D2ACD986F39C1F9A7F6784ED3DF5E2708
	1637BC4C1E3AD807F76C4B3B8C49CABF191DAA6F52CE9C6BFF23F3E0A73C27A87303617F0362FBB5F8B6E7A50A47F2A854A5F57239F17A83AECBFD5AD812D3D82FEB0751F19506E6AB1341EA7517E5AC1DF7232D0CE71610A51A6227EB70CC16BC0A47722BD0D75E95174E66D758B9DC4311D01E81908F30GA05B96173E93205C466758AB5F6C9E609CEBB4BA65CA3745EA121C0451061D79901E12D7F69A6BECAE59E8EB15CE9A1D96E30BD5F291254D18B3E803969F3A9F50395F8ABC3E14EC1253A8DB6CB668
	5D58A5491253455ACCBC33825BB8A5DA5BEC691200C1170370FE271CD90576B33A64938E13D11654962C7EC7E7F0CC3A0DA721C768G5E19DD1E7E966DF596741781C2049D0A4EE2FFC7A5A7F80B3232FA3B3D536DE8C6CDC83C8D6D7CA6759D2361FD2A0D5B78DF4EA3EE1DBDAE3D404E7B311CDD2969275A520A629B1DBDFC0D3DA6231F2E3673B9730753FC4ED4AF1BC072DEBE22BBE7903CF6B6D7426C7EEB678D5AA74A454A1D7269C12EE3FF28366B2FB955D704FCE79E29FD54B17B6EB052B98F324483A4
	8B9BC71D4DE5F33652681278ACEDF0428413CC2A45191C116E558AAF748BCB143532CC6871A82D4FF6442597831C85888108F1F0DB9639CFEB64DDCCD45F437B79AEB6E0BC0E6A7248507079AEBC184D461E43FDCA6A16ECF2356410ECA6F8F1A9F58F1CC3BE4ABAEE338B1A09946F00F5DF858B4DA570134F615AB44A3253526216A53E5F0A7AE914174B5D62D0DD8667B5639C6AE777403A976D1FB907EB2655EE33C9F4708409D27FACDD3312D56A82212B6CEE1B4C704FFD0B733ED36AD1FC468B5FE2E054ED
	343A517F30B8E22A916D67F0DA3AE0CA208BA32597F1BD3FAF62A004F898EF0C073221AE77A2DF1B1EADFCEDEE3D74G19F9FE1BC1EFEF99BD215AB064A2DBAF6197553549E47A49F9FD59E7D15F2160DDCC56D31FEE076740A29A3EC6AE34E07F3140F7B6C062C53EC68EDD40FA6E071445D1E537DA1909056DBFB36660D8C9975D9238EFF4F8C705BD0B0DAB7D6ACEAA36E575655436D60D2F8CF322ED5FF07378F1A15A9AF085CEE6DF07D371E1345D44EE5EAE814A49G2BDDFA6DBABC3CDDA3541B81C66836
	7B4C4B5B4D027A8400C53A6D12C43BD29962AEB9309D9B0F0C7398136845206DEECB3EDB362007B74A30E89CEED993E71A6BB215BE9CB20FA7436BF11C2221BC9360D5194F31971771F8B23CB312A4BE783720B1F4124CE4DB6DD667D86EADBC1E1C540ABC1B214E6466F360C7C66D1ED661F259DBADF4BBD5462F2DC5BB87992E33E5A9BC5BACCE17DC87CE424B477AE28B4A0B7B2D02D6620F6C6EECC5D7AF701D8A30359B716B29DF65324FF733FDB3876AD2E9FF2E6EA274A1F4B26633C72364EC33BB3BA453
	4E8EE337B15F681173293F4A2F20550771FD979F1BD93BD6B2B9A5EAFD96846C5CF512470563D79DD2F84FF9F4ACA93F364A2097270FD87E11415A2D2C5DEE552E3D0F2727AE78763D8C2663DC21E3A234D96761BA2694EBF5EC74B09D3D9E2E63BE8F46D696D96A4A477F1AE490314AEC3435872AD887750A020C67A88F67998A7D4D92BC47F77B73DC84106B65BC5F7D8EBCCFFAB94FE2684F6A65BC0B1A34BC23GBF87A0C970FC5A0BBCDB0DCEC94E277FAB23017C3C79AE8F38AE05E595567A70DC5F43F926
	75C0DC5A43F9E64AFABC659E3EAFF755791F97C67428B1CBC4CFE07C4262682D47105F549E96C72F9078920ACF506019FCE3693A45F26625387470CBBC765BAF617E11FB99E486E8863082048244DC666B6F1D16BD241F583A8E7C0993FCFD134FE649E35CD634AF6CEFCA7909D634935D5F880F9DD6F67B5BE176E5DF5D67791599DDEEGDD3EDFB93357170E2D3BF66B866E93B543DBD6F684F4EC31D75A3D047940C6B7BFEB6776F2DFF725C66B6BE6ACAFB2E48571F527955E5EDEF5CD660933739BED683F27
	C3DDBC4082004CABF00686288530DE61767F5C74F9FF7627F33159DE8538DA58472A7CE390E12FFF3FCEBB4F6B574E21D7746CFC70E4B0BB1B2556CE554E2545E8E734EF54953EB646F82ECF560981F47E32A678494AE2299C0FB616CA3E427C603A2BB0A6GCC4E573A391C55D7391CE73D57A7E7C7G1D3F1C3DD775EC3AED67C6FD1BB6C36CDBE3112CA6264FCF3B75748907BE2305BE1626CCC51E19C21FCDBD57274FE781F47E7AAC7453074970054CE5483986EB011ED3C3E86CD18B653382E7BC0A38A1D0
	9EFD0D4B7A6931DCF84E00722BG8B81B68E09CB2F86B08104GC48E61ED8F3036D0CE984262B90C5596417B461B587E033ED9B9EF558F7113DD5B27964FF9637B46A1017E5A9F96DE8BEC9399040E4972EC00AC7C4C365C8F6FFF366BCF168C9D5C0AA0ED19AF2A14021F3F6AA47D73D70324FF7EBAA12DE0F868C31B15617567243E4FE71224F7BE7B085A61398F17533EDDAE49C51DA38638CA5D1B729A22F3CEBBEE6C12949B02AE5DCB14B607E5FBD511980B936E3EF20B65B7850EA373759DE60C839A215E
	8A90829081B063A66E635F137A5DE38FFB7B58E35FEE8BBEAE49ED7A6372EE9B4EA1175D6DEC157CED1F523ECE54293B8906DAE8A656E6E9BB524BC6E73B94B0F6ABA8BD2B8BC67F3776E0634B46E019C7BB86FB4498FCDE0CE31060E76BA4251CF6B3FB2E3BD92DE77D4D75692F04F0FF7A67DDA82B0EBF551C05EABD5CC7655D4C4FC2EF511C25894A3DG61B7F35F719B3AD7465C6CAB5FCC259C2FBC678748B74A2B15EFAF617E71AFF44F2139D2417B48E564DDFCCBDCFABD0069964E5B5654577C6C05F6E1
	3728FC23EE61316F499EAD6FFD425655F2DF7DCD877AB91A7E92C5FF636AB007E1B576585DB2A197C2D066476B78FE148C6D3281D78CD09B4276A5B6960FF9D04FB627318B062049A135127221C86BBD4AE923217D8C008400C5G9B07F21E30FF90A56F505056661264FAE30BE465F5884DCE234DE5F237CA4E669E07C42B892F7FB020DE0402CA1BBBF2F15E72E406060F08E5D83B2C2FD77A4A51643058E82208559FAEC5DFF042A51974E53D1C0D6B2ADAB21ADA0CB61312A6D1E8A7EE423A2F0D9E1A4260F5
	6C2C61BA26BDEB048CE5F60D9E4A4FA2539A746AE7087A83BEE73239CAFD5AB0DE5FD622C73FEE985F1F2DEE3EBF678D63FE4DE98E6E17FA4C7AFE691AD9FF3F98EA6EFB3F38439C4C1FB0791AE90C598B320589196F2738A8A8478A1C21046BB1D3601E213A658C875D067335F64CA74EAC85FC7DF0F5ADDC989E389730B35AF9FA8E170733B35AE2016F2078919ABCBB3B7D494D4F1691D0C7C6F0197E077A8593143D82F7107A29D0A8C7885C039E2ECF3440459EC75CC2A82F3E156B33C844783900AB86283D
	D55561AC3CBBEE750B1B295CBF213EA864D6A6779201FF1E62A3B5F826CFA345E3F98654655E46E59A5A44658C913837AEA3AE9E4A8B846E217D08A337036C37F35C9919536E9438570BB82DD76052E83BD0A8C782B0F9DE2F635FEEE3813760F66E0B8AAF78445918F2A8AB7B52E9B1D59A5B6B29E7E37EF97311CC05FEEBA8774F0DC7687799A84BG21217C6C685D4E4F0EB18F0DA575C30E6A6649921B0B8CD1214C5633C35533E3B41C9D994F9F9C65BC536E00337B9D081BF3D43BBE51DF349AD88C4E6A5F
	77294735320BE86B8B6A3575A86F26D4EDFD0BCF7DE28DBD536D79EDDC37C54FC7936B347DC1F300A1F7B01D2215E76349ADE46BED6FE5A87BF1DB107D187AC8267F24E328BF761FF887BF4BACE860386ABB7998DE5D3B1DE8F21F1D56767C0ED674AA79EDF8D04203A8FAE7FAEA627B2DD36C3751F7723E6FAF917C90F7977D666F2E5651CDEB1F8C42E38C1089E8CE83ECD1F0D6F8F7A976E13E3D137AA2170439ED49B41772383918679FE65E45773958E27CDEA00363DC9D06F7A85E375319C55B2FAF69CB26
	D4221E9170F9DAA3934AF1BA0C2DD5D526D75D28B3CBECE2E88A9B9214995F3D9711AFDBD7A66AE3CC0077067135B6378AF191D01EAAF08D0779BA0D8F63FEC722765EBAB2AE7D50C80EF3D09C66484F8A5CDF5C037DD6077D7557E62B52067148699EAC0F4583E8132AF92EA2591E40F15E6261635CBB120F73890F6FB8DFF52A637C14E7A0634C010F739AE5ECC3C77A0F737347030F7383AEF51CD7D1B9C783FD6CC8BE26BF26F3F4B61493846E77CB290F3E9BFC745D9CE74E66E3DFAAF0DF5179B0824A11F7
	73F11EF5996338D8A84F85C882489E456BDE24F5F92358F7117AD16C3B872BBBF9D9EF6C300F7E460773704747EAE8998F3BAE0F014C817E9DB4585C78C00CEDC4D3FFFEC5BB3656D13EFEE58814DF0367DF619F9BDBC3AF7AB28828ADDD6E2EA64BA50930B97737DE3A97C27B5123C48EFCBF4A59EA373A3BECAE25DD7CFE3E574C9A45FD1FD36ECFC62D1C71FEB24E574848F8246EC19E4900A761DC168DF220ACC5B217A5AF1C0FFBE2C08EFDA0E331027C61BA69F69C99AC9D0B07B69F451820BCDCDDFB8C7F
	F70A2F5560156F98C7F8DC528CF55343F94EBEC4467DF163BD300EG9A814CGA3G226E61F17796CB8169A71F50ECF754C3E0EE550452CC3F870FDE5F7D29E7B2787E9F7B54F8B2FD58BBA9417262536FE16B6AD565B93FF98B45B37D56523CE5663DF06EG288538GA083045E4B756B3464B17D70DBF205AC9BDB4D746B9FD76E24C57228E8BCC8B25D269C53DB3F1177B2B9E26F55779533811F60D7178465347B747220273E526331CEE91BF71FBE0FC300EF762BB3C339D71707C7446AE1D09F25E9B39D5E
	67GA46A520DAF5113ADD9E91BB9DADF360D00AF702B2B0672E9BF5CA6424E52547753FFC9DF8462783EE2404F2F4A5C794FECBEF7B6BEB20ED43F72CE8A0FC97CE531AABCE70CD66362799D0BB2184F9843E2A327DBE27DC6FA34F7BBCB176E533D7BC0ED207417B9C669FF7896C536DF1D517611B37693C3CA10BE8EA93475E3D459182E8BFB30A765FC6F415CC80B5DC98F2FACB7F2377876B084E8C7834C8518B7066525995D1BD501B915772BB85D22B1AC2732FAAC1C0146B2BA662B5B8E7797ABACD16687
	3DD38A344DE3192F4632995E5F86BF7DC7311FBCCD6FDA707C84CFFE30BA4F3E651ABA1E78E079037C867F7C824AA80F657A15F97858B11BED382C5A6CD38F46A78E2754A6B91D12096EE2C459932AE8CCB2827A8997FD3DD49CE8635F97F3DB4D9C4BEC7CAABC970CE5F9FFA58F1495E86335D91CAEED9C3371C6F8960CE3F44C4657327B33F116464635C0FBF61CEAE3873C2726E863B173E17DBDFB880D17FA217D08F1DC662CB3E873E3EE2BECF1D8AD12D31B57BA5F344A27CEFCA2D26A6FBECE474BA89BB5
	E9A825EECA66AA1FBA15160D6762DE7FFC905AA3F61C38D7D026D7BF9B6A757C849B7326DEAC317795D755719F377FE3F85AA4CF2E787ADFED81570E17A2F71614646596956E62ED5F5A0FF6447DAFB81F62EBAA1F962272F947B5F0BEF5B26743FC6B4E23C43C97EA5E4F9E792EF78C7B824643FDC3F9A879B5B9F8AEAFDC564F650D1575F3F911F25F5FF6226560F76F26485A3BF7ACE6A8CA413916GF3EBA1404688D81FG5591629B3AFBB3217102CE2CF058540E5FB870662F4C7967F68757F5C7F77F7705
	8B3A717ED10BDD166DDD58BF476F6D4671126D8E0AE4388374DB135352EE16D9D385DFC14F40D6290D2259F969C51A7738GFAB9A27899EAA34DFB04C0B9C260569CC45CD4A84F91381736A3AE914AC98277C5A1BF13255D4FF1CF6750BBD8D0368ADC4FD71C6F90014B7A1A738D97387504730D96389777F13EB384EE5BDA4EF72E404D5D4379667E00636E24B6A806F22340E5507326894A32406D267D0D00F22440B976F39E3182370F6E95E9BF04B972C30E1BC5F3BB85D0BEA4F03B76A22E9E4AA6014B2234
	DEA8078B5C4F29AC51D01EAEF00EEB1C6F9C016BB805380D11F169451162EE3B81770CDAA81BGC6GC48244824C81C884C88648FCG6C81D08AD08FF081C0868881988D9073G1F63DFFAB19FF7525821C47B6C1AA51D6500D49217F422BB0D961C6A4A5A795FD414EDA67413A07A3A537B77BE7AEAF45A9D12D36EE1D87AA9C2696BCF3805AB73B75E3BADD89F306A0E00FFEDE86910DA65E6E3BB61E71E27BC7CB7890BC0166271BC0F7766C728DB5FF7981BAD32D52AD436FCA517E77072BB0C5671BCB77BD1
	95720802728C007871BC8F3C3D1A1F3796GEE6B03BC77E72B66E74E338F72BEA2E85BF0A847G4CFAD0FFCF8B3E7FFCC5FF7B447A179AF83EB1817AE17574682B6CB3D30B311FA0FBD2E1E9C1DEEEE9A93F073622045BEFF5944C17A82E43EF4B389DC222388EFB17525F8CC1B99EE0C194374397D95C8EE99362524BA770BE06E7F1BB4893F89F53E85B98A84F82C818B0D8BB5CC7D1CA9EF3092A73E7178722B3132B3C1767E613052CD5E59CF77AA1BE274BD2027B740A147EFD7A1194FA4F94FCB1FE9DD46E
	B62460C05AE9FC445BB5276488DC1D2B516E32E0D60D69B57F984F6931F86EFD6A4FE7000C339F62715C1E869A7B993DFE315FD1BACF66C13BA451B66FE0703878B05D83B6CE047340C45E7E775B11FF9B2C9D56662FF4CFE8067A8B93F9ACFB32303FD8D61B5B714FA107C1BFDF92EDAC9B1F239F4BAE207BCC84340F86E03871F41F0907F222409553FD06C4C36C9D4DF115F4AE15C239DA605E257B0C834A3D51DC1772357D69125107AEB151EACE0671F8076ED15381BFAB1A5BF4C2F770B178895D3F12206D
	A2517E63E25FB1A826FB484E07415FBD4C5B542C2571033135335DE9F7E3B216357D166EF3A6E867F8186BB8FC7FE0F2F181637530FFAE7838C7FF3C5A687E97816D23051C3FAE714DD1FF12252E61C3FB756771E93A3746C39F09221F4DFD1C6F8ED03E5913625257CD626DFF63E743E4EA43EA28EF1CA46CF22DAF3BE474E31710C97EF63969143EDD46503DBD8C5AC7CC923F6DB18477B50F987A77B531867D7871A9C3E07C78D303FE7C78B303B6FEE438DF507CC7BB448227C53BDF895CBF84AE1E760938D3
	9613EC96785729DC082F1510A147A7D239BC94F7DA417D1786778F85775F8674172032596E6434FFB4503134D82D2C7CE75A46E28377A96C31104AE8F198B5F27F1F4029F23F170AE3AB4E95ACB4D16A8CE92BE93F600E0DD64BA57A9302A6E8E8EBE7E728198177C67DE176A43D1C5AAF7B257BD8E76E2DA099747E673CC97DFF1FC0D8886DD83EF8A40DDF329F714FA30F2578F50F78671137D4717DFCAB540DF804674D732A2FAF7FDDF61DF42EC153B13DF28E237C8B8274DDCF718B83749DAF6238341838F4
	F38C57372301731F83384498556E49701EB9994E84137546777E326B1B970F74CB27BFAF66D13A0249831B975513793C18C4633953137D6D7484451BA67B5B29FCA93713836A62A7F3BB9D4E3A3E71ED3ACE3A50C153B13DF6E5237C250F7A6BDBC471550F7A6B7BE39157B6C2DD6C23DC5F0ADE5DFC7D94A5DFBFA5C83E9E704DFEF5E6A877CE511B8B35E5FABC421436D1C1F8CC877C9C3F3AC4A8A77B61585ECB273D32D72DE9407DE7B33DE0164B52F7E51B15F6745A2652EE6941BE72DD7BCA73F28B776E62
	7D5F7601CA77736D83275B35C6255BD338591F2E86B6711D1BCD967C2560663CDC886D8B0B85EDBDEE154A3BFD2D5A4F680601733FFB1DCA17CA6F66EDD6AF7A65E2AC8F26D15A4EDC2F367D0074F586A95C4318B03A6ECDAA5D3F6E9BB85DCB1F28F46B578E4EB6E40BEA1BFDDB55FE964983677F4DB68D5D1EBEEC3A97F89795895E1BF62AF4DB86A97752CFD5DA73FE5C1385DD155DEAF766FB4C96D9798670595FD59D4F6429F4F5A58317756AE7AA9D7174F72EACAA6036F9A0DF251B516F99DC257B20C425
	EB3D3623AF5B6437582DA6B0D0A1B39023AF5C2B6A5C3CCF6D6B4F5D8311015DF11F398A973945E6355824AA76978A883F4371B14D09E4CE0DCB4F1D4A73D393AAE91E8D4A35D339DF3C2F0A887F9529F95FD5F5637359157F849E9F9ED6F55A24F90F2E3E713C1F3921BC58185E5440F3274DE21C8F32FBD5D0768A5CFD27F86EB4D4606E3E44F3181182F72E0467CE13846EBD1AE7C9FB8CE22747B86EA7CBE8BE954A550F7139B4F529EA5FFF513CD7AE3D7136BE73CF6031BBDB5529D4737ECC560D677D7A8D
	654146341566589AE1BCAF08F11ECDF1D6A807885CB8FA07F7B414E3854ECB6F704E06722B827704388738C06076517E4A9F07797A38481B5379E50632D760FEC5F1A1D00E9038F8FABFFBAA1467885C9A1A93C804F21240B5FEA83EF7BC41F1B16C3B83142DCF707CC2897DAD060C99E574570467A20ED15FB9845A050A36B3AA977336047BEDFA6DC02119C45761CCE8BFC7501C68764DDFF452F539906A53E2F9BEEBA6DD1745D0BE944BF39A8FDF9A4C5DB07FFBFF97201F93C41B4F9AC65734957B0F65322D
	BC202F7B278768DDF9E8B7D534FDF9C9206E93769342772CCB3D3E792159E5EA5DEFFB7573C3FF2173E09E70C892FC9EEF8A1E9FFA1E4E390D536252F7CE636D6F3A6CEB5F89F48EB6C37D05E95C163F6877DE041EADC55EEC1AEAD7A5EF96641EDD9B55A7825AC78B79EEAA7335CBF739EA175F15695B65FDBA6F63210FC4514FDB6E60F6C923F3AA7B493874F5CF0AFC31DF5E2C12654D203E71C9EE17768F3FCB7EF748137EF933E777685B6557F45D05C27B51CF726F9EAF9DD46DB03AF2703193B924526F69
	9EF80C58DE29525DFEEAF0716C8BD5AA6DED177A0EE71350348A5D2A9A15EE7CA0E435572AF40FAD993C0D6E2FD369431A7ABAC795781C23669DD1693AAE8F1C6E50B115EE6261415956F6DC35574886351FB50310BB36D125FB4E5D875DFEDF3AD73ED269169677C1D76ACBD7F5C2252B7EF0F07A361DD4753D75145A4FDBFD5DCB917C595FE15C173997A210183749E2192C61BFD74B552682E432210F1F3149A47D308D3351FDB7F3B949F2A1CC9D4D56A6F66FA9CBFD6341D483C92BA369ACFBDDC752B085DD
	C796538CF59DC9156D8E2FC371A26881702A4CE257C5F21524A9D874E97837B8F688AC77BE781E6A53966DD3087E8664D999E81C0ABBDFFBA566FC75EA83B100C8E60949305803D9EDAA857A70FD2247651A9E19CCCB825AF0890F79344198AC56C7DE5EA65D370DCC3218A1F371A700B2D966132491ED719B62F13F1213AC7319C6BFFB2064F7F3FEF35215CA526B48FA746ACD1213A637A5935E5BD27EEADC8DD9C27F4CA47ADE988E97FDACF649BDD6291E2CEE7B9A7FB212CB16CC95BCED6E0209D30F1CF663
	17755D35C617D94689F1201E2459706F5B11152C5AEA34356FE6B97483541AA99597F052D27D89483B0A4A0B3D640BDED67DF87936CF0E4C1B1ECA8CB5E42955EEB45598DBE53B330EAC114DCE49E5861F65F5FCC1BDB94974700F6329505D8621BEAA1B0FB75204501D5E7BC3174DDFD6E3A0446BA43BE9CB5CE073C58BD315596884D61233CE463F57E7F7EE95F5B7FDACEFFF614B35E78DA4C5D192CD3F566B005550E66C32D8FB6005ADDABAA89BE4109633C87837FEC032DCBF49F815E010BF6A3902FB9EFE
	779B4AC0A51549A6E6CAA05D5DAE593BF6D734354121DDF4GF6056EBF925DE31BFC3FB6AB3DA3579CF9792FADB08F2AC9EAD7C1817D3F107EDF047F97D2CCA145949601F113C81F7F7C6B3DDCE7BAF9444A47614F2F019114DFFF7B39713FAECFDAG5A55104574EF3B40A064282D0E3B3BDAA4673018FBBE4A8ACF3C9D474801D3450B7DEE720AF5433F5A6349A653F7764BA447BFED67536022CEE9CD4014C46C7A403905680F4488E3A917B65DCC7F5FB6E8FBA19573DE3B041AFBD4B51784D5D35582B31F65
	5FBFAD4A5171FF609B6124CCD8FBCB4EDDFB5C8612D9034B456AB09B6B14E9D0CF32DA2867171C509686D11D6E1496FC0A21959C4E86955F0AF8D65B64B036225BD9E2B2BABBE98FA8C3459932244D595EC2994844F026668C3025A52C2E2AA7CBE5B35E694375AF1335EA570DF873C929E09E0B7E8845C17F79F8F8B42A01E8BD3CBAA0D842B8AFD62764BFD8A9C186AB0D8FD6350FD3C9E75FCB19511166460CE23DBA97B1EA46AD8F67E33A2FC2B24964D739415DFDED74641EE4964FD2EF941749A49B779A1F
	8D6CBA0D0025E378DF430D517A0C777B3E3ADA180414374E4464A150A54243203EF5791E583DD335BB7A389A1D5277EA94DC6A6AC08F0D79BC030F45833578462754787D53690A2A538A0DCE993EF2735F636C3A267D4D4B42216B027EEEA676D965F76A4F2A3FF9E1F7C12FAC233961D9F1693933783D18973271E3CC30BB21CDDDC62BD5B6DABBABECA63C9FDA59A3CB6A1D701B17E17FAE5EC629E4F537AEC339BBB4B47F8FD0CB8788652C2CBF759EGG70DEGGD0CB818294G94G88G88GE5F954AC65
	2C2CBF759EGG70DEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGAF9FGGGG
**end of data**/
}
}
