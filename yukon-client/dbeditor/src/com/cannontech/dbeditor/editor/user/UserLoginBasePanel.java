package com.cannontech.dbeditor.editor.user;
/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.user.UserUtils;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.cache.DefaultDatabaseCache;
import java.util.List;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.db.web.EnergyCompanyOperatorLoginList;

//import com.cannontech.database.db.user.YukonUser;

public class UserLoginBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelNormalPassword = null;
	private javax.swing.JLabel ivjJLabelRetypePassword = null;
	private javax.swing.JLabel ivjJLabelUserName = null;
	private javax.swing.JPasswordField ivjJPasswordFieldPassword = null;
	private javax.swing.JPasswordField ivjJPasswordFieldRetypePassword = null;
	private javax.swing.JPanel ivjJPanelLoginPanel = null;
	private javax.swing.JTextField ivjJTextFieldUserID = null;
	private javax.swing.JCheckBox ivjJCheckBoxEnableLogin = null;
	private long oldEnergyCompanyID = -1;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JCheckBox ivjJCheckBoxEnableEC = null;
	private javax.swing.JComboBox ivjJComboBoxEnergyCompany = null;
	private javax.swing.JPanel ivjJPanelEC = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == UserLoginBasePanel.this.getJCheckBoxEnableLogin()) 
				connEtoC4(e);
			if (e.getSource() == UserLoginBasePanel.this.getJComboBoxEnergyCompany()) 
				connEtoC5(e);
			if (e.getSource() == UserLoginBasePanel.this.getJCheckBoxEnableEC()) 
				connEtoC6(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == UserLoginBasePanel.this.getJTextFieldUserID()) 
				connEtoC1(e);
			if (e.getSource() == UserLoginBasePanel.this.getJPasswordFieldPassword()) 
				connEtoC2(e);
			if (e.getSource() == UserLoginBasePanel.this.getJPasswordFieldRetypePassword()) 
				connEtoC3(e);
		};
	};

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public UserLoginBasePanel() {
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
	if (e.getSource() == getJCheckBoxEnableLogin()) 
		connEtoC4(e);
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
	if (e.getSource() == getJTextFieldUserID()) 
		connEtoC1(e);
	if (e.getSource() == getJPasswordFieldPassword()) 
		connEtoC2(e);
	if (e.getSource() == getJPasswordFieldRetypePassword()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JTextFieldFirstName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldLastName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldPhone1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JCheckBoxEnableLogin.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.jCheckBoxEnableLogin_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC5:  (JComboBoxEnergyCompany.action.actionPerformed(java.awt.event.ActionEvent) --> UserLoginBasePanel.fireInputUpdate()V)
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
 * connEtoC6:  (JCheckBoxEnableEC.action.actionPerformed(java.awt.event.ActionEvent) --> UserLoginBasePanel.jCheckBoxEnableEC_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxEnableEC_ActionPerformed(arg1);
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
	D0CB838494G88G88G2CD538B2GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DC8DDCD44735372DC9EDE3D35312770CB1AF5410BC1A72CBCCEAD25B50BE531856B436352DE9CCEADB721EE9ED1EC9FDFDA6312DEDAEAC1F8A0A028695E57DC0A192847C8C9283828A080A3AB18B22F2E1AF6C2E7B155D0BAC3268BBB3F34F5C3B3BF779B0B53FBEFF9E6E1D7F4CB9F3E6664C194F3BC2EEC1F4CAE2D22EA0A426884A3F830902B06F12A03CF62A3F9A310E55F60320793785E835501C
	1B8CCF8F44F5F86C06EC61030F53A1BC9642F20F5D70B63C6788DF70AF7890DEA264938FF12F67DFFBE368FCFE5F01792451F045C0A6BCD7820D83AC8EE869EA12FF53C01E024F0170CA651DD0E212A0CCDDC3523C1C1A27412B5630F4E361B9914814C352C88379CA7CD240C81AEC4037673034D5704C205A5DD2F9D0C957FEC734B077C779E9D9A4AF0DFC24498A6F8C65A988B12924A28C424914AAA23365F24F61AE373334CBF4395CAED96A3216DABAA50BCDF6FBCB15C72FCF72D6D6FE88FF1B5DBDB65791
	51A73508AE49D116CBF4DE1116CE65014E10C7BC45EECFA375A4437BF82018DC547BE7D9D50ADED117230599772D0A28373016693BFD2D2A7774BB8792672E2572E9BB90BE8B04A783B1DDFEB9003A642DC3DD1E93C85A9688DB0146F0FCF2A241A7C2F8AA502CF528637BBBD1C773404342380B4E24D591F4CCD847F4CBFFDB55B17A50971356D3DB3956C95A3B8E622CC08120E82099C089C09B290D160A6F403326D32EF5BB1DEED70D47F344E1711EF0DBA4873CD7D6C20C021D113A5CDE0BA0B0BB4CCEAB51
	5187E9695B41B6C808156DA3816DA66E4C8302B556151422D3362867B627E566B19946BC35EC56BCD2EFEB52C93DD543BB61A3F543702D940F53602C2D2228AD50B044352C47FAB62FC4DD1A13A7890B4BEA5252F4F471FFFFFF5A6C75CC07856BD5DDD89D1BAD240E33B7588DA5C0F5C06DC08120499B300EBFA76E992E0E9B416E3DAE51F1363753A3FA65A33DB607C572AAF59DB5B0D4DD6B91631BB2EABED6AF31FB34ED947F4DEA5DFA090FADCC1B3E0155471C8DEA3D303CFF95B16F459B42EDE3A5EF43C6
	5AA712B732B6B4F07CAC45F3B5B8EB5BDF513EC5425BA1EE9C90CB1F1BC470C5CA7AACAEE79B453373D51C4919C2651370DE080B86E26D7B06C47CE166A63B2188289A488C64870A820A5D04ED7C93F177887BD10B26FD996F67BB5741D3724BDE31516513C5D717F4CE745A44CE07649314B493BAC93DF686715DC97BF79764F1CCF2C9DED13639DDA0DDF4CAE0CCBE16665FBAD713B4DE09461AECCE89B2F0FA84147BC0E78EBC9D22CF7E50E391E5C9CD41629F6AA4B6F1C5BC8792C1G3CAF5DC46AEBA77539
	3360FD41A62C2BEDBD842B5A8CE349E62C17622EB570BC8E46ADB9AAAB6BECDDC48D515B0FB61A17CE6A578A6997GD98A6C06C5C09E235D3090A8EA8B7655574CE813F5D9778B194FFD20EB13047236B27BAA5B9AEA13C5B4AFB360FE2009C053016681AD854A5B06E36C24B4D2BFA46F51749DD28622B7511F68CEB0D29E6AB7B68FAF0B657DED1A772AD176DBE27FAB147E1B2511C71EA9A360E76DF70715740103D08FA6A0F99B3621254B48EC1B0C3BE84D273CE00812C5EDD116D634905D25AB12CB2E13BC
	124B82AFBE45162C168C554E3B5D5C86DD94A7FE2F968C5147F159321A61A0C35BEBB0BEC063C5D9765ABAFBE5692CA44BB6D70F4ABF166A628BF229BE252CDF3511320E07B246GCD63E54D12F2B4765A6CF6DF6E7518C8CFD46C7ECFF4FC1F856993383D1CCAA4F334646D98EE25E1B57EA5412C29EB12EEB4EDBBB23B79CBD09E06FF20FCB50F8DC9445FA4AAB6152654F306E2FBACCDB1CDD382E5B4818500E200E683AD6175931B58A128FD97E5211C217458B8EA1D1F22F999C272A279E48D431FB10A3C18
	6D885D11EDBF2DDB5F76333A75EDBF27FBE85B5E582DE75BACEEEBB7A9273B4F2569ABBB3B099F6EF23BDC926D301CC5E0F37157CC249CB617CD7E48E669116433362B92785AC2BAA6D3DF1F8F6FE505680F7B3621BF5EFE75EBC274258DE9FA73B87F7D6F250DABE476BE39D07547CC27229E5287A50764040A2AF5773AE4E5CECA4791857799A8F6E21B8B1E4622A6935461D11B64301055C0E3DDC997388739147EE5A3D811DD3CA27ACBFDFDD0DF2534B1CAEBC9ECBDF9ADB63E7B6EE1E378C222245B66159A
	DD1EDE94D2FC2E380C59CD0EF038C9E6F937C08B7541A418660F011B28C1854B7F41412BBA791F1164FE0F74A9E831876B61566CABCC0FDA2B54F579085BDF6FA21E11CAA89129D91532C70B646DF6FB1D1205AAD1AA76490A86B5B4FA54AA14F39D3E356E3222035B5969869D4AEFEE4EFB15FC178622754ADEDF7B8F4C5E9EAA7BD2CD08506648D28F01327B182EF519FD41F5D48F1E20279FC29E5155FF336B0B0D2FBF30133E9D8FFDFE8E5042C29CDF9F6CA171B83EAA1308DA3743415C9244F56CA0BE67D2
	FA2E460F88BB58DEC7B3BCD9FC02EEFCAC0F97B3746263F97CFF6A464F6171DD4B7562977078853A71CBF93C34C2AFDE9DAB7E551ACD7C3774F12F8453FD525C4725FE1C33DB5704441D939D3D6ADA6081AB993F9C1A3989436F32697B53FBEDE4BEF2194B3F4F16AA10B9BD13497C7FD4114874F8EDCE184A11491632E71491506E99E56E04BAA26DG4F8EA0E6EF9B86C81B56767AE437D372563ADDB25896B595EAA6042F0836E9926EAD7901F78250D4C55E221D2CEED83A2D86CCE7063087E8C211DE3A17F9
	3A5990DE90B15DDD4918AE3958EE48872AAA56CBF7A1855385A0BE8AE81AEE3AAD2918EE8944E73E83EB7FF7D8BA47BBAC5D74F7C83A8E3EAF351BE3FD94BB8E13101712F13DFDA2DB3B1796F73BD3F7CF0450B2C5FE762E50FD083FDEA6F24BG2F5E05FDED13DDBB172D71795CDDB63A8CD46CE536DDBB4EB26CE7766570040E6D139BE18263C71BFB5ECEF4A56BC485D66C669B3B5E9742573D75F45B8E178A35FD540D185C3530D4EDG332D655310F3B60978AA36F776AF69D8DF26DDB84F98BC2A5D2F1B3C
	EAE75A0A88F5B3DE2913B8CD5D349C7AE292137DF6864ADE348B776AA6F2ECFB89E26B17A3564E31C99C0B62584695084D860C5545DD15E41FC7EFBF330BD6CEE9CF2F2D34D736110530A80B54F3329E42E47A82C4A6191FA63F0BFB0429F4FF238A42A6A087905B2F98C777B1C83EE3C95AF7516F6CEB557A1DA500671702CD14E2DF7DE3923145D1CE9614361ED93C0659C60853C7BB1E12D30D7D7121C9C27E7574B43DFDCB6B2AB5E926D256C601D2352DCAB2EFCB1A10F3DB92AB5FF18316EF962491F6E379
	0A5B3565BB381B154F329B4B772861C64A4772DC190CF97AC15ED41E67BC3FDE1E8BF7E3DBFC1F73AD83ECEF9972BDE45672B94A581AB12A8CF5ADCE2900676807E35C333AB585730DA532F93EFF6F50661BD78E6D8FE4A947FCDF7D0479B62572FA8219D34BB15FBFDF88ADAF49F7A14F7711549B4D1759DFF1A6312FCFBACBD27621AB34BEB7BAE1076E7A057A5CF24567D6047A5C5FBAC9FFAD813C3A82FD6E194B2BB5BE778CF8B9515583A3B4DFA3BAC8DD40D82C04139DA4DFBA968B38E63DBBFDB47B3626
	8A5CFF4BF7137A37C2F8AC50E420E9C0F339EE37B9065D072E778F318F1D1FB19A3D16D460FA7F1ED171256FC13E4D4BC743174F796E9D95DF9967B32E988DDF9D677387881FF90F3A2F63503C0F59933E5F4376222FD25F9E35076DC527F17CD60A47E9F0566FB2A9CE42B3A12EE8AF66FFFBD2285E63778667999B92E6FC134378CAC34233C378D8BFACACA2739FBA4EE95CEAE5253AB6E7F3477F9034F34785FB197FCB5F877EF39F0E230ED65207C81F5C3B8F6D586BA1729D909E87948D948F14G348C4838
	9F6D79C5075E5CA3581EA3CDBC543DC4D697278DB7D607174278C26B30FA3FDE9DBE3E5928DF07C1EBFCD6C79D6DD8C7667D58663949B7266F56B03ED0FDA704684BF4D8550FBA44F29DE65D208E3F9CD607845DBABBFCEE23FE1D610820687A329967D1CB392E63D3EECC57C9E1FC213A169D5053354791C9D7E5CD2E2C2FDEEF556BA38DA0337DG7611FDA216C7BE0065E9C93D3172DC8C635B9C920EBB20577EE597098EE46EBD73G7653C7691D07E5904EBC08FD7200136C2B627857623639485E990BDB60
	A46BCE375786ABC3447EC031AE3717EED632BD76C769BAA09FE49E64F2373BA36F2D6EF26B6F2D163B7577D68F3807BED778409DE96F156937A78375EB86E258B40EF5F0EC5FF244E40EBD4E31F19CBB308231499C7BB2DD8BCC0570AC0E3DC1313990DE4831DFDCA3D866FBF643767750B64650F12281429620319C9FCFF1B2F7A5EBE0B2B7E3781A2033035192119F0AB5A81805673D6B9365112603E97774B75170B36BD651F67579A49FF57CE41525447DDEDEAB686CFF1F941D1200FB0BCFA7E5A8E96AE5F7
	EDB96F4B7FDB1C275F17B5F7D05E94300FCEFC8F57EE1B689E589C88AF8492AA31AEBEEE45BE18D799DEF7A500D58319B4F14AF917A7F29F781CC73F8FFC41C37A234F5D6B6D12C26DFC02A70F4729A3E323ACB9C935A8FBCA14DF963DBDD2D8BF09267CACAE927F57BD117A922B73F186ED1DD788682724F62CA32BCE9D85G1BG942309E3720EA7EB656D6172F27BD15EF49DF93381CBGDA92A66F2DA0F9FB393CD74CDABF1AFE087165AB4F32C361F9B0F9CF25E8656D93503F3E5B0676D3F7886D674885E2
	BF8188C781459DC27B2994B16F19BA794C83ECD1884E727EFC2AB66F7D3CACCFF421BC21CA654BD55E0B60D98554D295EA0F5F7A184CCF7DBC7C848D7773FEA3552C6476D16F32A803815B87FB067A781CE4C1783200D893752165003D4931E7A93687305D0908B5D39F7AB6E035BC5D0FA936878613F21E6EA4752B4FC23A73C9D8878F844898A63B5D8E134DC3662B02250A5C3B3BC20A2124499AA4E312C36CF777023E396F93D95B87F11C0E0574B3009682AD2BE263B5AB6FA3FDC4F637D7F402091F75C8DD
	42E24A7B4AB564ED073087E88250D420B96F733531C4760319C7D137CED9DCC99AD957691D3A3078582CF44EDBDF4B5A7451C42DFD9EBC8CE355E1357DBA8E075B8DDBFB5CC9FD16FCB8745ECC8C45A79E8E3DB7334D0E77E662A12E68839C174651F9FD940427F26CD2G472A199C7B8C1D5B66D683EFB5E2CFD32C8A42A60E855AD11E15E39387D0DE00E3FFBA0F72E6F3ECBF17174031DD7DA8EF8947FEEAC6F969B508E57523BCB347CE7723BC874792E9F947C0F8824796EEA6D88C0427F16CC48166B12306
	5F69EC5336D57691984B0F286DF370C82436FA1C7A0766A32CCDD2B87EA445BBB4B8EB2B068B38EFA3C35C3CA33CBCA256C5D5ADE22F8828E78B475CA216BB1AE3BF6FC25EE99CDBC4F9E7C1F89E47CEA7223C859C7B961537378EEC338E3134C44C23238E7700FFFADE3BD733782EF5E924DCFAFBB5F29D2B2BB1F56ADE8DABFB73A05E571C86F10B6A304F16F7842DE5492ED1E565B12F4DF2C46CE9269E007967BF8D26AB7C5B6B51BF9B2F91196D9016014657233E7E9D38E79D7740A421E4CC356E9D539926
	1DE9B175CC4FD9752A3E63791EF576F54CB379A8344DD1029D3E2E5DE3A37D7F5BC9ECCD4262BDC171E46CA5739FB5FECAD0BC513746200D1F9B94CF6AE827424F4A76ECA116ED593331C273E325916F4F0EBD4A4A942BBC279B4C29333EB7BE093573B3341D93A0EE49D1EC7B94A341720E598DA547909B3B055B61B16CAFF75B09BF0D06F0BC50FC1296A82F816A288153BCC853D8A0BC36011DB545B43033A4F2CE245CD531F70468CCE4E843C4B61109F98DB7CFDD2A130665D59716D768DD9712AECBE95311
	5C59523B1F42460376BEA24763153AA52FD732302B9C423286D21F0FD0BFB5935E67G490D843B92289BA21F94C5AED2B570180036A96DEEEDE4FEA61167DF65A4E51168B0E5D172FD309D4FCB629BF14C3B45C96AG86CB1B3357C935E4E9DF23E9E7C33AF9C08C3B1FE249CD705E04FABFBCB01C5EDA5D2B1A0275EEE9D275E6F9EC64BA36B7E13E17A86600301FE3A66A7FE3A0BC156B72BB73F03A24E9F4199F224B62B0DD767723AE4BF83ED7A916F99CEC7DB856E1DEABB95B6DF63BE416261436EF9D4437
	70B4EFEDA6E9E4186C30394E1748F5920169EDA998CAEFED3B52F3B9256D57AA78984823861EFFFB6B2D37D8FFED2473587100C79F47BE5C1ACCED8E4273B8B6AF857B753263589F63AF902C39994655E644FEC8B1F246CA4EE399F67242E85712A8F3A4E9D99EBD234EE3247DF6A5EF632BF47C1BGFACDEEC6BBCACC9CCDDF1C559CECC773A17C823405EAC793E99E8B81DF520CF674D7BAE69BCF001FBD01B6E2C88C36112787D49B318E2913A104CCA17E418CF97CD9510965E5E47DDE7407747B842A2B8552
	FBCE202EE5F41C188C615893FCFFB8C0F4E3EB0E6F788B61F9CE346B1EBCB74110FE2A538E8BD31358435BF66A600D5D277F5E8D723DB9EABEB60F0B22730B39A7D45F4070FBA81E2041599CE747B51C63AC02381613D8DFDF21EB17E9901E833490C8B8E5B798012ACE617EDAD2FF19B04CDE3A496DE9162EC88E4D1699AB5F5A6BB7761D4113E9B81718761DA94244DBDFCB0DF4365FF20A75DD0F72ECC9B22712A7ABDF61F5BA9F0038F820842025C019ADB00FEE4172B55175BFB991G832911E5314BCAEFBD
	E06148C58495A60BB7E62B8909E4AD586976520D8F36967CCB862E678E027C962071C051AD6CDC1871659B435710FB0C48B73D052DA16740F30142477A5A5DDB0673475A3E368CF833CFB3FFCC6FB1407B2B5057FE8C4474F02EA1FCCD4DE22764A0B7385806970B1B54164B63CE3A3DCE5101078FAC7EF4A2DD13065C38E4F1EFFC2472865FC4E471FF6BA55FB4685F18447C9FEC4C8FCB93ACCB396BE5574EC94BCE337AB21D46F3679782FA71D69EDF289B9F00A773F50B29B5B21DCC0182F83A24BEA3BFC42E
	3301C9100F9F0AAAAA5EABB1D61495AB769B5D27DD1764ADAF09F806BC61B4335B986539FD1FB9350066A75035927B377803F0CCD076A76ECD92F83FF9A9937BCD7EB76E9636BF7FDAAA7A5A50FC67AB79151CD157976833F304A75B294F7776CAAC0F0CE4359E52C7D48F0C2FA6C5657B524090FC657B8FB0BE3D32B3D916D4D5D6C508FE772429F1CF78C7128F632B4FD079E61ABF19FE56652A2C937D417A5D36C20D3BE3C479B03E23EBD43E791B3704729D0549DD51569A2FCDF4ECADB116437F3D455CAEEC
	B9AAEFC7DB382F6AEBC31FE3B943FCD5G1E934E307DFF4667C98E77D51FCF667BDDE7182F1A814F398A9F73D5FFCCC1DF3568CC785C8A779E50D789E76D065C332A2FAABA9B6A2B1EAAA76B55309B363CFCFFBBCFFD0A6E2DD62C0F7139EAFD7CD6984A064B77C19B6C992A2D9FDE2B763F1F2FD365CE9F11DC46777A5BAA5FCBC371159F588BFC95481737DE4D7B5086D54632D14830ED54645DB192BE654C739A61E36F77E95ECB2EFD12B31C114D156AEFEA9EECAEF077E064F314A9037A67A831037AE7098F
	8D8E7D1D466303113F53786E2076BB8DB617F9AF1F9870DE689BADC0B250B82049E7F19EF37C5AD6014EE3F466B075169EF20EC33EA014B17F526B114B3A6F7A705F9D9E3ECE9CDE27DB165DCEA29F713A6B74BC5A6D21A0439A2F13BA725AFA2CB2CB2A602768BE16C36A26B01B2BEEDD0E5FEDCE0732ADBE0B7BF15FDDCE6621A33AC3E932498E698875F6EC3F4CCE4FG081CA2936E511DA920FB21907683CDB0611D3D73C61CAB478216E04233767B0C38671677A14A7899CD5B82E1AB509820110D5D68C7E6
	13ADB8BCCB6C0EF016C8E7D24ADDD5FA1717345B448FD12F6CC14416F12CBD00D847B91C6B7753BD671990CEB807B633AFF11790E10D84E00B576D113CF2BFC369CDF345969F23FB8342C7B0DF822A86B283B9004682CD840A831A81B48FE8B1D03259EE4883AA812A82EA813218D10F85C945C36821EC3AD283760A6C5EGEB2BFB5B50D67CA0EB2A994B7F7BB6EC6F05E6EC2B87683DD1E32B5DF0902825955B7B61E4ECEFABE0512DA8634DE4EC6FF92DA823105E2B4CEE339B0A002A5BB02DB19573B3370D56
	867A9A855E9FEA72A37749067C617B6469FCFAEF9B7A1289664A8CBB17CF6FD6F4F77BA45E774F67E7F32C515762765948792C72AD6C30F764A634695D9F59BFACDF68FD92466726FCF1EDA35B031951065FF17F00765F39ED21EB601FD2BC21ADF48DECAE60EBE008EBB90FFBC6227146566E6E9B640B9DB59FABD77D96227F34732165BDC9719967C34B7BBC77E333A12E2C9D4B1BFFD9EF9C0DEAD776645B755BA19E70B9A1F18BA03C94A873020AAB6B7EC13DBC0CCA3A3D977473E8G3CBDA44E836131C093
	43729083FAF944A969E6C448E3AE608BC36216C1B87B225D307DE268B976354D641D0F614C85AAF137ECA663B179AE92265F362B7473093310505543621FA9085C1767948C5F17DFA8509FDFFFDB90BE3EAEA8509FDFDFA9500E2F8C7BE38171D3BD60D34DBC5D920E35F26C4FD4A641BE32D9E4AB47DFBE36DC419BA412A1627FFD0C6855C7B13302AD56E02D8A767AB1620F2048D63797F97FF20C6ED55B9C8E967EBBCDE3F301FB6275E1E8A0BA5ABC22C66FF40E35F23B383F6D467C57B707655377DF0BA85F
	410BA373DF4D9751FFED2063CE4745507E3C0D620E0B217DB94E007D598FF173AFE2FFFEAC6C4E64487C50D3B74837EC54FC2CDCF75131337AD2E8F927D03C79D2E8F9F725E0F94D9097FF894BDB98F4CF5158E1B76C6D983A6E59BD2F99B9D954269CB617D44BFE99C340FBFB375279E18348696840F3089769FCC90670388E4CFBB6994615FAF8CE73FE3160662FFF7ABE05BC0EEFD14BE4523C7F5AF87373FE632666415A74B01DBFC7C1FB4670F64EF062DD03299C2B6F433B8BB3B976738A024D0370C20E75
	DD61E7D59CABF4623D10E691B117135FDD605801C154C5665844C154E58C4726717BADD19CFBB3003A44F26CF6FE07A61EE31386D0174CCE44FEB3003A98B976B94A3B9742F59CDB98C0DDDAB8766CF954454231FB28BCBF0447F16C4EC154E5B2473A83284BA20EBD98C0DD04AE448C6C1E8E04CBB8B6090EE35590B6F571EFB212D57BAC563CBFEA3879367A6C2710473DA9EA19E2B56F65A9B7BF6F261B1A87EB53AA3AE66A68E2FE1FE1B59473F7293F6514A35C331C4CC3E43224A77B1D821EDFBEC6FDF694
	240D6E427D4A7F591C02E9855CA31B54A4F0D99FDE8EBE4B7C6EF1014BBBC87B689C10B53F8B4F98FB299FC837588D39964C638F9541676AEFD13FD08D714D965C7F3C7D4AA7B9978DD8C24F7C4FBA714CFF1C85F53B07E293A19CE341730A04E635BCCF52BEB8876266F31EDFD3DF12AEC1F9A4440CB4DDB5041BA5547F7EA16FFD60F9F6C47D25D07D1F623FFDB70E671BC67B7EC48847C8D8371382416DE3B82916E7B5AB8F241D4FE5AC213EA23D9B4A530DB2268D84374F0FE85E55905F5C0D650B9B9C2EFC
	E9C3152FBB34FC6CCEBEADDFB76A768BD6BE8847F05DB61C5737658ADA0EB910EEBECF7BC8C138AD6FB8A5703A97F7107911327E20D3F406677B33D49C27688A7FDB27553E7013D11EA7ECBD2B72D6768D35E7DCD1811C8778990BC9657BE1454879F6FC2472DD3E32D5DFD78B286A6E81E54B94E599EF23D96535BB034FE29C2DEADC7CD0E7D9A17AFC39CD657BFA7B48799EBD2F72AD9AC5FE6BAF2AFC1F8D0C2E7CD517D45E9787024BFF29C30D4BEB9D39BE1F95D53E9967C74EF7FF274A7715A14FG03796E
	3029FCDF989CDD792759D55E87824165FF7E329A17B90AF65C63D279EEAF98E95FE13F777A0B6C5FC0870FFBD5C8013EA88BD9740A36119DE3D9E4A12F59663AEC145DC6F6F0E5D4CE2EAA0DBE2F30A6DC331A5BDFF8BE6ED2DDEE121054A824DAE997EF94D27A088F70139F09A39B93AF836FAA4E5BC8175289D776D49A7E6BBEC312108C0CECEB21D1C8A67B83406FE5F28C325BC325506B25C0E42BA2577731F00D961E4956067E1D06D2B49C2447A769248967489D0243ABE485556E53DF5B773B593FB967B3
	88290D42FA62604FCADE3A77A2D94877BC4A8F919E95B2BC64FB82F2FD888A67230F941F5C6F101A055C6EB36447D3FC32E42941DD9D1FAC2C259F066FA627873B9BC41FD5A68DF328D9C8F6111FCC945630E807686A594D36F88E319A2A21FD098E9A0920EF8E5517C8A965D2F21ED8D53825E941F40310F4D4D86EF00B1623E2176C76B68A993255AB792CEE07456F79201A8ACA27738DF2D81C8C62B67262134216125B96DC694B7EFB46E7AD4CBA1A8453A52F301B26A463E6A9CFE129350ADE48CA72B64A64
	A7A05D5E6DBC6EB31B659DBFBE76F6FB1210289412D4FD365FA38B29EC3706DB8FA9F469D150DBFE7155E7267C2AEA71A2E0BAAA24508F53A16BECB55549DEE727643DB56E4ECDAB2292EEA31AF9C831E833664AD0DE2F687211AF71C138B124EC9845D5AC7D6AB3E5F7FE7DAFEDD4C515D5968AD8E3G6B6E9E496D5CDD535D8DABFAAEGDA8644EF6262C91A5210B4EB7CF72CEDFA6E2DCE3024BA4160ACAB23FF4B695F8A72371CA26594A92F0066E197C1A27E8B0D77E319297951C09E319504D33BC5274D51
	8FAFF5540179893E534F3B853952C1AC612FC04BF937A03E64F91E065C10A13BE419C15DD3CDE3D03A6EC31CE313E2DB29C19CEA1AF08E0322871D5DF0B4D903A64B42EA664D48212857C937C1E5E1B93991E4A4D70AAA216171528E3CE733EBBCC68D34B2586B51ECD60675AE022E896FA684DEC77EECA0FFB652E41B68DF63289B0DF0EDD59B0B3C9486B7D1151FD794B95C3D2A938A55DC08203961535037AE5824F215A6DDB7ECA97E094BF4D23FCC497F1F4BE4D64B34F618B27D13760C8EBF5EC1AB3E2E3D
	7BB66F162C08F75FE2BC4A779F1E505FCF29DDCB0A31047CFE7E47F8C67C555CF7054877F94FBAC507C3969D17EBDC96F23777C83FAC29F7AD9A569279BECC23C4AAF79056923D6D9A1E7F83D0CB8788487C8AAA1A9EGGGE0GGD0CB818294G94G88G88G2CD538B2487C8AAA1A9EGGGE0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG549EGGGG
**end of data**/
}

/**
 * Return the JCheckBoxEnableEC property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxEnableEC() {
	if (ivjJCheckBoxEnableEC == null) {
		try {
			ivjJCheckBoxEnableEC = new javax.swing.JCheckBox();
			ivjJCheckBoxEnableEC.setName("JCheckBoxEnableEC");
			ivjJCheckBoxEnableEC.setPreferredSize(new java.awt.Dimension(174, 22));
			ivjJCheckBoxEnableEC.setText("Link to Energy Company: ");
			ivjJCheckBoxEnableEC.setMaximumSize(new java.awt.Dimension(174, 22));
			ivjJCheckBoxEnableEC.setMinimumSize(new java.awt.Dimension(174, 22));
			// user code begin {1}
			ivjJCheckBoxEnableEC.setToolTipText("Only for use with STARS.  Verify that STARS and its operator groups have been properly configured.");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnableEC;
}


/**
 * Return the JCheckBoxEnableLogin property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxEnableLogin() {
	if (ivjJCheckBoxEnableLogin == null) {
		try {
			ivjJCheckBoxEnableLogin = new javax.swing.JCheckBox();
			ivjJCheckBoxEnableLogin.setName("JCheckBoxEnableLogin");
			ivjJCheckBoxEnableLogin.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJCheckBoxEnableLogin.setText("Login Enabled");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnableLogin;
}


/**
 * Return the JComboBoxEnergyCompany property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxEnergyCompany() {
	if (ivjJComboBoxEnergyCompany == null) {
		try {
			ivjJComboBoxEnergyCompany = new javax.swing.JComboBox();
			ivjJComboBoxEnergyCompany.setName("JComboBoxEnergyCompany");
			ivjJComboBoxEnergyCompany.setPreferredSize(new java.awt.Dimension(215, 23));
			ivjJComboBoxEnergyCompany.setMinimumSize(new java.awt.Dimension(215, 23));
			// user code begin {1}
			ivjJComboBoxEnergyCompany.setEnabled(false);
			ivjJComboBoxEnergyCompany.setToolTipText("Only for use with STARS.  Verify that STARS and its operator groups have been properly configured.");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxEnergyCompany;
}


/**
 * Return the JLabelNormalStateAndThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNormalPassword() {
	if (ivjJLabelNormalPassword == null) {
		try {
			ivjJLabelNormalPassword = new javax.swing.JLabel();
			ivjJLabelNormalPassword.setName("JLabelNormalPassword");
			ivjJLabelNormalPassword.setText("Password:");
			ivjJLabelNormalPassword.setMaximumSize(new java.awt.Dimension(122, 17));
			ivjJLabelNormalPassword.setPreferredSize(new java.awt.Dimension(122, 17));
			ivjJLabelNormalPassword.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNormalPassword.setEnabled(true);
			ivjJLabelNormalPassword.setMinimumSize(new java.awt.Dimension(122, 17));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNormalPassword;
}


/**
 * Return the JLabelPhone1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRetypePassword() {
	if (ivjJLabelRetypePassword == null) {
		try {
			ivjJLabelRetypePassword = new javax.swing.JLabel();
			ivjJLabelRetypePassword.setName("JLabelRetypePassword");
			ivjJLabelRetypePassword.setText("Retype Password:");
			ivjJLabelRetypePassword.setMaximumSize(new java.awt.Dimension(122, 17));
			ivjJLabelRetypePassword.setPreferredSize(new java.awt.Dimension(122, 17));
			ivjJLabelRetypePassword.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelRetypePassword.setEnabled(true);
			ivjJLabelRetypePassword.setMinimumSize(new java.awt.Dimension(122, 17));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRetypePassword;
}


/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelUserName() {
	if (ivjJLabelUserName == null) {
		try {
			ivjJLabelUserName = new javax.swing.JLabel();
			ivjJLabelUserName.setName("JLabelUserName");
			ivjJLabelUserName.setText("User Name:");
			ivjJLabelUserName.setMaximumSize(new java.awt.Dimension(122, 17));
			ivjJLabelUserName.setPreferredSize(new java.awt.Dimension(122, 17));
			ivjJLabelUserName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelUserName.setEnabled(true);
			ivjJLabelUserName.setMinimumSize(new java.awt.Dimension(122, 17));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelUserName;
}


/**
 * Return the JPanelEC property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelEC() {
	if (ivjJPanelEC == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Energy Company");
			ivjJPanelEC = new javax.swing.JPanel();
			ivjJPanelEC.setName("JPanelEC");
			ivjJPanelEC.setBorder(ivjLocalBorder1);
			ivjJPanelEC.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxEnableEC = new java.awt.GridBagConstraints();
			constraintsJCheckBoxEnableEC.gridx = 1; constraintsJCheckBoxEnableEC.gridy = 1;
			constraintsJCheckBoxEnableEC.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxEnableEC.ipadx = -4;
			constraintsJCheckBoxEnableEC.insets = new java.awt.Insets(20, 11, 29, 6);
			getJPanelEC().add(getJCheckBoxEnableEC(), constraintsJCheckBoxEnableEC);

			java.awt.GridBagConstraints constraintsJComboBoxEnergyCompany = new java.awt.GridBagConstraints();
			constraintsJComboBoxEnergyCompany.gridx = 2; constraintsJComboBoxEnergyCompany.gridy = 1;
			constraintsJComboBoxEnergyCompany.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxEnergyCompany.weightx = 1.0;
			constraintsJComboBoxEnergyCompany.ipadx = -20;
			constraintsJComboBoxEnergyCompany.insets = new java.awt.Insets(20, 6, 28, 12);
			getJPanelEC().add(getJComboBoxEnergyCompany(), constraintsJComboBoxEnergyCompany);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelEC;
}

/**
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLoginPanel() {
	if (ivjJPanelLoginPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Login Information");
			ivjJPanelLoginPanel = new javax.swing.JPanel();
			ivjJPanelLoginPanel.setName("JPanelLoginPanel");
			ivjJPanelLoginPanel.setBorder(ivjLocalBorder);
			ivjJPanelLoginPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelUserName = new java.awt.GridBagConstraints();
			constraintsJLabelUserName.gridx = 1; constraintsJLabelUserName.gridy = 1;
			constraintsJLabelUserName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelUserName.insets = new java.awt.Insets(22, 20, 2, 3);
			getJPanelLoginPanel().add(getJLabelUserName(), constraintsJLabelUserName);

			java.awt.GridBagConstraints constraintsJLabelNormalPassword = new java.awt.GridBagConstraints();
			constraintsJLabelNormalPassword.gridx = 1; constraintsJLabelNormalPassword.gridy = 2;
			constraintsJLabelNormalPassword.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNormalPassword.insets = new java.awt.Insets(4, 20, 2, 3);
			getJPanelLoginPanel().add(getJLabelNormalPassword(), constraintsJLabelNormalPassword);

			java.awt.GridBagConstraints constraintsJTextFieldUserID = new java.awt.GridBagConstraints();
			constraintsJTextFieldUserID.gridx = 2; constraintsJTextFieldUserID.gridy = 1;
			constraintsJTextFieldUserID.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldUserID.weightx = 1.0;
			constraintsJTextFieldUserID.insets = new java.awt.Insets(20, 4, 1, 22);
			getJPanelLoginPanel().add(getJTextFieldUserID(), constraintsJTextFieldUserID);

			java.awt.GridBagConstraints constraintsJLabelRetypePassword = new java.awt.GridBagConstraints();
			constraintsJLabelRetypePassword.gridx = 1; constraintsJLabelRetypePassword.gridy = 3;
			constraintsJLabelRetypePassword.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelRetypePassword.insets = new java.awt.Insets(4, 20, 21, 3);
			getJPanelLoginPanel().add(getJLabelRetypePassword(), constraintsJLabelRetypePassword);

			java.awt.GridBagConstraints constraintsJPasswordFieldPassword = new java.awt.GridBagConstraints();
			constraintsJPasswordFieldPassword.gridx = 2; constraintsJPasswordFieldPassword.gridy = 2;
			constraintsJPasswordFieldPassword.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPasswordFieldPassword.weightx = 1.0;
			constraintsJPasswordFieldPassword.insets = new java.awt.Insets(2, 4, 1, 84);
			getJPanelLoginPanel().add(getJPasswordFieldPassword(), constraintsJPasswordFieldPassword);

			java.awt.GridBagConstraints constraintsJPasswordFieldRetypePassword = new java.awt.GridBagConstraints();
			constraintsJPasswordFieldRetypePassword.gridx = 2; constraintsJPasswordFieldRetypePassword.gridy = 3;
			constraintsJPasswordFieldRetypePassword.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPasswordFieldRetypePassword.weightx = 1.0;
			constraintsJPasswordFieldRetypePassword.insets = new java.awt.Insets(2, 4, 20, 84);
			getJPanelLoginPanel().add(getJPasswordFieldRetypePassword(), constraintsJPasswordFieldRetypePassword);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoginPanel;
}


/**
 * Return the JPasswordFieldPassword property value.
 * @return javax.swing.JPasswordField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPasswordField getJPasswordFieldPassword() {
	if (ivjJPasswordFieldPassword == null) {
		try {
			ivjJPasswordFieldPassword = new javax.swing.JPasswordField();
			ivjJPasswordFieldPassword.setName("JPasswordFieldPassword");
			ivjJPasswordFieldPassword.setPreferredSize(new java.awt.Dimension(167, 20));
			ivjJPasswordFieldPassword.setEnabled(true);
			ivjJPasswordFieldPassword.setMinimumSize(new java.awt.Dimension(167, 20));
			ivjJPasswordFieldPassword.setDocument( new TextFieldDocument(64));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPasswordFieldPassword;
}


/**
 * Return the JPasswordFieldRetypePassword property value.
 * @return javax.swing.JPasswordField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPasswordField getJPasswordFieldRetypePassword() {
	if (ivjJPasswordFieldRetypePassword == null) {
		try {
			ivjJPasswordFieldRetypePassword = new javax.swing.JPasswordField();
			ivjJPasswordFieldRetypePassword.setName("JPasswordFieldRetypePassword");
			ivjJPasswordFieldRetypePassword.setPreferredSize(new java.awt.Dimension(167, 20));
			ivjJPasswordFieldRetypePassword.setEnabled(true);
			ivjJPasswordFieldRetypePassword.setMinimumSize(new java.awt.Dimension(167, 20));
			ivjJPasswordFieldRetypePassword.setDocument( new TextFieldDocument(64));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPasswordFieldRetypePassword;
}


/**
 * Return the JTextFieldFirstName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldUserID() {
	if (ivjJTextFieldUserID == null) {
		try {
			ivjJTextFieldUserID = new javax.swing.JTextField();
			ivjJTextFieldUserID.setName("JTextFieldUserID");
			ivjJTextFieldUserID.setPreferredSize(new java.awt.Dimension(229, 20));
			ivjJTextFieldUserID.setEnabled(true);
			ivjJTextFieldUserID.setMinimumSize(new java.awt.Dimension(229, 20));
			ivjJTextFieldUserID.setDocument( new TextFieldDocument(64));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldUserID;
}


/**
 * adds any default group roles for a new user here
 *  these group DB objects only needs their GroupIDs set for now
 */		
/*private void addDefaultGroups( YukonUser user_ )
{
	if( user_ == null )
		return;

	YukonGroup group = new YukonGroup( new Integer(YukonGroupRoleDefs.GRP_YUKON) );
	user_.getYukonGroups().add( group );
}
*/

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	YukonUser login = null;
	
	if( o == null )
	{
		login = new YukonUser();
		/* addDefaultGroups( login ); */
	}
	else
		login = (YukonUser)o;

	if( getJTextFieldUserID().getText() != null && getJTextFieldUserID().getText().length() > 0 )
		login.getYukonUser().setUsername( getJTextFieldUserID().getText() );

	if( getJPasswordFieldPassword().getPassword() != null && getJPasswordFieldPassword().getPassword().length > 0 )
		login.getYukonUser().setPassword( new String(getJPasswordFieldPassword().getPassword()) );

	if( getJCheckBoxEnableLogin().isSelected() )
		login.getYukonUser().setStatus( UserUtils.STATUS_ENABLED );
	else
		login.getYukonUser().setStatus( UserUtils.STATUS_DISABLED );
		
	if(getJCheckBoxEnableEC().isSelected())
	{
		LiteEnergyCompany co = (LiteEnergyCompany)getJComboBoxEnergyCompany().getSelectedItem();
		
		EnergyCompanyOperatorLoginList ecop = new EnergyCompanyOperatorLoginList(new Integer(co.getLiteID()), login.getUserID());
		
		//if this is the same energy company that it was before, don't bother updating
		if(co.getLiteID() != oldEnergyCompanyID)	
			login.setEnergyCompany(ecop);
	}
	
	//there was an existing energy company link, but we want it no longer
	else if((! getJCheckBoxEnableEC().isSelected()) && oldEnergyCompanyID != -1)
	{
		EnergyCompanyOperatorLoginList ecop = new EnergyCompanyOperatorLoginList();
		ecop.setOperatorLoginID(login.getUserID());
		login.setEnergyCompany(ecop);
	}

	return login;
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
	getJTextFieldUserID().addCaretListener(ivjEventHandler);
	getJPasswordFieldPassword().addCaretListener(ivjEventHandler);
	getJPasswordFieldRetypePassword().addCaretListener(ivjEventHandler);
	getJCheckBoxEnableLogin().addActionListener(ivjEventHandler);
	getJComboBoxEnergyCompany().addActionListener(ivjEventHandler);
	getJCheckBoxEnableEC().addActionListener(ivjEventHandler);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerContactLoginPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJPanelLoginPanel = new java.awt.GridBagConstraints();
		constraintsJPanelLoginPanel.gridx = 0; constraintsJPanelLoginPanel.gridy = 1;
		constraintsJPanelLoginPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLoginPanel.weightx = 1.0;
		constraintsJPanelLoginPanel.weighty = 1.0;
		constraintsJPanelLoginPanel.ipadx = -10;
		constraintsJPanelLoginPanel.ipady = -26;
		constraintsJPanelLoginPanel.insets = new java.awt.Insets(3, 8, 3, 8);
		add(getJPanelLoginPanel(), constraintsJPanelLoginPanel);

		java.awt.GridBagConstraints constraintsJCheckBoxEnableLogin = new java.awt.GridBagConstraints();
		constraintsJCheckBoxEnableLogin.gridx = 0; constraintsJCheckBoxEnableLogin.gridy = 0;
		constraintsJCheckBoxEnableLogin.ipadx = 32;
		constraintsJCheckBoxEnableLogin.ipady = -2;
		constraintsJCheckBoxEnableLogin.insets = new java.awt.Insets(10, 8, 2, 251);
		add(getJCheckBoxEnableLogin(), constraintsJCheckBoxEnableLogin);

		java.awt.GridBagConstraints constraintsJPanelEC = new java.awt.GridBagConstraints();
		constraintsJPanelEC.gridx = 0; constraintsJPanelEC.gridy = 2;
		constraintsJPanelEC.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelEC.weightx = 1.0;
		constraintsJPanelEC.weighty = 1.0;
		constraintsJPanelEC.ipadx = -10;
		constraintsJPanelEC.ipady = -26;
		constraintsJPanelEC.insets = new java.awt.Insets(4, 8, 4, 8);
		add(getJPanelEC(), constraintsJPanelEC);
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
	if( getJCheckBoxEnableLogin().isSelected() )
	{

		if( (getJTextFieldUserID().getText() == null || getJTextFieldUserID().getText().length() <= 0)
			 || (getJPasswordFieldPassword().getPassword() == null || getJPasswordFieldPassword().getPassword().length <= 0)
			 || (getJPasswordFieldRetypePassword().getPassword() == null || getJPasswordFieldRetypePassword().getPassword().length <= 0) )
		{
			setErrorString("The Userid text field, Password text field and Retype Password text field must be filled in");
			return false;
		}

		if( getJPasswordFieldPassword().getPassword().length == getJPasswordFieldRetypePassword().getPassword().length )
		{
			for( int i = 0; i < getJPasswordFieldPassword().getPassword().length; i++)
			{
				if( getJPasswordFieldPassword().getPassword()[i] == getJPasswordFieldRetypePassword().getPassword()[i] )
					continue;
				else
				{
					setErrorString("The Retyped Password text field must be the same as the Password text field");
					return false;
				}
				
			}
		}
		else
		{
			setErrorString("The Retyped Password text field must be the same as the Password text field");
			return false;
		}

	}
	
	return true;
}


/**
 * Comment
 */
public void jCheckBoxEnableEC_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	getJComboBoxEnergyCompany().setEnabled(getJCheckBoxEnableEC().isSelected());
	
	if(getJCheckBoxEnableEC().isSelected())
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		List companies = cache.getAllEnergyCompanies();
		java.util.Collections.sort( companies, LiteComparators.liteStringComparator );
		
		for(int j = 0; j < companies.size(); j++)
		{
			//weed out the default energy company
			if(((LiteEnergyCompany)companies.get(j)).getEnergyCompanyID() != -1)
				getJComboBoxEnergyCompany().addItem((LiteEnergyCompany)companies.get(j));
		}
		
	}
	
	
	fireInputUpdate();
	
	return;
}


/**
 * Comment
 */
public void jCheckBoxEnableLogin_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJTextFieldUserID().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJPasswordFieldPassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJPasswordFieldRetypePassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJLabelNormalPassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJLabelRetypePassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJLabelUserName().setEnabled( getJCheckBoxEnableLogin().isSelected() );

	
	fireInputUpdate();
	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		UserLoginBasePanel aUserLoginBasePanel;
		aUserLoginBasePanel = new UserLoginBasePanel();
		frame.setContentPane(aUserLoginBasePanel);
		frame.setSize(aUserLoginBasePanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;

	YukonUser login = (YukonUser)o;
	
	if( !login.getYukonUser().getStatus().equalsIgnoreCase(UserUtils.STATUS_DISABLED) )
		getJCheckBoxEnableLogin().doClick();

	getJTextFieldUserID().setText( login.getYukonUser().getUsername() );
	getJPasswordFieldPassword().setText( login.getYukonUser().getPassword() );
	getJPasswordFieldRetypePassword().setText( login.getYukonUser().getPassword() );
	
	if(((YukonUser)o).getUserID().intValue() == UserUtils.USER_ADMIN_ID)
	{
		getJTextFieldUserID().setEnabled(false);
		getJCheckBoxEnableLogin().setEnabled(false);
		getJCheckBoxEnableLogin().setSelected(true);
		if(ClientSession.getInstance().getUser().getUserID() == UserUtils.USER_ADMIN_ID)
		{
			getJPasswordFieldPassword().setEnabled(true);
			getJPasswordFieldRetypePassword().setEnabled(true);
		}
		else
		{
			getJPasswordFieldPassword().setEnabled(false);
			getJPasswordFieldRetypePassword().setEnabled(false);
		}
	}

	
	long company = EnergyCompanyOperatorLoginList.getEnergyCompanyID(login.getUserID().longValue());
		
	if(company != -1)
	{
		getJCheckBoxEnableEC().doClick();
		for(int d = 0; d < getJComboBoxEnergyCompany().getModel().getSize(); d++)
		{
			LiteEnergyCompany ceo = (LiteEnergyCompany)getJComboBoxEnergyCompany().getModel().getElementAt(d);
			if(ceo.getLiteID() == company)
			{
				getJComboBoxEnergyCompany().setSelectedItem(ceo);
				oldEnergyCompanyID = company;
				break;
			}
		}
	}

}
}