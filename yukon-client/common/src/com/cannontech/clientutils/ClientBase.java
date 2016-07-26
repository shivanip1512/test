package com.cannontech.clientutils;

/**
 * Insert the type's description here.
 * Creation date: (5/9/00 3:00:34 PM)
 * @author: 
 * @Version: <version>
 * @deprecated Only TDC should continue using this - UGLY
 */

import java.util.Set;

import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
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
public PointRegistration getPointRegistration( Set<Integer> ptIDs )
{
	//Register for points
	PointRegistration pReg = new PointRegistration();	
	
	if( ptIDs != null )
	{
        pReg.setPointIds(ptIDs);
    }
    else
	{	// just register for everything
		pReg.setRegFlags( PointRegistration.REG_ALL_POINTS | 
					  PointRegistration.REG_EVENTS | 
					  PointRegistration.REG_ALARMS );
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
public void reRegister( Set<Integer> ptIDs ) 
{
	getConnection().write( getPointRegistration(ptIDs) );
}

//private void handleMessage( Object in )
public void messageReceived( MessageEvent e )
{
	Message in = e.getMessage();

	if( in instanceof PointData )
   {           
      PointData point = (PointData) in;            
      receivedPointData( point );
   }
   else if( in instanceof Signal )
   {           
      Signal sig = (Signal) in;           
      receivedSignal( sig );
   }
   else if( in instanceof DBChangeMsg )
   {
      DBChangeMsg dbChange = (DBChangeMsg) in;
      receivedDBChangMsg( dbChange );
   }

}

/**
 * Write a message to dispatch if it is available
 * Creation date: (1/24/2001 1:47:59 PM)
 * @param obj java.lang.Object
 */
public void write(Message obj) {
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

}
