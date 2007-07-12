package com.cannontech.billing;

import java.util.Date;
import java.util.Vector;

import com.cannontech.billing.record.MV_90Record;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class MV_90Format extends FileFormatBase 
{
	java.sql.Connection dbConnection = null;
	java.sql.PreparedStatement prepStatement = null;

	/**
	 * Default MV_90 constructor
	 */
	public MV_90Format() 
	{
		super();
	}
	
	/**
	 * Retrieves values from the database and inserts them in a FileFormatBase object
	 * Creation date: (11/30/00)
	 */
	@Override
	public boolean retrieveBillingData()
	{	
		long timer = System.currentTimeMillis();
		
		String [] SELECT_COLUMNS =
		{
			SQLStringBuilder.DMG_METERNUMBER,
			SQLStringBuilder.DMG_DEVICEID,
			SQLStringBuilder.RPH_TIMESTAMP,
			SQLStringBuilder.PT_POINTID,
			SQLStringBuilder.PT_POINTOFFSET,
			SQLStringBuilder.RPH_VALUE
		};
	
		String [] FROM_TABLES =
		{
			com.cannontech.database.db.point.RawPointHistory.TABLE_NAME,
			com.cannontech.database.db.point.Point.TABLE_NAME,
		};
	
		SQLStringBuilder builder = new SQLStringBuilder();
		String sql = new String((builder.buildSQLStatement(SELECT_COLUMNS, FROM_TABLES, getBillingDefaults(), null, null, validProfileDemandAccOffsets)).toString());
			sql += " ORDER BY " 
				+ SQLStringBuilder.DMG_METERNUMBER + ", " 
				+ SQLStringBuilder.PT_POINTOFFSET + ", " 
				+ SQLStringBuilder.RPH_TIMESTAMP;
	
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				CTILogger.info(getClass() + ":  Error getting database connection.");
				return false;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp(getBillingDefaults().getDemandStartDate().getTime()));
				rset = pstmt.executeQuery();
				CTILogger.info(" * Start looping through return resultset");
				
				int recCount = 0;
				java.sql.Timestamp lastTimeStamp = new java.sql.Timestamp(new Date().getTime() + 3600);
				java.sql.Timestamp lpDemandRateTimeStamp = null;
					
				String lastMeterNumber = "";
				long lpDemandRate = 3600000;
				while (rset.next())
				{
					java.sql.Timestamp ts = rset.getTimestamp(3);
					Date tsDate = new Date(ts.getTime());
					
					if( tsDate.compareTo( getBillingDefaults().getEndDate()) <= 0) //ts <= maxtime, CONTINUE ON!
					{
						int pointID = rset.getInt(4);
						Vector <Double> readingVector = null;
						double multiplier = 1;
						if( getBillingDefaults().isRemoveMultiplier())
						{
							multiplier = ((Double)getPointIDMultiplierHashTable().get(new Integer(pointID))).doubleValue();
						}
						
						String meterNumber = rset.getString(1);
						int deviceID = rset.getInt(2);
//						int ptOffset = rset.getInt(5);
						double reading = rset.getDouble(6)	/ multiplier;
						
						inValidTimestamp:
						if( lastTimeStamp.compareTo(ts) == 0)
						{
							if( tsDate.compareTo( getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;
								
							//** Get the last record and add to it the other pointOffsets' values. **//
							MV_90Record lastRecord = (MV_90Record)getRecordVector().get(recCount -1);
	
							lastRecord.getReadingKWVector().add(new Double(reading));
							lastRecord.setTimeKW(ts);
							lastRecord.setDateKW(ts);
						}
						else
						{
							if( tsDate.compareTo( getBillingDefaults().getDemandStartDate()) <= 0) //ts <= mintime, fail!
								break inValidTimestamp;

							MV_90Record mv90Rec = new MV_90Record(meterNumber);

							//Check for a new meterNumber.  MV_90 records are grouped by meterNumber rather than deviceID
							if( !meterNumber.equalsIgnoreCase(lastMeterNumber))
							{
								mv90Rec.setNewMeterNumber(true);
								lpDemandRate = retrieveLoadProfileDemandRate(deviceID);
								lpDemandRateTimeStamp = new java.sql.Timestamp(getBillingDefaults().getDemandStartDate().getTime());
							}

							//Increment the interval that rows MUST occur at.
							lpDemandRateTimeStamp = new java.sql.Timestamp(lpDemandRateTimeStamp.getTime() + lpDemandRate);
							while (lpDemandRateTimeStamp.compareTo(ts) < 0)
							{
								//If intervalTimeStamp is less than ts, we must enter 'dummy' data into recordVector.
								if( lpDemandRateTimeStamp.compareTo(new java.sql.Timestamp(getBillingDefaults().getEndDate().getTime())) > 0) //ts <= maxtime, CONTINUE ON!
									break inValidTimestamp;

								readingVector = new Vector<Double>(4);	//best guess capacity is 4
								readingVector.add(new Double(-99999D));

								mv90Rec.setReadingKWVector(readingVector);
								mv90Rec.setTimeKW(lpDemandRateTimeStamp);
								mv90Rec.setDateKW(lpDemandRateTimeStamp);
								getRecordVector().addElement(mv90Rec);
								recCount++;
								
								lpDemandRateTimeStamp = new java.sql.Timestamp(lpDemandRateTimeStamp.getTime() + lpDemandRate);
								mv90Rec = new MV_90Record(meterNumber);
							}

							readingVector = new Vector <Double>(4);	//best guess capacity is 4
							readingVector.add(new Double(reading));
							mv90Rec.setReadingKWVector(readingVector);
							
							mv90Rec.setTimeKW(ts);
							mv90Rec.setDateKW(ts);
							
							lastMeterNumber = meterNumber;
							getRecordVector().addElement(mv90Rec);
							recCount++;
						}
						lastTimeStamp = ts;
					}
				}
			}
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error(e);
		}
		finally
		{
			try {
				if( rset != null ) rset.close();
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 ) {
				CTILogger.error(e2);
			}	
		}
		CTILogger.info("@" +this.toString() +" Data Collection : Took " + (System.currentTimeMillis() - timer));
		return true;
	}

	private int retrieveLoadProfileDemandRate(int deviceID) 
	{
//		long timer = System.currentTimeMillis();
		int demandRate = 3600000;	//defaults to 3600000 seconds (1 hour)
	
		StringBuffer sql = new StringBuffer	("SELECT LOADPROFILEDEMANDRATE FROM YUKONPAOBJECT PAO, DEVICELOADPROFILE DLP WHERE ");
		sql.append(" PAO.PAOBJECTID = " + deviceID);
		sql.append(" AND PAO.PAOBJECTID = DLP.DEVICEID");
			
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;

		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			if( conn == null ) {
				CTILogger.info(getClass() + ":  Error getting database connection.");
				return demandRate;
			}
			else {
				stmt = conn.prepareStatement(sql.toString());
				rset = stmt.executeQuery();
				
				while( rset.next()) {
					if( rset.getInt(1) > 0  )
						demandRate = rset.getInt(1) * 1000;
				}
			}
		}
				
		catch( java.sql.SQLException e ) {
			CTILogger.error(e);
		}
		finally
		{
			SqlUtils.close(rset, stmt, conn );
		}
		return demandRate;
	}
}
