package com.cannontech.billing.record;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.cannontech.billing.mvrsformatsegments.CustomerExtraRecord;
import com.cannontech.billing.mvrsformatsegments.CustomerRecord;
import com.cannontech.billing.mvrsformatsegments.CycleHeaderRecordAndTrailer;
import com.cannontech.billing.mvrsformatsegments.FileHeaderRecordAndTrailer;
import com.cannontech.billing.mvrsformatsegments.MeterRecord;
import com.cannontech.billing.mvrsformatsegments.OffSiteReadsRecord;
import com.cannontech.billing.mvrsformatsegments.OpticalProbeInputData;
import com.cannontech.billing.mvrsformatsegments.ReadingRecord;
import com.cannontech.billing.mvrsformatsegments.RemoteReadRecord;
import com.cannontech.billing.mvrsformatsegments.RouteHeaderMessageRecord;
import com.cannontech.billing.mvrsformatsegments.RouteHeaderRecordAndTrailer;
import com.cannontech.billing.mvrsformatsegments.RouteHeaderSurveyRecord;
import com.cannontech.billing.mvrsformatsegments.TableRecords;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.point.RawPointHistory;

/**
 * Insert the type's description here.
 * Creation date: (6/28/00 11:55:04 AM)
 * @author: 
 */
public class MVRSRecord implements BillingRecordBase
{
	private FileHeaderRecordAndTrailer fileHeaderRecordAndTrailer;
	private TableRecords tableRecords;
	private CycleHeaderRecordAndTrailer cycleHeaderRecordAndTrailer;
	private RouteHeaderRecordAndTrailer routeHeaderRecordAndTrailer;
	private RouteHeaderSurveyRecord routeHeaderSurveyRecord;
	private RouteHeaderMessageRecord routeHeaderMessageRecord;
	private CustomerRecord customerRecord;
	private CustomerExtraRecord customerExtraRecord;
	private MeterRecord meterRecord;
	private OpticalProbeInputData opticalProbeInputData;
	private ReadingRecord readingRecord;
	private OffSiteReadsRecord offSiteReadsRecord;
	private RemoteReadRecord remoteReadRecord;
	static int numberCustomers = 0;
	static int numberRoutes = 0;
	static int numberMeters = 0;
	static int numberReads = 0;
	static int inputLine = 1;
	private String inputFile = CtiUtilities.getExportDirPath() + "/inFile.txt";
	//Map of deviceName (MeterNumber) string to RPH object
	private HashMap deviceNameToRPHMap = null;
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddyyyy"); 
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmss");
/**
 * MVRSFormat constructor comment.
 */
public MVRSRecord()
{
	super();
}
/**
 * dataToString method comment.
 */
public String dataToString()
{
	StringBuffer writeToFile = new StringBuffer();
	try
	{
	    File file = new File(getInputFile());
	    if( !file.exists())
	    {
	        CTILogger.info("File: " + getInputFile() + " Does Not Exist!  Cannot Generate a billing file");
	        return "";
	    }
		java.io.FileReader fileReader = new java.io.FileReader(file);
		java.io.BufferedReader fromFile = new java.io.BufferedReader(fileReader);
		StringBuffer buffer = new StringBuffer();
		buffer.append(fromFile.readLine());
		if (!buffer.toString().substring(0,3).equalsIgnoreCase("FHD"))
		{
			// invalid data; no file header
			CTILogger.info("MISSING MVRS FILE HEADER - PROCESS TERMINATED");
			return "";
		}
		writeToFile.append(processFileHeader());
		while (!(buffer.toString().substring(0,3).equalsIgnoreCase("FTR")))
		{
			inputLine++;
			buffer.replace(0, buffer.length(), fromFile.readLine());
			processMVRSField(buffer.toString(), writeToFile);			
		}
		
	} catch(Exception e){e.printStackTrace();}
	
	return writeToFile.toString();
}

/**
 * Gets reading data from database
 * Creation date: (6/2/00 9:42:19 AM)
 */
public final HashMap getDeviceNameToRPHMap() throws java.sql.SQLException 
{
    if( deviceNameToRPHMap == null)
    {
        deviceNameToRPHMap = new HashMap();
        
		String sql = "SELECT RPH1.CHANGEID, RPH1.POINTID, RPH1.TIMESTAMP, RPH1.QUALITY, RPH1.VALUE, RPH.MILLIS, PAO.PAONAME " + 
		        		" FROM RAWPOINTHISTORY RPH1, POINT P, YUKONPAOBJECT PAO " + 
		        		" WHERE RPH1.TIMESTAMP = ( " + 
		        		" SELECT MAX(RPH2.TIMESTAMP) FROM RAWPOINTHISTORY RPH2, POINT P2 " + 
		        		" WHERE RPH2.POINTID = RPH1.POINTID " + 
		        		" AND RPH2.POINTID = P2.POINTID " + 
		        		" AND P2.POINTOFFSET = 1 " + 
		        		" AND P2.POINTTYPE = 'PulseAccumulator' ) " + 
		        		" AND P.POINTID = RPH1.POINTID " +
		        		" AND P.PAOBJECTID = PAO.PAOBJECTID";
		        
	//	        "SELECT TO_CHAR(TIMESTAMP, 'MMDDYYYY') FROM RAWPOINTHISTORY WHERE POINTID = "+
	//					"(SELECT POINTID FROM POINT WHERE PAOBJECTID = (SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE "+
	//					"PAONAME = '" + (meterRecord.meterNumber).toString() + "')) AND POINTID IN (SELECT POINTID "+
	//					"FROM POINT WHERE POINTOFFSET = 1) AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY "+
	//					"WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' AND PAOBJECTID = "+
	//					"(SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE PAONAME = '" + (meterRecord.meterNumber).toString() + 
	//					"')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1))", databaseAlias );
		
	//	String readDate = null;
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				CTILogger.info(getClass() + ":  Error getting database connection.");
				return null;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
	
				CTILogger.info(" * Start looping through return resultset");
				
				while (rset.next())
				{
				    int changeID = rset.getInt(1);
				    int pointID = rset.getInt(2);
					java.sql.Timestamp ts = rset.getTimestamp(3);
					GregorianCalendar tsCal = new GregorianCalendar();
					tsCal.setTimeInMillis(ts.getTime());
					int quality = rset.getInt(4);
					double value = rset.getDouble(5);
					short millis = rset.getShort(6);
					String name = rset.getString(7);	//key value
					RawPointHistory dummyRPH = new RawPointHistory(new Integer(changeID), new Integer(pointID), tsCal, new Integer(quality), new Double(value));
					deviceNameToRPHMap.put(name, dummyRPH);
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
				if( rset != null ) rset.close();
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				e2.printStackTrace();//sometin is up
			}	
		}
    }
    return deviceNameToRPHMap;
}
/**
 * Gets KWH reading from database
 * Creation date: (6/2/00 9:42:19 AM)
 */
//public final String getReading(String databaseAlias) throws java.sql.SQLException {
//	
//	SqlStatement stmt = new SqlStatement("SELECT VALUE FROM RAWPOINTHISTORY " +
//	        " WHERE POINTID = (SELECT POINTID FROM POINT WHERE DEVICEID = " +
//	        " (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + 
//	        (meterRecord.meterNumber).toString() + 
//	        "')) AND POINTID IN (SELECT POINTID FROM POINT " + 
//	        " WHERE POINTOFFSET = 1 AND POINTTYPE = 'Analog') " + 
//	        " AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY " + 
//	        " WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' " +
//	        " AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + 
//	        (meterRecord.meterNumber).toString() + 
//	        "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1))", databaseAlias );
//	
//	String reading = null;
//	try
//	{
//		stmt.execute();
//	}
//	catch( Exception e )
//	{
//		e.printStackTrace();
//	}
//	if (stmt.getRowCount() > 0)
//		reading = new String((String)stmt.getRow(0)[0]);
//	else
//		reading = new String("");
//	return reading;
//
//}
///**
// * Gets the reading time from the database
// * Creation date: (6/2/00 9:42:19 AM)
// */
//public final String getReadTime(String databaseAlias) throws java.sql.SQLException {
//	
//	com.cannontech.database.SqlStatement stmt =
//	new com.cannontech.database.SqlStatement("SELECT TO_CHAR(TIMESTAMP, 'HH24MMSS') FROM RAWPOINTHISTORY WHERE POINTID = (SELECT POINTID FROM POINT WHERE DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + (meterRecord.meterNumber).toString() + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1) AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + (meterRecord.meterNumber).toString() + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1))"
//												,databaseAlias );
//	
//	String readTime = null;	
//	try
//	{
//		stmt.execute();
//	}
//	catch( Exception e )
//	{
//		e.printStackTrace();
//	}
//	if (stmt.getRowCount() > 0)
//		readTime = new String((String)stmt.getRow(0)[0]);
//	else
//		readTime = new String("      ");
//
//	return readTime;
//}
/**
 * Handles a customer record from the input file
 * Creation date: (6/1/00 10:59:18 AM)
 * @return java.lang.String
 * @param buffer java.lang.String
 */
public final String processCustomerRecord(String buffer) {
	StringBuffer storage = new StringBuffer();
	storage.append(buffer.substring(0,97));
	storage.append(buffer.substring(117,118));
	storage.append("N");
	storage.append(buffer.substring(118,125));
	storage.append(buffer.substring(97,117));
	storage.append("\r\n");
	numberCustomers++;
	return storage.toString();
}
/**
 * Handles the input file's header
 * Creation date: (6/1/00 10:39:24 AM)
 * @return java.lang.String
 * @param buffer java.lang.String
 */
public final String processFileHeader() {
	StringBuffer storage = new StringBuffer();
	storage.append("FHD");
	for (int x=0; x<123; x++)
		storage.append(" "); // Reserved/undefined spaces
	storage.append("\r\n");
	return storage.toString();
}
/**
 * Handles the input file's trailer
 * Creation date: (6/1/00 3:25:11 PM)
 * @return java.lang.String
 */
public final String processFileTrailer() {
	StringBuffer storage = new StringBuffer();
	storage.append("FTR");
	storage.append("    "); // data not present + 1 "Reserved" space
	for (int x=0; x<(3-(Integer.toString(numberRoutes).length())); x++)
		storage.append("0");
	storage.append(Integer.toString(numberRoutes));
	for (int x=0; x<(6-(Integer.toString(numberCustomers).length())); x++)
		storage.append("0");
	storage.append(Integer.toString(numberCustomers));
	for (int x=0; x<(6-(Integer.toString(numberCustomers).length())); x++)
		storage.append("0");
	storage.append(Integer.toString(numberMeters));
	for (int x=0; x<(6-(Integer.toString(numberCustomers).length())); x++)
		storage.append("0");
	storage.append(Integer.toString(numberReads));
	for (int x=0; x<91; x++)
		storage.append(" "); // "Reserved" spaces
	storage.append("000000");
	storage.append("\r\n");
	return storage.toString();
}
/**
 * Handles a meter record from the input file
 * Creation date: (6/1/00 11:34:54 AM)
 * @return java.lang.String
 * @param buffer java.lang.String
 */
public final String processMeterRecord(String buffer) {
	StringBuffer storage = new StringBuffer();
	storage.append(buffer.substring(0,17));
	storage.append("000000");
	storage.append(buffer.substring(23,69));
	storage.append(buffer.substring(61,69));
	storage.append(buffer.substring(89,99));
	storage.append(buffer.substring(100,101));
	storage.append("     "); // data not read in
	storage.append(buffer.substring(101,106));
	storage.append("   ");
	storage.append(buffer.substring(69,89));
	storage.append("     ");
	storage.append("\r\n");
	meterRecord.meterNumber = null;
	meterRecord.meterNumber = new StringBuffer(buffer.substring(45,57).trim());
	numberMeters++;
	return storage.toString();
}
/**
 * This method checks a record indicator and sends the program to the correct processing method.
 * Creation date: (6/1/00 10:22:36 AM)
 * @param indicator java.lang.String
 */
public final void processMVRSField(String buffer, StringBuffer writeToFile) {
	if (buffer.regionMatches(true, 0, "CHD", 0, 3) || buffer.regionMatches(true, 0, "CTR", 0, 3))
		return; // not implemented
	if (buffer.regionMatches(true, 0, "FHD", 0, 3))
		writeToFile.append(processFileHeader());
	else if (buffer.regionMatches(true, 0, "FTR", 0, 3))
		writeToFile.append(processFileTrailer());
	else if (buffer.regionMatches(true, 0, "RHD", 0, 3) || buffer.regionMatches(true, 0, "RTR", 0, 3))
		writeToFile.append(processRouteHeaderAndTrailer(buffer));
	else if (buffer.regionMatches(true, 0, "CUS", 0, 3))
		writeToFile.append(processCustomerRecord(buffer));
	else if (buffer.regionMatches(true, 0, "MTR", 0, 3))
		writeToFile.append(processMeterRecord(buffer));
	else if (buffer.regionMatches(true, 0, "RDG", 0, 3))
		writeToFile.append(processReadingRecord(buffer));
	// others not implemented, as in original C code
	else CTILogger.info("MISSING/UNSUPPORTED MVRS RECORD INDICATOR, LINE " + Integer.toString(inputLine) + " - LINE IGNORED");
}
/**
 * Handles a reading record from the input file
 * Creation date: (6/1/00 3:58:54 PM)
 * @return java.lang.String
 * @param buffer java.lang.String
 * @param reading java.lang.String
 * @param readDate java.lang.String
 * @param readTime java.lang.String
 */
public final String processReadingRecord(String buffer) {
	StringBuffer storage = new StringBuffer();
	try {
		storage.append(buffer.substring(0,15));
		storage.append(buffer.substring(16,26));
		storage.append(buffer.substring(30,31));
		storage.append(buffer.substring(61,68));
		RawPointHistory dummyRPH = (RawPointHistory)getDeviceNameToRPHMap().get(meterRecord.meterNumber);
		String reading = "";
		if( dummyRPH != null)
		    reading = dummyRPH.getValue().toString();
		for (int x=0; x<10-reading.length(); x++)
			storage.append("0");
		storage.append(reading);
		storage.append("0000");
		if( dummyRPH != null)
		    storage.append(DATE_FORMAT.format(dummyRPH.getTimeStamp().getTime()));
		else
		    storage.append("        ");
		if( dummyRPH != null)
		    storage.append(TIME_FORMAT.format(dummyRPH.getTimeStamp().getTime()));
		else
		    storage.append("      ");
		storage.append("N");
		storage.append("000000000000");
		storage.append("N");
		storage.append("         "); // data not present
		storage.append("00000"); // data not present
		storage.append(buffer.substring(26,30));
		storage.append("  "); // data not present
		storage.append("0"); // data not present
		storage.append(buffer.substring(61,67));
		storage.append(buffer.substring(31,41));
		storage.append("           "); // "Reserved" spaces
		storage.append("\r\n");
	} catch (java.sql.SQLException e) {e.printStackTrace();}
	numberReads++;
	return storage.toString();
}
/**
 * Handles the route header/trailer from the input file
 * Creation date: (6/1/00 2:45:17 PM)
 * @return java.lang.String
 * @param buffer java.lang.String
 */
public static final String processRouteHeaderAndTrailer(String buffer) {
	StringBuffer storage = new StringBuffer();
	storage.append(buffer.substring(0,51));
	storage.append("  ");
	storage.append(buffer.substring(37,45));
	storage.append(buffer.substring(17,21));
	storage.append("00000000");
	storage.append(buffer.substring(51,77));
	storage.append("          "); // data not present in host download
	storage.append(buffer.substring(79,83));
	storage.append("           "); // "Reserved" spaces
	storage.append("\r\n");
	numberRoutes++;
	return storage.toString();
}
    /**
     * @return Returns the inputFile.
     */
    public String getInputFile()
    {
        return inputFile;
    }
    /**
     * @param inputFile The inputFile to set.
     */
    public void setInputFile(String inputFile)
    {
        this.inputFile = inputFile;
    }
}
