package com.cannontech.cbc.popupmenu;

/**
 * Insert the type's description here.
 * Creation date: (1/5/2001 4:26:25 PM)
 * @author: 
 */
import com.cannontech.cbc.data.CBCClientConnection;
import com.cannontech.cbc.data.SubBus;
import com.cannontech.cbc.messages.CBCCommand;
import com.cannontech.debug.gui.ObjectInfoDialog;
import com.cannontech.message.dispatch.message.Multi;

public class SubBusPopUp extends javax.swing.JPopupMenu implements java.awt.event.ActionListener, javax.swing.event.TableModelListener 
{	
	private SubBus subBus = null;

	private javax.swing.JMenuItem ivjJMenuItemConfirm = null;
	private javax.swing.JMenuItem ivjJMenuItemEnableDisable = null;
	private javax.swing.JMenuItem ivjJMenuItemResetOpCount = null;
	private javax.swing.JMenuItem jMenuItemWaive = null;

	private javax.swing.JMenuItem ivjJMenuItemSubBusData = null;
	

	private CBCClientConnection connectionWrapper = null;

/**
 * StrategyPopUp constructor comment.
 */
public SubBusPopUp() {
	super();
	initialize();
}
/**
 * StrategyPopUp constructor comment.
 */
public SubBusPopUp( CBCClientConnection conn ) 
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
	if (e.getSource() == getJMenuItemSubBusData()) 
		connEtoC1(e);
	if (e.getSource() == getJMenuItemEnableDisable()) 
		connEtoC2(e);
	if (e.getSource() == getJMenuItemConfirm()) 
		connEtoC3(e);
	// user code begin {2}
	
	if( e.getSource() == getJMenuItemResetOpCount() )
		jMenuItemResetOpCount_ActionPerformed( e );

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
		this.jMenuItemSubBusData_ActionPerformed(arg1);
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
 * connEtoC3:  (JMenuItemConfirm.action.actionPerformed(java.awt.event.ActionEvent) --> SubBusPopUp.jMenuItemConfirm_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemConfirm_ActionPerformed(arg1);
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
 * Return the JMenuItemConfirm property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemConfirm() {
	if (ivjJMenuItemConfirm == null) {
		try {
			ivjJMenuItemConfirm = new javax.swing.JMenuItem();
			ivjJMenuItemConfirm.setName("JMenuItemConfirm");
			ivjJMenuItemConfirm.setMnemonic('c');
			ivjJMenuItemConfirm.setText("Confirm Sub");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemConfirm;
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
 * Return the JMenuItemStrategyData property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemSubBusData() {
	if (ivjJMenuItemSubBusData == null) {
		try {
			ivjJMenuItemSubBusData = new javax.swing.JMenuItem();
			ivjJMenuItemSubBusData.setName("JMenuItemSubBusData");
			ivjJMenuItemSubBusData.setMnemonic('t');
			ivjJMenuItemSubBusData.setText("SubBus Data...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemSubBusData;
}
/**
 * Insert the method's description here.
 * Creation date: (11/20/2001 9:48:52 AM)
 * @return com.cannontech.cbc.data.SubBus
 */
public com.cannontech.cbc.data.SubBus getSubBus() {
	return subBus;
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
	getJMenuItemResetOpCount().addActionListener(this);
	getJMenuItemWaive().addActionListener(this);
	
	getJMenuItemSubBusData().addActionListener(this);
	getJMenuItemEnableDisable().addActionListener(this);
	getJMenuItemConfirm().addActionListener(this);
}
/**
 * Initialize the class.
 */
private void initialize() 
{
	try 
	{
		setName("SubBusPopUp");
		add( getJMenuItemConfirm() );
		add( getJMenuItemEnableDisable() );
		add( getJMenuItemResetOpCount() );
		add( getJMenuItemWaive() );		
		add( getJMenuItemSubBusData() );

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
public void jMenuItemConfirm_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   //do not confirm disabled subs
	if( getSubBus() == null || getSubBus().getCcDisableFlag().booleanValue() )
		return;

   int confirm = javax.swing.JOptionPane.showConfirmDialog( this, 
         "Are you sure you want to send a Confirm " +
         "command to '" + getSubBus().getCcName() +"' ?",
         "Confirm Confirmation", 
         javax.swing.JOptionPane.YES_OPTION);
   
   if (confirm != javax.swing.JOptionPane.YES_OPTION)
      return;
	
   Multi multi = 
			new Multi();
	
      
	for( int i = 0; i < getSubBus().getCcFeeders().size(); i++ )
	{
		com.cannontech.cbc.data.Feeder feeder = 
				(com.cannontech.cbc.data.Feeder)getSubBus().getCcFeeders().get(i);
	
      //do not confirm disabled feeders
      if( feeder.getCcDisableFlag().booleanValue() )
         continue;

   			
		for( int j = 0; j < feeder.getCcCapBanks().size(); j++ )
		{
			com.cannontech.cbc.data.CapBankDevice bank =
				(com.cannontech.cbc.data.CapBankDevice)feeder.getCcCapBanks().get(j);

         //do not confirm disabled banks
         if( bank.getCcDisableFlag().booleanValue() )
         {
            continue;
         }
			else if( bank.isInAnyCloseState(bank) )
			{
				multi.getVector().add( new CBCCommand(
							CBCCommand.CONFIRM_CLOSE, 
							 bank.getControlDeviceID().intValue()) );
			}
			else if( bank.isInAnyOpenState(bank) )
			{
				multi.getVector().add( new CBCCommand(
							CBCCommand.CONFIRM_OPEN, 
							 bank.getControlDeviceID().intValue()) );
			}
		}			
	}
	
	if( multi.getVector().size() > 0 )
		getConnectionWrapper().write( multi );


	return;
}


/**
 * Comment
 */
public void jMenuItemWaive_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getSubBus() == null )
		return;

	boolean isWaived = getSubBus().getWaiveControlFlag().booleanValue();

	int confirm = javax.swing.JOptionPane.showConfirmDialog( this, 
			"Are you sure you want to " + (isWaived ? "Unwaive" : "Waive") + " control " +
			"for '" + getSubBus().getCcName() +"' ?",
			"Confirm " + (isWaived ? "Unwaive" : "Waive"), 
			javax.swing.JOptionPane.YES_OPTION);
   
	if( confirm != javax.swing.JOptionPane.YES_OPTION )
		return;


	getConnectionWrapper().write(
		new CBCCommand(
				(isWaived ? CBCCommand.UNWAIVE_SUB : CBCCommand.WAIVE_SUB), 
				getSubBus().getCcId().intValue()) );
}

/**
 * Comment
 */
public void jMenuItemResetOpCount_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	try
	{
		getConnectionWrapper().executeCommand( 
				getSubBus().getCcId().intValue(), CBCCommand.RESET_OPCOUNT );
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
public void jMenuItemEnableDisable_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	try
	{
		if( getSubBus().getCcDisableFlag().booleanValue() )
			getConnectionWrapper().executeCommand( getSubBus().getCcId().intValue(), CBCCommand.ENABLE_SUBBUS );
		else
			getConnectionWrapper().executeCommand( getSubBus().getCcId().intValue(), CBCCommand.DISABLE_SUBBUS );
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
public void jMenuItemSubBusData_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( actionEvent.getSource() instanceof javax.swing.JMenuItem )
	{
		ObjectInfoDialog d = new ObjectInfoDialog(
			com.cannontech.common.util.CtiUtilities.getParentFrame(this) ); 

		d.setLocationRelativeTo( (javax.swing.JMenuItem)actionEvent.getSource() );
		d.setModal( true );		
		d.showDialog( getSubBus() );
	}
	
	return;
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
 * @param newSubBus com.cannontech.cbc.data.SubBus
 */
public void setSubBus(com.cannontech.cbc.data.SubBus newSubBus) 
{
	subBus = newSubBus;

	if( getSubBus() != null )
	{
		if( getSubBus().getCcDisableFlag().booleanValue() )
			getJMenuItemEnableDisable().setText("Enable");
		else
			getJMenuItemEnableDisable().setText("Disable");

		if( getSubBus().getWaiveControlFlag().booleanValue() )
			getJMenuItemWaive().setText("Unwaive Control");
		else
			getJMenuItemWaive().setText("Waive Control");

               
      getJMenuItemConfirm().setEnabled( !getSubBus().getCcDisableFlag().booleanValue() );
	}

}
/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
public void tableChanged(javax.swing.event.TableModelEvent event ) 
{
	// the subbus could change, so lets have the popupmenus
	// text change along with it
	setSubBus( getSubBus() );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA8F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D98BEC9C5595B579C004D294AA37CD43C7069A64D6AE21EDD421AAEADD0B02DBA2C15BA0A264CAA9CAD55A525692D1651634FADE479FC8C89C9C288B860C921A846C75AFF15C455058DE9BA7B1459093ACE2A0635D313D766C4CE6E6766375E7FB6F7B4C0E57331B985AC8B7B36F3E776E3D6F7E5E19B5B978F2CEC5196BA0A1E595047FBBD9C6C8CB83A141561BF68BDE16DCD8C9EC7FDE81BAC076FC
	048B56415C36D50515B5642321EA986742B88746FF0777DA7269D049CB7012C6CF914C793F5AB41CD94F432F8BBDD5CCEFE4AFBC778165818F1FBBE554FFA8D24779DBB8DFD0190B108746F14DDB11FA4E6B04719050DA201CF1367EAEF8BEC9B52F76F472F5235767101DBFB962FA9A6558E48A2A1BE0FB3B783301ECAC5BB1013AA6281D384704F1BE909E3E1C949FF6637A21690937FCDA506D13D4D5D3CD59B769760D795CA1AD948E85E5B56C711C8B0F75040DA1AD74C108100B13A8F3E3241A4AA064A80C
	9F233C9797D0CFAE3CEF865AEA71F7D0FECB2030F2G68E2C01C6D4DA3DD5C7611590DE453C3EA797EB4E70B855819F2824933E5077FD2758F9A07671B505F45B0C7268AAB8F82F5820DG4500DE2071FF2571B5F8F60F193DDAB028295D21D00F62575F517C32826F9E8F4CF05E7B32CF537D04309887A30D8E76E4A236EFF6597B18CF725AC53C735F3F1D94FF672C2B4A41A745F9AD2EF5D34C97F9D3C91FB05D1B526AAE1ADA1C0F0BF5FFC86C3AF32F564AAB9CF4EFFA70182B184BA93169EE1846D8DFCFE3
	DD836FE5BCBF987F864A775B78ACB78FD1BE0ECD185BB6ADF2E3E41F30E5F07786D253BA68680775055D2EAE0559B02C245AB212C059D98A53D9E55962277C75B6BE33653754969C5F89F387030515AC3F8EB5E32D46E01C8D148FF4B7508EA02242BAD56458D4E37395665810241BBD61006217F542769EEAFA8E1EF24C5425532AE1CA2ACFBEAF6981E9CC118D4257B4B621EDE30B7635525C77010E37E5D556A5B32029A0DD8A4A262C9BEC0D2769G2E51E5BABB9C884A20A088254CE574B45542D3118C7303
	10DFB265648AB6FF2689E392114E03C490G6F4CAFC3AD682F63F07EC120D14B8FBF22724E4A3AAAAB9E4F2026CD07C343F892122F221F6F233DA3846FEBD4616341D664154038DA93F2F6B5BDE313638D7850FCC91F91B57615856C47751A4819BF0C081C71BEFD9BE9FC700CE34EA09D57D82E8CEA2935F38B1509F63171310511E556F13D66DCE369FBC54F32F514AFE3AD736F775AB0D6A658368E68CE4B47A75D8DACE7FBA4C396D97A2E8E89A67B139EE7F6FE129077886A2F306C1F227CA210D982D49792
	3EAB2167322FDF4E992F4CF7CFA6C6D26C79DF6AE0BEEAEA46FBC90E482A6915C3326A0797034F9DEF465C63F5BB2ED9FBBC149F02BA6F0542B2AC7E29E62CC549B47540D858144F492699D0A7127383D417919E8BA5DB045075EEB31ED10BC21DDB6B071B31C6FC1A2A4AB4D85696BEFF1E56282C48F013C3DF8A2BA6630F36E01ECF4BB33CC7DCECC140931114B0769B068F6E1476609974C090D2GDB96498EE17DBEE2619B24D340C31CB3894F3910284568A1D10B0763371002CFDFF5B95DA56BEF733A0AC3
	7CAE89A5EB115974DE2B336D9F52DE4179C6B9653D11C0DB6DC005657D636D74BE3AD4D8F998286B12487B1F37623C68ADBC61FBB5C5E1EE347CDB50D6037110AF05E5E84958444EDABE3B376D191439735CFFEC7E7EB634C931753FFA9D7DF7D3C2E03FE130C781DAFB0979F04BA5668336CEA2E25DA40C3374643A9C5D3E6E59B231EEA70C779A05150D86DB2798EC9D0BE7E59D2E23F8EFCA0AC8B13791055C73F84E8CA154FB87209E43F603D1016D6EB28406AB08219FCBE0DCED96D6B618A24EBFFDDAE038
	6C699C327DEB4F2745271DA633E554345D53AB8506FB73846A3C916672CD9123B36D76FB245BB0B4DF00DEE15C3FFFEE376781637D2D7DA9F80E87F443BC8D059A933158FD9C6D45BB0E33791DFF8F6DDD681F8250DB8A2404117F785CC11B6DADE1F6D70D401365DB93E943BA264962E60FA1D99F5774206CBF0A5EF5CBD153CDFB063B1BCE77617BB1910B0E7A9EF8CE2153CF1BF210A52ED7B225CF3BD3048D261776463173823B5DDAFB0A5B3A650E8D24ABD16BF24252EB763F685A94E67EAF8EA763B0776D
	4A7247BEF9220A7964DAA2FCF2B482F8BDA2FC72500C13CF56C744774444CBFD76B3752918735E00010FE59F0B47672DB2E1CBBE6859E15972542C13AD75D1911FBD651FAFBEF7387FEDBFCB2F26C2CE8517FB8AF6C73D97CD4563AD514C98016154AF4E60593D51D47CFAB3650FC6D3716BBDB101DFCD185B969558EACB87766B2E9864AFD088E88DD09ED0D1CC545A5A636D64B25875B4FC9B6A2A241CDBF4B9898CF9827D8E77899F771FC0BF527B04083BFACBA2558FBBE20B7DC0E6960F593E07176CFBA9E5
	FCB8E59F0BE1D1458B4E7DCEC415FF4B3EB6DF4DF5F54D083EB65FEF6FEB1B773739762469EBA6571DBD132C27C66B5BB45209BDF7AB4C15GD547619E829AG72832D098B7FBFF0624D4B791FE65530568B3C8138D3FAD36FFC4BDF6B48E773734DCB76257AB9AF3EF89F735F3E3870DFC1DCE03FEF38BE1B8DF7AD5917EAC329238D5D3342061AD938C368F746A43D4786E0BCBAAB6CDACFEBA78B4639C08596BF1771797D09F7B24F59D8A6ECAE64DC8916ACF0D847F47CAA230E65E0E7A77C4B726B410E7498
	3638439943BE52610CE19F6DB84078A0437EA5AB66CBBAB2E35C528EA70C7BA2BD6B0F1F5BCFE59B06EC500447BB114F7D5AFC16B8E05D7F4047AE77D3AD7C7FA4DF53E7EA3D2D160F1B67D373EABB44E3A70FC9755C5258307A7D6B3F701E290779CE20A120C920B5F322EE2FBBF9593E59974B50B733BA5347651ACE6738FC2193B1162105F51F1C6A7B6C4EBAEBAE59A13059211B581A1B68FED352A764A5314B257B59DC3A7D5F6CCC97DF9603FB9745204D0AC1EDBCB586F960474D5C778FB84400497B254B
	AE2F5D1257BF3B2447000CD2AEE76FFCBAF909A8FE6BA8520C96B689094DE32F78F2D4FC53B44038136F3DB84FF0855B77A795FF23B96308EF046CDDCA0DFD8D478DEC6E785B357639A466633F0B7453FB28GF4EE1D97BD6CDA4AFB944625966FF71437FB81704E0248438ACF7ABC5A6BF14E23E7BC4E75DD6749DC5F8D1EF471E7763D969767B835A0ECB662629C4396EFDBDC1C6322457B187671D5B0DEEF71C66710178F634D966F07F3E2EF919043BF5F0F0A5FA79E835E610440256DEDD84B968EC0386671
	74ABDAF41027GE15FB8CF507D23304F84DA1B90D8A076CF018572EF5FC086334EBAFE63EC9FEEF365A6D85E94A512D8A08F30G0BFFB7BDEFD01521D184FC8733D027C6E3799BBCCB164833EB6378EDEEC240C632706F90273A518AF616EFC591070C6BD290DCFEAEA47B480DD40EEB210EB85F89C8CC5788557F8B2C9398B359BE7C8C61B27E984FA40359E64EAEBDC3B70D453DC0BB2DB398F30B4FF0C3EFD24F3639CCFAD8ED5F3D84B3DF495DF7007CEC597B982656A8F65EFD954354E596BFC679B5B6BE4B
	3532184835FA184B3ACAE06D85AF1EED870C490A424ABA209620A1A0E50528556FFADA48E56E0CE1ADB488452C584A0F79756AF9FC9EB10F706F3E1C287D6FC31DCF1DF47C1D05E215959C27726766EB8EF19977DF304B2859700A2BAC0D0CADFC6F3695497CE6B20ABE96B5326B3EBC52707AEE479AA97D385935F7A55B5B32B2A9E36B54AEDEA3DA1C1ECFBD4273EDE40E0EFDC7F8FC4378694E5F97EC6FD599B15467A52623617F2A035F795E74FD7CBA2FF39F4F76BA77718D5E4CB86CD6EF7A5F9A3F6E357F
	5648F25B6847BA77C35C32D60ADC76743D8E4F7352944F62BEFF4054F41A46409C5235102C1BB30CCBFF9D6332543709E5C7D65F2B69E4C0A91C01167D6E5894G11E1E94232E575G56D98E583199E8BBD0A9505ED55077D7D9FF0768BB1A41B6567C6102B6401F81715BE22CFF1F5DE74CE4EEE7229F31E0C08D1881C9894469CFD167E021BA4163B310BEF6DF9A387C5FF7EE9E405F2E46B4535402FD7E89CBEF6EG7D2657C2144963B300B157839313A6DB4A799B87B0C60ABCCE596CEF5FBF98411E187FFB
	D2E112FD8CF5B4A06CF03B5DA629E4B046A4FBB8D6E900952C260FA56CFD21F4F5DD5A5ED23F1A57746AE4CD33B839AED0FB003FE635084B7B9FB5E50877392024A82624CCF72BFE0CFD4F0CA9A7F3366A824AB7449ABE496676DDC03B27ECFB7E8BD0CB8788142B332A708BGGD8A0GGD0CB818294G94G88G88GA8F954AC142B332A708BGGD8A0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGAA8CGGGG
**end of data**/
}
}
