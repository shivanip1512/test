package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.device.MCTIEDBase;

/**
 * This type was created in VisualAge.
 */
public class DeviceMCTIEDPortEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JComboBox ivjConnectedIEDComboBox = null;
	private javax.swing.JLabel ivjConnectedIEDLabel = null;
	private javax.swing.JLabel ivjDefaultDataClassLabel = null;
	private javax.swing.JLabel ivjDefaultDataOffsetLabel = null;
	private javax.swing.JLabel ivjIEDScanRateLabel = null;
	private javax.swing.JLabel ivjPasswordLabel = null;
	private javax.swing.JCheckBox ivjRealTimeScanCheckBox = null;
	private javax.swing.JComboBox ivjIEDScanRateComboBox = null;
	private com.klg.jclass.field.JCSpinField ivjDefaultDataClassSpinner = null;
	private com.klg.jclass.field.JCSpinField ivjDefaultDataOffsetSpinner = null;
	private javax.swing.JTextField ivjPasswordTextField = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DeviceMCTIEDPortEditorPanel.this.getConnectedIEDComboBox()) 
				connEtoC1(e);
			if (e.getSource() == DeviceMCTIEDPortEditorPanel.this.getIEDScanRateComboBox()) 
				connEtoC2(e);
			if (e.getSource() == DeviceMCTIEDPortEditorPanel.this.getRealTimeScanCheckBox()) 
				connEtoC6(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == DeviceMCTIEDPortEditorPanel.this.getPasswordTextField()) 
				connEtoC5(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceMCTIEDPortEditorPanel() {
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
	if (e.getSource() == getConnectedIEDComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getIEDScanRateComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getRealTimeScanCheckBox()) 
		connEtoC6(e);
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
	if (e.getSource() == getPasswordTextField()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (ConnectedIEDComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		String holder = getConnectedIEDComboBox().getSelectedItem().toString();
			if(holder.compareTo("Alpha Power Plus") == 0)
			{
				getDefaultDataClassSpinner().setValue(new Integer(72));
				getDefaultDataOffsetSpinner().setValue(new Integer(1));
			}
			if(holder.compareTo("Landis and Gyr S4") == 0)
			{
				getDefaultDataClassSpinner().setValue(new Integer(0));
				getDefaultDataOffsetSpinner().setValue(new Integer(0));
			}
			if(holder.compareTo("General Electric KV") == 0)
			{
				getDefaultDataClassSpinner().setValue(new Integer(65235));
				getDefaultDataOffsetSpinner().setValue(new Integer(0));
			}
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
 * connEtoC2:  (IEDScanRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (DefaultDataClassSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(com.klg.jclass.util.value.JCValueEvent arg1) {
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
 * connEtoC4:  (DefaultDataOffsetSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(com.klg.jclass.util.value.JCValueEvent arg1) {
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
 * connEtoC5:  (PasswordTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC6:  (RealTimeScanCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G15ECD0B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8BF4D4C51638879DF4628C63208767B8F3A26A9AB5234C0AA34EF2B86C9C5CC14D2E3A419515554C1163F076204BBA4C9E46E14722BB11C4928804CF940400FC928979G0110F4C2BE1DC0C392A269F49288A1AF5DAFC9C3273B69FEC99A0D3277D63D2A773A73BA894C59D917634DFBF56B5EDB37EE5D3AD5F5DF35E4534E58145526CD042CCEA16A3FE32B89493591F2CF5C474D9C37F4DB221968
	7E6D8258C06A07126139926ACADD0966CC3225BB954A695B934D2B4EA71A3742FB9659563D2492DE2234B3876ACA7E3DEF72586D7C290C37330616BB6D6970DC871089ED9D8118349D5BD76C59AA3E9D4A6B54F70455A6C22C1FA10DFCA9DB071F2F52E5C3DDBE40D2CA73C37BF635BE864AC873A0BCE7FF46E8DF02E79A556E5632D2152E73C7F1E445735BCC995816CEBE07229D0C572EBEF348835490E6F23974A4BCAD4DD77AAA1D3E01822764757A3C0A6C6CAFF07548AE37628B94288F17BC64F64AE5E5D6
	7AEC2C355B6A2C4D3E00D2C72B1BA52F6CA173F7207E5F3F14CAE5037ED0EE5E09B8F3B75AAC955EF3GCAF772BE3C1CE1D1F51CF6A50EA4BD30AEEA9F266DE43A4F5E2975A13BB1A525BF87654F26E302FCAB213E88F0CC97524AF5094F613A74F4A06D92A82F8258340B63734EA3BE9F4AD6004EDDDC470A7DDC47762F1FA6ABFBAACD6B2368286CE23A5D37CB53B1B4374B7489751B6FD9F06C97C15DAA004C5D60BBGF6G85E09B75571A72AF60D95D23547A8686FC5EEA3F3F46638AB47ADC32875E4B4A20
	C6451D131D3E000B906613D3AF659B6843A1E6F738BFA2303E35916EC371679EA70AF24414E25037D92FFCE60AD5E54C5A2D75ED65EE345B57F4EC9742BB726178B17C9D94EF79DC433331CA23F8ACF7C3DD42675C4E6D6B38AE8D49B3C8E6F115E90D01AE53FFDDE2AA5D43F4E85E236942ECACD5220DC7GBF9DE0A640820025G29FB390DE34A8F0FE7E39B4C0100D772348C76782500D2B36876386400EA6B397631ECED840CAF61067918DD628E6B47E87113A78C6D32B8EE0FE94FDEE68F4BDE4DAE2C6D14
	56E8EDF76E9D6D9B6B44987EAB1DAB7E3DEC8C4D821FC47113F4F8B6367D940F6529D0378200515FCF67D67B3EC89F19C971A37BA2FD2470BC7711D828CB5F1FE8E6637B61890C0DA1A8CF830887188F3084006442DC4E65E37CEAF9618467D133EEFC996FD44B96F84AA1A5A0593CC1C572BAE50794F0CBBD9EB9C8D41AFBADE8571EB03E1F51796D04B6CE4ADEB9A0A9EE1F9724CB83B2B8D31051BCEC5900B48119565A5D83B2B4B060A7DC6E9316ACF8FA2420526AF7C90A2CD1307A27AD6893C31283A402G
	F8F765223D9E26B177883CB767F2DB65D7A3AE8E4A7304DD8ED5EC046799F0EE5983315C6DC4B52440B56E2337F4E39F9682FDF7DE22F9994064AF924D5B8FA41AE38F70F97ADEBB77C7EB464FC97EAB15067E08101F4FFC4B1A9F690FD3E9BB9E4047G4483A4GAC83C8BF485759755D5D9318A3289BFF576B2C27417E7077588361F52C2DAFA65456DA5DFB0A4A1B96C1E3563DE7C45431F179C98DDDE7201F27G7A8F7231992848E1BEDBA385E56E25E78360E032CB9BA9C6FB399257A0F9C876AAD659AFFB
	DD7092D4FD64EAE51A663F3DBE61DB43940F712C969CACA8705FD4EEE0F810211F8D3C7ED6DAAFA9CA405DB32848AD3222383DFD9A7F1DD417E0D8288C2AFD3D2F1646896863F400B85157ADA732F4FE586073DD99745BF106297E3C1D2E5BF300FE217007459DF49FA64A6F5032D66F6DE81F40980E0AF3870D631FF18C7F5ECD35E11EA09DEB636173B8B7F8DC557B59DAC173CBCA13D3GBE846087188EB08FE0E98137C749796EB1645CE89F4656391936E554CE649C78CB0079CEDAD5F45F4F2CB2767DADD5
	463E3F3DEAEC5F5ED3E5645B2C6EC0957655B76C554D1522AA0C2FCE1F57AB5389ABD8885BEF3FE747FE383DEE254D6D6A13159677D7B2A125056873F3E98C4F0477BD05BC568EFF4EE36D1E2F9EA173AEFDE238BF1B7ED029E92410797154C2AD56B21D0A2BF18E4A9EF9G8CD56B9B74AA8CFF0C2E8FAABEE826386715EF11B7371675C0F641AE9F16509EDF0DAF14AF51AE9530C7339C68758586E4D76EE5E9C8AA10061582BAA48555343A8E5F73F28E9E2C4C996344105F6B8E48B62FFFD0DD6EF2739CF9C5
	4C0F32C85F0EEF28AE4056828713F3D07FD7D62518F0DDE61C3C1D6AE215FB25C10FE2159409BAD20B5F8D068A648FC91EC139007E2D6D173CFD208B1E272EF87A8AAEBB1130E0D0F1FB98C14169DA87BEEFDE2FC3C22FFF3BFAC724DEE7FBFB0360D87FA70A9D66FACD5F9222FAB5C38B43F0CE3143142CF74B9ED73E93569925007ED579F050C28541E118F86A3855E26D4DEBF10469302D067972B9D97260BE8A1D2836DFF6DE796BB9D089DF73DF34629C1C89F3F3BE4062C23E8EBED90D75FC9DD4977BDA1F
	4743428754D595E1EC78476ECD3A793E2210659D56097A7747295FA10DDDFFFA1C7A21F16AE7C42DE77D0C2D4944B8AADF9D14E1BB0D8B7D99799A5F9357EC0C2823DEA8765ACF5460BA62516D9198FEC62DF1DC0B2B45FD4195A1BF3E76A302FBE6A613456119924A74875C83302542CD0F1A1FB95C2A4F4FCC013EE1AEE781BC9783B0DE92C0392AA7605E7EE440B7685767C1E20AD1563273BC0F3382F8538117DF4C64F90A19ED985D4C8B1CEE911417C725F3F5F23A4CC30966DC00D3070C683E38486926C2
	FDDCD43AB7855D8228CF82A0070D686E6F62F496283783A891F4338EA35DABA26FD3A8F04BA86E8D58905C1E4C4F330D197A3C15ABD8EC3826513D59E1A6BF7DC864B9BF399E6566835EF2044F25FDD67D1E32BA9874B95D7418257A4BABD67DFA47F02FDBBF06A74C5CA01C79DDF2087BDC1295F545F3180AD67D66318395E47439120DBD5F32578E5300E1775542D1709438ED2D588EB85C72B061F67826DE1F7B1A33FE1FE9ED94BBB42B7D776B6CE0195BE5E2BA5DDB06798C239C1E13AAD950B76826D19E8E
	FC30BE5090254F577D146E35D1564CA3BC97B6500EF3EBA914C9C922B99B001D4BB7DFC8D7E90B90872046078E7D7C0FADE17B768570E431F864822ED9B79EF4395D6767EDE4639491E139ADE7E4D529EDA7BCBE03D82E2FB14C51CDD93F4134380459B07928E64B74730D269CG564757C49F1B01E64AD151478A23BE4EBB4AEDF3E7A767DB883874E31C2F30DC4FE7BD4678C24738EDD6F4167178F4D38BB54F1934DC646D47006C79227D0D970D5ADFF50C6B3D260B73E516C22CA865FC3FA95573CDAAE5FCF1
	25DCEFEF974E739BDC4FD5DD1F97ED4E81F9ABC41B3F8F33D57EF156267DB8EF3378524D7A111A6FBC240FBD731276C60DBD7D4719FF0C9C0F0CBDAF5AF03ECC87FC7CF19EFBAE54ED50451EF3B05BE188E12592E716FA549F56A4351CD11F42471C70BDC27A996C7BBC103998E085C0FA992C85E53C0DE7CE0E1B372C8B0D11371CB1AA8758D0264B1D140DBE87339C5DAE1A53D34AD80EEE2B4017D0FC3C8E4F46B6C8E3871617C0DDECB9BF0FEE3B9059FE27A53C4D1045A8B713B70A2FA022FCDF849F0B99FD
	1EEDBCAE6ADC322C6CB4BFC528716F679DB81E689F33ACBCF7565300EB5EB2A827D6A41AF3GACG5DGA3G3195FCCC1EBFE5349E040FC93445C04B3330FE3EF51376F9F7DC7B4C2B8867D37BDD417B3D28020F510FBBEFCE077BC771C56A10D9E9B4C65F9E5DC7F4EB17AE5A7562E981878BCFB2746840756EADCFD55B4D2D64FAF7DC3CB93D7BC671C56A5DDDE9E4BB5BC52EC3C8681051F5F3BAEC9B45972943CCC35B6D4D5DEE6C5F623045F47DF3A9DF5F9788DDDF3AC9DD5F9CD7D7F242C857CAC7343928
	9E48D43BFEE161F64DBE4167620CC644B5C3397B841FF77BED18D7627136596776AA04475A05B65C6F7B826EBE371763DE23B827AFC013B58C3769CC74BC5236B346F924DDE70C73C87B4F0C1DC3ADBC93AD4F44721D4F50BD189F7AB799006196BBE87E9E4A73CE70F15B5F0176DF8265642AC4739E00AAG9740940087816683AC81C82D0635231A7315D3BEBC03B4G4E839083908F10D04D7674F86E48D54F8BF896E0FC575AE7C60CEFA4208E11387991B817CE9FACCFB160D99261B7CB8CE82A54BE72F232
	810D662BF1636A3E46G37A1220CFB724D042DE7D8B64AE7C666F7338CE850EF56C74C8BA31DBE23721E5B321E7AD3B0A887698202BBF7356EF74AE6E210E3EC1286E44273AF67AE2429B4F50A2F36D858646FBA787CDD86E3C1EA426D37894A3991B8663F1E0668F3A558E0BCD72EB5202D023E4100D30E1C8F139A32C51D36A25A94F9G3B41E8EE277C0A946813C74D2729141F55C5633F37A15A1CE3B65A52293751A14DC6955CC69630C5F304BDDCD08EC560545CE9183C43C25E15F2AEAF26E6343F4682EE
	16213C299775720E88F9CDE24DDAE0A0EFB160169B4A33F669651588F96663E3FD17CE2E8D1715DD4B7762EBAE69659D95728AADFAF979AAFF157A34570E56D94D83B562BEB2A44A0F5172B5217FCB2D9843BD52B55FA0847BB93598A3FF97162F69875CD4008755B6585A730BE11C433D81E9G063F45AFBB49F4ABBDEBB8B8EFB2143381CA815A81C2D65EC74B906E490D32612CFEBF0D977A7A862987CE56DC67A5D7B16F254BE053EAD6B77B844E63480CF2B87F3921AD61B4AC3B2B92B11ABFBBF11E70CCEB
	B87F4C92545FA8834A6A5F581D952E1F589B337AD98DDB8DF4D4134BAA4D336559C6BA2AC40CE66ED79BF4BA0A8D8A6179431E30B3EA12151D7B126B7839F0C715D1FD36282F3BEAD41FAF6A9FD8EBD4DFA56A5FA8B32A3757713578B9FAC6680772E4018BDD2767C0A847895C6A13FC4D1EA3F07FE92777526AE13D2E9772AA383CD2013B230B4BEB9038D57B383CEE017B709BAEEF26407DCDB917B7DF604CDD1CF711407D200C73661E64B8196EE5ADD0EE973860F14E6B91382FBB69B7D9A8AF9438CF855DD2
	01DBC3EDD5FA8A7AF10A636AAD3CEF5D8277A7EABF854A13856EECAF17B775944F3796F7076DF37104DFD6F6B260F655C8FD8DB49A303DE3865DAFE14CC8BA4573D8658EBC0BE65B924DC5G27EC7C3BDAE89F4F3D457F6D8C52B06904613D33A47BBED3370D451249B6AD8F90B3374B445A9CEE63ED4E037A15B644BD542677A5747F97CCEC1F4C6ADF8D2B47F911C77475E96D7AFA54375A2C2FAF8F2BCF5171333E3D301777ED558BB309672903D16F54D51DE6FDF2294F500B1F92A533C94DAB7E3EB71B986E
	59E908E17D3FD71C036EBB4D6F5C35D6F15CC2019B394AF169E7B86E75359CE795380D62ECB7F2064760248E545F9F10FB65C0C0F631CF26E45999247FA37535D970BECF507F70BA667FFBFDDE0551BCC47D2C28814E70G318D084BBEE9B530075E26E69DF800E79240E635EE9A3CD7906D5BFA96C9ED45B5869698774060G5510351DC37775CB01FE85G433DCF67FECEA34427C60EFB124E55F6A83B9A390ED97B4753D12F673446F09D639AD9DEE63530D56E3754D6001FA75AFD0C5AFB9914D70AF6BF6D9A
	2F5DB53AF6A769EC03ED6DE9CAB46F8CEB77E99AF30AGEFE99279600B514774EE6AD37DC02BB4F11D6EB8BE1ECEE9E368945B24590235FFD72771F8BDCAE3DCBC504F96ED970F5B76DADD5BCB1A4247E1A5149F89EBFBC7875A4F69738C8EF803EA3BA5540FB196A41F65E36323B1F39314F34EF23B7DF7A37A36826BE773D93C2BDC580AB31E69595BBB161E481F217319C89BCD83F99754F7360F5B6838517B0AACAF78840D3931E7B51BB37CEC0A0F5761D93E305A416F6E4E063A6D4D7C5C3F116E4FA6C179
	BE00D9G89G4BGD23F64F9002226A2B2CE7E4D6E73B740414523BB16337E0D345D5CFD4CFFD843636B2CDFBDCAE65F766E6AE85FED36FF295E47D41F197F7CA945337EBD54C66F40C15D9400F80079G0BG160B7E051A72D97F7093D33522C84EFE7A750AF78EBFA8E9E85C5C323E7DACAC2F15FCCE3DE72EBE734F0D5E03B33EF955FA3EAA154E2EBE7B23725DF2D54FB72252CDD51F8FC665FBEB2D1EEF36CA1720BE132272EDAD57732DD069D2DB58F3FB4BE8BEB6DF1C9D0470F9B8F09D6314D79E4E919F
	8E2C30C97662454A5C62A3C579B9070E6529E39EBB2C7FEED07A7101285F8D0A54362D6AF3891CE6D85BF7F5E9ED7F21FEF7E45B7530E0656EF639A50F2FEFF7FE8EB4DFFC8496761D046593D2897F377C44E89C6F5FD3170870535F26F3BF5D7E5883447A6A3B2BBF0F2273082AEBC20B364729EACEC9E1B28FC81A5EF597A7E2B34657542371DD6F1AB85F073D9A5F72FDFF091D183C5D7D1AFD6A5C1A6C0D659357A96E32463772620E68FCC7CB006DD09EEF6F05ABDA5B6FF9F4635FB9060C1222B0999B3C9A
	DF7F7109739D71E9ED3779B5990F16CEC4C6F4FBFEF5D5135550017ED4AA5A79EDD02B7B0F6B939DBB662FAFDE504F314C1FECB671F5A252DF136DEABC35EB7E4A5A378D22A496A31EF2E86FF3F56FA79DB7F307F322405A687EDFEDC34DD1BB226764F6BB0CF3F2398E637CF501E36CFB10C79D516FC1D6B87477A0595A76894D1BD8E1FC3A81A683CC87083773F56D85877A865E429F3D2655397AB0A708B76F955E7E63ED517B7AD45B787775673661BD0F9E1F227886D0BE474FEF4371D2FCFE0AE438675A50
	C681F7DF3F42C8D57C0B742C6711FBA91A1DD71F3A484F358B6C7C8C7BCEA3BF57AE9638B3CD621CACF02F527B17252DF0AEED9567E9FA066D0432C7602E0B6FGA1011BCE4FD38B203CC860DA69FEF19914D7895C2FAC1CB75541F17FC54FDD6DD06E97388FFA90B7826598013B64643C53852E001E9716C2F92540255098115E86FB37B60ECBF0F15E5CB63E1FBBBA0C3890142735F1BF086BB8GCF07F4F974CD9AC0B687FCFEB920DCE3D8FA65C1752FC088773A33C04ECBGABG525BE11F8CD08A50G508D
	20G44GBC88B09BA081A089E0B9C0F2C722B93B036B7141253CB174D0138D54A983927B564474D80469C955BF76AAFB2349G2FBE8DB170EC4FE559295825BE4133EBA83ADF678E0D6F5745C374CE9378237D1ADF4DFB9F9D225F3FE8920E53158FE58A1CAD586C8B3AB1DF4E4E99EFD29F4E87DB148230B8DA7AB5760B1DABFA1A46BBD768A172DC518FB2CB087E7C73DEF57879BC39064E97201B54A16EF9B8424FB997C6B4FD6EF10C27CF4A987A4C04B64A4374F962EA38BE4F5239B587689688FDBC16F0FD
	EE7FC65367E54BF87A0CF5DEDE59413E35E97AE424076B73F9BA6A13FC9E5E4FF3FDB6B84375097FD653E748B91EBEE31D152B4EC71E156B4A437539C0E3C08350358BFD3EF5056B7376F5CD1F37DD636933F68CFD26C09B5754F755170711CF2CD5ACAC28F5B543386E607DFA58E3383F22577FDA00505B476AEF8BC51FEFF706461FEF77040C57117BC22357117BC3466B4843A17DBA225ED18D214F76C11CE997F43F90388E017BA5150938B637CB699778B9D4AF441F1231C10E7FB555EB18625AD55CEFF438
	8E951790421881DD6E77096F63AF0768383BBD9E757EA425F1FBA10488FB3CCEF5F47BA51D5EEF899C6A2D6E037127542ABF3D5EB851FD213AEFC6F2757D453D1A1CF50E9B13737EBECDCED31EA647E639B1B9D73F50647CCB11A6673D1E9B137307E2CD4E62A31A1CBFCEF85F4CF8BF46CFEF047D5E7D19C0A1B8C87C3BA4C5A1BBDB03F20E2D4E1A035FE4F2FAFD019C7C8EF4D0013DFE8EFE6E8986484651A7006A9F3C76EA7CA56BA693B15948C77D54CBECA4E5985DAD44FF2898833C99E15ABDFBFF49BB89
	EFBA02E672110DFC024122C58E50B9A83B30FD75870B75A44D0F17B5B065E6A349C17AC889AA57BCF2835954FB8EAF01879559D54DE7F7D0A11B9B3025C23C7ED8F8CA8A76ABB8A34B9BC83297FFDAC9B632EA0F646DABE4D33D1C6A393A3A9B6DC2038C81FD33283E749225101275776B776EBA3DE43619186A49479E1F642A171C30E85BC81A529F10037DBE0FAB64BFD1C58525527D8B0DD5A0EE1B68BEF636GBFE989252F04FE9A1331B4235E845B1DGA92414989B8B84052B36DF8AC0D3F2402660CFC5FD
	01BD226E169D4A3EFFBA393553C4D62B1D3CCA3EA5A9A13FC2BE6A15865C1EEB70E2253E90441AB4857E8B40F862B5C850ACA7C2B3DEA59AA87871F3C5F7BF7241F95A00468A9E43CC892C05FD32EF20303A37576D1505G30AB087FCC08C71A02881A0D219FEDBE7D4A07BD6087D6E29EA8AA22FF0B695FC3783718E20AA926789098F7B99973DFE4FD087719BA8F68331E6A0343DFD08FA3283C7E6EF3B35E30ACDF863D2BA7A9740AA58CC226C655B4B850A387EE0B3F7B333553123E0FE364C7D789215C0C10
	1008BD3451E017B4F4384C7C7683E693C906A94102BFB8AFC6F0F05EG1BA1E6887E546F788684A30C1DAA4B172E980312F91427A2F64DE817C374DAB170D8DAC7C9B3695F2C9BB6A3F2ED263CDBC56FF318F1BFA55A67857C5FC3A4C76985AE175523B86E42E7984DA103121E637EA89CFA1A2F854DF74AE2051A453E8428D7AD8651F71D389400B1388C90980AB16E6660F7F6C0E4291F4CD9FC2667F140EE8A4332CF6900CE635F4E4B3EFBC6A8570C7031EA849C32566F1E26B51A26E9BACDD33FFB1A5AB4CD
	5768B47D683B27E9737F9BCD5BB4CDBF52E91A22669F732E6B730FABEFCD0F1A639FF1295FB7DC113F8D2820BDCAG7CD2974FAD5CB9F410C44F8B348CC89E0FA2F92ED4FBDD18A328392648DA5E2D061EF5021CC62DE4F536E1547B320E67FFGD0CB87887C6DABCDEF98GGD0C9GGD0CB818294G94G88G88G15ECD0B07C6DABCDEF98GGD0C9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2998GGGG
**end of data**/
}
/**
 * Return the ConnectedIEDComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getConnectedIEDComboBox() {
	if (ivjConnectedIEDComboBox == null) {
		try {
			ivjConnectedIEDComboBox = new javax.swing.JComboBox();
			ivjConnectedIEDComboBox.setName("ConnectedIEDComboBox");
			ivjConnectedIEDComboBox.setPreferredSize(new java.awt.Dimension(140, 27));
			ivjConnectedIEDComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjConnectedIEDComboBox.setMinimumSize(new java.awt.Dimension(140, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConnectedIEDComboBox;
}
/**
 * Return the ConnectedIEDLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getConnectedIEDLabel() {
	if (ivjConnectedIEDLabel == null) {
		try {
			ivjConnectedIEDLabel = new javax.swing.JLabel();
			ivjConnectedIEDLabel.setName("ConnectedIEDLabel");
			ivjConnectedIEDLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjConnectedIEDLabel.setText("Connected IED:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConnectedIEDLabel;
}
/**
 * Return the DefaultDataClassLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultDataClassLabel() {
	if (ivjDefaultDataClassLabel == null) {
		try {
			ivjDefaultDataClassLabel = new javax.swing.JLabel();
			ivjDefaultDataClassLabel.setName("DefaultDataClassLabel");
			ivjDefaultDataClassLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDefaultDataClassLabel.setText("Default Data Class:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultDataClassLabel;
}
/**
 * Return the DefaultDataClassSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getDefaultDataClassSpinner() {
	if (ivjDefaultDataClassSpinner == null) {
		try {
			ivjDefaultDataClassSpinner = new com.klg.jclass.field.JCSpinField();
			ivjDefaultDataClassSpinner.setName("DefaultDataClassSpinner");
			ivjDefaultDataClassSpinner.setPreferredSize(new java.awt.Dimension(65, 22));
			ivjDefaultDataClassSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjDefaultDataClassSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(99999), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultDataClassSpinner;
}
/**
 * Return the DefaultDataOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultDataOffsetLabel() {
	if (ivjDefaultDataOffsetLabel == null) {
		try {
			ivjDefaultDataOffsetLabel = new javax.swing.JLabel();
			ivjDefaultDataOffsetLabel.setName("DefaultDataOffsetLabel");
			ivjDefaultDataOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDefaultDataOffsetLabel.setText("Default Data Offset:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultDataOffsetLabel;
}
/**
 * Return the DefaultDataOffsetSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getDefaultDataOffsetSpinner() {
	if (ivjDefaultDataOffsetSpinner == null) {
		try {
			ivjDefaultDataOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjDefaultDataOffsetSpinner.setName("DefaultDataOffsetSpinner");
			ivjDefaultDataOffsetSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjDefaultDataOffsetSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjDefaultDataOffsetSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(4096), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultDataOffsetSpinner;
}
/**
 * Return the IEDScanRateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getIEDScanRateComboBox() {
	if (ivjIEDScanRateComboBox == null) {
		try {
			ivjIEDScanRateComboBox = new javax.swing.JComboBox();
			ivjIEDScanRateComboBox.setName("IEDScanRateComboBox");
			ivjIEDScanRateComboBox.setPreferredSize(new java.awt.Dimension(140, 27));
			ivjIEDScanRateComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjIEDScanRateComboBox.setMinimumSize(new java.awt.Dimension(140, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIEDScanRateComboBox;
}
/**
 * Return the IEDScanRateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getIEDScanRateLabel() {
	if (ivjIEDScanRateLabel == null) {
		try {
			ivjIEDScanRateLabel = new javax.swing.JLabel();
			ivjIEDScanRateLabel.setName("IEDScanRateLabel");
			ivjIEDScanRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjIEDScanRateLabel.setText("IED Scan Rate:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIEDScanRateLabel;
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
			ivjPasswordLabel.setFont(new java.awt.Font("dialog", 0, 14));
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
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPasswordTextField() {
	if (ivjPasswordTextField == null) {
		try {
			ivjPasswordTextField = new javax.swing.JTextField();
			ivjPasswordTextField.setName("PasswordTextField");
			ivjPasswordTextField.setColumns(8);
			// user code begin {1}
			ivjPasswordTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_MCT_PASSWORD_LENGTH));
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
 * Return the RealTimeScanCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getRealTimeScanCheckBox() {
	if (ivjRealTimeScanCheckBox == null) {
		try {
			ivjRealTimeScanCheckBox = new javax.swing.JCheckBox();
			ivjRealTimeScanCheckBox.setName("RealTimeScanCheckBox");
			ivjRealTimeScanCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRealTimeScanCheckBox.setText("Use IED Data for Real-time Scan");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRealTimeScanCheckBox;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	MCTIEDBase mctIED = (MCTIEDBase)val;

	mctIED.getDeviceMCTIEDPort().setConnectedIED((String)getConnectedIEDComboBox().getSelectedItem());
	mctIED.getDeviceMCTIEDPort().setIEDScanRate(com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue(getIEDScanRateComboBox()));

	Object defaultDataClassSpinVal = getDefaultDataClassSpinner().getValue();
	Integer defaultDataClass = null;
	if( defaultDataClassSpinVal instanceof Integer )
		defaultDataClass = new Integer( ((Integer)defaultDataClassSpinVal).intValue() );
	else if( defaultDataClassSpinVal instanceof Long )
		defaultDataClass = new Integer( ((Long)defaultDataClassSpinVal).intValue() );
	mctIED.getDeviceMCTIEDPort().setDefaultDataClass(defaultDataClass);

	Object defaultDataOffsetSpinVal = getDefaultDataOffsetSpinner().getValue();
	Integer defaultDataOffset = null;
	if( defaultDataOffsetSpinVal instanceof Integer )
		defaultDataOffset = new Integer( ((Integer)defaultDataOffsetSpinVal).intValue() );
	else if( defaultDataOffsetSpinVal instanceof Long )
		defaultDataOffset = new Integer( ((Long)defaultDataOffsetSpinVal).intValue() );
	mctIED.getDeviceMCTIEDPort().setDefaultDataOffset(defaultDataOffset);

	String pass = getPasswordTextField().getText();
	if(pass.equalsIgnoreCase(""))
		pass = "None";
	mctIED.getDeviceMCTIEDPort().setPassword(pass);
	if( getRealTimeScanCheckBox().isSelected() )
		mctIED.getDeviceMCTIEDPort().setRealTimeScan(new Character('Y'));
	else
		mctIED.getDeviceMCTIEDPort().setRealTimeScan(new Character('N'));

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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getConnectedIEDComboBox().addActionListener(ivjEventHandler);
	getIEDScanRateComboBox().addActionListener(ivjEventHandler);
	getPasswordTextField().addCaretListener(ivjEventHandler);
	getRealTimeScanCheckBox().addActionListener(ivjEventHandler);
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
		setSize(416, 357);

		java.awt.GridBagConstraints constraintsConnectedIEDComboBox = new java.awt.GridBagConstraints();
		constraintsConnectedIEDComboBox.gridx = 2; constraintsConnectedIEDComboBox.gridy = 1;
		constraintsConnectedIEDComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsConnectedIEDComboBox.weightx = 1.0;
		constraintsConnectedIEDComboBox.ipadx = 30;
		constraintsConnectedIEDComboBox.insets = new java.awt.Insets(84, 5, 3, 41);
		add(getConnectedIEDComboBox(), constraintsConnectedIEDComboBox);

		java.awt.GridBagConstraints constraintsConnectedIEDLabel = new java.awt.GridBagConstraints();
		constraintsConnectedIEDLabel.gridx = 1; constraintsConnectedIEDLabel.gridy = 1;
		constraintsConnectedIEDLabel.ipadx = 26;
		constraintsConnectedIEDLabel.insets = new java.awt.Insets(85, 70, 10, 5);
		add(getConnectedIEDLabel(), constraintsConnectedIEDLabel);

		java.awt.GridBagConstraints constraintsIEDScanRateLabel = new java.awt.GridBagConstraints();
		constraintsIEDScanRateLabel.gridx = 1; constraintsIEDScanRateLabel.gridy = 2;
		constraintsIEDScanRateLabel.ipadx = 29;
		constraintsIEDScanRateLabel.insets = new java.awt.Insets(8, 70, 9, 5);
		add(getIEDScanRateLabel(), constraintsIEDScanRateLabel);

		java.awt.GridBagConstraints constraintsDefaultDataClassLabel = new java.awt.GridBagConstraints();
		constraintsDefaultDataClassLabel.gridx = 1; constraintsDefaultDataClassLabel.gridy = 3;
		constraintsDefaultDataClassLabel.ipadx = 3;
		constraintsDefaultDataClassLabel.insets = new java.awt.Insets(6, 70, 7, 5);
		add(getDefaultDataClassLabel(), constraintsDefaultDataClassLabel);

		java.awt.GridBagConstraints constraintsDefaultDataOffsetLabel = new java.awt.GridBagConstraints();
		constraintsDefaultDataOffsetLabel.gridx = 1; constraintsDefaultDataOffsetLabel.gridy = 4;
		constraintsDefaultDataOffsetLabel.insets = new java.awt.Insets(6, 70, 7, 5);
		add(getDefaultDataOffsetLabel(), constraintsDefaultDataOffsetLabel);

		java.awt.GridBagConstraints constraintsPasswordLabel = new java.awt.GridBagConstraints();
		constraintsPasswordLabel.gridx = 1; constraintsPasswordLabel.gridy = 5;
		constraintsPasswordLabel.ipadx = 60;
		constraintsPasswordLabel.insets = new java.awt.Insets(5, 70, 6, 5);
		add(getPasswordLabel(), constraintsPasswordLabel);

		java.awt.GridBagConstraints constraintsRealTimeScanCheckBox = new java.awt.GridBagConstraints();
		constraintsRealTimeScanCheckBox.gridx = 1; constraintsRealTimeScanCheckBox.gridy = 6;
		constraintsRealTimeScanCheckBox.gridwidth = 2;
		constraintsRealTimeScanCheckBox.ipadx = 45;
		constraintsRealTimeScanCheckBox.insets = new java.awt.Insets(5, 70, 81, 71);
		add(getRealTimeScanCheckBox(), constraintsRealTimeScanCheckBox);

		java.awt.GridBagConstraints constraintsIEDScanRateComboBox = new java.awt.GridBagConstraints();
		constraintsIEDScanRateComboBox.gridx = 2; constraintsIEDScanRateComboBox.gridy = 2;
		constraintsIEDScanRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsIEDScanRateComboBox.weightx = 1.0;
		constraintsIEDScanRateComboBox.insets = new java.awt.Insets(4, 5, 5, 71);
		add(getIEDScanRateComboBox(), constraintsIEDScanRateComboBox);

		java.awt.GridBagConstraints constraintsDefaultDataClassSpinner = new java.awt.GridBagConstraints();
		constraintsDefaultDataClassSpinner.gridx = 2; constraintsDefaultDataClassSpinner.gridy = 3;
		constraintsDefaultDataClassSpinner.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDefaultDataClassSpinner.ipadx = 15;
		constraintsDefaultDataClassSpinner.insets = new java.awt.Insets(5, 5, 5, 146);
		add(getDefaultDataClassSpinner(), constraintsDefaultDataClassSpinner);

		java.awt.GridBagConstraints constraintsDefaultDataOffsetSpinner = new java.awt.GridBagConstraints();
		constraintsDefaultDataOffsetSpinner.gridx = 2; constraintsDefaultDataOffsetSpinner.gridy = 4;
		constraintsDefaultDataOffsetSpinner.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDefaultDataOffsetSpinner.insets = new java.awt.Insets(5, 5, 5, 161);
		add(getDefaultDataOffsetSpinner(), constraintsDefaultDataOffsetSpinner);

		java.awt.GridBagConstraints constraintsPasswordTextField = new java.awt.GridBagConstraints();
		constraintsPasswordTextField.gridx = 2; constraintsPasswordTextField.gridy = 5;
		constraintsPasswordTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPasswordTextField.weightx = 1.0;
		constraintsPasswordTextField.ipadx = 84;
		constraintsPasswordTextField.insets = new java.awt.Insets(5, 5, 5, 123);
		add(getPasswordTextField(), constraintsPasswordTextField);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	if( getConnectedIEDComboBox().getModel().getSize() > 0 )
		getConnectedIEDComboBox().removeAllItems();
	getConnectedIEDComboBox().addItem("None");
	getConnectedIEDComboBox().addItem("Alpha Power Plus");
	getConnectedIEDComboBox().addItem("Landis and Gyr S4");
	getConnectedIEDComboBox().addItem("General Electric KV");

	if( getIEDScanRateComboBox().getModel().getSize() > 0 )
		getIEDScanRateComboBox().removeAllItems();
	getIEDScanRateComboBox().addItem("1 minute");
	getIEDScanRateComboBox().addItem("2 minute");
	getIEDScanRateComboBox().addItem("3 minute");
	getIEDScanRateComboBox().addItem("5 minute");
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
	MCTIEDBase mctIED = (MCTIEDBase)val;

	if(mctIED.getDeviceMCTIEDPort().getPassword().compareTo("None") != 0)
		getPasswordTextField().setText(mctIED.getDeviceMCTIEDPort().getPassword());
	
	getConnectedIEDComboBox().setSelectedItem(mctIED.getDeviceMCTIEDPort().getConnectedIED());
	com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( getIEDScanRateComboBox(), mctIED.getDeviceMCTIEDPort().getIEDScanRate().intValue() );
	getDefaultDataClassSpinner().setValue(mctIED.getDeviceMCTIEDPort().getDefaultDataClass());
	getDefaultDataOffsetSpinner().setValue(mctIED.getDeviceMCTIEDPort().getDefaultDataOffset());
	getRealTimeScanCheckBox().setSelected(mctIED.getDeviceMCTIEDPort().getRealTimeScan().charValue() == 'Y');
	
	
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	if (arg1.getSource() == getDefaultDataClassSpinner()) 
		connEtoC3(arg1);
	if (arg1.getSource() == getDefaultDataOffsetSpinner()) 
		connEtoC4(arg1);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
}
