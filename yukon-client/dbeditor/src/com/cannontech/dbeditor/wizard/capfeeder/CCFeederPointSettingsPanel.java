package com.cannontech.dbeditor.wizard.capfeeder;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
 
public class CCFeederPointSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private java.util.List points = null;
	private com.cannontech.dbeditor.wizard.capsubbus.JPanelCCPointVarWatts ivjJPanelVar_Watts = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCFeederPointSettingsPanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{
	if( e.getSource() == getJPanelVar_Watts() )
	{
		fireInputUpdate();
	}

}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE1FE73ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D9FDD0D45795371D1A2153FF4C0C13B11ACE71A316091A78018685F5D5C49423F1EA7A11B28D0D36E3260DB2ADD3498C13DC96D7CCD8F179D091B0E2E4CDB404E8E5264C0E0DA828AC22905C6A5A5A240F5D87BCDD762D3BEF65C3B0BD675EF75FBE56DDB41930B3E75E3B67FE1EF3FE675CF35E1252A33126DCE3A9A139A6227E7E11CBC87B91C21453DFFC49F959AFF564935D6FA8D091B9F1E697BC
	774C6D4877ED6848371019A776C03B925A96E89F047792B26B4436437092E59F8374FD78067F5F236F332F006F13CF5BD54DE6F89682D5812F89E8826EC96A1B4BD4FE8F348B55F724DCA3A14EF9B846B1DC2663EFD64755C0DF83D08E9DF33D39D26D0F05B60EC900677AF9EC6CD6F83ECFCFB75ED62F0E3B7DF4AC49DA7711F1AF6E25DB1FD353FCB637C7FDD6119728A272C8E3379D1EE75B6FF6FF6112FBEBDD0257ABFB955155D36B6E945D12A27BEB7B24C1416F06AEDF17A83AC53F4D5658F8093E354B12
	D739AEAA0A646D8E348BDE5183EB7DFABE4AB0B83C072E8FB2C05B3DGF9FF99C63DD542FB9DD06B82AE47263DE755F3CE3C9BCBB2266F0FAAC75C82F67E758BC2F2D4B56FB08D45637AC354AEB84F8C6DA6A0F61664E6FE96C3BCBF4B6B8FF0ECB64EDB58117F49C24E1FBA047C86E8BB01FC8B79997F79B1BF237341CBA064563CFDD14E9833101DED6E425099A75AF6188ED16CCCB1237D7F8CFD16C59D7955C04DC0BDC0B1C0959433B78ACE4273FC2752A877764A5E73BE5F850F5BFFD5F60B9EF8375920C7
	65F508AE596FA60461721B611A886761943BE8A4A61118ECB78947516C0E19A47329BF6419A248967FDA35B1DEDDA3F5D1C8362CC528372E072837ED701E2B4EE5FC19726BDF8E71192D3207392D1A20AF6EE52EE7E7A1BFCB6B2E4944F27A32B1BF42D9A62DBCEBB4A730B3D4A7044E42F49CACC49D37835F8374A320670012005293380EE38B4EBCCE47F670833FD770DC8FF67A843FF2A1A8F9G732A2EFF58BC1A2EA3911BB761DB4FE3FA09BB2337D17A02CB9175129E7729B1DB5507E5F1C8AFEC6F35D177
	BE3578D1EC94EAB63CC6FD22E9B133E11E46F7D23EC347E7364D203E05EDB774258231717B294DEB1604E364C34A2FDF920E111F8FE998013EB8A0E65F928B46C7F3A278ADD0831093289FE8A2D0DCA237711F8ABEFFC2BFEA5759174D1DE1BEGCF31DF718BF6EFC0913CAE7106601704CE0F98A06A18B9E654EB6708F97329FF3BE00F4B22D7748B0AA4FBE1F521D784B0855818C4F3910E710B3457A1750A30C12F0F70F5D719CB606991824A3FFCEEC191C3A3D8FF1A99B1F1DF3881AB4282701E1D087AFA06
	465CD4F85F1A48F555D0043C33C99D790EA42E17CBFB0B61F98540ADFAEC36A6490547907C839C231E07C4435DF6A747DD535E67C94DEB97A3620E5EADC98CC3631242F197246B3177F7071D5F927F5F5E478EBE9C4BBD18DEDFACC6EC46012C49C0DBB55D7EE0EF95435C85A1A0F214357981A022BB24E9B676E1A15EA162FD512BB409BE516B061700EA6371056F077057A5EB58788965E3BCEA840084B47E5305C50C8FEB68514C7B1F257D022278254E20A272CBDB6B1FCA4F9298916A822A2CF3CBD0569C10
	31F4A95CD9CB392CA7771568F054AA4BF703BE87FA080A478CFA6F5643787625BCF7F28CE1AE25E86D2FE8BB547F47875F276DF01FC771715E773AC7DEC48CD68C07B0DF2DFB1FB3B47678CF9953BD186D8FDB22E3375A92993BA7AD1131FB4AB2BAB66BAD1130497A4ED9B0D7117B3CBA2CB7D9B03E39E42FD7248E27CDA1AC675D6EC0B9A42F245C145C5D22F2DD9A94A127DC0A181DC3E3688CF8CFD84AF3123EE3BC86D68FCEA5062F2B0C117223C94F1FB3DA1629B94932D08CE4E73AD804BEA4FA44DED0D4
	239C74AA0CFF0546E7159FE07E3625145E3730069348370C7B5B1AA26C677E268615C65963E1E2EA724797DBD0DF623D20885796BA6495F1005FBD4545E1FDB784CFB0F4272DAAAE0027C7674B0C3F28A4727913CA50FF6FEA6B9BCAF6933C1B581ACC5FF385DC536717FAA174E1F0D2EB211D4D7ADAA88664443AA9951E69C0F9F43D6DC73186EBEC04A48C2EC0E64DC0443AA3EB994D75353A63F30D77DB4A5B894EB1BD0F5FEBD7AD7A5A2961277E08B6C52ADF4EEC593ABCBC4FACBB006BFA00BF341C5B48D6
	2A0F0967838159A551E32BFAFC2BD4EFEF465BDE7A81BC3BA4FFGF2CE37584FED11D50AE745BCC0E52B7AFC560639EB241A4DC5931D5A6E20D49BD4A43C5C8585FC5097D41816D8BDA4587728EBCDD84E6B1E372F21FDD3211D81143D1CE37F665F75FAB2E454C455134520E25F902E272F8E62FE8D40F798381E647DC5BABDC19D83A9D6B720CD0BAF8772982E54765183A60EAB4263DC17BDBC1F6DB104627D1061511C16651BC29B4ABA41901E4F5E257CD8C3F8BE7B57EBBC1F1D8BFD25ABF8EE65A8C3397A
	219D33024B356840DE1DDC94281ACC2E03689B32DF6A163C1C57C3F9D04551G466E4DE1EA1FC93066ECA0460BEDC15E2B505E36025F155DED68FB66153CFD0F36BF515A6FDCFB52FBG5778EEF7CDF15B136E91FECFBE79DDF32B8C6D512F357FCB5B836A1AA5E422FDB7751381B9884A0EDD097A1AE5574716D660A9C0B12A1C4C0F6675E1DC6A02649C6248F51F68A2C32BF0EE60AA1F3B8D4667GD5827FAC506DD5BCAE3DF9886F24966AD6D098FCFD932E2F009A5726356977CEDE45721F52E428D700585E
	277F07B58C7561BB42FD21D668D3EC364BFE49FDC168EE2512B0DF5DC4310072B6A4F3DFFD4F0EF86C0776842038E46E2B7D4779B70A59B3A71356F117A2FEC749F0FCE6B4A42B5F1212C33E9AE75BE1E2FB6E3C4A772CDA8DF750EA64D92F1506593257C8C8ADBCD97F4D917D685B47083E7F47A37A713C1BC64CCF9C510FBABA2C4EE73225D939ECB9E9F1443330AE6AB7225855CC26E4755953F10C2497E61A185E1521A672445F3BA065688C86ECB6E66646C66A4EE0EDEAEC262B575B78FD13331A5B675EA0
	5A27AE25A33F95481D427D71170F442B47910BCB0196DCEFA8A53CEE9E227C1814703A5954426B660950171D4263553EF244656DB5D0AB83CDG0A83CA855A3A06472FB74BCE1147544C8E59578A091FC7D7E6B079F22EFE37EF9BAB7239ED6317CCA389CFE56596C431ED769AE65332D476A4DF9E23FCA65F348A142F997A5CC0B1C031C071C06BD3351A65D08D136FA2987B3C22882E9EFA93F361EEC8E2DF080DC1014976C65BE832E524C63BEB2650EFA1D92961361BC179E4ED386D7A8635EF185067DB4B31
	B350024355EFCF3A770216312FA72A47F48FE6173272687544117248750435BCF2BD71E97968754419726875042DDCDFCFB0DCED2B44583499EC1189D43A8E62B85059F59CD329659FB1CCC54053C5A8B09AE57A85C9617B4F2A08AE6B7C0A47FFF7CA2C40FC31D3D694399757677C9595E8AFC576D1A66325D4200EA06568D158D0153F3E02662EE297E5339C63EFEDBC47F02E63381B7AA0043597F46F4303E30F3BE746F48F26074C4A683658D979F8DB1043B41F879DBA86FCA263198E53BC2F2BABA0EAB67F
	6030C56359210089C8D894242FC33DDB1CCD231CB3AF0C62E0CE9250FE357D6A3AD08CB9424EC470CC6C17F9AE922FBA8D7F6732D6E30B45605F1BF98C2E1CB505B47DAAAB779809721DAF0D453A0434D06EF0FBB853445659789FFD3DE019DCE94C0D32CE26BABFA7AD3CDE78199575F1EABD5C1FC0BE20982067006201D2015201B201763C5211DF85D487548CF49B4883B48EE862AB5CBF6FD41F00678D610EFA1BDD2477BC75D0E02AC997F5D23F4072F5F68E5347C443495CEAEB34B5404BDBA005EB6B3C83
	D50CC3685666AC30C64756926B6331354A9A594F57DA9F75738D5648FE3E592A77F3467B0D95E3DCB7486D54467DCE635D52F8DB681A483BA939159E0D7F96BD97721BC55C1073F750F375D11E135730BA5EAD15770E957D83C46E117DFC6E3BD62CDD3AA40F07350D47F10C6485F7517411FF9C4FA879845D394D9A8F4F4D7E737D45698EF84EFE1B1882FE320F275735F63AD8467DB3363CAD53730CE4171D6CE64A3513DD28A1BBB1D1855AC99EA8379FBF48634712DDAA19382F1C78C60F716C71A5D1EB6278
	8D6A7FE59B42B1DED507F6593591703B116354F76633D1B0F63DD770F89441F3773C570DF83BB0200821BB4DDA076B877898351375555461396F68667C9FD0CB87880F3E2F574C8BGGF89FGGD0CB818294G94G88G88GE1FE73AC0F3E2F574C8BGGF89FGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG868CGGGG
**end of data**/
}

/**
 * Return the JPanelVar_Watts property value.
 * @return com.cannontech.dbeditor.wizard.capsubbus.JPanelCCPointVarWatts
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.dbeditor.wizard.capsubbus.JPanelCCPointVarWatts getJPanelVar_Watts() {
	if (ivjJPanelVar_Watts == null) {
		try {
			ivjJPanelVar_Watts = new com.cannontech.dbeditor.wizard.capsubbus.JPanelCCPointVarWatts();
			ivjJPanelVar_Watts.setName("JPanelVar_Watts");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelVar_Watts;
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
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
	CapControlFeeder ccFeeder = (CapControlFeeder)val;

   ccFeeder.getCapControlFeeder().setCurrentVarLoadPointID( 
            getJPanelVar_Watts().getSelectedVarPointID() );


   ccFeeder.getCapControlFeeder().setCurrentWattLoadPointID(
            getJPanelVar_Watts().getSelectedWattPointID() );

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
 * @exception java.lang.Exception The exception description.
 */
private void initConnections()
{

	getJPanelVar_Watts().addActionListener( this );

}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CCSubstationBusPointSettingsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(360, 310);

		java.awt.GridBagConstraints constraintsJPanelVar_Watts = new java.awt.GridBagConstraints();
		constraintsJPanelVar_Watts.gridx = 1; constraintsJPanelVar_Watts.gridy = 1;
		constraintsJPanelVar_Watts.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelVar_Watts.weightx = 1.0;
		constraintsJPanelVar_Watts.weighty = 1.0;
		constraintsJPanelVar_Watts.insets = new java.awt.Insets(5, 4, 18, 4);
		add(getJPanelVar_Watts(), constraintsJPanelVar_Watts);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

   initConnections();
   
	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
   String val = getJPanelVar_Watts().isInputValid();
   if( val != null )
   {
      setErrorString(val);
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
		CCFeederPointSettingsPanel aCCFeederPointSettingsPanel;
		aCCFeederPointSettingsPanel = new CCFeederPointSettingsPanel();
		frame.setContentPane(aCCFeederPointSettingsPanel);
		frame.setSize(aCCFeederPointSettingsPanel.getSize());
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
public void setValue(Object val ) 
{
	com.cannontech.common.util.NativeIntVector usedVARPtIDs = 
			new com.cannontech.common.util.NativeIntVector(10);

   if( val == null )
      usedVARPtIDs = 
         com.cannontech.database.db.capcontrol.CapControlSubstationBus.getUsedVARPointIDs( null, null );
   else
      usedVARPtIDs = com.cannontech.database.db.capcontrol.CapControlSubstationBus.getUsedVARPointIDs(
         null,
         ((com.cannontech.database.data.capcontrol.CapControlFeeder)val).getCapControlPAOID() );
   
   if( val == null )
   {
   	getJPanelVar_Watts().setInitialValues( usedVARPtIDs, null );
   }	
	else  //val will not be null if we are using this panel for an editor
	{
		CapControlFeeder feeder = (CapControlFeeder)val;

		getJPanelVar_Watts().setInitialValues( usedVARPtIDs, null,
				feeder.getCapControlFeeder().getCurrentVarLoadPointID().intValue(),
				feeder.getCapControlFeeder().getCurrentWattLoadPointID().intValue() );
	}


}
}