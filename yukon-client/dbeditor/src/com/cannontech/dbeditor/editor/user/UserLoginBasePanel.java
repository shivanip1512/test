package com.cannontech.dbeditor.editor.user;
/**
 * This type was created in VisualAge.
 */
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.user.UserUtils;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.gui.util.TextFieldDocument;

//import com.cannontech.database.db.user.YukonUser;

public class UserLoginBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelNormalPassword = null;
	private javax.swing.JLabel ivjJLabelRetypePassword = null;
	private javax.swing.JLabel ivjJLabelUserName = null;
	private javax.swing.JPasswordField ivjJPasswordFieldPassword = null;
	private javax.swing.JPasswordField ivjJPasswordFieldRetypePassword = null;
	private javax.swing.JPanel ivjJPanelLoginPanel = null;
	private javax.swing.JTextField ivjJTextFieldUserID = null;
	private javax.swing.JCheckBox ivjJCheckBoxEnableLogin = null;
	private javax.swing.JLabel ivjJLabelLastLogin = null;
	private javax.swing.JLabel ivjJLabelLoginCount = null;
	private javax.swing.JPanel ivjJPanelLoginPanel1 = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public UserLoginBasePanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJCheckBoxEnableLogin()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldUserID()) 
		connEtoC1(e);
	if (e.getSource() == getJPasswordFieldPassword()) 
		connEtoC2(e);
	if (e.getSource() == getJPasswordFieldRetypePassword()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JTextFieldFirstName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldLastName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldPhone1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JCheckBoxEnableLogin.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.jCheckBoxEnableLogin_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88G73F2D1AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDC8BF8944711869BB0AF5B4296B17870FD84F3B141449F79223B48B63E10043BC002E36E8EAE640E584A19B8584189B913C44EB971680584C8A0C098B0B248C8E081C28FA0894802A0A4E4840B90C800F8A4E8F6F724DD343B336C0E50CABCFCD55DD3BD332BD9BDC848E7FFAE4D74DF5DDD555D5555555D33C8B9DB4727A5A567C8D2D21A247F7707A4C972B4C8525B975FBEC758225336F449745F07
	C0EB246BA7D26119883CDB93EC6959D2D6F18624B36BED6913465A52B7407BBAA92EAC2696DE2248F181EFDE43ECDF4FF246FEC8F252D93A28A3931E2B0032C156FE208175A83F3AE3230EB7C1FA157E0E1414ACC9874EE01E1F35ECB461B374FC19404B83DA4072DC6D48557983A00DF94640F34A991EF786BC17B36D86179F5073DD99B5C9DA7A6D9D49D9A84BD4BFD141D9DE76387E4C131EE19D11A25525D54033223E2D7530C37596BBE41FCF75E90A43D56C34ABCE3726860A75C7FBD08914171F033FB635
	556DBBAA8715FA5927F8624F224EFF6749E075014E102EEEC0EC1D977B699DC867C03A2801747E372C8ADD2F5836C9D242131B226ABD34016BBB2541503B7E7A6BAB2EB4E27DC359B8E0394540AFG0C6BB22703F4995FC83A28F64CBB9F52CB00B24E917EFB8762F910BEGD4FF0EF4BC74916958F4732BD2734863E92B236868BA47F50BB9E768B843717A0ACD4CD6366763F84F865E922054733674C22063C0AE204D4CC693BE5C854FCA3BD625FA3D2A2F526FBF6AF186CE29CE45836F6565405131330AC38D
	B8A5095B61F8CF1105BEC4834E075BA092EF5B85096CE64A59E7247A0FEB5352AC5A96B72FB0B9D62FE352F923ED8B4FE33FBD4046F6A63CA76975F2BC166105978C1C0F55A606E33A9AF813AED0BFB72DA2DDEC2993246C32134969963A0C794611644CA62EC3C113218B6F63F78B300F6B8177G8D829A87B48DE8C19375712EED7BFA6B63DA307B00CF76B4345B7DF2C0BB5A6E76B815005E578FF5745457D6444B0D6BF7B95EAF13761947E841D7CED8764B02C9A54909FAFFE4DFB47A054B1E97D5F669456E
	3631CA0C612BEC2ED4DF64E318A270B7995EE842795836B39C53CE604D846279A73339D5F4A952C63E4A706317A2ED2442C1B662845ECCA0BE3ECF6CC0FFD8F499668DD0B3D088E88CD09C504C4BB4466D5BCA7AB80F6ACD634B4B96673F87CFA5248564DADFD013FD8E653C9CF04BF60F921474BCFF4847FE3507152BE0735B81B2BED1FCCAC0565C2A8FEA173D8A98D31067B9163F8673849446EDF4FB95906075CBD46F297CF5707448C16D1C5FA9EB0A1103734F65E31F5C104FC30DD0813CEF3C427A11795C
	C5507E54AB54D75F7E8831AB10562ED03F4C49DF8B4F13E05C0A273C3C5A6DC0B564C0A75968035EECF88E057C29FF0279831479E71A1FBFEDA2BB2C4E7A12D4B42F56528E197F7B336EAB7E9CE9070F337A67G3E84A87B2AAD3D74AA2D25DB3DCDFAF9149DEDCE20BE03FA600B7562CAF89EAE63001011564B3CCB6F437CCC09C873FEBF3093ED379E5A65819AF9157AFEE5C19E37C9DCAF498ACF8740009427B192BCEF46F6DCE3149B0ACF2BD67C0A4F89AFC15D86D6EFDFEE58E70BAAECE7BD4351DFD50181
	85853EF97B9A0EC39DE6EBA7FEBE634B1A96F05B5BB525C151B4372F55A83F1B69928CF305C13D2D653B302D93210D5300E60B3646EDDFE732B31B2A36357B9BF18669763A182D4B8910BFD10C7F6BF60C2DF21BA97D96CB9B7C1BF69A3BD5FA3E7E78523E796858B09911637B5711B15501F3A0C93741F43D1FB96F85462B0636F900E20126097EB86AE8120C79C6E57BA2F7F31FF53B4CE4245C050C65FD54E31D74540E68367D4C8EEB5B0E5BE1ED5B4F6D6859F63F31434AF6B9EF568EEC1F5A61B34D056F6E
	C07F68D0FDBE05CDC8D1C462B172CF9B319DEE1FDB3B60F636AADA033BCB811FA423CDFF17796005B0DE094D64B3BB7249E796F4FDD11AFE35A059AA3E9A738FD54975B237E727EC78CC2E53CB9F619CD3BC0A97BA2ACAED77E99C1F477C3B0E87D3A43EFE7830EC6109C668439A3762F1E214DEDBDD6400692F95333FFCE5A93CA65F10C345418E682FE2B69845D548BD062FBB73766CB91C57BD40AFEAF18714DA1F3F1DAA59F9FEE7A93717F55261939A17DD8FA3548141A913CF09FB2841DEAEFF726DAE8B79
	E7952D532F7C8D3458C77DB0A47B8657234A25B85A0E2A21E3BE74FC2C06A21919D5B1FF54AB0196B560D51CCC09E239C353B528E46CFE2BD0360F56A06FEE3419C1E0CBE300264A64174F94A21F7C323E78D4299E8FB7F760955A19ADFBF3CC76B9D366FB5B9C60F17EE91F955FAF78F3AC7903847F0CEA459FE3A7BD8F6FC4BF98D42E37AB90FEE1771DD4BAA9065A39B602F7DE76349B3159291DAB6169B12DB99CBF324BFA9E556C42F526CD54FFE257B289E3ACDEA71F77D3E52C539FF0FBE10946C5D45F43
	27044CFB78A920BF6E779743D3F2585239BD3CFF937B3C2ABD2829DEA5D085FBEA98FDB618ECA0311C836B7A3903767A9BA1DD8AD46F60750DF468FBDF16AF4EC97992B16D043CCE2BFC9D8A6573GFFD054FC3BDBA85FC460CF83DA9011AFD741FCBF927B7A92013D4330B2D8A45ECC217D4A29EC73D9C441924D5267A1B5AB3CFECD095C47CDA842FAE3811F2810CD4C58E50EA9AA03C15561E6E134BE3E415DE6FF4831506E957004299204BD1DD389110D5C590D3AE21C2D437AB8BF3D7B1054FD5FF00C9DFB
	D048D65541A6E623DA85217EF1B033AA31DC1CF7AB9DD01E1FBD2479283F6295DA8FEE57184FBB262DAECBDE9925EF1668FD125DE2744D95476BABF85DC3C45D75ADF456112E92B6D4E0C385B69DB05E463165387F35BA67F130C69737363B0B5BB5B7EE90E4CDE6BE06DBAA2F539B42BAB1AEC8EC21331309B7F1EE1536423E8C2899086F635EF7E66AFD9B82ECEAAB4D7F93A966793F8870EC9778G974D1901CE3431FEBAEBFD8CE7B07F505DBD12FDCED8F7C417BB7B1989D24527EB132D4EF3062E4ECDBE6E
	62FD6FF799E3B0FDD7B0F9C95A9B7A39538FC47B26C11E25AEEA5F210B66769539F97B1A5C543EEA65EE5A47E50ED7C826876A1B68A6195FDBEBA5F3011B462231054AAD41B25728DCFE3939DC73B59E138F3DC63ABE5F32971E7DDF38449953DE33BF181ED09A559F0C3B467BF96A35C8FF70F9F61EBB9B7004EB648F5E5E3D46648F4E428C14FD2D606DC51CD9047A03DF5753CF9721DC6657A50AF7D7787AF3A633749A6D2DDFAE41BE4BE8332597GD5GB582E9ED245B29BD3D1EB19D8B75F046B422DFFA45
	36519EA1D36DCF39C9225C037DAAB7CD148B04305C1CB6E3AF3650741E5856FD0F464FFDB65EC2DB4C7470F31FF40197B03C4004F35B1D4C7C8B26AB00B75EC372731C11FA27FA43E566F92D4E810A3B15AB0EC8D7C714633EE5FBA12E154CF71A26EAF93991EF733860CF374CF14195AF1FD38360996BA55FBCB0A5C32FF70A176CEAFF99563F9052124F163E91E8BFD0931086B446C776B5EE0F55BA95EEDF5196A9E37F4F7B62553B6C431F775A0771BE2BBE7C27ADF956FD98963773BE52AED29F4D715118FF
	C139BBFD633A150B54B7C38DAF47F5183B1EF448D3C907CBADF727032BDB39C89D1AD42BBEBBFCFE33F51F519EC35775D2B92D4DFE15ECCA5B0B58A4C8472B42F76E41FB96721D752A5B07FBB74E7BC2B1463FEA408D91AAE1CFB34C218658E6191F61FC48E21019D067C2D16FEBA5517776EF16D86F6D5FAA315E5B3FDD527339D5D2E9343DBF57EF0250AF9108E379AAE119FE42A68AACCFE043D80C339F52B601ADE1D89324BD823B5E05D88C24A77949B636B09FB69B520B00B22E933E0B6138A6E30CDDA870
	29B74DE7CE183F2FFEB89A21ECFCAE92BA4D8A135157B31D1E48FCC67481CB4FFAEFB59B2FE0D089B2670351234E7B3136DE32B8D729133D0AC47B668B4E65FA1EE31ADAD5A6EC7F2D1D9B2DED5FF467B87A96593D6DBA4524E7DCEC8F8C69F1C0F1223F07247434564D06FC8991E3206FC94B225B7631B2EB5BBED90673AC2836879CCA247D1EAD5BA8F806F72E5594AFF68B4FF319155764C02B524D7E6D2CBC67C5AB5FD696ED0E68FBD3455C67FBA572D31D97FB6A232550BF990170BE62757DA02C3EFD223E
	6F2F6F293EDC282B54323ED1AD667A760B7A2E161B6B2B564BB669CFCF207B9C627D513997630A10C85FE669CEE1EBCA65AA512747FCCE5DB1723D6E8166CF5625CA524392E14FB22C8C30DF892C1C7913FD0015A49176BC43B6GD6A55AB6BE04FBBFCDD5BD0DEEBF468F5200GFEF3306720A47224F7225F75481DEABB6831B108F5ED698C7BEEG4AAC46F2C15EE65E0EAFF7E05DAD81598B435D60D79C52D2D6F6E89715ED063486948B9487B4B7C8717E1795BCFB61334BB826603C22F40CFB2DCE36B87FE9
	ED0DC5D91C4BBCAE3A52E09597D5E83674FA0D666763374CE36B827C16E60CE70C96AD261C45667BC40D470ED9821F4F7019A61C471497980E694540EBEAA71F7EC48A797499825BF499311C9B6037EF9096F31172D588EC60DA4AB7D2E0CB56D33E7182FB71A065DBA2309BEC6DCE6D302567F490769FE90895C27A00408EAC237A2ABB68DC61254B66BDD67663F94958D62BBDD6EC876F3F719D115FFF14B5074536382BABAF7FA460F69E15DBED4C82392F2D6E4C506B1E5BC13EF6CEA70B67C3309F822A8811
	DE219DF45EB1658B93A45B0093165FF7A4B416A5B70638BE21102157FCF15E71CC97490C837E6290E28B3A4C7BD6343FE713F9BC45792BEE1A79E8173896997CA3E1FC5437B2454C8F0471310FBE524B7336FDE7BB356D1D6FCC15BC7FF8A06A37AB7BBBF57F24BF8BEF64CB19E5EF68ED0BED2035AB26137A33DCE6B68B69842044CE9A7BAF33F5AE3B8B7A390B303179089D07749501ED5CCEB6B2200B6655CBAE1CFBF3A03D98A873A658B250F1A04FCD4A73B24B938B69381B7C8CF26ECDFE46086707BC4F1F
	DC4D916D43BA4C69CC3DCE1255DBACD1F833FB9EAEAB54CD56CA0BFCD97A7877F427EB9E67EE72EF31F355146A9E4A2569B20816024E99D0EE3D4E4B057797137878F3DF786FDBD0B70539C827B40049F861A01D4779A14D65BEF531B739E659B3EE054B1D8F6FE7F439DC466435A4E32110117EDEEFB2524DED0B1011F1BBD246AFC5BBF2EE533A71E0B91E233728BE0D67F91279338260170ABCEF24E19E8D96BB3E56DD7DD8927DFFFAD9CFBA26C4D0B3540914AFF13DB4F82F0467EF5EFD77DD3E6F49446929
	2F73578FEE0767F9791A65096E89F0F7276D57A000EA14DB056DBD55595F6FF578FAD35D05F3F740ED3E5EA4897C9443C71AF03E8E7DE7A7FD3BB686F86F5C26FDDB51A11C0B4DF7E07D830A851A82B49BE8519D5AEFBDFB30D46A659C20D1755BA0E8751836D03CFDDF693A3B6F113E1ECEFEAF6E3949D27C104424E8E7586F5C516F8FBE654F69DABE43F97B3EF6985BE7831E8BE8A450C420F820B91FD27B12D81C072799B0E0151AA6BBDC6CF41F9A07876E860C018EEF5B5886F35B96C2FD091F9ABEA6F3C0
	3498629966F7738670B1CA9378338C5FEF4279589D1169CC75B870268CA0BB1FF98B63B63B9AE081BB0F5B4624D16C13BD9066A51093A8B4101F5372F2D3733B47FB5F4A27F2B11097633DC9701C261763F327E4FBEFF3476CC366C259C583795C4174BB707EAB18B7EF81F1BD3CEB315C891BECD7BCF87748B7EA1C7778259C412BD383DE59C387B71CFFA609450F91B7501C774B8BC659701BD94E7FCDBBFEFBE5FD03CC72A75766F64B93DE97BF574EF11B576F0C7BF8FF955DC7675E6FED35629F917CB78EDA
	719B613915698118C47FAD79E3F7CC5263794F9F11445C79E1A64D1D5C271F142A3F1718C47EABF24CDC77F19B0D3F4F0807767F7D9BABF89F4CBD487CE820DD61750E6FB047FF87D67CBE6A997B923D3E547B79B37629FC492ABDDC7761C7A5097A7D6BEBC9E6747CDF2AB272E715D3FE4EEB2AB5F85FCA436F29FDCAC71E38C028010523F0DBE540ADFB36956515417F7BF7C21E687DBA7D843E7379FE224BF8EFB03DAF68B41FFB7C35094B7845BD15417D606F8EC5BFFB28B8E4FD76F0741075395A71C3BDFF
	B3D3FFA87AB7B3978E193F1961FE7C6913B8CFAA401E1A00EE8145GCD3C1FFC780F8EED13188F37705F471C2DF87601DFD9EAA4FF666168EDFD69F06F5FE64EBF0CF73EF6D553D4AF56CF784B07F13CB4554FC00E3DFA987BA860EEF5E9BC2B0E3FFE986D4D2334B0184754D3D91CBE9D5AB577FE0A337FF9B3E209104E9CC4588FF3A976AE9DC4B1F31C0C58GC80F93585798968F6979822B117F92DB427A69FD5C20E8B175553FC806791BB82BF81B2F0B535368BB60C45023F4B05DBD3F30826315BE5DBDB7
	3AB50FF2142D20FC1F14310C4E61DD03E91F745AB614B39E5271C0F38653DD674F72A926DA84D85E903AF379C3816DA31B06D09DCA81660D0174A42069C37A6A5F49679DBEA5C9622C32A54AD9A5DB71388EA75DD8875A5DFC2157292D04E5BFC0580765049DFFC04404F5384E8F0074B82069C073019683258E857B83AA82BA82548464879A84B486E88A50B420B9C08B07525CDCD309BE56B2AE86D05F7C33899A10799D81576311534C6E211E1CE1D4D7D3656E9E6A2A8F28FEA520F5F214FD31234F352F1C0C
	BE5F1FBB597BFC7F66C9765D874C53C60881F49FF2125DE13434849561D7DEBC19AD305AE03D9AF463F9293E9EB5F40F57DE96E7D57B0771F84D864F66E17C6C1E171B3739FB3C36F8F36479DC884A0C9C266F7DD93C16185BDB3CE65E0FCD043271430CF8EDB63C3F89315ACFC43CB6A21F45DC4C4A58399B7B3E097354E52B8D1E9C641F9E695FF97DDC0E0CF130ED7307D18C74205D0A3FF898792BFFCD6BED8FA18D373A871B5BEB39AD91E9DEAEEB99164B9D5E37BB125261747B03CB4C9FD48F0F5C13B998
	DEBFBCF2CF36F89B790FAB401BBE1C5A7B0B7C3B7B6DC7CA415D152B68F7B95E2E6FB31FD6B0A2323D7F43705291116DEDAB2076D6GEF7C88EAEF3D5BACBFF5A46F770D7A33881EC7C6DA0D6F592D66F20DFA9E177E3C854F984BF2BB424E75A76AF9627567ECF8A6C414533F6D2B0B6EC3BE296B5D075457D9478C676B3A478C176B2CE386394E9CB3F02C258E5705D67023CDA2DF1B40AE8A4C4F6AC46C025B293984FE03690578F1858592FE0B69554130A69D9BE842AE6A5810BA74CF50E4179A2032A36A58
	B90D5B63616907D99E378F5C22680FD826235BAF1B749EABB0C36F47CE639808D8123BF51D777727F3A47E2DB404746EAE7629DE83E47435F21BDB56C8B142D298532287E95D2BEB24F55E79A02D51ABB7E35EF1100E831AAD722E4D2575FC2148FBA4977366BEE4CB5F8FE4FB283F6BF94A99CC717A8B6D484773C567C3A4F30640E682466FB7272F4BE2ED7538FDCA957F8510C4775085ACEEA9FA9874FB18E2C45FB6F6178CE94F43B4571A513D6B7340EDFA7F5FED77FE8F33B27F5E4BF82540E853A2537B75
	02FBAFFB78BD154147341545F5A1984F21E21C6FABC5EC9C2427886C472534BF08975873DB68BEA8A306B077D62AAFD7E0ADAE2A2FD4E05FF209FBA801ADCD9677D082BB44624971101EAA30CDC9084D0074DC015DC9227A92847663A62A2FF41478562C716E567E77FBADE73D76B730F255237A3676B70D22357FD366BBBC23A25742218C8F0D0ADC8B57EC2635F09070960976EE483D3B35B80E79A35BA31172DFE0F853A31172EBF2C93E93F8B39FA179FB6CD6B6DC74E8F8FF9CF934FB7FB4C6E0AEC85F820A
	B1617AF7A216B2A6C6140F3710B1BB82CB0074D2200CD883677168474BF02D53F0FB07E7BE925DA9B4B0BF198B798BE3A93E9E3CAD0D72CAF46E33632CA46A3A638E3FF308E5F330894ABBC59DE3CB436F9C3486AC4F63705F1676674EA143F4AF0263938BB29C923FF36072DF5FA2FEEF8EBC0EBD426671D4C8CFC30C6970D82321434B2E7E1C5DC66A30986A8C066970C18A6910A87A20B162EE669A73859923E17FB61A7214A60577532F4F99BA0ECE6A4FFEA5D247EA1031A6CC476C0B627B755164E32E31F5
	3F9952A101BD7ABEE2A3A1BDFEB44D034198DE50EF31CD6F9BB65F7BF52474EFA06339DC23CD5FB43D5BF26F3DEC651E4A60E3FA1A45CFD3E0BC2709F10EE358CCC8AF96D8060345E61F03774F9116AA6E358B84768ABBC753A0BDD4E0BCEE9B8769E9827B894366FC4EACF7230364A608FC8E06AD05F46AE304D588399B85B60B49F5C17A1640568A39B10F51DCBF0FDD2A7BAB7B363E1EFB73322FA499E5EF14EE33AE6B0422EAAB94AE558B733249A9C6D9E9EB78D97C93E9866F122BFF7532EBED3D6C7E1470
	FA5FDDEE7032127AD76F2115C6590FAED23D7A6FC19A2D570C314CE7CC043E1E7A987931496FF7DFB31AB30D3ADB6C61FE6E7EAC03779DBB6E95E133646E425FE9F970920BCE0AC866EBF65A1FA48819DB9D617E75F74C06B24700DD0C21BB3A4D7D3A23237BED722147219E7A3EC43FCB133BFFDF10E5A73F5AB806F47B23FDDDD81BAABD6ED69F1E426BE5AE0A76B8C519E40768A3162752C1F9E2C41E2E08B64730BC5300BFD334392157B6DB5D69D31B933B35B941224D3F12294D29E3E94D7DD834A9E7AC4D
	43D7D69B637C407BFD1D077A3D249D770B66DF67B460DF3A3A88965B0E55B46A36B6F02DA1A73120FF73FD62BA23EC01BDFC8E787B24834F5B64683B3E52FB064C526D7D53777086236C2A88FD4F76C9DF7EEFB37D36E6A68C5814B725B4185B1A14454EA8737867054EE0C0DAAD5429E5679489B77615273C7AE4CA3214DAABAD6367B635D2AA9E3E54CAE96CEC26D6CA51D4FF482F5B8F6A06B1C8F2703AF4C7CAB6D9D5FD980A83004FB38C8DC8D9E19D71AF1F7F5DEB33DFBE9FCC111655CA1B50769B14GBB
	5ED01C7839337E6FF254C84B7D7869A87EBC9C548A32C7DAD06B74A8B6A92765AC7EF6B528A94ECABAB889EA52FA9BCAAA414B0F1263F25025E15BBF36C929BE7C97C424351C6D11FD2DA57C9465E35E364AE69C82B61F12C05FF5CCDF2C25D854326E79555BBFBC31B0BEC5CA2E11D6F8D459D9A3BBB4B5D0ABAD57DC81A568D2BD4E107F0FC7D8C5996C1FE6C0DF1C8A55ED964D47469623BF94CA37051E981935A82B863AAB9410CAD8CEF49945A207334AA587C01492285570DFC4D1838502B7F00B366345CF
	B6DCC9161274C66290E4047C1A342CC5763ABD1D70D24D168AB6B819106407DB429E30B30AEB20BD5AFF3DB9EB42FFD7ACD98C1555C8E96CB7DE20D236112B2E5DEBD782C3260C7EE0E5EC428854580F4D251A17EB815997445F34C165F991EDA616D03D7851D925233F786BCBCCF52328A6ED650384C5CBDA9555DBD259526276A9228298B1287E83D1BD66A90E4833B6B4EA7D09F96F5A41422A2594EFE9A97BDB467E6E453FE58CA9E3C859DE9836A5D20F7FC572C354E6E616AC318EEDA804EF59A1B1911050
	927E8FE805C01769A8F33982EDB221691A3406CFDF3C0D8EF859391FA62D404FC07270BB12F268EE7AD2835E3379379BF982E235AE6BE65A082E68EE23882F92160229B578E7BD7EF90F955A487EEE6EF7BFE1298FA27AE7E748D2BBA90AF6A95D357B6B6BD492BEE674E356544F1E263F6F2EE972E7D353C30626B93A26AC405024CD551D602B5C0EBC8B9BCF5A73D9365D9AEB5B7D0CE8E7B3FA9CC31067013EAD110F790C6A5CE0681CE252F9AD7FA6EB672766EF329287EF087ACD5674897C0CEC6E04485FBD
	3B585DF67613367422A7691EBD7E449EA97A372C8DDE596301A83F2D5267446FDA0FF6EA0A71ED0F77A4569F24BCBA137334132877B5D3197F87D0CB87881C0B0233E89BGG14D3GGD0CB818294G94G88G88G73F2D1AE1C0B0233E89BGG14D3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG229BGGGG
**end of data**/
}

/**
 * Return the JCheckBoxEnableLogin property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxEnableLogin() {
	if (ivjJCheckBoxEnableLogin == null) {
		try {
			ivjJCheckBoxEnableLogin = new javax.swing.JCheckBox();
			ivjJCheckBoxEnableLogin.setName("JCheckBoxEnableLogin");
			ivjJCheckBoxEnableLogin.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJCheckBoxEnableLogin.setText("Login Enabled");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnableLogin;
}


/**
 * Return the JLabelLastLogin property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLastLogin() {
	if (ivjJLabelLastLogin == null) {
		try {
			ivjJLabelLastLogin = new javax.swing.JLabel();
			ivjJLabelLastLogin.setName("JLabelLastLogin");
			ivjJLabelLastLogin.setText("-");
			ivjJLabelLastLogin.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabelLastLogin.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelLastLogin.setEnabled(true);
			ivjJLabelLastLogin.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLastLogin;
}

/**
 * Return the JLabelLoginCount property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLoginCount() {
	if (ivjJLabelLoginCount == null) {
		try {
			ivjJLabelLoginCount = new javax.swing.JLabel();
			ivjJLabelLoginCount.setName("JLabelLoginCount");
			ivjJLabelLoginCount.setText("-");
			ivjJLabelLoginCount.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabelLoginCount.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelLoginCount.setEnabled(true);
			ivjJLabelLoginCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLoginCount;
}

/**
 * Return the JLabelNormalStateAndThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNormalPassword() {
	if (ivjJLabelNormalPassword == null) {
		try {
			ivjJLabelNormalPassword = new javax.swing.JLabel();
			ivjJLabelNormalPassword.setName("JLabelNormalPassword");
			ivjJLabelNormalPassword.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNormalPassword.setText("Password:");
			ivjJLabelNormalPassword.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNormalPassword;
}

/**
 * Return the JLabelPhone1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRetypePassword() {
	if (ivjJLabelRetypePassword == null) {
		try {
			ivjJLabelRetypePassword = new javax.swing.JLabel();
			ivjJLabelRetypePassword.setName("JLabelRetypePassword");
			ivjJLabelRetypePassword.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelRetypePassword.setText("Retype Password:");
			ivjJLabelRetypePassword.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRetypePassword;
}

/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelUserName() {
	if (ivjJLabelUserName == null) {
		try {
			ivjJLabelUserName = new javax.swing.JLabel();
			ivjJLabelUserName.setName("JLabelUserName");
			ivjJLabelUserName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelUserName.setText("User Name:");
			ivjJLabelUserName.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelUserName;
}

/**
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLoginPanel() {
	if (ivjJPanelLoginPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Login Information");
			ivjJPanelLoginPanel = new javax.swing.JPanel();
			ivjJPanelLoginPanel.setName("JPanelLoginPanel");
			ivjJPanelLoginPanel.setBorder(ivjLocalBorder);
			ivjJPanelLoginPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelUserName = new java.awt.GridBagConstraints();
			constraintsJLabelUserName.gridx = 1; constraintsJLabelUserName.gridy = 1;
			constraintsJLabelUserName.ipadx = 49;
			constraintsJLabelUserName.ipady = -2;
			constraintsJLabelUserName.insets = new java.awt.Insets(4, 15, 2, 1);
			getJPanelLoginPanel().add(getJLabelUserName(), constraintsJLabelUserName);

			java.awt.GridBagConstraints constraintsJLabelNormalPassword = new java.awt.GridBagConstraints();
			constraintsJLabelNormalPassword.gridx = 1; constraintsJLabelNormalPassword.gridy = 2;
			constraintsJLabelNormalPassword.ipadx = 57;
			constraintsJLabelNormalPassword.ipady = -2;
			constraintsJLabelNormalPassword.insets = new java.awt.Insets(4, 15, 2, 2);
			getJPanelLoginPanel().add(getJLabelNormalPassword(), constraintsJLabelNormalPassword);

			java.awt.GridBagConstraints constraintsJTextFieldUserID = new java.awt.GridBagConstraints();
			constraintsJTextFieldUserID.gridx = 2; constraintsJTextFieldUserID.gridy = 1;
			constraintsJTextFieldUserID.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldUserID.weightx = 1.0;
			constraintsJTextFieldUserID.ipadx = 230;
			constraintsJTextFieldUserID.insets = new java.awt.Insets(2, 4, 1, 17);
			getJPanelLoginPanel().add(getJTextFieldUserID(), constraintsJTextFieldUserID);

			java.awt.GridBagConstraints constraintsJLabelRetypePassword = new java.awt.GridBagConstraints();
			constraintsJLabelRetypePassword.gridx = 1; constraintsJLabelRetypePassword.gridy = 3;
			constraintsJLabelRetypePassword.ipadx = 8;
			constraintsJLabelRetypePassword.ipady = -2;
			constraintsJLabelRetypePassword.insets = new java.awt.Insets(4, 15, 20, 3);
			getJPanelLoginPanel().add(getJLabelRetypePassword(), constraintsJLabelRetypePassword);

			java.awt.GridBagConstraints constraintsJPasswordFieldPassword = new java.awt.GridBagConstraints();
			constraintsJPasswordFieldPassword.gridx = 2; constraintsJPasswordFieldPassword.gridy = 2;
			constraintsJPasswordFieldPassword.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPasswordFieldPassword.weightx = 1.0;
			constraintsJPasswordFieldPassword.ipadx = 168;
			constraintsJPasswordFieldPassword.insets = new java.awt.Insets(2, 3, 1, 80);
			getJPanelLoginPanel().add(getJPasswordFieldPassword(), constraintsJPasswordFieldPassword);

			java.awt.GridBagConstraints constraintsJPasswordFieldRetypePassword = new java.awt.GridBagConstraints();
			constraintsJPasswordFieldRetypePassword.gridx = 2; constraintsJPasswordFieldRetypePassword.gridy = 3;
			constraintsJPasswordFieldRetypePassword.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPasswordFieldRetypePassword.weightx = 1.0;
			constraintsJPasswordFieldRetypePassword.ipadx = 168;
			constraintsJPasswordFieldRetypePassword.insets = new java.awt.Insets(2, 2, 19, 81);
			getJPanelLoginPanel().add(getJPasswordFieldRetypePassword(), constraintsJPasswordFieldRetypePassword);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoginPanel;
}


/**
 * Return the JPanelLoginPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLoginPanel1() {
	if (ivjJPanelLoginPanel1 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Dynamic Login Information");
			ivjJPanelLoginPanel1 = new javax.swing.JPanel();
			ivjJPanelLoginPanel1.setName("JPanelLoginPanel1");
			ivjJPanelLoginPanel1.setBorder(ivjLocalBorder1);
			ivjJPanelLoginPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelLoginCount = new java.awt.GridBagConstraints();
			constraintsJLabelLoginCount.gridx = 1; constraintsJLabelLoginCount.gridy = 2;
			constraintsJLabelLoginCount.ipadx = 6;
			constraintsJLabelLoginCount.insets = new java.awt.Insets(3, 187, 33, 202);
			getJPanelLoginPanel1().add(getJLabelLoginCount(), constraintsJLabelLoginCount);

			java.awt.GridBagConstraints constraintsJLabelLastLogin = new java.awt.GridBagConstraints();
			constraintsJLabelLastLogin.gridx = 1; constraintsJLabelLastLogin.gridy = 1;
			constraintsJLabelLastLogin.ipadx = 6;
			constraintsJLabelLastLogin.insets = new java.awt.Insets(18, 187, 3, 202);
			getJPanelLoginPanel1().add(getJLabelLastLogin(), constraintsJLabelLastLogin);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoginPanel1;
}

/**
 * Return the JPasswordFieldPassword property value.
 * @return javax.swing.JPasswordField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPasswordField getJPasswordFieldPassword() {
	if (ivjJPasswordFieldPassword == null) {
		try {
			ivjJPasswordFieldPassword = new javax.swing.JPasswordField();
			ivjJPasswordFieldPassword.setName("JPasswordFieldPassword");
			ivjJPasswordFieldPassword.setEnabled(true);
			// user code begin {1}
			ivjJPasswordFieldPassword.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_PASSWORD_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPasswordFieldPassword;
}

/**
 * Return the JPasswordFieldRetypePassword property value.
 * @return javax.swing.JPasswordField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPasswordField getJPasswordFieldRetypePassword() {
	if (ivjJPasswordFieldRetypePassword == null) {
		try {
			ivjJPasswordFieldRetypePassword = new javax.swing.JPasswordField();
			ivjJPasswordFieldRetypePassword.setName("JPasswordFieldRetypePassword");
			ivjJPasswordFieldRetypePassword.setEnabled(true);
			// user code begin {1}
			ivjJPasswordFieldRetypePassword.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_PASSWORD_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPasswordFieldRetypePassword;
}

/**
 * Return the JTextFieldFirstName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldUserID() {
	if (ivjJTextFieldUserID == null) {
		try {
			ivjJTextFieldUserID = new javax.swing.JTextField();
			ivjJTextFieldUserID.setName("JTextFieldUserID");
			ivjJTextFieldUserID.setEnabled(true);
			// user code begin {1}
			ivjJTextFieldUserID.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_LOGIN_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldUserID;
}

/**
 * adds any default group roles for a new user here
 *  these group DB objects only needs their GroupIDs set for now
 */		
/*private void addDefaultGroups( YukonUser user_ )
{
	if( user_ == null )
		return;

	YukonGroup group = new YukonGroup( new Integer(YukonGroupRoleDefs.GRP_YUKON) );
	user_.getYukonGroups().add( group );
}
*/

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	YukonUser login = null;
	
	if( o == null )
	{
		login = new YukonUser();
		/* addDefaultGroups( login ); */
	}
	else
		login = (YukonUser)o;

	if( getJTextFieldUserID().getText() != null && getJTextFieldUserID().getText().length() > 0 )
		login.getYukonUser().setUsername( getJTextFieldUserID().getText() );

	if( getJPasswordFieldPassword().getPassword() != null && getJPasswordFieldPassword().getPassword().length > 0 )
		login.getYukonUser().setPassword( new String(getJPasswordFieldPassword().getPassword()) );

	if( getJCheckBoxEnableLogin().isSelected() )
		login.getYukonUser().setStatus( UserUtils.STATUS_ENABLED );
	else
		login.getYukonUser().setStatus( UserUtils.STATUS_DISABLED );

	return login;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	// user code end
	getJTextFieldUserID().addCaretListener(this);
	getJPasswordFieldPassword().addCaretListener(this);
	getJPasswordFieldRetypePassword().addCaretListener(this);
	getJCheckBoxEnableLogin().addActionListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerContactLoginPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJPanelLoginPanel = new java.awt.GridBagConstraints();
		constraintsJPanelLoginPanel.gridx = 1; constraintsJPanelLoginPanel.gridy = 2;
		constraintsJPanelLoginPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLoginPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelLoginPanel.weightx = 1.0;
		constraintsJPanelLoginPanel.weighty = 1.0;
		constraintsJPanelLoginPanel.ipadx = -5;
		constraintsJPanelLoginPanel.ipady = -7;
		constraintsJPanelLoginPanel.insets = new java.awt.Insets(3, 8, 6, 8);
		add(getJPanelLoginPanel(), constraintsJPanelLoginPanel);

		java.awt.GridBagConstraints constraintsJCheckBoxEnableLogin = new java.awt.GridBagConstraints();
		constraintsJCheckBoxEnableLogin.gridx = 1; constraintsJCheckBoxEnableLogin.gridy = 1;
		constraintsJCheckBoxEnableLogin.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxEnableLogin.ipadx = 32;
		constraintsJCheckBoxEnableLogin.ipady = -2;
		constraintsJCheckBoxEnableLogin.insets = new java.awt.Insets(10, 8, 2, 251);
		add(getJCheckBoxEnableLogin(), constraintsJCheckBoxEnableLogin);

		java.awt.GridBagConstraints constraintsJPanelLoginPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanelLoginPanel1.gridx = 1; constraintsJPanelLoginPanel1.gridy = 3;
		constraintsJPanelLoginPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLoginPanel1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelLoginPanel1.weightx = 1.0;
		constraintsJPanelLoginPanel1.weighty = 1.0;
		constraintsJPanelLoginPanel1.ipadx = -10;
		constraintsJPanelLoginPanel1.ipady = -11;
		constraintsJPanelLoginPanel1.insets = new java.awt.Insets(7, 8, 88, 8);
		add(getJPanelLoginPanel1(), constraintsJPanelLoginPanel1);
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
	if( getJCheckBoxEnableLogin().isSelected() )
	{

		if( (getJTextFieldUserID().getText() == null || getJTextFieldUserID().getText().length() <= 0)
			 || (getJPasswordFieldPassword().getPassword() == null || getJPasswordFieldPassword().getPassword().length <= 0)
			 || (getJPasswordFieldRetypePassword().getPassword() == null || getJPasswordFieldRetypePassword().getPassword().length <= 0) )
		{
			setErrorString("The Userid text field, Password text field and Retype Password text field must be filled in");
			return false;
		}

		if( getJPasswordFieldPassword().getPassword().length == getJPasswordFieldRetypePassword().getPassword().length )
		{
			for( int i = 0; i < getJPasswordFieldPassword().getPassword().length; i++)
			{
				if( getJPasswordFieldPassword().getPassword()[i] == getJPasswordFieldRetypePassword().getPassword()[i] )
					continue;
				else
				{
					setErrorString("The Retyped Password text field must be the same as the Password text field");
					return false;
				}
				
			}
		}
		else
		{
			setErrorString("The Retyped Password text field must be the same as the Password text field");
			return false;
		}

	}
	
	return true;
}


/**
 * Comment
 */
public void jCheckBoxEnableLogin_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJTextFieldUserID().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJPasswordFieldPassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJPasswordFieldRetypePassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJLabelNormalPassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJLabelRetypePassword().setEnabled( getJCheckBoxEnableLogin().isSelected() );
	getJLabelUserName().setEnabled( getJCheckBoxEnableLogin().isSelected() );

	
	fireInputUpdate();
	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		UserLoginBasePanel aUserLoginBasePanel;
		aUserLoginBasePanel = new UserLoginBasePanel();
		frame.setContentPane(aUserLoginBasePanel);
		frame.setSize(aUserLoginBasePanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;

	YukonUser login = (YukonUser)o;
	
	if( !login.getYukonUser().getStatus().equalsIgnoreCase(UserUtils.STATUS_DISABLED) )
		getJCheckBoxEnableLogin().doClick();

	getJTextFieldUserID().setText( login.getYukonUser().getUsername() );
	getJPasswordFieldPassword().setText( login.getYukonUser().getPassword() );
	getJPasswordFieldRetypePassword().setText( login.getYukonUser().getPassword() );
	
	if(((YukonUser)o).getUserID().intValue() == UserUtils.USER_ADMIN_ID)
	{
		getJTextFieldUserID().setEnabled(false);
		getJCheckBoxEnableLogin().setEnabled(false);
		getJCheckBoxEnableLogin().setSelected(true);
		if(ClientSession.getInstance().getUser().getUserID() == UserUtils.USER_ADMIN_ID)
		{
			getJPasswordFieldPassword().setEnabled(true);
			getJPasswordFieldRetypePassword().setEnabled(true);
		}
		else
		{
			getJPasswordFieldPassword().setEnabled(false);
			getJPasswordFieldRetypePassword().setEnabled(false);
		}
	}
	//set some dynamic data, for the sake of curiosity!+
	getJLabelLastLogin().setText(
		"Last Login:   " +
		new ModifiedDate(login.getYukonUser().getLastLogin().getTime()).toString() );
		
	getJLabelLoginCount().setText( 
		"Login Count:  " +
		login.getYukonUser().getLoginCount().toString() );

}
}