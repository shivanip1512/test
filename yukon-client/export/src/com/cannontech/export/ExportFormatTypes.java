package com.cannontech.export;


public class ExportFormatTypes
{
	//Valid format type ids.
	public static final int CSVBILLING_FORMAT = 0;
	public static final int DBPURGE_FORMAT = 1;
	public static final int IONEVENTLOG_FORMAT = 2;

	//String values of valid formatIDs.  Also used for service install names.
	public static String [] formatTypeNames = 
	{
		"CSVBilling",
		"DBPurge",
		"IONEventLog"
	};

	//Configuration file for valid formatIDs
	public static String [] formatWrapperConf = 
	{
		"csvwrapper.conf",
		"dbwrapper.conf",
		"ionwrapper.conf"
	};

	//Dat files for format property values.  Key=Value dat file format.
	public static String [] formatDatFileName = 
	{
		"\\csvprop.dat",
		"\\dbprop.dat",
		"\\ionprop.dat"
	};

	/**
	 * ExportFormatTypes constructor comment.
	 */
	public ExportFormatTypes()
	{
		super();
	}
	
	/**
	 * Return the formatID for the format(String).
	 * @param format java.lang.String
	 * @return int
	 */
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
	
	/**
	 * Return formatTypeName for the format(int).
	 * @param format int
	 * @return String
	 */
	public static String getFormatTypeName(int format)
	{
		if( format < 0 || format > formatTypeNames.length)
			return null;	//does not exist
			
		return formatTypeNames[format];
	}
	
	/**
	 * Return formatWrapperConf for the format(int).
	 * @param format int
	 * @return String
	 */
	public static String getFormatWrapperConf(int format)
	{
		if( format < 0 || format > formatWrapperConf.length)
			return null;	//does not exist
			
		return formatWrapperConf[format];
	}
	
	/**
	 * Return formatDatFileName for the format(int).
	 * @param format int
	 * @return String
	 */
	public static String getFormatDatFileName(int format)
	{
		if( format < 0 || format > formatDatFileName.length)
			return null;	//does not exist
			
		return formatDatFileName[format];
	}
}
