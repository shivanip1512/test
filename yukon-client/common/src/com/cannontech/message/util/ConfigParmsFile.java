package com.cannontech.message.util;

/**
 * ConfigParmsFile represents a configuration file.
 */
import java.util.Hashtable;

public class ConfigParmsFile extends java.io.File implements ConfigParms {

	public static final String DEFAULT_FILENAME = "config.cfg";
	public static final char DEFAULT_SEPARATOR = '=';
	public static final char DEFAULT_COMMENT = '#';
	
	private char separator = DEFAULT_SEPARATOR;
	private char comment = DEFAULT_COMMENT;
/**
 * This method was created in VisualAge.
 */
public ConfigParmsFile() {
	super(DEFAULT_FILENAME);
}
/**
 * ConfigParmsFile constructor comment.
 * @param dir java.io.File
 * @param name java.lang.String
 */
public ConfigParmsFile(java.io.File dir, String name) {
	super(dir, name);
}
/**
 * ConfigParmsFile constructor comment.
 * @param path java.lang.String
 */
public ConfigParmsFile(String path) {
	super(path);
}
/**
 * ConfigParmsFile constructor comment.
 * @param path java.lang.String
 * @param name java.lang.String
 */
public ConfigParmsFile(String path, String name) {
	super(path, name);
}
/**
 * getKeysAndValues method comment.
 */
public java.lang.String[][] getKeysAndValues() {

	java.util.Vector keys = new java.util.Vector();
	java.util.Vector values = new java.util.Vector();
	
	try
	{
		java.io.FileInputStream inStrm = new java.io.FileInputStream( this.getPath() );
		java.io.InputStreamReader inRdr = new java.io.InputStreamReader( inStrm );
		java.io.BufferedReader bufRdr = new java.io.BufferedReader( inRdr );

		String line;

		while( (line = bufRdr.readLine()) != null )
		{
			//Check if this line contains a comment character
			int commentPos;
			
			if( (commentPos = line.indexOf( (int) comment )) != - 1 )
			{
				//get rid of everything after the command character
				line = line.substring(0, commentPos);
			}
			
			java.util.StringTokenizer t = new java.util.StringTokenizer( line, (new Character(separator)).toString(), false );

			if( t.countTokens() != 2 )
				continue;	//bad line should we be more wrathfull?

			keys.addElement( t.nextToken().trim() );
			values.addElement( t.nextToken().trim() );				
		}
	}
	catch( java.io.FileNotFoundException fnfe)
	{
		System.out.println("*** File Not Found Exception:");
		System.out.println( this.getClass().getName() + ".getKeysAndValues()" );
		System.out.println("  " + fnfe.getMessage());
		fnfe.printStackTrace(System.out);
	}
	catch( java.io.IOException ioe )
	{
		ioe.printStackTrace();
	}

	if( keys.size() != values.size() )
		return null;

	String[][] retVal = new String[2][keys.size()];

	keys.copyInto( retVal[0] );
	values.copyInto( retVal[1] );
	
	return retVal;
}
/**
 * This method was created in VisualAge.
 * @return char
 */
public char getSeparator() {
	return separator;
}
/**
 * getValue returns a single value based on the key supplied.  If there is more than one value
 * for the given key then the first value is returned and the rest ignored.
 */
public String getValue(String key) {

	//This is horribly inefficent but its works, will rarely be used, and the clock is ticking
	//It hits the file each call
	String[][] keysAndValues = getKeysAndValues();

	for( int i = 0; i < keysAndValues[0].length; i++ )
	{
		if( keysAndValues[0][i].equals(key) )
		{
			return keysAndValues[1][i];
		}
	}

	return null;
}
/**
 * getValues returns an array of strings that represent the values associated with the supplied
 * key.  
 * @return java.lang.String[]
 * @param key java.lang.String
 */
public String[] getValues(String key) {
	//This is inefficent but its works, will rarely be used
	//It hits the file each call
	String[][] keysAndValues = getKeysAndValues();

	java.util.Vector values = new java.util.Vector();
	
	for( int i = 0; i < keysAndValues[0].length; i++ )
	{
		if( keysAndValues[0][i].equals(key.trim().toLowerCase()) )
		{
			values.addElement( keysAndValues[1][i] );
		}
	}

	if( values.size() > 0 )
	{
		String[] retVal = new String[values.size()];
		
		values.copyInto( retVal );
		
		return retVal;
	}
	else
		return new String[0];
}
/**
 * This method was created in VisualAge.
 * @param sep char
 */
public void setSeparator(char sep) {
	separator = sep;
}
}
