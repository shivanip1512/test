package com.cannontech.graph.menu;

import com.cannontech.graph.model.TrendModelType;

/**
 * This type was created in VisualAge.
 */


public class OptionsMenu extends javax.swing.JMenu {
	private javax.swing.ButtonGroup graphOptionsButtonGroup = null;
	private javax.swing.JCheckBoxMenuItem ivjDwellMenuItem = null;
	private javax.swing.JCheckBoxMenuItem ivjMultiplierMenuItem = null;
	private javax.swing.JMenu ivjMultipleDaysMenu = null;
	private javax.swing.JMenuItem ivjSetupMultipleDaysMenuItem = null;
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
	private javax.swing.JMenuItem advancedOptionsMenuItem = null;
	private javax.swing.JSeparator ivjJSeparator2 = null;
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G24DCDCAEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8DF0D457F596AE6EB089EDF10609D58FED29A3FBE8CD9A59D6E6E8C61EE0CCE7F021986A1FB02D12282DBC21AD4E5014E908073ACFAB2D91BF927AB388CB00B08289C194013123A8AA86A10404C4A4239F8492C2EFF71F340B5E7E686DDB3482E1FA4E3D6F5C77F67DD632132219B3775D6F1CFB4F3D671EFB5FBDE71F2452C30B1D590ED2C94AF6CA465FAF33A5E97E99C9DA774993950465F5EB02
	0FFF07010A24C7BD39D01681AF7D77B4E72174E8DF9E54CFC2BD65914D398F1ECB242F74292761A1011E96607D6C4539AF4C2C277EBC69594E6A4FE66F0632G289E30DE20455D287F656CB2830FC23D40F8C64AF6C8D26727A8734C41B28B1EE548D5826F8C722F22CCD6F60541DF8858D220AC202D1F1AFD6EE023FB343141288FBD36D85A72373FF26423AECB7FC483D7F95BE9232C14DEE4064811745EFAA81B3A47474E3A037EBA379C8884833A6276560DE9F248DB67D782114646CB785C9552FD41C0788A
	A01B58B8579F4CE3FDC8D29A5423BD08359FC25B5443F39B5088909F6B7A7CA61A5378E2A9739BA7930EB53D070FB133479CEB673E3D4E853D58FF3EE337512E926A83C0FC9E7953A73E40BCACD310789C7A9CB40757FAE98E936F230E5DFD1A338A28290F66703F55B40701FB5F12363A069CFB924C21374F30F71FB9072A4EE7F276B37FC9EFC69FD8863C8C204DC0257D307EC03DC0654CEFA74F57C2F95E25DF887A7D414079D028D975E8D7029EC5056746C6609858D5459D54BC1244FD730D03A7ED46C324
	76477A2569CB57A4722525D71F16F47D2A43E9B3373457FF6DC8B27AD858EF4E6D35FE345B8BEC6D5360995B6DC21BB2FCB5434B2E19B8DF4BAE06E33D81F80B2E111D878AE8AC1D39CFC80527069D5BED46123C2247A18D70B1148D18E361B67EFA8B5A3885709120A8508220D420F583E463974FFFB81B0D5B410734002C76C4DCA1D9531BA3BE5523E8062DCB9CB3595A0EF83B43DF3A9D374B128F2DEB14717CCDDB3BE4ACE9F6ECB26C11FB5D348B57FDA3216E2A6B1F770D823106EF32BD51F01D2F21D360
	1B995EE941795ACEB29C6BA340DB89445765CCAB1EEDB5B7B4E78B5090108E3490A895E8658DDA1B5D4D3F78027E5FEDD9975E76EF1A77C229C4F5CDEE8F04F5B960D67AE54DA73BD4A5AC99B2EB1B519E2E18F62F32FD69869D1714002249F8D6C06F32DF81A788F3190C66A2145194466D757995D0608FC954EFD6F3896EB3B92C7705BC322E18921C3F299957720E5C8FBDC28770DCBA0876FA0B1D25D9B0FFE910ECA5DFC2ECG6A21C1324B5805E2A8BB40A995353131456746E1485A947956CB0760A5A74D
	8579C5C0E9C06B00B68D525E7A71G79D0CB7ED352495707ECFD8829F0087BC34DD03C8F2DE7BA3A81570112015201B207687D58F7E840FAE698E34071A4726F39B37010B6D978DCC7A0C647F6E30FB8E768A3E716BD364B724C6D7FCA9B7A30F4D3F3D6GB55D24B5F872C2A5774DE6B92C10B7F6E960C80A47DC91AE7BE7AD780ED06EA8813DC589A9818FBC048DDFF83AE507692723C161C3CB990E674D85F02430401FEBA962B874E175FA62FF0771E5DD57FC2E082E74A83A6E8B0C196DD7303104E30E3230
	B157756DB857DE18238AB4AD665AF1314462EF1D4160F8A4540BBB49705B89767EDCG72CBEE527A7F7B7B082D01FA16407E0BE1A8A38D93F67FFD6B9A226CEC67601AAFA0936BA7DF0F51B113FFFCD1BF31923751595644EBFC3155FE0DBBDA6D57785356195770FA2B5D9AF25EC8AB4EAFB899307804520A6705BB9888A84CB1C593095F1BFE5C0B737085FC7AB51FE7CC51FBFCF795B84F07F1EDF38F611AD541F343B01D9F13C7687C283A7B675272DB3F343DA7A4BF556FD8B04C57AAE558BCBF7818C6AF21
	2FA92A8217B2B85FA3811D63AABB6F8CBC1C4330D307F0DFDA6FED9C7FFE63CEA8BB15B1B0B7038D7CB7FD05D0DE09283AAF24AAAD7294EB44F9EFFF0C63382A0403EA847B33367B660377203CFCD57678024D91DD675CF6DD711F9267C1EE870EFBA54CE7835066E15AA39BAF6D376C9163A03890D4D5EEF2096E7E6FD6DA6F7EF937F86CF08ECBA0AE337D281D0C8AE51210B9DBEDA7138C65D2A193B9EEA733924A8DC015A3A813FA2254A2335910B17D7A75B634E5D81908A8708A45CD5F21CC51FB302DB80E
	57AF2B91737D1A55066B26DA4E8B0EFF2F5DFE6D3F5F0EB15638687F1F5A5F1570BD497B643E1AAAE31FA14D670763958FC0432E739C5639FAE1EE98FF4D07F2B1504CFEE46FAF3C5F46F23BFE17C13986A80B59307C90458FF8DF6158E9066184CAF2696EF625509A2BF942DD367B063DEFE53E86CDF27C1D76D71D586F90602ACC3EB7F859FABE1F8F0703EE9F3B1A98765E59E15DD39C5B5301FBE754270561FE6BD12234E66FF560D8716EE240065D2F6F44D8C444D6BE173FAEF247DFA73B74BA4563539B9B
	AF234D7AFD61082C32E5B1E22B3315F9C69FC9B245D63DD5387E4B203E8EE8A390177D64A849CAAE127D6AB1F66704FAB71057C532F72AC53FC2F6C58D6B976A6B00B68A59EF9E977D3AC9763FEBD93FD06F86723AA9067C573D9FC81F3FF3DE04A986B5919038F1C373B53D5FEA0DE9537734B8F6A5D853A4B7DF4B45EEF3CDC7A09E64FAF76C5BC7763DAD5F1123F561C9B8738D339A367C09036611A966F1B21745D59E4DD98F546621B9EB65A4BB62A1597A83A81B8C75D420D59EBA4F2F1D367A65724C8B89
	7DB25343473E45936F172B2F203EC28562E5057CF2C1C71145AFA1C605EB78981C82621D591943773BD1EF6EF452FE17685DFD2152AED6E9D0387ECE6573F7819E132CE2BEB6227018A4DB606B992EDAF09E2B5C28221CC294F8998A5DCFDA0E5A69BFB74A75760EA6527FBF4CEF3D23717A732A9157C76375BFF60C74A781AFEB14745F2B36535FB646758E0DA5527F71F16CAFB4962F7F9306CF0F456BDFD5C37A6781EF539869FF6E381D7EEEAF576B71A652BFF1827B0BFA63755FE578DCEF3C7E1C9324FF81
	7036F8A97E496B427753008F628C2079C0A9C0ABFD64E3AFF74E9A3FDF0C4E903F7F74F331F096743D5517780E4967114E765D6E5BFC9EC5827FBB06B7D8F0BE3F538C473A87F81BEF13FDA74B6D6CBBBD4E75AF98CFE45F943617D346391E9C017F954353ACB8577FB143313E92F8B5AA59373A9B776DFC28AFD24926C72E64DB76EDF7509750451EDDF7855F49C14DB7668B08BB82435CC10DDDBAF9CCF0031D7BCB214FF4A00EFD17E12B201E29527C57D6C1E12C4577AC4FA3D583123D7D7FBF086B18FA28BA
	781C870F129DF27DE4077423E407C2BF5941D9ED4E3D40727C783107EF0754072A034F79EB55E407B3428E7FD9CDF6B8A76C50F45C1C7BC54B735A1A07EF079FBDD49DFC4EAF333B0887662B8BBB5455129D12049DA6CF18F3CF32BC6FB871706D7041C355414F0C422E4471723EAE7BF879C017FD3CFC24EB661C48092EC471B437FFC18E5ABF85EC3FCC2C49B5062D01FA06D813953915D0EE04FADEG6EB9C0EDC0AA50FC20942074G493E4AE4B7C0FDABD0D99062E7208120A8D0B2D0FA10E49BF213EC6C04
	FD4EE4471484FC9C1FEA707262E452636AB487F36D87ED4712BF4B1A6EC900637B06F2EA71F917A29B5F5BA1D9774A9F32BBECA658E9F3D01C996534D736897B7D65810C9BABC3D4CFE375B6D1EFBAB0E0709F5E1E69E3BA3ECC5E684B6F19135D6CFEAA6A752CBEA5D17EE22292672D4346710670B74CFF6B17A40A5F1E1D44F59855E4BF38FECFC8F1CB52845A738D110FD8EA58E739D1E6C2497B49472999722F0A5CC45B04F187B54A6989123FF85D145FD9C3720B8C39D423DCB7C1F1F9667158F1FBEF186D
	AF56D27B4DC63BDC0D17D59A35BF4A762ABF20408550676E5530DD9343FE8854A26482B931FA1287A5E13B35AC17E164GFC0AC6199F623F6D43BD50B229282A49B2F89777E25E28DB8D6A5715B05CB5BD725467E41EBFD7E648DC71852E48D11EA9A1B1AE33E5875EF5BB033227A542FFF908653F445670F20F82C71D8C716829D972E07639AE1E7759653466FD12B51E3BDD25D1DEE8C33E9DBFC3707F34400E3FD9708F965A71F343445FE84BAF937C670A2C7C13E14E47E71E1779C1D1FCDE86793DA0678DD3
	7EEBD3291DCC9270970649A71EAA0F75094CA10B6FD6083D81724B01B201A49D4628D37B67CB709E8D2F14C8A8BE01E42CBF1F4F1B872C73B927739C38D727987C15C1EB8CDE7844078E0A0F624F02E91D5BE11EEE4660BC665F466E5758A69DF8DBF444EA8FDB7562190B172ABAA04E7F204A4A4733623B591C4F47357AA865B7362DDEA2295F6EC8781BFDD3040FE744A837BED2AE55FF6DAD879F5B4F0E525812A3B4366AE3B30F6D7A317831911F77D9DFC3FD2E93FD8E574C5C67021A78BE5F4B3176B9DECB
	FD665D21BE7F38F666BED757A66A137BC8CF8E165541EA43260BA72DEB5D3033BDE13E655C9DA34FE0141E34F263BCFFE94F9149B6DFF4412B38471B03D16101FCDE4DCE4AD14E3FC3B922BD85042D94586B825BA6303EC2424ECC92F62808B0F512F2E70F699576E392A796EF3B37185A26CCD2EE6AAF0AB1BE4D007A96205DD15273153DA4DB95A559837BD0F6886ABA50FCA1DBDFC1B92F14A82D1D37423AB6EC9DE2566E090A78351D13ED652F0961637A6D09613FD3913F76DFB5785CA62BD8CC1DA02FE7D9
	1F8A719DCDC614E27311E174967E2E2F3AF9944AFE7936EDC672B2F45D653A2D3875DEF9CC22BBC976615F6E9B039737531ECF7B4E1352325F5F15B027D9BA457D720CD1AE18D34EF0BE7E3F3E05672787F8C9C0A9C069C06B00B6CED12E6176CD9CA3E6E9E0C267F5DDF6FBD9161AE6566FD3A6CD98AFAEFCEE6F586665E6A25E6E231A5F2EDD28764B3663B6489841F55D8673AD3BCBF3FE766609C4EB89E037968CA91ABE45D1F6CFB046F049E91B8B3D1B78DE4B5B159658356B1C355DBF58369B19355D0DC2
	3BF67A2C6D2A0A6C5A4D1B35DDF931DD3BC5B33463393283A5F80E245E0D7F1E250661AB6F46FF4F722DE27A1EE583701A6ED1FC71C7FB6D74AF3EB71B7E4758B916F60F6B59AE70BFE1789A8B4E759FE1B856330057B6CD7ABF2A306ADF8A78726919E39D639BA45B6F44DE1B21AD776937E5BC73B602DC6EFD4DD983548454FD1F7C7B074346EFA8B67B7922E7CC3990E4DF6468621B04DB09F381AF5F1A7DBB1E3F3F0577DFD7D05703FE6C1F708D37F01EFAB044C063B746DBF846E93EB12F4EC58D7CDFEE61
	BED515D1869B3FBF0E601CC55E00EFC5C3FE63885AGFFB377492A6FAE3B2D7700E0E00C4729436CFD2B02CD266FD36CFA220030F9827B8F06AD04FA0A40A6587BB6956A4B8576EB763EDD83758C011DE66F510DD05FFA1FFC61C889E2391FE94E2A4F882BE3D89B543D827BF6716F1A63417E6D70DC21E36D6F2CA351379A781E64B6F81A5D8F66014EE42177F136CFD2203E9CA8D3605FE03874G6E6FC0679E90FEFCDF22F1AE0F1BE3CA02B993E55A70390E6604BA6267B8DB8EE13B8D46ED51496EBFA3B02F
	FF9458278C0B82F6CAE08D87A92F3270816DD907AB719E5C611AFD8F963B58EF4C30F7FA27C2467E5967E23F950C0E06953157ABDC0582EB8FF78743BE5CB97C1E9CF14646E229EEDCAE7E7C2AC71268BE1D37A7D62EFB54644DAB0C650547CC5E5522D85ED22F49EBAF0E653D62B3F9E7F7E3EE20479B1C6431FBFCEE60271F6DB578983B374AEEF8EF1BB2BC7E9BA9090FF7B187387881453B47CA6D78E98FA84EFCE1EF6C78FEF25B9CDFFD912E5DEF0281652A19D2B0F87D1F601D36C7F187839EAB175ABE79
	9172E1DEF040457279385E58E79D572A87BCEF39CD0C7B4DB2BBFEE192711F59EF472FCA2218C1E63FFB9A6768B51FC777F23CA21AEF62ED8A1E5742975ED3F16E7C5E7CF8895D1B9B12A88E38D9CAD8C86879414F0FC93677F01B2FFD286F7CB20AA716083EAB76D35F19827BE701154D21B3E5D1096D6F26F3E63B4B38CA6D5AF9E6EDD7DC066D22F1F273E7EDF7F83F6D5DE556F6BF32ED17B6C3BBFE76BCDFC26F2CD5F368BDB6D5CA58EB82BB4EEC1F8975AD82534A682C4FFB0430067D04158A6C9D01D5BE
	C2B94986F4E943FF8683665EB9477A8CAB781D1662613943EBE5341EA7C57B95C1337D6D8973798FB4332FC0995B23DAC4617FF7126D5E85973825EFC9CEDD5AEEFCF624CB6F72C41DAE65D3422892B3C63A34534C54D96563458A59BFE2D41A29CADD5A4D7212151D32CBD1C122G530C15A29729CBA58C00CD7E8BB83DAAF96A91BA46532A12D20D1601458CA40F65366A6A6AF429940FB86C46670F7831ABACE1C08CE61D7133CF17F271B0029E7939838F7C00B1624B638F2C790F2D0F56A64CFFC8F339BF15
	4E0D7F5EE4DA41B56A86DC1DCB6F30B51E1FCB09E3649E3F2C2A3A2C0E1F8FF8B0DEEE1E529573B724B98A769FA6990349F973949C77EDCB1B7F83D0CB878860A349358992GG34B4GGD0CB818294G94G88G88G24DCDCAE60A349358992GG34B4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC392GGGG
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
 * Return the MultipleDaysMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getMultipleDaysMenu() {
	if (ivjMultipleDaysMenu == null) {
		try {
			ivjMultipleDaysMenu = new javax.swing.JMenu();
			ivjMultipleDaysMenu.setName("MultipleDaysMenu");
			ivjMultipleDaysMenu.setText("Multiple Days");
			ivjMultipleDaysMenu.setBounds(197, 192, 135, 19);
			ivjMultipleDaysMenu.add(getSetupMultipleDaysMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultipleDaysMenu;
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
 * Return the SetupMultipleDaysMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getSetupMultipleDaysMenuItem() {
	if (ivjSetupMultipleDaysMenuItem == null) {
		try {
			ivjSetupMultipleDaysMenuItem = new javax.swing.JMenuItem();
			ivjSetupMultipleDaysMenuItem.setName("SetupMultipleDaysMenuItem");
			ivjSetupMultipleDaysMenuItem.setText("Setup...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSetupMultipleDaysMenuItem;
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
 * Return the AdvancedOptions property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
public javax.swing.JMenuItem getAdvancedOptionsMenuItem() {
	if (advancedOptionsMenuItem == null) {
		try {
			advancedOptionsMenuItem = new javax.swing.JMenuItem();
			advancedOptionsMenuItem.setName("AdvancedOptionsMenuItem");
			advancedOptionsMenuItem.setText("Advanced...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return advancedOptionsMenuItem;
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
//		add(getDwellMenuItem());
//		add(getPlotYesterdayMenuItem());
		add(getPlotMinMaxValuesMenuItem());
		add(getLoadDurationMenuItem());
		add(getJSeparator1());
		add(getLegendMenu());
		add(getResolutionMenu());
		add(getJSeparator2());
		add(getAdvancedOptionsMenuItem());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
public void setSelectedOptions(int optionsMask)
{
	if( (optionsMask & TrendModelType.GRAPH_MULTIPLIER_MASK) == TrendModelType.GRAPH_MULTIPLIER_MASK)
		getMultiplierMenuItem().setSelected(true);

	if( (optionsMask & TrendModelType.PLOT_MIN_MAX_MASK) == TrendModelType.PLOT_MIN_MAX_MASK)
		getPlotMinMaxValuesMenuItem().setSelected(true);

	if( (optionsMask & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)
		getLoadDurationMenuItem().setSelected(true);
		
	if( (optionsMask & TrendModelType.LEGEND_MIN_MAX_MASK) == TrendModelType.LEGEND_MIN_MAX_MASK)
		getShowMinMaxMenuItem().setSelected(true);

	if( (optionsMask & TrendModelType.LEGEND_LOAD_FACTOR_MASK) == TrendModelType.LEGEND_LOAD_FACTOR_MASK)
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
}
