package com.cannontech.dbeditor.wizard.device;

import java.awt.Dimension;

import com.cannontech.database.data.device.PagingTapTerminal;

/**
 * This type was created in VisualAge.
 */
 
public class DeviceTapVerizonPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjPagerNumberLabel = null;
	private javax.swing.JTextField ivjPagerNumberTextField = null;
	private javax.swing.JLabel ivjPasswordLabel = null;
	private javax.swing.JTextField ivjPasswordTextField = null;

class IvjEventHandler implements javax.swing.event.CaretListener {
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == DeviceTapVerizonPanel.this.getNameTextField()) 
				connEtoC1(e);
			if (e.getSource() == DeviceTapVerizonPanel.this.getPagerNumberTextField()) 
				connEtoC2(e);
			if (e.getSource() == DeviceTapVerizonPanel.this.getSenderTextField()) 
				connEtoC3(e);
			if (e.getSource() == DeviceTapVerizonPanel.this.getPasswordTextField()) 
				connEtoC4(e);
			if (e.getSource() == DeviceTapVerizonPanel.this.getSecurityCodeTextField()) 
				connEtoC5(e);
		};
	};
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjSecurityCodeLabel = null;
	private javax.swing.JTextField ivjSecurityCodeTextField = null;
	private javax.swing.JLabel ivjSenderLabel = null;
	private javax.swing.JTextField ivjSenderTextField = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceTapVerizonPanel() {
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
	if (e.getSource() == getNameTextField()) 
		connEtoC1(e);
	if (e.getSource() == getPagerNumberTextField()) 
		connEtoC2(e);
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
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (AddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (SenderTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceTapVerizonPanel.fireInputUpdate()V)
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
 * connEtoC4:  (PasswordTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceTapVerizonPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC5:  (SecurityCodeTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceTapVerizonPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
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
 * Comment
 */
public void deviceNameAddressPanel_ComponentShown(java.awt.event.ComponentEvent componentEvent) {
	return;
}
/**
 * Comment
 */
public void deviceNameAddressPanel_ComponentShown1(java.awt.event.ComponentEvent componentEvent) {
	return;
}
/**
 * Comment
 */
public void eitherTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G20F405B2GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DC8FD8D44735377DCC63575A5676E12A0DE928B12DCDCC2A09F935096DE7DA5A18443E52B6E953463664457762CBEC42EBED62EB6D4B300000AE6CAEFF84655F8A28204B32409A95B522A0C1A51527BC5D3C5806053D4046E5F75D3D889ADE724E1939B3776E6EDDA8366967737B8E774EEF661CB9B3F3664C4C193B12424A444CB4C3A1A1E919C47937BF0D10E9E78879FD5E6EE7B956C9DCC6227937
	8348C29EFAAD831EAE48CB7D234BE8A69995591076C17A40891731985E8BC8CE450AF6F809D34F9C48ABDEE7F8FE62FAFA72F8BDD9B4BD57040572000200A5G25902CFF0929C44117C1BACFF9C7CAB390121806E5EEEDAC5160B5E92C1C8C4F99C06D86AC334AD426642F858C4B90408B8D2C2C8D1EB9D43B1BDA8EA865AEFEE6A1D97F58278C39D817C6BE27198A6FE265E9A5DB8D58916964EA65B1F89E6E3EB478FAFFE0585E6F767B83FED96A9F32FB7AA40FD78E046C235EEB6E10476E112EF87B251616F6
	7A6CF1874FCBA16F35003F5B6D17FCA8B39DF5FF38B11B4A855D212EC594BB58047D650177F12004F42E7F8FF28FAB7AA5DCDAC8D26E78C8DC7DD353195E05992A7E3EF92FE6A6E5207C5F5271C03E9EC84F84E23A9CB1F3DD32B339AE83B6AC5B8AE997D0D0607575084F00F4A25032CC2E63115DDCC757FBDFA39B7A1698CCF1F4CC4EE43AED48D4F54CDE77E046B6EAB3A954B61C1B20DEA09F50AC2045C049C02554D6EF4D5B834FE3FDF2DBE0F8B860BF968C9E77F9C2EF86BC128F5EDBDAA0C7414ECA7D01
	100790E60F5FED34696843E96D26C8DBC4E2ED3BC0387DACBAFBAF1165AFD31B08EE5B52A7E799B6AAB24CD9EA5B3A33305F6E21E36B00F7645B82447007A81E204159D89D25B82617C2DEC9B66FE7D79E5725ABE3BEB1B75EEF485251E54E37969A4633198E89592AAE2C0FFFE841BEDE8178EA2075C0469C1751865415437B78DF721AA76B6313E07FA13F5BF7EE24AF688E4947C73CBE0F94D27A7A7726097ADA0F98DF0EF92AFC2CDF96B6EB47E8757D7769764B6A051FB38C65307E181623768B2B7BF85C3A
	93F3E2EDA3CF0C614F68DCD91A4346B0DD60EBA81E244159587AA80E69A7A06F40E61711157FA21DDB73B6C75B48D7A93EFCF3340DB457F39BD98DF92DDBDCC6B63E4182740B8BA03D8268A920D4A0339146982855484758E1EA788B67D137E6FC996F3F193642D39A13C36E137E306C7677CB675DA12F3B4FA70509D26657A66C573E883ED7687C6E07BA5E107CD248ADFB51B3065C439298D31815A1E68B1689C9B43747BBACC18543C14265E61B218C71394372FF87BDEED9D2CB30FC3399ED620A9B9CEF9884
	407BACA376574F294F6D0577F1A36F2B5B8A91FB82526BC43FFC421C8F4F53E05C128F7C38379F55F0072EF29B3D3F2990CDAE57E5DCG541D67B22E826AB271B97A220B5BE2FB6EBD44766492DDDB243E50446CEA2DA95A969F26F5184DAE2383289BA888B44B4C575693CDAEC5866AB0157902047AD27BD57466B8D1E431BA7EAC6A481C22FCBEAF392CAD9ABCDDF9327E5F3295EDF8B134EB9550BAB39F036F1A2D4CB60F3B43923746B3A1B0A449230E88AB7B3485579A690A64175B2520647740CBD83105FF
	366428F6BA9090B634166268375A40104282FF51E2E1B84850DABD4FFF19663BE5B9646D9B1125F312ACFB7D03AA7FFFD2DD4291AEB12C34B537985B1AE1F1994B00EC965E561B738BB476569588DC9A897660CCD26C76AD3ABE37C2F917050FFF0A8D77DA63A27DBCCD2B79A19B9F3BF8043CD39D53C8DF7D09C96BC83FCE79EA9DDF2E47F9407DE7260267087C87E8FEA234E5A5D0AAD0C9BE7838FC5E8F876BDDF1782FD72FD89D4F52BA2265EF799B5541EC64DAC1FC9B7FC8013E0D5FDC20EF631FAC185806
	E7976859B04B1BD700ED8C0C7AB5F3620B8568AF7B83FE3FC4A726E0A1EC6F7CE28F36436B774A973C1EC1C9BE673DA601CF4EC75BDECFFD72903C0F67F37FB9DA4D7DE74D352F10956F4C545DEF4D3973EE4393790A7F4CD77DA753E9FEA14EB549A78DC3C73585C67CB24397D2FF2F6061F40A7D4EBF87F9EB7F882BD08FF4E307D772F9EC7D6083E4BB7D4B16195AF75DD75CE37670A8F4161D0E043D8DF3CF60EB1D353E7EF52B6E1E5FA6F965A1A9A4A43F53261159B51150C3F5676B9CCC47C72AEE27BAF6
	3B8725509FC7067BB482EF94D59B8953755963C12A6BB9B4538FC54D81EFC8BA698F0EF0B9D0F9932B7B70F8C86927F0F894767EFF275A1B0952765E2CABCA5B7BC7C2DE79EA9B6CF47ECEBAB8791A5ADA02361FDDG6B8C1033002FB3B5C5184F5799E5B1ED8B78FCEC5AC25E22821C13AF533D871FE7DD856C6CBE871EACFF34D9AFFF3D485F201B1FDD48732FBA75724BC47E34963DFCC7A1EF07E52B997D13F4F9C402EDA7AE1427252B4A5E31E4EBFED45EF937EFC45D13DA3722DF76E956D80697946B7B0B
	ED45382EDE92724B0BB7915CDBB2194C3FADF6234CE048BB8CDB925CB4A8B10C20D99B43E8877DB15EB19D1EF300986F19BD380FE6E30C7E671847931242E1A5D860ABC2B94F5773B847E260DB8934D611D5D3447C9EABF70F1D17DB8669E42054A23DF24186DE4E3655E5EC8772ECE5659237EA4BB5BBF8399500BF95375CF3225C86404D4531659C45D8EECB930FB3B4882C0AE2AF4122F9DB86BF4B3DE95646EBBC61853A7E9FC9AEE672E794C71FF1C736215C0500AFAB66F67339E26DBE6BD8B89C687752A3
	06E2839DA55A3501E127CBB64393E6DB984E3B9EE90C5B51FF15202EF886D1E045966E5EFB04441E29CE5088915F46360D52695D93C06750892658A6164E73DEE9147098D1FDB36F2FD545FCED7C5F8EEDACE839E92EE1CB1C3E49A8E1FDD2D322764D2A979E4CE0326F9532BDA5BC8E54606458BC013D564231A78476B5019937F1BB1E5B0271823D78D8BF6D887B600857BEA2FB71C065165D54F5B1A3E7B2FFC67738A82BFD9B0FB5ED22EB78B824939083E2675E823BD1A91B845846ED5CEFD8CDDA3F615C6E
	B2DE841A8644FC73B5BB5A5D94FD30B22E2F6C41FE7FEB16C7B6FE738BDA957DD65DBB1F9C7E601AEE1CEC06E93AA1E1BB9B37655B557133265F156184E27D70E3518F59253067AD657DF0EB15DEBF0C1572FEB852F0BD7D406A1C5B406B1C897212C41D5677746A5CA86AA40E6B6F7B243AFCB68723D6BC9E87396860BA194BDC464EB22E53CB7A76D0466D485C487992GDBA5789EF26A71E51473B9F821C96B1FD6244C096B1FCA4A15D8F4F934FFFA2B946D3D93700B655CBF2DAA31E87C53D97088EE7FA02C
	D0628C308D7B9256A2A57D7EB62C172EC5041FC51A1A2792BF8B1673B848ADE558666910CE84DA86148C14AAF44B5CBEE9BC7044588471407B27241739021F5F1C4E297055883EC3ADD361BBA07816CF092FCB707DB2A6F658DB211E2F650A5833B60B6DDD23BEEEC6C5F46CF0BA4567D4C4478E0D05BCF63890720C153C7EED76687A031511F54E0CCAB33E5DB1FC95D16945D1FCECCE6E2C45B51F7AFB4D94EEE9B9A51CA35B83BDD4285D832D2CE4B10CF570A4D63E1EDCB0E5AB7A14D839BD2EABC7795D1076
	81CD87CA84CA82CA814A58416D32E73B5EFA9BE9177196DBB536437AE275F57661731376E14D0EC8BE566E9FD671F69F5E41477176066B53611E98BEFBD4DA56556142FB28837A3519C207BE477569900C610B56A1C9D707DDF46D27F1EC214356183B1A3FCC071A983EE89DDA2B74F47835136B602A62B6E82BC4ECB924D7D5F1FBDB329D6F5E380F6E8EF87DB2617E794F25F8B688043C03DEBF477AA9569F885100890B671D24FB2F55A0F303107B053278711DBB4B7463BBF7176947F71614CD9C437CFAD93C
	788F536FB1211F315AE5E458E9A7472C82BB5B42B12740FEA430CE013DCC4777A22403EA39FC4492A03D281A0F7934C2BCEBA50B7447E99A57DE164EAF1CA85E08FCFF295F56A3D6475E896B78EB6219C865D44623DBCDF4FC42E1A9CC1D936E08153C6764A22293CB4323A161B106EEFB0ED266049CE8EBA45CA77F268EFD2D0ECF269BEDE6673FA9647B6CB555FC1F5DDB09EBCAD98D0CA3D0D78D6F6F0B91F7F81E1A487E9D0BCA4F2C61367CF2F9FCDB7EC3393EAD27D7603C8A87C6C27DD2343DEE2EA891F9
	2A77BEA9CB4358AD4A791D724B6E502094E36F5BA9BF4B0B47DFDD91EFCE30BEAFE85076F91330171FD4E97BA8B12ACF16C625D756707D700FA364B58BF9975F534ADB2D7027AA4F0C1D1172CAF6F2F9F39DDAF9CE551E4DFA76EC5B29BFD7DAB5F8CFD419A1C80FC7E12C6EB79B35F56F93F53F62542BFBD61C3A97E870E5D1E5D6C1BAA58AE36356D8097B23B1115EC753D7451C100E650931BF6177A88E1B459EFA291F3B891C8C6EA198768C453C407C30407A286FFBA91DFD8B43B0AF75F3E940BBD7E04FD3
	AC934A8D9BF85F64DA507F7B5CD783A3D0EF6FAEACD7E361FB227550A6A3E0CE204EDDECEF4474DEB20AEB51C048BD8CE6F8AEA87513433BE99D791CF7A114DF8E349AE8A3D059EEEE8BDFED477EE7333ED1990B8D05516B62745DEAFF4E599DBBA6EC9F1AEDC55F30E8B75B0796893C1462A99A1C6DCF8728AF41F4E12D4B3822167B7BB05D63796A40066AB8D651445700D982EB25FB2294C82F97580BD51CB7FB8F47FE1147F94B84761B831C57A730B05D5FCC433440E6DB90DBG6925827B1D054BDBA130AD
	B6444CFBDD461A3D9CFB132EE587A05DA9307BA8568369A1017D1EE263101E29E26F71BA92857607B444D2A11DE1635836FD0815C03AD6E06FD34C8969CE01BDC2310B1076892C391A57B1CDE03F5D0ED8B22457587859374B423F855AG18231E5B5D775F413D0FD8C36C6ED13925650D10575325183032DFCA207D057CC375FC9D799615198869E5C049757C3CBC360B472C96FDE9BE691AF61F6E77CBA9BDF38DEB6B199D9A6D6AF939EC9D0FD9198BF81D1D10BF4D0E586B855A33BA5A7FE752581E0F6587A3
	725147D793ED7E7C08FC54779B86ED7E93917958C7658ABFEB5B63BBF95BB6BE3E18789EC80C7BED56CA25CD6B14E772AD55E446576F4AE0E3E4AA423655B4380C879A78383DD0414772E283DF13732C35289B2467GAD85CA86CA85B2BADCC69BD097D090E80603736C20BC8BA01D64E071E30C8D2FF13018EEC6A30B5956B432D8B06371D5A6EAF4473A78FB17A2DB2CF18D313EA36850777149D1E5D32352B69DBEE39CD953C41FFCAEC2D76D378A96459771F4160E1C829D0C5B02B69DFD1FEB5261490DA3AB
	1D707560BBD65C538E847C72F46CF7B20F7A45F6F86F865AC831FDCD6DF164707625EBE8A5702C86AAD272D261FDC91AFA67DDC0AED4E13B7B833E11E1FF1830FA7D542756E15DCDF03E26D8B1759DBD106EED623A7CB6EFB2DD347AA4B4C56A3230C9550555D1F610573B34092FD78F14C675897513C9101FAC4AAC32E09999B60E994D786D69FD3BA0C9180E167C49F44C526858599C29634566687E2A32457657C19B57FB28196B64B1C76A7464EEA2E4BCEA0E1511C25784749FE3C2466387A35BDEC37D6A8C
	48CFE866E310BFBED97BF2347690553E55102E0FE85F5D743BAC490F57DF9EC537DD8671BD2A504D31AF32FD9BEA55764D5E975B3EC57BF87BB688995FB0C436AF151649F63A0C05CE5E3ED3551335EF0BF67C1CD163670CEE5F28B336FD95253CFDC3CE6EB74EDA2E6F9B455E6B641BBBE5BE3657CA4847F5F54C4976DAE9823F0D6253B5B8DB5BD70A35FD96642DF772F8465374FC685A67B24AC0B30196GA581256C63E73787AB9DE412F8DACFA0588507BB1FA634405AD7D4F0FD5FEEBE1C455750258F5EC5
	16FD2CB76E1D567AFD4A774D2F3167506CEA0A33767977E27B5AA12F97E8BA50BC2045C0ABDF636DB3D8ED2CFDF8A5F7CC165D7DC374360FB78EAF60D498B75E4C26F695456E63DB4B35314D55D0C746FE1731E4BF532DFDBF16B1D472B20E7D316B9813FD17877B814F22DD6EBE7A0D38027762BD37660B8F1A4D72DEB8172B7008002B60BB17DD9849A70A307C4472DC1A4F3E10082C7323F60BA6AFD6763307B0DF7BF5C3A43FBDD494159FAB23F56796DA863F8EE15C8CBF3047A4F0150B5D493CED556E43E6
	6EE7F7B20B7773BB1B3555FA79C9A25FFDD0AF7FA911FF4E20173FCE641FAE554BA787E04C29EF1C0553C6E90725E9A7BC7D522815EF216D9D60GEB2B42EEFFB8AC053C83D5B62B43E6EDF45609B910B82A3DD3BA30798BF16F148A8FB0BB2AD51E07D7D613CA4276B159047FCBBD9A0B9142F43D9D438DCA3D4F98795CAB3B7B8E527E13DEC3F51CFAFB157A969FD0776689373F1A495AFCD395EC1BC9130357316CB535BFFE10B7157E50EB8B137974FED5E67341487AC6DB543C991629777F775F56763F7973
	B38C5C27C67743DA257D6BB57D90DF67DF9ED2756A36F11DD97A95B3269B1D3C8D07DED74B3E785E5F264F7EFCD415F9042E45F2E8C4E269EF6F0B2C7F41E3EA59DD55FF1B7AFFFEDC15392F14574F560B4F63352E32B65E21F97FA74BE45F157EB5446AF86EC32D032DB5EB6A6247C1DF28530F037EFB1DFECC7F153A093F59FC35AE7EB71B1B76E83F59E4EB65BDED684FEA8F42198FE888E89CE856C13ECEA66C29A2F41D54D9A3CFF886B18E0BDF7D4B3C7E513D715B7A7E5E49FFABF073DE7C4E26AFA04B01
	E114AF3ED55D0B63A5870294E458E777E29F053C03C3B2AB2A60F37652B811B4C0E1F6AEE96C0D3827D46399475D03AC2441E28BE595BC363068A00FE71C2A40FD549AC8EF84AAEC6177153515BC06EEEB61B10E15FF5246812CFBAF694681B67C69960327C579E629C5759FC360C715987EC01C98BEDDA8D97D1F3572FB4365C2274A9A0E2D97581C037CEE4FF608E39FB7F04CA3300F16F25EF90778DE51F4027EAE8252290738CDBC69588B4F736EF7E33FEA8230BB9488CAA179AAC369D7F70A2D1D6E407D3D
	79304BE8836A869A829A87CAGDA88349CA899E8AD90B982FD8DD48BF498288B2897C8BE4275D853DCB7019E4A60D2838D39597D9C53E3499BC4584AC3ADBB6349G8BBF85DDFF266FDD29DF6EF18F8A1E875A625B78723649ED7CBBED747BB2304D1E2BC1655E6131B6FAE7B8B0901644DC7AFE1BD9E0A7435D0130976FAB14D8CE45751DA13EC9EDF9461168B344A394CFB892FD06682E60E708C4484BF81D1F196AAB35758F81BE7E7A4477EE2C6FD7F622DD4F02320B0012015682ED84AABC4A47555DDEBB41
	3832592A8E2B927B31EA7529BD2A6C0914E77751D85D14FB621A083BAD255C18721C99176F36039177BDCA39254AF3A5BCD7C771B23ECF9B34FC29CA190CD676AC01272DD50F6F66D2ADDF2BD226C7F98E35466A497A3270843E0B7502CDC7A52F74847AFE7C9E5F6B76F92F51CFA04FC1C17FA04BFF27A33E1D8FF4CCEE67018EFDDFBE52916B4B2FF5687B72E99DDADF4E301BBA50C78E427CF609F29F9758DB827BB4150958852FC79E9278EC2A97621D92D64871DB29DE2394F3A9D82286FB0B5F43F760DC05
	A68F85C21C772E8E9A5F707AFCACFDAFAD63754354957DF1BF55519BF4EB74DEA6303744D8A761E7A34A7B77B46F67ABBE7C7DD060C32D0345335B693AB98EB60BB1F665DB880AA5G06F1FB56B7BF680CEFFFCFF5CEEEFF4FF46A5B5FF31D3176372ED35F7EFE5D2935BF266B3DD6FE8F3538154768EBDBE92C9252CF886C132DBCEE1FA230C3B51C57F10CE386B767ED97D83F1B73F68BEC69C1FE1714A8308BF44DDE8A69A401ADAF653C4982CBB0717BA56BF10EFD49406F0D1C821BCFFF075389E917403244
	7D1CC7E03DCEFE1734D4E0DFA865F7ECAB8556FA19FE878269F5823B73B2173761B83FEFF26FB22A3E08CED706170D652AB81F2ECAAC6FE9BA8E1DEDAEE3CF9B0FB39EED0D0CD5FE69FD1CBFAC3E3820F52278E2FA942D564453E95C9A6A3090ED8C5BC347F29660735A78797F518F547A2E39A72BAFF3027AD63529EBB12B6F99BA7ECF813EC63457DF9A595ED3E9EA7DBFAA1D2C7E1C896AAFEC0F2E7FDBA69EBB3536733B029FD19BF2C07AF0BB57696B0D11BA61ADB85729E242BB8D645FB201CE3EF6364FD6
	F57A1478D6FDCC6874B835439910CE90BADD389C29532CF4D5276517A753A9EB821D12200E3715F7A67B9F69D10055DB560A67A71D7333876CB9B028B9BCB35E1F224BD25E5F4CD5653C5CBBB5B941BCD54EAA33AA67F46954643CE4D1F9FFEA6267F616F73AD04D3B25F1EAF27F23C8653D5399A9F738C44DF3DE1E1A5CD9384DA46C7FB2B1B55F85A33168D7A4D3A65B2F0EDC02AD23943AA205DAA3FF15A0134C0651FEB9A813CD35GCBF56190490D286DBBDF5C772F2BFEF1BE1CCEB61DA45B7092631C9422
	FBA649035FECA9BFB06FA0B9C17C2E84FF57F812E40469A3B3ACDF75C9DD24F060AC7E98A9ACCB1EE3FCB79616C9917D39C0837EA22221539D9E12F185BB54C5B27C78D3F8124F32FDEE7FE0835B1A9D9A439F55259D6B45F6D283A520EF815597255805140207CCBBF71CDA33AC1D98BA48E6DF406D69F07743C67AA4491107C2D2F8A860730C850F36D2C159237C3B47F490D7AA1A0F0D35E390D6A8FDE96C361939EBF3BBE0D20F05C883AD09734AAECAF85A065CA128CA8A1D1471277D01D00D487BC8053C6B
	FBEF94DFB410B4251117497BA4F38CFBFF40BD6C75DD15C90E08AD05B1B38790D8667D6101C0E8981433C6A94733C49D76FFF854B17BAB3F7B9FDA074AAA13CA561B405AB0A885069B0E8D8CF87D1290GDD8B624B05F8ACE30FAA13BF761922D3CF3E5A872650CE52079D8E7A37117EED423F0D94E924C8E39374EFAA1970DFF47E98EFB3359F5047C475C18B30F740A04ABF7B5523731FBE1C3A8EDA57C1B269D77FB08EE635940B75FFEC516C72AD89A91F40E18A22350C215C5CB1A191DBF8126ECFD32EB437
	24BFFBC73A01E41CA41B58FE896C97B7BDE03FF4CFF4122443FE091ADEC4BC1AAA1B9EE31008E645DA9642DB34630BG13E126FF8B265CDB48D5C4F90BC5A3ABD89FEE0FD2B67306D4F6677FA7E5ED314AF65F304AB62B4AD6EA151509117D4E520A57E6ADB24925D7E4D6B6EFA01D432F3D605D442F302CF80755E215C9963B18EA392EB6EFE24B5607536023EA03F143122BBC4DB71E269D7A1AE65DF81A3E992B694E9BD3539ED5539D0A26E9B72626EF4799FDFE87D77701768EEE43CD9F443D83F51EE1F12F
	4EB3513F2B3BEFBFB6E9BA6009E7441D7F7EFA92BF56FBEE586D7349EE5F25E3FE8F46FD0FDF15A575DE65EB7BD1FE1817D1B2D95EB777235E6FEAF87E8FD0CB8788B3CDC8315C99GG28CFGGD0CB818294G94G88G88G20F405B2B3CDC8315C99GG28CFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG969AGGGG
**end of data**/
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
			constraintsNameLabel.ipadx = 8;
			constraintsNameLabel.insets = new java.awt.Insets(16, 13, 7, 7);
			getJPanel1().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsPagerNumberLabel = new java.awt.GridBagConstraints();
			constraintsPagerNumberLabel.gridx = 1; constraintsPagerNumberLabel.gridy = 2;
			constraintsPagerNumberLabel.insets = new java.awt.Insets(7, 13, 7, 7);
			getJPanel1().add(getPagerNumberLabel(), constraintsPagerNumberLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.ipadx = 216;
			constraintsNameTextField.insets = new java.awt.Insets(14, 1, 5, 14);
			getJPanel1().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsPagerNumberTextField = new java.awt.GridBagConstraints();
			constraintsPagerNumberTextField.gridx = 2; constraintsPagerNumberTextField.gridy = 2;
			constraintsPagerNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPagerNumberTextField.weightx = 1.0;
			constraintsPagerNumberTextField.ipadx = 216;
			constraintsPagerNumberTextField.insets = new java.awt.Insets(5, 1, 5, 14);
			getJPanel1().add(getPagerNumberTextField(), constraintsPagerNumberTextField);

			java.awt.GridBagConstraints constraintsPasswordLabel = new java.awt.GridBagConstraints();
			constraintsPasswordLabel.gridx = 1; constraintsPasswordLabel.gridy = 4;
			constraintsPasswordLabel.insets = new java.awt.Insets(7, 13, 7, 37);
			getJPanel1().add(getPasswordLabel(), constraintsPasswordLabel);

			java.awt.GridBagConstraints constraintsPasswordTextField = new java.awt.GridBagConstraints();
			constraintsPasswordTextField.gridx = 2; constraintsPasswordTextField.gridy = 4;
			constraintsPasswordTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPasswordTextField.weightx = 1.0;
			constraintsPasswordTextField.ipadx = 150;
			constraintsPasswordTextField.insets = new java.awt.Insets(5, 1, 5, 80);
			getJPanel1().add(getPasswordTextField(), constraintsPasswordTextField);

			java.awt.GridBagConstraints constraintsSecurityCodeLabel = new java.awt.GridBagConstraints();
			constraintsSecurityCodeLabel.gridx = 1; constraintsSecurityCodeLabel.gridy = 5;
			constraintsSecurityCodeLabel.ipadx = 4;
			constraintsSecurityCodeLabel.insets = new java.awt.Insets(7, 13, 33, 1);
			getJPanel1().add(getSecurityCodeLabel(), constraintsSecurityCodeLabel);

			java.awt.GridBagConstraints constraintsSecurityCodeTextField = new java.awt.GridBagConstraints();
			constraintsSecurityCodeTextField.gridx = 2; constraintsSecurityCodeTextField.gridy = 5;
			constraintsSecurityCodeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSecurityCodeTextField.weightx = 1.0;
			constraintsSecurityCodeTextField.ipadx = 150;
			constraintsSecurityCodeTextField.insets = new java.awt.Insets(5, 1, 31, 80);
			getJPanel1().add(getSecurityCodeTextField(), constraintsSecurityCodeTextField);

			java.awt.GridBagConstraints constraintsSenderLabel = new java.awt.GridBagConstraints();
			constraintsSenderLabel.gridx = 1; constraintsSenderLabel.gridy = 3;
			constraintsSenderLabel.ipadx = 45;
			constraintsSenderLabel.insets = new java.awt.Insets(7, 13, 7, 7);
			getJPanel1().add(getSenderLabel(), constraintsSenderLabel);

			java.awt.GridBagConstraints constraintsSenderTextField = new java.awt.GridBagConstraints();
			constraintsSenderTextField.gridx = 2; constraintsSenderTextField.gridy = 3;
			constraintsSenderTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSenderTextField.weightx = 1.0;
			constraintsSenderTextField.ipadx = 216;
			constraintsSenderTextField.insets = new java.awt.Insets(5, 1, 5, 14);
			getJPanel1().add(getSenderTextField(), constraintsSenderTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("Device Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setColumns(20);
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * Return the PhysicalAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPagerNumberLabel() {
	if (ivjPagerNumberLabel == null) {
		try {
			ivjPagerNumberLabel = new javax.swing.JLabel();
			ivjPagerNumberLabel.setName("PagerNumberLabel");
			ivjPagerNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPagerNumberLabel.setText("Pager Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagerNumberLabel;
}
/**
 * Return the AddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagerNumberTextField() {
	if (ivjPagerNumberTextField == null) {
		try {
			ivjPagerNumberTextField = new javax.swing.JTextField();
			ivjPagerNumberTextField.setName("PagerNumberTextField");
			ivjPagerNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPagerNumberTextField.setColumns(20);
			// user code begin {1}
			ivjPagerNumberTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_PAGER_NUMBER_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagerNumberTextField;
}

/**
 * Return the PasswordLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPasswordLabel() {
	if (ivjPasswordLabel == null) {
		try {
			ivjPasswordLabel = new javax.swing.JLabel();
			ivjPasswordLabel.setName("PasswordLabel");
			ivjPasswordLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPasswordLabel.setText("Password:");
			ivjPasswordLabel.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordLabel;
}
/**
 * Return the PagerNumberTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPasswordTextField() {
	if (ivjPasswordTextField == null) {
		try {
			ivjPasswordTextField = new javax.swing.JTextField();
			ivjPasswordTextField.setName("PasswordTextField");
			ivjPasswordTextField.setColumns(14);
			ivjPasswordTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPasswordTextField.setEnabled(true);
			// user code begin {1}
			ivjPasswordTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_IED_PASSWORD_LENGTH));
			ivjPasswordTextField.setText("/wctp");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * Return the SecurityCodeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSecurityCodeLabel() {
	if (ivjSecurityCodeLabel == null) {
		try {
			ivjSecurityCodeLabel = new javax.swing.JLabel();
			ivjSecurityCodeLabel.setName("SecurityCodeLabel");
			ivjSecurityCodeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSecurityCodeLabel.setText("Security Code: ");
			ivjSecurityCodeLabel.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSecurityCodeLabel;
}
/**
 * Return the SecurityCodeTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSecurityCodeTextField() {
	if (ivjSecurityCodeTextField == null) {
		try {
			ivjSecurityCodeTextField = new javax.swing.JTextField();
			ivjSecurityCodeTextField.setName("SecurityCodeTextField");
			ivjSecurityCodeTextField.setText("(none)");
			ivjSecurityCodeTextField.setColumns(14);
			ivjSecurityCodeTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjSecurityCodeTextField.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSecurityCodeTextField;
}
/**
 * Return the SenderLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSenderLabel() {
	if (ivjSenderLabel == null) {
		try {
			ivjSenderLabel = new javax.swing.JLabel();
			ivjSenderLabel.setName("SenderLabel");
			ivjSenderLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSenderLabel.setText("Sender:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSenderLabel;
}
/**
 * Return the SenderTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSenderTextField() {
	if (ivjSenderTextField == null) {
		try {
			ivjSenderTextField = new javax.swing.JTextField();
			ivjSenderTextField.setName("SenderTextField");
			ivjSenderTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjSenderTextField.setText("yukonserver@cannontech.com");
			ivjSenderTextField.setColumns(20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSenderTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	PagingTapTerminal tapTerm = (PagingTapTerminal)val;

	String nameString = getNameTextField().getText();
	tapTerm.setPAOName( nameString );

	String pagerNumber = getPagerNumberTextField().getText();
	tapTerm.getDeviceTapPagingSettings().setPagerNumber(pagerNumber);
	tapTerm.getDeviceTapPagingSettings().setPOSTPath( getPasswordTextField().getText() );
	tapTerm.getDeviceTapPagingSettings().setSender( getSenderTextField().getText() );
	tapTerm.getDeviceTapPagingSettings().setSecurityCode( getSecurityCodeTextField().getText() );
		
	//server checks both DeviceIED and DeviceTapPagingSettings for a password
	//make sure this one is not set to any real password.
	tapTerm.getDeviceIED().setPassword(com.cannontech.common.util.CtiUtilities.STRING_NONE );

	//Tap Terminals cannot be slaves like some IED meters
	tapTerm.getDeviceIED().setSlaveAddress("Master");
	


	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getNameTextField().addCaretListener(ivjEventHandler);
	getPagerNumberTextField().addCaretListener(ivjEventHandler);
	getSenderTextField().addCaretListener(ivjEventHandler);
	getPasswordTextField().addCaretListener(ivjEventHandler);
	getSecurityCodeTextField().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceNameAddressPanel");
		setLayout(new java.awt.GridLayout());
		setSize(350, 200);
		add(getJPanel1(), getJPanel1().getName());
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
public boolean isInputValid() {
	if( getNameTextField().getText() == null   ||
			getNameTextField().getText().length() < 1 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if( getPagerNumberTextField().getText() == null 	||
			getPagerNumberTextField().getText().length() < 1 )
	{
		setErrorString("The Pager Number text field must be filled in");
		return false;
	}

   if( getPasswordTextField().getText() == null ||
            getPasswordTextField().getText().length() < 1 )
   {
      setErrorString("The Password text field must be filled in");
      return false;
   }
   
   if( getSenderTextField().getText() == null ||
			getSenderTextField().getText().length() < 1 )
   {
	  setErrorString("The Sender text field must be filled in");
	  return false;
   }
   
   if( getSecurityCodeTextField().getText() == null ||
			getSecurityCodeTextField().getText().length() < 1 )
   {
	  setErrorString("The Security Code text field must be filled in");
	  return false;
   }

	return true;
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceNameAddressPanel aDeviceNameAddressPanel;
		aDeviceNameAddressPanel = new DeviceNameAddressPanel();
		frame.getContentPane().add("Center", aDeviceNameAddressPanel);
		frame.setSize(aDeviceNameAddressPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}

/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
	return;
}
}
