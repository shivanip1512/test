package com.cannontech.database;

import java.sql.*;
import java.io.*;
import java.util.*;

import com.cannontech.common.util.LogWriter;
import com.cannontech.clientutils.CTILogger;

public class PoolManager
{
	public static final String DB_PROPERTIES_FILE  = "/db.properties";
	
   static private PoolManager instance;
   static private int clients;

//   private LogWriter logWriter;
   private PrintWriter pw;

   private Vector drivers = new Vector();
   private Hashtable pools = new Hashtable();

   private PoolManager()
   {
	  init();
   }
   private void createPools(Properties props)
   {
	  Enumeration propNames = props.propertyNames();
	  while (propNames.hasMoreElements())
	  {   
		 String name = (String) propNames.nextElement();
		 if (name.endsWith(".url"))
		 {   
			String poolName = name.substring(0, name.lastIndexOf("."));
			String url = props.getProperty(poolName + ".url");
			if (url == null)
			{   
//			   logWriter.log("No URL specified for " + poolName,
//				  LogWriter.ERROR);
              
            CTILogger.error( "No URL specified for " + poolName );
			   continue;
			}
   
			String user = props.getProperty(poolName + ".user");
			String password = props.getProperty(poolName + ".password");
   
			String maxConns = props.getProperty(poolName + 
			   ".maxconns", "0");
			int max;
			try
			{
			   max = Integer.valueOf(maxConns).intValue();
			}
			catch (NumberFormatException e)
			{
//			   logWriter.log("Invalid maxconns value " + maxConns + " for " + poolName, LogWriter.ERROR);
            CTILogger.error("Invalid maxconns value " + maxConns + " for " + poolName);
			   max = 0;
			}
   
			String initConns = props.getProperty(poolName + 
							  ".initconns", "0");
			int init;
			try
			{
			   init = Integer.valueOf(initConns).intValue();
			}
			catch (NumberFormatException e)
			{
//			   logWriter.log("Invalid initconns value " + initConns + " for " + poolName, LogWriter.ERROR);
                      
            CTILogger.error( "Invalid initconns value " + initConns + " for " + poolName );
			   init = 0;
			}
   
			String loginTimeOut = props.getProperty(poolName + 
			   ".logintimeout", "5");
			int timeOut;
			try
			{
			   timeOut = Integer.valueOf(loginTimeOut).intValue();
			}
			catch (NumberFormatException e)
			{
//			   logWriter.log("Invalid logintimeout value " + loginTimeOut + " for " + poolName, LogWriter.ERROR);
            CTILogger.info("Invalid logintimeout value " + loginTimeOut + " for " 
                  + poolName + ", defaulting to 5" );
                  
			   timeOut = 5;
			}
   
/*			String logLevelProp = props.getProperty(poolName + 
						   ".loglevel", String.valueOf(LogWriter.ERROR));

			int logLevel = LogWriter.INFO;
			if (logLevelProp.equalsIgnoreCase("none"))
			{
			   logLevel = LogWriter.NONE;
			}
			else if (logLevelProp.equalsIgnoreCase("error"))
			{
			   logLevel = LogWriter.ERROR;
			}
			else if (logLevelProp.equalsIgnoreCase("debug"))
			{
			   logLevel = LogWriter.DEBUG;
			}
*/
			ConnectionPool pool =
						   new ConnectionPool(poolName, url, user, password,
											max, init, timeOut );

			pools.put(poolName, pool);
		 }
	  }
   }
   public void freeConnection(String name, Connection con)
   {
	  ConnectionPool pool = (ConnectionPool) pools.get(name);
	  if (pool != null)
	  {
		 pool.freeConnection(con);
	  }
   }
/**
 * Insert the method's description here.
 * Creation date: (2/20/2002 10:48:47 AM)
 * @return java.util.Enumeration
 */
public String[] getAllPoolsStrings()
{
	Enumeration enum = pools.elements();
	String[] strs = new String[ pools.size() ];
	int i = 0;
	
	while ( enum.hasMoreElements() )
		strs[i++] = enum.nextElement().toString();
		
	return strs;
}
   public Connection getConnection(String name)
   {
	  Connection conn = null;
	  ConnectionPool pool = (ConnectionPool) pools.get(name);
	  if (pool != null)
	  {
		 try
		 {
			conn = pool.getConnection();
		 }
		 catch (SQLException e)
		 {
//			logWriter.log(e, "Exception getting connection from " +
//			   name, LogWriter.ERROR);
         CTILogger.error("Exception getting connection from " +
            name, e );
         
		 }
	  }

	  return conn;
   }      
   static synchronized public PoolManager getInstance()
   {
	  if (instance == null)
	  {
		 instance = new PoolManager();
	  }
	  clients++;
	  return instance;
   }
   private void init()
   {
	  // Log to System.err until we have read the logfile property
	  pw = new PrintWriter(System.err, true);
//	  logWriter = new LogWriter("PoolManager", LogWriter.INFO, pw);
	  InputStream is = getClass().getResourceAsStream( DB_PROPERTIES_FILE );
	  Properties dbProps = new Properties();
	  try
	  {
		 dbProps.load(is);
	  }
	  catch (Exception e)
	  {
//		 logWriter.log("Can't read the properties file. " +
//			"Make sure db.properties is in the CLASSPATH",
//			LogWriter.ERROR);
       CTILogger.error("Can't read the properties file. " +
         "Make sure db.properties is in the CLASSPATH" );
         
		 return;
	  }

/*	  String logFile = dbProps.getProperty("logfile");
	  if (logFile != null)
	  {		 
		 try
		 {
			pw = new PrintWriter(new FileWriter( com.cannontech.common.util.CtiUtilities.getLogDirPath() + logFile, true), true);
			logWriter.setPrintWriter(pw);
		 }
		 catch (IOException e)
		 {
			logWriter.log("Can't open the log file: " + com.cannontech.common.util.CtiUtilities.getLogDirPath() + "/" + logFile +
			   ". Using System.err instead", LogWriter.ERROR);
		 }
	  }
	  else
	  {
		   com.cannontech.clientutils.CTILogger.info("*** LogFile value not found, not logging database activity."); 
			pw = null;
	  }
*/

	  String printSQLfile = dbProps.getProperty("PrintSQL");
	  if (printSQLfile != null)
	  {		 
		 try
		 {
			 com.cannontech.database.db.DBPersistent.setSQLFileName( com.cannontech.common.util.CtiUtilities.getLogDirPath() + "/" + printSQLfile );
			 com.cannontech.database.db.DBPersistent.setPrintSQL( true );
		 }
		 catch (Exception e)
		 {
			 // no biggy!
			 com.cannontech.clientutils.CTILogger.info("*** Unable to set the PrintSQL to the filenamed : " + com.cannontech.common.util.CtiUtilities.getLogDirPath() + "/" + printSQLfile );
		 }
	  }
 
	  loadDrivers(dbProps);
	  createPools(dbProps);
   }                                                                           
   private void loadDrivers(Properties props)
   {
	  String driverClasses = props.getProperty("drivers");
	  StringTokenizer st = new StringTokenizer(driverClasses);
	  while (st.hasMoreElements())
	  {
		 String driverClassName = st.nextToken().trim();
		 try
		 {
			Driver driver = (Driver)
			   Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(driver);
			drivers.addElement(driver);
//			logWriter.log("Registered JDBC driver " + driverClassName,
//			   LogWriter.INFO);

         CTILogger.info("Registered JDBC driver " + driverClassName );
		 }
		 catch (Exception e)
		 {
         CTILogger.error("Can't register JDBC driver: " +
                  driverClassName, e );
         
//			logWriter.log(e, "Can't register JDBC driver: " +
//			   driverClassName, LogWriter.ERROR);
		 }
	  }
   }
   public synchronized void release()
   {
	  // Wait until called by the last client
	  if (--clients != 0)
	  {
		 return;
	  }
   
	  Enumeration allPools = pools.elements();
	  while (allPools.hasMoreElements())
	  {
		 ConnectionPool pool = (ConnectionPool) allPools.nextElement();
		 pool.release();
	  }

	  Enumeration allDrivers = drivers.elements();
	  while (allDrivers.hasMoreElements())
	  {
		 Driver driver = (Driver) allDrivers.nextElement();
		 try
		 {
			DriverManager.deregisterDriver(driver);
			CTILogger.info("Deregistered JDBC driver " + driver.getClass().getName() );
		 }
		 catch (SQLException e)
		 {
         CTILogger.error("Couldn't deregister JDBC driver: " + 
							 driver.getClass().getName(), e );
		 }
	  }
   }
}
