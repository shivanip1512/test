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
		sq.printStackTrace( System.out );
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
	//of device it is - CommLine, Carrier, Repeater

	//Cast should be ok
	DeviceBase device = (DeviceBase)val;
	
	String nameString = getNameTextField().getText();
	device.setPAOName( nameString );

	//Search for the correct sub-type and set the address

	Integer address = new Integer( getAddressTextField().getText() );
	
	if( val instanceof IDLCBase )
		((IDLCBase)device).getDeviceIDLCRemote().setAddress( address );
	else
	if( val instanceof CarrierBase )
	{
		if( val instanceof Repeater900 )
		{
			//special case, we must add 4190000 to every address for Repeater900
			((CarrierBase)device).getDeviceCarrierSettings().setAddress( new Integer(address.intValue() + 4190000) );
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
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
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
			System.out.println("*** Valid range for MCT addresses is 0 to 2796201 and can not be 1398101");
			return false;
		}
			
	}
	else if( PAOGroups.REPEATER == getDeviceType() )
	{
		if( address < 464  
			 || address > 4302 )
		{
			setErrorString("Valid range for Repeater900 addresses is 464 to 4302");
			System.out.println("*** Valid range for Repeater900 addresses is 464 to 4302");
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
		exception.printStackTrace(System.out);
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
	D0CB838494G88G88GE4F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8BD0D4D716C4099AE594932DA2E63746A4A6C5E658E8F65DDDEB06ECD84ACC690CD3A19BB33AB3542CB3E3E5589D133292AAF1B2EE6652FCC48581C103025A449F286123848C1A11CF4BC7C942A803BA1251877D0006C73F763D57F4039A771CFB5FFD7DE83AA11232EA29BA3CF74F3D771CF34FBD3FFBDF133C838BD313ACF904A425927D2FBA09107CCAC2D27B7A7E48F1A1E2EC9AB17D95816410
	144C94F806C3DFCCC4ECDAB6493A1B8E6DC5503E79D8EC5A07701ECB726EAE7998DE02701981FD2F453DD3B5BE1FB732B81FAD34FD73DC86BCF7GC4828E1F09A272771E4B57712BF4BC07A48BA171BDB86631F305BA6EB4345BGA2G22FA587865705CCAB9BFDCF3DA9F57B9F7A1C97C71D1CBA652B15164F028174DED561FD632B8E9E3AF720A26F2621C90E8AF834045A7134CE31F4273EC5BC06F1F3A654152EE4169141D1A585DD76AF10C880A3D54AE8EB93A451A9A9BFDFEA18C0AF5F63BA22AEA1B6094
	A518AB75A17D145AF4CA0F10CAE8EF2238D08F72DC826FABG5699780BDE441FF544263583C88E3E4E4F0E1E5557F17576E2923A46191C95E41D218E363EA807EF1D735C6FEF59C37764C88E6ABE817AB27AE3532CGB6GBBC0A8C08135050F330F41332ECBEB108787E5E71D4BD5AF59154FE53BA841FBCD8D746838CBE2372C5889E17B9DFB6EC4GF94683B6AF6E3E67B11DC41E62FB9FF569D9927FCF97ADDB8268A4BE325C72F77DCC97CB7BFDBAE13C375686633D3AFF34ED0E66FD0518F9EB53646454G
	3C173EDEE2C95069ECB671BEB100FB3D095AC0A13CA76976417096EA83AE931E5969CCBA9E5BA15037FE005B46559DDC16561485A43B22B5209EA2E26BAC4D9213612664AF4BCB14E71844F8269A327C1862971A70CC16BFD3BC3617C2DF7EE0EC9A332F32DC745BBB500EGD8823082A081A04589631C5C4606324B3E210D35890AD66FF6C8F6D1A1ECEED94EEEF80ADECD91DA1C2AA6B83B458EC1F188DD122892FD4C699C54C15728F91FD15B6F869ECD22D3D4844DA1BB01BA382DA6AAAA9B53181303E39411
	7636BB86C5E0B068A21C6E97B9396857022A7D45E597B451B70275F764601E8C899DC0918840BB53CBE79E6A6BB42C3F8D60262107D7A93D0B228281232626D516875C2EF6DC89D962C4BDDBE96C5060BD4C49F55C3D8BF109504E10B91D741DBBCDF4EC0EEE94DFD006390FFD604158EC15394D3CF5155B0CAD7399F262758B81ED06764B4CD6BAE5FF5FC977A2CD140B3543BDD7A770DB2B9C58273EF92CF8EEC29E4913243F43582BD7F763DE05022C8B81E28C9DB76C34B21B2D97D411DB69178A9818E877E9
	1C497932B7DD57E732A1FB9C452D85FA4445F5F645FB55B4EE32F20F2FB397650174D39FG8F261BCFF3B19F09C322D33309AE51E90797D56F2B4DC51B537D35C7B666B4D23C8B7C3B819CCAB5702D3968030226A90EAE37A6DE96B54D616C757537D3DE2A3B4B658B8D1C5775DCDC236C817FB6465F4CC55F68161DCE11EE12B1C56F6F223EA9CA62A0885DA03B1D9A43776521FD8F0843FAEC107230689992A4B74699D6A3AC9136639A9447A0ECBD06AA9266C23F2D2E4DB7692898F0D86B5C04276662BE68B9
	48FD70504813A46646E1CB209C92710C4D325A457CA6416573C1DD46DD01E5F752982163D5A66B1747B0EE86ABD6186D6F4AC7FA6937E2538A81AAEFF15B7F1FDD584F630BEE748D32A4B1159A3ADE33BB9B77C63C6596A1ACE3A03BE8686F173BF77A75F5683AE47DEF6CC6DDCA2698182E50FCB84CEB41D610478E90FA0B69B3669653879B7732B12E9D5A92C018B2FA9C5343169B18738355F53DEEC729DBF3E08495B4B0D617DB235AE1F55C13B496A30D958A2F57FE5A007AA2AA64B600F2156F6D2B193CDE
	1BB730102CFBFAEF503A34D9E532C92AEFEF2DD3F93DF6E38F720C023EE8156F057421B9E754292A5C6D2069CA57E3C13EF96F994E1A3F8D1EBD8EC555DA40B93DDC67C772D1DE4CE7BADA4F6F4707F9FE0F833E699A44C28D71CFDF4BB349DE8B38BE00B000E4CA6F51E14C6B5D02A2EA2574BFCB13457D421060ADD5BD604825B4CA14B6E06FF9FCAD5177653DE328B351217509CABB040BC607A859EFB41828340EC766CCC987537DD339353AFC2B1EDFC04E5E4B35842A15E7E46D33A4E8CC6759EE1F6E6F7C
	E3DA72063F7DE18B5343DB069EBAE14CBCB7574377878269E1151B6B61AFA31351830B574F0E29C95739FDF9B351BDB60732DA2D4EC6FDF7483F1E3CC87179C37E7564EB8D3C1EAC063E30A1DEDF3C3597E357C6E8E7F8E2538E811C856884386361B678FC7EA9B2C17D5682BE25B885697228C02D5B1DFDCFB20B2DFA3BEFCFAA0F2D04672D0AE1FFBDC4F8C62FFD29DF1B4DBBB7E65EFE3F76DA3FF94C7E8E97977238E05A261A1A8B06B13258BE45E60E6D9BBD2CB64B734626958330D87173EB693ABC35DE9E
	936EB61AE3C2F456C94B76A0B1416BE572C5F8CDF53AF1060BA8C4B957C2DFA2C046B07085E8856883889B66FB74761E4FA65AA3EAF76DF283601AA16EB67867C8C32709135C8B4B48C4FB91B5BCFA9E535F2F87387E16C3BF2B05DF2471F7B33433C7B8EEC1BD62AA215DBA42EB2AA69B66ED17517EB3ED23FF3276FFB60CD7C3623CEFDFF33DBDAE0FEFDB6B317DFDEFEF701A6B033D01EB2E140240B55736029C06879A6693976F5FD9B0FECD3637A0D0CD360F2EF5656EACCADBD5C5159A9CA6D73D6F375AAE
	9220B64372C457934406612D7A18731A5CD0E1786BBBA579017D551433FFE063B9BBF404676C10A60CFB5150DE8D304130137F3AE63655145B236DA05F2FFD62B60F178776E3AE2805F6A70086908690F51B7B61120289E365F96FB831722942607B7CDCE160FDDEDC087506AA3B15EE51FFAF7F25B05F6873F9FC0BA68E225A59181768FCCDD0FA45B13670A3BA1F75851B7FCAE1B0FBE1FBFAFC443C27A78D3F796D00F98FD67969FC1DDFBB71B64F372BEB31B61704E1592D01ECF7F01FFFE7546069503682B4
	83F45EE1711A59508B9E0C41BD0AB08862DDF6095D246DAE4EFD211E4F0D0671F1G9B11A674155F65BC975BD0E6E6ED953A7CAF594644493BBE19236E864B5D0954A6E36E329C3D5540A7B5A3BE5E04E739FB9A9D0F6DCD505779B50F076F523C93836DB883B72718474875862E0D46574AFB2013FB5CCE0F6E8FEEE7257B835BD9657E407124E67F787124FEFFB07BE072FDB0405751F90F4BFC2D032F43E560CABDFC9DB1GCCB739F57CBEF7B560B2C278DE3FF2E30FB9DE148A9E2D2626C9F158DBF53351F3
	4F75BAFEF637057018718825F9875A73G22C2F8CE779E61B9BD6A598524B564E240BAFFDD7BC94B72907D8C97624B69111053994FB5751C672107E04D8FA1AE3541DCE3224D4D05812570E47D55237AD11F8709395FB12A9F65FD51E26EFFE2D4BF6AE81FBE1F2D6DA707795AB67FA412C87F7CE950B34C2207581AD6684F360E0364504CBFECE1FB74B39A03499428112670FD5B5E44777274949EFF5FEF4233E3A734B500F9B856685B520CFD2B203DE18ABB8362790E7505B571DC0B63E9FD25538816BFBB47
	698B1C4F1FB1F8ECB561473BCF391F9C4F6249B34DD8277440D934F5AA6A6840DFD1C799701E8F301C621EAD3669F342270EDDC3B2715DA5A1AC02B18885BAFE59D496C7128C1E3D83380EEED9F28FBAD54278FECDFD8FFDA5A3946AAA0ABBAAA12E905A4521DC96D7871725B3B430AC4981E4B92873F7057AE4E1BC2ED0DFC69B3B934A77F6CF5D643EA99C1D643C1B77BD0F45696B75747E5C34A60637D3FC04894FE2549CA3C6AD023E7407F9AD11EA453CA3C1FB8640C200E5GEB81929F66F1FA6D7EF2B2C1
	BD51AE3BDAA110CB26504B5697DFBF39EFAD7F3E05470225BFF80AAC1B767B24E0E71774E94C960A75E7C27BC10A574FAEC5383E2B502781C4GAC81D88E90BF0D2F4FFA6084DB5FF93BC32B53B4213B0F5EE47045F5B8C40F8F0DC91B59721B3631B5C04AF91E47B7827D7469103F81EC5359F95DB59D47FC544447F4CE8FEC473964FBF6548356B72DC2977D364770D7B40C21EDFD432A23DB10749BB5BA04757F6EF226BE4FB8C49AF34F96E1CC5367985D6CFE61CC33B9B6AF53650D1F4E6F9FFE169428FFA3
	BCD3E6603759AC7C1A2B7319D2FC981ECE51E365E92F349102CC71C72A60041ADCF174FCF442DAFE42DAD1D9E26C77C20F793B6A69ED55816FA6911AE7685F8774E7747403FA9EDDD34CEB6FF950F7004A8276C1FCFFCC56D0CCB9BA5FDFE5F0BBAB7C7E9344366677C9870370DD2573ABFC4417CF937AB7EFE1EB1EBFG290ED41C64BC7AABFC7AE86D389FFD68F7FF9DFCAD6D0FF09DFF67240F265303B26287F266E7C2BDB15E7BCD6F736BA67AFE71ED0071F87E0172E0BE3A439A3CD65CE58DDCAB96D88357
	0AC556714F16A55660777D65D673FDBF0BB1DF1DC25F8803FD0A84D885308EA071919EDF7E4D7A912171A5C0ECB9EF6F45B391FED554B87FA70B022FB532E862EF11AF94615DEB172CE972A0526778FFAD427D52E497C5B2DCF4916AC8F17476E9EC280E0FADC27B13449E0AE6B9645767F98E491E89F57CCCDE67D65C9CF50F652BF36B05DED66AB21BBE5648EB4E10193C4E3D5A08B417C1BB8EA0E1A63F4FEAEE6276CFE6715AF745D7667A50FABCA5E0FD38692BB24B69D9FA0C1865735555602B7AB93EA748
	B91E46D146DFE96677C1613338CC19960EDBE148744AF5F35C4ADED0E4C98AA0938289E332E40479E4E276137289522D85FCE7983719760F0F4333C3685733AD26A6D921D68348B6C5F6090AB64C30742B066E0B714566FCFB876805070DDFFBB299EA2B3036DA84E3E3G5683EC86487E8E54DFGE7815AG6CGF7G6681C482C48344812C83C884C81F4D65DFD9DAB20E7C3AF1D043D784871ABE3F0B6EB04BDF88744AE70FAFBF63A71E8A6EB3522909FD46FD0AFE43GDBEF9FF669F78AA327B03F4BBDBD2A
	E878663DD359862EC5ED13D5875EC5B059934ECF2E86FCF712734A9AA7D7BB2E22BEE71B6DDFBB3ECE71ED337DEB473FB5F23F6F043E18593CD67E3C494C7F509CB8DB4D79A63676DAB57ADFAB0C6D838883D884308CA0EE8E371F1C4A62F16C07C5951F79B0F9BC4DE6F9566B32EC521F9961E3E5E373F6DA4C732C7A38CA7D599CE09EDB473B687A7CF798150703498BD17E820419AF3B7A45EE2DDD68B566EC7D84E999F90839315E177D89669407934CCB109CA374F35BE598686CE56759F7E82D53994E6290
	43ED22B88DF0A5860F762A60FEF13DEAE23F30D7854EA57DD5E3F309DC95B817382B4C39C4772DAA0C453D90872E9A636E9938EB86AE1452C45C9507DD6BB370B329DC08EF9611A14707D339BC94F7D5474DB7612E693885D5685B3064BED961F33FDBC54F438EC9E26D2769980793DC5D50C7A415516192CCF27F03013BE66C450B9DE377A736433CBF4C767E2311986F3FB03D5B9B9FFC5DF6770172E0EB36B7F1BD60DD9043B91A389E70FE0969B03EBA381D7E26FAE2BB5DD09D58CE5F2E9EEB276FD5873653
	7F2DB65BA913F5F6B33F978C8F67F7C921FB696FD9213D54407D77DEFE3F94E360CAACFCEE76DC0EFBA51B4FBDE4604AF3785C72393C26920EE4787C1F1ABE4397FAB3FDF8EE7A7AFD468F292CA1C0A3ECAE3FB3FEE2AF5A34864AE3E708A7EA504658FD8AA96077A951F34746B8733D0D7FFD8A7DCEBF577FBE6592DDEFBC60936672B34B4AB3BEFECDB91CDF7E230179853B3F213F8FFC54470F51967FC488BF8FF977E20D9D600CE587BD493D2683962B65AA8AF8FDF97AD1AE6B0CF34420BD5D66237DD5567D
	511E1A43E93B8C5A5FBB6F736D2A82F33E0904B1518174B156DFE764186745411C75934CE37675F981774145866ED2817741A283775D9CBE6ED786EED10EF99C7B6D36F35BE5D8DC541BA4D5D5C866A81D3C7C64A7EF2C7AE5071ACC52DA489E3CDA3C8C67DB4CE0229D3F4269BFD3ECA4DBDDB2646D4B00EFA1A9AAFD242A5A30A436123C1ECB78139DD5936DF5BCB72A9A59C5FF0CD106BF17A9EB9654BE8D7D7FCCABC9F1628FAA49CE56AD894E5EB216A84FF871E714C9F5B7D173D49D84644D2572A215D203
	CA6E8F338E97DDD83FAC19D89A49B6C9966C0DC2B7246F9632D56BD3C435CF166CDE5739DACAA81D5E9A62EDD4B210ABB0160F0BAD45DB95C368816F5F07E5A6E4B6C26073AA240C0EC4CFACB5C6589B7A8485D809CA0B06BF901515C3C65FC37B35A3BFED7A3053C21274C55EA2DF930B572511349EE150A18DEBE42BF10320E2E79AE0A0C8BA559ED99984612CFE4271AE03C769E3AB4B67BF777EDFA98F5FD40D9CE05A0429E53D22BCD8D6575363F00A8681D0AD105FE71047B125FEE3F6FA676E3A707A9F3B
	4094ECA4F9303C1C7E2F207FCF627F8A0A2920180A13205F0DE45CBF7FFEAFDFB3359F10A70B4A0396D05A881B287D7C4D15CF7D626C468D303AC612CAFF6B817B106D9B7505FB30CBD426C54D5F37FD5E3AD938CDAE3496AF525D66B5A8628ADBE89530EE68D4CD72EF1ECE361014961246328D58AF268C30DF1AD1DAC8B2E49BEAFA988136694226D2E1499803C4EC4AD84BC2F43AF9FF91311D0E4E247F336FDBDBB8EBA71D1BE7AC32006970C3FFE1B51241FE36E7451B449A0D64709BC72BFEFDD8B3297524
	310879E096F3483718D1FF7F7FA4BD9EC4D27DCE32641E79CEE1534379C16FC2D39EE771BF7FF17FBB05349D3822F6403B9E67E72A0ECC3C248DF6C639BCA8C812A6C883F5CEBB1ED76A07B551F7A736FD8752D779983DD37F2D468E143B5FB4677FGD0CB8788AB6C932F7992GGC0B4GGD0CB818294G94G88G88GE4F954ACAB6C932F7992GGC0B4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB393GGGG
**end of data**/
}
}
