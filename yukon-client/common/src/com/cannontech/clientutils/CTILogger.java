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
      Layout layout = new PatternLayout("[%d{HH:mm:ss}] %p [%t] - %m%n");
      
      //add all appenders below. Appenders are destinations you want the message
      //to be printed to
      logger.addAppender(new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT));
      
/*      try
      {
         //does not append to the log file
         logger.addAppender(new FileAppender(layout, "d:/test.txt", false));
      }
      catch( java.io.IOException io )
      {}
*/
//      logger.addAppender(new SocketAppender(layout, ConsoleAppender.SYSTEM_OUT));
   }
   
	/**
	 * Constructor for CTILogger.
	 * @param arg0
	 */
	private CTILogger()
	{
		super();
	}


   public static void debug( String msg )
   {      
      logger.debug( msg );
   }

   public static void info( String msg )
   {      
      logger.info( msg );
   }

   public static void info( long msg )
   {      
      logger.info( new Long(msg) );
   }


   public static void error( String msg )
   {      
      logger.error( msg );
   }

   public static void error( String msg, Throwable t )
   {      
      logger.error( msg, t );
   }

   public static void fatal( String msg )
   {      
      logger.fatal( msg );
   }

   public static void fatal( String msg, Throwable t )
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


/*c Used to output the category of the logging event. The category conversion specifier can be optionally followed by precision specifier, that is a decimal constant in brackets. 
If a precision specifier is given, then only the corresponding number of right most components of the category name will be printed. By default the category name is printed in full. 

For example, for the category name "a.b.c" the pattern %c{2} will output "b.c". 
 
C Used to output the fully qualified class name of the caller issuing the logging request. This conversion specifier can be optionally followed by precision specifier, that is a decimal constant in brackets. 
If a precision specifier is given, then only the corresponding number of right most components of the class name will be printed. By default the class name is output in fully qualified form. 

For example, for the class name "org.apache.xyz.SomeClass", the pattern %C{1} will output "SomeClass". 

WARNING Generating the caller class information is slow. Thus, it's use should be avoided unless execution speed is not an issue. 
 
d Used to output the date of the logging event. The date conversion specifier may be followed by a date format specifier enclosed between braces. For example, %d{HH:mm:ss,SSS} or %d{dd MMM yyyy HH:mm:ss,SSS}. If no date format specifier is given then ISO8601 format is assumed. 
The date format specifier admits the same syntax as the time pattern string of the SimpleDateFormat. Although part of the standard JDK, the performance of SimpleDateFormat is quite poor. 

For better results it is recommended to use the log4j date formatters. These can be specified using one of the strings "ABSOLUTE", "DATE" and "ISO8601" for specifying AbsoluteTimeDateFormat, DateTimeDateFormat and respectively ISO8601DateFormat. For example, %d{ISO8601} or %d{ABSOLUTE}. 

These dedicated date formatters perform significantly better than SimpleDateFormat. 
 
F Used to output the file name where the logging request was issued. 
WARNING Generating caller location information is extremely slow. It's use should be avoided unless execution speed is not an issue. 
 
l Used to output location information of the caller which generated the logging event. 
The location information depends on the JVM implementation but usually consists of the fully qualified name of the calling method followed by the callers source the file name and line number between parentheses. 

The location information can be very useful. However, it's generation is extremely slow. It's use should be avoided unless execution speed is not an issue. 
 
L Used to output the line number from where the logging request was issued. 
WARNING Generating caller location information is extremely slow. It's use should be avoided unless execution speed is not an issue. 
 
m Used to output the application supplied message associated with the logging event. 
M Used to output the method name where the logging request was issued. 
WARNING Generating caller location information is extremely slow. It's use should be avoided unless execution speed is not an issue. 
 
n Outputs the platform dependent line separator character or characters. 
This conversion character offers practically the same performance as using non-portable line separator strings such as "\n", or "\r\n". Thus, it is the preferred way of specifying a line separator. 
 
p Used to output the priority of the logging event. 
r Used to output the number of milliseconds elapsed since the start of the application until the creation of the logging event. 
t Used to output the name of the thread that generated the logging event. 
x Used to output the NDC (nested diagnostic context) associated with the thread that generated the logging event.  
X Used to output the MDC (mapped diagnostic context) associated with the thread that generated the logging event. The X conversion character must be followed by the key for the map placed between braces, as in %X{clientNumber} where clientNumber is the key. The value in the MDC corresponding to the key will be output.

See MDC class for more details. 
 
% The sequence %% outputs a single percent sign.  
*/