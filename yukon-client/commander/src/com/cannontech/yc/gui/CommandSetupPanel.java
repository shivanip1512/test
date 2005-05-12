package com.cannontech.yc.gui;

import javax.swing.JDialog;

import com.cannontech.database.db.command.Command;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CommandSetupPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
    private Command commandValue = null;
	private javax.swing.JDialog dialog = null;
	private String dialogTitle = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JButton ivjOkButton = null;
	private javax.swing.JPanel ivjOkCancelButtonPanel = null;
	private int CANCEL = 0;
	private int OK = 1;
	private int buttonPushed = CANCEL;
	private javax.swing.JLabel ivjExecuteCommandLabel = null;
	private javax.swing.JPanel ivjCommandSetupPanel = null;
	private javax.swing.JLabel ivjCommandLabelLabel = null;
	private javax.swing.JTextField ivjCommandTextField = null;
	private javax.swing.JLabel ivjExampleCommandLabel = null;
	private javax.swing.JLabel ivjExampleLabelLabel = null;
	private javax.swing.JTextField ivjLabelTextField = null;
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public CommandSetupPanel() {
		super();
		initialize();
	}
	/**
	 * AdvancedOptionsPanel constructor comment.
	 * setValue(command), where command is the object to be editted.
	 */
	public CommandSetupPanel(Command command) {
		super();
		initialize();
		setValue(command);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/17/2002 11:49:08 AM)
	 * @param source java.awt.event.ActionEvent
	 */
	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		if( event.getSource() == getOkButton())
		{
			setButtonPushed(OK);
			exit();
		}				
		else if( event.getSource() == getCancelButton())
		{
			setButtonPushed(CANCEL);
			exit();
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/3/2002 4:00:35 PM)
	 */
	public void exit() 
	{
		removeAll();
		setVisible(false);
		dialog.dispose();
	}
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G4C09B8B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4DC6515312B2EE79B333145DDEA4D1E28512616EAFA16BD4B36595D34355B38462DF6139ADB6CE24DF123A6AD569CB736B9470F99C84084A101A428E8D0098525A4C4E81010840601C018848893A0899A9E4CE36605B7BF3CF98303C63377FEBFEF9E73C3C85AF04E653D6FFE77FE77FE773B5F7D6EFD9F1062BD8BF332AC4504E465907EF3B00B907590A1CBB72B9F89DC72619EABB17D3C8D30
	1D5475E543F3B174D505FB2C0544EF4F0576D2E827EBBD565D70DEC4F4FBF19D3CA410332E16105F7C570307E6167368A9A1E78BED570CEF03672BG4BG3786E07F91145FB1DE42713950FE153FA3E4D9C0AFCA73E4F609892FF33A457CF97DD124F10D17727EECE817828CG28C7A3E366D16DEEE828676D41DB96128D7F395D120F32CC638BD841F9B379330CFC0D9A42CA563ED6814F466EB17711913F37FAC472797CBEDD9E71D44B6100DF539B9A6C366E1066163BA51F2C8EB521FEDBF2F2A9AFA19550E6
	3ABC1A5F48E5A50DADA499F7E5A754E5D19353E1F9D3C497FB5A43F961E69C7BE6EADB644B05F693G53D35E734784FACE0DD43BC3CAC3C30B5F6B15FC2EDED98F852832C04F74DDB5AE74DD8A63B55A9077FAB64A59G6DED50AE3709F99C7E03184740E777130D43351602847318E363EBE70B4CE3705E0773DE23BE72319357F98D746536409A82B4818C814C81F8037AE6C96F7B70EC9E56D1F53F2FB9903029AE6D045FA52B705E50GBD9CF7CA9E71EBAEC2187FED4B290A230F006416693E977117FEA27C
	A56554BDC4575F37644419DB6A2AA2CB8A9FE3D9CBE4EEECDEAF1D46F9E5GFE93C0319D769E0083C0350BF98D77FEF839F9F5405AE9BEC96D8D8D87A4CD37059455A5EBFCFEDF9D1FE9FE7100715DF145FC4CAE0BBFB45BA57D9F6BACDB6258A5FD716B161B6C4C9E8B6D913BB059BFCFA8FB19BDF6BD905658518F67D1BFDC896FD95CC799BE1962ABDAA3F836BF4AA89E5B7550F7FB2B705B01D7459C9C593711421ABF451DC3728FBE3010B626CFC9DB745A2E6A4738E3877C90C098E0BE40920095G99EDE2
	FD3F5D3BEF16FE5BEDDADB46BB6A5C85CFB92CEBD207AF28CB3E9139CF5294E9D815030453781D287B70B4BE1D6E279110F1CC76491A24ABFE9F0CAEF9E5F024A023795C399DE9B41976BA952F8C823C81A2463D41D984CFD58A6A2783AEC917A3942C7FE6A75AF2C26A0391E1GF85FFF8CED7EAE0DF7845ECB81182DACE79027C3FB6EB1E1175C53BB6059890EAD2B90B71591D4C3522604FCCB9F4692F9C27669F6B9A07BDC709264FDF9FDF9913B0C7A8D1D8BA89E77C88BA89EB470BB7B36B3BC0CE1363268
	FF1376CB3A2EA943A1DD062828ABBEF704FFAF55A5B8ED7B85895F63E7504796423C52G169B733B2F2F48B4BF075FBF968AB8F165381D1E27F1B69D5EB79C93E7E5D2B61E1DA56D42CFFFBDA07C541EFFB729DAD59B57CF916A5B19FFF637C76F35FF20E3C6E43C17BDF0057BFE3600FAB399C72F198C5C4B4C6E7B9A516EE1101984105AAE6C7ECCDF995BEFB6A9A80B9D5625410A492E0817B15A7F69CF6CE3CF77477731F57D71FD6C377DB37B506F7A6379906B4B9A403345BF69B3796466019DF48F7BFCB2
	D55AE0A16C0C7F35936D2178943DDFF139E53DD779D406F5EEC75FEA20FBF0853CE7983EB4792E702572CF6FA04B4E9744BD4F126F2E3654F7B09FF2F4C4FC096934057A3A2C4ADEB0D40BBF645399BE1F6EEF0E677165658434B9E7519F4764A9CE17F7F6B341B82206E4E3EECB242DE8E3CD7142D261066579E02EE64E87D5508FF34794F8AE83305271EEE843F5B067B7ED98788C3F719CC63DD6837DBA006C6342EFEEB87B1AE93FF207E9712BAAB33F31AEF50D05380E72F8C806F00C9B3A5318CBE3630E28
	3EBEBEAF56EFEF44F92926D8D0FA9C6755E36408F8D6B01C076226E07172B70B3377C421B907F5857F90F78D91060E3335530FC71FDB8B0E6038C900DFE44C7D5F9B4D312AB998740FA874D8606B7135864CD1626513A374D0431C32BA24AB9848A59D76CBA024332C1265141A06B2B1B6249D97B9657F06F09DB2211D5B59E3AD6B943A788E477AD27070AB709CD534A064C2AEB9AC6CB9F5986D02E794C70B5C6D0059CE4BB25EC8E8277ACE6E6B1D51F6FA6AA86A61817CC5C3378D07371B6C84F9A69C49EED5
	B6FC634EA3D6665B3CFD4F119C615BC464DD13DAF46EB33FB31273AC0277344E69B9904BF14ECD203DD6F4C667BEB24527F7C667BE6BC3A277D987FD5D8EF196BF5E0473CA0576838EB12FC3C773CD736A76ABBE5D18535A2338BF7D1A62D6FC82371E62A04B26810B1DE773689A2F04B15782B05C180E38EDA720E6B8A1F0F3C308EB07766089F1DEC893989382C6FB0C36679B6D97C2516789527E254E94DEA7CCC44B307E0546FF553065AF1BF00D42C67BD734BD45E595113B354D541F277CA1D8003213E82F
	25C64CCB05B92FG58G10FB12658DECFFFEE792634AA844B9B8F2FA83728859C4F93F248BDE8F34AF825C8E3094A07D240817F705F07E93CAD041F8646CC23E6F06C4BC32F7893A14F0F4DC95F5A30B4D37CF18E3F3F29753EFA9BC9F81E0BE39C693756ADA4015F58B7DEB06B01726716502B4A1D5CB13D0CA9F539417CDF2BB28B5D89C298A66F27E01EE91C74E86F14C7950DE8C302CDB1CF76177C4ED1AF24FED44B127AEEE0D1D61AC352C6EE63E3429BB92835A21B6E5B2976BC2E6FDCF0FB55003381F69
	45D1FE528F8455681734BFB7E46EC73B5590F3FF4334FE54774DECF33FBA2D9FED3407733339BD34D74CED53C30B097ACF6FA67CFE10564366144E1F198F3CCE543B6E4BE3EB7AE88877192E05E4B66E42C9F35C2CFF656D04F13378949B2F02BF87DFFC1D1F95AFD4161857B2DC9D1C8427ECE8B8EE7AA672B71339FCDEC32744BA1E8963BAA6C3FB894072D3E26FBF9B93AFE7371FB7DEA5DFCD70CA79D89C2E52D17F0CD3AC8EE79978BA0ADFE742333DB09494FBE1A3748D760A787C8B9B5AEF99345381B6G
	ECF376D82BG5A1DA2DE3B1B77134B5414CEFF4081092DEAAAF7587C3E2DDFDD5D7F7DAD4267D23FFBA7C93B71292C2D89FCE35049FC62A2FF5E744357A91E4D4FE7477925C05FF200F5G5927FB2CA5G7BCF0B795D5FDC4566576AD274E6DD17C6BCB49B90136BD36449889A839E9B7B16D674ABBB0CB3E40C75E27387706C13AEF0AB6110FE0D0E8448EE4D9F10B5FD0AE1E936467D60EFA7236DB467F4644CC9BA9DFB7E3075FE027A716253EC3D8B8C7CFA0AFF4404E7FEF01E62313D917A74BE6177877088
	663A9CB23D0F862FD5ED955983172E298C36AEEBEC09EB2F67EC71EB2F67ED71EB2F7F334DDCFB3DECCBDCFBEDEEB157DE4C0F6EEF43781B89EB312D9F7C92408EB050AFFCAA29659D661FF1FC33950A3196BF7DDA238B79137644F37D42FE79EFBCFFED477CF7582F6BFEAF0EAF70B75BF13DF4FF00A2996EABF6349124EB9E1D11F2FCB27D5E2F4A23944DF0FF577A2A59EE4C7DB97D02D6348156170A24AA1F52F2209788FDEE16475DC3E3388EB63929DF64F695D9A25FCB6997FE7CD4984F76CD50AE9D6831
	56838CG048112GD2870C9C0F522D0476860062B3C08BB0G9086C8BEA36864F0007B910EF3B9DF93F2AEC7D74AF529B751224E53774575066C3CD978B946477CD950217F95F09982E74EBD59FA7CF3DBE29F7A37364B7B500F5BE8DD85EB6F1C8A7075DF51C6F37B515120EC786A23ED05862EA3586D8FAA3872ACDF785749587C7431CBA26FCBBB437253C7603986G73777DE71166E816FD06790BF3B1075BEB937096825FCEEE07723311331369F16ACF26DC54A18D4BAA7DE574CF1461389467842B34A932
	6A92FDB717E32D5BB55602D6D2EDA158D3BE1649327EE7A6313FB5AC0F400EB00B607DC74A8BA3320D51D95FB7356D14D77286D4B90EDE7A87C5117EE9E3AF21F6FAFF4A1C77054FB29B5E8ECF567FC23339FFA36FAFBB87F9B64D41B30B22F3F06C2F057E76F3E20C37C8BC99C3C6FF7241F8B2F4237F0361F87CB7997D8BACF167F0CE54GAFDF92F94F92402D85E0B961B1A2726C32C111930688528EC0BBGF093409EEAC7C2E959CF6651F8380503F8B7796489C2C47E7264B6113F14FE6B6B447E58D3D96F
	1278794B1AC1366FEBBF0E548329C93F4F61796DE8027C16AEA41B0BB4A572DB75E3D10FFC5EE63E73DBDAD0EAC914BFAD62F29FB049CF06FA04495FDBD19ADF3E610BCC0777ED22064B7EC458F85ECE21D763A742464FF6220DF5E84F85D8E4501EA602F629017B49C1B1668683B7BCAC3E6DD41D9738159601F31D97F1BB07DCDDBEDEF415FC0EAB66E3F95AB35467EEBF9F1D477F1662971F0F4E63BFA3421FD3212FE4C82C77CF4E19EB29425B36DA84DF345C0BC3BCCF9E0A7E86750DAE0C1BCB813F9AE093
	C03154E32DGE882F082F8GAE82A48124GAC83D8893096E0A34036E1088F43A2EFF96FE4A5C91C8B73021BA6AF1A443E81711CF3AA1EADEB07193E6D433167AB535F590331E0907A43G73819683ACB774891C3C6C5DE7EBF8063B4FFD361956F8ED9C3D781D694EE83ECDA65A4211C4397DA78EDC6B72916683B986DE2178FA931E79C6A65DDB58F6C05F129131974E46754DE2972F29DD0974DEF9F02679B6A664F38F1B793AB91D0BBF4389799E37187966F23A057C191A106FB7F48770BA5C74FE9D397635
	4BED57D486736B60A91C9373613413FBC902BD856EFB9CC2F4577085F9C4F7CAEE0367FBDD09F3399FF4DDBE17FB28ABFEBD70E8D7ECBD70332E7875C0FAD7ECBD70E4977A2A9B624000C1773401BBE3601E23E3A22EDFF1699E837FBC558B716DB28AB46AB32A57A4458DF05CA6936E0C38876B467C92266C71EB46BDD8B7FE239AD5D41535DF21B40A8F52D3439E855D28239210CCFA979BB8541B55A71F53F3F9B9786DEA1728D9FEB5A5EA16B5AE911786FB922F4FC8CF7C5AD46909DF1B7AFBE63EFB1E6CC9
	D43BB27D5AECC23FDAD968FCAFBD472F07F632012B5CAD68164AA24E389C94B75AE3AD9F95B8BF4555C3FB40403D5819E84FA06F556E15F24E4BE494B8924978F33E336770A73341B8916B9B9E95F679A991F6395E40BDF7D060D69A38AFE89ED36E86BB3B85EECB9D3D2B00760001D36B842F4B40E5DA84EF2A017BF21360DDEE6056B599F25D623BFA52947D868B8E45F2567F6E4558426A261726C45DD46E09DD87F3CDC47F4685E89CG6FF23B8CF9A2398A9B6FE31B98CFCDB01E5584F32246CB72C457E15F
	5F99DB8F2E5EA9EA10059ED68F26615343EAFA26C753EE21471A84FA64F119747BA850ECD3D86E0F38B2A5DA0F4FEDF84E1AABBA067F219B6D5DB546F02CBE3943E12ECF2A942617D391750B3DA4DE3F871EECFDDEF4C6566799A266F1D1091D07B54A2673156976DC24C42F0F25DE0C37A441F8E6F8A0EA3C55B1F639F51846CE3FCFE777BA5F22BE3E8668B29561FFEF55CD773F313E487C7E2ACE68D3F6A17EBA65187429859A8421CF7B0508BECC7EEBD6146F847C5085E14FF97D91F956E6A1EFFE9CF9564B
	48DBE41247EB20CFC58D336442ECEBC770DB2A57F7B0DC71F1665944B8EB930C43F8D662D143F3BF32CB5485E5E390BF46C4ED543ECB54397347C48DB6C1E917C3BB83E023C1BB3EDB48ADD46349E577C271755FF60E9876AA68453CA5CE4D48B606B14F558EA12FDD95BAFCD602B82CD56798386B0684DDDADC3D04BD768DC6EC78E13D60491C11E77D4711B9040689913E7C7BDA7C666A13A74B4495E1F59BB8F645BBAEC5D27D6EF72A4A76D715559C289472D26B043C8AEFFC5BB1998B64487BC71E083CCBCD
	D7A62F36593853728A5F9ED4F0BCDE2BFCEA4E251301A645BB73594AEB15E9B54EB260D9B9AB3E4D3B2E2EEEAE3ECA3E93D74C47EA27E7296F2F75C6575BAFD07C86EFF43DFD5158D74487E730CF64B0933B4D721783BE5DB79BBBBD610827F7A6674D76A72AF16614883E9293CD95FFEF02279340E36AE3FCB78E1965DD047E79F1E4444A2B2FB773AD821E34D9717DCB1D19EF8570E44C0A6F23E6B35F8660498D4C4C4773D59AF3CB83EC5D982E1362EA27616E3545525DEFB353B1BDB2319C626FAF195E3F34
	6B5A5712DF3F26B2581CEF5E9DEB072F6C0E3557869A1B3B81B7949079641993A21FD48D5C5F53F83D945A2B8D1C5CA8F85799387FE8943C9B8D1C031ECD6D63A0E3DC603AAB69BD8F34679A3885C331BA5FB9942BF3D7FDAC1D33BE166E113AD83AD5F531F45D4D31F4271B4DF46C3C135E4398416F2EE8094A7025617C88DE1428BC5EEFBDB1BD8F3AF481FD0D65A50193B35DDFC467F98B41CE88BBB9BEF5BCBAAF3B2F847588E872282CE9320B7D69A64F11FECC57EF99702C989767481DE391DD1EE81C4DDD
	CAA2DDB64668B2328B4FF62F94D63CA1AF55046971997505EC8D5EB511AB3AABA9AD98F0BA6DA56AA30DC0572E4DF4567EC80D3C2F7592C307330DEF112833562EF8E59F7EE1F945418F2B4A8E54D58A3E2D7E4898550148982D15339F23E7BC4217C5E643477EBF6A2E5EE6F0241475A4A7281182033E03FE0C48187830417A54DDD68B496EA01B5947198E120DDFD8BAC88E7DG53C12C3ABFC0AF91F34C2013FCFE9BD4C6EF91F4D240AF204AB8DEA73BAA783DD1C359A9D9F215357AE44DBDD529135D118E37
	2C53BFB6AC9B1B74D486474997547653C499D892979B1FF6B6A3E8A4FFDA227423BB6A1EDE714B3E2015EC6EA02FE18D52ABEB74AAD9F6619F09727F9BE8A3F9817CFBC374A630D610BEF2027A14AABBC87168A97CDB5A20AE3B1A45871220CEF6BAD052BE7C0353FD6DD25023634731C38E126D437FF0A0BBD837AA795C7B580D75A1EA722C66A62AB34686827A96D1FDF114EAE314226F956CFD7B781AB4AB313411D7D43F64EA13C6F43F56C172F40FA687BDFE55958E9CE58365CE0A54588A433DE1CC9FA7DB
	0D57B10652E36185F373576637D9A00B5148BECA0921265A20F035F8A48DC449DA070E7F3161574A0D3E6B5E525FFB78586EC18B49621344A5488F87F432F9D472AA6A943C58E9DACA97E73B8E5AEA12AFB86A573C20D9D914E6224B90D07D5587775F7A4D5F1D2582A22CBA5943CC892C7B5C325F3B2FF9F4D4714946GE0D7987ECDE3F824290E2259913EE56771D5AF8F039F5809553BFFBF7DDDC3FF9F405FB594D3C3B1B587403819E4461F687E3018B3F59EE3E36272D737418A6ABFDF7F601D3FE84CDC87
	33EBA3B9F40340A294C628CE063C4332F6E34A2DEFEECD4A78B22ED1GDDA50C636E8C475D8504E23746F895E28BE25D8351C546A2E1EB97A5AD213F5F3862A9A3571B14F70F21695E841A02D876E5C4A7162ECE03863F5496F3F8852C36A3B67CB54FFB7C31147376E28B31C0C47348AC0465CC6201CAF794D6159D89C6C46C9F275198ADA26EE4ABAF196F48B65EE0C978770601B07F1F4FF0749DD9D5BFBDAF811F9E965FEFAF76212CC45F64FB3D122A6A12BA566CF36177F95B14AEC73E5756746378C1C143
	BBD9DFBD7D5F0F8BA61E7F87D0CB8788E0FB2B2E7A94GG14BBGGD0CB818294G94G88G88G4C09B8B1E0FB2B2E7A94GG14BBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB495GGGG
**end of data**/
}
	/**
	 * Returns the buttonPushed.
	 * @return int
	 */
	private int getButtonPushed()
	{
		return buttonPushed;
	}
	/**
	 * Return the JButton2 property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JButton getCancelButton() {
		if (ivjCancelButton == null) {
			try {
				ivjCancelButton = new javax.swing.JButton();
				ivjCancelButton.setName("CancelButton");
				ivjCancelButton.setText("Cancel");
				// user code begin {1}
				ivjCancelButton.addActionListener(this);
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCommandLabelLabel() {
	if (ivjCommandLabelLabel == null) {
		try {
			ivjCommandLabelLabel = new javax.swing.JLabel();
			ivjCommandLabelLabel.setName("CommandLabelLabel");
			ivjCommandLabelLabel.setText("Command Label");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandLabelLabel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCommandSetupPanel() {
	if (ivjCommandSetupPanel == null) {
		try {
			ivjCommandSetupPanel = new javax.swing.JPanel();
			ivjCommandSetupPanel.setName("CommandSetupPanel");
			ivjCommandSetupPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCommandLabelLabel = new java.awt.GridBagConstraints();
			constraintsCommandLabelLabel.gridx = 0; constraintsCommandLabelLabel.gridy = 0;
			constraintsCommandLabelLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCommandLabelLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommandLabelLabel.weightx = 1.0;
			constraintsCommandLabelLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCommandSetupPanel().add(getCommandLabelLabel(), constraintsCommandLabelLabel);

			java.awt.GridBagConstraints constraintsLabelTextField = new java.awt.GridBagConstraints();
			constraintsLabelTextField.gridx = 0; constraintsLabelTextField.gridy = 1;
			constraintsLabelTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLabelTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLabelTextField.weightx = 1.0;
			constraintsLabelTextField.insets = new java.awt.Insets(5, 5, 0, 5);
			getCommandSetupPanel().add(getLabelTextField(), constraintsLabelTextField);

			java.awt.GridBagConstraints constraintsOkCancelButtonPanel = new java.awt.GridBagConstraints();
			constraintsOkCancelButtonPanel.gridx = 0; constraintsOkCancelButtonPanel.gridy = 4;
			constraintsOkCancelButtonPanel.gridwidth = 2;
constraintsOkCancelButtonPanel.gridheight = 0;
			constraintsOkCancelButtonPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOkCancelButtonPanel.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsOkCancelButtonPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCommandSetupPanel().add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);

			java.awt.GridBagConstraints constraintsExecuteCommandLabel = new java.awt.GridBagConstraints();
			constraintsExecuteCommandLabel.gridx = 1; constraintsExecuteCommandLabel.gridy = 0;
			constraintsExecuteCommandLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsExecuteCommandLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsExecuteCommandLabel.weightx = 1.0;
			constraintsExecuteCommandLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCommandSetupPanel().add(getExecuteCommandLabel(), constraintsExecuteCommandLabel);

			java.awt.GridBagConstraints constraintsCommandTextField = new java.awt.GridBagConstraints();
			constraintsCommandTextField.gridx = 1; constraintsCommandTextField.gridy = 1;
			constraintsCommandTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCommandTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommandTextField.weightx = 1.0;
			constraintsCommandTextField.insets = new java.awt.Insets(5, 5, 0, 5);
			getCommandSetupPanel().add(getCommandTextField(), constraintsCommandTextField);

			java.awt.GridBagConstraints constraintsExampleLabelLabel = new java.awt.GridBagConstraints();
			constraintsExampleLabelLabel.gridx = 0; constraintsExampleLabelLabel.gridy = 2;
			constraintsExampleLabelLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsExampleLabelLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsExampleLabelLabel.insets = new java.awt.Insets(0, 5, 5, 5);
			getCommandSetupPanel().add(getExampleLabelLabel(), constraintsExampleLabelLabel);

			java.awt.GridBagConstraints constraintsExampleCommandLabel = new java.awt.GridBagConstraints();
			constraintsExampleCommandLabel.gridx = 1; constraintsExampleCommandLabel.gridy = 2;
			constraintsExampleCommandLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsExampleCommandLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsExampleCommandLabel.insets = new java.awt.Insets(0, 5, 5, 5);
			getCommandSetupPanel().add(getExampleCommandLabel(), constraintsExampleCommandLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandSetupPanel;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getCommandTextField() {
	if (ivjCommandTextField == null) {
		try {
			ivjCommandTextField = new javax.swing.JTextField();
			ivjCommandTextField.setName("CommandTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandTextField;
}
	/**
	 * @return
	 */
	public String getDialogTitle()
	{
		if (dialogTitle == null)
			dialogTitle = "Edit Custom Command File";
		return dialogTitle;
	}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getExampleCommandLabel() {
	if (ivjExampleCommandLabel == null) {
		try {
			ivjExampleCommandLabel = new javax.swing.JLabel();
			ivjExampleCommandLabel.setName("ExampleCommandLabel");
			ivjExampleCommandLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjExampleCommandLabel.setText("(Example: getvalue kwh)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExampleCommandLabel;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getExampleLabelLabel() {
	if (ivjExampleLabelLabel == null) {
		try {
			ivjExampleLabelLabel = new javax.swing.JLabel();
			ivjExampleLabelLabel.setName("ExampleLabelLabel");
			ivjExampleLabelLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjExampleLabelLabel.setText("(Example: Read Energy)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExampleLabelLabel;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getExecuteCommandLabel() {
	if (ivjExecuteCommandLabel == null) {
		try {
			ivjExecuteCommandLabel = new javax.swing.JLabel();
			ivjExecuteCommandLabel.setName("ExecuteCommandLabel");
			ivjExecuteCommandLabel.setText("Execute Command");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExecuteCommandLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getLabelTextField() {
	if (ivjLabelTextField == null) {
		try {
			ivjLabelTextField = new javax.swing.JTextField();
			ivjLabelTextField.setName("LabelTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabelTextField;
}
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
				// This listener is not used because it's calling class implements it instead
				ivjOkButton.addActionListener(this);
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
				constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 0;
				constraintsCancelButton.insets = new java.awt.Insets(10, 20, 10, 20);
				getOkCancelButtonPanel().add(getCancelButton(), constraintsCancelButton);

				java.awt.GridBagConstraints constraintsOkButton = new java.awt.GridBagConstraints();
				constraintsOkButton.gridx = 0; constraintsOkButton.gridy = 0;
				constraintsOkButton.insets = new java.awt.Insets(10, 20, 10, 20);
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
	 */
	public Object getValue(Object o)
	{
	    //update the commandValue object with the changes in the text fields
		commandValue.setCommand(getCommandTextField().getText().trim());
		commandValue.setLabel(getLabelTextField().getText().trim());
		
		return commandValue;
	}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		 com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
	try {
		// user code begin {1}
			//set the app to start as close to the center as you can....
			//  only works with small gui interfaces.
			java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((int)(d.width * .3),(int)(d.height * .2));
		// user code end
		setName("CommandSetupFrame");
		setLayout(new java.awt.GridBagLayout());
		setSize(526, 145);
		setVisible(true);

		java.awt.GridBagConstraints constraintsCommandSetupPanel = new java.awt.GridBagConstraints();
		constraintsCommandSetupPanel.gridx = 0; constraintsCommandSetupPanel.gridy = 0;
		constraintsCommandSetupPanel.gridwidth = 2;
constraintsCommandSetupPanel.gridheight = 6;
		constraintsCommandSetupPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCommandSetupPanel.weightx = 1.0;
		constraintsCommandSetupPanel.weighty = 1.0;
		constraintsCommandSetupPanel.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getCommandSetupPanel(), constraintsCommandSetupPanel);
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
			AdvancedOptionsPanel aAdvancedOptionsPanel;
			aAdvancedOptionsPanel = new AdvancedOptionsPanel();
			aAdvancedOptionsPanel.showAdvancedOptions(new javax.swing.JFrame());
		}
		catch (Throwable exception)
		{
			System.err.println("Exception occurred in main() of javax.swing.JDialog");
			exception.printStackTrace(System.out);
		}
	}
	/**
	 * Sets the buttonPushed.
	 * @param buttonPushed The buttonPushed to set
	 */
	private void setButtonPushed(int buttonPushed)
	{
		this.buttonPushed = buttonPushed;
	}
	/**
	 * @param string
	 */
	public void setDialogTitle(String string)
	{
		dialogTitle = string;
	}
	/**
	 * Object o contains value of String, the DeviceType
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
	 */
	public void setValue(Object o)
	{
		if ( o == null || !(o instanceof Command))
			return;
		commandValue = (Command)o;
		getCommandTextField().setText(commandValue.getCommand());
		getLabelTextField().setText(commandValue.getLabel());
	}
	/**
	 * Show AdvancedOptionsPanel with a JDialog to control the closing time.
	 * @param parent javax.swing.JFrame
	 */
	public Object showAdvancedOptions( JDialog dialogParent)
	{
		dialog = new javax.swing.JDialog(dialogParent);
		dialog.setTitle(getDialogTitle());
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setContentPane(getCommandSetupPanel());
		dialog.setModal(true);	
		dialog.getContentPane().add(this);
		dialog.setSize(526, 145);
	
		// Add a keyListener to the Escape key.
		javax.swing.KeyStroke ks = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true);
		dialog.getRootPane().getInputMap().put(ks, "CloseAction");
		dialog.getRootPane().getActionMap().put("CloseAction", new javax.swing.AbstractAction()
		{
			public void actionPerformed(java.awt.event.ActionEvent ae)
			{
				setButtonPushed(CANCEL);
				exit();
			}
		});
	
		// Add a window closeing event, even though I think it's already handled by setDefaultCloseOperation(..)
		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				setButtonPushed(CANCEL);
				exit();
			};
		});

		dialog.show();
		if( getButtonPushed() == this.OK )
			return getValue(null);
		else
			return null;
	}
}
