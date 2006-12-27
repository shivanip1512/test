package com.cannontech.dbeditor.wizard.device;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JOptionPane;

import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IDeviceMeterGroup;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.db.device.DeviceMeterGroup;
 
/**
 * This type was created in VisualAge.
 */

public class DeviceMeterNumberPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjMeterNumberLabel = null;
	private javax.swing.JTextField ivjMeterNumberTextField = null;
	
	private int mctType = 0;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceMeterNumberPanel() {
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
	if (e.getSource() == getMeterNumberTextField()) 
		connEtoC1(e);
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
 * Comment
 */
public void eitherTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
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

			java.awt.GridBagConstraints constraintsMeterNumberLabel = new java.awt.GridBagConstraints();
			constraintsMeterNumberLabel.gridx = 0; constraintsMeterNumberLabel.gridy = 0;
			constraintsMeterNumberLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMeterNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getMeterNumberLabel(), constraintsMeterNumberLabel);

			java.awt.GridBagConstraints constraintsMeterNumberTextField = new java.awt.GridBagConstraints();
			constraintsMeterNumberTextField.gridx = 1; constraintsMeterNumberTextField.gridy = 0;
			constraintsMeterNumberTextField.anchor = java.awt.GridBagConstraints.EAST;
			constraintsMeterNumberTextField.insets = new java.awt.Insets(5, 10, 5, 0);
			getJPanel1().add(getMeterNumberTextField(), constraintsMeterNumberTextField);
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
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMeterNumberLabel() {
	if (ivjMeterNumberLabel == null) {
		try {
			ivjMeterNumberLabel = new javax.swing.JLabel();
			ivjMeterNumberLabel.setName("MeterNumberLabel");
			ivjMeterNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMeterNumberLabel.setText("Meter Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeterNumberLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMeterNumberTextField() {
	if (ivjMeterNumberTextField == null) {
		try {
			ivjMeterNumberTextField = new javax.swing.JTextField();
			ivjMeterNumberTextField.setName("MeterNumberTextField");
			ivjMeterNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjMeterNumberTextField.setColumns(12);
			// user code begin {1}
			ivjMeterNumberTextField.setDocument(
				new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_METER_NUMBER_LENGTH));
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeterNumberTextField;
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
	if( val == null )
		return val;

	DeviceMeterGroup dmg = null;
    if (val instanceof SmartMultiDBPersistent) {
        dmg = ((IDeviceMeterGroup) ((SmartMultiDBPersistent) val).getOwnerDBPersistent()).getDeviceMeterGroup();
    } else {
        dmg = ((IDeviceMeterGroup) val).getDeviceMeterGroup();
    }

   String meterNumber = getMeterNumberTextField().getText();  
   checkMeterNumber(meterNumber);
   dmg.setMeterNumber(meterNumber);

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
	getMeterNumberTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize()
{
	try
	{
		// user code begin {1}
		// user code end
		setName("DeviceNameAddressPanel");
		setLayout(new java.awt.GridLayout());
		setSize(350, 200);
		add(getJPanel1(), getJPanel1().getName());
		initConnections();
	}
	catch (java.lang.Throwable ivjExc)
	{
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end

}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() {
	if( getMeterNumberTextField().getText() == null   ||
		getMeterNumberTextField().getText().length() < 1 )
	{
		setErrorString("The Meter Number text field must be filled in");
		return false;
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
 * Creation date: (7/27/2001 9:23:34 AM)
 * @param address java.lang.String
 */
public void setDefaultMeterNumber(String address) {
	
	if(DeviceTypesFuncs.isCL(mctType)) getMeterNumberTextField().setText("");
	else if(DeviceTypesFuncs.isMCT410(mctType))
		getMeterNumberTextField().setText(address);
	else
		getMeterNumberTextField().setText("10" + address);
	}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)
{
	getMeterNumberTextField().setText( 
			new String(com.cannontech.common.util.CtiUtilities.STRING_DEFAULT) );
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getMeterNumberTextField().requestFocus(); 
        } 
    });    
}

public void setMCT400Type(int truth)
{
	mctType = truth;
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8FD4D457194706A8EB30C7D35332561EE3921A5046CD5C56EDE9EAB78935E735B11B6454341E443307EEBDDD1BE3DAF75733B5A9E96F8CFFC485A502C0B69871DF8A299D7EE8CF8C12D68491908443C0B09070B173869EBE660D6FBDE08690777B6EFD774DE31801C4B71C73715E7D6E1F6F3B5F7DFD5F7D6EFDC3728E2FC837597388312593636FBD9BA171FFA6645D07FEFF18732244248CE279BB
	827486F9BDB78D1E0B212E795E240C9C72FAFAA61463317C45240C82F84FA5760C65A57092C10E8EF57F75ECC2696CF2FED547656C22657782D9705C8714GBCFC6E90D1FEEB205060EFB0781CECF6C2B6FB304DD02048601D01F29BD09C50CA8FEB3F961E3B29643B2B4E986DFA162CA0BBFE7CC7FBB60EE39913537181563741F8961347EC5B87D05617281E58A78A4A09C0B879D4723072BEBC2B5B2E8F7C55258CBBDC02572BF8F551B56898134685556DF00B2312CB2C2A2A234F96D197554BA3437D225AA6
	F8C5997A4A03B87ECB01CCBA9EA127213C1372DA73D166AAF8DF8714EC72738B10DFADA5E5B801E4094F7303BFD69B73681AF80C2C7E19B7F5FF04F9C6CBECFEAB2560BC170E3C366BCD3AA62575E87B2DD017B51414D18CD48764860A86FA0BE2217C423B702C6957EB1561E145DB63731D175DEA0B6296E5F82F2A029A03772168D2D4B7A1EC3D7FBEF0B20CBE33916B7770E76E47EC12F00A2F7D4A8F9FA21B3FFD493EAB0CCDB6A7145B178FB1DB2C9E8A5A0449CE0EA8FB6350F4ECCE177D91314A0E1F2F24
	2607113D7A05D27BD6E31C940B6C1357F12DFFC8B1D0846FB6839F0C7F8C657BACFC06538E4A47F29454ED394E3151350F6B521C360C64D4B40735C3DCD20D3DC1E6BADC13C3F5D9C8F197AB076A321472174B213A9495F0DDD6C2DD4EF0D2864357878D6837BA149783A5GBD89148C1482146365988BDCA87B1498EB93D47D7C08A43BC515303E15757970947D3AAAB4FAB5DD703A44CEC11504FED954085166ECBD5A20FFDA3FF3947BAE10F1D1740A2A20CB0A97C6970651F1B55626317E8DEC230A3456A98D
	0BA0E058C77838D76AF3512F85CD6F703985DD8C36E075D76BF1CDC605CE98918600F7E61721C634D7B94C3F81284B3443C6BA5EA5D10500D1D5552CA857C7FCCE1C89C970221DFFCBE3078C6F51DEEEE3CF937236C1B9CD61636C3DF840B2CE1D64C27585B540FDECFDBE4666C205E36657DD9CB3F559DFA7A7DFE88A0B9924EA05E14529047A4ECF68182897ABF767F74D613705CAF81F7A7431C21FD3C66AED0E3F4FDC2B0D17F02DA6C157B820C4534675970B99E64F8B1A48D15A2A8240C4F75062CC4F4585
	E906BDD9F999ADBF27F0DB9DAD680AE06F7F8FE2B24ED299F7E20F59085964E2836EC3622868556BC41F68F5430BE654DDEEC02C99FE6AD14CBECE4A7701DF5702A3E9A67F638674BDC157D529FFC4975BC5DD173C83417AFEAACB9B697785C38217A5B5E07CD546402F4D765E8674891762750AF4F14CAEC63D0F7A24A80B4320F42DB2625599FF3491576A3A98B0E242F8A3A6BB2302BC02710565862B043DB887D59A06A54790C5FAFC682F7B468BADB65A896B0DB9CEB9545579386F0D9D633EF7FC7CFE72E4
	5F89FB383DA36E6BF576F89F7317C4DF507718CE571B426BFE034686032FB1DDDB5F45F84912144B906DEA5CEED554B4A3C9E1184FEE4171B6011CEDC0E9B7B866DFE942FA9ED78C3057AA324CCCEA5A7A45CBB938B6620D919142B18630CB267DFEFE69C0C8DD27E1CBD67F2BCBE8CB59927BB65DC0DBBEDD40F340A2A81F86F25EE076DCFC0359034DEFD79F6E61617234019149B122CB9891859DC0689B51692CD9DE669F4FB446B82E727CAB7EA65A21874ABAD02C4A576C79EC1EFFAD3D3E02A4BF783F9173
	4CF8156936CE8D2ED971DDBC7F7A68B24A4C5412B28AB5EEE3F733F58F2851B445A5516D4730CFEE0BF5CD992F30E58FBCBD122A690D60F4FEEE4B37DBD0DF5C1F8C3631DF6F207BB54E75A448F58325E8487FCACB1EC5776540DB8B3495A8150E57DD087B34CBD0C55DC17F33ED2FE4C898957C8EED8C9C54C13D5FD10B3597703554D8175F3E0BB693A5FDD0D41D90866AA5D1F67755DAC6E91EED1833251D4C768F641EB3745B7068B2D2FDAB579EAE770D59FF48DE27B31B4BFA5076135F4AC85D567B3BDDAC
	A65E9C8F4D779275601E34C11F39BF31BC28F08A6D172C0766C7C7A9FF3B9E1A9FFD73A64F0F488858FB046F5D3FEC4578308E4ADB00D20072C6815BC04ED10E0707DACE11B9F223C640376A956476E9415040C04F65D4963F0C72354B69BCFE913EB73CD690EA875F687439470614D93F4C4250FEEF0714D70574E3D8B8D1D244FD543266D5D5CDA6B0D87C3CBAE50D1F6BC6D95E338D1EE48C4EC354C77F157A836AD3B8467D73E63D55BF576C2F346F0D601FEDE3CCBF5F18A587B64FC70B2F201E2B20EE8350
	96A062CF4AB88EF48E486D67EB7472658F66DAA38AE227D28B3C8608ED35217B9074E7B95ED29A0722E16CB8A0463BB1093CB5D05E6867B9435BD338AF6DB44BA5340C7E4A4A2BEE4E169BE13FBB4FA9564FAA63CEF38DE65B5F34C64EA97E27B5FCCE11529ABE2730373D4178B00675A4416B77344D1EF39CE88B17F39C22F3DD1F3F1F0E2DE922C6979BB799236EBF7403A4CC6E015B2FE127DCD2D1385BE8F3C1D7EAABCCDF796F5242703EE2593B16CC713DABAB40772E0FEC98F36A205C832487B8AEFE52E2
	75595840F49C2C88A92F8E70D8D5500EFB43BAA8EF81CA814A9B07D8B54EFDA021ED4EB8F541BFCB1CDAF6A572BA5FFFA57CBABFFC8577DDCD99D1DDE268DABEFA25502C8BFADB23AE8E2359D91B6F527E3A208E08B330704F34BF2B0B547FDF2EC4428BDB53C746B1D7140500B222937264B82ECB121993BA21AC83AD85CA98E7310D2D79BF0EE13C7240E19406EB7709AE32FC827B46CC723E19D0AE822A866A811A1C60FB74E3F5384E8C9D95061FFEF9AAB4CEEF19882E734E09C87B5C5394C3D913ECBFCBB7
	79BF257CE28B1F6DF31F58F8DEF19A6A62A6F97CFA19466894A8E75D643C53CDBC26951B3C9D87116703F274CD0E2B436D11F171CEFBF8DC1CEC8F6F7F156D337BFFD5FB2475E473FBF91C5F8D4601FE4F5D646B75EC5F1BD69FF588E3FAD555C5D5F2B79BCBEF5C7DD5CEE49A7DB327381FDE1B40B14FC1398B48B74577B07FBBFC8FDB7950B2529CF5A9EC0E196C2C3447CC99F7FCD341BDAC8176B0A67351C9AEF3AB54975FC25E66C9EB1E0738D9828D9C5817566F3BE92DC71B9CA3567AF3536AD15F9F582D
	756A34FA3451A123BF1B5B53A7785CD21ECEA072BF3D9FB1FF9E3C456694EBBC5BBA0F11638BFF370B61659FE85CDB8BF51BEEF18C7DBB6138CA3945E35E73B66495C7013FC4F15EE64A6B09E2E78946F359A26DE75877F67731EA23AF13B199D1461D6C176916B1F2095F0E3900874EBDEB2250A639741CB1896FB1B85FF9483B54D4E774A173E64EAD55D80FD40372208D52DB863FE49E73FD1BA9F3EFBE4E4F2548A343DE0DB039C773D1AE7A4664BCEE636DB3F26ECF17EB7C7EB67B3DB771D97B3198D7B9C9
	6FC0AD73E47C33149FE761335860196031A19E6AB23F40774D571D9833E5A847G2DGCA845A8434638BBC46BD57DECE6658BB1D0A2F990220EC89DBECFE5F1E3C3D7B7227F6F19FDC7D7883A4F17E2B36C8B9F266DD8C9FA546F32B7398653379FD3593675785F5BAD09C50AA2035C01B6F6273ABF41EE4733B6016749ADD97DC0374744AA757A909E3C1B6EEF88CD3EB27E66E1FAF19E7026DB0FEE6F4D2C6B9D0DDB4BB2B7A22314D2B043769098E0F6DDC32FF8F5ED1D8BE53B48B7D224C6A1E4F4F1BDEE71E
	CB58B9F91F5D9A5F928D591B6139BD9A3F055D07AEEE48F92A6184BC3D62D8B15F9A9C756018A5C7B5418B3922AAF90E1EACAEBFD9DCF13A54DC1B95E3566FD8E7763C9776CE8869745D46F7AC6339E641B1E32F19EC62B9E194549D263A909A9B799F53758FD709A97767D99C93C50FFC15547D6CD55B3188F2938DF9197303FB4E562114DDEC4E6F6743F6C0AAAA398F1E19A426BF56D85E6509396EEF6F04180C3BBFD7998C5F999D11F3146C0E70B9CAEEC7789C654D0E594FA0C7BAA25FFB16F4D86FBD19FF
	66F7A3D64F41BA75GC581AD85CA184FFD73BB9DC789754DB0FEF941BD0039B3FED551397C784E48F3FD24F36EEFB12BBB712E2ADF51F5E59847677C47BBF13DF445C7190C77843D73D12501C11DB5B578BF6A245FC1C48FE533787B3DDBBC7E2E0539254C6739D94535E9F78D415C6C3CB04052B366AFB6427324538BF8EED6C6F0CC994A51C04B9770BB0743B66EDB2B9770FCED5DA7561C2678CFE9E1F31A1D1F145937ADE0BE139393741D0D60BB46F94F93613CC7639313FF414E6FBC6AE2387CE73B2DB1A3
	E759913BAD0CFC24C9C3EE0CC5BE434A8FFAE86E847CCDB19C9FAFF67D891E1D421031ABE188D7D40A90E036290ACFD475G63521BDC436F66B5D977251DB0DE565F4D1EC3B19D52FAB1AFA9063655C057007CC031C071C009C09B001201F68265ACCC4AB88EF486289928874887943D106BDF77F169AC7A9BC020A0D78589E14E6FCE3BA3637CC4775CB8F7F453FBDA4027B360B34E0B273AF1BFD3BC9ECDB47D69CCF70E49EB545A94CD42F3A633619337EEAF67F966B67BE5105B4B159E24FE9237B0B4D7DAC9
	79718BC3F3253F906E2B2B20AE6F9E1E9B9E37D9657B019FFB4F27414CD77A50E7D6C05BB5C049C0BB0132E2819B319C87BF6EAE198587AC9284E14074E932DB75B9934BF4E9B61E57E2E76A4674F97C9AB1F1343F67C4A43990E11B4045DB7B07C4176E94864CBEC9BDB8163987B037B26A5677E0BC173CG93C11646692701F6E8689DE0671FB83A6F6B31ACAEB05E57A8AF96F825260C425E4878BE5CBBB73ECBFA4347F1C76F4CB8FE2AB7FC9CFF3F579A47996F2F3D98878740AF3B4CF635A66F2A49EB20E3
	A26FA3492D8F1A7C4BD4AF64B708A8507C4EC8759A233CAE7EFD5E423BEA70FAFB51C7E14A030A4A7B3AFA69F9CA12E5D69E20EDA4AF382CE98F196AA879840B5EB7CC5ED553473E0150B05E3FE5F9BFCBBE7FDC4879394AE0D8BBEB637843BB8146BBE7637843FB86E61B95FD117177F05F5C78DB55979EFF5F691B093F6F7705475FD3FDD67CB1DD85BB3F2BCA0865F78ABF246758B5D05EE072CA8A78BD4326D81EEB886FE485FD134212710B7C59C1BE0725F1C67EA215D732884E650B789D5E0B743794BA98
	006568757D0893F6EE7FDB81BF373B974D0CBF56B339755C0EF9BC6A62DFE40945F46C6F398961E7156F66E3AE98662C62863D15814BC10565A13E821E87C5AF62E71FA31EA026C726FDD7DA83ED1E8B2373CC8C4596F08CBDE6721694F08C616F8B596F941FF863BFC171152F10F4CDA55953F47E517DFF796506FF6B54D2C9C6A3F9932FC25A612C0591D3F4634D3C71131CFA325B27C03CEF87FEA3C95368A3DD53833258CC72BC9F62E7EACD975DB5BC96EBBAB9C8BFD4166127643286C19B549117E71BC91A
	97FFBCC48E30EAD9708E143140FC560FBF993255DC236E0E53A420EFAE5597C7F1182364FEFF7F09A3CDDB92D3093D1E6C119541DDAF38E03BE8A43B75C1D5548695596D777D6D9C9DA8F38CAD022776D4986EADF37AB8D9871ED8CD252F7B3F961B3DB53B9E9C522F12B25A925165B0DB38EB878595C409EA230EBF06D25463E65D3C3775F71E39D850E3A7B6E312B74894317BFDBA49708843129C5049EE73B42BE1E586F040F93D1AC7D107C139629065F815A9437125756577FD63350F290CE0D71D9CE65604
	2EE58322B2DCD66371C8DE519CGCC8B439FB20747B60E10B6877CCB8EB63D7007FE00C29DC99DAEAF277FAB687FCA7CDFC1B99514D3D189765DCEE67D8B2D7773B9D37C00BE7B29BE08GC7BDAC227E62AB6B9FF829FA7BB618DDBDC9275FDEE19DF2022D58AD437C15779D5A3BB4799EDCA69F22450F636E711BA3628C9B692E13BCFA2AAA75978F265AC9DAA349E0D190700B218C70CBA3DDA3C905A8C821079E3A47D0B69DG5C81201374C0B13D61A8E6422B68A4488C20A2B7EDA662101D89GE01181CBFB
	E953EC7ABF67B31B90FB9D20FD734C1997B1439604694F4EBC25372CE71E1DF797C63CA7D9FCAF0BBB7177061EF9F664611C12011FF22FF9E6487BB3091C33358F8B322C8B72759A2F9B73377381DD8C1E4FDF4D4371B55E4628E4F564A06ABDE46973FFD0CB878884EEB657EA91GG4CADGGD0CB818294G94G88G88GB6F954AC84EEB657EA91GG4CADGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2491GGGG
**end of data**/
}

/**
 * Helper method to check meternumber uniqueness
 * @param meterNumber - Meternumber to check
 */
private void checkMeterNumber(String meterNumber) {
     List<String> devices = DeviceMeterGroup.checkMeterNumber(meterNumber, null);

     if (devices.size() > 0) {
         StringBuffer deviceNames = new StringBuffer();
         for (String deviceName : devices) {
             deviceNames.append("          " + deviceName + "\n");
         }

         int response = JOptionPane.showConfirmDialog(this,
                                                      "The meternumber '"
                                                              + meterNumber
                                                              + "' is already used by the following devices,\n"
                                                              + "are you sure you want to use it again?\n"
                                                              + deviceNames.toString(),
                                                      "Meternumber Already Used",
                                                      JOptionPane.YES_NO_OPTION,
                                                      JOptionPane.WARNING_MESSAGE);

         if (response == javax.swing.JOptionPane.NO_OPTION) {
             throw new com.cannontech.common.wizard.CancelInsertException("Device was not inserted");
         }
     }
 }
}
