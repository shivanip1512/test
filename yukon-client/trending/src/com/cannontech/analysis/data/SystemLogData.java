package com.cannontech.analysis.data;

import java.sql.ResultSet;

import com.cannontech.analysis.data.ColumnProperties;
import com.cannontech.analysis.data.ReportDataBase;

/**
 * Created on Dec 15, 2003
 * SystemLogData TableModel object
 * Innerclass object for row data is SystemLog: 
 *  java.util.Date dateTime	- SystemLog.dateTime
 *  Integer pointID 		- SystemLog.pointID
 *  Integer priority 		- SystemLog.priority
 *  String action 			- SystemLog.action
 *  String description 		- SystemLog.description
 *  String userName 		- SystemLog.userName
 * @author snebben
 */
public class SystemLogData extends ReportDataBase
{
	/** Class fields */
	/** Flag indicating data to be ordered ASC or DESC in tableModel*/
	private boolean orderDescending = false;
	/** Start time for query in millis */
	private long startTime = Long.MIN_VALUE;
	/** Stop time for query in millis */
	private long stopTime = Long.MIN_VALUE;
	
	/**
	 * Type int from com.cannontech.database.db.point.SystemLog.type
	 * Allows for reporting by type, null value results in all types.
	 */
	private Integer logType = null;
	
	/**
	 * PointId int from com.cannontech.database.db.point.SystemLog.pointID
	 * Allows for reporting by a pointid, null value results in all points.
	 */	
	private Integer pointID = null;
	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;
	
	/** Enum values for column representation */
	public final static int DATE_COLUMN = 0;
	public final static int TIME_COLUMN = 1;
	public final static int POINT_ID_COLUMN = 2;
	public final static int PRIORITY_COLUMN = 3;
	public final static int ACTION_COLUMN = 4;
	public final static int DESCRIPTION_COLUMN = 5;	
	public final static int USERNAME_COLUMN = 6;

	/** String values for column representation */
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
	public final static String POINT_ID_STRING = "PointID";
	public final static String PRIORITY_STRING = "Priority";
	public final static String ACTION_STRING = "Action";
	public final static String DESCRIPTION_STRING = "Description";
	public final static String USERNAME_STRING = "UserName";
	
	/** Inner class container of table model data*/
	protected class SystemLog
	{
		public java.util.Date dateTime = null;
		public Integer pointID;
		public Integer priority;
		public String action = null;
		public String description = null;
		public String userName = null;
		
		public SystemLog(java.util.Date dateTime_, Integer pointID_, Integer priority_,
						String action_, String description_, String userName_)
		{
			dateTime = dateTime_;
			pointID = pointID_;
			priority = priority_;
			action = action_;
			description = description_;
			userName = userName_;
		}
	}
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 */
	public SystemLogData(long startTime_, long stopTime_)
	{
		super();
		setStartTime(startTime_);
		setStopTime(stopTime_);		
	}	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public SystemLogData(long startTime_, long stopTime_, Integer logType_)
	{
		super();
		setStartTime(startTime_);
		setStopTime(stopTime_);
		setLogType(logType_);
	}
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param pointID_ SYSTEM.pointID
	 * @param logType_ SYSTEMLOG.type
	 */
	public SystemLogData(long startTime_, long stopTime_, Integer logType_, Integer pointID_)
	{
		super();
		setStartTime(startTime_);
		setStopTime(stopTime_);
		setLogType(logType_);
		setPointID(pointID_);
	}
	/**
	 * Add SystemLog objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			java.sql.Timestamp dateTime = rset.getTimestamp(1);
			Integer pointID = new Integer(rset.getInt(2));
			Integer priority = new Integer(rset.getInt(3));
			String action = rset.getString(4);
			String description = rset.getString(5);
			String userName = rset.getString(6);
						
			SystemLog systemLog = new SystemLog(new java.util.Date(dateTime.getTime()),
								pointID, priority, action, description, userName);
 
			data.add(systemLog);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve SystemLog data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT DATETIME, POINTID, PRIORITY, ACTION, DESCRIPTION, USERNAME "+
				" FROM SYSTEMLOG "+
				" WHERE (DATETIME > ?) AND (DATETIME <= ?)");
				if( getLogType() != null)
					sql.append(" AND TYPE = " + getLogType().intValue());
				if( getPointID() != null)
					sql.append(" AND POINTID = " + getPointID().intValue());
				sql.append(" ORDER BY DATETIME " + getOrderByString());
		return sql;
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
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime()));
				com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()) + "  -  STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
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

	/**
	 * Returns orderByDirection
	 * @return boolean true if descending order desired
	 */
	private boolean getOrderDescending()
	{
		return orderDescending;
	}
	/**
	 * Returns SQL string value of order by direction.
	 * @return String ASC | DESC
	 */
	private String getOrderByString()
	{
		if( orderDescending )
			return "DESC";
		return "ASC";
	}
	/**
	 * Return the order by direction for the SQLStatement
	 * @param bollean orderDescending
	 */
	public void setOrderDescending(boolean orderDesc_)
	{
		orderDescending = orderDesc_;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[NUMBER_COLUMNS];
			columnNames[0] = DATE_STRING;
			columnNames[1] = TIME_STRING;
			columnNames[2] = POINT_ID_STRING;
			columnNames[3] = PRIORITY_STRING;
			columnNames[4] = ACTION_STRING;
			columnNames[5] = DESCRIPTION_STRING;
			columnNames[6] = USERNAME_STRING;
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
			columnTypes[0] = java.util.Date.class;
			columnTypes[1] = java.util.Date.class;
			columnTypes[2] = Integer.class;
			columnTypes[3] = Integer.class;
			columnTypes[4] = String.class;
			columnTypes[5] = String.class;
			columnTypes[6] = String.class;
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
			columnProperties[0] = new ColumnProperties(100, 1, 100, 18, "MMMMM dd, yyyy");
			columnProperties[1] = new ColumnProperties(0, 1, 50, 18, "hh:mm:ss");
			columnProperties[2] = new ColumnProperties(50, 1, 50, 18, null);
			columnProperties[3] = new ColumnProperties(100, 1, 50, 18, null);
			columnProperties[4] = new ColumnProperties(150, 1, 200, 18, null);
			columnProperties[5] = new ColumnProperties(350, 1, 200, 18, null);
			columnProperties[6] = new ColumnProperties(550, 1, 65, 18, null);
			
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.StatisticReportDataBase#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof SystemLog)
		{
			SystemLog sl = ((SystemLog)o);
			switch( columnIndex)
			{
				case DATE_COLUMN:
					return sl.dateTime;
				case TIME_COLUMN:
					return sl.dateTime;
				case POINT_ID_COLUMN:
					return sl.pointID;
				case PRIORITY_COLUMN:
					return sl.priority;
				case ACTION_COLUMN:
					return sl.action;					
				case DESCRIPTION_COLUMN:
					return sl.description;
				case USERNAME_COLUMN:
					return sl.userName;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getTitleString()
	 */
	public String getTitleString()
	{
		return "SYSTEM LOG";
	}

	/**
	 * Returns the startTime
	 * @return long startTime
	 */
	public long getStartTime()
	{
		return startTime;
	}
	/**
	 * Returns the stopTime
	 * @return long stopTime
	 */
	public long getStopTime()
	{
		return stopTime;
	}
	/**
	 * Set the startTime (in millis)
	 * @param long startTime
	 */
	public void setStartTime(long time)
	{
		startTime = time;
	}
	/**
	 * Set the stopTime (in millis)
	 * @param long stopTime
	 */
	public void setStopTime(long time)
	{
		stopTime = time;
	}

	/**
	 * Returns the logType
	 * @return Intger logType
	 */
	public Integer getLogType()
	{
		return logType;
	}

	/**
	 * Sets the logType
	 * Valid types are found in com.cannontech.database.db.point.SYSTEMLOG
	 * @param type Integer
	 */
	public void setLogType(Integer type_)
	{
		logType = type_;
	}
	/**
	 * Returns the pointID
	 * @return pointID
	 */
	public Integer getPointID()
	{
		return pointID;
	}

	/**
	 * Sets the pointID.
	 * @param pointID Integer
	 */
	public void setPointID(Integer pointID_)
	{
		pointID = pointID_;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");
		
		return format.format( new java.util.Date(getStartTime())) +  "  -  " +
				format.format( new java.util.Date(getStopTime()));
	}
}
