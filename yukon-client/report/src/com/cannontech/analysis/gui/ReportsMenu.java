package com.cannontech.analysis.gui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Insert the type's description here.
 * Creation date: (5/28/2004 10:14:16 AM)
 * @author: 
 */
public class ReportsMenu extends javax.swing.JMenu {
	private javax.swing.JMenu ivjAdminMenu = null;
	private javax.swing.JMenu ivjAMRMenu = null;
	private javax.swing.JMenu ivjCapControlMenu = null;
	private javax.swing.JMenu ivjDatabaseMenu = null;
	private javax.swing.JMenu ivjLoadManagementMenu = null;
	private javax.swing.JMenu ivjOtherMenu = null;
	private javax.swing.JMenu ivjStatisticsMenu = null;
	private javax.swing.JRadioButtonMenuItem ivjActivityLogMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjRouteMacroMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjSystemLogMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjCarrierMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjLMControlLogMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjLoadGroupAcctMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjLoadProfileMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjMissedMeterMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjPowerFailMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjSuccessMeterMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjMonthlyMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjPrevMonthMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjTodayMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjYesterdayMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjConnectedMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjHistoryMenuItem = null;
	private javax.swing.JSeparator ivjSeparator1 = null;
	private javax.swing.JRadioButtonMenuItem ivjCurrentStateMenuItem = null;
	private javax.swing.JRadioButtonMenuItem ivjDisconnectedMenuItem = null;
	
	private javax.swing.JRadioButtonMenuItem cbcCapBankMenuItem = null;

	private javax.swing.ButtonGroup reportButtonGroup = null;
/**
 * ReportsMenu constructor comment.
 */
public ReportsMenu() {
	super();
	initialize();
}
/**
 * ReportsMenu constructor comment.
 * @param s java.lang.String
 */
public ReportsMenu(String s) {
	super(s);
}
/**
 * ReportsMenu constructor comment.
 * @param s java.lang.String
 * @param b boolean
 */
public ReportsMenu(String s, boolean b) {
	super(s, b);
}
/**
 * Return the ActivityLogMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getActivityLogMenuItem() {
	if (ivjActivityLogMenuItem == null) {
		try {
			ivjActivityLogMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjActivityLogMenuItem.setName("ActivityLogMenuItem");
			ivjActivityLogMenuItem.setText("Activity Log");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjActivityLogMenuItem;
}
/**
 * Return the JMenu6 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getAdminMenu() {
	if (ivjAdminMenu == null) {
		try {
			ivjAdminMenu = new javax.swing.JMenu();
			ivjAdminMenu.setName("AdminMenu");
			ivjAdminMenu.setText("Admin");
			ivjAdminMenu.add(getActivityLogMenuItem());
			ivjAdminMenu.add(getSystemLogMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdminMenu;
}
/**
 * Return the JMenu5 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getAMRMenu() {
	if (ivjAMRMenu == null) {
		try {
			ivjAMRMenu = new javax.swing.JMenu();
			ivjAMRMenu.setName("AMRMenu");
			ivjAMRMenu.setText("AMR");
			ivjAMRMenu.add(getCurrentStateMenuItem());
			ivjAMRMenu.add(getHistoryMenuItem());
			ivjAMRMenu.add(getConnectedMenuItem());
			ivjAMRMenu.add(getDisconnectedMenuItem());
			ivjAMRMenu.add(getSeparator1());
			ivjAMRMenu.add(getMissedMeterMenuItem());
			ivjAMRMenu.add(getSuccessMeterMenuItem());
			ivjAMRMenu.add(getLoadProfileMenuItem());
			ivjAMRMenu.add(getPowerFailMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAMRMenu;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA6F347B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDC8DD4D55715F6F51154A62E163424518C195A1698E6C2D25BB109ED691A541844B44E4426A4F1D6B563E4C847B4CE63A45A10B5B6F3F97CEA709F95A3912A98C1109F850304A888A242C38920A092FD1777AE7074715E733D8B02224EBE675CFD6E65F29EAF49123A56DE771CEF6F3377BE7B7C3C7307D2FA567424F8CB3AA445A7C95A3FCF62A5A97224A4253C737D17916B5BE033C806FFFF855AA8
	F98FA442F788F86567EC168D12E5E7AA6427C0BE245DE65986694DD24A4EE5F51088E0E7AE7062E61CF10DEFE756DE3413C273ED0DE970DD87948A58ACD2E6A3316FEA4C507005002D53520462C1EB9415791D1AE140EBB7B239C9708D83AA5FC4E43E59182971D7GC6E452G4F5E246BDCCD3D3B2322DC4BF75DB3DD0A794D0B49EB09AD03FE24C9DA59886DABC91D49A4908952F3E57B61DB59FC3977085D53DFE8175DEE0FDBD56CFD0532DBF68D7B1D7E425E81E7C5C50B627578D4FF13629EA0615CCC7CFC
	CFCD25BAA4E991681DCE31AB83A4B69D107682CD86E23E7EF6ED2566CB5865695213BFFEBA202F311B190F29693A2F69F90F2C191DCE744F2431A7656AA1BF8908799257083E2CCAC7DF52D4A2DB3A45E63182B93620AF1F6DC5DFBA2E7FC4DA583DB2F9FDGDFC636E8F15B227B326043C756EC277D20AA0F34659260A5EE35D9B200AA013A00C600F6507E773FFB7741373ADB2D717477FB5C55DE6FB117435764F1A8AEC8D7D4GC743DA943B476700FECFF53F2F96887CC18A5BBA3AFF69FD22DD42BE9155B2
	C3EAD35F246DEC2EDB58CBB149111A0E682DFA5D523611382D9DA4F1DB84F8BC2D03A4B1BC0B624D5BF41C3549D55A66A4BF89F8199998670EF5680BB571FEE9F9713B49A982DF0666ACCB1E1D41FCD810217B42E27CE1BE0971AA40B3365BAC45C075C0FDC01337E30C0F679E8C9663135027FD5021DB873A3D32CFBDB660F4B9941F966B479B470B3508D83939DF399C0BCB64C1E39BC57F531F05F1090E7CFDF244F62D0D366BF1E1367F925076226DE37B46BA5E06136818D83E1D35E182476F223874210E33
	365DC4F1125FGBC9508354B6C82B2C7AD047C8A20349DB6CB8ED0AD108DC85D01ED931ADB74A57BFF3321DDD85913F9DB612B8C29BE79245B2F4AEE3BF2D676B965EE176217B41916BC920F6ED1655A693834030D930ADB71492A5363866DF23F821D404FE4ECF99B090CCF215CB6E73F82867A3D926A6D4BA37D59A57B55B3DE072CAA3A8463FB73C8DB8E4AE7C1A3A800745C9DA4DEDB8648BC94866919BBB0D62F6FA7D8EAA64C61199817B773B6413791BA25622A2828F55A099B32EF987B5666419C781602
	3C954881B482948EB48BE881D0AC1074910E35BFF5E01F2ADD7B20147652CAE11FA21471916B9F459F197B549EEA338370A120F020A701E201D2F75AAC85C04DC06ACE7C9D3CB190AA500F3E0A7AE03889AFB5652D067AFD1971A4E9F508B548B35FA2043E25E8E3D0246B83D3BE4114A773A8794DDA7FA57CDAA73179F09D75C547D8FFE8ADA2E3EAAA44F196D04CCE6C939F6F4BE6E365186CD7F0F41C76C147D69CFA8FE1323B73099F4A2062D6EB952F62F6C0422F754DFD792B75F1536361FD3A1862E47E2B
	010E6D6778217C0D8C879D46D1087CA314AF2B2A4F59BD20AA2D0A2ABA5D3DFA79BA6A0BFF54546A576AFA261854F59954B1B18BFE2B33302EAF64EFB674FF2B47F3F9405BC6C636B60E522DE44C6402FCD5967637BB55C42D0F30FCA84D6B7C8C3543500F3E4A3C7C65677C92EE435CBFEE8731584CA9885C364F95085B760582F15B3ED4B0FE5BADAE90359D63459610BE6F396AB67405B78A483CE577385D8A6D103C08445613FFEAA375F03A1DEA3B53512B282D4EEB8A2C4933C81B96503951816921AC1C37
	2E6E46F9AB675A8F256997978857ABC38F3E153CA0CB9B6BD97A3C45FC7A63FE52479417528F012A718C38D506AF27732E067B93A836563E863E8DE49DFBD2D57ACB506FD6050CFF63AA17497F44CA7CBC2D76A9BEED6D4B706327C89C2C9E5951848B65DEEA0289B07E825A6FEBE5D56E0641CCB88C1F26905FEBE4EF8DAC33FD9E17310C5DCE7AD8F5D30B417E64A4A25F2A424F14DFF55A7DC6797B69F82EF674BB5D1AEFEC4CCDAEA1369333E14D84D41C0DE34ED5385DB0663409254663F23126A4F14EA66D
	F4E1B05D907B7AEC3647A1E9360739EB503807F9D2634741B76D2FB68B13F9FAC8A413837CF2AE336A1AC82699788EAE73498D114C8870C3390C7B16C8A6927851DC6641F811CC8C7017F219C589A299E9974CDB3BD0E6DB32C826G78D53BB04E670AB610B125DC99D0E049C2A635C6E598579DC51BCC3C3332EBC0DF4F749511FE68B24C078CBFDFAC6E63F2B11983AFF37DCAF132C456A5CCA76B5BB3E522536BF376434F87196035F64C379A6B52817E13FD6B88FCC301A2F791191A815CD711F59C43BAA916
	841DEFE3922EE91BB6987722357E2542F1CCE859AEED7C6EB62F75FFF500684D863CF8B746725F0B0D3F9355FE3F476E24CBB6ADAE5FAEB10EF106FD2F040C719E274F8F6BFE07B204311DD6C2FCA5EBBA8D5662734656DD5258B56AF107D37578788642CE86882B6B0DBA63DEB6FC7D62648F825435D62B2343D057349D0F2CE1F64F2DA1FBC33E77F6F677978E8C76974A5DEA218246ABAA68B4F556699F10DD345934FD771C41D44D77085D38575DB4C477EC10DF811416037B623F8C21ECCE8E4A1E3CC6E43B
	A02F82CD6132255739DEAE9BB2C275C2FE85D05A9E14755C60FA77206CAFEFD23D10D70126F059993738DEAE7B36C475C2FE85D05A47A83BB81E6B7D98E5739228DE482BC0D338ECFAA2574BE59DC9D4AF64D7G256DC5595349DC6FDE143527D03D10D70126G31F6D93DED9B364BA5F9D09EAA74DF055FCC2DB934B638570A63A2026B4BED2236A13F82A8AD97FF635A8F98474674A50B820E0D1CDC56CF4AF34DE363F4A9315786F8DFAE0E0D74839B8DE32385E6F1595D8BB386DFC79CC8E0F30316FF66C092
	4E8D927ED65CD7C6667A9150B995E8A650BC20255CC6534120FB6B63C3636C2D1F9D946DD3D373D87DB273462E5358FE74CE5A1F0B7358FEB41E635F21F82581E77B540CA1FEAE843C28BCDC93DA06C576B76CE3F6F37685327FABBAC64A77196DBFC3715AFDE67B272F217DE6604D5A07768FDF97594F48E7F68B7283590F23632EAA5FECBF7E86412D79E67BF70D207D8E60C56723FD750648FEF68133DBDA9048FE51AD222F3E40EC3F1C62ED85E67B3F3E0976ED401BDB00769F3EA5321F3B1F592D5C9F48FE
	CFBCBDEB5AEF366F21F857FE337DB8896D7781EF7EFE347FDA3C48FEF1A133DBDB98487E83F44E69A8B45B1FC1F1C72159FED1825A7782AF26906DEFCF9459AFAFE2F62DC5016CAF2473182D48EC7FBFA86EAAB25B3F14047607003728081FD5A70B6CD795B33BED45016C3FC267463EE2337DA594D70B4D761DA9E8FF9270E20B517E7DD6117D7A12407BB3E6A70A4E25DDA54C4EFA0EC7D3BC4400B37B0594A779A860659E403317478F11F9B38272338E601C7650C1320F47F9335963F443CF2BB6C736151275
	1347676CF53A91BBC7B13B47C7B7C28C7BC1D960BD5A0CB2719EAD2ACC3CC77BD959787B6FDF14855A43313D722BF4BDB0976A978344301A2B84DB827938835896D34812C71B176FB524F78E611EF6A2085978E4C2ED30BAEF9F46B8E49E44B8DC9B46B8649E44B8BCF5CD2F7BFCC37A4C35090F03FBC2ED30BAB753F51C956A5B45637058F50CC39F0F437BA3FA5DD3EE68695011090F43BFCC288DD6673BEFE09C2670B8AC3B01F1184A63D0F6CB2F7B91C37A591B939F07A593EA0355F94EAD0CC3B40F43EE89
	63B00F4741952F57FD4010FEDF1A78B8EC0B1F78B83C970FF1D8466350E96173A40F430F93743AC79952E589939F07D393EA035539186E1BB2CBED1602D20C433D09980772D20C432BC9FA5D7F4B1076A6CDFC9C3EB521B6D81DAF53BDE1975457456390160CF19862F1D81C22577D8F0634A7E56263B0F9C2ED30BA7F146EE543213ED13C4EBFEBA277FB64FEE5B6E073011682C9E5B6CBB6D0E5994A4D21F2ED109F828A87FA92A896A87590742FC3A8D75A649558A7FA09AE12A67A49D7AA932F8D5197B31E9D
	C0DE0FE12B50B66161DD8E5E89196F001048A410A440570E93DB36F67B65A13AA66779FF2679E1894FF0D78E1273BB95960B4D07485B13614FA4894F25FEFA15785363137BE139576AD56C5252C3F44E3A0A67F6E107D85DA3356FBC78B2BD61C7089EA6FF4FB04A97FF226DFB342F833E4C0F5E6B237DD8F9D4AFBF7FBA168F514ACD553E33BFC1FB8DD53AFC42CD7E8EC613DB26FD534AD17E87553AFC0504728565CC2ECA7B5A387C1CE33A7C958B4ACF526442346F2CF22C4F09445175F93DC6AF9F150865E3
	34F2CB35EF6AE1ACDF57B13AFC6AF13D7C437C8CB87730360F553EDD3C7CBC6B6872D5649552B8677C635D8B843ACB88F687B15E5D4158FB8DF6A67CDE1379CC185C2F0DC03DC28F6399386B1CC8A6927851DCA65CAE1209817ED2AE33D59149C895B6CBFA854AF4740AE48A00DF45E53AAF09E4BA006F62B24FDC914904GFFEA851E533DF05EF8CE37FC5A7CE45C631A47764C8A6DDDC605610D92BD831CC477DD244CF26065FECA30C72F9A6FD348DEAE8F2810749F4ADFB20A1FE460B33F1E5F03674D2B1E0F
	142C0F4586FCBB657DD49B8F5AF7F6423A64663B3F3106797644B07AB67FD3746D75E133EF0FD90C3EED3BE6766D914DB726736D6B28B33192F5E60F18F59E9D2533E5442CF3E53CD1E75ECD54D949F5365FB46BEC0DB76A7C56CD334E7389C61DB689F52ADC6737A5334E5D235A65C9492C731B96234E7BAC28B30A6BFC5AE25679F723F43EEBB16BBC11E85479E2A26A0C65BA2524717B4F01A4334E27120CBAFF1B0CBAB3BFC31D7F17EC56795128F8162418F5FEE7D49B151C42736F364F707CFBCFBB192726
	C0BE82A87AB3BCFB6F6CC45998AE7BC6A711CDBBE23364GD59EC15998BB57FB041F53BB28DE48C7GC5F359D38A574BE597ABD46FD1508BD4F994E5C3FB395E23A81B55C775C2BE82A81A4B5EFF096B65327BAFD33DD5209728328AE55F3F4275D6216CBDBE2A977291C051D5F896B5FC020CE6368E88295B835F3372A561EDD303BCA81F6E3E245855B639D742F54C9DD73F5EFB33DF27607CB16B97BF12265E9991703EAA260A4D9BAB34EF33E5DDB279B27FDF2CA7F3E8CE354DD28564G9A818A850A2C4633
	355F54919F492DGD428DAD5E5FB9F3D19431A1DF5AAD7F518ACEAD85D220567746391AB772E70FCBDF839421BDF2F1CA2FD3DF261162FD76E956179F170F2AF8B4FFD03178BBBA5AA97DD9D6C3CF69A9D7B8B2A5939EC9247A3A89EEB4059F9EDBE45C9BE8EF8DD47F01FF031D3E47F49E3416C5FF11E680BB9E6FE33FA9F4593EB4CEFD65F6A44B72B2540DBD003767F4DAE326F2A8DE67F95BAC7CD2A9DF3DE4E662E5AB167658EBCAF1F8A3CD535E83FD59159379D8FE67F739EFAAEFF5C7C2E73BC45C38CB8
	337F1F8A3E6B8C83DE5CF134FFEF2F48FED7DDB07B5F23F329374E5C7EFFCF7110BAF37B6F6943761F8A3CD5F5E87F87170475BF914C7E8F697C2C1EE0F6D6F37CD10A079AF0E67FA045C9BE8AF89B6A51FE02CFE4DF2D8FE6BF49CF74CD2EB747FFBD4527561B63FF1F8F639F893C3413789B1151C0FEF73D101F82B493E8AE504213B83F461D505EAA886656630EDE85368F64252C0A3FB33F2F8FFCCF71DAFD707735EF561337945D9ED57574937D087FCFBD09176A71D210E1FF2EA77174B9FB7BD4A62A612B
	6A49EF24CB6921B0BBF3F8182E9FD61CE4E7AFDA5B3793AC3181FEE39B907BC70A55C25E413112765BF5A6C2ECE1BAC7CBB39B8D374D06711DA01EA130988CF6E28CC2F85D6EBECF5E6006C2FEA650FC20E5C0E90D909320DAA087D0C8A34ABFC065A3A0BF97A896A8518A32C035C08E20902088AB4A2F6CA46FE2D12F30BFB70E45EBCDD80EC0E6BCF2E87224CE4477D013ED66DB6679D0692B3D51A5E4FE0FBB5E190C0870FDEE1244FE8F4C6FBAD9DBAD37E3DB4D6531DC6C10386FFFB024BD0E3F413960045A
	E0F53E24E01D9770BA377710F30DC5BCAF53FC9C4F3F215CAE3F088DF15DADBD93B1B6C757BD2A976B1E5104F51B56C7309C4857F2AC02E2FD101F4C317C3EE07E919DA23CB6GBEB68635C1ED84FA1FACBA47944760F13A8F0A841FE6733ABDF4193CED1C8B79A5C052A91BA5739472E6D1DEA5643B00C60042B96F70E5233FCBCC751CEB4A933DDF368DF57BAD23EC186BE90EC74AD768A72BF9CCBE741118C4034D795C6E87FEB28696727C961A0F637987FC37F32CAEBCA51ABB9F3F2DB64C31E23F63598D01
	5792FB9B022FA50A9B68DBCDD883348DFB950615B5507796BDBDFE052FD9AA9AB6F06C243F5963F712871F6C5CF24EA9BCD7E5798F5711B93B5A2EBA871D6A3055530BAF3B35F34EE4BFBDAB9A7683E46462DB67C56D46F30E0C53B68B394B30421771E7F408783653F8AE7DF023A461196AD30709EC4D000FBC64A0472E8A1AE37C3C8DA4FE75CEBF6C7407CDFECE4DA2FE5630F7750A4360A763DFBCC5F457BA7DF63388634F38C856E92D0AD77649203E844B5D4AA071E9F27A7D24044ACEFB8DF6AF74903D2D
	83F63B62770F925062CD63CB4E181BFD1E9E27CBB1153F7F9C713B59F3D5715549CE5718783EFC5E9CBFF2CF247278CE6F9271A7F3FE4F97A2FEB86797DC9071E7F27E5B2378F3B57EBA6E5F4E0B22724BF879D8A1DFEAC67E06EE91BF1D73DF9672F31B317F44DAC97FE1E96BA93DAFBD5AC27ACA0D6C73B98DED46F4FE37472C135C39D5F21D1E533A1EF7AE91D9EB13F62DE098820C7F4447DB3476BD61738CF82B6DF6F5EC9B76196D117B3DE6DE47F027086FE8463B10FF39B47AAE643B4D06FA3A49FA2D4D
	631007477479A78AC95FBA2740587509784E8FC80C1A20E6FDAE73F85A5ACA7B25CF99247CB1F5DAFA596CB3392F9C62F5FA54A562CF61FC35DF441F4E7995EE91FF960FC909EFF4CC9E7BDC0FC9E3A33957E9718C4084A25BFD9E5DF9263B4EEF56CD6ED967B5637B65D777FFAC895FAF37488E276758G6C655C3ACEF60E3939037FADF7B3FF0B4D31744F91DB5B051815E32FF0AC04E38FFC01D8B4476C9790DB71B97AFA0F1AA97615CFE52C6C190BD8362085756D30A166605809EE4442B936161FA947F06C
	399E4452DA391C9331DA0E7D0EE3A39C4BF2A1B60BE32FF3EC99479E6EC7AC278D31ABEE443AB8F6430FD8D89B7FDB368E51B9447C36E0F75D2F894BAD89DAEED317285C0A206596894B251E89D66EC7DF084AE586AD57F7C1781EB7E8394E0B22F235C14B5DA90B4AF584ADE76D961EA386AD37C9F87EB8A9E839FF6D91158B8BEE4FA9AA9799345CC2E139682065F2DC22F28B02167B2330DCEC50F2B37BC5656202162B3CA2AA17F6B6D839EF78056F6D47A9477695C59D78962974AC3EC5FA0FE3D59C4B23F3
	2D9572DD9CFB07E3FD9C7B799708299C3BF58131C99C3BF29131A99CFBC0C63BD3B9E66FC6ACC277450E58CC0E3D5E037A22F5B9A7E273B8769647E2B876298B75AD61D8FCBFE24BB876AC4762B856FA853144F6442679514606F6DC9BADEC81C8DBEF9DEDA3E9761BDB55B57A77387B0CAEF747D9BD9D592E271F6F50537ECEBD7D7D73FA7A67DDFAFA5197FABA7F221EBEE55353D7E4BDBD3DDBCFBFE557536C2C0925D777984A122BC2097D3FB7CF97BF94AFC9D17FADA529D20A76E72D2A14C49FFC2852FA32
	604B56DF0D28D2AABEAFD1258FEAE4EF3636DED425046A2696D5DA233FA281AD74CFCE0DFA19506802860DE34D0D5627F915065B38ECD82692993AEBE319355A36AA1B6E2B4036360DD22555FC53849A0C9BA44832FDCFB65D57008EED1B0379B46A16362F81FBF49713CD36B120DFDBFA2B52BAEB93D60A39320596234B65ECD8AF133F4504A58BD4032EE241A13EE285B55A6A945410352853B3G5689846674851EF6CF1BF74B787EE4619DCF857CBB3111F3ECBE89BDE77EBB315E03244F4F87FC69B93CB3F8
	62G79BFFE825DB537764BAE17AA3BAED73B9D645E7958302A68FF1B60BAC8747BD1C6E3B21E7FA0717B12214C7F83D0CB87887BFE3E002D96GG50C9GGD0CB818294G94G88G88GA6F347B07BFE3E002D96GG50C9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6796GGGG
**end of data**/
}
/**
 * Return the JMenu4 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getCapControlMenu() {
	if (ivjCapControlMenu == null) {
		try {
			ivjCapControlMenu = new javax.swing.JMenu();
			ivjCapControlMenu.setName("CapControlMenu");
			ivjCapControlMenu.setText("Cap Control");
			// user code begin {1}
			
			ivjCapControlMenu.add( getCBCCapBankMenuItem() );

			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCapControlMenu;
}
/**
 * Return the CarrierMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getCarrierMenuItem() {
	if (ivjCarrierMenuItem == null) {
		try {
			ivjCarrierMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjCarrierMenuItem.setName("CarrierMenuItem");
			ivjCarrierMenuItem.setText("Carrier");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCarrierMenuItem;
}
/**
 * Return the ConnectedMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getConnectedMenuItem() {
	if (ivjConnectedMenuItem == null) {
		try {
			ivjConnectedMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjConnectedMenuItem.setName("ConnectedMenuItem");
			ivjConnectedMenuItem.setText("Connected");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConnectedMenuItem;
}
/**
 * Return the CurrentstateMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getCurrentStateMenuItem() {
	if (ivjCurrentStateMenuItem == null) {
		try {
			ivjCurrentStateMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjCurrentStateMenuItem.setName("CurrentStateMenuItem");
			ivjCurrentStateMenuItem.setText("Current State");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurrentStateMenuItem;
}

/**
 * Return the CurrentstateMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem 
 */
public javax.swing.JRadioButtonMenuItem getCBCCapBankMenuItem()
{
	if (cbcCapBankMenuItem == null)
	{
		try
		{
			cbcCapBankMenuItem = new javax.swing.JRadioButtonMenuItem();
			cbcCapBankMenuItem.setName("CapBankMenuItem");
			cbcCapBankMenuItem.setText("CapBank List");
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
	}

	return cbcCapBankMenuItem;
}

/**
 * Return the JMenu3 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getDatabaseMenu() {
	if (ivjDatabaseMenu == null) {
		try {
			ivjDatabaseMenu = new javax.swing.JMenu();
			ivjDatabaseMenu.setName("DatabaseMenu");
			ivjDatabaseMenu.setText("Database");
			ivjDatabaseMenu.add(getCarrierMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDatabaseMenu;
}
/**
 * Return the DisconnectMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getDisconnectedMenuItem() {
	if (ivjDisconnectedMenuItem == null) {
		try {
			ivjDisconnectedMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjDisconnectedMenuItem.setName("DisconnectedMenuItem");
			ivjDisconnectedMenuItem.setText("Disconnected");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisconnectedMenuItem;
}
/**
 * Return the HistoryMenuItem property value.
 * @return javax.swing.JCheckBoxMenuItem 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getHistoryMenuItem() {
	if (ivjHistoryMenuItem == null) {
		try {
			ivjHistoryMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjHistoryMenuItem.setName("HistoryMenuItem");
			ivjHistoryMenuItem.setText("History");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHistoryMenuItem;
}
/**
 * Return the LMControlLogMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLMControlLogMenuItem() {
	if (ivjLMControlLogMenuItem == null) {
		try {
			ivjLMControlLogMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjLMControlLogMenuItem.setName("LMControlLogMenuItem");
			ivjLMControlLogMenuItem.setText("LM Control Log");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLMControlLogMenuItem;
}
/**
 * Return the LoadGroupAcctMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLoadGroupAcctMenuItem() {
	if (ivjLoadGroupAcctMenuItem == null) {
		try {
			ivjLoadGroupAcctMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjLoadGroupAcctMenuItem.setName("LoadGroupAcctMenuItem");
			ivjLoadGroupAcctMenuItem.setText("Load Group Accounting");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadGroupAcctMenuItem;
}
/**
 * Return the JMenu2 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenu getLoadManagementMenu() {
	if (ivjLoadManagementMenu == null) {
		try {
			ivjLoadManagementMenu = new javax.swing.JMenu();
			ivjLoadManagementMenu.setName("LoadManagementMenu");
			ivjLoadManagementMenu.setText("Load Management");
			ivjLoadManagementMenu.add(getLMControlLogMenuItem());
			ivjLoadManagementMenu.add(getLoadGroupAcctMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadManagementMenu;
}
/**
 * Return the LoadProfileMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLoadProfileMenuItem() {
	if (ivjLoadProfileMenuItem == null) {
		try {
			ivjLoadProfileMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjLoadProfileMenuItem.setName("LoadProfileMenuItem");
			ivjLoadProfileMenuItem.setText("Load Profile");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileMenuItem;
}
/**
 * Return the MissedMeterMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getMissedMeterMenuItem() {
	if (ivjMissedMeterMenuItem == null) {
		try {
			ivjMissedMeterMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjMissedMeterMenuItem.setName("MissedMeterMenuItem");
			ivjMissedMeterMenuItem.setText("Missed Meter");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMissedMeterMenuItem;
}
/**
 * Return the MonthlyMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getMonthlyMenuItem() {
	if (ivjMonthlyMenuItem == null) {
		try {
			ivjMonthlyMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjMonthlyMenuItem.setName("MonthlyMenuItem");
			ivjMonthlyMenuItem.setText("Monthly");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMonthlyMenuItem;
}
/**
 * Return the JMenu1 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getOtherMenu() {
	if (ivjOtherMenu == null) {
		try {
			ivjOtherMenu = new javax.swing.JMenu();
			ivjOtherMenu.setName("OtherMenu");
			ivjOtherMenu.setText("Other");
			ivjOtherMenu.add(getRouteMacroMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOtherMenu;
}
/**
 * Return the PowerFailMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getPowerFailMenuItem() {
	if (ivjPowerFailMenuItem == null) {
		try {
			ivjPowerFailMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjPowerFailMenuItem.setName("PowerFailMenuItem");
			ivjPowerFailMenuItem.setText("Power Fail");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPowerFailMenuItem;
}
/**
 * Return the PrevMonthMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getPrevMonthMenuItem() {
	if (ivjPrevMonthMenuItem == null) {
		try {
			ivjPrevMonthMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjPrevMonthMenuItem.setName("PrevMonthMenuItem");
			ivjPrevMonthMenuItem.setText("Previous Month");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrevMonthMenuItem;
}
/**
 * Return the RouteMacroMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getRouteMacroMenuItem() {
	if (ivjRouteMacroMenuItem == null) {
		try {
			ivjRouteMacroMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjRouteMacroMenuItem.setName("RouteMacroMenuItem");
			ivjRouteMacroMenuItem.setText("RouteMacro");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteMacroMenuItem;
}
/**
 * Return the Separator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getSeparator1() {
	if (ivjSeparator1 == null) {
		try {
			ivjSeparator1 = new javax.swing.JSeparator();
			ivjSeparator1.setName("Separator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSeparator1;
}
/**
 * Return the StatisticsMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getStatisticsMenu() {
	if (ivjStatisticsMenu == null) {
		try {
			ivjStatisticsMenu = new javax.swing.JMenu();
			ivjStatisticsMenu.setName("StatisticsMenu");
			ivjStatisticsMenu.setText("Statistics");
			ivjStatisticsMenu.add(getTodayMenuItem());
			ivjStatisticsMenu.add(getYesterdayMenuItem());
			ivjStatisticsMenu.add(getMonthlyMenuItem());
			ivjStatisticsMenu.add(getPrevMonthMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatisticsMenu;
}
/**
 * Return the SuccessMeterMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getSuccessMeterMenuItem() {
	if (ivjSuccessMeterMenuItem == null) {
		try {
			ivjSuccessMeterMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjSuccessMeterMenuItem.setName("SuccessMeterMenuItem");
			ivjSuccessMeterMenuItem.setText("Success Meter");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSuccessMeterMenuItem;
}
/**
 * Return the SystemLogMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getSystemLogMenuItem() {
	if (ivjSystemLogMenuItem == null) {
		try {
			ivjSystemLogMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjSystemLogMenuItem.setName("SystemLogMenuItem");
			ivjSystemLogMenuItem.setText("System Log");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSystemLogMenuItem;
}
/**
 * Return the TodayMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getTodayMenuItem() {
	if (ivjTodayMenuItem == null) {
		try {
			ivjTodayMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjTodayMenuItem.setName("TodayMenuItem");
			ivjTodayMenuItem.setText("Today");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTodayMenuItem;
}
/**
 * Return the YesterdayMenuItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getYesterdayMenuItem() {
	if (ivjYesterdayMenuItem == null) {
		try {
			ivjYesterdayMenuItem = new javax.swing.JRadioButtonMenuItem();
			ivjYesterdayMenuItem.setName("YesterdayMenuItem");
			ivjYesterdayMenuItem.setText("Yesterday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYesterdayMenuItem;
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
		setName("ReportsMenu");
		setText("Reports");
		add(getAdminMenu());
		add(getAMRMenu());
		add(getCapControlMenu());
		add(getDatabaseMenu());
		add(getLoadManagementMenu());
		add(getStatisticsMenu());
		add(getOtherMenu());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	reportButtonGroup = new javax.swing.ButtonGroup();
	addToGroup(getAdminMenu());
	addToGroup(getAMRMenu());
	addToGroup(getCapControlMenu());
	addToGroup(getDatabaseMenu());
	addToGroup(getLoadManagementMenu());
	addToGroup(getStatisticsMenu());
	addToGroup(getOtherMenu());
	// user code end
}

/**
 * Add ActionListener(s) to all menuItems in each menu.
 * @param menu
 */
public void addToGroup(JMenu menu)
{
	JMenuItem item;

	for (int i = 0; i < menu.getItemCount(); i++)
	{
		item = menu.getItem(i);

		if (item != null)
		{
			getReportButtonGroup().add(menu.getItem(i));
			if (item instanceof JMenu)
			{
				for (int j = 0; j < ((JMenu) item).getItemCount();j++)
				{
					if( ((JMenu)item).getItem(j) != null)
						getReportButtonGroup().add(menu.getItem(j));
				}
			}
		}

	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ReportsMenu aReportsMenu;
		aReportsMenu = new ReportsMenu();
		frame.setContentPane(aReportsMenu);
		frame.setSize(aReportsMenu.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.analysis.gui.ReportsMenu");
		exception.printStackTrace(System.out);
	}
}
	/**
	 * @return
	 */
	public javax.swing.ButtonGroup getReportButtonGroup()
	{
		return reportButtonGroup;
	}

}
