package com.cannontech.dbeditor.offsets;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointOffset;
import com.cannontech.database.data.point.PointTypes;

/**
 * Insert the type's description here.
 * Creation date: (10/24/2003 9:37:54 AM)
 * @author: 
 */
public class PointOffsetLegend extends DataInputPanel//javax.swing.JPanel 
{
	private javax.swing.JPanel ivjAccumPage = null;
	private javax.swing.JPanel ivjAnalogPage = null;
	private javax.swing.JPanel ivjDemandAccumPage = null;
	private javax.swing.JTabbedPane ivjJTabbedPaneOffsets = null;
	private javax.swing.JPanel ivjStatusPage = null;
	private javax.swing.JLabel ivjJLabelTitle = null;
	private javax.swing.JTextPane ivjJTextPaneStatus = null;
	private javax.swing.JScrollPane ivjJScrollPaneAccum = null;
	private javax.swing.JScrollPane ivjJScrollPaneAnalog = null;
	private javax.swing.JScrollPane ivjJScrollPaneDmdAccum = null;
	private javax.swing.JScrollPane ivjJScrollPaneStatus = null;
	private javax.swing.JTextPane ivjJTextPaneAccum = null;
	private javax.swing.JTextPane ivjJTextPaneAnalog = null;
	private javax.swing.JTextPane ivjJTextPaneDmdAccum = null;


	private javax.swing.JPanel ivjControlPage = null;
	private javax.swing.JScrollPane ivjJScrollPaneControl = null;
	private javax.swing.JTextPane ivjJTextPaneControl = null;

	private static final java.text.DecimalFormat NF =
		new java.text.DecimalFormat("000000");

/**
 * PointOffsetLegend constructor comment.
 */
public PointOffsetLegend() {
	super();
	initialize();
}

// complete the needed methods with NO-OPS
public Object getValue( Object o ) { return o; }
public void setValue( Object o ) {}

/**
 * Return the AccumPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAccumPage() {
	if (ivjAccumPage == null) {
		try {
			ivjAccumPage = new javax.swing.JPanel();
			ivjAccumPage.setName("AccumPage");
			ivjAccumPage.setLayout(new java.awt.BorderLayout());
			getAccumPage().add(getJScrollPaneAccum(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAccumPage;
}

/**
 * Return the AccumPage property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getControlPage() 
{
	if (ivjControlPage == null) 
	{
		try {
			ivjControlPage = new javax.swing.JPanel();
			ivjControlPage.setName("ControlPage");
			ivjControlPage.setLayout(new java.awt.BorderLayout());
			getControlPage().add(getJScrollPaneControl(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlPage;
}

/**
 * Return the AnalogPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAnalogPage() {
	if (ivjAnalogPage == null) {
		try {
			ivjAnalogPage = new javax.swing.JPanel();
			ivjAnalogPage.setName("AnalogPage");
			ivjAnalogPage.setLayout(new java.awt.BorderLayout());
			getAnalogPage().add(getJScrollPaneAnalog(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnalogPage;
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCDD5D8AFGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4D557152435A953B893BBB5935A31935AE817E9E9C2DBBAF5D2BBC35B24E39A1B312BA4F1B52EA919BAD94E2AEDE9E39B5A5A6985A402022252C665E11E729190112FA27F878F84C4C5C1C4031F876F82AF3EDF5E3B88DACBE66FF36EBE6FF2F96F41F362DAE52D7D6EBD7B6CF376676CF34EBE1F0B147DF6E4EAD2F236A4A525CA6A5F09A4C9CAB8A3C90DCFBC7C9061925E1ECE17B4FF8781F6
	C92303DBB0E11CCECFF3CC27E7C95B1A52A0198D692C3169743760FD0F7414A92183117E792C023C77ACF9BF884EA72E0278ECE5690C4399705C813097F08DG6148D34ABF3CD7455F00748E759DA1A9D912728FA24DBFD76D5560D72BF4D98779B31A51B49FB62879F6C8A34D82F8C69C745549057CC8DD2D1A9E7A78F2E953F70712B3111726FE02C4352C6190FF9A25754C90A9D2535489F8B6745E9CEB9AF1B94AC6ACCE274B2948A363E556E159EAD3DC1EB2576828D7D63CF5F53DAE1BD3B947D2BD72186C
	34CA8B8F213C6B2A52D8DD12548FE9AE5B77B39BD45E0BEFAE17629FBF97D036B52ACC1BB432AD2EDB1632B48F6B9EE73646F255103683F01E5107096786400D67A36E6715C8EB0477DA005EFC122571B049B278672FC951FD5FC95999C01628FCAEC3DC3ECF1634234BD27631F63F415AAE2BE0BA3D9A2093408E3090A08AA00779DBCEE589BCCD43CA1B4B61F0B9CDEEF72B5D6AB963324AF6F82F2B039C95F7C19EF1F92C1244FD6A552AD2BF7290BCD3B0531FFCBEF0C9A29F083A702414700B17D2D27D6896
	7362356435EA9D9B8BFC3AF13DB6D723DE99055369C5GADG03GD3G91052417DCD9B517DEDD60B79E27455EB7B16C36F81456891B5DAAFBD47D1C070369678FF8393BA11763F6D9DE25354B3A2F04A5EF75E317F54B656418C2EE0F5505BE3BF05EDF8D48FBE3616C76E07D78B07A61C8957A21846FC92A0FF23C03617BB5F86E633FE2FE0BE9AB642DBACCFEBB3803F4685972D9A92B62813FBAC4FC6BD6F2E9911747DC24EF5B3547F1ACF183FEA140D20015G6BG9281B20A29FD23AB0F4D53EFFBB5ED4B
	4B5A2A7E84CFF9CA71D83A1CDE45629C1187AC9E1BE558AEFBA51546D305328F4FA8B7497A538870B8A5BBE50FC531391CD03B45A103A3F9B9CDD855AE247148AC375F6610010143ADD13D61D5FB60E937F8150BEE2BC511FD94BCFFD1955A7216E5GEA048A60FD2898ED1E48462CD2F86F846036CA2DC5DCA424D7883BEC2F5E8D4F536058323D2E4EEC9BC1B1AC1E5B44BF3586479279166CD44C329B46C4F8712AF999B569BE3B0C3A044C3B999E7BC89B886E95783DB53BB89E6A50DA19720DAC5F22A89E5B
	7004A277490AE2F30E794A97B1D93CB33A1FD7D27BF8AD7A58EA50EBBD40A6215F17EA76E8746BF139EECE387B3165D4BB2DE663ECDA49F43A310466BFDB954E07669272531F8D121F1AB33FA8153E783EDFBFC53016F07F3CDB226FEBDED6270F476FAA87C3687366927F7DB1785812DD958A0F1490E891385DBFD607F6DF8A7245824417105DB75698F9FFEB35F8E56AE167BC50E2325567E51C763F0E8776317FB96E5F47FEFE5C3F0F7D6AF8F09F7A7DF1FFBE4473361C403945B5695478E47A095D2C8FBB1D
	B293DA9411F8FC7133FE34054DE9D3AE592CE33252E73BA3C3BB9FC15F5AD90D3E1588B649B8C23EB419C73E14FF6789A9767A15E4FF73D944325B4956A33C4D270E78FC094B340D793AEC179DE028B65704D36178AC563FD53CBA3EA41F74CF1BF6927D7126FCDB254BB87926046308FDC2963AC5DB36230DBDB687B495F6D8B5467B71E1ED0C978172E1BC38921EEBGF8596EEEEC07599195794D376BD12E8DC03F99A03B147C6671137BB47DD5F518B6175D4E4DAF5AE5D1DD9636237C5E048C43B1F66853C2
	17C76BF66B7286D43DF87EE3F52817DDB396141622DE055594F361DC41F10D8C77BD7879C5AA4D3DE73234F16972EDAF7B0DC9905C253C6D96146A672D44862CF7A960E3046E7B6B35E315496BF50D5858342036476B45E8F7964B3EEB39E519AA73CE02EF5755F577DA20840F9B4F94E13DC83F2A14624631A3EC3EBEBA1D3E97207CA871FB2EFE363F3CD03F8D1E23B60F97629D2BBCC5B60B2BC75DF19ED251941FD5EAED919BBF6A571F91BA0FF29B8C9D555B22092DEB26G3FD048F62EE117469690CB4234
	BB86ADA57AF1C38A77DFB51D5418CA7EAB5178F721C89F5FC49E750D23B1C7E70F29BC0EF90559EB55D19E47248A7CEB8C3FCE0367710D0361B11DGF97DE5B45FD6B723DEABA03D2A0C747A0746CC0DDE2CFF881D5A1A308F3ABC36B11B13F01D8C8711B49B14781CE5E3ED3C966A5C8840F155A5084BBA86B16EB11AE3DEBD02389EC85B85EEA343AD0074F201739E19EB1E40724172FB0264F39E8B676411BAC7FE3079095B6AE4B3364B14C8B73274ED35AC44DAC5EFB29F3E6D1AG23D717235D9EAD2231EC
	9568908F1086F8C3B95707773BAFCD62F8B18A019BF437BE37BC8231AA16BDDDC2E56F827DA200E800B5G89G1CE77FD69C0B3AE1180165A676D3BE9AFAC9263F14E05D5DBD16E1595EEFD340437998FB20D4BB468ED67078E481BCA3GB6B07EAF9A32B5B451AACD9CBC39DF7EDB912DAD4BABE1AC282431396286464CE29CA933CCAAF5F527BCB6EB2BE52C07D9070FA5DB0B5354721115B4165494E31DEBA11DG10D6C5735AD4A12DC123BE77D929A7AC4C6FFAB83EFFAC59D8254672D53EF140GEBD04E73
	3D9262998679AB2A9077D809D6CF345BD760278C7B964B7F7E0CFC9CB70AA5EDFE4A91EDBE4A7B7896EDFE4D0C7CD4CDF92E5B7385245B666723A57BD71D81577AD2B557A9DFFD967D6643C93D2FADCB61F6BF1216AB799D3FFD5E81795C8E8325B4978DD5136D1F580736DF8C69A800E72A29BDDF9F8DB0AFB02F6275E564E2FD68EB6B2A294D7E52215D43D839F3ACF9FBG3D32EB38BE55B53EB60BACDD16427B76333346593900177B41BD164BA88E359C9F3F3716207D149ABEFEA789FC964307E9703C7F54
	97D37FD988F989B5B42E0F1A505E7D4727535DG61G11G31G6B0E53B87F2D66F2E90E75E63F4B5D83C12FDD3394627A5DAA3937BD01EFEE25760CF97A73520A874B8236E742F1F5DF6E84FF463E7161A4FCF27DFE530A7A35C05E90C098C084C09440B3A7C83FEDAD25DC3FF62BCDB1A90AE5E41CC591245C00CD1E7421F150643A3DD39ACC3735A7824D49397B31ADB61E60ED14AE70A58C1FD66B437336FB0A7595CC67C3DEF8AD4DF53BF2357CB756F2FE52C97E4CD61FC570ECD05F39CDC64CB8C674824E
	8E30GE089404A13E40F7E16B9770F5A27026C9FE562361A7A7EA74D3B29B814B5DC2840F9DC392FBC38FDDC2640EB3CDBA67FEB3C3BA67FEB3C8F19022F71BEE68A3C467B38C93B4663ED3B2C9D5BF69D34E5A200218EE2E4GF39D356D5B2D07F85B7A7173F6D8743539582E10C27C135B826B1A5EB677DE526EB60C4107DD0A62F2E07DE27F248D5BCBF139991263F25B50C690B20EAB1CD445673561F8EF17C7991A47081ED20A91EF54D1FF485B0F6B28E8C82F820887C894F965ACAF3BFEBA3D96E088A02C
	1E723E3A6FDEFD06F8A0BF649B58AFDF38E79ED89E6D4267C77FB45CA6C5ECDEDC8AFA4554D3AC7D09CC442D0274DA01CB4CA45B2D97386ACA44D5B7C04C5FC038F0069B07744286721F1B66407E60B17B777DA933FF5F1FB6875FC3FB509C3CEFFC31BD30ACB16DF37B66B75A591A91FC2A7F36DBD6673AF636EEE101AD517DFBFB1640F5F9FBDDDE9BAEE4639A50C68B2B4DC15AAEC553EE8811D0A686A0CFCD2F027791085F9ECB26987AD367313F74A996E5425BEB41CE4071FDB791EF02A157B52645FF738E
	5A56B4B2B26160E8957F3BEE340DD9F6D81CD6DFAE0F472D7B75FB9E3806B2B442D88140E9FE5A680F26937207844DCB957EE894488F97B4AF9F74C7938979B10D342EF876G6E5F0C3A1CCADA9316B94F7CF8B5642F95B41F4FC49A859A3427894F7D3E5D83C90937414E9DF335019696C1F904BC35ED16427B8F407EFF00BF4E6BF4A56EDD0C4362758E48E4311B6C36B1A70E149217CF2E2438BE3A0964FB2C72CFB34A74032464A912DA6EE9D1AE9E52EF33F210A751DFC233BF9C57F77BD9C9225868558C0A
	0D8CDFF8DCB23FDC161CA7790F0D321A7918F42359976B0677FECEDD1FAC59CF7BAB914D9417DFB0E02C3186529BG2496BA43ABE07B9AB8966DEDA15A0E96246D45B4C018205DD1C934910236241A558B698DG1209E8F79E92751A0876C8BE2B9753GE1G5C860EBEC97877C7B3F19EF34A13C6DA7615F500610B8ED9ED58C58E159A4BCB0D953545E43348C96D5ECE6DB6FB403D1D889337D5147ADCDC32A00957719F6FE86B487A54C0F2D200BA562BE593CCBE3B4FEEDBC962FA99AE7A743ADB0967581A25
	CFD99F7E52346CADEBEBBF5B5FFEC150FFFF5E74BCBE58B600FDA53FF5BA3D85408AF097E09140F20015GEBG5683EC8648EE1BCEAF82E8G6885388120G043751FC50FB9657178316F755A8311D1DC3330882106AC21CC96331707DA1BE9FFEE63FBEF6DE52661BBF235B82454E5FE17E79CC1B7E3CEE8D434735694F6B868DF4DE379E72BA4DB46717B46979C719FDBCE34DB37947C11A73F91A797C86331E7FB38C1FE856736FE9A17EE96D90EF37937FBD15FA7EEB5AFDBC57374F641F58CE7C7F0975230C
	8EBD7F658CEF685073AF2FA67E251037280378E79F52735F54616319F6EAA6FF43A9627F986B1B6527747C1FE07816D3FA7E9579443F9772E2CE513A53FD897B415E4E69746ACE7225AF779D8962CB3D9E17DB76A837B9166D022BBEFD93C3ABF52ECE6CAB88D487C4385D50CF4E8D3FAB0FA87D16B1D16640C060B822E0E06EB822EC40FF0CDBBDB0BB46BDB960BF46ED9E5046389CE79E40B1EA8C7A5020206B92384B82F716550938CBB62BB2AE707DCCAE44F74A481070D718DC138CB728622EEBF017D51CF5
	GE3A1D09926B5AAEB9BE0F3354DEE67E9872331B979BE209A87B299EDEE0BC66EC901C339F9FC39E2BF6D7D76F7D24CE9B4D0ECBA55C93E3A5A00BC170874CBAC3DD2242F99824556D866DEE26E59717BE4C09E211EB5860E5FCDEC0E0D8719B789FBBC59CC76585BC53897982E81523DDDE4035896344910C8FF17256F0A740516792EBF300EFB33514DF97338971BF15DCBD8EC9189B24688FBBCDAC5B6DAABF0DF2BA29BED96B6D8DA0DB6C96B267413AC1DAF52472BC3DD23E1DD215968DC48BCC23195577D
	880B29BAC13621EE32C7C49E59A8CC60D664110D168A9BAC4BC71BC40B74D3AC3DC624CF648F2A797FDF1F8FEC23DE46630368D77E000FC7931770DD2CAD79F4C2FBB517F08F854F41A116345DE14716FDC0689C6367AC2F0A7553A630C346E91843GBACF2B3176E924B9E1589BC4869E3B1DA94675EDF75F0847E537630EB9DF76C9F47FF0EB2E360E65D0AF6EDF4441F343E9DAF78CB64DBE87F2B5D1B929074B37971E25BDDA791EED1EDB3E7FE6630CE299E607B9BCD6FD9E07EFB57FDF8BF572736529107F
	F7ADFA79F16F458CF28C76107C264A59728FD6D2B937AAFFB8BC174C107F492AE072F3D9FFFB45A76BEF37E469E445C5B11773C755FAB9F1FFA886782D92F2369E1AAD6765C3D4AEDE15B3911FE734F2BE15B7371C35C3BEB94F756C19A9275941576F2ACD6373753262BED6BE702CBDC3E7E569B934575F8B3870B3F43672DA997A376E2C2C5505779278D119FADEF3BE07562931E7E8CD76BE2BB39152D9E7E1CDF0164EDDD6DD551EA7990F1C74FB1E14F8551A5CF2165B45FA5637A6D2E0AD4AE59BEB8FF01E
	643304C467C94D39F41E94F116647BDCAEDBDFC2FA8300F40EEC71E89359E2AF60FA4F513A73C7CD24E378B92AA33399E9E3A13D96A0C150D61908DFDAAF51BEE2E271AE2487815C3D44AF2C12780583AE36176AD8A5567E09220EDFB35A7273B0F6G5CB8CF34EF55903F2973C45B4C6E49C4C3FA95C07CF9627740A162178838728BD447F307085F78852A638DC69B896995GEB846D5E82F1BFD7505EA9E077F37B402FGFA7BE87C5F19F3EF67DC3977D8EEA064F2FCBD72BA735771BE7D7958E606D77A7467
	E39F91FDA68C72567751BA6552D56CEF1D7D30D686888708840881D85DCF6B163BC3C5D260F58B6FDA3EA5B0574B1C9BCC2F787EC06B5F7D87D04ECD7D7A75D7BE43EF3928DFFFC565527AAB9B7226AED2BBFEFA567AF7FE6D7105FBAC37BE64F2DC2F87D81FDCF7D15F0E9FE3788D97756D3827195AF193648D8E103E3526E07CD78E8432F7FAAB9BD78674765E43707183FAFB7F23096C1DGF97D17087F0333567B73335B62FBAC773D104BF13D5EE5771ED7DC525B5B4B704FDC525BFB33986F56C0DE75A069
	EB288946BFE2B0103D3F42463928C13D3D3F4170AB877576EE2FA5FB2F063C724B447F233376B766E737C76E315C8BA11763FA5D4CE377A0AE6B6D2DB0FC64E53D3DFFAF467BA8484B3AC27A669684632FDC89E46F1F31F1BE7C0A5E5E098C9FF1C5EF6F30823277F2484BF807787F8607C2559649865F7BC3B9777F4CF769FD654147789BD7836F910DDD1DFB0F48FD557F9E512D2B3377087EFC557F9E5183D7E71F033E13CB6BBA37E88BA3BBCFD9A252A5AC3DC2243F1C7BC15A8B794CEE1367BFD09E7A3520
	5A471BFDFE36C8733E2F797E7BDC65FD65415B761CC96CEB08365B590AED39E508527BD85AA8521FB105AA9756951A6ECF074CA31435BC67612E723567ED4DFBF2C85F815CDB5B665CD79E3CED2BEA28EDDBC45B7DCBAD3625DD243F4D5261A2FD3AB694396CA1342BCFF7EBC8BCC25D2361BC5E4B7335679D4DFBCA5E7DEFDB43FD65215E25A82036DDAE5AEED38136E51CC87FAFCBEF9269878B34F2450558F673537D3199BCBE687D35222B6CDECE2EB188CD76D52E975F6B585D02F3EAB7BBAA776DFE717B40
	6F9C505EE3F5C3B95C6F0F00A767B3EA1ABF1F3FAB1B41C75D2562FC3ADB75FCF04FBCCA70692E19BF1F8F656E184107ED5B28FCB6556A79603EF32C60F33AE07EFC920F6E527291BBC4446BD8211E976E5F46DD253D2C07F3F1AF2BCD46EFC069FCFCD2565EA9B03CDD120CBC7C1DEDEFD0E5493E665B4741A75F47F9774FB37618B47702D5E955BD1C3FBFC0F78FBA2F51BE44832D048B93380E1304DBA1F0230504CB38C66B74B72E217D0A2ECF271B81468142G9683C48144822C3DCE6B75DD57022D577906
	18FE3D7E6881EDFC3D697A4C7E19F6E3F6FFD57BF52B361CC1C7D79E305C69135AF2E69D5DE040F2B78B35656CBA3AB0BF6554FBFE57035C733BBE0FFBFE57835C733B6E671E5F75G777C2E4FBE83ADD26B45B6B9EBF108FA4B2E63BEA35F0FBDE3F1DA46583DB1754C6CG5D03DBFC034EBA3A6B4518ACF01FE67E369A526B846E1365C417A0F0434C8733AD50E7AD047B141168EA856EC34CAF07A0ED9738089CF16E82B8FE8F6895AE9F24238178779716FA5A57DD8F38C4003D43D0B74060B0755B18561977
	243E4B641F027C8543D4CFF4B9553382F02B81B682E40CCC27170ED0BD37CF4E2C67E126DF8F648F0ED0BDDFB2D2BD8B819789908B908FA0D9291ECF964E2C67A9263F9172CB2DD44FD3B9627FACGCE81D88C908390E725FBBD1D4312985B9A6B8BA53F77FA9CAE274B6B360C48D675EECFE5B16D87FF341E4603CDD67A4E2220D17F1DC5450C6FACAEB56A3F3340314E177FB70D7A6FAC9E1E711D45579B755FD9D0F92ED34A18CF270565216B74D2B969D4AA13CEBBAA74BA7DE4064CAD9541F572D468F52ACF
	5266AF2D882E539D1BCF273FB5062E534BC65249AAF45AF9B038CE260341F5729E8C2E53320341F5FA6926CF2707F2C25769459C52E90950A95390DC27E6C3F01D5EB3845769F1C3A01D789C704B866D9C9087F2ED100347133C5CAB655AF21B21CC5668FC4A2DB3EA4B65C3195AF9157B610C3D7D9EA8F3E30EF2FCCCCDEFE46712C00BFD1263F6B05CC240E1BF6738DF16939D5A0F635EA8A73A1F087ADEB74E26CBB44E267BC54EEC3ADF67E869F88CE4A944BDB2757C18DD4961F843D4268FCFD7F2A47E3FEF
	5E3F7893A00A7A29142AC87B592714C67EF55D3133930EE159E3645F9CFBBD52CE611CDD6C56CD7C2D2A3A149FBF1E12ACED69125E64B3F01734852751AEA9154D32DDD28A4C40D3EE559B1355679628ADF3062BBF7B191A57D67F6700B7C5FA33CB5A07A3FB1F6CE107603255ECD168BFB3F4C869EE9491BF9087D6DE76C875AA376DF20F14BDFA813FD676AA3255C4476BDEC57AE38FF2BAB621586C47BAAD5EF18543077A9EE90B937F0704341BE75BAD4E31E37C2C3D1E491BE4DA049DD442AF8FA510F78F13
	97EBA993356C797A4E02035D6BD724C8499D52B63B4BE26D300CC0C457A525AB639E59BB6E32DB275C4DAD2C2234C97AF8AC852A4B916A2332E598248A21EFCEFDFAE166064C8EB05714C7BA46A87182F2192030360DDBBC40CA76F4A978BFB1DC1EFC117740812570FB275E9ACA1612D4A55F13262554A93782D2F9ACCE6F284B63G890CBA89A8CBD4D47609674A9FF96237D7D8C53E220A74B6B79994BDB6A63B9C47CC2323B627ACAAG7BC1753922FA24A953516C1E7A789F3BDF7C43B03437D9CAF1141733
	5F8A76DB093F958CD341B09515E04484A9681FBEFF4A0BDA27AB5CC90477E2B317F5C0CBA9BF7C69F31FFD25A1E1A3E857A12532EF1E4158D9BEAA6E7E8FC6BD123BFDF17CC35896EEF4892A774DD10B43E63F8DAFE6F671F78A717BB8DB5A39A6FFFF93287862CF113633A553049D9B8D3C0FDF14B7EA102C069466015CF158F7678C3BF536ABA1FABB4B4CE43FD9A19B8ACB6DE6E53311A13E3C25C85B4E082907D164CD7DF54AD57CD7AA57313FD239EA2654F5F7717B5ABB65099FB99370DEFA0FC33DD76250
	7F4F012B17D17EA500DF6120B56E97870FCA016F4176B9ACF63BE2315FB4B92DF8A7367536A27B3E2B993E0C75FB09C64DD46FFFDEC6395F551479BFD0CB87881779587B9299GG98CEGGD0CB818294G94G88G88GCDD5D8AF1779587B9299GG98CEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCC99GGGG
**end of data**/
}

/**
 * Return the DemandAccumPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDemandAccumPage() {
	if (ivjDemandAccumPage == null) {
		try {
			ivjDemandAccumPage = new javax.swing.JPanel();
			ivjDemandAccumPage.setName("DemandAccumPage");
			ivjDemandAccumPage.setLayout(new java.awt.BorderLayout());
			getDemandAccumPage().add(getJScrollPaneDmdAccum(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandAccumPage;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setFont(new java.awt.Font("dialog", 0, 18));
			ivjJLabelTitle.setText("Point Offset/Number Legend");
			ivjJLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabelTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTitle;
}

/**
 * Return the JScrollPaneAccum property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAccum() {
	if (ivjJScrollPaneAccum == null) {
		try {
			ivjJScrollPaneAccum = new javax.swing.JScrollPane();
			ivjJScrollPaneAccum.setName("JScrollPaneAccum");
			getJScrollPaneAccum().setViewportView(getJTextPaneAccum());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAccum;
}


/**
 * Return the JScrollPaneAnalog property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAnalog() {
	if (ivjJScrollPaneAnalog == null) {
		try {
			ivjJScrollPaneAnalog = new javax.swing.JScrollPane();
			ivjJScrollPaneAnalog.setName("JScrollPaneAnalog");
			getJScrollPaneAnalog().setViewportView(getJTextPaneAnalog());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAnalog;
}


/**
 * Return the JScrollPaneDmdAccum property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneDmdAccum() {
	if (ivjJScrollPaneDmdAccum == null) {
		try {
			ivjJScrollPaneDmdAccum = new javax.swing.JScrollPane();
			ivjJScrollPaneDmdAccum.setName("JScrollPaneDmdAccum");
			getJScrollPaneDmdAccum().setViewportView(getJTextPaneDmdAccum());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneDmdAccum;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPaneControl() 
{
	if (ivjJScrollPaneControl == null) 
	{
		try {
			ivjJScrollPaneControl = new javax.swing.JScrollPane();
			ivjJScrollPaneControl.setName("JScrollPaneControl");
			getJScrollPaneControl().setViewportView(getJTextPaneControl());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneControl;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JScrollPane getJScrollPaneStatus() {
	if (ivjJScrollPaneStatus == null) {
		try {
			ivjJScrollPaneStatus = new javax.swing.JScrollPane();
			ivjJScrollPaneStatus.setName("JScrollPaneStatus");
			getJScrollPaneStatus().setViewportView(getJTextPaneStatus());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneStatus;
}

/**
 * Return the JTabbedPaneOffsets property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPaneOffsets() {
	if (ivjJTabbedPaneOffsets == null) {
		try {
			ivjJTabbedPaneOffsets = new javax.swing.JTabbedPane();
			ivjJTabbedPaneOffsets.setName("JTabbedPaneOffsets");
			ivjJTabbedPaneOffsets.insertTab("StatusPage", null, getStatusPage(), null, 0);
			ivjJTabbedPaneOffsets.insertTab("Analog", null, getAnalogPage(), null, 1);
			ivjJTabbedPaneOffsets.insertTab("Accumulator", null, getAccumPage(), null, 2);
			ivjJTabbedPaneOffsets.insertTab("Demand Accumulator", null, getDemandAccumPage(), null, 3);
			// user code begin {1}
			
			ivjJTabbedPaneOffsets.insertTab("Control", null, getControlPage(), null, 4);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPaneOffsets;
}

/**
 * Return the JTextPaneAccum property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneAccum() {
	if (ivjJTextPaneAccum == null) {
		try {
			ivjJTextPaneAccum = new javax.swing.JTextPane();
			ivjJTextPaneAccum.setName("JTextPaneAccum");
			ivjJTextPaneAccum.setFont(new java.awt.Font("monospaced", 0, 12));
			ivjJTextPaneAccum.setBounds(0, 0, 157, 117);
			// user code begin {1}
			
			ivjJTextPaneAccum.setEditable( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneAccum;
}


/**
 * Return the JTextPaneAnalog property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneAnalog() {
	if (ivjJTextPaneAnalog == null) {
		try {
			ivjJTextPaneAnalog = new javax.swing.JTextPane();
			ivjJTextPaneAnalog.setName("JTextPaneAnalog");
			ivjJTextPaneAnalog.setFont(new java.awt.Font("monospaced", 0, 12));
			ivjJTextPaneAnalog.setBounds(0, 0, 157, 117);
			// user code begin {1}
			
			ivjJTextPaneAnalog.setEditable( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneAnalog;
}


/**
 * Return the JTextPaneDmdAccum property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneDmdAccum() {
	if (ivjJTextPaneDmdAccum == null) {
		try {
			ivjJTextPaneDmdAccum = new javax.swing.JTextPane();
			ivjJTextPaneDmdAccum.setName("JTextPaneDmdAccum");
			ivjJTextPaneDmdAccum.setFont(new java.awt.Font("monospaced", 0, 12));
			ivjJTextPaneDmdAccum.setBounds(0, 0, 157, 117);
			// user code begin {1}
			
			ivjJTextPaneDmdAccum.setEditable( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneDmdAccum;
}

/**
 * Return the JTextPaneDmdAccum property value.
 * @return javax.swing.JTextPane
 */
private javax.swing.JTextPane getJTextPaneControl() 
{
	if (ivjJTextPaneControl == null) {
		try {
			ivjJTextPaneControl = new javax.swing.JTextPane();
			ivjJTextPaneControl.setName("JTextPaneControl");
			ivjJTextPaneControl.setFont(new java.awt.Font("monospaced", 0, 12));
			ivjJTextPaneControl.setBounds(0, 0, 157, 117);
			// user code begin {1}
			
			ivjJTextPaneControl.setEditable( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneControl;
}


/**
 * Return the JTextPane1 property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneStatus() {
	if (ivjJTextPaneStatus == null) {
		try {
			ivjJTextPaneStatus = new javax.swing.JTextPane();
			ivjJTextPaneStatus.setName("JTextPaneStatus");
			ivjJTextPaneStatus.setFont(new java.awt.Font("monospaced", 0, 12));
			ivjJTextPaneStatus.setBounds(0, 0, 583, 408);
			// user code begin {1}

			ivjJTextPaneStatus.setEditable( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneStatus;
}

/**
 * Return the StatusPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getStatusPage() {
	if (ivjStatusPage == null) {
		try {
			ivjStatusPage = new javax.swing.JPanel();
			ivjStatusPage.setName("StatusPage");
			ivjStatusPage.setLayout(new java.awt.BorderLayout());
			getStatusPage().add(getJScrollPaneStatus(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusPage;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointOffsetLegend");
		setLayout(new java.awt.GridBagLayout());
		setSize(593, 473);

		java.awt.GridBagConstraints constraintsJTabbedPaneOffsets = new java.awt.GridBagConstraints();
		constraintsJTabbedPaneOffsets.gridx = 1; constraintsJTabbedPaneOffsets.gridy = 2;
		constraintsJTabbedPaneOffsets.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJTabbedPaneOffsets.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTabbedPaneOffsets.weightx = 1.0;
		constraintsJTabbedPaneOffsets.weighty = 1.0;
		constraintsJTabbedPaneOffsets.ipadx = 561;
		constraintsJTabbedPaneOffsets.ipady = 335;
		constraintsJTabbedPaneOffsets.insets = new java.awt.Insets(4, 2, 3, 3);
		add(getJTabbedPaneOffsets(), constraintsJTabbedPaneOffsets);

		java.awt.GridBagConstraints constraintsJLabelTitle = new java.awt.GridBagConstraints();
		constraintsJLabelTitle.gridx = 1; constraintsJLabelTitle.gridy = 1;
		constraintsJLabelTitle.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelTitle.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelTitle.ipadx = 361;
		constraintsJLabelTitle.ipady = -1;
		constraintsJLabelTitle.insets = new java.awt.Insets(6, 2, 3, 3);
		add(getJLabelTitle(), constraintsJLabelTitle);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	initPages();
	
	// user code end
}

private void addOffsetText( JTextPane txtPage_, PointOffset ptOffset_ )
{
	String txt = txtPage_.getText();
	
	txt +=
		"________" + PAOGroups.getPAOTypeString(ptOffset_.getPaoType()) + 
		"_______________________" + System.getProperty("line.separator");
	
	for( int i = 0; i < ptOffset_.getValues().length; i++ )
	{
		txt +=
			NF.format(ptOffset_.getValues()[i]) + " : " +
			ptOffset_.getDescriptions()[i] + System.getProperty("line.separator");
	}

	txt += System.getProperty("line.separator");

	txtPage_.setText( txt );	
}


private void initPages()
{
	for( int i = 0; i < PointOffset.ALL_POINT_OFFSETS.length; i++ )
	{
		if( PointOffset.ALL_POINT_OFFSETS[i].getPointType() == PointTypes.STATUS_POINT )
		{
			addOffsetText( getJTextPaneStatus(), PointOffset.ALL_POINT_OFFSETS[i] );
		}
		else if( PointOffset.ALL_POINT_OFFSETS[i].getPointType() == PointTypes.ANALOG_POINT )
		{
			addOffsetText( getJTextPaneAnalog(), PointOffset.ALL_POINT_OFFSETS[i] );
		}
		else if( PointOffset.ALL_POINT_OFFSETS[i].getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT )
		{
			addOffsetText( getJTextPaneDmdAccum(), PointOffset.ALL_POINT_OFFSETS[i] );
		}
		else if( PointOffset.ALL_POINT_OFFSETS[i].getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT )
		{
			addOffsetText( getJTextPaneAccum(), PointOffset.ALL_POINT_OFFSETS[i] );
		} //use this for control points
		else if( PointOffset.ALL_POINT_OFFSETS[i].getPointType() == PointTypes.CONTROLTYPE_NORMAL )
		{
			addOffsetText( getJTextPaneControl(), PointOffset.ALL_POINT_OFFSETS[i] );
		}		
		
	}
	

	//we need to scroll up the scrollbars in a different Thread
	SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			getJScrollPaneStatus().getVerticalScrollBar().setValue( 
				getJScrollPaneStatus().getVerticalScrollBar().getMinimum() );

			getJScrollPaneAccum().getVerticalScrollBar().setValue( 
				getJScrollPaneAccum().getVerticalScrollBar().getMinimum() );

			getJScrollPaneAnalog().getVerticalScrollBar().setValue( 
				getJScrollPaneAnalog().getVerticalScrollBar().getMinimum() );

			getJScrollPaneDmdAccum().getVerticalScrollBar().setValue( 
				getJScrollPaneDmdAccum().getVerticalScrollBar().getMinimum() );

			getJScrollPaneControl().getVerticalScrollBar().setValue( 
				getJScrollPaneControl().getVerticalScrollBar().getMinimum() );

		}
	});

}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PointOffsetLegend aPointOffsetLegend;
		aPointOffsetLegend = new PointOffsetLegend();
		frame.setContentPane(aPointOffsetLegend);
		frame.setSize(aPointOffsetLegend.getSize());
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