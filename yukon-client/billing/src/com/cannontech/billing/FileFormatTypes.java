package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 2:08:15 PM)
 * @author: 
 */
public final class FileFormatTypes {

	// Constants representing types of file formats
	// ** THESE MUST MATCH THE DATABASE TABLE BILLINGFILEFORMATS **
	// ** IF YOU ADD TO THESE VALUES, YOU MUST ADD TO THE DATABASE
	// ** TABLE THE FORMATS THAT ARE VALID FOR EACH CUSTOMER.
	public static final int INVALID = -1;
	public static final int SEDC = 0;
	public static final int CADP = 1;
	public static final int CADPXL2 = 2;
	public static final int WLT_40 = 3;
	public static final int CTICSV = 4;
	public static final int OPU = 5;
	public static final int DAFFRON = 6;
	public static final int NCDC = 7;
//	public static final int CADPXL1 = 8;
	public static final int CTIStandard2 = 9;
	public static final int MVRS = 10;
	public static final int MV_90 = 11;
	public static final int SEDC_5_4 = 12;
	public static final int NISC = 13;
	public static final int NISC_NCDC = 14;
	public static final int NCDC_HANDHELD = 15;
	

	public static final String SEDC_STRING = "SEDC";
	public static final String CADP_STRING = "CADP";
	public static final String CADPXL2_STRING = "CADPXL2";
	public static final String WLT_40_STRING = "WLT-40";
	public static final String CTICSV_STRING = "CTI-CSV";
	public static final String OPU_STRING = "OPU";
	public static final String DAFFRON_STRING = "DAFFRON";
	public static final String NCDC_STRING = "NCDC";
	public static final String CTI_STANDARD2_STRING = "CTI2";
	public static final String MVRS_STRING = "MVRS";
	public static final String SEDC_5_4_STRING = "SEDC 5.4";
	public static final String NISC_STRING = "NISC-Turtle";
	public static final String NISC_NCDC_STRING = "NISC-NCDC";
	public static final String NCDC_HANDHELD_STRING = "NCDC-Handheld";
	
	//Set DEFAULT array values in case the table doesn't exist.
	private static int[] validFormatIds =
	{
		SEDC, 
		CADP,
		CADPXL2, 
		WLT_40, 
		CTICSV, 
		OPU, 
		DAFFRON, 
		NCDC,
		CTIStandard2, 
		SEDC_5_4,
		NISC,
		NISC_NCDC,
		NCDC_HANDHELD,
		MVRS
	};
	
	private static String[] validFormatTypes =
	{
		SEDC_STRING, 
		CADP_STRING, 
		CADPXL2_STRING, 
		WLT_40_STRING, 
		CTICSV_STRING, 
		OPU_STRING, 
		DAFFRON_STRING, 
		NCDC_STRING, 
		CTI_STANDARD2_STRING,
		SEDC_5_4_STRING,
		NISC_STRING,
		NISC_NCDC_STRING,
		NCDC_HANDHELD_STRING,
		MVRS_STRING
	};


/**
 * This method was created in VisualAge.
 * @return int
 * @param typeStr java.lang.String
 */
public final static int getFormatID(String typeStr)
{
	//Go through the valid FormatTypes Array and return 
	//  the int associated with it from valid FormatIDs Array
	for(int i = 0; i < validFormatTypes.length; i++)
	{
		if( validFormatTypes[i].equalsIgnoreCase(typeStr) )
			return validFormatIds[i];
	}

	//Must not have found it
	throw new Error("FileFormatTypes::getFormatID(String) - Unrecognized type: " + typeStr);
}
/**
 * Insert the method's description here.
 * Creation date: (5/18/00 2:34:54 PM)
 */
public final static String getFormatType(int typeEnum) 
{
	//Go through the valid FormatIDs Array and return 
	//  the int associated with it from valid FormatTypes Array
	for(int i = 0; i < validFormatIds.length; i++)
	{
		if( validFormatIds[i] == typeEnum )
			return validFormatTypes[i];
	}

	//Must not have found it
	throw new Error("FileFormatTypes::getFormatType(int) - received unknown type: " + typeEnum );			
}
public static int[] getValidFormatIDs()
{
	return validFormatIds;
}
public static  String[] getValidFormatTypes()
{
	return validFormatTypes;
}
public static void setValidFormatIDs(int[] formatIDs)
{
	validFormatIds = formatIDs;
}
public static void setValidFormatTypes (String[] formatTypes)
{
	validFormatTypes = formatTypes;
}
}
