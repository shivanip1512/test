package com.cannontech.analysis.tablemodel;

import java.util.Arrays;
import java.util.Set;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.spring.YukonSpringHook;

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
	protected final int NUMBER_COLUMNS = 6;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
    public final static int PAO_DISABLE_FLAG_COLUMN = 1;
	public final static int PAO_TYPE_COLUMN = 2;
	public final static int METER_NUMBER_COLUMN = 3;
	public final static int ADDRESS_COLUMN = 4;
	public final static int ROUTE_NAME_COLUMN = 5;
	
	/** String values for column representation */
	public final static String PAO_NAME_STRING = "MCT Name";
    public final static String PAO_DISABLE_FLAG_STRING = "Disabled";
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
		setFilterModelTypes(new ReportFilter[]{ 
		        			ReportFilter.GROUPS}
							);
	}

	/**
	 * Build the SQL statement to retrieve DatabaseModel data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT DISTINCT PAO.PAOBJECTID, PAO.PAONAME, PAO.TYPE, PAO.DISABLEFLAG, " +
                                            " DMG.METERNUMBER, DCS.ADDRESS, ROUTE.PAOBJECTID, ROUTE.PAONAME ");	
			sql.append(" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, DEVICECARRIERSETTINGS DCS, DEVICEROUTES DR, YUKONPAOBJECT ROUTE " );
            sql.append(" WHERE PAO.PAOBJECTID = DMG.DEVICEID " + 
                       " AND PAO.PAOBJECTID = DCS.DEVICEID " + 
                       " AND PAO.PAOBJECTID = DR.DEVICEID " +
                       " AND ROUTE.PAOBJECTID = DR.ROUTEID "); 
			
            // Use paoIDs from group if they exist
			if( getBillingGroups() != null && getBillingGroups().length > 0) {
                sql.append(" AND PAO.PAOBJECTID IN (");
                DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
                Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(Arrays.asList(getBillingGroups()));
                String deviceGroupSqlInClause = deviceGroupService.getDeviceGroupSqlInClause(deviceGroups);
                sql.append(deviceGroupSqlInClause);
                sql.append(")");
			}
			
			//Use paoIDs in query if they exist
			if( getPaoIDs() != null && getPaoIDs().length > 0)  {
				sql.append(" AND PAO.PAOBJECTID IN (" + getPaoIDs()[0]);
				for (int i = 1; i < getPaoIDs().length; i++)
					sql.append(", " + getPaoIDs()[i]);
				sql.append(") ");
			}
		return sql;
	}
		
	@Override
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
                    Meter meter = new Meter();
 
					int paobjectID = rset.getInt(1);
                    meter.setDeviceId(paobjectID);
                    String paoName = rset.getString(2);
                    meter.setName(paoName);
                    String type = rset.getString(3);
                    int deviceType = PAOGroups.getDeviceType(type);
                    meter.setType(deviceType);
                    meter.setTypeStr(type);
                    String disabledStr = rset.getString(4);
                    boolean disabled = CtiUtilities.isTrue(disabledStr);
                    meter.setDisabled(disabled);
                    String meterNumber = rset.getString(5);
                    meter.setMeterNumber(meterNumber);
                    String address = rset.getString(6);
                    meter.setAddress(address);
                    int routeID = rset.getInt(7);
                    meter.setRouteId(routeID);
                    String routeName = rset.getString(8);
                    meter.setRoute(routeName);
					getData().add(meter);
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
	
	@Override
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
		if ( o instanceof Meter)
		{
		    Meter meter = (Meter)o;
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
					return meter.getName();
                
                case PAO_DISABLE_FLAG_COLUMN:
                    return (meter.isDisabled() ? "Disabled" : "");
		
				case PAO_TYPE_COLUMN:
					return meter.getTypeStr();

				case METER_NUMBER_COLUMN:
				    return meter.getMeterNumber();
				    
				case ADDRESS_COLUMN:
					return meter.getAddress();
	
				case ROUTE_NAME_COLUMN:
					return meter.getRoute();
				
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
                PAO_DISABLE_FLAG_STRING,
				PAO_TYPE_STRING,
				METER_NUMBER_STRING,
				ADDRESS_STRING,
				ROUTE_NAME_STRING
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
				new ColumnProperties(425, 1, 75, null),
				new ColumnProperties(500, 1, 200, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title + " - Carrier";
	}
	
	@Override
	public boolean useStartDate()
	{
		return false;
	}

	@Override
	public boolean useStopDate()
	{
		return false;
	}
}
