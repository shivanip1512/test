package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.Carrier;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.db.device.DeviceMeterGroup;

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
public class CarrierDBModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
	public final static int PAO_TYPE_COLUMN = 1;
	public final static int METER_NUMBER_COLUMN = 2;
	public final static int ADDRESS_COLUMN = 3;
	public final static int ROUTE_NAME_COLUMN = 4;
	public final static int COLL_GROUP_NAME_COLUMN = 5;
	public final static int TEST_COLL_GROUP_NAME_COLUMN = 6;
	
	/** String values for column representation */
	public final static String PAO_NAME_STRING = "MCT Name";
	public final static String PAO_TYPE_STRING = "Type";
	public final static String METER_NUMBER_STRING = "Meter Number";
	public final static String ADDRESS_STRING  = "Address";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String TEST_COLL_GROUP_NAME_STRING = "Alternate Group";
	
	/** A string for the title of the data */
	private static String title = "Database Report";
		
	/**
	 * Default Constructor
	 */
	public CarrierDBModel()
	{
		super();
	}

	/**
	 * Add CarrierData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			String paoName = rset.getString(1);
			String paoType = rset.getString(2);
			String address = rset.getString(3);			
			String routeName = rset.getString(4);
			String collGroup = rset.getString(5);
			String testCollGroup = rset.getString(6);
			String meterNumber = rset.getString(7);
					
			Carrier carrier = new Carrier(paoName, paoType, meterNumber, address, routeName, collGroup, testCollGroup);
			getData().add(carrier);
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
		StringBuffer sql = new StringBuffer	("SELECT PAO1.PAONAME MCT, PAO1.TYPE, DCS.ADDRESS, PAO2.PAONAME ROUTE, COLLECTIONGROUP, TESTCOLLECTIONGROUP, METERNUMBER " + 
			" FROM YUKONPAOBJECT PAO1, YUKONPAOBJECT PAO2, DEVICEROUTES DR, DEVICECARRIERSETTINGS DCS, DEVICEMETERGROUP DMG "+
			" WHERE PAO1.PAOBJECTID = DMG.DEVICEID "+
			" AND PAO1.PAOBJECTID = DR.DEVICEID " + 
			" AND PAO2.PAOBJECTID = DR.ROUTEID " +
			" AND PAO1.PAOBJECTID = DCS.DEVICEID ");

			//billing group selection
			if( getBillingGroups() != null && getBillingGroups().length > 0)
			{
				sql.append(" AND " + DeviceMeterGroup.getValidBillGroupTypeStrings()[getBillingGroupType()] + " IN ( '" + getBillingGroups()[0]);
				for (int i = 1; i < getBillingGroups().length; i++)
					sql.append("', '" + getBillingGroups()[i]);
				sql.append("') ");
			}
			
			//Use paoIDs in query if they exist
			if( getPaoIDs() != null && getPaoIDs().length > 0)
			{
				sql.append(" AND PAO1.PAOBJECTID IN (" + getPaoIDs()[0]);
				for (int i = 1; i < getPaoIDs().length; i++)
					sql.append(", " + getPaoIDs()[i]);
				sql.append(") ");
			}
			
			if( getPaoClass() != null ) 
				sql.append(" AND PAO1.PAOCLASS = '" + getPaoClass() + "' ");
			sql.append(" ORDER BY PAO1.PAONAME" );
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
	 * Return the paoclass
	 * @return String paoClass
	 */
	private String getPaoClass()
	{
		return DeviceClasses.STRING_CLASS_CARRIER;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		//Use current date 
		return getDateFormat().format(new java.util.Date());
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof Carrier)
		{
			Carrier carrier = ((Carrier)o); 
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
					return carrier.getPaoName();
		
				case PAO_TYPE_COLUMN:
					return carrier.getPaoType();

				case METER_NUMBER_COLUMN:
					return carrier.getMeterNumber();
				    
				case ADDRESS_COLUMN:
					return carrier.getAddress();
	
				case ROUTE_NAME_COLUMN:
					return carrier.getRouteName();
				
				case COLL_GROUP_NAME_COLUMN:
					return carrier.getCollGroup();
				
				case TEST_COLL_GROUP_NAME_COLUMN:
					return carrier.getTestCollGroup();
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
				PAO_TYPE_STRING,
				METER_NUMBER_STRING,
				ADDRESS_STRING,
				ROUTE_NAME_STRING,
				COLL_GROUP_NAME_STRING,
				TEST_COLL_GROUP_NAME_STRING,
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
				String.class,
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
				new ColumnProperties(0, 1, 200, null),
				new ColumnProperties(200, 1, 75, null),
				new ColumnProperties(275, 1, 75, null),
				new ColumnProperties(350, 1, 75, null),
				new ColumnProperties(425, 1, 125, null),
				new ColumnProperties(550, 1, 80, null),
				new ColumnProperties(630, 1, 80, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title + " - " +getPaoClass();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#useBillingGroup()
	 */
	public boolean useBillingGroup()
	{
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#useStartDate()
	 */
	public boolean useStartDate()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#useStopDate()
	 */
	public boolean useStopDate()
	{
		return false;
	}

	public String getHTMLOptionsTable()
	{
		return super.getHTMLOptionsTable();
	}

	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
	}

}
