package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.database.data.device.lm.LMProgramBase;

public class LMProgramBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener, com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	//Control Methods
	public static final String TIME_REFRESH_CONTROL = "TimeRefresh";
	public static final String SMART_CYCLE_CONTROL = "SmartCycle";
	public static final String MASTER_CYCLE_CONTROL = "MasterCycle";
	public static final String ROTATION_CONTROL = "Rotation";
	public static final String LATCHING_CONTROL = "Latching";
	// Stop Types
	public static final String RESTORE_STOP = "Restore";
	public static final String TIME_IN_STOP = "Time-In";
	private com.cannontech.common.gui.unchanging.JCheckBoxDayChooser ivjJCheckBoxDayChooser = null;
	private javax.swing.JComboBox ivjJComboBoxOperationalState = null;
	private javax.swing.JLabel ivjJLabelOperationalState = null;
	private javax.swing.JPanel ivjJPanelMaxHours = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMaxHoursAnnaully = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMaxHoursSeasonal = null;
	private javax.swing.JLabel ivjJLabelAnnually = null;
	private javax.swing.JLabel ivjJLabelDaily = null;
	private javax.swing.JLabel ivjJLabelMonthly = null;
	private javax.swing.JLabel ivjJLabelSeasonal = null;
	private com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooser ivjJCheckBoxSeasonChooser = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMaxHoursMonthly = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMaxHoursDaily = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMinActivateTime = null;
	private javax.swing.JLabel ivjJLabelActivate = null;
	private javax.swing.JLabel ivjJLabelRestart = null;
	private javax.swing.JPanel ivjJPanelMinTimes = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMinRestart = null;
	private javax.swing.JLabel ivjJLabelActualProgType = null;
	private javax.swing.JLabel ivjJLabelProgramType = null;
	private javax.swing.JComboBox ivjJComboBoxHoliday = null;
	private javax.swing.JLabel ivjJLabelHoliday = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramBasePanel() {
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
		connEtoC1(e);
	if (e.getSource() == getJCheckBoxDayChooser()) 
		connEtoC7(e);
	if (e.getSource() == getJComboBoxHoliday()) 
		connEtoC8(e);
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
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxOperationalState.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxFallAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
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
 * connEtoC4:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSpringAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
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
 * connEtoC5:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSummerAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
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
 * connEtoC6:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxWinterAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
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
 * connEtoC7:  (JCheckBoxDayChooser.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.jCheckBoxDayChooser_Action(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxDayChooser_Action(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
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
 * Return the JCheckBoxDayChooser property value.
 * @return com.cannontech.common.gui.unchanging.JCheckBoxDayChooser
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.unchanging.JCheckBoxDayChooser getJCheckBoxDayChooser() {
	if (ivjJCheckBoxDayChooser == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Week Day Availability");
			ivjJCheckBoxDayChooser = new com.cannontech.common.gui.unchanging.JCheckBoxDayChooser();
			ivjJCheckBoxDayChooser.setName("JCheckBoxDayChooser");
			ivjJCheckBoxDayChooser.setBorder(ivjLocalBorder1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDayChooser;
}
/**
 * Return the JCheckBoxSeasonChooser property value.
 * @return com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooser
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooser getJCheckBoxSeasonChooser() {
	if (ivjJCheckBoxSeasonChooser == null) {
		try {
			ivjJCheckBoxSeasonChooser = new com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooser();
			ivjJCheckBoxSeasonChooser.setName("JCheckBoxSeasonChooser");
			// user code begin {1}

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder2.setTitle("Season Availability");
			ivjJCheckBoxSeasonChooser.setBorder(ivjLocalBorder2);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSeasonChooser;
}
/**
 * Return the JComboBoxHoliday property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxHoliday() {
	if (ivjJComboBoxHoliday == null) {
		try {
			ivjJComboBoxHoliday = new javax.swing.JComboBox();
			ivjJComboBoxHoliday.setName("JComboBoxHoliday");
			ivjJComboBoxHoliday.setToolTipText("Holiday schedule used to exclude control");
			// user code begin {1}

			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List holidaySch = cache.getAllHolidaySchedules();
				for( int i = 0; i < holidaySch.size(); i++ )
					ivjJComboBoxHoliday.addItem( holidaySch.get(i) );
			}

			//not implemented yet
			ivjJComboBoxHoliday.setEnabled(false);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxHoliday;
}
/**
 * Return the JComboBoxControl property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOperationalState() {
	if (ivjJComboBoxOperationalState == null) {
		try {
			ivjJComboBoxOperationalState = new javax.swing.JComboBox();
			ivjJComboBoxOperationalState.setName("JComboBoxOperationalState");
			// user code begin {1}

			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_AUTOMATIC );
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_MANUALONLY );
			
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
 * Return the JCSpinFieldMaxHoursAnnaully property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMaxHoursAnnaully() {
	if (ivjJCSpinFieldMaxHoursAnnaully == null) {
		try {
			ivjJCSpinFieldMaxHoursAnnaully = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMaxHoursAnnaully.setName("JCSpinFieldMaxHoursAnnaully");
			ivjJCSpinFieldMaxHoursAnnaully.setToolTipText("Max hours allowed, zero means no limit");
			// user code begin {1}

			ivjJCSpinFieldMaxHoursAnnaully.setDataProperties(
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
	return ivjJCSpinFieldMaxHoursAnnaully;
}
/**
 * Return the JCSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMaxHoursDaily() {
	if (ivjJCSpinFieldMaxHoursDaily == null) {
		try {
			ivjJCSpinFieldMaxHoursDaily = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMaxHoursDaily.setName("JCSpinFieldMaxHoursDaily");
			ivjJCSpinFieldMaxHoursDaily.setToolTipText("Max hours allowed, zero means no limit");
			// user code begin {1}
			
			ivjJCSpinFieldMaxHoursDaily.setDataProperties(
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
	return ivjJCSpinFieldMaxHoursDaily;
}
/**
 * Return the JCSpinFieldMaxHours1 property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMaxHoursMonthly() {
	if (ivjJCSpinFieldMaxHoursMonthly == null) {
		try {
			ivjJCSpinFieldMaxHoursMonthly = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMaxHoursMonthly.setName("JCSpinFieldMaxHoursMonthly");
			ivjJCSpinFieldMaxHoursMonthly.setToolTipText("Max hours allowed, zero means no limit");
			// user code begin {1}

			ivjJCSpinFieldMaxHoursMonthly.setDataProperties(
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
	return ivjJCSpinFieldMaxHoursMonthly;
}
/**
 * Return the JCSpinFieldMaxHoursSeasonal property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMaxHoursSeasonal() {
	if (ivjJCSpinFieldMaxHoursSeasonal == null) {
		try {
			ivjJCSpinFieldMaxHoursSeasonal = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMaxHoursSeasonal.setName("JCSpinFieldMaxHoursSeasonal");
			ivjJCSpinFieldMaxHoursSeasonal.setToolTipText("Max hours allowed, zero means no limit");
			// user code begin {1}

			ivjJCSpinFieldMaxHoursSeasonal.setDataProperties(
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
	return ivjJCSpinFieldMaxHoursSeasonal;
}
/**
 * Return the JCSpinFieldMinActivateTime property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMinActivateTime() {
	if (ivjJCSpinFieldMinActivateTime == null) {
		try {
			ivjJCSpinFieldMinActivateTime = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMinActivateTime.setName("JCSpinFieldMinActivateTime");
			ivjJCSpinFieldMinActivateTime.setToolTipText("Minimum time the program must be activated before it is stopped");
			ivjJCSpinFieldMinActivateTime.setBounds(75, 18, 56, 20);
			// user code begin {1}

			ivjJCSpinFieldMinActivateTime.setDataProperties(
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
	return ivjJCSpinFieldMinActivateTime;
}
/**
 * Return the JCSpinFieldRestart property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMinRestart() {
	if (ivjJCSpinFieldMinRestart == null) {
		try {
			ivjJCSpinFieldMinRestart = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMinRestart.setName("JCSpinFieldMinRestart");
			ivjJCSpinFieldMinRestart.setToolTipText("Minimum time the program must be stopped before it is activated again");
			ivjJCSpinFieldMinRestart.setBounds(75, 46, 56, 20);
			// user code begin {1}

			ivjJCSpinFieldMinRestart.setDataProperties(
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
	return ivjJCSpinFieldMinRestart;
}
/**
 * Return the JLabelActivate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActivate() {
	if (ivjJLabelActivate == null) {
		try {
			ivjJLabelActivate = new javax.swing.JLabel();
			ivjJLabelActivate.setName("JLabelActivate");
			ivjJLabelActivate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActivate.setText("Activate:");
			ivjJLabelActivate.setBounds(13, 21, 56, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActivate;
}
/**
 * Return the JLabelActualProgType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualProgType() {
	if (ivjJLabelActualProgType == null) {
		try {
			ivjJLabelActualProgType = new javax.swing.JLabel();
			ivjJLabelActualProgType.setName("JLabelActualProgType");
			ivjJLabelActualProgType.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelActualProgType.setText("(unknown)");
			// user code begin {1}

			ivjJLabelActualProgType.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualProgType;
}
/**
 * Return the JLabelAnnually property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAnnually() {
	if (ivjJLabelAnnually == null) {
		try {
			ivjJLabelAnnually = new javax.swing.JLabel();
			ivjJLabelAnnually.setName("JLabelAnnually");
			ivjJLabelAnnually.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAnnually.setText("Annually:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAnnually;
}
/**
 * Return the JLabelDuration property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDaily() {
	if (ivjJLabelDaily == null) {
		try {
			ivjJLabelDaily = new javax.swing.JLabel();
			ivjJLabelDaily.setName("JLabelDaily");
			ivjJLabelDaily.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDaily.setText("Daily:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDaily;
}
/**
 * Return the JLabelHoliday property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHoliday() {
	if (ivjJLabelHoliday == null) {
		try {
			ivjJLabelHoliday = new javax.swing.JLabel();
			ivjJLabelHoliday.setName("JLabelHoliday");
			ivjJLabelHoliday.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelHoliday.setText("Holiday:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHoliday;
}
/**
 * Return the JLabelMethodRate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMonthly() {
	if (ivjJLabelMonthly == null) {
		try {
			ivjJLabelMonthly = new javax.swing.JLabel();
			ivjJLabelMonthly.setName("JLabelMonthly");
			ivjJLabelMonthly.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMonthly.setText("Monthly:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMonthly;
}
/**
 * Return the SelectLabel property value.
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOperationalState() {
	if (ivjJLabelOperationalState == null) {
		try {
			ivjJLabelOperationalState = new javax.swing.JLabel();
			ivjJLabelOperationalState.setName("JLabelOperationalState");
			ivjJLabelOperationalState.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelOperationalState.setText("Operational State:");
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
 * Return the JLabelProgramType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelProgramType() {
	if (ivjJLabelProgramType == null) {
		try {
			ivjJLabelProgramType = new javax.swing.JLabel();
			ivjJLabelProgramType.setName("JLabelProgramType");
			ivjJLabelProgramType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelProgramType.setText("Program Type:");
			// user code begin {1}

			ivjJLabelProgramType.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelProgramType;
}
/**
 * Return the JLabelRestart property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRestart() {
	if (ivjJLabelRestart == null) {
		try {
			ivjJLabelRestart = new javax.swing.JLabel();
			ivjJLabelRestart.setName("JLabelRestart");
			ivjJLabelRestart.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRestart.setText("Restart:");
			ivjJLabelRestart.setBounds(14, 49, 56, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRestart;
}
/**
 * Return the JLabelStopType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeasonal() {
	if (ivjJLabelSeasonal == null) {
		try {
			ivjJLabelSeasonal = new javax.swing.JLabel();
			ivjJLabelSeasonal.setName("JLabelSeasonal");
			ivjJLabelSeasonal.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSeasonal.setText("Seasonal:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeasonal;
}
/**
 * Return the JPanelMaxHours property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelMaxHours() {
	if (ivjJPanelMaxHours == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Maximum Hours");
			ivjJPanelMaxHours = new javax.swing.JPanel();
			ivjJPanelMaxHours.setName("JPanelMaxHours");
			ivjJPanelMaxHours.setBorder(ivjLocalBorder);
			ivjJPanelMaxHours.setLayout(new java.awt.GridBagLayout());

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

			java.awt.GridBagConstraints constraintsJCSpinFieldMaxHoursAnnaully = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMaxHoursAnnaully.gridx = 4; constraintsJCSpinFieldMaxHoursAnnaully.gridy = 2;
			constraintsJCSpinFieldMaxHoursAnnaully.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldMaxHoursAnnaully.ipadx = 63;
			constraintsJCSpinFieldMaxHoursAnnaully.ipady = 19;
			constraintsJCSpinFieldMaxHoursAnnaully.insets = new java.awt.Insets(2, 4, 15, 26);
			getJPanelMaxHours().add(getJCSpinFieldMaxHoursAnnaully(), constraintsJCSpinFieldMaxHoursAnnaully);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelMaxHours;
}
/**
 * Return the JPanelMinTimes property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelMinTimes() {
	if (ivjJPanelMinTimes == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder2.setTitle("Min Times (minutes)");
			ivjJPanelMinTimes = new javax.swing.JPanel();
			ivjJPanelMinTimes.setName("JPanelMinTimes");
			ivjJPanelMinTimes.setBorder(ivjLocalBorder2);
			ivjJPanelMinTimes.setLayout(null);
			getJPanelMinTimes().add(getJLabelActivate(), getJLabelActivate().getName());
			getJPanelMinTimes().add(getJLabelRestart(), getJLabelRestart().getName());
			getJPanelMinTimes().add(getJCSpinFieldMinActivateTime(), getJCSpinFieldMinActivateTime().getName());
			getJPanelMinTimes().add(getJCSpinFieldMinRestart(), getJCSpinFieldMinRestart().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelMinTimes;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setToolTipText("Name of Program");
			// user code begin {1}

			ivjJTextFieldName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
			
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
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMProgramBase program = (LMProgramBase)o;

	program.setName( getJTextFieldName().getText() );
	program.getProgram().setControlType( getJComboBoxOperationalState().getSelectedItem().toString() );

	program.getProgram().setAvailableSeasons( getJCheckBoxSeasonChooser().getSelectedSeasons4Chars() );
	program.getProgram().setAvailableWeekDays( getJCheckBoxDayChooser().getSelectedDays8Chars() );

	program.getProgram().setMaxHoursDaily( new Integer( ((Number)getJCSpinFieldMaxHoursDaily().getValue()).intValue() ) );
	program.getProgram().setMaxHoursMonthly( new Integer( ((Number)getJCSpinFieldMaxHoursMonthly().getValue()).intValue() ) );
	program.getProgram().setMaxHoursSeasonal( new Integer( ((Number)getJCSpinFieldMaxHoursSeasonal().getValue()).intValue() ) );
	program.getProgram().setMaxHoursAnnually( new Integer( ((Number)getJCSpinFieldMaxHoursAnnaully().getValue()).intValue() ) );
	program.getProgram().setMinActivateTime( new Integer( ((Number)getJCSpinFieldMinActivateTime().getValue()).intValue() ) );
	program.getProgram().setMinRestartTime( new Integer( ((Number)getJCSpinFieldMinRestart().getValue()).intValue() ) );

/*	if( getJComboBoxHoliday().getSelectedItem() != null )
		program.getProgram().setHolidayScheduleId( ((com.cannontech.database.data.lite.LiteHolidaySchedule)getJComboBoxHoliday().getSelectedItem()).getHolidayScheduleID() );
	else
		sch.setHolidayScheduleId( 0 );
*/
	return o;
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

	getJCSpinFieldMaxHoursAnnaully().addValueListener( this );
	getJCSpinFieldMaxHoursDaily().addValueListener( this );
	getJCSpinFieldMaxHoursMonthly().addValueListener( this );
	getJCSpinFieldMaxHoursSeasonal().addValueListener( this );
	getJCSpinFieldMinActivateTime().addValueListener( this );
	getJCSpinFieldMinRestart().addValueListener( this );
	
	// user code end
	getJComboBoxOperationalState().addActionListener(this);
	getJTextFieldName().addCaretListener(this);
	getJCheckBoxSeasonChooser().addJCheckBoxSeasonChooserListener(this);
	getJCheckBoxDayChooser().addActionListener(this);
	getJComboBoxHoliday().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramBasePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(369, 392);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.gridwidth = 2;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipadx = 11;
		constraintsJLabelName.ipady = -3;
		constraintsJLabelName.insets = new java.awt.Insets(13, 9, 4, 6);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 3; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.gridwidth = 3;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 279;
		constraintsJTextFieldName.insets = new java.awt.Insets(11, 7, 2, 13);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJLabelOperationalState = new java.awt.GridBagConstraints();
		constraintsJLabelOperationalState.gridx = 1; constraintsJLabelOperationalState.gridy = 3;
		constraintsJLabelOperationalState.gridwidth = 3;
		constraintsJLabelOperationalState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelOperationalState.ipadx = 3;
		constraintsJLabelOperationalState.ipady = -1;
		constraintsJLabelOperationalState.insets = new java.awt.Insets(6, 8, 7, 1);
		add(getJLabelOperationalState(), constraintsJLabelOperationalState);

		java.awt.GridBagConstraints constraintsJComboBoxOperationalState = new java.awt.GridBagConstraints();
		constraintsJComboBoxOperationalState.gridx = 4; constraintsJComboBoxOperationalState.gridy = 3;
		constraintsJComboBoxOperationalState.gridwidth = 2;
		constraintsJComboBoxOperationalState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxOperationalState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxOperationalState.weightx = 1.0;
		constraintsJComboBoxOperationalState.ipadx = 101;
		constraintsJComboBoxOperationalState.insets = new java.awt.Insets(4, 1, 4, 14);
		add(getJComboBoxOperationalState(), constraintsJComboBoxOperationalState);

		java.awt.GridBagConstraints constraintsJPanelMaxHours = new java.awt.GridBagConstraints();
		constraintsJPanelMaxHours.gridx = 1; constraintsJPanelMaxHours.gridy = 7;
		constraintsJPanelMaxHours.gridwidth = 5;
		constraintsJPanelMaxHours.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelMaxHours.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelMaxHours.weightx = 1.0;
		constraintsJPanelMaxHours.weighty = 1.0;
		constraintsJPanelMaxHours.ipadx = -8;
		constraintsJPanelMaxHours.ipady = -12;
		constraintsJPanelMaxHours.insets = new java.awt.Insets(5, 5, 15, 14);
		add(getJPanelMaxHours(), constraintsJPanelMaxHours);

		java.awt.GridBagConstraints constraintsJCheckBoxDayChooser = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDayChooser.gridx = 1; constraintsJCheckBoxDayChooser.gridy = 5;
		constraintsJCheckBoxDayChooser.gridwidth = 5;
		constraintsJCheckBoxDayChooser.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJCheckBoxDayChooser.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxDayChooser.weightx = 1.0;
		constraintsJCheckBoxDayChooser.weighty = 1.0;
		constraintsJCheckBoxDayChooser.ipadx = -5;
		constraintsJCheckBoxDayChooser.ipady = 10;
		constraintsJCheckBoxDayChooser.insets = new java.awt.Insets(2, 8, 4, 11);
		add(getJCheckBoxDayChooser(), constraintsJCheckBoxDayChooser);

		java.awt.GridBagConstraints constraintsJCheckBoxSeasonChooser = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSeasonChooser.gridx = 1; constraintsJCheckBoxSeasonChooser.gridy = 4;
		constraintsJCheckBoxSeasonChooser.gridwidth = 4;
		constraintsJCheckBoxSeasonChooser.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJCheckBoxSeasonChooser.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxSeasonChooser.weightx = 1.0;
		constraintsJCheckBoxSeasonChooser.weighty = 1.0;
		constraintsJCheckBoxSeasonChooser.ipadx = 140;
		constraintsJCheckBoxSeasonChooser.ipady = 29;
		constraintsJCheckBoxSeasonChooser.insets = new java.awt.Insets(4, 7, 2, 4);
		add(getJCheckBoxSeasonChooser(), constraintsJCheckBoxSeasonChooser);

		java.awt.GridBagConstraints constraintsJPanelMinTimes = new java.awt.GridBagConstraints();
		constraintsJPanelMinTimes.gridx = 5; constraintsJPanelMinTimes.gridy = 4;
		constraintsJPanelMinTimes.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelMinTimes.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelMinTimes.weightx = 1.0;
		constraintsJPanelMinTimes.weighty = 1.0;
		constraintsJPanelMinTimes.ipadx = 167;
		constraintsJPanelMinTimes.ipady = 77;
		constraintsJPanelMinTimes.insets = new java.awt.Insets(4, 4, 2, 10);
		add(getJPanelMinTimes(), constraintsJPanelMinTimes);

		java.awt.GridBagConstraints constraintsJLabelProgramType = new java.awt.GridBagConstraints();
		constraintsJLabelProgramType.gridx = 1; constraintsJLabelProgramType.gridy = 2;
		constraintsJLabelProgramType.gridwidth = 3;
		constraintsJLabelProgramType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelProgramType.ipadx = 5;
		constraintsJLabelProgramType.ipady = 1;
		constraintsJLabelProgramType.insets = new java.awt.Insets(2, 9, 3, 19);
		add(getJLabelProgramType(), constraintsJLabelProgramType);

		java.awt.GridBagConstraints constraintsJLabelActualProgType = new java.awt.GridBagConstraints();
		constraintsJLabelActualProgType.gridx = 4; constraintsJLabelActualProgType.gridy = 2;
		constraintsJLabelActualProgType.gridwidth = 2;
		constraintsJLabelActualProgType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualProgType.ipadx = 151;
		constraintsJLabelActualProgType.ipady = 4;
		constraintsJLabelActualProgType.insets = new java.awt.Insets(2, 1, 3, 16);
		add(getJLabelActualProgType(), constraintsJLabelActualProgType);

		java.awt.GridBagConstraints constraintsJLabelHoliday = new java.awt.GridBagConstraints();
		constraintsJLabelHoliday.gridx = 1; constraintsJLabelHoliday.gridy = 6;
		constraintsJLabelHoliday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelHoliday.ipadx = 1;
		constraintsJLabelHoliday.ipady = -2;
		constraintsJLabelHoliday.insets = new java.awt.Insets(8, 8, 9, 1);
		add(getJLabelHoliday(), constraintsJLabelHoliday);

		java.awt.GridBagConstraints constraintsJComboBoxHoliday = new java.awt.GridBagConstraints();
		constraintsJComboBoxHoliday.gridx = 2; constraintsJComboBoxHoliday.gridy = 6;
		constraintsJComboBoxHoliday.gridwidth = 4;
		constraintsJComboBoxHoliday.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxHoliday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxHoliday.weightx = 1.0;
		constraintsJComboBoxHoliday.ipadx = 29;
		constraintsJComboBoxHoliday.insets = new java.awt.Insets(4, 1, 4, 159);
		add(getJComboBoxHoliday(), constraintsJComboBoxHoliday);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//default day of week and season check boxes to all!
	getJCheckBoxSeasonChooser().setSelectedSeasons( "YYYY" );
	getJCheckBoxDayChooser().setSelectedCheckBoxes( "YYYYYYYN" );

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


	return true;
}
/**
 * Comment
 */
public void jCheckBoxDayChooser_Action(java.awt.event.ActionEvent e) 
{
	//com.cannontech.clientutils.CTILogger.info("+++++++++++++++ A CHeck Box was pressed!!!");
	
	fireInputUpdate();
	return;
}
/**
 * Method to handle events for the JCheckBoxSeasonChooserListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JCheckBoxFallAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getJCheckBoxSeasonChooser()) 
		connEtoC3(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCheckBoxSeasonChooserListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JCheckBoxSpringAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getJCheckBoxSeasonChooser()) 
		connEtoC4(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCheckBoxSeasonChooserListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JCheckBoxSummerAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getJCheckBoxSeasonChooser()) 
		connEtoC5(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCheckBoxSeasonChooserListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JCheckBoxWinterAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getJCheckBoxSeasonChooser()) 
		connEtoC6(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMProgramBasePanel aLMProgramBasePanel;
		aLMProgramBasePanel = new LMProgramBasePanel();
		frame.setContentPane(aLMProgramBasePanel);
		frame.setSize(aLMProgramBasePanel.getSize());
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
	LMProgramBase program = (LMProgramBase)o;

	getJTextFieldName().setText( program.getPAOName() );
	getJComboBoxOperationalState().setSelectedItem( program.getProgram().getControlType().toString() );

	getJCheckBoxSeasonChooser().setSelectedSeasons( program.getProgram().getAvailableSeasons() );
	getJCheckBoxDayChooser().setSelectedCheckBoxes( program.getProgram().getAvailableWeekDays() );
	getJCSpinFieldMaxHoursDaily().setValue( program.getProgram().getMaxHoursDaily() );
	getJCSpinFieldMaxHoursMonthly().setValue( program.getProgram().getMaxHoursMonthly() );
	getJCSpinFieldMaxHoursSeasonal().setValue( program.getProgram().getMaxHoursSeasonal() );
	getJCSpinFieldMaxHoursAnnaully().setValue( program.getProgram().getMaxHoursAnnually() );
	getJCSpinFieldMinActivateTime().setValue( program.getProgram().getMinActivateTime() );
	getJCSpinFieldMinRestart().setValue( program.getProgram().getMinRestartTime() );

	getJLabelProgramType().setVisible( true );
	getJLabelActualProgType().setVisible( true );
	getJLabelActualProgType().setText( program.getPAOType() );

	//not implemented yet
/*	for( int i = 0; i < getJComboBoxHoliday().getItemCount(); i++ )
		if( ((com.cannontech.database.data.lite.LiteHolidaySchedule)getJComboBoxHoliday().getItemAt(i)).getHolidayScheduleID()
			 == sched.getHolidayScheduleId() )
		{
			getJComboBoxHoliday().setSelectedIndex(i);
			break;
		}*/

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
	D0CB838494G88G88GEFF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DFCD45515F8D4042831C60D92B5EAD42429269605AAEDD341ECF697D7565A0ADDD659CA5BF4E53B310BAD6B324A5617E4B2899F11C00200208C10C01226A1E0041820A4A4E00209C689A4C02CCCB2AF4910B79F4C3CA1938C70BF77635C77E666CD3E7A23FF797DCE5E3B671EF34F47BD6F5C2F778629E0CBFCD6FAC601A425E7C97C5F3EF4C9DA64102464E647DC44C54849E6C977EFAB409AE902
	EA02EBB454C57FAC591CA715FE118365A9D0EEC8C9B62F077BFC697923B1FF011BB0F29428FBBA69A70D434B5976AE4A4926652276D5F0FD93A081F0643AC4A672BFEC5F4071F3B99EA1BDC3129676901A0E764D9CD78565D600B800E9BD0CFE8EDC17D349D7D7D7F13A4E9B6325A57F34B3A3173423EB9321381771B6732BC57ADE7A12DEA26B8A2AA76119G65A4GE2FC2614725E81386EEF6D6F7D285B65A86F36BA1DAE27AAF777158F584FDBBD36F21BFC4E5EAD17AB8E3747556B31BA2A2B1BDBDA596DC1
	2BD7EE35BAE5C5F2779199CF366750B6419EA8AF2338DC9311BB836E6782248A7CBC0A2F35A71BBBG5CF63475431D7B39AD9DDFFFCF6A7C97E7666AB036C65A190D536C1A2DB13E5732B752FE695AC27C1F86F5F9E7124D05G0DGFDG11G1BE8BC585FAD05EBDD17DA6FF2B8DC4EBA377B20E2733438EC3282775555D043F1DF485DAE0FCD12D81FCFEAAFB350E7B8E0FC370C190F79A461BD6C7F44AF26CA8B26FF1211ED60138589959971E718AFE61E51FC42E4BF9DD6767CB3017199A83BDD524BEE1E68
	4A4CB210BD6339120CB45E0E542F492E682731C77B5A827769BCBE987EBA0AD7F5F8962B7994CF4A9320EED1BF46C6471B28CB3369F6A9EFF733219FE2136BB21A952603CD89564516C55A0ED6184CB5C2979F45A76970CC176729AE243C886AD4C7321945D77FD6726CCA4EE473E600CA00E6G85A092A04E09B176483B3BC699E32DD60FFA50E7D7EC32C7E23C7DDB5E022B6CD7BD56A627D735BA3B6563D60F5D5A2548DE095328DB088F3A827886E96CF7030C0FE5276C312AF61793DA37BAE4D576F8994D15
	DB08CD5D9E195636599DB288F038A5EC775AAD79F0D52CDE75185BE6D5E50D02555F34057449B96BF1E8919A00FB661758ED540FE07FBC00D46107DC8BE96F9359836982D2084B556FF33791CB24CA977173EFE86ED88A74F9AE74F14CF60273C3B95A056DB4D8566A5AE930F793752D1EC171EC1AC8FE1E62421879DD8746CCC36E7DD259F3C78CE306C00A0B454A82D77033F3A3ED1368454A9BCD9DE3FC0E5F64BA0DBED76C99170C4CD152B17FE694113EB239134D9600DAB77A78124542E216A4F80C524FBC
	90E032CD73B85373CC16057B13484E627A935F2F84A06354EAE1535714368364C4GA4GBC83301448BF8B799940FD96FD3C5094B10AF6476A1F318253A5B3ABE284BD567C8DF4E1FD635BCA644967E4275AA03BE5278DEE3C3CEEE8AB510B670B9E17603912625D10DF6A6101768AFC64D62237D5D5BD76AE1FAA9F15D5556E6C556A2329AC2F2F4B2D25A614B5F9AB7105EBG720B200F5BCA1E4DEE1753A953A091AC3C7EAE1A9BE4C5F6005275AE1FD3E578A95B487355AF8F725C14300DCC3C4ED9959F49F3
	EC1EB24DCA7C6D76589D90FAA4D5CA4B4E123C31CC4C33E86E879C19EFBD8357543318838636E38EA8BCFF0FB47B2B228C23B1AC767E060C868FFBC6BBBCDA8EE0BA5D365DD8773BE90E62F8EFA6455DBFC47CE0B0CD624FDD4D8E52169FE4C481CC7160F337291054E3EE638FDC3DCBD118BB051FF30A72C83F48E7FDB28C89A409FEA2FC373AE8EDD05DF16EC7D63F3E0878D151655FB6AF716374AC1C0BA601BE73G16F818AFAB3D4C970CEE400CF471D01E89B05FEBC4D71D0DF485EA32398C20D5B522B367
	A05DB4CEB747106EE7022E584768EAFDC6F4F7AD977AF13A1906F4E7D6A05D06F349668A0036F30CAE661C1E2EFCA552ADG7C920002014076D8FCE6FFC566D7C67368DE1F3D5C275A49E8E5D56141F47BD49A8DEC4E5C5311435BE89B4039F144A9929FD1D01E8210B400F17C53DC1C9B477447CB8B6FFBBB6CBAE05EG53ED69009647162BF0EEFC75CEA2334A8FB9570F31F7D311FEFC2E73FADD5DF6BAB560F1D337C39F678CF7F8478A387658BDDE358992119FE32CF98751174C9DB81A4F259650B9963135
	936446G648C927CB3D201CE7739FEB687D882D776BC551B7710BCC59F02F2F6E915BDBDAE0FC336951F311E3316DB8754F21A934B6BE875A1F2DF02FD13D2321669A9D36F3389CDF149F11C635E1BDF0B329F3ADD5AFFA9BF43E84D91397A1D0C56C166CF7F20665721C74C19E9FFF9BD1B5998EFC69BD300E659A05AF81FDBEFE365F9E6E367F934B1BB1B4C933B2D9ED9AD27FF391644BCFF39F7G92B337301E54F283194C6EEC1469067692GD8EE7C079AFDEEDC48E596FF1DEC5E7FB54A7D276C4F61FA24
	3ECF6E6EBF68729F152DDE17333E4F65724A1EF201BEECD59466592F8C7DCF623B1C6A74D9579948D6A5224FF76520EEEDA0B3CA483DB56768A8649E05046F6C9D1BE4A677F5A1B785E4AE93F23B160FCA2E4F6110BD63117B0F4BD1EE65902C8706D0EE790A5148ED37C34E98175C1BD7205CA9A0F341904640FB7B74B1D0F00145C087DC158B285BEBAB7E3A67EB7B018F60FA860DE8308EF2433EE254454D619B29A10FA13721FDA55AB081F4CB3A004F4E2F1A8D7303507F711563551F4D6F7ABB025746B997
	35397A660B2173F636E62C6DA47AD6DCE4EB460D82FF0C62959D1E59B71166F5D2CE00BA75922EF38ACAC89F55C6C0BEGD08122G928166C6E0FE1E3763BDE904F5E493099D27D5B99AB0E143755CCE62AB18E3717237F692BF53B91604FE78D5D6309F96C5845AEE3AA2304C789607701587150B0378D85C74AAC497BAE66A52DCF5759130314B85114477159C99382FD4FB850E05978E6B47422455FBB2D69A64EC1A2B396C58AB341CDDA676099EAFA5637BFC28DB82306ACA480FG4DGFDGD1D7227FB794
	FFB8127FE9D035396A815788732B7A60F9307057D473787CBCA304AF584F09D79A793928F833311F5BE02DF058AEABB66667675DB897C9399257E1766C7169BA9042972C6BB2C3DDF34FD7CB434C231C5DFDD6E7AF0F9223DCAA711879E38D46CC41D5B82F2A4EA74FDFB314EDGC3D7210D6F670C4F467A903EE09BE32EB262CBDFBEBEF92BC378C27A5FD05EBF0FD35E8BA34ACBB114F75B0A71497BF6885F3BC16585D799454CFF6D58B756185146A89EAF7D7BB0761708F8492FA171D2B6A1595CG50B98163
	654B1563332FBF04AF581F6A04C0BE265B469654AD8A6A59FE5915F45D108265798257F2126016C13960EA5475F0E785DC2B205C8A20GC4834C8418873084E043C46087E883D08122A7A26FA94A1BB8112D7366CEE46B423409EC3DC456D6F9130097208DC01D4456C70CB76A546CA05B02014855175BA6060E33610068DB45EDA0E534CBCF534A4B2A2E5D199C37646AD0D951867219AD33C634A5F87FE75DA86DA010AF31BC152B1339BC08A60BDF576BF0AB7955E89FEC23810E0DE36F14045FBFAAACB15EBF
	AAADB15EBF2AA8D943705006FE779A6B2BCA065FDF2AADB15ADFFA077A634937D653363DDE59CB87D632F866F5AF29443FA17BCC1FDA9DB277E53E34114E93884DA155D53FDB7425A7E338B31818C921B1406689074A489CB4816AD3G9610D887A8084479414FCBC61C1F9D728FB3BF1BD39A3EDFFED2EA5CAF4F151218713AFC1EEEB958778BCAB708BAEDA652244A8E62A6C6739B4A2FDABD3DF2C85F25D1FED6970E7F3FCB4375AF6B03FF8B6803BD228FDEF413BE280CB4FE5E9B8D702C3D78ECFDFB6F0976
	B2778F57271D866D31F97AC3F4BE600EE47374F401FF0C6207F4F8B6FF2F4D47B354C828CB0B4475071223572BD268DB35DC0FFFDF60CDAB74783D827FCCGFE1F304F3DCFEFDF6BB50176582E89E75FA4BAFE792F8936AF1A62A7DC93EC5F469A34AF9A6A16DC0376452C546BD5A574FA37056855F12DF1BFAA86F8565EED9DE4FFD7318E3AFC2AA425DDCBE4CF6B40F5EA8414E381E6834C3BD63F477670G49F9BD9E2B83426C28DB6E1666DCC74F74CEA2EFA5149B817AGA281922EC39B3E98A0FBD5C71A2D
	DD32429285433FD8C8783419303E6EBF69799B63794C4D8F526079D52D2A20B9D2C17203D8310412F13A651FD1B9F4672F456AEF0487530BF5F37272A55D5E00EEEE426A9FB53FA5055BBB40B6EC6BF56D5B59019E56492DEB049DFCCF3AED502D69F65D3B85229E5647BEAB25A2A4FC0F28B9D754B73A943B4DBAA8F8B7F565696D6755EC5DBE39D33FEE4F0BE26B764AA8DC577F314D283EB18A77AE548F74FB971D3CBE822EB1026665FCA31AC4384E96F20ACA0D644C97752B6B0D6A53C43D2D4528DE3A9E67
	DD57E408F95775B89E3414054F67EDE546797CE41971B8FB3AEC78F1362FACDC9EE67A3DCA57F5B650EDC8687C65D954B9D2605E5E0F38795763BCEE8689CD7E3C7FD8F7FFB8FFBC67132385A6632B4BAA0359BC094E7B0B3F15EC5E7FAD7443A1937A617437508F5757E8365F2A3B5FD2F3797DD0F5D9E5B01B5F27EB0CE9E06F9C610738967443BC4031B1E2FE870E9105B7401A6886FC0FE8776900BD863263D6DD7D3147EEBBE86DED26191E2DE5ACA7F2B8FF4C8D384E7A7484E9F3AE14D3896E865C63716F
	40BD1E442937CB4D911F981EF7ACEC5B13D1904D46092AE8ED0FA74177DAB613F96DC91429C2FDE2B4414DBC29FF26C9AEDB134956E32C7E3781756479A2F3CF2DFE73A9FDBD517757997A7ACF836A090FF6F2FEE65BD3C5E85B322792A4657B9F04BD4BD942ED5AF0A33B369E5FAE95DE7BFAB6737B9FFAB6C806FBBBB4DBB37B1F6E4C6131548AEDB05C7B479097F9A376619FCA423425BB2FCA7CG79E65F087D77B9FD37ECA91473EECAB6975F04B20A8A103676A624AD9C20F392A8C783A4885A4ED2512E40
	9D2EC75CD2013B2385F195B7A71B63EE26E7AE998D434477F2CE1F457B398FF806G36F3DCB45CBFA5E96F9A64CB39E749BCC3F53914B63B1B4C8C24541B493B00AF10A92744E42AE70713194D5B42EBF1CC204C7DD05E1541E43276F66F9F2E3DCCEE47F2FE5F887C1DB12CBDD2F6437DC6E82CC658706287E40E5565725001024F3144D940905047G4C85D898435673CC0FCE53C8FA2C64C06E17826F2ADBB4BDACF07FBFEF3C7146BA21C7F30B31AFCF3D2F79F2FA4BF0B2093FF2F536175D92684BDAA85770
	7EE3797297A17B79234B33E96364ABBAB1D6BEB68F371C247B1137844F4FCBA83E63166079F95B894C3DB6281BF38B1E9FFC341774EB592DB0F782B88D6087088518F1AB4EC7167F196C658C3BBEEDF3391BE142226826984C3E9BCE0E6F3D45A732B1474D78613D524C097F1B9EEE0FFE4E2DAC37AD42EB5BF60AE7767D14466D0649B0AE81F4G3881A6G44CDC67BBE7FF399336F104D2E56292A353B0F1ED423F1476D720006A60B87E65B331DFA5B26CDE672D378F57E64F0FBC2554774FCE91CCE0AE55702
	58F0FC8FFF2067AB863A7DBA5AD683BE96933F24B956969B7C5E626FA9BEBAB6783D4576F6FCEFB1896A9AEF43F9CCE901DE7EBC40AF3ACD13396C36F072E3E95E4E3B1D49D9AE7077D3FC198E4F6417D2BCA9B7C2DD626DA85FDA2A171FF78713D7482FD5F704735BA775FA3EE6CEF71ADF5530FC71ADFA3EC8CE97472F538C7836C8ECCFAEC742FF0B8F0462A41E47FECE0EBEF93C7FF295467B66876F109A7E65FF533707097794AE37A0CE1B2FACBA33AC1B357313D37A773D73EE5F1A119E261DE64E5F21EB
	47C8FF266BCDF45EBABA7B10677B44A45E27737761FB39D1A0AFA58E678DAF3DC34E7B07FF77254D2EAA72C1BE9C71FDEDCB956EEBAF0B437941CD19C4C6651D4966E6G9B4058741D7FE1B89BD97D6B9FC9A2B6F61C546EF7696ECF1E387CF3FA6FE51541F26247FB432FC1BF5DEB3C86ED5BEB3C86BD39F7783D5C2E3D615F95346F553FAB48F27AD387487CF8887AB796A08960990054BBB11F7F4F5EED924D6786397C102D176CE912B722D5147F7C3E70367EF25F486FD17FF69F599F6AF2292A4BC15AC77C
	62FD243FD4171BA2793E67BE62A30F3D37CFE5249C7FBADDCBAAF28FC533B554EF695CDB3A8B6E6F42F555AB741D41C2D29638630538562A95384F0FA1EF34405DCDE5CC01728C01B3D5A16FEC011B73812EEB736EC65C37AF92DCA5149B85EE07043C4A5D1823DFEA3740B5894A8B8116829444439C912099C085080BC75A2514B6854A29G26FB124D05G8DGB60089GD36E9167E66D9144177121B937196308ACF2A5F2093C243BB55A82833E51C21C0E176992C1F5B1BABB511F4310AB09DD1C6FA2CD7079
	4CD8C0FFD6447A200E2E13D240CF736F41FEC9B8C87B854AF977A26EF77558D7157722CF6F9AA07BEF93209C8F108410AA6A9E24F5B977411A8C2091206FBE2C2B988817FFC89B641AFAEFF8FFCF882A0B8FAAB399CDE1E520EF0773E3703B6CD9C1E56693BFDDB7C602DD3177211FF61A89AE914AC98297E5C65F4D91382EFA645DB085F1775167308C4A7B85AE2788F99B856EB13A8E0D05F20240FD360D601220BCD760E6EC93FBEAD3F03EDED2C756BB457743F3825087908190831088B09BE09E40A2GE9AA
	6886D086D08B5086E0837083C4CD956F8DD713717738758C1F5513A9304BC333A5A079A68CCD189E2B1D24CC1E03EFB888971E3B2C55C5615A000CFB44FA4E4A5EB7EB33760A7E7D453E11568571D30D62A2EDC43E60F3FF3E4E2AA2FCC9D3C717871E81BAB6C7B553EFF8D227862F33D6D37C6229416B2C58CCDCE7AD033A535FC6BFBFFBA07C98737C01114718DF9F206F504258C0765699EE518132CEF77574F8E5B1163DFCA0CF601A3C2DAE2F1D1CA83075F47A1764DD799E18DFC989C45775DF525C81BABE
	73EDBC6B48A9A4B4AAA84366BC2BC93871353623F02475BDFE67C02EC33A3D92620F068476AD656B3046E7724F53B12A956EBB93D07E237B82656FB924495FD4B5127CECC95BB789169F1540729708FF036DE93CAA4E88E293581E46B43826A4307794787B0197C34F1B1E3B08FC8B9258F953923866FC0771B1DDBF14C6521544830E8D96602D7C0E36975288772F001E7F8140747860C3A273C87DD1375DC98F1B70A02841EAD786994DBFEEA46D315DBB0A667BA865A75E94F8F6GE4D5F00F6544DA6DFC2685
	A223CF514EE7FCF99ADF1D536933AA241255FD54713631BE2291FE467355A6E3BA2188231BDB9D068E84DBFDC4B0BBCF2968506F0D36F11FC7FE874FDB1E74871F3750BE9175BFB42C1FA66A0F8C995527087AD7860D6A670B7AC81311FEE9227EDF2B0D6A258730FE52DA237A0287B09EBB8AC27766DC85980F458F30B82E05EB5B836C9D9B46B76BE0E89C7F7CA072A9C0BB61817E7DA767E3F11C55B0DC9C13F8433DB5824FGEF6A83DA9CAF057B4F60613BC3445461F6DDCCF52B76F33A73507EBDDAACFEC1
	3E98742802EF426160F833BB111F9CD8A25D79136BC2683436180FDF9C88B91383FBCD8FE29FD80E9B55EF90751F6F886EA3D2DFA66ABFAAB62A2FFD907B62065240BCFCBF1D0734C17DE9C17311B9B08F7E7E3016876FB30F14DB70DBB323BC988DB2B06FB079575139CC9C6093047C9F860D836B9AB579DB0AC65AB34F12420F8373C1462EF4ADEE722527DB827D11CA67C729C037D8685333ADD01F2AA6CD1F27360D640FDCDD0C866BE3C9E4B27E8FF2AD9BC3BF2C8BBFD6B7540DBCD63754992F870F56052E
	87BB6A0C5703FF29532F87992E3B0E78349766E49D022ECF60CE881C0336C9F06DF61B5AA770DE2A9741B74AC4A0628729DE83945741F197F538939CF7D59D49A1E0F21F4BA33E832C23FDE7D794D60E22B4F6A7CCBF04BFEE24BA5A5DD61D5E378A9C511B3D1BB5BBBF1772AAF627DC4F3EC016F02E3E1F2ED34B202F9A924519F3A641F5C0D9C9443549AA228E1F732D535D5F15F97977E59E3B2CB2D85CDFEC1144F3731B6DE41F43A98FD844078A0761A1AA5EE633DB95D76F36B2CBC519E5F7E5896EB1468F
	6877862BD66CB37C161100BF110D85D17C1AB4E9BB17AF11CFFE78FD5CD1CD171B770DD69746BB2B0D7032B9733B5538B718827216A562F9DDCFB56E71D5FE97777816DC205FC7C0F982C05CF771EC797FAE623CFB8660523E0BED9C3A046737058FE19B5ECB0436934AAAC054C3E35DA77C55B1C9427CF6F16F8EC937FF592F74161F69A63BDF65BDE474A12F8D0B218877AEFB45F7FBD39EC2BD25C1BC1BDCA0F02F7891D770BD44DDB10438E601ABBE0F388982B74B04B21284AE2FC63C27ACF00F2FC5DC59B4
	746564826C8F9B606226A16D0B85C8BBEF9A7A723F6B886D2A07134D05G7B9F46B67EF8907B238DF0D18F63FEEF6F36A23D3F6CDD0EF25FB9C73935CBAD17E159D8DD7DA9CC761A79B4C5E2B2EA6AC9DBA9504682527E43E36D2F2CF6D2E2B63CA5FC3A6A91342B7DB8629A84AEF2875ABAA4F03EE2444DG9CEB7B3DDE6D9958E49E5BB3F03CCFFB16AA7A35F6A697EEB5EE278E869A851A594D1B613CC7952D1DCA1756CE7D36516833C768536E56F8EF2BA6B9C3A5BB6F0407D45031D926F9B88D6CDF7A880E
	4D3B696FFAD0DA2147793304056D956C74507D4469103B2763FA786916BFC961779DDABDAE376CD1879916FE054CF3ECE17578768D760C48E73CEF50CF79EC53C737EF601F0E7B86FDB49FC5CD8F5EB7B8CB71315303778DDE3B004F4B94284B190163D96625711DAB2F99A75F11B173B13BDE22B9D51991EC6FE20A774F8836F7701278CDA128DBA46CD568B9FA73770D7DFD5AG4F78328675FA2B1CAE12DF6342723D6A8FB84365F4A97CBABFAC5F442100B3DCCEA7BD4A4F669E8D4777677381E7381CEEBF3F
	360665CBB151B8B42827F19716EFDD8D610B8A43979F166F6F56923E99E1786604653B33E0FC4F6703A37299BF1F2914EFC198BD03E1712378FC5A6978E4FAACB8DEBD941F77D8F03C3EDE07713A996AA29E43F8B5550F6FB9DBBBE6BE265F7768D8189A2277CC0ADF9C22F7DD3D580F043A53B3D16FCD81F11FB21347BB3F264D8C573F1D4775FC528FF83C73EB718F4271DD3F037E4601C1BD0D7B30FC670BE95C0761739B60597873D3BAE43171694D4C510E27ECECAA7310F644F9A59BFEF85DFB9E323637BB
	6DAA305A4F5377350F2A64CBF37EEDD5B52ED322FE006B14218B84978F6599BFC07FBBAFCA4246815D3DF471722FD3B67E8DE43CFCC93369D55D7D25CB17DFF66CE515417A742A8EFCAFFA366867548EBC2F19ABF01B87719CF6738F91772BF6BC072D9038FA413BDF60F285EF1440FD3783F96385CE9167C40982F7679072AE9338BF54A3EF5E0F905756003C1B852E012EA794A88F895C73F40E9C8D65F801BB50AE648A5C8B26D03F183210EE1920AB2B8925BBD603F44DC9C26E5AD03A4CFC240B93F42F5335
	C99A14CDBFC65C853A0732814AE5BF46674EF5D00BC53F6EBE7D60650F4B377F86B23EDF2F5974386E7E637A4BAF7B74E515417AF48FDD3FD5C1FFB60AFE0EECA338CEA8AB8277F29B4608DF603C4711B7CE609EA9C45EE98217AF4EEC1384EE8ADD8FAE0472D201DB399369CC0FA36E46E224AB96385F970B33F74031F55443F41C6800326DF1DCC75DFCA1F49D3544A7097B7583641E6DF9BE32F52477DEB3F59083B288601EE7825C1324CC5EABE77A9C2239E68660E7BF0EEB5BEA3FA66FE447E84E7E4249DB
	8C77FF1658599B1337174ADB86781CD9A86F4BC1CD5ED5436E69E60FA02F8A5A4C8D10778FB49FB6G3ED5480BB82F496B9F761CC3FF2EE9A4AFE2967B6E608DF1DEF76AFC601E76D91AD32300AEF6967677AB47837734E7DE5274B95EBE127D4369B3E796BBEBA57A7033532C1162E53876664D5262053527540CF486B0DC7FAC8CE96F6D7C511CA904EBAFCD579E736D5AF5467B97256BC8BFAC85FA53EC710DE2DD68F377826160EDB579837B72AB1A03AA013FD634F1F7DBE0DF7E1128453FB1B49D43F3B6B7
	50CFG1882108210BA9B73C19C4DCD26A7607E890C4F2D19DA9B25C558C6AD54378128GB1GB31EC09DFE344378FD798613564E361D584EBC60DB8410138C398A20B1996576E6E9744BCB103E8F6A07G62G12G9688FA7AD98E77157B42E8675A615F0B1D34DC3B7F75CA2D6D1F370FEDFFEDCD2E560EE335564EBD9DE3EBA7BACFEBE7D33E564ECB75E3EB2729C0EBE75ADBDABBCF9F9FDBBB4F2E575AF9F503F63F71ED6D7E3FF6EA77277E2449BADBB7363D452B4BB55E2136516EA7327D5252E338773EAC
	993F1D6AB8967C6DD46037D157865413775D7E94D01F94D0CF8265ECC07D6F826AC9A0EF62754C1E348A4D1E2905E33367D4915AD379F7E84FB5C5435B732322E0FB3A836ADFAE9A5E1EADBB026D417619CE0DBBD1A7C568F4EEE730CEA581EDA66C8C5629A8207E791D436B342AB8D8274981BA6DAAC11D26881D3EAC99DE279BCA02F57AB2207E091261F57AC3C9B81D78BB5A474637CFF33AE8FCFCADBB4747D7D9B2D6BE767B7453DD9FC1A171E5A9CB15D6138F51AD2E9E8B7FE0DB150A79375596EFF71FEC
	73A932456715ED9655E5117D5D0A4FA6DB3AA170BDAE456B11D60B0721091E62AEBC77DEF5662F6F4B4C10CCCD12191DEAB7C9A6F2B45DA4E551136BA6A9D3F5397D647840A4E9A70AA66FD969221481F7995CBE1210D706E0CD813424C46207A47E0CGACE977EEF22FCAD94444EAD5DA2B7BE85EC23F1A87E4AE375BC2DE15037272E21F335F699AF01628D28E77C4B5D81A9B70587F7DBD6F7F7B5CDF9C77E6CA66A6E9A3996D0F4A9E3A49A35B488F0870DF2BBEACADF73B6CD089F8F00317DE323C6A20A2B7
	CB85BDDF10DFCF752A322D8E370F3C2A340E7E085DAE72B5432EC62B37CFA52F8B54B4CBA6A779DDEDE9AD2BD62C4E5EDDECAF29067AB23D6E34A4F1DFCA20EF3E76ABFE22157C9F2DAE5AFAA4F5E62614F1D8DA21382C364356EE5565E912162BFD9E595B67D2ECFE7701DA5AD04EGBAB6931A5BA44CA74616138903D03A5FFFE7D46E225C43996437E625DD1412CCEE4A85052D3E4F6A81D13227C9A5DD6072940A3AAB5ED5F77C73476BBBB324F4EEA4694655FE37AA19FB2C8E3BB288B78DB449528E4E07BE
	D2BDD627177C6E9EE8E689528C2B0400721B1F2C3865BB2F1D2482B4D6D55A42DC892C3BFAE517E3D7DDCF0F5DA90B86402F507CBB22F9C2D39EC43356FF633AA34F3D5185F150A0E5BAAAAA685F5D746F9E72F7B7456C26185DFB40390B25E17F85577B51E69ABC6209A35DDFFE98FAD0FD7E65A76FFDE17F62B4306E3014C5FFE78FBAA1CF237A54676812BD9393EFF9E7E54C426BC89F39C92878C93B6B7D814F8C49146458BE17E2F3C3220AE057040687C12F931DC4CA9BA8695B746FBBE3B619F0ED213CDB
	0526BBB5CD3379B3CF1EF0334012A99B3E391D9510B508E564DD9653B74F2A8A4D2ACC6EFFB2E54CB054B4BD0C2626C0CDB1E04C6CFD9CA61170DC9E9376851AC0BAA5AF488426702A7FDF4E2E6565BACDD362E88C219CC8396D325CEFE900A12F6E1C552ED83B6C0ADD9D7CEBBA01142A481F6A4B6375A6D82F6536D87DF6074FE1212F3E2A528EA8DB7A483D452AA82E8159D6E6B9AFFBDC96078C594A62F4D9943B432E929D497B3EE42C4377EA61BE073F0BCB50787EAE5C6737581D967AA3AF16E2075DC906
	2892C2415F26A40C7C0DC938BDG44D4A39598ACEA1FEC617F170B4561732A16AE59E265FCB6A8C07E14ADF655E277DA3CB0A3F04BB6D5BAB4E28B1CB410DFEB565AEB35BB3F49BDF7529F101CC88412D519791B171C2C01CF76CACE1B754D5334AFD0D362CF53B7D3D31726E91E4E27195FBCCD4F85EA0ABE4D94D8BCAA5EA0998D38E4F7A1F0F0C66CDFB538DD6E27B3427F4D9B37A6FE83F50A7263FB22A517746F09AE3DFAC35877C4677D0C7F66424F34EF4859BBDFDF7B68F72A4FA61BEB1F45F73E1ED249
	99C0386F440EBAE0FCD22DCAFF1D53C63E99BBB8284A5AB7425779C87BDE2461157C7F9F70913D4F68F87E9FD0CB8788FAB021BD1AA1GG90EEGGD0CB818294G94G88G88GEFF954ACFAB021BD1AA1GG90EEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG54A1GGGG
**end of data**/
}
}
