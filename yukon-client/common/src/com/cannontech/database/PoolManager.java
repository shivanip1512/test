package com.cannontech.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.yukon.concrete.ResourceFactory;

public class PoolManager
{
	public static final String DB_PROPERTIES_FILE  = "/db.properties";
	
   static private Properties dbProps;
   static private PoolManager instance;
   static private int clients;

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
            	CTILogger.info("Invalid logintimeout value " + loginTimeOut + " for " 
                  				+ poolName + ", defaulting to 5" );                  
			   	timeOut = 5;
			}
   
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
         	CTILogger.error("Exception getting connection from " + name, e );         
		 }
	  }

	  return conn;
   }      
   
   static synchronized public Properties loadDBProperties() 
   {
   		Properties props = null;
   		
		InputStream is = PoolManager.class.getResourceAsStream( DB_PROPERTIES_FILE );
		try
		{
			props = new Properties();
			props.load(is);
		 }
		 catch (Exception e)
		 {
		  CTILogger.error("Can't read the properties file. " +
		 	"Make sure db.properties is in the CLASSPATH" );
		 }	
		 finally 
		 {
		 	return props;
		 }
   }
   
   static synchronized public void setDBProperties(Properties props) 
   {
   		dbProps = props;
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
   	  if(dbProps == null) {  
   	  	dbProps = loadDBProperties();
   	  }
   	  
   	  String printSQLfile = dbProps.getProperty("PrintSQL");
	  if (printSQLfile != null)
	  {		 
		 try
		 {
			ResourceFactory.getIYukon().getDBPersistent().setSQLFileName( printSQLfile );
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

	         CTILogger.info("Registered JDBC driver " + driverClassName );
		 }
		 catch (Exception e)
		 {
         CTILogger.error("Can't register JDBC driver: " +
                  driverClassName, e );        
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
