package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.common.util.StringUtils;

public class DirectModifyGearPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private String gearType = null;
	private java.util.Hashtable paoHashTable = null;
	private javax.swing.JComboBox ivjJComboBoxHowToStop = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldPercentReduction = null;
	private javax.swing.JLabel ivjJLabelHowToStop = null;
	private javax.swing.JLabel ivjJLabelPercentReduction = null;
	private javax.swing.JPanel ivjJPanelHolder = null;
	private javax.swing.JComboBox ivjJComboBoxWhenChange = null;
	private javax.swing.JLabel ivjJLabelWhenChange = null;
	private javax.swing.JComboBox ivjJComboBoxGroupSelection = null;
	private javax.swing.JComboBox ivjJComboBoxNumGroups = null;
	private javax.swing.JComboBox ivjJComboBoxSendRate = null;
	private javax.swing.JComboBox ivjJComboBoxShedTime = null;
	private javax.swing.JLabel ivjJLabelGroupSelection = null;
	private javax.swing.JLabel ivjJLabelNumGroups = null;
	private javax.swing.JLabel ivjJLabelSendRate = null;
	private javax.swing.JLabel ivjJLabelShedTime = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldControlPercent = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldCyclePeriod = null;
	private javax.swing.JLabel ivjJLabelControlPercent = null;
	private javax.swing.JLabel ivjJLabelCyclePeriod = null;
	private javax.swing.JLabel ivjJLabelMin = null;
	private javax.swing.JComboBox ivjJComboBoxPeriodCount = null;
	private javax.swing.JLabel ivjJLabelPeriodCount = null;
	private javax.swing.JLabel ivjJLabelGearName = null;
	private javax.swing.JTextField ivjJTextFieldGearName = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeDuration = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangePriority = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeTriggerNumber = null;
	private javax.swing.JLabel ivjJLabelChangeDuration = null;
	private javax.swing.JLabel ivjJLabelChangePriority = null;
	private javax.swing.JLabel ivjJLabelChangeTriggerNumber = null;
	private javax.swing.JLabel ivjJLabelChangeTriggerOffset = null;
	private javax.swing.JLabel ivjJLabelMinutesChDur = null;
	private javax.swing.JPanel ivjJPanelChangeMethod = null;
	private javax.swing.JTextField ivjJTextFieldChangeTriggerOffset = null;
	private javax.swing.JComboBox ivjJComboBoxGearType = null;
	private javax.swing.JLabel ivjJLabelGearType = null;
	private javax.swing.JComboBox ivjJComboBoxControlStartState = null;
	private javax.swing.JLabel ivjJLabelControlStartState = null;
	private javax.swing.JComboBox ivjJComboBoxCycleCountSndType = null;
	private javax.swing.JLabel ivjJLabelCycleCntSndType = null;
	private javax.swing.JComboBox ivjJComboBoxMaxCycleCount = null;
	private javax.swing.JLabel ivjJLabelMaxCycleCnt = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DirectModifyGearPanel() {
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
	if (e.getSource() == getJComboBoxWhenChange()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxGearType()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxShedTime()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxNumGroups()) 
		connEtoC5(e);
	if (e.getSource() == getJComboBoxPeriodCount()) 
		connEtoC6(e);
	if (e.getSource() == getJComboBoxSendRate()) 
		connEtoC7(e);
	if (e.getSource() == getJComboBoxGroupSelection()) 
		connEtoC8(e);
	if (e.getSource() == getJComboBoxHowToStop()) 
		connEtoC10(e);
	if (e.getSource() == getJComboBoxControlStartState()) 
		connEtoC9(e);
	if (e.getSource() == getJComboBoxCycleCountSndType()) 
		connEtoC11(e);
	if (e.getSource() == getJComboBoxMaxCycleCount()) 
		connEtoC12(e);
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
	if (e.getSource() == getJTextFieldGearName()) 
		connEtoC3(e);
	if (e.getSource() == getJTextFieldChangeTriggerOffset()) 
		connEtoC13(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxWhenChange.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.jComboBoxWhenChange_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxWhenChange_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (JComboBoxHowToStop.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * connEtoC11:  (JComboBoxCycleCountSndType.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * connEtoC12:  (JComboBoxCycleCountSndType1.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * connEtoC13:  (JTextFieldChangeTriggerOffset.caret.caretUpdate(javax.swing.event.CaretEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (JComboBoxGearType.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.jComboBoxGearType_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxGearType_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JTextFieldGearName.caret.caretUpdate(javax.swing.event.CaretEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JComboBoxShedTime.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (JComboBoxNumGroups.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * connEtoC6:  (JComboBoxPeriodCount.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * connEtoC7:  (JComboBoxSendRate.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * connEtoC8:  (JComboBoxGroupSelection.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * connEtoC9:  (JComboBoxLatchCommand.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * Insert the method's description here.
 * Creation date: (2/12/2002 12:36:14 PM)
 * @param change java.lang.String
 */
private String getChangeCondition( String change )
{

	if( change.equalsIgnoreCase("After a Duration") )
	{
		return LMProgramDirectGear.CHANGE_DURATION;
	}
	else if( change.equalsIgnoreCase("Priority Change") )
	{
		return LMProgramDirectGear.CHANGE_PRIORITY;
	}
	else if( change.equalsIgnoreCase("Above Trigger") )
	{
		return LMProgramDirectGear.CHANGE_TRIGGER_OFFSET;
	}	
	else
		return LMProgramDirectGear.CHANGE_NONE;

}
/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 5:37:00 PM)
 * @return java.lang.String
 */
public java.lang.String getGearType() {
	return gearType;
}
/**
 * Return the JComboBoxLatchCommand property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxControlStartState() {
	if (ivjJComboBoxControlStartState == null) {
		try {
			ivjJComboBoxControlStartState = new javax.swing.JComboBox();
			ivjJComboBoxControlStartState.setName("JComboBoxControlStartState");
			// user code begin {1}

			ivjJComboBoxControlStartState.addItem("OPEN  (RawState: 0)");
			ivjJComboBoxControlStartState.addItem("CLOSE (RawState: 1)");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxControlStartState;
}
/**
 * Return the JComboBoxCycleCountSndType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCycleCountSndType() {
	if (ivjJComboBoxCycleCountSndType == null) {
		try {
			ivjJComboBoxCycleCountSndType = new javax.swing.JComboBox();
			ivjJComboBoxCycleCountSndType.setName("JComboBoxCycleCountSndType");
			// user code begin {1}

			ivjJComboBoxCycleCountSndType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.OPTION_FIXED_COUNT ) );
			ivjJComboBoxCycleCountSndType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.OPTION_COUNT_DOWN ) );
			ivjJComboBoxCycleCountSndType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.OPTION_LIMITED_COUNT_DOWN ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxCycleCountSndType;
}
/**
 * Return the JComboBoxGearType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGearType() {
	if (ivjJComboBoxGearType == null) {
		try {
			ivjJComboBoxGearType = new javax.swing.JComboBox();
			ivjJComboBoxGearType.setName("JComboBoxGearType");
			// user code begin {1}

			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_TIME_REFRESH ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_ROTATION ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_MASTER_CYCLE ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_SMART_CYCLE ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_LATCHING ) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGearType;
}
/**
 * Return the JComboBoxGroupSelection property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGroupSelection() {
	if (ivjJComboBoxGroupSelection == null) {
		try {
			ivjJComboBoxGroupSelection = new javax.swing.JComboBox();
			ivjJComboBoxGroupSelection.setName("JComboBoxGroupSelection");
			// user code begin {1}

			ivjJComboBoxGroupSelection.addItem( StringUtils.addCharBetweenWords( ' ', com.cannontech.database.db.device.lm.LMProgramDirectGear.SELECTION_LAST_CONTROLLED ) );			
			ivjJComboBoxGroupSelection.addItem( StringUtils.addCharBetweenWords( ' ', com.cannontech.database.db.device.lm.LMProgramDirectGear.SELECTION_ALWAYS_FIRST_GROUP ) );			
			ivjJComboBoxGroupSelection.addItem( StringUtils.addCharBetweenWords( ' ', com.cannontech.database.db.device.lm.LMProgramDirectGear.SELECTION_LEAST_CONTROL_TIME ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGroupSelection;
}
/**
 * Return the JComboBoxHowToStop property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxHowToStop() {
	if (ivjJComboBoxHowToStop == null) {
		try {
			ivjJComboBoxHowToStop = new javax.swing.JComboBox();
			ivjJComboBoxHowToStop.setName("JComboBoxHowToStop");
			// user code begin {1}

			ivjJComboBoxHowToStop.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_TIME_IN ) );
			ivjJComboBoxHowToStop.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_RESTORE ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxHowToStop;
}
/**
 * Return the JComboBoxCycleCountSndType1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxMaxCycleCount() {
	if (ivjJComboBoxMaxCycleCount == null) {
		try {
			ivjJComboBoxMaxCycleCount = new javax.swing.JComboBox();
			ivjJComboBoxMaxCycleCount.setName("JComboBoxMaxCycleCount");
			// user code begin {1}

			ivjJComboBoxMaxCycleCount.addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
			for( int i = 1; i <= 16; i++ )
				ivjJComboBoxMaxCycleCount.addItem( new Integer(i) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxMaxCycleCount;
}
/**
 * Return the JComboBoxNumGroups property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxNumGroups() {
	if (ivjJComboBoxNumGroups == null) {
		try {
			ivjJComboBoxNumGroups = new javax.swing.JComboBox();
			ivjJComboBoxNumGroups.setName("JComboBoxNumGroups");
			// user code begin {1}

			ivjJComboBoxNumGroups.addItem("All of Them");
			for( int i = 1; i < 26; i++ )
				ivjJComboBoxNumGroups.addItem( new Integer(i) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxNumGroups;
}
/**
 * Return the JComboBoxCycleCount property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxPeriodCount() {
	if (ivjJComboBoxPeriodCount == null) {
		try {
			ivjJComboBoxPeriodCount = new javax.swing.JComboBox();
			ivjJComboBoxPeriodCount.setName("JComboBoxPeriodCount");
			// user code begin {1}

			for( int i = 1; i < 49; i++ )
				ivjJComboBoxPeriodCount.addItem( new Integer(i) );

			//default value
			ivjJComboBoxPeriodCount.setSelectedItem( new Integer(8) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxPeriodCount;
}
/**
 * Return the JComboBoxSendRate property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSendRate() {
	if (ivjJComboBoxSendRate == null) {
		try {
			ivjJComboBoxSendRate = new javax.swing.JComboBox();
			ivjJComboBoxSendRate.setName("JComboBoxSendRate");
			// user code begin {1}

			ivjJComboBoxSendRate.addItem("1 minute");
			ivjJComboBoxSendRate.addItem("2 minutes");
			ivjJComboBoxSendRate.addItem("5 minutes");
			ivjJComboBoxSendRate.addItem("10 minutes");
			ivjJComboBoxSendRate.addItem("15 minutes");
			ivjJComboBoxSendRate.addItem("20 minutes");
			ivjJComboBoxSendRate.addItem("30 minutes");
			ivjJComboBoxSendRate.addItem("45 minutes");
			ivjJComboBoxSendRate.addItem("1 hour");
			ivjJComboBoxSendRate.addItem("2 hours");
			ivjJComboBoxSendRate.addItem("3 hours");
			ivjJComboBoxSendRate.addItem("4 hours");
			ivjJComboBoxSendRate.addItem("5 hours");
			ivjJComboBoxSendRate.addItem("6 hours");
			ivjJComboBoxSendRate.addItem("7 hours");
			ivjJComboBoxSendRate.addItem("8 hours");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSendRate;
}
/**
 * Return the JComboBoxShedTime property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxShedTime() {
	if (ivjJComboBoxShedTime == null) {
		try {
			ivjJComboBoxShedTime = new javax.swing.JComboBox();
			ivjJComboBoxShedTime.setName("JComboBoxShedTime");
			// user code begin {1}

			ivjJComboBoxShedTime.addItem("5 minutes");
			ivjJComboBoxShedTime.addItem("7 minutes");
			ivjJComboBoxShedTime.addItem("10 minutes");
			ivjJComboBoxShedTime.addItem("15 minutes");
			ivjJComboBoxShedTime.addItem("20 minutes");
			ivjJComboBoxShedTime.addItem("30 minutes");
			ivjJComboBoxShedTime.addItem("45 minutes");
			ivjJComboBoxShedTime.addItem("1 hour");
			ivjJComboBoxShedTime.addItem("2 hours");
			ivjJComboBoxShedTime.addItem("3 hours");
			ivjJComboBoxShedTime.addItem("4 hours");
			ivjJComboBoxShedTime.addItem("6 hours");
			ivjJComboBoxShedTime.addItem("8 hours");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxShedTime;
}
/**
 * Return the JComboBoxWhenChange property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxWhenChange() {
	if (ivjJComboBoxWhenChange == null) {
		try {
			ivjJComboBoxWhenChange = new javax.swing.JComboBox();
			ivjJComboBoxWhenChange.setName("JComboBoxWhenChange");
			// user code begin {1}

			ivjJComboBoxWhenChange.addItem("Manually Only");
			ivjJComboBoxWhenChange.addItem("After a Duration");
			ivjJComboBoxWhenChange.addItem("Priority Change");
			ivjJComboBoxWhenChange.addItem("Above Trigger");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxWhenChange;
}
/**
 * Return the JCSpinFieldChangeDuration property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldChangeDuration() {
	if (ivjJCSpinFieldChangeDuration == null) {
		try {
			ivjJCSpinFieldChangeDuration = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldChangeDuration.setName("JCSpinFieldChangeDuration");
			ivjJCSpinFieldChangeDuration.setMaximumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}

			ivjJCSpinFieldChangeDuration.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(3)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldChangeDuration;
}
/**
 * Return the JCSpinFieldChangePriority property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldChangePriority() {
	if (ivjJCSpinFieldChangePriority == null) {
		try {
			ivjJCSpinFieldChangePriority = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldChangePriority.setName("JCSpinFieldChangePriority");
			ivjJCSpinFieldChangePriority.setMaximumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}

			ivjJCSpinFieldChangePriority.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(9999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(0)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldChangePriority;
}
/**
 * Return the JCSpinFieldChangeTriggerNumber property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldChangeTriggerNumber() {
	if (ivjJCSpinFieldChangeTriggerNumber == null) {
		try {
			ivjJCSpinFieldChangeTriggerNumber = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldChangeTriggerNumber.setName("JCSpinFieldChangeTriggerNumber");
			ivjJCSpinFieldChangeTriggerNumber.setMaximumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}

			ivjJCSpinFieldChangeTriggerNumber.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(0)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldChangeTriggerNumber;
}
/**
 * Return the JCSpinFieldControlPercent property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldControlPercent() {
	if (ivjJCSpinFieldControlPercent == null) {
		try {
			ivjJCSpinFieldControlPercent = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldControlPercent.setName("JCSpinFieldControlPercent");
			ivjJCSpinFieldControlPercent.setMaximumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}

			ivjJCSpinFieldControlPercent.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(5), new Integer(100), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(50)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldControlPercent;
}
/**
 * Return the JCSpinFieldCyclePeriod property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldCyclePeriod() {
	if (ivjJCSpinFieldCyclePeriod == null) {
		try {
			ivjJCSpinFieldCyclePeriod = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldCyclePeriod.setName("JCSpinFieldCyclePeriod");
			ivjJCSpinFieldCyclePeriod.setMaximumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}

			ivjJCSpinFieldCyclePeriod.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(5), new Integer(60), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(30)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldCyclePeriod;
}
/**
 * Return the JCSpinFieldPercentReduction property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldPercentReduction() {
	if (ivjJCSpinFieldPercentReduction == null) {
		try {
			ivjJCSpinFieldPercentReduction = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldPercentReduction.setName("JCSpinFieldPercentReduction");
			ivjJCSpinFieldPercentReduction.setMaximumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}

			ivjJCSpinFieldPercentReduction.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(100), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(100)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

			ivjJCSpinFieldPercentReduction.setValue( new Integer(100) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldPercentReduction;
}
/**
 * Return the JLabelChangeDuration property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelChangeDuration() {
	if (ivjJLabelChangeDuration == null) {
		try {
			ivjJLabelChangeDuration = new javax.swing.JLabel();
			ivjJLabelChangeDuration.setName("JLabelChangeDuration");
			ivjJLabelChangeDuration.setText("Change Duration:");
			ivjJLabelChangeDuration.setMaximumSize(new java.awt.Dimension(103, 14));
			ivjJLabelChangeDuration.setPreferredSize(new java.awt.Dimension(103, 14));
			ivjJLabelChangeDuration.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelChangeDuration.setMinimumSize(new java.awt.Dimension(103, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelChangeDuration;
}
/**
 * Return the JLabelChangePriority property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelChangePriority() {
	if (ivjJLabelChangePriority == null) {
		try {
			ivjJLabelChangePriority = new javax.swing.JLabel();
			ivjJLabelChangePriority.setName("JLabelChangePriority");
			ivjJLabelChangePriority.setText("Change Priority:");
			ivjJLabelChangePriority.setMaximumSize(new java.awt.Dimension(103, 14));
			ivjJLabelChangePriority.setPreferredSize(new java.awt.Dimension(103, 14));
			ivjJLabelChangePriority.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelChangePriority.setMinimumSize(new java.awt.Dimension(103, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelChangePriority;
}
/**
 * Return the JLabelChangeTriggerNumber property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelChangeTriggerNumber() {
	if (ivjJLabelChangeTriggerNumber == null) {
		try {
			ivjJLabelChangeTriggerNumber = new javax.swing.JLabel();
			ivjJLabelChangeTriggerNumber.setName("JLabelChangeTriggerNumber");
			ivjJLabelChangeTriggerNumber.setText("Trigger Number:");
			ivjJLabelChangeTriggerNumber.setMaximumSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerNumber.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabelChangeTriggerNumber.setPreferredSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerNumber.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelChangeTriggerNumber.setMinimumSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelChangeTriggerNumber;
}
/**
 * Return the JLabelChangeTriggerOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelChangeTriggerOffset() {
	if (ivjJLabelChangeTriggerOffset == null) {
		try {
			ivjJLabelChangeTriggerOffset = new javax.swing.JLabel();
			ivjJLabelChangeTriggerOffset.setName("JLabelChangeTriggerOffset");
			ivjJLabelChangeTriggerOffset.setText("Trigger Offset:");
			ivjJLabelChangeTriggerOffset.setMaximumSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerOffset.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabelChangeTriggerOffset.setPreferredSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerOffset.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelChangeTriggerOffset.setMinimumSize(new java.awt.Dimension(143, 14));
			ivjJLabelChangeTriggerOffset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelChangeTriggerOffset;
}
/**
 * Return the JLabelControlPercent property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlPercent() {
	if (ivjJLabelControlPercent == null) {
		try {
			ivjJLabelControlPercent = new javax.swing.JLabel();
			ivjJLabelControlPercent.setName("JLabelControlPercent");
			ivjJLabelControlPercent.setText("Control Percent:");
			ivjJLabelControlPercent.setMaximumSize(new java.awt.Dimension(112, 14));
			ivjJLabelControlPercent.setPreferredSize(new java.awt.Dimension(112, 14));
			ivjJLabelControlPercent.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelControlPercent.setMinimumSize(new java.awt.Dimension(112, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControlPercent;
}
/**
 * Return the JLabelLatchCommand property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlStartState() {
	if (ivjJLabelControlStartState == null) {
		try {
			ivjJLabelControlStartState = new javax.swing.JLabel();
			ivjJLabelControlStartState.setName("JLabelControlStartState");
			ivjJLabelControlStartState.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelControlStartState.setText("Control Start State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControlStartState;
}
/**
 * Return the JLabelCycleCntSndType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCycleCntSndType() {
	if (ivjJLabelCycleCntSndType == null) {
		try {
			ivjJLabelCycleCntSndType = new javax.swing.JLabel();
			ivjJLabelCycleCntSndType.setName("JLabelCycleCntSndType");
			ivjJLabelCycleCntSndType.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelCycleCntSndType.setText("Cycle Count Send Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCycleCntSndType;
}
/**
 * Return the JLabelCyclePeriod property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCyclePeriod() {
	if (ivjJLabelCyclePeriod == null) {
		try {
			ivjJLabelCyclePeriod = new javax.swing.JLabel();
			ivjJLabelCyclePeriod.setName("JLabelCyclePeriod");
			ivjJLabelCyclePeriod.setText("Cycle Period:");
			ivjJLabelCyclePeriod.setMaximumSize(new java.awt.Dimension(112, 14));
			ivjJLabelCyclePeriod.setPreferredSize(new java.awt.Dimension(112, 14));
			ivjJLabelCyclePeriod.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelCyclePeriod.setMinimumSize(new java.awt.Dimension(112, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCyclePeriod;
}
/**
 * Return the JLabelGearName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGearName() {
	if (ivjJLabelGearName == null) {
		try {
			ivjJLabelGearName = new javax.swing.JLabel();
			ivjJLabelGearName.setName("JLabelGearName");
			ivjJLabelGearName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelGearName.setText("Gear Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGearName;
}
/**
 * Return the JLabelGearType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGearType() {
	if (ivjJLabelGearType == null) {
		try {
			ivjJLabelGearType = new javax.swing.JLabel();
			ivjJLabelGearType.setName("JLabelGearType");
			ivjJLabelGearType.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelGearType.setText("Gear Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGearType;
}
/**
 * Return the JLabelGroupSelection property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGroupSelection() {
	if (ivjJLabelGroupSelection == null) {
		try {
			ivjJLabelGroupSelection = new javax.swing.JLabel();
			ivjJLabelGroupSelection.setName("JLabelGroupSelection");
			ivjJLabelGroupSelection.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelGroupSelection.setText("Group Selection Method:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroupSelection;
}
/**
 * Return the JLabelHowToStop property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHowToStop() {
	if (ivjJLabelHowToStop == null) {
		try {
			ivjJLabelHowToStop = new javax.swing.JLabel();
			ivjJLabelHowToStop.setName("JLabelHowToStop");
			ivjJLabelHowToStop.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelHowToStop.setText("How to Stop Control:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHowToStop;
}
/**
 * Return the JLabelCycleCntSndType1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMaxCycleCnt() {
	if (ivjJLabelMaxCycleCnt == null) {
		try {
			ivjJLabelMaxCycleCnt = new javax.swing.JLabel();
			ivjJLabelMaxCycleCnt.setName("JLabelMaxCycleCnt");
			ivjJLabelMaxCycleCnt.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMaxCycleCnt.setText("Max Cycle Cnt:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMaxCycleCnt;
}
/**
 * Return the JLabelMin property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMin() {
	if (ivjJLabelMin == null) {
		try {
			ivjJLabelMin = new javax.swing.JLabel();
			ivjJLabelMin.setName("JLabelMin");
			ivjJLabelMin.setText("(min.)");
			ivjJLabelMin.setMaximumSize(new java.awt.Dimension(112, 14));
			ivjJLabelMin.setPreferredSize(new java.awt.Dimension(112, 14));
			ivjJLabelMin.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMin.setMinimumSize(new java.awt.Dimension(112, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMin;
}
/**
 * Return the JLabelMinutesChDur property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinutesChDur() {
	if (ivjJLabelMinutesChDur == null) {
		try {
			ivjJLabelMinutesChDur = new javax.swing.JLabel();
			ivjJLabelMinutesChDur.setName("JLabelMinutesChDur");
			ivjJLabelMinutesChDur.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMinutesChDur.setText("(min.)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinutesChDur;
}
/**
 * Return the JLabelNumGroups property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNumGroups() {
	if (ivjJLabelNumGroups == null) {
		try {
			ivjJLabelNumGroups = new javax.swing.JLabel();
			ivjJLabelNumGroups.setName("JLabelNumGroups");
			ivjJLabelNumGroups.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelNumGroups.setText("# of Groups Each Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNumGroups;
}
/**
 * Return the JLabelPercentReduction property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPercentReduction() {
	if (ivjJLabelPercentReduction == null) {
		try {
			ivjJLabelPercentReduction = new javax.swing.JLabel();
			ivjJLabelPercentReduction.setName("JLabelPercentReduction");
			ivjJLabelPercentReduction.setText("Group Capacity Reduction %:");
			ivjJLabelPercentReduction.setMaximumSize(new java.awt.Dimension(112, 14));
			ivjJLabelPercentReduction.setPreferredSize(new java.awt.Dimension(112, 14));
			ivjJLabelPercentReduction.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelPercentReduction.setMinimumSize(new java.awt.Dimension(112, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPercentReduction;
}
/**
 * Return the JLabelCycleCount property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPeriodCount() {
	if (ivjJLabelPeriodCount == null) {
		try {
			ivjJLabelPeriodCount = new javax.swing.JLabel();
			ivjJLabelPeriodCount.setName("JLabelPeriodCount");
			ivjJLabelPeriodCount.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelPeriodCount.setText("Starting Period Count:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPeriodCount;
}
/**
 * Return the JLabelSendRate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSendRate() {
	if (ivjJLabelSendRate == null) {
		try {
			ivjJLabelSendRate = new javax.swing.JLabel();
			ivjJLabelSendRate.setName("JLabelSendRate");
			ivjJLabelSendRate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSendRate.setText("Command Resend Rate:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSendRate;
}
/**
 * Return the JLabelShedTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelShedTime() {
	if (ivjJLabelShedTime == null) {
		try {
			ivjJLabelShedTime = new javax.swing.JLabel();
			ivjJLabelShedTime.setName("JLabelShedTime");
			ivjJLabelShedTime.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelShedTime.setText("Shed Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelShedTime;
}
/**
 * Return the JLabelWhenChange property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelWhenChange() {
	if (ivjJLabelWhenChange == null) {
		try {
			ivjJLabelWhenChange = new javax.swing.JLabel();
			ivjJLabelWhenChange.setName("JLabelWhenChange");
			ivjJLabelWhenChange.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelWhenChange.setText("When to Change:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelWhenChange;
}
/**
 * Return the JPanelChangeMethod property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelChangeMethod() {
	if (ivjJPanelChangeMethod == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Auto Gear Change Method");
			ivjJPanelChangeMethod = new javax.swing.JPanel();
			ivjJPanelChangeMethod.setName("JPanelChangeMethod");
			ivjJPanelChangeMethod.setBorder(ivjLocalBorder);
			ivjJPanelChangeMethod.setLayout(new java.awt.GridBagLayout());
			ivjJPanelChangeMethod.setFont(new java.awt.Font("dialog", 0, 12));

			java.awt.GridBagConstraints constraintsJLabelChangeDuration = new java.awt.GridBagConstraints();
			constraintsJLabelChangeDuration.gridx = 1; constraintsJLabelChangeDuration.gridy = 2;
			constraintsJLabelChangeDuration.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelChangeDuration.ipadx = -5;
			constraintsJLabelChangeDuration.ipady = 6;
			constraintsJLabelChangeDuration.insets = new java.awt.Insets(1, 8, 3, 1);
			getJPanelChangeMethod().add(getJLabelChangeDuration(), constraintsJLabelChangeDuration);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeDuration = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeDuration.gridx = 2; constraintsJCSpinFieldChangeDuration.gridy = 2;
			constraintsJCSpinFieldChangeDuration.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldChangeDuration.ipadx = 54;
			constraintsJCSpinFieldChangeDuration.ipady = 19;
			constraintsJCSpinFieldChangeDuration.insets = new java.awt.Insets(1, 1, 3, 1);
			getJPanelChangeMethod().add(getJCSpinFieldChangeDuration(), constraintsJCSpinFieldChangeDuration);

			java.awt.GridBagConstraints constraintsJLabelMinutesChDur = new java.awt.GridBagConstraints();
			constraintsJLabelMinutesChDur.gridx = 3; constraintsJLabelMinutesChDur.gridy = 2;
			constraintsJLabelMinutesChDur.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelMinutesChDur.ipadx = 5;
			constraintsJLabelMinutesChDur.ipady = -2;
			constraintsJLabelMinutesChDur.insets = new java.awt.Insets(5, 2, 5, 8);
			getJPanelChangeMethod().add(getJLabelMinutesChDur(), constraintsJLabelMinutesChDur);

			java.awt.GridBagConstraints constraintsJLabelChangePriority = new java.awt.GridBagConstraints();
			constraintsJLabelChangePriority.gridx = 4; constraintsJLabelChangePriority.gridy = 2;
			constraintsJLabelChangePriority.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelChangePriority.ipadx = -13;
			constraintsJLabelChangePriority.ipady = 6;
			constraintsJLabelChangePriority.insets = new java.awt.Insets(1, 9, 3, 1);
			getJPanelChangeMethod().add(getJLabelChangePriority(), constraintsJLabelChangePriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangePriority = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangePriority.gridx = 5; constraintsJCSpinFieldChangePriority.gridy = 2;
			constraintsJCSpinFieldChangePriority.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldChangePriority.ipadx = 40;
			constraintsJCSpinFieldChangePriority.ipady = 19;
			constraintsJCSpinFieldChangePriority.insets = new java.awt.Insets(1, 1, 3, 25);
			getJPanelChangeMethod().add(getJCSpinFieldChangePriority(), constraintsJCSpinFieldChangePriority);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerNumber.gridx = 1; constraintsJLabelChangeTriggerNumber.gridy = 3;
			constraintsJLabelChangeTriggerNumber.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelChangeTriggerNumber.ipadx = -45;
			constraintsJLabelChangeTriggerNumber.ipady = 6;
			constraintsJLabelChangeTriggerNumber.insets = new java.awt.Insets(4, 8, 8, 1);
			getJPanelChangeMethod().add(getJLabelChangeTriggerNumber(), constraintsJLabelChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerOffset.gridx = 4; constraintsJLabelChangeTriggerOffset.gridy = 3;
			constraintsJLabelChangeTriggerOffset.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelChangeTriggerOffset.ipadx = -63;
			constraintsJLabelChangeTriggerOffset.insets = new java.awt.Insets(8, 9, 10, 11);
			getJPanelChangeMethod().add(getJLabelChangeTriggerOffset(), constraintsJLabelChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJTextFieldChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJTextFieldChangeTriggerOffset.gridx = 5; constraintsJTextFieldChangeTriggerOffset.gridy = 3;
			constraintsJTextFieldChangeTriggerOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldChangeTriggerOffset.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldChangeTriggerOffset.weightx = 1.0;
			constraintsJTextFieldChangeTriggerOffset.ipadx = 37;
			constraintsJTextFieldChangeTriggerOffset.insets = new java.awt.Insets(4, 1, 8, 25);
			getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(), constraintsJTextFieldChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeTriggerNumber.gridx = 2; constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
			constraintsJCSpinFieldChangeTriggerNumber.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldChangeTriggerNumber.ipadx = 54;
			constraintsJCSpinFieldChangeTriggerNumber.ipady = 19;
			constraintsJCSpinFieldChangeTriggerNumber.insets = new java.awt.Insets(4, 1, 8, 1);
			getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(), constraintsJCSpinFieldChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelWhenChange = new java.awt.GridBagConstraints();
			constraintsJLabelWhenChange.gridx = 1; constraintsJLabelWhenChange.gridy = 1;
			constraintsJLabelWhenChange.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelWhenChange.ipadx = 3;
			constraintsJLabelWhenChange.ipady = 4;
			constraintsJLabelWhenChange.insets = new java.awt.Insets(3, 8, 4, 1);
			getJPanelChangeMethod().add(getJLabelWhenChange(), constraintsJLabelWhenChange);

			java.awt.GridBagConstraints constraintsJComboBoxWhenChange = new java.awt.GridBagConstraints();
			constraintsJComboBoxWhenChange.gridx = 2; constraintsJComboBoxWhenChange.gridy = 1;
			constraintsJComboBoxWhenChange.gridwidth = 4;
			constraintsJComboBoxWhenChange.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxWhenChange.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxWhenChange.weightx = 1.0;
			constraintsJComboBoxWhenChange.ipadx = 119;
			constraintsJComboBoxWhenChange.insets = new java.awt.Insets(3, 1, 1, 25);
			getJPanelChangeMethod().add(getJComboBoxWhenChange(), constraintsJComboBoxWhenChange);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelChangeMethod;
}
/**
 * Return the JPanelHolder property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelHolder() {
	if (ivjJPanelHolder == null) {
		try {
			ivjJPanelHolder = new javax.swing.JPanel();
			ivjJPanelHolder.setName("JPanelHolder");
			ivjJPanelHolder.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelShedTime = new java.awt.GridBagConstraints();
			constraintsJLabelShedTime.gridx = 1; constraintsJLabelShedTime.gridy = 1;
			constraintsJLabelShedTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelShedTime.ipadx = 83;
			constraintsJLabelShedTime.insets = new java.awt.Insets(1, 6, 5, 1);
			getJPanelHolder().add(getJLabelShedTime(), constraintsJLabelShedTime);

			java.awt.GridBagConstraints constraintsJComboBoxShedTime = new java.awt.GridBagConstraints();
			constraintsJComboBoxShedTime.gridx = 2; constraintsJComboBoxShedTime.gridy = 1;
			constraintsJComboBoxShedTime.gridwidth = 2;
			constraintsJComboBoxShedTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxShedTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxShedTime.weightx = 1.0;
			constraintsJComboBoxShedTime.ipadx = 4;
			constraintsJComboBoxShedTime.insets = new java.awt.Insets(1, 1, 1, 13);
			getJPanelHolder().add(getJComboBoxShedTime(), constraintsJComboBoxShedTime);

			java.awt.GridBagConstraints constraintsJComboBoxNumGroups = new java.awt.GridBagConstraints();
			constraintsJComboBoxNumGroups.gridx = 2; constraintsJComboBoxNumGroups.gridy = 2;
			constraintsJComboBoxNumGroups.gridwidth = 2;
			constraintsJComboBoxNumGroups.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxNumGroups.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxNumGroups.weightx = 1.0;
			constraintsJComboBoxNumGroups.ipadx = 17;
			constraintsJComboBoxNumGroups.insets = new java.awt.Insets(2, 1, 1, 0);
			getJPanelHolder().add(getJComboBoxNumGroups(), constraintsJComboBoxNumGroups);

			java.awt.GridBagConstraints constraintsJLabelNumGroups = new java.awt.GridBagConstraints();
			constraintsJLabelNumGroups.gridx = 1; constraintsJLabelNumGroups.gridy = 2;
			constraintsJLabelNumGroups.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNumGroups.ipadx = 17;
			constraintsJLabelNumGroups.insets = new java.awt.Insets(6, 6, 4, 1);
			getJPanelHolder().add(getJLabelNumGroups(), constraintsJLabelNumGroups);

			java.awt.GridBagConstraints constraintsJComboBoxGroupSelection = new java.awt.GridBagConstraints();
			constraintsJComboBoxGroupSelection.gridx = 2; constraintsJComboBoxGroupSelection.gridy = 8;
			constraintsJComboBoxGroupSelection.gridwidth = 2;
			constraintsJComboBoxGroupSelection.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxGroupSelection.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxGroupSelection.weightx = 1.0;
			constraintsJComboBoxGroupSelection.ipadx = 4;
			constraintsJComboBoxGroupSelection.insets = new java.awt.Insets(2, 1, 1, 13);
			getJPanelHolder().add(getJComboBoxGroupSelection(), constraintsJComboBoxGroupSelection);

			java.awt.GridBagConstraints constraintsJLabelGroupSelection = new java.awt.GridBagConstraints();
			constraintsJLabelGroupSelection.gridx = 1; constraintsJLabelGroupSelection.gridy = 8;
			constraintsJLabelGroupSelection.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGroupSelection.ipadx = 12;
			constraintsJLabelGroupSelection.insets = new java.awt.Insets(6, 6, 4, 1);
			getJPanelHolder().add(getJLabelGroupSelection(), constraintsJLabelGroupSelection);

			java.awt.GridBagConstraints constraintsJLabelSendRate = new java.awt.GridBagConstraints();
			constraintsJLabelSendRate.gridx = 1; constraintsJLabelSendRate.gridy = 7;
			constraintsJLabelSendRate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelSendRate.ipadx = 8;
			constraintsJLabelSendRate.insets = new java.awt.Insets(5, 6, 4, 1);
			getJPanelHolder().add(getJLabelSendRate(), constraintsJLabelSendRate);

			java.awt.GridBagConstraints constraintsJComboBoxSendRate = new java.awt.GridBagConstraints();
			constraintsJComboBoxSendRate.gridx = 2; constraintsJComboBoxSendRate.gridy = 7;
			constraintsJComboBoxSendRate.gridwidth = 2;
			constraintsJComboBoxSendRate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxSendRate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxSendRate.weightx = 1.0;
			constraintsJComboBoxSendRate.ipadx = 4;
			constraintsJComboBoxSendRate.insets = new java.awt.Insets(1, 1, 1, 13);
			getJPanelHolder().add(getJComboBoxSendRate(), constraintsJComboBoxSendRate);

			java.awt.GridBagConstraints constraintsJLabelControlPercent = new java.awt.GridBagConstraints();
			constraintsJLabelControlPercent.gridx = 1; constraintsJLabelControlPercent.gridy = 3;
			constraintsJLabelControlPercent.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelControlPercent.ipadx = 34;
			constraintsJLabelControlPercent.insets = new java.awt.Insets(5, 6, 4, 1);
			getJPanelHolder().add(getJLabelControlPercent(), constraintsJLabelControlPercent);

			java.awt.GridBagConstraints constraintsJCSpinFieldControlPercent = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldControlPercent.gridx = 2; constraintsJCSpinFieldControlPercent.gridy = 3;
			constraintsJCSpinFieldControlPercent.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldControlPercent.ipadx = 39;
			constraintsJCSpinFieldControlPercent.ipady = 19;
			constraintsJCSpinFieldControlPercent.insets = new java.awt.Insets(2, 1, 1, 14);
			getJPanelHolder().add(getJCSpinFieldControlPercent(), constraintsJCSpinFieldControlPercent);

			java.awt.GridBagConstraints constraintsJCSpinFieldCyclePeriod = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldCyclePeriod.gridx = 4; constraintsJCSpinFieldCyclePeriod.gridy = 3;
			constraintsJCSpinFieldCyclePeriod.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldCyclePeriod.ipadx = 39;
			constraintsJCSpinFieldCyclePeriod.ipady = 19;
			constraintsJCSpinFieldCyclePeriod.insets = new java.awt.Insets(2, 1, 1, 3);
			getJPanelHolder().add(getJCSpinFieldCyclePeriod(), constraintsJCSpinFieldCyclePeriod);

			java.awt.GridBagConstraints constraintsJLabelCyclePeriod = new java.awt.GridBagConstraints();
			constraintsJLabelCyclePeriod.gridx = 3; constraintsJLabelCyclePeriod.gridy = 3;
			constraintsJLabelCyclePeriod.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCyclePeriod.ipadx = -38;
			constraintsJLabelCyclePeriod.insets = new java.awt.Insets(5, 15, 4, 0);
			getJPanelHolder().add(getJLabelCyclePeriod(), constraintsJLabelCyclePeriod);

			java.awt.GridBagConstraints constraintsJLabelMin = new java.awt.GridBagConstraints();
			constraintsJLabelMin.gridx = 5; constraintsJLabelMin.gridy = 3;
			constraintsJLabelMin.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelMin.ipadx = -73;
			constraintsJLabelMin.insets = new java.awt.Insets(5, 0, 4, 8);
			getJPanelHolder().add(getJLabelMin(), constraintsJLabelMin);

			java.awt.GridBagConstraints constraintsJLabelPeriodCount = new java.awt.GridBagConstraints();
			constraintsJLabelPeriodCount.gridx = 1; constraintsJLabelPeriodCount.gridy = 6;
			constraintsJLabelPeriodCount.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPeriodCount.ipadx = 26;
			constraintsJLabelPeriodCount.insets = new java.awt.Insets(5, 6, 3, 1);
			getJPanelHolder().add(getJLabelPeriodCount(), constraintsJLabelPeriodCount);

			java.awt.GridBagConstraints constraintsJComboBoxPeriodCount = new java.awt.GridBagConstraints();
			constraintsJComboBoxPeriodCount.gridx = 2; constraintsJComboBoxPeriodCount.gridy = 6;
			constraintsJComboBoxPeriodCount.gridwidth = 2;
			constraintsJComboBoxPeriodCount.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxPeriodCount.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxPeriodCount.weightx = 1.0;
			constraintsJComboBoxPeriodCount.ipadx = 17;
			constraintsJComboBoxPeriodCount.insets = new java.awt.Insets(1, 1, 0, 0);
			getJPanelHolder().add(getJComboBoxPeriodCount(), constraintsJComboBoxPeriodCount);

			java.awt.GridBagConstraints constraintsJLabelControlStartState = new java.awt.GridBagConstraints();
			constraintsJLabelControlStartState.gridx = 1; constraintsJLabelControlStartState.gridy = 9;
			constraintsJLabelControlStartState.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelControlStartState.ipadx = 25;
			constraintsJLabelControlStartState.insets = new java.awt.Insets(6, 6, 3, 20);
			getJPanelHolder().add(getJLabelControlStartState(), constraintsJLabelControlStartState);

			java.awt.GridBagConstraints constraintsJComboBoxControlStartState = new java.awt.GridBagConstraints();
			constraintsJComboBoxControlStartState.gridx = 2; constraintsJComboBoxControlStartState.gridy = 9;
			constraintsJComboBoxControlStartState.gridwidth = 3;
			constraintsJComboBoxControlStartState.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxControlStartState.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxControlStartState.weightx = 1.0;
			constraintsJComboBoxControlStartState.ipadx = 61;
			constraintsJComboBoxControlStartState.insets = new java.awt.Insets(2, 1, 3, 0);
			getJPanelHolder().add(getJComboBoxControlStartState(), constraintsJComboBoxControlStartState);

			java.awt.GridBagConstraints constraintsJLabelCycleCntSndType = new java.awt.GridBagConstraints();
			constraintsJLabelCycleCntSndType.gridx = 1; constraintsJLabelCycleCntSndType.gridy = 4;
			constraintsJLabelCycleCntSndType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCycleCntSndType.ipadx = 16;
			constraintsJLabelCycleCntSndType.insets = new java.awt.Insets(5, 6, 3, 1);
			getJPanelHolder().add(getJLabelCycleCntSndType(), constraintsJLabelCycleCntSndType);

			java.awt.GridBagConstraints constraintsJComboBoxCycleCountSndType = new java.awt.GridBagConstraints();
			constraintsJComboBoxCycleCountSndType.gridx = 2; constraintsJComboBoxCycleCountSndType.gridy = 4;
			constraintsJComboBoxCycleCountSndType.gridwidth = 2;
			constraintsJComboBoxCycleCountSndType.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxCycleCountSndType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxCycleCountSndType.weightx = 1.0;
			constraintsJComboBoxCycleCountSndType.ipadx = 16;
			constraintsJComboBoxCycleCountSndType.insets = new java.awt.Insets(1, 1, 0, 1);
			getJPanelHolder().add(getJComboBoxCycleCountSndType(), constraintsJComboBoxCycleCountSndType);

			java.awt.GridBagConstraints constraintsJLabelMaxCycleCnt = new java.awt.GridBagConstraints();
			constraintsJLabelMaxCycleCnt.gridx = 1; constraintsJLabelMaxCycleCnt.gridy = 5;
			constraintsJLabelMaxCycleCnt.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelMaxCycleCnt.ipadx = 6;
			constraintsJLabelMaxCycleCnt.insets = new java.awt.Insets(3, 6, 5, 62);
			getJPanelHolder().add(getJLabelMaxCycleCnt(), constraintsJLabelMaxCycleCnt);

			java.awt.GridBagConstraints constraintsJComboBoxMaxCycleCount = new java.awt.GridBagConstraints();
			constraintsJComboBoxMaxCycleCount.gridx = 2; constraintsJComboBoxMaxCycleCount.gridy = 5;
			constraintsJComboBoxMaxCycleCount.gridwidth = 2;
			constraintsJComboBoxMaxCycleCount.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxMaxCycleCount.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxMaxCycleCount.weightx = 1.0;
			constraintsJComboBoxMaxCycleCount.ipadx = 16;
			constraintsJComboBoxMaxCycleCount.insets = new java.awt.Insets(1, 1, 0, 1);
			getJPanelHolder().add(getJComboBoxMaxCycleCount(), constraintsJComboBoxMaxCycleCount);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHolder;
}
/**
 * Return the JTextFieldChangeTriggerOffset property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldChangeTriggerOffset() {
	if (ivjJTextFieldChangeTriggerOffset == null) {
		try {
			ivjJTextFieldChangeTriggerOffset = new javax.swing.JTextField();
			ivjJTextFieldChangeTriggerOffset.setName("JTextFieldChangeTriggerOffset");
			ivjJTextFieldChangeTriggerOffset.setPreferredSize(new java.awt.Dimension(40, 20));
			ivjJTextFieldChangeTriggerOffset.setMaximumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}

			ivjJTextFieldChangeTriggerOffset.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-99999.9999, 99999.9999, 4) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldChangeTriggerOffset;
}
/**
 * Return the JTextFieldGearName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldGearName() {
	if (ivjJTextFieldGearName == null) {
		try {
			ivjJTextFieldGearName = new javax.swing.JTextField();
			ivjJTextFieldGearName.setName("JTextFieldGearName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldGearName;
}
/**
 * Insert the method's description here.
 * Creation date: (6/18/2001 5:46:52 PM)
 * @return java.util.Hashtable
 */
private java.util.Hashtable getPAOHashTable() 
{
	//store the references to our cached PAOs and points in this class
	if( paoHashTable == null )
		paoHashTable = com.cannontech.database.cache.functions.PAOFuncs.getAllLitePAOWithPoints();

	return paoHashTable;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMProgramDirectGear gear = null;
	
	if( o == null )
		gear = LMProgramDirectGear.createGearFactory( getGearType() );
	else
	{		
		gear = (LMProgramDirectGear)o;
	}

	
	gear.setGearName( getJTextFieldGearName().getText() );
	gear.setControlMethod( getGearType() );

	if( getJComboBoxHowToStop().getSelectedItem() != null )
	{
		gear.setMethodStopType( 
			com.cannontech.common.util.StringUtils.removeChars( ' ', getJComboBoxHowToStop().getSelectedItem().toString() ) );
	}

	gear.setPercentReduction( new Integer( ((Number)getJCSpinFieldPercentReduction().getValue()).intValue() ) );
	
	gear.setChangeCondition( getChangeCondition(getJComboBoxWhenChange().getSelectedItem().toString()) );

	gear.setChangeDuration( new Integer( ((Number)getJCSpinFieldChangeDuration().getValue()).intValue() * 60 ) );
	gear.setChangePriority( new Integer( ((Number)getJCSpinFieldChangePriority().getValue()).intValue() ) );
	gear.setChangeTriggerNumber( new Integer( ((Number)getJCSpinFieldChangeTriggerNumber().getValue()).intValue() ) );
	
	if( getJTextFieldChangeTriggerOffset().getText() == null || getJTextFieldChangeTriggerOffset().getText().length() <= 0 )
		gear.setChangeTriggerOffset( new Double(0.0) );
	else
		gear.setChangeTriggerOffset( Double.valueOf(getJTextFieldChangeTriggerOffset().getText()) );

	

	if( gear instanceof com.cannontech.database.data.device.lm.SmartCycleGear )
	{
		com.cannontech.database.data.device.lm.SmartCycleGear s = (com.cannontech.database.data.device.lm.SmartCycleGear)gear;

		s.setControlPercent( new Integer( 
			((Number)getJCSpinFieldControlPercent().getValue()).intValue() ) );

		s.setCyclePeriodLength( new Integer( 
			((Number)getJCSpinFieldCyclePeriod().getValue()).intValue() * 60 ) );

		s.setStartingPeriodCnt( (Integer)getJComboBoxPeriodCount().getSelectedItem() );

		s.setResendRate( com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue( getJComboBoxSendRate() ) );

		if( getJComboBoxMaxCycleCount().getSelectedItem() == null
			 || getJComboBoxMaxCycleCount().getSelectedItem() instanceof String )
		{
			s.setMethodOptionMax( new Integer(0) );
		}
		else
			s.setMethodOptionMax( (Integer)getJComboBoxMaxCycleCount().getSelectedItem() );

		s.setMethodOptionType( StringUtils.removeChars( ' ', getJComboBoxCycleCountSndType().getSelectedItem().toString() ) );
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.MasterCycleGear )
	{
		com.cannontech.database.data.device.lm.MasterCycleGear s = (com.cannontech.database.data.device.lm.MasterCycleGear)gear;

		s.setControlPercent( new Integer( 
			((Number)getJCSpinFieldControlPercent().getValue()).intValue() ) );

		s.setCyclePeriodLength( new Integer( 
			((Number)getJCSpinFieldCyclePeriod().getValue()).intValue() * 60 ) );		
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.TimeRefreshGear )
	{
		com.cannontech.database.data.device.lm.TimeRefreshGear t = (com.cannontech.database.data.device.lm.TimeRefreshGear)gear;

		t.setShedTime( com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue( getJComboBoxShedTime() ) );

		t.setNumberOfGroups( getJComboBoxNumGroups().getSelectedItem() );

		t.setRefreshRate( com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue( getJComboBoxSendRate() ) );
		
		t.setGroupSelectionMethod( StringUtils.removeChars( ' ', getJComboBoxGroupSelection().getSelectedItem().toString() ) );
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.RotationGear )
	{
		com.cannontech.database.data.device.lm.RotationGear r = (com.cannontech.database.data.device.lm.RotationGear)gear;

		r.setShedTime( com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue( getJComboBoxShedTime() ) );

		r.setNumberOfGroups( getJComboBoxNumGroups().getSelectedItem() );

		r.setSendRate( com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue( getJComboBoxSendRate() ) );
		
		r.setGroupSelectionMethod( StringUtils.removeChars( ' ', getJComboBoxGroupSelection().getSelectedItem().toString() ) );
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.LatchingGear )
	{
		com.cannontech.database.data.device.lm.LatchingGear l = (com.cannontech.database.data.device.lm.LatchingGear)gear;

		l.setStartControlState( new Integer(getJComboBoxControlStartState().getSelectedIndex()) );
	}
	
	return gear;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getJCSpinFieldChangeDuration().addValueListener(this);
	getJCSpinFieldChangePriority().addValueListener(this);
	getJCSpinFieldChangeTriggerNumber().addValueListener(this);
	getJCSpinFieldControlPercent().addValueListener(this);
	getJCSpinFieldCyclePeriod().addValueListener(this);
	getJCSpinFieldPercentReduction().addValueListener(this);

	// user code end
	getJComboBoxWhenChange().addActionListener(this);
	getJComboBoxGearType().addActionListener(this);
	getJTextFieldGearName().addCaretListener(this);
	getJComboBoxShedTime().addActionListener(this);
	getJComboBoxNumGroups().addActionListener(this);
	getJComboBoxPeriodCount().addActionListener(this);
	getJComboBoxSendRate().addActionListener(this);
	getJComboBoxGroupSelection().addActionListener(this);
	getJComboBoxHowToStop().addActionListener(this);
	getJComboBoxControlStartState().addActionListener(this);
	getJComboBoxCycleCountSndType().addActionListener(this);
	getJComboBoxMaxCycleCount().addActionListener(this);
	getJTextFieldChangeTriggerOffset().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DirectGearPanel");
		setToolTipText("");
		setPreferredSize(new java.awt.Dimension(303, 194));
		setLayout(new java.awt.GridBagLayout());
		setSize(391, 452);
		setMinimumSize(new java.awt.Dimension(10, 10));

		java.awt.GridBagConstraints constraintsJLabelPercentReduction = new java.awt.GridBagConstraints();
		constraintsJLabelPercentReduction.gridx = 1; constraintsJLabelPercentReduction.gridy = 5;
		constraintsJLabelPercentReduction.gridwidth = 2;
		constraintsJLabelPercentReduction.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelPercentReduction.ipadx = 53;
		constraintsJLabelPercentReduction.insets = new java.awt.Insets(4, 7, 6, 0);
		add(getJLabelPercentReduction(), constraintsJLabelPercentReduction);

		java.awt.GridBagConstraints constraintsJCSpinFieldPercentReduction = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldPercentReduction.gridx = 3; constraintsJCSpinFieldPercentReduction.gridy = 5;
		constraintsJCSpinFieldPercentReduction.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCSpinFieldPercentReduction.ipadx = 39;
		constraintsJCSpinFieldPercentReduction.ipady = 19;
		constraintsJCSpinFieldPercentReduction.insets = new java.awt.Insets(1, 0, 3, 179);
		add(getJCSpinFieldPercentReduction(), constraintsJCSpinFieldPercentReduction);

		java.awt.GridBagConstraints constraintsJLabelHowToStop = new java.awt.GridBagConstraints();
		constraintsJLabelHowToStop.gridx = 1; constraintsJLabelHowToStop.gridy = 4;
		constraintsJLabelHowToStop.gridwidth = 2;
		constraintsJLabelHowToStop.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelHowToStop.ipadx = 4;
		constraintsJLabelHowToStop.insets = new java.awt.Insets(5, 7, 4, 49);
		add(getJLabelHowToStop(), constraintsJLabelHowToStop);

		java.awt.GridBagConstraints constraintsJComboBoxHowToStop = new java.awt.GridBagConstraints();
		constraintsJComboBoxHowToStop.gridx = 3; constraintsJComboBoxHowToStop.gridy = 4;
		constraintsJComboBoxHowToStop.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxHowToStop.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxHowToStop.weightx = 1.0;
		constraintsJComboBoxHowToStop.ipadx = 46;
		constraintsJComboBoxHowToStop.insets = new java.awt.Insets(2, 0, 0, 47);
		add(getJComboBoxHowToStop(), constraintsJComboBoxHowToStop);

		java.awt.GridBagConstraints constraintsJPanelHolder = new java.awt.GridBagConstraints();
		constraintsJPanelHolder.gridx = 1; constraintsJPanelHolder.gridy = 3;
		constraintsJPanelHolder.gridwidth = 3;
		constraintsJPanelHolder.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHolder.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelHolder.weightx = 1.0;
		constraintsJPanelHolder.weighty = 1.0;
		constraintsJPanelHolder.ipady = 1;
		constraintsJPanelHolder.insets = new java.awt.Insets(0, 1, 1, 2);
		add(getJPanelHolder(), constraintsJPanelHolder);

		java.awt.GridBagConstraints constraintsJLabelGearName = new java.awt.GridBagConstraints();
		constraintsJLabelGearName.gridx = 1; constraintsJLabelGearName.gridy = 1;
		constraintsJLabelGearName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelGearName.ipadx = 9;
		constraintsJLabelGearName.insets = new java.awt.Insets(3, 6, 3, 0);
		add(getJLabelGearName(), constraintsJLabelGearName);

		java.awt.GridBagConstraints constraintsJTextFieldGearName = new java.awt.GridBagConstraints();
		constraintsJTextFieldGearName.gridx = 2; constraintsJTextFieldGearName.gridy = 1;
		constraintsJTextFieldGearName.gridwidth = 2;
		constraintsJTextFieldGearName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldGearName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldGearName.weightx = 1.0;
		constraintsJTextFieldGearName.ipadx = 210;
		constraintsJTextFieldGearName.insets = new java.awt.Insets(1, 0, 1, 95);
		add(getJTextFieldGearName(), constraintsJTextFieldGearName);

		java.awt.GridBagConstraints constraintsJPanelChangeMethod = new java.awt.GridBagConstraints();
		constraintsJPanelChangeMethod.gridx = 1; constraintsJPanelChangeMethod.gridy = 6;
		constraintsJPanelChangeMethod.gridwidth = 3;
		constraintsJPanelChangeMethod.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelChangeMethod.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelChangeMethod.weightx = 1.0;
		constraintsJPanelChangeMethod.weighty = 1.0;
		constraintsJPanelChangeMethod.ipadx = -5;
		constraintsJPanelChangeMethod.ipady = -11;
		constraintsJPanelChangeMethod.insets = new java.awt.Insets(3, 4, 5, 4);
		add(getJPanelChangeMethod(), constraintsJPanelChangeMethod);

		java.awt.GridBagConstraints constraintsJLabelGearType = new java.awt.GridBagConstraints();
		constraintsJLabelGearType.gridx = 1; constraintsJLabelGearType.gridy = 2;
		constraintsJLabelGearType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelGearType.ipadx = 17;
		constraintsJLabelGearType.insets = new java.awt.Insets(5, 6, 4, 0);
		add(getJLabelGearType(), constraintsJLabelGearType);

		java.awt.GridBagConstraints constraintsJComboBoxGearType = new java.awt.GridBagConstraints();
		constraintsJComboBoxGearType.gridx = 2; constraintsJComboBoxGearType.gridy = 2;
		constraintsJComboBoxGearType.gridwidth = 2;
		constraintsJComboBoxGearType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxGearType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxGearType.weightx = 1.0;
		constraintsJComboBoxGearType.ipadx = 88;
		constraintsJComboBoxGearType.insets = new java.awt.Insets(2, 0, 0, 95);
		add(getJComboBoxGearType(), constraintsJComboBoxGearType);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	getJComboBoxGearType().setSelectedItem( LMProgramDirectGear.CONTROL_TIME_REFRESH );
	getJComboBoxWhenChange().setSelectedItem( LMProgramDirectGear.CHANGE_NONE );

	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldGearName().getText() == null
		 || getJTextFieldGearName().getText().length() <= 0 )
	{
		setErrorString("A name for this gear must be specified");
		return false;
	}
	
	return true;
}
/**
 * Comment
 */
public void jComboBoxGearType_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( getJComboBoxGearType().getSelectedItem() != null )
	{
		setGearType( getJComboBoxGearType().getSelectedItem().toString() );

		fireInputUpdate();
	}
	
	return;
}
/**
 * Comment
 */
public void jComboBoxWhenChange_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJLabelChangeDuration().setVisible(false);
	getJCSpinFieldChangeDuration().setVisible(false);
	getJLabelMinutesChDur().setVisible(false);
	
	getJLabelChangePriority().setVisible(false);
	getJCSpinFieldChangePriority().setVisible(false);
	
	getJLabelChangeTriggerNumber().setVisible(false);
	getJCSpinFieldChangeTriggerNumber().setVisible(false);

	getJLabelChangeTriggerOffset().setVisible(false);
	getJTextFieldChangeTriggerOffset().setVisible(false);

	
	if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_NONE )
		 || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "Manually Only" ) )
	{
		//None
		return;
	}
	else if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_DURATION )
				 || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "After a Duration" ) )
	{
		//Duration
		getJLabelChangeDuration().setVisible(true);
		getJCSpinFieldChangeDuration().setVisible(true);
		getJLabelMinutesChDur().setVisible(true);
	}
	else if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_PRIORITY )
				 || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "Priority Change" ) )
	{
		//Priority
		getJLabelChangePriority().setVisible(true);
		getJCSpinFieldChangePriority().setVisible(true);
	}
	else if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_TRIGGER_OFFSET )
				 || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "Above Trigger" ) )
	{
		//TriggerOffset
		getJLabelChangeTriggerNumber().setVisible(true);
		getJCSpinFieldChangeTriggerNumber().setVisible(true);

		getJLabelChangeTriggerOffset().setVisible(true);
		getJTextFieldChangeTriggerOffset().setVisible(true);
	}
	else
		throw new Error("Unknown LMProgramDirectGear control condition found, the value = " + getJComboBoxWhenChange().getSelectedItem().toString() );


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
		DirectModifyGearPanel aDirectModifyGearPanel;
		aDirectModifyGearPanel = new DirectModifyGearPanel();
		frame.setContentPane(aDirectModifyGearPanel);
		frame.setSize(aDirectModifyGearPanel.getSize());
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
 * Insert the method's description here.
 * Creation date: (2/12/2002 12:36:14 PM)
 * @param change java.lang.String
 */
private void setChangeCondition(String change) 
{
	if( change == null )
		return;

	if( change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_NONE) )
	{
		getJComboBoxWhenChange().setSelectedItem("Manually Only");
	}
	else if( change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_DURATION) )
	{
		getJComboBoxWhenChange().setSelectedItem("After a Duration");
	}
	else if( change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_PRIORITY) )
	{
		getJComboBoxWhenChange().setSelectedItem("Priority Change");
	}
	else if( change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_TRIGGER_OFFSET) )
	{
		getJComboBoxWhenChange().setSelectedItem("Above Trigger");
	}	
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 5:37:00 PM)
 * @param newGearType java.lang.String
 */
public void setGearType(java.lang.String newGearType) 
{
	gearType = StringUtils.removeChars( ' ', newGearType );

	if( getGearType() == null )
		return;

	getJComboBoxHowToStop().setVisible(true);
	getJLabelHowToStop().setVisible(true);
	getJPanelChangeMethod().setVisible(true);
	getJLabelControlStartState().setVisible(false);
	getJComboBoxControlStartState().setVisible(false);
	
	getJLabelCycleCntSndType().setVisible(false);
	getJComboBoxCycleCountSndType().setVisible(false);
	getJLabelMaxCycleCnt().setVisible(false);
	getJComboBoxMaxCycleCount().setVisible(false);

	getJComboBoxHowToStop().removeAllItems();
	getJComboBoxHowToStop().addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_TIME_IN ) );	
	getJComboBoxHowToStop().addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_RESTORE ) );
	getJComboBoxHowToStop().setSelectedItem( StringUtils.removeChars( ' ', LMProgramDirectGear.STOP_TIME_IN ) );
	
	if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_LATCHING) )
	{
		//Latching
		getJLabelShedTime().setVisible(false);
		getJComboBoxShedTime().setVisible(false);
		getJLabelNumGroups().setVisible(false);
		getJComboBoxNumGroups().setVisible(false);
		getJLabelSendRate().setVisible(false);
		getJComboBoxSendRate().setVisible(false);
		getJLabelGroupSelection().setVisible(false);
		getJComboBoxGroupSelection().setVisible(false);
		getJLabelPeriodCount().setVisible(false);
		getJComboBoxPeriodCount().setVisible(false);
		getJLabelControlPercent().setVisible(false);
		getJCSpinFieldControlPercent().setVisible(false);
		getJLabelCyclePeriod().setVisible(false);
		getJCSpinFieldCyclePeriod().setVisible(false);
		getJLabelMin().setVisible(false);
		getJComboBoxHowToStop().setVisible(false);
		getJLabelHowToStop().setVisible(false);
		getJPanelChangeMethod().setVisible(false);

		getJLabelControlStartState().setVisible(true);
		getJComboBoxControlStartState().setVisible(true);
	}
	else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_MASTER_CYCLE) )
	{
		//MasterCycle
		getJLabelShedTime().setVisible(false);
		getJComboBoxShedTime().setVisible(false);
		getJLabelNumGroups().setVisible(false);
		getJComboBoxNumGroups().setVisible(false);
		getJLabelPeriodCount().setVisible(false);
		getJComboBoxPeriodCount().setVisible(false);
		getJLabelSendRate().setVisible(false);
		getJComboBoxSendRate().setVisible(false);
		getJLabelGroupSelection().setVisible(false);
		getJComboBoxGroupSelection().setVisible(false);


		getJLabelControlPercent().setVisible(true);
		getJCSpinFieldControlPercent().setVisible(true);
		getJLabelCyclePeriod().setVisible(true);
		getJCSpinFieldCyclePeriod().setVisible(true);
		getJLabelMin().setVisible(true);		
	}
	else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_ROTATION) )
	{
		//Rotation
		getJLabelPeriodCount().setVisible(false);
		getJComboBoxPeriodCount().setVisible(false);
		getJLabelControlPercent().setVisible(false);
		getJCSpinFieldControlPercent().setVisible(false);
		getJLabelCyclePeriod().setVisible(false);
		getJCSpinFieldCyclePeriod().setVisible(false);
		getJLabelMin().setVisible(false);

		getJLabelShedTime().setVisible(true);
		getJComboBoxShedTime().setVisible(true);
		getJLabelNumGroups().setVisible(true);
		getJComboBoxNumGroups().setVisible(true);
		getJLabelSendRate().setVisible(true);
		getJLabelSendRate().setText("Send Rate:");
		getJComboBoxSendRate().setVisible(true);
		getJLabelGroupSelection().setVisible(true);
		getJComboBoxGroupSelection().setVisible(true);

		getJComboBoxShedTime().setSelectedItem("1 hour");
		getJComboBoxNumGroups().setSelectedItem(new Integer(1));
		getJComboBoxSendRate().setSelectedItem("10 minutes");
	}
	else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_SMART_CYCLE) )
	{
		//SmartCycle
		getJLabelShedTime().setVisible(false);
		getJComboBoxShedTime().setVisible(false);
		getJLabelNumGroups().setVisible(false);
		getJComboBoxNumGroups().setVisible(false);
		getJLabelGroupSelection().setVisible(false);
		getJComboBoxGroupSelection().setVisible(false);

		getJLabelPeriodCount().setVisible(true);
		getJComboBoxPeriodCount().setVisible(true);
		getJLabelSendRate().setVisible(true);		
		getJComboBoxSendRate().setVisible(true);
		getJLabelSendRate().setText("Command Resend Rate:");		
		getJLabelControlPercent().setVisible(true);
		getJCSpinFieldControlPercent().setVisible(true);
		getJLabelCyclePeriod().setVisible(true);
		getJCSpinFieldCyclePeriod().setVisible(true);
		getJLabelMin().setVisible(true);
		getJLabelCycleCntSndType().setVisible(true);
		getJComboBoxCycleCountSndType().setVisible(true);
		getJLabelMaxCycleCnt().setVisible(true);
		getJComboBoxMaxCycleCount().setVisible(true);

		getJComboBoxSendRate().setSelectedItem("1 hour");
		getJComboBoxHowToStop().removeItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_TIME_IN ) );		
		getJComboBoxHowToStop().addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_STOP_CYCLE ) );		
		getJComboBoxHowToStop().setSelectedItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_STOP_CYCLE ) );
	}
	else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_TIME_REFRESH) )
	{
		//TimeRefresh
		getJLabelPeriodCount().setVisible(false);
		getJComboBoxPeriodCount().setVisible(false);
		getJLabelControlPercent().setVisible(false);
		getJCSpinFieldControlPercent().setVisible(false);
		getJLabelCyclePeriod().setVisible(false);
		getJCSpinFieldCyclePeriod().setVisible(false);
		getJLabelMin().setVisible(false);

		getJLabelShedTime().setVisible(true);
		getJComboBoxShedTime().setVisible(true);
		getJLabelNumGroups().setVisible(true);
		getJComboBoxNumGroups().setVisible(true);
		getJLabelSendRate().setVisible(true);
		getJLabelSendRate().setText("Refresh/Resend Rate:");
		getJComboBoxSendRate().setVisible(true);
		getJLabelGroupSelection().setVisible(true);
		getJComboBoxGroupSelection().setVisible(true);

		getJComboBoxShedTime().setSelectedItem("1 hour");
		getJComboBoxSendRate().setSelectedItem("30 minutes");
	}
	else
		throw new Error("Unknown LMProgramDirectGear " +
			"type found, the value = " + getGearType() );

	return;
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	LMProgramDirectGear gear = null;
	
	if( o == null )
		return;
	else
		gear = (LMProgramDirectGear)o;


	getJComboBoxGearType().setSelectedItem( StringUtils.addCharBetweenWords( ' ', gear.getControlMethod() ) );

	getJTextFieldGearName().setText( gear.getGearName() );

	getJComboBoxHowToStop().setSelectedItem( StringUtils.addCharBetweenWords( ' ', gear.getMethodStopType() ) );

	getJCSpinFieldPercentReduction().setValue( gear.getPercentReduction() );
	
	setChangeCondition( gear.getChangeCondition() );
	getJCSpinFieldChangeDuration().setValue( 	new Integer( gear.getChangeDuration().intValue() / 60 ) );
	getJCSpinFieldChangePriority().setValue( gear.getChangePriority() );
	getJCSpinFieldChangeTriggerNumber().setValue( gear.getChangeTriggerNumber() );	
	getJTextFieldChangeTriggerOffset().setText( gear.getChangeTriggerOffset().toString() );

	if( gear instanceof com.cannontech.database.data.device.lm.SmartCycleGear )
	{
		com.cannontech.database.data.device.lm.SmartCycleGear s = (com.cannontech.database.data.device.lm.SmartCycleGear)gear;

		getJCSpinFieldControlPercent().setValue( s.getControlPercent() );

		getJCSpinFieldCyclePeriod().setValue( new Integer( s.getCyclePeriodLength().intValue() / 60 ) );

		getJComboBoxPeriodCount().setSelectedItem( s.getStartingPeriodCnt() );
			
		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( getJComboBoxSendRate(), s.getResendRate().intValue() );

		if( s.getMethodOptionMax().intValue() > 0 )
			getJComboBoxMaxCycleCount().setSelectedItem( s.getMethodOptionMax() );
		else
			getJComboBoxMaxCycleCount().setSelectedItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
		
		getJComboBoxCycleCountSndType().setSelectedItem( StringUtils.addCharBetweenWords( ' ', s.getMethodOptionType() ) );
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.MasterCycleGear )
	{
		com.cannontech.database.data.device.lm.MasterCycleGear s = (com.cannontech.database.data.device.lm.MasterCycleGear)gear;

		getJCSpinFieldControlPercent().setValue( s.getControlPercent() );

		getJCSpinFieldCyclePeriod().setValue( new Integer( s.getCyclePeriodLength().intValue() / 60 ) );
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.TimeRefreshGear )
	{
		com.cannontech.database.data.device.lm.TimeRefreshGear t = (com.cannontech.database.data.device.lm.TimeRefreshGear)gear;

		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
			getJComboBoxShedTime(), t.getShedTime().intValue() );

		getJComboBoxNumGroups().setSelectedItem( t.getNumberOfGroups() );

		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
			getJComboBoxSendRate(), t.getRefreshRate().intValue() );
		
		getJComboBoxGroupSelection().setSelectedItem( StringUtils.addCharBetweenWords( ' ', t.getGroupSelectionMethod() ) );
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.RotationGear )
	{
		com.cannontech.database.data.device.lm.RotationGear r = (com.cannontech.database.data.device.lm.RotationGear)gear;

		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( getJComboBoxShedTime(), r.getShedTime().intValue() );

		getJComboBoxNumGroups().setSelectedItem( r.getNumberOfGroups() );

		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( getJComboBoxSendRate(), r.getSendRate().intValue() );
		
		getJComboBoxGroupSelection().setSelectedItem( StringUtils.addCharBetweenWords( ' ', r.getGroupSelectionMethod() ) );
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.LatchingGear )
	{
		com.cannontech.database.data.device.lm.LatchingGear l = (com.cannontech.database.data.device.lm.LatchingGear)gear;

		getJComboBoxControlStartState().setSelectedIndex( l.getStartControlState().intValue() );
	}
	
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
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
	D0CB838494G88G88G6EF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DD8BDCD4D57EA7432216ADAB5AE54B0A0A0C128A13F659E20BA55D3428E50B4AADABAA5A28350DB6F733562D4BEB00FC2122E0D9CD2905C5489BC2ACD0B4B0ACCAD45AE8E3E4AEB0BA4CCCB3979988657FBB4F7B18BBB340EE7F0F6B6773734E797E1E67F94FBD675C0BD070D6D8F6FAC601A024E78B74DFEDBA7CB7DD90B2DE7972A10685087186C1716FED2055C2D2CB96DC2781AFC2385510AF9CFD
	AD975261106EFEAA5ED0883F578A0E5722CE059FDE7C8481EF750BD7E77976B37A8373130353DF37AF076BCA20884050F5B1DC73056F5B0BA81EC0F1C669990210520BE4025BB7D22C8E526DC0E1C0513DC4BE812EF95873547ABAAA57F5CE1830787677B2D6A0BB8A1B0CCA7A08EEBB3D9A056B521774A1DFB760B811CE90246300D066B305BF2F5C8957067623FD9F775887CAFBCCB61B5DA609BD7D25C316911353DCEA960FD9FA44D26B0043E96FF31A866A6B1BADCE31C75AE7B7DBFA07BF95CD4EF613CD34
	8A02540F7C7C33BD975B851B1096AC884B2EC43EE3A01D88144A71840CB7436FAEA04942723B633D861A1F4E9F2E93165EEF4BDC65A53F4196124FE80B1C5F104117F2B6603A099DC5F510863C02A37106E220B6A0ABD0B0506B38CD5C7A437BF0EDBAAC6D328F8C58EDCD8E47CE2B598919932D703B3E9EB8947BCA6C31BB4D02C06A7D2676921DF8FC91511BBFE1BDD2A691152C8DC4FEB5C3D8B87B330C9C1DB2D998D11691FE0414C56C9139CC086F0CAF3D79DEF8C45DC6553E8F89CA5F0BCE33E7E66B780E
	3EF7EBC69A3513F5D476DDF5945575B02E6B4D70BB1D368F021F0AF13782A76DF59946D1BA88F80B0E3236513912455216F50110DF5E26DB8E2171CD996DD692C33FD59BCBE895329DE2A5BEDF6731DC0B71A485CEE2399B47025285837106B4A0523E661EC07DB74186ED9AC830479B0A006A00BA007A6D2C0DAD9F5DB64EB656EEF2CABB87ADD6336894086E5C5175F0955D125354EAF3C9A6DB0FF8506434188EDBC517C0E56E9AC5E5F0D825F78FEE7BBD6063D351A6BACD1245EE836B2681D1921DAEA233F0
	F4B512F10A185BE19990414100C3E0F6D3C6574255EAF2C9879CE613A44A92043FE8945549B153C130888660B7A9173F0E21728A027C0781C571F268BB0E6CFDA6BAE13C282FEF335B0F8EBABAD0CE8441014AF94317E86C1883728B6D2C0C7FA2A02C9978DD8EE6676263EB94F61AADBDA8FC13F318753131CAB4C6BB9C2C4D7C251335196695D788A5776E55EDB3F8BCF11036926E50761DB330CD9497C92F2E6C1CE0BF9E8FB928DF62637D9F45C7A63D12726D4AC0F515GFED301F23FE7E57C4763C652E6F7
	1ADCA2EB25DFB8210109E63944C91C97D7D55172C43633E97C286C178121FB55AAEC93498A42BA20C26A1F68C7E3FDA378EE81F2G05824581258265BB21CF81F5838581C581ADG92DC2CAE96D4A60E437FEA9A03527FFF3BEC7F1BC472362AAAF19C79DAC7734672757AC9903FAFA2ED6C77A7D07C62B151A6B50B8E51E6069FAE4A3B6F844AA79D77FA6DDCE7A1469DB0CE6E020149457147CE20F2B0C91253F2F8D092770B12E43175497C2731AF5760E107BC44B2DF4F1DC0E5EB9F02F1124B7F7D849AE3FA
	6CB61B089BBBD7217CA5F80C932D62G843D4BBEE813887E72989AA70E0A43F40C4D9050A4721849BA0846EBB2670AB2217AF3B8AD8350055010AF843B5078D776E511220C0A81C3F347EE38CAAEB6168DEDE2E3596611CB05386F36E4685D0BC32FE84EC8F21131A455A50FE5A42667857D58DF44E3AD45DD1998BB7E9DAAB7B26513A7FBE46CB8BF93594915628D9B012AA4B6F68C26A3BE9B1F6920314BEE35122264E5FCB8A39F5509787D208837B5F4A3780C171BA5E30D06F71016A161FF1F014A502A3807
	948C22B2FC2E0A4D295BA09EB3D020C44AF10EC44A01485DD04D643AA16D9E1465C28715F2169AA617826925C09B0F91394EE3CA3912DAA66786FC9AD0142EDC9A17CB83BCF7481BDCF89D13AB8619C62FF21D9F71FC000C5B2B5C72FAA6970AE43C4A45F139C410C975AAA7EDE7F2396EF803516DCD2E380149B502CCA7105B2DA737084B45GBF512BDC588EDEFE404F9D06F97D30DA0E74311C6F50DCD76F3926EF50D2BAA8D9504C41A44160629814F023A64FAF9760B988EADB66E1761CB2F388357369100E
	C63E06D9DF7C438A761C92F2B4CCC83E7CB52F4FE5A943A4365C9139AF9ACFE54FA90FE4A11FAD406B9CE1FDE8D106F22E5464F259FBACF81AC65B7F05194A3ECA30304CD7615AEBF13A24D698CC5D2C2F4C48C47122F99C05693C76356A65343C2C60B79C286187047F2222C0917BC24033GAF86A2E342A95595E82C451D3914DC5AC5E72F5DB9A01A0B0F180E19CACDC3D2A99E57CB1BB0FBB77A3D15554D1AAE744CF984E643076DBB6D6EC37D22EDD73F4956A7FE5724B156665D5A762D87597349E5EB9BE9
	2C8957DEA0B40C2D4D50FBDE8CDA75C6C6608F247CA3FF106BE174FAC36622FF7FA3071449433CCC1600CC59A8AB13591236CC50DC30FF14BDC76F44B170BC212132E358B1519C11984E2DE1B10C027DE89EC35EB1E58C0BC6C98C8547D93D3CD8031EB3FACCCED1AA457F13E9BB2E92F729EB88EE08546BAE442515C2621F33F58DE9BBA26E91D49315B10969C0AD0B29987CF59FE7B15554EB4BA594F831BC26585A49369562F7C39D731B88B6F3CFB03F69A3FAFE9B01CF746E64FA9D0085F33D07BD6A91694D
	61FA27FC447496G16BF46745EA95253EB99E3FA5575CC2F8B30902E775CA1BD3D842E779457CB862CA800695DDD29575E5A83185EC55B191E993069DC6FA68FBD64AF096BFD594074D281B31E427412867574BACFE1759853701F55633A9DFC2C811B615CEFDB151E5F052730F867F33DB4400A27B0BD2B246D8BE80C6A1E4262AD7BF8B2FD014C39AF416BA492588A1AC2460969D3647511A878BDE70AFA3D042C9F6CF723D897CCA16B876BB85E0B7165274AB8495BACFCDFC169C660ADBA15BD738E9B50BCB0
	B4907ABED082D08AD05654F84366296C7ED01AD5A978D9D3E8057B1D53E6326ED7CDFA5933FD96AAA7182752F4EA96AAE3BCCF9558330731DA3BDE54B8D51D6FAECD1A6815FA683D29C987E874489854E7C53160FB36221A6A6B7732D1154E136F2DD44E13C327123E918B57C4A0F29FCF2CE06B1029D3597D7A789E657DBAF6D5851EC36A5D2F0D2711789ACF136F93A5FCDDF1D3AE1A0384GEFBAD0B4D0A250E2207C53E19EFCBA2B236359BB7C5591EEF79D76DD003540DCF61776F90317E94AA46B622FFE6B
	2263F4359EA93F27A5D6FE565359F3F6E84D64E2186121D72AC9071F2E579E3694EF54EF8F9D70DC3847A2DA4DA45646E3AC56D89E6BB735130B556221270DF531A6D652A6EF9E54EB134B03E00C8DE2EDF2201E45D9974462FC2BEEF2F1FE6821270D539124D72FF5A3AC06A09E43BD130C61D13FB144694630EE1045300047F07AC7130B61FC8FBDED8C79E7684570F5910B21788C9643767A4945506A21270D21DBB70662CE96031B47706CA4E378275F9822F4E3482BE2B14C63B1DC3EFDF2B14C72505346A0
	1C299743DFABD98CC5E732988EB5CCAE8651C3CF9BC33BEE8C4B7898524FE3F8FD4764E2586A21270DA1CCB706D3F9DD447098FEBF49989E729BC31AEE8C1763F19F59487D89BC7F62F579DF62E762C6C8F7F26C0D2F90E606F4000C1DC0D88824A3B8F666984292A01D42B1D3BA42F203E1EC8C667B286EE238D6C13A93A8GE8BAD0ACD092D09AD07ECF61F98C286D27CC470AF52C108E860A7CA9D92F40736B1F12359AB4A7DC74D3323EC3F4FEB59426A987C4481796E3142461D57910C5946B070F62C779C1
	F9892079D57273F97E9E530DB54F076D951AF4268FD9C44B7C70515C142D5D231832B5FC3466CA66BAD38C5E57F24FB0682F651EED50DF4BBD5F301A60E0C339A346789799FC2F75DEE150DB6BFD8317477C752B30ED17CBF4614997DA84223CA7A514DF0FB55F4FCD83A2AD4B3542216ABCAA33DB326FAA9758DC65792D687E2FB3D7D12CAD2D407D9B69AFBF0B2DAD3D1D0E7740A15D85A41D456A7E5E8ADF7B4441E7F936B1B2B73DBA8FBD970581BF96A889E891507233591CF4FB0E5F67065DEE9F4F8D9F66
	FA2F672ADC7DFA2E4FC56D52E59FF476085A3AEC4AAD62BCF9765BAA09832858094CDE2CAF191CFD22C7DB78926B931EB77DFF65FAEBAF24CEDF2FD156E905404650E7A5DFF5D0F236E79D90FB3FD7592B6476F69F73E52F512B3DF3EB157650DE99E9CFFF4773A83C46F1B6EBCFB761BD6CE0C80783451E4D5A1353E3FE209A633C7ABED82774DD4D7332E344173DD42F76DE7DC8E92F065BFBED50173D25DE6D5D2C32D74B6D7D3B48173D7CE95E6CB96B15766A383D8FBBFD595B6C555E3B5BDFD558ABAFE376
	D6D67932D767555E835B1571FD4463FB2152173DB62F764EEAD045D74E6C657BEC2B5DDE6DFD35C3E52F02598B7219DF492BBD4347AAFB154CDE846F1BC1BA3A53810B5260445E1DDF22FB1EE437DBBBAC8E749CA94C19064EF91D05362228CC55D7E83CF1B845DE5169944D7BADA3E87CEFBC87752FFDF85E138B3F8B4EE1EBCDD5A7509E1F55B4EC9F14E0DE0565BEBB411E8783A1BD9DE88E5002F314FB943306D0AC3DCE53G8CC77B9DE20F10F0AE523DF40C6956C13A8D488A948C94F9AE7369CEC73A20E8
	99989C40911278266139D541F9B037BA0F490EBA5099123DED26432235DD040156A6FDA51A8769E69C117939830D15FBF76DF7D8EC78515AC310484DBC320A5BEA318FF55877CBF687656D312C41B668EA0D4C26FC4BC1B4B611BBE20B9D8D63842FAFDC49ED2205F3F243A53C1EA358A6FF62577263C756F01BE493E11FA8755B4D0CBF72395AB6DA14E73CEB06D7AB63155982D9E31C61D62EB1361DC71E699D6731B548672B157CC04A0F617C2915FA7A891CFF46209EBF197387A4BD7E627358183EC65527F2
	C3628D9B014AC2C89BEB8E716C97E4BD72DEBC67688CA16B11059CFF8A635394B8D927FC8F63A8BD8FF8016733F97C228AB637EF7C991BDFDC1F67FDFE706BBC7D7941ADF97A734039F93E6701F766F93B2F13788C03AC3EE9BFE3B1F79DE3D8724FD8B97E43635ED376F3396C1AFF6E2D9C1F44FBC51DBFA76575814733717ECD0C82A76578DBFCEFC66922D0186B7D0245F4C6BD0BA9718206F50CB04CF8A1434AF9FE9C9C9BACE2D89C471ABB9916BF1DE1DB2A98564D31159C0B6258FB3CFC040B98F68917EB
	6758AD78B9B18C52319C9B616DA111E396ACE73CB85ED0F7B1DB077D7DB74A732C79973C1D014AC9EF9D366DE2D2761D974B6B30E43CB621EECF7B711A93C6385A44A1A35F08E8368C08B6F4A82D38322C3244D87ED155D662BBECC8693B6E55DA5D73930802294FB0FA0DBDFD932DC75789F616F6817072AFE1FB00655D2A75CA94C8FD7D27CE0BF927292F8D5F9D483C6DA67C1C0C74DBAEE1733627304DGC80782C5DD424A4A7DAEDB330E1CF101509670196E9EF3F2C7C5463CCBC81C0BAE114BAAE270259C
	627336B16633B88C5AC098427EB6269CDFD0D91E11C95689887FBDC149C7FD8DBDA7497C2FD5FC946FAA15FED33A120F4A68BD2ACF72F64796163725F7C488569BBE723AFF9E91C64FC453EB7B41CD4266B37F11C35AC2D7015C964E9AFA53FBDBA8AF0326D04677211F6F75724C066F96245C9EF223F2C3F1E5DD4A6A79C44D3B0262EC41D1EBDF69119EB4F2157622DB945A306037D1B6D7FF3F064D557BAFE56B413115EC2DA50CE3F1038CDB4031F03EA613FF996BD7DBF11FEC01F497473E416DAAG52A19C
	1B076BBD865273B856F1087553051731B17041F4B48616DC8E738DA089A884A89AE8A1D0D6F83C21842893A890A8AC1C2FD7E23DB8C8A78389D740F8855488548D94F085B92391865738AB48F99CBA1FCE0F5229E764977777F07518DBF23977B515E89DDE8845D0DED83C0C37D0A11F956E5DAE772D10A131F769461EC32FEBB5382FB5983D7597363E035A8ABB2B48F84A751DD5FE62E6FD167DAE18016ABE33325907FCB60DB70FDE13214E9683ED223CDC3071AC6D23445E9B037E6CE5AB6CF64DD05BF3C0FA
	164A1E6B58F862E357D10D3DE9103EA21D58A3EDFD4DB136A6B9FD863FCF8D71FB668C569E1F296E00EB7E15504E015A002CC0C1C011C089C029C039917106E220F6A089A884A892A881E891D07ED5F01F836A828A848A3F0A59DFD6036C4F3B0A1C8DC3673ED22FA26730F22F861BD71333DBADF06D878A820AG1A8714FAB5B97F15BB131C33AA1EC94ED1354CA46724087D5B2A5A537D370771904AA73E0FC410BCA8F956AB7D7797D55F5148C7AA527934BCD1D906D3BF28EC1572799A7FC1D4BFA1C2BF0E90
	0A63BA391254111EDC57D51EF81BA2363A2B1467305F2C7EEF15AD6B0F68DD222D9A1E2FF5DDEFFDC119D60E8D64DEC8460E8FA836CEA33FC913E6E351DBDE7C31B345E88E00469EEF67275F5341F2943F3750AB696FE17593696F7DB3557DFDF4A619A74963475111090CC7C19AFB53E7125F323D963F635B8A053D880D3DD88FFB41C57E6C2DD2581B27313750A33F5FF47A3337DAE1AFD5E3EF29073D06AADF76587973CCCA3911EAFB9BA17D2D4A5E961F76108E5BDB40FB319AFB8D107EC1E56FE33F7707BC
	C5FEDBB4763AA07D364A5EAF7D4637CC597EB476C69576C81F3BCF723EF674AC1E478501CE889071FFCE05AF7F4B94E503A8CE63BF89521F297C6F43EBCA83A637E6DD68DC6CBB996497C532F3C6F9E8C8204F52F76723FEF950F4046E886CB6DBA43B13ED896C05697197070F08BDD20729CFE0FB8D4F2FC3B60B2E09B7D4DD431E7FE72C79401B9DG5B1DF607681406890ACFF453B1F67844645E27BBFD12FAF19356A34F765F62F37F9D5710E77BF40E77E23CDB0113E745E779332295F8095730325FD6081E
	BD2A2E057B9FD0BFD0GD098D06C352CAC1FDDD3A678592B6930BB5A44E322D531BCC27217B0B639778CFF1B431E4922EF3CCC08B96D45F4EFE724922FA57748B4FACD6D5804F1123F78A214BF63F571068620EEA0B7D0B0D078F5ACFF35EBCBC87ED0F3E812A4D3CFBFBE4D4CB2F750A28E49B0DAA0257B22EE3D7722E22E23F3036B746F794900AF5670F223628D9B2374764F37F91CD9D2D29968B46B6A7DD6358E5FC9E52C749A9065999B511B2BDA038A21F291749A6BD56FAA559ED5A215CB2157A5BAFAE4
	1CE82CC2EB56076DCE3C20C6562C8FD531F5676533628D1B013A009C3348BEBA5D9F51D96BBE032FF5874DA2EB5DE1F00D1EC5FC933DC770FBB13DF61BC446233F11F381A033E0969B8B0F9E72B596A2BBAB84F9BC4C3D9E6A6EFAB29622F4997CFE6115D7DE79B390DDEF3621058172B4CF56061B87491B1004BFFB9435D545433ADA0847AE12B5E6ECE71F45B6A8092EDD7DA045781DBB35FE5A1D963B53A28D93FE4DC1DDBFCC087B9978E40346CE07535257A7BABF9F9CB80C57BD095C0E8EAFF2DF74763AC4
	094B8D2EC0F3B5F96D5C176C379DEF6A4528890048EE78B41F7B178F6F93DE21EB2D20D8D31759F4CD7D7D8C6D1AB6EADB8D5733B56FC40FB5F544EF677C4D55FA7AE64E1F562847F7F37E7DE3FA7603B9BFF7CCCFBF0C73AF2F5063C7F3FEFF051E7DF91C1F2D2B3F9078EF89E4AE047A8579177609A7A6507D2AEBDEC33F49187D68F2B6E6EF1CF92150FC7F0B69EC4E296DC3E95753F32D3365B54254A3CBF374FC93BF05E8C9D3704E5F7D86CE61BE3D7D989B875C3349B8B08D2E6154971D6F8C796B63B90A
	BE9E8B3A093365BE1EB2DBFECF1C3437441DABF9DB5C5FAF1A51BB4AFCCFEB3DEAFFC7E6D37E5307D47BD55048BFF55A879DAE463F5EAE6FF549CC425B7AF121D29773760B6C8DB7A273BB03BCAE288588BF3FC6913BE8B3FFE512F86C6BF629E367EC4AFFC6E1FB970C2ACEBB5B5AA37C0CEC6D18239262F65EECD92F119B6E310AA0E51177500A3EDDAD7BD232A97FA23CA74D47C70E5FD5A36B91957C32A1671F097757F89E95A2745D917C5D8ED59E77CBB051017F20A408CC434A5714B6BC45285C63DB5629
	73308BE694B6B35D6F236D658B35ADAC06426102C46E04A2DF7BCCEEE60E5B797C1D75CABBDC04E41E0C836DB55AF1G1DDBCB1B4D7796EB7578D9510CDFBE2247AF627C3E8DFA7C924E7F152E7FC64EFFD0520EE3085F41791F6B727BB9BF4E25E7FF1473CF514D5FB44E3FE3D80F9F4E7937F96C2DA2FE8C673F6F31370A4F85F27EC33A7A49517A63B0996B0E23DB240F31B06CAD5F7CFF3F6D1B9F7C0EEF7EF51BFC736F5E6C1B7F37AD3E79C56F79660F216316747EF399DA3E226D5DDE61E34F252A2C44D8
	0176DC3C5B1D7B3EECB77583596EE76332CB5F47DE076A116F8513C7CB4AFBE59D2AEB0B4DA2D9CCD64B881E5C6D87C1DB9F19C3770CA11F74CC2962775FCE7C985FDFD04F5FD70E7D18BEC87E6B8A3D6F49FFD2283FA73F3BD0FFCF7E33C25FE7AF7797FAFF4F7EDF054A776C49F3E2CC892A1B45506F72EF08B7D48135GF55D401E91D38BD127C0DFC871FCBE5CED6EC3E78651D7D1A4667F0EA26FF93D3B487F37D49EACC2670D8E5BA549BE006CB37C51A2D4DF125D01C1027D298815914C107BA5A24A3E0B
	0277642DE2AF01711EC356B0425C102F209B785EB846C2A09D493183F81DA68E528BB9D6E9C5D89ACA7F12E135E63607D140B1DB86BE138D69D10E6D2961E7A0B836FF85DE8B0274920EAD40FB114BFF854FD43FE25814C20415C13A11E3CF96B2FBED9CDB0877AC27C3BA12E33683B89F10CE6458BD87783EA34762F0FECBE2E0DC0EE1D85AFE0435C33A1BE387BA182EB5063D9BB8D6453E91948B58629836BF7B649BE8F0727D6EF707C5320ABB6963AF5D233D2D0A6D51167C1A6D4BC79CC7BE4C109E858A79
	B573BD0B2F6BC4821672EB36D77BA25F5B5FF8A333719ED9B3023495A890080CF5311572D857F2689D6DD8378702AEFE478CE3163D6F1D92A35A3B2E70B20EFA9FDB9FC147C5E97ECC99ECAFFB7A0DAC5E9C3EAFAD5C4430EF2A59FEF39D47EEEBE43A8E0E2D9418EE9247BE414F75E80CD89E4B3087AA98666058A595DC17E38D954C6F725F30F532BB2B5173C2B3243B0182012683C5814D83DA88148614F5B334DB20AA209620AEA087D0GD088D0444DECBC796BEB28EDF8DB1F2407B5702062B4D9701C0E54
	F74FB1D65F31E0EB714DAC661947D8FD175C426A7B0FD8568C69D12010DB467BEC476AAC3AD62E3353EBD819C45E42FC3EDF4730940E750FB0ECE39C43AE5E40308E0E7D250655E3B0475670F344E99C1BAAB12C78378CBBBE4CF43BB996D34B30B99C4B60319447B36CE5FE56211BE3A6FE56A114E385D58CCB0CE7F596D762FDCC3F2D447F181ED8023F15GE3B11A45936C5E92340EE5270BA084FB20A41FE32D2EF63B4BA2BF2B3DBE0C562FA4F0C46A687EBA396F868C6B2F390762724A3D954668DB597A19
	55477EGA973C610ED6372FB2D6AF533EEFCAFF083BF70D636EE7639595FB335F28FE331E2CF9F355BE830F3D4D06EA1CC4D50BFDBFA919EBF62C0BE017B7E36465FFE51B2C1FEDE577ADE82F63E90481EA171FDE405FED9CE41772C2CB970FB8EAB1BE7AB55E5130D5BCE8370DB384CAF8A55757692BAB2CD57A2DEAE74D7EE4A3DA4ED6C6EB9725E9971757C20BA1EBCBCB684025CB49E4F54836AF85E58AE47737481FF71A8C99BCF9C78D82B0A67FEC99D4FB338FDA5005C829ECFCE07BA1E4686B99EEB073F
	F832FD4413BBD77ECD663AD3FD1E8147EBC4F37556544F732B27FD27196E01E03D32397A7B81DAEA86B9321733915F5BBB67EA77F836E05CBCD73B47F36DF1F6EFF0GAFE9AEFF57EC12FBD3F3A7297772A47734AE4473133A5FE9739B0E71665FE9737B61984BEFBB7022FF47725B1B2177ACD3F49BA97F125B746B2191708E8D2F9F52233769350565BE77E52601CE382EDE376ABD7898AA13C02F49BA3151B65428172745D4BEF71EFE1EB682DE2661B5C33AF31EDEECEB85DFF93202CE002EDE336E33E38815
	0D2057D80DEEA224D3B45892C8AF1F9FEFB04E571EF5FB24C24FC79515EB21572E79EAFB8EC887EA30D0C8C78245F9788877792EDB9248274E574BFF13CAEFA915493F1DDCB75F6ED9B774D92E5A7BFCA2355A7FFC6239EA7DE744972ABD1F9117D66BBFA3E654A81F918916D3034653BE1803F6F239659C7B1AE32B717BE588BBE4B1CB7D9CAF2CF91562ADA2F24870B7EAD0DCC3986B2458BB8A6CEB0A3DD70366C41065FE3B1369165460FB0845EAA569B2ACE33141940917C7B50E5162B0A9622E6758573C3E
	ACBE5FA972BE2E8DCDD2AFEA42FAE45C5B073F4BD2F73BF6BCBC0871665B356361A37C59209DF851373371B0D8B5D6955DC147C1FAED3C435B1833CD75DD058EAA57CF2F23DE7586D46F64CD23F261749A63D56F728DCA3D84AA17CC2F0B3D6ABDDF23B7B664A690790D897AE3E399604D9ADEA7242D89FAFD3C5067FB01812093222BF7264FF7B7A3C0A7D6D7EF4A30DE1E9229EC0A17BCAD81FC791DEA1E9152D5F76A7978CD2D1E0F96AA5BF5273E8F8760019ADEA824A3F5FD64285AC29C15C92257D49D9FFA
	6FA5AE25F279F751B175AEEFFA3D2AFA2A23F2ED745A6DD5EF034A1FC4650268F53A0E9E19275A5027F568BCF72F041E253D1D79AF2FA8B116D7ED25FA67FCAC6B95DB472F175EA46B4DBDBC5E359A223B99FD6E0E7E3EE217EC673E6A717B1F57AC5B7873EE59C6FF65F8564869B3EB0B2CF72CF0E2F9F829D576FF30CD36F37840446C846E136DA4FFA15BC959BFB1BBEB8FA0DD725EFE5CDA74CCED31D9ADB6F1977962B5E262F5434771B3E1943423F9F7313544326388DB8869343B58D87D4E890137498F94
	3F230F7F78FBGF77EBF7838D4312FF13562F7650F3A7FC0FC347D28BEC81D8661350925D01F4BFF4F6A79990C95C13A0CE337623533E6C8F7F36C02D58813A09D48319E7E0ECCA8471E426F8D2422F4A243B6717575820E558C31F75E4C9C9B2AE6BE5C9C9BAEE2B187F36C817EBEDFA8477E524864D2B976C5934B5BE20E4589ACEF39FFE0187DE366F7A3475A17B19FE59C3BE38F4BDBG475E9718DC8847769CE2F9CB61D86F980BEF8947487BE6A83E2C3B99A66D4098249BB8B6EF870B391DE39B47D84CE6
	0EBD5D48E2F1F34CD044E21E47310F719A72C2C8AF65D8BADEFB4C3F871EEF6E61B1D73278CAB856A831789AB976930945574131318A96DFBF4712573178C6B936261245174031F249B33E1B78FB1349DCEE103FB7D9164430CE6E3719E34358EFA7247BB976B00FC5CAE2635B7EA1394FF7A9FEBFF16C476F7FAF7F28BEC81EAF477DF08E64B70917434F707AF7AA2417F0ECFD8FAB1B2CFBF91B46E3C287244D9C7B8237FDB72403B896AD77870E8562356E94C8AF6658F338EF665E97EFA83A0FE187F7B25D4D
	9C3B9147A7C1BA10E39760F69E8A69880E3D425BFEB4475661F5772C7BE15C391F3F5704B1A3246BB836952F3137C13A1FE35538DD0EC2BA18E3F9FCDFAF0CE3B7E139B4C867AEE0D8A9FF87F9A347CE41F2D6C887F26CD2090FE39CFB82375FD4147EA34342719AF98124B7F3AC8F17E99924DBB8B68D672D8B52EE0E65E25DE0C8CF675862A666B70AE399381C058720BE9EE058A358DE89249BB8560C5BC18B243BB9D603FDC8101E46B19B568D07F4B4474AF85B184731D6BCD696BD88FD78C1063D0973568C
	698E0EF5623D4DFEC83BB9769B3E5F9948B1728EF79224D3B8766DFA04AD01F46EC206452F6777A40E9D4775E606344431DDF8BFB68852219C0B60FB34619CAB43FE97C3BA6BA1FEEF406D258852459CBB1B37212A07587A72CBD5E82DC2C23B2DE8BEA7303D0EB578FE9888F253386C274733192C4066F9B587D155917EA5996A356A2F8F493C2CCC7DFD030D78BB59F160A3017BF9204E7B9E4C73388DAC81592C07197C2CD56AF5745B70FC218C788D8F33FD10913FE717FD2D15DB9FA6E717D7707C88EE7DBD
	18A27CBE329B6483396FFA1FFBB059FEFCC7011D81016C41909FA21E3F4482BE8F08E0E1383E96C3DAF8046DDF0F22E11D6E95BC67733D269CBFB11401CD145FFFC299905B2D68D8B40D470D7BF4834834BC4262F98A77B78952819C8B45F31DC8C847BC424A26EF192F38727C44158AF61E2475C2FCAC46732445008B494CC713606F9D9EDFBECA404E172A723763F1238E7066E4D656569139BC52707C48813C51E456C6FF32832DDB765B1D1691E827A6ABBA744DF6B0195E0C9DECCFB7A2191559F398CB02F4
	B247F6E37B4B9F057172D116477426711C5B7516479630F3252A9C376139DB87605D0F32BC9C28D82B4AC3135552E7C3A7FED83970B38341DC2728D2BDA61C7D03DCCEE7637BD28C484EF914654D42773553B8960D310D0F41D8769843A2302F8EC8F7BF464A6005F57E5A122FB21886F6CE05B2C86771ED4771CD87BC027B385B677BE059FEFCA4BD266D4B9713773EGDF4473F68A4EDB5163F04FFD1C156334F56AF25CF1DC6E5BFF7319EFFF7B129D0F13B9A8FBEFF02DF0E30DFABCCD44F337EE103372F82E
	59200E277B049C4F07BD7FC9BC5341471502F21F745BBAF5BCAEFC8F08G39E89ECF2F200E6754B1B91EBBFC76FFFFE3FF4A6372EF626B4291F5BC33701CA18D641672F8F2F72A6319A148718C6C74974FEA9F7114241033B7F2BCFF58200E67C9BC1F2A83396694FE0FECD447B3BFDD0EE73D5F31E3150FF8A47031DA95CFC92D5A578B9982671DD32BBF8F388A4F598240D6B00F39F1507BBC60EBBCDF0A8159B9DC3ECDF35E60B0199F83FFE98A6B3FC63FE78AFC755F12A7628D3BD5F93D3BD2FF9E70B476DD
	87724DCFB05F3B7D3E2B6C4B3763896DD90C50CA7D320CA24F8FA09F44FD47C87F4958986D61FBE9053E6F826CBB8E64931EE075F21366EC4EEBD9F2FBB00E685BA15F41DE82B6327E446CDCD96B3DBD446176D386328DDC7E2DE1F5FB28464F8A7D40177E444AE5194F3E186327DC42414E8D741EC1FCEE58205F9E4AF11F0C8279D89E5F5FB57DE4C736DCAE5F5469178B997BD24146E2EEE75F3077F26945F9B6BE99EFA8F9121FF129D5174B05386CBA01EFFE12154B27FE67CC3E4AA5846C58D56552FDC83F
	DC5CF8AE95867211BC3EEA4D787A99EE8B8B001F42E56C1AF3B15D78EF411258137C1E0BD961A3760254F8C3352A0FB7D42963798ABFEF99C12EA41545A32D575CFFF264F87E385EDFBCEBFD445385BEAAD57104E84EB1DD0D4742FE1013F8BC7F521CE31A11AB47F30B5FF3CCF9BE6289CF553ED37F2926FC3EA5EB7AA0974B63E9521C731A1FA747B3436F186C6B0C5AA270413ECBC5EC63BF21C2E35BFC9C452A33EF516434182C6F14984B693685557DEE052C9BD367EB7F87FF370C6F7FCCD9A96B1D3DEA7C
	FAABD64BFA0746FD4618681661EF2C115F7B574AF6E67B5C37D27BBF25C036913ECE3611D3BD7EFD2BEF0BE43D01220965A1E0036CFF766B321D9B047167613E0D321E7963716BADB34AFA4D4BA696F7695BF25CDF3EA35B39F1EC7C7E25CD320D1FEF16ED7C7944784B7E46AD321EE507AF5FD554B7117D8D16D57E351D7D68FF763A062B34ED771E8E6A6321F7E59F2BFD662FCA153F0F946FA0BD5CB831726DD23CF3B426F8672832E27CF9BC2DE27CF93C26C476D16DF33FDA6DA3C09A3F0F448FE59F523872
	C174CA4AE43D24F593AB47AF4A65F2F4D648F65E9DD79E69FBF315320DDB2BE49B296563EF276F57AA46088966E1DF1D6C7F2AFA594E199BA6E6A7E13BEC6743BD321DDD42446C34FFAA5B79D52BEC6709260959F9E02FEC27E61FEC672C8976957367321D5395FB76EF8E0E7F7C42556D325E7B156357EB6E10757E39FEE2F1779D1063BED3310EFC73844E0BDCF3C8361158A95BA817465FB61F7FDA36317E9B5946652E715B7828CB561BDABB7E783B3ED5141FFB7CFA37FE27680793ECF7FF6A16731BF7D836
	E31EC05CA5BD325E874363577B42AC6B75CC405FCB3D325E6689765762FEB93F1FD9E4BB57CEA06EAB0F4AFAED9D9373FF3BD5767F2EDD367365847BCB2BC3369365146D54CE700C4FBDAE594EA7EE45F8B141B3C707719EC0A1561D53349DFEC4BEA3E4CB0260F28AEBBC0B34692CBF5E9F79DDF3C1061051AA987A715156D6A1FB881D11F5232C4543A4FE1B6BFB61841E4B2333322F0225D55CD2ABBE551AFC2C32BE73714BB3B304ACB0C3CE79368AD96828AED8F3922B19125D01ED321320E81A1909ED6345
	877A8FF5228C0E26AB50F4950AE805AEAA28AC888AF43C96D6F8D8C07F362950CC1F5E52D5F6BDD17CD4AB203FD84DE4F29525122511F18AABD455BF77526ABFA5BCFC5015A998DA058D68A9EF3F6844AFEA0BE67447D98E0ACED43BFB04BC075D82CC4021BADC78126D12062DE21BD0507B957ACB51AEC9B4B731D713DD12308E7F3555ED680D4FEDADA6D73F040EF1EFEF9332EC2681D19256903655E46B5BC65E875FCE6A3429916792BF66C13CEBF13C48CAA937327626D5DB5E5E1B9213A9E46C91DE355ACD
	66BD269E496EEC957224FE27686A37DB4DEE47A744D0AE5ED0438F19E06EF51EFD1459D234D34303BE6A3EA8F8C56A0ABD990260F68A5B30A4FA28AD65926843BCCEF0A5BADB01CC60F2B3671D7226746E1D1F96F6E58869B4132868573B9D12E068B58DD82C43702399F7B6DCA92FC9902D53E4F3213F63891199B511B196F7D0FA5E7C32732FFA69DF58012CAA89EF11228455EDFD22FDE0DBD3EF2F45A6F283D02EE07E8DEE9E4914EAE456384FD937775ED78EC3BBE896B2874A4A707F65787F8A74FFB9C64A
	B1D2DE81051BA6787C27653BD91EF16361BD9FD5FF699E28C16901E766DF76E0C35AA2485D9EA19B7FADCC2804FCD90AFC86693448735FD896127C93D4C78E54D45C486E1BEE5E7A51B2DB9625CC0F7E08972BA561F57CC59863AE134354E311060D7C8FAD983754CBC2C10BFD48A8590D684FA79869C7CDGFE957D0D82A37A7B87BC01DEE92C67DD8C2D92B8F4D2859E4D9A21C51E6D93419BF9ABC1293730685B787F4D93AEF32475AE56AD66C5D5AD9795DAD54B2132D9BAF13341897D1BAD688FB8A7D55EBE
	117316C573067FC8544997699E372A048DD4B673640BF41F3ACCD924B093A8EC9A04CE027B81794A18117C758FC94827E976B1B768AF0662810B2DF42B4CE39FE0835EAA7A21B3A36962CA04FCA88DA168E3E628D392F5548351A7250C681BD210D83F49E86FB5120FC499F71BFA7A99231074F47E71A79AA36A6568430EA61B99BA3FCBC49713049472E907B7520FB5813482FFC148C8BE11244C4BFA7C29A338F5D11E91FFE50858A6C6300091FF8C89C5C98CE1A9A37A2A9499C0A4E165BE135BC819E0C03795
	24FBE985D96AD64046D983F93D0854A65279F11AC72FDBB5F0746924CE2A667C3D3A633139D23ACE5C4A3919377956C915379F54F7418C1A3F6C7F2D9A8AD45790FB4B5BDF8DED70D2C3CC63EC1D54C9156FE9F23EF38539E5E6794D45C91C278B64BC21A73EBC6ABBD7A7EE65130BBA5FA7E9EF8BD75F98D96CA761ADFC26FADCB0884A9EF5D2C5FA3D67E483C5EAB879A27D757FCC24F17A112A6F8AA77DE34B7C7167C2793C07508C1D11C4A91369C566FF26EC6ED7174DAAAA6B3F06CF62BCBD2A4E930E40CB
	1ECE2A381F72FC30135B69C995E91AE724A83E13F084FB61FF26CCDF763C53FE0C23A65F0A5EBA267C7BF7CB2696F97D7BF70B04DB713B57C27A2D7CDB51641BECF73462737800F782116FBD3C5E0AB61F3D7D7D087D83A62BD5B2D90FB6594C68EFC96C9C16C4791B11092D483E0B49D0A6615D5F0A62BE225079BFD0CB87889B6112FB92ACGGA81CGGD0CB818294G94G88G88G6EF854AC9B6112FB92ACGGA81CGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB
	8586GGGG81G81GBAGGGCCACGGGG
**end of data**/
}
}
