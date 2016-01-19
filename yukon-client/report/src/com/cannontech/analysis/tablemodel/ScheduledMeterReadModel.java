package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.NaturalOrderComparator;

public class ScheduledMeterReadModel extends ReportModelBase<ScheduledMeterReadModel.ScheduledMeterReadRow>
{
    private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    static public class ScheduledMeterReadRow {
    	public String scheduleName;
    	public Date scheduleStartTime;
    	public Date scheduleStopTime;
    	public String requestID;
    	public String command;
    	public Date requestStartTime;
    	public Date requestStopTime;
        public Date readTimestamp;
        public Integer statusCode;
        public String paoName;
        public String meterNumber;
        public String address;
        public String routeName;
    }
    
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 13;
	
	/** Enum values for column representation */
	public final static int SCHEDULE_NAME_COLUMN = 0;
	public final static int SCHEDULE_START_TIME_COLUMN = 1;
	public final static int SCHEDULE_STOP_TIME_COLUMN = 2;
	public final static int REQUEST_COMMAND_COLUMN = 3;
	public final static int REQUEST_ID_COLUMN = 4;
	public final static int REQUEST_START_TIME_COLUMN = 5;
	public final static int REQUEST_STOP_TIME_COLUMN = 6;
	public final static int DEVICE_NAME_COLUMN = 7;
	public final static int METER_NUMBER_COLUMN = 8;
	public final static int PHYSICAL_ADDRESS_COLUMN = 9;
	public final static int ROUTE_NAME_COLUMN = 10;
	public final static int READ_TIMESTAMP_COLUMN = 11;
	public final static int STATUS_CODE_COLUMN = 12;

	/** String values for column representation */
	public final static String SCHEDULE_NAME_STRING = "ScheduleName";
	public final static String SCHEDULE_START_TIME_STRING = "ScheduleStart";
	public final static String SCHEDULE_STOP_TIME_STRING = "ScheduleStop";
	public final static String REQUEST_COMMAND_STRING = "Command";
	public final static String REQUEST_ID_STRING = "RequestID";
	public final static String REQUEST_START_TIME_STRING = "RequestStart";
	public final static String REQUEST_STOP_TIME_STRING = "RequestStop";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String METER_NUMBER_STRING = "Meter No.";
	public final static String PHYSICAL_ADDRESS_STRING = "Address";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String READ_TIMESTAMP_STRING= "Timestamp";
	public final static String STATUS_CODE_STRING = "StatusCode";
	public final static int SCHEDULED_METER_READ_RECORDS_LIMIT = 150000;
	
	/** Class fields */
	private HashMap totals = null;

	public enum StatusCodeType { ALL_METER_READ_TYPE("All"), 		//All status codes
								  ERROR_METER_READ_TYPE("Errors Only"),	//Only status codes > 0
								  SUCCESS_METER_READ_TYPE("Success Only");	//0 status code

		private String displayName;

		private StatusCodeType(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}								  
	}
	public StatusCodeType statusCodeType = StatusCodeType.ALL_METER_READ_TYPE;
	
	public enum GroupBy{ GROUP_BY_SCHEDULE_REQUESTS("Schedule Requests"),
				  GROUP_BY_DEVICE("Device");
		
		private String displayName;
		
		private GroupBy(String displayName) {
			this.displayName = displayName;
		}
		
		private String getDisplayName() {
			return displayName;
		}
	}
	private GroupBy groupBy = GroupBy.GROUP_BY_SCHEDULE_REQUESTS;	//default
	
	private enum OrderBy{ ORDER_BY_DEVICE_NAME("Device Name"),
						  ORDER_BY_ROUTE_NAME("Route Name"),
						  ORDER_BY_METER_NUMBER("Meter Number"),
						  ORDER_BY_STATUS_CODE("Status Code");

		private String displayName;
	
		private OrderBy(String displayName) {
			this.displayName = displayName;
		}

		private String getDisplayName() {
			return displayName;
		}
	}
	private OrderBy orderBy = OrderBy.ORDER_BY_DEVICE_NAME;	//default

	//servlet attributes/parameter strings
	private static String ATT_STATUS_CODE_TYPE = "statusCodeType";
	private static final String ATT_ORDER_BY = "orderBy";
	private static final String ATT_GROUP_BY = "groupBy";
	
	/**
	 * 
	 */
	public ScheduledMeterReadModel() {
		this(StatusCodeType.ALL_METER_READ_TYPE);
	}
	
	/**
	 * Valid read types are enum StatusCodeType
	 */
	public ScheduledMeterReadModel(StatusCodeType statCodeType) {
		setStatusCodeType(statCodeType);
		setFilterModelTypes(new ReportFilter[]{
				ReportFilter.SCHEDULE,
				ReportFilter.METER,
				ReportFilter.DEVICE
			} 
		);
	}
	
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 * @throws SQLException 
	 */
	public void addDataRow(ResultSet rset) throws SQLException {
	    String scheduleName = rset.getString(1);
	    Timestamp timestamp = rset.getTimestamp(2);
	    Date schedStartTime = new Date(timestamp.getTime());
	    timestamp = rset.getTimestamp(3);
	    Date schedStopTime = new Date(timestamp.getTime());
	    int requestID = rset.getInt(4);
	    String command = rset.getString(5);
	    timestamp = rset.getTimestamp(6);
	    Date reqStartTime = new Date(timestamp.getTime());
	    timestamp = rset.getTimestamp(7);
	    Date reqStopTime = new Date(timestamp.getTime());

	    timestamp = rset.getTimestamp(8);
	    Date readTimestamp = new Date(timestamp.getTime());
	    Integer statusCode = new Integer(rset.getInt(9));
	    String paoName = rset.getString(10);
	    int address = rset.getInt(11);
	    String meterNumber = rset.getString(12);
	    String routeName = rset.getString(13);

	    ScheduledMeterReadRow row = new ScheduledMeterReadRow();
	    row.scheduleName = scheduleName;
	    row.scheduleStartTime = schedStartTime;
	    row.scheduleStopTime = schedStopTime;
	    row.requestID = String.valueOf(requestID);
	    row.command = command;
	    row.requestStartTime = reqStartTime;
	    row.requestStopTime = reqStopTime;
	    row.readTimestamp = readTimestamp;
	    row.statusCode = statusCode;
	    row.paoName = paoName;
	    row.address = String.valueOf(address);
	    row.meterNumber = meterNumber;
	    row.routeName = routeName;

	    getData().add(row);
	}

    public Comparator scheduledMeterReadComparator = new java.util.Comparator<ScheduledMeterReadRow>()
    {
        public int compare(ScheduledMeterReadRow smr1, ScheduledMeterReadRow smr2){
            int tempCompareValue = 0;
            NaturalOrderComparator noComp = new NaturalOrderComparator(); 
            
            tempCompareValue = smr1.scheduleStartTime.compareTo(smr2.scheduleStartTime);
            if (tempCompareValue != 0) {
                return tempCompareValue;
            }

            tempCompareValue = smr1.scheduleStopTime.compareTo(smr2.scheduleStopTime);
            if (tempCompareValue != 0) {
                return tempCompareValue;
            }

            tempCompareValue = smr1.scheduleName.compareTo(smr2.scheduleName);
            if (tempCompareValue != 0) {
                return tempCompareValue;
            }
            
            //Order by the GroupBy option, this order is necessary for the Report to group additionally by Request
            if (getGroupBy() == GroupBy.GROUP_BY_SCHEDULE_REQUESTS) {
                
                tempCompareValue = smr1.requestStartTime.compareTo(smr2.requestStartTime);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
                
                tempCompareValue = smr1.requestStopTime.compareTo(smr2.requestStopTime);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
                
            }           

            //Order by the OrderBy option, this order is necessary for the ordering of the item band
            if (getOrderBy() == OrderBy.ORDER_BY_DEVICE_NAME) {
                tempCompareValue = smr1.paoName.compareTo(smr2.paoName);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
        
                tempCompareValue = smr1.readTimestamp.compareTo(smr2.readTimestamp);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
        
            } else if (getOrderBy() == OrderBy.ORDER_BY_METER_NUMBER) {
                tempCompareValue = noComp.compare(smr1.meterNumber, smr2.meterNumber);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
        
                tempCompareValue = smr1.readTimestamp.compareTo(smr2.readTimestamp);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
        
            } else if (getOrderBy() == OrderBy.ORDER_BY_ROUTE_NAME) {
                tempCompareValue = smr1.routeName.compareTo(smr2.routeName);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
        
                tempCompareValue = smr1.paoName.compareTo(smr2.paoName);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
        
                tempCompareValue = smr1.readTimestamp.compareTo(smr2.readTimestamp);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
                
            } else if (getOrderBy() == OrderBy.ORDER_BY_STATUS_CODE) {
                tempCompareValue = smr1.statusCode.compareTo(smr2.statusCode);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }

                tempCompareValue = smr1.paoName.compareTo(smr2.paoName);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
                
                tempCompareValue = smr1.readTimestamp.compareTo(smr2.readTimestamp);
                if (tempCompareValue != 0) {
                    return tempCompareValue;
                }
            }

            return 0;
        }
    };

    @Override
    public void collectData() {
        vendorSpecificSqlBuilderFactory = YukonSpringHook.getBean(VendorSpecificSqlBuilderFactory.class);

        // Reset all objects, new data being collected!
        setData(null);
        totals = null;

        SqlFragmentSource sql = buildLimitedSQLStatement();
        CTILogger.info("SQL for MeterReadModel: " + sql.toString());
        CTILogger.info("START DATE >= " + getStartDate() + " - STOP DATE < " + getStopDate());
        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
        template.query(sql.getSql(), sql.getArguments(), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                addDataRow(rs);
            }
        });

        Collections.sort(getData(), scheduledMeterReadComparator);

        CTILogger.info("Report Records Collected from Database: " + getData().size());
    }

    private SqlFragmentSource buildLimitedSQLStatement() {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        buildSqlServerQuery(builder);
        buildOtherQuery(builder);
        return builder;
    }

    /**
     * Build the SQL query to retrieve MissedMeter data for sql server.
     * 
     * @param VendorSpecificSqlBuilder
     * @return SqlBuilder
     */
    public SqlBuilder buildSqlServerQuery(VendorSpecificSqlBuilder builder) {
        SqlBuilder sql = builder.buildFor(DatabaseVendor.getMsDatabases());
        sql.append("SELECT TOP " + SCHEDULED_METER_READ_RECORDS_LIMIT);
        sql.append("SCHEDULE.PAONAME, DRJL.STARTTIME, DRJL.STOPTIME, ");
        sql.append(" DRRL.REQUESTID, DRRL.COMMAND, DRRL.STARTTIME, DRRL.STOPTIME, ");
        sql.append(" DRL.TIMESTAMP, DRL.STATUSCODE, ");
        sql.append(" PAO.PAONAME, DCS.ADDRESS, DMG.METERNUMBER, ROUTE.PAONAME ");
        sql.append(" FROM DEVICEREADLOG DRL, DEVICEREADREQUESTLOG DRRL, DEVICEREADJOBLOG DRJL, ");
        sql.append(" YUKONPAOBJECT PAO, DEVICECARRIERSETTINGS DCS, DEVICEMETERGROUP DMG, ");
        sql.append(" YUKONPAOBJECT ROUTE, DEVICEROUTES DR, YUKONPAOBJECT SCHEDULE ");
        sql.append(" WHERE");
        appendCommonWhereClauses(sql);
        return sql;
    }

    /**
     * Build the SQL query to retrieve MissedMeter data for oracle or other vendor.
     * 
     * @param VendorSpecificSqlBuilder
     * @return SqlBuilder
     * @return
     */
    public SqlBuilder buildOtherQuery(VendorSpecificSqlBuilder builder) {
        SqlBuilder sql = builder.buildOther();
        sql.append("SELECT shdPaoName, jobstarttime,  jobstoptime, requestid, command, starttime, ");
        sql.append("stoptime, timestamp, statuscode, paoname, address, meterno, routename ");
        sql.append("FROM (");
        sql.append("SELECT SCHEDULE.PAONAME as shdpaoname, DRJL.STARTTIME as jobstarttime, ");
        sql.append("DRJL.STOPTIME jobstoptime,DRRL.REQUESTID as requestid, DRRL.COMMAND as command, ");
        sql.append("DRRL.STARTTIME as starttime, DRRL.STOPTIME as stoptime,DRL.TIMESTAMP as timestamp, ");
        sql.append("DRL.STATUSCODE as statuscode,PAO.PAONAME as paoname, DCS.ADDRESS as address, ");
        sql.append("DMG.METERNUMBER as meterno, ROUTE.PAONAME as routename, ");
        sql.append("ROW_NUMBER() over(order by SCHEDULE.PAONAME) rn ");
        sql.append("FROM DEVICEREADLOG DRL, DEVICEREADREQUESTLOG DRRL, DEVICEREADJOBLOG DRJL, ");
        sql.append("YUKONPAOBJECT PAO, DEVICECARRIERSETTINGS DCS, DEVICEMETERGROUP DMG, ");
        sql.append("YUKONPAOBJECT ROUTE, DEVICEROUTES DR, YUKONPAOBJECT SCHEDULE ");
        sql.append(" WHERE");
        appendCommonWhereClauses(sql);
        sql.append(")a where rn <=" + SCHEDULED_METER_READ_RECORDS_LIMIT);
        return sql;
    }

    private void appendCommonWhereClauses(SqlBuilder sql) {
        sql.append(" DRL.DEVICEID = PAO.PAOBJECTID ");
        sql.append(" AND DRL.DeviceReadRequestLogID = DRRL.DeviceReadRequestLogID ");
        sql.append(" AND DRRL.DeviceReadJobLogID = DRJL.DeviceReadJobLogID ");
        sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID ");
        sql.append(" AND DMG.DEVICEID = DCS.DEVICEID ");
        sql.append(" AND PAO.PAOBJECTID = DR.DEVICEID ");
        sql.append(" AND DR.ROUTEID = ROUTE.PAOBJECTID ");
        sql.append(" AND SCHEDULE.PAOBJECTID = DRJL.ScheduleID ");
        sql.append(" AND TIMESTAMP > ").appendArgument(getStartDate());
        sql.append(" AND TIMESTAMP <= ").appendArgument(getStopDate());

        // Use paoIDs in query if they exist
        if (getFilterModelType().equals(ReportFilter.SCHEDULE)) {
            if (getPaoIDs() != null && getPaoIDs().length > 0) {
                sql.append(" AND DRJL.ScheduleID IN (", getPaoIDs(), ") ");
            }
        } else if (getFilterModelType().equals(ReportFilter.METER) | getFilterModelType().equals(ReportFilter.DEVICE)) {
            if (getPaoIDs() != null && getPaoIDs().length > 0) {
                sql.append(" AND PAO.PAOBJECTID IN (", getPaoIDs(), ") ");
            }
        }

        if (getStatusCodeType() == StatusCodeType.ERROR_METER_READ_TYPE) {
            sql.append(" AND DRL.STATUSCODE > 0");
        } else if (getStatusCodeType() == StatusCodeType.SUCCESS_METER_READ_TYPE) {
            sql.append(" AND DRL.STATUSCODE = 0");
        }
    }

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof ScheduledMeterReadRow)
		{
			ScheduledMeterReadRow row = (ScheduledMeterReadRow)o;
			switch( columnIndex)
			{
				case SCHEDULE_NAME_COLUMN:
					return row.scheduleName;
				case SCHEDULE_START_TIME_COLUMN:
					return row.scheduleStartTime;
				case SCHEDULE_STOP_TIME_COLUMN:
					return row.scheduleStopTime;
				case REQUEST_COMMAND_COLUMN:
					return row.command;
				case REQUEST_ID_COLUMN:
					return row.requestID;
				case REQUEST_START_TIME_COLUMN:
					return row.requestStartTime;
				case REQUEST_STOP_TIME_COLUMN:
					return row.requestStopTime;
					
				case DEVICE_NAME_COLUMN:
					return row.paoName;
					
				case METER_NUMBER_COLUMN:
				    return row.meterNumber;
				    
				case PHYSICAL_ADDRESS_COLUMN:
				    return row.address;
				    
				case ROUTE_NAME_COLUMN:
					return row.routeName;
					
				case READ_TIMESTAMP_COLUMN:
					return row.readTimestamp;
					
				case STATUS_CODE_COLUMN:
					return row.statusCode; 
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
				SCHEDULE_NAME_STRING,
				SCHEDULE_START_TIME_STRING,
				SCHEDULE_STOP_TIME_STRING,
				REQUEST_COMMAND_STRING,
				REQUEST_ID_STRING,
				REQUEST_START_TIME_STRING,
				REQUEST_STOP_TIME_STRING,
				DEVICE_NAME_STRING,
				METER_NUMBER_STRING,
				PHYSICAL_ADDRESS_STRING,
				ROUTE_NAME_STRING,
				READ_TIMESTAMP_STRING,
				STATUS_CODE_STRING
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
					Date.class,
					Date.class,
					String.class,
					String.class,
					Date.class,
					Date.class,
					String.class,
					String.class,
					String.class,
					String.class,
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
				new ColumnProperties(0, 1, 100, null),
				new ColumnProperties(390, 1, 90, "MM/dd/yy HH:mm:ss"),
				new ColumnProperties(540, 1, 90, "MM/dd/yy HH:mm:ss"),
				new ColumnProperties(20, 1, 350, null),
				new ColumnProperties(390, 1, 90, null),	//not printed!!!
				new ColumnProperties(390, 1, 90, "MM/dd/yy HH:mm:ss"),
				new ColumnProperties(540, 1, 90, "MM/dd/yy HH:mm:ss"),
				
				new ColumnProperties(40, 1, 210, null),
				new ColumnProperties(250, 1, 70, null),
				new ColumnProperties(320, 1, 70, null),
				new ColumnProperties(390, 1, 150, null),
				new ColumnProperties(540, 1, 110, "MM/dd/yyyy HH:mm:ss"),
				new ColumnProperties(650, 1, 70, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
	    String title = "Scheduled Meter Read Status";
	    if( getStatusCodeType() == StatusCodeType.ERROR_METER_READ_TYPE)
			title += " - Errors";
		else if( getStatusCodeType() == StatusCodeType.SUCCESS_METER_READ_TYPE)
    	    title += " - Success";
		else
			title += " - All Attempts";
		return title;
	}
	
	/**
	 * Returns orderBy
	 * @return
	 */
	public OrderBy getOrderBy()
	{
		return orderBy;
	}

	/**
	 * Set orderBy 
	 * @param orderBy_
	 */
	public void setOrderBy(OrderBy orderBy_)
	{
		orderBy = orderBy_;
	}
	
	public void setOrderBy(int ordinal){
		for (OrderBy orderBy : OrderBy.values()) {
			if ( orderBy.ordinal() == ordinal) {
				setOrderBy(orderBy);
				break;
			}
		}
	}

	/**
	 * Returns groupBy
	 * @return
	 */
	public GroupBy getGroupBy()
	{
		return groupBy;
	}

	/**
	 * Set groupBy 
	 * @param orderBy_
	 */
	public void setGroupBy(GroupBy groupBy_)
	{
		groupBy = groupBy_;
	}
	
	public void setGroupBy(int ordinal){
		for (GroupBy groupBy : GroupBy.values()) {
			if ( groupBy.ordinal() == ordinal) {
				setGroupBy(groupBy);
				break;
			}
		}
	}
	
	public StatusCodeType getStatusCodeType()
	{
		return statusCodeType;
	}

	public void setStatusCodeType(StatusCodeType statusCodeType_) {
		if( statusCodeType != statusCodeType_) {	//reset the fields that depend on this type.
			   columnProperties = null;
			   columnNames = null;
			   columnTypes = null;
			}		
		this.statusCodeType = statusCodeType_;
	}
	
	public void setStatusCodeType(int ordinal){
		for (StatusCodeType statCodeType : StatusCodeType.values()) {
			if ( statCodeType.ordinal() == ordinal) {
				setStatusCodeType(statCodeType);
				break;
			}
		}
	}
	
	@Override
	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>Note : Maximum 150,000 results will be provided. " + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;		
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='title-header'>Scheduled Meter Read Status</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;

		String checkedStr = "checked";	//set to empty string after first iteration
		for (StatusCodeType statCodeType : StatusCodeType.values()) {
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='"+ATT_STATUS_CODE_TYPE +"' value='" + statCodeType.ordinal() + "' " +  
			 checkedStr + ">" + statCodeType.getDisplayName() + LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
			checkedStr = "";
		}		
		
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Group By</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		
		checkedStr = "checked";	//set to empty string after first iteration
		for (GroupBy groupBy : GroupBy.values()) {
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='"+ATT_GROUP_BY+"' value='" + groupBy.ordinal() + "' " +  
			 checkedStr + ">" + groupBy.getDisplayName() + LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
			checkedStr = "";
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Order By</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		checkedStr = "checked";	//set to empty string after first iteration
		for (OrderBy orderBy : OrderBy.values()) {
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='"+ATT_ORDER_BY+"' value='" + orderBy.ordinal() + "' " +  
			 checkedStr + ">" + orderBy.getDisplayName() + LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
			checkedStr = "";
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

/*
		html += "    <td valign='middle'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td>* Click this button to generate a list of missed meters that MACS can process.</td>"+ LINE_SEPARATOR;		
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='button' name='GenerateMissedList' value='Generate Missed List' onclick='document.reportForm.ACTION.value=\"GenerateMissedMeterList\";reportForm.submit();'>"+ LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
*/
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
			String param = req.getParameter(ATT_STATUS_CODE_TYPE);
			if( param != null)
				setStatusCodeType(Integer.valueOf(param).intValue());
			else
				setStatusCodeType(StatusCodeType.ALL_METER_READ_TYPE);	//default
			
			param = req.getParameter(ATT_ORDER_BY);
			if( param != null)
				setOrderBy(Integer.valueOf(param).intValue());
			else
				setOrderBy(OrderBy.ORDER_BY_DEVICE_NAME);	//default
			
			param = req.getParameter(ATT_GROUP_BY);
			if( param != null)
				setGroupBy(Integer.valueOf(param).intValue());
			else
				setGroupBy(GroupBy.GROUP_BY_SCHEDULE_REQUESTS);	//default
		}
	}
	
	public HashMap getTotals()
	{
		if (totals == null)
		{
			totals = new HashMap();
			for(int i = 0; i < getData().size(); i++)
			{
				String key = String.valueOf((getData().get(i)).statusCode);
				Integer initValue = (Integer)totals.get(key);
				if( initValue == null)
					initValue = new Integer(0);
	
				Integer newValue = new Integer(initValue.intValue() +1);
				totals.put(key, newValue);		
			}
		}
		return totals;
	}
}
