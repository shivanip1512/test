package com.cannontech.common.gui.panel;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (3/12/2001 9:57:47 AM)
 * @author: 
 */
public class ManualChangeJPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("MMMMMMMM dd, yyyy");
	//modes the panel is in
	public static final int MODE_START_STOP = 0;
	public static final int MODE_STOP = 1;
	public static final int MODE_DATE_ONLY = 2;


	//choices the user may choose
	public static final int CANCEL_CHOICE = 0;
	public static final int OK_CHOICE = 1;
	private int choice = CANCEL_CHOICE;
	private int mode = MODE_START_STOP;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JLabel ivjJLabelStartTime = null;
	private javax.swing.JLabel ivjJLabelStopTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStartTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStopTime = null;
	private javax.swing.JCheckBox ivjJCheckBoxNeverStop = null;
	private javax.swing.JLabel ivjJLabelLabelStartHRMN = null;
	private javax.swing.JLabel ivjJLabelLabelStopHRMN = null;
	private javax.swing.JPanel ivjJPanelOkCancel = null;
	private javax.swing.JCheckBox ivjJCheckBoxStartStopNow = null;
	private com.cannontech.common.gui.util.DateComboBox ivjDateComboStart = null;
	private com.cannontech.common.gui.util.DateComboBox ivjDateComboStop = null;

/**
 * ManualChangeJPanel constructor comment.
 */
public ManualChangeJPanel() {
	super();
	initialize();
}


/**
 * ManualChangeJPanel constructor comment.
 */
public ManualChangeJPanel( int displayMode) 
{
	super();

	mode = displayMode;
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
	if (e.getSource() == getJButtonCancel()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonOk()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxNeverStop()) 
		connEtoC5(e);
	if (e.getSource() == getJCheckBoxStartStopNow()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JCheckBoxStartStopNow.action.actionPerformed(java.awt.event.ActionEvent) --> ManualChangeJPanel.jCheckBoxStartStopNow_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxStartStopNow_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> ManualChangeJPanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCancel_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> ManualChangeJPanel.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOk_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JCheckBoxNeverStop.action.actionPerformed(java.awt.event.ActionEvent) --> ManualChangeJPanel.jCheckBoxNeverStop_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxNeverStop_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 3:40:34 PM)
 *
 * Method to override if desired 
 */
public void exit() 
{
	
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GABF28FADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D4551538C6CBDB5A4515FE2696F79129DF3A26EADDF6B735FCDF69963B31D28B9A84CA4420D1A23FD1C20DED541BC94284C8A0FFD4A081869204E08CFF517C9048FFC8B0C20488310615A1731219E47E1CF9A1C37DE14F39775D7B5E4C3CC988567DB65FF7725EBD671EF34FBD775CF36FBD77A524E057140CD4C381A12999C47929CCA564507B047C73407C07B9AE6277BDC62279598D10CBEA
	BECC07E7945236DFB4661072E319D01E8D651E7C0B46B761BD1F9CBD965684AFA15A69815A5DDE5B4BA337635B465BB152722F24ACF8EE82188338A4007AB9BD507EC2E92B025F8A65CD4ABBC22A0110A4DA6764712D9AFC18D2AF9A1E31G61F331CE12D42450EB210CF53A60E91D4B6A0E0367FA2A5DADB5D5CA3D1E5B2610245F6EB1E4E3DB9A799C62945E74A75853C47E159AA20DD478D0C6ED67E07F09DE17233C576CF43A1C3254EBAD07224365AC6F9F32153B4DCE49DED3730159B9E436B7D94D4EFE69
	D4A76264A7D0DF4771CCAA8B64C019BC0938670F230D2220BC87A061892E735C6CDAC527C903D3C85CBDDBC26A5C7CA453557A242AF374215599DBE3D07E0E3EAC05AF8268F1GCC97290F6B529343F5F9248A6BCE02F2A440AC011FD00D78D8A8A781E44D63BA9E5F47F56C7E7C81982378344DA1F4AC1947F46B18276AD8DB3CBAE39B751374C29C6B71C00B841889908F107ED40F318420107A67AF379D00E743E539091A3B416DEE34DBBC9F38AC129D5EEBEA0022604ECA3DAE0F059066031E63E5BA7AF028
	FD4A5F7F90D85FAE906EB311E76FA572356753B2F47AB6FDDE0921D311E1F9CA6D5B142750EE9F1FC03B0541FB2AA217617BA8BEDE03E7E3351C7A82EDFFFE0F51B21F5B39FB935725A37DCE12F37824412823CB78FF559A2267B39D62662B3AB09BAFAEC29BE7AD68B16E8728876886708104AF60B6DE3F7D68E8B6EE871F77B84D76F3C3175DE60F5CB8E433DBA40FE26B623E11EC2D870C6F70187918DDA20EEA47A8765FDAF56D929B7136E17A82E60F598BD43B303687C2363DE2C130EFEC92E3D8CD6784D9
	4846B0CD601BA8BEC703E7E37B981DDBD8B681AD8C0055A7B508CFD89868A359941F7E07C09F7927EA6EA3A620058130717DE2A7464284A827AF6AB19681D4GF4813881429671B16E5AFE70BA67D127E6FC99EFF661DF60A979E40F395D69154D4EDE693C59E3B3DF36CBDE225479CBA15A75329FDFA11D5F3D5046C949A9F94C324D658469E607844E64E5F5CA8AF3310EC72254AE1BC302869CEE42651695664353EE764A9F39ADE6D9D2EBB0FAC5A15A640A79BCC88481709E33886D75ED9AF327417B4CC55C
	D6BF5905382D31BD46C3315CAE8F94664173FDF0EE0C604D36DED4436C394AFD745393B0D624996AA7G04BD8D76DD4C67672AEE6E074D59BFA6E573DAF47D90A1E1B173A972CC209F5A297C7D00EF8330820CFF062F23AF9D6F06A736B916B9428175C41D75E8589F564606935D4AFC3C11B6508E99A1E868734C6EEF6EC65F0D00FE4D8208FF065BFED9210979E42359ABF1AFBC63818712AC6AC8303ACB0BF00D112EC8CE39D9F2CBCE8B3CF8959FD8D534DE754FBE17701DA40A47F855848E649578BF9565B2
	BC4850FABB279BF6A05DAC4B9E5B65A1D9BAA749324D592F72EF58013AF87DC221D7696B36BD58579468E3C19C2CE3F13C2F376F485778D9074BB5B8646E429924786BCDF4DD2E027A1DF1FCEC6A2AF1DFA50BF2BBAD2B7439D55DE398AB14B376983DECCCED240D51FFD89B6BEAF2945E8C45270C0216CFE913C07799G71GD9CBF87F432B3BB56DE6A8BC6BEF501775F57BD9D09BDC3F3FC79B4CB7EE5D995A373F37D35F37EF5F296F5BBF5CB9326F5E3DD34FF799ADE2A7764FB56C544C051F6E4478586BF2
	BAA5BAA1858BE17B63D5DD588F1B53A6DF30D97AA5791C6DEF926CAD1720CFCF21B138825E5B167018B9DC4CE3E6495F6EA6B3BFB5997476D761BF2EB3CCD9427CF57A92B5E6B21D6E5F05F3CC32CB8EB0D413EB48A9B37CF49A5F953CB70D621E5D940E3C7BCFB58E49324B59040B173D4CCC7BD24E9E1D12274F65F1C8167D8366AB66F27330DCCE47223C0112DB703D54744EBBA7CC419B7C32812D5CCF9B8284F60416F82C74FC69E12663645209DA9D4F8C7E7D75BBB3B8D65D0EB05DAA3DD319EECDD629F7
	305165BB8D35BD67E4177B6F29E4107031EADB4166E7BE7972F0043F3E67E4F0F494F95AB57CCD282C15BFD62D0F32B52323987DB9867CBC8100BC4B570CC3A62473B5C3D9981BDCF6BB1B0AC073BDCB675909824D5C597AACBBF3DB6049683B6B7468B385FD2AAEBDC6500B6B756889C24F9D3BB1C6FB254F06A4188538283DAFDD657B3B5DF9813473E67B103AEFAC5B3D911EF64DFA48703B76684F713DFBF08D9C9472CB772CA3387FE3B2D9CCFA500CB25D9E1B83368F3840AB390570BEEDEEA1857447BC04
	69B9585381B05EE984758D9EF0B8C345537CC48D4FBDF4G0F85A06CB9A6E756F3ACCE31FA9B0F71FA5DD0F6472B75A646EB6B7DD2540B01728A002C67D93D0E673575EE2E6575A683FEBAC00CD22F6085659C7A82567BCF11EBB8A8F00FD35CAB30E8752773B35487B95A5CC87DDD9E5DB80C9021481F71C26059722692149B8B7895AFF0DF38E70FF61F5360753AFAEDF4EB2F0C6B59E2ED0CE6388B459B6059E773F8611CE911FC5CB7AE96232E3877D7504A785E7776F192FC16E93158E41707EFA31B0669AC
	6BF2B54171238D5C2BC9ACE167ED52B07033FC487C935CDEE4A9DF233EEC5566E0E6ECFE4730B104ED2A16B21BF4AFD5ED9333E7F586135DDD47E507AF6579170582B7C7607EDA4FF119895C8F5714222F523C934690DF39F79856711A9AEDDE49501F295824BE0167EC1E30613C71C1F9A2C084GBBBF3A6939926B4E84DCF2821F5B3EBC6D5CBE74E20F319B608BG96CF67579C01675863A69F2B819B5A726B2C12ECCC266657ABBA4E3E7FCED2FBED13C1AF67B4FE73D643449759D8C43D280E099C63B6D8E7
	735CD845B1EE0B74E5BD465AE55C969B2B74ECA1AF63E31254DC2576E54CABAAEBBBC134BD9E644E94EDEFFBCF5BF672B2F68EB0AD6763F026766B0D43E31FB5206EDFF721A5BC9F91D94B7BD181BA5A170B35A4DF2F9F53C4BF720FFF3DFEBCFE32D95B0F2F317AAA793EA35AB8B8B32EACE49C0CDA2E6412168746415B5F42F918G781465BC8EAEA84E5544413390F9E0AD0155CD6C79CB509E300EA9656F14E03BF49DA37C6C91FBE2AC79311C95BC4FB1E79F0EC189146B813A815CG93D7F05D6A760E1A6F
	EB710D106F3BD8B7963D9EDC414FEB0B4744B7CB70FDDABF963E85024F589F4837E205FA86A6AB034F43AC87F76BG7AF641CA1603FBD3606F24788E8D1E4D0585B466E2B98CE8752BF87BD7EA825BCFD9655F6656557EE54697964437B320DC9B4047664666F3B87EBAEB860BD8045F31AC5D9C388F42B95A85B22D2B791A74C0DE26224F38555C2F5EAA45BDC89414E3GD6GE4A676984BG5AG6C095C3F3E3FD7EFFD76772FD00B331A0BE13628B9F6E3B6EC8B628B346104C4FFBE566F46AA6CB75E39CCCB
	6463386A86F5C899D5074C177446714479C27DF164D1CF513573BD54156604DF623ABED4FBE33A4E8C628B54357BA5BDFBAD4C67BA38DF62FE52F7GF191D00EFA097B44723DE8D39E8FBBDDB627CCF8AC8CFF8B7770AE0F2D5F6664383BA82E576521498816A3DBC377EA51A0B3DE48DD30AFF46E6419FD7A391367776967CE166F9BB9AF38E6DF285C8A53CF2E637AA583B05CDA014BFA19632E54F31CC960F6523DDC85145B842E410AB8BB1443DE6663BBE1G47A9824A33G6281489A1083D08FF089E09C40
	E40019G31EBB85F944A178C652DC9BD46AA00EEG9FC0F892BBF760DE874F95D1D0DE10A4E2366D41GBFC040B670D93FC6758954073F23FEA68DAD7E6560D84BE1661A50B42274855FAF0DD0CF8BEA1F338774F44FD51E5981730E3F633A3CB1E0FE04EAEB4B88347554E7587B666B509B8157109DD407C77F32197A2257ABF9E930ECB64BE60536D45EC2F4F2F2274D8E0970BC48CFEB56ABF5DAE4D75391B1DFF76570B3C3F292BFB3F4D5627AF5E8AD789E4025355CFE537382E3028FE8935632F11832B6F8
	BC143CC5E968B9D9D92ABFA70F1762F8F8DDC31EDEA9F05EB55333902329ABC53BACB9304B2C4E87149FF6037DD2503C6D227C0C960A7FE2E928394D6C7959B12DBD0F929EB7CF8665472703DD22955B446A5888A1D1871F7EC7181BFF744733367756EA5BFED7347D51FBB8BEE59A1EFAF86FD24A56BF863741643D68A76F301037B89F65FD21611B886F5314F2140EBCE63B4183389F7209320B162F923E1EC98D1B047DDB1C96A578335C44869A9F579A88793601633ECBF192A87BE5AA47E55118F98B887C85
	61386FD15C4DD06F9D5137F77BF15D301B2F3A06208D53ABD82F23DF3B5F198D7DC88248FC057507697833E11C73FD9E3383067D1CDB6AA5A914375B4AF92DD07E82E0B2408C0058D7781966CA837A7B298E73E549CE8FA4F8C9296873407182C1D3AB341D7D60B4237F20F913065765262C8AEF51597CC0DE179BD9957ABD1FD3FA974C26D61BE43784367D49013CG3A3F7CC80952034E041CDEE7213A510C4319C1F6F867341B7FE70B16D7FB8E93E371098F79A1F0412CF5DCF651BA1C761B6CECFF9AB62B64
	B3978DE84F104D49ECFFEACD66E74C95329E7D8BC1CFB66A51A78A3A27C50FBECD50FBAEE869D18ABDC550D7166B51F3DE6574B63FBCC2492B0CEE91747BF275683ED7F95CAD49890C3B93DED5675E14D70367A1BB1FC453B8BE7DD5F6BE499078A7A9BEDA03E767163FD672B3FCAC506A7F44779D0F513D7FB4A8C7895CE63A4F1B8D65D801F367737DC90240592AB8EF561FB96E21AA4EEB9238276937E8E5D0EE96382939086B0632ACF07FD1455B98AFF0CFD1DCA414E7891C1D6E0597C0B9D1601E243894A8
	6724F0DCD1A562CA20DCA1F0B7D572B66A85EEAD6D5BC4A8CF93383A8244C5C1F936400D2738B8A8A78A5C4A82AEAFA505670E8EDF4233135853E3D62226662447E6E9B477F750E84556E78F5D8762B8F53C465767A97D74FBA2A8CF86187E9A4FD37A5E62F94A48FB6FA49D63DAF53F950B6BFA47B07BB566B3092F29390B4EE21E270C3572B64BDE87BFFD1D46E51B56C7F14E7E90AA14E36C2574DABFBA2E1D25C4CBF77A51D15FE10316BE450F1E2161E7FDFBECAF6FDB4AE391447E6F95A13F030BFC1D75E9
	0E72F41FADA6E12F2C4ED07228FDA17228B420327E678D7233EE669B7C7CDBBC44F17582F7B01363FC827713960EFB708DBE56F5E557714D6010EC0303109A1CB1B037B8E54FD5A677768BBCA71CA05AAAB948F1A6F2D1E96B66D3072F37AD91EC7184497703572A384CEE1049F04F66F25CC440B11FBEC6770D51D00E953807F3391F13540BCA6CFA1C4E25CEA8DB85EEA145619D885E17B05C254A31FCD700722E271ED529475A38BA26B62E775B058CCD7DFCB20E5A4F61144056365EC929781DED920D1F7F0D
	F3964A2C5E23D574DB8E986D8500A36467B8EC244800753F153FF50476184CBBE8BB4F607C819EA6A39637750A9EC947F07F62B07BEC0EA1873D04E7FCD93548A7834FB88357656739B8175C9E29CF72F8A48B3B32D76ABFC147B4926AC681B07DFED7B512FECC664AAA5A3E4D9950FEC6B54A0B87B909227D1B5F433A324BE56F3239F18290561F018A35BF4B5E8B36EBAA75232751EEE9DC5651CAFF3BBE045BE35211067543AB1BC751FB3D72C4A8839E04E24262CCBD3C3F81BF29C227AC9A573D925E3FCA96
	26D3F1810F1D1304CE678B02F5F7D05CD20088D16FDF8A7CF5FF76BDD57774AD23694E3FC75153BD8A5EF7AB6FECAE8C8765C2C78336F7A830625AB38B6425AA7298FE87454F5160591E625EFE7E5DF1AC506A53AFAAB921C7AA510726C1F9A6408200C4001C8C509BC079DE6B40A1B2CA5E304B656E00ED2ADD131EE17DDBE23D31EFC9FFE5646B4C7407271228DB13D2C35D7555E73058F2C9F9E6FED8CC712CFF5D55583F09C00B84188310G1082D0300E772F766DB256BF3C2AEC10E5F32F155E02724E6145
	240A46C3886B5B1B035A3E6DDF475A2FD51E1D6B02E3A3635BBF246533A875FC4AF3C2C83E0ACCAD5F94255EF465999D126F7E96ADDF2CD2AFD1F9269BC37105DF98E96C360664ABBDB892DFD9C83E302A11786AC372AD4D9D092FCB070F4D7333B941E7D2DB0EB8D7825FB800880099G1807E3B1E0EE6EE8B1E033229B3E47GEF021145GAC2740FB8BD1E3C5BEF92184FDDEFCE5A470B30FE72BF8FE4CD3E89B7371392FF71FB2DF268CEB6F4B2AB69C89F9DF1619496CD124BCE3EFAED6E47C7E932D0C1CBB
	0B8C29A1E4F4A93C1714A76EDB190C680B5A7D21696DB7D275760749978F98A6A93CD19A9909D2E2866B5F7DD534FF2EFD4A9AD406E2153E5F110BB9F027B4EC9297094D30CBF36207467BAB0F16190E3C8B1BD646F766384AF7DF557573FD6B044A77C95E7570313ADBAB295E4EFDE497E1F946CC227EB059B36B701D59EBC99637D751FDBFA24D7313D279DA91E873950A1DEA572B76CA6F63762AA940716EB35B3DEA2EB12C006A6265B6FCG2F1A15B9335D2A3E97EB5EA37B4732B79BAB30B69E79C65BE0B1
	7D714A50394EF9157A394EC5157A778FCBAAC77EF673454A505FEEA6D6EA3F5DE4EB5235C6BCE349B00E93812683C483442E676B51D7D5FB88DD0FF4562296CBBF66DA71EBFF1937EF2E8E5DD7EB7568FFA370D9B5FE67F35985E1460172B9FE389A47CBF639A912613E28C69BF9EC7DD619D5D5707FC07713F6290F2259B9241C1E5F1221DF199B78596469C24495C139CC609A0B78F925CA606A06B86FF8017B300973CE9638071AB8EF04403DCF4F0349D04E5C48F1779BA9AF144B84AEBFD334ABF00F34F05E
	718237FAA3671DACF01DD922DD01FB608247A58BDC7DC10EEB4B62389F095CC938403DAE4EEC8900E3FB365BF2781E2DAA7B22511D4D4F16C70F785DA7331CC52B5DB55C21ACE57C8EB0076F01A3001765AC0C39A8B38E4A49GB91BAE9A0363D4E21D9E0E4510BCDC7A94BF7EE5B35277030C66CD5CF72BEA5E0667F973C07057DF006C74385C12C73E4A30740BC3C5564338A4AA3E74D54D5ED0B2E0861C02AE1F39BCGFB7DAEF33F50E5EEAB2E5B9650E39C40B40099GF3G9204EE3F2E59BF02EE4CF8F4EE
	F94CB6274C643E5B069DCE8199DBB7DFB45682F481D8814681CC8208G1881908330820064405E92E0BFC095C08740259C2EC3C6FD6988BAA809A7D589E113EFB5061E635FEF9CFD0E5F51C8F377B0B73B2E3A153B15490D74FE372F4FAB09D8F2F7E30E4035FBBBDDDE9B5E49307524E5904FC5FDAE274C4ECE1F5079ADC35F46657033D3D2217F59A977940EAB5BB7FDDCB45A3E091F1B744ECE8F66286F2CFDAB0D91B3G9F1D434F5E956F2B6DBD5AB4D2FB46D15ACB8619E87357602C497AF6B9533F7FBE9A
	BF52F361BD17777F9123FF7FAFF4287A9C489C2D7F9BC750279E5A586B577FD5B4D6F5G3EBB17777F298F54767A33C66B7FC86D0D4FD57B4F647D6642C872B2826CB9A9D715077888A87FD4114D64DDB8B81ABC2DFE538364C58749FB28EAB4F95AB10F891017GE5071F3CADF923494B5648CB8A101719274AE37B1EBB026E8F2EEF3F74639B64DBBCE6BE16C3781C2E9185F9013905B00AB76585669632F379BAD5863409F9BC564D22F7E8E25F44423D92E79EEB46BD907EFD0A4DEC377D0DFE0AFC8EAABA7B
	997D8F2D21635D125651635D32D67DBDCDE2EB701EE6ED2B7E1E664F2D5ABD8D43353705562B33EDF43DBEEE53576B5336E03DACED7AFA8D34856B65EAC31F6B077523DB5413856EE3013BCAE5A26E024DA2DB857E9A558B71ED92B6487137D03D06A92E1BFF932941FD2C607E318D579268325565613C135AE83C34596D4A77C4340E4D89CB0B3047DDD4C71B5B2C51FB2A40215EAC2E3D55C588BF574CAD447D315FF92C9502727EBD9670A5D77F1EB25321B253618AF116EB7F0888B93F3D204AE9E93A9EB9C7
	041C155D2A1CF9432A1C29463169136CD365AC7EDC15735C463169137AA572065E2B15FCB5B25DFEEDE4FAC16A4874CA8316D82C39A9D7FD7F0E66BDBF771BBFCF16FD23ED307F47F16E749D603C11ABC906CC5631DC0CCC8CE786E53227556569153CA659AA193C3DD649B2E417CC324B6499F21A7ADC9E3C3C174986FA09EF426BF4192C27B76E26532EE12F07648567949A3E3FF0FE64274D8586E2E8A76B2CF41E35130CE11C30BE3C0E472DE93B77B37295DDE874CA9E32D948ED2793AE6E4A511A34676EC9
	B310F4904A82D0BBC947A88232BD2C0DB4500F3600BF9B9447CC2332B1EC24AAD81CC36B8496A7B5E6D90E81244BE4A36B9D5E1B554844383F2D2D4661A88593E067835120E8361F33BFF2773BAF4CDEFC5E1BC6563513ED3857BAA7F96892A1D970D3AB656F49DB49FAB7FE2E02B79A508BAFFDE4F8652BF6290394741D453FEB724A122501AFBEDE19EC215F1D9F44CB3703EDE62FD54640FC2C0324BB71AF5FC99EA35B4D4E7E03ECA5BAC6CD115AF0091A83FD01003E79D4DF14D2AE24647FE2735E5D276223
	52082115EC303B4C16D6F3AF6C6B5B49FA596A113CD6175D62F35755D3C11943B81568568644950A6EE3E74B715AC5A8BD683BEBC2F6C2F6AB4CB31F079C24B5F1EBD9AEEAD81A2CE68FB4A5F95AE57CCBFE1727C450EE5AA93F753B13EF7698C82A52C99C31FC9FFAA0ACD1CEAFFE0E8F9A1882B460A4A1287C76C78F7D60A7FF7AAB9524324AE497B399309E6C17DC0E038DFDFDB627A484007DC07C8EA19E6B14875449735D3665543CB7AE43F8B713B44721C37477E17A7B887EBECCB107A9667091B0E2A299
	71A7106E23FE7A264C1CC4CC8C9C66F2B8B31B65BF2CFCF46A225A4495503BD612C13F7487E3672835CE8FB9AECB1EDBA3FF30E36324386F62D83851A5385CF5FDE6074DFE95DE1AE91876A1FE1FCFCC0FB44DD4B2D0ECAE75AA668C743FD6D06C16E077C074B6CAAC243FF70E397348E5223CFB30C1FC596B2FE9392AA9068F03B219B3B41AEA7D7F7FC453CB3A1A3E2BEAEA5058B4757F1FCDEB75ED1A6627BF6E0DB0209AF431A488DB6DA7A1DD0BD5F249255734797B64DB72C35E81B4EFE7B9646E6DEAAE19
	65BB5A4F2225A6817E416DFC9FFF5307789DDF28FC4CB90759EE174D7641862785F3B30DD7E5C94D0D1EBE0B723D3C0EC2E4340F4E225E839A1E7F85D0CB87884CDCAFE15A99GGD8CBGGD0CB818294G94G88G88GABF28FAD4CDCAFE15A99GGD8CBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG949AGGGG
**end of data**/
}

/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 3:43:40 PM)
 * @return int
 */
public int getChoice() {
	return choice;
}


/**
 * Return the DateComboStart property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.DateComboBox getDateComboStart() {
	if (ivjDateComboStart == null) {
		try {
			ivjDateComboStart = new com.cannontech.common.gui.util.DateComboBox();
			ivjDateComboStart.setName("DateComboStart");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDateComboStart;
}


/**
 * Return the DateComboStop property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.DateComboBox getDateComboStop() {
	if (ivjDateComboStop == null) {
		try {
			ivjDateComboStop = new com.cannontech.common.gui.util.DateComboBox();
			ivjDateComboStop.setName("DateComboStop");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDateComboStop;
}


/**
 * Insert the method's description here.
 * Creation date: (7/10/2001 10:48:08 AM)
 * @return java.text.SimpleDateFormat
 */
public java.text.SimpleDateFormat getDateFormatter() {
	return dateFormatter;
}


/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('c');
			ivjJButtonCancel.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}


/**
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('o');
			ivjJButtonOk.setText("Ok");
			ivjJButtonOk.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setPreferredSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setMinimumSize(new java.awt.Dimension(73, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}


/**
 * Return the JCheckBoxNeverStop property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxNeverStop() {
	if (ivjJCheckBoxNeverStop == null) {
		try {
			ivjJCheckBoxNeverStop = new javax.swing.JCheckBox();
			ivjJCheckBoxNeverStop.setName("JCheckBoxNeverStop");
			ivjJCheckBoxNeverStop.setToolTipText("Forces the schedule to run forever");
			ivjJCheckBoxNeverStop.setMnemonic('n');
			ivjJCheckBoxNeverStop.setText("Never Stop");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxNeverStop;
}


/**
 * Return the JCheckBoxStartStopNow property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxStartStopNow() {
	if (ivjJCheckBoxStartStopNow == null) {
		try {
			ivjJCheckBoxStartStopNow = new javax.swing.JCheckBox();
			ivjJCheckBoxStartStopNow.setName("JCheckBoxStartStopNow");
			ivjJCheckBoxStartStopNow.setSelected(false);
			ivjJCheckBoxStartStopNow.setMnemonic('s');
			ivjJCheckBoxStartStopNow.setText("Start Now");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxStartStopNow;
}


/**
 * Return the JLabelLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLabelStartHRMN() {
	if (ivjJLabelLabelStartHRMN == null) {
		try {
			ivjJLabelLabelStartHRMN = new javax.swing.JLabel();
			ivjJLabelLabelStartHRMN.setName("JLabelLabelStartHRMN");
			ivjJLabelLabelStartHRMN.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelLabelStartHRMN.setText("(HH:mm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLabelStartHRMN;
}


/**
 * Return the JLabelLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLabelStopHRMN() {
	if (ivjJLabelLabelStopHRMN == null) {
		try {
			ivjJLabelLabelStopHRMN = new javax.swing.JLabel();
			ivjJLabelLabelStopHRMN.setName("JLabelLabelStopHRMN");
			ivjJLabelLabelStopHRMN.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelLabelStopHRMN.setText("(HH:mm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLabelStopHRMN;
}


/**
 * Return the JLabelTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartTime() {
	if (ivjJLabelStartTime == null) {
		try {
			ivjJLabelStartTime = new javax.swing.JLabel();
			ivjJLabelStartTime.setName("JLabelStartTime");
			ivjJLabelStartTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStartTime.setText("Start Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartTime;
}


/**
 * Return the JLabelStopTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStopTime() {
	if (ivjJLabelStopTime == null) {
		try {
			ivjJLabelStopTime = new javax.swing.JLabel();
			ivjJLabelStopTime.setName("JLabelStopTime");
			ivjJLabelStopTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStopTime.setText("Stop Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopTime;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOkCancel() {
	if (ivjJPanelOkCancel == null) {
		try {
			ivjJPanelOkCancel = new javax.swing.JPanel();
			ivjJPanelOkCancel.setName("JPanelOkCancel");
			ivjJPanelOkCancel.setLayout(new java.awt.FlowLayout());
			getJPanelOkCancel().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelOkCancel().add(getJButtonCancel(), getJButtonCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOkCancel;
}


/**
 * Return the JTextFieldStartTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStartTime() {
	if (ivjJTextFieldStartTime == null) {
		try {
			ivjJTextFieldStartTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStartTime.setName("JTextFieldStartTime");
			// user code begin {1}

			ivjJTextFieldStartTime.setTimeText( new Date() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStartTime;
}


/**
 * Return the JTextFieldStopTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStopTime() {
	if (ivjJTextFieldStopTime == null) {
		try {
			ivjJTextFieldStopTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStopTime.setName("JTextFieldStopTime");
			// user code begin {1}

			if( getMode() == MODE_STOP )
				ivjJTextFieldStopTime.setTimeText( new Date() );
			else
			{
				java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
				cal.setTime( new Date() );

				StringBuffer hour = new StringBuffer( String.valueOf(cal.get( java.util.GregorianCalendar.HOUR_OF_DAY)+4) );
				if( hour.length() < 2 )
					hour.insert(0, "0" );
					
				StringBuffer minute = new StringBuffer( String.valueOf(cal.get(java.util.GregorianCalendar.MINUTE)) );
				if( minute.length() < 2 )
					minute.insert(0, "0" );
					
				if( cal.get( java.util.GregorianCalendar.HOUR_OF_DAY) > 20 )
					hour = new StringBuffer("23");
					
				ivjJTextFieldStopTime.setText( hour + ":" + minute );
			}
		
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStopTime;
}


/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 2:12:22 PM)
 * @return int
 */
public int getMode() {
	return mode;
}


/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 2:56:28 PM)
 * @return java.util.Date
 */
public Date getStartTime()
{
	if( getJTextFieldStartTime().getText() == null
		 || getJTextFieldStartTime().getText().length() <= 0 )
	{
		return new Date(com.cannontech.message.macs.message.Schedule.INVALID_DATE);
	}
	else
	{
		java.util.GregorianCalendar c = new java.util.GregorianCalendar();
		c.setTime( getDateComboStart().getSelectedDate() );
		
		String start = getJTextFieldStartTime().getTimeText();
		
		try
		{
			c.set(java.util.GregorianCalendar.HOUR_OF_DAY, Integer.parseInt( start.substring(0,2) ) );
			c.set(java.util.GregorianCalendar.MINUTE, Integer.parseInt( start.substring(3,5) ) );
			c.set(java.util.GregorianCalendar.SECOND, 0 );
			return c.getTime();
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.info("*** Received a bad value in getStartTime() of " + this.getClass().getName() + " : " + e.getMessage() );
			return new Date(com.cannontech.message.macs.message.Schedule.INVALID_DATE);
		}
		
	}

}


/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 2:56:28 PM)
 * @return java.util.Date
 */
public Date getStopTime()
{
	if( !getJTextFieldStopTime().isEnabled() )
	{
		return null;
	}
	else if(	 getJTextFieldStopTime().getText() == null
				 || getJTextFieldStopTime().getText().length() <= 0 )
	{
		return new Date(com.cannontech.message.macs.message.Schedule.INVALID_DATE);
	}
	else
	{
		java.util.GregorianCalendar c = new java.util.GregorianCalendar();
		c.setTime( getDateComboStop().getSelectedDate() );


		String stop = getJTextFieldStopTime().getTimeText();

		try
		{
			c.set(java.util.GregorianCalendar.HOUR_OF_DAY, Integer.parseInt( stop.substring(0,2) ) );
			c.set(java.util.GregorianCalendar.MINUTE, Integer.parseInt( stop.substring(3,5) ) );
			c.set(java.util.GregorianCalendar.SECOND, 0 );
			return c.getTime();
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.info("*** Received a bad value in getStopTime() of " + this.getClass().getName() + " : " + e.getMessage() );
			return new Date(com.cannontech.message.macs.message.Schedule.INVALID_DATE);
		}
		
	}
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonCancel().addActionListener(this);
	getJButtonOk().addActionListener(this);
	getJCheckBoxNeverStop().addActionListener(this);
	getJCheckBoxStartStopNow().addActionListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ManualChangeJPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(315, 204);

		java.awt.GridBagConstraints constraintsJLabelStartTime = new java.awt.GridBagConstraints();
		constraintsJLabelStartTime.gridx = 1; constraintsJLabelStartTime.gridy = 2;
		constraintsJLabelStartTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelStartTime.ipadx = 17;
		constraintsJLabelStartTime.insets = new java.awt.Insets(2, 10, 3, 3);
		add(getJLabelStartTime(), constraintsJLabelStartTime);

		java.awt.GridBagConstraints constraintsJLabelLabelStartHRMN = new java.awt.GridBagConstraints();
		constraintsJLabelLabelStartHRMN.gridx = 3; constraintsJLabelLabelStartHRMN.gridy = 2;
		constraintsJLabelLabelStartHRMN.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelLabelStartHRMN.ipadx = 7;
		constraintsJLabelLabelStartHRMN.ipady = -2;
		constraintsJLabelLabelStartHRMN.insets = new java.awt.Insets(5, 2, 5, 61);
		add(getJLabelLabelStartHRMN(), constraintsJLabelLabelStartHRMN);

		java.awt.GridBagConstraints constraintsJLabelStopTime = new java.awt.GridBagConstraints();
		constraintsJLabelStopTime.gridx = 1; constraintsJLabelStopTime.gridy = 5;
		constraintsJLabelStopTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelStopTime.ipadx = 18;
		constraintsJLabelStopTime.insets = new java.awt.Insets(1, 10, 2, 3);
		add(getJLabelStopTime(), constraintsJLabelStopTime);

		java.awt.GridBagConstraints constraintsJLabelLabelStopHRMN = new java.awt.GridBagConstraints();
		constraintsJLabelLabelStopHRMN.gridx = 3; constraintsJLabelLabelStopHRMN.gridy = 5;
		constraintsJLabelLabelStopHRMN.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelLabelStopHRMN.ipadx = 7;
		constraintsJLabelLabelStopHRMN.ipady = -2;
		constraintsJLabelLabelStopHRMN.insets = new java.awt.Insets(3, 2, 5, 61);
		add(getJLabelLabelStopHRMN(), constraintsJLabelLabelStopHRMN);

		java.awt.GridBagConstraints constraintsJTextFieldStartTime = new java.awt.GridBagConstraints();
		constraintsJTextFieldStartTime.gridx = 2; constraintsJTextFieldStartTime.gridy = 2;
		constraintsJTextFieldStartTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStartTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldStartTime.weightx = 1.0;
		constraintsJTextFieldStartTime.ipadx = 86;
		constraintsJTextFieldStartTime.insets = new java.awt.Insets(2, 4, 2, 1);
		add(getJTextFieldStartTime(), constraintsJTextFieldStartTime);

		java.awt.GridBagConstraints constraintsJTextFieldStopTime = new java.awt.GridBagConstraints();
		constraintsJTextFieldStopTime.gridx = 2; constraintsJTextFieldStopTime.gridy = 5;
		constraintsJTextFieldStopTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStopTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldStopTime.weightx = 1.0;
		constraintsJTextFieldStopTime.ipadx = 86;
		constraintsJTextFieldStopTime.insets = new java.awt.Insets(0, 4, 2, 1);
		add(getJTextFieldStopTime(), constraintsJTextFieldStopTime);

		java.awt.GridBagConstraints constraintsJCheckBoxNeverStop = new java.awt.GridBagConstraints();
		constraintsJCheckBoxNeverStop.gridx = 1; constraintsJCheckBoxNeverStop.gridy = 4;
		constraintsJCheckBoxNeverStop.gridwidth = 2;
		constraintsJCheckBoxNeverStop.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxNeverStop.ipadx = 16;
		constraintsJCheckBoxNeverStop.insets = new java.awt.Insets(6, 10, 1, 87);
		add(getJCheckBoxNeverStop(), constraintsJCheckBoxNeverStop);

		java.awt.GridBagConstraints constraintsJPanelOkCancel = new java.awt.GridBagConstraints();
		constraintsJPanelOkCancel.gridx = 1; constraintsJPanelOkCancel.gridy = 7;
		constraintsJPanelOkCancel.gridwidth = 3;
		constraintsJPanelOkCancel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelOkCancel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelOkCancel.weightx = 1.0;
		constraintsJPanelOkCancel.weighty = 1.0;
		constraintsJPanelOkCancel.ipadx = 34;
		constraintsJPanelOkCancel.ipady = 1;
		constraintsJPanelOkCancel.insets = new java.awt.Insets(4, 61, 6, 59);
		add(getJPanelOkCancel(), constraintsJPanelOkCancel);

		java.awt.GridBagConstraints constraintsJCheckBoxStartStopNow = new java.awt.GridBagConstraints();
		constraintsJCheckBoxStartStopNow.gridx = 1; constraintsJCheckBoxStartStopNow.gridy = 1;
		constraintsJCheckBoxStartStopNow.gridwidth = 2;
		constraintsJCheckBoxStartStopNow.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxStartStopNow.ipadx = 16;
		constraintsJCheckBoxStartStopNow.insets = new java.awt.Insets(6, 10, 1, 87);
		add(getJCheckBoxStartStopNow(), constraintsJCheckBoxStartStopNow);

		java.awt.GridBagConstraints constraintsDateComboStart = new java.awt.GridBagConstraints();
		constraintsDateComboStart.gridx = 2; constraintsDateComboStart.gridy = 3;
		constraintsDateComboStart.gridwidth = 2;
		constraintsDateComboStart.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDateComboStart.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDateComboStart.weightx = 1.0;
		constraintsDateComboStart.ipadx = 54;
		constraintsDateComboStart.insets = new java.awt.Insets(3, 4, 2, 57);
		add(getDateComboStart(), constraintsDateComboStart);

		java.awt.GridBagConstraints constraintsDateComboStop = new java.awt.GridBagConstraints();
		constraintsDateComboStop.gridx = 2; constraintsDateComboStop.gridy = 6;
		constraintsDateComboStop.gridwidth = 2;
		constraintsDateComboStop.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDateComboStop.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDateComboStop.weightx = 1.0;
		constraintsDateComboStop.ipadx = 54;
		constraintsDateComboStop.insets = new java.awt.Insets(3, 4, 4, 57);
		add(getDateComboStop(), constraintsDateComboStop);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	if( mode == MODE_STOP )
	{
		getJLabelStartTime().setVisible(false);
		getJTextFieldStartTime().setVisible(false);
		getJLabelLabelStartHRMN().setVisible(false);
		getDateComboStart().setVisible(false);
		
		getJCheckBoxNeverStop().setVisible(false);
		getJCheckBoxStartStopNow().setText("Stop Now");
	}
	else if( mode == MODE_DATE_ONLY )
	{
		getJCheckBoxNeverStop().setVisible(false);
		getJCheckBoxStartStopNow().setVisible(false);
	}

	getJCheckBoxStartStopNow().doClick();
	getJButtonOk().requestFocus();
	
	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 10:18:44 AM)
 * @return boolean
 */
private boolean isInputValid() 
{
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 12:46:05 PM)
 * @return boolean
 */
public boolean isStopStartNowSelected() 
{
	return getJCheckBoxStartStopNow().isSelected();
}


/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	choice = CANCEL_CHOICE;
	exit();
	
	return;
}

public void setInitialDates( Date starting_, Date stopping_ ) {
	
	getDateComboStart().setSelectedDate( starting_ );
	getDateComboStop().setSelectedDate( stopping_ );
	
	getJTextFieldStartTime().setTimeText( starting_ );
	getJTextFieldStopTime().setTimeText( stopping_ );
	
}

public void setStartLabel( String start_ ) {
	
	getJLabelStartTime().setText(start_);
}

public void setStopLabel( String stop_ ) {
	
	getJLabelStopTime().setText(stop_);
}

/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getMode() == MODE_START_STOP 
		 && getStartTime() != null
		 && getStopTime() != null )
	{
		if( getStartTime().getTime() > com.cannontech.message.macs.message.Schedule.INVALID_DATE
			 && getStopTime().getTime() > com.cannontech.message.macs.message.Schedule.INVALID_DATE )
		{
			if( getStartTime().getTime() >= getStopTime().getTime() )
			{
				javax.swing.JOptionPane.showConfirmDialog( this, "Start time can not be greater than the stop time, try again.", "Incorrect Entry", javax.swing.JOptionPane.CLOSED_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE );
				return;
			}
		}

	}

	
		
	choice = OK_CHOICE;
	exit();
	
	return;
}


/**
 * Comment
 */
public void jCheckBoxNeverStop_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJLabelStopTime().setEnabled( !getJCheckBoxNeverStop().isSelected() );
	getJTextFieldStopTime().setEnabled( !getJCheckBoxNeverStop().isSelected() );
	getJLabelLabelStopHRMN().setEnabled( !getJCheckBoxNeverStop().isSelected() );
	getDateComboStop().setEnabled( !getJCheckBoxNeverStop().isSelected() );

	if( getJCheckBoxNeverStop().isSelected() )
		getJButtonOk().setEnabled( true );
	
	return;
}


/**
 * Comment
 */
public void jCheckBoxStartStopNow_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getMode() == MODE_STOP )
	{
		getJLabelStopTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getJTextFieldStopTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getJLabelLabelStopHRMN().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getDateComboStop().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
	}
	else if( getMode() == MODE_START_STOP )
	{
		getJLabelStartTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getJTextFieldStartTime().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getJLabelLabelStartHRMN().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
		getDateComboStart().setEnabled( !getJCheckBoxStartStopNow().isSelected() );
	}


	if( getJCheckBoxStartStopNow().isSelected() )
		getJButtonOk().setEnabled( true );

	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ManualChangeJPanel aManualChangeJPanel;
		aManualChangeJPanel = new ManualChangeJPanel();
		frame.setContentPane(aManualChangeJPanel);
		frame.setSize(aManualChangeJPanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
}