package com.cannontech.common.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Insert the type's description here.
 * Creation date: (2/18/2002 10:43:31 AM)
 * @author: 
 */
public class CtiProperties extends java.util.Properties implements ClientRights
{
	private static CtiProperties props = null;


/* # ------- Client Log Configuration ------- #
   # CLIENT_LOG_VERBOSE                  0x00000001
   # CLIENT_LOG_CONNECTIONS              0x00000010
   # CLIENT_LOG_PENDINGOPS               0x00000020
   # CLIENT_LOG_REGISTRATION             0x00000040
   # CLIENT_LOG_ALARMACK                 0x01000000
   # CLIENT_LOG_MESSAGES                 0x02000000
   # CLIENT_LOG_MSGSTOCLIENT             0x04000000
   # CLIENT_LOG_MSGSFRMCLIENT            0x08000000
   # CLIENT_LOG_ALARMS                   0x10000000
*/

	public static final String COMMON_JAR = "common.jar";
	public static final String CONFIG_RESOURCE_NAME = "/config.properties";

	//all expected values
	public static final String VALUE_CC_INTERFACE_AMFM = "AMFM";



	//all key values for Yukon Clients
	public static final String KEY_YUKON_VERSION = "Yukon-Version";
	public static final String KEY_CC_INTERFACE = "cap_control_interface";
	public static final String KEY_EDITOR_CORE = "dbeditor_core";
	public static final String KEY_EDITOR_LM = "dbeditor_lm";
	public static final String KEY_EDITOR_CAPCONTROL = "dbeditor_cap_control";
	public static final String KEY_EDITOR_SYSTEM = "dbeditor_system";
	public static final String KEY_DECIMAL_PLACES = "decimal_places";

	public static final String KEY_CAP_CONTROL_MACHINE = "cap_control_machine";
	public static final String KEY_CAP_CONTROL_PORT = "cap_control_port";

	public static final String KEY_PORTER_MACHINE = "porter_machine";
	public static final String KEY_PORTER_PORT = "porter_port";

	public static final String KEY_LOADCONTROL_MACHINE = "loadcontrol_machine";
	public static final String KEY_LOADCONTROL_PORT = "loadcontrol_port";
	public static final String KEY_LOADCONTROL_EDIT = "loadcontrol_edit";
	public static final String KEY_UTILITYID_RANGE = "utility_id_range";

	public static final String KEY_MACS_MACHINE = "macs_machine";
	public static final String KEY_MACS_PORT = "macs_port";
	public static final String KEY_MACS_EDIT = "macs_edit";

	public static final String KEY_TDC_RIGHTS = "tdc_rights";

	public static final String KEY_ACTIVATE_BILLING = "billing_wiz_activate";
	public static final String KEY_BILLING_INPUT = "billing_input_file";

   public static final String KEY_CBC_CREATION_NAME = "cbc_creation_name";
   public static final String KEY_LOG_LEVEL = "client_log_level";
   public static final String KEY_CLIENT_LOG_FILE = "client_log_file";
   
   public static final String KEY_DISPATCH_MACHINE = "dispatch_machine";
   public static final String KEY_DISPATCH_PORT = "dispatch_port";

   public static final String KEY_TDC_ALARM_COUNT = "tdc_alarm_count";   

   public static final String KEY_MODE = "yukon_mode";   
   
   public static final String KEY_LOGIN_PAGE_LOGO = "login_page_logo";
   public static final String KEY_LOGIN_PAGE_HELP_EMAIL = "login_page_help_email";
   
	public static final String[] ALL_CONFIG_KEYS =
	{
		//Do not put KEY_YUKON_VERSION in here since it is not in the config.properties file
      KEY_CLIENT_LOG_FILE,
      
		KEY_CC_INTERFACE,
		KEY_EDITOR_CORE,
		KEY_EDITOR_LM,
		KEY_EDITOR_CAPCONTROL,
		KEY_EDITOR_SYSTEM,
		KEY_DECIMAL_PLACES,
      KEY_DISPATCH_MACHINE,
      KEY_DISPATCH_PORT,

		KEY_CAP_CONTROL_MACHINE,
		KEY_CAP_CONTROL_PORT,
		
		KEY_PORTER_MACHINE,
		KEY_PORTER_PORT,

		KEY_LOADCONTROL_MACHINE,
		KEY_LOADCONTROL_PORT,
		KEY_LOADCONTROL_EDIT,
		KEY_UTILITYID_RANGE,

		KEY_MACS_MACHINE,
		KEY_MACS_PORT,
		KEY_MACS_EDIT,
		KEY_TDC_RIGHTS,

		KEY_ACTIVATE_BILLING,
      KEY_CBC_CREATION_NAME,
		KEY_BILLING_INPUT,
      KEY_LOG_LEVEL,
      
      KEY_TDC_ALARM_COUNT,
      KEY_MODE,
      
      KEY_LOGIN_PAGE_LOGO,
      KEY_LOGIN_PAGE_HELP_EMAIL
	};


/**
 * CTIProperties constructor comment.
 */
private CtiProperties() 
{
	super();

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2002 10:46:51 AM)
 * @return com.cannontech.common.util.CtiProperties
 */
public static synchronized CtiProperties getInstance() 
{
	if( props == null )
		props = new CtiProperties();

	return props;
}
/**
 * Insert the method's description here.
 * Creation date: (2/18/2002 10:49:04 AM)
 */
private void initialize() 
{

	try
	{
		java.util.jar.JarFile jf = new java.util.jar.JarFile( COMMON_JAR );

		put( KEY_YUKON_VERSION,
				jf.getManifest().getMainAttributes().getValue( com.cannontech.common.util.CtiProperties.KEY_YUKON_VERSION ) );

		jf.close();
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.info("*** PROPERTY TRANSLATION ERROR: " + KEY_YUKON_VERSION + " key/value not stored." );
	}


	//all config.properties values go here
//	java.util.ResourceBundle cb = java.util.ResourceBundle.getBundle( CONFIG_RESOURCE_NAME );
		
	InputStream is = getClass().getResourceAsStream( CONFIG_RESOURCE_NAME );
	Properties cfgProps = new Properties();
	try
	{
		cfgProps.load(is);
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.info("Can't read the properties file. " +
			"Make sure " + CONFIG_RESOURCE_NAME + " is in the CLASSPATH" );

		return;
	}
		

	for( int i = 0; i < ALL_CONFIG_KEYS.length; i++ )
	{
		try
		{
			put( ALL_CONFIG_KEYS[i], cfgProps.getProperty(ALL_CONFIG_KEYS[i]) );//cb.getString(ALL_CONFIG_KEYS[i]) );
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.info("*** PROPERTY TRANSLATION ERROR: " + ALL_CONFIG_KEYS[i] + " key/value not stored." );
		}

	}
	
}

   /**
    * Insert the method's description here.
    * Creation date: (3/5/2001 11:32:59 AM)
    * @return String
    * 
    * This method returns the String that represents the key_ as a method.
    * We search for a method of the vlaue_ object that returns a String
    * and has a the name:
    *   get(key_);
    * If anything goes wrong, we print out all possible getters that return
    * a String and use the default_ as our value.
    * At most we accept 1 getter method. The percent(%) sign is used as a
    * token seperator.  key_ may look like this:
    *   CBC %PAOName%
    * A call to getPAOName() will replace the %PAOName%.
    */
   public static String getReflectiveProperty( 
         final Object value_, String key_, final String default_ )
   {
      if( value_ == null )
         return default_;


      java.lang.reflect.Method[] methods = value_.getClass().getMethods();
      
      try
      {
         StringBuffer buf = new StringBuffer(key_);
         String methodName = methodName = buf.substring( key_.indexOf("%")+1, key_.lastIndexOf("%") );
         
         for( int i = 0; i < methods.length; i++ )
         {
            if( methods[i].getName().toLowerCase().startsWith("get") 
                  && methods[i].getReturnType().equals(String.class)
                  && methods[i].getName().toLowerCase().endsWith(methodName.toLowerCase()) )
            {
               String s = (String)methods[i].invoke( value_, null );
               
               buf.replace( key_.indexOf("%")+1, key_.lastIndexOf("%"), s );
               
               //remove all % signs
               while( buf.toString().indexOf("%") != -1 )
                  buf.deleteCharAt( buf.toString().indexOf("%") );
                  
               return (buf.toString() == null ? default_ : buf.toString());
            }         
         }
      }
      catch( Exception e )
      {} //no biggy, print some info and use the default_ value


      /******************  ERROR HANDLING BELOW *****************/
      //oops we failed, list the properties for this reflective class
      com.cannontech.clientutils.CTILogger.info("*** PROPERTY REFLECTIVE TRANSLATION ERROR: " + key_ + " key/value not stored.");
      com.cannontech.clientutils.CTILogger.debug("Available REFLECTIVE properties for: " + value_.getClass().getName());

      for( int i = 0; i < methods.length; i++ )
      {
         if( methods[i].getName().toLowerCase().startsWith("get") 
               && methods[i].getReturnType().equals(String.class) )
         {
            com.cannontech.clientutils.CTILogger.info( "   " +
                  methods[i].getName().substring(3) );
         }         
      }
    
      return default_;
   }
   



/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isCreateable( final long readOnlyInteger )
{
	return ( (readOnlyInteger & CREATABLE) == CREATABLE);
}

/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isEnableable( final long readOnlyInteger )
{
	return ( (readOnlyInteger & ENABLEABLE) == ENABLEABLE);
}

/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isClientEnabled( final long readOnlyInteger )
{
	return (readOnlyInteger & ENABLE_SERVICES) != 0;
}

/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isHiddenCapControl( final long readOnlyInteger )
{
	return (readOnlyInteger & HIDE_CAPCONTROL) != 0;
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isHiddenLoadControl( final long readOnlyInteger )
{
	return (readOnlyInteger & HIDE_LOADCONTROL) != 0;
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isHiddenMACS( final long readOnlyInteger )
{
	return (readOnlyInteger & HIDE_MACS) != 0;
}

/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isAlarmColorHidden( final long readOnlyInteger )
{
   return (readOnlyInteger & HIDE_ALARM_COLORS) != 0;
}

/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isStartable( final long readOnlyInteger )
{
	return ( (readOnlyInteger & STARTABLE) == STARTABLE);
}


}
