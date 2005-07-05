package com.cannontech.dbeditor.wizard.customer;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.customer.IAddress;
import com.cannontech.database.db.customer.Address;

public class AddressPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JComboBox ivjJComboBoxState = null;
	private javax.swing.JLabel ivjJLabelCity = null;
	private javax.swing.JLabel ivjJLabelState = null;
	private javax.swing.JLabel ivjJLabelZip = null;
	private javax.swing.JTextField ivjJTextFieldCity = null;
	private javax.swing.JTextField ivjJTextFieldSecLocation = null;
	private javax.swing.JTextField ivjJTextFieldZip = null;
	private javax.swing.JTextField ivjJTextFieldPrimeLocation = null;
	private javax.swing.JLabel ivjJLabelAddress = null;
	private javax.swing.JLabel ivjJLabelCounty = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JTextField ivjJTextFieldCounty = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public AddressPanel() {
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
	if (e.getSource() == getJComboBoxState()) 
		connEtoC6(e);
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
	if (e.getSource() == getJTextFieldPrimeLocation()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldSecLocation()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldCity()) 
		connEtoC3(e);
	if (e.getSource() == getJTextFieldZip()) 
		connEtoC4(e);
	if (e.getSource() == getJTextFieldCounty()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}

/**
 * connEtoC1:  (JTextFieldCompanyName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldPhoneNumber.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextField2.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (JTextFieldCounty.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerAddressPanel.fireInputUpdate()V)
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
 * connEtoC6:  (JComboBoxState.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerAddressPanel.jComboBoxState_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxState_ActionPerformed(arg1);
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
	D0CB838494G88G88GAA07C4AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDC8DD4DCD7350ECF2AD133163146E5D22BAF2678CCDF31AFAADD25ED1EAF2FC6D7EA73AAEA7A1A2754050A16770CFD69921FF99A1B4B70B310BF7E83E9A190FE929248G01G997E860670971250C0A704E24A40DCE01AE1E61C390461C77376BE671EB3F706BBD0D27316D9EBF36F794EB97B6C3377BEFF7B1E0910193FB1A1D613A9883189027C2FAED69056DC9604EF45556DE6585E9413D6D07CBB
	8914A668CC7118C8B5E90307CC5AD4A15E108449CCC8C7751B3459701EA1A49B369B91D4EF67E14813D2927A16EE6771B7D9BBDA120E3CF3981EC701F2GEB84DA1D0A6D6F3B13A363A310BEAA3FA345EA8421AF8D4B3C58122340F74A650AA12F9EA80614491849157357C39A4BEC016776B4DAB6821E49C43A877475F23961CFED96E23ED32EB902EDA978B3EACC27F5C764E70190CA9491A7BC56530D79FD37A71A479CD33AB1335D6E30CB6258244EB2AADA2C1243251B314E19DD961DC53CE39D93F5E353EE
	49B1A53A74FA237C56EA3138C4373B4FEC97ED40ABBC9D7B707B16A4429F7AG69618C445C8654DBA63C178335E730FEFC6FC823AC673A5B1B0548C7728376E3E38615FFFB06379F15BBA26225CC645FB8F3D82E978F69C6A0AACB508C13A5AC134972730BD8F6AF241332CC5A02AC060768912F07F49F50E496133169B413F1F07EC9E17DBFBF2DCD89A0632AACAA5B66AC2F0C41CFC7449FA73E73EE9E5ABF8A72120E193439C086209120D5C0EF921F7D4D1BE76159BAAA999DD3D38EFB2B5359E633382EB8AC
	228D5E75FA4811316B621843E591846A170221DCC59EC66B0F797AA4926D5B8D0179D1487547055536E73489AAFD8BFDD1278911F9ECBB666DDBFCB66A6D1744361100E3BD349F45FFC7702EECAFCEEDB5CBF0CC5BA0EFF7B6537360D1A6CBEF7CA6A1356AAD0DD6C516754FF6E85AF3288C43B9DED9280EAF66230ED7839E8C3495E897D08CD066F12663165C6A65F45C8D6340E5B75B7A27C71DE61754B6ED35D9C4172C6B361925F42DC634DE5F0A6BD13DEC2ED65AA8626BFD2AFA0958FCC1D3F91C6A236B38
	D7AF346D0F85ECFB7278E25FB84AEDF8130C0905635406F19C9FA3789A85CEED7BCBB236B07DB06489EF183434FCF1836216B77CFD241A602B5F7077112F69190F04C0DE4E1BA6AD356FCB85B8BF86C1FAA3D098D0B850DE204339B016F3190DBF1EDB71BE47D11F423E346EE45EB1F80A9E49E56E363BA533FDCCB419DDD67328CDF48BF219BF6521DEC7FD6A5DA163FB8C5A68946D224BACD99DF660EE1E9241195C344C5D3CB4AC6392C96E00F5CA0486261C82637B513C8C9C97E637743653E216C4EF891A7F
	49BC7409BBE693F08486706E4CC5FDE513754FG6F4339CCD771050805C2FAA7574B419369703C8C4EAD5A747AF66B980AE1F64DB29FFD4D00FD08047216BC13B69A48F242240D849A4EE7637455C1660F6DC73EA2143FF8CD559F098F95D05F4AAD7077C721955BE9875C829484948C349DA82A002D35F78DA3B29F14070C77FC2F2FB279D0B60E1DD07765654766C33C2DB879F9C4D1A6C1AEF748EFEC2E34ADEA97D7917AF67CC970D7A043C9E61BCBA78A284F36195DA27352EBAEF0B051623594AD5B160FEB
	10F8C734CB6D22D334DB6045AD7BC8F7FE3257FF479D5C372E919C67B3A3B8181B63EF6727D19CF8A8C7834B7FA349B7CB124BBABAAD097D22A4D96D935E7AA211456DB3D53A653ECA455857C1682393E8956F6B2B79998ABF6CF5B8EECFBB87F004497E3C112C5B6B20FC48C9668F6F6AF1AF964E53D61276663FF6F1D056B316D929ED5E1F1F9C5433B6627E4A7C3DED1CED40314166D566FB49327F51B297C9193D504F1CC270A120C92035C0E105CC9FE17A11A5787CF5E57E81E9CB2D1DA3FF4536286F7C24
	A0306FC797287B7E2B856A3E9FD33034EF6FAFD073ED1A97F7927B6A1831AB46CA52C91CDF479CF63BC886AC2FA250BD772B83588F2B5DAA5D30DAA6C4295FBAA78AC2D2917A3C0E4C6111E03F18C2B6574E94313936F86ED1E15B3BE7B5EA7B3375DF6A567496497B7CA26FDCCBE5CAAE44B1A85A44A9D01451B1ED17A81EC2568799F74791AC36F7866B16768C008EBB2C224D526702A52857B1C6962C72B118A5A49D79CB972852774CF74C9E1DFB865426A3B65199B177923E1EA9B8F72E39C06D30D0BEEEF5
	095DF667B4E3F346F426D220B2645D1D761321DF9C7BFF1120CA76618C61F2095DCF8623D51A3D2F0D1FE7EDFF694C14DF5BC3D667FDED3A1A773BEC5A695FEFF4187B5B739A5A76D7679E26ED431EED5451667074CBD0205CCC86120EBE7AC4573843B5A5DAC873BA730CA4375DCA3217EB7CBD9F566F367A716C8D4C3481643CC05734AF1760F8DA8563EC9DD0C891DB53BED10879ECCD1397EE2343E623D38164259663B82FED4DD40C5D6DC5B40EF014670F2A668FE29E497F19EA3E0D67DB5A5472D7717C1F
	2B662F637963C6357C4D452C1F1E22D41C9345BFCD0B30B546C57B32B84B7637C569FEF9A633ED5A3BEF7EE8912E89B645FACF7139E275B9EAD5B12E7137B97F2062C481773F14A71DD3371811279366A9589E6186C60E37BCB9230C37EC8579B1B6339F7BD8E252D21F336A312FEA3E323584F9B89AD8BC269452862011924A676192EAB3DA6EFCA3AB17F48A76F52782157B85AFE700B203814BBD54444AF9204C5A00652EB633F2DB204C8E2068D3EA6552DAD8396253E0CF206153F2395334DC74E9ACF74940
	62A5959C9BA6D895AC6A7F9D474E24D7D21571275D3F1ED3DD17104ACBA9FFC3297FD9BD6294729D815CD94AFC6B75E2653E30556DF60CD9493CAF7B49D7CA14EB96453ED6F2901EB02F38615CEE91BD4C5742CAD0D6BCCB49306CAF0F15B5890B4F0617C8440BED3B0DB3E4548FB80CF01C6B82F7B572A55DE495F10D24FA78F307B2FE35B525CEF3A8009E02647E87AB745078F4C4BC0DD3EDE8E5FA0FAAE5312BB75A9816FB06E11F6358G47F20D8CDB43314FF3ECBBE0345F9B74B84F2B457B4608A2F49353
	D65D34E4450321D9B213C91AC676A84F139E6409FB3B7DE7D84CECF09E47ED4DD91336974882C44F6F6B49399E5BDDGAC74AC1BDB3EFBD3B9376C81BC334C242DAFE36371C0837A5D8A979B592E5B4F24D3BF70DBDF182DB6E59864F6F7BE3EC9E83C1B23D10B632DCEA9547416D19BC9E5DEDBC93AD9CD78704BF23C711DC6563FEDD0E6DF996B1FA7DD2DFF1565CCAFA9CD2CDEBBE08B652C5EE7977C6B05C25EEEDE6F1B3C5E9E400A4F71397C0FEA75064F31FAB34D2C1E93304D3CDE4CF535FA1167189DFE
	5CFCAFF620ED6ADBD81BB1402FDC475A6C19D736E95151B65768D81BDFEC01AD103072651E0DEB69ED9C579FE80967637A46F9657C36AD32B6607C36D1C7FDA4D467BF3FEDAB4571928EF8140E4DEF55275294735BF518D14C7689D885795966946A8256BBB97D73D358AED96F8496BF7843995463BE6019D9817A85EA8732D430B6EC27170DEFDE72AC915F7CC26BCA6214C1956C3C195F3612FA6BF93D0DAB2A97426B1DB42E245EB6DEEF530A6A6D6275CEF930DEF405B73630DF711EDA39B866C0E31CBB499C
	D9DCC9E31C899CFF0960758A1C0E1FDB73EC7C74C25E16CA567E979A7C654E2C72ED3352AFCD6B05AC2A2773CB8F78552363292494778CE43DD08CFB3DDEF1B6A47B2CC7D2157BACDB950D592C05E7F095DB0F920758FA34350A45A7EF17A17F98C8271EB7E9CB015A01A6010200B61FE77E7C4CE935755A571F83AD565ED896550539715EF468D8D44FDF07BB4E7B56237D7E27F4566F48734C0ED9CD77A6C35122FA7EB264D47B562376C9D9D033CFA514ED2FE676F9E10149B9DC4D64FC6E9E65FCE9D9B957D5
	2B69AA779613A1044B706766FB1361930B6A794B9025AAC3D9BF13E1BF174150F2EFB2DCDDD46F04DF3A26C6ED5CCD58506FD5469DDB59E4D9BFB44FE46D2DE1E36A31F24402A03D2E060D1F1CD20C4131B5274FE135CB82DBEF3ED50A67A9074BBAE135B36CBB849BF338C8E00BE289E783475C8E1FD50F39251FD50F39651CDDBA5EDCF0B6D0CC0E46061FA0FB65E0685FB6A00A5536B1EC97471E64D8B4476A0D8C5B4F3127B816FA01E1CDE42FDB8C69C60E85932C8B52439CAB19C3EC952457DFE07E72FDE2
	172D108E67584B84437D8E6E87A9E619E731E4B5427A5CB7AFACDEDB16A3F97CAC5906B2061D30C27EC8F90447F347D208BD5DEE51CDA6E2BCBD48F92FC8D902CABC742AF9CA94D84C694506E4394CA549E12C62636057E7F2544781B91450B1702354A439FC5485F6A639CA3E9D66561A34B5C03D35CC5F070616DA77AD350BF5CC7DF13AAC306FAF14297B7E5F156118F3BB26DDE3223FFFFF2CAC0767F9D72AEEC91CC23550B28F127A1259B5A1AE9A9F9BC8FD1A97287E2632C0E3086AFCFD23D26767393F04
	26AF25A30F0A0EA83F5EA6A5BF5CFFD27BF4AEB07B2C29E57689A37B1B6D100EG0A6176794142D2ED9F2E8B5436502CEC3B067765642D2578958464976A436F8267D7593F943F1A007CE6DA147CEAB93F0F4CAF452FCB059F356D16F25CAFFAF8BA1424E7793891DB0FF27F38E4374893BE0D656CA4F36705F8C158A0B06C8A19A753G7BDF0E3DC030A640DE09E558B5B2CF7E9EBAD54131F0023D8D65BE9C4B7ABB6041980864F058862CCE5CAB8A43F5F8374A02EEA117F94603EB0C4DBC6B188659E26A1057
	77BD4AFD4AC640F6GC548FA20FD7B6A8C729EF71927401D7B1D621890DECF6E7F4C313A0D106E83F2822D855AD24F4E171EF654BB1DBD2AE4B9B6DE76E267E53BC4267A5BA5672257966597836D731FA5730E61A25D4FEBB97E82418795B85D678F909C53CE485BF5112DA905C3ECCD2957B36C79F4062D563371523BE83C6C56FBE55B238FA4E7578236ABB4D0F952B8FE1360658A1C4A798DB2DEB1BD88F9D18DCC267FDAE0B2B99B797AFA0BE15BB856594F3054A606FDFE1EE1A3CD6CDB57F75FD11E39D39F
	BA25C1B9544E5CDB1A6458E6137F1D362A911FB38B9E74757ACE175552E61E68A55EC66725FDE4ADC75EFB1B583C94B40B7DACEDB6E98DC0034DCCAE4FA996638B7907CDC26F2ABE55BBEA1183F59AE7B315E7DD33D72EB09E639B1DE3ED06C3FED28BE2EB6714B191744927B4F42FCA731F7549C79B603A6A4D0F7149C7F9DB63147927FCE2AE282353F2FD5A3767CBD85F76BF3FC530BD519C707EDDDF0B9C93131FBB260B04BD9FFDB91E5A733729ECEF945242EC1C45FDB63C05796C8E6273858658D7810D98
	983E0B60C1869AE34778B945077877F6AC0F4F9103572F8B8CAB5F97A909366166EDA4A8F025CDBEC89B3B8D280B0BC36DCB14E15F4D63E48A06BAE1C0C5B23E935E8FE828DDA83F1F26AF47CF2B60D7506A4B2F26554B0F5AEAEC01594F504A6C27CFE4763334B2FBDCDDA800E7A8244301220104B613B6872886288F4856466FEB1032780DA38830E0206DED74DB86FEFF40EF95D13C6C7679D5AA7DC07E4817258545FBCE1B3A6D19EC01F4E313658C6C8F2F284A02F94A3B8C4A7BBF016ED3B03B86128547F3
	4A12FE937B67256C2C7499F2B74F68EB679CC87FA8D669B757EEAD47AFD9412F580FDFBD2433FC785D6ADF0E1FD246F6BFFE030B78054EAF47CF695796BFFE9EC8BFF560408164C74F432F60F6D05EA7ED2EAE0127497C1E6A971F9ED8AE2E0D3E871B698173847705F7E6D7FAE7132E257B48DA92E424EBE9AC47E36790DF2B40699A7B71D9F6FF79E148935A59F9DEDB0B7B9A8B24D7812D875A82948E34271D1D6F77D6D78A4B4447869C4EDEB840589447ED5A3F0939FB3B137A2FDA36EE04BE7D0FC258C7DE
	0F8D745DCB38A42F0572F3C4AAA2B86D5F576A30FFFD10E783DA8B3499E8AB502ECB2CFF75B56534FFE830D6C9B20FCD122FFF2CF378C14E8B6326106EA78B527C77135140F33FAC87753772E567D1A5E5F61834259D545FB05D586135A35DCFF6A6629C5153EB9E8565532F49BC4F940F3FCB88F033C6AE935F1365D3C6F173C5E07783348D4AB53D2B034694EC704CA7BCE06D97583F3DAD0BB1C15E0FBFFAD160767C49E1E64F5C479E915ADFFABD36A800BDD7F5D07D05F7F877B8193FF9B9DE2DED5ACEFD83
	2628CDBE996FEF1324CE13B637D3E9133792D732D656C05D2ECE2FCD06BB7DED1255FC54C727F813076535569F6476D2627A4AC39CA79F12E47CF6C7B247072CCE5E466C5DA3BEEDA8731A2AC97BBE5F25D85E1AEB293E32115BB6B4AF6278E12FEC84176FF7E47B5B9F63CA131D6CBBDBE427DA7E824FEF3F25163F16670F26296587F72A7B973571A43A0F90B87F87CDCB677FCF735279478D1822F3566B335EF76D1C77FD6D6CD2312E8FCA340DC76FEB9BF43ECB2D8D9CEF4A2ED50FB765552A47DA0BEA173E
	5FD8D69B78FE6379DA657DC6BADF7F2781FDA38CFCE19750BE2043DD30DF6FE2F3756E5AC2014C55AA7374A54B84463B70C63D445AFF26AEF0DF1F2DDB7E9E7E73F5B8FEC79D126418C27E8C7FDE9DB9E7BB1C8424583F57210EDC5609C90996157108BA74B71BB8CEE0FAD66BC8E3E73546AEF6BE1452F0BCCA10DE83945C75FE67DB66431FEA43149C5B9C8F905BA4231D4A505C406EDBEC6DE27B013BC61C9BE3A01D5A8DFBE320DE2091A08F509A200DC021C0BB01220076819D6E819B81D58235838D7670FB
	58974F888176C4G4A87E7E2B41719FEB720325DCEE07A3175B07DECC8C07D84C3FAAB502E1E156A671DCB82677F39EC5677BDBD6C9BD5F697430AAFB3EC7E964306B936A10DE16BAE33FE3EE6886CE77B8C4B7BD9AC091B09609F8333CEB9461A58CA3E1D0C0F3BC56E4F07DAD3B9566D6EF3382D38F4533963F25A3D6DA523095F6D3C6C3F17FC0D603BAF7B6FA5675258DEB28A723AFA59994B68739DB824D77EBD177C5C559BA85E78E9E3E05DBDE4DCDEF70F98554768234645E374F1237A988DB5AA47A845
	1EB262D81AG9F9D64653E41311B9C7BA66109588D2BC51A6478F3C4AE443BC4EC10615FA5F24D90ECD046F6A9301BB2769FC634BFF4F952413F137D58087795472DB69BCD7F1414315A419D38BEDEA1B2DA1DE6055CFBB8F6D3E0EB0DB3615E7C65C8A25ABF3A575FDF0E91BC26575FDF7EBE01790BF0854E7AD7783D0CECE57BBB2E483FC1151FFB2FAC7697DAAF37CBD9AF7EAAAD17A3BF4B2F862A374AA7DEEF104B8D484F4900753E60B32E9664F2EB65E7F040FA2F6252A83F7FD6713EAA6D7E6FA3B65C57
	B6E87CE6082C9FE1507F9DD7D9CC27C003D8842477F06CC39A966759F71559BEA3512B0FDC457B17926E3FEE3EF1DF5B103F8DE7B35DF4DDE3FAF879A0534DB0475A8EB15DCCF22C1A2CC3E1105E4131BA72DB0A88C86F61584305DC2F9C7B6CADD6372B0F4747F3D95DE10EED4E656DF26C89322E05C1FA87470E5DA4F5A13D07E373B7F93BFD6C5B478B59E4DEF258252037304C0709EFF4417B40DB2CCC4CC1ACA341E4CE5763F59DE8F7FAA6DB2FD949B9B9CC913F44F9E58734518A4FBF9CB8F000369FC574
	3F8B5EA301E8FBCFF6FA5B3BF968033417FA1D6E1730BD5A371EAE5F7EFF3B9B5B4F05F22557D97F2F967A767F07DDDEF93EDE3812F82B3FBC0350C61ACF7F3791BF30G6E3C4E7A3F3A475B5E3F64AE57DE72926DEDDC547F1DE93E7D7FA9712590A897467BFF6E26EF7F9F3B6C1567411BABB9137B4B938DED244872D05E8E727FAE50F6B3B5F8E6300BB3857CEEE68788DADAE8311AED0E094272024A72022A1AB3ACD631F1C6791D287EE0E3403B19B157695C1F54CF1FB5DFAE12E39F472FA3A77A7E2049AB
	4B778E3EDFD9E85DA7EEF879248DF97914952E0C4F8373DEBE7DFF7172E948DD991FBBF73DFCEEE83CFC8E5EDC991F87311AFC66EB09A73FAD869D8E77337DF4E8DF1990785C5067F2B8C517B4CBD1728B90F91F539E0FF66067D11AA567DD0E4733A57EB68EE4324E1120D7BF9434CF507F4F43509F8AABD1482F0484582A39DDC28A6FCAB759DFC55E2956477D6C11B80D905FADA4527DE637900F1B46EEA1016CA93B05B858EFFAB0B6231147AC796D327BCF42DF08879DD6209A0E92C5423F9174F30EA64B68
	B7D14B9C0DD3200BB97C96A81E23683709B227439CFDC0C6F1F71AA889C9F29CD1AF8989982202E7A209A141CB7C1055898FAD8D8F69C1A9C7FC6C7B2DAFDE78454E9F1B5CF1C2E237F09C07FF3F68A25A95ADF8CBC77E99F907106C44DB8278EBC450181BBC925C522CCD6C95B2472F634FF95C12E8E9E5F6F3CBC29639C9DD013F972868B23BA7A55C73B7748A71F67C413B10CE33EDE67BC485B5E283D1FBEC6B88D1843A819AB103480BDCF41CCB46B3A9A5A7FB22426284CD07F05066B0DBBA4CE360C85DC2
	32B4892A18F458AC9EE70B01B0CAA23F94BEC4EC60925E645D474E6AB02E46053E6D797C1AA351C7BA40969E17D0C1CA62EC2763A5AC46C9338B1A92DD5D927E005F61AA66F99FBAA11D7A374E6CE10D90ABF7926D1566F1CAD4E15CA1314B3A8E10DA7A612F1E5B7423463DFB20CA0710C0AEF1C343295ED2D72727C6C557C7C2BE13F7E8DD64A7D0AEA72A07F8C29618F95CBCE5354D42CBBB99258CCF0653165D0D975A2151823F9E33ACAE386E534FD5FE66515F8D91413DD5A5A11F1A882AD6CC080E290A56
	71F12BDD648C40DE40BE0F3347B2BA3FB2691ECFE5753CF8E0947C2BDD081B2A2CA4FF2B485F7378370AA0D58429BA8FC65BABAC794FBF5F6341BE1F707041A1504191DB4D07E24AA22FC2B4E331FBA01CCDB20FE3F1FC49653A45D4BE49BCC97E965F1B24E7BDBE93818EE3DC7835FFFB12D6F996CDC3077FB6A52D77CAFAD01694038EFF0356EF71CA0AD2EA65CC8DDDA94E5CD56EDB76BD1097F05F92BDC477AB7B077CFFD3D2F283BBD47F8E6C5B5FE1EB7134691C90783BE67F1459E6134C365B2DF68BFE63
	EC1B15C4EF7C776C8D646FE6E564CC1AD7F58365FECFD1677FGD0CB8788A8351E857098GG34CBGGD0CB818294G94G88G88GAA07C4AEA8351E857098GG34CBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGAA99GGGG
**end of data**/
}

/**
 * Return the JComboBoxPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxState() {
	if (ivjJComboBoxState == null) {
		try {
			ivjJComboBoxState = new javax.swing.JComboBox();
			ivjJComboBoxState.setName("JComboBoxState");
			// user code begin {1}

			for( int i = 0; i < com.cannontech.common.util.CtiUtilities.getStateAbbreviations().length; i++ )
				ivjJComboBoxState.addItem( com.cannontech.common.util.CtiUtilities.getStateAbbreviations()[i] );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxState;
}


/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAddress() {
	if (ivjJLabelAddress == null) {
		try {
			ivjJLabelAddress = new javax.swing.JLabel();
			ivjJLabelAddress.setName("JLabelAddress");
			ivjJLabelAddress.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAddress.setText("Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAddress;
}


/**
 * Return the JLabelNormalStateAndThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCity() {
	if (ivjJLabelCity == null) {
		try {
			ivjJLabelCity = new javax.swing.JLabel();
			ivjJLabelCity.setName("JLabelCity");
			ivjJLabelCity.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCity.setText("City:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCity;
}


/**
 * Return the JLabelCounty property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCounty() {
	if (ivjJLabelCounty == null) {
		try {
			ivjJLabelCounty = new javax.swing.JLabel();
			ivjJLabelCounty.setName("JLabelCounty");
			ivjJLabelCounty.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCounty.setText("County:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCounty;
}


/**
 * Return the JLabelPrimeContact property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelState() {
	if (ivjJLabelState == null) {
		try {
			ivjJLabelState = new javax.swing.JLabel();
			ivjJLabelState.setName("JLabelState");
			ivjJLabelState.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelState.setText("State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelState;
}


/**
 * Return the JLabelPDA property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelZip() {
	if (ivjJLabelZip == null) {
		try {
			ivjJLabelZip = new javax.swing.JLabel();
			ivjJLabelZip.setName("JLabelZip");
			ivjJLabelZip.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelZip.setText("Zip:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelZip;
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

			java.awt.GridBagConstraints constraintsJTextFieldCity = new java.awt.GridBagConstraints();
			constraintsJTextFieldCity.gridx = 2; constraintsJTextFieldCity.gridy = 1;
			constraintsJTextFieldCity.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldCity.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldCity.weightx = 1.0;
			constraintsJTextFieldCity.ipadx = 237;
			constraintsJTextFieldCity.insets = new java.awt.Insets(5, 0, 2, 96);
			getJPanel1().add(getJTextFieldCity(), constraintsJTextFieldCity);

			java.awt.GridBagConstraints constraintsJLabelCity = new java.awt.GridBagConstraints();
			constraintsJLabelCity.gridx = 1; constraintsJLabelCity.gridy = 1;
			constraintsJLabelCity.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCity.ipadx = 12;
			constraintsJLabelCity.ipady = -2;
			constraintsJLabelCity.insets = new java.awt.Insets(7, 6, 3, 17);
			getJPanel1().add(getJLabelCity(), constraintsJLabelCity);

			java.awt.GridBagConstraints constraintsJLabelState = new java.awt.GridBagConstraints();
			constraintsJLabelState.gridx = 1; constraintsJLabelState.gridy = 3;
			constraintsJLabelState.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelState.ipadx = 8;
			constraintsJLabelState.ipady = -2;
			constraintsJLabelState.insets = new java.awt.Insets(7, 6, 5, 12);
			getJPanel1().add(getJLabelState(), constraintsJLabelState);

			java.awt.GridBagConstraints constraintsJLabelZip = new java.awt.GridBagConstraints();
			constraintsJLabelZip.gridx = 1; constraintsJLabelZip.gridy = 4;
			constraintsJLabelZip.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelZip.ipadx = 12;
			constraintsJLabelZip.ipady = -2;
			constraintsJLabelZip.insets = new java.awt.Insets(5, 6, 10, 22);
			getJPanel1().add(getJLabelZip(), constraintsJLabelZip);

			java.awt.GridBagConstraints constraintsJTextFieldZip = new java.awt.GridBagConstraints();
			constraintsJTextFieldZip.gridx = 2; constraintsJTextFieldZip.gridy = 4;
			constraintsJTextFieldZip.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldZip.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldZip.weightx = 1.0;
			constraintsJTextFieldZip.ipadx = 138;
			constraintsJTextFieldZip.insets = new java.awt.Insets(3, 0, 9, 195);
			getJPanel1().add(getJTextFieldZip(), constraintsJTextFieldZip);

			java.awt.GridBagConstraints constraintsJComboBoxState = new java.awt.GridBagConstraints();
			constraintsJComboBoxState.gridx = 2; constraintsJComboBoxState.gridy = 3;
			constraintsJComboBoxState.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxState.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxState.weightx = 1.0;
			constraintsJComboBoxState.ipadx = -36;
			constraintsJComboBoxState.insets = new java.awt.Insets(4, 0, 2, 247);
			getJPanel1().add(getJComboBoxState(), constraintsJComboBoxState);

			java.awt.GridBagConstraints constraintsJTextFieldCounty = new java.awt.GridBagConstraints();
			constraintsJTextFieldCounty.gridx = 2; constraintsJTextFieldCounty.gridy = 2;
			constraintsJTextFieldCounty.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldCounty.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldCounty.weightx = 1.0;
			constraintsJTextFieldCounty.ipadx = 138;
			constraintsJTextFieldCounty.insets = new java.awt.Insets(2, 0, 3, 195);
			getJPanel1().add(getJTextFieldCounty(), constraintsJTextFieldCounty);

			java.awt.GridBagConstraints constraintsJLabelCounty = new java.awt.GridBagConstraints();
			constraintsJLabelCounty.gridx = 1; constraintsJLabelCounty.gridy = 2;
			constraintsJLabelCounty.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCounty.ipadx = 7;
			constraintsJLabelCounty.ipady = -2;
			constraintsJLabelCounty.insets = new java.awt.Insets(2, 7, 6, 0);
			getJPanel1().add(getJLabelCounty(), constraintsJLabelCounty);
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelAddress = new java.awt.GridBagConstraints();
			constraintsJLabelAddress.gridx = 1; constraintsJLabelAddress.gridy = 1;
			constraintsJLabelAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAddress.ipadx = 32;
			constraintsJLabelAddress.ipady = -2;
			constraintsJLabelAddress.insets = new java.awt.Insets(5, 6, 0, 307);
			getJPanel2().add(getJLabelAddress(), constraintsJLabelAddress);

			java.awt.GridBagConstraints constraintsJTextFieldPrimeLocation = new java.awt.GridBagConstraints();
			constraintsJTextFieldPrimeLocation.gridx = 1; constraintsJTextFieldPrimeLocation.gridy = 2;
			constraintsJTextFieldPrimeLocation.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPrimeLocation.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldPrimeLocation.weightx = 1.0;
			constraintsJTextFieldPrimeLocation.ipadx = 383;
			constraintsJTextFieldPrimeLocation.insets = new java.awt.Insets(1, 7, 2, 7);
			getJPanel2().add(getJTextFieldPrimeLocation(), constraintsJTextFieldPrimeLocation);

			java.awt.GridBagConstraints constraintsJTextFieldSecLocation = new java.awt.GridBagConstraints();
			constraintsJTextFieldSecLocation.gridx = 1; constraintsJTextFieldSecLocation.gridy = 3;
			constraintsJTextFieldSecLocation.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSecLocation.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldSecLocation.weightx = 1.0;
			constraintsJTextFieldSecLocation.ipadx = 383;
			constraintsJTextFieldSecLocation.insets = new java.awt.Insets(2, 7, 12, 7);
			getJPanel2().add(getJTextFieldSecLocation(), constraintsJTextFieldSecLocation);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}


/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCity() {
	if (ivjJTextFieldCity == null) {
		try {
			ivjJTextFieldCity = new javax.swing.JTextField();
			ivjJTextFieldCity.setName("JTextFieldCity");
			ivjJTextFieldCity.setDocument(	new TextFieldDocument( TextFieldDocument.MAX_STATE_NAME_LENGTH) );
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCity;
}


/**
 * Return the JTextFieldCounty property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCounty() {
	if (ivjJTextFieldCounty == null) {
		try {
			ivjJTextFieldCounty = new javax.swing.JTextField();
			ivjJTextFieldCounty.setName("JTextFieldCounty");
			ivjJTextFieldCounty.setDocument(	new TextFieldDocument( TextFieldDocument.STRING_LENGTH_30) );
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCounty;
}


/**
 * Return the JTextFieldThreshold property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPrimeLocation() {
	if (ivjJTextFieldPrimeLocation == null) {
		try {
			ivjJTextFieldPrimeLocation = new javax.swing.JTextField();
			ivjJTextFieldPrimeLocation.setName("JTextFieldPrimeLocation");
			ivjJTextFieldPrimeLocation.setDocument(	new TextFieldDocument( TextFieldDocument.STRING_LENGTH_40) );
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPrimeLocation;
}


/**
 * Return the JTextFieldPhoneNumber property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSecLocation() {
	if (ivjJTextFieldSecLocation == null) {
		try {
			ivjJTextFieldSecLocation = new javax.swing.JTextField();
			ivjJTextFieldSecLocation.setName("JTextFieldSecLocation");
			ivjJTextFieldSecLocation.setDocument(	new TextFieldDocument( TextFieldDocument.STRING_LENGTH_40) );
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSecLocation;
}


/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldZip() {
	if (ivjJTextFieldZip == null) {
		try {
			ivjJTextFieldZip = new javax.swing.JTextField();
			ivjJTextFieldZip.setName("JTextFieldZip");
			ivjJTextFieldZip.setDocument(new com.cannontech.common.gui.unchanging.LongRangeDocument(00000, 99999) );
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldZip;
}

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	if( !(o instanceof IAddress) )
		return o;

	IAddress addy = (IAddress)o;
	
	if( getJTextFieldPrimeLocation().getText() != null && getJTextFieldPrimeLocation().getText().length() > 0 )
		addy.getAddress().setLocationAddress1( getJTextFieldPrimeLocation().getText() );

	if( getJTextFieldSecLocation().getText() != null && getJTextFieldSecLocation().getText().length() > 0 )
		addy.getAddress().setLocationAddress2( getJTextFieldSecLocation().getText() );

	if( getJTextFieldCity().getText() != null && getJTextFieldCity().getText().length() > 0 )
		addy.getAddress().setCityName( getJTextFieldCity().getText() );

	if( getJTextFieldZip().getText() != null && getJTextFieldZip().getText().length() > 0 )
		addy.getAddress().setZipCode( getJTextFieldZip().getText() );

	if( getJTextFieldCounty().getText() != null && getJTextFieldCounty().getText().length() > 0 )
		addy.getAddress().setCounty( getJTextFieldCounty().getText() );
		
	addy.getAddress().setStateCode( getJComboBoxState().getSelectedItem().toString() );
	
	return o;
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
	getJTextFieldPrimeLocation().addCaretListener(this);
	getJTextFieldSecLocation().addCaretListener(this);
	getJTextFieldCity().addCaretListener(this);
	getJTextFieldZip().addCaretListener(this);
	getJTextFieldCounty().addCaretListener(this);
	getJComboBoxState().addActionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerAddressPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 2;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(6, 6, 133, 10);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 1; constraintsJPanel2.gridy = 1;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.weighty = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(12, 6, 5, 9);
		add(getJPanel2(), constraintsJPanel2);
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
 * Comment
 */
public void jComboBoxState_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CICustomerBasePanel aCICustomerBasePanel;
		aCICustomerBasePanel = new CICustomerBasePanel();
		frame.setContentPane(aCICustomerBasePanel);
		frame.setSize(aCICustomerBasePanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}


/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null || !(o instanceof IAddress) )
		return;
	
	IAddress addy = (IAddress)o;

	getJTextFieldPrimeLocation().setText( addy.getAddress().getLocationAddress1() );

	getJTextFieldSecLocation().setText( addy.getAddress().getLocationAddress2() );

	getJTextFieldCity().setText( addy.getAddress().getCityName() );	

	getJTextFieldZip().setText( addy.getAddress().getZipCode() );	

	getJTextFieldCounty().setText( addy.getAddress().getCounty() );	

	getJComboBoxState().setSelectedItem( addy.getAddress().getStateCode() );
	
	
}
}