package com.cannontech.common.gui.panel;

/**
 * Insert the type's description here.
 * Creation date: (3/12/2001 9:57:47 AM)
 * @author: 
 */
public class ManualChangeJPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
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
/**
 * ManualChangeJPanel constructor comment.
 */
public ManualChangeJPanel() {
	super();
	initialize();
}
/**
 * ManualChangeJPanel constructor comment.
 */
public ManualChangeJPanel( int displayMode) 
{
	super();

	mode = displayMode;
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
	if (e.getSource() == getJButtonOk()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxNeverStop()) 
		connEtoC5(e);
	if (e.getSource() == getJCheckBoxStartStopNow()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JCheckBoxStartStopNow.action.actionPerformed(java.awt.event.ActionEvent) --> ManualChangeJPanel.jCheckBoxStartStopNow_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> ManualChangeJPanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC3:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> ManualChangeJPanel.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOk_ActionPerformed(arg1);
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
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('o');
			ivjJButtonOk.setText("Ok");
			ivjJButtonOk.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setPreferredSize(new java.awt.Dimension(73, 25));
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
			ivjJCheckBoxNeverStop.setMnemonic('n');
			ivjJCheckBoxNeverStop.setText("Never Stop");
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
			ivjJCheckBoxStartStopNow.setSelected(false);
			ivjJCheckBoxStartStopNow.setMnemonic('s');
			ivjJCheckBoxStartStopNow.setText("Start Now");
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
 * Return the JLabelLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLabelStartHRMN() {
	if (ivjJLabelLabelStartHRMN == null) {
		try {
			ivjJLabelLabelStartHRMN = new javax.swing.JLabel();
			ivjJLabelLabelStartHRMN.setName("JLabelLabelStartHRMN");
			ivjJLabelLabelStartHRMN.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelLabelStartHRMN.setText("(HH:mm)");
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
			ivjJLabelStartTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStartTime.setText("Start Time:");
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
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOkCancel() {
	if (ivjJPanelOkCancel == null) {
		try {
			ivjJPanelOkCancel = new javax.swing.JPanel();
			ivjJPanelOkCancel.setName("JPanelOkCancel");
			ivjJPanelOkCancel.setLayout(new java.awt.FlowLayout());
			getJPanelOkCancel().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelOkCancel().add(getJButtonCancel(), getJButtonCancel().getName());
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
 * Return the JTextFieldStartTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStartTime() {
	if (ivjJTextFieldStartTime == null) {
		try {
			ivjJTextFieldStartTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStartTime.setName("JTextFieldStartTime");
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
 * Creation date: (4/18/2001 2:12:22 PM)
 * @return int
 */
public int getMode() {
	return mode;
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
		return new java.util.Date(com.cannontech.message.macs.message.Schedule.INVALID_DATE);
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
			com.cannontech.clientutils.CTILogger.info("*** Received a bad value in getStartTime() of " + this.getClass().getName() + " : " + e.getMessage() );
			return new java.util.Date(com.cannontech.message.macs.message.Schedule.INVALID_DATE);
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
	if( !getJTextFieldStopTime().isEnabled() )
	{
		return null;
	}
	else if(	 getJTextFieldStopTime().getText() == null
				 || getJTextFieldStopTime().getText().length() <= 0 )
	{
		return new java.util.Date(com.cannontech.message.macs.message.Schedule.INVALID_DATE);
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
			com.cannontech.clientutils.CTILogger.info("*** Received a bad value in getStopTime() of " + this.getClass().getName() + " : " + e.getMessage() );
			return new java.util.Date(com.cannontech.message.macs.message.Schedule.INVALID_DATE);
		}
		
	}
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
	// user code end
	getJButtonCancel().addActionListener(this);
	getJButtonOk().addActionListener(this);
	getJCheckBoxNeverStop().addActionListener(this);
	getJCheckBoxStartStopNow().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ManualChangeJPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(315, 204);

		java.awt.GridBagConstraints constraintsJLabelStartTime = new java.awt.GridBagConstraints();
		constraintsJLabelStartTime.gridx = 1; constraintsJLabelStartTime.gridy = 2;
		constraintsJLabelStartTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelStartTime.ipadx = 17;
		constraintsJLabelStartTime.insets = new java.awt.Insets(2, 10, 2, 3);
		add(getJLabelStartTime(), constraintsJLabelStartTime);

		java.awt.GridBagConstraints constraintsJLabelLabelStartHRMN = new java.awt.GridBagConstraints();
		constraintsJLabelLabelStartHRMN.gridx = 3; constraintsJLabelLabelStartHRMN.gridy = 2;
		constraintsJLabelLabelStartHRMN.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelLabelStartHRMN.ipadx = 7;
		constraintsJLabelLabelStartHRMN.ipady = -2;
		constraintsJLabelLabelStartHRMN.insets = new java.awt.Insets(5, 2, 4, 61);
		add(getJLabelLabelStartHRMN(), constraintsJLabelLabelStartHRMN);

		java.awt.GridBagConstraints constraintsJLabelStopTime = new java.awt.GridBagConstraints();
		constraintsJLabelStopTime.gridx = 1; constraintsJLabelStopTime.gridy = 5;
		constraintsJLabelStopTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelStopTime.ipadx = 18;
		constraintsJLabelStopTime.insets = new java.awt.Insets(1, 10, 1, 3);
		add(getJLabelStopTime(), constraintsJLabelStopTime);

		java.awt.GridBagConstraints constraintsJLabelLabelStopHRMN = new java.awt.GridBagConstraints();
		constraintsJLabelLabelStopHRMN.gridx = 3; constraintsJLabelLabelStopHRMN.gridy = 5;
		constraintsJLabelLabelStopHRMN.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelLabelStopHRMN.ipadx = 7;
		constraintsJLabelLabelStopHRMN.ipady = -2;
		constraintsJLabelLabelStopHRMN.insets = new java.awt.Insets(3, 2, 4, 61);
		add(getJLabelLabelStopHRMN(), constraintsJLabelLabelStopHRMN);

		java.awt.GridBagConstraints constraintsJTextFieldStartTime = new java.awt.GridBagConstraints();
		constraintsJTextFieldStartTime.gridx = 2; constraintsJTextFieldStartTime.gridy = 2;
		constraintsJTextFieldStartTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStartTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldStartTime.weightx = 1.0;
		constraintsJTextFieldStartTime.ipadx = 86;
		constraintsJTextFieldStartTime.insets = new java.awt.Insets(2, 4, 1, 1);
		add(getJTextFieldStartTime(), constraintsJTextFieldStartTime);

		java.awt.GridBagConstraints constraintsJTextFieldStopTime = new java.awt.GridBagConstraints();
		constraintsJTextFieldStopTime.gridx = 2; constraintsJTextFieldStopTime.gridy = 5;
		constraintsJTextFieldStopTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStopTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldStopTime.weightx = 1.0;
		constraintsJTextFieldStopTime.ipadx = 86;
		constraintsJTextFieldStopTime.insets = new java.awt.Insets(0, 4, 1, 1);
		add(getJTextFieldStopTime(), constraintsJTextFieldStopTime);

		java.awt.GridBagConstraints constraintsJCheckBoxNeverStop = new java.awt.GridBagConstraints();
		constraintsJCheckBoxNeverStop.gridx = 1; constraintsJCheckBoxNeverStop.gridy = 4;
		constraintsJCheckBoxNeverStop.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxNeverStop.ipadx = -1;
		constraintsJCheckBoxNeverStop.insets = new java.awt.Insets(4, 10, 0, 3);
		add(getJCheckBoxNeverStop(), constraintsJCheckBoxNeverStop);

		java.awt.GridBagConstraints constraintsJPanelOkCancel = new java.awt.GridBagConstraints();
		constraintsJPanelOkCancel.gridx = 1; constraintsJPanelOkCancel.gridy = 7;
		constraintsJPanelOkCancel.gridwidth = 3;
		constraintsJPanelOkCancel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelOkCancel.anchor = java.awt.GridBagConstraints.SOUTH;
		constraintsJPanelOkCancel.weightx = 1.0;
		constraintsJPanelOkCancel.weighty = 1.0;
		constraintsJPanelOkCancel.ipadx = 34;
		constraintsJPanelOkCancel.ipady = 1;
		constraintsJPanelOkCancel.insets = new java.awt.Insets(6, 61, 6, 59);
		add(getJPanelOkCancel(), constraintsJPanelOkCancel);

		java.awt.GridBagConstraints constraintsJCPopUpFieldStartDate = new java.awt.GridBagConstraints();
		constraintsJCPopUpFieldStartDate.gridx = 2; constraintsJCPopUpFieldStartDate.gridy = 3;
		constraintsJCPopUpFieldStartDate.gridwidth = 2;
		constraintsJCPopUpFieldStartDate.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCPopUpFieldStartDate.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCPopUpFieldStartDate.weightx = 1.0;
		constraintsJCPopUpFieldStartDate.ipadx = 37;
		constraintsJCPopUpFieldStartDate.insets = new java.awt.Insets(1, 4, 4, 57);
		add(getJCPopUpFieldStartDate(), constraintsJCPopUpFieldStartDate);

		java.awt.GridBagConstraints constraintsJCPopUpFieldStopDate = new java.awt.GridBagConstraints();
		constraintsJCPopUpFieldStopDate.gridx = 2; constraintsJCPopUpFieldStopDate.gridy = 6;
		constraintsJCPopUpFieldStopDate.gridwidth = 2;
		constraintsJCPopUpFieldStopDate.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJCPopUpFieldStopDate.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCPopUpFieldStopDate.weightx = 1.0;
		constraintsJCPopUpFieldStopDate.ipadx = 37;
		constraintsJCPopUpFieldStopDate.insets = new java.awt.Insets(1, 4, 5, 57);
		add(getJCPopUpFieldStopDate(), constraintsJCPopUpFieldStopDate);

		java.awt.GridBagConstraints constraintsJCheckBoxStartStopNow = new java.awt.GridBagConstraints();
		constraintsJCheckBoxStartStopNow.gridx = 1; constraintsJCheckBoxStartStopNow.gridy = 1;
		constraintsJCheckBoxStartStopNow.gridwidth = 2;
		constraintsJCheckBoxStartStopNow.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxStartStopNow.ipadx = 16;
		constraintsJCheckBoxStartStopNow.insets = new java.awt.Insets(6, 10, 1, 87);
		add(getJCheckBoxStartStopNow(), constraintsJCheckBoxStartStopNow);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	if( mode == MODE_STOP )
	{
		getJLabelStartTime().setVisible(false);
		getJTextFieldStartTime().setVisible(false);
		getJLabelLabelStartHRMN().setVisible(false);
		getJCPopUpFieldStartDate().setVisible(false);
		
		getJCheckBoxNeverStop().setVisible(false);
		getJCheckBoxStartStopNow().setText("Stop Now");
	}

	getJCheckBoxStartStopNow().doClick();
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
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getMode() == MODE_START_STOP 
		 && getStartTime() != null
		 && getStopTime() != null )
	{
		if( getStartTime().getTime() > com.cannontech.message.macs.message.Schedule.INVALID_DATE
			 && getStopTime().getTime() > com.cannontech.message.macs.message.Schedule.INVALID_DATE )
		{
			if( getStartTime().getTime() >= getStopTime().getTime() )
			{
				javax.swing.JOptionPane.showConfirmDialog( this, "Start time can not be greater than the stop time, try again.", "Incorrect Entry", javax.swing.JOptionPane.CLOSED_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE );
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
	getJCPopUpFieldStopDate().setEnabled( !getJCheckBoxStartStopNow().isSelected() );

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
		ManualChangeJPanel aManualChangeJPanel;
		aManualChangeJPanel = new ManualChangeJPanel();
		frame.setContentPane(aManualChangeJPanel);
		frame.setSize(aManualChangeJPanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G52F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DF894C51660A232020A9A9491A52B38E6F7D9F5D76E64F6F35FE2163DC30D4A5D62AE1E3993DDCEF1CF6F63DBF39E1E6CDA19A493FE02A08990A040047093A4971210109002648FC8A0E2A491D1321AA653498C19191E74F41289C439772A3A2AFBFEC358733B4B773DF4572BFA3FD5756A3D6E9E123DBDB6BD4512CDC8CABA517FAAD288D9D4C748817BCDF7F05CA8B941CACCFFF9G6F13FFADCB
	036BC4681B70E002F5ADB93DFAB93427C3FBF1EC02F5A35CE711D7EC150761A6021C3550F77ED739A7234B191F4D65E4507682CFA6DC5F8308839CDE13E5143F4413236393F5BC07948BA1733BF04CAAA5D747D5C23B85E0A24074AEB6BE9E2EAB28646B2BAA75F16D37461264A777D8D6A39F93CF8E395D0C36C63F5A4843A90B3BD1D61912295378217D88G9A1FCADE58DC82576A161E6E8FBB95F7D1276471A89ECD6EF494F577B90B3C12C7F6D5D5FDA4F97AA4D73DC372F44BA7DB9087B4F687729DD216D3
	BE0494C2FB89455DFE90E545417DEC0085827FC585624B1C8956E6G3B135BF7E4CF352E7F394B8F13E53F75242E09E05F101359B55DE958B7AF751D0CCDF4AE32B7204F97C2DF5A4584EB8EC0B5C0BB409040963A86362C7FG2E359734FA456DD6BC35DEEF1D4B2EFE2458E5975CD7D5C10F0EBBA3F7AA2A1D90B64FD5CAE198FD228123EB9CB19D73C95C81BE67B34EFC1FA4BDFA4A129146A7C9F14516180B4C97B3AE9ABEE132C7C7141DF8B1F0CD864A7E0C18E53FBED6C9CD8FA3FB66F3FBAD8BF5BE49A6
	59BBFBF02EB368DCE743FD0A3EBE987EFD0AF71870ECFD5EC5578C367D501754435746397738AE4DE9D3485A126630FE181CD0EB29F1B19D4E3902F5F9064A9C63E2B2D788DDDE2078D8931E6952C171580E07BE1BBB414A5657569C5C2FE3209D8BB08BE09E409200CC8F0C7370B5E6DF3F7FAA57D80B24EAF5FDCE17DDD689235D3AE183DCE53F26CACD9E1FA6F9BA6533126A14AE38E49F51476C5E00BE3890C0370F2E7DCE10F1C276482A24B9958FF0175C32A62BBEB626EC437BB8C615E9EF1B53AD03G37
	17F03E559B32606A12FC5A27DE3B24494688565F3081FD52AF1D850E40G6E19DFCEEEC2FF55007D67G9C428F3F207CCE49AA8DA24D0A525367EDC3CB48AC8F7AF9B20D9DC3F09F6361BEBE3E99F14B201D2DF0BEEFEDD8E76253606CC475A5F5106F31559598138B943EE67E7D9CDFB38D2B9FA0054F1D8C3BE6909A9536D6BA14603D331172C43DD8FB5A41F3A3584385CA787D95BDD64C9A110C54910CFDCF4C554CADB8D763C03F69GF3040FCBB65A581A2D13FCB2DF259F2B3040E43B61F126677C0A353A
	7CF4FD1FE4889BDE22FDC940B74D1BE0AD84E873F29FF6D51CB3691E2E532C98219D51FDF8A5C486576FFFC38673551E9CBC1F64FE5923B548DE59E3079B1F5ED7140372747D5B25881AB20A7742FE2F078D6693782A9C5C131226294E8BFD1A5CAAEB1A5353ED7457D1D93E3E8BDEA3D4F0D9CDB9E81FB2G7BDD0CBF1D03FB25D371F8E4BAE902C46FEF21FBD5F649EED03ADE6973E88CFFF6932E779EF9D00F95DFEC426427DFF275E15CE13942A352AA34C1F53AE1A9E068A209DE5C478FABB9A69F8D818EF3
	1ED8384E7472BDB9104F77644ECB5FA3333F5AE589F726CCFE2041125D4B76CEC12F31A719CE27B607577D939AB3F43C0F69BA1D209F42A5AEEC9F3C110B3C9AC1C6BB00564B7741531B311F479AFD8354ABAE97F32770734F3660BA77493DFDB204E88CEA27046F9E5F32AE286F2C6EC7567F449674234B948F1BD574637ACA1E8FCEGFD26834C69E53ECCD319AF58386745384950FEC4B5464D8B98F753A1BEAE5B87FB9120594746CD701947FDD24547A581BE99A0DBE3635AB4B60E79744D3DB84FB4D73CA8
	75CB7EA25FG2C552AAA66D9168FEE72AE5747CF57F85EF740073ECE02F6B2C0E69FDF937F301A67FDB1BD31E4617D1BA36635BB7B189E0DFD461AE8FF1F67FD955BD066B8681B5C476731A157FC76547AFCCA27139EFB7A9C3C19EBDEB38C37B4F7A5DC3B1C2ACFEB02CD6D67737527DC54974FC59D2D67899FD6E66A364EG390BG067A917F8BFF36C977DC40B5GB8GD2A93F87AACBF14F5385D544AEAD325A2528EE59DE001EAD128634A29ADF0AEAE977B13C5F4B676122937DF6312ECF5394CFBD26882E
	2FEA0358B4C766F3F86FD9667F7B32EAF49D939F1AC22A2FE4D942655D6356EC354C69E7FEFF3D5F707F50CF2D2985EF3C1341FCE1B944FDD1B910E0758EF0DFFCF9B01CAF6286F87D115CD0E95872F14F886DE0327FDA480E873E49C27634B2336CC2BF4BB35A7CFC9E36D67DE57370D4EFAD6ADE6F10BBFB6A947FE9982936EA0AF704C60875F4290A5B61G9DE37D5C0E9F7A435931C0587152DFE847B3A79A4CF634EA905A510E534A4088CDE1E770B6EFF05D713A5F480748E0E8EE447271D7E91C489EE479
	78C601FF07621BCDF8662FC3940F6DB150D7F3096710FF379D6354B2E867DCCE309683B482B8G46DD6671E16C5683E4189C3D896AE055A339DA838EDF1EABEFC35F43F92937EB3621AF69F9C9F8AE52D0996C07691783ED1F9D54E6F4E7C26836853597855131F9DC530A7607094FECF761BE2767751D7D4167B52E27E540F36D905465B4E63F65DF2E6BD3B044637557474D71BAFECD29E5D518F80150B144749BB5E4448B0728533DF9F85E4C063EA400E40035DFA7D84BGDAG342F799CBD396D48F0F3C497
	DE1BD28F38C6B8CF6B03739E615365072EEDAEB60450854FC54C5701F44CFF138E227F70BC1B71B54FFF632FD107A70755E16DB761564307E73704DF8FFC276B3A7E268CF545B9AE78066BBA2A6A5AF43DB904AED8570EEF42796BA5BF57418F7D2CBEAC273944A3501EA3F0C4C55CE2E813ABDC57B7FCA8F3A734EBGBAG06G26824483A481AC83489915E02D86E89F4569D2B4249B856DD80059G8B8148E82865C733FC894F2A9A386F984D72A2C6F75477C810CD4897E560FD7C95433E9A537DCEDDA76107
	ABA32B9145FE3D42EC407BA45D2E2B21E33AFBC2F4DFCF716CFEF5509A6277986BD685713A965DF1FEB76A7718EBE4DDA59D0B874FEE0FDCF77DE6FB783AABE9FB783A6B656D6FB3BC70B0BF05617D3F5F9E3DAE7B4376F0F559D66A2BA7B62C213CFDBE59C783D40324C9FA5F2B9A7ABB24BEBBAD39E55D4FD964F2658AFD4CB1CD29AF95FB6459FE1EDB0F9A4DF36B874B718C1C856DF9G0BC773B57A0EBFF89F126BA02F3FCE2F2D2E8BDDB3ACB67F678E4CB52A217F9C0097E09CC05CF5BCA60F338DFBEE9E
	73C7B9B7AF65C51E4351F961677006BCDC17BE25CF6D14036769663C9C51E7C47FA6CDF623CB5918BBA9BDE4B55DF248BC5FCB69D9DFA47A8773A22D85B6DFC78F19676B0058EBD3C21E674C85BFA6693EDF92E68E90523E930A4F81DCE1901E49FEB3C0F639107DC2994EFF0D09268D6E9DFAFBA80C8C466F31AAB33F92416F953A1EA61A6826437DAC3D1D9811DF33971FA33824C125CF039CEE8C2E59CEAF4FBF96825DD2G9B604B4618EB4D1F8CE03C69D2A5B7383A55ABF712026B11F6234AE9E3E0FC9C40
	DC0045G4B2F6779F0FFAD2E31134D528559C513D9FC10296B737011EC51E78CE8BC7351E956BF2961BD932D622524BAED6E192CE0DA450B24FA7F7D17E9FF9B2C6063CEA80803E51F7FE0DDD0FFA07F99B26D8F29A744BA33D35DE8357DF18FAB7CF85FD87FFACA5B22F8BF759A52A17298F1654AB759A1E394AF8EE17D5F79F23DD93E398E40B5C3EB11EA4DDC0BF82FE739E34C753CD6F936B6DCFF1C684F488E579FAF7A87F6076B1FA77ADF8B286996697DBB47727E4D7D617AABC57FEB03667E66312CFF14
	68BFDA912EBFE6AC0F2B737B0363EA5CD8E36D470F8D5D872C0659C25FEF4C9B4BEA18F4015FCD710BCCF8D65B7C381C5702CB212F7D861E7F2C9B44B3E0B634678B5CD90ADB8C6D25827758A04F13524671587D6E0E4831571A9FBE76E66607BFBF5765C7BFBFB767C70A19CC3F78035C0E02F1DC67278EF1BB2A852E388AF10D506E9038981AABEA501EA8F0ABAB383D53856E830A1B8B6D058277871A6BA6C3BB733B9C77F9B962F220DDA8F077D1DC8D34DB84AE63A01751AEF0FF243A4C0076EC01BBC1FDBF
	9F5A0B85EEA245AD03F6660D9C779FE20EF2G4766FD3C173F27F5GAE76C67E1C242483EBA6114BE385DED5F5C2F55A6B246EE69ADDD90E70EB1AB3A37D029BF90E10520BBC570E87DF83540C67751CFFB72F67E6FCFF8AE99EF5AA6C733705ED2516F36375F3E33CD14FAD02FA0E49BC22F2193320FF49F844752866FD047165AE98D084D756FFCFC0BF2E153D445C7FECC0BF6ABBE0B17727784C7D69A6FAE65BD33B38ED4B1E0AA32E3FBA947159E26584E6D33BFEEDB91BCFBAF216E5684FBC3BA2BC732487
	G335F2771DAB9E6822F1F2DF59C17A8F09FE6F3DC5ACD9C7750EE0E2B3E094F7587DB233D136F531CAE10AFCE91BCC10EF9B4FC9D26670936F27ECEDE93F2BE69673899C20ED35EE31653636AAE3A58093B3C288B99E32188C7C59FBBAAB86FCB1C4FA2413B3F0263F2EF66EB7B5F283CB6E8BB846E221F2F7789B773F81AC777D4923413852E10627039B6BE83E738C74BC772EE8C79DD4D38E4FD9C1311B8A299D77B7EAD5DB4BE0B7C1A5A66764810BCBBBBED3720DF8E53B8707738C7EF61B94C4B0770FD0C
	8613EC3F853FC399051FAD10B8BA7E19411CA872984F5FD3B9AF62FE01B663F17FBEA274B8D1057915DB72BB5DFD6ED667A5D9277B0A46DBB2916254C42E4B0F7C38F73C2A5CA52B2AEC2723391CB9F4CEABE1EC83G53AF2FA21AFE0CE7ED85156F7484497F1C2EA5AF709A9272D71463D8CDD1DCEDCEAF2EFB426C59745F06BD5E72D03F5EC0F17F0CFE933C7E26BC502F92068C32E0A20E5FF630E1983DD768D704D9C00310CFD8DCC9047BF761AFC56814CD6755A7633BA3594EF45AB748E3256DD62E531703
	213A77D15C2BGE5E25C0303013ACF5DEF681EF9E9B85D797B50F03AB7000CBC7D1E6D05DBC31E618E877AFB72EB24DBB0E2BA16BF7D1A1EEDAD37327CA9C56013A83E5D04E767EBC62FB8DF21AF71D67E4C78CAB12E59625B922C4DGF600A10029G33EE637953A37945E4185A37CD71B6C30265B225C44C3EBA755A3E83FABC031FE3B3FFF69F19B5766D14C84FDC93EF535F51685745ED79944F6CDBD70A76655E8E39B0C0BB0086B08EA076F6EE5F527CC2E65FB13BD32B55B42953C15F0EF1634EBA658183
	0DC599334D2F19ED1BF9BB13BFD73FA65D9E9A0B995D2ABAB35D92FDDCDA8C3B6644C4222B4FB653956A63EA74EBDBC43AC7F71B699C7A38A17DBAB1A25D2E72E8F3B7BDA21D94D2AB18E1D6E4F9172251A5C6243BDC910DEEE198BA96D7AC8321B57A26819E431301AEF3925467G0D1358B3D196F3C67B070BB9EBF45D703E83E8B5006DFAFB9C5C9FA3C6EC4AA22F6C44B5FF123F8B947859C7D69BF8DE766AFDE786713A27F30F3EDFE2874C5FCAD62E2C887B1D81C24CA4660FB87D9AFFC33E4E635973E69E
	EB2764D9D2A270D828533EBE49480B990F39FFB6671F36FDE9E1734F25FF5EEF315D41E89B6EB0F82CFDFBD98633AF25025A276C514F3C474B08307D3BFEFC366E1187EC6225DC83A4039E7CB82B2062C0212D349C12E2C637285C20FB7760555365D698F477DD15BCB6765EF22A37E78F59CE58F3DE1AD768FF0C772768E65D67AFE5F2FF657E68EE52705B37D37859946CF33F6E2739A6FF6D947E2A9B44796E12DCBE6339478436C6FC5C078DD5C46C1967FB0D7B17CD772BFBC7128B0E94180C5D5F2A8C7DF9
	EDF1641A7F166270B57F246270B57F5D45511F095FDF9C79DB259F961B3FD5523F57BD0CB59C39B3411A8BD08350866038131FC715A5BB88BD0F421CC5476C5D783C973F1054387CAD2511ED5DD1BA7CF7157BCA717B0F8B8A0499B772677812D21CAFCD71D2A4439DACC59F294EEE07460672774225384EDDF297C5333A6796DA9F0E813BE26E6435506873080B4336403DF21E57C7F384EEC79D274D1E4CF1CFFF44E98B842E68B427AD9338AFE83DB9865AB18277F31952E2DB6046F68B39827764EECE1BFD97
	47FD33115396885C0B9B05DC012BAE6738B182676F6738794638CB9C37F38A475DA61E63E8D378B3115A811E334D815C32A93CFEBDD09A703E1BBD93B96ED2861A7523CC2FD93FBB40F36E323B79B31147A8CF973447GCC3D1B1DCB01F1EA4951F0B896C3FE0A61CADF47DF9D417E1940E35E5D62BB32D2AC364ECA9775AC91D3AAC5252B9710AD2A6215D5ED10E16957E1BA2FAFBEA4E2ADE59D589509876C0013E0724797AEC2ED51A6F58BDDFA0F62393D8474481C1AE0AD86E884E8875026F25D4E9DA80822
	9BF39E5DDB2A6474E80C6FF3B5F8560F839E53816682AC84C886483CA741EA83A883E88468G7083CCG08851889B087E081406B77F09D26D46E0D22037EE04BD0C278A477F064BD1EFFF878BDDEF818FE7785FB33ED502B3F5FA9B90C790F52556513C5ACB9F8F82D40B579DA941F935F8B3173647B9A56E1DD0AC7E335DABC5D5F64DE70453D3CD61BFCBE30D67BC99D8EE1F9539F4F8F17B771BAADDC2DD6FD2FF14F641F22B1229170ED7772DA7F45FAC35E31535164E58CA3EF8C70C41F7F91EADBE6DBEFF6
	207DB750B8B39146CD95767F239CE83F6518214FBD5D43593FAA0ABE09A0E3D7007DEF52D835G700B047D4E63063C37B68EE7FFB4F959538C7B993F5672E87C52037CE91BE670C3FC9934FF2C73E67CEE699F0E1FD93F1AA0FEEDA17CDA23BE8B605FDDF39DBB0278E950BE95406F87954371DBED62B7AA08DF0C099F4BFBCE8CDC5BB302CF2F116E66915331E7887B7C7477A153021FAD14D17C0CE9414F967ED61CD33320AFB7D67C9E64887294F9938B77FA1C69216FA31D9E88AF124BF909FE225A8A83BD5D
	7A7B7223D15E179F9DBE5E5DF8B4FCCEB371E8E8CEB369E8781C666E23661C0661DE2B092C57121A6175FA3BA63CDE6F56046A15D69BDE2F153521FA2D2945B5578D6747B9B1AEDB60BE9738CD14A762BEF35AB50740EF2FDD29639BE59448713BEAD12F810ABB2763BEB061BE57F17BEB71AC81139D0AAA3E7D2F25715269F269F9AD9D63744051A27CF1046A6874CAA63D6B848E75E6F1EDC6B3A13C2E69BB0F79F1C0BDF69C02F2418EBB2CA525FBC7212D3850D6D2A6EA39E7BFA2024F3E96034F639FDD8D1F
	D2416773CF8CBE33DB8DBE8F4BA35367F7ED861F784F8CBE6A46116973A23AACCC1D49F33571672377BF549E3D7F6C17517B873F42960BB5A5FE633E4AF47F4B01EF3F1ECC7AD6E5305F507E2964CEDC05EF10F40DD85933980DD8BE6E51480E630A5AA97BEC1AC33679BA9D323D4FA55BB4452676F9ECDD0A0A9FB3E8E4A57D284106DF95E8E4857D4240F6DA997029E4DD68B3055A1B1F7F6D0C2F9A32AD4452C42C8E3A4F1AC87AGEED8BF7EEC8DD353A6DFAF79069EB461DAAAD9A378B6518D37307FC0D56A
	6F6ECF3510B4E04A82D093C943A8823CD5A6A39574239270EF253EB053A8EF8C9BA9BA96572CD5E0F1D363872B789BE32BC6D6B16B7075DC15C6B28A9A9B2B5C6E3D6082B4BE988D0A2E8ED86C3F7ADE79AB097FF25617CA2CCDE49366DA2D32CA0F88590E1F13693F41BBCED6F895480F718D8AD8612317F41FB668121BC9F657997C8D0ACF136D357C7071E9E4BD7D06FBBF3EC75C5FA8799C9A8666434DA44D033F96A46BD837CB72F46FE7A751E16A0A145A8E6A8EDC8B84744D227AA217A241A56B67EBF665
	1DDCB4AB15D80E1315AEC5329F17BAA12FEFA2ABB407AA7B9C0A4B6E779E2D210C1653320C26B640EE0BB09F0DAD4257BCC2699E7FBD93D6AFDEFD5CC208DFA57B69C8CCAD0B4488FB3DC3D2C1142CB6E9786BC7C55DA97AC6EF53F6BFFDE2E33B052468C6620CE579E185DA3BA43753B588B78DB43450395C88AB930EAE0F8FBFF9874DECC11A71AEA1206876A70AA77D603F3E20828CD20DECE72E84527D5D32625EDF5B556574480281789558EF956CF1CCD15018F57EDB571FFC6E5D8B308E9AC82A3B38187E
	AF217FCB71FF894514D0CCC9A9B8F789097A975C6F67B65345A3B68CCEFF9154521276CFEFBCF15F8B55CBDE876B0E13F47A05BBCC42DAE35469BE7785D99DBBE35256D5B18B47639CF9F12978116F963F589E9653D632D06C7BF4D5314510C9D795E25707AE8FC4EF9213042DDCBAF49B7D1FB7E21311AA1F526EC271343D4F50B4CD5796B7F33AC9D3737A7FBF50147B34549F92085027A97F7FFCFAA83CCFD38374CF532F1630D8924081611030953169E62C7EACF96F9573737B255767C4FC87B06FC1768CF9
	51034633E4763CA309BE433245A5D82B63F89EDFD80EAF1CA3BD0FE9F5CBAE17A639FAEABDF6FCB6D3B72849463351174A113F0F0F51BBD95F62F2547B220966FFGD0CB8788295696F70897GG8CC3GGD0CB818294G94G88G88G52F854AC295696F70897GG8CC3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4297GGGG
**end of data**/
}
}
