package com.cannontech.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Makes all the global yukon properties accessible from
 * a single place.
 *
 * Creation date: (10/12/2001 11:13:34 AM)
 * @author: Aaron Lauinger
 */
public class GlobalProperties extends Properties {
	
	public static final String DISPATCH_MACHINE = "dispatch_machine";
	public static final String DISPATCH_PORT = "dispatch_port";

	public static final String PORTER_MACHINE = "porter_machine";
	public static final String PORTER_PORT = "porter_port";

	public static final String MACS_MACHINE = "macs_machine";
	public static final String MACS_PORT = "macs_port";
	
	public static final String LOADCONTROL_MACHINE = "loadcontrol_machine";
	public static final String LOADCONTROL_PORT = "loadcontrol_port";

	//Load all the properties from these files
	//must be in the classpath
	private static final String[] propFiles = { 
		"/config.properties",
		"/db.properties"
	};

	//singleton instance
	private static GlobalProperties instance = null;
/**
 * GlobalProperties constructor comment.
 */
public GlobalProperties() {
	super();
	loadProperties();
}
/**
 * Creation date: (2/12/2002 1:39:27 PM)
 * @return com.cannontech.util.GlobalProperties
 */
public static synchronized GlobalProperties getInstance() {	
	if( instance == null )
		instance = new GlobalProperties();

	return instance;
}
/*
 * loads all the properties
 **/
private void loadProperties() {

	for( int i = 0; i < propFiles.length; i++ ) {
		InputStream propIn = null;

		try	{		
			propIn = getClass().getResourceAsStream(propFiles[i]);		
			load(propIn);
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
		finally { //clean up the streams
			try {
			if( propIn != null ) propIn.close();
			} catch( java.io.IOException e2 ) { e2.printStackTrace(); };
		}
	}
}
}
