package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.device.*;
import com.cannontech.database.db.*;
import com.cannontech.database.db.device.*;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class DeviceMeterGroupEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.KeyListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjAreaCodeGroupLabel = null;
	private javax.swing.JLabel ivjCycleGroupLabel = null;
	private javax.swing.JCheckBox ivjLastIntervalDemandRateCheckBox = null;
	private javax.swing.JComboBox ivjAreaCodeGroupComboBox = null;
	private javax.swing.JComboBox ivjCycleGroupComboBox = null;
	private javax.swing.JComboBox ivjLastIntervalDemandRateComboBox = null;
	private javax.swing.JLabel ivjLastIntervalDemandRateLabel = null;
	private javax.swing.JCheckBox ivjChannel1CheckBox = null;
	private javax.swing.JCheckBox ivjChannel2CheckBox = null;
	private javax.swing.JCheckBox ivjChannel3CheckBox = null;
	private javax.swing.JCheckBox ivjChannel4CheckBox = null;
	private javax.swing.JComboBox ivjLoadProfileDemandRateComboBox = null;
	private javax.swing.JLabel ivjLoadProfileDemandRateLabel = null;
	private javax.swing.JPanel ivjLoadProfileCollectionPanel = null;
	private javax.swing.JPanel ivjDataCollectionPanel = null;
	private javax.swing.JLabel ivjMeterNumberLabel = null;
	private javax.swing.JTextField ivjMeterNumberTextField = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceMeterGroupEditorPanel() {
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
	if (e.getSource() == getCycleGroupComboBox()) 
		connEtoC5(e);
	if (e.getSource() == getAreaCodeGroupComboBox()) 
		connEtoC6(e);
	if (e.getSource() == getChannel2CheckBox()) 
		connEtoC1(e);
	if (e.getSource() == getChannel1CheckBox()) 
		connEtoC2(e);
	if (e.getSource() == getChannel3CheckBox()) 
		connEtoC7(e);
	if (e.getSource() == getChannel4CheckBox()) 
		connEtoC8(e);
	if (e.getSource() == getLoadProfileDemandRateComboBox()) 
		connEtoC9(e);
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
	if (e.getSource() == getMeterNumberTextField()) 
		connEtoC12(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (Channel2CheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC10:  (CycleGroupComboBox.key.keyTyped(java.awt.event.KeyEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.KeyEvent arg1) {
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
 * connEtoC11:  (AreaCodeGroupComboBox.key.keyTyped(java.awt.event.KeyEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.KeyEvent arg1) {
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
 * connEtoC12:  (MeterNumberTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (Channel1CheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (DemandIntervalRadioButton.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if ( getLastIntervalDemandRateCheckBox().isSelected() )
		{
			getLastIntervalDemandRateLabel().setEnabled(true);
			getLastIntervalDemandRateComboBox().setEnabled(true);
		}
		else
		{
			getLastIntervalDemandRateLabel().setEnabled(false);
			getLastIntervalDemandRateComboBox().setEnabled(false);
			getLastIntervalDemandRateComboBox().setSelectedIndex(0);
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
 * connEtoC4:  (DemandIntervalComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (CycleGroupComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC6:  (AreaCodeGroupComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC7:  (Channel3CheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC8:  (Channel4CheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC9:  (LoadProfileDemandRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
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
 * Return the AreaCodeGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getAreaCodeGroupComboBox() {
	if (ivjAreaCodeGroupComboBox == null) {
		try {
			ivjAreaCodeGroupComboBox = new javax.swing.JComboBox();
			ivjAreaCodeGroupComboBox.setName("AreaCodeGroupComboBox");
			ivjAreaCodeGroupComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
			ivjAreaCodeGroupComboBox.setEditable(true);
			ivjAreaCodeGroupComboBox.setMinimumSize(new java.awt.Dimension(200, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAreaCodeGroupComboBox;
}
/**
 * Return the AreaCodeGroupLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAreaCodeGroupLabel() {
	if (ivjAreaCodeGroupLabel == null) {
		try {
			ivjAreaCodeGroupLabel = new javax.swing.JLabel();
			ivjAreaCodeGroupLabel.setName("AreaCodeGroupLabel");
			ivjAreaCodeGroupLabel.setText("Alternate Group");
			ivjAreaCodeGroupLabel.setMaximumSize(new java.awt.Dimension(114, 16));
			ivjAreaCodeGroupLabel.setPreferredSize(new java.awt.Dimension(114, 16));
			ivjAreaCodeGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAreaCodeGroupLabel.setMinimumSize(new java.awt.Dimension(114, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAreaCodeGroupLabel;
}
/**
 * Return the Channel1CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel1CheckBox() {
	if (ivjChannel1CheckBox == null) {
		try {
			ivjChannel1CheckBox = new javax.swing.JCheckBox();
			ivjChannel1CheckBox.setName("Channel1CheckBox");
			ivjChannel1CheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjChannel1CheckBox.setText("Channel #1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel1CheckBox;
}
/**
 * Return the Channel2CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel2CheckBox() {
	if (ivjChannel2CheckBox == null) {
		try {
			ivjChannel2CheckBox = new javax.swing.JCheckBox();
			ivjChannel2CheckBox.setName("Channel2CheckBox");
			ivjChannel2CheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjChannel2CheckBox.setText("Channel #2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel2CheckBox;
}
/**
 * Return the Channel3CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel3CheckBox() {
	if (ivjChannel3CheckBox == null) {
		try {
			ivjChannel3CheckBox = new javax.swing.JCheckBox();
			ivjChannel3CheckBox.setName("Channel3CheckBox");
			ivjChannel3CheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjChannel3CheckBox.setText("Channel #3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel3CheckBox;
}
/**
 * Return the Channel4CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel4CheckBox() {
	if (ivjChannel4CheckBox == null) {
		try {
			ivjChannel4CheckBox = new javax.swing.JCheckBox();
			ivjChannel4CheckBox.setName("Channel4CheckBox");
			ivjChannel4CheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjChannel4CheckBox.setText("Channel #4");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel4CheckBox;
}
/**
 * Return the CycleGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getCycleGroupComboBox() {
	if (ivjCycleGroupComboBox == null) {
		try {
			ivjCycleGroupComboBox = new javax.swing.JComboBox();
			ivjCycleGroupComboBox.setName("CycleGroupComboBox");
			ivjCycleGroupComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
			ivjCycleGroupComboBox.setEditable(true);
			ivjCycleGroupComboBox.setMinimumSize(new java.awt.Dimension(200, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleGroupComboBox;
}
/**
 * Return the CycleGroupLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCycleGroupLabel() {
	if (ivjCycleGroupLabel == null) {
		try {
			ivjCycleGroupLabel = new javax.swing.JLabel();
			ivjCycleGroupLabel.setName("CycleGroupLabel");
			ivjCycleGroupLabel.setText("Data Collection Group");
			ivjCycleGroupLabel.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjCycleGroupLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjCycleGroupLabel.setPreferredSize(new java.awt.Dimension(140, 16));
			ivjCycleGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCycleGroupLabel.setMinimumSize(new java.awt.Dimension(140, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleGroupLabel;
}
/**
 * Return the DataCollectionPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDataCollectionPanel() {
	if (ivjDataCollectionPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Data Collection");
			ivjDataCollectionPanel = new javax.swing.JPanel();
			ivjDataCollectionPanel.setName("DataCollectionPanel");
			ivjDataCollectionPanel.setBorder(ivjLocalBorder1);
			ivjDataCollectionPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCycleGroupLabel = new java.awt.GridBagConstraints();
			constraintsCycleGroupLabel.gridx = 0; constraintsCycleGroupLabel.gridy = 1;
			constraintsCycleGroupLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCycleGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCycleGroupLabel.insets = new java.awt.Insets(3, 0, 3, 0);
			getDataCollectionPanel().add(getCycleGroupLabel(), constraintsCycleGroupLabel);

			java.awt.GridBagConstraints constraintsAreaCodeGroupLabel = new java.awt.GridBagConstraints();
			constraintsAreaCodeGroupLabel.gridx = 0; constraintsAreaCodeGroupLabel.gridy = 2;
			constraintsAreaCodeGroupLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAreaCodeGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAreaCodeGroupLabel.insets = new java.awt.Insets(3, 0, 3, 0);
			getDataCollectionPanel().add(getAreaCodeGroupLabel(), constraintsAreaCodeGroupLabel);

			java.awt.GridBagConstraints constraintsCycleGroupComboBox = new java.awt.GridBagConstraints();
			constraintsCycleGroupComboBox.gridx = 1; constraintsCycleGroupComboBox.gridy = 1;
			constraintsCycleGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCycleGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCycleGroupComboBox.insets = new java.awt.Insets(3, 10, 3, 0);
			getDataCollectionPanel().add(getCycleGroupComboBox(), constraintsCycleGroupComboBox);

			java.awt.GridBagConstraints constraintsAreaCodeGroupComboBox = new java.awt.GridBagConstraints();
			constraintsAreaCodeGroupComboBox.gridx = 1; constraintsAreaCodeGroupComboBox.gridy = 2;
			constraintsAreaCodeGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAreaCodeGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAreaCodeGroupComboBox.insets = new java.awt.Insets(3, 10, 3, 0);
			getDataCollectionPanel().add(getAreaCodeGroupComboBox(), constraintsAreaCodeGroupComboBox);

			java.awt.GridBagConstraints constraintsMeterNumberLabel = new java.awt.GridBagConstraints();
			constraintsMeterNumberLabel.gridx = 0; constraintsMeterNumberLabel.gridy = 0;
			constraintsMeterNumberLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMeterNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMeterNumberLabel.insets = new java.awt.Insets(3, 0, 3, 0);
			getDataCollectionPanel().add(getMeterNumberLabel(), constraintsMeterNumberLabel);

			java.awt.GridBagConstraints constraintsMeterNumberTextField = new java.awt.GridBagConstraints();
			constraintsMeterNumberTextField.gridx = 1; constraintsMeterNumberTextField.gridy = 0;
			constraintsMeterNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMeterNumberTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMeterNumberTextField.insets = new java.awt.Insets(3, 10, 3, 0);
			getDataCollectionPanel().add(getMeterNumberTextField(), constraintsMeterNumberTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataCollectionPanel;
}
/**
 * Return the LastIntervalDemandRateCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getLastIntervalDemandRateCheckBox() {
	if (ivjLastIntervalDemandRateCheckBox == null) {
		try {
			ivjLastIntervalDemandRateCheckBox = new javax.swing.JCheckBox();
			ivjLastIntervalDemandRateCheckBox.setName("LastIntervalDemandRateCheckBox");
			ivjLastIntervalDemandRateCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjLastIntervalDemandRateCheckBox.setText("Specify Last Interval Demand Rate");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLastIntervalDemandRateCheckBox;
}
/**
 * Return the DemandIntervalComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getLastIntervalDemandRateComboBox() {
	if (ivjLastIntervalDemandRateComboBox == null) {
		try {
			ivjLastIntervalDemandRateComboBox = new javax.swing.JComboBox();
			ivjLastIntervalDemandRateComboBox.setName("LastIntervalDemandRateComboBox");
			ivjLastIntervalDemandRateComboBox.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLastIntervalDemandRateComboBox;
}
/**
 * Return the DemandIntervalLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLastIntervalDemandRateLabel() {
	if (ivjLastIntervalDemandRateLabel == null) {
		try {
			ivjLastIntervalDemandRateLabel = new javax.swing.JLabel();
			ivjLastIntervalDemandRateLabel.setName("LastIntervalDemandRateLabel");
			ivjLastIntervalDemandRateLabel.setText("Last Interval Demand Rate:");
			ivjLastIntervalDemandRateLabel.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjLastIntervalDemandRateLabel.setPreferredSize(new java.awt.Dimension(175, 16));
			ivjLastIntervalDemandRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjLastIntervalDemandRateLabel.setEnabled(false);
			ivjLastIntervalDemandRateLabel.setMinimumSize(new java.awt.Dimension(150, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLastIntervalDemandRateLabel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getLoadProfileCollectionPanel() {
	if (ivjLoadProfileCollectionPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Load Profile Collection");
			ivjLoadProfileCollectionPanel = new javax.swing.JPanel();
			ivjLoadProfileCollectionPanel.setName("LoadProfileCollectionPanel");
			ivjLoadProfileCollectionPanel.setBorder(ivjLocalBorder);
			ivjLoadProfileCollectionPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsChannel1CheckBox = new java.awt.GridBagConstraints();
			constraintsChannel1CheckBox.gridx = 0; constraintsChannel1CheckBox.gridy = 1;
			constraintsChannel1CheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsChannel1CheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getLoadProfileCollectionPanel().add(getChannel1CheckBox(), constraintsChannel1CheckBox);

			java.awt.GridBagConstraints constraintsChannel2CheckBox = new java.awt.GridBagConstraints();
			constraintsChannel2CheckBox.gridx = 1; constraintsChannel2CheckBox.gridy = 1;
			constraintsChannel2CheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsChannel2CheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getLoadProfileCollectionPanel().add(getChannel2CheckBox(), constraintsChannel2CheckBox);

			java.awt.GridBagConstraints constraintsChannel3CheckBox = new java.awt.GridBagConstraints();
			constraintsChannel3CheckBox.gridx = 0; constraintsChannel3CheckBox.gridy = 2;
			constraintsChannel3CheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsChannel3CheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getLoadProfileCollectionPanel().add(getChannel3CheckBox(), constraintsChannel3CheckBox);

			java.awt.GridBagConstraints constraintsChannel4CheckBox = new java.awt.GridBagConstraints();
			constraintsChannel4CheckBox.gridx = 1; constraintsChannel4CheckBox.gridy = 2;
			constraintsChannel4CheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsChannel4CheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getLoadProfileCollectionPanel().add(getChannel4CheckBox(), constraintsChannel4CheckBox);

			java.awt.GridBagConstraints constraintsLoadProfileDemandRateLabel = new java.awt.GridBagConstraints();
			constraintsLoadProfileDemandRateLabel.gridx = 0; constraintsLoadProfileDemandRateLabel.gridy = 0;
			constraintsLoadProfileDemandRateLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getLoadProfileCollectionPanel().add(getLoadProfileDemandRateLabel(), constraintsLoadProfileDemandRateLabel);

			java.awt.GridBagConstraints constraintsLoadProfileDemandRateComboBox = new java.awt.GridBagConstraints();
			constraintsLoadProfileDemandRateComboBox.gridx = 1; constraintsLoadProfileDemandRateComboBox.gridy = 0;
			constraintsLoadProfileDemandRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLoadProfileDemandRateComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getLoadProfileCollectionPanel().add(getLoadProfileDemandRateComboBox(), constraintsLoadProfileDemandRateComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileCollectionPanel;
}
/**
 * Return the LoadProfileDemandRateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getLoadProfileDemandRateComboBox() {
	if (ivjLoadProfileDemandRateComboBox == null) {
		try {
			ivjLoadProfileDemandRateComboBox = new javax.swing.JComboBox();
			ivjLoadProfileDemandRateComboBox.setName("LoadProfileDemandRateComboBox");
			ivjLoadProfileDemandRateComboBox.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileDemandRateComboBox;
}
/**
 * Return the LoadProfileDemandRateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLoadProfileDemandRateLabel() {
	if (ivjLoadProfileDemandRateLabel == null) {
		try {
			ivjLoadProfileDemandRateLabel = new javax.swing.JLabel();
			ivjLoadProfileDemandRateLabel.setName("LoadProfileDemandRateLabel");
			ivjLoadProfileDemandRateLabel.setText("Load Profile Demand Rate:");
			ivjLoadProfileDemandRateLabel.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjLoadProfileDemandRateLabel.setPreferredSize(new java.awt.Dimension(175, 16));
			ivjLoadProfileDemandRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjLoadProfileDemandRateLabel.setEnabled(true);
			ivjLoadProfileDemandRateLabel.setMinimumSize(new java.awt.Dimension(150, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileDemandRateLabel;
}
/**
 * Return the MeterNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMeterNumberLabel() {
	if (ivjMeterNumberLabel == null) {
		try {
			ivjMeterNumberLabel = new javax.swing.JLabel();
			ivjMeterNumberLabel.setName("MeterNumberLabel");
			ivjMeterNumberLabel.setText("Meter Number");
			ivjMeterNumberLabel.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjMeterNumberLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjMeterNumberLabel.setPreferredSize(new java.awt.Dimension(100, 16));
			ivjMeterNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMeterNumberLabel.setMinimumSize(new java.awt.Dimension(95, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeterNumberLabel;
}
/**
 * Return the MeterNumberTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMeterNumberTextField()
{
    if (ivjMeterNumberTextField == null)
    {
        try
        {
            ivjMeterNumberTextField = new javax.swing.JTextField();
            ivjMeterNumberTextField.setName("MeterNumberTextField");
            // user code begin {1}
            ivjMeterNumberTextField.setDocument(
                new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_METER_NUMBER_LENGTH));

            // user code end
        }
        catch (java.lang.Throwable ivjExc)
        {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjMeterNumberTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	DeviceMeterGroup dmg = null;

	//The default object is either a MCT or a IEDmeter
	if( val instanceof MCTBase || val instanceof IEDMeter )
	{
		DeviceLoadProfile dlp = null;
		
		if( val instanceof MCTBase )
		{
			dmg = ((MCTBase) val).getDeviceMeterGroup();
			dlp = ((MCTBase) val).getDeviceLoadProfile();
		}		
		else if( val instanceof IEDMeter )
		{
			dmg = ((IEDMeter) val).getDeviceMeterGroup();
			dlp = ((IEDMeter) val).getDeviceLoadProfile();
		}
				
		if( getLastIntervalDemandRateLabel().isEnabled() )
		{
			String lastIntervalDemandRateString = (String)getLastIntervalDemandRateComboBox().getSelectedItem();
			int lastIntervalDemandRateLength = lastIntervalDemandRateString.length();
			Integer lastIntervalDemandRate = new Integer( String.copyValueOf(lastIntervalDemandRateString.toCharArray(),0,lastIntervalDemandRateLength-7) );
			dlp.setLastIntervalDemandRate( new Integer(lastIntervalDemandRate.intValue() * 60) );
		}
		else
			dlp.setLastIntervalDemandRate( new Integer( 0 ) );

		if( getLoadProfileCollectionPanel().isVisible() )
		{
			String loadProfileDemandRateString = (String)getLoadProfileDemandRateComboBox().getSelectedItem();
			int loadProfileDemandRateLength = loadProfileDemandRateString.length();
			Integer loadProfileDemandRate = new Integer( String.copyValueOf(loadProfileDemandRateString.toCharArray(),0,loadProfileDemandRateLength-7) );
			dlp.setLoadProfileDemandRate( new Integer(loadProfileDemandRate.intValue() * 60) );

			StringBuffer loadProfileCollection = new StringBuffer();
			if( getChannel1CheckBox().isSelected() )
				loadProfileCollection.append("Y");
			else
				loadProfileCollection.append("N");
			if( getChannel2CheckBox().isSelected() )
				loadProfileCollection.append("Y");
			else
				loadProfileCollection.append("N");
			if( getChannel3CheckBox().isSelected() )
				loadProfileCollection.append("Y");
			else
				loadProfileCollection.append("N");
			if( getChannel4CheckBox().isSelected() )
				loadProfileCollection.append("Y");
			else
				loadProfileCollection.append("N");

			dlp.setLoadProfileCollection(loadProfileCollection.toString());
		}
		
	}
//	else if( val instanceof IEDMeter )
//		dmg = ((IEDMeter) val).getDeviceMeterGroup();

	String cycleGroup = new String ( getCycleGroupComboBox().getSelectedItem().toString() );
	String areaCodeGroup = new String ( getAreaCodeGroupComboBox().getSelectedItem().toString() );
	String meterNumber = getMeterNumberTextField().getText();
	dmg.setCollectionGroup( cycleGroup );
	dmg.setTestCollectionGroup( areaCodeGroup );
	dmg.setMeterNumber(meterNumber);

	
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getLastIntervalDemandRateCheckBox().addItemListener(this);
	getLastIntervalDemandRateComboBox().addItemListener(this);
	getCycleGroupComboBox().addActionListener(this);
	getAreaCodeGroupComboBox().addActionListener(this);
	getChannel2CheckBox().addActionListener(this);
	getChannel1CheckBox().addActionListener(this);
	getChannel3CheckBox().addActionListener(this);
	getChannel4CheckBox().addActionListener(this);
	getLoadProfileDemandRateComboBox().addActionListener(this);
	getCycleGroupComboBox().addKeyListener(this);
	getAreaCodeGroupComboBox().addKeyListener(this);
	getMeterNumberTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceMeterGroupEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(411, 366);

		java.awt.GridBagConstraints constraintsLastIntervalDemandRateComboBox = new java.awt.GridBagConstraints();
		constraintsLastIntervalDemandRateComboBox.gridx = 2; constraintsLastIntervalDemandRateComboBox.gridy = 2;
		constraintsLastIntervalDemandRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsLastIntervalDemandRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsLastIntervalDemandRateComboBox.weightx = 1.0;
		constraintsLastIntervalDemandRateComboBox.ipadx = 17;
		constraintsLastIntervalDemandRateComboBox.insets = new java.awt.Insets(5, 3, 5, 76);
		add(getLastIntervalDemandRateComboBox(), constraintsLastIntervalDemandRateComboBox);

		java.awt.GridBagConstraints constraintsLastIntervalDemandRateCheckBox = new java.awt.GridBagConstraints();
		constraintsLastIntervalDemandRateCheckBox.gridx = 0; constraintsLastIntervalDemandRateCheckBox.gridy = 1;
		constraintsLastIntervalDemandRateCheckBox.gridwidth = 3;
		constraintsLastIntervalDemandRateCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsLastIntervalDemandRateCheckBox.insets = new java.awt.Insets(3, 12, 5, 156);
		add(getLastIntervalDemandRateCheckBox(), constraintsLastIntervalDemandRateCheckBox);

		java.awt.GridBagConstraints constraintsLastIntervalDemandRateLabel = new java.awt.GridBagConstraints();
		constraintsLastIntervalDemandRateLabel.gridx = 0; constraintsLastIntervalDemandRateLabel.gridy = 2;
		constraintsLastIntervalDemandRateLabel.gridwidth = 2;
		constraintsLastIntervalDemandRateLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsLastIntervalDemandRateLabel.ipadx = 25;
		constraintsLastIntervalDemandRateLabel.insets = new java.awt.Insets(8, 12, 9, 2);
		add(getLastIntervalDemandRateLabel(), constraintsLastIntervalDemandRateLabel);

		java.awt.GridBagConstraints constraintsLoadProfileCollectionPanel = new java.awt.GridBagConstraints();
		constraintsLoadProfileCollectionPanel.gridx = 0; constraintsLoadProfileCollectionPanel.gridy = 3;
		constraintsLoadProfileCollectionPanel.gridwidth = 3;
		constraintsLoadProfileCollectionPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLoadProfileCollectionPanel.weightx = 1.0;
		constraintsLoadProfileCollectionPanel.weighty = 1.0;
		constraintsLoadProfileCollectionPanel.ipadx = 85;
		constraintsLoadProfileCollectionPanel.insets = new java.awt.Insets(5, 12, 6, 12);
		add(getLoadProfileCollectionPanel(), constraintsLoadProfileCollectionPanel);

		java.awt.GridBagConstraints constraintsDataCollectionPanel = new java.awt.GridBagConstraints();
		constraintsDataCollectionPanel.gridx = 0; constraintsDataCollectionPanel.gridy = 0;
		constraintsDataCollectionPanel.gridwidth = 3;
		constraintsDataCollectionPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsDataCollectionPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDataCollectionPanel.weightx = 1.0;
		constraintsDataCollectionPanel.weighty = 1.0;
		constraintsDataCollectionPanel.ipadx = 27;
		constraintsDataCollectionPanel.insets = new java.awt.Insets(6, 12, 2, 12);
		add(getDataCollectionPanel(), constraintsDataCollectionPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	if (getLastIntervalDemandRateComboBox().getModel().getSize() > 0)
		getLastIntervalDemandRateComboBox().removeAllItems();
	getLastIntervalDemandRateComboBox().addItem("1 minute");
	getLastIntervalDemandRateComboBox().addItem("2 minute");
	getLastIntervalDemandRateComboBox().addItem("3 minute");
	getLastIntervalDemandRateComboBox().addItem("5 minute");
	getLastIntervalDemandRateComboBox().addItem("10 minute");
	getLastIntervalDemandRateComboBox().addItem("15 minute");
	getLastIntervalDemandRateComboBox().addItem("30 minute");
	getLastIntervalDemandRateComboBox().addItem("60 minute");

	if (getLoadProfileDemandRateComboBox().getModel().getSize() > 0)
		getLoadProfileDemandRateComboBox().removeAllItems();
	getLoadProfileDemandRateComboBox().addItem("5 minute");
	getLoadProfileDemandRateComboBox().addItem("15 minute");
	getLoadProfileDemandRateComboBox().addItem("30 minute");
	getLoadProfileDemandRateComboBox().addItem("60 minute");

	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getLastIntervalDemandRateCheckBox()) 
		connEtoC3(e);
	if (e.getSource() == getLastIntervalDemandRateComboBox()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void keyPressed(java.awt.event.KeyEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void keyReleased(java.awt.event.KeyEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void keyTyped(java.awt.event.KeyEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getCycleGroupComboBox()) 
		connEtoC10(e);
	if (e.getSource() == getAreaCodeGroupComboBox()) 
		connEtoC11(e);
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
		DeviceMeterGroupEditorPanel aDeviceMeterGroupEditorPanel;
		aDeviceMeterGroupEditorPanel = new DeviceMeterGroupEditorPanel();
		frame.add("Center", aDeviceMeterGroupEditorPanel);
		frame.setSize(aDeviceMeterGroupEditorPanel.getSize());
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

	DeviceMeterGroup dmg = null;
	int deviceType = com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((DeviceBase)val).getPAOType() );

	//The default object is either a MCT or a IEDmeter
	if( val instanceof MCTBase )
	{
		getLastIntervalDemandRateLabel().setVisible(true);
		getLastIntervalDemandRateCheckBox().setVisible(true);
		getLastIntervalDemandRateComboBox().setVisible(true);
		getLoadProfileCollectionPanel().setVisible(true);


		DeviceLoadProfile dlp = ((MCTBase)val).getDeviceLoadProfile();

		Integer lastIntervalDemandRate = new Integer( dlp.getLastIntervalDemandRate().intValue() / 60);
		Integer loadProfileDemandRate = new Integer( dlp.getLoadProfileDemandRate().intValue() / 60);
		String loadProfileCollection = dlp.getLoadProfileCollection();

		if( lastIntervalDemandRate != null && lastIntervalDemandRate.intValue() != 0 )
		{
			getLastIntervalDemandRateCheckBox().setSelected(true);
			getLastIntervalDemandRateComboBox().setSelectedItem( lastIntervalDemandRate.toString() + " minute" );
		}
		else
			getLastIntervalDemandRateCheckBox().setSelected(false);

		if( deviceType == com.cannontech.database.data.pao.PAOGroups.DCT_501 
			 || deviceType == com.cannontech.database.data.pao.PAOGroups.LMT_2 )
		{
			//the last interval demand rate can not be edited for DCT_501 & LMT-2
			getLastIntervalDemandRateComboBox().setEnabled(false);
			getLastIntervalDemandRateCheckBox().setEnabled(false);
		}
		
		getLoadProfileDemandRateComboBox().setSelectedItem( loadProfileDemandRate.toString() + " minute" );

		if( DeviceTypesFuncs.isLoadProfile1Channel(deviceType) )
		{
			com.cannontech.common.util.CtiUtilities.setCheckBoxState(getChannel1CheckBox(),new Character(loadProfileCollection.charAt(0)));
			getChannel2CheckBox().setVisible(false);
			getChannel3CheckBox().setVisible(false);
			getChannel4CheckBox().setVisible(false);
		}
		else if( DeviceTypesFuncs.isLoadProfileMultiChannel(deviceType) )
		{
			com.cannontech.common.util.CtiUtilities.setCheckBoxState(getChannel1CheckBox(), new Character(loadProfileCollection.charAt(0)));
			com.cannontech.common.util.CtiUtilities.setCheckBoxState(getChannel2CheckBox(), new Character(loadProfileCollection.charAt(1)));
			com.cannontech.common.util.CtiUtilities.setCheckBoxState(getChannel3CheckBox(), new Character(loadProfileCollection.charAt(2)));
			com.cannontech.common.util.CtiUtilities.setCheckBoxState(getChannel4CheckBox(), new Character(loadProfileCollection.charAt(3)));
		}
			
		dmg = ((MCTBase) val).getDeviceMeterGroup();
	}
	else if( val instanceof IEDMeter )
	{		
		getLastIntervalDemandRateLabel().setVisible(false);
		getLastIntervalDemandRateCheckBox().setVisible(false);
		getLastIntervalDemandRateComboBox().setVisible(false);
		
		getLoadProfileCollectionPanel().setVisible(false);
		/*DeviceLoadProfile dlp = ((IEDMeter)val).getDeviceLoadProfile();
		Integer loadProfileDemandRate = dlp.getLoadProfileDemandRate();
		String loadProfileCollection = dlp.getLoadProfileCollection();

		getLoadProfileDemandRateComboBox().setSelectedItem( loadProfileDemandRate.toString() + " minute" );

		com.cannontech.common.util.CtiUtilities.setCheckBoxState(getChannel1CheckBox(),new Character(loadProfileCollection.charAt(0)));
		com.cannontech.common.util.CtiUtilities.setCheckBoxState(getChannel2CheckBox(),new Character(loadProfileCollection.charAt(1)));
		com.cannontech.common.util.CtiUtilities.setCheckBoxState(getChannel3CheckBox(),new Character(loadProfileCollection.charAt(2)));
		com.cannontech.common.util.CtiUtilities.setCheckBoxState(getChannel4CheckBox(),new Character(loadProfileCollection.charAt(3)));
		*/
		dmg = ((IEDMeter) val).getDeviceMeterGroup();
	}

	try
	{
		if( getCycleGroupComboBox().getModel().getSize() > 0 )
			getCycleGroupComboBox().removeAllItems();
		String availableCycleGroups[] = DeviceMeterGroup.getDeviceCollectionGroups();
		for(int i=0;i<availableCycleGroups.length;i++)
			getCycleGroupComboBox().addItem(availableCycleGroups[i]);
	}
	catch(java.sql.SQLException e)
	{
		e.printStackTrace();
	}

	try
	{
		if( getAreaCodeGroupComboBox().getModel().getSize() > 0 )
			getAreaCodeGroupComboBox().removeAllItems();
		String availableAreaCodeGroups[] = DeviceMeterGroup.getDeviceTestCollectionGroups();
		for(int i=0;i<availableAreaCodeGroups.length;i++)
			getAreaCodeGroupComboBox().addItem(availableAreaCodeGroups[i]);
	}
	catch(java.sql.SQLException e)
	{
		e.printStackTrace();
	}

	String cycleGroup = dmg.getCollectionGroup();
	String areaCodeGroup = dmg.getTestCollectionGroup();
	String meterNumber = dmg.getMeterNumber();
	getCycleGroupComboBox().setSelectedItem( cycleGroup );
	getAreaCodeGroupComboBox().setSelectedItem( areaCodeGroup );
	getMeterNumberTextField().setText(meterNumber);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G93F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16D3D8FD815C576B8CEE509C5C5C5C50505C5ADED34D1D1D1D11150C6C5E5E505AD6DD231C5C5C59B95953B3D82A2A6AAAAAAAAAAA696EE0488D7C5C4C5C5C5C5A5C525C265AAD7C1AFFF028BDC906DF7E666BD737E3D17FBEF391F7CBEBF1F67F819B3671CB9F3667F3CE7E6046C853E634626E48B4258F10278EF65D8C198ED9AAB2C6B3D65D5443919C6268B32FF8B81268A779524412FA7448DBC9916
	1EA5DC7FD386047DA01CBCA2ACFD967CBDDD705CD8D38182ED24138FF16625AF3FEFBF1D7EDECCE7BC8DCFAE4E045F49G7E00A33F09701BA5FCD31CA362A3C5BC425894C108B9CAE85697670A38B288578278G849EE5749170BB0126FCFEF919C857F0192F10786437A913089C19CC0402E30C37C67C4D936E981BF80C24F51C6AC9F806C0B88400E4BED5F8EC66DA782D28BDF1EC6D11765622A346363676B60B69C8F311292945525ED954E46AE9B9E2AAAF2F223FBBCC96D367164E766E0ECDB43A5658E6B2
	8BC2C7B3C9632E628CAA93728361E40AFB2404249B84FFC78244F27C852584DF59B2B23D9E202385733A665B8AB1AF75A76F9012DFEECB1DE2A32F6EADAC0F01ADD2DE3D3ABF9FBF1B164B83D6E27FF8084BBABEB2BD9F209A2099409DE0AE2D8F0F767E97FE579F36ECECEFEDEDEFDB5F5131415C5439233D49E4063F4B4BA1C6446DB69DE96FEC9284D6669D4505BA7A588346F75AE9BEE6937FD2AC7F005D378851F7EFCF992FE313E87F62945F634C964147A51B30346F3319F654F1E57DD4263DDF10279DFF
	C1FB6AB81D3403DEDC1292AF4A91CEC8E9971FA0E5DDCA4BBA8F7E9EAB568F06DFC37196991E555547E81DA161A19097F7826BC67DE45425A66DDAA1EBD90D2E9D3CC72ECF29B1B39D1A4CEADDB69B08ECCFB3CBF3B65765E70A0F146119AE9ED4C7924EE89D19BE9A00552F30FE52F6FD5B20AD810C86088748EC9F19DEGD0510EF56CAB6BD2876BD82D315332213B455CE46A9498EF18F5A67C1A2C16CE635636AE0B316D08E91F313345F858EC6A92C41AC8AB3141E1855FF3346E9F01B436185ACC1DC6CBCB
	FB9BC8B73612465B45E8DE31CEA5B41DA69ADB5752EA0284DABB84147B3AF5BA7C1A0DDD163D9DCDC60BC922E0716FD8C91974987701C490A05AE82A10F012582B8172EF8198426DE06AA372361BBA213FA8AF2FE9EFBF515DD1C7F2A20CEEA7F6FE06769D5E70F7E0BB5AB8FE0060F2BAC62697F7209C1F3EE9B2B9D5ADC7087A464EBEECE32BCAC97FDC5501F5667DFA2CB3D513EE968ADF5C26DBE78818BBD8DD995221EEBB9B29CC22978BBFD5D26FE4BB1EAC6A64F8DF916BD29A298E52B17B7EF41A14D5G
	6895819047ED7CF2DF9E2B339B0CDDA62C253BBA2102191AA40BB3BDEB8C4570BBCE541B58FCA240BC0085GE41C1AC26908DCC11881B00B6751C8F905DF204DG5482D881FC81A2G92GF2BAC726D782B483F88284F7E299DCDE9AAA4B0FBAFDDB69A2BD6A672CFDFFEFE0F9F9CC1117D4D5FE7472B1DBC60F797D3F4E8B2BBBCF7793FDCDBD26B6CB152943545684FFF409F1237BC9FE447E6CE8BB67F90562BB207F5B889DCE9747477413FC9BAD164E16435D96539E13455252F6CC0A0F23E9F5F59F6E103A
	CECC6B03FEE24376DE687FB8FDE2BF69BB0E34373519E8A566ACE27CA7346FB219CD2D207446766EB68B43A71FA46D7F0429CF6CBB0581B2B16CB11A3BC9BF4C66D101C6D28E9D1DAD2D50B4C8D7AED4F511FE6D1FFC9EC8A0866AAF198F66C0DCE19776D13D0B300F4A6F9FA1049EDA1C22B746FA5FDC156251457A905FAE290FE2BAFDB8202F7B27348F9571DDCCD7211344591B50317E610ADF094CD0C8EBB4C0FC97768FDD27C8BC7641E24730315DECE6E66576EEBC1DC54A4774CB378906AE5259EF67B6BC
	F6FA1AAAEE1FE8CF965FFE1A5853AC9BA73CAD441E438DB8E7CE03F0AEC005055954EAE1B6E1F40D4B11AE9642490436DB0FEE518A242B07788EGCFDD3A37B85DA8080F33C9F75DCA244B6899191E87D05923C7F7288C695CA05E5BA65D74D5C8978C7123EC52BD4E6992A0BE2357965D49F224ABA0B4G0D3DFAF46BD6A31DEFAF33CB30AE1DD001F4713D4CAE7AF4C11CAE4D8AE581D0E1D552313AB97E9019276A2DC70EF53794F5DBDA4828EF34C0875251ED2115152DBDF6AD4B90E5D82D3846E821F33AC0
	88C7G44D8313DBDB7895798DEA7FC05181B6658DCCFA5D919EE39FDD2FB4BBB8F57982F8AA44D8608B377E15BF86734FC1E333E2B2B7DC88B1DE209757A1ADF656D11612E7F75AB78BD5A5259E5598A9D26955B005F2FC4DFB2879351621C742FB4DFA42FEE10EEB0C0E1BF410FDF17AD53BD210F4D25F2A0AE154A7BFEB9190B36D8CC2DC5643FBD961857EDECB6369DB3B5959CB776980B0C3D16A25AF597ED0578CD642FA5E23904AF1946F4B4D14B33C9E14112FD38CE38F1FA2518EE64DF2E95AAFE1D1E22
	37EEF31FB2AF45524FEC6953AF59F4602E74547803DF0CE7799B369C739787B4F9A7B1FF5FAC17672F7EA44B1F5BC94CDF02CB79E3E9562C40B43DC0DEA4CFF3C129BA4D3481182F8CE01AF72FA801DFA36D780A58CF2D2973E8FBE72BB65175B4DACCD6CCB7EBA526DB81B2AD83186EEA0BDE3A81G0CAF1C730582AE09730D78D9DD870ACF0DCC2FBB057AFED7765B74F5AF43F41BC1266FA9CC57D22317EE54A95437E295724583AE7FB47225756871B51CC63E844E5781B8FF4E77EFDD3E984E67D70EFC0904
	67D7643B4E2036AB61EB7A956DB3F175EF33CF73EACC578AB283395CE7D7AF96689C2288202E2FC3AB8D46C694A54ADAD2415B814849F3433C3D4A71750073F4433C3D57A54FDB049B2BB389EE28C3559A52BE0E98BBCD16A27A3F580A09A256222EDE18DC093AECA43122B6EC0EDA37CC3DFF10F60E341E49B9C73B36E16B6AD7E9DFD5F88EDBD7CF6478F7A93EC606E7F9EB27F8BA76C35C28F3F0ADE8CEA173A8433930CEGE886F083708588BD977B5EC221D498E42D3D9546124EB623F90FE25208EBDEBA77
	06729143EFD29B53F91E00F375C8035A8E5167AA731E248AB33ED7B4FCC52AF06EF9CABE566F2E5DC7F42163212C184A4B37EDECB69DB931215DAA6E3D2DDD17A126E5B88F4739D31B6563DC48141294E2D75D352F183647F9D21FDC487752E6261131BB926262G04A1B0FE83D482B4G388DC17BF70CDDB3187DE91D2AEB5F8838EA185BEDD44F45393DAEDD6E1A1D07EB7854F6769D22E767E3E6DBF6EEEFBD5CAE597940F21CE7848F4135686E952E695A2861D36B1A2052152DA9D2FB64EB0A0C7359B8D888
	3F15GCC4FFD25C4CFD28F6A4FC7BDE72EF4CD4FFC8D1FDACF4F7315FCCC07CE8B6A604FF5F852C59D228655A166FCBDDBF9146A59AA89FEB3AFC0DBC57D0CEDA77F82547312B257747451702975EC38C04FD63755208E962E4306D52E69D02B61D36B90202B436A9E54A11C6B70090BBA24158F26C3322E8E06DE54A1FBA86A70E70DAC47F4889ED4071A21FABABCEF40B65E44F5E8DB6D1A8E839ABE358EBEC3757816D638165E9A8D1FBA3DA05D745EF0B13D8F87CDAFF228DEFF3B38000CA9BA7DED1D49EA59
	5CE2B2B73132483760B725D81043760ACF5035DE2EBB4C5D5C91370276F7F590B63BE3193DC167FA1E908E810883489AB6B23DEC9846FFC8631345FD047AE1ECAEC866CCAC3E5BC076E0898F11B1D915AF82C4169A97E70396A1C40C977B991A468441765E6DC49D5CF8BB72514664170D4B5DA936776486D27477644ECD515F13F3CF194A70A0C37E458263BDD36C6F59F927686D594D2379FFE26694AA3B2B4B54C5A78AE421AF463DE5A1FB1F1A3D3B1D46D613E83B69C26A7289A24DA6CB7B46D2EE630D6B64
	F554AA161907786B2BD306EC5EB3FC9C299FC190BF8AA08EC0389066619762FC27A8F5507966A62B1D79E6DE1A6DF279B6CD3FDC3ECFA3FB95DD6D5D1DC7CCEA5B9B52F2F81CB433A2CBDDE2A6C6330672DB0C1D47CC1A322B227CAC4E967F0EB4DB654B4A602F0AB2B070B2B82C1917492192ECDAF721329C183CE1AB647216737E6323A5B97A7D07ECCFC89A4F1AAF44BD21C27ABD4F974241G23AE44367EF3A96A97F72136BEA483AE6BA2D837DEA445B17DF62C146B370267375702724AAE524A2B81DCA300
	C5A3EFEC19DC5EB281472470D24C0B7BC51817BB37537DD988C781A4DC04F9F975E7392DB3BC44BDAC713758C32B8FCB7BB1C55AA5BCAF01B5F2F9D5A2FF3D78EB36A92FEF15DC5ECAAE2F32C7AE4FCD647792FF7DED4A7B3EDCAE2F0C4BDB55AB1797A2720F92FFE3ED4AFBCDA1EF95177732E62D269887C1DE6645FAF5756655DF494B2E9871C7AB9478E5081F33C601AF417417D86C251FF7312D7C1C3B0CFC17B09B7B5A3BADD047AFA6F5641AE5B87FB5C03886408260FE31FC4F754EDEB2469C6DB436C293
	5A53E1BAA28CDCC2F8F77039F39C50A78364823E822061925CD3685CB11F54E73AC789DD9DAC162BCC2D4636265DE46F8D17CE22FE21136C5062F2D624B5664D35C9DBE3BCECB20BF4DF644FA1F46D46265A4E7623ADE613749DC274CDE1F4EFAEA57B32E4C0D193303D13FFED156F1D04DF425689F117607C62A73A3ECE06F0662508FBFEB34165C1381863D6EE41F9C84525B8CE5CBC4EF6BF9FB0CE3F1F3FEB1C7E789BBC4E7E789BBA4ED67F4C746BDB0E7AF9F01DEFDBCBF05190CE38947B90E59BCD739C19
	1E63A9553BC2CFED9DE47BBAFE4C2F47136D6BCC62783BA93EC906E77BBDA594CF426E90176809BAF5527E2E713211698317A1EE2185F577643897CA88AE8642099C17CBF9B3AE07317AF244DDE2C1F9860E7B5BB214D74DF1D9144E9B42819C7723856505DE0E76F922D4EE1F51000F3BDC32C97265366C33240664B76B8A35FD4AA85EF0055ABE77EDC77B54C3DC789528D3A2DD3396FB411A5F8BF18F33B9B0041BB86EDDBEAF6E603847EB30AE07F25C9779D817C7F3DC7EE2640D61387DF4CCAD3E92523D92
	F1357BA89D041BB86E54BE1E2EC4C757F621909E45F127A9DDAC0493B96EB64EABDC0538778D082B65B87FEE027305F09047D572BA92F1956E17BE3A8CFD5FF22E867E2B5177EDD923E28DC276E24B4B37F436B4EDB09E2B213DA89BFBA750BAC6785D2F4631F7A575D18A05F094C042553867E67D86775C82EE39D628F15B2E7BEDA9262EA4A5431B5511C2EFE94F4D3F7B7371AC4D2ED2CC339962FD3D695E3E62DB846957DECDE16BAD967F02A21E7483E4AFD90A9F27587BA67A2ECC15472FD4440F1371333C
	BD359873167C143FE03EE7154D6FE6F1E21EB22FE13F357B96897997FEB1DE1C3B9D35B1F7239DBB4B7F12EDA47F245F2C3A86471ADF56116F70C75B5BAC3157901A2134CF76073F03B84D5B1B890D85BA5402EB097FE6BDD1D1703816506F5CD22523AB827A200D97FF4D40C3E0119867F6ADEB1BE339BC4F6576640D93F0FCE5B0DAA60F6E694BE4B35DC72EA5E91B5A48676EA60165F14CDA2C870957AA5BE046F5B09EDF0738D2DA6FABA1DC47F1F7F5E3DB30DE4777D268189E8161D800B49F185382D483B4
	79A04D7D14E688047DFC5837DF72DDB75C07FD37E5B4E5250D245DDE276D6708BCDBB6A1D02D0AA7BAC42864B479485748FBE8DA63F4E4CD30138E81755A994BD60EC35F99F13DD1CA429D1D2623264ECED3531E16FE323EE4E5F1A3ED133120DB82G2B83E5962C8339437567E5F2A8861A2A61D29D289FAE55A9167EBB342FA13E37249630F44716629EC853F02C672525A4EFB0E1EBE96DEE25DAB25A6D145682F4EEG8CF79F2D9B81908E9E0EFA7FEB996A9DEBC36FF1F674CE52687D2DC52B77DA3EF713F6
	BD6AF2AE45E5C3B86FFA2C27B1D4679A88B7F25CEB1984B784425E9C57B8996BF34875D88F1756103A1AF94348748200AA00C60081G3F9B10E6AD2571967DB1C6097E9123EEE07E8F0C66216D0DB66A9011AB8F8F28420DB7582F6F1922DE12BED1B653D257F1F57D9D2C3EE37FB509174B4486E51F39300158B081F4C93E816B520B254ABE73A491A576F1A9862CA7053E03576FCA20A1007DDA1D2FD4CFD87A89F46E5188F8B3GCBAFEC0A145E6A45181E0FCEFAE387C9AF50D7DDAF5BFA14799FC27B4E9020
	0B70457C9F5B274C7F1BD912BEDE7BD01FA49B7A0C3323CF668835BE47FA157A1C22F3229C204B9F017ADC2952E766B4C91F11DC1F3A910337DB35BE4DA354E358B8BE06D9EC48135B5CFD846B87E6086978E87277C837F68CFB299B7B817F91587F6C677DE6308369C60ED076BFB132F4D91BFADC73FDFAB0E0FCAF3A48B73944D9BEB64F1FC057BB71B27D99FE9A45A74970EC6E3A2684672EE9B70ECCEF3E913F07FFB60D74E911900E8390EE027599C099C05DCD386E5C17CE7CEB6D6ECF5635F754404254AC
	DBCA327CD9CB1D4D9F1BA3BEBA9E6708C18F5CA884DF7059D8DB5F1C1BEFE2656EAE7E4655AD22F816BF0FEC123FE0089B8D1084106587ED8420428F73F7FDFAA14B9FF1DDDCEF31980FB4D32FAA4C5C3E96D32F04A69B9BACEFC55B74FC2BEB7D44B383FE7A7D3995709E2AB8DF888781C448702C4D3ECA3201FB221B3F065FB6D3EF9EF779588C9DC04157CDADC6F37B312F8B730A8B73169916208DFDFB6567FD4A3EDA296B8FCAA0DACCB7C17C8D993AC814716C4FF299D957AECC99EBC3C6414D0C57F03334
	8ED9A0B0FF638CC17A477265CDCCAF2671DAA616F56EED5789D5AFFFB6F6110DB41AC5594132B4620EA70F572613300E2513B2CF32DF42DA127F23C6F31734575F56C3763A8E37F7524D8F3657F5BE7FAE1EG69E45C82FD9FC09D0079966CDB37D934FBE4C68B72F95E4276487C61B7649676AD0875D7CFF48F36F6403E084E7501B76E9656DF11F0B27C3DD2107713ECED33316F0859C43DEF65FBDA02C0BA7C751DA6A3711310C50B716725CED3702A775982CBE72879A592112643CC86B96A7D3B333B753029
	132640621ECA4FD64671EFF2EC2F6C03D275DE9931D3461F508FE920DBAFBE174767146845977F8967834767CBE5BF5332C05DF62ADA20B913C3A785A54550F0CA16AF917748DBAC785D368AE46955E15B757A6285D21AB9ECBE5459EDE2F101F9D25CFC0774E1FCD10BA43ED14B9C4F4722924C47G37499779122C3D9625FE733F11623EF0A89DC67BE929368DCDB6E0DB887A93EBC311709B831074A7ECC3C9195AB6B4E59272657A33B6E4005FEAFF76CD1535215E490335217132B654883C96FFB65720F38F
	787BB6C59BCA5BCA6AB07170ECB31917618E3278AD6318AC2EC4997717F13238D2E55C4EAAD91CC1996759215AEBD6EFC9E37BCD1DE70BCE568ED9DBA86DD53785E2AFAFFFAC77878A24323D2B5451F1045523D26E47606F2F5F9E4431EA0974B7F57790537BA05F39744C0BB13D982EFF4377126C4825126C2AFD4E49FEEC9F4A4E3995E56FD8A649AEB2C832BB1D147D9117DD4BE57FC0BEB50A71AB384F177B2D766CE8D9A16970DEB736D1A6431D77CD21F6E5DCCD0689F10EB925D47ABBC77677C6174E06B9
	B73FEDB823E93079DB4AF45B5FC5A6CC577FAEB2E53A7EF71199536D7B9D4C1BEE7B2CD07EF479D9A1B67FBCD6C65AE59214D5761FE1DE8DD0835078E71CFB5EB51D54B1F2FAD3BB6F5C54F40CFCFFA7A7B6AD187E155936736A13BD78B94F1B33493742436D96CBFBAB110F785B32294FD8FB87C5B2DCE0B631D1E74B31E68BA3957177E6137AE6B69D25E8F1CF1E6E338EC03EBC7E0C7BAC77D01CB70483B9EEC2994105C3B806633E23F5BA9142E93771FD7FBA3A1E3D0D2DE3986E9A0AAB84DC9527CB667B74
	7537611AE648AA3A1F8B61705B504EDF4F798EFE77990F0B2B97F6CA09DA9A10503FF618BAADFD8CCBCF90096577EB3940735DBCE731AD99D0DA5B200F58F578386908254EF80C177F1FD71331AA96749082E05D81D081D08B508CB0G608960879082B09AA08EA099A06B761169F9G86006A5BB19F2F4EDDE2A79F62C787DAE9BA0DAD245A086B3F5BFB70ACF48348723A9D3FDD3CB50F4833FFCE262E45E2B6ED9007F731AF7B38877B21485B71DB4663EBC99AE9FF01B99A00810079CE762FD37BCE56C0DC87
	40103F603715E196DC1F7AGEE74DF502772B98B6EDBE75E0169FCC9E92BA05CGE039C32F9FCCD8E7FBCDF081F1AF9673710D813F8FF85C01E95ECD7799485ED782476DAAC13A3AC06E4B27783E9E8C7848C07BFBF80C2FC031660F811E44C178180DD698308CB36E04B5491D285BE9BEBEDB6FC49BADBAC54FACC1B882A066CEAC0B17CE294BA28962F26F027942DDD896ABA7E1D9D4834E6DAECC6757C9D89641F7E1BA37D35A8488E75C8D7575EEE74B625635D2D99C6FC59B175D4D3F8FA8FCA8FCG9FF837
	A3B67EE5311CAF9CF8229C62FBF21F1CAF9EF804A0C7781294FC5940D360905FEA05BFD3857054BA44F731226EB5810FF590BE71BCE4196D712327EC70716357B236BFFF4CC48E1FB05C10B2B20FEFBFFA344B444729E1E5D99C37352B363D2B05F8CA08FB175B147B12116B848167BB115B68778823D575BDA2160EA59E10C76FA0DCBF6415597E46F2839D834201F694275FE0D16E1956533EA59962B36EC19A2FBA793E23388E2E537FC692C14723B2602D3A8757A65387DD5F4B3F67293F8DF6001CD271EF51
	0E2B889F1F0F302189FDA4D711398528E50179E8CBBFBD9E3887885B0E317807D75BAE6B47D68FDE564F2C561FAB0CDE2D1DAB0CD92DBFD77847EA79DC0161E2D7137C9F03312A1E533D4DF1BFF25C7BD4A6416DEFE932B4F3FCA2550B602BCDA4C144FFCE756A2538FA9177258C772308CB2BA07595325C5C5E093C93AAE85DE8B11BD9F89225E9E9036A4B6DB12D02685852E11469BD0B63FE64ED736E1E4176BC876E517301FEF4D03E793AFD4167144F7B9E7B6D9FA1G6858DE716AF55477609E759EF29545
	C75E235EC3FEFAAD0E8BD190D7F1AF0EA9D7AA46F7F2B65D9762A2G226F45316331BE74BBCC3C97470EFBB653B3DD77019C00067B304F3F542E5F1A6DBEF1380BFCAFB94D476C5455CD6F1D39CFED3F810AF73BCFED3FF15DE8BF8F08CB60797D4E2037C62A8BE66559AC7E8E84AB4B579342FEAADCB004A303756AD6F037DE9AB1A2EDE230FE5D49389F668677AB630AA1DCF53FDE9AF53AEB3DFA1156AC7E3A29F83DA06C2F4205C0F8148A47667B6753BEBB76FE793A60C2560FABF06BCA34F4D5A5F2BA26F3
	05DD9FCD5BEDF55B20FC7AED75BC4A177300E3ED357081EC2BD75379DC65836A3A66CB71B58F286B5A12D3D8576AA1AE6481EC2B9F1ED237553C87E1FD815070A03655BB4B78992287391F52DCFA478B044381229F443A3BE612EBEDEE3B0BFCC31C66E3F6DAB819681F78205AFE7F25F8A1C4ED3F3B799CB89362BAC2B03F6D3DFAF5FCF408786DA9C43FDC13811F7510B2AE9F42E5GB5B2BC2B273B8C5A3ADBE75056F17745DA3A0B97EB69DE5B2725FBF31F16EE4AFEADDD76FEAD5D6E65DA3A7D4B35F4FEBA
	ED75D6C5DBE5733F8FAAC991303956FFD68E366F3CEC856E0BCF5BA07165595BCFDF36F2391C2F2DCA627B254E51BDBE46FB55D60977C33BE9AED768FA7FB642474E2004CEA7735616B6F3CB1BE9A33BDD0B5BE7AADD83B5C21D30BE04FB9CC3296F168704FD9F42FA381E4E19453EC876778BEB4F7C1EDE7C99CD035DE998388E6F7988F8085DE9988E3FD1E2DBC9F8887B2A4FFAF16D1B71B076D5A77AE9DF8961FA000E07791951BEF95F618EF81F072536977830366D32BE627BADC4DE7843EADF5B8A0A0F13
	61D95F7160E674354DFC844A7191CC3F2FDBAA27F3E5FFCF549D1BFF5FB24BBF23E930FA3A0375DB105F44C7306E76D31C90BAB2BDA794F1A7697ECAA104EBB96ED90AEB02309B4745D3FFAEAF887BC9F4B3F13FAF18636AE99A990F42D876A862DE69C6DD8C9C175A033AD4F2DCD8A926DB47F1AFF7E33A4D9CA76C45F4879ED56ACC520D64389E66C78E61840E3B094DBBA01CBD92F151DD84D7G619A0EABE63E65909E60386D145793423E9C37F4866A9244F1A3A9DDDA984C5742F0EF4F788D2907627A10AE
	2198BE57BAC9426392CAAC43EB29AD0BC1C6F598772326F7BFB5C038A38C6B7270D3D27D3DC5767752D3E73EAED71F51B4D87F13F99A7B1F01B0567FF8412FFF986BA3C243307F99D3067DCFD49876BF5B695D9DD961A3538BGAA4351E677AA4EF457835E9CAE75B9EE61367A1FBB685C4BABDC5D7FBCCC7141616A7EE74DDC6CFF22A12E7AB1CC7F5B49D2B9954B7E3EFF5219AF33674FE89A2C1EDEC64702A048EF78E37CAC83450D02F09C475D4866D2904E79AB627E447AA488D7F0DCA61DA355C238096376
	D21C95421E9C77976E9F6D77D75CC7AA68A5FB979632F3CE6693826ECBD5D2FD9201CEF89CE92F27FEA914D640F9C674F601176718F5B897AD861E6647316EAD2CC4FFB85F47316E9D2DA434D190CEG4808C07C776B89BE8F42E5G3591D8A77E532B1ECFB7C7C875F0A042D61D7C879DE3BDA35477313CC77101B2BC4B43AF7DF89F4BA808B3BC417DF3693E00B70483B8AE350A169904E3B96E5CCDE8630CA7D17752AD037B20EF20B40540D383E08670G88G0864F29E5D5CA87C2FFC5023E9DA7F9B9F74A6
	03F21F752B9D820F3B4C203F4FFAABED9B71E01B24A731FEDE5ECD768A1B5BBBDB7A21AA9B4D440D87F7F431DE5F5E0D7DE07ED34877E6376D7D59AFA9FDAD50B6F07AD403F2FFF6B655459D623D1E427D55B7E7627EEA78D3037B752A7D4D231ED27B19369B7477F60732398C50A7BC055F723F5FA9F07D8B69DCA2B79262A251A71D4DD76AA05C48F18225730030F7A466E156DE4CC3C464603E406ABCC4C72AFD211328AD63G1F9809363C38C4D976353B2432FF20C43F6C1F27F3083C27C72697BE0DF2BE6A
	9A3C6CA7F31F04FA4E3745E23B6C9BA97D902075647407CB14E5EF253A04C0FC4453E8374DB3388F75537AF61317FF154AEE194F284BFECC09FE59FFC8534E817A7CE7D03FBB8D7A7D7993341E5482DD83279DB9206D4F3BEA097DD9DF387CB45F87841E70E730BFBFF59A7B7364E7305F9EC9E98D4FC27F855074AC620751BBB587A06C859070AC76E5FF575C9B937E2CE42B28E7ED75670F50B9CB7C336A7E7CE90A4F9C256E4FFFD80D7DF9A144F90E42FE7AE9BA9FCA04F046F3087BB5050E2D902E643834B9
	581FB7BD073A5FB3F770B3986114469DF87C81A2G62GB21E871E6711E6635CC6C13F8F7D7D4FE09C3419566FFF86433FD759262F7AC1EA53AF766A37697768FC239AECD377BC564F7B8D365BA63B33468DE8BDB87D435D4A36B91A76B741909F4EE9966D537A6B176F437A1C48694C7B745B5C90CA1B76827C7D823677477837340A979CEF6FB3C4FC6D8BD2FBE7769AD6A7596B645A05026E3E4F7ACED894FDDD1837CC5C7691476DFF2CF3ED3F72BD97793E2934455720E38B299D3FC267FF9DAF286F480F25
	F8B7991E35E3F3A57A94F8C2DC628B385F7C9E1DAF560FD65FEF1F32012EC5E4F8A62714521370A80873F9915B798CABA9576617209D3F043832BEEC7B3EAFE1BB7AE73F7DBBF6BE227161C09F8B1076B2744DAFB3FFEC711E0C7EB3FF470EE77F193CE3C774096D537A52CE69D77B23D73F2C76C71FD3257525DDD605FC8DAF0BF7B6402F3BE8B7F6B743B51B10469770C1G238192813223504F5A3AC56DDBCA788DBCFE2ED5AF3E1A475FBFCFFDD7A609EF60714F6F560B6F60712F9A74648F6171FFDF253E2F
	1244FBC7613CADE02F406D5B516D487EB2238D273472DBAD77107F51A52F2B28E65366BA31EFF9F61F1446EB257676058D0AFD616FFE147842E6BA67EFBAEB964E3B82F8DEFF7C0948E3FF8F7CAC49BEE4AF5FAA1DEEE81078026D66E519C14EB772004497E9B7BDA55F0EC609AF51617DF8E6036A3DE803D8EE038E23E403AB8ECB32D31C484B3DC7A43E673A9C2FB30BAD0E5719B1CDD29A15A576F4D3FE77D8FECC62DBB443B9FB754EC0FB650EC1FB55B7CB76BA59A2499E55B55F36CE2B44CFAA38DFE11678
	EE98F0EEECDDFD5A35B1F22B0BFC675A642B9BE3EFECDDC1675C4DE354E3EBA545DB472847560853B8363AC3DC7C989CDB378A84DF7B0AFAECBDC8719E2F284756CBFF4531B582623C7E06636847F42E5D988D7DFAB462F24AF1EC750E463171315576475697E8FC8850C783A4C733B39A392FE27C7E55E7FEEC6DD8FD66475658B26D587AC5197A1CCA652B6AF3AAD725E847565BD3102F6ED5B6361A61576DD54647465639F310469B7081G11G71G19FF47316BDB4D7D53043F1047EF6F560B2F6471D93DFA
	71F5BC5E2C9B5F4C63B7642A47D62ABF0F2F352A47E6926F79F76CBB3EEC9538FD2FB6B87E6DB621DD627331BBD6A83F5D6E6F147884074FE7B07A9F76A94FBB75F7C932EE322B83FE4BE5FD68A7FC2F4B0F5BE1B859929077215750FDD972869171F3E675BB26925F93E261F7E8D80A6F2CBC73EF81DD64D29A365A23FE9A9F2DF77D9D1F0C8D04B79AEC1A768F28FFGF5G83GFEG11G09G39B1304E83B083F845207DD62D8FD535FBF97AE757BBBEE6C5DED2D579B9DB5E71E16D2A502A6DBBB7DA65BEC8
	A1D00689GE92F09FEEE346F5C55474F81823E98209620834073B56C1B4A662A7BA6521E82F97C488D6A3E0F440773781A136A3E0F44C73D063ED209FDF2BD715CC2BC448B2F631E5D16FEFE1E6CF5741F7F318A7777EA81674EE9C7D4217FFC686B380F77AC25CD02F0569B50173C01340FECC25A4AB7705E2677B6296FED7A27A03FF7E941E6753DCD5FAB62FFD044133A741C625E27739471A4CF25A2BF53690EAD681FEF65FA6E3662D909E80ECB1817A14A2F0CC55C04BD0833F2DC76F2149748F1B34AF14F
	A87B1F5836F3E74967EBB500EF7C27A3BE72BB764A79AC40636E905F408C45198360899C04CFBC1FF49A7BFDF226C274AF7CD5596FEF26E1A97E1953E73E5FFF4B25B41C6B77F7892E777BCD14B79CEC9C8F107D6648748A00A6G8F00A000E8003437205D8254818C3C05763B7257D0D51DBDFB7B7D88C5DE4E6EFEBFF315365FDF34CA5D6F7BC1B9863FA56F771716A34DA840478224450DCC4F87280A43FEFB4BA14D1C945AD3930F1FBFD6B36705F8AB0FAF2C504CF9A15EA38E7B7D570DFAE724FCA1BEA88E
	7B2952D57C7CDB9C767BAF2670BBD65E06BA7CB6524ECB453E5C72B67791C9257E34908E870866346B67A0ED625B586F374FD1777BDF8A72FEFB449CF53F7F27310A7B7C66586F77474D355F6F9F64779F973E03FA1E5B0D38A60E5B426F5C7761381BB8EEB4477565E23F1F79AE6206DA511E9D6FE25B7E22DB5E8F7B83BE64DD7B7D9EFBA32CEA83FFE36DDD36DF9C77AEBB1320FCCB6C01B9C8179F8F65GF49571FAF42B67A11DB51E7990F86A52556D666946B3BF83FD3A06BE240B03F8613D1169F96F6951
	CD64EF4FB5C23C55A65DA31C4E9B6283ED52F5C9EF4FC1FC1CCD3AEF2537675E879A004A7775685E65F4EE6FB33BF86B528570B76AA25FE7F65127EB64EF4F453E4F7CB312D5F4629BABDF13F3387C4CEA4B6156226E1E56A263E18BFDEC3A3CFCE73759DCA3F6BDF82707955BD5598758862F3652EFA590F687707DG6BE2F79FBF67788136411B7A6C5F1979E21F3A8DAA6F4C9C5FEF7F4E4CD57D6AB6087CCC27763958BE92381E1FED4036D510003873CEE25BEA643842FE248BCF607717537BE013A11C7DA1
	740B9FE21B2B69A7ED2E6AC3476697751C6FD015EBFBF12DAE725D62B49F5BE35B3809645B7C217A9CC08D45DBE5F831CF67F379A1909747739B3E49B53D0336684D45EA7E456C5DA87EDA7E25343FBB04FDD438C088072BF062FE1DD5AF0DA811B67EDF7AE5ABFC84F561A3E5DC8104ABBE52CB6363F9187F5A0F34721AGE785701045B13E49FB104FD707AF88F091G519A3E594B757214A05225A5325F1CC4254CC288D72AF0F590EECE544B539C4DBBAFF298C8546AAB5E19BACBFB4E21EC1676BCC453DEAD
	DD4BDEAD5D95B334F45EB364F42C3F7495303F74CCE4FB377E096CBC0F32DFFD62A06FD7A11E6C73A66B52EDB3A2DD5947305E866878D80FEE36856982A1BE42A65D531CAE6EE3662FA1FC22C7F7EBB752D5C23C3BCD3A4BFB789B2B1FB05F8FFD3A77B9DD47A74CBFC41F6E79DE24F37F04790F786A52DD45474D18CF18DD12F4697673713070D3E697FD3A09FCBC2C823A860001CF15F42C5F5E3A0A0FC31FE23F5DCB5FB1CC00F0C69270A4E19F3F371C0FEFC9B8E61DD7EEFF4C3A275C7E1875D6397DB1EB76
	EA7BE35669C3FCCEA769B99647A2FF0EBBD201E3D68C474DB90CF4D51FE15EBBFA294F93043DG82BE63F784504B0642BFF3EC4C0A62FCFFCBF12D8FFF5BC53E2FD3DD9B339E1BCDFD33BED30FD94FD0FC52E76AB16B07D43E8E799C7A3A4F795E439C5774BE22B8179C79B933F30C781B7839566E0CEF28E27E1E7185234B95FF0B3F3045D72370C92E92696A45DF33CDBEFF851F1BC867A57E7A5B64BB1D2BB73604087423F478E8DF8A78A4D5DCE6B234C9GC332FA7C72305A33FDB550B7A4ABE531FEFFDA0F
	F6AC1855A39F8BD81937D23F9C5A56C00EFFB2FE3B9ABA477537EC5F1803B2E3C0DE4E3FD1E646BC57E5B64DC31935A04F1D4B4C5B7D5B5ECBBD359B6506024C24FF631A7C7CBE44977F875A42FFF08F6015BE759E8171A7727D8F6A7332613769E360EF8F87014CF81E6E875B75524D7F92750D63FCE5006B789279DA37697179F3BE9F7E6EF08841F1BE0F4DFAFC05C21848D74B5F7F2D841C9500718D53FC77A3FC01904F6CF35F4A5FE61FEC7E7EEFB848CC66F2CF16B97F3E6D0BDC16E1ECD83AF9AC66ED98
	FF73578FF051E3B10D87FBDDFF3F364DEEBB365D07CF2475B0BBA5CC5507E7D3FC1E8C4FF49FEE45BE3C9062BC81D87A7DFD2E75611639767892C1FEC6EA184E1C78BF9B64FC39A2CD3178DB256211785CCF6A5E85A0521BF578E81F8DF8AFD51CBF04C3GC64970AC0DE57DF25DE2A1BEC9C6131926CDC3FCDB062EFD73524254EF4BD0FC158CAF3EAD435FCEE906385184E8BFFC249F4F6AD60EC35C3D9B35E7F537EE92384E3BE5FF7F2D6A4C1F3BCABC23E930BC3F445EAA829BC867BED32B6939CA408DF05B
	6C25FB931E908E6238DBA9AE8242319CF7F4B741A5C1B8A39DF196817DDAF2B96EA1FAB65D8C61A19C57E7C5DD24F31FB7DAD197EFCE7755F2CCB700637E54036906F2DCB8FFC3E6B447AD6E45F47347A3AEF3BE4115C1380E63CE73B71C1AB96E83DAE702A19C49F1E55486B190CE623845FBD0DEE686627E427DE673B86EB475816A0070900EFB3C8C65F9F35CEA1A5FG88CB67D21BA9AE0450F03AA35486F1904E1C00387BA8AE8F42959C77B8BDA3D18B61C60E4B67E7F3AD9C37131E098907F0B447AD63BE
	CA8993302FFEC2606350D7B08EFD05FD75D587DD1FEB7C6BA04A8C84F9F1DC66AC236BB23B0CA8B3EFE2D8FA7DC49CAFA30C6A711278CFF9CE44B47F59755B464B8D96BE16014CE81E6EB35DFA69E6E722DDBF61FC8500EB4CC4FD3A3BFF1BBE4FF723DCEF10B90A2777E08F62B3A60525D7CEC27C2CDE44DB81974871DFF0FC9C6072A623FE07D77CB67D9E617321FA10694965FE33527979059B1FA3C5009CE18A6AEE60730EB240F5CC41B26937287D11C91978CFC19D2EABF3FD7E31D3730E32E3730FF769DC
	A8E40AFA7E11C87191D354730FD32BF07EB19A624A32F07E71135DBDB25B6350F9076C7179007C40AC3D7947CFE365FC61A2CD14789B1FE5EB7E51DC21B76E89D399FD76D47D79C78160ABD4F13590EE8230CED54FBF969C166B6681713EB21AA01DB4184D63E81D0F182A1EFFFCCA7171D355730F1EDE1CFFE4CD0371F49A76233BD661D97DB80EABC9511E55FF0A3EA1407E9EAD7BFBFF6A191FFFF41C51B444BBA658FA98EC10B99D6DF0835DD74DA3E17E66DC843F43211853DDCF3FE33AC158136312683DFE
	FE90165E3FDB4CF903B85D91BA8FC84B8ECB4F4EC65CC7142EG42529BF6F9FC8ED146699E213C6E90766638GCA97GE1698D3BB11CB7045379526F2719B342E4675C1F2638DC4099E6A0DD5781FA768B42CD9C17C33FA3D9A16C4EF11E54FEBE908E6438B194978E61E80E335470F11B63EE32A22EF2A662462F2073BC887B4D44FE23EB033A7DBB76AE78B99A3EA2D5B8F826DE3F7136E62F4B3174BE557029539B251B5E71F92E25E7557029530B53CD6F3C5D2E25F71906CF1D1EB0CBAF3D0BB46BCE47523B
	C64327CEAFC7159E5B7F7D6FF25C7FAD1E05F528DC737633E3BAD4EB7854BAB46B6A70CD8D6A6046F5F85FC59D128755A1D8D7873FED2843A82E430DABDC53A1D0432756A1A3C7CF07304D28C3DE8E6A30EF25EBBAB4E97854BA546B6A90AA7907F09D66E84EBABB26C30106CF2D03FF0EDE9B781B0B693DBDE8FA213A69DD2A196BB9165EF08D1FBA3DA85D742676897C6FB9323FEF527D1E7CFB81CBA3640C264146250B667271AB070F39F45C4C1E9D16DEB09BF1B3689ECE8504EBB86E5B59F42F8742529B4D
	F533D1DE87277BB3DD4BFBC1D83A877FE50A7383DC88271B3B1260C6C1B81E635E26F382E1CED8FA569C44A550BD0E7CB9E1327D7F4CFA3AB7G38DACEF7040F43CD9CD7CD779A5CA16C43F17DF4DFA61404B96E332D481BB4875B561F378FFE967FFECAC37C1284516F087894654E8D23BEC36237396D0DB64A6477BF0B3F47E6DA3F67D9FCF6DE5D035A368272DABD9757F56FED41737962195CEA7DB379A5348C86004F1D731ACF8EFE4F465013588786F13E5013364F720FE17BCAC09B4F69CF6DD61E653F02
	6E3565640625678330F37797897CED40DC6D9AE630BBD61AF32535A4CBF374FE7DB37F7134EEDB005E8D00611E2275B8G42419CD74A6E1B04F082470DE2772F4C03F6BB8F757E9BFF5330FE1E3E5E766ED5B14F1374E6E96C246D518A78A17350FE0B665B36F7855D838C845A902E53157C2D60B89D1DD2857B36CC1E2736E5BAADD3657B2873CE224FFC46FC54F357C9FD1B9F2334B9C017BF9FED39136EB556C238E1BE6A7DD89F6A6DBEDFDFEFFB3674192F567B4AB22D5E81E52837BF57A5016202A19C4E73
	32FB3932DD7D3ADC3FDDDDCD6BC98270A5F35E7E1E415B55157CAD0B02854877CC0F6DF2FE0B525781EDA3277F3B6A7E1AC43ABF6A81715E8B50262B795DC5918B746B273D7BEB229728EDEA3271F66FA93A679A877409BC6D6AF2CCBBAFCF3FBC65E92B5F49B464C9E9333E76D9413539492B9A3EA2D538B2CFEFEE12F450357452B57BCE6A746AF5536BB33A165ED08D1FBA3D0EBC3D796D9D343E10F148BD8F4760EA4D3D640E6930CF432756A1D4D707F1BD28C39457A159C59D3E6A9ECC072C05FAF60F5270
	B9165E1B0326D7201B1E764EFD4752FBD6432729473A694D535C3767D8FA5FE978B475D8B73D311A6F550E25B7C5432729473A69BD66625A7405C157266E3A69DD6062BA54EB50F5282FEEFA65AE2E812B87DD8386AF54EB13F772374FC7AD44B67961AA014B7DDC7677F92B4E7CBA4D670C26A15EC1FB8857A399DF639A6541C602AB04F0A5477DB29677FD6BB82E63A75C771556E9C78E706F301C6EC26AF3687135FC1DF69F4579GAE08530D205FDCA3A09C4BF12DF46D1684610CC5082B2167F6F2974957E9
	4D94D78C38EACE37EBA56A5240F1AB282F239B043DB86EE1DA6E41900E6438B5BDFC7D3A086FC7758EFE87663FA9CDF6FED83A8120D674592D033F096FA9239968ED947ED7F7E0DECD53BA53F7E00A778852EFA496482BDBBE4E555C47AA67EAB7D13B86C1FCA827997A934EB9597C699F7C4E46047C4157C46A775253962B67EE77286ED4FB0AFE0748823A5C452843143DCA9DAA388EB50B87DFCB28F5E85468502FFAC35E1379B5G1D156B7055BE258EAB799A4BDFC787759ACB2DC308C60717D4BA44D39DA2
	00EEB457A1CC25C38257A16D1B4157A6EA9DF23ED16BF0DF2EF2FD911B2B3F3EC826FBC57940DF7C8D6AF661815B6B041BE83F53G344D1CFE69DCE5FEB751BDA9AF087765B4FF6AA175DAF9175E43BDFC9C60F489BD7A737F8CEA4398200B7F062F69EB50E6B95FBA3E76403B748A3F15EC4664BDE0C5F9E5BA72546B89779112BCD2B6B55F2A4BE07E8AED1E172F40BC57FF0BF97E738652C79CE9EFEB5BE4E95FD84A7973664A71860E9FB1CF0EDF4E718F6C1663D7F07C48BEB91EBC8C4B7A2C250A7D7BE6B1
	4F83622F278D9BCC97FED9AE17D74265FDD7A31767A772870B3F11B665ADDEA11737124B3BF51BDCDE0C481FA87EE6945812B7C6A12F0C4B0B582C707B93790B455FAA1B72AEDEA917370A4BFB4C2E5FE4BD48B32BE40AF73614FDA5AF0FE244CFDB2540AFC37CDF157892BCD7FC63DCBC536B8669F88184810C86C8AC607DCC2E368E3EC25BBC292759CB102EF023328DEF207B458590EF60B4EBE7E31F4252BFB19B53EF829AAB00B7C0B0C09467F97424329FFA7F24FEBF14CE6F63CF823E347FA2EF49CA5B7D
	50F6BA67A8865A8ACEAFD815F918496EE904F88B2799DFAF3707387FD52F5F672C26FB52FE409B8840725C4F6F790886DC82C056F750F78254FE07E99C6A575E4954436FBB6CGBAF2A713BB7C7AG909FE446F7D93576FC39FFB53F4B89E8491DCC11709BA372B11D3236A1CD9260B38B43528B81EAG4C05683FF37BE67579F0426F81716C2E1F34DE01574FDBB7FEAB5839032868F03B39C93A710C71FFEB1578371DF47CEE396BCFBAFE375C36BEA90D1FF7BBFE775FADA7A53EE641393B65C2845CC7736536
	FAE20048E3FFFFFACA127D1FDE47F51A732B4477253DBB26D4F77851E7DE710E299E47792EC91178021DFC83BCF3BF5AA00A5BA0A4D53241BF52E4BAB91197FA8519485735E530BAD3329C6F2A7A6B9654A76BFB54E771F8C916612472CEAC7AE9C20CCB35D7EFF8BA0C6F4484096F26BE47757B248F752B667ADD7215A4AB2CCC295F1D932538398E254378C6E7CAFC75766A022A4D8574B85E66BE1DA425916244FD0EC7F2A43E9CA76FGB54FC47B39F17BDDB0DB2AEFABB6C932E7DA9C394FD13C23219C6586
	F339BFEE16E4250F551F07ADBD24BF8F8BB42A67E162F9E87E96F242775AB9C5DAD1D8FA8EC0E11194A7FE83302867EF623EA35F172BAC524A2B83DCB34000C6DEE937FA5EA72E41FA5473B706CF6AD14F43983E2ED7BD1FE278B22BFADE447049AB7467B78F2D541F5F7CD2261E5F30FC5FEB778DCBCF9DDB3071704EC3B89E7A81CDB0C094C092317BD29C231FEF544E0B5EEE44F9D159D27CF6F3BD5D0F2901F07DD2BE87B800725D8167839082908D104C650F28D04EFB1E29501F773CC977E7F20B21BE96A3
	EFF01F6DF94FF34CDF9EE89BB97D10154AF94F35F4EE6481715E1C66489C6D3C6757B97A731E2B699ECF84700EAE46BC9FDD09F996160525E78398G6AGAC4BB00D9D0735731E26434867350C4DFB8260B794A00A733DE8514EFB62AD48173C0C4DFB32CB40C6G446F1869D45703B415002F8330GF8818415603C27D4F30FB3619F4D630FEA6EE1A671F1BC7E6E923D7864926CBF6E29167A0F45A94E4F1BF237CB7CD76CF52E1F4C5A474741D254E7DD8D11477EFEC676BEC63C13F7903772BB082B396C64DA
	C9F67D9EC976DBCE6A5D4C75F66332CF56C932DF165DBB6D6B64DCE5A11F2B84F3591F6F17E44F135DCD5DFC40713BBDABFF1278A64DF57CEE4F98593D53C3FA1C3B5B73459E65BCA17520A42B2546713BBDC3EA50A6895CA65F9CA232D87C3B5C8F2D400071DB9B2534EAD6209EEC4FF6B37DA65D88349D865C47F527F7C38CB1487D0B6F1905FB3BDE1CEEC38D7AA145F25C08856807A479B5BF39007B6FF03AD06AB7D83D9C7A6265088B22777219174BFD13DF1B073C831C6E9F545FAF9242519CF7B35D27CA
	D8AE77CDFE1E7BD389AB10AE1A7BA155F2DCB8FDD3429742C1G4C0E91F3F17C08G9C3BCF6013396A7B8A48B909A2816F9BA848D55FD7E0D25CF375D32E7A3E02F10AF80FDC75FD855709712C6CBE4CA53823C6F31774EE4B4D327B43674EF62EFDDDB69B73A82C44BCBEB2DB1D47150ABCFEB8DB1D47390ABCAC525C657516A2FE6F9CF59E71AEAF161F08A3D2FE321CF8EB75DD59DD65FF373BF6D33669AD47A43E587D4E3D35FA3CD9622D37BA5E06FD253B1E385D1F312A6D3ED0E1F7C1F3FF538905DDCB7A
	54F6BF2408EF6A33E5F7F60663FA7A7E99C93783F472DF096D60CDFEFFE88C6058BD970F53F7C5F34A42520B812A4B703C5128EABC135388B86FB21411339573BB2A8CE5EC5ECAE8B3D605256781142DC25A955B1036F6955A26E91B5AB64A7B652E5A26360D72FE3908EDEA5BA86F177BFC1BAD5BB01DE2E9FFC36E7FF0DF055F359ED578094483DEA857BF3303A07A306E1773E583CF01C3FCCFED1173D5GCF2DC3FCDFA87C5B1B00476A905FAFB395E707004757A13E530A73D7C14093B1889F6B031F227EA5
	51C01BD40E7D722BDD58D7E72EC65C83142E81429D9C17CA7D0906C05807639676203CC00E3B28846505F31C877D0E19DD8165D0013897ADA82F02631E3670F123826B6A4B07302E7AD6E0DD9D57E8FF0CD859E8FF0CE8E934BFC6DC57E8FF0C900C5AB1622F47859E5F7413F3E3046F814CE3944F63B3876C0F9142C17BE3C449C17BE3C453C17BE3445BA7247CDCB5689E0B5457E74A6E02BFECEFECD10D912D3FC8FC15F31C9BA3BCE57741D759CDD3B9C6F82CC43BE72DC13BBF30527E98913F527E98B13F4C
	7E98D1DBE6FF0C70B80CE3C4B5686436865B416843FC1E3E867B77109AC21BG610C35504783303BCB5BF7286FAEAD033886000E35B80EE4F763B8628E38503518CEC3B75AA4E9AD26F3A125ADDE876390C063BA24E5E76E882DF59D5A4F3FC7EDBF657D542F74286D273C1FBA33577EB83236577EB8F25C0A63C8A057F3B81DFB12BD026CCA443DD302F456CA9CEFE66C13772B5100CF28F424BF6ED45CB11D36BEACBDE73DA3FC2D0A748A012752A13EEF9463DB9D70B4BB44772862FB63G70F8BA44172078EE
	6787BC4103703136F8E039F21F250B2E03A201B7EABD6EE55CB0CB79DDBFF296FED34E58603CFFC36E86357F76893AFEAAG3CE183FE87FFED812651241346E0FE9C568D6A6F56AFEFD36673BDBA3F9A82F41E9BB01FCF4DD366B3E99E6A90EAC387FB7E8B23B5BAFC32D929C3A65D730F813AF82EC348DC258EEF4FC59DF2B7BA6FC3D13CD16D8F709177872830A14F1EBFC06DC6F51E7EE6506EB97E4B007EG8D9B315DBEED77DE9B5B757A2A592E715D6C747DBF0C4F2FFFB03E01FB747C866F9A14EF3EEEFA
	6FD2BE33CED968019B5031B3682B699A43EB237A1E00AA0A775D283EA76069ADB81684C0DCD695164BCB552E59A956C53EF9DBDD3B9761813ACE32D429739BC671EED56A7C56EC457CFAC0DC824F6F6AED2E697D76E6BDFF4A3ACD2C5C1AB769175FG60BDD5F1FE908E86081461595CBA0AFE17D55EA177AA45A96F9DAD582F257BFE3F166E75ADDA3A3736E8692675EB69E675EB695CE7EA69AE1E29253B32DCCBF7ED391CCE7C56E2676C86BB179183B60A5F04E3C2E297770D5A6C7C991D424D6A3E6B39926D
	D91738926C3B4AB6E31F9CEF517F26D0C857DB55C0D737993F2D7CC067DBEE9076580CFA5FD802FA875B505B5E991DC80D5EB7D934FABF407D0F2338AEE96C3DE988A7F2DDE2AD7CFC4996974E17ECD11FAF0936FBDF081D7E7900EBFC17E97C419D633B7C30EB7D3A6F20FC7A7D7A984AD729E3E3BD285B027DFA718E52EFB5EFD177F32BA85E32C55D4F0D2C41FE4E8D62E237607A62C2457AA226F3E4FAF5B54C2B8106D4637CFAEAED06282FCFB52EA38A0A083C51900E87C85B4A4FF0EB4EEDB8E677829779
	0CCE73B1BB7D072E03F2372A6DB71E5EF7D430D5EDBFCF3E46B2C01CB74F6F2F1AF3A30E695D2EFBBEBAF9AB2B87D95B58EF7EB6E53DA803F00D8A5788E10B8AA75EABE050CB43DD24755926DF6782819F2E0A0B02F03CEE9A9F14685E4B33DD3C17C77CAD582E642D00F02D8A5784E12B8A47460442FD5AF1E259BE6DF8B2CC67BE6ECBF46E639E22A3EF180E3CB5BA6367861D71B32AC6E7BC2E51520D6F5352E5764969589EC8BCFDFB0E756183331C5BFF7AB7FFC356E3BBFE4F1A52A3F059A7E43EEAD7AEF0
	CE7634BC149D4EE5DF52A749FEC766EB66BF4FB959CB66236CE4AEFB52A9C9F60B4CE76C1A394E491E130B328DB5A85B0308936373394F1219475FB1D6CAEF42F265B74094F67E9FE8C3EAF03FF3A81DAF0D0230F4CF53FA7AFD1C7D3DCD76778B3AEF867CDE4052087F9F24913AD54A5393323FF7EFBD73E91FB823E930B21D486645B572FB194AA8AE91F019BB306C0F517A1387618A0EAB23E3E6AD049BB9EEBF75AD31C0581D63E6D33F9B9F8887F15C67ABF06FBCE287564767E5BE0C4F8F7AFE31640F3758
	02F5BBE7A74A92E4BE0BDF16A83F33AF9AA745C5B8148E635BAC73E7F43367AB27526F0592542F166B57A273E7FC4E22544FC3664FF81593694C60EFFC3A73F46E20BE0E623D9CF4CFB6946222F6E2B9D451BEBC9E42525D2FABF6C875FC1D6C6F081AB3DF67DFBB23E9449CA5F93EF9873EE5A06C02B19AF0B970DB38CBBCA7308B67049BD720BD9BF6611C709EFAFF22970483G42F7619CE9E62DE2AF9670893B24F1BE2D56563DFA3EF40E19D32B3ED76F8E0A2F28D55F2BD7D404776AB5C2DCE4AD261F56A3
	1553A4595FD7F51F79B23B630C26416A69E5F41F3C6C8779FD3D8FD3DCB5609AFF407ABC136E37D9A06C4DF147A9AEG42919C77B1EDAB51900E67389D7C5EBAE1B7BFA3C957303590EE5E2D748DA13AC877753EE7C0DD86B85D169ECC5713630A8D182E9F470D66FEB4419CF72E81534D58033829F40D1D3BC7FE9F6EDA0AAB84DC95273B13FEA32D0770G470D23FD24A70483B82E1A76CB21909E45F1374DC0DDE2B96ECBCAE72803F9F99D5637C9B9727A5E88F8CB1DA3FB73337264FC6E40636390DF51FCB9
	DFA07004BB4437A0D74E9785BC718E71ADD1FCA397760625E76FF5649B426C1324433534D84CA6B2DF93F08FE7991DCF95008C43DE5CCB39FE4BB824657CDF2B6E635935C55F2F74989D231BC116054BBBED472F74EABAFE7B82EDG275FD52B5C03B75171BC8A62E3B9CD1B422F14511DBF8777D73277A15D6AC67DFD21BDF46C4F873A62FD386FB2F5A56E3BB46CF37EDCDF47BE753E4BFF95FE4483906F29A3D7DB46D1A5F2BEBF6089F608CF397E0F841E18C178180D469F512F9B8B0E50799648907623BDFF
	2E11578D5677BF3383576745C0573C9F472919BB31BCFC7763B87573CEC29B856184000CFA44FF3D0B6073A0DC86D0DB4F6F6D505CE550DCAF65E9205E567835045E396BD94F4AE4A2472F2478C099DE3CEB190E1FA4BC8A628CBFE29F73BD2DFB5E908E60381E6F68FA8342319C77F1A176CF99BF216E37948DFE1E7C9ECAD388BCB5GE6G8F00G00C8AE47D054A87C2F4E136F22E91D69736462BA552A6CD3DE346A77A96F5171B49E6C11749356436F8D035FC5D4ED40F9D27E4F48F70E41F6DFF4AD9B5B00
	360153E776A87B222F6958698E71DEBFE33F51444FD0077F6C7C9DDFD1BF2B770E0F58B80BFC123DDF837489BF637E71CC7AADB8378166558D086BE0F79BC0381163FE24F9730030F7836A7DAE7FC69C51607C9DDF518D6AEF443751B462G1F580076ABA8D516715ED27DB2EE2573053C83B05FBD003C91DD0317719BDDD846751CAF57E23B0C17B3FFA92075647407D477CDF553F9C58844C79CC0DB0D1C0136CABA607CFDD3198755E5BC5346FDD3C5548EB9C01FFF103F8510235D3BDA12235D63AA4A535215
	66E969B64D5752ED1B2F25DB112B25DB1D2B252B5459DB5B24333776226CCCD77F96674FACA4CD16789B1C70251A1BA571FDDF6B38AFD56FB409EF3B433ED40C5EE70EF26DFC73F4C95687AB9C772D5A30122F518F625AB902386E08F6DEEF515AFE33C5DBC69F14E8695877BBA55D32EEAD5D0AEEEDD9664F1272D3D66328ED5898B3FA07EB7B762F3B48B7F727ADBE77037A73A3B6D7F8164ED5FC44361B4A71E3A8BEC006E7F38863CE74B98B01383CC3783D68F5BA3799F208514F66F2BE2578D0991E49A920
	F892CE063808C66C878E51F53F17B1AC5D5F4857E37C8E1CC8A34E97162EA29544135CF38E908710B5ECE4FA59B01CF32C217189C01FF598F48428BD4C4EC5097ECDE5E45DCCF8088CBD7B90D9EADC1C8DDA0490F779BC63951A4684C1BBC730B51FC0DEFBE9603CC574E12C551E8B7B39D6FDAE4C82F9D71E8B3B78BB6D39B03F6F78593B436CDC18AF7C8609F6E36742B23FC71AC84047G88C7209EGD49E41F3DD6F96294FF5917EC69EEF1E2D97EF617141D354EF1410F8F79EBFF227DE3C8F0FFFD1F36E0C
	44877278EBCE2A5F5BA07161C7306F59A2BBDFBB515E19EB55595FC386474F7E9E1FAD2571CE3763E75B1F1BAB712DF7727C56157CECD8944FEBA2590E93E54F1AAF495EEA772C39D227728592DF059367668F64C9FC0B1DB85BFE7F5792DF04135F2FF2F35086D9CDE80368C5128D3E4C17E45FE037DC14BAFD7D0D44F74B207B5AD21D793C4B71BA3369DBA90DB5CE1C978FD8A24B538C676C75518C34D7B53757337F156C752F6FA459879CBAAF4E7A46AD8A7F89B7106D5564483904923A5E76EEE7FE4ACC56
	0BBB49BC242A837B7EFF503094FFEC27ABFB24C41E63632F1BCBE9589A977453085BD5AC16858913F1050C9F73GC8DDA0934FA9827313A693B132E7B30B737E1B727A030DC781A481641942522B81BAG3C81C28162G320E06251781B49EC57B956D8A156922CE5FD63AC80F7AB9EB1B5F9BC4FF8EC5DE5067897323170F59B2FA4C6F7FF5DED8FB6BD9219D43AFDEA17737890999196E8E6568F3D43CFF0A0E6157707366C1000FG088348B8866D7B980E11ABA72947D8521E2AF97CFD356AF7C26939819EFF
	55AA7598CC62BB0E21DF50BB63754E9D8C01F86FE33837F6C5AD76BD014770ACC264F7388F9787383CE6244D26FBD384DF5F0C7BED9B8A697E98047D81C2B9ED4977C89B550CE7919A3F37FF9661726F6D1FC5887F5E7ED904CF3E37FF96616B2599E27D4BEAC1BDEF2267C1C9FD33F05C87D3B2C479D19C7759CE44E59DC7DC66F214D74DF123783D5F814731EDDF2BB8D7908B7824630E6C43AED4FC0348BC81E5F142913E2499F2BE8370D48F42477A7D03AB35EB36A3AB65EBB676D67A2ADA7C3E58F0024D7D
	ADA40D6AB0551B6A77E772B769A11E2C93A2F4694A27A0DD1A196DDD661A75682AF672F44DECFFD31FEE77F8243390BA00GDD3A3F72B76963A1BE2D5596DDBFFF13BE9FE8AAEC5295D939FEC0E331C977BA2773A2B4G112DFAF43774A1DDE69B33CBFE1B9E5D31FE242BEFE3F651271BFB9269BA5A583E303B0A0E55613BD7E05B8DEF43F67E9AFD3BAF9942596D507F34E3FDFF041F592AEA47F67E1E664C16720D7B85E56AF62EFC637E874D19AD659B7767E94EECA95F38EF1B04FD5AG5773FD6E7F9844F1
	CF2F42769B4FF1DFE7A0DDED87667D61B612F7F7887B8284F7E03B76265F44C7F5B86677994B791E7F4E357D07989779E6973A66DFF9AF6D5B13BA547E150FD0FC5AAFEA7F4A6AC29CB732A04E728B66F765772E697D6DD23D79DE54AF4C5E71622F502934FFB6048BD4388A88572AF0AC8D7F59FAE9B4093456CE7D3275G3C2FAAAE8842913AE9A4CE417CC76B48CBGDCDAD7D8FACE179447787E43771B8A3B34FC15002B83E85670CDDD2E172781114ED37C75D3498C06F0248A97836144AE3DBC3DF852DE19
	E6D8347A0A5F6EF57CB66F55715BAC53592BDE2333D7BDC5671CC236621C822B1B07EBC95DA4FDC2AE68D6E741BD1A8BB2DDFFF77675CC1469817242394C71D3DC17F9E48A4ACC86F906EE14193573373D3D5A3A93651AC126DFB74E75CE0EC7FCB46032FBF0EEB9EA3CFA6EC97676EAFAD01F4BFBFE1BBE9F70B7E99BC126B7CF771CF63DF4C775203EB77137E9E381175B0BFCCF2568715575A25FFEAB72B5834E1773DD1D2647974579589B7104AF9EF079D6649BE6D04F5D89DF03956D5359775B6C73ACCF37
	83E47AF339AB7A1DFFAB57339FE545001C1CBE4CDB55C94457824E3D8F539837427537F003B55F599D9BEB765277FCC37B54E34DC10A0F6CD30FB5EF2D4031A68A62AA7AF12C89F7714CC857A4FBFC3EA0BF28DF2F9F7CC8619797A152C40B3F897D5AFED01C83685E591FF61251671C549FF78A81DF290A2B03F0B34040C975B870ED06DCB7CF08771351846B2441EC3E221D5A7C245AE7EF9345A71CD47B6C0537214FDE76C0D83A5BGFED33823967DB163B9EE49F7DAFF4C9022A65EC9AC7BFBE76119776DBB
	F6C653E0F95ECA571D42A939EF5FDFA8AE8BF0852750B68F5173FF9590EE643829747EBE8B04BDB9EE923D5B588F42C19CE71E467D0DB96E528D749B70693074625308FBF2B66AA27976FDB787F52962F4DFAD47F46BB94E2B8753B5F35CC86ED36846F18D3D186EA80E1B18C547E9888B3FA26EF57EF6EDB6472D1BCCF0CD9036F21CBB45F9C0580F63E66CC2F9419CB7F5A27F0645F1AF51335F39EEE083B744AD580E722AB80E3DE7D66FA677BD3C09621A816746692E215FC43DA09C4CF16FD1DCA40463B8AE
	00FD9305F066B95CB7F2A66A1747F12F506F749D90F667387813C867F38E76EB4D356A7E4931B7CEFAB4FCC52AF07CB9FA7DDADC26EB69FD2261D32717F62EDEFAADD3DCCB2FD74327CEAFD7B73DAE4D37DC875FBE56702953B36826F74EF857523BD44327CE2FC6159EDB47DE5D036B5826F3318EFD6C623BCA29033EF364232BC3F0BBDFF3F39DEEF5712D257B8655A1DED70714D454A16DBC5461000B6FAF1D98747D250A7374F4F8A68DF52863BACCF5716D6685033E5D6C212BC3A877E17363BA3C68228EEF
	8C2AC374F9FAED60AA4D3D890E25273D27C11DDE22EEFA5BB4EBD447522B577029534B9C22175E4F64CAA8716FA6595F6F2DB87373187415E7FE9EF361A49C177207F0FFFABAB6D7C33801633E7A010EE990F66338EF68586CB5C47E86549EBEDE7BF13AC43AA797B1C47E8654C2FA1FC0946092B85D2334DD271D9F161EF7BEBF0F301E7A71C3380A63FC2652F17DFC799BD04194E7865C90CEF747CE54451B6356523D4090880F6238CCB375E506F046857C0E35D664AD3CG5BD6C57B60EF75EE27B4E45F196C
	D1D708FB46359730BDE1C673E4DB230DB2797D5F6A7D274D34FE7F37FA23685A208372BAF0812EBB9B17AAFDBC9FDB2A6F6379AA1DBB8782DF88679DAD8C6E637981FF8BA10E73950C356D63391E4E3D3307C29D9B0A74792AF7C64A69DC3C86626B07220F67F95378BB2DC31D776375992A76713CE5033E0F6743F4FE6F8F74C1C351BF370E4E65E3A09CBFD479D6EF0EFBD8FA3EBB629EE267FEA05C600EFA0F617E479E6E4E7B717A3A2B7DF84F276B00G40873B237D3E4E32ED6F55EC0DG3489DC274F789B
	C939431C3F8724F8185A167BE8FF253CCF23F5B2FE332B98466F411A2CEF737BA8ED8D50558FC3DB2E25EB11A190769C06FA97CCC43DC3EC68ED4F1623B4FA33BB2C14FA27EDC73D2339AE5594978F61A41E17ED2A37FA9F3071D66F73341E64DF88FD7405489BE8993CDDBD454F27B6F03E34EE5B65BC0F7A013A832D97278FD379CE0FE1E737A0BE72C23469967ED6EF72054E7BCEE7DD24366907B6FC27A75039D2AE5097DC04E9278E707737AFF27EAE15E6CD5AB7546A3F5530E0127EDB8D3ED3745F4C7A73
	CE7DB73302476B3F196523D883D8443C380B3FBEB672B6DDA8D43C21A03D1975D03B625C1A489FAE7EC65914972310A73D19B5BED5AEAFDE6497BC443BA8BCEC4973D23C65A03D1975C21ADCDE0148DFA17E565A14D72DF8F3CC76E616415EBEE19348332AE40A67BF7A755F14F83ADF7FCD09F3CE6A3F19B5E2927AF4F8C0BA3EG21GB1G42455816C6CE561EE99BB3993F5366F13A05BFA85B5CCABAEFB3C0FCA527D9B3D179E65671097C9DA82099826087908E904BF9A616AB7B1B32F27D7EE65FA6BA073B
	A4ACBD6B92640DCA355DEFFCC867FC95C0DB4D698F2ED76641C267009608F763B4F7CF155B03611E1D2A5F765F22F343C0608D39847DF17F3A93739C8738E4005CCBE13E8650F0A92631B7C36BA3EC4EC03E01CB190F30977C7ADF4A7C24995FA233563F285C0CFC2117B29F61A8780D9779443753DA11A64DB3ACBD87208C209E406A095FA8B246297D03883F37A77A986EAC92F87D4C7D4E79F394BD6BA47E3B84477DB15347BA6E0FF9CD2514C64AC6477DE5050D92DF63B4677CB1032661F89640ED3508F47F
	F8CF026C7D1EDD339D5729C576D60FB3EFE6DD3DC5629B66046F6FC7DBA53EFB1D74E37E0A3FC3914BEDB0F33BE403D5B512ECAFA7725230C362FB595E9B902A3B9F82F723BE391721BE3F6E14E48D192CBCA7F3236C6D100FF6B8FE8746E33592DF7AC447757BF1A26AD747750B7BC112B5F73BD23F093277C7260EF7DC3F22BA096F920B63ED6EF50B63ED6E073DD29AA31C70013EFD3F44176F641BD9D67EE61687375F28FA293E5DA8BBC3B54209B7338C83A8B71C4BFD0C1E21E2EB773B69BAA89962B2AF47
	757C9B743E293C4B65EF01D4736F9C451CEEF4BB5F67603811DBF11FC3FA8BE451B6647562F47369D8968B61C40EFB161EA54F38C27E960841083CB9D7A05DBBF4BF319942D60E1BCE6FAC7738C27E9608153F316D4369228D080B6738E5548695DEB03EF8219F546313F03E506405FEDD9FCFD27BF5A96FF97FF6327DFB5EFF1C2C766BD25E73FE51E435DF17721E7704495AFB5E974E146A451C094E55B94F0918C79F1E4707A75A3F67BDE122BA0F4AFB5E3F4E34FF4FFBDD26AD9FD5161FAA59F938FBD39DBF
	1BF8CCF61E26E23D63E7931F19A371F9CEF56E1E778FE567E47CF6BAFE96B1FEA75AFDB4377B1CDD766FF92F5925363B721E77533B54F6D75E739E384B165D59B7EC2F8CBCF317F9E5D8FA5515588E22B338AF6115688F786389C29B8461C80058AB511F623099FD921281E7388AE57C590C79B5DF05B222A82D9F04C3GC6F35A08D6240D3F0A3F4153EA7FAD1002B67BEF017C54E65FEF5A234D3E5F743AF168A719FFB56ECB5EBFCDF14F8960FD2EF644B7F90A622E74C06089F7086F1E5D0AFBCE00A75EA13E
	039395771CF80725E7FBBB42F7CA7196C88170D4B84437EAC04ED78BBCCD0370317673246AFE00985AA7DB01F708B72E95BED73DBDFF086F57856924215E9BD35F979E612D5E2F7BB79D4FC683BE469B77BD4A37E19AD957B8FFB7FB7EB56AFD0F65294AFCEE2763E0B150D5DC0379FC33D1194F6DC65441ECC387FB77223BE9F498BBCE2943ACDA3FBD004E1B6B705F83CA9DAEBD406FB85051C13D1F2756A1DA2603F8B73B8165455910A7372D7AEE76E4CD1E2AF55E27BF445F274F38965BED24DDBFA75B757A
	4A092E71FDE8734C34FD3EEBB38663533F4357FFD0BE7DBBFCDF23FCB957BA664BDDF8AD1E49FE1A0E8515572A7D56DE22781AEB55FEEB07CE6098D18FF1A13CDC1A4D2E592947C53EBB1C66E3792AE925776BDC274E6FDE0AAF3CCE1D5F3F73B1318C62FC2E63EF84343A26F755B85D3BEAFD447DC19F7D72ABGFC05AA2E9642CDGD6991E4D07C7CD537A943FB8CD6BFB7C0D4E5D325F695CAD9B39DBCB775CEEADDD77C4ADDD7FC4AD1D170E4F73B5BAF733578EE869768E486958BCEE076CCC724BCE3E0569
	494FBBFB78607A2A031ED5965F42DCA8493EF72BF332CFEC63EBB7AE7B732FA5597B96CB32439A1D13BDE0C45949DCF65FB7126C978BA459638FB8A7BB74G4AB68CC759C974FC30F8D76DF20CB773783932B33EBD7C5DCCD6E6C9F48E6A8734A143791D23A788EE54F079DD35AF50B722585F51323FCDA74E3C7F41697FC19A3BDB25BC7DAC7BBB56FC66537E7799CD031569D1BA8F099DAE3F2B76A90ACB84DC6675D87687D7537D8988D7F0DC966D1FEAA15C48F13756533DF0883BF35C3FA84E8742C19CF799
	FF9FB462FA6C1F2FD41CD94C39A1AC3D7086C7663F9D0AB7B7AA01274EA13EF30CF23EE66099F0086FB1455B821E406367905F1265F23EE060099C040F354DA993746F387BF6827D6E8EB262EF40B9E471896D7D072746A93F0D3CBECE7F5B4867345C73FC416E3EA82FFB356DEFA31750BAD18F34CD1C7E5BE94AF96E9ADAC7BCA15E07530456EB670DAF57633CB11253BD3FD07FFB49BB8B89EDB4504579621A411F3FBB14BD42797B8F8BC6C8F3885117D6674C501DBAE70606EE5352F9EC5352F99B35F4430D
	DA3A6883DA3A578EE869AA17EB69B6AD570E333D64B8B3FE8FBC617C7716EB0BA47EBD2B9D5F9BFAE82944B7EE1A63FBC343CBA43E61754E5DDB75FE3DF23FBB34D412F501936F460E61EFBB9BC660D8798679ACAE36633FABFCB24DC06326D36734FD6D47D6ED19FDEE151759AC2A6B0F73FE0119C040FB42B80B30F0CF0769C84B513E3C9AE317A5EFEB1B455459E3B467D519DA0DEDCDF93B0D9613C5780A9C74EAB31973961548834B6401D2F9405055A9CCD2587431914B5F0C7C7B3E2ED4A1FD2BB01BB45F
	BD26CE30B2B43A26AA2345384F54C9AC35D91850515E8211005FAA24F5511FF1DD16BE3329C648BE3A5B74CB37294BE2EADAEF31F4369C6E36C0C018D1C3D2DA5AEDE9B1AF2DB6F6B5D3E71255B5C2DA1B3195141F46224D4636E3CB77GD75B3155568E529457B7920BD37389206FF42AAF11D24425CCFFF04A62055BE203D305144D42D766F6E353E66391CBFB67D6E10225395354558C955A5A312E128A4A201EC124C7C985F1F3F976C9E60B080B89D77A04F538472438C91BD348E1CCE1A925A43DE79127E8
	82D3F6C2D2264E2DGC6C8B21F471DB35F724D53DBE6B52488E345CC7EA21C9646DBBBACC27AD1E3EB0B398F7E2822B5380B44CC3400361D4636AEF2D08BB44BD3E906D1BC0122AB1EA83E72564FFF2289C82C96E181B3A530AEBDE6EAEFDD3A7E685116B69397GF685717338F8C2D3242219E63DEC4636973FBC8C7520CAC8EDADAE267FAF237F17107F17D14CB20AD9D68246CD906C7ED347DBB14F347200BED328BE24780BB6C389DADEF96F099B7FD611908F395BAC0C033A5EED02C2481228F6F6379EB6F5
	DE90F0653C09DEB19711B26AA0D545CA64CE31F209A407DB5B3ACC162E181E527254B7EECACD9152368A69075BAD1676D6283CE653D18BD45E4E16E34D701BEAE96F20750EDEF98860C3AB8A911AABC89BAD648B64B8C8E31A3697D9FF491817838ED5E52788A910CC3309499D575B52E4E926124905A391DCA2117615E894164E82854D8227B946E9DA8D41CE50D6FF021E44ABA189E5D152E9747F99CE97A9611AC5F9E77312A830F23D5364704753F469D923690A33C653B576B4350D5556F002A5AFD8E40A10
	4E3169B2AC0C13F3EC0E0F653C29102BB4A39421BBDCB3DD5399B45DF62BC2432BF806A70819A566483088D348301C37315DECB651233CB0502AB0F9DBBA5B3BE15419325E8CD6E983D3A0A6F30789B0F92CA7358833EAE0644C2B6DECBF5AE2B6A9C44ED644E80C2AB65CD89B06CBD356B99C8252FBE9A7498CC0F84E0CC5FF345A29819EFF3C9A50E4D5B4887C172A23B706BAF5C2FF28BC35B91227CEBB655474474B135511BC9D7AAD8D0504FEA27FB51C199C3859EB99FF403E515DD65FB878F07887313867
	D95697F97FBF58975D68DCDFF426276DE7CDFFF717637D1DFE3E7F20ED72C16B9FEF827D68D9B3550FB8EBB4FD762C5174A5FB1A1EB52375BF9C9F2D4F02B209BB4B66CA89FA567FC3D97453334C22DF7E51ADBAB67DEC696146CEBAEBB44DBEEBB41D6B7C265B7FE2FEFBB64D85477E776C6A134616383EA27EBF2D2B65E757BAF86C067F775641E3EB7E5F5B139B3B6FEC5F139BFB70ACEB99C77F9FEC999D7F7F8E11CBF6F37B3DF7087ECF4728E1E759DC6032BF7C7A447B2C19CB5FF056E8FA4BD923696D4E
	2FCF7E20E374BDE7D94F7010C32B84017998884212DF4BC42D9BAE7395124E4FC921399134FE0D21CFB23F475162EF1EF047D8B2EECD957E320464B06B293074C2GF2E8F73A701F82620C3A4FF8FCFDC74786F3D367262696CBFBA7844A4B579F366CE9B51A4D962379447A3626ED469E6306BE0B298B68996F5DCB087CAE2491A3D95C83CB085E47E5BC7F9FD0CB87882025EFC0F3D9GG7CE581GD0CB818294G94G88G88G93F954AC2025EFC0F3D9GG7CE581G8CGGGGGGGGGGGGGG
	GGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2DD9GGGG
**end of data**/
}
}
