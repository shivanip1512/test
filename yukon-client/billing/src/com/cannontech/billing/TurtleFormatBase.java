package com.cannontech.billing;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class TurtleFormatBase extends FileFormatBase 
{
	public String header = "H    Meter    kWh   Time   Date    Peak   PeakT   PeakD  Stat Sig  Freq Phase\r\n";

	/**
	 * Default Turtle (default format) constructor
	 */
	public TurtleFormatBase() 
	{
		super();
	}
	
	/**
	 * Retrieves values from the database and inserts them in a FileFormatBase object
	 * 
	 * WARNING:  We could have some issues if multiple points are collecting data, the last point read in will be the ONLY 
	 * one to show up in the file, since we only have one place holder. 
	 * For Example:  The peak demand point offsets (11) are all parsed into the same variable, therefore the last
	 * one read in will be the only one in the file. (SN 20051010)
	 * 2006-03-15 removed frozen peak demand (21-24)offsets per Matt.  These no longer exist and are all dumped to offset 11 for 410.
	 * 
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
		String sql = new String((builder.buildSQLStatement(SELECT_COLUMNS, FROM_TABLES, getBillingDefaults(), validAnalogPtOffsets, validAccPtOffsets, validPeakDemandAccOffsets)).toString());
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
				com.cannontech.clientutils.CTILogger.info(getClass() + ":  Error getting database connection.");
				return false;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				
				pstmt.setTimestamp(1, new java.sql.Timestamp(getBillingDefaults().getEarliestStartDate().getTime()));
				rset = pstmt.executeQuery();
	
				com.cannontech.clientutils.CTILogger.info(" * Start looping through return resultset");
				
				int recCount = 0;
				
				if( !getBillingDefaults().isAppendToFile() )
				{
					getRecordVector().add( new com.cannontech.billing.record.StringRecord(getHeader()));
					recCount ++;
				}
	
				int currentPointID = 0;
				int lastPointID = 0;
				int lastDeviceID = 0;
				int currentDeviceID = 0;
					
				while (rset.next())
				{
					java.sql.Timestamp ts = rset.getTimestamp(3);
					Date tsDate = new Date(ts.getTime());
					if( tsDate.compareTo( getBillingDefaults().getEndDate()) <= 0) //ts <= maxtime, pass!
					{
						currentPointID = rset.getInt(2);
						double multiplier = 1;
						if( getBillingDefaults().isRemoveMultiplier())
						{
							multiplier = ((Double)getPointIDMultiplierHashTable().get(new Integer(currentPointID))).doubleValue();
						}
						
						if( currentPointID != lastPointID )	//just getting max time for each point
						{
							lastPointID = currentPointID;
							String meterNumber = rset.getString(1);
							double reading = rset.getDouble(4)	/ multiplier;
							currentDeviceID = rset.getInt(5);
							int ptOffset = rset.getInt(6);
	
							inValidTimestamp:
							if( currentDeviceID == lastDeviceID)
							{
								if (ptOffset == 1 || isKWH(ptOffset))
								{
									if( tsDate.compareTo( getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
										break inValidTimestamp;
										
									//** Get the last record and add to it the other pointOffsets' values. **//
									com.cannontech.billing.record.TurtleRecordBase lastRecord = getRecord(recCount -1);
		
									lastRecord.setReadingKWH(reading);
									lastRecord.setTime(ts);
									lastRecord.setDate(ts);
								}
								else if (isKW(ptOffset) || isKW_peakDemand(ptOffset))
								{
									if( tsDate.compareTo( getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
										break inValidTimestamp;
										
									//** Get the last record and add to it the other pointOffsets' values. **//
									com.cannontech.billing.record.TurtleRecordBase lastRecord = getRecord(recCount -1);
		
									lastRecord.setReadingKW(reading);
									lastRecord.setTimeKW(ts);
									lastRecord.setDateKW(ts);
								}												
							}
							else
							{
								com.cannontech.billing.record.TurtleRecordBase record = createRecord(meterNumber);
									
								if (ptOffset == 1 || isKWH(ptOffset))
								{
									if( tsDate.compareTo( getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
										break inValidTimestamp;
									
									record.setReadingKWH(reading);
									record.setTime(ts);
									record.setDate(ts);
		
								}
								else if (isKW(ptOffset) || isKW_peakDemand(ptOffset))
								{
									if( tsDate.compareTo( getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
										break inValidTimestamp;
		
									record.setReadingKW(reading);
									record.setTimeKW(ts);
									record.setDateKW(ts);
								}
								lastDeviceID = currentDeviceID;
								getRecordVector().addElement(record);
								recCount++;
							}
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
		com.cannontech.clientutils.CTILogger.info("@" +this.toString() +" Data Collection : Took " + (System.currentTimeMillis() - timer));
		return true;
	}
	
	public com.cannontech.billing.record.TurtleRecordBase createRecord(String meterNumber)
	{
		return new com.cannontech.billing.record.TurtleRecordBase(meterNumber);
	}
	public com.cannontech.billing.record.TurtleRecordBase getRecord(int index)
	{
		return (com.cannontech.billing.record.TurtleRecordBase)getRecordVector().get(index);
	}
	public String getHeader()
	{
		return header;
	}
}
