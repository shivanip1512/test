package com.cannontech.dbtools.updater;

/**
 * @author rneuharth
 *
 * Some defines that are needed and that could change
 */
public interface DBMSDefines
{
	public static final String NAME_STATE				= "DBstate.txt";
	public static final String NAME_VALID				= "valids.sql";
	public static final String NAME_INVALID			= "invalids.txt";
	public static final String META_TAG					= "@";
	public static final String META_TOKEN				= " ";
	
	public static final String META_ERROR				= "error";
	public static final String META_ERROR_FIX			= "errorfix";
	public static final String META_SUCCESS			= "success";
	public static final String META_INCLUDE			= "include";



	public static final String[] OPTIONS_ERROR =
	{
		"ignore",  //print a messge and continue on
		"autofix", //try to fix it with the provided FIX statement
		"verbose",  //print out the provided error message
		"ignore-remaining",  //ignore all remaining errors in the file
        "ignore-begin",  //ignore all errors up to the ignore-end string
        "ignore-end"
	};



	public static final String SQL_EXT					= ".sql";
	public static final String LINE_TERM				= ";";
	public static final String COMMENT_BEGIN			= "/*";
	public static final String COMMENT_END				= "*/";
	public static final String STARS_INC				= "StarsUpdate";
	public static final double MIN_VERSION				= 2.40;
	

	/** What strings should be ignored by the App for all DBMS's */	
	public static final String[] IGNORE_STRINGS =
	{		
		"GO"
	};


}
