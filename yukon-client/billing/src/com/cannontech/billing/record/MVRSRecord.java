package com.cannontech.billing.record;

import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

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

	private static final DecimalFormat READING_FORMAT = new DecimalFormat("0000000000"); 
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMddyyyy"); 
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmss");
/**
 * MVRSFormat constructor comment.
 */
public MVRSRecord()
{
	super();
	meterRecord = new MeterRecord();
}
/**
 * dataToString method comment.
 */
public String dataToString()
{
	Vector vectorOfLines = new Vector();
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
		vectorOfLines.add(processFileHeader());
		
		while (!(buffer.toString().substring(0,3).equalsIgnoreCase("FTR")))
		{
			inputLine++;
			buffer.replace(0, buffer.length(), fromFile.readLine());
			vectorOfLines.add(processMVRSField(buffer.toString()));			
		}
		
	} catch(Exception e){e.printStackTrace();}
	
	StringBuffer writeToFile = new StringBuffer();
	for (int i = 0; i < vectorOfLines.size(); i++)
	{
		//Find the FHD record and update it!
		String str = (String)vectorOfLines.get(i);
		if( str.startsWith("FHD") )
		{
			vectorOfLines.set(i, processFileHeader());			
		}
		
		writeToFile.append(vectorOfLines.get(i));	
	}
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
        
		String sql = "SELECT RPH1.CHANGEID, RPH1.POINTID, RPH1.TIMESTAMP, RPH1.QUALITY, RPH1.VALUE, RPH1.MILLIS, PAO.PAONAME " + 
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
	
	storage.append("CUS");
	storage.append(buffer.substring(3, 11));	//A/N8 RouteID
	storage.append(buffer.substring(11, 14));	//N3 Number Meters
	storage.append(buffer.substring(14, 34));	//A/N20 Account Number
	storage.append(buffer.substring(34, 54));	//A 20 Account Name 
	storage.append(buffer.substring(54, 94));	//A/N40 Address
	storage.append("00" + buffer.substring(96, 97));	//N3 Group Code
	storage.append(buffer.substring(117, 118));	//A1 Extra Customer Record
	storage.append("N");	//A1 Exception Code ???Not sure if this should be "N" or " "
	storage.append(buffer.substring(118, 122));	//A/N4 Segment Code
	storage.append(buffer.substring(122, 124));	//A/N2 Utility ID
	storage.append(buffer.substring(124, 125));	//A/N1 Customer Flag
	storage.append(buffer.substring(97, 117));	//A/N20 Addition Customer information
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
	DecimalFormat countFormat = new DecimalFormat("000000");
	storage.append("FHD");
	storage.append(" ");	//A1 New Service Indicator
	storage.append(" ");	//A1 Future Use
	storage.append(" ");	//A1 Change Meter Indicator
	storage.append(" ");	//A1 Future Use
	storage.append("000");	//N3 Number Routes (N/A, Yukon doesn't process these records)
	storage.append(countFormat.format(numberCustomers));	//N6 Number Customers
	storage.append(countFormat.format(numberMeters));	//N6 Number Meters
	storage.append(countFormat.format(numberReads));	//N6 Number Reads
	
	while (storage.length() < 126)
		storage.append(" "); //Extended Route Header Flag, Pad, Reserved (Not Supported by Yukon)
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
	storage.append("MTR");
	storage.append(buffer.substring(3, 11));	//A/N8 Route Number
	storage.append(buffer.substring(11, 14));	//N3 Number Reads
	storage.append("00" + buffer.substring(16, 17));	//N3 Group Code
	storage.append("000" + "000");	//N3 Future Use, N3 Future use
	storage.append("00" + buffer.substring(25, 26));	//N3 Survey Code 1
	storage.append("00" + buffer.substring(28, 29));	//N3 Survey Code 2
	storage.append(buffer.substring(29, 30));	//A1 Bill Code
	storage.append(buffer.substring(30, 31));	//A1 Meter Status
	storage.append(buffer.substring(31, 45));	//A/N14 Optical Probe Record ID
	storage.append(buffer.substring(45, 57));	//A/N12 Meter Number
	storage.append("00" + buffer.substring(59, 61));	//N4 Meter Type
	storage.append(buffer.substring(61, 69));	//N8 Meter Read Sequence
	storage.append(buffer.substring(61, 69));	//N8 New Read Sequence
	storage.append("0" + buffer.substring(90, 92));	//N3 Location
	storage.append("0" + buffer.substring(93, 95));	//N3 Meter Instruction 1
	storage.append("0" + buffer.substring(96, 98));	//N3 Meter Instruction 2
	storage.append(buffer.substring(98, 99));	//N1 Special message Display
	storage.append(buffer.substring(100, 101));	//A1 Special Message indicator
	storage.append(" ");	//A1 Trouble Message Indicator
	storage.append(" ");	//A/N1 Survey Response1
	storage.append(" ");	//A/N1 Survey Response2
	storage.append(" ");	//A1 Meter Change Out
	storage.append(" ");	//A1 Change Meter Record Indicator
	storage.append(buffer.substring(101, 102));	//A1 Meter Category
	storage.append(buffer.substring(102, 103));	//A1 Location/Extra Meter
	storage.append(buffer.substring(103, 106));	//N3 Time Code
	storage.append("   ");	//A/N3 Future Use
	storage.append(buffer.substring(69, 89));	//A/N20 Original Additional Meter Info
	storage.append("     ");	//A/N5 Future Use
	
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
public final String processMVRSField(String buffer){//, StringBuffer writeToFile) {
	String record = "";
	if (buffer.regionMatches(true, 0, "CHD", 0, 3) || buffer.regionMatches(true, 0, "CTR", 0, 3))
		record = ""; // not implemented
		
	else if (buffer.regionMatches(true, 0, "FHD", 0, 3))
		record = processFileHeader();
		
	else if (buffer.regionMatches(true, 0, "FTR", 0, 3))
		record = processFileTrailer();
		
	else if (buffer.regionMatches(true, 0, "RHD", 0, 3) || buffer.regionMatches(true, 0, "RTR", 0, 3))
		record = processRouteHeaderAndTrailer(buffer);
		
	else if (buffer.regionMatches(true, 0, "CUS", 0, 3))
		record = processCustomerRecord(buffer);
		
	else if (buffer.regionMatches(true, 0, "MTR", 0, 3))
		record = processMeterRecord(buffer);
		
	else if (buffer.regionMatches(true, 0, "RDG", 0, 3))
		record = processReadingRecord(buffer);
		
	// others not implemented, as in original C code
	else CTILogger.info("MISSING/UNSUPPORTED MVRS RECORD INDICATOR, LINE " + Integer.toString(inputLine) + " - LINE IGNORED");
	
	return record;
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
	
	storage.append("RDG");
	storage.append(buffer.substring(3, 11));	//A/N8 RouteNumber
	storage.append(buffer.substring(11, 15));	//A/N4 Text Prompt
	storage.append(buffer.substring(16, 17));	//A1 Read Direction
	storage.append(buffer.substring(17, 20));	//N3 Compare Code
	storage.append(buffer.substring(20, 23));	//N3 Validation Code
	storage.append("0" + buffer.substring(24, 26));	//N3 Channel Number
	storage.append(buffer.substring(30, 31));	//A1 Read Method
	storage.append(buffer.substring(61, 67));	//N6 Meter Constant
	storage.append(buffer.substring(67, 68));	//N1 Constant Flag
	
	RawPointHistory dummyRPH = null;
	try
	{
		dummyRPH = (RawPointHistory)getDeviceNameToRPHMap().get(meterRecord.meterNumber);
	}
	catch (SQLException e1)	{ }
	if( dummyRPH != null)
	{
		storage.append(READING_FORMAT.format(dummyRPH.getValue()));	//N10 Reading
		storage.append("0000");	//N4 Read Order
		storage.append(DATE_FORMAT.format(dummyRPH.getTimeStamp().getTime()));	//D8 Read Date
		storage.append(TIME_FORMAT.format(dummyRPH.getTimeStamp().getTime()));	//T6 Read Time
	}
	else	//Defaults for the above fields
	{
		storage.append("0000000000");
		storage.append("0000");
		storage.append("        ");
		storage.append("      ");
	}
	
	storage.append("N");	//A1 Read code
	storage.append("000");	//N3 Read Reason
	storage.append("0");	//N1 Read Audit Result
	storage.append("000");	//N3 Trouble Code 1
	storage.append("000");	//N3 Trouble Code 2
	storage.append("0");	//N1 Audit Counter
	storage.append("0");	//N1 Clear Counter
	storage.append("N");	//A1 Unporcessed Read Flag
	storage.append("         ");	//A/N9 Meter ReaderID (N/A)
	storage.append("00000");	//N5 Elapsed Time
	storage.append(buffer.substring(26, 28));	//N2 New Number Dials
	storage.append(buffer.substring(28, 30));	//N2 New Number Decimals
	storage.append(" ");	//A1 New Read Method
	storage.append(" ");	//A1 Meter Constant Verified
	storage.append("0");	//N1 HHF Position
	storage.append(buffer.substring(61, 67));	//N6 New Meter Constant
	storage.append("0" + buffer.substring(76, 78));	//N3 Read Type
	storage.append(buffer.substring(31, 41));	//N10 Previous Reading
	storage.append("           ");	//A/N11	Future Use
	storage.append("\r\n");
	
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
	storage.append("00");	//force complete code
	storage.append(buffer.substring(81,85));
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
