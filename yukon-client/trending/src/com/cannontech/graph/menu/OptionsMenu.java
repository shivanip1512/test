package com.cannontech.graph.menu;

import com.cannontech.database.db.graph.GraphRenderers;


/**
 * This type was created in VisualAge.
 */


public class OptionsMenu extends javax.swing.JMenu {
	private javax.swing.ButtonGroup graphOptionsButtonGroup = null;
	private javax.swing.JCheckBoxMenuItem ivjDwellMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjMultiplierMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjPlotMinMaxValuesMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjPlotYesterdayMenuItem = null;
	private javax.swing.JSeparator ivjJSeparator1 = null;
	private javax.swing.JMenu ivjLegendMenu = null;
	private javax.swing.JCheckBoxMenuItem ivjShowLoadFactorMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjShowMinMaxMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjLoadDurationMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjNoneResMenuItem = null;
	private javax.swing.JMenu ivjResolutionMenu = null;
	private javax.swing.JRadioButtonMenuItem ivjMinuteResMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjSecondResMenuItem = null;
	private javax.swing.JSeparator ivjJSeparator2 = null;
	private javax.swing.JMenuItem ivjAdvancedOptionsMenuItem = null;
	private javax.swing.JMenu ivjReportsMenu = null;
	private javax.swing.JMenuItem ivjStatCarrierCommReportMenuItem = null;
	private javax.swing.JMenuItem ivjStatCommChannelReportMenuItem = null;
	private javax.swing.JMenuItem ivjStatDeviceCommReportMenuItem = null;
	private javax.swing.JMenuItem ivjStatTransmitterCommReportMenuItem = null;
	private javax.swing.JMenuItem ivjResetPeaksAllPointsMenuItem = null;
	private javax.swing.JMenuItem ivjResetPeakSelectedTrendMenuItem = null;
	private javax.swing.JMenu ivjResetPeaksMenu = null;
/**
 * YukonCommanderFileMenu constructor comment.
 */
public OptionsMenu() {
	super();
	initialize();
}
/**
 * YukonCommanderFileMenu constructor comment.
 */
public OptionsMenu(int optionsMask, long resolution) {
	super();
	initialize();
	setSelectedOptions(optionsMask);
	setSelectedResolution(resolution);
}
/**
 * Return the AdvancedOptions property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
public javax.swing.JMenuItem getAdvancedOptionsMenuItem() {
	if (ivjAdvancedOptionsMenuItem == null) {
		try {
			ivjAdvancedOptionsMenuItem = new javax.swing.JMenuItem();
			ivjAdvancedOptionsMenuItem.setName("AdvancedOptionsMenuItem");
			ivjAdvancedOptionsMenuItem.setText("Advanced...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedOptionsMenuItem;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6BD0D1B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8BF8D555158E1270D49BED5488A81186089A91BF71135A5426EADB26B2231D219D5A2696E728527950269D26E6264C57C392649182C1427322DE47A0A8B102B014469083C41B043CC8AEF9C0C6F2F26F494DC94EFDF06F3949CD48E3565E7B2CFD4E3D1C1B0B7A4177AD6E597FDAEB2F3D56FE2FF3A2943E1D34FEDDF621A02CDBAFE87F7E32CE90E6158B426C7206AB08ED74DA7284433FF700368B
	DEEFAE7C6E815E5207ACF985C26A979B21DC8C6542D4CB5EEEF85EA1BC72C546A77090414EA2605D3374056A716D088134330116FFD13685FE37829D814C861464A5767FD036C74303D05E2ABD93DA17AD8835D708CCF2579E833ED21333826FB8617B08CC7E297D9ABF8130D42015C0D9D774BAB7515645151E544A9D37A589EBFEFCBCBB1F58B2540F5466E33A435A2FD5C823014891D45B9178ADEB6CF3165BBD2E923B68F6FB5C2AE46FA9F17AC4EFCF09CBF287CACB7FC61E9B3C2A6CF17B6BGD9CD5B7963
	2E0D348E08A31403FE021D3113589C0167AA20CEA056561F641721CFFDC9420AEF9D0C5856B4BFEB638A3F5E564C40545CF81554FF625496CD4F8A65B6A066C77E70616B7043600240FC98BD05BEACD35107DF0944461600A52F8828AC00BE1CBA08BE34DDFDD8486AEA4D5E964187DBC00BF7C077E1713777656C25636715FEB2869683AF83A89328309F7A9F4886E42163B6A570817C1E6ED24FFADCAE0F7B3457FBC6F1786ABC8EC9016752D260E8D813E477789C02404666F3DD45A66DC1D27AC34725BE16DA
	859CCB29CD7384D52D4BDEEF625B221F1E480E516AC86857FD4B68A7F17BA16D7BC570CC74C8EC987EB30ADB87F41C75E59545C9398AF84F8CE01C5B36E2DBEAF3E78885C75B33B71834A571297A6C0AA0EBC3DBD0EF8B0B712F03A4463100A781AD86FA9EA89328E090E37C074031E8B12E06B164F30BCAF3204BAB7A54B381D9F1C8BEAD56E527460B3599B13D1AAF2D476212F24C58C7990F361946A5A365D4F671A00BC765209E97E6FBE2C45B0E41EB4746D65E07673BC8DF8587D99F66F07C92452798F056
	377FCA6790A9A782EFAD906B1711A13236F58ED97206019200D20116822D82DABB04FDF32A7F236B9C7F0D06FEE13AFF6C5F853FD2D0750955EE3FAA3A6DD20B681345AEC5728B1A4C1F7BC9BC3AC274F287481831030D3FC9EE49A7123582EA97DD928C82BF13499F58CEE4FC92655AE4178486DCDE816B5DB930034CB3512FDE70BAC4D552A5987F4D8152177DE28B54889510B1FE15446BA53A166E076713D7B1D68FDCA5D8BC14D32EE2DC92875E005FF3B0A8A5253434D23613E6083EC19CDBCB6DA43EE9A0
	3F8CE8B55046E1CBDEB1D0A5D067B04E31D75BF0ACD5664F950AFF5AEABA1668DEB64C46C57CF078D84A203696831E81A40C40FA89D4895483F473886E1775F6929F529E3E46DF3DF62CD58E6BD8713079D8C4FF224D9762A7336D2236579BF8B9A6727906673CF1666E266B182BE4BDE37D37E7044C01D408439220D5A358077FB4E0E5E37B0C6817F0B4B778E0A0CA8E3DC7196C2AA029CF6A175CEA2564155C8EF870EBE369F741CD7AB86F7670B1789F94A76B55D99808FE0E7FA9381D61D007F156A0BFE710
	70C5D57549DD81D5EA16D4D5F6BBF57D3CC15296FF48D26857FC5DB7CAFC95C6ADF9FB008E0D222F8903BB8C63355663698BF8EDE4A6EA63FE865DFF4BC05EB60AFD565AC54EDBC1DE96E9D9677FD6EC3B0EFE889943235147CC689A1D5D75E5ED180D2BC844E2B3F9A8F25F5EBAE45E3789C366FDBBE3E87C3E1BB3E456F70CF77F90199F1E8137E1ACAC98A26B0C5D63F6CBF4C0F295011D37DE3591BFE4372C364A8E2724B64BC3922C852324CF7F1B2EE571701CBC0A6B4D403B385E948D5DA724DF7E5874FC
	11B877FC76DA2D2F8A467475063569612BE40CC90A8407B9589782EE15610F51F5D2437DB994BB7AB6194746739E43FFD53A99FEEBA5A7041B429A7EDFA7082DA6496FD182C44748FBE8ECA77C56B4098E59F3A6202A0CDB2DCA2E238257276BC613647578D4233D3F96E875CAEA23A476790D752E2FA6ED29B1D44666D25D98BDC3037F15C09DE3B8170E8E6FB54CA5ED41B86BD19456C5825EB1F6B80CF78CF50C5DD1D261B79D084954F71B49AC075F55DCA62E47CCA6B78666B8D0E78C11C96FADB4489C09E1
	B2DBF9DBD6771A551182F2E9B1E867204BCCE69970D7F1BB6FF9A259E131F9EB24000CEF69CAC0026D1EACB06724C1ED4FBEB072C6982FC5D4827AD960439152F70AE1EDE238F554FC3C3DB7CA5623BEDE7F87232F8BE4CFE7F5327931D0A4F5FAFD328B16F23258EA71F8641451D7E1825CB9400FA278BD8994E96CB6CEA03A05F63C2B103391438ED36C93786FC7B9F8CE2CABB05E8B9D7E9A5339C6A8E1820BE14A04707373CD8229F789604BA76078CB9EB52E65277DFE0FDD2647A0ADDE4DE346F9483036B1
	B2463BE51F9F4E528EA908B17FE20C34151C13B4D80BDB7BE6F26F6177B8394BD59268F71508DDEA0964105552D2BADDDAE4FFC0D4E8D835FB5CB947C62D0E5593709EB7D3A27DD7F4935C6100ECC0CCF6D2B74AAAB7216CBFBA09ECB214530016F1596F7670FA39ECFEAF2DF7A2548BE41B08F7505F6E7A9F615AB36BE750EC0F0FDFA86CE42232FE9A795CF8A7CE5B76E9F6DE04FED2A6327E39F9225ECFEB60BE495AD85407EDDCBA915B28AA240D3931163C7DC047E3511F4BAE142D0DC5D93F1B4846C2F996
	5022D874E7536E5D58973DE23F98AC718F401E22F5019677E3655CB62F4F75A9359D8736010E47619E507A31F1DC262FA80FB8AEEB63181F1DF1616332B01B589B86BCBE8E47654F47369B46A55C0761486F04D94C77D9BAE7E03EEA6569343DF43E8A385FB7BA4C6EC5491A7D45F1571E9F587DE7DBB771F5E99C3B7F2C63789E0AAFB7606CDEF41F04790B15402B1A04E71A695DE676D3A7B13B6913A2597F3C075437ECD2387D860A2F1894EEFF39936D2F86DE63E4347FD40F197DC513195DA513A3591FC067
	C24664F07B53A83EEAF2387D9D3DE8FF8D705A2660DD67FC8ED95F53203C9CA893E863D4B8C7CE45BEFEE3DD543B7AE741F16E6A6F771979D7B5157955B1B512FF2B68BC72CE8D776F77949F1E9A6E5F10027ECD815E6A2998DF074B4CFE63B4E657B1AD127D17695C8CCE8B371F49666C34F07B03EE349F8F3CB553507E4972F07BED53ADF94A744867E2E667438AD2DF4CF4E6E7AF474FD0BC4D00B37BFEBA77C9B98BF84F5C0C7D6B4D25E742DBACF9C937E01FAA6B728D733651A33BD5BEE777E613BD556313
	1D321B6F719433FBFC74204A6E8FCE3A6EAF04BA5301983614E2CB213C729674FFE7B77CE8E361AD4373FC694B1E733F8CB19B69B7548673F9CEB746E1632D98079CA7462170D60CC3ED0F6E7B8543738B4E9B9F0757EE288D66734F69FEFD927C2D62F1A817B18EB69E07493D3A6F7106673D3DB7BE8E9F5FD09BEC2E3467C43EDBF666185FAD3BF34C6F16CA4E78F983B5A7525D13453F021ECF02907BA93CCF6676E11FA470BEF9D5517B61B543F30CF2637B646E9BEA0379FC151E3D96033FCBF89C12DD9807
	E5BC8E2F38F55F7FE8F89EF35F78B8A45ED09B4C6786FAE64C82FF37FC836310C8B1AB140FFC8363F02C023E1B02F287D090E896D0BA508A20AC2002F8CBDEB1D0E5BC4BEF764423EE95550D01F292D0BA508AA061B6CB1E9528824881B485A885E889506A5BD0F77053F4C31B4B8CB6C9BBA279C65A900957E3422BB449E3220F6439F81C7A108A8CB2D9E1EDE33E240678C24E41640EC1765D7563543BF39C5EDE93ECF704710479529D57B1B6369B1E37057158BA9348A5E70DA0AF8F527220007914D1873917
	2B3030955CCE5E639E236FF259DD661181D2687609AED81A1A3D12DDC8391D3E276039925B6DACFEBD5A6F94422775345054A6135F67C479CCCDAE771B6C37681BF8FF4F6C89ED472D793A7E859975ABB53DB66DB74875DB7BC2757FEE2B2E1F56077A091ADE2A763B146B7F22B5D47FBF37697A5F613916551A1E10205D3192D03F1B4EC517DB020336ECEFCCA0FAA3947BB5D08517FB38BC544EE95205DADF7CC3954939548594D576AA326443A4974F1749A4A81583122268AC0D7759AEB27E9A950F5AAE7961
	CC6F90872F11F934E20FA6D3A73B6B44A04B68209813D933095CA9EABD2223B2405E6604721F26E32226D9022DCD04BB7A512F11A3ECEA882D6BE38D7FE59F199B279D7D64AD14431053215EC44A9D321CD7EC05B167151040F2644FA4E0CE6CFC3599FFB9676FBEE746DF4D796F551A7105BB101FE94AAF647CA76A0C7CC3F7B0BEDD67E92E6B253A70DC9761B7021C638E4C7DDDECB2139986FEBC37B35892498E634F8D6127E87C67398DCF2B190D15404F62B2A3974DE48A6E3464955D09790B67AE99739785
	B3CA32716E953E16155D495AD7F5271E3FE07912CD52C6CDE798F80B6EA458DFA4E37B49A4FA9F288408716590BED94F3E370E71D93B1EBD00F924354F26884AE31FC77C36E2AD3667AEED0D9B368819CF7EC64BA3D5B831EDEDF7E15B5C4E715B16648CEF9B72D91DD7E52CB31E57F93FBCFE1D3F14436B5C19E32C73DF7A304EA53C4E82E57CBA4FA861F57EA9244E97DDD8271008F5EEF307577977A1F51EF6C7721D4D6B04F272FB50F3D06B2724816378B93939B2E27E6BE422B60F345FB58FD934FD6C69ED
	6F8A26B93933BD123D6F0CA74857966657F7AAB0E79C1B08B940A9551833CB63D87DB94456F06C7FEAD13778EE448E56A166389B730437297B4D5B44D7C3267BD6BD6A4E3A9BF3050F54537C95299FA8F7865A393D89E5774FC059834DC45686E585A8F6865ADF325EE2EEBFAC9699AD5CBE37B33F15FF7F436D1436516FFFE6425C87AA1B09320FDDC4D95BCC142D3CC4E4EF06F2B2D05ACC4CF7FC1FECFF5AB9A5EE7B8178ED91FBCDB342B55064062EDE492E5AC4270067362D52D77BC6647B9BF0ADD874449C
	E1712457A36614174D54461676EB3DEA61673B6D423EC2329E964D0233B21083E898A89EA8E59666EF1E5DCE5AC8B2EF6050E9D5956DBDB4730F1E354852008E13C397736DB367D77BEEA6C67EEAFAAF1B667E226B3DE41A339B0FD86CCE6DA463A48D62341C476A0DB78EC79A83GB67ABCDE49278EB2141EE5B0F7D4E156062CD9116F86CC6F16EAB33DAD77E8FDFD4F357ACC2F791C195EF1CD3EAA22DED72D19DE0709FC28DEF11D191EB72A5E3B75E6FA316368313C650186B2DF936FE1F94B759CBFC271D4
	834E72190F537509145301D7F4AF5E916FEAB233BF655EE8766F26EB582CFB191D8D9C1FCB71C5861C59BFCCF1D2DE8A3CA333517E0BADE67693E6C70BDBEA2B19DE4AB8FA2CFD8B681A18B6BBBCEE5F267873334363D65106F1DB893C2AA4EC779397CD5FF3A4C5333F182E3369C94C4EA60E7F006299861C592F21B8A9E7812F63BEDC17AF6DA2FBF6BA14B30004B9F01E85BAB48767E9458E6D9D1E49FA761943A91D7550AF4AD4DC5B5FAD0C1CE77B20B07AF7E8478A49DD234B232A9E97299F713F9612F829
	9EAF8599D6DEC87C71494E9E1509EAF8E5A1D9EF94291B428CBB3313440E676458122249D76FA4B1A05FEE4822A28F511BD1B3883A1DAC97734F744CD089B1314D61F92AEA44BAB9363E065A05F28C47FA69B9A2814A499CFB6E9C4AAD6458A0BDDBBC8365E59CBBCB4F96AB201C4931D2FAE6D88B65AD5F42B1F43801E0FB20FC12E3A7A8568165E10EFD377E6B6632C87DE6789EEE63E7DF5BC624BC8CB9E73098AC20E721D9E0B3155B1DCD570545D0DE863406638FD23CB05912F79C48160C785166C86DDC96
	66636288BEA32DB161EB5F35C634916EE334EF25B618E0AC96D9748C2702DF3149B8C61EEEB566D5D9DB96355177CDA01346E370BD0A2D04F26EDC44BEEF43B693592F530FC6CA536AE2B6AEF39BDF669B316B9DB7AC86BF3908F1A9623EBDF909FC93F3844A9DC031C08BB96FD94ADB81E5E11EA54F8AD4B18FF90D17A275A3294BD8DEE8A8F74C9DBFFE31F30DB111A35A880F11711B4B71F21646F14356B97B2E48EBF36F2E68EB33FF977D1E8556D45B20D7DBD7033B68FB496EEE3F44770011DD859C2B76B7
	FA7CB2D9D1599D70550A509C59F6F22C9658F3BB5912343B627E9A5276FE59AF136F0FD84E6DD88D5EC9BB67E14EED4239507AEE5BC76AE072D967D0DE65727135E139C00BEE73E2DDA86F63B7F55E797AD05E0537F4DE7996F239E96E718C305CDBF8EE6F3551DD9A1F645EBE976DF02655E5D84E2627A1BC37C4F272D366E14EA7613C99FF96776B87CD216D8B3E2D376F48F6526F759E3754A405675ADABEA57778E6496EF1BB0CDC541D733F048FFE4126994AE76DFA2159582E057358FB04153C5D2F584C78
	D91C7F6885B37E1679685736305C6DCC2B6ED77EFB24937837044D927970CFF258FCDA1E525887C7C50BD156FFDAD1688BF65DA3561E5F3705674E483B906BFCEC6F636DE67C6373B1B77E64BBFA7BE2C649GEAD6C57A1146599E516D169416563CA64E67DA76EB3215D23FEC1708C62828B6FF7AF91D224FA7CB3EEB85F91DB35B5F51E4EDBE516DF7492A9AC11E797667CB613E11F7C0D55C7769174D789D1CFF3E530C6F657C5D2299BF1677F5CD8DD94F3AC545AFF19F36559AB116F7185D00F9074479180B
	F876BCE24F4F47BC488B9F3EA71866C1CC3EDC453AF75AB06F33B1856BAE3A00F5D7F06C5FB896645821F64496F26CD50E2D6258098E442C77A37654E5445AB8B6259331F80E550B08AD399F77274D674D6E904283516EBE4F1B6A95C6555BE7A3FA0742644A226A3DFF41741B1F28FA3FB355F3C4557B305DCCAF98D56F5FCD75EE0E2A77C90719DED2D43D9FDDB67D56AC2A5EAD1DE6FACB226AB50BE6FA99636831B355BB67716E1179G1EB5DF6658DA0E9D25F3E86303163C7D8FA2B6E443B37B910ED5DCC0
	2C0CE32F37A0D645314AF6BC4F5AB83679A2E21D9CEB6A40B31F423197AEA39664D8F2A7BFB3F32CD7C4AC7EC1DC4BDFA4D7DF6DECD2D5C41E591AD4C5FD71EB5B8DBBEBDC3671DCAA576FBB206B6FB828BF1FB824BF373F2FBF8FFF20BF4FBE2CBFBFD52CBF7F66C37D7978C7FA1B7AEDF41F7485A476F73E296ED4B878243EA22CD7058D5AEBB6D5F81D3D44D3054D7ADBBB60EB2F5AD4A10FEE10D63AC39A758A689F3CDA7557172A30053E2B34560ADD128292DB492BC7ABFFBF298ABBA8G87070F60C4E4E5
	2FA3D5A11F1C002C787AB1D20372707DE0C9C90931F905648CC56A14DD819729177CC26BA8CCEBE607ABD548A5279D281E9DEC60011DE0C895DA37D969B181DD5D8667817AECE52782D558CBB6CA2B36E9DB49A66A37320628429E36C7DB29C8A8EB2F36AD1B715EB4EC435772D93E7A7DB163BB112C3883915F0994AC505E33AF887FA658F9050C45CE400797E09E67F1AF99B811F26E4DAED1D1D4D169BB6DF6107C7B19C1D5B2FC6FF405546FC7990D295D892E10F6779AF47E9FD0CB8788A9AA14966294GG
	44BEGGD0CB818294G94G88G88G6BD0D1B0A9AA14966294GG44BEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9C95GGGG
**end of data**/
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2002 5:18:21 PM)
 * @return javax.swing.ButtonGroup
 */
public javax.swing.ButtonGroup getButtonGroup()
{
	if( graphOptionsButtonGroup == null)
		graphOptionsButtonGroup = new javax.swing.ButtonGroup();
	return graphOptionsButtonGroup;
}
/**
 * Return the DwellMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getDwellMenuItem() {
	if (ivjDwellMenuItem == null) {
		try {
			ivjDwellMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjDwellMenuItem.setName("DwellMenuItem");
			ivjDwellMenuItem.setText("Dwell Labels");
			ivjDwellMenuItem.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDwellMenuItem;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}
/**
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator2() {
	if (ivjJSeparator2 == null) {
		try {
			ivjJSeparator2 = new javax.swing.JSeparator();
			ivjJSeparator2.setName("JSeparator2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator2;
}
/**
 * Return the LegendMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getLegendMenu() {
	if (ivjLegendMenu == null) {
		try {
			ivjLegendMenu = new javax.swing.JMenu();
			ivjLegendMenu.setName("LegendMenu");
			ivjLegendMenu.setText("Legend");
			ivjLegendMenu.add(getShowMinMaxMenuItem());
			ivjLegendMenu.add(getShowLoadFactorMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLegendMenu;
}
/**
 * Return the LoadDurationMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getLoadDurationMenuItem() {
	if (ivjLoadDurationMenuItem == null) {
		try {
			ivjLoadDurationMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjLoadDurationMenuItem.setName("LoadDurationMenuItem");
			ivjLoadDurationMenuItem.setText("Load Duration");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadDurationMenuItem;
}
/**
 * Return the MinutesResMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getMinuteResMenuItem() {
	if (ivjMinuteResMenuItem == null) {
		try {
			ivjMinuteResMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjMinuteResMenuItem.setName("MinuteResMenuItem");
			ivjMinuteResMenuItem.setText("Minute");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinuteResMenuItem;
}
/**
 * Return the MultiplierMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getMultiplierMenuItem() {
	if (ivjMultiplierMenuItem == null) {
		try {
			ivjMultiplierMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjMultiplierMenuItem.setName("MultiplierMenuItem");
			ivjMultiplierMenuItem.setText("Graph Multiplier");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierMenuItem;
}
/**
 * Return the NoneResMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getNoneResMenuItem() {
	if (ivjNoneResMenuItem == null) {
		try {
			ivjNoneResMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjNoneResMenuItem.setName("NoneResMenuItem");
			ivjNoneResMenuItem.setSelected(true);
			ivjNoneResMenuItem.setText("None");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNoneResMenuItem;
}
/**
 * Return the ShowMinMaxValues property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getPlotMinMaxValuesMenuItem() {
	if (ivjPlotMinMaxValuesMenuItem == null) {
		try {
			ivjPlotMinMaxValuesMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjPlotMinMaxValuesMenuItem.setName("PlotMinMaxValuesMenuItem");
			ivjPlotMinMaxValuesMenuItem.setText("Plot Min/Max Values");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlotMinMaxValuesMenuItem;
}
/**
 * Return the ShowYesterdaysTrend property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getPlotYesterdayMenuItem() {
	if (ivjPlotYesterdayMenuItem == null) {
		try {
			ivjPlotYesterdayMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjPlotYesterdayMenuItem.setName("PlotYesterdayMenuItem");
			ivjPlotYesterdayMenuItem.setText("Plot Yesterday");
			ivjPlotYesterdayMenuItem.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlotYesterdayMenuItem;
}
/**
 * Return the AdvancedOptions property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
public javax.swing.JMenu getReportsMenu() {
	if (ivjReportsMenu == null) {
		try {
			ivjReportsMenu = new javax.swing.JMenu();
			ivjReportsMenu.setName("ReportsMenu");
			ivjReportsMenu.setText("Reports");
			ivjReportsMenu.add(getStatCommChannelReportMenuItem());
			ivjReportsMenu.add(getStatDeviceCommReportMenuItem());
			ivjReportsMenu.add(getStatCarrierCommReportMenuItem());
			ivjReportsMenu.add(getStatTransmitterCommReportMenuItem());
			// user code begin {1}
			ivjReportsMenu.add(getStatCommChannelReportMenuItem());
			ivjReportsMenu.add(getStatDeviceCommReportMenuItem());
			ivjReportsMenu.add(getStatCarrierCommReportMenuItem());
			ivjReportsMenu.add(getStatTransmitterCommReportMenuItem());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReportsMenu;
}
/**
 * Return the ResetPeaksAllPointsMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getResetPeaksAllPointsMenuItem() {
	if (ivjResetPeaksAllPointsMenuItem == null) {
		try {
			ivjResetPeaksAllPointsMenuItem = new javax.swing.JMenuItem();
			ivjResetPeaksAllPointsMenuItem.setName("ResetPeaksAllPointsMenuItem");
			ivjResetPeaksAllPointsMenuItem.setText("All Peak Points");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResetPeaksAllPointsMenuItem;
}
/**
 * Return the ResetPeakSelectedTrendMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getResetPeakSelectedTrendMenuItem() {
	if (ivjResetPeakSelectedTrendMenuItem == null) {
		try {
			ivjResetPeakSelectedTrendMenuItem = new javax.swing.JMenuItem();
			ivjResetPeakSelectedTrendMenuItem.setName("ResetPeakSelectedTrendMenuItem");
			ivjResetPeakSelectedTrendMenuItem.setText("Selected Trend Peaks");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResetPeakSelectedTrendMenuItem;
}
/**
 * Return the ResetPeaksMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getResetPeaksMenu() {
	if (ivjResetPeaksMenu == null) {
		try {
			ivjResetPeaksMenu = new javax.swing.JMenu();
			ivjResetPeaksMenu.setName("ResetPeaksMenu");
			ivjResetPeaksMenu.setText("Reset Peaks");
			ivjResetPeaksMenu.add(getResetPeakSelectedTrendMenuItem());
			ivjResetPeaksMenu.add(getResetPeaksAllPointsMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResetPeaksMenu;
}
/**
 * Return the ResolutionMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenu getResolutionMenu() {
	if (ivjResolutionMenu == null) {
		try {
			ivjResolutionMenu = new javax.swing.JMenu();
			ivjResolutionMenu.setName("ResolutionMenu");
			ivjResolutionMenu.setText("Resolution");
			ivjResolutionMenu.add(getNoneResMenuItem());
			ivjResolutionMenu.add(getSecondResMenuItem());
			ivjResolutionMenu.add(getMinuteResMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResolutionMenu;
}
/**
 * Return the SecondsResMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getSecondResMenuItem() {
	if (ivjSecondResMenuItem == null) {
		try {
			ivjSecondResMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjSecondResMenuItem.setName("SecondResMenuItem");
			ivjSecondResMenuItem.setText("Second");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSecondResMenuItem;
}
/**
 * Return the ShowLoadFactorMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getShowLoadFactorMenuItem() {
	if (ivjShowLoadFactorMenuItem == null) {
		try {
			ivjShowLoadFactorMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjShowLoadFactorMenuItem.setName("ShowLoadFactorMenuItem");
			ivjShowLoadFactorMenuItem.setText("Show Load Factor");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShowLoadFactorMenuItem;
}
/**
 * Return the ShowMinMaxMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getShowMinMaxMenuItem() {
	if (ivjShowMinMaxMenuItem == null) {
		try {
			ivjShowMinMaxMenuItem = new javax.swing.JCheckBoxMenuItem();
			ivjShowMinMaxMenuItem.setName("ShowMinMaxMenuItem");
			ivjShowMinMaxMenuItem.setText("Show Minimum/Maximum");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShowMinMaxMenuItem;
}
	/**
	 * @return
	 */
	public javax.swing.JMenuItem getStatCarrierCommReportMenuItem() {
	if (ivjStatCarrierCommReportMenuItem == null) {
		try {
			ivjStatCarrierCommReportMenuItem = new javax.swing.JMenuItem();
			ivjStatCarrierCommReportMenuItem.setName("StatCarrierCommReportMenuItem");
			ivjStatCarrierCommReportMenuItem.setText("Carrier Comm Stats Report");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatCarrierCommReportMenuItem;
}
	/**
	 * @return
	 */
	public javax.swing.JMenuItem getStatCommChannelReportMenuItem() {
	if (ivjStatCommChannelReportMenuItem == null) {
		try {
			ivjStatCommChannelReportMenuItem = new javax.swing.JMenuItem();
			ivjStatCommChannelReportMenuItem.setName("StatCommChannelReportMenuItem");
			ivjStatCommChannelReportMenuItem.setText("Comm Channel Stats Report");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatCommChannelReportMenuItem;
}
	/**
	 * @return
	 */
	public javax.swing.JMenuItem getStatDeviceCommReportMenuItem() {
	if (ivjStatDeviceCommReportMenuItem == null) {
		try {
			ivjStatDeviceCommReportMenuItem = new javax.swing.JMenuItem();
			ivjStatDeviceCommReportMenuItem.setName("StatDeviceCommReportMenuItem");
			ivjStatDeviceCommReportMenuItem.setText("Device Comm Stats Report");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatDeviceCommReportMenuItem;
}
	/**
	 * @return
	 */
	public javax.swing.JMenuItem getStatTransmitterCommReportMenuItem() {
	if (ivjStatTransmitterCommReportMenuItem == null) {
		try {
			ivjStatTransmitterCommReportMenuItem = new javax.swing.JMenuItem();
			ivjStatTransmitterCommReportMenuItem.setName("StatTransmitterCommReportMenuItem");
			ivjStatTransmitterCommReportMenuItem.setText("Transmitter Comm Stats Report");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatTransmitterCommReportMenuItem;
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
 * This method was created in VisualAge.
 */
private void initialize() {
	try {
		// user code begin {1}
		getButtonGroup().add(getNoneResMenuItem());
		getButtonGroup().add(getMinuteResMenuItem());
		getButtonGroup().add(getSecondResMenuItem());
		// user code end
		setName("OptionsMenu");
		setMnemonic('o');
		setText("Options");
		add(getMultiplierMenuItem());
		add(getDwellMenuItem());
		add(getPlotYesterdayMenuItem());
		add(getPlotMinMaxValuesMenuItem());
		add(getLoadDurationMenuItem());
		add(getJSeparator1());
		add(getLegendMenu());
		add(getResolutionMenu());
		add(getJSeparator2());
		add(getAdvancedOptionsMenuItem());
		add(getResetPeaksMenu());
//		add(getReportsMenu());
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
		OptionsMenu aOptionsMenu;
		aOptionsMenu = new OptionsMenu();
		frame.setContentPane(aOptionsMenu);
		frame.setSize(aOptionsMenu.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JMenu");
		exception.printStackTrace(System.out);
	}
}
public void setSelectedOptions(int optionsMask)
{
	if( (optionsMask & GraphRenderers.GRAPH_MULTIPLIER_MASK) == GraphRenderers.GRAPH_MULTIPLIER_MASK)
		getMultiplierMenuItem().setSelected(true);

	if( (optionsMask & GraphRenderers.PLOT_MIN_MAX_MASK) == GraphRenderers.PLOT_MIN_MAX_MASK)
		getPlotMinMaxValuesMenuItem().setSelected(true);

	if( (optionsMask & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK)
		getLoadDurationMenuItem().setSelected(true);
		
	if( (optionsMask & GraphRenderers.LEGEND_MIN_MAX_MASK) == GraphRenderers.LEGEND_MIN_MAX_MASK)
		getShowMinMaxMenuItem().setSelected(true);

	if( (optionsMask & GraphRenderers.LEGEND_LOAD_FACTOR_MASK) == GraphRenderers.LEGEND_LOAD_FACTOR_MASK)
		getShowLoadFactorMenuItem().setSelected(true);
}
private void setSelectedResolution(long resolution)
{
	if( resolution == 1000)
		getSecondResMenuItem().setSelected(true);
	else if( resolution == 60000)
		getMinuteResMenuItem().setSelected(true);
	else
		getNoneResMenuItem().setSelected(true);
}
}
