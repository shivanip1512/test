package com.cannontech.export;

/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:40:22 PM)
 * @author: 
 */
public class ExportFormatTypes
{
	public static final int CSVBILLING_FORMAT = 0;
	public static final int DBPURGE_FORMAT = 1;
	public static final int IONEVENTLOG_FORMAT = 2;

	public static String [] formatTypeNames = 
	{
		"CSVBilling",
		"DBPurge",
		"IONEventLog"
	};

/**
 * ExportFormatTypes constructor comment.
 */
public ExportFormatTypes() {
	super();
}
public static int getFormatTypeInt (String format)
{
	for (int i = 0; i < formatTypeNames.length; i++)
	{
		if( format.equalsIgnoreCase(formatTypeNames[i]))
		{
			return i;
		}
	}
	return -1;
}

public static String getFormatTypeName(int format)
{
	if( format < 0 || format > formatTypeNames.length)
		return null;	//does not exist
		
	return formatTypeNames[format];
}
}
