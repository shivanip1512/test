package com.cannontech.clientutils;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author rneuharth
 * Jul 26, 2002 at 10:46:37 AM
 * 
 * A undefined generated comment
 */
public class CTILogger
{
   static Logger logger = Logger.getLogger(CTILogger.class.getName());

   static
   {      
      Layout layout = new PatternLayout("[%d{HH:mm:ss}] %-5p [%-11.11t] %m%n");

      
      /*
       * All levels that are greater than the current level will be printed
       * 
        final static public Level OFF = new Level(MAX_INT, "OFF", 0);
        final static public Level FATAL = new Level(5000, "FATAL", 0);
        final static public Level ERROR = new Level(4000, "ERROR", 3);
        final static public Level WARN  = new Level(3000, "WARN",  4);
        final static public Level INFO  = new Level(2000, "INFO",  6);
        final static public Level DEBUG = new Level(1000, "DEBUG", 7);
        final static public Level ALL = new Level(MIN_INT, "ALL", 7);
      */ 
      logger.setLevel( Level.INFO );  //default log level


      logger.addAppender( new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT) );
      
      String writeToFile = null;
      try
      {  
         writeToFile = com.cannontech.common.util.CtiProperties.getInstance().getProperty(
            com.cannontech.common.util.CtiProperties.KEY_CLIENT_LOG_FILE, "false" );

         if( Boolean.valueOf(writeToFile).booleanValue() )
            logger.addAppender(new FileAppender(
                  layout, 
                  com.cannontech.common.util.CtiUtilities.getLogDirPath() +
                  com.cannontech.common.util.CtiUtilities.getApplicationName() + ".log",
                  false)); //create a new file everytime
      }
      catch( Exception e )
      {}


      try
      {  
         String level = com.cannontech.common.util.CtiProperties.getInstance().getProperty(
            com.cannontech.common.util.CtiProperties.KEY_LOG_LEVEL, null);

         if( level != null )
            logger.setLevel( Level.toLevel( level, logger.getLevel() ) );
      }
      catch( Exception e )
      {}

      //add all appenders below


      com.cannontech.clientutils.CTILogger.info( "Logging started..." );
   }
   
	/**
	 * Constructor for CTILogger.
	 * @param arg0
	 */
	private CTILogger()
	{
		super();
	}


   public static void debug( Object msg )
   {      
      logger.debug( msg );
   }

   public static void info( Object msg )
   {      
      logger.info( msg );
   }

   public static void info( long msg )
   {      
      logger.info( new Long(msg) );
   }

   public static void info( boolean msg )
   {      
      logger.info( new Boolean(msg) );
   }

   public static void error( Object msg )
   {      
      logger.error( msg ); 
   }

   public static void error( Object msg, Throwable t )
   {      
      logger.error( msg, t );
   }

   public static void fatal( Object msg )
   {      
      logger.fatal( msg );
   }

   public static void fatal( Object msg, Throwable t )
   {      
      logger.fatal( msg, t );
   }

   public static void warn(Object msg) 
   {
   	  logger.warn(msg);
   }
   
   public static void warn(Object msg, Throwable t) 
   {
   	  logger.warn(msg,t);
   }
   
   public static void main(String[] ar)
   {  
      com.cannontech.clientutils.CTILogger.info( "HAHA");
      
      java.util.Enumeration e = CTILogger.logger.getAllAppenders();
      while( e.hasMoreElements() )
         CTILogger.info( e.nextElement().toString() );
   }

}
