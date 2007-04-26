package com.cannontech.dbeditor.wizard.copy.device;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Dimension;
 
public class GoldSilverPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjGoldAddressLabel = null;
	private com.klg.jclass.field.JCSpinField ivjGoldAddressSpinner = null;
	private javax.swing.JLabel ivjGoldLabel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjSilverAddressLabel = null;
	private com.klg.jclass.field.JCSpinField ivjSilverAddressSpinner = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GoldSilverPanel() {
	super();
	initialize();
}
/**
 * Return the GoldAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGoldAddressLabel() {
	if (ivjGoldAddressLabel == null) {
		try {
			ivjGoldAddressLabel = new javax.swing.JLabel();
			ivjGoldAddressLabel.setName("GoldAddressLabel");
			ivjGoldAddressLabel.setText("Gold Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGoldAddressLabel;
}
/**
 * Return the GoldAddressSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getGoldAddressSpinner() {
	if (ivjGoldAddressSpinner == null) {
		try {
			ivjGoldAddressSpinner = new com.klg.jclass.field.JCSpinField();
			ivjGoldAddressSpinner.setName("GoldAddressSpinner");
			ivjGoldAddressSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjGoldAddressSpinner.setBackground(java.awt.Color.white);
			ivjGoldAddressSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjGoldAddressSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(4), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGoldAddressSpinner;
}
/**
 * Return the GoldLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGoldLabel() {
	if (ivjGoldLabel == null) {
		try {
			ivjGoldLabel = new javax.swing.JLabel();
			ivjGoldLabel.setName("GoldLabel");
			ivjGoldLabel.setText("Enter the gold and silver addresses:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGoldLabel;
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

			java.awt.GridBagConstraints constraintsGoldLabel = new java.awt.GridBagConstraints();
			constraintsGoldLabel.gridx = 0; constraintsGoldLabel.gridy = 0;
			constraintsGoldLabel.gridwidth = 3;
			constraintsGoldLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGoldLabel.insets = new java.awt.Insets(60, 0, 10, 0);
			getJPanel1().add(getGoldLabel(), constraintsGoldLabel);

			java.awt.GridBagConstraints constraintsGoldAddressLabel = new java.awt.GridBagConstraints();
			constraintsGoldAddressLabel.gridx = 1; constraintsGoldAddressLabel.gridy = 1;
			constraintsGoldAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGoldAddressLabel.insets = new java.awt.Insets(4, 0, 4, 0);
			getJPanel1().add(getGoldAddressLabel(), constraintsGoldAddressLabel);

			java.awt.GridBagConstraints constraintsGoldAddressSpinner = new java.awt.GridBagConstraints();
			constraintsGoldAddressSpinner.gridx = 2; constraintsGoldAddressSpinner.gridy = 1;
			constraintsGoldAddressSpinner.anchor = java.awt.GridBagConstraints.EAST;
			constraintsGoldAddressSpinner.insets = new java.awt.Insets(4, 0, 4, 0);
			getJPanel1().add(getGoldAddressSpinner(), constraintsGoldAddressSpinner);

			java.awt.GridBagConstraints constraintsSilverAddressLabel = new java.awt.GridBagConstraints();
			constraintsSilverAddressLabel.gridx = 1; constraintsSilverAddressLabel.gridy = 2;
			constraintsSilverAddressLabel.insets = new java.awt.Insets(4, 0, 4, 0);
			getJPanel1().add(getSilverAddressLabel(), constraintsSilverAddressLabel);

			java.awt.GridBagConstraints constraintsSilverAddressSpinner = new java.awt.GridBagConstraints();
			constraintsSilverAddressSpinner.gridx = 2; constraintsSilverAddressSpinner.gridy = 2;
			constraintsSilverAddressSpinner.anchor = java.awt.GridBagConstraints.EAST;
			constraintsSilverAddressSpinner.insets = new java.awt.Insets(4, 0, 4, 0);
			getJPanel1().add(getSilverAddressSpinner(), constraintsSilverAddressSpinner);
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
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200 );
}
/**
 * Return the SilverAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSilverAddressLabel() {
	if (ivjSilverAddressLabel == null) {
		try {
			ivjSilverAddressLabel = new javax.swing.JLabel();
			ivjSilverAddressLabel.setName("SilverAddressLabel");
			ivjSilverAddressLabel.setText("Silver Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSilverAddressLabel;
}
/**
 * Return the SilverAddressSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getSilverAddressSpinner() {
	if (ivjSilverAddressSpinner == null) {
		try {
			ivjSilverAddressSpinner = new com.klg.jclass.field.JCSpinField();
			ivjSilverAddressSpinner.setName("SilverAddressSpinner");
			ivjSilverAddressSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjSilverAddressSpinner.setBackground(java.awt.Color.white);
			ivjSilverAddressSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjSilverAddressSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(60), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSilverAddressSpinner;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) {
	if(o instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon || o instanceof com.cannontech.database.data.multi.MultiDBPersistent)
	{
		Integer goldAddr = null;
		Object goldAddressSpinVal = getGoldAddressSpinner().getValue();
		if( goldAddressSpinVal instanceof Long )
			goldAddr = new Integer( ((Long)goldAddressSpinVal).intValue() );
		else if( goldAddressSpinVal instanceof Integer )
			goldAddr = new Integer( ((Integer)goldAddressSpinVal).intValue() );

		Integer silverAddr = null;
		Object silverAddressSpinVal = getSilverAddressSpinner().getValue();
		if( silverAddressSpinVal instanceof Long )
			silverAddr = new Integer( ((Long)silverAddressSpinVal).intValue() );
		else if( silverAddressSpinVal instanceof Integer )
			silverAddr = new Integer( ((Integer)silverAddressSpinVal).intValue() );

	if (o instanceof com.cannontech.database.data.multi.MultiDBPersistent)
		{

			if ((com.cannontech.database.db.DBPersistent) ((com.cannontech.database.data.multi.MultiDBPersistent) o).getDBPersistentVector().get(0)
				instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon)
			{
				( (com.cannontech.database.data.device.lm.LMGroupEmetcon) ((com.cannontech.database.db.DBPersistent) 
	                ( (com.cannontech.database.data.multi.MultiDBPersistent) o).getDBPersistentVector().get(0))).getLmGroupEmetcon().setGoldAddress(goldAddr);
				( (com.cannontech.database.data.device.lm.LMGroupEmetcon) ((com.cannontech.database.db.DBPersistent) 
	                ( (com.cannontech.database.data.multi.MultiDBPersistent) o).getDBPersistentVector().get(0))).getLmGroupEmetcon().setSilverAddress(silverAddr);
				

			}
		}
		else
		{
			((com.cannontech.database.data.device.lm.LMGroupEmetcon) o).getLmGroupEmetcon().setGoldAddress(goldAddr);
			((com.cannontech.database.data.device.lm.LMGroupEmetcon) o).getLmGroupEmetcon().setSilverAddress(silverAddr);
		}
	}
	return o;
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GoldSilverPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(350, 300);
		add(getJPanel1(), "North");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 9:50:26 AM)
 */
public void setGoldSilverSpinnerValues(Integer g, Integer s)
{
	getGoldAddressSpinner().setValue(g);
	getSilverAddressSpinner().setValue(s);
}
/**
 * setValue method comment.
 */
public void setValue(Object o) {
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getGoldAddressSpinner().requestFocus(); 
        } 
    });    
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB5F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD4D4671538E20256E81265F4ED56F34AEEC84A366E061EB5E75DAC75D056EEECCBCF52BD26B127749C125816533829CD5CED5D3269B7B3B0A0820E14D4C9088E0291DB02C6CDC388E64BCF0C68A8A3B392BC5A0D0F1987BCF8B3EF72660DB3E0543D5F5FFB0F618D98F342B9773C775D7BFD775E6F5E7B5DFB3FB7207AFDD965B6FBBDC236F2447E5E36A1147E86C2CBFFB3FA1063524482A7B27D
	B5816CC23B864B60F9B7505A6EADF03AD0FADFA50C33E13CF1F901F3B73C7F96AD6EEB7DBD3C241003007615FB0411396544EEF2B995E45C6B2906E7ADC08E6070F32B08650FFA76B0FCA143F33059914A9F45F396F89A992E8346DE00CC001CD1E37EF6A2F9D1E7879BDF3EA78BED7D4EC1FB8D66E3624921F20C2EEDE3CFB7FA4836E18C4B7AAA51932F8942F8A5G5E3C83FDE4FF9B1EDD5E49317782CA28BDA00443CAD89383636DF1E9DAD0036D81A5B2559E942FCA8131337383C58E7AA4792A28FA0530A8
	A36FB8667D02271270C2E88F0C0B88CE53303CCCF8DF85308E006E654441AE266B703507506AA7430E1DA9766216689ECEC946DEB2E37FDD51C06C7EB04276CD87DA8EC09EC0B1G1AA8F03A81DEA57E4E3B095D5DBD2275AA2110926E0EC4FA6420FAD6890AB23CF7F682056186450022869122BE4D7534D968B39750F5EBBF75BAEA131CE35C3F2B869FC4C57FF846DEE1E113221CA3760E89EA8B6F04E193AA7B17A9E5C7A6E6465FCC599FA2336C6CBB94C739056C55CF9C3227B3BEABCC3257CFE0DF1790DF
	2F01F79B0B830AFF01602BA78DBC0D45E9024763C62025CD7258982E653AF84A6EC32EB7BD16F6D8D150ED5FB4C9F5A81DB4F421F131560E4FD42BDC606C83388C2081E48264822C13F9EC14234337989BDEC155FAE2129C94D5C4572EC53F03271850D421BF9C5504F0C09C92D4C9981145A8E2F39EA5B1B1B2E3DDA10959G4878C08C0B2A20C9CA98388BA1D19355A81D73846C816628A2217A25108882C29144797E88GA1D908EA97A2C1C1930D99147EE362572B4290F0848670CE6D721C835BFC936C3F94
	20BA446DF04506791D91D5B84E1D1D9EC5190CC57CF8A768F2887B2A76ACBE5BEEF86FG203CFEDA06F1D9B0DE237379ABDB1D09CF1F94406A8B6A94BF9B4FEBB8EF9606382F1F9F663E6E2BF9G35BDF15A525798CAC25447A81C9C733FA2BC31DEF41C268D27B88305A16BB370694F7817D24AF0FCC67E353A2FAEB8312FDAE13FFDG41B037713F5B5CB4E6FB04284823F4C005G93030645695C75F61CD7452BE2D86B93A3E2B888AFD196A30F59379B71BB2A6831758441C7A05EFBA14022BA7E07F69C1302
	262952C8CC93FD2226C961B1037EAC11950D0DC40C2342E57D4C0E73099207F857677F5CDEC74EC1B8AC922575A50C7E8B922B22AC06C069DEA59656A87E178E6C6FC9F10A1D15DFBB1CA4566598BEF7341E658ABB709ED4A9842640C7979507F19C9FB84B6BB106CFG076B72EAF82E8F73180CB77318EC197E9B147F51813BD5AEDC71C01F3DD52171542598B1C9F55ADCE62D7B8B644CB0FC146AFAD7A336DBF2CD25B1302C8273398C7CA3G998A0F01F0F903691CB16777AA32CCCD295B78926C99FCA2FE9C
	93A1BD6183FDC6375B0833AE09B644ECC86993CEECC359148B283E959F619AE355AB0C45247618A66113AFE890D41118C6B6C37B0365A407E09ED98A6F8F7E450B77379146DBGAAA35C875F2F617DC166E496AA3E7F15143D0EBBC2ED5F97B1FC60DE487B030D15D8E6BA50B2A35CF6BFF61AF3DDF7B42A84A412E6593EB3AB4CBE2238AFD5D441F3D4D223DABF9C2284375117AB303EB88FB3B44B8F19E7136BF0CE4448BDF91159F908564A436731BD9E0FA45750F7883EB812DCC33F6965B5F48B507C9F735C
	7CF2950E73B5B05EG309920CCADF036G74295C8E87AB0F21F9EADFBF78D58D8B32EF462166B52852C14FA19BBFDDD94E4FA10ED9D2B73EC6FCFE85E4EAG9417B100F1ABE01C2B721C7D56F9FC26B768637FA56392FD7C28B7D53E2DE56BBEFBCEAF1ED346E74D6B54C6FB2BD2676383D55679788FD55679784D2ADD948FBC4C5D89273FD3B5F73E6E2E324A578EC2ABB86BA4F1BD2544B40492D16C3727745CD98D6396G8F4015A86D89E8EE78FA9C670CD1E8E6C014AFA28610DF43EB3FBC405766437C8DG
	DB816A01F69C004AFC7A4F588F27C946F813656B48F9F33EDE29D1D979705C00011C815BB9FE27598C3816985765A76DF81FA4CFCD88D705F6A12EF5F676A8B8D7FA6846E8DE6ABFD74956DF0E713C94A7BCB3E13C8AE0DD0C6725446BBCAF2DFA70BE64C9BBE319170A7DC76DC5B1562FDCB572D28E5CDB284CEF8DF019DDC0572EE25C0B8375C971F6916E086D70247483DEB39D7B965FC58D7A07B368D85FEF4CD83FF8863D1C4DE9C77C8E705D83FCEF255F4DC172436F244C39F9ECCF456C699DEAC6ADCBFE
	DDC16D7E6221BDE63BA75A23F188064E4E5326DA10F31E5702BDF1EE73BEBF3639974663GE9F1FE76AB4E5D5EBD6C655BDCF76ED32F23B93AFFG6B3FA2CEF334CD478F92FC3689CF63763A9E3739C02BCF705C6D284676CB40B893E09540BA00CDGE80A676E9FD49DC17364EE3F9271C02211CD29016E6FBB8337F7BF7DE6850F0F550F7CADDAF347B65B0E9471D1BFC56362B8FB16781B891E6E6F0BB5F8FFC1202581E4836481BC8ED0226FEF6F0EB63A3F770312562DE9C2E01CD4E33E39A1C90C9BE81CE8
	685E7E7EFC725ECA270C5A601ACED5F7571058EB1926BE5A21637F0D607BCCF86A3B53840F47C1202D1F6631BAB00074B89AB63DAFBCB7D7ED792CC0E52C7CDCE5D03FD4D7272EDF3F2D362EDF8D555675EBDF755C7569E0F56A7BC4FB3579BEC1632A2896673736EB854ED3GB2C09AC066B57DBBD075FE9AD3967174FEF0CC6CD548ADDE53FB3B1A54FBFD30E67E3B7FC3B538E79CD1B4CD89E17E9C7FF08D761726C48812629E2941B6D22531F10DCEE5787C9A1CA7E5F11420E97F340E644EDC58573AEB3C27
	5A6663BDD549B59E0BEB7D589E751F94B85BGCE810C83E4FC426905041E836375GA5G15578B1C2D57B95D6B677104F970B848E07C46BF393D1E6B94DBC7E5C83A8CF3FF054F952FCF37FB9E0C185DD41B5A0F4F554E6F472D3564BEG7677CFC5C40A5BD60B7BA3E5F4B4AA6A7172D22DCB4775C73DCAD442178A5A5B7C707C6CFE6A853D37B985B63F8210813092G773D6B2F63B9AE3FFB0EFD2E3ECEEDC9E55CF3905B8B5FA3BD42885CA0B97E62B59743F787032A980D92B225050F554D2479A2D2189CAA
	CA5F328B53693D543C1A537F3221BE194EB81C3AC1FA338B661E43F5036E6DB8BC993D550AFE37CEBF5BE026E7B37AC61D7ECC17557A4DBA3DFB5AEABD3A49FB474787F98D2E871C67A66F9D5F3C023F9FA6750E9F28D230C798234DA36DE37678F89F17FE1377B1A7FD18E79E16813079A66FEF5F70E35E6794D59BC73C2646C5DED39B3F765728F15FFF5AF6A16B1ADA16C6FDFDA44D68A7EB917F5E7162E80A1E0BF80B6A5AF10177DC41B45E4FDEBFE93EE7676DBCEACFD553D7B239F9A679EDFABF5B514AF9
	1726F19B5E78636B48741DE0D29EEB1F88608A50BEAA09F290146B4531F2928F906B45539BAA19ED0EAFA8F0D2DC43894EDB56F109E9BEAFFB814FD7BF983C3DFEEF53ED2EFB4DF7FBFD62A3A4BE0A96A477097FCA70A58B127BC40F0F4768D6208D7F856F938BDCEE6C8F98EF84A88528DF88799B60CFGFEGF9A12FFDF76FEC056710B041BAC55CD6A9AA29FE002417A2D27FD4C18AEBF4FFA7AED86DAFE3A1757F4A053373BF556B5DBAFC66F201DE883089209420B11D6B72544EF93FBB3C1F307C6E4074EA
	35522BAB1D5DC552E76BC557ADEE606B02C9F392B0DE8A101504276B761E3012371A4DDD1FD25E75E93E2EA8E94E969817ADAAF06ED9E4A5AF5F077477EF1B5E87FD1FFF8FB979394A207E7F1EABF5BD5E601A3F9E7F48E55DD7BD6B1A5DD715382C7B2A67DD663E0A62DEE4FC71D9B8A704F43E5BDC789ECCEB77D9A1AC0C111E1376DDFE12D35B400FDD0BF8AF764F97F8AFF6EA910F795E3A54FBBEDDE75DB7FB6B2C7B66213A393F7B7CDFDD2A3E1A695C4AF5DE216B7CF0AB57B9DB47E511DC3C95462EBBB8
	6E4F9398578263AE9D37251B2F756A384768EFC3B0DE23635EA2F93C9046C5BA6E3E93FA5F2A63B227395C0EBBB96E9F0EF2399E9D774784DFFBC5471DBE4665665E496B6E64857CADD183832F3B937F16BF0153946A4AA0757FC29F0B4D4A0C59671867E19CD764F7CA18032119610FE498391A723E6B9503F7E1AB671DEE415B1184493CD718F853FD7CD3830E4308AA0E0A2AAA86FD52B40E67A2A24BCF6E8959701E1B41772DCD10F8968213E32A928B87795C2544076E45504783D0DDABFA382E1E4533F5AD
	CF216B2BCC3FAB0B13F57DC9996EA3C2D2D88A45C2C4D3AA7BA59297B24CCFAC66FAFACE4C744F6A1D068D3FFE026B15ED2117A3C937B5B087434BCC17C213DED47EB7C8ECED847CA6G0AFB124416EB896CE98937475984175B35445A9ED6F239BD3CCB1265C6C9DCDE867C78925EFBBDF6497C7F9C2E7B1A6CFCCD323CCC26C35692237722FD10ED0877C69B00360F5893E29999FF5426D3B8A5301E7369EAF34F597764B6DBF38A3955DF2072AEFF4110DBB2D14AFA3E7F51FB3ECC20D3B94F5699B21D388DE0
	71B0B831971EE1B16E666DF4FB1F84B7F07C63F46BDBC75A5CC70F9E627AE5454DF66928FA5B7277B78C05CC2FE2764C3B3319498E7C4E10DD520D95516576AA3222364747A5CDE43DEB0F7EADF2A95F479D3B0DFD7C5D9E0357373AEEE59F146FB3FA5F6ADDBA17EFDC2F986F3D0D062C7B0F5E0AAC2663EB463A76445C7B3D1160FAE5687BAD5CEB687073FD862F8F35DB510176FDAB5DF89D2DC18E578114225F04766EB4709A981990831ADF98C33C77F08E197B2CFC50ED4352393FB1D0390FB66133DA82F3
	4B6E027E9760CFGFEG99A08DA093A087A09FE0A340E6G348C7AE50096008EG4FB2DE2B576EBA14CAF7C032CB2051AC6B7A5B8F19753F827C34E537227F9B4D589F99B0B7DB5761CC7D9F6650412BAA91D15526A8167CEA4BFB639E338E6B005F46F9F4206BFEBA23475D8CEB5072DBD937B0E1DED78FEBDA67D9C76B74EBA4EFF5AD27F9126276931C1F61587F9CB4256E1B1EE91A3FD7FC2E493AD77CC5536CDE713F1A2CFB455226593D22EDBFBEBFE3908B437ABC270E3B2863F66C5F4EF09FCAC1F26F27
	78DD7B2B987E140885F27C6E7DD82FB8418DB3DC2389F711611A76636FC93065F1C565EBDF5F0F6B6828A44BF47CFBB2C78AC7C5CD3747E122239491CCFA9F57F197F5DFB49C1A6D1F3D07E67B67E8B376295EEB52D0643FBFB657937912A68932B4CDFEE277414470986BC3FBE64B3858E316C1F1AF1D18BD4F715E6CF98FA4E64F7BEA42BC0F56EC6175EAC3AF62960AEFCC5498F86E96C47FE774BD6FC9BBC22B7E8315EB2865FD7C2B38DB9B975DE30A9CF48B6120BBCA3E0039857A89CC0CF6EA2886FFD5F3
	330FE2B05EC93F126918280AF66AC9351FF829786A31CE473377BB6C282C9FB9E9547623B29CFA7D281CC4E6BFF2C054A670FF02618862FFDD510F518D12334BCC58B210D1B7BBF1F7AFFB6A49D59F75555B119D440C0B14EFF99C1B02F046676AA82C2D1DD9C4EC8F6CDE68DAFEACE6C715A711F3D410A363822CA33F78FB500E9122A07E0EA0EB486541EB8F435A43E46D3B9EE48FC4B5742A018FE0BC257AA2C2C004A95B0302BAC9B8606DEC3F04360F2AE3A3C400066C17CEDE8231E40449BBBC288A1C500B
	7F79A421E9E837415A0B93B3A33CCB3604FE83FF91721742D4986B54B7FF2BF3AABFBDEF5E679C3AE96EF3FE35E8CF4ABEA7F2AF4D296977A67F1FE3A5797E188F7822FBF96D989EFC83252E3D3E10A04B1AA0CFF60703388E77CCE922F1372C71E17ED1BE0791A92D5E07751EB02D79FFD0CB8788364459C32F90GG2CADGGD0CB818294G94G88G88GB5F954AC364459C32F90GG2CADGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBA
	GGG6990GGGG
**end of data**/
}
}
