package com.cannontech.yukon.server.cache;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.mbean.*;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.roles.yukon.SystemRole;
/**
 * @author rneuharth
 * Sep 25, 2002 at 4:07:47 PM
 * 
 * A undefined generated comment
 */
public class CacheChangeListener implements DBChangeListener
{
   private ClientConnection connToDispatch = null;
   
	/**
	 * Constructor for CacheChangeListener.
	 */
	public CacheChangeListener()
	{
		super();
	}

	/**
	 * @see com.cannontech.database.cache.DBChangeListener#getClientConnection()
	 */
	public ClientConnection getClientConnection()
	{

      if( connToDispatch == null )
      {
         String host = "127.0.0.1";
         int port = 1510;
         try
         {
            host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );

            port = Integer.parseInt(
							RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) );
         }
         catch( Exception e)
         {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
         }
   
         connToDispatch = new com.cannontech.message.dispatch.ClientConnection();
         com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
         reg.setAppName( CtiUtilities.getAppRegistration() );
         reg.setAppIsUnique(0);
         reg.setAppKnownPort(0);
         reg.setAppExpirationDelay( 300 );  // 5 minutes should be OK
   
         //connToDispatch.addObserver(this);
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
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
         }
      }
   
      return connToDispatch;
	}

	/**
	 * @see com.cannontech.database.cache.DBChangeListener#handleDBChangeMsg(DBChangeMsg, LiteBase)
	 */
	public void handleDBChangeMsg(DBChangeMsg msg, LiteBase lBase)
	{
      ServerDatabaseCache.getInstance().handleDBChangeMessage( msg );
   }

}
