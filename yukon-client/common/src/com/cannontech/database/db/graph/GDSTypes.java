/*
 * Created on Jan 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.graph;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface GDSTypes
{
	// GraphDataSeries valid types (public int's only)
	// the private int's are for completeness only and are NOT valid GDS strings.
	public static final String GRAPH_TYPE_STRING = "Graph Only";
	public static final String PRIMARY_TYPE_STRING  = "Primary";	//old peak
	public static final String BASIC_TYPE_STRING = "Basic";
	public static final String PEAK_TYPE_STRING = "Peak";
	public static final String YESTERDAY_TYPE_STRING = "Yesterday Only";
	
	public static final String DATE_STRING = "Specific Date";
	public static final String DATE_TYPE_STRING = "mm/dd/yy";
	public static final String THRESHOLD_TYPE_STRING = "Threshold";
	public static final String USAGE_TYPE_STRING = "Usage";
	public static final String BASIC_GRAPH_TYPE_STRING = "Graph";
	public static final String USAGE_GRAPH_TYPE_STRING = "Usage Graph";
	public static final String PEAK_GRAPH_TYPE_STRING = "Peak Graph";
	public static final String YESTERDAY_GRAPH_TYPE_STRING = "Yesterday";
	
	
	public static final int GRAPH_TYPE= 0x0001;	//will be 'graphed' in trending (INTERVAL).  Use BASIC_GRAPH_TYPE as default.
	public static final int PRIMARY_TYPE = 0x0002;	//the coincidental point (summary info) (only one gds can be this)
	public static final int USAGE_TYPE = 0x0004;	//energy usage (summary info)
	public static final int BASIC_TYPE = 0x0008;	//interval/normal point readings
	public static final int PEAK_TYPE = 0x0010;	//actual peak day data used
	public static final int YESTERDAY_TYPE = 0x0020;	//yesterday data used
	public static final int THRESHOLD_TYPE = 0x0040;	//
	public static final int DATE_TYPE = 0x0080;		// 
	
	//COMBINATION DATA SERIES TYPES
	public static final int BASIC_GRAPH_TYPE = 0x0009; //BASIC + GRAPH
	public static final int USAGE_GRAPH_TYPE = 0x0005;	//USAGE + GRAPH
	public static final int PEAK_GRAPH_TYPE = 0x0011;	//PEAK + GRAPH
	public static final int YESTERDAY_GRAPH_TYPE = 0x0021;	//YESTERDAY + GRAPH
	public static final int DATE_GRAPH_TYPE = 0x0081;	//DATE + GRAPH
		
	//VALID TYPES FOR GDS TABLE	
	public static String[] validTypeStrings = 
	{
		GRAPH_TYPE_STRING,
		USAGE_TYPE_STRING, 
		BASIC_TYPE_STRING,
		PEAK_TYPE_STRING,
		YESTERDAY_TYPE_STRING,
		BASIC_GRAPH_TYPE_STRING,
		USAGE_GRAPH_TYPE_STRING,
		PEAK_GRAPH_TYPE_STRING,
		YESTERDAY_GRAPH_TYPE_STRING, 
		THRESHOLD_TYPE_STRING,
		DATE_STRING,
		DATE_TYPE_STRING
	};	
	public static int[] validTypeInts =
	{
		GRAPH_TYPE,
		USAGE_TYPE, 
		BASIC_TYPE,
		PEAK_TYPE,
		YESTERDAY_TYPE,
		BASIC_GRAPH_TYPE,
		USAGE_GRAPH_TYPE,
		PEAK_GRAPH_TYPE,
		YESTERDAY_GRAPH_TYPE,
		THRESHOLD_TYPE,
		DATE_TYPE,
		DATE_GRAPH_TYPE
	}; 	
}
