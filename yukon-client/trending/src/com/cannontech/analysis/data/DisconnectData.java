package com.cannontech.analysis.data;

import java.sql.ResultSet;
import com.cannontech.analysis.data.ColumnProperties;
import com.cannontech.analysis.data.ReportDataBase;
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
public class DisconnectData extends ReportDataBase
{
	/** Class fields */
	/** Start time for query in millis */
	private long startTime = Long.MIN_VALUE;
	/** Stop time for query in millis */
	private long stopTime = Long.MIN_VALUE;

	/** Number of columns */
	protected final int NUMBER_COLUMNS = 4;

	/** Enum values for column representation */
	public final static int COLL_GROUP_NAME_COLUMN = 0;
	public final static int DEVICE_NAME_COLUMN = 1;
	public final static int POINT_NAME_COLUMN = 2;
	public final static int POINT_VALUE_COLUMN = 3;

	/** String values for column representation */
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String POINT_VALUE_STRING = "Point ID";

	/** Inner class container of table model data*/
	private class disconnect
	{
		public String collGroup = null;
		public String deviceName = null;
		public String pointName = null;
		public Integer pointValue = null;
		public disconnect(String collGroup_, String deviceName_, String pointName_, Integer pointValue_)
		{
			collGroup = collGroup_;
			deviceName = deviceName_;
			pointName = pointName_;
			pointValue = pointValue_;			
		}
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
			Integer pointValue = new Integer(rset.getInt(4));
			disconnect disconnect = new disconnect(collGrp, paoName, pointName, pointValue);

			data.add(disconnect);
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
		StringBuffer sql = new StringBuffer	("SELECT PAO.PAONAME, P.POINTNAME, P.POINTID" + 
			" RPH.VALUE FROM YUKONPAOBJECT PAO, POINT P, RAWPOINTHISTORY RPH "+
			" P.PAOBJECTID = PAO.PAOBJECTID" +
			" P.POINTID = RPH.POINTID " );
			
			//"AND P.POINTOFFSET = 20 AND RPH.TIMESTAMP > ? " + 
			//" AND RPH.TIMESTAMP < ?" );
		return sql;
	}
	

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[NUMBER_COLUMNS];
			columnNames[0] = COLL_GROUP_NAME_STRING;
			columnNames[1] = DEVICE_NAME_STRING;
			columnNames[2] = POINT_NAME_STRING;
			columnNames[3] = POINT_VALUE_STRING;
		}
		return columnNames;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[NUMBER_COLUMNS];
			columnTypes[0] = String.class;
			columnTypes[1] = String.class;
			columnTypes[2] = String.class;
			columnTypes[3] = Integer.class;
		}
		return columnTypes;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[NUMBER_COLUMNS];
			//posX, posY, width, height, numberFormatString
			columnProperties[0] = new ColumnProperties(0, 1, 100, 18, null);
			columnProperties[1] = new ColumnProperties(100, 1, 100, 18, null);
			columnProperties[2] = new ColumnProperties(200, 1, 100, 18, null);
			columnProperties[3] = new ColumnProperties(300, 1, 100, 18, null);
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#collectData()
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
	 * @see com.cannontech.analysis.data.ReportDataBase#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof disconnect)
		{
			disconnect meter = ((disconnect)o); 
			switch( columnIndex)
			{
				case COLL_GROUP_NAME_COLUMN:
					return meter.collGroup;
		
				case DEVICE_NAME_COLUMN:
					return meter.deviceName;
	
				case POINT_NAME_COLUMN:
					return meter.pointName;
	
				case POINT_VALUE_COLUMN:
					return meter.pointValue;
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getTitleString()
	 */
	public String getTitleString()
	{
		return "Disconnect Report";
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getDateRangeString()
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
