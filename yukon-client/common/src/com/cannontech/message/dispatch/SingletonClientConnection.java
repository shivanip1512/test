/*
 * Created on Jan 8, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.message.dispatch;

import javax.servlet.http.HttpSessionBindingEvent;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.roles.yukon.SystemRole;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SingletonClientConnection implements DBChangeListener
{
	/** A connection to Dispatch */
	private com.cannontech.message.dispatch.ClientConnection connToDispatch = null;
	
	/** A singleton instance */
	private static SingletonClientConnection instance = null;

	public SingletonClientConnection()
	{
		super();
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.cache.DBChangeListener#getClientConnection()
	 */
	public ClientConnection getClientConnection()
	{
		if( connToDispatch == null)
			connect();	
		return connToDispatch;
	}
	/**
	 * Returns the singleton instance of this class
	 * @return
	 */
	public static synchronized SingletonClientConnection getInstance() {
		if(instance == null) {
			instance = new SingletonClientConnection();
			instance.connect();
		}
		else
		{
			CTILogger.info("SingletonClientConnection connection instanc already exists.");
		}
		return instance;
	}
	
	/**
	 * 
	 */
	private void connect() 
	{
		String host = "127.0.0.1";
		int port = 1510;
		try
		{
			host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );
			port = Integer.parseInt( RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) ); 
		}
		catch( Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}
	
		connToDispatch = new com.cannontech.message.dispatch.ClientConnection();
	
		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("SingletonClientConnection @" + com.cannontech.common.util.CtiUtilities.getUserName());
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 1000000 );
		
		connToDispatch.setHost(host);
		connToDispatch.setPort(port);
		connToDispatch.setAutoReconnect(true);
		connToDispatch.setRegistrationMsg(reg);
	
		try
		{
			connToDispatch.connectWithoutWait();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}


	/* (non-Javadoc)
	 * @see com.cannontech.database.cache.DBChangeListener#handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg, com.cannontech.database.data.lite.LiteBase)
	 */
	public void handleDBChangeMsg(DBChangeMsg msg, LiteBase lBase)
	{
		//see if the message originated from us
		if( !(msg.getSource().equals(
					com.cannontech.common.util.CtiUtilities.DEFAULT_MSG_SOURCE) ) )
		{
			CTILogger.debug(( msg.getTypeOfChange() == DBChangeMsg.CHANGE_TYPE_ADD ? "ADD" :
					(msg.getTypeOfChange() == DBChangeMsg.CHANGE_TYPE_DELETE ? "DELETE" :
						(msg.getTypeOfChange() == DBChangeMsg.CHANGE_TYPE_UPDATE ? "UPDATE" : ""))) +
				" Database Change Message received from: " + msg.getUserName() + " at " + msg.getSource());
		}
		else
			com.cannontech.clientutils.CTILogger.info("DBChange Message received that originated from ourself, doing nothing.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0)
	{
		
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0)
	{
		// TODO Auto-generated method stub
		
	}	
}
