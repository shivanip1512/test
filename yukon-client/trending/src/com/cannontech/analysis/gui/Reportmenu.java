package com.cannontech.analysis.gui;

/**
 * Insert the type's description here.
 * Creation date: (2/5/2004 9:03:09 AM)
 * @author: 
 */
public class Reportmenu extends javax.swing.JMenu {
	private javax.swing.JMenu ivjAMRMenu = null;
	private javax.swing.JMenuItem ivjConnectedMenuItem = null;
	private javax.swing.JMenuItem ivjControlLogMenuItem = null;
	private javax.swing.JMenuItem ivjCurrentMonthMenuItem = null;
	private javax.swing.JMenu ivjDatabaseMenu = null;
	private javax.swing.JMenuItem ivjDisconnectedMenuItem = null;
	private javax.swing.JMenu ivjDisconnectMenu = null;
	private javax.swing.JMenuItem ivjHistoryMenuItem = null;
	private javax.swing.JMenuItem ivjLastMonthMenuItem = null;
	private javax.swing.JMenuItem ivjLoadGroupAcctingMenuItem = null;
	private javax.swing.JMenu ivjLoadManagementMenu = null;
	private javax.swing.JMenuItem ivjMissedMeterMenuItem = null;
	private javax.swing.JMenuItem ivjPowerFailMenuItem = null;
	private javax.swing.JMenu ivjStatisticsMenu = null;
	private javax.swing.JMenuItem ivjSuccessMenuItem = null;
	private javax.swing.JMenuItem ivjTodayMenuItem = null;
	private javax.swing.JMenuItem ivjYesterdayMenuItem = null;
/**
 * Reportmenu constructor comment.
 */
public Reportmenu() {
	super();
	initialize();
}
/**
 * Reportmenu constructor comment.
 * @param s java.lang.String
 */
public Reportmenu(String s) {
	super(s);
}
/**
 * Reportmenu constructor comment.
 * @param s java.lang.String
 * @param b boolean
 */
public Reportmenu(String s, boolean b) {
	super(s, b);
}
/**
 * Return the AMRMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenu getAMRMenu() {
	if (ivjAMRMenu == null) {
		try {
			ivjAMRMenu = new javax.swing.JMenu();
			ivjAMRMenu.setName("AMRMenu");
			ivjAMRMenu.setText("AMR");
			ivjAMRMenu.setActionCommand("AMR");
			ivjAMRMenu.add(getMissedMeterMenuItem());
			ivjAMRMenu.add(getSuccessMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAMRMenu;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G19CEC5B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8DF094471546A03B449D17784EBA875BA491B1A9ABBE12F031FCC52538C44E11080AC90C93B945DD49A96E425D9197582AAB652C4B6172C85AC57FFFC6C2C2ACC83601E087E3B0322D1331430F8C82CBC111D6E28592526C6EC8DAE9F7E735BBAB2D7E503D6E1E57B3BBBBC2600A28FAF477775E6B577D3A2767751B15D0D917E84D4E299404EC2B207E7BB0DB900A1B84618735AB1B91BBF6B555A2
	687E3D8ED4A11C7910000D406B0DCF35148BC9C773215D8AED653ED4CBB554778A5FBCEA7B90AA7358497CC0903E337A4A3337376337231DBC5AFEE134984AD220F6E28BA865AA311F632DD1711550AED56B0432F384415DCEE41C95B5BA5C524E649A21EC854A20B2873DB6153F965AC4E6A35056F62D4F82BA3AFB1B9A55F66FDFA78819BFAE4EA9A136F47DA3B57E0969762AE53D10C09D11ABBCB0F3944AB36D23C31F38E45F891768774BFEC5F28D1F88CA81B928B4B5F55052A77943023079CFE4FC719579
	D4D79092206D68A0580FAA09CF4A21FE8C68AC909B6333A5E754B1A40CA6885B9EFDB5FB3EB12E6AE0E3CB695046F8B2EEAAAF50C97A3F68ADD67584E8B7823131FC410BE3C96E4431BCD2CEE4F7C25BE2CF35586CB8160FFF07E3F1CCBFAAE4B95F4DA91BE7AC8DF655DFF6EDAC2D8F16956D276B7F5BAE3206AB013781E88BD0A6D0E5D72A258168G5DF7CF5A5F06721CD339A07BFC327FDCA0F05E6B8E3649EE498B7526A66028D807641203EEC1E0FB2B33723849F810DA3B22771536972E89389756F6BCAC
	A84A119C2B495C121FAB4D99D07BD03A34392D6FA2FE7B240A78AD816AC40F2C93435BA83EDB0733B559CC571C346B3BD3ADAB3A514F0ED29CCB2B658BC27929D3B9F9A6E3D9790323B9D95DEC8C65573431B09F27DDA3BEBE833883A8G948F148434699A7A78D57B6905FCFC9976F250AFFABB434E0098D44E07BDDE3794D4FD5D6A3D1D2F4D0869DD3FEBBD661724537AB5CAFF622429DF52132AF2362B7E582D738B337D403C36ED0E583DD14A57302C022C55C987DB43DC0E6F2778D99D4E56F6B5FD06C83B
	9DF849C0ECDD02BD64EC2A69013D8E548A64860A83CA84CA6E41353952756E9D6E7FF65D3AB05D5C6EFDD0CA91A5A8DE760794516F123A4520C7F4FA2510204A94F793FFB82374AA3A491EF1010DCB12DF8A0A0AC776C36F22CF02CD90E2B2355D95C4A6A8D12E5D63134000AFA0E03FEFF46F0552AB06142E00DBD4A4CD02715F6EA6EBB9AEF6C30F508154AD57093FC6AA49B91486734F68C1DFFD75BA41DA00BFF09D7D72756E57207C8CB62564EDEAEA763848B04460A46E2D66AAB2878564D7GA5822D87CA
	834A3C0E4F570B8E5CC74DA58F89471FFB57F49F51676A865B9347EE9877D1A735538EF8GA881E8BDD0BA10500B6F391FD58E68779158A763A275B435DC211619EA19F85DFC8FAE7CCC6D0A32D5206EBDD2AFD2CB2BDA1668746E5696DB1712DE32378F43BC4F828D74625AEC6B2EE7FB763C9812F017DE8D4286135C5ACAB159672F11F707B4AE7915E6A9A0795DD08929FBE47B3582ED7F8E4AFCEF3DC0F1F28EDD008D9662F846358A06C39F7A278179D914AFAACA50638CABD22724A89E7F1026AFB848D8C2
	D1C7DCC81DEBD99F19EB8466F8BFD0A21F6BF247DE5DBEEC156551F040CE1EB0F5BFBFCE5F2B49A03F196F072A8A9287E570F69DEDEB7C474A9D2A1F094C5D2E4D1D6D1394EEA3774F5CFFA97767CC4F7CEB3B2C47FCED1777182F6D97FBEE3FF6DF6AB1DBBB467BCA8F59737204DF37979E6DA16707CB767BA53AA1390A406239976DE49E9E3FC73966F18FC9CA27E7CA02F33F0F2C69BD740C4A073A2D8F4F1209B771ACB9BC7537C2CAFF21E95C30726BEF65A8FDECCD637B3433040DE94DF53247A42F84C1A2
	1C77E13F4270476979276221DC0A551CAE0472CA9B0413179549F70A2D63DEE1EE0C2C6F39368E42D131870F10FE1BC5C5F44283C9988C6FE4E71FA7243A012A28BADFAEA47EE915C5F71B68970728E9BD7F0173C437D30163BF24F8DCA146E34F4A791B44DE924CEF83D0DA9FBEABC7EF6C57BDAB6A01F0C176FA5992G4FF61378F7F9D5254EE7BB7B585D0054D96C7FE415BE76F75FE47CD5D02E83E2B24FD71B49EC02B21D4BD8EB4DE4B2214C6FC719F7ECE6B27540EF60B2DD074CE4DA01BF500FF35F5BDB
	CE7627B4961660B5CC8E084F24C9FC17763EE660F50B5E30760EFE23171C5DDE5D5942702ABE737DF2200F1CA7233C7F3A3EBD82F95732BE597EDEA712BE83C10F8F0EE2F2D82A3EFD4AEB3CD711BBD88466318208694A67495E535FE75674935D1515F807A0B18B433ECD318B30A91F4F41782D2DDCFF5FF2075EB0FDD6886D68D76337FEE3DCBB51CF7A358D24DA0E8F200F9F6A531F4567C2A159652161096A2F161B7A6708E1EDB749DC86BD41904438EEA902BE6F3CC946CA629795D67D766BFDEF88317158
	27EE0FA287F9306CA21B194DF576227E5E36212CBA27E81E391E9DE0F39C9850662A40DD09596DA9A477A0FE3F74B8FDA742633E9322D3B9A1017126A6FA94F4FBC2E151CB17D33DE36E2AA2F3A6E7C262G5E6B5E2EA67BE6AB34B30072C53C83D65520EC3D08329D7B092C835A8120F8AE7BF1AD6F174BCE9D207DC2BB83A85F0932120D776BC4592F9D247DC2BBG944FE57F6A906F174B7E380E768B6D8C20FC974A7E5E172F2ECB3D8BE50F7AE93F508EG453B70FC3E761EFE4F25EC5BB76F1ECBF4B17FA7
	3B0CFB2EC0A47636G3E5D05FB6E27B7ABF4FB8E6E2090E68F41934A5F017D396CD9D45BCB7A2D78AC8AF8DEDEF41273AE8B7A2CF443FE86EA86F23B51C65E40027733CFA337391F7DD71559DDA74E4D6637D2AD573A35D8035DE93EC277C90A1B5DE932B97E9845B76BF0F657B9D90D39052D40BBA3E1BCD3DBE3E6BFD1D27D2C161BA4237D9F513D17AE996DBFC3719D12513EE3BF5A4F841EE3906D1F2FB5333FE110595B22165B870D76DF247BB9EB50E87FE50ADB060C76179CC07B6540D306507E084D4C7E
	56A1E6EF27DA8A43C67B7B8E117EAA070D766BA8DEBFEC341FF4906D9F875E7D43E87FEF8E19594FD46D65FBD8E973986DFFD2CF6F3A9E237DCB94BF63B15A5FD20776DB0037560376CF7A4C6C170FC4473607C70C76F753E739F14418C7A912895EBBE24C239C77E39EE58970F60E607DD1F2116756B11AEA999E45E7CA9CA851BD376D32472F70E736D4A46FCBB968997278915BCBB1179C24C1A4437E681CBF3E6DF21A4737B71C66712D683C7D5D45631CAF7EE5770C02FDE40E9118DFBC90435E23D8823457
	F26CA50AED01764ED1DC1FB5A4452B1E955F5455BF2846BB42E29033F1F5D1ED30B91F2AC15FE4F1BFDC2FC15FD83C08D56FC75F9C7322EFB66F577C711C2EFEF37F627BE6E2D1ED30B9775422EF9A389F165622EF4EF26C8F8750B7EE6E1B4C831ABFDE51556FBB30783E796A225AE0F33E47063E89F0BFBCEEC35F4CF0EC68A07AA6117B46F6C8734791DD7DDB87975FB7290BEA034D799B0750B7EB399FFEF688FD331EE3DF2CC35F24F35FB457EB7EE85355FFD137783EF9E9D1ED30B9DF22F1698E18EF9677
	43DA9F7A4162C3BF6448642EE54CCF167BD80E1249D4D01913005901A2C02B00D2003671FE5E738F684630DE17BB447E7EFC794536A8DB7A5CA1F92F113B8D6613B03FE83DCBBB0458BBEF44C55E6F915E7653762400B9086F48641E2B40CB5662A75F9B5F9991843C277D7D84994BE0D07441EB32B3A03904D5FE328E1777617D36452FFE1BD24B99A8D93F7F64A3E397E9D202045C225FCD56165AE89F95F87DAF3C244E7A9D2B41FE372B7DE529E50D8CEBCE65FF6253F4DF71EB3A8F71BCC4234CF4DA55524B
	F5FF9F50F4C3E31A6ED3BCBF31C255C9D44B942E3BBA2869EE8FE93A3F66F90BED2ACE26DAD686D0B7AFACF0FF5F74C57B7B7EF12D5FA7A6B439DFDDA231CA3BBCA185AF0A9EAF6628844C5FDC1275B90606G4B0DBA207486B027F2EA46CCA68E4A15DC267816194CBAA8B7F219CD96B319F4A8B300D83E69B7797AFCD37E9813B9B906FD588A4D7AE8863EE38C6F174F5C505FAF4B9F5E13033131F1EF8746547DB6265DAF59FDD626319951D9873C9DE3847B72BE7D5808DF49377493C00C7FCC941F6C7D0F2D
	0C4F467574914CBF6CFEFA1560FD72503C5F36CF8659F86CEAD95ED8AB242434673131FD29864736A208E37BE10DF1EC53D1E3FB3946B8360FAC0C4F7A7CFEAD76391177392B5658673FC775D9F7405867CB397ABE7F5306FD6E66FDD69DB476B91B2D6F7352C1E31F7764687BAC2B43BE9BC2586747F546BE67227A146B0CFD2ED667414E2702EA92E853FC4E08B8AEC6CE04A66020D253B82A5DB999739B5E9066B746A3640E33865A9B0052C2188B714C206CCEAE5BB3CB3F4FA8F0978732ABA85BFC0B772B20
	EC439C6D975A9B0052382CE0657DF2598CAB6DB78C7D82594318975CE4BDB07F1C6889407ABCDE406D07510E3D9031B5E13453DCC46F7F504E84AA9E477B5C4B6415203E93F2A2C7206C96C7CCB3F1D7E098D71DA312CB310BC3823E4314FD1F6F3B73D3F978ACA5FF77C1E17DFD3F4C1EAF17F7F81CBDC34DEA1974C1AD4559785B276837C9605D8F148C3499E89BD056B85ECFDBA2E40CA4338393BA27A822EB18E6ADF1E65D9EE9C203498B0E4DED15E92EE561777AC6539C4942FABBCDF39B8B6BD556FDBE3D
	B859CC2FF8A2BA1E291FB06692DE204FCD43844B99D8B97E9B0A3768F016CB1821B8E97782AFE582E3AD7F0C197D4311E87B0D91237D1F50E731B5E24C257C02620E08B11752BF0B3994B770B6C5507E65DBE6760FCFC65BBFBBE9341FC41FEF7BA433134771E40AFBF5B8335FC4F1520E835E0EC934EF351A59EF1D0A36BFB0E5346F5EC37AD3260C7E17A99EBFE5747FFFDB517F2B00A7CC237DD385E6765D535176A33AB6536BA9B453DBE150CB1CB60EFBBDBD03122759788AB87EC30AEF5161EC5CD7A8CE5A
	BB0157BE03E75671D972BEC806769620CC207CD908BFE671F99F1FD43FAB18BC6B1F3A07248BB27D85070267DE4F547C3927012905FF776119A25FBD1C322248BE52BF6272947117A287A848B0E50A4CA768999AD618280ACFCD11B35BAB8DD2185DF39E11E373D03911581CD386459AE17E0ED9447E17E28A3493B836B4E23C2F91793BB9B7221ECB03AE3371C80C0D4FF3E7C1B2FECFE7BE38F7A6B687759FB33179261F4F8AFCDC693A3AB43B78F763DB0BEA034DF960D6EC3E6929DB313925EF5173A79956AB
	8DA8136F076FD23C7C964467C06D3790BFBBF7A7E34FD457BFCD378F126FE0BF29E7489D5940BB31FE8F5D49EFAC18AFEC56581C532CB5B63FB43207E08118FF8277C118E24950CE6358EE6BC2E3A6FD5C497AA51848293FE75933100DBB7D0D0959F347FC50D200FE493C05FE79C6817AC518C3ACD0981B17FA36B0B687753DA272BD3E98740E8135G8D4FA16FE94A0B07F69250A6209D1C57DE241FAB51550F355850EE19DB583743F346F715A74A0651A7C6DF5E496F378AF4F5F6F6B74C4E7F7E78E8F66177
	C773AC7D168E673EFDB2A0B16C72AC7DEEB2B8981278FB6A0F33659C3B9CEA17C39E12D5E1F902F77F0FFC2FEF7304C2123B0D7C36105F9398BF7CA699E3E7586512C2A14EE3F776F7A60DF7F612A34BDF02F7FA7B14995F3684F3BCF1C789DF7B6D8B9981B34078C77711BB5D85A3D3409CC75DDEB23696CF88EE9B1306316D1AB35AA6F93B13FCEC1559E67CE64E7FA8470C6F60E3FFFF185C9F48EFF3AE8565F0601C4BC53E85C50FDFB8729A9B3F92143D2D7210B681E66331BDC69BA42F6865E378E91E99FF
	49924C8BFD7F16A0E06E675F66846E17A3FBC9FE41AE3B4549981F3DC0FFFF57A305E0294D78EF3ECEECDE888749C730B6987AF0741C7E67ED225FAA062219824E6955A26318C90EB4014F69E80999BF09735BCA4D789BB8DFA9B363EFD902F7D877A45EE137F36C5CB4E236FB90BBACA0E66758E759082D605856DC44B6F26C17FB905B4DB1EBBEE28DCB917B20185F31B956D7426F589C0BABC5AC1DE3FFD706D865B2BC77BC1326F17FB2C35C3F4CF816DD1CB653D38CFA71B1FAEF89E6FA2B8CFA6BE2745A33
	4D74B69A74364668BD1FEB26375320A7449975FE3547CC2FB2AEDA6FD80CDEE13E195E9903DEFB0C5EC74526779F03DEA4C64FD9E2FA7FB168A54668ADAFB553CBB668ED0A51DBD7E62617EE504B5035593BFA49A43E3FAD77627B7BDFA7717DDD4E313EE91433F16CFB53BC1E6658D901471B9CDBAD205CD90E8DE523DCAB47B6E5231C03E31939A86766D8178565829C7B6D9E141B61580DBC140B67586B79A81740312579BC5E63D8C7B10FEBB876C4B14A2D6758E4894AED64583F14205C960EA51622DCBA47
	324BD0EE8747D24BF85C4531E365BC6E3A8F5F876574CF0AD87DF40ED6FFB8D72B7F23C52BEF33EA758772347ABF64EB756D85DABD2FC82B3FDF2C551716E875E7CB35FAE619D62F2E506A97DF536A1E3DDAFDC525D67FF615CE37DA2BFFDA23550777EB753F2CA5F576F7F2F16F7FB3F8E26DAE412A8839675ABA707FFC7C79EDD435D08BC08C0DB272FA2F57FEFB2B882551BF4685F96D573706C6817BEDA684855175C27ACD261EFC1481867FC0238845AC882B27D118A29469E2B2C5A8D1A3B0649673080CB5
	75C1D4CC336002F9BDCF8D1D7435AABAE79A5354C3D0C3FE50CA07DD96956A9024C88DEC6AA13201E9E9E10EA2D8E96C211585BC5E082E1728F1C6BD0DA5E05C7A38837AA4E186728AF84CA1E0AE782DB97D771E2CFBDF491E777704716A6F5C620D3FA7BC5DC8B6FB8660454BB18F346C43F7047973591DBE516BD5C46F68B93F1B64364FCFAA12769B2246C652FF88E5D4A6637D21110CFBC427737FD0CB8788D850137BB393GGB4BAGGD0CB818294G94G88G88G19CEC5B0D850137BB393GGB4BAG
	G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGED93GGGG
**end of data**/
}
/**
 * Return the ConnectedMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getConnectedMenuItem() {
	if (ivjConnectedMenuItem == null) {
		try {
			ivjConnectedMenuItem = new javax.swing.JMenuItem();
			ivjConnectedMenuItem.setName("ConnectedMenuItem");
			ivjConnectedMenuItem.setText("Connected");
			ivjConnectedMenuItem.setActionCommand("Connected");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConnectedMenuItem;
}
/**
 * Return the ControlLogMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getControlLogMenuItem() {
	if (ivjControlLogMenuItem == null) {
		try {
			ivjControlLogMenuItem = new javax.swing.JMenuItem();
			ivjControlLogMenuItem.setName("ControlLogMenuItem");
			ivjControlLogMenuItem.setText("Control Log");
			ivjControlLogMenuItem.setActionCommand("ControlLog");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlLogMenuItem;
}
/**
 * Return the CurrentMonthMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getCurrentMonthMenuItem() {
	if (ivjCurrentMonthMenuItem == null) {
		try {
			ivjCurrentMonthMenuItem = new javax.swing.JMenuItem();
			ivjCurrentMonthMenuItem.setName("CurrentMonthMenuItem");
			ivjCurrentMonthMenuItem.setText("Current Month");
			ivjCurrentMonthMenuItem.setActionCommand("CurrentMonth");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurrentMonthMenuItem;
}
/**
 * Return the DatabaseMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenu getDatabaseMenu() {
	if (ivjDatabaseMenu == null) {
		try {
			ivjDatabaseMenu = new javax.swing.JMenu();
			ivjDatabaseMenu.setName("DatabaseMenu");
			ivjDatabaseMenu.setText("Database");
			ivjDatabaseMenu.setActionCommand("Database");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDatabaseMenu;
}
/**
 * Return the DisconnectedMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getDisconnectedMenuItem() {
	if (ivjDisconnectedMenuItem == null) {
		try {
			ivjDisconnectedMenuItem = new javax.swing.JMenuItem();
			ivjDisconnectedMenuItem.setName("DisconnectedMenuItem");
			ivjDisconnectedMenuItem.setText("Disconnected");
			ivjDisconnectedMenuItem.setActionCommand("Disconnected");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisconnectedMenuItem;
}
/**
 * Return the DisconnectMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenu getDisconnectMenu() {
	if (ivjDisconnectMenu == null) {
		try {
			ivjDisconnectMenu = new javax.swing.JMenu();
			ivjDisconnectMenu.setName("DisconnectMenu");
			ivjDisconnectMenu.setText("Disconnect");
			ivjDisconnectMenu.setActionCommand("Disconnect");
			ivjDisconnectMenu.add(getDisconnectedMenuItem());
			ivjDisconnectMenu.add(getConnectedMenuItem());
			ivjDisconnectMenu.add(getHistoryMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisconnectMenu;
}
/**
 * Return the HistoryMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getHistoryMenuItem() {
	if (ivjHistoryMenuItem == null) {
		try {
			ivjHistoryMenuItem = new javax.swing.JMenuItem();
			ivjHistoryMenuItem.setName("HistoryMenuItem");
			ivjHistoryMenuItem.setText("History");
			ivjHistoryMenuItem.setActionCommand("History");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHistoryMenuItem;
}
/**
 * Return the LastMonthMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getLastMonthMenuItem() {
	if (ivjLastMonthMenuItem == null) {
		try {
			ivjLastMonthMenuItem = new javax.swing.JMenuItem();
			ivjLastMonthMenuItem.setName("LastMonthMenuItem");
			ivjLastMonthMenuItem.setText("Last Month");
			ivjLastMonthMenuItem.setActionCommand("LastMonth");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLastMonthMenuItem;
}
/**
 * Return the LoadGroupAcctingMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getLoadGroupAcctingMenuItem() {
	if (ivjLoadGroupAcctingMenuItem == null) {
		try {
			ivjLoadGroupAcctingMenuItem = new javax.swing.JMenuItem();
			ivjLoadGroupAcctingMenuItem.setName("LoadGroupAcctingMenuItem");
			ivjLoadGroupAcctingMenuItem.setText("Load Group Accounting");
			ivjLoadGroupAcctingMenuItem.setActionCommand("LoadGroupAcctng");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadGroupAcctingMenuItem;
}
/**
 * Return the LoadManagementMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenu getLoadManagementMenu() {
	if (ivjLoadManagementMenu == null) {
		try {
			ivjLoadManagementMenu = new javax.swing.JMenu();
			ivjLoadManagementMenu.setName("LoadManagementMenu");
			ivjLoadManagementMenu.setText("Load Management");
			ivjLoadManagementMenu.setActionCommand("LoadManagement");
			ivjLoadManagementMenu.add(getLoadGroupAcctingMenuItem());
			ivjLoadManagementMenu.add(getControlLogMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadManagementMenu;
}
/**
 * Return the MissedMeterMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getMissedMeterMenuItem() {
	if (ivjMissedMeterMenuItem == null) {
		try {
			ivjMissedMeterMenuItem = new javax.swing.JMenuItem();
			ivjMissedMeterMenuItem.setName("MissedMeterMenuItem");
			ivjMissedMeterMenuItem.setText("Missed Meter");
			ivjMissedMeterMenuItem.setActionCommand("MissedMeter");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMissedMeterMenuItem;
}
/**
 * Return the PowerFailMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getPowerFailMenuItem() {
	if (ivjPowerFailMenuItem == null) {
		try {
			ivjPowerFailMenuItem = new javax.swing.JMenuItem();
			ivjPowerFailMenuItem.setName("PowerFailMenuItem");
			ivjPowerFailMenuItem.setText("Power Fail");
			ivjPowerFailMenuItem.setActionCommand("PowerFail");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPowerFailMenuItem;
}
/**
 * Return the StatisticsMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenu getStatisticsMenu() {
	if (ivjStatisticsMenu == null) {
		try {
			ivjStatisticsMenu = new javax.swing.JMenu();
			ivjStatisticsMenu.setName("StatisticsMenu");
			ivjStatisticsMenu.setText("Statistics");
			ivjStatisticsMenu.setActionCommand("Statistics");
			ivjStatisticsMenu.add(getTodayMenuItem());
			ivjStatisticsMenu.add(getYesterdayMenuItem());
			ivjStatisticsMenu.add(getCurrentMonthMenuItem());
			ivjStatisticsMenu.add(getLastMonthMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatisticsMenu;
}
/**
 * Return the SuccessMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getSuccessMenuItem() {
	if (ivjSuccessMenuItem == null) {
		try {
			ivjSuccessMenuItem = new javax.swing.JMenuItem();
			ivjSuccessMenuItem.setName("SuccessMenuItem");
			ivjSuccessMenuItem.setText("Success Meter");
			ivjSuccessMenuItem.setActionCommand("SuccessMeter");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSuccessMenuItem;
}
/**
 * Return the TodayMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getTodayMenuItem() {
	if (ivjTodayMenuItem == null) {
		try {
			ivjTodayMenuItem = new javax.swing.JMenuItem();
			ivjTodayMenuItem.setName("TodayMenuItem");
			ivjTodayMenuItem.setText("Today");
			ivjTodayMenuItem.setActionCommand("Today");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTodayMenuItem;
}
/**
 * Return the YesterdayMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getYesterdayMenuItem() {
	if (ivjYesterdayMenuItem == null) {
		try {
			ivjYesterdayMenuItem = new javax.swing.JMenuItem();
			ivjYesterdayMenuItem.setName("YesterdayMenuItem");
			ivjYesterdayMenuItem.setText("Yesterday");
			ivjYesterdayMenuItem.setActionCommand("Yesterday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYesterdayMenuItem;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
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
		setName("Reportsmenu");
		setText("Reporting");
		setActionCommand("Reporting");
		add(getAMRMenu());
		add(getDatabaseMenu());
		add(getDisconnectMenu());
		add(getLoadManagementMenu());
		add(getPowerFailMenuItem());
		add(getStatisticsMenu());
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
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		Reportmenu aReportmenu;
		aReportmenu = new Reportmenu();
		frame.setContentPane(aReportmenu);
		frame.setSize(aReportmenu.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.report.Reportmenu");
		exception.printStackTrace(System.out);
	}
}
}
