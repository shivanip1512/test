package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
public class DatabaseAccessPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener {
	private javax.swing.JLabel ivjUsernameLabel = null;
	private javax.swing.JButton ivjChangeURLButton = null;
	private javax.swing.JButton ivjConnectButton = null;
	private javax.swing.JLabel ivjDatabaseURLField = null;
	private javax.swing.JLabel ivjDatabaseURLLabel = null;
	private javax.swing.JComboBox ivjDriverComboBox = null;
	private javax.swing.JLabel ivjDriverLabel = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTextPane ivjOutputTextPane = null;
	private javax.swing.JPasswordField ivjPasswordField = null;
	private javax.swing.JLabel ivjPasswordLabel = null;
	private javax.swing.JCheckBox ivjSaveCheckBox = null;
	private javax.swing.JTextField ivjUsernameTextField = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DatabaseAccessPanel() {
	super();
	initialize();
}
/**
 * DatabaseAccessPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public DatabaseAccessPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * DatabaseAccessPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public DatabaseAccessPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * DatabaseAccessPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public DatabaseAccessPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getConnectButton()) 
		connEtoC3(e);
	if (e.getSource() == getChangeURLButton()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void changeURLButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * Comment
 */
public void connectButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * connEtoC1:  (ChangeURLButton.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseAccessPanel.changeURLButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.changeURLButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (SaveCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DatabaseAccessPanel.saveCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.saveCheckBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ConnectButton.action.actionPerformed(java.awt.event.ActionEvent) --> DatabaseAccessPanel.connectButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.connectButton_ActionPerformed(arg1);
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
	D0CB838494G88G88G69EDFEA7GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8DF4D4D71935255D76AC2B7DC9BDEC0B3B31F6B5D5EC699E2C286876D83AD297F753AD9ED1E3C54D565A03AB2E9C8FFA3AFAB379257C85C8C2D2C2FEA001A48D138924A58D8172B3799F5BECB3C0DAA9494B4CCBB2B019194E3C4084036C775D7B3E7BDEA6EFC24063B94AB9DF5F3C6F7FFE775E6FFE775E17323C52444CB4DB9EE3E919CC7FF7AC0D310DCE46BEB53060BE42656426581869DFB940
	CED6F6BE831E05C02BF02758F2D94A4E9CF82F0577643E945B9E783D1B7575ACBA8EBF625889816D2353797FBB371D47CF101DEC7E7E33342D705C8E60G9CBE27F351FE362DD0473BF5BCC19AE8BD3793F9765B0AF55C1340339620907035BB853F931E1B39658F5EEE5479D6645C4F3CC7C2D95BD00FC9A741433A6CAA7D891EE605F6212DBE5BD6DD0641FBA3GB6BE1DFD2D259E1E4D7D9747CEFA0293F60F928884831A6A99370FCD7A6C131A4F5F5464D4B4E5C409282D9E0F9A0974AB81558FB26B395E7B
	ECB9DC8FE34B60BD3A9BF11706511683FEF7838C6FA67C898571AB60779A00753B29FDBFBC552C7B6F9F7FA4CB2D7BE3560EB86D4B48936D2A4DB35A376E76165DC53CAFFEFE92E3BE8E3405G09GAB81D281B272D3ECFB799858F422861E2DA3DAFBF0E2A298E88D055A7C5E706F02DE558F3F1B1A0022635ED1BD413017B1514F7FE72B3570E7AE90F2C3B7ACA7E212F4147AFC116DC136690B93D95996B1892D184AAA4E9731E848B7E2A2ECAF0AEB5B1DBFF3CC4E34FD1619EDC7CB6F4A4A34306D9CBD1FB5
	2E6B19B659DE120FFD7D929F830B61F71ABEBE84FEA347EFB06145781C66F8FCE785A9B6EF810D0D04AA72E56B3D773045E3BE4BB874FF6D762CD5854207F585313EBC4E475D56BD42E63674654B9CDFE1428BDF5AB99E5F1B011688A046D7E6AB4E570C4294DB99C0A3C0BFC088E0A1C0E2A10D315613F5739CE37DCAD8EB1B74793DEA188959DF1FAC00279A5542CACFA022A9810FFAC6897B14913F9AE1BACF7AA974EDE406DC4EA99C7B9E30512586543022790281502ECC281A9A0E881E1D27F6A2CFD865D4
	37EFC2858393A1C6FA0BCE6D46B928C43453A12F2229860720171F42BE39241C810D20GFE0B389CE943F8BD896DDF8B70220C43E22E2FCF8DC342E8EAF2850397A7C3EEEC89EBAC42B8BF46F3474B401FD7C4B13637A3EE9A5E930AC84F7327F61974B8FD9EF4DF89CF519C5BB00CB9B92908464CFF8F52185974DF8F32D5CFDC309CB3BCCF9669E326A8F66E7C1A6BC43F447B2D4303F34C5924A26B79F4E3396241B9ED247F897A374B3EDA6C443E4AF99556C9G672B94632216B2B1E65BB0516B23746DB08C
	B055EBC4DC7879F90566EA262ED7601F6278F320F3814012D7A9F6BD4A20298D19B7606F7CE3B7F2D3B6326749A7E234239557A575129A501CEAC88DF861C7C4279534625853676DE8D04A94F3FC8866F9BBCC2C08446FEF453928E8DA58B7B22929832A267982E3863D065B0ACC0E040C94C1366ADB31FD414BB04FA57F9B2DB8C7BC41C0C0651DA5C5F4FAA31F232ADF1DG275B031381CD60CF3661B83F28CE69B9222DAD0B619C77CFE23E91B542D2E58B36A16C1B00A100A90BD56C45797B2F3246C1D8837D
	0D354EA6F8EE5DCBF37172811A0B5EB39FE50E2B6FD92EA5F76F3CAD4B3FD74C21DB769AF3D178F4345D5A77B7F92E507191616BC35318F32CAA96B17E3F59013A924146B20055FBE97CFF2E9D6914E374015F9E747BC5B8E51C9FF266E23F289FCC2A101AB119754958BD625C95C3BB2347D1509FF7E29C7D26BC18D80CF17C25C2F5E0AA7873B2C0F1310865E0310805607B4288712D07770CFDB0077789BE6FBEB35FD58F71AD817CCA93DF2A4EA7E2750BEA6CBFDE7BDDD0AEA9D1FB64B20C412626DE9EB0D1
	5FB952F3F4FEC78955F13DD99843F1F83F85E0D1094C3B3F23BAEE78278F31326727625629CBCB049FEBCA0C3EDE7DG55F125DDE8B32F947CAE257E296DB02FA52D11C85063634B189E5B6FF6184702402D6BF8851E233EF0C46B01499A25FEF821837D45F5CEC76B6BFE6948D63D2D0DE0B78410DA0A786D5772CC3EA7150A75AA991E69DC5F9DA3385E7BB4F5420E7F9950E06DEC9FD782E32A378A83EBD7AEEBF61EB66CBDC06F40DF55FA9F243C5204E3C63984922A67E2DBB0BA54932B4395CF49716AB3D4
	33317E966AF3674758E0CD503206DE6D9B4F2AAD93B1F71799312FDD7C8F854947DBF70BB87CD046A191F85616D19CBEFB5E9C075CF2910746F20AC30F678866B4BEE16C62512F06C70361095901E865E4BD94FAAC6EE24DC68EEB1B5434E0E028B5C6CDECACCCFAA09A427FCFFA48FF977836201C7C7F78B5AB7FD71453DEE8B4F2926D73281FFE47F5D39E50FA74FAFAEC6D1DDCEE54882965336B85D1133E120D3EEFAA97B5E9114417F2FC43FE83AF5A5A4F67A2FF87DA73812A239E6D46391DD381F3882099
	408D2081A4D4501C8AF49EE55729D3FBE0AF988EA87E01998B11BE0FAA3A305FE16D505F2B3AB00EFC6DE094074A1158B8AC2D18597655B16FC26E68AC391218778DB1F2228FCF1E59EB1D53E8FA697B560EEB1457F2AAA9FFBDF0441C3F264F1D4B5A92A7FF35D48A5B4315469CAA3E037257E0AF665FC4202DGD8833081A06FE00A4D8160BEC8717F672E93570BBF9FD66EE0BB603AE15DE80FDD5FE53CDE3C49B87F723AF1568EDA45F947C011F51C452CE1F4BE10FABEC7373570A05503CBBCB7676B97E749
	457A3AB246D76143DA5957A9C097FB8F97DF57728E25582A8E91EECFA662DC706EBDC43E66E6BB60F9A73CBF8C3092E09D404BGF9D5501FGAE00F10085G0B8116D711ECA517CD06778DG3955408F508F9082389BE0E935D81FF14D4775BC391AE44FE4BDE9B16E668274E7C6ACF4DF5177F15D5785D533738E368B1F58266596F4B3382EC3A7D82C7309365CF543ED5996730EFAB6B3D15BE39D00EBDB3E059C557DB8EF2F37FF58B9CFDF7668CFB1E7BF5F9DFF8F7015EE6BBD404AEE6BBD40573BF78ABC68
	B01F84907DBF3B675EA3FC275BEA0F300F476919029DDCF7A422C6F8924112D8277DD843584D5AAB3C25CC28B24667C6B66BBC9DDA307D086C4B5EEBD6E7CA9B2BC5FF67544C9E9FA2A73E5407EBEFB1509B817A8146816E2C21DC986A396EDA54911DE3AD9A690D5FAF3EDE6BFE897662B80B84A7439EB5B67617FA8BA54D483AD8F9E118844FB5AE2FA961B1F5D65F5D416585AD1E7CC7FA63752F6803D20F398F0E4ABE7849F9F39FA469B1DF21BF13AD7AC0687B518CFD75D25F77AC7BB4D55733A92EBECFBA
	6E957D4AD4F0D2E3AC7A9A6650AB6934A75C8A6F95GAE0061574C7B0E47AF637C9C8DAB93505C01106AE16EDA1435E7126C13403F86E0A3C09E509AEA29467C9B9F463C9F467565E0585B69D37DDE694F2A9A0C6769880E1289558D9DBE133E25EA1B096ED2C678B93620FDED5C2CF7A66DD0857A6B8C7BAE29E1C191782FEF59AE71B0ECC702D8DE683432865CA752BE942AC9AEA8E84F544650381B02761BEBB80E5BE756203A4E7CEA2C7586CCFB8501DFDD073E371B6BE6234DDBD026F74093063DAC6E6B6A
	C5AD2CE41BEB6145352296DED5CB3532AFEA262768740C43C4E7C5E6FA61E1C1EF16741AEBD674FEC9AFF2DB513D127E59BEB3BD2A5393A5BDB4E9A53FCC521F1F36222F0227B83B780FD366330B941D1EE3073D371D6A8105578D8D706E14382FDE239A215FCE396BFF7A62671EDF75D9671E5F76D92F89192E395704ED2EF8B9C3784706503FC470ED297479559AEA47CA097BC38D35A3D962B64A761655916E25C344D78BB83167F8A81D6E122681372C0E76CF7543D8774A1A9077C8CDCDDDE11F37CD99F371
	ACA16A3117F86D05726B6B68CC21B0031F45BBE08C81389DD413DFC9271A7C656ACFB0FF562D16E78A45DF8AE5799DA2475C6DB01DA95C5E32DB58F4E7124D55C04F28C75C2D1966B1027365FD9BE3F6F88A7A3299F40C7BD34CCCFFE1869D7D3D1AEE2697E61969982357F4F9513657FECFEDF33F71A9D6F55BE85C7392D73DE8D3C8FFBE3C7D594C245E4F940838BFD1D246621C63185224084322EC2A47136A295FAA2B0A2D65E58A953209D12A2357C959DF0C46B9C36279CE489DABA21B05C7D2EC82A76696
	627AA52E5FCD38BB0F107EB1FF3C7D9CA5DCFD1F51C73EBDA975FDEA1274ED12387C2B04F39C15B6BA76D95BB032A6233DC4F68B5999BFCAE37778F9FE26886F4B81120F52DA7555ABF8362AC190DFBC0AF749AF3D033DBE5C00B25F3E66547B3636E1767A1A460CFA972185F8908E68B4F703B88720BB345D6C5F325156E8B02025F27D55BC8FA4416FE5G427ED7878C7B7756107D2A572D5777AD967673F51B4D2F9B76056E475C066E34AA525DED21BBD30FFFBC5D67CC3AC5FD12B46BAC65FAA0641E38C939
	1F5D301CFE6749F3583849FF017F8547EBA63C4823A59914C7EF81DA6A6BF4F6F36FE91CD35DEF40FA88B08D10G308CA0798DDAF39EF5615EF24EFA599D8C39E0D1721B1691513EF7B3EF6ECEBA5BCD79AA6131FB59207F7D3867A329EF08FE4FB8A61E8E76ECA6BEC57BAA4FE27B9A00568F2081AC84C884D8FE0C5A77CBD72DE8DF07572735EA1A629967A73B5438B3BE75320146A2D2346DE35966362DBEB6F37C2D0BF95FF80C765B7F941DABA6B90DB3650A9BC92E3948AC6750791C7AF330F1767897F223
	B3EAEE3F4EF70B7ECC08ABF75ABD23765779D6684F6438F2BF69A339D48B1ECDF16516CC1A6DEDFDD37015694F06B763493DFA55AC572D731D531F2138F2DBDBC8EE01854FA28B1C18E30F719C3C644D586F8D3E4071AB5E0C7D5E2075BC5DE12E861A23096A0D3AC67337B64B763D1FC5F3B656EEC213FE6752E4549BE20DF805519A3186E825CC1CD964B0631F3E9E601525BE370AA3B43756F77DBD73FEAF1AF5A00E5D2D4775D89EB76CEE17BA573E4F982D895BEB7643B320DEAE13C7561D30C0D46D777A94
	FFF0ECFFED1923362C3E211AFC7881B3371D2D385B722E0E37DD371D24BFB76DFEB6D3347D299AD9A39C27F5A7FD88358B7A11AA2297B6937D3B1C2E5F65E7983FFFE57ADD11B1575D781F8A424609BF2B8D11579EBC9DFF2F7050E96B3DC252E96B3D42232767BE9FFA62F47CBB64AF1FB65FA10B3C7C44B0562B7D50AFFE003B819E86D85ECCB9396F8C0EA77CFAE5F6BE6E700E61B989FE312211FD7B5978EDFD7D6C753FF3B9F9966F6FC60230211DC07D04F71E457E5202A10E94381E33982330EFECDC932C
	BA7E6D33B85E7C6AA8C70BBD4D32EC5AA32DE926FDCED7AE6DF3DE14386D3C5EEDBC11E2F31DA05C0AA8718DCB5C278B09EF29448D94110D15927719AB343FDAABF11726C9DF2A44DD3DC6328DA7255D9E126D1638FD3DA46B16B83F1BE49F16384FF5116CF209AB68A659159247DC7CCC7C946443D304FB2504781C927754A459C81038B7AB49C61244BDD0C9324BA42E7EAA49E634902E380AE48BA56EEA9549D6C85C019672CF13384E82623B330556644F71EFC1574A77FF616F9B65FB5779794EF354F1F339
	E4F85EB6EE663B9EB1DF1FD3189C675FBFDB894FB34A853D924432A9982659598B797F6D118B2AC7F3ABE352479D4C3CC6673526582ADA67EE2F307B1B912C6F1B01F790E09AA081A08960C900B5GAF823C8C105716E22B85E881F0830C83C4819682A436D1DE29FB37BA1E6F00548F90F8F289ABBECCAF3A9FDEAF767DB25013ACF56DBB7F5A9C3A7A4341109A56268416FF1D2147E2D50DB9969BC0DFC67B5C3190F2472A4CF205A0D3FB9DB97D2EE0B8FEEEFCFA787A39715F0779778E10535CD3A17DBC7939
	E1BC0F8D0E0EC6D41903578E67CADCCF24BF987161B93458A37E00674196705785A076EE67F3E96FF60BC5BB52E3403CF74375A421BDF6DFDA9945B8F9027E4909C004893B4DBC0722CC22347BDC146CAE0DE3B7ED8E3BABCDF6050D678A49C6B25084EE1A6764F5703E3E1DEA0F7BF12950772DFFB7CDBE94BB2D7764D6BE9C50FDF0B8E3FD78A44F634D006FF6123D6828E12F21176CC56358330AB559DBB84B5E7DBC77AF827C4352DE6218E16FF0B759DB9347DEE69CFBFC1F6F0C6D5BBDAEBC2F8A0555D1B5
	9CD63D833EAB2A7465B8DFCBB6028C6B20BEAEAF11E79E9D56FDECE51F7A3825A3363D6B79BA64827CE087357736F1233D0FD7123DBBAD6C5179C23CF30845336C3D47572EA440AF1376FE6FB36CA548F30F54B86D1B4B5E26D976DCFC3D4B68045F1D9443778B48C6F327F51B36196C18ED60FBFFE7EC9F5EDA0EF2EFCFEA21C98D0F7B44E117B8C35FB063FB1FF31D62FEFAC1A72D3D87791EE6913CE32DAAF02FF15CD24061FD1548C13BBC71F3DD11677A39EE3F473A8E2C724C2E838FFB2C6B40239EF39D28
	FF936941F1B5866B4B2064BBA1F16FCADC9B57093833BE2FB6AE715D5CAF44F72BE81070EFF13FAEF35C200E9BB0615E55F1039E4C57506471E018E45F7770F3BC1F5FAF5E954E638BC07A16719865BE7AC20A496F0B92E7786D73E23F497D01D86EF4DA481B477542F22078FDD77827F483409898937D787D9A6A5B47E45F2656D05F9E1338372A086FB392B7D0E59E83E21E5CF31149B157140B350A453EB42284A291B56C9BB5EDCD75FD63C7F2E95F383A13665E8381C367C0947FD8275EC17C99D3441F51C5
	7C6120417FD2A1718B5A7887866D9F2F4CF75FACECFDE51AECB5CBDBAD382D52F537EA066E25BDB726DB6DA55D1A54FDF852503D7032217B112E9B53FD3E1BF4AF113A3DD1C37737268C5D3F5DD79A2B5B69039DA07EA1C0D5FDBDA8BE820A055EF7CAC8EF2A547B45AB065EAFCD9BFA8FD75E184FBF2CA45D455D247B3E2B066E3B2E993AFBEFD0775372EC42ADF5EF9FB1F47FFC4450FD3C20ACFEBC0EBAD03143D1AD74FE380074AE14FA1F71B0C66B5D05BF4439631089D968B9B4C4FFEB35321B6EAE1403DB
	0DF94C53134097C73799F8CACFFA4D1A10EB2ED9DF86DD39BD7329F5B7CF1965AAC026F1DEF2CFCD1B65DCA0B3BCAF398B3DE6B98DE46E1C171C275BAC3798E4164ECB6EEC09D9EEA5482C1D175C0FAB4DF26BC106754EC76E6BB3768EF9A0D3B5AF397B8A4CF24DA053BFAF395A21394E383D96BA74F30BDC4A711BA5EEE9AE6578C392D7B6B51B2FF2EAB65FF726096F9E097B7EB47115CB5C07FA096FBE093B23F7B65F15EE62D3A56EDA77EC3EC08971E52691AED2B2FBCD7BCDA571BDABF16987E76B7B01DC
	7BFEAFF1AF48352FD462BEDDC0FC599277D88171993876A1BA4B08C2DF08EFE25E9B226F949683EEA9C0B24086005CBE3A533CE5086E369DFDF4377D68D06C5DB65600F6C6F7536B06E26F3653E650F78D475EED4F14F78E475EED133C98EB374FB9562CC07C0D6EB75E43432AA5BFE5199A5B061F3A14513126463275CFDCB436D9FCA0E3375BA3E136C3E6609EDEA62D3BF434A97DC71FC837310C9E16A54A469E160135DF8F4B6425E18FCB0732B19A52DD177FC05B3699EB45539FFF7D05554F1F0924332C9E
	D604572B83D01FE04524FA71839E7DCF61BA5966D05087C440032988FFE4C634A93F6AE2F9236F601F04C4B4555BCA359834A61FFFC8DE07FFE2DB572DC446B52CF90F3BD886FE1C2431DD026C07F65609426CB877B72DF5183B3ADD77F7B77797355825165DDF5AD1D95E1B3ABC1D59BA59AB7E20626DD4BCDAB05C43B6EB63E1B5B29E74FB2321D3ADDCD14EE59CBD7C3697546D154D47465AF18512CEDF0CFEEC61369737F562B6B64C6AB8A75EEE5BA507977AA58C2654F00F06FF04988CD7C85A07CA34035F
	685AF34E465274C6FE407E483223A10DE50DAA93BE7F947C90ABF384A9DB201BE1D7900860F77760D9D90CE7C412866C77BD63387F11DF3F478D98229AAB952184513AB1B5B8D157BABA6A8B28D2814495546F136A11479E4333ABFACFFE6FB7FFBB8263404952A79C8E7E5FFA7E5FA3785FFA0E2967187AA3905C8DEC4EFF3174A835198F9EB92A317B6D1D50035AF7FE7A4CC35FED5E309EDA5749B279C7765089398657DB1393A3EA780EA5776F5B12306EEF310FC2B8D42228F707D00FE9A0CF87B15E890B33
	E087E1B53695BF12AB43BEEAAAAB5358967A10043FED96DF3C715FBB68AB3632536F38B8C6684C1CB5D2913BF97610C374B650A9CBFFC464F26EDD7C3F79B79CC9145A43E50BE4GAA2356CD1D83FB23CD50476E1F477F43B37D177F7E72BCED782B71743861690CFFB079F64DAE6ADBBF7C6DEFAD99F26659188DD615F1D5ACA3191731646609995769408C0DFF416329632F2617DCF34DDC74488B4E56DA7724FE2F3D2243FC2FDD1C77C15CFB6DF1377E7FB3F09BF76B622EE18F2F311683FE0D1B6E9A464E9F
	E6716FDC86A6943FDFD37C97DB83DE3CFFE91B52D463EE36F88875C708C7A78A5A01A1747B02C9667F81D0CB87887A86DC2BF097GG9CC5GGD0CB818294G94G88G88G69EDFEA77A86DC2BF097GG9CC5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2A97GGGG
**end of data**/
}
/**
 * Return the ChangeURLButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getChangeURLButton() {
	if (ivjChangeURLButton == null) {
		try {
			ivjChangeURLButton = new javax.swing.JButton();
			ivjChangeURLButton.setName("ChangeURLButton");
			ivjChangeURLButton.setText("Change...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChangeURLButton;
}
/**
 * Return the ConnectButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getConnectButton() {
	if (ivjConnectButton == null) {
		try {
			ivjConnectButton = new javax.swing.JButton();
			ivjConnectButton.setName("ConnectButton");
			ivjConnectButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjConnectButton.setText("Connect");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConnectButton;
}
/**
 * Return the DatabaseURLField property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDatabaseURLField() {
	if (ivjDatabaseURLField == null) {
		try {
			ivjDatabaseURLField = new javax.swing.JLabel();
			ivjDatabaseURLField.setName("DatabaseURLField");
			ivjDatabaseURLField.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDatabaseURLField.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDatabaseURLField;
}
/**
 * Return the DatabaseURLLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDatabaseURLLabel() {
	if (ivjDatabaseURLLabel == null) {
		try {
			ivjDatabaseURLLabel = new javax.swing.JLabel();
			ivjDatabaseURLLabel.setName("DatabaseURLLabel");
			ivjDatabaseURLLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDatabaseURLLabel.setText("Database URL:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDatabaseURLLabel;
}
/**
 * Return the DriverComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getDriverComboBox() {
	if (ivjDriverComboBox == null) {
		try {
			ivjDriverComboBox = new javax.swing.JComboBox();
			ivjDriverComboBox.setName("DriverComboBox");
			ivjDriverComboBox.setPreferredSize(new java.awt.Dimension(110, 24));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDriverComboBox;
}
/**
 * Return the DriverLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDriverLabel() {
	if (ivjDriverLabel == null) {
		try {
			ivjDriverLabel = new javax.swing.JLabel();
			ivjDriverLabel.setName("DriverLabel");
			ivjDriverLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDriverLabel.setText("Driver:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDriverLabel;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setPreferredSize(new java.awt.Dimension(300, 100));
			getJScrollPane1().setViewportView(getOutputTextPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the OutputTextPane property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getOutputTextPane() {
	if (ivjOutputTextPane == null) {
		try {
			ivjOutputTextPane = new javax.swing.JTextPane();
			ivjOutputTextPane.setName("OutputTextPane");
			ivjOutputTextPane.setBounds(0, 0, 10, 10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputTextPane;
}
/**
 * Return the JPasswordField1 property value.
 * @return javax.swing.JPasswordField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPasswordField getPasswordField() {
	if (ivjPasswordField == null) {
		try {
			ivjPasswordField = new javax.swing.JPasswordField();
			ivjPasswordField.setName("PasswordField");
			ivjPasswordField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPasswordField.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordField;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPasswordLabel() {
	if (ivjPasswordLabel == null) {
		try {
			ivjPasswordLabel = new javax.swing.JLabel();
			ivjPasswordLabel.setName("PasswordLabel");
			ivjPasswordLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPasswordLabel.setText("Password:  ");
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
 * Return the SaveCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getSaveCheckBox() {
	if (ivjSaveCheckBox == null) {
		try {
			ivjSaveCheckBox = new javax.swing.JCheckBox();
			ivjSaveCheckBox.setName("SaveCheckBox");
			ivjSaveCheckBox.setText("Save Settings");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSaveCheckBox;
}
/**
 * Return the UsernameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUsernameLabel() {
	if (ivjUsernameLabel == null) {
		try {
			ivjUsernameLabel = new javax.swing.JLabel();
			ivjUsernameLabel.setName("UsernameLabel");
			ivjUsernameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUsernameLabel.setText("User name:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsernameLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getUsernameTextField() {
	if (ivjUsernameTextField == null) {
		try {
			ivjUsernameTextField = new javax.swing.JTextField();
			ivjUsernameTextField.setName("UsernameTextField");
			ivjUsernameTextField.setColumns(10);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsernameTextField;
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
	getSaveCheckBox().addItemListener(this);
	getConnectButton().addActionListener(this);
	getChangeURLButton().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DatabaseAccessPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(446, 302);

		java.awt.GridBagConstraints constraintsPasswordField = new java.awt.GridBagConstraints();
		constraintsPasswordField.gridx = 1; constraintsPasswordField.gridy = 1;
		constraintsPasswordField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPasswordField.insets = new java.awt.Insets(0, 15, 5, 0);
		add(getPasswordField(), constraintsPasswordField);

		java.awt.GridBagConstraints constraintsUsernameTextField = new java.awt.GridBagConstraints();
		constraintsUsernameTextField.gridx = 1; constraintsUsernameTextField.gridy = 0;
		constraintsUsernameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUsernameTextField.insets = new java.awt.Insets(0, 15, 5, 0);
		add(getUsernameTextField(), constraintsUsernameTextField);

		java.awt.GridBagConstraints constraintsUsernameLabel = new java.awt.GridBagConstraints();
		constraintsUsernameLabel.gridx = 0; constraintsUsernameLabel.gridy = 0;
		constraintsUsernameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUsernameLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUsernameLabel.insets = new java.awt.Insets(0, 0, 5, 0);
		add(getUsernameLabel(), constraintsUsernameLabel);

		java.awt.GridBagConstraints constraintsPasswordLabel = new java.awt.GridBagConstraints();
		constraintsPasswordLabel.gridx = 0; constraintsPasswordLabel.gridy = 1;
		constraintsPasswordLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPasswordLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPasswordLabel.insets = new java.awt.Insets(0, 0, 5, 0);
		add(getPasswordLabel(), constraintsPasswordLabel);

		java.awt.GridBagConstraints constraintsDriverLabel = new java.awt.GridBagConstraints();
		constraintsDriverLabel.gridx = 0; constraintsDriverLabel.gridy = 2;
		constraintsDriverLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getDriverLabel(), constraintsDriverLabel);

		java.awt.GridBagConstraints constraintsDriverComboBox = new java.awt.GridBagConstraints();
		constraintsDriverComboBox.gridx = 1; constraintsDriverComboBox.gridy = 2;
		constraintsDriverComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDriverComboBox.insets = new java.awt.Insets(0, 15, 0, 0);
		add(getDriverComboBox(), constraintsDriverComboBox);

		java.awt.GridBagConstraints constraintsDatabaseURLLabel = new java.awt.GridBagConstraints();
		constraintsDatabaseURLLabel.gridx = 0; constraintsDatabaseURLLabel.gridy = 3;
		constraintsDatabaseURLLabel.insets = new java.awt.Insets(0, 0, 5, 0);
		add(getDatabaseURLLabel(), constraintsDatabaseURLLabel);

		java.awt.GridBagConstraints constraintsDatabaseURLField = new java.awt.GridBagConstraints();
		constraintsDatabaseURLField.gridx = 1; constraintsDatabaseURLField.gridy = 3;
		constraintsDatabaseURLField.insets = new java.awt.Insets(0, 15, 5, 0);
		add(getDatabaseURLField(), constraintsDatabaseURLField);

		java.awt.GridBagConstraints constraintsChangeURLButton = new java.awt.GridBagConstraints();
		constraintsChangeURLButton.gridx = 2; constraintsChangeURLButton.gridy = 3;
		constraintsChangeURLButton.insets = new java.awt.Insets(0, 15, 5, 0);
		add(getChangeURLButton(), constraintsChangeURLButton);

		java.awt.GridBagConstraints constraintsSaveCheckBox = new java.awt.GridBagConstraints();
		constraintsSaveCheckBox.gridx = 0; constraintsSaveCheckBox.gridy = 4;
		constraintsSaveCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsSaveCheckBox.insets = new java.awt.Insets(0, 0, 5, 0);
		add(getSaveCheckBox(), constraintsSaveCheckBox);

		java.awt.GridBagConstraints constraintsConnectButton = new java.awt.GridBagConstraints();
		constraintsConnectButton.gridx = 1; constraintsConnectButton.gridy = 5;
		constraintsConnectButton.insets = new java.awt.Insets(0, 20, 20, 0);
		add(getConnectButton(), constraintsConnectButton);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 6;
		constraintsJScrollPane1.gridwidth = 3;
		constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
		add(getJScrollPane1(), constraintsJScrollPane1);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getSaveCheckBox()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jButton1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * Comment
 */
public void jCheckBox1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		DatabaseAccessPanel aDatabaseAccessPanel;
		aDatabaseAccessPanel = new DatabaseAccessPanel();
		frame.add("Center", aDatabaseAccessPanel);
		frame.setSize(aDatabaseAccessPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void saveCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	return;
}
}
