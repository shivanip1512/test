package com.cannontech.export;

/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:51:40 PM)
 * @author: 
 */
public class AdvancedOptionsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JButton ivjOkButton = null;
	private javax.swing.JLabel ivjDaysToRetainLabel = null;
	private javax.swing.JLabel ivjRunTimeHourLabel = null;
	private javax.swing.JLabel ivjStartDateLabel = null;
	private javax.swing.JLabel ivjStopDateLabel = null;
	private javax.swing.JPanel ivjAdvancedPanel = null;
	private javax.swing.JTextField ivjDaysToRetainTextBox = null;
	private javax.swing.JPanel ivjOkCancelButtonPanel = null;
	private javax.swing.JTextField ivjRunAtHourTextBox = null;
	private javax.swing.JCheckBox ivjPurgeDataCheckBox = null;
	private javax.swing.JCheckBox ivjAutoEmailCheckBox = null;
	private javax.swing.JComboBox ivjNotificationGroupComboBox = null;
	private javax.swing.JLabel ivjDelimiterLabel = null;
	private javax.swing.JTextField ivjDelimiterTextBox = null;
	private com.cannontech.common.gui.util.DateComboBox ivjStartDateComboBox = null;
	private com.cannontech.common.gui.util.DateComboBox ivjStopDateComboBox = null;
	private javax.swing.JCheckBox ivjHeadingsCheckBox = null;
	private javax.swing.JPanel ivjCSVAdvOptions = null;
	private javax.swing.JPanel ivjDBPurgeAdvOptions = null;
/**
 * DBPurgePanel constructor comment.
 */
public AdvancedOptionsPanel() {
	super();
	initialize();
}
/**
 * DBPurgePanel constructor comment.
 */
public AdvancedOptionsPanel(int format)
{
	super();
	//setFormatType(ExportFormatTypes.getFormatTypeName(format) );
	initialize();
	setPanelsEnabled( format );
}
/**
 * Return the AdvancedPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAdvancedPanel() {
	if (ivjAdvancedPanel == null) {
		try {
			ivjAdvancedPanel = new javax.swing.JPanel();
			ivjAdvancedPanel.setName("AdvancedPanel");
			ivjAdvancedPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDBPurgeAdvOptions = new java.awt.GridBagConstraints();
			constraintsDBPurgeAdvOptions.gridx = 0; constraintsDBPurgeAdvOptions.gridy = 0;
			constraintsDBPurgeAdvOptions.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDBPurgeAdvOptions.weightx = 1.0;
			constraintsDBPurgeAdvOptions.weighty = 1.0;
			constraintsDBPurgeAdvOptions.insets = new java.awt.Insets(15, 15, 10, 15);
			getAdvancedPanel().add(getDBPurgeAdvOptions(), constraintsDBPurgeAdvOptions);

			java.awt.GridBagConstraints constraintsCSVAdvOptions = new java.awt.GridBagConstraints();
			constraintsCSVAdvOptions.gridx = 0; constraintsCSVAdvOptions.gridy = 1;
			constraintsCSVAdvOptions.fill = java.awt.GridBagConstraints.BOTH;
			constraintsCSVAdvOptions.weightx = 1.0;
			constraintsCSVAdvOptions.weighty = 1.0;
			constraintsCSVAdvOptions.insets = new java.awt.Insets(10, 15, 15, 15);
			getAdvancedPanel().add(getCSVAdvOptions(), constraintsCSVAdvOptions);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedPanel;
}
/**
 * Return the AutoEmailCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getAutoEmailCheckBox() {
	if (ivjAutoEmailCheckBox == null) {
		try {
			ivjAutoEmailCheckBox = new javax.swing.JCheckBox();
			ivjAutoEmailCheckBox.setName("AutoEmailCheckBox");
			ivjAutoEmailCheckBox.setText("Auto Email Group:");
			ivjAutoEmailCheckBox.setForeground(new java.awt.Color(102,102,153));
			ivjAutoEmailCheckBox.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAutoEmailCheckBox;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G08DDC1ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D457159809B19693CC2CC54516341A5A162F21AD595AED1A1A067DEA22C9ECBE13582E5D122D3AE60379520D5F1634F47B9CC0D194D1E954CA13B18E863784D150D0C0A3BF6270E3A4B2B2A38C4240BCE0E41899E79EBF7EA45DF36FFD673EB70FB7003A6E37FC5F713DFB4EBD771CFB6E39771EFB5E9D051C77E252B79AF284E1E33AA07FFD34D190CCAD027076FE4386442DBC1F15A9287E8E
	G6C940A065360398EE82685D91959C2E6C78614D3201C7DC8D6669EF85FADE4F7E4FC8AAFA164A4B68942EF9BDE1EB6361CEF7E8D65EC26650162ED704CA2951AC0B6002F09483F37F82F0CB7C3B9CBFEA73051GFA51BADF6F5C2B42474B7556494F04CFC91D1862DC19DE83E5BBC094C06C27CA1BDB28F67716154AE57B0C98E14333078C5B09ACD57B885BAEB05EA279E9943ECD8D11AA2C5B178F4F0A267E5ED35D5E0142EE07476375C8E2F7DF21B862737A2532321A2A26C1FF2F586470086E188BC43F67
	BAB3A82FA074C1196972336D95322C197DB1C26257760554E5352CC3C63322CB4C942BE1D1B3E97B99EADB42E706F298G53B3212AE89CBDAB1DC38ECF3768BC6F13DCDECFC0256F4E9254B7855A3BE9A138448EA227845E9B811C966C47498F309F369B0F8949DDF5069DA17A3158A20F1DC569C7641E43E97B280F1CA8A763EC3A1815E9863083048144GAC8658CFFD73B7FF7BEFF8D6F6C9555E01812F2752672BF2BB7D673CCE518D6F5023AEC946DD903B3DFE27A0B07FFB3133C0C79F04159703FDCF7197
	2B007E92FB61D1C112BEB6246B74AD7E25FCC3125C065022740D756B179524DFC640D7GD8G5CG91G31AD582F723262717AD587E36777B85C4D03DDBE07DF2A9AF4391D22DF6E5F8B45E375CF8F985FAFEF190F59E5E1315AAE2B3ED7EF582CE317D58B0F98164876C8D4590549FE27A414EC41BAFABC88182C448FAD1D448FF360FD236C238CFF19627BD4F8B6BF56D23FA56591202D32225F5A32308F0DE9F3046C238D3AFD087A49E943E99B5347E6530E6D8FCE11F5A79C70B1G0BG16833C89306DD2D6
	2671920E6F1432A393745BA65558B25E0F4B5F0127B8A2799DF51E00C4E6EC0B436FF2F4394500A057B117935D3B02784E5179548DB24E0A9E516FA0F39CDAF78C0860C881D62725FCA7296397A955629A90C14000CF40F6AF176F06275B9110AE7A1C8EC9D4EAB0FACFB93165102385DA0486605DF709587CB53A5E1D06F7BBG33555A0F89AE9E4A4B38DDDEAD5F854F86F0EC518D6B262B1B286170DFC379EBAB48DAA28E099E29C674099EA73C84E45A7A0AAD0ADDFA3CDC6737A81E4C11EAD0BC4071EFD76C
	E4F8E8C3EDE524EFBCC9688EC9723B3A86A531D914A41727D7615FF2126892881AFE81C11E6327090FA5C23F1281B2DA31FF8F1D5C2D6ADF23575BBF68331011136D74A1DDE7F321FEC9AB6E152D1DE46F3435221F3EE1C3BF2D593EC0A8F829CE57CF890C34B27F0CEC554EB5A7EDD3111154E13B45F92F85DB2B7E9CD55616744EBB1511BA860D593D290A58FD91683282E083377BF3A70DEC3ED5B982A24E3073FE98B15129F8992B7B742950BE766CA9FD9FDBFECA5F47DEBEB5368F7D4BA9BD9FE234B52748
	5E629D7628FC725FCF6D22F358639129521CC5E0FB7C9B96E28B1747A5D9DD4EDED1EAF6DD97E11C5B08EF898E62DB5B605D58063EB4FC90FD49F47D91E1F107C9F7BF0BDAD0ED98E9E3639B512678925369750F094DC537B8G062A768EFAA4F9EF2673DB464B6B4B2FCF6B57FD6DB47147FE711ADCEF7D69CD82D9C75C03A26FDB1C038416BE3FEBG060ACCD8B99E2CA9D1470331209F099D1360A95833B2D3E9FB07CBF348788747B735E46DE3FEF3FFB551EBAF54AFG2831235F38AA7729662B6CB055DE37
	1B191F0FCBCBD5B699C7716A2088824804EE60FDE92F5A252135487DE2F4310A744B2DDA8B6CF6522FBD8E0C3948DE41F0059477B718D0AF24625EFBAEDB9D43BA83953AE3C8A0464E46AE5E2E5D371E2CA16D2EG7CEA5E7744AA75DAD59988F83BDDF4DB1047E3B60DD1746249EE3A2995768E3A8A87A597D9489D924C975F20C4C3CA0145145F26EB9DE9AB590EB165AFBEA263D0548EE38850590E3ABCD8BD5A17E6D6EF05E70F4B9F00D848A90E20BD67D4933B10BDCAC6E36CF6CCED274509C5A16DB452AE
	7BFA3B56CEE935C40F0500FF026B76271A1DAABBC11C89DBF2AF0CA26A73F4CDAA73ED397C531AF474ED8157466F0D0AFD1637AB6B696A7651EBAB0BF1BEA9A576CAEEE7B1CEBA47DF2278ED1795BC0BFD9229FDC9598834704B38977F0E76EB9D1413AFE33F6CE7362B7A5564F5F9A45E27AD35E4FEFA7D2EDE1787F1D99487D1B6DD3058FE76AC9D630C0E2C4CDCG067B7A899A8BC05956017B0F251460EEC2B90A63BAA8EE9114D7F15C5A0F6EF48FA1B23438A89947E47C710EE5686DD3697213596A0F35E4
	DCC6F8B975ACA9DF13F9A18EAA59C4FD781AF7900C6E6EA4F6CB666B1CG653DG2781AC1DACD6E0F372BB43E4AD6901058D3619E61F58AD189D04774993489B8F7517G2C8348GDA3E8357C857D1B25E38C632190A7ACCB7113560FCFFB521392B86A509440E2486DB03E7142A5760C88753A99E1ECBGD81D02B63DBA09705CG407C53F89C4F2E5B3AE09D6E42FE9D6DA4F1B5DDEB2EB8069C050EE1B82E1E753B1CD50E5EC6EAA53626749D4F1079EFF6611AB20B36998F65E5G2B3BF06F9BB904675458C7
	67880DE1753A676DC44BD1C3CA173C8EF4AB6B41FCB827B2192BCF20CCBB50E7F6935C2E0FF2B47E700CC1908A614968B5C1F432FEFCA02869C3C1F4226F52A07E05C1F4E223A3B2BF6B5B52BC6CDB4A5205027B7152103904D5F21F84275C37327DC2C10255406C7E56613DEA3B0F94860641314A4A6AD53901E768BCA5F3275409B67F1E19585C876529GB184E8DDE79B2E790BF85DDFDEA0F55F04F20688310908F37D50715BBB43165CA65F40AD733135366F84513FD4E4EB6DC60E3FCA71B5AABC7371E86E
	63CDC00B97F18DFE5CCCEC3D37876685C0A30093A09CA02687576429E7C98EE74CF323456BEB0460552DBA52307E3DF662764E76CFEDC6DF0A7F472F8A0B26A4EF4C8C61CB71BD4C07D6484FC5257BA91E754FDCCF7A17568BE38CD083E0877081CC6D457EADBFDB407AF7466912AAA549515DC7F7FC6CDC0BCB9CD650E401E3FDFB3ED45D37E8E8AF2ED7D96793FAC36D1F9F541131D8592B5DBF0FD1FCD22FF67F7C019977CF218F46278FFD555526356D12BEC5662A3ED072BFED263E5F2715EF2778B417D67E
	2FAF207C3DC03B69C2791E93827763E1557BD70F5F69DEB67E3C7961DD15417C62C9F368B359BF19754FE4CB4D7AE7321F1947BE137D5C9C7ACC16E8D61F4918DFBF50C05662C4980B94G6395183FG66AB68531E3A7719CF6B787399B824D5FBE996C7C2791F56076E2B2DFE7C5C0F231E4445DDDE582387C87B086F2DA763A5F9FD9449F06EFAE2A388637AA4D6D5467B6B49BA6D96FBA81A45EDEB68BA6F04FE0DDC41D8EECDA541CD05F2B447B92BB13EDBFC857D7343BA024B6E4F4AAC6AC7DCA945B5C259
	47F1BFB65F29AF91996AF2112A4CE42C3CE3995A78AEDD754EEC905606F619590FF6791D9D6D320877575ACC63E3B7780D9BF1DD94D782E58B473DF6A1144E0477F67BE1F42B63DEC3F3A899E365DD46036038F7D6C3E85F1E57B03EEFBF5AC04F01601316EBBE11613E55C04FA2BDBD81114F213806EC0E2B8BB4F983AEF2F0E531ECCA69A6BABF7CB4599766A6E3331BFE1F125C988B27D40E0E3FF7D7E17C1A60E67177AAF83E89C04EA89583244E0D6A1AB1EC01F1C02A8C9940138B70A739DCB420448ACC0F
	CB8132D6495F0FA0B417B3971CFE301B44F5554DADC1B496EFDF2953465BC44FF01091B500B1790B87756A4481BD8100757B9BBCEEDFB5407A3D811E999EF6AEE37D4EED9F2F5F58E7FA76845E928F6BB7A91BBD5AFEFFC673F86A538643BF5DCB467FFCBF43C5FA086C194D6AD858A26BF5931E0C7EEA279EFD2587477ADFAB61A147BAA7C5F50E206806E0C89574E31DA48F1BBBA972B322FF3F3CC717D7E1FCD7600598410B316F64AABCA7C4FBB1767D8E2D3B824AC9GE93E2C4C7728CE609B027227074B75
	116F2C294D0A6E2FEE43B8AD77DBF3051AD712B79E8C21FB270F699C67D34EB39922EF9353732BF5984F2F73211E95A6222771EAD6E6A9C053D56C53D693566D3B0AF5579CA0F5E3203C88E079D5BC5B7D2C8D6D328EF0C6BF36314F0EF63179310D33F6D2B7924A8B81967B756C726647FAB8E617810BA0E05EF0C7B35927F54EB04C09082ED40F254DF8B6D96947B362E7356AEF32CF6CB8EA88957BE687646F43814526C9CFEDCBE7ED6F25F9BB1A3F8CE03C5FF3112CB98B8398A7D4B612B82145F1C50E6AC9
	886C7553C88110559063B7C348626945753A326A764EB967EF13EF6AAD7331D8B60F66D61F88E84FC705943FA420BD9FFD1F4F1F95C0AB15F0ACDEE8D34F4D6CB9C68372E965C6CBEC8C96CA5A7C5D2A9632CE2D82FCB2C06EA02CC3GE600CEG89E0AAC0B4C0BC40B200C4008DG99C330768194G1C9E42B1B3B79F9EE34C6484858D707C8E16BFE3367CFA1D5A1696E82FEFC85947EF8E05BAAF4451F998B944EC164971CFD2FC1C8A4FECD9C571243C9CE8A54398CF6CB02965C781BEF6D81139F8B8147CC9
	6F5375EA1849594D718FD3FC5A0802E772DF27EB81A917GED79884AFFE554F9E9E2BE3866B6795E355F1E6F7E102EC36EEBDA5FCD207811EBDA5FBDE7C75F8D875A3AEB585F654DFA729B2FB3BBF7DE9FEDEF793B040D6CDD9250A3819682AC86C83C0E7E7738E55CFB82E7C64638A77097876AA55CD0F448115F7361D981502422B139D7AE1278A890349E7D35DA6076AED13D7F286A6E1FC1DF3E2BB258D8AC3CA4707E2F3F18A7041877E072FA58E64EF7DD913BA50B2317733C5992BA767DED4B78312FE055
	BF57ED320EBE57E5DA754FF5BB2D6AF39D433DE3A5FBDEAF2CD5B6DEEFBF47DD62B8A3ED13602CAE275447710728DE84EF9609C04497D03D06A94EA6630E2AF017E45C89AB71A568F21F570F3C65D632B776385CEE794E832D6372C0B84F6DD1C3F5F4799CAA3D6BB80E684D4EDDCF52F84689BEBCF2834FE2FF256708E8A847F15CB79B71FC3662864E5BFFB01179E73A1915D9835089908690FD936989147E8414D78124G64FE86F366B3241BCD217C123427078FD3612365F713CE5DCE2E03A3240C749078AD
	2A77CC7929F7465B4CEDF84804B634FF0676FAA08FED9846F1999F208D67F39B5C773E891E9B201C7DB96C1FG0DG92C0A4C0AC40B200F51FE37D5934FE5A5F33B2CDGB5GCE00F00079G89G2B815242307EEF8EC4CEE08E93F96A72B2D997723E6E7350E768D8D9CFB5AEDB6E0336AE69E3703A34777D09681629A96F1479338436F70662A36B49F61DB15F22D3D7BD3ECC37276C82FFFFD6755EEC3F7B6B357B2E4AE03E78EFB41EB7011F140421FFBEFF897D73F4986E21B3EC215765A81BFEEE714BB67D
	5C62FC5B58F70AE2ED21F20FCC3F92FACE08845DE2394E53ED580F459C77068D7B310C637ECE6FD865CE027ECEC2DC9E2DE706F267A4BC6F3ED4274EFF30B3FE924F95C8D0AF82E0B1400AC94C1F99DFD543681C48258664CB1A44F283E9774093006475D8EE602946DB49099481EF4DBDCACE44F60FB6B760AA25B99147350045FBC114A02EEEF4F4293ED57E64G497DDC982457574C5EC1BFA5B31AD83CDB43EB01412D720E483CD3A41937D2A21CB21151EC99C4AE4D44107B8C55FDE2F7BF61047122F90637
	CD6ABC03D3364154FBB08FF120D60F9E4D69BB4DEAFA1CCCDF49694F97695113B8FDC6191AAECCE6747C491847B9EF42312A184C4658824F3E49EC0CD91D6F67E11D3049ECBC236099BB1965E859F83E7741C4F3BD849200F765E4E5BC13A6EB4773AF6788DF338453856CAA4A0349E85F3A321D523CBE95C9261D741371289C143CE7869CAE37EABC987DC745A4EE7B44AB39FADC5D74624AD93FF7508711F217D7DD6F59C1B266B5225BB56002D0B9E84C371A335434A05D3E513ED32D7728F667FC122D529D49
	0CB6ECA7B43368F012690F2A33F1FC71FD2D1F905BA7CF46F1FE7C3A9EFDDBB8529FEB517A81219BB97D23D6B53DA41C516D1C1EF8D96BE7046E6374F517756407F37A912B1A9EA5373F12535BBB7468C91CDEE35553DF3897572F6EBC1A2B05E1E2391E0CB6D20D79661ACBE379E62A86F220CD82FF127DB17FDE2DEFB6B8087F797CE20F68770BCEF61DCEF05FCB3FF350353A845ECF83B0DDCA6D0AAE69E36A12BE0EAEF123F41955C96204814708EBE0F000EA427478B6DD771F0077A55CC695B6DA57655154
	35507DE0833C8BD3B0AF17D30F77218BG671C02F7AE520D640C234D1B95320D232C6C0C545DA7BA2B64EDC44E21596BF90EF38A6672265176D7C0B989A06DBE14BB29814F4A3900335C077940E79A300D087B300D5F523A89D0DE8530613EDB4D8746348B5C66EF75043853C0E7B75321A88FF3015B2622DE36DA4455CCC59B6565676A37C5D611DA1768F6B2DE33997324B7F9FB69C791974F5BBB5532DF3FBD3E3AB15EA5E5041744A0C9D3511E43A69CC7537DB01F6FC7B9BFB621BDE75E0F76DCCF6BAE0372
	EA00147B310DDF65611864CC037DE39A367141A1ECA3FC9A36F171904D6BC1F985C05234DB9D1327AF8B5CEF369CC45B26FD81E5A6DDC7DC85472DED41FECBDFC01B753AC3598CD7E4466BE94576E2F9FBFBBAF08CD6F35CFB9DD8AFB782E5CC2EBFAACC7C2E23FAAFE0ED76D871AE27A582654C6CC0DC84475D30226C04887DEF814CF6EBBB1477F79D82DFA36E313D8BCF0FB8EC44EBD205B59085F948E57C7C0F0A8B0C470E9F466F85B1436AFCEA695693A16FC326C43098A1FB3A722D0349BE582D68D16BD4
	7478730474E0FCAED161FB3AE3A2FC6C8C71677A71F2FA666941E7B44617BFAE5F3B1A32FC4E9E17CF8BACD778DFF47DF3CE576690B753B54DB7DD1BC30C2C47B9F893E8095379B93A6176F21FDF3CCD3E67EF190F752B3F115EFBFAC05BDF09621B9E5076778FFC9F3083ED7183585F63F9FA72CD8F32B1ADFDF074791B7135576A66DA657A1DA1794E1911CFD25519AA3FC707645BFA947962D4F592647715A1791EABA3FCC99A3A90494AB972B3BFD21BD3784CF4FBFE7F05F179747DBEE1DCBEFDBFF0503D24
	A2D26B87D7A85E9C29750337CC68879620AD0AC4BFD8236B87637B6FDB3749D7F068767A7B8C5D8FF3E7E87B7BA245674F5076376D90763784E8D1B3303FD9877564274C10576007C27953EF2E6B4E9739FEE9C83E57DBF4670BDC3F931E9240540734FE98E89D4BCE51D0BF6EA1BDBF4C6DC03E8495FD257C1E94D2CFD3071EBC61E1F93EBC9C0A2F5FBA161E79A179E66B4AC32888499726AB2FC92E6F5461E35F975E69A17B7C8850E7BE0C3929EE47DF05505F971B7CDE1F68172EB1AC7DD5097097016C250B
	F713F30B4B63F6F944EA760BBF2EDFDA03405F372B5EE7555F7D3C5FE37F87B22EB6AAFD3A2EFACFED3C7B32F36F2A8C1653EB4C43FCE07C4318677BCE9E3FF347F1D2AD564B1E093817EA31DEAE47B96959448D65299C776A904D6DC3B91663EA07797DB50EBBF094F9B33E0838BD05481B4BF1A305481B4FF12B6999E6844A919C77E1B94145C0B90E639EAAC75EA7B86ED1A37DAD5B2C2C4C02D938EE7666A9E36DD33D3F9D727B487F5E386F4A3B7BB2DEB82474E9256A5DF1686E4B3EFED7E548396A03682F
	27E761B8CFB908E35F48F199B730DEB44739EEE03D059CE720E729E4A8E7FC89F13DD62C1743F1CF527C09934AB7B96E1F69F702C8A84F6738E92D5C7FB96E7E4BD8AFA38AF1459DB8A7F2B94E5202F32208639EEF4176AAB82E1C72C6C0B91A63E2BAF85FB86E9E5E0FE40E0B63754C339177A5AB369745F1491CF77EEC5CE7767710F1605FA558D6A2FF33F83F07646348EFCADD8E376BBA4D24321BC86C7CF69F7944AD673C16645DCA2EFF83485FA06BC07099F3149809357D4DBE256D245ADB49BBEB5B4E19
	234D275DB0074E6D4D24EB2291F88A66E0CEADF9A8B86F781CCB512DFDF83C3C23DABF5B9C269B5E4FF32BF4E3726B693ABA827870B918DBAC69D764BDD8F8ABF94EB80D3C04D13678BDDD0B83A279DD2B68E4BA6CAA477C44F2EE031A72E09BD85D0ACE5FA8C71D92670C0EF170F78CFCFC08CEF3D4E3BFD72B530F0CA4775167753B2EFBBD1243DD69F675FA486DF959C62F98B1A715B397EDF4FDC051E75E41DB71459AE803G6AE31B2B7545059E256D309B37620B5A361D237A3A4F9AEC5725DEC55667D614
	651B2BEF57ADAAD961D0A7DCA5ABEA142C17BA696FE63DFE31576F9D74B851675653F5F0BE540F1B0B637D9F17487AAFFA48C59427DCEFCBAB5AFD55DC7CA674C8AB6A78260E0E299A9D5322190EF87FF7EF34567F3FFB99E5180046F0F63A3E15C239A69A477B8D1FE227E5AD5CCE517ABA287DAF9C6AC4C5ABF3A2B6DAEB278B9D41E332532F487AFE874ADA24A3CB6B6B4BE4D9B8A609AAD92C3D59FC0C13C2682E76A9E19EEB8F6D17B3CF5B5ECF39FE46F97A6D2921C4531EF91E5677CB82CA5FF75830ED0B
	CE5B04E7332A6DA8CD5F7BD4ED074EEFDAA5657D51C1657D79E1010F4F4BC3E3648A0F9795980F959D56130179670DD5CA3BC555CA3B5B8A6F2C5D4BB5CADB0759FA699F94D9FE779765185F15660D155F1DF4C6E9638D6381F82ABF41B413FF71E7C301426AB31FD84EDC98531E73EB1577EDE6724E74F90C671BE3475467C81D727ED23D225B72CB6FE9EDD56DF5FB7D79C70B0E95D0B8DEFCD82EEBECA5F2FA9C6E0048653F48F3722BF56523CD139B94194FDCD63743687B4FA9743A16090C9F137F07961E6B
	7F72C43FDF204C3ACF94BB2E635F8CAC3A6DA00F7B3C4273AC37FDC488590C6741A605A7194BC99813673B1FAABC27ED481372E5DCB7FFC16F4733587ED7656AFCC549D7A0FE7C4A583FA9E2FC71C1F98EBB7078A64477CAD05EA89CF822A644E78812978BBC0BA74437BEA80F3382F856CD086F05A0F9A940139DB3913EBF8449B381CF6904784E5854FC0D4053B98E9F0B677F132EA9D28CDBD7996E7794979104DBEA9DDDEF39F5F43DB7DAC757FB2BF5F43DFDBA6D3D2B53DEDA6768FADBBAC757DB2B535E6B
	BA6D65683437CF27BD3BEDF4BD07CDDD0F6D518E1A07154FB774EAAB43670EECD770F835D5E07FA762FAE9B3AC6C316B057400DF58415719BAFAC1B5F12838AC7557DFCBB588E9F542A6F6E137CEC8A337EE6B04F4FAA937CEC8153C3E919A3D287F82D7054F35B8680330097D94CA928C679BF89D92466DC060589DAA589A54DE0E8C82683CEB745AD879404FDF096D2849B18886D03BCFE4FA268F930BD0CDC91A32AEC40B841B91D40717A4E18739F0E5143CC6F6652ACC92F6DF9874989D1211DC32B26697FD
	F82C78B0E08D45C5123031C4923652DBD6C6F24DCA927652ABB624A09A7387860EB81DC72E419FE158C3EE5768D2F612FBC1C6FAB148C82F7DGEE63CDE8195F6581448E582B8687BCC63C07437BC6BFBA4B4F74A0ECFA90B6C36E7F31BB2CF3B730FEA1ABF8CF5E38E04F646C870B878DC2C62D308976CFDF1F8346177EC7B40DC2E697F5CC518FF5A5A13B11709E815EA314373CD1B0F487A4E13F026FA6F8C6ED76B93AC5283245697077539608CA875B04ADBD7E5EAEAAG86372D368D445292A167B58ADB25
	BE726BE792E243D8AACDB7115F4A4804F2B6C97E5853B35BC77F7E7F19DA7E1EFE786F6A6F6949776E8979BD3DEF81DB57EFAE507EBEE99B7D86308870AB96E0EEE6F878C3A174EF8D1A879CEEB71C0E7BABBDCE723B032AEB12285CC75DC973BA812CA39399EDEF8F517B0A0A67FFGD0CB878803379A0A1E9BGGCCD6GGD0CB818294G94G88G88G08DDC1AD03379A0A1E9BGGCCD6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAG
	GG589BGGGG
**end of data**/
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelButton() {
	if (ivjCancelButton == null) {
		try {
			ivjCancelButton = new javax.swing.JButton();
			ivjCancelButton.setName("CancelButton");
			ivjCancelButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelButton;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCSVAdvOptions() {
	if (ivjCSVAdvOptions == null) {
		try {
			ivjCSVAdvOptions = new javax.swing.JPanel();
			ivjCSVAdvOptions.setName("CSVAdvOptions");
			ivjCSVAdvOptions.setBorder(new javax.swing.border.EtchedBorder());
			ivjCSVAdvOptions.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsStartDateLabel = new java.awt.GridBagConstraints();
			constraintsStartDateLabel.gridx = 0; constraintsStartDateLabel.gridy = 0;
			constraintsStartDateLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsStartDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getStartDateLabel(), constraintsStartDateLabel);

			java.awt.GridBagConstraints constraintsStopDateLabel = new java.awt.GridBagConstraints();
			constraintsStopDateLabel.gridx = 0; constraintsStopDateLabel.gridy = 1;
			constraintsStopDateLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsStopDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getStopDateLabel(), constraintsStopDateLabel);

			java.awt.GridBagConstraints constraintsAutoEmailCheckBox = new java.awt.GridBagConstraints();
			constraintsAutoEmailCheckBox.gridx = 0; constraintsAutoEmailCheckBox.gridy = 4;
			constraintsAutoEmailCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsAutoEmailCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getAutoEmailCheckBox(), constraintsAutoEmailCheckBox);

			java.awt.GridBagConstraints constraintsNotificationGroupComboBox = new java.awt.GridBagConstraints();
			constraintsNotificationGroupComboBox.gridx = 1; constraintsNotificationGroupComboBox.gridy = 4;
			constraintsNotificationGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNotificationGroupComboBox.weightx = 1.0;
			constraintsNotificationGroupComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getNotificationGroupComboBox(), constraintsNotificationGroupComboBox);

			java.awt.GridBagConstraints constraintsDelimiterTextBox = new java.awt.GridBagConstraints();
			constraintsDelimiterTextBox.gridx = 1; constraintsDelimiterTextBox.gridy = 2;
			constraintsDelimiterTextBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDelimiterTextBox.weightx = 1.0;
			constraintsDelimiterTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getDelimiterTextBox(), constraintsDelimiterTextBox);

			java.awt.GridBagConstraints constraintsDelimiterLabel = new java.awt.GridBagConstraints();
			constraintsDelimiterLabel.gridx = 0; constraintsDelimiterLabel.gridy = 2;
			constraintsDelimiterLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsDelimiterLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getDelimiterLabel(), constraintsDelimiterLabel);

			java.awt.GridBagConstraints constraintsStartDateComboBox = new java.awt.GridBagConstraints();
			constraintsStartDateComboBox.gridx = 1; constraintsStartDateComboBox.gridy = 0;
			constraintsStartDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDateComboBox.weightx = 1.0;
			constraintsStartDateComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getStartDateComboBox(), constraintsStartDateComboBox);

			java.awt.GridBagConstraints constraintsStopDateComboBox = new java.awt.GridBagConstraints();
			constraintsStopDateComboBox.gridx = 1; constraintsStopDateComboBox.gridy = 1;
			constraintsStopDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStopDateComboBox.weightx = 1.0;
			constraintsStopDateComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getStopDateComboBox(), constraintsStopDateComboBox);

			java.awt.GridBagConstraints constraintsHeadingsCheckBox = new java.awt.GridBagConstraints();
			constraintsHeadingsCheckBox.gridx = 0; constraintsHeadingsCheckBox.gridy = 3;
			constraintsHeadingsCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsHeadingsCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getHeadingsCheckBox(), constraintsHeadingsCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCSVAdvOptions;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDaysToRetainLabel() {
	if (ivjDaysToRetainLabel == null) {
		try {
			ivjDaysToRetainLabel = new javax.swing.JLabel();
			ivjDaysToRetainLabel.setName("DaysToRetainLabel");
			ivjDaysToRetainLabel.setText("Days to Retain:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDaysToRetainLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getDaysToRetainTextBox() {
	if (ivjDaysToRetainTextBox == null) {
		try {
			ivjDaysToRetainTextBox = new javax.swing.JTextField();
			ivjDaysToRetainTextBox.setName("DaysToRetainTextBox");
			ivjDaysToRetainTextBox.setPreferredSize(new java.awt.Dimension(50, 20));
			ivjDaysToRetainTextBox.setText("30");
			// user code begin {1}
			ivjDaysToRetainTextBox.setText("90");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDaysToRetainTextBox;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDBPurgeAdvOptions() {
	if (ivjDBPurgeAdvOptions == null) {
		try {
			ivjDBPurgeAdvOptions = new javax.swing.JPanel();
			ivjDBPurgeAdvOptions.setName("DBPurgeAdvOptions");
			ivjDBPurgeAdvOptions.setBorder(new javax.swing.border.EtchedBorder());
			ivjDBPurgeAdvOptions.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDaysToRetainLabel = new java.awt.GridBagConstraints();
			constraintsDaysToRetainLabel.gridx = 0; constraintsDaysToRetainLabel.gridy = 0;
			constraintsDaysToRetainLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsDaysToRetainLabel.insets = new java.awt.Insets(5, 60, 5, 5);
			getDBPurgeAdvOptions().add(getDaysToRetainLabel(), constraintsDaysToRetainLabel);

			java.awt.GridBagConstraints constraintsRunTimeHourLabel = new java.awt.GridBagConstraints();
			constraintsRunTimeHourLabel.gridx = 0; constraintsRunTimeHourLabel.gridy = 1;
			constraintsRunTimeHourLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsRunTimeHourLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getDBPurgeAdvOptions().add(getRunTimeHourLabel(), constraintsRunTimeHourLabel);

			java.awt.GridBagConstraints constraintsDaysToRetainTextBox = new java.awt.GridBagConstraints();
			constraintsDaysToRetainTextBox.gridx = 1; constraintsDaysToRetainTextBox.gridy = 0;
			constraintsDaysToRetainTextBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDaysToRetainTextBox.weightx = 1.0;
			constraintsDaysToRetainTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getDBPurgeAdvOptions().add(getDaysToRetainTextBox(), constraintsDaysToRetainTextBox);

			java.awt.GridBagConstraints constraintsRunAtHourTextBox = new java.awt.GridBagConstraints();
			constraintsRunAtHourTextBox.gridx = 1; constraintsRunAtHourTextBox.gridy = 1;
			constraintsRunAtHourTextBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRunAtHourTextBox.weightx = 1.0;
			constraintsRunAtHourTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getDBPurgeAdvOptions().add(getRunAtHourTextBox(), constraintsRunAtHourTextBox);

			java.awt.GridBagConstraints constraintsPurgeDataCheckBox = new java.awt.GridBagConstraints();
			constraintsPurgeDataCheckBox.gridx = 0; constraintsPurgeDataCheckBox.gridy = 2;
			constraintsPurgeDataCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsPurgeDataCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getDBPurgeAdvOptions().add(getPurgeDataCheckBox(), constraintsPurgeDataCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDBPurgeAdvOptions;
}
/**
 * Return the DelimiterLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDelimiterLabel() {
	if (ivjDelimiterLabel == null) {
		try {
			ivjDelimiterLabel = new javax.swing.JLabel();
			ivjDelimiterLabel.setName("DelimiterLabel");
			ivjDelimiterLabel.setText("Delimiter:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDelimiterLabel;
}
/**
 * Return the DelimiterTextBox property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getDelimiterTextBox() {
	if (ivjDelimiterTextBox == null) {
		try {
			ivjDelimiterTextBox = new javax.swing.JTextField();
			ivjDelimiterTextBox.setName("DelimiterTextBox");
			ivjDelimiterTextBox.setPreferredSize(new java.awt.Dimension(50, 20));
			ivjDelimiterTextBox.setText("|");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDelimiterTextBox;
}
/**
 * Return the HeadingsCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getHeadingsCheckBox() {
	if (ivjHeadingsCheckBox == null) {
		try {
			ivjHeadingsCheckBox = new javax.swing.JCheckBox();
			ivjHeadingsCheckBox.setName("HeadingsCheckBox");
			ivjHeadingsCheckBox.setText("Column Headings");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHeadingsCheckBox;
}
/**
 * Return the NotificationGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JComboBox getNotificationGroupComboBox() {
	if (ivjNotificationGroupComboBox == null) {
		try {
			ivjNotificationGroupComboBox = new javax.swing.JComboBox();
			ivjNotificationGroupComboBox.setName("NotificationGroupComboBox");
			ivjNotificationGroupComboBox.setEnabled(false);
			// user code begin {1}
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List notifGroups = cache.getAllNotificationGroups();

				for( int i = 0; i < notifGroups.size(); i++ )
					ivjNotificationGroupComboBox.addItem( notifGroups.get(i) );
			}			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNotificationGroupComboBox;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getOkButton() {
	if (ivjOkButton == null) {
		try {
			ivjOkButton = new javax.swing.JButton();
			ivjOkButton.setName("OkButton");
			ivjOkButton.setPreferredSize(new java.awt.Dimension(73, 25));
			ivjOkButton.setText("OK");
			ivjOkButton.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjOkButton.setMinimumSize(new java.awt.Dimension(73, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkButton;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOkCancelButtonPanel() {
	if (ivjOkCancelButtonPanel == null) {
		try {
			ivjOkCancelButtonPanel = new javax.swing.JPanel();
			ivjOkCancelButtonPanel.setName("OkCancelButtonPanel");
			ivjOkCancelButtonPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
			constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 1;
			constraintsCancelButton.insets = new java.awt.Insets(0, 20, 0, 20);
			getOkCancelButtonPanel().add(getCancelButton(), constraintsCancelButton);

			java.awt.GridBagConstraints constraintsOkButton = new java.awt.GridBagConstraints();
			constraintsOkButton.gridx = 2; constraintsOkButton.gridy = 1;
			constraintsOkButton.insets = new java.awt.Insets(0, 20, 0, 20);
			getOkCancelButtonPanel().add(getOkButton(), constraintsOkButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkCancelButtonPanel;
}
/**
 * Return the PurgeDataCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getPurgeDataCheckBox() {
	if (ivjPurgeDataCheckBox == null) {
		try {
			ivjPurgeDataCheckBox = new javax.swing.JCheckBox();
			ivjPurgeDataCheckBox.setName("PurgeDataCheckBox");
			ivjPurgeDataCheckBox.setSelected(true);
			ivjPurgeDataCheckBox.setText("Purge Data");
			ivjPurgeDataCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPurgeDataCheckBox;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getRunAtHourTextBox() {
	if (ivjRunAtHourTextBox == null) {
		try {
			ivjRunAtHourTextBox = new javax.swing.JTextField();
			ivjRunAtHourTextBox.setName("RunAtHourTextBox");
			ivjRunAtHourTextBox.setPreferredSize(new java.awt.Dimension(50, 20));
			ivjRunAtHourTextBox.setText("4");
			// user code begin {1}
			ivjRunAtHourTextBox.setText("1");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunAtHourTextBox;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRunTimeHourLabel() {
	if (ivjRunTimeHourLabel == null) {
		try {
			ivjRunTimeHourLabel = new javax.swing.JLabel();
			ivjRunTimeHourLabel.setName("RunTimeHourLabel");
			ivjRunTimeHourLabel.setText("Run at Hour (0-23):");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunTimeHourLabel;
}
/**
 * Return the StartDateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.cannontech.common.gui.util.DateComboBox getStartDateComboBox() {
	if (ivjStartDateComboBox == null) {
		try {
			ivjStartDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjStartDateComboBox.setName("StartDateComboBox");
			// user code begin {1}
			ivjStartDateComboBox.setSelectedDate(com.cannontech.util.ServletUtil.getYesterday());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDateComboBox;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStartDateLabel() {
	if (ivjStartDateLabel == null) {
		try {
			ivjStartDateLabel = new javax.swing.JLabel();
			ivjStartDateLabel.setName("StartDateLabel");
			ivjStartDateLabel.setText("Start Date (mm/dd/yyyy):");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDateLabel;
}
/**
 * Return the StopDateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.cannontech.common.gui.util.DateComboBox getStopDateComboBox() {
	if (ivjStopDateComboBox == null) {
		try {
			ivjStopDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjStopDateComboBox.setName("StopDateComboBox");
			// user code begin {1}
			ivjStopDateComboBox.setSelectedDate(com.cannontech.util.ServletUtil.getToday());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopDateComboBox;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStopDateLabel() {
	if (ivjStopDateLabel == null) {
		try {
			ivjStopDateLabel = new javax.swing.JLabel();
			ivjStopDateLabel.setName("StopDateLabel");
			ivjStopDateLabel.setText("Stop Date (mm/dd/yyyy):");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStopDateLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 4:48:50 PM)
 * @return java.lang.Object
 */
public Object getValue(Object object) {
	return null;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AdvancedOptionsFrame");
		setLayout(new java.awt.GridBagLayout());
		setSize(357, 356);

		java.awt.GridBagConstraints constraintsAdvancedPanel = new java.awt.GridBagConstraints();
		constraintsAdvancedPanel.gridx = 0; constraintsAdvancedPanel.gridy = 0;
		constraintsAdvancedPanel.fill = java.awt.GridBagConstraints.BOTH;
		add(getAdvancedPanel(), constraintsAdvancedPanel);

		java.awt.GridBagConstraints constraintsOkCancelButtonPanel = new java.awt.GridBagConstraints();
		constraintsOkCancelButtonPanel.gridx = 0; constraintsOkCancelButtonPanel.gridy = 1;
constraintsOkCancelButtonPanel.gridheight = 2;
		constraintsOkCancelButtonPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsOkCancelButtonPanel.weightx = 1.0;
		constraintsOkCancelButtonPanel.weighty = 1.0;
		add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args)
{
	try 
	{
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AdvancedOptionsPanel optionsPanel;
		optionsPanel = new AdvancedOptionsPanel();

		frame.setContentPane(optionsPanel);
		frame.setSize(optionsPanel.getSize());
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});

		//set the app to start as close to the center as you can....
		//  only works with small gui interfaces.
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((int)(d.width * .3),(int)(d.height * .2));
		
		frame.show();
		
		java.awt.Insets insets = optionsPanel.getInsets();
		//optionsPanel.setSize(optionsPanel.getWidth() + insets.left + insets.right, optionsPanel.getHeight() + insets.top + insets.bottom);
		//optionsPanel.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
public void setPanelsEnabled(int format )
{
	boolean value = true;
	switch(format)
	{
		case ExportFormatTypes.DBPURGE_FORMAT:
		{
			getDBPurgeAdvOptions().setVisible(value);
			getCSVAdvOptions().setVisible(!value);
			
			getDaysToRetainLabel().setEnabled(value);
			getRunTimeHourLabel().setEnabled(value);
			getRunAtHourTextBox().setEnabled(value);
			getDaysToRetainTextBox().setEnabled(value);
			getPurgeDataCheckBox().setEnabled(value);

			getStartDateLabel().setEnabled(!value);
			getStartDateComboBox().setEnabled(!value);
			getStopDateLabel().setEnabled(!value);
			getStopDateComboBox().setEnabled(!value);
			getDelimiterLabel().setEnabled(!value);
			getDelimiterTextBox().setEnabled(!value);
			//getNotificationGroupComboBox().setEnabled(!value);
			//getAutoEmailCheckBox().setEnabled(!value);

			getDaysToRetainTextBox().requestFocus();

			return;
		}
		case ExportFormatTypes.CSVBILLING_FORMAT:
		{
			getDBPurgeAdvOptions().setVisible(!value);
			getCSVAdvOptions().setVisible(value);
			
			getDaysToRetainLabel().setEnabled(!value);
			getRunTimeHourLabel().setEnabled(!value);
			getDaysToRetainTextBox().setEnabled(!value);
			getRunAtHourTextBox().setEnabled(!value);
			getPurgeDataCheckBox().setEnabled(!value);
			
			getStartDateLabel().setEnabled(value);
			getStartDateComboBox().setEnabled(value);
			getStopDateLabel().setEnabled(value);
			getStopDateComboBox().setEnabled(value);
			getDelimiterLabel().setEnabled(value);
			getDelimiterTextBox().setEnabled(value);
			//getNotificationGroupComboBox().setEnabled(value);
			//getAutoEmailCheckBox().setEnabled(value);

			getStartDateComboBox().requestFocus();
			return;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 4:47:40 PM)
 * @param object java.lang.Object
 */
public void setValue(Object object) {}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 4:52:55 PM)
 * @param parent javax.swing.JFrame
 */
public void showAdvancedOptions(javax.swing.JFrame parent)
{
	javax.swing.JDialog dialog = new javax.swing.JDialog(parent);
	dialog.setTitle("Advanced Export Options");
	
	class DialogButtonListener implements java.awt.event.ActionListener
	{
		javax.swing.JDialog dialog;

		public DialogButtonListener(javax.swing.JDialog d)
		{
			dialog = d;
		}
		
		public void actionPerformed(java.awt.event.ActionEvent event )
		{
			if( event.getSource() == getOkButton() )
			{
				com.cannontech.clientutils.CTILogger.info("OK button");
			}
			else if( event.getSource() == getCancelButton() )
			{
				com.cannontech.clientutils.CTILogger.info("Cancel Button");
			}

			dialog.setVisible(false);
			dialog.dispose();
		}
	}
		
	java.awt.event.ActionListener listener = new DialogButtonListener(dialog);
		
	getOkButton().addActionListener(listener);
	getCancelButton().addActionListener(listener);
	
	java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	dialog.setLocation((int)(d.width * .3),(int)(d.height * .2));

	dialog.setModal(true);	
	dialog.getContentPane().add(this);
	dialog.setSize(360, 270);
	dialog.show();

	getOkButton().removeActionListener(listener);
	getCancelButton().removeActionListener(listener);
		
	//if( getButtonPushed() == this.OK )
		//return (com.cannontech.data.graph.GraphDefinition) getValue(null);
	//else
		//return null;
}
}
