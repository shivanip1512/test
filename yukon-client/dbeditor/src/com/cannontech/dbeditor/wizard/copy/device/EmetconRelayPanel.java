package com.cannontech.dbeditor.wizard.copy.device;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

public class EmetconRelayPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JRadioButton ivjARelayRadioButton = null;
	private javax.swing.JRadioButton ivjBRelayRadioButton = null;
	private javax.swing.ButtonGroup ivjbuttonGroup = null;
	private javax.swing.JRadioButton ivjCRelayRadioButton = null;
	private javax.swing.JLabel ivjRelayLabel = null;
	private javax.swing.JRadioButton ivjSRelayRadioButton = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public EmetconRelayPanel() {
	super();
	initialize();
}
/**
 * connEtoM1:  (EmetconRelayPanel.initialize() --> buttonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getbuttonGroup().add(getARelayRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (EmetconRelayPanel.initialize() --> buttonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getbuttonGroup().add(getBRelayRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (EmetconRelayPanel.initialize() --> buttonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getbuttonGroup().add(getCRelayRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (EmetconRelayPanel.initialize() --> buttonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getbuttonGroup().add(getSRelayRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the ARelayRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getARelayRadioButton() {
	if (ivjARelayRadioButton == null) {
		try {
			ivjARelayRadioButton = new javax.swing.JRadioButton();
			ivjARelayRadioButton.setName("ARelayRadioButton");
			ivjARelayRadioButton.setSelected(true);
			ivjARelayRadioButton.setText("A");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjARelayRadioButton;
}
/**
 * Return the BRelayRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getBRelayRadioButton() {
	if (ivjBRelayRadioButton == null) {
		try {
			ivjBRelayRadioButton = new javax.swing.JRadioButton();
			ivjBRelayRadioButton.setName("BRelayRadioButton");
			ivjBRelayRadioButton.setText("B");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBRelayRadioButton;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G39F4FEA7GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBA8BF0D457F5EACD9A1AF19B621214A424E69AB5259EB515DB5923A472D86E60D613F1DB5C5214F19D1BB8F4C227A4A34C470288F375DB96340004E43CE091D60198A9462B5FCA08C5291110E28B4BC6C58A96B10EF95AFD52BE7476235DA750E7C5FB4E7D3CFDAB3D370B6934BBF3663EFB4E3D67774EBD775C7B16541ED8D5D1D2DACBC8C985613F33A5041CEAA664B3EB3E7D930133973A0871D78F
	D0CD7E3C3D9CDAA75006CF5413AA7242E9BB744FC0DF7DC9BDF9951EEB488FCEFB5C70E0A1478134D5130F1CCFAD67F1C94859CB7BDB5A9C509E84E8869C36F1E82B48BE0F136307B9DEC0C9A9A1B7366018D31EBA0E4B07B16B816AG5F3C050DFF875AFDD4726465CEBEAE4F7EF962EF0D588EA09F83CF812B795C27F88B1A5A3497D156FB9E871FD38E7DAEGB43E0C38DA4EC25BB5B8B17E1F3EF0506D13C221F0C813FD8177EDE5D60A7A5DFE791662135DEA506B6D8B4A1AAF9C3AA22B524C209412D5D2C8
	F9FF5EE3273C884905FEFCAB623CEE1467016781G7FD601EFEAC2FCBEBC2F87A85CAAECFC612DAEEE039A789A5958F447F64842C67BB6E6DB7336040D03BBC6CA0F527731E7947D2E82ED99C0A6C08140A6G7BF697F90D4601ED746750F60FEA974341E0B8549D0974287E68FBE13F2C4233578B940E3BA27B42D1BF616FFA48F346C41FD44066C91FF89E73496AB6715ED714FE1994FDB3E85BEB6213C85E0C4D351D7922F3FB42A7CC7617ACE50FECF725107D81B14A1EBE713B368A93593DE33F31291CCF06
	C1F64EF6FC57DF27B1308A1EF19E4EE778EFD0FC11814FE2748245E35F3143C5A2BBC4ECACEF903AB89E789CD9B92E187AE170EFBFE5DB37036930F947C2DD9E27F167FC09492C56F5D9CB713D86BC53251D62319F81DAB1G0BAF3B0CEBF6E9914496C09E40BA00EDGD5BBDD6454CE91E317C71B6EB24686252856B32528FEB94A675AFDC72015273528549F0AE9D248A7DF15220AB42A4AB14247D47B508723C9730E78B076FDA063EDB9A4C7A5CD8907003B844BD60E46581893BE34499715A9F5D8894AA0A0
	98A1026FE9DF8D2E9FA9267DAA6217B4B9B102511BFC78CEEEC9D701A3B000E7661776B16AC7307FBA002A7B61A14A6FDDB98A69426B9D8807A726A243E889495B09FEFE18660E8CF8DE31D37838E59CF1E4170BB8F789BE5F739DB67069D5FC283E941D91EBAC47CD73BD0C9F58A56266C7A3A2EE0A7E6D4B2460511B26F103605F45F3772E056B67F14A97F54B5CA5F27EAF5DA3F7390E974219DD66EB8EE10053180C0F6FD9C6D98ADA85EF191F9F8C60BBDB83F2B7G109F8BDF5779DDACF6FB2418AC2275F2
	9482CD76A7BC4F74FC324945FDDA4679A39E7DDF89007B54A15D266F5031CE10F381A0G308CA097E083003D98763EE2616373CD99F74177DE7C73497377EF28AE2974287EFF50053D1B43B24A13EF49A12DD70E48A1BFBC44B86D280CFA713CB19656671C20788864190B3030E3BA7EE7B26AADE9DAD4991D5264A1D9531450F8027EA615951B9A0DA4D2141065115197615B10E7747167E4DC23D0A104E49AA47A94CEBFCFF3042C4AC1D07AE2F8AA24B1FC779856D493728C4FD1FDE3B602B9C61D42FC476A14
	EC897D9D09AAC188BDCC19A42F9873C701DEE751BD8DF0D8EFAD03B633D864015BA7C59E70DF7D82E91E7F3569DE362C7ABEDB79EE36861D3B93F9006954B1EE2EFB974DD59C9FE33A6EF523DF97D7CBAC06EAC8400F36004B35F0686DE8F8AA425763FA85E5F4026C81G7FEE319EF34711AEF29FDF0897432A4A5C8C34553B5197E5CD3586FB623BDDFA6D49787CC9208A5F27BCB9A5431602C978DD5D67D901438BE8D7397F99BDA700BA2B067CBD78B24ABC58ACEA58425DAC075BDFE6395275B2133D0D0EAB
	50472D04BE66009CE857GEC84F0831E717BC20B98F7E1CFAAFE17DA453811BDAC17C4204D583310DFED1B9837AAA53F82FDDC6E9E161356C23BE1913F87741A9DCF84567CFE59AE46B9E19C4DED50DEG907CF82CFC0C7519D97DBDBE253827B485F7BBC903859D1952E898311A3B355D4EF95CG1E2C369E684478D9817DEC0082G3E8F3EA7EA6B9BBF7CE362FAEE46727C308166E0DBDED22F47FF117B9A2F2DFFBA01B2FB01B68C406B8145383FF747E2E11FC2CB8BCEFFCEB12E8F067B3E329F5AB1A59A53
	7AA101CD0B987BF78575455A0323390FF63B0F8B9F5D14EEC9536E58ED48D3DEEF0FE185093A75CB2DC6DB63572F5BAAADEC8DF09B179AEC4D04F3842B7F4EB6BB78FAADGBAA92D67EB770FA8DE89A91AA2294A2C5C5048F4EC8E7D94DA496FEF98B068686E9E0DC1B676E9CC55730DD7051EAF9C394075486A7D8AD97B486F181EF72A1E7B142D331469F723B4212720B3DD37683A2E06B19BF4DDC7DAB02FCA517196169F761DB36687E7990B5F5EB2B1BE030E5782CA0C6918FDC16818DB7AD992B8F33F6519
	EC240C6916D1E6256363ADC24735B046DEDE4F7131D6013FG3869F2214BB32D493A2F2BB453BDDB67730BB641E78D600AF57C969D6F2928A7EA056E9BCFB27F97674C78E7D688BE5F7288BE3900AB547161F601EF3055139BB6417FBB6D497CBF721A71DF8163D90DF136FD61B9AD4BD62FBF679B1E853073CBBEDD736B000E6F25D2477FBD45EFB360193ED7A89E7B35FB415FGECEDDF8E62BE7AAC740BG2A6C752481609D0000DD2C75491BEDA44D19269FF2D7B4A429C3C9C503B8DBCC20EFE01F677DB7A6
	F06D50FD1E081A7431907AE98948DC85108B309620D05761AFA7526A50B7ED2A837373AF1A531D452B76A57B1A4DFBB76DBC4F02F94C1E1A886E8383C08BGE4G2C844887F896006C97F69175FCDA3B30689B8EF753CD7F6242AA0B6B7952BD5AD75112EE1EEBFF72BC16770E7460455C625CFC690A64D742AC6D91FECFF4611C1D4B6B5CAF626E8FA651BF91685F8F1089108F309100D45613D31542BFD7541EF47E510F3A60A3D153AEF69337774934761A7B691F524E6B2AB41BB753FAEF72BE1DF65E7082F9
	4C57158ED19F2815A247DCEC3BB79D7EAB6D3CCCD31BDF3AC7F995E96F4B72CCEDEE0C8B1B5769B6BF111617398E7F10F61E43E1B6EFEAD19E3FBBF977251DF74AE1E673F22F30398B686CCE65FDDA9BEA50DF2A630E1EC5DC967457B804EF7E2693F36DA6685B8FC03E87F887A0F0C05057D1BA5668993C96467AB5C3270FF4AE3C234039585A8FA4F495FCD359E604CD5CCEA6E3F2110CCA1248BFC69F6E3BCB7E88620E046516112075197A63207919DA8E1A1F29A702558C8FBC0CB7FB029E8B26BEF34F852D
	4F5CCF062C75FCAAE42E67BF064C75DC9FCA2D670610151E47297FFE203DC2CC4E7A6FC386A6621C2BB56F63E37A3470FBADBA6F6391EBBBCEC54C6D78F9844FAC3170D454A7AF5435A56254E909045F2F49C1D4070D7152799A94E472A2DB3B69FCC6331AFFA9E2658FE62B3B25125BBAB8968D8787DBF87C761D43F53DEAC17C6770FE01493AE0723EB2E96D234CC9F39FBDB4E96D2307A7537B681B1356BEFAE2B23D0F3EBDC96FB5943F9265B7934CB745AD46B8E855FD165D1A6C33D66E33AA07D9AEFC167B
	2A5044E70C5FEC1BD1CEDB625DF8126534F1B96E38191C72030C3F7320151CA20FD10EC747E735A74B71F0B9DF741A49B94379DF3014A33763BD18AA4D0427B4B8839C446F1A71F67D3BA274D50065G2BB98FD613BDFC9B759803EA88C2F4A8A27B484AC3B837792C18EB07FE9DC09740F50078A1F1EE797DF398675D7456AA290463745F2BC6FA0FA57D375EC77AC5CB7A274B11BEE4C957DE473B81CA9E10C671520C1D1FE6E70D67270D07785DCB15585F2E535A328E7A4DBA6EC33AB776C2FFD84765FB45BE
	F8C3471DF608F159BAEE29C30C4B53F1FDF1B14ED1ADF0DF0B0BF1CE9D172D4B8D68386A73E21CD6AD4E0E196D621B6C9A409583307757F283EBE23D26F6CB37B52F776D28626F114687E8B4303DFFB35D67F13E67303883BA5601BCD568AF81D8F9D85C814D1609FB1162462F9255765B26F7C0F55F0A58F28E33D8DAFFB8F1166F5AB1D24AE4DEBDABE456565413019A0C2BA5E70D6F865F6335D2C26F38987D51A4BA3E7FC6E224EFCA22233E0FA54DBF56E924230F6A79FCE65B9B9F8A5B06BB7E14B45CB7E6
	F93F353C0659144B5B4C03CFD7A4564D56B1A7B1BD4B50C8E476BFBCAB6A2D429A31E6E2B476E2B25E474A7E4EDA5479B36744B77106DAB1AE6A2577AB10386335E84167700A0BEC23637F455B4BF54C2FDD1C8F44F790816BE08C42C9CE5BD44B4E79880C37B213607D20C3706EFA451CF7C58A5E032FAC645DBD1B607DD8DC703EEE41FBEF8A5E11C53C0F4EA5F8B71C973C63963C7725603D4C401B6561075256588B014D4B3D47F9DBBE71BCF6F733192E6115867D99FEBB452FB660D99EF1F508BC1283B467
	91F136AE1E43F80EC3FF85C0B6C08140A60072D74539FAA25ACC521C2B07431181A8C4D4C319486C7B706C3D7D2FE16F30D82F4B7F6281B222FEE4F9C76BFC153D77CE5EB69336DE19FD1F1DC77B82C0DB82309AA09FE09D40E65D3E3F0A1EE1767579952DDB53A4DF005EC48B632EAA726D849AB7CBE65B2333E63691A7532356E935E7FF4056F8F4CAE636FECF7F9F857EF70764ADB37D76045040F9F671362866698A4623295348A36778C736928B9E9A1F1B61CC64EE16376C67C55E4A815A892AAB5481A471
	E36B6F097FA67AFB2A0B097734796DAF927FF727EDA7AD64EE963A9FCD3E7FE532576AF7919A50191CB769BAE7749F696776AC1DDE91CF503FFBD250B76A7497A91D3D2B97BB924FBFB6BC3F5EF12F5F506FFE0D3F75FFAA0345F0C55C7AAC60081B1F858E474D4FFD4EF86A7327ABEEFD7EBC9DB7FE73E5EB702F67F1EF2EBBD6CFBA812E83C4G169C936B6F11B8FE9741FF3BACDEFBFD7EF1BC0B60BFDCB4A1FF453C352D8F4E277FDF4C6AF92C9FC743D0D486113F40FFFD9E5F17960ED0A44F8D73682328B2
	9E5058D00E7F46BC461BAA0FD1B42B5F7EEED654F42B0E091A4EBDA7EA3A2CE3A267BEFF87F14550F73EA6FCF0694E9B50DE15EE726C0A29A89C25DEG64E0B49C11235A8C43522F2E1C57B3F4ADB29E4B6E7C4C0A87F8729214AB17C7EFC27DB1AC0D6B31F9603CB107F502BE832F253E07E1F2ABCAF05F3D81E363G2BGF2811E825888D08460282BA7AE00CE00C1GBF40B4407DGAB8132815654897B07E69BD3584F8BDB9A88D1C941D0607A7FF352B9F0BD705BDC17DAFFB6EFEB52390E9C2FA735476FE6
	5E8BA70D739AE0CED71AF97C5C1BA2E61F3E0B18FD06461B8C31B6BC9361E7696767716C979E9B0B497A5A58B8DF25637AE30361180207EC168F371422292CEE6A1B93F55320097EE26F302A1B7C4793758517FB6720B1E7309065347F38036BDF7F9ECABF7D8E414050B8DBA327683E9361BC992E116216F29C7F6FCA09358FDDA569FD58D8E23E6E1BCA962FFBCF09793A77169857BD433DC57FA4BC8EB1BA220F6B55F157F4DCBF6509388F943F9650711729DE08FFC7C601827FAB2A57ED0A9B6138DF9BF057
	447DE3894681189C88C745DCFF895698E30A2A32FE000ED1C290963ABF02D4C7A5A299740E6A38EB7A3B586CD87CFE36B80C6F0761764FAF9EF7E8FE71381DA7970FFB7924F19C5B53EF4D09BDBD733858531F47F064E79169F543F7F66AD2062F1BBE10408B177275BBB7E7DC3F4F825F42B46B176977405CE21D7FF04E28B37B7FF83B67DF214D7AA12950C8C9B7C08F40C50D6C9DF2B5F42BEAE3ACCA8E09833B3B1F3E0E8D375A3CE55F7FEAD9A9A96FA7B6961E7D249CE32C1FD450906CA7E5901E53789706
	24DFEC125CA1657F9B2CC61CC374D06A528232AB0AF7AAAEAD6C1A0A49DEFD427E24992C6E4B6BB3561BF5351316B56B0A934C3FD9A792F59F5BC3B2BB50FF1BGDF6E927B403676B709759EB69414D4D51354096E109F77331E99CDCE54A5FF56017CE3E28CA7B2DAF6876AFD53B067FFGD0CB878852732D883090GGCCB0GGD0CB818294G94G88G88G39F4FEA752732D883090GGCCB0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81G
	BAGGG6A90GGGG
**end of data**/
}
/**
 * Return the buttonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getbuttonGroup() {
	if (ivjbuttonGroup == null) {
		try {
			ivjbuttonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjbuttonGroup;
}
/**
 * Return the CRelayRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCRelayRadioButton() {
	if (ivjCRelayRadioButton == null) {
		try {
			ivjCRelayRadioButton = new javax.swing.JRadioButton();
			ivjCRelayRadioButton.setName("CRelayRadioButton");
			ivjCRelayRadioButton.setText("C");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCRelayRadioButton;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200 );
}
/**
 * Return the RelayLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRelayLabel() {
	if (ivjRelayLabel == null) {
		try {
			ivjRelayLabel = new javax.swing.JLabel();
			ivjRelayLabel.setName("RelayLabel");
			ivjRelayLabel.setText("Select the relay to use:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRelayLabel;
}
/**
 * Return the SRelayRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSRelayRadioButton() {
	if (ivjSRelayRadioButton == null) {
		try {
			ivjSRelayRadioButton = new javax.swing.JRadioButton();
			ivjSRelayRadioButton.setName("SRelayRadioButton");
			ivjSRelayRadioButton.setText("S (All)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSRelayRadioButton;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o)
{

	char relay = 'A';

	if (getARelayRadioButton().isSelected())
	{
		relay = 'A';
	}
	else if (getBRelayRadioButton().isSelected())
	{
		relay = 'B';
	}
	else if (getCRelayRadioButton().isSelected())
	{
		relay = 'C';
	}
	else if (getSRelayRadioButton().isSelected())
	{
		relay = 'S';
	}
	else
	{
		System.err.println(getClass() + "::getValue() - None of the relays was selected");
	}

	if (o instanceof com.cannontech.database.data.multi.MultiDBPersistent)
	{
		if ((com.cannontech.database.db.DBPersistent) ((com.cannontech.database.data.multi.MultiDBPersistent) o).getDBPersistentVector().get(0)
			instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon)
		{
			((com.cannontech.database.data.device.lm.LMGroupEmetcon) ((com.cannontech.database.db.DBPersistent) 
				((com.cannontech.database.data.multi.MultiDBPersistent) o).getDBPersistentVector().get(0))).getLmGroupEmetcon().setRelayUsage(new Character(relay));
		}
	}
		else if (o instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon)
		{
			com.cannontech.database.data.device.lm.LMGroupEmetcon group = (com.cannontech.database.data.device.lm.LMGroupEmetcon) o;
			group.getLmGroupEmetcon().setRelayUsage(new Character(relay));
		}
		return o;
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
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("EmetconRelayPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 300);

		java.awt.GridBagConstraints constraintsARelayRadioButton = new java.awt.GridBagConstraints();
		constraintsARelayRadioButton.gridx = 0; constraintsARelayRadioButton.gridy = 1;
		constraintsARelayRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		add(getARelayRadioButton(), constraintsARelayRadioButton);

		java.awt.GridBagConstraints constraintsBRelayRadioButton = new java.awt.GridBagConstraints();
		constraintsBRelayRadioButton.gridx = 0; constraintsBRelayRadioButton.gridy = 2;
		constraintsBRelayRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		add(getBRelayRadioButton(), constraintsBRelayRadioButton);

		java.awt.GridBagConstraints constraintsCRelayRadioButton = new java.awt.GridBagConstraints();
		constraintsCRelayRadioButton.gridx = 0; constraintsCRelayRadioButton.gridy = 3;
		constraintsCRelayRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		add(getCRelayRadioButton(), constraintsCRelayRadioButton);

		java.awt.GridBagConstraints constraintsSRelayRadioButton = new java.awt.GridBagConstraints();
		constraintsSRelayRadioButton.gridx = 0; constraintsSRelayRadioButton.gridy = 4;
		constraintsSRelayRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		add(getSRelayRadioButton(), constraintsSRelayRadioButton);

		java.awt.GridBagConstraints constraintsRelayLabel = new java.awt.GridBagConstraints();
		constraintsRelayLabel.gridx = 0; constraintsRelayLabel.gridy = 0;
		constraintsRelayLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsRelayLabel.insets = new java.awt.Insets(0, 0, 10, 0);
		add(getRelayLabel(), constraintsRelayLabel);
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoM3();
		connEtoM4();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 10:43:41 AM)
 */
public void setRelay(Character r)
{
	if (r.charValue() == 'A')
	{
		if (!getARelayRadioButton().isSelected())
			getARelayRadioButton().setSelected(true);
	}

	else if (r.charValue() == 'B')
	{
		if (!getBRelayRadioButton().isSelected())
			getBRelayRadioButton().setSelected(true);
	}

	else if (r.charValue() == 'C')
	{
		if (!getCRelayRadioButton().isSelected())
			getCRelayRadioButton().setSelected(true);
	}

	else if (r.charValue() == 'S')
	{
		if (!getSRelayRadioButton().isSelected())
			getSRelayRadioButton().setSelected(true);
	}

}
/**
 * setValue method comment.
 */
public void setValue(Object o) {
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getARelayRadioButton().requestFocus(); 
        } 
    });    
}

}
