package com.cannontech.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogManager;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.util.MBeanUtil;

public class PoolManager
{
	public static final String DB_PROPERTIES_FILE = "/db.properties";
	
	public static final String DRV_SQLSERVER = "jdbc:microsoft:sqlserver:";
	public static final String DRV_ORACLE = "jdbc:oracle:thin:";
	public static final String DRV_JTDS = "jdbc:jtds:sqlserver:";


	public static final String URL = ".url";
	public static final String USER = ".user";
	public static final String PASSWORD = ".password";
	public static final String MAXCONNS = ".maxconns";
	public static final String INITCONNS = ".initconns";
	
	
	//derived props stored from other props
	public static final String HOST = "db.host";
	public static final String PORT = "db.port";
	public static final String SERVICE = "db.servicename"; //oracle only


	public static final String[] ALL_DRIVERS =
	{
		"oracle.jdbc.OracleDriver",
		"com.microsoft.jdbc.sqlserver.SQLServerDriver",
		"net.sourceforge.jtds.jdbc.Driver",
		"sun.jdbc.odbc.JdbcOdbcDriver"  //only exists for backwards compatibility
	};



   static private Properties dbProps;
   static private PoolManager instance;

   private Hashtable pools = new Hashtable();

   private PoolManager()
   {
	  init();
   }
   
   
   /**
    * defaults to get only properties for the DatabaseAlias in CTIUtilities
    * @param key_
    * @return
    */
   public String getProperty( String key_ )
   {
   	return dbProps.getProperty( CtiUtilities.getDatabaseAlias() + key_ );
   }

	public boolean isMS()
	{
		String url = getProperty( URL );
		if( url != null )
			return url.indexOf(DRV_SQLSERVER) >= 0;
		else
			return false;
	}

	public boolean isJTDS()
	{
		String url = getProperty( URL );
		if( url != null )
			return url.indexOf(DRV_JTDS) >= 0;
		else
			return false;
	}

	public boolean isOracle()
	{
		String url = getProperty( URL );
		if( url != null )
			return url.indexOf(DRV_ORACLE) >= 0;
		else
			return false;
	}

	/**
	 * Sets the derived properties for easy access and usage
	 * 
	 * @param url_
	 * @param poolName_
	 */
	private void setUrlProps( String url_, String poolName_ )
	{
		//jdbc:microsoft:sqlserver://dbserver:1433;SelectMethod=cursor;
		//jdbc:microsoft:sqlserver://dbserver/bunk:1433;SelectMethod=cursor;
		//jdbc:jtds:sqlserver://dbserver:1433;APPNAME=yukon-client;TDS=8.0
		//jdbc:jtds:sqlserver://dbserver:1433;APPNAME=yukon-client;TDS=8.0;INSTANCE=bunk
		//jdbc:oracle:thin:@dbserver:1521:preprod

		try
		{
			//fill in some convinience properties
			if( url_.indexOf(DRV_ORACLE) >= 0 )
			{
				StringTokenizer tok = new StringTokenizer(
					url_.substring( url_.indexOf("@"), url_.length()),
					":");
	
				String s = tok.nextToken();
				dbProps.put( poolName_ + HOST, s.substring(1, s.length()) );
				
				s = tok.nextToken();
				dbProps.put( poolName_ + PORT, s );
	
				s = tok.nextToken();
				dbProps.put( poolName_ + SERVICE, s );
			}
			else if( url_.indexOf(DRV_SQLSERVER) >= 0 )
			{
				StringTokenizer tok = new StringTokenizer(
					url_.substring( url_.indexOf("//"), url_.length()),
					":");

				String s = tok.nextToken();
				String hostStr = s.substring(2, s.length());
				
				if( hostStr.indexOf("/") > 0 )
				{
					dbProps.put( poolName_ + HOST,
							hostStr.substring(0, hostStr.indexOf("/")) );

					dbProps.put( poolName_ + SERVICE,
							hostStr.substring( hostStr.indexOf("/")+1, hostStr.length()) );
				}
				else
				{
					dbProps.put( poolName_ + HOST, hostStr );					
					dbProps.put( poolName_ + SERVICE, "" );
				}

				s = tok.nextToken();
				dbProps.put( poolName_ + PORT, s.substring(0, s.indexOf(";")) );
			}
			else if( url_.indexOf(DRV_JTDS) >= 0 )
			{
				StringTokenizer tok = new StringTokenizer(
					url_.substring( url_.indexOf("//"), url_.length()),
					":");
	
				String s = tok.nextToken();
				dbProps.put( poolName_ + HOST, s.substring(2, s.length()) );
				
				s = tok.nextToken();
				StringTokenizer tempTok = new StringTokenizer( s, ";" );
				dbProps.put( poolName_ + PORT, tempTok.nextToken() );
				
				dbProps.put( poolName_ + SERVICE, "" );
				if( tempTok.hasMoreTokens() )
				{
					while( tempTok.hasMoreTokens() )
					{
						s = tempTok.nextToken();
						if( s.startsWith("INSTANCE=") )
							dbProps.put(
								poolName_ + SERVICE,
								s.substring("INSTANCE=".length()) );
					}
				}

			}
			else
				throw new Error("Unrecognized database URL, URL = " + url_ );

		}
		catch( Exception e )
		{
			CTILogger.error( "Invalid format of the database URL string", e );
			Error err = new Error( "Unrecognized database URL, URL = " + url_ );
			err.initCause( e );
			throw err;
		}
		
		
	}

   private void createPools(Properties props)
   {
   	
		
	  Enumeration propNames = props.propertyNames();
	  while (propNames.hasMoreElements())
	  {   
		 String name = (String) propNames.nextElement();
		 if (name.endsWith(URL))
		 {   
			String poolName = name.substring(0, name.lastIndexOf("."));
			String url = props.getProperty(poolName + URL);
			if (url == null)
			{                 
				 CTILogger.error( "No URL specified for " + poolName );
			    continue;			
			}
			
			//fill in some convinience properties
			setUrlProps( url, poolName );			
			
			
   
			String user = props.getProperty(poolName + USER);
			String password = props.getProperty(poolName + PASSWORD);
   
			String maxConns = props.getProperty(poolName + 
			   MAXCONNS, "0");
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
							  INITCONNS, "0");
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
            //MBeanUtil.tryRegisterMBean("type=ConnectionPool,poolName=" + poolName, pool);


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
	Enumeration poolEnum = pools.elements();
	String[] strs = new String[ pools.size() ];
	int i = 0;
	
	while ( poolEnum.hasMoreElements() )
		strs[i++] = poolEnum.nextElement().toString();
		
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
            throw new RuntimeException("Unable to get database connection.",e);
		 }
	  }

	  return conn;
   }      
   
   public static final URL getPropertyURL()
   {   	
   	try
   	{
   		URL retURL = PoolManager.class.getResource( DB_PROPERTIES_FILE );
   		
			if( retURL == null )  //not in CLASSPATH, check catalina
			{
				File f = new File( CTILogManager.FILE_BASE + DB_PROPERTIES_FILE );			
	   		retURL = f.toURL();
			}

			return retURL;
   	}
   	catch( Exception ex )
   	{
			CTILogger.error("Something went wrong with the URL of a file", ex );
   		return null;
   	}
   	
   }


   static synchronized public Properties loadDBProperties() 
   {
       if( dbProps == null )
       {
		try
		{
			InputStream is = getDBInputStream();

			dbProps = new Properties();
			dbProps.load(is);
			
			is.close();
		 }
		 catch (Exception e)
		 {
		  	CTILogger.error("Can't read the properties file. " +
		 		"Make sure db.properties is in the CLASSPATH" + 
		 		(CTILogManager.FILE_BASE != null ? " or in the directory: " + CTILogManager.FILE_BASE : "") );
		 }
       }
       
       return dbProps;

   }


	static synchronized public InputStream getDBInputStream() throws Exception
	{
		InputStream is = PoolManager.class.getResourceAsStream( DB_PROPERTIES_FILE );

		if( is == null ) //not in CLASSPATH, check catalina
		{
			File f = new File( CTILogManager.FILE_BASE + DB_PROPERTIES_FILE);
			is = new FileInputStream( CTILogManager.FILE_BASE + DB_PROPERTIES_FILE );
			
			CTILogger.info( " Searching for db.properties in : " + f.getAbsolutePath() );
			CTILogger.info( "   catalina.base = " + CTILogManager.FILE_BASE );
		}
		else
		{
			CTILogger.info( " Using db.properties found in CLASSPATH" );
		}
			
		return is;
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
	  return instance;
   }
   
   private void init()
   {
		if(dbProps == null)  
			dbProps = loadDBProperties();
   	  
  	  loadDrivers(dbProps);
	  createPools(dbProps);
   }
                                                                              
   private void loadDrivers(Properties props)
   {
	  for( int i = 0; i < ALL_DRIVERS.length; i++ )
	  {
		 try
		 {
			Driver driver = (Driver)Class.forName(ALL_DRIVERS[i]).newInstance();
			DriverManager.registerDriver(driver);

			CTILogger.info("Registered JDBC driver " + ALL_DRIVERS[i] );
		 }
		 catch (Exception e)
		 {
         CTILogger.error("Can't register JDBC driver: " +
                  ALL_DRIVERS[i], e );        
		 }
	  }
   }
   
   /**
    * Resets our Pool with possibly new drivers and connections
    *
    */
	public synchronized void resetPool()
	{
		release();

        pools.clear();
        dbProps = null;
		dbProps = loadDBProperties();
   	  
		createPools(dbProps);
	}


   public synchronized void release()
   {
   
	  Enumeration allPools = pools.elements();
	  while (allPools.hasMoreElements())
	  {
		 ConnectionPool pool = (ConnectionPool) allPools.nextElement();
		 pool.release();
	  }

   }

}
