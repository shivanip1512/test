package com.cannontech.dbeditor.wizard.capfeeder;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.roles.application.TDCRole;
 
public class CCFeederNamePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private Integer originalMapLocID = null;
	private javax.swing.JTextField ivjJTextFieldSubName = null;
	private javax.swing.JLabel ivjJLabelFeederName = null;
	private javax.swing.JLabel ivjJLabelMapLocation = null;
	private javax.swing.JLabel ivjJLabelAlreadyUsed = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisable = null;
	private javax.swing.JTextField ivjJTextFieldMapLocation = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCFeederNamePanel() {
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
	if (e.getSource() == getJCheckBoxDisable()) 
		connEtoC3(e);
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
	if (e.getSource() == getJTextFieldSubName()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldMapLocation()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}

/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (DistrictNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyNamePanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JCheckBoxDisable.action.actionPerformed(java.awt.event.ActionEvent) --> CCFeederNamePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldMapLocation.caret.caretUpdate(javax.swing.event.CaretEvent) --> CCFeederNamePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextFieldMapLocation_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public void eitherTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GAAE910ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF4D4553531456A2B2CD5DC25EDEA2B22C6D7DC66A94F5237E6790D98CBC490BF7C8A81C1C1F9889A17E0ED4B2BA71F49A4109FC9D07AC409FCB451987221CA07947399C2C20304A4040081F3B3F313DC18DF6F3DA193CA6B5B7B1CFB6E5C193913885A354AF2675E3377B97B6C3377BE7B6CFD6EC8CA36CF312625179012E6A55A3FCF5388F97DD3C2AE7E676CFBB92E7E810B0D987E3D8730195C
	1D19894FF620752D37588A49D5CAB634DD504EFE59E2FB8B5E0B49C4A5368ADE224C63875A02618DB70CBDCF03BE0F0D36DF6B4905E7BE409060A682ACFCG674FBF39D543CF05F63E760E1016CE486487314F15A7369A705B9EE47D0660F991E0DFA276A9BF39CD2327G8E7B2C8348CEE4FDCB6119C3253B523ECF6B57FB6D9432EE66FB69F9B81701BF0798EDEC9C67C1EE270A48A077F75643332E7D5C6081274FD369943CDE1FD7951DC3152EFE51A529BE39F2C43AA048AEA07987C451A54AF6FBD3D3B3FD
	7BDC70086D02D7F417BC04F25FF1A21B728439E11EA99437136A2A875E7DGD7BF44E5FFB22FCE13ED7239A9E459ADA5D1E5DF719013B9FBFAD0763A3F9F4F32CCC77E9DA7F335F1AD500E81E032DCF7124B32E1BA17E5FDBB765D7B3045568660FA18636FBB06780B501E8C1070B01771AF9FF099FB7EFE97C9CEFAA13DA00A0CC98FB35956BC9C14F12A67F8569F293FAC34224D4B12E0DE00BE0089GF1GC9GEFD3BF3DBA73C3F8B6742BCDBE0F4767ED707B9B5DAE79084FA53A615DEE870A066B901DBE59
	C588734529A7AACC64611012946A0788ECEDA788771D040E5B08C5D915EEB5D95B34F9BB53D3B59E190F98747F886A6DA754363B609D4721BC8CFF8B454798704CD665940F6DD820EDF8046B39A71F4B5216F99DA92CEECE37194892BBFDFFFA5F2F188C01DF85E5E1BA3E5B06BA0E83FCA240C200D400429996DB558C2E63A5193563693895FCDF768A6E4E61FE3FA02B0D43129B7CDC53F54F4931F4ED86EC5C40A50FE3FA092FB55AA8659787CD7512927FE1FA5B8C268F570C20DE585CB7C61DFB420CC85F48
	57EDA851BD91BB0359B0C347AB949FEF40B35B7E1E6EAD5A865A36E40B0D752F6A203E109C6EA3F5949F1B9C6EA3530FF19F198A3412C7ADB6E65F769C0C0981E8CF86C8GC882D881C0E6C23F195C46E556BDDFF39F359B6C4B46AE373E89CFB1204AC22BD7D1852FD36C96E4C968F70B8A517A2C3622EC7DA163DE267B5B89F39C923D22AC28124F8B5CA1B802B3A92C4F6B564D58C796A935CB7208B00147CFB8DF1285C05C02229E773B84D58C76E0746CAC740973C2B7F08486E86B19282F99B4666E057776
	19DCD77F580838F8E8A76AFA79EED691BC7F8A4EAD3A6DF60764C4B184F9147B6875F4EF4E057E0147603CF90C6F4D17FA388FBA72EEA595739C26BEC84F16D94C1F6E1B956E0337D35EABG1FB95BE25BB51B1F23FBFAFACC78208CE36D8D14AD9AADE02031B9DACC6748789A7BB69A4DEAF8E77AFEF1932E4F816B6A8308194DF57EC0D6A97345C6C19139779D1541F1C4D750822C6F3DB6BCDB447322D7F508FE516B0297C5337DF4EA8B4DAF87FC3A4FA4D3BC4629A6F09CC547BFEE5B4C70404368651CBE1F
	5285D515257EE1D56C94D5D5728E8647AF2532A8A1A1D051563AB6176EDBD863D400C4FD2D035945867FEA73794E8D7B3BF067E8FE7A5F74BC1E8B7D576876779D433C2A70F15E9E256DA0BD6D18196D0C00E3AFA52EC646667F9BF70E71FCE57CB9BE6960B90AD543B1FCA3456F05B57482CC507510CCE562FB0C0F39DC994CE5DACE674878974D41FC62D4CEF41F96F24CFDFAB0475C27BDB9E37B6CF00E194FB25A459CDC1FEF44EB5883D764E0BCF47A3CDE11EEC4FD88E1F971CBDD388E49AB29A7A4572028
	F6CA97C4705F4751171752181BG6F090F73B8B932134749DD97EEA209DFEEB74D27E2EFBD103EF58E73590AB941B849E47A4EC6748751ADFAC0D1CD3EE12F2A6506B41EEBF8A5032DA9B79647169D6E829DB6CB225B85A98B666395CE5877EAA57D4B0E12323342F9A1D0290C0042AA29B5AA1B10FA90DF4BCBBF7E78C0E9C4C2DFA1CA6A10A86B4C3FECB270EB9B0B617E726E72AAA663EBE77DCC4626A151F92E5197F0C88A06048A012A3C12BD5AC5F940A7FBC4971533D298D1B51E8D14BC26148312AC36FA
	7D43DCB2183BC61BFB734DE17AB9A2785BFCCEFA7CFECBBABAEB467B52B4D54B4F1052FC34FD9B78C49FC0E08E0F2B191B104E632AF6F8B4795CEE6636C0CB1903BE1952D3E27033C9F3D8DD1A2F5333CF0ECD5F9B154E64F8E5D3A16EAF71EF43A224C5E8453F0A23BC3759D494C66B965C43411C0964EE02275BF0A6B07C2B39667E7E5BDCBC874E697C7FB7B70BE06E43F8327DB9D5C01EFED97240910A071CD6DF4FBEE92C2FF7BD8167942C43854FGG9BFB74C34C739C62F949A922B59BDCAED9D494EAD4
	30411348E73803576013E0DCBCC03246AB73C9269756EFDFA76F978B6D29GCFBC497A95BEE56CB73D1B772B82FC8B4050D32CDF1C56EF5DD334C655EB68BDBAAE3787F1CF03707BB3F9EDF02450D87B175EBB1CE196EF9076BD2D4971F4F84DF43CG793A81FF71E96EEFA3F946F33CC1D1FCCE097A35E63377730D310861BE4A5F88CF580B8A54CFAEB1406DDE1D0F32E2CE2B21B55B5D7151DFC8E40EFE105EB670B429E9046E11AEDF932455AD60BACDFA286E1644914275700FE663BD42FD8595691B2268E1
	12367E2986BD34C00D4EE47A299DEBC333FB90A79532F2F0D82A9CD6A5CC1E85D520C107B98C3BEF7874D436363E5427793D0273B47AF759DC0B2D9E20870055B8B750F39F7B7A81978F40766045B346BD38ECAE4B153267D9ECAC26D9BB51A7AEB1F6E9BA7F4D072827EF9255193EEFAE2E57641BF567F5246E2BBC53BB112B8B5ECA5FB60F6959B1AF286F7A75CAC6621B5D5A5D0D504975B0897AA44F63FAF8E3542887B21F6921ECBE57C3F2E78DBCAF7D90617E17D4DE447CB56C6C60B5680B5DDC2EBA18D3
	1D4F657A6E2819FD92F439BACEDC3EFDEEFC37911E5F7CC4516FF7EA0C71A1F159C7267988C262FCE61F3973436303E3B37AF4AA60B397707810103F59909FBAE0C78A5EC10868FA4ED71041E23756BED3E065311B70336E0AA25463D6603997208D4085B0C11FE3E5413877B48783E35C53541D8A3F7308DB902C93AC8BA2EB86F6A731186E57D98B581DC41E0EDFCD712986BC7393096231DD32906A3E05BCFF0F6B881FFF4D4250B933FF9D5AE6636E0E9877EED83BA2EC9C731D5DE55BF86CB2F8075D9E4CFD
	D80C7A4519ECEDAE472F79DD4102E29CFB915A31GD381E681248264AE32582A96F11B6CADB40B5921B60996300375A7DB67E0676569E7B8E2DCE5D83BFD1119FE8EF43FED2E9F1E17EAB17C138B289F5CBFC30B38ADDF693EBCD95F089897EE4BA9E132B2996E9965673FE5913751DBDB68BE04B6D94C6D717342BC43BEEC77C9DED55F032D1BB1DF724952206465384FA94E6913E99144F07FD1943DBE1BD4E4DE1F7D38483CBE7BF95158F78EF1C5516AB7F6BF50C94F4FC2D85FAEG062B6AC5DC8B347B96F3
	FBACBF0D795D84E84781A482AC83A0A9965B36945EA715762907F6DF8A4B5DE2D2D8EE06F917F677F03AAF4C9E91F1F1B163C94F2014481801739B5B93B4D9A2FD617B634E057D723477CDA6F4EBD8BBC373D3A36E9D0AFF744D82EA87C591953AA1B1DB51E8AF28DB08C94D0BA930463318AC6E48517A9CD4FDCD557A9AACE70C7EBCA5CC9F53425A49A95CCF1797C77731D54566BE76D2B16AC2718D4BCEB15C0F5697EF55E94188532A0A9EDC866B7387BADE95E4285B4347DBCB366A34E86373CB2279AA5351
	8D1DC69D556ABA5AFF4128239450C1AAC06692261B2DCBA2FD0871ABBBE16437D767776068D85F8DAACC78B17937ED41F3AA2037CBE9FBD41F6F6CA90C85EEE154B78CC1A3E1896E35ABCE713A299E5ABDGB1G1317309C0745517F9AC15F9C10858F282353AFBA4955CBF16C21DEBEF69D744F86DC99C08B4050D21E9B7DF998F5FA38CD68078343AD0B02EB7438A23AF4192E72976A74E01D2D5572EDF4EC78CD07BE766817C15E063CC9277F300F728E3B05E0346BCFED89619DBA1E65FD6F076465CFACE5BA
	D937146705718EB3FA76B3FA5EC84C685B1E61F1EE8EBD172B20DD2F638E3473585725634A88626220ED51F19D744C1A05F36938E6191E97504EDC46F1BF20382D50AE53F16BE4BE47DE9DB7FF1463A6AC63754A6D27787D65DC4065AE677ED05D9712F7E056ED379F12A5D723B058C63D0B1DE92F50580E639D4BF9ED347D8B7A4D955A1381921673FB34407B3C1ECB386DBA5296F350743B70322E0A7424654C7FD7AD8F662DC9D04F31B9BD3DFC4EDD4FDAEC3D4FA26EE73DC6BB207D2FCDE3B984234F8E2123
	CF6DA4C67A9BA1F414777EF4A33D7A34110EBAFAC79B4F567658EE3E368D0F459377AFEB23FE730EFB162DA9D9FB161E59C9D29A3A35DA7555012D44B41F21BB002D7F74991E4B106778B771E79CBC7F5B27636EA19C67FF0E6F552203F87FB2G756F2A67304F6B14D7BC3CCF8368DB0138636D0EB1E2D386895EF763FB850C29875829613A607DE89A6B43667C0C4A60743907BDDE0530F93B29EC1883452F6032C997C2E57B0A7A3F8568C9FA1F53542FBDDE9192D549497A299477A846A83D5FEDB272D2E16B
	E72E445FFDDC016ECC587AB6A9E32D0F2F0D2F2F5E30BE7ADB846037DFFBE77C560E0E27AFA3F42D8C65B7846D3B08715B41DD91B54EF840465DFF1963DE3B64F12CF6FA056EF3F5A52B1D52F47C6FA8BE4600E731E67797BC56CC84DA6ACA1E83D757204FF7BDEF3179812E86188210881072BC4FB5EE5D02BF759933C66972795BA099F19BD2D436BE3977723E51BFE463FBFA5ABDB7934B77DECD0BF67F147ABC5B4B05AF302725F7A7453375A555521C95E83DGB1G31G89GC9AF70752D5DD2415607D7E5
	8D2AAAB80768AD9CDF9CDE0C8551F8C03335750764EE8B357957E8CF328AE241AA333CF50543B82ECC6BD327BD5BD7C5669FDA5EC20C63DCDA3F00761CE8B20E6D3FCF6D0470F8D05D3E9B1EDEF124D43F50E906CDDF36C3913C105B4A52400E0A522A0A526A3D653AAD260C986FBC76ED2C0EFA67B1C513E19A176987BB49F6BAAF21B1077DCB7DAC9247E51D0D5BDB1BF7F9AE7701EDF77C0CB8963C1A4677ED783CEB388E7FA7F8E6DD3C2BBB0B2D59C360B881CF67F3DCF5A0288F3B3C43DC9FAEC9F07B868D
	4AE07D2F117179016B83CD46B9DF98F5D3F85D5B69E9D1E4D4B559E28CB2B2F97656A3A7B3BDB17A0986227B40EF3E883E135E607BC7DF0C773D751BG1B4371AF1D036DC5FBCD743A2621463C2EB9D4E3DEBB9F29997B5B66711A685FB6FBEB0C5FB6D9AC7920917310E9E03FA7G5681E42F06FCF3B50FA32BEB50177017B011B16420EB906BAA7C750B4A671FDD9BFD2DF3EB477F4D4C12DA7CBE506FD3D51F8779F37CF335E8AF556727C82DA62CC59D495260104A3A721A3016FE2B9087A81A652A23B4AF59
	876BEAD94D735738A14475C25B2D63F2A41E5386F4DC9B4D0512211D2263FE6F60F9F82A0E7B3E9DF119AFC20CFC1163DE36F3FE25BAEEBE5D8FFEE84768386F6CC05CE4E847693897F67031D3DF64E71B23096E25B5E023B55CC6976D9F4133DBB89B79F584106D324FAF4A6AA843522FED1A6D2FF1E01ED187FCFAGAE82CC860887C884188B308AE083C049DA0B2D82209E208BE088A0GB091E04ADAAEC7D1DD7998F2E849BEF598D9E0F7D0CC0ED78E925DF60F9D589D0D87F85CE108E7C77B4FC2A15EA58C
	6AE376B4C67739CF9A4777394F9A69F7AA70152ED13FD657BB9A693D584000A26A3E5D5AD82863DA15F61FA2E16947E26824B3213966ED545F2601EE9257729C725E21509C7258A1DCB74B79CA2531F2BE1EBBF2884FA1D357323BAA5E2F18B89D58BEAFA992FEC3E5B2DDF070BAE78374E738320374AE6DA5702717381CB759C365D4DB02F2E66D9FAF57CD9BC34EB618230098F37935A4D4EF99E9A8CF8F74F369727CF4C728BCBFE98D4A73079D6349B3165EA6439C5B35F74D46CDB8CE0F2FEC3BE83476A60C
	1578BB8FB8C7258B34706F040E5EC1C63F51915D8FEFF30C6F07F7B94CE35FAF9D11316F9E07F96CCBF498E39F43BD62C03F98047D5723771B2963CE6A38B914A762CEC8AEF5C8474F23F2A13EC54489B9FEB115EB0462FAB45CB2836E2406DB69403D84CB9E726977492B9D546E125B2D45EC5AC7724256527571AA15D1728B8639DF57F1A8B773074F5A086ECF31C378BBFC135C2781146D0654279A7E2BA95747769F890E3D16729E905C0A28539FB89A247F537EF573AAB6767ECE22733926BB4827D43DB4BE
	F71E40316C7FD1D81AF69894153016D8D5122F5DCE1532CBB0C5A6F9A18C9F3969CF4F4FDA5A2DE4102CD672C75CE31D22CCDDD7F461D52F767B50E6126347EF82783325D612295007D5D1C75DE29BA99968405FEAA82A68EA601BC2D149967AEDF18FFE645E53A2A8C3AA4A313F0DE4FA71172C2408115D02F7F08F5BA17B83787B1E3406BEDC8A5D1B8464AD2672A217CA1DCB713D853B5FBB3C4212C1521B49C637CFF0B58BCEB883DAC90EBAA40B4A104F6D8A78BF2B270C32C7505D310C4AGF6EF6B4B4745
	D662C51DAE743940759373D665B5C35A9C10499E5A93C3CA255E4355B4A448B015A8372A784BDC1F3CCB27DD712E7A7E6CC3EF752613B4ED11FFA37FA499813F4A94861397D01638644AE610DADD3476511B9757252E01A14D44CA3FEA42440541DE1F8FFB7AC579FB89BFFAE75364E55720DCFED40F021CEDAA499A90BC12FBD4A5B9FA9943C9B990C53C8AFE4406F9CB43964DC93A6C15BFFC346AC7377F6E9415BDB8D4A55B1915E0681EC151675953B0B0A0F9C51D81188C583F233347BE15E1FD0A8257EEB9
	BC6F0DFEF0B1874970D4D5513F5574EF8D7E2D2618EA0A292E813B2512B17F05538381DCF3E1C0D7B0CA52CA4344327335760C67EE49C8A7192DA40B05CD70DF0CFD603FB4B436128C881B5475F8F90761BE0BAAB2AD42A191EB0D74ACC467D062A66C0EAF79E09F875D2D585ACC0945746F1B173742F782F4B496766D0495CC47D0BE9D1BE6008C587BE55A4DF8A9DE0D172BA4178501D23C6D362B24700860AF653753252D8E3BE149EC0139BA7B48A5E79A17DC9F3EE45DC3D918E75D104537240B3220AE3855
	5E7A562C667AB62DD69314B4DB837CDFF3EC7FFE127EB9A8E906A6E99153AF2B794B3FB2567C2FDFD99475BEA4EE3DF6A735BE7CB7A037DB116DBA7C7F6BDE63B541EFB3BFA6517379CE0F60F62B027BDC03570539FD6328AA86EB59BB2D48DF61FDB4221693D9D16E3306B17F8FD0CB8788CFBD310F1494GGA0B8GGD0CB818294G94G88G88GAAE910ADCFBD310F1494GGA0B8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG
	4E94GGGG
**end of data**/
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
			ivjJCheckBoxDisable.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDisable.setMnemonic('d');
			ivjJCheckBoxDisable.setText("Disable Feeder");
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
 * Return the JLabelAlreadyUsed property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAlreadyUsed() {
	if (ivjJLabelAlreadyUsed == null) {
		try {
			ivjJLabelAlreadyUsed = new javax.swing.JLabel();
			ivjJLabelAlreadyUsed.setName("JLabelAlreadyUsed");
			ivjJLabelAlreadyUsed.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelAlreadyUsed.setText("(Already Used)");
			ivjJLabelAlreadyUsed.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAlreadyUsed;
}


/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFeederName() {
	if (ivjJLabelFeederName == null) {
		try {
			ivjJLabelFeederName = new javax.swing.JLabel();
			ivjJLabelFeederName.setName("JLabelFeederName");
			ivjJLabelFeederName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelFeederName.setText("Feeder Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFeederName;
}


/**
 * Return the JLabelMapLocation property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMapLocation() {
	if (ivjJLabelMapLocation == null) {
		try {
			ivjJLabelMapLocation = new javax.swing.JLabel();
			ivjJLabelMapLocation.setName("JLabelMapLocation");
			ivjJLabelMapLocation.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMapLocation.setText("Map Location ID:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMapLocation;
}


/**
 * Return the JTextFieldMapLocation property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMapLocation() {
	if (ivjJTextFieldMapLocation == null) {
		try {
			ivjJTextFieldMapLocation = new javax.swing.JTextField();
			ivjJTextFieldMapLocation.setName("JTextFieldMapLocation");
			// user code begin {1}

			ivjJTextFieldMapLocation.setDocument( 
					new com.cannontech.common.gui.unchanging.LongRangeDocument() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMapLocation;
}


/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSubName() {
	if (ivjJTextFieldSubName == null) {
		try {
			ivjJTextFieldSubName = new javax.swing.JTextField();
			ivjJTextFieldSubName.setName("JTextFieldSubName");
			ivjJTextFieldSubName.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldSubName.setColumns(12);
			// user code begin {1}
			
			ivjJTextFieldSubName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_CAP_SUBBUS_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSubName;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	CapControlFeeder ccFeeder = null;
	
	if( val == null )
		ccFeeder = (CapControlFeeder)com.cannontech.database.data.capcontrol.CCYukonPAOFactory.createCapControlPAO( 
							com.cannontech.database.data.pao.CapControlTypes.CAP_CONTROL_FEEDER );
	else
		ccFeeder = (CapControlFeeder)val;


	ccFeeder.setName( getJTextFieldSubName().getText() );

	ccFeeder.setDisableFlag( 
		getJCheckBoxDisable().isSelected() 
		? new Character('Y')
		: new Character('N') );

	 
	if( getJTextFieldMapLocation().getText() == null || getJTextFieldMapLocation().getText().length() <= 0 )
		ccFeeder.getCapControlFeeder().setMapLocationID( new Integer(0) );
	else
		ccFeeder.getCapControlFeeder().setMapLocationID(
				new Integer( getJTextFieldMapLocation().getText() ) );
	
	originalMapLocID = ccFeeder.getCapControlFeeder().getMapLocationID();

	return ccFeeder;
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

	// user code end
	getJTextFieldSubName().addCaretListener(this);
	getJCheckBoxDisable().addActionListener(this);
	getJTextFieldMapLocation().addCaretListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceNameAddressPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsJLabelAlreadyUsed = new java.awt.GridBagConstraints();
		constraintsJLabelAlreadyUsed.gridx = 2; constraintsJLabelAlreadyUsed.gridy = 1;
		constraintsJLabelAlreadyUsed.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAlreadyUsed.ipadx = 3;
		constraintsJLabelAlreadyUsed.ipady = 2;
		constraintsJLabelAlreadyUsed.insets = new java.awt.Insets(6, 1, 5, 23);
		add(getJLabelAlreadyUsed(), constraintsJLabelAlreadyUsed);

		java.awt.GridBagConstraints constraintsJLabelFeederName = new java.awt.GridBagConstraints();
		constraintsJLabelFeederName.gridx = 0; constraintsJLabelFeederName.gridy = 0;
		constraintsJLabelFeederName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFeederName.ipadx = 7;
		constraintsJLabelFeederName.insets = new java.awt.Insets(7, 8, 8, 42);
		add(getJLabelFeederName(), constraintsJLabelFeederName);

		java.awt.GridBagConstraints constraintsJTextFieldSubName = new java.awt.GridBagConstraints();
		constraintsJTextFieldSubName.gridx = 1; constraintsJTextFieldSubName.gridy = 0;
		constraintsJTextFieldSubName.gridwidth = 2;
		constraintsJTextFieldSubName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldSubName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldSubName.weightx = 1.0;
		constraintsJTextFieldSubName.ipadx = 167;
		constraintsJTextFieldSubName.insets = new java.awt.Insets(6, 1, 5, 31);
		add(getJTextFieldSubName(), constraintsJTextFieldSubName);

		java.awt.GridBagConstraints constraintsJLabelMapLocation = new java.awt.GridBagConstraints();
		constraintsJLabelMapLocation.gridx = 0; constraintsJLabelMapLocation.gridy = 1;
		constraintsJLabelMapLocation.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMapLocation.ipadx = 15;
		constraintsJLabelMapLocation.insets = new java.awt.Insets(5, 8, 3, 18);
		add(getJLabelMapLocation(), constraintsJLabelMapLocation);

		java.awt.GridBagConstraints constraintsJCheckBoxDisable = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDisable.gridx = 0; constraintsJCheckBoxDisable.gridy = 2;
		constraintsJCheckBoxDisable.gridwidth = 2;
		constraintsJCheckBoxDisable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxDisable.ipadx = 29;
		constraintsJCheckBoxDisable.insets = new java.awt.Insets(1, 8, 80, 81);
		add(getJCheckBoxDisable(), constraintsJCheckBoxDisable);

		java.awt.GridBagConstraints constraintsJTextFieldMapLocation = new java.awt.GridBagConstraints();
		constraintsJTextFieldMapLocation.gridx = 1; constraintsJTextFieldMapLocation.gridy = 1;
		constraintsJTextFieldMapLocation.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldMapLocation.weightx = 1.0;
		constraintsJTextFieldMapLocation.ipadx = 87;
		constraintsJTextFieldMapLocation.ipady = 3;
		constraintsJTextFieldMapLocation.insets = new java.awt.Insets(3, 1, 1, 1);
		add(getJTextFieldMapLocation(), constraintsJTextFieldMapLocation);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}


	boolean amfmInterface = false;
	try
	{	
		amfmInterface = ClientSession.getInstance().getRolePropertyValue(
			TDCRole.CAP_CONTROL_INTERFACE, "NotFound").trim().equalsIgnoreCase( "AMFM" );
	}
	catch( java.util.MissingResourceException e )
	{}
	
	getJLabelMapLocation().setVisible( amfmInterface );
	getJTextFieldMapLocation().setVisible( amfmInterface );
	getJLabelAlreadyUsed().setVisible(false);

	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldSubName().getText() == null
		 || getJTextFieldSubName().getText().length() < 1 )
	{
		setErrorString("The Feeder Name text field must be filled in");
		return false;
	}

	if( getJTextFieldMapLocation().isVisible() && getJLabelAlreadyUsed().isVisible() )
	{
		return false;
	}

	return true;
}


/**
 * Comment
 */
public void jTextFieldMapLocation_CaretUpdate(javax.swing.event.CaretEvent caretEvent) 
{
	//if we are using MapLocation IDs, we must validate them!
	if( getJTextFieldMapLocation().isVisible()
		 && getJTextFieldMapLocation().getText() != null 
		 && getJTextFieldMapLocation().getText().length() > 0 )	
	{
		int[] mapIDs = null;
		if( originalMapLocID != null )
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs( originalMapLocID.intValue() );
		else
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs();
		
		boolean show = false;

		StringBuffer buf = new StringBuffer("The MapLocationID selected is already used, try another\nUsed IDs: ");		
		int id = Integer.parseInt(getJTextFieldMapLocation().getText());

		for( int i = 0; i < mapIDs.length; i++ )
			if( mapIDs[i] == id )
			{
				show = true;
				//setErrorString("The MapLocationID selected is already used, try another");
				for( int j = 0; j < mapIDs.length; j ++ )
				{
					if( (j % 20 == 0) && j != 0 )
						buf.append("\n  ");
						
					buf.append( mapIDs[j] + "," );
				}

				setErrorString( buf.toString() );
				break;
			}
			
		getJLabelAlreadyUsed().setVisible(show);
	}

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
		CCFeederNamePanel aCCFeederNamePanel;
		aCCFeederNamePanel = new CCFeederNamePanel();
		frame.setContentPane(aCCFeederNamePanel);
		frame.setSize(aCCFeederNamePanel.getSize());
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	if( val != null )
	{
		CapControlFeeder ccFeeder = (CapControlFeeder)val;

		getJTextFieldSubName().setText( ccFeeder.getPAOName() );

		getJCheckBoxDisable().setSelected(
			ccFeeder.getDisableFlag().charValue() == 'Y'
			|| ccFeeder.getDisableFlag().charValue() == 'y' );
		
		//set our map location id values
		originalMapLocID = ccFeeder.getCapControlFeeder().getMapLocationID();
		getJTextFieldMapLocation().setText(
				ccFeeder.getCapControlFeeder().getMapLocationID().toString() );
	}
	
	return;
}
}