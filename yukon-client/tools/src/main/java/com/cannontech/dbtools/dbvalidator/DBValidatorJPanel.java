package com.cannontech.dbtools.dbvalidator;

/**
 * Insert the type's description here.
 * Creation date: (8/7/2001 3:37:53 PM)
 * @author: 
 */
public class DBValidatorJPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private javax.swing.JButton ivjJButtonValidate = null;
	private javax.swing.JList ivjJListOutput = null;
	private javax.swing.JScrollPane ivjJScrollPaneOutput = null;
/**
 * DBValidatorJPanel constructor comment.
 */
public DBValidatorJPanel() {
	super();
	initialize();
}
/**
 * DBValidatorJPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public DBValidatorJPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * DBValidatorJPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public DBValidatorJPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * DBValidatorJPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public DBValidatorJPanel(boolean isDoubleBuffered) {
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
	if (e.getSource() == getJButtonValidate()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonValidate.action.actionPerformed(java.awt.event.ActionEvent) --> DBValidatorJPanel.jButtonValidate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonValidate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonValidate property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonValidate() {
	if (ivjJButtonValidate == null) {
		try {
			ivjJButtonValidate = new javax.swing.JButton();
			ivjJButtonValidate.setName("JButtonValidate");
			ivjJButtonValidate.setMnemonic('v');
			ivjJButtonValidate.setText("Validate");
			ivjJButtonValidate.setBounds(11, 9, 85, 25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonValidate;
}
/**
 * Return the JListOutput property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListOutput() {
	if (ivjJListOutput == null) {
		try {
			ivjJListOutput = new javax.swing.JList();
			ivjJListOutput.setName("JListOutput");
			ivjJListOutput.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListOutput;
}
/**
 * Return the JScrollPaneOutput property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneOutput() {
	if (ivjJScrollPaneOutput == null) {
		try {
			ivjJScrollPaneOutput = new javax.swing.JScrollPane();
			ivjJScrollPaneOutput.setName("JScrollPaneOutput");
			ivjJScrollPaneOutput.setBounds(13, 42, 417, 366);
			getJScrollPaneOutput().setViewportView(getJListOutput());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneOutput;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonValidate().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DBValidatorJPanel");
		setLayout(null);
		setSize(468, 426);
		add(getJButtonValidate(), getJButtonValidate().getName());
		add(getJScrollPaneOutput(), getJScrollPaneOutput().getName());
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
public void jButtonValidate_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DBValidatorJPanel aDBValidatorJPanel;
		aDBValidatorJPanel = new DBValidatorJPanel();
		frame.setContentPane(aDBValidatorJPanel);
		frame.setSize(aDBValidatorJPanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF7F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D98BF0D455996695088EB30DB6E3990AB5AAB2F148A8D534CCE526B123A315D86D0C344C94BAE9E5ECBA0596ADD3D163746C6635A464C1F649EE9E10E002900D9185C70CD19392DA88292CA606CC0D551B6CCD32616E5E656E5DBCE0017E7FB9775C3DFB598D24E31979B9FB7E7FBC7F67F7CF0823AEA55FE2F590E249A75A3F8FAC0434B590B2BD1D3D17736608998544706FG509EE23767C11B8432
	AD0BB28A4A498A1B8D7A4B213F8E7ACE78EDA7ABED2E5761C71CFD3643C2B5FF4C5AB773BE37933ECFA16DFFDA319B5AD220D460E13BC3447D4FD538B4FE264667E43192726C880ED1AAEAB55EF1683B011200160F3071EB215DC5F7CEE8BD2E0D9B3CA5056CF87210358457B12C4929F6144DED575AFAF23FA5FB94777AA1BDA74E19027EAAA03CFCAE49EBFC8F5AB6777951CF06E5DF4B3060774BFED59C9EEB718CCD881257A32832525A5AF5221FF73A5D02DF1488710C613A4FD55868BA04B4C3FFBB65FDDE
	07FB25426FF4200DBA5FD50F7CE35E0C029EA00F175F6F63C3ED5A7987AE5DCF86FF6D4FAD0BF33F30175DEB39B7F23F6450AB05D554961BAD2873AC10650DE79438005A00860142C0B554873610B7216D98D2BBE51FCF76F78482A7A40F72AF59A3CA703B3595A49A6F8BF1D8D6BC04B0BB5FD9519C63BCB3911BF77F2C67B11D249E65B6CF7BE28559746099EBE18C1DECCABDE2CD9EE73AC89B0F68046D1D9DF76F4C71E81F0C5E7B9CB16E5D33D04E4D0F31776A8DCD56ACED1D9D063D9B4F232D9F27BE6000
	5F964DBF987FE94A9FB37019FF1E23FC6CCF01EC53F96E9B83257CACBDF9CBC9793BBDB17530A423435AAE31B38CC86633AC26FE37C0B21F65B64ACF164CE759DF4F4F329CE4B6DFC681732F67F3B1DEA568A78225G2D815A883483486667BE7636651D9B74B1372028A7C2DE49A3AA044DFD5ED2892DB825AA42E9FFD0957C43E23F20F805A1C98C92ED4CDF28FD0722666D203EBF8CFB7CD3740B0A20FAE5BF2CAE78C4D5D402EC4CEB16BDB8C69129344F6B93E183DF0070F5732DF63C1F90D43F8CC0280B11
	91CCDEE2C51BCC887D30A2ACG3F19DE6C79282F43F07FF6A0372E07C43A5E99D101FC515A5AA34B67C301BE3C89C971231E7FCEF30787FE077DDC47E585485B827D9CFD1D1FD9AB8C6BF4F907717802B24DE3ECFD9D6664F2197B4C1F87384FF4155CC31AB7F447749924A3B27315D332B9F6B650B571DC2C7FF55D40AC6338DC0E9DE371F345442C77481D45D8265F4B05E82BG1CEDB1D01A4CF57C0A351E796C89A1A8F2AFBD2B0003091E084659B9EF2D4F5374497A4BE87FB1196B2A31FEA00E3E3F8BE2FB
	FCCC77184D7DE7C3CCA75BE99D92A7C43F5AA586C43F87FE84B559CB3968EBDA1C0E487A1C57A8BFGF15D890194547939F998FB022AAA5E2110AA760A2A6A750FC66445F938D7B0B4940824843ED7C59E66DFF99262DA9F6F4C431898167DFE119AC71F22492BE8CC0A126803C3F74AA13F4A78F579E82B73623416939A729164C0258FE1FEE118E015D00CF7D03CBEB0B926A8529640F82D343B8CBA5A8A76C6ECD38E3246G0F3D49869EFB0D976FA4695F9C34462A9DCB6E6932AE883078D89208449EBB1323
	A076592BE9EE5078C1F6565B5FC23D4600A71A3FBFEC433556409E19C0DB825C5FEF23790667944D51BBE5C9E26A5475BC3F309C6DA2DE8809100AB1F91D51F5F7F3E105C9562F691149EFADC4BDCA063C17FE817578D4BD47FDB9F09E87700EDDE03AD4AFB0DD303B7D358975CD315938B0A1CC3584A741E7DADB5995997E1AE337E963F7AB9CE785AB714EDD509F84D295EE1BDF16F01C15FCBE05E45DDD9D97C7AED658B9D6A9915B544F67B80B94611E5BC1C602DC1FA5B6E32E6F888665E1AFADB31AAE565A
	0C36E33CC7EDC5500EF81520FA9A02EB0A6B6D899B1E976B1046566A725FE8DD463BBAE05F9E2074A0725FF7BA8CE7DF90E475A48D5ADC3A5E578790638A5430AD2CF10B4A082C78C44FE154EC0BB0293650B8EF69206213783B095BE17068A7500E1F88292A6C579CCD7C264334D0CF7C15BEEA6A67586AAEFB3BF64E4C7B1612362BF6EBAC2C1BD8364FEAD3196E0F28919B049FA8485D7A1FD78BD98ECC301B71DDD80D541CA4755A7A43F04F0B54BF162B8C77646B7C57A8FF0D014F74BEDD49751E8932A3A1
	DE2B3FD70C7E198C7DD5C06B00320048C4C6C17D84770B333B0E126BE02153703D21788529B7AA79F1CCD2144B7215565FD5144F73956135A0275E2C073609683B8F187AECDE5181733C7DE6FD1A66B1FFA86B2D0A9D174CC3887FB6121CB6ED2F64899E03177FE10C4135E56FD90B6344E02636775609087D1B75EF1D8FCBB007B4CFE6941C827A96A88C14823496A8EB126B7F7BC59FDFCF7F54C57B64CE601D02DC55E92EA9B01F6135E5B45710A948D9C00C77FB87720EC35FBD456B7F4ECA642950CF52F939
	D497186BD6693C2B15B702F7F01D9B2959C9D331ECFC778D6DF123B8A25F54E7BADED29C9FA75CD19C9BA75CD39C9BA72CAC5E43783006716B004B9FAC1E99C7BCD29C8BC76C23F77CF9E599DDBB9894035468DD02AAE832BF287840F28D1E788CBEA0B47D584951833B34B1A7D53973DDDD47971C66B8FA96EC112D59A3A7067D188F2717E25EAE1F4EA8E8866A829A848ACFF35F7DFC77F5F347492999F2C7E7C9FC3BF41744360B3B84EBE2D08EA94322D97767CADC3AAC92A527D55107EAE2E33E2573D5C199
	952F315DA81D4FE4716687CA6259175960C1BB62B5C91816C3AA7C3808B178240E4B16016ED6CFB3FDE7CF33BA48F2560FA7B14F0C4087A1AC579B900749A6BA77E5871FFB8A7A03C0E120E5C0EBAF6A755303FA6C0E2EFF5AF95C0887C8F76F30825889F14959109A20C7E318312E5A08999BAF31B3392175DC62B9A4116606B07413F45EA297FD3F037EEA1D375545F3CD7AA56EA36BCA635B78695258B65ED09ABB76FED3BAF36CBDD79A4FB62CCE3EE867EFECDB60FC35E12E7B51578F92439B1BF74857921A
	703588C3G95BCDE3535753310A475F013B28C770E83576A03B5A42085E1DEBF265E6075A3ED45D252B367CCCC8C1755771EF5D91879C1FAB8D2BFD221FE305AF4F92FCD1B178372E3171137F22F91C721FD3F047FDA20E5724DD1F25485C0A4033C2C52A847735E9BB53FA3CA1EEF184F6E367EA03FDB4E7AD4A2BD74E1DCFC9A3E4C6E1422356E7E8652F8732B051A3ECF6EA3B16BF344CB892F51672A797B65464B9CBF2F28C15D17DF49A8B88C54FE0547419D54CFFDFE911211F7F86C8A0EFD187236818D68
	63FE6442EFAC959CAF6D8A3EA1F760130E96EF39D5BC9E3603AC6FAAFCBB81F581F92E6A75739ADCF7BDE2738A9C331D47F05F5D545796DCE5384F227353A8BF4940E7FE7E3E037B79B21011B99C8FB6BA71EE9E684F81DA82348A6899202DF3F83CCE179DA1574969FDF2208782DAB204A03B5F3C3D7F5BDB6A23055C2FD67F74AE32E6614B16F8180B4C55E2C5EB337B9A8C756AA7AE3C1F9BE492D092D0AA50DA20E767727BBDDA564C6EF792A23BC3D50561B17A4543AF576F95A7A3ECCC446CEE0355B35DAD
	FBEEBCACDFDD0B364819EBFE5BEC247C5D734CEF1BE9B57CED3396E4F366F11FFD4A096D2109C31A4ED2A60DFFF3B8DE74C14C6FF824AD735859F6EA6D5AC58D5A9ACFFFE5DC23FC69812BA54E9AC7663339ED73A3390A45E7D3954FD30179BCCF1D2BB267A9448911BCF3D315B9CF5923640FD41973544AA87936AAF31EF2EAF2E62BFBD12C7DFE40707BA347EC5F6FE69F63FD7F57BD3477EBE77C1AFA4C993B2636BAE35754F6674CF87514B37E3B57D927715D0B4560CD552870D4700547003201F2004A9770
	785B6C44BF71618B7B3531F752B30A388DDF55D53EFF3AAB7EDD1FF0DD7FAD7E99973EE18C49G0DFC38BE677F4A0576D265GE5EA784205BAD23C23E3AA9B2A71FFC7F10DA40ED0B643BA41EA0EF51A97703AD0DF0BF5452D775F247DB13D7FC00D59B7F06CF76BFF6B2F59633BFC03E5B64EE9473BA74259176BF6D52A5E0236DF9857AA8626D7D921168526DB11832222CEB3AEFD055356FA290368FADED3F5B05E9A609D5DG954E8E0D83A86F93C6757B36D44D3666303DEE290E6FD7CB2A2F6FD7AFFDCA5F
	4B409F7A26835A37C14E27087D65111120287B2F253DDC671D8E3A6520973FA9985638D9475E0F019EB7826D84F2A5E4949CCF60F97FF0AD56426E9EEFD05510BBFBD37B2A561883879258F7FBB8015B4352D1EA0CEB26F26DCC368E1C6B7543D160E361A2FD866C05017ED11653D7D5731C1E1440F37AEF2B4DB9FD335518134BEB4CB97DCBE2141F28B167744EA86C295618F3FA0DB61FE14675CD1863E3E0C6540C0695EF2D65D8F1DD820F3907E62C5B7163688735B34DA38B2F0DDB16976E2B65F94181E318
	1D9F2F65F6BE8E3C9EA089E8B1D09A90BB7B1FEA79BBF166C22E7306FDE61D3F902553C7761BF5EE0912EF5BEF56F90745A82F2BB36B1C4FE7BA589E25033CC5601B0BE64ED7CC87057B70AE4DB09677E33C5D1457853C97F41B8AEF6C0E7861B92FC79DE37C5A2912887F14087918309AEFA73F5803F11B360D642B4426FF6BB2BC53F45508E7F6A63862E22243099A1EC90C6099160F0A68FB5994701393F9AC7D5B7EB6091F17FAFD02A42902F43E436F419CF5E2DA95A375334C016B877998CD48E4158EBC77
	38E14EFF81D0CB878803E60317A38DGGE4A3GGD0CB818294G94G88G88GF7F954AC03E60317A38DGGE4A3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDD8DGGGG
**end of data**/
}
}
