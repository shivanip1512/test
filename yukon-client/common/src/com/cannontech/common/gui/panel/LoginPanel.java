package com.cannontech.common.gui.panel;

/**
 * Insert the type's description here.
 * Creation date: (11/18/2002 3:58:30 PM)
 * @author: 
 */
public class LoginPanel extends javax.swing.JPanel {
	private javax.swing.JLabel ivjHeadingLabel = null;
	private javax.swing.JButton ivjLoginButton = null;
	private javax.swing.JLabel ivjPasswordLabel = null;
	private javax.swing.JPasswordField ivjPasswordTextField = null;
	private javax.swing.JCheckBox ivjSavePasswordCheckBox = null;
	private javax.swing.JLabel ivjUsernameLabel = null;
	private javax.swing.JTextField ivjUsernameTextField = null;
	private javax.swing.JComboBox ivjYukonServerComboBox = null;
	private javax.swing.JLabel ivjYukonServerLabel = null;
/**
 * LoginPanel constructor comment.
 */
public LoginPanel() {
	super();
	initialize();
}
/**
 * LoginPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public LoginPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * LoginPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public LoginPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * LoginPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public LoginPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G1DD805ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BAEBECD46715F4C53711EA25C4CBD21A222E25308DEAAD152AF4C5ABD4F933DE555A249B27A52D5BBA8A8D342189CD3D2D1575C6A87B4DF8ECEC63B1E3FB7CG466047C08CFE8727B8E36370B04404B1B876C06CC03ADCFB2E6D41F79E1D39FEC25ABD5F635C39765C9920527E282523FB6FF91F731D6F7B4E770D097DC8F23E49ECA74414CF445FA993A1B92D049C79F0F39A628AC91A05687E6A814A
	48ABA60A2C811A33B54DE2A357BFAD046F9678CEE9CE33D443FB85892BEAB33C443163865AA3BF7D5123096DFC77845A594B3E3FEFAB01E7A9C08760BCG49045AFF51D6A3708B70DDAA5EA9184C04B8CD14E7C7D90D8E3FD37059CC7C3909713CE1F3883A825F14E7B5BC571922BA0B18F71FF5F50B6F1B0F2CA5B97F3E4FEC2536F47A91F2052C434C1FF5649FD8A272484FBD076159BBB4B355575BFF616259EBEECF1FC70E28AE17BBB8658B8CC981D9A1A449CC7DAAAAAFE4B204F8601B7B701C35D758D8BB
	3316ECFF62CDD3BC9FB2046D9C1D8FF7D7FD5932A10F6AEEACAB91F2DD702DGF01BB3E5E8F3976026AD9467E03CF5705E8DB0E4C1DFCE3F053E0C5EF902640E3719774771A54542FD58E6097A126C731B8F30713D58C24748161FE6698298GD0G12GD2G8E313AB2B51F006759F175FC506F8F864E06C26794EFF8B0681595F8F73900A2F04372C4B06CA50457CEE9F90B01BF88E9794B6BA6BA569F919C6B146147092ABA4D798631EDFE5EEA4E94BAF667C7E363F13D54CA63AAA9C833B4811C819885D8G
	D8D700F1DDEEBEF92F38AE85D4B99C10141159711094D64F4D7A942F9C96719DAFCB941F91F039D38FAC4773327124BEAFD95FEAB76FB548CB56C63BF9F3814F47B385513CF05B213836F797440E871B2FFBE99D2EE1F5C8605DA4EA14639FE7F80F8E4FEB3C0161693797E869FB31EEC7CBB1863765E1E26B6CB00CE15DD3C74DAD055C1F7E4215E37B3DF63AE604G1F843081E0ABC096C0AEC0C9910E6FE34D6D77D937C33A316532D7DB2A60A9AF28E169D2A022CA0189793A9476C9630A9CA102E734057ABE
	3ECC6E93B61FA640467BF2C08ECB2AAF888BCBD8724BD0C8914EB359D2C6F942B223FAFCFED803A4FF08205EC0CB85BC95A922DE8BF9A5D50EF2F07ADC8B4D651CF49DB40282F83FD9C4F3FE142DD9AD70BEG40F375EA8745A5437796ADAF7F59D28E4F4BD0583262F2757BA6289BD2F8916D3F5ACA5792F9CE8E287DF2C88EF861A5A2E83FEBAD0A66E5B228791C4B70F40E1C8747A39A7E4D56B20E879D7AACA33D200D52A5D58D7B46E7D5F9C4D6D5DFE0AAAADF5AC6FD09AC1BFE91A266F8A72D31E7A02E1D
	GB9DAFCDFEC2B5045678E86E7E6C39EBAF2A2CF1FE36BECE1F11A252E987739474A683E57DF0CF57A5AA856E93F75CB246579B643BAE57BD1B12FCF35F865DCCBE6BA23B61A4AC69FE04E77979B4F4744EB4B5987321177G3C94F85E2F1D24F9DF8F7EED8148AC463C7F302D0E4F37F3D2C44699F6B58CA3A6FB23D546F9535B635758336D46B576FC3BF10D7D3CBDF18D6DE8B72AA1CEFB251D6EAD4179002EA6FF5BDE4E66F0A0A0B327B591427B0857BCB4973E00CF7D48671D1255915F128CBD53BEDADB3D
	EC8EE6C3CEAC7B3016669B31161CCB0F12547FADB25C4F567DD3137966BEBE6621FD51DA62BEE5F7501C4B0A6C07C41D8F4E86D40EFF1D4DEF01976B4B2ECEE35E57BAE9BD4E480B026FF71D85042EA34A2C2C4536C95AC76D07FDFE98AABAE1C5AF7715E53D5C9A700F76FD1B61198E40E5DBBC45705477D23CDE9E6E227EEC873E9C0012922C17405B87F473D4944A7920227034EB63F163240D0E1F7C27D99916E1BA11AFEBB10C1FACDFC13BAE6261745BA7E9BC0AEE8DF01650F8C6347E0E6E919CF71B612E
	0240D8816E3903B6FD5F690D549B0E9D056992BEE6F7CBD66ED74F3DC3752E87FC0A967BAFCF6A57283311C8F042473683B18E3FBFCE734DFA555B521C34509619071AF639BEE019657DE2A17317722716E03F58D5C9F31DCB6331C26FE3C5FBEB3BE26B64CBDDF43CA6FD618874B9DEF981F3760FDDB4F63A7F88B476E5EF6BF3113A3DB2EEAE4ED8F98EBC56153928BEC57D8881FE35665B41F74AF439009E92365BA998A97467476F64713A955FBFFBA79F6B16603AE70D69B557D9236BE70AB5F6AD657DCB86
	4BD72A1577AF959A7EA5064F5661F9DF63E2F87ADDD78AFDC7A96E3356EE9A5768FE98077D98D7708FD6DDDCC3C1DFC055E2F21C22F3AF987641E4C1DCBD43C1874D96A33ED7B95998AF004EA4G0EB359A9EE93FC276F473D654715CEF86E066FC29B0CBBC0BF0097E095C0B2C0AAC0268D79F7B17EEC78361525D9BAG06GC2GEBGB681E4GE417A17F194AB5A2EF7DC2E71350CFF5168A5BC6FBC632AD7EBED61800C6A1759EF46E5B14669B2DD32B406F936314E81F5B1FC0F7FE82DAA9331B184E47343C
	1B564F0276DD453E9705CF9524271C0E39A2AD86E721B86E1653719DAE473556865FCDGC3G5EF25E2F7075611B73B43EC9E8ACE1D998894993E434024A5658D1B68D78B381F281EA00568D40ED4E8668FABAB6BB938C0C4861B9B96C1646E50553665C954BE950BB0F874F8597043F2F1C22793D962127A23F4C8435D89EE13D9C52BC3001DF74490AD7233F3D174A8E4156BD8F274465321FF11769E83132FBBF3043F3C41A1311677C34BCB1830EF17A3FDA683C9C10A5AF64C397CF2F0966096DC967E6D595
	5657CC16277AAA3B3EC6ABF8EE536049692D43FAFA1D1D53876CC8EF9EB4123F2951CF576B69A1A13FCE2337AF994927E87454D6BDBDD5486F5668790A1ECEAAB93D299269C38EA37D3D9A7DD7DEBDFDC848AF40132FF3CB65F8C711843834CA2C3B4EDB746C256DCBED523C6AF23D9F76F94FC910F6DA45FCEFFA0A2DDBD4FECFA56ECD59CCE7D7D51A458D602D427EE861985EE524FC75E162DE55EEF837335DD3E53EDB455703642A683E6275794D5CE61F9DEDE6815DDECDF10ADD9FA72D0991E0E803A727FF
	E5991D5660D52227E7AC23D3FFAB4DFA3A39D2CF27B97AC44873581EBE0A316DF9FABDD13EDD9B775E6AE6B50FE915F85ABA8E1235CF4E1AC5BFB099279FE0354E639F2D42FEE0CBB56676530B7ABB2B2D7B2B4C7B62584FC63BB5515CE6C1EE397DA9651031FDEDFD60BEF8075107219A3C579A9FC45CC3B5D8C7C71B9C467A34294FE555FA2D475174CDAEA1AED753774F076B0C7561F2215379DF2DA85FF1G657B2E470BCFACB3A23EDE85E5278F20BF1FF3A0AED95337FF640031BE318A61BDE32D0C321987
	F02FDD15705EAC7EBE7870DFA9777D871663BD52D395F4BE5B8E729E4924611FE6F8078E4F57145735B52589E8C98731F7323BE8EDE743F74921B40B93209760A64042A16C253E5C5DC16EF1176389065CF020D4F4578CBC3E73763F6E3E6DDF76625C597C5D4748163FFF4194EF6EA49D62F3A6C5BCB7369DE4F89E5F13BDB43EAC206582B89CE916AE008100DB8E0CEFC7F78B0F6F0257271ED5D5E9E21AF56A985CF51FBC9FC5538D1F4776C915D1EC2A037B7110A336FF62BEB9CE53F5EBBD5037GE4G6C82
	A82CC5FFDA5F3D675D68050584F7233E61C4B9EF82BB3D354BFD933D5C20115C1060755646464365966B9359DBD8A117D40BF3EDE649C8AED970EF0EEBEFCFEBA2FB69AB6432B4FBE795A339EC41EFB99C4FDE1243C82EC670374415EB100D644E88FECFDC39F45A860A7767F46FFB4A9F64FE69C1015B282C787F34416B3B56957F7E4969B23EFFBA61B23EFF6AF0A53EFF6AF6453FFFFA4F253FFF62F3730FFDF4EECE43582C8658883095A063B04E4D9F389A881B1B866B44856F14FCBE48EE2CD5343F29A7FE
	2CCF755C7B1E7B699EFACFB09E040D4BCF75A37E399EBADEEAB04410622C5EC3F384475AE915338AFCD68F5D6794F9122179B9D6E57D4AEE08EB4FE1BC5BFE219A4F3605C7B0DE6769787EB71FB69E2B4E5346E3F56AF4627B6877CE479BCB6E5F2AE174EF74887A1C6126380D703DD5437DF990E34B50F0AF8E22EC1606DBD30F3816BA44EDDB24383B703DC6437D5D926A5B2061F6AC216CA68DD75106B8D20F38EF4C203EA18DB72A20BE2F067B0E0232A18D77048379D2B51C2596791E51F06F4A4867E8C05C
	63B272B5B560D8CE76459FCBFF5F3DEBF12E0F5DE3C18DF996C3B2475D69E3F7A9131391D92B79D5FDB68DF7A9B2940C7868659ABF93FE3E0A5E032AE0E821017E867E478BF4FD4BEE20BEFF353ADF2C93DB9BE257C67C0D8BA183F8A8B48A5A4E06E84F4375FDF19875F5B7466A4BDB816EC62E2F12701E70D6E3D49F777DE5B70F5F2752345CED24B65EF463DD26825FBC264FDC0C46747AA07A10EA600309605D2FF1CC5B9AD74674E4BD6A5B9EA7A63D3E1C95B195BAD746746AA2DDC7A6024A2CBF90A1BCA6
	B32BEDAAE3F7E2CC5FF9BF9A539BCB680353E99C535E84B1F5BBD746F4228D75759B684BDB21EF5439BCA6C5270F7B1FB9037EAFE87EE78DC47D4FD0505EC3F17CAFCA607FFA1DBD2EFB7603286EC33528FBE39C5D7AB1DF29FBEB0C6EF517233A3FA9236E74B8F9B2723BD2FCE769F4739E7B599B7ABBE55B6379E6242F543DCB584BF1C64F277C8C7513EABCC3B59DCD339CA17C5E2F10C47FF89CC303C4337B0B926C6D9DDF7F82697F498B2646B8F6EF9D957F2FF2B4EA774CE3BD626C172E1DF7F7995A4EFE
	0F5BFE768AA1D86F1503EC7D894F4A5C1764F9FD8E3A0B8F453DD72FBB46FD68924F3B52C1F10EFC6B8A7A219C43FAFBF708EA6774CC6D9C3D61D8A2BFBF7DB06A6701A5B3BCBBBBD0C7739D541125596898095A385106F47BDB09EC0CF822B6FEBB335C06490FBA5CEFE9EB5935288DF7AD52D7EB748D57237417347374D60D7E430FA81D77D6FF212D2558A3DE3EFC941E57255B62F4C30FDE41B0F6D29F40317D6A78EDF9C275C8D384FB532CEAFDBF1D83B68A1B925FD1F33BDF9B237346813C5DGB781D400
	07G1281B6832483E48164G149CCF335481F4818CG0C82A8G2B0E63BE7A686071F83E83D2DC4831C6B0AC715F9D387FBFD8F6AEDB8B7AB69E3F9F7FEDB7E8DE3782EF2666037DEAF3829F064241109CD697B9167DAAA7FCE8392277A19774151C381FFB7E97161D716AC0266B3E64BA6E686586C06666FD495DEA534B05C0E675FD49957875F26BC0A6653E64BE2C554B25024C367B12FBF95999EF97486C39079C6F4FFE4F56CDDBB3DF13B9AE17611A840E0F7377466277EC7FB6F66F1E6DBF460C4F8F5B46
	E24F8FBF9DB3BEBF3CB826BFBFF05C4EB1BA4F272056C7B53EDFEB38B18D779A53C9F19F793C6A34064FE1FED17C00CC8DA27E8D6657BC430D8A5C7F68F0E382E77918761D9072F4B00C32C59F5335FD5227A87C5B4AF8FC81E8C335FC14FFCCFD7405A41D5F559AEECC9B0B1D433163736B615871293CC147D4BB337029063FB35E30937E3F8ABEC971AD311F18C700B1B045751D3F92EBE3600A5E8647655447727DF7FDAC5F7BF7E2795CF7E279665BE2796E364572557AE3799A7C31FC533531FC7E5AD83EB7
	65D83E3CC9BD9F6F0B24E3A5513C313261F84702B50A4732A17C7FEF2F1099D010729B122F92AB7B892D0E7F0666D2C9917EACC65F71CE9A5E2D74A72DBAC4286458857A9F07F50B4159F09DD7B1AB846B240037AE24B166335F34A2E1329FFF07E93B442AEC7B5CC9D75E0EA7724C44F2099470D9F709D8685439C472594C3AC472E056AD507FB83368A172A772E7D2703703953D5C713F687B275C4F6EB1457D9F0DD651B7352E7C3F04DF18E98DEC817C36D65C5FBA4D2DA4FEFFB0621794C51514193381AF6D
	954EAD2AF2741E61D7E62ABF02BC0248E93FB1D33FEF6BE47E8FD0CB87888D2D9C51D890GG9CAEGGD0CB818294G94G88G88G1DD805AD8D2D9C51D890GG9CAEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1290GGGG
**end of data**/
}
/**
 * Return the HeadingLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHeadingLabel() {
	if (ivjHeadingLabel == null) {
		try {
			ivjHeadingLabel = new javax.swing.JLabel();
			ivjHeadingLabel.setName("HeadingLabel");
			ivjHeadingLabel.setText("Enter your Yukon username and password");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHeadingLabel;
}
/**
 * Return the LoginButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getLoginButton() {
	if (ivjLoginButton == null) {
		try {
			ivjLoginButton = new javax.swing.JButton();
			ivjLoginButton.setName("LoginButton");
			ivjLoginButton.setText("Login");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoginButton;
}
/**
 * Return the PasswordLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPasswordLabel() {
	if (ivjPasswordLabel == null) {
		try {
			ivjPasswordLabel = new javax.swing.JLabel();
			ivjPasswordLabel.setName("PasswordLabel");
			ivjPasswordLabel.setText("Password:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordLabel;
}
/**
 * Return the PasswordTextField property value.
 * @return javax.swing.JPasswordField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPasswordField getPasswordTextField() {
	if (ivjPasswordTextField == null) {
		try {
			ivjPasswordTextField = new javax.swing.JPasswordField();
			ivjPasswordTextField.setName("PasswordTextField");
			ivjPasswordTextField.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordTextField;
}
/**
 * Return the SavePasswordCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getSavePasswordCheckBox() {
	if (ivjSavePasswordCheckBox == null) {
		try {
			ivjSavePasswordCheckBox = new javax.swing.JCheckBox();
			ivjSavePasswordCheckBox.setName("SavePasswordCheckBox");
			ivjSavePasswordCheckBox.setText("Save Password");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSavePasswordCheckBox;
}
/**
 * Return the UsernameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUsernameLabel() {
	if (ivjUsernameLabel == null) {
		try {
			ivjUsernameLabel = new javax.swing.JLabel();
			ivjUsernameLabel.setName("UsernameLabel");
			ivjUsernameLabel.setText("Username:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsernameLabel;
}
/**
 * Return the UsernameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getUsernameTextField() {
	if (ivjUsernameTextField == null) {
		try {
			ivjUsernameTextField = new javax.swing.JTextField();
			ivjUsernameTextField.setName("UsernameTextField");
			ivjUsernameTextField.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsernameTextField;
}
/**
 * Return the YukonServerComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getYukonServerComboBox() {
	if (ivjYukonServerComboBox == null) {
		try {
			ivjYukonServerComboBox = new javax.swing.JComboBox();
			ivjYukonServerComboBox.setName("YukonServerComboBox");
			ivjYukonServerComboBox.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYukonServerComboBox;
}
/**
 * Return the YukonServerLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getYukonServerLabel() {
	if (ivjYukonServerLabel == null) {
		try {
			ivjYukonServerLabel = new javax.swing.JLabel();
			ivjYukonServerLabel.setName("YukonServerLabel");
			ivjYukonServerLabel.setText("Yukon Server:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYukonServerLabel;
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LoginPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(266, 169);

		java.awt.GridBagConstraints constraintsYukonServerLabel = new java.awt.GridBagConstraints();
		constraintsYukonServerLabel.gridx = 0; constraintsYukonServerLabel.gridy = 1;
		constraintsYukonServerLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsYukonServerLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getYukonServerLabel(), constraintsYukonServerLabel);

		java.awt.GridBagConstraints constraintsYukonServerComboBox = new java.awt.GridBagConstraints();
		constraintsYukonServerComboBox.gridx = 1; constraintsYukonServerComboBox.gridy = 1;
		constraintsYukonServerComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsYukonServerComboBox.weightx = 1.0;
		constraintsYukonServerComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getYukonServerComboBox(), constraintsYukonServerComboBox);

		java.awt.GridBagConstraints constraintsUsernameLabel = new java.awt.GridBagConstraints();
		constraintsUsernameLabel.gridx = 0; constraintsUsernameLabel.gridy = 2;
		constraintsUsernameLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUsernameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getUsernameLabel(), constraintsUsernameLabel);

		java.awt.GridBagConstraints constraintsUsernameTextField = new java.awt.GridBagConstraints();
		constraintsUsernameTextField.gridx = 1; constraintsUsernameTextField.gridy = 2;
		constraintsUsernameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUsernameTextField.weightx = 1.0;
		constraintsUsernameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getUsernameTextField(), constraintsUsernameTextField);

		java.awt.GridBagConstraints constraintsPasswordLabel = new java.awt.GridBagConstraints();
		constraintsPasswordLabel.gridx = 0; constraintsPasswordLabel.gridy = 3;
		constraintsPasswordLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPasswordLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPasswordLabel(), constraintsPasswordLabel);

		java.awt.GridBagConstraints constraintsPasswordTextField = new java.awt.GridBagConstraints();
		constraintsPasswordTextField.gridx = 1; constraintsPasswordTextField.gridy = 3;
		constraintsPasswordTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPasswordTextField.weightx = 1.0;
		constraintsPasswordTextField.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPasswordTextField(), constraintsPasswordTextField);

		java.awt.GridBagConstraints constraintsSavePasswordCheckBox = new java.awt.GridBagConstraints();
		constraintsSavePasswordCheckBox.gridx = 0; constraintsSavePasswordCheckBox.gridy = 4;
		constraintsSavePasswordCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSavePasswordCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getSavePasswordCheckBox(), constraintsSavePasswordCheckBox);

		java.awt.GridBagConstraints constraintsHeadingLabel = new java.awt.GridBagConstraints();
		constraintsHeadingLabel.gridx = 0; constraintsHeadingLabel.gridy = 0;
		constraintsHeadingLabel.gridwidth = 2;
		constraintsHeadingLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsHeadingLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getHeadingLabel(), constraintsHeadingLabel);

		java.awt.GridBagConstraints constraintsLoginButton = new java.awt.GridBagConstraints();
		constraintsLoginButton.gridx = 1; constraintsLoginButton.gridy = 4;
		constraintsLoginButton.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLoginButton.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLoginButton(), constraintsLoginButton);
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
		LoginPanel aLoginPanel;
		aLoginPanel = new LoginPanel();
		frame.setContentPane(aLoginPanel);
		frame.setSize(aLoginPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
