package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (6/7/2002 11:54:29 AM)
 * @author: 
 */
public class GraphClientFrame extends javax.swing.JFrame implements GraphDefines, com.cannontech.graph.model.GraphModelType, java.awt.event.ActionListener
{
	private javax.swing.JMenuItem ivjAboutMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjBarGraphRadioButtonMenuItem = null;
	private javax.swing.JMenuItem ivjCreateMenuItem = null;
	private javax.swing.JMenuItem ivjDeleteMenuItem = null;
	private javax.swing.JMenuItem ivjEditMenuItem = null;
	private javax.swing.JMenuItem ivjExitMenuItem = null;
	private javax.swing.JMenuItem ivjExportMenuItem = null;
	private javax.swing.JMenu ivjFileMenu = null;
	private javax.swing.JMenuBar ivjGraphClientFrameJMenuBar = null;
	private javax.swing.JMenu ivjHelpMenu = null;
	private javax.swing.JMenuItem ivjHelpTopicsMenuItem = null;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JRadioButtonMenuItem ivjLoadDurationGraphRadioButtonMenuItem = null;
	private javax.swing.JMenuItem ivjPrintMenuItem = null;
	private javax.swing.JMenuItem ivjRefreshMenuItem = null;
	private javax.swing.JMenu ivjTrendMenu = null;
	private javax.swing.JMenu ivjViewMenu = null;
	private static GraphClient graphClientPanel = null;
	private javax.swing.JRadioButtonMenuItem ivjLineGraphRadioButtonMenuItem = null;
	private javax.swing.JSeparator ivjFileSeparator = null;
	private javax.swing.JSeparator ivjViewSeparator = null;
/**
 * GraphClientFrame constructor comment.
 */
public GraphClientFrame(GraphClient gc) {
	super();
	graphClientPanel = gc;
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed( java.awt.event.ActionEvent event )
{
System.out.println("here");
	/*
	//////////////////////////////////	
	// File Menu //
	if (event.getSource() == getExportMenuItem() )	//EXPORT
	{
		//graphClientPanel.actionPerformed_ExportMenuItem();
	}
	else if (event.getSource() == getPrintMenuItem() )	//PRINT
	{
		graphClientPanel.actionPerformed_PrintMenuItem();
	}
	else if( event.getSource() == getExitMenuItem() )	//Exit
	{
		graphClientPanel.actionPerformed_ExitMenuItem();
	}
	//////////////////////////////////
	// Trend Menu //
	else if ( event.getSource() == getCreateMenuItem() )	//CREATE
	{
		graphClientPanel.actionPerformed_CreateMenuItem();
	}
	else if (event.getSource() == getEditMenuItem() )	//EDIT
	{
		graphClientPanel.actionPerformed_EditMenuItem();
	}
	else if ( event.getSource() == getDeleteMenuItem() )	//DELETE
	{
		graphClientPanel.actionPerformed_DeleteMenuItem();
	}
	//////////////////////////////////
	// View Menu //
	else if ( event.getSource() == getLineGraphRadioButtonMenuItem() )	//LINE GRAPH
	{
		graphClientPanel.actionPerformed_GetRefreshButton(DATA_VIEW_MODEL);
	}
	else if ( event.getSource() == getBarGraphRadioButtonMenuItem() )	//BAR GRAPH
	{
		graphClientPanel.actionPerformed_GetRefreshButton(BAR_GRAPH_MODEL);
	}
	else if ( event.getSource() == getLoadDurationGraphRadioButtonMenuItem() )	//LOAD DURATION GRAPH
	{
		graphClientPanel.actionPerformed_GetRefreshButton(LOAD_DURATION_CURVE_MODEL);
	}
	else if ( event.getSource() == getRefreshMenuItem() )	//REFRESH
	{
		graphClientPanel.actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
	}
	//////////////////////////////////
	// Help Menu //
	else if( event.getSource() == getHelpTopicsMenuItem())	//HELP TOPICS
	{
		com.cannontech.common.util.CtiUtilities.showHelp( HELP_FILE );
	}
	else if( event.getSource() == getAboutMenuItem() )	//ABOUT
	{
		graphClientPanel.actionPerformed_AboutMenuItem();
	}
*/
}
/**
 * Return the AboutMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAboutMenuItem() {
	if (ivjAboutMenuItem == null) {
		try {
			ivjAboutMenuItem = new javax.swing.JMenuItem();
			ivjAboutMenuItem.setName("AboutMenuItem");
			ivjAboutMenuItem.setMnemonic('a');
			ivjAboutMenuItem.setText("About");
			ivjAboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.KeyEvent.CTRL_MASK));
			// user code begin {1}
			ivjAboutMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAboutMenuItem;
}
/**
 * Return the BarGraphRadioButtonMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getBarGraphRadioButtonMenuItem() {
	if (ivjBarGraphRadioButtonMenuItem == null) {
		try {
			ivjBarGraphRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjBarGraphRadioButtonMenuItem.setName("BarGraphRadioButtonMenuItem");
			ivjBarGraphRadioButtonMenuItem.setMnemonic('b');
			ivjBarGraphRadioButtonMenuItem.setText("Bar Graph");
			ivjBarGraphRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.KeyEvent.CTRL_MASK));
			// user code begin {1}
			ivjBarGraphRadioButtonMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBarGraphRadioButtonMenuItem;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC0FD47ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD8D4571534EB9B5B584424EEC333242529CDC917FEEBDA5B528D3BA5297956A4AE2129CDED6376235F6757522E4D5A54EE5D2E5BBC91A2C4D48CEAD0B1C1C579F57897C4D4FE8DA842A883223111E16681A30F19F1662120686EB9773EF36771FCB304387AFD67FBF34F4FBD671EFB6EFD675C7BD05A76DEE46A3A64ED1234AED5527E552C132428D312F46B7C436B8867FDABBEDD527D5B8B30D5
	7A455BA9708C83DA03ADBEBDD37A70F69A3467C13B25BBBE7D9D781DA5B9EE4FEF039FC174CC826D1155B9A32175E497121E8D2C7D4C098CF8EE868887DCACC05AC6547F3393599ABE91F01B355F886B12A5E98163596F495661BBB6F23ED9708C87284FC01E55A7F2B47A9A40A1CFA66073B282FDF2A3BFD5D72B35AFBDB4CFDA7DC2F372A654256B1FE016A69B25BDF3259719A356CB3F759643335EBAB2F45C619935B86CEE37472D4A0EE14B104F6E9D2E2BFB9F9F4D0ACBF62BA7FD76D1D94A5E04B636FB52
	18BC0C957A1C4FF04FF850AF1760378AB087005B7923CD751A9D73C666C909DFBD9C544E24CD5C3E0C4D81BB7B7E9835BEF6B3763F27A1C3136B0076ACGEE0B5CC036C8DB4816CBEE642D053695E0F88B59F2AC1FEC693B79D5E94D40E07216A03604ED51FC36A5E04B1CB77F1C321345407CA21C4795C0CB4B0CCF4F81E8G68878883584DE26FF2E191BC9B8754E64F6828475D6875B6A9CE5F990FD3D660F7DD9DD0B45CB9596171B9A50947D7272744449E02704C2931950807739245C37439C7A5D53D141C
	EAB23605BFE9CD0E567A084B8C0CADE3AB7A6D2C977D168878F54C96188B069FE0F86B56G1E4F493F30B94776A450525EA6BF77EDA6DBBAD29E10B24B0713B71858927E6CDF13673D4DED08FEBBE08B77716D927471B2402F86484802B5G5082604CA29FBFD9D8B91D0F5BA11EFDEE3B52BDB660357B5426B1176214FD1A2F77B704723599F03972994BF13FC4D56A67E8793707CC7D32BC2AA3B9AC0B7BA3BCAB60972E7BCAD05DB1D9F74646E6B10789ECCDAC4962F338DE60FF4E704BF5F8BE37838C0F6D15
	C033EE0BCF677312E3417DE9A13493G12G246D716939G75G566DB4B72B8BCBBFE27CDBF57342E53FD934831E72386A33373B7D2A5D6D10FB6DBE17FDC0117D1246735DA2744740943927593AF4000E77E5376C332BAE0F9BFA070D8B02404FF99695E13CBAFCB2235ADC23B2A89875CA546F1222ACF8AAF63F5A63F55AD5B9404169CB0BF0AE2F59FB21C768GFEC7EEE77B905B933D70FB4EF672D5FBA962D6C0FB2D70CBD7715B70BC8DC1A9ABF5F5ADAE871AE177CDD0EC1D776642B3F3C7FCFA81C08700
	93E092E0AEC094C06C8EDAE72F75D1BC35EC7A22D47213C153F8C2C85841E3A3E907B11E06393EF762534BG2CGDE0039G8B8116812CFD075EFD7F6A891BA64E23F61847E0B048B40CB1D613AF501EF3CD7A63362C1A5616ADB3DC236B353E51379BA53E2FA1BED5FBEEB21161737C3DB2DCAB39593086GAEE553DCFF36B8172F01A63BDF26286F72C1404A4E404CF35E7BCA50DE799A3CEADBE42F6CF6428F3F96F38F142487564320C7446ABC0647FD2D998256AF700F16EC65F868C33F3A087EB8235BD555
	679A98D365EED9D5DD6E21007CB718AD7EA9DB26DF9B6B774BF12CAA0CF18EC00498EB6521ACDDDCF7F8BCA3E3DE9B2ED8EDFDFC023D2797G7F62EC0A27D75C18DB2D946DC4568E50075DFD9AFDA673B8333D7C93CC477AFB507FE6614F031660F3FB48E2BE3715967339BDE289BDF70D9633396334B68B4632673ADB978BE7AC389FB9BCEE374C82D208C8BCC7FC4D0663F039DD6AF917F3C8D63BDDB7E4C9AA5B09F3BA49763CB4781D331376246B7BE9CF4A3B7137D25C65C653BCA47CEB7D496ACEBE2F33F7
	8676A4EED3E7A946182C48236028E64F18DB65F89B5BCFB53CFFBD43E53B5E026729B332FB2CDD15C74B79DCC20C67EFC77CB93B5365E99AD3D50FDB472271C4EC19A2DBC5783A3AA9789A411F0E7ACF3A9499C99C77E98547ED032566C424687BA5E6EB2FCB3E9E601DBF0438B6D971A20E2F271595B856A870C1AC40521D341E92CAF76A561336E9B4FB9405CF9350F2F6619CC4FA37697C3AF2A72FC9B68B7A12E968EB262167DD8DC667B50D752A3E26F16E627488F8AEC0E0BC5DFEB31E4570DCAEF8165DB0
	63D98D4F345D447353C9B31EDC20D76DA63F7DD3D9A6463FFCF5CC063481B7205372843D7B4B5EB650FA6D4AD8A027D8D2067B3E225B3BB87E8765667178DCB96ED7A3227F674B5F14B0B760FD727533400EFDFAFD2ED1586AF1B3567C965EB02596847B31EE7442F396G170DBA0FE3B196E1913BD17E73228643BC0B633E41F07F8181797C1B14F31E49545709CE7F8553750810381B4F6B6A5D46DCFCEF95763BAD87F2709C72F3F305FE3FEF747BBD8E97CB29B41F3DDE21DF2B9C77278ADCD703AE1F9F72F2
	27BCCEFE7F1F8A3495F3AE8D2D796EB7BB76C9F766102DCE176A7109845F010B010F7556C9FD2D993B25B3F9E31031566670B176658446EA057A0E6B3D709656EE22AEF68D0CDA462E0DDA6C832AC58665F5F5ECEB68F579476C8A1BD22DAE1EFDB5CD33BDBC07EA51C89F46CE823413G24BDD4375E6FA75E6CBD44FB60862B3320BD8C90A6F8B3EF0AFE85EF5EA46B975AC9G523B443B65166877DD625DFF1B758B6DE10030F7A93E7EEBD7B60D730A7D1AFD5C623F8E6F92989E0BB35EDFF2A37607FE8C977D
	950CB05D50CE82105E233D7FFC05BE5662924F840D356C77385FCB5EB3465A9F2B599E8978CB6FD12C0DD7EE55459A544BD0928C416A946F572A75FC9DEA6D222AD4DA079265E895B5380FF9214FB9G7381E281168B9DCFD5CDDBCB360E0728A5BFF7552CAEDB2B0DAFB3774E9C0657DF5B7C684B3CDCDEFF2D9378BD8CDF2643733A6C899F1D035483ADB217F22539FEB37DA9FB395E6C3D4174FF1145DC49DE237E47993EF62FD1FF718D525F8234283D24FF7BCDB37D997B34795D97CCFFB80B632AFDC67DDF
	E17806FDC67DC513629C88E88B76117EED374C74EF5B2F55937B0369FF042D0D7A7DC67D118C5F365F283F70B6693781ADE6BF695F50087AF372200E82E8491B2A2F4F50D6204D75C430359416E72C4B1FE07839F9463A5CB2C2F5F9A450D28ED02DF93696574DB034478FD0CCEF2C5E24DBB7D60F4B2D0AB57367EAFCD7F9FC2EA1171BF0EFB01C4363E389A2475555844FDD9BEB4CF35777EB4CF357B3B5216B121E1AE039AD2FA166337DF5B60CAF9C0063FE44F073219DFB0066E2970A50990F6EF7342F4FA4
	C67E3F006BF86E1E6A60E37E3A1F7C10A87C1060A7BF2C94FE78720D405823F43F4BEF5CFBBF1C3A27BA7818CBEE129FB28F129FFAEE129FF28E129F3EB499987B57F53F4BA66F3D9F5A6F298EBE6662DB64078661875BAD72C3077043E33783E3FFC2773B74763D77C35BBD5541477CAE4BD53CB05EF9428FE38C97856DB801ABEEC2DC8234D7885CC3CD642FD582772DE616776647279764138F9F9F411C3FC33423D9DB916D2A1169460932F7674BB3536A381B738C3E97775662FBE7DC34AF327604C4756D53
	D7F15CAAEC7EF373715EAC66A046934FAB1F3A0E7962A0A41130FDF7FBE507D4D0007ECC923562D24D87C95AB38D683C1FD773319F4E7FEBD1B7FA8B343BB26DB9DF707F25A0402F5CA47EC59A5FB26D39C6709FAE8A700F5CA27E6CC2AD9F551E9D7064632CEE44F38427BCE89FD360F566714BDDDE6DE8996DA8E4B5F0A3655C0A101B53086FC097D4A76D2376A14CC32B0A58BB2C917D16A4781A1AD0CEF529AC1F65B6BED89C30B12A09EC9C8779B9G51GCBGD696D19F4F33B8E5C5C9135D67ACC63944E6
	124B00F6DE31B62EE2FE7E4665DEAF4077AD4F761B71668FDF4BEE340357287FB6222FD123B55984F82EA8261C7F258F74B9FF66234712A9DFB24655DA4D063412C04E4FEB0C4D3EB4CD269EE8EA89626AFD7AF38734339040023EE2F47794BA7A7079751C4E6DFA71G5502EBDF0C10146F74843DF70C556CC9541EB147F6C90B1F79E98A376D7393E4DB76A1326D190950367DE142E85BA3A91C4E7BFC78A675E995FD465D8C5D676BB70DFD4A297ABE9F3AC5FD4E96FD7E60D668BEFFFF4B58277FCDCE6771B1
	44F2DADDE9E8B96FF2BBBD57A1D670D6C675DBDA5A3B2DDD5D2D173BBAAFB7F7408F6E7B486B7A1828FD2BBBE89D98F7087BFC29764C736C52F4E7B79D57F4DB6C57D50B4D63D1C6DC2AE5C806B4142DC58DD3D008B836468A76312392BC91308C39867795D2AC9C137576145D5C133CBE08BD99966D6C4B9230077BB73E097C5BE1A17F2EED527B8F7B7C65BA3DFFAD4DC67FD7CE71FF7F94BA5A5A33DECFFF3859B8BF07B4F9DE8B7CF38B55A2334026D88B55751FBC32CBB22D6B359D8276ADB6267FE6722BC1
	AE23947608D2DA536387E96DC4BF71005491B6E43AF692ED1D4975255CCF7D2501B59DA94ED17ED09864FC81F799057BF53BC2FBE7C4A91DAFBCB90AF6AD0576CA0014B22AF1A2F0785A7B684745876059EB3FE2FAAAF48A54F58DDC119D2A4DBEA451FBB34B77716EED1F59C07ED8780FDF10E2BE3DBD6839D2CE9977C33D7634566FE2F86DEC43027ED000B69BA09AE09140B200D5E5D4336DACC19B7134818654282AF647B0BBC123116109F2G0DAFD7BE3670090FF7177D08E95DBE3D5C97CC6B6DD040FD10
	DD06731DD29E1F1EDBCEE36EAFA98EB61700347ABC7046D5A7B8161D58EAFDE531735786514F57CA760568275963143B2134F48F5198FE5C7471463E286563493DBCE3B9FE9660E94575E0AB67E7816982BF4970DE9D1E6F8B6B989E5BF300362A1C564F118A0CBFDBC5FC3A97E0B6C0A4C09C40728A72614A12B2E91AF3B01B475BA1DF1395DD892F555D0AF1FC2BAA8239EECAE530B318BD6CDB036CCA63994CC1064F2BB41E417C43A86DFBE5C01BD7C9638B2B447DF28D3437D545275782F4G74D75158F6DB
	3473E413F5556A9C12693DC6FB4CDFC25C5B2516CE7F0D4266D2BC6F9E70282AE7947BA77C36D274176A71B224366ECBF13EFC2E21E11533EA787758BD12A28FB2B4476DA9C35F09F3923EA4B47E3CB274815E57396C0A6B86CBD4F954F37ABB6C0E42BFE59D0C3562BD03719B83CE7BA427018FA3819681A4D509B32FD62CC3E2E7F067EFF61F2F07C90D9EA57467B29D69B3D8B329535029B65AA0FC5CD491FC1E5BAB4C4F383AAA4C4F38FAAAC25F4F7E35A2787D2C33C2FFBF4BEB50F276FE4C28067D321A6A
	526B23D42B36D4131F763BA5096633C4777BBB23773E360F3F27BA38EFE6D7861F2387AB27DF0B61156CBE8A5610ED422B2D2347AA5999686020DF96EB7E714ACC01EB77DBBDFE972EA0DE8FFD7F0C3E9E9A2E666782F3F0DE2A690E3239570CA7861ECB844FD39F1A712C00679A4173702099CF66E148538ED37D5626207D0AFD42B38676733A6F32C2F27540E7BB2C5D3143934FA8A28FA34F832359A6FE562F1F75D27ABB788DED109CD24B09FFFFA6CD0B012443143BED6FE25F4254407E8BE02D21FB291B3D
	14678D5790EF67F9640D04F68CC002607D4D07441BA4F8E3AF33FEEB21DFGEBAD713EB0A87A2DA55E4706D83F500E81C8G606F0D7B94FAEFA481AE7788654BC37B8EC8A16F7BBA479425C3F33316BBAFE2FD75C19FDE0059C7A8F73EB2C239E24E5FBF487261E0F5E76491BEB70B0E84F267401A7A4FB346F7E9CA9D0C992024AE54F7FE1FE17EEF28B3668ADFE078CBF546DCE1F797658A63C0DBDEC77BC9D82FD1FF43D10827234177EF2E67D9B62743C7391E8D829F4F70E1BABC57EFE3F8EC0783ED6DD152
	7F5A07C67D7D75D0F755CF273F2C9F7B1BDD4F75EC9478638C9F2543F37D4F32384276E220159CA37DAF8D9A754F855A7CE353695FBA0C7D459C63FAD285BE0761176870DC7FD7D92CE2BB91E8AD47C97F676E4825BE5ADE7CF7B316637655321CAA6A38B187EBE47805470DB9588BE2AD4581AD6F8465E06FD6635EB29B5A73819683A4822C8648E820BC2C065D35864B534FF9BC6C988ACF246CAEF89732344CE7475BAB7E7E5B5409EBBE977A2BEA20F7E2AF4335B5686F061EE638BE40F99B482F8FF6617E9E
	866D8800980025029641E8AB211D518831G5051C8343DDD7AF79C4A987D98E1028BB34199A1C6682F1B2243AC67B14BEDBE4AD9AF764FFD72C8AF79CEE9A4BF3D5ACB3ECB94F7C70B79FA851E880005428FCB99BE815A2BGB21B88FF713C5EF61489E6CBD8905A4290B2DC07FF0A0ED0F9DEB01FEC88A143FD11FA197CD356C47EE93FCC7E4994F7A9077BB14FEE819E85E09E40A200C4610F96C6DF8B6D1C667874FA00CBG134D423F17BFCA4E1428752B4735B45D69A732E12A3FFE7911F4ECB409B5B37F25
	9A7C47FD732BC172D7C4B3792BE8087C55A07C754EB07B7E93F892048F76B15CCAE8E734906E4921D0B6236CCC76B6BDAC90FA1F8D29E33A7AA19450B744DB25632312787D3E6EF702F26F73715F5CD39DFC8F4F2F8E1E0FDB2A274F47EB2A4DEB63E355F75646CD556635F1DB353EB666B9F19E5B73C75D7228476DF2707B19238C77B90012964A1DC7BB697E0A37DF6E44F5E0F7B8E4852FF7BCBE6D3E66370C2FDE481D18C09F340EFBBDBE15BE9025737257B3B05733C2E92DA3E9347BD89E085F0EF64BD07A
	B18DC4FB20A803756992E27C5B4B04AE7D79EF9B5800F538971E1C3E3E5B0CBECB503FE5535343B5BA7E6674DF584C646360497D96475E8BCF82AC95633F29A2EFB37E7D04EC9C7FCB8759B81C01F1F07C77DCEC5F824F6AE5388D9B7A0CB660B9438AB1066E8BE674B5027E650BE6740CD69A03056D5D5F8448ED25B1DC1270EFD8BAA037E09786A65F88F33ED76C98E7CDF6DFB0B63AA7494C437EBCF6E74B983F9A8C5D6F378BB09670A4774ED8B85F066B6A1CBC68137D43C28F9F6BC43FFE2CE52D7CBC4659
	CA3EB8B1E0C69F9774798EB37A9CC1CF170D7132E8CA3CD44BC6DFA3BDD278FA8B5B779F82D8A8FC3DE8883F3945EF1EED9E2F4B61B7464CDFCA51970D83D0DB69568C57E79BB66A433327C5425E27DCE674E5C27739CEFC661F4C57F4C9DD70506A5B4529B837C16ADBE6852FAFB7D8F1CC387F2CEC251A332E1BF0F9A72906FCC84DB16FCFCC31F65F51C3B5732513E467CFFB191D36FCCD6771BE6219F712F4FE76826116885CAB97891772BE7555FA1975551B2F0D7397078E1A5BE5964E1A7E857DB43E7A77
	C957358152258A5C2B8E42C58B1CA293EE053069609033290D46F77FB071643411DC120BF036B6FACFE7D80D354C9C20C582ACEC8B64810B5B0CF94F09EE14DB2E432F025FA96D202B5DBCFF504E2FFB0C7ACA003F814096D22E394FA8B78C7C13GF3C34ABDF841A8B79F78E3G160414FB7522D1AE9178D783241D8AA5771DFE23DC8E7017813468647ACE997DF9F340A827GCF98403C107A929DC639A8600F85C888A9F7D5B64A2DG7EB5G9927C3494D9DB64A6582FF95C0DBC8395F390CF217005F8BB0AB
	089C4FF377DBA9778DBFCD396F1015F25FE801DBEC25BAB4C660761FC55CE2E82F9038DE362F2C02F6DA8761A23BC9C70E402D68A69DE58257EAA39D7582D75EC3329D82B756C3327D8277CA8F49AA82F7310FE4A7856ECBFDA4BBD7605E68A3598801FB628249C68B5CEFAF10EC1C401D3EC832CB846E750BA43BDC608EFFC0322B846E118FC8F62D40AD69A7590CB3044B3413EC2E40DD3513EC0940AD3153FC548B5C438354DF1B403DB2C07DDD92389A8775B7ACF0FFF2106C38409DF5126C9C010BF012EC38
	404D16C9B6AAE01FCC323182F7E810E4978B5C5707C9F619407D7BB0492E94388E97492E913814ABA41B56C93836ABA41BA3F0CFDEA15902CEFA77F730983F9F2036133E35DAFE981ADA9E7DFBAB6651ED1D74CE28BB0B6FFDBB7BE3A63CEA343B1D92FD7F74C68D4AE9F9FAB74B4B47D40F5F6173A80ADF7B666AD35D7477AFB1420667996EABB80F4206FDC782B638EDE843B241FF11457DD3E8D79771DF2C8B703F51037CFDDD447FB7AC566F437E857F2D23817E028B2CFF417F920B7997F17F927C5F2D8F70
	2B9711FF35605F4DE27AF9E430927F3B47837CF19FA0FF2615787F48627DE7GB9023F71C4007FD1BB7217885A0D8624F15927D99CBF8790A1680FB586E47B9DA89BADF46532B87D8C6ABACB7CF1CD817E671CCC57D962FF0A456B5730FF411F5F92606F9DE47D8B7E849623BF82D8AB78CF35867847DC481FF10E788F31987CA16E556708FF74E4007F65AB6C3B4CF394EF8EAB4BC1DAA9975CF056A1CD7D366934BC512D7ABCA3B2FED0A49A856293A4D66BA2D7AB7B51FB7AF2EBC9EEB0EA336DDC4765B30D5D
	278B69EF873BD93D699B536A5444EEE60F156C19E53BFBFB2CB33067EA8F535FC77A37775E3D7E13D5418DE86C30CD517FE89F535FCC7A0FDF882C79DFDDE0B419E81F3D9820D5FC40E8CE22C5FC9820E55899CDA15A827B5D0F29E386BEFDF900699FA07D579CF72F3FE9867AEB1CCCFF8B69FF55F9777ADBE620FFB10B6D7CF324DF99EC397B182AAE49ADAB647D7DFA1875BFCC7D5F6FEA3E7B7E4BCB4A487E16AB2CFFBB751FF4656E7D5778117C477F3FA27B5657F0CF7C1D142ACAA9F8D424CA296CAF0B21
	0927857040C2DA1536B1AC98145B6875AAAE87BBF3D025F4FEACE531D8D4E97354A3AAD5CAE367D20C1602C7CD404ECFF198EA831E5A30DFEF72F39AD5FA8B8FC8F259C906AA6504BAAD012E1A6CBEE25D0487A139F49282DDEB07992A34912D4F6567GB0B4D67453B7F6057F2B7FC6F34D272A02FEB3BA4B267D5FA8B6635F8E7EA48B5F89CB813F4AC6F7CD8732702341E05F89F60F5A95C535ABA30DEEA7FEB358B4214A01EFB5FE1E057D7B09C7A3F25AAF33523558A0197F83D0CB8788B7F6C0C74A98GG
	50CAGGD0CB818294G94G88G88GC0FD47ACB7F6C0C74A98GG50CAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8499GGGG
**end of data**/
}
/**
 * Return the CreateMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getCreateMenuItem() {
	if (ivjCreateMenuItem == null) {
		try {
			ivjCreateMenuItem = new javax.swing.JMenuItem();
			ivjCreateMenuItem.setName("CreateMenuItem");
			ivjCreateMenuItem.setMnemonic('C');
			ivjCreateMenuItem.setText("Create...");
			// user code begin {1}
			ivjCreateMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCreateMenuItem;
}
/**
 * Return the DeleteMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getDeleteMenuItem() {
	if (ivjDeleteMenuItem == null) {
		try {
			ivjDeleteMenuItem = new javax.swing.JMenuItem();
			ivjDeleteMenuItem.setName("DeleteMenuItem");
			ivjDeleteMenuItem.setMnemonic('d');
			ivjDeleteMenuItem.setText("Delete");
			// user code begin {1}
			ivjDeleteMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteMenuItem;
}
/**
 * Return the EditMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getEditMenuItem() {
	if (ivjEditMenuItem == null) {
		try {
			ivjEditMenuItem = new javax.swing.JMenuItem();
			ivjEditMenuItem.setName("EditMenuItem");
			ivjEditMenuItem.setMnemonic('e');
			ivjEditMenuItem.setText("Edit...");
			// user code begin {1}
			ivjEditMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditMenuItem;
}
/**
 * Return the ExitMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getExitMenuItem() {
	if (ivjExitMenuItem == null) {
		try {
			ivjExitMenuItem = new javax.swing.JMenuItem();
			ivjExitMenuItem.setName("ExitMenuItem");
			ivjExitMenuItem.setMnemonic('X');
			ivjExitMenuItem.setText("Exit");
			ivjExitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.KeyEvent.ALT_MASK));
			// user code begin {1}
			ivjExitMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExitMenuItem;
}
/**
 * Return the ExportMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getExportMenuItem() {
	if (ivjExportMenuItem == null) {
		try {
			ivjExportMenuItem = new javax.swing.JMenuItem();
			ivjExportMenuItem.setName("ExportMenuItem");
			ivjExportMenuItem.setMnemonic('E');
			ivjExportMenuItem.setText("Export...");
			ivjExportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.KeyEvent.CTRL_MASK));
			ivjExportMenuItem.setActionCommand("ExportMenuItem");
			// user code begin {1}
			ivjExportMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExportMenuItem;
}
/**
 * Return the FileMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getFileMenu() {
	if (ivjFileMenu == null) {
		try {
			ivjFileMenu = new javax.swing.JMenu();
			ivjFileMenu.setName("FileMenu");
			ivjFileMenu.setMnemonic('F');
			ivjFileMenu.setText("File");
			ivjFileMenu.add(getExportMenuItem());
			ivjFileMenu.add(getPrintMenuItem());
			ivjFileMenu.add(getFileSeparator());
			ivjFileMenu.add(getExitMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileMenu;
}
/**
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getFileSeparator() {
	if (ivjFileSeparator == null) {
		try {
			ivjFileSeparator = new javax.swing.JSeparator();
			ivjFileSeparator.setName("FileSeparator");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileSeparator;
}
/**
 * Return the GraphClientFrameJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getGraphClientFrameJMenuBar() {
	if (ivjGraphClientFrameJMenuBar == null) {
		try {
			ivjGraphClientFrameJMenuBar = new javax.swing.JMenuBar();
			ivjGraphClientFrameJMenuBar.setName("GraphClientFrameJMenuBar");
			ivjGraphClientFrameJMenuBar.add(getFileMenu());
			ivjGraphClientFrameJMenuBar.add(getTrendMenu());
			ivjGraphClientFrameJMenuBar.add(getViewMenu());
			ivjGraphClientFrameJMenuBar.add(getHelpMenu());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGraphClientFrameJMenuBar;
}
/**
 * Return the HelpMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getHelpMenu() {
	if (ivjHelpMenu == null) {
		try {
			ivjHelpMenu = new javax.swing.JMenu();
			ivjHelpMenu.setName("HelpMenu");
			ivjHelpMenu.setMnemonic('H');
			ivjHelpMenu.setText("Help");
			ivjHelpMenu.add(getHelpTopicsMenuItem());
			ivjHelpMenu.add(getAboutMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHelpMenu;
}
/**
 * Return the HelpTopicsMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getHelpTopicsMenuItem() {
	if (ivjHelpTopicsMenuItem == null) {
		try {
			ivjHelpTopicsMenuItem = new javax.swing.JMenuItem();
			ivjHelpTopicsMenuItem.setName("HelpTopicsMenuItem");
			ivjHelpTopicsMenuItem.setMnemonic('h');
			ivjHelpTopicsMenuItem.setText("Help Topics");
			ivjHelpTopicsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1,0));
			// user code begin {1}
			ivjHelpTopicsMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHelpTopicsMenuItem;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(null);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the JRadioButtonMenuItem3 property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getLineGraphRadioButtonMenuItem() {
	if (ivjLineGraphRadioButtonMenuItem == null) {
		try {
			ivjLineGraphRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjLineGraphRadioButtonMenuItem.setName("LineGraphRadioButtonMenuItem");
			ivjLineGraphRadioButtonMenuItem.setMnemonic('l');
			ivjLineGraphRadioButtonMenuItem.setText("Line Graph");
			ivjLineGraphRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.KeyEvent.CTRL_MASK));
			ivjLineGraphRadioButtonMenuItem.setActionCommand("LineGraphRadioButtonMenuItem");
			// user code begin {1}
			ivjLineGraphRadioButtonMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLineGraphRadioButtonMenuItem;
}
/**
 * Return the LoadDurationGraphRadioButtonMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getLoadDurationGraphRadioButtonMenuItem() {
	if (ivjLoadDurationGraphRadioButtonMenuItem == null) {
		try {
			ivjLoadDurationGraphRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjLoadDurationGraphRadioButtonMenuItem.setName("LoadDurationGraphRadioButtonMenuItem");
			ivjLoadDurationGraphRadioButtonMenuItem.setMnemonic('D');
			ivjLoadDurationGraphRadioButtonMenuItem.setText("Load Duration");
			ivjLoadDurationGraphRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.KeyEvent.CTRL_MASK));
			// user code begin {1}
			ivjLoadDurationGraphRadioButtonMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadDurationGraphRadioButtonMenuItem;
}
/**
 * Return the PrintMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getPrintMenuItem() {
	if (ivjPrintMenuItem == null) {
		try {
			ivjPrintMenuItem = new javax.swing.JMenuItem();
			ivjPrintMenuItem.setName("PrintMenuItem");
			ivjPrintMenuItem.setAutoscrolls(true);
			ivjPrintMenuItem.setMnemonic('P');
			ivjPrintMenuItem.setText("Print...");
			ivjPrintMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.KeyEvent.CTRL_MASK));
			// user code begin {1}
			ivjPrintMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrintMenuItem;
}
/**
 * Return the RefreshMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getRefreshMenuItem() {
	if (ivjRefreshMenuItem == null) {
		try {
			ivjRefreshMenuItem = new javax.swing.JMenuItem();
			ivjRefreshMenuItem.setName("RefreshMenuItem");
			ivjRefreshMenuItem.setMnemonic('R');
			ivjRefreshMenuItem.setText("Refresh");
			ivjRefreshMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
			// user code begin {1}
			ivjRefreshMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshMenuItem;
}
/**
 * Return the TrendMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getTrendMenu() {
	if (ivjTrendMenu == null) {
		try {
			ivjTrendMenu = new javax.swing.JMenu();
			ivjTrendMenu.setName("TrendMenu");
			ivjTrendMenu.setMnemonic('T');
			ivjTrendMenu.setText("Trend");
			ivjTrendMenu.add(getCreateMenuItem());
			ivjTrendMenu.add(getEditMenuItem());
			ivjTrendMenu.add(getDeleteMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTrendMenu;
}
/**
 * Return the ViewMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getViewMenu() {
	if (ivjViewMenu == null) {
		try {
			ivjViewMenu = new javax.swing.JMenu();
			ivjViewMenu.setName("ViewMenu");
			ivjViewMenu.setMnemonic('V');
			ivjViewMenu.setText("View");
			ivjViewMenu.add(getLineGraphRadioButtonMenuItem());
			ivjViewMenu.add(getBarGraphRadioButtonMenuItem());
			ivjViewMenu.add(getLoadDurationGraphRadioButtonMenuItem());
			ivjViewMenu.add(getViewSeparator());
			ivjViewMenu.add(getRefreshMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewMenu;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getViewSeparator() {
	if (ivjViewSeparator == null) {
		try {
			ivjViewSeparator = new javax.swing.JSeparator();
			ivjViewSeparator.setName("ViewSeparator");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewSeparator;
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
		setName("GraphClientFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("GraphIcon.gif"));
		setTitle("Trending Application");
		setSize(426, 240);
		setJMenuBar(getGraphClientFrameJMenuBar());
		setContentPane(getJFrameContentPane());
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
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		
		GraphClientFrame aGraphClientFrame;
		aGraphClientFrame = new GraphClientFrame(new GraphClient());

		//graphClientPanel = new GraphClient();
		aGraphClientFrame.setContentPane(graphClientPanel);

		aGraphClientFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aGraphClientFrame.show();

		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		aGraphClientFrame.setSize( (int) (d.width * .85), (int)( d.height * .85) );
		aGraphClientFrame.setVisible(true);
		
	}
	catch (Throwable exception) 
	{
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}/*
		main.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("GraphIcon.gif"));
		main.setTitle("Yukon Trending");
		main.addWindowListener( new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				java.awt.Window win = e.getWindow();
				win.setVisible(false);
				win.dispose();
				System.exit(0);
			}
		} );

		displayGraph( gc, main.getRootPane() );
		
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		main.setSize( (int) (d.width * .85), (int)( d.height * .85) );
		main.setVisible(true);

		java.util.ResourceBundle res = null;
		try
		{
			res = java.util.ResourceBundle.getBundle("config");
		}
		catch( java.util.MissingResourceException mse )
		{
			System.err.println(mse.getMessage());
			mse.printStackTrace();
		}
		catch( NumberFormatException nfe )
		{
			nfe.printStackTrace();
		}
*/
}
