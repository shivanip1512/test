package com.cannontech.database.cache;

/**
 * This type was created in VisualAge.
 */
public class TestCache {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	System.out.println("Test Started...");
	//temp code
	java.util.Date timerStart = null;
	java.util.Date timerStop = null;
	//temp code

	//temp code
	timerStart = new java.util.Date();
	//temp code

	DefaultDatabaseCache defaultDatabaseCache = DefaultDatabaseCache.getInstance();

	defaultDatabaseCache.loadAllCache();
	//temp code
	timerStop = new java.util.Date();
	//System.out.print( (timerStop.getTime() - timerStart.getTime())*.001 );
	//System.out.println( " Secs for LOADALLCACHE()" );
	
	//defaultDatabaseCache.getAllDevices();
	//defaultDatabaseCache.getAllPoints();
	//defaultDatabaseCache.getAllRoutes();

	System.exit(0);
}
}
