package com.cannontech.report;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:08:15 PM)
 * @author: 
 */
public final class ReportTypes {
	// Constants representing types of file formats
	public static final int INVALID = -1;
	
	public static final int LOADS_SHED = 0;
	public static final int DAILY_PEAKS = 1;

	public static final String REPORT_LOADS_SHED = "Loads_Shed";
	public static final String REPORT_DAILY_PEAKS = "Daily_Peaks";

	public static final String[] SUPPORTED_REPORTS =
	{
		REPORT_LOADS_SHED,
		REPORT_DAILY_PEAKS
	};
	

/**
 * Insert the method's description here.
 * Creation date: (5/18/00 2:34:54 PM)
 */
public final static String getType(int typeEnum) {

	if( typeEnum < 0 || typeEnum > SUPPORTED_REPORTS.length )		
		throw new Error("FileFormatTypes::getType(int) - received unknown type: " + typeEnum );			
	else
		return SUPPORTED_REPORTS[typeEnum];
	
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param typeStr java.lang.String
 */
public final static int getType(String typeStr)
{
	//Go through the type string array and return it's index when the string is found
	for(int i=0;i<SUPPORTED_REPORTS.length;i++)
	{
		if( SUPPORTED_REPORTS[i].equalsIgnoreCase(typeStr) )
			return i;
	}

	//Must not have found it
	throw new Error("ReportTypes::getType(String) - Unrecognized type: " + typeStr);
}
}
