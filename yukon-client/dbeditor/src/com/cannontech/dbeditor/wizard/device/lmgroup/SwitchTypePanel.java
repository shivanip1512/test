package com.cannontech.dbeditor.wizard.device.lmgroup;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import javax.swing.ListSelectionModel;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.ClientRights;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.roles.application.DBEditorRole;

public class SwitchTypePanel extends com.cannontech.common.gui.util.DataInputPanel 
{
	private javax.swing.JLabel ivjSelectLabel = null;
	private javax.swing.JList ivjSwitchList = null;
	private javax.swing.JScrollPane ivjSwitchListScrollPane = null;
	
	//hex value representing some privileges of the user on this machine
	public static final long SHOW_PROTOCOL = Long.parseLong( 
		ClientSession.getInstance().getRolePropertyValue(
		DBEditorRole.OPTIONAL_PRODUCT_DEV, "0"), 16 );

	private static final String[] SWITCH_LIST = 
	{
			"LCR 5000       	(EXPRESSCOM)",
			"LCR 5000       	(VERSACOM)",
            "LCR 4700       	(EXPRESSCOM)",
            "LCR 4700       	(Golay)",
            "LCR 4500       	(EXPRESSCOM)",
            "LCR 4500       	(Golay)",
            "LCR 4000       	(VERSACOM)",
			"LCR 3100       	(EXPRESSCOM)",
            "LCR 3100       	(VERSACOM)",
			"LCR 3000       	(VERSACOM)",
			"LCR 3000       	(EMETCON)",
			"LCR 2000       	(VERSACOM)",
			"LCR 1000       	(Ripple)",
			"LMT 100 Series 	(EMETCON)",
			"DCU-S1180      	(Golay)",
            "T-STAT         	(EXPRESSCOM)",
			"Point Group",
			"MCT Group",
			"Integration Group 	(EXPRESSCOM+)"
	};
    
    // These are the values that correspond to each selection
    // switchList
    private static final int[] VALUE_LIST = 
    {
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EXPRESSCOMM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EXPRESSCOMM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_GOLAY,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EXPRESSCOMM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_GOLAY,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EXPRESSCOMM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EMETCON,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_RIPPLE,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EMETCON,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_GOLAY,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EXPRESSCOMM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_POINT,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_MCT,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_INTEGRATION
    };  
	
	//normally we cannot show SA protocol groups, since legal permission
	//has only been granted for a few specific companies
	private static final String[] SWITCH_LIST_SA = 
	{
			"LCR 5000       	(EXPRESSCOM)",
			"LCR 5000       	(VERSACOM)",
            "LCR 4700       	(EXPRESSCOM)",
            "LCR 4700       	(Golay)",
            "LCR 4500       	(EXPRESSCOM)",
            "LCR 4500       	(Golay)",
			"LCR 4000       	(VERSACOM)",
            "LCR 3100       	(EXPRESSCOM)",
            "LCR 3100       	(VERSACOM)",
            "LCR 3100       	(EMETCON)",
			"LCR 3000       	(VERSACOM)",
			"LCR 3000       	(EMETCON)",
			"LCR 2000       	(VERSACOM)",
			"LCR 1000       	(Ripple)",
			"LMT 100 Series 	(EMETCON)",
			"DCU-S3000      	(SA-305)",
			"DCU-S2000      	(SA-205)",
			"DCU-S1170      	(SA Digital)",
			"DCU-S1180      	(Golay)",
            "T-STAT         	(EXPRESSCOM)",
			"Point Group",
			"MCT Group",
			"Integration Group	(EXPRESSCOM+)"
	};
	

	
	//normally we cannot show SA protocol groups, since legal permission
	//has only been granted for a few specific companies
	private static final int[] VALUE_LIST_SA = 
	{
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EXPRESSCOMM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM,
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EXPRESSCOMM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_GOLAY,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EXPRESSCOMM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_GOLAY,
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EXPRESSCOMM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EMETCON,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EMETCON,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_RIPPLE,
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EMETCON,
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_SA305,
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_SA205,
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_SADIGITAL,
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_GOLAY,
            com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EXPRESSCOMM,
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_POINT,
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_MCT,
			com.cannontech.database.data.pao.PAOGroups.LM_GROUP_INTEGRATION
	};
	
/**
 * Constructor
 */
public SwitchTypePanel() {
	super();
	initialize();
}


/**
 * connEtoM1:  (SwitchTypePanel.initialize() --> SwitchList.setListData([Ljava.lang.Object;)V)
 */
private void connEtoM1() {
	try {
		getSwitchList().setListData(this.connEtoM1_ListData());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public Object[] connEtoM1_ListData() {
	
	//normally we cannot show SA protocol groups, this checks the 
	//specific property.
	if((SHOW_PROTOCOL & ClientRights.SHOW_ADDITIONAL_PROTOCOLS) != 0)
		return SwitchTypePanel.SWITCH_LIST_SA;
	return SwitchTypePanel.SWITCH_LIST;
		
}


/**
 * connEtoM2:  (SwitchTypePanel.initialize() --> SwitchList.setSelectionInterval(II)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getSwitchList().setSelectionInterval(0, 0);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G9AF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DAFBD0DCD77D5FB4B4E99BEADB658F0C29039615D12AF1C4CDED54C8B3E3EBB2265554D6A74E601899F1DAED9C69CCEA99BDAC892489F935E9C51B6AD6C28215D0729063165AGCB82E18B8B7B00C8B43930977612E56F666EDD9E89C97DFE4F636EE5398BA43F1F4CFC666E791E5777FD3E67DE4801BFE7EDAF319FA024E4BB61FFFFAFA1A47BADC2861FAE0F891ACD4EAFA3263FB7817B49A7F6EE03
	673D50673EA73FEC9FF96B468EE8E7C3BB7A217C325F436FD7485BB75654400F947BE4C25FEB67B6E8F36F73CC0B58271C360BBC95705C8348819ABE0BE45C3F2227125357F33AC0091D10CD43B866C84FC1CEBB85ED8FA09310BB4C462F0567CBF4675B1D2778388B77E511226F9C316F46F5CCEB8A9C9AE1F35B7953C13ED032E584777274D470B9B6E82F82207025E44799A7BC1BBC17C77EB9240E558FC911089A516521D07D04F2D5520275C1F9DC99126B43E3A31A9A0FBA1D5E89C59F8A79262232C70A48
	E1C242A1DCBF33E787DD0F1013505ECAE91F2F46BDD7426FC7818586BDFE9869CDCAFE198F90D6041C6F9EE962F274DF7B8249FBAAD23AB7051CE98A13AFD7C94819917FCD799F28CD7E6AC25D97C2DF45E8FE198360828481E9005729AF9CEF8537A24D03FA2BBAB626C61A235116F0D0FBDF8D4AE1786DF4C28F27754AC32A96A40459BB50D3E7414FDCE07302B7BD0F69A4670430FDEE6F274926AFF55A4BADF432A927413EE21469A2EFB4219336777DA9775EB8BA53B7E76E5DC74CFBE7AFD1CB37DB6C1D77
	E42D3D102FD3EC5A3B6EB25A7AAF54568761F789778FC62F237428094E7CB40F7A8C36ED50377932700D7EBD0297773665E45FB137259EB2731B6D6DE1464325F0B2AFBF27FB2607591E6586AF4FD3FA36094EF839C66958DE8DFD0E317CB266DFA74FE05C26C1BB8B308A70B8E08B20A28263A242472E353E3DC09F73C81A5E92D742C1D9A3EC6EC957EB7014A7F5CD6A0844F4A9B2A487A4CD118643B2E4C2B666DD9772B6B8E3DE8B757DA158632C9C11B5C9D754882CAE0D493A2C455818CE57FE9C23493457
	270C493041D81408F53D2ED7609916E23ABF9A14F4B9B102758F3850A663D2GD6048560B7530BF49675F59A646F87048CBD7C002E57A9EB10AF1CCE372ADE0EC7FDA889D99521B62739E39AFEE7C4040E7F5D0E34E2E89FD045BA3B5BDEB62D63D20610FDC91B92B1D6F398F3F30DAAFC66177D42E7DC3BBFC56A1EBCE769B334DFE53EF2C1CD0E1D63F4CD640B353FD65DFF13F13C07733470DC717DDB5A23F4016318FE57F5202D52002FAC409AC3476DED8E6633ADD2CC96DE5A2D010349410446991F4F9D96
	E78A6E3D5D607FC5CAFF9A56A4517C321A28505D22437DC963EFD64679F5371C6EE166677FF38F2623FF1E41F3C99E17A33ACB0E4A11A07C08713E36B368FBBCEE07D5E3CEA725C7A14EDBA130E2863D678C4622246B1AB29857E52F2C6BCAE4A451FF1E6E950B8FC693A9C26CF5698C4A28CEC01C9B6365B398A3C3EAA4A2D3E399D3F87F880DD1B9AC0F81532DEABC22B37A58D974734B72944F91D74EE271B3AE056318EFD82D30D25A05B2E84A9838G26AC12974578DDE754BA888F50306639831EAB22A296
	A72AC4AC9E3A7A893266E2355D6AAC497C144BDE9CE531326FCAA29699CF036D563C07E82E60749863B56D9D54DBF23142FCFFDBA72E53G6B3BG972E885F5F5C0E7DA23FF027EFD543E126CAC347CFF46CC31B48D762B224E5CCE41D065E1E6EF8B929AF40F5487A8BBAD007E1D38EAC3A02BA2CBFA2EAC17D8A4B0369704C862C65FAD8216138C2E35CE9E8E30C75EBCC5FA10D571FF43D2C9AB12ED01B399E09C1CC46C47DD7FE91EB832B7AEFA42E5447F585B32D2403B3C763BAD5A22B777E563B0347D8D3
	CC54F55DFE542B8E6DF4C0F6CC587E095D222E4B381CC58A9E78E34A7AF5F50C49D090CB585E31D854F5DDEE5C53216717B56842E683674CE7CBF3AC268EA974D8637A5E59E9768DC65B5B39931E430A9653BBA0F8A705EDDE6DC4FE715C63E4DE87749D91B5098B761D861452715F20F4A522680A94D62E4AB5357B62B85EF97400C926BC987FB420D897BA7F5659B7290F68EEA52623F2EB865C235238D49F16A2A3755D032360EF6F54860828439E7C53E93E567A4FAFA7BBA4275D2AAECE7F79C1FBFF1C694D
	96CF68AFFD63374B33GCC167DB5955CEE0F4298B2AEE4192C4178173411E30C476FB59C61F924CDD75FBFF651E0355602D74E670BEF88BE8F39D610D3C58ECBBE912746F90DB61E60D374B1BE9FB378DC89E38A47056E9EBA7E8726BB2F4C4332835CD503C4D55351C1F5467C73E22D70CF4704B8FF1C3D49F5F07FC462DC8ECF4CBE23D9FD788875EB5B842B8F379B746FD2FA06094EF8777B856FB950D7B1A9EA1A373BB0CFC6211D8E48862C86EC8294CF8AFF3E3F73841927E62482C72430F74621A0EAB7B7
	5A9372B8EF7739118F1A4709B8A37FFDE43E3BC86554CCDD30F917671D5714B40F59ECDDAD5E3FE90EC157192C0FCD409967F41EC32762E74825C67399621BE2B94B06CFG33691523422639D3A237DCBFE34EAD2B779E376FCA61F31BB9EF5B2E1A6E85461DF1F3B766CB9774DD82CC82B2G2BG9B81DB2F8A7B94385F194FBEEDD8987854E66A1D2D49A7B157656FEB12F5D9F9ED266E2CEDD0BDEBDEFDD27BE452BACC3EA0FD5760063E904086D881D8835884A053463D787D667964B32E93BE3595686DF016
	4D16D2703B6196657C713CF2BA262D6685EBF15E296905555BEE98476EA0BD74BC3B84ED5D209D74A1AD935AAB27C58EF94C0F340D50FE5620ED24340A6BAC4FB35A00FF3E9ADE2C3190BE13417650675D637FD2E7B3FFB855153A56FD2F4B3A56ED6B322EF5BB3B76B3BA2CE13E719A774E2E39EB6101AE2BDA788DAA674F74D709C55D5B851E4A75708A49B87292B11DE9C4642711B6DC43A2BFF907B4287E306AA146FB2E469DDC7FF557C55DF3FB9D667BA03427819957058F0C9F95BE1CFBFD361D57D850D8
	2CBE530B7543C6685F8258F1A33F4C81B8FDC344682F3A66BDA35AA667B8A3FE591D5A264FF7DB5B7445EE2C59E2EADC9B12136DE677D49AFD09CC50214BE328FEB6E61787676BD0E048336C7EB21D4F7AD24DFF5D134AB7187D9FFBEDAF752DD8CC0E51840C458A37FBD74DAEEEF74F30260E359E637BC6FAD26BA15EE32D07EBBD2975F0DB4F7CFAD85613DA8F9F6E19DF8F9F6BC92587A6EBD92D59470F43EF164B2E51FA689278D0740648EF0FD0DA9A34B3EE08BAB0540B773E30B42546F5C28EFC0063BE68
	95E7F38E0CDB83A884945FE07E4BE2680B932863E1B899009DEFD49EA2DB685C93BEB17782342300FB81AB810FFFA0767CEDA34EE5851DDB9A04FB9A23973D0EF1406EF0D8AAA442115559E581F31DFD4A46EA068BB691FF77B70A780B82AD4DB6B35EB2ED296A3D97E8BC675A125FD9169EC57AEADB72BB4BE5F562FE309E7A9A96897D5ECD6B158CE82FB4E82FD609B3E56DA2914F3F6ECD6D073F693576C362354E31E55E39F36C9E6F5C7E7361DAF18F583A88794D21BE027296AE92767A1198F31C0D59E20C
	E93A8DF2965549E7FA453BA1B75052EE93757B31CB874D3936DE1A501D4E331A92EC11C65C546BD81EFD2ED72C397AB611E75F70621AC5502ED81CDFF6E8312881A78F0B9AB077534B095B56E9F93FAC709D37B7AD66770E45099AB087EAC03667F91F58F38574EFDC0C3425BE330F217C771530DA0475BFB4239FF5FB14187B1F19510F7CFE5DEE6EFF43EF6EC79DD57179CC36F555C23662F5B9A47C65FF243CBB9FCCE3B21D66CFCF200A9C3A6B45F22677DF8FD7926B9A9C830E497FEEC0545713E942DF739A
	8D9FB6E881CADB8B6DE78D5A4AC6615764F691FF3F2BC3BF71986D170EE29B5FC13076FDF58BF94F0973EF3DC67A6C0276D8683BD32BF92F10F50DE81328A68F4B1AA6873D4AD52C97D8ECFC05660090482083D8CE2C2B92B9B18BE8F9000D00A24001A5A2463ED90BB6EEB07CA0EB427C8D70544E3FDB3EBFC31CDC423F8172676A25D51C4F7237441ACCB78F4E3A8FAFCC278F5F623C221B1E47726EF3B46EEDCBD85EADB168DBA9BD5DC4E739274AABF2CFA67495AF91776FCABF6A7D52D22853G9900DC40FA
	C061D2110B333D281FB9EBAB1F9AF5C3328E1B52AB136FBF3EDB7B46772DF29163F9DF7BA4D935640512D4F75862254C26876FE04FC2DF1529EE7CDCG65F3C3DF88108E48862C82BCFE07106FD9EF9D132FAD28684D3AAE8D05681BB1A1DCC011A792E4BC4819ECAD81AB59B6F3BE365EB1BBC6197FEF26F921624E7C324ABB05EF0FB5A23FA1D5D32E2A919D0AD4FA2E37C81AC78DABC3D3465CBB695C77GAE183B6ACE24FD3B51B507BE45FD2714A38D66E4822AF8BB97FE9F057CDBDEA2E277E1855F85A6EA
	8CE85C055F7D9F7392A2466C2CF240B3A2CFB84411D56FD2C0BD78712B66C4C3C31D63585FCE54329C5ADBA54E9057DD628C31D5A51FA14F937399D0D91DFC06144C68BFD71DFC061C1FF106CCD7A71FA1E2BE730BF1FA2ECD83BF19F789BFB8617BABBC8352A877F0F487D523DEGC40F26C6E5CD1FE2D47A861B477073D4AF6C77EFCD3FEB3D37724D6A6672C74B7FF48F2617B77D29EB319A3FF5ADD66F372E45CE7A673E4FB679D3FF5BE9751B3F6DB0BBE650DAE1957A95E0AB20E2995C8D17891BBE617F8B
	2131ED9157ED41913C136017E4DD6C1F97C8AD6B4301793FBF2F8DE06C8C2A3A2E0E617A027E1DG5ACBD7231448E89B8228A3CD998969ECA827BFC9EB0E30BCCC492C16B8CFE9CDA017FB1928AF74BED1DF74830D45E8DFDFA2C6BB9B979223ECECD6A35E0BCCE5D03D6926DF5F5E5D5B712F6EAEDF337B223735375B6DEEE96E3D585596E86BA5644FF4BEA1488B7B5B721E950D71379E5D1567781FD4089CFF70F39FA32E27DEA829A256B9BEE7994F294B92B5EA61E8F1B9DBE74300398E58377CCDFBC90AF5
	9A52597C267464EF5AFDD5B7FBCE713B2FEF0F591FD9DA90F7DFDA679A5FC268A798AF8C0C0C307E95F34455837E79FDAD173EE31341C770EB9E23FD510FF7D5F5F8B8A69BBE7D957FBE0356917328B185B9E1F60F86307E54E1A3E61FEB7FC20D32F3E5D95FFC670A99D150A9C258B0ADBDF1A7E4312A8C60FA7782BD9770B82090D08CB8F0F7FED98D2089608104GB6C086A087309A309ED0GA83ADB44767E7335A4F53E6697A89A601A24E008F3BEB6DCA0067D7A4ED727DA83B244B9088776BD47A70D90AB
	DFA77CECFC7BBF09B52F8F2436EB5A407CF63DFB40BA07FCE4E0F68E499C304EA19F9FB067906ED38318BFC7C0AF7D46389C03F65E20BDC857C4DA1F9254C3867DCB14AF24374B382120BFC4791A2034FECE7B0609F61E539E99C05F8411214C91F39F9DC07F9BD642E156DECF47A891F0D5C39FCFD09E1528E4627B87868D79E67F4BD8775286F8667E02EC0FE9646559B92F79C3BFFCAA77226B001D58BBC8D9087256C136CF201013782FBE9F854F2D0BDDA1B728DDD9CBA3FB0D15BAA8DB8563A71C25BFFD20
	54CE3641B24CCC9DE49B6A9AD653582A25E0A63A66DE6EF6883BCE7E48DE69B874106C50A1EA9D6A30A3C6ABADA75D9823454E07FF95301B674A5A8F4CB9EE6B6D15A96FBA775E43F2DB76BD099C477CBEF3977AC481500B6F91B1D43873689C7E6F9D1342E128C6AFB7C702988BADD33A1CB82B6F5F056B4744985E496F8C3B106FD1531C7F82D0CB878833C1D78E7D8FGG4CAAGGD0CB818294G94G88G88G9AF954AC33C1D78E7D8FGG4CAAGG8CGGGGGGGGGGGGGGGGGE2F5E9
	ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB790GGGG
**end of data**/
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200 );
}


/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSelectLabel() {
	if (ivjSelectLabel == null) {
		try {
			ivjSelectLabel = new javax.swing.JLabel();
			ivjSelectLabel.setName("SelectLabel");
			ivjSelectLabel.setText("Select the type of switch:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectLabel;
}


/**
 * Return the SwitchList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getSwitchList() {
	if (ivjSwitchList == null) {
		try {
			ivjSwitchList = new javax.swing.JList();
			ivjSwitchList.setName("SwitchList");
			//ivjSwitchList.setPreferredSize(new java.awt.Dimension(300, 400));
			ivjSwitchList.setBounds(0, 0, 300, 122);
			// user code begin {1}

			ivjSwitchList.setFont( new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12 ) );
			ivjSwitchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSwitchList;
}


/**
 * Return the SwitchListScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getSwitchListScrollPane() {
	if (ivjSwitchListScrollPane == null) {
		try {
			ivjSwitchListScrollPane = new javax.swing.JScrollPane();
			ivjSwitchListScrollPane.setName("SwitchListScrollPane");
			ivjSwitchListScrollPane.setPreferredSize(new java.awt.Dimension(300, 153));
			ivjSwitchListScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			getSwitchListScrollPane().setViewportView(getSwitchList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSwitchListScrollPane;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getTypeOfSwitchSelected() {
	
	//normally we cannot show SA protocol groups, this checks the 
	//specific property.
	if((SHOW_PROTOCOL & ClientRights.SHOW_ADDITIONAL_PROTOCOLS) != 0)
		return SwitchTypePanel.VALUE_LIST_SA[getSwitchList().getSelectedIndex()];
	return SwitchTypePanel.VALUE_LIST[getSwitchList().getSelectedIndex()];
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public String getTypeOfSwitchSelectedString() 
{	
	//normally we cannot show SA protocol groups, this checks the 
	//specific property.
	if((SHOW_PROTOCOL & ClientRights.SHOW_ADDITIONAL_PROTOCOLS) != 0)
		return com.cannontech.database.data.pao.PAOGroups.getPAOTypeString(
			SwitchTypePanel.VALUE_LIST_SA[getSwitchList().getSelectedIndex()]);
		
	return com.cannontech.database.data.pao.PAOGroups.getPAOTypeString(
		SwitchTypePanel.VALUE_LIST[getSwitchList().getSelectedIndex()]);
}


/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	return LMFactory.createLoadManagement( getTypeOfSwitchSelected() );
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
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SwitchTypePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsSelectLabel = new java.awt.GridBagConstraints();
		constraintsSelectLabel.gridx = 1; constraintsSelectLabel.gridy = 1;
		constraintsSelectLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSelectLabel.ipady = 1;
		constraintsSelectLabel.insets = new java.awt.Insets(13, 25, 2, 183);
		add(getSelectLabel(), constraintsSelectLabel);

		java.awt.GridBagConstraints constraintsSwitchListScrollPane = new java.awt.GridBagConstraints();
		constraintsSwitchListScrollPane.gridx = 1; constraintsSwitchListScrollPane.gridy = 2;
		constraintsSwitchListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsSwitchListScrollPane.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSwitchListScrollPane.weightx = 1.0;
		constraintsSwitchListScrollPane.weighty = 1.0;
		constraintsSwitchListScrollPane.ipadx = 278;
		constraintsSwitchListScrollPane.ipady = 131;
		constraintsSwitchListScrollPane.insets = new java.awt.Insets(2, 25, 15, 25);
		add(getSwitchListScrollPane(), constraintsSwitchListScrollPane);
		initConnections();
		connEtoM1();
		connEtoM2();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}


/**
 * setValue method comment.
 */
public void setValue(Object o) {
	//nothing to set
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getSwitchListScrollPane().requestFocus(); 
        } 
    });    
}

}