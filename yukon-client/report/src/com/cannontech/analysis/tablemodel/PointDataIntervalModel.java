package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.point.CTIPointQuailtyException;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
//import com.cannontech.database.db.point.RawPointHistory;

/**
 * Created on Dec 15, 2003
 * @author snebben
 * DatabaseModel TableModel object
 * Innerclass object for row data is CarrierData:
 *  String paoName			- YukonPaobject.paoName (device)
 *  String paoType			- YukonPaobject.type
 *  String address			- DeviceCarrierSettings.address
 *  String routeName		- YukonPaobject.paoName (route)
 *  String collGroup		- DeviceMeterGroup.collectionGroup
 *  String testCollGroup	- DeviceMeterGroup.testCollectionGroup
 */
public class PointDataIntervalModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 6;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
	public final static int POINT_NAME_COLUMN = 1;
	public final static int DATE_COLUMN = 2;
	public final static int TIME_COLUMN = 3;
	public final static int VALUE_COLUMN = 4;
	public final static int QUALITY_COLUMN = 5;
	
	/** String values for column representation */
	public final static String PAO_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String DATE_STRING  = "Date";
	public final static String TIME_STRING = "Time";
	public final static String VALUE_STRING = "Value";
	public final static String QUALITY_STRING = "Data Quality";
	
	/** A string for the title of the data */
	private static String title = "Interval Data Report";

	//Extensions of PointTypes
//	public final static int ALL_POINT_TYPE = -100;
	public final static int LOAD_PROFILE_POINT_TYPE = 101;	//some "unused" PointType int
	public final static int CALC_POINT_TYPE = PointTypes.CALCULATED_POINT;
	public final static int ANALOG_POINT_TYPE = PointTypes.ANALOG_POINT;
	public final static int DEMAND_ACC_POINT_TYPE = PointTypes.DEMAND_ACCUMULATOR_POINT;
	public final static int STATUS_POINT_TYPE = PointTypes.STATUS_POINT;
	
	private int pointType = LOAD_PROFILE_POINT_TYPE;	//default to Load Profile PointTypes
	
	private static final int ORDER_BY_TIMESTAMP = 0;
	private static final int ORDER_BY_VALUE = 1;
	private int orderBy = ORDER_BY_TIMESTAMP;	//default
	
	private static final int ASCENDING = 0;
	private static final int DESCENDING = 1;
	private int sortOrder = ASCENDING;
	
	/**
	 * Default Constructor
	 */
	public PointDataIntervalModel()
	{
		super();
	}
	/**
	 * Constructor class
	 * @param startTime_ rph.timestamp
	 * @param stopTime_ rph.timestamp
	 */
	public PointDataIntervalModel(long startTime_, long stopTime_)
	{
		this(startTime_, stopTime_, LOAD_PROFILE_POINT_TYPE, ORDER_BY_TIMESTAMP, ASCENDING);
	}
	
	public PointDataIntervalModel(long startTime_, long stopTime_, int intervalPointType)
	{
		this(startTime_, stopTime_, intervalPointType, ORDER_BY_TIMESTAMP, ASCENDING);
	}
	
	public PointDataIntervalModel(long startTime_, long stopTime_, int intervalPointType, int orderBy_)
	{
		this(startTime_, stopTime_, intervalPointType, orderBy_, ASCENDING);
	}

	public PointDataIntervalModel(long startTime_, long stopTime_, int intervalPointType, int orderBy_, int sortOrder_)
	{
		super(startTime_, stopTime_);
		setPointType(intervalPointType);
		setOrderBy(orderBy_);
		setSortOrder(sortOrder_);
	}
	/**
	 * Add CarrierData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			int changeID = rset.getInt(1);
			int pointID = rset.getInt(2);
			Timestamp ts = rset.getTimestamp(3);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeInMillis(ts.getTime());
			int quality = rset.getInt(4);
			double value = rset.getDouble(5);
					
			LiteRawPointHistory rph = new LiteRawPointHistory(changeID, pointID, cal.getTimeInMillis(), quality, value);
			getData().add(rph);	
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve DatabaseModel data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT RPH.CHANGEID, RPH.POINTID, RPH.TIMESTAMP, RPH.QUALITY, RPH.VALUE, P.POINTNAME, PAO.PAONAME " + 
			" FROM RAWPOINTHISTORY RPH, POINT P, DEVICEMETERGROUP DMG, YUKONPAOBJECT PAO " +
			" WHERE P.POINTID = RPH.POINTID " +
			" AND P.PAOBJECTID = PAO.PAOBJECTID " +
			" AND PAO.PAOBJECTID = DMG.DEVICEID " +
			" AND TIMESTAMP > ? AND TIMESTAMP <= ? ");
 
			//Use paoIDs in query if they exist
			if( getCollectionGroups() != null && getCollectionGroups().length > 0)
			{
				sql.append(" AND COLLECTIONGROUP IN ( '" + getCollectionGroups()[0]);
				for (int i = 1; i < getCollectionGroups().length; i++)
					sql.append("', '" + getCollectionGroups()[i]);
				sql.append("') ");
			}
			if( getPaoIDs() != null && getPaoIDs().length > 0)
			{
				sql.append(" AND PAO.PAOBJECTID IN (" + getPaoIDs()[0]);
				for (int i = 1; i < getPaoIDs().length; i++)
					sql.append(", " + getPaoIDs()[i]);
				sql.append(") ");
			}
			if(getPointType() == LOAD_PROFILE_POINT_TYPE )
			{
				sql.append(" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.DEMAND_ACCUMULATOR_POINT) + "' " + 
					" AND (P.POINTOFFSET >= " + PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND + " AND P.POINTOFFSET <= " + PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND + ") ");
			}
			else if ( getPointType() == STATUS_POINT_TYPE )
			{
				sql.append(" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.STATUS_POINT) + "' ");
			}
			else if ( getPointType() == DEMAND_ACC_POINT_TYPE)
			{
				sql.append(" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.DEMAND_ACCUMULATOR_POINT) + "' ");
			}
			else if ( getPointType() == ANALOG_POINT_TYPE )
			{
				sql.append(" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.ANALOG_POINT) + "' ");
			}
			else if ( getPointType() == CALC_POINT_TYPE)
			{
				sql.append(" AND (P.POINTTYPE = '" + PointTypes.getType(PointTypes.CALCULATED_POINT) + "' " +
					" OR P.POINTTYPE = '" + PointTypes.getType(PointTypes.CALCULATED_STATUS_POINT) + "' )");
			}
			
			sql.append(" ORDER BY PAO.PAOBJECTID, P.POINTNAME ");
			if (getOrderBy() == ORDER_BY_VALUE)
				sql.append(", VALUE " );
			else
				sql.append(", TIMESTAMP " );
			if( getSortOrder() == DESCENDING )
					sql.append(" DESC " );
		return sql;
	}
		
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		StringBuffer sql = buildSQLStatement();
		CTILogger.info(sql.toString());	
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		int rowCount = 0;

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
				CTILogger.info("ROWCOUNT: " + rowCount);
				if( rset != null)
					rset.close();
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
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");		
		return format.format(new java.util.Date());
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof LiteRawPointHistory)
		{
			LiteRawPointHistory rph = ((LiteRawPointHistory)o);
			LitePoint lp = PointFuncs.getLitePoint(rph.getPointID()); 
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
					return PAOFuncs.getYukonPAOName(lp.getPaobjectID());
		
				case POINT_NAME_COLUMN:
					return lp.getPointName();
	
				case DATE_COLUMN:
					return new Date(rph.getTimeStamp());
	
				case TIME_COLUMN:
					return new Date(rph.getTimeStamp());
				
				case VALUE_COLUMN:
					return new Double(rph.getValue());
				
				case QUALITY_COLUMN:
				{
					String qual = null;
					try
					{
						qual = PointQualities.getQuality(rph.getQuality());
					}
					catch (CTIPointQuailtyException e){
					}
					return qual;
				}
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
				PAO_NAME_STRING,
				POINT_NAME_STRING,
				DATE_STRING,
				TIME_STRING,
				VALUE_STRING,
				QUALITY_STRING
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
				String.class,
				String.class,
				Date.class,
				Date.class,
				Double.class,
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
				new ColumnProperties(0, 1, 175, null),
				new ColumnProperties(0, 1, 100, null),
				new ColumnProperties(0, 1, 100, "MM-dd-yyyy"),
				new ColumnProperties(100, 1, 100, "HH:mm:ss"),
				new ColumnProperties(200, 1, 100, "0.00#"),
				new ColumnProperties(300, 1, 100, null)
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
	/**
	 * @return
	 */
	public int getOrderBy()
	{
		return orderBy;
	}

	/**
	 * @return
	 */
	public int getSortOrder()
	{
		return sortOrder;
	}

	/**
	 * @param i
	 */
	public void setOrderBy(int i)
	{
		orderBy = i;
	}

	/**
	 * @param i
	 */
	public void setSortOrder(int i)
	{
		sortOrder = i;
	}

	/**
	 * @return
	 */
	public int getPointType()
	{
		return pointType;
	}

	/**
	 * @param i
	 */
	public void setPointType(int i)
	{
		pointType = i;
	}

}
