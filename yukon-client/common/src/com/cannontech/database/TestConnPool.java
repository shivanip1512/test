package com.cannontech.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class tests the basic behavior of the connectiom pool
 * To run the test you must have a db.properties
 * file with a definition of a pool named "demo", with maxconns
 * set to 1. It's recommended that you do not specify a log file
 * in db.properties and use loglevel debug for the "demo" pool, so 
 * that you can see all pool messages intermixed with the messages 
 * printed by this class.
 */
public class TestConnPool implements Runnable
{
   static public void main(String[] args)
   {
	  PoolManager poolManager = PoolManager.getInstance();
	  
	  // Get a connection and return it
	  com.cannontech.clientutils.CTILogger.info("Get a connection and return it:");
	  Connection conn = poolManager.getConnection("demo");
	  if (conn != null)
	  {
		 com.cannontech.clientutils.CTILogger.info("  Got connection!");
		 poolManager.freeConnection("demo", conn);
	  }
		
	  // Try to get a connection from a pool that doesn't exist
	  com.cannontech.clientutils.CTILogger.info("Try to get a connection from a pool that doesn't exist:");
	  conn = poolManager.getConnection("foo");
	  if (conn == null)
	  {
		 com.cannontech.clientutils.CTILogger.info("  Didn't get connection!");
	  }

	  // Get a connection, close it, return it and try to get it
	  // again. The pool should throw away the closed connection
	  // and open a new one
	  com.cannontech.clientutils.CTILogger.info("Get a connection when the pooled one is bad:");
	  conn = poolManager.getConnection("demo");
	  if (conn != null)
	  {
		 com.cannontech.clientutils.CTILogger.info("  Got connection! Close it and return it");
		 try
		 {
			conn.close();
		 }
		 catch (SQLException e) {}
		 
		 poolManager.freeConnection("demo", conn);
	  }
	  
	  conn = poolManager.getConnection("demo");
	  if (conn != null)
	  {
		 com.cannontech.clientutils.CTILogger.info("  Got connection!");
		 poolManager.freeConnection("demo", conn);
	  }
	   
	  // Try to get a connection from a pool that's empty.
	  // This requires the demo pool to be configured for
	  // max 1 connection. The second request should time-out
	  com.cannontech.clientutils.CTILogger.info("Try to get a connection from a pool that's empty:");
	  Connection conn1 = poolManager.getConnection("demo");
	  if (conn1 != null)
	  {
		 com.cannontech.clientutils.CTILogger.info("  Got the first connection!");
	  }
	  
	  Connection conn2 = poolManager.getConnection("demo");
	  if (conn2 == null)
	  {
		 com.cannontech.clientutils.CTILogger.info("  Didn't get the second connection!");
	  }
	  
	  poolManager.freeConnection("demo", conn1);

	  // Wait for a connection until one is returned.
	  // This Thread gets the only connection and starts another
	  // Thread to also try to get one. Before the time-out,
	  // this Thread returns its connection
	  com.cannontech.clientutils.CTILogger.info("Wait for a connection until another thread returns one:");
	  conn1 = poolManager.getConnection("demo");
	  if (conn1 != null)
	  {
		 com.cannontech.clientutils.CTILogger.info("  Got the first connection!");
		 Thread thread = new Thread(new TestConnPool());
		 thread.start();
		 try
		 {
			Thread.sleep(2000);
		 }
		 catch (InterruptedException e)
		 { }
		 
		 poolManager.freeConnection("demo", conn1);
	  }
	  
	  poolManager.release();
   }
   /**
	* Runs a getConnection() in a separate Thread to test time-outs
	*/
   public void run()
   {
	  PoolManager poolManager = PoolManager.getInstance();
	  Connection conn = poolManager.getConnection("demo");
	  if (conn != null)
	  {
		 com.cannontech.clientutils.CTILogger.info("  Got the connection after waiting!");
		 poolManager.freeConnection("demo", conn);
	  }
	  
	  poolManager.release();
   }
}
