package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.point.SystemLog;

/**
 * Created on Dec 15, 2003
 * SystemLogModel TableModel object
 * Innerclass object for row data is SystemLog: 
 *  java.util.Date dateTime	- SystemLog.dateTime
 *  String pointName 		- SystemLog.pointID
 *  Integer priority 		- SystemLog.priority
 *  String action 			- SystemLog.action
 *  String description 		- SystemLog.description
 *  String userName 		- SystemLog.userName
 * @author snebben
 */
public class SystemLogModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;
	
	/** Enum values for column representation */
	public final static int DATE_COLUMN = 0;
	public final static int TIME_COLUMN = 1;
	public final static int POINT_ID_COLUMN = 2;
	public final static int POINT_NAME_COLUMN = 3;
	public final static int PRIORITY_COLUMN = 4;
	public final static int USERNAME_COLUMN = 5;
	public final static int ACTION_COLUMN = 6;
	public final static int DESCRIPTION_COLUMN = 7;	

	/** String values for column representation */
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
	public final static String POINT_ID_STRING = "PointID";
	public final static String POINT_NAME_STRING = "Point";
	public final static String PRIORITY_STRING = "Priority";
	public final static String ACTION_STRING = "Action";
	public final static String DESCRIPTION_STRING = "Description";
	public final static String USERNAME_STRING = "UserName";
	
	/** A string for the title of the data */
	private static String title = "SYSTEM LOG";
	
	/** Class fields */
	/** Flag indicating data to be ordered ASC or DESC in tableModel*/
	private boolean orderDescending = false;
	
	/**
	 * Type int from com.cannontech.database.db.point.SystemLog.type
	 * Allows for reporting by type, null value results in all types.
	 */
	private int[] logTypes = null;
	
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
		this(startTime_, stopTime_, null, null);
	}	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public SystemLogModel(long startTime_, long stopTime_, Integer logType_)
	{
		this(startTime_, stopTime_, logType_, null);
	}
	/**
	 * Constructor class
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public SystemLogModel()
	{
		super();
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
		super();
		setStartTime(startTime_);
		setStopTime(stopTime_);
		if( logType_!= null)
			setLogType(logType_.intValue());
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

			SystemLog systemLog = new SystemLog();
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
		StringBuffer sql = new StringBuffer("SELECT DATETIME, SL.POINTID, PRIORITY, ACTION, SL.DESCRIPTION, USERNAME "+ 
			" FROM SYSTEMLOG SL, POINT P, YUKONPAOBJECT PAO ");
			sql.append(" WHERE (DATETIME > ?) AND (DATETIME <= ?)" + 
						" AND P.POINTID = SL.POINTID " + 
						" AND P.PAOBJECTID = PAO.PAOBJECTID ");
			
			//if LMControlLog model, let's trust the YUKONPAOBJECT.class value = 'GROUP'
			// rather than the SYSTEMLOG.type value == 3 (LOADMANAGMENT)	SN / COREY
			if(this instanceof LMControlLogModel)
			{
				sql.append(" AND PAOCLASS = '" + DeviceClasses.STRING_CLASS_GROUP + "' ");
				if( getPaoIDs() != null)
				{
					sql.append(" AND PAO.PAOBJECTID IN (" + getPaoIDs()[0]);
					for (int i = 1; i < getPaoIDs().length; i++)
						sql.append(" , " + getPaoIDs()[i]);
					sql.append(" )");					
				}
			}
			else 
			{
				if( getLogTypes() != null)
				{
					sql.append(" AND SL.TYPE IN (" + getLogTypes()[0]);
					for (int i = 1; i < getLogTypes().length; i++)
						sql.append(" , " + getLogTypes()[i]);
					sql.append(" )");
				}
				if( getPointID() != null)
					sql.append(" AND SL.POINTID = " + getPointID().intValue());
			}
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
		CTILogger.info(sql.toString());
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime()));
				CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()) + "  -  STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
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
		CTILogger.info("Report Records Collected from Database: " + getData().size());
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
	 * Returns the logType
	 * @return Intger logType
	 */
	public int[] getLogTypes()
	{
		return logTypes;
	}

	/**
	 * Sets the logType
	 * Valid types are found in com.cannontech.database.db.point.SYSTEMLOG
	 * @param type Integer
	 */
	public void setLogType(int type_)
	{
		setLogTypes(new int[]{type_});
	}
	/**
	 * Sets the logType
	 * Valid types are found in com.cannontech.database.db.point.SYSTEMLOG
	 * @param type Integer
	 */
	public void setLogTypes(int[] types_)
	{
		logTypes = types_;
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
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof SystemLog)
		{
			SystemLog sl = ((SystemLog)o);
			switch( columnIndex)
			{
				case DATE_COLUMN:
				{
					//Set the date to the begining of the day so we can "group" by date
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime(sl.getDateTime());
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					return cal.getTime();
				}
				case TIME_COLUMN:
					return sl.getDateTime();
				case POINT_ID_COLUMN:
					return sl.getPointID();
				case POINT_NAME_COLUMN:
					return PointFuncs.getPointName(sl.getPointID().intValue());
				case PRIORITY_COLUMN:
					return sl.getPriority();
				case USERNAME_COLUMN:
					return sl.getUserName();
				case ACTION_COLUMN:
					return sl.getAction();					
				case DESCRIPTION_COLUMN:
					return sl.getDescription();
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[]{
				DATE_STRING,
				TIME_STRING,
				POINT_ID_STRING,
				POINT_NAME_STRING,
				PRIORITY_STRING,
				USERNAME_STRING,
				ACTION_STRING,
				DESCRIPTION_STRING
			};
		}
		return columnNames;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[]{
				java.util.Date.class,
				java.util.Date.class,
				Integer.class,
				String.class,
				Integer.class,
				String.class,
				String.class,
				String.class
			};
		}
			
		return columnTypes;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 100, "MMMMM dd, yyyy"),
				new ColumnProperties(0, 1, 50, "HH:mm:ss"),
				new ColumnProperties(50, 1, 100, "#"),
				new ColumnProperties(50, 1, 100, null),
				new ColumnProperties(150, 1, 40, "#"),
				new ColumnProperties(190, 1, 90, null),
				new ColumnProperties(280, 1, 200, null),
				new ColumnProperties(480, 1, 230, null)
			};				
		}
		return columnProperties;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}
}
