package com.cannontech.graph.menu;

/**
 * This type was created in VisualAge.
 */


public class OptionsMenu extends javax.swing.JMenu {
	private javax.swing.ButtonGroup graphOptionsButtonGroup = null;	
	private javax.swing.JCheckBoxMenuItem ivjDwellMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjMultiplierMenuItem = null;
	private javax.swing.JMenu ivjMultipleDaysMenu = null;
	private javax.swing.JMenuItem ivjSetupMultipleDaysMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjPlotMinMaxValuesMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjPlotYesterdayMenuItem = null;
	private javax.swing.JSeparator ivjJSeparator1 = null;
	private javax.swing.JMenu ivjLegendMenu = null;
	private javax.swing.JCheckBoxMenuItem ivjShowLoadFactorMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjShowMinMaxMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjLoadDurationMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjMinutesResMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjNoneResMenuItem = null;
	private javax.swing.JMenu ivjResolutionMenu = null;
	private javax.swing.JRadioButtonMenuItem ivjSecondsResMenuItem = null;
/**
 * YukonCommanderFileMenu constructor comment.
 */
public OptionsMenu() {
	super();
	initialize();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G9CE3D8AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8DD0D4D716E6931232F6DD2311B14410080A0A89A69AFF02897ECD2CDAEBE2CD5C99D3F1EA1C0CDB6B4E38D546E1336E06F5591D87B4919594A393B5BABB6C44B1E6B5282B2BC8B19385D490D0041670674F237B818F3A7B75F63F96D408FB4E3D6F5C77E8DFC31CD929BA755E7D4E39775CFB6E39BF6774C3AA78CDFCF6C6E601A4E5E4CB465F698CC9CABCA1C9F383BF7C99E147C625BAA44B5FFF
	826D14324B91BC0B3CA5290EFC296AE48E141DD0AED81C6A588D6F3B244BA7679C071788FAC6826FCCFA686050FAC64824E7B3ABBFF6AA971E5B011AG6B87DAB68A750FBF35474063213C5DF8C74A4814A47DA91469EF58E341F31F62F21570EC83AA9E0DB2F3CF6DB778A9002D844A853CF034596696563BA7CA0A0DF25B18F8E963778EE66621AECB7BC4D1C65D8463D9A83D4C8C11A5694EE370AC2D6B697C03CB739639E41FCF73690A2B2B28B3A07B3B0A3C0AAFD4D2F2915F2F78F5D57385EB81A998037D
	1C5410435A10245550EEBC43BEF522ED1A605D8FB4820877752F73CA0D3E447444CBEBA66F0D585735E3F89FF31EB67B1A56BA10197CB4367F9E33BD562B04F294909FC7DE7F61EFB18E4B90A4BE060327E98C69CF53984A2E200E13E3D39DB5C06E31B4065FFFCEE3E83A770A1456DE1539A342987A479A769EEB0E417F58DE47DE66BF85AE7401B540F34424BA76G1582358175836DE3FE7B367CDF70BC572E17EBDE2F66BB677717F95C01DA4D2DF8603D2484B886D62F383400DB1238EFCEEBB8E2539F2218
	1841FEE97A52579279D2D27DD4C9573F4A4C36995B1CF78EE5A69AED244418E3CB0FC13B0DE2F33F9A5E315EB634A9436398FE76BBA64E67321061D83E853C255FA1BBB7ED273E54B846CB79472BB3B75B74A5763BE7B38F0D63FDB8BB4E6C8B377186B75A5883788820F820E420D5C0E9634846FB5BCF8CE763AA702100CF76B4045A7DF2C0AF8B299E3792B0EC3D7654D03636A3DE6F03072E476D12F842BAC72BE7DF32354B6A442319854FF0FB9CF946348B57FD78F4A45DB54FBC689B5B459C4EE3EB6256B3
	FC8E33853E04613A8567F3DB49F0AC0FG5EFAA0BEAFA3BBF0EF2B0BCDF5F80022016200D200D6812D0F2539F1379F7D167EDFE71997DE77572ECF6029746981394A9754E51FCBE914832A5C6ED10212A1F3500576E89FD46FB0DB17AE50F1D171A98199778AE8DD76AA6084C1AEF3523593E5828A63BAD52F828A3CFE095AADF56DC23F13037AB53FDB5695D30273ABDCB8173765C6E8919A007752E751DE8B59DE1A8B6F071EA5DB4D6FC4AC8A4A314F12DD9639BE06E7B5B82562A9A929D0DD588DB9F007FCAB
	46D9007E8172CB01D6832581950C2735352109FC28A2EF0AF4641DAADB9FC2AA9E4F7D21EEFC388FCDE2BA7AG0F83DA8C3496A867B9BA9F8FBA1B2C7EE874817B93493F130660A1950CB779DC47D703F4ECB656C87690EDE48D334636D95E397DBFD751078F4138AA005C4F519CECF295F25FAC13038AF96315GB81262B6E7044BFE6846B3C2392D78748A452F785C7092B4FC615F5DDBCCBF6D50048FB99444F13FA987C78A8AFC2B3213635006556B097FB1634B3A9ED05BC33A5220683A6A6BB46BFF2AE0DF
	0203363220B156AF3AF12C7DB046982084B156494AAE0B3F55E8DACF486F4415E478ED99BBBF13C1FE05187F14ABB9461C625CF36C2F9806B29B055C65AB61FEB25CBEB81C1FBC3857F68E52B114FFFCDBBF31923751380E48F37CFC077D9CCF6E301F63699DC34F612C8E3BB9643C648E9C1F566B337844028E5CAFDC1A4F27B04794D5A4FEEF5A6044F128BED57FDAF5F7AAFA03FAD70173B48E67769D36A76D0177A3F134FF74FEC67B47013B932545B78F585E93E22714E7C64771390A0DB3778F5E27051D68
	EB0AC701CB996C6FA11F4E7125EC3FB370E096430E7F865725755E46711F14EC05E70D528966E63001DF3E168F4F5A10C7D77D9E25C23E43AAF15E26D3580FFAA528F9C2581E355E0C7B3F026725FA592DEAE5A1DD675CAADD719E977BC1DB8F76BB9946339CE8ED9C2D1133DDFBADEB4458884AB50F071BDC223B7F3BCE6B5DBFBD0E478EA71FCFF5D482F11938E6BB19DB4897B24F355A490C84FE3C1099FF5DCEA699784B018ADEC019E7EF96D8E456BE4FE5CC3F7ED2C5DB86157F8DA9F00462222FD66E50B9
	28FE9C46EB14BDA173FCADD5F15EBC167D0263C73B6D677EFF3AB1466A916D1F697EC842F3123749FDF5260CED7A832A9736D75CG8D3BB61F360E358D460671D794BCE3DEA0FBEC2B3313C90267E220D54C9E6BC5BC03F78F0EE5B9912B024E6CF250BD2CB65F9AF739032536EBGE97F84EE4FE393426F27F19EEC378E70DB93480FE6F7DB775AF3412066D259B54330DDFB0FF5FDF02C338757C1079A8842DD552D74117D3DBD58D73C079830E143662D98D708B8C9ED779605EEFB0B64F63DC8F12BFAC949A5
	747BC6B59812BD4C44C61C3463DA0E5106BE0162244F9AF1AE13203C94E8B59017DD59CC329B056C3A9614BD928FE7A9D0DBBC497E30D5349BCF32BFEFE36DC2F9A950EAA17B036B22DDA17B339B2C5D0950AED05BC40A877F7E135FCA8F5E9F4F43903500385C3BF0F172B99D38E00DCFD3F69C4F5C96E1CE7509FCAEE3A61AF31A84319D573BE577EE32EF37FCDB6EAB8A76427EED185530651B27E99CABA752B87E2D984711BBA955F1G28F4920D393C0EE41D13C8B6239EE5C7C2B981A8E5926D4DDF7F3755
	AF972FB99651AFD7CE62FDDFB7A95CAF0BFC284FB19962524964176BFAF6DA7C9262CD38D2F7420AA6DF1B684962EB57A8CF71E4535A15689CAE38E697F79C19EC44EF139FBC57F9FCF13D99476A1C4C630B8C01F7B07C168567F147970D149F70GEF45E43AEB7C2859CE7F4984A37F1290C9FFDBAB365716902EDFE1F8D7C2387E8DAD24DF875E2A84523F2A55CEFF6994630EBDA5127E566B581EFBCA38FEB7437DD342753F5FC67A7B0137E68A69FF773A1D7E0A29DCEF5354C87ADBEEE2FB1E29617ADD8C6F
	1B9A2EFF7D8D529F8D3CF5D3A9166951702C2919166A68820A820A85CA1EC6BEF644BBECACFE3EEF08D8FC5983F16DAAE8FB7D344877C5BE0E3FBC037D159279B8F68A7C85069F3160FCFCD98C47F2937056A612FDAB6B6C6C6B1F4E75C7CF0FE45F90DB4B3153391EAC015FE7F82285677AB7B31C1501377FC5326F1F7BD92C886551AF12CD1F74E20E0D56ED1D267AE0BBB75668899F1E2FDAC06DD4FD0495B34C25855885125F6F8F33FDBF9E5A1C8944B1A743D2203C72C59A3F2B994D485FBD1677230DCD12
	3D7D7FBF48307DA35541471C5ACCF6C893F6585BC2F6F03CC4F6E8EFB5475EE3F95F5872686D10577A686D700BD632432117488EFB5A488EA7059D646B66583BAD6F7F5876686D10FB7D515BE1833B0BB441F83B049DF65FA0BB68428E37EE1AE3D7AD6F9F5CF874F658F673D16A60FBC6D00B9C7B5E556CE35FA84DBE76FDD29BBA3FB1CA0B949BF37B7F145D2BE24176C9E2CE8E95A33678A53EFFF13B0CBDD3884F9500258265A625BA0E81B90174A41E27CA93F90B84A6BB9270A42065C06B01F2E7C0CC8CD4
	83641FC132DB4EC45958895B9C4A0E1172E258BF27D1977BE96525055521B118F3FF4436AFF9434C690E88B81EB7149F8B4F216C34713DAD12F52D5CE4F758189956BDE3E99D2D1584E13FFB759883AE9365A7D8B9D514736A1B8C7E23DBB387190E074981BD7C1AF94A0FE7FA1FA80FE565BB9265A2BE3B006356E161643F0C3FC75676CA9245EF33FAF19EBA823297DC3F412F38244417511EBD57A83EF53E4C6D53E5BCC7A01F3563C715067C319612CFB5649C2F706701D7C8BE710EA9FF340D64AB8C39A663
	5987CF5E6FD155037B7D56DD33FE518D2A9FEB54CBB21E4BC57DFFE66B576BD36082283A52319FD29EDB3FBF85DAAF643EAAA6BD3C7C7DCA4CC598B99FD589D066066C39C9C53F2F68D5BC9E13E5704EFF02F91FBA0F26B7ABC138DF3A65BB8F484CBE3B471029D5FD35F29F4FF41098175938055DEFB559DD91623F9C8C663F49666DD203825B1B8CB168F10967E0D61F3166E04E4E64F9D47FCC4A51CCAF3363C78B3ED6EE470F957CDFD458711384FF7EF9BB7EE241FF7C02153F526033FD0D65D5DE39901ED7
	C1FEBA48654F225C4B02AABB19C340AF1DC5F3FA3B0E66147B4B0FC53E4689B2DDC0A30092011609BA33F7615D970E0110BFBC61E74C9F9F433CFA6B98564D62B9687CD7A9EEFE3B559AB7670FAF4C2418A6FC7D9EF85548673CEA464DBCCE9F436E44D8478D3C38D791DBF44D2A977749AF000A00B87F9F8671F1FDAF4A60FC5E2F37FEC7B909743792A54F5C62483F199B7D49194D1FB32B77C969712DD93CEF0DAD5437333329EF51ADC377EDDECBF85F084F5B3C56C6ED7609B69FEF9B3A4D39ED61ED7EAA4B
	5A26738635B9D33479580D215B1CF3A3D21B5CC78E946373F36DF34326713D5639AE5EFAAAE20EE41DE14BF46339FFE01F31873F3963B349B647D35E2538FA4A34BE6101FCDC3BCAA82FF8F28E65F556149166915854F2429284F6703C7846C2E02982AB18CB792EB17AFE7BBE099D07577D79852ADBB31772C9F7AFE2CC998D65B820B9F3C9CFEBB549AE97329B2AD156B18FE2CF201373C8B663AA6529EA66515C1D39EA1D9BB68F03662E67EA785C7ED906157F7CA0BE4E5F0EC17C372F064F7DDF98FCEE13E5
	597B6CED92B6BF6F5753B82367D1BCDD5D0F5E424F679D77FE874FC6395BB60BF8891A3E525E2D38F4275CA951BDE21CED3EEBA8627E39E4B32D79B92FCF12121FCC4B081407CC19477DF235712C3C340F613C7F898338FF663E867BACD08D109B288FE874EB149F38F68F7B0819959850B9DD17DDDDAC33CCA3EBD415DE93464B869FDBEB4B9F775DC3CB5B9FD72F7946435663B67841B7B82F89B05E45E24CF1778EC71ACBG6B821ADF8968F7B84A4EF9238FFBCA6C7A30663548F7D1DE6F6F4A6C6AED9C365E
	CB65F675F212072BF7783CDD3D7D43567BA75BFA47062D377E02EDDEF108FABC3F35016DB9CD49615F137C8B43BD49615F13C4DD246FC9220037B6D9445755F67AAB678F273F3D0665F567F3BD1B856EE338EE41397E0DEC5F4372C860254EA77D9FDD356AF73E8EF71557070ECF0C783F5E2E5F7DC35465BE1DFA9F773C51A017843482E8ADD05A6B645F5F6BB7FE7730D94F675D1DCA39463E0851E9EF1BB190B9FE1FB3B07CF7B48B86707727F6CD57B5AF36CF781B83B8CEDD73B310E34B86F00F8B281DDDBA
	97B5706F8F60BA75A89D8C66583B5F6018C52C4F1722A17F5EB7E8837C4DDA15BD6ADDF65BEEGC1DFA70FAD0F327336600DD44701B7A85ECCAFA36C18403E4B4E5BD2A857882C0C1D2DCDD06E92D86E854AF969824B3A48722DEF70FC201163E358E840E63EC158AF9936924A9B85766445BFB5AF036D5B6123050E89FF320EC85FCA60B949ED50DBED4D01F13DDDEC7D9424001D016AD28857996E0772E82019825FD40D7DC459617AD3E03477B0E360BAF655B48DB11E7031EDFEC8BBF1DB7C1A5DFFD6C07FFE
	AC305FB2EC9DE04785362501F2A10E8534E6536EC7DE033F3CBF7C9A4C11586F4230F61CF77C467A4913D8FE3F23A32808357E3114AF302AE01D96D4F16570FB72051241317BAA5C7EA47E7E2F1912C477699F158F167B8F0749EBBEBF18F7AE5BECE361C7265C279787476FFF4845783D21CB6B6571F5F87C7E61B71F98FC0C2FAF48AEB81BCD999E639D2F8C0FE3B1B7173F0062588FED798796D0ACF9ECA74E41E54D27541B213D217FD2AF6B1F6252FC6E2015ED70C7707C036A030DEAB01F6B1ED6E355DD3C
	00678CBD22EF73EA6D78D1025FFF590E9F33006E7E72C1DCDB46FE78356A563BB83E3FAF4F44AB955CF745FC7C8D3A39F17F6D2A247BEF62823A4F97D59136C668F96FCB7CD05E66BEED73558C357DDE8D4585FB96D25B6F57D25BF5821BFD1930918BE9EF70D65A1D110B978EF7A7B9D1E5D7EF6530757E36866B2D8B13CB9F365E8735F67A72978DD7AF7932DD3D83C35463FBC8F4A51DBD450B68BC3AD4C5D80540B6B15B57C159AD3072DA5A337B843665B2E15182FBDBE02397D1BE7053AD66FA7E7DD6F3BD
	6FE4ED86957C5EC9F1733C5DA1B147F1227E2DEDE67D47F34D7729F9E6DBDF55323DA190D2787FEF0CBC7793F8A63DAFE56B52E663B3B0DD7A08FF7825CBF91478A9444C0FAEEDB53F6E5225FC76EF8B05E6E2D017F2D9963030C6EED7BCA03E9D13FA05A27327CB3B98GCB79A86CC305BC5187DAF05FA924441EAE65301CD3D1D111AE9560360495D4EF480B1570892A994C2A717DCA179C383940A00CFD845E0C9DC322586F0B7B565CC45A933BA366A6AA1698B958A5615FEF2433BBCD8C60B3175079D2D37E
	2594B9FEED704A9E0FAEFBFA4E795C984B165D61FD62F5B3AA307DA04998CC635C2940FEF7DB6A7C9FD0CB8788CC2445317791GG50B3GGD0CB818294G94G88G88G9CE3D8AECC2445317791GG50B3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB192GGGG
**end of data**/
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2002 5:18:21 PM)
 * @return javax.swing.ButtonGroup
 */
public javax.swing.ButtonGroup getButtonGroup()
{
	if( graphOptionsButtonGroup == null)
		graphOptionsButtonGroup = new javax.swing.ButtonGroup();
	return graphOptionsButtonGroup;
}
/**
 * Return the DwellMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getDwellMenuItem() {
	if (ivjDwellMenuItem == null) {
		try {
			ivjDwellMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjDwellMenuItem.setName("DwellMenuItem");
			ivjDwellMenuItem.setText("Dwell Labels");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDwellMenuItem;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}
/**
 * Return the LegendMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getLegendMenu() {
	if (ivjLegendMenu == null) {
		try {
			ivjLegendMenu = new javax.swing.JMenu();
			ivjLegendMenu.setName("LegendMenu");
			ivjLegendMenu.setText("Legend");
			ivjLegendMenu.add(getShowMinMaxMenuItem());
			ivjLegendMenu.add(getShowLoadFactorMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLegendMenu;
}
/**
 * Return the LoadDurationMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getLoadDurationMenuItem() {
	if (ivjLoadDurationMenuItem == null) {
		try {
			ivjLoadDurationMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjLoadDurationMenuItem.setName("LoadDurationMenuItem");
			ivjLoadDurationMenuItem.setText("Load Duration");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadDurationMenuItem;
}
/**
 * Return the MinutesResMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getMinutesResMenuItem() {
	if (ivjMinutesResMenuItem == null) {
		try {
			ivjMinutesResMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjMinutesResMenuItem.setName("MinutesResMenuItem");
			ivjMinutesResMenuItem.setText("Minutes");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinutesResMenuItem;
}
/**
 * Return the MultipleDaysMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getMultipleDaysMenu() {
	if (ivjMultipleDaysMenu == null) {
		try {
			ivjMultipleDaysMenu = new javax.swing.JMenu();
			ivjMultipleDaysMenu.setName("MultipleDaysMenu");
			ivjMultipleDaysMenu.setText("Multiple Days");
			ivjMultipleDaysMenu.setBounds(197, 192, 135, 19);
			ivjMultipleDaysMenu.add(getSetupMultipleDaysMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultipleDaysMenu;
}
/**
 * Return the MultiplierMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getMultiplierMenuItem() {
	if (ivjMultiplierMenuItem == null) {
		try {
			ivjMultiplierMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjMultiplierMenuItem.setName("MultiplierMenuItem");
			ivjMultiplierMenuItem.setText("Graph Multiplier");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierMenuItem;
}
/**
 * Return the NoneResMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getNoneResMenuItem() {
	if (ivjNoneResMenuItem == null) {
		try {
			ivjNoneResMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjNoneResMenuItem.setName("NoneResMenuItem");
			ivjNoneResMenuItem.setSelected(true);
			ivjNoneResMenuItem.setText("None");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNoneResMenuItem;
}
/**
 * Return the ShowMinMaxValues property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getPlotMinMaxValuesMenuItem() {
	if (ivjPlotMinMaxValuesMenuItem == null) {
		try {
			ivjPlotMinMaxValuesMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjPlotMinMaxValuesMenuItem.setName("PlotMinMaxValuesMenuItem");
			ivjPlotMinMaxValuesMenuItem.setText("Plot Min/Max Values");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlotMinMaxValuesMenuItem;
}
/**
 * Return the ShowYesterdaysTrend property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getPlotYesterdayMenuItem() {
	if (ivjPlotYesterdayMenuItem == null) {
		try {
			ivjPlotYesterdayMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjPlotYesterdayMenuItem.setName("PlotYesterdayMenuItem");
			ivjPlotYesterdayMenuItem.setText("Plot Yesterday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlotYesterdayMenuItem;
}
/**
 * Return the ResolutionMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenu getResolutionMenu() {
	if (ivjResolutionMenu == null) {
		try {
			ivjResolutionMenu = new javax.swing.JMenu();
			ivjResolutionMenu.setName("ResolutionMenu");
			ivjResolutionMenu.setText("Resolution");
			ivjResolutionMenu.add(getNoneResMenuItem());
			ivjResolutionMenu.add(getSecondsResMenuItem());
			ivjResolutionMenu.add(getMinutesResMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResolutionMenu;
}
/**
 * Return the SecondsResMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getSecondsResMenuItem() {
	if (ivjSecondsResMenuItem == null) {
		try {
			ivjSecondsResMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjSecondsResMenuItem.setName("SecondsResMenuItem");
			ivjSecondsResMenuItem.setText("Seconds");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSecondsResMenuItem;
}
/**
 * Return the SetupMultipleDaysMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getSetupMultipleDaysMenuItem() {
	if (ivjSetupMultipleDaysMenuItem == null) {
		try {
			ivjSetupMultipleDaysMenuItem = new javax.swing.JMenuItem();
			ivjSetupMultipleDaysMenuItem.setName("SetupMultipleDaysMenuItem");
			ivjSetupMultipleDaysMenuItem.setText("Setup...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSetupMultipleDaysMenuItem;
}
/**
 * Return the ShowLoadFactorMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getShowLoadFactorMenuItem() {
	if (ivjShowLoadFactorMenuItem == null) {
		try {
			ivjShowLoadFactorMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjShowLoadFactorMenuItem.setName("ShowLoadFactorMenuItem");
			ivjShowLoadFactorMenuItem.setText("Show Load Factor");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShowLoadFactorMenuItem;
}
/**
 * Return the ShowMinMaxMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getShowMinMaxMenuItem() {
	if (ivjShowMinMaxMenuItem == null) {
		try {
			ivjShowMinMaxMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjShowMinMaxMenuItem.setName("ShowMinMaxMenuItem");
			ivjShowMinMaxMenuItem.setText("Show Minimum/Maximum");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShowMinMaxMenuItem;
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
 * This method was created in VisualAge.
 */
private void initialize() {
	try {
		// user code begin {1}
		getButtonGroup().add(getNoneResMenuItem());
		getButtonGroup().add(getMinutesResMenuItem());
		getButtonGroup().add(getSecondsResMenuItem());
		// user code end
		setName("OptionsMenu");
		setMnemonic('o');
		setText("Options");
		add(getMultiplierMenuItem());
		add(getDwellMenuItem());
		add(getPlotYesterdayMenuItem());
		add(getPlotMinMaxValuesMenuItem());
		add(getLoadDurationMenuItem());
		add(getJSeparator1());
		add(getLegendMenu());
		add(getResolutionMenu());
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
		OptionsMenu aOptionsMenu;
		aOptionsMenu = new OptionsMenu();
		frame.setContentPane(aOptionsMenu);
		frame.setSize(aOptionsMenu.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JMenu");
		exception.printStackTrace(System.out);
	}
}
}
