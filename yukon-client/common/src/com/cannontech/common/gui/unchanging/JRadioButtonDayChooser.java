package com.cannontech.common.gui.unchanging;

/**
 * Insert the type's description here.
 * Creation date: (2/14/2001 3:46:20 PM)
 * @author: 
 */
public class JRadioButtonDayChooser extends javax.swing.JPanel {
	private javax.swing.ButtonGroup buttonGroup = null;
	private javax.swing.JRadioButton ivjJRadioButtonFriday = null;
	private javax.swing.JRadioButton ivjJRadioButtonMonday = null;
	private javax.swing.JRadioButton ivjJRadioButtonSaturday = null;
	private javax.swing.JRadioButton ivjJRadioButtonSunday = null;
	private javax.swing.JRadioButton ivjJRadioButtonThursday = null;
	private javax.swing.JRadioButton ivjJRadioButtonTuesday = null;
	private javax.swing.JRadioButton ivjJRadioButtonWednesday = null;
	public static final String MONDAY_STRING = "Monday";
	public static final String TUESDAY_STRING = "Tuesday";
	public static final String WEDNESDAY_STRING = "Wednesday";
	public static final String THURSDAY_STRING = "Thursday";
	public static final String FRIDAY_STRING = "Friday";
	public static final String SATURDAY_STRING = "Saturday";
	public static final String SUNDAY_STRING = "Sunday";
	public static final String HOLIDAY_OFF_HOLIDAY = "Holiday off";
	public static final String EXCLUSION_HOLIDAY = "Exclusion";
	public static final String FORCE_RUN_HOLIDAY = "Force run";
	private javax.swing.JComboBox ivjJComboBoxHoliday = null;
/**
 * JRadioButtonDayChooser constructor comment.
 */
public JRadioButtonDayChooser() {
	super();
	initialize();
}
/**
 * JRadioButtonDayChooser constructor comment.
 * @param layout java.awt.LayoutManager
 */
public JRadioButtonDayChooser(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * JRadioButtonDayChooser constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public JRadioButtonDayChooser(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * JRadioButtonDayChooser constructor comment.
 * @param isDoubleBuffered boolean
 */
public JRadioButtonDayChooser(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:59:09 PM)
 * @return javax.swing.ButtonGroup
 */
private javax.swing.ButtonGroup getButtonGroup() 
{
	if( buttonGroup == null )
	{
		buttonGroup = new javax.swing.ButtonGroup();
		buttonGroup.add( getJRadioButtonMonday() );
		buttonGroup.add( getJRadioButtonTuesday() );
		buttonGroup.add( getJRadioButtonWednesday() );
		buttonGroup.add( getJRadioButtonThursday() );
		buttonGroup.add( getJRadioButtonFriday() );
		buttonGroup.add( getJRadioButtonSaturday() );
		buttonGroup.add( getJRadioButtonSunday() );
	}
	
	return buttonGroup;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxHoliday() {
	if (ivjJComboBoxHoliday == null) {
		try {
			ivjJComboBoxHoliday = new javax.swing.JComboBox();
			ivjJComboBoxHoliday.setName("JComboBoxHoliday");
			ivjJComboBoxHoliday.setToolTipText("Holiday");
			// user code begin {1}
			
			getJComboBoxHoliday().addItem(HOLIDAY_OFF_HOLIDAY);
			getJComboBoxHoliday().addItem(EXCLUSION_HOLIDAY);
			getJComboBoxHoliday().addItem(FORCE_RUN_HOLIDAY);
			
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
 * Return the JRadioButtonFriday property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonFriday() {
	if (ivjJRadioButtonFriday == null) {
		try {
			ivjJRadioButtonFriday = new javax.swing.JRadioButton();
			ivjJRadioButtonFriday.setName("JRadioButtonFriday");
			ivjJRadioButtonFriday.setMnemonic('f');
			ivjJRadioButtonFriday.setText("Friday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonFriday;
}
/**
 * Return the JRadioButtonMonday property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonMonday() {
	if (ivjJRadioButtonMonday == null) {
		try {
			ivjJRadioButtonMonday = new javax.swing.JRadioButton();
			ivjJRadioButtonMonday.setName("JRadioButtonMonday");
			ivjJRadioButtonMonday.setSelected(true);
			ivjJRadioButtonMonday.setMnemonic('m');
			ivjJRadioButtonMonday.setText("Monday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonMonday;
}
/**
 * Return the JRadioButtonSaturday property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonSaturday() {
	if (ivjJRadioButtonSaturday == null) {
		try {
			ivjJRadioButtonSaturday = new javax.swing.JRadioButton();
			ivjJRadioButtonSaturday.setName("JRadioButtonSaturday");
			ivjJRadioButtonSaturday.setMnemonic('s');
			ivjJRadioButtonSaturday.setText("Saturday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonSaturday;
}
/**
 * Return the JRadioButtonSunday property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonSunday() {
	if (ivjJRadioButtonSunday == null) {
		try {
			ivjJRadioButtonSunday = new javax.swing.JRadioButton();
			ivjJRadioButtonSunday.setName("JRadioButtonSunday");
			ivjJRadioButtonSunday.setMnemonic('u');
			ivjJRadioButtonSunday.setText("Sunday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonSunday;
}
/**
 * Return the JRadioButtonThursday property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonThursday() {
	if (ivjJRadioButtonThursday == null) {
		try {
			ivjJRadioButtonThursday = new javax.swing.JRadioButton();
			ivjJRadioButtonThursday.setName("JRadioButtonThursday");
			ivjJRadioButtonThursday.setMnemonic('h');
			ivjJRadioButtonThursday.setText("Thursday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonThursday;
}
/**
 * Return the JRadioButtonTuesday property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonTuesday() {
	if (ivjJRadioButtonTuesday == null) {
		try {
			ivjJRadioButtonTuesday = new javax.swing.JRadioButton();
			ivjJRadioButtonTuesday.setName("JRadioButtonTuesday");
			ivjJRadioButtonTuesday.setMnemonic('t');
			ivjJRadioButtonTuesday.setText("Tuesday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonTuesday;
}
/**
 * Return the JRadioButtonWednesday property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonWednesday() {
	if (ivjJRadioButtonWednesday == null) {
		try {
			ivjJRadioButtonWednesday = new javax.swing.JRadioButton();
			ivjJRadioButtonWednesday.setName("JRadioButtonWednesday");
			ivjJRadioButtonWednesday.setMnemonic('w');
			ivjJRadioButtonWednesday.setText("Wednesday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonWednesday;
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return java.lang.String[]
 */
public int getSelectedDay() 
{
	if( getJRadioButtonMonday().isSelected() )
		return java.util.GregorianCalendar.MONDAY;
	else if( getJRadioButtonTuesday().isSelected() )
		return java.util.GregorianCalendar.TUESDAY;
	else if( getJRadioButtonWednesday().isSelected() )
		return java.util.GregorianCalendar.WEDNESDAY;
	else if( getJRadioButtonThursday().isSelected() )
		return java.util.GregorianCalendar.THURSDAY;
	else if( getJRadioButtonFriday().isSelected() )
		return java.util.GregorianCalendar.FRIDAY;
	else if( getJRadioButtonSaturday().isSelected() )
		return java.util.GregorianCalendar.SATURDAY;
	else // if( getJRadioButtonSunday().isSelected() )
		return java.util.GregorianCalendar.SUNDAY;
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return java.lang.String[]
 */
public String getSelectedDays8Chars() 
{
	StringBuffer buff = new StringBuffer("NNNNNNNN");
	
	if( getJRadioButtonSunday().isSelected() )
		buff.setCharAt( 0, 'Y' );
	if( getJRadioButtonMonday().isSelected() )
		buff.setCharAt( 1, 'Y' );
	if( getJRadioButtonTuesday().isSelected() )
		buff.setCharAt( 2, 'Y' );
	if( getJRadioButtonWednesday().isSelected() )
		buff.setCharAt( 3, 'Y' );
	if( getJRadioButtonThursday().isSelected() )
		buff.setCharAt( 4, 'Y' );
	if( getJRadioButtonFriday().isSelected() )
		buff.setCharAt( 5, 'Y' );
	if( getJRadioButtonSaturday().isSelected() )
		buff.setCharAt( 6, 'Y' );
		
	if( getJComboBoxHoliday().getSelectedItem().toString().equalsIgnoreCase(EXCLUSION_HOLIDAY) )
		buff.setCharAt( 7, 'E' );
	else if( getJComboBoxHoliday().getSelectedItem().toString().equalsIgnoreCase(FORCE_RUN_HOLIDAY) )
		buff.setCharAt( 7, 'F' );
	else
		buff.setCharAt( 7, 'N' );

	return buff.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return java.lang.String[]
 */
public String getSelectedDayString() 
{
	if( getJRadioButtonMonday().isSelected() )
		return MONDAY_STRING;
	else if( getJRadioButtonTuesday().isSelected() )
		return TUESDAY_STRING;
	else if( getJRadioButtonWednesday().isSelected() )
		return WEDNESDAY_STRING;
	else if( getJRadioButtonThursday().isSelected() )
		return THURSDAY_STRING;
	else if( getJRadioButtonFriday().isSelected() )
		return FRIDAY_STRING;
	else if( getJRadioButtonSaturday().isSelected() )
		return SATURDAY_STRING;
	else 
		return SUNDAY_STRING;
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("JRadioButtonDayChooser");
		setLayout(new java.awt.GridBagLayout());
		setSize(345, 46);

		java.awt.GridBagConstraints constraintsJRadioButtonMonday = new java.awt.GridBagConstraints();
		constraintsJRadioButtonMonday.gridx = 1; constraintsJRadioButtonMonday.gridy = 1;
		constraintsJRadioButtonMonday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonMonday.ipadx = -2;
		add(getJRadioButtonMonday(), constraintsJRadioButtonMonday);

		java.awt.GridBagConstraints constraintsJRadioButtonTuesday = new java.awt.GridBagConstraints();
		constraintsJRadioButtonTuesday.gridx = 2; constraintsJRadioButtonTuesday.gridy = 1;
		constraintsJRadioButtonTuesday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonTuesday.ipadx = -3;
		constraintsJRadioButtonTuesday.insets = new java.awt.Insets(0, 1, 0, 6);
		add(getJRadioButtonTuesday(), constraintsJRadioButtonTuesday);

		java.awt.GridBagConstraints constraintsJRadioButtonThursday = new java.awt.GridBagConstraints();
		constraintsJRadioButtonThursday.gridx = 5; constraintsJRadioButtonThursday.gridy = 1;
		constraintsJRadioButtonThursday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonThursday.ipadx = 14;
		constraintsJRadioButtonThursday.insets = new java.awt.Insets(0, 1, 0, 5);
		add(getJRadioButtonThursday(), constraintsJRadioButtonThursday);

		java.awt.GridBagConstraints constraintsJRadioButtonFriday = new java.awt.GridBagConstraints();
		constraintsJRadioButtonFriday.gridx = 1; constraintsJRadioButtonFriday.gridy = 2;
		constraintsJRadioButtonFriday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonFriday.ipadx = -3;
		constraintsJRadioButtonFriday.insets = new java.awt.Insets(0, 0, 2, 11);
		add(getJRadioButtonFriday(), constraintsJRadioButtonFriday);

		java.awt.GridBagConstraints constraintsJRadioButtonWednesday = new java.awt.GridBagConstraints();
		constraintsJRadioButtonWednesday.gridx = 3; constraintsJRadioButtonWednesday.gridy = 1;
		constraintsJRadioButtonWednesday.gridwidth = 2;
		constraintsJRadioButtonWednesday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonWednesday.ipadx = -2;
		constraintsJRadioButtonWednesday.insets = new java.awt.Insets(0, 1, 0, 0);
		add(getJRadioButtonWednesday(), constraintsJRadioButtonWednesday);

		java.awt.GridBagConstraints constraintsJRadioButtonSaturday = new java.awt.GridBagConstraints();
		constraintsJRadioButtonSaturday.gridx = 2; constraintsJRadioButtonSaturday.gridy = 2;
		constraintsJRadioButtonSaturday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonSaturday.ipadx = -1;
		constraintsJRadioButtonSaturday.insets = new java.awt.Insets(0, 1, 2, 1);
		add(getJRadioButtonSaturday(), constraintsJRadioButtonSaturday);

		java.awt.GridBagConstraints constraintsJRadioButtonSunday = new java.awt.GridBagConstraints();
		constraintsJRadioButtonSunday.gridx = 3; constraintsJRadioButtonSunday.gridy = 2;
		constraintsJRadioButtonSunday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonSunday.ipadx = -4;
		constraintsJRadioButtonSunday.insets = new java.awt.Insets(0, 1, 2, 4);
		add(getJRadioButtonSunday(), constraintsJRadioButtonSunday);

		java.awt.GridBagConstraints constraintsJComboBoxHoliday = new java.awt.GridBagConstraints();
		constraintsJComboBoxHoliday.gridx = 4; constraintsJComboBoxHoliday.gridy = 2;
		constraintsJComboBoxHoliday.gridwidth = 2;
		constraintsJComboBoxHoliday.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxHoliday.weightx = 1.0;
		constraintsJComboBoxHoliday.ipady = -1;
		constraintsJComboBoxHoliday.insets = new java.awt.Insets(0, 4, 2, 3);
		add(getJComboBoxHoliday(), constraintsJComboBoxHoliday);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	getButtonGroup();
	
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		JRadioButtonDayChooser aJRadioButtonDayChooser;
		aJRadioButtonDayChooser = new JRadioButtonDayChooser();
		frame.setContentPane(aJRadioButtonDayChooser);
		frame.setSize(aJRadioButtonDayChooser.getSize());
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
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
public void setEnabled(boolean value)
{

	getJRadioButtonMonday().setEnabled( value );
	getJRadioButtonTuesday().setEnabled( value );
	getJRadioButtonWednesday().setEnabled( value );
	getJRadioButtonThursday().setEnabled( value );
	getJRadioButtonFriday().setEnabled( value );
	getJRadioButtonSaturday().setEnabled( value );
	getJRadioButtonSunday().setEnabled( value );
	getJComboBoxHoliday().setEnabled( value );
	
	super.setEnabled( value );
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 */
public void setSelectedDay( int day ) 
{
	if( day == java.util.GregorianCalendar.TUESDAY )
		getJRadioButtonTuesday().setSelected( true );
	else if( day == java.util.GregorianCalendar.WEDNESDAY )
		getJRadioButtonWednesday().setSelected( true );
	else if( day == java.util.GregorianCalendar.THURSDAY )
		getJRadioButtonThursday().setSelected( true );
	else if( day == java.util.GregorianCalendar.FRIDAY )
		getJRadioButtonFriday().setSelected( true );
	else if( day == java.util.GregorianCalendar.SATURDAY )
		getJRadioButtonSaturday().setSelected( true );
	else if( day == java.util.GregorianCalendar.SUNDAY )
		getJRadioButtonSunday().setSelected( true );
	else if( day == java.util.GregorianCalendar.MONDAY )
		getJRadioButtonMonday().setSelected( true );

	// ignore the value if we dont have it
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GACF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8DF0D455958ECA88C54609CE264DGE321A24D54B86018F62813BAE9E494AD55D8235291C72AE0E3256A54340262F0733781899004A214285184A39AA97293124D86134D8F84E20DE48901504ACB76A559E4773D1737EF738389745C5FFD593CCD4499B3F3663D7B1DFB6EB9675EF36EBD77ED504E8F6666E4E46EC4A8A387313FE39988B9CA902A121FB9433188B95906CCFF9F82EDC78FE6E743B3
	9AF82BEECA3665232D439B21BD9F5A31B312ED6F427B8E34E3B82D98DE4268C982DE7A6CBD75936B69BA4A756412366E4B0367D620B84070B3DD467AE778F6B3FC9943B9E5E4A234248B773943D74030F2E8B783458245F7857BEFA21AA36D652CFD711639A87D61124CADF89C53181C723A296CC176ACC48BB316F7E3DD0F90BB31CC8F346782E1673350EDBB6C70F4B477F59F6FD4FD87BAA5C5D195C36E6CB9509D709C88A81DBD12526DD13A6D76D3E7A437C7F5868CC3D56A24615A9ED5754BBAC22E9EBC7E
	AEE5A3998F2182E82FA4D85FE12CB3965E9781AD85227ED415B8183DEDD796220427142CEDE17CA976D0BF1ABCC1FFE282EF646EA1F33F208C4F71F4604583A5812D824A6ECD3695836DA5EBFE5751CF61D953E1542ABE1F2A54E81A536B563FD25D32975E6DF660B06C0C5C296AEE04683A267ACAAD6C1908285CF357ADC767A46EB0DF6378B38B500AFBCEE766DA4C490A3803190EDEBA972E5E601CD05DFB15F03A0D5E31B1B8D677B9E456BDFF1A1A15E321BB61097D195359B8F3CC3A17756235FE162CF5A2
	3CE730B820780B844F6F8B62B49E7B890E5B05401B5A47E323EDAB3725A9FB964A7F2249F29EE213EBB2537A288D489B3405464542F21CD725001F843A84B484948B14G34544BE3639FC77F7D9DE323D9528DE74063F5C340D35905E53B60A98F993A5428788DC96914DBA55DA3F5F8E5BFE2FD9617E15BBB46487D0644ECA768B8A1AB32AE999ED50151A51FEC483A1F76F920ECBB6E234B046B7278E4D06053909F7711329D58ED49EF1C555C12A187FBD07E13E5F8AE8724D698918600F7BAAFABAA701C2701
	7F4887EB606373704DB1BC5EE9D9D7E42F5D5E242AFD814D05BDC117F0BFF40864F6B13CBB0068D84B6DE4FF05F62298E77A31F7CC6354F9BA317912BE4CF3E375E13CF7267878DA3F5C46573AEE4B9D287409D316EBCD76D49FDD63BCA5B466DFA1E3E23BE8FB70F05BF5665FF7259421A37A875311A5562A4101576A88785B8C24A9FC0E6FBFD6C8E356A979E59E25DF6B90E032BBB863346F2F4B713EAA8F480AD1A7EB32620697BF0B112472CD41786DD2C5ECBDC0F08D623D96824CAF70E565B8A6A54350BD
	9D81C3EE118D83CE05A0BF15687287BA34E02AF0DDCF17637DC49DD470F1413015656F10BCD0941998ADC4987FB9922B32D776015135EAC0B1A81ED601573BCF9EE639322642C6E25D9B40F9C74F34C552E66C036E7141D46054C54B959C470D8ABF13B1CD878C1F4D7170CCD2F8CC8E967118AC3EBC8FA5FD3BAF53EAAF0C3D23AE33D02531F8C48D46A43569993B356D4F131CE1381F5AFA6797F89F89F73652D838D60547F3011E9E20881547423715FBCC79460220D675FA69140A39EEF2646335117B83B2EC
	D3B831CF0B79EBF13C9342EBE5F3C9796D8EBC17DE531EC06DFEEDBF1ECBD2B774CA835250817FA02D921AA5300656847710FD83770FD5F9CD70ECBF76A5855AE9C0C863737E5896DE9344744DC5ABEF7FD7589AE737C6675B2185673D70C6DE93BCF49C6B9C815ECC0D4F53138E737ED663772B1D9E3235B29FA32B4C6BC23199D5EF43334B237B0DC6C81CA1BE9F51D558DE3C77B2186DED0B471D3DF33560191B200DBFFF6979286AF8BE16EA74FC5CAE708802271BF0FAEE2EA673075B057D903B7DFCBF36D5
	6375A85713EDAEA0AF50D42079C0CBF4BE8F3B0F9FC6131CF70DD07B690A64ED9913487C5CB91EC5F30F357FF0BC07679EBC69D971A8D9731520B39D08E2719A46F67B13ED877DFC8F8D68F89FF3C15BG0A81CAGCA85DA8B345BC836159B3C6FB45A975A86D08CD082D0AAD0BA504EC0326DC80077FD29BF42A2E642915665E23AC91D64372E13C2A946548F5B495FD3196D54162D7A75583285517DFF93EB67FCC739DC537BF653BB4EDB3CA753355AD69D7EAC58D5EDFD9694D4DB1F859FD5EF27B80CE12E0C
	B87F536A094F0AC355D6E7C596613D2058C8FE8D2B81836EE4819CBFEF09FD5B85EDAFD08CD0DC0056A3F4BF3AFB907B5B85059428EA5164CEB4E7G4B5E2FF1590D50AEGF2GDD849A8122BACB8B719EE85E033FD29537B40C18CDDD8DBBC278AE58A1F1874AFF2FF6E7A83FA720638E14FF57672163B7689E5378BE67BFC37867E437C2B570BEDFDE885551A29981DD6838DA9C2A23A5E076E1B642E740A9487C8E55298E1DD43D580262C1BCC77769BBCDF133EA004EEBBE70A87F568AAB3ED77017D872A384
	BF57129FA3784FFDEC450F937C8DA3D67CC441AF2A34622788FE0AB106BFC879F9C349B63A276ED3795D38903036A19ED3DFDC427788F1469D10868D3B7D84AC1DD36AEEA291CA4FB91F3A1149C78F71F3EEB699F3A9EE832D9D62675C50477C1C0BDFB08BB5C51C36BC67D63A8EE5668F337AE2B8F84E45415D176AFCD163BAB560478DE36C5DFE331FF85D7F8BF4G08729B4770F10E6E9F433FB2060F6D3DA7534C3FEB8CBF47A4CFFD7B5DBE6E5B7A5F45A16FAFAB421E612FB21FF6DE264F6656A2D4BC634D
	DCBA6F3B1C6FA34B5A42945D7C3B435F750DCCFF53E5BE47238D66DAA2F15B214C4DE16C184A744F3F9C1C63D2B1478FD56031717E3A8A7894FBD3E085D7B8F6DCE0AE016DA96158CC01150DF2EC0940AED4F2EC7D959EF75D5E3D567E736CE53E6786F8FDF5E4041FB91F295F6F3BC5457714532FDB0E56B7BE8D6707F70456B799829FA078108927B9BACF6468D460250D703A275A09777726D1280D01A200E2019683250E723A67416A03E8123A4725EACDF0B8FACD4799756FE56D7BFD4F79EDAE0F4104FBFF
	0696CFDB1791AE8653C6E96CED3CCA1FE92EA202D37F9E2FA5F59D70DCC0C3C051C07301922FF27F365514D27F6A5D9E2346B0244E9ED249F267DABD72E0904607A3756D8D9D4B25DC8D56AF2B60FDADEB67DD9BDF8BD11B2A6A71BED088FC87108B488BB4739A3767FC4D24F5E67D10E51DC96DFA24A2F44E63E16CA4E6CF2A05DDD48E59C36552206FFA56BFBFA21CDC6DB8FD4550371C75EF8AAB77FEC9285CA568EB307E53434AD50E064A4D013E0BD87FA5E1653E2D8C15DB8EFD57307EE8CAB8393CG165B
	E9414774C9D8B9BF7E64445EC7CC6FBFD7FF286F9FC15DC9BF288E9A270D4E70B5F133533AA6EEF5DA5744971D93573CEEE7786FA3FDCE737791F667EF40B9660035E9839A810A861ABF056758BA67C7086418C53E573B3B65DA15FC15B4387E953561FDFD3EF672EF19EBEA71FD38C3053357074767785FEA71FA992AC6C00A3DDE0B67C877F4779834AB435F2A456715D76EA2B03D9BEE77E3AC897CCA1D426F0B79864652203DDEE0678916FFC3322D70860E5DE770FBE5698DFCDE6A6B437B59D4EF3D26E76A
	2D57342DFE62FB4E257AF0EBCE6DDBDD417D9891B66F37F3BF2285B6434E7D18A330F76DDCF60D405A05AC3A11E309C2B6CFE097045E0B823B2D024BEA825BD041E5C784F634044BAE93585492AE3BD2E02BCB386C9A011D9965320E299C7B71A817ED96586B23DC76224086AA39EC3C406E2D6432C9825B6B60324B84D6C46A200248E45B11C8B127815E2FBA1247C6D4C378583839E1729C78C98379CE84316B9A56E40A4DE94077D5352B4BAF0BDC1B5710AF30C6FF336A77600FCD749E768909F73F0C3F5149
	6E39115856AF7D7C373368C8FEA7ADA1316DD3E4B8C0BD1D345FF102BD8D34CA74538C7CBD54G4353A3716F15012F20095A26617ECF98F5936C01D9A1D48EB2180A902DD31A607D2D8D9BB660F72AFFAB093597F19BF8D4DF6CFF027A6659A75217B3093EC5707E6698FD55C45FE3C009C2DF4A57C1FD0FCF28EF73A47A16417BFB6C1D6A3B1164436F01168BFD2FB587756D1AD0DFD6080F217A52467977BF12C32FG258BFDC5E7027A5617CC24AFB7C4E7283E6C2850753BC2726E4FB82722383E3E16203E2D
	D7A7DB3F097CAB04B1B70C5177D3123FAF81158AFDB73B027A8EB9A673EFA2FD8E98F39D53C7F3419E40EBEE282A576551DC1092086A3C7B2CA9C68393695C946267492831BAAFC651FB8976136A4C23792787E4DA774F9DB47FD6DF7E76B14B5F8FB075C4513AEB04BD93230A189FE78D7CAC7115303B44236D66B173E7FD181991E64C84B6D6D2D470BE7B8199936E97A878C7676505F388717B4A1F7278FD25604E59286E29F599C5E1F42433316B2687F524752E4F25366F33935B0D9226630FA4FFA86F9C65
	75F05E7AEF023CD995045745F99F3484F991A504B748F9CED710F76BA86179B96FAFE7033CDFD592DE00733690FF474F472BDFD23E01CBCED6FFFDF81E7C0EGBE46CE6767C7C66BE770EC15FA590D86DF37D41DD4DDGB66B2AA66B46B0C5492FA8EC2C028BF8DCBA460956FD6146004A6D94DC773F6E68053341A5F58BDBCE346318DD8CF624822D854A7FD1322D984881548C64869A820A860A83CA84CA81DA85148E34F1C6322DE086776333367D937841BEF01182D217BC3804E436FFFC9E2F3728F7280B0C
	7779F9DC2FFA94B86AA42F67B279F92585BAAA5D6CF7247670E7EFC77B64E7EFDF3BF57D595FBE3E7E9CE8372EBFC75A4D75A7452634637C6C861F5BC43FE982BBAF301B481898BB67F19BBD823F0558057113B2D6A8EA85E257A0415A98B647041D6735C2BB2E9F40659ED567328B5A711E526571FAE97B97240FC701F2C24C47C2E223C713CCF6A7888C5BCD7F6729AC2B8A12B87E2FA847C0B67AD95BC0396C7B3501B672AF5540259F258D34C9FCFD86BE7F4A8CFC7AC199245947E33F0E36716F22879A09F5
	AB878E5B331E3FBDAB93E5B7A29BDD2DC6140D273C91651095E9C4D930DAC39A8A797377232BA2A7713EE333C4F34620190C335932AF9A57176F1B63516CF1A8C63258A0F40F5CFF4D3C8F2F0D5C9DF6EF7FE4A67B0DF2E6687F265C1703479F82BCE6A64F472DB91F2370FBC20BCF72FA8D495BD72338717E609CB664609D657E9CBC3E1F77E1CC4AFBB8875B5DEB1279BFD0CB8788962ED925F88EGGE4A7GGD0CB818294G94G88G88GACF954AC962ED925F88EGGE4A7GG8CGGGGGGGGG
	GGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG328EGGGG
**end of data**/
}
}
