package com.cannontech.analysis.gui;

/**
 * Insert the type's description here.
 * Creation date: (10/30/2003 10:57:32 AM)
 * @author: 
 */
 
public class StartEndPanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjDatePanel = null;
	private com.cannontech.common.gui.util.DateComboBox ivjEndDateComboBox = null;
	private javax.swing.JLabel ivjEndDateLabel = null;
	private com.cannontech.common.gui.util.DateComboBox ivjStartDateComboBox = null;
	private javax.swing.JLabel ivjStartDateLabel = null;
	private javax.swing.JLabel ivjEndTimeLabel = null;
	private javax.swing.JLabel ivjStartTimeLabel = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjEndTimeTextField = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjStartTimeTextField = null;
/**
 * StartEndPanel constructor comment.
 */
public StartEndPanel() {
	super();
	initialize();
}
/**
 * StartEndPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public StartEndPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * StartEndPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public StartEndPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * StartEndPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public StartEndPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GDCEF47B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BBEDD4D4C7161CD9C37C416E1A04A8C1E2D051E844C894A3637801DFB9A4B20E13B19BE7C3F218891BF5E74C2CB9E322E7465DFDB45020B45888B1A88D3293460F91C97B958987C591C7D09299A50A0909B47483DA1A6E367BA10D30133DD575EE3DC777EBC0E742B957776AFE547D28DBD5376A35C2413E480CD4DD01A024E688725F29D47867B3C1F83C7223EF90F7F6DC30CED07D7DAFC01E70EAEA
	BABC1B005615902CB38A5F5C4D0236955A1BD6856BBE0277FC216DEE50C9F889202785E859B32E558D2F67G5713C95BEF379A60198B50853890007563087E5F37964A7898E8674A6F84D221D77BE30447E5AAD461CD0FB13E9679F962F142335BE212693100CF8448823C69F1254F6D543AC72AAA65762D47A205F52F3C2A4FA13AD47DA38C4A321172D3903E5213C02489619DC760D95D545DF9265D59D351EEF1B81C8EC9EC6F2AF0B91D5D7D951268112A2A2EC99637F441E1ED32B8C4BB709B1FA0F60EB7E5
	51BE84E1BD744BEC7AE9CE352CB334BBD2081F3AAA20CD4DCFB0DB3CCFA8B6896BE76FB898CA7A2E3198E439B820EF82E076A6FF73C9GFBAD8E0B3D5FE373D4F4765AFCADE676BEEAC1FB6D20E3A355B3330868090277C5G6BC2510F5387500F1601B9427A367775BB8378E1FED29E43A795BF963DBCFB479E1AAB73F4E43CC30196871088308940B8BED8E786A822B96AC87D93BC4F35C9671DBDBDCE47B9172B56EEF5FF6E340AF6F82F2A820A0C3BAA36BB5DD6C1E0F9B851D42EE18FC27D7821B928644D8D
	8173A67A6AF421D97AB54D85DF5FC2DFDB273FA577619A2F78467C3A12CE7C9A8778E80015GC9G5BGCC93502F385463A37955GE36706013B565B6602812B6D35592D22DB766F02E5B87F340049DDFBE0B99617994755F1D9B46F83FD26C6DC964DF8C3DFB9014523E902929726BBA420EE57847F7120F11EC0723058C472F08C3C274AB94A7087A8FE3D8A4F66479C1A37243D81E80DE11837AD3968C3E3FA3830493CC553876F720D7A18B0E6CFC2186F589E561375E7B3600DCF856B8E821C85E885988498
	77940EEF1A6E13D166ED13EAEC196CA25DEEF80ADE49EDE9F0F8A40B23DD3CEEF15BACEDF651A348BC2B68FA5BB6C46E95BA1F5AC147C551A13AAD124D69005EADBDA2A41207717CCC17C7F85CA225B65BFAE08D337438846C778DDDBEBC6D960F74254BEA11C40503515F26313CE7398EBDC287701E7C940979DC3A5E4540FBA2G0BD5CBA6411505876B2A43B1AE5F266D0267E5C8EC51DED5D5E7EBA7E6D85C7D283FA51D2CA562BD51A15509AE51E105970FCC3B1D3EDD09CB07135BACD2BC19A36741F08F47
	F72767B1BC74210EB252BD14EE11A4372D2DD7922F0912E4F3F4AA728354964F1069679164B91EC5F22C897C6A8270F27F52B372D57EB542BE516BEAA6A3A747E9AADDE7C300BFAA9C774C0FCDE48FCD8847BC7DCD8B66E9DD4EB441705A7B1AF9CAA0A51C656736F05F39F60476296818DF547200735E97924235672832362C791BF5248DC3E3F1DF3E1D443DECA24CBB00560998771A0CD2B65FEAAD9E91E75895B70C18E8D5320C711E54874E31EA3DF60E556A35F32CDEBFFC8EB56935F20851EE503D455967
	D06564AD7DAEBA079D8E119A4DC58436477F261944426630C9B7EC56CED13AE63BAFC26DB21164564BF48EFA61BDE4A266D25F4718CBE577A78B113757EA6EE75EE97F29CF194846F76BC4A51718CD5FE412180BF6318782F55E596B10983E1D4EEF99AF2FAFDFE6E973FE1DC57231DB6C1779EEE76985320E58FBC56EDB0CA51B4458ED6B0121A293D62E8B97DA54F5E1E1C4300E54105570EC0A405AE7F6BD2925FCEBB116B33F5FC1EC6A825EC10050884C19B732889D672A1CAC671DF6BB8BBD9F13046DC6B2
	06625DDE9116E2B219AFF3BFFE32FD178F6D3A6C93233F3E1D78E4D72D8351914427E4DE9F12FD026136D0DC868CDCDE866E3B1F9B55F5EC1DE70366789158944146AD7DE95FBD4BEC206BB060CFBC0D3EDF5D21DE274EF9BC4EF69B5D926431786DA192F3DACB5E315C33F8ABBCFD1057D5D517E8E8D94DD856C66D8528FF9AEB46831DA456C1508E8308617AD2337DF3C51F3D831E9DB63787EA9D2B6845189932096FE48F1251D81B9DD347A2B2B9A9E0AC926498A4794562DE8E31E3AB608D135036C73373D4
	3100BA92365CCE98A9346724A10D652E5C7E4C100139AB605AF7284D3736A91D242C17A7A6792F1D2C0619C863D5BF49373619C671AD13FCEB1BA31DD85BF481ADF1926E3576DC62177999D8971FC13F5698F2D4FEB5B9ED8E097BB410C3661F53ED6B34B99097C4F1D0C55385096DD7E76998B7C11FD6G06AB36925CD8E8C7BE037B4B64CEB23FD77276AC5ACE626D631DFF6B5E10AB779FF87FE97C3B6888348731985D4DA56361656DDE5A6E1765E0CDEF53535C6DF776C2306D11A4DE0EB6DC3F84E897821C
	85E80EE4B5G1BEFAF7411F522830AB518E657DCE23BD0BF19481E3022ECAC70A7GECG48825AC1G26F377G199FF5D02D613A47564D24CE5FF513689B87F2D1C41E665378B6BCCBAE825C6649E84F51D6D2677279DFE16903236EC5374DDAEB69EC245E31B560AD1A9FC43EFC8A2E8106F65227955A03G21D3F0AF726E47F3E37474F021BC68834D73EFFC731B7A68A9EC1C57CED1666F8CB8B7B21DDFD9D127E1EA302EEEAA410D3596780C61EC008A9297CADFB804CE667B39A1740DC368445E4CB4B57D7D
	8EB51D4468E3D91E79367A0F685B3655B3044679A9814F76A1D319CFB172B3AEADD73F799FFFA07BD65015A567730629984F8527C9BCCB2302F515GCDD1B827A27C561C110049BD7710F2EFBF309CDB4B5E22795095455632D40E7FF70A17D4F816133B5AB1A70300169205EB5C81A349677AE93096818C82048244822C1D06EB5E6FF24D4288672EE6272B910ABF3B6AC8407C3BEDFD38337132CC9C7B5805D3043060A8FDF60031CF19A66F515365B332AED7CF1E4C3F05F9443F93C0EB8210GC2GA2816226
	23FF35BB4B19FF972CB6691CA4D95A3B680E0A4EDD3709FD8A1AACA44C37BDDD68DB222CBBF93A3216EE1E9EE8FF3AF8060CC55633EC0C72B83E0562CF2870EC6C9653DCA56DD620EDFC96F3B525DD60F9742E6A3D307D6F31E68F1F37C73FD79DECDC4C4640E70AD3C66DB345E9237619623CF178B345E5E360B345D5237ACC4172CA48A7EB17F7864C9700B800C400649918D34946BF301C5248278BF048B86F243790926ADF11975857D772C63E3B78D99E297B5A1C12646CA17DA37E67F9E43CA4270BA2996E
	2DBC92A3A8D33AA446AA63D37248BAE997BBA81A55A5E63AF6EE833F8AE7E22D72669D02AB0776D90E3BE5477A45BA93737373B325E47EC2BB91E083C056F3413AB200EA0096G69B9647D167206C2BB96E09D40E60002D9D0DF83B483C83310F74569A00D1CA17DE965D2350CAF8BC0A7A08520913BE3D5367B4F03F7B4ED5999C0CF0E7C4C9EA677738260D98D4174EE571CAFD97901F3E8E77E48B9D418CF4F93B0764D7DAE11610A73E9CD5B516191F92E7EA15F48F18D1EA62747C68EB92CEE3A58C5FCF439
	458E518DA7BFF6149692E7119CE92539948A6F1133304E5AFC473F364B6E469A4B984DEA3B03702C86A035EFC4B46171586BE41E66E87F31A3FDE30DCF6BFA60998478D0EE0F0BE67A917205DF59489A8B47DCF2B957E8E9936D8C5FDBCB7A23E7E0D28CD28A4AD43A7354B4589D5B1C35CEAF6FF366E12352E7B3447E4FB651EEE5B4E72F2C0F774A70AB6AF6AAF85EA323851D6236F0ABD157DD6FAEB50DEB938453ECDA131ED157DAA9F2AC330046680B8A3468D6CEBFBAC45EBB1B5163B87DC0111A1EA8530D
	4FA3BDF52FD67FE51C7E6E00D67F151C5EB020A55F4869FA13167ED620CB4F13EFEC3B768AFCDCDE3DD38ACF0758D70AE5F8C59D444CC1AEDF8F9EBB75E9F969311366C32C8E096CD31F0F8BF77CB26079B86EF916070972336CE42E1E6D5F473B3176CA1F83F98C406A5201EE2C3925B97C7B90650D0676CA00A44E9BE547FB014D005BC77D009AC2403FCDB5DAB8667BBBE45A4AF555AF8CD8D719E6CD944ADE0F52FF9C401F72986647E00CF2DE589034A9D33E4B6888F017C13312597C63B3E8F3EC8C1ED77E
	6F4F6A6FF591BB5F8CD857ED14759ADED074EF6567155F96E0CD5F78821E2F7E6152D1C17BDBDDBBADDC49173A0ADE49C6AE49074ED756E797351758B90677D9D3DEB95ABEAF71C9C7E6608507646EE77DFEDBC47AA5FEAEFA814733F2AF5A3F19634A86D1FF79DC44858DE08C2D9CF75A047DC54C45387E64EBF51EEEFAAAD10F753FEFDC574D15EB55393E77B85D45245F02F941BA73BC2CE3EE6CBD8C4F6B16BBF255CFCAE4271BD6B200ECF2BBDD22DB6AE7D8FACBA76F0B1FF5BF5CF96862C34A85BD301C7C
	9D4CCE3FB94C73BDC76D2778DB73FC4FD1F378DC6E82DA62BC2CC9E65A557A4D31F0060E8DDCEF90E0B1FF36046CE3AD40AB81048144G2C84C882588410BE9F76C0GB3C0BD40ADG97409800D00099G0B6663F89DB69D9AE63C644B86DAFC3AAD6C6E0A597F6A992D782D1D4FFCC8196F6F8B337F53FE326EEE85FA410B413AF200BAG6B0BE8CF2FE944EF32973C43FC135DDA20E5579868BF8CA07AC5FF3B18DC8F77A7DE03E79D60B6GECD35118DCFB914A9997A834B2793D921E0DG2D8BFC65CE6F5532
	D3823E31G918B8259F9E89065E2B4F8D682EEBD40C6BFFD0F8CE869936298DFC1DCA0FD357C9B7EC195CF357C5E84CFAB00D7C5E3637C65FD52BD9B534526BF864AB1984ECB309DDCE93BA336CB4D16CEAE63AAA6FD717397DBAAE4DADFB149A1727D45E6315B6E53AB73EB406868143F65116BB7597EF2557B7C6E6F7F1C3B66FB55414ECE71F4EF8F09E3EB8D432D2238A899476274231240757D7212116B7B55A55AE7441F16781F917F25C47B0C18D422BEA3B25CAFCA487E5489EBCB8B67FB0763EEF25C2F
	E91F84F743E6153AB87EBDEA9741570BC4A16237D03B7AA82EC546EDD361EE4A3854FD648C81AEF7B95DA82B5FC76E4ABBECF6BBEB6F20BCB6879CD9F8BCF66EA3B65ADC96155D859CF7130F45FF503AA09E46E2ED9C1EF73F284173EED29C2EF5E77A830F4F05FE6D7B0A4B7D5A7795FF699F7EBB7B4D7EC077994C3E1D9F52B37A8F215E7CA15A7C2F3B892E9E5ADD9C375A04FE8CF25C986AEFA234B7F05C0B86645B46F1170F90DC63C2D8B396A2EE8B6573C2FB9C475DAF427E22B86EBC2DEB62201D40F1B3
	86D047BA0EBBD404BA4AFE04387C7B282312631A77223F0D9CB71F62DA20ADF15CCB4528E3AC471D9DC47B56F3DCDB9172ED643804813425FA91623EAEC2DB1AB8AEE2G73454AF17F45FDF3F15CCD936A0D67386FF8DC56F1DCB217CD66B847BE34453C98F10F8E20ADF50B31FE356CB788FCED25530E61CD5E9C850F53CEBE9F89B5E46EC8102C6C7C30FCG1A82BBF70BB5F86EEED96C3F47609D85029DF802963379C1EB2B45CABD45F46D7FF0282EFF9B94B86D3D5DE4DF00BA5B65F440B4386236413FF4F7
	E07789599F91FFA3214F6845F8AF70CB935A172CE11FDAB7014D401335C4314F3444573E09E7825FE74452F9F290E4CEACC17DBFB6207E56A5A35FCBC840B396G4FE091FE7A5B0E10BA3F476235757476D06DCC77FDBA77E6GFF6C923CCB494D213CB028C3F977536F3349403791006126523CADDC8A73EAA94AFFB5B0F4AC327E2A0C7B5281742BE929F6ED219EF7AB70F817AAFE05ACD57CE27A2FD27DE1000FDA0AB6D553F93D965AC9CB715CFE78BBA59F927792DDD6E7AFECEA353D9D9D642785585F2CE2
	7C761F950FE371E6B15ADC982F1D8BEA1B4B0167EC3C128B4D713EB65B68BA518AF8BB5751DF04BA42B4F42479C0B4704447ABF1D9672763BDBA0713813FB19E63B2060D55B2982BE59897FA5DAF0FCD41A09F1BE55AF3D26D2B95F83C4B94BFC316795A70833AAE0581BE0A6B8BCED574ED613930D6C3DF260F3E9460593ACC7159305CD75F843A1695823EECB97A9CC36350846DDB4B715CBB3E864F42E317EB5D018802960E597F0ACEF9F727ABB9350456327C9E683C46E2D3716597173F38F2638B816F8972
	0B510E78873663498C65BDCE2F58B14BC0E4825CC99DBDE6AEAFBDF67C102C7F3F8D28FF2B267E616E025E48D47457EED774EF9CCE7FB1332C1F4909BB94399DC54359BDD4EEEC36A2D7B9D092D86ED3A2E6BE04715E446FC04CAB9E545FD986455FB5B90A7E2C7BA35AEDC63B5F4BD564D2768CE37789341B71AEAF266B293BD7E4FE6416209FDD1AFE8C17B785C6657DD41EE24F0DE163BFB4EF1EABC27D118F9C472FF6A97A1FAFD074B78D275FA7EF12BED4644A87C613B74C6E8B03E8777A870E5B7F6CD65E
	AB8A957D5283645FC12E5F3872C1637617BD0AFEC9F587BDB8327FBC7FA694A9F2A1C52349BF79777AB56ABB02FA307D564A616F24646F4745EAB997480C9915DCDA0EDAAE8CE422C7A5777E103B0CF810D9B7AA397C92355C861059B6AA39B9C5EAB963AA58F7D60DC66E5220DA2E92E49AC7A5F7E408DCAB48C8A348493F7F2E713F97D8D0233E97E038CF0A7D79CE947B736D4D71675B17634FE798C43EFF66383CC164BB44F1FFAA716F6F13927F7EDEAA7267DBDD644FE79974676B9874672B53302FE108FD
	6C7FE2FD705D9D18BD516F8A999E37304BFF3E1C7B271F3F9EFD3B2EC0A7689A84FD97BD84B58899FD641464A5BF5F7F9DD4A985A03B134BB6508B0464FB472B525E1E1A269352C110DD28B4886964D684645D2C1FB44969223D8C7913046D979C56D2720520CA923269873BD272B582DA99E6F31559ACF348B819C366E79C1E3B42DF85D24F242B816CAC9E360EF969D74FDE189C511E2893D29B04EC27C220D6E6866815E085B9245ED170A48CE9D9F90B6C9955A3489778C99C7C9E2D9A5E0E0730953F319D7A
	CE7DADE67DA3AB82FEB30CCEE06BC0FC026F371817F574F79700AFFC996F177668487F1D8BF46FFA2D47E237CB96FB77B907155C415676CB22F21F3406766FC19E1948E82F51EFC8F7D4B27F8FD0CB878874A1B5A72B93GGA8B9GGD0CB818294G94G88G88GDCEF47B074A1B5A72B93GGA8B9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6593GGGG
**end of data**/
}

/**
 * Return the DatePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDatePanel() {
	if (ivjDatePanel == null) {
		try {
			ivjDatePanel = new javax.swing.JPanel();
			ivjDatePanel.setName("DatePanel");
			ivjDatePanel.setPreferredSize(new java.awt.Dimension(276, 250));
			ivjDatePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsEndTimeLabel = new java.awt.GridBagConstraints();
			constraintsEndTimeLabel.gridx = 2; constraintsEndTimeLabel.gridy = 1;
			constraintsEndTimeLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsEndTimeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getEndTimeLabel(), constraintsEndTimeLabel);

			java.awt.GridBagConstraints constraintsStartDateLabel = new java.awt.GridBagConstraints();
			constraintsStartDateLabel.gridx = 0; constraintsStartDateLabel.gridy = 0;
			constraintsStartDateLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsStartDateLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getStartDateLabel(), constraintsStartDateLabel);

			java.awt.GridBagConstraints constraintsStartDateComboBox = new java.awt.GridBagConstraints();
			constraintsStartDateComboBox.gridx = 1; constraintsStartDateComboBox.gridy = 0;
			constraintsStartDateComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStartDateComboBox.weightx = 1.0;
			constraintsStartDateComboBox.weighty = 1.0;
			constraintsStartDateComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getStartDateComboBox(), constraintsStartDateComboBox);

			java.awt.GridBagConstraints constraintsEndTimeTextField = new java.awt.GridBagConstraints();
			constraintsEndTimeTextField.gridx = 3; constraintsEndTimeTextField.gridy = 1;
			constraintsEndTimeTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsEndTimeTextField.weightx = 1.0;
			constraintsEndTimeTextField.weighty = 1.0;
			constraintsEndTimeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getEndTimeTextField(), constraintsEndTimeTextField);

			java.awt.GridBagConstraints constraintsEndDateLabel = new java.awt.GridBagConstraints();
			constraintsEndDateLabel.gridx = 0; constraintsEndDateLabel.gridy = 1;
			constraintsEndDateLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsEndDateLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getEndDateLabel(), constraintsEndDateLabel);

			java.awt.GridBagConstraints constraintsEndDateComboBox = new java.awt.GridBagConstraints();
			constraintsEndDateComboBox.gridx = 1; constraintsEndDateComboBox.gridy = 1;
			constraintsEndDateComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsEndDateComboBox.weightx = 1.0;
			constraintsEndDateComboBox.weighty = 1.0;
			constraintsEndDateComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getEndDateComboBox(), constraintsEndDateComboBox);

			java.awt.GridBagConstraints constraintsStartTimeLabel = new java.awt.GridBagConstraints();
			constraintsStartTimeLabel.gridx = 2; constraintsStartTimeLabel.gridy = 0;
			constraintsStartTimeLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsStartTimeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getStartTimeLabel(), constraintsStartTimeLabel);

			java.awt.GridBagConstraints constraintsStartTimeTextField = new java.awt.GridBagConstraints();
			constraintsStartTimeTextField.gridx = 3; constraintsStartTimeTextField.gridy = 0;
			constraintsStartTimeTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStartTimeTextField.weightx = 1.0;
			constraintsStartTimeTextField.weighty = 1.0;
			constraintsStartTimeTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getStartTimeTextField(), constraintsStartTimeTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDatePanel;
}

/**
 * Return the EndDateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.cannontech.common.gui.util.DateComboBox getEndDateComboBox() {
	if (ivjEndDateComboBox == null) {
		try {
			ivjEndDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjEndDateComboBox.setName("EndDateComboBox");
			ivjEndDateComboBox.setPreferredSize(new java.awt.Dimension(120, 23));
			ivjEndDateComboBox.setMaximumSize(new java.awt.Dimension(200, 23));
			ivjEndDateComboBox.setMinimumSize(new java.awt.Dimension(100, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndDateComboBox;
}

/**
 * Return the EndDateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JLabel getEndDateLabel() {
	if (ivjEndDateLabel == null) {
		try {
			ivjEndDateLabel = new javax.swing.JLabel();
			ivjEndDateLabel.setName("EndDateLabel");
			ivjEndDateLabel.setText("End Date:");
			ivjEndDateLabel.setDoubleBuffered(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndDateLabel;
}
/**
 * Return the EndTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JLabel getEndTimeLabel() {
	if (ivjEndTimeLabel == null) {
		try {
			ivjEndTimeLabel = new javax.swing.JLabel();
			ivjEndTimeLabel.setName("EndTimeLabel");
			ivjEndTimeLabel.setText("End Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndTimeLabel;
}
/**
 * Return the EndTimeTextField property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.cannontech.common.gui.util.JTextFieldTimeEntry getEndTimeTextField() {
	if (ivjEndTimeTextField == null) {
		try {
			ivjEndTimeTextField = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjEndTimeTextField.setName("EndTimeTextField");
			ivjEndTimeTextField.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjEndTimeTextField.setText("00:00");
			ivjEndTimeTextField.setMaximumSize(new java.awt.Dimension(100, 20));
			ivjEndTimeTextField.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndTimeTextField;
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
			ivjStartDateComboBox.setPreferredSize(new java.awt.Dimension(120, 23));
			ivjStartDateComboBox.setMaximumSize(new java.awt.Dimension(200, 23));
			ivjStartDateComboBox.setMinimumSize(new java.awt.Dimension(100, 23));
			// user code begin {1}
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
 * Return the StartDateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JLabel getStartDateLabel() {
	if (ivjStartDateLabel == null) {
		try {
			ivjStartDateLabel = new javax.swing.JLabel();
			ivjStartDateLabel.setName("StartDateLabel");
			ivjStartDateLabel.setText("Start Date:");
			ivjStartDateLabel.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
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
 * Return the StartTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JLabel getStartTimeLabel() {
	if (ivjStartTimeLabel == null) {
		try {
			ivjStartTimeLabel = new javax.swing.JLabel();
			ivjStartTimeLabel.setName("StartTimeLabel");
			ivjStartTimeLabel.setText("Start Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartTimeLabel;
}
/**
 * Return the StartTimeTextField property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.cannontech.common.gui.util.JTextFieldTimeEntry getStartTimeTextField() {
	if (ivjStartTimeTextField == null) {
		try {
			ivjStartTimeTextField = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjStartTimeTextField.setName("StartTimeTextField");
			ivjStartTimeTextField.setPreferredSize(new java.awt.Dimension(60, 20));
			ivjStartTimeTextField.setText("00:00");
			ivjStartTimeTextField.setMaximumSize(new java.awt.Dimension(100, 20));
			ivjStartTimeTextField.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartTimeTextField;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
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
		setName("StartEndPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(387, 86);

		java.awt.GridBagConstraints constraintsDatePanel = new java.awt.GridBagConstraints();
		constraintsDatePanel.gridx = 1; constraintsDatePanel.gridy = 0;
		constraintsDatePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsDatePanel.weightx = 1.0;
		constraintsDatePanel.weighty = 1.0;
		constraintsDatePanel.ipadx = 1;
		constraintsDatePanel.ipady = 1;
		add(getDatePanel(), constraintsDatePanel);
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
		StartEndPanel aStartEndPanel;
		aStartEndPanel = new StartEndPanel();
		frame.setContentPane(aStartEndPanel);
		frame.setSize(aStartEndPanel.getSize());
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
