package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.PowerFail;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.device.DeviceMeterGroup;

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
	public final static int DATE_COLUMN = 3;
	public final static int TIME_COLUMN = 4;
	public final static int POWER_FAIL_COUNT_COLUMN = 5;

	/** String values for column representation */
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
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
	public PowerFailModel(Date start_, Date stop_)
	{
		super(start_, stop_);
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
			Timestamp timestamp = rset.getTimestamp(4);
			Integer powerFailCount = new Integer(rset.getInt(5));
			PowerFail powerFail = new PowerFail(collGrp, paoName, pointName, new Date(timestamp.getTime()), powerFailCount);

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
		StringBuffer sql = new StringBuffer	("SELECT DMG.COLLECTIONGROUP, PAO.PAONAME, P.POINTNAME, RPH.TIMESTAMP, RPH.VALUE " + 
			" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, POINT P, RAWPOINTHISTORY RPH "+
			" WHERE PAO.PAOBJECTID = DMG.DEVICEID  AND P.POINTID = RPH.POINTID"+
			" AND P.PAOBJECTID = PAO.PAOBJECTID AND P.POINTOFFSET = 20 ");
			
		if( getBillingGroups() != null && getBillingGroups().length > 0)
		{
			sql.append(" AND " + DeviceMeterGroup.getValidBillGroupTypeStrings()[getBillingGroupType()] + " IN ( '" + getBillingGroups()[0]);
			for (int i = 1; i < getBillingGroups().length; i++)
				sql.append("', '" + getBillingGroups()[i]);
			sql.append("') ");
		}

			 
			sql.append(" AND RPH.TIMESTAMP > ? AND TIMESTAMP <= ? ORDER BY DMG.COLLECTIONGROUP, PAO.PAONAME, P.POINTNAME");
		return sql;
	
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
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime()));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()));
				CTILogger.info("START DATE > " + getStartDate() + " - STOP DATE <= " + getStopDate());
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
	
				case DATE_COLUMN:
					return meter.getTimestamp();

				case TIME_COLUMN:
					return meter.getTimestamp();
					
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
				DATE_STRING,
				TIME_STRING,
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
				Date.class,
				Date.class,
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
				new ColumnProperties(0, 1, 200, null),
				new ColumnProperties(0, 1, 160, null),
				new ColumnProperties(160, 1, 160, null),
				new ColumnProperties(320, 1, 75, "MM/dd/yyyy"),
				new ColumnProperties(395, 1, 75, "HH:mm:ss"),
				new ColumnProperties(470, 1, 80, "#")
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
	
	public String getHTMLOptionsTable()
	{
		return super.getHTMLOptionsTable();
	}

	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#useBillingGroup()
	 */
	public boolean useBillingGroup()
	{
		return true;
	}
}
