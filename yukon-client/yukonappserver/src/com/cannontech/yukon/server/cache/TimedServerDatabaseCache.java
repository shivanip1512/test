package com.cannontech.yukon.server.cache;


/**
 * Insert the type's description here.
 * Creation date: (3/14/00 3:20:44 PM)
 * @author: 
 */ 

public class TimedServerDatabaseCache implements com.cannontech.yukon.ITimedDatabaseCache
{
	//stores a soft reference to the cache
	private static TimedServerDatabaseCache cache = null;
	private String databaseAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
	private java.util.ArrayList allPeakPointHistory = null;
	private TimedUpdater timed = null;
	private long updateTimeInMillis = 21600000;	//DEFAULT TO 6 hours
/**
 * DefaultDatabaseCache constructor comment.
 */
protected TimedServerDatabaseCache() {
	super();
}
/**
 * DefaultDatabaseCache constructor comment.
 */
protected TimedServerDatabaseCache(String databaseAlias) {
	super();
	setDatabaseAlias(databaseAlias);
}


//private class TimeUpdate extends java.util.Timer
//{
//	
//}

private class TimedUpdater extends Thread
{
    public TimedUpdater() {
        super("TimedServerDatabaseCacheUpdater");
    }
	public void run()
	{
		while (true)
		{
			try
			{
				//interval rate is in seconds (* 1000 for millis)
				//sleep for 6 hours...FOR NOW!
				Thread.sleep( getUpdateTimeInMillis() );
			}
			catch (InterruptedException ie)
			{
				return;
			}
			com.cannontech.clientutils.CTILogger.info("CACHE:  UPDATING TIMED CACHE.");
			//UPDATE peakPointHistory
			allPeakPointHistory = null;
			getAllPeakPointHistory();
		}
	}
}

public void setUpdateTimeInMillis(long millis)
{
	updateTimeInMillis = millis;
}
public long getUpdateTimeInMillis()
{
	return updateTimeInMillis;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 * @return java.util.Collection
 */
public synchronized java.util.List getAllPeakPointHistory(){

	if( allPeakPointHistory != null )
		return allPeakPointHistory;
	else
	{
		allPeakPointHistory = new java.util.ArrayList();
		PeakPointHistoryLoader pointLoader = new PeakPointHistoryLoader(allPeakPointHistory, databaseAlias);
		pointLoader.run();

		if( timed == null)
		{
			timed = new TimedUpdater();
			timed.start();
		}

		return allPeakPointHistory ;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:19:19 PM)
 */
public static synchronized com.cannontech.yukon.ITimedDatabaseCache getInstance()
{
	if( cache == null )
	{
		com.cannontech.clientutils.CTILogger.info("CACHE: CREATING NEW TIMED CACHE REFERENCE OBJECT");
		cache = new TimedServerDatabaseCache();
	}

	return cache;

}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void loadAllTimedCache()
{
	allPeakPointHistory = new java.util.ArrayList();
	
        //be sure all of our derived storage is cleard
	allPeakPointHistory = null;	

	
	Runnable[] runners =
	{
		new PeakPointHistoryLoader(allPeakPointHistory, databaseAlias),
	};


	//Just use 1 Thread to load the DB for now
	for( int i = 0 ; i < runners.length; i++ )
		runners[i].run();
	
/*
	try
	{
java.util.Date timerStart = new java.util.Date();
		runLoaders( runners );
com.cannontech.clientutils.CTILogger.info( 
	((new java.util.Date().getTime() - timerStart.getTime())*.001) + " Secs for LOADER" );

	}
	catch( Exception e )
	{

		//oops something went wrong, just load all cache in 1 thread
		for( int i = 0 ; i < runners.length; i++ )
			runners[i].run();
	}
*/

}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllTimedCache()
{
	allPeakPointHistory = null;

	//be sure all of our derived storage is cleard
	allPeakPointHistory = null;	
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void releaseAllPeakPointHistory(){

	allPeakPointHistory = null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2002 5:36:31 PM)
 * @param loaders java.lang.Runnable[]
 */
private void runLoaders(Runnable[] loaders) throws Exception
{

	// The 2 in the ThreadPool constructor is the max number of db connections
	com.cannontech.common.util.ThreadPool tp = new com.cannontech.common.util.ThreadPool(2);
	for( int i = 0; i < loaders.length; i++ )
		tp.enqueueRunnable( loaders[i] );

	tp.stop();
	tp.join();
	
/*	Thread t1 = new Thread();
	Thread t2 = new Thread();
	int i = 0;
	while( i < loaders.length )
	{
		if( !t1.isAlive() )
		{
			t1 = new Thread(loaders[i++]);
			t1.start();
		}

		if( !t2.isAlive() )
		{
			t2 = new Thread(loaders[i++]);
			t2.start();
		}

		Thread.currentThread().sleep(5);
	}
	t1.join();
	t2.join();
*/
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 * @param newAlias java.lang.String
 */
public void setDatabaseAlias(String newAlias){
	databaseAlias = newAlias;
}
}
