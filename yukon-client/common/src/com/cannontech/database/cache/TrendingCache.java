package com.cannontech.database.cache;

/**
 * Insert the type's description here.
 * Creation date: (10/4/00 5:07:47 PM)
 * @author: 
 */
public class TrendingCache  {
	private static TrendingCache instance = null;

	private java.util.ArrayList allGraphDefinitions = null;	
	private String databaseAlias = "yukon";
/**
 * TrendingCache constructor comment.
 */
protected TrendingCache() {
	super();
}
/**
 * TrendingCache constructor comment.
 */
protected TrendingCache(String databaseAlias) {
	super();
	setDatabaseAlias(databaseAlias);
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/00 5:12:24 PM)
 * @return java.util.List
 */
public synchronized java.util.List getAllGraphDefinitions() {
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	return cache.getAllGraphDefinitions();
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/00 5:10:16 PM)
 */
public synchronized static TrendingCache getInstance() 
{
	if( instance == null )
		instance = new TrendingCache();

	return instance;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:22:47 PM)
 */
public synchronized void handleDBChangeMessage(com.cannontech.message.dispatch.message.DBChangeMsg dbChangeMsg){

	// certainly overkill, should be more specific
	// usually a db change won't relate to graphdefinitions
	releaseAllGraphDefinitions();	
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 11:02:36 AM)
 */
public synchronized void loadAllCache()
{
	getAllGraphDefinitions();
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/00 5:14:39 PM)
 */
public synchronized void releaseAllGraphDefinitions() 
{
	allGraphDefinitions = null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/4/00 5:07:47 PM)
 * @param newAlias java.lang.String
 */
public void setDatabaseAlias(String databaseAlias) 
{
	this.databaseAlias = databaseAlias;
}
}
