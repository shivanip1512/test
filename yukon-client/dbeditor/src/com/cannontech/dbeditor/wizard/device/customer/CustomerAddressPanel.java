package com.cannontech.dbeditor.wizard.device.customer;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

public class CustomerAddressPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JComboBox ivjJComboBoxState = null;
	private javax.swing.JLabel ivjJLabelCity = null;
	private javax.swing.JLabel ivjJLabelState = null;
	private javax.swing.JLabel ivjJLabelZip = null;
	private javax.swing.JTextField ivjJTextFieldCity = null;
	private javax.swing.JTextField ivjJTextFieldSecLocation = null;
	private javax.swing.JTextField ivjJTextFieldZip = null;
	private javax.swing.JTextField ivjJTextFieldPrimeLocation = null;
	private javax.swing.JLabel ivjJLabelAddress = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerAddressPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldPrimeLocation()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldSecLocation()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldCity()) 
		connEtoC3(e);
	if (e.getSource() == getJTextFieldZip()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldCompanyName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldPhoneNumber.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC4:  (JTextField2.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * Return the JComboBoxPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxState() {
	if (ivjJComboBoxState == null) {
		try {
			ivjJComboBoxState = new javax.swing.JComboBox();
			ivjJComboBoxState.setName("JComboBoxState");
			// user code begin {1}

			for( int i = 0; i < com.cannontech.common.util.CtiUtilities.getStateAbbreviations().length; i++ )
				ivjJComboBoxState.addItem( com.cannontech.common.util.CtiUtilities.getStateAbbreviations()[i] );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxState;
}
/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAddress() {
	if (ivjJLabelAddress == null) {
		try {
			ivjJLabelAddress = new javax.swing.JLabel();
			ivjJLabelAddress.setName("JLabelAddress");
			ivjJLabelAddress.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAddress.setText("Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAddress;
}
/**
 * Return the JLabelNormalStateAndThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCity() {
	if (ivjJLabelCity == null) {
		try {
			ivjJLabelCity = new javax.swing.JLabel();
			ivjJLabelCity.setName("JLabelCity");
			ivjJLabelCity.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCity.setText("City:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCity;
}
/**
 * Return the JLabelPrimeContact property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelState() {
	if (ivjJLabelState == null) {
		try {
			ivjJLabelState = new javax.swing.JLabel();
			ivjJLabelState.setName("JLabelState");
			ivjJLabelState.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelState.setText("State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelState;
}
/**
 * Return the JLabelPDA property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelZip() {
	if (ivjJLabelZip == null) {
		try {
			ivjJLabelZip = new javax.swing.JLabel();
			ivjJLabelZip.setName("JLabelZip");
			ivjJLabelZip.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelZip.setText("Zip:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelZip;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCity() {
	if (ivjJTextFieldCity == null) {
		try {
			ivjJTextFieldCity = new javax.swing.JTextField();
			ivjJTextFieldCity.setName("JTextFieldCity");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCity;
}
/**
 * Return the JTextFieldThreshold property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPrimeLocation() {
	if (ivjJTextFieldPrimeLocation == null) {
		try {
			ivjJTextFieldPrimeLocation = new javax.swing.JTextField();
			ivjJTextFieldPrimeLocation.setName("JTextFieldPrimeLocation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPrimeLocation;
}
/**
 * Return the JTextFieldPhoneNumber property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSecLocation() {
	if (ivjJTextFieldSecLocation == null) {
		try {
			ivjJTextFieldSecLocation = new javax.swing.JTextField();
			ivjJTextFieldSecLocation.setName("JTextFieldSecLocation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSecLocation;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldZip() {
	if (ivjJTextFieldZip == null) {
		try {
			ivjJTextFieldZip = new javax.swing.JTextField();
			ivjJTextFieldZip.setName("JTextFieldZip");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldZip;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return null;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.customer.CICustomerBase customer = (com.cannontech.database.data.customer.CICustomerBase)o;

	if( getJTextFieldPrimeLocation().getText() != null && getJTextFieldPrimeLocation().getText().length() > 0 )
		customer.getCustomerAddress().setLocationAddress1( getJTextFieldPrimeLocation().getText() );

	if( getJTextFieldSecLocation().getText() != null && getJTextFieldSecLocation().getText().length() > 0 )
		customer.getCustomerAddress().setLocationAddress2( getJTextFieldSecLocation().getText() );

	if( getJTextFieldCity().getText() != null && getJTextFieldCity().getText().length() > 0 )
		customer.getCustomerAddress().setCityName( getJTextFieldCity().getText() );

	if( getJTextFieldZip().getText() != null && getJTextFieldZip().getText().length() > 0 )
		customer.getCustomerAddress().setZipCode( getJTextFieldZip().getText() );

	customer.getCustomerAddress().setStateCode( getJComboBoxState().getSelectedItem().toString() );
	
	return o;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldPrimeLocation().addCaretListener(this);
	getJTextFieldSecLocation().addCaretListener(this);
	getJTextFieldCity().addCaretListener(this);
	getJTextFieldZip().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerAddressPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJLabelZip = new java.awt.GridBagConstraints();
		constraintsJLabelZip.gridx = 0; constraintsJLabelZip.gridy = 5;
		constraintsJLabelZip.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelZip.ipadx = 12;
		constraintsJLabelZip.ipady = -2;
		constraintsJLabelZip.insets = new java.awt.Insets(9, 12, 151, 11);
		add(getJLabelZip(), constraintsJLabelZip);

		java.awt.GridBagConstraints constraintsJTextFieldZip = new java.awt.GridBagConstraints();
		constraintsJTextFieldZip.gridx = 1; constraintsJTextFieldZip.gridy = 5;
		constraintsJTextFieldZip.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldZip.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldZip.weightx = 1.0;
		constraintsJTextFieldZip.ipadx = 138;
		constraintsJTextFieldZip.insets = new java.awt.Insets(7, 2, 150, 214);
		add(getJTextFieldZip(), constraintsJTextFieldZip);

		java.awt.GridBagConstraints constraintsJLabelState = new java.awt.GridBagConstraints();
		constraintsJLabelState.gridx = 0; constraintsJLabelState.gridy = 4;
		constraintsJLabelState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelState.ipadx = 8;
		constraintsJLabelState.ipady = -2;
		constraintsJLabelState.insets = new java.awt.Insets(7, 12, 9, 1);
		add(getJLabelState(), constraintsJLabelState);

		java.awt.GridBagConstraints constraintsJComboBoxState = new java.awt.GridBagConstraints();
		constraintsJComboBoxState.gridx = 1; constraintsJComboBoxState.gridy = 4;
		constraintsJComboBoxState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxState.weightx = 1.0;
		constraintsJComboBoxState.ipadx = -36;
		constraintsJComboBoxState.insets = new java.awt.Insets(4, 2, 6, 266);
		add(getJComboBoxState(), constraintsJComboBoxState);

		java.awt.GridBagConstraints constraintsJTextFieldCity = new java.awt.GridBagConstraints();
		constraintsJTextFieldCity.gridx = 1; constraintsJTextFieldCity.gridy = 3;
		constraintsJTextFieldCity.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldCity.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldCity.weightx = 1.0;
		constraintsJTextFieldCity.ipadx = 237;
		constraintsJTextFieldCity.insets = new java.awt.Insets(8, 2, 4, 115);
		add(getJTextFieldCity(), constraintsJTextFieldCity);

		java.awt.GridBagConstraints constraintsJTextFieldSecLocation = new java.awt.GridBagConstraints();
		constraintsJTextFieldSecLocation.gridx = 0; constraintsJTextFieldSecLocation.gridy = 2;
		constraintsJTextFieldSecLocation.gridwidth = 2;
		constraintsJTextFieldSecLocation.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldSecLocation.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldSecLocation.weightx = 1.0;
		constraintsJTextFieldSecLocation.ipadx = 394;
		constraintsJTextFieldSecLocation.insets = new java.awt.Insets(1, 10, 8, 8);
		add(getJTextFieldSecLocation(), constraintsJTextFieldSecLocation);

		java.awt.GridBagConstraints constraintsJTextFieldPrimeLocation = new java.awt.GridBagConstraints();
		constraintsJTextFieldPrimeLocation.gridx = 0; constraintsJTextFieldPrimeLocation.gridy = 1;
		constraintsJTextFieldPrimeLocation.gridwidth = 2;
		constraintsJTextFieldPrimeLocation.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldPrimeLocation.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldPrimeLocation.weightx = 1.0;
		constraintsJTextFieldPrimeLocation.ipadx = 394;
		constraintsJTextFieldPrimeLocation.insets = new java.awt.Insets(1, 10, 3, 8);
		add(getJTextFieldPrimeLocation(), constraintsJTextFieldPrimeLocation);

		java.awt.GridBagConstraints constraintsJLabelAddress = new java.awt.GridBagConstraints();
		constraintsJLabelAddress.gridx = 0; constraintsJLabelAddress.gridy = 0;
		constraintsJLabelAddress.gridwidth = 2;
		constraintsJLabelAddress.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAddress.ipadx = 32;
		constraintsJLabelAddress.ipady = -2;
		constraintsJLabelAddress.insets = new java.awt.Insets(16, 9, 0, 265);
		add(getJLabelAddress(), constraintsJLabelAddress);

		java.awt.GridBagConstraints constraintsJLabelCity = new java.awt.GridBagConstraints();
		constraintsJLabelCity.gridx = 0; constraintsJLabelCity.gridy = 3;
		constraintsJLabelCity.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCity.ipadx = 12;
		constraintsJLabelCity.ipady = -2;
		constraintsJLabelCity.insets = new java.awt.Insets(10, 12, 5, 6);
		add(getJLabelCity(), constraintsJLabelCity);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{

	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CICustomerBasePanel aCICustomerBasePanel;
		aCICustomerBasePanel = new CICustomerBasePanel();
		frame.setContentPane(aCICustomerBasePanel);
		frame.setSize(aCICustomerBasePanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;
	
	com.cannontech.database.data.customer.CICustomerBase customer = (com.cannontech.database.data.customer.CICustomerBase)o;

	getJTextFieldPrimeLocation().setText( customer.getCustomerAddress().getLocationAddress1() );

	getJTextFieldSecLocation().setText( customer.getCustomerAddress().getLocationAddress2() );

	getJTextFieldCity().setText( customer.getCustomerAddress().getCityName() );	

	getJTextFieldZip().setText( customer.getCustomerAddress().getZipCode() );	

	getJComboBoxState().setSelectedItem( customer.getCustomerAddress().getStateCode() );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA7F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8BD4D55715D8FFA43191D3533092BA2389F51C9526B19D1BB1B34C54D2E6C69B3BE2D6CD1AB42E16CE1DA9DDE39B27C99B1B18FAF88F1E20FC1EBCFE711390FF5106A288AAC1E3C490C1DFD3D6C4CAB22E448B6F828FAE6FBE6F3BCF9E0AB0FB1F4FFD1747FD901DF42D3256665E3377591F334F5E676CF36EA3C5EFAE484A3095911211C5785F718CC2149AC24A2A6F7B11404548A98EE27AFB8BE0
	BB6975E542B38EE8C571A98EA7693D148D6DC4E8375C1362A805F797197DC9FC853CC45163875A6632055F1CDC4F708D21E78BED5F1B1B8B4FFC0045004367CBB26A7F3B5CB20EDF457182B2EC042475E01F95393BB82E8E5AEDG89GCBFAD87F95704C211AE7565771FE1767ADA0AFFD67EDDB9E4AB14994F03017713A79331CBC1271D2AF6A7A9FEAA7724C00F6B2G8E5ECE26DFB88D4F13ED833D273A55414AEE4967D3FD3A5C5DD7B9643DAEE91ECA0FFC4D5BADD7F687833ABAA8EB75750D7C2D41635164
	C020CD72490A3F8FF53C1B17CDE542F8203D0962FEDD0BFA17427BAA00F5867E1F0FA27E34B745518960770A313E7776C9BE164E9B0F10255F77598B220CB5564B4638449B9E6B7C60ABDBDE27737278887AFFBD501C7DA90EFDGADGFDG31GBBE9BCAC3F71BBF8B6F4690D6A60206AEB707B4FA89E6D8F2AC7D6603D3E9EA89C77215C2DEA9EC2581C3F1CDBE1E14FE440783272EE150F79E4F10D187F248F971135FFFF41364542A7EB97D7599674B3DFAC6B8F7B0469BE95D577337D6363F33C6EBF91336E
	44D92ABD4BC27752278F5956F3B9E4A02C3BEAG673A1F4EF5B93CE77078E0F81D62F5931E456AA69AB3581E81347481919B1D794296F7667D44D96D3674C3FCCA034D2DB09BBCCA24AD5547D0F61C42F46698361C2278A4931E5972EDEAA336D7G2DE2B0454162ABE594F3B78E5AC9G29GEB81B6811479201FCF44D84E48614F98E3ED12261F89FA950F2C91461BB2D282CFB924EBD22BAF20CB3EEE39C3523CD217A287886F7344887A20EB9C5FEA9A7B5D20631C6C13B5C9772ABE10AE8D4A3A2C85D81F67
	C636E39FCD2654F66F208C8A867DC4487D71088B1E0A9450AF79BD12AE07FBB07A4FC650A757248E108882601D796505B174179B466F81508DBFF8EE223C8B32862BC5FD3DDBD587027EF69C89C975211FAF5135A3965E9778040F7F1BA0AEDBCDF1142BC24E83B78BCDF21A3C5DE83E248D0B9C739F457539D695B1730BCE91B3CDF9DFA595CF1F370C9904F615450A22C6664EF5AA936DE2ED5B51CE4ED32BDA674E7F87180E1D068E7B67ACBF5F182B0FECB8D773E18CCBGD69BBEFE66E6B90B59B3D2C096D1
	7AC78D82CC7604BD4E6CBCFE4C497D09F6E6F17B992D095252C12E531F622883386297BEDCC947974977797A70BF288E337C1C4FD18773552AD15C1F64EB32CFEF127D324F83AF81CE7B5EA86A63795B239ABC4FD13C9F723D9192ACE060FFB40AB9A9693A666D8A6A72C5D9573D3E5EB07D27D4D7A058658FAF95C257AFC6F10C6A90643B517F45D14C15EE556713692499AC1C7EAB1A2B32A28F02510DEA5027B37C2BE3986F8372B0DFABB60FE1A1F4CDD202386E303AE109348D4720F987A194F069A26B7D18
	47CADE1949C73100437AE7B9BC5778C5CE8E6D95B9396F7AC2327C537DB62BBDA57E2BCD365A2BAC3FDA2E06F312593401D85B7EEB3AE6F0FC40CEF15EFAA87608D55D4272609EBB4A6A839DA3G732F0ABC50B210AE569A1EG0D2A22B0F79AFE7E5406F19E102F86E5D822F1D13BE0782E47D698C16B60FEE4F41F8D7D28185643539A7AB1F4CC5406C9E0CFAA403A2B4C17659A73856BF7623868B7875A09DA34FEAF987DD2213DB6EABF6F8951EF93340B82937B31795F72A95693D6F5E3EF50DB99543D38BA
	CBBA843EBF281BEA44E768BA0F39D89B903520F7BB7ADF01768C00040008132772C4ADB8FFE081C9FBE8C7543AF7E9005976ECA09CA76553C5AD38B693F51669A90E033A185B1F594C7BD1C3A020F6FB69D64867A55EEE0EA3067B0AFDABBCFB3CDAC0EF05C48F09B9FC480E7662DE49513CF6D8C8EB8A7457C9502B83246B083FB7D8E432BDC9E7FB5E9AF832F84DBE0EB5C3372449FAA57D4F3660037D52B5A9D4999802C521122EB8150DC8BD0B2F07783C249EAAE4B64A54731C73D00728679EF41D66FAD7FD
	6DFEF2F24CE5332A2FE38BF65B2A024C171D4130CFC73E6E302F7F6455ADECFC1E63E2FC0950E7EDD00CAF3946EAFCC557D29C0C2F2CCE709D845C15EB026F681895DF3C41F74F8941178438F586DF5C08955F2EA13157BCCFEB5F2A2170DE5DE4FA6F9C1A3807331AB02B104665902B89DD863E146293CCF8E61FCA63985B2B00569792F54C008357444CE148A100BA00B6GBFC05C300847F7B26118BBF91D588A792579A46562388DC054EB1998BB30E67376FFE6221F681ACD44FE78B747A24FGC94363473E
	A2224D783EB1012FB2223DBE020F45627E033B449AE18A5D7A7A736D30A7B6FBE545435604D5C1F1EE4C3CAEF27FE633B977138B0E50F552AA77CFDEE73AAFDF8F47E905F18EAC4EC69B9200368CE0B540FAG678D2851815AEE887F2BD96FCD65FF1AE76DEAA360DAE03DEE0C5CD78DFF759F3FBDBF07A670C57A59FFE3BC9F735F0BB542FF31B7C44C3FD1F7FBB6D4CC600B34E139258D4A183061D94306789337E7432289FC11B6B8C72CEC787791E143C12033F345AF69FEF3855AA18377D1BE62E6C0BB5E40
	9DF3A2AE995AEBC6047D5B8A7741F3A3341DB7A1DEG1AG2EG0CGACGC885C83BA97A6FE67DEF32FD33EC94F2FD146D3538AF639E5E896D5028689F5C9E476DC7F911F1CD47E9015F48471E4AED30623302B45E57BC8E2BFE4C368443363CA87232ACF0622C02F5EC41E7348B41E5520D793FADCABF162B3AA3FA4DFD43E1DDF3CFF3D8575C339D5B999EE418CF6002BE57B1F9CDFE1F432AA65FCDFD313224004A8E8464GDDBC30A062341F6A454422B67FG8E695C0FAE7243E3B93C4FD9DDED2CB6666629
	60E477C871239367152D0589B938CFA681FD85C09A40C60032B131867EFE4B14FB505950A4FB501B59516765C036753CD4E4E3FC85542056ADC77A3EA63B4C2005D75BD6DD9EC4B731BEA7A93FAEE93D720439EB247C0C960D5F1D9DEDFE599CACBCEE1E03A3469CEC2C19EC8E6A46A64E8113F7294EAC2F4610F7F5ECB2F96E28721EBCE116D7EB48DBB5B2193CABD1656564E36E692A2A34FB7D38CF93FD8C6F41E37008437B787371BC28C843EAD007B3DC8C2E1F33734539EF9974DF8D3091A0B70655E22C9E
	F8F488E577E852A0387822DF6EA6E49A727E41A9F895689F03BCG4B8152E2C4CDBC500C63BC6F163AE465E32F5F30E5F88C67BFDCD118E975D55B8C1E0BBA54419C7FDE8D6ABA8FD15E251ED1C30C44E94527724749EB746A430216F93EF89C6D225C6DD63BA5FE96E4FDBAB2CB4775E9039332EC74E2FD4EB9728C3B78E95460EF285BEA5018EED663F6941AEB5C16E94C2F4AB4D183A77A2D68B1867DF20319BE1F53138D3ADAE3453F5A209F2D35222F1BA6764F742058BFB7CE936BC9E2CE747560619C6B75
	60519C6BF57A9FF2A6DF273F1993AD0F19FD8535423E16AF881BE34784AE5E407DE4C460523FA0EA51A77F577C7D45F97FDBB62CC32DEA513A69AC2F1A26C7FEFFF9A4DFFCFF8981ED49F4119355D7465548155210DEDFFFCE73FA4EC83DEE1AE12C3639D4104D7953270BF3737482FA2FBE836A8C003699425E5081D1BBA7AD3A1F38E3AED81E1B535A0F587AE6B0BB674C885B3B98EAE726B36094BAD7G1D4C24E7DA27B98EB0E69F37317A0C513FB70E0EF3027BDD181E355DCCC7FB9B6CE67A71F1F474515B
	1C1F0D6D097DE2EC1B1ED8CC14EF3C9B75CE20E5A63F4F654F360E3DE45F1D2FEEE1FEFF31270CD81ECDE8663171FF31D05CD5A4028C06FB5EAFF0EBE70AB96CD5F6DA4B92AB8C2FCF3F54A06A5322D9C25E229AA14FED6014DA011B81388D339037B958E4B1C68166FBD4FCA6834FEA003D9C378E5E0B6D2C9F13F720F6AAF9D9A6F9F533474BF34F0E147757E3134933737158B9F4C648D3207DFC06D95EABA3D3495BE612970A10B7876D6372D86DE347EB913ED624BA774333C36A67954ED90FD7D7B5D1621C
	07A97CE3D7BF948B6DD2AF917B292B60763E793DF51BFC9F5FB29F3B1BF8192E897133595DC40601A7CE44A71A70ECDD3A23C02CCBCB00D694AB6EAC763AB0BFC2501E8F1084108A308E005CA156775C6DD5E40AFA31DD753BE183D0CCCBB6BF6BBAEF6FDB683736087CDF7A0F8F12E533FE13916DDE20688E1677357C195E3E176259789EAB4271F90096831088108C309AA05D98DF7B768AB6BE8C1086DD173A7B68ED2098DC07D79E8A233120E1E3EBF1194736096BF75E491E7B6E1CD8FBB13EB43F192F0E77
	F373671528FC3FBDB319AF7528FC8F0F2BE1E3F93F847EDC92156FEA2D19AF1577FB16BF57DB703DC95899AF1B083F8D6FCF4491DEEBAE434F1EFC3EFF1CAB66FB57438F10266F7FA6E3EF1479A6F771F57E2E70DE165E3FE90B15EE26E78F060025ED0C7E616B1353CFE1B948FD72DAC1785D618C3F4FAD905F717E9C40F4AC7E336A6067B4D774DA2E46E5DD4B3D6B322E655EF7CDFE26BE670A7E1D6B831779BB974B657C922C99E2E15E9381D6G2C8558F0174863352EBD046631C58E1F75746259923F2E6B
	C6ADD994FD2C7FD6B475B779A70B30066FD2F5DD9DC479827FF4914E172E7AA91261FED004BE523C3DFDBA6B4A71E9C598EF0A5CC3512CB67D5405385CB9A90EDDF3C43D7A759D082B0276E983D73AD35430EE837732DF70A698388C3F60DDE2603A7C02B7594095B78ADC6E9785AE0556AD17212D98385F16A0AE865AF1862E3BD470A6983858DA413B5E407DD52960A5F78B5CE603B777EE31AFA925D897DC06F688A081E0A9408A00B40017G326702FFGEA815CGD7GF400D800F800A40065F3C5EC3CD6F2
	08C42985G498BF19AA01A6445906173D3D292BDC6F617CC9DA387282FE4185B76E1BF3F0BF827844F55EACFCFC0B6E2312A44E960DA83EDEA400B87C9F6D63C66426F34BD2ACFF746213F665078D883E3CB1BAB4E4DABF7E09F9D0CE1EBD68BDD53D80DD539F32A9A50DCE32DB65514C8EB0FE3F3725A664D1B197E722B742E955E95G26EFE0C7D85F30FFAAFDB91368CBG195B39BEA62F22F12A9A51ACEFF15C789AB1995A0F03ACD1BB3B48F3B5637D79820D7195703EA6CE783338F43CBF9F5A999E5F7552
	2946A76AEA2B71E54ECBF1B40C736723B4CF1C005FB5CF787329DDE1FDA5E5D369B3575C117A5C73427347E2AD26B4FACC4FAE1DBA2663CA2D573DAF17CEDC779EA835DE7796161A57BD06DBD40ABE6B05DC6CB47ABDEC60BEB2F0CB28CC447D496B517B8C7CE354AE44374828D0601329DDC3945749F15FB261BE92EBFEA966A58C39CF550C5FDD51B86871AA8ADF73E99F2F8F525470479AEA2357AF196CFE4E40215DECCE9D38BD7198DC34839BBEF928DCDC87D4B6C3C09E5C6371CA0A5A3B27223C2A223C3A
	7610280F968C196FA66A369E377CAD85C267BCFE9E664F6459FB397E9B98927C7D7D03E1DBFE25FED6DB98EF97AE251354CBB3FF9756333EA22C6717A537262720B2ACE720AAAC671E52DB13F3B7DE29739A6244917AFBE1704D72F9E2EF3830679D92FDEFE853D43F2C69438CCBFF554167787491742FD15FB01228ED0E9451B801E5DCD23C57693560C568686BE53FD1FEEC60DFE08213FECE32A0CC839AA9B00652CAE3AB6DDACD3D7DA78F59EDA43315B8D82E3512CCCC18D612C573291558A157C2629EF4AB
	1F0B0C40D5B2CAA3A35B920B71EC8340BAAC5B40FE19E31F4155C4A7199FFB7D75BAF150BBD3F849667717701A05571575E0F25EB86F7F6B42237F356A079D81BBF1341257F1F13DA8EBF46C32873F07709F90B6139C3F8A9B6CC54043F88274119550079559CD0AFABE449F5084F45953A03C9A50C9319BB59D469F1B9CEE1182FDBA2EC6A75CA45307BFF5A4050C2CC83E5E434C45A728D3B29A2E50302407863057C56DC5A915069457BF956CFF6B7C3AE5F6E2EBA6DB95D572B4CB5DB04D2DA4C76F03817729
	0AA764FF7FB49514BD041E24FBA5085BE98C9F87DB09B741065183212F4C49CB4FEB860F06B4F21876443D24526861E96C13B4D0A5EB2DBA7EF4D355769934E9EF68873EFB2E74320DE470C162AC65067CB0A1BD5220D7990617A69A6BF47E1CBAD82BC93EC00F2A8D02E56591168912212072CBAB2B6E7D5BD7BE268A422CBAF9133992D88F774A6A6061061E9E2FCFB68400DFC17CEEC3BC76290C68D3981AD7FC7E694DDD9087CD44BED8D5C57FD7537FC770FFB545D4D3CC7591F06E86B269DFA4BDA446CC03
	47C89D1C7E4AE618C17D87BFDF796073A7B72C0751B513AC7A53881884E73857874141AED91B15F46F6EED73536E42B972E32804D0EED148C8097BGF2790467D06C5689D105587C09610168C2E31230D5CC3B16527FBBEEF94848350B723EE1D83AAFECE98E37141682865641D2BA832D4E7C4B333FA2ECBFE1E34840F1B87F72AC2D89DB1A45BD0D3E5DAA7666C3E3663DF9634C3268FBF3BC5F1B6343F797ECDF7AC445A1AD86FCEA3C58173E667BBD09FEFFF9F1D0D294DDD2869AFC9E3C4BBCB32C4B6133F5
	370A7283228FA7B2DA3F0AF6771BF87E8FD0CB8788D6F1BC072792GG14B3GGD0CB818294G94G88G88GA7F954ACD6F1BC072792GG14B3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6192GGGG
**end of data**/
}
}
