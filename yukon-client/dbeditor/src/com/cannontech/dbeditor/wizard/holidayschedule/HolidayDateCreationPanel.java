package com.cannontech.dbeditor.wizard.holidayschedule;

/**
 * Insert the type's description here.
 * Creation date: (1/21/2002 4:44:37 PM)
 * @author: 
 */
public class HolidayDateCreationPanel extends javax.swing.JPanel implements java.awt.event.ActionListener
{
	public static final int PRESSED_OK = 0;
	public static final int PRESSED_CANCEL = 1;
	private int response = PRESSED_CANCEL;
	
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JComboBox ivjJComboBoxDay = null;
	private javax.swing.JComboBox ivjJComboBoxMonth = null;
	private javax.swing.JComboBox ivjJComboBoxYear = null;
	private javax.swing.JLabel ivjJLabelDay = null;
	private javax.swing.JLabel ivjJLabelHolidayName = null;
	private javax.swing.JLabel ivjJLabelMonth = null;
	private javax.swing.JLabel ivjJLabelYear = null;
	private javax.swing.JPanel ivjJPanelHold = null;
	private javax.swing.JTextField ivjJTextFieldHolidayName = null;
/**
 * HolidayDateCreationPanel constructor comment.
 */
public HolidayDateCreationPanel() {
	super();
	initialize();
}
/**
 * HolidayDateCreationPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public HolidayDateCreationPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * HolidayDateCreationPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public HolidayDateCreationPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * HolidayDateCreationPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public HolidayDateCreationPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJComboBoxMonth()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonOk()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonCancel()) 
		connEtoC3(e);
	if (e.getSource() == getJComboBoxYear()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxMonth.action.actionPerformed(java.awt.event.ActionEvent) --> HolidayDateCreationPanel.jComboBoxMonth_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxMonth_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> HolidayDateCreationPanel.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> HolidayDateCreationPanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (JComboBoxYear.action.actionPerformed(java.awt.event.ActionEvent) --> HolidayDateCreationPanel.jComboBoxYear_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxYear_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
/* Override me with an anonymous class !!! */
public void disposePanel()
{
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:15:35 PM)
 * @return int
 */
public com.cannontech.database.db.holiday.DateOfHoliday getDateOfHoliday() 
{
	com.cannontech.database.db.holiday.DateOfHoliday hDate =
		new com.cannontech.database.db.holiday.DateOfHoliday();

	
	hDate.setHolidayName(getJTextFieldHolidayName().getText());

	hDate.setHolidayMonth( new Integer(getJComboBoxMonth().getSelectedIndex() + 1) );

	hDate.setHolidayDay( 
			new Integer( Integer.parseInt(getJComboBoxDay().getSelectedItem().toString()) ) );
	
	hDate.setHolidayYear(
		(getJComboBoxYear().getSelectedIndex() == 0 
		? new Integer(-1)
		: (Integer)getJComboBoxYear().getSelectedItem() ) );


	return hDate;
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
			ivjJButtonOk.setMnemonic('k');
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
 * Return the JComboBoxDay property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxDay() {
	if (ivjJComboBoxDay == null) {
		try {
			ivjJComboBoxDay = new javax.swing.JComboBox();
			ivjJComboBoxDay.setName("JComboBoxDay");
			// user code begin {1}

			ivjJComboBoxDay.setMaximumRowCount(5);

			for( int i = 1; i <= 31; i++ ) //January should be our starting month
				getJComboBoxDay().addItem( new Integer(i) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxDay;
}
/**
 * Return the JComboBoxMonth property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxMonth() {
	if (ivjJComboBoxMonth == null) {
		try {
			ivjJComboBoxMonth = new javax.swing.JComboBox();
			ivjJComboBoxMonth.setName("JComboBoxMonth");
			// user code begin {1}

			for( int i = 0; i < HolidayDatesTableModel.DATE_SYMBOLS.getMonths().length; i++ )
				if( HolidayDatesTableModel.DATE_SYMBOLS.getMonths()[i].length() > 0 )
					ivjJComboBoxMonth.addItem(HolidayDatesTableModel.DATE_SYMBOLS.getMonths()[i] );
				
/*			ivjJComboBoxMonth.addItem("January");
			ivjJComboBoxMonth.addItem("February");
			ivjJComboBoxMonth.addItem("March");
			ivjJComboBoxMonth.addItem("April");
			ivjJComboBoxMonth.addItem("May");
			ivjJComboBoxMonth.addItem("June");
			ivjJComboBoxMonth.addItem("July");
			ivjJComboBoxMonth.addItem("August");
			ivjJComboBoxMonth.addItem("September");
			ivjJComboBoxMonth.addItem("October");
			ivjJComboBoxMonth.addItem("November");
			ivjJComboBoxMonth.addItem("December");
*/			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxMonth;
}
/**
 * Return the JComboBoxYear property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxYear() {
	if (ivjJComboBoxYear == null) {
		try {
			ivjJComboBoxYear = new javax.swing.JComboBox();
			ivjJComboBoxYear.setName("JComboBoxYear");
			// user code begin {1}

			ivjJComboBoxYear.setMaximumRowCount(5);
			ivjJComboBoxYear.addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );

			java.util.GregorianCalendar gc = new java.util.GregorianCalendar();
			gc.setTime( new java.util.Date() );
			int minYear = gc.get( gc.YEAR );
			
			for( int i = minYear; i < (minYear+150); i++ ) //150 year range SEEMS reasonable
				ivjJComboBoxYear.addItem( new Integer(i) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxYear;
}
/**
 * Return the JLabelDay property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDay() {
	if (ivjJLabelDay == null) {
		try {
			ivjJLabelDay = new javax.swing.JLabel();
			ivjJLabelDay.setName("JLabelDay");
			ivjJLabelDay.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelDay.setText("Day:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDay;
}
/**
 * Return the JLabelHolidayName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHolidayName() {
	if (ivjJLabelHolidayName == null) {
		try {
			ivjJLabelHolidayName = new javax.swing.JLabel();
			ivjJLabelHolidayName.setName("JLabelHolidayName");
			ivjJLabelHolidayName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelHolidayName.setText("Holiday Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHolidayName;
}
/**
 * Return the JLabelMonth property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMonth() {
	if (ivjJLabelMonth == null) {
		try {
			ivjJLabelMonth = new javax.swing.JLabel();
			ivjJLabelMonth.setName("JLabelMonth");
			ivjJLabelMonth.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMonth.setText("Month:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMonth;
}
/**
 * Return the JLabelYear property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelYear() {
	if (ivjJLabelYear == null) {
		try {
			ivjJLabelYear = new javax.swing.JLabel();
			ivjJLabelYear.setName("JLabelYear");
			ivjJLabelYear.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelYear.setText("Year:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelYear;
}
/**
 * Return the JPanelHold property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelHold() {
	if (ivjJPanelHold == null) {
		try {
			ivjJPanelHold = new javax.swing.JPanel();
			ivjJPanelHold.setName("JPanelHold");
			ivjJPanelHold.setLayout(new java.awt.FlowLayout());
			getJPanelHold().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelHold().add(getJButtonCancel(), getJButtonCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHold;
}
/**
 * Return the JTextFieldHolidayName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldHolidayName() {
	if (ivjJTextFieldHolidayName == null) {
		try {
			ivjJTextFieldHolidayName = new javax.swing.JTextField();
			ivjJTextFieldHolidayName.setName("JTextFieldHolidayName");
			ivjJTextFieldHolidayName.setPreferredSize(new java.awt.Dimension(150, 21));
			ivjJTextFieldHolidayName.setMinimumSize(new java.awt.Dimension(150, 21));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldHolidayName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:15:35 PM)
 * @return int
 */
public int getResponse() {
	return response;
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
	getJComboBoxMonth().addActionListener(this);
	getJButtonOk().addActionListener(this);
	getJButtonCancel().addActionListener(this);
	getJComboBoxYear().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("HolidayDateCreationPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(331, 190);

		java.awt.GridBagConstraints constraintsJTextFieldHolidayName = new java.awt.GridBagConstraints();
		constraintsJTextFieldHolidayName.gridx = 3; constraintsJTextFieldHolidayName.gridy = 1;
		constraintsJTextFieldHolidayName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldHolidayName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldHolidayName.weightx = 1.0;
		constraintsJTextFieldHolidayName.ipadx = 73;
		constraintsJTextFieldHolidayName.insets = new java.awt.Insets(15, 1, 2, 8);
		add(getJTextFieldHolidayName(), constraintsJTextFieldHolidayName);

		java.awt.GridBagConstraints constraintsJLabelHolidayName = new java.awt.GridBagConstraints();
		constraintsJLabelHolidayName.gridx = 1; constraintsJLabelHolidayName.gridy = 1;
		constraintsJLabelHolidayName.gridwidth = 2;
		constraintsJLabelHolidayName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelHolidayName.ipadx = 7;
		constraintsJLabelHolidayName.ipady = 1;
		constraintsJLabelHolidayName.insets = new java.awt.Insets(17, 10, 4, 1);
		add(getJLabelHolidayName(), constraintsJLabelHolidayName);

		java.awt.GridBagConstraints constraintsJComboBoxMonth = new java.awt.GridBagConstraints();
		constraintsJComboBoxMonth.gridx = 2; constraintsJComboBoxMonth.gridy = 2;
		constraintsJComboBoxMonth.gridwidth = 2;
		constraintsJComboBoxMonth.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxMonth.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxMonth.weightx = 1.0;
		constraintsJComboBoxMonth.ipadx = 21;
		constraintsJComboBoxMonth.insets = new java.awt.Insets(3, 1, 3, 130);
		add(getJComboBoxMonth(), constraintsJComboBoxMonth);

		java.awt.GridBagConstraints constraintsJLabelMonth = new java.awt.GridBagConstraints();
		constraintsJLabelMonth.gridx = 1; constraintsJLabelMonth.gridy = 2;
		constraintsJLabelMonth.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMonth.ipadx = 7;
		constraintsJLabelMonth.ipady = 1;
		constraintsJLabelMonth.insets = new java.awt.Insets(6, 10, 6, 0);
		add(getJLabelMonth(), constraintsJLabelMonth);

		java.awt.GridBagConstraints constraintsJComboBoxDay = new java.awt.GridBagConstraints();
		constraintsJComboBoxDay.gridx = 2; constraintsJComboBoxDay.gridy = 3;
		constraintsJComboBoxDay.gridwidth = 2;
		constraintsJComboBoxDay.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxDay.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxDay.weightx = 1.0;
		constraintsJComboBoxDay.ipadx = -24;
		constraintsJComboBoxDay.insets = new java.awt.Insets(4, 1, 4, 175);
		add(getJComboBoxDay(), constraintsJComboBoxDay);

		java.awt.GridBagConstraints constraintsJLabelDay = new java.awt.GridBagConstraints();
		constraintsJLabelDay.gridx = 1; constraintsJLabelDay.gridy = 3;
		constraintsJLabelDay.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelDay.ipadx = 8;
		constraintsJLabelDay.ipady = 1;
		constraintsJLabelDay.insets = new java.awt.Insets(7, 10, 7, 11);
		add(getJLabelDay(), constraintsJLabelDay);

		java.awt.GridBagConstraints constraintsJComboBoxYear = new java.awt.GridBagConstraints();
		constraintsJComboBoxYear.gridx = 2; constraintsJComboBoxYear.gridy = 4;
		constraintsJComboBoxYear.gridwidth = 2;
		constraintsJComboBoxYear.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxYear.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxYear.weightx = 1.0;
		constraintsJComboBoxYear.ipadx = -24;
		constraintsJComboBoxYear.insets = new java.awt.Insets(5, 1, 5, 175);
		add(getJComboBoxYear(), constraintsJComboBoxYear);

		java.awt.GridBagConstraints constraintsJLabelYear = new java.awt.GridBagConstraints();
		constraintsJLabelYear.gridx = 1; constraintsJLabelYear.gridy = 4;
		constraintsJLabelYear.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelYear.ipadx = 15;
		constraintsJLabelYear.ipady = 1;
		constraintsJLabelYear.insets = new java.awt.Insets(8, 10, 8, 0);
		add(getJLabelYear(), constraintsJLabelYear);

		java.awt.GridBagConstraints constraintsJPanelHold = new java.awt.GridBagConstraints();
		constraintsJPanelHold.gridx = 1; constraintsJPanelHold.gridy = 5;
		constraintsJPanelHold.gridwidth = 3;
		constraintsJPanelHold.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHold.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelHold.weightx = 1.0;
		constraintsJPanelHold.weighty = 1.0;
		constraintsJPanelHold.ipadx = 159;
		constraintsJPanelHold.ipady = -2;
		constraintsJPanelHold.insets = new java.awt.Insets(5, 5, 4, 6);
		add(getJPanelHold(), constraintsJPanelHold);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	response = PRESSED_CANCEL;
	disposePanel();

	return;
}
/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	response = PRESSED_OK;
	disposePanel();
	
	return;
}
/**
 * Comment
 */
public void jComboBoxMonth_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int maxDay = 31;

	java.util.GregorianCalendar gc = new java.util.GregorianCalendar();
	gc.setTime( new java.util.Date() ); //just set it to todays date just in case
	gc.set( gc.MONTH, getJComboBoxMonth().getSelectedIndex() );

	if( getJComboBoxYear().getSelectedIndex() > 0 )  //the 0 is (none)
		gc.set( gc.YEAR, 
				  Integer.parseInt(getJComboBoxYear().getSelectedItem().toString()) );

	maxDay = gc.getActualMaximum( gc.DAY_OF_MONTH );


	//only reset the day selection if the current
	//  selection is not a valid day for the new month OR year selected
	Integer oldSelection = null;
	if( getJComboBoxDay().getSelectedItem() != null )
		oldSelection = (Integer)getJComboBoxDay().getSelectedItem();
	
	getJComboBoxDay().removeAllItems();
	for( int i = 1; i <= maxDay; i++ )
		getJComboBoxDay().addItem( new Integer(i) );

	if( oldSelection != null )
		getJComboBoxDay().setSelectedItem( oldSelection );

	return;
	
}
/**
 * Comment
 */
public void jComboBoxYear_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	jComboBoxMonth_ActionPerformed( actionEvent );
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		HolidayDateCreationPanel aHolidayDateCreationPanel;
		aHolidayDateCreationPanel = new HolidayDateCreationPanel();
		frame.setContentPane(aHolidayDateCreationPanel);
		frame.setSize(aHolidayDateCreationPanel.getSize());
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
 * Insert the method's description here.
 * Creation date: (1/22/2002 3:26:23 PM)
 */
public void resetValues() 
{
	getJTextFieldHolidayName().setText("");
	getJComboBoxMonth().setSelectedIndex(0);
	getJComboBoxDay().setSelectedIndex(0);
	getJComboBoxYear().setSelectedIndex(0);
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:15:35 PM)
 * @return int
 */
public void setDateOfHoliday( com.cannontech.database.db.holiday.DateOfHoliday hDate )
{
	getJTextFieldHolidayName().setText( hDate.getHolidayName() );

	getJComboBoxDay().setSelectedItem( hDate.getHolidayDay() );

	getJComboBoxYear().setSelectedItem( hDate.getHolidayYear() );
	
	getJComboBoxMonth().setSelectedIndex( hDate.getHolidayMonth().intValue() - 1);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G8BF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BD4D4D712E854E00289A6E6E2544CA18949A1A7A6F1A3B34B2EE60FA11FB193F3464C6463EE1C0D3B634EB8F3CC0619F8E6CD620C1786DA7093D1D081A102E2C44596D4BEB6C4859A9403898928E446ECFC508F3A2169EEDFBF2071172D7BFD2F1B57A0CEF24EF0CE795E2DFBEFD55D3AD5F52BEEBFD1660E2814C4D3A6C209A9087DD5A4A2F475B8C2E72F5C77BC47054971E6247BABG58041EAA
	CB06E7A47495C6471B53518BE929500E06762B77471B3741FB86FAA92D3092DEC270D9D501507BDD6FFCB9BC1F74EC4EE78DE9AF2DD98F4F8F81E2G071FABE44CFFD5CD9643AFE0F88E09A60416F662B15BEAF3992E925A4DGD1G311DF47C82F826914E632D15EC5C0549D1E845CF7718B6E0BABA1A9C0A3B685CE6764CC7CFA62E6C423CCE542EE7F34221BD8FGAFBE89592CB6F8D6B777F49D6B7074DABAA4375B63D6658E07E540F9D9D26C9607476534CB033E8E07EC6FF349D66BA90A30C92ADC274812
	6A74381BA5376CD29D18CFF8EDAA218BB4210D1C98D7D909F947C1FBA140B201DFCD70B6F83FG20BA79FABF59D34D5653F665C9346A8DF75246906B0DF052F546BA3575CE69FBFFCDB6591B669C3C87895017599DEFAE82E882F081C4GECA7B6F1BEFB9FBCEB5A55BACFEF2F47DD6375563A6C4A990FDDF6413B558ABD8C77255C61D16C8851FDEF28ADB610E7B82073DAC7BD0F6AA466B0370119DFBE02964F7E543446C0A70BE3CACC515DD497F33BB51DD05E13C272DE5C9DE823013C4FA3BD6F6593BCC9A9
	863CE3DF5BEBCAE0F412FBB45EC7FA70DE4FA4FBDD886F094CBEA87E47846F5761293D9EA3F85C8E073E65BD5CB65ABE6432B4A5CFC36925CD06FA189ADFE3EAF6D1999C2EE0D9FCC7B16DA9AE4AB3CD4892C67033F4F8AACB8A4163768268AB690DB7D37BAA5C0A7DF78A34E7814C87D882005C7166DC0092B73731286C03B7E8E34D12225676B9DDF6D9C1F46ED7B9DB61A97BD5C5EAF47BD4495DA11F139427546E12FD080DE94F41BAE88F1857C5EC3F83F81C145D32425CD411FAE5D5D6FCF40CB7E7939E43
	1C385559AB8303DEAF62F47DB99970F4C9BE752C578E6E2E0D207DE1B9D8A77D52B9208884601D6A650BEDD8DF4D30FE8700DF686105CCCC6FD3D901E8E135B6F9BCBDFD5ED63C92B45F0D759CCDE2C7843CC73B390E4FE4E15CFA8F44F58F2773674C4DBABAB6E78796DFD286390FBDD509E3F425075B4CBBED5CE6EC9B9EC6452F1DB6349992DFBD54D6D4CF306F4CA5B431DC34ED2BEC9B259F0702CA4FD05F23BC3E7E4EF8A4996028FE67E4633D1A81B24481AC92BA2E5C12CFED36D672495CCABFD74040E4
	3B26F1AA676051F426CF4CA70549CF7BA6133E84201B650DB71FGF0F8398E579DEDB31877DD2CD753E1A16121271F769D72203A32EC4567135CAF3BD51B6C155DF6F871313E322D189F735FCE0F18D3C570DE7077BAF0B01F4057EC45BEA9292A62EC6FD36596D9D51D6EAE2D3F1170727535FB35D041F9B5EF45EB748C003F0B71E737E2DF6970385DB259B4B105751FA73EAA3B64DE903A4E5367D6A97E6BED585EFB64C196AB24EDB8996A17DCFDB86E505CE116348E2FC1F17602A960500516F8319F4F294D
	5269A892F0B8870A0567FCAF774901DD5CA78BAFBF0066FD335BE4F426CCFD58E6AA3AC47D237A126613D426AF330CE5EFA3B10361FDD456E7BF44B2054ADD28AF2C484174AEG9FAFC078A56E8B2FE661FE9EEF189354F9DCAE2AD2216BE732312D7B64CBFDB204E99C58BE957A1B1F3DB9286F9C53A56DDF180DF5695245C43B02F5797CD11EA3C601BCF1G0BAED1FDE6A9D49FF45CC4AB9FB78B5A738116A9F4DC11CFBF6ECC959F67827CB81FB6AEBAE0DCE2B59F379C7049AA44DF150E332BF49C556B3BFB
	31DEC97E58AD75CBFE0BEFG6C55EABD2D4B89F757253271B3D51E7BFD5100F53D945A2BGB27B38DD3C320167FED3FA22501207F2C2673AFDAC576D536CA27F961E7B9D4B45BCA321AF2A0F6F6327B97A7327466773F4B8098D30BDF8BFC7EFB79437BAE7ADBCBB1D0ACFED844776737DCA590E6545E7A3C333DC619D12AB603546825F65G977BB17E5B24CC1D6C057D740CEB02E79275D36BA16C774420AC7451ACAB1D9E25D73697E14DDA248155C2E20C2506F45763773DFC9FDE36FF824FEE48B05ABD359E
	7F99C8629D5F548451E98ACD28EA6FB944733D87B3CEB0B997BCB18DD5FF1BE1B24A3F43B76619E27A296E9776EBFBF075A9F352727F7BE08D5547D3D62E0F5C01F8F36BG574704C13DBEA2G9F8B30E8006BA33B6A6F53478A9B0EAF5D35FD2A6AF1FF5EB3CAD5D0597B2B386C4BC12EC2BF177D438AA359AF78F9CD556D2C56F85761144DF5D37C77D5F37EDE209DA3785F56EFE4CBCB7CDCF7132B7FBE5DBDFE2CDAEFCBDF4912B2CA7969593B2FAE385ED86157F21D544121F98F4D435FA631A1F710666199
	823F1A601BF4F82A2773840F5BD721AF6BB24F9DDF4E4331A9965A8B81168120AB7166FC0093D7F8CC18347DB09AA1B7EF04FAD8F1CB2E1600C31745013ADC2CF3B8A7D97BE4AE56A1B9A7914FC1D69C8D564305AB01EB77873569BC13B5F85EC7C16D99C173687EF539369B47E43E230846659FA771DABD768A0F3F579A7471F76E46C326F5A1626FF246FB7DD54D770BC56DBD30831F9FCD5067G88831881B08FE0B1G3A46757FCA6EA7A3691F98D52B278EF02760FC2C8B4EE5043E6206686B46747C4208FA
	4E3FE624670DAD5946FA261E8FFDB49F7AC5D9F0BE047D3592E8B681503DF8FA906F85BE6B6C57F8FEFC356A6656F35B10F9416B199F349EAAC3FDC5AA2331C448D0D2FDF3B29C9BB2AFD8062C6BC6B2BC534FED32847AE9DDD8C3728787342F8A5CC7759897856D386BDC56F79B8A6139985A68DB70F300EA00D6G9740B800A80038EF79781413F87CC2E8A7FCCBF32B74B0285B811A43E84E638D23311D55E38D117044F402FDA10E61228C7A96DF9F0A9B8EDAF5B4709A90DB074B0036266FF1C4360D867D99
	6C3949204F28AE9C8E56857145FE10466C56E8BC75715773C257C73F4CB32E0FFE15E7DC9F7DB6EF9345838D7DED896F7FC35E707553FBF9C675D39E51450B24367282BB59C7028E5489926B7B0D3A859954D11FC93DB253E3865AEFCDE3E36AD5CFDD29581BB9C9435D79C5068D5DD79AC37F120F4F79E8681F873088A081A0FD8C0F1DF7FEB46259D56F9F666C9A33A3743E046FB05E17BBF6603D76F97A148EB9D877776C4892FDDA14EED465DE2CA6BA6607E43EAAA9DD72103DFB184C27FD2166BF31A3547E
	52BD78FD409E60BC084615F5653C86A99943EB101F79F15E6000F6984054B15C4F6387036FDDE6C15FF3E368BEAD9AE36C074B8D70E8AC54B6E38371D44E59D5FAB98F8BDBB9DD91ECABC5B03F1A51E89EBB14872557D52D27F7C450FB21FFB85B33072457D6076BFD17B46869D321CE9B0B7565296367189F5A11GF1G8B4652730C1E097FB400E3C427A275423634F8658EB46F96BC372C1E4FAD067689000BGE1GD137703CF5EF9E7631532DE0F58DCE59E5E755FA000F95F4617DBF5DA4354BAE237E59CE
	4C63F4C09D447B3C2D6B44DC5AC1710F9CD82F1FE31386A9FE70E81A988FC841A36954867DF81C9F0B3EA325EB459C5AC171CFA793BC290AC1EEBBC2B417BFF6D21F4B270E233AAC9947F37DDDE7757DB6566F907D0F9DB36A3FAA7AADADC635C224681F5DE7B4BFDA7427159975470D6367704F1378B93CE09C0FCB660250F1E5C301F1DC49A8B00E77598543477B0202D071005AFFFAF9307D670E57EC3EE4FC289A24EE107CB6B51E569AA9827F99413769703486F94D4FEBB59774AD9C4F75331464F5C7A644
	1BCFCD60388317B16E8234DD82F7974179219DA9F0E3C9BE968D6DF98217B848757D2A4095905CCAE8275F4AF1D28567DBA8F08FD7F03E1582E72E6474EC82774BFE0EF3G0E2E6F5EBA7E7BE19C6092EE657EDEFA916779A28746952155FAD2F15AEB252EA692BDE87CFD0B64F0F8FED1B80F3F9BEC1866C5E87B81A243F98D627F1857A0B39F19061A42BEB53C83DA52FA4894934EFEAB8D57EA109828C1A84F3F55F31E2993A166CCA477E475FAFB463EF98D86D860C97B678474E35B0232D8577FF6C0BF1677
	DE133EFFC7033EBFC5B71F2E6D255DFCED2BDE0AC12E9F9D8DF93F95BE112EE9A6FBB61F5B058AEF7BE08D557B6E22G3DEB3507081ED487FDA7F9CE3FF4A25F33F7BBC35C591128C5677D75AC1F17FB9B2FD17E7B9847358A1C5C42737588017BDD9F9F771C401DA86338D500F35C0EF13FCD32992CDB2FBB8E6477C218F38AE09743DD00777DCCF754EE771473BAE1566D5C163B2C42DFEE6767FBA371174288B8D7A3B82E1560703DA93E932538B77D23797D85533B11F155EC9C3B9B98950F9B4D57D3F463B3
	5087E4ED3DEE9912C4E707A3826BE5B309B7EFG34C5707357F6995F792BE0C0B3A370378F7DC5C850F874B2BEBBFBA53F33372F3745F9D9C6144EB304CE823C2F92F46E2B40B66DD564CED9D1E4BB990D587827C88CAA1A94EFBE82F0F19246E58DE68D3396CA33F21070F73A0378B7133545G1DD8G0A3B0D7078894092405135BC0E5D90D1FEF915437123363922FFA45BCC5359E64224C05BCC3EA35E7C4A6A552B9345BE5FBF64AEECA420739E3B49F9CBC7BD0F1EEFEF11181CFE87BD5F9285FE9941676A
	7074DC483071F321887AA26E60F7EF117B48379750DEFFA71CC1G55G97G7CF772BC611F8BCA508875CB2B475B840904CBF7745375DD2C3F396FA81ED94363EF6C3FBE0862A63C17986AFEAB62CE96FF59F3D96BAE02276B5BD50C5737987AD68264C602BD831C8238984957373A20182E2F5E6ED4EBD4D56AF010DF9678624EB965818D0D13E53A364BA7795A544821B1A59CF0B3A20D6EDC3EB92B5749ACB666B976DCE4C00B4E7B4DB17D3C65EC9C1ACC1F1913C34D6BEE99EE8F0AC24E7BFD1FFEDEB59B57
	4C1E7610736A4B74737CECDC84FBC6994C5B01687DC7AA62FF89470762E00FC90C58E9C142CE7EEBBD37135C4726A35B9B6FA56E8AE1A7310CEF72DDDA8E322CFB559ADAB3B51773D8D38D7D2DGD781A26F2277CAB456DC378E97EBF0FCD947F4005FE3E06E5C3BE82C416D0570FE9C847BB3449B9AABCB92C95D42FF7BC0BC061E4B5F2061696F9222EF6FE57C5C5333072DBFEAC07F4DD9655A8A435FE7B1ACE66BCEE04F39376EE2B4FE76B7BD0D74E985264490B40A6E26F30F5C2D69106E49EE9CA2180C83
	95788E51AD8F640B9F9FECCEF09D7C71C9D14561627CC3E5FB79DECEAF55667D49FFA373284C733F5667E7797B138D733315DF9FB4B9182C91BA19DF05FD277C138EEB7C7FD0FEA37C695887AA48BEF46CB134DDCA3B8A17774CA77E47263D3F23FB4F36FDD75F0E988165F168FB65C1E368FD7BC25712D17B0CEB49C776995712B3778DFFB7983BAF743795F376693F2D20E740BAD29B4F037DDF8C0026441BB3810A2670785F38FFA7A2715FA076575B3B709D997E22CB657C0B0BC32F75F071485F01D59763BB
	06F68F38F7AF264F713545F83FD40F17A0596FD445D8C70A334B2152218CFF26985B1BCB6EA4E81AC3CFA6B5048D56F5F18A4F2BE7DF63F935AAF0EFAA98779C34978BDCBF29A392200D6E6138C78EE0DCA6348B85EE1D0553AB91383F10FAA38C5A1182B7678A2909219DABF079D7795CF982B726056352FFA0EADDD257DE0436D7601EA3720D037694015BFB0E4F0D92388B24FEC900B63A1763AE136FCCB221DDA8F0EFE509F55C4BF3A1E7B9F64CB0E84FG18873088E0B9C07254F8F396C0B1408900B6GAF
	40B80029GB3816682AC84D8BA155BD51785A7336724EE16B161744223904B82A4AB3E09F1A912931B974B557C65BCD7DB8934CA6E63756061C3813FE3519A3E416599E8E257FF6C8EF5DEB92F279D77719A7E9DC2B38A5AF1G8B81EE6C4C65312C9E9F914C1F7EC86EA216810D64E9FC2DCFD8768F3356E645631595F510E24997B50C565BC711306995165D21E800A71E06A5FF5E5EADF7282DD21710A5398A1F5B05A04789GBB40D500C80098A15BDB25C54348C61527ED833B67AA8B6D63B9E5A37B78AEE2
	07B278E66B20175D099715613BDACFE727CF963164C0D93A40B57A1ABDBEA73ECB27E7CC4C49507559B3445767429A67CF637949CF2F8D171F703C04430AE90135D06A74F8735DC87B4EAF837DD1B12E65567A08BF41787C693C161CF49657241D7836160CF90878EB93743709B1CF9F882CDBE763A31C55F1BBADA365D67C5BC07CBC253BE3A05F4A830F0D3AF199E84B71C0F9769F4772CC01F1D1C21E1FDF89146775CFB4F9AA2E0EA4CF0A0EDF30BC8BF572D0FAD3DBC62AD35768E8AE1E9E38B7895335BA15
	4A1E579F383E43A4E62E02F129B3787A5EBC9B383E770EE96BBBF1EE2475696DA5F8FD1540E3D3003EFDE50172CCA476EF03F14DC21E49D90172641E506479D356C872248DA3CF588C9A4FF09F752BEB65217DF7DC79487EBB295C780C3E3BFC6899BD355C780C7EE1397E0C2638074A71FEF6C16CEF9363E2846EAB01FB1C50443873CEBB7EE90461E79339B07E140C99F27C3F903986882E0D617ECD077B0A611EAD47B188166C70A8FC6E7CF232F7CE170B369710B1CEB704A4210FD7080CCE2F24137BF5017B
	8A71DC737A10FB7C9B4BD1A3EEF25E73231EC76FC90A4877EE91B3026FCFCA89FE6A0C607B139562CC0E063E547B7959723F15182648D56951467473174ACC22C78869124BF919FC425882835DDD6CB7162A50B61AD9B5320D66D5995B682E2A21B6DAD4E5EC2316AA3D0DD23FBCDE0390776B18EB582FC254C425E5A5D094156E9D2686EA2F45AD7A5EED53681A4F5E88DDBA6F63C6ED5E3483BB0367B5C050A95AE9879DFB3AF69667435452C3223EECBC2D71FF38C9237364155151794599BCB774BAFB3E5078
	7C24C56393F7F6F4FCFE572A51F9763CC6471DB5BABA3F46EE4AFC44E2555EABF46F6F17FF7F35E7C6457749037EFF41578E55420AE73E0DD2D4E47A3CC7C5E6FAE962D350C6212DC6924E16749F36A67D6A21A493CAEEC4E66ABA0DA8995BFFA3CAA16E51081240F57CDE26FA6C72E371BBD05BBC54D6EB6E7877B7E6FEE34BB4A19390F44814C24AG8E5A0486CED39BFD1750F514BCCC4B0468C10F39BD095F85B6197560108C6B5B40FECC4F473F26DBE1296487F2F8C936C90370C841BFE6DB85877CC37D8F
	84AD9363004DF7E3509885ED88302C679FA87B75027FBC67CBC266C6140D8F7696D9A1F1CC36636FED58FFA6EAC0E9DE8FE44DB8BD84C57A48A345278E3A64A6145979A57E105E274A769A9EA1FDAA5AC2BECABD5827BADD87CFC9BE070A23DAD593CAF6637F7604B653EE17646EBAC843E59551E4E24D8962F2B890A310B703480B29D884150CB99BF7971CDE9A1704CC8DE82D4BA3599B248EC8769BD11A6AD0E41F4363327B3D47A92154812C08F5ECF3370B656345DA706FF3C2689E7F7D919B16EDE8G857A
	95F4100C44F90CC50C3057B9A485D849CA230A7F9B17C7A994FDE3BED2BFFE7964368BA614489609D51F61774276F5CA3DCE57A03C58089F13CD592602340A647661EFF8C1327CA049F817E0E0397B4512FB9EFD7F2F0401B6D5C5BB28AAE16A41AE5953FB302633536916858150AB104F93647198CB50184D7E49DBCE3F363A9D6C400612FACBCA483F25645FC3785FD202A9A51852C3205C84B46CDFF03F1F2F19980F70D93C7D96A8EEA575BF5EFE7141B72B9316436A9AD08A79E497B6A1DD9B75D9DFEF3B2C
	CC18F9CF5E3AA9CBEE47FB6445266247F4377B83BCE9AD7300CD843BE908D5E16C162166015159E213F0AB178C7D087CDBB06AA563D93B485C5DC2527D1A24E6A6A97EB387484F63C372BF1E7C07B57957B07949F578BF1E24D6CD52D41D26D3795D725EEF757761AB47E7053CD3F7C453BB652B515A5DB23D17F814647EF100FFB51A670A6F159DC0216FB7DAFAA517CB15DCBDB5EEBB3E6B289DD4E56D4E7309F2CC5F474730CE5A77A3D2ABF46B667CBFD0CB8788D2B8D8B1E896GG24BFGGD0CB818294G
	94G88G88G8BF954ACD2B8D8B1E896GG24BFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2296GGGG
**end of data**/
}
}
