package com.cannontech.dbeditor.wizard.device.lmprogram;
/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public class DirectModifyGearPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, javax.swing.event.CaretListener, com.cannontech.common.gui.util.DataInputPanelListener {
	private String gearType = null;
	private java.util.Hashtable paoHashTable = null;
	private javax.swing.JLabel ivjJLabelGearName = null;
	private javax.swing.JTextField ivjJTextFieldGearName = null;
	private javax.swing.JComboBox ivjJComboBoxGearType = null;
	private javax.swing.JLabel ivjJLabelGearType = null;
	private boolean thermoWasLast = false;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private GenericGearPanel ivjGenericGearPanel1 = null;
	private LatchingGearPanel ivjLatchingGearPanel1 = null;
	private MasterCycleGearPanel ivjMasterGearPanel1 = null;
	private SmartCycleGearPanel ivjSmartGearPanel1 = null;
	private TimeRefreshGearPanel ivjTimeGearPanel1 = null;
	private RotationGearPanel ivjRotationGearPanel1= null;
	private ThermostatSetbackGearPanel ivjThermoSetbackGearPanel1 = null;
	private NoControlGearPanel ivjNoControlGearPanel1 = null;
	
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DirectModifyGearPanel() {
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
	if (e.getSource() == getJComboBoxGearType()) 
		connEtoC2(e);
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
	// user code end
	if (e.getSource() == getJTextFieldGearName()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}

/**
 * connEtoC10:  (JComboBoxHowToStop.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
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
 * connEtoC11:  (JComboBoxCycleCountSndType.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
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
 * connEtoC12:  (JComboBoxCycleCountSndType1.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
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
 * connEtoC13:  (JTextFieldChangeTriggerOffset.caret.caretUpdate(javax.swing.event.CaretEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (JComboBoxGearType.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.jComboBoxGearType_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxGearType_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JTextFieldGearName.caret.caretUpdate(javax.swing.event.CaretEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JComboBoxShedTime.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JComboBoxNumGroups.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * connEtoC6:  (JComboBoxPeriodCount.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
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
 * connEtoC7:  (JComboBoxSendRate.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
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
 * connEtoC8:  (JComboBoxGroupSelection.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88GF8CF82ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8DF4D4C516F6D71C43A803B0A286858990A0C8D010289941B1C2F4D0B2C7E68EAAEA4ECCE64D19E19D1CE55D6CAE3B4B19AD3ABB5079017C1004702761C78DC352A490D7A66818BFFE02ED4801089D8C12176E17F4C3F73F7675CB5261C7775E2AD72FDFF73A83B812F3EE5E2B2F7EEE5D3AF56B5E2A5724785D582CF506E2C256E591756F23F504349FA16429EB36C20E856E2EB7935D5FAE20C2B2
	274B844F11101779FC3D39003CF38A8BCD04347359FA7396F85FCC761CCA7E90DE22749383F927B675D58D5F4F7FDD607DEC20698F776741F393D0ACE0A9C085E3307F4F77172978CA40B6296FC86B8C042C20E53A3615697076B12CDC82BC13012E0E45B24E7DBB547CF2C8E3194370340EE5E59D704C265C5DD1F7D8AD57B9F69C49FC215430917B52354FA9C52D3BD2FD16138729A00CE4F257E7707C386DD26F5F6C1247E2973CDE492B08F6274551ADBADC0AA4DB86DC178559E1F1087DAE3BE8F1FBFC3254
	AB8B1E3A3AE617AC5A154FA5072BE77038A848ED02D7F4935BCFF18CE33719E97B509724B3A8163D9D65B6915E9301D2FF4A47712B0D9F2BFC0E3BB40E24CF4B0EBA0E0AFB987F56FB0263C8DB65B42C3D0776C967856B0D02F48A906365557D1C177AF11C172F36E059G2447G45EB786EB24413A13D82E875B84E63A79FF09EEDD72611952F5718722370E83E1771D6FDEF1047B5EFD41B36D25DD95405736F063CB1C0F1C0CB00D6821947571B37D31D3DF661AF70EC68D61AA40FC772B678FC0DEE078CF296
	5D70DED787B9AAF6DA34CB32039026176337D5C66007D37978D01DC4E2E37B12F0BDCAB8FDBFC95DBC461095E1EC09AF1599EA55B6CE0C8F0E6D6AF8145BB5BA37CEF847FAD81F61A3A91E2C4359DC65D39C534BA16F70FDDC4E36CD1C972BE9B4A928B9E25890011718C555060977B19E926F8B7242E4EC73210C5381DF8BD49CD3EF2E84328239E3380CFFF1615075E45C826BC0768A6EB3FD5DBEC1D69A7BDCEE07A82B323EEF7FF0320EC42C5E741B2E476492FFC8BFC7E9736BA24AA5AD7EDD434898A60F58
	1820DCD85F3F095AF7F24CD05D58244D610BF42DAC0FE1F3E85470D7A91E2143595C8A9447F4A66459A600DD236547533535F0C2380ECC2178328961BAD2D146F5A48372CE5CDFEFE6737B9F5F20FDCC00749220F420B5C0E58F541BEB01CEBC406738286B408D2E23B65D7C323ACB3BCA61A98694D9E8717A9541EB974F8A32CB68F60BFE2216D95E05F26D8E2977AADD5FF66863386895E5C1F1C9DEE8DD700820CCFED6661F3AF0E3334BA24DEDF7F9C4684063A33C5DB73BB64353AD7815AFFC8EC19103A5D8
	7E5BDD28937D42D9E8919A407570G4AEB8E3539CEF89F71G17559EBFE2E9104E546462910A607999A83768C6ABEEC7B684F9106B686D5B310EF9E23DB9E9A2DF17FF32F17DEB5EB801D43E7457087A07F4F892532536C9617AB71A369BGBCE6925FCBCB365B426AE31F1B547EEFE61DC4DFF316A1FDE429CF635F59BE921373D885F5B58946359CA8F39217F53CD04EF430D1700BDC6BCE4920B022A3A8F9D6D670611EA2760BDE25D97409DE873C7855B9F77A32037A58A3E93A6225B85A27A6D0983F06AB3E
	C206C39BFA6D6679D7E93E20A8322B3BCF914F080A627276866B0F203C78C3CC1FDF9D6BBDFDB8563C876B4DFB01EA9F64E35D784DE61DDED9A569D21F2F9DD70C2A1F7FCE776193D03E6BC1BE7FAFEDC15FEA04167EADCD8773FB37044F5D8FC158DFD0BFEE3BA5FDF05DE2FDCCAC63BEC816064D24D8AC7032C49B2F254C262B1F758373447AB8FECB7BE03AC064683A3CC10E2C4B9B65483A3CD99EDED7374B11F415656D16F1FC5200D7277B9F48E87F6C1257AB528528D5A14CA77ED3BB0E4365F5A9DF3A9C
	3D22F246F5D9A424ECB26AF08A35312BE13ECC1339ED9C5843ED635E4B7712648B45917D271869078C1D13195E79A687EDA36329420FEBCAF40B9E90D4135467D598EE2176DB457DC60A4D74CC433A95A7E1DF6B169A25GBA5F6D03BE31D220633130C71BA877C832C7F4D4DC947A850BB020D868FCD89AE876D1FC5FD7DED5753772080EFC65457066AFB40435EB0D5E7011FDE777559046EF43EEBF63379D667C18CBF4BB3045D65893AB6DE020948B7D4F76BA4AEB40629F0099D6DBED425C6BF25B83E80B57
	57475B017E8F2A766A39FE146D9810F99C5042495CDEBD6447FCEE2FD4235CA4395DCCAD1067D81C73646D453AF9DCBE1945FC1B347CD5D173D9BF77A985281F62B7FDA23891E890BF9387D5DF60C125A8AC6F2C606E8B7A98B314DCF83AF5361461937AA26B4B64BE341F17347627752DA768AB30B619FE4F9530CD1F6C724056031B039A1B66D56AE3D3A70C8D6358F8F8AE84E2F3F96D822EB5B6854118AFE68A75874BF85C3A9C4A2F824A1B425A30CEE172E065A6ED6565B2A03D96E847D4D64EB615158BCC
	45F22FEBB164818DFB1BE27F82066CF993770DBFAF504741D667E044750734D0EDFF5954701861FF86683A861CCC637A3133DF3F1FB5787D125DC5DDB6F58ED27B75EB13E13F6A4F01A7A8239F62870798607378CABF720ABE1D8A2BF3B1FB7FA7E4280FFA14C65E5CDDE89A205A5FAEB501DBF982D421C9B3DDE7DD62G61F238F6CC9FC7AF4C5FE5480DA20762E9EC7C355302F2D888B1AA63E9C29D46C6114E846C14C9CBEF1F4B52273850F99494012EBAFD6C5FDF09F2C1BDD12671387A5BFC54579510DE8D
	E40E03F927BE3EE1AB77714B81338131B555E7542F29B1F14CE7C80A63FA685AFA9077041B36F7DC6E0D1B6B6179771AB8A6772934AD64B375615164636F8C06C8E783A373738C29F1CC5E19F1C139BBF3EBCCD70118BC1E5364D1BF3D5E9C184E6561336965B1F7BA13470A69DC9ED55BF00D5C24B9D5E511322F0869EC1881554E878E6A57D7F2FAF9547535EABA9B9719913E3EFEBB08BA3183706A99FCFD1D9EA8542D2F532051023797AC1C66C3840C4C1629E9D3A00B5BB2427D6E9DD7D0AE4D50E697D0G
	E88C50DC2D0F3B82570D730F860609732FD4222F34ECC6B096490077B5B3060FCD72E6423C4D8CADD38B6993E1980BFF5F26EB23F3A60BFFF3B51C94A0FED507B39D99C14BE3FAA26411F86EBB1A370647770E78507EEE8BCB33FA25C36A3D93160E8B2B477426573D1D5B891D1A5555E9FE09EA8F920CE6352F0571BCAE152F629E10896902D975668A20E6A0A750882078D9FC7EDE8BC4320F2173974DB886E39EB64E546FA91F570654330425174C0AA41F77AAF6C4160F6687B1794C34F179244F6272F9749A
	E2E58F01EC9E623278E8F023EE3D34C9AE2F222D150CC15C67A559556B72F26C1FA9E617E46A7CB2EC6F1568FE797EAB117D721AAB117D720F2E8C9FE3FEFAA51A5F4E6241C9F41FB88C63EB83E218298A31CEC87BB46C8E8BE263A01D20E1AFD3AC9952E99AF6144AF1B5244D33B976F98735BB102E55B06FB944EC10764D66EBE7CDBE5A1191100E85CA86CA83DA2B652FAF407C6284662F54A6B07F26BD01675F199FA90644F67099BBBB289FE96A7B0859C3ED485A88D8D08F27C56CE303CE67B816CD0271DD
	F8795C889852CEDA7E7952FCBA27FE3F68278B8BF7F8B56F8FCA890990B721A32F7685F33EB5DBADF3D4111A8EEA3C4FB36A57D0G64B6AAA1B85658042163D64FA92EC657575B2FC656579FDF45F562177AE43B982E13E32E16E9F9C1CB5122089E9C066ADB537A0AA0C36C97DEBF1656E7F9516A4F3A9ACD6F190C0C5B74B2BA2449A8412617D1A248E409CEAEE9D1E57444B55C9F82DAFA81CD8FEA7DA5D6621CAB1264EEF7795076105589F84FF589C385354C76AA3495EEE1D06A8323B2F28E2E1303D57AF8
	209630CEA05F9C46872BB751027E2E60F675FA518B6E00CD6BE12C1BC85767B2A81BB1077BA8EFD9D02EBED96C91E50834D8D82D16DFFFG4B3743FB57433CFC27AD24ED9BAF2B50F5BE8A5EE301182D1DB700E36C11858F087A0CCF349347A3D856514147308C4AE6G19G5F8B547C886F47709556050AAECF1F07F2457A5976957613G65123432F5DB302D13D621DBF473A8164B42FE917D2821D1AE4F3F5C9ADA979DCE1EB7E790F5E6C8C42F4939E6BD6E79A74F58E508CF51592EE1FE6111823DDF187188
	738B4DF3395F780B6AC879BBA01F45FC17BA75F3DCBD1765F74253B7175B4BA5F47F9E8969D88DABB6A1168869D48D3BE06236B6DD43965BF85D6AF99C4B6F60F51BB56C1CE627ED7378394A0BDF697D4E027B8B8C2857117C4E78F96ADD503C707B4D1A2E10BD98BD793A3A63324B51A874DA293633BD58C6779DECFB65BC9E33E4D4214C4B9F2DB79F86EAFB147395F81F47D989B3C6936BEDF5916FAB535BF7991C0FB2FEC6BD9A64ABAF235A74AE1DD3C274174DEC1E17DA4A6129859D208FBE490BEEFB7B29
	368B8D5616632D1F9FEDEDE7FC448E6865F3B8677D28FE7992357FB475597C46EE5586242C8F76DDE7433E3D62C03956FF338BD69EDEB2D49C2ABED4D9DE73575AFD51F93F4B36ABB26F5623475A034CC399A673BBBB384C0B67571B2D7391CB6A506BAB367BBBA37337D87E1BA17938E6F0EF8A666FB8274FC7B9FD91D23FB5A49F657501DA1F49F469FBFCEE57AE1DC85C0FD5C43D0B9EB71F49B2C9FD2E18371BD88F3B8CCC2E547C29F291725F09AE571AEA90EB3596177EDBCF990968B3D22B4164A697E063
	E833D64F67F75CFF2E66D8C5A237093DC7F7C6EEABE8C534391856E9D6E5501548757F39E2547F98C84F85DA1248ED20BBBF32DD7ED7EAA75221DC26D6764705117707CF282D587B982CAD20184768390129B9021C75F315254A944981F582C0A5AABE8A5E6729F3457AD8E5637C24E37B94BB5C417BADF81C777BCD47757AB56A686347C37BED037474F52CDF663B3FCE671E6D450F185E0367D961E244131D13B0AD273AAF029351AE74926E87AC283A59BBE296AF5AACB40EFC1C450B6BB4DC2038D30733B872
	4DAA7E3D0482F9AB9E673147BBB9D4871FG5D8272810D840A87CAF90247A27FE92AA657094B5AA51F959CB737AEB4E06DB7662200D6C0DB19DAFB1FE66D0FA6A7G5BE449A74A4AA0C3698929AA2B3BBB3E5FFD7AB39B78BACFFCF2BCC97A514AF5D14F4A125433B275691B3B1B62ECAC89F9A8AB87645D8694873490E899504AA4BE366C8D15CCD648FE032288F6A7BDB1640242C33CA00C4E8B9B5BC001FEECEB213D021F016F82F498487A33217EA02BE72C51576B02F28A50C82009C0F323560B6F5457CB01
	F2AB00D629654913C36BB17D2AABC1BDAAFEB27C7B00E60A6FFDB27C7B0054E27EFDC0AD6445GC55FD34C269B5DD3223751EEAA0F5CC6481EBA347E6ACFA335496C7BD4AA9226CB3F4B633A34E3765D247915156B76C45125352A8C2D8B027EC0FB6ACF8CEADC50F123E3E565CFF5FC5FF131B164613504BA77DFDB026F3D3A7755D53762EEB4D46F32AD37328F754EAFA7FA9CE949099CC75666C4BE77284B997EBE32B1A77AFDE4CB4E507B4863391B746DB17326663566223F02F711AE58C4AF536B07B3D050
	5B4B6CCF9A757FC700BE458225G2DGDA35005B1EDF676EA65476C430BBC79D3D98836357AD8A6F6F313C68F2DA18F77DEFE21E4D437D3EDBD29449036DF3FCE99E4E35A279A84830E5F9A8DF59556BD4D8D195FFB98FF555AD76D018458723294FE3DED8EFAEDB48E306AF72F9ECD129E1B3A9D68F69B68D138BF93C5129E10B8BF93C615330EDB574CC89524BB46CDCA76FA3C343E6D0EC8D244DCFF1AC77BC6F23D8434E1E67FDD4E8585334EEG52319A3675BC6FA3D143C42D3DA59A565F455BCBFB0A1FBB
	5517E090DF79F31EBEC953D6AD7D6B629FFAEDE1DF216B770FBFF89F7AF3AB2667F5C5682379206FD1C0F30116G2581E582191F061896289A6884D0971082B492E8A2D082D0B2507227793A7843A6F039237A9BEA00C89707AC041C3396C4DF9FFFAC387E7A4824FAAC02DEE3F84F307FAA20E737BDBDFED1DB07FFAEA8503096FF1B64F7614AE7F6FFFA814EF10F64D518CF3B00363992461679B477F3C764F35FF7EFB27745FF1E0F7594E010597FD2B49D2A2FBC25107B456D495CAFFE2DF0B83F5898C6811D
	BF0EFB5FED504EE675DDBD432889657BB8DDEF23303FE44E776D1D1C6FA59A5FCFF506725DDA92643B76BC67FB0D4637677C75784E9A066F3D4F541B7704707DF2A76A20D312DD1701F7414D4227C6C1EE135CAE7B20464B5B1477CF012A1F6163E9BC4F47635330519A4F314FF01EFF52F5BD1EB76878CE05FA694FB01E69F716703E93DE0E9BB94FB1D938C751EF90DDF66D223E06706F502F14684FFD48A2F6165D8ECF67A23EC6DA0B226B7A97C55757752F0BA26F850E2221FB012BA872DEA095697782755B
	36A214C3AF2CD11BD6EED043BAB46CDB5AA6E2DF3A9C0AD343C7D03E90BFA1E2079C3F1372B5C0B11B0A5D2D43BAD4ECDC912ED798B228842FBB21086A364B6DE669C9340C4B8B4BD71347D44A234BA768781E29E148B753EF5BB62269DECBBE6E5FA1E78747C089ABF6BB409F10FAF7D71663794141FD3C7C8379B767C3A69516C7AE9F62C3B23E16A24BAA0F8365C19EDBBBEF0E477BBBEF0E474F4E5FB80F776F8A7215FD7E20DA0F5F006BCEBEACA7CE1DEEB9F72A353D41FA61CC5369D3D6EBE343698B2DC7
	4F9EBD4D6BF71C3FB9BE275F901FB796E7F09F7DA9942D3A6E0F840E116F7BDB0DF0CB404FFD661770EFECEEDB444FFD62CB69372C10DE8634F29177A512CA060B354962E871618E7ABDF8716270F30977A8DE3EB87CDC6261D2FEAED189F9E396F37F6745926E7FE4AE66BC6D461F7928FE48FE5DFBE2692D0FDBFEF9CB7BE077973F576CF4D98A13EF257A342670333F3CD2332AA36E94FE0E5ADC9AFECE0A723490FE8E5ADF9AFE8E4A7359588ACB75F39D876DA6258C7F4D831B238A4ACBAA14EDB2F22C0AE2
	9900F90CDC170577F10E5538061AE7066F88EC8C625CBC9376DBAE6B575F0111CDF80BE4816617C95150635406512F3E12F0213958C08CADE43D1356EFA1D98358C1G3F30DF8483BD607F06FCCB470B7BC08E34142F3554C2ADF6FA7F21BA639B530C86E202E6588E56C2CC388DC1EBB2EB5588BB98ED534016AC79C7FC27ED1BD4F9F2F41DC2F2F0691563F5D09DCF2087D8873DEF8CB11B4FCE797077293FB96BB71275ADE4ABFACBE7E065E270A7BA70B2D67D6A7F9849766197GF8DF852C7969A34B2F8C3A
	C5ABA96EB90DDF947A9551514043CA3FC2CA6857CC8770532D83A7843FD3416D7A0815183C787B84D244325D023777G0BB10F50712DEB2827A34199C339EF267CE2AB962D154D8B725F5BF5B2A349C88C47C80EDB929C4784BBD82496122DB8E5516F145C0E006FD35610F9009FD69B21396D5A70F130963CBE53183E9418B4EA634A0D47C04481199C20A5519134E8A59CCDCEC1062EC439C5415FDBC872DEAD6F9F5ED15E7F6571AD1D8632CE9DA4CECEEE40271075BD0247659E0417E63AC951E94BD700DBD9
	707A7163AF602CBC0CB31E25F5E03967796AFBE77D77B95AC1302AC25EE522042A87FAC549F32021274765953586C02E507CCE2DF9ACE389ABD39498DBF272257F6D86BDE8A6C6CFF5B57DDFC37F9F447FB5942921C84DC1906EEAB26CDFF8FE000F19AA0F368AF07AAD47E08615575EFAFE6A6B9F2FDE8523BBC63268F7D7B0898541D22DFD1EEED17ED1423DBBF347255F05F364C3D589E03B1B835ACA405DE53D5A29112259C3348A510DC3558361C2ED12B0D5CC0B16527FDBEFFA48D8EBBB2D3BD363F4CF10
	5375BACE33B45424A218E3D04F7C76FD273FFBDBF3470628F7EF07DFD06D758B615F444589B46E87BC6E856EF3E7F6610FD1235D971C7188EEB7C4A0179A3C8E3CBBE89CD444603953AC815B7773B2EAA64B1BABA05F97F5F57E9FD0CB8788B02E48AE9196GGFCBBGGD0CB818294G94G88G88GF8CF82ADB02E48AE9196GGFCBBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCB96GGGG
**end of data**/
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 5:37:00 PM)
 * @return java.lang.String
 */
public java.lang.String getGearType() {
	return gearType;
}


/**
 * Return the GenericGearPanel1 property value.
 * @return com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GenericGearPanel getGenericGearPanel1() {
	if (ivjGenericGearPanel1 == null) {
		try {
			ivjGenericGearPanel1 = new com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel();
			ivjGenericGearPanel1.setName("GenericGearPanel1");
			ivjGenericGearPanel1.setLocation(0, 0);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGenericGearPanel1;
}


/**
 * Return the JComboBoxGearType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGearType() {
	if (ivjJComboBoxGearType == null) {
		try {
			ivjJComboBoxGearType = new javax.swing.JComboBox();
			ivjJComboBoxGearType.setName("JComboBoxGearType");
			ivjJComboBoxGearType.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJComboBoxGearType.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}

			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_TIME_REFRESH ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_ROTATION ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_MASTER_CYCLE ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_SMART_CYCLE ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_TRUE_CYCLE ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.CONTROL_LATCHING ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.THERMOSTAT_SETBACK ) );
			ivjJComboBoxGearType.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.NO_CONTROL) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGearType;
}


/**
 * Return the JLabelGearName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGearName() {
	if (ivjJLabelGearName == null) {
		try {
			ivjJLabelGearName = new javax.swing.JLabel();
			ivjJLabelGearName.setName("JLabelGearName");
			ivjJLabelGearName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelGearName.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJLabelGearName.setText("Gear Name:");
			ivjJLabelGearName.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGearName;
}


/**
 * Return the JLabelGearType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGearType() {
	if (ivjJLabelGearType == null) {
		try {
			ivjJLabelGearType = new javax.swing.JLabel();
			ivjJLabelGearType.setName("JLabelGearType");
			ivjJLabelGearType.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelGearType.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJLabelGearType.setText("Gear Type:");
			ivjJLabelGearType.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGearType;
}


/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjJScrollPane1.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJScrollPane1.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			getJScrollPane1().setViewportView(getIvjTimeGearPanel1());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}

/**
 * Return the JTextFieldGearName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldGearName() {
	if (ivjJTextFieldGearName == null) {
		try {
			ivjJTextFieldGearName = new javax.swing.JTextField();
			ivjJTextFieldGearName.setName("JTextFieldGearName");
			ivjJTextFieldGearName.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJTextFieldGearName.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldGearName;
}


/**
 * Insert the method's description here.
 * Creation date: (6/18/2001 5:46:52 PM)
 * @return java.util.Hashtable
 */
private java.util.Hashtable getPAOHashTable() 
{
	//store the references to our cached PAOs and points in this class
	if( paoHashTable == null )
		paoHashTable = com.cannontech.database.cache.functions.PAOFuncs.getAllLitePAOWithPoints();

	return paoHashTable;
}


/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMProgramDirectGear gear = null;
	
	if( o == null )
   {
      setGearType( getJComboBoxGearType().getSelectedItem().toString() );
		gear = LMProgramDirectGear.createGearFactory( getGearType() );	
   }
	else
	{		
      setGearType( getJComboBoxGearType().getSelectedItem().toString() );
		gear = LMProgramDirectGear.createGearFactory( getGearType() );
		gear.setGearID(((LMProgramDirectGear)o).getGearID());
	}
	
	gear.setGearName( getJTextFieldGearName().getText() );
	gear.setControlMethod( getGearType() );
	
	if( gear instanceof com.cannontech.database.data.device.lm.SmartCycleGear )
	{
		return getIvjSmartGearPanel1().getValue(gear);			
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.MasterCycleGear )
	{
		return getIvjMasterGearPanel1().getValue(gear);	
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.TimeRefreshGear )
	{
		return getIvjTimeGearPanel1().getValue(gear);
	}
	//else if( gear.getControlMethod() == LMProgramDirectGearDefines.CONTROL_ROTATION)
	else if( gear instanceof com.cannontech.database.data.device.lm.RotationGear )
	{
		return getIvjRotationGearPanel1().getValue(gear);
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.LatchingGear )
	{
		return getIvjLatchingGearPanel1().getValue(gear);		
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.ThermostatSetbackGear )
	{
		return getIvjThermoSetbackGearPanel1().getValue(gear);	
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.NoControlGear)
	{
		return getNoControlGearPanel().getValue(gear);	
	}
	else
		return gear;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBoxGearType().addActionListener(this);
	getJTextFieldGearName().addCaretListener(this);
	
	//Listeners for the variety of gear panels
	getIvjTimeGearPanel1().addDataInputPanelListener(this);
	getIvjLatchingGearPanel1().addDataInputPanelListener(this);
	getIvjMasterGearPanel1().addDataInputPanelListener(this);
	getIvjRotationGearPanel1().addDataInputPanelListener(this);
	getIvjSmartGearPanel1().addDataInputPanelListener(this);
	getIvjThermoSetbackGearPanel1().addDataInputPanelListener(this);
	getNoControlGearPanel().addDataInputPanelListener(this);
	

}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DirectGearPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
		setPreferredSize(new java.awt.Dimension(303, 194));
		setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		setSize(426, 496);
		setMinimumSize(new java.awt.Dimension(10, 10));

		java.awt.GridBagConstraints constraintsJLabelGearName = new java.awt.GridBagConstraints();
		constraintsJLabelGearName.gridx = 1; constraintsJLabelGearName.gridy = 1;
		constraintsJLabelGearName.ipadx = 9;
		constraintsJLabelGearName.insets = new java.awt.Insets(3, 6, 3, 0);
		add(getJLabelGearName(), constraintsJLabelGearName);

		java.awt.GridBagConstraints constraintsJTextFieldGearName = new java.awt.GridBagConstraints();
		constraintsJTextFieldGearName.gridx = 2; constraintsJTextFieldGearName.gridy = 1;
		constraintsJTextFieldGearName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldGearName.weightx = 1.0;
		constraintsJTextFieldGearName.ipadx = 210;
		constraintsJTextFieldGearName.insets = new java.awt.Insets(1, 0, 1, 130);
		add(getJTextFieldGearName(), constraintsJTextFieldGearName);

		java.awt.GridBagConstraints constraintsJLabelGearType = new java.awt.GridBagConstraints();
		constraintsJLabelGearType.gridx = 1; constraintsJLabelGearType.gridy = 2;
		constraintsJLabelGearType.ipadx = 17;
		constraintsJLabelGearType.insets = new java.awt.Insets(5, 6, 6, 0);
		add(getJLabelGearType(), constraintsJLabelGearType);

		java.awt.GridBagConstraints constraintsJComboBoxGearType = new java.awt.GridBagConstraints();
		constraintsJComboBoxGearType.gridx = 2; constraintsJComboBoxGearType.gridy = 2;
		constraintsJComboBoxGearType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxGearType.weightx = 1.0;
		constraintsJComboBoxGearType.ipadx = 88;
		constraintsJComboBoxGearType.insets = new java.awt.Insets(2, 0, 2, 130);
		add(getJComboBoxGearType(), constraintsJComboBoxGearType);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 3;
		constraintsJScrollPane1.gridwidth = 2;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane1.weightx = 1.0;
		constraintsJScrollPane1.weighty = 1.0;
		constraintsJScrollPane1.ipadx = 398;
		constraintsJScrollPane1.ipady = 419;
		constraintsJScrollPane1.insets = new java.awt.Insets(2, 0, 4, 6);
		add(getJScrollPane1(), constraintsJScrollPane1);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	getJComboBoxGearType().setSelectedItem( LMProgramDirectGear.CONTROL_TIME_REFRESH );
	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldGearName().getText() == null
		 || getJTextFieldGearName().getText().length() <= 0 )
	{
		setErrorString("A name for this gear must be specified");
		return false;
	}
	
	return true;
}


/**
 * Comment
 */
public void jComboBoxGearType_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( getJComboBoxGearType().getSelectedItem() != null )
	{
		setGearType( getJComboBoxGearType().getSelectedItem().toString() );
		getGenericGearPanel1().jComboBoxWhenChange_ActionPerformed(actionEvent);
		
		fireInputUpdate();
	}
	
	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DirectModifyGearPanel aDirectModifyGearPanel;
		aDirectModifyGearPanel = new DirectModifyGearPanel();
		frame.setContentPane(aDirectModifyGearPanel);
		frame.setSize(aDirectModifyGearPanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (2/8/2002 5:37:00 PM)
 * @param newGearType java.lang.String
 */
private void setGearType(java.lang.String newGearType)
{
	gearType = StringUtils.removeChars( ' ', newGearType );

	if( getGearType() == null )
		return;

	if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_LATCHING) )
	{
		//Latching
		getJScrollPane1().setViewportView(getIvjLatchingGearPanel1());
	}
	else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_MASTER_CYCLE) )
	{
		//MasterCycle
		getJScrollPane1().setViewportView(getIvjMasterGearPanel1());
	}
	else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_ROTATION) )
	{
		//Rotation
		getJScrollPane1().setViewportView(getIvjRotationGearPanel1());
	}
	else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_SMART_CYCLE)
				 || getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_TRUE_CYCLE) )
	{
		//SmartCycle
		getJScrollPane1().setViewportView(getIvjSmartGearPanel1());
	
	}
	else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.CONTROL_TIME_REFRESH) )
	{
		//TimeRefresh
		getJScrollPane1().setViewportView(getIvjTimeGearPanel1());
	}

	else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.THERMOSTAT_SETBACK) )
	{
		//Thermostat Setback
		getJScrollPane1().setViewportView(getIvjThermoSetbackGearPanel1());
	}
	else if( getGearType().equalsIgnoreCase(LMProgramDirectGear.NO_CONTROL) )
	{
		//No Control
		getJScrollPane1().setViewportView(getNoControlGearPanel());
	}
	else
		throw new Error("Unknown LMProgramDirectGear " +
			"type found, the value = " + getGearType() );

	return;
	
}


/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 5:22:51 PM)
 * @param newIvjGenericGearPanel1 com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel
 */
public void setIvjGenericGearPanel1(GenericGearPanel newIvjGenericGearPanel1) {
	ivjGenericGearPanel1 = newIvjGenericGearPanel1;
}


/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	LMProgramDirectGear gear = null;
	
	if( o == null )
	{
		return;
	}
	else
		gear = (LMProgramDirectGear)o;

	getJComboBoxGearType().setSelectedItem( StringUtils.addCharBetweenWords( ' ', gear.getControlMethod() ) );
	getJTextFieldGearName().setText( gear.getGearName() );

	if( gear instanceof com.cannontech.database.data.device.lm.SmartCycleGear )
	{
		getIvjSmartGearPanel1().setValue(gear);			
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.MasterCycleGear )
	{
		getIvjMasterGearPanel1().setValue(gear);	
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.TimeRefreshGear )
	{
		getIvjTimeGearPanel1().setValue(gear);
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.RotationGear )
	{
		getIvjRotationGearPanel1().setValue(gear);
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.LatchingGear )
	{
		getIvjLatchingGearPanel1().setValue(gear);		
	}

	else if( gear instanceof com.cannontech.database.data.device.lm.ThermostatSetbackGear )
	{
		getIvjThermoSetbackGearPanel1().setValue(gear);	
	}
	else if( gear instanceof com.cannontech.database.data.device.lm.NoControlGear )
	{
		getNoControlGearPanel().setValue(gear);	
	}
	else
		return;
	
}


/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}


/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
	/**
	 * Returns the ivjLatchingGearPanel1.
	 * @return LatchingGearPanel
	 */
	public LatchingGearPanel getIvjLatchingGearPanel1() {
		if(ivjLatchingGearPanel1 == null)
			ivjLatchingGearPanel1 = new LatchingGearPanel();
		return ivjLatchingGearPanel1;
	}

	/**
	 * Returns the ivjMasterGearPanel1.
	 * @return MasterCycleGearPanel
	 */
	public MasterCycleGearPanel getIvjMasterGearPanel1() {
		if(ivjMasterGearPanel1 == null)
			ivjMasterGearPanel1 = new MasterCycleGearPanel();
		return ivjMasterGearPanel1;
	}

	/**
	 * Returns the ivjRotationGearPanel1.
	 * @return RotationGearPanel
	 */
	public RotationGearPanel getIvjRotationGearPanel1() {
		if(ivjRotationGearPanel1 == null)
			ivjRotationGearPanel1 = new RotationGearPanel();
		return ivjRotationGearPanel1;
	}

	/**
	 * Returns the ivjSmartGearPanel1.
	 * @return SmartCycleGearPanel
	 */
	public SmartCycleGearPanel getIvjSmartGearPanel1() {
		if(ivjSmartGearPanel1 == null)
			ivjSmartGearPanel1 = new SmartCycleGearPanel();
		return ivjSmartGearPanel1;
	}


	/**
	 * Returns the ivjThermoSetbackGearPanel1.
	 * @return ThermostatSetbackGearPanel
	 */
	public ThermostatSetbackGearPanel getIvjThermoSetbackGearPanel1() {
		if(ivjThermoSetbackGearPanel1 == null)
			ivjThermoSetbackGearPanel1 = new ThermostatSetbackGearPanel();
		return ivjThermoSetbackGearPanel1;
	}

	public NoControlGearPanel getNoControlGearPanel() {
		if(ivjNoControlGearPanel1 == null)
			ivjNoControlGearPanel1 = new NoControlGearPanel();
		return ivjNoControlGearPanel1;
	}
	/**
	 * Returns the ivjTimeGearPanel1.
	 * @return TimeRefreshGearPanel
	 */
	public TimeRefreshGearPanel getIvjTimeGearPanel1() {
		if(ivjTimeGearPanel1 == null)
			ivjTimeGearPanel1 = new TimeRefreshGearPanel();
		return ivjTimeGearPanel1;
	}

	/**
	 * Sets the ivjLatchingGearPanel1.
	 * @param ivjLatchingGearPanel1 The ivjLatchingGearPanel1 to set
	 */
	public void setIvjLatchingGearPanel1(LatchingGearPanel ivjLatchingGearPanel1) {
		this.ivjLatchingGearPanel1 = ivjLatchingGearPanel1;
	}

	/**
	 * Sets the ivjMasterGearPanel1.
	 * @param ivjMasterGearPanel1 The ivjMasterGearPanel1 to set
	 */
	public void setIvjMasterGearPanel1(MasterCycleGearPanel ivjMasterGearPanel1) {
		this.ivjMasterGearPanel1 = ivjMasterGearPanel1;
	}

	public void setIvjNoControlGearPanel1(NoControlGearPanel ivjNoControlGearPanel1) {
		this.ivjNoControlGearPanel1 = ivjNoControlGearPanel1;
	}
	/**
	 * Sets the ivjRotationGearPanel1.
	 * @param ivjRotationGearPanel1 The ivjRotationGearPanel1 to set
	 */
	public void setIvjRotationGearPanel1(RotationGearPanel ivjRotationGearPanel1) {
		this.ivjRotationGearPanel1 = ivjRotationGearPanel1;
	}

	/**
	 * Sets the ivjSmartGearPanel1.
	 * @param ivjSmartGearPanel1 The ivjSmartGearPanel1 to set
	 */
	public void setIvjSmartGearPanel1(SmartCycleGearPanel ivjSmartGearPanel1) {
		this.ivjSmartGearPanel1 = ivjSmartGearPanel1;
	}

	/**
	 * Sets the ivjThermoSetbackGearPanel1.
	 * @param ivjThermoSetbackGearPanel1 The ivjThermoSetbackGearPanel1 to set
	 */
	public void setIvjThermoSetbackGearPanel1(ThermostatSetbackGearPanel ivjThermoSetbackGearPanel1) {
		this.ivjThermoSetbackGearPanel1 = ivjThermoSetbackGearPanel1;
	}

	/**
	 * Sets the ivjTimeGearPanel1.
	 * @param ivjTimeGearPanel1 The ivjTimeGearPanel1 to set
	 */
	public void setIvjTimeGearPanel1(TimeRefreshGearPanel ivjTimeGearPanel1) {
		this.ivjTimeGearPanel1 = ivjTimeGearPanel1;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.util.DataInputPanelListener#inputUpdate(com.cannontech.common.editor.PropertyPanelEvent)
	 */
	public void inputUpdate(PropertyPanelEvent event) {
		fireInputUpdate();		
	}

}