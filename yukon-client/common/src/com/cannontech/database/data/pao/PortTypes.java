package com.cannontech.database.data.pao;

/**
 * Insert the type's description here.
 * Creation date: (10/2/2001 1:18:50 PM)
 * @author: 
 */
public interface PortTypes extends TypeBase
{
	//Specific Types - map to basic types
	public final static int LOCAL_DIRECT        = PORT_OFFSET + 0;
	public final static int LOCAL_SHARED        = PORT_OFFSET + 1;
	public final static int LOCAL_RADIO         = PORT_OFFSET + 2;
	public final static int LOCAL_DIALUP        = PORT_OFFSET + 3;
	public final static int TSERVER_DIRECT      = PORT_OFFSET + 4;
	public final static int TSERVER_SHARED      = PORT_OFFSET + 5;
	public final static int TSERVER_RADIO       = PORT_OFFSET + 6;
	public final static int TSERVER_DIALUP      = PORT_OFFSET + 7;
	public final static int LOCAL_DIALBACK      = PORT_OFFSET + 8;
	public final static int DIALOUT_POOL		  = PORT_OFFSET + 9;


	//Strings of port types
	public final static String STRING_LOCAL_DIRECT		= "Local Direct";
	public final static String STRING_LOCAL_SERIAL		= "Local Serial Port";
	public final static String STRING_LOCAL_RADIO		= "Local Radio";
	public final static String STRING_LOCAL_DIALUP		= "Local Dialup";
	public final static String STRING_LOCAL_DIALBACK	= "Local Dialback";

	public final static String STRING_TERM_SERVER_DIRECT	= "Terminal Server Direct";
	public final static String STRING_TERM_SERVER			= "Terminal Server";
	public final static String STRING_TERM_SERVER_RADIO	= "Terminal Server Radio";
	public final static String STRING_TERM_SERVER_DIALUP	= "Terminal Server Dialup";
	public final static String STRING_DIALOUT_POOL			= "Dialout Pool";

}
