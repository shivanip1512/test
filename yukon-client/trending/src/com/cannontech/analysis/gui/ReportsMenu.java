package com.cannontech.analysis.gui;

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
	private javax.swing.JMenuItem ivjActivityLogMenuItem = null;
	private javax.swing.JMenuItem ivjRouteMacroMenuItem = null;
	private javax.swing.JMenuItem ivjSystemLogMenuItem = null;
	private javax.swing.JMenuItem ivjCarrierMenuItem = null;
	private javax.swing.JMenuItem ivjLMControlLogMenuItem = null;
	private javax.swing.JMenuItem ivjLoadGroupAcctMenuItem = null;
	private javax.swing.JMenuItem ivjLoadProfileMenuItem = null;
	private javax.swing.JMenuItem ivjMissedMeterMenuItem = null;
	private javax.swing.JMenuItem ivjPowerFailMenuItem = null;
	private javax.swing.JMenuItem ivjSuccessMeterMenuItem = null;
	private javax.swing.JMenuItem ivjMonthlyMenuItem = null;
	private javax.swing.JMenuItem ivjPrevMonthMenuItem = null;
	private javax.swing.JMenuItem ivjTodayMenuItem = null;
	private javax.swing.JMenuItem ivjYesterdayMenuItem = null;
	private javax.swing.JMenuItem ivjConnectedMenuItem = null;
	private javax.swing.JMenuItem ivjHistoryMenuItem = null;
	private javax.swing.JSeparator ivjSeparator1 = null;
	private javax.swing.JMenuItem ivjCurrentStateMenuItem = null;
	private javax.swing.JMenuItem ivjDisconnectedMenuItem = null;
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getActivityLogMenuItem() {
	if (ivjActivityLogMenuItem == null) {
		try {
			ivjActivityLogMenuItem = new javax.swing.JMenuItem();
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
	D0CB838494G88G88G1DD53CB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDC8BD4D55719F635E4E2BBF6E2B2F4AD5230B2CCE3B2A4C3AAEDF4452634B10DE9CD62CC1C991CBA092BA109CD0963242413CC1D1124C739A09AB5988191D1AF8F9FA8617D38DC5E8F9F98D0822818106A61F2002B17FB6F5CFBD0E8E8B37F5E7B7C7B9C0E7BF213AC99577A57B97B7B1F7B5F0F3B77BE9B25740391491BB627CB5226E4C97BD733C912CEF7C8520B77A75B91CB3506DAA4433FDC20DD
	52799A82E681EFE2B45412A63D13178A65A2A8AF9D8E356C01775DD2D27E5C137092404F95603D7E2A23FCE6BF799968E78BADA727EE0767CE20D2407A00A22C447F7E54AC8D1F0072CE6D1D50264D125419CBE45EA84DB260EBB5B9AB70AA01D6D399DBEA0E468F05B21109026752DC5D66D69A5DEDF61BD69E38B3C20AFDE6F54A8E624BE09F491A47F45B35A754BC05A4A2C9FA3A6A83F856F7DF9BE9F4F8460A9D325B6DF12B0AE334D8F64B2EC93F53DFBCB26E345BFB942F47277A3B9477B848AF4AA3F16E
	A9CB25B6A4E98A4A4D79843B230C64E68361C379A8900B751FF654E31D2EC5C8CB6FFBB6E02CA179AC4648FCBD562817B2AD7D85443ECCF3CF746201EF850C4572E0AA4692DE00319CABA53231D0DE8FE4B90431B49C41D87ABFDBA82D9E5C1872DE00D8F28EE9F9BB2447E2FBB65332177603051924AD67GEF81509220B5C009C0B907C3AD7BE87F3B1AF18C1EAD03EA1BE7EC4C63EE71FADBDDC33EAE4F106202F73B9DB89A5623B8BC3EA1C9E2FDAC272CC8908FD26561697DCB6F9397A56C93D1BD8FC8FD6A
	9B341D4DF58BDD75FC4AE94D4640E1C31E8F133C9DAFA7F91B0277CD348EE00B628D94CFB0602CCD3ECD5B1C142DC7C2ADB1C7B04F7DBBB116CE4BF724045237D33688E2197869EFD37A0F32983CC775D8D80E3F11C5F29C8E78922015C071C0290550378AB1470BB2AA026538837A348FBAF46F7820D776292D63CE57106253F2BD12BAD32EC54474463F349E4BCBE405310DE29EC99466A5A672251476C22D0D8A753CB05F5F8B68FB2A70663E311337613DF4CC4CAFE4ED184471C80AC798F056362594A765E8
	60259F8B557A7E11ECB2C7CDC0B994A88AE89950DAA069B8489D473679FDC649976C7F5D06F6E13ACBB2B36129CC28BE39436DD7E537C339A07B1C7220CB71CB1A4C9319A49F035374FEC6472583FC1CD25C0ACFD61D9EB7D81747946884FEA673DC66AEA263D3A8374FB9260003B12F04F6FF11C97A33CB762B673DC3322A68920C7FE2A6E94B6B728530888660FD68B849D7851D93AB613D7BB8662AEF9F41A2213C14676563BD6F4373C368140A4BEEEFF7BAC89832EF927BD6D9F981BCE3C1FEBD1025886694
	20FA20BEA0AFD088D0F8910E35577B31CF356F38DF5A3EEA2330CF91DAD2447A474AA2F31FEA23BEB7G1E758178823A82948294893482A881A83D98FF876FA91BAF300F310A7AE03D89A775B316C9DD31FEDFE4BC05E8F26186F9965BCF0531ED51462048563B26F21229CC66D1721B755E9708EBA70D01BDF79BB0569FFE1AC346148D7258872496E31F78769EAB9BAB2D32DF4151F14E879DDB9952FB88133DAB0B4421DCD75CEA3B62D55CC37062577A665DD9DB75F1B36C61FD7AEFA8CE663FB66858FE0E5F
	1F350B61E043B88A117FB0654B2A6AF38E0E2BCA2F222ACE77082E3F1846621FB6357A353AAE5BCF6ABA8F6A38GE8912FEB635E5D067E5F6971DC9B777611112D0D2327686F79F2100F637DEDC129C56BA35A9CC84BBA3F3A345F500F3E4C3C7C45677CF35C0739FF5C8AE239A94B8E5C3636ECF15BB6E60B5B36BDFB66366B4C96359D6375E613BE6F3961B674057EECB2EFB9BCEE37C2BBA4D71158FA7275BED28F275B29DEF48E0DA8EA2F730F8A2C49CBC81B1E21F323855E33CAF05E3A110F73D6419F7FD6
	0A387C2F42754A447DEF25F8CBD81B06146873960B6952BE52479417B28609EA730C3BD506DF26732E067B13A83643318D1EE748BA36C3D5464AB06EDE050CFF63AA1749AF6CA4F11ED3C7951F3676E5780933A48F1D9EF9288B964AA3548593E07C1574F733DDD665C1984C0443703B9592FB1B6CED03E5364F63B26AB89C240F35F475987CFF67B8715FB2B466F4CF73BFB7195869D561674B2FBA9D5A421C0D395F9DA03E97C01E16G2DAC41B177423E3D06B127CDACED9E170BB5A57032CAC9BBCD15279BF2
	9FDF4276B8641D6DE1A24B0DFB18AB250C9F864FA8A0A673CA25C8E699BCD7F3196C9A11CC82BCADE5A8F33ED6A413837CD2AEB32FCEA4F39A7883DC664986110C8A7C39DC66FF1AC4B29140DF44E54ADAC5B2AB009FD706F9DE1E13C64614723F638AACD948247621B2096B0E1C77CD3C8B32EBDCDF4F7CA20774C317E1BEE478B37B45FDFC65FEB287DE6376E37727C8E4DD42EC323E9DAD931BDE1FF38CFEBE48842F35631AD4E3DDB6C07C38EF4DA9077DE8B95DEF71BD9ED947B1EC02E24950B1F7A5631A36
	AB4D3897ED773FAE9C47F4AC17B39F734A4DEB7DA2AB319B85F8CCB966726C81636FC40B5F6FF1B86912CD4B4B5B870CE31CE11B8F12B1BE6C7479E15DBF24CCE0EED38F12D8491ACE03357C3C1619A75D3CC6BDB164D4BDBE3E01F01081426A7A2713463DEC78FB2F243C9B202EAB35BAA6986A2A427E11793D340D6C8D795E5BB9B8D6BCFEFD2CD89ED40B95F0EE3753E96A0253BFAE3BE833E97B6E3872D4AD76528A5C6B16D6127EB18A65B9C0E19538AF5ED7053251DCF6241A4846C3D92A04393892E5FBEA
	385DCA147D2E0D5A05729C20B0AE7B2DDAEE174B7E1B1D5A0532D485F62BD0F6D99D37DB0532E98D54AE1467G05F1590D8D5CAE17BD55C46DC2D92A863B55A8DB5E4C6DD6236C1FDB28DDA84F818A63324ED6EE174B7E300D5A0532D483F601D83BEC5D33875B652AFCDD1EA8765F005FCC2DB9A426DB37156816024EE920ABB578FBF6315CB88EA262DE8EBC8EEA34F1D0E39E87BF49257B8540E3EAF09CFC7D60AE43B8680199DBF60F406C40578C56A4B68FE865AAEBB24E83927EAEEC4CA3737ACA30399E48
	E203F994285E06BEE22CC17751A7A6E658C77F2ADC34A79D30E9FBFB5B4DEBB23677DCC07BEE080D6DBDB7F17CE10A4FB760ECCFDADD09E7C0614013EAF17DE72D92799F2AE5FEA7EA8379FF010E07F935E67FEBA99ED6EB76BFD60D7E97GAF550E7EAF56087C3B6C4C6F9CFBA07F193544DE285D6C7FA045A36CE67F11B6749F853C34BA74FFD72D483FDA477C4E2D8B643F2B0E588B2FB37B3FC07148BA337F976D68FF9170326A517F53F5A27FD3754C6F7C7AC07EC368DC30205E6C7FDB940F2EB77B4FECC0
	7FB14033B620FF29D164BF2401798DEB88647FC7F4FE09EAB07BDFC671A58DE67FDDCD68FF99700EB6227F1AE6117FF90D4CEFC4E3A07F3F25F35622C6337F0D94DF5AE8769F52027ED7G2F34897D3BDBC57EC31B34735426C07EBFE8A776E21A4C7E2BA93E3C496C7F71B6749F8BBCDBB32C7B287C63F4CE8CEFE6726FF1BB4FD1FC1981E7F6DAA8CE4A6B01B75A0267A55985E47EDB8B65B7DBF0EE4A4CA5FBEF1C7F3ABDCEB77C9CEAF35D1339E44D6371B9C71CEE4416D34C6171514D8B431272836F2B3665
	0B77D5E97962FDD5E67E4CFB6683790176DDECFF1BC8FF43AD2D2116F4A00649958433C259560AED7AD0A5BC3479759143FBFDA56EC3E703180F73336A035539268A73B0407390D605F99865F9F829DA2F7BBA433B3FFA7673B0EFD6FD30BA3B695AEB9E54B71C6761DFEAB08F11BC8FB935FA5D8F995E3FE71B7DBC7CFCD6FD30BABFD80BF9D84173304D0EF9D84D7350DB2757FD40707EAB7B6C67611DD975416A9CD707F9C864F9E82947BC2436E19E3E5920577D2F8D6FB98D331F072AD975416A3C279173D0
	5406F918E86473A44F4393CDFA5D1FB13C77B64DFE9EDC336A035579ECB366E10067E1E1B31FA7F99EB63468F55F542A3FFF33E57673F05F2C7AE0F53E0D6E6366C1FD43F99E563572F912672122DD2FFB3D617D2936594F43AF7F9FFCBC3515FCFFDB8175DD8B3481A83D9D4688D0A750E8BB66E195151B836585C04B015683251D8835D482751FC039D3DB3D82DF44AE31C55E5335E7D13B78EC9DE3B1631D8164753A8C8BFD939EFEEB41EFB666EFB4C8E46F1BAC40F74C10C736CEBBD8C05624933C1CCF4B13
	921E336E2EA067EBAAAC8C27CE103BA12F13A39B6D5C687BB7C8BC43BEF98C16F63DDE45A1251F2473D3851E2BADBD416A9E2BBD5F04A733330BAC55B57905D5A8BFF0D25BABEB4F7970E4F15CD6B3BD0E0E8FF47DF57C9CB5DA53DB26BD571EC4FF2EE2DDBE570E7216D3CCAECBFB5ACE217C93A53AFCFFBD4A77EBF2AE6DB90F4B2FAD55656FE8C479A8CDEE29760C6372DB4AF5791FB5233CF4DA6BEB5A3372B456BF3CF5FA7D0FD5687A1B5AD03FCF539B551EF339FE20F3F451197BCCE774014E7503FD8F90
	1F57877A46404EE737EFB31F4F12EFDD11D0271853F89E7DF906C8A6967871DCE661C1110C5481F3C6874A94660AE40A005F4CE52E650BE47A016F62B26307C5B2A1408F63B23B0F0BE422013F2C834F511E7B44F80E16F077BF27609E54BC0E57F430360E6F504F5158195F3DF4BFC5F4F24E04DA7A4E90ECC505715B8659239582114141786FCC63A79B78AC2EE78F6159EF623311D26762F5816FB1051F517A3E76DC12141EC21EAC36D5D5985B869EDBD235B936451B0D31D5D51BE3FBD80B0D59CC33214D52
	8F51E6234DEC33E91ACD2F4DEC732DCDC61B276CE8F3145B9C371BED76EEB25A7CBB3B5966A7C9C61BFF2EC71B915CE6D43D59E67E34F679E53D59666D1B0DB697B7224D555C66AB0DE61B4FCE331951E436F9A459E8F3FDB35ACC6BC41B59ADB3771F4E96334D9D53EC9EE9C31B27394D73EDB35B3C3D4DEC7349E9B65B3761B71AB91DF8367D7C7B746C8A4AAB0162BB719C7C518C14153AD076E4A63D0FG65EE2051AE147D75C1EE174BDE37D23BD0DE89944FE565DCEE77AC4AA664D23BD06E869ABD0B3277
	65F33BDC363C005A0572CA20F8AE7B70E1EE779C4AB69F21F6215C8DB4FA8EE5BFBC4E6DF259054554AE14D782451F43B3A687BDCBE13F796E0AC3703CA0DF95FE79B9A3DF174F8DDED59CEA1FBCA27135EE45D73B7B7544961CBF96BD76DDA96CAF9E8A786DC86A566E06EA4F6E4DEC7EE0714750B5C3B770DCC07301A201E200E23B793DB89AA3B935078A35282A6C9825DF4930E6971C4A8D9DA68B98D63767056768B391534B30FDB53DCE7BD7537BFA7DD753FBCCF8CE9CDC6F8D6179EEF03D22362F265731
	CD24975F9DF8BD4D4EDB23F613BE1F584D4EDB13B97EA84553BE52F1F68E9BC7678AD2AEG5E5C0FF0CF9013A1723F7E23E07E437690FBD20F797E68C394AF6DB15F9F6D4C447B23D7001758037E579F94798F6B8D66FF9D1D2322FA4D67597F49662EDE73F97614151F67836FE89F7A9F4E951E67778573BF14C73F5B74196FD83AA99E5DE73EE379DF39F847F2A9708A4E237F48FC117F39670379FF004E2561674D6D7FFD0AC71FB7373F2DG5BFF99700EDEC07FBFB8AC2C7F05E07E975179B9628273331563
	CFD0BC4600B37FA7A9CE4A2B01D7FF917D67960B7CC7DC8C66BF3F04FECF39E84E7F8794DFF6511C7F4545187FD860597A71B7E2F38D795D8D07729220B5C089C016CBB83F3EDC295D9B904C2DA706C69458BE10DB2BAA7E4EBCD595787B430A2A60F7DDD7D511FB8D039ED5750C917B083FD0C57225FA3C94E4D8DC9549274FB9B22AB2D18DFF152EBBDD4AB005597949953AFE30C23DCAAF71365FCD30FAA88FF06C47941B02F2B847DEFD7FD61DF590DF78BE203DB39F3F3FE5BE0CF776703CC0BBEB4F409C44
	703A5D3B075C07DD8E65F8A04B47B0B6015A01860026004201E2BEC679E8AA3F864A09C0B91FC0DF869AG1A828A870A81DA73894A77E412BB2AE8D794EF3BG1FB2E18381F483D138A6CF6AC4E2DFFEE93ABE0BED124676E56E4B92B25F0D1D697CC5C4F8E7B6D9E23F8766BB162C2D122C58D609BC1789D609477E5F06772FD9E77FAC6E1ED975416A7C27DC2CF36AG5679EA9EB993C967E51516CBF9B9B177D645C5FC086B1E11B79BE3F3FA5D9F4F473AF7733AC551EF4483D01E625823940B0072D20EB596
	840BEFC0D0B7C2D381701BF3F0B1280FC0F705C5E716629CBCCD77C1F190D3822F5BE3C748BD43C4A8E7FD8A3FDBC0FD1FA26F671427C2B994E891D0AC67F59EB146CBF40D7E92CD7536FD7A4567975D3FB24D07391E66FCEC79927DE4AB4F092D186464CD70E979837AADACA1E3A00D17ABE8391417FFD4FCAB47AA71F3F3FF083D25BE4C39526EAB5484DECB6C2A893E1648265F1695D88374CDFA956DAC3C065E23989E76ABFC4DD2D01346318EFF37476FA417AF5939650DED534FE49713A3BB6DCCB3F28F19
	3FDB9C2A733AD31D6C740C600DEB0DEF39C04F0DA67DG9939F88779B73B0DE79E5D10E7728DE3821E0C7F43F491FF5E9F70FCF9F52D9ECB58C1A25BB66EA317B548912C026E98FFFFAB4965E9279FF67D1326B8DF12C91CED6C3E3BB2E40813717F6AB0315D6E74BB4CA20C7F40E532E66BD53C32CF8673E5287717B4BFDDCE3F1FE8286C645760776F9D44EE6F384321787D5384987FC89251A7674D5DBE4F3053251874338E10383BBDB7945FC959693AA93F2F6DB1670FFC9F0A6079FDA4CB44DF447913FBC5
	7C651C5F14AD622F617C64FDC6FE02465F4963AB5BA7524F3A0C7A3F93720BB87F47B9A2FEB3673FF6C0446F3B4C3F4F50BB856CFD32DE6FCB9D1F113E52A67BFCCEC31BB11B776719ED12EFED235CE6F803EE674ED1A25B5925FDE2B08C81467FF5F206563E27FC1EF1EF0B43215E5C0685E6FF643B5E942F6342C3A27E7C4B78CDA46E7074717BF8239E5F574E1235DB1FE7C81E3C294F7F638D5237AEA9B0F6FDA2FE65C51223AE2859284BBC1E6E29247D5227DC277C1B6A347108B9E6721DF2812F5393C7C5
	7CA51CFFFB21083F02733B0E0978F1BCA7670A27676465A6BDA7CBBEA75FF8FABC63B001488E1FC78F1E599EAAB15BA65FD75F3C0C770A17A713B9B5403DE2EA081DE39ECE47F34C54ABF836194031E60ED933901B6058BA0EC5F3ACFAAFE2719C3B1E0D18D546786ED4F3447171690B690E6EC35DFE996D9567A0B61FE3EFF2ECB9472C7CCCD99AC4ECCD9EE2B60ED98FA16665582B9C0B64D879D14462B83601E3D98E44FED208D8B747C20EA1B617E37714A0365401EB06636922F3088D0EE05F355F926AED9F
	8A26F7A8CB24E78D2A779B21DEE5D03D45FBC5FA27036AFD16AD3C6F9DD4EFEC1FC84F9BD42FA2C7F83FB7285E658342F344606DA0BCFF0C8A2A77D21EC8EFE9F0FF07C4FA31C1757EDD28979FD42F6628C8AFB1285E5BC23DB4A5185E1305A23D0220FA5FB8A6523385550BA89169F54E204776952DF42E3D82F2DE856F98ED4FC0EC0AE375F42E1D8B65B00E256CC5EC8147D6F0AC1AE3F7E5A396433110FD08AD6758199C44E2B9F67DGE2F1FAAC8791DB4F318DF908ED50658EA1E699C6EC73E14452B8F676
	A8E2B99CCBAFC46CA8471E67D8A547A20EA1564C31C7CBB0FF1D4378DBFBA9FD7AEFEDB23D3B265DD721F75718DCEF56F439BB5AF4399F346B6F2BCE686F9BCE6A6F91277577A7BB7477D74F686FDBBB75777EAE7D7D33337A7B3D677477FF68565F93BE525FF7776A6F55FD865D73641D7D1FB48FD6BD348966117F1012D569DD431F1C2AD2B23D8422CA6F11051FD53FC922CA29F86584D45AE42FD5DBB72AD2D2CBD78FA8521BAD2A34CD3FE12203CCE43A1A415E4D4E26DB51E25A0E1BB9ABACD6098C5D3B31
	428EEDF3E5253BABD55A22ED26D4E9AB5FBA018563B6890AEC77E3253B9B3021EDF6303C1D06256DEE409F5D4BD849E6866CEB8BF0D55A59590515E221E4D0B53AE836422A197C25A4ACE2209AF4AD8B8171F5AB18515628E0062CC81D1EF170CEA052AEE419E7256BBC7CF6DB783971CE4A6A5B1E8E7837DDB1235A7F31B0EA7E5B2ECEFAC7E33B934610933FA30457104BDC013EBF770E49AE17AA3B2E35380748376856C9D5517F8E61A39B316FC7990D297D2D360D44FD552073FFD0CB87889285797AF396G
	GF0C9GGD0CB818294G94G88G88G1DD53CB09285797AF396GGF0C9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2D96GGGG
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getCarrierMenuItem() {
	if (ivjCarrierMenuItem == null) {
		try {
			ivjCarrierMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getConnectedMenuItem() {
	if (ivjConnectedMenuItem == null) {
		try {
			ivjConnectedMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getCurrentStateMenuItem() {
	if (ivjCurrentStateMenuItem == null) {
		try {
			ivjCurrentStateMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getDisconnectedMenuItem() {
	if (ivjDisconnectedMenuItem == null) {
		try {
			ivjDisconnectedMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getHistoryMenuItem() {
	if (ivjHistoryMenuItem == null) {
		try {
			ivjHistoryMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getLMControlLogMenuItem() {
	if (ivjLMControlLogMenuItem == null) {
		try {
			ivjLMControlLogMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getLoadGroupAcctMenuItem() {
	if (ivjLoadGroupAcctMenuItem == null) {
		try {
			ivjLoadGroupAcctMenuItem = new javax.swing.JMenuItem();
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
private javax.swing.JMenu getLoadManagementMenu() {
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getLoadProfileMenuItem() {
	if (ivjLoadProfileMenuItem == null) {
		try {
			ivjLoadProfileMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getMissedMeterMenuItem() {
	if (ivjMissedMeterMenuItem == null) {
		try {
			ivjMissedMeterMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getMonthlyMenuItem() {
	if (ivjMonthlyMenuItem == null) {
		try {
			ivjMonthlyMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getPowerFailMenuItem() {
	if (ivjPowerFailMenuItem == null) {
		try {
			ivjPowerFailMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getPrevMonthMenuItem() {
	if (ivjPrevMonthMenuItem == null) {
		try {
			ivjPrevMonthMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getRouteMacroMenuItem() {
	if (ivjRouteMacroMenuItem == null) {
		try {
			ivjRouteMacroMenuItem = new javax.swing.JMenuItem();
			ivjRouteMacroMenuItem.setName("RouteMacroMenuItem");
			ivjRouteMacroMenuItem.setText("Route Macro");
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getSuccessMeterMenuItem() {
	if (ivjSuccessMeterMenuItem == null) {
		try {
			ivjSuccessMeterMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getSystemLogMenuItem() {
	if (ivjSystemLogMenuItem == null) {
		try {
			ivjSystemLogMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getTodayMenuItem() {
	if (ivjTodayMenuItem == null) {
		try {
			ivjTodayMenuItem = new javax.swing.JMenuItem();
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
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getYesterdayMenuItem() {
	if (ivjYesterdayMenuItem == null) {
		try {
			ivjYesterdayMenuItem = new javax.swing.JMenuItem();
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
	// user code end
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
}
