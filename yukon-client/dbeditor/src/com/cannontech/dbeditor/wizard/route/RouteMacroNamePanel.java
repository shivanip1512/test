package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class RouteMacroNamePanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjEnterLabel = null;
	private javax.swing.JTextField ivjMacroNameTextBox = null;
public RouteMacroNamePanel() {
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
	if (e.getSource() == getMacroNameTextBox()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (MacroNameTextBox.caret.caretUpdate(javax.swing.event.CaretEvent) --> RouteMacroNamePanel.macroNameTextBox_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.macroNameTextBox_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the EnterLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEnterLabel() {
	if (ivjEnterLabel == null) {
		try {
			ivjEnterLabel = new javax.swing.JLabel();
			ivjEnterLabel.setName("EnterLabel");
			ivjEnterLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjEnterLabel.setText("Route Macro Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnterLabel;
}
/**
 * Return the MacroNameTextBox property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMacroNameTextBox() {
	if (ivjMacroNameTextBox == null) {
		try {
			ivjMacroNameTextBox = new javax.swing.JTextField();
			ivjMacroNameTextBox.setName("MacroNameTextBox");
			ivjMacroNameTextBox.setFont(new java.awt.Font("sansserif", 0, 14));
			// user code begin {1}
			ivjMacroNameTextBox.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMacroNameTextBox;
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
	return new Dimension(350, 200);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	String macroName = getMacroNameTextBox().getText().trim();

	//Create a Macro Route object
	val = com.cannontech.database.data.route.RouteFactory.createRoute( com.cannontech.database.data.pao.RouteTypes.STRING_MACRO );
	((com.cannontech.database.data.route.RouteBase) val).setRouteName(macroName);
	((com.cannontech.database.data.route.RouteBase) val).setDeviceID( new Integer(
							com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID) );
	
	((com.cannontech.database.data.route.RouteBase) val).setDefaultRoute("N");
	
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getMacroNameTextBox().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RouteMacroNamePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(367, 200);

		java.awt.GridBagConstraints constraintsEnterLabel = new java.awt.GridBagConstraints();
		constraintsEnterLabel.gridx = 0; constraintsEnterLabel.gridy = 0;
		constraintsEnterLabel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getEnterLabel(), constraintsEnterLabel);

		java.awt.GridBagConstraints constraintsMacroNameTextBox = new java.awt.GridBagConstraints();
		constraintsMacroNameTextBox.gridx = 1; constraintsMacroNameTextBox.gridy = 0;
		constraintsMacroNameTextBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsMacroNameTextBox.weightx = 1.0;
		constraintsMacroNameTextBox.insets = new java.awt.Insets(4, 10, 4, 4);
		add(getMacroNameTextBox(), constraintsMacroNameTextBox);
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
public boolean isInputValid() {
	if( getMacroNameTextBox().getText().length() < 1 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}
	else
		return true;
		
}
/**
 * Comment
 */
public void macroNameTextBox_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {

	fireInputUpdate();
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		RouteMacroNamePanel aRouteMacroNamePanel;
		aRouteMacroNamePanel = new RouteMacroNamePanel();
		frame.add("Center", aRouteMacroNamePanel);
		frame.setSize(aRouteMacroNamePanel.getSize());
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
	D0CB838494G88G88GAAF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359AEBF0D4E7B58885AC0CC315AA53D6A72D34C645BE14AAAABFB0625887CEF1ECFDC60D53280CC3BB284C1456D43F6CA6CB0004A4E401B4ACC9A850921BAC9BCA8B2621927200C02A91E21AD6ECF3133D1B5C646E5E655E3B595DA4341E73BD6E5EECF6C3DBB5B3E73F7B1D6FF55E675C6F0638EBB30B8A9CEEC28A0A887FBBD1GBF4F93726D3567BEAAF099F2360B587E8EGD410AF8F94C23B844632
	96E53B4A496AFF94C3FFB974D7AC48F66D0567CA72E6EFBECECEF34E43B036745761BF4FFECEDE29B8E7AB6D67C7CA205D891085B8ECB74BF83EFB3C0A6357F13C008287A1BF8C601C23639E0EBB897D9E0025GD981B6FF8D345B684957B51F6473AE5F10C9B6BF703C239477316DA920EE182DED67ED9D392BE063B01E55BFDE4257CCC1FFA5GB26FA49163A5E8DBFA4606DF9D52025EA1A99452C226BCB4620DAA93126E776ADA44141B1BAFE2731AB424EBFF1302F20F9412D5D8B7B202FBE70E97533D8871
	C1FFB34505F760F9AB60F9ADC00E05BFD10278138A708BB022889ECFBD5F42F9681F3C0B2C7CC148D91606478C05711625A4F8DC9AF9EA6BB3D49F074AD06EF9B0D6BC1A6D72G1C8698G48G58C76D60051D286456C133CD8B8635D0EBB8FCC6756B2FE9FED9056766E69861380B7210267B89E13AFEEB3CA185BD3381DB273E6FF5CCA6D9AF8A3D2F38F88749795285475694B2494972B9160DB2D95CBD1A1089BB7B7634E72F9F1DEE17534FFE1D584FDEBEDFF39625B8FB65A3750EBC3E4F965B59C746D057
	F954862A6039005B8743BFCE712A8D4FECF41C6231BF85E339E342B67AF78ADA3A8BEFA265CD5DA96530AC3B55512EB29AAE2B4934FC1E5A5DC2B51916FBA9FE191ACC4B2992C1CB960C158433DD4C3E9AAB50E74350DF8C309CE0B5C08E409600121030B1F3675177E8E3BD12EE1E09A82ADF5689DB5BD83687DAB9E66AD2D748B02550105CA7690AB4284A8661F3DEAEC3998CCEDB778A357DA1B8632C9C12F549D434906C8E7EEA4A3A41663417D5609CDD26233DCAD0068302E1A2766DAE2BC45DC806794F30
	DFB265448CB65EDB06BA99177AE0C758G1E19DC5E5C0572EAG7E5B81FAADB9BCC4773BA06B90B01A1B3BB5EDAC926EC5CE4872900D53B4F60C40F3C6C848382F92F19B20CFB4310F2BFC17ED1FF6E5884917743870315F6C40386C56044DBC5EAFEC263D74F65270487914B603F0DCE336522DA57B4EE63AA752457AD3BB7A799A37165AF77E9BE0E75CE81D617C9F6F3F535255DAB76A4A849E16G5CED4938353C0E596C99491005157EDD8783137D8909B3BA6FA9A96472E47D2F537EFD1A10D5DBC9FF9AF9
	7F6FE4F5191E717F1093821349098A4CC372389CB25B65309C7243034147DE2DC0DB63FE9A502CB56D949F863FEE83C7B2AC7C798A74BD49B4F5E5903269A559B41550F0E27CA2BD4B088C0693A1C11C75C68546DFAD8AFEED4DFF2B82FDE2C88B05E42A9CEB899F9F20BEA92BF29008EE53A2A11361833BD0D7E3F21C470451DDD8680CCBEA84638B2B8B6E16F6A08F3A928415E308A23DE174576FD9F58D42A650B756B7C7E02CA5ACFCAFFAD0785E2109DB491A378FBBD2650EE53737BBEE89733C95CE789E23
	69DF1529E917E8EC60F80351BA67A24618D4958A337727F763DE6B610CBC007C3030771FD6623808A95C505BB4D5E56234647C003B9C75A2DF0948900AB1F8DD30E477DD772E2431BEAEC7B67EA3B74AD135453D072F209CB716085A4F8D7D86006EABCC168B2FB0D9B05E363E0D79BBD5FDB69CD13C91D341E8A819E0006108C919E6B55929E8B15F23CE973517B201F26807FE98E021AE74755DD2D1FBAD9D4BA4391F2ECEDBDFE66A0C3635FAC2DFF5F3C56DF5F6AF1ED9E8E43B5C0610F16FEEFB7CEFB58CED
	C82129074B4739472ECF06AB5E339D5A00229BE697B8DCCC48F2679E2497F393C773DC3D3B340473FA844E9DGF858C07C9F4F386D76E6309C3386DAA75DEF2A94F37410244B26177E3214F7E4D49A17E2DEA38A4E69251E6FED43518EFC2C67FAA9B9F88A5A20B037DE0882E734585BED36ED3AE75B67157ABEA2EA2D5BAACFF39A575DF993E9F93752112A76DDD8365F61B1F9FDEFA664BF75C517F363DBFF584AE26219E8F23D3744CC642A65664C3C456A201FD25BD8E532BA284842EF2078F5B6BC1339B6A1
	E41E8BE36D91113B3FDF0571A18B7AEB81F2GB683384733DD474705CDFCFC4F0B649A35D19758389E1254CB53022128D176BAD97C627D1E3DC5A2FE91119B7615A64B21E7FCBA6FEAD21F2D2B1F312EA6D91EC96B18BD9CBE6291FEEAD3FBF373F9340CCEC5D67D4CAFDFEA93FE19B5AE7C6FEA275D7FD6179DF36CC863FF397CEC92CD683F41FA774928C69ACE4358E5G93E08940CA0075G1B23C27EBF5EFB6ADA7227B65A2B35816E9C442E36649C0336C46B37B7E81CF147A087GB05C4BF144F5C35F9F93
	7540239308DB887DCC8B77B8452D0E317847F011097E94FC27825C6B3D6672CCBE171D711177FC467B29AD0A083DEE78F9D57A3A61D7D5296B060DD5296B065FD5D5B0BC6CE1FFDB9063CFD54DDED7B81EC9D5D76C277C5D3F270C6EED9832C1150E49040F7D5A5CCDD25497986F38EC20E6A95D4667F418DADB13A56366B63B9F65A469EAD38A5DB19B5E6A4118DD984FF655811C8468818847056DAEFB661A3123A3B6CB6C383EBA3DDEEE28CE2D17CFD4E3BEB43408BEA4A74B7ED355D556D842CB3ACCB908E2
	E2F3BEC3571B12BEAC4F505D1DF4BD9BCB377EAB5569744BF4900EE27D26CAF1280088490F238FAE080ABAED897433G56836445D98EE4B16B1EA84619G3CA042F6174272907961842EFDADAE5636C33F9FA086308CE05504481D3BAFE20E6E40B05DAD8D62E590432FBA053552EBC9F91165633AC9FBBE2E1BE43434CF0A38718F9A9B7B21BFE2611E6C9031E4EAD258C019A73D8E77F8D26B5063C96DDB4FFAE6772DBFFB52491E65413B2262DEED9D50D7B8A5E45BB4B0AD9FF82528595CFCD6D77CE72461EE
	2AA9168F1E2071935737CC09BA6DF00C3E1FC1FFB14072A911A7E24F09BC31620E1BC8F7460514F5DAEE6FB1476AA966DF39D309BC1185F9021DE94445191E2B702EFE95F137466D3AC1DD5EG933C5032716FCD9BC71954937BF85104FD9C697D3943BEFEE25AF811EDBD636D414302377C873308FA6FDF52562037DC6577233C6D69BBC88EDD7F072DCC6E3F8FD41114F918DAA9637F7549E29E2736DC9576FC7DA43E938520368E3F03F3A628AD7A603985A01F624ABB5BB98DAB5F19993F1C1CDEA7077BE0CE
	8E40EE4E6F26F7D88D15F8E73EF7C62DF6ADE06B3E798157653F6FF52C86FC025AE37B3B897A991E44915FE343B31FF8A1A6FC62B20C2DF9D75406DB7623EE8EE5E43BCE837483048116G2C48903E7D2047C72E915FFB35F0B7B83FEAF3D746DFAC7E416ED93F31D5585E4A2F5DC6D64DFF32A0DD7D35A6037B95EFB7749E34652EC53548DF799C28E7GFAGC6G26G164E917CD56EEFE07CF578953355B42521917A66A3186BD364E8820DC1195FF5CC5AF94B1A434EDF4D5B75F3E65AE35E9C7CFE71847D1E
	41ECFCE347E1E8C3F234CE04A4EFA7987C11E78DA98479DFD7824FB654799A6A1A0E57DB32490C5A3FBD1C5CFEA2653BBC35158FB19ADAF83BFA41C17EFD2035D3543CA60C55D2DA88A9A662EF53DFE762847DAA324DE979C50950136773B713769FBCD9F0B08DADABB88D799FCA443F8D23797C9D69E9FCF564BE610AA71E1BE26F35AE7CA0404EB87FFFBD0359D8C1CD7A7CD8DC13BABF6E2CC91D9F5DB53357161E1A74F7D687EB6CF7D64CC766B6E29EB8BE977C834084D888F04BDC619F7754BCCB28FF2470
	0D8E7FB056D8F8A3EE0A731756266775665AEB5F23AF2F45BAE6D0B3CDAD087B8B7C67EAD1DF269626C8063B2B96E524AB43A3A61B4A717756225D2AF20022D95DE2CC0ADAE655DCD14BDC2AC65CBA6867D838D555223E59B0D76479077EE57739721B8EB8C4FCCD567749F97CBEE0DE424E191CFF6945FDE300DFBACF4836650597206D13C6F9D445902469D43A006C5135302C1BF1062537D85CE6BBBB6D31E7A56CF75F3C595F3D988D87FC18AFF3E06E96G4FF5592E63G67G86GCC0005G37G2C84D887
	108B309920F8FE362B8A20816074FC4BB69A6AE7211F97D754C0F4C9C193617AD954185EC6BE5AF8ED9B3939115ED101EEFB63E1DE13E7B6E24D2D858206EC59626D0D65962E4B68518C85EBF9D6CFE4D2FB6885DE86660B1AE3349AEB8E938EE7312E7E98EA1F559A8FD50BDAE3497C19329676A0EA0D9CDB2D0178657393B9195F4B503D791D3677F0BAB902171D075C707741D1F8916915062DB53F716E344781E69EFC6C37DE74E9A5846CCA2AB2C12F77AE414450B03371FF5378FFB727096186A96EBE0EE3
	7B3C64CB2F27965F35755461CB6D4B97FCB3FD3947175A177BFCF6DF66F751BE0CE343E0DF7D563C818B77060513691E08FBDD711BA396FE0C520578F3B29EA870D7A8DDD10A6B6738F19B6E8D0E1B74212D814BA31AAE56E678E83D2A28AA6B4F23F31490181EA50F8FD39A1530E4237BA3968E69E6F6B7097F8240EB04335598FFD35488FE502DB6ECAB90D8EEA77BC4EE4F83DD327D9EFD1990F1AFA6BDD712308DAA96067744CA93F8A1969EEF0A7659630DE7C1364B37E076F843EC4A33EF261D9D58E737B3
	76FFA071379E83E6D7BCC60A8C1D14CE6373DB373E742BF5BF6BB31C4455C51E415701CBD09721D54BFE3C99611FBDBB49363086A16692603BC821C11BA2430C2BF2B7F187AE6267G43147D2D42DF8C136C267741C7714A7E68B94998B151F6DF69A605A17CC0CBF631E1D58A8D9FE54E73CA8CBF4B96348E20B4A91B84682D247462AEDEEB174A2F179DBEF0BEEF1513B8BA49F6D5137C1D529038F49759E60E6832B122297ED8782F2769C645740D93ABDCA7EC374FE29F1975627B3AC574D86C130BCBB714F6
	420BDCCCA7C769CC0CD6DEEB063FEDC45261A8D96FB2710333269F32466654184FFD676C5E4B8ED240193CC25EA10ED858A42E0094D45438C936D915270103AE400003070C00268701383AA4624410F50677E3777BEE7C6CD3EF52B392CBCDD24B2489CB0F8E4BDA70E8EBA02004E4EB8390ADEC3F5F5A9E67F813666C0A5D307B7CA3FF9A84D3E8A74E204FC7FF1B686FB17CED2218A60AE9BA86725DC4E67DCB9E0F891E297DGBDE5149E34GEFA7A8517C71E3775F761316CD9B013BCED2C46F37C18F6509D9
	FF0B8487E5FD7E0A9B776FD81A3B885594C6EB09613E5BE3560E48E1970D8C3963AFB6BB9F7D3453C18A3B080BC5CA30DF8CF7E03FB49AF691A7C4CAEAFA98E136733650A495748BDB9D3D5B29434B1DE64AG19E12408AD1CE9ED08AE866533C8023D9DF4EAA97DADFF5F6243D53B68DA3745350789F52F205A862C362AFF57DEDBED392EAA6D7B4C46C57C2EFDF1F2ED758589F9EA81FC7FE2D11BBCBD50C8526754CBC1C9D5CDC99DEB8D79B13F1E091BF222067E22047B9BE28E9FE4E3DF15106ED15B1A7FG
	D0CB87885D21B694F68FGGA8A7GGD0CB818294G94G88G88GAAF954AC5D21B694F68FGGA8A7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG308FGGGG
**end of data**/
}
}
