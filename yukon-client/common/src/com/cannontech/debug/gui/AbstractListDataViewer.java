package com.cannontech.debug.gui;

/**
 * Insert the type's description here.
 * Creation date: (1/5/2001 10:43:16 AM)
 * @author: 
 */
public abstract class AbstractListDataViewer extends javax.swing.JDialog implements java.awt.event.ActionListener {
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JList ivjJListInfo = null;
	private javax.swing.JScrollPane ivjJScrollPaneInfoList = null;
	private javax.swing.JButton ivjJButtonOk = null;
/**
 * RowDebugEditor constructor comment.
 */
public AbstractListDataViewer() {
	super();
	initialize();
}
/**
 * RowDebugEditor constructor comment.
 * @param owner java.awt.Dialog
 */
public AbstractListDataViewer(java.awt.Dialog owner) {
	super(owner);
	initialize();
}
/**
 * RowDebugEditor constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public AbstractListDataViewer(java.awt.Dialog owner, String title) {
	super(owner, title);
	initialize();
}
/**
 * RowDebugEditor constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public AbstractListDataViewer(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
	initialize();
}
/**
 * RowDebugEditor constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public AbstractListDataViewer(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
	initialize();
}
/**
 * RowDebugEditor constructor comment.
 * @param owner java.awt.Frame
 */
public AbstractListDataViewer(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * RowDebugEditor constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public AbstractListDataViewer(java.awt.Frame owner, String title) {
	super(owner, title);
	initialize();
}
/**
 * RowDebugEditor constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public AbstractListDataViewer(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
	initialize();
}
/**
 * RowDebugEditor constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public AbstractListDataViewer(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
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
	if (e.getSource() == getJButtonOk()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOkAction_actionPerformed(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('o');
			ivjJButtonOk.setText("Ok");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPaneInfoList = new java.awt.GridBagConstraints();
			constraintsJScrollPaneInfoList.gridx = 1; constraintsJScrollPaneInfoList.gridy = 1;
			constraintsJScrollPaneInfoList.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneInfoList.weightx = 1.0;
			constraintsJScrollPaneInfoList.weighty = 1.0;
			constraintsJScrollPaneInfoList.ipadx = 0;
			constraintsJScrollPaneInfoList.ipady = 0;
			constraintsJScrollPaneInfoList.insets = new java.awt.Insets(16, 11, 10, 12);
			getJDialogContentPane().add(getJScrollPaneInfoList(), constraintsJScrollPaneInfoList);

			java.awt.GridBagConstraints constraintsJButtonOk = new java.awt.GridBagConstraints();
			constraintsJButtonOk.gridx = 1; constraintsJButtonOk.gridy = 2;
			constraintsJButtonOk.ipadx = 0;
			constraintsJButtonOk.insets = new java.awt.Insets(5, 172, 10, 169);
			getJDialogContentPane().add(getJButtonOk(), constraintsJButtonOk);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JListInfo property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
protected javax.swing.JList getJListInfo() {
	if (ivjJListInfo == null) {
		try {
			ivjJListInfo = new javax.swing.JList();
			ivjJListInfo.setName("JListInfo");
			ivjJListInfo.setBounds(0, 0, 287, 203);
			// user code begin {1}

			ivjJListInfo.setFont( new java.awt.Font("monospaced", java.awt.Font.BOLD, 12 ) );
			ivjJListInfo.setFixedCellHeight( 14 );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListInfo;
}
/**
 * Return the JScrollPaneInfoList property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneInfoList() {
	if (ivjJScrollPaneInfoList == null) {
		try {
			ivjJScrollPaneInfoList = new javax.swing.JScrollPane();
			ivjJScrollPaneInfoList.setName("JScrollPaneInfoList");
			getJScrollPaneInfoList().setViewportView(getJListInfo());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneInfoList;
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2001 10:56:31 AM)
 */
// Return your object
public abstract Object getValue();
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------" + this.getClass());
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonOk().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RowDebugEditor");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(426, 480);
		setTitle("Data List");
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jButtonOkAction_actionPerformed(java.util.EventObject newEvent)
{
	this.dispose();
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2001 10:56:31 AM)
 */
// Pass in your object you want listed in the view pane, store the obj
// and set the JLists data with the new data
public abstract void setValue(Object o);
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF2F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8FD0945799C705080A56F4EC4AD8E7020DC96814B626359D5B312D395A54A43631291918991B10A9535899CD1DA96D18E85B479D9C44E04408CAD22D98C125A5C485A3A14600C0A3C882A3479F951505DB0E65165B3B3DBD7EA8CA3F6F3DFDFB4BF187D1D3E6FE337B3E376F5F777FFB8749BD101011E64DA5A4AD0368FF27528871969132507252A827C50996BBB17D9D846CA4E78E2543F3AE7465
	4D3158F348BB3B33203D885AF3E7DB6CFB61FD97A9591BFC84DEA22C33947AFC7FBA20CD3C4EF7DA79BA19347DB64906671B00C42061B3D544750F13BC1D3ECA27F324D989D95B035FB410FDBA2D9C5ACD00F900449E767DF2F83ECED70E29A8573F6B38B70124BEF9443A9D67B14D49116FE2E3EB74E7BEF9A4ED038B578A50FD621891E8AF816061ED6403F7CE4273F4135BF526DB69AF6996BC9E4523095D3DA5CE31AB60AAF185240A0A2AAE3F268A5DDA036457EA84CDE81544C1D105F13D3DB877CFC8961D
	0B10E350CE25347A82DCAF895ED7G5699741D05C8AF17603C00DE091F7163A32775B3345FF804F4BC67316D08F046A8091DADD18A1EF1DE60354C3729BCB636A15FD320AF2B4FE25F872884F48222G7F24FA105A7AEFF8D6F5E955CAFF3F622972FA4F4ACE75B345A94A700E4755F45A8531DBD11D04B0D95FA045E176B39158389937BD0E71A431144BBD6942C3E45D774FDBB34370E4DD62B1EBFC9F6345623EA0CF585A1608EB2F6E9B2B17E357EEA366359B6ED1EC99E156DE72EC11B5C51FE733E96D23EE
	1475CF298E6441FB1A2E9F0C3E1252E5931D69E89325E3FB847A125DDCB75A5F64FBE9C81FCFF20EB70465C33C254ADAA333BDF44821FB0921FA97AB076E65AB149EAF076EE5EFA15FCBA274E577DB6CCC3F7CADE833DEE84781968116815681B68332BDDC47BEEEFB7F8B6AD81320EAE783126C840BE2E37F5A36871E629058DC1D472F891EEE31D5D0A521CB967DC47F669FED48032EB163525BD1773BE10DCFC50F288A1A24F8E0F621DF54C4554F3E595E3E933FD1C55A6B107AC5D8205FCB783C3B5BF721EC
	843F5662F58A1A987C02756FEFC7198C882DB0A3CCG6F0CAF892D482FE2B8FF8D40E170A11E4EF7DED4BD22DCD15120A86E005701A7A10BBC486767296F680577A88F677102B6242D07B6D178BCCF373FE51A27C66A466D8B6AB0373195856817F3952EB32F34F31D29597EA0A9FE36BE2C4EA00EA9CCD74EA9213673EBBAA76E0B35DB8B5AA73059DCA53CBD5D1E2FD0A6DC43F697733FE948EA5BC5141597769B87C8B2F8BC5D114FF4762C60973916B62A20E022B348F1364FD90569BABFD97BEB343DC2613C
	5ADF589E015FDF86589A9F50B56E16A71140F8F233856310B8A0FA349A51ABFA1C7062577B22DBD057F4BB6DD10CB1B3A95D8BF6DD8D0664B768F3DB507684CDD3252E00A6B60B1AA6F9DC417EF83A16BF50658D3A843E567DAD68FF15C18F0D3B0C362885ED22DB71F8C4AA9CE3085E7F9035C9D1967BE15355CA4023B1FA924DE15C62306E939637E222B3A0488174AFACAFD8AC3C01E7D025FE90B93AA8D269C5FB5DE464B5088DA0EF4CEFF2216F28175B5E60A1EEFB8557971265578A2D61E2C77C03B556D8
	AF3319855E206D31BDADEC8B3F770729EF5069FE3657D4BF0E3D208C56E04AD26B14B4C5653ADEFD8967D986732F862C77F2DDBF540E7D5C1F68CADE2D48B2E32541631C0BB9A893519790418D2363BAEF70ED6F4537C27ADAF59E327E83971107324967AD77A18F9FAF64F99F01F69E205C4778B864E3FCE0673A6043BD525C2CCF989006CA7C0320AF959575B512A0ABAE42732FCBE9D93ACDE42BBC4FDAE143F357C03B832029DCB63F594E732CF96E84127C40BB91F349B8D54FA95420EC722771BCEB76E5DC
	F3937491BF67697C0BE6DFDF6577AB5D928DB3BABF6AAE19E547E81FDF5A864F9EC975EBF5E0DCC31CF70ECB38DF0CC3BAD90F4BFF22F19979138B6BB6GD27CC83FF0B2573477E820A581D683ECF43E5643A7507628F0CB5823C9D4FB9435DFF49EC56E168803DA89357312AA5ADD0B6FC5DC962E518F6159F7B620E90A27514DBE3996BAD1831DA920C9F2899D5E5855877A74D1D1AB61F955B7F7D56AFBDC755D79647468AEEB38BCB7F647FEEB3146786E50027C9F791E5D36616A16CC667F2E25E1EE172005
	0FBBCBC268AB214DF21E72F46461BAADB497BACB699B3450DC68C99B4F0536C2DFE7004769FDD750972C00F6B2E0B3A0E740E2BF86E89A60BA316ED2A919A48F2A03FAC37588F2739847276B41D7AF5B182F525B77DD4E603E0A70B8701742509CD79E98FB765810B69BE7BB9CBA2EA4241D94B20E69420E6688F64934837A182F9CCEB77B4A9583AC47D98F4F2DGE63BB3CEF25B4D9D64B6FA733FE69BDD366304750D88B65AB04876A78F86F5245828059E3F0AFEA6897AD6821281DB8179C3967BE9C0679017
	D1FE4747134908EA33C329865AB970E75521B1874633FCEE007A22A818FB910252FEC5E9CB213D5A20B55810168A6D2CE11EB31CC927B91C51BEC75BE8472C3D5AB6D1EEF4A74035466A449F3F74B54C798963F7611548B9C5711570B94509AB61F30A72ABBB999D66B0D7923C3F724A44B94739AB61F20E7DF45FCF6C59C167767BC5BFD5G2C7D753E17355DA4CC6E71B9949B7A1961AC07DF573F2955146A63067D7C39A8AF3C7DB48990B918DDBC1A1E25FFDFB64CE35A3B1934EE04F6B4E00121A7F3CE065A
	719268DB294BF65D70F87F486C6333CE0C991B20BF7B3A45DEGB88D68384E6D6205AB137A2E5A2189FC576A2B11657C5C5570F27E5DD50C47FEA520F60B2132CC391AE77485AD30CE937B116D6C1B0DF43CA628AEF11CAE2452712CAF52783FDD0D24AFCC263353D06ECE31C78848200A0ADFEC746A25A6727DBA4AA9B10D4BEE64BA3FE7FAF490FDD88F9423B0DD33D76CA6F1B7705B14B46EA3B7433759C0AB86B4G649BBCF6F7533B87CD52E4542DB5B770DE6E8C26BF7A1EF63BB08F533322EA3C3EC2DB70
	085C26FF189E2A9FF9A3C11DA89E992F9FAC9E5EC7752DF20445438C033E10529DA6BA0B13C7B3F9EE528BFD2BC6382FFB107A78E3B721B63E49E90ED3C8EB07F62FC17B51A96EA7351BDC87DBBAA36BD0C7E7F89D6A6A8C6FABFABBA776955E4E09E57FB31AEF18CC3624CD72B815C1E0B95ED2E87E121A3A662646665AEB0D1FDF2BEE0097964F92864D7717655BCE052D87E80C3C49E431D6FFAE1BFEC84FF137D93374B1B937AC76F33728FC2C661C8F7778180D45F0563FF2CCBF1E8D52B9D37FEBE37AF12F
	ADE346975B4C7D9926716CDCCF9572B83D7529C4A27F60430839745CDB6CBCCB74E7D36BA1D2B0F3CBA61B67694BE67E644CBFE8CD0BB04F86FD7C66DB41F82F47D86AAF0BC7AD764AD12ECF1B6CDC1F1C235CD7AE48C4DAAC34938D5A431436FC1455A20CD612F9BBF19067626F3123135F0330B5BE3D2DB5B2C2E47DC5626EAE320C5ADE3FC78487AEF5B37F74DBCAFB9110B24AFD0C7B94669A9A989373AFE592CAA6B78A3FDFFD2AE612F5B274A7A29566C46C56E51885F3546A6FAC6E3CA4617A9DC09F0A62
	F6BE4D759FF8368AFDFA14E1D5AC8DB3C0ECD29570325AB02352AAD367E13F754E6E169F4B643A3B64475FA4CB6FF9B5ADD20E999F456439D4FF2EF79CB2455073EE745F6B21AFFD8A549700CA0083A0CF61E7FB5205FB44E8895B2F52B4213B17D600BC08624F89C1B28687B637C7461D6AF5F8EFE1A47E408C756013D82D6590DC066CD35271DEC3960615008636B385E53945484923209D8FD88ED8B3051D2DECAAFE13191997068FE375EAFA95563A754D5D2AA24B98EC5083636FA3FABFA9C2BE5773FA1170
	1A6079B1F30FCCE5F5EAA2BC17CD653A58F48A1FC714A364G1B0B5E1172BF261FFBB05C6A32FCA91B4BF25F373FC1EA1EFBB56DD084D92E194A4ED9B0AD68BF5677ED4DE4BC7E0304B4437FB3B553793FD1C2DFA8F9A48D222CF41D867CE6785063E232FACF417E8E18F78830G309830E29A5781DF5F3BCC8742483F566992F98CB16AFFF76438F759BD797D38530D778ADD8A303F9F676774BEB75A0D26F8A9115194B772D115DC3D9A7BD427875C48F7D96C21E496631F4F603F23A543597222F9FEF33C93EB
	ADA327455B040A0ACFD549F9D6F0B5D08DE4F16C201D473126E81E57B65A69EF9350CEGAC0D66F56050E1AE5724076613062873E163CB326304F5F5347E5BE3F4D02E09D08732F97A5CE67D403EC8F16AD80CFE6790931C077BDE9BF1556EA7E17376209590FEA735B30B7FA6B8AF061FF3FBA91EF39534D3GDBE378BDC23E8F7DEB6EBD967BB10083A08322G7100A440F2409A4086C07AF460BB2098D089E882B881C353392EDD7694C57295C05405C295CE95A4D4B9DD5EA71548BAF7DA19DC67EA95FA3F85
	3A6298766A79F43D02BEC86969710B06EEB7AAB9862D4E5F2478A534A6664FDF4C38335F98B75E613843765B9D47F2601FD31D0D1B4EF260B4033E0A5263CDF4E6B7ADF6EEB70B20AFAB164BFE190A7ED006F6ACA08130943096109A4B6519229CA31354E18E455BG0213CDE9AD1BFF0F0673E74D007A8EF08E508950GF1B3787CA55E52897465022250EA828B8BD097D1B5E98E63611E2C09F818B0A3D2FDB1055A4312992177F033A8FD450C50FB38DD257C9EEE8D74154F647BAF73C5565B0FFC136BED0DAF
	3C2F2C770D77150D3E703E3245E776150CF64907FE43857656EEFCF74D20DDB4E8CEBAA7525AA42756EB507B683E10FECE44850DBA056EEB10525A392FB651AE6A346BBE34BDB8F2AF7E9E40E823BE4C73FAA4D95673AE7A0D6481D3B47891CB77A8F985533EE79B340B066DBCC17FBF043DBFE3FA3FEC7F326F10465B2D76FFDD031D7997EAE43DDA25062FBD1FD143571EEB55096F211255483FFD3D2C0E7F6D6B0096F9EF073549F57EFDAD3C4E1F54466BFC0596DE67ABB5334E331A6B3DACDEF3AD1849EB29
	692528F39B0C763D341DBE4B789F225249E409E36FCEDFCECC3A465D7C0E4A6A241A2C496AA4B38A607C6533D81D046D86F85FE2A564FEAB4F311F2D24B9B4E6553A0E315FFAAC25663A3C9D46E16DBA824F71797226CF4251D88E7D4BG36D8EE73D4917E0FD8185C066674AC27F9351467B4F38D597D3DF4A25F3FF256F839B1BD59DD4675E49DFC436E420A4BF8EDC062AC76DCC09940815062786F85974A785D5A3CB8FE8FB233AC749E646BE966FB0CE5E52177A098D7027D2F14055E03FCE2B577671F8C3D
	876163998F5A4A4CBCD8857BCA0E1BD8DF998FBC74AC2970AD2E47E8FECA4B1EED31639918EC04439877745A030688C65FB734BDC867A102307F75FB6160B0BC13B6128C0D3C0E775679A8C80DD89B5DFE156CB0FE712B231EBFF9203442767B87ECD612DEC76C4CAB5411F4B46DBA12C1AD3F0E5840AB8C61FF4A60FF76A4813E85F84B6FA33708D567AB565038579C42EB3322D1735D4E66183C08F7DF9DF3984F3CF30239B74BE1CE96A0CF12003EF28E0F79B1075EA3116B6166FEC116B5C1F6D7F91CD89B1F
	9D5644202F2DA840797D7C9B3D1375D595603E7BCCE37E87D0CB8788C6D85F620E8FGG18A9GGD0CB818294G94G88G88GF2F954ACC6D85F620E8FGG18A9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG488FGGGG
**end of data**/
}
}
