package com.cannontech.dbeditor.wizard.capfeeder;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
 
public class CCFeederNamePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private Integer originalMapLocID = null;
	private javax.swing.JLabel ivjJLabelGeoName = null;
	private javax.swing.JTextField ivjJTextFieldGeoName = null;
	private javax.swing.JTextField ivjJTextFieldSubName = null;
	private javax.swing.JLabel ivjJLabelFeederName = null;
	private javax.swing.JLabel ivjJLabelMapLocation = null;
	private javax.swing.JLabel ivjJLabelAlreadyUsed = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisable = null;
	private javax.swing.JTextField ivjJTextFieldMapLocation = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCFeederNamePanel() {
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
	if (e.getSource() == getJCheckBoxDisable()) 
		connEtoC3(e);
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
	if (e.getSource() == getJTextFieldSubName()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldGeoName()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldMapLocation()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (DistrictNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyNamePanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JCheckBoxDisable.action.actionPerformed(java.awt.event.ActionEvent) --> CCFeederNamePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldMapLocation.caret.caretUpdate(javax.swing.event.CaretEvent) --> CCFeederNamePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextFieldMapLocation_CaretUpdate(arg1);
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
public void eitherTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}
/**
 * Return the JCheckBoxDisable property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisable() {
	if (ivjJCheckBoxDisable == null) {
		try {
			ivjJCheckBoxDisable = new javax.swing.JCheckBox();
			ivjJCheckBoxDisable.setName("JCheckBoxDisable");
			ivjJCheckBoxDisable.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDisable.setMnemonic('d');
			ivjJCheckBoxDisable.setText("Disable Feeder");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisable;
}
/**
 * Return the JLabelAlreadyUsed property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAlreadyUsed() {
	if (ivjJLabelAlreadyUsed == null) {
		try {
			ivjJLabelAlreadyUsed = new javax.swing.JLabel();
			ivjJLabelAlreadyUsed.setName("JLabelAlreadyUsed");
			ivjJLabelAlreadyUsed.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelAlreadyUsed.setText("(Already Used)");
			ivjJLabelAlreadyUsed.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAlreadyUsed;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFeederName() {
	if (ivjJLabelFeederName == null) {
		try {
			ivjJLabelFeederName = new javax.swing.JLabel();
			ivjJLabelFeederName.setName("JLabelFeederName");
			ivjJLabelFeederName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelFeederName.setText("Feeder Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFeederName;
}
/**
 * Return the DistrictNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGeoName() {
	if (ivjJLabelGeoName == null) {
		try {
			ivjJLabelGeoName = new javax.swing.JLabel();
			ivjJLabelGeoName.setName("JLabelGeoName");
			ivjJLabelGeoName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGeoName.setText("Geographical Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGeoName;
}
/**
 * Return the JLabelMapLocation property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMapLocation() {
	if (ivjJLabelMapLocation == null) {
		try {
			ivjJLabelMapLocation = new javax.swing.JLabel();
			ivjJLabelMapLocation.setName("JLabelMapLocation");
			ivjJLabelMapLocation.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMapLocation.setText("Map Location ID:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMapLocation;
}
/**
 * Return the DistrictNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldGeoName() {
	if (ivjJTextFieldGeoName == null) {
		try {
			ivjJTextFieldGeoName = new javax.swing.JTextField();
			ivjJTextFieldGeoName.setName("JTextFieldGeoName");
			ivjJTextFieldGeoName.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldGeoName.setColumns(12);
			// user code begin {1}
			ivjJTextFieldGeoName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_CAP_GEO_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldGeoName;
}
/**
 * Return the JTextFieldMapLocation property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMapLocation() {
	if (ivjJTextFieldMapLocation == null) {
		try {
			ivjJTextFieldMapLocation = new javax.swing.JTextField();
			ivjJTextFieldMapLocation.setName("JTextFieldMapLocation");
			// user code begin {1}

			ivjJTextFieldMapLocation.setDocument( 
					new com.cannontech.common.gui.unchanging.LongRangeDocument() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMapLocation;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSubName() {
	if (ivjJTextFieldSubName == null) {
		try {
			ivjJTextFieldSubName = new javax.swing.JTextField();
			ivjJTextFieldSubName.setName("JTextFieldSubName");
			ivjJTextFieldSubName.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldSubName.setColumns(12);
			// user code begin {1}
			ivjJTextFieldSubName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_CAP_SUBBUS_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSubName;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
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
	CapControlFeeder ccFeeder = null;
	
	if( val == null )
		ccFeeder = (CapControlFeeder)com.cannontech.database.data.capcontrol.CCYukonPAOFactory.createCapControlPAO( 
							com.cannontech.database.data.pao.CapControlTypes.CAP_CONTROL_FEEDER );
	else
		ccFeeder = (CapControlFeeder)val;


	ccFeeder.setName( getJTextFieldSubName().getText() );
	ccFeeder.setGeoAreaName( getJTextFieldGeoName().getText() );

	ccFeeder.setDisableFlag( 
		getJCheckBoxDisable().isSelected() 
		? new Character('Y')
		: new Character('N') );

	 
	if( getJTextFieldMapLocation().getText() == null || getJTextFieldMapLocation().getText().length() <= 0 )
		ccFeeder.getCapControlFeeder().setMapLocationID( new Integer(0) );
	else
		ccFeeder.getCapControlFeeder().setMapLocationID(
				new Integer( getJTextFieldMapLocation().getText() ) );
	
	originalMapLocID = ccFeeder.getCapControlFeeder().getMapLocationID();

	return ccFeeder;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	// user code end
	getJTextFieldSubName().addCaretListener(this);
	getJTextFieldGeoName().addCaretListener(this);
	getJCheckBoxDisable().addActionListener(this);
	getJTextFieldMapLocation().addCaretListener(this);
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
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsJLabelAlreadyUsed = new java.awt.GridBagConstraints();
		constraintsJLabelAlreadyUsed.gridx = 3; constraintsJLabelAlreadyUsed.gridy = 3;
		constraintsJLabelAlreadyUsed.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAlreadyUsed.ipadx = 3;
		constraintsJLabelAlreadyUsed.ipady = 2;
		constraintsJLabelAlreadyUsed.insets = new java.awt.Insets(6, 1, 5, 23);
		add(getJLabelAlreadyUsed(), constraintsJLabelAlreadyUsed);

		java.awt.GridBagConstraints constraintsJLabelFeederName = new java.awt.GridBagConstraints();
		constraintsJLabelFeederName.gridx = 1; constraintsJLabelFeederName.gridy = 1;
		constraintsJLabelFeederName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFeederName.ipadx = 7;
		constraintsJLabelFeederName.insets = new java.awt.Insets(7, 8, 8, 42);
		add(getJLabelFeederName(), constraintsJLabelFeederName);

		java.awt.GridBagConstraints constraintsJTextFieldSubName = new java.awt.GridBagConstraints();
		constraintsJTextFieldSubName.gridx = 2; constraintsJTextFieldSubName.gridy = 1;
		constraintsJTextFieldSubName.gridwidth = 2;
		constraintsJTextFieldSubName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldSubName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldSubName.weightx = 1.0;
		constraintsJTextFieldSubName.ipadx = 167;
		constraintsJTextFieldSubName.insets = new java.awt.Insets(6, 1, 5, 31);
		add(getJTextFieldSubName(), constraintsJTextFieldSubName);

		java.awt.GridBagConstraints constraintsJLabelGeoName = new java.awt.GridBagConstraints();
		constraintsJLabelGeoName.gridx = 1; constraintsJLabelGeoName.gridy = 2;
		constraintsJLabelGeoName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelGeoName.ipadx = 9;
		constraintsJLabelGeoName.insets = new java.awt.Insets(8, 8, 4, 1);
		add(getJLabelGeoName(), constraintsJLabelGeoName);

		java.awt.GridBagConstraints constraintsJTextFieldGeoName = new java.awt.GridBagConstraints();
		constraintsJTextFieldGeoName.gridx = 2; constraintsJTextFieldGeoName.gridy = 2;
		constraintsJTextFieldGeoName.gridwidth = 2;
		constraintsJTextFieldGeoName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldGeoName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldGeoName.weightx = 1.0;
		constraintsJTextFieldGeoName.ipadx = 167;
		constraintsJTextFieldGeoName.insets = new java.awt.Insets(5, 1, 3, 31);
		add(getJTextFieldGeoName(), constraintsJTextFieldGeoName);

		java.awt.GridBagConstraints constraintsJLabelMapLocation = new java.awt.GridBagConstraints();
		constraintsJLabelMapLocation.gridx = 1; constraintsJLabelMapLocation.gridy = 3;
		constraintsJLabelMapLocation.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMapLocation.ipadx = 15;
		constraintsJLabelMapLocation.insets = new java.awt.Insets(5, 8, 3, 18);
		add(getJLabelMapLocation(), constraintsJLabelMapLocation);

		java.awt.GridBagConstraints constraintsJCheckBoxDisable = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDisable.gridx = 1; constraintsJCheckBoxDisable.gridy = 4;
		constraintsJCheckBoxDisable.gridwidth = 2;
		constraintsJCheckBoxDisable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxDisable.ipadx = 29;
		constraintsJCheckBoxDisable.insets = new java.awt.Insets(1, 8, 80, 81);
		add(getJCheckBoxDisable(), constraintsJCheckBoxDisable);

		java.awt.GridBagConstraints constraintsJTextFieldMapLocation = new java.awt.GridBagConstraints();
		constraintsJTextFieldMapLocation.gridx = 2; constraintsJTextFieldMapLocation.gridy = 3;
		constraintsJTextFieldMapLocation.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldMapLocation.weightx = 1.0;
		constraintsJTextFieldMapLocation.ipadx = 87;
		constraintsJTextFieldMapLocation.ipady = 3;
		constraintsJTextFieldMapLocation.insets = new java.awt.Insets(3, 1, 1, 1);
		add(getJTextFieldMapLocation(), constraintsJTextFieldMapLocation);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}


	boolean amfmInterface = false;
	try
	{	
		amfmInterface = com.cannontech.common.util.CtiProperties.getInstance().getProperty(
			com.cannontech.common.util.CtiProperties.KEY_CC_INTERFACE, "NotFound").trim().equalsIgnoreCase(
				com.cannontech.common.util.CtiProperties.VALUE_CC_INTERFACE_AMFM );
	}
	catch( java.util.MissingResourceException e )
	{}
	
	getJLabelMapLocation().setVisible( amfmInterface );
	getJTextFieldMapLocation().setVisible( amfmInterface );
	getJLabelAlreadyUsed().setVisible(false);

	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldSubName().getText() == null
		 || getJTextFieldSubName().getText().length() < 1 )
	{
		setErrorString("The Feeder Name text field must be filled in");
		return false;
	}

	if( getJTextFieldGeoName().getText() == null
		 || getJTextFieldGeoName().getText().length() < 1 )
	{
		setErrorString("The Geographical Name text field must be filled in");
		return false;
	}

	if( getJTextFieldMapLocation().isVisible() && getJLabelAlreadyUsed().isVisible() )
	{
		return false;
	}

	return true;
}
/**
 * Comment
 */
public void jTextFieldMapLocation_CaretUpdate(javax.swing.event.CaretEvent caretEvent) 
{
	//if we are using MapLocation IDs, we must validate them!
	if( getJTextFieldMapLocation().isVisible()
		 && getJTextFieldMapLocation().getText() != null 
		 && getJTextFieldMapLocation().getText().length() > 0 )	
	{
		int[] mapIDs = null;
		if( originalMapLocID != null )
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs( originalMapLocID.intValue() );
		else
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs();
		
		boolean show = false;

		StringBuffer buf = new StringBuffer("The MapLocationID selected is already used, try another\nUsed IDs: ");		
		int id = Integer.parseInt(getJTextFieldMapLocation().getText());

		for( int i = 0; i < mapIDs.length; i++ )
			if( mapIDs[i] == id )
			{
				show = true;
				//setErrorString("The MapLocationID selected is already used, try another");
				for( int j = 0; j < mapIDs.length; j ++ )
				{
					if( (j % 20 == 0) && j != 0 )
						buf.append("\n  ");
						
					buf.append( mapIDs[j] + "," );
				}

				setErrorString( buf.toString() );
				break;
			}
			
		getJLabelAlreadyUsed().setVisible(show);
	}

	fireInputUpdate();

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CCFeederNamePanel aCCFeederNamePanel;
		aCCFeederNamePanel = new CCFeederNamePanel();
		frame.setContentPane(aCCFeederNamePanel);
		frame.setSize(aCCFeederNamePanel.getSize());
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	if( val != null )
	{
		CapControlFeeder ccFeeder = (CapControlFeeder)val;

		getJTextFieldSubName().setText( ccFeeder.getPAOName() );
		getJTextFieldGeoName().setText( ccFeeder.getGeoAreaName() );

		getJCheckBoxDisable().setSelected(
			ccFeeder.getDisableFlag().charValue() == 'Y'
			|| ccFeeder.getDisableFlag().charValue() == 'y' );
		
		//set our map location id values
		originalMapLocID = ccFeeder.getCapControlFeeder().getMapLocationID();
		getJTextFieldMapLocation().setText(
				ccFeeder.getCapControlFeeder().getMapLocationID().toString() );
	}
	
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB2F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8BD0D4D716A4C10351CCB012A9B7518D49109936C2ED482EB365EC4C2C4338B53A63A6599973590C15E59333C355A6D937061DB11F4A5EEE2081C5C5FEF6920DAD622889C1B0C48D0202A00AE2F4A39AF4D0F8508F3A3569573EFEAD0DA1F14F393FFE8D5D9059C955D89EDE5FF36EBD675CF34FB9775C7B9EA9591218E3311690E249A17C5FBE8BA1157B89191A9CB7A2F0B1EA1A0D187EED8558C0
	F2CA3261998F34E5F352EC05C43766C1BB895A05772459B6426FE2B2E46D598DBF2248598F34D377B7FFB53E1C54C2A1A71736933A8A60398EA099F0784CC419A4254B4E71CBB9DE0045CA48F37D586767DD1BB8EEBF34CF83A4GA477337E8B6119CFA5CF295B4F7BF54DCCA4197F38433A967918F88AF08C3031AD7C69A00FD8DE99C0D9AB291EB8E6845A73GF072D9643BE77741337E74150103FD5AE0D51F6275EADEC36DF3D58D392FAB3A93B03EFED5F52AFADDDDF3F3AB7D7519B2281ED63C2A0738DC48
	FB7F45BC4A0B10EAE8E7D25C3EFA1417823F9781AC177837A9FE3F9B668B60F20BB9B66C2867F3687C7291B26FD9EF567AA8F30CF13339A53BC3F3CC883C117B8EDD0F159B506EAB0016F7B94D368920912087A08660BD6A873FAF7CG1ECD3DC633B6B828F91BFC3E439E27FED2F32A9E78DDD787940E7BDC6D53F4A7A1EC2DFF57D599C11F71000D4B3EF8336318CD12BF966B1E72797749723FBDE14D0DE01365495556591719ADD2AF07EC42E4B7C4157D5465F03F8C177D85B14BCE1A2AE565C410BD6F69DD
	56951C4FAA136C1DD7F02D9F26EBED075F966E9F8C7FC30A7718704CC79BA89E5BA3C0CB3FA2FC23F31D5025BD7BDED2D8539E518E33531A2CAD9E26C317E734AEFA83720E73B01979D2179B941FE442B3DD32A99E5B8B01D6B118E6E37E15DD02B19B876DA40085G4BGB2818A3D504FABFC2CF1436EEF68E327955DB89CF0FBA030889B7B7A06B2F82AC1C3D75A3CFEC371762967945D2D74FAD4BF61FD2CC5E8035E30F139C5687BFDA063286AD5F545F0EBDE608E51EA283A1F75A9AC5A00FDF415D2BB5C03
	AA889874914157DED40CEB23780D33BE27E228219E0C6EA8C21BDCD34E81C7E0G3F19DDEACA51DEADB07FAEG0F3443FD145F89D507F4D1D7572EE9D7823E8E1C89D960C5BBF7505C91833FE7FB050DBFAAC39C5152ECF6CD7079E5D11109CF0B3B8F55D774E191E30F56E3DE2E54044F3C56A9FC26E56DC32472696391FD86613446FC45250D0E1D47A8CF540B351B6BBBA7085BCAADF2CCFD73DC51B5210C2CC972DFA7576A3E0D38D67120EB8A40D2E96335458E663307953FAA3C74148E8E26BAC396E7FA86
	9B8A39BDD11F9C2EBF23CD21349540374017E62B856872899B2EEE680CB0EE327309EC43EDD40619FF7E1FD08633553A925C1F54EB2A57E8D1FD2A5789BF7C1CD6DA0272F87C76EBF24CFB946F03F8EF06G73CB7C56920CC945B0F4F7EF40D04F280661768E04683B28ACFF205797CA95C2D6F5894ED19B02F8177DBFA941D869533CDE15AE1A9C42697BE82C2A9EF59014EE5682DE03619BCA515F2F2843BCD7B415E281F4CD7184B06F30FAA1D5D903F3505D03608A183AC8068F63784AC53B49C63100433AE7
	81BC1F7209189C5AA6E232627A83E4E177F6EB24BDE576C3AD566A2B3C0E399A0AC926531E32483A6F27B903637DCC57D39FE09EEDD12F397BD42CD91A1CCEDD757BD96942E3E1199D79B9C1CE90A07E2A0805F9E5C897790687C1336671B013CADBBF3091FD5D2FDE8D281026B1311D1076CB5ED8B40AF60E5B1251D3B722ADBD261C68545116BFEE90F5E1B2683390A07DAA3367A61D5903759BB9A87A25C2FBB1C0069E295F21C322DF05BF4DD68F5065E77D66784D7D329AC53F8500DF8610497BD59B2C9F33
	EBEEB7569411EA4601003BAAE038B1C3AB86B83FAFE0D0EB33BA71E91A6B11C70CA16A417F6CC17B4F0776D3G99067015FFDEAB6A4104AB09A47DFB6FC62DF9C90069D69188790A63B6D18F3E7FAE4AF4824D8808357D50EE5E131A7CFE2D4FCD37C33EAE4F5B4D3E44F07FEADF8D4FFE376EB75AA05803E28DDF36233E38DFF2B42F9FDE2475834EB58E64AE84283D06783BCBCACC3A2F8A30FD4F8134AC4A2F6DA0568DFD0A2E9AD574AF5B06F7DED62EA941AA7F90A406AA1AF52A1A11FA84FF6E626B727B8F
	50E62A5BF029FA87241FD6376AF1F6B71B38340F4766402EF34C768F96B7F27D167E753D247EC631B5D2AD9E37FE33F5649A33F95235106DC77E4616754A25B7F3199D9EBAA86C30927A6C9C92F6D8ED09E4071EA1E10702C31331831369BAA4E49A40AFC54ADC95A6F3459013D99094B21FBC348773B08D70AA76B82D6A7D1ABE28BA2960AAE54860D21BA8190B6576DF342B087908CABD1FEB3C6B1C70094AC6211783E4F68605DE9DB911EC91AF751A55B8F91F387F7D43703CAC5D612462EB577A28375F14E7
	083D7579B1670924E02866199F9CDB7F70BADB4139AF8D32BAFB034447D37CAA931E596A3FE9DE40F645F01AADF5D854064F1660BED3FF9D6C8760830883C886D8FCDD447760BB9F1389EA6FB648D73AD7711C895BD4F9CCEFFB976D8E7BA0EF6FF897EDC977C1A26C1051B05A8EAB2E074F1DFC995E66E748B1635E9F552E98B50E2D63761D181BE94EB52DD4DD5DF13932AC47AEA94963326A3F9439742BD6F3AEDD30FE0FF5CD14DCEAF05989DF066239D21E2DEFA9C39D1681EDA5C05E881CF7G0E81B88162
	C6047DFFDD5CB0117D29DBF5E84D00BB867BDF7368DAC55A6B61031333730F470C9BED67641170F14CFECD96E13F05A322E6759E1A1C8EA3E346D50DEA2F9A093456874F3D97F92D1BDDEA5F1543DA106B3A1B5EF1619A97FEA5F45D53B8B9DD9B470C9BED2FE3DFC53257749CF1775383F4F6267BAF3A4F4F06F60A44AD68C65CD3504E103ACED19C70AC78BA4DD689F08C4085908B10843088A07DEB51773B34EF768D70399B2CA6EA3941EA9E678DD653E03D427A3E5993934137436672757867B4D784FA9247
	4572E7BAFF221E85237AD7CA3D5FD7A652E52273CF24334BDA7EDC431F9B78F3BD99FBBEC41FE2F119BE4E1965B7D14EAC2FC6B9337C161ED9G8FBC4CB798027E6684E71A2C52C8E71A4DD47FA5E56BA9EF3FDF7553C40145A4277D46A8A5914EB6D85E73399713AD8773F91FA30656DCA3FD7C0992330F475E88DF3359235AA9B7441E13E7477DF9A1341783E4829444004F4608FC779725936EB7C702636CB7534B222F4B5DE51157E5F6997A00DF8B68FD6AE85B5FDFE61734D0E6EDB354C1B493BF1B507106
	228F28E3566E91BA1E51220D1FDF96EDFD599A3C983686FB649A34DA4CEB509E93EE731ED1EDA3C654C08F9DB2737BD8722B351A7945C17FB9G291C4F221831F14C78399B4D7CEAA53F781C716EEA1F0B4AAF6F921E15BD4A3096B088693C85F35E16CB62CC7C8A0C4B86DCA5C063AD66335423C398437D3AB28866BB63D37BC86D2DB876D75DE2ECA2741F87308CA0932064D6E113CF833886475B15DEF0B40F2EAA4E6133FE55A9F53A5DD7A869211BFBC67BDDBB9DAB2B87F04C3098FA692335F26CD1D58B23
	E517050F9DCDBF559D524BD4EFCA7A2C9E2A17584DDA5CFEBC7CB25A5C8B25E13C434733FA798945DCAF0F5C4A6A6544DBC5BDFD34B192FD1E249FA411680BA55DE509C4DFFE2B584BDE26B556CAE8135B84AE762C585FCAA46E259B746C896D60EDA2D7945A23477AC6FB64D85FEC0F1C033759474F419F5A2345A8536FA22DF5E600EEF3244ED3BA45BCD2A5EEAD6DC7E2E1EE3182F702569EBB213DDF62CAAFA02E9D5ABD92D7C1EB81835A3192B76D221091AFF1BF489138F44031B34051CB621DD57594288D
	2608F7D6B5BDE135B01E4A6A6A0E6AEE67E1E5201DC6A02BCD6E3A144747A7CE91778D0FD31E4FC1FBA5C05ED4D1A3877FA0EA64146F5F8BF969C4447B06740EBDD647D4FEB7B5B5D4A3A7C30D4CE43E5EADE446GFD7ED4446D68B17B937A79CC8B2B85997DFC989D57EC9BB153270751D15F472DE67A63E1F43451E6BE1E4D6D975B455C5E7AC5B2717C705368F7A937733B947EBCFDEE9B2938634DDCE6775F765BC9443A14C6BA1BFFCE2F28C93B008743FD5DA8EA6A84098B9001DBAAF1774B5ABBBBCE6438
	C79B71CE32DF739AA3F158E7DDA97629075F47GB2A96E3E33AD9166E236C71669399046AC835846F199707B14A5F4A7DDCC5E3801BA74E91E402057CF185C8DD4DF2C876A27895D12C938EED3297E6DC06F1CA6F41B53B911EEB9A65DE6CC8B57ADF15AE85DAAACE3F5BBE0913A25C85DFA2D613A85E93CAEG7AE2592712466B20D70522455D477A655358DC8230CA76BBF091F9991018726E40F762F337202B3079A5DE9CEFFED95C97447C1619EC0FF3EA87FE8778EF466FA7B99359AB1773C2683CA31C1F87
	5A0F90733B081317A6774E760F139C17F0536358BD48DD5D181702F730FB900B444F2678D8931E65461F495C988F344CBBC42DBA5D01B15AB9BD4DE6G4CGC882D88410BEDD6CC1BF35D713896A558E4D578E1B1447342D30793D5DBD39F759BF4D95B9E85E5FBD88F96BF5CB34BB084C69AC77144CE04F0C0EED944F6FF34BF1FEADC06B8108851883108A30F80618DF7EBB15ECFEC71CEE2349B014BE973D499513BB67D607C2E8AC3A783B29DE735C16F379AB79B37B4EB41B7D4EC8E7675B424E4E153CCFA3
	FFF65CB936E6E4632E9373B8976FB7421F71D147A5055550C93C5FFC7EDC9AE19C0B5B19BB8991F9E4686CF6F8FA55A1073C58EC05E431335C2FF8615C253B7B4BAB9D55150E1A5ADDF28D93074C5F216CDF3DAF627B9B04F42EC3A6FFAE38FD9B59C265921AC7593F15074662042EBB70938DAE775F8A046FECFA78BE52726C6B96916F2365D6FE075B70BB213DB96372DB39EC4EBB89EC1B24E60F1071CCE5489E414E1B31C72439B01EEDD5A11E1FDB42653DDC9D2265DFA80FAC4F69D6BC5A00C9986B6F3808
	4F9D4E9D5CA6CF7E513C960577EE350A7CB05AA67338AD961AEC42746944E359B8F379920FD95C676E698E7D7ECB536FA517A6FADF7D7F81A66345EFD5864B99890E68F579FD0E48F579830E48F5798F9C635FFDBC62087EBE77C78E737BDC1673CE54E2FD17896BD7F8971CD1G0E81F45DA572DD4BD674A57C22E4EC2EBB629C40BBG7C0A449072F714C71FEBD579445F1E6CAB47F7E13D1AE1E80348DF609B4AF13D8C4DC7118CF7389CED243B87DC866B4A71474A515FBCEABFC533B340D6DABB86E1DEF1F7
	09F3C18E2D2767C0BBC562AE4B3B447992779F079017971FE65B94AFF077B60AF3DA3544CDBB093896E8F7C85CEBA7853F9E091BC94459A8D562123EC05CA2E8AF1338FCF9DECA173847E8BDD6BD934E7AB3255C73086B0036D362DEBBAF467AE60A7D5B75896296C2FB79CC313E4F6C7C901E67144BFC2746EDCD53698A8372342E79D45D98E6D87A06197BCD52DE2C255E82BE763B53EC75G9DGAE0098008400E40085G4BGDE81A033600C873093E0BFC0BBC04FAC21076703DD6368418FE05459F4450D6E
	4675D80D29CAFC4BD033BD9A8F705663108BCF75DED67B0C8EE5C00EB9D39B5DDFAF54CE6C2FFD3574FDAE78D947300F5FDF39EA713EC66B6F772BB2AE86EB8BA52E4DFFDA733B715A01655F7D3D617574E754D78D30CD4CACD1A7EF6A8E2F13831F623CD9DDFBCB77F8F52D28D7C5DDBB3ACECE1D456E13C59DDDCC2C0D583E66763B713B812653BB0D626C39G7AB35C4FA96EA9E8AF17FAA61E8C57F3F6DDC84F1C4FA62A6733465133A0A14DA65E95B379BF21F1E487FCC502107F589761729FBB90126F9877
	3CE4BE47C5125F81B20A09793C74CBCB783AE550586C01FE9E29CF6279F0FDDE28B7596342C47A0C37EEF3C046967E1B7B58A7B8CE6E8DACDCB94D7884733C5B8BEE27F85C576925579968689D60F72EFB2347415F6F1DB88E966F0D1C371F58BBB6EF7FEAEF643C7DAFFB4DF91B61DE580BEBB2G715FA97B3DA4F167A5AE0372C45C97EE276112785728DE08BF2622C0014F24FA8DD1DC2778B65204BB4FF1EF6F45D806A93BB4DD3EDF58C7575D6D7170EFA3775928EDA13425BD8A76210EEE1FE252FB034421
	5E4C9FB662ABF46ECF2FDF426F40A354EDCDE0EC8F14EDB570FF4FAEB9362EB9B4363A91F977AB9E3FAA69452DA17AD7A73FE9CD48466EBDCAA41F7263A1BE333F38B9BEC7CE0478241CB469F37E6678BC79990EE55F5ABFD378A3D82814D7C90EC156719BDE873B04766BE4ED9843FFF86013FFDF7A42B9FF96313511F7B0D64F28BA8D8155092FFE7837262DA45F27413EF28670EDA45BCF9FB9FEE358233613127E4F71BBAB3F21BA1BC4F0798DD2CA3FEB580D5FA46DBE2678DD866AF1201DE4FB712BD8D244
	489E45BB301BC55A01A0FE8BEBE96A41295098A720EFB1559739D4C9AE450F2D5F3E75780A79D9445ACAD6FBB445592A7441DE56C6728D172E7ADD1A4799749DEA240C7206B0EC70481B856C5E13534749D661E537D47ACAF06E0C3599EBDB6168905449EE5A93D3D3156C61ECF6A9BA08D275B6833F7255748AC93B65FD638F7FF4F4E31715D878A42F122FC9D650E790DB3FB268768C9BA4DF9EE97CC84C858CE48D2F9FBFF481659C231493A4A923EA56126AFB7E6A0D8BD4C6E828C136B0EB42505D832AB638
	3B293F5F6DD5A583B0AD305FAC59E31F2AD1FD0A02B3CB0FBF7DBF3D608AADA4EB303A1A7E2D21FF77605F9A0A2921181ABDE05F15E45CFF2369C1B1E76ABF204FFA2A8FFAC0D5ABAC22717C2BCB9E7CF57D4AD7E0F62DA407FE74816BD0986A75D9E030D75527265C33F9CDC27AF4DCA69FFACB90792E89CA0EB843B61AEE522FFDDC17755277322CA43B0D58D87A857F459C8A7ECBD3EC9B490274CBDDCF9C29F15B30D1E5ADE39C9231B9E3BD8B51796675C5C4816D3D0E7E5DF0535642D145F4EC291C64A6E6
	C3BB4F93C2591CC9A9EBEB2139645B51F4DBC85353041DF07F977513D80B8932A0D16D642F519C789EED17C18AD846F2606B2DBAC897C7D5EDC0D7FCAEF71F62914842130A4FA15ED1B95ADA6ACCF6E033AE10B2475AA1BB5C8E42BBEDEC07E37341B1FFDA83B572766E1001045FE17A1E1C5F591C5F62D2FE9A52B40F835E90657E79E95A9852B41BEB0A7A657D79E9FAB4BC10D1B6FE4018A36E5DF65DB05FB12D1AE20FFA5FE71F4B6E16AA67066E1858596FAD7A2E5485785839629C59DE729109FE86BCB328
	F8BC0662395264F562F970703021066ECEAC25485FAF7AF0A2236515225E17CDE37E8FD0CB878814565A46C994GGD4B8GGD0CB818294G94G88G88GB2F954AC14565A46C994GGD4B8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0394GGGG
**end of data**/
}
}
