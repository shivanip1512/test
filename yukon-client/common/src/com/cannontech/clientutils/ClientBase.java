package com.cannontech.clientutils;

/**
 * Insert the type's description here.
 * Creation date: (5/9/00 3:00:34 PM)
 * @author: 
 * @Version: <version>
 */

import com.cannontech.common.login.ClientSession;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.roles.yukon.SystemRole;


public abstract class ClientBase extends java.util.Observable implements Runnable, ClientBaseInterface
{
	private ClientConnection connection = null;
	private Thread runningThread = null;

	// just in case someone wants to observe the connection
	private java.util.Observer observer = null;
	
	//Default host string
	public static String HOST = "127.0.0.1";
	public static int PORT = 1510;

	private int sleepTime = 200; // .2 seconds default
	
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
			
		runningThread = null;
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
      HOST = ClientSession.getInstance().getRolePropertyValue(
               SystemRole.DISPATCH_MACHINE, 
               "127.0.0.1");
         
      PORT = (new Integer( ClientSession.getInstance().getRolePropertyValue(
               SystemRole.DISPATCH_PORT, 
               "1510"))).intValue();         
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
	runningThread = new Thread( this, "TDCClientBase" );
	runningThread.setDaemon(true);

	//make sure we have a created connection before trying to connect!
	connection = new com.cannontech.message.dispatch.ClientConnection();
	
	if( observer != null )
   {
      //connection list
		getConnection().addObserver( observer );

      //our own list
      addObserver( observer );
   }

	getConnection().setAutoReconnect( true );
	getConnection().setTimeToReconnect( 10 );


	//try to connect	
	//runningThread.start();
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/00 3:02:24 PM)
 * @param mpc com.cannontech.message.dispatch.message.Multi
 */
/*private void receivedMulti(Multi mpc) 
{
	// loop through all the messages in the MULTI
	for( int i = 0; i < mpc.getVector().size(); i++ )
	{
		if( mpc.getVector().elementAt( i ) instanceof PointData )
		{
			receivedPointData( ((PointData)mpc.getVector().elementAt( i )) );
		}
		else if( mpc.getVector().elementAt( i ) instanceof Signal )
		{
			receivedSignal( ((Signal)mpc.getVector().elementAt( i )) );
		}
		else if( mpc.getVector().elementAt( i ) instanceof DBChangeMsg )
		{
			receivedDBChangMsg( ((DBChangeMsg)mpc.getVector().elementAt( i )) );
		}
		else
		{					
			handleException( new Exception( "Uunknown message recieved in MULTI message : " + mpc.getVector().elementAt( i ).getClass()) );
		}

	}	
}
*/
/**
 * Insert the method's description here.
 * Creation date: (5/4/00 3:29:31 PM)
 * Version: <version>
 */
public void reRegister( Long[] ptIDs ) 
{
	getConnection().write( getPointRegistration(ptIDs) );
}

private void handleMessage( Object in )
{
   if( in instanceof Multi )
   {           
      Multi mpc = (Multi) in;

      for( int i = 0; i < mpc.getVector().size(); i++ )
         handleMessage( mpc.getVector().get(i) );
   }
   else if( in instanceof PointData )
   {           
      PointData point = (PointData) in;            
      receivedPointData( point );
   }
   else if( in instanceof Signal )
   {           
      Signal sig = (Signal) in;           
      receivedSignal( sig );
      
      //tell everyone about our signal
      setChanged();
      notifyObservers( sig );
   }
   else if( in instanceof DBChangeMsg )
   {
      DBChangeMsg dbChange = (DBChangeMsg) in;
      receivedDBChangMsg( dbChange );
   }              

}

/**
 * run method comment.
 */
public void run() 
{	
	// first time only
	tryConnection();

	while( true )
	{
		try
		{
			Object in = null;

			if( connected() && ((in = connection.read(0L)) != null) )
			{
            handleMessage( in );
			}
			else if( connected() && in == null ) // connection.read() == null
			{
				receivedNullMsg();
				
				try
				{	
					Thread.sleep( sleepTime );
				}
				catch( InterruptedException ex )
				{
					//com.cannontech.clientutils.CTILogger.info("Thread Interrupted in com.cannontech.clientutils.ClientBase");
					break;
				}
			}
			else if( !connected() )
			{
				try
				{	
					Thread.sleep( sleepTime );
				}
				catch( InterruptedException ex )
				{
					//com.cannontech.clientutils.CTILogger.info("Thread Interrupted in com.cannontech.clientutils.ClientBase");
					break;
				}			
			}
		}
		catch( Throwable t )
		{
			handleException(t);
		}
	}

}
/**
 * Insert the method's description here.
 * Creation date: (6/6/00 4:59:46 PM)
 * @param newSleepTime int
 */
public void setSleepTime(int millisecs) {
	sleepTime = millisecs;
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/2002 12:51:17 PM)
 */
public void startConnection() 
{
	if( runningThread != null )
		runningThread.start();	
}
/**
 * This method was created in VisualAge.
 */
public void stop() 
{
	if ( runningThread != null )
	{
		runningThread.interrupt();
		runningThread = null;
	}
	
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
}
