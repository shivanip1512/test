package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.device.lm.LMControlArea;

public class LMControlAreaBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener 
{
	private javax.swing.JLabel ivjJLabelControlInterval = null;
	private javax.swing.JLabel ivjJLabelMinRespTime = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JComboBox ivjJComboBoxOperationalState = null;
	private javax.swing.JLabel ivjJLabelOperationalState = null;
	private javax.swing.JPanel ivjJPanelOptional = null;
	private javax.swing.JLabel ivjJLabelTime1 = null;
	private javax.swing.JLabel ivjJLabelTime2 = null;
	private javax.swing.JLabel ivjJLabelStartTime = null;
	private javax.swing.JLabel ivjJLabelStopTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldTimeEntryStart = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldTimeEntryStop = null;
	private javax.swing.JComboBox ivjJComboBoxControlInterval = null;
	private javax.swing.JComboBox ivjJComboBoxMinRespTime = null;
	private javax.swing.JCheckBox ivjJCheckBoxReqAllTrigActive = null;

	private static final java.util.Hashtable STRING_MAP = new java.util.Hashtable(6);
	
	static
	{	
		STRING_MAP.put( com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_NONE, "No Change");
		STRING_MAP.put( com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_ENABLED, "Enable / ReEnable");
		STRING_MAP.put( com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_DISABLED, "Disable / Waived");

		STRING_MAP.put( "No Change", com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_NONE );
		STRING_MAP.put( "Enable / ReEnable", com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_ENABLED );
		STRING_MAP.put( "Disable / Waived", com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_DISABLED );
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMControlAreaBasePanel() {
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
	if (e.getSource() == getJComboBoxOperationalState()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxControlInterval()) 
		connEtoC5(e);
	if (e.getSource() == getJComboBoxMinRespTime()) 
		connEtoC6(e);
	if (e.getSource() == getJCheckBoxReqAllTrigActive()) 
		connEtoC7(e);
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
	if (e.getSource() == getJTextFieldName()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldTimeEntryStart()) 
		connEtoC3(e);
	if (e.getSource() == getJTextFieldTimeEntryStop()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMControlAreaBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JComboBoxOperationalState.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMControlAreaBasePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextField2.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMControlAreaBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (JComboBoxControlInterval.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaBasePanel.fireInputUpdate()V)
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
 * connEtoC6:  (JComboBoxMinRespTime.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaBasePanel.fireInputUpdate()V)
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
 * connEtoC7:  (JCheckBoxReqAllTrigActive.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaBasePanel.fireInputUpdate()V)
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
 * Return the JCheckBoxReqAllTrigActive property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxReqAllTrigActive() {
	if (ivjJCheckBoxReqAllTrigActive == null) {
		try {
			ivjJCheckBoxReqAllTrigActive = new javax.swing.JCheckBox();
			ivjJCheckBoxReqAllTrigActive.setName("JCheckBoxReqAllTrigActive");
			ivjJCheckBoxReqAllTrigActive.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxReqAllTrigActive.setText("Require All Triggers Active");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxReqAllTrigActive;
}
/**
 * Return the JComboBoxControlInterval property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxControlInterval() {
	if (ivjJComboBoxControlInterval == null) {
		try {
			ivjJComboBoxControlInterval = new javax.swing.JComboBox();
			ivjJComboBoxControlInterval.setName("JComboBoxControlInterval");
			ivjJComboBoxControlInterval.setToolTipText("Default operational state");
			// user code begin {1}

			ivjJComboBoxControlInterval.addItem("(On New Data Only)");
			ivjJComboBoxControlInterval.addItem("1 minute");
			ivjJComboBoxControlInterval.addItem("2 minute");
			ivjJComboBoxControlInterval.addItem("3 minute");
			ivjJComboBoxControlInterval.addItem("4 minute");
			ivjJComboBoxControlInterval.addItem("5 minute");
			ivjJComboBoxControlInterval.addItem("10 minute");
			ivjJComboBoxControlInterval.addItem("15 minute");
			ivjJComboBoxControlInterval.addItem("30 minute");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxControlInterval;
}
/**
 * Return the JComboBoxMinRespTime property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxMinRespTime() {
	if (ivjJComboBoxMinRespTime == null) {
		try {
			ivjJComboBoxMinRespTime = new javax.swing.JComboBox();
			ivjJComboBoxMinRespTime.setName("JComboBoxMinRespTime");
			ivjJComboBoxMinRespTime.setToolTipText("Default operational state");
			// user code begin {1}

			ivjJComboBoxMinRespTime.addItem("(On New Data Only)");
			ivjJComboBoxMinRespTime.addItem("1 minute");
			ivjJComboBoxMinRespTime.addItem("2 minute");
			ivjJComboBoxMinRespTime.addItem("3 minute");
			ivjJComboBoxMinRespTime.addItem("4 minute");
			ivjJComboBoxMinRespTime.addItem("5 minute");
			ivjJComboBoxMinRespTime.addItem("10 minute");
			ivjJComboBoxMinRespTime.addItem("15 minute");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxMinRespTime;
}
/**
 * Return the JComboBoxOperationalState property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOperationalState() {
	if (ivjJComboBoxOperationalState == null) {
		try {
			ivjJComboBoxOperationalState = new javax.swing.JComboBox();
			ivjJComboBoxOperationalState.setName("JComboBoxOperationalState");
			ivjJComboBoxOperationalState.setToolTipText("Default operational state");
			// user code begin {1}

			ivjJComboBoxOperationalState.addItem( STRING_MAP.get(com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_NONE) );
			ivjJComboBoxOperationalState.addItem( STRING_MAP.get(com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_ENABLED) );
			ivjJComboBoxOperationalState.addItem( STRING_MAP.get(com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_DISABLED) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOperationalState;
}
/**
 * Return the JLabelControlInterval property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlInterval() {
	if (ivjJLabelControlInterval == null) {
		try {
			ivjJLabelControlInterval = new javax.swing.JLabel();
			ivjJLabelControlInterval.setName("JLabelControlInterval");
			ivjJLabelControlInterval.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelControlInterval.setText("Control Interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControlInterval;
}
/**
 * Return the JLabelMinRespTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinRespTime() {
	if (ivjJLabelMinRespTime == null) {
		try {
			ivjJLabelMinRespTime = new javax.swing.JLabel();
			ivjJLabelMinRespTime.setName("JLabelMinRespTime");
			ivjJLabelMinRespTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMinRespTime.setText("Min Response Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinRespTime;
}
/**
 * Return the JLabelName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}
/**
 * Return the JLabelOperationalState property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOperationalState() {
	if (ivjJLabelOperationalState == null) {
		try {
			ivjJLabelOperationalState = new javax.swing.JLabel();
			ivjJLabelOperationalState.setName("JLabelOperationalState");
			ivjJLabelOperationalState.setToolTipText("Default Operational State");
			ivjJLabelOperationalState.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelOperationalState.setText("Daily Default State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOperationalState;
}
/**
 * Return the JLabelDailyStartTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartTime() {
	if (ivjJLabelStartTime == null) {
		try {
			ivjJLabelStartTime = new javax.swing.JLabel();
			ivjJLabelStartTime.setName("JLabelStartTime");
			ivjJLabelStartTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStartTime.setText("Start Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartTime;
}
/**
 * Return the JLabelDailyStopTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStopTime() {
	if (ivjJLabelStopTime == null) {
		try {
			ivjJLabelStopTime = new javax.swing.JLabel();
			ivjJLabelStopTime.setName("JLabelStopTime");
			ivjJLabelStopTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStopTime.setText("Stop Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopTime;
}
/**
 * Return the JLabelTime1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTime1() {
	if (ivjJLabelTime1 == null) {
		try {
			ivjJLabelTime1 = new javax.swing.JLabel();
			ivjJLabelTime1.setName("JLabelTime1");
			ivjJLabelTime1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelTime1.setText("(HH:mm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTime1;
}
/**
 * Return the JLabelTime2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTime2() {
	if (ivjJLabelTime2 == null) {
		try {
			ivjJLabelTime2 = new javax.swing.JLabel();
			ivjJLabelTime2.setName("JLabelTime2");
			ivjJLabelTime2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelTime2.setText("(HH:mm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTime2;
}
/**
 * Return the JPanelOptional property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOptional() {
	if (ivjJPanelOptional == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Optional Control Window");
			ivjJPanelOptional = new javax.swing.JPanel();
			ivjJPanelOptional.setName("JPanelOptional");
			ivjJPanelOptional.setBorder(ivjLocalBorder);
			ivjJPanelOptional.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelStartTime = new java.awt.GridBagConstraints();
			constraintsJLabelStartTime.gridx = 1; constraintsJLabelStartTime.gridy = 1;
			constraintsJLabelStartTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStartTime.ipadx = 6;
			constraintsJLabelStartTime.ipady = 2;
			constraintsJLabelStartTime.insets = new java.awt.Insets(26, 17, 9, 0);
			getJPanelOptional().add(getJLabelStartTime(), constraintsJLabelStartTime);

			java.awt.GridBagConstraints constraintsJLabelStopTime = new java.awt.GridBagConstraints();
			constraintsJLabelStopTime.gridx = 1; constraintsJLabelStopTime.gridy = 2;
			constraintsJLabelStopTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStopTime.ipadx = 7;
			constraintsJLabelStopTime.insets = new java.awt.Insets(10, 17, 34, 0);
			getJPanelOptional().add(getJLabelStopTime(), constraintsJLabelStopTime);

			java.awt.GridBagConstraints constraintsJLabelTime1 = new java.awt.GridBagConstraints();
			constraintsJLabelTime1.gridx = 3; constraintsJLabelTime1.gridy = 1;
			constraintsJLabelTime1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTime1.ipadx = 6;
			constraintsJLabelTime1.ipady = -2;
			constraintsJLabelTime1.insets = new java.awt.Insets(29, 5, 13, 53);
			getJPanelOptional().add(getJLabelTime1(), constraintsJLabelTime1);

			java.awt.GridBagConstraints constraintsJLabelTime2 = new java.awt.GridBagConstraints();
			constraintsJLabelTime2.gridx = 3; constraintsJLabelTime2.gridy = 2;
			constraintsJLabelTime2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTime2.ipadx = 6;
			constraintsJLabelTime2.ipady = -2;
			constraintsJLabelTime2.insets = new java.awt.Insets(12, 5, 37, 53);
			getJPanelOptional().add(getJLabelTime2(), constraintsJLabelTime2);

			java.awt.GridBagConstraints constraintsJTextFieldTimeEntryStop = new java.awt.GridBagConstraints();
			constraintsJTextFieldTimeEntryStop.gridx = 2; constraintsJTextFieldTimeEntryStop.gridy = 2;
			constraintsJTextFieldTimeEntryStop.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTimeEntryStop.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTimeEntryStop.weightx = 1.0;
			constraintsJTextFieldTimeEntryStop.ipadx = 131;
			constraintsJTextFieldTimeEntryStop.insets = new java.awt.Insets(9, 0, 34, 4);
			getJPanelOptional().add(getJTextFieldTimeEntryStop(), constraintsJTextFieldTimeEntryStop);

			java.awt.GridBagConstraints constraintsJTextFieldTimeEntryStart = new java.awt.GridBagConstraints();
			constraintsJTextFieldTimeEntryStart.gridx = 2; constraintsJTextFieldTimeEntryStart.gridy = 1;
			constraintsJTextFieldTimeEntryStart.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTimeEntryStart.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTimeEntryStart.weightx = 1.0;
			constraintsJTextFieldTimeEntryStart.ipadx = 131;
			constraintsJTextFieldTimeEntryStart.insets = new java.awt.Insets(26, 0, 10, 4);
			getJPanelOptional().add(getJTextFieldTimeEntryStart(), constraintsJTextFieldTimeEntryStart);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOptional;
}
/**
 * Return the JTextFieldName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setToolTipText("Name of control area");
			ivjJTextFieldName.setText("");
			// user code begin {1}
			
			ivjJTextFieldName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
}
/**
 * Return the JTextFieldTimeEntryStart property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldTimeEntryStart() {
	if (ivjJTextFieldTimeEntryStart == null) {
		try {
			ivjJTextFieldTimeEntryStart = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldTimeEntryStart.setName("JTextFieldTimeEntryStart");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTimeEntryStart;
}
/**
 * Return the JTextFieldTimeEntryStop property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldTimeEntryStop() {
	if (ivjJTextFieldTimeEntryStop == null) {
		try {
			ivjJTextFieldTimeEntryStop = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldTimeEntryStop.setName("JTextFieldTimeEntryStop");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTimeEntryStop;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return null;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	//first panel for any LMControlArea, so create a new instance
	LMControlArea controlArea = null;
	
	if( o == null )
		controlArea = (LMControlArea)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( com.cannontech.database.data.pao.PAOGroups.LM_CONTROL_AREA );
	else
		controlArea = (LMControlArea)o;


	controlArea.setName( getJTextFieldName().getText() );
	controlArea.getControlArea().setControlInterval( 
			com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue(getJComboBoxControlInterval()) );

	controlArea.getControlArea().setMinResponseTime( 
			com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue(getJComboBoxMinRespTime()) );

	controlArea.getControlArea().setDefOperationalState(
			STRING_MAP.get(getJComboBoxOperationalState().getSelectedItem().toString()).toString() );

	int totalTime = 0;
	
	//do the optional defaults here
	if( getJTextFieldTimeEntryStart().getText() != null && getJTextFieldTimeEntryStart().getText().length() > 0 )
	{
		controlArea.getControlArea().setDefDailyStartTime( new Integer( 
			com.cannontech.common.util.CtiUtilities.decodeStringToSeconds(getJTextFieldTimeEntryStart().getTimeText()) ) );
	}
	else
		controlArea.getControlArea().setDefDailyStartTime( new Integer(com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED) );

	if( getJTextFieldTimeEntryStop().getText() != null && getJTextFieldTimeEntryStop().getText().length() > 0 )
	{
		controlArea.getControlArea().setDefDailyStopTime( new Integer( 
			com.cannontech.common.util.CtiUtilities.decodeStringToSeconds(getJTextFieldTimeEntryStop().getTimeText()) ) );
	}
	else
		controlArea.getControlArea().setDefDailyStopTime( new Integer(com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED) );

	controlArea.getControlArea().setRequireAllTriggersActiveFlag( 
		( getJCheckBoxReqAllTrigActive().isSelected()
		  ? new Character('T')
		  : new Character('F')) );
	
	return controlArea;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldName().addCaretListener(this);
	getJComboBoxOperationalState().addActionListener(this);
	getJTextFieldTimeEntryStart().addCaretListener(this);
	getJTextFieldTimeEntryStop().addCaretListener(this);
	getJComboBoxControlInterval().addActionListener(this);
	getJComboBoxMinRespTime().addActionListener(this);
	getJCheckBoxReqAllTrigActive().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMControlAreaBasePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(375, 358);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipadx = 5;
		constraintsJLabelName.ipady = -2;
		constraintsJLabelName.insets = new java.awt.Insets(15, 12, 7, 5);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.gridwidth = 2;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 220;
		constraintsJTextFieldName.insets = new java.awt.Insets(13, 5, 6, 84);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJLabelControlInterval = new java.awt.GridBagConstraints();
		constraintsJLabelControlInterval.gridx = 1; constraintsJLabelControlInterval.gridy = 2;
		constraintsJLabelControlInterval.gridwidth = 2;
		constraintsJLabelControlInterval.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelControlInterval.ipadx = 29;
		constraintsJLabelControlInterval.ipady = -2;
		constraintsJLabelControlInterval.insets = new java.awt.Insets(9, 12, 5, 4);
		add(getJLabelControlInterval(), constraintsJLabelControlInterval);

		java.awt.GridBagConstraints constraintsJLabelMinRespTime = new java.awt.GridBagConstraints();
		constraintsJLabelMinRespTime.gridx = 1; constraintsJLabelMinRespTime.gridy = 3;
		constraintsJLabelMinRespTime.gridwidth = 2;
		constraintsJLabelMinRespTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinRespTime.ipadx = 2;
		constraintsJLabelMinRespTime.ipady = -2;
		constraintsJLabelMinRespTime.insets = new java.awt.Insets(5, 12, 9, 2);
		add(getJLabelMinRespTime(), constraintsJLabelMinRespTime);

		java.awt.GridBagConstraints constraintsJLabelOperationalState = new java.awt.GridBagConstraints();
		constraintsJLabelOperationalState.gridx = 1; constraintsJLabelOperationalState.gridy = 4;
		constraintsJLabelOperationalState.gridwidth = 2;
		constraintsJLabelOperationalState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelOperationalState.ipadx = 10;
		constraintsJLabelOperationalState.ipady = -2;
		constraintsJLabelOperationalState.insets = new java.awt.Insets(9, 12, 7, 8);
		add(getJLabelOperationalState(), constraintsJLabelOperationalState);

		java.awt.GridBagConstraints constraintsJComboBoxOperationalState = new java.awt.GridBagConstraints();
		constraintsJComboBoxOperationalState.gridx = 3; constraintsJComboBoxOperationalState.gridy = 4;
		constraintsJComboBoxOperationalState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxOperationalState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxOperationalState.weightx = 1.0;
		constraintsJComboBoxOperationalState.ipadx = 57;
		constraintsJComboBoxOperationalState.insets = new java.awt.Insets(7, 4, 3, 43);
		add(getJComboBoxOperationalState(), constraintsJComboBoxOperationalState);

		java.awt.GridBagConstraints constraintsJPanelOptional = new java.awt.GridBagConstraints();
		constraintsJPanelOptional.gridx = 1; constraintsJPanelOptional.gridy = 6;
		constraintsJPanelOptional.gridwidth = 3;
		constraintsJPanelOptional.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelOptional.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelOptional.weightx = 1.0;
		constraintsJPanelOptional.weighty = 1.0;
		constraintsJPanelOptional.ipadx = -10;
		constraintsJPanelOptional.ipady = -26;
		constraintsJPanelOptional.insets = new java.awt.Insets(11, 10, 58, 19);
		add(getJPanelOptional(), constraintsJPanelOptional);

		java.awt.GridBagConstraints constraintsJComboBoxControlInterval = new java.awt.GridBagConstraints();
		constraintsJComboBoxControlInterval.gridx = 3; constraintsJComboBoxControlInterval.gridy = 2;
		constraintsJComboBoxControlInterval.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxControlInterval.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxControlInterval.weightx = 1.0;
		constraintsJComboBoxControlInterval.ipadx = 57;
		constraintsJComboBoxControlInterval.insets = new java.awt.Insets(6, 3, 2, 44);
		add(getJComboBoxControlInterval(), constraintsJComboBoxControlInterval);

		java.awt.GridBagConstraints constraintsJComboBoxMinRespTime = new java.awt.GridBagConstraints();
		constraintsJComboBoxMinRespTime.gridx = 3; constraintsJComboBoxMinRespTime.gridy = 3;
		constraintsJComboBoxMinRespTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxMinRespTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxMinRespTime.weightx = 1.0;
		constraintsJComboBoxMinRespTime.ipadx = 57;
		constraintsJComboBoxMinRespTime.insets = new java.awt.Insets(2, 3, 6, 44);
		add(getJComboBoxMinRespTime(), constraintsJComboBoxMinRespTime);

		java.awt.GridBagConstraints constraintsJCheckBoxReqAllTrigActive = new java.awt.GridBagConstraints();
		constraintsJCheckBoxReqAllTrigActive.gridx = 1; constraintsJCheckBoxReqAllTrigActive.gridy = 5;
		constraintsJCheckBoxReqAllTrigActive.gridwidth = 3;
		constraintsJCheckBoxReqAllTrigActive.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxReqAllTrigActive.ipadx = 123;
		constraintsJCheckBoxReqAllTrigActive.ipady = -5;
		constraintsJCheckBoxReqAllTrigActive.insets = new java.awt.Insets(3, 13, 11, 46);
		add(getJCheckBoxReqAllTrigActive(), constraintsJCheckBoxReqAllTrigActive);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	//used to make sure the starting time is less than the ending time
	int totalStartTime = com.cannontech.common.util.CtiUtilities.decodeStringToSeconds( getJTextFieldTimeEntryStart().getTimeText() );

	if( totalStartTime > 
		com.cannontech.common.util.CtiUtilities.decodeStringToSeconds( getJTextFieldTimeEntryStop().getTimeText() ) )
	{
		setErrorString("The Start Time must be before the Stop Time");
		return false;
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
		LMControlAreaBasePanel aLMControlAreaBasePanel;
		aLMControlAreaBasePanel = new LMControlAreaBasePanel();
		frame.setContentPane(aLMControlAreaBasePanel);
		frame.setSize(aLMControlAreaBasePanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o != null )
	{
		LMControlArea controlArea = (LMControlArea)o;
		
		
		getJTextFieldName().setText( controlArea.getPAOName() );

		if( controlArea.getControlArea().getControlInterval().intValue() == 0 )
			getJComboBoxControlInterval().setSelectedIndex(0);
		else
			com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
					getJComboBoxControlInterval(), controlArea.getControlArea().getControlInterval().intValue() );

		if( controlArea.getControlArea().getMinResponseTime().intValue() == 0 )
			getJComboBoxMinRespTime().setSelectedIndex(0);
		else
			com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
					getJComboBoxMinRespTime(), controlArea.getControlArea().getMinResponseTime().intValue() );
		
		getJComboBoxOperationalState().setSelectedItem( 
			STRING_MAP.get(controlArea.getControlArea().getDefOperationalState()) );

		//do the optional defaults here
		if( controlArea.getControlArea().getDefDailyStartTime().intValue() != com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED )
			getJTextFieldTimeEntryStart().setText( 
				com.cannontech.common.util.CtiUtilities.decodeSecondsToTime(controlArea.getControlArea().getDefDailyStartTime().intValue()) );

		if( controlArea.getControlArea().getDefDailyStopTime().intValue() != com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED )
			getJTextFieldTimeEntryStop().setText( 
				com.cannontech.common.util.CtiUtilities.decodeSecondsToTime(controlArea.getControlArea().getDefDailyStopTime().intValue()) );

		getJCheckBoxReqAllTrigActive().setSelected(
			Character.toUpperCase(controlArea.getControlArea().getRequireAllTriggersActiveFlag().charValue()) == 'T' );
	}
	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEEF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D45715A6D12389B198B5B156C4E2B421DFEDE3DB5A5A2D599ABE5B1A5434B6BF0DEDC9575D3A0D5DB58DDFEBDB5B1866B140G91A38AAAD1509150C0CA918D0DC82861DF9108D491C956AA83730051473CF1668143CFF54F39FF6F4D7006D17AE5B7FE5F713DFB6EBDBF775CFB4FBD673EBBC8997BA29263AC99129417A831FFC763A4297934A48D6564FE1663A64851D64970EFBF40B6697124
	84F806C35D46AFC7DB53247636E4A8AF00F2ECE434F5973C27CB33540AEAF889A2A7936A9EFD34E35E58F2FA77F0B9C9243C2BAA951EEF82C4828E1F9BE414FF28AA1361D7B2BC07B80BA42D6B42B6272A32982E944A4DG8BG16F451762B6019C2A4CFAAABE56DAECC0F10B6BC7536E58B72B17064D05CCDE9DB5853A6BD9637299BE5F9091EC893866565G5879F869474DA760D95EFC257BF8275ADB54E9F739D417A6F776948DB8076C9EC711C36EF7F64AC5CAEFA760BD2AE277487632324626EADA2C04E2
	155DABB75BDD32A279FAD056B72A1289EFC92A00F20293F1B57DA8FFA9141F83D8AF701B8790FF925E5B81FCCE5E673F3EDD4E7A54B67C18F46185D77C56A0FD8EF7523EAEF16AFD1E59770724374878FCBE8B47E18354E5DE0E369682B483388142817610F99135679DF8D6F6E855EAEF2F6A2AF43B2B940727C9F5488A3C1715C18D431D11BBD50FC31268588FD6951A68B396D03A5BEF1A0E5AA472DDBE8F961DF9C40A79F203A54944A6B1114516484B54964BAE6BB621321785153D66327FBC7517FDDEB24A
	EE1C2C46A71A480EFA3E40320171C93E224BAE3D02E33D044C01FCF80FE37303622389FE440027F3769441E3B98C6AE22F70395176A65725B1E10E14F63851548E3323ABADAD8A55C1D182F5391B4C3B598A1539D568B20F60179970D417ED840F65F5D067680D36527975553D380657C0F993C0068B6698C0A3C08F40080B4F3157769C3A41B956EC77E8D5FDCE45A1FBA4CA7B552C1D7014FD1A47DE6F72EAF6D7275CEA77B86D9D0A6C15D81BEFE6218DBA7C681613395F89B2CE48AE59E3571C2A8B385BFBE5
	CD76F8E91B273336E19BD845D85B626C15C1C02FDB62FC1F4FCAC77B593D5AB937432E49FA8BDA7FE2960EC93F3D95B88283F827F67959BE3457DC68FF9440F2E10736EC6457A0FB40DD009BD155ABFD6E966C091421221DDFA03EE3B5340FF5F19B7F1B8DF1EDD06FD6B91F3B33379B7854B8BBD1FD3BE7102F31FF6DC7BFBDD165F36617EDFC4E54ECD9A895BEFF4AF44E90BF2E323523862E1D9504A76AC54B4D7DEDA1566DC455FCCD5D382FF0061491BFCE7EEF0A31EA4A4131DA873AA638232DF9EEEE6327
	32EDF44E22036733743487A6186C50ADCE751CB510456C097AA4B27D51761B8136087EACA46D4AC10683A08CA08AA086A061AA2C1D2BE2DF9DF00760B75EBE073679FBC4F6B039DBBFA15954664B76A2FF39DFF6E9B532DBF6B960454B6A3E339775E0FE20CB95B4CF933C9B7CC6B5ACD42F40BF3B975736DD53BC4E0EBECDBEABEB1A53552D577F04487276F538F51743E57D47DE6C3BBAGFEC334FFE9AF2EB90883DCB299FCC1426AFFC1563C2C483D20F4355A6752A87E15FD38EE2E48034C677CFA9F86D67D
	F6258F7D970DC3965B513EEE0F339726943AC0E949D574873F94F194C28B60B01E8A03E744D53E3687F2795A4E9BFAC8DAF6690045ECEF1A3D3046A2F968FA4C7068EB1B6A3456E62E7BFF915F43705EF8022B99C6FF9CA484E2EBEAF2AE72AB81B9A7815ABDFCCDF56D47FA6E3758E22AD695051AD4587AECCE9A0E0DFC35CF86F70F8E32C1586F631C6D81F52D4C16343EA387ED2998FCEB2A97ED99B34063CD9F68938E3058C36D391E5903361B67636D5A206C8688771A356B9C646DD6C1F99DC032E6566E60
	90EFD783759734E06DFEAC5A0DC07DCC00A853F68F8E73F69B20BE35AFD83B55F13CDDA1342981F074793723F3AF699246C4E6B1F0F71F3328CFF3628EE357E051397BB4B24AB44EED2BCEE6BC967471F8F6E29D0EFB8C14B7G247673B97A03ADBC1E1DF9A5C2DA73706E20B1FCDEBF556DE43FBEC7ED3771F8F6D59E4A1C82F5337B791CFAB1473827D6FA3DEA2713EC67ECBE04659A67B045CD4FFD831EDDCE0FD72B87A76363F3673EDC549777FB06E671CF22AF1575F59148DD8B50B6007877F3B38C3AE78D
	50FD3B861EF42DA8BE0CFBBAA1EB500A487FB40C483FEC6F377B0A3C8360100A0837AB2A465ADAFCADE063323CE0BB55D1A616E714852DBCA61D1FDE41642E7C429C297CFA3A45ACC71832B55B92B1C0ED39E2C0376948172C716BAF3E1AC47B37C274AF4387711B0F77EFFF1531FF93FD34FF0BFC3CFF07860F200FA50B3708BE1AE5CF176A69159D2413C576810D753012D433AEB23B068DF2394B0067C6A17773470DF20B87E19D824C9C64F2FFBDB89E3BD2192707384CC86037E6104B4CCFB21319B98431AD
	210B9F66F40500F38CF13A5C1DE6B61ABB44F53DBC7C4F5968BBC26EE26039CE481DFD54CC2EED186B9BE761F4A500D306B95D39FEA35D02E1CA37F2186B5BE3992F3EB4FE78FBF5E00E35E6D80F83B78C0F0E89E96ED1F88275CD9E2139478E017F00604B8DF85A3FBB082F41329B6A327E416365CB87F14FDC816535G1BGB22EC5DBCBGDA2EF11F31AF6FDDA9C4BED28FBE506332ABE77D82841E9764212DE0CFE7659F65211D491EAEF1BBEC76855A41FD4D3F6FE181E5CA7756A83A22007262GBA6AAF8E
	641358927D38E19914151DEA0118294EA9AB8E6A37BD449F222C955738FF7EC71D51BFAF5DFA4432594417A0C857198F392E7B12C2F15E10C2F4E8073A910019G0B811E8308854818406D5FF9602F216CCFA6D50BDA8D38133017D78746DD42DE5F9B271DE3C25A39E402191D3B15BD66F6063EF428D52A0F1D8335D5717D31F1820F6B6F999C1F2E91236882F51DE82A6BBB590725D0FB3BBEBFB03B2D05208E52C896FFA66CB6461FF3A7D0DF318C1E4F8150FE2EBD4E4F3B560BFE1E9B9ADFBFBBC7518576B3
	6F96FFBA2A4325A42EC379ADDC071461716930EB94DD208E1A298E9F6F64639DA6F4B89BB7BE9D6C236882F5D8EE2A43770FF29DE2048EC916716930E394DD208E99371A4D39632D4156C70F5CF9C5DF9FE17D5C8F95829FFA7E2111185281E51F407D2D96F1F3219CF5AB6F5311937970DC8D65D800545B222D45G4DG9A40DC00A5GB1G1BEE63B43504A6EFA20D7DEB6069G18B8114678A43EE071B4464A14A63CAE821E48BF508F1A8117AF568B53E35398743C8FB742BFA600CFB1ABD3DD3FC1F4CD3A81
	BE88016788DB898F3A1F862361E782B742BFBD201C421E547F779C8C1E672B874D737C3E0366F97E7041ED948FBC0C27073C7E3603E31F835CF9506C9CA01B7477491DDB89EF2FD776128D8993A1D6771206369AF59E70213DD7E636CA172AFCA92CCD2D26D69F96E3B5100BEBEB41C47F71DC424AABA70E1E8FF4FF8DAF4058ED8D54EF82481894EDAD84E81C4477557DEF070CEBEAFDE344B569794147E5F73E79385872F19EF955BECF279CE87B3773B3C51D3E0357EBF2AF1A0936A9A6741A5D53AD0F9A3B23
	041E5685232F488FB63EF48C92870DE3F0C40C415F4937GE512795A1BE002277C1E7463772E447D5E2F8BD0D7933FE748194BF7F31FB7F3924F19EFA57948CAA82F8558B4097B1EDF9C9F6BBBC94664E07A058D9975AB917DF5A40D45AFBFA83F0AE1A33F3F88FE17F612F38393BA628B03723BA34E486FBD416F3923484F91041FAFA83F6396A33F23025F74FEE3FF4398FD84FBC68565F7E7B51E4FA976C135CF039870F69C237955BC8EDA81F4EBG12811FF5BB0D076878FFF1GFDEA17475E8B43FE56ADF7
	CAE9D310362716530EC07BF00045G2B1076F61EF3DDA9417308D30D768ED9218E04620F94A01D9E21996B425F5CA1E858D99C49D9A08B91FDB9FDF51BE85364F41D113DEE8C72C43DDC3FD3541FF6330FA4609BB4C826058E5751770868F6F4B3566E16FA22AB196D275D34852F2B6F4AB47288D01736F921A543584620AE2DEFAD22FA30A8620CFC35D2D1DABC4EEECCC27BD18F1A5FDEBFE14CEF5B2650B19A1942735F8D974D6A43C57D3CEC337A85227E40F1337AA5D3F83CB20D64D7AB213CD660FEC3F031
	D01626F25C47F9BC2EC91D4A7D776C02607EF7DE01397FDDD8E03EAFFE2EE06CFD714B85417CA60B452AF8BFAE881D1F2F62BA77CC65FE2AF237F11DAD817C4A297A5ADAB3F574BA23F97D9F085F5BB0156675DB84BE051CF7E45E2163E93EBF053447F2B954C55E41F512BE60BAE55C49F143C99C679038FE921FC7C1F925403D381337DBADF07FCDE2687C30E8EBC598471DA9656D9A85AE12442FF3213CC8609EB90738E5D0DEA5F01BDBB9EDCC98BF1BF92A1A5FC7A83C8BF43C0B7B10436DFEB9BC1E341415
	1D70B89DD5766EC6621168BE323DA619514F3D0B6FA395B54873B9A82FC75CB41E5B7B8E725CFE51A3F3A097EDB0BDFBDD53F24412B50D0ED545B4BD370F045C1E4A1CD64BE50EC0FD54B444AD2DB52E85DC27CFDBE81EC06BFF69D70F73ADD7B2565B6A0C75286FABFE74E77C6A51C68798BD6D5BF78F703EED7AEE2424FC65D850F365CD3CCFF753E7F3EB2E14F7472BC9EC6F6E8A32F7934FC87B7F5593FC6FEE3B1B673EFF3A44F161827743ECF13EA2F097C80ECCF48E67BEFFE1AE6E971A2AAAADCEB7FAF8
	29B49C6F5C64E218487CE6EA9EFEBB5220125655E178A2AD8EC71ED773EACC7AE934D53C61B982B461G398C37G5E3F9EC7EDC5F9DFFBD7673D3EEAAC5E7C1BE0BC030D813CD327C7DB3DEC9C689AE8A3F1CFA6608B2773F5F11064FA4DD0F6885C429A3ED626CC677EC59A4631D98D658DGE977C4DB7381AAG9C7770B6DBC91BA9D00E84D881306E9E7A2D853F4B50B653065A6109B43C9F950677B4764E65846B7BD810AF7479AC11E54C2FB687580F2F179B4D2782A1C50C552E8FEEE49EF0C81E81F14B8C
	FD2C0A61BD094D834A6F1E24D07C0CBC4B8378B59B7851B9FC57CE73F97E6D92FD3EED5899EA3E99E5DE1861BF5F5CB37479C6E5DACA4DE5463D274BF416061219E410B992A0B3FC144CEF127B91DD905A2C1EC176CA3ABF40FB648C3E669FBF673F1E1DC7F5FDF2DA4752A71E4DA12ED338E14D218ED9B3232D3F03676FDEFF7DF5BA87D70DBA078F85146E2763245BD5F333F4F43F5FDECB6EC54D247BFD1C406FA17892831E6E1DF5B5FC6F2C003A85B37939FFE3A97A0064D95156BC00F20096G8DA0FC960F
	2F6A5FA916C26447AD2A3B9182B04590B2517E4D289D5F3D35A712789E9575AF7325A513FF9F976CECFD41AC6AF71633673A16DC02E777000E113BFCD017F6EF3435842091209D40F7AF6F5F74F78AE97FEA9DCE2DD2536C1DBD642BA76FDC2BD39E5051182430332393FE39523DACD7E24F28FBC77BC3CAD7FC49C83702350BE14F5820F4BF49B652A55CC75BE532E761FD41686CA4A72EE075AD6C59431EA3A6F4F48C3E7FBF46BB24E9F376DB781C8AEC9F4EF84C3DCF0F0176C974BCABD9627FE2BF980D637B
	68636FCBC26E3F2772314F7A5CE7241A97FE9F4757EB205CD5DC5EECDD6E3A4B1B124CE4D3B965788905799EF5B7662C9D2A078478B4E71DAC4E6E5B00279BA092E069ECFA56C9697EEA126B3608DC773959B457DD8FCF69FECAC7FD55521AD03E0A5FF94277CC20AD3C1F7AAAF2AF965EDF86BF75B4G7BC67AB74CFBE83485D925C7A3D9AA2D33A73EE928D3D982CB6B8E8CED96F508BF4C70B9CEFF7C910637D563DDE813AF9E481835F927BDB3D89BD00C6600354301B9A05A2671FE1EA3CECDB02BEF977527
	124C6AB5D1BF4B34FE8A545F5CDCDCFB9C5F034F216C0A316BABAA472EF7D569322C757AFC1CF3AE871EAEF940A6BE98574146181F63F05A9535BB2750D6DCE8BBDCD24057C94400F1FD163EF154748E93D9177753753118BD175E1E4B648F1F12A46EA7B2EB75777D0677461AD077897F9920B264CFD486757367CA031F93DCA8B5BFA768A8B5BFA768A99D7BFC5CDD9A7C1E5CC029711E9C5D279A9AF06E2E0371C9188331BAC0B9C0739C3EC7A59E433901B7FEC76FCF350EEEBCA746DB3E9A173F6ED8703E3E
	F2AC745D600D4770EC2DC355B4359779F37C9F0F61F8413AA7C80A0BAFC39BF91C5DBD9AED4A70A9E5B87795390B20E92E728C4987956857489C1E3FFC11647A61D00E90381D67F9CE33C860CEDC62341B84EE5285CE1B76990E7B7985CE1BA5F03F4A66340A40BD311953CE90381CD4CE9BA6F03E637CCCA7C660FEC4F06B203CD1604E27A3AEF5AE481D4BF1CB53B93F7C39BC666AE914040D6EAF435C3C55FE19C5D59802289EBE3227408F1C6E38ACF7EAAD76EEB1D7E6B461BE6283FEB38116G2C82D88B30
	89A06D810865GCA811A81FAGC6G428196GC4812CG08F9004FADCBF9C1B09DG498ED2488473581DB845D8CCF979A23FEB9F8B3C729F6467B1AFE5A33F313F1F37B8B5C52EE2DBAF7B3679250B7CBBEC7B037CFC067E86E5B61497832CF8101F850D083320B5004B1C474F8C16D773731666F91C472FC85BA9D00EGD8B26FC677836E2F55531250F1E4181FD32C92B217A5F0DCF284475D1244F1958237D260FC91FCAEE4B5A3EEB114D7C67071E8287F539863516CD15D32C79B24D8F20B135F7FEC883E66
	BFEE883D66BB9B481DBFD82BAD03EE761D282781E39D352B4BAB8B5F525B10A6F0755EE6556B44F3EB3A077D6204FFBE77BAD96F6B21EF9BA3F87C342255BF1FFB7AEFE8DF9AA37D79FC289849880179DC7EC351D60CE1DE0398094AF7931FD182788AGAAAF760CAE6F358B216499F377C0F9EE60793E1F3CBDFBD15E88602788F969AD3A3CE3292164A50DA1EF49C3F40D21BC4A7B3DF31244630758637E363712B35E65C0336AA1EEFBD73A3F6DBFEA55F5FBB6BD14EEA9E368A64D0F366EE03A51F972E8D370
	79780526507371EBCD66FB5063CD23772068A673BDE8C513F18FE2F75F1B308F5D60575AC43BE7846EA301FB0170C45CF927C36B91789709DE08BFA923C00E7F9951EB00605A78BEEC40FD44F0AFB7611A02AE77289ECE7B2BA6B2F6CEC521655F12B6CE97ACB1E10FD7090ECE375D203774A147FDA471D84AFDB1D44E1DBA5F6C6E4A3510F4FB834A146EEB04AEFF7E0D1D7F15C3BBFAD67192716D4D73834FB0E2897E427C40B30CC171FB449E28DBB91F1F9B5EBA4E331770F152BDF953F434DFDD648C267861
	407E2A84DF7EF0E0FFB70ABD6EA454ADFA187777963F3BD6E98B285D735833F4C130F300EFA598699AD93BF676540252CD767B86BE05351B4B1E0B03527D400FEEB9EB371ABD571B50317B984D78AEE2EB3A45313A7CE60C131DAE58F26C0AF308FCD3BD8B8DDD5D541FFD85178673078BDBEFB41F22344FB810163E97F56BFC86AF5C9C9F39C767336F0A4E67AB1BEF0ECF15227349D5F5BE8D69B747278EFF1A446E5FACCB5FC26C27B8DDF2B57D151F443FB7FC0F44DF52C208279672787555CA4495C239E2A1
	1FFFAF137C153E7F5670FE3D6A134F93E77F9F48D0EA74BE75995E7FD87D494B5E75096641F4CCE78F735C25D10C738F4F735C25DD600E36715CC591381884CE3BD460AAAFF15AD582B77F1278FEABF08F107837709148ED9F91B9D3BB791E8565F601DB5AAE648A5CBAC13BD460BE94342B846EB1C19B73880FF97E73A26E639AE69C38CE601D46C72F1179DE78A86CC70F72365B2B92F9DB092F9F67D5C950BCC1EC57836D35C7F9FC9706A98C0B21BEEC3B1978B54B709D887D7C8260096BB3D148CBCCA47981
	60178AF90FF9F5F9DF3FF4A371EBB0F96B01A77F7D8B6DFFD412FF8C7994317BC6E81790A9E248F67F987299CD57675976D05F07466AFFC9A46D3F9ECFEFCBC6799500EF0C647D7FCD1FAE6F5BA1650D557F9103BC4A3BE3C49236F825927FFE03091F2D04C0D581B7FB1839D9B6CF6A2F6B34CF1D3FB99FCDDCBDFBFF562273794B251B63735BF81D4F992B4E27217DC6789C91FC7A12F4BEB1A9BA1F2A1B64C3BE57CB74EFE9BC9E3F898267C5AFCB091A14011732ECEA170D7D09889B7E0D8818C71A74D60D5C
	E56FD3B41B2A5F1932F9715254786B761C112F76B9BD322DD2D1ECF87B29DB76F8ED748A1457A3ED95FD29A781771A7EF74B62FF76F03CC5CA28172CB481291792B00B281792C912D1AF45C38262431D554AA699E6F9DB3CD725EB84036642E50229FC12403EA151FFD833DBE0F38C58940135B0AE98C4D98556EA40DAC077ADFE63702D077E727315BFED7546634172DB380C4F4A9E92A9498E3C054BFEF2DFA72538D5A7D4829EBA66A50FC42FB628480DD2C697DACB76EA32231247E0DECD5A5108128E61714D
	2113F6EF0F06395A7B0DD2028B7FB800341DD6ABF6D777A19A103DCF2C93D759CE7A064BD182FD53093E4825C8F0C97F4656837BCF2DDD92AFD96A24B794556E2833F7EA2A27DECA51FABC3237C7D59CBE77879504D172GFA841CD67140EE0F68BEF636883FBF88252F789E885B32EECB1D85FF3CAA9DA2AD51EB9509960E6A9E3B87C4491EFA8DA70D6A4993F5376C558EBEFDE257850B9447BA09C330556756A4EB173D57298C42CB8D1975E4F052B55056E3F7F971C7DF2019ADC0B3DEA584944DF832F856E7
	7F70B1912013EA52BEEACAA0BD54AD2B3D07AA3B3AA0DA938C402E40BEDB3047B6C581ED367B266FB8757C6B9DB08FEA24785E62E2727FE1727F917C7FB0419CA61843C740383152187F826BFD3C4FE47208B50443DFD487A3287D7865A7677F24BCF6BD742ECECAA4BF7202C1C853DBFD58575BA1FBA6AF1A153DF9661ABBF10C5CB8D5FC48F717CF4CF7FE653E088CB8471631350487F61BB5A9913DC919A6EDE3F7A4ED7C12A46036B7B9DDB63C9429425A3661F729B20333B0DC4C341D254EA223468E03EB63
	AD88360CC85EB2EABEA3F65B680909689DE2FAE0A913B425CBBE7B260D0DD47B886DFEE123BF6BB6C2DF4DFF3638755327E9092EE9A253D477F41FAACD5F57B5CDE11A2297CE7C74E9DAB5FA7449E5B9CD5A452F915B78E2B86FF4B95481CD5ACC3E4572A57086FE3D6505247C13A74BFAFB8B74B753AE478569F202FF1739EF3052F3AF2ABA52FCB236785077699B1F737EB3E99BEB3B7B532769C5DD539C0326EF98B425FA257EFFE9DA414A322EA9CF12700795591FBECD95DD538386CDD369F77B026B466F76
	9BA7E5867DEEDF9E456E5DC66977DB68F723A812970581BEB20AFFB7CAC87E33947CDB6259DE3B22E8F665CA254B015F952B86B5D97FDE7D75E4646F65EDD8A57B7BB2492877E5834D7F82D0CB87888EB7AE416999GGF8CFGGD0CB818294G94G88G88GEEF954AC8EB7AE416999GGF8CFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA39AGGGG
**end of data**/
}
}
