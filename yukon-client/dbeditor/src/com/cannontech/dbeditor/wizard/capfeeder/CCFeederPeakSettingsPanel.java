package com.cannontech.dbeditor.wizard.capfeeder;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.common.gui.util.DataInputPanel;
 
public class CCFeederPeakSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjOffPeakSetPointLabel = null;
	private javax.swing.JTextField ivjOffPeakSetPointTextField = null;
	private javax.swing.JLabel ivjPeakSetPointLabel = null;
	private javax.swing.JTextField ivjPeakSetPointTextField = null;
	private javax.swing.JPanel ivjJPanelPoint = null;
	private javax.swing.JLabel ivjJLabelLoweBandwidth = null;
	private javax.swing.JLabel ivjJLabelUpperBandwidth = null;
	private javax.swing.JTextField ivjJTextFieldLowerBandwidth = null;
	private javax.swing.JTextField ivjJTextFieldUpperBandwidth = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCFeederPeakSettingsPanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPeakSetPointTextField()) 
		connEtoC1(e);
	if (e.getSource() == getOffPeakSetPointTextField()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldUpperBandwidth()) 
		connEtoC6(e);
	if (e.getSource() == getJTextFieldLowerBandwidth()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}

/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (DistrictNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyNamePanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JTextFieldLowerBandwidth.caret.caretUpdate(javax.swing.event.CaretEvent) --> CCFeederPeakSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (BandwidthTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyPeakSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G90CF65ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DD8D4571564EBE24956F44DD6731944EEB5B18929FC8D7D6236E6CBB624EBB7BF359B1B1A0DEDCD16EE5DA41B5A545D38AD4DA6594B70AF7F080A094AA8E0D089BF8A08B0D1109F79D39190D1C760B1730051E1E6B2738803EB52BD675EF76F3C7903E01ADD793E43FB771CFB4FB9775CF34F3D775CB7A4B7FFF1C22CAE17105884227E9D09A5243C0910C7B3DEF91D63B616F5A4914D5F9E00ECD2
	549D0F05720E24D6D7C7D296A94CC806E2AE14A32EF4A4ED0777ADC47AE8DEADA2834B99835ACB2BEFDFBD3D1C50F4AEA70916BFC9C903E786C09E608CG2165A87F6BA9F9AAFE884A996ABBC22C0E104E8A2CF3E7E11E863FCA2D2787DAA540C6DA6761141DAAFDBE1431CEB8BC23AAD85D3570CC215A5DD6DB2356B35E3918ECFAF60FAE9DE5E978F328B9445A9A55271EBCCC8D91C7B2EADA61E9683ABAFA4CE49FAFB5C9B61B5D26482631D27330EC36A8F6E76924651A64B48349B1A24BE659D9DB5B5858C2
	5F3AE469EA0F2CA8965B282BCB324956D507D07F6F97A6D35E20BF147B8FA32E2C88ED16856F45G7507F99FFE1AEE60FD3D3A18C45F1F9B348F0B8EB35D238EFB7A30E66B144ED1097C1F27E3026D8814EBG18AE6FED663AAC2F643A6468316E86A847D7F5A46D2C62781FD3FCA514BB814CD5DC470FBE60BA767F4FA3247EC1D3DCE6909D2FD7B15D16D6F9F4ECCD1E526D20FE131B09E39F8D34786A0E24BCG83009160BA407B54DF1FCBBFG4F06E12551BEBEEE37B5B89CA72CE667A93BD93642FBEDADD0
	D4DC37EC32BB4D04B01F0CACAC89208F0779555E7E08407AF61EF09F8A6FFE10141FB347A584685B72978AF4E1AA0F48EACF5FE22A51EE775331DD876F31AADF060F20F843918F1E0DD59545E3399FE811C7381D7BB338AE1D7177102C0A96DDD2GDD967E30CE272FE1BA5454F8F4E1B6FEA38BEDEC86FC8840C200880055G9BEB380DF7E49C1E4946EDB0871CB6495AB3B16C101C4A09890B957CDD35755A1469EC9D88D83BD7E65D0E59A56C30F60C56BD5A9C50AE6B428E68520EB2FB949E755805494E5F9C
	CCF653D1FF5F4890E3B8074E097E23EC8C6384FE9E450FE970ECEC73A89E4BEE202D83E075CF6DC1FCFD2D2F0FDC24F8E32D2F0F7C331E7B089BE86BG58784E4D4158D8DF877386C081188B908E70B4403ABABE46660C324FB90F3AB4634B5A6E4A5C86CF592DB825B61BCB11ECA639CFF2DA24E12B6CA2EA1DFD19E857E12FF687697CB6010C1332CDF6CA0A45EE836E52388C4E64E2F5AAB333310ED32654DE4B388C82469D0473B5E4EE0127D5F2A9679CE6C911BDB5983DA993ED720954879C01813C179850
	DEBF23B177ED68FF1E015B6A31AD08F3C0B954406D72C4E68EBCBB4039E5EBEDED134504EAC84EA96E238F97E541F39154AF7C88668EC065B1BEBF5F6867FE58147EGA9F921A9209F5298FDCCDD2B0E797A6177A8FFF23CA3C98FD08FB0F41C2F2BF5C57D33189BDC3FC0B4543F7218B70D49681D150C8435DDA0DA1C6A1FDA9C337B87DB51F7C320DF0BGA20FF35B3F1529E7BEF9C2F2495C8B4FB84101E433E7A4D85DBFE4619AA3FFA25B14A659A15B4C7062D2FD605D2C940FFF0E58056F44E75379837E5C
	888E6492784D59598C8FBC345E4E69B914AEA90A53B2BC2148FCC597747732D19717D7A8F429FD5D3F8D7B3A867A388160ED51577B3337E87C2C53EE3FBA61684599247AEBB5DD17336AE1BFD04F47FF2E9E77D91D22FC97ADFB683B753399BB64B37B98FDE4D6B262E6D157A323E78F4E819EB7D3D5BC238DD11A823AAF82D88930C1747FDF29EE319A395CB7D3E729C7F05D5E25B262D45F67EB10D6569781669BF76584776DEF6684766D7BF3827B7643B9537B6EF7F38279AE232D4841BE5AA7ED1A3970F88E
	46C7135DE613690494CD885BAF3F510B7D3058AC4AF90BF9D4D6FAAC57E45863B420CF3FCEE3F0AE3C97B77018B9D940E3E66135A5A4EAB0DF97E8FF357001E33A1086662F739BBCB1136974F7DBF00E49D6F99C8C55E81F30A98C3F1246F7956F0A233835435730ED313AB56F32DBECCAAF183345A2DB4DA5A60881CAA97D4F161762AB52A7123B54B589C6AB25A3D25A0854E6FC5D2F7F7043E37A201B7D12910BD3EE33B9A6B82F7DFD7B4B196FECA13725FDCA75B8B3B2727F24CA85613A3CF6F6126AF2C232
	19A7ADE6E56C7FD00BAAD5070CC1E60FF6A13B53BEA9BB05CADF3EA63867E5B17B166D68CF4B414F1E8608EE60B13BBE9769BCE62B8BD3235DEAE5D381E8CDA7281F9765EAFCB726011D01B384FD5B3E6969A7E6205B8252671E60FAFE3895E324CB7EF8C2062D99AEAA9D72945FDFED4D712175C956894F3E4D3095E325D5339EB1FC593640F32CEA9B2EC1D7857F235B92896E3F98CF9693A2A46469F0DA46E1794685D6BD6B1F5A2CBD6BAF867DB1AF908D4FCDG4CAFA2CA5066C187925BB5A22F79FBF8CEA0
	8D4A05GCD0D0CDFC8A3339D2BF7FEAF2F37814A71CD416A259670FA05D047GE0EC8AD46F9FC53D05C00FGD86DD3AF2D996B459671F3F6194065D19CE6D48EA670334C29ACED4E22F0F3FC4073A6C2D7B363EFEE76BD63FDAF8F795E8A78794D5CA79E5D265DEFB438DCF61305EE3155717D6AF6ED2CE438395BB74393260A8B4EFBE6594DFD642F36232E3887D7516AB8AFBB789171BFD3B453AC895F4EB5CE527959EBEF04E3C0AB38D923D8CA7AACB246A1E607CFDB34F90F484C92DDEA90BB04297DFFDAE3
	076824A99D4BEFBC300F5BBD3925A30969F9F7AD4E75C0B99D93D53CF4F442D2BA21D870GA0A9920D934C5B547C8D5D1BA12F7A961E9B79EABA4E8FB7146781048130F35A63747C06F523G9783406670ADFD5AB9DCFE9278GDCBF497D3EF0AF7A49AC031EBA8EAB776730F17389F15C3677ED29D7652E7A4EBD447027F4DD20FCCDE866F65D3C134C26ABCEFAEC9B7A0B58381AC7F9DEA9E4AF6FDFFCAB4C13D65E3FCF8F846A1F524A6DD2D9405B0582AECA34336F8A54AE26155B655E029B318B137987A1B3
	2D8D6435F11987758147220D4B9CA83C9119EA7E6810F6BEC7C59F8C383FC118572666465AFC6733318A7DAD8A70EB5A78FCCE4F4B564C67EE18C112ED9422B51F23BF48C35DA1AE2B65A772D0AE0D4B042FF3F7D7E33FDF831E716D9DC9BB81AA81FA5B390C169DB366011A5D5364013E3DEFB6799C251D6F63A37DF2B1216D1E334A22F67FF38B4B157C971D03916D3E3918048C44C7357B66E26EC86739185558778EAE7F6F77784ADF5861ADF3F9C7203373CF7D5A157A14577A34E3733528F8A70FC79A172A
	2D556C0568FAE948502E179BBB5859B037931E1DBC1F3B22AFD955273E1367B9FEF784793B21BC9FA09C60E9008DG4927BA12CACE7171DE30A3D07C769E6FE0415BF3E6E63618337746ECB85F2F1D2F8DBBCFF937E37DDEFE00775BFC0A0FE3ED410D69506A574ED707C581F5F8F09757E13950611D9B54A155AFDF6D2B430600BA8C6B398E71277978179DC51C814A1D2779D87F478E3CC76071031EBD880F9DD2956E616CCE4B284546F1A394E732BB69E1106116D487BF2706D587BE27FE27BA70B9F5F97574
	B918486AE067D816AFF9122E73C6681F9BC04D7951F5FAAE14978B5C6FD39137924A6BCE73B10A4D2077D7E7F8B91516F18DE065EF244FA667918810F7B09A13917E05E5CC17EB59C5694FEC4B24B6F639E4971D48383BD1E92FAADBC900F37DE9E9DCA67C1C78561E9435CE33E2EF4473A30BDFEF6E473894A0FEE96E3C94FA77086D734E70BD5302C10C3FDDD09E83889176FF3CCF6B7B8BFDEC976ED30EBA43FD7A0FC7027BA329896C0F09B5B88FDC768927C9767539741ABCC173C43AB6C59EC73330BA5BE8
	FBC5F20E4AFEBE3B0B36E734E06D77558473EBE6734777EAEDFEC8784BE38734B6DA63E31357FC4AEF1F61FB190F8B347C2A853FE53B347C323A3C5B977A14EB3AB83F4AC2AD3F43025F28DE4B2F53277D10CFD96962E3D8F99457E83730E18DADCF89DF139BB204CD1BEDE6B5F0317354DA9A836E8445E2E3397DDE5F0CF14BAACD59A7205E3AAEFA96CB552E2321000BGD8296A4464FCF7924F9BA3CEE99C062347A11B480A3358B6BD0D37AD04F28DC0BF009BE061D9EE13DBB331AD73FE26A03349D4863789
	69E632F23BBDF6A806F7C33737DD587E25E8084E13EE36FF499678EA0A3F2E4133FD4D3DF4DEE1B98CE815BDBC762D21FB03A8A82F96384D6DBC9E2E97383B9390D7595B11545A4BF12B697AEA0432D560FEE364EDC384EE55EE442D0672FA017BA1DD97E3201CFC0E6376DC66EDF78A5C6AFC44D9219CA2F01F5175ECBE14178A5CBF0836AB846ED95AB6398F787571F3DE4510573E2AD41AD4EAEBCFBAAD669352E8A77588961BBE20EB825A303C0F4726F329747E8E4AA1G8B7BF83E523D0F1FCB439F3C07F4
	06B4873C0F0F6EAD51C5743131DC5367594F9B137939744EB4AEB36BBC5879BC621EC8531EC35017164532F51F517F5D0B0E63DBC0347402F4AD9D75DD2053524FF95151C63B54762CEFBFAE62FDFB7B47E1447AB70703FEEBB06FBC6B53F2756968AEA03DAFCF6958D84C2331FE8350127B7978B45B9037934A6582B7E6676358ABF0210908DB8A6548FE3E9E9C9C5223CFC1F9A3C06E8570E900FEGB740A200288B3C2E01560D3E40F2C064A24B7168AF329C8E66E76AAF723ACB86C3547EA0EF64F7A36BEE14
	26DD2E663DD2554D6D43977B603F7E2F943A70BB0F9B51A55B072F5697F0EDCC77291F60D3E6716B65A15C7F0C58ED4A620BB8A66F5038B1846FD6003CCBF47E36B7CD23C7024A0B43FAE89783D020525200472B44F3D73305C4A4203EA63BF5E25C66A2CC6ED3B48E214EC31744D9BC51DB37BF523864G7A75CBDCB75D6519F4536A97F149DB3715FE3A7DFA373FEEEF6D663A2D963A5D196F2D5BEFE85C0B867A8621DB4D0C3AA5E9F4539B3DF52BB47A6A76EA3E3FEE6F66F35D6A0DDC3785FAEF5D1651385A
	85F423116B76520C3A25E8F41B67235BD2BF5DFE95C0379821DB04114F43125459FE7741563FE7E80C0EB23275AFD6601F2778D59ABCDB97AF2572EF2056824DF0191F0FDE2F45B5FDB114A38156GECGC89B00F375G5F17BCFE341C4CF0B66E35BBBAE173E9559CDF54FB5A349B7B2E6549A49E0F177F6DFDE4451CB7E30365C08D836A77EB6AB379EC8145337EFDEA407E4D85DA9840AA0075GB1GD9033CFF8935A52CFF182EEDD09449B4C6B3413CF3181C7520F1C3444678218C9CCB07D39E111DCE594C
	2E7B54310E226BC0A148A89F64FEE834796F47DCB65F7D18916ABB86D9DF18EF7E49BE1DEF4679C0A834DDB448FC134EF1F87FCFF856895F7CD6A25A92B6F7167109F12AB553797BF47D09047ACF8B1D6377E2DC55DE53F5CA43746E0151EF37EF75210B440D2853CD731ABE37FD148D239F581167CF97ECD81DDE1AD3ED272D4EB99C1A5BB175CE3192F9F832C6016ADC3F6ADD477B160D5589255FA6B0B9C897E4757EC999526EA3B68D32FCE95E904F27EECA88C4AF91748F7283516B853DC89F085E8B7431A1
	3CAD793713C458751D36A2F85A64C9BD5F621536C010AB5E6B12ECF04EF5DAC67616684BCB7495157B453CDABC294D4956EC2E881A138D9FD23F29D41F6B3ED2C07229DC18E3047FEDBC6E0F033DA35535270D88393FCA6373F96732FBC9530BEF469684119B234AEB15BCFB476230EBBAF5FDCE40B655C2C6E927479EAF9BE7E30FC0FDE1BC4F1E72704C4E7716373D4BC34B3D7C6711F76D2C0767D63D37BCD31F0776581FC95E1C739E1E19C29E0B574FE1E8605FB2EA5E2554AF1A23198E180CEBDF2A8C966B
	5F2C8D1EA7F92BB6F01E0454854E5BA556CD7FFDC9C6DD706FCB3656E93FAFE1EBD5DFBF4E7D90707BC5GD1GEBGDE1344F7D3F5FB89DD27822CD14D66D14C536097098A17BF47903C2FF798E67E0E71AE835E018E5B9545BE0E7CB9FE118147CB31BBA8D24D079A50C6CE4B6818422A2A78078C74DCAB0FD0349A43AEF87C2F5626777569A68B748CBFE0AC2EB8DA8D5EDCDE3E1F1D9B6F3671F3E37CB0BF2BBEE5235F91C33997408A7079E2941F8B051730445ECF9ABDFA3987BF0FDEEA8EF0A4C88E102EA2
	CC77E5C3BCB7FD6BB03F2FB81848F11182B7104FF11B844EA8725AA5A6FE96FF6E12B60EE75D733E0E6F77FC7D5EECD24F29A65F3B3523B417309470D1A66EE3252D876159A7DD713F4186E417538E4B2AB24530746B8955F7A786F00F928DFC5E86501BBB12EAGBA814CGEE0039G0B81D6G2C86D88F908310A6F7A46D84A887E812399E8B5B76CF23071A48208E6F1434797B017E60BEAF774F6C7336FE7AADG78EA6F14C34DD33A7A691D404808CB96F34B5D1FA5F0ED2EAE3B4B0279CD96477E5AF6E3FB
	7ABABB7D1EDA765D53B7D13CD9765D537F444677748E202D117919A2E2C8AB3FE6040DFB277A9C9A71BF6B33F665095AF60ADAAFD4FDAE8A5A4E142FED972156DB29BE5786EDA7F965C9B7287548281A9F9C75EF47FC6D1703C40C7757681A9F50CFA0AA3643F4BCB3FCC5B6A93D5228E8F36CB27292F11B3931CAEB3E0CB1983F6133C8D64BB57A19C88FD4340DB27A6D83417DEB5E404C7EF577C060187A2D817F183AF4A0F0CC7D7600B626B25CA383980BC6E1DE740BFA4B856E0240BDC6F9A26EBC5D55F27C
	93D4AF44374AA81063FFC4751A2438FE95772C86F7C145BDB700F3843ABCE697F7E4EB8668395BE2353272DADA47E203A9A36C719255516210B4FA7F0B40DD903E52004B317A5E2EF97F196D4B5F17EC78D2E5305C5FD3C3BCBFDDBC4A730145745B878314BB856E0EAA1EB76C97381309BCBF9DA6F0BA091E65203CCA60C6A55EF62D40DD4B67796962B10E7B4BCBD4AE14BB85EE53A5A1D7605CA2BF9DA6F0FFC1FDB3924A2B846EF783C2AE6058D9F3936DEF3125A32952424F1A472B50DF9418C0ECBD2E13F0
	1C5999F8E955EC4E40B11A1C9B46C7B34898D55F19FC055A4C81786BC27E8E49DB7E4030C77EE752CC729326119FEE7144EEA67FA1EA779500DFA964BFF849DB7EADA60F7CD463CC721326113F89E49C74123F040E9D39826B6F95AEFF4900377CC833C7FE6260CC72D32611EF3862114FF8BF0243216E35F6D6613EB64039468CB158BE2AB95430364FE358D25F9F3D6A61737B21597179A72B078F0BA90C4FD7AE4D0E8F7D090E7A7E0C53436733015971F9F1885B325F26BEF9FAAE8CD078EFC9024BC9523D5A
	7FE8C955AB2BDE6EF3451144B63283531ABDF0764355CAB6632D367A731F96126240C48AA6F85AC83C0BBE92DC4A14D56EA439A35D78992CCB114D8DFC9DF4A9E4EBA7CAAA436F7F4ADAA5571802EBD7DDA70937618F15C88EA3DBA55BE899DB946B5C587758862AB9F585827AEE217AA217D241E54B8FB20B76342FDF91C7F4ADE43355AE19DBA493AC55EDA4C599F34A2EB13B556CF69C2F270C12A751BB717899876C5E975D474E16E27EC6A8FD557D4D39692F25376888F1BBC9992D099EDCAAEA189B47A4A7
	08121DED8A7E704A6EAC9434DBF6AB7BFEF2F23BD1C7E255CEFECCBEA3BA37C3812D1CF02A9E31BB47C1833D0F861CA49815FE631972858F3FF511B272B4D5C8BEB399B4AD9B156D63E58DA3A3961BAC98007D007DAE419E6B147A5449F15F39357D057F9E0671EEA2F163656574FF857DFF887FD7D0CC8545D49C82A3EEA4537E79525DAE6CF522421C847449247A60B0173640C8A93F786DB3777D5230F183742E05A4508F694058D91EDA27A74607E5671C70853BD267C7FF8D474201AE4179A60EC863966B94
	98D5E4AA5CC85A6696423073EDF4E7907D49615A38DF5F9F27A371EDA4116D1440FFF13B837ECBF7C3EDA48EF6CA5475F026612E82F706A9D49E71F3C844467BFB962213A9F1B37D1FBEEB3BE12BCC5AB69B8562CB2EDFA7F7B37362340E57A89BF7D3AADB64D1162FE96F617326D4364423EC12C6D98C99B97E2776062F7F7C45704126DC9D5101DF0D494C1192E8F2173A922EF6AB8547D39A2EC994DB4A6BA8A493B3557A9ED95153DCF52DC2361C9999517BE133E89ADAAF12480822D9E78D6A66B0EDA2DB13
	A8354EE3D76CBD9EF451F3D3EFBECDCFF8B4CDD4B555131B46DD351A36F9CFACBCC4624549CD9232341AF6F94FAA2E291AF75A7FA7ED5EA966361C207787F3DD6AEFC5DC3EF9271DBD5821F5000FF171FC0F357BC3927C9C5FB3AED92D0AE43D5AE0B363197E4414A2FB722CFBFA103F0B57D1090CD65C03FADF513479DFD0CB8788D00FE2G7C96GGD8C4GGD0CB818294G94G88G88G90CF65ACD00FE2G7C96GGD8C4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1
	D0CB8586GGGG81G81GBAGGGB697GGGG
**end of data**/
}

/**
 * Return the JLabelLoweBandwidth property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLoweBandwidth() {
	if (ivjJLabelLoweBandwidth == null) {
		try {
			ivjJLabelLoweBandwidth = new javax.swing.JLabel();
			ivjJLabelLoweBandwidth.setName("JLabelLoweBandwidth");
			ivjJLabelLoweBandwidth.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelLoweBandwidth.setText("Lower Bandwidth:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLoweBandwidth;
}


/**
 * Return the BandwidthLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelUpperBandwidth() {
	if (ivjJLabelUpperBandwidth == null) {
		try {
			ivjJLabelUpperBandwidth = new javax.swing.JLabel();
			ivjJLabelUpperBandwidth.setName("JLabelUpperBandwidth");
			ivjJLabelUpperBandwidth.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelUpperBandwidth.setText("Upper Bandwidth:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelUpperBandwidth;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelPoint() {
	if (ivjJPanelPoint == null) {
		try {
			ivjJPanelPoint = new javax.swing.JPanel();
			ivjJPanelPoint.setName("JPanelPoint");
			ivjJPanelPoint.setPreferredSize(new java.awt.Dimension(196, 100));
			ivjJPanelPoint.setLayout(new java.awt.GridBagLayout());
			ivjJPanelPoint.setMinimumSize(new java.awt.Dimension(196, 100));

			java.awt.GridBagConstraints constraintsPeakSetPointLabel = new java.awt.GridBagConstraints();
			constraintsPeakSetPointLabel.gridx = 1; constraintsPeakSetPointLabel.gridy = 1;
			constraintsPeakSetPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakSetPointLabel.ipadx = 51;
			constraintsPeakSetPointLabel.insets = new java.awt.Insets(11, 16, 5, 4);
			getJPanelPoint().add(getPeakSetPointLabel(), constraintsPeakSetPointLabel);

			java.awt.GridBagConstraints constraintsPeakSetPointTextField = new java.awt.GridBagConstraints();
			constraintsPeakSetPointTextField.gridx = 2; constraintsPeakSetPointTextField.gridy = 1;
			constraintsPeakSetPointTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPeakSetPointTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakSetPointTextField.weightx = 1.0;
			constraintsPeakSetPointTextField.ipadx = 115;
			constraintsPeakSetPointTextField.insets = new java.awt.Insets(9, 6, 3, 25);
			getJPanelPoint().add(getPeakSetPointTextField(), constraintsPeakSetPointTextField);

			java.awt.GridBagConstraints constraintsOffPeakSetPointLabel = new java.awt.GridBagConstraints();
			constraintsOffPeakSetPointLabel.gridx = 1; constraintsOffPeakSetPointLabel.gridy = 2;
			constraintsOffPeakSetPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOffPeakSetPointLabel.insets = new java.awt.Insets(5, 16, 6, 32);
			getJPanelPoint().add(getOffPeakSetPointLabel(), constraintsOffPeakSetPointLabel);

			java.awt.GridBagConstraints constraintsOffPeakSetPointTextField = new java.awt.GridBagConstraints();
			constraintsOffPeakSetPointTextField.gridx = 2; constraintsOffPeakSetPointTextField.gridy = 2;
			constraintsOffPeakSetPointTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOffPeakSetPointTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOffPeakSetPointTextField.weightx = 1.0;
			constraintsOffPeakSetPointTextField.ipadx = 115;
			constraintsOffPeakSetPointTextField.insets = new java.awt.Insets(3, 6, 4, 25);
			getJPanelPoint().add(getOffPeakSetPointTextField(), constraintsOffPeakSetPointTextField);

			java.awt.GridBagConstraints constraintsJLabelUpperBandwidth = new java.awt.GridBagConstraints();
			constraintsJLabelUpperBandwidth.gridx = 1; constraintsJLabelUpperBandwidth.gridy = 3;
			constraintsJLabelUpperBandwidth.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelUpperBandwidth.ipadx = 21;
			constraintsJLabelUpperBandwidth.insets = new java.awt.Insets(9, 15, 3, 21);
			getJPanelPoint().add(getJLabelUpperBandwidth(), constraintsJLabelUpperBandwidth);

			java.awt.GridBagConstraints constraintsJTextFieldUpperBandwidth = new java.awt.GridBagConstraints();
			constraintsJTextFieldUpperBandwidth.gridx = 2; constraintsJTextFieldUpperBandwidth.gridy = 3;
			constraintsJTextFieldUpperBandwidth.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldUpperBandwidth.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldUpperBandwidth.weightx = 1.0;
			constraintsJTextFieldUpperBandwidth.ipadx = 115;
			constraintsJTextFieldUpperBandwidth.insets = new java.awt.Insets(5, 5, 3, 26);
			getJPanelPoint().add(getJTextFieldUpperBandwidth(), constraintsJTextFieldUpperBandwidth);

			java.awt.GridBagConstraints constraintsJTextFieldLowerBandwidth = new java.awt.GridBagConstraints();
			constraintsJTextFieldLowerBandwidth.gridx = 2; constraintsJTextFieldLowerBandwidth.gridy = 4;
			constraintsJTextFieldLowerBandwidth.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLowerBandwidth.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldLowerBandwidth.weightx = 1.0;
			constraintsJTextFieldLowerBandwidth.ipadx = 115;
			constraintsJTextFieldLowerBandwidth.insets = new java.awt.Insets(3, 5, 7, 26);
			getJPanelPoint().add(getJTextFieldLowerBandwidth(), constraintsJTextFieldLowerBandwidth);

			java.awt.GridBagConstraints constraintsJLabelLoweBandwidth = new java.awt.GridBagConstraints();
			constraintsJLabelLoweBandwidth.gridx = 1; constraintsJLabelLoweBandwidth.gridy = 4;
			constraintsJLabelLoweBandwidth.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelLoweBandwidth.ipadx = 21;
			constraintsJLabelLoweBandwidth.insets = new java.awt.Insets(7, 15, 7, 21);
			getJPanelPoint().add(getJLabelLoweBandwidth(), constraintsJLabelLoweBandwidth);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelPoint;
}

/**
 * Return the JTextFieldLowerBandwidth property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLowerBandwidth() {
	if (ivjJTextFieldLowerBandwidth == null) {
		try {
			ivjJTextFieldLowerBandwidth = new javax.swing.JTextField();
			ivjJTextFieldLowerBandwidth.setName("JTextFieldLowerBandwidth");
			ivjJTextFieldLowerBandwidth.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldLowerBandwidth.setColumns(6);
			// user code begin {1}

			ivjJTextFieldLowerBandwidth.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99999999.999999) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLowerBandwidth;
}


/**
 * Return the BandwidthTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldUpperBandwidth() {
	if (ivjJTextFieldUpperBandwidth == null) {
		try {
			ivjJTextFieldUpperBandwidth = new javax.swing.JTextField();
			ivjJTextFieldUpperBandwidth.setName("JTextFieldUpperBandwidth");
			ivjJTextFieldUpperBandwidth.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldUpperBandwidth.setColumns(6);
			// user code begin {1}
			
			ivjJTextFieldUpperBandwidth.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99999999.999999) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldUpperBandwidth;
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}


/**
 * Return the OffPeakSetPointLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOffPeakSetPointLabel() {
	if (ivjOffPeakSetPointLabel == null) {
		try {
			ivjOffPeakSetPointLabel = new javax.swing.JLabel();
			ivjOffPeakSetPointLabel.setName("OffPeakSetPointLabel");
			ivjOffPeakSetPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOffPeakSetPointLabel.setText("Off Peak Set Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffPeakSetPointLabel;
}


/**
 * Return the OffPeakSetPointTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOffPeakSetPointTextField() {
	if (ivjOffPeakSetPointTextField == null) {
		try {
			ivjOffPeakSetPointTextField = new javax.swing.JTextField();
			ivjOffPeakSetPointTextField.setName("OffPeakSetPointTextField");
			ivjOffPeakSetPointTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjOffPeakSetPointTextField.setColumns(6);
			// user code begin {1}
			
			ivjOffPeakSetPointTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( -999999.999, 999999.999, 3) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffPeakSetPointTextField;
}


/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeakSetPointLabel() {
	if (ivjPeakSetPointLabel == null) {
		try {
			ivjPeakSetPointLabel = new javax.swing.JLabel();
			ivjPeakSetPointLabel.setName("PeakSetPointLabel");
			ivjPeakSetPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeakSetPointLabel.setText("Peak Set Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakSetPointLabel;
}


/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPeakSetPointTextField() {
	if (ivjPeakSetPointTextField == null) {
		try {
			ivjPeakSetPointTextField = new javax.swing.JTextField();
			ivjPeakSetPointTextField.setName("PeakSetPointTextField");
			ivjPeakSetPointTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPeakSetPointTextField.setColumns(6);
			// user code begin {1}

			ivjPeakSetPointTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( -999999.999, 999999.999, 3) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakSetPointTextField;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	CapControlFeeder subFeeder = (CapControlFeeder)val;

	subFeeder.getCapControlFeeder().setPeakSetPoint( 
		stringToDouble(getPeakSetPointTextField().getText()) );

	subFeeder.getCapControlFeeder().setOffPeakSetPoint(
		stringToDouble(getOffPeakSetPointTextField().getText()) );

	subFeeder.getCapControlFeeder().setUpperBandwidth(
		stringToDouble(getJTextFieldUpperBandwidth().getText()) );

	subFeeder.getCapControlFeeder().setLowerBandwidth(
		stringToDouble(getJTextFieldLowerBandwidth().getText()) );

	
	return val;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPeakSetPointTextField().addCaretListener(this);
	getOffPeakSetPointTextField().addCaretListener(this);
	getJTextFieldUpperBandwidth().addCaretListener(this);
	getJTextFieldLowerBandwidth().addCaretListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CCFeederPeakSettingsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(330, 213);

		java.awt.GridBagConstraints constraintsJPanelPoint = new java.awt.GridBagConstraints();
		constraintsJPanelPoint.gridx = 1; constraintsJPanelPoint.gridy = 1;
		constraintsJPanelPoint.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelPoint.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelPoint.weightx = 1.0;
		constraintsJPanelPoint.weighty = 1.0;
		constraintsJPanelPoint.ipadx = 122;
		constraintsJPanelPoint.ipady = 29;
		constraintsJPanelPoint.insets = new java.awt.Insets(7, 7, 77, 5);
		add(getJPanelPoint(), constraintsJPanelPoint);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	return true;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CCFeederPeakSettingsPanel aCCFeederPeakSettingsPanel;
		aCCFeederPeakSettingsPanel = new CCFeederPeakSettingsPanel();
		frame.setContentPane(aCCFeederPeakSettingsPanel);
		frame.setSize(aCCFeederPeakSettingsPanel.getSize());
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	if( val != null )
	{
		CapControlFeeder subFeeder = (CapControlFeeder)val;
		
		getPeakSetPointTextField().setText( 
			subFeeder.getCapControlFeeder().getPeakSetPoint().toString() ); 

		getOffPeakSetPointTextField().setText( 
			subFeeder.getCapControlFeeder().getOffPeakSetPoint().toString() );

		getJTextFieldUpperBandwidth().setText( 
			subFeeder.getCapControlFeeder().getUpperBandwidth().toString() );

		getJTextFieldLowerBandwidth().setText( 
			subFeeder.getCapControlFeeder().getLowerBandwidth().toString() );
	}

	return;
}


/**
 * Insert the method's description here.
 * Creation date: (7/5/2002 10:37:18 AM)
 * @return java.lang.Double
 * @param text java.lang.String
 */
private Double stringToDouble( final String text )
{

	try
	{
		if( text != null 
			 && text.length() >= 1 )
		{
			return new Double(text);
		}

	}
	catch( Exception n )
	{}
	
	return new Double(0.0);
}
}