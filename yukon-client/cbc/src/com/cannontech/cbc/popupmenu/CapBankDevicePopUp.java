package com.cannontech.cbc.popupmenu;

import javax.swing.JSeparator;

import com.cannontech.cbc.capbankeditor.CapControlEntryPanel;
import com.cannontech.cbc.capbankeditor.CapBankTempMovePanel;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.debug.gui.ObjectInfoDialog;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;

/**
 * Insert the type's description here.
 * Creation date: (8/25/00 10:36:10 AM)
 * @author: 
 */

public class CapBankDevicePopUp extends javax.swing.JPopupMenu implements java.awt.event.ActionListener, javax.swing.event.TableModelListener 
{
	private javax.swing.JMenuItem ivjJMenuItemConfirm = null;
	private javax.swing.JMenuItem ivjJMenuItemEnableDisable = null;
	private javax.swing.JMenuItem ivjJMenuItemOpenClose = null;
	private javax.swing.JMenuItem jMenuItemAckAlarm = null;
	private javax.swing.JMenuItem jMenuItemTempMove = null;
	private javax.swing.JMenuItem jMenuItemMoveBack = null;

	private javax.swing.JMenuItem jMenuItemCapBankData = null;

	private Feeder ownerFeeder = null;
	private CapBankDevice capBankDevice = null;
	private javax.swing.JMenuItem ivjJMenuItemManualEntry = null;
	private CBCClientConnection connectionWrapper = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CapBankDevicePopUp() {
	super();
	initialize();
}
/**
 * CapBankDevicePopUp constructor comment.
 */
public CapBankDevicePopUp( CBCClientConnection clientConnection ) 
{
	super();

	connectionWrapper = clientConnection;
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
	if (e.getSource() == getJMenuItemOpenClose()) 
		connEtoC1(e);
	if (e.getSource() == getJMenuItemEnableDisable()) 
		connEtoC2(e);
	if (e.getSource() == getJMenuItemManualEntry()) 
		connEtoC3(e);
	// user code begin {2}

	if (e.getSource() == getJMenuItemConfirm()) 
		jMenuItemConfirm_ActionPerformed(e);

	if (e.getSource() == getJMenuItemAckAlarm()) 
		jMenuItemAckAlarm_ActionPerformed(e);

	if (e.getSource() == getJMenuItemTempMove()) 
		jMenuItemTempMove_ActionPerformed(e);

	if (e.getSource() == getJMenuItemMoveBack()) 
		jMenuItemMoveBack_ActionPerformed(e);

	if( e.getSource() == getJMenuItemCapBankData() )
		jMenuItemCapBankData_ActionPerformed( e );

	// user code end
}
/**
 * connEtoC1:  (JMenuItemOpenClose.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankDevicePopUp.jMenuItemOpenClose_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemOpenClose_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JMenuItemEnableDisable.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankDevicePopUp.jMenuItemEnableDisable_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC3:  (JMenuItemManualEntry.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankDevicePopUp.jMenuItemManualEntry_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemManualEntry_ActionPerformed(arg1);
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
 * Creation date: (8/25/00 10:40:12 AM)
 * @return com.cannontech.cbc.CapBankDevice
 */
public CapBankDevice getCapBankDevice() {
	return capBankDevice;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 3:41:20 PM)
 * @return com.cannontech.cbc.CBCClientConnection
 */
public com.cannontech.yukon.cbc.CBCClientConnection getConnectionWrapper() {
	return connectionWrapper;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getJMenuItemAckAlarm() 
{
	if (jMenuItemAckAlarm == null) 
	{
		try 
		{
			jMenuItemAckAlarm = new javax.swing.JMenuItem();
			jMenuItemAckAlarm.setName("AckAlarm");
			jMenuItemAckAlarm.setMnemonic('a');
			jMenuItemAckAlarm.setText("Ack Alarm");
			jMenuItemAckAlarm.setActionCommand("jMenuItemAckAlarm");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemAckAlarm;
}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getJMenuItemTempMove() 
{
	if (jMenuItemTempMove == null) 
	{
		try 
		{
			jMenuItemTempMove = new javax.swing.JMenuItem();
			jMenuItemTempMove.setName("TempMove");
			jMenuItemTempMove.setMnemonic('v');
			jMenuItemTempMove.setText("Temporary Move...");
			jMenuItemTempMove.setActionCommand("JMenuItemTempMove");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemTempMove;
}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getJMenuItemMoveBack() 
{
	if (jMenuItemMoveBack == null) 
	{
		try 
		{
			jMenuItemMoveBack = new javax.swing.JMenuItem();
			jMenuItemMoveBack.setName("MopveBack");
			jMenuItemMoveBack.setMnemonic('k');
			jMenuItemMoveBack.setText("Move Back");
			jMenuItemMoveBack.setActionCommand("JMenuItemMoveBack");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemMoveBack;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 5:13:20 PM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemConfirm() 
{
	if (ivjJMenuItemConfirm == null) 
	{
		try 
		{
			ivjJMenuItemConfirm = new javax.swing.JMenuItem();
			ivjJMenuItemConfirm.setName("JMenuItemConfirm");
			ivjJMenuItemConfirm.setMnemonic('c');
			ivjJMenuItemConfirm.setText("Confirm");
			ivjJMenuItemConfirm.setActionCommand("JMenuItemConfirm");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return ivjJMenuItemConfirm;
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
			ivjJMenuItemEnableDisable.setMnemonic('b');
			ivjJMenuItemEnableDisable.setText("Enable");
			ivjJMenuItemEnableDisable.setMinimumSize(new java.awt.Dimension(75, 21));
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
 * Return the JMenuItemManualEntry property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemManualEntry() {
	if (ivjJMenuItemManualEntry == null) {
		try {
			ivjJMenuItemManualEntry = new javax.swing.JMenuItem();
			ivjJMenuItemManualEntry.setName("JMenuItemManualEntry");
			ivjJMenuItemManualEntry.setMnemonic('m');
			ivjJMenuItemManualEntry.setText("Manual Entry...");
			ivjJMenuItemManualEntry.setActionCommand("JMenuItemManualEntry");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemManualEntry;
}
/**
 * Return the JMenuItemOpenClose property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemOpenClose() {
	if (ivjJMenuItemOpenClose == null) {
		try {
			ivjJMenuItemOpenClose = new javax.swing.JMenuItem();
			ivjJMenuItemOpenClose.setName("JMenuItemOpenClose");
			ivjJMenuItemOpenClose.setMnemonic('o');
			ivjJMenuItemOpenClose.setText("Close");
			ivjJMenuItemOpenClose.setMinimumSize(new java.awt.Dimension(69, 21));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemOpenClose;
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
private void initConnections() throws java.lang.Exception 
{
	// user code begin {1}

	getJMenuItemCapBankData().addActionListener(this);
	getJMenuItemConfirm().addActionListener(this);
	getJMenuItemAckAlarm().addActionListener(this);
	
	getJMenuItemTempMove().addActionListener(this);
	getJMenuItemMoveBack().addActionListener(this);
	
	// user code end
	getJMenuItemOpenClose().addActionListener(this);
	getJMenuItemEnableDisable().addActionListener(this);
	getJMenuItemManualEntry().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() 
{
	try 
	{
		setName("CapBankDevicePopUp");
		
		add( getJMenuItemAckAlarm() );
		add( getJMenuItemManualEntry() );
		add( getJMenuItemEnableDisable() );
		add( getJMenuItemConfirm() );
		add( getJMenuItemOpenClose() );

		add( new JSeparator() );
		
		add( getJMenuItemTempMove() );
		add( getJMenuItemMoveBack() );
		add( getJMenuItemCapBankData() );
		
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
public void jMenuItemAckAlarm_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	java.util.Vector data = new java.util.Vector( 2 );  // we are only sending 1 ack event and the token
	data.addElement( new Integer(-1) );  // this is the ClientRegistrationToken
	
	//add the pointID
	data.addElement( getCapBankDevice().getStatusPointID() );
	//add the ACK_ALL reserved value instead of the AlarmCondition
	data.addElement( new Integer(Command.ACK_ALL_TOKEN) );

		
	// Sends a vangogh command message to capcontrol, which then forwards the exact
	//   message onto dispatch(vangogh)
	Command cmd = new Command();
	cmd.setOperation( Command.ACKNOWLEGDE_ALARM );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new java.util.Date() );

	getConnectionWrapper().write( cmd );
	
	return;
}
/**
 * Comment
 *
public void jMenuItemClearAlarm_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	java.util.Vector data = new java.util.Vector( 2 );  // we are only sending 1 ack event and the token
	data.addElement( new Integer(-1) );  // this is the ClientRegistrationToken
	data.addElement( getCapBankDevice().getStatusPointID() );
		
	// Sends a vangogh command message to capcontrol, which then forwards the exact
	//   message onto dispatch(vangogh)
	com.cannontech.message.dispatch.message.Command cmd = new com.cannontech.message.dispatch.message.Command();
	cmd.setOperation( com.cannontech.message.dispatch.message.Command.CLEAR_ALARM );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new java.util.Date() );

	getConnectionWrapper().write( cmd );
		
	return;
}
*/
/**
 * Comment
 */
public void jMenuItemTempMove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	java.awt.Frame pFrame = CtiUtilities.getParentFrame(this.getInvoker());	
	final javax.swing.JDialog d = new javax.swing.JDialog( pFrame ); 


	CapBankTempMovePanel panel = new CapBankTempMovePanel( getConnectionWrapper() )
	{
		protected void disposePanel()
		{
			d.dispose();
		};
	};

	if( panel != null )
	{
		panel.setOwnerFeeder( getOwnerFeeder() );
		panel.setCapBankDevice( getCapBankDevice() );
		d.getContentPane().add(panel);
		d.pack();
		d.setTitle("Cap Bank Temporary Move");
	}

	d.setLocationRelativeTo( pFrame );
	d.setModal( true );		
	d.show();
	
	return;
}

/**
 * Comment
 */
public void jMenuItemMoveBack_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	try
	{
		getConnectionWrapper().executeCommand( 
			getCapBankDevice().getCcId().intValue(),
			CBCCommand.RETURN_BANK_TO_FEEDER );
	}
	catch( java.io.IOException e )
	{
		handleException(e);
	}

	return;
}

/**
 * Comment
 */
public void jMenuItemConfirm_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	//do not confirm disabled cap banks
	if( getCapBankDevice() == null || getCapBankDevice().getCcDisableFlag().booleanValue() )
		return;
	
	try
	{
		if( CapBankDevice.isInAnyOpenState(getCapBankDevice()) )
		{
			getConnectionWrapper().executeCommand( 
				getCapBankDevice().getControlDeviceID().intValue(), CBCCommand.CONFIRM_OPEN );
		}
		else if( CapBankDevice.isInAnyCloseState(getCapBankDevice()) )
		{
			getConnectionWrapper().executeCommand( 
				getCapBankDevice().getControlDeviceID().intValue(), CBCCommand.CONFIRM_CLOSE );
		}

	}
	catch( java.io.IOException e )
	{
		handleException(e);
	}
	
	return;
}

/**
 * Comment
 */
public void jMenuItemCapBankData_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( actionEvent.getSource() instanceof javax.swing.JMenuItem )
	{
		ObjectInfoDialog d = new ObjectInfoDialog(
			CtiUtilities.getParentFrame(this) ); 

		//do some mapping to get the compatible DBPersistent
		DBPersistent cbcObject = null;
		
		//do not try to get the controller if we are fixed
		if( !CapBank.FIXED_OPSTATE.equalsIgnoreCase(getCapBankDevice().getOperationalState()) )
			cbcObject = LiteFactory.convertLiteToDBPers(
				PAOFuncs.getLiteYukonPAO(getCapBankDevice().getControlDeviceID().intValue()) );
		
		try
		{
			Object[] items = null;

			if( cbcObject == null )
				items = new Object[] {getCapBankDevice()};
			else
			{
				Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, cbcObject);
				cbcObject = t.execute();
				
				//DeviceCBC
				if( cbcObject instanceof CapBankController )
					items = new Object[] {
							getCapBankDevice(),
							cbcObject,
							((CapBankController)cbcObject).getDeviceCBC() };			
				else
					items = new Object[] {getCapBankDevice(), cbcObject};
			}

			d.setLocationRelativeTo( (javax.swing.JMenuItem)actionEvent.getSource() );
			d.setModal( true );
			d.showDialog( items );
		}
		catch (Exception e)
		{
			CTILogger.error("Unable to retrieve object from the database", e);
		}
		        
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
		if( getJMenuItemEnableDisable().getText().equals("Enable") )
			getConnectionWrapper().executeCommand( getCapBankDevice().getCcId().intValue(), CBCCommand.ENABLE_CAPBANK );
		else
			getConnectionWrapper().executeCommand( getCapBankDevice().getCcId().intValue(), CBCCommand.DISABLE_CAPBANK );
	}
	catch( java.io.IOException ex )
	{
		handleException( ex );
	}
		
	return;
}

/**
 * Return the jMenuItemCapBankData property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemCapBankData()
{
	if( jMenuItemCapBankData == null)
	{
		try
		{
			jMenuItemCapBankData = new javax.swing.JMenuItem();
			jMenuItemCapBankData.setName("jMenuItemCapBankData");
			jMenuItemCapBankData.setMnemonic('t');
			jMenuItemCapBankData.setText("CapBank Data...");
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemCapBankData;
}


/**
 * Comment
 */
public void jMenuItemManualEntry_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	java.awt.Frame pFrame = CtiUtilities.getParentFrame(this.getInvoker());
	
	final javax.swing.JDialog d = new javax.swing.JDialog( pFrame ); 

	CapControlEntryPanel panel = new CapControlEntryPanel( getConnectionWrapper() )
	{
		protected void disposePanel()
		{
			d.dispose();
		};
	};

	if( panel != null )
	{
		panel.setCapObject( getCapBankDevice() );
		
		d.getContentPane().add(panel);
		//d.setSize( new Dimension(260, 200) );
		d.pack();
		d.setTitle("Cap Bank Manual Entry");
	}

	d.setLocationRelativeTo( pFrame );
	d.setModal( true );		
	d.show();

	return;
}
/**
 * Comment
 */
public void jMenuItemOpenClose_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	try
	{
		boolean close = getJMenuItemOpenClose().getText().equalsIgnoreCase("Close");
		
		int confirm = javax.swing.JOptionPane.showConfirmDialog( this, 
				"Are you sure you want to send a '" + getJMenuItemOpenClose().getText() +
				"' command to '" + getCapBankDevice().getCcName() +"' ?",
				"Open/Close Confirmation", 
				javax.swing.JOptionPane.YES_OPTION);
		
		if (confirm == javax.swing.JOptionPane.YES_OPTION)
		{

			if( close )
				getConnectionWrapper().executeCommand( getCapBankDevice().getControlDeviceID().intValue(), CBCCommand.CLOSE_CAPBANK );
			else
				getConnectionWrapper().executeCommand( getCapBankDevice().getControlDeviceID().intValue(), CBCCommand.OPEN_CAPBANK );
		}
		
	}
	catch( java.io.IOException ex )
	{
		handleException( ex );
	}

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 4:17:02 PM)
 */
private void setAlarmMenuItems() 
{
	if( TagUtils.isAlarmUnacked(getCapBankDevice().getTagControlStatus().intValue()) )
	{
		getJMenuItemAckAlarm().setEnabled( true );
	}
	else
	{
		getJMenuItemAckAlarm().setEnabled( false );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 10:40:12 AM)
 * @param newCapDevice com.cannontech.cbc.CapBankDevice
 */
public void setCapBankDevice(CapBankDevice newCapDevice, Feeder feeder ) 
{
	if( newCapDevice != null )
	{
		capBankDevice = newCapDevice;
		setOwnerFeeder( feeder );

		// set the state of the OpenClose menu item
		setFixedMenuItems( getCapBankDevice().getOperationalState().equalsIgnoreCase("Fixed") );
		
			
		if( CapBankDevice.isStatusClosed( getCapBankDevice().getControlStatus().intValue() ) )
		{
			getJMenuItemOpenClose().setText("Open");
		}
		else
			getJMenuItemOpenClose().setText("Close");


      getJMenuItemConfirm().setEnabled( !getCapBankDevice().getCcDisableFlag().booleanValue() );

		//allow any bank that have not been temp moved to be moved
      getJMenuItemTempMove().setEnabled( !getCapBankDevice().isBankMoved() );
      	//feeder.getCcId().intValue() ); 
//      	!CapControlTags.isTemporaryMove(getCapBankDevice().getCapBankTags()) );

		//allow a return to the og feeder if this bank is the moved feeder OR
		// it is the original bank that has been moved
      getJMenuItemMoveBack().setEnabled( getCapBankDevice().isBankMoved() );
      	//feeder.getCcId().intValue() );
//      	CapControlTags.isTemporaryMove(getCapBankDevice().getCapBankTags())
//      	|| CapControlTags.isTemporaryMoveOrig(getCapBankDevice().getCapBankTags()) );
      
		// set the state of the alarm menu items
		setAlarmMenuItems();			
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 3:41:20 PM)
 * @param newConnectionWrapper com.cannontech.cbc.CBCClientConnection
 */
public void setConnectionWrapper(com.cannontech.yukon.cbc.CBCClientConnection newConnectionWrapper) {
	connectionWrapper = newConnectionWrapper;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 4:14:44 PM)
 * @param isFixed boolean
 */
private void setFixedMenuItems(boolean isFixed) 
{
	getJMenuItemOpenClose().setEnabled( !isFixed );
	getJMenuItemConfirm().setEnabled( !isFixed );
	//getJMenuItemManualEntry().setEnabled( !isFixed );

	// set the state of the EnableDisable and OpenClose menu items
	if( getCapBankDevice().getCcDisableFlag().booleanValue() )
	{
		getJMenuItemEnableDisable().setText("Enable");

		if( !isFixed )
			getJMenuItemOpenClose().setEnabled(false);
	}
	else
	{
		getJMenuItemEnableDisable().setText("Disable");

		if( !isFixed )
			getJMenuItemOpenClose().setEnabled(true);
	}

}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @param newJMenuItemAckAlarm javax.swing.JMenuItem
 */
public void setJMenuItemAckAlarm(javax.swing.JMenuItem newJMenuItemAckAlarm) {
	jMenuItemAckAlarm = newJMenuItemAckAlarm;
}

/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 4:17:19 PM)
 * @param value boolean
 */
public void setManualEntryEnabled(boolean value) 
{
	getJMenuItemManualEntry().setEnabled(value);
}
/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
public void tableChanged(javax.swing.event.TableModelEvent event ) 
{
	// the capBankDevice will change, so lets have the popupmenus
	// text change along with it
	setCapBankDevice( getCapBankDevice(), getOwnerFeeder() );
}

private Feeder getOwnerFeeder()
{
	return ownerFeeder;
}

private void setOwnerFeeder( Feeder feeder_ )
{
	ownerFeeder = feeder_;
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF2F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359AEDF0D455B5C0C0343403AD2D48E0A7DAF5B21D0C260A2D6D5099A6C59BAA9D35058ACAABE32972832DF5284DD0275EACA410AFC2BE089B4538C8B0C1C3889F8AC42C8959A8102099934936C071E577A5F9495B7716775EE6B7A1011EF36F3BEFDF1637817CE0664CFD775CF34F3977FC5F8D247C0D141CECD7B9A159B9447CF7B01B90778EC2BE7E5B1BB59C17A4E664925B3F6DGA52463448698E7
	425A026BB2F23710DFFD38896637A3AD4CDF016FB21279E18792A710D38BEB6A3EE6FDE2B9CDA5DC4EC6BA1F2697423899A095F0B82E95D17E8FF53709DFEC62B9E43B88D9560BB4BF57371938C3B06FG1885105A4B68974018C7A5CFEDBCE4521D39B1052C7DCD0D2B887958F8F27074313DDEF374103B335774212C2768B9F14FA84C678160655713ED523BB09E69986C7B37DF8D567B85C5D195C37477577BFB7C75A1B5948E85C5A55C5858A2040E8A4A20D79C127CE2079A7ABC847B7A7B11775B46A64A0B
	1086182F25384F73D1DE9AFCAF84D8E1613795A07E108477856817789DBF28B9E25E41F761EEF266B1E5FDF102BBA6C96CEE29D26C0E33426BB6D6D2FBA415225ED7415A26010C5CEDGCDGDFGA4813CC67D603A1237E1EC6EB1DA54E0D0D51AC32123F2C0BB2986C4993E9B9BE14544FDA67AD5ADC0883375EA3D4E61BC93815B7762B56FE3BAC95D4F6D1E76591DE4453DED2E0D8EBAD9115A601ABD40F411BE905389133D47C8A47B110171FEB9DEF6B7314BEE1F262E4FF110BDEF69AE57AA13CF16CDF66D
	A05AFA917581B7FCE71B7E41703F27F85906E7BEFA1A62F1BE8AEBAB8739EF78B6733334EF18C3366CEDF754436C0CE617D7E6E7B8A3471FE5A6753B69B2133951BA4BDC0ACF316159D95E24F81C5F8FEB5B0299394C3F2EAF4718CD0279DC00F9G0B8156GECD200CE61BE76C8491E2B74318EC1B30E06A5B9A0EA046D3D3E3482C6B1EAE842F1C5B78445AF1E92B4C96811C548CE0C663B25F8361EF17BEE223E6F87999F0B0A28890624AA40DD880A0628690CA6253484E9B4112EF6C9C1918484B11659FAEA
	E9995AC6500D4FC3814190E394ECBD3D94EDB2A41C820E40G3E19DEFE3A9575F5846E5F8590307470CE9972EB93B5C5149B9B5BD5F5B09C6A421B10F98A6A79A31ABB2270BDD361BA3E2782F1D9382EF2BEBDE525B6BEDE490F479734E19EE38F67E3DE2ED6394FBC6763BE63AD3A0354ADBD6168B3F4DDB5FDC60D0F1D6514A71E0B4D05FC5FB5467155C035A917499879C6E42CB7C726DF0F9BED158CF2D3G96D8BA3E254C43FC7628200B5CCBBF55404144C0CC636C1CBFA8607584F967D8673F0D621781CF
	924A482D8DF15D55967862683F4E7BB1994DD4067DBCDF278C26236F15E3DD9207C445700AA1D1894007EE2E4DA9C75FB36336D7357624D0FC88623C8582CB3770F716E3AC8A0621C9BDE1C36C948DC3D27AE26BF7D1D9FA38A794CB91DC56FD65F8C7B582F1EE517F229CE3442FAA0AC80DE5EDB15767539895E591AABA642530E2B0FC46D67473C1F1584C918FEE4546E7C8104318EFD81F10AE94609DB4A988AE00A90B5C9E4278BDEB38EDBA6AG9C76BBD198E704F8ACC6F670D82C9E3915ACB8375365D4CB
	E65F61F52D8931D8A067E331484E74338A67337F12668A932F3333FE120FFAF0EAD8187F7F2792F9F9C046810076735C7F8F3F026BBC47180E5F224AB2D3272567375CDB50AE62793088299913D91B253B5D6E523835D3269E597A3BEE5423EC4B03F59A6AF1E9816F8583F01ED100396719AED7183AE0F4B797F23AA84CE7EAB13AF44DCEF7320853ED5021AE819C51995D284E68182EF25DC8C77B3F81E1C808566B9170414646939D587A1D04560F75F83F26F99A6D39D8673D1CB108FA5CE0GFF0083863777
	23C53C171BB518C2D67E6855047DEA3B414E92B2E27676CC613D5C1EFC14198EEB8B8CEE23F4373D1EB46B3A6A17E8A9B375FB2E526E8F8C97284C0731D752F463B884EC145BE220924F0B354EC41B357FDFB40721FE16005C42B0740EE1446F164BEDE77706D94D12E1DCCF7909057BB01E2933543321C354FAD5ADA886EAD13B75C244282739233E19AE376277AEEE0B2621E69887D069478DB178A9241796D95545F34DF13C5A93B3FBFF57A942FB385B4A1A4C33AE3EEB8EB9F2294C6554D3CFAFFE5D1599E6
	7AFFBE9C334368BD396B577C6F1F9B19CEB28A39CE0E8CE5649AC3DCA737F4BB69A4ED083FAF7A2ADA6DF7EAD550673D120E43355F0B1DE528081FE5814849324E7278A62733B4C438FD7ED874556CD3D452E23F4BC9C1898BF22BE2E84357FA93D62F96DB3D39B742746E0B44FAF2993E13A263FBF456332E26F1B7AB925F4B2E257834C8FCAFBBB248FB590530D69B65FD5622824C5DB3E01E8A30GE099C09600FB18475B8B157B49957A586370DE5494C16E9CD7284C987BB09FF58F35451C376423AEE9EDA1
	3CEEEFA80CFF239C989EFF6F0E38B95BD7FA593E2A383991370F59B1B367B5671C47ADEB3EEB83B24FF9B306F9EE9B7B481E5B66976FF395A448ED8BCD592B06E3B1D5E73DD3F51A1F2BC760FD8A6083B0GE6834C83D8B642757F5B7C8F2E24FF6AD5DDEA8B600EC1DDE9092F7F163EB23F241E7FF0C5BDE70D0C5F477437241B6BEF4B855E870E96FD39B35CF0593E78B39C3B60F406123CCDA60FAFE01D3DB97ECCEB59EC1CDB385E9144E542FC05054B9FC59C9905F898653815C3082B0339F7145F69B99ADF
	BE18C78166DA78F5940F359C6BB456E006CF9A741974CEFDE6B481FEEE82FCBCB099B78F72DE5809A6D17FFD35BDB3734D5F95A46E051797B87742CF96B87742FFA9A8E1F860E1FF917375E78BA66E157FDE6054AB3FCEF5302822187256F5D1274162958C41DCFB4658CA9CFA66CF6051EC6A2F0C3CDF18E752349AEA4BDECB47129C6F137341C60F18F6DA85E3D61C4DD86C2F5B0CF52AF08CF2B1C093008FA0BA46E37EDB05D74C392D5189F26ED8D1E23BA497B95B6506A26C51F4B52C7945F85D5FD8643656
	E2598593A52A49FC7B507D0620750917596ED63A1F2DA55A7F6322C476E5B6F8F91C8D76D9B6D85E9DEF0359204734B173B7D498170C0D378163F7EF111D5FFE0BDFC5DEBC3F55307F25B89EDBAE46660C5FBA836F9F5244DED103D7C327B4027EF366A2660C32B0660CEA78EE3848FB113191FCE34942309AB6A0FFD23A97C678DBAA8566771BB2D6DEE4FD8C5B37FD946BCDD008CA41F010CAE1B28E53DC55F4895EB317B86DBF06A82D24445196C768EFAEC017EC512EB80D6F169317751E7CFEDF6C2F302F0F
	6B63B84D7C32F2BB0D2DBFE239B3095653F4103970924F270BA8EE994CD7DB387AA06256423CB00963BE213886189FCB62F162AACE6C67F9454EFEDEDC6C1CFF3696CF1CFFDEAFCE641F6CFC4FF7733B1D314EFC40476F9632F05F77713B0DDA387BFC7CEEA996EEB72DDB73E01EE96106A8EE854C57D838A7A8AE8B666513B2F2BD1370EF961B51D426CDCE0F3C8123A2C6BCD6C76A1560ED04BF5E556E5DFB304EF338E11779DEC809587F4EF0A87F2063BB9DA1B00979E6549C67DF37436CB5DF3D40FF97CA07
	35551378BBE07FBE4CFDD64F0587E9EC7CC8D6A36DE6889076266C267BBD13B3F28F81F4CC66FDD774AD5EF7255DB90734A735B93EA9D7F66DF375CFE6E71AB1B956F725C25F45F4734CABB15DAC9D3A9A5D30FD791531FD378EDC4DBEC6FB93756F20A2C2A2177CAC7E6E2238A781B2A77378DBA963EFB086B8AC135778AA4AE33A7BC9846338D793A0147BBAC3221FAC1B0CFCDA03EE62DC1FE3F9C98CBA6700B19ACF1EA99939F5G8CD7C97D7081G5F946ECBE7517DCDF9E93E0964B19EFF22BC7AE13FB105
	5F4D1BF7398ECED35C9368F5965D3773466BA0E9DBCC67AF64213D59238D4B222084707C14EEB516F253079FBEED7761ADF3363B329378F07A9453F727447C045567472B70EC4F833EB019671A4E0AF7E0BCA58C185538B5A0992AC64BB1A0BBB4B5A4EA46B04352DFFC4CFA623970657EE6D4FF4D7B589B70A19ABFF5496C8D18ED6117D07CA19B1E456BD9ABDE3D30161A4C5F06859E34F561D468D1G1AGFCGD100D9D339CE66D6B410AB74A9DDEA289D9234ECCB294C1E8F48B85628B5265DB24F7278FE69
	21D462F9E703E3FCE71D5D63CA1D4A6C35F8EA4CEECB86DEB2637B656DB11FC96A2ECA9C276F9D2E736C5B3B0B53B67868F9FAEACC1FFAF3C70CCF774689789CFCF79C9FAD0F728956183D5E43148F53D7DD554EC4BE842ABA813CBF6D99006628CB683376BCD905F7346A8EF3ADF36D69AA2C37905A06A44852887D992C938815BE66C30FDEA016BFBDEE7B76DF78A67E46B05EFFC73FD1996C7E651E44757FB50FF37D5F6EF12E7FB51E095F97FBBC09FF0BFF4FE37FAD1E597D69EA0C9FB28D5EFDGCDGBEG
	99A0F99A0F1F87BD35097C81102CB0C23F2C439DA47EFB77DF2B935F79456AAB7F5D7065EA7CAD34C7B58CB5589A6833FCA9E7A75A4DD0C39449F079BBD1D71A5457EFB0D2933FE5A766C7D96C25E876FFAB1EBAF68F0CE94F129CDDA3251747C973F716BF16F64EDB6EA22E63A437DFC42647C9CEC48A987DD17C7363FF81427AF9F21126830719C1F2E8C7EA10DC56FD9A241835189E5AE35657579B245C297B347860D32FF55C2C0D570DDD176CF5A3EB2ABBE16F33E1BA4BBB6E6971F56363B654CB9760C353
	390DEFBA31BB110DA166BB0302AC9B02BC582C84B07E0F8E9BE24C675A5B103F4EE94CC5339E35613987ECFB7E8FD0CB8788F7B8CB5DCC8DGGC0A3GGD0CB818294G94G88G88GF2F954ACF7B8CB5DCC8DGGC0A3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG068DGGGG
**end of data**/
}
}
