package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * Insert the type's description here.
 * Creation date: (8/1/2002 3:39:47 PM)
 * @author: 
 */
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
 
  public class MasterCycleGearPanel extends GenericGearPanel {
	private javax.swing.JComboBox ivjJComboBoxHowToStop = null;
	private javax.swing.JComboBox ivjJComboBoxWhenChange = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeDuration = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangePriority = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeTriggerNumber = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldControlPercent = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldCyclePeriod = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldPercentReduction = null;
	private javax.swing.JLabel ivjJLabelChangeDuration = null;
	private javax.swing.JLabel ivjJLabelChangePriority = null;
	private javax.swing.JLabel ivjJLabelChangeTriggerNumber = null;
	private javax.swing.JLabel ivjJLabelChangeTriggerOffset = null;
	private javax.swing.JLabel ivjJLabelControlPercent = null;
	private javax.swing.JLabel ivjJLabelCyclePeriod = null;
	private javax.swing.JLabel ivjJLabelHowToStop = null;
	private javax.swing.JLabel ivjJLabelMinutesChDur = null;
	private javax.swing.JLabel ivjJLabelPercentReduction = null;
	private javax.swing.JLabel ivjJLabelWhenChange = null;
	private javax.swing.JPanel ivjJPanelChangeMethod = null;
	private javax.swing.JTextField ivjJTextFieldChangeTriggerOffset = null;
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
	private javax.swing.JComboBox ivjJComboBoxStopOrder = null;
	private javax.swing.JLabel ivjJLabelStopOrder = null;
	private javax.swing.JComboBox ivjJComboBoxGroupSelection = null;
	private javax.swing.JLabel ivjJLabelGroupSelection = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == MasterCycleGearPanel.this.getJCheckBoxRampIn()) 
				connEtoC1(e);
		};
	};
/**
 * MasterCycleGearPanel constructor comment.
 */
public MasterCycleGearPanel() {
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
		jComboBoxWhenChange_ActionPerformed(e);
	if (e.getSource() == getJComboBoxHowToStop()) 
		connEtoC10(e);
	if (e.getSource() == getJComboBoxStopOrder() || e.getSource() == getJTextFieldRampOutInterval())
		this.fireInputUpdate();
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
			ivjJComboBoxHowToStop.setPreferredSize(new java.awt.Dimension(159, 23));
			ivjJComboBoxHowToStop.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxHowToStop.setMinimumSize(new java.awt.Dimension(159, 23));
			ivjJComboBoxHowToStop.setMaximumSize(new java.awt.Dimension(159, 23));
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
			ivjJComboBoxWhenChange.setPreferredSize(new java.awt.Dimension(195, 23));
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
			ivjJCSpinFieldChangeDuration.setPreferredSize(new java.awt.Dimension(35, 20));
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
			ivjJCSpinFieldChangePriority.setMaximumSize(new java.awt.Dimension(40, 30));
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
 * Return the JCSpinFieldControlPercent property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldControlPercent() {
	if (ivjJCSpinFieldControlPercent == null) {
		try {
			ivjJCSpinFieldControlPercent = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldControlPercent.setName("JCSpinFieldControlPercent");
			ivjJCSpinFieldControlPercent.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldControlPercent.setMaximumSize(new java.awt.Dimension(60, 20));
			ivjJCSpinFieldControlPercent.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjJCSpinFieldControlPercent.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJCSpinFieldControlPercent.setMinimumSize(new java.awt.Dimension(60, 20));
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
			ivjJCSpinFieldCyclePeriod.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldCyclePeriod.setMaximumSize(new java.awt.Dimension(60, 20));
			ivjJCSpinFieldCyclePeriod.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjJCSpinFieldCyclePeriod.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJCSpinFieldCyclePeriod.setMinimumSize(new java.awt.Dimension(60, 20));
			// user code begin {1}
			ivjJCSpinFieldCyclePeriod.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(5), new Integer(945), null, true, 
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
			ivjJCSpinFieldPercentReduction.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjJCSpinFieldPercentReduction.setMaximumSize(new java.awt.Dimension(60, 20));
			ivjJCSpinFieldPercentReduction.setMinimumSize(new java.awt.Dimension(60, 20));
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
			ivjJCSpinFieldRampInPercent.setPreferredSize(new java.awt.Dimension(76, 20));
			ivjJCSpinFieldRampInPercent.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldRampInPercent.setMaximumSize(new java.awt.Dimension(76, 20));
			ivjJCSpinFieldRampInPercent.setMinimumSize(new java.awt.Dimension(76, 20));
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
			ivjJCSpinFieldRampOutPercent.setPreferredSize(new java.awt.Dimension(76, 20));
			ivjJCSpinFieldRampOutPercent.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldRampOutPercent.setMaximumSize(new java.awt.Dimension(76, 20));
			ivjJCSpinFieldRampOutPercent.setMinimumSize(new java.awt.Dimension(76, 20));
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
 * Return the JLabelControlPercent property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlPercent() {
	if (ivjJLabelControlPercent == null) {
		try {
			ivjJLabelControlPercent = new javax.swing.JLabel();
			ivjJLabelControlPercent.setName("JLabelControlPercent");
			ivjJLabelControlPercent.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelControlPercent.setText("Control Percent:");
			ivjJLabelControlPercent.setMaximumSize(new java.awt.Dimension(112, 14));
			ivjJLabelControlPercent.setPreferredSize(new java.awt.Dimension(112, 14));
			ivjJLabelControlPercent.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelControlPercent.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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
 * Return the JLabelCyclePeriod property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCyclePeriod() {
	if (ivjJLabelCyclePeriod == null) {
		try {
			ivjJLabelCyclePeriod = new javax.swing.JLabel();
			ivjJLabelCyclePeriod.setName("JLabelCyclePeriod");
			ivjJLabelCyclePeriod.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelCyclePeriod.setText("Cycle Period:");
			ivjJLabelCyclePeriod.setMaximumSize(new java.awt.Dimension(112, 14));
			ivjJLabelCyclePeriod.setPreferredSize(new java.awt.Dimension(112, 14));
			ivjJLabelCyclePeriod.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelCyclePeriod.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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
			ivjJLabelRampOutInterval.setMaximumSize(new java.awt.Dimension(182, 125));
			ivjJLabelRampOutInterval.setPreferredSize(new java.awt.Dimension(182, 125));
			ivjJLabelRampOutInterval.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRampOutInterval.setMinimumSize(new java.awt.Dimension(182, 125));
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
			ivjJLabelRampOutPercent.setMaximumSize(new java.awt.Dimension(182, 125));
			ivjJLabelRampOutPercent.setPreferredSize(new java.awt.Dimension(182, 125));
			ivjJLabelRampOutPercent.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRampOutPercent.setMinimumSize(new java.awt.Dimension(182, 125));
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
			ivjJPanelChangeMethod.setMaximumSize(new java.awt.Dimension(335, 88));
			ivjJPanelChangeMethod.setPreferredSize(new java.awt.Dimension(335, 88));
			ivjJPanelChangeMethod.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJPanelChangeMethod.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

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
			constraintsJCSpinFieldChangeDuration.ipadx = 34;
			constraintsJCSpinFieldChangeDuration.ipady = 19;
			constraintsJCSpinFieldChangeDuration.insets = new java.awt.Insets(1, 5, 3, 2);
			getJPanelChangeMethod().add(getJCSpinFieldChangeDuration(), constraintsJCSpinFieldChangeDuration);

			java.awt.GridBagConstraints constraintsJLabelMinutesChDur = new java.awt.GridBagConstraints();
			constraintsJLabelMinutesChDur.gridx = 3; constraintsJLabelMinutesChDur.gridy = 2;
			constraintsJLabelMinutesChDur.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelMinutesChDur.ipadx = 5;
			constraintsJLabelMinutesChDur.ipady = -2;
			constraintsJLabelMinutesChDur.insets = new java.awt.Insets(5, 3, 5, 5);
			getJPanelChangeMethod().add(getJLabelMinutesChDur(), constraintsJLabelMinutesChDur);

			java.awt.GridBagConstraints constraintsJLabelChangePriority = new java.awt.GridBagConstraints();
			constraintsJLabelChangePriority.gridx = 4; constraintsJLabelChangePriority.gridy = 2;
			constraintsJLabelChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangePriority.ipadx = -13;
			constraintsJLabelChangePriority.ipady = 6;
			constraintsJLabelChangePriority.insets = new java.awt.Insets(1, 6, 3, 3);
			getJPanelChangeMethod().add(getJLabelChangePriority(), constraintsJLabelChangePriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangePriority = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangePriority.gridx = 5; constraintsJCSpinFieldChangePriority.gridy = 2;
			constraintsJCSpinFieldChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangePriority.ipadx = 29;
			constraintsJCSpinFieldChangePriority.ipady = 19;
			constraintsJCSpinFieldChangePriority.insets = new java.awt.Insets(1, 3, 3, 8);
			getJPanelChangeMethod().add(getJCSpinFieldChangePriority(), constraintsJCSpinFieldChangePriority);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerNumber.gridx = 1; constraintsJLabelChangeTriggerNumber.gridy = 3;
			constraintsJLabelChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerNumber.ipadx = -45;
			constraintsJLabelChangeTriggerNumber.ipady = 6;
			constraintsJLabelChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 16, 5);
			getJPanelChangeMethod().add(getJLabelChangeTriggerNumber(), constraintsJLabelChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerOffset.gridx = 4; constraintsJLabelChangeTriggerOffset.gridy = 3;
			constraintsJLabelChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerOffset.ipadx = -63;
			constraintsJLabelChangeTriggerOffset.insets = new java.awt.Insets(8, 6, 18, 13);
			getJPanelChangeMethod().add(getJLabelChangeTriggerOffset(), constraintsJLabelChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJTextFieldChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJTextFieldChangeTriggerOffset.gridx = 5; constraintsJTextFieldChangeTriggerOffset.gridy = 3;
			constraintsJTextFieldChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJTextFieldChangeTriggerOffset.weightx = 1.0;
			constraintsJTextFieldChangeTriggerOffset.ipadx = 26;
			constraintsJTextFieldChangeTriggerOffset.insets = new java.awt.Insets(4, 3, 16, 8);
			getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(), constraintsJTextFieldChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeTriggerNumber.gridx = 2; constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
			constraintsJCSpinFieldChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangeTriggerNumber.ipadx = 34;
			constraintsJCSpinFieldChangeTriggerNumber.ipady = 19;
			constraintsJCSpinFieldChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 16, 2);
			getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(), constraintsJCSpinFieldChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelWhenChange = new java.awt.GridBagConstraints();
			constraintsJLabelWhenChange.gridx = 1; constraintsJLabelWhenChange.gridy = 1;
			constraintsJLabelWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelWhenChange.ipadx = 3;
			constraintsJLabelWhenChange.ipady = 4;
			constraintsJLabelWhenChange.insets = new java.awt.Insets(0, 5, 4, 5);
			getJPanelChangeMethod().add(getJLabelWhenChange(), constraintsJLabelWhenChange);

			java.awt.GridBagConstraints constraintsJComboBoxWhenChange = new java.awt.GridBagConstraints();
			constraintsJComboBoxWhenChange.gridx = 2; constraintsJComboBoxWhenChange.gridy = 1;
			constraintsJComboBoxWhenChange.gridwidth = 4;
			constraintsJComboBoxWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJComboBoxWhenChange.weightx = 1.0;
			constraintsJComboBoxWhenChange.ipadx = 69;
			constraintsJComboBoxWhenChange.insets = new java.awt.Insets(0, 5, 1, 27);
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
			ivjJTextFieldRampInInterval.setPreferredSize(new java.awt.Dimension(76, 20));
			ivjJTextFieldRampInInterval.setMaximumSize(new java.awt.Dimension(76, 20));
			ivjJTextFieldRampInInterval.setMinimumSize(new java.awt.Dimension(76, 20));
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
			ivjJTextFieldRampOutInterval.setPreferredSize(new java.awt.Dimension(63, 20));
			ivjJTextFieldRampOutInterval.setMaximumSize(new java.awt.Dimension(63, 20));
			ivjJTextFieldRampOutInterval.setMinimumSize(new java.awt.Dimension(63, 20));
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
	LMProgramDirectGear gear = null;
	
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
		gear.setRampInInterval(new Integer(getJTextFieldRampInInterval().getText()));
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

	com.cannontech.database.data.device.lm.MasterCycleGear s = (com.cannontech.database.data.device.lm.MasterCycleGear)gear;

	s.setControlPercent( new Integer( 
		((Number)getJCSpinFieldControlPercent().getValue()).intValue() ) );

	s.setCyclePeriodLength( new Integer( 
		((Number)getJCSpinFieldCyclePeriod().getValue()).intValue() * 60 ) );
		
	s.setGroupSelectionMethod( StringUtils.removeChars( ' ', getJComboBoxGroupSelection().getSelectedItem().toString() ) );
			
	return s;
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
	getJCSpinFieldControlPercent().addValueListener(this);
	getJCSpinFieldCyclePeriod().addValueListener(this);
	getJCSpinFieldPercentReduction().addValueListener(this);
	getJComboBoxWhenChange().addActionListener(this);
	getJComboBoxWhenChange().addActionListener(this);
	getJTextFieldChangeTriggerOffset().addCaretListener(this);
	getJComboBoxWhenChange().addActionListener(this);
	getJComboBoxHowToStop().addActionListener(this);
	getJCSpinFieldRampOutPercent().addValueListener(this);
	getJCSpinFieldRampInPercent().addValueListener(this);
	getJComboBoxStopOrder().addActionListener(this);
	getJTextFieldRampOutInterval().addCaretListener(this);
	
	// user code end
	getJCheckBoxRampIn().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MasterCycleGearPanel");
		setPreferredSize(new java.awt.Dimension(402, 430));
		setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		setLayout(new java.awt.GridBagLayout());
		setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
		setSize(402, 430);

		java.awt.GridBagConstraints constraintsJLabelControlPercent = new java.awt.GridBagConstraints();
		constraintsJLabelControlPercent.gridx = 1; constraintsJLabelControlPercent.gridy = 1;
		constraintsJLabelControlPercent.ipadx = -7;
		constraintsJLabelControlPercent.insets = new java.awt.Insets(14, 10, 6, 34);
		add(getJLabelControlPercent(), constraintsJLabelControlPercent);

		java.awt.GridBagConstraints constraintsJCSpinFieldControlPercent = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldControlPercent.gridx = 2; constraintsJCSpinFieldControlPercent.gridy = 1;
		constraintsJCSpinFieldControlPercent.gridwidth = 2;
		constraintsJCSpinFieldControlPercent.insets = new java.awt.Insets(10, 6, 4, 18);
		add(getJCSpinFieldControlPercent(), constraintsJCSpinFieldControlPercent);

		java.awt.GridBagConstraints constraintsJCSpinFieldCyclePeriod = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldCyclePeriod.gridx = 2; constraintsJCSpinFieldCyclePeriod.gridy = 2;
		constraintsJCSpinFieldCyclePeriod.gridwidth = 2;
		constraintsJCSpinFieldCyclePeriod.insets = new java.awt.Insets(5, 6, 4, 18);
		add(getJCSpinFieldCyclePeriod(), constraintsJCSpinFieldCyclePeriod);

		java.awt.GridBagConstraints constraintsJLabelCyclePeriod = new java.awt.GridBagConstraints();
		constraintsJLabelCyclePeriod.gridx = 1; constraintsJLabelCyclePeriod.gridy = 2;
		constraintsJLabelCyclePeriod.ipadx = -4;
		constraintsJLabelCyclePeriod.insets = new java.awt.Insets(7, 10, 8, 31);
		add(getJLabelCyclePeriod(), constraintsJLabelCyclePeriod);

		java.awt.GridBagConstraints constraintsJLabelHowToStop = new java.awt.GridBagConstraints();
		constraintsJLabelHowToStop.gridx = 1; constraintsJLabelHowToStop.gridy = 7;
		constraintsJLabelHowToStop.ipadx = 4;
		constraintsJLabelHowToStop.insets = new java.awt.Insets(7, 10, 4, 23);
		add(getJLabelHowToStop(), constraintsJLabelHowToStop);

		java.awt.GridBagConstraints constraintsJComboBoxHowToStop = new java.awt.GridBagConstraints();
		constraintsJComboBoxHowToStop.gridx = 2; constraintsJComboBoxHowToStop.gridy = 7;
		constraintsJComboBoxHowToStop.gridwidth = 3;
		constraintsJComboBoxHowToStop.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxHowToStop.weightx = 1.0;
		constraintsJComboBoxHowToStop.insets = new java.awt.Insets(3, 6, 1, 88);
		add(getJComboBoxHowToStop(), constraintsJComboBoxHowToStop);

		java.awt.GridBagConstraints constraintsJCSpinFieldPercentReduction = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldPercentReduction.gridx = 3; constraintsJCSpinFieldPercentReduction.gridy = 11;
		constraintsJCSpinFieldPercentReduction.gridwidth = 2;
		constraintsJCSpinFieldPercentReduction.insets = new java.awt.Insets(4, 6, 3, 156);
		add(getJCSpinFieldPercentReduction(), constraintsJCSpinFieldPercentReduction);

		java.awt.GridBagConstraints constraintsJLabelPercentReduction = new java.awt.GridBagConstraints();
		constraintsJLabelPercentReduction.gridx = 1; constraintsJLabelPercentReduction.gridy = 11;
		constraintsJLabelPercentReduction.gridwidth = 2;
		constraintsJLabelPercentReduction.ipadx = 53;
		constraintsJLabelPercentReduction.ipady = 3;
		constraintsJLabelPercentReduction.insets = new java.awt.Insets(6, 10, 4, 5);
		add(getJLabelPercentReduction(), constraintsJLabelPercentReduction);

		java.awt.GridBagConstraints constraintsJPanelChangeMethod = new java.awt.GridBagConstraints();
		constraintsJPanelChangeMethod.gridx = 1; constraintsJPanelChangeMethod.gridy = 12;
		constraintsJPanelChangeMethod.gridwidth = 4;
		constraintsJPanelChangeMethod.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelChangeMethod.weightx = 1.0;
		constraintsJPanelChangeMethod.weighty = 1.0;
		constraintsJPanelChangeMethod.insets = new java.awt.Insets(3, 5, 41, 62);
		add(getJPanelChangeMethod(), constraintsJPanelChangeMethod);

		java.awt.GridBagConstraints constraintsJLabelRampOutPercent = new java.awt.GridBagConstraints();
		constraintsJLabelRampOutPercent.gridx = 1; constraintsJLabelRampOutPercent.gridy = 9;
		constraintsJLabelRampOutPercent.ipadx = -57;
		constraintsJLabelRampOutPercent.ipady = -111;
		constraintsJLabelRampOutPercent.insets = new java.awt.Insets(7, 10, 4, 14);
		add(getJLabelRampOutPercent(), constraintsJLabelRampOutPercent);

		java.awt.GridBagConstraints constraintsJCSpinFieldRampOutPercent = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldRampOutPercent.gridx = 2; constraintsJCSpinFieldRampOutPercent.gridy = 9;
		constraintsJCSpinFieldRampOutPercent.gridwidth = 2;
		constraintsJCSpinFieldRampOutPercent.insets = new java.awt.Insets(3, 6, 2, 2);
		add(getJCSpinFieldRampOutPercent(), constraintsJCSpinFieldRampOutPercent);

		java.awt.GridBagConstraints constraintsJLabelRampOutPercentSign = new java.awt.GridBagConstraints();
		constraintsJLabelRampOutPercentSign.gridx = 4; constraintsJLabelRampOutPercentSign.gridy = 9;
		constraintsJLabelRampOutPercentSign.ipadx = 16;
		constraintsJLabelRampOutPercentSign.insets = new java.awt.Insets(7, 2, 2, 140);
		add(getJLabelRampOutPercentSign(), constraintsJLabelRampOutPercentSign);

		java.awt.GridBagConstraints constraintsJLabelRampOutSec = new java.awt.GridBagConstraints();
		constraintsJLabelRampOutSec.gridx = 4; constraintsJLabelRampOutSec.gridy = 10;
		constraintsJLabelRampOutSec.ipadx = 4;
		constraintsJLabelRampOutSec.insets = new java.awt.Insets(5, 2, 5, 140);
		add(getJLabelRampOutSec(), constraintsJLabelRampOutSec);

		java.awt.GridBagConstraints constraintsJTextFieldRampOutInterval = new java.awt.GridBagConstraints();
		constraintsJTextFieldRampOutInterval.gridx = 2; constraintsJTextFieldRampOutInterval.gridy = 10;
		constraintsJTextFieldRampOutInterval.gridwidth = 2;
		constraintsJTextFieldRampOutInterval.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldRampOutInterval.weightx = 1.0;
		constraintsJTextFieldRampOutInterval.ipadx = 13;
		constraintsJTextFieldRampOutInterval.insets = new java.awt.Insets(3, 6, 3, 2);
		add(getJTextFieldRampOutInterval(), constraintsJTextFieldRampOutInterval);

		java.awt.GridBagConstraints constraintsJLabelRampOutInterval = new java.awt.GridBagConstraints();
		constraintsJLabelRampOutInterval.gridx = 1; constraintsJLabelRampOutInterval.gridy = 10;
		constraintsJLabelRampOutInterval.ipadx = -57;
		constraintsJLabelRampOutInterval.ipady = -111;
		constraintsJLabelRampOutInterval.insets = new java.awt.Insets(5, 10, 7, 14);
		add(getJLabelRampOutInterval(), constraintsJLabelRampOutInterval);

		java.awt.GridBagConstraints constraintsJCheckBoxRampIn = new java.awt.GridBagConstraints();
		constraintsJCheckBoxRampIn.gridx = 1; constraintsJCheckBoxRampIn.gridy = 4;
		constraintsJCheckBoxRampIn.ipadx = 53;
		constraintsJCheckBoxRampIn.ipady = -2;
		constraintsJCheckBoxRampIn.insets = new java.awt.Insets(1, 10, 2, 14);
		add(getJCheckBoxRampIn(), constraintsJCheckBoxRampIn);

		java.awt.GridBagConstraints constraintsJLabelRampInPercent = new java.awt.GridBagConstraints();
		constraintsJLabelRampInPercent.gridx = 1; constraintsJLabelRampInPercent.gridy = 5;
		constraintsJLabelRampInPercent.insets = new java.awt.Insets(7, 10, 4, 20);
		add(getJLabelRampInPercent(), constraintsJLabelRampInPercent);

		java.awt.GridBagConstraints constraintsJCSpinFieldRampInPercent = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldRampInPercent.gridx = 2; constraintsJCSpinFieldRampInPercent.gridy = 5;
		constraintsJCSpinFieldRampInPercent.gridwidth = 2;
		constraintsJCSpinFieldRampInPercent.insets = new java.awt.Insets(3, 6, 2, 2);
		add(getJCSpinFieldRampInPercent(), constraintsJCSpinFieldRampInPercent);

		java.awt.GridBagConstraints constraintsJLabelRampInPercentSign = new java.awt.GridBagConstraints();
		constraintsJLabelRampInPercentSign.gridx = 4; constraintsJLabelRampInPercentSign.gridy = 5;
		constraintsJLabelRampInPercentSign.ipadx = 16;
		constraintsJLabelRampInPercentSign.insets = new java.awt.Insets(5, 2, 4, 140);
		add(getJLabelRampInPercentSign(), constraintsJLabelRampInPercentSign);

		java.awt.GridBagConstraints constraintsJLabelRampInSec = new java.awt.GridBagConstraints();
		constraintsJLabelRampInSec.gridx = 4; constraintsJLabelRampInSec.gridy = 6;
		constraintsJLabelRampInSec.ipadx = 4;
		constraintsJLabelRampInSec.insets = new java.awt.Insets(5, 2, 5, 140);
		add(getJLabelRampInSec(), constraintsJLabelRampInSec);

		java.awt.GridBagConstraints constraintsJTextFieldRampInInterval = new java.awt.GridBagConstraints();
		constraintsJTextFieldRampInInterval.gridx = 2; constraintsJTextFieldRampInInterval.gridy = 6;
		constraintsJTextFieldRampInInterval.gridwidth = 2;
		constraintsJTextFieldRampInInterval.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldRampInInterval.weightx = 1.0;
		constraintsJTextFieldRampInInterval.insets = new java.awt.Insets(3, 6, 3, 2);
		add(getJTextFieldRampInInterval(), constraintsJTextFieldRampInInterval);

		java.awt.GridBagConstraints constraintsJLabelRampInInterval = new java.awt.GridBagConstraints();
		constraintsJLabelRampInInterval.gridx = 1; constraintsJLabelRampInInterval.gridy = 6;
		constraintsJLabelRampInInterval.insets = new java.awt.Insets(6, 10, 6, 20);
		add(getJLabelRampInInterval(), constraintsJLabelRampInInterval);

		java.awt.GridBagConstraints constraintsJLabelStopOrder = new java.awt.GridBagConstraints();
		constraintsJLabelStopOrder.gridx = 1; constraintsJLabelStopOrder.gridy = 8;
		constraintsJLabelStopOrder.insets = new java.awt.Insets(5, 10, 6, 77);
		add(getJLabelStopOrder(), constraintsJLabelStopOrder);

		java.awt.GridBagConstraints constraintsJComboBoxStopOrder = new java.awt.GridBagConstraints();
		constraintsJComboBoxStopOrder.gridx = 2; constraintsJComboBoxStopOrder.gridy = 8;
		constraintsJComboBoxStopOrder.gridwidth = 3;
		constraintsJComboBoxStopOrder.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxStopOrder.weightx = 1.0;
		constraintsJComboBoxStopOrder.ipadx = -25;
		constraintsJComboBoxStopOrder.insets = new java.awt.Insets(2, 6, 2, 88);
		add(getJComboBoxStopOrder(), constraintsJComboBoxStopOrder);

		java.awt.GridBagConstraints constraintsJLabelGroupSelection = new java.awt.GridBagConstraints();
		constraintsJLabelGroupSelection.gridx = 1; constraintsJLabelGroupSelection.gridy = 3;
		constraintsJLabelGroupSelection.insets = new java.awt.Insets(5, 10, 6, 5);
		add(getJLabelGroupSelection(), constraintsJLabelGroupSelection);

		java.awt.GridBagConstraints constraintsJComboBoxGroupSelection = new java.awt.GridBagConstraints();
		constraintsJComboBoxGroupSelection.gridx = 2; constraintsJComboBoxGroupSelection.gridy = 3;
		constraintsJComboBoxGroupSelection.gridwidth = 3;
		constraintsJComboBoxGroupSelection.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxGroupSelection.weightx = 1.0;
		constraintsJComboBoxGroupSelection.insets = new java.awt.Insets(4, 6, 0, 63);
		add(getJComboBoxGroupSelection(), constraintsJComboBoxGroupSelection);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	rampItOut(false);
	rampItIn(false);
	
	getJComboBoxWhenChange().setSelectedItem( LMProgramDirectGear.CHANGE_NONE );
	
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
		MasterCycleGearPanel aMasterCycleGearPanel;
		aMasterCycleGearPanel = new MasterCycleGearPanel();
		frame.setContentPane(aMasterCycleGearPanel);
		frame.setSize(aMasterCycleGearPanel.getSize());
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

	com.cannontech.database.data.device.lm.MasterCycleGear s = (com.cannontech.database.data.device.lm.MasterCycleGear)gear;

	getJCSpinFieldControlPercent().setValue( s.getControlPercent() );

	getJCSpinFieldCyclePeriod().setValue( new Integer( s.getCyclePeriodLength().intValue() / 60 ) );
	
	getJComboBoxGroupSelection().setSelectedItem( StringUtils.addCharBetweenWords( ' ', s.getGroupSelectionMethod() ) );
	
}
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
}
