package com.cannontech.export;

/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:40:22 PM)
 * @author: 
 */
public class ExportFormatTypes
{

	//These int variables need to match the location in the
	//  following arrays.  They are used as index variables.
	//  If you add to the Name arrays...YOU MUST ADD THE int
	//  value also that represents its index location.  SN 4/18/02

	
	public static final int CSVBILLING_FORMAT = 0;
	public static final int DBPURGE_FORMAT = 1;

	public static String [] formatTypeNames = 
	{
		"CSVBilling",
		"DBPurge"
	};

//	public static String [] formatTypeShortName =
//	{
//		"csv",
//		"dbp"
//	};
/**
 * ExportFormatTypes constructor comment.
 */
public ExportFormatTypes() {
	super();
}
public static int getFormatTypeInt (String format)
{
	if( format.equalsIgnoreCase(formatTypeNames[DBPURGE_FORMAT]))
		return DBPURGE_FORMAT;
	else if ( format.equalsIgnoreCase(formatTypeNames[CSVBILLING_FORMAT]))
		return CSVBILLING_FORMAT;
	else
		return -1;
}
public static String getFormatTypeName(int format)
{
	switch(format)
	{
		case DBPURGE_FORMAT:
			return formatTypeNames[DBPURGE_FORMAT];
		case CSVBILLING_FORMAT:
			return formatTypeNames[CSVBILLING_FORMAT];
		default:
			return null;
	}
}
}
