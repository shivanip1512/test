package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class MVRSFormat extends FileFormatBase
{
	private com.cannontech.billing.mvrsformatsegments.FileHeaderRecordAndTrailer fileHeaderRecordAndTrailer;
	private com.cannontech.billing.mvrsformatsegments.TableRecords tableRecords;
	private com.cannontech.billing.mvrsformatsegments.CycleHeaderRecordAndTrailer cycleHeaderRecordAndTrailer;
	private com.cannontech.billing.mvrsformatsegments.RouteHeaderRecordAndTrailer routeHeaderRecordAndTrailer;
	private com.cannontech.billing.mvrsformatsegments.RouteHeaderSurveyRecord routeHeaderSurveyRecord;
	private com.cannontech.billing.mvrsformatsegments.RouteHeaderMessageRecord routeHeaderMessageRecord;
	private com.cannontech.billing.mvrsformatsegments.CustomerRecord customerRecord;
	private com.cannontech.billing.mvrsformatsegments.CustomerExtraRecord customerExtraRecord;
	private com.cannontech.billing.mvrsformatsegments.MeterRecord meterRecord;
	private com.cannontech.billing.mvrsformatsegments.OpticalProbeInputData opticalProbeInputData;
	private com.cannontech.billing.mvrsformatsegments.ReadingRecord readingRecord;
	private com.cannontech.billing.mvrsformatsegments.OffSiteReadsRecord offSiteReadsRecord;
	private com.cannontech.billing.mvrsformatsegments.RemoteReadRecord remoteReadRecord;
	static int numberCustomers;
	static int numberRoutes;
	static int numberMeters;
	static int numberReads;
	static int inputLine;
/**
 * MVRSFormat constructor comment.
 */
public MVRSFormat()
{
	super();
	//setInputFileName( getInputFileName());     /* this is hard coded for now */
	numberMeters = 0;
	numberRoutes = 0;
	numberCustomers = 0;
	numberReads = 0;
	inputLine = 1;
	meterRecord = new com.cannontech.billing.mvrsformatsegments.MeterRecord();
}
/**
 * Gets data from input file and database and converts it to a String object
 * Creation date: (5/30/00 1:30:25 PM)
 */
public String dataToString()
{
	StringBuffer writeToFile = new StringBuffer();
	try
	{
		java.io.FileReader file = new java.io.FileReader(getInputFileName());
		java.io.BufferedReader fromFile = new java.io.BufferedReader(file);
		StringBuffer buffer = new StringBuffer();
		buffer.append(fromFile.readLine());
		if (!buffer.toString().substring(0,3).equalsIgnoreCase("FHD"))
		{
			// invalid data; no file header
			System.out.println("MISSING MVRS FILE HEADER - PROCESS TERMINATED");
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
public final String getReadDate(String databaseAlias) throws java.sql.SQLException {
	
	com.cannontech.database.SqlStatement stmt =
	//new com.cannontech.database.SqlStatement("SELECT TO_CHAR(TIMESTAMP, 'MMDDYYYY') FROM RAWPOINTHISTORY WHERE POINTID = (SELECT POINTID FROM POINT WHERE DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + (meterRecord.meterNumber).toString() + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1) AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + (meterRecord.meterNumber).toString() + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1))"
												//,databaseAlias );
	new com.cannontech.database.SqlStatement("SELECT TO_CHAR(TIMESTAMP, 'MMDDYYYY') FROM RAWPOINTHISTORY WHERE POINTID = "+
											"(SELECT POINTID FROM POINT WHERE PAOBJECTID = (SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE "+
											"PAONAME = '" + (meterRecord.meterNumber).toString() + "')) AND POINTID IN (SELECT POINTID "+
											"FROM POINT WHERE POINTOFFSET = 1) AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY "+
											"WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' AND PAOBJECTID = "+
											"(SELECT PAOBJECTID FROM YUKONPAOBJECT WHERE PAONAME = '" + (meterRecord.meterNumber).toString() + 
											"')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1))", databaseAlias );
	
	String readDate = null;	
	try
	{
		stmt.execute();
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	if (stmt.getRowCount() > 0)
		readDate = new String((String)stmt.getRow(0)[0]);
	else
		readDate = new String("        ");
	return readDate;
}
/**
 * Gets KWH reading from database
 * Creation date: (6/2/00 9:42:19 AM)
 */
public final String getReading(String databaseAlias) throws java.sql.SQLException {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT VALUE FROM RAWPOINTHISTORY WHERE POINTID = (SELECT POINTID FROM POINT WHERE DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + (meterRecord.meterNumber).toString() + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1 AND POINTTYPE = 'Analog') AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + (meterRecord.meterNumber).toString() + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1))"
												,databaseAlias );
	
	String reading = null;
	try
	{
		stmt.execute();
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	if (stmt.getRowCount() > 0)
		reading = new String((String)stmt.getRow(0)[0]);
	else
		reading = new String("");
	return reading;

}
/**
 * Gets the reading time from the database
 * Creation date: (6/2/00 9:42:19 AM)
 */
public final String getReadTime(String databaseAlias) throws java.sql.SQLException {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT TO_CHAR(TIMESTAMP, 'HH24MMSS') FROM RAWPOINTHISTORY WHERE POINTID = (SELECT POINTID FROM POINT WHERE DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + (meterRecord.meterNumber).toString() + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1) AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + (meterRecord.meterNumber).toString() + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1))"
												,databaseAlias );
	
	String readTime = null;	
	try
	{
		stmt.execute();
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	if (stmt.getRowCount() > 0)
		readTime = new String((String)stmt.getRow(0)[0]);
	else
		readTime = new String("      ");

	return readTime;
}
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
	else System.out.println("MISSING/UNSUPPORTED MVRS RECORD INDICATOR, LINE " + Integer.toString(inputLine) + " - LINE IGNORED");
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
		String reading = new String(getReading("yukon"));
		for (int x=0; x<10-reading.length(); x++)
			storage.append("0");
		storage.append(reading);
		storage.append("0000");
		storage.append(getReadDate("yukon"));
		storage.append(getReadTime("yukon"));
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
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveBillingData(java.util.Vector collectionGroups, String databaseAlias)
{
	return false;

	/*String databaseAlias = new String("yukon");

	java.util.List billingDevices = retrieveCurrentBillingMeterList(collectionGroups,databaseAlias);
	java.util.List points = com.cannontech.database.cache.DefaultDatabaseCache.getInstance().getAllPoints();

	String sqlString = new String("SELECT CHANGEID, POINTID, TIMESTAMP, QUALITY, VALUE FROM RAWPOINTHISTORY WHERE POINTID IN SELECT");
	for(int i=1;i<collectionGroups.size();i++)
	{
		sqlString += " OR COLLECTIONGROUP = '" + collectionGroups.get(i) + "'";
	}
	sqlString += ")";

	java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
	preparedStatement = conn.prepareStatement(sqlString);

	Integer accountNumber = null;
	String importType = new String("HH");
	String serviceGroup = new String("ELEC");
	String paymentSign = new String(" ");
	Double payment = new Double(0.0);
	java.util.GregorianCalendar batchDate = new java.util.GregorianCalendar();
	String batchNumber = new String("800");
	java.util.GregorianCalendar readDate = null;
	String meterNumber = null;
	Integer meterPositionNumber = null;
	java.util.Vector registerNumberVector = null;
	java.util.Vector kwhReadingVector = null;
	java.util.Vector kwReadingVector = null;
	java.util.Vector kvarReadingVector = null;

	try
	{
		java.sql.ResultSet rset = preparedStatement.executeQuery();

		int tempDeviceType = 0;

		while(rset.next())
		{
			registerNumberVector = new java.util.Vector(4);
			kwhReadingVector = new java.util.Vector(4);
			kwReadingVector = new java.util.Vector(4);
			kvarReadingVector = new java.util.Vector(4);
		}

		for(int i=0;i<=100%8+1;i++)
		{
			for(int j=0;j<8;j++)
			{
				int tempRowNumber = (i*8)+j;
				tempDeviceType = com.cannontech.database.data.device.DeviceTypes.getType( ((String)(stmt.getRow(tempRowNumber)[0])) );
				if( tempDeviceType == com.cannontech.database.data.device.DeviceTypes.ALPHA ||
						tempDeviceType == com.cannontech.database.data.device.DeviceTypes.VECTRON ||
						tempDeviceType == com.cannontech.database.data.device.DeviceTypes.FULCRUM ||
						tempDeviceType == com.cannontech.database.data.device.DeviceTypes.LANDISGYRS4 ||
						tempDeviceType == com.cannontech.database.data.device.DeviceTypes.MCT360 ||
						tempDeviceType == com.cannontech.database.data.device.DeviceTypes.MCT370 )
				{
					meterNumbers.add( (String)(stmt.getRow(tempRowNumber)[1]) );
					kwhReadings.add( (String)(stmt.getRow(tempRowNumber)[2]) );
					kwReadings.add( (String)(stmt.getRow(tempRowNumber)[3]) );
					kvReadings.add( (String)(stmt.getRow(tempRowNumber)[4]) );
				}
				else//regular mct
				{
					meterNumbers.add( (String)(stmt.getRow(tempRowNumber)[1]) );
					kwhReadings.add( (String)(stmt.getRow(tempRowNumber)[2]) );
					kwReadings.add( "       " );
					kvReadings.add( "     " );
				}
			}
			getRecordVector().add(new CADPXL2Record(accountNumber, importType, serviceGroup, paymentSign, payment, batchDate, batchNumber, readDate, meterNumber, meterPositionNumber, registerNumberVector, kwhReadingVector, kwReadingVector, kvarReadingVector));
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}*/
}

/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveBillingData(String databaseAlias)
{
	return false;

	/*String databaseAlias = new String("yukon");

	java.util.List billingDevices = retrieveCurrentBillingMeterList(collectionGroups,databaseAlias);
	java.util.List points = com.cannontech.database.cache.DefaultDatabaseCache.getInstance().getAllPoints();

	String sqlString = new String("SELECT CHANGEID, POINTID, TIMESTAMP, QUALITY, VALUE FROM RAWPOINTHISTORY WHERE POINTID IN SELECT");
	for(int i=1;i<collectionGroups.size();i++)
	{
		sqlString += " OR COLLECTIONGROUP = '" + collectionGroups.get(i) + "'";
	}
	sqlString += ")";

	java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
	preparedStatement = conn.prepareStatement(sqlString);

	Integer accountNumber = null;
	String importType = new String("HH");
	String serviceGroup = new String("ELEC");
	String paymentSign = new String(" ");
	Double payment = new Double(0.0);
	java.util.GregorianCalendar batchDate = new java.util.GregorianCalendar();
	String batchNumber = new String("800");
	java.util.GregorianCalendar readDate = null;
	String meterNumber = null;
	Integer meterPositionNumber = null;
	java.util.Vector registerNumberVector = null;
	java.util.Vector kwhReadingVector = null;
	java.util.Vector kwReadingVector = null;
	java.util.Vector kvarReadingVector = null;

	try
	{
		java.sql.ResultSet rset = preparedStatement.executeQuery();

		int tempDeviceType = 0;

		while(rset.next())
		{
			registerNumberVector = new java.util.Vector(4);
			kwhReadingVector = new java.util.Vector(4);
			kwReadingVector = new java.util.Vector(4);
			kvarReadingVector = new java.util.Vector(4);
		}

		for(int i=0;i<=100%8+1;i++)
		{
			for(int j=0;j<8;j++)
			{
				int tempRowNumber = (i*8)+j;
				tempDeviceType = com.cannontech.database.data.device.DeviceTypes.getType( ((String)(stmt.getRow(tempRowNumber)[0])) );
				if( tempDeviceType == com.cannontech.database.data.device.DeviceTypes.ALPHA ||
						tempDeviceType == com.cannontech.database.data.device.DeviceTypes.VECTRON ||
						tempDeviceType == com.cannontech.database.data.device.DeviceTypes.FULCRUM ||
						tempDeviceType == com.cannontech.database.data.device.DeviceTypes.LANDISGYRS4 ||
						tempDeviceType == com.cannontech.database.data.device.DeviceTypes.MCT360 ||
						tempDeviceType == com.cannontech.database.data.device.DeviceTypes.MCT370 )
				{
					meterNumbers.add( (String)(stmt.getRow(tempRowNumber)[1]) );
					kwhReadings.add( (String)(stmt.getRow(tempRowNumber)[2]) );
					kwReadings.add( (String)(stmt.getRow(tempRowNumber)[3]) );
					kvReadings.add( (String)(stmt.getRow(tempRowNumber)[4]) );
				}
				else//regular mct
				{
					meterNumbers.add( (String)(stmt.getRow(tempRowNumber)[1]) );
					kwhReadings.add( (String)(stmt.getRow(tempRowNumber)[2]) );
					kwReadings.add( "       " );
					kvReadings.add( "     " );
				}
			}
			getRecordVector().add(new CADPXL2Record(accountNumber, importType, serviceGroup, paymentSign, payment, batchDate, batchNumber, readDate, meterNumber, meterPositionNumber, registerNumberVector, kwhReadingVector, kwReadingVector, kvarReadingVector));
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}*/
}
}
