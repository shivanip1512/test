package com.cannontech.dbeditor.editor.capsubbus;

import com.cannontech.database.db.*;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class CCSubstationBusBaseEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private Integer originalMapLocID = null;
	private javax.swing.JPanel ivjIdentificationPanel = null;
	private javax.swing.JLabel ivjOffPeakSetPointLabel = null;
	private javax.swing.JTextField ivjOffPeakSetPointTextField = null;
	private javax.swing.JLabel ivjPeakSetPointLabel = null;
	private javax.swing.JTextField ivjPeakSetPointTextField = null;
	private javax.swing.JLabel ivjPeakStartTimeLabel = null;
	private javax.swing.JLabel ivjPeakStopTimeLabel = null;
	private javax.swing.JCheckBox ivjDisableCheckBox = null;
	private javax.swing.JLabel ivjJLabelFormatTime2 = null;
	private javax.swing.JLabel ivjJLabelTimeFormat = null;
	private javax.swing.JLabel ivjBandwidthLabel = null;
	private javax.swing.JTextField ivjBandwidthTextField = null;
	private javax.swing.JCheckBox ivjJCheckBoxFriday = null;
	private javax.swing.JCheckBox ivjJCheckBoxHoliday = null;
	private javax.swing.JCheckBox ivjJCheckBoxMonday = null;
	private javax.swing.JCheckBox ivjJCheckBoxSaturday = null;
	private javax.swing.JCheckBox ivjJCheckBoxSunday = null;
	private javax.swing.JCheckBox ivjJCheckBoxThursday = null;
	private javax.swing.JCheckBox ivjJCheckBoxTuesday = null;
	private javax.swing.JCheckBox ivjJCheckBoxWednesday = null;
	private javax.swing.JPanel ivjJPanelControlSummary = null;
	private javax.swing.JPanel ivjJPanelOffPeak = null;
	private javax.swing.JPanel ivjJPanelOnPeak = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStartTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStopTime = null;
	private javax.swing.JLabel ivjJLabelGeoName = null;
	private javax.swing.JLabel ivjJLabelSubName = null;
	private javax.swing.JTextField ivjJTextFieldGeoName = null;
	private javax.swing.JTextField ivjJTextFieldSubName = null;
	private javax.swing.JLabel ivjJLabelMapLocation = null;
	private javax.swing.JTextField ivjJTextFieldMapLocation = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCSubstationBusBaseEditorPanel() {
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
	if (e.getSource() == getDisableCheckBox()) 
		connEtoC9(e);
	if (e.getSource() == getJCheckBoxSunday()) 
		connEtoC8(e);
	if (e.getSource() == getJCheckBoxMonday()) 
		connEtoC10(e);
	if (e.getSource() == getJCheckBoxTuesday()) 
		connEtoC11(e);
	if (e.getSource() == getJCheckBoxWednesday()) 
		connEtoC12(e);
	if (e.getSource() == getJCheckBoxThursday()) 
		connEtoC13(e);
	if (e.getSource() == getJCheckBoxFriday()) 
		connEtoC14(e);
	if (e.getSource() == getJCheckBoxSaturday()) 
		connEtoC15(e);
	if (e.getSource() == getJCheckBoxHoliday()) 
		connEtoC16(e);
	if (e.getSource() == getJTextFieldStopTime()) 
		connEtoC6(e);
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
	if (e.getSource() == getJTextFieldSubName()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldGeoName()) 
		connEtoC3(e);
	if (e.getSource() == getPeakSetPointTextField()) 
		connEtoC1(e);
	if (e.getSource() == getOffPeakSetPointTextField()) 
		connEtoC4(e);
	if (e.getSource() == getBandwidthTextField()) 
		connEtoC7(e);
	if (e.getSource() == getJTextFieldStartTime()) 
		connEtoC5(e);
	if (e.getSource() == getJTextFieldMapLocation()) 
		connEtoC17(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (PeakSetPointTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC10:  (JCheckBoxMonday.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
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
 * connEtoC11:  (JCheckBoxTuesday.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
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
 * connEtoC12:  (JCheckBoxWednesday.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC13:  (JCheckBoxThursday.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC14:  (JCheckBoxFriday.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ActionEvent arg1) {
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
 * connEtoC15:  (JCheckBoxSaturday.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
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
 * connEtoC16:  (JCheckBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
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
 * connEtoC17:  (JTextFieldMapLocation.caret.caretUpdate(javax.swing.event.CaretEvent) --> CCSubstationBusBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (PasswordTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceTapTerminalEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (BankAddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCapBankControllerEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (OffPeakSetPointTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (PeakStartTimeTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC6:  (JTextFieldStopTime.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC7:  (BandwidthTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC8:  (JCheckBoxSunday.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
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
 * connEtoC9:  (DisableCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyBaseEditorPanel.fireInputUpdate()V)
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
 * Return the BandwidthLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBandwidthLabel() {
	if (ivjBandwidthLabel == null) {
		try {
			ivjBandwidthLabel = new javax.swing.JLabel();
			ivjBandwidthLabel.setName("BandwidthLabel");
			ivjBandwidthLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBandwidthLabel.setText("Bandwidth:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBandwidthLabel;
}
/**
 * Return the BandwidthTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getBandwidthTextField() {
	if (ivjBandwidthTextField == null) {
		try {
			ivjBandwidthTextField = new javax.swing.JTextField();
			ivjBandwidthTextField.setName("BandwidthTextField");
			ivjBandwidthTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjBandwidthTextField.setColumns(6);
			// user code begin {1}

			ivjBandwidthTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 99999999) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBandwidthTextField;
}
/**
 * Return the DisableCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getDisableCheckBox() {
	if (ivjDisableCheckBox == null) {
		try {
			ivjDisableCheckBox = new javax.swing.JCheckBox();
			ivjDisableCheckBox.setName("DisableCheckBox");
			ivjDisableCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDisableCheckBox.setText("Disable");
			ivjDisableCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisableCheckBox;
}
/**
 * Return the IdentificationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getIdentificationPanel() {
	if (ivjIdentificationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Identification");
			ivjIdentificationPanel = new javax.swing.JPanel();
			ivjIdentificationPanel.setName("IdentificationPanel");
			ivjIdentificationPanel.setBorder(ivjLocalBorder);
			ivjIdentificationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelSubName = new java.awt.GridBagConstraints();
			constraintsJLabelSubName.gridx = 1; constraintsJLabelSubName.gridy = 1;
			constraintsJLabelSubName.ipadx = 57;
			constraintsJLabelSubName.ipady = 2;
			constraintsJLabelSubName.insets = new java.awt.Insets(2, 25, 5, 2);
			getIdentificationPanel().add(getJLabelSubName(), constraintsJLabelSubName);

			java.awt.GridBagConstraints constraintsJTextFieldSubName = new java.awt.GridBagConstraints();
			constraintsJTextFieldSubName.gridx = 2; constraintsJTextFieldSubName.gridy = 1;
			constraintsJTextFieldSubName.gridwidth = 2;
			constraintsJTextFieldSubName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSubName.weightx = 1.0;
			constraintsJTextFieldSubName.ipadx = 66;
			constraintsJTextFieldSubName.insets = new java.awt.Insets(2, 3, 3, 20);
			getIdentificationPanel().add(getJTextFieldSubName(), constraintsJTextFieldSubName);

			java.awt.GridBagConstraints constraintsJLabelGeoName = new java.awt.GridBagConstraints();
			constraintsJLabelGeoName.gridx = 1; constraintsJLabelGeoName.gridy = 2;
			constraintsJLabelGeoName.ipadx = 32;
			constraintsJLabelGeoName.ipady = 2;
			constraintsJLabelGeoName.insets = new java.awt.Insets(5, 25, 2, 2);
			getIdentificationPanel().add(getJLabelGeoName(), constraintsJLabelGeoName);

			java.awt.GridBagConstraints constraintsJTextFieldGeoName = new java.awt.GridBagConstraints();
			constraintsJTextFieldGeoName.gridx = 2; constraintsJTextFieldGeoName.gridy = 2;
			constraintsJTextFieldGeoName.gridwidth = 2;
			constraintsJTextFieldGeoName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldGeoName.weightx = 1.0;
			constraintsJTextFieldGeoName.ipadx = 165;
			constraintsJTextFieldGeoName.insets = new java.awt.Insets(4, 3, 1, 20);
			getIdentificationPanel().add(getJTextFieldGeoName(), constraintsJTextFieldGeoName);

			java.awt.GridBagConstraints constraintsDisableCheckBox = new java.awt.GridBagConstraints();
			constraintsDisableCheckBox.gridx = 1; constraintsDisableCheckBox.gridy = 3;
			constraintsDisableCheckBox.ipadx = 26;
			constraintsDisableCheckBox.insets = new java.awt.Insets(2, 25, 12, 52);
			getIdentificationPanel().add(getDisableCheckBox(), constraintsDisableCheckBox);

			java.awt.GridBagConstraints constraintsJLabelMapLocation = new java.awt.GridBagConstraints();
			constraintsJLabelMapLocation.gridx = 2; constraintsJLabelMapLocation.gridy = 3;
			constraintsJLabelMapLocation.ipadx = 3;
			constraintsJLabelMapLocation.insets = new java.awt.Insets(7, 3, 11, 1);
			getIdentificationPanel().add(getJLabelMapLocation(), constraintsJLabelMapLocation);

			java.awt.GridBagConstraints constraintsJTextFieldMapLocation = new java.awt.GridBagConstraints();
			constraintsJTextFieldMapLocation.gridx = 3; constraintsJTextFieldMapLocation.gridy = 3;
			constraintsJTextFieldMapLocation.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldMapLocation.weightx = 1.0;
			constraintsJTextFieldMapLocation.ipadx = 83;
			constraintsJTextFieldMapLocation.insets = new java.awt.Insets(7, 1, 10, 20);
			getIdentificationPanel().add(getJTextFieldMapLocation(), constraintsJTextFieldMapLocation);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIdentificationPanel;
}
/**
 * Return the JCheckBoxFriday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxFriday() {
	if (ivjJCheckBoxFriday == null) {
		try {
			ivjJCheckBoxFriday = new javax.swing.JCheckBox();
			ivjJCheckBoxFriday.setName("JCheckBoxFriday");
			ivjJCheckBoxFriday.setText("Friday");
			ivjJCheckBoxFriday.setVerticalAlignment(javax.swing.SwingConstants.TOP);
			ivjJCheckBoxFriday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjJCheckBoxFriday.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxFriday;
}
/**
 * Return the JCheckBoxHoliday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxHoliday() {
	if (ivjJCheckBoxHoliday == null) {
		try {
			ivjJCheckBoxHoliday = new javax.swing.JCheckBox();
			ivjJCheckBoxHoliday.setName("JCheckBoxHoliday");
			ivjJCheckBoxHoliday.setText("Holiday");
			ivjJCheckBoxHoliday.setVerticalAlignment(javax.swing.SwingConstants.TOP);
			ivjJCheckBoxHoliday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjJCheckBoxHoliday.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxHoliday;
}
/**
 * Return the JCheckBoxMonday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxMonday() {
	if (ivjJCheckBoxMonday == null) {
		try {
			ivjJCheckBoxMonday = new javax.swing.JCheckBox();
			ivjJCheckBoxMonday.setName("JCheckBoxMonday");
			ivjJCheckBoxMonday.setText("Monday");
			ivjJCheckBoxMonday.setVerticalAlignment(javax.swing.SwingConstants.TOP);
			ivjJCheckBoxMonday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjJCheckBoxMonday.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMonday;
}
/**
 * Return the JCheckBoxSaturday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSaturday() {
	if (ivjJCheckBoxSaturday == null) {
		try {
			ivjJCheckBoxSaturday = new javax.swing.JCheckBox();
			ivjJCheckBoxSaturday.setName("JCheckBoxSaturday");
			ivjJCheckBoxSaturday.setText("Saturday");
			ivjJCheckBoxSaturday.setVerticalAlignment(javax.swing.SwingConstants.TOP);
			ivjJCheckBoxSaturday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjJCheckBoxSaturday.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSaturday;
}
/**
 * Return the JCheckBoxSunday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSunday() {
	if (ivjJCheckBoxSunday == null) {
		try {
			ivjJCheckBoxSunday = new javax.swing.JCheckBox();
			ivjJCheckBoxSunday.setName("JCheckBoxSunday");
			ivjJCheckBoxSunday.setText("Sunday");
			ivjJCheckBoxSunday.setVerticalAlignment(javax.swing.SwingConstants.TOP);
			ivjJCheckBoxSunday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjJCheckBoxSunday.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSunday;
}
/**
 * Return the JCheckBoxThursday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxThursday() {
	if (ivjJCheckBoxThursday == null) {
		try {
			ivjJCheckBoxThursday = new javax.swing.JCheckBox();
			ivjJCheckBoxThursday.setName("JCheckBoxThursday");
			ivjJCheckBoxThursday.setText("Thursday");
			ivjJCheckBoxThursday.setVerticalAlignment(javax.swing.SwingConstants.TOP);
			ivjJCheckBoxThursday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjJCheckBoxThursday.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxThursday;
}
/**
 * Return the JCheckBoxTuesday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxTuesday() {
	if (ivjJCheckBoxTuesday == null) {
		try {
			ivjJCheckBoxTuesday = new javax.swing.JCheckBox();
			ivjJCheckBoxTuesday.setName("JCheckBoxTuesday");
			ivjJCheckBoxTuesday.setText("Tuesday");
			ivjJCheckBoxTuesday.setVerticalAlignment(javax.swing.SwingConstants.TOP);
			ivjJCheckBoxTuesday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjJCheckBoxTuesday.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxTuesday;
}
/**
 * Return the JCheckBoxWednesday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxWednesday() {
	if (ivjJCheckBoxWednesday == null) {
		try {
			ivjJCheckBoxWednesday = new javax.swing.JCheckBox();
			ivjJCheckBoxWednesday.setName("JCheckBoxWednesday");
			ivjJCheckBoxWednesday.setText("Wednesday");
			ivjJCheckBoxWednesday.setVerticalAlignment(javax.swing.SwingConstants.TOP);
			ivjJCheckBoxWednesday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjJCheckBoxWednesday.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxWednesday;
}
/**
 * Return the JLabelFormatTime2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFormatTime2() {
	if (ivjJLabelFormatTime2 == null) {
		try {
			ivjJLabelFormatTime2 = new javax.swing.JLabel();
			ivjJLabelFormatTime2.setName("JLabelFormatTime2");
			ivjJLabelFormatTime2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelFormatTime2.setText("HH:MM");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFormatTime2;
}
/**
 * Return the BankAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGeoName() {
	if (ivjJLabelGeoName == null) {
		try {
			ivjJLabelGeoName = new javax.swing.JLabel();
			ivjJLabelGeoName.setName("JLabelGeoName");
			ivjJLabelGeoName.setText("Geographical Name:");
			ivjJLabelGeoName.setMaximumSize(new java.awt.Dimension(112, 16));
			ivjJLabelGeoName.setPreferredSize(new java.awt.Dimension(112, 16));
			ivjJLabelGeoName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGeoName.setMinimumSize(new java.awt.Dimension(112, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGeoName;
}
/**
 * Return the JLabelMapLocation property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMapLocation() {
	if (ivjJLabelMapLocation == null) {
		try {
			ivjJLabelMapLocation = new javax.swing.JLabel();
			ivjJLabelMapLocation.setName("JLabelMapLocation");
			ivjJLabelMapLocation.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMapLocation.setText("Map Location ID:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMapLocation;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSubName() {
	if (ivjJLabelSubName == null) {
		try {
			ivjJLabelSubName = new javax.swing.JLabel();
			ivjJLabelSubName.setName("JLabelSubName");
			ivjJLabelSubName.setText("Substation Bus Name:");
			ivjJLabelSubName.setMaximumSize(new java.awt.Dimension(87, 16));
			ivjJLabelSubName.setPreferredSize(new java.awt.Dimension(87, 16));
			ivjJLabelSubName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSubName.setMinimumSize(new java.awt.Dimension(87, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSubName;
}
/**
 * Return the JLabelTimeFormat property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTimeFormat() {
	if (ivjJLabelTimeFormat == null) {
		try {
			ivjJLabelTimeFormat = new javax.swing.JLabel();
			ivjJLabelTimeFormat.setName("JLabelTimeFormat");
			ivjJLabelTimeFormat.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelTimeFormat.setText("HH:MM");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTimeFormat;
}
/**
 * Return the ConfigurationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelControlSummary() {
	if (ivjJPanelControlSummary == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder1.setTitle("Control Summary");
			ivjJPanelControlSummary = new javax.swing.JPanel();
			ivjJPanelControlSummary.setName("JPanelControlSummary");
			ivjJPanelControlSummary.setPreferredSize(new java.awt.Dimension(250, 218));
			ivjJPanelControlSummary.setBorder(ivjLocalBorder1);
			ivjJPanelControlSummary.setLayout(new java.awt.GridBagLayout());
			ivjJPanelControlSummary.setMinimumSize(new java.awt.Dimension(250, 218));

			java.awt.GridBagConstraints constraintsBandwidthLabel = new java.awt.GridBagConstraints();
			constraintsBandwidthLabel.gridx = 1; constraintsBandwidthLabel.gridy = 1;
			constraintsBandwidthLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBandwidthLabel.ipady = 1;
			constraintsBandwidthLabel.insets = new java.awt.Insets(1, 23, 4, 7);
			getJPanelControlSummary().add(getBandwidthLabel(), constraintsBandwidthLabel);

			java.awt.GridBagConstraints constraintsBandwidthTextField = new java.awt.GridBagConstraints();
			constraintsBandwidthTextField.gridx = 2; constraintsBandwidthTextField.gridy = 1;
			constraintsBandwidthTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBandwidthTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBandwidthTextField.weightx = 1.0;
			constraintsBandwidthTextField.ipadx = 61;
			constraintsBandwidthTextField.ipady = 1;
			constraintsBandwidthTextField.insets = new java.awt.Insets(0, 7, 1, 208);
			getJPanelControlSummary().add(getBandwidthTextField(), constraintsBandwidthTextField);

			java.awt.GridBagConstraints constraintsJPanelOnPeak = new java.awt.GridBagConstraints();
			constraintsJPanelOnPeak.gridx = 1; constraintsJPanelOnPeak.gridy = 2;
			constraintsJPanelOnPeak.gridwidth = 2;
			constraintsJPanelOnPeak.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelOnPeak.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelOnPeak.weightx = 1.0;
			constraintsJPanelOnPeak.weighty = 1.0;
			constraintsJPanelOnPeak.ipadx = -10;
			constraintsJPanelOnPeak.ipady = -4;
			constraintsJPanelOnPeak.insets = new java.awt.Insets(2, 10, 1, 10);
			getJPanelControlSummary().add(getJPanelOnPeak(), constraintsJPanelOnPeak);

			java.awt.GridBagConstraints constraintsJPanelOffPeak = new java.awt.GridBagConstraints();
			constraintsJPanelOffPeak.gridx = 1; constraintsJPanelOffPeak.gridy = 3;
			constraintsJPanelOffPeak.gridwidth = 2;
			constraintsJPanelOffPeak.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelOffPeak.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelOffPeak.weightx = 1.0;
			constraintsJPanelOffPeak.weighty = 1.0;
			constraintsJPanelOffPeak.ipadx = 19;
			constraintsJPanelOffPeak.insets = new java.awt.Insets(1, 10, 7, 10);
			getJPanelControlSummary().add(getJPanelOffPeak(), constraintsJPanelOffPeak);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelControlSummary;
}
/**
 * Return the JPanelOffPeak property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOffPeak() {
	if (ivjJPanelOffPeak == null) {
		try {
			ivjJPanelOffPeak = new javax.swing.JPanel();
			ivjJPanelOffPeak.setName("JPanelOffPeak");
			ivjJPanelOffPeak.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOffPeakSetPointLabel = new java.awt.GridBagConstraints();
			constraintsOffPeakSetPointLabel.gridx = 0; constraintsOffPeakSetPointLabel.gridy = 0;
			constraintsOffPeakSetPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOffPeakSetPointLabel.ipadx = 1;
			constraintsOffPeakSetPointLabel.insets = new java.awt.Insets(7, 10, 9, 4);
			getJPanelOffPeak().add(getOffPeakSetPointLabel(), constraintsOffPeakSetPointLabel);

			java.awt.GridBagConstraints constraintsOffPeakSetPointTextField = new java.awt.GridBagConstraints();
			constraintsOffPeakSetPointTextField.gridx = 1; constraintsOffPeakSetPointTextField.gridy = 0;
			constraintsOffPeakSetPointTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOffPeakSetPointTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOffPeakSetPointTextField.weightx = 1.0;
			constraintsOffPeakSetPointTextField.ipadx = 5;
			constraintsOffPeakSetPointTextField.insets = new java.awt.Insets(8, 5, 9, 120);
			getJPanelOffPeak().add(getOffPeakSetPointTextField(), constraintsOffPeakSetPointTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOffPeak;
}
/**
 * Return the JPanelOnPeak property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOnPeak() {
	if (ivjJPanelOnPeak == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder2.setTitle("On Peak");
			ivjJPanelOnPeak = new javax.swing.JPanel();
			ivjJPanelOnPeak.setName("JPanelOnPeak");
			ivjJPanelOnPeak.setBorder(ivjLocalBorder2);
			ivjJPanelOnPeak.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsPeakSetPointLabel = new java.awt.GridBagConstraints();
			constraintsPeakSetPointLabel.gridx = 1; constraintsPeakSetPointLabel.gridy = 1;
constraintsPeakSetPointLabel.gridheight = 2;
			constraintsPeakSetPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakSetPointLabel.ipadx = 1;
			constraintsPeakSetPointLabel.insets = new java.awt.Insets(3, 16, 8, 11);
			getJPanelOnPeak().add(getPeakSetPointLabel(), constraintsPeakSetPointLabel);

			java.awt.GridBagConstraints constraintsPeakSetPointTextField = new java.awt.GridBagConstraints();
			constraintsPeakSetPointTextField.gridx = 2; constraintsPeakSetPointTextField.gridy = 1;
constraintsPeakSetPointTextField.gridheight = 2;
			constraintsPeakSetPointTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPeakSetPointTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakSetPointTextField.weightx = 1.0;
			constraintsPeakSetPointTextField.ipadx = 2;
			constraintsPeakSetPointTextField.insets = new java.awt.Insets(2, 2, 4, 1);
			getJPanelOnPeak().add(getPeakSetPointTextField(), constraintsPeakSetPointTextField);

			java.awt.GridBagConstraints constraintsPeakStartTimeLabel = new java.awt.GridBagConstraints();
			constraintsPeakStartTimeLabel.gridx = 1; constraintsPeakStartTimeLabel.gridy = 3;
constraintsPeakStartTimeLabel.gridheight = 2;
			constraintsPeakStartTimeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakStartTimeLabel.ipadx = 3;
			constraintsPeakStartTimeLabel.insets = new java.awt.Insets(10, 15, 4, 2);
			getJPanelOnPeak().add(getPeakStartTimeLabel(), constraintsPeakStartTimeLabel);

			java.awt.GridBagConstraints constraintsPeakStopTimeLabel = new java.awt.GridBagConstraints();
			constraintsPeakStopTimeLabel.gridx = 1; constraintsPeakStopTimeLabel.gridy = 5;
constraintsPeakStopTimeLabel.gridheight = 2;
			constraintsPeakStopTimeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakStopTimeLabel.insets = new java.awt.Insets(6, 15, 5, 6);
			getJPanelOnPeak().add(getPeakStopTimeLabel(), constraintsPeakStopTimeLabel);

			java.awt.GridBagConstraints constraintsJLabelTimeFormat = new java.awt.GridBagConstraints();
			constraintsJLabelTimeFormat.gridx = 3; constraintsJLabelTimeFormat.gridy = 3;
constraintsJLabelTimeFormat.gridheight = 2;
			constraintsJLabelTimeFormat.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTimeFormat.ipadx = 4;
			constraintsJLabelTimeFormat.insets = new java.awt.Insets(13, 2, 4, 16);
			getJPanelOnPeak().add(getJLabelTimeFormat(), constraintsJLabelTimeFormat);

			java.awt.GridBagConstraints constraintsJLabelFormatTime2 = new java.awt.GridBagConstraints();
			constraintsJLabelFormatTime2.gridx = 3; constraintsJLabelFormatTime2.gridy = 5;
constraintsJLabelFormatTime2.gridheight = 2;
			constraintsJLabelFormatTime2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFormatTime2.ipadx = 3;
			constraintsJLabelFormatTime2.insets = new java.awt.Insets(5, 2, 9, 17);
			getJPanelOnPeak().add(getJLabelFormatTime2(), constraintsJLabelFormatTime2);

			java.awt.GridBagConstraints constraintsJCheckBoxThursday = new java.awt.GridBagConstraints();
			constraintsJCheckBoxThursday.gridx = 4; constraintsJCheckBoxThursday.gridy = 4;
			constraintsJCheckBoxThursday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxThursday.ipadx = -6;
			constraintsJCheckBoxThursday.ipady = -10;
			constraintsJCheckBoxThursday.insets = new java.awt.Insets(2, 16, 2, 41);
			getJPanelOnPeak().add(getJCheckBoxThursday(), constraintsJCheckBoxThursday);

			java.awt.GridBagConstraints constraintsJCheckBoxMonday = new java.awt.GridBagConstraints();
			constraintsJCheckBoxMonday.gridx = 4; constraintsJCheckBoxMonday.gridy = 1;
			constraintsJCheckBoxMonday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxMonday.ipadx = -6;
			constraintsJCheckBoxMonday.ipady = -10;
			constraintsJCheckBoxMonday.insets = new java.awt.Insets(0, 16, 2, 50);
			getJPanelOnPeak().add(getJCheckBoxMonday(), constraintsJCheckBoxMonday);

			java.awt.GridBagConstraints constraintsJCheckBoxFriday = new java.awt.GridBagConstraints();
			constraintsJCheckBoxFriday.gridx = 4; constraintsJCheckBoxFriday.gridy = 5;
			constraintsJCheckBoxFriday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxFriday.ipadx = -7;
			constraintsJCheckBoxFriday.ipady = -10;
			constraintsJCheckBoxFriday.insets = new java.awt.Insets(2, 16, 2, 61);
			getJPanelOnPeak().add(getJCheckBoxFriday(), constraintsJCheckBoxFriday);

			java.awt.GridBagConstraints constraintsJCheckBoxTuesday = new java.awt.GridBagConstraints();
			constraintsJCheckBoxTuesday.gridx = 4; constraintsJCheckBoxTuesday.gridy = 2;
			constraintsJCheckBoxTuesday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxTuesday.ipadx = -6;
			constraintsJCheckBoxTuesday.ipady = -10;
			constraintsJCheckBoxTuesday.insets = new java.awt.Insets(2, 16, 2, 46);
			getJPanelOnPeak().add(getJCheckBoxTuesday(), constraintsJCheckBoxTuesday);

			java.awt.GridBagConstraints constraintsJCheckBoxSaturday = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSaturday.gridx = 4; constraintsJCheckBoxSaturday.gridy = 6;
			constraintsJCheckBoxSaturday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxSaturday.ipadx = -6;
			constraintsJCheckBoxSaturday.ipady = -10;
			constraintsJCheckBoxSaturday.insets = new java.awt.Insets(2, 16, 0, 43);
			getJPanelOnPeak().add(getJCheckBoxSaturday(), constraintsJCheckBoxSaturday);

			java.awt.GridBagConstraints constraintsJCheckBoxHoliday = new java.awt.GridBagConstraints();
			constraintsJCheckBoxHoliday.gridx = 4; constraintsJCheckBoxHoliday.gridy = 8;
			constraintsJCheckBoxHoliday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxHoliday.ipadx = -6;
			constraintsJCheckBoxHoliday.ipady = -10;
			constraintsJCheckBoxHoliday.insets = new java.awt.Insets(2, 16, 15, 53);
			getJPanelOnPeak().add(getJCheckBoxHoliday(), constraintsJCheckBoxHoliday);

			java.awt.GridBagConstraints constraintsJCheckBoxWednesday = new java.awt.GridBagConstraints();
			constraintsJCheckBoxWednesday.gridx = 4; constraintsJCheckBoxWednesday.gridy = 3;
			constraintsJCheckBoxWednesday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxWednesday.ipadx = -6;
			constraintsJCheckBoxWednesday.ipady = -10;
			constraintsJCheckBoxWednesday.insets = new java.awt.Insets(3, 16, 2, 27);
			getJPanelOnPeak().add(getJCheckBoxWednesday(), constraintsJCheckBoxWednesday);

			java.awt.GridBagConstraints constraintsJCheckBoxSunday = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSunday.gridx = 4; constraintsJCheckBoxSunday.gridy = 7;
			constraintsJCheckBoxSunday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxSunday.ipadx = -6;
			constraintsJCheckBoxSunday.ipady = -10;
			constraintsJCheckBoxSunday.insets = new java.awt.Insets(1, 16, 1, 52);
			getJPanelOnPeak().add(getJCheckBoxSunday(), constraintsJCheckBoxSunday);

			java.awt.GridBagConstraints constraintsJTextFieldStopTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldStopTime.gridx = 2; constraintsJTextFieldStopTime.gridy = 5;
constraintsJTextFieldStopTime.gridheight = 2;
			constraintsJTextFieldStopTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldStopTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldStopTime.weightx = 1.0;
			constraintsJTextFieldStopTime.ipadx = 73;
			constraintsJTextFieldStopTime.ipady = 4;
			constraintsJTextFieldStopTime.insets = new java.awt.Insets(3, 2, 3, 1);
			getJPanelOnPeak().add(getJTextFieldStopTime(), constraintsJTextFieldStopTime);

			java.awt.GridBagConstraints constraintsJTextFieldStartTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldStartTime.gridx = 2; constraintsJTextFieldStartTime.gridy = 3;
constraintsJTextFieldStartTime.gridheight = 2;
			constraintsJTextFieldStartTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldStartTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldStartTime.weightx = 1.0;
			constraintsJTextFieldStartTime.ipadx = 73;
			constraintsJTextFieldStartTime.ipady = 4;
			constraintsJTextFieldStartTime.insets = new java.awt.Insets(7, 2, 2, 1);
			getJPanelOnPeak().add(getJTextFieldStartTime(), constraintsJTextFieldStartTime);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOnPeak;
}
/**
 * Return the BankAddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldGeoName() {
	if (ivjJTextFieldGeoName == null) {
		try {
			ivjJTextFieldGeoName = new javax.swing.JTextField();
			ivjJTextFieldGeoName.setName("JTextFieldGeoName");
			ivjJTextFieldGeoName.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJTextFieldGeoName.setColumns(15);
			ivjJTextFieldGeoName.setPreferredSize(new java.awt.Dimension(33, 20));
			ivjJTextFieldGeoName.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldGeoName.setMinimumSize(new java.awt.Dimension(33, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldGeoName;
}
/**
 * Return the JTextFieldMapLocation property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMapLocation() {
	if (ivjJTextFieldMapLocation == null) {
		try {
			ivjJTextFieldMapLocation = new javax.swing.JTextField();
			ivjJTextFieldMapLocation.setName("JTextFieldMapLocation");
			// user code begin {1}

			ivjJTextFieldMapLocation.setDocument( 
					new com.cannontech.common.gui.unchanging.LongRangeDocument() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMapLocation;
}
/**
 * Return the JTextFieldStartTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStartTime() {
	if (ivjJTextFieldStartTime == null) {
		try {
			ivjJTextFieldStartTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStartTime.setName("JTextFieldStartTime");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStartTime;
}
/**
 * Return the JTextFieldStopTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStopTime() {
	if (ivjJTextFieldStopTime == null) {
		try {
			ivjJTextFieldStopTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStopTime.setName("JTextFieldStopTime");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStopTime;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSubName() {
	if (ivjJTextFieldSubName == null) {
		try {
			ivjJTextFieldSubName = new javax.swing.JTextField();
			ivjJTextFieldSubName.setName("JTextFieldSubName");
			ivjJTextFieldSubName.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJTextFieldSubName.setColumns(15);
			ivjJTextFieldSubName.setPreferredSize(new java.awt.Dimension(132, 20));
			ivjJTextFieldSubName.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldSubName.setMinimumSize(new java.awt.Dimension(132, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSubName;
}
/**
 * Return the OffPeakSetPointLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOffPeakSetPointLabel() {
	if (ivjOffPeakSetPointLabel == null) {
		try {
			ivjOffPeakSetPointLabel = new javax.swing.JLabel();
			ivjOffPeakSetPointLabel.setName("OffPeakSetPointLabel");
			ivjOffPeakSetPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOffPeakSetPointLabel.setText("Off Peak Set Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffPeakSetPointLabel;
}
/**
 * Return the OffPeakSetPointTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOffPeakSetPointTextField() {
	if (ivjOffPeakSetPointTextField == null) {
		try {
			ivjOffPeakSetPointTextField = new javax.swing.JTextField();
			ivjOffPeakSetPointTextField.setName("OffPeakSetPointTextField");
			ivjOffPeakSetPointTextField.setPreferredSize(new java.awt.Dimension(75, 24));
			ivjOffPeakSetPointTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjOffPeakSetPointTextField.setMinimumSize(new java.awt.Dimension(75, 24));
			ivjOffPeakSetPointTextField.setColumns(6);
			// user code begin {1}

			ivjOffPeakSetPointTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( -999999.999, 999999.999, 3) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffPeakSetPointTextField;
}
/**
 * Return the PeakSetPointLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeakSetPointLabel() {
	if (ivjPeakSetPointLabel == null) {
		try {
			ivjPeakSetPointLabel = new javax.swing.JLabel();
			ivjPeakSetPointLabel.setName("PeakSetPointLabel");
			ivjPeakSetPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeakSetPointLabel.setText("Set Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakSetPointLabel;
}
/**
 * Return the PeakSetPointTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPeakSetPointTextField() {
	if (ivjPeakSetPointTextField == null) {
		try {
			ivjPeakSetPointTextField = new javax.swing.JTextField();
			ivjPeakSetPointTextField.setName("PeakSetPointTextField");
			ivjPeakSetPointTextField.setPreferredSize(new java.awt.Dimension(75, 24));
			ivjPeakSetPointTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPeakSetPointTextField.setMinimumSize(new java.awt.Dimension(75, 24));
			ivjPeakSetPointTextField.setColumns(6);
			// user code begin {1}

			ivjPeakSetPointTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( -999999.999, 999999.999, 3) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakSetPointTextField;
}
/**
 * Return the PeakStartTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeakStartTimeLabel() {
	if (ivjPeakStartTimeLabel == null) {
		try {
			ivjPeakStartTimeLabel = new javax.swing.JLabel();
			ivjPeakStartTimeLabel.setName("PeakStartTimeLabel");
			ivjPeakStartTimeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeakStartTimeLabel.setText("Start Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakStartTimeLabel;
}
/**
 * Return the PeakStopTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeakStopTimeLabel() {
	if (ivjPeakStopTimeLabel == null) {
		try {
			ivjPeakStopTimeLabel = new javax.swing.JLabel();
			ivjPeakStopTimeLabel.setName("PeakStopTimeLabel");
			ivjPeakStopTimeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeakStopTimeLabel.setText("Stop Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakStopTimeLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.capcontrol.CapControlSubBus subBus = (com.cannontech.database.data.capcontrol.CapControlSubBus)val;

	String subName = getJTextFieldSubName().getText();
	String geoName = getJTextFieldGeoName().getText();
	Double peakSetPoint = null;		
	Double offPeakSetPoint = null;

	try
	{
		peakSetPoint = new Double( getPeakSetPointTextField().getText() );
		offPeakSetPoint = new Double( getOffPeakSetPointTextField().getText() );
		subBus.getCapControlSubstationBus().setBandwidth( new Integer(getBandwidthTextField().getText()) );
	}
	catch( Exception ex )
	{
		System.out.println("Invalid date entered by user in " + this.getClass() );
	}	

	

	String daysOfWeek = new String();	
	if( getJCheckBoxMonday().isSelected() ) 
		daysOfWeek += "Y";
	else
		daysOfWeek += "N";
		
	if( getJCheckBoxTuesday().isSelected() ) 
		daysOfWeek += "Y";
	else
		daysOfWeek += "N";

	if( getJCheckBoxWednesday().isSelected() ) 
		daysOfWeek += "Y";
	else
		daysOfWeek += "N";

	if( getJCheckBoxThursday().isSelected() ) 
		daysOfWeek += "Y";
	else
		daysOfWeek += "N";

	if( getJCheckBoxFriday().isSelected() ) 
		daysOfWeek += "Y";
	else
		daysOfWeek += "N";

	if( getJCheckBoxSaturday().isSelected() ) 
		daysOfWeek += "Y";
	else
		daysOfWeek += "N";

	if( getJCheckBoxSunday().isSelected() ) 
		daysOfWeek += "Y";
	else
		daysOfWeek += "N";

	if( getJCheckBoxHoliday().isSelected() ) 
		daysOfWeek += "Y";
	else
		daysOfWeek += "N";


	subBus.setName(subName);
	subBus.setGeoAreaName(geoName);
	subBus.getCapControlSubstationBus().setPeakSetPoint(peakSetPoint);
	subBus.getCapControlSubstationBus().setOffPeakSetPoint(offPeakSetPoint);
	subBus.getCapControlSubstationBus().setPeakStartTime( getJTextFieldStartTime().getTimeTotalSeconds() );
	subBus.getCapControlSubstationBus().setPeakStopTime( getJTextFieldStopTime().getTimeTotalSeconds() );
	subBus.getCapControlSubstationBus().setDaysOfWeek(daysOfWeek);

	subBus.setDisableFlag(
			getDisableCheckBox().isSelected() 
			? new Character('Y')
			: new Character('N') );

	if( getJTextFieldMapLocation().getText() == null || getJTextFieldMapLocation().getText().length() <= 0 )
		subBus.getCapControlSubstationBus().setMapLocationID( new Integer(0) );
	else
		subBus.getCapControlSubstationBus().setMapLocationID(
				new Integer( getJTextFieldMapLocation().getText() ) );
	
	originalMapLocID = subBus.getCapControlSubstationBus().getMapLocationID();

	return val;
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
	// user code end
	getJTextFieldSubName().addCaretListener(this);
	getJTextFieldGeoName().addCaretListener(this);
	getPeakSetPointTextField().addCaretListener(this);
	getOffPeakSetPointTextField().addCaretListener(this);
	getDisableCheckBox().addActionListener(this);
	getBandwidthTextField().addCaretListener(this);
	getJCheckBoxSunday().addActionListener(this);
	getJCheckBoxMonday().addActionListener(this);
	getJCheckBoxTuesday().addActionListener(this);
	getJCheckBoxWednesday().addActionListener(this);
	getJCheckBoxThursday().addActionListener(this);
	getJCheckBoxFriday().addActionListener(this);
	getJCheckBoxSaturday().addActionListener(this);
	getJCheckBoxHoliday().addActionListener(this);
	getJTextFieldStartTime().addCaretListener(this);
	getJTextFieldStopTime().addActionListener(this);
	getJTextFieldMapLocation().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CapControlStrategyBaseEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(408, 374);

		java.awt.GridBagConstraints constraintsIdentificationPanel = new java.awt.GridBagConstraints();
		constraintsIdentificationPanel.gridx = 1; constraintsIdentificationPanel.gridy = 1;
		constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsIdentificationPanel.weightx = 1.0;
		constraintsIdentificationPanel.weighty = 1.0;
		constraintsIdentificationPanel.ipadx = -10;
		constraintsIdentificationPanel.ipady = -9;
		constraintsIdentificationPanel.insets = new java.awt.Insets(6, 8, 1, 8);
		add(getIdentificationPanel(), constraintsIdentificationPanel);

		java.awt.GridBagConstraints constraintsJPanelControlSummary = new java.awt.GridBagConstraints();
		constraintsJPanelControlSummary.gridx = 1; constraintsJPanelControlSummary.gridy = 2;
		constraintsJPanelControlSummary.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelControlSummary.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelControlSummary.weightx = 1.0;
		constraintsJPanelControlSummary.weighty = 1.0;
		constraintsJPanelControlSummary.ipadx = 142;
		constraintsJPanelControlSummary.ipady = 36;
		constraintsJPanelControlSummary.insets = new java.awt.Insets(2, 8, 7, 8);
		add(getJPanelControlSummary(), constraintsJPanelControlSummary);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	boolean amfmInterface = false;
	try
	{	
		amfmInterface = com.cannontech.common.util.CtiProperties.getInstance().getProperty(
			com.cannontech.common.util.CtiProperties.KEY_CC_INTERFACE, "NotFound").trim().equalsIgnoreCase(
				com.cannontech.common.util.CtiProperties.VALUE_CC_INTERFACE_AMFM );
	}
	catch( java.util.MissingResourceException e )
	{}
		
	getJLabelMapLocation().setVisible( amfmInterface );
	getJTextFieldMapLocation().setVisible( amfmInterface );

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldSubName().getText() == null
		 || getJTextFieldSubName().getText().length() <= 0 
		 || getJTextFieldGeoName().getText() == null
		 || getJTextFieldGeoName().getText().length() <= 0 )
	{
		setErrorString("The Substatin bus Name text field and Geo Name text field must be filled in");
		return false;
	}
	
	try
	{
		Double.parseDouble( getPeakSetPointTextField().getText() );
		Double.parseDouble( getOffPeakSetPointTextField().getText() );
		Double.parseDouble( getBandwidthTextField().getText() );
	}
	catch(NumberFormatException nfe)
	{
		setErrorString("The PeakSetPoint, OffPeakSetPoint and Bandwith fields must all be valid numbers");
		return false;
	}
		

	//if we are using MapLocation IDs, we must validate them!
	if( getJTextFieldMapLocation().isVisible()
		 && getJTextFieldMapLocation().getText() != null 
		 && getJTextFieldMapLocation().getText().length() > 0 )	
	{
		int[] mapIDs = null;
		
		if( originalMapLocID != null )
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs( originalMapLocID.intValue() );
		else
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs();

		StringBuffer buf = new StringBuffer("The MapLocationID selected is already used, try another\nUsed IDs: ");

		long mapId = Long.parseLong( getJTextFieldMapLocation().getText() );
		for( int i = 0; i < mapIDs.length; i++ )
			if( mapIDs[i] == mapId )
			{
				//setErrorString("The MapLocationID selected is already used, try another");
				for( int j = 0; j < mapIDs.length; j ++ )
				{
					if( (j % 20 == 0) && j != 0 )
						buf.append("\n  ");
						
					buf.append( mapIDs[j] + "," );
				}

				setErrorString( buf.toString() );
				return false;
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CCSubstationBusBaseEditorPanel aCCSubstationBusBaseEditorPanel;
		aCCSubstationBusBaseEditorPanel = new CCSubstationBusBaseEditorPanel();
		frame.setContentPane(aCCSubstationBusBaseEditorPanel);
		frame.setSize(aCCSubstationBusBaseEditorPanel.getSize());
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
	com.cannontech.database.data.capcontrol.CapControlSubBus subBus = (com.cannontech.database.data.capcontrol.CapControlSubBus)val;

	Integer bandwidth = subBus.getCapControlSubstationBus().getBandwidth();	
	String subName = subBus.getPAOName();
	String geoName = subBus.getGeoAreaName();
	Double peakSetPoint = subBus.getCapControlSubstationBus().getPeakSetPoint();
	Double offPeakSetPoint = subBus.getCapControlSubstationBus().getOffPeakSetPoint();
	Integer peakStartTime = subBus.getCapControlSubstationBus().getPeakStartTime();
	Integer peakStopTime = subBus.getCapControlSubstationBus().getPeakStopTime();
	String daysOfWeek = subBus.getCapControlSubstationBus().getDaysOfWeek();
	
	getBandwidthTextField().setText(bandwidth.toString());
	getJTextFieldSubName().setText(subName);
	getJTextFieldGeoName().setText(geoName);
	getPeakSetPointTextField().setText(peakSetPoint.toString());
	getOffPeakSetPointTextField().setText(offPeakSetPoint.toString());
	getJTextFieldStartTime().setTimeText( peakStartTime );
	getJTextFieldStopTime().setTimeText( peakStopTime );

	if( Character.toUpperCase(daysOfWeek.charAt(0)) == 'Y' )
		getJCheckBoxMonday().setSelected( true );
	if( Character.toUpperCase(daysOfWeek.charAt(1)) == 'Y' )
		getJCheckBoxTuesday().setSelected( true );
	if( Character.toUpperCase(daysOfWeek.charAt(2)) == 'Y' )
		getJCheckBoxWednesday().setSelected( true );
	if( Character.toUpperCase(daysOfWeek.charAt(3)) == 'Y' )
		getJCheckBoxThursday().setSelected( true );
	if( Character.toUpperCase(daysOfWeek.charAt(4)) == 'Y' )
		getJCheckBoxFriday().setSelected( true );
	if( Character.toUpperCase(daysOfWeek.charAt(5)) == 'Y' )
		getJCheckBoxSaturday().setSelected( true );
	if( Character.toUpperCase(daysOfWeek.charAt(6)) == 'Y' )
		getJCheckBoxSunday().setSelected( true );		
	if( Character.toUpperCase(daysOfWeek.charAt(7)) == 'Y' )
		getJCheckBoxHoliday().setSelected( true );
		
	
	getDisableCheckBox().setSelected( subBus.getDisableFlag().charValue() == 'Y'
						||  subBus.getDisableFlag().charValue() == 'y' );

	//set our mapLocIDs values for all variables
	originalMapLocID = subBus.getCapControlSubstationBus().getMapLocationID();
	getJTextFieldMapLocation().setText(
		subBus.getCapControlSubstationBus().getMapLocationID().toString() );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G82F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8DDCD4D57ABF35DAD456DAE129D9D1E151EF2D2C580D0A0AC88D135D2855B223224D0A76EFAD7D577D7D28F56BB240000AC90A0A2509EF050988B83CE4243CE928A0A828D8D60E4EC5C687E61C394828187F671CF31FF36F5C39B33C6C5F7D787373F93CF33E67BCAF671C673C5C73F29132D78427A59B3285A1B9CD107F15A58BC27988C1F0F7ED9D01D80098E594D47FD682FDAA18162442F3A844
	457FAB4A18A50CBC1E81619088E77E1FA86392783DD038757821417043079E9344E50FFE66E97FFADEDB07FA52E9F87C6ECCF8FE82948A98F94E94097ED7F66748F80C0CA3A59B84E1FA9BC91334FB190C15C33889A898A82C0D250F0167EC2AF97066F2B95D21EB0305194FFCE518C76428E4A2659FE33CCD72B3D738AFB96998513532B1D3668904F0A49049FC0A30332A961E95CDA70EEDB9EA6FA8B8EA6E6C34F7CA6251768251E2156CCEC09C2E2EA3C73ADC1BB75754B4F79DF1C9E649EA6F2C6EF2D51BDD
	62B61A28495CA95A24F622475A18C16502CC888BD602957ECAF407C3B896A801637FC371DA78FD88C832E2FE3F7B2AC24ECF6B597B04D9AFF6264C77115FA1D6164FB02B125F202E8F5317523AD9311E54C1A244E59F0FB266815583590006GFDCEFD624B2F3F06E7559129465E51E16F2CF2B82AED96E7035DA25A6077664D90A3E3FB4523F627C590D83DFF55182FE30FBFE2FCA57D66E3E5923A89FDE04C5EBB053887F79952F54AA4AE345090F21C15C544F125CC186ECE1F3A630EFB7A28276E8302DA770C
	4B6CA9E9BA3A43262CB3A44AF2D2CFA83ACDA7C8DD07503ADE833F13E57FE0783D94F72BF0662F2594A761C0081BF182FD2375933425BEF52410D5D42FDB8E43232A8CCDB6E6C33BCDEB4B5F4F9359C1B626F3B537E51EC07069AA1C5972AFEA8BCD57816D34B34A487CABEF83E93FB6FB14B190A898A89CE8AA50CC208C877A187D6B0DFD7431A633D32A6E325AAC22D3E03CF96B9743D3F4CBCE730ECEE8FB1DC7457DE62755FC44A63A84B9CD51FAD286C7BC78CA296F9F859D5F0B1D221336D910EE6E90A551
	69E2E9362E7F1424F10AB436455AA102028E0700F23FDF3F10644F6C1276B9ACE6C9D4D23078467524CECE197703C490G3FD9396CABA0651587792F84EA6265708C1537CBF4C2EF31F9F33D5DFE224B51C2F2A284BBC8B98F26FD0785FE77B8300C77ECA458F4884F62F2127397286454DA0F92734D4E5358467E6BD752C7E71DC41FF92F95FD26F65EE8A1FF4ACEDD1FA1D4F91279CA6BC9ED5B8923B209DDACDC71EBEBAF6DB66B24FE1B6AFBDF312FD79DA983147F892F2BA78AC9DD85002D2300A2CEE299D7
	6467B21FA53DBCFAE923939CCC34A8A54E6C7C60FC2DECCB1AAC176102C07A6D35D41EA0AC82A2637086593F16832DGAA825AA430711571AD94083C49E04BAC20B5CE683701A420D1C06300E6GE53B220C95C09620A1C0E1C0F1C029D214B19F28C5423ACAB91F152C14D71A2CC7DD9673E5FB55F6AE555839D2CE3BD6433BC1FEE2BE86DA9F7FE9E2E533532BECE6EB52113946DCCD7924EB4AE7352A9C553CCB64273A7C7EB790EB83459BC8FE44D3E227D4ABBA44CE8B7CF049F15FECA065A4774BEDF64E33
	15628E6847EB2063F4F1FC5B86D2FEE6C9F2DA0FF4C9E233A8C9564EE3CAFC835585B3AD07B284202E168D24DE6C5D500F737487B710BE70283D33D3BC4A66E3B20B9C7FA36D03C51B5881C657583BBAA5061B8BC8BFF6C2BCAD0F81E28119641EB25B3A48F8426604E34D243E9DCEEB87B4F1B2A4897192691FC7B566284A2887B0B2378D03E73404FDED77EA6CEB571C39CD087CE52DC1EF2EB0FCF42DA12F0B750F95DDCADF4BEC5A3FD15F76C3F4AC10F1D78A456A96913F2AB1BBEAE05A6A345B1A21C2A471
	58E94D64D4407EAE290848BE84BA9DC001DD585FCD5DC862F1CC11BB3A9A3B4D460A17177B7842ACD2CF6249AE9106E2B2F86D62E599D338C09337DFAED796BF3910142BCDB56E19CE11F2ED9170BDA0986C89871A5A454AB67B94AB9B16EEC9B2268B007070D33E52CD626942A19C8B14F8CAAF5DB586CC175F9DE52C6D76152EBA8553D9A0CD8F5028EE3DF446D4CC978771B301325DFA69A672F4CD905F8E94281B6E6AB4CC978D7171BE53D599B11DF0BA4A18F35AD73AFF70F465102649E73A30F4CC67A0E9
	FC26EB4F40F42110A652E73AE533B1DDBC24C97219AE16274BB993E5ACBC63AB5D20B93C7CA0CDBBC9F7C6AF5D77F3B15DB8080F831A251BEEEEA6262BB88B63A610F456B39D6B9752FFA16F8FFA6F0A473A2C85DD12154C464C92F4080EAEC975CEE8EC4A10E50CBD0B6FFE394D249DA610F08F54D98F769F4F4D43F73F209341C27C9D1F79FC57AD6CE1363574A87DC76EEF705D2FFA93519984F1A1BD58469B0B54734FAA174BFE54CA273EF27B7C28C85D3FB0AC25F88EBC5B2CCE17348386GB73665594544
	DEB2B716E179DD619CFD07A0F98D873D09C00167282F1C4AD6591E87D8931084447A316982F9C7B8EAF60AD2817D1FCD39730E1BCF195D852EEE98AC8A68C8D4D0C3E237111F6B64FA9937EE8133D124A5AFF32E5B0F6FEF37AF2C1475465CBBD228B83F502077BE9DB8FF39A1769CAB4B24F3CA1976BCE0CC197173BF53D97EAA13B1FF353FC2DF70AB666F815D7C05C1BC631B49792281934EA35F3286ADDFB9449DBA0F657223E1A06542F4BEE4C01D8E10974AF5A64E556A0C073854G3475C48A7265G5694
	00FCABCB54FC0181AC0FE383505667D30A61E92683C681FBB4094EB63B33C334D0038B4C5D12ECED950D164D156D5D180AFA47014CD9DC6F76EF35761AAE09B236DE02FAEFC89DF8997DDF2E538672C2AEC11D3FB7E873CA6AA50E6BBC18766F65758FE928B791E466DD0AFADBFE5653FB78D22C1B63C66413G9B4379C6F46B71CD67FCDF24A3DF92E0053FC13E1AD2BD3E765FA05FAB1C2F8730B04ED7D52D47B70373DD1B01FC42A0089B447D3DCC0F4FB18879364DC63EC100C5F03E353AF64E64FC9FF03E0C
	41705EBD98795E57AD9777E0643BE78E728D81EC9C67DB68D17F3386B33E0A4B307E33678E5C679CF3D1E793488B3C8CF56E2952539949F53EB8775F73B9D3A66A1D8CB2B3AFC73D0EAE3D3E2B76F254FBD366C0724A5E2F66B6E9575CDAAFD75685EC17FB2F91303528A0BABE86DC4E562252B8FEAB4503D4B84B5B174D1837D0084B8C44751367CCE44E6E0070902090208820B820D901B8AEDEDBCC5EDB7D2ECF6D00F15E59E936B5FB3C2060BA51A6D2CE70CEA107F7EDA2F650F78A815FB3FFA3E84BA1678A
	4F3C1BB4E146F72D97DF0136BCB5FCECCCDC1BC766B4F42E222A264D1BF73640BB5BF62BE83330F71A3FB42B5FE99CD7303527A0F8068231794B2D27483C004CC3A22F40F94A396D6AF9CA447CE283195B6B4DD356DC496C2B3DD299D373799A7559D2E267A0088B868A871A8C148414FD95787FD5D8C7D3B6FD57DB9DD13FEB3157GD6876F98B55AF7C3DE265B1387D697ADDEFCDEF5F115A79FAB3FF7F879B92E42B5198F87E8C30621B79BA6EBECE075FC67D6EDBD13B1F486249506E0BDB7B790BBC9BD668C
	C1BB9F735257B7BBA775EAE75390BDBB6D15FAF636C35A9EEE67D719E8E7903773E44A406CBC6F452735B3E608DE7B5A321F7825CE7B2AE9970F1E28363B653AEFADA13612B6B31D5BDA12BAB0DBEB3C783436AE3BDA2FCCAF5D245746CD10366EEAAC53F35FE648FA8EDF0DF6A68D50CEE3DAEFF68E3FDA2F1D4CB0E03D0E65B6C4F8496A1B8D4F74EAC3022E8DD77D0CB68857208D8E6340EC78550BCFEBC379B5FAB644F6238DCD5C06B569833321580BCFEBC3202E8DE7CA5106E0EE439B8334617DDEED182A
	EB033B9AEDC864B65C18B1B09BC6FB71E9ED487B2D1E8D4DE5E8C365EF5106DD3387E643C1AFBE2D8DEEDD9B8E713A984AED78F800B64C1B531B8D513AB664395106F8EE4303DE327AE643133D5A10B3D42F4F7DFAB959E3703F7E224CEF48EE5D36CE09EC0B49F958E9403D7942211807D3F3871607C1DEFC5ABC348F55AB4740AA34A1005B30B9F3E0B654F971E9ED885735612EAE34A19662595E63AA3AC615F1ED14F15935083D3B07E01590EE3D96ED9D59CC309E880F6258E80AB5496F658C4BEF6EED6F0E
	487097CFE8140FB4CC4736DEF574E5FFB04D874E661D53CC3E77C0DEB7696F013CED525F83F95774A943C106FA279B637FE1723FC772AF135E9E49F21A4F090B67D359AE17682293E13290A9473DAD11BDAB2F3D125D668ED1AE230542A3F40EC252EC136CB5458236393F2FA3F3871DF90EEAFD7333961CBBC4DC0B6B1BEB687E4BCC88E7DE97E5DCF39D7AC548A9FFF5D6F11DE7FDB749E18BBC5D1A38A1908E56E06C1D42D0CE5EE743A0AE96A881C838BE4A18FBBD3ECBDCDF526B7B5EB6371F773D41253EFD
	E2C8293ECFDCD7CA667FAEFB177328282D779125B9BCCEF9EB59A1099D240AD81A5BA93FE4F69E933D7C66EE4A4F62FC71BFD06A4B37D87D1FCBD6577FA65E465E73DBDF1557FB16FF0B9CEE07E70FA6EEA804C3B49853DDE4D06BA6FDBC732BD73722DF05DF0FFE35211E78D5A204B30220CE03502FDA9B30AFAC872CBE48D34FE188CBAAACB08857B6E6FA68B6F15DF7D5226ED1C1287BAB3A1EBA9942B300D207713E0E77434BGAB9C66293B96422DAA4CB68CF5072628F5FF434B7C2792F53F9EA07386494F
	D0F8C68C53ABC7DB2ADADE994F4BA31BF06C0E9D06F959E6A2F94938814A90287C864C4B255F2AF5B75D60294722893BEF403CECCCD36BAE67F9F95F6BBD1036DD608B868A1365C54B4FB8AE6F6FE9F354BED10872067E2CA7AF9178D2EF0CB2665C4864644B4F4A9BD15EDDC68FF9C5A8EFCA371E3C9660EB876A11658C151FA1DC5E0F699E720A797BE4291E3CF0600B810A17654C141F9943D15EE7999E72B6213CF355FA721681DFA1D06DF0793C0E7C34F1F9FF7214E7C2F92DE5FA728200AF88A8D41693A1
	BFE3393CCBE7FB48ABC1F9E65D7CCE873EA4204C91721A0F7CB40DC0F925F3BC6415223C8DEEBDF9F540F78848A14B99A4BF07F3F9FF1D23763F926EFF0D3A7E07B4E604777C02493BE72EDADEA917F7F515BFF911BE651D4C7448EF994A9B5365CF5EE41F723EE9A26796EC665376AE897CF5A4E953CDCD781E1FGFC82E0F9C095A355FB1877F713FE204DE96E006E3F59A19E958AEFA23C29FB11F794249F8B948B14C8645E04E57C58BE32FF38038CF756B66BD179CC9DB9DD4062DDDF13F1174D8C70B8C2D7
	C787BFA73522C51DD7B7489DF213123F601B3C734A56FCC369DCA26CA63666BB1F6361940FD361ECAD78EB0A13F06A28A8E340A81C4FCE206BF033A01CFDB3E26F1B881687618A0EED26EBB6CD9036F1AC0D4E1983A0BC14E3376E4379F18847E63888B69342193760FCE3CE396F794227657A7305A5657AF34865657E6710EB4BFD0D734C3E2CEEAC035CDB5066F9A5D886A60E6D61E5D04731BBCEE1999C62584A83D8868E0E95F7E399C45E42578E362A6BFEB2608937A875BD6B96DFF53F3C0154E5562D2C0E
	D7F23C146207D4B82B7BCDF4FEC042219097960CB659695AE664EDD146385B904BC9A5D8A204055B91FB420C794966580B336939CC8877F0EC4196028D05F0486D1847B1156ABC06839EF33B122F785BFD6571A6BA2F18F93BF62F63CE0AE75C215D6B584077F517C1DC409DE8D3CDA666F1BA47CE6CA0D89204B3C290ABDA0BF9DC4631830D18C79B47261C23758D61A00EC954AFC2A19C4E3129B9840B01F09C473EDB0ABA92B8A650F50C7251B0BF9B0DE57644A6AC33D640ECC0812315F28A025F2123BD4B0D
	154D43F47E92B11A154DEC0ECF2078D4954E4AEC9745C9F8A644355E09B60D206B1E91900E61D83291ED0F6758AEBA97AD3C8B465C3B904BC8A1D8AB045BB9E625188461A00E0525203CD00E5DCC6717C9904E89C5AC01E279902E6758DDBF230E439C4B7885F558B8567C8B6A8860182B94F544F26C71D254B11DE3B33AD1C792479A5C28A375EE44063BD1C78E47FACAD0C7BB471EAEC19DBD9C3B2194F58C65587F14220ED19C2BAEC39DE3B916E9C09D42FFF1FFB5200EEC0E85D5230EBC0E2556208E1342DB
	03BA6AB8567E996A984E3177FEC29DE3B8E6AFC39D119C0BADC39DB19C7B3C1C7B904792CF210E425FA176EDB76A2865181B17D5AB47DE62E5E561589ADED6EE0E55F5230EE80EDD4FF544F12C560DBA92B9B61A5779AC0EA572BA4F9A0358C8DE6707B976A62FF309E3ED86648D64D8C895E2D3B976D497E2F977603E3C3B896F6B38819BFB8F5E57A9BA6C312FC9F63CB7EF7E5EE935D41B0F55535990FB6709246BD904BF619EFC67F90F4A4C3D975EF7001A6E453DCC77173817B9664E11C2FD40AE5DB3D771
	AD450676FBD9DFB464DEE5AFB3346B43F426736B3D28B39A62057B687E4FDE755EBA19A73DEBE06BF4AC7ECA0FF8B2F62F935471919E71445E2294F57CFB9E71E9AAFE1637C9EBB1EF33A6058A365FFF6373BCD95DFD72BB047CEC5A3FDAD8F365BF53D99D1552B51E90088B3F8F6B0D15F19C0493B936E1AF9FBF47E29FFFEF8129B38B0483000201A20062001200F26E47F40F53F4957733B320076EE7E7BC49F94B1E7B5939BC162E30005C51A03C3E664D96D93E9AA3FA78DCD39597A15B65CB96127CBEC1F5
	4F73130E5471DC8DB6DFA3C7DD1F3E569F3D677C4FEEA4F2BB4CEEEBC7D7C733750CA8445DCF4A7CEF34DF99853FC36F4739FAC68951F354EE6B6A68F44969D650B6CA4E2267BE00696E7E0A6ECBBB45B6516994ADD42AA027BFCA5397C25A8A1EDEDCC44ECF37416C1E25A920ED379D62A5203887887607FD35FE7212A6A8E76D49B394F8A2009649D8AC7C9EAC7BA85379914B3735D3136FC592513D66C168CF0198768D6D6B8EC1381DE325BC5F23B8F606E2E3A09C4E3198DA0E53A11C78A066257CC0EFF9C9
	D365E5CD18E7DE4AA13CAB1965056978115AD78BF8D39836198F695C228742C3B936E4A54142A09C4D319BD6E15B1A9106EDE1F5C3A5E9B33F07768294G948C94899487B48BE8599F203F83EA81F2G8D859A83948314G1471903F53C3E565BD444ECC5741339DA890A874A1F68E181CC10E144F9813F33C640CAEB9C7CC4EB04749E7F1192CEF6A05E4251C08CE7CCD74B97E605D5E083DE3E4BCC1A75E9F156B24A779A3F9A7E5630BAF52C7DC0BCA5EAC39DC7BE247A1397CBD7B0C9E0F32D82ACACF5ED316
	6848A1ED72F379770AFE1443A7823B232245168BECEC71379F226D33109629FE4F973C6F8E79EB9B6A3BB42CADDF56603B3F39153E23C503FFCCFE887B1B379ABC7B1BAA7A4EA604437BF5B83651974CFD69EF304F29843E962055F238BD1C4DAD147E663D597A7D0DF18E1D9FC17A00F034EF5D966FFE36F28B1E7799934E67B8155846A3B816D605EDBC06E3672B310DA706E3DB3A0D4D239E067956437CDD13E2039EE6779698364960EB2D1E7076470754F4D866E5BA6AFD6A78F76E626198484A4FC65FE3C3
	C06F5807311C6BD2BC7DA0083E5746C1FC42436887F5EB7D79C10A2A0E89ADFB847A2FC794BF28055FF8060D6988206F44CD009FFA846D18F14E3B3ED3FE457A8E64692EAA713477B6BA5E8C0778901E66EFB9A40DA43A25490F103B7617ECA52E4D72523234373C242B7292219AC3097DB90FC699CBBCFCFAC9A9D9AF6CB0BB0FD9BB850169CB2FA27A185F3DC0778856GDF6523680B77CBB867EB61D852961C7339B9662CC41F9D453111CB88B6834229917CDD5FC46E8E65C058845482E463F1B1B4AEB0025D
	1BA167DE4919F496374704BEC778308CF2D43FDBA27C7B374DCFBC53F11C6BD04F47D2B4697C69505E9B545657C2613846A722685AC3A858969601BE1CE36CEBDFC6A8A14233AF1B8561332A780542112D5E7EFAFAAB7AEB46E3680B3F2B7674452F2B85AE63739AB2F61C921D12752859D6E5339E6BA4B7C4F8BB39AE854F9364BC0679181462AF9F242C662B6CECD1754944E7EBC14EBFBE7E7863F701188D67BFA363DA3B5DE9BD836527360259B0017AA0BDD34D7314C64F52235D64E0C8135DE5A52B560C67
	53AAFCDF6B819E166F9FEA841E2F22DFB0DFE3F83E0E7F52DB3ED27C64EB2AAADFCC4728D234FB7AE3B88FFD3B946D4AFA9C6DBAD72B58F54A0DF6D5BE0EF65D676E4D2EB4BFF6599E575AB53A846DF2BF0EF6A51620DDA15C2E60ED0ADD39E5E857E4EE5741325E6CCA77E3177004562E92835A1579845A95E0C03B4A1FC03B1E5A2E58D5D203F635BF01F6F55674E657ECBFF685F959B562B334AB185B3562E734AB065B3533CE316B83DEDE3338DD45653D59B5570FDDF911D1463FFB5835349B6DB2C522DD16
	EE346BF0A45A5531C3314B406D9A9A09F6FD5BEBBD4E73E3D7C424363CCEF23B2239DD915C2E195C2EBBF7AAF67D097B7D1AA7512E74533D5975299F3B1A1E545A7594777BC3CF22DD1F70769848751EB37456CF23DF533BF0CFFA7653B1903EDE360F49FB342AB7F9F3D57226EA644D7012B72D4B1F3CF441335DA5BD69795E1A9995E53CCB4E33FC16556B9EC7EF4478068C10EFFC3F79587AFF649E3246179345567F13B9BE1162EBD438FCAF0F2FB396C2DCD0945E9B39E297598FCD02F076D3D146FC20DAA0
	8BD040D3382F77E8F921504BB922963B23DEBCA55AD4DBF5ACFF9B778E6CBBB8CF2663FADE58A3378B6117FD106C6B5EC550D3EC4E93A6BF93DAD6D31C65EFD983495FF4089346C59957GD5GB581350F437C25FC134F72C7AE12D7C91279E8BB3D990A195BEF953B9518ECA633F13721054CB50F581DF4E3126DCDDBDAF0FF398764878145G450FE3F70D647BCDBAFB5A6F71BD6D24F1ECCFBBEBBC58BBDEBEAFC2FD3DF6EFEFF3E5B5D5GEF53F856F6C858823FC97B7F9B1FEB585D24AF5BD9EFBEA21283E0
	9E6764AE5DC27A93651429B63EF229427B3DE87708B3EBF83571B56B491C28566AA2F76379BD82F97E6E7A144BEDB0BB6A6DEC7B1D730E78FA1107EC4FB46C1E57BF8A54EB450E712C6C034663BD30CF0A7562C3F97C8B12DEFC44F86C2B9F5EADF0FB261B3E04E727581D4BAF2655DA41CD4807F2720A0BCCE52672CD2526327C5C6242F5685B415D6A6FFC154FA9537DF68221D8397E274B4F084BD74BFACBCB8C70A467D318CD2B1B941BBE14D6783629E8D3B1B306713D39CF613BEE5FDAAD5FF61804662DF2
	19BBDDAE51E9EDDB151FDB181FDBE4A2EB6FAB843686C357A9683F442D5E985A17F1C060ED7B75CCEC5B4BFEF713D07B628749B8CEE9733FEC023CD6BFC15907C8B8BEAB1D49FC20D5317D7D223E67F959C905CFA869CF7D496DDED376DB1D0A3C61273E70ADEF53A6D98E637B67A905AF7B403FDB76CC6697EE254C2BCEAB72D3CB7AE297CB7BE925F73F16476FAB05CCE07DDA84BCE3A730F7D846D730523BDF5B36121F5519407AB561E998EB1E166F9953FE6D1ED53D75EB6A3567FC602DFCDA6957DA1EE67D
	5A873CDF9BFF1074A1CD2279C433882FC256CE09F6A73CACDEBB38C8934F3B931EE6638F8BB00D84E39AB9E4AF8B115F53FE60BAA43BC395AD471FB9CB5E73D9BFC6E2375B1D9DE609457D36CB696398CED294F35E1B5DB49EFB461676AE274BE2BE4D63DFAD1A2F0EEF30F712D8F97DE805C75CF627D509FB74D016075CAE1108E5F1D5BFF968ECB6CBDDCE354E79F68F5EBA3B4D4AF98FD6ACD0479D90AD1DA2B318456F34F858545C253257F10C72AA630CDC1A48BB7873051AF839B6643E3E27C15BD753FB64
	CFE3DF5E31C5AFDE6271A935FA7101BC7E21EA3D78D1BCFEF415DE7C5827F12C301FD15A603CD4BFED302C845A2009778D13FA943E874C972EDFB67C2A7CDEFBDE51193E25AF3632348736103E1B1C9D0CFD1A14490CCA3246B28EFE33BE20208A7B006CE85687644333B21A2DFD31BE60066A3E2C83E29F508A3C36E8258F88005FDF896AF540C5BF10FAE9ECEB736A8664B172E00EF7BC77B2D6372F9A34F5CB6C9D924D5F5984253C8E6F68FB5DDE1722702DD9FB616A7677294A6F29E90A4EE74E753DEEBF7B
	956B16492147886538181CD57A36DB2CE61B7D184AF0B66EBF7B23FA5E13B5F225815FD134759D9C4D467BB1514A384F745FB5DB517F6EA65FFBACC6EAE8BC704F08C6BF1CA6219F66CCE4E3D8A1BCEBA7327BBCAC4DCAFEFF7850C4662B92BC8365B44CD72F286C4BF8057370E0608D1B2878EAB47CDE8E195E4173F17F927DFD1809F4EDF9B2240F1F484F7FBA090EEAF327255BEA115ABD4624945782F51CEAD0E3711D74FB8C6C5CEAE3A7F1FCF93C5CBDCF41D90B90F0AEAD14E85BGA9074CE7308DFC1229
	54474C347FBFF31822F90A4C21DB517FD8786E94623B6AAFB2B4137FEB6C742319122B20253189FD356593C54EAB15AB7C7355EF5B5EA2274D2BDA69BFED4D365DAD5B76F259F53F745D266E790ACD8F16765D260FCA7BE753556E3E5BB4B2CB31A922246FB62577532632323E5B7458270ACD75063E5BF42321FFB65557745D2678850ACDEF7C54F71BCAFF6E1FCDD965FD37A9A5DB3129303B6FB61D6D6E1FCDCB7BD1F7DFAED2EC8A6C07CDFF6E27CDEF764347A7AFD6EC1A568F9FDF5BE79F775D1F7D8FB97A
	60A77E33257E633F7B0C0458FA5A04BD4A6FE7D53FBFEC6A6D0E683FC37277DA77DCC89D727D41DD3E6F900C5825FF076056DD7AF788C66F72FF4F741EDD3E3F459936CB7DADCE361618DEC49ADBAE0CFD95C00701A42040E7F09D313A1E4C0748D71E3D579037D90E0968B47C8EC3036F3C66B7747EBD681286F2576608DD126C9DC4BE625FB6107A0297A98AB22C3201141153FA2CDDE2C9E5FCBBBD0BE2935BA84C560A2B7761B739C3A0EFF14F601954371713F15B7F3D74962BE4932B65258379DE5E157BF0
	2FA1FB921ED11DC757236BA1EC816A190467E1976F41F56AA040E2A661F7BBFE5E03FB9329B1A8E388CDDB816196A09BD05F6647582EC613D7EE3C7F5B66637EAF1DE1B19B72B762FE72209834EBDBB1E2899C1BC74F8A12BA287FA32E336F25678E86C1B8780F68AF9F6DDE8F4F7D6663720ABA7B0EA97598G1B1CF6076814CEB314FEE1D47643EBCCE47E9989F2620004BFC599F3007201EA01DA01ECC081C0430146820D831A8AB483E896D05633D146B54F229D8D87567931C3BEF4CC1D57E936527DF0D6D7
	CD1568A765A04B71AC66FFB85F238EFD966B4A12CB5246C3B889A8AB966B3BE3A556F79EE007E3D1466FD7E2FD8F0FC599EF50343190CEA058F37D2D6F63E49AA97B64038DD8C7B94F214E10AD08B5F1ECF3ADE2019CFB2F9A31C80E45D5A116749C4E558D745C097C378EC4753BCF6159E4C30A6063FB174F4BF7BA1FD756FCD9D96D2F4232F2C0DC6873287B963E3F9F7FBC16D58CCD1B73E7F847812A7BF3FF4B6A65B2252C9625E05E8E7F997DB910E4C76EC35C99EBFD798F74FCBB21B9B59EB9AE9E15DA4C
	47849C0F9348B5E0F90C3DD34A95FC2F4116409BC3F1D179BAD68606AE743961E0CB6C1F318CF6F6214FE5CC66F7FF3710341590EE85F2CCC61F5B31954BB190304849A8635A2DD80EC9DC4693B4ED6194A8C32043D37ADB0EF33F95F81E8BAA14BC67197A12E7E6D7D989163FFB8A365979C53E470E45C53D0F9DAB58B7D0214FEFB96D106F4E2FA9A26B377636B617484728F5C5D99C5B6142F3A572F7B976753637B7F60A5E77BC76774A7705A64C78FECB79E226746D0CDFBC24E3FB22175031E666946DDE69
	159497DE506E15AE64FB25199067F8814FE7AD5BB330BD5E2F8748F7345FFCACDF42DEFAFF7F85EDFEE7D3BC5EAB3F5771B1F78644B53D0879AD5A28B75F8BFBD13E4B7E22FEBD44811E2809CB1D8AE354D4BDDF58D92C27A3DFCEDBB9554739E5405BB5F1BD909E2A2BE301475D6390B9CD387C0C515141FC7F40A6721B4F9F5990AC477D3C094C852D1D509C4CB66B993A81598C89BB0F495F317073BD153BCD3D374F87CC7AF33BF013775C6EB1137E5C6EA913FAEE4730E89369A30F41185E4A537D11E387B9
	76BC15C93083E4AD0963D329DD842F9309C244DF25F6F5D32CD5465ED0E187E5AC41C47A9848F23B5D29FCAF061E97345AECAC7CBECDE36D04AE071747FFD39B2D8E334A6EFFF26CA02F6B7D15836B13AC3D726977C9B7D33EF89FFE2A2519D331CF1ACA67C399AFE95B686B944FFEC95BC60F67E29B4D0538C1AFE19BF52D9CD85FF249G799E69B79F4B5701D5447E842F7C9E2678CC2F7C3E43670542B4F8C71806798DEFD06B0F1EA61F0F101F09533C6B01715D3DC54D177AB2CB17A3BF73DF76453735D64D
	D7A927EB111F6DBE797E6E71BD0B9EB95DD0799962136FF50F6FAE044B69E264E73C8E9F1B0F3DDCC15A504C17F1FEF0CFA9F99775B527A75F8EE53D956ABD6477FB8F3E7D6178G796E68B79F73174AEA5AEE6234FE34036259F1DABFFA1E4F19F3A1EED09C7A51E79E5F43CC08E365BACBFEE63D622BFEB27CFE470210FE7F3238D7BE7D7E2511722D5131C70F4ADF417E65E1BA772DFFC5DBCED194EFFDC5DBCE3B36E0B9D9A0AE7A95AC27462D832B5F1F8648F7C33F79D83EB651F7517CD735792D20F8792B
	5A7C0E676FG3590977AAA3F9B6671CD224C57647DA779E9FA4D3B9E583747A29712B5GEB274D5AA956303F46A5601D03BB697AC79D709EFE8D6F8D8C23F7AEA488873E063AAF27E7F8646F7F28FEAF7D8F2C6D957F87F49877AAF91A2F7ABD7C022EABB29D77DFD09D723789687AD09054E7882F67F00A116F100C6558F5DF517BB6B7C79947F1EC77A20211EF0ECC65D8DEBB3F8749313729BC724D110AF844EC45283709E3FFACC63D07B876308B75BAB816DA02FA87F16C099C54BB1CE32E92541B4031F574
	1DFB9604B3DFC74CCC6FCF67C23810E393DAD0DE854742689A51908807F0EC468A0205C3B866F5EC932F66AAFE72366A37A377427B4C957F819DBF2DD27254267A7D371597DEF7462A8B5FA61E256BEC645BB3D3F9BD7F0B5E6F1D8161D99CBB329AFDA463AF7C5EBDDD0FEB05F0BB475E4A26778BA1BC04E33B96F1FF6558FE3AEE17G61A40E3DC27DBA730DA86332B778F7B87877DB72B8B6075E23F0C3B810E3010B89B68A42E3B8766EE2648D67D8D29572E6CCC76C11C5483B0CE31FF35E3C69B8E7FB2D02
	3C7738BC66E55B2B951FE8D47D1EDAF5617D637D8B2AC37EC68FDDDBA45F6DF17332A9275FD6988261D19C3B19FFEFA114E3ED347FC902F0561B3C5F7B0E5E6F03F0791B7CBE5E96255C22D53F77EC3970E5F87CBF20631BCAA5CF552A5F1336DEF85D53AF288EF9DC25E38F79F6D1AD2F67BBE8DBEE05F0BB478C96749169CD65DBEEE49DC3A23BD264AC0A0067032650B959B8C8974B530EE9CC433482BFC3CB7D05459F5F68F9F7F0366A2EDA83DDCFCDFD8BFE3F0577E92268B8DF8161BA0E1D25FD01846100
	3770BC5156767E1C6B575E019D8BF2481EE69A37E5F1A1ADAF40235F42F3BA792A7BEB7FCD6786B3A1CEF89B6D7A8B1D9B64C3381CE39BE839DBA06CF81B5FC14F694F59A72D2D41A0274A4356D16C7B9400073F0D75D05965F9FFF4BFED6753A1BE1127F137F856C5A33B87C7EDFC25252FF71674EC4CCDE07D06F216F0D2253E8F3D413EE385694B93502E14DCEF9FF2FE2F147DAC365F8A695D1CA7FB35E7DE0655A9F9B93D3A2F3EC174EA6FFD054ABA143C3C384533EC5F25E3EC98240B64767CAB5B531E70
	9D0ABDC79675753E361EBD09DEE55BD16BE94FEF6838BD8B52E53C433FDD2B39BF7E56CE451E033D5E9F1F6B471E72F71477C226EBE50D27BDE5F4AED08B691A38BD37AE567483750ABD29CB7AFA9FCD4F1E00F758371A143B79AB69DE639040073F435BF203222FF8F1EF7A7C65BF7A9D25BE183E1F69F8BC9970F81E5F463D1A3636DB519F6677CE123AEC89952A3EC5C06F7D7ED56B1F673A745B5AB05A87AC0374F9FF65FFCB6FBB1D3656A8F01EG5A379C02746D1C67910DAF8DEBD2723254521FFED8DB16
	437F2A7CEDF3391E1A851E2F40C6F2BE5967D95902A3F61BE5D5FEEE91BB9DA917F30B42FF79D7FE4E6C161781DBA9BFDFFCCEF52727E6D15F79DEBA2070ADEB6F6B59DE46BB0216A37B9D31C711334B5FFD980D7E3F6ED5786EF5793B4FD3D44C98995FBCD5B9FDE06ABBDF19AA3F8F7679ACB36395DB157C869ED4648CBB55F77DFF7BD1615B5752BF7D8BFED274F77E2248F9EDC57F7D6C9933427F7E2A7E59716E91450E96D111B3AD3BFFF23A5B94B92F36ABF21E6BE73DA49FD764345B94B9BD0B7BA22718
	4B392AC3117336DD65170B7AA7E751C9C54EC917A2A7FFEF7F72B5C9D2647C60D664C4FE577F7AB626AA7CB77633FC5751FB84CBA8EF55279396405C6DDDA1CD9252656B1CAE27B01FCB5AC1377062CFED5A1C72469DA986A1F507E0E4DB1ABB04D432AF39C3C8235B16BB0494496EF013CF4EF97C1384A33BAD84BF58B5A750A45FDB1204597C2611A4E460ADA9C849AEBD8106371800C93E308466AD70EE18D5573C746218DFEA338D0281ACEC9719C9E97494BDB5AA7B7C479FFF6CBAA97CCAC7D33273BB9AA4
	4D73A8327137153C95736AFED70AE05CA1ACA5B308E651C9B707C58B79A3957BC5A75135DD186DA0F7CD48B9FFA88597FD24392453B631DE48EE5BCB7E2A34CB92ADD5386D6C1204C575C453C6F242EEE31D5955AE11127E36DEC86DB4F708123000C55B4C1D47B632DD1DEF295549D507E9A91251CBGFB97D2FB091482AEE5612373572E5CB9BDBCC5B0EC9766586CE64BF673D1496E5CA14C165A1D222B9D7C476D58DAC985E570BF58148262BE6759A719ADA0578D3851A75CB78F1917B0EF3BC1905CCEE1A3
	CDC9865D821E42D253EEF602AA513983488CAA577038CB3E103E7C6377CB8E9904E4B9132468335C8E285CB6F30755C6AA1FDFAA2075128D086484087C19C8B0AED7E39CC6F19D8557CFAC9CF67707BFD09D8A2BA42CE025892C9B0F09760E0DD5EDED56CE118B002285714B39F81226C013E6017B5AC5BB27FCFC84DC21D6C869A8AC247F97517F0B497FC594A922C8D1B114EF225673BD7FE9635D18E76ABF3C4D918FA8588E15A8CDFBF7626DAFD7A44E005CED9752681FE804FA48D2D26D6E6AB8A2BAAF9BB3
	EC795C20782BC8B5B9083738095C9CA6DEBDF1A3AEEF70E8G031410CE63A021CF3CDC10208B3CFD09400B55B5CA0025B47567747FAF7ADDBE04AB17722E6259DA2FE48B33C60CCD75B0FDA58F1173205597E5C6CCCAC6C8CFEE10B3E470B0FD577F020CFC27E4843F5CC306368C1D0C7457F4E3AD9D3FAE0C5D5B943B49AEA25901B7718A38588A395E536D496BDEACB78B51ACB98B499CB548E831C7DAC48BF8F655A8D946CE71C25769267D4E8395E4F43D87BADFC6CB3CA4204F27FB74D408AAB64C1111EC0F
	3404B2E4B2FA20F3E4D234252B50E46E185300BE74C803728C1EE9A461934FBFA4A189594DDDC7DC928D64D6F739F24937B1B643A87E3DE8BF66B4BB5A49C73C904CEAB0BBF271839739BBEA81CAEF6C4CA5D774A4E1CE33A865520B2BG4F25B723F34955684DA48A66D398C82B2B5B5C5020BC97B63635D19139EA817365BFE511AB7FAD8B90422F92EE56756CE49F1E1D6A5986F12CB23213E04CBD894F05717713EEDE8344F78E68047A5D19D8AEE0E7522334CF5281124E446061C75E39F06804AE2ABCDD66
	76E82D1827340B5D6EEB943BF159CC69E586687EA464A47FC9974666E16E0B2F9CEFD24A11684DC25D971F2537FB4E254808C3BCF54E45E7695D1E7D843114EC979BD516B23BB27BE26905189ED54A61315E73D3628173AEBECB9F721C0092CB491541340B2F769F7734F40EDC3697F54FB47EA26C19A6F97AA6D94812B7C058BD2DF567557734128667787CB6571A0F58D940720F14EFD43033386B695E348478500F70EC4759A59B845FF7241ABB4CB61BE4361D286A34107BD25527A5D13927DA18C3643BB00D
	9C49624AF2085D47D5BC7F8FD0CB87887E8C2692EAACGGE019GGD0CB818294G94G88G88G82F954AC7E8C2692EAACGGE019GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG24ACGGGG
**end of data**/
}
}
