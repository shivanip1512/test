package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.device.*;
import com.cannontech.database.db.*;
import com.cannontech.database.db.device.*;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class DeviceTapTerminalEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JTextField ivjPagerNumberTextField = null;
	private javax.swing.JLabel ivjPagerNumberPinLabel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceTapTerminalEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPagerNumberTextField()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (PagerNumberTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceTapTerminalEditorPanel.fireInputUpdate()V)
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
 * Return the PagerNumberPinLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPagerNumberPinLabel() {
	if (ivjPagerNumberPinLabel == null) {
		try {
			ivjPagerNumberPinLabel = new javax.swing.JLabel();
			ivjPagerNumberPinLabel.setName("PagerNumberPinLabel");
			ivjPagerNumberPinLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPagerNumberPinLabel.setText("Pager Number / Pin:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagerNumberPinLabel;
}
/**
 * Return the CycleGroupTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagerNumberTextField() {
	if (ivjPagerNumberTextField == null) {
		try {
			ivjPagerNumberTextField = new javax.swing.JTextField();
			ivjPagerNumberTextField.setName("PagerNumberTextField");
			ivjPagerNumberTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjPagerNumberTextField.setColumns(16);
			ivjPagerNumberTextField.setPreferredSize(new java.awt.Dimension(44, 20));
			ivjPagerNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPagerNumberTextField.setMinimumSize(new java.awt.Dimension(44, 20));
			// user code begin {1}
			ivjPagerNumberTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_PAGER_NUMBER_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagerNumberTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	PagingTapTerminal tapPagingTerminal = (PagingTapTerminal)val;

	String pagerNumber = new String(getPagerNumberTextField().getText());

	tapPagingTerminal.getDeviceTapPagingSettings().setPagerNumber( pagerNumber );

	//Tap Terminals cannot be slaves like some IED meters
	tapPagingTerminal.getDeviceIED().setSlaveAddress("Master");

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPagerNumberTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceMeterGroupEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(444, 309);

		java.awt.GridBagConstraints constraintsPagerNumberTextField = new java.awt.GridBagConstraints();
		constraintsPagerNumberTextField.gridx = 0; constraintsPagerNumberTextField.gridy = 0;
		constraintsPagerNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPagerNumberTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPagerNumberTextField.weightx = 1.0;
		constraintsPagerNumberTextField.ipadx = 132;
		constraintsPagerNumberTextField.insets = new java.awt.Insets(64, 175, 225, 60);
		add(getPagerNumberTextField(), constraintsPagerNumberTextField);

		java.awt.GridBagConstraints constraintsPagerNumberPinLabel = new java.awt.GridBagConstraints();
		constraintsPagerNumberPinLabel.gridx = 0; constraintsPagerNumberPinLabel.gridy = 0;
		constraintsPagerNumberPinLabel.insets = new java.awt.Insets(0, 4, 160, 240);
		add(getPagerNumberPinLabel(), constraintsPagerNumberPinLabel);
		initConnections();
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
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		DeviceMeterGroupEditorPanel aDeviceMeterGroupEditorPanel;
		aDeviceMeterGroupEditorPanel = new DeviceMeterGroupEditorPanel();
		frame.add("Center", aDeviceMeterGroupEditorPanel);
		frame.setSize(aDeviceMeterGroupEditorPanel.getSize());
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
public void setValue(Object val) {
	PagingTapTerminal tapTerminal = (PagingTapTerminal)val;

	String pagerNumber = tapTerminal.getDeviceTapPagingSettings().getPagerNumber();
	String password = tapTerminal.getDeviceIED().getPassword();

	getPagerNumberTextField().setText( pagerNumber );

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G88F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD4D45719A62212AD264909B935EE5A2509491206C63235DBB731A7065A26316EE932A7A6F4EB3BE4EBDBFB56EC5D331E33B6F50F17997ECC78139F01D6E4900CE0E1GA5CAA8CDC4904510160D94C917ED7218F9B3BCF8B3EFFC6F8DB308BF7BFD773E7B66B14C2066341C734DFB773BFF5F7F775D7BA0653549B9D936F2C232F20871F7AA0B10ED0D04B45E707F1263960969394472D787301F1C
	F6E743739E68CBDA1E1EDBC27E65FF72213D9A5A1DF72567D642FB997978D2E2BD3C445967E3687B036B1D26057739F4006F13C75B1DB58570AC82C8819CBEF709387FD0CD15015FEC60B9E4598849746018291A03862E935AC3G8FG24F95878EF41F39F5DF9C9D727B1EE6C5EE432EB53D1DBA12EE3D913C323174D9DB41E8E32A6EB179777DAC16944B98950DE8F004C5B49E4693B706C9E1A723E63D2FCCE176077ABFEDDF4CDB8C53724AB2A53ADCECBAE312B2B0FBE07054030287AA43FA01F257DC302DF
	14C9E0827758DA13CF5784FE203D0762F685F15F35703E99E01B09FFEA9A712725745CD1000044F97D6D51EE03175159B5E44F4BFEFBF19CDE93A546E31A9461F5C570353CEA2A174FD4227CF7C0DF49E4FAEE8340F9008900C400DFD1FB787C01E3706C9957FB951FCF71778482E7E4377A3E6296E5F86F6A029E83770168D2D4B7A1CC672BEA1AE350339030F9A937BD0F49A4658457FF6A870F102DDFFE4F1697C3A6DBD35AEC4913CC966BA6A3B2E1FB3F92F76F0C493976B9F76FCB443A770E250ABDA7465E
	EB37B459F6986B1029485EEDD3286B892AEB873CE799764170AA456B96BC335557284DE0BB817A36CFF15B98AD62348CE62FA2A56D03B16530B23D47B6A8B39A5CF2B4AD2DA1DC7B9EB91A1637A9BED90E266519E9CECB9A74157B52F319FDA5D7236F06213D82A095E0A340B6G6207F1FEEEE3BFB950FA0BB6B6A4287A1920A43BC515303949158760A906F5D59870EB3A60F709A302AA8963322891E34C2315A80371B9739E27366F02BD4E09FED195F4C971436A02CF54C5D5E3E33EDA399F4728A26D9D16FC
	A2EC608B903E6E5315E5701485CD7FC340AD68E2E4846BFF3692F5B2AD0C400A30G3CB3399CADC0F9F5G7F03GE3269C4ED461FA6F09AA040B2E2EC1C5198A86061193126AC7B96F2631A3G6F09FEAE637AC2446D04F63E42571929AA352C53A73910FCC11D61BE661F4678FCD061B6736F235CE67A8A9FA64DDBAE4434995A2FB0DB99D322FDA7CC57C43AD81BCC0F5E269F9799B45DFA2C287EC4FB58EFF19C136F3DC5282B8420AB99E003A96335D58EE633E784CD64D67AFB958CCCF4C7A44E68FCAB14ED
	4813355F256D8C054B6AD94AC7ACF97FB9006D11C97738D55EEF9718CC3ED80DF9C81C967DFA1F98907DEEF8510C3EA72A51568CBF75A8661C2FD2FCG7C3A979CC9B371DF2BC65F93F4DD1546033AF8D154F5496F0D74FF036E25854783111040775ADC0D71D78901DF1B63DF28C61FF0A9FE3FC815E3CEB17ADB28CF0A32688322FB1520DFE7789385282BA9F1460889278A3060199664A04697D69F2489EFA08F2A648315E308A26EG7AEB7121AA0B0C8860304E690467E0007BDE281E7BDE4315AF108D9F9D
	31454A9DAB9F6E33258618EFEC88C4FC0F5154D8981BF6A70D8D86DEE33492957BD82172BE463AF32A928CD86B94E6771B0AF14DCC58EB97C041E5EE77DFAC44FE9EDB8C036FD5E41909551477E70ACAD0BF62652088A1990358FB268CBFDFD49A55B7E2481375BFD2047214AD71EF5DE514E7A044EB4186E8F7830CDDE6B2DDF9194904711877916671D8751AB7A8B9033A04D1D150419083C11DB24DEA3319C379469A59AA2F419E2EC3B9F4C3FB98C0D6395EDEA864B5580A29E4127950AF63561B89AA23ADC5
	0D684D311857E0657BF14F6D5037DB65B22EAB3666019ECDD3DC92CDC106FC1EAF366A156136943F8ECF0F24EA7AGB8DE184B72FB45C8AF66A883ED646C4F52DC0E3C96E86939FDG6BB4440F9DAE3750FED5E339E6B5BC6DF4BD9246DC6D92D4D1F752DF167A9AA705E9A16C54C26024CE9A811C3D58FB96DF1B8C3DECECAAE5B40AD472464C2691DEC7BDD8F65A58F773632BC8770DB2DB2C3AB6313846C6F4A64BC6BDA2532BFF17EB5F7127DF6431F8F77DD0F4AD276B115C12284F4FB32C4629BE0CF2F9C0
	E7B5CE0E09EF2078B48B1E497131BAAE470D50579864F97921D2747DA4E827GECG48G588DD0B54D756CAAB9C1EED2778C005D2AF01038B8A750993A2D5EEFE73149E8576C4F61310970387FB721E8B9F4CC4F65FDA82A4D66FDE95EBCE734BC2366B19D9FE9BC48FD4FE292DDDD9706A17677CB226CE63EF65FE134E1741924E96ED3577A2DBE353E78386D0DB8BE3553583BA49451FF33F92EF93F9CE99802BE99A081A099E0A3C0A6C0F6184B7FEF777F76E6722776BB2C74826EBC44235E687C8173D9EDF6
	BF0D9D8D30F6A7G43298E440DC1DB8F73DCDFCB79DE896DF5A66E9845E1BC4BB0F1CF545DCEED03EB45422F0C03E7FBEC3A2DBDF26260635594CC46292571EB02AF17462E897E21B4F6CD70F469FE0607B52CA7815E7FEC6942B543F7CAE3558CB51407678E945335B5CD542852B1C198FDBF51ABC80C5A61F7F0D8B078AFA3AF057699E34E6ACAEF3BA96315079712716EA87D944C98768C4F0E1939FD6721BD9605E3767EE285466CG74A5812C86D88F30F5065B39D8F653B8F3B63CC01C39D49EDF07FFAA0F
	2DC3F7B97A362684D517982D272972AA33AF62D1833A68C31132B1BA1D2F8B2AD71C2767D9BA1F75451B1FD09E4F96183ED28FE19DA78BB3CAD0A7E4628A7A6B574DFAED8F482E9CF05DGC3D7D88EE4716D099046A48F9C94E1390B8151C57AE6F16EDD8EBEB78D46EF845886108DFD8D33BCF77E78986AE9C870026D84FD6322EA46C4567FCEC5595C7EA149BFA80CC3CD46F2732333565C1CB44B68CA1B657166C99AC7B6C2FB2B091B6E2567C3E8E7DF653827A92E8A5A9DA66E2E1308BB8F6DD19317BE4463
	17EC626A0FA2EEBD34DF3C4A6DEBEBC5FC7B78E1C5EC7B78E9C5EC9F7FD94542BE7EDF9571744A72713F9D62F7F85B01BE47B52E37760F6764A52790523B3A4E29127B0C609D24D6407252095AFCE37E68B5DE838A3574BE845AA9G9B2E71FC95FE13672B54C7D61141C56F452C81B3070F5BB22EB13F5DF3AD122FD2A0DF31BD57B8781E1D5753F383579177FD07D557B44F5BD88EE67D45F556FE1489DE4AC67AFB6674A33DB3F6EB3FB627BF47B21F71766DA31C37BD5FCEA172D75E0EDB5F2E3B4EF85AEABC
	07C66AC943DF7DA20F497DBFBDD5A4E6BDC08D1B711FB90B7CE33CAC3F41FD65054368E7BEA1AC79023E0B529511A45D4031BF25B6BE886F236658653DB8572548C11FDFB3462D26B60E77514F1B63DE6AC59E822A6891D5B83550D509B17EE7F4FCA63C6FB047A71F44331F874E8EEC4C5FD39F29DA84BE8D103488F15F9D6A0BA1938ED986588D9805B9B2C03DA173C5703EAFABB2260CF49D25FCCB7EA83EBF20FE77A20C4FDC44EB047F18D7635E8C583C3D1FF0DEE76D6D4EE3357389877ACF419D2CF64EB2
	715D94DFE541B39F7656F29FEE003E44BBF8CD7DA43B2F06F67E1D605BG1DG23G7A1DBC96ADBF50C6EE126B061540A084AB5992DE98FFEB9D1F6C6E7919BC6EABEB1FFC102CDB7A732CF8F5EB621D46D950F8EE9F2E376471772B113F8C685B85D035B8BD378D208FE0ECB167EF4B01E6469F9E59FBF4DDF0CD50D3A0E7EEC492C391B4A6A8465B0FE62D3C8596337D930C674A45736B44544578EDC7F29053B6255EB7616997C38E9EC21DFD92EC03171B0D475BBBCEF5F41EB859F12A59F13C2D094BA6B9E4
	7DAE53797A2918779B88BB8D9A329358F37D32FAE35F27FAA1741176131C265DF5911A2E1FBC941F260E8E0396B62F7DE8E45ED7060EC44F6B87C7EFBC2C89FE2881D549F3385951566CE86F4038D8CB679012CF785F4EF7676378BE430D44340DD78A38ED9CFC6C2FC95F4BBF4F2A0F43FF3741F7D2C2A4C7EC1F5C1347565C549421BDE976D6F8E6FAFF1B6A0F3D1F313C7BEABF495D696D450B25FF51BD18BFFC3DB2FE6D3129B2F66D71FCE56C5A63254A054F8FBF280CFF677823CA6B1DA3736707E93D3791
	741989103D84FC9A20F98977E52D6AB0213E9C430F4F3A3DD89B63978D1D6FBF32407D6A7FD55F7CBB08279A6F084695DDD7FC38BE474B5528AFDD89D0A443DD2677322A641D5059D0839F2AC6FB17C58FC5331AF08F4D5F67012F51A53CCE5C57CF47C17B2A091B696775E46292DEC3FD67FF2D7121E4D51D0D6702E8FDE7ACE1FE31EDC944BF181C731AE9DEDE8A756CD2AE5B479D3F066708B0E9E4GF643C9258B48A1D589082ABE433074765150D9EF8B6673E1D8A7G3082A095E083C086408EG328C4E86
	G4DGFDG23GB240A200FBGD6832CDB466978ECE35382F49885A8D534AAC828EAC34EDFBA96DF57EB0F5DDC57DFBBC66F88C1C743B38163CC14FE8C4FBC0A47230926CDBDFB2C44448DE8C30AA661D90A4519322BF36B1FCFDF43B5B783EF994BF80D646847B1BA904362D4ADDE8F92D69B5D534FEB237C447979A53AB6BA88E390AA8CFCDBE224AEE03A59CC57E6F4FF786BA371648A5EF38162626F47A761E0B8ACF84DB9854DC5D67FE6EAB77A4A1A51D7216C52A5C1162E506B560BB0506FB54E40AD8B1C
	01DBEE2E8FEFCBEC5F73354C77BD35A5366F05DB2C3E47F057DAB06EF8410EC64DF1F71A388FCD5CB23AA662AEC9EEFD4244AF27F4A17E3C089BF27CFD142E90450D9A3815965C07866EF3ADE8D34072042272398F36D03B11E459384F27E3A4BF1818A90F47A80DD2C030501DE66210EEE6CFEBBA8861F96F3F4FE23C0C11335D20B345EBC9586CDC71CCBF3F4F6F86DB0A1F2F7F69ADFCE7B96B1B7D56BAC907F909B1ECF7CE1DC4E35B7769DE8F98B64BF07FCAF1EB93A3F746F6A23CD99031B32A8A06BF98AE
	0C6039AA887B1F1CD705F3A05054D7C90E26125279B93F676E6F3E1C7AD1DF390D5886C86E841DBCC0F2C238FA98BFD7175F583BF72FF619DC27CC23212741CA05F324780DAF3C75634DBF98516CA4F700D42353DF04AA88FDC1F463DD1871913B1F6C8BA8902870E8B2C032B57A48517499D99CA4651E8F70230E260B6E9E6EE51ACEAA86F127D67C7052FADE50A6F4740C5F8C12ECBFFEEEA7252CDB967C5ED666F23F21D4E775FCCC69C5A19320370C520B2BB84DD54A1EAABED2F7E15BBABB31751357E5C5F0
	778BAE888483E41FBE210A5A04A23B4301F7CF530572C3288F2C4D6D305C2FCC7611D9A75E4518C4CF05BF17D4383D309F6A4E30CADA69C80CF1CEF3043BF7C2D0E1ABD19D50715F879435416C3B63107E66BF1E2B9C3311AC03C994FDCEB82013DC0F601364991D6CB36BCC2A17FD0001706157BC0A6A83629CD14471AEF38F67FD4F355D7F68EBFF24FBC4266A2416C9932636FAC545575A6371C8FE51DCGC48B4B57184B6398E7541852703D9597366C9D87D368A3F6DFDB9B7DED273F4771371DE25AA9267D
	B848F7A7D970AF3ABF4CF92676837494D3FA50821C7D20C47D1FDFFD6E416FF56F5C815C75139C7AC58374D09299456E4716265EDF73460A4CCF211A82E8ADE1DC37B0EC2E089C8E503813B9FD224B7E43076CB6128DBE4262B058AF86D330DF15390C9D62B0B53DD7G56E05CGB8BD47F11AF5D2C6EF689C0C84C72BE3C872F751B176F9C60B583C79560768B740987AE85C42D6919DDAC2FF4BEEDB1CB82B024E2DB42560E0C246FF89CC8640084C26313A2B6906356E5A3D24AA6E396C7965C65D35BC3A6EBA
	A3A2C70E3B53F33B6F66754EB2F78B091F17AF7A84D95685F92A476F469CFDE6C697A37575B991575778982313757DCEC43AA7ADF37E9FD0CB8788CED5A1CBF590GG8CA9GGD0CB818294G94G88G88G88F954ACCED5A1CBF590GG8CA9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2F90GGGG
**end of data**/
}
}
