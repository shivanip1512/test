package com.cannontech.report.cbc;

import java.sql.ResultSet;
import java.sql.Timestamp;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.device.Disconnect;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.point.PointTypes;

/**
 * DisconnectReportDatabase TableModel object
 * Extending classes must implement:
 *   addDataRow(ResultSet)	- add a "row" object to the data Vector
 *   buildSQLStatement()	- Returns the sql query statment
 * 
 * Contains the start and stop times for query information.
 * Contains the devicemetergroup - YukonPaobject.paoname
 * 				deviceName - YukonPaobject.paoname
 * 				pointName - Point.pointname
 * @author bjonasson
 */
public class DisabledCBCDevicesModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;

	/** Enum values for column representation */
	public final static int COLL_GROUP_NAME_COLUMN = 0;
	public final static int DEVICE_NAME_COLUMN = 1;
	public final static int POINT_NAME_COLUMN = 2;
	public final static int TIMESTAMP_COLUMN = 3;
	public final static int DISCONNECT_STATUS_COLUMN = 4;

	/** String values for column representation */
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String TIMESTAMP_STRING = "Timestamp";
	public final static String DISCONNECT_STATUS_STRING = "Disconnect Status";

	/** A string for the title of the data */ //DEFAULT
	private static String title = "Disconnect Status By Collection Group";
	
	/** Rawpointhistory.value critera, null results in current disconnect state? */
	/** valid types are:  Disconnected | Connected | History | Current  */
	public static String DISCONNECTED_STRING = "Disconnected";
	public static String CONNECTED_STRING = "Connected";
	public static String HISTORY_STRING = "History";
	public static String CURRENT_STRING = "Current";
	public static String INTERMEDIATE_STRING = "Intermediate";
	public static String INVALID_STRING = "Invalid";
	 
	private String disconnectType = null;
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	
	public DisabledCBCDevicesModel()
	{
		this("Current", ReportTypes.DISCONNECT_DATA);//default type		
	}
	
	
	public DisabledCBCDevicesModel(String disconnectType_)
	{
		this(disconnectType_, ReportTypes.DISCONNECT_DATA);//default type		
	}
	/**
	 * Constructor class
	 * @param disconnectType_ Rawpointhistory.value 
	 	 */
	public DisabledCBCDevicesModel(String disconnectType_, int reportType_)
	{
		super();
		setDisconnectType(disconnectType_);
		setReportType(reportType_);		
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
				if( getDisconnectType().equalsIgnoreCase(HISTORY_STRING))
				{
					pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime() ));
					pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime() ));
					CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()));
					CTILogger.info("STOP DATE < " + new java.sql.Timestamp(getStopTime()));
				}
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
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT DISTINCT DMG.COLLECTIONGROUP, PAO.PAOName, P.POINTNAME, " + 
		
			" RPH1.TIMESTAMP, RPH1.VALUE " +
		
				" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, POINT P, RAWPOINTHISTORY RPH1" +
				" WHERE PAO.PAOBJECTID = DMG.DEVICEID " +
				" AND PAO.PAOBJECTID = P.PAOBJECTID " +
				" AND P.POINTID = RPH1.POINTID " +
				" AND P.POINTOFFSET = 1 " +
				" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.STATUS_POINT) + "' ");
		if(getCollectionGroups() != null)
		{
			sql.append(" AND DMG.COLLECTIONGROUP IN ('" + getCollectionGroups()[0] + "'");
	
			for (int i = 1; i < getCollectionGroups().length; i++)
			{
				sql.append(", '" + getCollectionGroups()[i]+ "'");
			}
			sql.append(")");
		}

		if( getDisconnectType().equalsIgnoreCase(HISTORY_STRING))
		{
			sql.append(" AND RPH1.TIMESTAMP > ? AND RPH1.TIMESTAMP <= ? ORDER BY PAO.PAONAME" );
		}
		else		
		{
			if (getDisconnectType().equalsIgnoreCase(CONNECTED_STRING))
			{
				sql.append(" AND RPH1.VALUE = 1.0 ");
			}
			else if (getDisconnectType().equalsIgnoreCase(DISCONNECTED_STRING))
			{
				sql.append(" AND RPH1.VALUE = 0.0 ");
			}
			sql.append(" AND RPH1.TIMESTAMP = ( SELECT MAX(RPH2.TIMESTAMP) FROM RAWPOINTHISTORY RPH2 " + 
							" WHERE RPH1.POINTID = RPH2.POINTID) " );
		}
		
		return sql;
		
	}

	/**
	 * Add <innerClass> objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			String collGrp = rset.getString(1);
			String paoName = rset.getString(2);
			String pointName = rset.getString(3);					
			Timestamp timestamp = rset.getTimestamp(4);
			double value = rset.getDouble(5);
			
			String valueString = null;
			if(value == 0)
				valueString = DISCONNECTED_STRING;
			else if(value == 1)
				valueString = CONNECTED_STRING;	
			else if(value == 2)
				valueString = INTERMEDIATE_STRING;	
			else if (value == -1)
				valueString = INVALID_STRING;

			Disconnect disconnect = new Disconnect(collGrp, paoName, pointName, new java.util.Date(timestamp.getTime()), valueString);
			getData().add(disconnect);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * valid types are:  Disconnected | Connected | History | Current
	 * @return disconnectType
	 */
	public String getDisconnectType()
	{
		return disconnectType;
	}

	/**
	 * Set the disconnectType
	 * @param String disconnectType_
	 */
	public void setDisconnectType(String disconnectType_)
	{
		disconnectType = disconnectType_;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	 public String getDateRangeString()
	 {
		 java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");		
		 return (format.format(new java.util.Date(getStartTime())) + " through " +
		 			(format.format(new java.util.Date(getStopTime()))));
	 }


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof Disconnect)
		{
			Disconnect meter = ((Disconnect)o); 
			switch( columnIndex)
			{
				case COLL_GROUP_NAME_COLUMN:
					return meter.getCollGroup();
		
				case DEVICE_NAME_COLUMN:
					return meter.getDeviceName();
	
				case POINT_NAME_COLUMN:
					return meter.getPointName();
						
				case TIMESTAMP_COLUMN:
					return meter.getTimeStamp();
					
				case DISCONNECT_STATUS_COLUMN:
					return meter.getValueString();
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
				COLL_GROUP_NAME_STRING,
				DEVICE_NAME_STRING,
				POINT_NAME_STRING,
				TIMESTAMP_STRING,
				DISCONNECT_STATUS_STRING
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
				String.class,
				java.util.Date.class,
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
				new ColumnProperties(0, 1, 100, 18, null),  //Collection Group
				new ColumnProperties(0, 1, 200, 18, null),	//MCT
				new ColumnProperties(15, 1, 150, 18, null),	//Point
				new ColumnProperties(155, 1, 100, 18, "MM/dd/yyyy HH:MM:SS"),   //Timestamp
				new ColumnProperties(260, 1, 100, 18, null)   // Rawpointhistory.value
			};
		}
		return columnProperties;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		if( getDisconnectType().equalsIgnoreCase(DISCONNECT_STATUS_STRING) ||
			getDisconnectType().equalsIgnoreCase(CONNECTED_STRING))
			return getDisconnectType() + " Status Report";

		else if (getDisconnectType().equalsIgnoreCase(HISTORY_STRING) ||
				getDisconnectType().equalsIgnoreCase(CURRENT_STRING))
			return "Disconnect - " + getDisconnectType() + " Report";
		
		return title = "Disconnect Status Report";
	}
}
