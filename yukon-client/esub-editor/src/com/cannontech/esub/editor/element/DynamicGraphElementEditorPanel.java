package com.cannontech.esub.editor.element;

import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.graph.model.TrendModelType;
import com.cannontech.util.ServletUtil;

/**
 * Creation date: (9/25/2002 11:58:02 AM)
 * @author: alauinger
 */
public class DynamicGraphElementEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.TreeSelectionListener {
	private DynamicGraphElement dynamicGraphElement;
	private javax.swing.JRadioButton ivjBarRadioButton = null;
	private javax.swing.ButtonGroup ivjDisplayRangeButtonGroup = null;
	private javax.swing.JPanel ivjDisplayRangePanel = null;
	private javax.swing.JPanel ivjEditorGraphSettingsPanel = null;
	private javax.swing.JSplitPane ivjEditorSplitPane = null;
	private javax.swing.JPanel ivjGraphTypePanel = null;
	private javax.swing.JRadioButton ivjLineRadioButton = null;
	private javax.swing.JRadioButton ivjPrevious2DaysRadioButton = null;
	private javax.swing.JRadioButton ivjPrevious3DaysRadioButton = null;
	private javax.swing.JRadioButton ivjPrevious7DaysRadioButton = null;
	private javax.swing.JPanel ivjRightParentPanel = null;
	private javax.swing.JRadioButton ivjStepLineRadioButton = null;
	private javax.swing.JRadioButton ivjThreeDBarRadioButton = null;
	private javax.swing.JRadioButton ivjTodayRadioButton = null;
	private javax.swing.JRadioButton ivjYesterdayRadioButton = null;
	private javax.swing.ButtonGroup ivjGraphTypeButtonGroup = null;
	private GraphDefinitionSelectionPanel ivjGraphDefinitionSelectionPanel = null;
	private javax.swing.JPanel ivjGraphDefinitionPanel = null;
/**
 * DynamicGraphElementEditorPanel constructor comment.
 */
public DynamicGraphElementEditorPanel() {
	super();
	initialize();
}
/**
 * Return the BarRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getBarRadioButton() {
	if (ivjBarRadioButton == null) {
		try {
			ivjBarRadioButton = new javax.swing.JRadioButton();
			ivjBarRadioButton.setName("BarRadioButton");
			ivjBarRadioButton.setText("Bar");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBarRadioButton;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD3C7C2ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8BD8D4571924B589CDE9C2AAEDB13515EDC856B6F4C3DA134FE6EDE27331ADEB6C46EEE9E3367845ED7C3A36B52D59E59BB7A171428CB0A8A86F1728232222729815A8A2AA0FD14028999185AB09DCE6AEB0F11899E786C1033A7FB9677EE7AEB3F7F8ED6C77AD9F7FF76F799F677F4FFFDE7FF94C957236C624A4A56789C2D20AA07F9DCE9204C833A09C0B71389057AA046A84455FB600EC6171D1
	ADBC3B0016E78E55E5895F9289138D5209BD213AC2F84F953EA34E680117GFA3A01A61E52E40C2F676BF7D1CF9ACD6F5ACB84B6GB881978630DCA07A0F6FAD127173A13DC1FEA710148CE5CAA2BC170A0A947832A446B7824F900056E442F3F9EF29CCDF85B8422385FCD1B263B540B31DDAF7FFDD2D4C577DE8181030E8FDF2A65125489FA1D4161DA7BF7542CC6A880DF0FFEA8BBC6BCD577A0F1B9D03D5E651EEF758BD12F920CAF28F75D6C9962B47612A12EC5220E477545599EF5A45C12B3945A5BA87CE
	B264C94AE2926D12CDA853907BC70BF4B4EF309F74C5D3DCC2917159G3C878104EB308C3F482C17ED8C3B96A6ACFF7C7D24C0E5C850B05B0B345EB24C4859240955127C3FC06B0448F5C1BA94005932E8AF5A12110236849590DEA3243B81BC9C7FF8A14107C0BA92E0C18A5AF8ECB75A58755963421A5E2A640D81EC0CCBE136A526F8EDEC2D98519453F633BD0954FDEDAA6885F08204814C83088358C45B6B323BFB6159586BE9F68C8EBA6C0DCEE7134D62BA6B30C8B6F82F2B830A0CEB174C8E17C590D81B
	3C2F28C2459E04D529E35BA381D636CE815BD0D47BE30247D3161C22D236F93F4ACC9652D89EF9E90A3225913F8D94933F5540BB11A3728C6F267890851E5555BB34AD10F4845034BA74F3578634650C76E1A1EB3FA1B9CD45167017F6A68F68188DC1BA2FAD4C47E99A6263A8404782AC87D883D0149E2A2BCDC79F6F968ECE6463B66883AE3BE83BB0546B94DD1E26A12B4DA239E4DF876F9D4F57EA40643EBEE5B96617398715F5947F43EAD53F444F4DCB6EC8E77E3025FB7D42F43F92D0F7C83AFF5B58406B
	B001761508F4D607E99CAF503EB2DF01E7F57BG65A769A5C02BD987E39A65AF24F59E3E0E71A771FC74949F294033FC3EDB087DB99AE8F96BC3F52CFE1F501231F18452E1GD1GB1GABG048C604B40BAF68955136CC7A6C57DB259FDC9857014C6BCAE314D6E7608F633F4C9F4D945DE1B6496E41E9A6A575EB1F2C7E87FB6030E9649AE39C40F55E1075C45C1899A131B71B4A5E5939E17C4299D56C189948CBA854C77F4D2AE29BF516D396834089E494B41686DC924CD5C90AFC10E10813CDBB2083F4269
	18DB836F268C74553F26905CDCC8AF64FED91A14834F8F21F1CBB6984E2DE6E206683A09ED54DECCE46200BFA1837B651F3A307D99B3E789953F2AD2EDFFF4EC4E146728CC5F76F713665B857811CC1CC70562AE05AC51B7153E31C136F37CBED7BCC6C74A9473A7209907467C5C10C65BB614EB9EC0DCA67ABAB7D94F5AE01368163055FD640286A3D93C1EE73CD99AB227C8B7E0C2B5CACE49EE019737DC67051AF4EFFB6CF370363219624978548C8D464D71BBB4598C8FF9A8DBB75277D13A6871382C3DC39E
	69026471D86D7DDE790354967718214FAD1735C9C74A3A8A4A285DG7DED8316F501A6D7512E4EB89C57061C9D2447486D73F7F49EAE877E068DD87F6F5031428269D000E80ECF25FD1F704581A4F0FCC80132AE897FD46BB2D416312848B29DCF0C5111B60D7602EDCC2D5DB09D1B49B0A13F6FD23CBFDD58B58DFD138526A3761E6AE0ED61E9ED60367C1CD63DADAF502A37651734633755C5DA35364AE83F50123AF08C5B95ED7F57DAB27E199DF63BC4BBA09791D8BC7C278ED28E2B5D6A6934DA7AA54F856B
	AD8962630D248DBFCD4758BCF8AF5F08E363708E9C9B4BEEFDCDD8F8B5DDB5FE8AFF22BCB9E8A3EBF3E19B3DE3A333695594525E65983B59B1E4774831A09D3FE53CDBC3F17D67E091A350783B6326D3EA9A72F89C769617E34849681D47493A47E8F5BBED624DF6515E2FE4117BF0D0BA51B78F6C0885D83E917B7065D4C247BEAC8FD44D8E1B0D398AE875D9448FCF9567A94A36E6A3DB8315830D515F5C2EC61F1105FACE25E511BA132E8FC9B03512C162C36926BCBF1ECD4B7121DD92EDC35EF977E25AFAF8
	5A94638B43376A54FDF8CEC756EE57F87E6D3AD4014C1FACCFD66751A2495369328E42F0CC86CCF92D765EDE65DAAD9C6CA76B3A2570DC8540E487CB3790FF0F3FAC92BE0F3AE33E7D30D069DBEDB633E9832F1B6100F4C3B6217F142F4D2AB9EE9945B94001BFCB431873EC16F2EDE9F1EFD3ED5784BC59AC7F60EC5FD87C59F5B47EA0B6E4E35D3F2AD34E930DEE3743EC252110DC0F8F24AB5BBC43FDB9FD9DBC7B2CAEB7446596E9845B42236944D692AB49E839BEFFDF30DD700F7DD80570G5FCC9AB4DBA3
	7E1646A6E4FCDC1C0D7D7A76A9653AF44146026475814A2F4DE165AE4BD12CCBE14D4772AE5B01F9DBF2304D4C2AA36B8F35B537199ADC55BFE42D9A72D8C900A6FAE01CF48EF9146B6B7EFDBA392C33F3F06DBA330A347FB8C82F825066222E772AB6232ECF459B62C815FB98464F3ABAE54B62EB41834ABADF38BCBFE01D17664AEDAA57374EDBB208EDE7G5F130BF51E31AEDBD16730EE05103A9FFAAE5663736BB42C0F4A69975625E09F95F05E7A0FE25F750BA757BB0787AB5EE74B6F4C1E991924BFC483
	EEB1408A00C40052BC346D647A895702A7C746D98B6E5961EBD7FD1E579613625DA23FB3B95BBEDF399185EFC81EFFFC4256CBE955F4BD164756CB9A0E4F2678E8851E2D23E651F6C1528B01D6160FEB0347B710FA8A06F4F8BE7AE24E7ACCC5BD199CD63B075751198CB2EEBADC56FE2B9DF167A94E6CF051891645F96F533EB497720C85E038EFEEA338D5105696A0EE41F69A8FC23A3EG7507E4860E9BE6E62A478D33B2556306B919634740FF1F99A82EE076B56C70AFC7678EACC7B7AF47C89916A308633E
	C87BE7A42467F3DCCFA54A4697E0BC1BD9CD66A621905305B4DD4A53F32AA69B2B11BC26978F4E1F340E7156C1634703F3B710B6B94253D1B48DCBCEF93CF67410B93F1F4C0BD6335BA8394DAE2B9346EAC108A9A43E4BAFA173F0833C8FG04834CD7141765714CB0991F7BE0318D55F841A9190508A2A27B03EDB807E7C0DA8F50G508390D404E36453874953A6761229DA1E2BFF39BD50DC4D72356F40FCD7C2BA912034985AB2C0B7G4B6311327166F34673583ED42A7B26E388BA5B4AE21277B37BB06FD1
	C80781ACGD8D24C56CECC7FF0A57298CAC2F5C6G8B408C004892547F49D6521F583CF741E93352C9C40EEDDA2B1531CDCC894BFB95BC05CDA8EFA8A1E305E25A28D2583F7F905092B6113C4E141489EC2F1CC03A8C24FF9581BD92003C4770FC6FECA33E3DA17676DB7B5C5D96294FB9F4EB7F21AAC8B1FAB849C61E132E2C43F9B8A4FE26109739AC607EF0EDA9EB935D255EB9E9D4B825E5F366D2BA869139B020AFAEA53854B1F1AA513D9F208A0051EB4750C999E60E21FFBA061E6EA7BFFB8C1D3431CDE9
	0C4E4A7672CEACDB624B33855B331B836E5F67EDE6E52A151FD90692413973BEB946885B01E5F3EE46323D34E37C32A56C70AD5B33C9CA7A2E32714B56D566DB36CA39ECEC5EF9ED9F6E5F4D851B16EF463E763BAAB28670F824CA9C76C0B062A0EB0DB32C9330B2ED2B40B295ED419867238A1227895283GC1DBB0D69B5905FE0CFA72E161CCD0352A9F17F794A407EFE17EDB30455BC6B2F82C762DCA5439E68B7A7165CADFBF8E27A87D70DE252F9F770C71D3F5152F9F2D29CA3AD8656BC714E77E7AE6D529
	20963F1D669D1A599ED40DF13DE1AB7AAB22161E39C0BA88A0FCAB1E09FDDA92844F877A7A60FFA058BCABB4E8562C02605EA6DD3026B9B5585C2C8BCEEECA8D6EB56A051AE6C1E7B6B69DB0B6256BFBCF8E97DEB79A8D66161B6E613C9AFDEF4B7070F0CE2F41FC2AF0787A85A1353769FCF6FED36A00593873E2996453FB007C25860B340E8A02C1D99F5107FF9DFAE2631C92ACFFB458C7BAFE3D1E558D9BDBDE50AB47960D609DDF4201EF055EBB8F303630B5D399435A7E65AD3A1F2A9A37EFE332B572F3D5
	ED099CB33E1EC5464681400FG04GC4824C87085B0631CA75C6D2CF0D4E3EF6496D98F219253322DD6C17CF49G5DB688A965D4A64F374B36CD6F8C60A7E9584667BD7FF5E17E834B12822DC7D64B65495A4E1EF32BCAA81E15EDB0074E25C06B8688828887088208590EE53B311158C8A2DF18BA9ABD9E51BCC0D7E39890DF32CA43DEB419ECD959E6794542130BC1DE770B05A7A77766B465F6D5CCD50E45585BAAC9FFDA3A5D778CE3AF452F586EFB06F13182474055C06B58013177CFF3C99D4C07F49C40AA
	GED994466G8DE5D887AFEEB488932CCBBA9C4EB390AC5B9461ADAB5FF7AA2767177B2B47136BA88B34A649BBC24719B25F733F2D94BFDA66FB7E77E4AD1E7F0582AD218C4725937AA0E16CDF90A09652BFC1F87249C50BD62F960435EB5FFD17BCA360AFB2127010BF463FF62D17FFAD7D93267C47EC3CCF4F4F13F7066AC2F762DCA30D6278E2389CA298E645876C0731BB193FD66E740EAF2C1C717A86F85EDEA3886FA542DCA3435A77197DFEC07009DE383B065949464F24EDB8FE666DE2F32CE117F27D3B
	A71F0C1F2D006B86F0824CG188DB08FA086E0A9C042AEECF75A2CF2F832C657A2C764F5F54D7DC6690655AC11FD96F9E1C687B8A633B20B2CDB6C62201CDE1525DC3FB35CDBD9E48F47A58F16ED96443F1DC577C6EC36F3A0AE639233487CED353BA53B5B6A315E206CB2CD131DB516C63787D9D92F9790DFE46D8E55D5G18GACGA33B31ECB33365FDA115B16D2425DFEAF65053BE8F6A3A1193F8BDFABBE762B34287F2C819FB9D90B38F127C919F12C34A6CF1B8A9D25E4B4AA16D8B1657839E46AA633F11
	C36F34C8FD944D7A696ECAD27F6C5D20F83FD2F16F4F8FBC77D487AB73AB39017D7E6BDC75FD0078DC75FD005F660EFFFE70075C406787EF65AA4F8F58BAFEF6B52E6343F663BA3D648841AD04F4BC476D2438C4C86B4B9197D56B6BBBA273FF75D97CEE653E52BF7969184E3A5D9770CC0B69F8D0BF3D7965EBD316E37D7A993A27ED84BFF6810CG8483C414E33F3E1D27975446AC3AABACD9CC228DEADDE2E36F26EDE47C40BF1CA358185F388D477C791077EA1E7F8779247C43D60BE700371B3AFC32EE9F10
	C807C55C894A87A324024F18CF668737D9345B2CF649A4DA701CBF1F686A3D69115CA6D87AG0D61CD79E4AC35089E1125BB7253E83B26C510E5AF6713B2C08F303B658DAE6B08E4E3349E9F1A7966D951FD0D51EE96846ED7C185930FE7DFAA20FB73B08E11AD9106FB38006E8B767539A56E071985D99C5766B6B948C06D30576FA1BEFE6E30FE1C7AAE5F435AB25B87301511FC5A090BCD72C61EDD32B15A451DFCFF46A8751173BAFA598268ECFF237308F2FFE38664CB76556642F381G63493F2A4693874F
	15FB306EFFD4C86BD934DBECFC7CFE21B0C72E3BF64989D39AD8460EB0E47ACF8BC99D3A2D37E49FADAAD4BF33DAC24F399D6E76162613F69820A4B76BA7ADD3DEA3B0390BD31613773A8E13B2B6CB644E932EB706A5DCEF14BE7508D03A75B5FAEFC5AD4E917632BA2B586BDDD3631821917EEB8F71314A7A155695DB3BBEF2844FB3FA76625AF57ED1FA27022481E27762FD3E33D711F7B9675DA7925E22FD21BA83C06BBE0C336B0AC9DB6B0174A8C08440FC0045GABG56GE4D5E0BDF7941138671278297F
	E991A05986850D8FDC22556EE17E8EBB626B6F0A8A6FF85ED091A8DE2E28A776F6D4786E619F22F8CB056F9E7E8BC7F18F5F83342595B81F34DF75BBFB28D41CBDD48652BF077A4BD2692B7F3B946F2974553FDFC47DB3003632121F1D94879ECF4E94CFBC1EDCACD60F0F2E947B47C7BD456A71D1DF31B2BEE238EBF23E244EE8D8A973DEAFA6F1A55BEBE54BF56F3C7E94ED03E72AC2F5BDD538E73F77A84E75B32A5067876AC95F0E6669FA1A0E63691823D3090FC8BED36F674B2624E32A73BFAB3B157633B5
	E0DFC6B57A635BBD688FC3B516778911143F1B271F2669E01EBEAC7A5ACAF827E24F447EB0796978BCE21E31E364CBC77C7769DF63F3CCD4B55B270F0167D2G92779599E8CCF1D4AF732CD6A93756201C6FDE39CD72E3A38F1D59B0DAD54CEB03753EF39651558379F88C383F9EF4555F56EF0C196B082DA1409FE1E071A93375599E34F53E415F5645E3EC6D3CC94E15FD665F8B12FCD5C43E76A01FB5AC97153A63E51D067D21BAA3G9BE3126AB0766A86DC58FE9C6F5B6AF0CFF671FE9C6F07A92F76G4C57
	GB5871057FA946706B3875097FFF71424DDACCC136736CF3314F3DB7C4DBF871C5BE69CE0E50EBEB0F6EEE37A1E28C7FDAB8F20ED8F9CA736698F066AEA81CC8751369FDF45F28C9CC45EBFDE25E739101E8F300473AE6B61799E44F26453FEC546B9B62FBED0FACA18423D7D89AA0A69AD6AC53D7A9A347167B4CE6D023493A038867BF5DA5D74E2147CE94A1D1B329C1B537EC85BCAC40D6F1EDD8245C755786E595D666DF0BE50CABF40725A0FAA758781BE6C037147B196037C3104742939409B833092C0B8
	942A4B8328GE8G68BA4477DECA768901E3903E616F8DC3E45FFC2F5E6FEE44A1455D08C3016200F7E8DB1DFD48F75FAE65843DCBF048F75FAE64B86E5B45G2D7CB07A66A53FB8E4F2F5F335077EBE6030EF5D8CD0FC78E15F3AF96BAA56CDA450F435287F37BDCA7DB64087550EDFB772BD4CDE148BD3611F2B02E376B552FB2B8BEAFD6DBECD7131353EF67FB29F6D0E83DA6D91347B2DBA015B720E627D0BF577FE9FE776BD554162057B69F8BC9B4A7B3196F18FD15CBC4039D3B0AE7BCBC960B8723D1209
	634854CD6AF1E446A67FB8B2FB13FA9CD93849BF0EAC5D4476AC7BE120D446115BB7850EA3FFD20FF16412A398B79DBD0EF1D3822FFB618441696BC2F575F508DBCFF15D108E62382F9EA775C4F8147E5557FDBEB1945371945561FBB77E730A21188E29475B3E6D0A77372F5E7B361E55F36F5B7A7F74E0DB4F673877FB14ED1D61EA689C9B897537308E5B4709FCEC9FF13C5EDB8A3CBEB2A95EDF4B3F777E7A4FFB2A0345AF62AEB2A652F0C434F5122DB506AF9D497462DB656DB5B9067D830FFD0523903382
	B4GF4G1075975BDFEABF2E9FC7376D28B2063DF61D3C73BBBEE36E6A9A8E1018D0F9F7E7ECCCFB6C04EF9CCEEC88027C430E627E527ADBEABCD1C0DFF894633A8FFBC9F91DAE29CFF239A48B3BEEAEE45593F98BEDABCB01F745D1AC5F25FCACDFC73D7F98B0A063583C13F38C671DD1404F2B47BB987BFB465CA9E0F7B0DADCD6CB13584FAEE130F8702F47B0E6DDD50F71E0B04D336C98588350F18C4B61B8CE72BE67F0513DCFA67F1B863C7BEABB06723D497477DE10DEG90F78CE35E66DB483B0A733E57
	C2F59D87DDG9DG3663647739B96498C72E279A6A8F3BB42C4722D4992D0364F048E1AFBFE0B0D4688F9EB83807457D91434A3B81356BB6853C5F9AFD1C79B1C6FE0E0AA5F2196E64E30C1CF89C4B7EDC3EF27F0C752122E353BC1B1E265C55A94B3118A51156E74D895FD8A60546FA8DA7FCE31907F91BBA83346893B8DEADEFD06A4FEB007830415BBE6B9B8245D23F266D4154407424F37C9B94EFD360197E810AA769E0202DEAC07D2D37147ACD0DB036B6FAF50EB48652FF3D1546200DCC4FFA0E3FCB71D1
	8ABC532F21F812DE8CB4C3931E8B3FF2C55936329ECBC9C63F796A8DEBE236C4B4795E1D3EC46FF14581FEF5932E89FA370F37A6B0399CCE496539493074A894E348FC25BF321A211FB5CFA6A6CEBEAE7077750A7730E377FE5E797EBD5541665A376918D48B3EB8530C73EFFC9346ECCE0E3BE46467FC4D5846621349F9448AC88B46D01D9E205E08345FD3DAB724C781E683AC60B4FB72C465A2F911BCB1CDF4549BA7976349E7F2936AA0FC4A988F6FD2CD5EF7D7EF216F620D68273AD1741DF092F173EE236F
	4ACE228F06DB49F90D875221G51GF1GEBB87DFECAAFBD8565866886988518FD8A69692D24FCC4165803321331BDDA66730C434FF4ECEFD5464144B76B2750FE512701F8D89FBFCF2EE3603D686DBB05GFD3B3157F39A66330FFABF154C1E8E311F4BDC59C1726267592C4B4B34DEFA27D30E1D2C3768195485E0347733FAF9AC9FE3549349D827F372B1C63D1804F36A06C6B21F317DF30C3DF6B6E2AC33G6AA48EA02B856A97002CD798CFCAB372543730782C831E83B2CFEC8B5D3FB49AE51E20967F7A40
	B1D3A3C398704C8558A9279734F867A3E66BC09DD9BB127353F651E2F5308B4AB2EDF49D397BF741A3B95569EFFFCAF4B609AEA5C926A59CA33E6C98F0C912F1AC877B1D55E60D725EE6D6ABABE3FDAB7E864EF3C70DEE627435A6B53A055323EA5568A32D686BE09EDB0634327A0804677CD636F6E4BC66D164D9524A6AE3A5BC93E59ED69FAF5F467A48EBD32F8F0D824A01273E4DDB9F26B65F7A00390F784DE1A1D1B3779923DD223F4539AC39A1CED2237FF90858EBF2C9B72C0EA1778123F8536D4773632D
	CA1E032ABC3FB92964B96C43437C7BFC2B2FFF09CF3A5B507F576DEAF4A7273F38D70DBE035313BB5568611CFE26DA0D9E55067B61E79B796FF6G37268DE3779F2FA37782A7755B318E2B47A63113CB963BFE2D115F91BE0DB1761EA6FAF78F52C1G6127D1FFE5B37F7E8260D61E46B8FD289957847A8F79F7D3A8EF97241DG419FA26F370D98ABC7GEEAB2D9BC170FEE0E77589FF9C44FCB4465F340B3C33586A5FB3147B7546D717A56D9054E32B259F32B6DCFEC6719B00AD4FA5B35F95B4ED91D44F2247
	74FFE6FB07C6A76B6FB90365F96A8E16A71C638E1DC75C920EFB2396F13A33684F68DBD81F8D009BB90B3CEBEEE11DC41FC5FFBE789961DD89E9619C745BF3184717C731CE8C009BB807F97CF2946BA4629C6611F41B5ED50574EAG5DC7481BF81B6F71FFB455BA796AEE724E72F92B9557EBDDBC6F39F64CBB14637E3297F1319CF72493F10289F1DF2C467CEACDB867DFE81CE8CD63B40D9D33181CB8215C961FB413FBECC2B9A50418705BA65F22FDA842643B06FA126223CC3EEB286AA6FE46G345273D85E
	034D53DB7BB5CED36E56146564EF3998097DC1677D3E65C271A1677D3E65424712F020A57272BEEED46A6FE98F55F95AA7F32692D4CE662CE0600D840885D88E10G10F18162F2009A00D6009E00910090008800F9GB1GCB81D6DD40B5D64FAEF217BF509ACB5E98D11C3A207FAEEB087DC247D81B7364F4397C2CD75019DC749DB53F1BE4BECB073F8F185C5173EAF2A3B2FFC8C0391535EAF291B27F3C00F24F5D1ADE7F0B19D0CE3D7F3DB7211C92E2BB307F3DCB474B788E5F76282563654A8E5F7678154F
	30BDA6G2D6BA23647C7C727570F66CCD3EE691465D8390668583E6022EFF96FD0FC6CC55F72265D6667CFC02B3D0465CD9A538EE783BE7A52E476887E52CA64E2FCF8174A69D5AAF9486BFB3BDAF905CE793BE21D01645ED57DF6D5394CDF9FD06EF827EABF1379AD816502696FA0C6FC68A1F2BAC2C50E5C0550888BF3595EB53969594C3E82A560BA6B999AFB4D835958CEBE7EB589DCE714627D0326FB3F57F26EEF20E33351DB26DD0A77271B7F865F393927BAD81DBEEF4475F492473DE84475B4413179A5
	2CBC707966B74BA7BE5F0CACD7BF5F7CDE397F796653656A671B4F167B1FEFBEDFCEE230FE185F3AB85F0B9CF71963E2E81E8457B9668E74A2EA9741370E39873D045AB5CCF1DDB26ED78A5CE59977EF65E41F810ABCC0FEA047F02F1713FBAEFDD61BFC1F798D4AC3FE6F61617ED8C9ED34BAC5055DEFF25CE5DE97CE9A4347C1FFDB5909FBD3BF2238C4C8E7F4A1EE0B96772B0AB86E9D9AA78FC0FA86477D6C8E4A06F2DC79DD141D4DF16D6711EF95477D70BC722D613835354857FA19FFA7608872F5F0DC5E
	E7B8F6C4DE66E3772837BF2DD33C4F7C6C5E772D283F010E4FEEFB4BB4C3711EF97B5E6B5EF5CFF530BAAD3A63BFF6EC3E239CBB98AE338D5BF0B4EF8F675B305D44F05C8F5A305DAC613898BB72697F0A381D8E642B60381CFD48B74AF16EFD48974CF197BB11EFA947BD5709FCABB8EECEB55ADCF385F1AB2AF9DB673813865E562F60D9DAECA369479E32F5A1603A331FFC92D55EDFFA1076DFB73D3FA6D958B96937B538060E6479AC5110F1448383AB4B63A1F20CA230BD2F8F343867B56F4A44FB10314093
	8FC0769C08ED2B2F786EF9E55FA1733EC8AF5411DF2108F68B377D7BF409405ED7D7FAEDB8F197EDC88CE003E69C9B323A3DB6303C37D4FB73DEF49E73AE6DD64FBBE51C3C8DFEF9F79A3CF98F9F413C6B83641DB6CE5EA63F3CEF114F8A48675D075A309E3B3B712C377220D777BF3721EEDBG5DCA3F85F50F559D66275BD4634D3B5E01F9C78448DBB3CE5E737C72F6FF604D7B21FD187742C95407EF5EF1FEF9BFE972667D73CE4CFB79A46A43B76F84C55E2C8EE22BF1AFC978986BA0335D2B2F5B40770BBF
	D65717BE0E3E720FFD7B4E332C1F3A06646F9874E9F1EF286663296ECF9D3DC05ED9BE4E3B180F0D1763138EAF7DDD3EEF97496977DD74526D7CEEEFBC27C7DE92385DE75B465A7DCE9B7267FD22EEB74B63E71DDE9D038EFEDF799354715FDDDE7ABF726FFC4D6074223FFA696F73FD34851CFE6A0A5746572B892DCF3439A5AE7395834AA4F019A7BE96841C7B2EE915EB260A2B213A062B13D9938E5DD54AF5004C40246404F6255CA84804CECA4EBD664EEBA4484C1F145C5AB6255CE210D9BEA9B917C3A917
	GB23A1E4948AD9C730D02D210B1CCCACE532514B302CC5724643EEA98F39796E402A610E3F37197347E714803DA7FF8A4642EBF5F23F77D798A5B7D79CA5B7D799E2A7767FB245E1F2F6034BF5F26537EFCDFF27873BD6C7067FBF51FBFDF7CBEFF3E12AEFF3E2DDD7EFC5FB578737D03C14947FEFF7A7A2DF549029075261062F689B97EF7169A9FFE6D55282B463CE4A139CDC8E53FAFED93D2680FCFC71CB0F2B6C3B631A03B114B36514551729B876BB4EFBC2EC99634A048960BED02162C78C05E45725140
	E21164A2E815603EAE5C9112FD319EE19D3D9A27A7F763BC02161C21FA04BC7220F8452109C7C8A7272CFA4622E9A21F50CCBDE8545317CC79921D1E5E227388A974F090C478C120C7580807F57A83FAF2DC27449C74439C2698F52B7F3FE1710CEB4FDD657D21B577A7A6853A1B56E5E67D59E6763DBF7498DD334E85FC0C9977367FB0CA024EC0F78CAE8C0AB61BC734DDEB34DB48FD03A672FBE401FFC36FB6495F0DBCB211511E3CCD6C7ED4A1733FD0CB8788822E2862479FGG4CE3GGD0CB818294G94
	G88G88GD3C7C2AD822E2862479FGG4CE3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG81A0GGGG
**end of data**/
}
/**
 * Return the DisplayRangeButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getDisplayRangeButtonGroup() {
	if (ivjDisplayRangeButtonGroup == null) {
		try {
			ivjDisplayRangeButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayRangeButtonGroup;
}
/**
 * Return the DisplayRangePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDisplayRangePanel() {
	if (ivjDisplayRangePanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("Display Range");
			ivjDisplayRangePanel = new javax.swing.JPanel();
			ivjDisplayRangePanel.setName("DisplayRangePanel");
			ivjDisplayRangePanel.setBorder(ivjLocalBorder1);
			ivjDisplayRangePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTodayRadioButton = new java.awt.GridBagConstraints();
			constraintsTodayRadioButton.gridx = 0; constraintsTodayRadioButton.gridy = 0;
			constraintsTodayRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTodayRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTodayRadioButton.weightx = 1.0;
			constraintsTodayRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getDisplayRangePanel().add(getTodayRadioButton(), constraintsTodayRadioButton);

			java.awt.GridBagConstraints constraintsYesterdayRadioButton = new java.awt.GridBagConstraints();
			constraintsYesterdayRadioButton.gridx = 0; constraintsYesterdayRadioButton.gridy = 1;
			constraintsYesterdayRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsYesterdayRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsYesterdayRadioButton.weightx = 1.0;
			constraintsYesterdayRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getDisplayRangePanel().add(getYesterdayRadioButton(), constraintsYesterdayRadioButton);

			java.awt.GridBagConstraints constraintsPrevious2DaysRadioButton = new java.awt.GridBagConstraints();
			constraintsPrevious2DaysRadioButton.gridx = 0; constraintsPrevious2DaysRadioButton.gridy = 2;
			constraintsPrevious2DaysRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPrevious2DaysRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPrevious2DaysRadioButton.weightx = 1.0;
			constraintsPrevious2DaysRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getDisplayRangePanel().add(getPrevious2DaysRadioButton(), constraintsPrevious2DaysRadioButton);

			java.awt.GridBagConstraints constraintsPrevious3DaysRadioButton = new java.awt.GridBagConstraints();
			constraintsPrevious3DaysRadioButton.gridx = 0; constraintsPrevious3DaysRadioButton.gridy = 3;
			constraintsPrevious3DaysRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPrevious3DaysRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPrevious3DaysRadioButton.weightx = 1.0;
			constraintsPrevious3DaysRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getDisplayRangePanel().add(getPrevious3DaysRadioButton(), constraintsPrevious3DaysRadioButton);

			java.awt.GridBagConstraints constraintsPrevious7DaysRadioButton = new java.awt.GridBagConstraints();
			constraintsPrevious7DaysRadioButton.gridx = 0; constraintsPrevious7DaysRadioButton.gridy = 4;
			constraintsPrevious7DaysRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPrevious7DaysRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPrevious7DaysRadioButton.weightx = 1.0;
			constraintsPrevious7DaysRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getDisplayRangePanel().add(getPrevious7DaysRadioButton(), constraintsPrevious7DaysRadioButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayRangePanel;
}
	/**
	 * Returns the dynamicGraphElement.
	 * @return DynamicGraphElement
	 */
	public DynamicGraphElement getDynamicGraphElement() {
		return dynamicGraphElement;
	}
/**
 * Return the EditorGraphSettingsPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getEditorGraphSettingsPanel() {
	if (ivjEditorGraphSettingsPanel == null) {
		try {
			ivjEditorGraphSettingsPanel = new javax.swing.JPanel();
			ivjEditorGraphSettingsPanel.setName("EditorGraphSettingsPanel");
			ivjEditorGraphSettingsPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsGraphTypePanel = new java.awt.GridBagConstraints();
			constraintsGraphTypePanel.gridx = 0; constraintsGraphTypePanel.gridy = 0;
			constraintsGraphTypePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsGraphTypePanel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsGraphTypePanel.weightx = 1.0;
			constraintsGraphTypePanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getEditorGraphSettingsPanel().add(getGraphTypePanel(), constraintsGraphTypePanel);

			java.awt.GridBagConstraints constraintsDisplayRangePanel = new java.awt.GridBagConstraints();
			constraintsDisplayRangePanel.gridx = 0; constraintsDisplayRangePanel.gridy = 1;
			constraintsDisplayRangePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDisplayRangePanel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsDisplayRangePanel.weightx = 1.0;
			constraintsDisplayRangePanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getEditorGraphSettingsPanel().add(getDisplayRangePanel(), constraintsDisplayRangePanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditorGraphSettingsPanel;
}
/**
 * Return the EditorSplitPane property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getEditorSplitPane() {
	if (ivjEditorSplitPane == null) {
		try {
			ivjEditorSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjEditorSplitPane.setName("EditorSplitPane");
			getEditorSplitPane().add(getRightParentPanel(), "right");
			getEditorSplitPane().add(getGraphDefinitionPanel(), "left");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditorSplitPane;
}
/**
 * Return the GraphDefinitionPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGraphDefinitionPanel() {
	if (ivjGraphDefinitionPanel == null) {
		try {
			ivjGraphDefinitionPanel = new javax.swing.JPanel();
			ivjGraphDefinitionPanel.setName("GraphDefinitionPanel");
			ivjGraphDefinitionPanel.setLayout(new java.awt.BorderLayout());
			getGraphDefinitionPanel().add(getGraphDefinitionSelectionPanel(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGraphDefinitionPanel;
}
/**
 * Return the GraphDefinitionSelectionPanel property value.
 * @return com.cannontech.esub.editor.element.GraphDefinitionSelectionPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private GraphDefinitionSelectionPanel getGraphDefinitionSelectionPanel() {
	if (ivjGraphDefinitionSelectionPanel == null) {
		try {
			ivjGraphDefinitionSelectionPanel = new com.cannontech.esub.editor.element.GraphDefinitionSelectionPanel();
			ivjGraphDefinitionSelectionPanel.setName("GraphDefinitionSelectionPanel");
			ivjGraphDefinitionSelectionPanel.setPreferredSize(new java.awt.Dimension(200, 323));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGraphDefinitionSelectionPanel;
}
/**
 * Return the GraphTypeRadioGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getGraphTypeButtonGroup() {
	if (ivjGraphTypeButtonGroup == null) {
		try {
			ivjGraphTypeButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGraphTypeButtonGroup;
}
/**
 * Return the GraphTypePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGraphTypePanel() {
	if (ivjGraphTypePanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Graph Type");
			ivjGraphTypePanel = new javax.swing.JPanel();
			ivjGraphTypePanel.setName("GraphTypePanel");
			ivjGraphTypePanel.setBorder(ivjLocalBorder);
			ivjGraphTypePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLineRadioButton = new java.awt.GridBagConstraints();
			constraintsLineRadioButton.gridx = 0; constraintsLineRadioButton.gridy = 0;
			constraintsLineRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLineRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLineRadioButton.weightx = 1.0;
			constraintsLineRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getGraphTypePanel().add(getLineRadioButton(), constraintsLineRadioButton);

			java.awt.GridBagConstraints constraintsStepLineRadioButton = new java.awt.GridBagConstraints();
			constraintsStepLineRadioButton.gridx = 0; constraintsStepLineRadioButton.gridy = 1;
			constraintsStepLineRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStepLineRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStepLineRadioButton.weightx = 1.0;
			constraintsStepLineRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getGraphTypePanel().add(getStepLineRadioButton(), constraintsStepLineRadioButton);

			java.awt.GridBagConstraints constraintsBarRadioButton = new java.awt.GridBagConstraints();
			constraintsBarRadioButton.gridx = 0; constraintsBarRadioButton.gridy = 2;
			constraintsBarRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBarRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBarRadioButton.weightx = 1.0;
			constraintsBarRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getGraphTypePanel().add(getBarRadioButton(), constraintsBarRadioButton);

			java.awt.GridBagConstraints constraintsThreeDBarRadioButton = new java.awt.GridBagConstraints();
			constraintsThreeDBarRadioButton.gridx = 0; constraintsThreeDBarRadioButton.gridy = 3;
			constraintsThreeDBarRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsThreeDBarRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsThreeDBarRadioButton.weightx = 1.0;
			constraintsThreeDBarRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getGraphTypePanel().add(getThreeDBarRadioButton(), constraintsThreeDBarRadioButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGraphTypePanel;
}
/**
 * Return the LineRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getLineRadioButton() {
	if (ivjLineRadioButton == null) {
		try {
			ivjLineRadioButton = new javax.swing.JRadioButton();
			ivjLineRadioButton.setName("LineRadioButton");
			ivjLineRadioButton.setSelected(true);
			ivjLineRadioButton.setText("Line");
			ivjLineRadioButton.setActionCommand("LineGraphRadioButton");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLineRadioButton;
}
/**
 * Return the Previous2DaysRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getPrevious2DaysRadioButton() {
	if (ivjPrevious2DaysRadioButton == null) {
		try {
			ivjPrevious2DaysRadioButton = new javax.swing.JRadioButton();
			ivjPrevious2DaysRadioButton.setName("Previous2DaysRadioButton");
			ivjPrevious2DaysRadioButton.setText("Previous 2 Days");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrevious2DaysRadioButton;
}
/**
 * Return the Previous3DaysRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getPrevious3DaysRadioButton() {
	if (ivjPrevious3DaysRadioButton == null) {
		try {
			ivjPrevious3DaysRadioButton = new javax.swing.JRadioButton();
			ivjPrevious3DaysRadioButton.setName("Previous3DaysRadioButton");
			ivjPrevious3DaysRadioButton.setText("Previous 3 Days");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrevious3DaysRadioButton;
}
/**
 * Return the Previous7DaysRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getPrevious7DaysRadioButton() {
	if (ivjPrevious7DaysRadioButton == null) {
		try {
			ivjPrevious7DaysRadioButton = new javax.swing.JRadioButton();
			ivjPrevious7DaysRadioButton.setName("Previous7DaysRadioButton");
			ivjPrevious7DaysRadioButton.setSelected(false);
			ivjPrevious7DaysRadioButton.setText("Previous 7 Days");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrevious7DaysRadioButton;
}
/**
 * Return the RightParentPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRightParentPanel() {
	if (ivjRightParentPanel == null) {
		try {
			ivjRightParentPanel = new javax.swing.JPanel();
			ivjRightParentPanel.setName("RightParentPanel");
			ivjRightParentPanel.setLayout(new java.awt.BorderLayout());
			getRightParentPanel().add(getEditorGraphSettingsPanel(), "North");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightParentPanel;
}
/**
 * Return the StepLineRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getStepLineRadioButton() {
	if (ivjStepLineRadioButton == null) {
		try {
			ivjStepLineRadioButton = new javax.swing.JRadioButton();
			ivjStepLineRadioButton.setName("StepLineRadioButton");
			ivjStepLineRadioButton.setText("Step Line");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStepLineRadioButton;
}
/**
 * Return the ThreeDBarRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getThreeDBarRadioButton() {
	if (ivjThreeDBarRadioButton == null) {
		try {
			ivjThreeDBarRadioButton = new javax.swing.JRadioButton();
			ivjThreeDBarRadioButton.setName("ThreeDBarRadioButton");
			ivjThreeDBarRadioButton.setText("3D Bar");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjThreeDBarRadioButton;
}
/**
 * Return the TodayRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getTodayRadioButton() {
	if (ivjTodayRadioButton == null) {
		try {
			ivjTodayRadioButton = new javax.swing.JRadioButton();
			ivjTodayRadioButton.setName("TodayRadioButton");
			ivjTodayRadioButton.setSelected(true);
			ivjTodayRadioButton.setText("Today");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTodayRadioButton;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	DynamicGraphElement graph = getDynamicGraphElement();
	graph.setGraphDefinition(getGraphDefinitionSelectionPanel().getSelectedGraphDefinition());
	
	int trendType = TrendModelType.LINE_VIEW;
	if( getLineRadioButton().isSelected() ) {
		trendType = TrendModelType.LINE_VIEW;
	}
	else
	if( getBarRadioButton().isSelected() ) {
		trendType = TrendModelType.BAR_VIEW;
	}
	else
	if( getStepLineRadioButton().isSelected() ) {
		trendType = TrendModelType.STEP_VIEW;
	}
	else
	if( getThreeDBarRadioButton().isSelected() ) {
		trendType = TrendModelType.BAR_3D_VIEW;
	}
	
	graph.setTrendType(trendType);
	
	String displayRange = ServletUtil.TODAY;	
	
	if( getTodayRadioButton().isSelected() ) {
		displayRange = ServletUtil.TODAY;
	}
	else
	if( getYesterdayRadioButton().isSelected() ) {
		displayRange = ServletUtil.YESTERDAY;
	}
	else
	if( getPrevious2DaysRadioButton().isSelected() ) {
		displayRange = ServletUtil.PREVTWODAYS;
	}
	else
	if( getPrevious3DaysRadioButton().isSelected() ) {
		displayRange = ServletUtil.PREVTHREEDAYS;
	}
	else
	if( getPrevious7DaysRadioButton().isSelected() ) {
		displayRange = ServletUtil.PREVSEVENDAYS;
	}
	
	graph.setDisplayPeriod(displayRange);
	graph.setDirty(true);
	return graph;
}
/**
 * Return the YesterdayRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getYesterdayRadioButton() {
	if (ivjYesterdayRadioButton == null) {
		try {
			ivjYesterdayRadioButton = new javax.swing.JRadioButton();
			ivjYesterdayRadioButton.setName("YesterdayRadioButton");
			ivjYesterdayRadioButton.setText("Yesterday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYesterdayRadioButton;
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
		setName("DynamicGraphElementEditorPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(820, 699);
		add(getEditorSplitPane(), "Center");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getGraphTypeButtonGroup().add(getLineRadioButton());
	getGraphTypeButtonGroup().add(getStepLineRadioButton());
	getGraphTypeButtonGroup().add(getBarRadioButton());
	getGraphTypeButtonGroup().add(getThreeDBarRadioButton());
	
	getDisplayRangeButtonGroup().add(getTodayRadioButton());
	getDisplayRangeButtonGroup().add(getYesterdayRadioButton());
	getDisplayRangeButtonGroup().add(getPrevious2DaysRadioButton());
	getDisplayRangeButtonGroup().add(getPrevious3DaysRadioButton());
	getDisplayRangeButtonGroup().add(getPrevious7DaysRadioButton());
	
	getGraphDefinitionSelectionPanel().getIvjGraphDefinitionJTree().addTreeSelectionListener(this);
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2002 12:34:20 PM)
 * @return boolean
 */
public boolean isInputValid() {
	return (getGraphDefinitionSelectionPanel().getSelectedGraphDefinition() != null);
}
/** 
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DynamicGraphElementEditorPanel aDynamicGraphElementEditorPanel;
		aDynamicGraphElementEditorPanel = new DynamicGraphElementEditorPanel();
		frame.setContentPane(aDynamicGraphElementEditorPanel);
		frame.setSize(aDynamicGraphElementEditorPanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
	/**
	 * Sets the dynamicGraphElement.
	 * @param dynamicGraphElement The dynamicGraphElement to set
	 */
	public void setDynamicGraphElement(DynamicGraphElement dynamicGraphElement) {
		this.dynamicGraphElement = dynamicGraphElement;
	}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {
	DynamicGraphElement graph = (DynamicGraphElement) o;
	setDynamicGraphElement(graph);
	
	getGraphDefinitionSelectionPanel().selectGraphDefinition(graph.getGraphDefinition());
	
	int trendType = graph.getTrendType();
	String displayPeriod = graph.getDisplayPeriod();
	
	switch(trendType) {
		
		case TrendModelType.BAR_VIEW:
			getBarRadioButton().setSelected(true);
		break;
		
		case TrendModelType.STEP_VIEW:
			getStepLineRadioButton().setSelected(true);
		break;
		
		case TrendModelType.BAR_3D_VIEW:
			getThreeDBarRadioButton().setSelected(true);
		break;
		
		default:
		case TrendModelType.LINE_VIEW:
			getLineRadioButton().setSelected(true);
		break;
		
	}
	
	if(displayPeriod.equals(ServletUtil.YESTERDAY)) {
		getYesterdayRadioButton().setSelected(true);
	}
	else
	if(displayPeriod.equals(ServletUtil.PREVTWODAYS)) {
		getPrevious2DaysRadioButton().setSelected(true);
	}
	else
	if(displayPeriod.equals(ServletUtil.PREVTHREEDAYS)) {
		getPrevious3DaysRadioButton().setSelected(true);
	}
	else
	if(displayPeriod.equals(ServletUtil.PREVSEVENDAYS)) {
		getPrevious7DaysRadioButton().setSelected(true);
	}
	else {
		getTodayRadioButton().setSelected(true);
	}
}
	/** 
	  * Called whenever the value of the selection changes.
	  * @param e the event that characterizes the change.
	  */
public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
	fireInputUpdate();
}
}
