package com.cannontech.database.cache;

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

public class CacheDBChangeListener implements Runnable 
{
	private com.cannontech.message.util.ClientConnection connection = null;
	private java.util.Vector dbChangeListeners = null;
	private Thread listenerThread = new Thread("CacheDBChangeListener");

	private static final int REFRESH_RATE = 1000; //every second

	//(5 minutes) how many REFRESH_RATE to wait
	private static final int REFRESH_GC = 300;
/**
 * CacheDBChangeListener constructor comment.
 */
public CacheDBChangeListener() 
{
	super();

	listenerThread = new Thread(this, "CacheDBChangeListener");
	listenerThread.setDaemon(true);
	listenerThread.start();
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 2:02:40 PM)
 * @param listener com.cannontech.database.cache.DBChangeListener
 */
public synchronized void addDBChangeListener(DBChangeListener listener) 
{
	if( !getDbChangeListeners().contains(listener) )
		getDbChangeListeners().add( listener );

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
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 1:06:57 PM)
 */
public void run() 
{
	try
	{
		int cnt = 0;

		while( true )
		{
			Thread.sleep(REFRESH_RATE);
			cnt++;

			synchronized( getDbChangeListeners() )
			{
				for( int i = 0; i < getDbChangeListeners().size(); i++ )
				{
					DBChangeListener listener = (DBChangeListener)getDbChangeListeners().get(i);
				
					Object msg = listener.getClientConnection().read(0);
					if (msg != null)
					{
						if (msg instanceof com.cannontech.message.dispatch.message.DBChangeMsg)
						{
							//handle the Cache's DBChangeMessages
							com.cannontech.database.data.lite.LiteBase lBase = DefaultDatabaseCache.getInstance().handleDBChangeMessage( 
								(com.cannontech.message.dispatch.message.DBChangeMsg)msg );

							//do the listeners handler of DBChangeMessages
							listener.handleDBChangeMsg( 
								(com.cannontech.message.dispatch.message.DBChangeMsg)msg, lBase );
						}
					}
				}
			}

			//force the GC every so often
			if( (REFRESH_GC % cnt) == 0 )
			{
				System.gc();
				//com.cannontech.clientutils.CTILogger.info("*** System.gc() called");
			}

		}
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.info( "**** " + this.getClass().getName() + " had an unexpected Thread death");
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
}
}
