package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (6/27/00 4:14:10 PM)
 * @author: 
 */
public class NCDCFormat extends FileFormatBase {
	private String location;
	private String meterNumber;
	private String meter;
	private String kwhReading;
	private String kwReading;
	private String kvarReading;
	private String readDate;
	private String readTime;
	
/**
 * NCDCFormat constructor comment.
 */
public NCDCFormat() {
	super();
	location = "                         ";
	meter = "          ";
	
}
/**
 * dataToString method comment.
 */
public String dataToString() {
	StringBuffer writeToFile = new StringBuffer();
	writeToFile.append(location);
	writeToFile.append(meterNumber);
	for (int x=0; x<12-meterNumber.length(); x++)
		writeToFile.append(" ");
	for (int x=0; x<7-kwhReading.length(); x++)
		writeToFile.append("0");
	writeToFile.append(kwhReading);
	for (int x=0; x<12-kwReading.length(); x++)
		writeToFile.append("0");
	writeToFile.append(kwReading);
	for (int x=0; x<12-kvarReading.length(); x++)
		writeToFile.append("0");
	writeToFile.append(kvarReading);
	writeToFile.append(readDate);
	writeToFile.append(readTime);
	writeToFile.append("   ");
	writeToFile.append("\r\n");
	return writeToFile.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/00 4:21:59 PM)
 */
public static final String[] getAllMeterNo(String databaseAlias, String collectionGroup) throws java.sql.SQLException {
	
	com.cannontech.database.SqlStatement stmt =
	//new com.cannontech.database.SqlStatement("SELECT NAME FROM DEVICE WHERE DEVICEID IN (SELECT DEVICEID FROM DEVICEMETERGROUP WHERE COLLECTIONGROUP = '" + collectionGroup + "')"
												//,databaseAlias );

	new com.cannontech.database.SqlStatement("SELECT PAONAME FROM YUKONPAOBJECT WHERE PAOBJECTID IN (SELECT DEVICEID FROM DEVICEMETERGROUP WHERE COLLECTIONGROUP = '" + collectionGroup + "')"
												,databaseAlias );

	try
	{
		stmt.execute();
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}

	String allMeterNo[] = new String[stmt.getRowCount()];

	for( int i = 0; i < stmt.getRowCount(); i++ )
	{
		allMeterNo[i] = (String)( stmt.getRow(i)[0]);
		
	 }
	
	return allMeterNo;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/00 4:21:59 PM)
 */
public static final String getReadDate(String databaseAlias, String deviceName) throws java.sql.SQLException {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT TO_CHAR(TIMESTAMP, 'YYYYMMDD') FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + deviceName + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1 OR POINTOFFSET = 3 OR POINTOFFSET = 5 OR POINTOFFSET = 7 OR POINTOFFSET = 9) ORDER BY TIMESTAMP DESC"
												,databaseAlias );
													
	String newReadDate = null;
	try
	{
		stmt.execute();
		if (stmt.getRowCount() > 0)
			newReadDate = new String( (String)( stmt.getRow(0)[0]));
		else
			newReadDate = new String("        ");
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}

	
	return newReadDate;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/00 4:21:59 PM)
 */
public static final String getReadDateMCT(String databaseAlias, String deviceName) throws java.sql.SQLException {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT TO_CHAR(TIMESTAMP, 'YYYYMMDD') FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Accumulator' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + deviceName + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1) ORDER BY TIMESTAMP DESC"
												,databaseAlias );
													
	String newReadDate = null;
	try
	{
		stmt.execute();
		if (stmt.getRowCount() > 0)
			newReadDate = new String( (String)( stmt.getRow(0)[0]));
		else
			newReadDate = new String("        ");
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	
	return newReadDate;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/00 4:21:59 PM)
 */
public static final String getReading(String databaseAlias, String deviceName, int pointOffset) throws java.sql.SQLException {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT VALUE FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + deviceName + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = " + pointOffset + ") AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + deviceName + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = " + pointOffset + "))"
												,databaseAlias );
	
	String newReading = null;
	try
	{
		stmt.execute();
		if (stmt.getRowCount() > 0)
			newReading = new String( ((java.math.BigDecimal)( stmt.getRow(0)[0])).toString());
		else
			newReading = new String("");
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}

	
	return newReading;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/00 4:21:59 PM)
 */
public static final String getReadingMCT(String databaseAlias, String deviceName, int pointOffset) throws java.sql.SQLException {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT VALUE FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Accumulator' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + deviceName + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = " + pointOffset + ") AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Accumulator' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + deviceName + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = " + pointOffset + "))"
												,databaseAlias );
	
	String newReading = null;
	try
	{
		stmt.execute();
		if (stmt.getRowCount() > 0)
			newReading = new String( ((java.math.BigDecimal)( stmt.getRow(0)[0])).toString());
		else
			newReading = new String("");
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}

	
	return newReading;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/00 4:21:59 PM)
 */
public static final String getReadTime(String databaseAlias, String deviceName) throws java.sql.SQLException {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT TO_CHAR(TIMESTAMP, 'HH24MMSS') FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Analog' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + deviceName + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1) ORDER BY TIMESTAMP DESC"
												,databaseAlias );
													
	String newReadDate = null;
	try
	{
		stmt.execute();
		if (stmt.getRowCount() > 0)
			newReadDate = new String( (String)( stmt.getRow(0)[0]));
		else
			newReadDate = new String("      ");
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}

	
	return newReadDate;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/00 4:21:59 PM)
 */
public static final String getReadTimeMCT(String databaseAlias, String deviceName) throws java.sql.SQLException {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT TO_CHAR(TIMESTAMP, 'HH24MMSS') FROM RAWPOINTHISTORY WHERE POINTID IN (SELECT POINTID FROM POINT WHERE POINTTYPE = 'Accumulator' AND DEVICEID = (SELECT DEVICEID FROM DEVICE WHERE NAME = '" + deviceName + "')) AND POINTID IN (SELECT POINTID FROM POINT WHERE POINTOFFSET = 1) ORDER BY TIMESTAMP DESC"
												,databaseAlias );
													
	String newReadDate = null;
	try
	{
		stmt.execute();
		if (stmt.getRowCount() > 0)
			newReadDate = new String( (String)( stmt.getRow(0)[0]));
		else
			newReadDate = new String("      ");
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	
	return newReadDate;
}
/**
 * insertValues method comment.
 */
public void insertValues(String databaseAlias, String collectionGroup, int itemNumber) throws java.sql.SQLException
{	
	/*String deviceType = new String();
	deviceType = getAllDeviceTypes(databaseAlias, collectionGroup)[itemNumber];
	meterNumber = getAllMeterNo(databaseAlias, collectionGroup)[itemNumber];

	if (deviceType.equals("MCT-360") || deviceType.equals("MCT-370")
	   || deviceType.equals("Alpha Meter") || deviceType.equals("Schlumberger Meter")
	   || deviceType.equals("Landis-Gyr S4")) 
	{
		int thisOffset = 1;
			kwhReading = getReading(databaseAlias, meterNumber, thisOffset);
			while (kwhReading.equals("") && thisOffset < 9)
			{
				thisOffset +=2;
				kwhReading = getReading(databaseAlias, meterNumber, thisOffset);
			}
		kwReading = getReading(databaseAlias, meterNumber, 2);
		kvarReading = getReading(databaseAlias, meterNumber, 12);
		readTime = getReadTime(databaseAlias, meterNumber);
		readDate = getReadDate(databaseAlias, meterNumber);
	} 
	else 
	{		
		kwhReading = getReadingMCT(databaseAlias, meterNumber, 1);
		kwReading = "";
		kvarReading = "";
		readTime = getReadTimeMCT(databaseAlias, meterNumber);
		readDate = getReadDateMCT(databaseAlias, meterNumber);
	}*/
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
