package com.cannontech.database.cache;

import java.util.Observer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.roles.yukon.SystemRole;

/**
 * @author rneuharth
 *
 * Listens and handles DBChange messages from dispatch.
 * Use this class if you want a generic implementation of handling DB change
 * messages. 
 * Override handleDBChangeMsg() method for finer control.
 * 
 */
public class GenericDBCacheHandler implements DBChangeListener, Observer
{
	private com.cannontech.message.dispatch.ClientConnection connToDispatch = null;
	private boolean connToVanGoghErrorMessageSent = false;
	private String name = "GenericCacheListener";


	public GenericDBCacheHandler( String name_ )
	{
		super();
		name = name_;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (12/20/2001 1:46:57 PM)
	 * @return com.cannontech.message.dispatch.ClientConnection
	 */
	public ClientConnection getClientConnection() 
	{
		if( connToDispatch == null )
		{
			String host = "127.0.0.1";
			int port = 1510;
			try
			{
				host = ClientSession.getInstance().getRolePropertyValue(
							SystemRole.DISPATCH_MACHINE, 
							"127.0.0.1");
            
				port = (new Integer( ClientSession.getInstance().getRolePropertyValue(
							SystemRole.DISPATCH_PORT, 
							"1510"))).intValue();			
			}
			catch( Exception e)
			{
				CTILogger.error( e.getMessage(), e );
			}

			connToDispatch = new com.cannontech.message.dispatch.ClientConnection();
			Registration reg = new Registration();

			reg.setAppName( name + " @" + CtiUtilities.getUserName() );
				
			reg.setAppIsUnique(0);
			reg.setAppKnownPort(0);
			reg.setAppExpirationDelay( 300 );  // 5 minutes should be OK

			connToDispatch.addObserver(this);
			connToDispatch.setHost(host);
			connToDispatch.setPort(port);
			connToDispatch.setAutoReconnect(true);
			connToDispatch.setRegistrationMsg(reg);
		
			try 
			{
				connToDispatch.connectWithoutWait();
			}
			catch( Exception e ) 
			{
				CTILogger.error( e.getMessage(), e );
			}

		}

		return connToDispatch;
	}
	
	
	public void handleDBChangeMsg(DBChangeMsg msg, LiteBase object)
	{
		//no op, just have the cache update itself
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/19/00 4:52:57 PM)
	 * @param o java.util.Observable
	 * @param arg java.lang.Object
	 */
	public void update(java.util.Observable o, Object arg) 
	{
		if( o instanceof com.cannontech.message.dispatch.ClientConnection )
		{
			if( ((com.cannontech.message.dispatch.ClientConnection)o).isValid() )
			{
				connToVanGoghErrorMessageSent = false;
				CTILogger.info( "Connection to Message Dispatcher Established." );
			}
			else
			{
				if( !connToVanGoghErrorMessageSent )
				{
					connToVanGoghErrorMessageSent = true;
					CTILogger.info( "Lost Connection to Message Dispatcher!" );
				}
			}
		}
	}
}
