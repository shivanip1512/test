package com.cannontech.database.data.pao;

/**
 * Insert the type's description here.
 * Creation date: (10/2/2001 1:28:29 PM)
 * @author: 
 */
public interface RouteTypes extends TypeBase
{
	//Specific Types - map to basic types
	public final static int ROUTE_CCU           = ROUTE_OFFSET + 0;
	public final static int ROUTE_TCU           = ROUTE_OFFSET + 1;
	public final static int ROUTE_LCU           = ROUTE_OFFSET + 2;
	public final static int ROUTE_MACRO         = ROUTE_OFFSET + 3;
	public final static int ROUTE_VERSACOM      = ROUTE_OFFSET + 4;
	public final static int ROUTE_TAP_PAGING    = ROUTE_OFFSET + 5;
	public final static int ROUTE_WCTP_TERMINAL = ROUTE_OFFSET + 6;
	public final static int ROUTE_SERIES_5_LMI	= ROUTE_OFFSET + 7;
	public final static int ROUTE_RTC			= ROUTE_OFFSET + 8;

	public static final String STRING_CCU = "CCU";
	public static final String STRING_TCU = "TCU";
	public static final String STRING_LCU = "LCU";
	public static final String STRING_MACRO = "Macro";
	public static final String STRING_VERSACOM = "Versacom";
	public static final String STRING_TAP_PAGING = "Tap Paging";
	public static final String STRING_WCTP_TERMINAL_ROUTE = "WCTP Terminal";
	public static final String STRING_SERIES_5_LMI_ROUTE = "Series 5 LMI";
	public static final String STRING_RTC_ROUTE = "RTC";
	
}
