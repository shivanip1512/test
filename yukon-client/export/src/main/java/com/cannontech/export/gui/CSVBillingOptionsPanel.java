package com.cannontech.export.gui;

import com.cannontech.common.gui.util.DateComboBox;
import com.cannontech.util.ServletUtil;

public class CSVBillingOptionsPanel extends javax.swing.JPanel{
	private javax.swing.JLabel ivjStartDateLabel = null;
	private javax.swing.JLabel ivjStopDateLabel = null;
	private javax.swing.JCheckBox ivjAutoEmailCheckBox = null;
	private javax.swing.JComboBox ivjNotificationGroupComboBox = null;
	private javax.swing.JLabel ivjDelimiterLabel = null;
	private javax.swing.JTextField ivjDelimiterTextBox = null;
	private DateComboBox ivjStartDateComboBox = null;
	private DateComboBox ivjStopDateComboBox = null;
	private javax.swing.JCheckBox ivjHeadingsCheckBox = null;
	private javax.swing.JPanel ivjCSVAdvOptions = null;
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public CSVBillingOptionsPanel() {
		super();
		initialize();
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
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GAF088EAFGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BF4D4553569AB36340F36389E2B25BEFC459AE9DC45A5951E46BED80F27ACEB1525780AADAE29C6099273C1040881D2B819CCA041FCA61096DCA0C6089AEACC82212FF9B16113CC0004D1D293C2A8BCE012FBB3F313BBF307BBF71841023EFDFE775E19399310EA3356DE771E3D4FBEFB6FF37659E76FF3A7286A2D29C58536AA048A0A907B7BF381C2F54D885D775707D40E4BF8B6530ECCFFEF83
	D4220706B1B28FE82DDB336C8E946EAF06F6E9DA26FDFCD516FDAB3CEFC1D77A837B61A5011CE3C06BDB740B9F0CAD67C1DD4EC6523E6703D2F81683B8C056FE00A4G87FA6403ED8CBFG6DF2760E214086FABD077B9C9337197073D83FBC76DCCC7A3C70418EC66F03368CB083E05EF34618A5C43B1BDA1AD97B6C4DD3D16EA3FBEDE5D816E9FC8EB54FD35EE36CD90D6EA793D108B25E2805E7EB4F28674010622B9FF27B7D0ADF93073C75E2A42028DACB0B333DA724FA449E37DF14D31F477A7945E2420BD0
	923429AECF1435B2D913C62722345BEB926A124FF5D8E268B27E77A5058B1660314B6BCB995F85E8CF8620FACE887F4F18FA56FBC2D2CBCBC7EFFF3BA44B125FF3A220C90ABFC834C6086A5DDF4F752ECA4F34CFCA47382F89D81E8B5E8500AB695C1E367738BD83FF3B9B658D7659AA925833A81D5A01DEB06CF97831DBEDEF90DFB961456B5D8A348BG9A40E400D400C5G5B090FEE727C911E0787358E4567D37C078301F6D9D0BFD284D10677169620B05CA76210228A88D1BF8C0AF5967AF0D87EC2348F9A
	FEF38AF13F197E49B424E95D36A28B5BE6BD59E4333FC847587622E19B35ABCB42F6B5833E8FC0869887108CB06FC5EED71F2769DAF6F5419A2AFE375C9B9A8C38D52DBDA4490228B27B2A6A47324F8AA85F2EAF4CC767A52549BCAF0BE71E34ED34181745A9AD36B4B69FF926F921324FA4143DEDE97CFAE0E8DD0A7D70D56207F570DE40FC14625793FC1289CF7749B7899E37A782ADF7A9775B01F2EE034BFE8BF26C6D3734E1728347ED8399D41FC0C66C5A7EE294471FA900CF85D8G1081D07CD2263D8620
	79A53E3EBF72B4DC275F76185616728EF87FGCFB122296EAEFFD0F37B0744FE37AA3987E5B108D89F37976BBE9845B7CC76539048B8AA7AC5550DF7B90C6E760960C8C15AC771D662BE2AC828FD12CF84813EG6263063DDB60A93B035A49006056C4238725FF6E45F3F9495D8FA342G70BE7EA5BC675FA6F1EFG5EB5GBAD739B2464D07F63ABEAFEB3C1B61F99C9CDB14A1FECAC3D88D37FA194B4F15F0AC91AF09FE4DA986C43FGAFC1C6DBAB159873B22C68BA970CE0BC5EA39D20F8D047EF9C2924F898
	43BC4B1CDEC168EECDD3254110A6760A1A865151605FBA02F589C6ED3FA0E2FB5C07FDAC8F5B1E19E92F4E6476CD9D59E2324F25A82321C09FDEB9B6CFAA09337B213FAB131F19EB85FC0686B2391FAE9B60FE6AAC3B8355BD59E7692798A6E6D27FCC490C5DEBC5A29E5310719DE1608B6E7BD888E4DA6FD1A336CC7B3BE5940EC12373FECE41733EGF44986A84D62737E74C8B55DEF6D6E2048F7588995D6CC948CAF23FD1F98CD6CE30BC62DFD6C19D1EB9FCB9F9D5B0732C72DFC0852D60E6233C5897BCDBE
	19B73A196CE13FDFA4CA6BAC081E754B7A70DCC8FEC9BBA5899ED16B15BE95E11D3330EF5DC676E08D3CB7E7F1DF8A6F663ED47369EDE86E79864B73EC729D9F5BA6E65175CD4EB2FC096A34DA46F3AE4A228FA62AC38979B50ADFCF76B7433378720A4F3A6FEBBE6C0F2362E556EF2DEF8342F1C48E093AEDB35C2F63B9D6A59FAC955E30ACAF7CD6FDD4DE887A619CB2831E79G05E43CBD4DD5F87D05CBB8DA8AAC43B902E39F751B9F8430DED5591976DA00C3595CEF427EB7CC7B15B9CC07A24BF47A75F591
	9487DEC771E2C88481F8C39F57ED99D5B647507A19DD149ED430DD32A9968CE4E33BD275DC911F959477B841FD87962FEB83BFFBBFF218F3D9A1F854F28DB1CC4926EBB7A3BB765C1AFF910F3BG70E93A6D4B94F32CBA9C8CAAC392B99658FA2C5CC372B31CD70E38AF39A37541B078F6CBCB37A95FDDC962996E1F1B4D734637761173BAA7536E84B81B43655D9A0877179F87B641F3D8D203106F88E2044FD9CAG5B0E4FA106667959BE73DC4CCD7BB061DCE8B9F48E4667444E4566A056A39970293AEE3BAF
	D61A6682F2C9B8F6BD30D2DC1FA7AE96D27FE56DA7AF96F17FC5BC7E654565B773F30C181916939FBFE99E73AFE43EF2F3E2731BDB895E3EACB63FF9E79F4FEF2A0096D9464F5BE28D5B35985A594B38DDA3EA19492E9EC572EB3ACDDB03F88FAA2A64117C9C370360A013A6C1091ED9958DF85CFC985371B2540F84175DC84EB1E83BDE66E74C8F7711F3CCEF5FCE5A9374767B7B7E5E73A16ECCFA5918D3AA4379254BB01FC9F40E4AB43C9E913DDDC95A17D95FAD685D068D44F7AFABA1186C9D4B71FC35B570
	98B683F4FE98E0B900FDB94D8368FE7BD9984709E188DA304DFA8362904AA33C05FBB92FG6D8840E400D400C54BF97C5BBD045799EABC8801AC7AA1DED7AF5C979BBF314CC32FC0FD82C0FDCAEC6035258C3864D738CEFBAF60FCD70F81756EB0143DC7D5C9E8F7FBDC44C29A876EA2BE0279973F42634053E44C2A951976BAG678AFEA6C55E65756374E937A0D752C94BFAB82D2F55F6F685DD5F2495469E1E8575A31539E7AF17B99B6839ABB06E545E2A1835FB8ABA54637DC5685F0A2263BD1F034C747F08
	22E3FD7FE6B353F32268F80E9A98BF356D51F738ED790F26A0795FBB92567A2DAB29CD9758F3A0329DDDE87195D25BCA1B0A19CFCFDE4967F3DE9F49FD201D8100F2793E9AED3831DA70728D725D730579E8BC3B2B916B3FAD174633829DFFAF4157187054A71F53FD3291E813F2F91CFBB0047DB98F5AD52F02EF81B881840024D7F95C7B3E56082ED1FF75A98197A4013229B42076556F3D319A793FB672351F75739F2354EF945ADECF307613DE25EBBE0BBD17DF5ACE7054BEF7985B178EB4348A6ACA00D600
	9EG6FAAEE5FB3DA9D352FD31034431A669E7212D3159B57AF09E1830D0389352D3249EC5B9598EF62AAA306A62FCAF4C6DDB80957A2F595DD23ADBADEA5788C931E2E5DA344D7F1FB5BEB1976D92FF1DF1D0917057951EC537B5206AFBBEE477BADEA7CAAE550F5199FCADCDB7CAAE4DDDBBC95322EAD1E8D0DDDDBAC8DA52EADD6074C3585752B1FC4F06C6A494B344BG93811281D27338CFFD7352AE6AD3967E548945C607C2EEA3B4AEFFA41C58560B61EB5FE1FC9A46395F2022E90A8F0F4F71C9E13CDE1A
	92A0C80A3BA90C6788D2952FC63BB27CBF07F11C1445E1022639C9AD091D8B412E0CBC1E2FDCE8A3775F2B21D6DF4DF14F9E60B90CF3B5774F6113B5F8FFC2FB8640FC00EC002AB51976C6G97008CB0FE8D6F9FA17D1321BD8FA09D403E9676AC0093C0G9887103C1677DF50B771BAFC894BB33731CC2C8BFE9F3FA6F19D6EE2FA1AF1B3188DF1B1E8F5EC6E745AF56976FACC3B127117A33A5F9371E1DFA90B41E19FAD31689B1FF35D9DC96CEB77C62E6DEB7F99A1358778C85F6500C8F173A2A47F9D9E8E0A
	3ACF7FB26250F1DD419EA5A861DC0A66D8871BB690FFD549A5924D6BCEFF48F32BF930460B819C79307EGB86F25FC613678FC703BED1C2FB51F6603FD7074B23E0773F11FD5871C2CCFD2FE7C7A7133321041A468138270FB561E1DEF1C27D40FB7BF42FC3D9AC48B27DB93DD6EC1FC7FCFE9BF9DA9A3B4A5E0A2B1DA1B0A5778F0C8D3BAFDEEC96E700AC3236DCAC4274FE9427165E3C5130625A1D2C09ED515D0GCE5EC1455C6F11901EDB27A8CBBEC99355BED8B1A0D35A26E365E6DA14EEF77EDF25D96F38
	F1EF7958E1521D13595C1F4534E322DB4021132BFE6CF7F8FEFFF6521C4BB9FEC767FEBFBCA9FD334B0ABED127971DB3531399FD3ECE3F6AB45353983DF89D2777F61B69BB56D13AD327E73CEDA5FFC027BFF945CC17997FF41D3EC130224F556977DA6A3FF09D2F897E5C4A6B4C8C40D52F67B5C1D175BBA87EDE201EEE0C1616CEED48AB8A6DEC1B306FCB1F3772DCFDE0BD4FE76FA263CF04F68A405C75DC6E3DEDBC6FDB349E37336CF40CF5EDFC0CE30023E3549DA05FG20BD99E0065E773D037CDE62E140
	3D45E29346513F65872DF028A08BCA5627BEC70867F44F15721CEE47CF7F95B9FFD3E85B0D2CF3BAA77012384A1E38D6F16D7CB79B3BCB99CEF017C2BC1D6A1CF1126BBC9F4620722F9EB1FFBB1CDD511AB027345B285C9A1BA15F7EB82F153ADCFCEC8168D427837D5B2DF552F7B865EDBDD74C66E30A4E6B119371725DC9F9E7B9395C05BA6FBB35BB2CF9F1C4B8A20932C0F93F5D4D65169672F5FDEB979F4FD948477BFA77DEE47D7DF308A47F787BE7FDC813708707687841EA2D2B7C9E6E0AAE27CD6038D9
	BAAE679C171D8DB8FAE63635DE2BD62831E7C545EE4AF77C1AFCBBE35A14EF5CB579E2016669EF13BDF588F4092E3D7EC870AE931E6E41192DFC8F8EGED361D5BBB3B6D46EA46C7EE102F70400D59BB1544056A22D8FB6FA4783A22D8FB779D6076B683EDCA9137376E20D9BE5A10E52F5A1015B07FAAC7BCEEBCFE46FCCF693865FD9B17975B3F160D37FFC3D64CBD650F8A70796F857CB800940039G8B81328150C650852096609040G008C1084B089A099A095E0BEG4DEDF622BD706CF70F30EA98170E0A
	CAB2FCC032CB9D12642BEEFACFC867A06724559A24ED247A66EE0C1F8F2A7FD0A7BEDF0A0B336CB5G2DGFDGDAB157470B2E798D3CB3B246B7700FDDD6FACDAA267A2494476BC5798E1F33621B4D7AAF00E7BAC01E091F725D6F9C4B97CBCB326C55A5D6FB7DFB5D1CEF3F09FE0C3D1F05E740122FE617153CF12C6F6412C476D5DE9DCB4F6989795A05317866A664DBE1B91F8BD97F8C8B3EB7910E2DF337601CD3724B129F02B279EA2D0F79DE9B525F9BCD6F7737FE7577847F7D8F10B16D00E1535D267766
	83DF3D6C3FFC25B2E8ED5DC1722F7C929AE7A96E9841D5ED22B89AA37E6988361D460399886715167189C2C1B7248AA786C744A12D4F6D51F9E696A42E8BFFDEF06D3A7041826BBB08DF96445FC1BCD6E0FD87313040FC87416EF448EF48BC90D38774FE4F683853BAEE89999363CEC902665571AF913DB07E180885F27C4BC42FB0418D705FCC18F027797DCF812EB941E42F22F25EF58578FB4C30A44B347D069377117CD06A6A73315309F51482EE135E3BF55CE9FDAD6D6477040D301607GA8CE6A573F89ED
	62B1FE34B3717A281D5677E111CE6B7B304FBA477EBD47B7BA935D17D17DC49757F90A2E73C3AE2EF30A0E9BA0F9DE9E340BDF6738B967F83FAA9DF722836384E8DF51F1EF13FCF7A23413F55C82A767CD55F179DD3CDFF1A947CDA3796E8EE8B76B3874F60CF3C17B2C0E6B6A6063F9F55C147D98178A6D85BA6EC312AB27C3BBD747CD5D45F9D1994755DE65389E9DF7DA603829BA2E64BC9FEF390E9BB94F7B651771FC5C7DEEA9223FD1105CB2F1EB0A5F91A9B3705C2DD98D3F3D9F7B26864EC0EB30670F62
	38C06FC1127A79BD0895445E03384A336C9836B37C05F2A35723E3F7F799E37F56F52D318B4798DBB60DCD6DD04EC55B71E937A1EB61B9AEABD29EFFA662B1CACC32A6C01F89A6D9D363E48DF710EF670AAAFAD4A56497E0ED4A71EFFDBFA37EB19D5ED381E85FE6A70E91229F87BF0175BB616475F4EDC5963BF3DA6A64BA36D6446BD89823E3CF8555915FB9115A324250114AF8271B4B88D4F0DD66F4E3BF88286230282A22C0FF42427A7F1A749F87FDA76A7D5736C74F692D470DB93D5041751D1DC05FA213
	3E8B20CFBAG2F6173AAE267F466FEDCDF7B40C9FDA19F510C6A75A859DFF6873CBB38DE1F6C0A56EB2E4B506B3EDD7ABCBA2C575A2CD70F036A4557DAF098FA51718EDE656385AC46AB0C7155F1ECBC3EAE1363468B88FC3C64846319A1B5E63C790E58BD3564A343762F1D67E3AFCAE07BC6535869955136E71B460E3F236172361FB05E8F7460D7DA23971D67B5FA1E436A5E0773047FE23CCF6BC57A7A7F4109F97CE2381AFF6E2E6FD0E4C52D5D5B382F0E401F1A76309AEFEA58DC63B5EFBA1C7037A80D15
	F49E1C6CB96F4E6DFC8F1333F558AD87C55D06ED1D5C06AB1556F7D7D4EF47C9C36F56EE73B8145EFC4A20BF553EB356AE2764937D78C71C35FFEA2C2B5E5738074B3F39034B1F15C0FE62F9BDB3E03CFFF65A107F685BE348EF0A161F215F71E4DB4A67635F76D7E35D83D7B9CF4D6631666C71B3864FED8267B9BBA64F0A33864FBD3AFFCD829ECABFFC8E53E9EE3D693C39961A8FFD52B60FFDDFC07360E5A43F48053E0EADBCF74A6AFD9FA52E57FBD4A5A02A5AE50AA53FB8E3BA3C59E956219146F3EE99DB
	874A777D8EB35FG704857451793D527A6814F246B628BC55529A940B37B3A7826C555298B00A77D3A786E0BDA1BBC60A92D9A1B0F65F46763EB19F74F1BEB9976FB4EF318C74FBF6912B05A9AF2574EF29F69D372152497BA7ABDF43C7D1D71B27E3753AC03626E6A086FF7CFC7FCBF7B6E78FEA53B637BCD389A5F6F662B717D6688717D9E90627BBDE6B1D73F0A1AAB7A3FC7CF8BBF31C10E12030A02AA2A50E3C297A9C652AEB53594AE393D50066CDDE883AD4E3A109DD7D8DD280894E0DD28900A33887E61
	3A5D8C9A5AC6BE94D5631B5E6ADA1F2FC1909AAE435F1E968DED455F08ACA915786BD6B5793CD5CDBEDE812E600A06B669DF24GD181A7C148672F66DF13029751E7A4D09419618BE1D5D49EFD4E945C31756B0E6FB505EC2878885AG71BC60F5034D640706AE747AA0B1DED4212F069CAE4C5BG3C8D0477C397328D85B5345D408FE1BC2576865CC3A2F4A9915C6AA899812BD4FA86158C2B1EC1A2C0C336B3C74E00D8524264F2975A24F97117FF1C66408C9AC37760BBC2C67810AC84DABFBC7C43G79CBE8
	6E667870FF783BCF7DE67AF9E7158D59E0353DA2DD5E22B0AE9C4838F8676C9BE3DAB733BEBB3F048531443273FE4F676673BE6F26DD894F7B242D6CDB505658BB5DB99E72BBDC40E7EF65F13E4C7381CAFC6F516BF34B326616C78F7B85FC8752FED9930D1A78818F9EBF487BB0A23B4370E03DC7CCBC7F8FD0CB878843CCB4DA5793GGD4B8GGD0CB818294G94G88G88GAF088EAF43CCB4DA5793GGD4B8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586G
	GGG81G81GBAGGG9194GGGG
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
			constraintsAutoEmailCheckBox.gridx = 0; constraintsAutoEmailCheckBox.gridy = 4;
			constraintsAutoEmailCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsAutoEmailCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getAutoEmailCheckBox(), constraintsAutoEmailCheckBox);

			java.awt.GridBagConstraints constraintsNotificationGroupComboBox = new java.awt.GridBagConstraints();
			constraintsNotificationGroupComboBox.gridx = 1; constraintsNotificationGroupComboBox.gridy = 4;
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
			constraintsHeadingsCheckBox.gridx = 0; constraintsHeadingsCheckBox.gridy = 3;
			constraintsHeadingsCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsHeadingsCheckBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getCSVAdvOptions().add(getHeadingsCheckBox(), constraintsHeadingsCheckBox);
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
				
	//			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
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
	public DateComboBox getStartDateComboBox() {
	if (ivjStartDateComboBox == null) {
		try {
			ivjStartDateComboBox = new DateComboBox();
			ivjStartDateComboBox.setName("StartDateComboBox");
			// user code begin {1}
				ivjStartDateComboBox.setSelectedDate(ServletUtil.getYesterday());
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
	public DateComboBox getStopDateComboBox() {
	if (ivjStopDateComboBox == null) {
		try {
			ivjStopDateComboBox = new DateComboBox();
			ivjStopDateComboBox.setName("StopDateComboBox");
			// user code begin {1}
				ivjStopDateComboBox.setSelectedDate(ServletUtil.getToday());
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
