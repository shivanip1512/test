package com.cannontech.test.radius;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.login.radius.RadiusLogin;
import com.cannontech.roles.yukon.RadiusRole;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LoginTest implements ActionListener
{
	private String username = "";
	private String password = "";
	private String radiusAddr = "";
	private String secret = "";
	private int authPort = 1812;
	private int acctPort = 1813;
	private boolean useYukon = false;

	boolean attemptLogin = false;
	javax.swing.JLabel ivjAcctPortLabel = null;
	private javax.swing.JTextField ivjAcctPortText = null;
	private javax.swing.JLabel ivjAuthMethodLabel = null;
	private javax.swing.JTextField ivjAuthMethodText = null;
	private javax.swing.JLabel ivjAuthPortLabel = null;
	private javax.swing.JTextField ivjAuthPortText = null;
	private javax.swing.JDialog ivjJDialog1 = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JLabel ivjPasswordLabel = null;
	private javax.swing.JPasswordField ivjPasswordText = null;
	private javax.swing.JLabel ivjRadiusAddrLabel = null;
	private javax.swing.JTextField ivjRadiusAddrText = null;
	private javax.swing.JLabel ivjSecretLabel = null;
	private javax.swing.JPasswordField ivjSecretText = null;
	private javax.swing.JLabel ivjUsernameLabel = null;
	private javax.swing.JTextField ivjUsernameText = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JButton ivjOKButton = null;
	private JCheckBox useYukonCheckBox = null;
	/**
	 * Return the AcctPortLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getAcctPortLabel()
	{
		if (ivjAcctPortLabel == null)
		{
			ivjAcctPortLabel = new javax.swing.JLabel("Accounting Port:");
		}
		return ivjAcctPortLabel;
	}
	/**
	 * Return the AcctPortText property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getAcctPortText()
	{
		if (ivjAcctPortText == null)
		{
			ivjAcctPortText = new javax.swing.JTextField("1813");
		}
		return ivjAcctPortText;
	}
	/**
	 * Return the AuthMethodLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getAuthMethodLabel()
	{
		if (ivjAuthMethodLabel == null)
		{
			ivjAuthMethodLabel = new javax.swing.JLabel("Authorization Method");
		}
		return ivjAuthMethodLabel;
	}
	/**
	 * Return the AuthMethodText property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getAuthMethodText()
	{
		if (ivjAuthMethodText == null)
		{
			ivjAuthMethodText = new javax.swing.JTextField("PAP");
			ivjAuthMethodText.setEditable(false);
		}
		return ivjAuthMethodText;
	}
	/**
	 * Return the AuthPortLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getAuthPortLabel()
	{
		if (ivjAuthPortLabel == null)
		{
			ivjAuthPortLabel = new javax.swing.JLabel("Authorization Port:");
		}
		return ivjAuthPortLabel;
	}
	/**
	 * Return the AuthPortText property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getAuthPortText()
	{
		if (ivjAuthPortText == null)
		{
			ivjAuthPortText = new javax.swing.JTextField("1812");
		}
		return ivjAuthPortText;
	}
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData()
	{
		/*V1.1
		**start of data**
			D0CB838494G88G88G6ACA2AB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DDBFFD4D4571DE73B346B59F2CE6DB91C142436E59313439EC97D91D0CC50E8EA9A22A6418D369AA7899113508DC9E9431E5A934F76490F817921C4CD2215A6F4B591DB6A12E83A86C1FE8D624098099045A38283730087438C19F9A30331E13F775D7B3D6F4D4CFBA3C9337F2D67FCFD6FFE3EBF6F6F6F3DEF90763F93DB30B3F73FA06CAC90583F7F59A9884975023054752BEB08898BB24C02665F9F
			01DE979EB66743339CF85BDE4CB0178BFF7695C1798814231F4FB03F856F7B049A1F2396DE8C7C54816F46DB2FAC8C6FE7FE817AA9D44A4BCE1742F3B7D095E0B6201885447F93278FB0FC924A3B59BB211D3902D099CFE4B22E9D5060A6A6D79ACF1FB10A4C3F1F2EE07CD1A823CCC43CEAF317925DB7EBCF31F26FF7E3051C35A7F34B08AF0DFD24F4261BFFAFFDD68A49CAC364891F7B8F43332EF3FC644C10FB6278105D65F23BE4F1E87438ACFA655ADA1BFBC4F2F5432BA05FCBE25B55D72468018E14E99C
			9B4A6A181F6871D8A17D2E2306F1A4B17FA6CD9C2553B74DD1BFA636775515B23D8AA8F7839DD8C870CE85CF05F2A61030907D569FC53FBD1FAF94368FDE4E5DE36037F7A1759739D0759B3B7C77F987157E2CA9A4FD328AF85B003683D5AC4AB0578175829DD24651CB85FF02E753205C621E18F03B1AA6A71B1D8E4FF937C3F442FBEDADF098F6D19CF2FB9C02C0474A6E3EEA1DF81064C50163C46D5B4B8276ED7C45FB84D9BE1FDB20D337040DFF4D1D43EC4CDB24560D56EBFD9129D7B2601B0032010A96E7
			182B004EAD46FAFDE8BEF12BFAD9E1BCF8DCF6E717EFF0526E111BFD1253A1FAD87D8E56052B1F9ED13D23DFDA0F36CB5C89ED3B186E6B49AD54E997D3DCFD2EE3B1ED0F1B0B55F609DFCC46539AE5BC4D03771DECACD37CF1854F5660F4DCDED2F0D29616E4189DCBF07C756C46D8EC79F788656FDF510DA566210FF3D316509852178477D1DCB1196B2577C13FGB58275G79012201626E43FE0AB07F77AC47DF27260F286EC705EF42D3744B9E3B5565156D2EA171125DA359871D22D7E0B24D05A47641G3D
			B6E5DE8C010FB651A5FA6C3264F601F57B04888342CBE5BA8BDFA7B29ED161F6CB9330D458A7A685345BD3388F1ECE3BD77EF452E117C5D50272878AC9DBDE37DF820BE0G5E375FC75A7CDDE51DC90177EDC0342DBAEA89D617GEBC382364B16423D706C0081AABAEBEBAD529089436E19C67F9B0B481AA0DE97DD32C51C94DD8EF871B21E29E8175AAE43EE9E73D685A7E33D858277F2BC3368F50A038DEDABA3BFDB615BE559A38D7AE431CB14E549B5226AEFD7E27186CCA32FC06B562C6CA923D02F882039
			3CFE7F30EB1F26FEB637FB5CB759CDFA0E4D2335F5E4EF1A1F00E372159E9C1316323B056A0D17F547243296A75031181A200EC9EAB3CD3139BD81773B5E3A1EAFB9D775E8FEC268DA272E873E2F45C71E81CE5BB8D1194795099966D3C0BD095846493BAA695CEA36FBC51CCD97BC50BB22C39DD1D4F6C93171F8DADE2CBF1E9EAC569FCF8F97079FAF0F956B0D974ACBABA67B01FB4A2599FF1B0B77AA735565921520390AC0777AD73AC9DBC8AEC93EACB9C6C439CB3AA1427A14C866DB1DB25FA661BDB29147
			5254E19CCBC7EE7C0B302A7F835DBDA8666ECFF2CD0934EF33935531445607DA526622D31C0006EAF17BDCB245AFA9F31961ECADB9F7DADF766353E4BC0E0B53CC6E62E91224DD37BBFDE47D4AD33058A344DE3BC532BB5DA36F537EFE251E58ABDF1AE1BE82D437947B7B715387B5F30AF5F40B5B69244DA6E02ED6D02F4D553AC11F64F5B761B9175BFA202E1C74117819CF04E515CC4C8E9E67C3F5FB03F817D84C143F362E841ECE4D1C0EDBCA7CF6719C0B2C79949BD33053705FA3F938971EAF5766FF8EEF
			3BEE7F904ADACA7BC5D8963C7F9CEEA0F6AB812FD906F5EA2D572EB9CDDE2FFBC8D216F796F7CEFDE85F7D363E981E4312478B7902C374E39DFFD7CFE2A56BBF0359D83C7899712F641DE3766BF67FF16F940C7D5ADA560340237926DB71C5F4AC4BB09F74B610FE3D8965E82078E5B8DEAFFF20ED0FD569E78C5BE38DEB07CDA16DA1B592FFB90097A5E1FB5CB8733A26BDA03F03ADF484FA8B6BD85510C747A7ABD7B794607894F0FD136BC93ED311243F86566860B4A7F9D1292BA5A9B8D7791502F7A78567AA
			538D182BB800171204FBE7FF9329D775F218874B31DE2B9B4AB4756AF4CBAE19574955C866185BA3C17A0F18D741A03BD5969D3ACFFFDB699F9B581C842258D6850B04723C6538073C5CC06670AADE7E0DD2CE676519062FE37D57927195384744FC6DBE347B8DEDA3C793698FBFAF0FAA65E9815729BB6B5FA0FDA38E5BFDCE588E5CDE7142A4CB2D48FC381F345F8ABE667321CC674B62A932D68CC372855324EBD29C9276BFC0E4FF4B572888100D86CAGCA834A8122BEB3AB48BA44E6D68BB95E117C45A562
			BAD75528DD67FA41AE597BA360997DG5AC8D3462FE62A9E3FAC399C6EA9B0C7B2CA59FB5CE26D6A3C50555AFF61637E969B3C50B999BB251D13270AAD06F3B27E815ADF2B58B3FF79A136863CF3260869EC87DECDB241961C599F548FD3F046BECE661242FFBA00CF666F3820656FE95072C92C1F67E979CD81FC92739526CF6B356EDDBCFB6ED897A7B8934F9A1EF97D496CFC461EB17FF4C8C8DD78FC9E6D57D70F113657D987C987B9E97D879AF1BC24A5639A78C2BB196725ABE0EF832ADB01737C39E6A25B
			8D6549957C6C5A74E7F8DE320F3193C22BC3125D9EA551813053630691A8CFD3D4592158FC7946192FF6C6DBDD08ED13F07F1DC252374C39A586EDB3F785ED13C56CD9732DC31AFC6D120534CDBA70F6GD52E043C8D488664D809F57B20194488799A893FC9166DC3234A2E04E95CA5C91CD2E1B2F1E85D4446603A79D72A6BC554CA237578E79D245DE3D752F5770F9C7F2502E7E9F03A9EE7A97DC44A279E4CB0DBD6E15CAFB4931BBC77234D4E560C171B4958A3F99A4CD8690632B8F40120EB04364B3CD642
			6FDC1DE1F6824D851A8F14349AED5FEC79AF5AA6BA6D518A099F4ED97457EFB14EE907AD37BEBB3AADE46F9FF44B32FB0258C75CE7A1EDAD3BA795106D2596B2C6E0DB991529A843A3ACE45CBA45E185267B48CDE55C27C13DB2D7635E12500EFBCB69C338F718BA9A495801328DE894A892E89ED092D0AAD0A61070138CF38550C9A09B50A8D0A4D0ACD042CF5056FFA83656C3B98B28F4CD06399A488664840A820A87DA8F1485D47AB07001ECC0CE20A820780751562F5B4920571BA7A49623B9C462A3F541B2
			56834B31C13A36070CED0D8649C606719BCC114CEEA6EB43DB495BB43693D8DB0F72BE7A304328AD760531C96EF035FBACD9C74AB465F702648B4258BA94D47E43AC5B8157355D825DC7764CC27E20661D4CFD3267977231BE3F55F83EADE83D75FCCBE8D572EF18A75D5313A2BBFB36AA795A7030D76473FAE5EBB9472C5ECE37D7A2AB895D4FEFBBC7641D76E9378F64CF7D144C2DFB4FE19E309E4670B620D240AB01C81E9621482CEBBF90265E54F675BED23F0BF6076473B6B99C9E1BF236213C551D3B83F8
			5DD0D5543BFC10746D27DEF25BB8A102166864B6CF4C14EAF8C40B6283EE225389476AA937479128733DC6B2DE3A44A10FA8D38E45A747CBB5BA2AAD7365E2AE2F0DEBD7A971516413C7BB5D9EB95007D0D32A612936AAEC8ABEB4A48762EFDB4BB4F8202D6A3DFB182D7322BC6AF6E8E3CE19599D4053463769CDB29EAEF4B47BE085E67DBB67C3623F050C9FA74505C7C87FFDE85366B5A92CEF3360C979F1D7357CF30FD07E64A3484F117574A3B97F36DDFA7AB11C3FE0C6CFBF1E735FA851632F627CE73F50
			725318FDA1857983FEBD7EFE4E2F1A5033DF4579F63FDE7CF51C3F3FD70F5F4979B1E3FA769D1CBFF8CD0F6F677C5F0D6A590F627C5CBEADBFB60572D3B85FA66971CD7024794846F3780DA59B302AC771FC717EG390766F968F17B14DCDB5B66119C4D76911B32CA50DC748FD64C45879E45DCF427126344C0F991D04A23F89E773F07F9E07CBDF78836089E5D9CB93D3BA1B77DD116732FD54F63D14BFF1FC76D0C0D231D0A855FD7F8C639F6A353F7E86C603EG397630C12E2DCCB4DABF278DF36D79EB319E
			FFBB2B7D3E153C2741B01FB5B13FBBB47ECDD08F6A7F5D2A8AFD7FE4B21F15C4270346706CD57E0DEDDD0619E2745BA631A3F36CC4894A45F3EC6B8C4AED63182D84310A75585FCB5F2E540F8517C5CDBCEF4E201F6E75E873FBFE4422B856BF01FE56F0AC460F588E0EF574A2F672B1441E98C34C49315B2F713E6058DFA502113D54749856E5CF1741DD92DB7870AE6923BE34377FF134770F92E23647B1D759F26E2BFD537A45D7545BE47D32FA744C61D366DD6463744C311363DFA878DC8DCE67FEE1BB4E7D
			F940931E40B3E3EA9B19978EA8C7G45GAD82CA854AFA824F94DDE7EB04DBFCFF6AF6CF5AA029F1EABE0DD07BFF32927BC2AA6404C00DC0BDC0CE2048D4347F033364BB24519975225B2D5CD8104B8B3B6492BD4A9146E3A7B7D6348DBF336935E1CCAA1D0771292179AB0DAD3A1D2C172B00EF824A81AA5DGB97D860C6B97E7EF79FD34559F667B68362B617A5636A1B4AE2AD76353539BE07232215E2912F07E66986A3DB82327B70F49AFB2547BC4575F9AA63F49D06F102EBF24ACC33DF97EF0FA423F9969
			B9A6747476B379AAC33D9F687A2BE3721D06FA1D3DFAFA8EA66FB754CB9B53530BE2723106FABF3C2627174064D38C75CECBFAFAA6A61F8D4F7CA783F5295E995D5FB59CE03255CF9A791B236B2F1149F79B6AFD5EAE7077C84DFBD97B57FD279B6A7B3D7FD39F6C5BDD1B7119B03DCD7F3B628BED7A5F95335A42FFD74CE9B37E2E78DADB68F74593D66358EA2D37BE2F36D8756F072C56507B210F2D7A77C35D56507B21B9CA2EB68AE3467FA45E8F3DEC437B2148A7F18D1F57EE9C7FDD6D7AED7B2F6D7AED3B
			30BD7CEFG16359B35BD0D6F352B98DF9A0F394D0775B81206D81C8F65EAB8F6218431880E7D678C6A26F26C6193A8E76258E8894AD5EFC46C5BC7D16E9447BE18C1EC8E476E73A33609E31FCF203D2A1FA1E61AC039139CCB203AD00E665841E9140B6558C8AF4AE5F3AC259765F6F06C25B174EB7BB9E236F1146B61D862B5140B63581BFDA81740310B927A4D5F446BAB21DCB9476ED16C8DC0D96658270A3DB9D00E61587D5C47FC0E7DC8C2399C0E89E3A8A7EC46EFACBF3211EFE2B28C464E4D64F70837FF
			C25639144DC4BE7F3C052D97511BC357C84C73729845018C21C38CCF5E2C6602545E5EDE34D77ED4283DBCA6DB405E0FBCC56D3D4130D3CF2976E82C9BAE2A31BE68C35B969D5B5AB875E26DF9AAB85661923467B008B5CF932BBFA8562810D8F33A54D8CDA750F60CC12C85E1E20D8F31DD7729EA7B4E23E8BB4940F6E1985B6BC35A41BA0376B69934C301A65E2C20F69036845BDB6BC7FB25DB746D95EA6CD5EE8934F7F2CBF05D1D17543A3FBA01369BF5EC931DDDE16A5E2D31CD6746BAFF605C784EE555D7
			4DB47A9AB02847AECDBD6420FA4C892947129E557633FC1EC49B58AE51580E8B321D9CE2FB6395557650B85ACEB1E82312B0EDE48A31FD712AEA7BAFFDE8BB5340F6190E6DB798EFC708EDDF2FEAFB2D04360BCC7AEDD226E9138AD3E01B5418027BF645B5FA1FAD49A4ED205F40532E61BDD31D8957C7E7DF60987871B5B5268D3C3E1DBAB1913FFBB475F5000C9F68B07391E58AD94F7AD45BEF0D71B542203EFA36799A2131CD6F131E382A7DA6DEFE4771DC64875BCEE27E5618547BA4FAEF7161F93C37588E
			3CF7847A9D25C8D07F51FAFCB4A0F03F4F15EA6F51ACBFB7679EB670DB73B47B4D79532A5F2C3CC3F954F78B3F3FC9FADA4FF7F68375DDB5C8CAD4A71B5FDFE5038E65F78C297C56AA64D7BD934E668A072AD3F692F5861EC11B69224AFF72A872E3B8BFEFC4655F60F7E529E1FD3EB72A6A3C466F330A1E8D27E315D41DA3AE54E9FC9663F00E29FC7BB4727DE1EDFEE7DC55A967F7E30B426AACF12ABA51632813496358B821727B787DD8E5BA720FCDEA6A4E6F4AFA52437934FDC6CAF44E3EF40D488F5B1DDE
			115B496DC7BBF3F56D24B7980FE737C70D679C3FB3CC61717E10245A6CEE27037C1C096565EF743FAB985CAFB58D4A6D766B768B03E362105CEDA783041E7BEA4FEB4F2A42F399667D4F057F8ECA7DDED7721FAA1035G0D82C5GC58345832D82CA834A849236E6188FGD583B582F583B9007CC0D1DB717C724540B1235881E417741A9BB10C7FDC409DC4AC58CB589ABEFE768DBC606EA985F4CC3352EB2F526AE503CEFE46EC74761E546A9DG1D6AD969EDBA2A55EB841D6ED9694D845CBD0D024E4DD96989
			53DA3D3920B3FFD6FA47DCDA3DA450D9BFAB3D61GFF692013B3AB3DAD7DDA3D22ED99660AED33513BFDDC2BD783BA16D9698D855C8575000EF3D6FAC240DDD78468C44FCA2FB020FEF12013BCAB3D73817ED2C1E75BAD7468D96D93E52F5D3E0D6E63943B24E0654FD30C4E53FF76995FB3FC57F76BFB12BBFC7A77A4313E50FB123BFD7A77A48BFC5AFB920AAD7211B5F28456099EAE174831AB9C3BDF31C9304B12C39E6578AAA5AE021F9309C344FF2A44B525E0BD8CDB27412EB0AC55C7FEEBGD59EA52BB7
			7B3B8C9F496506A527135DFFA9B2124BAB4A3CBD1ED1E214A66D1A38B3B8F60577C5FFEFE87FB8FA357DC331526B21F22FDF8F1533D7054A8DD7054A9DBB99AAF77CE4285CAF0F064A659C8D153BEDA6D46E7BB321F2EFCD074AD5CC074AB53AC265DADD21F2FF5331778D9DFB3F6E8F157BCDFF285C2271D0394471D0399BFD21F2B3FDBA759D53296FD8285C5BBA719D56094F21E3CF8A30C74FAE7677CA857EBBBCE5CAD03C42DF2662B8A5847A77262F663764C24E7F32D0A08BEF521F39D4F6091E6B222712
			7C5CC574FAEBE5E1977E0E053C6387D7F8AF213FBD296C902721340F7C5A436DE1BFG2CA43F9781B47F7DD36FFFC09FA7E4213CE9E8087C2104649AC1017D01EA7467A23210575954A98BC515151597BA60BFD9B0539F07F8BD429E7C62FF5C2A4C3F746BA7EA73B26E4A4B95722D02192EC7D6A11FACAAD621C0D9F32CC29E2CC7FE129C66EB497B197005D07A7F685F0F32B35B31996DD9F17BB75FB57C7DF4D5A65D4B6AB2037F26A139100CEDBF60511918D316967EC5B04E073BA66CCE27ECF70EB7399CA4
			B7EE1E16C5758EFCF5A1316FC59946243C94656F7946B4BA7F8BD0CB8788AB77E91BC094GG64BDGGD0CB818294G94G88G88G6ACA2AB0AB77E91BC094GG64BDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFA94GGGG
		**end of data**/
	}
	/**
	 * Return the CancelButton property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getCancelButton()
	{
		if (ivjCancelButton == null)
		{
			ivjCancelButton = new javax.swing.JButton("Cancel");
			ivjCancelButton.addActionListener(this);
		}
		return ivjCancelButton;
	}

	/**
	 * Return the JDialogContentPane property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJDialogContentPane()
	{
		if (ivjJDialogContentPane == null)
		{
			ivjJDialogContentPane = new javax.swing.JPanel(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRadiusAddrLabel = new java.awt.GridBagConstraints();
			constraintsRadiusAddrLabel.gridx = 0;
			constraintsRadiusAddrLabel.gridy = 0;
			constraintsRadiusAddrLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsRadiusAddrLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getRadiusAddrLabel(), constraintsRadiusAddrLabel);

			java.awt.GridBagConstraints constraintsRadiusAddrText = new java.awt.GridBagConstraints();
			constraintsRadiusAddrText.gridx = 1;
			constraintsRadiusAddrText.gridy = 0;
			constraintsRadiusAddrText.gridwidth = 2;
			constraintsRadiusAddrText.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRadiusAddrText.weightx = 1.0;
			constraintsRadiusAddrText.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getRadiusAddrText(), constraintsRadiusAddrText);

			java.awt.GridBagConstraints constraintsUsernameLabel = new java.awt.GridBagConstraints();
			constraintsUsernameLabel.gridx = 0;
			constraintsUsernameLabel.gridy = 1;
			constraintsUsernameLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsUsernameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getUsernameLabel(), constraintsUsernameLabel);

			java.awt.GridBagConstraints constraintsUsernameText = new java.awt.GridBagConstraints();
			constraintsUsernameText.gridx = 1;
			constraintsUsernameText.gridy = 1;
			constraintsUsernameText.gridwidth = 2;
			constraintsUsernameText.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUsernameText.weightx = 1.0;
			constraintsUsernameText.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getUsernameText(), constraintsUsernameText);

			java.awt.GridBagConstraints constraintsPasswordLabel = new java.awt.GridBagConstraints();
			constraintsPasswordLabel.gridx = 0;
			constraintsPasswordLabel.gridy = 2;
			constraintsPasswordLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsPasswordLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getPasswordLabel(), constraintsPasswordLabel);

			java.awt.GridBagConstraints constraintsSecretLabel = new java.awt.GridBagConstraints();
			constraintsSecretLabel.gridx = 0;
			constraintsSecretLabel.gridy = 3;
			constraintsSecretLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsSecretLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getSecretLabel(), constraintsSecretLabel);

			java.awt.GridBagConstraints constraintsPasswordText = new java.awt.GridBagConstraints();
			constraintsPasswordText.gridx = 1;
			constraintsPasswordText.gridy = 2;
			constraintsPasswordText.gridwidth = 2;
			constraintsPasswordText.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPasswordText.weightx = 1.0;
			constraintsPasswordText.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getPasswordText(), constraintsPasswordText);

			java.awt.GridBagConstraints constraintsSecretText = new java.awt.GridBagConstraints();
			constraintsSecretText.gridx = 1;
			constraintsSecretText.gridy = 3;
			constraintsSecretText.gridwidth = 2;
			constraintsSecretText.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSecretText.weightx = 1.0;
			constraintsSecretText.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getSecretText(), constraintsSecretText);

			java.awt.GridBagConstraints constraintsAuthPortLabel = new java.awt.GridBagConstraints();
			constraintsAuthPortLabel.gridx = 0;
			constraintsAuthPortLabel.gridy = 4;
			constraintsAuthPortLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsAuthPortLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getAuthPortLabel(), constraintsAuthPortLabel);

			java.awt.GridBagConstraints constraintsAuthPortText = new java.awt.GridBagConstraints();
			constraintsAuthPortText.gridx = 1;
			constraintsAuthPortText.gridy = 4;
			constraintsAuthPortText.gridwidth = 2;
			constraintsAuthPortText.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAuthPortText.weightx = 1.0;
			constraintsAuthPortText.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getAuthPortText(), constraintsAuthPortText);

			java.awt.GridBagConstraints constraintsAcctPortText = new java.awt.GridBagConstraints();
			constraintsAcctPortText.gridx = 1;
			constraintsAcctPortText.gridy = 5;
			constraintsAcctPortText.gridwidth = 2;
			constraintsAcctPortText.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAcctPortText.weightx = 1.0;
			constraintsAcctPortText.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getAcctPortText(), constraintsAcctPortText);

			java.awt.GridBagConstraints constraintsAcctPortLabel = new java.awt.GridBagConstraints();
			constraintsAcctPortLabel.gridx = 0;
			constraintsAcctPortLabel.gridy = 5;
			constraintsAcctPortLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsAcctPortLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getAcctPortLabel(), constraintsAcctPortLabel);

			java.awt.GridBagConstraints constraintsAuthMethodLabel = new java.awt.GridBagConstraints();
			constraintsAuthMethodLabel.gridx = 0;
			constraintsAuthMethodLabel.gridy = 6;
			constraintsAuthMethodLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getAuthMethodLabel(), constraintsAuthMethodLabel);

			java.awt.GridBagConstraints constraintsAuthMethodText = new java.awt.GridBagConstraints();
			constraintsAuthMethodText.gridx = 1;
			constraintsAuthMethodText.gridy = 6;
			constraintsAuthMethodText.gridwidth = 2;
			constraintsAuthMethodText.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAuthMethodText.weightx = 1.0;
			constraintsAuthMethodText.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getAuthMethodText(), constraintsAuthMethodText);

			java.awt.GridBagConstraints constraintsUseYukonCheckBox = new java.awt.GridBagConstraints();
			constraintsUseYukonCheckBox.gridx = 1;
			constraintsUseYukonCheckBox.gridy = 7;
			constraintsUseYukonCheckBox.gridwidth = 2;
			constraintsUseYukonCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUseYukonCheckBox.weightx = 1.0;
			constraintsUseYukonCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getUseYukonCheckBox(), constraintsUseYukonCheckBox);

			java.awt.GridBagConstraints constraintsOKButton = new java.awt.GridBagConstraints();
			constraintsOKButton.gridx = 2;
			constraintsOKButton.gridy = 8;
			constraintsOKButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOKButton.weightx = 0.5;
			constraintsOKButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getOKButton(), constraintsOKButton);

			java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
			constraintsCancelButton.gridx = 1;
			constraintsCancelButton.gridy = 8;
			constraintsCancelButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsCancelButton.weightx = 0.5;
			constraintsCancelButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add( getCancelButton(), constraintsCancelButton);
		}
		return ivjJDialogContentPane;
	}
	/**
	 * Return the OKButton property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getOKButton()
	{
		if (ivjOKButton == null)
		{
			ivjOKButton = new javax.swing.JButton("   OK   ");
			ivjOKButton.addActionListener(this);
		}
		return ivjOKButton;
	}
	/**
	 * Return the PasswordLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getPasswordLabel()
	{
		if (ivjPasswordLabel == null)
		{
			ivjPasswordLabel = new javax.swing.JLabel("Password:");
		}
		return ivjPasswordLabel;
	}
	/**
	 * Return the PasswordText property value.
	 * @return javax.swing.JPasswordField
	 */
	private javax.swing.JPasswordField getPasswordText()
	{
		if (ivjPasswordText == null)
		{
			ivjPasswordText = new javax.swing.JPasswordField();
		}
		return ivjPasswordText;
	}
	/**
	 * Return the RadiusAddrLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getRadiusAddrLabel()
	{
		if (ivjRadiusAddrLabel == null)
		{
			ivjRadiusAddrLabel = new javax.swing.JLabel("Radius Server Address:");
		}
		return ivjRadiusAddrLabel;
	}
	/**
	 * Return the RadiusAddrText property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getRadiusAddrText()
	{
		if (ivjRadiusAddrText == null)
		{
			ivjRadiusAddrText = new javax.swing.JTextField();
		}
		return ivjRadiusAddrText;
	}
	/**
	 * Return the SecretLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getSecretLabel()
	{
		if (ivjSecretLabel == null)
		{
			ivjSecretLabel = new javax.swing.JLabel("Secret Key:");
		}
		return ivjSecretLabel;
	}
	/**
	 * Return the SecretText property value.
	 * @return javax.swing.JPasswordField
	 */
	private javax.swing.JPasswordField getSecretText()
	{
		if (ivjSecretText == null)
		{
			ivjSecretText = new javax.swing.JPasswordField();
		}
		return ivjSecretText;
	}
	/**
	 * Return the UsernameLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getUsernameLabel()
	{
		if (ivjUsernameLabel == null)
		{
			ivjUsernameLabel = new javax.swing.JLabel("Username:");
		}
		return ivjUsernameLabel;
	}
	/**
	 * Return the UsernameText property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getUsernameText()
	{
		if (ivjUsernameText == null)
		{
			ivjUsernameText = new javax.swing.JTextField();
		}
		return ivjUsernameText;
	}

	public static void main(String[] args)
	{
		LoginTest lt = new LoginTest();
		for (int i = 0; i < args.length; i++)
		{
			int delimIndex = args[i].indexOf('=');
			if( delimIndex < 0 )
				delimIndex = args[i].length();
			String key = args[i].substring(0, delimIndex);
			if (key.startsWith("rad"))
				lt.radiusAddr = args[i].substring(delimIndex + 1);

			else if (key.startsWith("user"))
				lt.username = args[i].substring(delimIndex + 1);

			else if (key.startsWith("pass"))
				lt.password = args[i].substring(delimIndex + 1);

			else if (key.startsWith("sec"))
				lt.secret = args[i].substring(delimIndex + 1);

			else if (key.startsWith("auth"))
				lt.authPort = Integer.valueOf(args[i].substring(delimIndex + 1)) .intValue();

			else if (key.startsWith("acct"))
				lt.acctPort = Integer.valueOf(args[i].substring(delimIndex + 1)).intValue();
			
			else if (key.startsWith("yuk"))
				lt.useYukon = true;
		}

		int option = JOptionPane.OK_OPTION;	//init to try at least once!
		while( (option = lt.login()) == JOptionPane.OK_OPTION);
//		showConfirmDialog(null, "Would you like to test another Login?", "Try Again?", JOptionPane.QUESTION_MESSAGE);
		
		System.exit(0);
	}

	public int login()
	{
		boolean authorize = showLoginSetup(new JFrame());
		
		if( useYukon )
		{
			ClientSession session = ClientSession.getInstance();
			if(!session.establishSession()){
				System.exit(-1);			
			}

			if(session == null) 
			{
				System.exit(-1);
			}
			if(!session.checkRole(RadiusRole.ROLEID))
			{
			  JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.", "Access Denied", JOptionPane.WARNING_MESSAGE);
			  System.exit(-1);				
			}
			JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername() + "' Authorized.  Test complete.", "Access Granted", JOptionPane.INFORMATION_MESSAGE);
			return JOptionPane.CANCEL_OPTION;	//force DONE!	
		}
	
		if( authorize)
		{
			System.out.println("Attempting a RADIUS login");
			boolean authenticated = RadiusLogin.login( radiusAddr, authPort, acctPort, secret, username, password);
		
			if (!authenticated)
				JOptionPane.showMessageDialog( null, "User: '" + username + "' is not authorized to use this application.",	"Access Denied", JOptionPane.WARNING_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, "User: '" + username + "' Authorized.  Test complete.", "Access Granted", JOptionPane.INFORMATION_MESSAGE);
		
			return JOptionPane.showConfirmDialog(null, "Would you like to test another Login?", "Try Again?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		}
		else 
			return JOptionPane.CANCEL_OPTION;
	}
	
	/**
	 * Show AdvancedOptionsPanel with a JDialog to control the closing time.
	 * @param parent javax.swing.JFrame
	 */
	public boolean showLoginSetup(JFrame parent)
	{
		ivjJDialog1 = new javax.swing.JDialog(parent);
		ivjJDialog1.setName("JDialog1");
		ivjJDialog1.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE);
		ivjJDialog1.setBounds(44, 53, 445, 315);
		ivjJDialog1.setContentPane(getJDialogContentPane());
		ivjJDialog1.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE);
		ivjJDialog1.setModal(true);

		//init values.
		getRadiusAddrText().setText(radiusAddr);
		getUsernameText().setText(username);
		getPasswordText().setText(password);
		getAuthPortText().setText(String.valueOf(authPort));
		getAcctPortText().setText(String.valueOf(acctPort));
		getSecretText().setText(secret);
		getUseYukonCheckBox().setSelected(useYukon);

		// Add a keyListener to the Escape key.
		KeyStroke ks = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, true);
		ivjJDialog1.getRootPane().getInputMap().put(ks, "CloseAction");
		ivjJDialog1.getRootPane().getActionMap().put("CloseAction", new AbstractAction()
		{
			public void actionPerformed(ActionEvent ae)
			{
				ivjJDialog1.removeAll();
				ivjJDialog1.setVisible(false);
				ivjJDialog1.dispose();
			}
		});

		// Add a window closeing event, even though I think it's already handled by setDefaultCloseOperation(..)
		ivjJDialog1.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				ivjJDialog1.removeAll();
				ivjJDialog1.setVisible(false);
				ivjJDialog1.dispose();
			};
		});

		ivjJDialog1.show();
		return attemptLogin;
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == getOKButton())
		{
			radiusAddr = getRadiusAddrText().getText();
			username = getUsernameText().getText();
			char[] pass = getPasswordText().getPassword();
			password = String.valueOf(pass);
			authPort = Integer.valueOf(getAuthPortText().getText()).intValue();
			acctPort = Integer.valueOf(getAcctPortText().getText()).intValue();
			char[] sec = getSecretText().getPassword();
			secret = String.valueOf(sec);
			useYukon = getUseYukonCheckBox().isSelected();
			attemptLogin = true;
			ivjJDialog1.removeAll();
			ivjJDialog1.setVisible(false);
			ivjJDialog1.dispose();
		}
		else if( e.getSource() == getCancelButton())
		{
			attemptLogin = false;
			ivjJDialog1.removeAll();
			ivjJDialog1.setVisible(false);
			ivjJDialog1.dispose();
		}
		else if ( e.getSource() == getUseYukonCheckBox())
		{
			boolean editable = true;
			if( getUseYukonCheckBox().isSelected())
				editable = false;
				
			getRadiusAddrText().setEditable(editable);
			getUsernameText().setEditable(editable);
			getPasswordText().setEditable(editable);
			getAuthPortText().setEditable(editable);
			getAcctPortText().setEditable(editable);
			getSecretText().setEditable(editable);
		}
	}
	/**
	 * @return
	 */
	private JCheckBox getUseYukonCheckBox()
	{
		if( useYukonCheckBox == null )
		{
			useYukonCheckBox = new JCheckBox("Use Yukon Components");
			useYukonCheckBox.addActionListener(this);
		}
		return useYukonCheckBox;
	}

	/**
	 * @param box
	 */
	public void setUseYukonCheckBox(JCheckBox box)
	{
		useYukonCheckBox = box;
	}

}
