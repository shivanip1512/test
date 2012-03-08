package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;

/**
 * Created on Dec 15, 2003
 * @author snebben
 * DatabaseModel TableModel object
 * Innerclass object for row data is CarrierData:
 *  String paoName			- YukonPaobject.paoName (device)
 *  String paoType			- YukonPaobject.type
 *  String address			- DeviceCarrierSettings.address
 *  String routeName		- YukonPaobject.paoName (route)
 */
public class CarrierDBModel extends ReportModelBase<Meter>
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 6;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
    public final static int PAO_ENABLE_FLAG_COLUMN = 1;
	public final static int PAO_TYPE_COLUMN = 2;
	public final static int METER_NUMBER_COLUMN = 3;
	public final static int ADDRESS_COLUMN = 4;
	public final static int ROUTE_NAME_COLUMN = 5;
	
	
	/** String values for column representation */
	public final static String PAO_NAME_STRING = "MCT Name";
    public final static String PAO_ENABLE_FLAG_STRING = "Enabled";
	public final static String PAO_TYPE_STRING = "Type";
	public final static String METER_NUMBER_STRING = "Meter #";
	public final static String ADDRESS_STRING  = "Address";
	public final static String ROUTE_NAME_STRING = "Route Name";
	
	/** A string for the title of the data */
	protected static String title = "Database Report";
		
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
	public SqlFragmentSource buildSQLStatement()
	{
		SqlStatementBuilder sql = new SqlStatementBuilder("SELECT DISTINCT PAO.PAOBJECTID, PAO.PAONAME, PAO.TYPE, PAO.DISABLEFLAG, ");
		sql.append(" DMG.METERNUMBER, DCS.ADDRESS, ROUTE.PAOBJECTID, ROUTE.PAONAME ");	
		sql.append(" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, DEVICECARRIERSETTINGS DCS, DEVICEROUTES DR, YUKONPAOBJECT ROUTE " );
		sql.append(" WHERE PAO.PAOBJECTID = DMG.DEVICEID ");
		sql.append(" AND PAO.PAOBJECTID = DCS.DEVICEID "); 
		sql.append(" AND PAO.PAOBJECTID = DR.DEVICEID ");
		sql.append(" AND ROUTE.PAOBJECTID = DR.ROUTEID "); 
			
		// Use paoIDs from group if they exist
		if( getBillingGroups() != null && getBillingGroups().length > 0) {
		    SqlFragmentSource deviceGroupSqlWhereClause = getGroupSqlWhereClause("PAO.PAOBJECTID");
		    sql.append(" AND ", deviceGroupSqlWhereClause);
		}

		//Use paoIDs in query if they exist
		if( getPaoIDs() != null && getPaoIDs().length > 0)  {
		    sql.append(" AND PAO.PAOBJECTID IN (", getPaoIDs(), ")");
		}
		return sql;
	}
		
	@Override
	public void collectData()
	{
	    //Reset all objects, new data being collected!
	    setData(null);

	    SqlFragmentSource sql = buildSQLStatement();
	    CTILogger.info(sql.getSql());	

	    JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
	    yukonTemplate.query(sql.getSql(), sql.getArguments(), new ResultSetExtractor() {
	        @Override
	        public Object extractData(ResultSet rset) throws SQLException, DataAccessException {

	            while( rset.next())
	            {
	                Meter meter = new Meter();

	                int paobjectID = rset.getInt(1);
	                String paoName = rset.getString(2);
	                meter.setName(paoName);
	                PaoType paoType = PaoType.getForDbString(rset.getString(3));
	                PaoIdentifier paoIdentifier = new PaoIdentifier(paobjectID, paoType);
	                meter.setPaoIdentifier(paoIdentifier);
	                String disabledStr = rset.getString(4);
	                boolean disabled = CtiUtilities.isTrue(disabledStr.charAt(0));
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
	            return null;
	        }
	    });
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
                
                case PAO_ENABLE_FLAG_COLUMN:
                    return (meter.isDisabled() ? "No" : "Yes");
		
				case PAO_TYPE_COLUMN:
					return meter.getPaoType().getPaoTypeName();

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
                PAO_ENABLE_FLAG_STRING,
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
				new ColumnProperties(0, 1, 260, null),
				new ColumnProperties(260, 1, 70, null),
				new ColumnProperties(330, 1, 80, null),
				new ColumnProperties(410, 1, 80, null),
				new ColumnProperties(490, 1, 80, null),
				new ColumnProperties(570, 1, 150, null)
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
