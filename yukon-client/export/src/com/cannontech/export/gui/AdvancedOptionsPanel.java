package com.cannontech.export.gui;

import com.cannontech.export.ExportFormatTypes;
import com.cannontech.export.ExportPropertiesBase;
public class AdvancedOptionsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private static final int OK = 1;
	private static final int CANCEL = 2;
	private int buttonPushed = CANCEL;
	private ExportPropertiesBase properties;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JButton ivjOkButton = null;
	private javax.swing.JPanel ivjAdvancedPanel = null;
	private javax.swing.JPanel ivjOkCancelButtonPanel = null;
	private CSVBillingOptionsPanel ivjCSVBillingOptionsPanel = null;
	private DBPurgeOptionsPanel ivjDBPurgeOptionsPanel = null;
//	private IONEventLogOptionsPanel ivjIONEventLogOptionsPanel = null;
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public AdvancedOptionsPanel() {
		super();
		initialize();
	}
	/**
	 * Return the AdvancedPanel property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getAdvancedPanel() {
	if (ivjAdvancedPanel == null) {
		try {
			ivjAdvancedPanel = new javax.swing.JPanel();
			ivjAdvancedPanel.setName("AdvancedPanel");
			ivjAdvancedPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCSVBillingOptionsPanel = new java.awt.GridBagConstraints();
			constraintsCSVBillingOptionsPanel.gridx = 0; constraintsCSVBillingOptionsPanel.gridy = 0;
			constraintsCSVBillingOptionsPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsCSVBillingOptionsPanel.weightx = 1.0;
			constraintsCSVBillingOptionsPanel.weighty = 1.0;
			constraintsCSVBillingOptionsPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getAdvancedPanel().add(getCSVBillingOptionsPanel(), constraintsCSVBillingOptionsPanel);

			java.awt.GridBagConstraints constraintsDBPurgeOptionsPanel = new java.awt.GridBagConstraints();
			constraintsDBPurgeOptionsPanel.gridx = 0; constraintsDBPurgeOptionsPanel.gridy = 0;
			constraintsDBPurgeOptionsPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDBPurgeOptionsPanel.weightx = 1.0;
			constraintsDBPurgeOptionsPanel.weighty = 1.0;
			constraintsDBPurgeOptionsPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getAdvancedPanel().add(getDBPurgeOptionsPanel(), constraintsDBPurgeOptionsPanel);

//			java.awt.GridBagConstraints constraintsIONEventLogOptionsPanel = new java.awt.GridBagConstraints();
//			constraintsIONEventLogOptionsPanel.gridx = 0; constraintsIONEventLogOptionsPanel.gridy = 0;
//			constraintsIONEventLogOptionsPanel.fill = java.awt.GridBagConstraints.BOTH;
//			constraintsIONEventLogOptionsPanel.weightx = 1.0;
//			constraintsIONEventLogOptionsPanel.weighty = 1.0;
//			constraintsIONEventLogOptionsPanel.insets = new java.awt.Insets(5, 5, 5, 5);
//			getAdvancedPanel().add(getIONEventLogOptionsPanel(), constraintsIONEventLogOptionsPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedPanel;
}
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G93CBF3AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BD0D55735F6E25E33B334B1935B21BEDFCB9BCDC9240A0A46C4925100A222C1030AD627B8E17A6C9B1A7A6659BE7A1EB35DDCAED70DA89FB1014AFBA18DCDE2C2912BC9A885A3F00D20D8E8C0A4299F8E5C83DCB977633D67FE50583C3576BE7B1C4365DE90B2141965B9FB6D35777A6C3557DEEB1FABA9FEA3A2B75BD0CCC8F6AED17EBE4AA6A46683C2FE1864791C634EC79293517D3D89D0C88E
	369861598EFDE53B08290034569F0436855A29BFA62657617D98F97ACF16B3709202CF9D743D7F72E349E37339F01B73B9C05B1F5E480367D100C140058124C5A27F5BB7CA94FCB4340FAA6F885986106BC924B958D5224317BD49685A15E775D3C873700DB225BF8A7071GC64017BC254DF908CA77F0ED0D523E7EE88459177436618872524D4F412F0C1D23BC4B4982EA089C322774DDF856358F8D1C6BF3582A7A843B5DE117453E41AA516FF438645ADAF3E33B47B5A0368BF6D14A1B0F72D5F59D24E389C9
	057918AC1B0E54A93CE68DC5107447CBC34A52BE1F49A04F57E4F1665EB8D49105F3AF203645F1B1501F894064FCD8BABB261CD5839EEBEDED0345AB587BC4CB1BD336BA6CEE1D4C3F3A41E59E84BEFBA82F062B48AB825E178324C4F1DD6A5F613AF45FDBC832FADB8C79A1F4297C91532173C71AAE537EF73731147A4926A1DC6B195097831088108910370018AA81CED07F8C3B7D9EBC9BFA65A6074D6630B7B81D0D1245F545E191A5F8870DFAE58577295867F0D988E1BEF8262BB208BC9C4C8BC67A1F66B3
	5788771928CF1FA0327C31A1B708EEB1DB5EB5F4ABF3C88BB45D18DEB69B6A958678C800B800B400AC001205DC2F2D374F0C27D78B2C1F4BAEC89D1EDE2760129BBDD649A23A947DE2EE0C25DFB0E063D6CFF89C33CB64993DDDF6AE39E4B89044AEBBA3CF994EAEE476E8DD285905717E1F103C2505235783E166C2744325548F2741FB3662238C3F1262D3F5F836C7AED2BC36B3202FB91A7BED77D12EC32BF1B6A9B8FDB9288E612B4F9B222219BC715101EB5B624058339770F90B082982208E60BA009FA0EC
	91DF5FB707CEBD205F366B56160D5DB1749ABCC53F6C92DA6CEE99F7EC1760328A3D1268A68A4D4BC3A8FB6F08F1FB68FE6A839E97C53B6892F00F436C02CD84C7F2B31A7F98AAC49A17C8FBBB2DB69198581C044F7B6B21E3389E02DB3E6A34883228D1307EEC89ED69953AE0C618G57FC915A7C920DF9D1701E8840EC65F5A22EFCB1B155AC66F679C2AA0267E5F0ECD10258E96DC3B18457B0676F35E1AC913D22DDB60BCE51EE01973752F75FF6C833CB3FC31579A10A47BD5284023BD57C57ED058C8FF368
	2D4C7B9F257D02AC3B2C3D9ED96C90E559EA9F5046FF074A629E317D5CC45963F75047DAC1AF8B002C6A77BB7BB11DFE2D8E4710475909AB27582968A65AE48650C7AC6667650A2BF8FE46AF66FE7ACAB777D37311F924F2CBCBD0BFC548D84C7CF37F62403D16C06754F8DC395ABD41FD9F88710B036FD1AD3674FFE59EB9E374B13B273B506E95CB0869BCC04F92EE779B76F2365F9A853748F7D81B8BD6CC34E8DE46E8BB9C21FD6CF3C7F09F6BF1847731FE4758BEE4F78473A15667F16059627059F5BEF957
	D1C477305DAED22155A1041D732FF422AD2CF62BFC4DEA9990658E6BDD9156F9897A16C077209F5E4316F0DF723D45FD29626E77C95C2D0A2067D9783CA6C34692363ED9CBB4DFE2B25DF3224DC5C93401211A9C9E3B4C70DF237BDB41AB7145FDA7B86D709D7447A1F1D8213BFF8793C92FA0F9C4D537E861B05A58E53541D26106D5F24213B775B9E1C98CB1E17E588DCF89A0074EF732269857FFE4FE738946BE66B725B24AB59D6843F1FC8C771BDF5FA9556DD745E11A9C12444C2F2E4BCFDD85380E629D0F
	888CF0C3DFD6F579392BA8202FCB510B757F5205FAC93AD8109803FA7955BC914F8A06FB04626643627DAC171F3DD78A74F92C45DD97F48D917216AAEB37B4705C7A0B8767BD8F787625DCF727DB9F2B9A5CEEC71F159E8B4AFAFC2796F314E0B9E59FBD54B0272C7248D68C64028C7B456911E9CAC9D8CE79980DF5B817E5A94FA9D78A388EF3209D83102C4A7206BC5A17AA64D76159EFF539A1973208FEEE4F77E4348B1ED18A1A67EEFF545BA9AE3DBA241DB2947BE40D3253773CA8C771B2E2FAF79917ED1E
	DC2833936419F0A48F40AAF2F9BABCB94C371576B5CFAE77ED42E3E3784D405C27EE1996CF5B170D0E2DAC47B9530B7632ACE3B9CE2E0ABFC771FE9D1E65BE89543E588E033E3D4B78D97CAD9F6A5579B41CD7CFF33DB2BDC7F4FA35BB2CF6D955E92E97772743E59D305AB96EC90A03AC1B86ACF61EAD24EBAC431CB3G98AE2C076682500EFF1A1FBFFF6AC55C9EE89B17F31C1962AA205D2C62568BDF758CC19E01B864291DD399DF19C730F3AAD7F9B2DB4D76613A785576F7E9FBD899FB0C2C39E922BEBC6C
	7000515316235DF629F1CE0276F400A8003865ACD7E0FBF2118FE3C9BF84B6B8E6BA1CE29F09F98647BE5E434716C37BACC0A7008CB06B999EA32D27F13DF90CE4BB154B730D8A0C85EDC3CD58A7B5FAE499F3C724E0B1585B2B0F417B1FE1B215BF8B3CG984D423AE0B42D505F83407CB377962FDF7D000BFE166BF53A8773EA9AEBEE8BDE21CA70C1E9FD51E535B48A832D54CAAC26DC39F5D0993F67D99ED36E51B94BD7C01E89503E021FFD7E37F91D9A7544EC52BA6DD2501ABB3D7334E1F085730F3095DA
	BC1020CEE5BC5775F01E0950CFE29177DFBD45817EF08A7E29C236347F24206F4778718E51777FEDC4BF4A3B41206F7F4608FE3451A9E5BC53ED7D6F39EE7B57C792E9E9CD487B0466D825CED51E4DF5A74879176F67B03B7F6749923D5D7DD5EE9FB8D6ED6DA55D5D40823ACFF16F4C0D65B63F5C04B65F896DFDGF94F81D05AFF2B63B13F62B9CEBB6FA852DE07368C90769C5F6B07EFCD2E06AD1D6438CFA7BC0E455AABBDA87F5C67D82C4DD671DD949F2D43B39F7F1A6A633150D77EBC0F41FEAF5AFABA34
	A3G168324G6C8548DB49E372DB7EEAB2CE5D5869F036C272AA69CA9A265F461E4955762B8EF0DF0AF97687E479BFE5E59F8E61CB65AB198F1DD71E6935A7A81E69F7580F7AB921AF8CA09AA091E0A740BED53FA1FFA5536F0245AAB7483250B7C8CFFC2EDC17D574E9E88CF0CC37A53DFA5D8E4691D3D91C96672B63C21D1FAB291F1A63589A9DD0716BA97E3A8E4F566E2F940FED997A5262382FE6560DBA3FD7694E6FD5217817665373FBD56079DDC1717ED50167777C236A798DFDFBD7F17E9DBDC475636B
	3A77FF3E75D54F327177CD441472D0EA89EF681AEC06B7F8CD76CDEF701AEC16F76C1AEC0EB7F4CD7638D7DF13B13F7E6BB046624E5544648418859089903B1A7B74E95F6F18CF8771678BD024B5B968AD0E4C79977BC36BFA42BF7E5DCF059F7362DE871C51B61CDF4D0B7D54EF9DCE0AE438D3FE349124F103B2A3D5709F78B1CECBE2BFC5333CAD094679B450ABF3B54F65120690379F5A85AFF0DC0B0D67F7E7DF607E19C0774DF4E8C782A42A78CD941F896D02F8200F6778EBCD68D3C8B71EFF70B9A76E37
	CE4A6340835063791DBB01799118ADF26A39FD3A6339FD6E1F637679C255770DFC44A5C3BBC3453DC7F1D950AEC96038C5C7C36DB59CBB59BD16912F4FFF13C272986B7EE5BC98197FFEB69C5A47FB064777F16BB02D8741B7BB071D22F217B2CCEB127EFE37286EA577F0010AEBF137BB5CD6ACE0D9CE3B3B57C477090BDE7A1993F0ED32FA719E22A60167443B07C66761D9C3BC0F1D1140727088F846GE02DC256A0CD0F4DBC06ADF8BE1023C0B20CCD87B82E3477A5E8B9831363250F51A6CD9DDD0DD6C902
	E8257FC243E90E77E30E2EFCEF9AC1407ABD87701BDECB5BDFAE2059DB9DA3A7E1B93A512C4F511B57B01D426099310667715B8B75B44B951AACF8662D65B4DFF874B495EB994DA0BC3FGE0F62C2C67B43356AA758CBC635632FA0F59F15639716C48ED087E1586E3B357B2BB528F150901F63C5F0871C6DF45B07C8B8368CFEDC38C571C083C2F676BF36C3CC4A657D9FC527E7DB702754FCC643E33F688C5D03E09097A3B076AFB4410C30267D0F39359DE0C61CF5B89656CAF17F85E1881FDE56BF8CEBDA871
	7A23F39D4F293FCDE94320BD97A096608DAA9364C6C47B8B06CBDD075FF0B791CD765DF9BC7FAB1B7FAF443CADAB7B2D903217A4B119871334BAA9F373EEA32BA542FC9F12713FC10672F126EB1D195F09C5A4F1DDCF94222EE9505E8BF0F0BD2F996FBAC35DCF6979855BAF0CD7E19167D53E1E73DAC76F893B216D84182172AAF86F23876095EA6FB1FE3F75293A297C767F196A866D3DG8713F91D5CD94FFD219CF05D495C976654F3DF18194C67C822340950CE8748CA8E668BFBBF8E06E33E70E783A17C8E
	B63F2314842D875946A1FC1D9E4B67F5DE4186DEEF5F7FC47F0DBBB67F34A1D49D513CC171238D1A9F452A757699FA2704E3A3B7705A69EC8E466D748DBC67B25E451C2BCB382DD4C8D8CEB8DCB46B82E49354CB9D107E598778197736B4391A716CA44759A6BC0E55852F5AF0AD77ED882CB50FD1BC5998D8EB4ED1E3C69E74B9B77235F871EF7AF8D4B03B5C40478572CD5D2854F29B836FC21FB2E2ECAEFE11182A81BA81A400E9G61GD1GF1G29G19G469448E3GAA814E833483D8G7CA9FC4DDE4BBE
	B9461AA917BDB4D9F6896CAE12593241BC16AD43D2C255DE0F5038911192E84BF00A0FCA8934651B055C164B212FEC934F49DEAB9A0B7F34CD217857FBF03ED91B8279FFC271911B82793FA8F37ED150D730D94D9DFDE37117B707626F2C4379E6EC8E64BFCC7133B68772B754F17E9150E7FC09737F3CFEF2FBE0F012633EBF61F14A1D78B914FF7025C0FDAFD23C7CD2203EA9EA0C1D86FD992A3E61794178B727B2BBDFCF9DEDEF36DF2E19B0DFF0C2FF9840DC00D8001D295C7F9FB20C7B1B0F8B7EB1FE73D1
	23FEC3582F1321E08BFB2F00E78DC06B962D0F712D49419C57BDA29E965808EA6FD25D7B8F24293FCFD8B125BC145F9EA0ABC57F95B93FA7A1628E187C929CF3ED3D3745BE39D398D047BCEF8CDD3FA498472FDFB69A035768294651B57AF6E3709A7DA7C6FD0D4EF099B4579A00D8592D527DCC45FD2662DE21F3A26E1A55A28F2A78FDD4AE44B70B4810637F1B4A652338EE85779B9D6EB385E74CC5DF8215879DAEBE76D0AE1E4D7D1054317691CAE335C3C9265A23A897E534BA851D5C2F2BB8141B554E5DE6
	DEBB77EC617B71DB051AFFFED7777ECE61547B6A79A96541F47EC39157B9DA5539492369592AFB5FA2CF3D4E2FCCA98F267307BE2EB3594AF57656E9FAFE29FBBFD8B775BA3F798F60E1BE2769F4C5773E35FE6AF97FE2CAF9A85F02E9CD508E6B5933155F05959770F5163672F37016A9F4EC6DB7853F6B15CC416FFADD2631FF63F557946AAE184957C6EB0DD810ADCD15795FCFF1BDB2D55C65EA2EC7160A7B57C144B5EF83FD37F15CA3E574FBB834433671BB8E07195FF1C4EC9B1DBB845E9545GCD62B66D
	2EE867B6ADEFE173190B78FCFB9EE03E7DC0D310264DD71196B85F959F1F2FBAED7C794CC0532D1BCFCA8B3C03D9130F67018C0B486A4B2D2C64243C5EAF986B3EA7A7G7CB037DF610573070D6255FF933FFBBADDE23F68F2099676F3A81212063C3E24EB9B8E6FF38118AC26D7B5D9FA2B4712A5F79CD90A37874A92B308B52BCD70DBED9E9B15044911CC7D249C68AB37733B23A74AA82D559EC07BBC751FEE2033EC67B5FF0419575AB337CF343637A39B25E6779471F992A7BC4F23F95ABCFF7771F9480E09
	4E93034BB5463DD466D16D7DA93C5AD5EC7B13D37F87CF3B68AB67BF8F28B2DBED229DFF047A6EC7E7AA4B7F7841C9FE1F956153573EB52FFE9872F7C0B5BB145FAA6F506E62986F62A2CD0E0F0EE9F2CCAFFB90B958384F0B35F195D79FE49C0B5573C6543B53C136709DE35F7D33F1F7C75429D1B0A66E0146BDB4223ECC05B1FB469947F26C834769378F20AD79B10F6D3FA8F81F042E715BDD8E2768120799167ECACE0943315447EBE01EFB869E1BD7D1DCBB6036E7F35C23C51CEE03CA776DA2CE7731CAB7
	5BB71A6EFBBEBD9D13B76FB86A231E8FCCA4256F58F1DCC07C2D25D5102CF769CF63582D920B8D425BF9DABFCD33993E4CFFC44373B41B307FEF633FD8873C23FECEF25DAE122F7AC28BCD16533DE7EAF3DEFEBC47C00CAD44440A0796E2448A2005645282210564C07160F71200BF779D7277C09C44E0E2E2D761B2B134DDD6E9F275B0A9AC7E95ABC0C0132251DE5D704D6D5B22EE190B8D44G4A8C0ACC7ADC9F5A094A0FCE5492E2C6449E9AC12336589EBF7925FE0FE7BD7CFA48BD5E33CB797FB43B826F37
	FE53027E908D7864DD5CFF5BDA7E904AFF21D66C308912A48B52D0035D02F5E36330ACEA390871924E6F66B4CAA76BBBFC89653E2D9B737FD0CB87888F78AF3E8692GG5CB5GGD0CB818294G94G88G88G93CBF3AE8F78AF3E8692GG5CB5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC092GGGG
**end of data**/
}
	/**
	 * Return buttonPushed.
	 * @return int
	 */
	public int getButtonPushed()
	{
		return buttonPushed;
	}
	/**
	 * Return the JButton2 property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JButton getCancelButton() {
		if (ivjCancelButton == null) {
			try {
				ivjCancelButton = new javax.swing.JButton();
				ivjCancelButton.setName("CancelButton");
				ivjCancelButton.setText("Cancel");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCancelButton;
	}
/**
 * Return the csvBillingOptionsPanel property value.
 * @return com.cannontech.export.gui.CSVBillingOptionsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CSVBillingOptionsPanel getCSVBillingOptionsPanel() {
	if (ivjCSVBillingOptionsPanel == null) {
		try {
			ivjCSVBillingOptionsPanel = new com.cannontech.export.gui.CSVBillingOptionsPanel();
			ivjCSVBillingOptionsPanel.setName("CSVBillingOptionsPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCSVBillingOptionsPanel;
}
/**
 * Return the dbPurgeOptionsPanel property value.
 * @return com.cannontech.export.gui.DBPurgeOptionsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DBPurgeOptionsPanel getDBPurgeOptionsPanel() {
	if (ivjDBPurgeOptionsPanel == null) {
		try {
			ivjDBPurgeOptionsPanel = new com.cannontech.export.gui.DBPurgeOptionsPanel();
			ivjDBPurgeOptionsPanel.setName("DBPurgeOptionsPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDBPurgeOptionsPanel;
}
	/**
	 * Return properties.
	 * @return com.cannontech.export.ExportPropertiesBase
	 */
	private ExportPropertiesBase getExportProperties()
	{
		return properties;
	}
/**
 * Return the IONEventLogOptionsPanel property value.
 * @return com.cannontech.export.gui.IONEventLogOptionsPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
/*private IONEventLogOptionsPanel getIONEventLogOptionsPanel() {
	if (ivjIONEventLogOptionsPanel == null) {
		try {
			ivjIONEventLogOptionsPanel = new com.cannontech.export.gui.IONEventLogOptionsPanel();
			ivjIONEventLogOptionsPanel.setName("IONEventLogOptionsPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIONEventLogOptionsPanel;
}*/
	/**
	 * Return the JButton1 property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JButton getOkButton() {
		if (ivjOkButton == null) {
			try {
				ivjOkButton = new javax.swing.JButton();
				ivjOkButton.setName("OkButton");
				ivjOkButton.setPreferredSize(new java.awt.Dimension(73, 25));
				ivjOkButton.setText("OK");
				ivjOkButton.setMaximumSize(new java.awt.Dimension(73, 25));
				ivjOkButton.setMinimumSize(new java.awt.Dimension(73, 25));
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjOkButton;
	}
	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getOkCancelButtonPanel() {
		if (ivjOkCancelButtonPanel == null) {
			try {
				ivjOkCancelButtonPanel = new javax.swing.JPanel();
				ivjOkCancelButtonPanel.setName("OkCancelButtonPanel");
				ivjOkCancelButtonPanel.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
				constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 1;
				constraintsCancelButton.insets = new java.awt.Insets(0, 20, 0, 20);
				getOkCancelButtonPanel().add(getCancelButton(), constraintsCancelButton);
	
				java.awt.GridBagConstraints constraintsOkButton = new java.awt.GridBagConstraints();
				constraintsOkButton.gridx = 2; constraintsOkButton.gridy = 1;
				constraintsOkButton.insets = new java.awt.Insets(0, 20, 0, 20);
				getOkCancelButtonPanel().add(getOkButton(), constraintsOkButton);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjOkCancelButtonPanel;
	}
	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(Object)
	 * Return com.cannontech.export.ExportPropertiesBase, which is populated from the swing components.
	 */
	public Object getValue(Object object)
	{
		ExportPropertiesBase exportProps = (ExportPropertiesBase)object;
		
			
		if( getExportProperties().getFormatID() == ExportFormatTypes.DBPURGE_FORMAT )
		{
			exportProps.setDaysToRetain(Integer.valueOf(getDBPurgeOptionsPanel().getDaysToRetainTextBox().getText()).intValue());
			exportProps.setRunTimeHour(Integer.valueOf(getDBPurgeOptionsPanel().getRunAtHourTextBox().getText()).intValue());
			exportProps.setPurgeData(getDBPurgeOptionsPanel().getPurgeDataCheckBox().isSelected());
		}
		else if(getExportProperties().getFormatID() == ExportFormatTypes.CSVBILLING_FORMAT )
		{
			java.util.GregorianCalendar calTemp = new java.util.GregorianCalendar();			
			Object date = getCSVBillingOptionsPanel().getStartDateComboBox().getSelectedDate();
			
			if( date instanceof java.util.Date )
			{
				calTemp.setTime((java.util.Date) date);
				exportProps.setMinTimestamp(calTemp);
			}
			else if( date instanceof java.util.Calendar )
			{
				calTemp.setTime(((java.util.Calendar) date).getTime());
				exportProps.setMinTimestamp(calTemp);
			}
	
			calTemp = new java.util.GregorianCalendar();
			date = getCSVBillingOptionsPanel().getStopDateComboBox().getSelectedDate();
			if( date instanceof java.util.Date )
			{
				calTemp.setTime((java.util.Date) date);
				exportProps.setMaxTimestamp(calTemp);
			}
			else if( date instanceof java.util.Calendar )
			{
				calTemp.setTime(((java.util.Calendar) date).getTime());
				exportProps.setMaxTimestamp(calTemp);
			}
	
			exportProps.setShowColumnHeadings(getCSVBillingOptionsPanel().getHeadingsCheckBox().isSelected());
	
			//Even if the delimiter textBox is longer than 1 character, we only take the first character.
			String delTemp = getCSVBillingOptionsPanel().getDelimiterTextBox().getText().toString();
			if( delTemp.length() > 0)
				exportProps.setDelimiter(new Character(delTemp.charAt(0)));
					
		}
	
		return exportProps;
	}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
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
			setName("AdvancedOptionsFrame");
			setLayout(new java.awt.GridBagLayout());
			setSize(358, 407);
	
			java.awt.GridBagConstraints constraintsAdvancedPanel = new java.awt.GridBagConstraints();
			constraintsAdvancedPanel.gridx = 0; constraintsAdvancedPanel.gridy = 0;
			constraintsAdvancedPanel.fill = java.awt.GridBagConstraints.BOTH;
			add(getAdvancedPanel(), constraintsAdvancedPanel);
	
			java.awt.GridBagConstraints constraintsOkCancelButtonPanel = new java.awt.GridBagConstraints();
			constraintsOkCancelButtonPanel.gridx = 0; constraintsOkCancelButtonPanel.gridy = 1;
			constraintsOkCancelButtonPanel.gridheight = 2;
			constraintsOkCancelButtonPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOkCancelButtonPanel.weightx = 1.0;
			constraintsOkCancelButtonPanel.weighty = 1.0;
			add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);
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
	public static void main(java.lang.String[] args)
	{
		try 
		{
			javax.swing.JFrame frame = new javax.swing.JFrame();
			AdvancedOptionsPanel optionsPanel;
			optionsPanel = new AdvancedOptionsPanel();
	
			frame.setContentPane(optionsPanel);
			frame.setSize(optionsPanel.getSize());
			
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
	
			//set the app to start as close to the center as you can....
			//  only works with small gui interfaces.
			java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation((int)(d.width * .3),(int)(d.height * .2));
			
			frame.show();
			
			java.awt.Insets insets = optionsPanel.getInsets();
			//optionsPanel.setSize(optionsPanel.getWidth() + insets.left + insets.right, optionsPanel.getHeight() + insets.top + insets.bottom);
			//optionsPanel.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JFrame");
			exception.printStackTrace(System.out);
		}
	}
	/**
	 * Set buttonPushed, valid values are OK, CANCEL
	 * @param newButtonPushed int
	 */
	public void setButtonPushed(int newButtonPushed)
	{
		buttonPushed = newButtonPushed;
	}
	/**
	 * Set properties.
	 * @param props com.cannontech.export.ExportPropertiesBase
	 */
	private void setExportProperties(ExportPropertiesBase props)
	{
		properties = props;
	}
	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
	 * Return if object = null.
	 * When object instance of ExportPropertiesBase, setExportProperties(object) and set component's values.
	 */
	public void setValue(Object object)
	{
		if( object == null)
			return;
	
		if( object instanceof ExportPropertiesBase)
		{
			ExportPropertiesBase props = (ExportPropertiesBase)object;
			setExportProperties(props);

			//start by 'hiding' all options panels...the necessary one will be visible'd in following code.
			getCSVBillingOptionsPanel().setVisible(false);				
			getDBPurgeOptionsPanel().setVisible(false);
//			getIONEventLogOptionsPanel().setVisible(false);
		
			if(((ExportPropertiesBase)object).getFormatID() == ExportFormatTypes.DBPURGE_FORMAT)
			{
				getDBPurgeOptionsPanel().getDaysToRetainTextBox().setText(String.valueOf(props.getDaysToRetain()));
				getDBPurgeOptionsPanel().getRunAtHourTextBox().setText(String.valueOf(props.getRunTimeHour()));
				getDBPurgeOptionsPanel().getPurgeDataCheckBox().setSelected(props.isPurgeData());

				getDBPurgeOptionsPanel().setVisible(true);
			}
//			else if(((ExportPropertiesBase)object).getFormatID() == ExportFormatTypes.IONEVENTLOG_FORMAT)
//			{
//				getIONEventLogOptionsPanel().setVisible(true);
//			}
			else if( ((ExportPropertiesBase)object).getFormatID() == ExportFormatTypes.CSVBILLING_FORMAT)
			{
				getCSVBillingOptionsPanel().getStartDateComboBox().setSelectedDate(props.getMinTimestamp().getTime());
				getCSVBillingOptionsPanel().getStopDateComboBox().setSelectedDate(props.getMaxTimestamp().getTime());
				getCSVBillingOptionsPanel().getHeadingsCheckBox().setSelected(props.isShowColumnHeadings());
				getCSVBillingOptionsPanel().getDelimiterTextBox().setText(String.valueOf(props.getDelimiter()));

				getCSVBillingOptionsPanel().setVisible(true);
			}
		}
	}
	/**
	 * Main function for caller JFrame to implement.
	 * @param parent javax.swing.JFrame
	 * @return ExportPropertiesBase
	 */
	public ExportPropertiesBase showAdvancedOptions(javax.swing.JFrame parent)
	{
		javax.swing.JDialog dialog = new javax.swing.JDialog(parent);
		dialog.setTitle("Advanced Export Options");
		
		class DialogButtonListener implements java.awt.event.ActionListener
		{
			javax.swing.JDialog dialog;
	
			public DialogButtonListener(javax.swing.JDialog d)
			{
				dialog = d;
			}
			
			public void actionPerformed(java.awt.event.ActionEvent event )
			{
				if( event.getSource() == getOkButton() )
				{
					setButtonPushed(OK);
					com.cannontech.clientutils.CTILogger.info("OK button");
				}
				else if( event.getSource() == getCancelButton() )
				{
					setButtonPushed(CANCEL);
					com.cannontech.clientutils.CTILogger.info("Cancel Button");
				}
	
				dialog.setVisible(false);
				dialog.dispose();
			}
		}
			
		java.awt.event.ActionListener listener = new DialogButtonListener(dialog);
			
		getOkButton().addActionListener(listener);
		getCancelButton().addActionListener(listener);
		
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((int)(d.width * .3),(int)(d.height * .2));
	
		dialog.setModal(true);	
		dialog.getContentPane().add(this);
		dialog.setSize(360, 300);
		dialog.show();
	
		getOkButton().removeActionListener(listener);
		getCancelButton().removeActionListener(listener);
			
		if( getButtonPushed() == AdvancedOptionsPanel.OK )
			return (ExportPropertiesBase) getValue(getExportProperties());
		else
			return null;
	}
}
