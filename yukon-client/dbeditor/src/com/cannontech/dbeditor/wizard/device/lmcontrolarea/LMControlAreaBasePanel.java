package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Map;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.util.CtiUtilities;
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
	private javax.swing.JLabel ivjJLabelWarning = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldTimeEntryStart = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldTimeEntryStop = null;
	private javax.swing.JComboBox ivjJComboBoxControlInterval = null;
	private javax.swing.JComboBox ivjJComboBoxMinRespTime = null;
	private javax.swing.JCheckBox ivjJCheckBoxReqAllTrigActive = null;

	private static final Map<String, String> STRING_MAP = new Hashtable<String, String>(6);
	
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

			ivjJComboBoxMinRespTime.addItem(CtiUtilities.STRING_NONE);
			ivjJComboBoxMinRespTime.addItem("1 minute");
			ivjJComboBoxMinRespTime.addItem("2 minute");
			ivjJComboBoxMinRespTime.addItem("3 minute");
			ivjJComboBoxMinRespTime.addItem("4 minute");
			ivjJComboBoxMinRespTime.addItem("5 minute");
			ivjJComboBoxMinRespTime.addItem("10 minute");
			ivjJComboBoxMinRespTime.addItem("15 minute");
			ivjJComboBoxMinRespTime.addItem("30 minute");
			
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
private javax.swing.JLabel getJLabelStartTime() {
	if (ivjJLabelStartTime == null) {
		try {
			ivjJLabelStartTime = new javax.swing.JLabel();
			ivjJLabelStartTime.setName("JLabelStartTime");
			ivjJLabelStartTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStartTime.setText("Start Time:");
			ivjJLabelStartTime.setMinimumSize(new Dimension(70,20));
			ivjJLabelStartTime.setPreferredSize(new Dimension(70,20));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJLabelStopTime.setMinimumSize(new Dimension(70,20));
			ivjJLabelStopTime.setPreferredSize(new Dimension(70,20));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJLabelTime1.setMinimumSize(new Dimension(60,20));
			ivjJLabelTime1.setPreferredSize(new Dimension(60,20));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJLabelTime2.setMinimumSize(new Dimension(60,20));
			ivjJLabelTime2.setPreferredSize(new Dimension(60,20));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelTime2;
}
/**
 * Return the JLabelDailyStartTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelWarning() {
	if (ivjJLabelWarning == null) {
		try {
			ivjJLabelWarning = new javax.swing.JLabel();
			ivjJLabelWarning.setName("JLabelWarning");
			ivjJLabelWarning.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelWarning.setText("Control Window changes take effect at midnight");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelWarning;
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
			constraintsJLabelStartTime.gridx = 0; constraintsJLabelStartTime.gridy = 0;
			constraintsJLabelStartTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStartTime.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanelOptional().add(getJLabelStartTime(), constraintsJLabelStartTime);

			java.awt.GridBagConstraints constraintsJLabelStopTime = new java.awt.GridBagConstraints();
			constraintsJLabelStopTime.gridx = 0; constraintsJLabelStopTime.gridy = 1;
			constraintsJLabelStopTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStopTime.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanelOptional().add(getJLabelStopTime(), constraintsJLabelStopTime);
			
			java.awt.GridBagConstraints constraintsJLabelWarning = new java.awt.GridBagConstraints();
            constraintsJLabelWarning.gridx = 0; constraintsJLabelWarning.gridy = 2;
            constraintsJLabelWarning.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelWarning.gridwidth = 3;
            constraintsJLabelWarning.insets = new java.awt.Insets(5, 5, 5, 5);
            getJPanelOptional().add(getJLabelWarning(), constraintsJLabelWarning);
			
			java.awt.GridBagConstraints constraintsJTextFieldTimeEntryStart = new java.awt.GridBagConstraints();
            constraintsJTextFieldTimeEntryStart.gridx = 1; constraintsJTextFieldTimeEntryStart.gridy = 0;
            constraintsJTextFieldTimeEntryStart.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJTextFieldTimeEntryStart.insets = new java.awt.Insets(5, 5, 5, 5);
            getJPanelOptional().add(getJTextFieldTimeEntryStart(), constraintsJTextFieldTimeEntryStart);
            
            java.awt.GridBagConstraints constraintsJTextFieldTimeEntryStop = new java.awt.GridBagConstraints();
            constraintsJTextFieldTimeEntryStop.gridx = 1; constraintsJTextFieldTimeEntryStop.gridy = 1;
            constraintsJTextFieldTimeEntryStop.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJTextFieldTimeEntryStop.insets = new java.awt.Insets(5, 5, 5, 5);
            getJPanelOptional().add(getJTextFieldTimeEntryStop(), constraintsJTextFieldTimeEntryStop);

			java.awt.GridBagConstraints constraintsJLabelTime1 = new java.awt.GridBagConstraints();
			constraintsJLabelTime1.gridx = 2; constraintsJLabelTime1.gridy = 0;
			constraintsJLabelTime1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTime1.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanelOptional().add(getJLabelTime1(), constraintsJLabelTime1);

			java.awt.GridBagConstraints constraintsJLabelTime2 = new java.awt.GridBagConstraints();
			constraintsJLabelTime2.gridx = 2; constraintsJLabelTime2.gridy = 1;
			constraintsJLabelTime2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTime2.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanelOptional().add(getJLabelTime2(), constraintsJLabelTime2);

		} catch (java.lang.Throwable ivjExc) {
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
			ivjJTextFieldTimeEntryStart.setMinimumSize(new Dimension(40,20));
			ivjJTextFieldTimeEntryStart.setPreferredSize(new Dimension(40,20));
		} catch (java.lang.Throwable ivjExc) {
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
			ivjJTextFieldTimeEntryStop.setMinimumSize(new Dimension(40,20));
			ivjJTextFieldTimeEntryStop.setPreferredSize(new Dimension(40,20));
		} catch (java.lang.Throwable ivjExc) {
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
	if(((String)getJComboBoxControlInterval().getSelectedItem()).compareTo("(On New Data Only)") == 0)
		controlArea.getControlArea().setControlInterval(new Integer(0)); 
	else
		controlArea.getControlArea().setControlInterval( 
			com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue(getJComboBoxControlInterval()) );

	if(((String)getJComboBoxMinRespTime().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		controlArea.getControlArea().setMinResponseTime(new Integer(0));
	else
		controlArea.getControlArea().setMinResponseTime( 
			com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue(getJComboBoxMinRespTime()) );

	controlArea.getControlArea().setDefOperationalState(
			STRING_MAP.get(getJComboBoxOperationalState().getSelectedItem().toString()).toString() );

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
		setSize(500, 500);

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
		constraintsJPanelOptional.insets = new java.awt.Insets(5, 5, 5, 5);
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
		frame.setVisible(true);
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

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getJTextFieldName().requestFocus(); 
        } 
    });    
}

}
