package com.cannontech.dbeditor.wizard.device.lmprogram;

import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import java.util.Vector;
import com.cannontech.database.data.device.DeviceTypesFuncs;

/**
 * This type was created in VisualAge.
 */

public class LMProgramDirectMemberControlPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	
	private com.cannontech.common.gui.util.AddRemovePanel ivjAddRemovePanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JCheckBox ivjJCheckBoxActivateMaster = null;

class IvjEventHandler implements com.cannontech.common.gui.util.AddRemovePanelListener, java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMProgramDirectMemberControlPanel.this.getJCheckBoxActivateMaster()) 
				connEtoC3(e);
		};
		public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == LMProgramDirectMemberControlPanel.this.getAddRemovePanel()) 
				connEtoC1(newEvent);
		};
		public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {};
		public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == LMProgramDirectMemberControlPanel.this.getAddRemovePanel()) 
				connEtoC2(newEvent);
		};
		public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseExited(java.util.EventObject newEvent) {};
		public void rightListMouse_mousePressed(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {};
		public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramDirectMemberControlPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
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
 * connEtoC2:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
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
 * connEtoC3:  (JCheckBoxActivateMaster.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramDirectMemberControlPanel.jCheckBoxActivateMaster_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxActivateMaster_ActionPerformed(arg1);
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
 * Return the AddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getAddRemovePanel() {
	if (ivjAddRemovePanel == null) {
		try {
			ivjAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjAddRemovePanel.setName("AddRemovePanel");
			// user code begin {1}
			ivjAddRemovePanel.leftListLabelSetText("Available Subordinates");
			ivjAddRemovePanel.rightListLabelSetText("Assigned Subordinates");
			ivjAddRemovePanel.setAddRemoveEnabled(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddRemovePanel;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G0EE07DB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDA8DF49457F5A6ADF1E9AB1FE39FABB93445AD41A496356C039B9213189E539C574135FAC2F2EC178612601844D6A39C1A529CDA535327DD7DG92A01084828BFB81592C303C7C9984C200D62BDFA4A1A1ADC200G0DF6C72B9133BB1B5991DA715BFB5F1BB7BBDA4D8AF0C2B9DC4D3C7B7E6E3D6FFB775DF7E7C951C75333B3EDC504E4E6937DDFE5A6A1338F92F2796A7077B8EF4509F4BBB17D5B89
	34097CD7F99696CE265B97A869768292BD39860AC5D0DEB414EE5F8A6F05C45BBE3782195673BC86F5AF4C4A78E462F91EBF4967492565161CFCF8EEGAA81DEB5501413B83FAF27C46777C1F9037E0E14E9A324779436B9F1274444CF535B6D073ABAA0D203ED6E64146A75B30107ED6641F361A95676DDF82E2552FD396A285E2E77719432721FDC3675B817E9FCCECDB52C2F2CBF9D645B5490F6B2756CF9F8D6F7DC8B1C74A9C117CF88051410A67A86DD7EFE51AFE90A6A9A11EE882A5F65972FCBBE51A587
	432A92D005E0D5D5EBDB87FB2D13D45127350941FED175C0F7D511BB0410A813A5D49F5F1DB5F4AE50874A03EE64AD3E03B6F4407BD1208EB75769076B2BF519132F251025CF6DCC28D32A1B6932509D5349799F6FE7CD2A45711F26EB047D8A205C8444E479F58E176565DAAECB608636DD8D65A2CF3A3D4243792E1B482F03F2AF106661B21E5843E56C397982D955FF5636B1010CC99EA65B9CCFCC46E9C5ABED5BA80ECE54A096D6C0DDD1DD3A5D89548AA483A581FDC0713B6A64DEF83A7BB50F928CAAA1F7
	B8DCAB7B55B645AF4A70DED585B5BA2FD374A92A1F90065141BB959672F01AD9B7961FC8CC377304E3AA3573B922E90D36EC8B5D663C315FB6D79FA32DAE26DBD6BD5A2D7DAE5AAD8378588F65E17CCB145FD49F633335FA0BE2814BFDD077F2BD37F34F86AECBEB568CD2F0204B16EBA14B546FB5591C8DCC061A0618AC4C46DBEB5146B27027GCD871A8B3488E8D58337F16D29C37732F1B36C89B5A448DD437DE1C155EA07A559AF2A3A2D1F4D194856D6447A3D78407D18DDD28E1957E849B7BBAD6D32A425
	52D654486CD15198338B1BFBE54239DB9B47E3E30331060B69DE69EBE4EBE8B7783F207C30094F56B6C879348CF54B00D87B99F4EFB5B545E364B94A6FEB0A474867B7B9C622D03784082D6F37BC68ABEB1AE10F82E9C0C9C029C08B0016B473B5FE74543E7B5CC79D2675E5FD9BEA3640D30CEA2A509C0AE8C248A7F68B2AA4744BE20468ED5AEBD0367EB17D3CF4FF7BE00EC6B1A42A02A6A9A198DD880AG2688EBF339E69336D1C5DA6B1502A2CC908C93BE2ED8D3087895A25A3930DF5044D88BD69F2CC1CC
	DC973AE1C498G577AB45A6BAE75392BC17F12535CD6291B1197067214535CAE6F56EC06E78B00DB142B2A6AA49F0AA128239C2367681E1D866D2BDB401EADFCEF3E5743B1D8377ED9D27146D9CB8CD2DF534A70B42FB59E03829DFB9970335A526DE5ED7CFCFD67CECF82EC627C66F2F50BB5D64D04320F5FFB64EE22B9F2634A597731AF6DF1E5E66F6DC5747CG3D7A00A635F11B6FF6BB9896EB05084851572E82F0C4FFEC85D8DBC7AD1EAD62F5B1245509E1B1640717083E76FBEA5746F0B9209818790C72
	51CFF9GB89103FF28F693634398E614737A6334DE50B4D56A9F5644AED15324D0A0565FC3E5090CF10191DD5752E254F5AA68B89BE83E21EBDAED2189DF2D0AF2EDB86C451D2363B4AF97B179BA344FB0567F2D9B98EF951C6165DF51F22CBEF2A3515A7DBE047345701174D06660B8E1F3245C643EB1D34799634FE1F18748A383A59BF6283A592337355FA7AE9F04589CDDF40EECFD0E783D707B924344CB1E4418FE45E30D69B40FB526FF64199833BF76D8E116553D65C1BD151110E98F7C52037E502704C2
	A25D08C6975DA66FF9D18FA9A4E967A5FFC0543A249BA27830B308652FD21FBB8F5E971E61FEF2E4B77713E5B7E61179D7ABAC632929DFEF31B95B997E2A5BE3FE1249747CE64403A80BC1B014C7998EE90C7F827567BABFE2273CA9079E473ECE375F5FA986156BA28D44AB04B1C59794EB07B58D4EC12A64D501BEBAC4F5C0D103225FB9A4DC97DC431AA43B6AF1155A7B07409A650E4FBEBB6938E750DFB1G954D217030FEE2B94B3B4B776BF8AAA4472EA45F07FCAAFD7FFF9571G136F2BFBDF24721D768C
	0A3EEB35CA94273F8EED5A608896558AA6064BD29AE1C4F3D1D43B184CD4307B95E948FA422B6E3819DA93CFF59C54B8287B6376AD0819DE40D298E8CABB7747078A311E7BE37D5071A8324C608EF5996D0865643BC5A6FCCEEFE7775C8DC67D7CFB54E7D856FF5E4165F09495603E94FFB7ACC2B80587C20BB82A47C4FB0AB64755F58B72F0AC565AD71487CF59F416B07E2EE26BFD62AC4673631AB1FEC5F18E4118090D497675EC81478C2BD2900EDEBC9C757B7A2EDC73FD3D8964473B7D94F8CE83E2187949
	C5A7BC0DA52E87FF92982D0F7A8605D0C0748CC7B4A5A82A2DD2C423AB6DBF0B633EF3135F716740B8E9C0AB74316B4EB2BB32F633EF71F6334F32F68B4EDA359B3D4D5B15F6021D013C1D2CDDF2271EB76844F64F9AF771FD866FE54A7BB5B87BA733789D232D401CDB70C76AAC7D96D2C517FE776A0A3FFBCD5E0A637681BF5C45577D9B4566F8409D09A8BE09063E7A9A1E5BE276E90CF7F14BBAF8421E0D40BD4CAFC6B98E7A36202C989B6BECFDAD1F797484999F6B5753EC868F37BCA3F44BF8958F0467CD
	GA50F61723BA5F104F0BB5CEEB067A366EDBCE64BCBE007493A7EB3CDF668033BBE4BBB6CA65C6E4B201E497997D5F86F344A3D7828602E4030C4DD9C846602A6D007E54E2BA451588947AA75721CC56FBA5C83DEA84BC01301587D2960D63E5EF69A70DE87E278AE4CB363BB7F9C0B43DA6159F70E63FB64D693BC7FD0AE1A697EF279E63626F1FE185BFDC6E10DAED35AF3B3C8755D6D16B916A99B4BEC51F34C5E534F456CBE731337EDF301186E7FEA681E81ED4A3A39DEB5376B476B75450EC6426F29B3EE
	7339FAE11EE4A0668F179FB17B4334EEE6671506ACEB6F9CC4D99E78F46118AF99EA00679F6690B1F2C1874DBEE07E522389FD401A734C76256763FD4060B64468D160371E67BEE07116CDA69F50897683C789DE1C6F6BBF5DEAE77EDAAFBF3AB51B7BEB426FD6DF5F0E766C03B1EF81A58325822599F394155CB327D39F1DA02763A48F121B49B84FE37447F3637B111E587D3328E77CFD1565BD9652FD6B6CE1F90FEC033F18722BCDFC06337EF59CE79DD0B727074F3F65D67C7CA597464E7979852B7BF05938
	FE2E38F2CFDCBF063BB6613EFD585810CF77E1ADF95CAF06AFF09FB679B06A16826579C00BAE707C457B9F609937824A7997E19F8355G7901A6814D3C48575ED7E2657F472EFDA2679F3B93B33B7C6C8B5AF379BD6D7972C52BFEA9373F58FC5F9E576F433872120BD66BF7327B833EFE6893222E48885C1F2A2A0C78D7DF2732CADCA77441ABAF7275B83E83F9BD17E05DAEF15B4FAF41FCBD5F5B9D0A9452885F5741ED98F7A92A9410C21C27D21ECFD169A50C711E5D1E78FEB8E73B757D703B5B2D6F077337
	CF1C73D830BD517D1165A7CE51F3BB8A7AA583B1DE8ABDF7D320BC4F603D3686F9CB213C6A925F0BEF2EC3DEE9EF3A7DE82F115B20BC0C63B0C653F3756B2CF28D58AF1EF75402F7AFE2F3FC49F20E783C954745034E31030E756A160D5456110898219B8C23963D6E9D2D18D85C2B4F88C111703BE15B2D35FA1BFACD719CB4E46F481B489777DA58C44FA59F21B969DE66BBB38D7E8B14FF4B44E7BE754F8E73EFC6D320AE2317E3BA3BB4B19E73CB2D7138399477C1C499D6FDE2BC6636151698F5B14F532C09
	C1B407FE17217DB5C18D0863B0DBCE7B333AC47D8F16A642B53375165BE6DB9FB078BF9B43BFE42CC1F9A562276372D8DB7B219C3DBCB1A61320FE3A294D9C7DFD010947E42E5901360F9A65BADA9EA55CD7096E8D065E75A13F6EDC589D261B6E53BF060667B339AEAF6522EF1105D1E5985A6D3F026D5E9CF31FDB82B2E4813F648A13054D737C88467883F0158313F505C59F493F0AFDFF3E06770DC27BA420D420B4200CABBC0E8BED407A3823CE1767328F3FC9A748AC687EA610477D0877AA778FF30E90E3
	9D7E5E747E58610711239C6BB7E6BD54B9184EAD74EC19897A4E3B4A7D6975AA64AD0072A20337187216C1B92B0F733EDF0C3C92A8BB8D5ECB3B11D78DE52F415BD4466DBAE8707EAA0A3C39D0DE54476FE4877AB046B04EC30C112B2A9AD549DFAB84DAA91258F9381A1E81084F0CBE9E376CDD037E23CCC837D783F9851E830CFE426F10295F18C1DAA7F5DAFE53DE6ABDE6138506434704D89CBD5B38C38E2C65F326C1FDD6BF4D532CB55FAB90E75F3131D803553FB2269EFD827A63D87D2AB175A82F5BEE2E
	5F3B4EDC0FB67AD86F4FF4FB6DE32E5B6A57E69379DBB5893F573776B31D427A3320DD4E4C7F495B36A5F41EBBF9F5936004673B6DBA25C01FF9C045FAF9E1BF7365BB895FEB7D153807B5C5113DD2588B7E19147970379D9574379E2C4D23D5189752201255756E09557D268A750698B62C04701A27CA7017A6AAD2FDA8EF7EF1DC0BC1F857FCFC6F7F14661986A07EE5EDD6D2EC2E007755C61BB7F7E3AEC31025C088B39C3039D85B77A8E6AB7C905382B11BB84B6EE593B305219FD231EE7F49701EEB0BFDAF
	A8A4FF97455C4BA084E0B7C0C6C1F6F3A9F49952229CDF0F79396F49DC73205F2859997B57F4AF4C77471F3DCFD3FE1ABF7E6C5D3F061F3D0B202EDA64B166A9A77A5C69D01E8774BAD086D07EG6C7781FEBE3F3A83734B93467BDEA55C8A8738EC8A8119FE5756FE31EF707F104BF1BF673BFFCB66BEC29266052A87985EFB75E7CA1B1C09CF265F0F4AD13FA428CB81CA83DA86348A28A040755B74E185538FD3D8EECD93FC03B4BB46154304D50C0D8756C7349F21E7A27B376214950F615E0D436BFA3D154F
	75AAFD66A9D277AF4436BB01DEE581260F9608792A0E5F3E1F45467941A5F36E2060AF1D364C8463246A7D671A46E1F2E5231B51317B1D62CF61E924ADBC969B5355DA5F603D6AED3F5A595C5864557519BEE21663683A1389F398997A7CE410BDEFCD12F5B9FEF3A8A647B7F7230F8909A38EC3168658CC4EDDEED59264DD950E83707FA06603465B1B0F11DD3653D29726003B353971CCDB7D99EF02B118CC4507E3633D9D65636957B7D7977E055090FF2C20C5DC9E982D3E1370B3FDA69DCE7F6D0A697D481A
	079FCF34BF54B958FEE9F2A60E575B1D56717AB927757D7112F3626F0BBEE7626F0BC3CE7377C5365FFFF1984F152300B1AFD094A889E87A2071FBAF67AEC2773A45BE2F7787C43E28FC7E456509F57DF9793DFF37722BF2BC0F7A95CDD302B8BE677FFBB92E172604A91371FEDB0EB6026B7B20461A6A7C556588A2D99C20EC262727924F2EF92057A22055C0C59278EC209AA0AF50A050AD20E420D42079C02F833D8B348AA8FFA85D6E9862F6F9248277D437B0B4BE1B8FCCBDC02346D18516F3E0F24C426D4F
	3FA1D7FC1CE88C306EE958BFAC1B6B9582C67FA70E21EE1F039C9D06AC27DD1FCEA0CB072A04C5D59BE5DC7A65CA1FFF796144EB3572703D576AFF8E53EFA3E0E36FE8D83F67590E501C4A40C0C4B4B011FB244060B5C7BA1408046E11551D2FCCAC43154AFB4B90283446CB30F2BCDE54CAEB3CC4AB4DF8E13C5B1538D782300EBDC63BBFB2F8978C5E1F50B111F7DE72EB03867FD1AA97721BC41C10731F20F20DD0DE0F4E1BEA62DD50F9CFD6229DC1E5880FF85F9915B41E13E4D97FF6C55BC8A1B02BE10FE7
	280CD2D8B049BD5B6045641EFAEC03590FB0D868F5FFF32C08305F944091A25D2007D997B48C8558EF186F6ED9860224AEA75991156C41B42B549FF48DDF8F3A04FE4D053F52F5F9D5A19411858D570AF5A56CDF260DE4DFA4058312A87B4F40322B3C2558CCB6D1EE2748B2910829D632F58C2FF6B86417C54CAAE8E4B37DE830CF96C201FDEC0A632D24E0CC7396F174F7648E49EC55A764FC561CD6C543D42CF0D8A39A3D88BBB445A14832B26250862508C37FF1B1F27BC5D0D5F4880EA03DA073AA1743312E
	EB38DFD17DD28854606C0843A704E08CB1849D9D6042A136957DB80EF9DC17C60A5CF4B676895E21FF038743EEB48E64E63A42CB2F9F2A323F7D145DC6321AC98EC3FCB349C258B613EC0A6AE6E28744D31590D190EC93C4CC960C36E342D06515FFFB3A7EEB53FCE9B61259CC7214D8859DAB877A2F9F93DE7C63578E7FB26D275D91BB49E9A65B700651A52A94AC229F13EC7A2F7F9A485AB0A6C570C7A9A0EA04BE32A35A288CABDBB45009DF544114FEB707E1C4A345ECD9B1C53F2FC9088CD288414AE605E0
	55E3CB16692E218BC723F4102FB0769D4C68D57862460FF71EDEB657CEEC8DE41D2C887E864187BE30192C5586D5B1B228487EE878949BE84D88BA01BC5DDC9F986A22F2AE3C3099C2DE0BBE19347E5D758DE05E28CA765116F8E9F399AD7C1EC1C10529C435D9435FD9AAEA19D177258F35CF7E39F1EB2F8DE1E9D6CAA3B94C8598EB0F50F6B5005C5A0F173FBAE3F1750A8C6854C0326917A7183AA0566A4CB0226711542F6C48CBDE7A67A8D998550A60589BB45097F799FEEC03419DF13A71AAC3C457932F6E
	7F4A2C7F3EC8C50CF55548C7ECED206B3E002884773987862410E88CF0BC9A9E03B4EC630AEB33B97AF87169B77E379F10D3C76C417D7B695F83746FC17CFB00F28ED04E010330BCAB48047F626B23DCE78AB7DA580CD8616C1C81A1A8492370D28700D582D1646FE43D78159EB78E3E6731183D7C2EB9E6DF75650F92466C79E17D4E900E5D9D58393DE2AF422B977851B0BF37EB76FEC6921F7FDDC1F0C3F0AB3E668E79B196289D5544D81C771FFBF17C88EF23D76A714FDE14FB4854677FGD0CB87883FFC77
	33D793GG58B2GGD0CB818294G94G88G88G0EE07DB03FFC7733D793GG58B2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1193GGGG
**end of data**/
}

/**
 * Return the JCheckBoxActivateMaster property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxActivateMaster() {
	if (ivjJCheckBoxActivateMaster == null) {
		try {
			ivjJCheckBoxActivateMaster = new javax.swing.JCheckBox();
			ivjJCheckBoxActivateMaster.setName("JCheckBoxActivateMaster");
			ivjJCheckBoxActivateMaster.setToolTipText("Check to allow this program to become a master program.  \nSubordinate programs can then be assigned to this program.");
			ivjJCheckBoxActivateMaster.setText("Allow Member Control");
			ivjJCheckBoxActivateMaster.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			ivjJCheckBoxActivateMaster.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJCheckBoxActivateMaster.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJCheckBoxActivateMaster.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxActivateMaster;
}

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramDirect program = (com.cannontech.database.data.device.lm.LMProgramDirect)o;
	if(getJCheckBoxActivateMaster().isSelected())
	{
		program.getPAOExclusionVector().removeAllElements();
	
		for( int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++ )
		{
			PAOExclusion subordinateProg = new PAOExclusion();

			subordinateProg.setPaoID( program.getPAObjectID() );	
			subordinateProg.setExcludedPaoID( new Integer(
					((LiteYukonPAObject)getAddRemovePanel().rightListGetModel().getElementAt(i)).getLiteID() ) );
		
			program.getPAOExclusionVector().addElement( subordinateProg );
		}
	}
	
	return o;
	
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
	getAddRemovePanel().addAddRemovePanelListener(ivjEventHandler);
	getJCheckBoxActivateMaster().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramEnergyExchangeCustomerListPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsAddRemovePanel.gridx = 1; constraintsAddRemovePanel.gridy = 2;
		constraintsAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddRemovePanel.weightx = 1.0;
		constraintsAddRemovePanel.weighty = 1.0;
		constraintsAddRemovePanel.ipady = 16;
		constraintsAddRemovePanel.insets = new java.awt.Insets(4, 2, 7, 4);
		add(getAddRemovePanel(), constraintsAddRemovePanel);

		java.awt.GridBagConstraints constraintsJCheckBoxActivateMaster = new java.awt.GridBagConstraints();
		constraintsJCheckBoxActivateMaster.gridx = 1; constraintsJCheckBoxActivateMaster.gridy = 1;
		constraintsJCheckBoxActivateMaster.ipadx = 259;
		constraintsJCheckBoxActivateMaster.insets = new java.awt.Insets(11, 2, 3, 5);
		add(getJCheckBoxActivateMaster(), constraintsJCheckBoxActivateMaster);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	initializeAddPanel();
	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (6/21/2001 4:56:13 PM)
 */
private void initializeAddPanel()
{
	getAddRemovePanel().setMode( com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE );

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List availableSubs = cache.getAllLMPrograms();
		Vector lmSubordinates = new Vector();

		for( int i = 0; i < availableSubs.size(); i++ )
		{ 
			Integer theID = new Integer(((LiteYukonPAObject)availableSubs.get(i)).getLiteID());
			//makes sure it is a direct program and it is not already a master
			if(DeviceTypesFuncs.isLMProgramDirect(((LiteYukonPAObject)availableSubs.get(i)).getType()) 
					&& !(PAOExclusion.isMasterProgram(theID)))
				lmSubordinates.addElement( availableSubs.get(i) );
		}

		getAddRemovePanel().leftListSetListData(lmSubordinates);
	}
}
/**
 * Comment
 */
public void jCheckBoxActivateMaster_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	getAddRemovePanel().setAddRemoveEnabled(getJCheckBoxActivateMaster().isSelected());

	return;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
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
		LMProgramDirectMemberControlPanel aLMProgramDirectMemberControlPanel;
		aLMProgramDirectMemberControlPanel = new LMProgramDirectMemberControlPanel();
		frame.setContentPane(aLMProgramDirectMemberControlPanel);
		frame.setSize(aLMProgramDirectMemberControlPanel.getSize());
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
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramDirect program = (com.cannontech.database.data.device.lm.LMProgramDirect)o;
	
	if(program.getPAOExclusionVector().size() > 0)
	{
		getJCheckBoxActivateMaster().setSelected(true);
		getAddRemovePanel().setAddRemoveEnabled(true);
	}
	
	//init storage that will contain exclusion (member control) information
	java.util.Vector allSubordinates = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );
	
	for( int i = 0; i < getAddRemovePanel().leftListGetModel().getSize(); i++ )
	{
		//make sure this program itself isn't showing up as an available subordinate
		if(program.getPAObjectID().intValue() != ((LiteYukonPAObject)getAddRemovePanel().leftListGetModel().getElementAt(i)).getLiteID())
			allSubordinates.add( getAddRemovePanel().leftListGetModel().getElementAt(i) );
	}
	
	Vector assignedSubordinates = new Vector( getAddRemovePanel().leftListGetModel().getSize() );

	for( int j = 0; j < program.getPAOExclusionVector().size(); j++ )
	{
		PAOExclusion subordinateProg = (PAOExclusion)program.getPAOExclusionVector().elementAt(j);
	
		for( int x = 0; x < allSubordinates.size(); x++ )
		{
			if( ((LiteYukonPAObject)allSubordinates.get(x)).getLiteID() ==
				subordinateProg.getExcludedPaoID().intValue())
			{
				assignedSubordinates.add( allSubordinates.get(x) );
				allSubordinates.removeElementAt(x);				
				break;
			}
		
		}		
	}

	getAddRemovePanel().leftListSetListData( allSubordinates );
	getAddRemovePanel().rightListSetListData( assignedSubordinates );
}

}
