package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.PowerFail;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

/**
 * Created on Dec 15, 2003
 * MissedMeterData TableModel object
 * Innerclass object for row data is MissedMeter:
 *  String collGroup	- DeviceMeterGroup.collectionGroup 
 *  String deviceName	- YukonPaobject.paoName
 *  String pointName	- Point.pointName
 *  Integer pointID		- Point.pointID
 *  String routeName  
 * @author snebben
 */
public class PowerFailModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;

	/** Enum values for column representation */
	public final static int COLL_GROUP_NAME_COLUMN = 0;
	public final static int DEVICE_NAME_COLUMN = 1;
	public final static int POINT_NAME_COLUMN = 2;
	public final static int POINT_ID_COLUMN = 3;
	public final static int POWER_FAIL_COUNT_COLUMN = 4;

	/** String values for column representation */
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String POINT_ID_STRING = "Point ID";
	public final static String POWER_FAIL_COUNT_STRING = "Power Fail Count";

	/** A string for the title of the data */
	private static String title = "Power Fail Count By Collection Group";
	
	/**
	 * 
	 */
	public PowerFailModel()
	{
		super();
	}
	
	/**
	 * 
	 */
	public PowerFailModel(long startTime_)
	{
		super(startTime_, Long.MIN_VALUE);
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
			Integer powerFailCount = new Integer(rset.getInt(5));
			PowerFail powerFail = new PowerFail(collGrp, paoName, pointName, pointID, powerFailCount);

			getData().add(powerFail);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve PowerFail data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT DMG.COLLECTIONGROUP, PAO.PAONAME, P.POINTNAME, P.POINTID, " + 
			" RPH.VALUE " +
			" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, POINT P, RAWPOINTHISTORY RPH "+
			" WHERE PAO.PAOBJECTID = DMG.DEVICEID  AND P.POINTID = RPH.POINTID"+
			" AND P.PAOBJECTID = PAO.PAOBJECTID AND P.POINTOFFSET = 20 ");
			
		if(getCollectionGroups() != null)
				{
					sql.append(" AND DMG.COLLECTIONGROUP IN ('" + getCollectionGroups()[0]);
	
					for (int i = 1; i < getCollectionGroups().length; i++)
					{
						sql.append("','" + getCollectionGroups()[i]);
					}
					sql.append("')");
				}
			 
			sql.append(" AND RPH.TIMESTAMP > ? ORDER BY DMG.COLLECTIONGROUP, PAO.PAONAME, P.POINTNAME");
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
				CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()));
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

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM dd, yyyy");		
		return format.format(new java.util.Date(getStartTime()));
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof PowerFail)
		{
			PowerFail meter = ((PowerFail)o); 
			switch( columnIndex)
			{
				case COLL_GROUP_NAME_COLUMN:
					return meter.getCollGroup();
		
				case DEVICE_NAME_COLUMN:
					return meter.getDeviceName();
	
				case POINT_NAME_COLUMN:
					return meter.getPointName();
	
				case POINT_ID_COLUMN:
					return meter.getPointID();
					
				case POWER_FAIL_COUNT_COLUMN:
					return meter.getPowerFailCount();
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
				POINT_ID_STRING,
				POWER_FAIL_COUNT_STRING
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
				Integer.class,
				Integer.class
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
				new ColumnProperties(0, 1, 100, 18, null),
				new ColumnProperties(100, 1, 100, 18, null),
				new ColumnProperties(200, 1, 100, 18, null),
				new ColumnProperties(300, 1, 100, 18, "#"),
				new ColumnProperties(400, 1, 100, 18, "#")
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
