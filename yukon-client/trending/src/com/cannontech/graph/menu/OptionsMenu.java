package com.cannontech.graph.menu;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class OptionsMenu extends javax.swing.JMenu {
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
	D0CB838494G88G88GC500D6ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359A8BD0DC55B5CED1B31A99BF1329D16994BF310C22250ACE5AC9951BCC1BAAD3D3872751E2E51A3455911536F6CC1DF4F243A7A18889980CC4D23361CF008D9012907E1FA501049894122022646D6E8B3C303BEF337B00A5A121675C7B6E5B376BDBE234E1664C5DFB4E3D77FC6F3D673C8B495B95113EA1A50F108D69C47DBB380110D5150404953F5544F1992611CC227B5B8D10CB44EDE9506683AD
	6358C8E68E39E7E386742B20DFF5F4A473C3783D0DBC30716D8A789102CF9C500A6A1E10666633F51C7359C47B3DC29634DBGEAGB78890E1C27E02102F62FD505F227EC65810C2C85FEE9CF363F43E8E3FC69DE782DABD528BF14C0C30D325AF84DC94409A00353B7DEBEE2652DD5F58207607EF0BA0EFBF131B120D3CF46BF398AAE4F3E75456C46E2406C8A54AE08D344D7D63232DB659E93689AE176CD2C45B18F954A338474CCE51B55158F884FF1FF0AB126C729E87CC92157361698C3A86A1B1507795A1
	2E64825A2686FE77G1C81E0327EBE3B196BB49EC1926FCB8BA96B52A2A6E3E211DF56704713D2EEAD467597D35B63BC937407G189E59B3155FC28F1D8A0469700640F508AF66BA7C4D0BBC32CAC6B20B811ACB388EADE5DC07210BBFA5EB2D75A9DBC368B0D8225A3B442FC344D0C15A8E9ABFD1059883CB0016G108C10D78A7E879884A820F1FBEAFC8F349DD625CBF6BAE5D7075B5D6930FB0E4BF651813F9B9B012262BE93ED3247CE880B4DC7272B8C646160A88D0CCBFFAC1DA6BC1622BEFB00A84A1E14
	F4835DE2DE5816B2CFDDE3E129DF3738D2345B1D5477B1709B6721BC8C3F1862F34A7CF8664BCA0A47FEB9509614F1BB8FED613274255DC5F2EA8F24ECB210E5512FAAD3124B188C9965FED9180DEFA8C69B57833E9FE08CE09EC084C0ECB937F144783EAB593897E24863929C8393D63760D1BAA7A407DD742836FEC8184B56C640667D7C2A67B13BC46E537BA861317D06F6C9087CA8A5C135475BBA3BB05E7F8849BB2F621B3131C57361178A7A2A3C0279B0DD431F2578E69D1E79768D3A07305F8F3498G66
	1762D2BC5B7277C03C837481588142GA2GE276F05F3CBE3E77DB46FF3F4EAFEC6E63055B2195FD0AC768F5F99541E593CF899EC930BAC4AFD147BCD9087630864CDBC677258DF89C91DD22C7403382D6971CA28401170D09AB4C45B19E11D287A527888C1CEE42570DAF5C067BCC70AAA75DF6C1917DA398BD2190FDB9A91C0295E181781DD609767AADBDCB63C17F64BD5CD643E5086B817A19CAEE1753C56FC3FB8C02D2F4B4B6DAA49B0AA1F826F9EC7D186EA98546AFG082C64FB6A4DA19EBB166C45246A
	05FA4358C1082DE4F1302AB2B8F69E22EB13AAB89781FA2A78FDD8FBE1C8B7FF01BAAF3252B81E362872DD5D1EB99A40A3DDC78BB5AF75AA76973377B365281F83741A8F90D945EDFEFB110945E2276095F9741D70C06008F63F875858DB0A71CE90A7C517E2915D224B8EBF3C2A6FFFD83C599F17E7E5ADE642A99E4F17AE889C2F063F37B81761E18DFD14F37A0314AEA80AC732CEA8620028A812EB54BF7FD1AA0BB76068722A3A46D5202E312063AA00D7B5DD1BCA3669622BCF1647A75C0338F354B85DEC45
	18DC8763732A397F8585F18D506F57F08E0A43B18E8D77CF2F5E07B876CAFEE97896E38263A4CBB10A133960EA62045948DC9A5A477BCB0DFD5CD4EA6C630E5239FD58DBEA64C3C66BAFC57D64A917AEA6CE166279E013DDAE118626B60530BC694DC154C3F2C94AE949BEAAAA83528591F211EA74EDA2BD03EE055F77D773F3E32A081F9B45976EA131A3DB8D7302C50B4DA96BD4DF65187D678613C9A843D8939DA2A4E1F01ECF3894069F25671B0A7726D2DC6D2E77205567E98C7F49499CE80FCFB894496D90
	AD42B4A5B25A4B0D1834770923608ABAC50D7175FBE8AE8A72D8G064DBC46D7176D5045383A113BE40703190C70DC7DCF9774393AE2E639FE943431GEC4C0ECBC6E3D6C11B240D991CB59A1336F7A4B3FFAF17E9E5B96A67954FCF08F0DD6106BBA6CE2BF74E9F4A5F8F221D929C937E3B6C15F23419C337D7997E798AE33B2F2A40FAE6DCDBBF21E2A341BB092D4962A4DA40B55D9E4989C79B9EBE2ACDCCD63DAEB5A0BF56BA03500E81FC979F32F5239523F567C13B90A0EAAF0EF9C72BB7B0B7E038AD9790
	578B8AE0B148E276F80E3EAE32FB4B8CE396A1F9AF0B55341A607C313E8A57AD86FCFD0D16136F511F0D9DDE2FEC13E89A205ABB3DD29F4F8C17D90931F9D672F8A117340BBE6E331CCA14957384952D5A7D73CC4C7B35BAC632BA4D9313CE33E0D54C22DDD29A9B0F224DCEC95E8941C15D2256B15BAFE628EB745470BA666C8C7AFF9E749781C48330311FDE62E3D7E8E36F39CC6F76DA08C9001ADABEF6412C36EEAD9FFBEFAADD977A0BG22EBF98D3679438F396C6704C941E776CE41D9260A2C4AF913D563
	2D2D375CC6F95781EF001ABA4E7B13C90DF79D9F1B65233C213F88A03A0E1FD3276B74BE0FCDAC88697395F54C5789F541BE3FD00D7C5682BEEB9F77794D1539BA1FC32D856964A86CB06E47C3D529ECAF297D162AF43E17883F138AAE9A655C26FD0CFF7D3EEF5EF1AC37FE097A2FE79F4B2DB7E8785594BF2843331C5BB143EB63B3C0DB3E0F5F3B27AE9971AF2FE7FC1B6BC3717FE096576B2F8F667FB1458F5787737F49E54EFF8CE8F1751C7FED33C67CEB76B33E167D2178EFCF4375067687735FC571767D
	417CA3D3B9FFB7506277F37E37D80379B79C003379C0685C0271E93663FAE78EB0BE599A7E98450769700C7FB3B40E319F89343C031C7F4024117E738E2AF57441D07A4F23319EF91071C95570B7D17C929D1E71FF1F62313F82E8D58D3C0EF01B693D8E7D08869E574FD7E56B623ADF16DC0A96533355F89F489EE9D4F2F1DC984559E48FCDB6D8AED8C74F1CE8D8B39600616E2038B8682FE9607AA74E90EDAF3C267B6D1999A246767FFFG63F173B56541F4F65273B463901CDF07389D1E3F446DD0FC085B21
	E0562FFB316E7743172F3D9DFEFDCDF9B01DA369DD51837A8EE9F648A65C8EF64D8EBB537C3A1768FEBF12FA6D6D701BEB4A03697CG3D6366033E611A9D5E2238E86847EAF66834E34E93877DA4000CC65833G7DGE30DFC4C27F44CBC68C7G4482A482102611CCD3939F3352F6C6A7832E07AD2E23DF77EAF5998B1A43F83D9E400B57CE186F252BE3B6FF873BE59B5A719C3D7FAD2067E0933763F313BC1E4EE87A5F6043A08A537A3752FE14564F778D2974EB97D7351447555412D7836C8C1EB063F96F53
	7A5334BF2D7205DA71A26A2DC0719957046F982D7B8961F9654F2650C7E7BD0293D2328137E8A376E63467A397F95E1D534C6CD32C3616E61E4F07DD8ADCD7B8006B32792FE975CF14BAAFD6ED933579CF4D864E3F7320FFFEB16173C98B1B17273675ADFC7EAF695EF13AC4276C12EC76961C97CFF12FG8CEA63B65A8279AC4514D0354D6FFA3006D2EB8FC974E0CA5A2B08CECEFFD7429834CC098E071F24523A37FFGED3FC3D6BE973D0A68318B535F9873D8FB3EBA663864BAAE78D805460731B14B288F0E
	8E08D04B8B0A6C29A5216A57A42A630D76BCDD8CC43630EFAE2B20E5749FB90D68752D1C3EC3B62277E874550674E10D7E30DBCFF737B2BA7EE6B55B0B6E601A8D690BE0DCD4AB7749F51301BE49ED747B7E2F13FC7EF2983F8AE0ADC0FE5BC8E6C39B1F7F58B64CE186C4E5429DDCB82A76E732BE6D534BBA54463EBB4DEB637541F3DF6A6B031C3B5ECB6139E370DEDB5846F40CE87357872C9E0927398D4E0987DAD6BB62D2E674FC31D64067C6B3G2357875053F5F4A65733A57CCDE95D33114471F8D148F7
	3141F6A60FCFED13A113DB1E736F0DCC362D1739EC516DDC36164B4132752EDF3F5EAF1BFCB9D836DBD219ECEC4D23042F792A36260F4C2D2FBBB5F84D598D0C4E7C793A955BB239CC55BFE2CA6F17064C5DA16BB6D307FAAE286DF0F301FA36AD5BDAC48C6B4E2EB151B65EA97B34E8E1FABDE967F5252303474725437A774925DBF7263C97C20E7BD57E4BBB7C7111B0D41026460813FF23C86B64F5EC3F4CF14D9A6EA3B747A91D3CFE3ECD59E92C07F6E230393967795C28CEDE975FFF9EF37FB550DF871057
	4579E4CDF1DD2B3A388FADD3FA9FD1FF8478F0F2AA584757ED5053A38368E83F2D8174172602E360E6154E74DC11DEE02CE7101F567A381E76AEDE4B3CCC7FA7C07D5659D8826DA9611C7A9254ED1740C67C837DD1D87A04759CD46C0342A86177740B063578DC40626369CDBCBEE2FEF1AFD9F2436A8D2162A33C0B4545D2351D3F390062197C87DA704CCB82DA068572B2G8B4090005B42EB333B9BD1C6D081956AD09441B6C63FA8F14DCEC962149F0D17B953AD6F72F7FB0FBCC12EF69E536573D674CF9848
	9D21497E67464AD0BE81E43FC7F60B9EE51AE1699D294A304C0EB244DA82F334F8CB681C0D49F0F79B465A2BB0AE2DFBA4F3A7C0BD40E0B717272A690AEF3C5D3EB95EF8DFF49A59C6698EAD971BF7D2B61AB77F0A73F6390D6605DFF15E7673C673226718473EA564FB705CD85E9D7C2EDBC4712B3A035FF59FBC4F5FF5130056FF18675839D3FA7EB100DFF1F86EFC1B4D7B0F4FC86E04B9662A67C0BB7ABD9946E59D99492C8130G8C9D61FE9FEEAEE4FEB758BF5D76D131CB26AF438ABFCB5ADAC23F699CEE
	3972FB7209964C612C322248CEDCDFBB23DAD0CFC5F6D3A443FD5102E70AC79A9DD358D095FF26857713C3BCCB518C77F5AB6A2C3D8F31AD230E97DB51867816A3898E69827D94BDG83DD232CD6FA095EF38A58E47E91DEBF15527BA99C7AD11A2D6EEB8B2D7B03ED466FD951ED466FD9CF344D7DE679E4DB2877AEA6DF32A65F92CDE6873DBF17C3BFDE439D2077E7A27413B5DCB53D9757C1BF2B074765479E4465C33FC143D5D05CB074E7B4DC74796F7BDDG57B74267EBBCFE7B3DF904FA4FCC556CD7CE6F
	7BF06079C78DD7CDF1B1002B55F0F9533C36DE59436360AF6D2163A0297D4AFB60DF6D747D83E2F7F05A2D466F3B6D74DB625933DED15BEB6B5BF3B4DC2F37DF76CA9839ACBF7CBB0DD92F08EFF1221D55FA99A24F8392FAF8DD50628C2CAB3E788A3A047D3E74B5A1BC5F7C019CB8AEE244CFBB6E8E247DD77053AC6783E96DD6BF2DB58BEB4901B1F90A55FA413564BB17372B74BED9309F96ECF057794730FA65CBCFF06D055FF312FAF86D355BEBC4A73DBCD794CA337CFB7F34E4D74698FE27AF5B0F6F9171
	0C516CB2ED736BD0486C671990D92EF53B075BB82F17670EC3DE0E6B6B65B1FC0747683C8E6B3D527D741557E85E22B96631B8FD444377F4D4AF5F670A176316G0E6996EE777B7FA9BCD2897B7F4F645C1550C63DC1529532C9FD12D4C8B62FA5CDD8CCAAE4A3AB3E679A12EF113C986DA67A3F10A67F178405E4514F8626BE41AABAE064967C8AE052BE95A8E49BC5C02C6C05E0B131AF83BA1E99343AB51B4D8A494320411112F342092331D5C88EC5E3B019D8B4311C34E2D6DFBB2D3DBEA5E46D54F70C5979
	4C3160B72F61BCFADEGFE49B1FE8EC4ED2BA621736A812760F0A802E33C43E5479C3BF3DA917D673F10076BFB79981548E8E3F9A877B95D1C7F81D0CB878863C024B4318FGGGACGGD0CB818294G94G88G88GC500D6AD63C024B4318FGGGACGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6B8FGGGG
**end of data**/
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
			ivjMultiplierMenuItem.setSelected(true);
			ivjMultiplierMenuItem.setText("Disable Graph Multiplier");
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
		// user code end
		setName("OptionsMenu");
		setMnemonic('o');
		setText("Options");
		add(getMultiplierMenuItem());
		add(getDwellMenuItem());
		add(getPlotYesterdayMenuItem());
		add(getPlotMinMaxValuesMenuItem());
		add(getJSeparator1());
		add(getLegendMenu());
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
