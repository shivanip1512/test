package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * Insert the type's description here.
 * Creation date: (8/1/2002 3:43:26 PM)
 * @author: 
 */
import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.device.lm.TargetCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
 
public class SmartCycleGearPanel extends GenericGearPanel {
	private javax.swing.JComboBox ivjJComboBoxCycleCountSndType = null;
	private javax.swing.JComboBox ivjJComboBoxHowToStop = null;
	private javax.swing.JComboBox ivjJComboBoxMaxCycleCount = null;
	private javax.swing.JComboBox ivjJComboBoxPeriodCount = null;
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
	private javax.swing.JLabel ivjJLabelCycleCntSndType = null;
	private javax.swing.JLabel ivjJLabelCyclePeriod = null;
	private javax.swing.JLabel ivjJLabelHowToStop = null;
	private javax.swing.JLabel ivjJLabelMaxCycleCnt = null;
	private javax.swing.JLabel ivjJLabelMin = null;
	private javax.swing.JLabel ivjJLabelMinutesChDur = null;
	private javax.swing.JLabel ivjJLabelPercentReduction = null;
	private javax.swing.JLabel ivjJLabelPeriodCount = null;
	private javax.swing.JLabel ivjJLabelSendRate = null;
	private javax.swing.JLabel ivjJLabelWhenChange = null;
	private javax.swing.JPanel ivjJPanelChangeMethod = null;
	private javax.swing.JTextField ivjJTextFieldChangeTriggerOffset = null;
	private JComboBox<String> ivjJComboBoxSendRateDigits = null;
	private JComboBox<String> ivjJComboBoxSendRateUnits = null;
	private javax.swing.JCheckBox jCheckBoxNoRamp = null;
    private JLabel jLabelKWReduction = null;
    private JTextField jTextFieldKWReduction = null;
/**
 * SmartCycleGearPanel constructor comment.
 */
public SmartCycleGearPanel() {
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
	if (e.getSource() == getJComboBoxPeriodCount()) 
		connEtoC6(e);
	if (e.getSource() == getJComboBoxSendRateDigits() || e.getSource() == getJComboBoxSendRateUnits()) 
		connEtoC7(e);
	if (e.getSource() == getJComboBoxHowToStop()) 
		connEtoC10(e);
	if (e.getSource() == getJComboBoxCycleCountSndType()) 
		connEtoC11(e);
	if (e.getSource() == getJComboBoxMaxCycleCount()) 
		connEtoC12(e);
    if (e.getSource() == getJCheckBoxNoRamp())
        this.fireInputUpdate();
	// user code end
	
	// user code begin {2}
	// user code end
}

public void caretUpdate(javax.swing.event.CaretEvent e) {
    // user code begin {1}
    // user code end
    if (e.getSource() == getJTextFieldKWReduction()) 
        fireInputUpdate();
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
 * Return the JComboBoxCycleCountSndType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCycleCountSndType() {
	if (ivjJComboBoxCycleCountSndType == null) {
		try {
			ivjJComboBoxCycleCountSndType = new javax.swing.JComboBox();
			ivjJComboBoxCycleCountSndType.setName("JComboBoxCycleCountSndType");
			ivjJComboBoxCycleCountSndType.setPreferredSize(new java.awt.Dimension(120, 23));
			ivjJComboBoxCycleCountSndType.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxCycleCountSndType.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
 * Return the JComboBoxHowToStop property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxHowToStop() {
	if (ivjJComboBoxHowToStop == null) {
		try {
			ivjJComboBoxHowToStop = new javax.swing.JComboBox();
			ivjJComboBoxHowToStop.setName("JComboBoxHowToStop");
			ivjJComboBoxHowToStop.setPreferredSize(new java.awt.Dimension(75, 23));
			ivjJComboBoxHowToStop.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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

private javax.swing.JLabel getJLabelKWReduction() {
    if (jLabelKWReduction == null) {
        try {
            jLabelKWReduction = new javax.swing.JLabel();
            jLabelKWReduction.setName("JLabelKWReduction");
            jLabelKWReduction.setFont(new java.awt.Font("dialog", 0, 12));
            jLabelKWReduction.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
            jLabelKWReduction.setText("KW Reduction:");
            jLabelKWReduction.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            jLabelKWReduction.setVisible(false);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return jLabelKWReduction;
}

private JTextField getJTextFieldKWReduction() {
    if (jTextFieldKWReduction == null) {
        try {
            jTextFieldKWReduction = new javax.swing.JTextField();
            jTextFieldKWReduction.setName("JTextFieldKWReduction");
            jTextFieldKWReduction.setPreferredSize(new java.awt.Dimension(70, 22));
            jTextFieldKWReduction.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            jTextFieldKWReduction.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
            jTextFieldKWReduction.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.0, 99999.999, 3) );
            jTextFieldKWReduction.setVisible(false);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return jTextFieldKWReduction;
}

/**
 * Return the JComboBoxMaxCycleCount property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxMaxCycleCount() {
	if (ivjJComboBoxMaxCycleCount == null) {
		try {
			ivjJComboBoxMaxCycleCount = new javax.swing.JComboBox();
			ivjJComboBoxMaxCycleCount.setName("JComboBoxMaxCycleCount");
			ivjJComboBoxMaxCycleCount.setPreferredSize(new java.awt.Dimension(136, 23));
			ivjJComboBoxMaxCycleCount.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxMaxCycleCount.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			ivjJComboBoxMaxCycleCount.addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
			for( int i = 1; i <= 63; i++ )
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
 * Return the JComboBoxPeriodCount property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxPeriodCount() {
	if (ivjJComboBoxPeriodCount == null) {
		try {
			ivjJComboBoxPeriodCount = new javax.swing.JComboBox();
			ivjJComboBoxPeriodCount.setName("JComboBoxPeriodCount");
			ivjJComboBoxPeriodCount.setPreferredSize(new java.awt.Dimension(136, 23));
			ivjJComboBoxPeriodCount.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxPeriodCount.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			for( int i = 1; i <= 63; i++ )
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
 * Return the JComboBoxSendRateDigits property value.
 * @return javax.swing.JComboBox
 */
private JComboBox<String> getJComboBoxSendRateDigits() {
	if (ivjJComboBoxSendRateDigits == null) {
		try {
			ivjJComboBoxSendRateDigits = new JComboBox<>();
			ivjJComboBoxSendRateDigits.setName("JComboBoxSendRateDigits");
			ivjJComboBoxSendRateDigits.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJComboBoxSendRateDigits.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			ivjJComboBoxSendRateDigits.setPreferredSize(new java.awt.Dimension(75, 23));
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
 * Return the JComboBoxSendRateUnits property value.
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
			ivjJComboBoxSendRateUnits.setPreferredSize(new java.awt.Dimension(75, 23));
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
 * Return the JComboBoxWhenChange property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxWhenChange() {
	if (ivjJComboBoxWhenChange == null) {
		try {
			ivjJComboBoxWhenChange = new javax.swing.JComboBox();
			ivjJComboBoxWhenChange.setName("JComboBoxWhenChange");
			ivjJComboBoxWhenChange.setPreferredSize(new java.awt.Dimension(205, 23));
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
			ivjJCSpinFieldControlPercent.setPreferredSize(new java.awt.Dimension(48, 20));
			ivjJCSpinFieldControlPercent.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJCSpinFieldControlPercent.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldControlPercent.setMaximumSize(new java.awt.Dimension(50, 20));
            ivjJCSpinFieldControlPercent.setMinimumSize(new java.awt.Dimension(48, 20));
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
			ivjJCSpinFieldCyclePeriod.setPreferredSize(new java.awt.Dimension(48, 20));
			ivjJCSpinFieldCyclePeriod.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJCSpinFieldCyclePeriod.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJCSpinFieldCyclePeriod.setMaximumSize(new java.awt.Dimension(50, 20));
            ivjJCSpinFieldCyclePeriod.setMinimumSize(new java.awt.Dimension(48, 20));
			// user code begin {1}
			ivjJCSpinFieldCyclePeriod.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(1), new Integer(945), null, true, 
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
			ivjJCSpinFieldPercentReduction.setPreferredSize(new java.awt.Dimension(49, 20));
			ivjJCSpinFieldPercentReduction.setMaximumSize(new java.awt.Dimension(50, 60));
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
			ivjJLabelCycleCntSndType.setText("Cycle Count Send Type:");
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
 * Return the JLabelMaxCycleCnt property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMaxCycleCnt() {
	if (ivjJLabelMaxCycleCnt == null) {
		try {
			ivjJLabelMaxCycleCnt = new javax.swing.JLabel();
			ivjJLabelMaxCycleCnt.setName("JLabelMaxCycleCnt");
			ivjJLabelMaxCycleCnt.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMaxCycleCnt.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelMaxCycleCnt.setText("Max Cycle Count:");
			ivjJLabelMaxCycleCnt.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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
			ivjJLabelMin.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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
			ivjJLabelPercentReduction.setMaximumSize(new java.awt.Dimension(160, 14));
			ivjJLabelPercentReduction.setPreferredSize(new java.awt.Dimension(160, 14));
			ivjJLabelPercentReduction.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelPercentReduction.setMinimumSize(new java.awt.Dimension(160, 14));
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
 * Return the JLabelPeriodCount property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPeriodCount() {
	if (ivjJLabelPeriodCount == null) {
		try {
			ivjJLabelPeriodCount = new javax.swing.JLabel();
			ivjJLabelPeriodCount.setName("JLabelPeriodCount");
			ivjJLabelPeriodCount.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelPeriodCount.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJLabelPeriodCount.setText("Starting Period Count:");
			ivjJLabelPeriodCount.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
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
			ivjJPanelChangeMethod.setLayout(new java.awt.GridBagLayout());
			ivjJPanelChangeMethod.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			ivjJPanelChangeMethod.setMaximumSize(new java.awt.Dimension(300, 75));
			ivjJPanelChangeMethod.setPreferredSize(new java.awt.Dimension(300, 75));
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
			constraintsJCSpinFieldChangeDuration.insets = new java.awt.Insets(1, 5, 3, 4);
			getJPanelChangeMethod().add(getJCSpinFieldChangeDuration(), constraintsJCSpinFieldChangeDuration);

			java.awt.GridBagConstraints constraintsJLabelMinutesChDur = new java.awt.GridBagConstraints();
			constraintsJLabelMinutesChDur.gridx = 3; constraintsJLabelMinutesChDur.gridy = 2;
			constraintsJLabelMinutesChDur.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelMinutesChDur.ipadx = 5;
			constraintsJLabelMinutesChDur.ipady = -2;
			constraintsJLabelMinutesChDur.insets = new java.awt.Insets(5, 5, 5, 3);
			getJPanelChangeMethod().add(getJLabelMinutesChDur(), constraintsJLabelMinutesChDur);

			java.awt.GridBagConstraints constraintsJLabelChangePriority = new java.awt.GridBagConstraints();
			constraintsJLabelChangePriority.gridx = 4; constraintsJLabelChangePriority.gridy = 2;
			constraintsJLabelChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangePriority.ipadx = -13;
			constraintsJLabelChangePriority.ipady = 6;
			constraintsJLabelChangePriority.insets = new java.awt.Insets(1, 3, 3, 3);
			getJPanelChangeMethod().add(getJLabelChangePriority(), constraintsJLabelChangePriority);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangePriority = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangePriority.gridx = 5; constraintsJCSpinFieldChangePriority.gridy = 2;
			constraintsJCSpinFieldChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangePriority.ipadx = 29;
			constraintsJCSpinFieldChangePriority.ipady = 19;
			constraintsJCSpinFieldChangePriority.insets = new java.awt.Insets(1, 4, 3, 8);
			getJPanelChangeMethod().add(getJCSpinFieldChangePriority(), constraintsJCSpinFieldChangePriority);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerNumber.gridx = 1; constraintsJLabelChangeTriggerNumber.gridy = 3;
			constraintsJLabelChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerNumber.ipadx = -45;
			constraintsJLabelChangeTriggerNumber.ipady = 6;
			constraintsJLabelChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 21, 5);
			getJPanelChangeMethod().add(getJLabelChangeTriggerNumber(), constraintsJLabelChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJLabelChangeTriggerOffset.gridx = 4; constraintsJLabelChangeTriggerOffset.gridy = 3;
			constraintsJLabelChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelChangeTriggerOffset.ipadx = -63;
			constraintsJLabelChangeTriggerOffset.insets = new java.awt.Insets(8, 3, 23, 13);
			getJPanelChangeMethod().add(getJLabelChangeTriggerOffset(), constraintsJLabelChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJTextFieldChangeTriggerOffset = new java.awt.GridBagConstraints();
			constraintsJTextFieldChangeTriggerOffset.gridx = 5; constraintsJTextFieldChangeTriggerOffset.gridy = 3;
			constraintsJTextFieldChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJTextFieldChangeTriggerOffset.weightx = 1.0;
			constraintsJTextFieldChangeTriggerOffset.ipadx = 26;
			constraintsJTextFieldChangeTriggerOffset.insets = new java.awt.Insets(4, 4, 21, 8);
			getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(), constraintsJTextFieldChangeTriggerOffset);

			java.awt.GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldChangeTriggerNumber.gridx = 2; constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
			constraintsJCSpinFieldChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJCSpinFieldChangeTriggerNumber.ipadx = 34;
			constraintsJCSpinFieldChangeTriggerNumber.ipady = 19;
			constraintsJCSpinFieldChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 21, 4);
			getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(), constraintsJCSpinFieldChangeTriggerNumber);

			java.awt.GridBagConstraints constraintsJLabelWhenChange = new java.awt.GridBagConstraints();
			constraintsJLabelWhenChange.gridx = 1; constraintsJLabelWhenChange.gridy = 1;
			constraintsJLabelWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelWhenChange.ipadx = 3;
			constraintsJLabelWhenChange.ipady = 4;
			constraintsJLabelWhenChange.insets = new java.awt.Insets(4, 5, 4, 5);
			getJPanelChangeMethod().add(getJLabelWhenChange(), constraintsJLabelWhenChange);

			java.awt.GridBagConstraints constraintsJComboBoxWhenChange = new java.awt.GridBagConstraints();
			constraintsJComboBoxWhenChange.gridx = 2; constraintsJComboBoxWhenChange.gridy = 1;
			constraintsJComboBoxWhenChange.gridwidth = 4;
			constraintsJComboBoxWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJComboBoxWhenChange.weightx = 1.0;
			constraintsJComboBoxWhenChange.ipadx = 79;
			constraintsJComboBoxWhenChange.insets = new java.awt.Insets(4, 5, 1, 17);
			getJPanelChangeMethod().add(getJComboBoxWhenChange(), constraintsJComboBoxWhenChange);
            ivjJPanelChangeMethod.setBorder(new EtchedBorder());
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

	com.cannontech.database.data.device.lm.SmartCycleGear s = (com.cannontech.database.data.device.lm.SmartCycleGear)gear;

	s.setControlPercent( new Integer( 
		((Number)getJCSpinFieldControlPercent().getValue()).intValue() ) );

	s.setCyclePeriodLength( new Integer( 
			((Number)getJCSpinFieldCyclePeriod().getValue()).intValue() * 60 ) );

	s.setStartingPeriodCnt( (Integer)getJComboBoxPeriodCount().getSelectedItem() );

	String sendRateString = (String)(getJComboBoxSendRateDigits().getSelectedItem()) + " " + (String)(getJComboBoxSendRateUnits().getSelectedItem());	
    s.setResendRate(SwingUtil.getIntervalSecondsValue(sendRateString));

	if( getJComboBoxMaxCycleCount().getSelectedItem() == null
			|| getJComboBoxMaxCycleCount().getSelectedItem() instanceof String )
	{
		s.setMethodOptionMax( new Integer(0) );
	}
	else
		s.setMethodOptionMax( (Integer)getJComboBoxMaxCycleCount().getSelectedItem() );

	s.setMethodOptionType( StringUtils.removeChars( ' ', getJComboBoxCycleCountSndType().getSelectedItem().toString() ) );
	
	if(getJCheckBoxNoRamp().isSelected())
		s.setFrontRampOption("NoRamp");
	
    if(gear instanceof TargetCycleGear) {
        if(getJTextFieldKWReduction().getText().length() > 0)
            s.setKWReduction(new Double(getJTextFieldKWReduction().getText()));
        else
            s.setKWReduction(new Double(0.0));
    }
    
	return s;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {
    System.out.print(exception.getMessage());
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
	getJComboBoxPeriodCount().addActionListener(this);
	getJComboBoxSendRateDigits().addActionListener(this);
	getJComboBoxSendRateUnits().addActionListener(this);
	getJComboBoxHowToStop().addActionListener(this);
	getJComboBoxCycleCountSndType().addActionListener(this);
	getJComboBoxMaxCycleCount().addActionListener(this);
	getJTextFieldChangeTriggerOffset().addCaretListener(this);
    getJCheckBoxNoRamp().addActionListener(this);
    getJTextFieldKWReduction().addCaretListener(this);

	// user code end
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try 
    {
		setName("SmartCycleGearPanel");
        setPreferredSize(new java.awt.Dimension(410, 350));
        
		java.awt.GridBagConstraints constraintJLabelControlPercent = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJCSpinFieldControlPercent = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJCSpinFieldCyclePeriod = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints142 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJLabelCycleCntSndType = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJLabelCyclePeriod = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJLabelMaxCycleCnt = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJComboBoxMaxCycleCount = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJComboBoxCycleCountSndType = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJLabelPeriodCount = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJComboBoxPeriodCount = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJPanelChangeMethod = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJLabelPercentReduction = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJLabelHowToStop = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJLabelSendRate = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJCSpinFieldPercentReduction = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJComboBoxHowToStop = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJComboBoxSendRateDigits = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJComboBoxSendRateUnits = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints constraintJCheckBoxNoRamp = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints constraintJLabelKWReduction = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints constraintJTextFieldKWReduction = new java.awt.GridBagConstraints();
        
//      constraint for "(min)" label, no one knows why he is here? so we won't paint him for now.
        
//        consGridBagConstraints142.ipady = -14;
//        consGridBagConstraints142.ipadx = -112;
//        consGridBagConstraints142.gridheight = -1;
//        consGridBagConstraints142.gridwidth = -1;
//        consGridBagConstraints142.gridy = 1;
//        consGridBagConstraints142.gridx = 1;
        
        constraintJCheckBoxNoRamp.insets = new java.awt.Insets(0,0,5,0);
        constraintJCheckBoxNoRamp.ipady = -3;
        constraintJCheckBoxNoRamp.ipadx = 200;
        constraintJCheckBoxNoRamp.gridwidth = 3;
        constraintJCheckBoxNoRamp.anchor = java.awt.GridBagConstraints.WEST;
        constraintJCheckBoxNoRamp.gridy = 1;
        constraintJCheckBoxNoRamp.gridx = 1;
        
        constraintJLabelControlPercent.insets = new java.awt.Insets(0,0,5,5);
        constraintJLabelControlPercent.anchor = java.awt.GridBagConstraints.WEST;
        constraintJLabelControlPercent.gridy = 2;
        constraintJLabelControlPercent.gridx = 1;
        
        constraintJCSpinFieldControlPercent.insets = new java.awt.Insets(0,0,5,5);
        constraintJCSpinFieldControlPercent.anchor = java.awt.GridBagConstraints.WEST;
        constraintJCSpinFieldControlPercent.gridy = 2;
        constraintJCSpinFieldControlPercent.gridx = 2;
        
        constraintJLabelCyclePeriod.insets = new java.awt.Insets(0,0,5,5);
        constraintJLabelCyclePeriod.anchor = java.awt.GridBagConstraints.WEST;
        constraintJLabelCyclePeriod.gridy = 3;
        constraintJLabelCyclePeriod.gridx = 1;

        constraintJCSpinFieldCyclePeriod.insets = new java.awt.Insets(0,0,5,5);
        constraintJCSpinFieldCyclePeriod.anchor = java.awt.GridBagConstraints.WEST;
        constraintJCSpinFieldCyclePeriod.gridy = 3;
        constraintJCSpinFieldCyclePeriod.gridx = 2;
        
        constraintJLabelCycleCntSndType.insets = new java.awt.Insets(0,0,5,5);
        constraintJLabelCycleCntSndType.anchor = java.awt.GridBagConstraints.WEST;
        constraintJLabelCycleCntSndType.gridy = 4;
        constraintJLabelCycleCntSndType.gridx = 1;
        
        constraintJComboBoxCycleCountSndType.insets = new java.awt.Insets(0,0,5,5);
        constraintJComboBoxCycleCountSndType.anchor = java.awt.GridBagConstraints.WEST;
        constraintJComboBoxCycleCountSndType.gridwidth = 2;
        constraintJComboBoxCycleCountSndType.gridy = 4;
        constraintJComboBoxCycleCountSndType.gridx = 2;
        
        constraintJLabelMaxCycleCnt.insets = new java.awt.Insets(0,0,5,5);
        constraintJLabelMaxCycleCnt.anchor = java.awt.GridBagConstraints.WEST;
        constraintJLabelMaxCycleCnt.gridy = 5;
        constraintJLabelMaxCycleCnt.gridx = 1;

        constraintJComboBoxMaxCycleCount.insets = new java.awt.Insets(0,0,5,5);
        constraintJComboBoxMaxCycleCount.anchor = java.awt.GridBagConstraints.WEST;
        constraintJComboBoxMaxCycleCount.gridwidth = 2;
        constraintJComboBoxMaxCycleCount.gridy = 5;
        constraintJComboBoxMaxCycleCount.gridx = 2;
        
        constraintJLabelPeriodCount.insets = new java.awt.Insets(0,0,5,5);
        constraintJLabelPeriodCount.anchor = java.awt.GridBagConstraints.WEST;
        constraintJLabelPeriodCount.gridy = 6;
        constraintJLabelPeriodCount.gridx = 1;
        
        constraintJComboBoxPeriodCount.insets = new java.awt.Insets(0,0,5,5);
        constraintJComboBoxPeriodCount.anchor = java.awt.GridBagConstraints.WEST;
        constraintJComboBoxPeriodCount.gridwidth = 2;
        constraintJComboBoxPeriodCount.gridy = 6;
        constraintJComboBoxPeriodCount.gridx = 2;
        
        constraintJLabelSendRate.insets = new java.awt.Insets(0,0,5,5);
        constraintJLabelSendRate.anchor = java.awt.GridBagConstraints.WEST;
        constraintJLabelSendRate.gridwidth = 1;
        constraintJLabelSendRate.gridy = 7;
        constraintJLabelSendRate.gridx = 1;
        
        constraintJComboBoxSendRateDigits.insets = new java.awt.Insets(0,0,5,5);
        constraintJComboBoxSendRateDigits.anchor = java.awt.GridBagConstraints.WEST;
        constraintJComboBoxSendRateDigits.gridy = 7;
        constraintJComboBoxSendRateDigits.gridx = 2;
        
        constraintJComboBoxSendRateUnits.insets = new java.awt.Insets(0,0,5,5);
        constraintJComboBoxSendRateUnits.anchor = java.awt.GridBagConstraints.WEST;
        constraintJComboBoxSendRateUnits.gridy = 7;
        constraintJComboBoxSendRateUnits.gridx = 3;
        
        constraintJLabelHowToStop.insets = new java.awt.Insets(0,0,5,5);
        constraintJLabelHowToStop.anchor = java.awt.GridBagConstraints.WEST;
		constraintJLabelHowToStop.gridy = 8;
		constraintJLabelHowToStop.gridx = 1;
        
        constraintJComboBoxHowToStop.insets = new java.awt.Insets(0,0,5,5);
        constraintJComboBoxHowToStop.anchor = java.awt.GridBagConstraints.WEST;
        constraintJComboBoxHowToStop.gridy = 8;
        constraintJComboBoxHowToStop.gridx = 2;

        constraintJLabelPercentReduction.insets = new java.awt.Insets(0,0,5,5);
        constraintJLabelPercentReduction.anchor = java.awt.GridBagConstraints.WEST;
        constraintJLabelPercentReduction.gridy = 9;
        constraintJLabelPercentReduction.gridx = 1;
        
		constraintJCSpinFieldPercentReduction.insets = new java.awt.Insets(0,0,5,5);
        constraintJCSpinFieldPercentReduction.anchor = java.awt.GridBagConstraints.WEST;
		constraintJCSpinFieldPercentReduction.gridy = 9;
		constraintJCSpinFieldPercentReduction.gridx = 2;
         
        constraintJLabelKWReduction.insets = new java.awt.Insets(0,0,5,5);
        constraintJLabelKWReduction.anchor = java.awt.GridBagConstraints.WEST;
        constraintJLabelKWReduction.gridy = 10;
        constraintJLabelKWReduction.gridx = 1;
        
        constraintJTextFieldKWReduction.insets = new java.awt.Insets(0,0,5,5);
        constraintJTextFieldKWReduction.anchor = java.awt.GridBagConstraints.WEST;
        constraintJTextFieldKWReduction.gridy = 10;
        constraintJTextFieldKWReduction.gridx = 2;
        
		constraintJPanelChangeMethod.insets = new java.awt.Insets(0,0,5,5);
        constraintJPanelChangeMethod.anchor = java.awt.GridBagConstraints.WEST;
        constraintJPanelChangeMethod.gridwidth = 3;
		constraintJPanelChangeMethod.gridy = 11;
		constraintJPanelChangeMethod.gridx = 1;
        
		setLayout(new java.awt.GridBagLayout());
		this.add(getJLabelControlPercent(), constraintJLabelControlPercent);
		this.add(getJCSpinFieldControlPercent(), constraintJCSpinFieldControlPercent);
		this.add(getJCSpinFieldCyclePeriod(), constraintJCSpinFieldCyclePeriod);
		//this.add(getJLabelMin(), consGridBagConstraints142);
		this.add(getJLabelCyclePeriod(), constraintJLabelCyclePeriod);
		this.add(getJLabelCycleCntSndType(), constraintJLabelCycleCntSndType);
		this.add(getJComboBoxCycleCountSndType(), constraintJComboBoxCycleCountSndType);
		this.add(getJLabelMaxCycleCnt(), constraintJLabelMaxCycleCnt);
		this.add(getJComboBoxMaxCycleCount(), constraintJComboBoxMaxCycleCount);
		this.add(getJLabelPeriodCount(), constraintJLabelPeriodCount);
		this.add(getJComboBoxPeriodCount(), constraintJComboBoxPeriodCount);
		this.add(getJLabelSendRate(), constraintJLabelSendRate);
		this.add(getJPanelChangeMethod(), constraintJPanelChangeMethod);
		this.add(getJLabelPercentReduction(), constraintJLabelPercentReduction);
		this.add(getJLabelHowToStop(), constraintJLabelHowToStop);
		this.add(getJComboBoxHowToStop(), constraintJComboBoxHowToStop);
		this.add(getJCSpinFieldPercentReduction(), constraintJCSpinFieldPercentReduction);
		this.add(getJComboBoxSendRateDigits(), constraintJComboBoxSendRateDigits);
		this.add(getJComboBoxSendRateUnits(), constraintJComboBoxSendRateUnits);
		this.add(getJCheckBoxNoRamp(), constraintJCheckBoxNoRamp);
        this.add(getJLabelKWReduction(), constraintJLabelKWReduction);
        this.add(getJTextFieldKWReduction(), constraintJTextFieldKWReduction);
		setSize(402, 430);
    } 
    catch (java.lang.Throwable ivjExc) 
    {
        handleException(ivjExc);
    }
	// user code begin {2}
	getJComboBoxWhenChange().setSelectedItem( LMProgramDirectGear.CHANGE_NONE );
	getJComboBoxSendRateDigits().setSelectedItem("1");
	getJComboBoxSendRateUnits().setSelectedItem("hours");
	getJComboBoxHowToStop().removeItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_TIME_IN ) );		
	getJComboBoxHowToStop().addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_STOP_CYCLE ) );		
	getJComboBoxHowToStop().setSelectedItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_STOP_CYCLE ) );
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
		SmartCycleGearPanel aSmartCycleGearPanel;
		aSmartCycleGearPanel = new SmartCycleGearPanel();
		frame.setContentPane(aSmartCycleGearPanel);
		frame.setSize(aSmartCycleGearPanel.getSize());
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

	com.cannontech.database.data.device.lm.SmartCycleGear s = (com.cannontech.database.data.device.lm.SmartCycleGear)gear;

	getJCSpinFieldControlPercent().setValue( s.getControlPercent() );

	getJCSpinFieldCyclePeriod().setValue( new Integer( s.getCyclePeriodLength().intValue() / 60 ) );

	getJComboBoxPeriodCount().setSelectedItem( s.getStartingPeriodCnt() );

	SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxSendRateDigits(), getJComboBoxSendRateUnits(), s.getResendRate().intValue());

	if( s.getMethodOptionMax().intValue() > 0 )
		getJComboBoxMaxCycleCount().setSelectedItem( s.getMethodOptionMax() );
	else
		getJComboBoxMaxCycleCount().setSelectedItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
	
		getJComboBoxCycleCountSndType().setSelectedItem( StringUtils.addCharBetweenWords( ' ', s.getMethodOptionType() ) );
	
	if(s.getFrontRampOption().compareTo(CtiUtilities.STRING_NONE) != 0) {
		getJCheckBoxNoRamp().setSelected(true);
	} else {
	    getJCheckBoxNoRamp().setSelected(false);
	}
    
    if(gear instanceof TargetCycleGear) {
        if(s.getKWReduction().doubleValue() == 0.0)
            getJTextFieldKWReduction().setText("");
        else
            getJTextFieldKWReduction().setText(s.getKWReduction().toString());
    }
}

public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
	/**
	 * This method initializes jCheckBoxNoRamp
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private javax.swing.JCheckBox getJCheckBoxNoRamp() {
		if(jCheckBoxNoRamp == null) {
			jCheckBoxNoRamp = new javax.swing.JCheckBox();
			jCheckBoxNoRamp.setText("No Ramp (Expresscom Only)");
            jCheckBoxNoRamp.setPreferredSize(new Dimension (165,23));
						
		}
		return jCheckBoxNoRamp;
	}
    
    public void setTargetCycle(boolean truth) {
        getJLabelKWReduction().setVisible(truth);
        getJTextFieldKWReduction().setVisible(truth);
    }
}
