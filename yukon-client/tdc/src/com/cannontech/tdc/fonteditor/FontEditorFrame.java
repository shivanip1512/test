package com.cannontech.tdc.fonteditor;

/**
 * Insert the type's description here.
 * Creation date: (2/7/00 12:46:20 PM)
 * @author: 
 */
public class FontEditorFrame extends javax.swing.JDialog 
{
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private com.cannontech.tdc.utils.FontEditorPanel ivjMainPanel = null;

class IvjEventHandler implements com.cannontech.tdc.utils.FontEditorPanelListener {
		public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == FontEditorFrame.this.getMainPanel()) 
				connEtoC1(newEvent);
		};
		public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == FontEditorFrame.this.getMainPanel()) 
				connEtoC2(newEvent);
		};
	};
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
/**
 * FontEditorFrame constructor comment.
 */
public FontEditorFrame() {
	super();
	initialize();
}
/**
 * FontEditorFrame constructor comment.
 * @param owner java.awt.Dialog
 */
public FontEditorFrame(java.awt.Dialog owner) {
	super(owner);
}
/**
 * FontEditorFrame constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public FontEditorFrame(java.awt.Dialog owner, String title) {
	super(owner, title);
}
/**
 * FontEditorFrame constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public FontEditorFrame(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * FontEditorFrame constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public FontEditorFrame(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}
/**
 * FontEditorFrame constructor comment.
 * @param owner java.awt.Frame
 */
public FontEditorFrame(java.awt.Frame owner) {
	super(owner);
	initialize();
}
/**
 * FontEditorFrame constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public FontEditorFrame(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * FontEditorFrame constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public FontEditorFrame(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * FontEditorFrame constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public FontEditorFrame(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * connEtoC1:  (MainPanel.fontEditorPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> FontEditorFrame.mainPanel_JButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.mainPanel_JButtonCancelAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (MainPanel.fontEditorPanel.JButtonOkAction_actionPerformed(java.util.EventObject) --> FontEditorFrame.mainPanel_JButtonOkAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.mainPanel_JButtonOkAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
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

			java.awt.GridBagConstraints constraintsMainPanel = new java.awt.GridBagConstraints();
			constraintsMainPanel.gridx = 1; constraintsMainPanel.gridy = 1;
			constraintsMainPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsMainPanel.weightx = 1.0;
			constraintsMainPanel.weighty = 1.0;
			constraintsMainPanel.ipadx = 26;
			constraintsMainPanel.ipady = 35;
			constraintsMainPanel.insets = new java.awt.Insets(5, 9, 6, 7);
			getJDialogContentPane().add(getMainPanel(), constraintsMainPanel);
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
 * Return the MainPanel property value.
 * @return com.cannontech.tdc.utils.FontEditorPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.tdc.utils.FontEditorPanel getMainPanel() {
	if (ivjMainPanel == null) {
		try {
			ivjMainPanel = new com.cannontech.tdc.utils.FontEditorPanel();
			ivjMainPanel.setName("MainPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMainPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/00 2:49:35 PM)
 * @return java.awt.Font
 */
public java.awt.Font getSelectedFont() {
	return getMainPanel().getSelectedFont();
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION FontEditorFrame() ---------");
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
	getMainPanel().addFontEditorPanelListener(ivjEventHandler);
}
/**
 * run method comment.
 */
public void initFontNames() 
{
	getMainPanel().initializeFontNames();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("FontEditorFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setSize(431, 270);
		setTitle("Font Editor");
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	initFontNames();
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		FontEditorFrame aFontEditorFrame;
		aFontEditorFrame = new FontEditorFrame();
		aFontEditorFrame.setModal(true);
		aFontEditorFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aFontEditorFrame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void mainPanel_JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{
	this.dispose();
	return;
}
/**
 * Comment
 */
public void mainPanel_JButtonOkAction_actionPerformed(java.util.EventObject newEvent) 
{
	this.setVisible( false );
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 11:00:27 AM)
 * Version: <version>
 * @param newFont java.awt.Font
 */
public void setSelectedFont(java.awt.Font newFont) 
{
	getMainPanel().setSelectedFont( newFont );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB2F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DAFDD0D45795A771A3A6F12636618F47B80DC9CC4AB4CCBE9AB3C9BBE9E308191AEA27C91A8F5BBAADB5E962E4EC47997D43C4A63D30302041089FC956C4AB297855A2D5C1BE1685970148173888C5ECED60317B969E3C5D373E77168FC16DB9773E7B76316CE2F48CB3E76E3B675EFB6E39677C6E39675E056C5C3BA0ABBDE3A7A169D94478ABCBA7E47D81C20EAC7A300573924494BB317CFD89340D
	7C521E89659CE81BFFF70ABD0FFCBD16837505D0BF8A754F60FB87710EA56C070FB873F4C1DB23B7B4E36AF96E790C4F13CD6BBFBF1E8B6547C0C94043F23D08737F61384360AFB7781C52B388F94D0FFDBEB83E5B6015C35D83B487E8211F75FF964ACDF466994EF2235F456FAEA06BFFF1B0E38B4A314864343B0F0DF59BE5BEF9ACFDF59F4E352FB85798B382754701F071B6E26BAF065265996C2B76AA01A22F908CAAC1DD74769769BEEF119F3FFD122E28CE67D770DDCF3F3FD20500C8FC7DA8F730B8074A
	A124906AEBA9EFC3A54E15845F0B01D6187C67DC48AF11D26CADC0BE092F2F6A204B503FEB6CB132684D20EDEB1C750DCBECDD8B25487A92439FE46F223E783583ED1E8AED1983A9F687108B68A250B8509794833F7D6C9FD056746A35CAA02084EBC221D332CFBD2378C4993E1DCEE8B1F867C42F227A88E1FEBEDCDC98C31F29080DAB3B61F14CA6C9455C6749679EA6AB1FEC4E480EE11315C9C7B3928718AD1287A2B6E1F307624E3DFCE0A2A6A74EFD1ED867DEB8D331E54518FB516B07B3D28DB96BADF397
	8C222FE56A6B1D701DEE60037107A83F5F42E7787C10E2866BA35036F210E3236BE32ECBCB66BC12F72CA5269D662654E438E526C3179C2DCBF1954A1EAE33B933CCDD2AA9FF2E054FF4F9116A0875E4E84B8B2458993E1CE538DFF5284F81CA82DA8C148A1486149764985B677857B74418C7D075D3E1C97609AAE1E3D7BABE05D29C51D521B12869C250ABF68A2AA4744A22C60CBEFFF2208DFAA70CFB0FE25F8BF31C960322AA681292846930D1F5D155D81FF50EED58C795E9EB079490E102C008F0399B9DBB
	201485CD7FF748A768E22487EB376DC21F8C891DA0918440B733CBDFB95A6BA82C3F81284B344355DDA82FD9D403226CF436A84AE0B85401ABA1C9C134739DB4F64870BDBD48ED6C2DC05E9A28E7AADC4ED33B36DB6454C9DED4DFD0C7799ED3DD98139D8A474CDF3AB8E66A36BCC48ADFEF0A0999249205E125C5095EBB63D4A66A456A192E2E9B5C478EA5769E0B9FAB765C709C369B684B6C7B0A93FD2503EEF3009EB7ED3CF6D7BE436CA9C193B9CA4F2AGB0519731B8533322AA4730274D543D0E7216023C
	358A3759B23A1E749B547346EC76B61D8365E7FD8BF3B05B541561F9A48E09C13DCE8C09C19FFCE8C6DBF399E24E582FFE459C53C679A1585F353021B413FF3E8C7720206B2A549B5645F6D15725E0DF243D1B4E2505FBC3115040677217619A15E1585FE67F41B25C9BDEA5989429134CA1C6FB006ECDD1968320F42D928E6A0C2F15A33E8745D1A3B68C17E332B3A448E10CB3ACB7F8DC78885720CA81F0BD06AAA207F05FFE3F58E1311183F8186338216C8871BDB83C0F6F41024B7713455D7BB3E21DA1F39F
	2A4BD894E27BE4E9A8328719CE7D9531F58F5298E170B526EB5BC134DBF40E42B03F19467355A0BF8DE867A50E7977AA301D4795836C350AACB3D31AB6FE531907BE91AF05C5884798401ACD3B7D5E39BD2A2D5330A1EBFF4709B614AD31EF27CA4F41AA1E7B9502BEF5C07D17189D97DFE2F6E07D5C553CDF0F4A7A0D282C5FF21575E36BBFF78957C2733881E1C898A9520681D7CEE7D31DA448CA9F3431FCAD2784ED07BE5A2D713C6C2725E81F8E284BC053B56E43D736703CACF1F081C9FD707338F967FC0D
	6932D80B78B0FF9A4F4BBAE8ECA0BA4C25F35B8B15563321C653942FC40FA543EEF9AE2B0F996F9357E6A87D122A690D3089C7380D3FF0213EF8EE99EC639C3F2F0A1F6385B06FC5A0065FD5CEABFE93017FB250FA201CF00A5DC6E55A2B1BF0AE93CF9E8194ADEABA9556F5A5D80B4732DCC3D152AD5042A32AFEC58D083EC3E87E2230AE49C57598B14E768EG1C8EF3BF9DD5CEC3999024A015587DCDA4364494D9F9385348579E5861B656327C51F944F5EDC7C62C7CF95656BD9979E1661F06F044CF63BF32
	5B56FC3DB11B59CC2A66B6CB04BE4BCDFB143A3F0AEF0F3303B7E58B7FFFEAE359A22E3478F6E0E74116924CF7B307A0779F1AFC7696C6715CD0E7F96031B20A7F2168DC351272FDC351396A8B25BCD75521ED45904FDD5E2AC6DC3906A187828A814D82CA82DABA4CF17F208B6FDBD366060DF067D203025CBE6190B030FE214A466236D17FDFD5960F5B041F098FD5C5677DEF8FCFDCBB9919D8E7631E1CB46EEFD1750228F14C0F9BB6951178F7C87417E63DBED2E79363ACD14B2CFB51B542F2419EA8F5A096
	33BE2F64F74C59A3BCE655537B4292282F84DABF42FDD06346F819BB1AE2BF8464863A8814GB49FE871A877433BD5D557738345D807D28B3C86085F3551E72CE93757CF5E1C3D5F1AB4AE5A5E29239347313C390046708D50367BB2600B7246A86FA8549BCCDE418964DD043AEE726ACB11B7976A4917F91ED6D6063940B3E63D0656B1A6327ACB25B71A17DE0FF02E0936D8F54B6730661C4C67EB2A6367F16B2AE367F16FD7474E637EDA3D0D71C1067596475B33CFCE1D67EDBD99AB4F5BC375FE69532DD436
	26099A85E11D208BC65B3B7AA7A4C63E570A598EDBB35CA12BB699FD6AF525761839CF579DF61018B9825D11EC2F751467987DD7DC66FB6D883DB3640D411E82F20FF1DC6C2D1C6AED23EBECF28CE6F17547F46F4AE3AC2E261B7C944A9F3170D93CED2E64EFD75321ED75985F434D6E787EBC670E6D4FAEB766B01A92D63DE2344F3A5D8E33AD325B9BF5B10066B5F2D2BADE9754BEF1124F95BA1E35459BBF620E078B663BDC3755F747CDFEE5897A5DA77A05308CD0D4B471EC48386623DD4651CE1DA55CF7AD
	637C5C7EDE894AD3C5CD3ACC33CF5E7707A57C8E37F41C3F85BEB10C392A9FB2E7D029BDA4FAC9688ABD87CF70983D927A2E457140BF8A6439426739D80AB2F4C927F3BCF3855FCEBBB155B174FF61E34C130DEC34960F875CB7C1116FFF31BC9ACBE9D7A278493B9A8FCB77D3EC96DC0DBE3B9F217C122B51E7F731131F5DF55016F415474552CA9EABF32FF1FCBFD7097A336F25166F364ADB9D2BA61F8D835F6A9C4CA7811AAFD9C2C151F9A968D30641BD787224EBC5F50D6D1E336D755DE7DB3BEBDB600365
	1B8B062D6F51651B4BE25E6DE87BB5663710D1BEFB47BEE36E3DA7F00CDF10B5448B0BBD42890E49A4681FFA8DF95FB931B3CA6FBD990494C1495A1F1F500EFB8B5F5EA26D6BA634237E5DB6EB7B7ED2EBBB6AF449984F563AECBF3F8325ADCBA272D395F16FC007920C37A42374F46EA385F7ED4CE6F2FE75DF2B4D7266FD19119EC74EF4E37C1C04C80E4EB0BACEE37392602FC860385DD841F11B1640F17B83A71E5F7937717A93340EF7C7637D46990DAD6CFBEB71FBEE529C37728DA861B6DC7B194ABA23DE
	9EC3FFDB9435C01F9E20CF0C3A8E5FA72044A045F247B79A689B96705F3E0D1FB1771E7EA7141D420011A132941626084074288AC4E1FD14F1694D5FD83F7F444D3D7BBF1F4D713668A78F10E7E63E1F7ED19C1CE45E4E56DCE0142BBB76D1BEDB0B3B89E3F6873405009201120196832D381D2FED55E95491B3DDD03FC65785EFBF3DED738438D39207A3ECBC900CB75AE6541449B85430BF1EFDC0C2934496F6CB6B907A880FBB775037A6D998D542BA64895368F9D44E4F18B520DFA6708A015C53585DFC816D
	33FD423DBDF6CCFBFAB35A718C3FD3929EDB2ED67233F1B5485ACB7B92C25FA5685F5A1331F86C9C4BB9C3CC7F7CB1177BE777A377123AB75FCF5F9747BF79530D58B75D72DB5CC0DAB6335B6F9A10E766818CBA06CDD7B5E0CC1202128E27A51C5BF84837C347E09F337F5ED6ECCF8239CB00D6832581654D60FE75B77E1D79B506CF6BFDFDA20F6FFC3E462678F91427697A3FC7DCE842F719DE85AE6F8114EF66D3CD3897F4A5C4190C67EB22790854572F332E86FF20097E96A07AA91B1D552F16733C2F9056
	661B417314E3BDF847B37358A2E1D8F7BACF2B126F145057C2D1E55C9BAB78F9B2FF864FE59B681B62EBD0DF8314B9133FF50D9C60FECDFEF89E44706618F1BE3563F8466E1946EF2CB3AD71B97C01916787862D784036F8674538B1FE01C5CEA4FECE1B10DB27C21F75B3270E518C9B5BBD9893F36EC83197GB5G75G69C0330196GAD82DA8A148A3481A8F7D60ABD9F2884288E288BC81E4531F4664C61F87B9B180651A920D458F33A694F032D71B1D554FAFDCC15355277BF40C247E848481DDD2D98B794
	3FDF93CD6C563466193CC64D23E8926E96E6439513F24AEFF6F63DF3136376D65C68B8162BBECBB119B0AB7ABE74A2654F1E95FD9FEA2D607BE2AE34254D62EF9F0F36E1FC6C39B345BE8E1488148C349CE8751D5C1FEF349EA557F97768D0C2AD60B859F22DE572F35AD1FE9A48F25C15E2F781F5G7583A55C4565671FAD1E82AF679405E67DF881G38082A85B9AC1F19C763F4A248CB3771770E7BA8AF99F875695C6F87BC7171F544F3FDFC15FAE247AC17E7F24C2A71440ED927BD561845F8AD9E0CD1FD30
	AF3A4CFEEDA66F02496B24B211F7DE72697DA67FA2558B798DA2CE48793DD42FE14A6BB2F8FD965E8503A7FBF00F40127B95150F3D64417C4FAF49B22B07E99FA988DB463447E52A239492ACFADFB3F997CC0C27D49073FB19653B23625B3F93853E55B9581A1FEE0B0F2B1F3545FED3D852967B0D6897EDD33F813D5A967F373E5F34CD7E2DEFDBFBFC5D9C6D57477C176D31B1FF30FDB2668B5BE3E37EF83B95736CBC968E6492B3DF21F0E67C5DA3DBA2FC8EE7427E97CB4F6A01B2794FA4CBA79F615BF0BE0B
	941ACA3672C33D2811C2B4F52858E9FB6BC1DB8649ECA4F626FEA349C49D9AC996D5311158C07D917CD7860476A683B2D96CCCBEFC4DFA775AB04391773E1AFA37717FACF7C74EE4967B16E453775559A9761659BC566D4CBEC262673E6D81C116F5C19E2C897AB08FBEB52A0B915F3F140D72B55E47E8E4EDAF53DCE240B2667FD0CB8788A1A80A67E78EGG9CA7GGD0CB818294G94G88G88GB2F954ACA1A80A67E78EGG9CA7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4
	E1F4E1D0CB8586GGGG81G81GBAGGG218EGGGG
**end of data**/
}
}
