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

	public static final String[] ALL_CONFIG_KEYS =
	{
		//Do not put KEY_YUKON_VERSION in here since it is not in the config.properties file
		KEY_CC_INTERFACE,
		KEY_EDITOR_CORE,
		KEY_EDITOR_LM,
		KEY_EDITOR_CAPCONTROL,
		KEY_EDITOR_SYSTEM,
		KEY_DECIMAL_PLACES,

		KEY_CAP_CONTROL_MACHINE,
		KEY_CAP_CONTROL_PORT,

		KEY_LOADCONTROL_MACHINE,
		KEY_LOADCONTROL_PORT,
		KEY_LOADCONTROL_EDIT,
		KEY_UTILITYID_RANGE,

		KEY_MACS_MACHINE,
		KEY_MACS_PORT,
		KEY_MACS_EDIT,
		KEY_TDC_RIGHTS,

		KEY_ACTIVATE_BILLING,
		KEY_BILLING_INPUT
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
public static CtiProperties getInstance() 
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
 * @return boolean
 */
public static boolean isCreateable( final int readOnlyInteger )
{
	return ( (readOnlyInteger & CREATABLE) == CREATABLE);
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isEnableable( final int readOnlyInteger )
{
	return ( (readOnlyInteger & ENABLEABLE) == ENABLEABLE);
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isHiddenAll( final int readOnlyInteger )
{
	return (readOnlyInteger & HIDE_ALL) == HIDE_ALL;
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isHiddenCapControl( final int readOnlyInteger )
{
	return isHiddenAll(readOnlyInteger)
			 || ((readOnlyInteger & HIDE_CAPCONTROL) == HIDE_CAPCONTROL);
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isHiddenLoadControl( final int readOnlyInteger )
{
	return isHiddenAll(readOnlyInteger)
			 || ((readOnlyInteger & HIDE_LOADCONTROL) == HIDE_LOADCONTROL);
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isHiddenMACS( final int readOnlyInteger )
{
	return isHiddenAll(readOnlyInteger)
			 || ((readOnlyInteger & HIDE_MACS) == HIDE_MACS);
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isStartable( final int readOnlyInteger )
{
	return ( (readOnlyInteger & STARTABLE) == STARTABLE);
}
}
