package com.cannontech.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.RoleFuncs;


public class ConnectionPool
{
   private String name;
   private String URL;
   private String user;
   private String password;
   private int maxConns;
   private int timeOut;
   private int initConns;

   private int checkedOut;
   private Vector freeConnections = new Vector();
   

   public ConnectionPool(String name, String URL, String user, 
	  String password, int maxConns, int initConns, int timeOut )
	  {
   
	  this.name = name;
	  this.URL = URL;
	  this.user = user;
	  this.password = password;
	  this.maxConns = maxConns;
	  this.timeOut = timeOut > 0 ? timeOut : 5;
	  this.initConns = initConns;
	  
	  String lf = System.getProperty("line.separator");
	  info("Creating new DB connection pool...");
	  debug(lf +
					" url=" + URL + lf +
					" user=" + user + lf +
					" initconns=" + initConns + lf +
					" maxconns=" + maxConns + lf +
					" logintimeout=" + this.timeOut );

	  //create the pools
	  initPool();


	  debug( getStats() );
   }

   public void freeConnection(Connection conn)
   {
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized (cache) {
		synchronized (this) {
			if( conn == null || checkedOut <= 0 )
			   return;
     
			 // Put the connection at the end of the Vector
			 freeConnections.addElement(conn);
			 checkedOut--;
			 notifyAll();
	  
			 debug("Returned/Added connection to pool");
			 debug( getStats() );
		}
	}
   }

   public Connection getConnection() throws SQLException 
   {

	  try
	  {
		 Connection conn = getConnection(timeOut * 1000);
		 ConnectionWrapper cw = new ConnectionWrapper(conn, this);

	 	 debug( "Request for a DB connection granted" );


	  
		  //The below line lets you find any connections that are not
		  //   releasing the connection to the DB!!  (Creates a lot of output!!)
		  //debug("   " + com.cannontech.common.util.CtiUtilities.getSTACK_TRACE() );

		 return cw;
	  }
	  catch (SQLException e)
	  {
		 error("Exception getting connection", e );
		 throw e;
	  }
   }                        
   private Connection getConnection(long timeout) 
					  throws SQLException
   {
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized (cache) {
		synchronized (this) {
			// Get a pooled Connection from the cache or a new one.
			// Wait if all are checked out and the max limit has
			// been reached.
			long startTime = System.currentTimeMillis();
			long remaining = timeout;
			Connection conn = null;
	
			while( (conn = getPooledConnection()) == null )
			{
			   try
			   {
				  debug("Waiting for connection. Timeout=" + remaining + " millis");
	
				  wait(remaining);
			   }
			   catch (InterruptedException e)
			   { }
	
			   remaining = timeout - (System.currentTimeMillis() - startTime);
			   if (remaining <= 0)
			   {
				  // Timeout has expired
				  debug("Time-out while waiting for connection" );
	
				  throw new SQLException("getConnection() timed-out");
			   }
			}
	
			// Check if the Connection is still OK
			if (!isConnectionOK(conn))
			{
			   closeConnection( conn );
	
			   //try to create a new one	  	
			   freeConnections.addElement( newConnection() );
		  	
			   // It was bad. Try again with the remaining timeout
			   debug("Removed bad connection from pool" );
	
			   return getConnection(remaining);
			}
			else
			{
				checkedOut++;
				debug( "Delivered connection from pool" );
				debug( getStats() );
			  
			
				// Great we have a good conn, Be sure we have our fair share of DB conns
				int totalConns = freeConnections.size() + checkedOut;
				  for( int i = totalConns; i < initConns; i++ )
					 freeConnections.add( newConnection() );
			  
				return conn;
			}
		}
	}

   }   

	/* closes a free connection that is not being used */
	private void closeConnection ( Connection conn )
	{
	 	freeConnections.remove( conn );

		try
		{
			conn.close();
			debug( "Closed connection" );
		}
		catch (SQLException e)
		{
			error( "Couldn't close connection", e );
		}
	}

	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/17/00 11:49:47 AM)
	 * @return java.lang.String
	 */
	public String getName() {
		return name;
	}

   private Connection getPooledConnection() throws SQLException
   {
	  Connection conn = null;
	  if (freeConnections.size() > 0)
	  {
		 // Pick the first Connection in the Vector
		 // to get round-robin usage
		 conn = (Connection) freeConnections.firstElement();		 
	 	 freeConnections.removeElementAt(0);
	  }
	  else if (maxConns == 0 || checkedOut < maxConns )
	  {
		 conn = newConnection();
	  }
	  

	  return conn;
   }
   
	private String getStats() 
	{
		return "POOL STATE: Total connections: " + 
				(freeConnections.size() + checkedOut) + ", " +
				checkedOut + "/" + freeConnections.size() +
				" (Checked-out/Available)";
	}
	      
   private void initPool()
   {
	  for (int i = 0; i < initConns; i++)
	  {
		 try
		 {
			Connection pc = newConnection();
			freeConnections.addElement(pc);
		 }
		 catch (SQLException e)
		 { }
	  }
   }

   private boolean isConnectionOK(Connection conn)
   {
	  
	  try
	  {
		 if (!conn.isClosed())
		 {
		 	//something used to query the DB, works for all drivers
		 	conn.getMetaData().getTypeInfo().close();
		 }
		 else
		 {
			return false;
		 }
	  }
	  catch (Exception e)
	  {
		 info( "Pooled Connection was NOT okay" );
		 error( "Pooled Connection was NOT okay", e );

		 return false;
	  }

	  return true;
   }

   private Connection newConnection() throws SQLException
   {
	  Connection conn = null;
	  
	  /*************************************************************************/
	  /** THESE PROPERTIES MUST BE RECOGNIZED BY THE DB DRIVER TO WORK!!!     **/
	  /*************************************************************************/
	  java.util.Properties p = new java.util.Properties();
	  p.put("user", user);
	  p.put("password", password);
	  p.put("programname", System.getProperty("cti.app.name", "YukonClient"));
	  //p.put("timeout", "5");
	  
	  try
	  {
	  	  conn = DriverManager.getConnection(URL, p);  	  

	  	  debug("Opened a new connection" );
	  }
	  catch( Exception e )
	  {
	  	  //try to connect the old way!
		  //conn = DriverManager.getConnection(URL, user, password);
		  //debug("Opened a new connection using an out dated connection method" );	  	
	  }

	  return conn;
   }
   
   public void release()
   {
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized (cache) {
		synchronized (this) {
			Enumeration allConnections = freeConnections.elements();
			while (allConnections.hasMoreElements())
			{
			   Connection con = (Connection) allConnections.nextElement();
			   try
			   {
				  con.close();
				  debug( "Closed connection" );
			   }
			   catch (SQLException e)
			   {
				  error( "Couldn't close connection", e );
			   }
			}
			freeConnections.removeAllElements();
		}
	}
   }


	private void debug( String msg )
	{
		if( RoleFuncs.hasLoadedGlobals() )
			CTILogger.debug( msg );
		else
			CTILogger.getStandardLog().debug( msg );
	}

	private void error( String msg, Throwable t )
	{
		if( RoleFuncs.hasLoadedGlobals() )
			CTILogger.error( msg, t );
		else
			CTILogger.getStandardLog().error( msg, t );
	}

	private void info( String msg )
	{
		if( RoleFuncs.hasLoadedGlobals() )
			CTILogger.info( msg );
		else
			CTILogger.getStandardLog().info( msg );
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2/20/2002 10:43:51 AM)
	 * @return java.lang.String
	 */
	public String toString() 
	{
	   //private String name;
	   //private String URL;
	   //private String user;
	   //private String password;
		return user + " @ " + URL;
	}
   
   protected void wrapperClosed(Connection conn)
	{
	  freeConnection( conn );
   }

}
