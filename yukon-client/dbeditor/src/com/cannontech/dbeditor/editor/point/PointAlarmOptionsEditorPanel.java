package com.cannontech.dbeditor.editor.point;
/**
 * This type was created in VisualAge.
 */

import java.util.List;

import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.dbeditor.wizard.contact.QuickContactPanel;

public class PointAlarmOptionsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private PointAlarmOptionsEditorTableModel tableModel = null;
	private javax.swing.JPanel ivjConfigurationPanel = null;
	private javax.swing.JScrollPane ivjJScrollPaneAlarmStates = null;
	private javax.swing.JTable ivjJTableAlarmStates = null;

	public static final LiteContact NONE_LITE_CONTACT =
			new LiteContact( CtiUtilities.NONE_ID, 
					null, CtiUtilities.STRING_NONE ); 

	
	private javax.swing.JCheckBox ivjJCheckBoxNotifyWhenAck = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisableAllAlarms = null;
	private javax.swing.JComboBox ivjJComboBoxGroup = null;
	private javax.swing.JLabel ivjJLabelGroup = null;
	private javax.swing.JButton ivjJButtonNewContact = null;
	private javax.swing.JComboBox ivjJComboBoxContact = null;
	private javax.swing.JLabel ivjJLabelContact = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointAlarmOptionsEditorPanel() {
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
	// user code end
	if (e.getSource() == getJCheckBoxDisableAllAlarms()) 
		connEtoC1(e);
	if (e.getSource() == getJCheckBoxNotifyWhenAck()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxContact()) 
		connEtoC3(e);
	if (e.getSource() == getJButtonNewContact()) 
		connEtoC2(e);
	// user code begin {2}
	
	if (e.getSource() == getJComboBoxGroup()) 
		fireInputUpdate();
	
	// user code end
}

/**
 * connEtoC1:  (AlarmInhibitCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (NewEmailButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.newEmailButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newContactButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (JComboBox1.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (JCheckBoxNotifyWhenAck.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88GC7D1C5AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D4D715F40D0A167A954595AB7DC40ACAEDD454F4656B52B53638520ACAF7E9C5CDEBF4D1D9B7352C32CA7B21FD197CCC7889A4A401A0D00690A010A13FC05249A464E77287C3B210E19258E8F9493CA4831319F1662519G25FB4E3D6F3CF9194CA48441ED3E6F643EFB6EBD771CFB6F39671EFB6ECD045C7D735312F53902101CA6A8BFD54902B06B02A08CFF78397B8937EE3BC3AFE8FE8EG
	E48B8F99D2B1136550AF949D7AACE1DFCB86E4F3A15FF349215F855FB9C2C1CBF5BBA2A37349023254B7AACBA76713BECAFC74AC7F3E462D10EE8348871C99E0E696727FD4E33E023F8279ED4AB7C232CE902C59D867313AFC8DFE05D24FGE5A7G5631BA7F5438D7A91F83F92C938BE9FCB62F3B9A52CDCC3A771B2A15FA3D3397896B1FB56AB2111726FD026A9D1C36D7C98DC29C9B0894E1571FDBA1B55B2E8D5675FB07CB7AC50F476B11257E2192E71F64F449DEFF0912783CAE0FECB259B0E9F00B7E61CE
	1F6C72FA824D2C58A6FAA43730E2874A7FDCDD86EB9B6407FCCF8E625ACF6318E541F791C0FD8E756139CCB375755AA2A171117DD17BB0AF074B9E1F936AC3597204D4DFAE367F93B6A7C8A7C03E9A004BF22409E4094BA5D9F61B30EE9264D3739C7A3DF904CFE47893103782B873C8465AA3A4E34F0D2F8B99A59FCA599EC5461BF9DC3605F9A19997BE11103A1B694DC703B87709D0163A53214F87B08374825C84584374755C68B1C89B7A64A66F703057536073B53A1D7EF3DE276406EF1389CA945C85295F
	6BF78A825749976A0C9164A11833F33CBEA2703EF58B24C331973EA4ECC878D0CADA043E45BDDF22DB243431F4E728EF9BF66238BD4966B6813E1315F6B97ED906B76F8A6179DCF5B2BC66FB20EC69AE9A671EEDA40BB5F50110D5FEDE270FA04B5C27DAF406FCAEC3F5FEC896BE46B157F10C1D001F81B097E0B140CA00F579B4461F982D1CEA0C5BE18D78BD223BEB244FA77A654691175BA97915317EF963E4E39D89B85DEF27CD4747E5D125F60E923EE60BB8AE890BCE6A366E6663D138BBB4AE1C3731A99A
	EF4B6E093A31CD1D4387581A68594D67B0C545FF026107B4F8BE377B999E73C1A8CBG60756DB5082F7FD3380E088C5F7B27F09D790509F4A488E589GFCFE8BEE22ED2C5F836B86C0861885908B309CA0E18F4D714FC6CBEFF39D59B4734BE9BF955C892994147DE23BA7A00B1EFE49A17ADDE21FDB8A88CA1D05C19C573EF1F40F32755D8FBCDAA50F649751BCC26B6230844A9460F53E9A4C46BAFE09155ADD4392B0987689546E93419CC85DE2C03E68F30A32942A414B1F8A62183CADBA20C5E8G3E0DFBF1
	3CDEE6B6F7A374BFFFAF0DD567BB0873C1FE66DE9A175A319D101E8565165CA61345550FE2087EB1525127CFE7C1BA8F6A9B77415A84E8FB0356672FFBC88FAD19DF940C4FF7C654C384E1BF57297C7D61FA381235DF8F78ABGB3G666F27FD7562691EE92E0FC800F2237C4CD66D63A9760173705C959EE90A0E535A26354A477D49DB283B71502F84000D7BE96C03E3862E130DE2C0A2AD6C740382C94E50CC703A8FDC47BDC6FADB72489649A7F91C7091D0F4E04E75CDA17D9C702A3A739043233DEA82858A
	2878795733B99E5A50EABB15AFE26522AC7BDDFDA33254A549324BB39822DF4CE4890CB30581252FEB5E453E669AC0DFG4C866A6B1BB7F2B4FAE675FA2F0D786C380294FDEDE27B328D6A8F99E8EEBEE6C2BFEB161AFF1865C36507CCF7E36E88104FC49BDDF7D7F9240465B90F4BB538861215BC0ED729DAB6484A62C03684005483B02E87287FC9CC36E4253ED6772602BD37D10773C82B69D1645E2451755B61F1BB32F05DB05E0C2E5B15B7A36B761BB7A36B76691B136BEE6B4DC83A4B4BBAEEE25F3C239E
	4DDA683A0976315F6B71C8ECC12AA44A1A7F359D7B61723864EE17F3D0123BDC57A5707B8E20CE271EC61D36437750813219238749E696DEFFD408FF6B18AE12FFB577336D3A7883DC9FD79D8859CCAE137D9DDCE312DB9A0601EA720EF8E40E3F4C6C3B028F24B05C0D9BB1C8DBF426E9C86A3F56688DDADC81DCF68DEEB7F35A83C611752B04A7B649BF60758FCB4E222B625BE209B8AA1730F9A9E9E0454D78FD54F07CF81DE1B23F5FB86072CB6D9E5F0832E9959DF59CAD63727CFB41FD636569704A2E0131
	6EA1495350FF6D6FA5CC252253B942FF965FD264718E77F9C19EF023DDBE97347C7712253C12E479157E8BDC164691D976FABA2451A6B8FB817FFBA20AC7ED1E33FB2BA12CF5EB7466EBC06E0ACA5A5FF6A523BE2E87BD4DA8F0688D85E473DFF8874B4966AB9BDB13576D66CB89753C002D137ADC0D6ED79770B3F48C243C7C7D914B9355727406C8656B55724C56C8659987C94E27EF210D8DC8FF9D1140354355F1D69AD37C33E7EF6D88ABF3086E11105F77EF3736C06A566CE79C7F2BF7A32F51F56F629EF6
	CDEDFF7D3B69827AEF3CCDEED3960B58264F6F9A066D9FB7E8A5D6F039C99BAB588B72E3DC2197D29FG275DFA837D6149A65ED048E293B594D3188934738162157604C2455E30FAEF19291E9B72B1G8B8BA355BBD8CB7556C0FEA3405EC3116A7DD22D578B65B2409C255E2AC3CA3D43D8EF4FE9BA27172A388A06AB070D651594BA8B1D4B5246BCE2FEF3BD22BDC5F09E666D878F071F912DBAECF7AE60979DA61D7088DAFF25A19070763B180B2E4C6F3EE42D2D653802644D1042928F40F951A985C9C70A12
	D1D674619534B24F0F95578A934FA4FC2A489DEC9AE58B4E6EED02E3C49B28D9133A95B9DC52A8507338C6CDBD0D57524334573C53220D25AC5DDE255B92E5EC56ABE312FBA4B4B666A792D2F95BCB55366DC7A80E72F48B61E629B8CBAB61169DA1FD7C2489EDED24F8D2BF6BF44960082BE4C4F66161C314C5E69B353123E54CAF4436B69E21384CCBADACAED36450DB811CG7C0C380C1D9D316ECD404595517A7FF42DF67DA795F19FB37728C34F6DEE31398252695BD81A47E5C7F770F98FB36D24238BF26A
	955EAB3E34C0B03F1B2F0B94AF1A393DC087C4EC7CEDC7C373E0B8BE26DB597B2292571AE926BE4E03BA2B0ED29F638B437B387518C3DFF60C7A784A9D771173355692DF8B34F97398717DCB2916EF5CB14EB7C1657B355A3F0DEFD69D71DD87ED9695935F6AAAAD5FAB451C6F4CE262BBDA77374D69178F1F0674EF5CA3A926D62135D1710927225A28F945FC5E9797075B2832D4DC87ABG1FD8CCB66A1164EC0D0D8247C596BD033083115DB92F4331003DC649DFE4FC59DEA310FFDFDFBF1D98547AE20AA58C
	276178E7989D7AC2GB3009DC0B612EC893AA9E3EA4D41C9E2EA5F1C16DCF30CF4A6DA5EB29D3AC5AADDFB6BF468162AF44FCC08232DB4064E11EB0C93E3E9BC4EF5219575FA0311473974AA7EAF8C1FF5BC04676BE08D33F918AF0232D947097F33B5617C579D9F4FF3EBC924F36D6A89F4A5E17932B0BA3EAE6A9CB83F4C1E236A87CB8223F03EB219D4FFDE315BAF54223E66813415104EBC1D0EFBFFAC641783A482EC84B014027EGDCA9A55DA9CE0934AF0E571DE81BE2A816417B7991731D0D4F3C89F461
	63B323F4BC9D7751BE512A75516616723DE6A924ABCBE91F7CFDE10642E7EDA94D63055ABB13734A843AF0B98DE51166F1509DED9E15F310B20F9FAF25BD3D3A0CE45DB961EE6176E4AD1CC077C6D85E97D1566DDD3BA34B4A0D32C0BE10370A46F5E69969DCCA866262A13F320C74EBE68A5EA9115DE43E32C0B673E4AA7A63DE3FEB5065A11C09617A3DFE969860712AEE6693A5C21B9B55F6C7522247B1EE26C50EE37CC3DA64B8464C3449E3F48F26C50BF3F079D62B72E515BB749C77139642952A38AB2D04
	2BD6F1094C6732C27E0A0AFB2C99F1B1101FD7CEFA5043ECD5BC649381D2AB886FE4F85C0F0D957CEC41713FEA19AA16066DE00AEDCDE503634BA369D8CA6BD4BC52B4752762919E2F5B47F04F6C5C4E66A69010824C9021672A147DD24E93A2440BBA44E1C920F3FE32F913D227D976B6152B327F38B6126CB6E5AC1CCA9A540C4DAC9CDF00383071DA8E798400F5G29153C0C6B4DF753236B682A7448BA7A23F4DCAF816F083FDF8A57431F26672BE5A1AB5CAECB4358E565AC4C68E551BFA8CD50637FE6743C
	AC9A7DEB6951F41D0F6737460D678955EE7C76E8FEE43B418E985CE6FC3F15ECC6FEA51DAF7EF09075578679A1009915243F9B8B3576EB2EB22E31CA9ADFB9D1E739FC9F2A554AD7294E77C32513799C2B2236F72ECE5BDE055A1EDC25EDEF2DC23FD1C933CECCEC0F0FEFC6867A9AC1B53F15654754F1149A3629F3546CF1AA86131F49BD4C4EBC8902BD2FA35CAB8C77B860B2138977BBE6FBBE1D22BD4F0F3231BF1DAA8833553E7D3E89ED2DDB9C730EG0F87CFE03DED965AD38BA1EF86F08284157EF0993F
	B20AE75B813FB88CD35C65137A8577C924FD2A19E8D7C37DA4002D00B782D8CF52D96161CABC3F01768D3886C7681A8E0F200ACC758E3C7FBB53556F773A5DD842A2ABDDB21C7282D4E745D95CEF22C6D985728F3ED825758F62CEF27FA0915275A749567E0179CEDBCFB974FBCF912E0F5D6795C13EDE45BD30134517A16FD3F107F6B1BBFD0A7B0F4A5D8A434D875CD2355E8E9362D6C2BED1457DCB8A6256C3BE230AF06BAF30B7B418D7F12945082B2F62E7FC0EBBD8CC7B4315AADAAF2B4679540B813F2CAA
	24032B2B227946A3056CBDC6956FC3260A0FE1782C6A901E7B46A98C0F7993D0B63F1A644CB9444671CD9847B789B7D40338B248572B386F56106CD6957715D2F53FD3F177B37FA289729BD51C319CF1D9A670E3CD041BDDCE34C69345A6CED968BD4ACD404519A8BED2FEE52F56C661D152E4EA753B1C0D6220152D82EE27EED9B2947AA4935929D856E6E18D2C89GFB8D45FD0207A96E937B398502F506AD629B1AC4FB154EDD436760411A50F9F3099A77D95BCCBCD7C0F92A99F1BBDB3467EE547D9F68384F
	494B5B4615235D06A335267C7A38F214B7B9C5DB9EBB2EBCCDC34F7B761DC354370D5FD9AC389F2F0B7ABE48EA66FD72A969721103C25287DFCCD57C5F66FDC2443DA1344A8572ABFF4E669D79AFB253582F6B4131CF047C86143B16ECC9EE9D46F7E558C40BEA716D5890EE370A0D982BC39BB5AC86DD43A3436C22E8E1AD539736365D709DD45BD938937B458397F88C92BDCE48737ACF32F53F963E572B754D3B30FE1F97036CB6918C3464D479DEE6E5BE3FB4A0797D1213DFD1A9ED8518ED482A03B5D2C7ED
	9DE5772C83DE0F4C6B1CE636228D4A6DEA1D67D352D9DF7C4CCF66FDFDA1AB5457ADDD2C2FAECFD8DF8FF63135816DBC2836352798E32DC360F4DF47E08B18CC572087632AC2C29D52180AAD9166D72B03FAA5E571CF20C1B8286845CA78FEC779666B733F583ED3F89AFC5E5334E64FF4A22E9772BE9577F59B2D63B92749265DA8C47B3D867299753066814CGBDGF2BD5579B02B033172B9000B83D8D54FE362986F66F5B695DED16447F6347D116B237B3E48579C253CC711E722CFDC28728A3F3B4D1CE2DC
	B7C5A95BA2CC741F79DC9EBBB4DEF76A198DDE8FF2898DB45F6F560CDFA32F2219D066FACD4D6D4CB53D37881F6BB260013A747B57DFFF1D37373CF4327652B4EDA1189BC26DA15E8679C3CA5BDC7685D5637B37045906DE28B724762F29FCFC7F9AF2C27DDBD4BE19BC7AB0F9427BB737016FFD949B4A964C16BBFBFBF6668E695EB7EDBA3EF75FEAC6FB39D0A3BF47FF106197EB70FC3F7C2A3ADFAE05B2C3A31D678BF3595A027CFC00A5GAB815681ECED22F3786C0CB2E10A78115D6B3342A1482DB9DA737E
	3D5CFCE7EF723E2927FDA96E09AF88CBBE704764E8F7AD06A63EC66B15740AFC1061F97F3E1E077D73C159AC0045G4B8192G562B7D7BE10611778F2F0A9AE4D96C9FE237D054B93C988A21519966FDDBEC223EE5D8405E8314D9C27642E209660FB9AF31B7FF1670B7FF9E069F32043F797BDD0F7A668F4A92AC6417FC7FFF35F1702C078E6BE84E437956B7F3D92C4DA13F042F25D770993732967FE167FE0145688DEA945E6202AE63C33822723293C6C34529234A7C4C9F55722E5EEC0E9A031F21701C2324
	A7BEFFD0595FAE6D8E71FEE397EE6C854C3BB734FB06DCFDAE39C029B77BCF21FAFA536D4848690E99C2F4DFCDA98827EB8123D3D460F409EE6FE00151D0E6B4141FB8CAFB6FC78F04E8DFBDF6BBBC77333A208BC268073775B832D6466D27DBC92F77BE7608E079419F134996050F5B8AE53C76360466AC77FFDECC65B2695A50DF607E891D6B765A68ACE4879AB740A200258ABD2793BBA71E87031DC4372A051F8713A095DAB99D37335F33CD350FE8A19FE80D2D5C4EE23E3E156F533F05FD044B914B4F0091
	DF2D28BAD10FEB7E0CD56C135C2DFE6F08CF415F6A5A46E81548AAAFD24AD677E62AB44AD5135A777CCDD9DABA2518174D2C46CDF342CB91015ED45C1F2975232D2D7CFE4C57CA77E75FEF0BF4EF972316FFA1BB927DDCC8390E3CF4A824EF3DB553535564A3C8BBD17F561D666505C5A1DD1CF1AC4447D0FEBBFCAAD4BE3692246536AF26B9747D0066FB0965EE3E6F0B3CE7BEF7CFF970BD61F8EE74385E095C48F1BDD3EE6458F3FD6E64EF685AF2233F216B4C553E2163FB1A015D9147026EAC87C882905A9C
	7A5CB65A4FDE4C45F9459758937732E667A04695715535CC7C63732277755BF9D33F55DE19076FB47A3C30F40631FD423F100773A5FBFD8C49F1BF4E43B1723B8607E4DE15621EF96C4CA68DB0B4BFAFFC1B1D134A20DF76B6BAC398D8ECE188728F2A386B8C379072CBD45C22E4BAEBA4345199616AA574A30D6D1417D95E2A66B7765C1577516D114F931CC75E253B41A3BC0ECE407523965DB5E5DC7F3A938D0BC33C9A71B54C99D8771DFDD725FE59AE8E2A74C7D83C45877D1855CEBAF5A23FB8DABB003479
	3DBE49AF0FF1ACFB51253455DF017BC6AC34339CA089A0750CC31F8FD086E0816881F08344G4C83D88C308CE0B540DA000DGD9E7C90E578A0ECEA2079268E18A6E9779BD947721DE33118FD588ED751E25F851131BF130EE6BBD0D5DA53B25C6366372B8C663F9FACBB367AC45B19E6DC0BEABA03F86E063D90AD74D6FA47FBB576A50DB2DF4F7393613E2C2B1D6EAE3A713F5A964D781A481C4326D516DFD5DE1C1D0E37683D1E276EC0764B2BC15C9B17B54F3A4D7FABB754D2C62FE1ACD7564F3B49FC52551
	EDC7E9695436231A4585A5D87376B11FF2BFD1DB4A6E8C87868212EA239ACB33D4DCFB40668D38B01243773117CC68443E8D8A686A975DBC6054A87AEDDE372BFFCC09133C4AEC49F15445F3F42E2C2AC19D8D45C942A9A92653456CCB83C012CABB3BE6227F3528067CA8E387773FAC1076F4E87D28EFA4CF65C7E94F2BBE200D698879D1F3617BB3BAC148CB26BD3C2D1579ADF69E9D570451157878E7AFE963638BBB789D62F2C893BAE88E0D65D8266ECFFC7952FB71F25CEB70BDB678842E6BAC185F8595BD
	033CFC34A27A7C5F2A18FA7EBFD891F96FF820E2625E71510A48FB475C8A6D5E41F18FD760B88E02BD68D16BADD0F117D45C67D91B086BF6B965A1957FC5A697625BA4E4C8782FB139C6992EC741AD5160AEA938A7ABD0BF204B20DEC47BD4850BF5385CEE1E7FD7D647658115D64763BBCCC617CF54483DD245DD92E89F914ECFF52EDE5F91A9F635EDCA3A70B7841C2E1551E5F5CEFD0F0CD058C9FF17360F593D6A4E70B845E10637F40647A93E5CC1F65286E5F11D34B7FF2E734E62ABF1F7C877723469F83F
	BE4CECF73EAD3C3F9FE378C2DBF8FF775A283FE5D0B647C67D7DF626167F869B9F772D6715336079097340693634CFA6778920EBBB9FC9AFFE112D256BD56A7814B4A682BFEEFF32EA703EDD333514E8A27EA50E668B767626866BDBDDCD97BA2D5646068BEFB5D83B9BAEF52974D7EBAAA721EF6B3C50FE3933439E2985BEFEABEBE8DF1F8BB22EBCCFFBD53E19762F0C8B342F6A593B7AFA4C8378AECCF7DF9559B90B5B672BB7226C2B4C92F399DE3CC4EFFDE62AB2A43177267110DF8D308E003FF3084F41F3
	2B4B63F6F924A67E970E8245577F427C0C0CAE075E50C57EEDF98762CEC03E2D0BF466E027204E27D1737DF8473DBF13FD6F6F4063C1DB28CF1F54FC97586EBD6F1AFB4A034F69CB19F43FEAD767D937196ED707D4DC7FE6BA5B48AA2E201DF04BD4DCA77357F26D8EFD11DD3D8B4FA69EE695F7DFB7713029381FF5D3FB3DF6522F97A63CF18C0748FB4E1AA969A26FB97B581B41D87B6D6DB971F65AF3EE30F53E4A9EEE03E398BE519EEE03B3134986A7C1196DA27577E717A613BB6EE234786E957636FB7945
	F07EAE06DFF5B11C7FEF4C44BF914AAC8E72B1C74E23EFA263318363B28279238FB0FB30886A4529F55BBA52282EC0711A98967761B47AEDC833866A2FF3D04C6973F814D27C526B1B27935F8B3FC7B1F4F3BB763AFACF74E67BD477C45A760C5D636F096A3B436F098632F1CEA48FDAD7275227F732493EDA21BE479D5E416E0FA0BF54CD7DFC76D8281F2FF6CF477F1EF0DF544DFD66505D6789B35966055D34F66688E41B17F413AE5D477EF506F21F29794EC93E77364BF8CFF970BE3FFAC935BFEA1F87EAC3
	7D74EA3EB7186FFD1F33A33E7D3BDB4075EAAE9EA7957DBC54B13D186DD8D90876F71B26C7BB8FDD2EC9623D5F288835BDA71B5D1D7AC7A4DE3667E428EC24FBFAFC6BCEA1ADDFE7FFE6FA3F9666FA3D3A4E44AAAC676F4432AE2BEF4CFA9C7AEA009E00A0405C9E2A7F641BA1D9E68A37AB8B772DCEDD263849129EFACB633C9C7E16E6F63276AD4CBFDE8EFFCB0374217219667037B463691FB0073F25A1FA45C738BC5DB3837F9FCA6D7B7001DF6C4BC21AAC6CD0DE3A999A5CEE83FF6B96708B5B55C1E9E727
	454437ABCDA97F71C80ACEC8ED9752796939DDC845A3F03B1046CE486DC28A1C1E03781FAFF4DA1005FCFE59E2405B96C3C37FB50FF7540D819827ACE4B661BF4E108CB08FA5A5A540F847449B2F060F7C708731EFD9F2F5028EF88FC91CD95AA89E1F99BB3C2D0383F366383B3C40DF05DBEC48D8A9341CB9EE1E3F7519133FD87112A310A224378B3B71B9C01764E7918749092F0115FF4751A2ECE27FA6899F40C0FF83ACC98B48E3EE49AA648EDC40BFAB8D48123301E29981D948E3FFD3D40AB13C52B6B1B0
	A4637056D805D48F7E6390E187AFF60B1E41D29E5828E13DC8EE38429695CE13G7266B0F931159235151CFF5EFE684019B5CBD284DD0B30596D951DADE23F6C75378B1B64A13F9498723A1DC15F69FA56D046A8EAAC5EE825C0F3FB546EE3E7CB6CD2D0D6053E96FCF8D6665A4C96182F20DFA8E5B57175C309DA4359B4A47A011564EF17719F21F87D05EA59FDEF48073F5B3A2BD7A7A4AB1D44215F927449C27A00B86CF20F4187DFD5ECD2364BA02DDF748470CF30C0B2C318E4D424B2A87958B3E59FFF7435
	4B0CC108D496767321845252C149BBDC5AB0B0G2799358198D7E8FE1F5ABC56A9892B33A3B8BB6F4C732F77019ED804146132B2763B1C7D2E405F658CD34EB06595B0386B04C9FF424B0354E726BC6A524269AFE9019914FF7472B38BFEECDE17843DEB9152589FF741A4E405EAF50C8C77C97E8F44FEFC5F16B90977639C79D0D50258EECEB0CC4FB1C70FAB5120BC47269F311BA66898E2B3A7AA8B2233E14AACEC4AB0174B2A6EE43FF7CFFBG10EA8F235D274ADD98D4A5C539D0FE8C6B69D4EC2AE2BEB0CD
	55484F255D7A7FA5FF3D12B7066457C5159FA546205E26F1F5891BAA8BDB19E1F47533A0A7C84CAE16CD321021DC861B5EFB93D79DFC6F49D4971A8CFA8F06831CB2EE58C919125FFB72370464CFD764C7E953C191581606F7376FB921BB027CFD4951F7356FF2B63CFFDF54F7B9EBC5656F9944703F0DFD74G0ADE5D67505B7B680E60D583BA3B516E913B06C537DB965D579ABCCE3CD3EC9C63F7901C76CB87307DG55D18AF9596387D06E2B9A1A7F83D0CB8788BF39B941609BGG34D2GGD0CB818294G
	94G88G88GC7D1C5AEBF39B941609BGG34D2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9A9CGGGG
**end of data**/
}

/**
 * Return the ConfigurationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getConfigurationPanel() {
	if (ivjConfigurationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitle("Notification");
			ivjConfigurationPanel = new javax.swing.JPanel();
			ivjConfigurationPanel.setName("ConfigurationPanel");
			ivjConfigurationPanel.setBorder(ivjLocalBorder);
			ivjConfigurationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxNotifyWhenAck = new java.awt.GridBagConstraints();
			constraintsJCheckBoxNotifyWhenAck.gridx = 1; constraintsJCheckBoxNotifyWhenAck.gridy = 3;
			constraintsJCheckBoxNotifyWhenAck.gridwidth = 2;
			constraintsJCheckBoxNotifyWhenAck.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxNotifyWhenAck.ipadx = 9;
			constraintsJCheckBoxNotifyWhenAck.ipady = -5;
			constraintsJCheckBoxNotifyWhenAck.insets = new java.awt.Insets(2, 5, 9, 15);
			getConfigurationPanel().add(getJCheckBoxNotifyWhenAck(), constraintsJCheckBoxNotifyWhenAck);

			java.awt.GridBagConstraints constraintsJLabelGroup = new java.awt.GridBagConstraints();
			constraintsJLabelGroup.gridx = 1; constraintsJLabelGroup.gridy = 1;
			constraintsJLabelGroup.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGroup.ipadx = 14;
			constraintsJLabelGroup.ipady = -1;
			constraintsJLabelGroup.insets = new java.awt.Insets(3, 5, 5, 10);
			getConfigurationPanel().add(getJLabelGroup(), constraintsJLabelGroup);

			java.awt.GridBagConstraints constraintsJComboBoxGroup = new java.awt.GridBagConstraints();
			constraintsJComboBoxGroup.gridx = 2; constraintsJComboBoxGroup.gridy = 1;
			constraintsJComboBoxGroup.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxGroup.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxGroup.weightx = 1.0;
			constraintsJComboBoxGroup.ipadx = 18;
			constraintsJComboBoxGroup.insets = new java.awt.Insets(1, 2, 2, 4);
			getConfigurationPanel().add(getJComboBoxGroup(), constraintsJComboBoxGroup);

			java.awt.GridBagConstraints constraintsJLabelContact = new java.awt.GridBagConstraints();
			constraintsJLabelContact.gridx = 1; constraintsJLabelContact.gridy = 2;
			constraintsJLabelContact.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelContact.ipadx = 13;
			constraintsJLabelContact.insets = new java.awt.Insets(6, 5, 6, 2);
			getConfigurationPanel().add(getJLabelContact(), constraintsJLabelContact);

			java.awt.GridBagConstraints constraintsJComboBoxContact = new java.awt.GridBagConstraints();
			constraintsJComboBoxContact.gridx = 2; constraintsJComboBoxContact.gridy = 2;
			constraintsJComboBoxContact.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxContact.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxContact.weightx = 1.0;
			constraintsJComboBoxContact.ipadx = 18;
			constraintsJComboBoxContact.insets = new java.awt.Insets(4, 2, 4, 4);
			getConfigurationPanel().add(getJComboBoxContact(), constraintsJComboBoxContact);

			java.awt.GridBagConstraints constraintsJButtonNewContact = new java.awt.GridBagConstraints();
			constraintsJButtonNewContact.gridx = 3; constraintsJButtonNewContact.gridy = 2;
			constraintsJButtonNewContact.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonNewContact.insets = new java.awt.Insets(2, 5, 2, 15);
			getConfigurationPanel().add(getJButtonNewContact(), constraintsJButtonNewContact);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfigurationPanel;
}

/**
 * Return the NewEmailButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonNewContact() {
	if (ivjJButtonNewContact == null) {
		try {
			ivjJButtonNewContact = new javax.swing.JButton();
			ivjJButtonNewContact.setName("JButtonNewContact");
			ivjJButtonNewContact.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJButtonNewContact.setText("Create new...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonNewContact;
}

/**
 * Return the AlarmInhibitCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisableAllAlarms() {
	if (ivjJCheckBoxDisableAllAlarms == null) {
		try {
			ivjJCheckBoxDisableAllAlarms = new javax.swing.JCheckBox();
			ivjJCheckBoxDisableAllAlarms.setName("JCheckBoxDisableAllAlarms");
			ivjJCheckBoxDisableAllAlarms.setText("Disable All Alarms");
			ivjJCheckBoxDisableAllAlarms.setMaximumSize(new java.awt.Dimension(104, 26));
			ivjJCheckBoxDisableAllAlarms.setActionCommand("Alarm Inhibit");
			ivjJCheckBoxDisableAllAlarms.setBorderPainted(false);
			ivjJCheckBoxDisableAllAlarms.setPreferredSize(new java.awt.Dimension(104, 26));
			ivjJCheckBoxDisableAllAlarms.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDisableAllAlarms.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJCheckBoxDisableAllAlarms.setMinimumSize(new java.awt.Dimension(104, 26));
			ivjJCheckBoxDisableAllAlarms.setHorizontalAlignment(2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisableAllAlarms;
}

/**
 * Return the JCheckBoxNotifyWhenAck property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxNotifyWhenAck() {
	if (ivjJCheckBoxNotifyWhenAck == null) {
		try {
			ivjJCheckBoxNotifyWhenAck = new javax.swing.JCheckBox();
			ivjJCheckBoxNotifyWhenAck.setName("JCheckBoxNotifyWhenAck");
			ivjJCheckBoxNotifyWhenAck.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxNotifyWhenAck.setText("Notify When Acknowledged");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxNotifyWhenAck;
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxContact() {
	if (ivjJComboBoxContact == null) {
		try {
			ivjJComboBoxContact = new javax.swing.JComboBox();
			ivjJComboBoxContact.setName("JComboBoxContact");
			// user code begin {1}

			refillContactComboBox();
			
			ivjJComboBoxContact.setToolTipText("Will use the first e-mail for this contact");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxContact;
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGroup() {
	if (ivjJComboBoxGroup == null) {
		try {
			ivjJComboBoxGroup = new javax.swing.JComboBox();
			ivjJComboBoxGroup.setName("JComboBoxGroup");
			ivjJComboBoxGroup.setEnabled(true);
			// user code begin {1}

			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List notifGroups = cache.getAllContactNotificationGroups();

				for( int i = 0; i < notifGroups.size(); i++ )
					ivjJComboBoxGroup.addItem( notifGroups.get(i) );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGroup;
}

/**
 * Return the JLabelEmail property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelContact() {
	if (ivjJLabelContact == null) {
		try {
			ivjJLabelContact = new javax.swing.JLabel();
			ivjJLabelContact.setName("JLabelContact");
			ivjJLabelContact.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelContact.setText("Contact:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelContact;
}

/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGroup() {
	if (ivjJLabelGroup == null) {
		try {
			ivjJLabelGroup = new javax.swing.JLabel();
			ivjJLabelGroup.setName("JLabelGroup");
			ivjJLabelGroup.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGroup.setText("Group:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroup;
}

/**
 * Return the JScrollPaneAlarmStates property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAlarmStates() {
	if (ivjJScrollPaneAlarmStates == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder1.setTitle("Alarming");
			ivjJScrollPaneAlarmStates = new javax.swing.JScrollPane();
			ivjJScrollPaneAlarmStates.setName("JScrollPaneAlarmStates");
			ivjJScrollPaneAlarmStates.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAlarmStates.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAlarmStates.setBorder(ivjLocalBorder1);
			getJScrollPaneAlarmStates().setViewportView(getJTableAlarmStates());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAlarmStates;
}

/**
 * Return the JTableAlarmStates property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableAlarmStates() {
	if (ivjJTableAlarmStates == null) {
		try {
			ivjJTableAlarmStates = new javax.swing.JTable();
			ivjJTableAlarmStates.setName("JTableAlarmStates");
			getJScrollPaneAlarmStates().setColumnHeaderView(ivjJTableAlarmStates.getTableHeader());
			ivjJTableAlarmStates.setBounds(0, 0, 200, 200);
			// user code begin {1}
			
			ivjJTableAlarmStates.setAutoCreateColumnsFromModel(true);
			ivjJTableAlarmStates.setModel( getTableModel() );
			ivjJTableAlarmStates.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableAlarmStates.setGridColor( java.awt.Color.black );
			//ivjJTableAlarmStates.setDefaultRenderer( Object.class, new ReceiverCellRenderer() );
			ivjJTableAlarmStates.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableAlarmStates.setRowHeight(20);
			
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableAlarmStates;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/00 4:58:59 PM)
 * @return com.cannontech.dbeditor.editor.point.PointAlarmOptionsEditorTableModel
 */
private PointAlarmOptionsEditorTableModel getTableModel() 
{
	if( tableModel == null )
		tableModel = new PointAlarmOptionsEditorTableModel();
		
	return tableModel;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	//Consider commonObject an instance of com.cannontech.database.data.point.PointBase
	com.cannontech.database.data.point.PointBase point = 
			(com.cannontech.database.data.point.PointBase) val;

	Character alarmInhibit;
	if( getJCheckBoxDisableAllAlarms().isSelected() )
		alarmInhibit = new Character('Y');
	else
		alarmInhibit = new Character('N');

	point.getPoint().setAlarmInhibit( alarmInhibit );

	// Set all the values for the PointAlarming structure
	String alarmStates = new String();
	String excludeNotifyState = new String();

	int i = 0;
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )	
	{
		java.util.List liteAlarmStates = cache.getAllAlarmCategories();
		
		for( i = 0; i < getJTableAlarmStates().getRowCount(); i++ )
		{
			int alarmStateID = com.cannontech.database.db.point.PointAlarming.NONE_NOTIFICATIONID;
			
			for( int j = 0; j < liteAlarmStates.size(); j++ )
			{
				if( ((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarmStates.get(j)).getCategoryName() == getTableModel().getGenerateAt(i) )
				{
					alarmStateID = ((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarmStates.get(j)).getAlarmStateID();
					break;
				}
			}
				
			char generate = (char)alarmStateID;
			boolean notify = getTableModel().getDisableAt(i);

			alarmStates += generate;
			excludeNotifyState += (notify == true ? "Y" : "N");
		}
	}
	
	// fill in the rest of the alarmStates and excludeNotifyState so we have 32 chars
	alarmStates += com.cannontech.database.db.point.PointAlarming.DEFAULT_ALARM_STATES.substring(i);
	excludeNotifyState += com.cannontech.database.db.point.PointAlarming.DEFAULT_EXCLUDE_NOTIFY.substring(i);
	
	point.getPointAlarming().setAlarmStates(alarmStates);
	point.getPointAlarming().setExcludeNotifyStates(excludeNotifyState);
		
	if( getJCheckBoxNotifyWhenAck().isSelected() )
		point.getPointAlarming().setNotifyOnAcknowledge("Y");
	else
		point.getPointAlarming().setNotifyOnAcknowledge("N");

	// get the selected contact from its combo box
	LiteContact contact = (LiteContact)getJComboBoxContact().getSelectedItem();
	
	point.getPointAlarming().setRecipientID( new Integer( findEmailContact(contact) ) );
	

	// get the selected notificationGroup from its combo box and insert its id
	LiteNotificationGroup grp = 
		(LiteNotificationGroup)getJComboBoxGroup().getSelectedItem();

	point.getPointAlarming().setNotificationGroupID( new Integer(grp.getNotificationGroupID()) );
	
	return point;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getJComboBoxGroup().addActionListener(this);
	
	// user code end
	getJCheckBoxDisableAllAlarms().addActionListener(this);
	getJCheckBoxNotifyWhenAck().addActionListener(this);
	getJComboBoxContact().addActionListener(this);
	getJButtonNewContact().addActionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointAlarmOptionsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(384, 363);

		java.awt.GridBagConstraints constraintsConfigurationPanel = new java.awt.GridBagConstraints();
		constraintsConfigurationPanel.gridx = 1; constraintsConfigurationPanel.gridy = 1;
		constraintsConfigurationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsConfigurationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsConfigurationPanel.weightx = 1.0;
		constraintsConfigurationPanel.weighty = 1.0;
		constraintsConfigurationPanel.ipadx = 3;
		constraintsConfigurationPanel.ipady = -7;
		constraintsConfigurationPanel.insets = new java.awt.Insets(9, 9, 4, 10);
		add(getConfigurationPanel(), constraintsConfigurationPanel);

		java.awt.GridBagConstraints constraintsJScrollPaneAlarmStates = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAlarmStates.gridx = 1; constraintsJScrollPaneAlarmStates.gridy = 2;
		constraintsJScrollPaneAlarmStates.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAlarmStates.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneAlarmStates.weightx = 1.0;
		constraintsJScrollPaneAlarmStates.weighty = 1.0;
		constraintsJScrollPaneAlarmStates.ipadx = 336;
		constraintsJScrollPaneAlarmStates.ipady = 138;
		constraintsJScrollPaneAlarmStates.insets = new java.awt.Insets(4, 9, 2, 10);
		add(getJScrollPaneAlarmStates(), constraintsJScrollPaneAlarmStates);

		java.awt.GridBagConstraints constraintsJCheckBoxDisableAllAlarms = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDisableAllAlarms.gridx = 1; constraintsJCheckBoxDisableAllAlarms.gridy = 3;
		constraintsJCheckBoxDisableAllAlarms.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxDisableAllAlarms.ipadx = 62;
		constraintsJCheckBoxDisableAllAlarms.insets = new java.awt.Insets(3, 9, 13, 209);
		add(getJCheckBoxDisableAllAlarms(), constraintsJCheckBoxDisableAllAlarms);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initJTableCellComponents();
	
	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (11/15/00 10:52:29 AM)
 */
private void initJTableCellComponents() 
{
	// Do any column specific initialization here
	javax.swing.table.TableColumn nameColumn = getJTableAlarmStates().getColumnModel().getColumn(PointAlarmOptionsEditorTableModel.CONDITION_COLUMN);
	javax.swing.table.TableColumn generateColumn = getJTableAlarmStates().getColumnModel().getColumn(PointAlarmOptionsEditorTableModel.CATEGORY_COLUMN);
	javax.swing.table.TableColumn notifyColumn = getJTableAlarmStates().getColumnModel().getColumn(PointAlarmOptionsEditorTableModel.EXNOTIFY_COLUMN);
	nameColumn.setPreferredWidth(120);
	generateColumn.setPreferredWidth(120);
	notifyColumn.setPreferredWidth(50);
	
	//Create new TableHeaderRenderers		
// DOES NOT WORK IN IBM's JRE1.3 DECAUSE THE getHeaderRenderer() IS NULL
/*	nameColumn.setHeaderRenderer( new javax.swing.table.DefaultTableCellRenderer.UIResource() );
	generateColumn.setHeaderRenderer( new javax.swing.table.DefaultTableCellRenderer.UIResource() );
	notifyColumn.setHeaderRenderer( new javax.swing.table.DefaultTableCellRenderer.UIResource() );

	//Assign the tableHeaderRenderers som toolTips
	((javax.swing.JComponent)nameColumn.getHeaderRenderer()).setToolTipText("Alarm Name");
	((javax.swing.JComponent)generateColumn.getHeaderRenderer()).setToolTipText("What group the alarm belongs to");
	((javax.swing.JComponent)notifyColumn.getHeaderRenderer()).setToolTipText("Click to enable/disable notification");
*/

	// Create and add the column renderers	
	com.cannontech.common.gui.util.CheckBoxTableRenderer bxRender = new com.cannontech.common.gui.util.CheckBoxTableRenderer();
	bxRender.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
	com.cannontech.common.gui.util.ComboBoxTableRenderer comboBxRender = new com.cannontech.common.gui.util.ComboBoxTableRenderer();

	// Get the alarm data from the cache	
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )	
	{
		java.util.List allAlarmStates = cache.getAllAlarmCategories();
	
		for( int i = 0; i < allAlarmStates.size(); i++ )
			comboBxRender.addItem( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(i)).getCategoryName() );

		generateColumn.setCellRenderer(comboBxRender);
		notifyColumn.setCellRenderer(bxRender);


		// Create and add the column CellEditors
		javax.swing.JCheckBox chkBox = new javax.swing.JCheckBox();			
		chkBox.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
		chkBox.setBackground(getJTableAlarmStates().getBackground());
		javax.swing.JComboBox combo = new javax.swing.JComboBox();
		combo.setBackground(getJTableAlarmStates().getBackground());
		combo.addActionListener( new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e) 
			{
				fireInputUpdate();
			}
		});

		for( int i = 0; i < allAlarmStates.size(); i++ )
			combo.addItem( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(i)).getCategoryName() );

		generateColumn.setCellEditor( new javax.swing.DefaultCellEditor(combo) );
		notifyColumn.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );
	}
		
}


/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	return true;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		PointAlarmOptionsEditorPanel aPointAlarmOptionsEditorPanel;
		aPointAlarmOptionsEditorPanel = new PointAlarmOptionsEditorPanel();
		frame.add("Center", aPointAlarmOptionsEditorPanel);
		frame.setSize(aPointAlarmOptionsEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}


/**
 * Comment
 */
public void newContactButton_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	QuickContactPanel contactPanel = new QuickContactPanel();	
	int userResponse =
		javax.swing.JOptionPane.showInternalOptionDialog(
			CtiUtilities.getDesktopPane(this),
			contactPanel,
			"Create New Contact",
			javax.swing.JOptionPane.OK_CANCEL_OPTION,
			javax.swing.JOptionPane.PLAIN_MESSAGE,
			null,
			null,
			null);

	if( userResponse == javax.swing.JOptionPane.OK_OPTION )
	{
		Contact contactDB = (Contact)contactPanel.getValue(null);

		fireInputDataPanelEvent( 
			new PropertyPanelEvent(
						this,
						PropertyPanelEvent.EVENT_DB_INSERT,
						contactDB) );

		refillContactComboBox();

		//select the newly created contact in out JComboBox, seems reasonable
		for (int j = 0; j < getJComboBoxContact().getItemCount(); j++)
		{
			if( contactDB.getContact().getContactID().intValue()
				 == ((LiteContact)getJComboBoxContact().getItemAt(j)).getContactID() )
			{
				getJComboBoxContact().setSelectedIndex(j);
				break;
			}
		}
	}

}

/**
 * Looks the first email notificatoin type in the list passed in.  Returns a NONE_ID if
 * no email type is found.
 * @param contact
 * @return int
 */
private int findEmailContact( LiteContact contact )
{
	if( contact != null )
	{
		//find the first email address in the list ContactNotifications...then use it
		for( int j = 0; j < contact.getLiteContactNotifications().size(); j++  )
		{	
			LiteContactNotification ltCntNotif = 
					(LiteContactNotification)contact.getLiteContactNotifications().get(j);
						
			if( ltCntNotif.getNotificationCategoryID() == YukonListEntryTypes.YUK_DEF_ID_EMAIL )
			{
				return ltCntNotif.getContactNotifID();
			}
		}
	}

	//no e-mail notif found
	return CtiUtilities.NONE_ID;
}

/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 2:54:35 PM)
 */
private void refillContactComboBox()
{
	getJComboBoxContact().removeAllItems();
	getJComboBoxContact().addItem( NONE_LITE_CONTACT );
	
	
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		List contacts = cache.getAllContacts();
		for( int i = 0; i < contacts.size(); i++ )
		{
			LiteContact contact = (LiteContact)contacts.get(i);
			
			//be sure we have an Email notif for this contact
			if( findEmailContact(contact) != CtiUtilities.NONE_ID )
				getJComboBoxContact().addItem( contact );
		}
	}


	
}


/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	
	
	
	//Consider defaultObject an instance of com.cannontech.database.data.point.PointBase
	com.cannontech.database.data.point.PointBase point = (com.cannontech.database.data.point.PointBase) val;
   int ptType = com.cannontech.database.data.point.PointTypes.getType( point.getPoint().getPointType() );
	
	Character alarmInhibit = point.getPoint().getAlarmInhibit();

	if( alarmInhibit != null )
		CtiUtilities.setCheckBoxState( getJCheckBoxDisableAllAlarms(), alarmInhibit );
		
   //be sure we have a 32 character string
	String alarmStates =
      ( point.getPointAlarming().getAlarmStates().length() != point.getPointAlarming().ALARM_STATE_COUNT
        ? point.getPointAlarming().DEFAULT_ALARM_STATES
        : point.getPointAlarming().getAlarmStates() );
        
	String excludeNotifyStates = point.getPointAlarming().getExcludeNotifyStates();

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )	
	{
		java.util.List allAlarmStates = cache.getAllAlarmCategories();
		java.util.List allStateGroups = cache.getAllStateGroups();
		String generate = new String();

		if( allAlarmStates.size() <= 0 )
			throw new ArrayIndexOutOfBoundsException("No AlarmStates exist, unable to create alarms, occured in " + this.getClass() );
	   
      
		if( ptType == com.cannontech.database.data.point.PointTypes.STATUS_POINT )
		{
			String[] stateNames = null;

			// get all the states the status point may have
			for( int i = 0; i < allStateGroups.size(); i++ )
			{			
            com.cannontech.database.data.lite.LiteStateGroup stateGroup = 
                  (com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i);

				if( point.getPoint().getStateGroupID().intValue() == stateGroup.getStateGroupID() )
				{
					stateNames = new String[stateGroup.getStatesList().size()];

					for( int j = 0; j < stateGroup.getStatesList().size(); j++ )
						stateNames[j] = stateGroup.getStatesList().get(j).toString();
						
					break; // we have all the states, get out
				}
			}
		
			// insert all the predefined states into the JTable
			int i = 0;
			for( i = 0; i < IAlarmDefs.STATUS_ALARM_STATES.length; i++ )
			{
				if( ((int)(alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (int)(alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				boolean notify = ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y' ? true : false );
				
				getTableModel().addRowValue( IAlarmDefs.STATUS_ALARM_STATES[i], generate, notify );
			}
			
			for( int j = 0; j < stateNames.length; j++, i++ )
			{
				if( i >= alarmStates.length() )
					throw new ArrayIndexOutOfBoundsException("Trying to get alarmStates["+i+"] while alarmStates.length()==" + alarmStates.length() + ", to many states for Status point " + point.getPoint().getPointName() + " defined.");
						
				if( ((int)(alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (int)(alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				boolean notify = ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y' ? true : false );
				
				getTableModel().addRowValue( stateNames[j], generate, notify );
			}		
			
		}
		else
		{  
			// All other point types are processed here
			// insert all the predefined states into the JTable
			for( int i = 0; i < IAlarmDefs.OTHER_ALARM_STATES.length; i++ )
			{
				if( ((int)(alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (int)(alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				boolean notify = ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y' ? true : false );
				
				getTableModel().addRowValue( IAlarmDefs.OTHER_ALARM_STATES[i], generate, notify );
			}		
		}


		// assign the correct contact to the JComboBox component
		java.util.List contacts = cache.getAllContacts();
		for( int i = 0; i < contacts.size(); i++ )
		{
			LiteContact ltCnt = (LiteContact)contacts.get(i);
			for( int j = 0; j < ltCnt.getLiteContactNotifications().size(); j++ )
			{
				LiteContactNotification ltCntNotif= 
						(LiteContactNotification)ltCnt.getLiteContactNotifications().get(j);

				if( ltCntNotif.getContactNotifID() 
					  == point.getPointAlarming().getRecipientID().intValue() )
				{
					getJComboBoxContact().setSelectedItem( ltCnt );
					break;
				}
			}
		}
	
		// assign the correct notificationGroup to the getJComboBoxGroup() component
		java.util.List notifGroups = cache.getAllContactNotificationGroups();
		for( int i = 0; i < notifGroups.size(); i++ )
		{
			com.cannontech.database.data.lite.LiteNotificationGroup grp = (com.cannontech.database.data.lite.LiteNotificationGroup)notifGroups.get(i);

			if( grp.getNotificationGroupID() == point.getPointAlarming().getNotificationGroupID().intValue() )
			{
				getJComboBoxGroup().setSelectedItem( grp );
				break;
			}
		}
		
	}

		
	Character excludeNotify = new Character(point.getPointAlarming().getNotifyOnAcknowledge().charAt(0));
	if( alarmInhibit != null )
		CtiUtilities.setCheckBoxState( getJCheckBoxNotifyWhenAck(), excludeNotify );

	getTableModel().fireTableDataChanged();
	
	return;
}
}