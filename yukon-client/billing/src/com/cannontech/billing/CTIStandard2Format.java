package com.cannontech.billing;

/**
 * Insert the type's description here.
 * Creation date: (6/28/00 10:47:08 AM)
 * @author: 
 */
public class CTIStandard2Format extends FileFormatBase
{
	private String meterNumber;
	private String pointName;
	private String reading;
	private String readDate;
	private String readTime;
	private String quality;
/**
 * CTIStandard2Format constructor comment.
 */
public CTIStandard2Format() {
	super();
}
/**
 * dataToString method comment.
 */
public String dataToString() {
	StringBuffer writeToFile = new StringBuffer();
	writeToFile.append(meterNumber);
	for (int x=0; x<8-meterNumber.length(); x++)
		writeToFile.append(" ");
	writeToFile.append(",");
	writeToFile.append(pointName);
	for (int x=0; x<20-pointName.length(); x++)
		writeToFile.append(" ");
	writeToFile.append(",");
	for (int x=0; x<10-reading.length(); x++)
		writeToFile.append(" ");
	writeToFile.append(reading);
	writeToFile.append(",");
	writeToFile.append(readDate);
	writeToFile.append(",");
	writeToFile.append(readTime);
	writeToFile.append(",");
	writeToFile.append(quality);
	writeToFile.append("\r\n");
	return writeToFile.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/00 11:01:54 AM)
 * @return java.lang.String[]
 * @param databasealias java.lang.String
 * @param collectionGroup java.lang.String
 */
public final static String[] getAllPointID(String databaseAlias, String collectionGroup) {

	com.cannontech.database.SqlStatement stmt =
	//new com.cannontech.database.SqlStatement("SELECT POINTID FROM POINT WHERE DEVICEID IN (SELECT DEVICEID FROM DEVICEMETERGROUP WHERE COLLECTIONGROUP = '" + collectionGroup + "')"
												//,databaseAlias );

	new com.cannontech.database.SqlStatement("SELECT POINTID FROM POINT WHERE PAOBJECTID IN (SELECT DEVICEID FROM DEVICEMETERGROUP WHERE COLLECTIONGROUP = '" + collectionGroup + "')"
											,databaseAlias );

	try
	{
		stmt.execute();
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}

	String allPointID[] = new String[stmt.getRowCount()];

	for( int i = 0; i < stmt.getRowCount(); i++ )
	{
		allPointID[i] = (stmt.getRow(i)[0]).toString();
		
	}
	
	return allPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/00 11:23:24 AM)
 * @return java.lang.String
 * @param databaseAlias java.lang.String
 * @param pointID java.lang.String
 */
public final String getMeterNumber(String databaseAlias, String pointID) {
	
	com.cannontech.database.SqlStatement stmt =
	//new com.cannontech.database.SqlStatement("SELECT NAME FROM DEVICE WHERE DEVICEID = (SELECT DEVICEID FROM POINT WHERE POINTID = " + pointID + ")"
											//,databaseAlias );
	new com.cannontech.database.SqlStatement("SELECT PAONAME FROM YUKONPAOBJECT WHERE PAOBJECTID = (SELECT DEVICEID FROM POINT WHERE POINTID = " + pointID + ")"
											,databaseAlias );

												
	String newMeterNumber = null;	
	try
	{
		stmt.execute();
		if (stmt.getRowCount() > 0)
			newMeterNumber = new String( (String)( stmt.getRow(0)[0]));
		else
			newMeterNumber = new String("");
		
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}

	
	return newMeterNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/00 11:26:56 AM)
 * @return java.lang.String
 * @param databaseAlias java.lang.String
 * @param pointID java.lang.String
 */
public final String getPointName(String databaseAlias, String pointID) {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT POINTNAME FROM POINT WHERE POINTID = " + pointID + ""
												,databaseAlias );
	
	String newPointName = null;	
	try
	{
		stmt.execute();
		if (stmt.getRowCount() > 0)
			newPointName = new String( (String)( stmt.getRow(0)[0]));
		else
			newPointName = new String("");
		
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}

	
	return newPointName;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/00 11:16:28 AM)
 * @return java.lang.String
 * @param databaseAlias java.lang.String
 * @param pointID java.lang.String
 */
public final String getReadDate(String databaseAlias, String pointID) {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT TO_CHAR(TIMESTAMP, 'MM/DD/YY') FROM RAWPOINTHISTORY WHERE POINTID = " + pointID + " AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY WHERE POINTID = " + pointID + ")"
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
 * Creation date: (6/28/00 11:12:44 AM)
 * @return java.lang.String
 * @param databaseAlias java.lang.String
 * @param pointID java.lang.String
 */
public final String getReading(String databaseAlias, String pointID) {

	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT VALUE FROM RAWPOINTHISTORY WHERE POINTID = " + pointID + " AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY WHERE POINTID = " + pointID + ")"
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
 * Creation date: (6/28/00 11:16:28 AM)
 * @return java.lang.String
 * @param databaseAlias java.lang.String
 * @param pointID java.lang.String
 */
public final String getReadTime(String databaseAlias, String pointID) {
	
	com.cannontech.database.SqlStatement stmt =
	new com.cannontech.database.SqlStatement("SELECT TO_CHAR(TIMESTAMP, 'HH24:MM') FROM RAWPOINTHISTORY WHERE POINTID = " + pointID + " AND TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM RAWPOINTHISTORY WHERE POINTID = " + pointID + ")"
												,databaseAlias );
	
	String newReadTime = null;	
	try
	{
		stmt.execute();
		if (stmt.getRowCount() > 0)
			newReadTime = new String( (String)( stmt.getRow(0)[0]));
		else
			newReadTime = new String("     ");
		
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}

	
	return newReadTime;
}
/**
 * insertValues method comment.
 */
public void insertValues(String databaseAlias, String collectionGroup, int itemNumber) throws java.sql.SQLException {
	
	String pointID = new String(getAllPointID(databaseAlias, collectionGroup)[itemNumber]);
	meterNumber = getMeterNumber(databaseAlias, pointID);
	reading = getReading(databaseAlias, pointID);
	readDate = getReadDate(databaseAlias, pointID);
	readTime = getReadTime(databaseAlias, pointID);
	pointName = getPointName(databaseAlias, pointID);
	if (!reading.equals(""))
		quality = "N  ";
	else 
		quality = "F  ";
	
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
}
