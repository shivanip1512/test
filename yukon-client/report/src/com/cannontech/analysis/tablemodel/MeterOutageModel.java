package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.JdbcTemplateHelper;

/**
 * Created on Dec 15, 2003
 * MeterData TableModel object
 *  String deviceName	- YukonPaobject.paoName
 *  String pointName	- Point.pointName
 *  Integer pointID		- Point.pointID
 *  String routeName  
 * @author snebben
 */
public class MeterOutageModel extends ReportModelBase<MeterAndPointData>
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;
	
	/** Enum values for column representation */
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int DEVICE_TYPE_COLUMN = 1;
	public final static int METER_NUMBER_COLUMN = 2;
	public final static int PHYSICAL_ADDRESS_COLUMN = 3;
	public final static int ROUTE_NAME_COLUMN = 4;
	public final static int DATE_TIME_COLUMN = 5;
	public final static int DURATION = 6;
//	public final static int POINT_NAME_COLUMN = 7;

	/** String values for column representation */
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String DEVICE_TYPE_STRING = "Type";
	public final static String METER_NUMBER_STRING = "Meter Number";
	public final static String PHYSICAL_ADDRESS_STRING = "Address";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String DATE_TIME_STRING = "Date/Time";
	public final static String DURATION_STRING = "Duration";
	public final static String POINT_NAME_STRING = "Point Name";

	/** Class fields */
	private int minOutageSecs = 0;
	public static final int ORDER_BY_DEVICE_NAME = 0;
	public static final int ORDER_BY_TIMESTAMP = 1;
	public static final int ORDER_BY_DURATION = 2;

	private int orderBy = ORDER_BY_DEVICE_NAME;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_DEVICE_NAME, ORDER_BY_TIMESTAMP, ORDER_BY_DURATION
	};

	//servlet attributes/parameter strings
	private static final String ATT_ORDER_BY = "orderBy";
	private static final String ATT_MINIMUM_OUTAGE_SECS = "minOutageSecs";
	/**
	 * 
	 */
	public MeterOutageModel()
	{
		this(null);
	}
	/**
	 * 
	 */
	public MeterOutageModel(Date start_)
	{
		//Long.MIN_VALUE is the default (null) value for time
		super(start_, null);
		setFilterModelTypes(new ReportFilter[]{
                ReportFilter.METER,
                ReportFilter.DEVICE,
                ReportFilter.GROUPS}
                );
	}
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 * @throws SQLException 
	 */
	public void addDataRow(ResultSet rset) throws SQLException
	{
	    Meter meter = new Meter();

	    int paobjectID = rset.getInt(1);
	    meter.setDeviceId(paobjectID);
	    String paoName = rset.getString(2);
	    meter.setName(paoName);
        PaoType paoType = PaoType.getForDbString(rset.getString(3));
        meter.setPaoType(paoType);
	    boolean disabled = CtiUtilities.isTrue(rset.getString(4).charAt(0));
	    meter.setDisabled(disabled);
	    String meterNumber = rset.getString(5);
	    meter.setMeterNumber(meterNumber);
	    String address = rset.getString(6);
	    meter.setAddress(address);
	    int routeID = rset.getInt(7);
	    meter.setRouteId(routeID);
	    String routeName = rset.getString(8);
	    meter.setRoute(routeName);
	    int pointID = rset.getInt(9);
	    String pointName = rset.getString(10);

	    Date ts = null;
	    Double value = null;
	    Timestamp timestamp = rset.getTimestamp(11);
	    ts = new Date(timestamp.getTime());
	    value = new Double(rset.getDouble(12));

	    MeterAndPointData meterAndPointData = new MeterAndPointData(meter, pointID, pointName, ts, value);

	    getData().add(meterAndPointData);
	}

	/**
	 * Build the SQL statement to retrieve MissedMeter data.
	 * @return StringBuffer  an sqlstatement
	 */
	public SqlFragmentSource buildSQLStatement()
	{

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT PAO.PAOBJECTID, PAO.PAONAME, PAO.TYPE, PAO.DISABLEFLAG, ");
        sql.append(" DMG.METERNUMBER, DCS.ADDRESS, ROUTE.PAOBJECTID, ROUTE.PAONAME, ");
        sql.append(" P.POINTID, P.POINTNAME, TIMESTAMP, VALUE ");

        sql.append(" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, DEVICECARRIERSETTINGS DCS, ");
        sql.append(" DEVICEROUTES DR, YUKONPAOBJECT ROUTE, POINT P , RAWPOINTHISTORY RPH");
        
        sql.append(" WHERE PAO.PAOBJECTID = DMG.DEVICEID ");
        sql.append(" AND PAO.PAOBJECTID = DCS.DEVICEID ");
        sql.append(" AND PAO.PAOBJECTID = DR.DEVICEID ");
        sql.append(" AND ROUTE.PAOBJECTID = DR.ROUTEID ");
        sql.append(" AND P.POINTID = RPH.POINTID ");
        sql.append(" AND PAO.PAOBJECTID = P.PAOBJECTID "); 

        sql.append(" AND P.POINTOFFSET = 100 AND P.POINTTYPE = 'Analog' ");	// OUTAGE POINT OFFSET and POINTTYPE
        sql.append(" AND VALUE >= ").appendArgument(getMinOutageSecs());
        sql.append(" AND TIMESTAMP > ").appendArgument(getStartDate());
        sql.append(" AND TIMESTAMP <= ").appendArgument(getStopDate());
		
        // RESTRICT BY DEVICE/METER PAOID (if any)
        String paoIdWhereClause = getPaoIdWhereClause("PAO.PAOBJECTID");
        if (!StringUtils.isBlank(paoIdWhereClause)) {
            sql.append(" AND " + paoIdWhereClause);
        }
        
        // RESTRICT BY GROUPS (if any)
        final String[] groups = getBillingGroups();
        if (groups != null && groups.length > 0) {
            SqlFragmentSource deviceGroupSqlWhereClause = getGroupSqlWhereClause("PAO.PAOBJECTID");
            sql.append(" AND ", deviceGroupSqlWhereClause);
        }
        
		sql.append(" ORDER BY ");	//TODO what to order by?
		if (getOrderBy() == ORDER_BY_TIMESTAMP)
			sql.append(" TIMESTAMP " );		
		else if (getOrderBy() == ORDER_BY_DURATION)
		    sql.append(" VALUE ");
		else //if (getOrderBy() == ORDER_BY_DEVICE_NAME) //default
			sql.append(" PAO.PAONAME ");

		if( getOrderBy() == DESCENDING)
		    sql.append(" DESC ");
		
		if (getOrderBy() == ORDER_BY_DEVICE_NAME) //add more ordering columns when device name selected
		    sql.append(", POINTNAME, TIMESTAMP"); 
		return sql;
	}
	
	@Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
				
        SqlFragmentSource sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        CTILogger.info("START DATE > " + getStartDate() + " - STOP DATE <= " + getStopDate());
        
        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
        template.query(sql.getSql(), sql.getArguments(), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                addDataRow(rs);
            }
        });
		
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof MeterAndPointData)
		{
			MeterAndPointData meterPD = ((MeterAndPointData)o);
			switch( columnIndex)
			{
				case DEVICE_NAME_COLUMN:
					return meterPD.getMeter().getName();
					
				case DEVICE_TYPE_COLUMN:
				    return meterPD.getMeter().getPaoType().getPaoTypeName();
				    
				case METER_NUMBER_COLUMN:
				    return meterPD.getMeter().getMeterNumber();
				    
				case PHYSICAL_ADDRESS_COLUMN:
				    return meterPD.getMeter().getAddress();
				    
				case ROUTE_NAME_COLUMN:
					return meterPD.getMeter().getRoute();
					
				case DATE_TIME_COLUMN:
				    return meterPD.getTimeStamp();

				case DURATION:
				    return TimeUtil.convertSecondsToTimeString(meterPD.getValue().doubleValue());
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
				DEVICE_NAME_STRING,
				DEVICE_TYPE_STRING,
				METER_NUMBER_STRING,
				PHYSICAL_ADDRESS_STRING,
				ROUTE_NAME_STRING,
				DATE_TIME_STRING,
				DURATION_STRING
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
				Date.class,
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
				new ColumnProperties(0, 1, 180, null),
				new ColumnProperties(180, 1, 80, null),
				new ColumnProperties(260, 1, 80, null),
				new ColumnProperties(340, 1, 80, null),
				new ColumnProperties(420, 1, 120, null),
				new ColumnProperties(540, 1, 100, "MM/dd/yyyy HH:mm:ss"),
				new ColumnProperties(640, 1, 80, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
	    String title = "Outage Report";
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
	 * @param i
	 */
	public void setOrderBy(int i)
	{
		orderBy = i;
	}
	
	public String getOrderByString(int orderBy)
	{
		switch (orderBy)
		{
			case ORDER_BY_DEVICE_NAME:
				return "Device Name";
			case ORDER_BY_TIMESTAMP:
				return "Timestamp";
			case ORDER_BY_DURATION:
			    return "Duration";
		}
		return "UNKNOWN";
	}
	public static int[] getAllOrderBys()
	{
		return ALL_ORDER_BYS;
	}
	@Override
	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Minimum Outage Duration</td>";
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='main'>";
		html += "            <input type='text' name='"+ATT_MINIMUM_OUTAGE_SECS +"' value='" + getMinOutageSecs() + "'>&nbsp;seconds";  
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;		

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Order By</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < getAllOrderBys().length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='"+ATT_ORDER_BY+"' value='" + getAllOrderBys()[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getOrderByString(getAllOrderBys()[i])+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Order Direction</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < getAllSortOrders().length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='" +ATT_SORT_ORDER + "' value='" + getAllSortOrders()[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getSortOrderString(getAllSortOrders()[i])+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}
	@Override
	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
			String param = req.getParameter(ATT_MINIMUM_OUTAGE_SECS);
			if( param != null)
				setMinOutageSecs(Integer.valueOf(param).intValue());
			else
				setMinOutageSecs(0);

			param = req.getParameter(ATT_ORDER_BY);
			if( param != null)
				setOrderBy(Integer.valueOf(param).intValue());
			else
				setOrderBy(ORDER_BY_DEVICE_NAME);
		}
	}

    /**
     * @return Returns the minOutageSecs.
     */
    public int getMinOutageSecs()
    {
        return minOutageSecs;
    }
    /**
     * @param minOutageSecs The minOutageSecs to set.
     */
    public void setMinOutageSecs(int minOutageSecs)
    {
        this.minOutageSecs = minOutageSecs;
    }
}