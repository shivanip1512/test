package com.cannontech.common.gui.unchanging;

/**
 * Insert the type's description here.
 * Creation date: (2/14/2001 2:59:30 PM)
 * @author: 
 */
public class JCheckBoxDayChooser extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private javax.swing.JCheckBox ivjJCheckBoxFriday = null;
	private javax.swing.JCheckBox ivjJCheckBoxMonday = null;
	private javax.swing.JCheckBox ivjJCheckBoxSaturday = null;
	private javax.swing.JCheckBox ivjJCheckBoxSunday = null;
	private javax.swing.JCheckBox ivjJCheckBoxThursday = null;
	private javax.swing.JCheckBox ivjJCheckBoxTuesday = null;
	private javax.swing.JCheckBox ivjJCheckBoxWednesday = null;
	public static final String MONDAY_STRING = "Monday";
	public static final String TUESDAY_STRING = "Tuesday";
	public static final String WEDNESDAY_STRING = "Wednesday";
	public static final String THURSDAY_STRING = "Thursday";
	public static final String FRIDAY_STRING = "Friday";
	public static final String SATURDAY_STRING = "Saturday";
	public static final String SUNDAY_STRING = "Sunday";
	public static final String HOLIDAY_OFF = "Holiday off";
	public static final String HOLIDAY_EXCLUSION = "Exclusion";
	public static final String HOLIDAY_FORCE_RUN = "Force run";
	protected transient java.awt.event.ActionListener aActionListener = null;
	private javax.swing.JCheckBox ivjJCheckBoxHoliday = null;
/**
 * JCheckBoxDayChooser constructor comment.
 */
public JCheckBoxDayChooser() {
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
	if (e.getSource() == getJCheckBoxMonday()) 
		connEtoC1(e);
	if (e.getSource() == getJCheckBoxFriday()) 
		connEtoC2(e);
	if (e.getSource() == getJCheckBoxSaturday()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxSunday()) 
		connEtoC4(e);
	if (e.getSource() == getJCheckBoxTuesday()) 
		connEtoC5(e);
	if (e.getSource() == getJCheckBoxWednesday()) 
		connEtoC6(e);
	if (e.getSource() == getJCheckBoxThursday()) 
		connEtoC7(e);
	if (e.getSource() == getJCheckBoxHoliday()) 
		connEtoC8(e);
	// user code begin {2}
	// user code end
}
public void addActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}
/**
 * Comment
 */
public void anyCheckBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	//create a new ActionEvent with the one received
	java.awt.event.ActionEvent ev = new java.awt.event.ActionEvent(
		this, 
		actionEvent.getID(), 
		actionEvent.getActionCommand(),
		actionEvent.getModifiers() );

	//tell all of our listeners that a value has changed
	fireActionPerformed( ev );

	return;
}
/**
 * connEtoC1:  (JCheckBoxMonday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JCheckBoxFriday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JCheckBoxSaturday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JCheckBoxSunday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JCheckBoxTuesday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JCheckBoxWednesday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JCheckBoxThursday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Method to support listener events.
 */
protected void fireActionPerformed(java.awt.event.ActionEvent e) {
	if (aActionListener == null) {
		return;
	};
	aActionListener.actionPerformed(e);
}
/**
 * Return the JCheckBoxFriday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxFriday() {
	if (ivjJCheckBoxFriday == null) {
		try {
			ivjJCheckBoxFriday = new javax.swing.JCheckBox();
			ivjJCheckBoxFriday.setName("JCheckBoxFriday");
			ivjJCheckBoxFriday.setMnemonic('f');
			ivjJCheckBoxFriday.setText("Friday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxFriday;
}
/**
 * Return the JCheckBoxHoliday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxHoliday() {
	if (ivjJCheckBoxHoliday == null) {
		try {
			ivjJCheckBoxHoliday = new javax.swing.JCheckBox();
			ivjJCheckBoxHoliday.setName("JCheckBoxHoliday");
			ivjJCheckBoxHoliday.setToolTipText("Holiday");
			ivjJCheckBoxHoliday.setMnemonic('y');
			ivjJCheckBoxHoliday.setText("Use Holiday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxHoliday;
}
/**
 * Return the JCheckBoxMonday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxMonday() {
	if (ivjJCheckBoxMonday == null) {
		try {
			ivjJCheckBoxMonday = new javax.swing.JCheckBox();
			ivjJCheckBoxMonday.setName("JCheckBoxMonday");
			ivjJCheckBoxMonday.setMnemonic('m');
			ivjJCheckBoxMonday.setText("Monday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMonday;
}
/**
 * Return the JCheckBoxSaturday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSaturday() {
	if (ivjJCheckBoxSaturday == null) {
		try {
			ivjJCheckBoxSaturday = new javax.swing.JCheckBox();
			ivjJCheckBoxSaturday.setName("JCheckBoxSaturday");
			ivjJCheckBoxSaturday.setMnemonic('s');
			ivjJCheckBoxSaturday.setText("Saturday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSaturday;
}
/**
 * Return the JCheckBoxSunday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSunday() {
	if (ivjJCheckBoxSunday == null) {
		try {
			ivjJCheckBoxSunday = new javax.swing.JCheckBox();
			ivjJCheckBoxSunday.setName("JCheckBoxSunday");
			ivjJCheckBoxSunday.setMnemonic('u');
			ivjJCheckBoxSunday.setText("Sunday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSunday;
}
/**
 * Return the JCheckBoxThursday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxThursday() {
	if (ivjJCheckBoxThursday == null) {
		try {
			ivjJCheckBoxThursday = new javax.swing.JCheckBox();
			ivjJCheckBoxThursday.setName("JCheckBoxThursday");
			ivjJCheckBoxThursday.setMnemonic('h');
			ivjJCheckBoxThursday.setText("Thursday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxThursday;
}
/**
 * Return the JCheckBoxTuesday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxTuesday() {
	if (ivjJCheckBoxTuesday == null) {
		try {
			ivjJCheckBoxTuesday = new javax.swing.JCheckBox();
			ivjJCheckBoxTuesday.setName("JCheckBoxTuesday");
			ivjJCheckBoxTuesday.setMnemonic('t');
			ivjJCheckBoxTuesday.setText("Tuesday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxTuesday;
}
/**
 * Return the JCheckBoxWednesday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxWednesday() {
	if (ivjJCheckBoxWednesday == null) {
		try {
			ivjJCheckBoxWednesday = new javax.swing.JCheckBox();
			ivjJCheckBoxWednesday.setName("JCheckBoxWednesday");
			ivjJCheckBoxWednesday.setMnemonic('w');
			ivjJCheckBoxWednesday.setText("Wednesday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxWednesday;
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return java.lang.String[]
 */
public Object[] getSelectedDays() 
{
	java.util.ArrayList list = new java.util.ArrayList(7);
	
	if( getJCheckBoxSunday().isSelected() )
		list.add(SUNDAY_STRING);
	if( getJCheckBoxMonday().isSelected() )
		list.add(MONDAY_STRING);
	if( getJCheckBoxTuesday().isSelected() )
		list.add(TUESDAY_STRING);
	if( getJCheckBoxWednesday().isSelected() )
		list.add(WEDNESDAY_STRING);
	if( getJCheckBoxThursday().isSelected() )
		list.add(THURSDAY_STRING);
	if( getJCheckBoxFriday().isSelected() )
		list.add(FRIDAY_STRING);
	if( getJCheckBoxSaturday().isSelected() )
		list.add(SATURDAY_STRING);

	return list.toArray();
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return java.lang.String[]
 */
public String getSelectedDays8Chars() 
{
	StringBuffer buff = new StringBuffer("NNNNNNNN");
	
	if( getJCheckBoxSunday().isSelected() )
		buff.setCharAt( 0, 'Y' );		
	if( getJCheckBoxMonday().isSelected() )
		buff.setCharAt( 1, 'Y' );
	if( getJCheckBoxTuesday().isSelected() )
		buff.setCharAt( 2, 'Y' );
	if( getJCheckBoxWednesday().isSelected() )
		buff.setCharAt( 3, 'Y' );
	if( getJCheckBoxThursday().isSelected() )
		buff.setCharAt( 4, 'Y' );
	if( getJCheckBoxFriday().isSelected() )
		buff.setCharAt( 5, 'Y' );
	if( getJCheckBoxSaturday().isSelected() )
		buff.setCharAt( 6, 'Y' );
	if( getJCheckBoxHoliday().isSelected() )
		buff.setCharAt( 7, 'Y' );

	return buff.toString();
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
	getJCheckBoxMonday().addActionListener(this);
	getJCheckBoxFriday().addActionListener(this);
	getJCheckBoxSaturday().addActionListener(this);
	getJCheckBoxSunday().addActionListener(this);
	getJCheckBoxTuesday().addActionListener(this);
	getJCheckBoxWednesday().addActionListener(this);
	getJCheckBoxThursday().addActionListener(this);
	getJCheckBoxHoliday().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("JCheckBoxDayChooser");
		setLayout(new java.awt.GridBagLayout());
		setSize(345, 48);

		java.awt.GridBagConstraints constraintsJCheckBoxMonday = new java.awt.GridBagConstraints();
		constraintsJCheckBoxMonday.gridx = 1; constraintsJCheckBoxMonday.gridy = 1;
		constraintsJCheckBoxMonday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxMonday.ipadx = -1;
		constraintsJCheckBoxMonday.insets = new java.awt.Insets(1, 0, 0, 0);
		add(getJCheckBoxMonday(), constraintsJCheckBoxMonday);

		java.awt.GridBagConstraints constraintsJCheckBoxTuesday = new java.awt.GridBagConstraints();
		constraintsJCheckBoxTuesday.gridx = 2; constraintsJCheckBoxTuesday.gridy = 1;
		constraintsJCheckBoxTuesday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxTuesday.ipadx = -1;
		constraintsJCheckBoxTuesday.insets = new java.awt.Insets(1, 1, 0, 5);
		add(getJCheckBoxTuesday(), constraintsJCheckBoxTuesday);

		java.awt.GridBagConstraints constraintsJCheckBoxWednesday = new java.awt.GridBagConstraints();
		constraintsJCheckBoxWednesday.gridx = 3; constraintsJCheckBoxWednesday.gridy = 1;
		constraintsJCheckBoxWednesday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxWednesday.ipadx = -1;
		constraintsJCheckBoxWednesday.insets = new java.awt.Insets(1, 0, 0, 1);
		add(getJCheckBoxWednesday(), constraintsJCheckBoxWednesday);

		java.awt.GridBagConstraints constraintsJCheckBoxThursday = new java.awt.GridBagConstraints();
		constraintsJCheckBoxThursday.gridx = 4; constraintsJCheckBoxThursday.gridy = 1;
		constraintsJCheckBoxThursday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxThursday.ipadx = 10;
		constraintsJCheckBoxThursday.insets = new java.awt.Insets(1, 1, 0, 18);
		add(getJCheckBoxThursday(), constraintsJCheckBoxThursday);

		java.awt.GridBagConstraints constraintsJCheckBoxFriday = new java.awt.GridBagConstraints();
		constraintsJCheckBoxFriday.gridx = 1; constraintsJCheckBoxFriday.gridy = 2;
		constraintsJCheckBoxFriday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxFriday.ipadx = 1;
		constraintsJCheckBoxFriday.insets = new java.awt.Insets(0, 0, 3, 8);
		add(getJCheckBoxFriday(), constraintsJCheckBoxFriday);

		java.awt.GridBagConstraints constraintsJCheckBoxSaturday = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSaturday.gridx = 2; constraintsJCheckBoxSaturday.gridy = 2;
		constraintsJCheckBoxSaturday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxSaturday.ipadx = 1;
		constraintsJCheckBoxSaturday.insets = new java.awt.Insets(0, 1, 3, 0);
		add(getJCheckBoxSaturday(), constraintsJCheckBoxSaturday);

		java.awt.GridBagConstraints constraintsJCheckBoxSunday = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSunday.gridx = 3; constraintsJCheckBoxSunday.gridy = 2;
		constraintsJCheckBoxSunday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxSunday.ipadx = -4;
		constraintsJCheckBoxSunday.insets = new java.awt.Insets(0, 0, 3, 29);
		add(getJCheckBoxSunday(), constraintsJCheckBoxSunday);

		java.awt.GridBagConstraints constraintsJCheckBoxHoliday = new java.awt.GridBagConstraints();
		constraintsJCheckBoxHoliday.gridx = 4; constraintsJCheckBoxHoliday.gridy = 2;
		constraintsJCheckBoxHoliday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxHoliday.ipadx = -3;
		constraintsJCheckBoxHoliday.insets = new java.awt.Insets(0, 1, 3, 18);
		add(getJCheckBoxHoliday(), constraintsJCheckBoxHoliday);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
		JCheckBoxDayChooser aJCheckBoxDayChooser;
		aJCheckBoxDayChooser = new JCheckBoxDayChooser();
		frame.setContentPane(aJCheckBoxDayChooser);
		frame.setSize(aJCheckBoxDayChooser.getSize());
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
public void removeActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}

/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return boolean
 */
public boolean isHolidaySelected() 
{
   return getJCheckBoxHoliday().isSelected();
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
public void setEnabled(boolean value)
{
	getJCheckBoxMonday().setEnabled( value );
	getJCheckBoxTuesday().setEnabled( value );
	getJCheckBoxWednesday().setEnabled( value );
	getJCheckBoxThursday().setEnabled( value );
	getJCheckBoxFriday().setEnabled( value );
	getJCheckBoxSaturday().setEnabled( value );
	getJCheckBoxSunday().setEnabled( value );
	getJCheckBoxHoliday().setEnabled( value );

	super.setEnabled( value );
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2001 3:22:22 PM)
 * @param value boolean
 */
public void setHolidayVisible(boolean value) 
{
	getJCheckBoxHoliday().setVisible( value );
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 */
public void setSelectedCheckBoxes( String days )
{
	if( days == null || days.length() < 8 )
		days = "NNNNNNNN"; //we have a bad value, clear all check boxes
		
	for( int i = 0; i < days.length(); i++ )
	{
		boolean setSelected = (days.charAt(i) == 'Y' ||  days.charAt(i) == 'y');
		
		switch( i )
		{
			case 0: //SUNDAY
			getJCheckBoxSunday().setSelected( setSelected );
			break;
			
			case 1: //MONDAY
			getJCheckBoxMonday().setSelected( setSelected );
			break;

			case 2: //TUESDAY
			getJCheckBoxTuesday().setSelected( setSelected );
			break;
			
			case 3: //WEDNESDAY
			getJCheckBoxWednesday().setSelected( setSelected );
			break;
			
			case 4: //THURSDAY
			getJCheckBoxThursday().setSelected( setSelected );
			break;
			
			case 5: //FRIDAY
			getJCheckBoxFriday().setSelected( setSelected );
			break;
			
			case 6: //SATURDAY
			getJCheckBoxSaturday().setSelected( setSelected );
			break;
						
			case 7: //HOLIDAY
			getJCheckBoxHoliday().setSelected( setSelected );
			break;

			default:
			// ignore the value
		}

	}

}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 */
public void setSelectedDays( int[] days )
{
	if( days != null && days.length > 0 )
	{
		for( int i = 0; i < days.length; i++ )
		{
			switch( days[i] )
			{
					
				case 0: //SUNDAY
					getJCheckBoxSunday().setSelected( true );
					break;
				
				case 1: //MONDAY
					getJCheckBoxMonday().setSelected( true );
					break;
					
				case 2: //TUESDAY
					getJCheckBoxTuesday().setSelected( true );
					break;
				
				case 3: //WEDNESDAY
					getJCheckBoxWednesday().setSelected( true );
					break;
				
				case 4: //THURSDAY
					getJCheckBoxThursday().setSelected( true );
					break;
				
				case 5: //FRIDAY
					getJCheckBoxFriday().setSelected( true );
					break;
				
				case 6: //SATURDAY
					getJCheckBoxSaturday().setSelected( true );
					break;
				
				default:
				// igonre the value
			}
		}
	}
	else
		getJCheckBoxMonday().setSelected( true );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GABF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBA8DD4D45719447F461FE016DA0EB1AD29A6E19BCEE392FACA3BEC57D237318DED12DD535A93CECBF6EDC2D25B641C3A6718CA37173F8123D194CC4956ECB0EAD103449FD4C44891E6D0D0CC91A6B022C61F4C03991819B74C3C61C7FE5C6F7E4E1B19771804BD3BF34EF75E3B5F7F7D6E3D5F7D6E1D07CA5FCFA84849ADC5A827G315F699C04569CC3487DAB291463E264B493527C76826CC4E3B579
	700C851AF7C91A29986DB9DC846D9550AE7BF21A694F70DE02769EF6FC84AF86F64E812D7E69779F1D5ACECEA737D3C85ADFEB5C864F6DG0900434F0DB2367F6D46B206CFE7F88EB939882D6B45BC7F52380761EA205D8690871058CB79D741F38B31BC3B360671DDDF1C00B67E38A277ED2CC72313C3F99F153530E7B9FABCE7C39F3635197809E546211D8C00BB1F07DE7F6BE9F856358D74FD5C23382BFAA40FC77128F20F232AAF602C8AF8FA9C12274F69692B2D3DE8F648BD830D4A08C59AB5BB9445AF7B
	104301F51B4DC5C497C2C7213D11603EFA825BCB027755G9982AFD7E3FC0D937A8B60F072BE56D75431BE58469EC749BF77646DB068E30C1376AD51996CE3DC608F056F1271E86842F1DF8F34227EB4539E00F3G37GE2G7EC266C1F367E1F8B65CD64D0A5B2DF89A3C5EC6175DF7D9314BAEF82F2D858A43354BBD0A4F0E909D6B9CF3250EBFD381155B710565E8CC92CF70F1CFEAFF84E5BCF5A937D0A7A69909C7F3637BE9ACD67687E3C2EDB79B5AFE2EBFF4DE065A6EC2DA5BAB66A8F985BA3613DFB814
	3B1E69592431FDF0G0F75AEB256E5701E436687456FA1F81786CF67688AB2E7F0FB9CE81983FCEE5836F3DFDA73172262E32D3AF108CFEB483538288F57DD613E7C8731B94FC5ED6E943E18CEE2FC3286CFFD99A07C381D89B417BB4DC46757E4BBDE331B20DD6AC9B3D582D8G6CGB1GF19EBE4796F7FE74B967D81B64D39B83CE179D5693153D58758EBC659155A735F87C2A646911BBA51FD33A6D127D0871F4F461985C8E113311395F83B6AE489E59A729CE45835AA5372C4ABEBF65113AF01FFAFCB221
	DA1DEE998C383D086BF5F6156031117C6A27DE3B244AC18ECA9F6C42E3B2A4F502C6D0G6FB4AE0FF510B8C27F5381B2C49CB690FD17E41FC7F6555636AA4AC040EB45BDC1158A0E73CD12BB5EG7EA2054778D1AB46F921BDCF61FA7659F6E974D81CBD58FD49B74A57D8D9B54E4B4B94BEE7FEE763F3467276432872050B3AF386C32AC2674ADAA5FC6DFCC0F4E23FE87B9BA7ECD1566DB2C5FFCDFD7EDC710328B6722629FF3B982B5D1DF82C324157B2EF1A29464BE37C192D1C4E59C649AF73D97AB79FCCB0
	599E0CB875F3F3F5B90BA7762700790FE33F95G6FCFBBC41F361D443CEDE0E784A081E08D409BGE503101F819C03BC460355B11FC36FF47B7F4546A20E78B2159FBB7F9FFC21E3B32F9D5B1307E40FEA113D32478EAFFEC63B2F9D7B4572C52FA2E43ECC70DE48AFE6D850FE01FF209D7BAD292A4FF9BB204A9D322A428E9F24AFA7367C015B5EE0EA62363E510EE3218CC3FE917C0F3763355923F8BCB219A4C204511FA039C1F649EEF05A2C84BCAA45FF3B832F2F81F9146526FF6840855710648A60BCC76B
	1315920E375767F44354432992E58E623CB1E4AE5344A896F0384ECA06671AC11E830677719C307F4EC3E8554D8339FAFBD87C43165C03BE3AEE6BFC419CC0FDCA326A7B7EA449D18C6F273E4EF560DC27D7A751F577C99756F59DECF8816679783A2B7A9453F9EEE38B4E2C38DCB41CA24EFF69AC4663A28F86E45892F0923DA4E277C1672EB0DAA70BA3259F6E44F1F4E9726FADBF0E2365A42FC193401F9400F5BE9A4BB2BF0D8565ABBA457912203D5AEF44B7638C67DB8F6DECC33ED3351CEF0F1AE62AD60D
	78DE93FCD660F19972FD75AC67DBGBCAB8C795A6BB85FEA6049B064B355F33EEC60A98D000F01D0BEBA663FBF0467A1291F7B25A1E9244ABFCC2B65B689C613564719E40F437CEA005741FFB8CF6AE3E82F86488870B97B7C5B3C8E0E9BC8C0194B7F4B30565F9820FE148D8567EC79CCDE87475B30CD9B509CC3FC1EBD5C295D0B9B7CFE2547C94AGB6C72CDD5AB9CDF1D73B5E02E72F5367D7DBA0690C7079F413548138CEE0E8D6B739C9BD057BBA8EF613818E8EE37CAFAE14EAFC5FB0C4777BD2206591FD
	EBCF9E47B909CC78AA7AE813FD3D0A4FAD5B8F6248D6C943EA9549FFD58D045C045F8F71F198BFD70B65BD23FC755DEC88D352EA2C656C21CEDE77FE3D649C73B17D1BCBD15D5D12DC3DF3483C9D6F65FA07E95C630703719FFF52143761334D05B49647CE71D82C871EBDA3BC96B3DA75E2E185BA15FB6C8C17F3GAEC1487D5024A737CE485D2C65F29BG37FF144B45576949DD9F65F2A5E7391C97F009C2EE5191BD39CCA17734105B88384ABBDC6E55B6BDB97B9DAEB7D64765C6G37D2483D6356134B92F2
	9F55F339EC40D50FF139F4D5CF4EB5466756127A694EADDA877CBA628C95B3962C736246A2EBBEFA3638778276B7F1AC7C4C71G412F9D8BBFF3208B7C4C014653CC8B46F9BDDC510DF3CC598474992095408EB08BE04584DF5B2FF41DC0D14E9BADF02E77F9A4D7C748464E567303B69CAB580BD97BA19B0EB3590B910F037FE4F89CD6CD0476BDA32CCD656E09107B6F30B61A8C1523EB7A63CE6C0BCEEE65EB1C5DBF94B797B1DBE513BC0FCE1C576651549D47F3376A2CE38CED4C36F7B2380EAB45FD427357
	70BE3092E869G6B8150DDD8DBG67G6CF7F97CBF3CDA9FAD7EE4D2D995B3601AE19FB60757CBA2DEFF2C19DE1C371E099667D9F7C365E87C3E534A63376CAE2F431F08507579FC782728BE2C5575610FA66E43866103DCBBBD9F7C91F261BE6C0F51736159BA6EC3DD8C77615D33537321A2C2AE5C87D55707FFBE42FDD8A0FC7849B4FDF8B12A8F2BF5FDA83F42FD4890BE5CD3B7BD9F6E0B108B7721749EBD9FAE3B398F1577F09FCE56CF4F87F304DC388FF6DD9F728258871CCF4601CE6FAB32C9AD178C6D
	B58277F49346ED00F67E8C6EEB6C05F2F89E0436856096C08CC082C0AAC08640A6417B90614535370B552E38AEDD4D6A67EAD6FFD2DE53059826977B39E9C6285F29B3A277A28C37985E2221479870EAA1C107A7C378FD0878426FA3424F42EF9B68ACE44F828DEECB94BFB0EC5541615A0C665F235D466763535D7A67637AEE7D733139FBA745038E6D6D9C27DF6A1E7A7C5C512DF7FEFE0F446AC76F6CA03A7DFE59CFB6840B24CA0C760A3A9B691C23BF115CB20BB31C3FCFEEE1BCCD2AE2BEA666786E6670B9
	DEBA936A23196CDCBBB3F29C697EE63B01EB27B6203BGE681AC83C81D497735D73B235695CDA3D35495AFDEB39E17FF3B26BFAED957705C71AB81DF0F9C9E7B572F1589DAF087EDD1E5B78E936559C464D54957A7C70C5D1F08BC25994997DDB79ADFBA8655E734E3F0DC0C4177DA4347A0836278860BFD7E2C48B1207A32C2741D907AF28B437515010ECA26671C213EE535DAFD55C25F5A08DCEC859D8E26E75CD0DF5BD92D3E13C25F0F0F046B0B859DAB181E94C3FD79F5DAFD27043E83D7427525030ECC26
	E723213E1490FD27053EF6F7383E2259F08E1BCD759C1DED244FD52F55D7A374958530BE0BC64E866FAE560EB154D7E24177DAAEE9D4892870B2876F9907AD7C7EAA8E6412G5682E44D565EADBCB10CF3DE2FCFF243746B704ABDA803487E32094BB6C37BBA40B840B20054B97C4C3378A6DEF722D23E2CF86C52A873673B5EE2AD4D9A107DC02434405EDDDADA17EC7790AA6F4B17CAF60648BA82BECAE674BAF3085D73BEE7506E13B7C3E4BBA4B560534A46EF8F116D88E8FCFEEDA04467E6450595537D3712
	547F7B672619EA6672BD7943960CEB0576F5010BED617B34E3AE4FEF37EE9867273E9B7A7949FDC3FF5F705F18FA5F983BE114D728FFAF3472FE24881F5F3D4C7B11AEF0F7AE73FE2C8B7295F2590373B86EC6A1172D93386F95F259E6017BCD9D170D97380F6A39EC12404556F3599401FB698817AD7A92479D286232FB84EECE9517BDA8F0F5D7386C084045DC61328B846E25ABDCB6DE601CEEAE1BA5F0A58A174D96B8D56132C577F25C7B242E33C15BABF0F388EE9C5A318277AC412D00F63240D58FF1FD2B
	6E6577926D967E5FFA71FC086BFC7E5F7A31DB786CA84E2F7816213676822C08C6292F15E483DAE7AE3594B17918797CFEF08D5119826D67G326673F36D48DF7939B669112528B566126E7DE02675F8AEDAC0F3497E8541F3EDA21CEB294D7C735C66AD2047AF403863673577A8F85DC9GD5G14EE8F216339FCA8043EA4040E7DFDAAD7CBDF93C2AF5048533EBDF30077AD7B19C4647A5699433B4FCC56277C05745956398F6D3FF7F3A14D95E6128F5C9E998A96E7CF6BC2EC3B1B60DE44633110674846967C
	7F038A8BBCF6A17E5663E7F66C45C6427FE80BC5472EB6B6DAD88BB29876B1FF33607DCF70436F547EFF12357D93ECFF914CB1E26FCDB9E8EF7165E8768A2630573A0876A9E82F112C73D770BC97760E75856D3DD0B8153D02A8769C9176CAC9EE788D0E2730E7F5E862D9BF153D42A876E6014EB7C362799649A72F82448AFB331CC1FB8FD6C56B5FD4631730A8FC7CE41203DE87C892769E6D8F5A5BF4A5DA7F2632179AE16FFE125F1E87D8A36C3DBE9034B7DF194A5E16A87656014E5DA878BFEB89BA954035
	062AA8AE2B53EB05A501284DDDEEA4F89E8CC42E2BE4027BBD407AC5FCDD3D9C88DDD797BDC13FAF8DC573FB2AF93091ECECE6F122B5194DB23DEFF2FA27A97735AFACC76FFC17B661FC0E6E23F73EB9823F1C6037E970F4CF792958D34A00B6FE9F3F8BBE22605AAF935A59G253130AF813482B8E2F9CD33733323A84A194D2AF8DB2168F1E94A945A3FAD6727772D52778BF96ECE7E6E57D14A1CB7F30C6EDB47E3E94EDE461ED956FD84CF7B374A0B7B178E34AC00ED0B53CC7B81EAG5A96737EDD7D2C1276
	2F496ED49BD4D56AF110FF2CF867BA1D72F0900D8BE7BAAFABDA703342DD41FCCE9856FED7D77356E95D7F07B15897D3DFC758B3F56EBE266359EBDA9D45CB7766669868D849E5978777637709DFF0AEC1419FDDB392AE8B190D173671586EF96C81E479791BB97B8CEC3C41F41F3BBFE8A32BBF1B6D0B3BAF137E77D6B09B4F605388DB5BD789CF051F53FE978852BE2CA734G276D9C8E5276D6911A4AE95F998952462F905AB027E50C86E9BB94C2F3F01AF9ACC8FBA6C0E8231C56BB0EE9F4FEA6B4855F7FDE
	733E56926D3B197F8DD09B3F7DBF35C16776A94578BCD22F681FC74C0A7EF924C5197A1E6A0AE27C1D07CD51FE67C157662989DC3F0D405C0A83C881C887483C1F2F4B17158F90D917BAEB32495E076FEB7057ECAA377FA32FF1DF1F73C67F866E97DE7C5F74EDC5D595B7564F713F726271D295AFC1D25CFAAF0E114F5967D0A9AB433F6645EB4FA57792B46DE747A446E5C33F4A7E0E77EDEC70C3F8F6CA7DAC1B62542378C86FG5966D33C32CF9D25D87265857F6663AE92363EBFF840C887C468A29C917EF6
	3BDF6ED12DD21F0879A8427BE58D78E18598810885D881108A709C40FA00CDG45F1F036822883E883308328G73G164571FEBC62BFB4C5BF5861048C14CFF26221E23EFFBCE1BCD6CD9351476A138972ED814458BA6AE577EC9D9378DEC4696D754BE2CE58A60A852E455F26781D78FE0E51A637EB67B38DB37F9EE1924FCD278758A517738E792C218398BDFD143E90997B9F07227B7FA0521FEB4BD164DCCBC47AF36D1BC8BB57A86EA9D26F74C15CED02AFC5602E8A5CBF921D985765342B8E017F81718B63
	1BE5ECD02CAD6257B04159986E279A5CD5067BD70447G3A6CD0FCDCF69D4275DB2F5365226D8C426374401008F83CC4FCF4FAA50D5FAF8B9C761BFEF37F4A29CB30D1A57D96952848C4EF216005DE8B2928085F81810EDE772828105DC729E80B38FD82CEFE53848C6C72C7C5DBBF754B652C6577219D623F7C9662EB6650095A3CFFDF1E170B72DB10090EDD8B4A478350028A48783420BC983B917C7D2B7617639FC413220A46BF32C307E0F3B8F3846FFBA1D844F88BC274A28DA5A702F7D7048F9C4BEB02C3
	F735F5472659E50635CB7292DA8B2CD9922C89E8EE711D413D4A7FCA1A297CABBCBF24559CC146B92A43AD39DC2A649AE87058F13EEA9CD56560FEB2FC866B77F39EC62434C9F2475D2F11799FD0CB8788DC2E4F75D391GG70B1GGD0CB818294G94G88G88GABF954ACDC2E4F75D391GG70B1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0D91GGGG
**end of data**/
}
}
