package com.cannontech.tdc.roweditor;

/**
 * Insert the type's description here.
 * Creation date: (3/20/00 4:27:31 PM)
 * @author: 
 */
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.dispatch.DispatchClientConnection;
import com.cannontech.messaging.message.CommandMessage;
import com.cannontech.messaging.message.dispatch.MultiMessage;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.cannontech.messaging.message.dispatch.PointRegistrationMessage;
import com.cannontech.messaging.message.dispatch.RegistrationMessage;
import com.cannontech.messaging.message.dispatch.SignalMessage;
import com.cannontech.tags.TagManager;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.yukon.conns.ConnPool;

public class SendData 
{
	private static SendData sendData = null;
	
	private static TagManager tagManager = null;

/**
 * SendData constructor comment.
 */
protected SendData() 
{
	super();

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 5:49:33 PM)
 */
private void buildRegistration() 
{
	//First do a registration
	MultiMessage multi = new MultiMessage();
	RegistrationMessage reg = new RegistrationMessage();
	reg.setAppName( BootstrapUtils.getApplicationName() );
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 300 );  // 5 minutes
	reg.setPriority( 15 );

	PointRegistrationMessage pReg = new PointRegistrationMessage();
	pReg.setRegFlags(PointRegistrationMessage.REG_ALARMS);
			 					
	multi.getVector().addElement(reg);
	multi.getVector().addElement(pReg);
	
	getConnection().setRegistrationMsg( multi );	
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public static synchronized boolean classExists()
{
	if( sendData == null )
		return false;
	else
		return true;
}

private DispatchClientConnection getConnection() {
    return (DispatchClientConnection)ConnPool.getInstance().getDefDispatchConn();
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public static synchronized SendData getInstance()
{
	if( sendData == null )
		sendData = new SendData();
		
	return sendData;
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public synchronized TagManager getTagMgr()
{
	return tagManager;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION SendData() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 4:29:45 PM)
 */
private void initialize() 
{
    DispatchClientConnection connection = getConnection();
	buildRegistration();
	tagManager = new TagManager( connection );
	
}

/**
 * Insert the method's description here.
 * Creation date: (3/21/00 10:13:50 AM)
 */
public void sendCommandMsg( CommandMessage cmd )
{
    DispatchClientConnection connection = getConnection();
	if( connection != null && connection.isValid() )
	{
		cmd.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
		connection.write( cmd );

		TDCMainFrame.messageLog.addMessage("Command msg sent successfully", MessageBoxFrame.INFORMATION_MSG );
	}
	else
	{
		//connection = null;  // force this class to re-register next time
		TDCMainFrame.messageLog.addMessage("Unable to send Command msg", MessageBoxFrame.INFORMATION_MSG );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/00 10:13:50 AM)
 */
public void sendPointData( PointDataMessage point )
{
    DispatchClientConnection connection = getConnection();
	if( connection != null && connection.isValid() )
	{
		point.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
		connection.write( point );

		TDCMainFrame.messageLog.addMessage("PointData change for point id " + point.getId() + " was sent successfully", MessageBoxFrame.INFORMATION_MSG );
	}
	else
	{
		//connection = null;  // force this class to re-register next time
		TDCMainFrame.messageLog.addMessage("Unable to send point change for point id " + point.getId(), MessageBoxFrame.INFORMATION_MSG );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/00 10:13:50 AM)
 */
public void sendSignal( SignalMessage point )
{
    DispatchClientConnection connection = getConnection();
	if( connection != null && connection.isValid() )
	{
		point.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
		connection.write( point );

		TDCMainFrame.messageLog.addMessage("Signal sent for point id " + point.getPointId() + " was sent successfully", MessageBoxFrame.INFORMATION_MSG );
	}
	else
	{
		//connection = null;  // force this class to re-register next time
		TDCMainFrame.messageLog.addMessage("Unable to send Signal change for id " + point.getPointId(), MessageBoxFrame.INFORMATION_MSG );
	}
	
}
}
