package com.cannontech.dbeditor.wizard.capsubbus;

/**
 * This type was created in VisualAge.
 * 
 * CLASS NOT USED AS OF 7-5-2002  --RWN
 */

import java.awt.Dimension;
 
public class CCSubstationBusPeakSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjOffPeakSetPointLabel = null;
	private javax.swing.JTextField ivjOffPeakSetPointTextField = null;
	private javax.swing.JLabel ivjPeakSetPointLabel = null;
	private javax.swing.JTextField ivjPeakSetPointTextField = null;
	private javax.swing.JLabel ivjPeakStartTimeLabel = null;
	private javax.swing.JLabel ivjPeakStopTimeLabel = null;
	private javax.swing.JLabel ivjJLabelFormatTime = null;
	private javax.swing.JLabel ivjJLabelFormatTime2 = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStartTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStopTime = null;
	private javax.swing.JComboBox ivjJComboBoxControlUnits = null;
	private javax.swing.JLabel ivjJLabelControlUnits = null;
	private javax.swing.JLabel ivjJLabelLowerBandwidth = null;
	private javax.swing.JLabel ivjJLabelUpperBandwidth = null;
	private javax.swing.JTextField ivjJTextFieldLowerBandwidth = null;
	private javax.swing.JTextField ivjJTextFieldUpperBandwidth = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCSubstationBusPeakSettingsPanel() {
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
	if (e.getSource() == getJTextFieldStartTime()) 
		connEtoC3(e);
	if (e.getSource() == getJTextFieldStopTime()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxControlUnits()) 
		connEtoC7(e);
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
	if (e.getSource() == getPeakSetPointTextField()) 
		connEtoC1(e);
	if (e.getSource() == getOffPeakSetPointTextField()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldUpperBandwidth()) 
		connEtoC6(e);
	if (e.getSource() == getJTextFieldLowerBandwidth()) 
		connEtoC5(e);
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
 * connEtoC3:  (JTextFieldStartTime.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyPeakSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (JTextFieldStopTime.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyPeakSettingsPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JTextFieldLowerBandwidth.caret.caretUpdate(javax.swing.event.CaretEvent) --> CCSubstationBusPeakSettingsPanel.fireInputUpdate()V)
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
 * connEtoC7:  (JComboBoxControlUnits.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPeakSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88G4ED365ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BF4944711580023A4588F5B620EB8648EBB36A3A7CACC92A5C74E640EDC38BB08090DF390F38E7191DB6710BC126018F7D18CF99E2D7EA001909292B68459C0A20CAC8448A004ECA47447DAA350828284E834BB3BDAE9FF5A9D31AB9970D5F5CF774C4E4E6EA2127341FB45CCD7F5D7D5D7D7D7F7D74FCAA85EB1B3AB5DD4AC8869D902726FF03AA0541F91047777B6CEE7385C86F30E207977A640
	A6E1EDFEA6BC4B0066CAB467948AFFA366C239924ABD89661CD2F85FAC3C6CAFEF0717A8F2525A846162CBDF4E0AAD6775ABCCCE8EA96FBF1C8F4F8DG550033G4CECC0799D074B94FC884A9B14F704F413A0389A314E95D319861F2A54EBG1A856947310E6D70B6051E82B82C33901E4B9BE95DB4F866916DA65755AA75FA6F1DA92C7E411B26821425614F2067B8EDFBCDF9168B0F91C3E488BFBFF2921E75DDC30E772DDEF715D574F83C9ED9328ED4597AA51BD3767A2B024EB151EF8312AFB0525FBF9228
	2BEBEA6A9E698F48226C74FA0E0F843AA4F128DB12E52747916892BD12CBD8C97AB15914CBE4C0BF209CEAC25C9FCCE83BEAF8EF8768EBE2FDF92620DE51B5F1E826307C0B45D17BB2270976E1F9135A177A62D15354E664FFB7999BECD78E659EG2A4B1243CC17454DCC175C2BD8B73F451C338B203E05611FA6F88B14DDG13DA180E6F7D196958735163424CE9560C0DD1F41C51C2F51B57226AB8ABFB543415784F23FD6883C2AB4885288568853886B083608D6237621537615958AFB7F95DEE2F275167BB
	6E32794DDE1B6402773ABA20A8385312556B378982754D4F18AA8D74E110529A6E178834EF678466CB49279F9526B65BB2328C7A1632E427E93E42E3D92B5A37DDEDE837BBB250EEEB810F6D50B694FFAF418F3429F8BAD69984CF4AC04BEFE7F66E5940F4694CFCC0A85C5FEA4AB150E57A3F9CB5A534D39DE6362B3AD09BCF6FC79BAF847C8AG21431CD386D08B505341EC7C15AB876359388366025FA33A405FFD22DFBEBE62F459A43FE26B7B8F4732359150F68F0E3B9D35CB52C16D98AD7BE60B21DD16A5
	3DED12BB28BD26F428F621321F0BAABB39A352B7B670B13CC666445C8EBA06999CFF9741AF52606958EEA0F8ACAF87DA738970F5D23FBB08782493FA9F31927C2293FA9FD9F21579C89A50648F4CB9F4FC8F59B0C62E02F2FEA77086288768858881CC69E4E37CBFD7765D64BC6A520CAFED7BF85F96F8CAA159AFF6F8A05CF92C52D9516F947BDDD2C0D06A4C6EC33B7607357BAE195FD61051A6F9A4BF891340DDF4CB60CC81DA675F7AB6E19D3FC4289627DB8281EE1F4078BE5537991EAEB1A01F7159C4D9D2
	EBD07A0F7B50A72E0AE701A3B040F16FC4FBBDC8E2EEB23C4F6FE43632CA08ABB7037F1B19DDCE09C5707CG1CDBF25555B5BB2D2806689FE5BE7A298C34EFA754DF86607A501C138610F09266D117B9A7390B4D55DF77B01FECAEF8C428DC52EC681388752728FFD9CE69FD72EF092CEB001F81B087E09940DA00ED27E1DCCF33F5B7AF4397E30E201E28EF2CF9043AEB4BAE25CF589F6CDF74B9DB91D3F616626B31E4979870DD2F78F6DE14B6F47C1E31639C6885BB04G261FE6E3B8C5AC273EFDDC8CC84C1B
	CF7A4191A51BBA22346E037DA8C73AAAF964E649A7F9EC7092D0FCA929BFCF75F33B177B6057889E63DE93B8E20063D37AB7D1BC70504E9AC61FC36822AC7B1D7DA3324436861CBE0F6892888B2981252F7F61403E4E02BE4E87D84A7BEA36EE5678EB2757BBB4623360CCD47C7EE5323E2F047A195D4CDF6E3C0A7B368ADE1EC24AAA3D78EACF1CB1433663095311FE73CEDC99717CA63E0C73C11CBFAC7E2ED770B8F6BB943F23751C24DEA774C9861881B08FE0A5C031856619055965A7D7FD9AFE9BB47C569B
	7831916C38653E3CCAE4235CACC1DD7BD81FB65E841F1B51CF8F54479AEC5167C81B4DF80E18ED46F3245B96FB8EDC34994D81CA6B37E15F3DC10FE6CEB9EC982F2DDE0FC73252ED3752C409BD3F36E0BF1C9E27FC4EE9F3C8F237F3CC02755F02F3E3B6D993EC709E3230389D5C4962762E31870539D7F6180C76FB539FF95F3448C267C02AC50D5BD4278189672A64125CE028A66F08C726F88FD9EF94FCA003606EB9F387362DD04E8ADDDE27C73600B9DB1D124BD6E905D8A2D7117F69F2D7B1A8DE95C3D501
	A0982D0A0CC8D593D2DB70F5CF79BB6F3CDF9E6F90D2E9F77A258E0FEF04315CF3F6CF3562DB30365BA791FDCE5A6D7F3FAA6D277ABC3F71865167386831851DB6F960135764DDE61B351DF792DDCEF09D3AE570D25C02D40A447B2A6823CB725B3DFE37E4A39AD509C1D9D12711107FC2FD8EB2FD76EE39A3C29F2F6F13D627067B4E97CECC566953698DCAFEBEF41F1C6F3C4BF5CADAC77D79846CF37B3D473D21A6B8467B3D2EB39091821F24198E51F5BC5B0931A1738C6C97812A4F30F57C17925259BA2EEC
	D61A3CAE978DEBC0CBBC03B16B290CE2CD9CEABFC373AC9BB87D55B874BFE546261F0EC33F91077EE5C37AB2F8D27A5EACA37AAAEE07E76D05987F256191890E95381179C09AD54E864F5B0BF4343322EBC4BDF33CE4477550255983D17CFFBA0C6371FFB9F05FB344793F684896706CC0F95275E31608BCFDFE279B360C3829D372D539355AFCD566D9D8C3209F4D706C8120EDCF3E0D6724E6692A53AAFD88271CC61B4DAF8514D41010F98E794C8C321C168F5AA5GA4A93CD24FD23B52FA56902B57856501F3
	516A150E32FA89D01E89B0771CD13D67F83D35D0AE6EB167547498557B3BB1D6CF867A142875168A2CDEB2506781249A567BFCBA2BD7FE9E4EDDGBD67436BCDBFCFF2CDBCC7360F63DEA2388FE04333B70B1D7D4D055ADC5F2EF51906799904E567A97FD56775B911230348377002B9277C827343CB835AFDF5E3A06035BA49FCD7FCAA5F29DD4BA9EE13F39DBCE16E87648E58A40518DF16BAD1D7BC33AAE845373E3277BDA1728C5EC2320B6C585294A4F14762ED02E3F3BB38F6935F6A1CF5CAC1015961FA2B
	B6CFB8E7E325E9FD94BB54DCD04E1B97D4BB34E70F1AE8BE70E7194C6E0997D80E30BF0B6196DEE03E7D39BA4C919965C72D24B3D50E91E755086C44C334A80BA498EAF3207E5ADC458E1997D9FE7178D11C2775D036G38GE82EE3814901E05DC9001BF3114512646B5AD8327AA2BD63EC6BB5675058DF9DC25F996722230C4D3CBDC5F4ACF5F11C597B214D8D0A5C05DFFBC0287F3840E41473CC58D8EA2A6E25F66E69D56D1D70DCFA464C2F335C6C14906B5F43D0E7D9AF6B5F23FDC67DAB3E446C52B04A5A
	D5G2E6F92EB373255285D74CB4CAEDF9A3D953BA83949B1A6B3997825F2196F35E9E5EE3BCCE576DDE6B287478E40F37C6BAD4BF77DBB17AB834F644BCC6E1DA5C6F2D3AFB39B252733F6E9002B3E425A6DF19B59E8608A53F7A07D56EDB40F4B3C867CD2384C60C92D4C15D714716CE3B2771AEE55C6CA2E7A00B6964EDD3E57706C02D051C7FD313ECF9F8BF738F0DE76G5E5547E261FD4ECD1AD8F89A220F68F1406A4A4F070328BF2C23CA39F9906512F5D4E0391ADEE26F8940F386C08A40C20015DC4613
	03F1F34EAD2198B96717B24713BB4E9759195E11B51EF6653CDD30D65F2EC6D4F3A26DE2E4FE0466F38F54131C15C87339EFF27C87841F2441D3DFEAA7B1914B2B01A6D8197C2703FA797936F019953AB2ED775308F6D53AF2272E9D0D073BABF05DA671DEB3856A6A543DBE0D6BAFDE4FD5E459ECAC4F7A8D8F361D816559G0BGD68294CA661CCA00AE0979406BC3C66BE1388FC4DB8C55DC9B6D67FD21DB334F4C08F6FA7B782470F6345FBF6DE37DCE105898350E5E1A8E1608F6FA9D669A6A30330D693014
	6B10B7F6EBBA14C634536BD0E8B77213E3153542CD6E8BD45741A3EB8B9C4BC6E9BF64B6365F283033FEF48B37568FB1221D3E9FFDF6A3DBFE2704699062BA64245F1A8EA5916D74BA4CB254214F4D46F3BE5761873728435238BA888E2371F4385E908C67BDBBCFAB3AFE6DA433D719034D7B5FFA91578565BE871B6341A14CC3323504641684360E6CF261794B6BF7BA1C9E065BC3F0D62F1FA47DA84E660E1E0F9CF29B67A3075D46794820BBF64E7EE3F734FCA54D2F7F016C17FD503FA9G94F7A30B61E6F2
	5CBDE43F1B8265799C7775A3A4B78F654C81B63E35758DB806D0EE87F081CC8118853088E095C03E9362A6C0A7008FE0AA40AC0045G2B810A87992FD304D76DA0BDD7768C52F3E3881E5381E683AC85C89B246744E265FC07E73732A1B81B8D517399657561D15CC8E87D0347C32771DDC03E86BFD5730EFDC09D35F5C99F957D0DF8057977G6D7F621B289B8DE6A9365563BBF5BC5B953B074F25FB235A22B40A3CCD1A772D1AF77463B7F5F573945F3B193B823AEF94046D37E88784F607204717684A988F34
	F7885B097C85DB308FBE18D2D200AC4EF85AD3E8BF1711C7C4EE9ED3A1DC776CD01ED227C576B66D67B6DEF1DD9B2FFA06F477133A7204A196DF66F82247066FFB0CE343828F76AB609D71DBA57D7CDF64A963B4F53751A1CBEE6C0612DFA26DE5516F10A26247F2521E5222357F05A7DA0C21B6DA90E623835CC6AF74E9ED142833C9122EBCE7081DB32E0FEA799D627CAA5A347C96695A276A4AE91CDF4318165FC14EEF34AD565EBA5F95B97FA83F4F88DAFEB51C5FBF154462D79E155F7B69DAFE6FF2FE12DB
	4B2FC6573EDDD76EF5317E261B347C8EF3FE5FBC99CBBF1F01FEF47CFF6745BDFF0817571072284056E029F1837721960FCDD9B4E9CE2C182CB50F03E2AD698CB70D603E8A38FB53198D9E2945F554A50EFAC7206DD297561B9316431C8438E400390A1ED4765703989F6CFE518DAE596D132CC20A9B5B4EBF425A16C339864082A083A43A191DB25FC23F56DEA3F50A7D187AD4F47A3437C4C767BBC2DE6774F6BCB76AEE23889BCAFFFBEBD9A41D3321F52A1CC5CC8E3B2FD19421F4C7AE57D339BF895773F3EE
	4CC91FA048D6B80D0B0485257D2EFB13816DGEF7BFAE9B1271F7179B437A114BEBB8B75D73742C6F556B7EC8E2B43FB2148B8EEAE5251E9AF188EA93B43E5045F4A50BACB72D4BD0D680B8F95F13A768A0549383C8547527016C5E0F7837756EB739FEB5DB47FD16EE17911E7769951EBB83D56E2C4EF67746FB79B51FBB97D23A6A33A0F53279852A7F17A0FDA0D6853B97D63E1A3FAB2272F739A5167FA58FE70B5F2F6DE8C65D40E7BCDAF5BA326F15C55FD08EB77C25F3C8C77FBF27E74C1F982475D28E3ED
	93B96E275D08CB05F29A479DA3672DFC1FB9E71B0F613EF718352D663897DA91B7814A099CB7350D55CB6238A5A5DCE70E3BC64EB26D43308F9DE6385FEDE575DC9C37EB98F1F3203C0863FEF01255CB9DE679677DFDE179884C0C555535791D3663222313C4B9FAAEF9016C3BB11E5678D91E39708866D4DCD01E84B0434F6E7EC3FFE2B97464C79F90BAA73498FEEB3B5CD2E9CA71539839542F664F66719CFAE79D13D99C00FDFAGF1E3F55A3147B9730BF4FA86237464B0BA46141D02167EFC989D757D2EC9
	CBAFBC2A25230D36AB6DE95F1E5C4D7A367649A441752D03D13FA3CE8C50BE4DD61E3E53BB05B94BC6CD546E2F584B844373A0098C347F736B5939F5D5005DA164D6B3DC054CF0DE8B4375F15C2B4D8CB71D633E5144F00BB86E532D8C17BB42F0F9BC7F5140F15F9EE6389047B5FA99EE5688DB2B565463DD209D42D645D52CD3F1836B2CG7AEA00198417FD295940DEDA1BE7E84085EDA6G6CD4E809707E85C17DFEE833F077BE54416AF50D38BD81014AFD184CED9C1B95D719EEC72B43F5FB0BFC4F1F866F
	19C1265B343AF83AE9752B8D066B5699546B363ABBD237759626DBCF1069D65F962E5BG990381204BDC372507636936DE23DB12CE37B9913A7D3EB5D23742B6265BFCAE77CBED716466E864AE5549DD294845BA145F4A12F87C72B47C52F47C72C3AABF5A0F7B5D6136FB0C4448B22837AB447A306175F8B28BB4B2FBC261B2DDA13D6D0E8EC75A6E74B033DD084B7D5949F8F2B7EAFCAAC1A7F7C6084654F4016D0DFF9E11AF0F87B4AFBE09446A24904D0B27F37C5D841F224153FC7916A36CF78EF3510E23AC
	977555E10CDB89D0FE98E08E40E20055G05E36C6CB841075FC1454CBFDB3C3ECEB8DC3AB429A2A557D2F7EB5F2EFFAF07456514FFFCC818FD57AB69516EEDF70D51F85C2CBCF3CF6DA4F85A3F06GB9E782EDAAC08A40C20065GE93CFF73FD1534FFF8455CA84B22F5005CDE334E6105320A464DBE6D5B8F6B35FD4B7FC8D97314E74DC711679A5A2E20DA5B2EDD29572BBCFDD15B4996ED3BC9CA3D694AB3B9EA3B571A356D66AA7596AB4F95D15B7DC313365DDA25DE61B57A5CF5ADDA3BFBDA356DEA15FA1D
	4A33AFEA3B8DE167E2D9291720BCE7C4ED774430365DAC255EBC6539B4EA3BD62F365DCA251EF01DBE0B2FC7362B380E3F77CA46CF6F143871C26FEEF8FA24E0B93F08EC454075D6C0748482125FE9FF2B323C3A32FCFF4D9E6E5BB3035A7B485AF57B235EC73A949DA6A84FE593F7127C971EFBF28576EF5531C89C53F58BF927F2FF164F6654362FFCDEE8FE7615741DD1644ED664655ED077F1D39F9DB351BEBF318F5B9C62B2DEB62976982E9B0FBD0C7AC2F996E72ABC5F32044B7BD636CACBB97C5711D716
	2B727CF39B13973D7ED1E25268744B6B54B1E85B28727E65563F0E3E0305AACF67F0FCFD6F462DB37B4D5791757DFE4D7B36A3713EBD7FCB004AF8777FD4860D675F9E0E1E6B7C6E30F12E737B434677A08B06E3FF177D4CF0746F32178EEB3F4B266B5125A2BC0FB4403C6A8518G108810F4032DC5F67FDB82D90B8C5621961B83F32D784BA219496F88C46FEBD7A07E6F114E8750D17B3D326CF5A3FF063F924071123DBE02D46E1782E8A33F53B1A0532A8AFEB0007E66126C84CD4F2629756C4CBE6F86BB2F
	EEA1E71825D0DE41F19FB533B36CEA0E5BD54D4E6C4D9FB35CDC9F797E824A839C77D9BFEBAB2BB832971F8765459CF7031C59D7C0B90D63DE6467784C89DC3F9644D920EC633803B5088BC1B901632657707C8147BDC54EE229D0DE4DF1EB8EA2AE778EF3CE599D8C57475B6E6238C7DAD9DB19639EB8445AA6F05C1AC3DCAE476D66B90AD40E3B0F74AD8D4A39F7721C826FEFB1477DE818659EEC9CF7211474974A899C774F25DCAE47DDA2E745D4A82F66381F10FA3993213F93992E210C77F7A24BA7BCFDD1
	3B7E95BE7006096DD57573D91E282CFF93755F637C73E6F2EF8878C593595CF97946DEF81E9587A33F9884E4175F6B137C72A84512AFE415B939269877742B00CF7EA4F3CEB5C0BBC0AF008C10GB083E09640FC0065G2B81F2A71BF33681D482B4GD8A6B3BDAE8AFBE26821A4E348C4760B5AFBDE29A87ADCF695451F4BA3C5645BCB180316D11FF207B2D6C46E0E6D760044E34647C5059C57916872861CF877C24FBC1B5EA367D4680BBC191DE55FE94645D88661F4ADF916ECAA68F9683E66D867218C9DCC
	501C7DF13C9F86998E651D4ABFC5667EAC404F61721F75054BFF35D4157F10BF1E7C2C9872D7CED64FC9D456C316703367EC92CF5220DE66DDCC1FE9074375593ED555675518E7FAEC1F93C31F1A3B747A3C53922ECF9309D18DD02F136BF322A6DC1F7B0E297ABCDE93CF1F3C987ADC0B50673F1B4275D9C362DE8254CB647A94697479D6032ACFB026BEB9F16CB36FAEFA6F2E7A4BD3A4F6AE827CB2GAAEFE923AA2FA866F8305F29C513177FA93D3C53A45E1681FE5727D8FF9F5379431A632A7C8D35716C3F
	BE06FC8B48589BA6FF8D0959FD00F7F179432561728FB42B72FFDC9ACFFEC18C79B3B47229AC599BBE7E1FA16BC09254CB617A7CD6274F1996D51FF3E571745998C31FD410F1C5F927315B540A3C156F4CB7635E5AB06E426E69846C37CF768FCAD659A2BAF81BF91BD1AE5F5F51E5C121BD399977EA789BB92768F20E114F5C3B212247C1699BCB22476BADA57163750E92633D576E12483D575BA546FB2F6A926D5E0B625EAD41186700F52607572B6338739CF70C70C45CB93C506278962297625BA59448709F
	903D0284574376189A5CF985F72684579C68720017FF1BF42104780D536522654B240E5383CB90370715686874099A3D87B88E75267E74FB7C8BAD0ABF2EEB467D3241F94A86E366F5E88ED3346D03A7844E678F1DAA1F945F787814FF287239562572719F9E9F1F3FBF2572D9532D7279F34D78787C710C4AA72BC765731B03B74367G67D3F1DE65B369224A671EC363637325DE15CF19CD65F357B86DF3D4D279BC61D079FCD8BABEBE99CE154F15A1154F536364F3A7DEFF8B74EF85FDA7B7BBDF9012FFA5E4
	857CC2C1D87BFFFD705D1796BEFFB610A1E4F788DBB1DCF643599E2308E443AF0114BF4350AA647970E29EFFFD5BA1E48648A3ABA00F3A24CE2158FE9AFF529510A5DBA30BCF81D9A869C4C97B70DB51FD6DE2E0C04618F234D34874609F0C900AA859A5FA9C7BE830BA9AA2BF4FEE6CC34DC91895C05F4DC4DF64D24539ECFEE2636EB7CF244E4E40303A4E6595ED2D2295C2E80710A78F7825400057E58B790EB590C639C10CBCE47582F6EF706EE3E72B70CE1DABBD947A42140295852DA6B8D87805FD24A62E
	8ED53C062DE9C07403A8495FA1639F40707AF7F15A9DFF147F74433652DE13102EF4F2D838A118C2BE9934720B1EG7E90GB4A857E940C81CD1557D8B2A27BD766A8542C8EDAA8BBB28492069BE0764F56FEB345B1D9E09B3G7B817B6D1CBD56295255A98A5DDBF2E249EB7DB05E4DC2063B3A1A7C3F1F7CFFG7F5FCFB07B89E67F81B062AAA166BFBDBD9440DEE74B54C9C01F0DC49F9C662AD698A97939DFADF868A7752BD6C26FDA05AC72A383B0F621DA6B4391F73F643FABF95A7675094BBF0BE361C317
	E0FC336D225B699A852372CCD488C9DBC2DC98F63E03C46C65D78F56E53C78458C131059A1E453958C7C9717A170DF32CAF588993002915743481F25BCF309BCA142A1911B9969D90826CD56117F8B46EDB7EC3511345D04827125B82213FF2466AD55A91BF5DBAA3B5BD8598C0E4D9274DDC02C69366FD8D52CD1C06CD6850B1F52E54A42C67CB4293CDB124B4957CDF5323079245DDE9E01AD24987CBA289C3F7F2963CD41C399A62B3D3D4EECD61F05640B23F27E398F72A19FG0521B6A89F741413AFFA6AEE
	8F33E653350BD00F29B64DD6642285575DFE1A36C6EA0A1FG675CFE1A1AD5CDF394395537272616709905F2776BB425FA65FFF21AB6281AB62B1ADE8C9F7DCCC55375371F267D2A26ACC22126F9375F68BBA3C79FBFC63C8D7D54E72C69EDE853202A699B9ACD33599D6B1E0F35396D3449C5D16FF6E7A5AA7754097A5C76AB85647EEE1AB9E75BB416D3EEAAF8C7081E5B68F60BAE17AC3A069ABDB64CF39C9F15A5750EAA2DG7987D89D05C8E92F95205E031AB67F8BD0CB878803041DC7D69AGGE4D5GG
	D0CB818294G94G88G88G4ED365AC03041DC7D69AGGE4D5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG109AGGGG
**end of data**/
}

/**
 * Return the JComboBoxControlUnits property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxControlUnits() {
	if (ivjJComboBoxControlUnits == null) {
		try {
			ivjJComboBoxControlUnits = new javax.swing.JComboBox();
			ivjJComboBoxControlUnits.setName("JComboBoxControlUnits");
			// user code begin {1}

			ivjJComboBoxControlUnits.addItem( 
					com.cannontech.database.db.capcontrol.CapControlSubstationBus.UNITS_KVAR );
			
			ivjJComboBoxControlUnits.addItem( 
					com.cannontech.database.db.capcontrol.CapControlSubstationBus.UNITS_PF_KVAR );

			ivjJComboBoxControlUnits.addItem( 
					com.cannontech.database.db.capcontrol.CapControlSubstationBus.UNITS_PF_KQ );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxControlUnits;
}


/**
 * Return the JLabelControlUnits property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlUnits() {
	if (ivjJLabelControlUnits == null) {
		try {
			ivjJLabelControlUnits = new javax.swing.JLabel();
			ivjJLabelControlUnits.setName("JLabelControlUnits");
			ivjJLabelControlUnits.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelControlUnits.setText("Control Units:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControlUnits;
}


/**
 * Return the JLabelFormatTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFormatTime() {
	if (ivjJLabelFormatTime == null) {
		try {
			ivjJLabelFormatTime = new javax.swing.JLabel();
			ivjJLabelFormatTime.setName("JLabelFormatTime");
			ivjJLabelFormatTime.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelFormatTime.setText("HH:MM");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFormatTime;
}


/**
 * Return the JLabelFormatTime2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFormatTime2() {
	if (ivjJLabelFormatTime2 == null) {
		try {
			ivjJLabelFormatTime2 = new javax.swing.JLabel();
			ivjJLabelFormatTime2.setName("JLabelFormatTime2");
			ivjJLabelFormatTime2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelFormatTime2.setText("HH:MM");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFormatTime2;
}


/**
 * Return the JLabelLowerBandwidth property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLowerBandwidth() {
	if (ivjJLabelLowerBandwidth == null) {
		try {
			ivjJLabelLowerBandwidth = new javax.swing.JLabel();
			ivjJLabelLowerBandwidth.setName("JLabelLowerBandwidth");
			ivjJLabelLowerBandwidth.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelLowerBandwidth.setText("Lower Bandwidth:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLowerBandwidth;
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

			ivjJTextFieldLowerBandwidth.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99999999.99999) );
			
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
 * Return the JTextFieldStartTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStartTime() {
	if (ivjJTextFieldStartTime == null) {
		try {
			ivjJTextFieldStartTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStartTime.setName("JTextFieldStartTime");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStartTime;
}


/**
 * Return the JTextFieldStopTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStopTime() {
	if (ivjJTextFieldStopTime == null) {
		try {
			ivjJTextFieldStopTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStopTime.setName("JTextFieldStopTime");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStopTime;
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
			
			ivjJTextFieldUpperBandwidth.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99999999.99999) );

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
 * Return the PeakStartTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeakStartTimeLabel() {
	if (ivjPeakStartTimeLabel == null) {
		try {
			ivjPeakStartTimeLabel = new javax.swing.JLabel();
			ivjPeakStartTimeLabel.setName("PeakStartTimeLabel");
			ivjPeakStartTimeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeakStartTimeLabel.setText("Peak Start Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakStartTimeLabel;
}


/**
 * Return the PeakStopTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeakStopTimeLabel() {
	if (ivjPeakStopTimeLabel == null) {
		try {
			ivjPeakStopTimeLabel = new javax.swing.JLabel();
			ivjPeakStopTimeLabel.setName("PeakStopTimeLabel");
			ivjPeakStopTimeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeakStopTimeLabel.setText("Peak Stop Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakStopTimeLabel;
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
	com.cannontech.database.data.capcontrol.CapControlSubBus subBus = ((com.cannontech.database.data.capcontrol.CapControlSubBus) val);

	subBus.getCapControlSubstationBus().setPeakSetPoint( 
		stringToDouble(getPeakSetPointTextField().getText()) );
	
	subBus.getCapControlSubstationBus().setOffPeakSetPoint(
		stringToDouble(getOffPeakSetPointTextField().getText()) );

	subBus.getCapControlSubstationBus().setUpperBandwidth(
		stringToDouble(getJTextFieldUpperBandwidth().getText()) );

	subBus.getCapControlSubstationBus().setLowerBandwidth(
		stringToDouble(getJTextFieldLowerBandwidth().getText()) );

		
	subBus.getCapControlSubstationBus().setPeakStartTime( getJTextFieldStartTime().getTimeTotalSeconds() );
	
	subBus.getCapControlSubstationBus().setPeakStopTime( getJTextFieldStopTime().getTimeTotalSeconds() );

	subBus.getCapControlSubstationBus().setControlUnits( getJComboBoxControlUnits().getSelectedItem().toString() );

	return val;
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
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPeakSetPointTextField().addCaretListener(this);
	getOffPeakSetPointTextField().addCaretListener(this);
	getJTextFieldUpperBandwidth().addCaretListener(this);
	getJTextFieldStartTime().addActionListener(this);
	getJTextFieldStopTime().addActionListener(this);
	getJTextFieldLowerBandwidth().addCaretListener(this);
	getJComboBoxControlUnits().addActionListener(this);
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
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 244);

		java.awt.GridBagConstraints constraintsPeakSetPointLabel = new java.awt.GridBagConstraints();
		constraintsPeakSetPointLabel.gridx = 1; constraintsPeakSetPointLabel.gridy = 1;
		constraintsPeakSetPointLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPeakSetPointLabel.ipadx = 51;
		constraintsPeakSetPointLabel.insets = new java.awt.Insets(10, 11, 6, 5);
		add(getPeakSetPointLabel(), constraintsPeakSetPointLabel);

		java.awt.GridBagConstraints constraintsPeakSetPointTextField = new java.awt.GridBagConstraints();
		constraintsPeakSetPointTextField.gridx = 2; constraintsPeakSetPointTextField.gridy = 1;
		constraintsPeakSetPointTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPeakSetPointTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPeakSetPointTextField.weightx = 1.0;
		constraintsPeakSetPointTextField.ipadx = 78;
		constraintsPeakSetPointTextField.insets = new java.awt.Insets(8, 5, 4, 4);
		add(getPeakSetPointTextField(), constraintsPeakSetPointTextField);

		java.awt.GridBagConstraints constraintsOffPeakSetPointLabel = new java.awt.GridBagConstraints();
		constraintsOffPeakSetPointLabel.gridx = 1; constraintsOffPeakSetPointLabel.gridy = 2;
		constraintsOffPeakSetPointLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsOffPeakSetPointLabel.insets = new java.awt.Insets(7, 11, 6, 33);
		add(getOffPeakSetPointLabel(), constraintsOffPeakSetPointLabel);

		java.awt.GridBagConstraints constraintsOffPeakSetPointTextField = new java.awt.GridBagConstraints();
		constraintsOffPeakSetPointTextField.gridx = 2; constraintsOffPeakSetPointTextField.gridy = 2;
		constraintsOffPeakSetPointTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsOffPeakSetPointTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsOffPeakSetPointTextField.weightx = 1.0;
		constraintsOffPeakSetPointTextField.ipadx = 78;
		constraintsOffPeakSetPointTextField.insets = new java.awt.Insets(5, 5, 4, 4);
		add(getOffPeakSetPointTextField(), constraintsOffPeakSetPointTextField);

		java.awt.GridBagConstraints constraintsPeakStartTimeLabel = new java.awt.GridBagConstraints();
		constraintsPeakStartTimeLabel.gridx = 1; constraintsPeakStartTimeLabel.gridy = 3;
		constraintsPeakStartTimeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPeakStartTimeLabel.insets = new java.awt.Insets(7, 11, 7, 48);
		add(getPeakStartTimeLabel(), constraintsPeakStartTimeLabel);

		java.awt.GridBagConstraints constraintsPeakStopTimeLabel = new java.awt.GridBagConstraints();
		constraintsPeakStopTimeLabel.gridx = 1; constraintsPeakStopTimeLabel.gridy = 4;
		constraintsPeakStopTimeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPeakStopTimeLabel.insets = new java.awt.Insets(7, 11, 4, 49);
		add(getPeakStopTimeLabel(), constraintsPeakStopTimeLabel);

		java.awt.GridBagConstraints constraintsJLabelFormatTime = new java.awt.GridBagConstraints();
		constraintsJLabelFormatTime.gridx = 3; constraintsJLabelFormatTime.gridy = 3;
		constraintsJLabelFormatTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFormatTime.ipadx = 3;
		constraintsJLabelFormatTime.insets = new java.awt.Insets(11, 4, 6, 49);
		add(getJLabelFormatTime(), constraintsJLabelFormatTime);

		java.awt.GridBagConstraints constraintsJLabelFormatTime2 = new java.awt.GridBagConstraints();
		constraintsJLabelFormatTime2.gridx = 3; constraintsJLabelFormatTime2.gridy = 4;
		constraintsJLabelFormatTime2.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFormatTime2.ipadx = 3;
		constraintsJLabelFormatTime2.insets = new java.awt.Insets(9, 4, 5, 49);
		add(getJLabelFormatTime2(), constraintsJLabelFormatTime2);

		java.awt.GridBagConstraints constraintsJLabelUpperBandwidth = new java.awt.GridBagConstraints();
		constraintsJLabelUpperBandwidth.gridx = 1; constraintsJLabelUpperBandwidth.gridy = 5;
		constraintsJLabelUpperBandwidth.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelUpperBandwidth.ipadx = 21;
		constraintsJLabelUpperBandwidth.insets = new java.awt.Insets(7, 11, 4, 21);
		add(getJLabelUpperBandwidth(), constraintsJLabelUpperBandwidth);

		java.awt.GridBagConstraints constraintsJTextFieldUpperBandwidth = new java.awt.GridBagConstraints();
		constraintsJTextFieldUpperBandwidth.gridx = 2; constraintsJTextFieldUpperBandwidth.gridy = 5;
		constraintsJTextFieldUpperBandwidth.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldUpperBandwidth.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldUpperBandwidth.weightx = 1.0;
		constraintsJTextFieldUpperBandwidth.ipadx = 78;
		constraintsJTextFieldUpperBandwidth.insets = new java.awt.Insets(3, 5, 4, 4);
		add(getJTextFieldUpperBandwidth(), constraintsJTextFieldUpperBandwidth);

		java.awt.GridBagConstraints constraintsJTextFieldStartTime = new java.awt.GridBagConstraints();
		constraintsJTextFieldStartTime.gridx = 2; constraintsJTextFieldStartTime.gridy = 3;
		constraintsJTextFieldStartTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStartTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldStartTime.weightx = 1.0;
		constraintsJTextFieldStartTime.ipadx = 78;
		constraintsJTextFieldStartTime.ipady = 3;
		constraintsJTextFieldStartTime.insets = new java.awt.Insets(5, 5, 5, 4);
		add(getJTextFieldStartTime(), constraintsJTextFieldStartTime);

		java.awt.GridBagConstraints constraintsJTextFieldStopTime = new java.awt.GridBagConstraints();
		constraintsJTextFieldStopTime.gridx = 2; constraintsJTextFieldStopTime.gridy = 4;
		constraintsJTextFieldStopTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldStopTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldStopTime.weightx = 1.0;
		constraintsJTextFieldStopTime.ipadx = 78;
		constraintsJTextFieldStopTime.ipady = 3;
		constraintsJTextFieldStopTime.insets = new java.awt.Insets(5, 5, 2, 4);
		add(getJTextFieldStopTime(), constraintsJTextFieldStopTime);

		java.awt.GridBagConstraints constraintsJTextFieldLowerBandwidth = new java.awt.GridBagConstraints();
		constraintsJTextFieldLowerBandwidth.gridx = 2; constraintsJTextFieldLowerBandwidth.gridy = 6;
		constraintsJTextFieldLowerBandwidth.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldLowerBandwidth.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldLowerBandwidth.weightx = 1.0;
		constraintsJTextFieldLowerBandwidth.ipadx = 78;
		constraintsJTextFieldLowerBandwidth.insets = new java.awt.Insets(5, 5, 4, 4);
		add(getJTextFieldLowerBandwidth(), constraintsJTextFieldLowerBandwidth);

		java.awt.GridBagConstraints constraintsJLabelLowerBandwidth = new java.awt.GridBagConstraints();
		constraintsJLabelLowerBandwidth.gridx = 1; constraintsJLabelLowerBandwidth.gridy = 6;
		constraintsJLabelLowerBandwidth.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelLowerBandwidth.ipadx = 21;
		constraintsJLabelLowerBandwidth.insets = new java.awt.Insets(9, 11, 4, 21);
		add(getJLabelLowerBandwidth(), constraintsJLabelLowerBandwidth);

		java.awt.GridBagConstraints constraintsJLabelControlUnits = new java.awt.GridBagConstraints();
		constraintsJLabelControlUnits.gridx = 1; constraintsJLabelControlUnits.gridy = 7;
		constraintsJLabelControlUnits.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelControlUnits.ipadx = 47;
		constraintsJLabelControlUnits.insets = new java.awt.Insets(6, 11, 27, 21);
		add(getJLabelControlUnits(), constraintsJLabelControlUnits);

		java.awt.GridBagConstraints constraintsJComboBoxControlUnits = new java.awt.GridBagConstraints();
		constraintsJComboBoxControlUnits.gridx = 2; constraintsJComboBoxControlUnits.gridy = 7;
		constraintsJComboBoxControlUnits.gridwidth = 2;
		constraintsJComboBoxControlUnits.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxControlUnits.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxControlUnits.weightx = 1.0;
		constraintsJComboBoxControlUnits.ipadx = 4;
		constraintsJComboBoxControlUnits.insets = new java.awt.Insets(4, 5, 25, 51);
		add(getJComboBoxControlUnits(), constraintsJComboBoxControlUnits);
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

	if( getJTextFieldStartTime().getTimeTotalSeconds().intValue() >
		 getJTextFieldStopTime().getTimeTotalSeconds().intValue() )
	{		
		setErrorString("The peak start time must be before the peak stop time");
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
		CCSubstationBusPeakSettingsPanel aCCSubstationBusPeakSettingsPanel;
		aCCSubstationBusPeakSettingsPanel = new CCSubstationBusPeakSettingsPanel();
		frame.setContentPane(aCCSubstationBusPeakSettingsPanel);
		frame.setSize(aCCSubstationBusPeakSettingsPanel.getSize());
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
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