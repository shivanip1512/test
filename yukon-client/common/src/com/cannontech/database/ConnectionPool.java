package com.cannontech.database;

import java.sql.*;
import java.util.*;

import java.io.*;

import com.cannontech.common.util.LogWriter;


public class ConnectionPool
{

   private String name;
   private String URL;
   private String user;
   private String password;
   private int maxConns;
   private int timeOut;
   private LogWriter logWriter;
   

   private int checkedOut;
   private Vector freeConnections = new Vector();

   public ConnectionPool(String name, String URL, String user, 
	  String password, int maxConns, int initConns, int timeOut,
	  PrintWriter pw, int logLevel)
	  {
   
	  this.name = name;
	  this.URL = URL;
	  this.user = user;
	  this.password = password;
	  this.maxConns = maxConns;
	  this.timeOut = timeOut > 0 ? timeOut : 5;

	  logWriter = new LogWriter(name, logLevel, pw);
	  initPool(initConns);

	  logWriter.log("New pool created", LogWriter.INFO);
	  String lf = System.getProperty("line.separator");
	  logWriter.log(lf +
					" url=" + URL + lf +
					" user=" + user + lf +
					" initconns=" + initConns + lf +
					" maxconns=" + maxConns + lf +
					" logintimeout=" + this.timeOut, LogWriter.INFO);
	  logWriter.log(getStats(), LogWriter.INFO);
   }            
   public synchronized void freeConnection(Connection conn)
   {
	  // Put the connection at the end of the Vector
	  freeConnections.addElement(conn);
	  checkedOut--;
	  notifyAll();
	  logWriter.log("Returned connection to pool", LogWriter.INFO);
	  logWriter.log(getStats(), LogWriter.INFO);
   }   
   public Connection getConnection() throws SQLException 
   {
	  logWriter.log("Request for connection received", LogWriter.DEBUG);

	  //The below line lets you find any connections that are not
	  //   releasing the connection to the DB!!  (Creates a lot of output!!)
	  logWriter.log("   " + com.cannontech.common.util.CtiUtilities.getSTACK_TRACE(), LogWriter.DEBUG);

	  try
	  {
		 Connection conn = getConnection(timeOut * 1000);
		 return new ConnectionWrapper(conn, this);
	  }
	  catch (SQLException e)
	  {
		 logWriter.log(e, "Exception getting connection", 
			logWriter.ERROR);
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
	  while ((conn = getPooledConnection()) == null)
	  {
		 try
		 {
			logWriter.log("Waiting for connection. Timeout=" + remaining,
						  LogWriter.DEBUG);
			wait(remaining);
		 }
		 catch (InterruptedException e)
		 { }
		 remaining = timeout - (System.currentTimeMillis() - startTime);
		 if (remaining <= 0)
		 {
			// Timeout has expired
			logWriter.log("Time-out while waiting for connection", 
						  LogWriter.DEBUG);
			throw new SQLException("getConnection() timed-out");
		 }
	  }

	  // Check if the Connection is still OK
	  if (!isConnectionOK(conn))
	  {
		 // It was bad. Try again with the remaining timeout
		 logWriter.log("Removed selected bad connection from pool", 
					   LogWriter.ERROR);
		 return getConnection(remaining);
	  }
	  checkedOut++;
	  logWriter.log("Delivered connection from pool", LogWriter.INFO);
	  logWriter.log(getStats(), LogWriter.INFO);
	  return conn;
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
	  else if (maxConns == 0 || checkedOut < maxConns)
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
   private void initPool(int initConns)
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
	  Statement testStmt = null;
	  try
	  {
		 if (!conn.isClosed())
		 {
			// Try to createStatement to see if it's really alive
			testStmt = conn.createStatement();
			testStmt.close();
		 }
		 else
		 {
			return false;
		 }
	  }
	  catch (SQLException e)
	  {
		 if (testStmt != null)
		 {
			try
			{
			   testStmt.close();
			}
			catch (SQLException se)
			{ }
		 }
		 logWriter.log(e, "Pooled Connection was not okay", 
						   LogWriter.ERROR);
		 return false;
	  }
	  return true;
   }
   private Connection newConnection() throws SQLException
   {
	  Connection conn = null;
	  if (user == null) {
		 conn = DriverManager.getConnection(URL);
	  }
	  else {
		 conn = DriverManager.getConnection(URL, user, password);
	  }
	  logWriter.log("Opened a new connection", LogWriter.INFO);
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
			logWriter.log("Closed connection", LogWriter.INFO);
		 }
		 catch (SQLException e)
		 {
			logWriter.log(e, "Couldn't close connection", LogWriter.ERROR);
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
   synchronized void wrapperClosed(Connection conn)
	  {
	  // Put the connection at the end of the Vector
	  freeConnections.addElement(conn);
	  checkedOut--;
	  notifyAll();
	  logWriter.log("Returned connection to pool", LogWriter.INFO);
	  logWriter.log(getStats(), LogWriter.INFO);
   }   
}
