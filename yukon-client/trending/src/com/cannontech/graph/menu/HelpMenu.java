package com.cannontech.graph.menu;

/**
 * This type was created in VisualAge.
 */


public class HelpMenu extends javax.swing.JMenu {
	private javax.swing.JMenuItem ivjHelpTopicsMenuItem = null;
	private javax.swing.JMenuItem ivjAboutMenuItem = null;
/**
 * YukonCommanderFileMenu constructor comment.
 */
public HelpMenu() {
	super();
	initialize();
}
/**
 * Return the AboutMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getAboutMenuItem() {
	if (ivjAboutMenuItem == null) {
		try {
			ivjAboutMenuItem = new javax.swing.JMenuItem();
			ivjAboutMenuItem.setName("AboutMenuItem");
			ivjAboutMenuItem.setText("About");
			ivjAboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.KeyEvent.CTRL_MASK));
			// user code begin {1}
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G72FED0B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D9FDCCD45995B72D5B582EED353109B5B425555D3A8DEEED57A6F6C3D25D58556C3AA9A6EE32FF1806BF2C5BA6F6E39B1B1A56B61779C01485E6G818533A8B88CB28E83A262GAA82FE3B22D0E43BEE74B17304A7EF660DB38F813F68B977634D93E7303BC61363FD77F76E4739671CFB4E3983A97EA4551EE5A9A6A44BCE783FE3D904246CA3646D1F7D6B9D01ED7B4419CBCC7F7683B949F80F8D5A
	1CCAE7EEFA1BB357C142E7F320DF817D5950AF056FA292BB9B2C000FA47B3481EF49436FAD18FE1F39F9E21F9D343F6C6147506683D5825685B4279277DF7B300C6343504F675FC8D996C21A766318AD56B2933E1E0F4B81DE85508ABA664307951C7F907AB8E69E340B7747575CC925FB495F427BB76626122D6FB4D972F0AF537A02F28E30399E5ED612F42A88ABF169EEE85BFAC606CE843410B7A00543DAD81783435E2128949976066470285FFFCED6A31761AB65G4AD7E24B21F38989C25FDD0558F7EC28
	13CC68EF43B100B199576535F199660D24124C05B5C9E5D42B18ECF32A62B212FB5F341D2A46759D0FBE6673B2001F839813E5601110E5E6351025560AE353213F9EE8F33510253DC648B2706067E45B60F5CBC192D9EC87393E8E46E5D97E6B3D56BD547E7B8A500643401B8D148A348AE8A3104D654CAD27FE77C15EA1E8BB8675CEAD9452429D1148E9B5983D2C85E5953E7DFE60F06C1A9C5022C1C218EF155BBC896491D461FA522F623EF01D88DFC83B76AA51754B96FB0233ADFB3F596263EBF43962E79B
	F7215EACF654DB903EF19E4EE7F89E4553CDB83349BCEAF36CE7G2F29C668F9A0DF48F2493600B89ABE336CC8A04B7C37BE354C2FE1B2AC2D094B42F47CF7876AF883605B009CEE30AD50B920E1375071017CA34F5271F97065E8D8D2FBC787A3D2D4BFBD2A28C1B94AF5DD72E8BADDA7A2B62F7ACB4FE3FAD9FC44EC238DEF8CA4544B06456D1619EE260F94F7DCAFEC6FAF126E1D6EFE5AB7728D9B7E105E098CB733215540FFCA71CC934EECDBCFF16CEF81DEDF2DB31759657B05981B16C3FFBD50E6A05BA1
	E7EEB550A9203EC342B6217C7A7F537FFBCCF6E1F3DD85A5504A63FAD4BA9F0E69D2B8A077CBD1C59AD4659861E36A8BD09F03CF4CEB2277B2GFB1C1543F2D45295AD8C2BCBA1991CA046463497B8F1CCD4265CBEA5A443862188916BF69794C12BCAB17DBF1120244B71910CFF39GEDF9CF6A0795E18178DEF4887555C8E3228E5FF38E89DD3DD504589F21CF6A04DE569494C2FB911CD2D67D7EAEA500E2C8518961DBB9F61CD38C63BDF562BEFDB4A07C26AB6F9562F97FFAC23FC1BAD547FCE020EE2A5F94
	51F5E7G1EDAA77258825B4017746329D4DC175867BC469EEFBC779E56A4B8469D26E75FEE7455D530E7A6907188DDDB9D154C87CFCBB1D9F85D15A8B80C9C0CEB1E0DA5CE4C8572BDB92CF74991B99C040F9837798E674E38BF5E568CDF4923B84615CEF018180197B91D8C07B54C5EAD786514AF69FAD4999C5565DED9571570D0FCFE9515A576C4480A7133B61660D94B600CBE20D346D9531CC5A63F3A24E9A32311BE3CB15CBFF51AA77BE03C6A9136D9E835BDE12BA5349F679F35BE2F6D12D1FC4FCFDF58
	9ED6C31FBFAACCEE5BD78B935BB62DB031EDFFD9B83D6D5EACCCE4BB46DBD908BE2B0D05CD3E70F6A1460300968E4B54A10DA90455E79F7561D9143022DFD702C3325E2B5C17A17EFA502667E94C19835F0BBCA2D60CD509D8D1FD7F47E4454D4604F5407CD72EDA36FB585DF59C0E478AA6536AA274B1D91521780378BD9A56997E9E0DE79C0FD9A956B2398B5A8BD89C1E5765D003103B5701E3C55D48FCD7AE45F5FD30DF97500D4342F74F976FB179AE3F201D1A2AB215GEF51E1BCEF373D58F4867DB02B3D
	711B5556F76D665AFAB367D7FB1D39ADDE31D773EE875AC73EBBAAC3DA418BF2D11E9039E1F76194DE3F240E46F3CE77EEFC9C28263B45701612447ABAD10277E944D87FF4C9B6415C4156E4FADAAA611A11289202D00441021F6565C7663374007C782650211D85146A45B1370CBA9CF3AD4346A8568A7F2D360A3A6332437CCE8846AEA47489240DDE264FED5E29755802B2DC372C9E6A30FA214B7492F34C6908453400C253AA574BC82959DF9896A9C5FF392DC4E3D01B8565F1215BFB25A8AB66DD8EF37D7C
	2964GF93A0E688EAA3A96B50A3CGBA89BB6B23B36677C6FA41C9CBEE1233365533B35E280F1FF5AB5478EC5F4FF6E17DEE3C0B144110F774DE48AB8D6ADE99B6777B294B77AB31D1C925E6A36CBD729A4D3338C6CA3DF80F2C4CC67FD88F7D4DC0B61FB3176D313334D46CF1C73AA70DFBE3E390CB7852FC3D3FBD926BD57844FA751338DE9F74D52019BEF1772FB71A6D3CA273C4D2BB2778F85D6D1BEAE74FDE5CEFAD6019BEE1673F14BACDF606778A14E4C3607DC6FCAD33B2BF677D05E5F66167C464E825
	9C6F6ED6D85351G67GBA85F42BC16CF1E84FB3EB796E71E9EA79576D096A62998D6CFC739A1E2ED3D87DFBF4876AF2F1832BFF338C7C84451719F0D6972F4E966F5095402BEE94F96C9FCF5565330017521838BE0A6F3F105AF2E96354B7576B94DF55B8754DF5F4D23C39BEGDE5B91D1EB4E2AC03B25C2FF6991215319FB72CCF66B511430EE582CE1AF46A2AD2A8CA9E101B5D38CDE23B4C1B16CEEF9725CB9DE1EB8F7BEAECF1CBBE716CFDF974DAECF16DBD98D73DBFA37D64079B2009876D70AED007E56
	A342962D58F03F68B2FD3F177D22EA0DFCE30FCDAFF48FF6E667E39A0B1B600D50A47470DF0A1DEAE23AE1323CB609D88FE011A66337AB0A4D06FE1A0135CECEA7B34E7D2A727698FB74CD3B077DB9F4922F733EDDC16F2F511FCB7B93FCCC9159E047FC2C03432EED425F794ED691A2724CAF46B0CF5C0647A238DCEFC48E1060D1546B4EEC11079DC7199E2AF95B85ADDB57CDF59F8A4B903814C0BA1DF712E26B00520CF1F79F0B7DD9FF32836B8D2C117A3408920809A2CA487BFBBB5E5F0EC1EDD40F33F8AD
	34E952DC8B11E6D6573B21E57C9CCBA2FEDB3348953F7B5C1CAB9C8B3C9691671E32E1B34F134D71DC41F2D3FDF68E1FB387F8EB1A917BA25B3CAF5E67DAA0AF9063FF77893E5D44E7F23DFBD0646F6D6FAEA66A2F4EA47D3D50F10C57FE3C1D75301CAC4D58E4E53A6BFF0CED4DF08DDFB7F54CFC5E16DDDDC9F3234A571B415B2DCB4A390F2C3123DFA5495754BACCAF7B886A857DFE79B171BB65C90B4863DB0E09183D3A1A983E7AF255C1E87B25BB89AB1C8B305D1541BB10F97B24A1A2E2CDD376D77B9DEA
	658E2163E5EF7E04AC7FC6F6529A495D42F4F00E37158F4AA94E642FF4615D98875EBC2065C099C09B013637089C342E4AC3FDD84783F5683A9498265520B8D93FA20F45E13CF8EC6D4E9A541763B8443863E22D326A3AE4BA8230A72AC56428BE41D0DA39F3BD9DA0D375F489563C85249FCF9C5BF86E30A4526FAC181312E49E13DDF2230FAF05B1EB0136G597C4E5CB23FB84719033C26CD200FEEF864F5EA74D7AFDD78C62DABF98E76391E7DDB59F197565C031A2EEBA1DCDF60A7DDF8CEDD0BD010E1DDAE
	7491A88B06F5B6146397DCE88FD53ECDE1061D2B41B39B791A291C0F3FD203BA40F72BA2294AFD7AAC68050161A1162F0E53BB5284BAB96797B96CEB943B81FD5540BE2458B8746798586796B1AE554052ADE2DC1A21E7451DDCEF91773375B661266F97B8EF5FC4041F79311B56CD37EF47E443BEDFF7BB8C6CFC2CC70BA9F8DA9607FA485339E108085C106E9739618764495CD4004F8F1E877ECD70FECA01GBC252292B8B729E825FF074942B9EBE18D1E4F70FAF05E9FAC483B55AA56FFCCC4BCE4FDCFD6A7
	3426B06605F7B098C89B113DC332EEF45CB844AB0D4133877508233A69C77F451BE79ABC3EDA9ED35AE5F3FC75BD4837D81344140CD6F6F7B2DB6371154974CFCB6039E56A70D4A663F676DD3BF473F2C76F45DAFECF0FF9D0C70EB661AF5F2ABBB0CDFC6904378873E0E177D68F7AC20CE36C6F0D5EAB78B74334BF933BCEEC185AF5120BCDA5CB71BA3153FCEE44625AC9335E363DF4B0E9DEA26DCCDF456DD35FEC5AD5DA67812E360B337C2667F03233C014698DC92A2ACB6AC8C7B80839657404AE476BF47D
	AA2E9F93E3B813719EDC5D49EDA4667C8FD0CB87882038921D1F8BGG449DGGD0CB818294G94G88G88G72FED0B02038921D1F8BGG449DGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG598BGGGG
**end of data**/
}

/**
 * Return the HelpTopicsMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getHelpTopicsMenuItem() {
	if (ivjHelpTopicsMenuItem == null) {
		try {
			ivjHelpTopicsMenuItem = new javax.swing.JMenuItem();
			ivjHelpTopicsMenuItem.setName("HelpTopicsMenuItem");
			ivjHelpTopicsMenuItem.setMnemonic('h');
			ivjHelpTopicsMenuItem.setText("Help Topics");
			ivjHelpTopicsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1,0));
			// user code begin {1}
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
		setName("HelpMenu");
		setMnemonic('h');
		setText("Help");
		add(getHelpTopicsMenuItem());
		add(getAboutMenuItem());
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
		HelpMenu aHelpMenu;
		aHelpMenu = new HelpMenu();
		frame.setContentPane(aHelpMenu);
		frame.setSize(aHelpMenu.getSize());
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
