package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * Insert the type's description here.
 * Creation date: (8/1/2002 11:56:33 AM)
 * @author: 
 */

import javax.swing.JComboBox;

import com.cannontech.common.util.StringUtils;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
 
public class TimeRefreshGearPanel extends GenericGearPanel {
	private javax.swing.JComboBox ivjJComboBoxGroupSelection = null;
	private javax.swing.JComboBox ivjJComboBoxHowToStop = null;
	private javax.swing.JComboBox ivjJComboBoxNumGroups = null;
	private javax.swing.JComboBox ivjJComboBoxWhenChange = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeDuration = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangePriority = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeTriggerNumber = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldPercentReduction = null;
	private javax.swing.JLabel ivjJLabelChangeDuration = null;
	private javax.swing.JLabel ivjJLabelChangePriority = null;
	private javax.swing.JLabel ivjJLabelChangeTriggerNumber = null;
	private javax.swing.JLabel ivjJLabelChangeTriggerOffset = null;
	private javax.swing.JLabel ivjJLabelGroupSelection = null;
	private javax.swing.JLabel ivjJLabelHowToStop = null;
	private javax.swing.JLabel ivjJLabelMinutesChDur = null;
	private javax.swing.JLabel ivjJLabelNumGroups = null;
	private javax.swing.JLabel ivjJLabelPercentReduction = null;
	private javax.swing.JLabel ivjJLabelSendRate = null;
	private javax.swing.JLabel ivjJLabelShedTime = null;
	private javax.swing.JLabel ivjJLabelWhenChange = null;
	private javax.swing.JPanel ivjJPanelChangeMethod = null;
	private javax.swing.JTextField ivjJTextFieldChangeTriggerOffset = null;
	private javax.swing.JComboBox ivjJComboBoxSendRateDigits = null;
	private javax.swing.JComboBox ivjJComboBoxSendRateUnits = null;
	private javax.swing.JComboBox ivjJComboBoxCycleCountSndType = null;
	private javax.swing.JLabel ivjJLabelCycleCntSndType = null;
	private javax.swing.JCheckBox ivjJCheckBoxRampIn = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldRampInPercent = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldRampOutPercent = null;
	private javax.swing.JLabel ivjJLabelRampInInterval = null;
	private javax.swing.JLabel ivjJLabelRampInPercent = null;
	private javax.swing.JLabel ivjJLabelRampInPercentSign = null;
	private javax.swing.JLabel ivjJLabelRampInSec = null;
	private javax.swing.JLabel ivjJLabelRampOutInterval = null;
	private javax.swing.JLabel ivjJLabelRampOutPercent = null;
	private javax.swing.JLabel ivjJLabelRampOutPercentSign = null;
	private javax.swing.JLabel ivjJLabelRampOutSec = null;
	private javax.swing.JTextField ivjJTextFieldRampInInterval = null;
	private javax.swing.JTextField ivjJTextFieldRampOutInterval = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JComboBox<String> ivjJComboBoxShedTimeDigits = null;
	private JComboBox<String> ivjJComboBoxShedTimeUnits = null;
	private JComboBox<String> ivjJComboBoxStopOrder = null;
	private javax.swing.JLabel ivjJLabelStopOrder = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TimeRefreshGearPanel.this.getJCheckBoxRampIn()) 
				connEtoC1(e);
			if (e.getSource() == TimeRefreshGearPanel.this.getJComboBoxWhenChange()) 
				connEtoC2(e);
			if (e.getSource() == TimeRefreshGearPanel.this.getJComboBoxCycleCountSndType()) 
				connEtoC3(e);
		};
	};
/**
 * TimeRefreshGearPanel constructor comment.
 */
public TimeRefreshGearPanel() {
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
	if (e.getSource() == getJComboBoxShedTimeDigits() || e.getSource() == getJComboBoxShedTimeUnits()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxNumGroups() || e.getSource() == getJComboBoxStopOrder() || e.getSource() == getJTextFieldRampOutInterval()) 
		connEtoC5(e);
	if (e.getSource() == getJComboBoxSendRateDigits() || e.getSource() == getJComboBoxSendRateUnits()) 
		connEtoC7(e);
	if (e.getSource() == getJComboBoxGroupSelection()) 
		connEtoC8(e);
	if (e.getSource() == getJComboBoxHowToStop()) 
		connEtoC10(e);
	// user code begin {2}
	// user code end
}
	/**
	 * Called when the caret position is updated.
	 *
	 * @param e the caret event
	 */
public void caretUpdate(javax.swing.event.CaretEvent e) {}
/**
 * connEtoC1:  (JComboBoxWhenChange.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.jComboBoxWhenChange_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxRampIn_ActionPerformed(arg1);
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
		rampItOut(getJComboBoxHowToStop().getSelectedItem().toString().compareTo("Ramp Out / Time In") == 0
			|| getJComboBoxHowToStop().getSelectedItem().toString().compareTo("Ramp Out / Restore") == 0);
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
 * connEtoC2:  (JComboBoxWhenChange.action.actionPerformed(java.awt.event.ActionEvent) --> TimeRefreshGearPanel.jComboBoxWhenChange_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JComboBoxCycleCountSndType.action.actionPerformed(java.awt.event.ActionEvent) --> TimeRefreshGearPanel.jComboBoxCycleCountSndType_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxCycleCountSndType_ActionPerformed(arg1);
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
 * Return the JCheckBoxRampIn property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRampIn() {
	if (ivjJCheckBoxRampIn == null) {
		try {
			ivjJCheckBoxRampIn = new javax.swing.JCheckBox();
			ivjJCheckBoxRampIn.setName("JCheckBoxRampIn");
			ivjJCheckBoxRampIn.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJCheckBoxRampIn.setText("Ramp In");
			ivjJCheckBoxRampIn.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRampIn;
}
/**
 * Return the JComboBoxCycleCountSndType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JComboBox getJComboBoxCycleCountSndType() {
	if (ivjJComboBoxCycleCountSndType == null) {
		try {
			ivjJComboBoxCycleCountSndType = new javax.swing.JComboBox();
			ivjJComboBoxCycleCountSndType.setName("JComboBoxCycleCountSndType");
			ivjJComboBoxCycleCountSndType.setPreferredSize(new java.awt.Dimension(146, 23));
			ivjJComboBoxCycleCountSndType.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxCycleCountSndType.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxCycleCountSndType.setMinimumSize(new java.awt.Dimension(146, 23));
			// user code begin {1}
			ivjJComboBoxCycleCountSndType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.OPTION_FIXED_SHED ) );
			ivjJComboBoxCycleCountSndType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.OPTION_DYNAMIC_SHED ) );
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
 * Return the JComboBoxGroupSelection property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGroupSelection() {
	if (ivjJComboBoxGroupSelection == null) {
		try {
			ivjJComboBoxGroupSelection = new javax.swing.JComboBox();
			ivjJComboBoxGroupSelection.setName("JComboBoxGroupSelection");
			ivjJComboBoxGroupSelection.setPreferredSize(new java.awt.Dimension(184, 23));
			ivjJComboBoxGroupSelection.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxGroupSelection.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxGroupSelection.setMinimumSize(new java.awt.Dimension(184, 23));
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
			ivjJComboBoxHowToStop.setPreferredSize(new java.awt.Dimension(184, 23));
			ivjJComboBoxHowToStop.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxHowToStop.setMinimumSize(new java.awt.Dimension(184, 23));
			// user code begin {1}
			ivjJComboBoxHowToStop.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_TIME_IN ) );
			ivjJComboBoxHowToStop.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_RESTORE ) );
			ivjJComboBoxHowToStop.addItem( "Ramp Out / Time In");
			ivjJComboBoxHowToStop.addItem( "Ramp Out / Restore");
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
 * Return the JComboBoxNumGroups property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxNumGroups() {
	if (ivjJComboBoxNumGroups == null) {
		try {
			ivjJComboBoxNumGroups = new javax.swing.JComboBox();
			ivjJComboBoxNumGroups.setName("JComboBoxNumGroups");
			ivjJComboBoxNumGroups.setPreferredSize(new java.awt.Dimension(146, 23));
			ivjJComboBoxNumGroups.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxNumGroups.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxNumGroups.setMinimumSize(new java.awt.Dimension(146, 23));
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
 * Return the JComboBoxSendRate property value.
 * @return javax.swing.JComboBox
 */

/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSendRateDigits() {
	if (ivjJComboBoxSendRateDigits == null) {
		try {
			ivjJComboBoxSendRateDigits = new javax.swing.JComboBox();
			ivjJComboBoxSendRateDigits.setName("JComboBoxSendRateDigits");
			ivjJComboBoxSendRateDigits.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxSendRateDigits.setMaximumSize(new java.awt.Dimension(67, 23));
			ivjJComboBoxSendRateDigits.setPreferredSize(new java.awt.Dimension(67, 23));
			ivjJComboBoxSendRateDigits.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxSendRateDigits.setMinimumSize(new java.awt.Dimension(64, 23));
			ivjJComboBoxSendRateDigits.setEditable(true);
			// user code begin {1}
			NewComboBoxEditor ncb = new NewComboBoxEditor();
			ncb.getJTextField().setDocument( 
				  new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 1000) );
         	ivjJComboBoxSendRateDigits.setEditor( ncb );
			
			ivjJComboBoxSendRateDigits.addItem("0");
			ivjJComboBoxSendRateDigits.addItem("1");
			ivjJComboBoxSendRateDigits.addItem("2");
			ivjJComboBoxSendRateDigits.addItem("5");
			ivjJComboBoxSendRateDigits.addItem("8");
			ivjJComboBoxSendRateDigits.addItem("10");
			ivjJComboBoxSendRateDigits.addItem("15");
			ivjJComboBoxSendRateDigits.addItem("20");
			ivjJComboBoxSendRateDigits.addItem("30");
			ivjJComboBoxSendRateDigits.addItem("45");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSendRateDigits;
}
/**
 * Return the JComboBoxSendRate11 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSendRateUnits() {
	if (ivjJComboBoxSendRateUnits == null) {
		try {
			ivjJComboBoxSendRateUnits = new javax.swing.JComboBox();
			ivjJComboBoxSendRateUnits.setName("JComboBoxSendRateUnits");
			ivjJComboBoxSendRateUnits.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxSendRateUnits.setMaximumSize(new java.awt.Dimension(102, 23));
			ivjJComboBoxSendRateUnits.setPreferredSize(new java.awt.Dimension(102, 23));
			ivjJComboBoxSendRateUnits.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxSendRateUnits.setMinimumSize(new java.awt.Dimension(102, 23));
			// user code begin {1}
			ivjJComboBoxSendRateUnits.addItem("minutes");
			ivjJComboBoxSendRateUnits.addItem("hours");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSendRateUnits;
}

private JComboBox<String> getJComboBoxShedTimeDigits() {
	if (ivjJComboBoxShedTimeDigits == null) {
		try {
			ivjJComboBoxShedTimeDigits = new JComboBox<>();
			ivjJComboBoxShedTimeDigits.setName("JComboBoxShedTimeDigits");
			ivjJComboBoxShedTimeDigits.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxShedTimeDigits.setMaximumSize(new java.awt.Dimension(67, 23));
			ivjJComboBoxShedTimeDigits.setPreferredSize(new java.awt.Dimension(67, 23));
			ivjJComboBoxShedTimeDigits.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxShedTimeDigits.setMinimumSize(new java.awt.Dimension(64, 23));
			ivjJComboBoxShedTimeDigits.setEditable(true);
			// user code begin {1}
			
			NewComboBoxEditor ncb = new NewComboBoxEditor();
			ncb.getJTextField().setDocument( 
				  new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 1000) );
			ivjJComboBoxShedTimeDigits.setEditor( ncb );
			
			ivjJComboBoxShedTimeDigits.addItem("1");
			ivjJComboBoxShedTimeDigits.addItem("2");
			ivjJComboBoxShedTimeDigits.addItem("3");
			ivjJComboBoxShedTimeDigits.addItem("4");
			ivjJComboBoxShedTimeDigits.addItem("5");
			ivjJComboBoxShedTimeDigits.addItem("6");
			ivjJComboBoxShedTimeDigits.addItem("7");
			ivjJComboBoxShedTimeDigits.addItem("8");
			ivjJComboBoxShedTimeDigits.addItem("10");
			ivjJComboBoxShedTimeDigits.addItem("15");
			ivjJComboBoxShedTimeDigits.addItem("20");
			ivjJComboBoxShedTimeDigits.addItem("30");
			ivjJComboBoxShedTimeDigits.addItem("45");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxShedTimeDigits;
}
/**
 * Return the JComboBoxSendRateUnits1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxShedTimeUnits() {
	if (ivjJComboBoxShedTimeUnits == null) {
		try {
			ivjJComboBoxShedTimeUnits = new javax.swing.JComboBox();
			ivjJComboBoxShedTimeUnits.setName("JComboBoxShedTimeUnits");
			ivjJComboBoxShedTimeUnits.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxShedTimeUnits.setMaximumSize(new java.awt.Dimension(102, 23));
			ivjJComboBoxShedTimeUnits.setPreferredSize(new java.awt.Dimension(102, 23));
			ivjJComboBoxShedTimeUnits.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxShedTimeUnits.setMinimumSize(new java.awt.Dimension(102, 23));
			// user code begin {1}
			ivjJComboBoxShedTimeUnits.addItem("minutes");
			ivjJComboBoxShedTimeUnits.addItem("hours");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxShedTimeUnits;
}
/**
 * Return the JComboBoxStopOrder property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxStopOrder() {
	if (ivjJComboBoxStopOrder == null) {
		try {
			ivjJComboBoxStopOrder = new javax.swing.JComboBox();
			ivjJComboBoxStopOrder.setName("JComboBoxStopOrder");
			ivjJComboBoxStopOrder.setPreferredSize(new java.awt.Dimension(184, 23));
			ivjJComboBoxStopOrder.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxStopOrder.setMinimumSize(new java.awt.Dimension(184, 23));
			// user code begin {1}
			ivjJComboBoxStopOrder.addItem("Random");
			ivjJComboBoxStopOrder.addItem("First In First Out");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxStopOrder;
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
			ivjJComboBoxWhenChange.setPreferredSize(new java.awt.Dimension(170, 20));
			ivjJComboBoxWhenChange.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxWhenChange.setMinimumSize(new java.awt.Dimension(170, 20));
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
			ivjJCSpinFieldChangeDuration.setPreferredSize(new java.awt.Dimension(66, 20));
			ivjJCSpinFieldChangeDuration.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldChangeDuration.setMaximumSize(new java.awt.Dimension(66, 20));
			ivjJCSpinFieldChangeDuration.setMinimumSize(new java.awt.Dimension(66, 20));
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
			ivjJCSpinFieldChangePriority.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJCSpinFieldChangeTriggerNumber.setPreferredSize(new java.awt.Dimension(35, 20));
			ivjJCSpinFieldChangeTriggerNumber.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldChangeTriggerNumber.setMaximumSize(new java.awt.Dimension(40, 20));
			ivjJCSpinFieldChangeTriggerNumber.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			ivjJCSpinFieldChangeTriggerNumber.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(1), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(1)), 
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
 * Return the JCSpinFieldPercentReduction property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldPercentReduction() {
	if (ivjJCSpinFieldPercentReduction == null) {
		try {
			ivjJCSpinFieldPercentReduction = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldPercentReduction.setName("JCSpinFieldPercentReduction");
			ivjJCSpinFieldPercentReduction.setPreferredSize(new java.awt.Dimension(88, 20));
			ivjJCSpinFieldPercentReduction.setMaximumSize(new java.awt.Dimension(88, 20));
			ivjJCSpinFieldPercentReduction.setMinimumSize(new java.awt.Dimension(88, 20));
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
 * Return the JCSpinFieldRampInPercent property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldRampInPercent() {
	if (ivjJCSpinFieldRampInPercent == null) {
		try {
			ivjJCSpinFieldRampInPercent = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldRampInPercent.setName("JCSpinFieldRampInPercent");
			ivjJCSpinFieldRampInPercent.setPreferredSize(new java.awt.Dimension(66, 20));
			ivjJCSpinFieldRampInPercent.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldRampInPercent.setMaximumSize(new java.awt.Dimension(66, 20));
			ivjJCSpinFieldRampInPercent.setMinimumSize(new java.awt.Dimension(66, 20));
			// user code begin {1}
			ivjJCSpinFieldRampInPercent.setDataProperties(
								new com.klg.jclass.field.DataProperties(
									new com.klg.jclass.field.validate.JCIntegerValidator(
									null, new Integer(0), new Integer(100), null, true, 
									null, new Integer(1), "#,##0.###;-#,##0.###", false, 
									false, false, null, new Integer(100)), 
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
	return ivjJCSpinFieldRampInPercent;
}
/**
 * Return the JCSpinFieldRampOutPercent property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldRampOutPercent() {
	if (ivjJCSpinFieldRampOutPercent == null) {
		try {
			ivjJCSpinFieldRampOutPercent = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldRampOutPercent.setName("JCSpinFieldRampOutPercent");
			ivjJCSpinFieldRampOutPercent.setPreferredSize(new java.awt.Dimension(66, 20));
			ivjJCSpinFieldRampOutPercent.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldRampOutPercent.setMaximumSize(new java.awt.Dimension(66, 20));
			ivjJCSpinFieldRampOutPercent.setMinimumSize(new java.awt.Dimension(66, 20));
			// user code begin {1}
			ivjJCSpinFieldRampOutPercent.setDataProperties(
						new com.klg.jclass.field.DataProperties(
							new com.klg.jclass.field.validate.JCIntegerValidator(
								null, new Integer(0), new Integer(100), null, true, 
								null, new Integer(1), "#,##0.###;-#,##0.###", false, 
								false, false, null, new Integer(100)), 
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
	return ivjJCSpinFieldRampOutPercent;
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
			ivjJLabelChangeDuration.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelChangeDuration.setText("Change Duration:");
			ivjJLabelChangeDuration.setFont(new java.awt.Font("dialog", 0, 12));
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
			ivjJLabelChangePriority.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelChangePriority.setText("Change Priority:");
			ivjJLabelChangePriority.setFont(new java.awt.Font("dialog", 0, 12));
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
			ivjJLabelChangeTriggerNumber.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJLabelChangeTriggerOffset.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJLabelCycleCntSndType.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelCycleCntSndType.setText("Refresh Shed Type:");
			ivjJLabelCycleCntSndType.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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
			ivjJLabelGroupSelection.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelGroupSelection.setText("Group Selection Method:");
			ivjJLabelGroupSelection.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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
			ivjJLabelHowToStop.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJLabelMinutesChDur.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJLabelNumGroups.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelNumGroups.setText("# of Groups Each Time:");
			ivjJLabelNumGroups.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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
			ivjJLabelPercentReduction.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelPercentReduction.setText("Group Capacity Reduction %:");
			ivjJLabelPercentReduction.setFont(new java.awt.Font("dialog", 0, 12));
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
 * Return the JLabelRampInInterval property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRampInInterval() {
	if (ivjJLabelRampInInterval == null) {
		try {
			ivjJLabelRampInInterval = new javax.swing.JLabel();
			ivjJLabelRampInInterval.setName("JLabelRampInInterval");
			ivjJLabelRampInInterval.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelRampInInterval.setText("Ramp In Interval: ");
			ivjJLabelRampInInterval.setMaximumSize(new java.awt.Dimension(119, 14));
			ivjJLabelRampInInterval.setPreferredSize(new java.awt.Dimension(119, 14));
			ivjJLabelRampInInterval.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRampInInterval.setMinimumSize(new java.awt.Dimension(119, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRampInInterval;
}
/**
 * Return the JLabelRampInPercent property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRampInPercent() {
	if (ivjJLabelRampInPercent == null) {
		try {
			ivjJLabelRampInPercent = new javax.swing.JLabel();
			ivjJLabelRampInPercent.setName("JLabelRampInPercent");
			ivjJLabelRampInPercent.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelRampInPercent.setText("Ramp In Percent: ");
			ivjJLabelRampInPercent.setMaximumSize(new java.awt.Dimension(119, 14));
			ivjJLabelRampInPercent.setPreferredSize(new java.awt.Dimension(119, 14));
			ivjJLabelRampInPercent.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRampInPercent.setMinimumSize(new java.awt.Dimension(119, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRampInPercent;
}
/**
 * Return the JLabelRampInPercentSign property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRampInPercentSign() {
	if (ivjJLabelRampInPercentSign == null) {
		try {
			ivjJLabelRampInPercentSign = new javax.swing.JLabel();
			ivjJLabelRampInPercentSign.setName("JLabelRampInPercentSign");
			ivjJLabelRampInPercentSign.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRampInPercentSign.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelRampInPercentSign.setText("%");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRampInPercentSign;
}
/**
 * Return the JLabelRampInSec property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRampInSec() {
	if (ivjJLabelRampInSec == null) {
		try {
			ivjJLabelRampInSec = new javax.swing.JLabel();
			ivjJLabelRampInSec.setName("JLabelRampInSec");
			ivjJLabelRampInSec.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRampInSec.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelRampInSec.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRampInSec;
}
/**
 * Return the JLabelRampOutInterval property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRampOutInterval() {
	if (ivjJLabelRampOutInterval == null) {
		try {
			ivjJLabelRampOutInterval = new javax.swing.JLabel();
			ivjJLabelRampOutInterval.setName("JLabelRampOutInterval");
			ivjJLabelRampOutInterval.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelRampOutInterval.setText("Ramp Out Interval: ");
			ivjJLabelRampOutInterval.setFont(new java.awt.Font("dialog", 0, 12));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRampOutInterval;
}
/**
 * Return the JLabelRampOutPercent property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRampOutPercent() {
	if (ivjJLabelRampOutPercent == null) {
		try {
			ivjJLabelRampOutPercent = new javax.swing.JLabel();
			ivjJLabelRampOutPercent.setName("JLabelRampOutPercent");
			ivjJLabelRampOutPercent.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelRampOutPercent.setText("Ramp Out Percent: ");
			ivjJLabelRampOutPercent.setFont(new java.awt.Font("dialog", 0, 12));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRampOutPercent;
}
/**
 * Return the JLabelRampOutPercentSign property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRampOutPercentSign() {
	if (ivjJLabelRampOutPercentSign == null) {
		try {
			ivjJLabelRampOutPercentSign = new javax.swing.JLabel();
			ivjJLabelRampOutPercentSign.setName("JLabelRampOutPercentSign");
			ivjJLabelRampOutPercentSign.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRampOutPercentSign.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelRampOutPercentSign.setText("%");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRampOutPercentSign;
}
/**
 * Return the JLabelRampOutSec property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRampOutSec() {
	if (ivjJLabelRampOutSec == null) {
		try {
			ivjJLabelRampOutSec = new javax.swing.JLabel();
			ivjJLabelRampOutSec.setName("JLabelRampOutSec");
			ivjJLabelRampOutSec.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRampOutSec.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelRampOutSec.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRampOutSec;
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
			ivjJLabelSendRate.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelSendRate.setText("Command Resend Rate:");
			ivjJLabelSendRate.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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
			ivjJLabelShedTime.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelShedTime.setText("Fixed Shed Time:");
			ivjJLabelShedTime.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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
 * Return the JLabelStopOrder property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStopOrder() {
	if (ivjJLabelStopOrder == null) {
		try {
			ivjJLabelStopOrder = new javax.swing.JLabel();
			ivjJLabelStopOrder.setName("JLabelStopOrder");
			ivjJLabelStopOrder.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelStopOrder.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelStopOrder.setText("Stop Order:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopOrder;
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
			ivjJLabelWhenChange.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJPanelChangeMethod = new javax.swing.JPanel();
			ivjJPanelChangeMethod.setName("JPanelChangeMethod");
			ivjJPanelChangeMethod.setLayout(new java.awt.GridBagLayout());
			ivjJPanelChangeMethod.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJPanelChangeMethod.setMaximumSize(new java.awt.Dimension(360, 100));
			ivjJPanelChangeMethod.setPreferredSize(new java.awt.Dimension(330, 100));
			ivjJPanelChangeMethod.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJPanelChangeMethod.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

			java.awt.GridBagConstraints constraintsJLabelChangeDuration = new java.awt.GridBagConstraints();
			constraintsJLabelChangeDuration.gridx = 1; constraintsJLabelChangeDuration.gridy = 2;
			constraintsJLabelChangeDuration.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeDuration.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelChangeDuration.weightx = .5;
			constraintsJLabelChangeDuration.ipady = 6;
			constraintsJLabelChangeDuration.insets = new java.awt.Insets(1, 5, 3, 5);
			getJPanelChangeMethod().add(getJLabelChangeDuration(), constraintsJLabelChangeDuration);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeDuration = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeDuration.gridx = 2; constraintsJCSpinFieldChangeDuration.gridy = 2;
			constraintsJCSpinFieldChangeDuration.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangeDuration.insets = new java.awt.Insets(1, 5, 3, 1);
			getJPanelChangeMethod().add(getJCSpinFieldChangeDuration(), constraintsJCSpinFieldChangeDuration);

			java.awt.GridBagConstraints constraintsJLabelMinutesChDur = new java.awt.GridBagConstraints();
			constraintsJLabelMinutesChDur.gridx = 3; constraintsJLabelMinutesChDur.gridy = 2;
			constraintsJLabelMinutesChDur.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelMinutesChDur.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelMinutesChDur.insets = new java.awt.Insets(5, 1, 3, 2);
			getJPanelChangeMethod().add(getJLabelMinutesChDur(), constraintsJLabelMinutesChDur);

			java.awt.GridBagConstraints constraintsJLabelChangePriority = new java.awt.GridBagConstraints();
			constraintsJLabelChangePriority.gridx = 1; constraintsJLabelChangePriority.gridy = 2;
			constraintsJLabelChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangePriority.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelChangePriority.weightx = .5;
			constraintsJLabelChangePriority.ipady = 6;
			constraintsJLabelChangePriority.insets = new java.awt.Insets(1, 3, 3, 2);
			getJPanelChangeMethod().add(getJLabelChangePriority(), constraintsJLabelChangePriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangePriority = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangePriority.gridx = 2; constraintsJCSpinFieldChangePriority.gridy = 2;
			constraintsJCSpinFieldChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangePriority.weightx = .5;
			constraintsJCSpinFieldChangePriority.insets = new java.awt.Insets(1, 3, 3, 8);
			getJPanelChangeMethod().add(getJCSpinFieldChangePriority(), constraintsJCSpinFieldChangePriority);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerNumber.gridx = 1; constraintsJLabelChangeTriggerNumber.gridy = 3;
			constraintsJLabelChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabelChangeTriggerNumber.weightx = .5;
			constraintsJLabelChangeTriggerNumber.ipady = 6;
			constraintsJLabelChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 3, 5);
			getJPanelChangeMethod().add(getJLabelChangeTriggerNumber(), constraintsJLabelChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerOffset.gridx = 3; constraintsJLabelChangeTriggerOffset.gridy = 3;
			constraintsJLabelChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJLabelChangeTriggerOffset.weightx = .5;
			constraintsJLabelChangeTriggerOffset.insets = new java.awt.Insets(8, 3, 3, 12);
			getJPanelChangeMethod().add(getJLabelChangeTriggerOffset(), constraintsJLabelChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJTextFieldChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJTextFieldChangeTriggerOffset.gridx = 4; constraintsJTextFieldChangeTriggerOffset.gridy = 3;
			constraintsJTextFieldChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJTextFieldChangeTriggerOffset.ipadx = 4;
			constraintsJTextFieldChangeTriggerOffset.insets = new java.awt.Insets(4, 3, 3, 8);
			getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(), constraintsJTextFieldChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeTriggerNumber.gridx = 2; constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
			constraintsJCSpinFieldChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangeTriggerNumber.ipadx = 4;
			constraintsJCSpinFieldChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 3, 1);
			getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(), constraintsJCSpinFieldChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelWhenChange = new java.awt.GridBagConstraints();
			constraintsJLabelWhenChange.gridx = 1; constraintsJLabelWhenChange.gridy = 1;
			constraintsJLabelWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelWhenChange.ipadx = 3;
			constraintsJLabelWhenChange.ipady = 4;
			constraintsJLabelWhenChange.insets = new java.awt.Insets(0, 5, 3, 5);
			getJPanelChangeMethod().add(getJLabelWhenChange(), constraintsJLabelWhenChange);

			java.awt.GridBagConstraints constraintsJComboBoxWhenChange = new java.awt.GridBagConstraints();
			constraintsJComboBoxWhenChange.gridx = 2; constraintsJComboBoxWhenChange.gridy = 1;
			constraintsJComboBoxWhenChange.gridwidth = 3;
			constraintsJComboBoxWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJComboBoxWhenChange.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxWhenChange.weightx = 1.0;
			constraintsJComboBoxWhenChange.ipadx = 4;
			constraintsJComboBoxWhenChange.insets = new java.awt.Insets(0, 3, 1, 8);
			getJPanelChangeMethod().add(getJComboBoxWhenChange(), constraintsJComboBoxWhenChange);
			// user code begin {1}
			jComboBoxWhenChange_ActionPerformed(null);
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
 * Return the JTextFieldChangeTriggerOffset property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldChangeTriggerOffset() {
	if (ivjJTextFieldChangeTriggerOffset == null) {
		try {
			ivjJTextFieldChangeTriggerOffset = new javax.swing.JTextField();
			ivjJTextFieldChangeTriggerOffset.setName("JTextFieldChangeTriggerOffset");
			ivjJTextFieldChangeTriggerOffset.setPreferredSize(new java.awt.Dimension(35, 20));
			ivjJTextFieldChangeTriggerOffset.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJTextFieldChangeTriggerOffset.setMaximumSize(new java.awt.Dimension(40, 20));
			ivjJTextFieldChangeTriggerOffset.setMinimumSize(new java.awt.Dimension(40, 20));
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
 * Return the JTextFieldRampInInterval property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldRampInInterval() {
	if (ivjJTextFieldRampInInterval == null) {
		try {
			ivjJTextFieldRampInInterval = new javax.swing.JTextField();
			ivjJTextFieldRampInInterval.setName("JTextFieldRampInInterval");
			ivjJTextFieldRampInInterval.setPreferredSize(new java.awt.Dimension(66, 20));
			ivjJTextFieldRampInInterval.setMaximumSize(new java.awt.Dimension(66, 20));
			ivjJTextFieldRampInInterval.setMinimumSize(new java.awt.Dimension(66, 20));
			// user code begin {1}
			ivjJTextFieldRampInInterval.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-99999, 99999) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldRampInInterval;
}
/**
 * Return the JTextFieldRampOutInterval property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldRampOutInterval() {
	if (ivjJTextFieldRampOutInterval == null) {
		try {
			ivjJTextFieldRampOutInterval = new javax.swing.JTextField();
			ivjJTextFieldRampOutInterval.setName("JTextFieldRampOutInterval");
			ivjJTextFieldRampOutInterval.setPreferredSize(new java.awt.Dimension(66, 20));
			ivjJTextFieldRampOutInterval.setMaximumSize(new java.awt.Dimension(66, 20));
			ivjJTextFieldRampOutInterval.setMinimumSize(new java.awt.Dimension(66, 20));
			// user code begin {1}
			ivjJTextFieldRampOutInterval.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-99999, 99999) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldRampOutInterval;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.db.device.lm.LMProgramDirectGear gear = null;
	
	gear = (LMProgramDirectGear)o;
	
	if( getJComboBoxHowToStop().getSelectedItem() != null )
	{
		if(getJComboBoxHowToStop().getSelectedItem().toString().compareTo("Ramp Out / Time In") == 0)
		{
			if(getJComboBoxStopOrder().getSelectedItem().toString().compareTo("First In First Out") == 0)
				gear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_FIFO);
			else
				gear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM);
			gear.setRampOutPercent(new Integer(((Number)getJCSpinFieldRampOutPercent().getValue()).intValue()));
			String interval = getJTextFieldRampOutInterval().getText();
			if(interval.compareTo("") == 0)
				gear.setRampOutInterval(new Integer(0));
			else
				gear.setRampOutInterval(new Integer(interval));
		}
		else if(getJComboBoxHowToStop().getSelectedItem().toString().compareTo("Ramp Out / Restore") == 0)
		{
			if(getJComboBoxStopOrder().getSelectedItem().toString().compareTo("First In First Out") == 0)
				gear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_FIFO_RESTORE);
			else
				gear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM_RESTORE);
			gear.setRampOutPercent(new Integer(((Number)getJCSpinFieldRampOutPercent().getValue()).intValue()));
			String interval = getJTextFieldRampOutInterval().getText();
			if(interval.compareTo("") == 0)
				gear.setRampOutInterval(new Integer(0));
			else
				gear.setRampOutInterval(new Integer(interval));
		}
		else
			gear.setMethodStopType( 
				com.cannontech.common.util.StringUtils.removeChars( ' ', getJComboBoxHowToStop().getSelectedItem().toString() ) );
	}
	
	if(getJCheckBoxRampIn().isSelected())
	{
		gear.setRampInPercent(new Integer(((Number)getJCSpinFieldRampInPercent().getValue()).intValue()));
        if( org.apache.commons.lang.StringUtils.isBlank(getJTextFieldRampInInterval().getText())) {
            getJTextFieldRampInInterval().setText("0");
        }
        gear.setRampInInterval(new Integer(getJTextFieldRampInInterval().getText()));	            
	}

	//This will need to be altered once serverside supports the strings for fixed shed times and dynamic shed times
	if(com.cannontech.common.util.StringUtils.removeChars( ' ', (String)getJComboBoxCycleCountSndType().getSelectedItem()).compareTo(LMProgramDirectGear.OPTION_DYNAMIC_SHED) == 0)
		gear.setMethodOptionType(LMProgramDirectGear.OPTION_COUNT_DOWN);
	else
		gear.setMethodOptionType(LMProgramDirectGear.OPTION_FIXED_COUNT);
	
	gear.setPercentReduction( new Integer( ((Number)getJCSpinFieldPercentReduction().getValue()).intValue() ) );
	
	gear.setChangeCondition( getChangeCondition(getJComboBoxWhenChange().getSelectedItem().toString()) );

	gear.setChangeDuration( new Integer( ((Number)getJCSpinFieldChangeDuration().getValue()).intValue() * 60 ) );
	gear.setChangePriority( new Integer( ((Number)getJCSpinFieldChangePriority().getValue()).intValue() ) );
	gear.setChangeTriggerNumber( new Integer( ((Number)getJCSpinFieldChangeTriggerNumber().getValue()).intValue() ) );
	
	if( getJTextFieldChangeTriggerOffset().getText() == null || getJTextFieldChangeTriggerOffset().getText().length() <= 0 )
		gear.setChangeTriggerOffset( new Double(0.0) );
	else
		gear.setChangeTriggerOffset( Double.valueOf(getJTextFieldChangeTriggerOffset().getText()) );

	com.cannontech.database.data.device.lm.TimeRefreshGear t = (com.cannontech.database.data.device.lm.TimeRefreshGear)gear;

	String shedTimeString = (String)(getJComboBoxShedTimeDigits().getSelectedItem()) + " " + (String)(getJComboBoxShedTimeUnits().getSelectedItem());	
    t.setShedTime(SwingUtil.getIntervalSecondsValue(shedTimeString));


	t.setNumberOfGroups( getJComboBoxNumGroups().getSelectedItem() );

	String sendRateString = (String)(getJComboBoxSendRateDigits().getSelectedItem()) + " " + (String)(getJComboBoxSendRateUnits().getSelectedItem());	
    t.setRefreshRate(SwingUtil.getIntervalSecondsValue(sendRateString));

	t.setGroupSelectionMethod( StringUtils.removeChars( ' ', getJComboBoxGroupSelection().getSelectedItem().toString() ) );
		
	return t;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
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
	getJCSpinFieldPercentReduction().addValueListener(this);
	getJCSpinFieldRampOutPercent().addValueListener(this);
	getJCSpinFieldRampInPercent().addValueListener(this);
	getJComboBoxWhenChange().addActionListener(this);
	
	getJComboBoxShedTimeDigits().addActionListener(this);
	getJComboBoxShedTimeUnits().addActionListener(this);
	getJComboBoxNumGroups().addActionListener(this);
	getJComboBoxSendRateDigits().addActionListener(this);
	getJComboBoxSendRateUnits().addActionListener(this);
	getJComboBoxGroupSelection().addActionListener(this);
	getJComboBoxHowToStop().addActionListener(this);
	getJComboBoxStopOrder().addActionListener(this);
	getJTextFieldChangeTriggerOffset().addCaretListener(this);
	getJTextFieldRampOutInterval().addCaretListener(this);
	
	// user code end
	getJCheckBoxRampIn().addActionListener(ivjEventHandler);
	getJComboBoxWhenChange().addActionListener(ivjEventHandler);
	getJComboBoxCycleCountSndType().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TimeRefreshGearPanel");
		setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
		setLayout(new java.awt.GridBagLayout());
		setPreferredSize(new java.awt.Dimension(402, 430));
		setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		setSize(402, 466);
		setMinimumSize(new java.awt.Dimension(402, 430));

		java.awt.GridBagConstraints constraintsJLabelShedTime = new java.awt.GridBagConstraints();
		constraintsJLabelShedTime.gridx = 1; constraintsJLabelShedTime.gridy = 2;
		constraintsJLabelShedTime.ipadx = 51;
		constraintsJLabelShedTime.insets = new java.awt.Insets(7, 10, 5, 1);
		add(getJLabelShedTime(), constraintsJLabelShedTime);

		java.awt.GridBagConstraints constraintsJLabelNumGroups = new java.awt.GridBagConstraints();
		constraintsJLabelNumGroups.gridx = 1; constraintsJLabelNumGroups.gridy = 3;
		constraintsJLabelNumGroups.ipadx = 17;
		constraintsJLabelNumGroups.insets = new java.awt.Insets(7, 10, 5, 1);
		add(getJLabelNumGroups(), constraintsJLabelNumGroups);

		java.awt.GridBagConstraints constraintsJComboBoxNumGroups = new java.awt.GridBagConstraints();
		constraintsJComboBoxNumGroups.gridx = 2; constraintsJComboBoxNumGroups.gridy = 3;
		constraintsJComboBoxNumGroups.gridwidth = 2;
		constraintsJComboBoxNumGroups.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxNumGroups.weightx = 1.0;
		constraintsJComboBoxNumGroups.ipadx = 1;
		constraintsJComboBoxNumGroups.insets = new java.awt.Insets(2, 1, 3, 97);
		add(getJComboBoxNumGroups(), constraintsJComboBoxNumGroups);

		java.awt.GridBagConstraints constraintsJLabelSendRate = new java.awt.GridBagConstraints();
		constraintsJLabelSendRate.gridx = 1; constraintsJLabelSendRate.gridy = 4;
		constraintsJLabelSendRate.ipadx = 8;
		constraintsJLabelSendRate.insets = new java.awt.Insets(7, 9, 5, 2);
		add(getJLabelSendRate(), constraintsJLabelSendRate);

		java.awt.GridBagConstraints constraintsJLabelGroupSelection = new java.awt.GridBagConstraints();
		constraintsJLabelGroupSelection.gridx = 1; constraintsJLabelGroupSelection.gridy = 5;
		constraintsJLabelGroupSelection.ipadx = 12;
		constraintsJLabelGroupSelection.insets = new java.awt.Insets(7, 10, 5, 1);
		add(getJLabelGroupSelection(), constraintsJLabelGroupSelection);

		java.awt.GridBagConstraints constraintsJComboBoxGroupSelection = new java.awt.GridBagConstraints();
		constraintsJComboBoxGroupSelection.gridx = 2; constraintsJComboBoxGroupSelection.gridy = 5;
		constraintsJComboBoxGroupSelection.gridwidth = 2;
		constraintsJComboBoxGroupSelection.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxGroupSelection.weightx = 1.0;
		constraintsJComboBoxGroupSelection.ipadx = 1;
		constraintsJComboBoxGroupSelection.insets = new java.awt.Insets(3, 1, 2, 59);
		add(getJComboBoxGroupSelection(), constraintsJComboBoxGroupSelection);

		java.awt.GridBagConstraints constraintsJLabelHowToStop = new java.awt.GridBagConstraints();
		constraintsJLabelHowToStop.gridx = 1; constraintsJLabelHowToStop.gridy = 9;
		constraintsJLabelHowToStop.ipadx = 4;
		constraintsJLabelHowToStop.insets = new java.awt.Insets(9, 10, 3, 31);
		add(getJLabelHowToStop(), constraintsJLabelHowToStop);

		java.awt.GridBagConstraints constraintsJLabelPercentReduction = new java.awt.GridBagConstraints();
		constraintsJLabelPercentReduction.gridx = 1; constraintsJLabelPercentReduction.gridy = 13;
		constraintsJLabelPercentReduction.ipadx = 4;
		constraintsJLabelPercentReduction.insets = new java.awt.Insets(9, 10, 1, 1);
		add(getJLabelPercentReduction(), constraintsJLabelPercentReduction);

		java.awt.GridBagConstraints constraintsJComboBoxHowToStop = new java.awt.GridBagConstraints();
		constraintsJComboBoxHowToStop.gridx = 2; constraintsJComboBoxHowToStop.gridy = 9;
		constraintsJComboBoxHowToStop.gridwidth = 2;
		constraintsJComboBoxHowToStop.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxHowToStop.weightx = 1.0;
		constraintsJComboBoxHowToStop.ipadx = 2;
		constraintsJComboBoxHowToStop.insets = new java.awt.Insets(4, 1, 1, 58);
		add(getJComboBoxHowToStop(), constraintsJComboBoxHowToStop);

		java.awt.GridBagConstraints constraintsJCSpinFieldPercentReduction = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldPercentReduction.gridx = 2; constraintsJCSpinFieldPercentReduction.gridy = 13;
		constraintsJCSpinFieldPercentReduction.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCSpinFieldPercentReduction.weightx = 1.0;
		constraintsJCSpinFieldPercentReduction.gridwidth = 2;
		constraintsJCSpinFieldPercentReduction.ipadx = 2;
		constraintsJCSpinFieldPercentReduction.insets = new java.awt.Insets(4, 1, 1, 58);
		add(getJCSpinFieldPercentReduction(), constraintsJCSpinFieldPercentReduction);

		java.awt.GridBagConstraints constraintsJPanelChangeMethod = new java.awt.GridBagConstraints();
		constraintsJPanelChangeMethod.gridx = 1; constraintsJPanelChangeMethod.gridy = 14;
		constraintsJPanelChangeMethod.gridwidth = 3;
		constraintsJPanelChangeMethod.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelChangeMethod.weightx = 1.0;
		constraintsJPanelChangeMethod.weighty = 1.0;
		constraintsJPanelChangeMethod.ipadx = 10;
		constraintsJPanelChangeMethod.insets = new java.awt.Insets(5, 7, 1, 55);
		add(getJPanelChangeMethod(), constraintsJPanelChangeMethod);

		java.awt.GridBagConstraints constraintsJComboBoxSendRateDigits = new java.awt.GridBagConstraints();
		constraintsJComboBoxSendRateDigits.gridx = 2; constraintsJComboBoxSendRateDigits.gridy = 4;
		constraintsJComboBoxSendRateDigits.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxSendRateDigits.weightx = .5;
		constraintsJComboBoxSendRateDigits.ipadx = 5;
		constraintsJComboBoxSendRateDigits.insets = new java.awt.Insets(3, 1, 2, 1);
		add(getJComboBoxSendRateDigits(), constraintsJComboBoxSendRateDigits);

		java.awt.GridBagConstraints constraintsJComboBoxSendRateUnits = new java.awt.GridBagConstraints();
		constraintsJComboBoxSendRateUnits.gridx = 3; constraintsJComboBoxSendRateUnits.gridy = 4;
		constraintsJComboBoxSendRateUnits.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxSendRateUnits.weightx = .5;
		constraintsJComboBoxSendRateUnits.insets = new java.awt.Insets(3, 1, 2, 58);
		add(getJComboBoxSendRateUnits(), constraintsJComboBoxSendRateUnits);

		java.awt.GridBagConstraints constraintsJComboBoxCycleCountSndType = new java.awt.GridBagConstraints();
		constraintsJComboBoxCycleCountSndType.gridx = 2; constraintsJComboBoxCycleCountSndType.gridy = 1;
		constraintsJComboBoxCycleCountSndType.gridwidth = 2;
		constraintsJComboBoxCycleCountSndType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxCycleCountSndType.weightx = 1.0;
		constraintsJComboBoxCycleCountSndType.ipadx = 1;
		constraintsJComboBoxCycleCountSndType.insets = new java.awt.Insets(8, 1, 2, 97);
		add(getJComboBoxCycleCountSndType(), constraintsJComboBoxCycleCountSndType);

		java.awt.GridBagConstraints constraintsJLabelCycleCntSndType = new java.awt.GridBagConstraints();
		constraintsJLabelCycleCntSndType.gridx = 1; constraintsJLabelCycleCntSndType.gridy = 1;
		constraintsJLabelCycleCntSndType.ipadx = 22;
		constraintsJLabelCycleCntSndType.insets = new java.awt.Insets(11, 10, 6, 17);
		add(getJLabelCycleCntSndType(), constraintsJLabelCycleCntSndType);

		java.awt.GridBagConstraints constraintsJCSpinFieldRampOutPercent = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldRampOutPercent.gridx = 2; constraintsJCSpinFieldRampOutPercent.gridy = 11;
        constraintsJCSpinFieldRampOutPercent.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsJCSpinFieldRampOutPercent.weightx = .5;
		constraintsJCSpinFieldRampOutPercent.ipadx = 3;
		constraintsJCSpinFieldRampOutPercent.insets = new java.awt.Insets(3, 1, 2, 1);
		add(getJCSpinFieldRampOutPercent(), constraintsJCSpinFieldRampOutPercent);

		java.awt.GridBagConstraints constraintsJLabelRampOutPercent = new java.awt.GridBagConstraints();
		constraintsJLabelRampOutPercent.gridx = 1; constraintsJLabelRampOutPercent.gridy = 11;
        constraintsJLabelRampOutPercent.insets = new java.awt.Insets(6, 10, 2, 28);
		add(getJLabelRampOutPercent(), constraintsJLabelRampOutPercent);

		java.awt.GridBagConstraints constraintsJLabelRampOutPercentSign = new java.awt.GridBagConstraints();
		constraintsJLabelRampOutPercentSign.gridx = 3; constraintsJLabelRampOutPercentSign.gridy = 11;
		constraintsJLabelRampOutPercentSign.ipadx = 4;
        constraintsJLabelRampOutPercentSign.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsJLabelRampOutPercentSign.weightx = .5;
        constraintsJLabelRampOutPercentSign.insets = new java.awt.Insets(3, 1, 2, 58);
		add(getJLabelRampOutPercentSign(), constraintsJLabelRampOutPercentSign);

		java.awt.GridBagConstraints constraintsJTextFieldRampOutInterval = new java.awt.GridBagConstraints();
		constraintsJTextFieldRampOutInterval.gridx = 2; constraintsJTextFieldRampOutInterval.gridy = 12;
		constraintsJTextFieldRampOutInterval.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldRampOutInterval.weightx = .5;
		constraintsJTextFieldRampOutInterval.ipadx = 3;
		constraintsJTextFieldRampOutInterval.insets = new java.awt.Insets(3, 1, 2, 1);
		add(getJTextFieldRampOutInterval(), constraintsJTextFieldRampOutInterval);

		java.awt.GridBagConstraints constraintsJLabelRampOutSec = new java.awt.GridBagConstraints();
		constraintsJLabelRampOutSec.gridx = 3; constraintsJLabelRampOutSec.gridy = 12;
		constraintsJLabelRampOutSec.ipadx = 4;
        constraintsJLabelRampOutSec.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsJLabelRampOutSec.weightx = .5;
        constraintsJLabelRampOutSec.insets = new java.awt.Insets(3, 1, 2, 58);
		add(getJLabelRampOutSec(), constraintsJLabelRampOutSec);

		java.awt.GridBagConstraints constraintsJLabelRampOutInterval = new java.awt.GridBagConstraints();
		constraintsJLabelRampOutInterval.gridx = 1; constraintsJLabelRampOutInterval.gridy = 12;
		constraintsJLabelRampOutInterval.insets = new java.awt.Insets(6, 10, 2, 28);
		add(getJLabelRampOutInterval(), constraintsJLabelRampOutInterval);

		java.awt.GridBagConstraints constraintsJCheckBoxRampIn = new java.awt.GridBagConstraints();
		constraintsJCheckBoxRampIn.gridx = 1; constraintsJCheckBoxRampIn.gridy = 6;
		constraintsJCheckBoxRampIn.ipadx = 53;
		constraintsJCheckBoxRampIn.ipady = -2;
		constraintsJCheckBoxRampIn.insets = new java.awt.Insets(3, 9, 1, 23);
		add(getJCheckBoxRampIn(), constraintsJCheckBoxRampIn);

		java.awt.GridBagConstraints constraintsJLabelRampInInterval = new java.awt.GridBagConstraints();
		constraintsJLabelRampInInterval.gridx = 1; constraintsJLabelRampInInterval.gridy = 8;
		constraintsJLabelRampInInterval.insets = new java.awt.Insets(3, 10, 2, 28);
		add(getJLabelRampInInterval(), constraintsJLabelRampInInterval);

		java.awt.GridBagConstraints constraintsJTextFieldRampInInterval = new java.awt.GridBagConstraints();
		constraintsJTextFieldRampInInterval.gridx = 2; constraintsJTextFieldRampInInterval.gridy = 8;
		constraintsJTextFieldRampInInterval.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldRampInInterval.weightx = .5;
		constraintsJTextFieldRampInInterval.ipadx = 3;
		constraintsJTextFieldRampInInterval.insets = new java.awt.Insets(3, 1, 2, 1);
		add(getJTextFieldRampInInterval(), constraintsJTextFieldRampInInterval);

		java.awt.GridBagConstraints constraintsJLabelRampInSec = new java.awt.GridBagConstraints();
		constraintsJLabelRampInSec.gridx = 3; constraintsJLabelRampInSec.gridy = 8;
		constraintsJLabelRampInSec.ipadx = 4;
		constraintsJLabelRampInSec.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelRampInSec.weightx = .5;
		constraintsJLabelRampInSec.insets = new java.awt.Insets(3, 1, 2, 58);
		add(getJLabelRampInSec(), constraintsJLabelRampInSec);

		java.awt.GridBagConstraints constraintsJLabelRampInPercentSign = new java.awt.GridBagConstraints();
		constraintsJLabelRampInPercentSign.gridx = 3; constraintsJLabelRampInPercentSign.gridy = 7;
		constraintsJLabelRampInPercentSign.ipadx = 4;
		constraintsJLabelRampInPercentSign.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsJLabelRampInPercentSign.weightx = .5;
		constraintsJLabelRampInPercentSign.insets = new java.awt.Insets(3, 1, 2, 58);
		add(getJLabelRampInPercentSign(), constraintsJLabelRampInPercentSign);

		java.awt.GridBagConstraints constraintsJCSpinFieldRampInPercent = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldRampInPercent.gridx = 2; constraintsJCSpinFieldRampInPercent.gridy = 7;
		constraintsJCSpinFieldRampInPercent.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCSpinFieldRampInPercent.weightx = .5;
		constraintsJCSpinFieldRampInPercent.ipadx = 3;
		constraintsJCSpinFieldRampInPercent.insets = new java.awt.Insets(3, 1, 2, 1);
		add(getJCSpinFieldRampInPercent(), constraintsJCSpinFieldRampInPercent);

		java.awt.GridBagConstraints constraintsJLabelRampInPercent = new java.awt.GridBagConstraints();
		constraintsJLabelRampInPercent.gridx = 1; constraintsJLabelRampInPercent.gridy = 7;
		constraintsJLabelRampInPercent.insets = new java.awt.Insets(3, 10, 4, 28);
		add(getJLabelRampInPercent(), constraintsJLabelRampInPercent);

		java.awt.GridBagConstraints constraintsJComboBoxShedTimeDigits = new java.awt.GridBagConstraints();
		constraintsJComboBoxShedTimeDigits.gridx = 2; constraintsJComboBoxShedTimeDigits.gridy = 2;
		constraintsJComboBoxShedTimeDigits.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxShedTimeDigits.weightx = 1;
		constraintsJComboBoxShedTimeDigits.ipadx = 5;
		constraintsJComboBoxShedTimeDigits.insets = new java.awt.Insets(3, 1, 2, 1);
		add(getJComboBoxShedTimeDigits(), constraintsJComboBoxShedTimeDigits);

		java.awt.GridBagConstraints constraintsJComboBoxShedTimeUnits = new java.awt.GridBagConstraints();
		constraintsJComboBoxShedTimeUnits.gridx = 3; constraintsJComboBoxShedTimeUnits.gridy = 2;
		constraintsJComboBoxShedTimeUnits.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxShedTimeUnits.weightx = 1;
		constraintsJComboBoxShedTimeUnits.insets = new java.awt.Insets(3, 1, 2, 58);
		add(getJComboBoxShedTimeUnits(), constraintsJComboBoxShedTimeUnits);

		java.awt.GridBagConstraints constraintsJComboBoxStopOrder = new java.awt.GridBagConstraints();
		constraintsJComboBoxStopOrder.gridx = 2; constraintsJComboBoxStopOrder.gridy = 10;
		constraintsJComboBoxStopOrder.gridwidth = 2;
		constraintsJComboBoxStopOrder.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxStopOrder.weightx = 1.0;
		constraintsJComboBoxStopOrder.ipadx = 2;
		constraintsJComboBoxStopOrder.insets = new java.awt.Insets(2, 1, 2, 58);
		add(getJComboBoxStopOrder(), constraintsJComboBoxStopOrder);

		java.awt.GridBagConstraints constraintsJLabelStopOrder = new java.awt.GridBagConstraints();
		constraintsJLabelStopOrder.gridx = 1; constraintsJLabelStopOrder.gridy = 10;
		constraintsJLabelStopOrder.ipadx = 54;
		constraintsJLabelStopOrder.insets = new java.awt.Insets(5, 10, 2, 31);
		add(getJLabelStopOrder(), constraintsJLabelStopOrder);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	rampItOut(false);
	rampItIn(false);
	
	getJComboBoxWhenChange().setSelectedItem( LMProgramDirectGear.CHANGE_NONE );
	getJComboBoxShedTimeDigits().setSelectedItem("1");
	getJComboBoxShedTimeUnits().setSelectedItem("hours");
	getJComboBoxSendRateDigits().setSelectedItem("30");
	getJComboBoxSendRateUnits().setSelectedItem("minutes");
		try
	{
		initConnections();
	}
	catch(Exception e)	{ }
	// user code end
}
/**
 * Comment
 */
public void jCheckBoxRampIn_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	rampItIn(getJCheckBoxRampIn().isSelected());
	this.fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jComboBoxCycleCountSndType_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	if(LMProgramDirectGear.OPTION_DYNAMIC_SHED.compareTo(com.cannontech.common.util.StringUtils.removeChars( ' ', (String)getJComboBoxCycleCountSndType().getSelectedItem())) == 0)
	{
		getJLabelShedTime().setText("Max Shed Time   ");
	}
	else
	{
		getJLabelShedTime().setText("Fixed Shed Time");
	}
	
	fireInputUpdate();
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
		TimeRefreshGearPanel aTimeRefreshGearPanel;
		aTimeRefreshGearPanel = new TimeRefreshGearPanel();
		frame.setContentPane(aTimeRefreshGearPanel);
		frame.setSize(aTimeRefreshGearPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
public void rampItIn(boolean rampOrNotToRamp)
{
	getJLabelRampInInterval().setVisible(rampOrNotToRamp);
	getJLabelRampInPercent().setVisible(rampOrNotToRamp);
	getJLabelRampInPercentSign().setVisible(rampOrNotToRamp);
	getJLabelRampInSec().setVisible(rampOrNotToRamp);
	getJCSpinFieldRampInPercent().setVisible(rampOrNotToRamp);
	getJTextFieldRampInInterval().setVisible(rampOrNotToRamp);
}
public void rampItOut(boolean rampOrNotToRamp)
{
	getJLabelRampOutInterval().setVisible(rampOrNotToRamp);
	getJLabelRampOutPercent().setVisible(rampOrNotToRamp);
	getJLabelRampOutPercentSign().setVisible(rampOrNotToRamp);
	getJLabelRampOutSec().setVisible(rampOrNotToRamp);
	getJCSpinFieldRampOutPercent().setVisible(rampOrNotToRamp);
	getJTextFieldRampOutInterval().setVisible(rampOrNotToRamp);
	getJLabelStopOrder().setVisible(rampOrNotToRamp);
	getJComboBoxStopOrder().setVisible(rampOrNotToRamp);
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
* @return
*/

/**
* @param box
*/
public void setJComboBoxCycleCountSndType(javax.swing.JComboBox box) 
{
	ivjJComboBoxCycleCountSndType = box;
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	LMProgramDirectGear gear = null;
	
	if( o == null )
	{
		return;
	}
	else
		gear = (LMProgramDirectGear)o;
	
	if(gear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_FIFO) == 0)
	{
		getJComboBoxHowToStop().setSelectedItem( "Ramp Out / Time In" );
		getJComboBoxStopOrder().setSelectedItem( "First In First Out");
		getJCSpinFieldRampOutPercent().setValue(gear.getRampOutPercent());
		getJTextFieldRampOutInterval().setText(gear.getRampOutInterval().toString());
	}
	else if(gear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM) == 0)
	{
		getJComboBoxHowToStop().setSelectedItem( "Ramp Out / Time In" );
		getJComboBoxStopOrder().setSelectedItem( "Random" );
		getJCSpinFieldRampOutPercent().setValue(gear.getRampOutPercent());
		getJTextFieldRampOutInterval().setText(gear.getRampOutInterval().toString());
	}
	else if(gear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_FIFO_RESTORE) == 0)
	{
		getJComboBoxHowToStop().setSelectedItem( "Ramp Out / Restore" );
		getJComboBoxStopOrder().setSelectedItem( "First In First Out");
		getJCSpinFieldRampOutPercent().setValue(gear.getRampOutPercent());
		getJTextFieldRampOutInterval().setText(gear.getRampOutInterval().toString());
	}
	else if(gear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM_RESTORE) == 0)
	{
		getJComboBoxHowToStop().setSelectedItem( "Ramp Out / Restore" );
		getJComboBoxStopOrder().setSelectedItem( "Random" );
		getJCSpinFieldRampOutPercent().setValue(gear.getRampOutPercent());
		getJTextFieldRampOutInterval().setText(gear.getRampOutInterval().toString());
	}
	else
		getJComboBoxHowToStop().setSelectedItem( StringUtils.addCharBetweenWords( ' ', gear.getMethodStopType() ) );

	if(gear.getRampInPercent().intValue() != 0 && gear.getRampInInterval().intValue() != 0)
	{
		getJCheckBoxRampIn().setSelected(true);
		rampItIn(true);
		getJCSpinFieldRampInPercent().setValue(gear.getRampInPercent());
		getJTextFieldRampInInterval().setText(gear.getRampInInterval().toString());
	}

	getJCSpinFieldPercentReduction().setValue( gear.getPercentReduction() );
	
	setChangeCondition( gear.getChangeCondition() );
	getJCSpinFieldChangeDuration().setValue( new Integer( gear.getChangeDuration().intValue() / 60 ) );
	getJCSpinFieldChangePriority().setValue( gear.getChangePriority() );
	getJCSpinFieldChangeTriggerNumber().setValue( gear.getChangeTriggerNumber() );	
	getJTextFieldChangeTriggerOffset().setText( gear.getChangeTriggerOffset().toString() );

	com.cannontech.database.data.device.lm.TimeRefreshGear t = (com.cannontech.database.data.device.lm.TimeRefreshGear)gear;

	//This will need to be altered once serverside supports the strings for fixed shed times and dynamic shed times
	if(t.getMethodOptionType().compareTo(LMProgramDirectGear.OPTION_COUNT_DOWN) == 0)
	{
		getJLabelShedTime().setText("Max Shed Time   ");
		SwingUtil.setIntervalComboBoxSelectedItem( 
			getJComboBoxShedTimeDigits(), getJComboBoxShedTimeUnits(), t.getShedTime().intValue() );
	}
	else
	{
	    SwingUtil.setIntervalComboBoxSelectedItem( 
			getJComboBoxShedTimeDigits(), getJComboBoxShedTimeUnits(), t.getShedTime().intValue() );
	}
	
    getJComboBoxNumGroups().setSelectedIndex( t.getNumberOfGroups() );
	
	//This will need to be altered once serverside supports the strings for fixed shed times and dynamic shed times
	if(t.getMethodOptionType().compareTo(LMProgramDirectGear.OPTION_COUNT_DOWN) == 0)
		getJComboBoxCycleCountSndType().setSelectedItem(StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.OPTION_DYNAMIC_SHED));
	else
		getJComboBoxCycleCountSndType().setSelectedItem(StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.OPTION_FIXED_SHED));

	SwingUtil.setIntervalComboBoxSelectedItem( 
			getJComboBoxSendRateDigits(), getJComboBoxSendRateUnits(), t.getRefreshRate().intValue() );
		
	getJComboBoxGroupSelection().setSelectedItem( StringUtils.addCharBetweenWords( ' ', t.getGroupSelectionMethod() ) );
	
	
}
/**
 * valueChanged method comment.
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
/**
 * valueChanging method comment.
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {}
}
