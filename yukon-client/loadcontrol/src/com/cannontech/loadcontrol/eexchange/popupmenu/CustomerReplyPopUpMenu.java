package com.cannontech.loadcontrol.eexchange.popupmenu;

/**
 * Insert the type's description here.
 * Creation date: (1/21/2001 4:40:03 PM)
 * @author: 
 */
import com.cannontech.loadcontrol.eexchange.datamodels.EExchangeRowData;

public class CustomerReplyPopUpMenu extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{
	private EExchangeRowData currentRow = null;
	private javax.swing.JMenuItem jMenuItemDisable = null;
	private javax.swing.JMenuItem jMenuItemStartStop = null;
/**
 * ProgramPopUpMenu constructor comment.
 */
public CustomerReplyPopUpMenu() 
{
	super();
	initialize();
}
/**
 * ProgramPopUpMenu constructor comment.
 * @param label java.lang.String
 */
public CustomerReplyPopUpMenu(String label)
{
	super(label);
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{	
	if( e.getSource() == getJMenuItemStartStop() ) 
		jMenuItemStartStop_ActionPerformed(e);

	if( e.getSource() == getJMenuItemDisable() )
		jMenuItemDisableEnable_ActionPerformed(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 3:30:01 PM)
 * @return com.cannontech.loadcontrol.eexchange.datamodels.EExchangeRowData
 */
public com.cannontech.loadcontrol.eexchange.datamodels.EExchangeRowData getCurrentRow() {
	return currentRow;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemDisable() 
{
	if (jMenuItemDisable == null) 
	{
		try 
		{
			jMenuItemDisable = new javax.swing.JMenuItem();
			jMenuItemDisable.setName("JMenuItemDisable");
			jMenuItemDisable.setMnemonic('b');
			jMenuItemDisable.setText("Disable");
			jMenuItemDisable.setActionCommand("jMenuItemDisable");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemDisable;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemStartStop() 
{
	if (jMenuItemStartStop == null) 
	{
		try 
		{
			jMenuItemStartStop = new javax.swing.JMenuItem();
			jMenuItemStartStop.setName("JMenuItemStartStop");
			jMenuItemStartStop.setMnemonic('s');
			jMenuItemStartStop.setText("Start");
			jMenuItemStartStop.setActionCommand("jMenuItemStartStop");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemStartStop;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception 
{
	getJMenuItemDisable().addActionListener(this);
	getJMenuItemStartStop().addActionListener(this);
}
/**
 * Initialize the class.
 */
private void initialize() 
{
	try 
	{
		setName("ProgramPopUp");
		//setPreferredSize(new java.awt.Dimension(75, 25));
		setBorderPainted( true );

		add(getJMenuItemStartStop(), getJMenuItemStartStop().getName());
		add(getJMenuItemDisable(), getJMenuItemDisable().getName());


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
private void jMenuItemCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	//send a message to the server telling the prorgram to stop
	//create a new message to be sent to the server
/*	com.cannontech.loadcontrol.messages.LMManualControlRequest msg = new com.cannontech.loadcontrol.messages.LMManualControlRequest();
	msg.setCommand( com.cannontech.loadcontrol.messages.LMManualControlRequest.SCHEDULED_STOP);
	msg.setYukonID( getExchangeProgram().getYukonID().intValue() );
	
	//send the new message to the server		
	com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write( msg );
*/
	return;
}
/**
 * Comment
 */
private void jMenuItemDisableEnable_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
/*	if( getExchangeProgram().getDisableFlag().booleanValue() )
	{
		//send a message to the server telling it to ENABLE this program

		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(
				new LMCommand( LMCommand.ENABLE_PROGRAM,
					 				getExchangeProgram().getYukonID().intValue(),
					 				0, 0.0) );
	}
	else
	{
		//send a message to the server telling it to DISABLE this program

		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(
				new LMCommand( LMCommand.DISABLE_PROGRAM,
					 				getExchangeProgram().getYukonID().intValue(),
					 				0, 0.0) );
	}
*/

	return;
}
/**
 * Comment
 */
private void jMenuItemPostVoluntaryOffer_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
/*	final javax.swing.JDialog dialog = new javax.swing.JDialog(
		com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()),
		"Post Voluntary Curtailment Offer", true);

	CurtailmentEntryPanel panel = new CurtailmentEntryPanel( CurtailmentEntryPanel.VOLUNTARY_TYPE );
	
	dialog.getContentPane().add( panel );
	dialog.setLocation( this.getInvoker().getLocationOnScreen() );
	dialog.setModal(true);
	dialog.setSize(465, 333);	
	dialog.show();

	dialog = null;
*/
	return;
}
/**
 * Comment
 */
private void jMenuItemStartStop_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
		
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 3:30:01 PM)
 * @param newCurrentRow com.cannontech.loadcontrol.eexchange.datamodels.EExchangeRowData
 */
public void setCurrentRow(com.cannontech.loadcontrol.eexchange.datamodels.EExchangeRowData newCurrentRow) 
{
	currentRow = newCurrentRow;

	if( getCurrentRow() == null )
		return;

	syncButtons();
	
/***** REMOVE THESE LINES WHEN FUCNTIONALITY IS INSERTED *******/
	getJMenuItemStartStop().setEnabled(false);
	getJMenuItemDisable().setEnabled(false);
/***************************************************************/

}
/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 5:26:53 PM)
 * @param program com.cannontech.loadcontrol.data.LMProgramEnergyExchange
 */
private void syncButtons()
{

/*	switch( getExchangeProgram().getProgramStatus().intValue() )
	{
		case LMProgramEnergyExchange.STATUS_ACTIVE:
			getJMenuItemStartStop().setText("Stop");
			getJMenuItemStartStop().setEnabled(false);
			getJMenuItemDisable().setEnabled(true);
			break;
		
		case LMProgramEnergyExchange.STATUS_MANUAL_ACTIVE:
		case LMProgramEnergyExchange.STATUS_FULL_ACTIVE:
			getJMenuItemStartStop().setText("Stop");
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
			break;
		
		case LMProgramEnergyExchange.STATUS_INACTIVE:
			getJMenuItemStartStop().setText("Start");
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
			break;
			
		case LMProgramEnergyExchange.STATUS_NOTIFIED:
		case LMProgramEnergyExchange.STATUS_SCHEDULED:
			getJMenuItemStartStop().setText("Stop");
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
			break;

		case LMProgramEnergyExchange.STATUS_STOPPING: //only used by the server
			getJMenuItemStartStop().setText("Start");
			getJMenuItemStartStop().setEnabled(false);
			getJMenuItemDisable().setEnabled(false);
			break;

		default:
			throw new IllegalStateException("Found an unexpected state for a LMProgram object, value = " + getExchangeProgram().getProgramStatus().intValue() );
	}
*/

}
}
