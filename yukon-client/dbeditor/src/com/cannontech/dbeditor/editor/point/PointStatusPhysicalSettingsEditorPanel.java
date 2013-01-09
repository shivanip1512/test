package com.cannontech.dbeditor.editor.point;

import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.yukon.IDatabaseCache;

public class PointStatusPhysicalSettingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjPointOffsetLabel = null;
	private com.klg.jclass.field.JCSpinField ivjPointOffsetSpinner = null;
	private javax.swing.JCheckBox ivjPhysicalPointOffsetCheckBox = null;
	private Vector<LitePoint> usedPointOffsetsVector = null;
	private javax.swing.JLabel ivjUsedPointOffsetLabel = null;
	private javax.swing.JLabel ivjCloseTime1Label = null;
	private com.klg.jclass.field.JCSpinField ivjCloseTime1Spinner = null;
	private javax.swing.JLabel ivjCloseTime2Label = null;
	private com.klg.jclass.field.JCSpinField ivjCloseTime2Spinner = null;
	private javax.swing.JCheckBox ivjControlInhibitCheckBox = null;
	private javax.swing.JLabel ivjControlPointOffsetLabel = null;
	private com.klg.jclass.field.JCSpinField ivjControlPointOffsetSpinner = null;
	private javax.swing.JPanel ivjControlSettingsPanel = null;
	private javax.swing.JComboBox<String> ivjControlTypeComboBox = null;
	private javax.swing.JLabel ivjControlTypeLabel = null;
	private javax.swing.JLabel ivjJLabelControlOne = null;
	private javax.swing.JLabel ivjJLabelControlZero = null;
	private javax.swing.JTextField ivjJTextFieldControlOne = null;
	private javax.swing.JTextField ivjJTextFieldControlZero = null;
	private javax.swing.JPanel ivjJPanelControlString = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldCmdMins = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldCmdSecs = null;
	private javax.swing.JLabel ivjJLabelCmdTimeOut = null;
	private javax.swing.JLabel ivjJLabelMins = null;
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
@Override
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
@Override
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
			getPointOffsetSpinner().setValidator( new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)) );
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

	boolean value = ! controlType.toString().equalsIgnoreCase(StatusControlType.NONE.getControlName());

	getControlInhibitCheckBox().setEnabled(value);
	
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
	getJLabelMins().setEnabled(value);
	getJLabelSecs().setEnabled(value);
	getJCSpinFieldCmdSecs().setEnabled(value);
	getJCSpinFieldCmdMins().setEnabled(value);
	
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
private JComboBox<String> getControlTypeComboBox() {
	if (ivjControlTypeComboBox == null) {
		try {
			ivjControlTypeComboBox = new JComboBox<String>();
			ivjControlTypeComboBox.setName("ControlTypeComboBox");
			ivjControlTypeComboBox.setPreferredSize(new java.awt.Dimension(85, 24));
			ivjControlTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlTypeComboBox.setMinimumSize(new java.awt.Dimension(85, 24));

			//Load default possibilites into control type combo box
			for (StatusControlType statusControlType : StatusControlType.values()) {
			    ivjControlTypeComboBox.addItem(statusControlType.getControlName());
			}
		} catch (java.lang.Throwable ivjExc) {
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
 * Return the JCSpinFieldCmdMins property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldCmdMins() {
	if (ivjJCSpinFieldCmdMins == null) {
		try {
			ivjJCSpinFieldCmdMins = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldCmdMins.setName("JCSpinFieldCmdMins");
			ivjJCSpinFieldCmdMins.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjJCSpinFieldCmdMins.setBackground(java.awt.Color.white);
			ivjJCSpinFieldCmdMins.setMinimumSize(new java.awt.Dimension(55, 22));
			ivjJCSpinFieldCmdMins.setEnabled(false);
			// user code begin {1}

			ivjJCSpinFieldCmdMins.setDataProperties(
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
	return ivjJCSpinFieldCmdMins;
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
 * Return the JLabelMins property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMins() {
	if (ivjJLabelMins == null) {
		try {
			ivjJLabelMins = new javax.swing.JLabel();
			ivjJLabelMins.setName("JLabelMins");
			ivjJLabelMins.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMins.setText("(min.)");
			ivjJLabelMins.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMins;
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

			java.awt.GridBagConstraints constraintsJCSpinFieldCmdMins = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldCmdMins.gridx = 2; constraintsJCSpinFieldCmdMins.gridy = 2;
			constraintsJCSpinFieldCmdMins.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldCmdMins.ipadx = 2;
			constraintsJCSpinFieldCmdMins.insets = new java.awt.Insets(6, 4, 7, 1);
			getJPanelCmd().add(getJCSpinFieldCmdMins(), constraintsJCSpinFieldCmdMins);

			java.awt.GridBagConstraints constraintsJLabelMins = new java.awt.GridBagConstraints();
			constraintsJLabelMins.gridx = 3; constraintsJLabelMins.gridy = 2;
			constraintsJLabelMins.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelMins.ipadx = 3;
			constraintsJLabelMins.ipady = -2;
			constraintsJLabelMins.insets = new java.awt.Insets(10, 0, 11, 3);
			getJPanelCmd().add(getJLabelMins(), constraintsJLabelMins);

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
			ivjJTextFieldControlOne.setDocument(new TextFieldDocument(TextFieldDocument.STRING_LENGTH_100));
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
			ivjJTextFieldControlZero.setDocument(new TextFieldDocument(TextFieldDocument.STRING_LENGTH_100));
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
@Override
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

	point.getPointStatusControl().setControlType( controlType );

	if (point.getPointStatusControl().hasControl())
	{
		point.getPointStatusControl().setControlOffset( controlPointOffset );
		point.getPointStatusControl().setCloseTime1( closeTime1 );
		point.getPointStatusControl().setCloseTime2( closeTime2 );
		
		point.getPointStatusControl().setControlInhibited( getControlInhibitCheckBox().isSelected() );

		if( getJTextFieldControlOne().getText() != null
			 && getJTextFieldControlOne().getText().length() > 0 )
			point.getPointStatusControl().setStateOneControl( getJTextFieldControlOne().getText() );

		if( getJTextFieldControlZero().getText() != null
			 && getJTextFieldControlZero().getText().length() > 0 )
			point.getPointStatusControl().setStateZeroControl( getJTextFieldControlZero().getText() );

		Integer cmdTimeOut = new Integer( (((Number)getJCSpinFieldCmdMins().getValue()).intValue() * 60) + 
							((Number)getJCSpinFieldCmdSecs().getValue()).intValue() );
		point.getPointStatusControl().setCommandTimeOut( cmdTimeOut );
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

	getPointOffsetSpinner().addValueListener(this);
	getCloseTime1Spinner().addValueListener(this);
	getCloseTime2Spinner().addValueListener(this);
	getControlPointOffsetSpinner().addValueListener(this);
	getJCSpinFieldCmdMins().addValueListener(this);
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
 * Helper method to determine if the pointOffset is already in use
 * @param pointOffset - Offset to check
 * @return - True if offset is used by another point
 */
private boolean isPointOffsetInUse(int pointOffset) {

    if (this.usedPointOffsetsVector != null && this.usedPointOffsetsVector.size() > 0) {

        for (LitePoint point : this.usedPointOffsetsVector) {

            if (point.getPointOffset() == pointOffset) {
                return true;
            }
        }
    }

    return false;

}

@Override
public boolean isInputValid() {
    if (getPhysicalPointOffsetCheckBox().isSelected()) {
        Object value = this.getPointOffsetSpinner().getValue();

        if (value instanceof Number) {
            Number numValue = (Number)value;
            if (this.isPointOffsetInUse(numValue.intValue())) {
                setErrorString("Status Point Offset " + numValue.intValue()
                               + " is in use for this device");
                return false;
            }
        }
    }

    return true;
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
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
			Class<?> aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
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
@Override
public void setValue(Object val) 
{	
	//Assume defaultObject is an instance of com.cannontech.database.data.point.StatusPoint
	com.cannontech.database.data.point.StatusPoint point = (com.cannontech.database.data.point.StatusPoint) val;

	getUsedPointOffsetLabel().setText("");

    List<LitePoint> points = DaoFactory.getPointDao()
                                           .getLitePointsByPaObjectId(point.getPoint().getPaoID());
        usedPointOffsetsVector = new Vector<LitePoint>(points.size());
        for (LitePoint currPoint : points) {
            if (point.getPoint().getPointID() != currPoint.getPointID()
                    && point.getPoint()
                            .getPointType()
                            .equals(PointTypes.getType(currPoint.getPointType()))) {
                usedPointOffsetsVector.add(currPoint);
            }
        }

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

	//do all the setting for the values in the Control Settings JPanel
	String controlType = point.getPointStatusControl().getControlType();
	Integer controlPointOffset = point.getPointStatusControl().getControlOffset();
	Integer closeTime1 = point.getPointStatusControl().getCloseTime1();
	Integer closeTime2 = point.getPointStatusControl().getCloseTime2();

    SwingUtil.setSelectedInComboBox(getControlTypeComboBox(), controlType);

	getControlPointOffsetSpinner().setValue( controlPointOffset );
	getCloseTime1Spinner().setValue( closeTime1 );
	getCloseTime2Spinner().setValue( closeTime2 );

	getJTextFieldControlZero().setText( point.getPointStatusControl().getStateZeroControl() );
	getJTextFieldControlOne().setText( point.getPointStatusControl().getStateOneControl() );

	getControlInhibitCheckBox().setSelected( point.getPointStatusControl().isControlInhibited() );

	//set the text for the state zero label and state one label the their proper state text
	//Load all the state groups
	int stateGroupID = point.getPoint().getStateGroupID().intValue();
    
    IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		LiteStateGroup stateGroup = cache.getAllStateGroupMap().get( new Integer(stateGroupID) );

		List<LiteState> statesList = stateGroup.getStatesList();
		
		//Select the appropriate rawstate
		for( int y = 0; y < statesList.size(); y++ )
		{
			if( statesList.get(y).getStateRawState() == 0 )
				getJLabelControlZero().setText( statesList.get(y).getStateText() );
				
			if( statesList.get(y).getStateRawState() == 1 )
				getJLabelControlOne().setText( statesList.get(y).getStateText() );
		}
	}

	
	Integer cmdTimeOut = point.getPointStatusControl().getCommandTimeOut();
	if( cmdTimeOut != null )
	{
		getJCSpinFieldCmdMins().setValue( new Integer(cmdTimeOut.intValue() / 60) );
		getJCSpinFieldCmdSecs().setValue( new Integer(cmdTimeOut.intValue() % 60) );
	}
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
@Override
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
@Override
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
}
