package com.cannontech.billing;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class MV_90Format extends FileFormatBase 
{
	java.sql.Connection dbConnection = null;
	java.sql.PreparedStatement prepStatement = null;

//	public static final String HEADER =
//		"H    Meter    Kwh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase\r\n";

//	public static final java.text.SimpleDateFormat DATE_FORMATTER = 
//				new java.text.SimpleDateFormat("yy/MM/dd");
//				
//	public static final java.text.SimpleDateFormat TIME_FORMATTER = 
//				new java.text.SimpleDateFormat("HH:mm");
//
//	public static final java.text.DecimalFormat NUMBER_FORMATTER = 
//		new java.text.DecimalFormat();

//	static
//	{
//		NUMBER_FORMATTER.applyPattern("#####");
//	}

/**
 * Default MV_90 constructor
 */
public MV_90Format() 
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2001 3:22:12 PM)
 */
/*
private void removeDuplicateMeters() 
{
	//we must remove items from the end of our Vector
	int itemPos = getRecordVector().size() - 1;
	int j = itemPos - 1;
	
	for( int i = itemPos; i >= 0; i--, j-- )
	{
		//be sure we are not at the last element in our Vector
		if( j >=0 )
		{
			if( getRecordVector().get(i).equals(getRecordVector().get(j)) )
			{
				if( ((com.cannontech.billing.record.MV_90Record)getRecordVector().get(i)).getReadingKWH().doubleValue() >=
					    ((com.cannontech.billing.record.MV_90Record)getRecordVector().get(j)).getReadingKWH().doubleValue() )
					 getRecordVector().remove(j);
				else
					getRecordVector().remove(i);
			}
		}
		
	}
	
}

/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveBillingData(String dbAlias)
{	
	long timer = System.currentTimeMillis();
	
	if( dbAlias == null)
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();

	String [] SELECT_COLUMNS =
	{
		SQLStringBuilder.DMG_METERNUMBER,
		SQLStringBuilder.DMG_DEVICEID,
		SQLStringBuilder.RPH_TIMESTAMP,
		SQLStringBuilder.PT_POINTID,
		SQLStringBuilder.PT_POINTOFFSET,
		SQLStringBuilder.RPH_VALUE,
	};

	String [] FROM_TABLES =
	{
		com.cannontech.database.db.point.RawPointHistory.TABLE_NAME,
		com.cannontech.database.db.point.Point.TABLE_NAME,
		com.cannontech.database.db.device.DeviceMeterGroup.TABLE_NAME
	};

	SQLStringBuilder builder = new SQLStringBuilder();
	String sql = new String((builder.buildSQLStatement(SELECT_COLUMNS, FROM_TABLES, getBillingDefaults(), null, null, validDemandAccOffsets)).toString());
		sql += " ORDER BY " 
			+ SQLStringBuilder.DMG_METERNUMBER + ", " 
			+ SQLStringBuilder.PT_POINTOFFSET + ", " 
			+ SQLStringBuilder.RPH_TIMESTAMP;

	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		if( conn == null )
		{
			com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
			return false;
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setTimestamp(1, new java.sql.Timestamp(getBillingDefaults().getEndDate().getTime()));
			rset = pstmt.executeQuery();

			com.cannontech.clientutils.CTILogger.info(" * Start looping through return resultset");
			
			int recCount = 0;
					
			int currentPointID = 0;
			java.sql.Timestamp lastTimeStamp = new java.sql.Timestamp(new Date().getTime() + 3600);
			int lastPointID = 0;
			int lastDeviceID = 0;
			int currentDeviceID = 0;
			String lastMeterNumber = "";

			while (rset.next())
			{
				currentPointID = rset.getInt(4);
				java.util.Vector readingVector = null;
				double multiplier = 1;
				if( getBillingDefaults().getRemoveMultiplier())
				{
					multiplier = ((Double)getPointIDMultiplierHashTable().get(new Integer(currentPointID))).doubleValue();
				}
				
				String meterNumber = rset.getString(1);
				currentDeviceID = rset.getInt(2);
				java.sql.Timestamp ts = rset.getTimestamp(3);
				int ptOffset = rset.getInt(5);
				double reading = rset.getDouble(6)	/ multiplier;
				Date tsDate = new Date(ts.getTime());
				
				inValidTimestamp:
				if( lastTimeStamp.compareTo((Object)ts) == 0)
				{
					if( isKW_demand(ptOffset) )
					{
						if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
							break inValidTimestamp;
							
						//** Get the last record and add to it the other pointOffsets' values. **//
						com.cannontech.billing.record.MV_90Record lastRecord =
							(com.cannontech.billing.record.MV_90Record)getRecordVector().get(recCount -1);

						lastRecord.getReadingKWVector().add(new Double(reading));
						lastRecord.setTimeKW(ts);
						lastRecord.setDateKW(ts);
					}
				}
				else
				{
					com.cannontech.billing.record.MV_90Record mv90Rec = 
						new com.cannontech.billing.record.MV_90Record(meterNumber);
					if (isKW_demand(ptOffset))
					{
						if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
							break inValidTimestamp;
						
						readingVector = new java.util.Vector(4);	//best guess capacity is 4
						readingVector.add(new Double(reading));
						mv90Rec.setReadingKWVector(readingVector);
						
						mv90Rec.setTimeKW(ts);
						mv90Rec.setDateKW(ts);
						
						if( !meterNumber.equalsIgnoreCase(lastMeterNumber))
							mv90Rec.setNewMeterNumber(true);
					
					}
					lastMeterNumber = meterNumber;
					lastDeviceID = currentDeviceID;
					getRecordVector().addElement(mv90Rec);
					recCount++;
				}
				lastTimeStamp = ts;
		
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
	com.cannontech.clientutils.CTILogger.info("@" +this.toString() +" Data Collection : Took " + (System.currentTimeMillis() - timer));
	return true;
}
}
