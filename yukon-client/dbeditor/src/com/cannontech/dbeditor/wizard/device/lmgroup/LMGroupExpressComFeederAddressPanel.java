package com.cannontech.dbeditor.wizard.device.lmgroup;

import com.cannontech.common.gui.util.SingleLine16BitTogglePanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import com.cannontech.common.util.CtiUtilities;
import javax.swing.JOptionPane;
import com.cannontech.database.db.device.lm.LMGroupExpressComAddress;
/**
 * Insert the type's description here.
 * Creation date: (1/21/2002 4:44:37 PM)
 * @author: 
 */
public class LMGroupExpressComFeederAddressPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	public static final int PRESSED_OK = 0;
	public static final int PRESSED_CANCEL = 1;
	private int response = PRESSED_CANCEL;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JPanel ivjJPanelHold = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private com.cannontech.common.gui.util.SingleLine16BitTogglePanel ivjFeederAddress16BitTogglePanel = null;
	private javax.swing.JLabel ivjFeedLabel = null;
	private javax.swing.JButton ivjJButtonFeedModify = null;
	private javax.swing.JComboBox ivjJComboBoxFeeder = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMGroupExpressComFeederAddressPanel.this.getJButtonOk()) 
				connEtoC2(e);
			if (e.getSource() == LMGroupExpressComFeederAddressPanel.this.getJButtonCancel()) 
				connEtoC3(e);
			if (e.getSource() == LMGroupExpressComFeederAddressPanel.this.getJComboBoxFeeder()) 
				connEtoC1(e);
			if (e.getSource() == LMGroupExpressComFeederAddressPanel.this.getJButtonFeedModify()) 
				connEtoC4(e);
		};
	};
/**
 * LMGroupExpressComFeederAddressPanel constructor comment.
 */
public LMGroupExpressComFeederAddressPanel() {
	super();
	initialize();
}
/**
 * LMGroupExpressComFeederAddressPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public LMGroupExpressComFeederAddressPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * LMGroupExpressComFeederAddressPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public LMGroupExpressComFeederAddressPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * LMGroupExpressComFeederAddressPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public LMGroupExpressComFeederAddressPanel(boolean isDoubleBuffered) {
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
	if (e.getSource() == getJButtonOk()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonCancel()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxFeeder.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComFeederAddressPanel.jComboBoxFeeder_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxFeeder_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComFeederAddressPanel.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOk_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComFeederAddressPanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCancel_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonFeedModify.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComFeederAddressPanel.jButtonFeedModify_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonFeedModify_ActionPerformed(arg1);
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
/* Override me with an anonymous class !!! */
public void disposePanel()
{
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6CD490B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D457156E56266E56ED4D16EE7DF613DD1B58AE29EEBE5BEA57B4E4BF125A2F26A159B4D163DFD292CDC2A2C6E2C842A69AAF43A04370AFDAA1D19C9585929CC1D179B3002002991588A29823BC1887BC9DE64619078C56A6FB4E3D6F3EF94C8FBF36466FBB3CF74F3D775CF34EBD775CF34F3BA3593CE3DAE23CEEB3A17109C479F7A41E90CFA3A1A763FB17F0DC739F1275C473EFA7C096F928
	9D111DD09713E354E7127FACB7C0D906F2DC16D13F955E3349E7751EDAF889B2CEB3545D7DF9462A1147F96F889FA7091657A62742B39D408338FBG22GB2095118236043211C2E3CA3446B8889FB865B646E4D51600B1FE16D2640F3BAC0673358E60FF11BD22F07B23659864F12E7D95BC6F8A6D36E3EDDD92E343BF4F7A8097D7D99DD9A0E25214FA1D4699B21BCCDE491D5C482F962638B702CEE3E567BE937235F5CAD586D8E3BACF677192DDD22D5129DAE7320F4D3F0D94DD6710654AD1AED7D3DAE4700
	333252F27AA43E9C77B8DD225BDD6F68BFA10AD651D5E735E239D9300BB6D23DG652959EB20E301BCD01ECFF10F6DC59DCE007729G3396F0197E10D62D709CF2AD14C45EDF97D4265405CC161205DE1972AB566BA397A27DB346D4251F934A21G0C17FFB1F2DE7297F1DEFE34935BDE02328CB0D945DF5E057869D08E83D8300873F8340073587E677FA6F1DD57F599C1F80CDE44F85B7C1C17474233B9A939540E6E2AC0DBE806BA99E09240CC008800E8000F287D5615FF8C4F3AAE99945C6F3057B91D47EC
	D657E907D53441FBE5A554A838F3E23743E5A50459E8435E62G7CF050AF9EEE1F88CC368B045B544CF38F92D96E55A58610ED7642E6DD0EC223F831D7363E452837438528B78B3C47ABF4993E1E62E7EA70EC2E66D3DB4072FC28ABDE42755C1E4EF931686FA31925EEDDD2GDE26BE56271B320471903A444B8B53F1E7956AF88160E3G8CCB1375A600DA004E25DC475FADBFB81A0E1BE0CD386C022DE5204BA93864E383128DECDD51F52BF1A4DD8782562FEB5C7D18DEC28FEA67E879AFDD817532BC74026E
	56D2260F1025DE3D30317FAD685833177A5BC63ABA07A9F4CD4CDF4A66B0C145EF2178459ABC1B5BFB695A42F29454D916A56BD97BE7E87BD94BFCED640F949F314C57C62C3B380DC4C11DE5F9321E4DEF72D1741533209C813092E09D403667137565G1667799CAFAA5FBF46F5542C19DF56777B95FF02276811DDC2135DAD8B76EE31CDF0C9C217CDF493254D548A54EB5730FE7752755D8DE31C946D22CB10A5079D288B7DA298131B3579E9C59636F1093436D56A97E100FEA761F49F2C480627CDF04B671D
	D6C1963DADD87D2FAAD0A7B704B6208884607D1E67D1DFBD54675A60FD5273DCD707BFC5DCA41463D43D14D6EE0267E7E05C222D3232C16AC6B684579037516A02CCF8263EGBE90C07EA36CC311FCFD3E514E6D30A16D3FC871426B816D90A1A612591461C5DFBBEC2274CBG5F8C60841872A25FE71F5A5B6EC327A4B230EDA2A0EF48A7AF073D903C2DF78D3E6AB7C672A8FD92B46544D156AB537B749AEA6BA0D784C0740BDC7736CA933349E302DB64D6F8568586A4DA3DB3415ACAD5384708B7C43B5CA0BA
	C53B95DE5C0A8DB82B123D765963D0ED678645233F2A8783F22B78DBD5D98C8FB434564E6BA752FAC116DDD25700AC360832AC59FB3D7DA7D3DE5C43DC21DB11755103A82B7EA5D803G45AFF1D937D4E7EB6C4C62F0DC9BF036628AD26C7597F4DF2E05766DAF71794F5A05F157AD357C91ADFB6B6F5F69BBF7378BB8C6E07B887B1B0D1190G4746D83CABD329CFD4ECA6C92DFB1956CD837E668344G642C6072DF5D552E211D2874990D0F5CB1724B4678165F98A32D8FED5F310C41ECA37BE8F05B7E70E8E0
	5BB69D8DEC5BFB0F0EEC3B662301EC17559DBA0A72B9866D1A35D0F9947DE33743EE976902D43B9096BF3F510AF2C8F6C93EA0D9FBC539C53AA942DE3682ED7AAF54875742FB6B8A6EB387F7F31F19FF738192FE4592B03E1A7A13EB3A19AB18CD062F707ACC46D355273846C41B588F0A2AF78C58E5A5D6247EDD413B93A86E7BFBFF0CFD8BCF9D9B10E5077D6C35E2014AE1E60FE6515563F0750B56422B428D41AC8C4AE6BA8F66BADAFD9C5F0BCC7B76FDEA9AC338DFFC158FF2254E073AA5B8792A223622D2
	453E32492A0B8FE8792D474D56768D704C869AAF5F87D43E6FCDFA10718D23F4B90EB9BCEC28BB4A797021462D73831C77686A1FEBF50E64A0B012FA063E813DFB879BAF7F87895FDFE34BF0BDC442BA0985C8DD49771CC7BE45FA3E67A89BEB3D43E6E3CB996AA62E246B34E033E66D652FE4E7FAA7BCD97D87816BA3547A2F8B8355C72A7524A8D0FD2C4A674FEA50473B456B83A298A1EE0A1F09C3CAFC78731ACDBEF5ED02ED409BF7BED41382CF1BE6BFE5785F9D8C6CA31EBC08FB68B5157E9F8EEEA498BF
	B21A4C274D92102653A575C378018102123BF85C284DDD98DECE56E31E23981E35G2C6FEF5231CD2343A6D905218688D86B1550164E7A66D7E88EE4B74FEF34C3BFA74094055653AFAB67E55A6EF935DDA714BD2FF85BCDFDC55B6E6EFC5EAE924AF1GB92F32F66D2FEA5B1D5F435BCD87FC9840F2255D36A8562EAF8A5B0528790A7DAAEE86452D010D6DDEBDBF071D4E5466DF2CEEEBC0DF0EB0D3219F9E65FBBEADBACCE3F24047C4F1FB782CCC9BAB55395D0EEE096AD0195B774A34FE1E61B614996159A3
	395CF0D6350A9EEE9F090710D7BCBFA8E8E50EE7FCF214781F070E530C8F8FC56B8769E2EBF55443912691CC2CDE5D865BA4F1907A331C4A6C82B53F739A5F677EF2C21B47894B6850258451CD75EBCCA717DE736AA67ACCCE8A23FD2810533E67B51E4379150ADB20620E94F1DC6A6B5C965FAAC2DB2439ADF4A59E33FB90E2014A4AD35496D93F03A986C5A78D2F733C4F15F4DCBB37209C82B093001DC10F6DCED55A4E835C3A57797A1E5D26DD5FE52B127517GA6GB07F7A40EE5CA7466FCB79DC45B6201D
	5D6E664A66633E6CDA053F08876FA355DFD7864CD9CD4AE850052CE27390364ABB9F133A13D26E81E0FA58184F75E00806359F4D7570C2CDA0BDF82279FCDC152A3DF25C56064B467F35BA7EE420BDCF9D7FC7DB35632F0BE6E7097C55FC9EF66E796B6661112ACF117F3FEA5B65790C2F76F0994A01BF67EAAE43C6332F8ED106D0D506177FCA99C24E55FA676036B7DFA5D7F8C06B7F42A35B037A3F30554C161EDE6D6B7F52E84EBE9A70648D6E7FA69C4A52783FF360F184FBAF6CECEA8CFF98F581FB18D2BE
	F0984725FB98616716078A4613DB5B7C864F111C2FC07D97C239816092008C902272765061D1F30547BDA3648AAB8B4743576CB778D96F61F1751B2F762BAE9ACF3F65EA3F72945FFEB1EFF84F487AB57E67E5163FFB167A4E1CB5ACFF172262DF2478F28D1E2D831162315C8EF5E1EB78780D3BFD47B7458C9F335A274C7A3579754B73A9F77A74E36BA2238567BF40DE41D68A615FEF36E57846C038BEBDC0F3F28C5F0BD63521CD63F70B69B15C2EE6D4E26CE1F8937C91C0B5C0BB40AD0029GE1EFF27BB29C
	8934AF8F372FE01B32B70F43F4111AFFFBBA7C502F1F2F8E173F69C3074A3DE21BC121917BA61F47705B646129D1F9A8DE9BE89EFBED9F851EC76E3595DE1F5A4AF9EDD84BF91D386776F87DE73FFE3E3C5EDA9BC8DFC74D48837A5210355CCED6D5A1AE824A4B57F21B900F20ADF1FF586C106CB2613EF0D3B9466F8E1754AB59B9EEAB45F5BBDCB4114172EBCF5398AD9AE89A5E62F43F2C881EF731D6844E3BDC2D881CF7F1D60C1CD374D4844B4BB07EBEAF647CED837E986E9995D72262DA0AB82EC1451D25
	B1DCBB14EDAA2EB88DF1D3209C7A961F5FA569685BA3203C92A0B3966882D8E2F97D2B349EE396EBACBB9F60D9E212DAFFAD9DF3E158CFBB17A531A367193500E3DBB46D110F6136B4110E611BBF9BCB4E0D836E0DA9BE6B023FEF27750F7FA9034E155BAD3A29B301F31A20543DAAEFA18172DDE704FE1170FC41573B1315B647E5C77D81551EDBB3F8AC9D9A4BE369756769599C4AF1G19EFF3DD46347966D58B212E7AED260F6637836B549AG6F815CE49FBC3339B915416DBB2CB230FDBFD609EB4D6D98F0
	F50B3EB67CF8E50EDA67753ACD32580F6AE1ED1E217DE54155AB7A2D01A534BF2B8B567F254AE06B04693EBEDF2B7B032A5DC4ED7375C153C0A733953D4C8F20B7C66F5DE174CAD5FA4FEE5552DB6E53BF864A7AF70663983D0777E8691529740E1911DE0E5227D8F956AA4F56F77C79E37A082942F8412316E3E9F908F0FFAF56252BBABDEE37AA4E111D59DF25BEAF99BA5C2F6338BE6AD392618CBFC7453DC6F1270160BF910E3BCA7D4CD6404D086732752420DF3589C30E8198E341BBB4F714426301BEA8CF
	G888588FB0745850C471F8F62FA6CF1897DB015ADCE311B4CFA17FE8FCF63FD37C1398420994089B065DD9E6FBF2247F3BDBB31B6BAECD64E4B335538DE076555CA8F9F13A0AE6805D0195D3E60ED23CE22CD79E577547A1F3462BA7149DCAAF59345E4E59C0B50C5E932B8E851F6ED9C9477AE1337A18E1EF17CAC33ACCFFB16F14631B6A1F15C87F4E4785AECE81C667BDDDC309873FA9B7A142763D8AC192862EFD17CCA8D1E451889ED7C2C9587F5177E0F6F911BAE609C07C3F9010A7B026222209C276242
	A9CE7F9E6CCD6FF15CFB6790D782E50B0ABB7FB95FF3BAD55CB25AEEA216D55C06EDFC5CD9AA2EFEBB9FF71E0A1B3E1D53FBDA453DCE6316D8A86B5F67380BDF71F6B9AAEEC29E627A20FCCB45F550F8E38A14C3D5DCB8454D0672BC9577C18F6296C3B9CA45F57772B1E25F6779101F2470BBB68D6BE01CF55C5ECBBBB156D5E3B0BCC5D6D61EF4C956E3C22F052E9E36DF2C267BB6761F3D0E6F979F25A24DE8A86B57033E5773FC12E7AF4FA74D7C69FD44B2419570DED0E4EB072EECBD331B76755E33E675D9
	1ECF12527818A1D09F311E668D5234F60AF67FC3E8E006A72BFFFCD8BD2EA5B8CEEB6A5F9FD60F7CAE54E96B7725EB6B93B57D19ECCF6C61322DFBE296314D698CFA67E97387CC26F265C9FE3D1B44649AD2185EFF6128A1016FF2F553430439F7C0B28F48928411AD921E28AD12DD74739ACCCF69F9FCAFF7FE406726E59F4ECDA814438196FF206659FA026459284BE074B66D6071F64C875C261CB4DEA85CG7B7E860E7BE52E3A06B6F0BFB1C84FD33320BCDF457DBD45E16E9873428C176C97D30C84C8EFAC
	6D66AB6D58983B46B5C620EF3E01A0D153BE1B94D1FF53EF97E14E246E588D2817DA0AFB8EE065863EB738AEE0AEDF06A0261C9861353C1E28B41E3B00FBCE3F60117A877A69C7C9C6E78D25F38B5ACF82E0ED33DA505E1CAE31C7F439C4AB7B0429347F38057A87E89B8E90CD701E256D731C91E4E1B4571E2363CBF61F710D14DED93CD1DF8B40F005544F7D8E4086B8A60B2381E5E163BD3CFD747162F3D097BDE06F0C66569CAAA7501B84909D0FF42EFF55B08A1DC46509B0977AC4GECD17011701E2C2CCB
	B6660F689A41DB127861050D7BE09E4FC3176938FE1DE654056CF058DAA5E7ABCC97E1B2A61D504C17F9B87F2B29DFEEG9A4DAA1DAD3D43677B7CC92AA3AA5B4D5E11E4CB7411ADC2A39B4AB38146A8D05E191D4F704B7F0C86CA5E6DB67B2D9DF7BF36672F267B45E460BFDE611F615F2178299ABC5B33F62672BDEBBA5499920C4A393B3C91ED4C8665C9G53G6682AC860885E03167D0F58999A5B7536AF0DA6040E1539C0119FC76345B3B6B77E8925F9BE67F7AE7E46EDD26205FD18CFAA375D3054AF36A
	5C5D944F64FB228965EB07BA99E0AA40AC00F9G4B75DC3EC72B0B19FC789928CE16056EBE7A05098B079FFD3CE88CE419EC59F9DA59E21471B968930DC162385F517DA4A751F76E1E26787CC45F396B5847672E846AC2G5878DBF7E8470FD346CB5D481E260D7E633375741D8C7FF87E47DB392FA903FE96G99E092G668558DA7BFD6EE8FE8479626BED9A741D3D112DB5AC4F07772FC1089DAA9FA571B8576A8D8D955FE6CA7362597792356E4285FC96DCABD06CE45A20B697DFEE3C9CB4973FC051C71472
	342D5A2D50785FAF34B4B27F35D1979F0406A90975ADD61E98E3B11F347EB3227238669C66416C6220C97D1850A0016960C54542A3870BCD878EC1A0447ADDBF6D6DF76A733174E3BC4F7FD29BBB19BE79A4E06C9477E51B2ED361F52206E7D375FAE53FD9F246BB7E23B5E3991F352D59CE67213BC01163B7ADDEBA137E3C4B174E89F070053B2C12E0F3746EAAB615941BCACB0A18AD60B7DF83617FE2EA7CF1DCD71F1C47F7A67F0B29DC7EEDB36EA78D4F19F43B034C59DCC5EE0341AB3F8564E73C7EB60F6E
	AD2E81110D71E13BD70E35BDBB834BD16782C1C00CD2C58C053F239D04F03F7ED62A77FD1D663DA075EFF52FAE781ED2FBC747D06EDCB5864FB1B5B6864EB11DE98C1CC3EDED9C796E5A254660F7573A9B35F757184F4F3908670EDA186FCBG93GC2GC28D5C5F3F59087609B71F7DFD7DF1EBAF66387036334C47DF5094DC56E5CD235F11DE5904390CAE873833FE244F712FB761FC498EA7C5B25C1AA65411CB6A6D13D9D3857FF6933D57AC76D0B4BBB3FCCCE32EF9A057E2G060B3572F344BA835FA71E5C
	67353FC51A77AB7B6E3CADFA6E6898CCE6B3BDE3D912E13E13399E66A451F39214A728388F8DDCB71381477656FF4C61FB6BF240EDB69A15335D4183433E0B31337C891BE35022EC1B4AF971199CFE2E36C0DFF6DEA439C8F3A2146F81188DB0B6BF97D3437C480CAF08EA4B6D1768F7912091E56476FB5729CF6059A6DC753F3D8248E61743A93A64A10625372694DAADDF92551EA2CE6D89C683D641A99079EC57D531DBEE95FAD5DE2EDE46984110E2546783B482F48248G13D3B8EFF3CE970E409BD39EDDDF
	AEC1324B0C6ED3D7B02E1886B442GA28162GD2D3E19C00F2G8BC0A70087E08A40F40039G91G4B81E2G74E91C8767792291F8D092B2DEA6D41D18AE86DF678597C7DF67A597593928D7EC9DF2AAF96DC39769F72A1E9E37287A134A0B19AA2E495D6CF0CB188FE7FBCEDD5EC66AABDCB4014EE23376BC9E1B65000CA5GB60089G783D0645E6A962E83119761CB7957A4ECC7346E661707E567A8D9B8A885F7B0E6F98FEAE7B022E3188E837B80D1F4BA212061F4BFEA620FDB1FECA8D63B9F37A1E4B8C69
	C67D91F8E628FEE4CA4E6D1D2F7E7DB67BBDB76EFEAC0617695A5F1C6E9B5B7F0562CD693E317D46DC6EFF0A21EECABA3761AFAFA1CDF5CFE44B18FF233C047B9B5E3D06B0C43AC92F4835C0C3FBAF2BF7DF8EEE47B7AF0FEE475F3A9CF83F7A074B7E7B5577AE875E2FC2AEEB77AB659B4F1560FC3DF6E5F43E56DE894CD75C95FF3E56DF894CD7C227BFDF06CE3445DE708B6DEA3BB495572162B6F5A6AB388B12D56ED371B91DC6855FA862009C1F5709FC8DD2DC3B025B2D41F5A83842CEDC73A0F21F43457B
	6E6B246BCF3259145FAB51B6129DDC062A0F4314C749A9E8782ED2F19D2A0DCD48633FC50B857BAA4E60FB5CA3C6BC570FA9EF5AAA49B6719875CAEC5F1B2F66C92D99FC5F2B3362B877C0F9B6C0C446F8773D83B8CDCAAC7DBD5CF694DF74C052986270835EB8BC8C27C6D947B7F33D6F5F523C27665E7958E74FB7B046DDF923792915994373G2CDF48287DFC6FF2A831A46DC7B27DF38B01E0B334E37EEF8E350D42CCDF3F78BF94DF16696B97CFDA39DF2C053A69G6CBBFDF8B61EF3A53BCD320B756C9706
	044701AB29CD9A320CFAD3960F797EE39F4D3BC239B10B47438FDB3DF6B0CF73FE5AFA67672B670E0E41564B7BF96827E4DC3238CE884FE9E753BD3C95745019457770DF6DCB64ED89DFBF3192D15F9F43540BC277877B46308E4BF09D327F03601173516034E62EA609B2511D3DA6130DAC19A313A375B6297B1AC9F6185C428D51A477C9EE13403E661A68FDC62C127BC413DB449FAD08D6138DBF291801847B066BF611CD7EC90A3A6FADF9EE6615064DBA22EBA29B7B2813EDA20903682DBD785BD2F4GCD6E
	6B64AB228FD2F21134E1427D764707DE09F8214D1DC0B6B611DC0C07DAC4975DF6C5ABDE9FD1FEFFFA02A4BB7193BCE67C1B085ECD9F09EEF948A6DA48661EF378BB86B748D147B7F437CC36503B267B51416EEF945CFDB41BDEE5A1FABB7ED216ECE255B6415E3B1F6D6ED5D40678BA93CE8941BD07G3F5914DF24E2D629E4BF1C31E7672915F393086E84B15A9C027504508DB1F093C9167BC04BFD8E1B55632C2925048C03E89618DECDGF29F29622330E6CC5B2BCCDF735CBBB9ADAA6D040E900F0B6C27AD
	B17CB32BAD2C75FD028B0692DDCDB27E7257614AD76B7EAECF5E7B64492D17F4A4DE9192D5EF74B8E526B098BC0312C4114DF0569264252B9F3FEFD9F5CCB4F4B9C19229E54040195ED6E7867A3BC457DDB3FF38BDA5A4723B48179355C3A7B59D2C25C76817ECC37052C0D3CF9C1F8CE1065D0DD7FEE1D0130F443CCAE55C7C0347CBFE7840FB97A9635E2EB2594126883A6E6F959D7D7B6BFAFA40AD2984E03E007CF615BC36B17B34596439FB4B29059B3A403E9AC8C2FFC9897DDBCA7F9E403F2594D3CAB125
	87E052E248087FFC6BBD9E14F92BC755AEF252C46308489B87AB93D65C1F20A3FAD8AEAC2E82634560880C5745D6CF8244D5546E68F7994272EF49D411869FEB0C2758D4BF3352A3BA13D6EE4266783245EFCEB6322008B5C3927812C30B9F523F5BEFCF74FC8F6D3D03332E08E1D43168185181E00015A413A47A75365224569BB475D99A41191829E3953C56D7F0AC6D41BF85FFA3C9CBBD43E442E09BAF9025425497E000A7F5751B87EE741B05AE590CDF6F18295B846AB2180BD168456BC862C5125DA309B6
	6B998858DD3CA5F835AC0ABDA7327BE0F85235102D43F047866CD61BF806AFF08B499CD67D19B8246033076199870ACF20ECB841B52433EBB9A665320FC67F1D34CD7AD8755FF94775DF65E13969222F355F93623EDD93741BC4D9AE0B239AF33D39E916931955C16F4FGFEDAAEBF93128ECC5E854B5934748BB61BAC582E55592D183FB9B6A40B5E9C6A1C8E246F66ED14CA65CEE08772FDD553677F81D0CB87889C304F91F499GG20C8GGD0CB818294G94G88G88G6CD490B19C304F91F499GG20C8
	GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2E99GGGG
**end of data**/
}
/**
 * Return the FeederAddress16BitTogglePanel property value.
 * @return com.cannontech.common.gui.util.SingleLine16BitTogglePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.cannontech.common.gui.util.SingleLine16BitTogglePanel getFeederAddress16BitTogglePanel() {
	if (ivjFeederAddress16BitTogglePanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Feeder Address");
			ivjFeederAddress16BitTogglePanel = new com.cannontech.common.gui.util.SingleLine16BitTogglePanel();
			ivjFeederAddress16BitTogglePanel.setName("FeederAddress16BitTogglePanel");
			ivjFeederAddress16BitTogglePanel.setBorder(ivjLocalBorder);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFeederAddress16BitTogglePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:15:35 PM)
 * @return int
 */
public Integer getFeederAddressBitMask() 
{
	Integer address = new Integer(getFeederAddress16BitTogglePanel().getValue());
	
	return address ;
}
public String getFeederAddressName()
{
	String addressName = (String)getJComboBoxFeeder().getSelectedItem();
	return addressName;
}
public JTextField getFeederAddressTextField()
{
	JTextField unnecessary = new JTextField();
	unnecessary.setText(getFeederAddressBitMask().toString());
	return unnecessary;
}
public javax.swing.JComboBox getFeederComboBox() {
	return getJComboBoxFeeder();
}
/**
 * Return the SUBLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFeedLabel() {
	if (ivjFeedLabel == null) {
		try {
			ivjFeedLabel = new javax.swing.JLabel();
			ivjFeedLabel.setName("FeedLabel");
			ivjFeedLabel.setFont(new java.awt.Font("Arial", 1, 12));
			ivjFeedLabel.setText("Label: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFeedLabel;
}
/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('c');
			ivjJButtonCancel.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}
/**
 * Return the JButtonSUBModify property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonFeedModify() {
	if (ivjJButtonFeedModify == null) {
		try {
			ivjJButtonFeedModify = new javax.swing.JButton();
			ivjJButtonFeedModify.setName("JButtonFeedModify");
			ivjJButtonFeedModify.setToolTipText("Click to save this address value to the selected label.");
			ivjJButtonFeedModify.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonFeedModify.setText("Modify");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonFeedModify;
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
			ivjJButtonOk.setMnemonic('k');
			ivjJButtonOk.setText("Ok");
			ivjJButtonOk.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setPreferredSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setMinimumSize(new java.awt.Dimension(73, 25));
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
 * Return the JComboBoxSUB property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxFeeder() {
	if (ivjJComboBoxFeeder == null) {
		try {
			ivjJComboBoxFeeder = new javax.swing.JComboBox();
			ivjJComboBoxFeeder.setName("JComboBoxFeeder");
			ivjJComboBoxFeeder.setFont(new java.awt.Font("dialog", 0, 10));
			// user code begin {1}
			ivjJComboBoxFeeder.setEditable(false);
			ivjJComboBoxFeeder.addItem( LMGroupExpressComEditorPanel.STRING_NEW );
			ivjJComboBoxFeeder.addItem( CtiUtilities.STRING_NONE );
			ivjJComboBoxFeeder.setSelectedItem( CtiUtilities.STRING_NONE );
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
 * Return the JPanelHold property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelHold() {
	if (ivjJPanelHold == null) {
		try {
			ivjJPanelHold = new javax.swing.JPanel();
			ivjJPanelHold.setName("JPanelHold");
			ivjJPanelHold.setLayout(new java.awt.FlowLayout());
			getJPanelHold().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelHold().add(getJButtonCancel(), getJButtonCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHold;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:15:35 PM)
 * @return int
 */
public int getResponse() {
	return response;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
	getJButtonOk().addActionListener(ivjEventHandler);
	getJButtonCancel().addActionListener(ivjEventHandler);
	getJComboBoxFeeder().addActionListener(ivjEventHandler);
	getJButtonFeedModify().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("HolidayDateCreationPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(362, 202);

		java.awt.GridBagConstraints constraintsJPanelHold = new java.awt.GridBagConstraints();
		constraintsJPanelHold.gridx = 1; constraintsJPanelHold.gridy = 3;
		constraintsJPanelHold.gridwidth = 2;
		constraintsJPanelHold.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHold.weightx = 1.0;
		constraintsJPanelHold.weighty = 1.0;
		constraintsJPanelHold.ipadx = 191;
		constraintsJPanelHold.ipady = 15;
		constraintsJPanelHold.insets = new java.awt.Insets(7, 4, 7, 6);
		add(getJPanelHold(), constraintsJPanelHold);

		java.awt.GridBagConstraints constraintsFeederAddress16BitTogglePanel = new java.awt.GridBagConstraints();
		constraintsFeederAddress16BitTogglePanel.gridx = 1; constraintsFeederAddress16BitTogglePanel.gridy = 1;
		constraintsFeederAddress16BitTogglePanel.gridwidth = 2;
		constraintsFeederAddress16BitTogglePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsFeederAddress16BitTogglePanel.weightx = 1.0;
		constraintsFeederAddress16BitTogglePanel.weighty = 1.0;
		constraintsFeederAddress16BitTogglePanel.ipadx = -263;
		constraintsFeederAddress16BitTogglePanel.ipady = 19;
		constraintsFeederAddress16BitTogglePanel.insets = new java.awt.Insets(17, 4, 8, 6);
		add(getFeederAddress16BitTogglePanel(), constraintsFeederAddress16BitTogglePanel);

		java.awt.GridBagConstraints constraintsJButtonFeedModify = new java.awt.GridBagConstraints();
		constraintsJButtonFeedModify.gridx = 2; constraintsJButtonFeedModify.gridy = 2;
		constraintsJButtonFeedModify.ipadx = 19;
		constraintsJButtonFeedModify.insets = new java.awt.Insets(10, 9, 9, 68);
		add(getJButtonFeedModify(), constraintsJButtonFeedModify);

		java.awt.GridBagConstraints constraintsJComboBoxFeeder = new java.awt.GridBagConstraints();
		constraintsJComboBoxFeeder.gridx = 1; constraintsJComboBoxFeeder.gridy = 2;
		constraintsJComboBoxFeeder.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxFeeder.weightx = 1.0;
		constraintsJComboBoxFeeder.ipadx = 4;
		constraintsJComboBoxFeeder.insets = new java.awt.Insets(9, 68, 8, 9);
		add(getJComboBoxFeeder(), constraintsJComboBoxFeeder);

		java.awt.GridBagConstraints constraintsFeedLabel = new java.awt.GridBagConstraints();
		constraintsFeedLabel.gridx = 1; constraintsFeedLabel.gridy = 2;
		constraintsFeedLabel.ipadx = 27;
		constraintsFeedLabel.ipady = 7;
		constraintsFeedLabel.insets = new java.awt.Insets(12, 15, 7, 120);
		add(getFeedLabel(), constraintsFeedLabel);
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
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	response = PRESSED_CANCEL;
	disposePanel();

	return;
}
/**
 * Comment
 */
public void jButtonFeedModify_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if(getJButtonFeedModify().getText().compareTo(LMGroupExpressComEditorPanel.STRING_CREATE) == 0)
	{
		if(((String)getJComboBoxFeeder().getSelectedItem()).compareTo(LMGroupExpressComEditorPanel.STRING_NEW) == 0
			|| !(((String)getJComboBoxFeeder().getSelectedItem()).length() > 0)
			|| ((String)getJComboBoxFeeder().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			JOptionPane.showMessageDialog(
						this,
						"You have not specified a label name.  Please type a name in the combo box \n" +
						"and click Create again.",
						"Label name not filled in.",
						JOptionPane.WARNING_MESSAGE);	
		}
		else
		{
			getJButtonFeedModify().setText(LMGroupExpressComEditorPanel.STRING_MODIFY);
			getJComboBoxFeeder().setEditable(false);
			getJComboBoxFeeder().addItem(getJComboBoxFeeder().getSelectedItem());
		}
	}		
	else
	{
		if(getJComboBoxFeeder().getSelectedItem() instanceof LMGroupExpressComAddress)
		{
			int confirm = javax.swing.JOptionPane.showConfirmDialog(
						this,
						"By clicking Modify, you are attempting to change the address value assigned to  \n" +
						"this label name.  ALL OTHER LOAD GROUPS THAT USE THIS ADDRESS LABEL will receive \n" +
						"this new value once you click Apply and save your changes to the database. \n" +
						"Are you sure you want do this?",
						"Change may effect other load groups!",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
			
			if(confirm == JOptionPane.YES_OPTION)
			{
				getFeederAddress16BitTogglePanel().setEnabled(true);
			}
			else
			{
				getFeederAddress16BitTogglePanel().setEnabled(false);
			}
		}
	}
	return;
}
/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	response = PRESSED_OK;
	disposePanel();
	
	return;
}
/**
 * Comment
 */
public void jComboBoxFeeder_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxFeeder().getSelectedItem() instanceof com.cannontech.database.db.device.lm.LMGroupExpressComAddress )
	{
		com.cannontech.database.db.device.lm.LMGroupExpressComAddress selected =
				(com.cannontech.database.db.device.lm.LMGroupExpressComAddress)getJComboBoxFeeder().getSelectedItem();

		setFeederAddressBitMask( selected.getAddress() );
		//make sure they don't change this label's assigned value until Modify is pressed
		getFeederAddress16BitTogglePanel().setEnabled(false);
		
		getJComboBoxFeeder().setEditable(false);
		getJButtonFeedModify().setText(LMGroupExpressComEditorPanel.STRING_MODIFY);
	}
	else if(getJComboBoxFeeder().getSelectedItem() instanceof String)
	{
		if(((String)getJComboBoxFeeder().getSelectedItem()).compareTo(LMGroupExpressComEditorPanel.STRING_NEW) == 0)
		{
			getJButtonFeedModify().setText(LMGroupExpressComEditorPanel.STRING_CREATE);
			getJComboBoxFeeder().setEditable(true);
			getJComboBoxFeeder().getEditor().selectAll();
			getFeederAddress16BitTogglePanel().setEnabled(true);
		}
		else if(((String)getJComboBoxFeeder().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			getJButtonFeedModify().setText(LMGroupExpressComEditorPanel.STRING_MODIFY);
			getJComboBoxFeeder().setEditable(false);
			getFeederAddress16BitTogglePanel().setEnabled(true);
		}
	}
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMGroupExpressComFeederAddressPanel aLMGroupExpressComFeederAddressPanel;
		aLMGroupExpressComFeederAddressPanel = new LMGroupExpressComFeederAddressPanel();
		frame.setContentPane(aLMGroupExpressComFeederAddressPanel);
		frame.setSize(aLMGroupExpressComFeederAddressPanel.getSize());
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
 * Insert the method's description here.
 * Creation date: (1/22/2002 3:26:23 PM)
 */
public void resetValues() 
{
	getFeederAddress16BitTogglePanel().setValue(0);
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2002 5:15:35 PM)
 * @return int
 */
public void setFeederAddressBitMask( Integer address )
{
	getFeederAddress16BitTogglePanel().setValue(address.intValue());
}
public void setFeederAddressName( String name )
{
	getJComboBoxFeeder().setSelectedItem(name);
}
}
