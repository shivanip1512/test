package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.point.CTIPointQuailtyException;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.spring.YukonSpringHook;

/**
 *  WARNING!!! LiteRawPointHistory objects created in this report are NOT intended to be changed into RawPointHistory DBPersistent objects
 *  due to the changeID (primary key) value is not set in order to retrieve distinct values!!!
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
	public final static int LOAD_PROFILE_POINT_TYPE = 101;	//some "unused" PointType int
	public final static int CALC_POINT_TYPE = PointTypes.CALCULATED_POINT;
	public final static int ANALOG_POINT_TYPE = PointTypes.ANALOG_POINT;
	public final static int DEMAND_ACC_POINT_TYPE = PointTypes.DEMAND_ACCUMULATOR_POINT;
	public final static int PULSE_ACC_POINT_TYPE = PointTypes.PULSE_ACCUMULATOR_POINT;
	public final static int STATUS_POINT_TYPE = PointTypes.STATUS_POINT;
	
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

	//servlet attributes/parameter strings
	private static final String ATT_POINT_TYPE = "pointType";
	private static final String ATT_ORDER_BY = "orderBy";
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
	 */
	public void addDataRow(ResultSet rset)
	{
		try
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
					
			//Using only a partially loaded lPao because that is all the information this report cares about.  Maybe a bad decision?!
            Meter meter = new Meter();
            meter.setDeviceId(paobjectID);
            meter.setName(paoName);
            MeterAndPointData mpData = new MeterAndPointData(meter, new Integer(pointID), pointName, 
                                                             cal.getTime(), new Double(value), new Integer(quality));
			getData().add(mpData);	
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
		StringBuffer sql = new StringBuffer	("SELECT DISTINCT RPH.POINTID, RPH.TIMESTAMP, RPH.QUALITY, RPH.VALUE, P.POINTNAME, PAO.PAONAME, PAO.PAOBJECTID " + 
			" FROM RAWPOINTHISTORY RPH, POINT P, YUKONPAOBJECT PAO ");
			
			if( getBillingGroups() != null && getBillingGroups().length > 0 ) //NO BILLING Group, we must want other devices too!
			    sql.append(", DEVICEMETERGROUP DMG ");
		
			sql.append(" WHERE P.POINTID = RPH.POINTID " +
			" AND P.PAOBJECTID = PAO.PAOBJECTID " +
			" AND TIMESTAMP > ? AND TIMESTAMP <= ? ");

			//Use billing groups in query if they exist
            final DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
            final String[] groups = getBillingGroups();
            
			if (groups != null && groups.length > 0) {
			    sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID ");

                String deviceGroupSqlWhereClause = getGroupSqlWhereClause("PAO.PAOBJECTID");
                sql.append(" AND " + deviceGroupSqlWhereClause);
			}
			//Use paoIDs in query if they exist			
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
	            //Do not allow LP data, those points fall into the LP point type option.
	            sql.append(" AND (P.POINTOFFSET < " + PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND + " OR P.POINTOFFSET > " + PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND + ") ");				
			}
			else if ( getPointType() == PULSE_ACC_POINT_TYPE)
			{
				sql.append(" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.PULSE_ACCUMULATOR_POINT) + "' ");
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
	@Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);

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
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()));
				CTILogger.info("START DATE > " + getStartDate() + "  -  STOP DATE <= " + getStopDate());
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
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
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
					try
					{
						qual = PointQualities.getQuality(mpData.getQuality().intValue());
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

	public String getPointTypeString(int pointTypeID)
	{
		switch (pointTypeID)
		{
			case LOAD_PROFILE_POINT_TYPE:
				return LOAD_PROFILE_POINT_TYPE_STRING;
			case CALC_POINT_TYPE:
				return CALC_POINT_TYPE_STRING;
			case ANALOG_POINT_TYPE:
				return ANALOG_POINT_TYPE_STRING;
			case DEMAND_ACC_POINT_TYPE:
				return DEMAND_ACC_POINT_TYPE_STRING;
			case PULSE_ACC_POINT_TYPE:
				return PULSE_ACC_POINT_TYPE_STRING;
			case STATUS_POINT_TYPE:
				return STATUS_POINT_TYPE_STRING;
		}
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
		html += "          <td valign='top' class='TitleHeader'>Point Type</td>" +LINE_SEPARATOR;
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
		}
	}
}
