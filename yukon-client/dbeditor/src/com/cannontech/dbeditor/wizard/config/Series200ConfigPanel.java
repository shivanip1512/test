package com.cannontech.dbeditor.wizard.config;

import java.awt.Dimension;
import com.cannontech.database.data.config.ConfigTwoWay;

/**
 * This type was created in VisualAge.
 */

public class Series200ConfigPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JRadioButton ivjKY2WireButton = null;
	private javax.swing.JRadioButton ivjKYZ3WireButton = null;
	private javax.swing.JLabel ivjMpLabel = null;
	private javax.swing.JPanel ivjMultiplierPanel = null;
	private javax.swing.JLabel ivjTimesLabel = null;
	private javax.swing.JLabel ivjKeLabel = null;
	private javax.swing.JTextField ivjKeTextField = null;
	private javax.swing.JLabel ivjKhLabel = null;
	private javax.swing.JTextField ivjKhTextField = null;
	private javax.swing.JTextField ivjMpTextField = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	javax.swing.ButtonGroup channel1ButtonGroup = new javax.swing.ButtonGroup();

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == Series200ConfigPanel.this.getKYZ3WireButton()) 
				connEtoC2(e);
			if (e.getSource() == Series200ConfigPanel.this.getKY2WireButton()) 
				connEtoC3(e);
			if (e.getSource() == Series200ConfigPanel.this.getRecalculateJButton()) 
				connEtoC5(e);
			if (e.getSource() == Series200ConfigPanel.this.getEqualsJButton1()) 
				connEtoC6(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == Series200ConfigPanel.this.getNameTextField()) 
				connEtoC1(e);
			if (e.getSource() == Series200ConfigPanel.this.getKeTextField()) 
				connEtoC4(e);
		};
	};
	private javax.swing.JButton ivjEqualsJButton1 = null;
	private javax.swing.JButton ivjRecalculateJButton = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public Series200ConfigPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series200ConfigPanel.nameTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.nameTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (KYZ3WireButton.action.actionPerformed(java.awt.event.ActionEvent) --> Series200ConfigPanel.kYZ3WireButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.kYZ3WireButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (KY2WireButton.action.actionPerformed(java.awt.event.ActionEvent) --> Series200ConfigPanel.kY2WireButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.kY2WireButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (KeTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series200ConfigPanel.keTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.keTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (RecalculateJButton.action.actionPerformed(java.awt.event.ActionEvent) --> Series200ConfigPanel.recalculateJButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.recalculateJButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (EqualsJButton1.action.actionPerformed(java.awt.event.ActionEvent) --> Series200ConfigPanel.equalsJButton1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.equalsJButton1_ActionPerformed(arg1);
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
public void equalsJButton1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	Double mpValue = new Double(getMpTextField().getText());
	Double khValue = new Double(getKhTextField().getText());
	getKeTextField().setText(new Double(mpValue.doubleValue() * khValue.doubleValue()).toString());
	getMpTextField().setVisible(false);
	getMpLabel().setVisible(false);
	getKhLabel().setVisible(false);
	getKhTextField().setVisible(false);
	getTimesLabel().setVisible(false);
	getEqualsJButton1().setVisible(false);
	getRecalculateJButton().setVisible(true);
	fireInputUpdate();
return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G30F4F9B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8D455312922454AFB6AEBE8D3BE7BCA1567234F5887AD36BC0D9AA97AC0C0D002022006B6CAD450D4A924B52DB71B84828493A0C8C454288121041010244598057C9072AB11043071054FECF6EFF6EF586CAE3BB7E4C379F9B3676F5E5D3D1BGD6BFA7771EB9F3664C19B3E74E1CB9F711F25F9F1F11EA4A1524548C097D7777D4C91A5AA0C9DD0FE67C016346CFB51BA45DFF9FGEC1256CB69
	701C84F591291D269CE9F2DFA61427C339B845EC5A866FDB24DF75F55740CB18FE8EC25D5D038FCF993E1F45F53C1F35243C2BA08B1EEF824CG5CF200037FE3067EBFAF48E3789CA83F495E91D201EBB22119674A53614FB33A59700C87C83C87E9BA8BF6307A43D0B683CCG18FA8F251D874FF5C43A6BAA8EB23A2EDBA6CAAB9FF94734917B527167D0FCAFED5B4E1E79526DC491E95234CB55702CECBDED7F5C6A6EAF32DADCAE37CB152D0EA2DB0FECD3D43737E8D0B9E7715A0AEC72D945AAD7D454126797
	16FE394AE6734ABEDF2B45A5BBC16F77226C2BDC19042FA40D01F2EB8C62DCEE54D7A63C67839C0C6172BF3631124997F9FA2294FF7B1BE1650F0C21F2CF0D51640F0F3ABE43F19F723F064C8736CB067AE2405131FD54F878B247E6F53BFA95FBC5C51B6CD5E45F0162629A02A0A32363FA24000FAB7ABEBE2EE79D58EF8214D3GF26F6778BB944497C23996206BFEBE5E4FBE6663B57F6357D2F24F67266CB063D56F27633C75FEED3CD10963563CCD6C4FDE0FB634906AD2GF29EB01B4AG5A81D400F7085D
	FFF8742F702C6AD1EB5C7D7DEED71547D36D34F9DB5CB659896F9595D043F047E52B5BEB13A4EA5B2F3B8A8D6461B06601C03BC620E3BBA9F1DB0CBE7EF3C9D54B489C840FED4A02F7CD63990FA98FE8E3CBFEG7576BC3113B8F847F6E88B947F87022F0C5570F45ED50247F2B7544D0E65FAB63F49E5E9CE3FCD4AA9B9CC56ED30ACD1534ACD879F24B234BE2849C2F5DC5508BABE8F78A800C9GB3G9281B227F19DBB0F160E24638630B72F4B62EC9B6871D83CEA7500623449DE266B518543695A88E83B9F
	DCF1BB2A1709257AB9DA724BC306FAD9B23140D4B00D6A23F21A26975A775C30FDF7CD8B350DB7459C3611B56119C667B0CD60CF91FC048ECF6776B9323630FCB3542D8420746F75A15E7C1BE09B59C77067FF93ECA3518A371171D017B55DEC22737BEC937AD8BF14A38122812683A4GC88F41DAFE084F712F0F6E3F4CF5542A1BDF5A56D37F96BCE53F6A35B438FC2A45E515BBACDE45526314FD9223992AC73D7684343BC85637957AB8AA3BE42FC5D55CAE608E9E970C49C7E93EDB3F89E93CB229EDD77AE5
	68205FA3F13EB755EF01275362D3CFF8EC96D556A8E87D8F6B51A64EDABA00A3B000F75BC3282F4DEE74F3E5705E7A9057D5D68B62A6C2B9D668E5F343E6F8B601F14BCE589394AB0AE1718EF19BDD6E46B144817D79074D26027F0575836018E1B6654D646B74E5B337475A0DF7C9058BBEB734C70458195436964E8C364757C9BF52A33037G9C86688608G987F885F2F4F393B6119475A228C6156894AFAB96B894702E343F79C1B36B647123EB6B23A8D4370483802351BA6E96B9F1FF4DE4EFD01369D8B63
	DC821072881F1B1606FCEA3355961F4C2D7418978CCC36E9B3C5E99B9BF18F124F4AAE35D67648AE9B3C78180DFC5938CE335FDE37302D13840F7E2C868C4CA770279AB7D1BC70502F86DEAF13FA0B2AFA151E81D5EE13D5D5F15935767DC496DF002B7431311EBF06E34D1DE5B69582D44E62E3DD5538C5E7074DEE776981CFBB2EB0E64F4F13FD3B95689D3338BDF4AA984F0D91E5ABA9EB75AFB94CC1F30034D762EFC7766169CAF09FE97FA47EDA9F85FDF90CEF863319354C1EB6881AD2C2938D630B83C881
	A818EDB6B583283339BEFE21F46BE44C886293CE06372FCA66B9A1FD71FEB60E5076CA7A223673C2D3F85BCFEAB2367DD5CD46361F52B43CED27B79B59B62D5B500C63F48F3AF4EBA527997DAB4425AE19ACD851C422316D4B6DB88E452528A7951BDDD65B14F3B2446033516677909FFEAB3CCF1A4DFD6D60C75C57961C7B19947B755B0671D954FF9CB4E54E21363EE30E66EB29CC1BDAF08D4ACE399F94D5639EF029943F0D6C8F8C6FCBA3383BF3EF45363B1BBA3FAABD29F8656A81D5F53B8AADE4B0C57451
	AAFBFB5D5EFE59363B4FF256D2E499D40B48E494D5116AA3783EA7FF5F3E4F730D62724253812C3F2E8A62599C1E6927FBBA7614D0B92F7BE89C13734037A666012B1772GDD87DB24676CBFA0F262392B9D4C32CE111D36C2AB78DA35087C255BBC9151DF649B8463E39CEB30F6A4A1DDFA36DF576898B68F4711A8D248B87650ADD4175F0604272FD63ED2094B1729CCA07241094762348EB821F96337B0615E9076D7BA6BE5D45EBA152E21A3E786ACCE9F63D572CF15D58EE0FD25F216533D791ED6F2BE83FF
	518A60184377666F37E0BD5F1BD9GD263F6BA29CB033A59F350DF15F8F2F5BEA8E28E4D293CA96ACF0DD0FF438875F70FD03FF4047A0DE7425553F1C6FC11033E9DF4A9C3080F41C713BC446374AFB68755F5D81C83DA7CFF53973837B9F5F18B45DFF34C58570EBE06314AE941FF4C31B592467114A75D9BA6D91027472B74C3180701984BBD7D39C01FFB4AFD94F000EF072783005A5C0CE3B8D6EAAB258136D2BB97F9A8FDBCB79581ED22G26B2BE490FD21DD03AFD27B95D4DD01EB8D7231BB1D7CF37D450
	654CB31BF683544F23F423666969A6B8B95DEC40A76868529F5353197BB9DDBD603B81A29E23F43181F4BBDC225F47215F47B53A7A47A9DD6463C8F7584D73BE7B856EA441AD0620633AF4FE36EE49516762EC3E92437D95E1A1633F7C71601C43ABDFA25F2C79E6530E79FC8D2DE9554737D5BE1F5B2A10A39F331358D67D5ECBF18F352E07E72F6275298D905478392D4DEAC5D971CC48504CDE6E5C7B1994FA46BDC2B2E47C78D0B3C8D6FD3B3B860E2575E02EB5A2B469D064C1096B61C21DBE2797133D4B34
	A10C9E0A67335C4BFCCD8FDD096356D019FED401FE5BA8F7E7A5C2965987142281D5414327C505984DB32052C4A44D5759F7E632715DBC1F67657E3E9B57548CA84783A483509C413F115C81526644417C8350F59FF9A1E05D83BE9AE0DE9CDFABFF6C032DCF3AF2F74C75BE3D83757E0D429821775B369C666B636737C91517569A6696C7E7EFB2A544D1FDE7AD50749EFD638DEBE28128BEBACEF3FD1801666685DC9F7B1A0C74B1E3814F377EEAEFA5994F5547BB347F75CE5E7F92601D7B846F7FC4293E7F66
	A76819CEFD024F4799A72E51AB0CB858DC14ED2B00675507C1BC5FB4C748BE9A640A9532573802E5C75DA5BF41F5774B1D8709DF3F3A5EE95F5F6967FDE7BDE9B655BE497BCEB26B7BBE7FA46DFB4213DCEF097D5F4C0EFF37258A1E5FB86C11388E0F3878B826020CC9E29C1B6CC663583D106BF0D6A72E27EF94520875F472005E1F47466F8C6B4FAB97B2BF36B0581F4FEFC39F6484FC44C26E4F6B3E5C24736790092A96179DF6FD6E23D7FE19C66CE00815133F44FE497EAE713CCF4E8954C3A4701C84B083
	A09EC0DA447B38F4FC443C74917FB0F9E9674E609CEF7EA2AD7FD136A8B497C2F330F7927FDA3F0866E0578A7C2F893EDB0727737C9941E3598FF5CB9671FCC3D4DFF07F071F8A6C33AB28CC5B5D91526E3D20F2C4D0BBEAB7EFD5A39D59EBF46B2C2222713845263829A1717B3B3B2A74B1D454D354874540F39EG5DFBE6DD48E4B2A5BE45F3283D9D98EF64AFB61BAA815A81BCG11G13G62967339FB743851DE9CB8F761B6E2AD5FC67551F57A6A74589752AED80FAB978736236336B571BDB7EB891F4B9C
	67554970DEC83BE099EA17984D654E5DBB0C67D2B8D6AA6B18D2BEC7DDC25679D7A96B5291E50DDCE2242FF3622EB4DA48705D7E2B13E1ECC83BE099928C75155D7636313E3859B3D9BF6E647ACA9132B6055C95DE1E2C1DA16D02E5ADFB5AC8DF75F6AEC37353FCDD5DF192F123209C79B4DFC3FB5BB0D748FDEC2BDBF12992772F8B5B703C6476AAF64545F1CF931C556DA589B80A4BBB91BEB7787E89635C604E934639413DA7064F0B171E8817BB24B96CEBC9ACBB8146B79500626E580538A5D0DE79B41F0F
	49441F66BDE3B69583B4BF437177913C6399FA3E42BD1362EBF687672031DD30EF57C35E8875884D4F686FC33AC27A98695E63F27260BC5F7BAE211F79D6B6512F4FA77B08F34493812BDB26EE158C722818C613F8DEE8EE5FBAC6F3C4F5579C90EB6335BDF946EBC377DD40F3D55C670DFE061FB3EEBCCF6EE5203C90A0C94C433C8BE877194F866AF087AB97BF9B2ADBF60F58915E9687BB0CED71C2872E811FFB40EB15036DEDD4C71E2853F60F86D56EC735D01AFFA16DD50B57AE0758EBA4E9CF6B42357FF1
	C7B81B26BA7F7EE93D4ECB053DA8CDC6774B35CCB7E6839DD1FEC71DFAFEA5025FAD25FAFE4E207691C16548E7F9CC3C3EDF4F2FCC703BB46C779193434AF7FFG3FF2412F305308DF8C63B38F1E89C1BCA93FEBDCFAFEFF937CDA6CC67C12990F2CF8962346EBBC69FC5DF992E3B33FA87FB7A98F097E466DC49F69348C398740190E0EC7BB1F3CD39F8B15812E8B4043F853B5B4F9904F7F3DDECBBFCCFF1BC736CA362558763EDD3C6DBC20CFGC887FC81C06DD2BE87BFAA42B122CDB5DBFA7093A3A64B6422
	0D8CAFB6F5D177FBF26EED99F02A0A472948DE1A93E067205D7A73C374D29A3B4DDB4A57670BD5413ACBD82A69A9F9E9685C5278765085DC6FD93F25716DB601EFA378DA9D1E463D37907F006573D0F770F75C27AFA3F163CAA8E7A6F0DC71C5446D00F221400D3D48FD7FC1010BE862F42385EEE993270B943809A46EC90072CA01DB544269B21F633863442F19216C94389F38B8DD0440551038E09E146385EEE2A727DBAEF057BA90D779BC784867B94EC676F39B14D5011BE997FD885C774C248FA82FFC1E1F
	75CA3A8362BCBC50D5D49C75AA36EA0B3D19D8A9754FCF123D93751F390C7B670CDD287F43D0B683F81671BBB27FAE1E3B0A3E63B629B962106177E8716D3BCC2317D1DB08DE264B21A40EDBC37B2C7A1877198875790908F3FF2C37BD3455D9A6C9AA4236247E27817568BBBF12747573836AD15E7B825AE78458B66A68DD561E0EED56CEBE3614D913A4675D7B42FEEB67C824E39A431E75F336CB39555FE3F93957FA436C09E40932D8F18F0F5326A772B9CC2DC6DC8A4A7A824795547039AEFB012F41A3E40F
	9C856571GB1GF1G4B8152DFB41B72G0A81EA814CG6A0B62CE1F3443FCB4661AA381BF65C51AA70E0367CA005C17201F17E82E977342345D8F4FCF8A9ABF76097CC70AC1742086512733B24A9ECCDBBC826FBCDDBD0E9B47B9DE47E714505543A132A3FE4B95480D30AD284C6F59579950EE9A01D7366E9D7D7ED6A9B05ECB13381F7E8B71F5BE996F02E41B6DA534072D9779F9287DA53E879C3D08779DAA787909AF613739262F511A331613EF3BAE568EA38B6F0BFFBF339C5AA3ECE5F6AF810FBD6C1D72
	2E6856F8A7B78D47FB43883C5B0147E76C1D0E632BBDB80EDE37CB25E3FD0C7861EE20F38250BE13DA066BB3C3B7A604714B35BE31EE8A14AF246A7BD42A509E2CEE67C03F4BA751FEA308DF4733D32C6877AE57F07DEE8A6AFBDED03F89D0BE95B8564E40B9BBE551747AC067C8F31641EC50C82FC940E3F1009D0DF360984FAABE05FC73C346B859417DEF4AF2EEC77F30874A9451234994EF9FCE260DC1638F16A9E7857D56FC759BEF3CC17B7A29993F7BF340A1739C4C374509D1CA2B5B2750CBCFAAE30C19
	1F9BF62C606BF9C9C85EEDA420F14690594F6AD750B8A3D5602FA178F69D1E469F9BF6716F253B21EE7A8AFE1636DA51376FFE997C9FC097008A9089B069E5FE36AEB397CBA364275A5D1EE6B82CB8F5C7DDBA3E3A0F2F6E3B558757723DEB4ABD7FA5CD3DFEC5EA38BB2569AFD3BF19401E71333693BC9D5FAA9B0EAF779558C3G5A811CG91GD12F70711DB79752716175D7152ADA2C8EF23346870717DD9A9A83DA96476F510FAD7A955AFFACFB463D926EDC10D79D9AD76F2F8E0E2B9321FD8A63C1ED7653
	1A4BD9C75CEEF313E05FCB22B60B654AA44DA6289CDFEE41F601DF6B703A3EADA41F223F57916D6A9CB897AD9EAD922778089E44B7B902712F1EA1F8B9987F6F4E35048F0D74A96EE797719E13FC394A8EG94EFB22FA53C75342DA584A76BF1F90DB826402FA7B80FD32771DEBE74CB90FE67BF7F3C51FDCDFD923F0B70B49955F7097A72CF024F9AD86F9175B15B0C6AC7097A8F4A0D6A23C47D1CB2237A68A46E6F8A693E699D10699A9ABF283F4FB838FEEF587B0C58A4E6336CB965AF5B59BA1C7BFFFA9EB9
	63368A9B8A661175FB5AB66F77DAFC7C3EC4E313CCC97B0F7A629F5B711D7631B40B2F759DF75EAE55BE39A27523B0FDB4B35E376A7A483C757A8C4AF32D22717F9C4D154D7B6C161D70F4490379629E288E1C746E8FFD96174FA7FB155E8F8B730B8B73CB4AB0B68815B9699065E9F1EBBCFFD805BF9A426F2042530F72E872ACBF23351D6140F968858B15C5FD36CF2B1FEFC61E7A6B3FB67CDB63A69F142B3E2266A3F56DBCB7318B5B315F96685EB76D8A4E417DB301767157EF358F76FB8BEB78BC184FEA1C
	879B329A67E4AFD9077FDE733B56705FEB7E2BD57F3DA67BBD018F57E39C58E292C06E2B60EF81AADF657B4A8E5B079259D78C7614A3B63B4CA714777F06AD7CD85748A37F2EA0DBC65F5963861F560F7CB9FE2B0C73253ABD84C9F15B090F70AAF607CAC999BEDFA67717F2AFC133737E9E1EBFB03FAA4EF1B6C287657382F7030D1F6DC63F4A6F45CED47338EC612B7CACFEEDB546A2B92F013EG8E83982D21706B4ABB00A54AFF3D38F7B33F467B7C4FA60E3BD9601C1F7058EB06403D360D6324159CE7AE67
	38038217AEF01E15BCEEFA7FAC79FD84522C6473BDD95E8B4F8ECBDF6857B400EC753ABD32D79D22D872C59833236D78A98D537FEAF9E7B89EE0B50D60930E75744179295DE297F6F8440FF1C18248A17D41ECAAG2884E885F0G1C873899E082C08CC09CC0A2C08AC04EAA33A99F208C20FE959F47EF6DFB069987CB38906375DA7477AC4BFC616D77E55F48763B5AC73EEB823BEB9F72301CF20A0F5C5D74767AE431CED2559C01EB7071A81B46589EE22BDDB09647AA3EAFFEEA8BBC87FCF08E75CDE32A7136
	4BB9CF06BB87C42D52E2AA6A933F2A3E3AF8FEB5D99393D6854773D28D62A72D8A0E67EF286671FC8C4E61EAFE1E483EE0547F2855F43F0C02E774EA237B39E8439CFCAC230D03E722E13BB31F9835CBE134B94921B1B0EDF77FF623F6850C7EE058F6274A0D5AB5B37A6E306D569936D3997DE803F654568A4E623B58B3683265F7FCE7517F6377780A45291CA31F683581214BCE6BBB7D6157C337FF647520780D7D395B9F6A4FD53F31BFBF67577BF3368F7A511759E19D1B855DA8016B94389B88CF441DD4EC
	2AC360EFA2F2A13EDE468EB9FEAC11EB10604C8CB7CE076BE4381F78F1CD431061384C5BCE7013DC0962F45272C4C22338E0098BFD5CC5E4D4BC961D5C3F90B8141B2E6FE99761151F87489EEC907B59E04E5CF6DD60C75BBEF7C992B67367EA6DFDCD0D76FED375379FDBCD7CD67B207B75AE32F75E8A369F1D4C77709BAF70BDFC2A4065127DB4730FE6530EBFF25CEF1AF85BE201ABE866EDAB85EEF9BFEFBBC660C6133DF8BC14A7895C0ACFF85B9801DB301D53E57D09637E768E274B93B8156C4F8EA87B85
	EED1B9279BA5F079651CDF1C40FDC750457F096F971FDD885CAFAAF0DBE77B45ED97C64A576973C6452F87664DEA21FCCC521F4177B661FC4BAEDC78B616A72AEE62B1D3AB5033FCDA21A643274D434930AEC88EDB108CFE1D8CAC876B627D0DFA1D6B60A71F84654E76E97D27D7DCCE9EA26C1E89FD706FD4A96F5B0BB45EAD6F8C47FB6D883CA7GEF1EA722633831BCF09C8F6F577ACAAA3F9CBD066BEB526B5ABB657DC23146BBEAD85E59A3700E815E7C2EA6FC9C1C051FAD30F1F6B6694F14615B6CAF555A
	943AC24F2147715A1B3D8F14EB6FD1FF535E6F7DBB3E53FFEB43FF218618C42F10B2D4A92BF49FE60B488F4076285206832208A5DD5EC515D2BF73F925EC61129B081F0FBFDBDA11763B5B53CCD2FA0334066EFB8DD2BAEEDE8DD286595B9A24B458777CF830B6B199F0AF5A6DBBA3DDA47FB20809E18E912CD75A9860769F7AE97973331F697025C9EB9A2437B1B30F3FFA404DDB366187A46C1751F552BA8FFEC104BF308381FC641161D3071CF233145BFB9CFF5961D3E5DB958F8BFC2A3415FCE43A9FBFCF5F
	DFEF71B9D4DC511FB6CB6978FD31AAED2655CE0B4B3E1F46881F12D124D6F51379C12B10C05EADC4DE64D2A438EC39B7FB67870D89D353A4D31D345E6936586AACD608489B24F52A43AB7B9CEE274D6FB9F418B04A9C44C9A71795406E9DB1FC9CEC91E6600450277DBF9E33B1F1E39D284C6F157693CADCA8C50242D66330F821AB595B2062EF525D5E82D1771D7754DDF30EEE6BB2C929EC10B8856B7C9ED5DA53EB69D71CC32A34CEA4D8487CEC848C44BBAE9FFE168B42658789472BC49FC55F1FD9BC76E7FF
	7A0A7421B5D5257729B62169FE3B6C6E5FDF555B2B38E441GD48B6C5F956C1126A808E6337F162D0D8B5E6881D32815527A0B0B495F927277G7EADA1189202A9B9G7ACD12067DAF385E4F47CC6CC718BBDAC0D19DCC227A540A1937AD2ECCDA8E232B13B248673EB08FB99A5597837DBD32777A68316FEE080C3F912749035662C73E1B7C426451D171E73640BE4696833A14B582BB17AD9A32D4C2E8CD8134294CC9617BFAD55AC2FF97B4C07FED02FC7C942302502E8DB1E344EE8835C7C4E703F950508BCB
	1B89E9AE793B6D0A950C2D5EA6ED5F91FA592569E50324DD5AED02A0157C1F566229G77E30637A6873C25B7499579810A60FB471AG6C7B2CC643AEG48E222E486E0539907D06C0690EC9A631B49F221FBAE69732D4957E5054D59CECF6D247FBE82FBEA5F20F79FC515671ABACD45GB4CE1ED4079BF8383CC2DB3F4569D4AD4E53D5AE9B66982A07D4D94B0B49C7113F0F5330CA7E6F21205CFD3AB67F8FD0CB87881AFE85CAF699GG60C9GGD0CB818294G94G88G88G30F4F9B01AFE85CAF699GG
	60C9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3099GGGG
**end of data**/
}
/**
 * Return the EqualsJButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getEqualsJButton1() {
	if (ivjEqualsJButton1 == null) {
		try {
			ivjEqualsJButton1 = new javax.swing.JButton();
			ivjEqualsJButton1.setName("EqualsJButton1");
			ivjEqualsJButton1.setText("=");
			ivjEqualsJButton1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEqualsJButton1;
}

/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeLabel() {
	if (ivjKeLabel == null) {
		try {
			ivjKeLabel = new javax.swing.JLabel();
			ivjKeLabel.setName("KeLabel");
			ivjKeLabel.setText("Ke: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeLabel;
}
/**
 * Return the JTextField12 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKeTextField() {
	if (ivjKeTextField == null) {
		try {
			ivjKeTextField = new javax.swing.JTextField();
			ivjKeTextField.setName("KeTextField");
			// user code begin {1}
			ivjKeTextField.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.000, 10.0, 3) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeTextField;
}
/**
 * Return the MpLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKhLabel() {
	if (ivjKhLabel == null) {
		try {
			ivjKhLabel = new javax.swing.JLabel();
			ivjKhLabel.setName("KhLabel");
			ivjKhLabel.setText("Kh:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhLabel;
}
/**
 * Return the JTextField11 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKhTextField() {
	if (ivjKhTextField == null) {
		try {
			ivjKhTextField = new javax.swing.JTextField();
			ivjKhTextField.setName("KhTextField");
			ivjKhTextField.setText("");
			// user code begin {1}
			ivjKhTextField.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.000, 10.0, 3) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhTextField;
}
/**
 * Return the KY2WireButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKY2WireButton() {
	if (ivjKY2WireButton == null) {
		try {
			ivjKY2WireButton = new javax.swing.JRadioButton();
			ivjKY2WireButton.setName("KY2WireButton");
			ivjKY2WireButton.setText("2-Wire (KY)");
			// user code begin {1}
			ivjKY2WireButton.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKY2WireButton;
}
/**
 * Return the KYZ3WireButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKYZ3WireButton() {
	if (ivjKYZ3WireButton == null) {
		try {
			ivjKYZ3WireButton = new javax.swing.JRadioButton();
			ivjKYZ3WireButton.setName("KYZ3WireButton");
			ivjKYZ3WireButton.setSelected(true);
			ivjKYZ3WireButton.setText("3-Wire (KYZ)");
			// user code begin {1}
			ivjKYZ3WireButton.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKYZ3WireButton;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the MpLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMpLabel() {
	if (ivjMpLabel == null) {
		try {
			ivjMpLabel = new javax.swing.JLabel();
			ivjMpLabel.setName("MpLabel");
			ivjMpLabel.setText("Mp:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMpTextField() {
	if (ivjMpTextField == null) {
		try {
			ivjMpTextField = new javax.swing.JTextField();
			ivjMpTextField.setName("MpTextField");
			ivjMpTextField.setText("");
			// user code begin {1}
			ivjMpTextField.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.000, 10.0, 3) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpTextField;
}
/**
 * Return the MultiplierPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMultiplierPanel() {
	if (ivjMultiplierPanel == null) {
		try {
			ivjMultiplierPanel = new javax.swing.JPanel();
			ivjMultiplierPanel.setName("MultiplierPanel");
			ivjMultiplierPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsKYZ3WireButton = new java.awt.GridBagConstraints();
			constraintsKYZ3WireButton.gridx = 1; constraintsKYZ3WireButton.gridy = 1;
			constraintsKYZ3WireButton.gridwidth = 4;
			constraintsKYZ3WireButton.ipadx = 20;
			constraintsKYZ3WireButton.insets = new java.awt.Insets(40, 23, 2, 10);
			getMultiplierPanel().add(getKYZ3WireButton(), constraintsKYZ3WireButton);

			java.awt.GridBagConstraints constraintsKY2WireButton = new java.awt.GridBagConstraints();
			constraintsKY2WireButton.gridx = 5; constraintsKY2WireButton.gridy = 1;
			constraintsKY2WireButton.gridwidth = 4;
			constraintsKY2WireButton.ipadx = 30;
			constraintsKY2WireButton.insets = new java.awt.Insets(40, 10, 2, 63);
			getMultiplierPanel().add(getKY2WireButton(), constraintsKY2WireButton);

			java.awt.GridBagConstraints constraintsMpTextField = new java.awt.GridBagConstraints();
			constraintsMpTextField.gridx = 2; constraintsMpTextField.gridy = 2;
			constraintsMpTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMpTextField.weightx = 1.0;
			constraintsMpTextField.ipadx = 39;
			constraintsMpTextField.insets = new java.awt.Insets(6, 1, 209, 3);
			getMultiplierPanel().add(getMpTextField(), constraintsMpTextField);

			java.awt.GridBagConstraints constraintsKhTextField = new java.awt.GridBagConstraints();
			constraintsKhTextField.gridx = 4; constraintsKhTextField.gridy = 2;
			constraintsKhTextField.gridwidth = 2;
			constraintsKhTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKhTextField.weightx = 1.0;
			constraintsKhTextField.ipadx = 39;
			constraintsKhTextField.insets = new java.awt.Insets(6, 31, 209, 1);
			getMultiplierPanel().add(getKhTextField(), constraintsKhTextField);

			java.awt.GridBagConstraints constraintsKeTextField = new java.awt.GridBagConstraints();
			constraintsKeTextField.gridx = 8; constraintsKeTextField.gridy = 2;
			constraintsKeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeTextField.weightx = 1.0;
			constraintsKeTextField.ipadx = 46;
			constraintsKeTextField.insets = new java.awt.Insets(6, 2, 209, 21);
			getMultiplierPanel().add(getKeTextField(), constraintsKeTextField);

			java.awt.GridBagConstraints constraintsMpLabel = new java.awt.GridBagConstraints();
			constraintsMpLabel.gridx = 1; constraintsMpLabel.gridy = 2;
			constraintsMpLabel.ipadx = 7;
			constraintsMpLabel.insets = new java.awt.Insets(8, 12, 213, 0);
			getMultiplierPanel().add(getMpLabel(), constraintsMpLabel);

			java.awt.GridBagConstraints constraintsTimesLabel = new java.awt.GridBagConstraints();
			constraintsTimesLabel.gridx = 3; constraintsTimesLabel.gridy = 2;
			constraintsTimesLabel.ipadx = 5;
			constraintsTimesLabel.insets = new java.awt.Insets(8, 4, 213, 3);
			getMultiplierPanel().add(getTimesLabel(), constraintsTimesLabel);

			java.awt.GridBagConstraints constraintsKhLabel = new java.awt.GridBagConstraints();
			constraintsKhLabel.gridx = 4; constraintsKhLabel.gridy = 2;
			constraintsKhLabel.ipadx = 10;
			constraintsKhLabel.insets = new java.awt.Insets(8, 4, 213, 11);
			getMultiplierPanel().add(getKhLabel(), constraintsKhLabel);

			java.awt.GridBagConstraints constraintsKeLabel = new java.awt.GridBagConstraints();
			constraintsKeLabel.gridx = 7; constraintsKeLabel.gridy = 2;
			constraintsKeLabel.ipadx = 6;
			constraintsKeLabel.insets = new java.awt.Insets(8, 3, 213, 1);
			getMultiplierPanel().add(getKeLabel(), constraintsKeLabel);

			java.awt.GridBagConstraints constraintsEqualsJButton1 = new java.awt.GridBagConstraints();
			constraintsEqualsJButton1.gridx = 6; constraintsEqualsJButton1.gridy = 2;
			constraintsEqualsJButton1.ipadx = 11;
			constraintsEqualsJButton1.ipady = -5;
			constraintsEqualsJButton1.insets = new java.awt.Insets(6, 2, 209, 2);
			getMultiplierPanel().add(getEqualsJButton1(), constraintsEqualsJButton1);

			java.awt.GridBagConstraints constraintsRecalculateJButton = new java.awt.GridBagConstraints();
			constraintsRecalculateJButton.gridx = 1; constraintsRecalculateJButton.gridy = 2;
			constraintsRecalculateJButton.gridwidth = 6;
			constraintsRecalculateJButton.ipadx = 109;
			constraintsRecalculateJButton.ipady = 1;
			constraintsRecalculateJButton.insets = new java.awt.Insets(2, 23, 207, 4);
			getMultiplierPanel().add(getRecalculateJButton(), constraintsRecalculateJButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierPanel;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("Configuration Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setColumns(12);
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * Return the RecalculateJButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRecalculateJButton() {
	if (ivjRecalculateJButton == null) {
		try {
			ivjRecalculateJButton = new javax.swing.JButton();
			ivjRecalculateJButton.setName("RecalculateJButton");
			ivjRecalculateJButton.setText("Recalculate");
			ivjRecalculateJButton.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRecalculateJButton;
}
/**
 * Return the TimesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimesLabel() {
	if (ivjTimesLabel == null) {
		try {
			ivjTimesLabel = new javax.swing.JLabel();
			ivjTimesLabel.setName("TimesLabel");
			ivjTimesLabel.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimesLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	
	ConfigTwoWay conMan;
	
	if( o != null )
		conMan = (ConfigTwoWay)o;
	else
		conMan = new ConfigTwoWay();
	
	conMan.setConfigName(getNameTextField().getText());
		
	conMan.setConfigType(ConfigTwoWay.SERIES_200_TYPE); 
		 
	conMan.setConfigMode(ConfigTwoWay.MODE_NONE);
	/*
	if(getKY2WireButton().isSelected())
		conMan.setMCTWire1(ConfigTwoWay.TWOWIRE);
	else
		conMan.setMCTWire1(ConfigTwoWay.THREEWIRE);*/
	
	conMan.setKe1(new Double( Double.parseDouble(getKeTextField().getText())) );
		
	return conMan;
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
	getKYZ3WireButton().addActionListener(ivjEventHandler);
	getKY2WireButton().addActionListener(ivjEventHandler);
	getNameTextField().addCaretListener(ivjEventHandler);
	getKeTextField().addCaretListener(ivjEventHandler);
	getRecalculateJButton().addActionListener(ivjEventHandler);
	getEqualsJButton1().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Series300ConfigPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 357);

		java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
		constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
		constraintsNameLabel.insets = new java.awt.Insets(17, 11, 11, 3);
		add(getNameLabel(), constraintsNameLabel);

		java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
		constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
		constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameTextField.weightx = 1.0;
		constraintsNameTextField.ipadx = 190;
		constraintsNameTextField.insets = new java.awt.Insets(15, 4, 9, 9);
		add(getNameTextField(), constraintsNameTextField);

		java.awt.GridBagConstraints constraintsMultiplierPanel = new java.awt.GridBagConstraints();
		constraintsMultiplierPanel.gridx = 1; constraintsMultiplierPanel.gridy = 2;
		constraintsMultiplierPanel.gridwidth = 2;
		constraintsMultiplierPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMultiplierPanel.weightx = 1.0;
		constraintsMultiplierPanel.weighty = 1.0;
		constraintsMultiplierPanel.insets = new java.awt.Insets(10, 5, 1, 4);
		add(getMultiplierPanel(), constraintsMultiplierPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	channel1ButtonGroup.add( getKY2WireButton());
	channel1ButtonGroup.add( getKYZ3WireButton());
	// user code end
}
/**
 * Comment
 */
public void keTextField_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void keTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void kY2WireButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void kYZ3WireButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
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
		Series300ConfigPanel aSeries300ConfigPanel;
		aSeries300ConfigPanel = new Series300ConfigPanel();
		frame.getContentPane().add("Center", aSeries300ConfigPanel);
		frame.setSize(aSeries300ConfigPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void nameTextField_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void nameTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void recalculateJButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getRecalculateJButton().setVisible(false);
	getEqualsJButton1().setVisible(true);
	getMpTextField().setVisible(true);
	getMpLabel().setVisible(true);
	getKhLabel().setVisible(true);
	getKhTextField().setVisible(true);
	getTimesLabel().setVisible(true);
	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
	
	ConfigTwoWay conMan;
	
	if( val != null )
		conMan = (ConfigTwoWay)val;
	else
		conMan = new ConfigTwoWay();
	
	String name = conMan.getConfigName();
	if( name != null )
	{
		getNameTextField().setText(name);
	}
	
	/*Integer temp = conMan.getMCTWire1();
	if( temp != null )
	{
		if(temp.compareTo(ConfigTwoWay.TWOWIRE) == 0)
			getKY2WireButton().setSelected(true);
		else
			getKYZ3WireButton().setSelected(true);
		temp = null;
	}		*/
	
	Double temp2 = conMan.getKe1();
	if( temp2 != null )
	{
		getKeTextField().setText( temp2.toString() );
		getRecalculateJButton().setVisible(true);
		getMpTextField().setVisible(false);
		getMpLabel().setVisible(false);
		getKhLabel().setVisible(false);
		getKhTextField().setVisible(false);
		getTimesLabel().setVisible(false);
		getEqualsJButton1().setVisible(false);
	}
	
	return;
}
}
