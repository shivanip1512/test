package com.cannontech.dbeditor.wizard.device;

import com.cannontech.database.db.device.*;
import com.cannontech.database.data.*;
import com.cannontech.database.data.device.*;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Dimension;
 import com.cannontech.database.db.*;
 import com.cannontech.database.data.device.*;

 import com.cannontech.common.gui.util.DataInputPanel;
 
public class DeviceNameAddressPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener 
{
	private int deviceType = -1;

	private javax.swing.JTextField ivjAddressTextField = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjPhysicalAddressLabel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceNameAddressPanel() {
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
	if (e.getSource() == getNameTextField()) 
		connEtoC1(e);
	if (e.getSource() == getAddressTextField()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 1:16:35 PM)
 */
private void checkMCTAddresses( int address )
{
	try
	{
		String[] devices = DeviceCarrierSettings.isAddressUnique(address, null);
		if( devices != null )
		{
			String devStr = new String();
			for( int i = 0; i < devices.length; i++ )
				devStr += "          " + devices[i] + "\n";

			int res = javax.swing.JOptionPane.showConfirmDialog(
							this, 
							"The address '" + address + "' is already used by the following devices,\n" + 
							"are you sure you want to use it again?\n" +
							devStr,
							"Address Already Used",
							javax.swing.JOptionPane.YES_NO_OPTION,
							javax.swing.JOptionPane.WARNING_MESSAGE );

			if( res == javax.swing.JOptionPane.NO_OPTION )
			{
				throw new com.cannontech.common.wizard.CancelInsertException("Device was not inserted");
			}
			

		}
		
	}
	catch( java.sql.SQLException sq )
	{
		com.cannontech.clientutils.CTILogger.error( sq.getMessage(), sq );
	}

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
 * connEtoC2:  (AddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
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
 * Comment
 */
public void eitherTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2001 10:04:55 AM)
 * @return java.lang.String
 */
public String getAddress()
{
    return getAddressTextField().getText();
}
/**
 * Return the AddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddressTextField() {
	if (ivjAddressTextField == null) {
		try {
			ivjAddressTextField = new javax.swing.JTextField();
			ivjAddressTextField.setName("AddressTextField");
			ivjAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjAddressTextField.setColumns(6);
			// user code begin {1}

			ivjAddressTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(-9999999999L, 9999999999L) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressTextField;
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 10:02:36 AM)
 * @return int
 */
public int getDeviceType() {
	return deviceType;
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
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = 0;
			constraintsNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsPhysicalAddressLabel = new java.awt.GridBagConstraints();
			constraintsPhysicalAddressLabel.gridx = 0; constraintsPhysicalAddressLabel.gridy = 1;
			constraintsPhysicalAddressLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPhysicalAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 1; constraintsNameTextField.gridy = 0;
			constraintsNameTextField.anchor = java.awt.GridBagConstraints.EAST;
			constraintsNameTextField.insets = new java.awt.Insets(5, 10, 5, 0);
			getJPanel1().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsAddressTextField = new java.awt.GridBagConstraints();
			constraintsAddressTextField.gridx = 1; constraintsAddressTextField.gridy = 1;
			constraintsAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAddressTextField.insets = new java.awt.Insets(5, 10, 5, 0);
			getJPanel1().add(getAddressTextField(), constraintsAddressTextField);
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
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
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
			ivjNameLabel.setText("Device Name:");
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
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
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
 * Return the PhysicalAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPhysicalAddressLabel() {
	if (ivjPhysicalAddressLabel == null) {
		try {
			ivjPhysicalAddressLabel = new javax.swing.JLabel();
			ivjPhysicalAddressLabel.setName("PhysicalAddressLabel");
			ivjPhysicalAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhysicalAddressLabel.setText("Physical Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalAddressLabel;
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
	//The name is easy, the address is more difficult
	//since at this point all devices have a physical
	//address but it is tedious to tell what type
	//of device it is - CommLine, Carrier, Repeater, MCT Broadcast group, etc

	//Cast should be ok
	DeviceBase device = (DeviceBase)val;
	
	String nameString = getNameTextField().getText();
	device.setPAOName( nameString );

	//Search for the correct sub-type and set the address

	Integer address = new Integer( getAddressTextField().getText() );
	
	if( val instanceof IDLCBase )
		((IDLCBase)device).getDeviceIDLCRemote().setAddress( address );
	
	else if( val instanceof CarrierBase )
	{
		if( val instanceof Repeater900 )
		{
			//special case, we must add 4190000 to every address for Repeater900
			((CarrierBase)device).getDeviceCarrierSettings().setAddress( new Integer(address.intValue() + 4190000) );
		}
		
		else if( val instanceof MCT_Broadcast )
		{
			((MCT_Broadcast)device).getDeviceCarrierSettings().setAddress( address );
		}
	
		else
			((CarrierBase)device).getDeviceCarrierSettings().setAddress( address );
	}
	
	else  //didn't find it
		throw new Error("Unable to determine device type when attempting to set the address");

	if( DeviceTypesFuncs.isMCT(getDeviceType()) )
	{
		checkMCTAddresses( address.intValue() );
	}
	
	return val;
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
	getNameTextField().addCaretListener(this);
	getAddressTextField().addCaretListener(this);
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
		add(getJPanel1(), getJPanel1().getName());
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
	if( getNameTextField().getText() == null
		 || getNameTextField().getText().length() < 1 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if( getAddressTextField().getText() == null
		 || getAddressTextField().getText().length() < 1 )
	{
		setErrorString("The Address text field must be filled in");
		return false;
	}

	int address = Integer.parseInt( getAddress() );

	if( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(getDeviceType()) )
	{
		//check the range for the MCT's
		if( address < 0  
			 || address > 2796201 
			 || address == 1398101 )
		{
			setErrorString("Valid range for MCT addresses is 0 to 2796201 and can not be 1398101");
			com.cannontech.clientutils.CTILogger.info("*** Valid range for MCT addresses is 0 to 2796201 and can not be 1398101");
			return false;
		}
			
	}
	else if( PAOGroups.REPEATER == getDeviceType() )
	{
		if( address < 464  
			 || address > 4302 )
		{
			setErrorString("Valid range for Repeater900 addresses is 464 to 4302");
			com.cannontech.clientutils.CTILogger.info("*** Valid range for Repeater900 addresses is 464 to 4302");
			return false;
		}
	}

	
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceNameAddressPanel aDeviceNameAddressPanel;
		aDeviceNameAddressPanel = new DeviceNameAddressPanel();
		frame.getContentPane().add("Center", aDeviceNameAddressPanel);
		frame.setSize(aDeviceNameAddressPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2002 10:02:36 AM)
 * @param newDeviceType int
 */
public void setDeviceType(int newDeviceType) {
	deviceType = newDeviceType;
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
	D0CB838494G88G88GD7F161ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8DD4D4671569D91B122C9BCDC39A5AE3FBECA4D91BE59337EB32F623FBCCEB3B2625273647341E2C3BCB4F5AAC3BB5BD6E4653F076F85A0F81F44461CFE6848B4888C8D089C2E43B86CD0A0084941570A7AA880F19B7B0B8B3EFFC6F0DC0E25B3D777BF97398E6A01A63B9651C4B1B6FFEBF77FE773BFF5FFD0F584B17E725DB6C0424E7917E77FF690494B792729F15C59E01CB68E83192535FE100
	825236A7931EAB21EF4B47AD56FC72F18F8EDA8B6D695BAD56A278DDC85E1FDA75B67C08C1A78E7A32D77F3EECFEBA6DF6C1E78FEDFF5DE603E7AE40FA4061535609743FEBAB6678ED9CAFA05DC248CEBA66E736D20E6B06768440AA008D1DEC7CD6F86E2514BF5BF40A0F9BFAB41E6C7C6E81CB8E2EE3DAD3401973EC6EA8FFBA49D7522D9710D6EE0E0D4FC9003ECDG38798C12F36CDD1C57FDF37CFD17622FF3C901009250E557C45D1477A3C9F55739655BDE175C5454C61F17A43FFC566DD6E5CD6B168232
	8F66AE226B8F64D869FA04F4E13B8BF12943C8F3A334D3G528C7CF3B7905F873FFDG0B3A44BE5FFB6B8C5F476047ABC862576BB373E26CF3D9975B5F062E70BE13DD3519876919BC68C059130BAD56AA00D3GC3GF7G168114D0DD788B7BB1F81E9D53DB953FDF891C8D86DBFCEE75226216FD703B2989FAB82EC7F6A92A1B90F65E761C5AA87C4C87ECDE45DD4FE3B2D9F9D21CFDD24F1748563FED326C09A2132DAB4BAD2BAFB2D9A4DF8C4B04513E9E1376760B33F5F3B66DAB44CCBB7E6F9E31E4C5213D
	7A15E28B39445631DF8A53EE3B04E7BDC44F3A91FE27F37DE0F88F45471970CCCFFFC1F5865B8950376B12500D41DC41CBD766A3A43F61F4D4B9A4FC335EB2FA1971B0FDB912174A91DCBB71B22319E570D2C771EBCCF8464B7314C7ECA7C3DFFDF70B15695712A2345BF8E82F8258881082C0BEEC31168254FFA8F46C4776931FD0473AA5D5EF89F9FDEED9A5EC6E924781F84A533AAAF586B4DD8A386481C975CAE3BED9A3FCCC0283E5B0B6EB5E17296E3B004687F2C0D6A55D2B84E0F5B0DBDDD6B5B666E9C7
	810ED1E55A5B67754BC0409FA4E25DE79D057074C91A5E9FF4CB3A9C9E417ADFF060195C1686E0C5D8GFEB3392CADC6F9F5437EA7G268DB9B49C4075AE48AAB80C2626AEC539998A7661CE48468FD14EAA759D0B61F76207C246EB8EA24E5683B65CA35619B8305F34CE1B57056CCB6A0C3031319B681BCF75881D7945205019361CAF125AD75E0D2AB3542F76B0DD51FBA2ED47C757C43ED8FB67706082F6FB2AA73ACDFDF2DF11F3E3A19A997738FE2EF1D655A5F8D64B0057B5GDB8C992FB860E4BA5BA2E9
	3250524BAAA8186C8ECB1C71F9F4444A6519E1705EC8F1BBE03D62DEA1339769FE52BFA55F734BECB325016BE75D879ACCB6DFAC42F8A45F1683FA1B9C1483EE7821713E65C528F35CDEBD0AB167E90A8F02FD3702C1E986BE2988EDD052F555BB965265DED9573D0171F07FEACACB8B0D85432EC150FA31887728CC01FD9B63579721ED3814C0C02607E4CC617D5F2436A97BE4BFB05D2A0482BA43EFACC67D3EA94FF05F10DC3C1B20ED7BC268E7D80E30CA5A07FBD03DFEB8FAF4D5A431976D76211CE2130C1A
	8107394EB4BC97778A9B1C2A94B6D8755163E47D08BDEA8CC9F86A24E5FBAF339B529736C146533A0351F97F9675919C2FB1DEAF9FC33F99ABD9E13ABFD90A6BB901CEA3C0D71F507D5F7D867B05FF61CA5F2A78FCCC24062CEBCB7271EC64DBA1995CB2BA328B067CCE166C0F689B6032E47D27CBD016BE138FF47653F8B8A2F2C1B770F387E0D99F136796A18FBA6E682898670376A200447E5963189C760CE04C0F16570D073CF5A15D0B9ED452C1D903A11DCA0765F1EFD0DF0CEB24740BFC6DD8814A2BF8G
	E29BC04700B85B9F6608FCAD7EE6BCC9D931B7E6DEBABA40F8DBB490BE5B549F6D49E067313A8CE9EE003E4D8362ACDEA8B1470C331A26383CB4DCF1B986CB4DE74FF02152ECF8FA3C2A26F702F1CE8B19FFDC0A7CE2BC63E89E5F170C08781E8AF41DD74097DEC17CE4275D447BA0609EGC88448206B7DF284633ACBD2E53D0E7EE7E132E6D23AADCD57E9D3E048F554CB5435E26FB97CF9141F4B1B47D0E632D71F1055BEF0976DDE59679EE9B52D52B55FB2270F8EB059BFD9584C79CBFE7691F2660F96CB34
	DCB9AE4FE6A103CC66B503E1596B893519F1GCC8EBD23C28EBA0CC99A94F278C9FFB4B9EC9B94F27846683D4801796B34B9B9E95AE0B8EE5A2E4E0D21ACD77B8A55C5675548FCF2A54557DF0D4CA76B8BC4BE598CFD09D7C5FE51DA0E3E4BFA8D728B00B3GFDGBAC07CB5210399E5A7498279DBA758149A10FC3D339CB5573BEF14E5B05F4A5B6B4A3204EFA5A2EEBDBE92A907D557E66FBDB9224D662D18B36FD0C4FBC744BC26FFC7EACA05DFB09DD3D353F9C39919EF7F4F8233EF37DEE739D9ADBC1B8118
	2F78EA271573B3F8DD78045F371BFD425A3C4316FDB1FC42526B0C3FD55743FA395438433D6EC4BEF7C01FED884E882099408D70GC06210B8238F8E3D375099D13D6BD3DA815781FE37B5B2C69AB29D9C3D3733704C1997F9969B06E64FE3724B9D90725B8A7DAC974E4BC51CFD3845DAB3ACF0EF66A12E8B5A6EE111D3A596E05C0EB35A4F52B65AA7EB7F36E03E9C9267FD7A1C6B723CB4BEED2E474E372BBCF64E55DB9EBD673ADA9EBD679AA9AFE0F8D843FC63927D63657367E4CAF9341C2C0C6E756583F9
	F4EDCD13B52AF098DCF95F3F6B8E92A5B74374044B89722E113DFC4CB9DDE9EDB06C750D2345516D559433AB760B183DEC58283D58516FED0676F6004C9BC2AF1E6EB46BEA690D59FAD09F51EE3BA17C25DE0131E0905ABA40E200C4008DB7049DE6B99774156726677115EFBAE31F732F1C514FB973B0669B1A92D2DDF264D96EBBDCEC7405AD3ED3177DA8F6B646CE676B12BAAE4F510592BA1F75451ADFF5B8163E30B3DDB2EABE53F78C3BA99CB01F4136881927C534EDA3A25EB664E2EE6E13E614108E7765
	91BC670EDC61271D50BE85B08A200FB0FF4DF468EF265087FBD4498F6C7586E5971998257EA7DF4C5D8C63D3812C002F81689895B41FEBC31E1936B5F07E5F5ABF47CF0E06F95EB09AABF68FB9D0A7370C32983D5740FBA9FE27894FEBC1D407311DAF411EA4618F57513833855A2986EE50A6FC64AE83F7271F6246C0A6E3C2CF6FD4445633BF2B082EE78FD5C477A7CBAA6677A7CFD444528F465F3181318FFDCC70ECF308FD4499384F8F0BFDEC81E03289660AFA6EF640D53944D97FE0642059DF54C9D3FAD3
	53872A575D455506471EB539626EBE6492BE63B53AE6BC3413GB638C4CC1F2E96B1BD6969C7C85783CDD1737C143E43162DAEFE07F307E3FA8A44F4C633B1DF50BC837D3A9BF1E379669C93F56ED198D00736C77B63E77523BCAB09397FBB337A115FF596F3FF5A2CFE14D1991F4F76763DA3E2EF3B3E370C781E2F09F907D94B7714421FE9AFD41044E3568CF6C66FD39FDCACC30EA40BF3536C62AC3BE561FF479CF8F754213D98A089479AFD8A6D5B866DCC8F3B83627D0E571D6CA25662F8FA96FC0DD871D3
	1F27AFFABC4FF1889AFBCD78796AA9F7936319BF49B900F90A876E22EE8F55B9EA4BD5703B9EE0AB45A936B6BEE725E76E9EB2C8381604309646A014F07CA68F73A369864D477AF19FAE45977287B44268BEC3FD835ACA55B864D59457CB6D31915A4D630217CA0760C59F0F4ECBC694DEAAB97D3871B0AF0C460B5416D147624745595E4E3D37F78A0B6EF15E3A3B1E477C741ABCDABFB76D0961DF2278D5A6BC73D15B8D9F35967A1C93A21738D605F1E7913417832C8158843083406695FE7A6D4AFA32C0BE51
	27843B40117BCC2E176D6F5D7CFBFB5772529E618BD67F7D97481A87D353E35DDD1CDE268B4D7C3974798A0AE77BCBBB027B8BC25FE200D5G9B8136826CB476972A2CE57BBB67766AE7F5DDF2CD50CA06585C00D71E8A23B1E8B3DD6E5FBFB787706D97F15CBA89F28668G981AE4777538C99C7330DD0C51A7236BF1A1F9460DF2407C26CB9A236F76987E0A0EBE34FBE2C6733AA49F2F2851A12C7F673DB9FC1EF109B4661EB90CBE0D4FB13AD9FDE105436C1BB7F1FEF7CE0A7A432BFB2375DBEF36D8CBEF62
	3B592518A6F3BA97ECC7609910271CA26C5535031329295024G64642A57D3D16B2C2FF5B6B49EB54EFB7914793D6A296C6A2835C904511B7C7DGFF3AD3ABF89CDDEE9339F7927415D3DE609CC8780F71DA0269B1277BD31B5033523FDAC25AFE121ADE99036EB6CE2F51970E270B1FFA4842767CD2BF04BA52700E20617ECDD89E3F365F0DBC58DE7A9CE2AFBE1F107167CA42EB7EE898F97CAD91FE649FB015623F7F51747B3FF397FAFF71E900515817F7BFE9B09BF5D5454E95A72B22670A412A683962E72A
	663FDBBED4953B5E3F344ADC6FE7BE66CAA95AC6A21C53FA00EDGE9GB63F70AF070ED4906ADF227816F36EF13C9361DBC5DD50CF2F0E3DD7EB75426FA28B2A3176BA26683A624775853E289A4FCBD7029449F02555A8A355BBBE2133219CDFD10D7A6713BD944DE24879C291C3EAE0EF23FE1167B60C4E2AE305735C96E91C253ACC27BFDFA8F24E843F48F35753B5B7C1BB950084C4BDEB15DD68FFF1C0643E9B074D792173789B695172433461C3166EG779101302DAE87DB6577F8CF0CFBBC75230C7E2B8E
	D18FDAE970D410ADF0A986CFBF38EE76DB79DF4A3724C7618921D8E13CD4A9E11E187E7C0A565C87812FABC2E7D29A0E43F3C01A645196C31322D22D81E4372A84E5D51FE1D87AD6035B62D3B6F33C0D0775D6AA73671E0C0726F24C2D564258ADG3BG6CC138FF82B4837481CCG5C810887C882D88F3099A095A08D40F68B625DAD417FDF979D1D07FF2E9CD471D5490B2A4F793F6AB0735F886BF55C1A1FFFDECFAA0DEDB31E5205EDC6E16FB0C0577BE6023C262017E2FCD7BC9ECDB6ECF326B45F40F5EA5D
	0A6645DA84633D3B705EF2409B77B86F093B1E47F244C6FA179F3A9519BB3EC7719337A2F3476F98F62FC35F16DBA2D7FE5EEE26FFC6053B157AC9F46D7BCE34BFB70CFDGA091E0AD40A600D4D5684F3E321AF97407F915307AB0FE7EF5166E6C623C64EB5C6E34393C31F987324D73CE71F1DD7CB99AE59E5B478E7C8C0D6BC5F75911D87C0217BF8FEE667258246C527B24F1E3CE46A1DC4B08C34C0CF9DF76A10CA95EG28176473FEC4DF3775424040B83B4F2E25390E2EB1BF44F0AFD15CE2409DB5E8DC1A
	27E65BDF3E30DD5CA80F9ECB5C65F3E30937BCFAACD14A4D31045BD6B97A62F170830346389983F755407D012E0938ABDE37BEE16097D13E905FA1A3C101FF1872B5C5F10322B6E442DD653878F234ED587204229A5F9414537B305767635F6F50B15EG183AA10FA7A90F5E20E4627BAF8D5CD563AC020E396793F2184F0769DE939EB37F5DEA7A1DDCF87F73361F5DD79AEC4F49F6A1872C85B15C8F6DC28ED8DF625FE74DD3F37EE76742FA7A1AB33A1EEEF74E55537FF2C65753B71DE6BDE53C7E52A16A02AB
	B5D1CBFA6CB87D1E955A4986AE6D3828AFEDB1F0EFE70B39B53A40BD1CA7661EB1F0BFB75EA9F568A227122AEDE17B272A4F702553B9E13CD0FDDE4F28BC006BA4409A093A2899BFF39CF5DA876131BB44F23C5E90D6CF492B9575144D7ADC9FE72E5BC456D368FBFABD321EB2C0773B9370A4A46EAC5F2A8A5333648BFA75216874E255EF68770121B0BD36F668ADC244FD68F347B1470EF247F2031C14F153850B65F24D35A23F6CB6F8CD40104357FE626D705AB939F7377649FC31F61C3176376B4236FD2E56
	9CEF5643184DD164B157DECF4D2A2524421CDD8B4CE37AB5D4ABEC70B983A7558A9BBCEC60AE670BF1BFB5F0FD7966F16C5B6DE7B3FBE1F3C92F13ACCDA5B933E4726D2F7E6FEB497FB220E5105D1D64A01696FB61FE0B91CCF663DBB87E19E2BB599BD4A0EE77823E13E4EA74112569B3BE390B58BDBD78490E264B6E33A2B6EABAF1500FB1CE6067B2A7BAA4EDC2C77BBF5DC5B28378C1A5594F3AFDD2E07C848B14272771B34A743323A8F9AA8E827C96D2FEF115BAE3154297730E9CBE3FEDCD86313413EC1F
	A2395BA597046FCE32D71FD0E5EDC2713927033FEB268BD927D0A2D80D4A0065CA0C6D63E66B302AE2B0FDF37A4B0BF3D2F35A4171CD2B64849D0916D8E70CF037CEC8AA101255CE9DBF90D554AA236FB307746A6FFFD0B4E4A169FC1337489F08E5BA28135D9E496F754D68E42FD1C15030F3B7E040C986B40F227A01B9E784F3224B20D177584B750FFF6DFF2ED19A6129BAA9E7520429A746E545FF622C47638D484682A0DAD83E4CD89E4754C50C59BF7D28637CAB3F9E83D5E8A3997E7AFA7A3F017EFF877F
	B7D0CC8345B43C83725DC1667D0B6C1F96FB267A837C64D1FED0836A5A619075D7DFFF7949FFBA33E3BB6C2E1DE451EFBD609C724323AE057CE3327AE05263E57B62D37E9C0FA9085AB20D6BE6CF9BAB628EBBE99610F27BE4D3463F2D4830104CCE321BC59B50DF8C99203FB422F4128C08B6D4755083E4F3E633A833E40EC2A2B6F32EE6A15AEABEDFC46C2323F3687F7C3B16964E5ACF675A0DCD16B09996C5B22B939B7BEC4F091544A61D94080A2313178F1B6EE9BF3B19473CBF1B298AEFE6565F1F9E2747
	E3F04AEB12C77FE82EA924FDF6CF4CDAE8699D667F6B6FC456945A2DB41781FC5C9DF1277A429E8CEA316EA83DFE496753A55F4D3381B75ED7DAE6F4B9DC933BC0575744985E497AFAE80EBBE91A737FD0CB8788DF2B1B146692GGC0B4GGD0CB818294G94G88G88GD7F161ACDF2B1B146692GGC0B4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA093GGGG
**end of data**/
}
}
