package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class RouteRepeaterQuestionPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.ButtonGroup ivjQuestionButtonGroup = null;
	private javax.swing.JLabel ivjQuestionLabel = null;
	private javax.swing.JRadioButton ivjNoRadioButton = null;
	private javax.swing.JRadioButton ivjYesRadioButton = null;
public RouteRepeaterQuestionPanel() {
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
	if (e.getSource() == getNoRadioButton()) 
		connEtoC1(e);
	if (e.getSource() == getYesRadioButton()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (NoRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> RouteRepeaterQuestionPanel.radioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.radioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (YesRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> RouteRepeaterQuestionPanel.radioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.radioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (RouteRepeaterQuestionPanel.initialize() --> QuestionButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getQuestionButtonGroup().add(getYesRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (RouteRepeaterQuestionPanel.initialize() --> QuestionButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getQuestionButtonGroup().add(getNoRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (RouteRepeaterQuestionPanel.initialize() --> QuestionButtonGroup.setSelected(Ljavax.swing.ButtonModel;Z)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getQuestionButtonGroup().setSelected(getNoRadioButton().getModel(), true);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabel1.setText("rebroadcast the communication?");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getNoRadioButton() {
	if (ivjNoRadioButton == null) {
		try {
			ivjNoRadioButton = new javax.swing.JRadioButton();
			ivjNoRadioButton.setName("NoRadioButton");
			ivjNoRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNoRadioButton.setText("No");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNoRadioButton;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension(330, 194);
}
/**
 * Return the QuestionButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getQuestionButtonGroup() {
	if (ivjQuestionButtonGroup == null) {
		try {
			ivjQuestionButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQuestionButtonGroup;
}
/**
 * Return the QuestionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getQuestionLabel() {
	if (ivjQuestionLabel == null) {
		try {
			ivjQuestionLabel = new javax.swing.JLabel();
			ivjQuestionLabel.setName("QuestionLabel");
			ivjQuestionLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjQuestionLabel.setText("Does this route use a repeater to ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQuestionLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	return val;
}
/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getYesRadioButton() {
	if (ivjYesRadioButton == null) {
		try {
			ivjYesRadioButton = new javax.swing.JRadioButton();
			ivjYesRadioButton.setName("YesRadioButton");
			ivjYesRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjYesRadioButton.setText("Yes");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYesRadioButton;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getNoRadioButton().addActionListener(this);
	getYesRadioButton().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RouteRepeaterQuestionPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(330, 194);

		java.awt.GridBagConstraints constraintsQuestionLabel = new java.awt.GridBagConstraints();
		constraintsQuestionLabel.gridx = 0; constraintsQuestionLabel.gridy = 0;
		constraintsQuestionLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsQuestionLabel.insets = new java.awt.Insets(4, 0, 4, 0);
		add(getQuestionLabel(), constraintsQuestionLabel);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 1;
		constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabel1.insets = new java.awt.Insets(4, 0, 4, 0);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsYesRadioButton = new java.awt.GridBagConstraints();
		constraintsYesRadioButton.gridx = 0; constraintsYesRadioButton.gridy = 2;
		constraintsYesRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsYesRadioButton.insets = new java.awt.Insets(4, 0, 4, 0);
		add(getYesRadioButton(), constraintsYesRadioButton);

		java.awt.GridBagConstraints constraintsNoRadioButton = new java.awt.GridBagConstraints();
		constraintsNoRadioButton.gridx = 0; constraintsNoRadioButton.gridy = 3;
		constraintsNoRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsNoRadioButton.insets = new java.awt.Insets(4, 0, 4, 0);
		add(getNoRadioButton(), constraintsNoRadioButton);
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoM3();
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
public boolean isYesSelected() {
	return getYesRadioButton().isSelected();
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		RouteRepeaterQuestionPanel aRouteRepeaterQuestionPanel;
		aRouteRepeaterQuestionPanel = new RouteRepeaterQuestionPanel();
		frame.add("Center", aRouteRepeaterQuestionPanel);
		frame.setSize(aRouteRepeaterQuestionPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void radioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	fireInputUpdate();
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB3F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF494D516D179894189981443209B15F163CCD45C49B80E4B6EB2D9DCF54D4EB29E9CB8B3F14C1E4D6C624A380C631CC1E54F3CF4A71D840200C40D9A2085E4C0E3C810D58C611350FC82D1B37907G2652DDC9BAF4F7B55555C9871A30773ECFF525D31D964E6E1E611C1B6A3A775DF7BF6F3E7B6EFB75A0A5DBD372F3ADA5046466937E6F33DCC276FFCE4832D53FF18ADC021CE1A306FF6F83EC
	A4032DF970CC82DA769D99368DE40F23905E6741FB476C8C5B5B70FB935967C85D8BBFE248C9G5A42A94D2F0CAF673EA6A127003EBFDCD38C4F5781D281074F17E5147F131AD20E4F64F8813996C232FA314D73B5E59C379F5E9B81E683247532760B60D9C4A5CF2C5E4F5B1D1D11C2DE7E47BFDA56E1BF06BE856C68E33CA77953CE9E4CDD5907325EB8D84CF992607D91GB45ECAC2C5B5702CE93C50F74829F8AB1C124F2778B45959DFB1643EA4292E8AD589EAF2F5F593BE1AE43FACE9327A27209C505C0A
	2FD172499ECFBF76FF66E0A16D0F10FD703E0A621AF2D166FC787D84C0360EBFE1C1FC0DBB43568260F18BBB8F7E31065B51F179C1123E54E7DD9F434E89EEE6DF1ABBE2E7F2705502F76818BC5F0C3EDF8E3462018C1B9D4081608218G709E0D0515CD9F423336C7BB2AF83D0A2F566F2F733854AF94176C015F5555C06138A6592928AEC258F8179EACB751E7BCE0FCBB2F190F79A47593B176E9CD77122C6F1F36941878A4AB3552B2F70079A2FDA06293A67B6B1832178C0C0E4D51325B09D1763CC90AB55F
	C4F67AB33BAD4BF9BF2B8D32ABAF60D87F830D01B2780DFC484F70BF25F84D00E7F15AC17178BE85E8ABAE085868F8DD685210B707EC28EAB075436C0CDACB230769506F0956E5A90D3BE48F1339D157E5B945AFB46019AEDFD0BC3E2FGDA481BE1E37175D2AB4E5BBC9F449640FE00C6GBFC0A2C00ACF4458674DFB3EE10CB5CA2AD697F4FBDC32CA986FCB4DEF41D38EE92AD46F8BE8124FA937C92ADB6A714881425B3C560CBE689945C7DAB0761DA063046C13D509CDD3D5724AB0EF832CCDD18B5A64D4E5
	CAEDF1FBE1B6CBDEBF917DEEEC01FE08C78AE82DFE974C77C88BC6FF278547E4D0EA039E2183784D7C72E1BB75A3583F8CE0396E07E45A5FE9D905ECD1DD5D20A897027E9634046CD7504F03B4F72C0676A50A70714E8E440561BDD9917DBC557206219F075B096ACB6A3018E3193948138A6D97ABA2EEDE6C90F163D8779DD27E4CA95338C1D82930F8A974C74F1F2534DF542D52AF727E795C8E4E37D8B11FC3C6C87D86ED46660CA1DDC67EB76031DEC37F2F6BE3B6258D472C916C72GA47A052FED2DF6963B
	F5D2C096517A158A01A63BA21EE7FABEEB31F31FDA392E08C77F2F854035EA3DEE530B346DBC10138970B2C059C5484FG9E00E4008597050F1BAD895F205FEB3179FA0069B2C8F599CF0F0D7F8F3A3031D95D0A7264C15927B9E0C5763960C700532CED28974F9B3D0A4ED3C471FE48B3C7E1E287F47C7AB654DB52B4555D83ABFC332CE9EEDFDF047EF69B4A8A84FB7C1194A5E4EDE9C3DFA8C310E774765B5BF00EBA951FCF26C12233F0FAA54D9132C7760252C71520CFE378CF5B312E3AA08F739CD55D0E45
	57206489E23EE3354AFC897D6DD75DDE88BDCC1964240A79E37A41D2030FF2A0FE3066AA83DA25AA724050F61187F6DC3A0BAC7C7A830B59DAB67BBB8ECB124A666ABCB5128718CE9FF5186BDEC5F3954787182E3D36F761993BEAE23134097C2C8CF323A05585B5CD711D001A4B4F67670F293F9601AE4BGD62AE2FE5E5F01F4118B7944BC2AF8BC4C6DC05B97C05F3C07052EEEDFE10079A692CB3734ED407115AF86E5D8E2B0A91F5647E0969DCBA32D0D0F87235F51066361B16473ACAAF327D5543561G4B
	69498116BB5382CCF602066D3275F63B609DF34291F8B6827881AAG4F7AFBA0CF344B54466B2FA7DF344B51D8EED98D4F62E0F4FF65B6512EBCB83ABF873C07GF20258CEAA906D9203ACC74D8B327E5203CC6E62C1EC77CE21E8D7B94868A787191F278C9A5B5D33D634AB9BE2F6C637E331D370B556EBE6B5F9DF50DD91545C387AC99ACCF0FFD0B354603F276BA87631EFC8545A9BEBB1FEBC70BE81E06E10188FBFDDA7EA6D648BA9A47B1EF7E36EA9520718EE4B06A27341FE13283557F6224C52D006EDD7
	C844E6E93BF13D2F8D8494271B169ABCEE9EEFB74E97064BECDF834FDE379A506AA12105C40CBD5D0E7AE2AD42515CC77FD531D978E8C09A14C29501A148DB55554699A46A58AB470D36AED83F5732B6062DB5A1E66359D04456F258D730FA70ABEBB1E173F6AE5017G30B97CB0453BFDEE4DADF95C1764DD3B190E153EED18DBDD2EDD8D869DABEAFB82101D1D9AD375406EB621677DDB0E08B8FFE08EA9142AADE67B1F44674BAC4543CC3FFD4391BD9317BCDE108240F4AD4A933A0621CD7A3050551F07F9D6
	D27B2AD8FED00B0D79E145B01B8F2517C47B59343D566F8EB09D431DC247044CB4D23A294ED4C7BA37AEB15DDAAEC5F494B42663D2DD47F950A68B006127658BFC49658CDB43E5214BCB7923F55F13E7267B044B221FD61B58CB4C865C624BE24FFB31EC974DEBDA330C69D20E9A9FB6AE34CCBFF0E6F79B1BDBAF58447E36B0AC7A1FDAA074BC8238D0D868791B8221A713377554F6F876499A6DF3576E1852F89CE87B050F531E3E1FF48C9C0D696379E1BEB74331FCDCD7A8FC3063CA066D6C95212332960FC2
	A43AD8D430C7232C76AA2A975C017AD5C8C3DA852DB1AAEAA979987E5EAD665F037B2B6129CAAE3742AC783AB62A1306583D98E27E6ECD46182F393A495456A96BB7DB122FB09B97DC09589A7EAB1BF5457957F84CE72E9536960E40DAB2A26A4855E3763F2111C8BD1EB8B236B6E77B4209B4F7260C30FDE1110E1FC9718B8CF8A67FED0A4777E5C02B39AA76B2D9DDD81F24407B8200A5GABG0A93B2EC6589A2E71E6978044459AB56439A207AA4CF7328E20C67498D1DB82ED0BF717712CE74832D1F082875
	CF1D4379F292E43AG42G49G693A8E5F6E0C2B433110298E4C4F2D56F8E79CCB92C67B1A71B963722D0A62E376241C4775F4438D60C7G87C0B74084009400C5B7883B1E6FBC9057AE8C4B9605456851686A156B39BE6F7A6C5B9C17AF670651FCEC0E3DD53705182DF1271AA2538E56C61A1F6EABAE6472D65FA0624E5E0D7E59F7A3549AG7DG93GD2819681AC3FD1786740193AF87E510F9040C7E22FB056CD5C5EE72F53CFAF4465A3B719714D483FBEF9FF91172FB4CA9E73F5EF1E70F565CDA247F459
	227B6A0F62B55741BD062FA26AFD02294DAB47700D87F3F5BD138A2E4FD7F746651BFF13599C3DD34298FC82E8B9G2B81CAA7C0EE8468G88CF90B1F84B1943579203634E535757DE5FD8140D615B926D4789E6B63E69C49B53013698E0B9C05EC42807819CG7D93050D59E78E46331167212300BB897BAA13A946F5FD7ABAED4C0EEB631409237958995D26C39873A9C0CB87E038DF9EC65CB2F8DFB9D1445734DA5CCF16CC027D94008320FB12204D2634B03C4F1D4476BF38D762B9323643A093F90CBA14CC
	9A3BC6A3D44640BBA619597E69A899C5D1BC314E4FE21D3D95C43D33B17ED5D76C331995DD66E7B3AFF5191F4D2C6A5A487050077104D8504959714FEE8A4F46BE3BE9BA97DB4F4EF366FAFEFD4EDCCF795C78FAFA4F4552F3B3757B3FEBEF9213B323AFE13693F1CE12E8AD62ED0EE94A97D5FA5F5FBD9F5B0E074E1B5B71437338578DA8C155A9C76B7A3767CBF5DAE47655EB329755617BD74A2FC101AF0F3175A7141F51E271AFBD9F4B9F4C5663F9EB392D0D3D2A62ED2C62717BC031F16EAE60B13E18BFF3
	CC66821377E1F7EC9FD5F51B7B68336E58BEBA549D5FC7A73AE37B684B6E78BEEA6FC69F7438DDEE151FE8B15F14649B63E02F6E33A772C77BECAF775905D1B545AA6E236249FC8FB1F92C4FD8CCBE7956FA9A5301009C20C9998FDC382C19B6238ED8B333FCD8D00079F09F746918AC76D90F970C528BA2175917AA459E1387A573B1790194FBCC96C8714764B1A97618E4CA71476499E97C38BD365660172311B93A55995B561DCEF3DB4B1D31ED7D44995F569AE7ECDB0FBA635B5A609C5F5622E2232D11B87C
	70901EDBFB24E1A5289132E8B246C56DA1F1BE5D816FFE0059GE9130DE7FE8F8DE11C74C20583E21A7D321324CCC15E5F9D963C4570EE87B882508D10B0C5445870C534D51CA1B7C8BDF83C4C7539EB886741A90A2C62385E907657A987C66DA3986D0EB66C6BCBE594091FA5E7569A4FDB16CFE167AD45B7E758985DDFE5C6375FAC6A0547689EE59F3CBBF45C62E644F540FB3F0E3B3DD554951A0E3BB1CF70AE52F1771C913C4BF4DCDE17604D39D978E70BB80F596B180AED769568F795260A339C77A1F1BF
	E28A6096CE95E7C5D5F48F256F4170BC233A7A046AF655C9FD8DF414591E60A1DA9FA17F2A29626C75B95AE765348C5BC9006EE9626CB534D31CC7265DBB07B4A41CB6BDFB4DEE59EB89CDE3B1B2F7DA644CA3B578EA8113795E91A1F39950B7A4A22E7E08F18C50460939ECCF416817C751B146F793A37D7ED1F454F71645C8FFEE94BD5F404FECFB6A83E15B6A27D20967871F47BCD76EC8E4B605793331EDBB59B175358266775F761692533DAF0DE4E63F35CE6C4352218F067B4BAA01DB11A8467F53BA9C7F
	DE85528F9D7F97E99C6E188EB56AF4512616FEFF52A091F5CF47FBBF0F8E20B54B26536F8F2D8E2E733C69E3578BF1FF414A6191E803301D53B22733F3A284A67FFBB4166D37E4584A81183CE7AFC4643D57A5641D3D45DCDE41B8727C37C464B15BCE53785754204C46A8E548F81FE87F1A4FCC3F05513161721378F341646DFCDDD58EDD5FBD18AB57497770B57331333A078E639CFC42609306FF1462179870AC8F7CCA4F8359C0F3FCCB1C2594F8B19ED361FD91C096404BGA5C990C3C9E2FF772A33124459
	5F35A87E86D888BD06B21959E7BFF2FD7703FEDCA066DB7A23F713C7A63D129B6B5B06A3090D210BBF1737EC27F8E65F9C9F5A178C3479GCBGD6G109930DF1FA16C2BF315B37B0E395CDA2D26C94EFE7A85C79857661607A2E8DC4418ED05F5C65B4AE7B079C77833E5465898E7FCE9D5C63EFE5EAE4C1FC9A6FCECAE7DDEA3FA6CAFEA46EFB0BEF9482E9FC79F07C430EB1B4BADF9143EED65764AF2FB553E5D421F7F5CE51C9B9B663CEF917191ADEB9E17BFFFC6A4B7339C74DC2B788E1783342DD48FC28A
	C9649F53F35AA0514771DF0A45B816FD6F5B443174155C6DB164164DE472BCB3A3F2178F2CAEE0FDCE9D0E58FE7B19EFEABB533B21CB68BDEF2648CB77DFA2FA0C1C390C74AC202D1AA9E2E21B67A3F836C983BC5AB1B49415C684A09BD545AF2B5AB043522F47FCECFF0847BD7C77C2436F978EF518587C3F85CCC65E617FCB997CBC5E9B3B66FD5BEBDE73EE761A6F2F37FB475F67FF640D3D4F5F6BB55E51E063988A639ADEFAAB54EDG9DG9E0004DB4518AE74E25C606D3431F37C182B8FEBEE3C11268979
	7778E25B7ADDDF7CFBEC5F7761375F9E850ACDAF76AF70BF7261F8E90A1FA2797E5C07BED25DFD7D9AEB4A710BFC981B9E391722D9AD3835CE540CF3EF957561076D22E61C2F633E55AEEA4685BA6E6F2B84EFF13240F51C963CF69D7723530237BCD92C95332E90DD2F83418FE24D8F7066A91817DF758C408624C56A5363B3275518774250EFD272787747185C05A3D8B74C03368B81F2GD683EC1815E15B85D083508860828883A4832482ACGD88C108D70B2C061EDA28E968F6E9EE7EE73A2188603AA39B1
	9C387EFF6AB26ADF867DD55EB63E7E7CFC69726B46B8E7690253FAAFE34C6A5F5869F502E6E8686BE37404F06C981C9C0E9F03C9E1FAA781E227E5584F770033423817D3FAFB83329E6BF3429BF4DCFD20D18938D193D657B550F8F3002D0D37097CB997C30157F577360B3AAE41449F461A4D2C2ECB3EAD3A0EDCC1E3B48570E93A3C3F4B0D48DBFCDA484B0AA14FBA0E3C1586F9AC56DF236B5AEA0EE7B852C66FC65E4EF04CC7CBC6E20F45AFC6620FC54E08F9BE780F11317960BFC74C73416FC60C790057E7
	A3188B7BA0EEBB74F6FF50F11DBAAE6FEA9147353BDDDA3F0EDFF3F58D471F14D1201E6B2F22DEC3945741F1EF9AF01D9C776ED50CA7B039DFD17533112BF4EF6276F8F82E27ED5CBE88AF5D9F3B2E220EEE3FE4507BE39D57290FC5FF57587171F699470745C8970A64B5421676EB2B9116360B9A2172F691F354053C6F1F97C47AEEB8FDEDFD67B6083E7B753E9F46F2166F2F241D06FBB6F4E8983EAC34AE0297C343F3D1D63BB19725C03F69370F1F0B981F2D4148778470E44561E37E7EED7B58B1D855EE9C
	83065B5CB0365D3686E3BB76FFD6FE56F4990CCC7BB549570835D38EE844722592D0493AD1AEFD6C2E7F7E374CDF36852C44D6CF5E411DEE332C324BC8AEBC3864B7350F13A23F82193A997075A4AFC09F7981ED58A3B710125EA63C699750E4177E0997443DC96FF16C41DBE7FBCECA01FE8D276A01861267430FF1648DC676C83E3EBDAC9F9F88614D2B5C5AEE9CB86A99827AEE227AE2AF95FAAF1B7EFA7D876F1F4AF944CAAC47499A0FA2390ECBCED8E16AC911562F4A01FE45638A798F9F219D958E21F3B0
	9A2C505DFB3A79E8ECC58BA494DD698B21BB9257ADDFF75CC2C8C8A5FBE8CB5C58D768ADDCC77BA53C01A52B759A5E11D7549DBA6D06AD5A4EFFBA7176D98B04A3B372A299A1F9A13FC6EC3D12576D99069FAC0083C8A954C0DBD57285705A8BE8E60F52CC10F4819537BED9B96B3ED74FD08191D60DECE52E8456BDFD32625DD35B5B6B7649FA8760D768FE335EBD362908EA73C6E8461B271E79C38F440103D83D151574EF957D3B977FD6D1CC9545D46D85672EA4637E0B2607044DB4F8C01F75D49F9C7E0A63
	B0025A4FFF7D645D3F28D939822CBBCE72697D9398048D11D6DF863DBD32BAA9ED566635495953F00C7C98AAA16C37B8247708965653AC18BD78C93575DF6F31DAC8DEBD3131D5810297D3BB84AF4D7C75448A2B820D3BBC0332F9D4D94B18E8C42CEDECD8A1FA0DBE3878360EB6DDCF7F3EF14D2EC22E924A7B16EE61E66640F722B55548B607A2876CF8514ECE7F4B15BD98106D12DD6517076D1AE2574896D56ED1E11EB8250086EDE53BD371FA03BE371356C3077F7C6C5D99435EBFBFCDBF0E21A95F1F6F3E
	EABC97D8B531B46619590EBB59BA317F4E487E1C5567DEDAABE900CF3AD35458FF537AB1093DBFE876CA9E0FA6F9AE547ADC38D7289B5664483ECFED437E83228DA7B25AB03D6FBFE060799FD0CB87883A0FBEE5F294GGE0B9GGD0CB818294G94G88G88GB3F954AC3A0FBEE5F294GGE0B9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2C94GGGG
**end of data**/
}
}
