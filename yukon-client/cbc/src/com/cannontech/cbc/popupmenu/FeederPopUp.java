package com.cannontech.cbc.popupmenu;

/**
 * Insert the type's description here.
 * Creation date: (1/5/2001 4:26:25 PM)
 * @author: 
 */
import com.cannontech.debug.gui.ObjectInfoDialog;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.Feeder;

public class FeederPopUp extends javax.swing.JPopupMenu implements java.awt.event.ActionListener, javax.swing.event.TableModelListener {
	private Feeder feeder = null;
	private javax.swing.JMenuItem ivjJMenuItemEnableDisable = null;
	private CBCClientConnection connectionWrapper = null;
	private javax.swing.JMenuItem ivjJMenuItemFeederData = null;
	private javax.swing.JMenuItem jMenuItemWaive = null;	
	
	private javax.swing.JMenuItem ivjJMenuItemResetOpCount = null;
	
/**
 * FeederPopUp constructor comment.
 */
public FeederPopUp() {
	super();
	initialize();
}
/**
 * StrategyPopUp constructor comment.
 */
public FeederPopUp( CBCClientConnection conn ) 
{
	super();

	connectionWrapper = conn;
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
	if (e.getSource() == getJMenuItemFeederData()) 
		connEtoC1(e);
	if (e.getSource() == getJMenuItemEnableDisable()) 
		connEtoC2(e);
	// user code begin {2}

	if( e.getSource() == getJMenuItemResetOpCount() )
		jMenuItemResetOpCount_ActionPerformed(e);		

	if( e.getSource() == getJMenuItemWaive() )
		jMenuItemWaive_ActionPerformed( e );
	
	// user code end
}
/**
 * connEtoC1:  (JMenuItemStrategyData.action.actionPerformed(java.awt.event.ActionEvent) --> StrategyPopUp.jMenuItemStrategyData_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemFeederData_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JMenuItemEnableDisable.action.actionPerformed(java.awt.event.ActionEvent) --> StrategyPopUp.jMenuItemEnableDisable_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemEnableDisable_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/5/2001 4:45:07 PM)
 * @return com.cannontech.cbc.CBCClientConnection
 */
public CBCClientConnection getConnectionWrapper() {
	return connectionWrapper;
}
/**
 * Insert the method's description here.
 * Creation date: (11/20/2001 9:48:52 AM)
 * @return com.cannontech.cbc.data.Feeder
 */
public Feeder getFeeder() 
{
	return feeder;
}
/**
 * Return the JMenuItemEnableDisable property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemEnableDisable() {
	if (ivjJMenuItemEnableDisable == null) {
		try {
			ivjJMenuItemEnableDisable = new javax.swing.JMenuItem();
			ivjJMenuItemEnableDisable.setName("JMenuItemEnableDisable");
			ivjJMenuItemEnableDisable.setMnemonic('n');
			ivjJMenuItemEnableDisable.setText("Enable");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemEnableDisable;
}

/**
 * Return the jMenuItemWaive property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemWaive() 
{
	if (jMenuItemWaive == null) 
	{
		try 
		{
			jMenuItemWaive = new javax.swing.JMenuItem();
			jMenuItemWaive.setName("jMenuItemWaive");
			jMenuItemWaive.setMnemonic('w');
			jMenuItemWaive.setText("Waive Control");
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemWaive;
}

/**
 * Return the JMenuItemResetOpCount property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemResetOpCount() 
{
	if( ivjJMenuItemResetOpCount == null ) 
	{
		try 
		{
			ivjJMenuItemResetOpCount = new javax.swing.JMenuItem();
			ivjJMenuItemResetOpCount.setName("JMenuItemResetOpCount");
			ivjJMenuItemResetOpCount.setMnemonic('r');
			ivjJMenuItemResetOpCount.setText("Reset Op Counts");
		}
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return ivjJMenuItemResetOpCount;
}


/**
 * Return the JMenuItemStrategyData property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemFeederData() {
	if (ivjJMenuItemFeederData == null) {
		try {
			ivjJMenuItemFeederData = new javax.swing.JMenuItem();
			ivjJMenuItemFeederData.setName("JMenuItemFeederData");
			ivjJMenuItemFeederData.setMnemonic('t');
			ivjJMenuItemFeederData.setText("Feeder Data...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemFeederData;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------" + this.getClass());
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception 
{
	getJMenuItemResetOpCount().addActionListener(this);
	getJMenuItemWaive().addActionListener(this);
	
	getJMenuItemFeederData().addActionListener(this);
	getJMenuItemEnableDisable().addActionListener(this);
}
/**
 * Initialize the class.
 */
private void initialize() 
{
	try 
	{
		setName("FeederPopUp");
		add( getJMenuItemEnableDisable() );
		add( getJMenuItemResetOpCount() );
		add( getJMenuItemWaive() );
		add( getJMenuItemFeederData() );
		
		initConnections();
	}
	catch (java.lang.Throwable ivjExc) 
	{
		handleException(ivjExc);
	}

}
/**
 * Comment
 */
public void jMenuItemEnableDisable_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	try
	{
		if( getFeeder().getCcDisableFlag().booleanValue() )
			getConnectionWrapper().executeCommand( getFeeder().getCcId().intValue(), CBCCommand.ENABLE_FEEDER );
		else
			getConnectionWrapper().executeCommand( getFeeder().getCcId().intValue(), CBCCommand.DISABLE_FEEDER );
	}
	catch( java.io.IOException ex )
	{
		handleException( ex );
	}

	return;
}

/**
 * Comment
 */
public void jMenuItemResetOpCount_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	try
	{
		getConnectionWrapper().executeCommand( 
				getFeeder().getCcId().intValue(), CBCCommand.RESET_OPCOUNT );
	}
	catch( java.io.IOException ex )
	{
		handleException( ex );
	}

	return;
}

/**
 * Comment
 */
public void jMenuItemFeederData_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( actionEvent.getSource() instanceof javax.swing.JMenuItem )
	{
		ObjectInfoDialog d = new ObjectInfoDialog(
			com.cannontech.common.util.CtiUtilities.getParentFrame(this) ); 

		d.setLocationRelativeTo( (javax.swing.JMenuItem)actionEvent.getSource() );
		d.setModal( true );		
		d.showDialog( getFeeder() );
	}
	
	return;
}

/**
 * Comment
 */
public void jMenuItemWaive_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getFeeder() == null )
		return;

	boolean isWaived = getFeeder().getWaiveControlFlag().booleanValue();

	int confirm = javax.swing.JOptionPane.showConfirmDialog( this, 
			"Are you sure you want to " + (isWaived ? "Unwaive" : "Waive") + " control " +
			"for '" + getFeeder().getCcName() +"' ?",
			"Confirm " + (isWaived ? "Unwaive" : "Waive"), 
			javax.swing.JOptionPane.YES_OPTION);
   
	if( confirm != javax.swing.JOptionPane.YES_OPTION )
		return;


	getConnectionWrapper().write(
		new CBCCommand(
				(isWaived ? CBCCommand.UNWAIVE_FEEDER: CBCCommand.WAIVE_FEEDER), 
				getFeeder().getCcId().intValue()) );
}

/**
 * Insert the method's description here.
 * Creation date: (1/5/2001 4:45:07 PM)
 * @param newConnectionWrapper com.cannontech.cbc.CBCClientConnection
 */
private void setConnectionWrapper(CBCClientConnection newConnectionWrapper) {
	connectionWrapper = newConnectionWrapper;
}
/**
 * Insert the method's description here.
 * Creation date: (11/20/2001 9:48:52 AM)
 * @param newFeeder com.cannontech.cbc.data.Feeder
 */
public void setFeeder( Feeder newFeeder ) 
{
	feeder = newFeeder;

	if( getFeeder() != null )
	{
		if( getFeeder().getCcDisableFlag().booleanValue() )
			getJMenuItemEnableDisable().setText("Enable");
		else
			getJMenuItemEnableDisable().setText("Disable");

		if( getFeeder().getWaiveControlFlag().booleanValue() )
			getJMenuItemWaive().setText("Unwaive Control");
		else
			getJMenuItemWaive().setText("Waive Control");
	}

}

/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
public void tableChanged(javax.swing.event.TableModelEvent event ) 
{
	// the feeder could change, so lets have the popupmenus
	// text change along with it
	setFeeder( getFeeder() );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GDCF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13599EDEC944755A1A4C1EA54520AC8087243CDCCE4D5A8C053C629E43548C9D5B7B1E9521014C4EEC29B24FC0834D435D2CAD1B5BE6343C98818E0D2B70184148F20F18C319D639E46780C4D07B90C8B8E31DA92D85F2D4D59FB3BFB3BF3BEFFE05C7766E3EFB9760E12A8A7BD4D4E1B19775E3C6F5DA3556F64D614792A89A92BA062373F0C10695B8929F8FD6BB2094BD18B7D44755B8930195CCD5641
	B89356EA27957A2B4842E4A54C73F0AF4C7F8E4FDB480F12BD5B60A1831F5D30663FD0D6121D4F398649E7BD1BCF46B6403891A09FF0B8162A487F36D80D40978B3C04B29FA18F8FB099E35B842E9966BDG3782648E707D774278B263FCD320D96CBB776DDCD27A40AE5FEBC847C5D3C24DA0BF5BA2469DE4C1590AC164758413934FD0184F87404B1793FB2A8E77778C8F36060DD8DDD851F5C327EA78E2DD38BFDCE79AE6420C29FAA290B8222A91556AB14CCFCDC2AEDCC41ABB75CAC683F48773158C779F0A
	FC726039GE029035F19C07C3EE8217F984005283C5B41DDADC2763E0985E47A2F7572CD996EB69A65F74A0D266EB6AB31E67DBF189D66B6223ECBE00D8C957A2B811A817AGC6815EE2763F23611FB0867B69E1A396B37420E936EB916B2491D1B5F88E84E0C560CE2BE1430A9042ED7CF42C56C31EEC404F3DF85D6738CE729B243D671F3E13145CFD42375EC3A7A5797BFCB30738AE7207D2BA613CBF50B371AE9A3A529F2F647D99F17326B79B65959E3CFF78589EDF0920334A45FB47B05A7AA16683D570DC
	A67C036317B1FC4405673EF90161F1CEE1ED5930740D3E0DD216503AB9246A6310279EE69786FDAD9A1721D7CB1765B666F7B99A67D96148124B7033DCF8AE4BAE0647F9BE2CED08957A39FF4DEB42D8B5E1FEABC09EC081C08940EA008D3A7431158DFB7FCF9F6BD1AC5A1E08EA90D5041F1D5778860C6AA83514633ACD95BD2C1ED52C28522F29B691FB6EEAC49D74DFF16EEE667BE160F1D455D5CB21D1C3876ACACC252AE573BD850D1BF10F2532555EE8CC8586B188E5C1637E46ADB0EA0ACDBFB5A38AD5D3
	BB78FAF1A35AE4C4B98B9401GBCF33D7CEABF6A2B966E5F81506B686168A7C86F04EA692A9688048CE3B8E17662CDC81E0EFABE48F247C5F84E51250E9FEAC65CF3B0A70624F3791357DDF4BA22E194DF3146E40CAD25180F2B8D69B33F6F13BE53715ABCD27BD817274FA0549A5CD7BA0C7458F90A51C43978FC0076E50959EA43BB1E2EAFD7CCE565D17E9568EFF4EC5592C0DBE9A06F8C00FCC747BF7EE4877759F645D625171E32404154C8CA63DC4E6FA6AA05BE4B9D596FE43845C06FB9C36A6C23C41FEB
	5FD711BD334E0EB09EC83F62EB6041F53320896B11BA226A34C3B5D5BD828F36D83B2789FDCE446B00611CA9E0F893627BB084146D608B1BB08695CA2DE8FF022AE7D4CA237AE0EA7D81464BCE741B2954A0F9BD5204F7B412905F4E7E471BB0B642062E2B4CC84E91313E0C45262A29D041A19FA5F44A71CF6DC77F9ED647C4EE785DFEECF4C694AD01F90677858B15D7718ED6B4862647D4C5A2A646ED2FDE6352D19560303FE906B1E44A98CC3EABE3703D716F1145675F77F955105973BAFC73CD9EBB0B4DD4
	8CF2199EED76167D8916A3845EAEE738691F232C6E86057BFB62G52D88E34CB81B644253FFF7AAFDC17B9C5B87AE1C353389A9D7D36872A509EEABC21C2AA4664F542515971406BE9EBE7057E787A3F832F32D8CB653D15F1D683933267FB8F66ADG6762DC07F363DC87FC5F5C9127B73420B734D27BCC0B6F63F6725760BE563F8DA9A34AE81D1D841F8A843AFA30F5BB892D9B6F55FE6274EA77DB32A72BB7D1BFEBE1DEEDC35FECCB7BBD721A6C49E68D671265F73C19315F6C3039AC97EDD75D3DD176E4BF
	EDC11E7930F62FADF5DF9CF0570520ED9B61A8ABC9C2EF93875C7665381C832F40B89035ECFA9C82F0D46A786683A8AF56AC0196B53CECC45670E2603B0EC28FC891BFEED53BE4EF213C76C4E064FEF4F1249E6313B9C19D9FFAD4EB4030E2EAE4B7EA37CEC952BA168B6A02EC39931F77C8DBFC63C8BB0CC3287463D40DF1076CD028F2BE18C6AC14195A01BDE7E5AFF67B16B6A1EB71DDF3C84B54961FD7EFBCE353F65FE24A757F9CCD5961528F7C65AB3F786BFA2E131FB9BAE9CC947A35045449513817CE72
	92727DE0706DCE771DBAF574650E280D43F5DFCB646D7B2E6ACD6FCDE42F213CE7DB9747BB94A752FB39CD8C3FBC115E4BB51B724E253056B7A27B0C6ED64CE185B0DF86308A20B2D9685F8DF0ACA97DF4E3CB8339C69FF79C5E13ACDD514EDC1130E5BF55D24EF32C189735D4489C6B68E17DC83A9EA249AB751013B6676736DEF56E6D34F9DE5AB9EE4B220A373CF305342EF82FDBE4D58ADE85C91993A60F38F3C24126FA5F2B99F2C22960DDBD1A7245DA67BDED75A14CD73D30E6824CG4883A882D881D0B9
	A675FF7A6041EB691F79D52FF198F047A04F9ECE2F030E3E9E7C12FAFE621AFA5EBDF665B92E3F4BF1293FB6D8677D6E13AC7F0E427C56B159A33D4276AD0479D287371561B05FE3ED6038D6B3DBEF38F06C7A7B4F252EB31C47E7D9F9FC199E4E5D13F11BFE561A39177A22553B170A34FA77D2C32D1BB99EE8385F24643A551A3D579AEF756A3536333B7E620DCD0C36ED2BB6F3B24C61E26DD93A15F874DC5D7032A5743405340E3CAC76F4D26370470E0E6F31527D29946CD0B94EED31ED7CEABB7218198AE2
	0E5E876BA1008840A5003963B2D62A8EDDB3D7F50EE649D53E364CF6F9394D5BAE1B5A30A7300D0495D653F57FC6DB0D33160ACACCB028A63E679DF61EAA5620FA156DBEE06779DA2673FB5BB259175B4017F45B205E31410D71F49B94009E9F963ADF61E183CE6F0FBA76481AB2E6A4A844678446F425A1FB6155F02EEA021FED1E60B5141FFBC947F77AAE4F7AA9E95F125C625E136A9B443B7292160FE7835D7993B21F7C5CE66FDCB0FF58416D2608AB01F96904740DDB0219ED7B4D2037EDE7853DE3EECEB0
	FB4C5D9E4CE493AE5F1C383CC74DA5A973B3F1F90FDA87F74C1277E8869C2F657B8C797D729C60E6DD127DC9C3BD4608D353307988840EE8C6B2A4CCC5F82F9BE3670B119E405ACB322E0DFEA06B5A7CBB6710D04E894FDEF7F9EF3D2FFAD258F7B2D55772212EF19BAEE2770869AA84E0B4BCFD92F93E446E71BC7A72247407B5F1FC7722207422C97C3E3C6498A132BEAFCAE2CD9D301498286D0C290649064BC867902DA11973AC67D544787F8620974EF05A2B0EA3EDCE633C150D860F0DA6634BFD676C386E
	F33CEF7BAD33C964B26F5B4A9C7C8B8CEF3A705C87EC4787AE415A724B321FFB348B6F56B1D5683FGB08AB093E0A1C071140C036676FD649AB93257B0C390A81A4B35394D6EE36F193B68AE511767A65D5F631BDF596F79FE4B7CE6CA7CA7A04602DB5E95B4FE79DFB70D2AB9BBFDE599E85466883F4BC9799D27D17439745D358F66139D9F2E73745D551F6F75E9626CCC970D25C3EB056FBE9DE77753F789FBBE0B29C0BC2FF2BD0F9959FA042F8A1C47F73ED69E5C97CA3AB26744E73A3CF36273DD5EB9718F
	DD597B0CBFF7E57E267337AE77B79D6E4BC5A10C637B41CE4BF3246FFE6B4407B01ED5060457F6C6225430185B823247B2CC5522E39C4B3EC288DA771DA40E9C0F1DF8BF938DF07DAEC81D277A07205076AA030EACCFF6E3DC119B8A7D5BG5AG7AGB40069B7C8595E8C6D4EA29BCF42D043ED50E7D4FE43D9925A68569997D94839B4043DCED40F522822C547592701B330D19F9476694EEC3B973A2F7D7D7CCF5D78AD215F205408F5C6869D3EFF69C67F2306491042BE5DE8F3ABBAF811722D823F6E945AC8
	D387981A7F37B8B4056F8673DFA49514EC6495FC8716703A3ABACA7C3C6C4B777ABDD36E58DFFDD3CD467CE1CE6371BAFDDAAAEE39ED3EDF466AA660CB26C9DB2CA19FE53171191822E9D4510603FA846D5DBEC655141FAEA8C37A365CA39645F7CE16EF06DCE77E87D0CB8788C4F0BDF2AF8BGG2C9DGGD0CB818294G94G88G88GDCF954ACC4F0BDF2AF8BGG2C9DGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE98BGGG
	G
**end of data**/
}
}
