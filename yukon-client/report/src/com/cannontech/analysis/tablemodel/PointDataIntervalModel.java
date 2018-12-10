package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.point.PointOffsets;
import com.cannontech.database.data.point.PointType;

/**
 *  WARNING!!! LiteRawPointHistory objects created in this report are NOT intended to be changed into RawPointHistory DBPersistent objects
 *  due to the changeID (primary key) value is not set in order to retrieve distinct values!!!
 */
public class PointDataIntervalModel extends ReportModelBase<MeterAndPointData>
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
	public final static int LOAD_PROFILE_POINT_TYPE = 101;	//some "unused" PointType int
	public final static int CALC_POINT_TYPE = PointType.CalcAnalog.getPointTypeId();
	public final static int ANALOG_POINT_TYPE = PointType.Analog.getPointTypeId();
	public final static int DEMAND_ACC_POINT_TYPE = PointType.DemandAccumulator.getPointTypeId();
	public final static int PULSE_ACC_POINT_TYPE = PointType.PulseAccumulator.getPointTypeId();
	public final static int STATUS_POINT_TYPE = PointType.Status.getPointTypeId();
	
	public final static String LOAD_PROFILE_POINT_TYPE_STRING = "All Load Profile";	//some "unused" PointType int
	public final static String CALC_POINT_TYPE_STRING = "All Calculated";
	public final static String ANALOG_POINT_TYPE_STRING = "All Analog";
	public final static String DEMAND_ACC_POINT_TYPE_STRING = "All Demand Accumulator";
	public final static String PULSE_ACC_POINT_TYPE_STRING = "All Pulse Accumulator";
	public final static String STATUS_POINT_TYPE_STRING = "All Status";

	private final static int[] ALL_POINT_TYPES = new int[]
	{
		LOAD_PROFILE_POINT_TYPE,
		CALC_POINT_TYPE,
		ANALOG_POINT_TYPE,
		DEMAND_ACC_POINT_TYPE,
		PULSE_ACC_POINT_TYPE,
		STATUS_POINT_TYPE	
	};
	
	private int pointType = LOAD_PROFILE_POINT_TYPE;	//default to Load Profile PointTypes
	
	public static final int ORDER_BY_TIMESTAMP = 0;
	public static final int ORDER_BY_VALUE = 1;
	private int orderBy = ORDER_BY_TIMESTAMP;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_TIMESTAMP, ORDER_BY_VALUE
	};
	
    private boolean excludeDisabledDevices = false; 

	//servlet attributes/parameter strings
	private static final String ATT_POINT_TYPE = "pointType";
	private static final String ATT_ORDER_BY = "orderBy";
	private static final String ATT_EXCLUDE_DISABLED_DEVICES = "excludeDisabledDevices";
	/**
	 * Default Constructor
	 */
	public PointDataIntervalModel()
	{
		this(null, null, LOAD_PROFILE_POINT_TYPE, ORDER_BY_TIMESTAMP, ASCENDING);
	}
	/**
	 * Constructor class
	 * @param startTime_ rph.timestamp
	 * @param stopTime_ rph.timestamp
	 */
	public PointDataIntervalModel(Date start_, Date stop_)
	{
		this(start_, stop_, LOAD_PROFILE_POINT_TYPE, ORDER_BY_TIMESTAMP, ASCENDING);
	}
	
	public PointDataIntervalModel(Date start_, Date stop_, int intervalPointType)
	{
		this(start_, stop_, intervalPointType, ORDER_BY_TIMESTAMP, ASCENDING);
	}
	
	public PointDataIntervalModel(Date start_, Date stop_, int intervalPointType, int orderBy_)
	{
		this(start_, stop_, intervalPointType, orderBy_, ASCENDING);
	}

	public PointDataIntervalModel(Date start_, Date stop_, int intervalPointType, int orderBy_, int sortOrder_)
	{
		super(start_, stop_);
		setDateFormat(new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z"));
		setPointType(intervalPointType);
		setOrderBy(orderBy_);
		setSortOrder(sortOrder_);
		setFilterModelTypes(new ReportFilter[]{
				ReportFilter.METER,
				ReportFilter.DEVICE,
				ReportFilter.GROUPS,
				ReportFilter.RTU}
				);
	}
	/**
	 * Add CarrierData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 * @throws SQLException 
	 */
	public void addDataRow(ResultSet rset) throws SQLException
	{	    
	    int pointID = rset.getInt(1);
	    Timestamp ts = rset.getTimestamp(2);
	    GregorianCalendar cal = new GregorianCalendar();
	    cal.setTimeInMillis(ts.getTime());
	    int quality = rset.getInt(3);
	    double value = rset.getDouble(4);
	    String pointName = rset.getString(5);
	    String paoName = rset.getString(6);
	    int paobjectID = rset.getInt(7);
        PaoType paoType = PaoType.getForDbString(rset.getString(8));

	    //Using only a partially loaded lPao because that is all the information this report cares about.  Maybe a bad decision?!
        PaoIdentifier paoIdentifier = new PaoIdentifier(paobjectID, paoType);
	    PlcMeter meter = new PlcMeter(paoIdentifier, "", paoName, false, "", -1, "");
	    
	    MeterAndPointData mpData = new MeterAndPointData(meter, new Integer(pointID), pointName, 
	                                                     cal.getTime(), new Double(value), new Integer(quality));
	    getData().add(mpData);
	}

	/**
	 * Build the SQL statement to retrieve DatabaseModel data.
	 * @return StringBuffer  an sqlstatement
	 */
	public SqlFragmentSource buildSQLStatement()
	{
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("SELECT DISTINCT RPH.POINTID, RPH.TIMESTAMP, RPH.QUALITY, RPH.VALUE, P.POINTNAME, PAO.PAONAME, PAO.PAOBJECTID, PAO.TYPE ");
	    sql.append(" FROM RAWPOINTHISTORY RPH, POINT P, YUKONPAOBJECT PAO ");
	    sql.append(" WHERE P.POINTID = RPH.POINTID ");
	    sql.append(" AND P.PAOBJECTID = PAO.PAOBJECTID ");
	    sql.append(" AND TIMESTAMP > ").appendArgument(getStartDate());
	    sql.append(" AND TIMESTAMP <= ").appendArgument(getStopDate());
	    if (excludeDisabledDevices) {
	        sql.append(" AND PAO.DISABLEFLAG").eq("N");
	    }

	    //Use billing groups in query if they exist
	    final String[] groups = getBillingGroups();

	    if (groups != null && groups.length > 0) {
	        SqlFragmentSource deviceGroupSqlWhereClause = getGroupSqlWhereClause("PAO.PAOBJECTID");
	        sql.append(" AND ", deviceGroupSqlWhereClause);
	    }
	    //Use paoIDs in query if they exist			
	    if (getPaoIDs() != null && getPaoIDs().length > 0) {
	        sql.append(" AND PAO.PAOBJECTID IN (", getPaoIDs(), ")");
	    }
	    if (getPointType() == LOAD_PROFILE_POINT_TYPE ) {
	        sql.append(" AND P.POINTTYPE").eq_k(PointType.DemandAccumulator);
	        sql.append(" AND (P.POINTOFFSET >= ").appendArgument(PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND);
	        sql.append(" AND P.POINTOFFSET <= ").appendArgument(PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND).append(")");
	    } else if ( getPointType() == STATUS_POINT_TYPE ) {
	        sql.append(" AND P.POINTTYPE").eq_k(PointType.Status);
	    } else if ( getPointType() == DEMAND_ACC_POINT_TYPE) {
	        sql.append(" AND P.POINTTYPE").eq_k(PointType.DemandAccumulator);
	        //Do not allow LP data, those points fall into the LP point type option.
	        sql.append(" AND (P.POINTOFFSET < ").appendArgument(PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND);
	        sql.append( " OR P.POINTOFFSET > ").appendArgument(PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND).append(") ");				
	    } else if ( getPointType() == PULSE_ACC_POINT_TYPE) {
	        sql.append(" AND P.POINTTYPE").eq_k(PointType.PulseAccumulator);
	    } else if ( getPointType() == ANALOG_POINT_TYPE ) {
	        sql.append(" AND P.POINTTYPE").eq_k(PointType.Analog);
	    } else if ( getPointType() == CALC_POINT_TYPE) {
	        sql.append(" AND (P.POINTTYPE").eq_k(PointType.CalcAnalog);
	        sql.append(" OR P.POINTTYPE").eq_k(PointType.CalcStatus).append(")");
	    }

	    sql.append(" ORDER BY PAO.PAOBJECTID, P.POINTNAME ");
	    if (getOrderBy() == ORDER_BY_VALUE) {
            sql.append(", VALUE " );
        } else {
            sql.append(", TIMESTAMP " );
        }
	    if (getSortOrder() == DESCENDING ) {
            sql.append(" DESC " );
        }
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
	@Override
    public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof MeterAndPointData)
		{
            MeterAndPointData mpData = (MeterAndPointData)o;
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
					return mpData.getMeter().getName();
		
				case POINT_NAME_COLUMN:
					return mpData.getPointName();
	
				case DATE_COLUMN:
                case TIME_COLUMN:
					return mpData.getTimeStamp();
	
				case VALUE_COLUMN:
					return mpData.getValue();
				
				case QUALITY_COLUMN:
				{
					String qual = null;
					try {
						qual = PointQuality.getPointQuality(mpData.getQuality().intValue()).getDescription();
					} catch (IllegalArgumentException e){
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
	@Override
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
	@Override
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
	@Override
    public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
				new ColumnProperties(0, 1, 200, null),
				new ColumnProperties(300, 1, 200, null),
				new ColumnProperties(100, 1, 100, "MM-dd-yyyy"),
				new ColumnProperties(200, 1, 100, "HH:mm:ss"),
				new ColumnProperties(300, 1, 100, "0.00####"),
				new ColumnProperties(400, 1, 100, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	@Override
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
	 * @param i
	 */
	public void setOrderBy(int i)
	{
		orderBy = i;
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

    public String getPointTypeString(int pointTypeID) {
        if (pointTypeID == LOAD_PROFILE_POINT_TYPE)
            return LOAD_PROFILE_POINT_TYPE_STRING;
        else if (pointTypeID == CALC_POINT_TYPE)
            return CALC_POINT_TYPE_STRING;
        else if (pointTypeID == ANALOG_POINT_TYPE)
            return ANALOG_POINT_TYPE_STRING;
        else if (pointTypeID == DEMAND_ACC_POINT_TYPE)
            return DEMAND_ACC_POINT_TYPE_STRING;
        else if (pointTypeID == PULSE_ACC_POINT_TYPE)
            return PULSE_ACC_POINT_TYPE_STRING;
        else if (pointTypeID == STATUS_POINT_TYPE)
            return STATUS_POINT_TYPE_STRING;
        else
            return "UNKNOWN";
    }
	/**
	 * @return
	 */
	public static int[] getAllPointTypes()
	{
		return ALL_POINT_TYPES;
	}
	public String getOrderByString(int orderBy)
	{
		switch (orderBy)
		{
			case ORDER_BY_TIMESTAMP:
				return "Order By Timestamp";
			case ORDER_BY_VALUE:
				return "Order By Value";
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
		html += "<table align='center' width='100%' border='0'cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='title-header'>Point Type</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < getAllPointTypes().length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='" + ATT_POINT_TYPE +"' value='" + getAllPointTypes()[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getPointTypeString(getAllPointTypes()[i])+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Order By</td>" +LINE_SEPARATOR;
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
		html += "          <td class='title-header'>&nbsp;Order Direction</td>" +LINE_SEPARATOR;
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
        
        html += "    <td valign='top'>" + LINE_SEPARATOR;
        html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td class='title-header'>Disabled Devices</td>" +LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;        
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td><input type='checkbox' name='" +ATT_EXCLUDE_DISABLED_DEVICES + "' value='true'> Exclude Disabled Devices" + LINE_SEPARATOR;
        html += "          </td>" + LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
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
			String param = req.getParameter(ATT_POINT_TYPE);
			if( param != null)
				setPointType(Integer.valueOf(param).intValue());
			else
				setPointType(LOAD_PROFILE_POINT_TYPE);
			
			param = req.getParameter(ATT_ORDER_BY);
			if( param != null)
				setOrderBy(Integer.valueOf(param).intValue());
			else
				setOrderBy(ORDER_BY_TIMESTAMP);
				
			param = req.getParameter(ATT_SORT_ORDER);
			if( param != null)
				setSortOrder(Integer.valueOf(param).intValue());
			else
				setSortOrder(ASCENDING);
				
			//These are custom parameter values declared in the Reports.jsp file
			param = req.getParameter("startHour");
			String param2 = req.getParameter("startMinute");
			if( param != null && param2 != null)	//hmmm, maybe we shouldn't be so judgemental and not require both to be set
			{
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTimeZone(getTimeZone());
				cal.setTime(getStartDate());
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(param.trim()).intValue());
				cal.set(Calendar.MINUTE, Integer.valueOf(param2.trim()).intValue());
				setStartDate(cal.getTime());
			}
			param = req.getParameter("stopHour");
			param2 = req.getParameter("stopMinute");
			if( param != null && param2 != null)	//hmmm, maybe we shouldn't be so judgemental and not require both to be set
			{
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTimeZone(getTimeZone());
				cal.setTime(getStopDate());
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(param.trim()).intValue());
				cal.set(Calendar.MINUTE, Integer.valueOf(param2.trim()).intValue());
				setStopDate(cal.getTime());
			}
			param = req.getParameter(ATT_EXCLUDE_DISABLED_DEVICES);
    		if( param != null) {
    		    excludeDisabledDevices = CtiUtilities.isTrue(param);
    		}
		}
	}
	
	@Override
	public boolean useStartStopTimes() {
	    return true;
	}
}