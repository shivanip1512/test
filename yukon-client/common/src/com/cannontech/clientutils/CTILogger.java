package com.cannontech.clientutils;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.YukonFileAppender;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.roles.yukon.LoggingRole;

/**
 * @author rneuharth
 * Jul 26, 2002 at 10:46:37 AM
 * 
 * All levels that are greater than the current level will be printed
 *
 *	  Level OFF   = new Level(MAX_INT, "OFF", 0);
 *	  Level FATAL = new Level(5000, "FATAL", 0);
 *	  Level ERROR = new Level(4000, "ERROR", 3);
 *	  Level WARN  = new Level(3000, "WARN",  4);
 *	  Level INFO  = new Level(2000, "INFO",  6);
 *	  Level DEBUG = new Level(1000, "DEBUG", 7);
 *	  Level ALL   = new Level(MIN_INT, "ALL", 7);
 *
 *
 *   
 * */
public class CTILogger
{
	private static boolean isCreated = false;

	private static final Layout DEF_LAYOUT = 
				new PatternLayout("[%d{MM/dd/yyyy HH:mm:ss}] %-5p [%-11.11t] %m%n");
	
   //private static Logger logger = null;
   private static String MAX_LOG_FILE_SIZE = "100MB";
   
	//used to discover what class requested the logging
	private static final Throwable t = new Throwable();
 
	//checks the LoggingRole.GENERAL_LEVEL property for its level 
	public static final String STANDARD_LOGGER = "StandardLogger";


	//a mapping of package objects to their log_level property
	private static final Object[][] ALL_NAMES = 
	{
		{ "com.cannontech.dbeditor", new Integer(LoggingRole.DBEDITOR_LEVEL) },
		{ "com.cannontech.database", new Integer(LoggingRole.DATABASE_LEVEL) },
		{ "com.cannontech.tdc", new Integer(LoggingRole.TDC_LEVEL) },
		{ "com.cannontech.yc", new Integer(LoggingRole.COMMANDER_LEVEL) },
		{ "com.cannontech.billing", new Integer(LoggingRole.BILLING_LEVEL) },
		{ "com.cannontech.calchist", new Integer(LoggingRole.CALCHIST_LEVEL) },
		{ "com.cannontech.cbc", new Integer(LoggingRole.CAPCONTROL_LEVEL) },
		{ "com.cannontech.esub", new Integer(LoggingRole.ESUB_LEVEL) },
		{ "com.cannontech.export", new Integer(LoggingRole.EXPORT_LEVEL) },
		{ "com.cannontech.loadcontrol", new Integer(LoggingRole.LOADCONTROL_LEVEL) },
		{ "com.cannontech.macs", new Integer(LoggingRole.MACS_LEVEL) },
		{ "com.cannontech.notif", new Integer(LoggingRole.NOTIFICATION_LEVEL) },
		{ "com.cannontech.report", new Integer(LoggingRole.REPORTING_LEVEL) },
		{ "com.cannontech.graph", new Integer(LoggingRole.TRENDING_LEVEL) },
		{ "com.cannontech.stars", new Integer(LoggingRole.STARS_LEVEL) },
	};

	private static FileAppender fileAppender = null;

	/**
	 * Constructor for CTILogger.
	 * @param arg0
	 */
	private CTILogger()
	{
		super();
	}


	/**
	 * Should not use any logging inside this method
	 * @param logName_
	 */
	private static void createLogger( String logName_ )
	{
		//Init our logger object for the first time
		Logger logger = LogManager.getLogger( logName_ );
   
		logger.setLevel( Level.ALL );  //default log level
   
		logger.addAppender( new ConsoleAppender(DEF_LAYOUT, ConsoleAppender.SYSTEM_OUT) );
	}

   private static synchronized Logger getLogger()
   {
      if( !isCreated )
      {
         //Init our logger object for the first time
         createLogger( STANDARD_LOGGER );
        
			//create the extra loggers to keep our web folks sane
			for( int i = 0; i < ALL_NAMES.length; i++ )
				createLogger( ALL_NAMES[i][0].toString() );				

			isCreated = true;
			
			//initLoggers();
      }
            
	  //by default, use the standard logger
      Logger log = LogManager.getLogger( STANDARD_LOGGER );

	  // If we are told to use the standard logger and if we
	  // have loaded the global properties
	  if( DefaultDatabaseCache.getInstance().hasLoadedGlobals() )
	  {
			t.fillInStackTrace();
			
			//if( t.getStackTrace().length >= 2 )
			for( int i = 0; i < t.getStackTrace().length; i++ )
			{
				if( t.getStackTrace()[i].getClassName().startsWith(CTILogger.class.getName()) )
					continue;
			
				Logger l = LogManager.exists(
						t.getStackTrace()[i].getClassName().substring(
						0,
						t.getStackTrace()[i].getClassName().lastIndexOf(".") ) );
				
				if( l != null )
					log = l;
					
				break;
			}
	  
	  }

	  return log;
   }


	private static String getLoggerLevel( Logger logger_ )
	{	
		String level = null;	
		if( !DefaultDatabaseCache.getInstance().hasLoadedGlobals() ) 
		{
			return "ALL";	
		}

		for( int i = 0; i < ALL_NAMES.length; i++ )
			if( logger_.getName().equalsIgnoreCase(ALL_NAMES[i][0].toString()) )
			{
				level = RoleFuncs.getGlobalPropertyValue( 
						((Integer)ALL_NAMES[i][1]).intValue() );

//				level = "ALL";
			}
			
		//just in case we can not find the name above
		if( level == null )
		{			
			LiteYukonRoleProperty p =
				AuthFuncs.getRoleProperty( LoggingRole.GENERAL_LEVEL );


			//extra special case since we may not be inited yet with out properties
			if( p != null )
				level = RoleFuncs.getGlobalPropertyValue( LoggingRole.GENERAL_LEVEL );
			else
				level = "ALL";
		}				


		return level;
	}
	
   /**
    * This method is called everytime to update the current logging properties to what the user
    * has set. This allows changes to occur on the fly such as changing the LOGGING_LEVEL and
    * having the change take effect immediately.
    * 
    */
   private static final void updateLogSettings()
   {
		if( !DefaultDatabaseCache.getInstance().hasLoadedGlobals() )
		{
			return;
		}
   		
  		Logger log = getLogger();
		setLogLevel( getLoggerLevel(log) );

		LiteYukonRoleProperty lToFile =
			AuthFuncs.getRoleProperty( LoggingRole.LOG_TO_FILE );

		String writeToFile = "false";
		if( lToFile != null )
			writeToFile = RoleFuncs.getGlobalPropertyValue( LoggingRole.LOG_TO_FILE );		

		if( Boolean.valueOf(writeToFile).booleanValue() )
		{
			if( !log.isAttached(fileAppender) )
				log.addAppender( getFileAppender() );//lazy init the appender here since it creates a file
		}
		else
		{
			if( log.isAttached(fileAppender) )
				log.removeAppender( getFileAppender() );//lazy init the appender here since it creates a file
		}
   }
   
	private static final FileAppender getFileAppender()
	{
		if( fileAppender == null )
		{
			try
			{					
				fileAppender = new YukonFileAppender(
										DEF_LAYOUT,
										CtiUtilities.getLogDirPath() + System.getProperty("file.separator") +
										  (CtiUtilities.getApplicationName() == null
										  ? "webapp"
										  : CtiUtilities.getApplicationName()) 
										);						
			}
			catch( Exception e )
			{
				error( 
					"Unable to initialize RollingFileAppender", e );
			}
		}
		
		return fileAppender;
	}



	//
	// All functions for logging are below
	//
	public static Level getLogLevel()
	{      
		return getLogger().getLevel();
	}

	/**
	 *  
	 * Use this log directly if you are not sure that the DB cache has been
	 * loaded. Logging database connectivity is an example of where this 
	 * should be used.
	 * 
	 * The STANDARD_LOGGER will be inited correctly to what the properties are
	 * set to once the roles & properties are loaded. This is occurs in the
	 * updateLogSettings() method.
	 */
	private static Logger getStandardLog()
	{      
		return getLogger().getLogger( STANDARD_LOGGER );
	}

	public static void setLogLevel( String level )
	{      
		getLogger().setLevel( Level.toLevel(level, getLogger().getLevel()) );
	}

   public static void debug( Object msg )
   {      
		updateLogSettings();
      getLogger().debug( msg );
   }

   public static void info( Object msg )
   {		
		updateLogSettings();
      getLogger().info( msg );
   }

   public static void info( long msg )
   {      
		updateLogSettings();
      getLogger().info( new Long(msg) );
   }

   public static void info( boolean msg )
   {
		updateLogSettings();
      getLogger().info( new Boolean(msg) );
   }

   public static void error( Object msg )
   {      
		updateLogSettings();
      getLogger().error( msg ); 
   }

   public static void error( Object msg, Throwable t )
   {      
		updateLogSettings();
      getLogger().error( msg, t );
   }

   public static void fatal( Object msg )
   {      
		updateLogSettings();
      getLogger().fatal( msg );
   }

   public static void fatal( Object msg, Throwable t )
   {      
		updateLogSettings();
      getLogger().fatal( msg, t );
   }

   public static void warn(Object msg) 
   {
		updateLogSettings();
   	getLogger().warn(msg);
   }
   
   public static void warn(Object msg, Throwable t) 
   {
		updateLogSettings();
		getLogger().warn(msg,t);
   }

}
