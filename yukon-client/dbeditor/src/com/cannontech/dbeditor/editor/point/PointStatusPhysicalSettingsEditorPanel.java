package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointTypes;

public class PointStatusPhysicalSettingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjPointOffsetLabel = null;
	private com.klg.jclass.field.JCSpinField ivjPointOffsetSpinner = null;
	private javax.swing.JCheckBox ivjPhysicalPointOffsetCheckBox = null;
	private java.util.Vector usedPointOffsetsVector = null;
	private javax.swing.JLabel ivjUsedPointOffsetLabel = null;
	private javax.swing.JLabel ivjCloseTime1Label = null;
	private com.klg.jclass.field.JCSpinField ivjCloseTime1Spinner = null;
	private javax.swing.JLabel ivjCloseTime2Label = null;
	private com.klg.jclass.field.JCSpinField ivjCloseTime2Spinner = null;
	private javax.swing.JCheckBox ivjControlInhibitCheckBox = null;
	private javax.swing.JLabel ivjControlPointOffsetLabel = null;
	private com.klg.jclass.field.JCSpinField ivjControlPointOffsetSpinner = null;
	private javax.swing.JPanel ivjControlSettingsPanel = null;
	private javax.swing.JComboBox ivjControlTypeComboBox = null;
	private javax.swing.JLabel ivjControlTypeLabel = null;
	private javax.swing.JLabel ivjJLabelControlOne = null;
	private javax.swing.JLabel ivjJLabelControlZero = null;
	private javax.swing.JTextField ivjJTextFieldControlOne = null;
	private javax.swing.JTextField ivjJTextFieldControlZero = null;
	private javax.swing.JPanel ivjJPanelControlString = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldCmdHrs = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldCmdSecs = null;
	private javax.swing.JLabel ivjJLabelCmdTimeOut = null;
	private javax.swing.JLabel ivjJLabelHrs = null;
	private javax.swing.JLabel ivjJLabelSecs = null;
	private javax.swing.JPanel ivjJPanelCmd = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointStatusPhysicalSettingsEditorPanel() {
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
	if (e.getSource() == getControlTypeComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getControlInhibitCheckBox()) 
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
	if (e.getSource() == getJTextFieldControlZero()) 
		connEtoC4(e);
	if (e.getSource() == getJTextFieldControlOne()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (ControlTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointStatusPhysicalSettingsEditorPanel.controlTypeComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.controlTypeComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (ControlInhibitCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointStatusPhysicalSettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldControlZero.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointStatusPhysicalSettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JTextFieldControlOne.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointStatusPhysicalSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC7:  (PhysicalPointOffsetCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointStatusPhysicalSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if ( getPhysicalPointOffsetCheckBox().isSelected() )
		{
			getPointOffsetSpinner().setEnabled(true);
			getPointOffsetSpinner().setValidator( new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(10000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)) );
			getPointOffsetSpinner().setValue(new Integer(1));
			int temp = 2;
			while( getUsedPointOffsetLabel().getText() != "" )
			{
				getPointOffsetSpinner().setValue(new Integer(temp));
				temp++;
			}
			getPointOffsetLabel().setEnabled(true);
		}
		else
		{
			getPointOffsetSpinner().setValidator( new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(0), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)) );
			getPointOffsetSpinner().setValue(new Integer(0));
			getPointOffsetLabel().setEnabled(false);
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
 * Comment
 */
public void controlTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	Object controlType = getControlTypeComboBox().getSelectedItem();

	boolean value = PointTypes.hasControl(controlType.toString());

	getControlPointOffsetLabel().setEnabled(value);
	getCloseTime1Label().setEnabled(value);
	getCloseTime2Label().setEnabled(value);
	
	getControlPointOffsetSpinner().setEnabled(value);
	getCloseTime1Spinner().setEnabled(value);
	getCloseTime2Spinner().setEnabled(value);

	getJLabelControlZero().setEnabled(value);
	getJLabelControlOne().setEnabled(value);
	getJTextFieldControlZero().setEnabled(value);
	getJTextFieldControlOne().setEnabled(value);

	getJLabelCmdTimeOut().setEnabled(value);
	getJLabelHrs().setEnabled(value);
	getJLabelSecs().setEnabled(value);
	getJCSpinFieldCmdSecs().setEnabled(value);
	getJCSpinFieldCmdHrs().setEnabled(value);

	
	revalidate();
	repaint();
	fireInputUpdate();
	return;
}
/**
 * Return the CloseTime1Label property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCloseTime1Label() {
	if (ivjCloseTime1Label == null) {
		try {
			ivjCloseTime1Label = new javax.swing.JLabel();
			ivjCloseTime1Label.setName("CloseTime1Label");
			ivjCloseTime1Label.setText("Close Time 1:");
			ivjCloseTime1Label.setMaximumSize(new java.awt.Dimension(87, 16));
			ivjCloseTime1Label.setPreferredSize(new java.awt.Dimension(87, 16));
			ivjCloseTime1Label.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCloseTime1Label.setMinimumSize(new java.awt.Dimension(87, 16));
			ivjCloseTime1Label.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCloseTime1Label;
}
/**
 * Return the CloseTime1Spinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getCloseTime1Spinner() {
	if (ivjCloseTime1Spinner == null) {
		try {
			ivjCloseTime1Spinner = new com.klg.jclass.field.JCSpinField();
			ivjCloseTime1Spinner.setName("CloseTime1Spinner");
			ivjCloseTime1Spinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjCloseTime1Spinner.setBackground(java.awt.Color.white);
			ivjCloseTime1Spinner.setMinimumSize(new java.awt.Dimension(55, 22));
			ivjCloseTime1Spinner.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCloseTime1Spinner;
}
/**
 * Return the CloseTime2Label property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCloseTime2Label() {
	if (ivjCloseTime2Label == null) {
		try {
			ivjCloseTime2Label = new javax.swing.JLabel();
			ivjCloseTime2Label.setName("CloseTime2Label");
			ivjCloseTime2Label.setText("Close Time 2:");
			ivjCloseTime2Label.setMaximumSize(new java.awt.Dimension(87, 16));
			ivjCloseTime2Label.setPreferredSize(new java.awt.Dimension(87, 16));
			ivjCloseTime2Label.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCloseTime2Label.setMinimumSize(new java.awt.Dimension(87, 16));
			ivjCloseTime2Label.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCloseTime2Label;
}
/**
 * Return the CloseTime2Spinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getCloseTime2Spinner() {
	if (ivjCloseTime2Spinner == null) {
		try {
			ivjCloseTime2Spinner = new com.klg.jclass.field.JCSpinField();
			ivjCloseTime2Spinner.setName("CloseTime2Spinner");
			ivjCloseTime2Spinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjCloseTime2Spinner.setBackground(java.awt.Color.white);
			ivjCloseTime2Spinner.setMinimumSize(new java.awt.Dimension(55, 22));
			ivjCloseTime2Spinner.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCloseTime2Spinner;
}
/**
 * Return the ControlInhibitCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getControlInhibitCheckBox() {
	if (ivjControlInhibitCheckBox == null) {
		try {
			ivjControlInhibitCheckBox = new javax.swing.JCheckBox();
			ivjControlInhibitCheckBox.setName("ControlInhibitCheckBox");
			ivjControlInhibitCheckBox.setSelected(false);
			ivjControlInhibitCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlInhibitCheckBox.setText("Control Inhibit");
			ivjControlInhibitCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlInhibitCheckBox;
}
/**
 * Return the ControlPointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getControlPointOffsetLabel() {
	if (ivjControlPointOffsetLabel == null) {
		try {
			ivjControlPointOffsetLabel = new javax.swing.JLabel();
			ivjControlPointOffsetLabel.setName("ControlPointOffsetLabel");
			ivjControlPointOffsetLabel.setText("Control Pt Offset:");
			ivjControlPointOffsetLabel.setMaximumSize(new java.awt.Dimension(128, 16));
			ivjControlPointOffsetLabel.setPreferredSize(new java.awt.Dimension(128, 16));
			ivjControlPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlPointOffsetLabel.setMinimumSize(new java.awt.Dimension(128, 16));
			ivjControlPointOffsetLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlPointOffsetLabel;
}
/**
 * Return the ControlPointOffsetSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getControlPointOffsetSpinner() {
	if (ivjControlPointOffsetSpinner == null) {
		try {
			ivjControlPointOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjControlPointOffsetSpinner.setName("ControlPointOffsetSpinner");
			ivjControlPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjControlPointOffsetSpinner.setBackground(java.awt.Color.white);
			ivjControlPointOffsetSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			ivjControlPointOffsetSpinner.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlPointOffsetSpinner;
}
/**
 * Return the ControlSettingsPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getControlSettingsPanel() {
	if (ivjControlSettingsPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Control Settings");
			ivjControlSettingsPanel = new javax.swing.JPanel();
			ivjControlSettingsPanel.setName("ControlSettingsPanel");
			ivjControlSettingsPanel.setBorder(ivjLocalBorder);
			ivjControlSettingsPanel.setLayout(new java.awt.GridBagLayout());
			ivjControlSettingsPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjControlSettingsPanel.setPreferredSize(new java.awt.Dimension(390, 125));
			ivjControlSettingsPanel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjControlSettingsPanel.setMinimumSize(new java.awt.Dimension(390, 125));

			java.awt.GridBagConstraints constraintsControlTypeLabel = new java.awt.GridBagConstraints();
			constraintsControlTypeLabel.gridx = 1; constraintsControlTypeLabel.gridy = 1;
			constraintsControlTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlTypeLabel.insets = new java.awt.Insets(3, 8, 2, 0);
			getControlSettingsPanel().add(getControlTypeLabel(), constraintsControlTypeLabel);

			java.awt.GridBagConstraints constraintsControlPointOffsetLabel = new java.awt.GridBagConstraints();
			constraintsControlPointOffsetLabel.gridx = 3; constraintsControlPointOffsetLabel.gridy = 1;
			constraintsControlPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlPointOffsetLabel.ipadx = -18;
			constraintsControlPointOffsetLabel.insets = new java.awt.Insets(4, 3, 9, 2);
			getControlSettingsPanel().add(getControlPointOffsetLabel(), constraintsControlPointOffsetLabel);

			java.awt.GridBagConstraints constraintsCloseTime1Label = new java.awt.GridBagConstraints();
			constraintsCloseTime1Label.gridx = 3; constraintsCloseTime1Label.gridy = 2;
			constraintsCloseTime1Label.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCloseTime1Label.ipadx = 30;
			constraintsCloseTime1Label.insets = new java.awt.Insets(4, 3, 5, 6);
			getControlSettingsPanel().add(getCloseTime1Label(), constraintsCloseTime1Label);

			java.awt.GridBagConstraints constraintsCloseTime2Label = new java.awt.GridBagConstraints();
			constraintsCloseTime2Label.gridx = 3; constraintsCloseTime2Label.gridy = 3;
			constraintsCloseTime2Label.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCloseTime2Label.ipadx = 30;
			constraintsCloseTime2Label.insets = new java.awt.Insets(2, 3, 10, 6);
			getControlSettingsPanel().add(getCloseTime2Label(), constraintsCloseTime2Label);

			java.awt.GridBagConstraints constraintsControlTypeComboBox = new java.awt.GridBagConstraints();
			constraintsControlTypeComboBox.gridx = 2; constraintsControlTypeComboBox.gridy = 1;
			constraintsControlTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsControlTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlTypeComboBox.weightx = 1.0;
			constraintsControlTypeComboBox.ipadx = 0;
			constraintsControlTypeComboBox.insets = new java.awt.Insets(3, 1, 1, 2);
			getControlSettingsPanel().add(getControlTypeComboBox(), constraintsControlTypeComboBox);

			java.awt.GridBagConstraints constraintsControlInhibitCheckBox = new java.awt.GridBagConstraints();
			constraintsControlInhibitCheckBox.gridx = 1; constraintsControlInhibitCheckBox.gridy = 2;
			constraintsControlInhibitCheckBox.gridwidth = 2;
constraintsControlInhibitCheckBox.gridheight = 2;
			constraintsControlInhibitCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlInhibitCheckBox.ipadx = 17;
			constraintsControlInhibitCheckBox.insets = new java.awt.Insets(4, 8, 22, 20);
			getControlSettingsPanel().add(getControlInhibitCheckBox(), constraintsControlInhibitCheckBox);

			java.awt.GridBagConstraints constraintsControlPointOffsetSpinner = new java.awt.GridBagConstraints();
			constraintsControlPointOffsetSpinner.gridx = 4; constraintsControlPointOffsetSpinner.gridy = 1;
			constraintsControlPointOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
			constraintsControlPointOffsetSpinner.ipadx = 4;
			constraintsControlPointOffsetSpinner.insets = new java.awt.Insets(3, 6, 3, 5);
			getControlSettingsPanel().add(getControlPointOffsetSpinner(), constraintsControlPointOffsetSpinner);

			java.awt.GridBagConstraints constraintsCloseTime1Spinner = new java.awt.GridBagConstraints();
			constraintsCloseTime1Spinner.gridx = 4; constraintsCloseTime1Spinner.gridy = 2;
			constraintsCloseTime1Spinner.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCloseTime1Spinner.ipadx = 4;
			constraintsCloseTime1Spinner.insets = new java.awt.Insets(2, 6, 1, 5);
			getControlSettingsPanel().add(getCloseTime1Spinner(), constraintsCloseTime1Spinner);

			java.awt.GridBagConstraints constraintsCloseTime2Spinner = new java.awt.GridBagConstraints();
			constraintsCloseTime2Spinner.gridx = 4; constraintsCloseTime2Spinner.gridy = 3;
			constraintsCloseTime2Spinner.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCloseTime2Spinner.ipadx = 4;
			constraintsCloseTime2Spinner.insets = new java.awt.Insets(1, 6, 5, 5);
			getControlSettingsPanel().add(getCloseTime2Spinner(), constraintsCloseTime2Spinner);

			java.awt.GridBagConstraints constraintsJPanelCmd = new java.awt.GridBagConstraints();
			constraintsJPanelCmd.gridx = 1; constraintsJPanelCmd.gridy = 4;
			constraintsJPanelCmd.gridwidth = 4;
			constraintsJPanelCmd.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelCmd.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelCmd.weightx = 1.0;
			constraintsJPanelCmd.weighty = 1.0;
			constraintsJPanelCmd.insets = new java.awt.Insets(5, 9, 10, 8);
			getControlSettingsPanel().add(getJPanelCmd(), constraintsJPanelCmd);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlSettingsPanel;
}
/**
 * Return the ControlTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getControlTypeComboBox() {
	if (ivjControlTypeComboBox == null) {
		try {
			ivjControlTypeComboBox = new javax.swing.JComboBox();
			ivjControlTypeComboBox.setName("ControlTypeComboBox");
			ivjControlTypeComboBox.setPreferredSize(new java.awt.Dimension(85, 24));
			ivjControlTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlTypeComboBox.setMinimumSize(new java.awt.Dimension(85, 24));
			// user code begin {1}

			//Load default possibilites into control type combo box
			ivjControlTypeComboBox.addItem( PointTypes.getType( PointTypes.CONTROLTYPE_NONE) );
			ivjControlTypeComboBox.addItem( PointTypes.getType( PointTypes.CONTROLTYPE_LATCH) );
			ivjControlTypeComboBox.addItem( PointTypes.getType( PointTypes.CONTROLTYPE_NORMAL) );
			ivjControlTypeComboBox.addItem( PointTypes.getType( PointTypes.CONTROLTYPE_PSEUDO) );
			ivjControlTypeComboBox.addItem( PointTypes.getType( PointTypes.CONTROLTYPE_SBO_LATCH) );
			ivjControlTypeComboBox.addItem( PointTypes.getType( PointTypes.CONTROLTYPE_SBO_PULSE) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlTypeComboBox;
}
/**
 * Return the ControlTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getControlTypeLabel() {
	if (ivjControlTypeLabel == null) {
		try {
			ivjControlTypeLabel = new javax.swing.JLabel();
			ivjControlTypeLabel.setName("ControlTypeLabel");
			ivjControlTypeLabel.setText("Control Type:");
			ivjControlTypeLabel.setMaximumSize(new java.awt.Dimension(86, 23));
			ivjControlTypeLabel.setPreferredSize(new java.awt.Dimension(86, 23));
			ivjControlTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlTypeLabel.setMinimumSize(new java.awt.Dimension(86, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlTypeLabel;
}
/**
 * Return the JCSpinFieldCmdHrs property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldCmdHrs() {
	if (ivjJCSpinFieldCmdHrs == null) {
		try {
			ivjJCSpinFieldCmdHrs = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldCmdHrs.setName("JCSpinFieldCmdHrs");
			ivjJCSpinFieldCmdHrs.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjJCSpinFieldCmdHrs.setBackground(java.awt.Color.white);
			ivjJCSpinFieldCmdHrs.setMinimumSize(new java.awt.Dimension(55, 22));
			ivjJCSpinFieldCmdHrs.setEnabled(false);
			// user code begin {1}

			ivjJCSpinFieldCmdHrs.setDataProperties(
				new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(
					null, new Integer(0), new Integer(999999), null, true, null, new Integer(1), 
					"#####0.###;-######0.###", false, false, false, null, new Integer(0)), 
				new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
					new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), 
					new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldCmdHrs;
}
/**
 * Return the JCSpinFieldCmdSecs property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldCmdSecs() {
	if (ivjJCSpinFieldCmdSecs == null) {
		try {
			ivjJCSpinFieldCmdSecs = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldCmdSecs.setName("JCSpinFieldCmdSecs");
			ivjJCSpinFieldCmdSecs.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjJCSpinFieldCmdSecs.setBackground(java.awt.Color.white);
			ivjJCSpinFieldCmdSecs.setMinimumSize(new java.awt.Dimension(55, 22));
			ivjJCSpinFieldCmdSecs.setEnabled(false);
			// user code begin {1}

			ivjJCSpinFieldCmdSecs.setDataProperties(
				new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(
					null, new Integer(0), new Integer(59), null, true, null, new Integer(1),
					"#####0.###;-######0.###", false, false, false, null, new Integer(0)), 
				new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
					new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), 
					new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldCmdSecs;
}
/**
 * Return the JLabelCmdTimeOut property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCmdTimeOut() {
	if (ivjJLabelCmdTimeOut == null) {
		try {
			ivjJLabelCmdTimeOut = new javax.swing.JLabel();
			ivjJLabelCmdTimeOut.setName("JLabelCmdTimeOut");
			ivjJLabelCmdTimeOut.setText("Command TimeOut:");
			ivjJLabelCmdTimeOut.setMaximumSize(new java.awt.Dimension(87, 16));
			ivjJLabelCmdTimeOut.setPreferredSize(new java.awt.Dimension(87, 16));
			ivjJLabelCmdTimeOut.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCmdTimeOut.setMinimumSize(new java.awt.Dimension(87, 16));
			ivjJLabelCmdTimeOut.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCmdTimeOut;
}
/**
 * Return the JLabelControlOne property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlOne() {
	if (ivjJLabelControlOne == null) {
		try {
			ivjJLabelControlOne = new javax.swing.JLabel();
			ivjJLabelControlOne.setName("JLabelControlOne");
			ivjJLabelControlOne.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelControlOne.setText("Control One");
			ivjJLabelControlOne.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControlOne;
}
/**
 * Return the JLabelControlZero property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlZero() {
	if (ivjJLabelControlZero == null) {
		try {
			ivjJLabelControlZero = new javax.swing.JLabel();
			ivjJLabelControlZero.setName("JLabelControlZero");
			ivjJLabelControlZero.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelControlZero.setText("Control Zero");
			ivjJLabelControlZero.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControlZero;
}
/**
 * Return the JLabelHrs property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHrs() {
	if (ivjJLabelHrs == null) {
		try {
			ivjJLabelHrs = new javax.swing.JLabel();
			ivjJLabelHrs.setName("JLabelHrs");
			ivjJLabelHrs.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelHrs.setText("(Hours)");
			ivjJLabelHrs.setEnabled(false);
			// user code begin {1}
         
         ivjJLabelHrs.setText("(min.)");
         
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHrs;
}
/**
 * Return the JLabelSecs property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSecs() {
	if (ivjJLabelSecs == null) {
		try {
			ivjJLabelSecs = new javax.swing.JLabel();
			ivjJLabelSecs.setName("JLabelSecs");
			ivjJLabelSecs.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSecs.setText("(Seconds)");
			ivjJLabelSecs.setEnabled(false);
			// user code begin {1}
         
         ivjJLabelSecs.setText("(sec.)");
         
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSecs;
}
/**
 * Return the JPanelCmd property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCmd() {
	if (ivjJPanelCmd == null) {
		try {
			ivjJPanelCmd = new javax.swing.JPanel();
			ivjJPanelCmd.setName("JPanelCmd");
			ivjJPanelCmd.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanelControlString = new java.awt.GridBagConstraints();
			constraintsJPanelControlString.gridx = 1; constraintsJPanelControlString.gridy = 1;
			constraintsJPanelControlString.gridwidth = 5;
			constraintsJPanelControlString.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelControlString.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelControlString.weightx = 1.0;
			constraintsJPanelControlString.weighty = 1.0;
			constraintsJPanelControlString.ipadx = -15;
			constraintsJPanelControlString.ipady = -7;
			constraintsJPanelControlString.insets = new java.awt.Insets(2, 2, 6, 4);
			getJPanelCmd().add(getJPanelControlString(), constraintsJPanelControlString);

			java.awt.GridBagConstraints constraintsJLabelCmdTimeOut = new java.awt.GridBagConstraints();
			constraintsJLabelCmdTimeOut.gridx = 1; constraintsJLabelCmdTimeOut.gridy = 2;
			constraintsJLabelCmdTimeOut.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCmdTimeOut.ipadx = 47;
			constraintsJLabelCmdTimeOut.insets = new java.awt.Insets(9, 2, 10, 3);
			getJPanelCmd().add(getJLabelCmdTimeOut(), constraintsJLabelCmdTimeOut);

			java.awt.GridBagConstraints constraintsJCSpinFieldCmdHrs = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldCmdHrs.gridx = 2; constraintsJCSpinFieldCmdHrs.gridy = 2;
			constraintsJCSpinFieldCmdHrs.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldCmdHrs.ipadx = 2;
			constraintsJCSpinFieldCmdHrs.insets = new java.awt.Insets(6, 4, 7, 1);
			getJPanelCmd().add(getJCSpinFieldCmdHrs(), constraintsJCSpinFieldCmdHrs);

			java.awt.GridBagConstraints constraintsJLabelHrs = new java.awt.GridBagConstraints();
			constraintsJLabelHrs.gridx = 3; constraintsJLabelHrs.gridy = 2;
			constraintsJLabelHrs.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelHrs.ipadx = 3;
			constraintsJLabelHrs.ipady = -2;
			constraintsJLabelHrs.insets = new java.awt.Insets(10, 0, 11, 3);
			getJPanelCmd().add(getJLabelHrs(), constraintsJLabelHrs);

			java.awt.GridBagConstraints constraintsJCSpinFieldCmdSecs = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldCmdSecs.gridx = 4; constraintsJCSpinFieldCmdSecs.gridy = 2;
			constraintsJCSpinFieldCmdSecs.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldCmdSecs.ipadx = 4;
			constraintsJCSpinFieldCmdSecs.insets = new java.awt.Insets(6, 4, 7, 2);
			getJPanelCmd().add(getJCSpinFieldCmdSecs(), constraintsJCSpinFieldCmdSecs);

			java.awt.GridBagConstraints constraintsJLabelSecs = new java.awt.GridBagConstraints();
			constraintsJLabelSecs.gridx = 5; constraintsJLabelSecs.gridy = 2;
			constraintsJLabelSecs.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelSecs.ipadx = 8;
			constraintsJLabelSecs.ipady = -2;
			constraintsJLabelSecs.insets = new java.awt.Insets(10, 0, 11, 0);
			getJPanelCmd().add(getJLabelSecs(), constraintsJLabelSecs);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCmd;
}
/**
 * Return the JPanelControlString property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelControlString() {
	if (ivjJPanelControlString == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("Command Strings");
			ivjJPanelControlString = new javax.swing.JPanel();
			ivjJPanelControlString.setName("JPanelControlString");
			ivjJPanelControlString.setBorder(ivjLocalBorder1);
			ivjJPanelControlString.setLayout(new java.awt.GridBagLayout());
			ivjJPanelControlString.setFont(new java.awt.Font("dialog", 0, 14));

			java.awt.GridBagConstraints constraintsJLabelControlZero = new java.awt.GridBagConstraints();
			constraintsJLabelControlZero.gridx = 1; constraintsJLabelControlZero.gridy = 1;
			constraintsJLabelControlZero.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelControlZero.ipadx = 10;
			constraintsJLabelControlZero.ipady = 7;
			constraintsJLabelControlZero.insets = new java.awt.Insets(0, 11, 0, 6);
			getJPanelControlString().add(getJLabelControlZero(), constraintsJLabelControlZero);

			java.awt.GridBagConstraints constraintsJLabelControlOne = new java.awt.GridBagConstraints();
			constraintsJLabelControlOne.gridx = 1; constraintsJLabelControlOne.gridy = 2;
			constraintsJLabelControlOne.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelControlOne.ipadx = 12;
			constraintsJLabelControlOne.ipady = 7;
			constraintsJLabelControlOne.insets = new java.awt.Insets(0, 11, 6, 6);
			getJPanelControlString().add(getJLabelControlOne(), constraintsJLabelControlOne);

			java.awt.GridBagConstraints constraintsJTextFieldControlOne = new java.awt.GridBagConstraints();
			constraintsJTextFieldControlOne.gridx = 2; constraintsJTextFieldControlOne.gridy = 2;
			constraintsJTextFieldControlOne.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldControlOne.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldControlOne.weightx = 1.0;
			constraintsJTextFieldControlOne.ipadx = 273;
			constraintsJTextFieldControlOne.insets = new java.awt.Insets(3, 7, 6, 20);
			getJPanelControlString().add(getJTextFieldControlOne(), constraintsJTextFieldControlOne);

			java.awt.GridBagConstraints constraintsJTextFieldControlZero = new java.awt.GridBagConstraints();
			constraintsJTextFieldControlZero.gridx = 2; constraintsJTextFieldControlZero.gridy = 1;
			constraintsJTextFieldControlZero.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldControlZero.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldControlZero.weightx = 1.0;
			constraintsJTextFieldControlZero.ipadx = 273;
			constraintsJTextFieldControlZero.insets = new java.awt.Insets(0, 7, 2, 20);
			getJPanelControlString().add(getJTextFieldControlZero(), constraintsJTextFieldControlZero);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelControlString;
}
/**
 * Return the JTextFieldControlOne property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldControlOne() {
	if (ivjJTextFieldControlOne == null) {
		try {
			ivjJTextFieldControlOne = new javax.swing.JTextField();
			ivjJTextFieldControlOne.setName("JTextFieldControlOne");
			ivjJTextFieldControlOne.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldControlOne;
}
/**
 * Return the JTextFieldControlZero property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldControlZero() {
	if (ivjJTextFieldControlZero == null) {
		try {
			ivjJTextFieldControlZero = new javax.swing.JTextField();
			ivjJTextFieldControlZero.setName("JTextFieldControlZero");
			ivjJTextFieldControlZero.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldControlZero;
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
			ivjPhysicalPointOffsetCheckBox.setText("Physical Point Offset");
			ivjPhysicalPointOffsetCheckBox.setMaximumSize(new java.awt.Dimension(185, 27));
			ivjPhysicalPointOffsetCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
			ivjPhysicalPointOffsetCheckBox.setSelected(true);
			ivjPhysicalPointOffsetCheckBox.setPreferredSize(new java.awt.Dimension(185, 27));
			ivjPhysicalPointOffsetCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhysicalPointOffsetCheckBox.setMinimumSize(new java.awt.Dimension(185, 27));
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
 * Return the PointOffsetSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPointOffsetSpinner() {
	if (ivjPointOffsetSpinner == null) {
		try {
			ivjPointOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjPointOffsetSpinner.setName("PointOffsetSpinner");
			ivjPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjPointOffsetSpinner.setBackground(java.awt.Color.white);
			ivjPointOffsetSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}
			
			ivjPointOffsetSpinner.setDataProperties(
				new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(
					null, new Integer(0), new Integer(99999), null, true, null, new Integer(1), 
					"#####0.###;-#####0.###", false, false, false, null, new Integer(0)), 
				new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), 
				new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255),
					new java.awt.Color(255, 255, 255, 255))));
			
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
public Object getValue(Object val) {
	//Assume that commonObject is an instance of com.cannontech.database.data.point.StatusPoint
	com.cannontech.database.data.point.StatusPoint point = (com.cannontech.database.data.point.StatusPoint) val;

	Integer pointOffset = null;
	Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
	if( pointOffsetSpinVal instanceof Long )
		pointOffset = new Integer( ((Long)pointOffsetSpinVal).intValue() );
	else if( pointOffsetSpinVal instanceof Integer )
		pointOffset = new Integer( ((Integer)pointOffsetSpinVal).intValue() );


	if ( (getUsedPointOffsetLabel().getText()) == "" )
		point.getPoint().setPointOffset( pointOffset );
	else
		point.getPoint().setPointOffset( null );


	//get all the values in the Control Settings JPanel
	String controlType = (String) getControlTypeComboBox().getSelectedItem();
	Integer controlPointOffset = null;
	Object controlPointOffsetSpinVal = getControlPointOffsetSpinner().getValue();
	if( controlPointOffsetSpinVal instanceof Long )
		controlPointOffset = new Integer( ((Long)controlPointOffsetSpinVal).intValue() );
	else if( controlPointOffsetSpinVal instanceof Integer )
		controlPointOffset = new Integer( ((Integer)controlPointOffsetSpinVal).intValue() );

	Integer closeTime1 = null;
	Object closeTime1SpinVal = getCloseTime1Spinner().getValue();
	if( closeTime1SpinVal instanceof Long )
		closeTime1 = new Integer( ((Long)closeTime1SpinVal).intValue() );
	else if( closeTime1SpinVal instanceof Integer )
		closeTime1 = new Integer( ((Integer)closeTime1SpinVal).intValue() );

	Integer closeTime2 = null;
	Object closeTime2SpinVal = getCloseTime2Spinner().getValue();
	if( closeTime2SpinVal instanceof Long )
		closeTime2 = new Integer( ((Long)closeTime2SpinVal).intValue() );
	else if( closeTime2SpinVal instanceof Integer )
		closeTime2 = new Integer( ((Integer)closeTime2SpinVal).intValue() );

	point.getPointStatus().setControlType( controlType );
	if ( getControlInhibitCheckBox().isSelected() )
		point.getPointStatus().setControlInhibit( CtiUtilities.getTrueCharacter() );
	else
		point.getPointStatus().setControlInhibit( CtiUtilities.getFalseCharacter() );

	if( (controlType.equals(com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.CONTROLTYPE_LATCH))) ||
			(controlType.equals(com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.CONTROLTYPE_NORMAL))) ||
			(controlType.equals(com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.CONTROLTYPE_SBO_LATCH))) ||
			(controlType.equals(com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.CONTROLTYPE_SBO_PULSE))) )
	{
		point.getPointStatus().setControlOffset( controlPointOffset );
		point.getPointStatus().setCloseTime1( closeTime1 );
		point.getPointStatus().setCloseTime2( closeTime2 );
	}

	if( getJTextFieldControlOne().getText() != null
		 && getJTextFieldControlOne().getText().length() > 0 )
		point.getPointStatus().setStateOneControl( getJTextFieldControlOne().getText() );
	else
		point.getPointStatus().setStateOneControl(" ");
	
	if( getJTextFieldControlZero().getText() != null
		 && getJTextFieldControlZero().getText().length() > 0 )
		point.getPointStatus().setStateZeroControl( getJTextFieldControlZero().getText() );
	else
		point.getPointStatus().setStateZeroControl(" ");

/*	if (pointOffset.intValue() == 0)
		point.getPoint().setPseudoFlag( new Character('P') );
	else
		point.getPoint().setPseudoFlag( new Character('R') );
*/

	Integer cmdTimeOut = new Integer( (((Number)getJCSpinFieldCmdHrs().getValue()).intValue() * 60) + 
						((Number)getJCSpinFieldCmdSecs().getValue()).intValue() );
	point.getPointStatus().setCommandTimeOut( cmdTimeOut )	;

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

	getPointOffsetSpinner().addValueListener(this);
	getCloseTime1Spinner().addValueListener(this);
	getCloseTime2Spinner().addValueListener(this);
	getControlPointOffsetSpinner().addValueListener(this);
	getJCSpinFieldCmdHrs().addValueListener(this);
	getJCSpinFieldCmdSecs().addValueListener(this);

	// user code end
	getPhysicalPointOffsetCheckBox().addItemListener(this);
	getControlTypeComboBox().addActionListener(this);
	getControlInhibitCheckBox().addActionListener(this);
	getJTextFieldControlZero().addCaretListener(this);
	getJTextFieldControlOne().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointStatusPhysicalSettingsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(440, 318);

		java.awt.GridBagConstraints constraintsPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsPointOffsetLabel.gridx = 1; constraintsPointOffsetLabel.gridy = 2;
		constraintsPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetLabel.ipadx = 11;
		constraintsPointOffsetLabel.insets = new java.awt.Insets(9, 16, 6, 58);
		add(getPointOffsetLabel(), constraintsPointOffsetLabel);

		java.awt.GridBagConstraints constraintsPointOffsetSpinner = new java.awt.GridBagConstraints();
		constraintsPointOffsetSpinner.gridx = 1; constraintsPointOffsetSpinner.gridy = 2;
		constraintsPointOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetSpinner.insets = new java.awt.Insets(5, 101, 4, 7);
		add(getPointOffsetSpinner(), constraintsPointOffsetSpinner);

		java.awt.GridBagConstraints constraintsPhysicalPointOffsetCheckBox = new java.awt.GridBagConstraints();
		constraintsPhysicalPointOffsetCheckBox.gridx = 1; constraintsPhysicalPointOffsetCheckBox.gridy = 1;
		constraintsPhysicalPointOffsetCheckBox.gridwidth = 2;
		constraintsPhysicalPointOffsetCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPhysicalPointOffsetCheckBox.ipadx = 12;
		constraintsPhysicalPointOffsetCheckBox.insets = new java.awt.Insets(12, 16, 4, 227);
		add(getPhysicalPointOffsetCheckBox(), constraintsPhysicalPointOffsetCheckBox);

		java.awt.GridBagConstraints constraintsUsedPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsUsedPointOffsetLabel.gridx = 2; constraintsUsedPointOffsetLabel.gridy = 2;
		constraintsUsedPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUsedPointOffsetLabel.insets = new java.awt.Insets(6, 7, 5, 90);
		add(getUsedPointOffsetLabel(), constraintsUsedPointOffsetLabel);

		java.awt.GridBagConstraints constraintsControlSettingsPanel = new java.awt.GridBagConstraints();
		constraintsControlSettingsPanel.gridx = 1; constraintsControlSettingsPanel.gridy = 3;
		constraintsControlSettingsPanel.gridwidth = 2;
		constraintsControlSettingsPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsControlSettingsPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsControlSettingsPanel.weightx = 1.0;
		constraintsControlSettingsPanel.weighty = 1.0;
		constraintsControlSettingsPanel.ipadx = 27;
		constraintsControlSettingsPanel.ipady = 104;
		constraintsControlSettingsPanel.insets = new java.awt.Insets(5, 16, 10, 7);
		add(getControlSettingsPanel(), constraintsControlSettingsPanel);
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
		connEtoC7(e);
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
		PointStatusPhysicalSettingsEditorPanel aPointStatusPhysicalSettingsEditorPanel;
		aPointStatusPhysicalSettingsEditorPanel = new PointStatusPhysicalSettingsEditorPanel();
		frame.add("Center", aPointStatusPhysicalSettingsEditorPanel);
		frame.setSize(aPointStatusPhysicalSettingsEditorPanel.getSize());
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
	//Assume defaultObject is an instance of com.cannontech.database.data.point.StatusPoint
	com.cannontech.database.data.point.StatusPoint point = (com.cannontech.database.data.point.StatusPoint) val;

	getUsedPointOffsetLabel().setText("");
	usedPointOffsetsVector = new java.util.Vector();

	Integer pointOffset = point.getPoint().getPointOffset();

	if( pointOffset != null )
		getPointOffsetSpinner().setValue( pointOffset );

	if( pointOffset != null )
	{
		if (pointOffset.intValue() == 0)
			getPhysicalPointOffsetCheckBox().setSelected(false);
		else
			getPhysicalPointOffsetCheckBox().setSelected(true);
		getPointOffsetSpinner().setValue( pointOffset );
	}
	else
	{
		getPhysicalPointOffsetCheckBox().setSelected(false);
		getPointOffsetSpinner().setValue( new Integer(0) );
	}

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
				break;
		}
	}

	//do all the setting for the values in the Control Settings JPanel
	String controlType = point.getPointStatus().getControlType();
	Integer controlPointOffset = point.getPointStatus().getControlOffset();
	Integer closeTime1 = point.getPointStatus().getCloseTime1();
	Integer closeTime2 = point.getPointStatus().getCloseTime2();

	CtiUtilities.setSelectedInComboBox( getControlTypeComboBox(), controlType );

	getControlPointOffsetSpinner().setValue( controlPointOffset );
	getCloseTime1Spinner().setValue( closeTime1 );
	getCloseTime2Spinner().setValue( closeTime2 );

	getJTextFieldControlZero().setText( point.getPointStatus().getStateZeroControl() );
	getJTextFieldControlOne().setText( point.getPointStatus().getStateOneControl() );

	getControlInhibitCheckBox().setSelected( point.getPointStatus().getControlInhibit().equals(CtiUtilities.getTrueCharacter()) );

	//set the text for the state zero label and state one label the their proper state text
	//Load all the state groups
	int stateGroupID = point.getPoint().getStateGroupID().intValue();
	cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		LiteStateGroup stateGroup = (LiteStateGroup)
			cache.getAllStateGroupMap().get( new Integer(stateGroupID) );

		java.util.List statesList = stateGroup.getStatesList();
		
		//Select the appropriate rawstate
		int currentRawState = 0;
		for( int y = 0; y < statesList.size(); y++ )
		{
			if( ((com.cannontech.database.data.lite.LiteState)statesList.get(y)).getStateRawState() == 0 )
				getJLabelControlZero().setText( ((com.cannontech.database.data.lite.LiteState)statesList.get(y)).getStateText() );
				
			if( ((com.cannontech.database.data.lite.LiteState)statesList.get(y)).getStateRawState() == 1 )
				getJLabelControlOne().setText( ((com.cannontech.database.data.lite.LiteState)statesList.get(y)).getStateText() );
		}
	}

	
	Integer cmdTimeOut = point.getPointStatus().getCommandTimeOut();
	if( cmdTimeOut != null )
	{
		getJCSpinFieldCmdHrs().setValue( new Integer(cmdTimeOut.intValue() / 60) );
		getJCSpinFieldCmdSecs().setValue( new Integer(cmdTimeOut.intValue() % 60) );
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
	D0CB838494G88G88G69F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8FDCD56579B895B9AAD6D6D8245428A859469A9695A5AB4630A8ED61C6E50BEDEEE34B1AEBD46C13ABDBC740ABAAAAAA86969A26B5DC087C9749B590C4AC8C16E8D42E3CF08FF071F26F755EF36102183F67FD5F733C675CF34F3D002D6F2F7979BC1E73BC6F1F674F7B3E4F7B3CFF4EC548FFA3FC79324CFCC1D836DC107F6DD9A68801978942D31FA43C0F34GB141A0287EED83D8AF3C54168D4F
	2910D67ACC02A1CF583AA387708840C3FF11E0A800770D42769D0BA4F87141A79E520C177FF41DFFBE616FA01F95948F5B148B4F35G11C0A34F8C11700F59D4A8536749F404E5190230281764F9F8D311CC2B833C83A082A0361765CF0667CA4A79427ABAB95F714B43050C877F1E390654232A93213C0F156D141F45428F17AD6DA33C16D0B9C919E04093810872D9424657DA61392F63E45FFBBD3641329E2355EA33CAE2CFFF19E8B2CBB6C7195DE636CA75759D64F1C4B2CAAEE7C77F08535CE3349C91A549
	EC6DF39E2099BB0CD651626EA73C9ADE4D21F58BC2836059E6C2ABF3917EF10027GAC66743FD0FAAB3C1FGF01BD167FF7EFD1F2CD357699F8ACB9F33E62D732173D4B353B5562C689C62FAF145E65ABE8BCBC9BBE4C0DA61G748D008EGBB40D40057E93FF8721DFF403331DBEA328D8E5A2C0DF67BFE0B49F158E692AD70DEDF8FA9B26DA3314766B08982EB7B3FEDAA5511479F30F2392FCE369C33C9E4957603280FEE96D2EF7BA0F3050ECDD2A34BB3A38718AD6287941BB05E87FC72DEB86059CFBDF9FF
	AC28F9AF1EE24BDA2E43BB66111D1999F2BDB9A7955EF5A7C9DB5FC05B3A845E17497D035123A8FDCCC5E7FD361A52899E8CE969A731EFF42DC5D95A32278BF995ED3AF688CDE84C6C34B099AC962DAC03C3246ED08B6339064BB2C669F1AABA1365A5AA8B4197C2DA5BE00201752F274A48980E85BC85E0B1C036B5413083208120530AFD2C3CF477847BD80751A16DF719ADA651A13032CF153E8ACF51ADB90C072CCE49E86D910F999DE6E337C5F48AF21E67CA098D3ABD4A3DC07BFE8F70B8A8DAC507D1B25B
	2CD03BF1D014C407136591F62DA7F99CA2CD6DB48F0A40E050AEE03DB93BB6927B991D52D13B49A809CA8E161E370B344910719854889540BB334B2B6544DE92689F8C90466DF0892D6F8351815E223E3E4DE6BB6932F792CD0445D6E2676354F7C443FB1295ED3C2102504AED8906D69B561330EB032A1EE6F38F915F689841B19669A2FE7A388D7B4CB3DD58E71A575CA414BE723EEE1F217E44467ACA24CDBBF6EE25F5923998DE6F6A52A9FF5C26BF0EA68B0C4791DD9ED97F057A5772368A28A4ED158CF2AF
	864835230DDF5ED54C7A6CFE23D344DE5A6E008EA61A940BB3B91DC3057278A076DEAE4B3F85E0B55765FC1AE787545F86A0810481A482AC3623CD178D1DD04907752C146BD9B30EBEDBA6E55BA28FDE48E7394CE7BC9B2F1EA0AFE64B674B487CA58E09D629D9340BD6933CB86534974B88DFF9FC775AF899E1B7215B419FB441GF4F23AE1B79933C6C9F2183BDD120873B0CFDF3B1B70F23A3A6D0AABC1DE1BF6937DEC43608FF87E57F6133154E333DAC55A283C081CDEC44732E89187C168A61B4BAAB17A1B
	65E4BC1C94C7E4DF72F6B98918060C969771CBAC3E08B692BB599D66C168AA4435896ED3E41CBBDFADD459A89FE8A4CEEA036709D3B8E6073763185DB1FA3D907F45DB19FAF3CE68CD4D1971275818CBB9250CD9A6D3C1053E6CDB28CF1169CEA6EB1C69EDF8CEAC524131F3D79529BF837866B8928C458E9CBB57D410F474CF7220E932D9AC4C445C76D3AA73C8DB0927DCA238F56288BF60767CF66586CD5AB15936ACFDDAA5312DC565C367B908EDE307F8DC89F887005D416C9B65E076E179CE8FE33EC2E702
	219C204329172F510D79C2A1BD9AA0D94E17A72973AD9F41FCA7006E868815746AFB0067DBG6969GF9AE3DFC4123182F8D52CDAE6FFC2C5DD6FCC1E29A3D98364FE5AEF3C9E6B2C398A598DCF617240AD3FFC9679AD2C7208B635197B653799F70940045AE6C0B7357E0BC9AF2B2DCD8F863EBBEE3706CA1A6DB491052970BAF40F8B42B06706C07B4779076153CAA751C5868F45AFA4CF4BA165B795EAAF5DFE534442AD5706CB5BB1C52A1F0A6EE6C938FD691F949FCAD13657865B7B42EA13A86835FC4003A
	E19A87C979AA59178E31F9F78724E5517ADA07C95CE2164441B2729F9997E2D33F515AA71ACA860CC346B26330D4C65DDA59A1C8BFC05EF64A6D1238F38313D12416E781C4494EE398D35E3031C166BB6F1669423E339BB375E27C20F5DBB2830665B9F9D83169582D062C451F3F34026997B10C7AE5C01ED2B76A17592F564F64E67A053AD13F556EFA74D9E574FFD40E742413163E32019E62FBE9AFE2994A5EEF2243D0D6D5366F009B79C7C15DE91C3F4B433EC5A38906E6007E9164BF5FDD89CFA3F58AE56C
	51A1BAFAED0EC1EF83B752E4644BECBCE76BFE663BA5074D52B9E297A13E6C366D37393FE854D456663B36F7C91BB0BD4AC6D00FB110B1E6847528766AA7C40F45DC0FE9A3672A87637B9C67AB0C824FD164FBDB2E1A2F9D6821GF123487713D152BFFB0C8ED1AA237F4B3D0C30F417B90741ED4BDC1BC82A4C14713CFD94F9A6C3FDB92711E7C03B9E4F065398237C56EB8D55F9DA09797ACFFB47FFECEDF172B53A4EB94D56962BB9FD045243D4F4A65F4B549791BC8E520A47B09E3E22164C1D010007834482
	A4832483641DC11F72D1F515B04EFA639078C80795E6B70FC081637E9AE2DF185BE5BC37E6B90E93816327A70734F6A8BD6329FB2B86E7657E62D52ECC03DBB465D8DFFF6F9811057AF9D57356573F5F54AF761C04BEAFB03F9EA165D0BEE023B3683F4F34287DF75C3A4A4C55BA3E0640A219F74E170A2FA9657B897B6A481C548AE9A600B100D000B80085GCB3FC47BDFD5734F716CCF7BE2272D89E82DB007B7E963AFEE2F6F8D1F1B1DE33D4AE96DDCF4D64F4EDF563EAD2866D315976CB50B9693B178913B
	595AC290C136795EFEE2F3E26B6A33B8CE6C6EF3137BCB2FF2DB357DC3D76EBE0B2F7EA17BC4B976DCBD2C0EBD834F32B9A1921EF1GCC1FA88975C9617AD40F1C1BBE7B3D4AE95BA1B740339C1361A4150174539D81A84312F31441B0BA1E8C478374EC7AD6C9113ECDBBE13DC2FBG33695EAA2DCDA528AF94A0AAGED7AF4AE46DB09DC1F59DEF2CDCC1F874655E7292ECD6FECC799724F03790B6EB97C154601ED001F603444C2C28B84BC6CBC147554EBE4DE0887BC95E0A9400E73E19EBC9F534F52F45379
	F2AC8DB1EA6079EC1EE469D92FE95772242C567F2C15F9685155C0F8239C0ADD0A3CF878DA27CEE48FC13D16A7B873EDB3EAFD2FE5EF2855DF4BC6566A2FE5EF29DD4F68D007FA678B536F28753F563D27D6EF2D3B056A77402B6BE85DCE2768244E16AC826434A725CD024E1A77C36320A85BE62330F5F8251C6700E4EB5A43ED7CDDC9EFCFA6C2EE4BD879B9CF55368B61BD83A067824FF6AD823CDCC3E3734CCF76123E548CE9A7G5CGD38122AF407925F34F38737B81371F793D254EF79B9E2E53EF43A3F5
	E44D6334399CBD22361DBE2DAB64E94ACCC616844424ACCF8FADAF999DFD22D7BB8F5072AC4DD7F9E71D2F3E405A6BB79E6DD5455BAB420C3EA2D9E36BB44017G6486FA52D9FDB35D6A7AAA8474094F6FA4326A78C4555A32E918788FE26362C0DCDB462EA171DCA060E1GB1016897E6C9A85FBC20AD54481281F84E05FA721D9CD14BD7497535537A0A64B2657233D9FEF649CF0B2ACED65F6ED1F5FD55704EFC7BFF1CDA5FCEFCDAG148F3990FD7B29AA1CAF23AEC4FDFFDFCD74CD83FCA9C07E9454774F39
	7E4EBFCAA1DF4394BDFD1F73102F066B9B576E2F3ECE282BDF373E3C8232DFE5B10E58DC9244FDD3084CFFAFC0DD47G8F810883C81E225EEF18B5CCFCEA2F43B8884DFE44AE7688095FA2E51FA844326500B78318G8281A23E056B95C1A07BBDF40F293D3757A9CAED46EE5142520E9C5B6019C622AC3AD147527FF0120C835C14D2656371AF4B17B8C8FC47D127E85267F3E3AFC9EFE2EBC69CC5346F32754FADAB546B1F5CA0969F1587617A68BFD5FA694DC1B8F74E25F1CB97607D1CD66EA2B48970E0CE7B
	F3A52105839E47E99F53FE1484F8AA2705D3DABA603997A1ED9665D18CF8B927559E45B93B19530AA95F904023AEC25FB9F72FEF5F77333D7A3EEF41DE7D796BD7FB7D4FDFFF586B4BE7B179E61B09FC71A0DBAA175976815597701C0B11561F093A95F15A3B54AE964083AF463155BA2C1E1BC2019EF531527763AF560FABD2003EC8133694703CCBBCE9EC3DFAE2AD9957BBAEE16B55CD1C6E2474AE951D2DE36F21FE0F604110D6970C72A751B6CC83DC78B6525E23FDA29F709D1C7617CD28FB9D2755D35D03
	G8F6734EF517AE2GCF6234791416CAF0CE8B6AA4B461D2F83F94E9FF6A64766534A7E9D98B60C117221F1B664478B164D274F3C3747CE49E60E9GCBAF4536982AD27B257C4B928CA5G7BAEE3B66D384C57BE41A7B524BE53E54C0EABB93D175283D4F4E65FBFD2BFCB70B0C89326227C06B528130953CE35D19B811E44E9BFEF47FC291CF6BC977315DE8EBAD1BE6D85F8072081E86E4B718EC145890FB5825969292FBF68B01B769B7B5A28F7E5363B3AA0C7AE9FFDB95AEE8E2DF391604295890642ABF03D
	6EFE9B576BD1B7CF975A82BE505DEFDD58D919D9FE05FC67618AE53D9E896BF546F359E664B99552132EA0345D1B557E0B7844B319EC7D40520D9E69E47C6E945469D7FA2493F9CFE5295367F824AFD715E73A4DFD8BF5DBBAB7D2305C3E5767DEF25E15CC27EA7959F1EC3B30636217D648F1C82F0FB8047AFA267F6FD7907DC97D16AB318F07D4A1AD79CA1C17FEAB11339C899CDFF5883977525DA888B86F9CF41179EE5068B68F3A860F18C7C5A1BA04547385752DEEF88F8C41FA8CD524DE3681C8C2D32355
	C46427F5CE5F2F54E925B2B8C5F29EA21AD8FD97D6613D118CDE5F1D34BE3BC36C959D8E51C4398BB27FE41ABFFB9A3CCF437CC9D40FDE185BD81EDF51B13C8F52DB81C2C9DE617051E69DDB2B7BC31600F3BD6887E588EC175BA8F89A5B07DB46F559436CE336EA6C53CE6D339072AF66727D4C6CDB1FBF52392070AA70FDD7E17EA99F10FD7EEEE34F49BE074DC50C4972DEC767088E48F77CAA546BEC66F8FAADD7692755EB6CAA66D7943DBE2A5257EB0079DE489FF2957A0B819A6F46831E42E9F31A88ED89
	6039D7A32D72GBD4B873C15532EF063BCF01C53FA28CF32839E40E97B789C9842E93F2D45F9A06DEA743B3DEBCB88DF4073C3E14EG68843083CC850881C885D88AD0F48D169921E52A816F8410GC22EE1E7DD649CAB865ED3GB22EE17B9A2CCC621AF09F36A63C4913704B177990FE5AFCB1AA1ADD164BD77B5579C9237E4FC73A9CF37B1495778156487DE3134062D4ED3EF52A775582730B3EE4D961A78DFD26AFD9B68A5FF57B9EA34F52711CBFBD41D0BC9D47481CA14FB15FC7E308B6C86F1A0EE3A377
	D5FFE3C3BDAE8884CDD74686C98B833C4AE3EC7C35D6FFEC745031918979E338FC57C95E3E6EF689672505BC5FA20D6F7A9B4D138169428C4C731DCECFBF7D5BA6C08526E37638FEED398A34633FFC06724EF87DCD726D4B179F90F85F1A6E542E3F764DD07AE2478CDFF14EB3B4EEB24D505E41FA195203E6E86FE00DD5629D2CA8C8AB8CC39F70EDEA73C040C3B94DC14FD963GCF6634E7292F48GBC7BDA245DD700BE2570DA9C57DF5410B5DABF60C1G91G09BC4DC25396811EF39DBB13A66757AC6D59EA
	F56CCD4AEA75CE146BD3537AF572799E472BEA54BC167B98DB5A71B7517BC22C9FEDD9B31931D282F658F71DB2D6BA48FB263A9FED2BA57208D6F2C541A40F1369ED587F0FDF07E374697671782AF95BB5FC83D5FC65F8A21747FE57E19B9DADB837BB1BBD67D8AEEC52655898383A107475086B14B9195143A9BDDAC5E7E3E3AE0F4D63A02D78BBF8D63868GBDAB84BC9CA096A099A09DA0B79C5743E3756542B8FB091DB6FB9BAC18AD2AA52EFC2E30795C6E327EF8854640B1F75FA044CE796BB2DF67F14561
	ECFCB4484FC51D5BA91D69F77AA0514F8EE941G51GC9G29G99DC3F447AD2269F393F53A8C9461EFEFA138215BBE6960795B25950E13ABD3DC2EF1FB767FAA6C751757A63369C684D1A34AE40AD9A9A63F1CD159E0FGB9EF088F9E11C00F5324A5831E2621B1DBBF7C197A7EF85E74ED1958F73475AE114BE75F202C25183F3F639029090D67344AB760E995070B7941FE3399CCC7AE0016D454551496D76E59A9738E9FD6732EDB35C7778E976D77B24FB67999772D6DB23F30AABA67B9DCA213257883C516
	1B250948424A6D6CD04A7D63681B5AF2ADB071163CE9B29BAD363EB7CB0B4BCB0BAB2AE16176064062A13AE7A07FE375357DDB60B67ECDAE7667226F4F901A9F7B6BB21CCB35FA864A7A2528EC3CE8E0698AD667CB9FA9B2B6D4CDDC372D1DCAB95387DBFD172BA145AAF7228E8FFC2C14BBC1BE0A90F819A61B4566A89B6EB7CB325D97F7A93A2F7BD4A957A1CD049F4BFB5EB059F36D36B968A6995BF38D65673139B7425A8760B8007DC6364F4C4A25B6F96F55BE5F046502EEE4FB3561700C3911591515BBF4
	C0FF6DEA246B10F910F781G23A550F5C8FE84784F8864BB5A6E9913CD256B12B6C86F0A40F96B48386BAEB256F16E8A02F2E191EC5EA2F8B43C13332579824EDB6B7750588568C9911E7B46CD4ABD9F55FE2EA094A8E95AADDF817310783D49E2F352AB5C95FE52AABD53B6FA7265E769727ADCFAD5C9BFE46DB7F71B156DE8B94F32FFEC56150FEFEF4B795E6FDA6FA123B67D4F2FFA24D7EA53DF20F75F58A9CC53A009D565BBC4EB357B5524DFE5C460FE760D55FA69B9B7E1FA4428DEFA91CFBFD42717DE4E
	530DAE6D9DB4125E4C5303A43D74AE1E7E1AEE3A855259D82C7CCC990BD515FE46A2F59955FC6CDF739F255CD31B4E5D9F267F0B55F77F670AFFF87A8B256E82FF7E014B44724E1348B869B5DA1CA22FCB6CD66A9A6B1C281C2CECD08FA9AB1F99503D5BG30DB48CD1E6792A47E0F3D89E3440CCA62D7F2EECEB014GB48318G46G42EE66E779743E83399796833494008C007C1950A6B3B14F7915A7B43E005461AFB68AD3251BE45EDE71070AD6426539560B57F8775355F76975568C683B709D7D57C6A122
	4A5B074F6677BFBAE7B29FEE01E740CCF6F70E153B28465B070757E03950194C07C743B35123DC4E9ECF9F3C157A47D44833E8A67A60DF96CCE4CFEF357CAC0CCCB014C6AABE38815EFF774AAB2FBC8F40F8E6FDC6FC8D5BC915BD5727224346ED703B42BCEDFA3BD51477A38A48F8D5EE48E8531766EC52CB176BE7E3BE3CC66BD308BD5AA251A7D89A7552CFC03A5E78749D432C941531B52BCDBD76FC0F7352DE25CCD82E3A0C5C97C9134A6FB72B5E679768453A7FAD10E3762F1587DBCFBCFB407779611283
	7A6707FFBB20FFFE185D627FFECBEE0B6FEFB9B636283F65E06B212574ECD702FEB095A086A089A0B59257C25737907FC93EB673DE879DB075113B9B648BB38979879E742D6BA587477FAEAD64A05D3F36C912ED10540F746987C9FBC9B6BBA5B25AF78E929BB94CFD7D924BAA53EFBAC84F6144DECAE6FEBBB7076EAD03DEB95F65E747AB892D8870EACE8B6FA534D640CD1C56300B1EDBG9E44E93B1A78FDAFCE6BFD035096831E7DBD244D26FBCD050017F05A9F683ED2B560ED1CF6EA0850CEGBE46E9F32A
	89EDAA60611C3675AC720D613426E374DC637B90E3FC9F57711B7B85EE0F1A563760F94CB8A02F5459E7A9580A6F433C585EBDA076C81D46BE5EAF326859C2B9545776FD6C8BAF9DFA47D7BDC06CF0586C22C39AE1D4FA65DD2E6BDF834457DA201EA000A800C40085G69G59D18906C200D200E60063GF600C000D000C800B80064A8146372FF6F74A307FC10C8BB2643E8A6DDD35E73089F466FC353202E229F60F96413DBC8FD7E3F8769B4CB96F13FBC0549F783F35CF8365B7983BC1F5C50C4F884839E81
	907783BC8BFD3D9977DBD20016FB8B1E2D75B4631ED373ADD84765B46F9860A1GD137E05C365DA28868C7AFF01789BEE3244A0A6ABD55F5D53555FBA0D6626FBC46E999D46A69EAAF76DDCF55DE929CD5566CE4655E33AB654AB337E94B31382A91863485422A8AD21258F5E2738B4E9546D384E36F018E055F0CB59311D33EB73A964FAB93391DFFD10D772BCAFE0834A7C611964CE981F5C8CB6734D0975E2B6D0CC65A3D7C2EEDB0277D1B53922379792253BB0679B53F9F35B41A45B0793340BF4C126F4052
	786420EB3C78E4258CB49E01321D33147824FF1676EC6EC09E31373C5611EF72409CE26D1353BF352B6213C193D9AF353BA41656D6C3E39B6592B7E4E8F5A877686ABFDF494B2A697D5D9B3C4A9D91FB1C187EA8BD2BE465981D45AAC9ED5AD80558C51A0531CC0CC13B3EA169C1BC3DC552AB9F4653DB3D56D7A4BD1A27176916CF1C05E379F7D568C796GAD6FD6EC7FF72A71BC3A75D69C4B77523B9681000782C45F0AF5A45520BFC8821AF09B5611D70B7E206EB62C633DDA12578EF890C078ED688F525D4A
	78383B602B2C47FC0F470D2384E332BDD603F776E2393CB7B6A2AD7DB6744F59FD3EE30055FD6347G79FD747BB5183B49868223ED6EA36348C67781B05F563EBCCEBB646C30B94D64640959E3FB8E1EFB31B540DEBA7F1744006DE2F0ECDAD76A6F75CC21F1C18764BB4E735E596B67DE810D9902A0EF884F7F71AE4FB548A00DA162A1FDDE8C0E7166262FF27E3EB4067DCE09B2464F3C21FF46B80D46A5B937031F391D5FAB2872BC97ECBC2D742517EB756D32092E239A200EB6DECF5010EF3BDCCFE39AB764
	8D6479D32BBD6D729C9DB351109EF7BB5A25666CE44EF534F6C93FDD6B7BBEBD26EF97870D1B16C27E1CBB306F6618898F9EDF33B0C66EEF6B4C44AFC0B5920CA973A87D6412B9D1793E73006FFE7F6940787D3EE7C0BF76B58FF8473E56817D58D79AD0473E0CB6B2C06C5587F1D1974F77A527FD42E99750BA896DE333C96A67740B28DC045EAA9206C83F0C4AB5CCE9DD983F2BE81F4834EB86485885157BED8EACFB5DG6D87E60BC57E8E0066B1DBE1A8F3FB4C24B21A6DC6155CD11CC664E6EB387B3C3EDB
	52C2759DFAE7A9A9631653FEC7444A2D215F9BF55CB131F3D89364E3E7F1D3E8EC683EC3FBC6F79925875E21BD23ABEC42B9E2AA24E55C01FBB75B1A4F6DECF177B9166B1FF4B92657728374FE671DDAFD57D23A69CE2D3ED77398598EE9A9F7727B5FEB755668F5317279CF2CFEBB1CG3A24C98B82BCACD62FAF7C3ADA0FC7341CB751870F85C0DF2CC9936E82BF8BD0F217C2E7BC960E6A7158A7676B38CB1F078968EECDDAB06061GB1DEBCAE2C536311A467CB7541A39D6859F7FB269582DE8A50F0371647
	35AE6C8F1D1AB27D000F81CC850870AA17483FDB0855141B8778C2008C001C595AF247F83922591E654A81EF866882302852D8FFDAA1E1FF8A00345859983334733BB46933B146BAC3731644013EGDDF1935D9743B849B4C6B0D67FABED38063047A14FBF9AF0ED1042E9E778B70339BFC25AD93E56E866B411679B7B910E09272A4E4D673DB0EEB9FD1FD7DEBD999F9073A374F9D5B44ECD7A1156876C2374149FE9FD401CEAEC33051056FC8F6A7B531AF373DD2967D8EED36D39793CDBE92C9EF90FD65F5914
	9EF30FD65F66DA54B79E52F65C0B7AFA4A9DF4AFEB4BB079998D4F44FB755AB732D1DDEE011CE7317C946241B745FB16E37D77DBE7C832FCBE3B12745DB76949DBB1BBFA2BFFD34E773DAF15FC53FA7D1C536C290375C6AD5FF3F8682CD22EE657444B45E4AA6536B5CD6EAC67B1D2CCFEFFB1DB296777FD93673F4520146B9D72FB3E8E4CAB4B71FC3DE105D26E6E6A09171B39D2A9377E6C5F7D69DB56ED3318641D16EAAC7F60AAC567BF2DD66AFAE6C23233EF9D63B712185EEC35182DE2937BB5B0814F44F6
	50BD2F9268C38D71387FB8FDBF21F5825E9F0F7DB7185EB3E46F53D46FDB1B3E7EBD7DBD7F87BCD62B6EB3662B5E2FED7E7AF95F713572E0ED3AFE9D7D7E8A5AB31037734D6B70AEF6A827ED2A413B58D11CF64B90FE13934FE92BBE46FD6B94CE5BBC0CF731F77C98E94BEA10EF9D27F557A05FE6CE5BBF04FC3BB82D348A79DAB86D175D48B700537E389779A6F3DAAB5D8BCC83FC8927E51EC6D9F29310F65DE914251853FE3D87E529663453A3A8CBAB275D5508329C673434C214A50A531E5B033244F35A4C
	BA14A505536A7622AC0BB86D4A9114E5A927BD3987E5497B8952AEBD03326C60347BBBD196095336CAA8CB9027455098A58C70D8CEFB115E211E87F89A2731FB7A044792CE9B26ED541A18E0B811485B0D7210G8F6234A7A8AD8C70D8CE7B9E6381F89A2799949E1C769F1A2FF58E701803341FB8110744E90314C79060911CD6C44B46821E4CE98F8EA10FB4CE0B223F87D6FDDF02216DBEAE8B67F162BE7403EB24F30BBFDEBF47F21D13AE476213E7E97CAB5D270DDBDE2474007B34F14B85BC368E0634F42E
	EF76E1BD5F54F9BF0BB97A6F57DFFF0C81FD2AA6AD8270D800F977EB5783698654F12126CC8660B9C98906A20072A4ED3973BDEC53AC27F749CF8BBC831274E22AC03E5E895124C7829E87108C106645EF40035F92B9BD7781799E9FBC2B9F702C0F752303F4CF2C95520EBF00F65DCBBEDF465F87D13D4F55DD377EF7671B347F839E31350ACEBFD63D3FDF7B75736E79DAF930B6FD1E7D0E9B34277B8174970FD35F9F8CF898273D3799FDCDA4272D22E782645B4AA51CF6AF1DCBF29F043E7EA07F66F7BD1D8F
	GEF60340756E3FDED1C36339D6B8BFB9077E55F253FDDA011D3CE92E38A3807FB047A57E8489747734E5A3F9C738A98EB3ECEC2C6B956ACD96739777E9E0D9B96C379A58F625D4907A9ADFFAE4C23F311769E35D19B60DD1C7684733D00CF1D0B7B435BBE1E485D9DDF7B4371F395DF46F86C27F148BC20AF180BE7AB6F112A6474CB28DC597360FD9E4AF5124A358F70D6CEBBCA67B6897000F9A8EBC637BFD9D70CA3EBB454734BD7DEF9C511B53F1E5E7584FA52BCEC0FAFC63D3F17989A45BD0E8CAE5FBBB4
	F649F98856008FA16DFE964FG3E0F53F2E82C518F3844E991B42E8887BC6AA154AD39F1BC5D7C6D5327C2BD3BBD5AE18D6D7B0B001E41793EDE07FA947F94E94914D68DF8832799E9ACF49C70FECE0B23B207G9E4EE9F9346CBC40977C947538694CF8FA2C7023C74E43DABD52E99C158F746207116FC13E675476B0365BDC176F3313B49AE31D003C961EFFFD157E371DBBE93F8B05FC918F23CE5FF57B53E925603F5FA5BE4C3E2BD64ED0AE7571ED674D14F7B264CF65F2B6C95E7D71D32EFFCEB266FBCE72
	2D7FEA2AFF9164AD6179FFDC292F7F63B4E66B04FCA712D17FAD636A6F2FEF86A4EB7517AA75758F263C03895FE4EC6B45CE54B5114B9E68C757EB29ED96C05EB41E3F4A072E9FD0FEF973416F4FC7DDFFB3B41E2E7E7A6F3E79DADDFFD2212F6B93B466ED057C1D73D1575DDC570079982B3CC0268FF9EE95DC4A7BC53A3F03745F1D4BEF78DAF93079736D43542F003E9173F1CE3D04522281CF62346B6819F3AA60CBB86DA57A9DC16E4F207DFE46573C5BF10EAE6734939F92DAG60211C56C5771023G0F67
	349574FB7D944053B96D936A6332FF8EBCFE0E34420D1C87273DC647EEG60211C76B4ED4FA84063B96D9A5ABFD3GCF6334C16A6316G1E1302341F715F4448673445342C89F0B7274D20E3BB9870C8CECB207DBA8E7094CE2B63EB3BC51C360D4ADC77C8022163914E174AEC825C4DE967D13E410007F15AC9AADFB460711C164CE51E2754C74B96BD8A36FA94476367555EB10F117E6D8F163E2A5673DC3B1846EC4DD03E035711DE68F9669EC82EAF485FD09E5DBC1918E61F6ADBC6BA76008739A3CC8412E3
	29C69C17411C7F230D1EB2AE26F942A1BD0A67B93B5ED346196B95995356CF663E02D64654C7595DC4C5460F5AD146C50F72EF9372951BDE3AFF82F74C48F62F5C9EC7B7A9E53F330E6EE14BD71D0A49C5923E076DFED555EEB57E3EB528ADAD2E2C62FB41B38A14F2B78ECD3C5C8B1B15F2FFBBB639BD6CBBC8E8A13FBFDE2E54B325FA627C17D6A86576785D872E14B73FD939B72A14F25B7CFEDB66D96E12DA255C7C6E4969BBD627687B1D3DCABD7F9C1D88FF16775B235E5F04BCD42F54F575E956B74807C1
	456426DBF1856F9B696FAA79D2767833719E0FF3020D7FD44A59C7A6DE4E76AF255C8F9AA7E72BAB9A94DB5D2E7A7D15A7A7A4B74BDBD967ED2B852A5FDD29582B35D5A53755CBCDCA3E20C9683C7500D2AEF592B63E7020D26E7CB31333D5E42BE22B392AEFD3B77A7D564853D61F6BFCEB744C7BCADDFF7677AD1566BB4A4EB6255C45EE7F5FD18E9FD6E43F69C355181C44F71427F4E4CFEAD7794BC948DE76EF15BF9B76AF7B710F94594F9ED14A45BAA7AEFB160E6CB7283EDFDDBF8959F30EA96592067C4B
	7E4E470A6C9FF6A965DEF2CCDC769627DAF659179D761CE3CFFD2254FD43E17D3BF7F75378F2A94C13B90F61DCFDFB0E6FB545CFE96CD98DF99BF87E8DAD1E73FFC98B49E301F477E33816A85C7ED56EA4C6C2BDDF886A354470077AEB094BE8FC9B83796339FCB603773AF10A81E30445BC5F56953E755EC363E1E1C102A1F781663757692F2502E81CDC87791A97207E3BB7FE153B0726856C1B82C57F8A9F6B66C3B40E35C37EB1AE67B21DF5F3A1DFB7C772FC33B53F7B42FE33AE8E5213F81EC04D6F3E0450
	6F2F190ECFF5FE15FD1BA58B58BA6FE5655BB717375C2F39D06E6C47D126DB7D6CF74C25798BA16F8E1E7FF19FEB60E769DA2183729DFF9C5B2D7DAB2D77479E572E016BFC34DB877BADAB489F4265FC4A692D3F405BAD1E678B55345BADECDD85698BF91E73B56DF667E7CA3BBD69375DD60F23E35EAF928C6BBD5A6DA91D7EA670F6AB7A854664052A6F779EAA1C483C2B785BF77B1432C79BF57C38D9C9BF3EFEF2F54F99D04A1A5B3D6BDEF4D2C93FB2E762F146EA5577B16987A6DE0EFE0EA9177B4B1B130B
	CF426937B56C7DA72714FA8E9BA69E1FDCEB7036419FD45F50FCD8B771F932CDD24A65ED74BFCF5ADC0A6CD78CA965FEBD09B9FE374E9C3FEDD4294BB8216F3895791BD57759ABDC5EF5770DA969F7CEA2FED8203AA7D5BECE5C7652170ADD8887ACF77FA46CD220E3178BD5F7223E1824DDEE4BD24A96F858057D4D5385ADD3B68AC2541F0465123089FFBA38D8FD95CC92DE7773134212308E575C722FBB784F6FF4889B3C2D50F869630FC5FD511C1FA9E49E928C7D74B679A1E179B039166E46BFB6F8177314
	7025104DB10730464386F32E2F79433CDF9DF3E68906C342E662F60F088EFAF3DFB411DFF6177F84E10B30127E4DD262648F8959CE7AD86E14C6ACE21B105F7B9179F3DACEC9B4B562B781CE308C7D63B83B4927013BDB0D4EFE093850F75B04ECAB79E309428616ECB1DA7BF6338F845E25D2AFEBBCC13B81BDDB82F9B7D2F9691F8A6235EC1C3D6E2DED6F2745E68919AD42AA0B4DE8EAB176C8B647A1E125546F901D7DB60B49ED7FD7832DA8071EDC512F54213A57397AC459B2723DB8977A247B5A60B50B56
	34C057F1BB045DB4A719E54AF88ED3D33F51812CC447A1097C71C71BE387CFBBEF2B7476C3878B0EE78A4BE4A50969D73A6D12E068B58E1AADA37042FA9CED14B592C86BB0DA1D648F7A00E4459A49B009B3A83B720172E95FFD71D34AC0A9AA89EFB0D3C2515DFD22EDF0F7E3EF2F59AA728A402ED07D96DEBD49D326493341FD7926779FF9259B7AC13310B5D8DECE7F2F207FD7127FABA82502D2AAAA413869025FFF5AF4B76ACCBB8F48330E4AC31A3F2C85DAD07A451F9E386109FD690BC13B96E1B97D23B8
	5088F9CA2E8FDD035D22E3CA5434AD2BC396DEC25A48CE3A0A1B543B5E4DEBA49A9E221FC8AC9C2A2A4F7A5D0DD919C2B68C90764988F4DE725D87F4DE879BAFD9124DCE7B9D99A3E42F71F7G391AD1039859935B831C56FA7A33E5B795DC10F7D915ABD348E9A1EDED31779B8F49BA34892B3BA9AB5181F9A5A1CFE9ABDA9686C4E68F0C165795FA8F213354A3F6E38F99B3ABCDC647C9DA8391E57BE7424ADEC7DFB7E5A089191F35FC86ECA9C6125FEA93D6C97D642BF352F9A521C0293A03FC6BABA73061A6
	C024730A1D7E23EAEE15CD013FBC30DC23BCE2646B4C2394DB61B5E889F5357768A364F5FC8C90EC834D1ACF7FAF18F40FA225B653322F730E304B4D3540736BCCBA723F61F257A8F293BB5333D152921C1AA937C7BE1714D08DAA2A2C63D9E8CF82DCE3180C17CDD1E94C7423B713FCE81C0D9AB7FBF8AB837BE209C9CE4A7CF7CD51A0638D0AA94852A6CF50F6C42DC213E842662F3189BFF0FBB48BE9B0A2ED26A42CE653F9B1796B819E3A11659411916208B59EB749392EC47B8FF530EFD4677D58D3F3A2A5
	59C9DE69C38B52D9E3F4B02DCE936C1E84EBA47FB5FDBD5A99956D704F7610FD820327866059B126435F9B019887C99D3CE5E96CC7FF763518C5BD10AD973311DF2B016E02E83BD57CA69B468A016C9A0F43A3EE9292A79F53413ED15DF6D8E9D8B2D8F3643CD9BE34A01DB8C0877BC669F41E22935EDFB1F868C465A66D26BE5871C87DD8877BC669986CB9A0D147ECCF39BDF52C7C9F5371EA4FC990F54C742105921B11152EDECC07B53CA628FB03E7CCB7FEAD4BD435E41EF3AD4BD535AC73ACC75DA8F30F72
	4FEB0047DCD152EAF3B91CBBE141D4F2C46C31D9CD4E1D3A814D7F17793FC146EFD11ACC10799A38CC5FA8C9EF771C2FBDA57D1F99A277F88E919A668B5A28E3B2C304E8C7F252CE2ED36E9B55068FAA5A2F1673916DD71D033E197F8B7ABE622DAF79378AFFABF867D975EFA2AF39305067EFA25B1FE15F9384BE237CD6B07B8D2E676879E1BC50D31F415F3A58FFF817607BF7410E8C9AAD9649E8B959E8B5115F885BBFA2094A6F4C3DF018546F44BCF2227C5BF507095C832AB27F8FD0CB8788DFF579E87CA9
	GG2C09GGD0CB818294G94G88G88G69F854ACDFF579E87CA9GG2C09GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB6AAGGGG
**end of data**/
}
}
