package com.cannontech.clientutils;

import org.apache.log4j.*;

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

      logger.addAppender( new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT) );

      String fileName = null;
      try
      {  
         fileName = com.cannontech.common.util.CtiProperties.getInstance().getProperty(
            com.cannontech.common.util.CtiProperties.KEY_CLIENT_LOG_FILE, null);

         //we must have a valid file name
         if( new java.io.File(fileName).isAbsolute() )
            logger.addAppender(new FileAppender(layout, fileName, false)); //create a new file everytime
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

   public static void main(String[] ar)
   {  
      com.cannontech.clientutils.CTILogger.info( "HAHA");
      
      java.util.Enumeration e = CTILogger.logger.getAllAppenders();
      while( e.hasMoreElements() )
         CTILogger.info( e.nextElement().toString() );
   }

}
