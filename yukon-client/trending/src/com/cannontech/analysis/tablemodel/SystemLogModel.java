package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.ReportTypes;
import com.cannontech.database.db.point.SystemLog;

/**
 * Created on Dec 15, 2003
 * SystemLogModel TableModel object
 * Innerclass object for row data is SystemLog: 
 *  java.util.Date dateTime	- SystemLog.dateTime
 *  Integer pointID 		- SystemLog.pointID
 *  Integer priority 		- SystemLog.priority
 *  String action 			- SystemLog.action
 *  String description 		- SystemLog.description
 *  String userName 		- SystemLog.userName
 * @author snebben
 */
public class SystemLogModel extends ReportModelBase
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
	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 */
	public SystemLogModel(long startTime_, long stopTime_)
	{
		this(startTime_, stopTime_, null, null, ReportTypes.SYSTEM_LOG_DATA);
	}	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public SystemLogModel(long startTime_, long stopTime_, Integer logType_)
	{
		this(startTime_, stopTime_, logType_, null, ReportTypes.SYSTEM_LOG_DATA);
	}
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param pointID_ SYSTEM.pointID
	 * @param logType_ SYSTEMLOG.type
	 */
	public SystemLogModel(long startTime_, long stopTime_, Integer logType_, Integer pointID_)
	{
		this(startTime_, stopTime_, logType_, pointID_ , ReportTypes.SYSTEM_LOG_DATA);
	}
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param pointID_ SYSTEM.pointID
	 * @param logType_ SYSTEMLOG.type
	 */
	public SystemLogModel(long startTime_, long stopTime_, Integer logType_, Integer pointID_, int reportType_)
	{
		super();
		setStartTime(startTime_);
		setStopTime(stopTime_);
		setLogType(logType_);
		setPointID(pointID_);
		setReportType(reportType_);
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

			SystemLog systemLog = (SystemLog)ReportTypes.create(getReportType());
			systemLog.setDateTime(new java.util.Date(dateTime.getTime()));
			systemLog.setPointID(pointID);
			systemLog.setPriority(priority);
			systemLog.setAction(action);
			systemLog.setDescription(description);
			systemLog.setUserName(userName);
 
			getData().add(systemLog);
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
		com.cannontech.clientutils.CTILogger.info("Report Records Collected from Database: " + getData().size());
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
		if( logType == null )
		{
			switch (getReportType())
			{
				case ReportTypes.SYSTEM_LOG_DATA:
					logType = null;
				case ReportTypes.LM_CONTROL_LOG_DATA:
					logType = new Integer(SystemLog.TYPE_LOADMANAGEMENT);
				default:
					return logType;
			}	
		}
		return logType;
	}

	/**
	 * Sets the logType
	 * Valid types are found in com.cannontech.database.db.point.SYSTEMLOG
	 * @param type Integer
	 */
	private void setLogType(Integer type_)
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
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");
		
		return format.format( new java.util.Date(getStartTime())) +  "  -  " +
				format.format( new java.util.Date(getStopTime()));
	}
}
