package com.cannontech.billing;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

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
//	public static final int CADPXL1 = 8; Not valid - not implemented
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

	private static int[] validFormatIDs = null;
	private static String[] validFormatTypes = null;

/**
 * This method was created in VisualAge.
 * @return int
 * @param typeStr java.lang.String
 */
public final static int getFormatID(String typeStr)
{
	//Go through the valid FormatTypes Array and return 
	//  the int associated with it from valid FormatIDs Array
	for(int i = 0; i < getValidFormatTypes().length; i++)
	{
		if( getValidFormatTypes()[i].equalsIgnoreCase(typeStr) )
			return getValidFormatIDs()[i];
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
	for(int i = 0; i < getValidFormatIDs().length; i++)
	{
		if( getValidFormatIDs()[i] == typeEnum )
			return getValidFormatTypes()[i];
	}

	//Must not have found it
	throw new Error("FileFormatTypes::getFormatType(int) - received unknown type: " + typeEnum );			
}

/**
 * Method retrieveFileFormats.
 * Retrieve possible fileformats from yukon.BillingFileFormats db table.
 * Returns true when loaded correctly
 */
public static synchronized boolean retrieveFileFormats() 
{
    boolean returnStatus = false;
    String sql = "SELECT FORMATID, FORMATTYPE FROM BILLINGFILEFORMATS" +
				" WHERE FORMATID >= 0";
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		java.util.Vector formatIDVector = new java.util.Vector();
		java.util.Vector formatTypeVector = new java.util.Vector();
		
		conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		if( conn == null )
		{
			CTILogger.info( "Error getting database connection.");
			return returnStatus;
		}
		else
		{
			stmt = conn.createStatement();	
			rset = stmt.executeQuery(sql.toString());
			int rowCount = 0;
			while( rset.next())
			{
				formatIDVector.add(new Integer(rset.getInt(1)));
				formatTypeVector.add( rset.getString(2));
				rowCount++;
			}

			if( rowCount > 0)
			{
				//(Re-)Initialize the validFormat..Arrays.
				int[] formatIDs = new int[rowCount];
				String[] formatTypes = new String[rowCount];
				
				for (int i = 0; i < rowCount; i++)
				{
					formatIDs[i] = ((Integer)formatIDVector.get(i)).intValue();
					formatTypes[i] = (String)formatTypeVector.get(i);
				}	
				
				//Copy into class arrays.
				validFormatIDs = formatIDs;
				validFormatTypes = formatTypes;
				returnStatus = true;
			}
		}
	}
			
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}
	return returnStatus;
}
    /**
     * @return Returns the validFormatIds.
     */
    public static int[] getValidFormatIDs()
    {
        if (validFormatIDs == null)
        {
            boolean success = retrieveFileFormats();
            if( !success)	//if load failed, default them.
            {
                validFormatIDs = new int[]{
                        SEDC, 
                		CADP,
                		CADPXL2, 
                		CTICSV, 
                		OPU, 
                		DAFFRON, 
                		NCDC,
                		CTIStandard2, 
                		SEDC_5_4,
                		NISC,
                		NISC_NCDC,
                		NCDC_HANDHELD
                };
            }
        }
        return validFormatIDs;
    }

	public static  String[] getValidFormatTypes()
	{
	    if (validFormatTypes == null)
	    {
	        boolean success = retrieveFileFormats();
	        if( !success) 	//if load failed, default them
	        {
	            validFormatTypes = new String[]{
	                    SEDC_STRING, 
	                    CADP_STRING, 
	            		CADPXL2_STRING, 
	            		CTICSV_STRING, 
	            		OPU_STRING, 
	            		DAFFRON_STRING, 
	            		NCDC_STRING, 
	            		CTI_STANDARD2_STRING,
	            		SEDC_5_4_STRING,
	            		NISC_STRING,
	            		NISC_NCDC_STRING,
	            		NCDC_HANDHELD_STRING
	            };
	        }
	    }
		return validFormatTypes;
	}
}
