package com.cannontech.dbeditor.wizard.device.capcontrol;

import java.awt.Dimension;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.roles.application.TDCRole;

/**
 * This type was created in VisualAge.
 */
 
public class CapBankNameAddressPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener 
{
	private Integer originalMapLocID = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjBankAddressLabel = null;
	private javax.swing.JTextField ivjBankAddressTextField = null;
	private javax.swing.JLabel ivjOperationalStateLabel = null;
	private javax.swing.JComboBox ivjOperationStateComboBox = null;
	private javax.swing.JLabel ivjJLabelMapLocation = null;
	private javax.swing.JLabel ivjJLabelAlreadyUsed = null;
	private javax.swing.JTextField ivjJTextFieldMapID = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CapBankNameAddressPanel() {
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
	if (e.getSource() == getOperationStateComboBox()) 
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
	if (e.getSource() == getBankAddressTextField()) 
		connEtoC2(e);
	if (e.getSource() == getNameTextField()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldMapID()) 
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
 * connEtoC2:  (AddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC3:  (OperationStateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankNameAddressPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldMapID.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapBankNameAddressPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextFieldMapID_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the PhysicalAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBankAddressLabel() {
	if (ivjBankAddressLabel == null) {
		try {
			ivjBankAddressLabel = new javax.swing.JLabel();
			ivjBankAddressLabel.setName("BankAddressLabel");
			ivjBankAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBankAddressLabel.setText("Street Location:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBankAddressLabel;
}
/**
 * Return the AddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getBankAddressTextField() {
	if (ivjBankAddressTextField == null) {
		try {
			ivjBankAddressTextField = new javax.swing.JTextField();
			ivjBankAddressTextField.setName("BankAddressTextField");
			ivjBankAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjBankAddressTextField.setColumns(12);
			// user code begin {1}
			ivjBankAddressTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_CAP_BANK_ADDRESS_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBankAddressTextField;
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
 * Return the JTextFieldMapID property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMapID() {
	if (ivjJTextFieldMapID == null) {
		try {
			ivjJTextFieldMapID = new javax.swing.JTextField();
			ivjJTextFieldMapID.setName("JTextFieldMapID");
			// user code begin {1}

			ivjJTextFieldMapID.setDocument( 
					new com.cannontech.common.gui.unchanging.LongRangeDocument() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMapID;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("Cap Bank Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setColumns(12);
			// user code begin {1}
			
			ivjNameTextField.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * Return the OperationalStateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOperationalStateLabel() {
	if (ivjOperationalStateLabel == null) {
		try {
			ivjOperationalStateLabel = new javax.swing.JLabel();
			ivjOperationalStateLabel.setName("OperationalStateLabel");
			ivjOperationalStateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOperationalStateLabel.setText("Bank Operation:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOperationalStateLabel;
}
/**
 * Return the OperationStateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getOperationStateComboBox() {
	if (ivjOperationStateComboBox == null) {
		try {
			ivjOperationStateComboBox = new javax.swing.JComboBox();
			ivjOperationStateComboBox.setName("OperationStateComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOperationStateComboBox;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/2001 5:42:49 PM)
 * @return java.lang.String
 */
public String getSelectedCapBankType() 
{
	return getOperationStateComboBox().getSelectedItem().toString();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.capcontrol.CapBank newCapBank = 
		(com.cannontech.database.data.capcontrol.CapBank)DeviceFactory.createDevice(com.cannontech.database.data.pao.PAOGroups.CAPBANK);
	
	newCapBank.setPAOName( getNameTextField().getText() );
	newCapBank.setLocation( getBankAddressTextField().getText() );

	newCapBank.getCapBank().setOperationalState( getOperationStateComboBox().getSelectedItem().toString() );


	if( getJTextFieldMapID().getText() == null || getJTextFieldMapID().getText().length() <= 0 )
		newCapBank.getCapBank().setMapLocationID( new Integer(0) );
	else	
		newCapBank.getCapBank().setMapLocationID( 
				new Integer(getJTextFieldMapID().getText() ) );
	
	return newCapBank;
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
	getOperationStateComboBox().addActionListener(this);
	getBankAddressTextField().addCaretListener(this);
	getNameTextField().addCaretListener(this);
	getJTextFieldMapID().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CapbankNameAddressPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
		constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
		constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsNameLabel.ipadx = 1;
		constraintsNameLabel.insets = new java.awt.Insets(8, 5, 7, 13);
		add(getNameLabel(), constraintsNameLabel);

		java.awt.GridBagConstraints constraintsBankAddressLabel = new java.awt.GridBagConstraints();
		constraintsBankAddressLabel.gridx = 1; constraintsBankAddressLabel.gridy = 2;
		constraintsBankAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsBankAddressLabel.ipadx = 1;
		constraintsBankAddressLabel.insets = new java.awt.Insets(7, 5, 7, 27);
		add(getBankAddressLabel(), constraintsBankAddressLabel);

		java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
		constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
		constraintsNameTextField.gridwidth = 2;
		constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsNameTextField.weightx = 1.0;
		constraintsNameTextField.ipadx = 205;
		constraintsNameTextField.insets = new java.awt.Insets(6, 5, 5, 11);
		add(getNameTextField(), constraintsNameTextField);

		java.awt.GridBagConstraints constraintsBankAddressTextField = new java.awt.GridBagConstraints();
		constraintsBankAddressTextField.gridx = 2; constraintsBankAddressTextField.gridy = 2;
		constraintsBankAddressTextField.gridwidth = 2;
		constraintsBankAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsBankAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsBankAddressTextField.weightx = 1.0;
		constraintsBankAddressTextField.ipadx = 205;
		constraintsBankAddressTextField.insets = new java.awt.Insets(5, 5, 5, 11);
		add(getBankAddressTextField(), constraintsBankAddressTextField);

		java.awt.GridBagConstraints constraintsOperationStateComboBox = new java.awt.GridBagConstraints();
		constraintsOperationStateComboBox.gridx = 2; constraintsOperationStateComboBox.gridy = 3;
		constraintsOperationStateComboBox.gridwidth = 2;
		constraintsOperationStateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsOperationStateComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsOperationStateComboBox.weightx = 1.0;
		constraintsOperationStateComboBox.ipadx = 83;
		constraintsOperationStateComboBox.insets = new java.awt.Insets(6, 5, 6, 11);
		add(getOperationStateComboBox(), constraintsOperationStateComboBox);

		java.awt.GridBagConstraints constraintsOperationalStateLabel = new java.awt.GridBagConstraints();
		constraintsOperationalStateLabel.gridx = 1; constraintsOperationalStateLabel.gridy = 3;
		constraintsOperationalStateLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsOperationalStateLabel.insets = new java.awt.Insets(8, 5, 8, 5);
		add(getOperationalStateLabel(), constraintsOperationalStateLabel);

		java.awt.GridBagConstraints constraintsJLabelMapLocation = new java.awt.GridBagConstraints();
		constraintsJLabelMapLocation.gridx = 1; constraintsJLabelMapLocation.gridy = 4;
		constraintsJLabelMapLocation.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMapLocation.ipadx = 9;
		constraintsJLabelMapLocation.insets = new java.awt.Insets(8, 5, 71, 5);
		add(getJLabelMapLocation(), constraintsJLabelMapLocation);

		java.awt.GridBagConstraints constraintsJLabelAlreadyUsed = new java.awt.GridBagConstraints();
		constraintsJLabelAlreadyUsed.gridx = 3; constraintsJLabelAlreadyUsed.gridy = 4;
		constraintsJLabelAlreadyUsed.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAlreadyUsed.ipadx = 34;
		constraintsJLabelAlreadyUsed.ipady = -1;
		constraintsJLabelAlreadyUsed.insets = new java.awt.Insets(11, 1, 74, 15);
		add(getJLabelAlreadyUsed(), constraintsJLabelAlreadyUsed);

		java.awt.GridBagConstraints constraintsJTextFieldMapID = new java.awt.GridBagConstraints();
		constraintsJTextFieldMapID.gridx = 2; constraintsJTextFieldMapID.gridy = 4;
		constraintsJTextFieldMapID.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldMapID.weightx = 1.0;
		constraintsJTextFieldMapID.ipadx = 82;
		constraintsJTextFieldMapID.insets = new java.awt.Insets(7, 5, 71, 1);
		add(getJTextFieldMapID(), constraintsJTextFieldMapID);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	if( getOperationStateComboBox().getModel().getSize() > 0 )
		getOperationStateComboBox().removeAllItems();
		
	getOperationStateComboBox().addItem("Switched");
	getOperationStateComboBox().addItem("Fixed");
	getJLabelAlreadyUsed().setVisible(false);


	boolean amfmInterface = false;
	try
	{	
		amfmInterface = ClientSession.getInstance().getRolePropertyValue(
			TDCRole.CAP_CONTROL_INTERFACE, "NotFound").trim().equalsIgnoreCase( "AMFM" );
	}
	catch( java.util.MissingResourceException e )
	{}
	
	getJLabelMapLocation().setVisible( amfmInterface );
	getJTextFieldMapID().setVisible( amfmInterface );
	
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isFixedCapBank() 
{
	if ( getOperationStateComboBox().getSelectedItem() == null )
		return false;
	else
		return ( getOperationStateComboBox().getSelectedItem().toString().equalsIgnoreCase( 
					com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE ) );
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getNameTextField().getText() == null   ||
		getNameTextField().getText().length() < 1 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if( getBankAddressTextField().getText() == null 	||
		getBankAddressTextField().getText().length() < 1 )
	{
		setErrorString("The Bank Address text field must be filled in");
		return false;
	}
	
	//if we are using MapLocation IDs, we must validate them!
	if( getJTextFieldMapID().isVisible() && getJLabelAlreadyUsed().isVisible() )
	{
		setErrorString("The MapLocationID selected is already used, try another");
		return false;
	}


	return true;
}
/**
 * Comment
 */
public void jTextFieldMapID_CaretUpdate(javax.swing.event.CaretEvent caretEvent) 
{
	//if we are using MapLocation IDs, we must validate them!
	if( getJTextFieldMapID().isVisible()
		 && getJTextFieldMapID().getText() != null 
		 && getJTextFieldMapID().getText().length() > 0 )	
	{
		int[] mapIDs = null;
		
		if( originalMapLocID != null )
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs( originalMapLocID.intValue() );
		else
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs();

		StringBuffer buf = new StringBuffer("The MapLocationID selected is already used, try another\nUsed IDs: ");
		long mapID = Long.parseLong( getJTextFieldMapID().getText() );
		boolean show = false;

		for( int i = 0; i < mapIDs.length; i++ )
			if( mapIDs[i] == mapID )
			{
				//setErrorString("The MapLocationID selected is already used, try another");
				for( int j = 0; j < mapIDs.length; j ++ )
				{
					if( (j % 20 == 0) && j != 0 )
						buf.append("\n  ");
						
					buf.append( mapIDs[j] + "," );
				}

				setErrorString( buf.toString() );
				show = true;
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
		CapBankNameAddressPanel aCapBankNameAddressPanel;
		aCapBankNameAddressPanel = new CapBankNameAddressPanel();
		frame.setContentPane(aCapBankNameAddressPanel);
		frame.setSize(aCapBankNameAddressPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
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
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GBAF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF09C4515827C45B648D8D43940CEA9E0F2023808C3F4F5BA223BB3CA28B2C1A4628A4ED462DC1CBB67623B884A3978F266D3C5EBD73B12C0C232A49BE1CC905846D2CE481210031185567F6795DE30AC14E0870F34335A15E6F7D6B3A3E9ED4B763D57BDBDB33B5A1530AB29424553EC3F576F532FDF3FFE5DB31072B7139DB936F2C2F29CC47F77FE8EA161E304ECFB704D9D9C17A0E6B809655F
	DBG3B492D6EDCF8A6822DF2ED063304EC7BA89F5A6B205DF3FB0673D578DDC67E732355C760C79CBDA120E5B83462397558CFF0BDF9346D4AA90467ABGA9004367CE9175FBF2AAF4FC260E6710E3A3E43B977BDC4D592F630EC15B8D308EA04D4B7AE7413300EADE58F4CC6FF7F6E5B25979034336A214E3114921EE14718E684FCA722D1C97C6D1D78A3593F916C37B7BGB8F8BB596C6A04E733FBFC742391B9D0BBA28403F2D093C7FC35537E0B0262297508D37E9191882191A0A832545454AE045A046078
	47C2C0EC75F894D1D55DC2D014C0CE58073AF66464D3590434C0BB570F384AD2541F8E6DA7G328D7CAFA93E87FE1F87887B7918BFBC5C2C0FE9685237C86A66203DB84E18937DEC2CE9FEF34CC9134F653DC667273D8567E1A750AA46B21CB5GEE0090C0A2406BB4AE7ACE7C9E1E2D43DA3B9C88484156D028CD72A87D32C71460F7D393D0F45C2762082CF888E1F3FFFFCECD8CFB66824617FE4DFC4CA7A96F71B8D87F69B748166F1C34654570491614BADB4A98734546B153A7CC777F44553DF5ACB2CEA3F5
	FFC62C3A57AD126D0E983AD31F2C366D5465640F1B3A0F0D63DCA751392E025FB9FAFCB07C57A8FE4602E7B17B8E45E3FBB950F60C735898FA055B624A3D0314543BE27AE1F5C62BEDC0E2B6C8D234AD1F17216C559253D9E058622378D48B1E5972B435855BD9C0EB8CE4B8D9FC395AF08D2F06F6AAC0A640B6005CA064A10046A00F31D5A70EFC4998F38B0A56B66917BC22C2982F2BE5AFBC453026883DC1D59302A36220207805E1C9D4095E67CC8B7AE0B8026FCFB476C7C0C737989495C1734BC1108E4BD6
	9395157599E9590DFD9411D2877C81919484C2044B9DEFA9C37F892AF6A66491B4516C4168EA8B7AE4CA988409A0GFEB33FACE8C7FF8D4078A50099438F4FD0F9A7C58592C6D313CB1647A7C383B8921299C4BF975AB0F7A4426F14A077F1C28762CA648CE7154C659CEAAD354869700F207902F2012F31BBCAB1CFB74BBCE61E9962B153D1F48F29F932AFE64CA01C17D92C04656835330E4AC43BD83B26F468CB2E5FE6B9765A1ABFD7F4FEE99D76EB147F0AB1D74FF462DCA5030D69GDB8C9F1FEF2DE4B15B
	A628A20F52D38A841868B1BD4E6C944ACA6069506D48516DE734A0256D84397BC3994E8EGA944FD789BBABE3E6E9DBA4F350EE3EE9F96168D693F39FD85FFC19D4CD703ED38BF09D3E2D06B90C3E25083BFD41DF62E8D75696B57AB9BBCA3949F02755E8E8BCCB5707EB6DC130226A97E61C9CDBCADEA1ABFB8EA52A728AEF5F2B8E4268A2EAB5C06E31427E13D9B7DAF3761DA016DBBA852C9B3D8F47A4DF42D0A1298G235B6549204670096D986F636285BDD7A435E3C1B4A5C81318F718AFB788AF639894FF
	GC281D397599542F5FC40D6E171D19260308E4A04675690DF135387781ABCF8716BE4639707EC3176145577F458DAA6743AE642DC134C26059D31EDDFC6F3060ED76D947733C22C8321FA990ED53D3035F0389B65E920E789C07284DF8BF91DC86779C6DF846D32A4B1179A3E5E5909312E0A9313A224E9CCECA78D7FBD5FD99AC59B54FD496876AE7425E44909EE85FDF9D7992F9353401EAC009D934C1FF58A73876B67AD677D56C0BB956089A5D63F6ABD3CDF098AB9D50D576F27C63F9E68F39EA0C1E57D52
	D5560F45C05E97D8D3442AA1C7A77D35131A9FB33420C17007A6B56AECD6A72E20399EE564EA3C9E7CFB877A3F995A83G1246E36571A2DE8FA60DA7132DF76F0BDB83AF5018EDA91A99AB15B771FAF02287F5EE875AAE0D4F6D15AE6B1E542A2A72081FEE077A3C3C5BED0DA506EB68FE891EDE3F22EA3D305843FC8E0FF5233D38DF6AE83DFEF8145695B85642C94801GE913087F55D439457619C9366F2D03A70B590672864C85B448EA5943ADAADED989081E2AB1E1CA28952635DA1AF7EADBA93993FFD773
	3979FEF5A933D3245EE71BF8D575A02F6B6EAAEB51F5E75EFF87E93EDAE60BD5E7AFA9FE43C62618BF2B26CC3F4EBC6034E77F7979BCB646DB4B7998B56833FE0A0F716910F50C5B2658988B277998FFDB0E3551082008DAAD7D2BDB09438B572A53107C749136A3D59FA05379699E2E3392648DCDF31D4A39D8BA93014E781CFBB95FBA40EDB1786EEC0A45D79E66368676DE0F2DECAEFA76E38C0F8D40CE506597A5CF3F906A6D78225DA245B51718635503ECBF3B4D96DD43D7054D7A22B9BC3B56E0356DF9A7
	0E599DE635ED1901D7A8BE4102E7BE7AA73A96313D81E82E8B3C9EA3FD185B37C3BB77A26C69G0DG83G5AC53E26B6F63FC76629F7FBA1C7A8C1C1BA9D3111696BE82A877D8DFB0F5E3E5803BE24FB8F617B7A63E551FEC83C98B97675D1ED467733D9FC35D16D2CA8BEB6FF23526BBC4FD9662929298F46B8AC3749E17D7C7B76B4BF7FEE3F48735765AEEB7ECAAFEE303D9CA7FFD5DEE23ADBAE196B2C46B84F5EF792F3F2825056G2482E4816C84A81901F918617E4F6D7DF0BE7F53209A105B815783FBCE
	FBF4FDE0786B76726B73735D3378227DBCB0934B4F072A7647763331F2181F8FCE646B3A2499DECF8E6D39BEDB3D337822EDDD97E5AB33615EF35C06F44306423D57E7433ED9FC3F0BEA6F0CE943BFB771F8AB3C8C7BB3BDC75DCB77568EE88F9938C539081B01F652E5EE2B57F9901EE9504E824886483F82B581008BA084108830618A6F9F267DB3211DFD05558ED8E7E0CDD2F8B543D983603A4AEA85567FE1C7E20C98C7C0DD7CF7C857415BAE5D067CAB33F3D9E294AE6DF27C33D5D69C9A336DFF635AE6BD
	0394931683F99B6B8EDC3B314E903B235AF31D499CF170EC7D967445BFCF147445BECF6C6D0BFD1E585F371B61C10675F601538F764DFD5E28690BF55EF8030E6F113D45D4362A0AAACDA8D8686934DFEAFBC80CF387D65E3AEF4AC8FEF9015E27D3135B5FB366667074DCF7E4525559734A72E3FDBF6E5F89C0DF83108A108910FD1567450C3EF977254E709C7BD26A497873725D13316765211398B32ABC290C08513E5FF4324220199938D7938368A65667F14A2F894A28B8EB6E1E227C0C960F7F67A7634DAF
	1B034C08B9E8B06620FA42BA87058911BE2F0CEAB7A6709A6956BDD6F975063C7B4FD96575C4711F0DEA078CF96DFB2D729A8DF91B1B660A11858933E304495BE843F32CA4DC10A7B5B8FB5C0039F133F1DEDD8DFC9BG1E8248CEE0759F5B173EBD0D6B5E2B8881F05F6910B8C236D25E35391C57856D73G89G49G9BEF60E3384F03E340B9F18943742E1C615F390267C53CCE578F23144CE83FBADD2473989B1F4167ADAA0F64B33AB03AA7976F8CCF0574EB3F539A1417C619427DD03A475A87A29EFBC55A
	6732E36C74D1A4542BAEF9C4504F3B0C7E50E5135EAAA9226039F0C695CDBBBF4843755297D97392D6EBFF5BE92D352BEEE43EF65D48EB71CF26E3514F9B745B9AE351B5035EFFB496FD09C1FFB0A6FD4D0DFC3F7CE81A6F179BEE647963037E786B3F25BF767A6F6A0F1D17DD7DF36765017EF86B165957C26B9FF2937C3E095B7C5FE7384D878D5C6DD30853203D4440BDFC0E732EB1F00EEAE33C862EC6663CE48147BD53447B15AF60672997ED7C7D1287F0EB9670B5D3FFBE22164353EAD3D3376277348923
	AE3A82D98D53ED4F57791FD8406F82A66DA833F0A10C8520F9A12F25436F70DAFA7DB76EA02E0413B16F82368EB45886966AF7398B4DDABA85EAE92673FB39DCE7AA503397A26E573956184098DE1943EAC1C6BF603052F1FE8E90AB7DCC849D6D7D871B153EB8020EBEFAC367E7E3FB74909F5B8B0F269069EFBF08FB4F5138080DE9C8FF3A878F1003B7BF1F477C7E9BEF8509D9C353154A463F5D49EB57C41041F0CB26B9EE1301FB24116348E20EDBF014630E9938DFD39C0DF540EDD9CC7DB15D9143F66B78
	2D7732A940138EF0C047E5416F3FA1667DF099F9BD8F6FA43DF2D073ACC179FE3A868E426F3AA5DCE74160FCBA9DD61DCBA2F526C37B4E9C2B4EF699ED9C112549C0D0A5CC6FBF52B505B547A6E8B35CB3F4CDBD853F3799366C2F1E4F963B65D931B452161A2526ADCC47469057FBECA95761B8BE1F0E82DDBEC20FC5874ABD8B6DFB08F9E73F1BB86C5777DE336CBA79DC574C476EAD3AF3B1BF781632FB0B9C836F26F84D02E7B96A121D67288420EDDB4A6FB3DEBD05EB2567E618C7009900A400D4002C1BF9
	5E7F2F7EBAB2CF5DB8A007DC30B1C816D44E4637A9777A5E77BE1447F3C16A03F71134C54F66443BB358F6B34B81394B58F37B4081CBCDFC1F9B475788B4B70086309CA099A0ED999FDF4347B5ECFC1D9E3F562AE942080F5EF672418D7A45E9930D458F9B5B53CE6B58B2F57DDB75674EE533EBB04637A2223E4FDF4E7A6D571FF54B63717D30514A5721779B521FD2DC3E25C72DFC89FA3FA47D1992176F3791FC69FA3FAC7D398D1E3B22F85FA46C3CC877D77AEF470959B85857D66077A57F7699B1667B6705
	FC3E77FF73CE523179599C3EC623EDF32FE0BA13D7187B5A76319772184C674E22B4163BFEF16690BC0362F425F1D95C8549336AEDD5884219CD71FB5F2E292C2B292CEF2CB66CC81E36FEEFF26C2577E33E17C1F8CA571F2DBF53979F08B9FEE6D7621FCD3B1E1C02ED1154B7F0BF588513362B6ADAEC0E2FEFFC581439B94475456F3F8A0F257A3C77671A3F87AD3FEF30FF59773157837A3B6B3F2A8E365EABCF452FE38F1F0ADD473EFBAAF69D7B5E29396F8F0E1F0A7F3E326D1475FDA54BD7A3DE2C118E5E
	1261EC8170G0481165F42F3D5562937894DD5B172D427E7944F517815044675FF479DFF2C8F3A677F3662FBEEFC57B3ACEB1A9CC0799C7F089B67CB13C39449F0BFF4230F947F28CFE3DDF57C93EE0CB7C974D2B42B23F373F88D1EF28B2F2D1376A1AE9D5B866E59FD3C5EFE4A402D1B663CD537F2DCEFB167EDB6F07795F35E9E83778BDA4F2D06F60A015BDDC1F931ED6026AA8C3D86AE1956F8D509994EE3099C371176F3C17B2C01BB749A6775993822BFA0AE8D5A1B8C5C5BF49DEE01F63601DBCCB86FAE
	83F7021EB9FAD6020E157C9C70234F2DF92A640E37ECFC3F0F0E7950CA161FE6D61A7912451A530735538640E7826CG483FAD43399F2091208760ACC088E089C0B2C0AA40A600ADG59GE4951C03D67158BCBC04B9E9D09853AB845CCEE505C6A7A075038F8DD0C570E3086AB1AF7929DFC1CE07A16B44397F1BC316DB1161642EDDE0D87AA619FF33630D9F6F0AF77EF83F64256FBBA1CE87AE0474BB2484AF5E1948DE2FAA9A6BEA21374440752AEED9756355G2BD36BE9ECFBE0AC21D57C8E641B7BB011EA
	201C655B1F0E61AC329A36647539EAD8FB9424AF0E2CE113F59DE61D7C98DD9F6B811F86407491495477D071FC7A9CF3685BEE51474676C2E3643924142E131D500FA47171D7D5C40E7FC04034E7C545FC76644DE1CFDD12D96BB2DDFFF7B4521E4728BD4D502F4730E7F314BDDDC1531E6657663327E08EFB422023AC421E03FF0834270966028550AF5130678392E9CF2AEC5A73B5B217BDF973583391F460F72EAF42BF26EB3089F9267C2A9F3F33E0B6851A78FDC0E692BF9B8878A91C9E6F2F0E9E0A379EE1
	676903DA65547018B8228D8823864FBEDF7C357816EF7E3578FBDF6C3D274EB7FB6FB96A0B3D779C77D9779E7D2E4A0771BD8A79E8486857E9607EE8607A28CC44FD6677E8BE836F26F6A13EC7C40546F7D0542EE90A9B52F11FDBF0FF54F167FD38A0E148BED9613C220F4608DF12746F20E89FFF90520B610FA035519F92ACF62B864E34FB420FF1E154A4ACBD72EF247C65D4AE24ABC172DF54AFC89528D9D85CB930F45163F649BE2CB5E254229E6015C7AD05A8632D42142E7F3EF54A1473E1713549F9E05A
	1413F05114632838B6B9F7DF3258F3451413FA0DF29E38EA4AB9E4B3656C255F454710538A01AC0118FA782F215A60252F75F55E85CDB8EFDEC1D2B1D659B77A1F777FG02E57D53B0898AA9B6E47652A058BA75DE137D5F6F365BC8EEAFF132C551CBF2B132FB090386FEAF31432288632A4560C185F8E8F6023422888B9F7E7A517F487C57C155CE1C3D64B5CCCC27C50546036841F7D87A872DDD24A0A443C6FD9A7020CA258F072ADD10C497A977FE0A9FF3291A68E9651126EAE48F7D0C6388FE78F424C7D0
	FD9A2E6863AE129B44CFEFC9A9A3CBC2F074888B3B63545E1C5673689B1A508858DBC66DC5A9350614326F969FFA2BEFDB1A1D583A48CB12ACF83A0491C8C23D24C073A9226A13A5CFB8F422058A4A27FBA08E5F8E62DEB7060F032D459BF9436871705A65C55B0B3AE0E2438AB9C2FBE29E2EB5FAF85AFD02822AC425D743778532F250205D70BB6D1D473ADFBDEBA3B97AA0A74895129B8EE944699582FE6902C68A0C331B0A44FC40C05A8B2A78A59398D799E59CA799BAEAD7BDD2F77B3D4F7D096AB0D9B572
	A673A6309E9995654011D62F579F948D8160DA907F06A19E7B54C675A98D2F5C5377640B43908A9D449E282B23FF6B695F867CDBCFB17594D35FG7EB57EE714587F2269E1BEE69ABFC6E0E38454F641A4EABFFE7A113BFE523CA39BC657C59C74AB971807923357471301E1D1D9347E76B7DECE5A3A8C27A9045192C63905CCBCA1663DA64E3903E2ED33828B314E59910268172C730408A25A3B187EAD3D66D1A3D7B9655DEB983BBF3631CE835B2DE369F524C6DEE997C215781A2B925F03B5E92408B67417DE
	50AE26ED63A596E0CA7A01053F282A6C6DG54ABD57A3B29CAFCB9D5FDDD1EF1324C7857F1CB356996FE9F0E777E79DFBDCB6BE6DB0A3BEA5ED74F5277CDCB79F7260E2F26CFBF8A4F8A7FE8CB19DD747EBA0E25395C528EEAA9CFB9CED6F7B10D4873179DC20B5E6EB40750C7586AADE6063133FA75D56BD9FD57420A38F70A1E357ABB3B3551E775C51F60D8D3811F35161F117F457DAE09DF131F8E8812248952F8EB500375F95B85CDB46F2516FD0272D55EC7A7B25AED1F205DE3961E7F87D0CB878807832E
	D29F94GG20B8GGD0CB818294G94G88G88GBAF954AC07832ED29F94GG20B8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD994GGGG
**end of data**/
}
}
