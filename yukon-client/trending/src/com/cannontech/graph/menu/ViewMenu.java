package com.cannontech.graph.menu;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
//import com.cannontech.graph.model.GraphModelType;

public class ViewMenu extends javax.swing.JMenu {
	private javax.swing.ButtonGroup graphViewButtonGroup = null;
	private javax.swing.JRadioButtonMenuItem ivjBarGraphRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjLineGraphRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjLoadDurationRadioButtonItem = null;
	private javax.swing.JMenuItem ivjRefreshMenuItem = null;
	private javax.swing.JSeparator ivjViewSeparator = null;
	private javax.swing.JRadioButtonMenuItem ivjBarGraph3DRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjLoadDuration3DRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjStepGraphRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjShapeLineGraphRadioButtonItem = null;
/**
 * YukonCommanderFileMenu constructor comment.
 */
public ViewMenu() {
	super();
	initialize();
}
/**
 * Return the BarGraph3DRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getBarGraph3DRadioButtonItem() {
	if (ivjBarGraph3DRadioButtonItem == null) {
		try {
			ivjBarGraph3DRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjBarGraph3DRadioButtonItem.setName("BarGraph3DRadioButtonItem");
			ivjBarGraph3DRadioButtonItem.setText("3D Bar Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBarGraph3DRadioButtonItem;
}
/**
 * Return the BarGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getBarGraphRadioButtonItem() {
	if (ivjBarGraphRadioButtonItem == null) {
		try {
			ivjBarGraphRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjBarGraphRadioButtonItem.setName("BarGraphRadioButtonItem");
			ivjBarGraphRadioButtonItem.setText("Bar Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBarGraphRadioButtonItem;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G1A08B2ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DAFDD09D559587899189A6E0B31D0C658F5A7A11360C1FE8B356515409CE46502AD3B2F5ECC629133631030E53091636B8F3F9BC64631104A4E84C90C7ECC8C25A084424CA991AA684ABA4245270842CA8D95EDBE0E15F6E323B0F778890FA4E3DFB77ADE4DF500E494C193B771CF36FBD771CF34F7D5DC7C84DDBD925451E9AC20ACB09756F03E2C23A769352E8642D673CA6B549C79C7F5E865AC9C2
	3DDEE8CF026C260CA4DFB5F96AECB9747D288B7DBD703D1BBCF3B68B15632C938559ED3E600AAB2F53A971F54AE87FEE3D8A5A6DC07D401B81DA2F627A9B74B7ACFE967437DB5FC8459EC2E48DF54472B79C7CAA0D691D04560F72895479353E4F1267G8FF5F20172AD5DC2E8AB28F549AD4D165EC0C696597A68C14FB6DC4BB1BF277E89B6F646EA6B48ED5491A5642E038D5036F60F0F1C882821460020A82AE20A01514691DD50C69BC32292EEE969154448B978AA2076ADABAF27E341BE68C7F56415D420CF
	1A603B8368024EEDFCE2DB2BE5434A71AC127F5D3DF1ED3CDFE736656BB19B331676F9528D1CFF07D1E50D2B03FEBF9033254F603664995C1664D2542DB213FC75C02DA6675FD506FCBF74E520A5A637719F07380D7D5337134221CE4F0EB8B6E61A4C36F5E64C46CDFB8FFB77523C58B80631A5E1D8972899E8GE886A893281666E33A7497E8CF8D192755D0C8D5CEE9DA1B9C544F29C1D1066F169610D83C73E2C0550304301CCBAFBF62E28F271C707CFCC3E2FB6BA3BCC7324F5FC2CC731F1ED2173D65ECEC
	702C376658940E6D2DFA927D962131AD82BE0E43710CFF8D657BA7E3FC962B8A4A477EA848F2A7391F7B37F3DB3A3CB7126A23E7BDE5AE362CFA682427A942EC680844ECE1BE3EDDC69F1B40CF87DA8D3496A89F08C4390F1F120EAD6663CE48F1DD91641E7010A668E6DBD8120322EE797AB563CA3EF6A3B6EE7FD79E477C327A18B3C61B6EBE636A17CD2B5F756C0BB2FF9C0F467C42569E083B36BFFAF9EEEC37E3B8C84F44E81445304466CB149FF570D9EC175133057D941095GB17D0365486F1EE27A4576
	BC6FD27E00034F66D9D3464F73A848F201D8FC4FA9D87B1AAEA6793A0002C0B3C019C0EB00F2AF72989F98FB77CB1E23EEC7FC59582747DE07D60C1A3A50299826208444DEC11704A1D9B40825731BB1746B503CF18574FC87E00D0FC4C55485D3D29518DD880910CC8653F9E9ECA76A68A2157A2510888B04B44267FDE5ECB73432E0181FE8C14194E39ACCDEBA0EB9B1A974420CB081FC6F1BC6FF5DCAEBEE816C3FFC1A7B6A220A3C8B500FCEF33FA40C6F0276ACA437A8373434CB81B4C35027F80E4ED66018
	B4504F1A66677205FE1EFF6D5BEEA6C7B6F63A669F2DFB53D60D18DE18FFA9F4DE6FCC122FE1065F13BBAA7A3F62F9D80C322CF551FE36C643573EC62955E20EB3BFEF51B0D7BBE0DFC120A5B35C57BF9A2FE3B958A698A24F3A0FF5C898B19873BC53DDA3631DA2CE0A0A59AEEA2292048F430A7903F2C5AC9F07D5BBD79E26FC2CCF27A1E18C1B3FC15E4978B007B33B397CE7D4AE1826AE8D05CD31C7B4CDC999090D7F85354518D77A8CEB2F3F1D403DE6429EF3005659FB8D06F6BB722ACBD547431A9FCF0C
	151F863D071F847D976D780BE55EF931D2E9BFA64FA87D3AE347A93666772F5A9A189FEC0DA338F56B7BEF0E6F874A2E565A5B6DB5FEFED557E0316FD362676E970AFB6E8695775C9DD32E1C1B3A62161BCCB6256059D4A30AA357AFA9D86F822A2208744059C3884337AF78F19F12A219FDD2F0C4B4FB240BA2605DD94C5987E8CD2D016F06D9DE8BA37BF9AD2C3F78ED32F6305E95AF2D3A7934A7E1165522153331DA48EC1AD3718C09328860996E2730E2B2FEB22D57965FE076FDD403BC0E2FCB28DEEF7DEB
	501EB9AF84A535ADEC1A2A0232CED38C9DA53CA63DC971F08E2C3D9EA8FF161F53E28D657C1CDA456834AA4B4CBD043FC36E2BF03EC38AE759BB2669D2122F9DA86D926A3C642BF1688CDCE2BA313CF8C12B46D80993E1912ED0AC86E745A9FE8FEA3B9648FA85B99C3BDFDF517021A5BB6A88633FB8616E3B17A730F60C5B737FF162D50277A41B1379728E816754F4A984E5978B233557D4433957BC58873E4F8A212D1A832CBE07BA9B6DB78D628A46AB203C3F43C1FB2C04E335F3554EB7D750E8F54D8F2451
	B96B4DB53790431EB7F15ECC60E74F7138193A333E1EB28CB5A0D188E179E53F614C9D46EBB0AA219D16F48370ECD00CF25FB69AE8ABE28C0BED796753CAFCFB58EFBDE9A854981E8CB58AC3E62398144C1616B318E73D129196E46ABE425ED83F23F7BB4E31F60E3F255AFC9827C274D3821C9FA03646F32F7F19DC0E4BBE0469D55D865F81CCC4664F59FFB95F07776FB86A71457167498466478B89B1FF26CC271430F5AB766C61FB9B93A605E82391013AE2ED495A47D6036FA3AB016F23B10C7BF8927AAF82
	15A772BA50771EB34EEB731B624639AE514264098B633CB90C6BF583BF1848637CBDE327A34E70468378B98259EF57DA33046539557F4CAC65F9CEF84D7FFD059B560FDA6B27A5DE0E7799963F1746ACABF1A1C6FF1072339397E2748E9F4768EBC0366F9A0EBB5F0EE0DDC800FEA6D08ED0AED041B5FC0F4F1A0B3E3FBE0CDE617D75074B5EB2D5C9C93E3AA477F7CCECFF0F52D89ECFE27B58ED73B7D1FE5041E77BBBCE7958DF893226A5FCFF0F78B1EE39505F34046F29B53C4D9137EED5D2CCBBE64F053196
	293AB4A2A9F6ED24BCF841534B0AE1246D159443431CC5C096EE223C72E458DFB247AC77535F205877430E6FFF7B2EBE1E98392AEBB05F9C0E444793CD91F7BC71FE449DCF34C62E0C055BA371708673FFB52D456D607B7EE49E13CFA9CF067E0C9D134743755026C0BF9B68F120A20C5952A4DF871086148934E6A957FF166A67C1FFABD04D35C93E63C0FEA093E8A5D08ED05E35DC3FA51C9E47E7B82F331FED2D4B7B9D563A0B799EED47D67B923AFCFF4CB6A12EEDBB977431D6799C7DED041D2F4556AB315A
	9DDFB2CFAB6C78EE7063190E5A7D47E8FF4A1AF3B7B9D009384104045AFAAD7E361B7C99A17CBE3CB302368E43839ED222C79383A4AD056205CA0E971AD3182F3A2CD606164DFB0866C8C8912140C9014DF45C894AFB9E63EE6BE9667C75D78F905B36DF91DC27CBD2440F7037D6875EC32C47F4EE29C31D9ED354626B3CA53F09BA2302A6461B4CDEF3C847FB32CD50DD35184E2B33FB9DBA475A634DD5FA8CFD5E258A4176B07BB544FD3EC7BEF8F301DE7CB9FF799F4C994443BDA21CEA81E003AD4BB90179F1
	DE9C56C5E314C3E24230EA6D24932BB6DC475E218357F1ACFBF44ECD2E5972BB091BFC09AD1FAEF6132F326527BCEE72ECDBDED662A6DFEB4BFF62AA4F33655F70BA65DBACB9FEB3790373645529CC5E1C4A714CE31FB971CC750DF59EFE57AFBCDFDD29EC5E7E54981EE178698EFAD76118B4102DCBC55E53154EF5319E9C86EA84E2726AF9EF08D2071C593561G47FFC59BD693791E6638FF9F28DA466CE9325AAD4D3564483ABE8F4B0DCFA8EEBAB4F9481AB7AB625CEFF3657B71F13AB55F0C557A47EA2D7A
	7074BB8709AB26F4F919F12CFCFE12E34C1CE57CEF9423F37CEDD1E07336904E2BCF633C3B0AB9EF4066C5BD1C3752661DA8613CDC1B6775F21E77FA0E45B34CFD6E765BC70B692DAF2DF55733B74756F859DE2375FA0E159A3EC0EFDB3F0F8E9C4033AB0C39227EB3B07547C3E300F87D428861F8631E4A7F6FB7739F17713C49396FBBE44D524262F86F8673FA6B1DEE35899F54D2BE33BFF1906B6BBA106583F9178386GEA826AD84E315FFB1F230D08DAE1C327CCD3880C52D7985F99D62B989BAF92B677CD
	9750DF83B0CF541EEB6A733F467391B03BF5D593F5F30AF1690B5972D35FA45A30EA7982AC30BC7EDD4E4605665C7C3BF651F14F9337F1F90B0E7BE1315B38AD0B0E1B75380DABDAF45C699237F155AB969BD761F59BD73F6838BF390EEB3E42B8168B8F89D8873BC0EF94A88DA88BA8E7854F0BDB3F305E4DAE7975E1F0C4BC2D523FD4187C2C258E46475099030B7FFD6346C17C6DE0C8059A964279B9BFEB9077E92A9AE5B25E4D03F86660F9B3EAB2D50B7F03C10A1345E14A365E3797F04FB65EE6A9EC695F
	F181FD003F45C902ACDD24C82087941591063B95DABB9F871FECDE413178FD1710D7887D72F44EDBC26BE98D746BED1E17564EA6681F34F9BFAD66E33BEC5ECA8F9F5BEF737EDB427502B62F4E4B75B41B372B146B4D247338BDAA440F43934262F1F8CA203F6B007F7CD31A6543E7847A1E9C9EB6C4BB5E1B05EA1B57E9F42B06045EE3F7DFB2751BA162EFFCE210E1514CC9DE2BD352B996FD6252FCAC2A868861586ADBF373E53786E3325A627932A2CC81423E5F9A09FD370D463EC5A9767D6CD86C3BD40E4D
	FBAF33C78F0B6C7FEA3C7A49B7214DFE1E141A440BC555EEAA915756D1B8EA129D08066B1C1C5D34F3300EC2E003F3ABG41726F2AE36DF50E6EAE8423F59C0D5239AEE362AAA6A93360A63FF78E4FB9F1C5E1721E3838E2CB862B89C5998BFF978A76E1EC1AEF0077608DBC0FF2FA5FA1716F4E1E10A04B26A00F1FD202F80F36CD19E26C8DAC75617C86573104CC2675215DE30EB17F83D0CB8788CEA494A66E8CGGF4A3GGD0CB818294G94G88G88G1A08B2ADCEA494A66E8CGGF4A3GG8CGGG
	GGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA88DGGGG
**end of data**/
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2002 5:18:21 PM)
 * @return javax.swing.ButtonGroup
 */
public javax.swing.ButtonGroup getButtonGroup()
{
	if( graphViewButtonGroup == null)
		graphViewButtonGroup = new javax.swing.ButtonGroup();
	return graphViewButtonGroup;
}
/**
 * Return the LineGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLineGraphRadioButtonItem() {
	if (ivjLineGraphRadioButtonItem == null) {
		try {
			ivjLineGraphRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjLineGraphRadioButtonItem.setName("LineGraphRadioButtonItem");
			ivjLineGraphRadioButtonItem.setSelected(true);
			ivjLineGraphRadioButtonItem.setText("Line Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLineGraphRadioButtonItem;
}
/**
 * Return the LoadDuration3DRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLoadDuration3DRadioButtonItem() {
	if (ivjLoadDuration3DRadioButtonItem == null) {
		try {
			ivjLoadDuration3DRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjLoadDuration3DRadioButtonItem.setName("LoadDuration3DRadioButtonItem");
			ivjLoadDuration3DRadioButtonItem.setText("Load Duration Step");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadDuration3DRadioButtonItem;
}
/**
 * Return the LoadDurationRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLoadDurationRadioButtonItem() {
	if (ivjLoadDurationRadioButtonItem == null) {
		try {
			ivjLoadDurationRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjLoadDurationRadioButtonItem.setName("LoadDurationRadioButtonItem");
			ivjLoadDurationRadioButtonItem.setText("Load Duration Line");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadDurationRadioButtonItem;
}
/**
 * Return the RefreshMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getRefreshMenuItem() {
	if (ivjRefreshMenuItem == null) {
		try {
			ivjRefreshMenuItem = new javax.swing.JMenuItem();
			ivjRefreshMenuItem.setName("RefreshMenuItem");
			ivjRefreshMenuItem.setText("Refresh");
			// user code begin {1}
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
 * Return the ShapeLineGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getShapeLineGraphRadioButtonItem() {
	if (ivjShapeLineGraphRadioButtonItem == null) {
		try {
			ivjShapeLineGraphRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjShapeLineGraphRadioButtonItem.setName("ShapeLineGraphRadioButtonItem");
			ivjShapeLineGraphRadioButtonItem.setText("Line & Shapes Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShapeLineGraphRadioButtonItem;
}
/**
 * Return the StepGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getStepGraphRadioButtonItem() {
	if (ivjStepGraphRadioButtonItem == null) {
		try {
			ivjStepGraphRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjStepGraphRadioButtonItem.setName("StepGraphRadioButtonItem");
			ivjStepGraphRadioButtonItem.setText("Step Line Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStepGraphRadioButtonItem;
}
/**
 * Return the ViewSeparator property value.
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
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	try {
		// user code begin {1}
		getButtonGroup().add(getLineGraphRadioButtonItem());
		getButtonGroup().add(getStepGraphRadioButtonItem());
		getButtonGroup().add(getShapeLineGraphRadioButtonItem());
		getButtonGroup().add(getBarGraphRadioButtonItem());
		getButtonGroup().add(getBarGraph3DRadioButtonItem());
		getButtonGroup().add(getLoadDurationRadioButtonItem());
		getButtonGroup().add(getLoadDuration3DRadioButtonItem());
		// user code end
		setName("ViewMenu");
		setMnemonic('v');
		setText("View");
		add(getLineGraphRadioButtonItem());
		add(getStepGraphRadioButtonItem());
		add(getShapeLineGraphRadioButtonItem());
		add(getBarGraphRadioButtonItem());
		add(getBarGraph3DRadioButtonItem());
		add(getLoadDurationRadioButtonItem());
		add(getLoadDuration3DRadioButtonItem());
		add(getViewSeparator());
		add(getRefreshMenuItem());
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
		ViewMenu aViewMenu;
		aViewMenu = new ViewMenu();
		frame.setContentPane(aViewMenu);
		frame.setSize(aViewMenu.getSize());
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
