package com.cannontech.dbeditor.wizard.capsubbus;

import com.cannontech.database.db.device.*;
import com.cannontech.database.data.*;
import com.cannontech.database.data.device.*;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Dimension;
 import com.cannontech.database.db.*;
 import com.cannontech.database.data.device.*;

 import com.cannontech.common.gui.util.DataInputPanel;
 
public class CCSubstationBusMiscSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener {
	private javax.swing.JLabel ivjControlIntervalLabel = null;
	private javax.swing.JLabel ivjFailurePercentLabel = null;
	private com.klg.jclass.field.JCSpinField ivjFailurePercentSpinField = null;
	private javax.swing.JLabel ivjMinConfirmPercentLabel = null;
	private com.klg.jclass.field.JCSpinField ivjMinConfirmPercentSpinField = null;
	private javax.swing.JLabel ivjMinResponseTimeLabel = null;
	private javax.swing.JLabel ivjMaxDailyOperationLabel = null;
	private com.klg.jclass.field.JCSpinField ivjMaxDailyOperationSpinField = null;
	private javax.swing.JCheckBox ivjMaxDailyOperationCheckBox = null;
	private javax.swing.JCheckBox ivjJCheckBoxMaxOpDisableFlag = null;
	private javax.swing.JPanel ivjJPanelMaxDaily = null;
	private javax.swing.JComboBox ivjJComboBoxControlMethod = null;
	private javax.swing.JLabel ivjJLabelControlMethod = null;
	private javax.swing.JPanel ivjJPanelDailyOp = null;
	private javax.swing.JComboBox ivjJComboBoxControlInterval = null;
	private javax.swing.JComboBox ivjJComboBoxMinRespTime = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCSubstationBusMiscSettingsPanel() {
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
	if (e.getSource() == getMaxDailyOperationCheckBox()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxControlMethod()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxControlInterval()) 
		connEtoC3(e);
	if (e.getSource() == getJComboBoxMinRespTime()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (MaxDailyOperationCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyMiscSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.maxDailyOperationCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JComboBoxControlMethod.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusMiscSettingsPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JComboBoxControlInterval.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusMiscSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (JComboBoxMinRespTime.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusMiscSettingsPanel.fireInputUpdate()V)
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
 * Return the MaxDailyOperationLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getControlIntervalLabel() {
	if (ivjControlIntervalLabel == null) {
		try {
			ivjControlIntervalLabel = new javax.swing.JLabel();
			ivjControlIntervalLabel.setName("ControlIntervalLabel");
			ivjControlIntervalLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlIntervalLabel.setText("Control Interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlIntervalLabel;
}
/**
 * Return the FailurePercentLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFailurePercentLabel() {
	if (ivjFailurePercentLabel == null) {
		try {
			ivjFailurePercentLabel = new javax.swing.JLabel();
			ivjFailurePercentLabel.setName("FailurePercentLabel");
			ivjFailurePercentLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjFailurePercentLabel.setText("Failure Percent:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFailurePercentLabel;
}
/**
 * Return the FailurePercentSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getFailurePercentSpinField() {
	if (ivjFailurePercentSpinField == null) {
		try {
			ivjFailurePercentSpinField = new com.klg.jclass.field.JCSpinField();
			ivjFailurePercentSpinField.setName("FailurePercentSpinField");
			ivjFailurePercentSpinField.setPreferredSize(new java.awt.Dimension(55, 21));
			ivjFailurePercentSpinField.setMinimumSize(new java.awt.Dimension(55, 21));
			// user code begin {1}
			ivjFailurePercentSpinField.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(90), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFailurePercentSpinField;
}
/**
 * Return the JCheckBoxMaxOpDisableFlag property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxMaxOpDisableFlag() {
	if (ivjJCheckBoxMaxOpDisableFlag == null) {
		try {
			ivjJCheckBoxMaxOpDisableFlag = new javax.swing.JCheckBox();
			ivjJCheckBoxMaxOpDisableFlag.setName("JCheckBoxMaxOpDisableFlag");
			ivjJCheckBoxMaxOpDisableFlag.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJCheckBoxMaxOpDisableFlag.setText("Disable sub after reaching max op count");
			ivjJCheckBoxMaxOpDisableFlag.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMaxOpDisableFlag;
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
 * Return the JComboBoxControlMethod property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxControlMethod() {
	if (ivjJComboBoxControlMethod == null) {
		try {
			ivjJComboBoxControlMethod = new javax.swing.JComboBox();
			ivjJComboBoxControlMethod.setName("JComboBoxControlMethod");
			// user code begin {1}

			ivjJComboBoxControlMethod.addItem( 
				com.cannontech.common.util.StringUtils.addCharBetweenWords( ' ', com.cannontech.database.db.capcontrol.CapControlSubstationBus.CNTRL_INDIVIDUAL_FEEDER ) );

			ivjJComboBoxControlMethod.addItem( 
				com.cannontech.common.util.StringUtils.addCharBetweenWords( ' ', com.cannontech.database.db.capcontrol.CapControlSubstationBus.CNTRL_SUBSTATION_BUS ) );

			ivjJComboBoxControlMethod.addItem( 
				com.cannontech.common.util.StringUtils.addCharBetweenWords( ' ', com.cannontech.database.db.capcontrol.CapControlSubstationBus.CNTRL_BUSOPTIMIZED_FEEDER ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxControlMethod;
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
			// user code begin {1}

			ivjJComboBoxMinRespTime.addItem(com.cannontech.common.util.CtiUtilities.STRING_NONE);
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
 * Return the JLabelControlMethod property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlMethod() {
	if (ivjJLabelControlMethod == null) {
		try {
			ivjJLabelControlMethod = new javax.swing.JLabel();
			ivjJLabelControlMethod.setName("JLabelControlMethod");
			ivjJLabelControlMethod.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelControlMethod.setText("Control Method:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControlMethod;
}
/**
 * Return the JPanelDailyOp property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelDailyOp() {
	if (ivjJPanelDailyOp == null) {
		try {
			ivjJPanelDailyOp = new javax.swing.JPanel();
			ivjJPanelDailyOp.setName("JPanelDailyOp");
			ivjJPanelDailyOp.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanelMaxDaily = new java.awt.GridBagConstraints();
			constraintsJPanelMaxDaily.gridx = 1; constraintsJPanelMaxDaily.gridy = 2;
			constraintsJPanelMaxDaily.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelMaxDaily.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelMaxDaily.weightx = 1.0;
			constraintsJPanelMaxDaily.weighty = 1.0;
			constraintsJPanelMaxDaily.ipadx = 14;
			constraintsJPanelMaxDaily.ipady = -4;
			constraintsJPanelMaxDaily.insets = new java.awt.Insets(1, 18, 3, 3);
			getJPanelDailyOp().add(getJPanelMaxDaily(), constraintsJPanelMaxDaily);

			java.awt.GridBagConstraints constraintsMaxDailyOperationCheckBox = new java.awt.GridBagConstraints();
			constraintsMaxDailyOperationCheckBox.gridx = 1; constraintsMaxDailyOperationCheckBox.gridy = 1;
			constraintsMaxDailyOperationCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMaxDailyOperationCheckBox.insets = new java.awt.Insets(1, 1, 0, 149);
			getJPanelDailyOp().add(getMaxDailyOperationCheckBox(), constraintsMaxDailyOperationCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDailyOp;
}
/**
 * Return the JPanelMaxDaily property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelMaxDaily() {
	if (ivjJPanelMaxDaily == null) {
		try {
			ivjJPanelMaxDaily = new javax.swing.JPanel();
			ivjJPanelMaxDaily.setName("JPanelMaxDaily");
			ivjJPanelMaxDaily.setBorder(new javax.swing.border.EtchedBorder());
			ivjJPanelMaxDaily.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsMaxDailyOperationSpinField = new java.awt.GridBagConstraints();
			constraintsMaxDailyOperationSpinField.gridx = 2; constraintsMaxDailyOperationSpinField.gridy = 1;
			constraintsMaxDailyOperationSpinField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMaxDailyOperationSpinField.insets = new java.awt.Insets(5, 0, 2, 43);
			getJPanelMaxDaily().add(getMaxDailyOperationSpinField(), constraintsMaxDailyOperationSpinField);

			java.awt.GridBagConstraints constraintsMaxDailyOperationLabel = new java.awt.GridBagConstraints();
			constraintsMaxDailyOperationLabel.gridx = 1; constraintsMaxDailyOperationLabel.gridy = 1;
			constraintsMaxDailyOperationLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMaxDailyOperationLabel.ipadx = 17;
			constraintsMaxDailyOperationLabel.insets = new java.awt.Insets(5, 10, 4, 0);
			getJPanelMaxDaily().add(getMaxDailyOperationLabel(), constraintsMaxDailyOperationLabel);

			java.awt.GridBagConstraints constraintsJCheckBoxMaxOpDisableFlag = new java.awt.GridBagConstraints();
			constraintsJCheckBoxMaxOpDisableFlag.gridx = 1; constraintsJCheckBoxMaxOpDisableFlag.gridy = 2;
			constraintsJCheckBoxMaxOpDisableFlag.gridwidth = 2;
			constraintsJCheckBoxMaxOpDisableFlag.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxMaxOpDisableFlag.ipadx = 3;
			constraintsJCheckBoxMaxOpDisableFlag.ipady = 3;
			constraintsJCheckBoxMaxOpDisableFlag.insets = new java.awt.Insets(2, 10, 7, 4);
			getJPanelMaxDaily().add(getJCheckBoxMaxOpDisableFlag(), constraintsJCheckBoxMaxOpDisableFlag);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelMaxDaily;
}
/**
 * Return the MaxDailyOperationCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getMaxDailyOperationCheckBox() {
	if (ivjMaxDailyOperationCheckBox == null) {
		try {
			ivjMaxDailyOperationCheckBox = new javax.swing.JCheckBox();
			ivjMaxDailyOperationCheckBox.setName("MaxDailyOperationCheckBox");
			ivjMaxDailyOperationCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMaxDailyOperationCheckBox.setText("Max Daily Operation");
			ivjMaxDailyOperationCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaxDailyOperationCheckBox;
}
/**
 * Return the MaxDailyOperationLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMaxDailyOperationLabel() {
	if (ivjMaxDailyOperationLabel == null) {
		try {
			ivjMaxDailyOperationLabel = new javax.swing.JLabel();
			ivjMaxDailyOperationLabel.setName("MaxDailyOperationLabel");
			ivjMaxDailyOperationLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMaxDailyOperationLabel.setText("Max Daily Operation:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaxDailyOperationLabel;
}
/**
 * Return the MaxDailyOperationSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getMaxDailyOperationSpinField() {
	if (ivjMaxDailyOperationSpinField == null) {
		try {
			ivjMaxDailyOperationSpinField = new com.klg.jclass.field.JCSpinField();
			ivjMaxDailyOperationSpinField.setName("MaxDailyOperationSpinField");
			ivjMaxDailyOperationSpinField.setPreferredSize(new java.awt.Dimension(55, 21));
			ivjMaxDailyOperationSpinField.setMinimumSize(new java.awt.Dimension(55, 21));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaxDailyOperationSpinField;
}
/**
 * Return the MinConfirmPercentLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMinConfirmPercentLabel() {
	if (ivjMinConfirmPercentLabel == null) {
		try {
			ivjMinConfirmPercentLabel = new javax.swing.JLabel();
			ivjMinConfirmPercentLabel.setName("MinConfirmPercentLabel");
			ivjMinConfirmPercentLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMinConfirmPercentLabel.setText("Min Confirm Percent:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinConfirmPercentLabel;
}
/**
 * Return the MinConfirmPercentSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getMinConfirmPercentSpinField() {
	if (ivjMinConfirmPercentSpinField == null) {
		try {
			ivjMinConfirmPercentSpinField = new com.klg.jclass.field.JCSpinField();
			ivjMinConfirmPercentSpinField.setName("MinConfirmPercentSpinField");
			ivjMinConfirmPercentSpinField.setPreferredSize(new java.awt.Dimension(55, 21));
			ivjMinConfirmPercentSpinField.setMinimumSize(new java.awt.Dimension(55, 21));
			// user code begin {1}
			ivjMinConfirmPercentSpinField.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(100), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinConfirmPercentSpinField;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the MinResponseTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMinResponseTimeLabel() {
	if (ivjMinResponseTimeLabel == null) {
		try {
			ivjMinResponseTimeLabel = new javax.swing.JLabel();
			ivjMinResponseTimeLabel.setName("MinResponseTimeLabel");
			ivjMinResponseTimeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMinResponseTimeLabel.setText("Min Response Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinResponseTimeLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.capcontrol.CapControlSubBus subBus = ((com.cannontech.database.data.capcontrol.CapControlSubBus) val);

	subBus.getCapControlSubstationBus().setControlInterval( 
			com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue(getJComboBoxControlInterval()) );

	subBus.getCapControlSubstationBus().setMinResponseTime( 
			com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue(getJComboBoxMinRespTime()) );

	
	Object spinFieldVal = getMinConfirmPercentSpinField().getValue();
	subBus.getCapControlSubstationBus().setMinConfirmPercent( new Integer(((Number)spinFieldVal).intValue()) );

	spinFieldVal = getFailurePercentSpinField().getValue();
	subBus.getCapControlSubstationBus().setFailurePercent( new Integer(((Number)spinFieldVal).intValue()) );

	subBus.getCapControlSubstationBus().setControlMethod( 
		com.cannontech.common.util.StringUtils.removeChars( ' ', getJComboBoxControlMethod().getSelectedItem().toString() ) );

	if( getMaxDailyOperationCheckBox().isSelected() )
	{
		spinFieldVal = getMaxDailyOperationSpinField().getValue();
		subBus.getCapControlSubstationBus().setMaxDailyOperation( new Integer(((Number)spinFieldVal).intValue()) );
		subBus.getCapControlSubstationBus().setMaxOperationDisableFlag( 
					getJCheckBoxMaxOpDisableFlag().isSelected() 
					? new Character('Y')
					: new Character('N') );
	}
	else
	{
		subBus.getCapControlSubstationBus().setMaxDailyOperation( new Integer(0) );
		subBus.getCapControlSubstationBus().setMaxOperationDisableFlag( new Character('N') );
	}

	return val;
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

	// had to add this manualy. For some reason Visual Age
	// does not allow me to add it in the Visual Composition Editor??
	getMaxDailyOperationSpinField().addValueListener( this );
	getMinConfirmPercentSpinField().addValueListener( this );
	getFailurePercentSpinField().addValueListener( this );

	// user code end
	getMaxDailyOperationCheckBox().addActionListener(this);
	getJComboBoxControlMethod().addActionListener(this);
	getJComboBoxControlInterval().addActionListener(this);
	getJComboBoxMinRespTime().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceNameAddressPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(316, 264);

		java.awt.GridBagConstraints constraintsControlIntervalLabel = new java.awt.GridBagConstraints();
		constraintsControlIntervalLabel.gridx = 1; constraintsControlIntervalLabel.gridy = 2;
		constraintsControlIntervalLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsControlIntervalLabel.insets = new java.awt.Insets(6, 11, 6, 39);
		add(getControlIntervalLabel(), constraintsControlIntervalLabel);

		java.awt.GridBagConstraints constraintsMinResponseTimeLabel = new java.awt.GridBagConstraints();
		constraintsMinResponseTimeLabel.gridx = 1; constraintsMinResponseTimeLabel.gridy = 3;
		constraintsMinResponseTimeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsMinResponseTimeLabel.insets = new java.awt.Insets(6, 11, 7, 10);
		add(getMinResponseTimeLabel(), constraintsMinResponseTimeLabel);

		java.awt.GridBagConstraints constraintsMinConfirmPercentLabel = new java.awt.GridBagConstraints();
		constraintsMinConfirmPercentLabel.gridx = 1; constraintsMinConfirmPercentLabel.gridy = 4;
		constraintsMinConfirmPercentLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsMinConfirmPercentLabel.insets = new java.awt.Insets(5, 11, 7, 7);
		add(getMinConfirmPercentLabel(), constraintsMinConfirmPercentLabel);

		java.awt.GridBagConstraints constraintsMinConfirmPercentSpinField = new java.awt.GridBagConstraints();
		constraintsMinConfirmPercentSpinField.gridx = 2; constraintsMinConfirmPercentSpinField.gridy = 4;
		constraintsMinConfirmPercentSpinField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsMinConfirmPercentSpinField.insets = new java.awt.Insets(5, 8, 5, 103);
		add(getMinConfirmPercentSpinField(), constraintsMinConfirmPercentSpinField);

		java.awt.GridBagConstraints constraintsFailurePercentLabel = new java.awt.GridBagConstraints();
		constraintsFailurePercentLabel.gridx = 1; constraintsFailurePercentLabel.gridy = 5;
		constraintsFailurePercentLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsFailurePercentLabel.insets = new java.awt.Insets(5, 11, 3, 38);
		add(getFailurePercentLabel(), constraintsFailurePercentLabel);

		java.awt.GridBagConstraints constraintsFailurePercentSpinField = new java.awt.GridBagConstraints();
		constraintsFailurePercentSpinField.gridx = 2; constraintsFailurePercentSpinField.gridy = 5;
		constraintsFailurePercentSpinField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsFailurePercentSpinField.insets = new java.awt.Insets(5, 8, 1, 103);
		add(getFailurePercentSpinField(), constraintsFailurePercentSpinField);

		java.awt.GridBagConstraints constraintsJLabelControlMethod = new java.awt.GridBagConstraints();
		constraintsJLabelControlMethod.gridx = 1; constraintsJLabelControlMethod.gridy = 1;
		constraintsJLabelControlMethod.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelControlMethod.insets = new java.awt.Insets(11, 11, 5, 38);
		add(getJLabelControlMethod(), constraintsJLabelControlMethod);

		java.awt.GridBagConstraints constraintsJComboBoxControlMethod = new java.awt.GridBagConstraints();
		constraintsJComboBoxControlMethod.gridx = 2; constraintsJComboBoxControlMethod.gridy = 1;
		constraintsJComboBoxControlMethod.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxControlMethod.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxControlMethod.weightx = 1.0;
		constraintsJComboBoxControlMethod.ipadx = 18;
		constraintsJComboBoxControlMethod.insets = new java.awt.Insets(9, 8, 3, 14);
		add(getJComboBoxControlMethod(), constraintsJComboBoxControlMethod);

		java.awt.GridBagConstraints constraintsJPanelDailyOp = new java.awt.GridBagConstraints();
		constraintsJPanelDailyOp.gridx = 1; constraintsJPanelDailyOp.gridy = 6;
		constraintsJPanelDailyOp.gridwidth = 2;
		constraintsJPanelDailyOp.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDailyOp.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelDailyOp.weightx = 1.0;
		constraintsJPanelDailyOp.weighty = 1.0;
		constraintsJPanelDailyOp.insets = new java.awt.Insets(1, 11, 19, 7);
		add(getJPanelDailyOp(), constraintsJPanelDailyOp);

		java.awt.GridBagConstraints constraintsJComboBoxControlInterval = new java.awt.GridBagConstraints();
		constraintsJComboBoxControlInterval.gridx = 2; constraintsJComboBoxControlInterval.gridy = 2;
		constraintsJComboBoxControlInterval.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxControlInterval.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxControlInterval.weightx = 1.0;
		constraintsJComboBoxControlInterval.ipadx = 18;
		constraintsJComboBoxControlInterval.insets = new java.awt.Insets(4, 8, 4, 14);
		add(getJComboBoxControlInterval(), constraintsJComboBoxControlInterval);

		java.awt.GridBagConstraints constraintsJComboBoxMinRespTime = new java.awt.GridBagConstraints();
		constraintsJComboBoxMinRespTime.gridx = 2; constraintsJComboBoxMinRespTime.gridy = 3;
		constraintsJComboBoxMinRespTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxMinRespTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxMinRespTime.weightx = 1.0;
		constraintsJComboBoxMinRespTime.ipadx = 18;
		constraintsJComboBoxMinRespTime.insets = new java.awt.Insets(4, 8, 5, 14);
		add(getJComboBoxMinRespTime(), constraintsJComboBoxMinRespTime);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	getMaxDailyOperationCheckBox().setSelected(false);
	getMaxDailyOperationLabel().setEnabled(false);
	getJCheckBoxMaxOpDisableFlag().setEnabled(false);
	
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	return true;
}
/**
 * Comment
 */
public void jComboBoxControlMethod_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CCSubstationBusMiscSettingsPanel aCCSubstationBusMiscSettingsPanel;
		aCCSubstationBusMiscSettingsPanel = new CCSubstationBusMiscSettingsPanel();
		frame.setContentPane(aCCSubstationBusMiscSettingsPanel);
		frame.setSize(aCCSubstationBusMiscSettingsPanel.getSize());
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
 * Comment
 */
public void maxDailyOperationCheckBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	this.fireInputUpdate();
	
	if( getMaxDailyOperationCheckBox().isSelected() )
	{
		getMaxDailyOperationSpinField().setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(999), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
		getMaxDailyOperationLabel().setEnabled(true);
		getMaxDailyOperationSpinField().setEnabled(true);
		getJCheckBoxMaxOpDisableFlag().setEnabled(true);
	}
	else
	{
		getJCheckBoxMaxOpDisableFlag().setEnabled(false);
		getMaxDailyOperationLabel().setEnabled(false);
		getMaxDailyOperationSpinField().setEnabled(false);
		getMaxDailyOperationSpinField().setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(0), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
	}		

	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	com.cannontech.database.data.capcontrol.CapControlSubBus subBus = ((com.cannontech.database.data.capcontrol.CapControlSubBus) val);


	if( subBus.getCapControlSubstationBus().getControlInterval().intValue() == 0 )
		getJComboBoxControlInterval().setSelectedIndex(0);
	else
		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
				getJComboBoxControlInterval(),
				subBus.getCapControlSubstationBus().getControlInterval().intValue() );

	if( subBus.getCapControlSubstationBus().getMinResponseTime().intValue() == 0 )
		getJComboBoxMinRespTime().setSelectedIndex(0);
	else
		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
				getJComboBoxMinRespTime(),
				subBus.getCapControlSubstationBus().getMinResponseTime().intValue() );

	
	getMinConfirmPercentSpinField().setValue( subBus.getCapControlSubstationBus().getMinConfirmPercent() );

	getFailurePercentSpinField().setValue(	subBus.getCapControlSubstationBus().getFailurePercent() );

	getJComboBoxControlMethod().setSelectedItem( 
		com.cannontech.common.util.StringUtils.addCharBetweenWords( ' ', subBus.getCapControlSubstationBus().getControlMethod() ) );


	Integer maxDailyOperation = subBus.getCapControlSubstationBus().getMaxDailyOperation();
	if( maxDailyOperation != null
		 && maxDailyOperation.intValue() > 0 )
	{
		getMaxDailyOperationCheckBox().doClick();
		getMaxDailyOperationSpinField().setValue(maxDailyOperation);

		getJCheckBoxMaxOpDisableFlag().setSelected(
				subBus.getCapControlSubstationBus().getMaxOperationDisableFlag().charValue() == 'Y'
				|| subBus.getCapControlSubstationBus().getMaxOperationDisableFlag().charValue() == 'y' );
	}


	return;
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	// if its a spinner, do something
	if( arg1.getSource() == getMaxDailyOperationSpinField() ||
		 arg1.getSource() == getMinConfirmPercentSpinField() ||
		 arg1.getSource() == getFailurePercentSpinField() )
	{
		this.fireInputUpdate();
	}
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
	D0CB838494G88G88G9BF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8BD4D45735E80CA1892944E8C37C05A89A125A46365865CAD8DD06E7D64D8BF925ED32123E7ADA5B32ACFD25FD2C56571A54BEAF3F81951588D192CDC2ABA6FE882290C1C0859D09A89A9A88120628434C9D981919BB19398303107876BE3F39B3F3070FFD3E553232F36F59676C4F59671CFD76BE770CD271DB313919D945121419AB313F1ACCC90AEA13241FCDBDB60D63A264A40324797BB340AB
	D21FB1871E51D0D73DB449D0A83D7DDABE1463205C7015A443CEF8AF125EAB0FBA83AFE164D8206E791211ED234B79ED971713C74AB1F585705C8A908FB8FC2E15D17E123A9206CFE6F88E19D91214E645B64F546DE638DAA837814481AC335276A9705CC8A45FDED74B5AF55F9BAB2D7D57F73236A09F8DCF8E153D143603BDCB2547B25777222C35C4CF240902728AG6CFC3674CD25991E8DED7D3DA74C0A2342ECF2BA9527AA1B7BAA86ED57CDEE8BE0DC9EEFCF0F57D3D7F77AF43B3747231AD41B62BC6575
	DC30F94C6D322A5A1C3D1EB613D336CB122F8FE5B55467935E125488659C9B6226D5217CC4A8BF8710AE701D5508BF8B6FD781FCB65E67636FB430BEF58DBFA6A53C604C5E96264F51B65A57E5B6FF1FE7F8DF4EFB0D0C0F2F9B47E1AD5415DCCBB21483348138G2281F611F9F163138370EC6AD1CFAB8E0762ECF239CE59AD6E8B0AC53643FBDD9D54B05C0732D9F1DBA4090E7D4BF565BA7A0C8614AE3FFE22F454A671D5FC9EAC7EF011346A1B673372F4EC32AA3EB2AB7E9A354572EBFE1BD059CDE1E52F3E
	96B8CF83E5DF1234326326AA5939BA32931E5F1F351671496F774B2E6D47314EA6E33D8F5EB3597C2078AD84BF224153B9BB134C99ACC7C1DDC6BF1F9BDDDB39AE2DB9332442432D3AF608C9EA4A6A30D39D6C76E0DDBEA16BA146CEE5EE943A7491FC0286CFF569A6BAE2B9856A2A9DC986BA3FA6DD46B59C83658400E400D4009CE7122194205A4967584ACF8E0DF30E35195C6AA92F4DEE115D92251D543D831E32CFF51BDA1C307E1CE63953643619FA6C32C7E2ED6E6AC69B748450CDA3F35F8CB25E171D32
	1B2CDB60EEF2482A6C7650B6B15D2FE09B37CCEABBEC8E9984B8DC92679B5BDD0476B3F9540FDC9613AA7BDB507A78EE3449002993B88283F827F6795A9534D787745F8EB0A26C3067D364F7DEF6033F282BEBD514FE2F2B83FBA2A5BB514EDD44F7C443FB3C135B78512B08ABD4128C7B944E67EF1FEE5770B15A4C283E49BD44575846EA74538D8A1FB33F6962F34638E521D47E7CB95DB903F0D521F34527842F1DA2429375226559D5DDE32C5B86C5FFCD0D5FD7A40CA9A37BA679EF95E3D55203E3958B3AA6
	822C92B61E79E9A91D3327CC9E194F528F5CB041E40B5F62D4CFD3DDA133A76A134B7427F5CED23796786EF6A5990CGF69737E1CFF5178E5D4D76C75F0643C406167F467FC39954D6F7DF467DC99E101D2AD1F649CE8B3CF8D85D744BA80F2DDF2BA2E8E2885E856B7DB4ACB00F404F3B0CEB52242AEEDB0FD715799EAB6A9791D930AB3B7C2E024B7A6AE56C23B2886BDD347F46E5DCABE64569144DF46FE6A42CBE012CD559AEBBC069530A5729D27C63D7F03E774BC34CD7FC7B8A86C483A63B977D8E0D9F16
	18B6E39F5CB687CC85F4DD52BA972EE3253EC4E323990043B8A8991E2BDDFCCD8E6E61EB326C7AC35272ABFB337476141805462C464FD8DC7319FFCDD21D9E3B2A2F7BD262B3985EC3F57D60G7AD123BCE0B34B97418BB6D9ACEE5943E3973A967EEAC6FEAA480984087D0C2F0543A62C677E06AD02530A5DCECDAAEC7DE68F4EF50F7C19D786B70D0E6D3C305F5E1E6DC1F51D4C16347E5D9E3425DD63932738511673C51C388C74C98148780C5A3352CD6DC15BD96BF93B94A8278164FBE83B339EED3B838D3C
	DD9F60A3GE2F55B7DC634DB8D78F5BA6D28DD732EE0CC2197C376FAED95DE55069E5A244264F7F9D5E2ED9AA77E1078FA64D12272F870F7A7507EEDD06E8308D079DC795E969E8F4E680F15D6AFF8BDEC8C9C23D25D92D57FDCA93D0D470333ADA8F3BD5495F879584677E87724A60FC7B15B48F648466512D9BB17A82E5B3C891ED61B5B233640E27771B16CB1233E38DFB2B40B9F1EA1F18576359464F681E4F891FFE330D823FBB46016812C8248A67C0E549FC1DFC0A6D985FD3449EE2B62F64816FD57CC83
	268A5320DAC17CCEC59329BE036F7B7958C4BA4F4253E17299CDB67B5087AE96861C6E134D7D27945F1526A0162D61F9566F6F24E3B13F2811691B7C35D9D2430D22AC3D58BCF25B9BD93583F48C2E8E7847E2649B066C744BFF4C23F619D64F6D32985A248EF03BBCFDD4EB175D03B49EB08EF23B7C7E6F344B0A7D5B697849E4D65260E65F7ECEBEDE6D8DDC2FAE1099BD48752A2C5253EB25506B71061B550B4A5DF91C4BDD85BC0BFDDC6EB62F1E5CD69F5FC7FF9C9257DF7579E38F55979A0750F8776113A8
	AF52C7635D1D827F8441AF576029FEAF11750965F5D017B044E334772C686FDD57138CD1GF1G09G2BG56DF676B6CBF4DD55298B1F08B788D3753E4EF8F585CF8ACEAC19B43FE444AF1963493590FA43E57FFAFA4A7AB998E6CFBF5D019527DA4046E4D20F2D7909D1DC7A7BAF7F15F07236B2B708C42DEDDD7F70E2FB0369FFEEDD03B9F3A0669784D40A7G75075F9AC4BF03EBEC79B077FB1FB7EB7DDE6236A3D91B43783DF5CC3F12917FDAAB97F970CFFA5117F7C11D8A9085908F108C1086D070B99F23
	8A7978D8E3C4A6EC07F29AF0E7E12FBA9D9CD7881B4E8C1913639B0B85A1F495C165724F754622579EEEAC94C70F02E3C16DAC557073896367FC7DFC5CF0F33A760650856BBA9224AB5521370A6BB0C3687046711B5361E088DD308E493ABAB4FB398E29D0CF732F7FA6FB726EAFA036780263169FC7DC87146DDFF0DD3FFA92E5C6C3F989C08A40DA005DB7128C8DGD781A2G62G1683245E60F4CF923A1C08A4C39900914082B085A08EE0C5840DDBF08F0CE0B187255BF0E289539D65A06F2DCCFE70BA58FD
	C3BF670A5341232EA9BABC4683CBB4F429C13C2F32FED35DABCEF25DB712B2FD574B5BB60711D524035BA25DFC2E142BF95F99D407FB792B920FE7CED84367B5E72C7AF94DF92BFEDE73D76BAB948FBC34279C3C7EE36B68F90F492A17773CC1EC79740EED043747A3FB0803420013557DC2457E0464BF188230F1A812726BB732B6E7D46574E1097B145F6DC73F2D63D3B4F16C1AC19E4726C670B856F19477495D13128C55G2D1378DCF8E2D03BD6AD1358D983FBC641B3F6D260DC227E5BEA437DBF816AD2G
	52G2449C90652495CEF7F41BA665EFA46B74A5E7A4B5E70631C512BBF4E3F6F45B968D13CEE339CBC16FF6AAD91F57E9D22C5159DE8F65A465007742A495DAB074C05ADFDA522AE9C7D0E3EF073050EE9F240189E11383F4C2D996D7C35F1F228BF207C229B347C2A84BFFB55E87CBA42726BB82E65D7AD781D770E462FAFAC3F02864C4F6D26A1452BCAD25AE41C07FB9AF85CB1826599G09GA91335795B5787516FD85DA687182F5DA51B259537A16D8B47B9EDA5140DG9600A9GF13771F8F76BE11C17B0
	C22ADB3113688D6231D6D30FEC2775EB0A317E024D79216CF1A9CE8FB9C054541F6D5FC16B0185C4638E88164DB075C88B5EAF7B7657755A343BEC4EE61BEC3770F60BBA50CFB6C32263F54B810CE83D6B6C4E10FABF935A66DB6FA20FF3040C756A022C76A93CBE65DD54D7C49401CDD81B6F6F47F9CDBD84CB3B44987986CAF4683961E81B1D270A35ED187150F0B497D8FFD21B8B94CC2131E465941EAB1807756A0D22FE77C53D7AAED19F5B242D37337AD8D17F9FDEBD7A84D16F9D5023DFA96A332B7568D7
	CD61BE72D7034173BFE30AFF4E675C9EAE1779A97139A537531CA5D76053893EDC0327390C67A84F359A21AE6EF69E6FFC1F78737C29C90612299CD7F489F165D0EE90B85FA59E971D9538FE92D345C0F971D46E1B1D36703E55EB53772D43B67DBDF412ED74BD74CEDBB81FC8755BDD43F5DEA6F43E2D0A63726F60389EAF47F5810E5A6BFC837F06988F3855F770EF08072FE2EEA176C84CF06B6A5EF75BAC27CC3D2D449B51FD723E06FCC6DF92497749A7894FB6A87781C4C4723C4777B64FFB96AF1AA535C6
	1C57BD6FD95DF1A4ABA6121D77C47A731EF848FB284CBF9D67B257C1FD651D08BBF8C2BB7FF04D3D12C5E3E6DA7FE9C0BD5AF30F242D1F96D00F7A8E8550BF99D00FB6FA0351533EBD331777ED7DB37112FD693130E7D93EBBE91F6630E7DB679E296C2EBF663158449AA6B6A15E0B763F2A917B0F6B6A39BBF91EB9EB18638A6E6238760B9CE73C0B0F6B97C75F16B467FA7D765E0AEBE61C0D95D6741818D8E9FCB06573C29367B3A2F8978FF0DC0240AD9638F4012B2B6638323B79BC4C7271FC24DB60C60678
	5C0C381B7B0CDF9CC5DC8A1453856E3784D79CC54FCCA9EE6868E85FD310F63438B9E50CFAAAE37A28B2F2C7296373F12CFA08578671EC5E8A5B079A05B659C0FCD6AD3C9B23785EFC609236D14139AC0B423B8CFF40EDC85AFD8F1943170CE3485042FA20C7584366E961BD5417E68AFDA6FBB176F0C03CE6F3CA9215C75C9213D7D5B31A3C5CA0F99620715DC365E1DD943C6FE572A83FD9D5E37153720C896237B8045F10F7ACFEF99AFE4B0278A5EB7851B9705710F301310052DD39C93A188953513D71BEF2
	5E7822E6BCA9FE8E4127EA70F48F780E5883B2202E63CB7C7C6F2C87FD51B2A8BF87108E10B38DF2FC0046E9FC9FFC6BDA25B4C60E522138DAE12334EB36B65A3F2C93B7F7E76249BC6EEB939E1FAFAD1B7AD2E63873330EE9F4ED2B6C1956310760E97FFE24E27F6220EEB9C0AA40FA004268A4433EE85E3F5EEB6534FFE7ACB635C9D5CD66BE724500F72E53A68F7A5198E8533E55B6EA7B56904DFCBDFBDA22C3FD8E25FBF0D8CB67E36D2258B3B6ACDD57C5ADDD82EB3712BDD70525DB554469B2F45A645C8B
	71523DFA67D32F8DE8651533B60D6C59F1EFB8F9DF97F4FDBAEDC6G972DAB6FE435D6DE9CEB330CBD13F5F851DC69200F67BB2B6F253952BAF896CC2767D154CF5CB7B4165F443D1C7B1EB2202D1DCE7D8416DB27877B4D7F7A9869E82EF001FD25917966AE1CFB9742FD3B01BAFA36DCD693FC360C7AF601ACBB6ACEFCF15D30A4E466DD7A8BBC1D72E02978A0518C9B493E3FD8ECA63B527B1772524A725243557B799A0A9D545EBF2B5DD4237B1D96E14AF4EA5B9876CC3CE38F63715DCF34BC8AE77DB9ABB3
	8C0F94C63BEA3ABF1EFBCB22E7E079127F0F766B17A378CEE57C34002F755DDF19AD99DFF8A9F3CF98990577316FD27779E524DDDB1F97AAA763E4F8596DDF603B9E8D2D1FFCE3747AA524C4676C83477D6F73B56F4FB60CF57F636F81AAE34DAD15414E7EBC61F320AE0FFE8EF459231F83D9BC231F935ABD616FC7F8BC5A7B915437C7E4E23C9E83F3A18160C500F4GE986776B19AA2E973C21956A534FD8FA71FC8BEFE529DCFE2A9A3E2F3FD2473E4B35D645EF4BBD0A2AAA8E644F71AF2BB8DE2A62A2C80A
	5B20220D5C365EBE15B6E578DC123F59E5ABC153387983128B6C06FE154F60317441B244B5C239CD60224BF8FC5DADF05FA2B943E2A8A78A5CEC02CB0172EA0113C6B8ED3A40A9A437E81819E4E81D49F13BCA90F7954AAE010BA86134A38277BB12CFA4C3B9CD601E906D568B5C90499DDA3F1CE430FC1963AAC90E6103F22440AD7888F1F3203CD86072BBC5CEACF0AB89ED7E7D90435C4FF11FD673F6358277E552AE924A318257C95AA5C0F90540B53D0E3897211CA6F009D7B83F3577739C3B5D4763ABE38C
	58A10667C633FDBC0FCD086139735324EDBA169F007D89E0FC7E0B7B1FA25C8ED85E56DD43F32F6A87B87F126B485F8E65A9GF39E60315E72EC52BFA827BCA06E508E9F04E727699A0B6AB084D25CE415G324D2D406E258ED1AC397542565E93182230B97B54705EF0BCE0251D033D6A031EEB32D96DB0750A353BA69777638C5023E0966477GE7813A81D400C800B9GCBGD6822C86D88B10BF9B6C85D08E508850B1DBFC7F7CFC7FA87DE0079AE4413BCDB6DC72CC77484C70EBFEDA6658EBBEA6135CB101
	355AB164E2675BF3B3B127D22CD60FACFC4B7C4CC201EB7134A99E9B0684F4DF37B6866604A3E43D77C15F5459BCA7BCD5961893A66110339C68613231E29BEDCE655364C0B8AF63E68747B64F8E876A73F362AF16C03BC4214FB7068375B930512F4F4E113174499BC51F7490FD26DE8C54E78E71CB6B20DD4E9CF15F26A4D01F8B1B7C7A5CDFB216BE9BC75127FACE30BE1BCEE12C68F24BD6596D16AD6D366B683F29EEFBCE913F8CB42DF3F86C797DD165670EA15F97A2BF3A9AF3A1074DE9F3F89DC4BA159D
	CF7C5794E89FAD6C727EC0206DBEA5FEAF916AD70AB60FFF94E8BB57E63F6D5E6A9C4BF61BC751FDDD086EBF9988EF3BF5C4371C3970BE175BCE2D9E4BF62349EF189BAC7F2B837A36FB0A48BE8B6DBB66F23BB82A83EDF79B31EF8454C709B6DD2F875AEEDE015FF6BF38B211331AE05D93B53A333B8E3E1BBBE3683DC93A58895351B30863C3E80415F3034FA61A897E393941E7935F95FB67EA28B34E63E7FE26006FFD7100CF1C674F81D366056603D4CE94597FD267D1B9F9829FC3706BB5F8AA7FCD0247F2
	7103B0868F72BD6529EC64A962D53A7DB1FF1E1C0D31A7CCA395F2AF5BF5126035C3C3E7AF0B79F2436FAB1906317715CD867DD83250909ACB3EEA500FA5F7993431A4451512B362DE588F3BC43B3D827731409DA0BC91F749E6D17B843E1268057833B28A647823C42FC1026BE238FA8D6EE306EBB4605E88DD6ED35C1C760C012CB31B5DCE4BAD240D4D89DB253047C522234DE552685DAEF028B7DDFFE7486F40685AFC2BEC3C79B2256D43AD1A3D2F586E67B3EDF8E2FC666C707349FE4D4F67119D9363D377
	3A1F4F11BDFEBECBCA5E8C66E334C1F603BF6458775EFB6525C78E8B9ED74B7CBCEE5B6B67315CBBFE9E16FDFE3A7BBF1AD89F6E586F177F6881BF1F8F2B47AF7F5903FE9E9965FE9EEB2A464F2375101FCE59B931BEA8F87C49FC6269A17FFB2B667DF95F2D4F77FFF3CBE530EF23A41FD885BEB063C11E6F7C2116FC0F09CDB26C0B653801BA1E833546F2FFBD1D7851B9D0DE8110A67073883E70A10873815A9E6278326B5A7EA08D52CEC46734D15AD3994783E470B8F822369FED8F877FD0037E2AC7F1138F
	3A74CCF211B85BF3C1FFA3819281D2G0A45F71E2D47C26FFD6CBD4669529E22E71952FC2019CF69E8DCF1CF5DC4627DF220ED1C6FBF4B6C00F77C2E7952068D9B18AF28A777B4024FABC36EE9AC2C5F2157AE604E475F08439337E8217187AE234D03FBDB331D64FF74AE41772E875FB5407EDD1D4F6F9AA455845FB540FAD5548FF5855F3F477A4879BC9F7F379A7E9B44B900FBEEBE7FB61ADB0ACEB0785BEB859D3C3A3AB32A39CF361CE2C349F26C9D35BC07AFD840F3ECE3AD72B7C2399BC0DD4065DEB8A6
	FE2F8B38958B78B9404CE31C47FA4163DFC85B6AB8D8C3G1638091E83CC476BEECC47FC71EDF8A40E4BFCC91C8DA4885C12AEFED7B9BD0E2F4367C63DD705D036D06F3C7EE7E352855F4526F43BEAEFAE2EDCC6EC5E38B0B82EFC1260DB9786471567EA79D8F4C1DD62C25E5F76E3B7978F1BEE12EE560469E83FEA48EFCFCA9785773711604B97857777E9B177EA21EE4EA25E5FAD62FB1374F0283FAC865C3E07754677FF8246370135E9E3CF0B8EAFCA3734AB607B96EB97451E3170CC88222577BD1797E1EE
	E8F35AEDCE79B47D752B6079ED7C8C415E3F23F9EF2B3D757B2E6D7FC14671BAFF1F1AB56F5FBDF66BE5275DD299B4DEA8A37EE9A50CFB6A43626C371647906B846E27640E4859F8588B62B96ED552CE057294014BA07EB0864A718277D1A66717A0F049445F65BC827B43A39CB769866276C139D660565C6034C6014BA27744E6C0B97E913EE7578F07BF3F68A0FDCC00364B9F61FBF5F46DC46ECF24839D82BF83109E8DBE3FD8B724FFFED1F89DE597C07B12C7392E653501679727089D0DD05FA65A64E5861E
	DFAC456B366C7C421EB911730BE05DA39E753FD3D9BF6F8A546787FBD01FA8E897A374093991284F0BD5FEFD7EF4E3BCF1CFEE98FDD6BECA7DE11E506735CB21F7DF7E0857F9597B1BC7A55156D9BB1EDCA37CB747269A7F3B25564FF7E14D78F3184EFABFDD2AB4319CE62841AF7F7993FEBE5E9B6361F3C470D9F792E9693F4B3238B19D8209453F16F2D52988A23DD29297160A4050631636892EADA41DDFBDD0D5177D738559D9D2CE0BE420479BADD28E1ED134C839648823C54AD695174FA5857D8137AD81
	BABE7550FBEB12FF5C6949168CAD52EBB82D5AE5B7B90D11AD78AB8276CF9BB4CB9BDD0A8DAA818F22BC64116BD1076CF22BD4EC7D90FF566BD1E5CB93BF677128522B6467140770A730074E1ABCFDAA462F752DD20E937F9186E9BB2D361B1C3D076821CFBD51B73369AAD1F5AB533708680BDCAA841722A7366D7D733954E559D2D63334492E18AC4DA6332A38DB240DEA1FDB7674A9F60B4FF53211B04AA737434889A7305BA53A0F1D2D6800C5A2146E774D0D5A123625B98BB6DC37F4083444D5D4A1DAD8CE
	77195CA0CAF63728B8C00A3BCC54CDFAD3FD7B5977F7F6E7C9192C131FC9DFC839BE17AA992CA6870CAA3C98490C70E04DC6957EF31B1C9E7C01A4E8D69A24992F9282AA6EFB3AF266A3AF7F0D8870132A52DB5414C0FA28D7D69C071A2CD6587D858330AB30FFC33047B695C1ED367B6EFD755C739BFAE09E9825ECC7E5A5797FE1727FA3787F4384F318E08E9F8163E6844F2D403F60FA9F6FB319BCE2D66370D7B443882A7F7E6B27677F28A1A39DFA57AC65129FB642A0947ADBDD74BAFAE4775445B35F58BC
	E3755DB8C6AE1CAABE643B05324FE44270B9931F821B4D9C8A4E2A0D821B23416688EC2EAE3608CDA272CFE72852AB6C76FAA93F3EDE87D37A024DD94A6F7A17621DF5C096A312DD5EAFE5976F81DD482E62EBD05BB8CBFA233E0E084E8ED992084D8B1D5B085E8CB30C7A9CACEDA5CD8B497F0BA6BCDEC875AA215DA94C7C365F4C86A6779DFC7F475374DD3F26F9CC6E26FFCCCD2BC3B545E7AEAC116D215BD95317FE784262AB4662ACA98BFCFF1FCC1DFD6EA01EAF9377093F762A89194A3D04A3E20F853479
	073244C9DF40F2C539EF07511B47F37FB4FDEB71850C896F5B66009EEFE74FCC749A2121G3808F758B1D029475BD3EA320267A9F54BA6F39F6C1E258E20D0DC25E67CF7C0823C21788BE348ACD6FD701F4110177C06C46F3D09354D55E8CA75AA980F2646DB20E9A3ABDF8EDC581B1846D9743E5E7E9B5ABB016BEEAF89FB2F503714657ECB7D7775687D0FABE7C8BE887854257CFB5BE346F7257077C85A9DA63BDDB5597B1B1C963CD3F2EAC815B5F74B4EA0FF8FEF43AAE9DD7F99547B1A0666FF81D0CB8788
	48CBB62CE29AGG34D0GGD0CB818294G94G88G88G9BF954AC48CBB62CE29AGG34D0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1C9AGGGG
**end of data**/
}
}
