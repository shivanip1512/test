package com.cannontech.database.data.pao;

/**
 * Insert the type's description here.
 * Creation date: (10/2/2001 1:18:50 PM)
 * @author: 
 */
public interface PortTypes 
{

	//Specific Types - map to basic types
	public final static int LOCAL_DIRECT = 1000;
	public final static int LOCAL_SHARED = 1001;
	public final static int LOCAL_RADIO = 1002;
	public final static int LOCAL_DIALUP = 1003;

	public final static int TSERVER_DIRECT = 1010;
	public final static int TSERVER_SHARED = 1011;
	public final static int TSERVER_RADIO = 1012;
	public final static int TSERVER_DIALUP = 1013;


	//Strings of port types
	public final static String STRING_LOCAL_DIRECT = "Local Direct";
	public final static String STRING_LOCAL_SERIAL = "Local Serial Port";
	public final static String STRING_LOCAL_RADIO = "Local Radio";
	public final static String STRING_LOCAL_DIALUP = "Local Dialup";
	public final static String STRING_TERM_SERVER_DIRECT = "Terminal Server Direct";
	public final static String STRING_TERM_SERVER = "Terminal Server";
	public final static String STRING_TERM_SERVER_RADIO = "Terminal Server Radio";
	public final static String STRING_TERM_SERVER_DIALUP = "Terminal Server Dialup";

}
