package com.cannontech.dbeditor.wizard.device;
import java.awt.Dimension;

import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.MCT_Broadcast;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.RTCBase;
import com.cannontech.database.data.device.Series5Base;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.data.device.Ion7700;

/**
 * This type was created in VisualAge.
 */

public class DeviceNameAddressPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private int deviceType = -1;
	private javax.swing.JTextField ivjAddressTextField = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjPhysicalAddressLabel = null;
	private javax.swing.JLabel ivjJLabelRange = null;
	private javax.swing.JPanel ivjJPanelNameAddy = null;
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G71EEA6ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DD4D4553A5EDB5DDC45FA576E33F54BE705090AE929B7ACB2AAB5B2D4FC12BFD76B7A0AEE2EF76DBEAB5E4AF72332F7B783887E831A5152E4903C7EC4A388B78D49ABB210A8A841BC848375401CE1C68EB3634CC1C6B3FB5F377759679C0699944B352E4B0FF376377F3E7D6D6FF76FB3E4F3CEE8EA12E1B3A1C929C479F7A00990F385A1117B3EAA603878878E27915D3FDC00CC726C77A970CC00
	3A1A7843E99964534EF4A827BCF8B8AD646543E91F407BA6B22E636AA1F889B20F856A6E197055357E6709EC627324517274636B603981A09D66AAG70BD08733FF2BCDB4157C1F903720E10E4A06460C358E64C0EEC9DBECAE9378E6A0CGCBE81B7FB93ECD299F84E5ECF39FBC47BF445AC643F3B5256E0E5203CA3B16FBC2490AD9390675B817EEFC8E79212C6FB165E9A4E3A9A31249749F0F42332C2E3363F03B2B4B54AEB81DAE27AC365BCD56B6516A10DD9ED30F630A60311A2C62A5C73BD8DAEA264F13
	C217D8EE35FAC42F37CEF00A926CD7A852BEE7C7BA9D97E80732F98462AA7310DFAB213C994A85A3B87DAF2DAFD3689B5A99CA62C7EE8ECA7F60910C6E71A3B47A97573F1F5C72B00E3F7D44BA255FF2284F879C2345F60253927AB027E5E48E36DD8C65840074119CFF4108F8A3148F82540D64B4FE3D0B5358747D843240561932B1880D76110C36A1A3B59AEBAA5FCF7E144A0C798C6EFBAC54A5G24049DCE5B8DF08C408E30154A6ADF1B7642333CCD2EF0F5F5391C65EE77D1496A29F5D9C5895ECBCB21C6
	41550B6DAE0F1590A60F73F6948420074320305E32084056F61AF07999DFBF1AC4FFAC252486D8DB4442BC43D0E50C70B0EDEDCB4210EFCF513D0D01F76C077D99BE1A620D23B4BC5B2B93940F6592283BEF9467F35386CECBCD4AB012D1D4E5C88BC04BFD538F99D60EE2B4EC9E2D5142F83C3D85F9FC8470ADGB2C08840F80058511C47554D455763F1B5483F47A9C88D5DEDEE41A39F6DF6C8D6512370BA7FC4FF3C8E842C5F7E8177E3FC89AF566F511247AB8372E5C978DE43B2059FE40C4697B6F7E75039
	0DE37A4A4686F58FBF21BAD1B2066DE1320A4F25F8338E4F76F6AC55ADACDB20AE920035FF15365F966EAFA3EFD2FCE1383F0C380DDCC64CD0978E4076B72A956D623A3120BBGC7G1AGFCGC381424772BD9E5A3C6F867528CE373F2CEF6D19AD7094FD32C728F6FAE54159AEB68A9E0750A609DE2234E9BC03FCED6B556FBB2A5F6DB047B722D37488324365045141CA02B0F9D99B6B19CCEC6391E92D4551A54284DDEE4247154EEC0227A4F8657FF3DB85D954DA30FA798C4A44A52191C6048160BDF1AC65
	A33539B1703EF4AC67551BE791D7764861B44BA31CAF6F1C4902678990EED182BB6EE8C7B2844FE5AE234F65E19FBB344F984775726DA6AEFF667523C841C2F3C079C3089C47E4A9EE1C3F7C4D2263A682BEFBBC77215F66B5A97DF03E9B55090CF1B756164DF1C11DA3F9GFA17FA036D981FCF1FC7D9BD886B32G4863B92F77FEE7E4B2F8D4700ADC6ACEF9C0E0C42B46F956F6D78B7A94711268144D22DBF4DA61452B6C3929E535A60FB617AAABA5940F76298284462B62CBDBB2999E4650CBB72FBFCA6B85
	D976B85A3AE531C114E50733C36BFF1C52626DE57A3C4ADA1B85DCEB882CB18CA0C2DD6B7C16CDBA392AF139BA3B5D965498C5BEFFC97DF08C340FD7777FB3A346D4A90F7272AEDA566AA764703D8B8658F7A076342FFC44DCF70E0148CD60B9169AF16D5CCEB25CBB94379B682FD357FF495824EBF733F376CF4B2F699C4983107101G1305992D41E5F9CEEBE0D91E5F9AD816DFEE6DDFD6DFEF8DA42B2CEED9AB2E51556354497EDB2DE87F5ADDCE27C895D06DC2D8AC7C36855761F0BA64538EEB07A8B7B82E
	0890BBBE0AB2CC288D7341FB48235CB67664F15B18FFE58419FAAE4790A8FE02984730D4115544C7B55B48E8FA7BAC6A14A809DD40288AD737D3E6789554FEABF8EFB2459D7A7AFE6C3B9BE3EF8B30314AA1CA5602F650F559C47FB237317B02F0C970193CBD40AC935D89D38556D6626B9E6397DF9CB6860E618BC407EC97BD6A5067AAF4035674B76A21BD0DFB8A95595AC46EF28E27F4AAE37F7311DA446CD9C89B72BE7BB148C9G4C0FF1FB66BE07755C1EA9C63B42A5C9CCEC20AE6AB1140915F91BF57B5C
	72984B854361496AFF488FD41FB2014F53FABE8365D73C58ADC21801867304F819478A67337C6A9A8529DB0BC12473EB61A969ECAD431F93824B13A820FD6DD447F7882B884692ECCCA67F93859C536DF1F401EBC26721642C771F50672C59C0BF6637CD7014GD85FD3FBB1EE8A169B9E19C86346DC1E575E8E7D0683C4A9E3A5CEE0FA415AADD55B05C0B98CA0FAE26FF6E4920DCF73F8FE37CF4519A82E9B96F7DB8A0F21EBB374F9F274300B81759441BC090D5FB449BF371856CE759F7003A7717DFBC3507B
	3DF22F57556E20211D32973FE9536BB0437D36ED8DBCED8E0F97728C2B68637B79C09B520A311F02D676E45C67DF133E31ECA54D4CF9D8D151C3A55B622A0070739808C405EA629A9DE28F6139F9F1BE675770C95C067DD0254F4123B696985686614D8205A7ABF43CD9EAFEBF190D9D210EDD72BBAE937717E22E9568EC211DAE4654516DB0F54B8E8CC68599FC023BDBE6824346CC2EC9D778A07D0E676973CF22FC07C1B98AE081G4B990E67721CE1B960729F67BAE8E96C2503003F9DA09C00592AC939A8BB
	83B4CC4A5E3C3797F977D3AC915B0307B79DD1680BFDEC98A97BF13DA1509943600D1F98229EE73CDF7A3836871B67398CADEF73331075BBB89F8AA38E2759A3B89F96188371A1B40273C15EF1B3FCD04E91766B75EBEA7C67C175ABA202519D93612FDF23C45C6B2500CF0C607A155D1E29532FFA10E841598116CE0DB55A1319CDD34A335AD339CDA3BC66B918BF103CFB5DE41EBF7D51067CB2C2398C4082A08104CC6634FDEB3D6EB9C2252F1FF3042786C4577849BCCECC2F716FB7F5329687AF185CB7A6E6
	3976B727D0AE16CD764F416BA9FE65E47F9C7C6513DC0EB21E003C6689BEFFDD2E7F7C0B1F68BD678A3FB26BF736CF3F6DFE656CA7FB77E3BA36F377B6EEC3F422D8DAFADCD5B866236EBA2577D105CF32DC4382CF7B137CBC706E46F4051EC1CF72FDBEE84771A3213CGE0B9C0FAA478E900E3GD2A45F6FD0EBA0BB5CFB3F0399E1AD87E33C5854673C6E46F8185B271FBF8FC3A2FB77E36BDEDB49579D9649576DF3A0EE8914172BEB7C483ADE27EFF5AE07D3D6F56D9191638517475161F0F25C840AEBF7F9
	E8104DF23DD3548F2C04B1331F62637E5996BCA778EFDB601C60BDDB601C60A3DB7F79EDEAC7301C0151972752D7G74B1DC8E75B3C720ECD1F1C7EB293F07721027386C571EC2DC841463D4DC934521ADDD2A62DEBB294FAB313DFECF621E3A793CD5130544DEF378670CBFB53F7B0C0EB9F34BC64AEB2FD774D261C6AF2D547DC97E18844825B074A3BC8F38B3F7355226D2F6D59429BA7D6E1E6C40BACD1DBF135969142F58BFFD8A777DE5B4D7BA8265A6G77944E6FA10DFA3D98BC25B7FF077B15A7CE61F2
	F9549EDCAE2B6D016572149DF5446B6A76340B7E3257E84FD66BB46D2F1645AEE40B921F537E3260017C523F3F0D76E7F5417A3B6C41641B717C788EBD4F772B7212D12967D1349FCF967B15172BBC7A4101FE412752788BDA3E2C6E25D83EC1253952E965C6034662B62AEB2F81E12D04637EC4F1C9901B3F2B527CE58D5A91C9386C6A063E162731DDED0D5E1E93406D86A8FB1A5149661E5403312A4DA3F4818B9A5CE2BBA90C423E4D353C6FF0E8BF91A08EA081470D6271CE5EA12CE7922764AB17B9BDFBCF
	2629F5C5CA8E94E58E64DFFCB0DEC894174397CF795B6730A80D2F11709E8B902F43B19FBC114A750AA86603D72B78E7A8BE63998D4FFC73D10A47F281548DF9065B2CB1543F2D00F24A339C17F5005B31EC95F759CC4FF921FCD5456D2A64FD07283874925EB7CC4559543EAB1E65F1FE5179DEBE5AA4744825255FF89C5623C2C78D5DD526535E9AFE96167E9C576991B5D427215C8460FE0E67A53E3F713CE47C68E124E6D0E5403B41F8CB01E170F34A5D60F3DACCF93E1267A57FD14B67DC86754629549F1E
	5447423867338D041830AF2D7F3AD7BD6ADB9E51573BFA55A33D4F746ABF22D7BD7268B325BFDB5B6C1DFCEDABE70793E9F2F150FBCF7BD43626906599DA1FC7FC1FFE202CED855D8B540168291C1FD575B41E037266E960632671FDFB759477F1E6957757E63E3FF695B76DA862A2219C27626C19B4B607724AE9DC561FB80BE7979953C1AF810E81D827733A67E95DA0A807CEE7F9BE66702C6E58D96E3730BDBE0FA9E38473C5997D5485760D02BA47EA9D3EBF1FB810B3D7E6A304F3F81EE203FC16BCCF638A
	2AFBD30156D8G9F456DB9E06EE71CD4E5AC8EE6685384102754C9700E46B6C91DF3089957516E123A3B1CE071699CE3288CE0CCB7954A8CD7C7F5B98E5E17BC4FE949A9399EADFAFAB222FB53129F2D51426678B51D976536B01A6F2D2FCFEEF2BDE0F66E63DAEA8322191DCBD2715BA93EC607E776EFCC8D3F6FEF023A28E89E5396F7220DB63EGFE824082A08104G043F407D592A8BF8CE59EF9CEFF139EBA0A810F461A5DB5F9FEBEF6E8EF7DA9A577708A98F1348DF3D1B946C0CA56A85A66BCB146741EF
	73A81E2D6FF5895737EE86688BC08D00954087B0E486DF5F498B85ECFDF8C4D4AE4BC23B1D1EBE714561011006C6274956763E192FADEC861BBBF20626F731709E8F30E2063F6F1AC26DCD7A0B7EF74451941F7D227F9DF1CDBD3FA35E8DF5A1G4C5F2FE9627EB581F0686F53E38E27ED0BE173B1995EF226BF99CE76039268FBACC613611698CD0E18BEADBE0A7BG4103232B3B0B9D7653F992288D34C3FBB906478BBFE65236024F2F6DEF29ED8C05F66355360FD8F15FB12A28915A7078D3196F340CFE214E
	FE596BE897A4650414B6E1757F5530DE6927A637EA5F32DC34297EA76A6CEC673D33FAFF33B8061D6D2C0C61E7BF9B45C07599B38F27654F446F1D6C985EA973249E5889CF2758E3642E5CD485C6EE778E2F6004BC4063306DA8B09696980BCA7628729D5A23BF7FB93826A868790FFDA65B4F2B4AF35D6DF9A4074E8BB2C4783F04FF7445018D2234FE042127B26F6B6B38DEED9B77EF443C685D243CA073C6A873254C52E20444BBBFC8E6EBFE568C6E1B946D67F3AC3E2A716343620170A350DA58189F7E200D
	B95A0CB4FEC9383F5ADACB5477FC5D7B23B5573B1F7CA9406618F1CB67E0B6292CB3F81ED55199B84F2A6E8C1C7F1F6A6C7FCE70F4E770BB41331D7ABBC1E6D3E7DFC35DA88339E882388AB084A0EC963727E9528EC26DE9GDBDAE96D40BC8F3F9211797CFF167AB96B102E7FED4987925E8F3439E459550563F3BC69427D12DDEE0AD44EBA3A10C79EC707DDE6CD957C5AAE14BFC934D1B40B61B2E8CCB0955635E4960F6B3E4A64B1E1020A9B1145E3424D33B9EE7D859E7FD9D55C599E9EA77AE6F37FBF359E
	E3229035BC0B164354F2FD7D4FADEBB8D7EFF96E7C59675047064C076DEF6271C7B44C3FF2B6BFDF3C5244F30D12D89E0F1FED46369214EF87988EF0E3760E5B0D1134445EE750700B591056CCA37133CBE687C8A7FE98343B644B2F40A295956EE17D5EB4E87DF24E5EC8BF651C4696641C067AAC36E653B9FE3698914B79D0A5F25C72D81E4F4D7DCE6FA3B206EDB5F0DF6C4F6F7CB9ECCF8B67785F917CA599473582FE509C2E1F0BFD1F4333D1385077E68C10F59E17DB74481799165E20AAFA3FBF95E309D0
	98A78AA09EA091E05D3F03CF84B888D083F09E4087B084A08CA092A096E089C082C04ADC75AC694A1EFE68D012E1EAACBC027EDCF57E3560766215EB573797EFDC23F70C2067164BEE650CE859B5FAD6EB33F9C555AE3DF5ADC345D5FB6BDCDE871EADB11958D509F1075BA35AC40FC7343268C609EF0ED03B118D6B4C1F4BE33536CC75BBE740598106G044D5547EA11D9570B557479C694740D1B2B45EACB67EA319A2363CBEA0392G3FF2AE4FF36E24B6E8F75C613412B89EFB794AB0CEE392DAAFB841AA32
	986843FA7F98884FEB4F47F15BF43069663E6B2C3A49FEFFEF3E39DCE99E35A7F21CFF2E74870A9F94671FABD91B392D8A013A84F53D45677473DBDEE2FBE7D71ED7DF6A1BF333FEA7C4FD3FA1CA3BB0659999209F53377BD0A495D97CA2F9E7B0DD812FFA9CCC4D29368BE23BEC91BA54BE9FA663D82A5FE72A2C5499D2F0EF713B9D07A0B92E50AB7886E8686CE075D52941F52CB675863E77CC8D6C13DBD27B7AE4A1B530CF6EC8557BE4659B0ED414758E308DCDEA3B0BAA2ED945DD22E3A26E3443AA5BD57C
	D5CA97620F09B8A1477F125255C3F1CD8A6ECE9D2ED9410524622640126DAE8F6FFBCFAABD57F0C892AB5FCB5BB81CE0B6D4FE8C23B4BA5C020E6E07D49C524D7CCEA27DAD8553CBD3A67A38A0FE2738E4FF01F17F01BD0A4C5E1549E5B69664A9E59EBFE7AC5E0F3264F74ED8A539FAEA1423E3653EEBDEA67743E573389FA6D9D4EF209C8290B6EF20FEF8ABF5E36C7D7EB55A3A9AFAEEE4DD2CDF54DA2D5F1E7A9B694756333F8169C1990E1A4775F7B132D9514957F56F971AEFFD4EF05BAD1D034517474F71
	73E863FC9EF3B6DFC4DC89144DAA6E590BBCDE2DD3F1EDA26F9B2A620A8F703E91AAEE28FAB6BEF53EFA1714F9F3767C411B6C37E8407D183D1729BCE7AC707783BFD07C36857EFEE0D59657295DD09732002F77C0835F4B04851A6DCED9485E3315E741423EF67D089F4E22146DBABC7353BDF4AF2F82FE70C26E275D97514E48E0F819EE3C0664A93143420B83B967E9521D1B627AA2E10EE2220F9D6E2672908D7838057C4C4330C11BEFDC3F67347E310A7FFC64779A2F99CF2F6027FBDCD7F46F6B32EE3DEE
	6E3C25F3A8FAC86D51BAD8377177DC17C6D2DC89144DAAAE5D0238BAA8DBD55C3794670372F095B71A62A6C2F92A0AB364F05DDC20623E2638C4A8E7AF627B38854D35E2D3EBAFE22E9E602C468AF1012BC3F7D0437AFE05E10372FE6F96ED1CDD8786B6CE64A7DADFF7830D3D5D42C57A91920BD39B38BFAAD84465FFCAC3EF79FFA7DB9BA34702FBF94961F560F71DCA7CEB615F579DD14730DB10E8F05E0EABC01EA06131A00F6C795C37D9785CF5EA3FE7AC3D6FAE6653B60320BE84A0EA9162CAF274771D01
	F4E2933164EC6AB5FF396468F0628909B237B3074F1D32184F7DE0836A12CD103C5AF78235149E5953AD3272B08B7E6ED27F65D9837E65C7ADDED3C565C9CBE53D3AF7AD07B45E7DE5FBEE603DAB0720CE022DAB027F055A59E5CC2B7EAE4E78797FA6853A0BCBEC5DE3A8D92CFC9B3AD8777D23FA06F8C74E0D524C62616F4B103FA1B0D6B8C0AC40D200C4000C1755EF0E8E6DA641F3CA9674E8A9A56BF3FBD970D8776E326B473AC34B82473A434A7A463A8F15850EF5C71569E3DD76BB5CB85FA4D00C71EF11
	D42F072C6F35B7AF0C787BFF463E5A68CDA62B2A4927A8168D2207C67222953FB8D1FE6ED4C5D63B716B82CCCF2BC90A17BED23D72E5C92CA11BED75782934D7962D65BCC7704A64E37A8166BE7C46F45FB141EB17F12D07EAC80A93FF18C532D83584F962BE16B09C72614F31124A4FD36120BE82685DC46945D1CC6AA81B1E5E38B377785248E4E228A2EBA417E02D925AE1FB2A49EA596E913DF617E475397FF1048E145E43EFEA13E1382D6A72F131A6D4DB1568CE5F83A16B172D2FB200717310FD34A5DA89
	135A42DAE197BCB0156829167107E6AECF3EDA770B6D725F66FC73C90B01A4A90B3CC82E91034FAD13D5B6214BA1DD1649EA75645A0B152BG838261745ADC1EAEA04E68C79C2FD267B07D6B4C42FB47FEF0064E21F515C98E63A6F45D57A13A3A76155BEC8E27288EG2C0561BFD30747B6A63FB6D93EFBBEBE3E702FEDA08AE6125CD5D8C87F96513F7B71EF914594D1CC51FE60EF8269771FFF3D0F2F194A8F503311520392E002CCC210DFF9EB6643FFA8CBD88E2B2BA22974DBD258078C2D55496E2EB65173
	2B7177FE36F6E87C5D38CDEE14969F0E3B46270E08AB2C26D9D37C25625264B7C6A69BC8CAB5D945B48B649755836417EACFB5C9864D222247EFE2710B0AD514D852C7A0911B52D732901D2E5FDFC42C252D57533F99836696764A22FDB72B0B5C4AF808EA3DD7C7EC2A0AB5A8D8B46ADB00A1D9FDB31C72FFF9F95178F3664D86E2G1E58C54604549ECCEFA99B40FFA5AD24A36676A90111C9EAD4E71B254C04E08850C6AD49E49D7BF90291EF58CAE51249EF620C4A35DA69CD71FB950B8AEE8D338BB4E62791
	6DBB0674FFBECA0BB5CABF57D3AA138DA6D3FD7949EFAA0D164AA60B4924A35DC81F68F01202101E42C9B7D35239762EE287990C846C73732E4928148F6145A80B93767C28BF3FCE3CA3AB689D6710F865CCAC5E7F7CFA72D5DCD6BC60D746F39F1FFA758B927CAC2C21CB10A4082CBA4B1DD6BC97BBFAD996353B2EA9D7F1FCAFEF23D4323AE9D7116E8B3ABE7F8FD0CB878823FF16F95597GGECC3GGD0CB818294G94G88G88G71EEA6AD23FF16F95597GGECC3GG8CGGGGGGGGGGGG
	GGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8F98GGGG
**end of data**/
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
 * Return the JLabelRange property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRange() {
	if (ivjJLabelRange == null) {
		try {
			ivjJLabelRange = new javax.swing.JLabel();
			ivjJLabelRange.setName("JLabelRange");
			ivjJLabelRange.setOpaque(false);
			ivjJLabelRange.setText("..RANGE TEXT..");
			ivjJLabelRange.setVisible(true);
			ivjJLabelRange.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelRange.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelRange.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}

			ivjJLabelRange.setVisible( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRange;
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
			ivjJPanel1.setPreferredSize(new java.awt.Dimension(342, 27));
			ivjJPanel1.setLayout(new java.awt.FlowLayout());
			ivjJPanel1.setMaximumSize(new java.awt.Dimension(342, 27));
			ivjJPanel1.setMinimumSize(new java.awt.Dimension(342, 27));
			getJPanel1().add(getJLabelRange(), getJLabelRange().getName());
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
 * Return the JPanelNameAddy property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelNameAddy() {
	if (ivjJPanelNameAddy == null) {
		try {
			ivjJPanelNameAddy = new javax.swing.JPanel();
			ivjJPanelNameAddy.setName("JPanelNameAddy");
			ivjJPanelNameAddy.setLayout(new java.awt.GridBagLayout());
			ivjJPanelNameAddy.setMinimumSize(new java.awt.Dimension(469, 110));
			ivjJPanelNameAddy.setMaximumSize(new java.awt.Dimension(469, 110));

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameLabel.ipadx = 25;
			constraintsNameLabel.insets = new java.awt.Insets(8, 7, 7, 5);
			getJPanelNameAddy().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsPhysicalAddressLabel = new java.awt.GridBagConstraints();
			constraintsPhysicalAddressLabel.gridx = 1; constraintsPhysicalAddressLabel.gridy = 2;
			constraintsPhysicalAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPhysicalAddressLabel.insets = new java.awt.Insets(8, 7, 43, 5);
			getJPanelNameAddy().add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.ipadx = 180;
			constraintsNameTextField.insets = new java.awt.Insets(6, 5, 5, 28);
			getJPanelNameAddy().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsAddressTextField = new java.awt.GridBagConstraints();
			constraintsAddressTextField.gridx = 2; constraintsAddressTextField.gridy = 2;
			constraintsAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAddressTextField.weightx = 1.0;
			constraintsAddressTextField.ipadx = 180;
			constraintsAddressTextField.insets = new java.awt.Insets(5, 5, 42, 28);
			getJPanelNameAddy().add(getAddressTextField(), constraintsAddressTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelNameAddy;
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
   {
		((IDLCBase)device).getDeviceIDLCRemote().setAddress( address );	
   }
   else if( val instanceof Ion7700 )
   {
      ((Ion7700)val).getDeviceDNP().setSlaveAddress( address );
   }
   else if( val instanceof DNPBase )
   {
      ((DNPBase)val).getDeviceDNP().setMasterAddress( address );
   }
   else if( val instanceof RTCBase )
   {
   		((RTCBase)val).getDeviceRTC().setRTCAddress( address );
   }
   else if( val instanceof Series5Base )
   {
		((Series5Base)val).getSeries5().setSlaveAddress( address );
   }
   else if( val instanceof CarrierBase )
	{
		if( val instanceof Repeater900 )
		{
			//special case, we must add 4190000 to every address for Repeater900
			((CarrierBase)device).getDeviceCarrierSettings().setAddress( 
                  new Integer(address.intValue() + 4190000) );
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
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 357);

		java.awt.GridBagConstraints constraintsJPanelNameAddy = new java.awt.GridBagConstraints();
		constraintsJPanelNameAddy.gridx = 1; constraintsJPanelNameAddy.gridy = 1;
		constraintsJPanelNameAddy.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelNameAddy.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelNameAddy.weightx = 1.0;
		constraintsJPanelNameAddy.weighty = 1.0;
		constraintsJPanelNameAddy.insets = new java.awt.Insets(22, 5, 2, 4);
		add(getJPanelNameAddy(), constraintsJPanelNameAddy);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 2;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanel1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(2, 4, 202, 4);
		add(getJPanel1(), constraintsJPanel1);
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

   if( !com.cannontech.device.range.DeviceAddressRange.isValidRange( getDeviceType(), address ) )
   {
      setErrorString( com.cannontech.device.range.DeviceAddressRange.getRangeMessage( getDeviceType() ) );
      getJLabelRange().setText( "(" + getErrorString() + ")" );
      getJLabelRange().setToolTipText( "(" + getErrorString() + ")" );
      getJLabelRange().setVisible( true );
      return false;
   }
   else
      getJLabelRange().setVisible( false );

   //Dont do this, for now
/*   if( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(getDeviceType()) )
      return checkMCTAddresses( address );
*/

	
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
public void setDeviceType(int newDeviceType) 
{
	deviceType = newDeviceType;

   if( DeviceTypesFuncs.hasMasterAddress(deviceType) )
      getPhysicalAddressLabel().setText("Master Address:");
   else if( DeviceTypesFuncs.hasSlaveAddress(deviceType) )
      getPhysicalAddressLabel().setText("Slave Address:");
   else if( deviceType == com.cannontech.database.data.pao.DeviceTypes.MCTBROADCAST )
      getPhysicalAddressLabel().setText("Lead Meter Address:");
   else
      getPhysicalAddressLabel().setText("Physical Address:");
}


/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
	return;
}
}