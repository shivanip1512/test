package com.cannontech.billing;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class SEDCFormat extends FileFormatBase 
{
	java.sql.Connection dbConnection = null;
	java.sql.PreparedStatement prepStatement = null;

	public static final String HEADER =
		"H    Meter    Kwh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase\r\n";

	public static final java.text.SimpleDateFormat DATE_FORMATTER = 
				new java.text.SimpleDateFormat("yy/MM/dd");
				
	public static final java.text.SimpleDateFormat TIME_FORMATTER = 
				new java.text.SimpleDateFormat("HH:mm");

	public static final java.text.DecimalFormat NUMBER_FORMATTER = 
		new java.text.DecimalFormat();

	static
	{
		NUMBER_FORMATTER.applyPattern("#####");
	}

/**
 * Default SEDC constructor
 */
public SEDCFormat() 
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/2001 3:22:12 PM)
 */
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
				if( ((com.cannontech.billing.record.SEDCRecord)getRecordVector().get(i)).getReadingKWH().doubleValue() >=
					    ((com.cannontech.billing.record.SEDCRecord)getRecordVector().get(j)).getReadingKWH().doubleValue() )
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
		SQLStringBuilder.PT_POINTID, 
		SQLStringBuilder.RPH_TIMESTAMP,
		SQLStringBuilder.RPH_VALUE,
		SQLStringBuilder.DMG_DEVICEID,
		SQLStringBuilder.PT_POINTOFFSET
	};

	String [] FROM_TABLES =
	{
		com.cannontech.database.db.point.RawPointHistory.TABLE_NAME,
		com.cannontech.database.db.point.Point.TABLE_NAME,
		com.cannontech.database.db.device.DeviceMeterGroup.TABLE_NAME
	};

	SQLStringBuilder builder = new SQLStringBuilder();
	String sql = new String((builder.buildSQLStatement(SELECT_COLUMNS, FROM_TABLES, getBillingDefaults(), validAnalogPtOffsets, validAccPtOffsets)).toString());
		sql += " ORDER BY " 
			+ SQLStringBuilder.DMG_METERNUMBER + ", " 
			+ SQLStringBuilder.DMG_DEVICEID + ", " 
			+ SQLStringBuilder.PT_POINTOFFSET + ", " 
			+ SQLStringBuilder.RPH_TIMESTAMP + " DESC ";
		
/*		
	StringBuffer sql = new StringBuffer("SELECT DMG.METERNUMBER, PT.POINTID, RPH.TIMESTAMP, RPH.VALUE, DMG.DEVICEID, PT.POINTOFFSET " +
				" FROM POINT PT, RAWPOINTHISTORY RPH, DEVICEMETERGROUP DMG " +
				" WHERE PT.PAOBJECTID = DMG.DEVICEID " +
				" AND RPH.TIMESTAMP < ? " + 
				" AND DMG.COLLECTIONGROUP IN ('" + collectionGroups.get(0) + "'");
				for (int i = 1; i < collectionGroups.size(); i++)
				{
					sql.append( ", '" + collectionGroups.get(i) + "'");
				}

				sql.append(") AND PT.POINTID = RPH.POINTID " +
				" AND ( (POINTTYPE = 'Analog' AND POINTOFFSET IN (" + validAnalogPtOffsets[0]);
				for (int i = 1; i < validAnalogPtOffsets.length; i++)
				{
					sql.append( ", " + validAnalogPtOffsets[i]);
				}

				sql.append("))  OR  (POINTTYPE = 'PulseAccumulator'  AND POINTOFFSET IN (" + validAccPtOffsets[0]);
				for (int i = 1; i < validAccPtOffsets.length; i++)
				{
					sql.append(", " + validAccPtOffsets[i]);
				}
				sql.append( ") )) ORDER BY DMG.METERNUMBER, DMG.DEVICEID, PT.POINTOFFSET, RPH.TIMESTAMP DESC");
*/

	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		if( conn == null )
		{
			System.out.println(getClass() + ":  Error getting database connection.");
			return false;
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setTimestamp(1, new java.sql.Timestamp(getBillingDefaults().getEndDate().getTime()));
			rset = pstmt.executeQuery();

			System.out.println(" * Start looping through return resultset");
			
			int recCount = 0;
			
			if( !isAppending() )
			{
				getRecordVector().add( new com.cannontech.billing.record.StringRecord(HEADER) );
				recCount ++;
			}

			
			int currentPointID = 0;
			int lastPointID = 0;
			int lastDeviceID = 0;
			int currentDeviceID = 0;
				
			while (rset.next())
			{
				currentPointID = rset.getInt(2);

				double multiplier = 1;
				if( getBillingDefaults().getRemoveMultiplier())
				{
					multiplier = ((Double)getPointIDMultiplierHashTable().get(new Integer(currentPointID))).doubleValue();
				}
				
				if( currentPointID != lastPointID )	//just getting max time for each point
				{
					lastPointID = currentPointID;

					String meterNumber = rset.getString(1);
					java.sql.Timestamp ts = rset.getTimestamp(3);
					double reading = rset.getDouble(4)	/ multiplier;
					currentDeviceID = rset.getInt(5);
					int ptOffset = rset.getInt(6);
					Date tsDate = new Date(ts.getTime());
					
					inValidTimestamp:
					if( currentDeviceID == lastDeviceID)
					{
						if (ptOffset == 1 || isKWH(ptOffset))
						{
							if( tsDate.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
								
							//** Get the last record and add to it the other pointOffsets' values. **//
							com.cannontech.billing.record.SEDCRecord lastRecord =
								(com.cannontech.billing.record.SEDCRecord)getRecordVector().get(recCount -1);

							lastRecord.setReadingKWH(reading);
							lastRecord.setTime(ts);
							lastRecord.setDate(ts);
						}
						else if (isKW(ptOffset))
						{
							if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
								
							//** Get the last record and add to it the other pointOffsets' values. **//
							com.cannontech.billing.record.SEDCRecord lastRecord =
								(com.cannontech.billing.record.SEDCRecord)getRecordVector().get(recCount -1);

							lastRecord.setReadingKW(reading);
							lastRecord.setTimeKW(ts);
							lastRecord.setDateKW(ts);
						}												
					}
					else
					{
						com.cannontech.billing.record.SEDCRecord sedcRec = 
							new com.cannontech.billing.record.SEDCRecord(meterNumber);
						if (ptOffset == 1 || isKWH(ptOffset))
						{
							if( tsDate.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
							
							sedcRec.setReadingKWH(reading);
							sedcRec.setTime(ts);
							sedcRec.setDate(ts);

						}
						else if (isKW(ptOffset))
						{
							if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;

							sedcRec.setReadingKW(reading);
							sedcRec.setTimeKW(ts);
							sedcRec.setDateKW(ts);
						}
						lastDeviceID = currentDeviceID;
						getRecordVector().addElement(sedcRec);
						recCount++;
					}
				}
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
	System.out.println(" @SEDC Data Collection : Took " + (System.currentTimeMillis() - timer));
	return true;
}
}
