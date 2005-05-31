package com.cannontech.billing;

import java.util.Date;

import com.cannontech.billing.record.NISC_TOU_kVarHRecord;
import com.cannontech.billing.record.TurtleRecordBase;


/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class NISC_TOU_kVarHFormat extends TurtleFormatBase
{
	private static final String HEADER = "H    Meter   TOU kWh  Time   Date      Peak   PeakT   PeakD\r\n";
	/**
	 * Default NISC constructor
	 */
	public NISC_TOU_kVarHFormat() 
	{
		super();
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
					if( tsDate.compareTo( (Object)getBillingDefaults().getEndDate()) <= 0) //ts <= maxtime, pass!
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
									if( tsDate.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
										break inValidTimestamp;
										
									//** Get the last record and add to it the other pointOffsets' values. **//
									NISC_TOU_kVarHRecord lastRecord = (NISC_TOU_kVarHRecord)getRecord(recCount -1);
		
									lastRecord.setReadingKWH(reading);
									lastRecord.setTime(ts);
									lastRecord.setDate(ts);
								}
								else if (isKW(ptOffset))
								{
									if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
										break inValidTimestamp;
										
									//** Get the last record and add to it the other pointOffsets' values. **//
									NISC_TOU_kVarHRecord lastRecord = (NISC_TOU_kVarHRecord)getRecord(recCount -1);
		
									lastRecord.getPeakDemandReadingsVector().add(new Double(reading));
									lastRecord.getPeakDemandTimestampVector().add(ts);
								}
								else if (isKVARH(ptOffset))
								{
									if( tsDate.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
										break inValidTimestamp;
										
									//** Get the last record and add to it the other pointOffsets' values. **//
									NISC_TOU_kVarHRecord lastRecord = (NISC_TOU_kVarHRecord)getRecord(recCount -1);
		
									lastRecord.setReadingKvarH(reading);
								}												
							}
							else
							{
								NISC_TOU_kVarHRecord record = (NISC_TOU_kVarHRecord)createRecord(meterNumber);
									
								if (ptOffset == 1 || isKWH(ptOffset))
								{
									if( tsDate.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
										break inValidTimestamp;
									
									record.setReadingKWH(reading);
									record.setTime(ts);
									record.setDate(ts);
		
								}
								else if (isKW(ptOffset))
								{
									if( tsDate.compareTo( (Object)getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
										break inValidTimestamp;
		
									record.getPeakDemandReadingsVector().add(new Double(reading));
									record.getPeakDemandTimestampVector().add(ts);
								}
								else if ( isKVARH(ptOffset))
								{
									if( tsDate.compareTo( (Object)getBillingDefaults().getEnergyStartDate()) <= 0) //ts <= mintime, fail!
										break inValidTimestamp;
									record.setReadingKvarH(reading);
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
	public TurtleRecordBase createRecord(String meterNumber)
	{
		return new NISC_TOU_kVarHRecord(meterNumber);
	}
	public TurtleRecordBase getRecord(int index)
	{
		return (NISC_TOU_kVarHRecord)getRecordVector().get(index);
	}
	public String getHeader()
	{
		return HEADER;
	}	
}
