package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.device.Series5Base;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 11:06:58 AM)
 * @author: 
 */
public class Series5SettingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JCheckBox ivjJCheckBoxSaveHistory = null;
	private javax.swing.JLabel ivjJLabelHighLimit = null;
	private javax.swing.JLabel ivjJLabelLowLimit = null;
	private javax.swing.JLabel ivjJLabelMinutes = null;
	private javax.swing.JLabel ivjJLabelMultiplier = null;
	private javax.swing.JLabel ivjJLabelOffset = null;
	private javax.swing.JLabel ivjJLabelSeconds = null;
	private javax.swing.JLabel ivjJLabelTickTime = null;
	private javax.swing.JLabel ivjJLabelTransmitOffset = null;
	private javax.swing.JPanel ivjJPanelPowerValue = null;
	private com.klg.jclass.field.JCSpinField ivjTickTimeSpinField = null;
	private com.klg.jclass.field.JCSpinField ivjTransmitOffsetSpinField = null;
	private javax.swing.JLabel ivjJLabelStartCode = null;
	private javax.swing.JLabel ivjJLabelStopCode = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextField ivjJTextFieldHighLimit = null;
	private javax.swing.JTextField ivjJTextFieldLowLimit = null;
	private javax.swing.JTextField ivjJTextFieldStartCode = null;
	private javax.swing.JTextField ivjJTextFieldStopCode = null;
	private com.klg.jclass.field.JCSpinField ivjMultiplierSpinField = null;
	private com.klg.jclass.field.JCSpinField ivjOffsetSpinField = null;

class IvjEventHandler implements javax.swing.event.CaretListener {
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldHighLimit()) 
				connEtoC1(e);
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldLowLimit()) 
				connEtoC2(e);
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldStartCode()) 
				connEtoC3(e);
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldStopCode()) 
				connEtoC4(e);
		};
	};
/**
 * Series5SettingsEditorPanel constructor comment.
 */
public Series5SettingsEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextFieldHighLimit.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (JTextFieldLowLimit.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC3:  (JTextFieldStartCode.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldStopCode.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G83DE2BB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DDCDC6519EF549831C5035B34621A8FCCE2CCDB5A455D2C663639F729F53B744AF65D3BF42B5DECCB37E9B7FB9B5D74A7FBEF5A1BDE8F1F43D78284C2A099E092D109928281A381CCA0108891A38D98090DEB866640CCB24CCCE68EB0A4287BBC6FFB1E771CB91C8122561F7979F0667DBF6F7371FE3F6FF35E9925122A184C543492C9CA4D1454FF2F26CAD27728A45957367E8F42327BCAAD126E
	5FFE20A2A92DB5831EE5408BBED4EAA914521B32A1DD8B6908352516BD7039D85AE36D9B048FE16C54G2FE668AB7FEFF6BB0BD3490E05251BB272613993289E30812018BE345F1BD12662C1C86FD4BFA32526C91263771867590AB29D3EC94D578C3CDE206CF318E7B4432A72E38143BC0F4273095F733CDB6119433C3B3525D94DF7712E35D24A7F2CCC5B0136F47A097A4EF1D9C5FD5A24B52CA25225676BCF43332D6F4A6871E16FD85D305D6371FA94F958D96798129DAE456B2FD39F8EF942B5AC3734744B
	FE179CF825DFD69417E7B4F00AF17B6C9E598D65E17EBFDF194DF4037F107675A3362C926B2C86BE37838D76D3997EFEC71B6A6312ABEB2564D5C5E14B302E1F7B7EC43FD606F57DD919918328BF0A35894A15C13A8F087B72C4867912B8C03EACAF473C99EF17DA2CC0CDEF93AE6FC53C97528E206937494757DEA49F872FBFACED9B6AC8AB886363123739EF9B5E56FC5C92674AACE77DE6DFBA367DB634FB3E54D28FB4G24GAD815A477A6BBF263E844F0EA12553BBB66675F478FCA75D8E7F1BDE076C064F
	ADAD40D131F37230576F10A45EA7DF2A2CB57107A876FCE8FFC462E5FBC722BE94FB6E1B12223C1A16E9D236F5BF3425452BBA124EEBE5333D0375F6288A6BAD8570D4668B7098FE1C610EF7B41C3755AA5697B09D85BC6BA0557360CE7225B7E325D4585019E6B171A57A11663435035C0778C14D97DE477F414660964033AF14DAECC04DC003C0418BD447363446396A388746005FE3F7770F8F796CFE6564384B6D107DEADDFFA5E3363AB6A3AE377A0665F83D2CED543751463FB8E9DAAF9B573E183664822F
	0F588BDA3DF05B7F9C56F6620519FDE327E843DDECCCA4DF60ED18AEF0AB43376AF05E362B5958423474AE0CA3A01E7FC916BF61DD1EBFC76879A543B769F02E678A43D9797FGFA00F87BFEA98B674684C8EF825A8ED4F29146B0D09F1063A23571153443739CC7FD3A7665325569257014030A5F5E6389A8F64F30FC5E6EF759075CF2C0D2739CCA47FA9D8A11BB424677B058F8DD7648FE3B6272FAC03BFDCC064E9460F9DA53315F8F7BE5469DF00D49E0E04CA7115E4E74E2F83A6D8165ED1F432E48DA8E4E
	FFA39D6BE442FE9EB402829C1B97313EBA581C3B90BE47DC243A0A37A0368D796FD13DFCAFFD97BC5F004EAD3BDBDA3ADC4368065DBFC5FD34314A864FEA483F9A287ABF6189D47DFE29A596287ED229E57DA59A2FCF8FD23F6C5A718D2976079D267D92294D4E7B58005D58AF3B183DE9401782EDG5A88349D48BAD4EAE9833AB8C46B6ED7AB97287A50A733BE0B3EB1BE7A7E9E77FB3EE3AD56A4EF35418E161F7B12A8FC4989232FC8FDE62A63A1EF1EFEA06DB041AC06B42EC13C1D6BF2F02CCCC3BDC5832D
	9F2236CE4D3071B1F0529E1029573F65078EABBB341667F97FFF866A13A7E40F52A57BE40F83BE8454BE171919230D07912F682BF98C47793193BAECC060C519C59C879D7A51C57CF24637AB0A5FB5B42E483413907C7D1968CBA0E46A8D28E52D4F4532BE8EE55C8C34CD1475EB19453AFE5D6B75DE99778D6008D547C78A5B87648FC31F9826B6AC5B0B7B3AEE11BE40529A7FEB6503B750E673A134953AA6FC77D33711EE68B3C552CF7796CAFCDC262B7D51A2F8DB984F87FEC5834583ED96653F3AF750C46E
	5372135B78823321579FEEBCFD9C62FDE3D9D6783E3DBA4B3CEFFFAD4B3CEF47E54D5EF757E71975DD4E7B2BACAC23F752239B8B0FE661FCBC6C75F8E4B6A00508BAEFBCBD0065F0F9DC4ABBAE47282C743B2E49F0DEF0E01F9EE4F3FEA1FC2EF6501CBC799C4D4955573EAA453FEFB55D4FC55F5F1AB66D60FDB14A214D495C273F366098135D7298D4D427F75C23F07CB1361E28F8A01DE13F73DDC5591AB383D00727DD325B51D0BB8CA3DF29E3FF79A2D6F359BEE18F5685A6212A6AD8BB54F5A27794FEBCE8
	BBF46838AD7C2922F6446517FBBC3EF1D2F6707C41FA354F94CBABF77A0D76BF8B879A48FE4B0C729F798C4C9F91655FDFE3345F789958EF9476FFADCD996CF73BC61D3D2EB11772197683792520410FDE6F64E765C68377A1225FD8977D8ACEA030A17CCC7B44FFCC986B22DF717ABEA3B79A79BABD1C07F3C89C4CAD0F82EDF450BA7DFAB672E91DD6B7A31DDE371BCFFF40EB17F1EE1B2EAA514DD7A98E9EE759A978EB6760A74D4117EC3373EB67608F4E41DFB887BFAEAC1F57D3D38E2E4B81796A388C478F
	5C483CA1CF5199A2E71701F75E6E9E574EA61DB93857F46B76C09CBF1AEBBE2F3765623E670A505F1E1BA56199036B646BD01C9DF57A7C2EB158B26226CE0DE765E56A63D991D0B60CFDA540F3A390177DFD43DEF84E96B70A98C1DD36FD9477CA8159FC40EAC738BE67882F9B1E6F67A2DF922437044DF7371572650F429AB99AAE5F9B95142F8B72DCB449B7BD0A793CA22EF5D8E00B9876A4ECDCEE4D20737A1B057A381CA350E43A86A3450F2AB115D1E39CA3BA9F7526G1E612431732DDC7D9E37A390708E
	3B58B1D2EDDFD71EFEFD6618A7AF971EB0CE83CA8FECFC0254C714BC74954F19AA2C367357DFFECD1AF9EE660DC5C7084EC9B6BB8CF8F1CA6906EE56A936AF67DD7224C4757041E9FDECEFC34141343CB075D066D46320CE2D9E6262DC195C27FBDAF03DB10BD18EB3A76BC647DDF5630A8B8F34F6454EA6A45E31F88CF0BC936B85ED871D946B3B98E473932493001201F85C21E7DF3EDA975BG2BF715DA78F82D19500FD7A760D1C06B00781C9B378F7B448DCE2FEA1DBFFAF097EFA343DCCA75383238DD351B
	784D15D25BC7392671470802123484179AA3F6E97528C48FE6C6A0317265DB29FCED17CBAD4AE5AADFC633D979E2AFD33D448B39F840368939F5FB0DF2F8FE2A3FC2F2CA8549F581B6FD05643EDBE9A6376E8A6DE35FB9229FC37149B5E147D04295DE560DD70CE3E8FAA70BFFG1E6F26B1746FF9C53AB1F48EFA2D5DB38AB324B8AB64277339D0CD5F171FC9F321C4676D7FD6003E5BC0E79B50G109BA8D2582859B1E7FC71D4F016786235CC0A55453A55B827DBBBB7A641674DC05BF5189A131B423AAE9C63
	3134F201B7B03CD707733699E2E3002501B76021F35FDB7B0C31C21BB7B48E52E6C8F33977E648559952178CF26ABB089AAB0DEF5DF0E9E95176AFFC2D89CC69571A2017771925700C73D23C7F7D09ECF57EC970D29C2C208875CB3ED20B952889288FC8810A82DA6723F61B5CE1B6770536DB3809CF0BF1703A58E57DF8F5E81BA1E72C43A4DF289CAFF7EBB315FB0B0F5A71FB9F530724B9FD283EEA66432F76E62BBA5A2E128F57AABE1E8F91B3640CBEA826BE7C3612FC083CCA6D7F56AE449E05F452D5EA6B
	249D98E323F9204F6B72A8924D81B76D447D0F576F9AF5F988DB4430E12F1F9D5EB9761D0270F10504827338428F8A4C638AC98533474CB696040BBB707856DDEC0D5B8465137C502799F6540AD88924EB8476F085E23D10F6886C89D6E791100E915822D24456C1BA51AF62E3D3F85ECA0674F6A0EB2054528CB4G24GC5832D83CA825A8A142F40BECAA159D719AC2EA93D003901A201E201920136G650FC3FE205EF13ED743FD9817F58663F56D0CB60CEBG917A950E970E2C2A3EE875C93E33BE96469616
	3BD68965E13D8C6872A7854C7A753DA1E5A91A452F4C9BAC875940E7017A5C8D34C77DEC96D3C6427ED8EE400C716AE2833F026579FEE9816B3301009CE093A36E3ED45E3FAA3BA5133856D97B18AC51B9F5513E9CB54FA9455B59A051FA704CC11C67CD5683362963637C6BD359EA195C633427CBB946E243105E8CA4CDD07FF1CC50DCD2B6B133BDEB81EB879A507154B8F5D17871EDABB29F5FAF9421CF816F38FFD8B60E61C3C5E502272D9CBD0ABC0655221E51183CE2770F4AB366005718BC670513BFDD94
	EE1E60F55EE35557799151AF5B1B290E1CA6F5B48DD89450EA9DCF0D7F0568EB947A366EA5FD6BCD74A58216EC40393E87AB747A1A043EE7457B6D9493FD5913B09FCD06623CBE86F6611EA7A85203ACBDA56C2D4D447A76796591596F179DBC3CAAB9A731BFFD074D29757039ED127679EFDAF19DF05B273C6338E084B15FA3D67D3EA49672A681EDD67D61FD7A41C99CCFA3FE7B98B4C73FCF9E16B6B2599F087BA05D103E88B48D3494E8C310EC7EFBA54A0E59032E3171B166A1774FD2057ED5CF41FEFB0A72
	AEABE5F9DD9EC35E3FE47379C5486794F99FF4E20C010F2ABE6F246CE7E1844E2B37E2BF9AF08DDF41B7107DBE1707ED44C43D5DDF02BE1F69358F49EE4AC53C6F8E6D903CB7DD9E6806DA7FBB393FC413735BBD01B117725648C8C0D6B83F76A44ED7219C4DBA4FF34BF31A7EFE997A0AC3537FFFFDCCFF27D39E3EF2529B6C37CF485D2EG9CDE27D459E001A6CB51B9D5B62BFD270E47A3E62457729E4E01A611BD15EF7D43AE831F6479F967DC197E3C33E40A6FDD932668BC5492425F287225EB445FD5EE46
	AF917CEF1972EB84FF7708197DB6417FC61519FC1F607F59281E6F38467911026FB6359FA378163DE67CF557E80E0E1B523F87CBGFC63B5EDBCEF3DE63E8EE7DC1F091781D6EB4079B9E8E18B2E8F6D570D77D43E4CF077F563BD155F9E23FBAA51402B1E263D51F5F6460803F422407C17E9DF15AC305FDDE573478725166E8F88FB494E46A12495013D53CC7AA284D6F70074AD9558758A5237C5E0078F10BE69C342AC7BC8DF09405E2EA2FDB582DB3317748585D6DAC53211820BACA75998017DD3A549EE85
	AC63A33CB7570C5B88F5BC3CEC354153A3CF5AF0892F33CFAAF5DDB0C5F87052C2CDE3F3FD2D2D71E87DC1FE560F19541F751BF36B421E753BBF626DFAD1FDAECCBD204E2F8D17C24E10E8302565F53F4BF152BE5A4BE6EB3EEF584C764428FB49C734EFA8E5B10AC4C8EF825A7E9145A002AFD07CAAF64DCA29F741C9533BE949838753CA9628712B85DA8CC2FA501549ED26D410CD8578318B90BB90927F40BA7B7BB4FE46617C0190BE764167A4BDFFD1889F7D7DCD087C7A90BE56510B2ABCAF5BE34FD35936
	BF96A7397F32BE6C3D3B9435CCE5B7294F4777CB9B4FDE4864EDBE38CFEB736B2573E9F3AEF75D2A491D884EC70E377307C7DF10F43140AB6E513A4B43F8122A9B41E997B6091D3A450A577F6DE5F4B66E3B09E2524F0C0459E332591F4BDDAD2333E444CD94DF68A9A7ACDEE03F9658B601750E104D7A1B895BD4C5791CB7139F474F6FB37703D6B0358C3F9CC5D9DC3BE2043ED542EE46AD049D574E603750D87DDFEC2F2CC0BAC2E0FFD50CD8AC246385564D76881BA03DDDE0BF31513932F0A1E15FBBCE73C1
	35404EF651F9D1D9C8F37A97DA30DDA3A03D9AA89EE8A3D08AD0492D2516A62001DBA96F3DAC2F8F52D1C0F1C00F5F4A5F81E0FCFFABFCAEDCC4F925168570C4D9FDBFADD153E8CB2CB38BEF7CBCC8942113C55F1BF476D2C4991FE33E984FF1F456521F61BE493D86631D0E82DDBACF0C23BF19883F177DBADB336A218E5B0016AEE26362F257ACB6695E921193E4260170DC0773C454A23EDE268A7BA5FE733DE72D1F75A1483FF5916DBDBB6C66FB5A8BECFD345E866B949047963075F1G5217EEA35FCF9D18
	4BF73D7F1137057A9EF31B66BB37311A4D5131002F97F687595A3A8952DB85D64F56D6EB847897C13E382B6672A5C7674FC004668BE2CEC87F6A59E71F55FC79BBB606954097C4105D93EC6E080574FAE1F7D579DCF673F4F6B7996CEE1BE1B7035948389DBE5FCEEB605F7DC13FB6977EE9D99A551B51DE5B6DFCFCF45FEE0C430F313A1D86BC7AF60A3FFDD07EB2BC4F5BAF4FFC4384E01F5F6B137D4A94C75931C4BD87BEB5A3B6B99771BD5E97589A3F7EF62D5DB97EA506BF2A43795E4FDAC177499F87DE53
	97A816383C92570DE8C82F83CA845A84147145D20B6D0B620EE5B9463AE60DA38FF8FD3D72849C3B34D09FAF5FEFE644BD67A25ECE5F3150FA3E6EDBDF1356AF5A1A9A6EDDDB5397553B186AB36E877B994E4B57E04372AD84DE8C5023C09B013682E5C7D279F63233BDC64321F9BA9445BE6CE4EF9C29F07892D00371304B4BF6E799154D9AB9F3DE2E872C8BE8D04763F293E57ABAF12B7C856AF30909AEAEF736DCAF37D64D37C1FDBE9ED66E36AA3D5CA6B55FB67519FFC7B8395F0F101C4DA4CF93E05DC097
	6FB0166F5FC26C79D47EC275999D565E2FC64D6E8E462A796355E712C1FE33093E6D00955E896B791DC65F560654E1334A6FD51F176E8C67DB67DE3D1C22660BD01FCBCD64783A70EFD398DF99727AF9600B45C824EB94A309833984200C45251632457C9DA5177B476219F119270A0DF119FA1069DA4CEDF33977CB4D5721C99EAF013C4E45B487DB76A3368452B10BE98E7E6763334D416912F62711FD8785641297EB6BD04AE2BE6FBC897330EA176D2324A8587BC4D1590AEA34180538DD257A3F694CCE4123
	9BCFD4364D178B856F4DF137627239DD321F73BEB814AFF87A784BCF4F0646B90C7656D8C2639CC61BCD1DA82FD9B346EEEE7E904BE20C6B709841BF3498E384583E4DD194C3983FEE466F957C8D074C7817847F749EB33EA2788FDA0DB19264C7887E668AB37E52A8BA7F7E8BFEDDCCAD676865CAA96C1966A89CE90E549F1424AA094756D91B33FFDBCF4C44E0CDE67A4F6091D01D6FFF16CF733D756B2B242E9FEDCDFDCEB21F6F6322F8FF176E5256650D673354735B3AEA4D67AA7BFCFC66F2DB1E57647E75
	55794B953C2849BDF7606356915755DD23554D56031A5E6AF96965F245AFEBF2CB2C7317BBDA2B491D291AC52E6915B94BF16110D60E69BACD6F1A4A796865F2BFBF2C495DD47E497C49EA507CE9BAF223FA79BE62E7CCCE3D77EC454EE23A57035D429950755650E5F9D899300F8A99FF8576D5719E03EF81D55E949B07F8A75AE5F73B2E31ABD07D1051B34A571EA8FC85252EC37768BE9F2878346F5A4FDC77DA7F28B6F8799F288C7FDE6A1B15666F2556D51A3FF77ED6656C77599F298CFF1F7D7B157A7B6C
	3C5D1FEE4436E90639E6GA888948994F3976D9D5F29BAA0313D23493E7114E3945F0B61B72E94325FE28BDF568E5B5C5F53BAE3437BEFC3DEC5710E21FE427BEC58DE0A5747C00E3DED43BA72430A27702CAA7E879683F14BA38C66710E7BD9ECE883146B093BA886E2956F563788ECE19945C95B7EA4B4DF9F241D823B03651B06F424408E1DC0AC8652F1823B73845958A0B0391CE4733FC4D8D697495A84B65AC5323582DBDEC5325382FB1A451A22A09DA330FEA749468A4C49E2DC5BA1DD360430FD2F899B
	827BF195554134406E2C26BA089218446AF9B52685566DA6DF928476D5B759C892588EA715377A4B04F91C944F2A9758FED6FFDD10F68AEC7C844538268536DB444B238476F8A545381285E6E1F51F8269122F08581AE80F9A016DEFA77F5A84F6F3BB596896583BFBC9B6DAE06D9DA49BAB307BBAC8F67DD7682EDD4DB57A6EE5765D30E7391BE20C7FBD974F23733ABFB960D25C72C9367B6671C45FB53403FA02F7D33CBC3F185D1D04F482D0725D94FB1C5ACDF16EEDA26F833BB1EFFDB4149168D2B46D15EE
	FAC512E8DD79871BCD8A9F7FED020537217195B5EE3C7A28A6776D6373111B5F9E0B563E3FF995D37C737FEE56EC7D6858FCEC71FA48ED2598ECB01A6AA676BAE1F182FB6690E11B8576419E42EC7790465FD9A0B6A830AAF14FA96A9E0ACDBCF18C4FA2891887A87BCFE16C83B5817582DD82D200A200E2005683A582A58325GE55C8B63982876DE1A2B97BF071B2AF0B19A75658A1B307DF67D7D297F5398FE4E7EDD635CF3F6967B9D8799665A01A91FFA2FA12F095D49E267864AD758D4A8301EC01FB76042
	1DGEF2F8DE561E321BFE4F3EDBB143377DEBA47B5D64CF60E33C8213150E9C3ACB4F82FB11E788DE6E3A160D17752B92E31427C2C7986DB8FB6C03E8411371B7D3645880CD31E67A126AF857852D2F1AF005D5BD720B2553DFCAB76D7DE1E7BCF4CF5AE1D2DBC75E023B124BCF732B92D8D706E25B406FF5426597B30EBB6FBB9F3588B024E17C36C7D8D1BFF97829EA54A1B698CAD6F8B47B57B171DF315D79F93B65A0F879BC512BE163D2FA2FC7F390B2DFB09A07344D26ABFC7DE1B2B7F686351C67B994B0C
	7DA71D2D050500DB17D17C61E136E6F6C3FAE09955499EDBE8BFB9446496GBFF29935D34F892D1E625DF33553ECFEEED8263593579D01533B6A735DEC3DCD00BCC9423F6693B85E1DDE3F6B9A78E8F7E36000C6AA49751F20B5C6DACEE5DD4E30DAC8B7AF27BA2E1DB54E2E6FE319A63EBB169B6B78B7EC8E7581BE3D1C7C2DACAF8E7137436D9A7560AED515E9AE2777FAEB052F1B991684696465D4679BF6EAF5FECF7BDCFDD36F3B516F4295DA5F6476EEE1FB84AB60B5AB485ECF8BB4FB6BBB663217B70B3D
	41955AE7FE26293BB6D7CC5A31426C2EE35B1CF215A66324C89AE7F28BD64C0C911A511295749B938B583EE46D8AE30C7FF6062FDBE10C716FAA2698FFBC702AE3681D5E073BBF5EBB06FD2528AFA246E83F1A61CBE20C763F311B6C47GAF63BE32DF502A376F3CCF7D8E127A0C3AAFDC4C75416BFA3955EA3E756AB3B12C5C36C3FA39E4B5DF0A7A4CDE99CE6E66BDFAB92B1A2FDEFDF60515535E618C1A64F18336E025D9FF2A96F7G1718482D85EC0381677B04AD47426FC71EB9B677FEE47BB173B3E4DA4B
	4CB32425457C8C3923C5FF0664582E961C27C6E19FB5A8726D91588501D919CE445EF1B99427408F34642AF8370C86892FE1FECDB2EC106E1E6A308B74FBA5AD382702A243F4C7322F3630754465F62B5FB5E4F9DC9E58E2097AE8E7BE3AFCF61D5F278406FE73B9A98D27ADF5BDAD2A18ADF6549C924BFA9EC34C6A67BB0ABF1E0E3892CD2E22EC7EF20F696EB06DBFF140A8F79A96051A838E175D6D9DBDD0EB43EDFF1310FDFA2F667731CACD4FEFBB771B6B69000E6686B58D705F1103C2F634CA5373DDDDCC
	79DFC6EF4C1FDF9E50746468623C052F4D3FBEEA1F57F43C75022663166A796B706BE23ED13346CAC365FE200B693639EF2C8EFF7D1266F709AED6DB4A4EFEE16CB79CE98287C08B4FFB196555FF032F9F7FD208AAD057FB6A7400EA6355C31A0D5C2A795860F23F28537C4C2E57F4ECAF1B3F1FEF155BE67773DC4F6B5D835C0622BBBF6FEE3F317EF4376EEC7C4CB1CDCFD3470D69A9E567947EBD0178629DECFEF13BBCF2A77F65A4093E539249E28F0F431C3EF9A545BDFCEDAC66846942D534DEAEE2F3847F
	3CD877392C780F9F6FED788CEC6CAF554A74126E73DF6C7E635BCE7C235A60ED7AC2AB453C2C2B44F712F2A966D5AF305772A86655A630046BA2EEAA30BFEFA2599801953FA2621782AB28A3596D82FB2A04E48BD70B3BED3BC956AA30FB76102CDBE0FF1BCA328B84D617C63211824B28203869C6017D221272ED9558A362FEEE7B7D0435D6126C00409EA9A3D927401E92F705575FCFE71BD557F09F21E0088F4727C46709EF33F11688791E90F9EFEB4B243C920D5B336C3C4FE57E1335D9761AD2CB499ABA03
	DC452FCF2AE710013CCF92FF68DA436F9FE9E71EDF33F66E837C223037C2F77EFF6915791C7F43590B841DF9A1761AD95F0886FC353077086E9CAD6F7EA4712224B546B35DA32CBFA5833EC5587BC53BE66FD45AA709AFD89FB046F31AD87F28813C6981BADFEF2F7CA4B11CCB8F984F577C7B20EE40038F50D959417A6FEAC847893BCF16FD1218444699F6CB190D4D0027BCC0F5A9F7CA922DD70E369B5BCB7CB8DF13CD4C1D4FDAF7C42CF53F4365CD7D6C526D0757B45D181EC85D1E78215D1A1E1F155C181E
	A7CBB5BD793A3B5B5FC93DB1BD072D1A1EF3951A1E9F4F764E7AE8D3487EEE3CD25391DD2569F8EC36FD3EC14737ED1A5C43336D738DF2BF592F59E6D7A7D49D2F4CCB877FED616E23BF03560DFDCA4A8C7825DDB373F75C79640FE25F6FAAC91352FA24ACA7BBFB75C81913F8088B7A40AE9C65D22783D7258F517EECA989CFB3EDE025C0D869E1C73264094616741F2FCACF13B24084BF2276C899F84E84CBFEEEB19D0E28E8CF2257C3B8556DB7681EAB05C330C8A73FE35E720704BC9ED753A43C0282D316C9
	1E19922DBA096CB09237C1CD8D1824D09E8F2F784496AE8C4D23C8397845B59BFEF32DC511B246DC1EBAC5AA242F1E59789DA5648464E1606463F749EC6A1749B0095FG33611E354526C8F97805AFB59562E7FD08AFE9D49E13BCDA4AAF6D889905FFFD5FAB3FCC7C4979C03A1455A315E33097FFCC876FBE488E7C4E2D7AE32027259C9FFE119D03555089826C1199D0265CF22FD4B2F28EFFB0A820480E8EFA5B9FD0245D3DE869B03E7DBB5CED8FB8950CFC3476CA999E7C59D2E997E73B6D1E51437C0AC5AB
	6BC9299D17183FB8C1CB60EFB17397355489AD455FAEF8FE7F19CD6B53253453D22E5BEBF71C368FC36575C8B90A53AF871CDE37A368BB514E94E5CFE62B9DAB9D546D9345474256E150D8B8FDA538ACF247669D27219A03FE69B04B096F846AC48EC727536E87D3323FC7411FE1757A2B856F26CA65053FFDFD4F45B4A9D5ADA4D6FDF55027708A93E38C0BDCF79A3CD6FE7C5477D77ED35B56ADA0F2DA4AE4BF8582068B35DCE7474706E47F2258AFD764ADC97EA27A65436AE10DDA23C8D9A376B1179B3ACCD6
	971BCB894FD1302B0DF87DE3E054E6A8B1310463F5DF7AFE7D173F7A5BF719631A28A2D571A68251432332F76CF04748881C250482E8AFD0DFA154E31EBAC31EDD413BF61F796133C3503F3A2474317AFA7637017DBD02FF9B9852401006A350E8DB25D97F997941A01639A1A8468286190A5531709CC38F30DE45BB838B0BB27405995DA383611798339633630743B36AB60BC73AF8B6D4019F0E30E4937B7B6A47AB42EBDA91E80FF78F50733AA2688706F9915275C538E4AC422764696B1A27987D245D26A54C
	64D4A8E913D3662C13532757D818BA0EFF5ABF25B21F534A0C5BA5EA1D9CD1825C7CCC1B24D08257FD8C12613E6F30C91E4FD56B3E2715946DE26B62621A7B79735421F95A2AFA0ABB7A9C1814DE445BA82E21313A710931BA7B10427ECF83FC6AF35B599242179CD5DFEA1A14792ED44C3E67F7D61E107D1493D639A2061E13790FAB603501DEE9CF88F6F25C63F04BE7E9426F158AC358EF48D3AADE9C02F38F54B50239610325A20FFDDD5E46EEA542D6GAFC95B58B5E836956875CEEA29FC6DAEB4A63355ED
	056D73BC0E7CC1D36F66550FCC3D7B94FC8AEA7D880FD438214371DD7D797374A34DD3DC62F0CE41652E607367692250BAC5FF52BE1FF5FAC7E81D122785A1EBD33B48C3879DDC85761B6419A9D1241388BD5AE0F9791D7B039F693F8B376D5602305FD35F70107A9D23070C5F057B8D0B1B65BFDCEA29F9186ED97534624B03F077237B476CEE37E2F7DF6970B8702E7449A97EFB9A6A6F3637227EG65D1196A6F3637215F17F5B27F85D0CB8788816ADA2B09A0GGF0E7GGD0CB818294G94G88G88G83
	DE2BB0816ADA2B09A0GGF0E7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG43A0GGGG
**end of data**/
}

/**
 * Return the JCheckBoxSaveHistory property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSaveHistory() {
	if (ivjJCheckBoxSaveHistory == null) {
		try {
			ivjJCheckBoxSaveHistory = new javax.swing.JCheckBox();
			ivjJCheckBoxSaveHistory.setName("JCheckBoxSaveHistory");
			ivjJCheckBoxSaveHistory.setText("Save History");
			ivjJCheckBoxSaveHistory.setMaximumSize(new java.awt.Dimension(124, 22));
			ivjJCheckBoxSaveHistory.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJCheckBoxSaveHistory.setPreferredSize(new java.awt.Dimension(124, 22));
			ivjJCheckBoxSaveHistory.setMinimumSize(new java.awt.Dimension(124, 22));
			ivjJCheckBoxSaveHistory.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSaveHistory;
}
/**
 * Return the JLabelHighLimit property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHighLimit() {
	if (ivjJLabelHighLimit == null) {
		try {
			ivjJLabelHighLimit = new javax.swing.JLabel();
			ivjJLabelHighLimit.setName("JLabelHighLimit");
			ivjJLabelHighLimit.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelHighLimit.setText("High Limit: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHighLimit;
}
/**
 * Return the JLabelLowLimit property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLowLimit() {
	if (ivjJLabelLowLimit == null) {
		try {
			ivjJLabelLowLimit = new javax.swing.JLabel();
			ivjJLabelLowLimit.setName("JLabelLowLimit");
			ivjJLabelLowLimit.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelLowLimit.setText("Low Limit: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLowLimit;
}
/**
 * Return the JLabelMinutes property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinutes() {
	if (ivjJLabelMinutes == null) {
		try {
			ivjJLabelMinutes = new javax.swing.JLabel();
			ivjJLabelMinutes.setName("JLabelMinutes");
			ivjJLabelMinutes.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelMinutes.setText("min.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinutes;
}
/**
 * Return the JLabelMultiplier property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMultiplier() {
	if (ivjJLabelMultiplier == null) {
		try {
			ivjJLabelMultiplier = new javax.swing.JLabel();
			ivjJLabelMultiplier.setName("JLabelMultiplier");
			ivjJLabelMultiplier.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMultiplier.setText("Multiplier: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMultiplier;
}
/**
 * Return the JLabelOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOffset() {
	if (ivjJLabelOffset == null) {
		try {
			ivjJLabelOffset = new javax.swing.JLabel();
			ivjJLabelOffset.setName("JLabelOffset");
			ivjJLabelOffset.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOffset.setText("Offset: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOffset;
}
/**
 * Return the JLabelSeconds property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds() {
	if (ivjJLabelSeconds == null) {
		try {
			ivjJLabelSeconds = new javax.swing.JLabel();
			ivjJLabelSeconds.setName("JLabelSeconds");
			ivjJLabelSeconds.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelSeconds.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartCode() {
	if (ivjJLabelStartCode == null) {
		try {
			ivjJLabelStartCode = new javax.swing.JLabel();
			ivjJLabelStartCode.setName("JLabelStartCode");
			ivjJLabelStartCode.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStartCode.setText("Start Code: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartCode;
}
/**
 * Return the JLabelStopCode property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStopCode() {
	if (ivjJLabelStopCode == null) {
		try {
			ivjJLabelStopCode = new javax.swing.JLabel();
			ivjJLabelStopCode.setName("JLabelStopCode");
			ivjJLabelStopCode.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStopCode.setText("Stop Code: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopCode;
}
/**
 * Return the JLabelTickTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTickTime() {
	if (ivjJLabelTickTime == null) {
		try {
			ivjJLabelTickTime = new javax.swing.JLabel();
			ivjJLabelTickTime.setName("JLabelTickTime");
			ivjJLabelTickTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTickTime.setText("Tick Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTickTime;
}
/**
 * Return the JLabelTransmitOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTransmitOffset() {
	if (ivjJLabelTransmitOffset == null) {
		try {
			ivjJLabelTransmitOffset = new javax.swing.JLabel();
			ivjJLabelTransmitOffset.setName("JLabelTransmitOffset");
			ivjJLabelTransmitOffset.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTransmitOffset.setText("Transmit Offset:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTransmitOffset;
}
/**
 * Return the JPanelPowerValue property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelPowerValue() {
	if (ivjJPanelPowerValue == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Power Value");
			ivjJPanelPowerValue = new javax.swing.JPanel();
			ivjJPanelPowerValue.setName("JPanelPowerValue");
			ivjJPanelPowerValue.setBorder(ivjLocalBorder);
			ivjJPanelPowerValue.setLayout(new java.awt.GridBagLayout());
			ivjJPanelPowerValue.setMaximumSize(new java.awt.Dimension(340, 135));
			ivjJPanelPowerValue.setPreferredSize(new java.awt.Dimension(340, 135));
			ivjJPanelPowerValue.setMinimumSize(new java.awt.Dimension(340, 135));

			java.awt.GridBagConstraints constraintsJLabelHighLimit = new java.awt.GridBagConstraints();
			constraintsJLabelHighLimit.gridx = 1; constraintsJLabelHighLimit.gridy = 1;
			constraintsJLabelHighLimit.ipadx = 4;
			constraintsJLabelHighLimit.insets = new java.awt.Insets(36, 35, 13, 3);
			getJPanelPowerValue().add(getJLabelHighLimit(), constraintsJLabelHighLimit);

			java.awt.GridBagConstraints constraintsJLabelLowLimit = new java.awt.GridBagConstraints();
			constraintsJLabelLowLimit.gridx = 1; constraintsJLabelLowLimit.gridy = 2;
			constraintsJLabelLowLimit.ipadx = 5;
			constraintsJLabelLowLimit.insets = new java.awt.Insets(15, 36, 19, 4);
			getJPanelPowerValue().add(getJLabelLowLimit(), constraintsJLabelLowLimit);

			java.awt.GridBagConstraints constraintsJLabelMultiplier = new java.awt.GridBagConstraints();
			constraintsJLabelMultiplier.gridx = 3; constraintsJLabelMultiplier.gridy = 1;
			constraintsJLabelMultiplier.ipadx = 8;
			constraintsJLabelMultiplier.insets = new java.awt.Insets(36, 12, 13, 2);
			getJPanelPowerValue().add(getJLabelMultiplier(), constraintsJLabelMultiplier);

			java.awt.GridBagConstraints constraintsJLabelOffset = new java.awt.GridBagConstraints();
			constraintsJLabelOffset.gridx = 3; constraintsJLabelOffset.gridy = 2;
			constraintsJLabelOffset.ipadx = 25;
			constraintsJLabelOffset.insets = new java.awt.Insets(15, 12, 19, 2);
			getJPanelPowerValue().add(getJLabelOffset(), constraintsJLabelOffset);

			java.awt.GridBagConstraints constraintsJTextFieldHighLimit = new java.awt.GridBagConstraints();
			constraintsJTextFieldHighLimit.gridx = 2; constraintsJTextFieldHighLimit.gridy = 1;
			constraintsJTextFieldHighLimit.weightx = 1.0;
			constraintsJTextFieldHighLimit.insets = new java.awt.Insets(33, 3, 12, 11);
			getJPanelPowerValue().add(getJTextFieldHighLimit(), constraintsJTextFieldHighLimit);

			java.awt.GridBagConstraints constraintsJTextFieldLowLimit = new java.awt.GridBagConstraints();
			constraintsJTextFieldLowLimit.gridx = 2; constraintsJTextFieldLowLimit.gridy = 2;
			constraintsJTextFieldLowLimit.weightx = 1.0;
			constraintsJTextFieldLowLimit.insets = new java.awt.Insets(13, 3, 17, 11);
			getJPanelPowerValue().add(getJTextFieldLowLimit(), constraintsJTextFieldLowLimit);

			java.awt.GridBagConstraints constraintsMultiplierSpinField = new java.awt.GridBagConstraints();
			constraintsMultiplierSpinField.gridx = 4; constraintsMultiplierSpinField.gridy = 1;
			constraintsMultiplierSpinField.insets = new java.awt.Insets(33, 3, 12, 18);
			getJPanelPowerValue().add(getMultiplierSpinField(), constraintsMultiplierSpinField);

			java.awt.GridBagConstraints constraintsOffsetSpinField = new java.awt.GridBagConstraints();
			constraintsOffsetSpinField.gridx = 4; constraintsOffsetSpinField.gridy = 2;
			constraintsOffsetSpinField.insets = new java.awt.Insets(13, 3, 17, 18);
			getJPanelPowerValue().add(getOffsetSpinField(), constraintsOffsetSpinField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelPowerValue;
}

/**
 * Return the JTextFieldHighLimit property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldHighLimit() {
	if (ivjJTextFieldHighLimit == null) {
		try {
			ivjJTextFieldHighLimit = new javax.swing.JTextField();
			ivjJTextFieldHighLimit.setName("JTextFieldHighLimit");
			ivjJTextFieldHighLimit.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjJTextFieldHighLimit.setMinimumSize(new java.awt.Dimension(60, 20));
			ivjJTextFieldHighLimit.setMaximumSize(new java.awt.Dimension(60, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldHighLimit;
}

/**
 * Return the JTextFieldLowLimit property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLowLimit() {
	if (ivjJTextFieldLowLimit == null) {
		try {
			ivjJTextFieldLowLimit = new javax.swing.JTextField();
			ivjJTextFieldLowLimit.setName("JTextFieldLowLimit");
			ivjJTextFieldLowLimit.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjJTextFieldLowLimit.setMinimumSize(new java.awt.Dimension(60, 20));
			ivjJTextFieldLowLimit.setMaximumSize(new java.awt.Dimension(60, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLowLimit;
}

/**
 * Return the JTextFieldStartCode property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldStartCode() {
	if (ivjJTextFieldStartCode == null) {
		try {
			ivjJTextFieldStartCode = new javax.swing.JTextField();
			ivjJTextFieldStartCode.setName("JTextFieldStartCode");
			ivjJTextFieldStartCode.setPreferredSize(new java.awt.Dimension(45, 20));
			ivjJTextFieldStartCode.setMinimumSize(new java.awt.Dimension(45, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStartCode;
}
/**
 * Return the JTextFieldStopCode property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldStopCode() {
	if (ivjJTextFieldStopCode == null) {
		try {
			ivjJTextFieldStopCode = new javax.swing.JTextField();
			ivjJTextFieldStopCode.setName("JTextFieldStopCode");
			ivjJTextFieldStopCode.setPreferredSize(new java.awt.Dimension(45, 20));
			ivjJTextFieldStopCode.setMinimumSize(new java.awt.Dimension(45, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStopCode;
}
/**
 * Return the MultiplierSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getMultiplierSpinField() {
	if (ivjMultiplierSpinField == null) {
		try {
			ivjMultiplierSpinField = new com.klg.jclass.field.JCSpinField();
			ivjMultiplierSpinField.setName("MultiplierSpinField");
			ivjMultiplierSpinField.setPreferredSize(new java.awt.Dimension(64, 20));
			ivjMultiplierSpinField.setMinimumSize(new java.awt.Dimension(64, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierSpinField;
}

/**
 * Return the OffsetSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getOffsetSpinField() {
	if (ivjOffsetSpinField == null) {
		try {
			ivjOffsetSpinField = new com.klg.jclass.field.JCSpinField();
			ivjOffsetSpinField.setName("OffsetSpinField");
			ivjOffsetSpinField.setPreferredSize(new java.awt.Dimension(64, 20));
			ivjOffsetSpinField.setMinimumSize(new java.awt.Dimension(64, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffsetSpinField;
}

/**
 * Return the TickTimeSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getTickTimeSpinField() {
	if (ivjTickTimeSpinField == null) {
		try {
			ivjTickTimeSpinField = new com.klg.jclass.field.JCSpinField();
			ivjTickTimeSpinField.setName("TickTimeSpinField");
			ivjTickTimeSpinField.setPreferredSize(new java.awt.Dimension(48, 20));
			ivjTickTimeSpinField.setMinimumSize(new java.awt.Dimension(48, 20));
			ivjTickTimeSpinField.setMaximumSize(new java.awt.Dimension(48, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTickTimeSpinField;
}

/**
 * Return the TransmitOffsetSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getTransmitOffsetSpinField() {
	if (ivjTransmitOffsetSpinField == null) {
		try {
			ivjTransmitOffsetSpinField = new com.klg.jclass.field.JCSpinField();
			ivjTransmitOffsetSpinField.setName("TransmitOffsetSpinField");
			ivjTransmitOffsetSpinField.setPreferredSize(new java.awt.Dimension(48, 20));
			ivjTransmitOffsetSpinField.setMinimumSize(new java.awt.Dimension(48, 20));
			ivjTransmitOffsetSpinField.setMaximumSize(new java.awt.Dimension(48, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTransmitOffsetSpinField;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	Series5Base fiver = (Series5Base)o ;
	
	fiver.getSeries5RTU().setTickTime((Integer)getTickTimeSpinField().getValue());
	
	fiver.getSeries5RTU().setTransmitOffset((Integer)getTransmitOffsetSpinField().getValue());
	
	fiver.getSeries5RTU().setPowerValueHighLimit(new Integer(getJTextFieldHighLimit().getText()));
	
	fiver.getSeries5RTU().setPowerValueLowLimit(new Integer(getJTextFieldLowLimit().getText()));
	
	fiver.getSeries5RTU().setPowerValueMultiplier(new Double(getMultiplierSpinField().getValue().toString()));
	
	fiver.getSeries5RTU().setPowerValueOffset(new Double(getOffsetSpinField().getValue().toString()));
	
	fiver.getSeries5RTU().setStartCode(new Integer(getJTextFieldStartCode().getText()));
	
	fiver.getSeries5RTU().setStopCode(new Integer(getJTextFieldStopCode().getText()));
	
	return fiver;
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
	getJTextFieldHighLimit().addCaretListener(ivjEventHandler);
	getJTextFieldLowLimit().addCaretListener(ivjEventHandler);
	getJTextFieldStartCode().addCaretListener(ivjEventHandler);
	getJTextFieldStopCode().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Series5SettingsEditorPanel");
		setPreferredSize(new java.awt.Dimension(380, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(380, 360);
		setMaximumSize(new java.awt.Dimension(380, 360));
		setMinimumSize(new java.awt.Dimension(380, 360));

		java.awt.GridBagConstraints constraintsJPanelPowerValue = new java.awt.GridBagConstraints();
		constraintsJPanelPowerValue.gridx = 1; constraintsJPanelPowerValue.gridy = 3;
		constraintsJPanelPowerValue.gridwidth = 4;
		constraintsJPanelPowerValue.weightx = 1.0;
		constraintsJPanelPowerValue.weighty = 1.0;
		constraintsJPanelPowerValue.ipady = -20;
		constraintsJPanelPowerValue.insets = new java.awt.Insets(16, 5, 6, 35);
		add(getJPanelPowerValue(), constraintsJPanelPowerValue);

		java.awt.GridBagConstraints constraintsTickTimeSpinField = new java.awt.GridBagConstraints();
		constraintsTickTimeSpinField.gridx = 3; constraintsTickTimeSpinField.gridy = 1;
		constraintsTickTimeSpinField.insets = new java.awt.Insets(32, 2, 6, 2);
		add(getTickTimeSpinField(), constraintsTickTimeSpinField);

		java.awt.GridBagConstraints constraintsJLabelTickTime = new java.awt.GridBagConstraints();
		constraintsJLabelTickTime.gridx = 1; constraintsJLabelTickTime.gridy = 1;
		constraintsJLabelTickTime.ipadx = 25;
		constraintsJLabelTickTime.insets = new java.awt.Insets(35, 19, 4, 2);
		add(getJLabelTickTime(), constraintsJLabelTickTime);

		java.awt.GridBagConstraints constraintsJLabelMinutes = new java.awt.GridBagConstraints();
		constraintsJLabelMinutes.gridx = 4; constraintsJLabelMinutes.gridy = 1;
		constraintsJLabelMinutes.ipadx = 21;
		constraintsJLabelMinutes.insets = new java.awt.Insets(37, 3, 7, 153);
		add(getJLabelMinutes(), constraintsJLabelMinutes);

		java.awt.GridBagConstraints constraintsJLabelTransmitOffset = new java.awt.GridBagConstraints();
		constraintsJLabelTransmitOffset.gridx = 1; constraintsJLabelTransmitOffset.gridy = 2;
		constraintsJLabelTransmitOffset.gridwidth = 2;
		constraintsJLabelTransmitOffset.ipadx = 8;
		constraintsJLabelTransmitOffset.insets = new java.awt.Insets(7, 17, 15, 1);
		add(getJLabelTransmitOffset(), constraintsJLabelTransmitOffset);

		java.awt.GridBagConstraints constraintsTransmitOffsetSpinField = new java.awt.GridBagConstraints();
		constraintsTransmitOffsetSpinField.gridx = 3; constraintsTransmitOffsetSpinField.gridy = 2;
		constraintsTransmitOffsetSpinField.insets = new java.awt.Insets(5, 2, 16, 2);
		add(getTransmitOffsetSpinField(), constraintsTransmitOffsetSpinField);

		java.awt.GridBagConstraints constraintsJLabelSeconds = new java.awt.GridBagConstraints();
		constraintsJLabelSeconds.gridx = 4; constraintsJLabelSeconds.gridy = 2;
		constraintsJLabelSeconds.ipadx = 21;
		constraintsJLabelSeconds.insets = new java.awt.Insets(9, 3, 18, 153);
		add(getJLabelSeconds(), constraintsJLabelSeconds);

		java.awt.GridBagConstraints constraintsJCheckBoxSaveHistory = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSaveHistory.gridx = 4; constraintsJCheckBoxSaveHistory.gridy = 1;
constraintsJCheckBoxSaveHistory.gridheight = 2;
		constraintsJCheckBoxSaveHistory.insets = new java.awt.Insets(47, 36, 30, 41);
		add(getJCheckBoxSaveHistory(), constraintsJCheckBoxSaveHistory);

		java.awt.GridBagConstraints constraintsJLabelStartCode = new java.awt.GridBagConstraints();
		constraintsJLabelStartCode.gridx = 1; constraintsJLabelStartCode.gridy = 4;
		constraintsJLabelStartCode.ipadx = 8;
		constraintsJLabelStartCode.insets = new java.awt.Insets(10, 17, 8, 10);
		add(getJLabelStartCode(), constraintsJLabelStartCode);

		java.awt.GridBagConstraints constraintsJLabelStopCode = new java.awt.GridBagConstraints();
		constraintsJLabelStopCode.gridx = 1; constraintsJLabelStopCode.gridy = 5;
		constraintsJLabelStopCode.ipadx = 10;
		constraintsJLabelStopCode.insets = new java.awt.Insets(9, 17, 59, 9);
		add(getJLabelStopCode(), constraintsJLabelStopCode);

		java.awt.GridBagConstraints constraintsJTextFieldStartCode = new java.awt.GridBagConstraints();
		constraintsJTextFieldStartCode.gridx = 2; constraintsJTextFieldStartCode.gridy = 4;
		constraintsJTextFieldStartCode.gridwidth = 2;
		constraintsJTextFieldStartCode.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStartCode.weightx = 1.0;
		constraintsJTextFieldStartCode.ipadx = -11;
		constraintsJTextFieldStartCode.insets = new java.awt.Insets(7, 2, 10, 32);
		add(getJTextFieldStartCode(), constraintsJTextFieldStartCode);

		java.awt.GridBagConstraints constraintsJTextFieldStopCode = new java.awt.GridBagConstraints();
		constraintsJTextFieldStopCode.gridx = 2; constraintsJTextFieldStopCode.gridy = 5;
		constraintsJTextFieldStopCode.gridwidth = 2;
		constraintsJTextFieldStopCode.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStopCode.weightx = 1.0;
		constraintsJTextFieldStopCode.ipadx = -11;
		constraintsJTextFieldStopCode.insets = new java.awt.Insets(8, 2, 59, 32);
		add(getJTextFieldStopCode(), constraintsJTextFieldStopCode);
		initConnections();
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
		Series5SettingsEditorPanel aSeries5SettingsEditorPanel;
		aSeries5SettingsEditorPanel = new Series5SettingsEditorPanel();
		frame.setContentPane(aSeries5SettingsEditorPanel);
		frame.setSize(aSeries5SettingsEditorPanel.getSize());
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
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {

	Series5Base fiver = (Series5Base)o ;
	
	getTickTimeSpinField().setValue(fiver.getSeries5RTU().getTickTime());
	
	getTransmitOffsetSpinField().setValue(fiver.getSeries5RTU().getTransmitOffset());
	
	getJTextFieldHighLimit().setText(fiver.getSeries5RTU().getPowerValueHighLimit().toString());
	
	getJTextFieldLowLimit().setText(fiver.getSeries5RTU().getPowerValueLowLimit().toString());
	
	getMultiplierSpinField().setValue(fiver.getSeries5RTU().getPowerValueMultiplier());
	
	getOffsetSpinField().setValue(fiver.getSeries5RTU().getPowerValueOffset());
	
	getJTextFieldStartCode().setText(fiver.getSeries5RTU().getStartCode().toString());
	
	getJTextFieldStopCode().setText(fiver.getSeries5RTU().getStopCode().toString());
	
}
}
