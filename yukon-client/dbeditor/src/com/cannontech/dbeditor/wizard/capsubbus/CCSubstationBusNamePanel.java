package com.cannontech.dbeditor.wizard.capsubbus;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.roles.application.TDCRole;
 
public class CCSubstationBusNamePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelGeoName = null;
	private javax.swing.JLabel ivjJLabelSubName = null;
	private javax.swing.JPanel ivjJPanelNames = null;
	private javax.swing.JTextField ivjJTextFieldGeoName = null;
	private javax.swing.JTextField ivjJTextFieldSubName = null;
	private javax.swing.JLabel ivjJLabelMapLocation = null;
	private javax.swing.JLabel ivjJLabelAlreadyUsed = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisable = null;
	private javax.swing.JTextField ivjJTextFieldMapLocation = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCSubstationBusNamePanel() {
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
 * connEtoC3:  (JCheckBoxDisable.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusNamePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldMapLocation.caret.caretUpdate(javax.swing.event.CaretEvent) --> CCSubstationBusNamePanel.fireInputUpdate()V)
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
			ivjJCheckBoxDisable.setText("Disable Sub");
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
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSubName() {
	if (ivjJLabelSubName == null) {
		try {
			ivjJLabelSubName = new javax.swing.JLabel();
			ivjJLabelSubName.setName("JLabelSubName");
			ivjJLabelSubName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSubName.setText("Substation Bus Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSubName;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelNames() {
	if (ivjJPanelNames == null) {
		try {
			ivjJPanelNames = new javax.swing.JPanel();
			ivjJPanelNames.setName("JPanelNames");
			ivjJPanelNames.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelSubName = new java.awt.GridBagConstraints();
			constraintsJLabelSubName.gridx = 2; constraintsJLabelSubName.gridy = 2;
			constraintsJLabelSubName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelSubName.ipadx = 7;
			constraintsJLabelSubName.insets = new java.awt.Insets(40, 12, 8, 2);
			getJPanelNames().add(getJLabelSubName(), constraintsJLabelSubName);

			java.awt.GridBagConstraints constraintsJTextFieldSubName = new java.awt.GridBagConstraints();
			constraintsJTextFieldSubName.gridx = 3; constraintsJTextFieldSubName.gridy = 2;
			constraintsJTextFieldSubName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSubName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldSubName.weightx = 1.0;
			constraintsJTextFieldSubName.ipadx = 172;
			constraintsJTextFieldSubName.insets = new java.awt.Insets(39, 2, 5, 12);
			getJPanelNames().add(getJTextFieldSubName(), constraintsJTextFieldSubName);

			java.awt.GridBagConstraints constraintsJLabelGeoName = new java.awt.GridBagConstraints();
			constraintsJLabelGeoName.gridx = 2; constraintsJLabelGeoName.gridy = 3;
			constraintsJLabelGeoName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGeoName.ipadx = 17;
			constraintsJLabelGeoName.insets = new java.awt.Insets(8, 12, 6, 2);
			getJPanelNames().add(getJLabelGeoName(), constraintsJLabelGeoName);

			java.awt.GridBagConstraints constraintsJTextFieldGeoName = new java.awt.GridBagConstraints();
			constraintsJTextFieldGeoName.gridx = 3; constraintsJTextFieldGeoName.gridy = 3;
			constraintsJTextFieldGeoName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldGeoName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldGeoName.weightx = 1.0;
			constraintsJTextFieldGeoName.ipadx = 172;
			constraintsJTextFieldGeoName.insets = new java.awt.Insets(5, 2, 5, 12);
			getJPanelNames().add(getJTextFieldGeoName(), constraintsJTextFieldGeoName);

			java.awt.GridBagConstraints constraintsJLabelMapLocation = new java.awt.GridBagConstraints();
			constraintsJLabelMapLocation.gridx = 2; constraintsJLabelMapLocation.gridy = 4;
			constraintsJLabelMapLocation.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelMapLocation.ipadx = 13;
			constraintsJLabelMapLocation.insets = new java.awt.Insets(8, 12, 4, 29);
			getJPanelNames().add(getJLabelMapLocation(), constraintsJLabelMapLocation);

			java.awt.GridBagConstraints constraintsJLabelAlreadyUsed = new java.awt.GridBagConstraints();
			constraintsJLabelAlreadyUsed.gridx = 2; constraintsJLabelAlreadyUsed.gridy = 2;
			constraintsJLabelAlreadyUsed.gridwidth = -1;
constraintsJLabelAlreadyUsed.gridheight = -1;
			constraintsJLabelAlreadyUsed.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAlreadyUsed.ipadx = -83;
			constraintsJLabelAlreadyUsed.ipady = -14;
			getJPanelNames().add(getJLabelAlreadyUsed(), constraintsJLabelAlreadyUsed);

			java.awt.GridBagConstraints constraintsJCheckBoxDisable = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDisable.gridx = 2; constraintsJCheckBoxDisable.gridy = 5;
			constraintsJCheckBoxDisable.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDisable.ipadx = 45;
			constraintsJCheckBoxDisable.insets = new java.awt.Insets(2, 12, 40, 2);
			getJPanelNames().add(getJCheckBoxDisable(), constraintsJCheckBoxDisable);

			java.awt.GridBagConstraints constraintsJTextFieldMapLocation = new java.awt.GridBagConstraints();
			constraintsJTextFieldMapLocation.gridx = 3; constraintsJTextFieldMapLocation.gridy = 4;
			constraintsJTextFieldMapLocation.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldMapLocation.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldMapLocation.weightx = 1.0;
			constraintsJTextFieldMapLocation.ipadx = 114;
			constraintsJTextFieldMapLocation.ipady = 3;
			constraintsJTextFieldMapLocation.insets = new java.awt.Insets(6, 2, 2, 70);
			getJPanelNames().add(getJTextFieldMapLocation(), constraintsJTextFieldMapLocation);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelNames;
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
			
			ivjJTextFieldSubName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_CAP_SUBBUS_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );

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
	CapControlSubBus ccSubBus = 
		(CapControlSubBus)com.cannontech.database.data.capcontrol.CCYukonPAOFactory.createCapControlPAO( 
						com.cannontech.database.data.pao.CapControlTypes.CAP_CONTROL_SUBBUS );

	ccSubBus.setName( getJTextFieldSubName().getText() );
	ccSubBus.setGeoAreaName( getJTextFieldGeoName().getText() );
	ccSubBus.getCapControlSubstationBus().setDaysOfWeek( new String("NYYYYYNN") );


	ccSubBus.setDisableFlag( 
		getJCheckBoxDisable().isSelected() 
		? new Character('Y')
		: new Character('N') );

	if( getJTextFieldMapLocation().getText() == null || getJTextFieldMapLocation().getText().length() <= 0 )
		ccSubBus.getCapControlSubstationBus().setMapLocationID( new Integer(0) );
	else
		ccSubBus.getCapControlSubstationBus().setMapLocationID(
				new Integer( getJTextFieldMapLocation().getText() ) );
	
	return ccSubBus;
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
		setLayout(new java.awt.GridLayout());
		setSize(350, 200);
		add(getJPanelNames(), getJPanelNames().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}


	boolean amfmInterface = false;
	try
	{	
		amfmInterface = ClientSession.getInstance().getRolePropertyValue(
			TDCRole.CAP_CONTROL_INTERFACE, "NotFound").trim().equalsIgnoreCase( "AMFM" );
	}
	catch( java.util.MissingResourceException e )
	{}
	
	getJLabelMapLocation().setVisible( amfmInterface );
	getJTextFieldMapLocation().setVisible( amfmInterface );

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
		setErrorString("The Substation Bus Name text field must be filled in");
		return false;
	}

	if( getJTextFieldGeoName().getText() == null
		 || getJTextFieldGeoName().getText().length() < 1 )
	{
		setErrorString("The Geographical Name text field must be filled in");
		return false;
	}
	

	//if we are using MapLocation IDs, we must validate them!
	if( getJTextFieldMapLocation().isVisible() && getJLabelAlreadyUsed().isVisible() )
	{
		setErrorString("The MapLocationID selected is already used, try another");
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
		int[] mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs();
		boolean show = false;

		
		long id = Long.parseLong(getJTextFieldMapLocation().getText());
		for( int i = 0; i < mapIDs.length; i++ )
			if( mapIDs[i] == id )
			{
				show = true;
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
		CCSubstationBusNamePanel aCCSubstationBusNamePanel;
		aCCSubstationBusNamePanel = new CCSubstationBusNamePanel();
		frame.setContentPane(aCCSubstationBusNamePanel);
		frame.setSize(aCCSubstationBusNamePanel.getSize());
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB5F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF494D516A09F9198848D0800E722A013B9E6155941DD5CE10FCE86E763CEBC32F3D0D90775B0AB0E6C9177B0BB380B1A337352C9BAC1C072836270B118A009E278BB98D18809A141B8E4A5A60D202152DDC9F728F4B5D555C9A704E46FFD1F2A6ACEF7A2B9FA4E64F0F9756EFB6F5E7B6E3B6F3E7BEED513023762B3539C850424E5927E7767B4C2D2BEA16429BBFE91A7F02364A4A7317D6D8148
	A5FB0299D0CE05B6E3EE12B307D455E4C3FD9E5493E7A7B93743F3BE292DC9BD868FB178AC0336AFDEC8099F1ACFD9336013C56B7785B7C17986C0826030DCA7A37FDF848BB9BE05638524B988D95906FD1E8D6E643863D0EFG188330200D75CF06F28365BC2E62B86FF7E9DABCD9774F7B9C1B110E0D260052F6B6361E17C5640134F56D48AB2BEB939FB3966A0B81F07269646AC9D0AFB951F0257D93375AD96E167CFE55EF48EEEFF9372FD752BC00896841565620DED1D1D5F5BE582A9B1261D37D27037ADF
	24CE39C1724B8AA181AF72B85715CDE942FC201ECAF18BF2116FC2F8CE81D8E5623B7390FF4A1764F48184FCE22E9F6FBB4167623A76GD97834BFFDCB0C39CE7031B9AE70D9F30D8B3E1A75A6DD971F8B753F865AF2BA121CA5G35GDE0089GBB28BD841A5F0532325528D2BBBBD5FFE5A0F0DA71E81F2B9ED901670A8AE86138AFE4372AF988E1EB3E39EBFF94F90682B6EE478D0FE3BAC9B8A256BF710B79E445CFBFF3E4C551490A04030E788E260BC59D16CE986F46183C17F7045BE7B86FE6E267BDEF3C
	1A1E99057742A74B9CEBB89DF24562FD708A2EF589DD6BA2F8CE637641706594EF58704CD67F0E5A8C5647C25B6AAB42B6DCEF88D96AB3E6111CC375D175B0B32952D12FB099BCCA24ACAFD21ED39546F303A94BFA0ACF346119AC57A99E6B4950363FB349496C2B3799776ED428A782AC81D881108AD06807FEFEE1E38FB59F7816B656A0E9466920CF71489AE1E3B557B6A8651021C9F5FE58F7FE375CA4E9BE29D511F5427BDCF3218EDA43460D22366F869EE7E43F2C517D8A54E13B9A322633BE3738F2310F
	A65356C6DF278C8CBA83C4501D664A07D211F4634B00C7B2E42B87EB3F5305BA69121A00A29000E726179F39D0DF75B0FF8F00E16AE1E793527BCC5640DFD4D4542B6A15E02091E7C21678D14FD7286F18G4F71FE2163399790172DA6B90BD4C167FCD31E0DCE354F0D62CBDA0F58E36F6621FFBE2A8A1B79CF1730196A4D77127DCF1E0DEAB3545F284CD682EA645EB9CAE922DC2C3EB857B54C3EBD2AC65FD35F5ED7BCB9AC0F749152FF43DC2B5C0B38D6B3C15605G4BCC9DCFE9AAE2B6FBDA52E5E12567B4
	B0B059E3E91C49797B3C9C2ECF14A7134B4F5A7E17362D813A8501A46789GCFC068F0CC1EAB4A38114EA73A8E67D09EF67A9B3EC39ECCD7E31B71FC123BE43FD1AD87E43F879EF45EB6299979717D5B261AE326D1FCG76FB95ECB05D445F510CFBD2B28C4D579AB4647332E1787C6DD67B8FA9AFB80D83162B903C66B7639C55EE586FE67F7B1AF12F38D53FDFF633B31B8F616DFFC3772A2C481DA0F4159A749B8C7FD3975A7B9539077B0A3FF7E1A054A5A9C174BBACEED8A0ED44B9E83ECEB085F4DDE4EDG
	7771CDDD05B69DCD869C46BFCB20DC9E90FB323BD86C49125E3B49A3ADFB9D514E141977D6BB0EDF65714CD5EBCFB21992AEC4177DFE6AB3B8DEE7321EFB977DE8355C65F34B9834D4FABC1A2C6BBCF6E1FB616CA52427G1FD1GB32F0A3D706ED758AE7C8D5F84D52A22B0151A3A5EFE91EDDD172F86E5F0536858BEB375377BE2DEC4DB9357A5EBFF67A26AD23179C4C5C3DD9E4E9771618210A799E075D5264F920D690375FB596C37886ACB8156EA517AADAA907D766BC94EEAG0F4E7A4D536D7D7A360AFE
	CBG3F82A01577BBEE30FECC2FD9AD98D3C40B9D5B033E722061C38FAD99E07C0120C1354D6244B55457A30D890608878BFBD17F0FC0FDB940DAC3584A3FEC9671E05C15F832F26E1FE2463E59C1A65B7E20E5ABC5E3C4BC9877B572D4202DAFA856766E0B76B329D257D5370F9E07FCDD9AAE59ED09613E3C74BA14EDBECDB76AE03307449ADE3C047262F94951BCFE38183F094FF5AA70CD86385C05787856821B6C84F025G75G6914DED2BE468DEEC9130DF27ABFBB06CBBB24AEA9D42EF703E3A827DE273C
	8ADBBF4547B23EAE7F73AE6ACC7699DED9EB8477D3631395CFCB150DCA7DD0E4BEAAEBE23A3FA77F9417AF657ED96444C03EA3DAACBEE14BAEC7C297537952AECB77FDFF6BCCDF734DEBD9CC8F3B8B049EF6F6A7B99B3B059E8E1E0C2607495DC28F638AC62287467337A64FF820375C6419DBE2671993E2BC0F07844F5A2D07518F538DDE4E0A86D9EBD335CE59C39917CB5D8667DAC91BB9DB2E7FA5E5F94CC6E4EA79DC6232A6E19377EE95F255834F31A1A157FB9551F4315814EB5336115B448FF71F063243
	B4074F25C03D6A2656FEC316A14E565F8F3ACFAC8DD9B14F2A5060780745591369BEDF9F0A3C8B4C20786C1E483B406EDEF197A804363E9E919B963760B933926A29G853D90F3835483A83DE2FFBFF7698899A6762E83FF2579A565FC5821AAE25C2F11AF1C033CFE575728CBFA8E922187A9BFD28F93FA4367BEAF224E462903466D0E28A7C70CE36B383794FDB3753936152A28B8EB2EACBB079F6F351F43ABFBD9DC1DF18D6EA7G4C8FBF5D1A4D65B9F8CD785B6BB5F6FF3BF84BE14746A8FB9E41FB0D49B7
	611A3567771B776F47AF231C4950368AA0957976C18C886082686B93EB346F1B0F07DBA3EAFA0DEA9560EA610C2C0A0CE7CC1D16940CECAD8E8F9A973996F37A4247B17D351F947ADB58A7625A97C6A8434B034615C7545774C53307CF1AF6C43707AA2F6C3EF2DA8DF1D9EBCBD0D6DC630C6BE66EEA6B48E4FDE85038C8FD1D389ECDDF2D95A2BF54886D6C5E175D0D3849D00FB7F14F079037846AABCCD9EF6B451814740B7A9CDAC77F48573EF7387B523781249F8D4FF8FC7C1D7008F5EFE37BE1FFCB6C7B44
	11166877098FDB225FA7CE3664B2BC5030E797C47B1916216F9B67DA225DB7F651B9BC36ED8B252D6B32CEB7A886FA3C6D85E3AB09F26F40501B4FBF1F4C4C5F407BFCEA28D507CCFB7EC3D9E1F4FB36450F1957C47CD858AF6247072E63B951GF5AF40A853CED6345AEDF5E6447AA6C6549F699767CBA30D4F16C1FDADC076C012338860540070DB4F378CFB36FC9A9A62ECF97AF26CF5FE76F274F5DEF599E3DFDD8DEAEEB9F2ADDF3CDCE836D99E324E10BBD16D2C4FBABA5E1034F6F910AD3CC6473336D863
	B324D876425674E201FDCD8F1B7BC6B9E9DF034B8361BAB7A26A9386C43C33FB2B1D5E91135E67A5F6FAF3207F8200A51C4E720141FB18517B77B0FAC7CDFA6D95C365E557442477D6B75E0B9529C78D9AD00EC2BB7C20DB1C3B846A05G27G9AC73173175978CF3A71CCED5324CED05F79006CA63523F1ECEAC80CDD887D1381D683E4C3DB69E8219369B20EE53B81D7C667F3F87DDA649C260E36641EB7BAD6BCD6570F7BE651E896F7651A782FA8FE158D4F62310769BE43FA4ECDC94EB9B7891F7D879ACB14
	0E01B8FA0C407D52A57CF83D09CB580638B9D0DFB0C66C25CB2D3177023BB57ADE7035C677F9EA6B50BE2F3BB5168DB3794650B3FD8948364C14795681B10F55A6EEDA93620E0F05390D95B88FBDE3AFC3BDE0626EC9C35CD8284FB4F1AFD2DCA254971B3886076011EC627AAB842EF0DC1213697F1BEE719E478B3878F142261EE8C13FE57ACD3C21D4D41C51FC1EFAEE1E7C8C1ECF4FDB9C3FFC1C701D4BA84D1C71E0E7G27460BD8B0740E088593674FA27523BE0BFA77DE59F858619A4F6C2BEF3C958BA6C0
	AC48F83E5DA3F8AE0276356369FCFB6CF79D346DE9E9AC66E16DB70535631A9593FB7B4230F614779F9D76765F0535230EF67171ECEE3F5AAB66167A2B8422BC78E14C3C42711B591CAE713221291814CCFCAD0B2D519EFA9645C1DB424DE25D2A2F0BF5CB39D91CC3677A0BD0B628EF1AGF79500DAGAF4084008400A593C45F4B346F6A89AC6713FD8B4B6914C2D9FB8B4B5BE0CE0675FD747A2888F9575E9C3E57115FD071487EA86DDE0E2B656592DEA6F0F92347C72B874992996BE48EA1072093095BE8DB
	E3E18BD8224F589C25BF73130E810C8F5BD43F11F08B2E49CEB72E09971E8D002209087BB7D775B032E4F2FA88EBE09C01F1459CDFG4F97522C9CF6BEF9F89B0EF12BCA30532F934677A96A6FF0EFFAA78A9F5E39BDDC36C46AF3C250BEF62210ED7E40F032E559E4DBB4B1DC3614C132A5B48D166D61A6A15BF2D336EBA56132CD23BEEDB5342FB37BBCC47DD727DF06404767E67D16D25CE3GA513C4BF0783E999603C0FCE42F7697F6A03AAE173EBF78CB53FCC3E6EE2FE44B6BF1CD38868DD644F0C5E740A
	61743541C6EF6C24F0FD4D047A73447E6E4253BD32F73C5A88475DF7436358B9BDBF043EFB5EA4F6CE27197887A8FE018D4F4E0FE74C73E3B13495CD96716E628E0CBD46C2BD9EE09140D20035G1BFEA04E694D6E03E4181837D18D5443C12E580EDEB63FFDBDA3FB777D73AC6127977E43BDF0FE3C1296AB9FD174836617CE71F2F5E3B14533791DD3F0FE81E81B8C30GA099E08540BAF3FEDF3A773379FD6A71991506A1393DB473ABA65764133BADB486EE4C4E0FDF9B9C871E3BA6E2396CA920E700DA00CB
	D3F84EE48A4D8DDF97FD0CA9B1E356F474F1E76B25D699AF92E1F7287F2EC7DFE8E5FEA25B1FFB5F9AFBC6D6435AB236050F0DEC7F27433966D8DB2E50EC5F24DA6D150AA6CB1E1EAFF559E3365F6F4677A1E696225A27630B8DAED74756B05E6174D9AEF4E63F7D7CDEBA0565C257CD913952746D515A33EF956DEB1A22356FB45BAB0FC5EBBF886D4CD7CD4410114F65A157DEA87DF2F711197C2D8187D97A36AE79617E2B795A5E5EDFF4F0FF512123E5265D46F75B3F59B97E7A1F233E63C2283E152D3D0B17
	0BEFAEA6EFD13EE0BBC47C2DBDB998C78813358843BB4E77371B44FE59F95FECD27D74ABE942E7C6721D4A792D3C550A59D6F724E631B93F348D42A9F268306071CC17250F1B86EEC49F516642E81E8ED9B41FEA8A6777EC2F55B6A36D6D687CBCBEC9D15BED4CD87F9752305C675947F57244C57BDA644C5A6390BEB1D2A705D319AECA26DABAE172547411A167623B0EB5666B92C35673033667155D5FC57EE868F364156F158773130FF6443E2F25F4C43F2F7D3AA37AFDEDF94750B928159D315FF93F50E1FF
	674D7C7C0754EF38E07D8C0019G09G0FCC95BEBEF885ED893F3A996C5FBF7534E36E843F34B184FF1792FB2EAD4A705F67F895FCDF582A9A065A0974855E2F60FA99EA00A299CED7D0C71A2F5DEB302E9C5F2350F717F29BC533F37C7DBEF10E2F003995CC937740C317435E8FD8774053D2BB3B8A32BBD930CF5C496A2709FB60DCCAF3AC5467GAC1CA65E930C3EAE72EDA953445DB079EB7B7D29683D0C28772775DF9FF0E45C4676D269ED561EDA86FE06677B5AE2647B68494278BF51AF726C17EE93B2D5
	EC9738B893D75CACF0A9A6AEF44C4CFB5FAEE21D5974BEFF826A2E5B05DDBC213D87E5135441239A8C81D40DDA86A09BB4B5A0EBC68F435237775C5E1EA698F70600CE9C40A20025G2BGD281F262121CA5G47819AG3CGFDGD3816681AC84C886D891A7641071B5E2CCB978C2D2A355A49F1AA91763E3F41F5CDE5E3E36B7968D3072332066F32D9D325BE8145A4DB1157D316D3C36FFF8BB7FBF3AC6B258E7E3CF00678B1B7AB1AED25B5AF4595CCF977BF3CCDC1D5E206ABEBC76196FBA52B7325839F204
	63FAEFF89C0B2D37539C585A3848183B0862D363A2E36E9F1BFBB5FBFA12D319AE6E6E632F5B79AF857C2A69C35F5559DA3724631ED98FFD8BE600ED81B4GF8G7AE688BBFA293FF488BBE21E40B2A3A64F32FE3BBCD3E7B0D96671F2510CD8777E135B6D63D2F83F153CDC97F3DCE933FDDC769D2C5FCEDE9E3CA356386B476C632AF9BF97AF95A8C7C50CE5BA099F40E7732CE15B18377DE8G4F8D1F9F4CD1D2FC3DB498BD8F9D7D6DBC3FCEE26F0F7FA2436F0F57C874F3A0ADED70B960CC0BFE8EECCA331F
	838C17CBBFE2EF873F60B27BEDB3F197CC5C8ECA93F14DBE0F61B571FB52DE6778DA99998A7CBEAAD7B74539443BA99B6E82479DCA43BD8ED3762A1A987BC19A4DA178940555ABE89F1F9F363C290F13D4C6DFC03249DDED62AE18EB3D4B96D73DE3FBFE206F7B0F25123FD79EACD7F897BDDB6340DEB1FF49F346941788B84C0972B8A2BD367D2969435BDFA8BD3A7D75278F363FB169516D6F16F43B7D71DCF63F487BA75FA1721F47729037826AEBCC5C2CBC1193DDEF621A360BDCFB6DCC01FBD6C75CA528AB
	A62E4A90E3C3A66EA4BD6B13213E5C440DADC45C9A28271A387FA894E3336F9438B89A9338206EB5F1CBE8CC50877549A62E0C0831B3CD5C0345B49F867554BBC54CA53D3349729DF4FBB17C4E50E68BAF36974F3D95D2FEB93360CEBACB644B6A734273E56B69ABEF165F7AC95EF079B2FB2EB2B2DFD68FBC4437DB0C7F982AFB9760BDA67F1775F07EBB1C967F2F0C6178E78D41BFEE16754CF8B5B5076799953A1E7150AF5114E7F6E138BC1FE4D97210B70713E74390722C18454EC9CB1E7B0E054B73F3EAA3
	2B215FBAD31E5F8C044BA3E5DB721CA25FE6FD7E487FA265A9191D643C88786781982F14E21CEC17CF77617720CC26670AC54CFEF03648D35C04A64667D01677ED6F49EC6C3DA89AFF5E1AEB517935FEE3F4E664D9F43A37DAF4C205B7C6E756B60B0E63CD0B4E2201BD5169D40213D3004CA178F7384C9C3B77CF5658D70BD16FED1222239FE33F83BB5774B3D0EE62CBA453A09BF9E62A88FF5A24114DE19C9E3D7B586FD21EED525309330E3C0946F1DE56E8FCA0FB70FB877E63079A32A120C294F59E70F5A4
	C327C526EE74A8F2BDA9E87B82BF7C558D59D3A9A28F5DA0DB6907F68770A3598335126EB5D0080F6AC9069FFF26C172D833A2795B8F30B06423907EB8A32D72B24E0D9A9581F973293CC8255C24127F33ADFB771CDD35A81DB8EA486B0AAAF9EAA4B784FFF5E4036155E45D2BAA1ED06064A9CA08FDFFC3F36DC0EE07B9FD1CECB9E668CC212F046E1A3CF975669AB826C29AB9C0FB62DEA8B7FBF82A3C12862CE42D4E401F1D28DA0959B6FA37714E63E736DFF210B4BE492B241F24078286F136C91DBE2547A0
	9B4C7C110E0DD90001A34D2F631717A0DCD104F0224964D1FE7BE387277F78552FA88FEB28C15EE25A0421875AE53573C0E5DB1B4FAF1B84C035C0FE17C99E7B14C774498BCD5BFA7649BF3602A9D413744E0387697F07687F07717FC394F308E28E9D867D2EA5C37EC53607441C297D00BCDB28BCE88165B5300846B3AFBDF64FEFCE2CDD83332BA11974ABC4D8079C2B57DF021D2D32B6BEF17A2E0DF1ABA761B28550DAC2C8F7E3482408B32C2331584A2EA395694F4DCDF7100CBA62E4318158AF9E70E03F74
	7C2FA369909BD05343FD0417CCBC6B76D1E1538699A4E2B387DB9622B7585797911BE86FB7687F39B72CAD9C15CF47EEB5A739136930B0C2584C91896B2C26CE6A7B113458123414CB0952BACC2C03C38D0053A085568FAF0BCE8775A2CC55D70017B8A32B6D1A94707A5C12A210B91FCB01A211CFAF2A2B86549B25BCC5DF04B97A3211694373BD6A6300250FEC1BBECA7F7AA47D401254E913B47B2FCF520FADC9B338243BA2A4E5F25164C48CC9B342F70370D1CEF689E49CF14CF7BB05D33CDEEDCDA17C0F65
	A04B866CF97D75638AE33EE3D9B4176583D266DAB9C816CBA9243FC5491E1764AC19A7F2A75E607BA4F67E6CFC2724A80624DC2974FBB017F63A47102DFC752EAE242F0BBE3C113515F4215C9D36B17F8FD0CB87888678CE638796GG74BEGGD0CB818294G94G88G88GB5F954AC8678CE638796GG74BEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC196GGGG
**end of data**/
}
}
