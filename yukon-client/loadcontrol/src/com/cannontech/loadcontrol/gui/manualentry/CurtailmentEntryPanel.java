package com.cannontech.loadcontrol.gui.manualentry;

/**
 * Insert the type's description here.
 * Creation date: (1/20/2001 10:59:31 PM)
 * @author: 
 */
public class CurtailmentEntryPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	com.cannontech.loadcontrol.data.LMProgramCurtailment curtailProgram = null;
	private javax.swing.JLabel ivjJLabelKwhAvail = null;
	private javax.swing.JLabel ivjJLabelKwhPrice = null;
	private javax.swing.JLabel ivjJLabelOfferExpires = null;
	private javax.swing.JLabel ivjJLabelOfferID = null;
	private javax.swing.JLabel ivjJLabelOfferIDtext = null;
	private javax.swing.JLabel ivjJLabelStartTime = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JTextField ivjJTextFieldMessage = null;
	private javax.swing.JLabel ivjJLabelHour = null;
	private javax.swing.JLabel ivjJLabelHour1 = null;
	private javax.swing.JLabel ivjJLabelHour2 = null;
	private javax.swing.JLabel ivjJLabelHour3 = null;
	private javax.swing.JLabel ivjJLabelHour4 = null;
	private javax.swing.JPanel ivjJPanelOfferings = null;
	private javax.swing.JScrollPane ivjJScrollPaneOfferings = null;
	private javax.swing.JTextField ivjJTextFieldAvail1 = null;
	private javax.swing.JTextField ivjJTextFieldAvail2 = null;
	private javax.swing.JTextField ivjJTextFieldAvail3 = null;
	private javax.swing.JTextField ivjJTextFieldAvail4 = null;
	private javax.swing.JTextField ivjJTextFieldExpires1 = null;
	private javax.swing.JTextField ivjJTextFieldExpires2 = null;
	private javax.swing.JTextField ivjJTextFieldExpires3 = null;
	private javax.swing.JTextField ivjJTextFieldPrice1 = null;
	private javax.swing.JTextField ivjJTextFieldPrice2 = null;
	private javax.swing.JTextField ivjJTextFieldPrice3 = null;
	private javax.swing.JTextField ivjJTextFieldPrice4 = null;
	private javax.swing.JTextField ivjJTextFieldExpires4 = null;
	private final int HOUR_FIELD_COUNT = 4;
	private javax.swing.JLabel[] hourLabels = new javax.swing.JLabel[HOUR_FIELD_COUNT];
	private javax.swing.JTextField[] availableTextFields = new javax.swing.JTextField[HOUR_FIELD_COUNT];
	private javax.swing.JTextField[] priceTextFields = new javax.swing.JTextField[HOUR_FIELD_COUNT];
	private javax.swing.JTextField[] expiresTextFields = new javax.swing.JTextField[HOUR_FIELD_COUNT];
	private javax.swing.JButton ivjJButtonStart = null;
	private javax.swing.JLabel ivjJLabelTextUserComment = null;
	private com.klg.jclass.field.JCPopupField ivjJCPopUpFieldCurtDate = null;
	private com.klg.jclass.field.JCPopupField ivjJCPopUpFieldNotifDate = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabelCurtDate = null;
	private javax.swing.JLabel ivjJLabelNotifTime = null;
	private javax.swing.JLabel ivjJLabelNotifyDate = null;
	private javax.swing.JPanel ivjJPanelDate = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldCurtTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldNotifTime = null;
	private javax.swing.JLabel ivjJLabelCurtailDuration = null;
	private javax.swing.JLabel ivjJLabelHours = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldDuration = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private java.awt.FlowLayout ivjJPanel1FlowLayout = null;
/**
 * VoluntaryCurtailmentEntryPanel constructor comment.
 */
public CurtailmentEntryPanel() {
	super();
	initialize();
}
/**
 * VoluntaryCurtailmentEntryPanel constructor comment.
 */
public CurtailmentEntryPanel( com.cannontech.loadcontrol.data.LMProgramCurtailment curtProgram )
{
	super();

	setCurtailProgram(curtProgram);
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
		connEtoC2(e);
	if (e.getSource() == getJButtonStart()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> VoluntaryCurtailmentEntryPanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonStart_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonPostOffer.action.actionPerformed(java.awt.event.ActionEvent) --> VoluntaryCurtailmentEntryPanel.jButtonPostOffer_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * Insert the method's description here.
 * Creation date: (4/9/2001 3:16:28 PM)
 * @return java.lang.String
 * @param seconds int
 */
private String createTimeString(int seconds) 
{
	int intHour = seconds / 3600;
	String hour = Integer.toString(intHour);
	if( hour.length() <= 1 )
		hour = "0" + hour;
		
	String minute = Integer.toString( (seconds - (intHour*3600)) % 60);
	if( minute.length() <= 1 )
		minute = "0" + minute;
	
	return hour + ":" + minute;
}
/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 6:48:08 PM)
 * @return com.cannontech.loadcontrol.data.LMProgramCurtailment
 */
public com.cannontech.loadcontrol.data.LMProgramCurtailment getCurtailProgram() {
	return curtailProgram;
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 11:17:17 AM)
 * @return java.text.SimpleDateFormat
 */
public java.text.SimpleDateFormat getDateFormatter() 
{
	return new java.text.SimpleDateFormat("MMMMMMMM dd, yyyy");
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
 * Return the JButtonPostOffer property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonStart() {
	if (ivjJButtonStart == null) {
		try {
			ivjJButtonStart = new javax.swing.JButton();
			ivjJButtonStart.setName("JButtonStart");
			ivjJButtonStart.setMnemonic('s');
			ivjJButtonStart.setText("Start");
			ivjJButtonStart.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjJButtonStart.setActionCommand("Post Offer");
			ivjJButtonStart.setPreferredSize(new java.awt.Dimension(73, 25));
			ivjJButtonStart.setMinimumSize(new java.awt.Dimension(73, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonStart;
}
/**
 * Return the JCPopUpFieldDate property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCPopupField getJCPopUpFieldCurtDate() {
	if (ivjJCPopUpFieldCurtDate == null) {
		try {
			ivjJCPopUpFieldCurtDate = new com.klg.jclass.field.JCPopupField();
			ivjJCPopUpFieldCurtDate.setName("JCPopUpFieldCurtDate");
			// user code begin {1}

			com.klg.jclass.field.validate.JCDateValidator dv = new com.klg.jclass.field.validate.JCDateValidator();
			dv.setAllowNull(false);
			dv.setDefaultDetail(com.klg.jclass.field.validate.JCDateValidator.LONG);
			dv.setCasePolicy(com.klg.jclass.field.validate.JCDateValidator.UPPERCASE);
			dv.setMin( new java.util.Date() ); // right now is the minimum for the stat

			// create the invalidinfo and set its properties
			getJCPopUpFieldCurtDate().getInvalidInfo().setInvalidPolicy(com.klg.jclass.field.JCInvalidInfo.RESTORE_PREVIOUS);
			getJCPopUpFieldCurtDate().setValueModel( new com.klg.jclass.util.value.DateValueModel( new java.util.Date() ) );
			getJCPopUpFieldCurtDate().setValidator( dv );
			

			getJCPopUpFieldCurtDate().setSelectedItem( getDateFormatter().format(new java.util.Date()) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCPopUpFieldCurtDate;
}
/**
 * Return the JCPopUpFieldNotifDate property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCPopupField getJCPopUpFieldNotifDate() {
	if (ivjJCPopUpFieldNotifDate == null) {
		try {
			ivjJCPopUpFieldNotifDate = new com.klg.jclass.field.JCPopupField();
			ivjJCPopUpFieldNotifDate.setName("JCPopUpFieldNotifDate");
			// user code begin {1}

			com.klg.jclass.field.validate.JCDateValidator dv = new com.klg.jclass.field.validate.JCDateValidator();
			dv.setAllowNull(false);
			dv.setDefaultDetail(com.klg.jclass.field.validate.JCDateValidator.LONG);
			dv.setCasePolicy(com.klg.jclass.field.validate.JCDateValidator.UPPERCASE);
			dv.setMin( new java.util.Date() ); // right now is the minimum for the stat

			// create the invalidinfo and set its properties
			getJCPopUpFieldNotifDate().getInvalidInfo().setInvalidPolicy(com.klg.jclass.field.JCInvalidInfo.RESTORE_PREVIOUS);
			getJCPopUpFieldNotifDate().setValueModel( new com.klg.jclass.util.value.DateValueModel( new java.util.Date() ) );
			getJCPopUpFieldNotifDate().setValidator( dv );
			

			//make the default Notif time to be today + the Programs minResponseTime
			//java.util.Date date = new java.util.Date();
			//date = new java.util.Date( date.getTime() + getCurtailProgram().getMinResponseTime().intValue() * 1000 );
			getJCPopUpFieldNotifDate().setSelectedItem( getDateFormatter().format(new java.util.Date()) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCPopUpFieldNotifDate;
}
/**
 * Return the JCSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldDuration() {
	if (ivjJCSpinFieldDuration == null) {
		try {
			ivjJCSpinFieldDuration = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldDuration.setName("JCSpinFieldDuration");
			// user code begin {1}

			ivjJCSpinFieldDuration.setDataProperties(new com.klg.jclass.field.DataProperties(
								new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), 
								new Integer(24)/*MAX value*/, null, true, null,
								new Integer(1)/*Increment*/, "#,##0.###;-#,##0.###", 
								false, false, false, null, new Integer(2)/*Default*/), 
								new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
								new Integer(1)), 
								new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), 
								new java.awt.Color(255, 255, 255, 255))));
			
			ivjJCSpinFieldDuration.setValue( new Integer(2) ); // Default value
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldDuration;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabel1.setText("(HH:mm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabel2.setText("(HH:mm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JLabelCurtailDuration property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCurtailDuration() {
	if (ivjJLabelCurtailDuration == null) {
		try {
			ivjJLabelCurtailDuration = new javax.swing.JLabel();
			ivjJLabelCurtailDuration.setName("JLabelCurtailDuration");
			ivjJLabelCurtailDuration.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelCurtailDuration.setText("Curtail Duration:");
			ivjJLabelCurtailDuration.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjJLabelCurtailDuration.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCurtailDuration;
}
/**
 * Return the JLabelDate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCurtDate() {
	if (ivjJLabelCurtDate == null) {
		try {
			ivjJLabelCurtDate = new javax.swing.JLabel();
			ivjJLabelCurtDate.setName("JLabelCurtDate");
			ivjJLabelCurtDate.setText("Curtail:");
			ivjJLabelCurtDate.setMaximumSize(new java.awt.Dimension(29, 16));
			ivjJLabelCurtDate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelCurtDate.setMinimumSize(new java.awt.Dimension(29, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCurtDate;
}
/**
 * Return the JLabelHour property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHour() {
	if (ivjJLabelHour == null) {
		try {
			ivjJLabelHour = new javax.swing.JLabel();
			ivjJLabelHour.setName("JLabelHour");
			ivjJLabelHour.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelHour.setText("Hour");
			ivjJLabelHour.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHour;
}
/**
 * Return the JLabelHour1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHour1() {
	if (ivjJLabelHour1 == null) {
		try {
			ivjJLabelHour1 = new javax.swing.JLabel();
			ivjJLabelHour1.setName("JLabelHour1");
			ivjJLabelHour1.setText("1");
			ivjJLabelHour1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHour1;
}
/**
 * Return the JLabelHour2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHour2() {
	if (ivjJLabelHour2 == null) {
		try {
			ivjJLabelHour2 = new javax.swing.JLabel();
			ivjJLabelHour2.setName("JLabelHour2");
			ivjJLabelHour2.setText("2");
			ivjJLabelHour2.setEnabled(false);
			ivjJLabelHour2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHour2;
}
/**
 * Return the JLabelHour3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHour3() {
	if (ivjJLabelHour3 == null) {
		try {
			ivjJLabelHour3 = new javax.swing.JLabel();
			ivjJLabelHour3.setName("JLabelHour3");
			ivjJLabelHour3.setText("3");
			ivjJLabelHour3.setEnabled(false);
			ivjJLabelHour3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHour3;
}
/**
 * Return the JLabelHour4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHour4() {
	if (ivjJLabelHour4 == null) {
		try {
			ivjJLabelHour4 = new javax.swing.JLabel();
			ivjJLabelHour4.setName("JLabelHour4");
			ivjJLabelHour4.setText("4");
			ivjJLabelHour4.setEnabled(false);
			ivjJLabelHour4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHour4;
}
/**
 * Return the JLabelHours property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHours() {
	if (ivjJLabelHours == null) {
		try {
			ivjJLabelHours = new javax.swing.JLabel();
			ivjJLabelHours.setName("JLabelHours");
			ivjJLabelHours.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelHours.setText("(Hours)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHours;
}
/**
 * Return the JLabelKwhAvail property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKwhAvail() {
	if (ivjJLabelKwhAvail == null) {
		try {
			ivjJLabelKwhAvail = new javax.swing.JLabel();
			ivjJLabelKwhAvail.setName("JLabelKwhAvail");
			ivjJLabelKwhAvail.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelKwhAvail.setText("Available");
			ivjJLabelKwhAvail.setMaximumSize(new java.awt.Dimension(52, 16));
			ivjJLabelKwhAvail.setMinimumSize(new java.awt.Dimension(52, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelKwhAvail;
}
/**
 * Return the JLabelKwhPrice property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKwhPrice() {
	if (ivjJLabelKwhPrice == null) {
		try {
			ivjJLabelKwhPrice = new javax.swing.JLabel();
			ivjJLabelKwhPrice.setName("JLabelKwhPrice");
			ivjJLabelKwhPrice.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelKwhPrice.setText("$ Price");
			ivjJLabelKwhPrice.setMaximumSize(new java.awt.Dimension(41, 16));
			ivjJLabelKwhPrice.setMinimumSize(new java.awt.Dimension(41, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelKwhPrice;
}
/**
 * Return the JLabelNotifTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNotifTime() {
	if (ivjJLabelNotifTime == null) {
		try {
			ivjJLabelNotifTime = new javax.swing.JLabel();
			ivjJLabelNotifTime.setName("JLabelNotifTime");
			ivjJLabelNotifTime.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelNotifTime.setText("Date");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNotifTime;
}
/**
 * Return the JLabelNotifyDate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNotifyDate() {
	if (ivjJLabelNotifyDate == null) {
		try {
			ivjJLabelNotifyDate = new javax.swing.JLabel();
			ivjJLabelNotifyDate.setName("JLabelNotifyDate");
			ivjJLabelNotifyDate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelNotifyDate.setText("Notify:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNotifyDate;
}
/**
 * Return the JLabelOfferExpires property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOfferExpires() {
	if (ivjJLabelOfferExpires == null) {
		try {
			ivjJLabelOfferExpires = new javax.swing.JLabel();
			ivjJLabelOfferExpires.setName("JLabelOfferExpires");
			ivjJLabelOfferExpires.setText("Offer Expires");
			ivjJLabelOfferExpires.setMaximumSize(new java.awt.Dimension(73, 16));
			ivjJLabelOfferExpires.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOfferExpires.setMinimumSize(new java.awt.Dimension(73, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOfferExpires;
}
/**
 * Return the JLabelOfferID property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOfferID() {
	if (ivjJLabelOfferID == null) {
		try {
			ivjJLabelOfferID = new javax.swing.JLabel();
			ivjJLabelOfferID.setName("JLabelOfferID");
			ivjJLabelOfferID.setText("Offer ID:");
			ivjJLabelOfferID.setMaximumSize(new java.awt.Dimension(44, 16));
			ivjJLabelOfferID.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOfferID.setMinimumSize(new java.awt.Dimension(44, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOfferID;
}
/**
 * Return the JLabelOfferIDtext property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOfferIDtext() {
	if (ivjJLabelOfferIDtext == null) {
		try {
			ivjJLabelOfferIDtext = new javax.swing.JLabel();
			ivjJLabelOfferIDtext.setName("JLabelOfferIDtext");
			ivjJLabelOfferIDtext.setText("offID");
			ivjJLabelOfferIDtext.setMaximumSize(new java.awt.Dimension(36, 19));
			ivjJLabelOfferIDtext.setFont(new java.awt.Font("sansserif", 1, 14));
			ivjJLabelOfferIDtext.setMinimumSize(new java.awt.Dimension(36, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOfferIDtext;
}
/**
 * Return the JLabelStartTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartTime() {
	if (ivjJLabelStartTime == null) {
		try {
			ivjJLabelStartTime = new javax.swing.JLabel();
			ivjJLabelStartTime.setName("JLabelStartTime");
			ivjJLabelStartTime.setText("Time");
			ivjJLabelStartTime.setMaximumSize(new java.awt.Dimension(59, 16));
			ivjJLabelStartTime.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelStartTime.setMinimumSize(new java.awt.Dimension(59, 16));
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
 * Return the JLabelTextMessage property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTextUserComment() {
	if (ivjJLabelTextUserComment == null) {
		try {
			ivjJLabelTextUserComment = new javax.swing.JLabel();
			ivjJLabelTextUserComment.setName("JLabelTextUserComment");
			ivjJLabelTextUserComment.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelTextUserComment.setText("User Comment:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTextUserComment;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(getJPanel1FlowLayout());
			getJPanel1().add(getJButtonStart(), getJButtonStart().getName());
			getJPanel1().add(getJButtonCancel(), getJButtonCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel1FlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanel1FlowLayout() {
	java.awt.FlowLayout ivjJPanel1FlowLayout = null;
	try {
		/* Create part */
		ivjJPanel1FlowLayout = new java.awt.FlowLayout();
		ivjJPanel1FlowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel1FlowLayout;
}
/**
 * Return the JPanelDate property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelDate() {
	if (ivjJPanelDate == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Date");
			ivjJPanelDate = new javax.swing.JPanel();
			ivjJPanelDate.setName("JPanelDate");
			ivjJPanelDate.setBorder(ivjLocalBorder1);
			ivjJPanelDate.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelCurtDate = new java.awt.GridBagConstraints();
			constraintsJLabelCurtDate.gridx = 1; constraintsJLabelCurtDate.gridy = 3;
			constraintsJLabelCurtDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCurtDate.ipadx = 23;
			constraintsJLabelCurtDate.ipady = 1;
			constraintsJLabelCurtDate.insets = new java.awt.Insets(8, 19, 7, 1);
			getJPanelDate().add(getJLabelCurtDate(), constraintsJLabelCurtDate);

			java.awt.GridBagConstraints constraintsJCPopUpFieldCurtDate = new java.awt.GridBagConstraints();
			constraintsJCPopUpFieldCurtDate.gridx = 2; constraintsJCPopUpFieldCurtDate.gridy = 3;
			constraintsJCPopUpFieldCurtDate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJCPopUpFieldCurtDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCPopUpFieldCurtDate.weightx = 1.0;
			constraintsJCPopUpFieldCurtDate.ipadx = 33;
			constraintsJCPopUpFieldCurtDate.insets = new java.awt.Insets(5, 2, 4, 8);
			getJPanelDate().add(getJCPopUpFieldCurtDate(), constraintsJCPopUpFieldCurtDate);

			java.awt.GridBagConstraints constraintsJTextFieldCurtTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldCurtTime.gridx = 3; constraintsJTextFieldCurtTime.gridy = 3;
			constraintsJTextFieldCurtTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldCurtTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldCurtTime.weightx = 1.0;
			constraintsJTextFieldCurtTime.ipadx = 83;
			constraintsJTextFieldCurtTime.ipady = 1;
			constraintsJTextFieldCurtTime.insets = new java.awt.Insets(6, 8, 5, 2);
			getJPanelDate().add(getJTextFieldCurtTime(), constraintsJTextFieldCurtTime);

			java.awt.GridBagConstraints constraintsJLabelStartTime = new java.awt.GridBagConstraints();
			constraintsJLabelStartTime.gridx = 3; constraintsJLabelStartTime.gridy = 1;
			constraintsJLabelStartTime.ipadx = -8;
			constraintsJLabelStartTime.ipady = 1;
			constraintsJLabelStartTime.insets = new java.awt.Insets(2, 37, 0, 9);
			getJPanelDate().add(getJLabelStartTime(), constraintsJLabelStartTime);

			java.awt.GridBagConstraints constraintsJLabelNotifyDate = new java.awt.GridBagConstraints();
			constraintsJLabelNotifyDate.gridx = 1; constraintsJLabelNotifyDate.gridy = 2;
			constraintsJLabelNotifyDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNotifyDate.ipadx = 19;
			constraintsJLabelNotifyDate.ipady = -2;
			constraintsJLabelNotifyDate.insets = new java.awt.Insets(5, 19, 10, 1);
			getJPanelDate().add(getJLabelNotifyDate(), constraintsJLabelNotifyDate);

			java.awt.GridBagConstraints constraintsJLabelNotifTime = new java.awt.GridBagConstraints();
			constraintsJLabelNotifTime.gridx = 2; constraintsJLabelNotifTime.gridy = 1;
			constraintsJLabelNotifTime.ipadx = 13;
			constraintsJLabelNotifTime.ipady = -2;
			constraintsJLabelNotifTime.insets = new java.awt.Insets(2, 50, 3, 66);
			getJPanelDate().add(getJLabelNotifTime(), constraintsJLabelNotifTime);

			java.awt.GridBagConstraints constraintsJTextFieldNotifTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldNotifTime.gridx = 3; constraintsJTextFieldNotifTime.gridy = 2;
			constraintsJTextFieldNotifTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldNotifTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldNotifTime.weightx = 1.0;
			constraintsJTextFieldNotifTime.ipadx = 82;
			constraintsJTextFieldNotifTime.insets = new java.awt.Insets(2, 10, 7, 1);
			getJPanelDate().add(getJTextFieldNotifTime(), constraintsJTextFieldNotifTime);

			java.awt.GridBagConstraints constraintsJCPopUpFieldNotifDate = new java.awt.GridBagConstraints();
			constraintsJCPopUpFieldNotifDate.gridx = 2; constraintsJCPopUpFieldNotifDate.gridy = 2;
			constraintsJCPopUpFieldNotifDate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJCPopUpFieldNotifDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCPopUpFieldNotifDate.weightx = 1.0;
			constraintsJCPopUpFieldNotifDate.ipadx = 33;
			constraintsJCPopUpFieldNotifDate.insets = new java.awt.Insets(1, 2, 5, 8);
			getJPanelDate().add(getJCPopUpFieldNotifDate(), constraintsJCPopUpFieldNotifDate);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 4; constraintsJLabel1.gridy = 3;
			constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel1.ipadx = 17;
			constraintsJLabel1.ipady = -2;
			constraintsJLabel1.insets = new java.awt.Insets(9, 1, 9, 26);
			getJPanelDate().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 4; constraintsJLabel2.gridy = 2;
			constraintsJLabel2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel2.ipadx = 17;
			constraintsJLabel2.ipady = -2;
			constraintsJLabel2.insets = new java.awt.Insets(5, 3, 10, 24);
			getJPanelDate().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabelCurtailDuration = new java.awt.GridBagConstraints();
			constraintsJLabelCurtailDuration.gridx = 2; constraintsJLabelCurtailDuration.gridy = 4;
			constraintsJLabelCurtailDuration.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCurtailDuration.ipadx = 14;
			constraintsJLabelCurtailDuration.ipady = -2;
			constraintsJLabelCurtailDuration.insets = new java.awt.Insets(8, 50, 16, 8);
			getJPanelDate().add(getJLabelCurtailDuration(), constraintsJLabelCurtailDuration);

			java.awt.GridBagConstraints constraintsJCSpinFieldDuration = new java.awt.GridBagConstraints();
			constraintsJCSpinFieldDuration.gridx = 3; constraintsJCSpinFieldDuration.gridy = 4;
			constraintsJCSpinFieldDuration.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCSpinFieldDuration.ipadx = 79;
			constraintsJCSpinFieldDuration.ipady = 19;
			constraintsJCSpinFieldDuration.insets = new java.awt.Insets(5, 8, 13, 9);
			getJPanelDate().add(getJCSpinFieldDuration(), constraintsJCSpinFieldDuration);

			java.awt.GridBagConstraints constraintsJLabelHours = new java.awt.GridBagConstraints();
			constraintsJLabelHours.gridx = 4; constraintsJLabelHours.gridy = 4;
			constraintsJLabelHours.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelHours.ipadx = 26;
			constraintsJLabelHours.ipady = -2;
			constraintsJLabelHours.insets = new java.awt.Insets(9, 1, 15, 26);
			getJPanelDate().add(getJLabelHours(), constraintsJLabelHours);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDate;
}
/**
 * Return the JPanelOfferings property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOfferings() {
	if (ivjJPanelOfferings == null) {
		try {
			ivjJPanelOfferings = new javax.swing.JPanel();
			ivjJPanelOfferings.setName("JPanelOfferings");
			ivjJPanelOfferings.setPreferredSize(new java.awt.Dimension(353, 370));
			ivjJPanelOfferings.setLayout(new java.awt.GridBagLayout());
			ivjJPanelOfferings.setLocation(0, 0);
			ivjJPanelOfferings.setMaximumSize(new java.awt.Dimension(353, 370));
			ivjJPanelOfferings.setMinimumSize(new java.awt.Dimension(353, 370));

			java.awt.GridBagConstraints constraintsJLabelOfferExpires = new java.awt.GridBagConstraints();
			constraintsJLabelOfferExpires.gridx = 4; constraintsJLabelOfferExpires.gridy = 1;
			constraintsJLabelOfferExpires.ipadx = 4;
			constraintsJLabelOfferExpires.ipady = 1;
			constraintsJLabelOfferExpires.insets = new java.awt.Insets(2, 10, 2, 62);
			getJPanelOfferings().add(getJLabelOfferExpires(), constraintsJLabelOfferExpires);

			java.awt.GridBagConstraints constraintsJTextFieldExpires1 = new java.awt.GridBagConstraints();
			constraintsJTextFieldExpires1.gridx = 4; constraintsJTextFieldExpires1.gridy = 2;
			constraintsJTextFieldExpires1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldExpires1.weightx = 1.0;
			constraintsJTextFieldExpires1.ipadx = 126;
			constraintsJTextFieldExpires1.ipady = 1;
			constraintsJTextFieldExpires1.insets = new java.awt.Insets(3, 10, 4, 9);
			getJPanelOfferings().add(getJTextFieldExpires1(), constraintsJTextFieldExpires1);

			java.awt.GridBagConstraints constraintsJLabelHour = new java.awt.GridBagConstraints();
			constraintsJLabelHour.gridx = 1; constraintsJLabelHour.gridy = 1;
			constraintsJLabelHour.ipadx = 18;
			constraintsJLabelHour.ipady = -2;
			constraintsJLabelHour.insets = new java.awt.Insets(5, 9, 2, 13);
			getJPanelOfferings().add(getJLabelHour(), constraintsJLabelHour);

			java.awt.GridBagConstraints constraintsJLabelKwhPrice = new java.awt.GridBagConstraints();
			constraintsJLabelKwhPrice.gridx = 2; constraintsJLabelKwhPrice.gridy = 1;
			constraintsJLabelKwhPrice.ipadx = 7;
			constraintsJLabelKwhPrice.insets = new java.awt.Insets(3, 13, 2, 28);
			getJPanelOfferings().add(getJLabelKwhPrice(), constraintsJLabelKwhPrice);

			java.awt.GridBagConstraints constraintsJLabelKwhAvail = new java.awt.GridBagConstraints();
			constraintsJLabelKwhAvail.gridx = 3; constraintsJLabelKwhAvail.gridy = 1;
			constraintsJLabelKwhAvail.ipadx = 6;
			constraintsJLabelKwhAvail.insets = new java.awt.Insets(3, 11, 2, 26);
			getJPanelOfferings().add(getJLabelKwhAvail(), constraintsJLabelKwhAvail);

			java.awt.GridBagConstraints constraintsJTextFieldAvail1 = new java.awt.GridBagConstraints();
			constraintsJTextFieldAvail1.gridx = 3; constraintsJTextFieldAvail1.gridy = 2;
			constraintsJTextFieldAvail1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldAvail1.weightx = 1.0;
			constraintsJTextFieldAvail1.ipadx = 70;
			constraintsJTextFieldAvail1.insets = new java.awt.Insets(3, 11, 5, 10);
			getJPanelOfferings().add(getJTextFieldAvail1(), constraintsJTextFieldAvail1);

			java.awt.GridBagConstraints constraintsJTextFieldPrice1 = new java.awt.GridBagConstraints();
			constraintsJTextFieldPrice1.gridx = 2; constraintsJTextFieldPrice1.gridy = 2;
			constraintsJTextFieldPrice1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPrice1.weightx = 1.0;
			constraintsJTextFieldPrice1.ipadx = 61;
			constraintsJTextFieldPrice1.insets = new java.awt.Insets(3, 13, 5, 11);
			getJPanelOfferings().add(getJTextFieldPrice1(), constraintsJTextFieldPrice1);

			java.awt.GridBagConstraints constraintsJLabelHour1 = new java.awt.GridBagConstraints();
			constraintsJLabelHour1.gridx = 1; constraintsJLabelHour1.gridy = 2;
			constraintsJLabelHour1.ipadx = 38;
			constraintsJLabelHour1.insets = new java.awt.Insets(6, 9, 8, 13);
			getJPanelOfferings().add(getJLabelHour1(), constraintsJLabelHour1);

			java.awt.GridBagConstraints constraintsJLabelHour2 = new java.awt.GridBagConstraints();
			constraintsJLabelHour2.gridx = 1; constraintsJLabelHour2.gridy = 3;
			constraintsJLabelHour2.ipadx = 38;
			constraintsJLabelHour2.insets = new java.awt.Insets(8, 9, 7, 13);
			getJPanelOfferings().add(getJLabelHour2(), constraintsJLabelHour2);

			java.awt.GridBagConstraints constraintsJTextFieldPrice2 = new java.awt.GridBagConstraints();
			constraintsJTextFieldPrice2.gridx = 2; constraintsJTextFieldPrice2.gridy = 3;
			constraintsJTextFieldPrice2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPrice2.weightx = 1.0;
			constraintsJTextFieldPrice2.ipadx = 61;
			constraintsJTextFieldPrice2.insets = new java.awt.Insets(5, 13, 4, 11);
			getJPanelOfferings().add(getJTextFieldPrice2(), constraintsJTextFieldPrice2);

			java.awt.GridBagConstraints constraintsJTextFieldAvail2 = new java.awt.GridBagConstraints();
			constraintsJTextFieldAvail2.gridx = 3; constraintsJTextFieldAvail2.gridy = 3;
			constraintsJTextFieldAvail2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldAvail2.weightx = 1.0;
			constraintsJTextFieldAvail2.ipadx = 70;
			constraintsJTextFieldAvail2.insets = new java.awt.Insets(5, 11, 4, 10);
			getJPanelOfferings().add(getJTextFieldAvail2(), constraintsJTextFieldAvail2);

			java.awt.GridBagConstraints constraintsJTextFieldExpires2 = new java.awt.GridBagConstraints();
			constraintsJTextFieldExpires2.gridx = 4; constraintsJTextFieldExpires2.gridy = 3;
			constraintsJTextFieldExpires2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldExpires2.weightx = 1.0;
			constraintsJTextFieldExpires2.ipadx = 126;
			constraintsJTextFieldExpires2.insets = new java.awt.Insets(5, 10, 4, 9);
			getJPanelOfferings().add(getJTextFieldExpires2(), constraintsJTextFieldExpires2);

			java.awt.GridBagConstraints constraintsJTextFieldPrice3 = new java.awt.GridBagConstraints();
			constraintsJTextFieldPrice3.gridx = 2; constraintsJTextFieldPrice3.gridy = 4;
			constraintsJTextFieldPrice3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPrice3.weightx = 1.0;
			constraintsJTextFieldPrice3.ipadx = 61;
			constraintsJTextFieldPrice3.insets = new java.awt.Insets(4, 13, 2, 11);
			getJPanelOfferings().add(getJTextFieldPrice3(), constraintsJTextFieldPrice3);

			java.awt.GridBagConstraints constraintsJTextFieldAvail3 = new java.awt.GridBagConstraints();
			constraintsJTextFieldAvail3.gridx = 3; constraintsJTextFieldAvail3.gridy = 4;
			constraintsJTextFieldAvail3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldAvail3.weightx = 1.0;
			constraintsJTextFieldAvail3.ipadx = 70;
			constraintsJTextFieldAvail3.insets = new java.awt.Insets(4, 11, 2, 10);
			getJPanelOfferings().add(getJTextFieldAvail3(), constraintsJTextFieldAvail3);

			java.awt.GridBagConstraints constraintsJTextFieldExpires3 = new java.awt.GridBagConstraints();
			constraintsJTextFieldExpires3.gridx = 4; constraintsJTextFieldExpires3.gridy = 4;
			constraintsJTextFieldExpires3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldExpires3.weightx = 1.0;
			constraintsJTextFieldExpires3.ipadx = 126;
			constraintsJTextFieldExpires3.insets = new java.awt.Insets(4, 10, 2, 9);
			getJPanelOfferings().add(getJTextFieldExpires3(), constraintsJTextFieldExpires3);

			java.awt.GridBagConstraints constraintsJLabelHour3 = new java.awt.GridBagConstraints();
			constraintsJLabelHour3.gridx = 1; constraintsJLabelHour3.gridy = 4;
			constraintsJLabelHour3.ipadx = 38;
			constraintsJLabelHour3.insets = new java.awt.Insets(7, 9, 5, 13);
			getJPanelOfferings().add(getJLabelHour3(), constraintsJLabelHour3);

			java.awt.GridBagConstraints constraintsJLabelHour4 = new java.awt.GridBagConstraints();
			constraintsJLabelHour4.gridx = 1; constraintsJLabelHour4.gridy = 5;
			constraintsJLabelHour4.ipadx = 38;
			constraintsJLabelHour4.insets = new java.awt.Insets(5, 9, 247, 13);
			getJPanelOfferings().add(getJLabelHour4(), constraintsJLabelHour4);

			java.awt.GridBagConstraints constraintsJTextFieldPrice4 = new java.awt.GridBagConstraints();
			constraintsJTextFieldPrice4.gridx = 2; constraintsJTextFieldPrice4.gridy = 5;
			constraintsJTextFieldPrice4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPrice4.weightx = 1.0;
			constraintsJTextFieldPrice4.ipadx = 61;
			constraintsJTextFieldPrice4.insets = new java.awt.Insets(2, 13, 244, 11);
			getJPanelOfferings().add(getJTextFieldPrice4(), constraintsJTextFieldPrice4);

			java.awt.GridBagConstraints constraintsJTextFieldAvail4 = new java.awt.GridBagConstraints();
			constraintsJTextFieldAvail4.gridx = 3; constraintsJTextFieldAvail4.gridy = 5;
			constraintsJTextFieldAvail4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldAvail4.weightx = 1.0;
			constraintsJTextFieldAvail4.ipadx = 70;
			constraintsJTextFieldAvail4.insets = new java.awt.Insets(2, 11, 244, 10);
			getJPanelOfferings().add(getJTextFieldAvail4(), constraintsJTextFieldAvail4);

			java.awt.GridBagConstraints constraintsJTextFieldExpires4 = new java.awt.GridBagConstraints();
			constraintsJTextFieldExpires4.gridx = 4; constraintsJTextFieldExpires4.gridy = 5;
			constraintsJTextFieldExpires4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldExpires4.weightx = 1.0;
			constraintsJTextFieldExpires4.ipadx = 126;
			constraintsJTextFieldExpires4.insets = new java.awt.Insets(2, 10, 244, 9);
			getJPanelOfferings().add(getJTextFieldExpires4(), constraintsJTextFieldExpires4);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOfferings;
}
/**
 * Return the JScrollPaneOfferings property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneOfferings() {
	if (ivjJScrollPaneOfferings == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Offers");
			ivjJScrollPaneOfferings = new javax.swing.JScrollPane();
			ivjJScrollPaneOfferings.setName("JScrollPaneOfferings");
			ivjJScrollPaneOfferings.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneOfferings.setBorder(ivjLocalBorder);
			getJScrollPaneOfferings().setViewportView(getJPanelOfferings());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneOfferings;
}
/**
 * Return the JTextFieldKwhAvail property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAvail1() {
	if (ivjJTextFieldAvail1 == null) {
		try {
			ivjJTextFieldAvail1 = new javax.swing.JTextField();
			ivjJTextFieldAvail1.setName("JTextFieldAvail1");
			ivjJTextFieldAvail1.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAvail1;
}
/**
 * Return the JTextFieldAvail2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAvail2() {
	if (ivjJTextFieldAvail2 == null) {
		try {
			ivjJTextFieldAvail2 = new javax.swing.JTextField();
			ivjJTextFieldAvail2.setName("JTextFieldAvail2");
			ivjJTextFieldAvail2.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAvail2;
}
/**
 * Return the JTextFieldAvail3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAvail3() {
	if (ivjJTextFieldAvail3 == null) {
		try {
			ivjJTextFieldAvail3 = new javax.swing.JTextField();
			ivjJTextFieldAvail3.setName("JTextFieldAvail3");
			ivjJTextFieldAvail3.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAvail3;
}
/**
 * Return the JTextFieldAvail4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAvail4() {
	if (ivjJTextFieldAvail4 == null) {
		try {
			ivjJTextFieldAvail4 = new javax.swing.JTextField();
			ivjJTextFieldAvail4.setName("JTextFieldAvail4");
			ivjJTextFieldAvail4.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAvail4;
}
/**
 * Return the JTextFieldCurtTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldCurtTime() {
	if (ivjJTextFieldCurtTime == null) {
		try {
			ivjJTextFieldCurtTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldCurtTime.setName("JTextFieldCurtTime");
			ivjJTextFieldCurtTime.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			// user code begin {1}

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
				
			ivjJTextFieldCurtTime.setText( hour + ":" + minute );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCurtTime;
}
/**
 * Return the JTextFieldOfferExpires property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldExpires1() {
	if (ivjJTextFieldExpires1 == null) {
		try {
			ivjJTextFieldExpires1 = new javax.swing.JTextField();
			ivjJTextFieldExpires1.setName("JTextFieldExpires1");
			ivjJTextFieldExpires1.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldExpires1;
}
/**
 * Return the JTextFieldExpires2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldExpires2() {
	if (ivjJTextFieldExpires2 == null) {
		try {
			ivjJTextFieldExpires2 = new javax.swing.JTextField();
			ivjJTextFieldExpires2.setName("JTextFieldExpires2");
			ivjJTextFieldExpires2.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldExpires2;
}
/**
 * Return the JTextFieldExpires3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldExpires3() {
	if (ivjJTextFieldExpires3 == null) {
		try {
			ivjJTextFieldExpires3 = new javax.swing.JTextField();
			ivjJTextFieldExpires3.setName("JTextFieldExpires3");
			ivjJTextFieldExpires3.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldExpires3;
}
/**
 * Return the JTextFieldExpires14 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldExpires4() {
	if (ivjJTextFieldExpires4 == null) {
		try {
			ivjJTextFieldExpires4 = new javax.swing.JTextField();
			ivjJTextFieldExpires4.setName("JTextFieldExpires4");
			ivjJTextFieldExpires4.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldExpires4;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMessage() {
	if (ivjJTextFieldMessage == null) {
		try {
			ivjJTextFieldMessage = new javax.swing.JTextField();
			ivjJTextFieldMessage.setName("JTextFieldMessage");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMessage;
}
/**
 * Return the JTextFieldNotifTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldNotifTime() {
	if (ivjJTextFieldNotifTime == null) {
		try {
			ivjJTextFieldNotifTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldNotifTime.setName("JTextFieldNotifTime");
			// user code begin {1}

			ivjJTextFieldNotifTime.setTimeText( new java.util.Date() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldNotifTime;
}
/**
 * Return the JTextFieldKwhPrice property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPrice1() {
	if (ivjJTextFieldPrice1 == null) {
		try {
			ivjJTextFieldPrice1 = new javax.swing.JTextField();
			ivjJTextFieldPrice1.setName("JTextFieldPrice1");
			ivjJTextFieldPrice1.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPrice1;
}
/**
 * Return the JTextFieldPrice2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPrice2() {
	if (ivjJTextFieldPrice2 == null) {
		try {
			ivjJTextFieldPrice2 = new javax.swing.JTextField();
			ivjJTextFieldPrice2.setName("JTextFieldPrice2");
			ivjJTextFieldPrice2.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPrice2;
}
/**
 * Return the JTextFieldPrice3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPrice3() {
	if (ivjJTextFieldPrice3 == null) {
		try {
			ivjJTextFieldPrice3 = new javax.swing.JTextField();
			ivjJTextFieldPrice3.setName("JTextFieldPrice3");
			ivjJTextFieldPrice3.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPrice3;
}
/**
 * Return the JTextFieldPrice4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPrice4() {
	if (ivjJTextFieldPrice4 == null) {
		try {
			ivjJTextFieldPrice4 = new javax.swing.JTextField();
			ivjJTextFieldPrice4.setName("JTextFieldPrice4");
			ivjJTextFieldPrice4.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPrice4;
}
/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 3:09:53 PM)
 * @return int
 */
private int getTotalSeconds( String string ) 
{
	if( string == null )
		return 0;
		
	int hour = Integer.parseInt(string.substring( 0, 2 )) * 3600;
	int minute = Integer.parseInt(string.substring( 3, 5 )) * 60;
	
	return hour + minute;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2001 3:32:14 PM)
 */
private void initArrays() 
{
	hourLabels[0] = getJLabelHour1();
	hourLabels[1] = getJLabelHour2();
	hourLabels[2] = getJLabelHour3();
	hourLabels[3] = getJLabelHour4();

	availableTextFields[0] = getJTextFieldAvail1();
	availableTextFields[1] = getJTextFieldAvail2();
	availableTextFields[2] = getJTextFieldAvail3();
	availableTextFields[3] = getJTextFieldAvail4();
		
	priceTextFields[0] = getJTextFieldPrice1();
	priceTextFields[1] = getJTextFieldPrice2();
	priceTextFields[2] = getJTextFieldPrice3();
	priceTextFields[3] = getJTextFieldPrice4();

	expiresTextFields[0] = getJTextFieldExpires1();
	expiresTextFields[1] = getJTextFieldExpires2();
	expiresTextFields[2] = getJTextFieldExpires3();
	expiresTextFields[3] = getJTextFieldExpires4();	
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
	getJButtonStart().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CurtailmentEntryPanel");
		setBounds(new java.awt.Rectangle(0, 0, 411, 201));
		setLayout(new java.awt.GridBagLayout());
		setSize(447, 365);
		setMinimumSize(new java.awt.Dimension(411, 201));

		java.awt.GridBagConstraints constraintsJLabelOfferID = new java.awt.GridBagConstraints();
		constraintsJLabelOfferID.gridx = 1; constraintsJLabelOfferID.gridy = 1;
		constraintsJLabelOfferID.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelOfferID.ipadx = 33;
		constraintsJLabelOfferID.ipady = 1;
		constraintsJLabelOfferID.insets = new java.awt.Insets(7, 11, 4, 20);
		add(getJLabelOfferID(), constraintsJLabelOfferID);

		java.awt.GridBagConstraints constraintsJLabelOfferIDtext = new java.awt.GridBagConstraints();
		constraintsJLabelOfferIDtext.gridx = 2; constraintsJLabelOfferIDtext.gridy = 1;
		constraintsJLabelOfferIDtext.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelOfferIDtext.ipadx = 74;
		constraintsJLabelOfferIDtext.ipady = 3;
		constraintsJLabelOfferIDtext.insets = new java.awt.Insets(4, 2, 2, 227);
		add(getJLabelOfferIDtext(), constraintsJLabelOfferIDtext);

		java.awt.GridBagConstraints constraintsJLabelTextUserComment = new java.awt.GridBagConstraints();
		constraintsJLabelTextUserComment.gridx = 1; constraintsJLabelTextUserComment.gridy = 3;
		constraintsJLabelTextUserComment.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelTextUserComment.ipadx = 9;
		constraintsJLabelTextUserComment.ipady = 1;
		constraintsJLabelTextUserComment.insets = new java.awt.Insets(6, 11, 5, 0);
		add(getJLabelTextUserComment(), constraintsJLabelTextUserComment);

		java.awt.GridBagConstraints constraintsJTextFieldMessage = new java.awt.GridBagConstraints();
		constraintsJTextFieldMessage.gridx = 2; constraintsJTextFieldMessage.gridy = 3;
		constraintsJTextFieldMessage.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMessage.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldMessage.weightx = 1.0;
		constraintsJTextFieldMessage.ipadx = 324;
		constraintsJTextFieldMessage.insets = new java.awt.Insets(4, 1, 4, 10);
		add(getJTextFieldMessage(), constraintsJTextFieldMessage);

		java.awt.GridBagConstraints constraintsJScrollPaneOfferings = new java.awt.GridBagConstraints();
		constraintsJScrollPaneOfferings.gridx = 1; constraintsJScrollPaneOfferings.gridy = 4;
		constraintsJScrollPaneOfferings.gridwidth = 2;
		constraintsJScrollPaneOfferings.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneOfferings.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneOfferings.weightx = 1.0;
		constraintsJScrollPaneOfferings.weighty = 1.0;
		constraintsJScrollPaneOfferings.ipadx = 396;
		constraintsJScrollPaneOfferings.ipady = 62;
		constraintsJScrollPaneOfferings.insets = new java.awt.Insets(5, 11, 5, 11);
		add(getJScrollPaneOfferings(), constraintsJScrollPaneOfferings);

		java.awt.GridBagConstraints constraintsJPanelDate = new java.awt.GridBagConstraints();
		constraintsJPanelDate.gridx = 1; constraintsJPanelDate.gridy = 2;
		constraintsJPanelDate.gridwidth = 2;
		constraintsJPanelDate.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDate.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelDate.weightx = 1.0;
		constraintsJPanelDate.weighty = 1.0;
		constraintsJPanelDate.ipadx = -10;
		constraintsJPanelDate.ipady = -8;
		constraintsJPanelDate.insets = new java.awt.Insets(2, 11, 4, 11);
		add(getJPanelDate(), constraintsJPanelDate);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 2; constraintsJPanel1.gridy = 5;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.anchor = java.awt.GridBagConstraints.SOUTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.ipadx = 7;
		constraintsJPanel1.ipady = -2;
		constraintsJPanel1.insets = new java.awt.Insets(5, 31, 12, 140);
		add(getJPanel1(), constraintsJPanel1);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initArrays();
	
	getJLabelOfferID().setVisible(false);
	getJLabelOfferIDtext().setVisible(false);
	getJScrollPaneOfferings().setVisible(false);
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/10/2001 9:55:42 AM)
 * @return boolean
 */
private boolean isInputValid() 
{
	if( getJTextFieldCurtTime().getText() == null || getJTextFieldCurtTime().getText().length() <= 0 )
		return false;

	
	if( getJTextFieldNotifTime().getText() == null || getJTextFieldNotifTime().getText().length() <= 0 )
		return false;

	return true;
}
/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	com.cannontech.clientutils.CTILogger.info("THE CANCEL BUTTON DOES NOTHING!!! (please override me!!)");
	
	return;
}
/**
 * Comment
 */
public void jButtonStart_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( isInputValid() )
	{
		//get the date for the start of curtailement
		java.util.GregorianCalendar startCalendar = new java.util.GregorianCalendar();
		if( getJCPopUpFieldCurtDate().getValue() instanceof java.util.GregorianCalendar )
			startCalendar = (java.util.GregorianCalendar)getJCPopUpFieldCurtDate().getValue();
		else
			startCalendar.setTime((java.util.Date)getJCPopUpFieldCurtDate().getValue());
			
		startCalendar.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(getJTextFieldCurtTime().getText().substring(0,2)) );
		startCalendar.set(java.util.Calendar.MINUTE, Integer.parseInt(getJTextFieldCurtTime().getText().substring(3,5)) );
		startCalendar.set(java.util.Calendar.SECOND,0);
		startCalendar.set(java.util.Calendar.MILLISECOND,0);

		//get the date for the start of the notification
		java.util.GregorianCalendar notifyCalendar = new java.util.GregorianCalendar();
		if( getJCPopUpFieldNotifDate().getValue() instanceof java.util.GregorianCalendar )
			notifyCalendar = (java.util.GregorianCalendar)getJCPopUpFieldNotifDate().getValue();
		else
			notifyCalendar.setTime((java.util.Date)getJCPopUpFieldNotifDate().getValue());

		notifyCalendar.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(getJTextFieldNotifTime().getText().substring(0,2)) );
		notifyCalendar.set(java.util.Calendar.MINUTE, Integer.parseInt(getJTextFieldNotifTime().getText().substring(3,5)) );
		notifyCalendar.set(java.util.Calendar.SECOND,0);
		notifyCalendar.set(java.util.Calendar.MILLISECOND,0);

		//get the date for the stop of curtailment
		java.util.GregorianCalendar stopCalendar = new java.util.GregorianCalendar();
		stopCalendar.setTime(new java.util.Date( startCalendar.getTime().getTime() + (((Number)getJCSpinFieldDuration().getValue()).longValue() * 3600000L) ) );//3600000L is the amount of milliseconds in an hour!!
		stopCalendar.set(java.util.Calendar.SECOND,0);
		stopCalendar.set(java.util.Calendar.MILLISECOND,0);

		//get the total milliseconds for our Notif time
		//check the validity of the two times
		if( ((notifyCalendar.getTime().getTime()) + (getCurtailProgram().getMinNotifyTime().intValue() * 1000)) > startCalendar.getTime().getTime() )
		{
			javax.swing.JOptionPane.showConfirmDialog( this, "The notify date/time plus the MinNotifyTime MUST \nbe less than the curtail date/time, try again.", "Incorrect Entry", javax.swing.JOptionPane.CLOSED_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE );
			return;
		}


		String msg = null;
		// build up our message below
		if( getJTextFieldMessage().getText() != null && getJTextFieldMessage().getText().length() > 0 )
			msg = getJTextFieldMessage().getText();
		else
			msg = com.cannontech.common.util.CtiUtilities.STRING_NONE;


		//create a new message and send it
		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write( 
				getCurtailProgram().createScheduledStartMsg(
					startCalendar.getTime(), 
					stopCalendar.getTime(), 
					0, 
					notifyCalendar.getTime(), 
					msg) );

		
		//close the Dialog
		jButtonCancel_ActionPerformed(actionEvent);
	}
	else
		javax.swing.JOptionPane.showConfirmDialog( this, "Your data is not formatted properly, try again.", "Incorrect Data Input", javax.swing.JOptionPane.CLOSED_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE );

	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CurtailmentEntryPanel aCurtailmentEntryPanel;
		aCurtailmentEntryPanel = new CurtailmentEntryPanel();
		frame.setContentPane(aCurtailmentEntryPanel);
		frame.setSize(aCurtailmentEntryPanel.getSize());
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
 * Creation date: (4/9/2001 6:48:08 PM)
 * @param newCurtailProgram com.cannontech.loadcontrol.data.LMProgramCurtailment
 */
protected void setCurtailProgram(com.cannontech.loadcontrol.data.LMProgramCurtailment newCurtailProgram) 
{
	curtailProgram = newCurtailProgram;

	if( curtailProgram != null )
	{
		int notifMinutes = curtailProgram.getMinNotifyTime().intValue() / 60;
		
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.setTime( new java.util.Date() );
		cal.set( cal.MINUTE, cal.get(cal.MINUTE) + notifMinutes );

		getJTextFieldCurtTime().setTimeText( cal.getTime() );
	}
		
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DDCD4D55AB815A99535586AAE15959A96ED2C391BEDD6D4AE61C6E69B35DAEECB3BECD15AAE6D1F5DE57F796E525B659B7CC8828CD4ACD2D4B09679D4C0CD3ED4D0D2DEC1430DADC698E0F4E04619BBB08A4A7B1CF36EF36E1DBBF786701F7B0F1F0F771E67B94F47F94E3967BC67635E91B2BF71CD0C0D4B940458C4C17A5B93AB881EF788C255F32F7DB762BC74C1C902626FD3008F854B118438
	FA832DD8F8BAA9CD78E2F38A247DA06D71CED0D2965CE788A7B607CE019B97FA7C01F64BCFCF5E65DECF45886AC92669745EB5F07DG409FF0649A8D57B4E1E7EF3604DFAC6191E26384E1C58F49F330B7C742D5C23A85408FE0FE8F4B9F8A57D42A797A6ACAA9DF47D4DFA1FA510E3835C40EC2A6C2F1AF63ED132E39420331B13DC457G351370F8C1BA980094BEDEB05B8F41F5DF4B395E4F3BCC7DC5DD3A01815300286F6AABB21AF45DDDF0EFB1990BFAED0622FE5D00CDE7548362C2F5F53D4DA26A8C46FE
	C89EA418965D005EA88876BE226BE73DA9D436A054C0BA41C0F0CF15937D0110DE8290497113AB883E916EBB816C86AC737E9D7B24B2358FBFA84CFBE5A0FE1D0BB2FB9BD8D9679B64B2CF337DBDF9832D9F5BA5D28F51C04BBE9B14D4885082E086708658C45B454848CE38561E916BCD7D7D2601DA33394E58ED7942542DB742FDF5B5D0A45C89FD174952AD882C6E455EC28DFB5C81633BF642FC4CA77EE5588E82CE5CAB04BDF4B4AED943A7E17E45F17EE719AF961C15FD42F4BF6ED2F778D947F66A287BCB
	C1295BEF32A9BED1C3773C2585F151121C14F3326E4AF3242E4BE8DD6743FD2C54BE98FEBF450FA8702C4DAE24ED06243D0096F58E5BC67B87E8CBF3426DC2DAC933269FFC02EA635A0C4C8623D1ED4B5F29CE9FA3531948EDC95CCD70818ABC33450B66A76985C04B6B8FCAE26D6B72A8F906BDA16D8790883084A09AA0EDG728DE09BDBB832EB1CED2CC5E7916BEC86E3375EA2B05E4B17BE022B5EAEDAF4CD83D6D1B75025BF253398F4E70CFA2BA06519F20978600C831F97ED7BDD206330FEC0EF51098653
	GC857756BC53D454A72CC3F74A149E351D3EA1B21DF8F8A7A4D824A3D73D286711F4EAA1EB4F76BC43D1C0351E7DFA2F5B228BB8592C1G5CB33FBCA090FFB5C079BBG0C5C8F3917093C23FA8B749755554DA653B91B390D14C4889CA0FEFE09769D9EF06FB300BE3EAF16608493BC43A614736565F4051C86C397B1DFE739004FD8D58569278BCD58E67E540EED26E16DEC21F069914DB6C32045445ACA1FC97D6C54D31944AE16FE2E3CFD024F31BBA8B4B9BEFFCCC744372AA3DE15E67EDD9FCF6A4A9B7487
	GAC66BE3E75F2AEEB33F5BA2B9EDB69F18BB4B0FD376CF1E627309B7B7CACEE7BEA0A0BGF9EB4C6833DBAB5ADD58628A32A6D0BE266301896BC81C008E669B4FD1B29E6987E1GEC501B75835DF0E315E85F9BA5EDCEFADEFBCC1CE7BA451B61792E07874A4A71F70C12E7D0A70A9643991B28EF550B22E120D7265FC3F5D9EDE74CF257003AFEB4CAECB78D41734D73BFB8CA1E8D983B87743412B80BC47F89FDB675C6BD993E6BCD368111619F91C87BBE273FA0758D0F8BA4G9A5499ED241FE17142DC5DEA
	D2860B21DFC7C6FD0BA8CCB2136776ED9E6F90A8879C09FB5A61EAB463B3B834951F413C0BB305855FEC0B539AC3FCE6B744ADB633E7A15CACBF034C2600D8ED5BFFCA7B8889EFE5366E7BF8BDDCDD04AAD25BBF16C06489670312B2818A4FE35BAF0AA3F46CDF24C6DFEFB29A19CB392FB74527113A511F376921DBA69D59D16E3FAD7169AA5AA949170C3EB31E785228688393ACF4BC5C0DF1E183585381B0F21E79B370BC73874B973B877335C03A8F4053427205D8D8BE66073F96103A21B15ED95D204EDEE4
	9D0276D5DDFD043A0245EC2DFD44FF241E522C981BD56F20319924BB816CD62C479757E2ECB66D1C2F90FE4FC6177128371559B14FAA57E36EF5981B79A4921D51C41F08FE1F9D2F9C9FEA2DD6D317018ECD124F5A9214754CF02793D6413547E0310ACD70A05A513F5FA490FB4958A52125313CE7B70E6559203785E005C8709BABB29536FB82EEAE409200F8AA6F795D2564B9258D2008DDDA74169E1325DF5F1DCFBCDB249B920BE81FD0D4CB498749FD815643D983715B59BA1BA81A866A49B0EE7C26D6A526
	59351C3D8527187FE7E554C8B6AE1EF33B30EFB4A3CEABB676DC37B92E58467C5EEE137DBF72532478482F7F114CFC31F38F7A428F72045950970F8FEA79A2F390FDB1F54F7F1BAFB23F5AAB7B22D5040EE4022E107A7A76BEE5BC1B8F766D9B14475196386F9EF49CD7D91C77A4ED5B76C196672D6378DFD23C2F824F7CD4C371A49D823406A10CCDF6A413E7EB812443G22815658219F81E836E31BDE1ED0A60C917BB5411C4BB220B336BAF4729803A54633FED9CA4FCCCC44FED940B14F38DB9D5777599D4B
	BD492E95DFDCF4627BD81576D771317ADB573AC13BCFE1B5CA7451F16173BA7538C0622785A0F389G6BFB7E2702BC53248F0934E39FF36910320F89DCD79A375AC59F53F20159E73EA03771C2BE7F8BCBA1FD64DC20AD8608G90AEC2DC8ED08350FD916B6833247DE35591EDF9ED26FA40B5C21FDD2F9EDF39CF0B77DCD9DDFC6E44272E0BC9979D79187F7EB218A2354D99C0E7F13409766742B0BC334308ABE9A538FCC86F633818B6026B04341D63B61FA23899101EB70CF1DB708E624790C8C7GA40C84A5
	6583B4827481F80CE03EA5B45FB4C84F83D882908D10F9A9A82972926BC3D83EC3BB489C03703A0A339AA5DA0824EFBC31D93E140F582844FBC8F826DBCFF52FF5A3E735A0470F496350CBA0D571FCA8716B84E55CD81C6CBAEE5C132C9DB76ECF560E9B6B13BFE4F810211CB5A27DE8327B3832B5D9AB2E5CCC4B7D6CC76B28EC2BD5EF258D3FC1A76AA45AF21144D5CE7165B118D8CABE0318F5F72A146720E82AAF65EDF530AEC572D147A50CB39286C95FBA8D5281G4117300D1C2AD04FFD420096F5C90A01
	AE33EB7665315BC6A16429D165E37DC357AAB24E35814D8C6089608B3060B2768BEFA70F59F79F343B693B5FCAF1DD5F6F24E85777DFD3C85B341AEC16AE3D3ACE7F1E124DE9F28F54A46A7B097BD91E78D4428FE3EA2F5E29CD2CCA4D66B4D77C99292E5A8D2B5B5EBD4A3AAD614F553B036A7A8A87BFC6CBFECF99E5571CD147FAA89EF52E3F8640352B70CC7764BEB2A7B98301FFB79894B0CA5A4F1D342FB3423DFD94631346D6124F283BE032095056BCC83EA7DAF1AC0A02FCA900AB86E870D0464ABF99A2
	4FFE0FC5578FC5EEB56B3B044AEB88EF46C96475037C738116812C84483E86F5161FA23C40E8683775379AAE6AA57B623E24EB9A104F0B67E57D4F11E65D993D71F8CF0F5E5254404BC867530EB491DA8927FFFEFABD2737816124D5EFA14D931A964F73F48E15C148078CFAE37797FA2BD557ABBF1FFF7F12347BA32DDD96185310F9GD5C42A195137D412181A4D90605157A3DF441EE40EAF91D82C38BADF99AB4E3F060D71E157E0ACD91022C50F62741FE7EB51932EC57AF107D8B47BDAC62F01AB0BB112AB
	14B1C61BC477006BB41EA75126950784GFD4135585F8C5729475FE5574AEDB072DA67F64AE2C6BB6D3F046B54EB0957D1FC66F56A3544D50398F367834D6BBA9CF76DB48E0A02F442A44485D2DCB62473B96EA8DDD7598769B60E0B22B1C09F24A7F15C61A19C5BFDB82EF788477640C958471DDB653A0F322C526E236C2B3447244B2B5C0FC913D7396ADB18FD9B065087215C66B3C36803880EFBE688FD9045F1B30E218F922EC7DC59F14A8B69F20E5B467D5248F18739DF0C0073381E6C7D4CA026CA6DBE2F
	F5BBDC8774C339FCD6F2827ACE5DC02FD11FDFDC188B7F4ACBCA8AF3CB0BCB8AF04D5CF7C839EFD339EA0F66FA8701606BD95BDAA6DD8327EC155A4A1B2D38EF13B6996C1D0C7BB6A51DA44E6271B7B1283A7A304550DD276BED263D9E9BEF7FC963BB426FB199475B2F5A084C79108E85D8B199E3EE7BF60C39836E3DDDE876B82AB92F8FEFAB0D9326B0BB732648B137BF445CCCE755C9545989F41FA9345D1DD4BE4344273F099304A238B27AED8EF462F8F023027E1C831D58BB9A2F243F67C0CFD4703332BD
	378D4B96731C3FE0FC384A651AC538D42684CFF6EDB935D5483B719F492CCD04E548EDE2730EDCF51BE8B0C0AFCC964273F9EB1056E0FADC2C4150DE1C792DAE1F780D0CA17BBCF1EE541902B8DF0EDB19CDF0244FCA38815B43367C9CED79FC84E03C1387F11DF69F676DBD38D91BD79E9D840C5F8ED420EE7186ECCF4D55F44F845241GE1B7201D7BED18B71A673D1CC572965E8863AEC0470D787C5DCA63C291521E9CD754004FE9400D58C7DF267647C0BA73A6445D407ABE8BDBABE338B427D864CA016842
	FB22D31EC794FCEBBA146B54098A9C4C97E83F54BF200760535045620919348E48D6F671CD98CFDC98A2EB37A4CCE0F95E24F9EA005E4C73E4965078C4E7D745A7A29DCFBAA11F1167FD6898F9BE58428E09ADF4835D8246DB2C0FA632FDG82EEA2382F07325D140F71F9D33EE848AFF8815F28BCC6ADA7ED428B7B5DAC925EC8BCD12C1F87DAA367D9CC7BF9116477C2FB0F9CA7761AADFA08E2AC7AEEDABA1451C547G9F486B6705766E9B073D83D4F7A87004F9E19F7942D74ABEBD6D76CF63C89F22550F24
	5C4C5ACA664DF29F49669C299F91390D006F3E9947605FFF7899DCCF694ECAB30C035D8651E421D38CC036D8CCE63DC53C4030F495D9EA03FFEC3D32BD687F3AC23E5236097231D86885BA66CC3A1945C2319C3F0C623D95F8B66E695AF05C1B81B46196DCEF2B4EA275568DE98FG9F0039G21G113720AFF726978BE34C595ACC66E688F80C0A90C5DA03B9F9E5FB73BFCF4671E55EE333E0EC7D2FD8D76BD542772476A0DDD734ED25F8D63E6733C979DA00E684708670878884D872BDAC1FA53D10150FB407
	DAD154F5755155F0AC5CA903FEC8C613C98CAB5B61FC2D7EEA05E4C74C77DC4F23533C9DE9F9102ED46124351594AD9D4DD25ECEEF57BAC4954D9352B3B4F53C172D2C2339D21EE0693ACCC387633BE950DD5DC63A64EB28C0BEE1AA0CBBD39D7365CFF5B5AFD8C04745FDD355679D96D1FC73D475F9079355F85E2183E88B2662D8D6E7D35A1DF7AB787ED6D9E7732D2E749F49A672BAEFE5FAB6F1FCA7457BAA70CC7FA4BAB613F4B850823F0FFD6B7DF54EF36985FCDD57E79ABC03G4BGA22731B53E4A69B4
	26EEE8F063EFB24E2D164ACBA081F83281BE115205F0CF66A56F3D7F7E7B4C0EDBE8ACFCC431E760B017D66E2B88B8FE4E2F00B0D3583D8762DFBA3674EC94B86D909D7736B00EAD0CBEED13CCFFF3102C3F3A086F7614C1F8D7D100F96B061432D81F9072EFE54C197BD902E64C39725F3B62BC2633BA741BAE0F95CB4E46C8B1E7CC0EEC532C72715B747590396E306E104ACED9A55A54E3631143783E7CC4667B67F1D287DBDACCD6B117AE99C8FE3BA3D74E33F1E87C321F7CD466FBEFDCE5E37D6439AD0200
	F34A2F56EF93DC0C1D509B9E81D9474F1C0549DA1B2E1773FC709171B31F0732A1D5222D7F084CA58C83865120B39AAE526D47D611AC0E3067F1E91B407B065FAA6EFB5B3E4D339A5AFD1147D555A12DABE739DE8FB812253D9E5012253D9EF0AA4B7D9A7457D92E4FB6683314E79BD83D5B37123AD9864FC9B469AFFE9014D488D073839C9B7F1AC59A86B9CD65BCAE9E6C6EA5EB26648415087ADF4EF6DD56573247BEF775D6B6D9A7BBE3022E271F48C7FCE4B61DA3194C9449F0FF4EA6BE3298FA7BC416D542
	3FCB67F3C6FD8FC533390F014E85BB20DC469F60FC683DFCDC7370603821AC021B86E9BF0E0B0A43F953FC0E5B71AFDC5FC879A1622624200E9C0E7BA895F594F35C424FD1C7834725E7230EF60E7BCBAD6A7067384C8D489B48F1DEF4FFA99452619C7767D3A8AF1263E68DA2EE1F8F623E22739AAFC84F6538F994978C69E59C57980F3CB1BEB80E7E221A4C111AEF43F4A8CD7771F4FB35E66CF25B78761E46777C1E5754B15EF3CAE381AB7BE9BA6F76823BFDEF63F5C36BB098524BB86E1B9A6EA36E03166C
	F2D2665B03127AGBC817C8102814281E2GF26E00678D208D208F408BA0608E646F217C41100EGC8189114148750G508DB089408FA098A082A061CE2083B45C097CB7E605EA140B68A7D722C72B5CE8A731A947C59EAD281172465CAE7374A978098F1AA5DA38F4751A00FC473A7F39E659F02FF1139BDE52EF2C97583A58F8F5E6C857F58A1C72ACDD22225D61DE6806ABE8F3ACF6DCD1077335EE284B99F7625AD2D92943B98136CEF948E89AEA1642C9E9DD29389E5725165C09EBC54375C4661A3B200DG
	6C3B0B450624DCF4EF1B7EC59D50423158421B0485527C6C77EBB0964BF9608E2161157F0A5DAAE84F4F5A6E12E2313B64D8EC8544E2EC7C7949F692770681EDA5C04E5DC1C965G0DG1DF7639834ECD33E60FAAE4F8ACE47A10B4E006757BC363A9E0726EC9DFB9C725ECA4FC5417851F641AC6D63CD5FCA62F6D3CF0FD54F473B5B3726F1DC139542B88309EED87C65116F38067483BA5E08D0AE0F3BF16D2530002E25C05A6FEE1C9F5C12C578FACC83921F9F9D13C2019EF6B74E8512635D4D8562D53046B7
	A809G56D1AE5C57CBB42623050EE705002F74C55B6652712C9B52E60EBBC04733C910766638BF513EB09052A13EE8DFC176D8F395F77645407DF401BD2BCC47A63A1E19B293660BB3D16FFEBA9656C03AF9A67A6E1ED368F356E60F1026A3B01BD23E9C4BE749EEEC9A819D99920D49FBF4F02CB2AA65F94E1465915A8CC85F9047E4B35BAD9524BF9954DBC4C3174E48960B6BF41696135150F5C172434DF4EC7EA700BFAF73F395EC8F57C2B70658FC72F53E07378272893320DEG1AE7C9E7C7287D5F0C5906
	D295F5558D3C76D94CFE7A8E815CEF001BCB5C7E03EB49B34776B2797627F4EEA9F62FFA3F10586183B282E6E1B94CB667F2CC617B0C4133D8B942G22E63173AF0CAF750073FCBA77G72255C437626F361DAFE8F6B03D87987EB46AAFF22227C4D405BF90FDCFE916E5F0139741F79FCFAD58D61E3BB9064142CF21FF772D13ABFDC5FE2B21FB4537D834C4168F3BEA1E7DB655D85C2A56F8648FB586B38ECBAD527C4C67BEBCE9A279DB309061E8B325C0DBB56B95294FCB1E9998EBA5574266C75AA1BE98ED6
	ACA94F51E349DCFE0904FBC001AB15F0F3FA56BB78C6E7B0B65858AB94DC561643541E7AD633E1002A63B9987D7A2255DCC6234986739A361FFD0F53FEB7E99B9E7760FE778A077D6EE977B0FA8827DB9C4E2906C9749CBF247FEEC8CBFEB1275FB928C5EF60745BECDA74F6CE3FAECECB3F11538F55EA596F4169AFE816FF9A27AFF1BAAFC0687E1CDE30D60B9E48690B9D6821FE0C1EB09B6951C7347833E7E38C7AA727F3G630B6FE22E102F22DE49D7B8FB6C78122D637D9F9A9F554CD62F292F247866596A
	B575EB6AB1C6EB87DA60ECDCD32FACA07DCA6E3D90E381F48258813CG7CG6683AC868887D88910F69F44E3G15G4DG9DGE600C977E17C33F49B59FBF5957FC81B54F2GA4E0EC7258F657B1504F370F9D83AD5A2EBD978F5D6EBC97DF3ADDFBAE7E1B6D4A39B89B13B349560C74CC3F1BCF1E69ADF43848EDEA285EA22DE38D974AF90CD9EE56314A4B8BF3CBF673752F0DE5B2DFC45C96B55FA1882A7237F49BF4C6D36F96FA4A202494F81DE3DE1457D8AE5F1F2B10E52FC9993FCD376D1679BECCA53E5B
	8281E3D38316B5F00FCCFF6CF3B7F24BF6035CAAAEB72FDA66CB4C5E26DDD62BEE40EA052138870ADB827F4A8BF8F98E6F154BF6F61FAC4BAFFB7CB6549F1079BCCE0D575F525AF18D61F5BD5F3821D6E1D385B9972758CEAFD26C26971DBAF8222D293E36791B567A93471B1B6BEACFFCD35B7CE56D69D67E2C2E5A0DFB81BE70DC055E077329651B09BF345E57A56F685AC403313A3A4DA09A75F5D2D8A24D2F7E3D9B4FDD2678637CEAFA9C6DBFA05D8EE0761F687CAA20CE60722B7662B985CFAE7F5D8BC47E
	82C8AF830882E0E5FB5006E5CB39BFA82966FEBCAF702E8DED345F0FB28EFCCE724E05F488C0787DA82371G76E5AB81D77CA31461FDG4F1E747D88E5BCC9737AC2FABEC0680FA6DA4E3686391C2FE6E3B9D77C886B44205F212C13F3465E2233DDE495342807C4855555B4A4313110C4127349904EF71B9FC0B9BBB7170CD937F274C3829F7AC293376D7E8A745F2487509F9F59515EC00EDBB20867B2F28390974A4FEA34F35CE3F1E823B747DD2AC5DC8847A5EDC0DE614708DB1D0A38CA0E6BDC0B3618FF0C
	E53E3CFB7BD83E63619516B37E8334411F4B7E6CA86A8B7FB12EEFB553B5056CB9CC979BEB7FC62611527DFFAB6E2754DF7D35FB5F2B2A030DDF4D852E475056023147500E826DB174CC0173985ADB20BD067697A847D0E94F1A1E036A05313F1D679B6238539CB7C2E5925C1706EE310F632F25F691FC231EA8646FE1D23B06A82EDD425D2C401D16F0379610B99794394FE4C15E9F9650F58B0351A83D07C97398862CFA117B43175AE8B06B94F64F6638533CEEB7B93DC7230662B9DAFB5985E372295F81E1FC
	E7A8DF731C712DC5F6C2BE969B2652F7FD45B96A18B11D62BD6628E346BB6278F7A30096B587E3644F77225DED8FCA6FCBBFA85BB282775EGFE8A9C5313B5CC4F9EBE487494F0FCB945E74E1571CC7FFBF48CA1E9B35082FF027A9F36DD193F1F9E13CF5B5FB114AF65217179BB67A17477C3F47CAAFEC86D6FC00A5F77105A5F87BFC7FFB782AD60A1AC6F3183D7B6376838C23E699366E365AA2DA176274DD317F7AF45674CD317B7180F6379C0731A07657DDD36D6DF99B5CFBA1B79306BFA48D6518AA1DD73
	30D6DB489BF2D746B68D9D0CEFCE051A2FCF11F744A55FA63BD6193C257CFEEE4AB4DFC5DB8C69F04DB27D5A610CCB3414A765E7523B97BFF3E55BD2076722D84A57A0DD5BDD72BD95674E07C617FC57563A6373F04937FA0312EF1A144FDF3A863A64CBCBD57205CA79A2246BCA17FC7A35CA3EB50FC8FB825235DC3AB642356391655950148F5C154F7C08ABFD45C715FC1324FCBE52B5C0030F4DB73A6A493829B39AFA872477C0583BDA8BB2485E0EE140E898505733AF018838CFF8014E8996004CE50FE04C
	7465A841C5C2DA180F4F62BA22C232AFDBF1BFB36E6A47CC0FDDD59D2C4CA7695CE58D14B7FFBE7A21BC1D6E03C33A05639E4A407DD211632E4F247BA1101E4BF127AB69DEAB2443388FB78F3740B58A52E90F42FC852091209B4083E08640FC00D00095G890F85A5658254GF4G5881FCG668204GC4G880F236CEAAABB9752B5G1DG9EG3EG8BG428104C01087D08350896081608B30GA09CC0F88268GB5G1DG9ECF206C780BB531E37B31D8AA8BFD3624AB31576939164A5621C17BB6A0CD72E5
	0DA4FF7E230EFA085FC89DDC8D5D24EEC85D65CA7595A2559129E769FD46F179126445BDE15C437DD4A39FF92E497E4B662B74EC1098174CA5F0DFDA3D77CC663C5F563985B510BD8B327ECE56A7493C1C2C9511981074F16C994B20F3D8DFE827731F606F846DA73845108E676D77514F85817B12A0457D514F2FFE5F557D9F50D1D9A31729D6F13F684055577D46D555416AB4F6831ED10A6475FCE6831ED1DA49F16C9B82641C511AA791B7131FD14A653817AB700C12196376727741A6F15CC21AEF8924A3B9
	AEE98F66CBFB8AF17B6DE8DFA7475DE5C77B0C9C67416D736038FBC7D15EB40EFB043FFB9F41F16C5D3E1510CED900388C7A0ED58E248BB94EFE9265D5F2DC248DF9BDB9AE7AB0724E6038130D486B4FF1AF73F3E4ABB96E36DA7E6E5C4F9137E5836A706138CF0F230EG0E3B76B8BF1746F1BBD2F9590278B9B73DB36F769E644D6179C668FA129152A39C17C273F9C35A17637AB9EFG47E5D05E98C867BE4D5F0D2438F2C837F15CA79FA0EF9F479DBE0AFA67F35CA39BD06FE20E5B3A91F917BD0DFB3F5E74
	3DF8112C501235E6814F8B7853D8ACBFB8A829BC98738E50EFBB513C822EC1DFFB10BC6352BE43DE576F3CDCCFE31AEE10E566720E26BB6FBB1FC947FD679941EC5FF99EDCC3G4839B8766E73228C4C939E4C464EE8691A3990E54F4854FE37E89E0D0F4AA1DF8D4FEB2E54FECF5A1346CDC648E7DFA83FA7CD771965F7218F5A4D868BD92F124E920CB86EC3CB649224571EDDA5A877E0657D698FB87E68D0DF0B455025F7C12B9D54990CC86B98CDF350C709A50C563B5311C605CA348A3B63DEB06EBD179FF2
	44637E73168E8DD925D21986B5EC10E8AFB4E87A23946DFF3ED8C3EE9923DD7AC6C32EC4CB5F26A9378C6536B6B91623CC423FBD6A08AF1770B735E858D1AE6D3D77EB58A151624BB56DA897243D754B436A3DDD520E7D96625E6F23696AF74989FDBE27BFDC25453F18538BB469611C7E17B2ADFAB4273FD62925BF6597C8CF9B562267F07AF2CD7D451CBE2AD97E86CEFF3FCACBFEBB27D7E86AB7F23A27A63F87271F50641F4669C7B4797DB97D961B96BD1053D717EB15AF145327B81DDDA07488CE3714E949
	DF4969512296FD4DB3C86F55644FFD867B2B1D035A7DFB9D9D434BA1DF8D4F7B5B7D4E7D7BEC72B9DE69BE64304079EF22630B99F8C71E4173E497E9CC618F69F94F60992979EE4F310DF5E6AC82646C9214676C5EE75FA4GFC8C57F1E2E8A2674872C39C4F116D8BE1E77964F3E4DBB3C89F51E7686DB312A5FB3DC59A1B7E444F04B582CFE708345693424E95B3DBB66FF1E7CB324A1685AADB16C07ABE0946FC7EFF9BE51FBFC36338F048939902BEE7FBEC594F86A565BD0BB6BCB1BA168DDAE78757CBBEE8
	83B9F8A60B6988E7E76181EFFC965BCA721063994B4DB4E675813A9F4F33732463D9428DC7C8D9180D5FBBB91E33E32E5A459250C166FB64DDAC26FFBDED4F61000FFC965F5B5DF7D456E7EC3C12731468131CC56CAC895147643D93B7160F15B27397295A9C24E7CB32193C3935E35927145728125731C8EE434C9FC769735987F8F1917A631BE6599F379DF727AFD5F07FDC7AADE277327FDBE85CBC9770010B309D6E6F99CB073BBA0ED468E06D697BEB9D5B5C7DB4BEDE8979921E43B677C90FE31B3B768B39
	4C135C5A13A859644A1EF2503159411E1DEB497EE51F49E23888B6696837EF7084B35643C1EAA311DB43ED3C3CB643012F162F12B29E9F4EE3FC8EFD397B03315A1BBB5BFDC04E5D82DBA3E1BA4CF44E608778394FE17B18FDCC76D57546316A4E1D3EF010D9A1790A490E7D9FC160E79EC7497A1146B917DA0BC1E7F4BEE713F7C266ADC84FD573AA4E56D4149566166DA9AEE0FBB9B72663DECEA4581337987766DFCA47B3999D0B71CC0610C13F798569G00E01EB7AA834FB004AD46EF0714E52A3F8DC2E495
	89786D8F3D83BDD1C1174E8D354A65B9D6B91E72B0BEDB1B4C776A05717031B2BCBC0C679CB21F6765CA67E7DBB8AE328A5B5DDC0E6B2C6267CBB82E209CF1393FC4DCE6954AEB67384343087366380F2B71DCC488477DEC98758AAFA0EEF7B572D6F21C114BB3F35CD3DC2FBF475D407305F35CB5BCDFF6A862967273BDAD9CD7D70E36F8F25C13B644AD60387B4B10F7A547FD4665953F08383BF83E3E9771B96B78D22E474777CF6CD9489A11F98FD538B9E7D6DA65F0064EFAD9661B1EBD31F3E6BF9895387E
	C785D94EF36E5EC7667ADD1FE38AA30F05346F7A91BFDF668BFEDA76A21E47796579AA4179DBB0C5E6232E27680C4EEA682A2E2EA3977A5D3126BCFA5F28085C8401BD5F2DD4FE5ACBC1C979GB5AFE91F39E2F63D9FA717F7CB02DCDE1BBBFFD7B87A7BF2124CF77B847B3B1949327E97D2E4B99E6E7C2D527F2F55B2DF724909557725B5327EDFFFA04BF9FD5F444A11CC264DEE6AFF67076E69DF244BF64CD9AF5B317E78444A7362C7321CFFEC10657CD9BFB1B99FEF1265DC3FD91693B3C1B97E0A775EBB56
	668A0E67B2DB497F78DA303568C4536146B6167741F165E5BA1E7BC43635F40B2C6F018D933375A499522575F56F74ABBB3BF15795723DB2E1BEF636C324E3F97BCB6AB39F17A83E7BA575190F248C0C934CC0DB72926E557CADC3293F72D7C1C94D3FF23FD7A76D918FEB6DC3F4CA3C229B991EAA5A8CC84FD561A45B1CFC932C4837CCC387637BF31512AFD24AA7AC1156CF25EB7E9219DFBAF7604037CF2237C857EE385A813C1C780A4A15FC3E92FD1EF48D01EB98C014935F06AAF57992164AF932173AAADF
	73301A2FD0113746A55F56EA2D3656A6656F132EA3CB55F6BE6E240F0037144F8F2E738196BB716D555407908E792317EA557BF9CDFD88A9AF87A5653C2C45976C64CFA59483CF03A65FADEE7535830FD113EF32DBFD9E40B3CD13EF115B73F07E40932849F738DC0BAFD44A9B815715GEBDED1570376793BDCA9DFB9DC9B81BA1C78661439BDCF8379A73D22E5E7315B7279GCF00A65FBDEE75ADG1EA51AFCB9D7F88EF467044F0132BE76999AE72DF8C55D7706D2FC74AB6A3E7774EE6CFB05E5C1C95D4B30
	6F3DC83FEB486E2FD35C2F49387A7B485B2E2A8E369737066EC305C0F97746A36EC30A8B875C9BBCDF58B06E6746AC635F1298417D5C14DFA36E7BD5381F1B43F1EF717D52E20EFBE1AF6E67FAF05C1A8C5CDB1E46F12D19FCEF1463D2681CAC655520246CD79177G55D18869FD9C17463FF55148F1CF533DB49FC84F673891EE73E219B78D4B9B4EF16753303C119C57DC076590FE03383C8D28B71363DA4AD1C7B74795E7218EBB47CD4DC29D13B8AE339CF578F05CEB97795E2DAC2F8A7D9743F1672B79DEFF
	98628606B11F19639E9C61FB6E9CD7DD0DBE0D6038BB383CA80EEB98F67669CB4348DB739A7F66AC57514CF197294DB3A0BD1763022B1C651D60FA03F93E5FDEC45E4C5FA24EF3987365F3DC8957E167385FD4E1BEAF0E7B87179746F133393CA80E7BEBA5662B799D626E64725AB8EE0F8D7379F15C6FED3CBDF35C3CB24C17920E382F4B51FFB99C77D4B9AF9B47C5083CEC9CD7E0C37FF9F15C2BB614674BF1FB79F7E482B86EAF25A8AF06637614B9574742B264CD7BBD7781372513638E73720A9C17464B9B
	4CF10FF1F94BB8EE8E17177FBA76474F6F167B601795775F6C3E7A7D317D2A6AE06B8C3A6DE49C123E31C5DF75E0789C7BDA990F2FFAC8F3A3FB9E7183DBCFBD4F7AE470D7636B38B793C85B2688E90F57F19D77BD377B864AB5D582F3010FG6EC3843F4E3E7BA96F4D2C247DF9A86043G58BE55DEFEBEA2658DF91FAA778DF69E1359F1B6F3A2EB7315EF48F690FCB3245FD350B304CF2A9D5766ABE83F518E793A5F40F56F20B8473579FB6977D018BDBBA674BD8E35DF26018EA2CCDE1BFF35527D9AFBF4A5
	2E817B3E0175B51B56D7A824435E40353AE7374BF5FCBB9D0756C440789701BCB6BA8EB5C03AA582FD3BF743C4761A54E59909E05F991477F2AE507E47937053A2502E489D32DD0F5131AB98E8215C2E7D14A78652A9EF22DD5197A732AF28362B9C64607782D9BBBBDD056D2C66CD391D353F494E79B21D1E559359A7B33F69584EA6C17AE6871DBF9CC61D5E8A1D7EEF32F345CC67AFC6A6321736C025F3892407A51D4C576B8A64F67AD33765D16FDB2AFD988E323FF010DD3AD316FD442D5D6A3DF2356CC805
	EC768CCC2DA279758364053AEEBC57D5056DFEA5EF978637E5D26F1F2FF94B51DF39EF497EE2BA1207D1C761DB28636E61096C0B5728F4343DA537832663DF3C9C1DDC47DF2AA63227A92AF4F83E457AB4D9C79EAF47B42EA350EDB954FB087EAA9D01CE3E4A61658861BA6639AD07FA2FAFCC25A34A49D71DB6549143F52C34CDE4FFAFED39230E3C65EADF3DD606BA0A17E39FF572B339ED3FDCBE1EEF6538EA5B8D4B65364D742D65E5EAD90EFD5E890E6B66B6588BE59BCE387D5E90513B5A0D8D531CEC5844
	4B6C3B9C7D9AD6B611EF4C4ED379B5E4393AFD7CC2C49DCB380E2F5D56DD32CA478A150E18656A76D1454B113282F53C68369C29AA9DB9AB9CF5942FD0370FD7F8B9762D403A3903FC06C96A2BBC2B4678562AE2EF635DDDB25F2B5F42B7AA361548770D25326C99696331092DE3F4E5607ED58B144FF3856EAB7BD27C82C8AF8308DA61EE5F69DC19EC47B30AEFE55CBD6EB2B25E3FA93E17313A0A6C593859B328BFF82C6D60892E7714621B9189E99353BB29CE662D2DF377FD0F92077A3C2FDE665B30E17CFC
	CB9BE4FF159C16E564E5CD4C6E36C619B7555DB7FFABCA9D748FB549FCAFDD9CBF5F6D749C14748D1CAA7E6D0937A1BEFF1BFFA302626D10768670FF5BDD3BF96228AC6F29E11437GF8D6F2F9B194DF7C87181FGF47E411D3C70E6592F7147647B53ADF2F91F2AA24FF90F4EE8A5E785DD4B9AD11C7B78F8F8FCBCF7AB4EFB1C9F271E6795E7AA760ED3CF1462BCC559B875E41C12F906EC6363592D586F0FAED372C8FEED1769F98EB2994EE01B58B3BCF2DA16D7DAB6CE1F7FCB66F9CD9C274F3F12F9DA4629
	A72AC3667913039EB657B7E4DC59FA755018FC5A6B55191974B97B03FA3DFAB345CF7A03FA3D7ADE3ED7688D3468BF607A4894377B9D2E6D1EBEA61F365D25745B7C6DFFF4FA2F1D623B7F6874DEFB955F63845A12BF225D8B5D6E6F38367BD7E372E95BED9EA1F6D4C62A6D3EC0718D11EA3B63C6506E96204D0BC43BB712A3D638F727388F788F6C8FAC7C8F68282C164BD42B38DFD4759FF80F6EBF20E3F0C4AE532862BEE5646A6B7E742A6AA06F40C2BC5BB0AB9DE25FF704C42BC5C8F77E0D3627671ECD5C
	38682C98A7A4B489C9FDF4E131C9C89CA2AB0FCDC22C9D60021D7CAC6C33507A6F9024BF87D9BC3E283D65572F84FC531099A744B94912E508C2A27D7025A8A431DF05301E97AE8B642893B9925364AEA58A2B649FD8F04BE69156F17B1A6807EB42874B2A635F38A71E9514FD4827C9C8A0DF63819BAD4C56F851E4261612071D8C9EDEG092230167C14E42E74DB125522104A3FF3477512B3C60520F72DC32C37F0E645DB0BFFFB4A9AAFA4B5899B4832E42B5EC2FFBDC15FCDFEB976941483BCF7C8C8B51B8C
	C084BC98E52517C42BF84128EF96B2FBCE684F5B74D6D15FDD0B3F97E095057574F740F7114FDF6DEA54D97BC4323C3237D9C89850756BC5A11D110DD04B3B580FB46C25A50B2D2D21D538C62A478CEAAF11D24425E4BC3EEE5B27C7A266478BF10704D5C6132E7B102ECBB4D91A04D4314F223776190C5DF67381A6A8E588CFE1460338CD3C782430C564B89CB77A1C7DCE2F35AB569E82F759AD42AE1A132C4296719C5D75FDBA8B2852DB1AGF420B20F532E79D85C7E7C612C0EB8A1D6AAA4F1FD065D8C8D29
	C757EFB0DE009B96D953CA4902EA92AD3A81AB7959F430ACD7E599123802226FBFDBBC7D7E3F7F0BAA10D9C5619366CAE05D552BB7756F2A6D69B18C6839G70AB085F4C4513BCC52ABC6976296B0FACFD7F8C340386A13E3F38187EDFC27FAFA57F17D0CC8945141402F3238437FFEA3A9D4BCC9B8FEF7F247A0B8EC18D0A2F3E736C2C5F6C0B0A04529D9292690F24C3A52449390E597A4F68AD138326EFDEBDAD7CA6D2C7E652D46CC46EA6A61E341984293249D8176C587A6149265FC364D892FA655194BD1C
	DC5BC9CDA51DD3AC452EF7EAC289843DC194D6330AACBA83AD8DCB16443EDCC5135B88A3394921490F697F1FCE58E904EBAB93C80C2369A2392CF19210ED70841725D2BECF5A250A17CA1554C0CB756D5A0DF5D4A55B0D4BBB64AF492166C8G330A17C61917C022D3AD93088343D90F8B2D0BFCB4D894D224CF7FC2D718C03E37A78A496C034555BCCBF2FEE3E3F5FFFF01A8FCA8E54E454FGD7D3AA7970EF4195B58E059BF7117FCA2ECEC3E836E31547DAGF4E3263E2365B8494BC19F534DD756F43B2FE253
	6D101BAE090C4883C6D781B56C4646C93074C43BC3C3CE11A8C98E1CFB3F4B3553AB171D7C115DC3322A996732844C5E7F6F5D0DC9361BF4A8D8E7095FE5DF8F4AB61319BFD9E22D633E465637CAAAC91CC34B4276967B5D2B09D139D429126D64475CD23FFB16CEF6ECB3248E48AEEA62F74F521BE5CB4973C7DA8ABDF34631EBA5CC6AF7537E69327D3807CF5B84CCE55854C57ABEG8C57E4A00681B9AF17FEEAG02E67AD981B28D9005589200D200B200F207AE29693BB70C4CF60C5CC893A3DFB60A53309B
	0711D5D29546FF772AF00EE3E908152451DDE1E33B1AFEFF58B140A850C8E1A9C889F22E5C5FD73394CFBA1622DCA30525A0B6ED6425D0C6512B1C30EB318C9C0B5D86214851F932821B6250C6D102F3F43E4A890B6DB951496BF438F5AA45AAA7ACCAF01CA120E5499C7B2E20EE1374E035C2DB1C839635B9E2D1DB9C7E9ED5412872F724D7DE1F9D076F2D2A5737C461E93A9E6CA9DD655F11B650F70A03819F8E403ECD7C689132496B6A37B0DB7BF5C62328B31E2B9D68A63F0BD9F7C1544B3F45B8F0144837
	E29E0948E83623446E338A1E7F85D0CB8788CFAECCFA7CADGG301BGGD0CB818294G94G88G88GD6F954ACCFAECCFA7CADGG301BGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB6AEGGGG
**end of data**/
}
}
