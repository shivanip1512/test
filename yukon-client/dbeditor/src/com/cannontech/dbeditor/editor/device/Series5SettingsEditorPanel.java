package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.device.Series5Base;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2004 11:06:58 AM)
 * @author: 
 */
public class Series5SettingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JCheckBox ivjJCheckBoxSaveHistory = null;
	private javax.swing.JLabel ivjJLabelHighLimit = null;
	private javax.swing.JLabel ivjJLabelLowLimit = null;
	private javax.swing.JLabel ivjJLabelMinutes = null;
	private javax.swing.JLabel ivjJLabelMultiplier = null;
	private javax.swing.JLabel ivjJLabelOffset = null;
	private javax.swing.JLabel ivjJLabelSeconds = null;
	private javax.swing.JLabel ivjJLabelTickTime = null;
	private javax.swing.JLabel ivjJLabelTransmitOffset = null;
	private javax.swing.JPanel ivjJPanelPowerValue = null;
	private com.klg.jclass.field.JCSpinField ivjTickTimeSpinField = null;
	private com.klg.jclass.field.JCSpinField ivjTransmitOffsetSpinField = null;
	private javax.swing.JLabel ivjJLabelStartCode = null;
	private javax.swing.JLabel ivjJLabelStopCode = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTextField ivjJTextFieldHighLimit = null;
	private javax.swing.JTextField ivjJTextFieldLowLimit = null;
	private javax.swing.JTextField ivjJTextFieldStartCode = null;
	private javax.swing.JTextField ivjJTextFieldStopCode = null;
	private com.klg.jclass.field.JCSpinField ivjMultiplierSpinField = null;
	private com.klg.jclass.field.JCSpinField ivjOffsetSpinField = null;

class IvjEventHandler implements javax.swing.event.CaretListener {
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldHighLimit()) 
				connEtoC1(e);
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldLowLimit()) 
				connEtoC2(e);
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldStartCode()) 
				connEtoC3(e);
			if (e.getSource() == Series5SettingsEditorPanel.this.getJTextFieldStopCode()) 
				connEtoC4(e);
		};
	};
/**
 * Series5SettingsEditorPanel constructor comment.
 */
public Series5SettingsEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextFieldHighLimit.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldLowLimit.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldStartCode.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldStopCode.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series5SettingsEditorPanel.fireInputUpdate()V)
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G87E525B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDC8BD8D45735B6899A531A46A466565BCB7A19E7C9CB9BFB6B6D35093D252DCDCCCA3EA60DE9529B5B5246B674162436352D6DE7EE36BC9CD494940CE8D0C7A58A0A83A26AB0B3A0889AB093C5C1C4030F01B94068B0B34E9CE4C8545C3577BEEB1FB307B3BC3C09DF72E5F94E7E57DE0F7D5EFB1DBD103C0DD3B29625669132A803A87F6DDBC448A4B7A1B3F2D29A91AB325ACDC4735FA62015E4D9C5
	BABC2D401B7BEFF6D38E195B1489E9A7245DDF301B5640FBAE79496A2CD6F809E2E76E89C2FEF8654FB30736B3E5A35AB13174AFB6E441F385D093E0B2D002155A7F6B06FC851F8C69954ABB25C5290444D452BC0FD567EB7074CA1E2F8D1EDE20A29B4D133331C061A782C6734C85DA20644D01BCCB18F7E3EDD6A5DF7B1DF1E47E539BD217D3DB9A7DC8819B171D24BC4D64E1D691E9641D32C370F4B4DD6C2E6E7477DABADDBE1F5FA7CB1DBD96F7076476487E20C5F938254B1ECE49E6EB16029EA93427D912
	E50F2FBBF410F11BDCBE498B6531D37F132AB319EE709F6CCDE4D85BFEDAE78D70FE9EA8EC47B2BC375C2178B869E29CC9FAE0E554B24C36F35F975855B24CEEB6E544B9287E4A0DD90A1C955281A06E4BCD9B511794877AF24AC67396D659CD8E2016AA444DF60AFBA19D8394DB05BED6EDC39F5BBE7ACF322023B6B5BB0A0F532A38EFF32AD49F3D365A0C352C5F54EF22ED1FDF8DF6011A00E420C9C053005EE67D754D0D5B61D95BA15779FBFB7D3E5AC06000579DFC5F6F163C70EE3381C7410ECB1D7EA00C
	935EA7FFDBDDE2608FD2E2F5E4FF24444BF612E09F0ABF7E8DA24B7BD2B38C4AB66D85F3EA1222E3FE35DA361A7D345E12D95B66417BA2668BA19C7F8743E3B4B8EF2B338C27692940F354E0BD372DC0DF1C6977131CDDF529A683DFA67F401ABA2B067B10D4237A426BF84396DA47D935B01600EA005A0042C013EB310E9B4D3B072B63C69883C11F4B5B5C5791F0856583FD9E2FDB8AAAF53DFF63D0F5EDC4DC6E7DD14B71FA095B2DED23B95FBAE0D8AFF3623625CE2B657511D82B568B37BDAE2A6D145A41FD
	E305E843E758D8A187F89B26897CC50667E8F05E366798CE53E66045G717C135858CAD672AF91FA6EE3F8FA1D0AF3BDDB99CE53E66045G71761DD1C86746E4C827575BCD85C095C0ADC0812018FAEC63C96652910E23A6CD7BF25977B73D85CFA9AC87DD0D3E106C72F5CA2D2E2047556115C2C449533A0956EBC7045C87ECFCF7020DF7A51F94F4499E3F8F343BFAA568CCA11E473D09767B4E204438AD1EDE898C7486886A75EE4A0527579512CF845CAED9D2F3F03E3C095649E5D7ABE8848570BE3B1E5657
	C2B667BE886F8975D8D73FFF07E279876D263203D8AFFF5C348A1E6FC167163CB6DB3D27133A618A8EE09FFDFD3F991E7510FFA6D07DA1F882553FEBB7A582B5B558CDAFB560F8FD3D8D7BE57D722F131297EA8D7BA5DB1F9AF99F139B757D72CDE6AF967099C0F3009682959C06F981289D684AE1DCF75D7B47A87A28CFC6FD167A46785477035C6F110E35C4033C75BABB347C5C17DB05AFCB2268DB29BCB3147130EC04FED0DAEE0019F4E93A8671F6BE390D0E15D8282769C0AF9D46365E3E594C474081D7C8
	42DEFFB4889DD6F22BAD4F73EE5BC275C917A51FDCAF85A41F9BDEC2CA1F33ECD9220E07AE3F682B958C2773E39DF4581040EDDBD6F29CF4E8C797728FB03ECB16031E0EBED9421D04609FE63E04A2265E10D256D3C5342C73200C196F59CD796FE1D91F5F122B6957CE3F7FE2DF20050ED8E5FC5C4B7681A5103F7EBDEC4367EDF4DF57A3523FE0E915AF595AC651E6A3A1EAABF2CD3876095BC85375191564CB769C4247E51A529FCD027795461B88FECD87CA824AF4E279CB6CED86F21F141F5C46C1E6C32BBF
	5AF83A9E62FD63094268FD7B19C2633E7DFC21F15F7EF96150FD775705C6FD1773128BE9997D7DBE4DD8F82D104E471DFE1FCFE283D208A873466BAD349C9E1FC7BE69F1F7CBF23367C389F6CACE5A27F37753BEDD866F75CE1C137B37601CDC7861A3A461DC0161FEEE72C37654D8A76F0BD31D6A1C4CFDFA7D9DBA46A42F548B95D5676F73499C1F4F5693858F25B16C0D40A5AADBF438856A7010C7723AF715F4424817AD6CDF3E0895DDF0DDF605AD21FE28AA8BEB87CB9D659E242F45661DBB2B4D51CF95A5
	DD1E2054688B742132625662B2254F64127BD7847576EF04833B503EEDD0794BEF0079F2D17ECDC5FA7B3BEF007D5D427E1F48004EFE03273B4769697548B7349FC85B43BABF1C7E7E9B6546AE6E4378E6FDDDB44BF482018D618D6D93FF3D2C2F0BE6599F38C1EE6C666B7404E2BA07BC8BF34BDC2005CEDC2783DBA99F57E9E5B3D26777FA7974873C736F5339EDDFCD1EE63E4AFB1F47D9D688FE67B07CBB0661BFBE8C7F55E1783935C373778F433F98154F6B694CB63AAE0724CBFD929CBF68C666BDE9G4F
	905BD669F82DAEEF1FFAB671EE23FBCD2FE68F4471F3C546733AD4C477BD9705FECF51E2C24F98DCA7DF07263A284EC050538BDBC63A29D362D953B6EA63D9F1D08E9A7BCA0667C2A0AEFBEC57BAF88E95B70ABBC2F53DEC4738D79E24CB001CC7383E71C7F85D70FCDFF6E03E79104EBA9AAD5F71AA4CD782F96A2366DBDC0D795C10670AC13E5823B45F767D98CBAA95D8AD43DE060D4B58F4BC2F3F1F230D4B39C39506EBB0252423CACC6528BE0E717D6DD4EFDE135DD45804E36715A26D9E37B69472F7FA58
	B1D2E95F3B0A356BB3473ED83C941EB0CEC3F2A3ECFC4258C76EAD263E52F32682AB6D7C359DD5E4703919B7969EA16A7A596C506227D3CA83F433BA31FDE975C87D846B616AA1EDECEFC6F6F16A32A875E0EDD26220CDEABD1459EAB338CF7FEA236B0DD10C3213B9E9696E73D87AE48FBD503AE4971B10F84762B1408DE6DAAF547664A60C75B5AEE6739324130148B13B09479D529DD9CADD6483568444472BBD209D2F63811F8AB49B084F39979D34CF0CF2FAD56AFCE671AA5EC63A39946B717E5C9A45EE62
	B76EA70E0F179A469F47E76725A69FD3E2444755FA2C58D41651G444B7724A8DFBB6419F49C4B77C60ED179920FE33DFCD405F2C90065B723DC2ACBAFC74FCFCD4DA8D7DA0DF2EE40E2055C9F579A494DEE46FD6C49F26D98CAC8AA0ABA06121BF9D997B66B4750C3A5AC7E5182F3C98B0E217C6DABB5E368B874DA172F9BE6C8F1D6581E4667C2A57D74768C1C8B891E37FF33137ADE83BA5B01E42089C0714246319D4346978F060708AFEEB1E32CAE31C509F33628674679701EF942EEAAB821E2BC163644C4
	6B3A6C840F252D95788606FBB5B8EF1B23EC8C30B47064D6BC77ADF568E305B5A7A363A06D3AB4174B9FA4E7512547686414EF90C585B83EB54345E6D377AFFC2DB9EF522EB513CF72BEB3831E4F1E44F8FFB010297813FC9263E056D22A5F5CE6B7B900DA0082C0130026824DEE43F61BE2B877C536DB3409CF0DF1703AF866BA6B7065E16BF0FEDB249CAFF745CAACF756A9EC471B2A2E4F07BB864969FD28BFE564C3258BFDE897BE6C9974DDE2E4BE548D12537BB04950072D6B500778D3587E61B20A4D0574
	7CD35856EF6C20B1B61C871A7C9E1FCCF08E78EA895D7F78031EEE0F8F31C79956698F3243BB477EE7E77438421FF69A47957E3E53B82E70464E21E3E69916E8F1879E5F3231B5AE7D345DE486D2303D942B00F4034056D8A96605F40C402C15940B03F402403ED4CE315910CEB90DED19ED2267AD72814CBDC08E20B6A099E89250F42059C07301F25AE14E85F23623ECA11325EB0A973089C071C009C0A9C0D9E7A0BF109348FB066F5568BE0C4B1ED81C20E9E7EAC33F86A0958C410BC68EC5E63A72C45FD99F
	0B220F165B599E49A3CADD08F17A01D13F9E9FD11615C37815B14AF2208D7A4CD61E2B0156A86FC6B1E5CA34BF2E55E17AF8F52E0E3F1E65F96A2DEC56E7C3A1A944A6C63A7BD2F82F4A2B09C1DC6B082BD7A2F8CE2DF5ACD1729C147DF53B882E87FFA926733C41FA40B6F5FC1CCFB6E5AAE51AF08677F4DBD73058B024B34F42B8B80B7D27BF00F309756C6076F482F6DE07ABF16A5268633B3154F8FC9FAD25BE057CFD41CEC9BF06DBCB7385CFDDB99AE52917D60BF2C6E37232AB58AD8D1A833A18BC67C513
	7717C61BA7F81D27D7E96B3CDC744B4A15D8C732C19D0D87ACD607F3FD53A3746D967A2ADD28EF2A013E19003DE4284FDB2D55D7A1746DD8077AD28C7411F3F6D35EB923B63CDAC677BCE1113E092587043DE3E6DA5F012054A58503121B07D74979F334BFDDE0F3EA913CD71C43FD7E313DF49D703A867CFD30E0641C27791E580B7B1287A15F8C2039C08B4E717D896F535F6C276329AB686A0566E88EC81DA40549DA2CA85B86E9AF508420F8204473E873E8A5156DF505BD3DFD3D4CC36E5FFA9B1B5FDDF6D3
	0D8B73FE331C657578F4F91FE27339977205553CBDB44640C7D5133FDF8A32B08267159550FE5462693CC83FC0B687BCBE3691937576D09E7579305355A1F9B19772FE5831DC705E7778209B2A7D6F4026BCD5AE687205FABD72512E2E10A4F3FE4981BADFC5F2D46BBC4FADDBD47D4D927495372A7F6F6C1CF6382EC76A3CF8409FEEF6DD169ABCA1B83C8EA83261ECD59623F30A6C621A959A9E0F1821DE5399BA879AC47694FE41872BF4FC1467671D10D5FB5EF950457B46CBAEBC8FA56D5372D394FECE8772
	7F57EA44AF94FCC725915FAA782D15C6761D021FEBA8FFDE703F9EF14E13BBB8BFD6700355C672D385FF7BD9A37E4C8E1C236FB5E93F033D84F8CA07BA1EC92771BA1CE70097816650617C9C74ED36BEB4F56A6F29BC4970AB1D7AFBAAA5AB701ECA9C70AA5C38B77A82BBE3A4C0FA0E40A6E4623EEA1E400E39A9D6A341BEC9C22CE9A54AFA85E66AC259B182A3D5A87B2C40CE38D0F62E404A45FEEE0140FA2AD136218B31836BD036DDE09B05ECC0E049EBD1B601E2DD74BE1CEB9F91632BF42F991EBE295FCC
	17E60B2BDF3654435077514B88C53B2DE5A5665DFB4B0A7999FECA3F768CEFDDEA09FA064F6F666DD522BC654C75CA9B3C3E976FF53901975B0D73692E7391E7C66A084D76EE5063BE606AF63259196F93766E4BD464E7F763BE413D0F6A4C6A01718364684118C3F8AB462B629F3E1FB8479CB03C0B1654D21C5A52437D8C7728B107B1365A8CEE33450AB62781BF2507E2B7C70CDFDA174F2572B38D67CF0B6053BE3705E8793F0B60D3FF7F91A11F9FB1FEE99DEDD364F959FE7C8E16ED610F27926FFF14C53D
	E7D761D1E2F14AB3734582927BE2D9866F8B8FD72AFD610E72117485AE7714CD15ABD9BC92B95E4E5776EEA51A585FC5EF3765C2A7BDB9D93A68B48B1B42BA4D62446B7F2B2C7E297CC48F76173FF4C559D332591E4B7133911D0FE6F9B01E302792B1F2813110402C823B3D92ED8684F6D8640B3B00FED4373EED6C872ED8CA99663231CE572AA4216F11EA54D7FF91B157D9442EDC44B17CBD36B71E8669D982EBE1313094C893AFE2152594AB0434C3E077DA719C59A4B05FE61CA75C820B1371FCB84D0BF378
	0C9534DDE7C1FAAE906905F3A2D085D08B108CB4299773BE4372CE057433C07300F2FCBC66CF6379E5705E64433C65AB4640134AEA7BE90B12263690AB689D7D798FE916C68FD17C95E3C278FD027922BF376159CAFBE67B7F5CE3505F614856241709F174D0A07A5E753F589A9500BA3C82B447C73187B26B07300977141062C0E68650EA05772C0F2F0F0B047D9E3771DEF38CDB778A7D500EC09C9BE06BDEBB24FB7C684F062E617C517AB4499F69CF1CDF75075BA8E6EB66B44093045DEBEC9C25C0FA01305B
	E89D4E6E920D6D42G6C23822ADDE7C0EF77CEB6AE5BGF787506EC5365ECE04F4ECG6D6E9D566EB20D5DD9BA3BF38659DD49ECA483BEBF00EB58CFBE502E39B9DF4ACFC5993DBD73A5E54D3D240F1BBFEE27FA4F83FE6592464BFE3CEF87BCDBDD9786FF918230A9688FC8C1F900236C98211C5B666F3D3E3B32297B2ECF4EBEEAB93E87FC096D89E2AF29F54C71DFB3BCCE0373FD4905FD38AF19863C7CA0469AD39CF41D39826949C0D301E681A583250730AE732CB496B6E41C39459FF0CA1761D8260682F9
	79CED92F6F8E7177CD387ECFFB6C2BE47A38F90B22FD0B4B8F717EE0D51ED3DEA8E0B8AF5F476C778BBD400B810A83CAG1A8D14A24A574EFC2451F2688E35326C6A6CE1DFA431F074A3218A5343B0AF5B23C3160D48F046170DE2EA8591F2C5CA9E0772EC12872F89DC4ED62915F3AB79424AF3C2D439F0045C94A55FB465B9AB2A5CDD15C315EF8E484DB3AC5F51883974BE1EA7DFF91674C53377FB17515D429AA5FF0B726C5149DFB150B79130870126EBF85C46B7A26274098ABFC9F94E0F6ADB57D92DDC66
	E51E2FC0F916DD9EAC47571C1F19E87C25439FE401B19EFFF9CDFC972A8739F62049C0711779B7CC657BEC496038CD72CE141BF91947ED6640F3DE04DCD329F16C25076D1DB27B21CF76631C7F845BBB55C05A590FF37E584DC34D79E9CAFD601A9E8639897DFC4E2769A97D7AB97FB9366F0A87FCFABF7A3832C80DE9085BD7CAFD4DBD3CC27070C69456E572059C41FB3F4FABFB82DE0F94643C2BBB3384CF9B1F79551148B8085E5E432648B8085EE6C59D15D72D69E3BBB7DF23E55147FDF88CA1A3DB9FC320
	6DB4379FE38C5D270D788B84BFB95F081F95C67E5DBB0C78E641FF44210F21D0FE0560CF339B719B42F80EFE270608FEFCD3668692758C3447DAE2AEAFABA6E4A36131F75A667C3FF97B87E3308FE77AEF2F256FFC3E7FF5964E7785DFFB00543FB8EF51969265EEFE180F3578303A8FD858EAD24EFBFFBF207A1C14B3921F39DCE93DAA37D03A5E32F2DD478F2AE57C7CA1D56F8447487D595D204AED338EA1D731673A6A3E35D1FD9FB82C5A7A695A115862F2316F29F2F9576DA35775BD276A4F56A32ADE07ED
	A4FA79DA7FBC1363EB7AF27BBBA44A7E8FD67443206BE8478529D3EEF1F58B1984BB6DCF62DB845F96AA3CA759D821771ABDAE2F67C3F60D2999B27A3A797A70CB96F3626F2FEA5E837BBE697B720357265BBED59B3C7CFFF5C47F363450E17CEDA9354A785BF1E65550F752F32A225FC9DFD3253D134E5BBD69A8ED1B39B09F10813B2990488A649C407D5DC3D51B895B5F996C6D8E3A3B6937AD7A2BA9996D0F2F0EDE56BB2A077F2D5564EAFA072D43AF4B7EDE2A9F71AFD75376127D8186F26C81767DA8882B
	124C33AAB80F1DF825AE8673984693A27E59B300F10DD8917F1CAE305DACDFA22467886C98BBA34D03B4799031FF37233EAC019D2BC459F601A56EC7590040F2BBD0760A406E903209827B9E33118469F982ABF4206CC201BDD2091873A3446EDB07E50B95D8974BB7955289820BB7210D5982DBAE625BC982DB560165A83B021837C3441484D643ECB4C1DA965824CA0CB30D9758E355E8A3D6E0274D629E02401633FC64AA2C4DD7917BD1B556D50540BE37987DEB90589F96230D9601DDBD0B328F2A7ACCA8BB
	DDE0DBC59D4C3C0A775BD6E7626F9D732EC1DDDF43B85FF71752B35E086EAC36F8E42FF4006DE8F9CC6FCCA64692E32EE14C7A3E9D544E8CC84F86CA390676F3ACF88ECD7F986A7CE30C8B36D9B086ADFF0CBA46313C71101E89B467E35C17040E91026B43DDD6B3099E1B2DA8ABB16F5A3DC709697E4BF1D52EF373C864C63626629A761DE61A626FF3DAD4DBCBD60C44962F87C7B646C7670B3A7972E944CA46A036F1ADE2EE01CD34A096AB30A487364DEC017D410ED856CD9887F87A845DG55C37ABCD08CD0
	AC50D420D9C0F3006681255FECB795GD5GB5G3583850046GCD820A3B99675C1F5642329CB57E22FC78E093EF50253D4B14FCB47A5C7B4723434F3D8B0E32BB45B0E7368C8414BB86EF9CE57723589EDD2CCDCDB982EB8CB579C39E3A2273B350AF060CFB19C8E45C512A09FB517EBB63E67D9968C72C2DE681BE7BE6BC837933699D76AE98E3BCCFBB1B3349ADB0975F02F98AABE99E998A2CFC4BB8C933F09F1F328FF7CE9B4AC78758589D616307ECFEEBG3C65969CE7CF35297636F78CE5EF49B076E2C0
	670E88FB53193D0900470A727E4C9ED95E14D32AFD3BE33872EA632FFA7B09E0E325724E75A5D48E57465AD82EDE5FCB37685B78E936D64D85FC1EA84F554A48B63E1B1539A046EEAA0A41BA4E7EC0ADE36110716EA5437854962356B157FD361D887F6EE16B1C9B7284E2503F1315F4BC7578031E8F41C717179E02F1A420DCD0FCA70A0D413841F799B60B16A18663864FD58F657BB20D5F99863E13317A7A3C052D23D900670F956321BAB7425FDA2F275BC7F7F30ACCB5DBD3E9BD550FC5DFDFE7D8G52D746
	E21D5FF4C62D73D453F07DCA5B3775FE470FD57B953777B0DB1B2783BED358FB702CEA2FEA48B646FBEC516C4D9F2B3E733D7F5B194345D749B82398DD7130F29B8C4644CA5241648A468D0EF799D159B87CFB8A2BCB68DADEBFCE9F2F5E407026F17AF875D7F660BE219DF8896370FB56DB166B0B3397DE275C19D14B71F27D136DDD8AEF5517B72D145DF93FD5DF5E89E2DF64G5E14DB313C355991315471CAACD5F9160C0F962FFC6034D62EC6495722BCFB224A952E554ADDD172CDD41E8FC6153BA7221E26
	AB799215E7D2D4395F8E3ABF2E2579A017F91BD17F1DE79FCA2EGE44AF4F2FC5D7F6509687B0B5F1E98FEFF717A8963335D7C93034FF67FB8E1FC36FB730476EC473174D6BAAFF6433E28CD64DBA230D382DB513AC441CEFA5CF20F4073DA17AAF803C48DA23E2E157A554F30B6855B2841CEA9D8E1AB5DA3C111E1FAC55962D636FEF93CDE1E5E4972F8FC30E59275D14EFC7484DC9A3F77890C7A4D67C076F9D7F93F1B057C143BF77B060A6F14C5441BFE35C215BBE85F2C17BB840BC251E637476575F7EFAE
	B153EDF50510CD4DD16D4FD9256AC9583F49D8CFADB43C97546C027F4B0B056C1B392A1E85EF29FA3EB5CAFF5E4ED775D42FD55FDD6BD41DFF9BB22696D9B75FADD06556250E2ECCC96BD57BE99BD5BDFBAAB7C6373F2B3C829CGADBC6FEDAC2F7657E34D74DF8C2D04AC4E0307DAB03EE7D6EDBCDABD929BDC2EF1136A671D1BD59D29A35241731E28B68F6D677146F79BDA380D2B452A0DEB0BC7573ED3362B3EAEACD175FC47B4BABD9BADD4165FD1CF48DD4E461F5763136A78DF6DA178FB0A81F6862F07B9
	2F7DB6BC7FBFDB4376BE100E398D57133719AF4ADC2FF97F7A0ECFBFCE79448D3041FE6A2A3CAF513C5FE57974EDFF6DD335415B34AD9BE3BF93C5BBAF2F44584F03828B0AF85AD401BDF69AE5496790BB3486E573847615B5A8EB9618FDAD4A86847603904A4688EC0B0C32938596EFC159A401BDD900327385D6349EE5496791DBA8E205CD82FBE0BD468527884CA472CD9358B891BFCC1F00E7080919F47111E9880B0EBB027B76B8B6FE72A1DF21487BEACD8666A5B89ECF32BE45E5463236E8017C67A760DE
	7FC37613A33E573FED48B3267E0CBEC3F3FF096E83A7CE60F7E4543345AB2C7DE2810F93766E5D255A7B2FB5A3B9A3C733779268DC96E14F4D5AFCAE6073043D04F2551ECD9ECD1CC4EF4FFC3B7E6C74826BA7A500DBEFC7FB2F6CD66D6DDFBF1AB33B5EDE4F6DEA4C036B4B2D9ACDCCC32FCF3EDDFFE6CDE47DF28C6093EE97F746060CDB684F74FA9B538759F81475691900BFAB6AE87FDEC2F0FD19D9B33A357FFBD6D5F6D165C85626F231B67D065D2DD576B08ED50FED4D6874382AD4BDFF5B216A79F6E8F4
	FA566ED475541529FA9EAF981D1E2EDD2A1E5B4A5577785D2A4EAF5AC73EB77BC9052A634FFBD49D2DA35A5F713F8B5BDBFA89967F7857C8C6A8C8D68D4EDF7B051F3F98FF2EBEAF1524B61245BDECEF5FC8B27A69A1A19C009E84C705C5D7C21748B5EAFF2894219F53ECE0A5DBD8E9E4DB7E244B3BEDE92FBC1016CA524184BF82B512F4FA0E81CBC1EEB18D0EC0541E88E3502E3CC127FBF00A8E026D9A092CA1A4F68A09454A5020C73A4586F986CBD8B59226A89237C2169603948698A9514B895911F9E432
	147E3048CCFFD9E413C9FA2F47E711C98E7EB4484C6F08D0CEC86A84CE967D2D0FD979318FCD52DF6818693E4EE61649B27A039CA5A52C535AC9D5A832CE0B203D16C7746E9F5D3767F7093FEC8D2511450DE4AD8DC252BFE8C23FDDCBEE7A3BC7658FB29EA2CB82744744742A90B4E608BDB2C27200D7F2123C2E63740F3604E449DD0BDFEBC3B2D96D2416CA6957205286D728C726A7E43B13247B681F0EA42BB85B6B72F51772CF64F656A396551EE77E32D39C781B4B7C25DAACC2CB6E63596FECBABCF7FA9A
	C9BDC416FA7DAE77A1D7A7D4D0A3D9A2778425D00F5F6B8E877657B0C5197D195851C05D5B22783430969A4C94CEDF8C5FBBE1F97272C3D0F161A0A9E5B9E91C59A2F2386BFADCC1B0A5859BE57A27B07D41C2413BE9033C7519F757342712C5CAA1E9551B4381993C224D5B658F760287E61D8748920AACF7BFD5F64FA37FBC4D942922B259482B8CC4CB3BA5FFEFE9EDD7976C71058228BFD03FDE2827F9AC3ABC2B42F72EBE7C421B9D505E75A42D372C0C7D3B0B7DDBCE7F5D4510DD8C59D58E15B80F8C791F
	1E9FE69D2ED0661DC44C8F3419AD072025647FFE6D297BDFF64CCB01529DA2996CCFD0C0E56728390E7475F6C841F171772CDFB6A9697334AD8234CB205E45DD2EDE0F97064262FAB6AF07A9DE91963D1F86A3F0433005219BD82F621D01054F983AF9D077C897A6F85003A5CBE9AE7A3215A50B583FBBC6DDABD40A4F9D2554937AD29ED9046A48A260A05696C1BBB00C0B107649FAFA5E5053032A27B91A4AB689F485D1A720B45D84B42A862130157EE33B91ADF0A432DC58826A82C083B9D78CD2D4E21DA2C1
	77CC5B8D72FC26DA7014DAD265BCCCE8DCF059E74F53732A27F60D27CBE1E259C6EFA0F8BAFAADFD17FBAD2E8E197DC5F7BE1DFADDEC19604B0AE2ECD1AA49B8CDF2596F2B0EC81725A0660415ECA5C30FCB7CC76C741330132C09408E74795CDE6988CE6ACE129341FECF9AD0705C881CFB202C834C0DGAC874B584F124D6C6698AC6974322919DDA7E5CB3A535F2F263254BB25B41929EC8F4C1F65F1648B9BFAF7037A1131CF325A0FE8F40C06D269E85D7C597374A355531D9ACF33BFFB1E5E9CD927F4C31E
	7A592C535BA26B94BD4D96A89EC34CE470169973EC576409BCF894913C1FDC7C3176F7CA8B46E6C77DEDF0D12C72FB0ED87D6F14D277503A8983BEA9966F499C2F20FEC73B275A5C6B72FAE51777E22D4FCD6F2C9E9860FF1B004B1A76D07DA14C23B0156FC4FB285F97B4B27F87D0CB878866788353049FGG3CE4GGD0CB818294G94G88G88G87E525B066788353049FGG3CE4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG
	3E9FGGGG
**end of data**/
}
/**
 * Return the JCheckBoxSaveHistory property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSaveHistory() {
	if (ivjJCheckBoxSaveHistory == null) {
		try {
			ivjJCheckBoxSaveHistory = new javax.swing.JCheckBox();
			ivjJCheckBoxSaveHistory.setName("JCheckBoxSaveHistory");
			ivjJCheckBoxSaveHistory.setText("Save History");
			ivjJCheckBoxSaveHistory.setMaximumSize(new java.awt.Dimension(124, 22));
			ivjJCheckBoxSaveHistory.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJCheckBoxSaveHistory.setPreferredSize(new java.awt.Dimension(124, 22));
			ivjJCheckBoxSaveHistory.setMinimumSize(new java.awt.Dimension(124, 22));
			ivjJCheckBoxSaveHistory.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSaveHistory;
}
/**
 * Return the JLabelHighLimit property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHighLimit() {
	if (ivjJLabelHighLimit == null) {
		try {
			ivjJLabelHighLimit = new javax.swing.JLabel();
			ivjJLabelHighLimit.setName("JLabelHighLimit");
			ivjJLabelHighLimit.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelHighLimit.setText("High Limit: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHighLimit;
}
/**
 * Return the JLabelLowLimit property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLowLimit() {
	if (ivjJLabelLowLimit == null) {
		try {
			ivjJLabelLowLimit = new javax.swing.JLabel();
			ivjJLabelLowLimit.setName("JLabelLowLimit");
			ivjJLabelLowLimit.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelLowLimit.setText("Low Limit: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLowLimit;
}
/**
 * Return the JLabelMinutes property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinutes() {
	if (ivjJLabelMinutes == null) {
		try {
			ivjJLabelMinutes = new javax.swing.JLabel();
			ivjJLabelMinutes.setName("JLabelMinutes");
			ivjJLabelMinutes.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelMinutes.setText("min.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinutes;
}
/**
 * Return the JLabelMultiplier property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMultiplier() {
	if (ivjJLabelMultiplier == null) {
		try {
			ivjJLabelMultiplier = new javax.swing.JLabel();
			ivjJLabelMultiplier.setName("JLabelMultiplier");
			ivjJLabelMultiplier.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMultiplier.setText("Multiplier: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMultiplier;
}
/**
 * Return the JLabelOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOffset() {
	if (ivjJLabelOffset == null) {
		try {
			ivjJLabelOffset = new javax.swing.JLabel();
			ivjJLabelOffset.setName("JLabelOffset");
			ivjJLabelOffset.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOffset.setText("Offset: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOffset;
}
/**
 * Return the JLabelSeconds property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds() {
	if (ivjJLabelSeconds == null) {
		try {
			ivjJLabelSeconds = new javax.swing.JLabel();
			ivjJLabelSeconds.setName("JLabelSeconds");
			ivjJLabelSeconds.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelSeconds.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartCode() {
	if (ivjJLabelStartCode == null) {
		try {
			ivjJLabelStartCode = new javax.swing.JLabel();
			ivjJLabelStartCode.setName("JLabelStartCode");
			ivjJLabelStartCode.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStartCode.setText("Start Code: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartCode;
}
/**
 * Return the JLabelStopCode property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStopCode() {
	if (ivjJLabelStopCode == null) {
		try {
			ivjJLabelStopCode = new javax.swing.JLabel();
			ivjJLabelStopCode.setName("JLabelStopCode");
			ivjJLabelStopCode.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelStopCode.setText("Stop Code: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopCode;
}
/**
 * Return the JLabelTickTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTickTime() {
	if (ivjJLabelTickTime == null) {
		try {
			ivjJLabelTickTime = new javax.swing.JLabel();
			ivjJLabelTickTime.setName("JLabelTickTime");
			ivjJLabelTickTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTickTime.setText("Tick Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTickTime;
}
/**
 * Return the JLabelTransmitOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTransmitOffset() {
	if (ivjJLabelTransmitOffset == null) {
		try {
			ivjJLabelTransmitOffset = new javax.swing.JLabel();
			ivjJLabelTransmitOffset.setName("JLabelTransmitOffset");
			ivjJLabelTransmitOffset.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTransmitOffset.setText("Transmit Offset:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTransmitOffset;
}
/**
 * Return the JPanelPowerValue property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelPowerValue() {
	if (ivjJPanelPowerValue == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Power Value");
			ivjJPanelPowerValue = new javax.swing.JPanel();
			ivjJPanelPowerValue.setName("JPanelPowerValue");
			ivjJPanelPowerValue.setBorder(ivjLocalBorder);
			ivjJPanelPowerValue.setLayout(new java.awt.GridBagLayout());
			ivjJPanelPowerValue.setMaximumSize(new java.awt.Dimension(340, 135));
			ivjJPanelPowerValue.setPreferredSize(new java.awt.Dimension(340, 135));
			ivjJPanelPowerValue.setMinimumSize(new java.awt.Dimension(340, 135));

			java.awt.GridBagConstraints constraintsJLabelHighLimit = new java.awt.GridBagConstraints();
			constraintsJLabelHighLimit.gridx = 1; constraintsJLabelHighLimit.gridy = 1;
			constraintsJLabelHighLimit.ipadx = 4;
			constraintsJLabelHighLimit.insets = new java.awt.Insets(34, 25, 13, 3);
			getJPanelPowerValue().add(getJLabelHighLimit(), constraintsJLabelHighLimit);

			java.awt.GridBagConstraints constraintsJLabelLowLimit = new java.awt.GridBagConstraints();
			constraintsJLabelLowLimit.gridx = 1; constraintsJLabelLowLimit.gridy = 2;
			constraintsJLabelLowLimit.ipadx = 5;
			constraintsJLabelLowLimit.insets = new java.awt.Insets(15, 25, 45, 3);
			getJPanelPowerValue().add(getJLabelLowLimit(), constraintsJLabelLowLimit);

			java.awt.GridBagConstraints constraintsJLabelMultiplier = new java.awt.GridBagConstraints();
			constraintsJLabelMultiplier.gridx = 3; constraintsJLabelMultiplier.gridy = 1;
			constraintsJLabelMultiplier.ipadx = 8;
			constraintsJLabelMultiplier.insets = new java.awt.Insets(34, 21, 13, 2);
			getJPanelPowerValue().add(getJLabelMultiplier(), constraintsJLabelMultiplier);

			java.awt.GridBagConstraints constraintsJLabelOffset = new java.awt.GridBagConstraints();
			constraintsJLabelOffset.gridx = 3; constraintsJLabelOffset.gridy = 2;
			constraintsJLabelOffset.ipadx = 25;
			constraintsJLabelOffset.insets = new java.awt.Insets(15, 21, 45, 2);
			getJPanelPowerValue().add(getJLabelOffset(), constraintsJLabelOffset);

			java.awt.GridBagConstraints constraintsJTextFieldHighLimit = new java.awt.GridBagConstraints();
			constraintsJTextFieldHighLimit.gridx = 2; constraintsJTextFieldHighLimit.gridy = 1;
			constraintsJTextFieldHighLimit.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldHighLimit.weightx = 1.0;
			constraintsJTextFieldHighLimit.insets = new java.awt.Insets(30, 3, 11, 21);
			getJPanelPowerValue().add(getJTextFieldHighLimit(), constraintsJTextFieldHighLimit);

			java.awt.GridBagConstraints constraintsJTextFieldLowLimit = new java.awt.GridBagConstraints();
			constraintsJTextFieldLowLimit.gridx = 2; constraintsJTextFieldLowLimit.gridy = 2;
			constraintsJTextFieldLowLimit.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLowLimit.weightx = 1.0;
			constraintsJTextFieldLowLimit.insets = new java.awt.Insets(12, 3, 42, 21);
			getJPanelPowerValue().add(getJTextFieldLowLimit(), constraintsJTextFieldLowLimit);

			java.awt.GridBagConstraints constraintsMultiplierSpinField = new java.awt.GridBagConstraints();
			constraintsMultiplierSpinField.gridx = 4; constraintsMultiplierSpinField.gridy = 1;
			constraintsMultiplierSpinField.insets = new java.awt.Insets(30, 3, 11, 32);
			getJPanelPowerValue().add(getMultiplierSpinField(), constraintsMultiplierSpinField);

			java.awt.GridBagConstraints constraintsOffsetSpinField = new java.awt.GridBagConstraints();
			constraintsOffsetSpinField.gridx = 4; constraintsOffsetSpinField.gridy = 2;
			constraintsOffsetSpinField.insets = new java.awt.Insets(12, 3, 42, 32);
			getJPanelPowerValue().add(getOffsetSpinField(), constraintsOffsetSpinField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelPowerValue;
}
/**
 * Return the JTextFieldHighLimit property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldHighLimit() {
	if (ivjJTextFieldHighLimit == null) {
		try {
			ivjJTextFieldHighLimit = new javax.swing.JTextField();
			ivjJTextFieldHighLimit.setName("JTextFieldHighLimit");
			ivjJTextFieldHighLimit.setPreferredSize(new java.awt.Dimension(58, 20));
			ivjJTextFieldHighLimit.setMinimumSize(new java.awt.Dimension(58, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldHighLimit;
}
/**
 * Return the JTextFieldLowLimit property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLowLimit() {
	if (ivjJTextFieldLowLimit == null) {
		try {
			ivjJTextFieldLowLimit = new javax.swing.JTextField();
			ivjJTextFieldLowLimit.setName("JTextFieldLowLimit");
			ivjJTextFieldLowLimit.setPreferredSize(new java.awt.Dimension(58, 20));
			ivjJTextFieldLowLimit.setMinimumSize(new java.awt.Dimension(58, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLowLimit;
}
/**
 * Return the JTextFieldStartCode property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldStartCode() {
	if (ivjJTextFieldStartCode == null) {
		try {
			ivjJTextFieldStartCode = new javax.swing.JTextField();
			ivjJTextFieldStartCode.setName("JTextFieldStartCode");
			ivjJTextFieldStartCode.setPreferredSize(new java.awt.Dimension(45, 20));
			ivjJTextFieldStartCode.setMinimumSize(new java.awt.Dimension(45, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStartCode;
}
/**
 * Return the JTextFieldStopCode property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldStopCode() {
	if (ivjJTextFieldStopCode == null) {
		try {
			ivjJTextFieldStopCode = new javax.swing.JTextField();
			ivjJTextFieldStopCode.setName("JTextFieldStopCode");
			ivjJTextFieldStopCode.setPreferredSize(new java.awt.Dimension(45, 20));
			ivjJTextFieldStopCode.setMinimumSize(new java.awt.Dimension(45, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStopCode;
}
/**
 * Return the MultiplierSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getMultiplierSpinField() {
	if (ivjMultiplierSpinField == null) {
		try {
			ivjMultiplierSpinField = new com.klg.jclass.field.JCSpinField();
			ivjMultiplierSpinField.setName("MultiplierSpinField");
			ivjMultiplierSpinField.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierSpinField;
}
/**
 * Return the OffsetSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getOffsetSpinField() {
	if (ivjOffsetSpinField == null) {
		try {
			ivjOffsetSpinField = new com.klg.jclass.field.JCSpinField();
			ivjOffsetSpinField.setName("OffsetSpinField");
			ivjOffsetSpinField.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffsetSpinField;
}
/**
 * Return the TickTimeSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getTickTimeSpinField() {
	if (ivjTickTimeSpinField == null) {
		try {
			ivjTickTimeSpinField = new com.klg.jclass.field.JCSpinField();
			ivjTickTimeSpinField.setName("TickTimeSpinField");
			ivjTickTimeSpinField.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTickTimeSpinField;
}
/**
 * Return the TransmitOffsetSpinField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getTransmitOffsetSpinField() {
	if (ivjTransmitOffsetSpinField == null) {
		try {
			ivjTransmitOffsetSpinField = new com.klg.jclass.field.JCSpinField();
			ivjTransmitOffsetSpinField.setName("TransmitOffsetSpinField");
			ivjTransmitOffsetSpinField.setMinimumSize(new java.awt.Dimension(40, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTransmitOffsetSpinField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	Series5Base fiver = (Series5Base)o ;
	
	fiver.getSeries5RTU().setTickTime((Integer)getTickTimeSpinField().getValue());
	
	fiver.getSeries5RTU().setTransmitOffset((Integer)getTransmitOffsetSpinField().getValue());
	
	fiver.getSeries5RTU().setPowerValueHighLimit(new Integer(getJTextFieldHighLimit().getText()));
	
	fiver.getSeries5RTU().setPowerValueLowLimit(new Integer(getJTextFieldLowLimit().getText()));
	
	fiver.getSeries5RTU().setPowerValueMultiplier(new Double(getMultiplierSpinField().getValue().toString()));
	
	fiver.getSeries5RTU().setPowerValueOffset(new Double(getOffsetSpinField().getValue().toString()));
	
	fiver.getSeries5RTU().setStartCode(new Integer(getJTextFieldStartCode().getText()));
	
	fiver.getSeries5RTU().setStopCode(new Integer(getJTextFieldStopCode().getText()));
	
	return fiver;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldHighLimit().addCaretListener(ivjEventHandler);
	getJTextFieldLowLimit().addCaretListener(ivjEventHandler);
	getJTextFieldStartCode().addCaretListener(ivjEventHandler);
	getJTextFieldStopCode().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Series5SettingsEditorPanel");
		setPreferredSize(new java.awt.Dimension(380, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(380, 360);
		setMaximumSize(new java.awt.Dimension(380, 360));
		setMinimumSize(new java.awt.Dimension(380, 360));

		java.awt.GridBagConstraints constraintsJPanelPowerValue = new java.awt.GridBagConstraints();
		constraintsJPanelPowerValue.gridx = 1; constraintsJPanelPowerValue.gridy = 3;
		constraintsJPanelPowerValue.gridwidth = 5;
		constraintsJPanelPowerValue.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelPowerValue.weightx = 1.0;
		constraintsJPanelPowerValue.weighty = 1.0;
		constraintsJPanelPowerValue.insets = new java.awt.Insets(15, 5, 5, 35);
		add(getJPanelPowerValue(), constraintsJPanelPowerValue);

		java.awt.GridBagConstraints constraintsTickTimeSpinField = new java.awt.GridBagConstraints();
		constraintsTickTimeSpinField.gridx = 3; constraintsTickTimeSpinField.gridy = 1;
		constraintsTickTimeSpinField.insets = new java.awt.Insets(30, 2, 3, 2);
		add(getTickTimeSpinField(), constraintsTickTimeSpinField);

		java.awt.GridBagConstraints constraintsJLabelTickTime = new java.awt.GridBagConstraints();
		constraintsJLabelTickTime.gridx = 1; constraintsJLabelTickTime.gridy = 1;
		constraintsJLabelTickTime.ipadx = 25;
		constraintsJLabelTickTime.insets = new java.awt.Insets(35, 17, 4, 1);
		add(getJLabelTickTime(), constraintsJLabelTickTime);

		java.awt.GridBagConstraints constraintsJLabelMinutes = new java.awt.GridBagConstraints();
		constraintsJLabelMinutes.gridx = 4; constraintsJLabelMinutes.gridy = 1;
		constraintsJLabelMinutes.ipadx = 21;
		constraintsJLabelMinutes.insets = new java.awt.Insets(35, 3, 4, 2);
		add(getJLabelMinutes(), constraintsJLabelMinutes);

		java.awt.GridBagConstraints constraintsJLabelTransmitOffset = new java.awt.GridBagConstraints();
		constraintsJLabelTransmitOffset.gridx = 1; constraintsJLabelTransmitOffset.gridy = 2;
		constraintsJLabelTransmitOffset.gridwidth = 2;
		constraintsJLabelTransmitOffset.ipadx = 8;
		constraintsJLabelTransmitOffset.insets = new java.awt.Insets(7, 17, 16, 1);
		add(getJLabelTransmitOffset(), constraintsJLabelTransmitOffset);

		java.awt.GridBagConstraints constraintsTransmitOffsetSpinField = new java.awt.GridBagConstraints();
		constraintsTransmitOffsetSpinField.gridx = 3; constraintsTransmitOffsetSpinField.gridy = 2;
		constraintsTransmitOffsetSpinField.insets = new java.awt.Insets(3, 2, 14, 2);
		add(getTransmitOffsetSpinField(), constraintsTransmitOffsetSpinField);

		java.awt.GridBagConstraints constraintsJLabelSeconds = new java.awt.GridBagConstraints();
		constraintsJLabelSeconds.gridx = 4; constraintsJLabelSeconds.gridy = 2;
		constraintsJLabelSeconds.ipadx = 21;
		constraintsJLabelSeconds.insets = new java.awt.Insets(7, 3, 16, 2);
		add(getJLabelSeconds(), constraintsJLabelSeconds);

		java.awt.GridBagConstraints constraintsJCheckBoxSaveHistory = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSaveHistory.gridx = 5; constraintsJCheckBoxSaveHistory.gridy = 1;
constraintsJCheckBoxSaveHistory.gridheight = 2;
		constraintsJCheckBoxSaveHistory.insets = new java.awt.Insets(42, 3, 26, 41);
		add(getJCheckBoxSaveHistory(), constraintsJCheckBoxSaveHistory);

		java.awt.GridBagConstraints constraintsJLabelStartCode = new java.awt.GridBagConstraints();
		constraintsJLabelStartCode.gridx = 1; constraintsJLabelStartCode.gridy = 4;
		constraintsJLabelStartCode.ipadx = 8;
		constraintsJLabelStartCode.insets = new java.awt.Insets(11, 17, 7, 10);
		add(getJLabelStartCode(), constraintsJLabelStartCode);

		java.awt.GridBagConstraints constraintsJLabelStopCode = new java.awt.GridBagConstraints();
		constraintsJLabelStopCode.gridx = 1; constraintsJLabelStopCode.gridy = 5;
		constraintsJLabelStopCode.ipadx = 10;
		constraintsJLabelStopCode.insets = new java.awt.Insets(10, 17, 59, 10);
		add(getJLabelStopCode(), constraintsJLabelStopCode);

		java.awt.GridBagConstraints constraintsJTextFieldStartCode = new java.awt.GridBagConstraints();
		constraintsJTextFieldStartCode.gridx = 2; constraintsJTextFieldStartCode.gridy = 4;
		constraintsJTextFieldStartCode.gridwidth = 2;
		constraintsJTextFieldStartCode.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStartCode.weightx = 1.0;
		constraintsJTextFieldStartCode.insets = new java.awt.Insets(6, 1, 6, 15);
		add(getJTextFieldStartCode(), constraintsJTextFieldStartCode);

		java.awt.GridBagConstraints constraintsJTextFieldStopCode = new java.awt.GridBagConstraints();
		constraintsJTextFieldStopCode.gridx = 2; constraintsJTextFieldStopCode.gridy = 5;
		constraintsJTextFieldStopCode.gridwidth = 2;
		constraintsJTextFieldStopCode.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStopCode.weightx = 1.0;
		constraintsJTextFieldStopCode.insets = new java.awt.Insets(7, 1, 56, 15);
		add(getJTextFieldStopCode(), constraintsJTextFieldStopCode);
		initConnections();
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
		Series5SettingsEditorPanel aSeries5SettingsEditorPanel;
		aSeries5SettingsEditorPanel = new Series5SettingsEditorPanel();
		frame.setContentPane(aSeries5SettingsEditorPanel);
		frame.setSize(aSeries5SettingsEditorPanel.getSize());
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
 * @param o java.lang.Object
 */
public void setValue(Object o) {

	Series5Base fiver = (Series5Base)o ;
	
	getTickTimeSpinField().setValue(fiver.getSeries5RTU().getTickTime());
	
	getTransmitOffsetSpinField().setValue(fiver.getSeries5RTU().getTransmitOffset());
	
	getJTextFieldHighLimit().setText(fiver.getSeries5RTU().getPowerValueHighLimit().toString());
	
	getJTextFieldLowLimit().setText(fiver.getSeries5RTU().getPowerValueLowLimit().toString());
	
	getMultiplierSpinField().setValue(fiver.getSeries5RTU().getPowerValueMultiplier());
	
	getOffsetSpinField().setValue(fiver.getSeries5RTU().getPowerValueOffset());
	
	getJTextFieldStartCode().setText(fiver.getSeries5RTU().getStartCode().toString());
	
	getJTextFieldStopCode().setText(fiver.getSeries5RTU().getStopCode().toString());
	
}
}
