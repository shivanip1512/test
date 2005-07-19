package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

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
//	public final static int COLL_GROUP_NAME_COLUMN = 0;
//	public final static int DEVICE_NAME_COLUMN = 1;
//	public final static int POINT_NAME_COLUMN = 2;
//	public final static int TIMESTAMP_COLUMN = 3;
//	public final static int DISCONNECT_STATUS_COLUMN = 4;

	/** String values for column representation */
//	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
//	public final static String DEVICE_NAME_STRING = "Device Name";
//	public final static String POINT_NAME_STRING = "Point Name";
//	public final static String TIMESTAMP_STRING = "Timestamp";
//	public final static String DISCONNECT_STATUS_STRING = "Disconnect Status";

	/** A string for the title of the data */ //DEFAULT
	private static String title = "Disabled CBC Devices Report";
	 
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	
	public DisabledCBCDevicesModel()
	{
		super();		
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
		
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
/*				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime() ));
				CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()));
				CTILogger.info("STOP DATE < " + new java.sql.Timestamp(getStopTime()));
*/
			
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
		//TODO
		StringBuffer sql = new StringBuffer("");
		return sql;
	}

	/**
	 * Add <innerClass> objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		//TODO
//		try
//		{
//			String collGrp = rset.getString(1);
//			String paoName = rset.getString(2);
//			String pointName = rset.getString(3);					
//			Timestamp timestamp = rset.getTimestamp(4);
//			double value = rset.getDouble(5);
//			
//			String valueString = null;
//			if(value == 0)
//				valueString = DISCONNECTED_STRING;
//			else if(value == 1)
//				valueString = CONNECTED_STRING;	
//			else if(value == 2)
//				valueString = INTERMEDIATE_STRING;	
//			else if (value == -1)
//				valueString = INVALID_STRING;
//
//			Disconnect disconnect = new Disconnect(collGrp, paoName, pointName, new java.util.Date(timestamp.getTime()), valueString);
//			getData().add(disconnect);
//		}
//		catch(java.sql.SQLException e)
//		{
//			e.printStackTrace();
//		}
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		//TODO
//		if ( o instanceof Disconnect)
//		{
//			Disconnect meter = ((Disconnect)o); 
//			switch( columnIndex)
//			{
//				case COLL_GROUP_NAME_COLUMN:
//					return meter.getCollGroup();
//		
//				case DEVICE_NAME_COLUMN:
//					return meter.getDeviceName();
//	
//				case POINT_NAME_COLUMN:
//					return meter.getPointName();
//						
//				case TIMESTAMP_COLUMN:
//					return meter.getTimeStamp();
//					
//				case DISCONNECT_STATUS_COLUMN:
//					return meter.getValueString();
//			}
//		}
		return null;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		//TODO
//		if( columnNames == null)
//		{
//			columnNames = new String[]{
//				COLL_GROUP_NAME_STRING,
//				DEVICE_NAME_STRING,
//				POINT_NAME_STRING,
//				TIMESTAMP_STRING,
//				DISCONNECT_STATUS_STRING
//			};
//		}
		return columnNames;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		//TODO
//		if( columnTypes == null)
//		{
//			columnTypes = new Class[]{
//				String.class,
//				String.class,
//				String.class,
//				java.util.Date.class,
//				String.class
//			};
//		}
		return columnTypes;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		//TODO
//		if(columnProperties == null)
//		{
//			columnProperties = new ColumnProperties[]{
//				//posX, posY, width, height, numberFormatString
//				new ColumnProperties(0, 1, 100, 18, null),  //Collection Group
//				new ColumnProperties(0, 1, 200, 18, null),	//MCT
//				new ColumnProperties(15, 1, 150, 18, null),	//Point
//				new ColumnProperties(155, 1, 100, 18, "MM/dd/yyyy HH:MM:SS"),   //Timestamp
//				new ColumnProperties(260, 1, 100, 18, null)   // Rawpointhistory.value
//			};
//		}
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
