package com.cannontech.loadcontrol.gui.manualentry;

/**
 * Insert the type's description here.
 * Creation date: (3/12/2001 9:57:47 AM)
 * @author: 
 */
public class DirectControlJPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("MMMMMMMM dd, yyyy");
	//modes the panel is in
	public static final int MODE_START_STOP = 0;
	public static final int MODE_STOP = 1;
	//choices the user may choose
	public static final int CANCEL_CHOICE = 0;
	public static final int OK_CHOICE = 1;
	private int choice = CANCEL_CHOICE;
	private int mode = MODE_START_STOP;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JLabel ivjJLabelStartTime = null;
	private javax.swing.JLabel ivjJLabelStopTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStartTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStopTime = null;
	private javax.swing.JCheckBox ivjJCheckBoxNeverStop = null;
	private javax.swing.JLabel ivjJLabelLabelStartHRMN = null;
	private javax.swing.JLabel ivjJLabelLabelStopHRMN = null;
	private com.klg.jclass.field.JCPopupField ivjJCPopUpFieldStartDate = null;
	private javax.swing.JPanel ivjJPanelOkCancel = null;
	private javax.swing.JCheckBox ivjJCheckBoxStartStopNow = null;
	private com.klg.jclass.field.JCPopupField ivjJCPopUpFieldStopDate = null;
	private javax.swing.JComboBox ivjJComboBoxGear = null;
	private javax.swing.JLabel ivjJLabelGear = null;
	private java.awt.FlowLayout ivjJPanelOkCancelFlowLayout = null;
	private javax.swing.JButton ivjJButtonMultipleSelect = null;
	private com.cannontech.common.gui.util.MultiSelectJPanel ivjJPanelMultiSelect = null;
	private javax.swing.JPanel ivjJPanelControls = null;
/**
 * ManualChangeJPanel constructor comment.
 */
public DirectControlJPanel() {
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
	if (e.getSource() == getJButtonCancel()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonOk()) 
		connEtoC2(e);
	if (e.getSource() == getJCheckBoxNeverStop()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxStartStopNow()) 
		connEtoC4(e);
	if (e.getSource() == getJButtonMultipleSelect()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCancel_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOK_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxNeverStop_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JCheckBoxStartStopNow.action.actionPerformed(java.awt.event.ActionEvent) --> DirectControlJPanel.jCheckBoxStartStopNow_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxStartStopNow_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JCheckBoxNeverStop.action.actionPerformed(java.awt.event.ActionEvent) --> ManualChangeJPanel.jCheckBoxNeverStop_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonMultipleSelect_ActionPerformed(arg1);
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
 * Creation date: (3/12/2001 3:40:34 PM)
 *
 * Method to override if desired 
 */
public void exit() 
{
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 3:43:40 PM)
 * @return int
 */
public int getChoice() {
	return choice;
}
/**
 * Insert the method's description here.
 * Creation date: (7/10/2001 10:48:08 AM)
 * @return java.text.SimpleDateFormat
 */
public java.text.SimpleDateFormat getDateFormatter() {
	return dateFormatter;
}
/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic(67);
			ivjJButtonCancel.setText("Cancel");
			ivjJButtonCancel.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjJButtonCancel.setActionCommand("Cancel");
			ivjJButtonCancel.setMinimumSize(new java.awt.Dimension(73, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}
/**
 * Return the JButtonStartMultiple property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonMultipleSelect() {
	if (ivjJButtonMultipleSelect == null) {
		try {
			ivjJButtonMultipleSelect = new javax.swing.JButton();
			ivjJButtonMultipleSelect.setName("JButtonMultipleSelect");
			ivjJButtonMultipleSelect.setMnemonic('m');
			ivjJButtonMultipleSelect.setText("Select Multiple>>");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonMultipleSelect;
}
/**
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic(79);
			ivjJButtonOk.setText("Ok");
			ivjJButtonOk.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setActionCommand("Ok");
			ivjJButtonOk.setMinimumSize(new java.awt.Dimension(73, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}
/**
 * Return the JCheckBoxNeverStop property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxNeverStop() {
	if (ivjJCheckBoxNeverStop == null) {
		try {
			ivjJCheckBoxNeverStop = new javax.swing.JCheckBox();
			ivjJCheckBoxNeverStop.setName("JCheckBoxNeverStop");
			ivjJCheckBoxNeverStop.setToolTipText("Forces the schedule to run forever");
			ivjJCheckBoxNeverStop.setMnemonic(78);
			ivjJCheckBoxNeverStop.setText("Never Stop");
			ivjJCheckBoxNeverStop.setMaximumSize(new java.awt.Dimension(87, 22));
			ivjJCheckBoxNeverStop.setActionCommand("Never Stop");
			ivjJCheckBoxNeverStop.setMinimumSize(new java.awt.Dimension(87, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxNeverStop;
}
/**
 * Return the JCheckBoxStartStopNow property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxStartStopNow() {
	if (ivjJCheckBoxStartStopNow == null) {
		try {
			ivjJCheckBoxStartStopNow = new javax.swing.JCheckBox();
			ivjJCheckBoxStartStopNow.setName("JCheckBoxStartStopNow");
			ivjJCheckBoxStartStopNow.setMnemonic(83);
			ivjJCheckBoxStartStopNow.setText("Start Now");
			ivjJCheckBoxStartStopNow.setMaximumSize(new java.awt.Dimension(81, 22));
			ivjJCheckBoxStartStopNow.setActionCommand("Start Now");
			ivjJCheckBoxStartStopNow.setMinimumSize(new java.awt.Dimension(81, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxStartStopNow;
}
/**
 * Return the JComboBoxGear property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGear() {
	if (ivjJComboBoxGear == null) {
		try {
			ivjJComboBoxGear = new javax.swing.JComboBox();
			ivjJComboBoxGear.setName("JComboBoxGear");
			ivjJComboBoxGear.setEditor(new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource());
			ivjJComboBoxGear.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGear;
}
/**
 * Return the JCPopUpFieldStartDate property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCPopupField getJCPopUpFieldStartDate() {
	if (ivjJCPopUpFieldStartDate == null) {
		try {
			ivjJCPopUpFieldStartDate = new com.klg.jclass.field.JCPopupField();
			ivjJCPopUpFieldStartDate.setName("JCPopUpFieldStartDate");
			// user code begin {1}

			com.klg.jclass.field.validate.JCDateValidator dv = new com.klg.jclass.field.validate.JCDateValidator();
			dv.setAllowNull(false);
			dv.setDefaultDetail(com.klg.jclass.field.validate.JCDateValidator.LONG);
			dv.setCasePolicy(com.klg.jclass.field.validate.JCDateValidator.UPPERCASE);
			dv.setMin( new java.util.Date() ); // right now is the minimum for the start date

			// create the invalidinfo and set its properties
			ivjJCPopUpFieldStartDate.getInvalidInfo().setInvalidPolicy(com.klg.jclass.field.JCInvalidInfo.RESTORE_PREVIOUS);
			ivjJCPopUpFieldStartDate.setValueModel( new com.klg.jclass.util.value.DateValueModel( new java.util.Date() ) );
			ivjJCPopUpFieldStartDate.setValidator( dv );
			

			java.util.GregorianCalendar c = new java.util.GregorianCalendar();
			c.setTime( new java.util.Date() );
			ivjJCPopUpFieldStartDate.setSelectedItem( getDateFormatter().format(c.getTime()) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCPopUpFieldStartDate;
}
/**
 * Return the JCPopUpFieldStopDate property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCPopupField getJCPopUpFieldStopDate() {
	if (ivjJCPopUpFieldStopDate == null) {
		try {
			ivjJCPopUpFieldStopDate = new com.klg.jclass.field.JCPopupField();
			ivjJCPopUpFieldStopDate.setName("JCPopUpFieldStopDate");
			// user code begin {1}


			com.klg.jclass.field.validate.JCDateValidator dv = new com.klg.jclass.field.validate.JCDateValidator();
			dv.setAllowNull(false);
			dv.setDefaultDetail(com.klg.jclass.field.validate.JCDateValidator.LONG);
			dv.setCasePolicy(com.klg.jclass.field.validate.JCDateValidator.UPPERCASE);
			dv.setMin( new java.util.Date() ); // right now is the minimum for the stop date

			// create the invalidinfo and set its properties
			ivjJCPopUpFieldStopDate.getInvalidInfo().setInvalidPolicy(com.klg.jclass.field.JCInvalidInfo.RESTORE_PREVIOUS);
			ivjJCPopUpFieldStopDate.setValueModel( new com.klg.jclass.util.value.DateValueModel( new java.util.Date() ) );
			ivjJCPopUpFieldStopDate.setValidator( dv );
			

			java.util.GregorianCalendar c = new java.util.GregorianCalendar();
			c.setTime( new java.util.Date() );
			c.set( c.HOUR_OF_DAY, c.get(c.HOUR_OF_DAY) + 4 );
			ivjJCPopUpFieldStopDate.setSelectedItem( getDateFormatter().format(c.getTime()) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCPopUpFieldStopDate;
}
/**
 * Return the JLabelGear property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGear() {
	if (ivjJLabelGear == null) {
		try {
			ivjJLabelGear = new javax.swing.JLabel();
			ivjJLabelGear.setName("JLabelGear");
			ivjJLabelGear.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGear.setText("Gear:");
			ivjJLabelGear.setMaximumSize(new java.awt.Dimension(36, 19));
			ivjJLabelGear.setMinimumSize(new java.awt.Dimension(36, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGear;
}
/**
 * Return the JLabelLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLabelStartHRMN() {
	if (ivjJLabelLabelStartHRMN == null) {
		try {
			ivjJLabelLabelStartHRMN = new javax.swing.JLabel();
			ivjJLabelLabelStartHRMN.setName("JLabelLabelStartHRMN");
			ivjJLabelLabelStartHRMN.setText("(HH:mm)");
			ivjJLabelLabelStartHRMN.setMaximumSize(new java.awt.Dimension(51, 16));
			ivjJLabelLabelStartHRMN.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelLabelStartHRMN.setEnabled(false);
			ivjJLabelLabelStartHRMN.setMinimumSize(new java.awt.Dimension(51, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLabelStartHRMN;
}
/**
 * Return the JLabelLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLabelStopHRMN() {
	if (ivjJLabelLabelStopHRMN == null) {
		try {
			ivjJLabelLabelStopHRMN = new javax.swing.JLabel();
			ivjJLabelLabelStopHRMN.setName("JLabelLabelStopHRMN");
			ivjJLabelLabelStopHRMN.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelLabelStopHRMN.setText("(HH:mm)");
			ivjJLabelLabelStopHRMN.setMaximumSize(new java.awt.Dimension(51, 16));
			ivjJLabelLabelStopHRMN.setMinimumSize(new java.awt.Dimension(51, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLabelStopHRMN;
}
/**
 * Return the JLabelTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartTime() {
	if (ivjJLabelStartTime == null) {
		try {
			ivjJLabelStartTime = new javax.swing.JLabel();
			ivjJLabelStartTime.setName("JLabelStartTime");
			ivjJLabelStartTime.setText("Start Time:");
			ivjJLabelStartTime.setMaximumSize(new java.awt.Dimension(69, 19));
			ivjJLabelStartTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStartTime.setEnabled(false);
			ivjJLabelStartTime.setMinimumSize(new java.awt.Dimension(69, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartTime;
}
/**
 * Return the JLabelStopTime property value.
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
			ivjJLabelStopTime.setMaximumSize(new java.awt.Dimension(68, 19));
			ivjJLabelStopTime.setMinimumSize(new java.awt.Dimension(68, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopTime;
}
/**
 * Return the JPanelControls property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelControls() {
	if (ivjJPanelControls == null) {
		try {
			ivjJPanelControls = new javax.swing.JPanel();
			ivjJPanelControls.setName("JPanelControls");
			ivjJPanelControls.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxStartStopNow = new java.awt.GridBagConstraints();
			constraintsJCheckBoxStartStopNow.gridx = 1; constraintsJCheckBoxStartStopNow.gridy = 1;
			constraintsJCheckBoxStartStopNow.gridwidth = 3;
			constraintsJCheckBoxStartStopNow.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxStartStopNow.ipadx = 16;
			constraintsJCheckBoxStartStopNow.insets = new java.awt.Insets(3, 3, 1, 87);
			getJPanelControls().add(getJCheckBoxStartStopNow(), constraintsJCheckBoxStartStopNow);

			java.awt.GridBagConstraints constraintsJLabelStartTime = new java.awt.GridBagConstraints();
			constraintsJLabelStartTime.gridx = 1; constraintsJLabelStartTime.gridy = 2;
			constraintsJLabelStartTime.gridwidth = 2;
			constraintsJLabelStartTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStartTime.ipadx = 17;
			constraintsJLabelStartTime.insets = new java.awt.Insets(2, 3, 2, 3);
			getJPanelControls().add(getJLabelStartTime(), constraintsJLabelStartTime);

			java.awt.GridBagConstraints constraintsJTextFieldStartTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldStartTime.gridx = 3; constraintsJTextFieldStartTime.gridy = 2;
			constraintsJTextFieldStartTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldStartTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldStartTime.weightx = 1.0;
			constraintsJTextFieldStartTime.ipadx = 86;
			constraintsJTextFieldStartTime.insets = new java.awt.Insets(2, 4, 1, 1);
			getJPanelControls().add(getJTextFieldStartTime(), constraintsJTextFieldStartTime);

			java.awt.GridBagConstraints constraintsJLabelLabelStartHRMN = new java.awt.GridBagConstraints();
			constraintsJLabelLabelStartHRMN.gridx = 4; constraintsJLabelLabelStartHRMN.gridy = 2;
			constraintsJLabelLabelStartHRMN.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelLabelStartHRMN.ipadx = 7;
			constraintsJLabelLabelStartHRMN.ipady = -2;
			constraintsJLabelLabelStartHRMN.insets = new java.awt.Insets(5, 2, 4, 37);
			getJPanelControls().add(getJLabelLabelStartHRMN(), constraintsJLabelLabelStartHRMN);

			java.awt.GridBagConstraints constraintsJCPopUpFieldStartDate = new java.awt.GridBagConstraints();
			constraintsJCPopUpFieldStartDate.gridx = 3; constraintsJCPopUpFieldStartDate.gridy = 3;
			constraintsJCPopUpFieldStartDate.gridwidth = 2;
			constraintsJCPopUpFieldStartDate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJCPopUpFieldStartDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCPopUpFieldStartDate.weightx = 1.0;
			constraintsJCPopUpFieldStartDate.ipadx = 37;
			constraintsJCPopUpFieldStartDate.insets = new java.awt.Insets(1, 4, 4, 33);
			getJPanelControls().add(getJCPopUpFieldStartDate(), constraintsJCPopUpFieldStartDate);

			java.awt.GridBagConstraints constraintsJCheckBoxNeverStop = new java.awt.GridBagConstraints();
			constraintsJCheckBoxNeverStop.gridx = 1; constraintsJCheckBoxNeverStop.gridy = 4;
			constraintsJCheckBoxNeverStop.gridwidth = 2;
			constraintsJCheckBoxNeverStop.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxNeverStop.ipadx = -1;
			constraintsJCheckBoxNeverStop.insets = new java.awt.Insets(4, 3, 0, 3);
			getJPanelControls().add(getJCheckBoxNeverStop(), constraintsJCheckBoxNeverStop);

			java.awt.GridBagConstraints constraintsJTextFieldStopTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldStopTime.gridx = 3; constraintsJTextFieldStopTime.gridy = 5;
			constraintsJTextFieldStopTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldStopTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldStopTime.weightx = 1.0;
			constraintsJTextFieldStopTime.ipadx = 86;
			constraintsJTextFieldStopTime.insets = new java.awt.Insets(0, 4, 1, 1);
			getJPanelControls().add(getJTextFieldStopTime(), constraintsJTextFieldStopTime);

			java.awt.GridBagConstraints constraintsJLabelStopTime = new java.awt.GridBagConstraints();
			constraintsJLabelStopTime.gridx = 1; constraintsJLabelStopTime.gridy = 5;
			constraintsJLabelStopTime.gridwidth = 2;
			constraintsJLabelStopTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStopTime.ipadx = 18;
			constraintsJLabelStopTime.insets = new java.awt.Insets(1, 3, 1, 3);
			getJPanelControls().add(getJLabelStopTime(), constraintsJLabelStopTime);

			java.awt.GridBagConstraints constraintsJLabelLabelStopHRMN = new java.awt.GridBagConstraints();
			constraintsJLabelLabelStopHRMN.gridx = 4; constraintsJLabelLabelStopHRMN.gridy = 5;
			constraintsJLabelLabelStopHRMN.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelLabelStopHRMN.ipadx = 7;
			constraintsJLabelLabelStopHRMN.ipady = -2;
			constraintsJLabelLabelStopHRMN.insets = new java.awt.Insets(3, 2, 4, 37);
			getJPanelControls().add(getJLabelLabelStopHRMN(), constraintsJLabelLabelStopHRMN);

			java.awt.GridBagConstraints constraintsJCPopUpFieldStopDate = new java.awt.GridBagConstraints();
			constraintsJCPopUpFieldStopDate.gridx = 3; constraintsJCPopUpFieldStopDate.gridy = 6;
			constraintsJCPopUpFieldStopDate.gridwidth = 2;
			constraintsJCPopUpFieldStopDate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJCPopUpFieldStopDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCPopUpFieldStopDate.weightx = 1.0;
			constraintsJCPopUpFieldStopDate.ipadx = 37;
			constraintsJCPopUpFieldStopDate.insets = new java.awt.Insets(1, 4, 3, 33);
			getJPanelControls().add(getJCPopUpFieldStopDate(), constraintsJCPopUpFieldStopDate);

			java.awt.GridBagConstraints constraintsJComboBoxGear = new java.awt.GridBagConstraints();
			constraintsJComboBoxGear.gridx = 2; constraintsJComboBoxGear.gridy = 7;
			constraintsJComboBoxGear.gridwidth = 3;
			constraintsJComboBoxGear.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxGear.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxGear.weightx = 1.0;
			constraintsJComboBoxGear.ipadx = 74;
			constraintsJComboBoxGear.insets = new java.awt.Insets(4, 2, 3, 33);
			getJPanelControls().add(getJComboBoxGear(), constraintsJComboBoxGear);

			java.awt.GridBagConstraints constraintsJLabelGear = new java.awt.GridBagConstraints();
			constraintsJLabelGear.gridx = 1; constraintsJLabelGear.gridy = 7;
			constraintsJLabelGear.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGear.ipadx = 9;
			constraintsJLabelGear.ipady = -5;
			constraintsJLabelGear.insets = new java.awt.Insets(7, 3, 9, 1);
			getJPanelControls().add(getJLabelGear(), constraintsJLabelGear);

			java.awt.GridBagConstraints constraintsJPanelOkCancel = new java.awt.GridBagConstraints();
			constraintsJPanelOkCancel.gridx = 1; constraintsJPanelOkCancel.gridy = 8;
			constraintsJPanelOkCancel.gridwidth = 4;
			constraintsJPanelOkCancel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelOkCancel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelOkCancel.weightx = 1.0;
			constraintsJPanelOkCancel.weighty = 1.0;
			constraintsJPanelOkCancel.ipadx = 116;
			constraintsJPanelOkCancel.ipady = 1;
			constraintsJPanelOkCancel.insets = new java.awt.Insets(4, 3, 7, 4);
			getJPanelControls().add(getJPanelOkCancel(), constraintsJPanelOkCancel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelControls;
}
/**
 * Return the JPanelMultiSelect property value.
 * @return com.cannontech.common.gui.util.MultiSelectJPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.MultiSelectJPanel getJPanelMultiSelect() {
	if (ivjJPanelMultiSelect == null) {
		try {
			ivjJPanelMultiSelect = new com.cannontech.common.gui.util.MultiSelectJPanel();
			ivjJPanelMultiSelect.setName("JPanelMultiSelect");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelMultiSelect;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOkCancel() {
	if (ivjJPanelOkCancel == null) {
		try {
			ivjJPanelOkCancel = new javax.swing.JPanel();
			ivjJPanelOkCancel.setName("JPanelOkCancel");
			ivjJPanelOkCancel.setLayout(getJPanelOkCancelFlowLayout());
			ivjJPanelOkCancel.setMinimumSize(new java.awt.Dimension(161, 35));
			getJPanelOkCancel().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelOkCancel().add(getJButtonCancel(), getJButtonCancel().getName());
			ivjJPanelOkCancel.add(getJButtonMultipleSelect());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOkCancel;
}
/**
 * Return the JPanelOkCancelFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelOkCancelFlowLayout() {
	java.awt.FlowLayout ivjJPanelOkCancelFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelOkCancelFlowLayout = new java.awt.FlowLayout();
		ivjJPanelOkCancelFlowLayout.setAlignment(java.awt.FlowLayout.CENTER);
		ivjJPanelOkCancelFlowLayout.setVgap(5);
		ivjJPanelOkCancelFlowLayout.setHgap(5);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelOkCancelFlowLayout;
}
/**
 * Return the JTextFieldStartTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStartTime() {
	if (ivjJTextFieldStartTime == null) {
		try {
			ivjJTextFieldStartTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStartTime.setName("JTextFieldStartTime");
			ivjJTextFieldStartTime.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			ivjJTextFieldStartTime.setText("16:25");
			ivjJTextFieldStartTime.setCaretPosition(5);
			ivjJTextFieldStartTime.setSelectionEnd(5);
			ivjJTextFieldStartTime.setEnabled(false);
			ivjJTextFieldStartTime.setSelectionStart(5);
			// user code begin {1}

			ivjJTextFieldStartTime.setTimeText( new java.util.Date() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStartTime;
}
/**
 * Return the JTextFieldStopTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStopTime() {
	if (ivjJTextFieldStopTime == null) {
		try {
			ivjJTextFieldStopTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStopTime.setName("JTextFieldStopTime");
			ivjJTextFieldStopTime.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			ivjJTextFieldStopTime.setText("20:25");
			// user code begin {1}

			if( getMode() == MODE_STOP )
				ivjJTextFieldStopTime.setTimeText( new java.util.Date() );
			else
			{
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				cal.setTime( new java.util.Date() );

				StringBuffer hour = new StringBuffer( String.valueOf(cal.get( java.util.GregorianCalendar.HOUR_OF_DAY)+4) );
				if( hour.length() < 2 )
					hour.insert(0, "0" );
					
				StringBuffer minute = new StringBuffer( String.valueOf(cal.get(java.util.GregorianCalendar.MINUTE)) );
				if( minute.length() < 2 )
					minute.insert(0, "0" );
					
				if( cal.get( java.util.GregorianCalendar.HOUR_OF_DAY) > 20 )
					hour = new StringBuffer("23");
					
				ivjJTextFieldStopTime.setText( hour + ":" + minute );
			}
		
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 5:13:45 PM)
 * @return int
 */
public int getMode() {
	return mode;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2001 5:32:52 PM)
 * @param newLoadControlProgram LMProgramBase
 */
public Object[] getMultiSelectObject()
{
	if( getJPanelMultiSelect().isVisible() )
		return getJPanelMultiSelect().getSelectedData();
	else
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/2001 9:18:54 AM)
 * @return int
 */
public com.cannontech.loadcontrol.data.LMProgramDirectGear getSelectedGear() 
{
	if( getJComboBoxGear().isEnabled() 
		 && getJComboBoxGear().getSelectedIndex() >= 0 )
	{
		return (com.cannontech.loadcontrol.data.LMProgramDirectGear)getJComboBoxGear().getSelectedItem();
	}
	else
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 2:56:28 PM)
 * @return java.util.Date
 */
public java.util.Date getStartTime()
{
	if( getJTextFieldStartTime().getText() == null
		 || getJTextFieldStartTime().getText().length() <= 0 )
	{
		return com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime();
	}
	else
	{
		java.util.GregorianCalendar c = new java.util.GregorianCalendar();
		if( getJCPopUpFieldStartDate().getValue() instanceof java.util.GregorianCalendar )
			c = (java.util.GregorianCalendar)getJCPopUpFieldStartDate().getValue();
		else
			c.setTime( (java.util.Date)getJCPopUpFieldStartDate().getValue() );

		
		String start = getJTextFieldStartTime().getTimeText();
		
		try
		{
			c.set(java.util.GregorianCalendar.HOUR_OF_DAY, Integer.parseInt( start.substring(0,2) ) );
			c.set(java.util.GregorianCalendar.MINUTE, Integer.parseInt( start.substring(3,5) ) );
			c.set(java.util.GregorianCalendar.SECOND, 0 );
			return c.getTime();
		}
		catch( Exception e )
		{
			System.out.println("*** Received a bad value in getStartTime() of " + this.getClass().getName() + " : " + e.getMessage() );
			return com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime();
		}
		
	}

}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 2:56:28 PM)
 * @return java.util.Date
 */
public java.util.Date getStopTime()
{
	if( getJCheckBoxNeverStop().isSelected() )
	{
		java.util.GregorianCalendar c = new java.util.GregorianCalendar();
		c.set( c.YEAR, c.get(c.YEAR) + 1 ); //set the stop time to 1 year from now
		return c.getTime();
	}
	else if(	 getJTextFieldStopTime().getText() == null
				 || getJTextFieldStopTime().getText().length() <= 0 )
	{
		return com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime();
	}
	else
	{
		java.util.GregorianCalendar c = new java.util.GregorianCalendar();
		if( getJCPopUpFieldStopDate().getValue() instanceof java.util.GregorianCalendar )
			c = (java.util.GregorianCalendar)getJCPopUpFieldStopDate().getValue();
		else
			c.setTime( (java.util.Date)getJCPopUpFieldStopDate().getValue() );


		String stop = getJTextFieldStopTime().getTimeText();

		try
		{
			c.set(java.util.GregorianCalendar.HOUR_OF_DAY, Integer.parseInt( stop.substring(0,2) ) );
			c.set(java.util.GregorianCalendar.MINUTE, Integer.parseInt( stop.substring(3,5) ) );
			c.set(java.util.GregorianCalendar.SECOND, 0 );
			return c.getTime();
		}
		catch( Exception e )
		{
			System.out.println("*** Received a bad value in getStopTime() of " + this.getClass().getName() + " : " + e.getMessage() );
			return com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime();
		}
		
	}
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
	// user code end
	getJButtonCancel().addActionListener(this);
	getJButtonOk().addActionListener(this);
	getJCheckBoxNeverStop().addActionListener(this);
	getJCheckBoxStartStopNow().addActionListener(this);
	getJButtonMultipleSelect().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DirectControlJPanel");
		setBounds(new java.awt.Rectangle(0, 0, 300, 234));
		setLayout(new java.awt.GridBagLayout());
		setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
		setMinimumSize(new java.awt.Dimension(315, 260));
		setSize(525, 234);

		java.awt.GridBagConstraints constraintsJPanelControls = new java.awt.GridBagConstraints();
		constraintsJPanelControls.gridx = 1; constraintsJPanelControls.gridy = 1;
		constraintsJPanelControls.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelControls.weightx = 1.0;
		constraintsJPanelControls.weighty = 1.0;
		constraintsJPanelControls.insets = new java.awt.Insets(1, 2, 5, 1);
		add(getJPanelControls(), constraintsJPanelControls);

		java.awt.GridBagConstraints constraintsJPanelMultiSelect = new java.awt.GridBagConstraints();
		constraintsJPanelMultiSelect.gridx = 2; constraintsJPanelMultiSelect.gridy = 1;
		constraintsJPanelMultiSelect.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelMultiSelect.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelMultiSelect.weightx = 1.0;
		constraintsJPanelMultiSelect.weighty = 1.0;
		constraintsJPanelMultiSelect.ipadx = -62;
		constraintsJPanelMultiSelect.ipady = -49;
		constraintsJPanelMultiSelect.insets = new java.awt.Insets(6, 1, 13, 12);
		add(getJPanelMultiSelect(), constraintsJPanelMultiSelect);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	getJPanelMultiSelect().setVisible(false);
	getJButtonOk().requestFocus();

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 10:18:44 AM)
 * @return boolean
 */
private boolean isInputValid() 
{
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 12:46:05 PM)
 * @return boolean
 */
public boolean isStopStartNowSelected() 
{
	return getJCheckBoxStartStopNow().isSelected();
}
/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	choice = CANCEL_CHOICE;
	exit();
	
	return;
}
/**
 * Comment
 */
public void jButtonMultipleSelect_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJButtonMultipleSelect().getText().equals("Select Multiple>>") )
	{
		getJButtonMultipleSelect().setText("Select Single<<");
		setParentWidth( 225 ); //300, 250
		getJPanelMultiSelect().setVisible(true);
		getJComboBoxGear().setEnabled(false);
	}
	else
	{
		getJButtonMultipleSelect().setText("Select Multiple>>");
		setParentWidth( -225 );
		getJPanelMultiSelect().setVisible(false);
		getJComboBoxGear().setEnabled(true);
	}

	return;
}
/**
 * Comment
 */
public void jButtonOK_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getMode() == MODE_START_STOP 
	 	 && getStartTime() != null
	 	 && getStopTime() != null )
	{
		if( getStartTime().after(com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime())
			 && getStopTime().after(com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime()) )
		{
			if( getStartTime().getTime() >= getStopTime().getTime() )
			{
				javax.swing.JOptionPane.showConfirmDialog( this, "Start time can not be greater than the stop time, try again.", 
							"Incorrect Entry", 
							javax.swing.JOptionPane.CLOSED_OPTION,							
							javax.swing.JOptionPane.WARNING_MESSAGE );
				return;
			}
		}

	}
	else if( getMode() == MODE_STOP
				&& getStopTime() != null )
	{
		if( getStartTime().after(com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime())
			 && getStopTime().after(com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime()) )
		{
			java.util.Date cDate = new java.util.Date();
			
			if( getStopTime().before(cDate) )
			{
				javax.swing.JOptionPane.showConfirmDialog( this, "Stop time can not be less than the current time, try again.", 
							"Incorrect Entry", 
							javax.swing.JOptionPane.CLOSED_OPTION, 
							javax.swing.JOptionPane.WARNING_MESSAGE );
				return;
			}
		}
	}

	
	choice = OK_CHOICE;
	exit();

	return;
}
/**
 * Comment
 */
public void jCheckBoxNeverStop_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJLabelStopTime().setEnabled( !getJCheckBoxNeverStop().isSelected() );
	getJTextFieldStopTime().setEnabled( !getJCheckBoxNeverStop().isSelected() );
	getJLabelLabelStopHRMN().setEnabled( !getJCheckBoxNeverStop().isSelected() );
	getJCPopUpFieldStopDate().setEnabled( !getJCheckBoxNeverStop().isSelected() );

	if( getJCheckBoxNeverStop().isSelected() )
		getJButtonOk().setEnabled( true );
	
	return;
}
/**
 * Comment
 */
public void jCheckBoxStartStopNow_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getMode() == MODE_STOP )
	{
		getJLabelStopTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getJTextFieldStopTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getJLabelLabelStopHRMN().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getJCPopUpFieldStopDate().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
	}
	else if( getMode() == MODE_START_STOP )
	{
		getJLabelStartTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getJTextFieldStartTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getJLabelLabelStartHRMN().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getJCPopUpFieldStartDate().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
	}


	if( getJCheckBoxStartStopNow().isSelected() )
		getJButtonOk().setEnabled( true );

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DirectControlJPanel aDirectControlJPanel;
		aDirectControlJPanel = new DirectControlJPanel();
		frame.setContentPane(aDirectControlJPanel);
		frame.setSize(aDirectControlJPanel.getSize());
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
/**
 * Insert the method's description here.
 * Creation date: (7/17/2001 9:24:14 AM)
 * @param gears java.util.Vector
 */
public void setGears(java.util.Vector gears) 
{
	getJComboBoxGear().removeAllItems();
	
	if( gears != null )
	{
		for( int i = 0; i < gears.size(); i++ )
		{
			getJComboBoxGear().addItem( gears.get(i) );
		}

		if( getJComboBoxGear().getItemCount() > 0 )
			getJComboBoxGear().setSelectedIndex(0);
	}

}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 5:13:45 PM)
 * @param newMode int
 */
public void setMode(int newMode) 
{
	mode = newMode;
	getJCheckBoxStartStopNow().doClick();

	if( mode == MODE_STOP )
	{
		getJLabelStartTime().setVisible(false);
		getJTextFieldStartTime().setVisible(false);
		getJLabelLabelStartHRMN().setVisible(false);
		getJCPopUpFieldStartDate().setVisible(false);
		
		getJCheckBoxNeverStop().setVisible(false);
		getJCheckBoxStartStopNow().setText("Stop Now");
	}

}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2001 5:32:52 PM)
 * @param newLoadControlProgram LMProgramBase
 */
public void setMultiSelectObject( Object[] rows ) 
{
	getJPanelMultiSelect().setSelectableData( rows );
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 3:40:34 PM)
 *
 * Method to override if desired 
 */
public void setParentWidth( int x )
{
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8FFCD44535B056A814C60D9AACC20400D1C103D20D92A5D6CC53FEE871957B6013D7737AE19F2D3C16D65EFB7495EDACB7FF8992A0C050D0820490A1E89A9272EF33C485327C892C901290E3CBADCB76A65964E6F7593D1BECC200F7E6661E39F7B7F7B701CA3F171F07FB674C19B9E74E1CB9F3664FDD051C1D116949A9B902101CAEA8FF554902B026D3903ABF78FE8F62C60971990266EF9740C6
	2126B18D1EE110D77ACAFCC636F0F2E3A62423A0DD14901F71813CEF966AAA3CDF41CBGBE8D10A7EFDE7FDEF0BE26ED48E7B5631B1D854F75G53GC71EABC4427FECF62E021F27609112D384E1C9BB21113273941C81528DG11GB16D0CFE9EBC57D04E37998D8A5D793BA30595AF6FCDD9C76A515409D05C414AB6A84FFC610964159D0457B8AAA7A9B38652F3GC863D385E731891E358D5D9D1F36B9FACA5AACF63B43AE0BED1DA51243E2ED03F717C3AA697058CAFAACF60FC5928151EFB41AECAE31CD2E
	E359A79BACF6D112BB891FFF4D4E2475C21D1096EC8477038A42BB9652738116F27C9D1584EF027773G328D5B7B495EDA25BD2D171F90E2DE3327EE88505ED09BEBE70CCDEDEF38671D55DBE95F046411BED88EF9B9DDE087GE6G89A094E09B3509EFED7B881E07AFC2DBFAFA9C7643CE6791496A7A5CE195A5F8B79AA1C741B50AED8E17D590D83FCF4CAE5611A79830F2532F3B9C534934F23401684607054427CE252C5651C96234521428AE260B38AED5A70C774F8272CE6C7235D1DF5EDF88DA5ED137BB
	D253F5F847AC5C1732DC29A72DDB65DD51CD7A5AC87B3A905E13957BE0F89345FBB5F8E62F73284D10749848DB560D36513A9EE5B127CD90324B4C3AFA989FFFB825C1E2B2F4CA7E32A4D7113A43A546F3AD17E5834547E870CC16FB290CA43DG728CBD71994C3E466FA063B7825231G73811682E45A63B38A818CF63431855B760F50469AACAE790847A6D9C517404A5E1177BEBCC52F6C32545B5D32455EA636D8DCB64BC5C9F48B8A4DFDF9C48797FD4ACD24365F86BC0E0BF651E511ED8EBB54EE6991E551
	65E6B4D1F99B890DCB24394D369E919874B8852CB7BAEFB3BCA50BDBBE6734DAE4D125E079B1F924CFFAADADD0A3D4G6FCCAF5F5FC974558A6DF7820C62FA583F1D54F7CAF40137B09A4D8EC7374759CCDAA24C3793BD3FCDFDC7B83CC75BD147AF6693DC0EA3BE2358017534EF5F242947E4EBA362DBDC7DB8468ED5929FFD4801B6731BD63499533A07046205A7F5ED0640858733952F43FF6C1C24F59239D87A47952DC1466CA1077EF83ABEDF712B20BCD27F0E7A57733E5ADFC07AAA92640D83C864BA1E32
	BD1F596C910BDBC4ABBD638283932D2A46191C2B2BF294FD92195295795770F6EC2179AB206EBCE7FC0689C08288F722AE6F2FEC8DD07E6FE91F2FAE1F24BC7C6BDF7BB571E0BA1B3A034CD3E2AF4C11A651A95A2D7062D672266FA07C14F15C6E60E51E20F8A70C7BBA98E8EE0EFFFA87991B96D9F659AEFAE431C914E51B3DC34DFF1E72F2FBAEBAD517013C92F610B6BA7AE05CF37A17F610B1837339DD241D470BA879BF24E3D614449E903A4E61314B8C7F6ACEE2775DE23F62B3966EA4C1D12FC572907F43
	E208199622C7274B5683A6C1DC1810F5090C676C0D399A9D45C1FF13D8E8B1BCD7B8F1EC766D4631D9B8B0C51873378FD3746616718F19D21AAFA97E7C12BAB619CCBF485717FD9E759D8A5E4DE4ED5EC4FC10CE78220C074E5D242EB140A382A066920E07933BC8BE7A9CE5A054B9A40929136B3932A01B740BF849A3022BA64E6D9457DDED41A63F3C96C50FAC5FD4C074A8E97CE2280B4632D598A74E83F916G243918AECDAE268BC6373E9A6996C1FA39CB254BF6EB6962B9DDB360A537CA976AC357EBC03A
	C4402F50506548DA3A0FEA104E8A78C100881951A5AAF4CC777F350F58830DB53BAC3D96EF093B8FEC5AE854460F83B4EEA7FAA876E01C68FC1F7409955203G619E341DD757E11C985E9DA9ACFE700F816362E88F13E33EC7351D7CDBB1CEFC3210704C6E0D4F486F457EFE21C0BBD79DF63B9DEDB6BACDAAFDE55F2D35AD06F36FA60B07F61B4BAD57436077E23F766FA67212F9D4C1ABF1051B469BC4B79540D782C86BA378933DB99A5923GBF8FE0B9C0AA2D6FA7D58708EF2006D7429E8D222B5D616A912D
	C5C433A516BE39047A211243B47BA8F95F077D50E5A3FA6BBA6211E5073D0E0494525F8E7BD5E38EDC4F41FDADCC7FD3B79FC23BFCFC02D0FBEDF30ADE1CBEE6437694F39F533B5C276AFF70490C54E5DF7DFEB553C5F5B56AE28E502C6CC3DD5C262B0BD2AF2EDF4E1B8CEADB4E1C3A4EF6B05E3F623CCDD06F2097F9CBDEAD6F9840A782887D487B07178E935EF51DE2DB77910777B4F0F0B5498E67F58A416DE132816548819E66FE14E3DE131E9CA1DC0EFFBAEE524A51A4038BA6F21CF6745D10BE1AEAD00E
	7140E3BE17235E2357971983E8174F557CFDF6F94FD6135A171FFBA4596614E066A3AE76BA5B41667CEB9B356B1DBC1033F4C00D4BCC705E8AA0E9F0EC9D30047A1BD183EC9D104E71EFD1FC38864F74557BBE6AEB9A64E5DD465875709E626F1C108E8508820883C884C83A0CFEA63930DC98E6EDD08F6BF1175DA2B579CC760AEF19D5C8F48F7333121EDDC8642073330031CFFF157F3AAFF750B7BE2D70CB33F22307145B6117EE75AB477AF1C393E93F0E1FE7BD8BF9ACBEC8396C9F9F903BF6C21DA1GECBE
	3856CB7CAC716B910368772F9C537A7D388D87D2566A781D7514D471B7D7D43FD34C7787FE35174CDB97A0EF90A09CE0A640820065GB9D7310F0EFE7849F0FDC48D2F59D1873893B0FF577947D9DC271FD45FD8DF1C9AD24E3FAFAA2E7A16E37A0B637AB3DF45F87BBFEED006D5434AB0732A1EBDFC5A32CD5F9E50E3A976307783FFFBA06B2D39D0E762D53407873D24BDE41ED9415B731061465A735410F2C36C7B1A1ECED31BD1274EEBA84317B5B7A6C34710F27EB2AC5015E114A7D3516FB248E76B677BE8
	6CD4B8AABE43B08AF1A927884E8C698B9C770A1960C2A09D41F18F54925CCCC84F6538CF3690DCA224D70C42F67E72FD024B3DA5BE236296447D16624858ED66384177F16DCAE8E97BEF797B5644C3F5B70E734035F07A57D4BF81B676CF6E893C26BB3BC7FFCD77459E7DB55DDF77ECE4F828C33B530379621E60EB3E1EBDFAEB3E6D347DAF3D3F01566DF60BEE6A0CCC965922643DA9EF91F456FE27ADBD22223B4D424C2AB58A4DD159D1D7466D4CF8B9D3513777960C4767F948FCB79352F3819673BE8F6975
	5F2FD889F9D95FD0FC2F722C784670F6F082E84E7B51B11F6D5AC7E290A7640582C4814481A4FE83FDF56A5EE16753235EA07369EF77866E6F243D7A7D1DD2C4FC015B61F135097EFD3A26A817672933C23DAC769075B31ACD34BCC4ED9D62901B58CA4B333CC0658B0A82598D6BDB39DA5B37877878F9F1C8FFA5019E33EFE5FAAFD41E06DB076FAFB350DC7023E33C0B8CDA5E655C2E7E76817AAF79D6342BC79A09DDC5C3BA81A071D6342BE9DEA2678ACD7D19A17199F9A1ACDD2ABCCDA1434B598AB4D2081E
	1C09BEF2D6F09DE5B67B6BE894148FD76A1826BC63C640FBBE50AC51659DD62365DD49F95F66711F9BD6C2792C5B8273E2754D58C876E8AEBABCF6AB9841055B08EE93685C108F6F4537E13C3E75942113AC7D8E0F8C6B5F5B895D1D27F08E0E823AB800A5GAB951E2C5C92B30937FAACDEDB0F272749B6A0AABC12683C528A75D8EFC79EB1351456E677231DC7671BC8200B3E9D57B15F69A3346DAE8B10F7B4B945B6C19ACDE81B3720CC0B00F699C096600B814C2311CFE78A49E7BB894A3E0E1B77ED72C61E
	C71795ECCDA1E09CFA3ED79BF7440CE6F147D2F8A60DC69B7C6C32FFDF640CD17B21885EEB47784D77F44DF0281758F4439836E6D84D71E60A6F546059DA624754371274A848DBB286676207E99CF7621B60973F0938DFD21C935223B82E3F8F6776F00E1BD6C1F053A01D40F12BAAF0EEDF44F1BBABB186C87AA67A5301FD017D61AD7B747D6168FD7A735F9D7B024FFF63768572E3CC3EFF63BA489E0BB26F6DC39D94F25C53DC87860EF3F19D18B9EEAE5741850EDBDA01BA88990BFDEF7D403F6F470FD57BBA
	FA6C5071487A585DC47AF24ED856475BB87E1B94AFFCCB45333E1FC17D9FC99F023C3CD01469E59A732E04F4769D08EB6FC7DD94F25C237D288B83479987D097E60E3BDC053A3840F17FDC053A10B9EE0A97F51140F19FB5A35FC59C77AF67106FB20E7B3485792E64B8E9B3724D3A93F129B948B71F635E7C9479BAB9AE02463BA1109E4FF1BF2738E8C847814C3D131C57BFD6AAF0BF755B13FB60E9977B7279E6C1A30CFB0B3DC3920BCA0B73613F72D27867C0F945BEBC6B0C6C531E359B5E2B565D0FA610F5
	179257A84F38513B15FEFC66941E3537C2DE58DD685F4AAE1075A6DF8791010C4663AE1B75082543CC3DB01B9B7FC0E3F6D2FE6EDDB8B7961B294D0445E76482140661DA583B875742518FCF00B87F146E9E68626683A9A742181CD2183A961E86EBE1263768832A5E6E2CAD71571B4956A35A49A1DF5101320A6A8AC3F9D5C5B5A80E3F037ED8BDEF162B759C58129F381E7232CA28202AF41F326F3B855BBC8D64DB9CC67D56966DFE9329F3A1909059D87ECFFC72096FGB1B479D96FEB73093E48658DB57FD3
	1F7CF4CDF9265B9FFE083ACD7A61B4C1FA3AA6607EF2515DCC27E665595032DBA89C7B7B552C5F0F9FA99234F7ACE49B3DD06111ED1251281910347B60613DF80FA264EE34014D74CCBC8E528BG165D0D632065B22E71526E613E608A0E2BA20EFB0C7BB66FBD685B0E51F9A89452D1GF3B83E1162499982B9EFA0FB7A8C3F48936CCC529FC83D017266E8722214F7E5FF623AF884BB87DD6D573FEA8CF3187A319E3B880B84DB9B0BC9FEC4F5BE9AE049BD98D7ACBFC74E3FE41864984DE6BA372C00FC61DE70
	1F94B71562F2A01DFFAF163BF5B331953689CA16A2963BD540D86C558A2C43CC6A3817605E2F4C8D528ED66E841D1F2781FD8C671DC5F9CFA6B64DF18D94379C52C99C77D30A4B8A877F910E380995C8E78AC7B97EDF4570F2EC24F2B413B261FCCE2171FCA824A3B9EEE9AE5AE0C2B85ACFFF93092DF346419C85508CA08304834482AC82D88910F3DFFCC685C0B30093E04CFDD87EAEDA1E1CC5C581AE81E089C05A3759B9D6A1BCCDGD6009000A80004EF33FDEAE54F27F13ECE3BD6AAB2A0DF02A3FC09BC64
	5DB0EEE4FB97B99A3A66E14A44EA72475C971C1668C18ED21F32B7235BB6629757D21B9404AD8A0E2C25B7780D0B6B99E72B0721DD27F95FB68CAD391B976C2C1F0D27B73C248D3243A1B55B1C4DB09C95DBFC1B46A8CB200F17FF9B6D7AAE9A87BDG10BF9EF17FCCF125102E6538B69A9BB513B447C5D5A1DDA8C042F89AE7D7190611EF0D00FB1D604B208C015D821BABE261FD957CA573714362392C7BE1AC5E0FFCDB29BFFA8440F4BF46B2E749D2D0A953C16329E6C24F4BC450F8AA9452636FC7D97F18B3
	1C2C29CA7F6949BA936A59253CB39E591F919EF1C4267B5137BD65996ABBDF26385781D6F03AA48F7ACE569E39CA90F0EE7D51DF34F1D67604DDA949027E5C1AB60159FD7684B5EEE1FBC16D550427897097A660FAE5F9694770EC31F4A9BBBFC72DB6596122DBBF00ECF0B91C22CB6EE7D8FA7A2E0C1D38D3B7F61F6F659BAC17EA3E5EF2EC6D90C96340D193585AA1196327D3FC28864FE25183E60CC547C3DE5284BC1B7A360178798B93E1EE8788870886188730F4A26A32260C8453C177521A9DCEB3AC8EA5
	4DF2CEB9FB5BF2E3779CDFDC0DB1D74CF3D305585B5FCE8EF43613B41159C3DE84FBAEED5E4DFD5BC661A3A3E91F9972BA81C28122GE28166C7E07B0AAB0AD97B08B99C16E5CBDBA7BD4D4746355844BE95CDB6A4147BCA3D415A36A4A250FAF0B40D5FD6C67877DD984527BD605FF7DBAB306F32A1CF86E0FBB457783959A240AD87481D941FD1BC094DF34C8F3CF8E5B8BF30D151B9F9B7C15956C94C8F1034846F7F2E3C33713D00468ADE4007CC42714D4AAB27FAE73A8574554C7F1F545EAF60F91F2F5922
	66791E71F21ADF742640332A9A57945FBBA870BC1946B1852C5682167F7BDAB57F018AF2FE92E04DD1DD8ECB0E4AFDC87BCE0536AEE6FFF37F2A0D7973BFCE530D79D77EF5FF4A4CC92C1F97CCD27D52022EA4E5BD557629AAD331E764B26D6EA3ED2F2E4136C7FC2656731DBE62CF8B4EF4AB6DFE61101A57F19D6DCE28702D67E70755FABE6E9DF9BD77FCB0F47FF0BABF434B1C4C768F7361D9B119690859662A5C114CD188E6A8FBE132BAC7491355F10276C6EDCA6F7E03060644BBA74D160B22C48948E5D6
	1EC760A409AA0E59C449BAB4BF0717D5ABB8517879E9DF7E8D8E67B927DA0349A20B9A7E1B34B2723BA2FE799A999CCEAA824FDF2F115FBFEF333FFC8EA7914FD7FE9F799CCEA21E4FF885AFFF5181329D97ADAE0D5C6F713AD9060FBCF445FA269B87B93BA752B720DDD70F1A4C4E7523A663BD12577A347931CA7ECA1E7F26C12FFCD6A466CF2C526667C7327CE61E7FCE25DE7D1DBC7F7FFA756A9F64798BE53D7CB01E5FBB2047BF0A673F65A3FF2C123F1867E7DE562BFFC5A446FDE36B547178677EC2B2D6
	0FB9DCED22BBDF6E94735DED1D2255A3097932A35F65316737BBDC44188A987D437D54FF9850FF4CB029F5D58C107924005ADEBEB181E57C4FBF2A52B82B020CFF835973B940477FABD57E756DD4EC0378D57CE3BC2A0F13F766E35F48C29F1B7758C441745A5B49B8367DFDC1669466B7DA2728BEF6A97731D3CE28F27FEB654865DEF80E6848D00E32A5542B75D8B6872B27CC29C7596BBA2916FBF7B309D38A68404F8747D320503CE7D6F570274FB57DBAB092BE4A5A47C31EFBFB76AA314DCFE8BDAC06092D
	7AD088906FC284F3926ABEF3318BA657E6CB87AF53DDCDF4497759D9982C64392A09ED5A6CB659E611EC83744AE393905ABBD86C73B0ED8FFBFFC273EEB0DF4F1E4D758263F1762672E06D0FB484BE6FF84420FF5EB14320FF5E7114A178F97EECC360BB5C71866D9DEE566FEF1EA6FD9386E3A19AE0BE40920015D3B01EED28A990E8BC2B934B9E35F61073E77285090C7C4B0D015BEAB48E7F5DC91D114C8D979D904A75107A91DFEFA47D857E00A2996E34116848E56B6814992902EFB692DFA1096D944D760D
	E676623ED14ED45C373923C235357BB46FBB03FEA37175581D612672E0317EA317B15637C21BA3266219C179811F3BF46C4C60186468B3ABE117F21E7E963F2731E0AA6E977FEE10541975E0FCC6A1C0AD40D07F3C7CB3BD9C73D7838DA4455E9FB8AB88680F1E7010B281FCD7D995B82FF28892194D76B512F83099F8BB814281228166GACFA906D7633CF487EF820B5BBEBA835DD1745E617193EDE7DG75350C34AF8A6F07ED4D45F30473D128076339745EA624238192222ED78F66F3A4255CE1EF8FF0071D
	C6C8CC06478770FED8E2947663C75B4B847D6FAC5523GB57AA3515DD17225A56F5F757C5E5B2107302DBBEA10CF54C34847A66E55726916BACA3A5A08D7AAE9A79513CBF79086FAD89828547D63AA3C0B1179B056DDD60576F408635675635DFCAF47E5F990B71363E68CE0FDCB9FC639BA24C0F7G1528D311E5CF8D724DFF846B7B6CB272E8FD8477AF0CADA4308903F4B440FC0025GC9GB95362B30AGEA819AGBA81C6810483CC83088318873098E045B4344141637B025820F2C8279A21007E6B2D5301
	FD68FF1F9E5E072EBACD3FA3G5F575C6FD46E73241DA16BAAC7FB3BDB643EFA4D19EC0E2BF7B7B85CB6B2B33375547F698D361E42FDC9044C697199F9535535FEA93C5F36EA552A49G4C5F4D98F257E1E4FEF2768D16DB61D32EF63A6F7EC983242D53756ED6F4DF215F0ECE775FCF11A9BEE43A7FFE4A3B0368B342A0EF45F46C7BD10D017BF0F46370FD985628BF8F5E57B8F49E1C5828BF8FCEE9544E03CCAF2F8D39F7B0B2FD7E7C864B6D483D31BD4959543F36BE6A5F8F8994EFFD543F9FBE4F45FEF0C2
	5E02C779594C005EBCEBF8CCB9DBFDAC707947853FBC9952E3GA2B4F846E333571F47CC20C970AB3FA8882FE5CA1E908D3EA61A3D97C5EB4FAFF757683523D621E9088EDC3755AF4F8B69D00048E87FF63CB664EEBE019805EE2E8E8FD62EEAC839C420DD2E50274D88D4EED33F36DC2EC2D72CBC8F41339920F3063F1C9BFD6E308DAA79E14AB38A1E31G730614FBF2C02FFD0B953A9581657CF848FD6A4C47415FBD2E5C95FCBCD0B9130F8FB2A9F42D4AD30267A800F0CDF9A52EED896CBB4E358C6FBB3EEA
	51779D5616213E4356226FBB9CADDA5F41F0F28B717B9DB0EF35F23AFE0E7B12632E52BA896E8B1BD56E647890AA97411F9089C3440F25F275D1DC2B023BCB037BD241053710B98C1A5C69F0E1597BDB483EE33BCD12D87A81CAE33343144675B1154AE8F3DAB4F2BF42F1C46E6B0B5D6895F1657E585AC1FAFE8EFDB867F1BC23BEC7F18BA03D04639E203815104EFC82FD5325AB823715814DFB6A151B3F7E487B8770F8B1D7ED534B1A77265C1B4FDB3A29BCD81FAE24B1E99E74E77193584FDD17710E5AA10E
	33323B8D1036F2DC4DD6FA768169D00E3BF5AB5E250964382F789D3A980E2B24F1FB5ACC70C1B391B7C86368C310EE60380554662C109E45F179748ECFB82423B92E6B8A4AB207631E60F7FD96F05CD8837D9664BB50366FA06EE88D6A2019639656208EBAB9AEFBGF5B048F1912D48B70C635EE8C59DC4F11C490B3244F25CEF28DEB21FD43E4125382CEA24ABFC9269BE2FC2DD499CF7E7B56AEABC47EDB7E059E80E5B570FB2A7F15C02E6ECEF76D33CFF4FE1FB8BB9AE53036DB0F0DCCBB972B0F3DC1187F9
	C4F05C12A348E3A6475556A10F840E7BCFEEAF0BB82E0337E399473DB4B0D4D70B87102EB686690E50FE8B05F4A4470D21FBA98910DE44F17FF4904BA6F15CDFAEE3BB4CCFA32E5C046D3840F171A6EC074CF16CBEA9E947980EFB6FAC7208F89A4F868B86493CE211EC9DF6328FA560B922F1906F35AD633452A01997FABBACCEC6730DC1DC6FA5F11A98CA5349E9DE66B4D94FA04D32815F3BDAA9341DF910DF748C5E19381762CC10EE6065AE535F76E80775B823391B7A00C1489F43E99A3692DFA55A4964ED
	45BB99DBF12FA116579FCDF173A03D6899BC0B758CFBFFA4DD88FCFFA4ED167A4EE4B1533BB01DB05DCAE44A95DD4A591B550BE7EF59D026E816F20FE9963BA7454A5E5B6F2B2369F45C0C811A70D9DC0F03E4CF214D6292E5DCE8727ECB3F02FA1F46692B69FA4BADAA3F05F054EEE534E7B8ED6CAC2E7FAB7CEE6BAC544F8F021ED52F96FC57300933D47D907CF2C84F70514F9443FA2DBCF4CF9E65FF4E40ED0B4BE427E3A9ABB6BEA3B796F1FF2038D2C857F2DC8D357DCEC84B9C777DD6ECCF9447BD3E0B60
	E2A11D900BED7C0BB7D89B577835515F8616C3BDA44EFC9756682C0D69F48F5FEA11AD4A1D969B09A9992F5D554369D32B5324D8DFFDE6BF9B1FF1CA614B6C2A378A6DAA6FD9552EAA1EE5776DD81BE7D1BFED86DC6B3358668A43F0F22C5534D9FA56D70ED14F2A7D4AF8B4D2DB8D857C78E7116F3B54474F0574820E3B037A78E510DE49F1E554F797BE971FD17A9C4A57D3BE5C38DC9BE4DC1E07FABE74116FAC655189F879B9643B2E8EEDAB0A63C40A0B0374DC0E5B76C7FAA7945242EC146FEAD07D616F57
	84321962592A4D307AFEB7B0DCFF2C533457B05B37BF4C33D53BE0B678E58D095FC53AD30636622CC1DBE985FA67ECE6AB313359DDCCD6EEB21D27DCE4316492DD2C5CB3C6ACB79FE817AA65AA6214BB1CD47E1F9F9CEE1FEB03205EEDB347794AFF815263FC7AAB044E8592603DF1588F67691CB78D52B19C77AB0A1B8B6985F13EF787056763B3321EC779F61D9D4E1EB68469AF8354C356B03F63E37C6CA04117F1FD5FCF5315BC7D86CFABEB1DB6016735DDA6FD291CC7126DE4E38127BBEAD5693E8CF6EEDA
	D5D31CDFD6434FBBA3ECEA39BFEDAD70AFF78CA65022822B4DA2B9BA8A685795E5957C8C736AD652876D9649ADF2B9DE6ED26BEB8AF66E6CA7472F49D5A3E5AFA9540B3F93E7827D8DBE4FFF9363D20DB0F43F3944A9D95ACBAEDA5C36B6237188F910AD77F37505747D04E6C605BFFAF670922DBF866A1D873064F9BC234F6ED1E51FBFC8EC2E202C4AF82032C04165DD716D2BF63F7423D7347A883CFEAD372BFCDEB57876F717C34D7B917D063020684489E3CF4FBE352F6FF02AB469414E1CAB4BC047D5DC47
	475DEA393056117435FADE1D58BA342F5F12557AEA3CA317E3D32F90D4BF15FD2AADCC2AC2DBC802FE2A78AE1E7F3CCE719224C3GA23E0BFD586AD5E54AB550BEBCD0CA7BB0B03F4B64C8C6116D354BEA79394DA39D8F2C6CBB03EA592F4EE531316AF0AAC31551EC3F2A523CAB879B9F861F71F1681ADA6ED479484BD177213C3F1022563168487575FFD51D3EAD7EACD52D734D2DA3370174F4355CF943484B95E584371D57572A36B33E86EDA7866CE359F7510F1836134B077AFE24C714AD648B9E7240E3AC
	F6EED4F82E3ED1E49FFDABFEE4A12D3F708508A7811A81985F1F9A112F8438289710EF6C471F84642B75DF4837D119CDFDB8AB3C379DA4BC92216E95GD9F3744FBF19CE7E10255A401FB728FA5FEB3ABE7B3E122D16FD5EC4479639BEE644FBB1119B557C7D417CD2F9150F0DFEEF13DAEED543484B7D02B4D759EF3A47BB5CB9C7619C3D5F6A089C365C105FE42167A01F7493259E1A63FFBEF20C624DF37C4FC7FE64655F8FC2DE5C9C5C030C525D739FDE6EA707AD27AF77997A3BD879AF7A4B5DCA7145AF7A
	4B7DC6954AED003C0897D16EC9C37664C7A6F774306574652E27E3CA0877173B116233627D65FE3D86654E053C41F814FB097146647E4FE14B694BFD979D270B0748BD1E62178D11FB57C114FBA5641D7FDEFC867B3D0FB9745BB31BDD325945BA768B30DC36BABAFF31F73366FD6110F3312F93980F5F7C83F81C2BD65B7415667D4D2A1B4FDB283E79BC4E9855B635EA5E5F50BDF37CFAF93F738F60B16E205A26C91A773D87EFBE6FC3B715877B8D7AE7CF3F8D13EE742F05F437CB58B4743B746F4D6CCA7F63
	4BDDF20A10D6AFE4F452B335FAA13D0F9C4E558B49DE00FEAF0969FF89A31F38997A67D3971F838F5F79AF2FC57F4D1413A2248C294B6B3C866BCC826ECB42D581223D64B400033230065FE115050D6C4B0CFC7CD06353CFE5A103DD7646E2741ED73DEFCA96D24EF487A798410DE6D9F8CF3DDA4C4B13A355AF203D9BF8FB6B698162625EF2E36A3FBD184A94478ED46B05B4F2AA8AEDF6313627C2CDD4FB648F9762E9FEF5EBD3E7357A20B9A42E41AD10AF4853171E5360B6C41A8F8732581F42E9C828172254
	174C31784189F16E993230D63306677510E866A8E8E11DCFB4767DA9153F18775396F72A10D1AFECA59B5ACD220B5E63952D647720149F6ABEA62CF1BAEC1089F8D0111BBE525DF23FA41A051C76C6728335EED9349E461B2BEED958C2FFF0F1BF3928367F04455DA9130D7003E6A14DCEFED2DC584432A530617D6C3A70C12A67644397282E57ABD63A194ACBEAA96135EC1E3D6143DDA71744268AA9470477487FF66018258D0268FAE10D5C69925D1D8E496AF5FEF608D61459077B3729D05DB65EFC52589232
	1867C2F7FB9F88DD37F45DB1D01757A56C2714E45F3B04D3D86BBAADAEE0A53A6AE57293699037733CDBF648FBDEB97E417994A1D9E9A4D17DCE2F9386C03B2547A675438B0BFDE9276C86C311DD963B1B7CBEA5C8166FA799E6F186A5773ED4BAEE7ABBFF268C542232301329920A6E6F909DBD7B8F373743BC4EABG3DC2755BF9750426440FE613776EADA7972E3A88F6E092D2FBCACB693FE5745F83645FB20AA923183283205C65C250BF7FFCAF36199A8F9F0D247BCB0EC18F4ABF7975CBD3DF2FDD3E8CDA
	F7CCC827BFC7891D102DD21D7674DC94DD37C70F5B3EB6FC7137C89FB90929F8C93DFB3DFEF60EA3A3C5B3EAD3E4A11D6E38780C4AB46589392B1575A72F0B50145354F6EA194C20E828C8313B061A98C1FF48BB1A240AA869C7745FBFDD375AC8297D34EC99E1CF5335DE9D8933153690AC59E4CE774192201F69028E680E052E8E84CDDBAFD07E6B7FFF35750212BE6E85272E6EF378F433C9A725F53684127F77F55FD93D6E8B5E19299C0B9344AEC1BB8124AA34841B763F2F934FC3A72653453CCF0F956A24
	C8EB709BD37C1DAB768D503EEB5A6F3FD75E169B70F7F664D7589AEB4CAB7E5FFF17B65173EE40AFFEC53D4B79A7A17077B0CDBD96C912ADD277E13B15FC9BF324DF96556FB1AA1BC87DEE24D1B2155FEEEFA2F2F7E94A7C8FD0CB8788C90CB5430FA2GG30E8GGD0CB818294G94G88G88GC6F954ACC90CB5430FA2GG30E8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG49A2GGGG
**end of data**/
}
}
