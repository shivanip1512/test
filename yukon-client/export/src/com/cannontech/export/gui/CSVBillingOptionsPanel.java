package com.cannontech.export.gui;

import com.cannontech.export.ExportFormatTypes;
import com.cannontech.export.ExportPropertiesBase;
public class CSVBillingOptionsPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private javax.swing.JLabel ivjStartDateLabel = null;
	private javax.swing.JLabel ivjStopDateLabel = null;
	private javax.swing.JCheckBox ivjAutoEmailCheckBox = null;
	private javax.swing.JComboBox ivjNotificationGroupComboBox = null;
	private javax.swing.JLabel ivjDelimiterLabel = null;
	private javax.swing.JTextField ivjDelimiterTextBox = null;
	private com.cannontech.common.gui.util.DateComboBox ivjStartDateComboBox = null;
	private com.cannontech.common.gui.util.DateComboBox ivjStopDateComboBox = null;
	private javax.swing.JCheckBox ivjHeadingsCheckBox = null;
	private javax.swing.JButton ivjBrowseButton = null;
	private javax.swing.JPanel ivjInputFilePanel = null;
	private javax.swing.JPanel ivjCSVAdvOptions = null;
	private javax.swing.JLabel ivjEnergyFileLabel = null;
	private javax.swing.JTextField ivjEnergyFileTextField = null;
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public CSVBillingOptionsPanel() {
		super();
		initialize();
	}

	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		if( event.getSource() == getBrowseButton())
		{
			javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
			chooser.setCurrentDirectory(new java.io.File(getEnergyFileTextField().getText()));
			int returnVal = chooser.showOpenDialog(this);
			if( returnVal == javax.swing.JFileChooser.APPROVE_OPTION )
			{
				getEnergyFileTextField().setText(chooser.getSelectedFile().getPath());
			}
		}
	}
	/**
	 * Return the AutoEmailCheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JCheckBox getAutoEmailCheckBox() {
		if (ivjAutoEmailCheckBox == null) {
			try {
				ivjAutoEmailCheckBox = new javax.swing.JCheckBox();
				ivjAutoEmailCheckBox.setName("AutoEmailCheckBox");
				ivjAutoEmailCheckBox.setText("Auto Email Group:");
				ivjAutoEmailCheckBox.setForeground(new java.awt.Color(102,102,153));
				ivjAutoEmailCheckBox.setEnabled(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjAutoEmailCheckBox;
	}
	/**
	 * Return the BrowseButton property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JButton getBrowseButton() {
		if (ivjBrowseButton == null) {
			try {
				ivjBrowseButton = new javax.swing.JButton();
				ivjBrowseButton.setName("BrowseButton");
				ivjBrowseButton.setFont(new java.awt.Font("dialog", 0, 12));
				ivjBrowseButton.setText("Browse");
				ivjBrowseButton.setMargin(new java.awt.Insets(0, 2, 0, 2));
				// user code begin {1}
				ivjBrowseButton.addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjBrowseButton;
	}
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF2F0F3AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BF4D45535780A15AED9163EE29FD23B1AE7D13132DA5A461A5752D77B24ADD5ECD131C4089885291F2031C69B919FA7135FG79812104C7FEDA40442679C0442C08109FA1A103641990129210494C4D4CA4F7BE1D39E3A6CA656DFD4EBDE76E4C5C19G2DAFEBED6FBD7BF376BE676CF34E5EFBAE12620A386C8CC3B1A19959C47DFBB70310346304342D29EC60B863325C9C2279FB83E09B39F7AC
	8B1EA5C05B3B4B18D3C4BEB9ED04F68D34FB0B0CB93B60FD8739F43A2485DE2268C9BDCA48B7EFCFD89EDB0F6F885713CB5BFF177260D9GD08F38FE00B800A2F223D422628350AED05F91B28CE057034873CDF309863FD665ABF910BDB7D21E05D229CAD720BD93E099405A8703FDEE2656DD5B5C2436073F92C7526E5BEF48C7DD1A7EB9F4BC44E46D6A3312FC07CEC4A6F9FAF795BC8F770E5B0ED85C4EDA0B5965F23B9449E22F15829E37D7E9EE6EE86B75FBEDD22F59A549098F21FDBFB79B29ACA1F150E6
	36BC18FFD855B5FBBC0EA4FFEBF7D4DBCA9FE6B634BC9C344513FDEEF36A43587723F4EED1EE9A3493G181DB3A78E4534335666F7B4B737778D34B9E45961321D72A88E374BC72DA604597D46A8373BF1F9EECE7CF244A5DEC0FDB23CCF8738F9B99F4F7BEF71718CFE7ADD12BE52EDA80CB21E74656AB89E890EE7498F9E4B5ACDFDE553D1DC6FE120CD83188D10G1088108E30077A680F0E3C8D4F5691255D6DF43ADD2D9ECF1BEC751EF4DBA5995E1B1B012262BE14ACEE2F159066077719EBF46C61D0745B
	D09F8C7A4D99427DE641073791C5B9EA485699DB7CA3D5063DEA9FCD3F8D0E0D0D6B6B2DB82EC140ABGB3816681AC86D87BDBBE2E2C2387279AD7B72C2157E5167B7CA39E33D7E973BBE42B64D547773894EBFCFA406456DF319C1B17798735731274039306DC1DF9C91ADFE7A0AB54FD3BA2B8AFCC777E51E83A1BD6C42E875D3BAB508FCDE674C3933CE728BE4A7083949F274133FD321A7AAD3697822DB8117B6DE0819F03A9EBAEA9EA68519D431C7FEAB1849219BD33924357F6229D4F1FC500CF84C885B0
	BE1A1BD3855081B078A8DF5FD3C76BAE53EFFBB5EB4BE40DADFF042794D03C66EE17CFB13BAC5200596BB00F48120F28BC5BDA5076119039BF36205FD9C047F149A5F94D384B21F733D382C772B11E0A16ED486315A8355F6114C00153C3F83FEF356C0027EC76A927BDD633A285B9983D2E856772E373G74889D20E7BC0AF37E84BD778270BE9B004D55F7BB9017826D2C15FCDE3E5D329D1EBD6058128C67274302E6183D13DC7F3CD6BCCB240FA51752A1F9A41795DEFCAA6D4E564D41F999F58B1B3FC77138
	C75A41F01F405F5D3A0D61218F6DACF37ACFA95D2CA8DE4708DF117AA4C50153B1A87FCBEA0BAFE47B79889B5BED475047CAE0DC75GADE2FC3DEDBBB463B3395D63FECFBF2E1CBACF6F53F3369F7865157C4E7C500CF7682C155CCF1F9F64FE5A11FFAB29F924DB57CF91962EE47E39E4E578DE9B26FD86F52C3DB0F8057BBE9CE62D545F2341336575FFD8C7E68C9A1B775D47F15ED341163CD539B9B52B783CDF5FD64976DB1B59A7719DF64A8BABA6D903DE46F82FE90F6EE3B35A75FD6C06F6FD9F1B5D9E5B
	07EEEE5773A1C67BD6BB5EAD6E891746A76737EF27FB5865122851C204303B7E79FE1C8B074B211CF1D8ED12526778C402F5DE053E15CE77E0873C8F2E623EB471A76ECBFBBF79F7F24FC7FBF56F33B937369B962EE26B3BF8D5501718CD719DB86712ACB9E1225A5DFE17225E4DF4FF2BF875FCD9F0CC1F777B47509F4725C9156F0747B0187C58AC7BA5B13605662DB847DE0793168AB72C9A97760F064405E09F461046245C1CD200CC5ADFF5D3B12E3F75E3BCAD2DEA045309E79F731B8F3B502EC6606F8238
	10447D66C067EE4DFED59D265DAD4BEC7A453AD49CAF42F5147E6617C081EE689EB1167D473707518654F1B1FA6DF19C172CB98B82C9B82E42919EF361DD41F07BA96ECED83CE732795DFB32C89B4BDAFD07F55790E1D192DB3BE5C9617756F55D58EFAA6049E3FC6CF79C571ED52DBE1F5B622057023A9EAFD5633C5338F24C7C31B9D06B1BG5FEEEEBE2109F77F131EE748DF7C980F9B1FF5507B9A5AD6000BC25FE0D7243F1C6B5A824FD1075787710ED58A70B933F46158719ED251BCBEBB201D0BFB126B23
	4E456C47589C4CFFACFCAE9EB801F6AC86FC2230EDF557B64DDCC0AC89572E8DD60A5BB333BB13792F5A1E551D4D7D1770736F5E0878A665316019C9D6C71E1FAC0EB9BF0673D53CBABC3E31D37C5E556171CD2A0347B70DC01B331A5F3777766038B6C2BB6FF1BE2EFDA772B5636AF5BBDC0A9853C3A7F08F3A3D8E1B4345F1ABA88EA2E9FAA831BBEBA5DD63D268339E00616E3653FB8C5A72637C0E31521CF316E83BE8FB01E83F6278C76F0708BB6971601CB29D05E37FEC9D5ABB094D51129EDC0F00E8AF25
	6DC915F787F9F1B4077A6E245B8F13FDB899674BE863E758B23079E900A2406FCDE6F1G5BEF5F1B40F3E2948EAD58E6FD9E49C2CA1EC059DF5B396CF4601F8310G1088101E4C4F3FBFD9F01DA14703A3D0BD7D08482B47424FCF54F9817AD6G18CF555BF8EEB993F00B1F60B6B5DC40F8D71C81356689C8FB0FFB9D56B6334DC4C7484E01004D284AEFFC021F833751BE9B1F4C4DB181D81F64F7D2E0BF4F9F975CB61718261D504D07137B9B8C971FE46B9B77E4F08F47C37E48F42659394EA420972FC15C1B
	0E623035FB9498EAF1FFD1FAFF889D777CFA2225FFB1040E76FEEA505293C268B8C7F52ABC9B5B7D7B7858B65EBF1F48F7B5C54D75075730B1CDD31FDD077710417CEB8DECECAB460C2ACFA72C6173F96DD69A7BC05B38B6B727F2AD5FD707ECD7178BF6DC255C5F2FD80E1DE781BB5A5F34161DE79982FF8D45F7E8704CA797881F6C85DA7CDAFE4EFDF6927D3964295C1CC6G930095E0BAC05CD37C5C7BDFD3BD19A27F6AF7FBCC90844A1A54000D6FD57B556548BF4B65EB9F7F23DBC8429753B336C6D97B78
	27581AAFD71F494DFBA81E0DEF5BA99CDF56BAD8E3008E00E1G8F400CF5FCFCB719EA58780ED99DCA2B2218ADF6FA2B72418DB82409A09A8F93B63624B16D58EE067E962E8B1E210B57C53B237E1207EB11380E2D51CE01EF21783C5F8571EC6D2E273E0A6D9620257C0E7B6AE7F6A27C68BA4D7B77ED7F6CF3BB52EF6F7FDCF530F5396EE4745C6206137A39456C137A39455C1331F30BDBCEC64FAD6EB8294DAD18DF1D694533EB4E533017G9281D2G48B35C275E7D60CD66D3BA7EF48C120DF6B72DC6A8DC
	7F7F1C0ABE56B7CECDDD43F87B9446FEA3EEC5F1BB31FF0E2FBF056B2538BD9449F06F1C42B902D0452EB0D6957F5EA9BCA7E5E91422D9EC7292BDBBCBE1DC754F70F8254AC96395E85B856EF6970FE1E6BE437D73E09E56FC13201D8ED07CAC1CB1G43G810059G71G4BGD21E65B21DD44678DCEECE95C097009DE08640FC00A5GCF8398D3001E42E53E32B58E4F01E759FE1B7DECF05F952B6F0D2A1EE87E16920306007687266041F161D8E36F1BC55456EDAA2EB00C4E4F3F9DB1746C1CE26F202F66CF
	4103E7E92C7AG73070FFA237B64E86F54BE696E25B98A78D27F24C7E2B8251746492323BEC9787EE4EF1140F57BFA5DBE8746DCAC965BB21EC37D5ACB0BCDAC7EFBE31C47E0A67081AB409C0085880F73986E28B3B2EE9CF0F23945A9ACEECC02E72AAA573B1E66E02E8E154738BEF2AD791D1A29C2A97054836CD45B9D6B0377AE33236CA44A75A9F02AF4189549E49E415F89986D4E31FCCAF3FBB4A415763E97FD24552F380FB94D8E395DAED9465B5C81C17F49C1BC07BEF0AB0ED10705A61A473DEE3F87EE
	689137166FBEBF4EED07A4BB1C8EC57276430A81D91D5B2E82ADAD4436BB4EED535A9D516F5C8F0AB436F3B223CD8CA32DCBB2DB71086526B35A175FC21D5DAE0FDF69F448127A3B49B2BA7753723471601C75ECDD96411351D70C6B51CB1E67747B5E5752EB1EE774FEC11FB526255BD57AACC1AF2E56526729742502DE532F27BFD950B714EB69E92AFC638B1C6EB92DC76F9274CD25FA768F831D659EB71E894FBD106E817A7497F86E31E91C6733B783EE798BBC77482E5CC7A26B8F35ECE3B5B79FD3ACF649
	5A26EEB375F72C63B24F8972D2F95C6C17E9FC896DE1GA5156B7D4CA9F29E40AD86E0FD7C42A9F29951C79A65EDFC9162CDG6B0B1C7785972FFFDC84DC85E167A06258DF6AD1BDDC7C0B78DB6A174EE00B450ECF64715831744E2F130E956999FFA2D1FED3FBD14D97FE9F4C099A9D2B33541A4DE8141A8D5DA94C663B73384D53FE4FF3323FF7EAFF23DCD458E008963BA628FA93B57A3362F9CED6A0733ECBDE626BF8E4E00F3ECD6204E03299C70C6AFC748AD91B9CCD166FEEA67B4BB12EF706105DD7D52A
	AF0BA7CA27C3122DCC36271A6B3D67A53E2E16FE5EDF3A686F8BA79A087E6F2C961AE4606F2C35FE4501BFEC041EBF2C4F068ADE6F2BCF63FAEEBF4DF1F601BBD446F547895C83E7B8AEB10D7B6463E56813C5AFC3DC815072B20FAD32652972174117C36F89A637E3CA397230B613B3CDA99791F7505C61993AAF15174373415FD37C3417437341CFE531DF0116AA46BB5DF9F5F96C3FDE255C7DD7AC275673DCE8FF7FAB61637D9B45DFF8A5FC3C9B4479A483ED59ABFC3CE9AE2D7E46BF40F97C0758F19E5B63
	3FB92B2D1D964D2DB4F0FD91762A7D597F90DEBBBDB50431464D00CFGC882C883B026C32E8A50885085B08C20G4C8408830887D88210881082C0DE65B9472F4FD443F340BC26E668184E3A3DB46BG24DAE8220907574CEA17EACC1A273786452FB2FB2BDE0D1C8FE67F463F62FDF5986803G9E0099G7305BDA503D37EAEFFAC9063F7F97DBD34C435A7C947AEA617F3C4CFAED5654F5AGB183C04D0670FA675231D83E58827C7D9B7476FAEFB51733EB68975577D9701C27ABE76F5753172072AE5D90EDFC
	07AAE259199CD5EE4169D8F2E9D1655EAB5313B33E4678CBDF0BA6775019F039FA8DEF070E9C5B43FD6538D787DFE3FBB8D760BF22F80F864F7676D3746C46768C2025G14913C876E5901713543A5BBDCF00150DF7205EDAFE0282D3E27EB5E2F11BF7F5A495C7F879DAED7F0CC931AF7627A7CF56F7EDCF530FA43D6B75E613D9B19AFB0DC914559D59CBB23768C635859F9F4E190E3E05D73910E229390561C9A9913ACCA3F59A6E4ACC351F360312129F3E05F10FEDDE6F2A832AE735910FEDDE67A1036AE43
	F0DF9A42784586E77A20603BC16006846E2B34CF441DF1D8953B405FC46DC2FC17048AB97E1B542E890A9B645F11E8F0C33CA6B60479B58C596E76F2596F8C61EFD4238ED9D6FF2726BC8E972475E2BE6E26B6BABCE60D5D0B84EEC82C6543B42E1E8EEBB9FBA32FB1B565739AD35CC6716D5759686B33752CFE0DF05BD97D9A612E33313FF129B89B2D06486CFBDB66B6278A1B2F17394D9B852E0C4666265761EEFF1D637E658867338B5C3B179017G6DE58257C8E3733550CE9738B5E3DCB66F3FB96EA7D51C
	EFD860ECB4B677C0FB2640A51DC3DC9C34978ADC6BF95E5FA201FB2794F1F91BF2F376EE6238CBB42EEF02F617402D6D67327D82F732026366895C3D27B9AECD60BEAC637DF590230A7BC199676B9538E7E87C9E876DF801338FF13EC44031F3E8085E97D599461C8EGEB8647CB94FF915AB783AC9278674A221DDB5847651CCDDCCF7433B12BBC1A0ED875B8ADE413E02DD00F16C94C7B73887B2E45E1166936E77852C0FE904F373DDA4FF9B68F772E821B0565531F1E03A6E1B531D3799DB16C892F09A54178
	91762878D4F867BC2C6FB56703FD5FA2CF55F7E60C3EB7EA7AE663E8BC92BA0E9D97023AEEBAA2EAFB0648B542BE36EAF415828F8257D5EF88577541A57A3D055BAB593CEE3F4B8AFA8D78FD78A85D3707613D8B0071760C611AC9AE3C9C2CAA5F48982F0DA4C01B5938E10C5B38D447464CB09B13D59BF72AEB1F26311169D8D745F510CCEE4BE75568879E2FB4AAF93D1215FD7624727F9BBDA30A0137D2703FF4AEF4CE8F1903F3BAF81E5BEB4A5437B7DBE36F8560D1GF8BDE6C6E6781C1ACAF1FCCEF0D227
	5FC9ADE3F6D96979B39B786304DDDF6B8F35EBE8A4E857EB7DE29EF56C4A8833ABD9350B2FF51A46AE561F350277C732744729755562AC569FDF172A2C707ED61E667DB5C669CF8BDDE17D8DE7056F290B1660586FAB63FD5BF57AC6194D1A3E153031DF4C8ADF93B298D9CB5FB548EB11B3009FEB69F1700C87405FE5189D1706449AC431A3D7338E49401316E59435F4E3F6389DEF9D443B7E18CB725AA631F84C8A560C7605329DA1B4D116D2695750EF695A3C6E891F546607E84C45EA2EF34A43EB3AE8FFB1
	68E6742EB361B5E124D789FAD2C0CBBF1C4D46EF05E7E42D122FD53CB5783E82371EDA533A6F882FCB85E24A9759026FA79CC44CCF75984A382409CA7EF9C9ED3BDBF6FB2B9A6A8F54D0F867E035DA3F081B5056AF1A369C0A7A6D5742EC36F60B55E73CF30F2AEFF28CE5C64D32CF92E37873B89F43C65DB170FAED7CD8506E572B347DB0FA129C24BFF82EBCFCDC9D8E2764420F262B5E292F29BCD0DF4D75FF69BC575F18232FBF7A3CEEF2865F5FF4877597764750FFB0D4FF4DE92EDF56554F7B6F7584577D
	468AAEB3AF0A4DCC467737204C5681AE1394D366EB5E204CEB2296DA1C8BFB15521F72D1EE1AC79F9D64B5B293502783B0396B86799D328865E83D74AE4A1B866DBC23B1E72F1173AE9C62BA8E9BAFF77E392DC6FF50565F1473FE94517725B37CBB1E8500E367C6E19F66F9BAF5E93649C5FFBBA6F9DDFA2D1147FB3F89299315EEB66654EF0E9D67311C72AEBF7DB698F8AFECB62AF948179DFFA651EBEF3DDE37C772AA138CCB3FE8D563417AF12D8D53203F59D3584064EEB82F151B8FB20BAECB2EA1248634
	8CE456DE165C5C101AD3BA4864ED399C3922812DDCA548B4DE169CA9574AF5014C70E5499D9B3C3ABAF45FD54A4D3CE2B9D64B2A9E22352CAD46307A75810A3F2841333D79E331B7E7GADE58B77613B06347AFB37C27E3175F266E9F9391E5D81D5F626CE9F6A6F83A1B53DB815AFDEFDAE09AAD7B22915CBD279D255E7D61EB127A4AFD41665F56B0EC456F91EBB222D73285F4F7BD1C66466EC0B29341F79710ED6739E47A7745772BEE0F459D8FF4747A3F5184635BA986E567311FC5FBE9F4957F5BA126F64
	69C83E6FD5C4727D30A2126F2D01C83E1A01C83EDD6511FC2565DABE3696F5D8E6D15F1BB46FBF9B7C7CEB03ABBFD79DEC4CCB07A2676141214879FA0E4ECD8D785D61BCDEC76895350AAE017BBA3DF766E0DB60FE580638C550DEAAF0FB5AC58DC2606A683DD315EF4CE94A6738769E2E57A4F073FA386C20406D5D07387950DE144FF39EC3C5E84E53B0016B4762ED1B48D116646B475BA1F9BA70A06CD4712979617176774F50FA1E5B25309CEC891D070D40E7AC6076BC5096EA4F6D0120BD875A39BDCD85D3
	67CC260220BDF837DFA8883727F3B2541EE1BA37B270858AF8BCC3BFFFD5757F38076B1F2F23BF1B441E0FC5917A5F5B07F910536C255F78B3FD57E3AA2D4698F3C49C3724A0D66C771D02607B9D4822767F6DB67CE6B0A44E6C0441D63DE905F34BEDFB332692E24D0683A2563434F3FD24B0D68C3834B8286F56DDC1FD6F76DC193E149E2E2F29104F7792CC83553D7D0D10984183BC538BAF67AE3237EB6566004C024B12FB31C7ABF78F48AC1FC20E6D2BF81DF377BFF44E5DCB6D11FC535BA3790C26C83EFC
	13160F7DBF821659FE81B336E0BD4976F9C9211873EEDAA0CF7E78E0F3661AEFE59AC8D6B749E1BF98F413AC2C7AF713EC7A23C0B749D45C1EGE68ED9DAD0C8897DD02B923F14282CF2BA6B2C563AC9782BEED648AE7CC6CB17328D3FAE2B241F17D5520F47G17F1D1A1DB4497E100A8048C526FF4D5722F39A087790CBAD736962E886BA5852179E9462D3B3ED0746503FE83B1F6129C4883BDF6B30C197E03A09359BAC287AFF901D7A1C5A6142D8359BAAA7B1E0998ACBE056C8962AD08E754BE0F59A2814B
	E62B59BBCEFBC013724E124D23DE5B88D5209043594E3320163610DCE0A2DB94BBFE210B479A4CE0306BDE7C5DDCA53CC79702EC9A9D3D49C37F228EF7FBE45A58FA4323AB96FC54D1ECA086D8ED3B4416B7FB820B3534DF5C3287E2CC6BF615277C1F30A0D700D5C89E2B31D4E211857C234A52BCBC699FF73B06AD32C3F2A9439637EB54E19BE6DC9F781DA31257D72B60BC66306A0B68987F8CAA102813179FF2B67D7C5F5FD937F47500AF1364F4135DF89574C9DE9A55C9D670F17ECF29BB49E68F7E73ACAC
	A042A67251C736CF191441210AC7BF44FFDE68D3A4EBAB0F974103F632E546EF0E6A3A4CBE3B0225DA701DAC977E23EF321D11E5334BD64702C766G992DAD54EEFA6F003DBB283D58CB2D68E5470F8B773DF1E2EDC2A6B1F412AD325BEC6DB4DBA0916CA61B953BD772595D32B560B94ABAB2CE6049C12B3C505D9EB1FC9CECAD163504516301EF4C4CFFBA3F93A6B060A5F51493BD33D6F0D85B6DE6AF28123C5D8A7EA3F637F72F20DDD32E6CFF60782EE18349D08709CB70E6402300D5DE334BB76A76BA4102
	4AB08BB8C9F4D47B55DF555FF847063F520E02228A2988EE9C1B64F656350E0EBADC1268G668F3AAF935DA3CFED984F7640D7F61EF8E453882CF78749F45657537FB6507F9E407FB6D0CC8345B49C00C9CCA5B17F4269816AF87B9566A462DC46E52E6D0415D2D62E7F55AD2B8E27264068BA55B389A63BA84845FC7C0B8BEEAC5BBABB79FADC8B8F3A846F974EB327C31E04178EFA2B8690FFA890E353D5C6F895E277C63A87222BA871ED7A5F3FDC7160D12A0E4AB620C2FCE98C35747DA816726F202AAFE96B
	0869576E0ADAC7346FE2F773C575997C8E6AC7B457CAG7C72DD3C1E32E57CAFA47AEF75FDCE33ACABE6F93C55E5455F6D5BA695A978BB6ECF4731FF9F67D1090C760BF134FBCCA373FFD0CB878886BA7450A099GG9CC8GGD0CB818294G94G88G88GF2F0F3AE86BA7450A099GG9CC8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDA99GGGG
**end of data**/
}
	/**
	 * Return the JPanel2 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getCSVAdvOptions() {
		if (ivjCSVAdvOptions == null) {
			try {
				ivjCSVAdvOptions = new javax.swing.JPanel();
				ivjCSVAdvOptions.setName("CSVAdvOptions");
				ivjCSVAdvOptions.setBorder(new javax.swing.border.EtchedBorder());
				ivjCSVAdvOptions.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsStartDateLabel = new java.awt.GridBagConstraints();
				constraintsStartDateLabel.gridx = 0; constraintsStartDateLabel.gridy = 0;
				constraintsStartDateLabel.anchor = java.awt.GridBagConstraints.EAST;
				constraintsStartDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getCSVAdvOptions().add(getStartDateLabel(), constraintsStartDateLabel);
	
				java.awt.GridBagConstraints constraintsStopDateLabel = new java.awt.GridBagConstraints();
				constraintsStopDateLabel.gridx = 0; constraintsStopDateLabel.gridy = 1;
				constraintsStopDateLabel.anchor = java.awt.GridBagConstraints.EAST;
				constraintsStopDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getCSVAdvOptions().add(getStopDateLabel(), constraintsStopDateLabel);
	
				java.awt.GridBagConstraints constraintsAutoEmailCheckBox = new java.awt.GridBagConstraints();
				constraintsAutoEmailCheckBox.gridx = 0; constraintsAutoEmailCheckBox.gridy = 5;
				constraintsAutoEmailCheckBox.anchor = java.awt.GridBagConstraints.EAST;
				constraintsAutoEmailCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
				getCSVAdvOptions().add(getAutoEmailCheckBox(), constraintsAutoEmailCheckBox);
	
				java.awt.GridBagConstraints constraintsNotificationGroupComboBox = new java.awt.GridBagConstraints();
				constraintsNotificationGroupComboBox.gridx = 1; constraintsNotificationGroupComboBox.gridy = 5;
				constraintsNotificationGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsNotificationGroupComboBox.weightx = 1.0;
				constraintsNotificationGroupComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
				getCSVAdvOptions().add(getNotificationGroupComboBox(), constraintsNotificationGroupComboBox);
	
				java.awt.GridBagConstraints constraintsDelimiterTextBox = new java.awt.GridBagConstraints();
				constraintsDelimiterTextBox.gridx = 1; constraintsDelimiterTextBox.gridy = 2;
				constraintsDelimiterTextBox.anchor = java.awt.GridBagConstraints.WEST;
				constraintsDelimiterTextBox.weightx = 1.0;
				constraintsDelimiterTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
				getCSVAdvOptions().add(getDelimiterTextBox(), constraintsDelimiterTextBox);
	
				java.awt.GridBagConstraints constraintsDelimiterLabel = new java.awt.GridBagConstraints();
				constraintsDelimiterLabel.gridx = 0; constraintsDelimiterLabel.gridy = 2;
				constraintsDelimiterLabel.anchor = java.awt.GridBagConstraints.EAST;
				constraintsDelimiterLabel.insets = new java.awt.Insets(5, 5, 5, 5);
				getCSVAdvOptions().add(getDelimiterLabel(), constraintsDelimiterLabel);
	
				java.awt.GridBagConstraints constraintsStartDateComboBox = new java.awt.GridBagConstraints();
				constraintsStartDateComboBox.gridx = 1; constraintsStartDateComboBox.gridy = 0;
				constraintsStartDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsStartDateComboBox.weightx = 1.0;
				constraintsStartDateComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
				getCSVAdvOptions().add(getStartDateComboBox(), constraintsStartDateComboBox);
	
				java.awt.GridBagConstraints constraintsStopDateComboBox = new java.awt.GridBagConstraints();
				constraintsStopDateComboBox.gridx = 1; constraintsStopDateComboBox.gridy = 1;
				constraintsStopDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsStopDateComboBox.weightx = 1.0;
				constraintsStopDateComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
				getCSVAdvOptions().add(getStopDateComboBox(), constraintsStopDateComboBox);
	
				java.awt.GridBagConstraints constraintsHeadingsCheckBox = new java.awt.GridBagConstraints();
				constraintsHeadingsCheckBox.gridx = 0; constraintsHeadingsCheckBox.gridy = 4;
				constraintsHeadingsCheckBox.anchor = java.awt.GridBagConstraints.EAST;
				constraintsHeadingsCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
				getCSVAdvOptions().add(getHeadingsCheckBox(), constraintsHeadingsCheckBox);
	
				java.awt.GridBagConstraints constraintsInputFilePanel = new java.awt.GridBagConstraints();
				constraintsInputFilePanel.gridx = 0; constraintsInputFilePanel.gridy = 3;
				constraintsInputFilePanel.gridwidth = 2;
				constraintsInputFilePanel.fill = java.awt.GridBagConstraints.BOTH;
				constraintsInputFilePanel.weightx = 1.0;
				constraintsInputFilePanel.weighty = 1.0;
				constraintsInputFilePanel.insets = new java.awt.Insets(5, 5, 5, 5);
				getCSVAdvOptions().add(getInputFilePanel(), constraintsInputFilePanel);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCSVAdvOptions;
	}
	/**
	 * Return the DelimiterLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getDelimiterLabel() {
		if (ivjDelimiterLabel == null) {
			try {
				ivjDelimiterLabel = new javax.swing.JLabel();
				ivjDelimiterLabel.setName("DelimiterLabel");
				ivjDelimiterLabel.setText("Delimiter:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDelimiterLabel;
	}
	/**
	 * Return the DelimiterTextBox property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JTextField getDelimiterTextBox() {
		if (ivjDelimiterTextBox == null) {
			try {
				ivjDelimiterTextBox = new javax.swing.JTextField();
				ivjDelimiterTextBox.setName("DelimiterTextBox");
				ivjDelimiterTextBox.setPreferredSize(new java.awt.Dimension(50, 20));
				ivjDelimiterTextBox.setText("|");
				ivjDelimiterTextBox.setMinimumSize(new java.awt.Dimension(30, 20));
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDelimiterTextBox;
	}
	/**
	 * Return the FileDirectoryExportLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getEnergyFileLabel() {
	if (ivjEnergyFileLabel == null) {
		try {
			ivjEnergyFileLabel = new javax.swing.JLabel();
			ivjEnergyFileLabel.setName("EnergyFileLabel");
			ivjEnergyFileLabel.setText("Energy File:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnergyFileLabel;
}
	/**
	 * Return the FileDirectoryTextField property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JTextField getEnergyFileTextField() {
	if (ivjEnergyFileTextField == null) {
		try {
			ivjEnergyFileTextField = new javax.swing.JTextField();
			ivjEnergyFileTextField.setName("EnergyFileTextField");
			ivjEnergyFileTextField.setFont(new java.awt.Font("dialog", 0, 12));
			ivjEnergyFileTextField.setText("c:\\yukon\\client\\config\\EnergyNumbers.txt");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnergyFileTextField;
}
	/**
	 * Return the HeadingsCheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JCheckBox getHeadingsCheckBox() {
		if (ivjHeadingsCheckBox == null) {
			try {
				ivjHeadingsCheckBox = new javax.swing.JCheckBox();
				ivjHeadingsCheckBox.setName("HeadingsCheckBox");
				ivjHeadingsCheckBox.setText("Column Headings");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjHeadingsCheckBox;
	}
	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getInputFilePanel() {
	if (ivjInputFilePanel == null) {
		try {
			ivjInputFilePanel = new javax.swing.JPanel();
			ivjInputFilePanel.setName("InputFilePanel");
			ivjInputFilePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsEnergyFileLabel = new java.awt.GridBagConstraints();
			constraintsEnergyFileLabel.gridx = 0; constraintsEnergyFileLabel.gridy = 0;
			constraintsEnergyFileLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getInputFilePanel().add(getEnergyFileLabel(), constraintsEnergyFileLabel);

			java.awt.GridBagConstraints constraintsEnergyFileTextField = new java.awt.GridBagConstraints();
			constraintsEnergyFileTextField.gridx = 1; constraintsEnergyFileTextField.gridy = 0;
			constraintsEnergyFileTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsEnergyFileTextField.weightx = 1.0;
			constraintsEnergyFileTextField.insets = new java.awt.Insets(5, 5, 5, 5);
			getInputFilePanel().add(getEnergyFileTextField(), constraintsEnergyFileTextField);

			java.awt.GridBagConstraints constraintsBrowseButton = new java.awt.GridBagConstraints();
			constraintsBrowseButton.gridx = 2; constraintsBrowseButton.gridy = 0;
			constraintsBrowseButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getInputFilePanel().add(getBrowseButton(), constraintsBrowseButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInputFilePanel;
}
	/**
	 * Return the NotificationGroupComboBox property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JComboBox getNotificationGroupComboBox() {
		if (ivjNotificationGroupComboBox == null) {
			try {
				ivjNotificationGroupComboBox = new javax.swing.JComboBox();
				ivjNotificationGroupComboBox.setName("NotificationGroupComboBox");
				ivjNotificationGroupComboBox.setEnabled(false);
				// user code begin {1}
				
	//			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	//			synchronized( cache )
	//			{
	//				java.util.List notifGroups = cache.getAllContactNotificationGroups();
	//
	//				for( int i = 0; i < notifGroups.size(); i++ )
	//					ivjNotificationGroupComboBox.addItem( notifGroups.get(i) );
	//			}
							
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjNotificationGroupComboBox;
	}
	/**
	 * Return the StartDateComboBox property value.
	 * @return com.cannontech.common.gui.util.DateComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public com.cannontech.common.gui.util.DateComboBox getStartDateComboBox() {
		if (ivjStartDateComboBox == null) {
			try {
				ivjStartDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
				ivjStartDateComboBox.setName("StartDateComboBox");
				// user code begin {1}
				ivjStartDateComboBox.setSelectedDate(com.cannontech.util.ServletUtil.getYesterday());
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStartDateComboBox;
	}
	/**
	 * Return the JLabel3 property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getStartDateLabel() {
		if (ivjStartDateLabel == null) {
			try {
				ivjStartDateLabel = new javax.swing.JLabel();
				ivjStartDateLabel.setName("StartDateLabel");
				ivjStartDateLabel.setText("Start Date (mm/dd/yyyy):");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStartDateLabel;
	}
	/**
	 * Return the StopDateComboBox property value.
	 * @return com.cannontech.common.gui.util.DateComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public com.cannontech.common.gui.util.DateComboBox getStopDateComboBox() {
		if (ivjStopDateComboBox == null) {
			try {
				ivjStopDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
				ivjStopDateComboBox.setName("StopDateComboBox");
				// user code begin {1}
				ivjStopDateComboBox.setSelectedDate(com.cannontech.util.ServletUtil.getToday());
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStopDateComboBox;
	}
	/**
	 * Return the JLabel4 property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getStopDateLabel() {
		if (ivjStopDateLabel == null) {
			try {
				ivjStopDateLabel = new javax.swing.JLabel();
				ivjStopDateLabel.setName("StopDateLabel");
				ivjStopDateLabel.setText("Stop Date (mm/dd/yyyy):");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStopDateLabel;
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
		setSize(317, 236);

		java.awt.GridBagConstraints constraintsCSVAdvOptions = new java.awt.GridBagConstraints();
		constraintsCSVAdvOptions.gridx = 0; constraintsCSVAdvOptions.gridy = 0;
		constraintsCSVAdvOptions.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCSVAdvOptions.weightx = 1.0;
		constraintsCSVAdvOptions.weighty = 1.0;
		constraintsCSVAdvOptions.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getCSVAdvOptions(), constraintsCSVAdvOptions);
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
		CSVBillingOptionsPanel aCSVBillingOptionsPanel;
		aCSVBillingOptionsPanel = new CSVBillingOptionsPanel();
		frame.setContentPane(aCSVBillingOptionsPanel);
		frame.setSize(aCSVBillingOptionsPanel.getSize());
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
