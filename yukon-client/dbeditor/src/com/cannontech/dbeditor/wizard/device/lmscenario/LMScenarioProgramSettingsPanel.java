package com.cannontech.dbeditor.wizard.device.lmscenario;

import com.cannontech.database.cache.functions.DBPersistentFuncs;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.device.lm.LMProgramDirect;
//import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.data.lite.LiteGear;
import java.util.Vector;
import java.util.HashMap;
import javax.swing.JComboBox;
import com.cannontech.common.gui.util.ComboBoxTableEditor;
import java.awt.Component;

/**
 * Insert the type's description here.
 * Creation date: (3/31/2004 12:15:45 PM)
 * @author: 
 */
public class LMScenarioProgramSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjProgramsScrollPane = null;
	private javax.swing.JTable ivjProgramsTable = null;
	private LMControlScenarioProgramTableModel tableModel = null;
	
	private javax.swing.JList ivjAvailableList = null;
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JScrollPane ivjJScrollPaneAvailable = null;
	private javax.swing.JLabel ivjNameJLabel = null;
	private javax.swing.JTextField ivjNameJTextField = null;
	
	private Vector allGears = null;
	private Vector allDirectPrograms = new Vector();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getJButtonAdd()) 
				connEtoC2(e);
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getJButtonRemove()) 
				connEtoC3(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getNameJTextField()) 
				connEtoC4(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getProgramsTable()) 
				connEtoC1(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
/**
 * LMScenarioProgramSettingsPanel constructor comment.
 */
public LMScenarioProgramSettingsPanel() {
	super();
	initialize();
}

/**
 * connEtoC1:  (ProgramsTable.mouse.mousePressed(java.awt.event.MouseEvent) --> LMScenarioProgramSettingsPanel.programsTable_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.programsTable_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (DefaultGearJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdd_ActionPerformed(arg1);
		// user code begin {2}
		fireInputUpdate();
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> LMScenarioProgramSettingsPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		fireInputUpdate();
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (StartDelayJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (DurationJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the AvailableList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getAvailableList() {
	if (ivjAvailableList == null) {
		try {
			ivjAvailableList = new javax.swing.JList();
			ivjAvailableList.setName("AvailableList");
			ivjAvailableList.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAvailableList;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G510D1AB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4D45735561A0426B6B5294D2309C90D21F92625D934AFE9DD2D2FA5296DB22DC9ED17E9ECCBDFC9DF7A6AEBED9F69F33D5056559C86D09488A8E030CE4C60978C0EFC15A0AA43CF509189202292614E4C85AE4C4C9DE66E48E0CCFDFB1FF34F1D4BB04347D5572A4B4D3DE71F5F5E676C33FF77G49FFE7F1FA0AA11F1014F4227EBB16C2C8C687A15F7E5121E39CB7671D51CC227BD784300DBC
	31B58D1E8B20AE4EBD1A19CBDEBA14816598A82F92C7B35F0277BC7233C34696F809B04F06E6C2D263ED0FCEBD4FCF7A79BC1BE979C2C696BC5F84D80AF381941AF0FED7C6210ACF825C1B6ABBC20A011075340DD1AE5461FBCD2CDDBCBC57GACA842B6D199C6353E8A4A58468A4F7EA256B6G4FAD143A3BEAEB78B877AFA59B3E3F5B100DF369466730D66D3BD1FD1A4893F4A1D249A70EDD00E7DD474860C93B6CB25B8537DBF6AB22FD486C30098EC9113D66B1690660F5189D62F549AE1A1DAE1FDDF48BDE
	C92E2D351E6BD25FBB3C7220D7F0F5090AA23987FD9D02DBF442384EBD48CF031CC16782FE201CCCF15F10F18D6360FDA5C0629E4E530F326BD41A970EACA5090FEF0B48D35DDE46CB7F5EA0CFC973B625676E43713FC277887BAD067A35G0C16BFE5F0DA3A77F1DA968DE35B79D00E81D82E616DA308DF836575GE97BB90DA78EF29AFBBE7CBAC936D98CB991E8B46EE734B56D8F5238682AA5FDA715A31B8DE5E18E5445GAC83C8G5888E0BCB01A390B4A6F5B42A1F8DAECCA236CF249EE0B475360F4F84F
	498E51896F3535D02362BAC53B6CF59042E47434DC9A069E8ED587A64AA782636D9261B2955B7994D114E30674B03CBD7D1249502A0E51FBA048DB7481DCB73387576DA63C272863B27CF10ACF506159DE45D3D940F2B254759E646B5C73A627451A36046416B79AB60721A57A399AC37CC1C6435A03C1DA589A57B9F00D530AC7B3CDGB5G9DG9E008545FC0D87054A6956388D4E0457ADB83B7CB60F60D59A7C1253A1FA55357EFA46D4EB9D8ED83F6F4E3A9FDB1725157ABDCA783706306B12307420A13618
	2D470A62603A30397F9AF16ED70BA74B461B5A9E6652B311DC4C76B0D5437F0D62B3CA02F83637CB685942F2A1545D84E06DDF266D93CBC2E564D50A5FD892AAA35288171142C3B08EG5B5F1F0928AB93213C91A07F5D514CD2GAB4090404DF7799EFFD5B8B243F354215BDF56F74436831EE2C0718AEDEE1FA2386D62C5D0FA024DA97A085AC631613A5AA6749B27675B8EF31C965D22D7D0A4598D238BAE910449475A4C35ED43B6DE1156F6CBAE91A6F0F9889F77DEDB9EBC1D02CF3960F1880A98EC416A9F
	30611ADC97AE4208B0G3C2FFA97576B9A55390B60FD593BFC2DB68FA2AE37F4B43338142FCB36FDBBBC4F02F00B4E5A5AA6490EE4885EF1AE23D51EDCF856C17BD781969C9E4D2CB7737379079EAE07CD59DFA625AFD9424AA115BFB3132984F3289CDA6878E9C7C6B37781B481B80EF0BB7BBD39871E75EA9F1CFB266784698DAB5BBABC1B6317F40EADBA5CCC67C80D00CF573D33F5778F216C4E833E9683449F61EB5FEAB7B119EC90FCA2174273DE90A051915C8956365901B6C63CAE3A15A651A33A9D7062
	D3E5406A5892144F81D9131DCE0AC7FD558882645370BD0EED8C8FE368251D577753FAC1D13C124D2F085C28EB75C314965F84D568D3F99D1310573540E392GA9633C3E6648534919D516C77C1EEEBCC12A3C96ECC1594C077625E5FC7FF70C205F552115CD349C2CDFBA5CB30B7D42F1E62F231F1D559C11E4E26AB9FEB102E7006B4DF4954F6AFECB6BEE826DB1G2BGB6E87C4BA3BDE17A4DE67E69E90B22F368475F728F1C0349462BE2E4597E1D98DE36DF93434B766B6254327B0698CEF6D95D268164D1
	9EF36B4EC266G6AC73B6CF60B74C0EADD88731F7F500DFCC8EEC939A4B986C525CB3AA102BEACC71976D39DEC047732F22EB34776F21D396F4613A43E4F98563F0A7E42F1C3D4B91357C565C11D49E84A9A44B3A6BAC597ACD4236CF7AB8C3F1D6AF7956FCB25381A7C0731EF316A207B6819ABF549FE1FE826BFBB3C224FA7BA0A07056B02D998D34CF49F4C67302E99DFCBCC078F1FB4CD6D6A17FA7443771D538FEC0DB87271120BA5E50451E9EDFF1452F926412FA8E01F9D0ED201AE3819BDBAC46F006CF5
	CDA654C22BE7CE6930B6C11FA5E464D0CAF5C3A32965C425B5EFB4C6CFEB2768122F0BF716DCB647ECA92EE0745E1B932074360367F1269B4ED20BA4BA9D25F6B0G0A197EE43E8525B5E0760D41746A000DD8BBF3EA87A42F586676787900C0C3A5338BDF24FE60F2106555G6B4A39DD38EF906B39DDD00DDF236CF4326386F5568AFA16BC793A73C1AAD85C7D26D67F58B475DD57272E3F95311E517961906AE11FB86A97417DC3A9BFAB0EF39FEEE8FBC85DC541698F7A06778EE1D2412933F98C7F11947E9C
	FFDCC2BBB7220DFF0F3409200F4746E4FAA7CE40B1BDDE4985AE829AF1B53FD03AC51FDFD888FCE0AEE28DBC5781303E8F956FC63E27895E17D762F83B1DBC97C120DC88D0D749468CD43275E16DDE515AE5D4C12C85D0DF455A4D2952377B020B37DB8978C4DDBBD22DEFF7594D5B35823E9FE0CEB5EB37DCED17D00D6D66EB3905A39A6EF10A7BB9982B3B52F84CF4AED71FABF1782A426ADD2AE70F32712B0E0646123F9C41F13B816FB84A65B7E5D86F57D8FCBE59AED1D7DE5D6338E13DCEE638E706374293
	4E0A8F62CA079860F2724DE12495FDFD952D6E75175EBDC1A647AE4DB4BB435D4646B1FACA3B65C688B7DAC1549AB513F5D1924788DF070FDA74791565B9A5064C886B90D0790F562DC37FD5CBBA2369F33598B3074B9B59A91166C13FE476AB92869502A2D02540840B65E19E247E9E4E3D76A84F37EC9D437310FB8C629300FAG967B1DF6E629EB51833885GCC561FEB574B7ACA44E31F9A08A5G186EFB4AF9981E3332F75A3A0F245650B3F6FB260D2D77123CFA153ED5CFADA1F53736064D95C565649B6A
	EA58BABBEA026B6DE8A8CB8FG30F5586A626B9087ED5681B05D35522557DD2677188F5D7A9EDF03D1D785BCE7EF1F78BA7C3974BDF85E3661A4BC06FFD1233F97E8DB789E277F73A56168DF759E57D7B545F54179EF4B923279673A797C09B0F6E1AD1F7FBBFB7473F75432798335FC7D7E5B0D3AE51666D2DD3B95A55B59D98F310BDA3E2DC22F1762930BA36A257935CCAE9657066A256F3870ACAD877C6ADA2E170E0EEC536925CE508402FB90AC0F66F30FA0EFE0E354723A911C175A984263D6A2633A3C8A
	E3126330DEGE5G9D47799C32F35A3CD9F3E00A3C5943DBF88E4AF31C71B46FF8B0C60B0677D800F8BD0E660866D2BD3166B84B91E5EB787BA9BED907E77B1ECB71D8B6560DE6C6557178691CB3B4873621EEE20C18FBE2E219753BBA295F6E10F2D5C8BFA68B439F96F21D29931D5A5AB3D435B01D764218DE27D9CF2832884F79A7F88E7B4F6DDCF7461C603A33001E2D84A8EFG48BA49F3BE97BDE8C777C1399E2087C0810886F89AE06DC93E175FF20633B19377B2120189668F587A643AEEEFDDF7CF6A97
	3A2E49A7432DEBCE574E706B4AB486615FA15E206784674ABD45E5E04DED527A2BE9E9EDBDB5319F2361A7A51C06FE0D06A8776D51706024FE6610F27429F06B353F5898C18EF9EC204A599E6EDB451D62727460A86256C1B97994171DDDAEBCDBDC57F44812DBA1DC4F2CF4216FAAFB25C1494DF1AFD01CDD7652C01B61B26448B9009CB9FC8E20C08E1F8358A5CF1D5F5AABC74A9130DC54D729EF12D18FFE84G43FDEBB762EA20EC55F076EA44F9209C2561669DC5DCAC14D7E8383B4D08DB8B6524FAED4F69
	B9CF33409CGF5GBDG810005965E66DFE89B74E9CC2A2F9CEBE13E70EA2D4D0E317E10BDC7403170D9E7091C73CAD36B82AA8D615AACD471EC2E7230F34DA4F74A73E219E4E2FED48F596A73ED5A7E799DB9F48F414F72D14503DE24DA779B2500044949E0F8CBF80CBE4F39C5ED532C480D651A6C8FB63F9DDE763B6CDE08BA51C5554EEAFAG778D69C832F0BD1B5D0EFA363B81F4B1403C863E8FEF3463D9DC8465380649EB3982F0EBD57CFA7549EC56A7285EA60D4CE625E87887A8BECB07E736EC170BFF
	57AC043A1B0D7C8C76FBA21F1FC1CF78736372601A7BE43F57AE061E9145D3285585B5FD1BA23AF01955781576D7846F20B8690C4D237DD9DD247E1F72C4BA07EC8FCF3B74FBD821496177BDFA3D17584856F3037A4CE81A3C7EEC3CCC37FE3CCAED3C1F176847B32A7D4B54E7D344713EBDE13CAAED3CB776CC75DD2FA74CF8ECBD9E9E45FD8DE8654F537238B65F73DBF03DBDDEF1C074FAC587CBB51218A6140BDFD11D650477C09377DB9F28C4BD6B9446E5BFA8646EE6EC373D0A7B976B209D81DCB1C0DDB3
	73B35899794A981E538188FAE0BB3ABC221D1435E05F9B553C6FA2E89F87309AA08947ED61F3BEFC947BC2C74965F7D18A99FD52B124AFG6D66E9ED9FB15336C2A0246DB3D4DF46C13B65DA5B11EE7423F996D3FFD8D9FD46FB98D38733079C7F30A5BB08E73103361E26CE74EFF5A75FF2DD101C680B317A74F3486B442C996F3B7B5CD6DEE795EC18AFE17EDBC6F6B87FED9D70313105479BB5797AF8A37FB4EBD3F31A6B122D817E4D418A38FEG6574745FAE22204D2210F6F1D0DE9102E37A65E3D40F2DBD
	9D7A9D7713943F6EF468F75C3CF67E9DB7996AFADB39ED5BF19E77AA3E1535E738AA0ACBGDC32566E638A3715D9ED9C77149FF1E5D0EE55F0B154376D05F2D49BDF0FCF3B08F63622F56FC52E59FC1B19AD30B9EA6F689C0C67D7BC74DBAD709B2B2D4383863A2ED0DE2D61DEB770B5CC50F0FFAC61FDCB4FF01C18457B56EB386FE5713ED68D37E3AF47AD825C7AB3F87769962A9F2EEFFCA6F83A45B11396DCB7C9AE510D9F1A0B2BAA4ACACD15C74BCAD89C3BF8CC9F4756ECB5C70CE34B4E2AFAD5FDD62598
	D5992B2D62366DA654BDFD166B23727E897EAA92D2DBFB5AABB99A04C1AB55EE4CDE4F2D4AD07B2FBF4B6DF59C9DF31F95F49CC03715FF07899C60F93058A716906B1C0630770C923BCB8CCE2B7A7D599A0C3B632EDA52591C0B2B791C2B20BE6D9C4DF3D46B4FB72E650F8C4CB7E77564283E9EEDA0661C027595936A115E36D4FD3DE3C2FD3A2EBF636D877BB9EF9BFF90C71C4F14C53CC3E5BD47F872284F15BF5CC5D6A51725B3D9787C0D20ACFCE81E09AC30FE4FFD986C772B6BB369475632BE1B4735CB4F
	717D0BB251FBA2D8C65E5B39CC7C6FC004D81AEAE3B61E1C4FE32D62F6AE732554276B0772BC8DF79745C5C3B9DE437D126256C0F91D06FB1466F2334E034DBC4FF15BE8CE388E4A56A06E83FE2E7AB55CDBD47F45C1F965F92E576EE947BCFF02D63E1F16B1474E4ABB5BE72BFBF02C5969370AD94FB11BEF5CCC868E1F47F7412F481D228F4CBCE695F85D597311FD9A8735A34940D3DA47E8A673C19ED238FDAD83DC930083E09EC0CC8777953E6B8F6FD77C025A1B9550EE3556761B6D6FCC181F7A8274930E
	A40EF9E42F226B3F165AA672BE583077F97FE48F6E59006CD6D81BB7E9FCDC87752DDA1BA78D58C68197021D0FBF5E44E1225F276DD31B26D93F4D0447AE2035208FC201FA1E67437BE5751D4D3F0B5E01CD847C7A7739CE3F727760FC3F481AEABEFE6FA0B542FC464E514C92751D0DF7F86FD463F13A793338B3B89E626A20FCCF8A9B0F692E9FDE555B115C078A8DBC7E899D3B351349FAF7E7E8BE54FA8B57A08A70B11DBCBEDA91F8971E97056149DFCFG89FE25C774AA638CCB3F5E2967233E6A766E7635
	5FE63FFBE65D0F79ED7B2AD1CF2E688C0DA38FD37C6A4E50B872EB1A2DCD003A7AAE1E5F790860790A01F2BC40DA00A4005CEE70AF3A35BBE363E5E41ADCE1376C31C2B06A5425DF98FFDF283E3DBB0F4FEE66B66D69EFFC11AC3BBBA9A552F72B7AEE75DEB0FF3E380B6299FF398664EF815445822C86D8873091A07F82676F42F8A9638F454122A802FD08FE3D634C21EA8822B1C0E23CB5E7F35E0AE12CBA000E8BC1FD6C00774005F03E78C6EA6366DF887545B795A1FE5105D0DF7CBE937745E321AE77A237
	97CA3EFEED150BEC2EA8753968E224B836B100F7652F4381106C0213294086415BA1BBA57B382AD72ED1FB08E7BF6EA257EB0B823826C332D73A817AEFF2CF2E1316513E27G12343E7F97405854A6FBE95200452659631AEE3F847A93E09EC074A576DD0F75FBE6B8323DF8115A67B8E83F7C92EFDFBCB2D1BF1F2476FA9D54EF4031298EAB7EE0BA9D3C058475623E9E0855FA188E433235A7F836581CE6A5FC8C5CC2FD009EE86F686174AD69C7FEA65C4E01B2535BCFE393F54C00E2A2917D284A1A6A526A32
	B26EC74553F9D03FAE6861BEE932926A13EEBB2877A98F79C3FDD264A7D85F6B8F75C9D3A778247779C3FDD25E1F7172F2DA10176876FDD370D2F16C68B1708A71217A13B91B02FDDFF796C56EDBD1867EE48DAE04B66F494CE05F3B8D137A3600E0946F31F8A541392754D48E7FAB306FBB34BDA1545E2B7F58F882CD57B11D74CA9657C946AFBDCE1ADECECA59CB426B2415BD6C6C95DE8E5A2A4228CD2A1F5D3DADC863652C1952487A0EE1DAA48C3DC927D87DC308D6F55FE95CA2B53774685F7713887690B4
	5C99187B3CEDD834AB5D422056677DDB540F6379BAE6A6791D52DBB8B85E0383B2259B748AC497B4F48FB25DF4281A90EE63AAF56F4B2B6EFC8C7F529D1D0371FFB7091CFF7D94891FFF7D8C897F7D62F3E46ABB0C0F1148F79817927D9DC6366F5D8538B79DA0FBCE0085GB1G4BAEEB5F0AD37690EA6F42583AE647A066FF71563B42675FE9084C2B49B07DDD799283664EEC322248AE9C1F630F507B630A6C21C8062BB2609AF92541A105B5D57135B497629487A81A45D83DB4FEDC83FCA5DD66F1D7964D0F
	24DD01186D0A169FA96131DD1D06BB162645EC9A6EFE1AB30903724AAB5C469A0AE84C261533E8B9D9ABAFB45D0E2C61F8B31767AF5F569CB30D5B986F1F2431F361D5D8372BFCBDB65BE9EC83E52B06F35A793A75DC65B64D9F40DC387EF604AE47EA3E585C595D56E83176F5B5F61E37DA9BAC1DFD162E3E7666662666A6567F07010AA97A371E6FEC3BF23E3DFB42886D4DB0AC770F2A4739FF3494E8CA3C4AF3867F3E957D56995DD96A16942758C03D93E6D3BF9A67B635301767A6FEC76F6FF5C0F988E0CE
	AF0F37FE03C7C055671BC6CCA4F27E232C8A34F9F535EA679AF7867B25FF30A7349F33838E502F72A09882B0F565D5A561F5FFE4FBB070B7AC3177CF6C8A4E17F4EEA6F42A3F3724687D4CE860BB36F7EA79E5FAE82E917D2CF8E81BGC0BE984DAC84A885E8826881F0824C8108860883D8813096E0BD40C6005CEB23197B2EF13D75C4965A41C8B11D1AA0244A4BAB683FC97796C456DF760269751733005E69833D53BD6ED13FE50D96506F5C8383BED15313578BF2B5DC1B2FC376C9E89559FA7B32A37B3173
	299E2B81BE1B2E71B33537E4FA9F7DA455EBF85F4D837D18CFFDDF897729632EB11FFAA5BC932E31EF494C7FEDCF1B4E7F65F77F71FD83744D688B7A3F463ED07FF7E9D6F87FB71E6A53E2E8DF5547790A22F787348FAFD649077B443E131CA8527B147DFD0C66F9DA5FA57993FD7BE5D4E7C5C3FD0C56E637FDE26E65B96A8FB23EBFEE1F0EEFBD0466C256409CF96ABBBB974747270BED937B425D63E81B36DF685D9256EFAE6D3731EFE636A3371F7F6EDD9F3D0B3D2FBFB4D7B0C471E57D21390257A41EAB28
	033A457DDC77A750B8A3CD984D5C192AFD4B20B8A3606CA97C9CBCE80CFC5E9EB1CEFF5E1EB0067797E20D137D052F98437B8BDFB76A7D8506DBEEC4399E84BD512335FBD643DD56F05F23E3A26E1264D006B47CAACA9762DBC51C1063FFCC699A23389E9577328EF7D945A598D1E7G4B90C2733E2F9829CCCBCEA7AB7F1A36115C20C2347578AD25D17288BA3A7FC743A15DCC56FFCDE395F68ED2CACCE49A7DDED9D6425ADE40DFF518157D36DE32DC6E526465EBA55C7E56009CF884FEEF6D2FA55C06AE9538
	8D1D17CA737BD88648350DE64E4E1E99D134D5FECF6584799DB14D04DF66977C006A56F05F92D09BB15A15A2FE972A58466D7C1FF4F15BDFEC330B5B9E23F1993B4F921F17CD7758A93945C6765B22041F274FD13F239F668C58346F9065081B8F6545B66EF70A43C4538BEE5D7B6B521D0F2FF2E775BBF2338536C74201AC2245177498B23CB1109D4473E348E34B125B53475F1F36DFF8FD1CFA887B455AE6260F63EDDC9FA753B33046962A0FD352685930056A63FBD339BEDE8FF59DF6AE8363C5D3517D34BD
	D27EAFEE9C751B02AEAF4AAC61FE48CAAAFBAB215F9A3B16FFABCF67ED8917651F523C82134D3F9D62721A6660726A237ED2A114CB9D1C563F2686E5F4F3DA70FDC16A1D1757A76F689C0C6785C5BCAE2AF1F0FDD10DEAC6DD73874AE72AAF58DEB452F56D0531069CFCAF6EAD183C97DE7CD42E5E577957A26E7BC5C31FD85C8F2485A0CB64B43D38BBC853E041CCE9E2BA7105A27E6D3BD464F9463F94056699DFA37ABC6161BDD367997B76046699ADA97A7A7B77C44AB3323D7D7114E7A09C303F93F3E473CB
	40CC6C6FC93A4FCB36CF3699167BFE7AF2ECDFD33E01985A4826A12AE85AC87A98EA22008728G37A0A546B7CA683736F0A505EC3778FC52205B2448A67E2BD78A49E71712CC83DE592521F94F3CA86879B1DE3A758682AF3DD38FBFD682355993F66B3B0FD57F572A7F3868CBA51B5A48CEF40E3BC4AF4D49098E3CC3297E21009632450397F551BDEFA3E9BE7AC877A9636003137C01CE7CE5B61FA2BAACBC8B851630007E1245910C100F348A3EA1853D3463D6126646BFE9C03633EA27609EBC42D2FF47F1E5
	C80A85A9A504AAAD20B70F520B231835D1723E19333F684C2B4BD2092105ECF54A0223C530C3B057C636A8C3DE51B7A4BB9D814FA9B6D046983FA71086436D5258C7E64DF8D3CBA3FAA470487C6CF559AD86F05F3D6488ED093100D9EB61E89C923CB01568EDD370CFB4485EFDDA5D47F6AB87DEB87DD62F012428CC6252E786BC8A5944BCB918BE078E0ACC1BDB00EE65E73FFFFE494F6B1256C3279612CEFF2182264E8D36EA773BEC22776E584F3E1D39B07113C8198759225B1A23G3FDE416D435FEA0141CD
	A13C71AA0DC473E71EAF7B6C13FF3EC2C98CF6D548BBECB3206B11C1D1F69D318C8C00F7218DGBB83433F2D8D0FED4CA1ED36876EAFB873529BB61024A6126AAAAB23BF4B694F8A7CD9CEB16594D3DE815B13C4267C97DA9F60BCD3712305BC14950E5EB4A038A467B83CB45124C7G71FB825A22A361ED54B3CE3CDED91B7A1F0F279AC89A1CCE96A900D4233B8FD26DE507B595A285AA10DC98D1E39868E4DBA609A9E233A74B9B2237610F827C3103B6AB24BFF74D1AFB6C75F610EBFCB1CD647590DB213DEA
	8F2437D0239931E8B8D1475A3016AB8672B0B030D047D7BF45222EA39178CA7D47F2539F161B4AA0B798B7E52810B211EE0564F0ADE8426B1535A62DFE2E46970EE1500D1196264DBF691E1E8A2E82C6F639AAA719A05587B1ABA959DCE67FF517D930A9E67C83DC6C50BB852AA619DAD5A7CBB11074ABA4EFG2F2536C3F46D65ADC113EF2358CE11DD6046B415153CB5815760F7BB1CE2BBD7EAD612BB217A2CB82E6273A66099852A9E24E4F8C09DEEE316CDF41878174CFF66ADE8E3DBE05148BF25651FB47A
	CD45AE3FCFB159C4276C9EC40BAD189AC185C394EC32F8C581B1B651641450C6167CB3B37BBEDE68E45FABCBEE696F56A45F1593710E668637FA17DE8E3DDB73C8A672518478FE1967E1FF3F95F30E113EA5F6398427D3911CA39637833FABB60CABE270DB5692BA3E0F37D1ABD95D13742E66302E4F7F83D0CB8788CD3C08C6269BGGB0D0GGD0CB818294G94G88G88G510D1AB0CD3C08C6269BGGB0D0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GG
	GG81G81GBAGGG609BGGGG
**end of data**/
}
/**
 * Return the JButtonAdd property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAdd() {
	if (ivjJButtonAdd == null) {
		try {
			ivjJButtonAdd = new javax.swing.JButton();
			ivjJButtonAdd.setName("JButtonAdd");
			ivjJButtonAdd.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJButtonAdd.setText("Assign to Scenario");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAdd;
}
/**
 * Return the JButtonRemove property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemove() {
	if (ivjJButtonRemove == null) {
		try {
			ivjJButtonRemove = new javax.swing.JButton();
			ivjJButtonRemove.setName("JButtonRemove");
			ivjJButtonRemove.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJButtonRemove.setText("Remove from Scenario");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemove;
}
/**
 * Return the JScrollPaneAvailable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAvailable() {
	if (ivjJScrollPaneAvailable == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder1.setTitle("Available Programs (must belong to a Control Area to be listed)");
			ivjJScrollPaneAvailable = new javax.swing.JScrollPane();
			ivjJScrollPaneAvailable.setName("JScrollPaneAvailable");
			ivjJScrollPaneAvailable.setPreferredSize(new java.awt.Dimension(404, 130));
			ivjJScrollPaneAvailable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAvailable.setBorder(ivjLocalBorder1);
			ivjJScrollPaneAvailable.setMinimumSize(new java.awt.Dimension(404, 130));
			getJScrollPaneAvailable().setViewportView(getAvailableList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAvailable;
}
/**
 * Return the NameJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameJLabel() {
	if (ivjNameJLabel == null) {
		try {
			ivjNameJLabel = new javax.swing.JLabel();
			ivjNameJLabel.setName("NameJLabel");
			ivjNameJLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjNameJLabel.setText("Scenario Name: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameJLabel;
}
/**
 * Return the NameJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameJTextField() {
	if (ivjNameJTextField == null) {
		try {
			ivjNameJTextField = new javax.swing.JTextField();
			ivjNameJTextField.setName("NameJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameJTextField;
}

/**
 * Return the ProgramsScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getProgramsScrollPane() {
	if (ivjProgramsScrollPane == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Assigned Programs");
			ivjProgramsScrollPane = new javax.swing.JScrollPane();
			ivjProgramsScrollPane.setName("ProgramsScrollPane");
			ivjProgramsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjProgramsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjProgramsScrollPane.setBorder(ivjLocalBorder);
			ivjProgramsScrollPane.setPreferredSize(new java.awt.Dimension(404, 155));
			ivjProgramsScrollPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjProgramsScrollPane.setMinimumSize(new java.awt.Dimension(404, 155));
			getProgramsScrollPane().setViewportView(getProgramsTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProgramsScrollPane;
}
/**
 * Return the ProgramsTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getProgramsTable() 
{
	if (ivjProgramsTable == null) 
	{
		try 
		{
			ivjProgramsTable = new javax.swing.JTable();
			ivjProgramsTable.setName("ProgramsTable");
			getProgramsScrollPane().setColumnHeaderView(ivjProgramsTable.getTableHeader());
			
			// user code begin {1}
			ivjProgramsTable.setAutoCreateColumnsFromModel(true);
			ivjProgramsTable.setModel( getTableModel() );
			ivjProgramsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjProgramsTable.setPreferredSize(new java.awt.Dimension(385,5000));
			ivjProgramsTable.setBounds(0, 0, 385, 5000);
			ivjProgramsTable.setMaximumSize(new java.awt.Dimension(32767, 32767));
			ivjProgramsTable.setPreferredScrollableViewportSize(new java.awt.Dimension(200, 8000));
			ivjProgramsTable.setGridColor( java.awt.Color.black );
			ivjProgramsTable.getSelectionModel().setSelectionMode( javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			ivjProgramsTable.setRowHeight(20);
			
			//Do any column specific initialization here
			javax.swing.table.TableColumn nameColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.PROGRAMNAME_COLUMN);
			javax.swing.table.TableColumn startOffsetColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTOFFSET_COLUMN);
			javax.swing.table.TableColumn startGearColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);
			nameColumn.setPreferredWidth(100);
			startOffsetColumn.setPreferredWidth(60);
			startGearColumn.setPreferredWidth(100);
	
			//create our editor for the Integer fields
			javax.swing.JTextField field = new javax.swing.JTextField();
			field.addKeyListener(new java.awt.event.KeyAdapter() 
			{
				public void keyTyped(java.awt.event.KeyEvent e) 
				{
					fireInputUpdate();
				};
			});
		
			field.setHorizontalAlignment( javax.swing.JTextField.CENTER );
			field.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(1, 99999) );
			javax.swing.DefaultCellEditor ed = new javax.swing.DefaultCellEditor(field);
			ed.setClickCountToStart(1);
			startOffsetColumn.setCellEditor( ed );
	
			//create our renderer for the Integer fields
			javax.swing.table.DefaultTableCellRenderer rend = new javax.swing.table.DefaultTableCellRenderer();
			rend.setHorizontalAlignment( field.getHorizontalAlignment() );
			startOffsetColumn.setCellRenderer(rend);
			
			//create the editor for the gear field
			javax.swing.JComboBox combo = new javax.swing.JComboBox();
			combo.setBackground(getProgramsTable().getBackground());
		
			combo.addActionListener( new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{
					//save the edited cell
					if( getProgramsTable().isEditing() )
						getProgramsTable().getCellEditor().stopCellEditing();
					fireInputUpdate();
				}
			});
			combo.addMouseListener( new java.awt.event.MouseListener()
			{
				public void mouseClicked(java.awt.event.MouseEvent e) {}
				public void mouseEntered(java.awt.event.MouseEvent e) 
				{
					//need to populate the combo editor to the program's gears
					userWantsTheirGears();
				}
				public void mousePressed(java.awt.event.MouseEvent e) {}
				public void mouseReleased(java.awt.event.MouseEvent e) {}
				public void mouseExited(java.awt.event.MouseEvent e) {}
			});
			startGearColumn.setCellEditor( new ComboBoxTableEditor(combo) );
			
			//for the gears, just use a default renderer
			startGearColumn.setCellRenderer(rend);
				
		}	
			
		// user code end
		catch (java.lang.Throwable ivjExc) 
		{
		// user code begin {2}
		// user code end
			handleException(ivjExc);
		}	
	}
	return ivjProgramsTable;
}
private LMControlScenarioProgramTableModel getTableModel() 
{
	if( tableModel == null )
		tableModel = new LMControlScenarioProgramTableModel();
		
	return tableModel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	//make sure cells get saved even though they might be currently being edited
	if( getProgramsTable().isEditing() )
		getProgramsTable().getCellEditor().stopCellEditing();
	
	LMScenario scen = (LMScenario)o;
	
	if(scen == null)
		scen = (LMScenario)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( com.cannontech.database.data.pao.PAOGroups.LM_SCENARIO );
		
	scen.setScenarioName(getNameJTextField().getText());
		
	Vector assignedPrograms = new Vector();
	
	for(int j = 0; j < getProgramsTable().getRowCount(); j++)
	{
		LMControlScenarioProgram newScenarioProgram = new LMControlScenarioProgram();
		int progID = 0;
				
		//program name needs to be converted to id for storage
		String name = getTableModel().getProgramNameAt(j);
		for(int g = 0; g < allDirectPrograms.size(); g++)
{
		if( ((LiteYukonPAObject)allDirectPrograms.elementAt(g)).getPaoName().compareTo(name) == 0)
		{
			progID = ((LiteYukonPAObject)allDirectPrograms.elementAt(g)).getLiteID();
		}
}
		newScenarioProgram.setProgramID(new Integer(progID));
		
		newScenarioProgram.setStartDelay(getTableModel().getStartOffsetAt(j));
		
		newScenarioProgram.setStartGear(new Integer(((LiteGear)getTableModel().getStartGearAt(j)).getGearNumber()));
		
		assignedPrograms.addElement(newScenarioProgram);
	}
		
	scen.setAllThePrograms(assignedPrograms);
	
	return scen;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getProgramsTable().addMouseListener(ivjEventHandler);
	getJButtonAdd().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getNameJTextField().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMScenarioProgramSettingsPanel");
		setPreferredSize(new java.awt.Dimension(420, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(420, 360);
		setMinimumSize(new java.awt.Dimension(420, 360));
		setMaximumSize(new java.awt.Dimension(420, 360));

		java.awt.GridBagConstraints constraintsProgramsScrollPane = new java.awt.GridBagConstraints();
		constraintsProgramsScrollPane.gridx = 1; constraintsProgramsScrollPane.gridy = 4;
		constraintsProgramsScrollPane.gridwidth = 3;
		constraintsProgramsScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsProgramsScrollPane.weightx = 1.0;
		constraintsProgramsScrollPane.weighty = 1.0;
		constraintsProgramsScrollPane.insets = new java.awt.Insets(1, 8, 10, 8);
		add(getProgramsScrollPane(), constraintsProgramsScrollPane);

		java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
		constraintsJButtonAdd.gridx = 1; constraintsJButtonAdd.gridy = 3;
		constraintsJButtonAdd.gridwidth = 2;
		constraintsJButtonAdd.ipadx = 44;
		constraintsJButtonAdd.insets = new java.awt.Insets(2, 12, 0, 7);
		add(getJButtonAdd(), constraintsJButtonAdd);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 3; constraintsJButtonRemove.gridy = 3;
		constraintsJButtonRemove.ipadx = 20;
		constraintsJButtonRemove.insets = new java.awt.Insets(2, 7, 0, 24);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJScrollPaneAvailable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAvailable.gridx = 1; constraintsJScrollPaneAvailable.gridy = 2;
		constraintsJScrollPaneAvailable.gridwidth = 3;
		constraintsJScrollPaneAvailable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAvailable.weightx = 1.0;
		constraintsJScrollPaneAvailable.weighty = 1.0;
		constraintsJScrollPaneAvailable.insets = new java.awt.Insets(4, 8, 2, 8);
		add(getJScrollPaneAvailable(), constraintsJScrollPaneAvailable);

		java.awt.GridBagConstraints constraintsNameJTextField = new java.awt.GridBagConstraints();
		constraintsNameJTextField.gridx = 2; constraintsNameJTextField.gridy = 1;
		constraintsNameJTextField.gridwidth = 2;
		constraintsNameJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameJTextField.weightx = 1.0;
		constraintsNameJTextField.ipadx = 195;
		constraintsNameJTextField.insets = new java.awt.Insets(7, 3, 4, 98);
		add(getNameJTextField(), constraintsNameJTextField);

		java.awt.GridBagConstraints constraintsNameJLabel = new java.awt.GridBagConstraints();
		constraintsNameJLabel.gridx = 1; constraintsNameJLabel.gridy = 1;
		constraintsNameJLabel.ipadx = 9;
		constraintsNameJLabel.insets = new java.awt.Insets(11, 16, 6, 2);
		add(getNameJLabel(), constraintsNameJLabel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	Object[] availablePrograms = getAvailableList().getSelectedValues();

	//also need to update the available programs list
	Vector allAvailable = new Vector( getAvailableList().getModel().getSize());
	for( int i = 0; i < getAvailableList().getModel().getSize(); i++ )
		allAvailable.add( getAvailableList().getModel().getElementAt(i) );
		
	for(int h = 0; h < availablePrograms.length; h++)
	{
		Integer programID = new Integer(((LiteYukonPAObject)availablePrograms[h]).getLiteID());
		
		//do the gears, man
		LiteGear startingGear = null;
		for(int d = 0; d < allGears.size(); d++)
		{
			if( ((LiteGear)allGears.elementAt(d)).getOwnerID() == programID.intValue() )
			{
				startingGear = (LiteGear)allGears.elementAt(d);
				break;
			}
		}
	
		//find the program name
		String programName = "ERROR";
		for(int g = 0; g < allDirectPrograms.size(); g++)
		{
			if( ((LiteYukonPAObject)allDirectPrograms.elementAt(g)).getLiteID() == programID.intValue())
			{
				programName = ((LiteYukonPAObject)allDirectPrograms.elementAt(g)).getPaoName();
			}
		}
		//add the new row
		getTableModel().addRowValue( programName, new Integer(0), 
			startingGear);
		
		//update the available programs list
		for(int y = 0; y < allAvailable.size(); y++)
		{
			if(programID.intValue() == (((LiteYukonPAObject)allAvailable.elementAt(y)).getLiteID()))
				allAvailable.removeElementAt(y);
		}
	}
	//update the available programs list
	getAvailableList().setListData(allAvailable);
	
	repaint();
	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	int[] selectedRows = getProgramsTable().getSelectedRows();
	Vector allAvailable = new Vector( getAvailableList().getModel().getSize() + selectedRows.length);
	for( int i = 0; i < getAvailableList().getModel().getSize(); i++ )
		allAvailable.add( getAvailableList().getModel().getElementAt(i) );
	
	for(int u = selectedRows.length - 1; u >= 0; u--)
	{
		LiteYukonPAObject lightProgram = null;
		String name = getTableModel().getProgramNameAt(selectedRows[u]);
		//find the program
		for(int e = 0; e < allDirectPrograms.size(); e++)
		{
			if( ((LiteYukonPAObject)allDirectPrograms.elementAt(e)).getPaoName().compareTo(name) == 0)
			{
				lightProgram = (LiteYukonPAObject)allDirectPrograms.elementAt(e);
				break;
			}
		}
		
		allAvailable.addElement(lightProgram);
		getTableModel().removeRowValue(selectedRows[u]);
	}
	
	getAvailableList().setListData(allAvailable);
	repaint();
		
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMScenarioProgramSettingsPanel aLMScenarioProgramSettingsPanel;
		aLMScenarioProgramSettingsPanel = new LMScenarioProgramSettingsPanel();
		frame.setContentPane(aLMScenarioProgramSettingsPanel);
		frame.setSize(aLMScenarioProgramSettingsPanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}

public void populateAvailableList()
{
	Vector availablePrograms = new java.util.Vector();
	
	if(allGears == null)
		allGears = new Vector();
	
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List progs = cache.getAllLoadManagement();
		java.util.Collections.sort( progs, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		allGears.addAll(cache.getAllGears());
		
		try
		{
			for( int i = 0; i < progs.size(); i++ )
			{ 
				Integer progID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getLiteID());
			
				if( com.cannontech.database.data.device.DeviceTypesFuncs.isLMProgramDirect( ((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getType() )
					&& LMProgramDirect.belongsToControlArea(progID))
				{
					availablePrograms.addElement(((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)));
				}				
			}
		}
		catch (java.sql.SQLException e2)
		{
			e2.printStackTrace(); //something is up
		}
	}
	getAvailableList().setListData(availablePrograms);
	allDirectPrograms = availablePrograms;
}
/**
 * Comment
 */
public void programsTable_MousePressed(java.awt.event.MouseEvent mouseEvent) {
	return;
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	
	LMScenario scen = (LMScenario)o;

	if(scen == null)
		scen = (LMScenario)com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( com.cannontech.database.data.pao.PAOGroups.LM_SCENARIO );
		
	getNameJTextField().setText(scen.getScenarioName());
	
	populateAvailableList();
	
	Vector assignedPrograms = scen.getAllThePrograms();
	
	//also need to update the available programs list
	Vector allAvailable = new Vector( getAvailableList().getModel().getSize());
	for( int i = 0; i < getAvailableList().getModel().getSize(); i++ )
		allAvailable.add( getAvailableList().getModel().getElementAt(i) );
	
	for(int j = 0; j < assignedPrograms.size(); j++)
	{
		LMControlScenarioProgram lightProgram = (LMControlScenarioProgram)assignedPrograms.elementAt(j);
		Integer progID = lightProgram.getProgramID();
		
		//do the gears, man
		LiteGear startingGear = null;

		//find the start gear
		for(int x = 0; x < allGears.size(); x++)
		{
			if( ((LiteGear)allGears.elementAt(x)).getGearNumber() == lightProgram.getStartGear().intValue() )
			{
				startingGear = (LiteGear)allGears.elementAt(x);
				break;
			}
		}
		
		//find the program name
		String programName = "ERROR";
		for(int g = 0; g < allDirectPrograms.size(); g++)
		{
			if( ((LiteYukonPAObject)allDirectPrograms.elementAt(g)).getLiteID() == progID.intValue())
			{
				programName = ((LiteYukonPAObject)allDirectPrograms.elementAt(g)).getPaoName();
			}
		}
		
		//add the new row
		getTableModel().addRowValue( programName, lightProgram.getStartDelay(),
			startingGear);
			
		
		//make sure that the available programs list is not showing these assigned programs
		for(int y = 0; y < allAvailable.size(); y++)
		{
			if(progID.intValue() == (((LiteYukonPAObject)allAvailable.elementAt(y)).getLiteID()))
				allAvailable.removeElementAt(y);
		}
	}
	//update the available programs list
	getAvailableList().setListData(allAvailable);
		
}

//This is what tries to fill the StartGearColumn combo boxes with the correct gears
public void userWantsTheirGears()
{
	int currentRow = getProgramsTable().getSelectedRow();
	javax.swing.table.TableColumn startGearColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);

	LMControlScenarioProgramTableModel scenModel = (LMControlScenarioProgramTableModel) getTableModel();

	String programName = scenModel.getProgramNameAt(currentRow);
	int progID = 0;
	//find the program
	for(int e = 0; e < allDirectPrograms.size(); e++)
	{
		if( ((LiteYukonPAObject)allDirectPrograms.elementAt(e)).getPaoName().compareTo(programName) == 0)
		{
			progID = ((LiteYukonPAObject)allDirectPrograms.elementAt(e)).getLiteID();
			break;
		}
	}
	
	Vector specificGears = new Vector();
	//find the appropriate gears
	for(int x = 0; x < allGears.size(); x++)
	{
		if( ((LiteGear)allGears.elementAt(x)).getOwnerID() == progID )
			specificGears.addElement(allGears.elementAt(x));
	}
	
	startGearColumn.getCellEditor().getTableCellEditorComponent(
				getProgramsTable(), specificGears, true, 
				currentRow, LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);

}
}
