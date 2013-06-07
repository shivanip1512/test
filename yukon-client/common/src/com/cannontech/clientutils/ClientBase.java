package com.cannontech.clientutils;

/**
 * Insert the type's description here.
 * Creation date: (5/9/00 3:00:34 PM)
 * @author: 
 * @Version: <version>
 * @deprecated Only TDC should continue using this - UGLY
 */

import java.util.ArrayList;
import java.util.List;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.cannontech.messaging.message.dispatch.PointRegistrationMessage;
import com.cannontech.messaging.message.dispatch.SignalMessage;
import com.cannontech.messaging.util.MessageEvent;
import com.cannontech.messaging.util.MessageListener;
import com.cannontech.yukon.BasicServerConnection;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;


public abstract class ClientBase extends java.util.Observable implements ClientBaseInterface, MessageListener
{
	// just in case someone wants to observe the connection
	private java.util.Observer observer = null;
	
/**
 * ClientBase constructor comment.
 */
public ClientBase() 
{
	super();
	initialize();
}
/**
 * ClientBase constructor comment.
 */
public ClientBase( java.util.Observer obs ) 
{
	super();

	this.observer = obs;
	initialize();
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean connected() 
{
	if( getConnection() == null )
		return false;
	else
		return getConnection().isValid();
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
protected IServerConnection getConnection() {
    return ConnPool.getInstance().getDefDispatchConn();
}

/**
 * Insert the method's description here.
 * Creation date: (5/8/00 4:56:37 PM)
 * Version: <version>
 * @return com.cannontech.message.dispatch.message.PointRegistration
 */
public PointRegistrationMessage getPointRegistration( Long[] ptIDs )
{
	//Register for points
	PointRegistrationMessage pReg = new PointRegistrationMessage();	
    List<Integer> list = new ArrayList<Integer>();
	
	if( ptIDs != null )
	{
		for( int i = 0; i < ptIDs.length; i++ ) {
            long id = ptIDs[i];
			list.add((int)id);
        }
		pReg.setPointList( list );
	}
	else
	{	// just register for everything
		pReg.setRegFlags( PointRegistrationMessage.REG_ALL_PTS_MASK | 
					  PointRegistrationMessage.REG_EVENTS | 
					  PointRegistrationMessage.REG_ALARMS );
	}
	
	return pReg;
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/00 3:20:03 PM)
 * Version: <version>
 * @param exc java.lang.Exception
 */
private void handleException(Throwable e)
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("---------------- EXCEPTION ----------------" + this.getClass());
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
}
/**
 * This method was created in VisualAge.
 */
private void initialize() 
{
	getConnection();
	
	if( observer != null )
   {
      //connection list
      getConnection().addObserver( observer );

      //our own list
      addObserver( observer );
   }

	getConnection().addMessageListener( this );
	getConnection().setAutoReconnect( true );
}

/**
 * Insert the method's description here.
 * Creation date: (5/4/00 3:29:31 PM)
 * Version: <version>
 */
public void reRegister( Long[] ptIDs ) 
{
	getConnection().write( getPointRegistration(ptIDs) );
}

//private void handleMessage( Object in )
public void messageReceived( MessageEvent e )
{
	BaseMessage in = e.getMessage();

	if( in instanceof PointDataMessage )
   {           
      PointDataMessage point = (PointDataMessage) in;            
      receivedPointData( point );
   }
   else if( in instanceof SignalMessage )
   {           
      SignalMessage sig = (SignalMessage) in;           
      receivedSignal( sig );
   }
   else if( in instanceof DBChangeMessage )
   {
      DBChangeMessage dbChange = (DBChangeMessage) in;
      receivedDBChangMsg( dbChange );
   }

}

/**
 * Write a message to dispatch if it is available
 * Creation date: (1/24/2001 1:47:59 PM)
 * @param obj java.lang.Object
 */
public void write(BaseMessage obj) {
    BasicServerConnection conn = getConnection();
	if(conn.isValid()) { 
		getConnection().write( obj);
    }
}

public void addMessageListener( MessageListener ml )
{	
	getConnection().addMessageListener( ml );
}

public void removeMessageListener( MessageListener ml )
{	
	getConnection().removeMessageListener( ml );
}

public String getHost(){
    return getConnection().getHost();
}

public int getPort(){
    return getConnection().getPort();
}

}
