package com.cannontech.dbeditor.wizard.device.lmscenario;

import com.cannontech.database.cache.functions.DBPersistentFuncs;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.device.lm.LMProgramDirect;
//import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import java.util.Vector;
import java.util.HashMap;
import javax.swing.JComboBox;

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
	/*This hashmap will simplify referencing program information each time; we
	 won't have to hit the cache or the database to get gears, etc.  This will 
	 be quicker since once the hashmap is populated, the program id and program
	 name can be used as keys to instantly provide the correct direct program*/
	private HashMap allTheKingsPrograms;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getJButtonAdd()) 
				connEtoC2(e);
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getJButtonRemove()) 
				connEtoC3(e);
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
	private javax.swing.JList ivjAvailableList = null;
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JScrollPane ivjJScrollPaneAvailable = null;
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
	D0CB838494G88G88G1DED1AB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD4D45719361B24242509C9C8EBD313A509CDC9C333F64B3634EBD35B356718145D18F3CCCB3B6E166D5A543331AD695A56535A4DE58684ACA8AAC9F065E7D09221A5A372A7A1C4987EB1288490D1C961B17300A7B37306779E02C8F43F6F5EF75FBC0699FEBC2B67FC3CF73F7B775D6FFE777B3BEFA4598511A9C916ECC212D2087E6FE492A1C3AD046C2D8D13B92E3FAA5ECACC7F8A817613DF65
	A743F38C6A922E45DB33C87501B4A84FC2F9D60B373E816F87C9741E9D2760A544BCF1D0771B076FEAD9F89EF2034F13CA4B55DA86BC77822CG5C9A0044AA1C7F2C162363B7C2F92F7E0E10E4A1E4C3B536F962E80E89DFD94D5A2D02E7B44058C9ECA3E939FAFD861431CD91BCEBCE3236DD70CC27545DD9DB23379B38AF0A6CF8AE5F1209F319466730D66F3BD9FF5A48931491D6F2774973702C6B9A9FBD6514BDF62760754ADECDF40E59DD4322CB52E445BEA54D880A4B6E922FCACE516E7628CE51ABA812
	DCDB5BF926C7FF6FD264D1C57074081AA6F9C755AE41AB3AE15C669ADCCF5C51B4BA972C874A1BA82E3988F998866F11G31B5FCCDAFE45669B4C70CC71104477707DCD376BBECADB56F785714F8476E14ED6F60787753BD42FE53D0DE8B40E8793146E9A92D65343CDC00EDDDD01E8508B070FFEFC3FCB414B7GEC2965B43EF70C5358FF7D6BE467304332AF840DBBEA99ED396F7AE9DC9D5516F2084AD178CD1405FE281B85D88990833099E08740E1AA3FEDB77E8CCF473056A4FBBC32576173B53ADD4A9959
	A53A613D3696EAF4DC3768149597A1CCC6DFB8DA9A049E8E99F5F36593012D6D8261B2955D7DB45134131614A0EB0BF951E63169E3D4567957662EC33E45523D6D05F76C0774B07C860ADFEB42333DBACD71D85E88F5256FF1BE776F6534F4A6BFC632CEB4D9D20350326A3BB516156FB19A225E735342F8BC1204BC5E8C78C40034D371D69BC0834050A94E637BEED4AC46638EB8930AD7F077CC8E7B84C5EB1C145CAED151F93DCADB0857410075FBE2597D98DF22AA4CFB34652B0DC17932A56A18E5769463C7
	44A9BFDF585CBF89B9F74C29793231575843C1FAA6B61EE2FBE8B5709245471B70ECEFDF21E70B16212E33BE5E4A5A3FD5C8E521BED0C64AA9FEE3FD200C445A380CA4C0DDF3C33C156DEF3295F5E59414B7GEC815881106D08371681B4BB789EEF39F1FC0967284B343F2C6F03B7DF0727B82DA9C207D755842FD36C8325A78C3BC51568ED9E3909348D4F6977B8BD5FCE1823CD740A0A20C93297C697BCA288134A5AC45F5C0FED94115676CA9E91A67078889FB76666C1640D20EA67FDAEC1937DADD87D3A1B
	A893D705BE989186403DF6A03F36D11D6B0277958E4E2B9349080B07F22241171364GBC5F8761965D35354D1293499014EBDCC61F21E7B339916C93C0FF93BF1B3F6C67B2581C79E5D27A22A3288CA224B56B32579CA8034F51315B81BF8690564C6DEBD7D13F29BF4E39547301F48643C71B70EC8EE94E9C294BB8035690789453BB63775F26607A22E0DD9BG36B6F31EAB4083AA0B0D02AAF269BB27006008AE7F8E3036F2925A96712A68551AC51F68F5410B2A6F7D5524F43FDC0E48064C4CD2BC6A29A690
	9C5540FFBCE9BF434398E6A967751F24750226A9527024A6F2E3EE545FCFE9D16728C0D5DF6B233BF12D3BE00D59AD602F3470359E369CB449D727AC0FCF7AFA716468F27ADBEA0FEB20FDD78B5FFF51067E16E614E5DA76577F2E403CF70B810E33FC5D1CE5DB4E9C21E4E261B9CA8B390FE255650761EBA8BE92685E8810E82C7B99CA13397DF266DC9ACDBF24F31869797F1C034944DEEBE819FE4D9ADC268FDB034BF421F5E119AD318613D9D6D7E645B54AD3DE5399283222BEF44ADE2FC88F225105B0FF79
	173D388E49ABE997A457282875C8B3A2A18D2DA84B2FD21D1B836F252DDCCFCE9D65FA32E866C932FEB0B728BF356A8B6FDA6EE855E301D63F1EE4B4D5A723BC08EE51830CEA12A73D9A4357D1FD2E63D5AB4555E4FF9E7B966B8E39CA4FDA29C71ED4C5BB7D5B25082AAA3A0A2F88D7853BB0255969BE584FE0DD8B3E16585EFE7B14EDE1573E54E79EFE700CF9604E10A33FDB52D7D2C6981D1DE79F21F41EEE1C54B43047AED729C099EEE70FAED19911954FFCC29D34FA6914DEB1A698F4840C9CC829E9E8A4
	758451E9BDB83146CCEB3768112F0A3717DCB647F2A9AEE73AF8F3AA4ACBAC48D19C4056D62E0BE313311E6BE25D60B449EEB793F528EBEFC3B97EE3D136C9B6F7353298F72FD1DF7266427567C356B3BA3E101205E7D21C18944115C289FADF3C467D21148381F5FD02FB526FE745247C891EEE139DE178AF6E8EFEC63E3C9BED4738B17ED7F66FA668AF31B15919DEAB6018BEC5720059C54328476A191366D8FDA52C8D637ACD705C8A407ABED41C8F4F45826158F69CEF3C1047753B20FFB660EA5A5918DABB
	638FEBE7BF425BA5F744DBF3816ABAD83B598EF33BDF9B6DB6GFE0B295DAE3DDD73E9EC77FA910FF90F9B3837A96ED720609FCE66F14519ACF3BE4125D68655D588E12759782BCF8746DB6969B86EBA40471D66F27717D4338F60D0D559A9D1F7D75F3B9F271A7598437DA4F58FBCC7A4C505584BA5CE737D7FD9AA520A7E300E567770293F3CC7667B77ADB40341DD2C26A9FAD2FA65A6F0495BC1041A8CB55FA709D304736123D6F38EE25D3E924B1FC270A1C1DFBFF95F4F07A1086F99CD1F2B4538B2D8EE45
	C909340FCECA76C9CDC247DB50405E79A6B5A6B0ACD7B1B509FC41399B5E67B909C3C9A86753D0DE899085406223886AB3603C6B81379300497039D9338C17F546DBFB812681428118FE7BC3615B70DC160DB078BEEE2D21E767564C816377E3879BF47A621EFE0C545D5C93B41F9236AF5B32261371B92E534FEF573D55A953G0C8F970EF0BE641C811F920069243B2B4DBAA96C8C73B7571E61BCF866C8B9BC172F53B99FFEDF7A8EBCEF5958909E679EBE4A695FG346DB2683FFFA6987D15E7399E2AA92E73
	4FFFCB56C34F7F141BF5407A046210BA207DAC5B03013301BA6097FBD0EE27819FFE166B0021347DA69D508D27CE700E0276B6FC42F4AB5353FA79C0FA8A57530447D3FF4EC09EC442186BGB683EC8748B8476778DC7A22F91C16698572B83313BCA7D2FA0E2D29611CBFF6680577B100D9930E65ACBECD4F644AF3ACE711EE609F2278E8931E6DF18145E3F9A35415F6F17F7E2142401C4C4A2E3931CBF4C019750B1A57AFBF203CA9209F1319AB57F338FEC24918362BD3E033EAEBCF53E34C744766A4337E58
	564564AE7B03F8EB7187BC277A09D9C3CFFD4075548F2A281E0272CA00288FF88EE2C5A65A2C75D05E823083A02B9B6498208BC06B66FB39ADBD18BE1F3B17211439BF2EE57C7970482D71F5FCDE3FC03EC6F487636B3E1EC341794ACEA761F9717555E9FA1F68EEAE8305737236CB2375783CFE0134EE8D201551F06FC71C06DD5DFC0F566FC5DCE58F6CEF8F5F0F16BD1926735BA5CBDE4DB83B3F5E03BE152CC823121763FEC7F1CED92141954315E7040E7B5E49889E7755E7840F7BDAB2964EE51C49889597
	323C4383543676427AFCG8C772A8FF1F7C0F91501DB21D0FF864A1B8D5C23AA62F6C2B92B17636A6B91D784653ADE3E178FA4211F5B85E59FC098409A23AE125621AD4E55FD378DC65D1BC941F28F58B79007605371E1A16A4D50E54CF1AA689C69A17A71DC43D27281284773687359577751BD82BFC02507953D9C3D6E675AEBA4C81CFDD670080447DDAF94266BEDDAB4396904F13EC6DB72021F2F9E2782518C3AD006FCE7D8F0DF100EED3DDCB7BDF49DF5D36779F82B8BE076BC67C978AC1E2188A8C71D1F
	4F4BF5005B2463376ACF2667CFD763F8BB4FB3BD1FE460FBA8BE394F0FE77AFFCB954F7BE4C11D56474F58A7B3C31F0F7BB2031F0F4FE66239D465C945A9861E01C7B2F30CBA3FF66C50C48F32D90F1BE8FFCDD0C645F9E768E95A1F55056A7F754CD0670C6DE14491739E16932E2F3ED79D6C7EA13E0F71F5FB5FFC7E33717A0F1A472BB0460B78A858F864829BA77B427C71987D5F5BCB6F880D723334FC4D186FCB13489F1FA20E088AC44BAC5DC306AE60BE7E9355A14570DEF9017B112FCF205EF38B5764C9
	D010BBE93BCFCD18ED689A404581A468B4B1197E4A945A4F91F02201FDBDBE51C9B677E35F4FAA3CEF8314FB81B400880018FEBEE72C0AFD2123641974D08A99FD1B281E4A3D88367AA2EF5BD7CF5B8A5381ED3DD4FF75C23BA1236DF8AF4662BC13E4BEDC2CBE6D9D0C517C999C0E7F3CA3530FE73E2841CFDBB77AF026136A38AAC8EE3F3F31A9BF183F918EB4C5DE6467F43F056B2ED8404581A4DCDCDC776D00B6E91766364B05F2D9G0E1D5D3D33F8C61BAF855EF365D0FC6F2540FB2E486B7C1E4B85F59B
	AFF13B70B8750DCAAF33768C779D0AEB871C6BB2EFF7280D5B19E983575C0E3855D00EB1F0C903085B88659D170D7B12EAE21C030B26771FD6ADA7073DDCE0F37C61364E41563C057AC9E98371561C814E8747C94E2FA2837754C73C1DE660B2EE70F6ABG37E9G3F6FF892CD0FAE0FBBFCB6F8FA45A91B919CB5CB9E510B17E9451565E525360A6A3292969BC5CE19E3231ABD761031D17607CC260A75E7E5D22EFE4ECF8C217FEB58AE1C303636CD11DC0D42E8A7559A4CEEFDE5A2CD9FFB68C3EE37FEB201B2
	368A4AEB81B6FE48F34C53EF71FCC574930F114E950DC13F19C868AD31A46874A47F559F332D0DEACBE1F3FECB61F3B6C07D74DF6937910AB96EC41E3DE0A1440E33527ABC55DC0F36GF3837E7AF3F36A115E8E2B397EE3F36AD3CC7D595A3E7FA6DF5B2E6F2FA56EFFA88B79BD48FAFDCDDB756746678F13381613A9EC4F1FFD433F671575CB59F356EFFB0E3F5FAE3214FE0C17E985BCA64A9D647B37B60F6A8EA83B816E9864B2DAC1FD1455D0DEEF6042A9EEB314F7993817A8AEEB88E4FE0863C6691E35C3
	59E5607A4BE97C8D65B0837755D4FE8ED69B384C8144ED07F23240F5C976AC66CBF30CF2BEADE32ED27FE6617AF24FBB0E35BC1D726DE54F311C7BB736175F1C45F7E1D2133BC5954CA0C6163CAEFEB6344DFF056A6ECAD8D383G3351E5ED5CE639813782A092E0BDC00240ED69657660F6D725BA1E8C47DBB306F95B1C4102B973D3DBC9D36112B86513954D543F0A5A03BA685BEE747F6FEAFAFF8A4EA213615C7F45A9D68DD359B8593C88EFD274A742B474C1F8CDBFF36170DE253F33316B8F7B47BEFAE321
	31D39699BBE11869B31EABA164794BE6FD1B75D00E057B5101E3EF9FE6B236F3B8B097F5A2175ADFA77047497D6CDF3E7697F87689D766E70981897E0ECFD434EB8CCBEF9FF4397C5A44F23F575163G854F7D18B3B0BE9820F84D99989F7CEC024787AB20EE2B13476543F9A8E76DAEB86BG33G91GB1G1BDC46779C2F1711C572A63D322F93028C37A9ECE66B7B2EF2EB5FA3FDA7156B6818EFFE09445E151894AADF3E55255B9F11BD077E65B04533757D067AFE15D0578520810483C482440AFCFDB7DFAF
	E56B436DF1E81A609C233786FCF1F8C47CE8F424595AB28B785A62E02C84009D22DFAF250DC09CBE924C8FAC4A473DA89B8974836D945FB0926887FEAD0F7B01DDD097B3424F64E98B9A202BA0D812D3F033A35DA8A8DD32DBF2DE534F32C875BB5A3F1CD15E6FF18B72E6CCD62499592B4D6F49755237E95FFA00FE236F9FAD980B8C4B8A8D6AD8AC12ED653AEA9534DB8B309D000C313533FEB714507A6F33545E64C07B22B1BE0FDC0E7AE68468E3ED6E2476278B6A87G18EEF8AEF5B13D13EA528F6150AFF2
	0C6986AC470CB1BE9FB6E8AC9A889E7354D11B369E5A479974BDB6046B19F3A3CE387E3A47C60CB17736E1A104FD2F2829AA2DAAAB63763D2F0D674DF60EF1DF691E36C0DFE97FB1332F333EAD50D742B5796BFF5D9668ABD96778CAC56D013E926F4F56725BC27FDA7E6B2FC58B2C257CE475C970D670217BB976237E3E31D505217B1617011FD3030C00B68534BD1C9F627F47469B4569F57D709F99DCBF64BE75B8E97EE1E25251907A21CCE2F238C2726B711C305D3A8F3765CD9C0D690A3B5FA204476CFFF3
	98AB026AF3508827E18D67062F08CE2DD798B57AD467D27B4F7396CC4D6BF56F65A21F719B8FC9F0CBB3742A32879AFAC775988BD3A43ACD98B63D3FBCF17B632DE465F64E4156BF1E97BA8F2564854FC3CD67854F535E48DB787B1C3B72C2FF1F7369BC7377B9EC5FBFF38C77A69664E3B340CE000CAB71D65B9563FB337CA3045A07A0362145B50AF9B07C02D363737FCFFE68352694AC7E5D675E824C018C4B1AA6FBF0FC0E4FAE407D52E49FC5B25C21826411A20D0EE92C290EAF203102DB9C21E8661B97D1
	DC9D2C2B7F8A77579F1E216D203C52407DF6064784EB8C5CDD055C575FE860862ED33DBD9EEFAD9B673E7C1F696FA71A0DF2A5AD8F9965F5F937A2EBB85E52657945DB1AE3297EBE53E73316CAF81AD245F6D37E495E57525D5B5164689C6CE96ABE57595968689EF4748C1EEDE9E9EEE9E67DFFE0A9DF20FF7B396E0ECB674E764E9961EC8B8C4BFD02BAAB7789D6GEFE247791D6D3776202F36246F83FAA54DADB6D2CB4E6C4F9DD67ECDC762B80FAF07FCB8CF319BE29300FEB74FF7BC0D2ADA674519F29B89
	9D4316D5165ACED4F11B60307B7BFDB175C8E03FD6A03678088BF424BCFA245486E661C4E5C9B01B10D89F8C4766F061A7A93A4D88AF774F775175255049E468F21B59EF75413A6FF0AFAC03CC174CD420CF328A5A46G4483A482A4FB409784A883E8866887708104812C86D88B3081E0B340B600DD9E2EFB220B105121628ABD49C39510A2186F4F9EB996DA87FD615862BA6869E3747B99509D3D57FCFADE7E2B47681D5C4808AA9A3AAE76D816016BD03BE4D5C24B46787DEEC1E81F2F176A22ACAF70454BFD
	2A1819457D596729EE42EFCB5A219F73BF8B2EF37FB34C4B7C4FC8F846F875EF9F292F78F061E23E22B9CE0D033E89DE3F2F38436B0F27982D6A75603E622728CEA4B25890192FAB4C03FCB012521D120A7B443E4190734D7ED70D4C6E6C86E42EAF9B2D0B45CFBEB9585DED57227D826FF2D93F3B976DE706F0197F762216461D11F2E0BC5AC47151F2E0BCBA626371E8AC54657A38CDD1288F3C824A072CBCD7BBC5F12BG67CC62F2F7CFC5E879FE30E2893F2D2888EEE357D44C3731CFD6843731FFD7E13631
	7A370495A8C323F0AE7B0DF65FB0F0978D5CB3F4CC44DD10DC5A18015FC069C2FC3B0893F27CF3142EA90A6B57F14F1BF097F55C668ABC2330E4886FF85F9FD550D8CAF23BD9795FE89B498BC7566047CFA90D12CFB0517DF38307F4B3FD7A99CC5568E7607E999BD9C41FD614153036AF4D60DD6BF26CE5678547459EC3DE5A0C3B109810036DBE7E1D483D57394DAA1A60B66BE774F7BD83D056G428116E7BF3EC16FBB587B4FCF7A577BC05ED2564BF44877CBD0178539CF26271F513EBA1F533E86E8E4F758
	6B8FE6523DF1CBDE31097D4A0770F390C96D7396E81BB84175C303A9D668B03D3BFD37BFEE3873364E4178A83C15C10C38099E95064F1D4E746379D15169BAF37D56F46665C57B855719910B76B3432E892EB3AF526FAB3314C01DB9CC7139CA204EFC5946F5E6B15405ABDC869E4DDFE87EC4A5543D7757294F28219BC8383C9760A7073A3CBDC54FD6329AEF4DD179DC0305FE999BB33D3F322C5F365C1A3C6DA93C7D77023179469D204A6D77F72F4F675305DADCBB4B55BFEF5CAF37C31F8100D90055G6BD4
	2E27FE134F6F413628BC37F334A0B037738AB167E64E97AC1C5B79C4C1E0EE4711E42E7FC6C1285C8E63676F97141BE040FE7F5E1F5B8703C57F0224288AB9B0DF97BA6E79518F23871B33AD4452C1F60F514359C1D2267074CE7B008AB0F7C9EB5489F283F9982C2411E4074BE55348EEF6554FEB8F06C16D47F869662B883CD450GFFB682C57B8C0ABA2809CB38DAD1EB7D4F472D96128C64B0135FC11251EE83D58A234E8AA69FE9A3969CC5E711150E1D2EE3F1830F9AD87C77B0D09381B3E64E09DF3E77E8
	55CBF17F5E27DA496E8EF288D318BD22C233BE228B3FD652FFD659CA52FD78499BBA0FC00ECA9FA92AF68DBCC412BD520DBFD7D0B5516560F98E309B2F514FED0FE37CF63CDDD047B474E95E6DA449DE7C81A8B9402A5D02F774B8CBAE3DCB5714649822746296922077A02597C7319B239C7C47FDEF961E5E9AEBA516D63247AD8B2ED6418921C687C9574694D19D135D2EE9DFFD839DA8ED0A5FC4DAE138434672F13176DE703C8C2247279F8E4F5C16598A8C1CD648F15A92EF116CC68BD753182040D4225221
	618FDAE52548287BD83E7656BF373DB1E0A1C97AA211757B26FDA099A302C7F2DF0317E69AAD52CDB92881350A60D5716BE4204C96C0992FB2A630BF70EC5903CF7E7E921D405FD5A3850C1550757828A8FB0EBBC6C640A29B83GDFE178BCE3F8ECE38FE8F3E07A3E57CE3F786AB048C1B331FA4A4A685F9374EFB97EBDC1B1A7A866C4B9B0B711AC78AF30FE1A2F198A0FA179387D76D658C16D473FF87631FF2DCB5C8E2BEBA5A97443E75804ACFF2B33131EE1D139AB7A413CBFC5A4FC8A77480722B20D633E
	B1EDC87F9EDD7A91AC81524FCB9FB7CA693AB263BD768534D148B1CCCDC8439E7B64D50FDD98566C783FC2305DF18BD49A19746A6BCC321014CB646008A43ADDE74165D7F8CBB8B07BA936DBE4DF24E12C5AC95E1803EB1C743A5C62D9AEBB1DA4EBCE757B62B59DFFF08E1ED1200B9BA54387D2375F212A5228D7F459F86A1FAE2E44E8D0EF04F9C68BDB31E7D255EC4322DB760E5AB459A658A0E057407935B994D1C04C30E8F3CBF858CBF8FE3B6426791EF2671D7BC2FE97D2B4237F1F95B3017714AF5198DB
	837C4A991ECF28AE4058B9D4DE3B47A3385D1A609EF7F8DD1863EE3C26097E3C6A4B85B83E4A5B68157A773AB48FF94554677FGD0CB878846119DE18A99GG08C5GGD0CB818294G94G88G88G1DED1AB046119DE18A99GG08C5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC499GGGG
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
			ivjJButtonAdd.setText("Add ");
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
			ivjJButtonRemove.setText("Remove");
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
private HashMap getProgramHash()
{
	if(allTheKingsPrograms == null)
		allTheKingsPrograms = new HashMap();
	return allTheKingsPrograms;
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
			ivjLocalBorder.setTitle("Assigned Programs");
			ivjProgramsScrollPane = new javax.swing.JScrollPane();
			ivjProgramsScrollPane.setName("ProgramsScrollPane");
			ivjProgramsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjProgramsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjProgramsScrollPane.setBorder(ivjLocalBorder);
			ivjProgramsScrollPane.setPreferredSize(new java.awt.Dimension(404, 157));
			ivjProgramsScrollPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjProgramsScrollPane.setMinimumSize(new java.awt.Dimension(404, 157));
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
			javax.swing.table.TableColumn startDelayColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTDELAY_COLUMN);
			javax.swing.table.TableColumn stopOffsetColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STOPOFFSET_COLUMN);
			javax.swing.table.TableColumn startGearColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);
			nameColumn.setPreferredWidth(100);
			startDelayColumn.setPreferredWidth(60);
			stopOffsetColumn.setPreferredWidth(60);
			startGearColumn.setPreferredWidth(100);
	
			// Create and add the column renderers	
			com.cannontech.common.gui.util.ComboBoxTableRenderer comboBxRender = new com.cannontech.common.gui.util.ComboBoxTableRenderer();
			startGearColumn.setCellRenderer(comboBxRender);
			//create the editor for the gear field
			javax.swing.JComboBox combo = new javax.swing.JComboBox();
			combo.setBackground(getProgramsTable().getBackground());
		
			combo.addActionListener( new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{
					fireInputUpdate();
				}
			});

			startGearColumn.setCellEditor( new javax.swing.DefaultCellEditor(combo) );
	
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
			startDelayColumn.setCellEditor( ed );
			stopOffsetColumn.setCellEditor( ed );
	
			//create our renderer for the Integer fields
			javax.swing.table.DefaultTableCellRenderer rend = new javax.swing.table.DefaultTableCellRenderer();
			rend.setHorizontalAlignment( field.getHorizontalAlignment() );
			startDelayColumn.setCellRenderer(rend);
			stopOffsetColumn.setCellRenderer(rend);
				
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
	
	LMScenario scen = (LMScenario)o;
	
	if(scen == null)
		scen = new LMScenario(); 
		
	Vector assignedPrograms = new Vector();
	
	for(int j = 0; j < getProgramsTable().getRowCount(); j++)
	{
		LMControlScenarioProgram newScenarioProgram = new LMControlScenarioProgram();
				
		//program name needs to be converted to id for storage
		String name = getTableModel().getProgramNameAt(j);
		LMProgramDirect entireProgram = (LMProgramDirect)getProgramHash().get(name);
		newScenarioProgram.setProgramID(entireProgram.getPAObjectID());
		
		newScenarioProgram.setStartDelay(getTableModel().getStartDelayAt(j));
		
		newScenarioProgram.setStopOffset(getTableModel().getStopOffsetAt(j));
		
		newScenarioProgram.setStartGear(((LMProgramDirectGear)getTableModel().getStartGearAt(j)).getGearID());
		
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
		constraintsProgramsScrollPane.gridx = 1; constraintsProgramsScrollPane.gridy = 3;
		constraintsProgramsScrollPane.gridwidth = 2;
		constraintsProgramsScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsProgramsScrollPane.weightx = 1.0;
		constraintsProgramsScrollPane.weighty = 1.0;
		constraintsProgramsScrollPane.ipady = 13;
		constraintsProgramsScrollPane.insets = new java.awt.Insets(2, 8, 10, 8);
		add(getProgramsScrollPane(), constraintsProgramsScrollPane);

		java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
		constraintsJButtonAdd.gridx = 1; constraintsJButtonAdd.gridy = 2;
		constraintsJButtonAdd.ipadx = 26;
		constraintsJButtonAdd.insets = new java.awt.Insets(3, 200, 2, 7);
		add(getJButtonAdd(), constraintsJButtonAdd);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 2; constraintsJButtonRemove.gridy = 2;
		constraintsJButtonRemove.ipadx = 4;
		constraintsJButtonRemove.insets = new java.awt.Insets(3, 8, 2, 35);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJScrollPaneAvailable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAvailable.gridx = 1; constraintsJScrollPaneAvailable.gridy = 1;
		constraintsJScrollPaneAvailable.gridwidth = 2;
		constraintsJScrollPaneAvailable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAvailable.weightx = 1.0;
		constraintsJScrollPaneAvailable.weighty = 1.0;
		constraintsJScrollPaneAvailable.insets = new java.awt.Insets(15, 8, 3, 8);
		add(getJScrollPaneAvailable(), constraintsJScrollPaneAvailable);
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
		//add it to the editable table and make it a scenario program
		addNewScenarioProgramToTable(programID);
		
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
		String name = getTableModel().getProgramNameAt(selectedRows[u]);
		LMProgramDirect fattyProgram = (LMProgramDirect)getProgramHash().get(name);
		LiteYukonPAObject lightProgram = (LiteYukonPAObject)LiteFactory.createLite(fattyProgram);
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
public void makeTheProgramHash()
{
	LMProgramDirect tempProgram = new LMProgramDirect();
	Vector availablePrograms = new java.util.Vector();
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List progs = cache.getAllLoadManagement();
		java.util.Collections.sort( progs, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		try
		{
			for( int i = 0; i < progs.size(); i++ )
			{ 
				Integer progID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getLiteID());
			
				if( com.cannontech.database.data.device.DeviceTypesFuncs.isLMProgramDirect( ((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getType() )
					&& LMProgramDirect.belongsToControlArea(progID))
				{
					tempProgram = (LMProgramDirect)DBPersistentFuncs.retrieveDBPersistent(((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)));
					//make an entry using ID as a key
					getProgramHash().put(progID, tempProgram);
					//make an entry using name as a key
					getProgramHash().put(tempProgram.getPAOName(), tempProgram);

					//while we are at it, throw it into the list of available programs
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

	makeTheProgramHash();
	
	Vector assignedPrograms = scen.getAllThePrograms();
	
	//also need to update the available programs list
	Vector allAvailable = new Vector( getAvailableList().getModel().getSize());
	for( int i = 0; i < getAvailableList().getModel().getSize(); i++ )
		allAvailable.add( getAvailableList().getModel().getElementAt(i) );
	
	for(int j = 0; j < assignedPrograms.size(); j++)
	{
		LMControlScenarioProgram lightProgram = (LMControlScenarioProgram)assignedPrograms.elementAt(j);
		Integer progID = lightProgram.getProgramID();
		LMProgramDirect heavyProgram = (LMProgramDirect)getProgramHash().get(progID);
		
		//do the gears, man
		Vector theGears = heavyProgram.getLmProgramDirectGearVector();
		LMProgramDirectGear startingGear = null;
		JComboBox gearBox = new JComboBox();
		com.cannontech.common.gui.util.ComboBoxTableRenderer comboBxRender = new com.cannontech.common.gui.util.ComboBoxTableRenderer();
		javax.swing.table.TableColumn startGearColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);
		
		//populate the gearbox
		for(int x = 0; x < theGears.size(); x++)
		{
			gearBox.addItem((LMProgramDirectGear)theGears.elementAt(x));
			comboBxRender.addItem((LMProgramDirectGear)theGears.elementAt(x));
			if(((LMProgramDirectGear)theGears.elementAt(x)).getGearID().compareTo(lightProgram.getStartGear()) == 0)
				startingGear = (LMProgramDirectGear)theGears.elementAt(x);
		}
		
		//gearBox.setSelectedItem(startingGear);

	  	// Create and add the gear column renderer	
		startGearColumn.setCellRenderer(comboBxRender);
		javax.swing.JComboBox combo = new javax.swing.JComboBox();
		combo.setBackground(getProgramsTable().getBackground());
		
		combo.addActionListener( new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e) 
			{
				fireInputUpdate();
			}
		});

		//create the editor for the gear field
		startGearColumn.setCellEditor( new javax.swing.DefaultCellEditor(combo) );			
		startGearColumn.setCellRenderer(comboBxRender);
		getProgramsTable().setCellEditor( new javax.swing.DefaultCellEditor(gearBox) );
		
		//add the new row
		getTableModel().addRowValue( heavyProgram.getPAOName(), lightProgram.getStartDelay(), 
			lightProgram.getStopOffset(), startingGear);
			
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

public void addNewScenarioProgramToTable(Integer progID)
{
	LMProgramDirect heavyProgram = (LMProgramDirect)getProgramHash().get(progID);
		
	//do the gears, man
	Vector theGears = heavyProgram.getLmProgramDirectGearVector();
	LMProgramDirectGear startingGear = null;
	JComboBox gearBox = new JComboBox();
	com.cannontech.common.gui.util.ComboBoxTableRenderer comboBxRender = new com.cannontech.common.gui.util.ComboBoxTableRenderer();
	javax.swing.table.TableColumn startGearColumn = getProgramsTable().getColumnModel().getColumn(LMControlScenarioProgramTableModel.STARTGEAR_COLUMN);
		
	//populate the gearbox
	startingGear = (LMProgramDirectGear)theGears.elementAt(0);
	for(int x = 0; x < theGears.size(); x++)
	{
		gearBox.addItem((LMProgramDirectGear)theGears.elementAt(x));
		comboBxRender.addItem((LMProgramDirectGear)theGears.elementAt(x));
	}
		
	//gearBox.setSelectedItem(startingGear);

	// Create and add the gear column renderer	
	startGearColumn.setCellRenderer(comboBxRender);
	javax.swing.JComboBox combo = new javax.swing.JComboBox();
	combo.setBackground(getProgramsTable().getBackground());
		
	combo.addActionListener( new java.awt.event.ActionListener()
	{
		public void actionPerformed(java.awt.event.ActionEvent e) 
		{
			fireInputUpdate();
		}
	});

	//create the editor for the gear field
	startGearColumn.setCellEditor( new javax.swing.DefaultCellEditor(combo) );			
	startGearColumn.setCellRenderer(comboBxRender);
	getProgramsTable().setCellEditor( new javax.swing.DefaultCellEditor(gearBox) );
		
	//add the new row
	getTableModel().addRowValue( heavyProgram.getPAOName(), new Integer(0), 
		new Integer(0), startingGear);

}
}
