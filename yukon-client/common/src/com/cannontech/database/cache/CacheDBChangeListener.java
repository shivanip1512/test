package com.cannontech.database.cache;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;

/**
 * Insert the type's description here.
 * Creation date: (12/20/2001 1:06:33 PM)
 * @author: 
 */
/* This class will scan all the listeners ClientConnections that were added to
	 it, searching for DBChangeMessages.  Once found, the DBChangeMessage
	 will be forwarded on to the DefaultDatabaseCache AND on to the classes
	 handleDBChangeMsg( DBChangeMsg ) method.  That is why only classes
	 of type DBChangeListener can be added as a listener
	*/
public class CacheDBChangeListener implements MessageListener
{
	private com.cannontech.message.util.ClientConnection connection = null;
	private java.util.Vector dbChangeListeners = null;

/**
 * CacheDBChangeListener constructor comment.
 */
public CacheDBChangeListener() 
{
	super();
}

/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 2:02:40 PM)
 * @param listener com.cannontech.database.cache.DBChangeListener
 */
public synchronized void addDBChangeListener(DBChangeListener listener) 
{
	if( !getDbChangeListeners().contains(listener) )
	{
		getDbChangeListeners().add( listener );
		listener.getClientConnection().addMessageListener( this );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 2:01:55 PM)
 * @return java.util.Vector
 */
private synchronized java.util.Vector getDbChangeListeners() 
{
	if( dbChangeListeners == null )
		dbChangeListeners = new java.util.Vector(10);

	return dbChangeListeners;
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 2:02:40 PM)
 * @param listener com.cannontech.database.cache.DBChangeListener
 */
public synchronized void removeDBChangeListener(DBChangeListener listener) 
{
	getDbChangeListeners().remove( listener );
	listener.getClientConnection().removeMessageListener( this );
}

public void messageReceived( MessageEvent e )
{
	synchronized( getDbChangeListeners() )
	{
		for( int i = 0; i < getDbChangeListeners().size(); i++ )
		{
			DBChangeListener listener = (DBChangeListener)getDbChangeListeners().get(i);
				
			Object msg = e.getMessage();
			if (msg != null && msg instanceof DBChangeMsg )
			{
				//handle the Cache's DBChangeMessages
				LiteBase lBase = 
					DefaultDatabaseCache.getInstance().handleDBChangeMessage( 
						(DBChangeMsg)msg );

				//do the listeners handler of DBChangeMessages
				listener.handleDBChangeMsg( (DBChangeMsg)msg, lBase );
			}
		}
	}

}


}
