package com.cannontech.database;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;


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
	  
	  initPool();

	  CTILogger.debug("New pool created");
	  String lf = System.getProperty("line.separator");
	  CTILogger.debug(lf +
					" url=" + URL + lf +
					" user=" + user + lf +
					" initconns=" + initConns + lf +
					" maxconns=" + maxConns + lf +
					" logintimeout=" + this.timeOut );
	  CTILogger.debug( getStats() );
   }            
   public synchronized void freeConnection(Connection conn)
   {
	  // Put the connection at the end of the Vector
	  freeConnections.addElement(conn);
	  checkedOut--;
	  notifyAll();
	  CTILogger.debug("Returned/Added connection to pool");
	  CTILogger.debug( getStats() );
   }   
   public Connection getConnection() throws SQLException 
   {
	  CTILogger.debug( "Request for connection received" );

	  //The below line lets you find any connections that are not
	  //   releasing the connection to the DB!!  (Creates a lot of output!!)
	  //CTILogger.debug("   " + com.cannontech.common.util.CtiUtilities.getSTACK_TRACE() );

	  try
	  {
		 Connection conn = getConnection(timeOut * 1000);
		 return new ConnectionWrapper(conn, this);
	  }
	  catch (SQLException e)
	  {
		 CTILogger.error("Exception getting connection", e );
		 throw e;
	  }
   }                        
   private synchronized Connection getConnection(long timeout) 
					  throws SQLException
   {

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
			CTILogger.debug("Waiting for connection. Timeout=" + remaining );

			wait(remaining);
		 }
		 catch (InterruptedException e)
		 { }

		 remaining = timeout - (System.currentTimeMillis() - startTime);
		 if (remaining <= 0)
		 {
			// Timeout has expired
			CTILogger.debug("Time-out while waiting for connection" );

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
		 CTILogger.debug("Removed bad connection from pool" );

		 return getConnection(remaining);
	  }
	  else
	  {
		  checkedOut++;
		  CTILogger.debug( "Delivered connection from pool" );
		  CTILogger.debug( getStats() );
		  
		
		  // Great we have a good conn, Be sure we have our fair share of DB conns
		  int totalConns = freeConnections.size() + checkedOut;
		    for( int i = totalConns; i < initConns; i++ )
			   freeConnections.add( newConnection() );
		  
		  return conn;
	  }

   }   

	/* closes a free connection that is not being used */
	private void closeConnection ( Connection conn )
	{
	 	freeConnections.remove( conn );

		try
		{
			conn.close();
			CTILogger.debug( "Closed connection" );
		}
		catch (SQLException e)
		{
			CTILogger.error( "Couldn't close connection", e );
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
		 	conn.getMetaData().getTypeInfo();		 	
		 }
		 else
		 {
			return false;
		 }
	  }
	  catch (SQLException e)
	  {
		 CTILogger.info( "Pooled Connection was NOT okay" );
		 CTILogger.error( "Pooled Connection was NOT okay", e );

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
	  p.put("programname", System.getProperty("cti.app.name"));

	  try
	  {
	  	  conn = DriverManager.getConnection(URL, p);
	  	  CTILogger.debug("Opened a new connection" );
	  }
	  catch( Exception e )
	  {
	  	  //try to connect the old way!
		  conn = DriverManager.getConnection(URL, user, password);
		  CTILogger.debug("Opened a new connection using an out dated connection method" );	  	
	  }

	  return conn;
   }
   
   public synchronized void release()
   {
	  Enumeration allConnections = freeConnections.elements();
	  while (allConnections.hasMoreElements())
	  {
		 Connection con = (Connection) allConnections.nextElement();
		 try
		 {
			con.close();
			CTILogger.debug( "Closed connection" );
		 }
		 catch (SQLException e)
		 {
			CTILogger.error( "Couldn't close connection", e );
		 }
	  }
	  freeConnections.removeAllElements();
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
   
   protected synchronized void wrapperClosed(Connection conn)
	{
	  // Put the connection at the end of the Vector
	  freeConnections.addElement(conn);
	  checkedOut--;
	  notifyAll();
	  CTILogger.debug( "Returned connection to pool" );
	  CTILogger.debug( getStats() );
   }   
}
