package com.cannontech.cbc.capbankeditor;
import com.cannontech.cbc.data.CBCClientConnection;
import com.cannontech.cbc.data.CapBankDevice;
import com.cannontech.cbc.data.Feeder;
import com.cannontech.cbc.messages.CBCTempMoveCapBank;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Insert the type's description here.
 * Creation date: (11/11/2002 9:53:00 AM)
 * @author: 
 */
public class CapBankTempMovePanel extends javax.swing.JPanel implements com.cannontech.common.gui.util.OkCancelPanelListener {
	private com.cannontech.common.gui.util.OkCancelPanel ivjOkCancelPanel = null;
	private javax.swing.JComboBox ivjJComboBoxFeeder = null;
	private CBCClientConnection connectionWrapper = null;
	private CapBankDevice capBankDevice = null;
	private Feeder ownerFeeder = null;
	private javax.swing.JLabel ivjJLabelFeeder = null;
	private javax.swing.JLabel ivjJLabelOrder = null;
	private javax.swing.JTextField ivjJTextFieldCapBankOrder = null;

	/**
	 * CapBankTempMovePanel constructor comment.
	 */
	public CapBankTempMovePanel() {
		super();
		initialize();
	}


	public CapBankTempMovePanel( CBCClientConnection conn ) 
	{
		super();

		connectionWrapper = conn;
		initialize();
	}


	/**
	 * connEtoC1:  (OkCancelPanel.okCancelPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> CapBankTempMovePanel.okCancelPanel_JButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
	 * @param arg1 java.util.EventObject
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1(java.util.EventObject arg1) {
		try {
			// user code begin {1}
			// user code end
			this.okCancelPanel_JButtonCancelAction_actionPerformed(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoC2:  (OkCancelPanel.okCancelPanel.JButtonOkAction_actionPerformed(java.util.EventObject) --> CapBankTempMovePanel.okCancelPanel_JButtonOkAction_actionPerformed(Ljava.util.EventObject;)V)
	 * @param arg1 java.util.EventObject
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC2(java.util.EventObject arg1) {
		try {
			// user code begin {1}
			// user code end
			this.okCancelPanel_JButtonOkAction_actionPerformed(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	protected void disposePanel()
	{
	
		return;
	}


	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5CF0EBADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BD0DC57F5F2EB55CC534EA809BAD19537450965901B096509BA11EB4FE455D1BDCECC6AC9E2CF19C67690DBF5193122D1DDF92AA432F359DDBE0EFE0B8189EC25C6C6487C347C960490208596091F8431D630C8CA4503FD400AE55FFA7771112DA4BD67FE5EBE167740F24C4E1CF96F1EF34F3D671EFB4E39675C37A47F3D444C8CEBBEA19919047F4EE4906229A1247409375F94381CE2CBB6517D
	CEG64126FCD5A61D988B4F540126DA085B3B9502E02767A4B166CE3701EC7F60F5CF0420B49BCAE206D7E6D13DD4B4FF3ACDF4C13CD5B7F1EF1901E0781EAG6785C8AC4679DF4BA8647885E89F666F8899D6C2FCA730CF73C8218E3F13772B821A9BE0BF6DF348FA1C5393210DFD3642B365846B3B931EEFD16956B6B771FEB73E18C876FE7F836BA11CCBB73EG4F7B0CF70ABF0B4923D491B61217F58E1EAD035313ED634A0CF3DC8A051410AA0FCFB947474621999E13C25332BF20AA116666CEA95C81CD2F
	BC933E244C4903D2C88E125477D16651119CBA9E488CEDFF8962D2C7D0CF47615D8550DFA2647E612196AE577A69C4127675F7CC65CEAAE172261444641E3AF3B56B4EC99C3F51FA10739D043687004972872B1045F2D248D2B904FD77A1DF29A53B2CD460DF23F8B734FDG61D2A1E36B87C2C65FA70F927F3F7E4CFA44C44604D2A65B6652180CBBC2D7330A282DBCD1007B3D8BE80ED3166C13G5DGD3G89G6FD29B7D3F7C8AF8360F291D4A4C0C92EA8F07BB027E48A545AF87613D3999A89C7721BC2EC4
	7C04B0BB14C72A8C649110F8EA318DA230358D93E1B7499FBEC43C3F5FED4DB4D85B16674B2DDB7898CF1F0A2DED7FA954DB7FB554DBBA3CA39F72B37CB0453BBF086159DE3DC8ED815BDE20ED7DC06859F7D84852EF5FC89C357D56EC83D9B67CF33B75F8991341D59613056978EF0E210EC781FF87E0BDC0B2C08A402EB221E3CBFE7DCABA6E813B0F0424601559313094D1BBE683C13F9C613AEE31AE27EBA3E0FC3DF74D477412D42F5F239D5F6EB3544B0E249AEBCEB957C7F9CCAFEC6EBFB71D5BDD3E54B6
	8EEBFB788F54A73C65EC8FED9A7E1B946F5761595E56D2BC36D5202582307E934308EF290837110FA8FE30A25EC6768D899B71836DE9G363F75C5988F2BAA41F7GC68196GB6GEC81F83AD26C7101026ACF69C7033A7DE53C5B8B0E42D3DED0A3D2CFA82ACA21F1F9C80A842431209CA53C4F8F8AD02FE30B78FEC47DFB9C6638A8076408248614900CAE4D48E0CCD15667A7853958A7A2D32AB7B0A34384B3E1A246FD39A08F1EC1A92ADE8D7BA5D50E75E074D78B50A66624A1989186007742AA54579D9AF3
	53E17D76AA212B51F790F7835AEA15504B44513761598746AD871B1BBD01F194C30A5C96B67A61B564C9007E5E5342AFF77B047DF98EEDA2D54F779A5A9FC2211359D20DB35E7EFEC7477581FE41A94E4E1DA33E3B7487B3C0F97109B2AF7639B7BE37B9EC7C192943B1BDC74B51D6B7403A36GBC67943AFE60E8B133418EA9AA8B2B9B080041487E1866D95F3FBF06E70ABCA707D40F9C16C3FEF80972BD5FF46C2D18BDCEA81A2DBCC271981FBA41E0229A7E31E3398C8FE3682DDB509F27F4C9D5A30131D9D5
	3EA22BEAA0B499635FC6E509AE8AFDD13E56598ADC6BAED8233D5A121DDFAD565ADB1827332BFEC5191E8DFB51E338FDEE2467F09974F7D70B7D5FB4043914DFEBBFCA5BB17A19A19F27FF9E7BE79C1387699C36D598BFB6C7F09857AE62A4434DD35C3D3036E4ED3DEFD2D9B2D6C99EB64731E1315E4CD5180359C2EB1139ADFB0A0CED3937485816870B1637D5DF11112DB25A4DA2DC23B29F52593E3F88635F38928A495481B59642F2605DDEDCC7A094D007837EC9D93D9278D806332A9AED784BB446BE876F
	6955A2B64E170858F87263870865E629E17E34E113476A2E61B12AA6969B19CC72BB68D3F2D01E81C5F5AA33A1156127E97C6678280D62FE753B6FA2EF5940F4A71ECBC11AF7D7A97A163337E3D6D51590C33553E55E1468E3D00ECCA8119959DFF6CB1A131C33EAA0686C42FD9A983B857AA8AFBEFD3A2D58A833DFBC434DCFB3C33F6194674A074AEB88DB4B39126FFC5A358CCC2F66BACCC7B7DFC3AD0F07C91538F7AA6CE982C0E20D0807FFD506F4918FF9506FD402C1E6F6C0AB2CC51B7A4635FC1D1DEC2D
	E1B5249DE80CDEF65D08BEDAAB6639DD6EC07B17BF1A15E1D998F07B645BBC57D8D37EF69CEDC88A4E46F218047283708C6AE2B543FFD2E1EC0F77D4E0FC1E5646DFDB11C5B097E1E3B27F59AC611861C8E0860EB6BCFCF82D7B18D5DF6B2AA0BF56459B60198C40F8DF4A47359AEDDBF29D4DB907C5AD3C9DF8F6G10BAB60E370E79946B977109FE7B6AD8BFC7BD6BB7DA4F7AED2947FE8FEBF5EA35065BC6F1BF86014E59C57EFD49212F2D0B1F7844E66463F4BEBE7EC1D7FCDD72C2953D0FGFC0BCB6C5D2F
	AA74E7E6FBB42A0C87E8DA487761DB15FA7FE738EFD77E9A1E9301C894EA943F3C201D6915A8AB660D9C4D7763114AD632B48F6E22C53CC8C9BA6729F1FB15CEC8DD3B419CBA3570B8941067011F5552AFDE937A9AF40978777B8B7A7A7D49A3F55683A63AC960BAC95269C6055A180DFD733A98BB5DA56C613F4B51E668FD847A6102B3BA8F313F3939175A8463FB57164357DA5CA06A78D7B351FEFD508E83A4G301A62C7432226C884DCAAG334D24AC3DEDBA9AD90EB288CFBFG0BD5E7FD5DF816FCCEF116
	2D4FFE22AFFE4CBFAD32F2FB7DDA1E1B2FA565DB9BC94B9F738C6FAB920E7C46FA27116D45030D31BDF13FFE5B366B58903FD7792ACF68EC8F74A9BBA374E19D71186963AEE3B553C5F545C5B3DD5CFDF496F79BF5FA3F3524559A1E4D8823E7587A55B371FE3B4D0976358E708F1E91FE6B2A4A55796D07B027941A0468A9FC7125AA9B0B133C7DCAD5260813C464E1AFDF3B1B3B002DE7C4CD77E6B5464894E8278310A6B8A7G1A1A04ECB327D73C5B68DAD866EE43FF7DEE6472B5095C3558964F97EC0A656D
	EB1A1666702C7E3F190576353E09557F87B47C8C45EF516119BD7E9775EDECBF076BBEAB66CF9D0E1F7F7E330B675C9A57E6FC2FAC617B5F38760EB8BEE62F6B8F5491133BD22AF86764EC001AA69C7EFA239631B4ADCB442D3DE7C55C3ABE0BEB2BEA36E4FBGEEB40BBD775662F926C2FB9DC09240F600F4G7BB97049F3E26F1FBCED946B976F3DD9200F5508CCAF8F78BE1BBE9FDD42972F4F6EF30B79589A575721BEC601965656D364BC247333C1A590D2B59FFB4A0939079289CC86C282778C450DAB911A
	70333A7397744CDA83E35E2F0D1BD5E3DE1F9C2AB12ECF72EA0C6B13221A65EB6D93B5E6758B136F136BC23E4DG8CF7061EE95B211D2A612E5991379F5A79AD426665AC0C85CD507682848192G92B5FA1452B17F598E381DG76D6CB7649D6C17F1FCCF1B7027C1A5F72F14CFC3E49041658E2E48F0EACB1C7E69C6DAD13718F93737A77C0DC7BB7346F770E9E21FB900D4AD1EA609EC915B86DBF5582E2D06FDD16E6E4BECF9EB962FB0B7769D2154EDACD767F480A37E5CFEBEC0D3ED6331876AC7563E0AB0B
	DD999ABE156297F4F8965324D9716D62DE202537EA5F82EA4D6DD42EB5365369DA741928B29B991763EDB1D2DB2851E21E5F234AB3288E1E7BD3FED50AC06D9B4F7FE7141F514C783FD0EBE66FCC57290BF4DD270D7D37F5387E852D7DF734FD1B08F8AB379F56666E8A79F9B0E0B9FBAE7521FBE0026FE4885C17A8EEAD343FE895F3DF36E1FC88CA3715D960CDEC43FE32CD1F7B7583CE85C8E8E3FB4B66FEEC9E6DEEA2A24D40D22E0465F1F267BC3D3B368B5EF4683F9F60B860DBGEE1C97F9D241E554E75C
	91406589FF0C7B5E5BAF0D49418B328C2AE6783C73386F3D904547148EE50111B84FA567810D07D6159CEFB90FF7563DDE30088B81B96867D5946B42EB4D43F37ADAF27BF91667261F973566172774747D1CFE52AD68FF99B062EF5268B9AEBD3D5F4D682ADB44307D744CCB00767D9A4E15AB621A45AD62527333C4734197F56F13331F57DD2E9130B9562C6A9C4C1EEAC771BC576A9A27B42FB6B7DF0C847C9D52E4BF35CE16A3FC41A66E8D9D6DA2C7F85C06B6538D6DD10005F6D107AD1C92F5D872C39BC97F
	1ABE436F28E95EBA6B3AF6DE833747F25DBDDA9D763ADD4C398768E59D08AB49546FAB5A5BA1686004A723DFDDC4C77FAFA7FA7AFD0B68A86FDA2B1E7E78A2FA260E1F2D6D19D231367D4FA41160BF1EB57DC69C6EE0EBDA4F1F8BC3A5A427F3A80B571393A675A475A6367E2EB9111725F408BAF4B2782EB11F70D01E53FDFFCA543076CE717DF9DBC00C5752A946ABADBBEEBC1E663D0CF7D30398CF6D9471A4AFFBE98CBBFDC44421E468379DE097G71303B0FFE8F76694B75ACE31762DEBA133F9782CF95C0
	89EF3B613DBCC3FFFF3DEEC93E3F9230B370341D667B1E783331116293BD71E763F7ED62ECCC46F5F4091CF840C54FA2E82F83C88230G24816C6F9267677B75B5E405FC582B047B61008D6AD2B7363EDF58BF5B375AED5942D6377C535748563F78F90659FD0863827FD6461F0E2BA5944F5677D2837D0E8D34BBG09GDB81D2GF6DE906B3BD5DF45560757B96D2AAA0DCF511BA231B83C3C09217160625F21670C56360F4BF17062523C0671BDBB351CCE0ACD791E8A987139F87FEED33E079B161B6F06819F
	33192BC7044D0421CF52C561F775F5C5E4C95C3D90D4667B39BBF1BF3E6290B1B06D22083B77B8F04C42EECBF68DC0B740FBF48F8849A16237673C91EE349B7FC7D407259937111F9E94B6F27C112F924F3F7DBC234444C69E6CE6EB5C5B9D0B55C91B073218FE6A71BAD67CA7C1777EAF3655BFA7DFDC55B978379D17F97EFB53E51C7F4AAE63BAED5A357CF724284B7CBB5247AE7DF7A4661FBDEDF8E6D57500ADG848156G2C6F913E195370BE213EE96017DD7EC94C3F711F852A187F558673353E5630727F
	91FE59007742E30A2AAAB3B83E40E7B462FE29CA18A2F97D5B08BA0252F9CAE5DDB97EE0A35AEDD01E20E8161FC974BC5C8C6B3274081C6D29B9113325E838D726846EF82F403DC04F3EA0346FE8380EDB22DF02065B56A0F0293DA2874CF5FC367F583C779979FCF74D474E2D57E9DCA86B0BBF4F5E24F8D7DF7CF9F61FC344A6B7509E6C93795B335775F7150E0DA72C02AFFE5E1CFE968B727B636FAAF71D47B33E9B70FE008400C400ADGA9GE9GFB81F2AED9320B811AG7A81C681D400FB81B6GA4DF92
	B6BC5F0AF95810F4EB69D783C07204159AF2C4525F4D7C364D5C16FDEDAB5B3254C63FFB008DFAEF07F9DDB951C66FFBA6A6223266B353ED8E8D57939DD422812CC7D91E34B00781D10509D87CCDEBC7AD33DC68053915F2A1BDD8C09F88A2C6BF876FF8269F5046FBE3EA3971B26346CB530DC76F7F607DABA4765DBF0F6C8DAC165D569913FD6456CA326B670B171DDCE6769613BD35E1A55975639D3C3CD876E2E8C779584C0FFE09DB425F6D3931773FF62C7E1974502A4E41FCECB35D8BE6FFEF341592937F
	00A85F8BA9867BA26115A6B59E62B6770DEC774A3E61F09B4779236E25F17EDD37F11CBF615647F906BB65467D1C841F76E97DAAB55C08062B21E3A2EEB860D727B4FCA3158B715DB2CEA870ADD42EF90A73F1DC3B8EB742F1975C685F3064A9A5A2F87B5C68879301E0103587E81FC0885CDD5307174A9888CBBA39C7B49C4A3DB4A7033C1A7A55FD743F5E66B95B4397E2FDFFF0F1793EFB7073A0E17FB33FB271C3F8A67F0CE4C6A36408C8B51DBDD47034397AE65B4BDF37D9093D07E4310D6CA1F65C0D9E12
	C9B72B0758E0A39770CF01D99C28012B4451B925A8D139D8AAE617C24DAA4925D7BC454A04783FC3F374A37287AADBA6E746672FB5AC7AF1862764E8D88747604F3E880BFD3F447958D9D57EC77DD935EFED2169F7352D837CBF5B8371E7D5AA4D97F286AD59A78745F9D3B6F51A187B531599A998D42560F4FB480F3E55F1DB15E3795B8BD3B8FED47461C4C65BB905F25F52717CBFD0CB8788DEAACF645490GGECB0GGD0CB818294G94G88G88G5CF0EBADDEAACF645490GGECB0GG8CGGGGG
	GGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8E91GGGG
**end of data**/
}

	/**
	 * Returns the capBankDevice.
	 * @return CapBankDevice
	 */
	public CapBankDevice getCapBankDevice() 
	{
		return capBankDevice;
	}


	private com.cannontech.cbc.data.CBCClientConnection getConnectionWrapper() 
	{
		return connectionWrapper;
	}


	/**
	 * Return the JComboBoxFeeder property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JComboBox getJComboBoxFeeder() {
	if (ivjJComboBoxFeeder == null) {
		try {
			ivjJComboBoxFeeder = new javax.swing.JComboBox();
			ivjJComboBoxFeeder.setName("JComboBoxFeeder");
			// user code begin {1}
				
				DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance().getInstance();
				synchronized( cache )
				{
					java.util.List feeders = cache.getAllCapControlFeeders();
					java.util.Collections.sort( feeders, 
								com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
					
					for( int i = 0; i < feeders.size(); i++ )
						ivjJComboBoxFeeder.addItem( feeders.get(i) );					
				}
						

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxFeeder;
}

	/**
	 * Return the JLabelMove property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelFeeder() {
	if (ivjJLabelFeeder == null) {
		try {
			ivjJLabelFeeder = new javax.swing.JLabel();
			ivjJLabelFeeder.setName("JLabelFeeder");
			ivjJLabelFeeder.setText("Choose a Feeder:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFeeder;
}

/**
 * Return the JLabelOrder property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOrder() {
	if (ivjJLabelOrder == null) {
		try {
			ivjJLabelOrder = new javax.swing.JLabel();
			ivjJLabelOrder.setName("JLabelOrder");
			ivjJLabelOrder.setText("Order of CapBank:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOrder;
}

/**
 * Return the JTextFieldCapBankOrder property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCapBankOrder() {
	if (ivjJTextFieldCapBankOrder == null) {
		try {
			ivjJTextFieldCapBankOrder = new javax.swing.JTextField();
			ivjJTextFieldCapBankOrder.setName("JTextFieldCapBankOrder");
			// user code begin {1}

			ivjJTextFieldCapBankOrder.setDocument(
				new com.cannontech.common.gui.unchanging.LongRangeDocument(1, 9999999) );

			ivjJTextFieldCapBankOrder.setText( "1" );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCapBankOrder;
}

	/**
	 * Return the OkCancelPanel property value.
	 * @return com.cannontech.common.gui.util.OkCancelPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.OkCancelPanel getOkCancelPanel() {
	if (ivjOkCancelPanel == null) {
		try {
			ivjOkCancelPanel = new com.cannontech.common.gui.util.OkCancelPanel();
			ivjOkCancelPanel.setName("OkCancelPanel");
			ivjOkCancelPanel.setLayout(new java.awt.FlowLayout());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkCancelPanel;
}

	public Feeder getOwnerFeeder()
	{
		return ownerFeeder;
	}


	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) 
	{	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}


	/**
	 * Initializes connections
	 * @exception java.lang.Exception The exception description.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initConnections() throws java.lang.Exception {
		// user code begin {1}
		// user code end
		getOkCancelPanel().addOkCancelPanelListener(this);
	}


	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CapBankTempMovePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(322, 134);

		java.awt.GridBagConstraints constraintsOkCancelPanel = new java.awt.GridBagConstraints();
		constraintsOkCancelPanel.gridx = 1; constraintsOkCancelPanel.gridy = 3;
		constraintsOkCancelPanel.gridwidth = 2;
		constraintsOkCancelPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsOkCancelPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsOkCancelPanel.weightx = 1.0;
		constraintsOkCancelPanel.weighty = 1.0;
		constraintsOkCancelPanel.ipadx = 148;
		constraintsOkCancelPanel.ipady = 1;
		constraintsOkCancelPanel.insets = new java.awt.Insets(17, 6, 6, 7);
		add(getOkCancelPanel(), constraintsOkCancelPanel);

		java.awt.GridBagConstraints constraintsJLabelFeeder = new java.awt.GridBagConstraints();
		constraintsJLabelFeeder.gridx = 1; constraintsJLabelFeeder.gridy = 1;
		constraintsJLabelFeeder.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFeeder.ipadx = 5;
		constraintsJLabelFeeder.ipady = 5;
		constraintsJLabelFeeder.insets = new java.awt.Insets(10, 6, 5, 5);
		add(getJLabelFeeder(), constraintsJLabelFeeder);

		java.awt.GridBagConstraints constraintsJComboBoxFeeder = new java.awt.GridBagConstraints();
		constraintsJComboBoxFeeder.gridx = 2; constraintsJComboBoxFeeder.gridy = 1;
		constraintsJComboBoxFeeder.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxFeeder.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxFeeder.weightx = 1.0;
		constraintsJComboBoxFeeder.ipadx = 75;
		constraintsJComboBoxFeeder.insets = new java.awt.Insets(8, 1, 3, 6);
		add(getJComboBoxFeeder(), constraintsJComboBoxFeeder);

		java.awt.GridBagConstraints constraintsJLabelOrder = new java.awt.GridBagConstraints();
		constraintsJLabelOrder.gridx = 1; constraintsJLabelOrder.gridy = 2;
		constraintsJLabelOrder.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelOrder.ipadx = 4;
		constraintsJLabelOrder.ipady = 5;
		constraintsJLabelOrder.insets = new java.awt.Insets(4, 6, 16, 0);
		add(getJLabelOrder(), constraintsJLabelOrder);

		java.awt.GridBagConstraints constraintsJTextFieldCapBankOrder = new java.awt.GridBagConstraints();
		constraintsJTextFieldCapBankOrder.gridx = 2; constraintsJTextFieldCapBankOrder.gridy = 2;
		constraintsJTextFieldCapBankOrder.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldCapBankOrder.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldCapBankOrder.weightx = 1.0;
		constraintsJTextFieldCapBankOrder.ipadx = 57;
		constraintsJTextFieldCapBankOrder.insets = new java.awt.Insets(3, 1, 16, 146);
		add(getJTextFieldCapBankOrder(), constraintsJTextFieldCapBankOrder);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

	/**
	 * Method to handle events for the OkCancelPanelListener interface.
	 * @param newEvent java.util.EventObject
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
		// user code begin {1}
		// user code end
		if (newEvent.getSource() == getOkCancelPanel()) 
			connEtoC1(newEvent);
		// user code begin {2}
		// user code end
	}


	/**
	 * Method to handle events for the OkCancelPanelListener interface.
	 * @param newEvent java.util.EventObject
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
		// user code begin {1}
		// user code end
		if (newEvent.getSource() == getOkCancelPanel()) 
			connEtoC2(newEvent);
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
			CapBankTempMovePanel aCapBankTempMovePanel;
			aCapBankTempMovePanel = new CapBankTempMovePanel();
			frame.setContentPane(aCapBankTempMovePanel);
			frame.setSize(aCapBankTempMovePanel.getSize());
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


	/**
	 * Comment
	 */
	public void okCancelPanel_JButtonOkAction_actionPerformed(java.util.EventObject newEvent) 
	{
		LiteYukonPAObject feeder = (LiteYukonPAObject)getJComboBoxFeeder().getSelectedItem();
		if( feeder == null )
			return;
		
		try
		{
			int order = 1;
			if( getJTextFieldCapBankOrder().getText() != null 
			    && getJTextFieldCapBankOrder().getText().length() > 0 )
			{
				order = new Integer(getJTextFieldCapBankOrder().getText()).intValue();
			}
			    
			// Build up  the move message here
			CBCTempMoveCapBank msg = new CBCTempMoveCapBank(
				getOwnerFeeder().getCcId().intValue(),
				feeder.getLiteID(),
				getCapBankDevice().getCcId().intValue(),
				order,
				false );

			if( getConnectionWrapper() != null )
			{
				getConnectionWrapper().write( msg );
			}
			else
			{
				com.cannontech.common.util.MessageEvent msgEvent = new com.cannontech.common.util.MessageEvent( this, "Unable to send Move Cap Bank, no connection found." );
				msgEvent.setMessageType( com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE );
				getConnectionWrapper().fireMsgEventGUI(msgEvent);
			}
		}
		finally
		{
			disposePanel();
		}
		
		return;
	}


	/**
	 * Comment
	 */
	public void okCancelPanel_JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
	{
		disposePanel();
		return;
	}


	/**
	 * Sets the capBankDevice.
	 * @param capBankDevice The capBankDevice to set
	 */
	public void setCapBankDevice(CapBankDevice capBankDevice) 
	{
		this.capBankDevice = capBankDevice;
	}


	public void setOwnerFeeder( Feeder feeder_ )
	{
		ownerFeeder = feeder_;

		//remove the feeder this cap bank is on		
		for( int i = 0; i < getJComboBoxFeeder().getItemCount(); i++ )
		{
			LiteYukonPAObject feedCmb = (LiteYukonPAObject)getJComboBoxFeeder().getItemAt(i);
			if( feedCmb.getLiteID() == getOwnerFeeder().getCcId().intValue() )
			{
				getJComboBoxFeeder().removeItem( feedCmb );
				break;	
			}
		}
		
	}
}