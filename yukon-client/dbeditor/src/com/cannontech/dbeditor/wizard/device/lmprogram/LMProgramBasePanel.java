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
	if (e.getSource() == getJComboBoxHoliday()) 
		connEtoC8(e);
	if (e.getSource() == getJCheckBoxDayChooser()) 
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
			ivjJCheckBoxDayChooser = new com.cannontech.common.gui.unchanging.JCheckBoxDayChooser();
			ivjJCheckBoxDayChooser.setName("JCheckBoxDayChooser");
			// user code begin {1}

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Week Day Availability");
			ivjJCheckBoxDayChooser.setBorder(ivjLocalBorder1);
			
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

			//not implemented yet as of 9-5-2002
			ivjJComboBoxHoliday.setVisible(false);
         getJLabelHoliday().setVisible(false);
			
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
			ivjJPanelMaxHours = new javax.swing.JPanel();
			ivjJPanelMaxHours.setName("JPanelMaxHours");
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

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Maximum Hours");
			ivjJPanelMaxHours.setBorder(ivjLocalBorder);
			
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
			ivjJPanelMinTimes = new javax.swing.JPanel();
			ivjJPanelMinTimes.setName("JPanelMinTimes");
			ivjJPanelMinTimes.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelActivate = new java.awt.GridBagConstraints();
			constraintsJLabelActivate.gridx = 1; constraintsJLabelActivate.gridy = 1;
			constraintsJLabelActivate.ipadx = 12;
			constraintsJLabelActivate.ipady = -2;
			constraintsJLabelActivate.insets = new java.awt.Insets(8, 13, 7, 3);
			getJPanelMinTimes().add(getJLabelActivate(), constraintsJLabelActivate);

			java.awt.GridBagConstraints constraintsJLabelRestart = new java.awt.GridBagConstraints();
			constraintsJLabelRestart.gridx = 1; constraintsJLabelRestart.gridy = 2;
			constraintsJLabelRestart.ipadx = 13;
			constraintsJLabelRestart.ipady = -2;
			constraintsJLabelRestart.insets = new java.awt.Insets(7, 14, 13, 2);
			getJPanelMinTimes().add(getJLabelRestart(), constraintsJLabelRestart);

			java.awt.GridBagConstraints constraintsJCSpinFieldMinActivateTime = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMinActivateTime.gridx = 2; constraintsJCSpinFieldMinActivateTime.gridy = 1;
			constraintsJCSpinFieldMinActivateTime.ipadx = 55;
			constraintsJCSpinFieldMinActivateTime.ipady = 19;
			constraintsJCSpinFieldMinActivateTime.insets = new java.awt.Insets(5, 3, 4, 35);
			getJPanelMinTimes().add(getJCSpinFieldMinActivateTime(), constraintsJCSpinFieldMinActivateTime);

			java.awt.GridBagConstraints constraintsJCSpinFieldMinRestart = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldMinRestart.gridx = 2; constraintsJCSpinFieldMinRestart.gridy = 2;
			constraintsJCSpinFieldMinRestart.ipadx = 55;
			constraintsJCSpinFieldMinRestart.ipady = 19;
			constraintsJCSpinFieldMinRestart.insets = new java.awt.Insets(4, 3, 10, 35);
			getJPanelMinTimes().add(getJCSpinFieldMinRestart(), constraintsJCSpinFieldMinRestart);
			// user code begin {1}

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder2.setTitle("Min Times (minutes)");
			ivjJPanelMinTimes.setBorder(ivjLocalBorder2);
			
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
	getJComboBoxHoliday().addActionListener(this);
	getJCheckBoxDayChooser().addActionListener(this);
	getJCheckBoxSeasonChooser().addJCheckBoxSeasonChooserListener(this);
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
		constraintsJLabelOperationalState.insets = new java.awt.Insets(6, 9, 7, 0);
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
		constraintsJPanelMaxHours.weighty = 1.0;
		constraintsJPanelMaxHours.ipadx = 2;
		constraintsJPanelMaxHours.ipady = -1;
		constraintsJPanelMaxHours.insets = new java.awt.Insets(3, 9, 50, 10);
		add(getJPanelMaxHours(), constraintsJPanelMaxHours);

		java.awt.GridBagConstraints constraintsJCheckBoxSeasonChooser = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSeasonChooser.gridx = 1; constraintsJCheckBoxSeasonChooser.gridy = 4;
		constraintsJCheckBoxSeasonChooser.gridwidth = 4;
		constraintsJCheckBoxSeasonChooser.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJCheckBoxSeasonChooser.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxSeasonChooser.weighty = 1.0;
		constraintsJCheckBoxSeasonChooser.ipadx = 10;
		constraintsJCheckBoxSeasonChooser.insets = new java.awt.Insets(4, 10, 5, 3);
		add(getJCheckBoxSeasonChooser(), constraintsJCheckBoxSeasonChooser);

		java.awt.GridBagConstraints constraintsJPanelMinTimes = new java.awt.GridBagConstraints();
		constraintsJPanelMinTimes.gridx = 5; constraintsJPanelMinTimes.gridy = 4;
		constraintsJPanelMinTimes.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelMinTimes.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelMinTimes.weighty = 1.0;
		constraintsJPanelMinTimes.insets = new java.awt.Insets(4, 15, 5, 10);
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
		constraintsJLabelHoliday.insets = new java.awt.Insets(8, 9, 7, 0);
		add(getJLabelHoliday(), constraintsJLabelHoliday);

		java.awt.GridBagConstraints constraintsJComboBoxHoliday = new java.awt.GridBagConstraints();
		constraintsJComboBoxHoliday.gridx = 2; constraintsJComboBoxHoliday.gridy = 6;
		constraintsJComboBoxHoliday.gridwidth = 4;
		constraintsJComboBoxHoliday.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxHoliday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxHoliday.ipadx = 29;
		constraintsJComboBoxHoliday.insets = new java.awt.Insets(4, 0, 2, 160);
		add(getJComboBoxHoliday(), constraintsJComboBoxHoliday);

		java.awt.GridBagConstraints constraintsJCheckBoxDayChooser = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDayChooser.gridx = 1; constraintsJCheckBoxDayChooser.gridy = 5;
		constraintsJCheckBoxDayChooser.gridwidth = 5;
		constraintsJCheckBoxDayChooser.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJCheckBoxDayChooser.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxDayChooser.weightx = 1.0;
		constraintsJCheckBoxDayChooser.weighty = 1.0;
		constraintsJCheckBoxDayChooser.ipady = 50;
		constraintsJCheckBoxDayChooser.insets = new java.awt.Insets(6, 9, 4, 11);
		add(getJCheckBoxDayChooser(), constraintsJCheckBoxDayChooser);
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
	getJComboBoxHoliday().setEnabled( getJCheckBoxDayChooser().isHolidaySelected() );   
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
	D0CB838494G88G88GA704A2AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DF8D447F5A869A3AE6FC56DA36FA98EE955D43109AD370ACBDE54163AEABF52D0975BA441A90E09ACB72441892F16933566EB30AD4C554F3282A4748B92C6408A7DA001D8C900C12CE4504FCAC8A0010C0410B0B1DAE9AF5242EAF733FB85026841BBE77E6E5D5D3B7A4B537B4C779D5DBBE74E19F3664C4C1919B3F397A97FE024A1B9A5DF12128D927BF7BAD912926EC8520D2F7E3409636AB67F
	48A8E97E958364C82FF62541339D72BA07630C59D201B99D52B7A03D22AF4ED8G6FF9D211397FD3F889A12786727E6075DFDE1FDA4EF387399CA3C9FF3DA8831E3BG86813790E08DC03674F7C5058C9F8569DD6C9DA1B9C512425FC11A171B8BB5787CF7A85DA8BCA7816A5EC51AF5C5C5ACBF9EF0C83389A07DDDCAEB0267F6225D67ADF50C6E46972324CDAFD625E422ACCD799C96B05E253C8CA90698A2D5FAE5FFBBBC9B3A6F0C1C9DF60F1907EDAE175B25484323E67B10ECF7A8EE2F791E63014DEBB75B
	653B0EE1596C9C73F85DA3DE5B1845525955CDDF1BEDBE395B66121D796F217E3FEACEA7E5037EA0AF12605CADE833FEF877GACFA0F5761654C8626E3781DA8E97D1387C356E143FBD47774A4358E6B1CB78D4B13307C647D990C2F9D528BG28AE9D7B39AEC9C9DC179F75A16D49AD50EFG6CDBB8BE729A62A7A19D8E903D056B7851E12EE37FEF7FDAF23EAC6F4C8A21634AADD43704AD2A0E1DB7EF9A7612FE53D60CEDDF71BE48859884D8883094E0A540BE52DF13CDC76059B424343847465C2EA60F2759
	E977F6396D32935EAD9648E138AB72305BEB17A45AA7131A2BF47461907F3EFFFFC420F53BA671BE94FD65D9694602DBBB8DBAF50BF9451C12484AC85B2A562DFBAB5AEDABE95BD2F8CFA63AC89245E7DB919F2E4153369AA77418DE81F91DD29C33F37FAE2ECBE75A93D2F66D47A9C69DDD167CC3DB4ABA604177C476C4DD280DCDA5E8632264B8E39DC0B740A840A200A8GEA630B269353593883460057E5F3760C8FF9ECDE25F95C61344BDEE6EB637E29EC2D8714EF772C7928DD22CEE85BA87EDB5D3AF609
	0F7AB0E5E5B235C7FC32EA97AA7BD2C859C90CB690E89BF611B1111D92C75AB0D5602F913CC90327ED7BE3B236B0DD83F961G143E269F711B993D5AC71A883EB4B5AE200FBCF50D7711EE480B87206D7B7725689BC753620C8B81A2GE2G5682A4822499F89BBFEE2A1E61B86A5634AF653DDC3C871E72046235F538FC0A4DB5AC77593C8E5B10D376C90C263F986DBA644777EBB23E07C146F959A5FBED0A436D0252EDE3B2F4A69F2539D50CF59A764AA43757B1A60300B10F444BF595674153E973A9D7BDF6
	1BA22B94B47FEEB176093B36BEA8918A0077DE834EFDF9AD6867CA613D5140ED75E6996216C0FA19304B3F97674273A2F4EE59E931D89D4328064DFB1F77D10355844F15C05F39AD4E3892E05498E74CC807B2GFA374799133673717A0BFE5EAF2D195F102ADE392C5BAF91A6F750BE963EA330DF6EA172E2819F8F1084D034B34E58GF083E092A08200527E36E50126CC54A7487F9AA80E6B6E574F376B77F3ADE4E84A44FA6333B7000F6A72D5BFDDB2F54ACAE5CF8363516665CC238742CE4D7BF6764C8A20
	216DBCF09847CAAC58E99D40661D3C2D2B8E196898407919777A4BDE683032DDEDF9CADBD902E54BF7E517E215BD324B8EAFBE5667EACA362B636116DB7455D3840F7E3185BA2CCF609BCAF2A89E4A500EAE1EEFA579B6C571BA06469539C7D69407EBC4656FA43A787CDC2F0F5575138A620FB2620C95GF5993C2E2F1464E97AF5275BFDE75C530BA3160D8F192C83BA015E1E417BD34FB5DC57AD9269DF13341A7FC6DF7F8C5AA990303C194F896F4FC9C66A8C69280CD37D588F381F4EE27DF5A74B5F2E6923
	B952F9C29B85F5D8833099401499E7EC8770GAC81D811496D93FDED0126DC03263C1DCCC6E0FF1D8F203AFC1F6842754851485F49F48A9C03738134AF2EAA8DBD163EDF2ABF16DEA9559FCB2F15CEBDD6FEDA2AB7D6E85E3F1522AD5C77DC1A31774BD2747F43EE17CBA68EC030C8F4BF700BDE2C074365D02EB96CA3325263F8A043F85B05E3688819E3B6C27BA7E572B960DE891F83CA9FBCA32D38F9A4C5EF7D38646B9DA93D3B6838985D254E81D42744B29C5332D39E83C3353847DD8A457FA7193F985E17
	CAF06D058F103762824C6BC36EE67744E58F1BE9E131264004D9E5A375B253C7376C3D65760E49760A5B363BB6336D1EE2A66DE2EEA2592D78DEE9BAF66C2CA9F8B3D3F54B6115BBDC1EF1B695D7D476D55650BE16A7651DF3D2BDFA21AD5B9C3253FE8966792AE1F0FA0A197CE5CCA8F7426C3B872D46C437E06E2CA557D239DB6C0F337A0F4A43F7207EBD324D67F6350C3A5DBE59DB250FB68BF41B4D6924D53F29EB20F14561B4935DAE8F5D069EB29B8D0FF35B746DD8723B68586371023166CB4B93D447D7
	4D3F1B0E63E3E332F73EF4BCC9F57CCE7A6F2463B587EEE266CB478F290E0F9D7BE3FF9D2D367BDC417FF7A371F6F069B7A9DFC5E768626AC14DD32CCFCAB7FF636FB75A5DCE075DF6FFFE5D45E9092F517E3092FD5CA270FD91GB13B783A45DB0E79FC5D4296E7ADEE27133AE748AB4CC25FFB491A2F71272B01FFA10BB1517C875364BFB5CD7E8F2649FFFF1A7CA32DD36777CD137FFBA173291DEE9E4E46F9CE7E4D388C5BB1DC58DD146F73BD55615C003CBE1BF3DC5D2B398E639A4029D993D23C2DC2FF5E
	992940F5609DD17E1D0AED926E39E819F41EDCE643B241D30D41929A9739AC3EB739DF9B5F9B043AE1ACB0921EB1G34AFFEFD92758D6EC130764CC6DBBC6A67713F55403381A0AD1B16E34D26F3A725EB9960F44BA03D8AE023AE5D366B1C2EB48776D6GB7F27468DEBF4B69A2A0BF86E09D23AB4C5552FD6D9C279B847C84C0C42EDEF9E30D1CAE0951656769511DE962F4CA9E258B5325B3B4F33A84C617169FC0170FF4CDADBC66D7ADF07D84D78B0B4037D2F9DC25ABDB9BBF75FCF92B6E5A85A1AA1F169F
	1B9F98EF7A4E91ACB79E708979FCCC3FD1215D0BB479FC6EE187D92230FE97DE29DD57D05CD7AAF74093DC08CF69008563846F3BFFDA093AE2BC0021D97F7B73239FC941710DD6923165DB3D16FB44E1753ADB5CF6399D3AFF0BD87675B964FB9237437FE95346E0E333CE276C8CE187E9B72B7FEE558E9BBE3DE920BAFD4502F1B53DD872B0D152BCB26EA0F30E45E235A9B662ABE94727315A1F11BD9A16B5886553182CD40CE3F4892417812C82207121A892B7C25A7500AB5C83C07C483B9734FE24FB8F5D6F
	CD6C61E3AFEBG16A9526C1DBE377B4A4ADC5ACE812E1E5B72093CC6BEF61FFDC2EAF8D4182297ABDE14F5A8A5EC8F35E14C9E55160BDF5E12F2A3FD1045341D833C0ED2816CF58BF89D7FF729360E1E82DA47A5853C0E7FBC007DFE16CBD196F72B3E4EE5C6C3F99B044C9F5E5433EBD1A117794C59395A154A5DF60E4B2D01B2C78128BF3D7ED06BCFA38B295C84F8CAFB396C31F33FDB1B161CBB834FB92FB1A4DE0797C59D8AC1376E3D3C8ECF56E96B30E8AF2D43BAF8A608BA54B67640F39E16F4CC37479B
	39EEE99F409C7081173BE5C6F26730A4E7F2071A38DC85E4C68B39AFB44DC86E6C1759CC2E39194B5DGB20B76F139DFEA7EF886F2E73DCD923E745AF16D1C32E27D2910F3CA6FBE369F5E97B827BCDD05FEF0A160176C63F34A87C7F2B4F34A9510E9F30D40CAC6449C0EE07DE04D42522B0F20DC32E611789AF443B19C4351D066AA0075G1B818A0B380C1E23531E0B34CECCF1AE72E65099C3DD119A8B6BAC8A0E0B51B3009544478F9651B3003D827F8A41C7E8704CD790BC2657C15E527DBC9E75ECFF20FC
	4F81FF19E18774E23931C1FC66007432GBE6AB7C61CE8B7B25FE9DC2B45A27698ECDE7BF2A7DF0F2CBA40E37316EADC4BC88721EF821C846886D0G9683449C64ED72046EBC6F5FA621A6F9B56ECA6B699D181BFD968471855AE76DC1BD7B14D594695BC744CC28FDCCE5FC5ECFB8485B72436BF35335A508AFD057D213BF9F55A1F3106B50E062BAE41C1B1B8E9F84719D88C8ABA6BDFBC51EBCABCD3FFEF28D0F4250E79DADE84A6275799F8FF9FD42CCFCADD5CE4E66D6C2BA9EE01328671AB95673DF027882
	ED1DD12CD74FB7DCCDF32C271F3B96ED77DA9D9FDF25453C2EBF49463A5E00348290D64C6B9A51B8373A7ED990DFE0DD17966B710DB64DCD5E5DA03EC0F9313A72CE865D8318193C66A03E20B19EA00F7A3187B5E87B8448CBAA66BEEB6BD10C3D73392A5B8D0B96094FD351D53817F6FB9D50329C77CD029BF6FBC9201A62920E050E213FFDCCBF067E4EB17D987A56E3D31FC7994D21E26C746CA865G56B163D01C5184C0F15FBD08B8AB24EF9C62FD6B79629AF8AE007492009800F5GD28978F800C6009BG
	13G91G4B815616F05E57886F26923A0F4FAF257B7E1325F41F5B8F4FC900880058D23A07477DB973D345AB825AAA90D036B6BDD992BC97078254996B34167170FAE9E91630F40426DC093D976948DA2E0323F5B9B7EDDD824F1472E6D80FDD92BD97CAE5FD174B5C9EC0E3E04F5D9A9CBF0B55BB67AD50416DA7F2DE5813C57A1D4FA77B48A40D3BDE1677F3E53724F38E030788920F233F513F1D5134AA6E16DAE123084EA96F7A14865B161D3BD6079ED3972B7547544755D8E71FFB5CBBAC870E1B7E6AC211
	272EBABA94F98C2B41E2EC04DF31F9C76420F1B7CA78E9DEA87E5FD4079A1BEC5FB9203551F1E12332B234D1220EAD90524A027134BC67F5EDF9F5223C5CC1AC2FD0078F21AAE4F9AD6734651D9665FD7921368D9B8378FBF54A23EB62BFA2F378E899DD93A78B7C13842FE870F42DDC55476F45AD003C8DE5FC9FE4EC546AF5CA68353EEE2A3E55DE9ECA2F2BB9E43EAD8F546BD3029FAD8F546B6759DCAF857256150B58051FDEA7059D7F34C90B7FD0606FB4EB71A7C45FFAD403E5CF0874FFA1697B02EF6F81
	1CF31C367B6EF1181C228E23CF2FB94077698D106E851884D8FC1856192EDD3EF98FFD41AD2FED8C3AE10FC79E1696D6A06F06031CB7916853GD78AE08530D7703ADD3907B13E8B1D36A159C987BA457F3894797CCF92F95E1B8777881E4023CECEF321867DD94893D1D1676D17099C92F26B32CD34434075713C3F5AC66404D8E8D19AFB01065FC1AFAF71BC39BBC76849625A3D77BD2A6CAF9C48977930339E3791AAA42179AFF6E60AFCF6B4A3F8778DE5EB6B4732E95E2A6CBC2D5E1A05B00DDB959C72BBD7
	28203117448A9E577298747253AAF96C2629DC9B3BA92C24798D706C96B461B9FAB423701C2C64F23EFDCACFCE3848DF231B9FA572CF76696547D672754837BA79FAE40D40B510FD1569C81C316E8847BDD946692C82D7C976BE61100E963845031CEE3940BDCB769469C7E1BDF31463929FF23A2A23FCBD9209A1B166CF1E513C9F1F53DD17198215F1F1DEE550BA7F2A0E57F9D25479139C351EF64D7BDB5973DF67EDB973A9037A6B7E835CDF2F06BA27D5459929BF2C9D745B63E31054E2B96FF5581BEDA31D
	443B52BDDAA4D9C3A3FFC3955F23FD0F1429C0BA8CE0E9953FD3B2D14E4FA3221FFAC26ADC502DFBA7FCFD6F691458AA6A1B57D729B134411BB78DD4668E9317D9F48C7A6BB1EC3BBA13F69C218F49C925FB9D1AFF5BAF9F9B9257EBEAFE04DFBE6A7B5394ED7E777C7251C60799BF2D5BCBE53CEEC9AFAD131CFFF9B664FD7708E334CEAB58F3495592E96244A0BBEBF97BD621249BDBA19E14567F5221F456C7B6C3999477340163EA4C3C8D5F2E8CD11666CE7F7DF24EB7EA666D575A096D97896965GEB4CDC
	466A9CCE3BD150561FA177052B419781F4D7F35A4D27C43982E7BC4DF11182F7270F6356814EDA0336FE3153BAC53F65F73E8C2C1D330127942004616A607DA5C93D3F16A77514615C2E385D4EDE0787E7E3A98CE8A4698DDC36C9D466F3E5D349B4323278F3CD004C8D70DE1D221559B828AFD3BE234AFCFAF0AA19D8EEA62BAF161998A0B32DB64ED86FD74F68F2DC7B8C393DE4B1DED40BB2DEFD08E75E59355C3646C7D34944F2F6B24077C160D3EA29CCCCAF2A256BBDD954E15719694A5B2E29C3B870C7E9
	4A0B05775D2C8E5467FDFAE0EEF7701DF3647B7A2C79687AB772A0767BD535016B62E788FEEDED603AF82D70336B31AD0E73F8F13B8547E89424D7824483EC8248BF91E72CB94163AE693598C318B2D65C6B76F4428650298903507AE5196676ED42370D5C1F45BC77E75272475ECF8EF5F6ECBD414E68593326370460E97DDE2B477A05C35EB20035G89G52C9D87F1C64756B2E2D227543E36EA6C5318D0F1293F4DEB9BC54D65138B8E777FC8FE96BD6F5124AEFE44F5E1321E2915186AD5FA8231BE44F45A1
	799E16EB7916825DF28D6DEA9DBEF65EC17C697A13015FC87C0C60B3BE8C7CC662E2A77FC6228E72D6FE4857A2AF67E865F71F023562A9D5664429D0727F02786830D3D44EF6010FA578A88D1E4AEFA2F8CC2F043C0A53DC7E16D3DA79E127293CC8760CB99D4AEE992735FC2B98DDBCFBA606647374E979526AA8DDA1FBD65585739D14E85CA8DD627F924F8563A4660B5F44A398565FFF12417BFB511FBFA9D9DFFDBF39A4C4FFEFE4F2975729EB13422F8D9AE8B95F7FC4FBCE187DC765A949A14AD9457857EA
	4A51531F6A7A74B9C937FE3CAEAFB662BB1D875EFA28566BA3D92BCF4DEF3725240650A77D8C33AFFB267DFE498C6D49F52CEF8E25E3E81ECFDBB0C55B7B29036A7BDFE85E638E4C7FDA7B6DF91541E2EE1650F13AFE0BFE1C6ED30BFE6C5BEE197A7E3853927A7E384F223DBFCE7D34195C83EC07363783AC8208GD8F6067B68647AC3927151BA7E3955BE02F1C27C22CA6172B754072E6B1B7553FF0735299EE3AAC3EEC5F10FE1799C7FEEBD3617627690A4BB8728C79BF99DA3238AA5E5F8C38376B727FC0B
	20699E6FB6D9BB2F06FA451F61FB5D67AFA2AE91521205630E0AFDF70640FD4740F9EF885CA7850873C0FA01402DA8603CE1827725F23E470E9738F802CB0034D4AF4A2B603C2575628C215304F506749200E5G2B81B68224B5407E9C20370153A6905AC9C8C7G44824483A48194FD94E7ECG387191277D38F381BC11BF303F26B19C4A426792A6CF29574CF77541FCB305DE0DCC2A0BC2F4517B2EA4D3C307CF6C3BFCFD4AE982E37D33816DB7DB348DB612FD4EA458E971C73CDD2AF39197896998010B4863
	ED35CE5874AF4FE0CC2B62AC2C71G8681961D65F97FC07222A13D9AA081A0639C4FBBFFA6147F41B2703968ECE8FBD78464D98352D446E0C8995C36D359B1705BA4C3C01A5AA41F6C712A20DE8D67381D7E181CA5F6C3FAD06024DAEEBB274015885ED8010BF90438B510DEAFF0FBA54E1BA0F03FCF76118D0DF1464EC60E7BC5A16286A16D91381E3DA24653485760E12D3807D98D690DG99CDB076G6AG3A816CG93GE1GCB81E28156GECG588C105E8CE38B2026197B4A1A33F8EF284FF6BB7876A6A0
	D99005B8CC2F0D1E21D2BD7E238D9E7CFB5033E5214AG0FFB8136737406D22FEDC470471F473D1ED5681159F4E88ABD70DEC90F02773F44F7FF5650BE7BA36B74BE3B4DCA6E3F222F3D6FE167BC972D64DC7856AD1FAC6606EEEB3640F5783A5DBE87C635695A65F307308E375CAEA53C995B6B4B44DF77433B3D19475BDF3F08B48AA8C3579057BA50EEF4BF5ABCE56CA0D5D23FA5A3778AB4FBEADC932DEB263FAF715E562DDB297C846273B740FB22106FAD70177F6002AA7F550269649BA5B50E90A85F54C2
	7D81085FCA776F5FA9C7D9D5ADDC765B6541679BBB8E73B8F5DD8BBD5F6806273D051EB3D33D3C955369453F294377C960DD5C226E5317365018926AC575B873914A3C505263F138486186BFF8305A9CCE96437F273DD89E0DDC91B4DB1F1A87F689BCBD1030B9795AF5A0D7BDAF6802DEB06AD44F8B4633D53EA617EB5C6644CC1AF7367F83FDFDC4A16C4C6166BEFDBA218825DBED89C18702ED63A818466FCB3B34F141E54C669BDAF8FC7F87B90171FD4C5FAC724B327572B32CBC7F7B3A79A6117F0779FA79
	A7C57E0F0F6B6957AE727F7A0CDE7E8D11FFA1C7AF5FE3657DF1EBCEF04CE9CFAE7659050226ADB7384F7EBA17775945D65AE7236099EB2573966D335F4A1B2A4F726FBF7918DE8B3C9B2DEA1FCD322AB1982647B5CD7F99D69CF7B5E7EDF70E2B7D6E8AFE196FD5845F4236403E65F0F1FEBCAC63F48F2E6F8E22D34B22767C4FB3016D05F7C052DB393D2F8F686597097C713E4076407C9A117FDF3374722D2D3CAD0E1E76772FCDE44E6D07FC3B2079DFB57E7E4D52297A37025A697C887FDED84F3F05038C6E
	E3287CCEA2BF927051C27ECB0F7C657F3ACB15DFA3CD979BB6C8217DFBBC4828CED67BCD1E7455BEFFFB4410F544C6205BA474B16C7557E741A5D51F5BFB2733C726260F866AD35AC6E5EC813FCA6746485650F370532D534F414BDA7577CDFF559A3CEF7A5BD67DFD5337DB357BA60AFB3E95EDBA82EB17FEC17712408D885C9AD2A6622EB96C4A28407F10680578F69985F2FCBC516B9E4175B35CCFB43881067BD9AB7A90287228DB5CA5FB3315341D4369E45FC0939A078B16956ABE1268687058B4FA3FA3F0
	2A5E6B4FA32F0F2D07E8BBBFFFC5927D64390B3816F24977CC621B01B668B495076C8E1B53BDF2284AD4D3E52ABDD94963AE117734F1173A9D8DA16F0B5734B11F572646DD287CF6F246CA5FFF592F6AF239E0263AD05E5C6BAA6F0F4A711D46790E9F66B169C9109D53464FE49E9C6667EB89ED7C1CA70A50161E07F5BDC0AF406C62BC0FBD124438FCF88A159077C46FB8C74C3707B13AE13E05DE936F058A974A6F02FE52456F3DBA4FF3BD37677233232582F7A31B636285EE1BB8374AEF6738FF4C6738CE01
	5BF3024BD8A8F03FB843F1318277399C0E5B544EFDC4D52EEA574F67F23BD6F500DF6D60FC5F4D65F60D6860F6FD0F502E05F482C05A05595A75991222659F3370BA95DD60B23FB840F13D82975DA76E908B5CCBD99C3792F034ECC32A5AEF7EFB4D6C7A5C09B4356F169855F2229E956B1753840E4C89455432E298EFDB3ADA8E795C1315732F05B35167385067821A196D13FE150105A6C1BD8BAF723D524E1E23D2683DD237576D113D4AFD0AA5DF2132BDF55D61391DBB354D116F512C79E82C3F08FC93DBF7
	B1703C2A0C602D97834F2B1E953E219B72E2AE72B845E09739AF5799623E1C8E1E726D4A753B974768FA59F3B4A41FADDB4BB749689633675210FCBB7D4ECA16B33A556C39BEA45F0B79DA3ECD0CAE3D0B5DF96D8A45376F8472556864935F9E126F6DB348579F024F99126FB1D23F85A17842C372FDA1F7EE7D6EABF3647B412C79E83FF4671133292E407EBAC170B1DD017DB5B90F77579510D7FA09775763962D7CC51758FDE676DCF6A9141D9E777BB6E6A523DB471E89A1793EC94E2C244B7A6D122F03E75F
	EAE4A1DFC5883E869DBC75DBC99978AE4E8128EBE2F929193897405FFC811F68F8C06E8E52088F15F928C292720FEA5EE38E4F7FF94BF767D5860D755D6B6277643AAF73785F3AAEF13FDF604AF2F9DCBFDA60BEB740637AAB846EDF87B86FEA011B48663CC55D9C57AAF8EB846EA9415BA0F007F2B8EF184045885EC8015BFD1D73C68B5CCF487AA58952999FF35C63044E84699A01FBCF70B6885C719341F6899B92E79602EE07A5186E040553458B3ABF498926FBBEC75C9F3C424762F81E5A479EEA5E0DF973
	5F5FCC73AA0356390358E6926A9BF6C558A1196CEFA13DCC600E26F05B448A5CB786B8EFC60F0811BF623CA60133BD92F69538D5E4BDB78969F0012B3E4669A2856E67D91CAED1605A33B9DDD28FDF777930CAECFF7B4B2E696EA825EA2033B74E0840773703700E8BFF3CABC777571748D8F382FE22174BFBBCCB15774E40CC62372164C5417BB10946A9293C2F117A45GFE051077B7592A3C3F1BD21EF19AF909701E69A76FA26977C900CF3F4A65456728722A2E4FA4FE92CADE5DD5FA17D20D43EE4E770FDF
	6C5ACD5A93683A2F723D49B72E7B47AF36662A7A64FEB2DD7D2752E741D59AAFC7FDE8F9CF8ECD57DF26AAAF6C2A5ADFE8F997AC5345FB26EA1FA5C1653D1EB31378D1287216EA4A23369D34785B7FAE71814B00AED6587F75E4FF7B132B6B7C3CA2E5B67D1D470F781D39042B748CEB0B68FFBFA4FEE4B360537AF87FEB5D2D4A1B18767E1FE18AF9A77B68FEE90B68FFBF6E533F67B83AC71559F6EDB6BEA4D0E6A348F4B31BB3FFD12016FDB3FBB67DAD306CCECD59216F567CCD217A7EA13917424EA386E637
	A7A6BF41485E4F1528651C19E5B9C325EAB9AF142B65C44D321C0443EAB9BDC754F27E5B755915F37728DA4ED72A5477FFBA263E7F1BD9FD3FDD2B4AFAAEF9F67B78BFB82E1673AB0D4E1B9E4E2E1CB20D1E6D9ABD6F90BD69EF4C5F6DF8881B0A6837A403A2E56147BBA677AD937B88C611AA5877AAA65F7028EC9FF74A26F11FECB7A9EE13BCB16C9C374B26E1D0406BF67A3CD216D02A03C4A7575FBDE1C97D691329A9D2DA07340DC6EBBB24B48C39F6C88692116D10D29537E782FFC1B8CDD2A307E93E5FC8
	8F2594F8C3C07FCCFE73538F0B7FA27DE831847E7B1D5B8496C7468EE641B4C5B2E095AD0A142B795048C43EB482E4A62B37890FF6A13D3DE25CF547653E672AD424F4E6898B54B4534F7C7F78358FFF367ADF7AFC2952368EE9AFFA499E59CBB6A3329DBF1AE43FF65CA6ED77605744782314E0869FF998FC4AFD275CA9655F3A023FG66D3E4FB935F667894E9B7797116EA7C450E6AF61BEFD441B0F8FD271466425FE516F2E93653669A2926FB1EFAE24B6426C1E29F324481FD73545F7D9025647DEDD6D971
	058D4BD32514B6E90753ED33375906953737C35A2E0CFAE55F285BE91F701CEBA485255FCBE706CD056276096AE3E54D68900552F7A67EA4ACF3E3E69B18FE42ABD593CA1C944C02425EB2EA7302A8595B21E093383D25A26FF38714726F1EAF3811A2A533CAE2B3E6CDF820AD942F4D65435FB1818DCC819A70ACD11079FF3ED07325E75E3DCE8AD2D99569A0B59930D60F486E316A26DB379CAED994G760362770B6211469CC013BB71455D97DE59BA846DED15D247EAEA485FDA7277B87E2DA518DA0229BD8E
	C6CC14267C9718BFC1BAEF1EC2BB0998D9584C66B6E8A9A56E2D971EF82DA1B181EA57A699486F24003133D52ACB63E3C332773168AF6D5F993E7E8B58969E6C923C5CED37ECE38E67FDF8319247B101781209201104278519841B99542B901B935CBD903D1BE496103F9F4C3A7248D5C4F88F20C0FCA9765774082A29D162E7915AB1FF1CE14938777384D98C1B76592B558935D67C0B821C29D3F4B5CD8E21E95ABCEBAAFA90BD3822E40D1C4D62DF85F4675981D5208AE77C7FB3F6232E312D7ADDA8DD114A3B
	EC9326D13C84E132B91D6EFB323D4A74C0763ACDE3B20CFF134BEDF2BA469C8A960737BDF0166037AA60BD1D5D44C0B43F3DC15E59C9BA123053F2F8BD5765F0B9464647CC0A8366DDE5D4B6317F1E44B4B66ED3CCC3324946786C10G5FA31B9C0A4961B379E0567448F6C5EA1D3684C66A4F2F96EB9B31B9DCFE93AB369A2E8537FD760648D57F7E3513F19ABEFB1A5E7057946D19765954F4D855B4DBE353544F1E26377DB565B6D59DBC8F0E978ACB73BE0DF4F83DC51DA232B4584F20FFD27C7D933FDE117E
	595374372A267C92B3515AEFB1013A1778354A8E8DB60DBB604AC75A7B841BBF3FBF64FD0245436CECECB87077673EF7892B3F9E70C9437C5C7340A55C7C043AE75AB386DEDA31B96FB4396CF86734793EA22B5F987C73A5AC5F47E9D8A64D0B3B04FA5F56707CDFD0CB878887027F4A8EA1GG94ECGGD0CB818294G94G88G88GA704A2AE87027F4A8EA1GG94ECGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC8A1GGGG
	
**end of data**/
}

}
