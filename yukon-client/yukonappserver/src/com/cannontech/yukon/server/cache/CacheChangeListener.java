package com.cannontech.yukon.server.cache;

import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;
/**
 * @author rneuharth
 * Sep 25, 2002 at 4:07:47 PM
 * 
 * A undefined generated comment
 */
public class CacheChangeListener implements DBChangeListener
{
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
	public IServerConnection getClientConnection()
	{
		return ConnPool.getInstance().getDefDispatchConn(); 
    }
   
	/**
	 * @see com.cannontech.database.cache.DBChangeListener#handleDBChangeMsg(DBChangeMsg, LiteBase)
	 */
	public void handleDBChangeMsg(DBChangeMsg msg, LiteBase lBase)
	{
      ServerDatabaseCache.getInstance().handleDBChangeMessage( msg );
   }

}
