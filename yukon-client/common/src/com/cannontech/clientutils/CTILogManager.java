package com.cannontech.clientutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.FileWatchdog;

/**
 * Used to configure our logging facilities.
 * 
 * @author rneuharth
 *
 * WARNING: Do not use CTILogger in this class. Only use this classes
 * local logger.
 * 
 */
public class CTILogManager implements ILogging
{
	public static final String LOG_PROPERTIES_FILE = "/log.properties";

    //how all our logging is printed
    protected static final Layout LOG_LAYOUT = 
        new PatternLayout("[%d{MM/dd/yy HH:mm:ss}] %-5p[%-8.8t] %m%n");

    //list of key to values found in the log.properties stream
	private static Properties logProps = null;
    
    //tells us if the file is being watched yet
    private static boolean startedWatch = false;
    
    //Local logger to the console
    private static final Logger MGR_LOGGER = LogManager.getLogger( CTILogManager.class );

    
    private CTILogManager()
    {
        super();
        init();
    }
   
   
   private static synchronized Properties loadLoggingProps() 
   {
       Properties props = null;
   		
		try
		{
			InputStream is = getLogInputStream();

			props = new Properties();
			props.load(is);
			
			is.close();
		 }
		 catch (Exception e)
		 {
             MGR_LOGGER.info("Can't read the LOG properties file. " +
		 		"Make sure log.properties is in the CLASSPATH" + 
		 		(ILogging.FILE_BASE != null ? " or in the directory: " + ILogging.FILE_BASE : "") );
		 }

		 return props;
   }
   
   /**
    * Called by the services that need the log file as a stream
    * 
    * @return
    * @throws Exception
    */
   public static synchronized InputStream getLogInputStream() throws Exception
   {
		InputStream is = CTILogManager.class.getResourceAsStream( LOG_PROPERTIES_FILE );

		if( is == null ) //not in CLASSPATH, check catalina
		{
			File f = new File( ILogging.FILE_BASE + LOG_PROPERTIES_FILE);
			is = new FileInputStream( ILogging.FILE_BASE + LOG_PROPERTIES_FILE );
            
            //start watching our file for changes
            doWatch( f );

			MGR_LOGGER.info( " Searching for log.properties in : " + f.getAbsolutePath() );
			MGR_LOGGER.info( "   catalina.base = " + ILogging.FILE_BASE );
		}
		else
		{
            MGR_LOGGER.info( " Using log.properties found in CLASSPATH" );
		}

		return is;
	}
   
   /**
    * Watch for modifications to our property file. Reload properties if this
    * occurs.
    * 
    * @param f
    */
   private static void doWatch( File f )
   {
       if( startedWatch )
           return;
       
        FileWatchdog fileWatcher = new FileWatchdog( f.getAbsolutePath() )
        {
            protected void doOnChange()
            {
                //do not check on the initial creation
                if( startedWatch )
                {
                    MGR_LOGGER.info( " Reloading " + LOG_PROPERTIES_FILE +
                                " due to file modification" );
    
                    reset();
                }

                startedWatch = true;
            }
            
        };
        
        fileWatcher.setDelay( 10000 ); // 10 seconds
        fileWatcher.start();
   }

   
   public static synchronized void setLogProperties(Properties props) 
   {
       logProps = props;
	   loadLogging( logProps );
   }

   protected static void init()
   {
       MGR_LOGGER.setLevel( Level.ALL );
       MGR_LOGGER.addAppender( new ConsoleAppender(LOG_LAYOUT, ConsoleAppender.SYSTEM_OUT) );
       
       logProps = loadLoggingProps();
       loadLogging( logProps );
   }

    /**
     * Sets any logging properties we have.
     * 
     * @param props
     */
    public static void loadLogging( Properties props )
    {      
        synchronized( ALL_NAMES )
        {
            for( int i = 0; i < LOG_LEVEL_NAMES.length; i++ )
            {
                String logProp = 
                    (props == null ? null : props.getProperty(LOG_LEVEL_NAMES[i]) );
                
                //ignore properties that we do not have a setting for
                if( logProp != null )
                    ALL_NAMES[i][1] = logProp; //INFO, DEBUG, WARNING...
            }
        }
        
    }
   
   /**
    * Resets our LogManager with current values
    *
    */
	public static synchronized void reset()
	{
        logProps = loadLoggingProps();
   	  
        loadLogging( logProps );
	}

}
