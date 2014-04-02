package com.cannontech.dbeditor.wizard.device.lmconstraint;

import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteHolidaySchedule;
import com.cannontech.database.data.lite.LiteSeasonSchedule;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.yukon.IDatabaseCache;
/**
 * Insert the type's description here. 
 * Creation date: (3/2/2004 10:09:59 AM)
 * @author: 
 */
public class LMProgramConstraintPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener {
	private com.cannontech.common.gui.unchanging.JCheckBoxDayChooser ivjJCheckBoxDayChooser = null;
	private javax.swing.JComboBox ivjJComboBoxHoliday = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMaxActivateTime = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMaxDailyOps = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMaxHoursDaily = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMaxHoursMonthly = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMaxHoursSeasonal = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMinActivateTime = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMinRestart = null;
	private javax.swing.JLabel ivjJLabelAnnually = null;
	private javax.swing.JLabel ivjJLabelDaily = null;
	private javax.swing.JLabel ivjJLabelDailyOps = null;
	private javax.swing.JLabel ivjJLabelHoliday = null;
	private javax.swing.JLabel ivjJLabelMaxActivate = null;
	private javax.swing.JLabel ivjJLabelMinActivate = null;
	private javax.swing.JLabel ivjJLabelMonthly = null;
	private javax.swing.JLabel ivjJLabelRestart = null;
	private javax.swing.JLabel ivjJLabelSeasonal = null;
	private javax.swing.JPanel ivjJPanelMaxHours = null;
	private javax.swing.JPanel ivjJPanelMaxValues = null;
	private javax.swing.JPanel ivjJPanelMinTimes = null;
	private javax.swing.JLabel ivjJLabelConstraintName = null;
	private javax.swing.JTextField ivjJTextFieldConstraintName = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabelSeasonChooser = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMaxHoursAnnually = null;
	private javax.swing.JLabel ivjJLabelMaxActSeconds = null;
	private javax.swing.JLabel ivjJLabelMinReSeconds = null;
	private javax.swing.JLabel ivjJLabelMinReSeconds1 = null;
	private javax.swing.JComboBox ivjJComboBoxSeasonSchedule = null;
	private javax.swing.JLabel ivjJLabelHolidayUsage = null;
	private javax.swing.JRadioButton ivjJRadioButtonExclude = null;
	private javax.swing.JRadioButton ivjJRadioButtonForce = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMProgramConstraintPanel.this.getJCheckBoxDayChooser()) 
				connEtoC6(e);
			if (e.getSource() == LMProgramConstraintPanel.this.getJComboBoxHoliday()) 
				connEtoC7(e);
			if (e.getSource() == LMProgramConstraintPanel.this.getJComboBoxSeasonSchedule()) 
				connEtoC2(e);
			if (e.getSource() == LMProgramConstraintPanel.this.getJRadioButtonExclude()) 
				connEtoC3(e);
			if (e.getSource() == LMProgramConstraintPanel.this.getJRadioButtonForce()) 
				connEtoC4(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMProgramConstraintPanel.this.getJTextFieldConstraintName()) 
				connEtoC1(e);
		};
	};
/**
 * LMProgramConstraintPanel constructor comment.
 */
public LMProgramConstraintPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextFieldConstraintName.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramConstraintPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JComboBoxSeasonSchedule.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramConstraintPanel.jComboBoxSeasonSchedule_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		this.jComboBoxSeasonSchedule_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JRadioButtonExclude.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramConstraintPanel.jRadioButtonExclude_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		this.jRadioButtonExclude_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JRadioButton1.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramConstraintPanel.jRadioButton1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		this.jRadioButtonForce_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JCheckBoxDayChooser.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramConstraintPanel.jCheckBoxDayChooser_Action(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		this.jCheckBoxDayChooser_Action(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramConstraintPanel.addDataInputPanelListener(Lcom.cannontech.common.gui.util.DataInputPanelListener;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		this.jComboBoxHoliday_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
public void enableHolidayUsage(boolean val)
{
	getJRadioButtonExclude().setEnabled(val);
	getJRadioButtonForce().setEnabled(val);
}

/**
 * Return the JCheckBoxDayChooser property value.
 * @return com.cannontech.common.gui.unchanging.JCheckBoxDayChooser
 */
private com.cannontech.common.gui.unchanging.JCheckBoxDayChooser getJCheckBoxDayChooser() {
	if (ivjJCheckBoxDayChooser == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Day Selection");
			ivjJCheckBoxDayChooser = new com.cannontech.common.gui.unchanging.JCheckBoxDayChooser();
			ivjJCheckBoxDayChooser.setName("JCheckBoxDayChooser");
			ivjJCheckBoxDayChooser.setPreferredSize(new java.awt.Dimension(340, 86));
			ivjJCheckBoxDayChooser.setBorder(ivjLocalBorder);
			ivjJCheckBoxDayChooser.setMinimumSize(new java.awt.Dimension(340, 86));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDayChooser;
}
/**
 * Return the JComboBoxHoliday property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getJComboBoxHoliday() {
	if (ivjJComboBoxHoliday == null) {
		try {
			ivjJComboBoxHoliday = new javax.swing.JComboBox();
			ivjJComboBoxHoliday.setName("JComboBoxHoliday");
			ivjJComboBoxHoliday.setToolTipText("Holiday schedule used to exclude control");
			ivjJComboBoxHoliday.setPreferredSize(new java.awt.Dimension(155, 23));
			IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				List<LiteHolidaySchedule> holidaySch = cache.getAllHolidaySchedules();
				for( int i = 0; i < holidaySch.size(); i++ )
				{
					
					if(holidaySch.get(i).toString().equalsIgnoreCase("Empty Holiday Schedule"))
					{
						// dont add it to drop down list
					}else ivjJComboBoxHoliday.addItem( holidaySch.get(i) );
				}
				
			}
			getJComboBoxHoliday().addItem(CtiUtilities.STRING_NONE);
			getJComboBoxHoliday().setSelectedItem(CtiUtilities.STRING_NONE);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxHoliday;
}
/**
 * Return the JComboBoxSeasonSchedule property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getJComboBoxSeasonSchedule() {
	if (ivjJComboBoxSeasonSchedule == null) {
		try {
			ivjJComboBoxSeasonSchedule = new javax.swing.JComboBox();
			ivjJComboBoxSeasonSchedule.setName("JComboBoxSeasonSchedule");
			ivjJComboBoxSeasonSchedule.setToolTipText("Season schedule used to exclude control");
			ivjJComboBoxSeasonSchedule.setPreferredSize(new java.awt.Dimension(155, 23));
			IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				List<LiteSeasonSchedule> seasonSch = cache.getAllSeasonSchedules();
				for( int i = 0; i < seasonSch.size(); i++ )
					ivjJComboBoxSeasonSchedule.addItem( seasonSch.get(i) );
			}
			getJComboBoxSeasonSchedule().addItem(CtiUtilities.STRING_NONE);
			getJComboBoxSeasonSchedule().setSelectedItem(CtiUtilities.STRING_NONE);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSeasonSchedule;
}
/**
 * Return the JCSpinFieldMaxActivateTime property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMaxActivateTime() {
	if (ivjJCSpinFieldMaxActivateTime == null) {
		try {
			ivjJCSpinFieldMaxActivateTime = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMaxActivateTime.setName("JCSpinFieldMaxActivateTime");
			ivjJCSpinFieldMaxActivateTime.setToolTipText("Minimum time the program must be activated before it is stopped");
			ivjJCSpinFieldMaxActivateTime.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(0)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldMaxActivateTime;
}
/**
 * Return the JCSpinFieldMaxDailyOps property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMaxDailyOps() {
	if (ivjJCSpinFieldMaxDailyOps == null) {
		try {
			ivjJCSpinFieldMaxDailyOps = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMaxDailyOps.setName("JCSpinFieldMaxDailyOps");
			ivjJCSpinFieldMaxDailyOps.setToolTipText("Minimum time the program must be stopped before it is activated again");
			ivjJCSpinFieldMaxDailyOps.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(0)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldMaxDailyOps;
}
/**
 * Return the JCSpinFieldMaxHoursAnnually property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMaxHoursAnnually() {
	if (ivjJCSpinFieldMaxHoursAnnually == null) {
		try {
			ivjJCSpinFieldMaxHoursAnnually = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMaxHoursAnnually.setName("JCSpinFieldMaxHoursAnnually");
			ivjJCSpinFieldMaxHoursAnnually.setToolTipText("Max hours allowed, zero means no limit");
			ivjJCSpinFieldMaxHoursAnnually.setDataProperties(
								new com.klg.jclass.field.DataProperties(
									new com.klg.jclass.field.validate.JCIntegerValidator(
									null, new Integer(0), new Integer(99999), null, true, 
									null, new Integer(1), "#,##0.###;-#,##0.###", false, 
									false, false, null, new Integer(0)), 
									new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
									new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
									new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldMaxHoursAnnually;
}
/**
 * Return the JCSpinFieldMaxHoursDaily property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMaxHoursDaily() {
	if (ivjJCSpinFieldMaxHoursDaily == null) {
		try {
			ivjJCSpinFieldMaxHoursDaily = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMaxHoursDaily.setName("JCSpinFieldMaxHoursDaily");
			ivjJCSpinFieldMaxHoursDaily.setToolTipText("Max hours allowed, zero means no limit");
			ivjJCSpinFieldMaxHoursDaily.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(0)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldMaxHoursDaily;
}
/**
 * Return the JCSpinFieldMaxHoursMonthly property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMaxHoursMonthly() {
	if (ivjJCSpinFieldMaxHoursMonthly == null) {
		try {
			ivjJCSpinFieldMaxHoursMonthly = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMaxHoursMonthly.setName("JCSpinFieldMaxHoursMonthly");
			ivjJCSpinFieldMaxHoursMonthly.setToolTipText("Max hours allowed, zero means no limit");
			ivjJCSpinFieldMaxHoursMonthly.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(0)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldMaxHoursMonthly;
}
/**
 * Return the JCSpinFieldMaxHoursSeasonal property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMaxHoursSeasonal() {
	if (ivjJCSpinFieldMaxHoursSeasonal == null) {
		try {
			ivjJCSpinFieldMaxHoursSeasonal = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMaxHoursSeasonal.setName("JCSpinFieldMaxHoursSeasonal");
			ivjJCSpinFieldMaxHoursSeasonal.setToolTipText("Max hours allowed, zero means no limit");
			ivjJCSpinFieldMaxHoursSeasonal.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(0)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldMaxHoursSeasonal;
}
/**
 * Return the JCSpinFieldMinActivateTime property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMinActivateTime() {
	if (ivjJCSpinFieldMinActivateTime == null) {
		try {
			ivjJCSpinFieldMinActivateTime = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMinActivateTime.setName("JCSpinFieldMinActivateTime");
			ivjJCSpinFieldMinActivateTime.setToolTipText("Minimum time the program must be activated before it is stopped");
			ivjJCSpinFieldMinActivateTime.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(0)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldMinActivateTime;
}
/**
 * Return the JCSpinFieldMinRestart property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMinRestart() {
	if (ivjJCSpinFieldMinRestart == null) {
		try {
			ivjJCSpinFieldMinRestart = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMinRestart.setName("JCSpinFieldMinRestart");
			ivjJCSpinFieldMinRestart.setToolTipText("Minimum time the program must be stopped before it is activated again");
			ivjJCSpinFieldMinRestart.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(0), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(0)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldMinRestart;
}
/**
 * Return the JLabelAnnually property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelAnnually() {
	if (ivjJLabelAnnually == null) {
		try {
			ivjJLabelAnnually = new javax.swing.JLabel();
			ivjJLabelAnnually.setName("JLabelAnnually");
			ivjJLabelAnnually.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAnnually.setText("Annually:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelAnnually;
}
/**
 * Return the JLabelConstraintName property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelConstraintName() {
	if (ivjJLabelConstraintName == null) {
		try {
			ivjJLabelConstraintName = new javax.swing.JLabel();
			ivjJLabelConstraintName.setName("JLabelConstraintName");
			ivjJLabelConstraintName.setPreferredSize(new java.awt.Dimension(127, 18));
			ivjJLabelConstraintName.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelConstraintName.setText("Constraint Name: ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelConstraintName;
}
/**
 * Return the JLabelDaily property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelDaily() {
	if (ivjJLabelDaily == null) {
		try {
			ivjJLabelDaily = new javax.swing.JLabel();
			ivjJLabelDaily.setName("JLabelDaily");
			ivjJLabelDaily.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDaily.setText("Daily:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelDaily;
}
/**
 * Return the JLabelDailyOps property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelDailyOps() {
	if (ivjJLabelDailyOps == null) {
		try {
			ivjJLabelDailyOps = new javax.swing.JLabel();
			ivjJLabelDailyOps.setName("JLabelDailyOps");
			ivjJLabelDailyOps.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelDailyOps.setText("Daily Ops: ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelDailyOps;
}
/**
 * Return the JLabelHoliday property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelHoliday() {
	if (ivjJLabelHoliday == null) {
		try {
			ivjJLabelHoliday = new javax.swing.JLabel();
			ivjJLabelHoliday.setName("JLabelHoliday");
			ivjJLabelHoliday.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelHoliday.setText("Holiday Schedule: ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelHoliday;
}
/**
 * Return the JLabelHolidayUsage property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelHolidayUsage() {
	if (ivjJLabelHolidayUsage == null) {
		try {
			ivjJLabelHolidayUsage = new javax.swing.JLabel();
			ivjJLabelHolidayUsage.setName("JLabelHolidayUsage");
			ivjJLabelHolidayUsage.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelHolidayUsage.setText("Holiday Usage: ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelHolidayUsage;
}
/**
 * Return the JLabelMaxActivate property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelMaxActivate() {
	if (ivjJLabelMaxActivate == null) {
		try {
			ivjJLabelMaxActivate = new javax.swing.JLabel();
			ivjJLabelMaxActivate.setName("JLabelMaxActivate");
			ivjJLabelMaxActivate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMaxActivate.setText("Activate: ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelMaxActivate;
}
/**
 * Return the JLabelMaxActSeconds property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelMaxActSeconds() {
	if (ivjJLabelMaxActSeconds == null) {
		try {
			ivjJLabelMaxActSeconds = new javax.swing.JLabel();
			ivjJLabelMaxActSeconds.setName("JLabelMaxActSeconds");
			ivjJLabelMaxActSeconds.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelMaxActSeconds.setText("sec.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelMaxActSeconds;
}
/**
 * Return the JLabelMinActivate property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelMinActivate() {
	if (ivjJLabelMinActivate == null) {
		try {
			ivjJLabelMinActivate = new javax.swing.JLabel();
			ivjJLabelMinActivate.setName("JLabelMinActivate");
			ivjJLabelMinActivate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMinActivate.setText("Activate:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinActivate;
}
/**
 * Return the JLabelMinReSeconds property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelMinReSeconds() {
	if (ivjJLabelMinReSeconds == null) {
		try {
			ivjJLabelMinReSeconds = new javax.swing.JLabel();
			ivjJLabelMinReSeconds.setName("JLabelMinReSeconds");
			ivjJLabelMinReSeconds.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelMinReSeconds.setText("sec.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinReSeconds;
}
/**
 * Return the JLabelMinReSeconds1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelMinReSeconds1() {
	if (ivjJLabelMinReSeconds1 == null) {
		try {
			ivjJLabelMinReSeconds1 = new javax.swing.JLabel();
			ivjJLabelMinReSeconds1.setName("JLabelMinReSeconds1");
			ivjJLabelMinReSeconds1.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelMinReSeconds1.setText("sec.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinReSeconds1;
}
/**
 * Return the JLabelMonthly property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelMonthly() {
	if (ivjJLabelMonthly == null) {
		try {
			ivjJLabelMonthly = new javax.swing.JLabel();
			ivjJLabelMonthly.setName("JLabelMonthly");
			ivjJLabelMonthly.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMonthly.setText("Monthly:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelMonthly;
}
/**
 * Return the JLabelRestart property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelRestart() {
	if (ivjJLabelRestart == null) {
		try {
			ivjJLabelRestart = new javax.swing.JLabel();
			ivjJLabelRestart.setName("JLabelRestart");
			ivjJLabelRestart.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRestart.setText("Restart:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelRestart;
}
/**
 * Return the JLabelSeasonal property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelSeasonal() {
	if (ivjJLabelSeasonal == null) {
		try {
			ivjJLabelSeasonal = new javax.swing.JLabel();
			ivjJLabelSeasonal.setName("JLabelSeasonal");
			ivjJLabelSeasonal.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSeasonal.setText("Seasonal:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeasonal;
}
/**
 * Return the JLabelSeasonChooser property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelSeasonChooser() {
	if (ivjJLabelSeasonChooser == null) {
		try {
			ivjJLabelSeasonChooser = new javax.swing.JLabel();
			ivjJLabelSeasonChooser.setName("JLabelSeasonChooser");
			ivjJLabelSeasonChooser.setPreferredSize(new java.awt.Dimension(127, 18));
			ivjJLabelSeasonChooser.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelSeasonChooser.setText("Season Schedule: ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeasonChooser;
}
/**
 * Return the JPanelMaxHours property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelMaxHours() {
	if (ivjJPanelMaxHours == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder1.setTitle("Max Hour Allowance");
			ivjJPanelMaxHours = new javax.swing.JPanel();
			ivjJPanelMaxHours.setName("JPanelMaxHours");
			ivjJPanelMaxHours.setPreferredSize(new java.awt.Dimension(340, 75));
			ivjJPanelMaxHours.setBorder(ivjLocalBorder1);
			ivjJPanelMaxHours.setLayout(new java.awt.GridBagLayout());
			ivjJPanelMaxHours.setMinimumSize(new java.awt.Dimension(340, 75));

			java.awt.GridBagConstraints constraintsJCSpinFieldMaxHoursDaily = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMaxHoursDaily.gridx = 2; constraintsJCSpinFieldMaxHoursDaily.gridy = 1;
			constraintsJCSpinFieldMaxHoursDaily.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldMaxHoursDaily.ipadx = 63;
			constraintsJCSpinFieldMaxHoursDaily.ipady = 19;
			constraintsJCSpinFieldMaxHoursDaily.insets = new java.awt.Insets(6, 1, 2, 24);
			getJPanelMaxHours().add(getJCSpinFieldMaxHoursDaily(), constraintsJCSpinFieldMaxHoursDaily);

			java.awt.GridBagConstraints constraintsJLabelDaily = new java.awt.GridBagConstraints();
			constraintsJLabelDaily.gridx = 1; constraintsJLabelDaily.gridy = 1;
			constraintsJLabelDaily.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelDaily.ipadx = 22;
			constraintsJLabelDaily.ipady = -1;
			constraintsJLabelDaily.insets = new java.awt.Insets(5, 11, 3, 0);
			getJPanelMaxHours().add(getJLabelDaily(), constraintsJLabelDaily);

			java.awt.GridBagConstraints constraintsJLabelSeasonal = new java.awt.GridBagConstraints();
			constraintsJLabelSeasonal.gridx = 3; constraintsJLabelSeasonal.gridy = 1;
			constraintsJLabelSeasonal.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelSeasonal.ipadx = 6;
			constraintsJLabelSeasonal.ipady = -1;
			constraintsJLabelSeasonal.insets = new java.awt.Insets(5, 24, 3, 4);
			getJPanelMaxHours().add(getJLabelSeasonal(), constraintsJLabelSeasonal);

			java.awt.GridBagConstraints constraintsJLabelMonthly = new java.awt.GridBagConstraints();
			constraintsJLabelMonthly.gridx = 1; constraintsJLabelMonthly.gridy = 2;
			constraintsJLabelMonthly.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelMonthly.ipadx = 4;
			constraintsJLabelMonthly.ipady = -1;
			constraintsJLabelMonthly.insets = new java.awt.Insets(3, 11, 16, 0);
			getJPanelMaxHours().add(getJLabelMonthly(), constraintsJLabelMonthly);

			java.awt.GridBagConstraints constraintsJLabelAnnually = new java.awt.GridBagConstraints();
			constraintsJLabelAnnually.gridx = 3; constraintsJLabelAnnually.gridy = 2;
			constraintsJLabelAnnually.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAnnually.ipadx = 11;
			constraintsJLabelAnnually.ipady = -1;
			constraintsJLabelAnnually.insets = new java.awt.Insets(3, 24, 16, 4);
			getJPanelMaxHours().add(getJLabelAnnually(), constraintsJLabelAnnually);

			java.awt.GridBagConstraints constraintsJCSpinFieldMaxHoursMonthly = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMaxHoursMonthly.gridx = 2; constraintsJCSpinFieldMaxHoursMonthly.gridy = 2;
			constraintsJCSpinFieldMaxHoursMonthly.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldMaxHoursMonthly.ipadx = 63;
			constraintsJCSpinFieldMaxHoursMonthly.ipady = 19;
			constraintsJCSpinFieldMaxHoursMonthly.insets = new java.awt.Insets(5, 1, 12, 24);
			getJPanelMaxHours().add(getJCSpinFieldMaxHoursMonthly(), constraintsJCSpinFieldMaxHoursMonthly);

			java.awt.GridBagConstraints constraintsJCSpinFieldMaxHoursSeasonal = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMaxHoursSeasonal.gridx = 4; constraintsJCSpinFieldMaxHoursSeasonal.gridy = 1;
			constraintsJCSpinFieldMaxHoursSeasonal.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldMaxHoursSeasonal.ipadx = 63;
			constraintsJCSpinFieldMaxHoursSeasonal.ipady = 19;
			constraintsJCSpinFieldMaxHoursSeasonal.insets = new java.awt.Insets(6, 4, 2, 26);
			getJPanelMaxHours().add(getJCSpinFieldMaxHoursSeasonal(), constraintsJCSpinFieldMaxHoursSeasonal);

			java.awt.GridBagConstraints constraintsJCSpinFieldMaxHoursAnnually = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMaxHoursAnnually.gridx = 4; constraintsJCSpinFieldMaxHoursAnnually.gridy = 2;
			constraintsJCSpinFieldMaxHoursAnnually.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldMaxHoursAnnually.ipadx = 63;
			constraintsJCSpinFieldMaxHoursAnnually.ipady = 19;
			constraintsJCSpinFieldMaxHoursAnnually.insets = new java.awt.Insets(2, 4, 15, 26);
			getJPanelMaxHours().add(getJCSpinFieldMaxHoursAnnually(), constraintsJCSpinFieldMaxHoursAnnually);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelMaxHours;
}
/**
 * Return the JPanelMaxValues property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelMaxValues() {
	if (ivjJPanelMaxValues == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder2.setTitle("Max");
			ivjJPanelMaxValues = new javax.swing.JPanel();
			ivjJPanelMaxValues.setName("JPanelMaxValues");
			ivjJPanelMaxValues.setPreferredSize(new java.awt.Dimension(170, 82));
			ivjJPanelMaxValues.setBorder(ivjLocalBorder2);
			ivjJPanelMaxValues.setLayout(new java.awt.GridBagLayout());
			ivjJPanelMaxValues.setMinimumSize(new java.awt.Dimension(170, 82));

			java.awt.GridBagConstraints constraintsJCSpinFieldMaxActivateTime = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMaxActivateTime.gridx = 2; constraintsJCSpinFieldMaxActivateTime.gridy = 1;
			constraintsJCSpinFieldMaxActivateTime.ipadx = 49;
			constraintsJCSpinFieldMaxActivateTime.ipady = 19;
			constraintsJCSpinFieldMaxActivateTime.insets = new java.awt.Insets(18, 0, 3, 2);
			getJPanelMaxValues().add(getJCSpinFieldMaxActivateTime(), constraintsJCSpinFieldMaxActivateTime);

			java.awt.GridBagConstraints constraintsJCSpinFieldMaxDailyOps = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMaxDailyOps.gridx = 2; constraintsJCSpinFieldMaxDailyOps.gridy = 2;
			constraintsJCSpinFieldMaxDailyOps.ipadx = 49;
			constraintsJCSpinFieldMaxDailyOps.ipady = 19;
			constraintsJCSpinFieldMaxDailyOps.insets = new java.awt.Insets(4, 0, 20, 2);
			getJPanelMaxValues().add(getJCSpinFieldMaxDailyOps(), constraintsJCSpinFieldMaxDailyOps);

			java.awt.GridBagConstraints constraintsJLabelMaxActivate = new java.awt.GridBagConstraints();
			constraintsJLabelMaxActivate.gridx = 1; constraintsJLabelMaxActivate.gridy = 1;
			constraintsJLabelMaxActivate.ipadx = 12;
			constraintsJLabelMaxActivate.insets = new java.awt.Insets(19, 12, 6, 3);
			getJPanelMaxValues().add(getJLabelMaxActivate(), constraintsJLabelMaxActivate);

			java.awt.GridBagConstraints constraintsJLabelDailyOps = new java.awt.GridBagConstraints();
			constraintsJLabelDailyOps.gridx = 1; constraintsJLabelDailyOps.gridy = 2;
			constraintsJLabelDailyOps.ipadx = 3;
			constraintsJLabelDailyOps.insets = new java.awt.Insets(5, 12, 23, 0);
			getJPanelMaxValues().add(getJLabelDailyOps(), constraintsJLabelDailyOps);

			java.awt.GridBagConstraints constraintsJLabelMaxActSeconds = new java.awt.GridBagConstraints();
			constraintsJLabelMaxActSeconds.gridx = 3; constraintsJLabelMaxActSeconds.gridy = 1;
			constraintsJLabelMaxActSeconds.ipadx = 21;
			constraintsJLabelMaxActSeconds.insets = new java.awt.Insets(21, 3, 6, 0);
			getJPanelMaxValues().add(getJLabelMaxActSeconds(), constraintsJLabelMaxActSeconds);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelMaxValues;
}
/**
 * Return the JPanelMinTimes property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelMinTimes() {
	if (ivjJPanelMinTimes == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder3;
			ivjLocalBorder3 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder3.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder3.setTitle("Min");
			ivjJPanelMinTimes = new javax.swing.JPanel();
			ivjJPanelMinTimes.setName("JPanelMinTimes");
			ivjJPanelMinTimes.setPreferredSize(new java.awt.Dimension(170, 82));
			ivjJPanelMinTimes.setBorder(ivjLocalBorder3);
			ivjJPanelMinTimes.setLayout(new java.awt.GridBagLayout());
			ivjJPanelMinTimes.setMinimumSize(new java.awt.Dimension(170, 82));

			java.awt.GridBagConstraints constraintsJLabelMinActivate = new java.awt.GridBagConstraints();
			constraintsJLabelMinActivate.gridx = 1; constraintsJLabelMinActivate.gridy = 1;
			constraintsJLabelMinActivate.ipadx = 12;
			constraintsJLabelMinActivate.ipady = -2;
			constraintsJLabelMinActivate.insets = new java.awt.Insets(19, 12, 8, 1);
			getJPanelMinTimes().add(getJLabelMinActivate(), constraintsJLabelMinActivate);

			java.awt.GridBagConstraints constraintsJLabelRestart = new java.awt.GridBagConstraints();
			constraintsJLabelRestart.gridx = 1; constraintsJLabelRestart.gridy = 2;
			constraintsJLabelRestart.ipadx = 13;
			constraintsJLabelRestart.ipady = -2;
			constraintsJLabelRestart.insets = new java.awt.Insets(5, 12, 25, 1);
			getJPanelMinTimes().add(getJLabelRestart(), constraintsJLabelRestart);

			java.awt.GridBagConstraints constraintsJCSpinFieldMinActivateTime = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMinActivateTime.gridx = 2; constraintsJCSpinFieldMinActivateTime.gridy = 1;
			constraintsJCSpinFieldMinActivateTime.ipadx = 55;
			constraintsJCSpinFieldMinActivateTime.ipady = 19;
			constraintsJCSpinFieldMinActivateTime.insets = new java.awt.Insets(18, 1, 3, 3);
			getJPanelMinTimes().add(getJCSpinFieldMinActivateTime(), constraintsJCSpinFieldMinActivateTime);

			java.awt.GridBagConstraints constraintsJCSpinFieldMinRestart = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMinRestart.gridx = 2; constraintsJCSpinFieldMinRestart.gridy = 2;
			constraintsJCSpinFieldMinRestart.ipadx = 55;
			constraintsJCSpinFieldMinRestart.ipady = 19;
			constraintsJCSpinFieldMinRestart.insets = new java.awt.Insets(4, 1, 20, 3);
			getJPanelMinTimes().add(getJCSpinFieldMinRestart(), constraintsJCSpinFieldMinRestart);

			java.awt.GridBagConstraints constraintsJLabelMinReSeconds = new java.awt.GridBagConstraints();
			constraintsJLabelMinReSeconds.gridx = 3; constraintsJLabelMinReSeconds.gridy = 2;
			constraintsJLabelMinReSeconds.ipadx = 7;
			constraintsJLabelMinReSeconds.insets = new java.awt.Insets(7, 4, 23, 6);
			getJPanelMinTimes().add(getJLabelMinReSeconds(), constraintsJLabelMinReSeconds);

			java.awt.GridBagConstraints constraintsJLabelMinReSeconds1 = new java.awt.GridBagConstraints();
			constraintsJLabelMinReSeconds1.gridx = 3; constraintsJLabelMinReSeconds1.gridy = 1;
			constraintsJLabelMinReSeconds1.ipadx = 7;
			constraintsJLabelMinReSeconds1.insets = new java.awt.Insets(20, 4, 7, 6);
			getJPanelMinTimes().add(getJLabelMinReSeconds1(), constraintsJLabelMinReSeconds1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelMinTimes;
}
/**
 * Return the JRadioButtonExclude property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getJRadioButtonExclude() {
	if (ivjJRadioButtonExclude == null) {
		try {
			ivjJRadioButtonExclude = new javax.swing.JRadioButton();
			ivjJRadioButtonExclude.setName("JRadioButtonExclude");
			ivjJRadioButtonExclude.setSelected(true);
			ivjJRadioButtonExclude.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJRadioButtonExclude.setText("Exclude");
			ivjJRadioButtonExclude.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonExclude;
}
/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getJRadioButtonForce() {
	if (ivjJRadioButtonForce == null) {
		try {
			ivjJRadioButtonForce = new javax.swing.JRadioButton();
			ivjJRadioButtonForce.setName("JRadioButtonForce");
			ivjJRadioButtonForce.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJRadioButtonForce.setText("Force");
			ivjJRadioButtonForce.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonForce;
}
/**
 * Return the JTextFieldConstraintName property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getJTextFieldConstraintName() {
	if (ivjJTextFieldConstraintName == null) {
		try {
			ivjJTextFieldConstraintName = new javax.swing.JTextField();
			ivjJTextFieldConstraintName.setName("JTextFieldConstraintName");
			ivjJTextFieldConstraintName.setPreferredSize(new java.awt.Dimension(149, 20));
			ivjJTextFieldConstraintName.setMinimumSize(new java.awt.Dimension(149, 20));
			ivjJTextFieldConstraintName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_LMCONSTRAINTS) );
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldConstraintName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
@Override
public Object getValue(Object o) 
{
	LMProgramConstraint con = (LMProgramConstraint)o;
	 
	if(con == null)
		con = new LMProgramConstraint();
	
	String constraintName = getJTextFieldConstraintName().getText();
	if (StringUtils.isEmpty(constraintName)) {
	    String message = "The constraint name cannot be empty.";
	    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	    throw new UnsupportedOperationException(message);
	}
	
	con.setConstraintName(constraintName);
	
	con.setMaxHoursDaily( new Integer( ((Number)getJCSpinFieldMaxHoursDaily().getValue()).intValue() ) );
	con.setMaxHoursMonthly( new Integer( ((Number)getJCSpinFieldMaxHoursMonthly().getValue()).intValue() ) );
	con.setMaxHoursSeasonal( new Integer( ((Number)getJCSpinFieldMaxHoursSeasonal().getValue()).intValue() ) );
	con.setMaxHoursAnnually( new Integer( ((Number)getJCSpinFieldMaxHoursAnnually().getValue()).intValue() ) );
	con.setMinActivateTime( new Integer( ((Number)getJCSpinFieldMinActivateTime().getValue()).intValue() ) );
	con.setMinRestartTime( new Integer( ((Number)getJCSpinFieldMinRestart().getValue()).intValue() ) );
	con.setMaxActivateTime( new Integer( ((Number)getJCSpinFieldMaxActivateTime().getValue()).intValue() ) );
	con.setMaxDailyOps( new Integer( ((Number)getJCSpinFieldMaxDailyOps().getValue()).intValue() ) );
	
	String dayString = getJCheckBoxDayChooser().getSelectedDays7Chars();
	if( getJComboBoxHoliday().getSelectedItem() != CtiUtilities.STRING_NONE )
	{
		con.setHolidayScheduleID( new Integer(((com.cannontech.database.data.lite.LiteHolidaySchedule)getJComboBoxHoliday().getSelectedItem()).getHolidayScheduleID() ));
		if(getJRadioButtonForce().isSelected())
			dayString = dayString + "F";
		else
			dayString = dayString + "E";
	}				
	else
	{
		con.setHolidayScheduleID( new Integer(0) );
		dayString = dayString + "N";
	}
	
	con.setAvailableWeekdays( dayString );
	
	if( getJComboBoxSeasonSchedule().getSelectedItem() instanceof LiteSeasonSchedule )
		con.setSeasonScheduleID( new Integer(((LiteSeasonSchedule)getJComboBoxSeasonSchedule().getSelectedItem()).getScheduleID() ));
	else
		con.setSeasonScheduleID( new Integer(0) ); //may be null or have "(none)" selected


	return con;
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
private void initConnections() throws java.lang.Exception {
	getJCSpinFieldMaxHoursAnnually().addValueListener( this );
	getJCSpinFieldMaxHoursDaily().addValueListener( this );
	getJCSpinFieldMaxHoursMonthly().addValueListener( this );
	getJCSpinFieldMaxHoursSeasonal().addValueListener( this );
	getJCSpinFieldMinActivateTime().addValueListener( this );
	getJCSpinFieldMinRestart().addValueListener( this );
	getJCSpinFieldMaxDailyOps().addValueListener( this );
	getJCSpinFieldMaxActivateTime().addValueListener( this );
	getJTextFieldConstraintName().addCaretListener(ivjEventHandler);
	getJCheckBoxDayChooser().addActionListener(ivjEventHandler);
	getJComboBoxHoliday().addActionListener(ivjEventHandler);
	getJComboBoxSeasonSchedule().addActionListener(ivjEventHandler);
	getJRadioButtonExclude().addActionListener(ivjEventHandler);
	getJRadioButtonForce().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("LMProgramConstraintPanel");
		setPreferredSize(new java.awt.Dimension(350, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 403);
		setMinimumSize(new java.awt.Dimension(350, 360));

		java.awt.GridBagConstraints constraintsJCheckBoxDayChooser = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDayChooser.gridx = 1; constraintsJCheckBoxDayChooser.gridy = 4;
		constraintsJCheckBoxDayChooser.gridwidth = 3;
		constraintsJCheckBoxDayChooser.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJCheckBoxDayChooser.weightx = 1.0;
		constraintsJCheckBoxDayChooser.weighty = 1.0;
		constraintsJCheckBoxDayChooser.insets = new java.awt.Insets(3, 4, 1, 6);
		add(getJCheckBoxDayChooser(), constraintsJCheckBoxDayChooser);

		java.awt.GridBagConstraints constraintsJComboBoxHoliday = new java.awt.GridBagConstraints();
		constraintsJComboBoxHoliday.gridx = 2; constraintsJComboBoxHoliday.gridy = 5;
		constraintsJComboBoxHoliday.gridwidth = 2;
		constraintsJComboBoxHoliday.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxHoliday.weightx = 1.0;
		constraintsJComboBoxHoliday.ipadx = 29;
		constraintsJComboBoxHoliday.insets = new java.awt.Insets(2, 4, 3, 54);
		add(getJComboBoxHoliday(), constraintsJComboBoxHoliday);

		java.awt.GridBagConstraints constraintsJPanelMaxHours = new java.awt.GridBagConstraints();
		constraintsJPanelMaxHours.gridx = 1; constraintsJPanelMaxHours.gridy = 7;
		constraintsJPanelMaxHours.gridwidth = 3;
		constraintsJPanelMaxHours.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelMaxHours.weightx = 1.0;
		constraintsJPanelMaxHours.weighty = 1.0;
		constraintsJPanelMaxHours.insets = new java.awt.Insets(2, 4, 23, 6);
		add(getJPanelMaxHours(), constraintsJPanelMaxHours);

		java.awt.GridBagConstraints constraintsJLabelHoliday = new java.awt.GridBagConstraints();
		constraintsJLabelHoliday.gridx = 1; constraintsJLabelHoliday.gridy = 5;
		constraintsJLabelHoliday.ipadx = 7;
		constraintsJLabelHoliday.insets = new java.awt.Insets(8, 24, 6, 3);
		add(getJLabelHoliday(), constraintsJLabelHoliday);

		java.awt.GridBagConstraints constraintsJLabelConstraintName = new java.awt.GridBagConstraints();
		constraintsJLabelConstraintName.gridx = 1; constraintsJLabelConstraintName.gridy = 1;
		constraintsJLabelConstraintName.ipadx = 7;
		constraintsJLabelConstraintName.ipady = 4;
		constraintsJLabelConstraintName.insets = new java.awt.Insets(20, 22, 6, 7);
		add(getJLabelConstraintName(), constraintsJLabelConstraintName);

		java.awt.GridBagConstraints constraintsJTextFieldConstraintName = new java.awt.GridBagConstraints();
		constraintsJTextFieldConstraintName.gridx = 2; constraintsJTextFieldConstraintName.gridy = 1;
		constraintsJTextFieldConstraintName.gridwidth = 2;
		constraintsJTextFieldConstraintName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldConstraintName.weightx = 1.0;
		constraintsJTextFieldConstraintName.ipadx = 1;
		constraintsJTextFieldConstraintName.insets = new java.awt.Insets(18, 4, 6, 59);
		add(getJTextFieldConstraintName(), constraintsJTextFieldConstraintName);

		java.awt.GridBagConstraints constraintsJPanelMaxValues = new java.awt.GridBagConstraints();
		constraintsJPanelMaxValues.gridx = 1; constraintsJPanelMaxValues.gridy = 3;
		constraintsJPanelMaxValues.gridwidth = 2;
		constraintsJPanelMaxValues.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJPanelMaxValues.weightx = 1.0;
		constraintsJPanelMaxValues.weighty = 1.0;
		constraintsJPanelMaxValues.ipadx = 4;
		constraintsJPanelMaxValues.ipady = -11;
		constraintsJPanelMaxValues.insets = new java.awt.Insets(7, 4, 2, 45);
		add(getJPanelMaxValues(), constraintsJPanelMaxValues);

		java.awt.GridBagConstraints constraintsJPanelMinTimes = new java.awt.GridBagConstraints();
		constraintsJPanelMinTimes.gridx = 2; constraintsJPanelMinTimes.gridy = 3;
		constraintsJPanelMinTimes.gridwidth = 2;
		constraintsJPanelMinTimes.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJPanelMinTimes.weightx = 1.0;
		constraintsJPanelMinTimes.weighty = 1.0;
		constraintsJPanelMinTimes.ipady = -11;
		constraintsJPanelMinTimes.insets = new java.awt.Insets(7, 40, 2, 3);
		add(getJPanelMinTimes(), constraintsJPanelMinTimes);

		java.awt.GridBagConstraints constraintsJComboBoxSeasonSchedule = new java.awt.GridBagConstraints();
		constraintsJComboBoxSeasonSchedule.gridx = 2; constraintsJComboBoxSeasonSchedule.gridy = 2;
		constraintsJComboBoxSeasonSchedule.gridwidth = 2;
		constraintsJComboBoxSeasonSchedule.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxSeasonSchedule.weightx = 1.0;
		constraintsJComboBoxSeasonSchedule.ipadx = 24;
		constraintsJComboBoxSeasonSchedule.insets = new java.awt.Insets(6, 4, 6, 59);
		add(getJComboBoxSeasonSchedule(), constraintsJComboBoxSeasonSchedule);

		java.awt.GridBagConstraints constraintsJLabelSeasonChooser = new java.awt.GridBagConstraints();
		constraintsJLabelSeasonChooser.gridx = 1; constraintsJLabelSeasonChooser.gridy = 2;
		constraintsJLabelSeasonChooser.ipadx = 3;
		constraintsJLabelSeasonChooser.ipady = 4;
		constraintsJLabelSeasonChooser.insets = new java.awt.Insets(8, 22, 9, 7);
		add(getJLabelSeasonChooser(), constraintsJLabelSeasonChooser);

		java.awt.GridBagConstraints constraintsJLabelHolidayUsage = new java.awt.GridBagConstraints();
		constraintsJLabelHolidayUsage.gridx = 1; constraintsJLabelHolidayUsage.gridy = 6;
		constraintsJLabelHolidayUsage.ipadx = 18;
		constraintsJLabelHolidayUsage.insets = new java.awt.Insets(6, 24, 6, 9);
		add(getJLabelHolidayUsage(), constraintsJLabelHolidayUsage);

		java.awt.GridBagConstraints constraintsJRadioButtonExclude = new java.awt.GridBagConstraints();
		constraintsJRadioButtonExclude.gridx = 2; constraintsJRadioButtonExclude.gridy = 6;
		constraintsJRadioButtonExclude.ipadx = 6;
		constraintsJRadioButtonExclude.ipady = -5;
		constraintsJRadioButtonExclude.insets = new java.awt.Insets(3, 4, 1, 2);
		add(getJRadioButtonExclude(), constraintsJRadioButtonExclude);

		java.awt.GridBagConstraints constraintsJRadioButtonForce = new java.awt.GridBagConstraints();
		constraintsJRadioButtonForce.gridx = 3; constraintsJRadioButtonForce.gridy = 6;
		constraintsJRadioButtonForce.ipadx = 5;
		constraintsJRadioButtonForce.ipady = -5;
		constraintsJRadioButtonForce.insets = new java.awt.Insets(3, 3, 1, 57);
		add(getJRadioButtonForce(), constraintsJRadioButtonForce);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	//	default day of week and season check boxes to all!
	getJCheckBoxDayChooser().setSelectedCheckBoxes( "YYYYYYYN" );
	getJCheckBoxDayChooser().setHolidayVisible(false);
}
/**
 * Comment
 */
public void jCheckBoxDayChooser_Action(java.awt.event.ActionEvent e) 
{
	fireInputUpdate();
	
	return;
}
/**
 * Comment
 */
public void jComboBoxHoliday_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	enableHolidayUsage(getJComboBoxHoliday().getSelectedItem() != CtiUtilities.STRING_NONE );
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jComboBoxSeasonSchedule_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jRadioButtonExclude_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getJRadioButtonForce().setSelected(! getJRadioButtonExclude().isSelected());
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jRadioButtonForce_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getJRadioButtonExclude().setSelected(! getJRadioButtonForce().isSelected());
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
		LMProgramConstraintPanel aLMProgramConstraintPanel;
		aLMProgramConstraintPanel = new LMProgramConstraintPanel();
		frame.setContentPane(aLMProgramConstraintPanel);
		frame.setSize(aLMProgramConstraintPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
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
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
@Override
public void setValue(Object o) 
{
	LMProgramConstraint con = (LMProgramConstraint)o;
	if(con == null)
		con = new LMProgramConstraint();
	else
	{
	
		getJTextFieldConstraintName().setText(con.getConstraintName());
	
		getJCheckBoxDayChooser().setSelectedCheckBoxes( con.getAvailableWeekdays() );
		getJCheckBoxDayChooser().setHolidaySelected(false);
		
		getJCSpinFieldMaxHoursDaily().setValue( con.getMaxHoursDaily() );
		getJCSpinFieldMaxHoursMonthly().setValue( con.getMaxHoursMonthly()  );
		getJCSpinFieldMaxHoursSeasonal().setValue( con.getMaxHoursSeasonal() );
		getJCSpinFieldMaxHoursAnnually().setValue( con.getMaxHoursAnnually()  );
		getJCSpinFieldMinActivateTime().setValue( con.getMinActivateTime() );
		getJCSpinFieldMinRestart().setValue( con.getMinRestartTime() );
		getJCSpinFieldMaxActivateTime().setValue( con.getMaxActivateTime() );
		getJCSpinFieldMaxDailyOps().setValue( con.getMaxDailyOps() );
	
		getJComboBoxHoliday().removeItem(CtiUtilities.STRING_NONE);
		for( int i = 0; i < getJComboBoxHoliday().getItemCount(); i++ )
			if( ((com.cannontech.database.data.lite.LiteHolidaySchedule)getJComboBoxHoliday().getItemAt(i)).getHolidayScheduleID()
				== con.getHolidayScheduleID().intValue() )
			{
				getJComboBoxHoliday().setSelectedIndex(i);
				break;
			}
		getJComboBoxHoliday().addItem(CtiUtilities.STRING_NONE);
		if(con.getHolidayScheduleID().compareTo(new Integer(0)) == 0)
		{
			getJComboBoxHoliday().setSelectedItem(CtiUtilities.STRING_NONE);
		}
		String holidayInfo = con.getAvailableWeekdays();
		enableHolidayUsage(true);
		if(holidayInfo.charAt(7) == 'E')
		{
			getJRadioButtonExclude().setSelected(true);
		}
		else if(holidayInfo.charAt(7) == 'F')
		{
			getJRadioButtonForce().doClick();
		}
		else
		{
			enableHolidayUsage(false);
			getJComboBoxHoliday().setSelectedItem(CtiUtilities.STRING_NONE);
		}
		
		getJComboBoxSeasonSchedule().removeItem(CtiUtilities.STRING_NONE);
		for( int i = 0; i < getJComboBoxSeasonSchedule().getItemCount(); i++ )
			if( ((com.cannontech.database.data.lite.LiteSeasonSchedule)getJComboBoxSeasonSchedule().getItemAt(i)).getScheduleID()
				== con.getSeasonScheduleID().intValue() )
			{
				getJComboBoxSeasonSchedule().setSelectedIndex(i);
				break;
			}
		
		getJComboBoxSeasonSchedule().addItem(CtiUtilities.STRING_NONE);
		if(con.getSeasonScheduleID().compareTo(new Integer(0)) == 0)
		{
			getJComboBoxSeasonSchedule().setSelectedItem(CtiUtilities.STRING_NONE);
		}
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

public boolean isInputValid() {
    String constraintName = getJTextFieldConstraintName().getText();
    if (StringUtils.isEmpty(constraintName)) {
        setErrorString("The program constraint must have a name.");
        return false;
    }else {
        return true;
    }
}
}
