package com.cannontech.loadcontrol.eexchange.views;

/**
 * Insert the type's description here.
 * Creation date: (8/2/2001 11:43:30 AM)
 * @author: 
 */
import com.cannontech.loadcontrol.data.LMProgramEnergyExchange;

public class JPanelCreateOffer extends javax.swing.JPanel implements java.awt.event.ActionListener 
{
	private com.cannontech.loadcontrol.eexchange.datamodels.OfferRowData offerData = null;

	public static final int MODE_CREATE = 0;
	public static final int MODE_REVISE = 1;

	private int mode = MODE_CREATE;
	
	//this ArrayList contains Strings that represent a ControlAreaName.
	//  It is parrallel to the JComboBox. So JComboBox.getItem(0) will
	//  return a LMPRogramEnergyExchange and ownerAreaStrings.get(0) will
	//  contain that LMPRogramEnergyExchange's ControlArea's name.
	private java.util.ArrayList ownerAreaStrings = null;

	private boolean editable = true;
	private LMProgramEnergyExchange[] eExchangePrgs = null;
	private javax.swing.JComboBox ivjJComboBoxProgram = null;
	private com.klg.jclass.field.JCPopupField ivjJCPopUpFieldNotifDate = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabelNotifTime = null;
	private javax.swing.JLabel ivjJLabelNotifyDate = null;
	private javax.swing.JLabel ivjJLabelProgram = null;
	private javax.swing.JLabel ivjJLabelStartTime = null;
	private javax.swing.JPanel ivjJPanelDate = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldNotifTime = null;
	private com.klg.jclass.field.JCPopupField ivjJCPopUpFieldExpireDate = null;
	private javax.swing.JLabel ivjJLabelExpireDate = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldExpireTime = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonConfirm = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private java.awt.FlowLayout ivjJPanel1FlowLayout = null;
	private JPanelHourOffer ivjJPanelHourOffer = null;
	private com.klg.jclass.field.JCPopupField ivjJCPopUpFieldDayOfOfferDate = null;
	private javax.swing.JLabel ivjJLabelOfferDate = null;
	private javax.swing.JLabel ivjJLabelArea = null;
	private javax.swing.JTextField ivjJTextFieldArea = null;
/**
 * JPanelCreateOffer constructor comment.
 */
public JPanelCreateOffer() {
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
	if (e.getSource() == getJButtonConfirm()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonCancel()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxProgram()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonConfirm.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCreateOffer.jButtonConfirm_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonConfirm_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCreateOffer.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC3:  (JComboBoxProgram.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCreateOffer.jComboBoxProgram_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxProgram_ActionPerformed(arg1);
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
 * Creation date: (8/6/2001 9:39:53 AM)
 */
//Please override me if you want me to do something when my owner
//    frame/dialog is closed!!!
public void exit() {}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 11:17:17 AM)
 * @return java.text.SimpleDateFormat
 */
public static java.text.SimpleDateFormat getDateFormatter()
{
	return new java.text.SimpleDateFormat("MMMMMMMM dd, yyyy");
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 12:39:10 PM)
 * @return com.cannontech.loadcontrol.data.LMProgramEnergyExchange[]
 */
public com.cannontech.loadcontrol.data.LMProgramEnergyExchange[] getEExchangePrgs() 
{
	if( eExchangePrgs == null )
	{
		com.cannontech.loadcontrol.data.LMControlArea[] areas = com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().getAllLMControlAreas();
		java.util.ArrayList list = new java.util.ArrayList(10);

		for( int i = 0; i < areas.length; i++ )
		{
			for( int j = 0; j < areas[i].getLmProgramVector().size(); j++ )
			{
				if( areas[i].getLmProgramVector().get(j) instanceof LMProgramEnergyExchange )
				{
					list.add( areas[i].getLmProgramVector().get(j) );

					//add the programs ControlArea name
					getOwnerAreaStrings().add(areas[i].getYukonName());
				}
			}
		}

		LMProgramEnergyExchange[] prgs = new LMProgramEnergyExchange[list.size()];
	
		eExchangePrgs = (LMProgramEnergyExchange[])list.toArray(prgs);
	}

	return eExchangePrgs;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 11:26:35 AM)
 * @return java.util.GregorianCalendar
 */
private java.util.GregorianCalendar getExpireCalendar() 
{
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	
	if( getJCPopUpFieldExpireDate().getValue() instanceof java.util.Date )
		cal.setTime( (java.util.Date)getJCPopUpFieldExpireDate().getValue() );
	else
		cal = (java.util.GregorianCalendar)getJCPopUpFieldExpireDate().getValue();

	cal.set( cal.HOUR_OF_DAY, 
			Integer.parseInt(getJTextFieldExpireTime().getTimeText().substring(0, 2)) );

	cal.set( cal.MINUTE, 
			Integer.parseInt(getJTextFieldExpireTime().getTimeText().substring(3, 5)) );

	cal.set( cal.SECOND, 0 );
	
	return cal;
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
			ivjJButtonCancel.setMnemonic('c');
			ivjJButtonCancel.setText("Cancel");
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
 * Return the JButtonConfirm property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonConfirm() {
	if (ivjJButtonConfirm == null) {
		try {
			ivjJButtonConfirm = new javax.swing.JButton();
			ivjJButtonConfirm.setName("JButtonConfirm");
			ivjJButtonConfirm.setMnemonic('c');
			ivjJButtonConfirm.setText("Confirm");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonConfirm;
}
/**
 * Return the JComboBoxProgram property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxProgram() {
	if (ivjJComboBoxProgram == null) {
		try {
			ivjJComboBoxProgram = new javax.swing.JComboBox();
			ivjJComboBoxProgram.setName("JComboBoxProgram");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxProgram;
}
/**
 * Return the JCPopUpFieldDayOfOfferDate property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCPopupField getJCPopUpFieldDayOfOfferDate() {
	if (ivjJCPopUpFieldDayOfOfferDate == null) {
		try {
			ivjJCPopUpFieldDayOfOfferDate = new com.klg.jclass.field.JCPopupField();
			ivjJCPopUpFieldDayOfOfferDate.setName("JCPopUpFieldDayOfOfferDate");
			// user code begin {1}

			com.klg.jclass.field.validate.JCDateValidator dv = new com.klg.jclass.field.validate.JCDateValidator();
			dv.setAllowNull(false);
			dv.setDefaultDetail(com.klg.jclass.field.validate.JCDateValidator.LONG);
			dv.setCasePolicy(com.klg.jclass.field.validate.JCDateValidator.UPPERCASE);
			dv.setMin( new java.util.Date() ); // right now is the minimum for the stat

			// create the invalidinfo and set its properties
			ivjJCPopUpFieldDayOfOfferDate.getInvalidInfo().setInvalidPolicy(com.klg.jclass.field.JCInvalidInfo.RESTORE_PREVIOUS);
			ivjJCPopUpFieldDayOfOfferDate.setValueModel( new com.klg.jclass.util.value.DateValueModel( new java.util.Date() ) );
			ivjJCPopUpFieldDayOfOfferDate.setValidator( dv );

			ivjJCPopUpFieldDayOfOfferDate.setSelectedItem( getDateFormatter().format(new java.util.Date()) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCPopUpFieldDayOfOfferDate;
}
/**
 * Return the JCPopUpFieldCurtDate property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCPopupField getJCPopUpFieldExpireDate() {
	if (ivjJCPopUpFieldExpireDate == null) {
		try {
			ivjJCPopUpFieldExpireDate = new com.klg.jclass.field.JCPopupField();
			ivjJCPopUpFieldExpireDate.setName("JCPopUpFieldExpireDate");
			// user code begin {1}

			com.klg.jclass.field.validate.JCDateValidator dv = new com.klg.jclass.field.validate.JCDateValidator();
			dv.setAllowNull(false);
			dv.setDefaultDetail(com.klg.jclass.field.validate.JCDateValidator.LONG);
			dv.setCasePolicy(com.klg.jclass.field.validate.JCDateValidator.UPPERCASE);
			dv.setMin( new java.util.Date() ); // right now is the minimum for the stat

			// create the invalidinfo and set its properties
			ivjJCPopUpFieldExpireDate.getInvalidInfo().setInvalidPolicy(com.klg.jclass.field.JCInvalidInfo.RESTORE_PREVIOUS);
			ivjJCPopUpFieldExpireDate.setValueModel( new com.klg.jclass.util.value.DateValueModel( new java.util.Date() ) );
			ivjJCPopUpFieldExpireDate.setValidator( dv );

			ivjJCPopUpFieldExpireDate.setSelectedItem( getDateFormatter().format(new java.util.Date()) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCPopUpFieldExpireDate;
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
			//date = new java.util.Date( date.getTime() + getSelectedEnergyExchangeProgram().getMinResponseTime().intValue() * 1000 );
			//ivjJCPopUpFieldNotifDate.setSelectedItem( getDateFormatter().format(date) );
			ivjJCPopUpFieldNotifDate.setSelectedItem( getDateFormatter().format(new java.util.Date()) );

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
 * Return the JLabelArea property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelArea() {
	if (ivjJLabelArea == null) {
		try {
			ivjJLabelArea = new javax.swing.JLabel();
			ivjJLabelArea.setName("JLabelArea");
			ivjJLabelArea.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelArea.setText("Area:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelArea;
}
/**
 * Return the JLabelCurtDate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelExpireDate() {
	if (ivjJLabelExpireDate == null) {
		try {
			ivjJLabelExpireDate = new javax.swing.JLabel();
			ivjJLabelExpireDate.setName("JLabelExpireDate");
			ivjJLabelExpireDate.setText("Expire:");
			ivjJLabelExpireDate.setMaximumSize(new java.awt.Dimension(29, 16));
			ivjJLabelExpireDate.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabelExpireDate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelExpireDate.setMinimumSize(new java.awt.Dimension(29, 16));
			ivjJLabelExpireDate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelExpireDate;
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
			ivjJLabelNotifyDate.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabelNotifyDate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
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
 * Return the JLabelOfferDate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOfferDate() {
	if (ivjJLabelOfferDate == null) {
		try {
			ivjJLabelOfferDate = new javax.swing.JLabel();
			ivjJLabelOfferDate.setName("JLabelOfferDate");
			ivjJLabelOfferDate.setText("Day Of Offer:");
			ivjJLabelOfferDate.setMaximumSize(new java.awt.Dimension(29, 16));
			ivjJLabelOfferDate.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabelOfferDate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOfferDate.setMinimumSize(new java.awt.Dimension(29, 16));
			ivjJLabelOfferDate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOfferDate;
}
/**
 * Return the JLabelProgram property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelProgram() {
	if (ivjJLabelProgram == null) {
		try {
			ivjJLabelProgram = new javax.swing.JLabel();
			ivjJLabelProgram.setName("JLabelProgram");
			ivjJLabelProgram.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelProgram.setText("Program:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelProgram;
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
			getJPanel1().add(getJButtonConfirm(), getJButtonConfirm().getName());
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
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Customer Dates");
			ivjJPanelDate = new javax.swing.JPanel();
			ivjJPanelDate.setName("JPanelDate");
			ivjJPanelDate.setBorder(ivjLocalBorder);
			ivjJPanelDate.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelExpireDate = new java.awt.GridBagConstraints();
			constraintsJLabelExpireDate.gridx = 1; constraintsJLabelExpireDate.gridy = 3;
			constraintsJLabelExpireDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelExpireDate.ipadx = 40;
			constraintsJLabelExpireDate.ipady = 1;
			constraintsJLabelExpireDate.insets = new java.awt.Insets(7, 12, 6, 1);
			getJPanelDate().add(getJLabelExpireDate(), constraintsJLabelExpireDate);

			java.awt.GridBagConstraints constraintsJCPopUpFieldExpireDate = new java.awt.GridBagConstraints();
			constraintsJCPopUpFieldExpireDate.gridx = 2; constraintsJCPopUpFieldExpireDate.gridy = 3;
			constraintsJCPopUpFieldExpireDate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJCPopUpFieldExpireDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCPopUpFieldExpireDate.weightx = 1.0;
			constraintsJCPopUpFieldExpireDate.ipadx = 23;
			constraintsJCPopUpFieldExpireDate.insets = new java.awt.Insets(4, 2, 3, 9);
			getJPanelDate().add(getJCPopUpFieldExpireDate(), constraintsJCPopUpFieldExpireDate);

			java.awt.GridBagConstraints constraintsJTextFieldExpireTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldExpireTime.gridx = 3; constraintsJTextFieldExpireTime.gridy = 3;
			constraintsJTextFieldExpireTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldExpireTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldExpireTime.weightx = 1.0;
			constraintsJTextFieldExpireTime.ipadx = 73;
			constraintsJTextFieldExpireTime.ipady = 1;
			constraintsJTextFieldExpireTime.insets = new java.awt.Insets(5, 9, 4, 1);
			getJPanelDate().add(getJTextFieldExpireTime(), constraintsJTextFieldExpireTime);

			java.awt.GridBagConstraints constraintsJLabelStartTime = new java.awt.GridBagConstraints();
			constraintsJLabelStartTime.gridx = 3; constraintsJLabelStartTime.gridy = 1;
			constraintsJLabelStartTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelStartTime.ipadx = -18;
			constraintsJLabelStartTime.ipady = 1;
			constraintsJLabelStartTime.insets = new java.awt.Insets(1, 29, 0, 17);
			getJPanelDate().add(getJLabelStartTime(), constraintsJLabelStartTime);

			java.awt.GridBagConstraints constraintsJLabelNotifyDate = new java.awt.GridBagConstraints();
			constraintsJLabelNotifyDate.gridx = 1; constraintsJLabelNotifyDate.gridy = 2;
			constraintsJLabelNotifyDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNotifyDate.ipadx = 36;
			constraintsJLabelNotifyDate.ipady = -2;
			constraintsJLabelNotifyDate.insets = new java.awt.Insets(5, 12, 8, 1);
			getJPanelDate().add(getJLabelNotifyDate(), constraintsJLabelNotifyDate);

			java.awt.GridBagConstraints constraintsJLabelNotifTime = new java.awt.GridBagConstraints();
			constraintsJLabelNotifTime.gridx = 2; constraintsJLabelNotifTime.gridy = 1;
			constraintsJLabelNotifTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNotifTime.ipadx = 16;
			constraintsJLabelNotifTime.ipady = -2;
			constraintsJLabelNotifTime.insets = new java.awt.Insets(1, 50, 3, 54);
			getJPanelDate().add(getJLabelNotifTime(), constraintsJLabelNotifTime);

			java.awt.GridBagConstraints constraintsJTextFieldNotifTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldNotifTime.gridx = 3; constraintsJTextFieldNotifTime.gridy = 2;
			constraintsJTextFieldNotifTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldNotifTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldNotifTime.weightx = 1.0;
			constraintsJTextFieldNotifTime.ipadx = 72;
			constraintsJTextFieldNotifTime.insets = new java.awt.Insets(2, 9, 5, 2);
			getJPanelDate().add(getJTextFieldNotifTime(), constraintsJTextFieldNotifTime);

			java.awt.GridBagConstraints constraintsJCPopUpFieldNotifDate = new java.awt.GridBagConstraints();
			constraintsJCPopUpFieldNotifDate.gridx = 2; constraintsJCPopUpFieldNotifDate.gridy = 2;
			constraintsJCPopUpFieldNotifDate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJCPopUpFieldNotifDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCPopUpFieldNotifDate.weightx = 1.0;
			constraintsJCPopUpFieldNotifDate.ipadx = 23;
			constraintsJCPopUpFieldNotifDate.insets = new java.awt.Insets(1, 2, 3, 9);
			getJPanelDate().add(getJCPopUpFieldNotifDate(), constraintsJCPopUpFieldNotifDate);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 4; constraintsJLabel1.gridy = 3;
			constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel1.ipadx = 17;
			constraintsJLabel1.ipady = -2;
			constraintsJLabel1.insets = new java.awt.Insets(8, 2, 8, 34);
			getJPanelDate().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 4; constraintsJLabel2.gridy = 2;
			constraintsJLabel2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel2.ipadx = 17;
			constraintsJLabel2.ipady = -2;
			constraintsJLabel2.insets = new java.awt.Insets(5, 2, 8, 34);
			getJPanelDate().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabelOfferDate = new java.awt.GridBagConstraints();
			constraintsJLabelOfferDate.gridx = 1; constraintsJLabelOfferDate.gridy = 4;
			constraintsJLabelOfferDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelOfferDate.ipadx = 40;
			constraintsJLabelOfferDate.ipady = 1;
			constraintsJLabelOfferDate.insets = new java.awt.Insets(7, 12, 12, 1);
			getJPanelDate().add(getJLabelOfferDate(), constraintsJLabelOfferDate);

			java.awt.GridBagConstraints constraintsJCPopUpFieldDayOfOfferDate = new java.awt.GridBagConstraints();
			constraintsJCPopUpFieldDayOfOfferDate.gridx = 2; constraintsJCPopUpFieldDayOfOfferDate.gridy = 4;
			constraintsJCPopUpFieldDayOfOfferDate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJCPopUpFieldDayOfOfferDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCPopUpFieldDayOfOfferDate.weightx = 1.0;
			constraintsJCPopUpFieldDayOfOfferDate.ipadx = 23;
			constraintsJCPopUpFieldDayOfOfferDate.insets = new java.awt.Insets(4, 2, 9, 9);
			getJPanelDate().add(getJCPopUpFieldDayOfOfferDate(), constraintsJCPopUpFieldDayOfOfferDate);
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
 * Return the JPanelHourOffer property value.
 * @return com.cannontech.loadcontrol.eexchange.views.JPanelHourOffer
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JPanelHourOffer getJPanelHourOffer() {
	if (ivjJPanelHourOffer == null) {
		try {
			ivjJPanelHourOffer = new com.cannontech.loadcontrol.eexchange.views.JPanelHourOffer();
			ivjJPanelHourOffer.setName("JPanelHourOffer");
			// user code begin {1}

			ivjJPanelHourOffer.getTableModel().setMode(
				com.cannontech.loadcontrol.eexchange.datamodels.HourTableModel.MODE_MW );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHourOffer;
}
/**
 * Return the JTextFieldArea property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldArea() {
	if (ivjJTextFieldArea == null) {
		try {
			ivjJTextFieldArea = new javax.swing.JTextField();
			ivjJTextFieldArea.setName("JTextFieldArea");
			ivjJTextFieldArea.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldArea;
}
/**
 * Return the JTextFieldCurtTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldExpireTime() {
	if (ivjJTextFieldExpireTime == null) {
		try {
			ivjJTextFieldExpireTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldExpireTime.setName("JTextFieldExpireTime");
			ivjJTextFieldExpireTime.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
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
				
			ivjJTextFieldExpireTime.setText( hour + ":" + minute );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldExpireTime;
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
 * Insert the method's description here.
 * Creation date: (8/14/2001 10:59:19 AM)
 * @return int
 */
public int getMode() {
	return mode;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 11:26:35 AM)
 * @return java.util.GregorianCalendar
 */
private java.util.GregorianCalendar getNotifCalendar() 
{
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	
	if( getJCPopUpFieldNotifDate().getValue() instanceof java.util.Date )
		cal.setTime( (java.util.Date)getJCPopUpFieldNotifDate().getValue() );
	else
		cal = (java.util.GregorianCalendar)getJCPopUpFieldNotifDate().getValue();

	cal.set( cal.HOUR_OF_DAY, 
			Integer.parseInt(getJTextFieldNotifTime().getTimeText().substring(0, 2)) );

	cal.set( cal.MINUTE, 
			Integer.parseInt(getJTextFieldNotifTime().getTimeText().substring(3, 5)) );

	cal.set( cal.SECOND, 0 );
	
	return cal;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 11:26:35 AM)
 * @return java.util.GregorianCalendar
 */
private java.util.GregorianCalendar getOfferCalendar() 
{
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	
	if( getJCPopUpFieldDayOfOfferDate().getValue() instanceof java.util.Date )
		cal.setTime( (java.util.Date)getJCPopUpFieldDayOfOfferDate().getValue() );
	else
		cal = (java.util.GregorianCalendar)getJCPopUpFieldDayOfOfferDate().getValue();

	cal.set( cal.HOUR_OF_DAY, 0 );
	cal.set( cal.MINUTE, 0 );
	cal.set( cal.SECOND, 0 );
	
	return cal;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 10:55:48 AM)
 * @return java.util.ArrayList
 */
private java.util.ArrayList getOwnerAreaStrings() 
{
	if( ownerAreaStrings == null )
		ownerAreaStrings = new java.util.ArrayList(10);

	return ownerAreaStrings;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 12:31:24 PM)
 * @return com.cannontech.loadcontrol.data.LMProgramEnergyExchange
 */
public LMProgramEnergyExchange getSelectedEnergyExchangeProgram() 
{
	if( getJComboBoxProgram().getSelectedIndex() >= 0 )
	{
		if( getJComboBoxProgram().getSelectedItem() instanceof LMProgramEnergyExchange )
			return (LMProgramEnergyExchange)getJComboBoxProgram().getSelectedItem();
		else
		{
			com.cannontech.clientutils.CTILogger.info("*** Found an object of type : " + getJComboBoxProgram().getSelectedItem().getClass().getName());
			com.cannontech.clientutils.CTILogger.info("    in the JComboBox of " + this.getClass().getName());
			com.cannontech.clientutils.CTILogger.info("    when expecting object of type LMProgramEnergyExchange only!!!");
		}

	}

	return null;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonConfirm().addActionListener(this);
	getJButtonCancel().addActionListener(this);
	getJComboBoxProgram().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("JPanelCreateOffer");
		setPreferredSize(new java.awt.Dimension(436, 513));
		setLayout(new java.awt.GridBagLayout());
		setSize(436, 513);

		java.awt.GridBagConstraints constraintsJPanelDate = new java.awt.GridBagConstraints();
		constraintsJPanelDate.gridx = 1; constraintsJPanelDate.gridy = 2;
		constraintsJPanelDate.gridwidth = 4;
		constraintsJPanelDate.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDate.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelDate.weightx = 1.0;
		constraintsJPanelDate.weighty = 1.0;
		constraintsJPanelDate.ipadx = -10;
		constraintsJPanelDate.ipady = -15;
		constraintsJPanelDate.insets = new java.awt.Insets(1, 5, 3, 6);
		add(getJPanelDate(), constraintsJPanelDate);

		java.awt.GridBagConstraints constraintsJLabelProgram = new java.awt.GridBagConstraints();
		constraintsJLabelProgram.gridx = 1; constraintsJLabelProgram.gridy = 1;
		constraintsJLabelProgram.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelProgram.ipadx = 7;
		constraintsJLabelProgram.ipady = -2;
		constraintsJLabelProgram.insets = new java.awt.Insets(16, 7, 6, 0);
		add(getJLabelProgram(), constraintsJLabelProgram);

		java.awt.GridBagConstraints constraintsJComboBoxProgram = new java.awt.GridBagConstraints();
		constraintsJComboBoxProgram.gridx = 2; constraintsJComboBoxProgram.gridy = 1;
		constraintsJComboBoxProgram.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxProgram.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxProgram.weightx = 1.0;
		constraintsJComboBoxProgram.ipadx = 23;
		constraintsJComboBoxProgram.insets = new java.awt.Insets(12, 1, 1, 17);
		add(getJComboBoxProgram(), constraintsJComboBoxProgram);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 3;
		constraintsJPanel1.gridwidth = 4;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.ipadx = 267;
		constraintsJPanel1.ipady = -1;
		constraintsJPanel1.insets = new java.awt.Insets(317, 1, 0, 1);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanelHourOffer = new java.awt.GridBagConstraints();
		constraintsJPanelHourOffer.gridx = 1; constraintsJPanelHourOffer.gridy = 3;
		constraintsJPanelHourOffer.gridwidth = 4;
		constraintsJPanelHourOffer.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHourOffer.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelHourOffer.weightx = 1.0;
		constraintsJPanelHourOffer.weighty = 1.0;
		constraintsJPanelHourOffer.ipadx = 403;
		constraintsJPanelHourOffer.ipady = 288;
		constraintsJPanelHourOffer.insets = new java.awt.Insets(4, 5, 33, 6);
		add(getJPanelHourOffer(), constraintsJPanelHourOffer);

		java.awt.GridBagConstraints constraintsJLabelArea = new java.awt.GridBagConstraints();
		constraintsJLabelArea.gridx = 3; constraintsJLabelArea.gridy = 1;
		constraintsJLabelArea.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelArea.ipadx = 4;
		constraintsJLabelArea.ipady = -2;
		constraintsJLabelArea.insets = new java.awt.Insets(16, 17, 6, 0);
		add(getJLabelArea(), constraintsJLabelArea);

		java.awt.GridBagConstraints constraintsJTextFieldArea = new java.awt.GridBagConstraints();
		constraintsJTextFieldArea.gridx = 4; constraintsJTextFieldArea.gridy = 1;
		constraintsJTextFieldArea.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldArea.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldArea.weightx = 1.0;
		constraintsJTextFieldArea.ipadx = 144;
		constraintsJTextFieldArea.insets = new java.awt.Insets(13, 1, 3, 6);
		add(getJTextFieldArea(), constraintsJTextFieldArea);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	for( int i = 0; i < getEExchangePrgs().length; i++ )
		getJComboBoxProgram().addItem( getEExchangePrgs()[i] );

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 11:20:51 AM)
 * @return boolean
 */
public boolean isEditable() {
	return editable;
}
/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	exit();

	return;
}
/**
 * Comment
 */
public void jButtonConfirm_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	//tell our table that we are done editing it
	getJPanelHourOffer().getJTableHour().editingStopped(
		new javax.swing.event.ChangeEvent(this) );

	int res = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you want to send this new offer?", "Offer Confirmation",
		javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE );

	if( res == javax.swing.JOptionPane.OK_OPTION )
	{
		com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg s = new com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg();

		s.setExpirationDateTime( getExpireCalendar().getTime() );
		s.setNotificationDateTime( getNotifCalendar().getTime() );
		s.setOfferDate( getOfferCalendar().getTime() );
		
		s.setYukonID( new Integer(getEExchangePrgs()[getJComboBoxProgram().getSelectedIndex()].getYukonID().intValue()) );
		s.setAmountRequested( getJPanelHourOffer().getTarget() );
		s.setPricesOffered( getJPanelHourOffer().getOfferPrices() );

		if( getMode() == MODE_REVISE )
		{
			s.setMessage("Offer revised by a TDC operator named : " + com.cannontech.common.util.CtiUtilities.getUserName() );
			s.setAdditionalInfo("Offer revised for program : " + getEExchangePrgs()[getJComboBoxProgram().getSelectedIndex()] );
			s.setCommand( new Integer(com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.OFFER_REVISION) );

			if( offerData != null )
				s.setOfferID( offerData.getOwnerOffer().getOfferID() );
		}
		else
		{
			s.setMessage("Offer created by a TDC operator named : " + com.cannontech.common.util.CtiUtilities.getUserName() );
			s.setAdditionalInfo("Offer created for program : " + getEExchangePrgs()[getJComboBoxProgram().getSelectedIndex()] );			
			s.setCommand( new Integer(com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.NEW_OFFER) );
		}
		
		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(s)	;


		exit();
	}

	return;
}
/**
 * Comment
 */
public void jComboBoxProgram_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			if( getJComboBoxProgram().getSelectedIndex() >= 0 )
				getJTextFieldArea().setText( getOwnerAreaStrings().get(
						getJComboBoxProgram().getSelectedIndex()).toString() );			
		}
		
	});

	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 12:40:31 PM)
 * @param field com.klg.jclass.field.JCPopupField
 * @param cal java.util.GregorianCalendar
 */
private void setDateValues(com.klg.jclass.field.JCPopupField field, java.util.Date date) 
{
	field.setValue( date );

	if( field == getJCPopUpFieldExpireDate() )
		getJTextFieldExpireTime().setTimeText( date );
	else if( field == getJCPopUpFieldNotifDate() )
		getJTextFieldNotifTime().setTimeText( date );
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 11:20:51 AM)
 * @param newEditable boolean
 */
public void setEditable(boolean newEditable) 
{
	editable = newEditable;

	getJTextFieldArea().setEnabled( editable );
	getJComboBoxProgram().setEnabled( editable );
	
	getJCPopUpFieldDayOfOfferDate().setEnabled( editable );
	getJCPopUpFieldExpireDate().setEnabled( editable );
	getJCPopUpFieldNotifDate().setEnabled( editable );
	getJTextFieldExpireTime().setEnabled( editable );
	getJTextFieldNotifTime().setEnabled( editable );

	
	getJPanelHourOffer().getTableModel().setEditable( editable );
	getJButtonConfirm().setEnabled( editable );
}
/**
 * Insert the method's description here.
 * Creation date: (8/14/2001 10:59:19 AM)
 * @param newMode int
 */
public void setMode(int newMode) 
{
	mode = newMode;

	//do not allow the user to change the ComboBox if we are Revising an offer
	getJComboBoxProgram().setEnabled( getMode() != MODE_REVISE );
	


}
/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 11:44:41 AM)
 * @param offer com.cannontech.loadcontrol.eexchange.datamodels.OfferRowData
 */
public void setOfferData(com.cannontech.loadcontrol.eexchange.datamodels.OfferRowData offer) 
{
	offerData = offer;
	//set the values of the program and area name
	getJComboBoxProgram().setSelectedItem( offer.getEnergyExchangeProgram() );



	//set all of our Date and Time fields to the correct values
	setDateValues( getJCPopUpFieldDayOfOfferDate(), offer.getOwnerOffer().getOfferDate() );	
	setDateValues( getJCPopUpFieldNotifDate(), offer.getCurrentRevision().getNotificationDateTime() );
	setDateValues( getJCPopUpFieldExpireDate(), offer.getCurrentRevision().getOfferExpirationDateTime() );



	//set the values in our JTable
	double[] target = new double[offer.getCurrentRevision().getEnergyExchangeHourlyOffers().size()];
	double[] offerPrice = new double[offer.getCurrentRevision().getEnergyExchangeHourlyOffers().size()];	
	for( int i = 0; i < offer.getCurrentRevision().getEnergyExchangeHourlyOffers().size(); i++ )
	{
		target[i] = ((com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer)
			offer.getCurrentRevision().getEnergyExchangeHourlyOffers().get(i)).getAmountRequested().doubleValue();
		
		offerPrice[i] = ((com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer)
			offer.getCurrentRevision().getEnergyExchangeHourlyOffers().get(i)).getPrice().doubleValue() * .01;
	}

	getJPanelHourOffer().getTableModel().setRowData( offerPrice, target );	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G83F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D445352000D1518645E764C702850C16D61420290F3EE2440A95AD7DC445BE7423A5AA3EE225CF3E96AC6A64BFE8C28261A74120D1C0204690487F8FBF01G892418C240288132495EE497B63B4B6ECDF673C3724E1939B3776E666EA64152E7BE8E774EB9B3E74E1CB9F3664C3977AED97FC9F0ECE454FAC2A2E30972D798C9C84485C2AE7F6F74C00E9BA20547914D5F27GEBC9E4D28CDC83
	01B6F2F6F8DC8AF972D88214A7C15978737038CDF01FCAA65AC3CF430D0FFEC682ADFB5A227F725F4F575F71FE62D93F7B92613A86A084F0F8DDAEE17F697B52947CDC854FA1B20A10854DD8A7EFDF0602AB02F28D40E80010E6D6FF96DCD7531E071794A9756AC78513653F5E911504FCB4BCB9E4363036954AB513BC903934857BBAC76544B65DD08E854041C7132C6A82381655DCEA595FE4EB4DEEB2D82DB62BACB51932A5495DE4B2D8DB246CF63364F216149C2FB1D8A54BE107E410252F1A1BA587A1A693
	721E3CAF0172A2A4874A4BA96E979B313F29F0BF9BE021407FE893620B4CB0DEG13190FF15F0EB2E58CF5DD8F10D5AFDA2313FD0CF108190DAD442C0EF1F45BBB7169F4BE6E2CC73DC7GAD61E2F8DC8640C1008600A1G9FD1BB387B3B3F43353CD1BEECEBED35D94B6D76C39623639FB623E4017B129220A838D3D2134DE1A4044DF5533EAC9DF97C81EBF7F9506D18CEC2F2793CCFBDB505AC1CFEA2AADEC7A78BC3F22202AEB2DDCC3B286A0475FD3F4F3E67DD7434CB4F3E3FA65A3E170E30C5476A749D7A
	7C2E2888054F8ACD5FBBAF61DC175139CE037BC845BE987EA845DBB4F8E6230FD31B41F2B75096DD6236D137064BD29DB306246C295655C3D0F8F9D4050549D0EF7116A5BA8DF987D8D81F71C216E40A8F566019ACF7D099319C86340C5670B8E6DF4167F14D8E0172B800D00039GCB81922CD04F4AED6C756FF68F5046EA8C8E79D01B59E20485455A8657EF042B64169D06AA2BD3B6D81B243386075950E8111CC429F3DFBD4A5668516EE754761B200FE312D5F298E4334D8A5C8D2D12ACB91C2C4EA375EB31
	8EAEE220561ADBA5682055CEB85FDF5627625C981C72993B91963ADA03511F2C47B9E9B71C850E40G6E19DE1E39007AAA0371578298059E8E1DC3FEA7A487380E1212EA1B6DD21B3D96C7C2C22D286747286FF043FD20156B782986442DC03A0D7369B837CE4327425C04629B9C9DFC0D9D5A04FEF91B0D5B4C1F6A384DD4A4CDA6D94F9F573599CA37A9B6E373DEBB5514A74A454A73B75679D8CB5BEC7AEBE9703E62CD1FFDC4FFCF7EEB44DC95B662DC8D83190381E6899D071E4BE4B6FB4860143815FE65G
	83130C2A46191C2B5378FE02F2458A79B7D07C8260C96C61F1BB6DDCF7E3B755F9557F3EE3692B3BD05A07D61EFFE59FCCC7771F47FDC9EA172CF205E4172CC638F1AA34874F236DA96B3659A65ABCC271F6D86707E1E1B9857E9767F1AD9AE459E1EEEC13255312AC1B2DADAA7D895A17332D512E3A885E575C73B8C61B4B0A5B30027B6DF9DCA3CDB62BD52213A51AA87467689A15ACD2AB88FD5856E615997E258BE86717248E45C73CFC81831FF603258D7D8D0B932699BE44B1B84C2DE0826832C8309D576F
	829167A054G8E639D992E81F63E96DDDB79DA5C56B9114C3A30BDCAEFAF891ADC9135444E5651AA3B3A9619CC4FB6684BBE1F7A8A856FE432666EC63D6A04AB0A7D5B1B10D7466570389C004A4B5C7EEB8CC867BEC6B17C43B60B0529D368F9DFE38A4E0BF439CD82570C4E6C0450DDC563BAAF5AD9C50F0CDE5D08FA34E87C60B6876A31B20D47028DA00F9BA068B25365024BCC972CDEE2BA2FE707F200C32D976250561BA56AAD03F2029356221355B3B9D9BD262B37F7617C51786F22215D604EF63A4086F9
	3047623BC37B931475946A64F15CB3E5284325D00E11E15F1379DC7FB60947F123AF8513C5BF5E6CB3D64D13199CF532BA57195773B86ED1A37699843490194F4F130D5A3D245C6934B51969B62668363DC9EB8B8C57557481DC1B4D8E27DC850B554D67E1E8934A0B7B1C02D6767D4B54FF21EEE6C23F4B81C636A17EC477FA0D6CD9002B85F083C4D3FE8B527662DA2606124DAEB512235966E8150CBBD1335986171CCD7DC6F6B9A59F417BDDFC9E16366F036B45C3ED32EC339E36D9C162568B65DEFC2AFDB3
	AA5DF51670586D1E54030A1CF3FFB606147526C66945528149DB226637A96B2DCD1D036E076222171E7FDBBC53C7FEBA57C7E5BB44506DDC9F1DD7747491564E4F95974DE51A71E088E39964F0D87FEF087E67826F98976F7F7DB6ED7FE5AE96BF98DDFCBEEAB6FF3F795811FEG6507682D51F648662EF158DAA0109A6C0C30FD6958FE9E033BDDCC4FA3DDEA3C906C6A9BBB3058B4FEBF0EBD5445E253E4015FC4718BB5F8262B67681A44F20C9B664A4D63295AE6DC632B201C5681BE8F209240849050415756
	EAE3AE69A7DE2D02F32143EA301C765810F85CE8C43D439E22149FB7229E699EC2787E6CCA73BE0BCC6A709C7BAC2FB2EBB73CCF3B0F3D4A91DE6D589CA61FCE5777ED4CAAD177F48F5BDF6C3D0721AD2D821EA91D20A76A6F2E6B66BEF0E7A777F5D70EEAFD5D4C643DD19F7A70F58D1DCC3EA11D6ABA4B92E75677DA50D74F825AC20065GA9DD61F1F9GB5GF2971F23FFC87B7A1BA3EAF9353643002B04BD6630F7ACA0F4DA1AFEF5F3D155271D77DC0C6E72EC47747760952E3F29DDBCA6FC6BAAE5F837DF
	99A23A746C214572113EBD70D52E48DA504E73992B008FBB271C27FBE0DEB74CC9B7477D2698F1C6A83B85EECCA96246E1391B0FB3E9BF4E6F82A8AF83C83C82FBB1C099C09D007D8A2F3705569B8965E9D7581E0DBEF59E5CAF87D85F436B3DD4E6D778E94708FC7C452FE5FE684B94DEF5DEF5382C2A6ED7503E3D4FA249FE7AC578508F0D1F85123C70585FEA220D3D4FB57B0E3D0D4D7A317745E67D585B5E3C1661010776844E696EE67F317910E63D58FC8B554DD39B13A9EF27D3F25285D9E1108D8A6DF5
	F983510951CF42A1DD51AB1C6353D6ABF50E483643FB449AEA2776067A4A6B61F15868AE1ACB0172B000F142C6EE6876DEA321C01B534366F2E1CFDFDBE03EE838997755E5C0CF6C8D0F5B86D086D05F4BFD504616FE7709A3EEBF7BC40249779CA61B7467F00389ED43E9EBF3B4C95E7314E1CA93B4555BD549D2AB2A145559CE5B4B86C70B54E71E33E8FBC67355BE5F644B9658FCB952357335D72C15072FF84FC1B768B1301769FED2EF5FB9E07CBE5F2C6517AB78BD5665AF0F986613DF5BFE9C3F5DA141A1
	88CEB4274D1DE8EBB1C350FEEE25FEEDAE34DD504BE327570A717CE7B1F4585AE4309BDA6F3B923EC7D6C23981A0GE05C906DD962C1972E73E60813C0BD276DD293899CCAFDFFA9EF3B8AF981AE87208640BE147779CEAE46696C54814BC56232FF5901329F2FB6B4CA96A58893637A7B37F88EBB6E9523B15AB11A9FE37C76707A694982D7899643CE05CA7F099F08FE4A614C487B58EFC5390F5702D59EB5CB96A323313821F63FDE5C30G4634F4A80FD327F84429B15731BABB614AF8CC6A5172A8D3685DF8
	3D0E737828D40FC708203F36DFCB1F2950A32E633E202B585BEED6DC275ACA62753E624F04EE74AD195733B8B3D660D3A9BECF03E7716718AEF1D6G5A546B791EF8B70D1DD20601BF9946F12FD3DC9114AB852E728A5FBBEB07F1FFF30B59373F98ED567797E34C7ABEBF586C5F670718FD2DF3A65FB3D778B85CC26635BDFC9CA3854E51434791A4F08B3AB8EEA6609207633325C93815AAF6B63BB8932ED64915A9CE9495E6B847E212F5E7DEEEC1D6E67E1E5CDDAC6E8CF6E91F8795FDD0281BCF21B6B91C4D
	2DD1394E3CE12BB2D75717707C5DB8204D9F4E4FEDFB9AB08691B1928AD2D2F24CE1B69EB234D4D30F40762227CB92147609A378DE3412722C00F2BD00BC0247496E4FF91CBCF54A98D2BD6404EECEE0D16D5E2811A3146798A354B8B98462E456670B253C4F25C05FF68362E24B34360F3A0C0EA1A49B2E0CDE68C147795DCA34F433879D65B5C7EB69E3BD682823E225BD9B5B535B7958D6BD9DC2ACB30AFD66BBACB730B185AA571A33DB49361B7E260CED2448314C39016B33AE9575C982605C861093406768
	6DE69FB99B6A3D983F175BB83FCA51AEB6B50F8C6AD91FF0120C67429E9E978F8960B21A681C07C1F99E4012GFEDEBAD54A7BA7B7727E376F4C5017DBF8D756F64B819E67945D48574FEDA256368B5CAE91EB0F3E117B3AB5542FC4C0195C44F1699407B9B54C17B1DCF0172FBC3BB7A02F0154C340BE55D87967836EE3B0397DD8CD1BD4B2030E2D55AAC1B0E6EEE2FB7ADC0AFB92A063A63E3FF61C419C2F8C0AE63EE7B63ABD12F593563FF0A54DCFFF0C67DF2EF01E72CD5CFF3D6ED4F95C58630FC7982D73
	D6D7C5BF630A5640B0E8938430D521CD057B5D4A5AE2EB6FB75FE97DE04A18CF23A2FD2CBDF2B31B17441BD53F42625D17E49C57C140575F4C771CB76CDF40752C6122925D9EB11AE51B0306370004B043AEB9648E062559DDE53EEFAC393A6743F7DEE53BF903EE47766F56D2DC335DB7337DBBD2605BA9BEC003E7FB457BE22F988D3495B7733C52876DB86775A3E1BF8488848881188390B1126BF26F659C524FF92156E62F068D5E22591259789616DE5DF373476239CF8EFD749E92B6E2E5242F3C4C0A114C
	9E52EEE157A535DBA91E0DEF3C8B47D789B4A3C0GC0B0C0A8405CDB7878BAAFE7317121B9144B322149C4334FFCF0E7411F2AE88C0A5958C66D570EED9170DBFE0B6AE792EE759513252D274FAEEEE5F314AE70DFD2FC1D864F666EBC5DC330BC9AE88DBF627E6F8FED5A7E3382D9FF8715EBEDE05F7ED93B5F77781B9313CEBBA65FC2EAC35D015EB677AA458784FA5B1CD535B9202D8864F2FF6DE1937523D8FFF665BAEC14AF39BFBB60CF6EA0E8B7552BADBB777C27488D4E827A82GF2DBF85C7A5B14FCB7
	75EB87CB7D7935E82266B270BE8B5A9E3C0D79B5AC57427D26A8C25E07BF662BEF2E40F647BD1E85F0FF9F1F1C2421513CBA6134455DF85D5134C3D987F34EE9E3134CAFE2F4E3139567F6C7B55C26686FB655BF4E3F38AA1E71CD6A61FCBF21E530CF427F169D680B83BFCB7707E7BA8861EB727709DC160C1F0CA595AF2E0C5C2AA38B5DAB949992EED7E5D9823230B579D8A7F2E3EB6F8BF9BB716127E1659D0790632B460BF0202FB53408B62F4848D344784CFDAB34D7E50C754CD633ECB6D84C1D745152E9
	28E8EDE1B664ACA5421E3AB5773125835D672F86D89F9FDF53BE5878D35BFD1F35B6376B1F35B25B754FDA1F377B4F1FE5377BFE361D5F2EFD364D667D59AE1C1B03E09375GC3GC683045C4E7D70636D1F916A07F5FC7091E38B660670CD9A1977BF4565FB2CBFF5757F7E4D43AE4C97B45AE09936A2FF0E1F69427912EDF60AE43847DC28A3073945A4332A7CDD9A972E990B54CC51AC268D21670059B02E853773B8B723GF12B201CB21A6346967258B7EFB4770D9F2F473DF698140381E683C4G10BB4263
	B2G0AG6AG3A818281A6814C3A03374D25ED17C0B961BF4263F682D48218G82GC2G66GAC81C8389368G15F772364654D99ADBC27EF88DD67A46FB14052E7DBBFC4752C5FEE8F5DE34C9BAF507A9637545E316CE1BC08DCE0945BC46122ADC519EBF5454432BF73EDA8B7E7261EB3D4A1BF46A203FDD6D07C73A8EEE03661E5948CAFA86B441BC396F64F653F8C664AB846E49B3A2DFA1F0ABC48EE3091867BF14902127BFEA6E6512EB6F7FEE3926FD30B1DB69F932A888ECBB086BE1FBAC626A21EC9138
	1BE239EE5C82D7FC006366881CE1A362526E8A0F4B82E0F14D62E29E579C841C7BAEFEB64E5D6B712C1265DD0EDAEC2EEAA58D2BBC837822041F41275D454F606DB4D6DA82E5B2A6BCAE8DE0E07B743283EC1FB6DCA1047B59D2FA0E4B819E55GF600G00F1G21E3382F7D5A3D1378BE9FB101293FF5984CD619711D500BB1529C60918110B0B6BCAE93A08F209A2081C08688G88868883188B3088E0B9C04CB89897C056B8AE433A4EDDFEE4D092D62A90047B5C973AFC7B7D173B7A777B2F51BC1E847E3A36
	432EBC9F78E397664BED4D4DCEC96CAF4B3AD2842E4AD9E3F31AB11AE07154AF77E37DC61B03A69FD91C790248A59F04B156818482CC824067012CDDEEC15F3C7C71825EEE66B816171F8F57A5CABB961FBED458DFFC0AB64863D3B29E625B71EAFC3A936E5F0458F43108CF732F503CBB4DB69DF15B4D8E89B3760C7668E954617143B5B67B99BB4D6928B5F81C6A6C443DDC4D7A309A7826E934E22349027FE9996283A4A9ED5F4ED07BBEE9134D4D9DEA5F1BF7A4FB52B46DD62524FA7469CD2FCA5B60A5B72D
	4144D66A1CB899AF7873E78ABF516076AAB813D91503264FA897C6BBDF39592B1F8AC347D74DEAB542F27A36755EB9FF1C4F3C71BC67FFFC1F16DEB91E513B855D50212587AA74F902EEAC52639FA168F71F54232F90740905FA74443BB9DDAA566BBFD3505FCA5113BFCF50C79568712F9474C65D7E6B857DD7F9FAF43B20E7676975BF6CEE3E56D25AB1DF56EC334AAC7F75B9FDA7A08868136E66792FAEF463CAEE6B15B37D2DB1AD84E9F2DB48E4BE70C41BF997569863175CBB18B365A2E83F7CEEF54DA6CC
	880F5B81B765D15CF60717FA0EE7AC5D6B56C33D4C89FC4CE5311EB94269D16A78A6467A13A73E1F7155CCE04F825471559C7047EF35973F7A891E39C0BB14BF0DD45FA7CEA56E0DA803E4C5E7EDA4EC0CBF5A481F97F4CF60B14C9BFD1E058DAC7E786BD5364B71485185CE68BF174C72A27FC37774C9933C73A5EFD37C34895E7912EE11AF1909F39A4C77490A9E5F7B5D091E7E773B5A9E7DF34E37BDFD4FB997FA744FB9D20F761C43F053FBFD4B75E8EF7FF2BD5E2BAF57D33DFD65FA36D7DF2E793DFD65FA
	29976D2B856207BAD16F95017BC66096D31E087B5AEC14CD827F86158B711592F64871EFD139DC94D7276016EBF05FA838153D38474310CDB687EF7BDEAFDD3BE60B0515238856B1DBA134907A08276B42ECB7E864CE9438EF041D3C371FFFE3B38CECE4E6B00FBFDF5F0271145E332116B6F3F61BEC361414541AE50BF4C889DB14F87428F8D633BC18472366FC6CA3EBE2F8DC85C07DC49E7BBA8A380D4A001BBA11BFABFA34002F5308091C4792DAB7639E5893G2A6F99ECCC7B828DE5987FC46A87505E0C77
	70711A259D5A71DE3234E4DFEC428CC6F6B3EE4A78D298EC51EDEC0BD6788C955F394D91FC7E3EE5CF3FFAD3238E8CB80ED8E57CECC56199544175C7FE4C75B13688F1F85EAC923867CFF25CB801DBD0481F314D9738F145DC4644C99C7779DA0E2B9038B9853CED37405DA578058A5C3BF9DCF7CB85CE4A637CEAA6733DB019267918AFBEFB86F3B19E4F320F02D2F6FEE6B49BAC36164F32B2F332B2776C5D457304FBF1498CB0374867778C2AC2E99F1922761D9EBB383E372D7555B7239FDB27721E32916729
	59E0F1228FE1E73CE374BC96B019598373794BB4E763151A7B6B7E8D6764317F06BEE27C66529102A76B3D37392E5FF65E6FF132F6A7E83B191387769CF69E54E3FB654B548FC5CC765EC3DF2378E5133D77D0DBBE77CF2B0056B005478C6E022B5B7BC7DCE53BDF8E3A9D9BD7FDA14ABFFB0A77F8A50A1FB745FB3CFF927EF8A1508E5E4B473BD6779B34C977B27D07A9573977761D0FC5DE3865D0CE88890F4B88714E330D707BCDECCE082F67B863BB3C5BD5E86A5679EC97DC24B7A60BD2FF08CEBB8E233DE8
	A1D01E9922E75F3FBB2915ED1ED2A7C2392ED02E0977C1FCF61F76BD01178B356D7294DA25F22D3F4F5718A67A0DC96DBE5BFD3156DF3BE1BE5BBD63E117C1CA3D294AF5164FF6F7FB0CEF3ED2EF09F2DD6533DDD43E5E1C255C2FFC6724DC0B6E6F5B3E5A8B57GE599A060FEEFBB3C1867CF9763F4F8AB6778925C708C96F30B951FB540BDFB3FF7D6AA66054DD60B59AA9DE6DF62923ED7B452D8E99A701CF3BF4F917E196E258B213C6CFE3E7E966793A15FD24DFD1B2EBE7ED5407A38755F50C7E321BA26CB
	1A7B6585573E6F2442EB1FC74E257166AA184F441F70F98EE239E0A867885C932790D781E51340251FC6DCB714C78ADCC3B64FAD878B5C37746C328C4A89D3B96E55B244E5C039CC60466F63EDEB84AE14461C41D08E93383FF572FA8B85AE3C080FA36B279C3735180F23C860963B384C558277678A4E2FC160E639384C5354FAB42E1D8D65798257130E38A5D0DE2176114E79457C0C63D696F07D558ADCC5A15701C960169772BE3A856E8F113C0FC001BB9C457B98A7F0E1459C37D4608E2D65383287B8AE22
	0077B1D2601E93638896383C4D3C6DD4011BA164DBA5F09D1B7B368DBA475B26CC6338ED7908EB0032DD60EE2438EEA80F963869EDDCD7A182779E454D0472DC019BA61E85AD9238E534DE4E0361F1158FF25CEFF3391E6B85EEE89EEFEBFFD03CF7F6G7D240CA7C70C4389FF1FEC94750533205EDCD1B7A1BF1657A5BCBE3F08E13962F30FEF744CCF5D32DE25BD30918F842D863739352D153E614E7AF9122DC568A361A15E4F42D318DF0083363993C4B2D870F846B374DC3E3F1E925FDA08F6B927BDF3E8C7
	69DAEDG3A65A11EEBF7E58FA4EF67ABCFB68E787CD3C3CFA517FB69384CD62FF15DC857FF88548F95722DAACB7598D739581DD81B0D549760FCAC9272066E6B6F1DBC2D3C0953BD73F0195355F772188CCFF7203CA6F30B4902E98D4921BC27D85C411FD3E4C11B0A694AF710535933E6A64BBA3F6F8746FB49B249CB16B0A85F4E652476B18DD37A4A582B69BA1E8DF56681B0DC221B5A861493C2398C29C73ECF1E3388783C2CBCA7E1FD3CC1FDDE8560EBC2051DFF65E9C79FFFC557AB5083C41DBB52BDED7D
	B9B4D9A5977BCE7AC01E677812B18C7A28A2AC974B7A7F2C105B7A6CD02E1B56C2EEAB4B84EE8B5D8FB2E6401C4D60677E3FECD2657AB6EAB06FF7FA4BD587BCBFA55A9C71EF0A7B1B8BADCF638CCFFBF0C3F9949FB365774D5A7E6453729B6645AFE806EA6BCC9FC9D4C713GBFCD68A3BDDD5547094D7D65387D7902C5B3583DBACF2F507E1682FE458CEE2F8F1C6B2F8FFF363073E1358FE6F725791EF6F78C9FD7A9EBBBB8DF5F4706517DA68FF89DFC185B4A42B6EED3160739CDEF70FA267285FB268274C9
	8F73710C6D77B94AEABF63197F30377F0CEF57771F1F503E97C17D25C266F7F2394D27BD4265894A6B4FE63472D4BC62E9B3F50F28B6236CD91F297AFCB58F6DDDA7D7D56EB09BAC1FE5E56E01FFF93BC45BE9DB55360F1E7A583B2D7A3D485EE2E8D8300B57DDF39A73DB1AD76E33CF637F7C15A8E7F675112335CA9F23BED77B284D9EDCAEAD94D32A4A7D0BBBD5BE3FAB9B382CDF6E4B74AF6B292AE315353E73F56F203A147C46071DBC57ED04F99877884F59BEFB993F61707E8EA15BEEB1B4E7B79A1C6626
	1212C3F8413860CC55B6FADF29595188E1B96ACA4AFFBE70DD8A9093261F23E6F2ED79C2554D41ACD5B78BDCFEF4134FF5435A5D1B2D367B645060EC67573B557E5F4CD1798CBDB9707EEBF355F60E744159C6F7115A3638F0A076486A8EAB9C08BD30BE46142AFD3C90B9B87D7C82DFCF751387DE344FBFBD7A002A5F8B652A9CCF8ED2CF579DD2793CD12172E95DBCB8BE1B0E2AFCCA0E29FCC25A863E965FEE9B385FF822CA6D43BD487C7F12632A2C9FD62BFCFE15BBF0D95766FDBF3F117D8FACA9794F4E2B
	4BEFFF52EFBB6F7CB64B4F4E24FEA4A34CBBEF7B84456F8C734E5B1E6C64F95BBC208505713CD1F827BA0627B577351D57BE2F52FACD7BE03F5F39B2A183B67329EF12D827032C6BEB9365373E7462548B956B23C8D49509B351672AD5A456058FE85DF6C270732048D01944AB6F544BA40E3DC18F6C12853BAA7ADCF5D1FBEEC974629FC7C7119860451EB3D71198FCD88CAC9D0CF534EC33A3E34267879FA6A5BAAF139E92235817C2095CA7138465836592194462E744A522A28F597131EEA488136431EE1E18
	187F5A5CD74EBA2389446D6998BE1D169C748DF549083FAC207C945FD1325AEEB3839170A02913DEE21DF207C52AA66B1BCF61CFD2B9E549D84E5F49F64AE4837D7D1A5D7818F2F72541E9127199FBE9B50931620F8612F50CECB1D8DBF6339761CB59F04B8F5241514F93C15ED4AAAFF24996DCD27FB3F97B2747A34222C954D1720145E6B09EB5B449B6C795D9AD1B9C1253E433985D76830CD1020BBF5D0C86F69F096163E033F19B96C2DFF20F9F1934A469E89484788E321B5644E8B5DB54B09EB6999C5015
	64281271C790ED0EED02B674E37973E70EED2A0FA2114AA0F18A925DF6B005E6C32B595281B79554CB5149C911C1DA07416A441F7B8149B23DA463A451C1766DCF655CF15FBB5F528E5426B2790429921A6EEE11EC2D3B4B1B1B4DD6C9B0G3D827BAD02BD5649762A334EBDEA4371675FEF84BB28A0512DB9B9747FBD747F3D787F9E0A59C3B1FB7602F217913FFF5EF4B79FB3B59E31A8F07A330F428C4A3FFB7329FB7E3BEC59D2985DD192CBFF5C87A6A1C52DF5322D35D1F20C18FA47168FC7AF3A9967480E
	2662C63E6B5D425E63943701FFA9827B29E69544D06C87FDEC8D31EB7A9A8D22570929435286DAF5937DFF732095012DB2E85B0F057C5BBD65DF5BC7FEDC45118AB5C9A6EB8E37B941E9C80ECCFC2F8F96DBBCFB4F9116FF8C3E87G887682A3A2308AA0F6D6D61634366E12C9D20521A3732B66CC7ADEE009C6998D34D3740911BE149163298CBE27F16CBD93B6A8EC732F55D203D24EE3DA221A8158F2F573E83C0673385FF39E79D69A2FA3B71FD5FC8F8E0FF9B1829B23E0688FA0F834AC7DBE130525FC7C2F
	705A4CCE15BAF67C43C7D6EB05F4FAA3E0727E3F2F3DFF2AF2A790F54EE2FF483A2EF3FB58907EACCC01108E31BCF900569525EBEF11BF3C19B0F84E84CA0A41FE428FCFD2133E241E2B7587681F6C2A5C0924FF7F44AD087AA7762D652EDE6DF771AB0627797CFD10F93315F794E6FBFF973FA289871D79C4F8DC59937CDD524A24AF096F6FADCF379AAC9659E039D4EEB562371707BAE4C97D5E6F5DA4646F64F594A223452CC139AFEA5A7C9FD0CB878836BC332A7C9EGG8CE0GGD0CB818294G94G88G
	88G83F954AC36BC332A7C9EGG8CE0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB69FGGGG
**end of data**/
}
}
