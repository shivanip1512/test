package com.cannontech.clientutils;

/**
 * Insert the type's description here.
 * Creation date: (5/9/00 3:00:34 PM)
 * @author: 
 * @Version: <version>
 */

import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.roles.yukon.SystemRole;


public abstract class ClientBase extends java.util.Observable implements ClientBaseInterface, MessageListener
{
	private ClientConnection connection = null;

	// just in case someone wants to observe the connection
	private java.util.Observer observer = null;
	
	//Default host string
	public static String HOST = "127.0.0.1";
	public static int PORT = 1510;

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
		return connection.isValid();
}
/**
 * Code to perform when this object is garbage collected.
 * 
 * Any exception thrown by a finalize method causes the finalization to
 * halt. But otherwise, it is ignored.
 */
protected void finalize() throws Throwable 
{
	// Insert code to finalize the receiver here.
	// This implementation simply forwards the message to super.  You may replace or supplement this.
	super.finalize();

		
	// NEED TO GET THIS METHOD WORKING!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	this.stop();

	if( connected() )
	{
		if( observer != null )
			connection.deleteObserver( observer );
			
		getConnection().disconnect();
	}
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
private ClientConnection getConnection() 
{
	return connection;
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/00 2:26:52 PM)
 */
private void getExternalResources() 
{
	try
	{
      HOST = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );

      PORT = Integer.parseInt(
					RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) ); 
   }
   catch( Exception e)
   {
      handleException( e );
   }

}
/**
 * Insert the method's description here.
 * Creation date: (5/8/00 4:56:37 PM)
 * Version: <version>
 * @return com.cannontech.message.dispatch.message.PointRegistration
 */
public PointRegistration getPointRegistration( Long[] ptIDs )
{
	//Register for points
	PointRegistration pReg = new PointRegistration();	 		
	com.roguewave.tools.v2_0.Slist list = new com.roguewave.tools.v2_0.Slist();
	
	if( ptIDs != null )
	{
		for( int i = 0; i < ptIDs.length; i++ )
			list.insert( ptIDs[i] );

		pReg.setPointList( list );
	}
	else
	{	// just register for everything
		pReg.setRegFlags( PointRegistration.REG_ALL_PTS_MASK | 
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
	//make sure we have a created connection before trying to connect!
	connection = new com.cannontech.message.dispatch.ClientConnection();
	
	if( observer != null )
   {
      //connection list
		getConnection().addObserver( observer );

      //our own list
      addObserver( observer );
   }

	getConnection().addMessageListener( this );
	getConnection().setAutoReconnect( true );
	getConnection().setTimeToReconnect( 10 );
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
 * Insert the method's description here.
 * Creation date: (3/8/2002 12:51:17 PM)
 */
public void startConnection() 
{
	tryConnection();
}

/**
 * This method was created in VisualAge.
 */
public void stop() 
{

	try
	{
		if ( connected() )  // free up VanGogh's resources
		{
			Command comm = new Command();
			comm.setPriority(15);
			
			comm.setOperation( 
				com.cannontech.message.dispatch.message.Command.CLIENT_APP_SHUTDOWN );

			getConnection().write( comm );

			getConnection().disconnect();
		}
	}
	catch ( java.io.IOException e )
	{
		handleException ( e );
	}
	
}
/**
 * This method was created in VisualAge.
 */
private void tryConnection() 
{	
	try
	{
		getExternalResources();
		
		com.cannontech.clientutils.CTILogger.info("Trying to connect to:  " + HOST + " " + PORT );

		getConnection().setHost(HOST);		
		getConnection().setPort(PORT);		

		getConnection().setRegistrationMsg( buildRegistrationMessage() );
		getConnection().connect();

		com.cannontech.clientutils.CTILogger.info("....Connection & Registration to Server Established.");
	}	
	catch( java.io.IOException e )
	{
		handleException( e );				
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2001 1:47:59 PM)
 * @param obj java.lang.Object
 */
public void write(Object obj) 
{
	if( getConnection() != null )
		getConnection().write( obj);
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
