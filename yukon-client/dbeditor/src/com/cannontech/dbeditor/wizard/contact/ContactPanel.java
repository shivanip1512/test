package com.cannontech.dbeditor.wizard.contact;
/**
 * Insert the type's description here.
 * Creation date: (11/21/00 4:08:38 PM)
 * @author: 
 */
import java.util.Vector;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.common.util.CtiUtilities;

public class ContactPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener, javax.swing.event.ListSelectionListener {
	private ContactNotificationTableModel tableModel = null;
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JLabel ivjJLabelAddress = null;
	private javax.swing.JScrollPane ivjJScrollPaneJTableEmail = null;
	private javax.swing.JTable ivjJTableEmail = null;
	private javax.swing.JTextField ivjJTextFieldAddress = null;
	private javax.swing.JLabel ivjJLabelNotifyType = null;
	private javax.swing.JComboBox ivjJComboBoxNotifyType = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisable = null;
	private javax.swing.JComboBox ivjJComboBoxLoginUser = null;
	private javax.swing.JLabel ivjJLabelFirstName = null;
	private javax.swing.JLabel ivjJLabelLastName = null;
	private javax.swing.JLabel ivjJLabelLoginUser = null;
	private javax.swing.JPanel ivjJPanelNotification = null;
	private javax.swing.JTextField ivjJTextFieldFirstName = null;
	private javax.swing.JTextField ivjJTextFieldLastName = null;
	private javax.swing.JButton ivjJButtonAddress = null;

/**
 * DestinationLocationInfoPanel constructor comment.
 */
public ContactPanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	
	if (e.getSource() == getJComboBoxLoginUser())
		fireInputUpdate();

	if (e.getSource() == getJCheckBoxDisable())
	{
		disableFlag_ActionPerformed(e);
		fireInputUpdate();
	}
	
	// user code end
	if (e.getSource() == getJButtonAdd()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxNotifyType()) 
		connEtoC5(e);
	if (e.getSource() == getJButtonAddress()) 
		connEtoC6(e);
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}

	if (e.getSource() == getJTextFieldLastName() )
	{	
		jTextField_CaretUpdate( e );
		fireInputUpdate();
	}		

	if (e.getSource() == getJTextFieldFirstName() )
	{	
		jTextField_CaretUpdate( e );
		fireInputUpdate();
	}		
	
	// user code end
	if (e.getSource() == getJTextFieldAddress()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> DestinationLocationInfoPanel.jButtonAdd_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdd_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> DestinationLocationInfoPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> DestinationLocationInfoPanel.jTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JTextFieldAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> DestinationLocationInfoPanel.jTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JComboBoxSendType.action.actionPerformed(java.awt.event.ActionEvent) --> DestinationLocationInfoPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		
		checkEntry();
		
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JButtonAddress.action.actionPerformed(java.awt.event.ActionEvent) --> NotifRecipientEmailPanel.jButtonAddress_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAddress_ActionPerformed(arg1);
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
	D0CB838494G88G88GAD06C3AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8FDCD4D55636BF5B75EB6D4BB65A35DDF7574CEDE93322154A4C7623168AB7CBAAACABAAB3F73522A2334D126C8283830A8AE9AB2640281418B002FF4A88153FB6E3E424A4E86C42B0730051E1E61AF9A81A66F74E3D6F5E77E6F883E2363F787D8E6F5DF36EB9771CFB6F39771EFB6F9B3222E0F4DAF24A8AC21253087277D1B2A1B16D043C39BD7E9D0E8B3F41E2A41A3F35GB9244BEC00678420
	0D4C319A33490B87B2A0BD855209C62BF1A53CAFA793F3F21B61A5C4B909CD04D8CEE47C2E7FF22E7D1617E32469DB7232603994A096F073GAAA3ACD07E543CDC851F8B69254ABBC2F28A28F0A366292C4B556007AA796640B389E57C9973BC1737C62137G0F8CB085E0460DAC6FFCF8E6D26DAEAC5F4173DD9ACE667D2DB8E5891625114F21794F0C771472B411C8DA9129640E0C2F60D951F034F3175D53ED365B5CEE0FDB166CDDE6C73B64F04A9E1F793873244D67B03BBD323343E93749CE0F5B6C136CCE
	2FD3F24B7E7272FDC8584F91F55DB6272B416616DCA0B7015A53D117C14BA2A492523D639177E49D56E1A93CDBG5A46F31B9ED8D221689CF6B41C4C382AA024CD1346B3DBE60CD7ED0A1E99EF989109721767E7A9FCA6C8B783B0FBDF30D83F2F3DB540E0334B01B6FE1D4FED0C0B64B63EF390F5483A49E2AC842838096363A93E9152AE0021B7F15BF7EE6036B7FFFB8B197E505E74E5A1EC9FF59333B97AA655769D53628D2BE87F7C68B476B1F2B3148B30832085609440A8005554AF66FE7B81BC2B5A659A
	CFF73747DD6575D63B9C3E4FBD8E49856F6565C0D1F050329E1F039056770F56956B6843A1726640FE0F40ECEBA23C2F0E5BFFBD097949E769E9BA36C5CEB32744A8B262EF5658F68B565B6F76E03DA5819E7950D6069FCF7127EED1712C8F4C22FD8C539300B6F4822F676625DC17FA439812DD76E50AD1C71711F76EC9E938156950F52B2A8B2B631CB3D847E1000FG08811881C0A6DA0C26093C0EABCEED9D280E2D5077FCEE1B6BC0CF3B5766132BFB1CAE0764D36AFAD5FEFFF52D870CEF432079D83D04EF
	5536D17C78865DFA098F5F1ED2B11155C763C435DED859EDA14B16A7766D9BCBC59B7E8F7509E193D99B268A7CE594BFD203E7ED3B1A62B19D8E342C5B201F537C0DCD08775E4672AF91F26C949F2641B3B9B3299CCCCFGDA61A40B11356F8D84476091109E87908D908F108410FB3B45D8FCBBEF631F1D5AFC16FE5420E9DF46DBF97ADF7014FAE51F4D6A764BB637DDBAE873B9ED6DAE49CF14BC5653D82F6D81FC7B28FF5B210CCFA5376423A394C837F5CB50197CACCF53699C4C6313A8355159ADC1815DDE
	4265361EDE8ECF174DAFFF65F558E4C94D41681D2731CF9C339D8409A0G7D6BF62C2F1F50313C9E5E3BEE67F5951F0C38A8C8CF9775B273745B707C8CBA3764AAAF2FF55AD18D1B6F846F23E3771442B381720F788B0C0D7F877E83B0AC8A4A3843E21C8450F8A74C2DGA37E8A759DEDB106C7F37FFD311977535AA5571162E9DF6876D3844BDD2C4F355D955CCFEF25658F83FCB8C08C409C000C3BAD46A2G8BC09740B000F0009800B9F773797F393AA81D32D0573371B534831FE83396BFC313C67B75F831
	FE0A237B52300E6873AF6C09752A25B7866906F54CEC495B23E74B72333083E195D1471D6071043FAF8B413B98E0258E7E9D651926BCD30368B9C169A50A9EB9E4EFAA7AF246BD6047G3577703E18771D09D09F2D3679A56E15DF784021A4075AB3D95EF74F203E52B1186AEBA52F64F6601C2F78445AB3E817622F9D9E61CB9BA89E476F9AF0A83F40171C49E1F8102175FECE5FCE69B6D976B95BFBE46900A44BCEF7274A3F136A628F189A7C0A2D7586343585EC1481068ADB6FBE03ED477D2E5E63B95A63ED
	4491C5715F63F41DB29272C75C437B40A587F1FD9BA752D75034CADFFB30B9C49BA24FE0661950F3D6F948B2025B7F5C4BB85C0463A61FC7901727745545A20F1366C942BE94E3B1568348G23G22E3F8FD4CB95826114365F3B9836912B7A81DD31B5A14F252B47845CA3F9FC8C666D916457A4E2DA4F45F0FA27AFD7FEE225F7727107E7BF69C516B5B0C76B83555F35C2D7115998467933B476D1628438A96425675AFB6229DCE37D3EEF2BABAA57900732484B157E46C73D750B9AB815A0FCC66F3487175FC
	8EA9BCF98D09EA7DA0C5EFBDBA728F561466492C0F3BA62BF388536969E474C149A5F5C3C555F8FA5CB2434F267321027727D25C1145A31137E8EFF50FAC43BA4161A836D1C34C6C51A079BABC3EEE49D1F444F64CE6369D174D34A14CD514DC076F9BCDA5A53BCC21422D62A3C2F4EBD5104C7A50C2BF59F8F0E3A9537143253F536A38DF6A769C13FEA8B5197441EADA46FC81569756DFB1DD9B21CF6EF1CAAE47BED87F945BE130154D74BFDB57D0BDFB4D7E63507B9411B5C8BDABCD056C569A0D447A7EC422
	12DB959D1F5D701BA09D21EDFC125F7F63D033EC1B22E7D87BC926A72CE15BBD551EDEDA540946935E735F7A9DCE1FE4F5FBFB38162047B65E263F794F68A09F2115754379890A9FEC8F5C4EF42DAC9BFE1E7CD93B1D707DFD78CE5FAF4E1F8F6B2976BD7DB64CBCA248A776B8FDFE791C1DB7D045F3F5D8265B7315171CC7FFBDAF1A899FFD5F68BE5FBE9A20E0FFFE3933B46CBC7AE5087EFECE3E485642E752F09E9E8E7373E800891379DA581E02F43E96D6967CB59E170BADA100D67AB7DC9F3C3AE705E64E
	0F1D4C76F457G0D510FFD2AC76F957457F46943853D5722C79FAD682F6B52A3857D24D50F9EA3688BF469CB05FD9B7676CFBFB0G7D4CG74EB8720BF92124E5A29A1B59B570252B7BD125BAEE1307219F4026FE3243E9DC4BBE8F375287BA32D293896F6E9629C065FEF50DF1BB59BB036B9AA64379852896EF3B019ECAD99E1C319DE1F339B42C28C5C143D7B7F4D576E5D4703ED384F3F66DEE807FB797E724BDF603ED12D640765AE5DFA297730ADE4DCB553BE1FB185E57D3E096F71DB00378DE0483DCCDE
	543D2CEED8BEFBB34F5788E95794B55F70A95AFCAB8F71FCF110CEG483A0F65335C274DE72861790683FE6CFD2172CD907922819F8F1024642B38DF1B6FD42DB82BG3C8C90F6BF4B972764B3CD45FC91628CE033405DC3F1E590503C144A77A0BF4F561EE9ECAB7BD6F7DD0E60184A6477CE8D5E1B0DCC27B1AE604327723160E103B6AE2E727BBDF6A7ED97253FDC1826DD33B35CC5E90B6059017313950221DE5E67C624212E38F7262015FEF36D261D246FDEE09DBDD5605B8EB547696855682971B8A48BF4
	5B9A91529CF4CA4789BFCFA87E1457572429BCE6B93DC7FBB6B2E959C7A90BC3544DBC25CED6442AF5939B97EFE0322F93329B014EF0E68B478D97389B84AEDA60CA2D9C17A4F0B78A5C3687389FDCD10EFB5FFA67ACF6DAB9664E9E2739C7F6626620CD365129071D28B0197369BE854AF2BD404FCDE25622CF0F0574A400B8G36079BC377F6B1EF82608A9F646362353B3563CE8B600782048330F9EDF37396F88EFE9A636D7DC6714770BC67A59B61FDFE4C72CAC547A9570FA195E7F2D3744EFD06ADDB17B2
	69C1561E339ED45BB5726809944771A714F32F614D3CAECA9F32983B9E62F5F1F32EDEDD0CFE0837570E228A5516F3DA6331720FD47372A3C1F602A87F0F6B34659746317D277AB85E968FD46338B048F516528EAF67FD043A1F5B92CBB987A82961FA3701CEA363385E3FEB8F569B6BADB60E773D27855FAC402D19467992DA35FC0D53985F29E95CDECB6D776BFB519B5FE6E3D3502A0D1FEB7C21166BB5824A0C91FA051D8E6E87588E64E12ED7D65D77536BAF79BB83FC629C16F542AF1A36E8670228999F06
	1C8BD6BC4C7C2168616039E0E4860EA33500EFFE184F850B53F3B4F3810408324D5D89AB87318F160EF581EB84A5FDBBBD2B24EB844277654ABE9D4C991B6BE1FED6B2AD936BFF8824C781C482CC81C8903A559A87BCB32C6B6D674C704F0352AB6B913E273955B298BE13600B9C945FB641375DBA98BE0B603BF9D0FCAD026F4DBE67125EC7543D6B210F745D47E667064D6B687963A36C5CB0C7601D949F2D41B33F3B1F4ED118CE845A29693C7C7B1B024B4FF8B4304C4220B4630B6F43179FDCB7C1FC4C8F17
	9D407EC667DF4DGD8DE2E44F0045FC7B97535F6CD5B76A89B8F0642F36423FCFE1F399BFD88579A910F72FE3CE9B12E7B66C05A70980CFBGBBGDAGFA81C6BD467B73E5C63DB5D1E0FF8E35A0D24FCBD8DD7C22795C6AF0F49F3E60BA1C74D8A09F337B05DCEEF75CE33C9D3D55672643F7FD7802F548FADC2F9D5FABDA235F0EE236E33ABE390E2F418A9F97BEDDF3EE3AD675618B563565F13D7A1A564E57EC3250E176B96A70EA9FBEF3D0FA1CEEFDF53AD66B57975FB7D1F4DD5F46F50D963ADEDBFBEE3A
	5E5A07AF383E12F46B6B5E53990A0C95715C2F0EE65173F5C83744F39FCAB462D98F1F2F9ABCCE37CC78DC755B8C0CDFBDBEE727534DF1E3A84E6E71518394F66EE721EBFE97489CA664BE11997A3CE7D6267EF94FF3197A67BD09197D1FF54E4F8CF59E44741BAC749B8540F0FBAD9C97A1F0778ADC344055DBB9EE3A405DA7F089823722GF164890BF145939C771189F1BBA05D70846F877FD9E742BE8C69B0008800D80084002CA7AD46A2G4B13BC2F076645F85B813821GE3812281E6BD4956FC9893333C
	F1EB0784750BD8258C1E161F68BB8F698157A5949D750B5048AA5264E53A3C56C717CC221EB1AEA5E7FF6EBA50D9E48E893C9F203D8F10C74B197C2FE5346F787D121FCE869891AA345972BBC4675C8F770B895F1FCCEB4ED47254491E1AB2424702D7B6625C26B39650C01379E064575C878DCF71B8F3CBAB4E619510EE84F0BDA546325D6867C31FD26BF2A43C0FD352D1700CFBAA30CD14F37E45217D2CE2313E1FD5AF46BA75FBFAFCF6A958172C0BF385CD1DAD2D32540D55226CFFD1FE59666B147A7862A1
	4A4FE821785B970772D7D66713836AFC0B68D7F3F3310E66E86AA1895E33E730F4A1BCF74C882CA3A66F0F55DAF9DB053CE75675375E2B1F5157871473569A2D3CEDC25EE36DA82FCD070F7ADFC8F97935DAF95B053C22B614B7AC043CD1A1654D8C1077211097FB9A65C58471C5AB6969BA72D8FBF933F09D5AAB52B2CD1F903EA0D5AD95EDDE67F6A813885B07EB25E3EF1A011056E40E1BC1F1F900FBC560ECF46C3D8F8C00F0CF414D243875106FF2114FCE475EABA05F27A99CB70B629E825C4FC59DECAA40
	F94AE5BB616981DDAA1F46FC55855A75E882583B8270C5CFB33B19ADB79E47B12743E76B06AEF940AB59491A19483B4F44F907C17ED1G13G6281121E6671EDBD3D9F23F37A2468E4AAC1DF5DDBEFEB17DC41341DB5EF8770565B9432C2DFF3FA29608D267D230C7272D5C83D87666B2F7C68144A597BC935DC1548E8F37FC3F9692835CFF3AB19730ECF276586445CE26FBEC39B6FBBE632757D3019FCBFE0F6035E3E4CA8C1B7EF50630F90743C4C60FD8E24C78BFA6FA1BD7E69B37918FA6B57DA7FCEGBCF9
	C66D63AB1E695B5FD97CF5A79D230B1E890E4B9E227846E70263329D2DBCAE9B82342CD9FC7EDFC8E31B11100E903856CA44CD02F42C4005D35CACC84F93384CAA3EF6A0FF673843B4E6E80034C360B2D771B2FA856E48AADE46D0013BFE95AFE32440ADFB1717B1D6602AABF89973846E0DF5BCDF46BFB86E65F6DEEE3340B55BF9BE07406DEE6379A285EEF49B4F97A5F033CF73FC5933B94E729D6256C03AD8602E790E736E184D77974B5A8262985C7CA9AF7F5467F4D45BBA6B6988406663AE3AC643369BB6
	1B4F473FAC40360B0274F4004459FCBF3C77FD3E9FBE6E6AB124FEC8036E3782B39ABFCA4918437AD1719CF5FF28FE9A5F8FFF50444B6C827A68B908A3EB357D967D6C65949603B07A16GBA76AB9C93D53AA3001E59077F178174B48DBF336D5E7738EDC977C69057CD3BC2FE87B1C731A96BD9760C69D9CF060CF942202C01BAC22C0168C8436CF7E572B5D06D33BC664F7F1247132784CE5A4073C58ADCE3A64F17A0F0B79C62798A1F63FDA2162E33DAA06D9538ED345E43A09DAEF0CDEBF95F19729C9FA3E2
	DACD58471E077E84508CF08AA08CA082E08A401C67F95EC7E95E0C84F636D514404E56705C2C9E5EDD896CAC0B655D55BA846DFBDE9DE31A1577B94F1F5DBADC8BEBB4BCBB947D34F42EEBB09F5345C6F5498C42733EB5D8DD34EBF76C8741779231AF695D4BE573C9069D734B9E0F2B516945590E8CCF406F220C58E515BC4768F8586D163ABDEE27FDC682365D459477D7005884BE5FBA73713CD586A1CC4687741B2E5A97B03F292A369F9BB21532B894828F42FA458FF6407B47C43D07391CFC130BF5A63971
	9858C118CE43F239EF8F077CCC879FDE3BD7681751B1776FG23857D72152A0E6F3FBB100EE9C43D1F992CE3448BEC2CCA96659D2D607ACC829AABA3E7FDFFE5F0799CE2B5E550F387F85F1A2C58C875DF34CA555FE3BF9B597C1998A45B702245786FG59A56F2A32EFE96BCF36B1C8776C9783E597C2BA07E8E5B72CD67B57D2E3E03F712FD14B0D7AEE20F2F919FAEDD22AA91779DEF741E03F17E1EB0DC5A69AA73E48569A49823F1C62AD9ABCDB030CAA605F24B582ED420BFC4F67A07DEEF04DCBB03E81B4
	GF4818C83987B925F2BF9BD8B3FD768F79F3551632D07A0532559FEE176BDE2BA376F016EB072B9A8F2621F48041FAECC8EF55EBC61A5A5BED11EED72FA0AE776BDCB6FCDE4A4C2BBGD8G5AGE40061095C3E2AA5454CBEBCAE2F12E51B3D0B1E44F36370F0DCC563221F5976CD26563651092C7CC865991398EA3CDD7FE5FFF5929F126F4886ADDF22124F70B2FB663E9C0A6FD0E6FF659507640BBC24652BD472B5AA4FAE9DBE66BF333F46582A5D632301BF0B2D960A3D7ED3409786908BB06BE536CF4578
	BEB3750D495ACD41B1D99270E44FE5E5B31FF42F6D4FA7D1B63FE30F6FC540DBB11779A4269B607D72375EFA6BE5G26C74147B8C731D50A88E9945B3674207F763925A768D17BF1005FFB406E7338681D4F3D74E6903D824A7344AD453B9DEA4CA462727CA5AA1E1D038B396B3F41FBCAFBEB3AA47BD10861EA1DFE944BE8D7EF48967A2AD773986D22FA9AF3763D392744E8B9ED41B19236C78B5489BB23A9CC51563FF7AE230F02E744DC9EE7DDD12115912364C9947436A63DB28C2F703574542F35E72D593F
	FEBF050FDD41ED197B0A3217730A3A16E6F60E2D4467865B86E57C98FDDCABF347220A1067374D0AAC17725C7647752400306F10681E2D72476602F07CACDBA96369ACBEC62D3976AAD27B48426475A14A982B481E2751BBFC591386267B6FF3D1378E1B4B2F36771FD6D1FBFC9B14F20BD034C27BC905B6FFDFDD93F7B31E4F8AC93FF477FB7D53AFFDDF35BBF2833E33F1A15B243E2F523C0FA988759D4D79GD646EDBFE8994A3D3E3C50FB269DF97AFB262EBC7D3389DFDE7F5F221C4C8B7DAD4A85F95AEFD1
	58FCB63488FD29F89E4CE5GAE00A1GE17378DC36ACFF9D21F3194EBCD6676844BDDB7C5AD366654F4D8FED6B6B7983FFA31AD200E3D93B87062CEE144F71998558DE3247CB118C3724G6B48676C6C12D9D6853F1C465DAE290322D9ACB60C46F691E0D754BC9E1FD52DC7DC9C24E7895C556BF94C16A8F065DFF2DC652B9CF7810D9FC7C0FA34400D73A1AE925251821763673C3182674D64B853BF45D9900DB33D109EA2F095ABE9CC89693182F765CA4E9B71CFFEEF677D967E7DB0F98D56DD2F71FD0A5B97
	61BA6B2C6E2DB5BAE517D4CDE7D896773ADB78192277B53EF7F181DD7305C3BA8AE07AEBBCC69EA6769C92C55E1B693A31E8BE5499C0B3005E3890FA2CD8091F74ABE70B16B69E03FB67F35BBA6B72086EDE00BAD38A7E16944E9FBE1F6BF762B06AB78352738132DF67F2F79D8CF5662A4C140A4CD89A7BE07FABFA1D57432F1AF8B9ED2F7375F0B43503754B2B8AF000BCE8BB22FB1BF2AF9477C57B9149AEB75ABA05FFDE3F096ED3013C485739CFBEDB30A9149CC0B678BCDE49A71FE0D8FA43D81175DCB12E
	EB62C0CEA2400AB7AC46E200CA00E6G97409000B000F1G51G538192G4882308F2090E047822E478BEBB776230732A1C687881FCDFB4EBB3CA87498F1D95140E34428A2FA3F95FC9B979E8CF7E591BDBB6E6870CBE2AC3A3AA8DB602C7E860F5F09BB4D4A9C1909B1D98778830BE5EF22BED98F36B5AF606B47667501F15BA23C56272C91EF6FB74ECDA57D477B4396B0FFF88D5608CC5E17DF8E945BEA635B30850171E77882B5EEE63A0F5C90E85F75F43C0A04FCD1423E3F7A826DABA9D56DAB728FE41FB6
	B68D36AF914AD89BE05F4FB28732CF3BC71294E4DFF61245381BE86D1BF9A8503E79F47CDC83790A1238FD87D6865A57D0265AB7E165C076E97589362FB9092DB55F847B58BA2238E52038541124F75F22FCC03E607B970C4FCF7906A41D5D5EDED8927FCD09D759981E949C67AF2478482460B87FE7A24E0F82DA611BFC3F7162C147612C1C8F681D32E1EF86173F1562435E8CAEFF120917BF9AE806053C7C4F5B3465F7ADD4FE93C8F90ED898F2EFB205730D05BC930272CD5161E37A156E24734542605F74A8
	27784405413F69F1663018135F32989DEFF13D27D5E079B1EFA93F8B23BC93152701D8C368BDAA606EDC2E122FD8F9D66A7031B1F07CA6FC976BC5B69DA834091BF06D075F99BBEDAE67C9FA8EF7GB23ABB99FDE1F168F1BA2DE460F1FAE9097EDAEEC5C95F355C2A927D35DCC109F6AD47F005A5B8A6F4427C52AC72ED9438C382E726B2915764F448DD823F0D6A05F80B0485F2FCB9556BB845B5AB385D9A5CA185D7DB02F38D185C657109FB8CA5F4BCF23ADCAC7DB94D63F44354A36AE3BF55516935E974EE
	92B8541B0DD35715904247396D6BF17D6D160E1B44BD63BDB068952DF3C01BF9BA57951BCA0BCDE55BB6F25ED8B37206DED73DC267B0765E39C5ADE734EFF065DC34357FF22E5B2616F378A3351C521503AB67F18C3B157BB3D14B1750BAF5B95DD28D7B4598426FDCDDC4572B0D6087AEG652C124681C3A09D86407CE8812A2C781441243EDF7CDF08B52F7EAF14D13AD63569E34D7B1D269F3E6C47FF50B2D81BBA5AF89CB7DA34737EFCFE569CA9F037640BB3DF016B25F1C0F6325538AD19639E20B88B243D
	8237A915738ECD667D66431D74F7C5C43A122693C47A424303319D658C3EFE1F2E98CC99E7FB67AD30FE93AA4439F88A2F0F4FF7523595240B856E35F4FED6DEA9F0ADC6DEEF7582B73E093757A801FBE1B5EF2F88015B370673CE829C9BAB2EEC4171DB4650944788424F1DEE253E1E15EAB56626723C2602B41E177031E3A19E29A9BC9753FEE3017C0DG0C3E7693243375607D795FE7BDBF94E42E52501713CBD270606D9884E2CE3B4D450252EA1B2F4163F25ACFA8BA7D0976BD0C21C688BB66506FFCE1E2
	F11E44EF0B7BF0F2FB8C340FD6814482AF3321BA75EC5634FCDD1BED309A8B81783E758EF83FA48574C966B65C59097B5C5A8DE736077B4DCE6D9EEEAD706159F497BCCF99F85D5662D626A24731B3F02DFE127627B048BBDA646F5C2936055FF8B631870FBF025BA25640FC92E38FD6DEBC6D0371001FA34A3BE817DA1E34E620723471D7F0F939E92CBC0C85186CCB2AD45B5F4C47C1D8E73E2B0245128B263BB2E53AD37AEC6227AA6F1E7C414D151B2CAA6F9DA9B881EB3F97536C0C188F566DEF3456D45537
	9E28597FC5FDFDF5557E562A832D7B6A6AEA6BEA997F6A542D7D70DB3E58EFBD7C453E46C08975CDD5078E682E89C22F93766DA522EE0FFC9B62FE876D01EC0F651A1D625BG28F73617B3FF977D7DA8C84BG4353F89BCF2DD76B6379744155655C4F09E813C79BD4B9CF2F9E1C1C24AF8961637775BB7B0B194626751D93D80C615A0D76CDGFAE04C54C371519ABC0BBD966E62B1D3AC50362573B1762E1DFC0C9D194E67AD5FEED53FEFB56F065DBF7CFAA16F87AD035D230C97E355A430B996A091A05BE8B5
	168230BE15320B6FDFB6G0E5DE9AA5D95FCE76A52E46D1D2776DD41F72610DF25076D8A3EB39548FF772E60BBD31C1F550DF157E063EB769B41D19F7C9A8A9B7782C9134946AA0743EF123BA4133D47079FB318343F83EBF23A7193C67AAE13D448AB13E2F6CAF836CC69AC3BCCBE28435F7EB4595CA689678913CD79D98D13374B6316CC6E1E6EF649D7EC12E43B99B29F10E4DA40F15B89F8BA7DA6BB7B998D933C4A9ED33B2494A901C6A6484C5471F8690F5B8961268EBCAF754BE411F2E6EAB61B7DBE32CC
	78251586DFB30EEDADCFFD662A5494E23012F4960CDA0981A3CAABC92381271524C2B05A0BBFA8499D1D866F7EEF48F7F448FADB034D9058948568262F4026EAA82A04E48D1684E0B3B888ACF606F4AED7A60B6955DD93DE40AD87E371B2AECFE49D6AB98AEDC26F5816036DCB8246243B2E5C7E0FA9CF9E742712F4ABD905536F8149C7A3FD4901DFA5A8BF073907E4FA7186B57E4A98D40C1FBE527C728917D4CFD6F46C471F2B704B12230A6FA1C0153FC33FFA5D0CE7871BADB6FF170C8374A7754460469F6E
	A4EFB3324B666E5C4CB694BE21351BDC55C6EBF8A92D859FD9CE75C5A9E6A1E579ED4B5EDB3BF75604D4123207ACF2F9EC0EBD50B5BCBEAB49143B20193BBCAEC72FF7F7A59514F11CFF55168A62D68B7351D8B31E298B250F767EF67812B9CB76C083747A48E61A93F7864DA20723264B660322A41FD5469FA275788A856D02FC797D7BBEDD5912C21295A331315E69754A20154F667623D3008626A08DB8C988B2DFB6397472EB5EB8CC85292CB2A9E0D5862C1BBBA5CF77662A0E8E08F205G28BF901FA744E3
	9EF3D01E37FBAFFDE76F34375A213DEBC9EAF7E9A97DDFC67FEF417FE594D3C6B1E5DB209293C93FFF4174DE5AC5D7492C1388DF43E6B66F0116129FFBE1721847AB9293403ABDA40DFEE48C151D2D665AC787021F0E3BBCEFF1580C1FE3DBF831CBF039699D36EE276B843C5452593497719BFB7BF8867A4DA2DDBFE49E373CCFDFC36C4A3E1D86512BA9B10F7EB78D3ACA10EB9D65AD448271E5C3207EE57A7AA78BEC7A0FDB7F0FD57D97E9740779A5CBFB01DFA6994AB88C835356C649652223367F840C9D5D
	B82C4318EE6A71CBEC448F18D3E048687240D83FC43905E27A4AEF6B44E1C4AB5F04E7BD653A55139C22FA8CBFF07508EE4B3687D936CA4ED6577B63EB4BFDEADB72EBCCF45BBAGEBD4DA597063537FE0DF7D0782247C78B47D372A29C1DB537C2E4F46B35A7BC473AF4C8BF91FA8BE074585094AD37DBD08564DA83E78ED2B5182407613EFB06366F128B366835DB617CB36390ED6399DF85EDCFDC216543BA30E4DA85F4F73A8C4C6BB3A9975BE226179FFD0CB878861F5F5BD499FGG70E2GGD0CB818294
	G94G88G88GAD06C3AE61F5F5BD499FGG70E2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG83A0GGGG
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
			ivjJButtonAdd.setToolTipText("Adds the current notification information");
			ivjJButtonAdd.setMnemonic('a');
			ivjJButtonAdd.setText("Add");
			ivjJButtonAdd.setEnabled(false);
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
 * Return the JButtonAddress property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAddress() {
	if (ivjJButtonAddress == null) {
		try {
			ivjJButtonAddress = new javax.swing.JButton();
			ivjJButtonAddress.setName("JButtonAddress");
			ivjJButtonAddress.setToolTipText("Sets the optional address fields");
			ivjJButtonAddress.setText("Address...");
			// user code begin {1}
			
			//we do not use this widget for now
			ivjJButtonAddress.setVisible(false);

			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAddress;
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
			ivjJButtonRemove.setToolTipText("Removes the current notification information");
			ivjJButtonRemove.setMnemonic('r');
			ivjJButtonRemove.setText("Remove");
			ivjJButtonRemove.setEnabled(false);
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
 * Return the JCheckBoxDisable property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisable() {
	if (ivjJCheckBoxDisable == null) {
		try {
			ivjJCheckBoxDisable = new javax.swing.JCheckBox();
			ivjJCheckBoxDisable.setName("JCheckBoxDisable");
			ivjJCheckBoxDisable.setToolTipText("Tell the system to not use this notification method");
			ivjJCheckBoxDisable.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDisable.setText("Disable Usage");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisable;
}

/**
 * Return the JComboBoxLoginUser property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxLoginUser() {
	if (ivjJComboBoxLoginUser == null) {
		try {
			ivjJComboBoxLoginUser = new javax.swing.JComboBox();
			ivjJComboBoxLoginUser.setName("JComboBoxLoginUser");
			// user code begin {1}


			getJComboBoxLoginUser().addItem( 
				com.cannontech.common.util.CtiUtilities.STRING_NONE );

			com.cannontech.database.cache.DefaultDatabaseCache cache =
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

			synchronized( cache )
			{
				java.util.List yukUsers = cache.getAllYukonUsers();

				for( int i = 0; i < yukUsers.size(); i++ )
					getJComboBoxLoginUser().addItem( yukUsers.get(i) );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxLoginUser;
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxNotifyType() {
	if (ivjJComboBoxNotifyType == null) {
		try {
			ivjJComboBoxNotifyType = new javax.swing.JComboBox();
			ivjJComboBoxNotifyType.setName("JComboBoxNotifyType");
			ivjJComboBoxNotifyType.setToolTipText("Set the way this contact is to be notified");
			// user code begin {1}

			//since the HashTable holding the ListEntries returns in
			// reverse order, we must walk backwards through it in
			// a for loop
			Object[] entrs = YukonListFuncs.getYukonListEntries().values().toArray();
			for( int i = (entrs.length-1); i >= 0; i-- )
			{
				YukonListEntry entry = (YukonListEntry)entrs[i];
				
				if( entry.getListID() == YukonSelectionListDefs.YUK_LIST_ID_CONTACT_TYPE )
					ivjJComboBoxNotifyType.addItem( entry );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxNotifyType;
}

/**
 * Return the JLabelEmailAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAddress() {
	if (ivjJLabelAddress == null) {
		try {
			ivjJLabelAddress = new javax.swing.JLabel();
			ivjJLabelAddress.setName("JLabelAddress");
			ivjJLabelAddress.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAddress.setText("Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAddress;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFirstName() {
	if (ivjJLabelFirstName == null) {
		try {
			ivjJLabelFirstName = new javax.swing.JLabel();
			ivjJLabelFirstName.setName("JLabelFirstName");
			ivjJLabelFirstName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelFirstName.setText("First Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFirstName;
}

/**
 * Return the JLabelLastName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLastName() {
	if (ivjJLabelLastName == null) {
		try {
			ivjJLabelLastName = new javax.swing.JLabel();
			ivjJLabelLastName.setName("JLabelLastName");
			ivjJLabelLastName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelLastName.setText("Last Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLastName;
}

/**
 * Return the JLabelLoginUser property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLoginUser() {
	if (ivjJLabelLoginUser == null) {
		try {
			ivjJLabelLoginUser = new javax.swing.JLabel();
			ivjJLabelLoginUser.setName("JLabelLoginUser");
			ivjJLabelLoginUser.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelLoginUser.setText("Yukon Login:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLoginUser;
}

/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNotifyType() {
	if (ivjJLabelNotifyType == null) {
		try {
			ivjJLabelNotifyType = new javax.swing.JLabel();
			ivjJLabelNotifyType.setName("JLabelNotifyType");
			ivjJLabelNotifyType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNotifyType.setText("Notification Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNotifyType;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelNotification() {
	if (ivjJPanelNotification == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Notification");
			ivjJPanelNotification = new javax.swing.JPanel();
			ivjJPanelNotification.setName("JPanelNotification");
			ivjJPanelNotification.setBorder(ivjLocalBorder);
			ivjJPanelNotification.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelAddress = new java.awt.GridBagConstraints();
			constraintsJLabelAddress.gridx = 1; constraintsJLabelAddress.gridy = 1;
			constraintsJLabelAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAddress.ipadx = 12;
			constraintsJLabelAddress.ipady = 1;
			constraintsJLabelAddress.insets = new java.awt.Insets(3, 11, 3, 1);
			getJPanelNotification().add(getJLabelAddress(), constraintsJLabelAddress);

			java.awt.GridBagConstraints constraintsJTextFieldAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldAddress.gridx = 2; constraintsJTextFieldAddress.gridy = 1;
			constraintsJTextFieldAddress.gridwidth = 2;
			constraintsJTextFieldAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldAddress.weightx = 1.0;
			constraintsJTextFieldAddress.ipadx = 174;
			constraintsJTextFieldAddress.insets = new java.awt.Insets(3, 2, 3, 2);
			getJPanelNotification().add(getJTextFieldAddress(), constraintsJTextFieldAddress);

			java.awt.GridBagConstraints constraintsJScrollPaneJTableEmail = new java.awt.GridBagConstraints();
			constraintsJScrollPaneJTableEmail.gridx = 1; constraintsJScrollPaneJTableEmail.gridy = 4;
			constraintsJScrollPaneJTableEmail.gridwidth = 4;
			constraintsJScrollPaneJTableEmail.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneJTableEmail.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJScrollPaneJTableEmail.weightx = 1.0;
			constraintsJScrollPaneJTableEmail.weighty = 1.0;
			constraintsJScrollPaneJTableEmail.ipadx = 327;
			constraintsJScrollPaneJTableEmail.ipady = 148;
			constraintsJScrollPaneJTableEmail.insets = new java.awt.Insets(4, 7, 3, 6);
			getJPanelNotification().add(getJScrollPaneJTableEmail(), constraintsJScrollPaneJTableEmail);

			java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
			constraintsJButtonAdd.gridx = 4; constraintsJButtonAdd.gridy = 1;
			constraintsJButtonAdd.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonAdd.ipadx = 28;
			constraintsJButtonAdd.insets = new java.awt.Insets(3, 3, 0, 12);
			getJPanelNotification().add(getJButtonAdd(), constraintsJButtonAdd);

			java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
			constraintsJButtonRemove.gridx = 4; constraintsJButtonRemove.gridy = 2;
constraintsJButtonRemove.gridheight = 2;
			constraintsJButtonRemove.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonRemove.ipadx = 4;
			constraintsJButtonRemove.insets = new java.awt.Insets(8, 3, 18, 12);
			getJPanelNotification().add(getJButtonRemove(), constraintsJButtonRemove);

			java.awt.GridBagConstraints constraintsJCheckBoxDisable = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDisable.gridx = 1; constraintsJCheckBoxDisable.gridy = 3;
			constraintsJCheckBoxDisable.gridwidth = 3;
			constraintsJCheckBoxDisable.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDisable.ipadx = 3;
			constraintsJCheckBoxDisable.ipady = -5;
			constraintsJCheckBoxDisable.insets = new java.awt.Insets(2, 11, 3, 132);
			getJPanelNotification().add(getJCheckBoxDisable(), constraintsJCheckBoxDisable);

			java.awt.GridBagConstraints constraintsJLabelNotifyType = new java.awt.GridBagConstraints();
			constraintsJLabelNotifyType.gridx = 1; constraintsJLabelNotifyType.gridy = 2;
			constraintsJLabelNotifyType.gridwidth = 2;
			constraintsJLabelNotifyType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNotifyType.ipadx = 3;
			constraintsJLabelNotifyType.ipady = -2;
			constraintsJLabelNotifyType.insets = new java.awt.Insets(3, 11, 4, 1);
			getJPanelNotification().add(getJLabelNotifyType(), constraintsJLabelNotifyType);

			java.awt.GridBagConstraints constraintsJComboBoxNotifyType = new java.awt.GridBagConstraints();
			constraintsJComboBoxNotifyType.gridx = 3; constraintsJComboBoxNotifyType.gridy = 2;
			constraintsJComboBoxNotifyType.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxNotifyType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxNotifyType.weightx = 1.0;
			constraintsJComboBoxNotifyType.ipadx = 8;
			constraintsJComboBoxNotifyType.insets = new java.awt.Insets(0, 2, 1, 2);
			getJPanelNotification().add(getJComboBoxNotifyType(), constraintsJComboBoxNotifyType);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelNotification;
}

/**
 * Return the JScrollPaneJTableEmail property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneJTableEmail() {
	if (ivjJScrollPaneJTableEmail == null) {
		try {
			ivjJScrollPaneJTableEmail = new javax.swing.JScrollPane();
			ivjJScrollPaneJTableEmail.setName("JScrollPaneJTableEmail");
			ivjJScrollPaneJTableEmail.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneJTableEmail.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneJTableEmail().setViewportView(getJTableEmail());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneJTableEmail;
}


/**
 * Return the JTableEmail property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableEmail() {
	if (ivjJTableEmail == null) {
		try {
			ivjJTableEmail = new javax.swing.JTable();
			ivjJTableEmail.setName("JTableEmail");
			getJScrollPaneJTableEmail().setColumnHeaderView(ivjJTableEmail.getTableHeader());
			ivjJTableEmail.setBounds(0, 0, 200, 200);
			// user code begin {1}
			
			ivjJTableEmail.setModel( getTableModel() );
			

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableEmail;
}


/**
 * Return the JTextFieldEmailAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAddress() {
	if (ivjJTextFieldAddress == null) {
		try {
			ivjJTextFieldAddress = new javax.swing.JTextField();
			ivjJTextFieldAddress.setName("JTextFieldAddress");
			ivjJTextFieldAddress.setToolTipText("Enter an e-mail address, phone number, etc.");
			// user code begin {1}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAddress;
}

/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFirstName() {
	if (ivjJTextFieldFirstName == null) {
		try {
			ivjJTextFieldFirstName = new javax.swing.JTextField();
			ivjJTextFieldFirstName.setName("JTextFieldFirstName");
			// user code begin {1}
			ivjJTextFieldFirstName.setDocument(
						new com.cannontech.common.gui.util.TextFieldDocument(
								com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_30));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFirstName;
}

/**
 * Return the JTextFieldLastName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLastName() {
	if (ivjJTextFieldLastName == null) {
		try {
			ivjJTextFieldLastName = new javax.swing.JTextField();
			ivjJTextFieldLastName.setName("JTextFieldLastName");
			// user code begin {1}
			ivjJTextFieldLastName.setDocument(
						new com.cannontech.common.gui.util.TextFieldDocument(
								com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_30));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLastName;
}

/**
 * Insert the method's description here.
 * Creation date: (11/9/00 4:58:59 PM)
 * @return RecipientEmailTableModel
 */
private ContactNotificationTableModel getTableModel() 
{
	if( tableModel == null )
		tableModel = new ContactNotificationTableModel();
		
	return tableModel;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val) 
{
	Contact cnt = null;
	
	if( val == null )
		cnt = new Contact();
	else
		cnt = (Contact)val;
		
	//HAVE THE ADD() METHOD GET THE NEW CONTACT_ID!!!!		
	//cn.getContact().setContactID( recID );
	cnt.getContact().setContFirstName( getJTextFieldFirstName().getText() );
	cnt.getContact().setContLastName( getJTextFieldLastName().getText() );

	Object selLg = getJComboBoxLoginUser().getSelectedItem();
	if( selLg instanceof LiteYukonUser )
	{
		cnt.getContact().setLogInID( 
			new Integer( ((LiteYukonUser)selLg).getLiteID()) );
	}

	Vector holder = new Vector();
	//grab the latest ContactNotification list
	for( int i = 0; i < getTableModel().getRowCount(); i++ )
	{
		holder.addElement(getTableModel().getContactNotificationRow(i));
		
	}
	
	//run all the ContactNotifications through the NestedDBPersistent comparator
	//to see which ones need to be added, updated, or deleted.
	Vector newVect = CtiUtilities.NestedDBPersistentComparator(cnt.getContactNotifVect(), holder );
	
	cnt.getContactNotifVect().clear();
	cnt.getContactNotifVect().addAll( newVect );


	return cnt;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getJTextFieldLastName().addCaretListener(this);
	getJComboBoxLoginUser().addActionListener(this);
	getJCheckBoxDisable().addActionListener(this);
	getJTableEmail().getSelectionModel().addListSelectionListener( this );

	// user code end
	getJButtonAdd().addActionListener(this);
	getJButtonRemove().addActionListener(this);
	getJTextFieldFirstName().addCaretListener(this);
	getJTextFieldAddress().addCaretListener(this);
	getJComboBoxNotifyType().addActionListener(this);
	getJButtonAddress().addActionListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DestinationLocationInfoPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(376, 376);

		java.awt.GridBagConstraints constraintsJTextFieldFirstName = new java.awt.GridBagConstraints();
		constraintsJTextFieldFirstName.gridx = 2; constraintsJTextFieldFirstName.gridy = 1;
		constraintsJTextFieldFirstName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldFirstName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldFirstName.weightx = 1.0;
		constraintsJTextFieldFirstName.ipadx = 153;
		constraintsJTextFieldFirstName.insets = new java.awt.Insets(7, 2, 2, 8);
		add(getJTextFieldFirstName(), constraintsJTextFieldFirstName);

		java.awt.GridBagConstraints constraintsJLabelFirstName = new java.awt.GridBagConstraints();
		constraintsJLabelFirstName.gridx = 1; constraintsJLabelFirstName.gridy = 1;
		constraintsJLabelFirstName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFirstName.ipadx = 15;
		constraintsJLabelFirstName.ipady = -5;
		constraintsJLabelFirstName.insets = new java.awt.Insets(10, 6, 5, 1);
		add(getJLabelFirstName(), constraintsJLabelFirstName);

		java.awt.GridBagConstraints constraintsJTextFieldLastName = new java.awt.GridBagConstraints();
		constraintsJTextFieldLastName.gridx = 2; constraintsJTextFieldLastName.gridy = 2;
constraintsJTextFieldLastName.gridheight = 2;
		constraintsJTextFieldLastName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldLastName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldLastName.weightx = 1.0;
		constraintsJTextFieldLastName.ipadx = 153;
		constraintsJTextFieldLastName.insets = new java.awt.Insets(3, 2, 2, 8);
		add(getJTextFieldLastName(), constraintsJTextFieldLastName);

		java.awt.GridBagConstraints constraintsJLabelLastName = new java.awt.GridBagConstraints();
		constraintsJLabelLastName.gridx = 1; constraintsJLabelLastName.gridy = 3;
		constraintsJLabelLastName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelLastName.ipadx = 16;
		constraintsJLabelLastName.ipady = -5;
		constraintsJLabelLastName.insets = new java.awt.Insets(1, 6, 5, 1);
		add(getJLabelLastName(), constraintsJLabelLastName);

		java.awt.GridBagConstraints constraintsJComboBoxLoginUser = new java.awt.GridBagConstraints();
		constraintsJComboBoxLoginUser.gridx = 2; constraintsJComboBoxLoginUser.gridy = 4;
		constraintsJComboBoxLoginUser.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxLoginUser.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxLoginUser.weightx = 1.0;
		constraintsJComboBoxLoginUser.ipadx = 31;
		constraintsJComboBoxLoginUser.insets = new java.awt.Insets(3, 2, 6, 8);
		add(getJComboBoxLoginUser(), constraintsJComboBoxLoginUser);

		java.awt.GridBagConstraints constraintsJLabelLoginUser = new java.awt.GridBagConstraints();
		constraintsJLabelLoginUser.gridx = 1; constraintsJLabelLoginUser.gridy = 4;
		constraintsJLabelLoginUser.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelLoginUser.ipadx = 4;
		constraintsJLabelLoginUser.ipady = -2;
		constraintsJLabelLoginUser.insets = new java.awt.Insets(5, 6, 10, 1);
		add(getJLabelLoginUser(), constraintsJLabelLoginUser);

		java.awt.GridBagConstraints constraintsJPanelNotification = new java.awt.GridBagConstraints();
		constraintsJPanelNotification.gridx = 1; constraintsJPanelNotification.gridy = 5;
		constraintsJPanelNotification.gridwidth = 3;
		constraintsJPanelNotification.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelNotification.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelNotification.weightx = 1.0;
		constraintsJPanelNotification.weighty = 1.0;
		constraintsJPanelNotification.ipadx = -54;
		constraintsJPanelNotification.ipady = -8;
		constraintsJPanelNotification.insets = new java.awt.Insets(7, 6, 5, 8);
		add(getJPanelNotification(), constraintsJPanelNotification);

		java.awt.GridBagConstraints constraintsJButtonAddress = new java.awt.GridBagConstraints();
		constraintsJButtonAddress.gridx = 3; constraintsJButtonAddress.gridy = 1;
constraintsJButtonAddress.gridheight = 2;
		constraintsJButtonAddress.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonAddress.ipadx = 8;
		constraintsJButtonAddress.insets = new java.awt.Insets(8, 8, 1, 8);
		add(getJButtonAddress(), constraintsJButtonAddress);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldFirstName().getText() == null 
		 || getJTextFieldFirstName().getText().length() <= 0 )
	{
		setErrorString("The First Name text field must be filled in");
		return false;
	}

	if( getJTextFieldLastName().getText() == null 
		 || getJTextFieldLastName().getText().length() <= 0 )
	{
		setErrorString("The Last Name text field must be filled in");
		return false;
	}

	return true;
}


/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	Object o = getJComboBoxNotifyType().getSelectedItem();
	
	if( o != null && (o instanceof YukonListEntry) )
	{	
		YukonListEntry entry = (YukonListEntry)o;
		
		ContactNotification cn = new ContactNotification();
		cn.setNotification( getJTextFieldAddress().getText() );
		cn.setNotificationCatID( new Integer(entry.getEntryID()) );
		cn.setDisableFlag( 
				getJCheckBoxDisable().isSelected() ? "Y" : "N"  );

		//cn.setContactID( ContactNotification.DUMMY_CONTACTID );
		
		getTableModel().addRowValue( cn );
	
		getTableModel().fireTableDataChanged();
		fireInputUpdate();
	}
	
	return;
}


/**
 * Comment
 */
public void jButtonAddress_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	return;
}

public void disableFlag_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	// if no rows exist, clear the selection
	if( getTableModel().getRowCount() == 0 || getJTableEmail().getSelectedRow() == -1)
		return;
	ContactNotification currentTableSelection = getTableModel().getContactNotificationRow(getJTableEmail().getSelectedRow());
	currentTableSelection.setDisableFlag( getJCheckBoxDisable().isSelected() ? "Y" : "N"  );
	getTableModel().fireTableDataChanged();
	return;
	
}

/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	// store the previous selected row
	int newSelectedRow = getJTableEmail().getSelectedRow() - 1;

	// delete the row
	getTableModel().removeRowValue( getJTableEmail().getSelectedRow() );
	getTableModel().fireTableDataChanged();

	// if no rows exist, clear the selection
	if( getTableModel().getRowCount() == 0 )
		getJTableEmail().getSelectionModel().clearSelection();
	else
	{
		getJTableEmail().getSelectionModel().setSelectionInterval(newSelectedRow, newSelectedRow);
	}
	
	fireInputUpdate();
	return;
}


/**
 * Comment
 */
public void jTableEmail_SelectionModel(javax.swing.ListSelectionModel arg1) 
{
	getJButtonRemove().setEnabled( arg1.getMinSelectionIndex() >= 0 );

	fireInputUpdate();
			
	return;
}

private void checkEntry()
{
	Object o = getJComboBoxNotifyType().getSelectedItem();
	
	if( o != null && (o instanceof YukonListEntry) )
	{
		YukonListEntry entry = (YukonListEntry)o;
		
		//is there a good input into the text field?		
		getJButtonAdd().setEnabled(
				YukonListFuncs.isListEntryValid( 
						entry.getYukonDefID(),
						getJTextFieldAddress().getText()) );
	}	
} 

/**
 * Comment
 */
public void jTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) 
{
	checkEntry();
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ContactPanel aContactPanel;
		aContactPanel = new ContactPanel();
		frame.setContentPane(aContactPanel);
		frame.setSize(aContactPanel.getSize());
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


private void setSelectedLogin( Contact cnt )
{
	
	for( int i = 0; i < getJComboBoxLoginUser().getItemCount(); i++ )
	{
		Object user = getJComboBoxLoginUser().getItemAt(i);
		if( user instanceof LiteYukonUser
			 && ( ((LiteYukonUser)user).getLiteID() == cnt.getContact().getLogInID().intValue()) )
		{
			getJComboBoxLoginUser().setSelectedIndex( i );
			break;	
		}
	}	
}


/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object val) 
{
	if( val == null )
		return;
	
	Contact cnt = (Contact)val;

	getJTextFieldFirstName().setText( cnt.getContact().getContFirstName() );
	getJTextFieldLastName().setText( cnt.getContact().getContLastName() );

	//select the login ID
	setSelectedLogin( cnt );

	
	for( int i = 0; i < cnt.getContactNotifVect().size(); i++ )
	{
		ContactNotification cntNotif = (ContactNotification)cnt.getContactNotifVect().get(i);

		getTableModel().addRowValue( cntNotif );
	}	

}


/**
 * @param event ListSelectionEvent
 */
public void valueChanged(javax.swing.event.ListSelectionEvent event) 
{
	javax.swing.ListSelectionModel lsm = (javax.swing.ListSelectionModel) event.getSource();
	
	//only one should be selected
	int selectedRow = lsm.getMinSelectionIndex();
	
	if( lsm.isSelectionEmpty() )
	{
		getJButtonRemove().setEnabled(false);
		return;
	}
	else
	{
		getJButtonRemove().setEnabled(true);
	}
	
}
}