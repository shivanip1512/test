package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.device.MissedMeter;

/**
 * Created on Dec 15, 2003
 * MissedMeterData TableModel object
 * Innerclass object for row data is MissedMeter:
 *  String collGroup	- DeviceMeterGroup.collectionGroup 
 *  String deviceName	- YukonPaobject.paoName
 *  String pointName	- Point.pointName
 *  Integer pointID		- Point.pointID  
 * @author snebben
 */
public class MissedMeterModel extends ReportModelBase
{
	/** Class fields */
	/** Start time for query in millis */
	private long startTime = Long.MIN_VALUE;
	/** Stop time for query in millis */
	private long stopTime = Long.MIN_VALUE;

	/**
	 * 
	 */
	public MissedMeterModel()
	{
		this(ReportTypes.MISSED_METER_DATA);
	}
	
	/**
	 * 
	 */
	public MissedMeterModel(int reportType_)
	{
		super();
		setReportType(ReportTypes.MISSED_METER_DATA);
	}
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			String collGrp = rset.getString(1);
			String paoName = rset.getString(2);
			String pointName = rset.getString(3);					
			Integer pointID = new Integer(rset.getInt(4));
			MissedMeter missedMeter = new MissedMeter(collGrp, paoName, pointName, pointID);

			data.add(missedMeter);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve MissedMeter data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT DMG.COLLECTIONGROUP, PAO.PAONAME, P.POINTNAME, P.POINTID " + 
			" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, POINT P "+
			" WHERE PAO.PAOBJECTID = DMG.DEVICEID "+
			" AND P.PAOBJECTID = PAO.PAOBJECTID " + 
			" AND P.POINTID NOT IN (SELECT DISTINCT POINTID FROM RAWPOINTHISTORY WHERE TIMESTAMP > ?)" +
			" ORDER BY DMG.COLLECTIONGROUP, PAO.PAONAME, P.POINTNAME");
		return sql;
	}
	



	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		int rowCount = 0;
		
		StringBuffer sql = buildSQLStatement();
	
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

			if( conn == null )
			{
				com.cannontech.clientutils.CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime() ));
				com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()));
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					addDataRow(rset);
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
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}
		com.cannontech.clientutils.CTILogger.info("Report Records Collected from Database: " + data.size());
		return;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");		
		return format.format(new java.util.Date(getStartTime()));
	}
	/**
	 * Returns the startTime in millis
	 * @return long startTime
	 */
	public long getStartTime()
	{
		if( startTime < 0 )
		{
			java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
			tempCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
			tempCal.set(java.util.Calendar.MINUTE, 0);
			tempCal.set(java.util.Calendar.SECOND, 0);
			tempCal.set(java.util.Calendar.MILLISECOND, 0);
			tempCal.add(java.util.Calendar.DATE, -1);
			startTime = tempCal.getTime().getTime();				
		}
		return startTime;
	}

	/**
	 * Returns the stopTime in millis
	 * @return long stopTime
	 */
	public long getStopTime()
	{
		if( stopTime < 0 )
		{
			java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
			tempCal.setTimeInMillis(getStartTime());
			tempCal.add(java.util.Calendar.DATE, 1);
			stopTime = tempCal.getTime().getTime();				
		}
		return stopTime;
	}

	/**
	 * Set the startTime in millis
	 * @param long startTime_
	 */
	public void setStartTime(long startTime_)
	{
		startTime = startTime_;
	}

	/**
	 * Set the stopTime in millis
	 * @param long stopTime_
	 */
	public void setStopTime(long stopTime_)
	{
		stopTime = stopTime_;
	}
}
