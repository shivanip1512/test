package com.cannontech.dbeditor.wizard.device.lmprogram;
/**
 * Insert the type's description here.
 * Creation date: (8/1/2002 3:25:06 PM)
 * @author: 
 */

import javax.swing.JComboBox;

import com.cannontech.common.util.StringUtils;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
 
public class RotationGearPanel extends GenericGearPanel {
	private javax.swing.JComboBox ivjJComboBoxGroupSelection = null;
	private javax.swing.JComboBox ivjJComboBoxHowToStop = null;
	private javax.swing.JComboBox ivjJComboBoxNumGroups = null;
	private javax.swing.JComboBox ivjJComboBoxShedTime = null;
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
	private javax.swing.JLabel ivjJLabelShedTime = null;
	private javax.swing.JLabel ivjJLabelSendRate = null;
	private javax.swing.JLabel ivjJLabelWhenChange = null;
	private javax.swing.JPanel ivjJPanelChangeMethod = null;
	private javax.swing.JTextField ivjJTextFieldChangeTriggerOffset = null;
	private javax.swing.JComboBox ivjJComboBoxSendRateDigits = null;
	private javax.swing.JComboBox ivjJComboBoxSendRateUnits = null;

/**
 * RotationGearPanel constructor comment.
 */
public RotationGearPanel() {
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
	if (e.getSource() == getJComboBoxWhenChange()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxShedTime()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxNumGroups()) 
		connEtoC5(e);
	if (e.getSource() == getJComboBoxSendRateDigits() || e.getSource() == getJComboBoxSendRateUnits()) 
		connEtoC7(e);
	if (e.getSource() == getJComboBoxGroupSelection()) 
		connEtoC8(e);
	if (e.getSource() == getJComboBoxHowToStop()) 
		connEtoC10(e);
	// user code end
	
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
 * Return the JComboBoxGroupSelection property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGroupSelection() {
	if (ivjJComboBoxGroupSelection == null) {
		try {
			ivjJComboBoxGroupSelection = new javax.swing.JComboBox();
			ivjJComboBoxGroupSelection.setName("JComboBoxGroupSelection");
			ivjJComboBoxGroupSelection.setPreferredSize(new java.awt.Dimension(175, 23));
			ivjJComboBoxGroupSelection.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxGroupSelection.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxGroupSelection.setMinimumSize(new java.awt.Dimension(126, 23));
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
			ivjJComboBoxHowToStop.setPreferredSize(new java.awt.Dimension(175, 23));
			ivjJComboBoxHowToStop.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxHowToStop.setMinimumSize(new java.awt.Dimension(126, 23));
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
 * Return the JComboBoxNumGroups property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxNumGroups() {
	if (ivjJComboBoxNumGroups == null) {
		try {
			ivjJComboBoxNumGroups = new javax.swing.JComboBox();
			ivjJComboBoxNumGroups.setName("JComboBoxNumGroups");
			ivjJComboBoxNumGroups.setPreferredSize(new java.awt.Dimension(137, 23));
			ivjJComboBoxNumGroups.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxNumGroups.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxNumGroups.setMinimumSize(new java.awt.Dimension(0, 0));
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
private javax.swing.JComboBox getJComboBoxSendRateDigits() {
	if (ivjJComboBoxSendRateDigits == null) {
		try {
			ivjJComboBoxSendRateDigits = new javax.swing.JComboBox();
			ivjJComboBoxSendRateDigits.setName("JComboBoxSendRateDigits");
			ivjJComboBoxSendRateDigits.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxSendRateDigits.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			ivjJComboBoxSendRateDigits.setPreferredSize(new java.awt.Dimension(175, 23));
			ivjJComboBoxSendRateDigits.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxSendRateDigits.setMinimumSize(new java.awt.Dimension(0, 0));
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
			ivjJComboBoxSendRateUnits.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			ivjJComboBoxSendRateUnits.setPreferredSize(new java.awt.Dimension(175, 23));
			ivjJComboBoxSendRateUnits.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxSendRateUnits.setMinimumSize(new java.awt.Dimension(0, 0));
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

/**
 * Return the JComboBoxShedTime property value.
 * @return javax.swing.JComboBox
 */
private JComboBox<String> getJComboBoxShedTime() {
	if (ivjJComboBoxShedTime == null) {
		try {
			ivjJComboBoxShedTime = new JComboBox<>();
			ivjJComboBoxShedTime.setName("JComboBoxShedTime");
			ivjJComboBoxShedTime.setPreferredSize(new java.awt.Dimension(137, 23));
			ivjJComboBoxShedTime.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxShedTime.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxShedTime.setMinimumSize(new java.awt.Dimension(0, 0));
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
			ivjJComboBoxWhenChange.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJCSpinFieldChangeDuration.setPreferredSize(new java.awt.Dimension(32, 20));
			ivjJCSpinFieldChangeDuration.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJCSpinFieldChangePriority.setPreferredSize(new java.awt.Dimension(30, 20));
			ivjJCSpinFieldChangePriority.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldChangePriority.setMaximumSize(new java.awt.Dimension(30, 20));
			ivjJCSpinFieldChangePriority.setMinimumSize(new java.awt.Dimension(0, 0));
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
			ivjJCSpinFieldChangeTriggerNumber.setPreferredSize(new java.awt.Dimension(32, 20));
			ivjJCSpinFieldChangeTriggerNumber.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldChangeTriggerNumber.setMaximumSize(new java.awt.Dimension(40, 20));
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
			ivjJCSpinFieldPercentReduction.setMaximumSize(new java.awt.Dimension(90, 30));
			ivjJCSpinFieldPercentReduction.setMinimumSize(new java.awt.Dimension(40, 50));
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
			ivjJLabelChangeDuration.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJLabelChangePriority.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
			ivjJLabelShedTime.setText("Shed Time:");
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
			//ivjJPanelChangeMethod.setBorder(new com.ibm.uvm.abt.edit.DeletedClassView());
			ivjJPanelChangeMethod.setLayout(new java.awt.GridBagLayout());
			ivjJPanelChangeMethod.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJPanelChangeMethod.setPreferredSize(new java.awt.Dimension(335, 87));
			ivjJPanelChangeMethod.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJPanelChangeMethod.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJPanelChangeMethod.setMinimumSize(new java.awt.Dimension(0, 0));

			java.awt.GridBagConstraints constraintsJLabelChangeDuration = new java.awt.GridBagConstraints();
			constraintsJLabelChangeDuration.gridx = 1; constraintsJLabelChangeDuration.gridy = 2;
			constraintsJLabelChangeDuration.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeDuration.ipadx = -5;
			constraintsJLabelChangeDuration.ipady = 6;
			constraintsJLabelChangeDuration.insets = new java.awt.Insets(1, 5, 3, 5);
			getJPanelChangeMethod().add(getJLabelChangeDuration(), constraintsJLabelChangeDuration);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeDuration = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeDuration.gridx = 2; constraintsJCSpinFieldChangeDuration.gridy = 2;
			constraintsJCSpinFieldChangeDuration.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangeDuration.ipadx = 31;
			constraintsJCSpinFieldChangeDuration.ipady = 19;
			constraintsJCSpinFieldChangeDuration.insets = new java.awt.Insets(1, 5, 3, 3);
			getJPanelChangeMethod().add(getJCSpinFieldChangeDuration(), constraintsJCSpinFieldChangeDuration);

			java.awt.GridBagConstraints constraintsJLabelMinutesChDur = new java.awt.GridBagConstraints();
			constraintsJLabelMinutesChDur.gridx = 3; constraintsJLabelMinutesChDur.gridy = 2;
			constraintsJLabelMinutesChDur.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelMinutesChDur.ipadx = 5;
			constraintsJLabelMinutesChDur.ipady = -2;
			constraintsJLabelMinutesChDur.insets = new java.awt.Insets(5, 3, 5, 3);
			getJPanelChangeMethod().add(getJLabelMinutesChDur(), constraintsJLabelMinutesChDur);

			java.awt.GridBagConstraints constraintsJLabelChangePriority = new java.awt.GridBagConstraints();
			constraintsJLabelChangePriority.gridx = 4; constraintsJLabelChangePriority.gridy = 2;
			constraintsJLabelChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangePriority.ipadx = -13;
			constraintsJLabelChangePriority.ipady = 6;
			constraintsJLabelChangePriority.insets = new java.awt.Insets(1, 4, 3, 5);
			getJPanelChangeMethod().add(getJLabelChangePriority(), constraintsJLabelChangePriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangePriority = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangePriority.gridx = 5; constraintsJCSpinFieldChangePriority.gridy = 2;
			constraintsJCSpinFieldChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangePriority.fill = java.awt.GridBagConstraints.NONE;
			constraintsJCSpinFieldChangePriority.ipadx = 0;
			constraintsJCSpinFieldChangePriority.ipady = 0;
			constraintsJCSpinFieldChangePriority.insets = new java.awt.Insets(1, 5, 3, 10);
			getJPanelChangeMethod().add(getJCSpinFieldChangePriority(), constraintsJCSpinFieldChangePriority);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerNumber.gridx = 1; constraintsJLabelChangeTriggerNumber.gridy = 3;
			constraintsJLabelChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerNumber.ipadx = -45;
			constraintsJLabelChangeTriggerNumber.ipady = 6;
			constraintsJLabelChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 15, 5);
			getJPanelChangeMethod().add(getJLabelChangeTriggerNumber(), constraintsJLabelChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerOffset.gridx = 4; constraintsJLabelChangeTriggerOffset.gridy = 3;
			constraintsJLabelChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerOffset.ipadx = -63;
			constraintsJLabelChangeTriggerOffset.insets = new java.awt.Insets(8, 4, 17, 15);
			getJPanelChangeMethod().add(getJLabelChangeTriggerOffset(), constraintsJLabelChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJTextFieldChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJTextFieldChangeTriggerOffset.gridx = 5; constraintsJTextFieldChangeTriggerOffset.gridy = 3;
			constraintsJTextFieldChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJTextFieldChangeTriggerOffset.weightx = 1.0;
			constraintsJTextFieldChangeTriggerOffset.ipadx = 30;
			constraintsJTextFieldChangeTriggerOffset.ipady = 20;
			constraintsJTextFieldChangeTriggerOffset.insets = new java.awt.Insets(4, 5, 15, 10);
			getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(), constraintsJTextFieldChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeTriggerNumber.gridx = 2; constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
			constraintsJCSpinFieldChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangeTriggerNumber.ipadx = 31;
			constraintsJCSpinFieldChangeTriggerNumber.ipady = 19;
			constraintsJCSpinFieldChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 15, 3);
			getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(), constraintsJCSpinFieldChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelWhenChange = new java.awt.GridBagConstraints();
			constraintsJLabelWhenChange.gridx = 1; constraintsJLabelWhenChange.gridy = 1;
			constraintsJLabelWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelWhenChange.ipadx = 3;
			constraintsJLabelWhenChange.ipady = 4;
			constraintsJLabelWhenChange.insets = new java.awt.Insets(0, 5, 4, 5);
			getJPanelChangeMethod().add(getJLabelWhenChange(), constraintsJLabelWhenChange);

			java.awt.GridBagConstraints constraintsJComboBoxWhenChange = new java.awt.GridBagConstraints();
			constraintsJComboBoxWhenChange.gridx = 4; constraintsJComboBoxWhenChange.gridy = 1;
			constraintsJComboBoxWhenChange.gridwidth = 2;
			constraintsJComboBoxWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJComboBoxWhenChange.weightx = 1.0;
			constraintsJComboBoxWhenChange.ipadx = 3;
			constraintsJComboBoxWhenChange.insets = new java.awt.Insets(0, 5, 1, 10);
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
			ivjJTextFieldChangeTriggerOffset.setPreferredSize(new java.awt.Dimension(30, 20));
			ivjJTextFieldChangeTriggerOffset.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJTextFieldChangeTriggerOffset.setMaximumSize(new java.awt.Dimension(40, 20));
			ivjJTextFieldChangeTriggerOffset.setMinimumSize(new java.awt.Dimension(0, 0));
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
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMProgramDirectGear gear = null;
	
	gear = (LMProgramDirectGear)o;
	
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

	com.cannontech.database.data.device.lm.RotationGear r = (com.cannontech.database.data.device.lm.RotationGear)gear;

    r.setShedTime(SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxShedTime()));

	r.setNumberOfGroups( getJComboBoxNumGroups().getSelectedItem() );

	String sendRateString = (String)(getJComboBoxSendRateDigits().getSelectedItem()) + " " + (String)(getJComboBoxSendRateUnits().getSelectedItem());	
    r.setSendRate(SwingUtil.getIntervalSecondsValue(sendRateString));
		
	r.setGroupSelectionMethod( StringUtils.removeChars( ' ', getJComboBoxGroupSelection().getSelectedItem().toString() ) );
	
	return r;
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
	getJComboBoxWhenChange().addActionListener(this);
	getJComboBoxShedTime().addActionListener(this);
	getJComboBoxNumGroups().addActionListener(this);
	getJComboBoxSendRateDigits().addActionListener(this);
	getJComboBoxSendRateUnits().addActionListener(this);
	getJComboBoxGroupSelection().addActionListener(this);
	getJComboBoxHowToStop().addActionListener(this);
	getJTextFieldChangeTriggerOffset().addCaretListener(this);

	// user code end
	
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try 
	{
		// user code begin {1}
		// user code end
		setName("RotationGearPanel");
		setLayout(new java.awt.GridBagLayout());
		setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
		setPreferredSize(new java.awt.Dimension(402, 430));
		setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		setSize(402, 430);
		setMinimumSize(new java.awt.Dimension(0, 0));

		java.awt.GridBagConstraints constraintsJLabelShedTime = new java.awt.GridBagConstraints();
		constraintsJLabelShedTime.gridx = 1; constraintsJLabelShedTime.gridy = 1;
		constraintsJLabelShedTime.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelShedTime.ipadx = 83;
		constraintsJLabelShedTime.insets = new java.awt.Insets(11, 13, 7, 1);
		add(getJLabelShedTime(), constraintsJLabelShedTime);

		java.awt.GridBagConstraints constraintsJComboBoxShedTime = new java.awt.GridBagConstraints();
		constraintsJComboBoxShedTime.gridx = 2; constraintsJComboBoxShedTime.gridy = 1;
		constraintsJComboBoxShedTime.gridwidth = 2;
		constraintsJComboBoxShedTime.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJComboBoxShedTime.weightx = 1.0;
		constraintsJComboBoxShedTime.ipadx = 137;
		constraintsJComboBoxShedTime.ipady = 23;
		constraintsJComboBoxShedTime.insets = new java.awt.Insets(10, 1, 1, 104);
		add(getJComboBoxShedTime(), constraintsJComboBoxShedTime);

		java.awt.GridBagConstraints constraintsJComboBoxNumGroups = new java.awt.GridBagConstraints();
		constraintsJComboBoxNumGroups.gridx = 2; constraintsJComboBoxNumGroups.gridy = 2;
		constraintsJComboBoxNumGroups.gridwidth = 2;
		constraintsJComboBoxNumGroups.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJComboBoxNumGroups.weightx = 1.0;
		constraintsJComboBoxNumGroups.ipadx = 137;
		constraintsJComboBoxNumGroups.ipady = 23;
		constraintsJComboBoxNumGroups.insets = new java.awt.Insets(2, 1, 2, 104);
		add(getJComboBoxNumGroups(), constraintsJComboBoxNumGroups);

		java.awt.GridBagConstraints constraintsJLabelNumGroups = new java.awt.GridBagConstraints();
		constraintsJLabelNumGroups.gridx = 1; constraintsJLabelNumGroups.gridy = 2;
		constraintsJLabelNumGroups.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelNumGroups.ipadx = 17;
		constraintsJLabelNumGroups.insets = new java.awt.Insets(6, 13, 5, 1);
		add(getJLabelNumGroups(), constraintsJLabelNumGroups);

		java.awt.GridBagConstraints constraintsJLabelSendRate = new java.awt.GridBagConstraints();
		constraintsJLabelSendRate.gridx = 1; constraintsJLabelSendRate.gridy = 3;
		constraintsJLabelSendRate.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelSendRate.ipadx = 8;
		constraintsJLabelSendRate.insets = new java.awt.Insets(7, 13, 5, 1);
		add(getJLabelSendRate(), constraintsJLabelSendRate);

		java.awt.GridBagConstraints constraintsJComboBoxSendRateDigits = new java.awt.GridBagConstraints();
		constraintsJComboBoxSendRateDigits.gridx = 2; constraintsJComboBoxSendRateDigits.gridy = 3;
		constraintsJComboBoxSendRateDigits.gridwidth = 2;
		constraintsJComboBoxSendRateDigits.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJComboBoxSendRateDigits.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxSendRateDigits.weightx = 1.0;
		constraintsJComboBoxSendRateDigits.ipadx = 83;
		constraintsJComboBoxSendRateDigits.ipady = 23;
		constraintsJComboBoxSendRateDigits.insets = new java.awt.Insets(3, 1, 2, 161);
		add(getJComboBoxSendRateDigits(), constraintsJComboBoxSendRateDigits);

		java.awt.GridBagConstraints constraintsJComboBoxSendRateUnits = new java.awt.GridBagConstraints();
		constraintsJComboBoxSendRateUnits.gridx = 3; constraintsJComboBoxSendRateUnits.gridy = 3;
		constraintsJComboBoxSendRateUnits.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJComboBoxSendRateUnits.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJComboBoxSendRateUnits.weightx = 1.0;
		constraintsJComboBoxSendRateUnits.ipadx = 98;
		constraintsJComboBoxSendRateUnits.ipady = 23;
		constraintsJComboBoxSendRateUnits.insets = new java.awt.Insets(3, 56, 2, 64);
		add(getJComboBoxSendRateUnits(), constraintsJComboBoxSendRateUnits);

		java.awt.GridBagConstraints constraintsJComboBoxGroupSelection = new java.awt.GridBagConstraints();
		constraintsJComboBoxGroupSelection.gridx = 2; constraintsJComboBoxGroupSelection.gridy = 4;
		constraintsJComboBoxGroupSelection.gridwidth = 2;
		constraintsJComboBoxGroupSelection.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJComboBoxGroupSelection.weightx = 1.0;
		constraintsJComboBoxGroupSelection.ipadx = 49;
		constraintsJComboBoxGroupSelection.insets = new java.awt.Insets(3, 1, 3, 66);
		add(getJComboBoxGroupSelection(), constraintsJComboBoxGroupSelection);

		java.awt.GridBagConstraints constraintsJLabelGroupSelection = new java.awt.GridBagConstraints();
		constraintsJLabelGroupSelection.gridx = 1; constraintsJLabelGroupSelection.gridy = 4;
		constraintsJLabelGroupSelection.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelGroupSelection.ipadx = 12;
		constraintsJLabelGroupSelection.insets = new java.awt.Insets(7, 13, 6, 1);
		add(getJLabelGroupSelection(), constraintsJLabelGroupSelection);

		java.awt.GridBagConstraints constraintsJLabelHowToStop = new java.awt.GridBagConstraints();
		constraintsJLabelHowToStop.gridx = 1; constraintsJLabelHowToStop.gridy = 5;
		constraintsJLabelHowToStop.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelHowToStop.ipadx = 4;
		constraintsJLabelHowToStop.insets = new java.awt.Insets(5, 14, 9, 30);
		add(getJLabelHowToStop(), constraintsJLabelHowToStop);

		java.awt.GridBagConstraints constraintsJLabelPercentReduction = new java.awt.GridBagConstraints();
		constraintsJLabelPercentReduction.gridx = 1; constraintsJLabelPercentReduction.gridy = 6;
		constraintsJLabelPercentReduction.gridwidth = 2;
		constraintsJLabelPercentReduction.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelPercentReduction.ipadx = 53;
		constraintsJLabelPercentReduction.ipady = 3;
		constraintsJLabelPercentReduction.insets = new java.awt.Insets(6, 14, 4, 9);
		add(getJLabelPercentReduction(), constraintsJLabelPercentReduction);

		java.awt.GridBagConstraints constraintsJComboBoxHowToStop = new java.awt.GridBagConstraints();
		constraintsJComboBoxHowToStop.gridx = 2; constraintsJComboBoxHowToStop.gridy = 5;
		constraintsJComboBoxHowToStop.gridwidth = 2;
		constraintsJComboBoxHowToStop.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJComboBoxHowToStop.weightx = 1.0;
		constraintsJComboBoxHowToStop.ipadx = 49;
		constraintsJComboBoxHowToStop.insets = new java.awt.Insets(3, 1, 4, 66);
		add(getJComboBoxHowToStop(), constraintsJComboBoxHowToStop);

		java.awt.GridBagConstraints constraintsJCSpinFieldPercentReduction = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldPercentReduction.gridx = 3; constraintsJCSpinFieldPercentReduction.gridy = 6;
		constraintsJCSpinFieldPercentReduction.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJCSpinFieldPercentReduction.ipadx = 48;
		constraintsJCSpinFieldPercentReduction.ipady = -30;
		constraintsJCSpinFieldPercentReduction.insets = new java.awt.Insets(4, 9, 3, 117);
		add(getJCSpinFieldPercentReduction(), constraintsJCSpinFieldPercentReduction);

		java.awt.GridBagConstraints constraintsJPanelChangeMethod = new java.awt.GridBagConstraints();
		constraintsJPanelChangeMethod.gridx = 1; constraintsJPanelChangeMethod.gridy = 7;
		constraintsJPanelChangeMethod.gridwidth = 3;
		constraintsJPanelChangeMethod.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJPanelChangeMethod.weightx = 1.0;
		constraintsJPanelChangeMethod.weighty = 1.0;
		constraintsJPanelChangeMethod.ipadx = 335;
		constraintsJPanelChangeMethod.ipady = 87;
		constraintsJPanelChangeMethod.insets = new java.awt.Insets(3, 8, 165, 59);
		add(getJPanelChangeMethod(), constraintsJPanelChangeMethod);
	} 
	catch (java.lang.Throwable ivjExc) 
	{
		handleException(ivjExc);
	}
	//user code begin {2}
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
		RotationGearPanel aRotationGearPanel;
		aRotationGearPanel = new RotationGearPanel();
		frame.setContentPane(aRotationGearPanel);
		frame.setSize(aRotationGearPanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel");
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

	getJComboBoxHowToStop().setSelectedItem( StringUtils.addCharBetweenWords( ' ', gear.getMethodStopType() ) );

	getJCSpinFieldPercentReduction().setValue( gear.getPercentReduction() );
	
	setChangeCondition( gear.getChangeCondition() );
	
	getJCSpinFieldChangeDuration().setValue( new Integer( gear.getChangeDuration().intValue() / 60 ) );
	getJCSpinFieldChangePriority().setValue( gear.getChangePriority() );
	getJCSpinFieldChangeTriggerNumber().setValue( gear.getChangeTriggerNumber() );	
	getJTextFieldChangeTriggerOffset().setText( gear.getChangeTriggerOffset().toString() );

	com.cannontech.database.data.device.lm.RotationGear r = (com.cannontech.database.data.device.lm.RotationGear)gear;

    SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxShedTime(), r.getShedTime().intValue());

	getJComboBoxNumGroups().setSelectedIndex( r.getNumberOfGroups() );

    SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxSendRateDigits(), getJComboBoxSendRateUnits(),
        r.getSendRate().intValue());
		
	getJComboBoxGroupSelection().setSelectedItem( StringUtils.addCharBetweenWords( ' ', r.getGroupSelectionMethod() ) );
	
}
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
}