package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.spring.YukonSpringHook;

public class ScheduledMeterReadModel extends ReportModelBase<ScheduledMeterReadModel.ScheduledMeterReadRow>
{
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
	 */
	public void addDataRow(ResultSet rset) {
		try {
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
		catch(java.sql.SQLException e) {
			CTILogger.error(e);
		}
	}

	/**
	 * Build the SQL statement to retrieve MissedMeter data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement() {
		StringBuffer sql = new StringBuffer	("SELECT SCHEDULE.PAONAME, DRJL.STARTTIME, DRJL.STOPTIME, " +
											" DRRL.REQUESTID, DRRL.COMMAND, DRRL.STARTTIME, DRRL.STOPTIME, " +
											" DRL.TIMESTAMP, DRL.STATUSCODE, " +
											" PAO.PAONAME, DCS.ADDRESS, DMG.METERNUMBER, ROUTE.PAONAME " + 
											" FROM DEVICEREADLOG DRL, DEVICEREADREQUESTLOG DRRL, DEVICEREADJOBLOG DRJL, " + 
											" YUKONPAOBJECT PAO, DEVICECARRIERSETTINGS DCS, DEVICEMETERGROUP DMG, " +
											" YUKONPAOBJECT ROUTE, DEVICEROUTES DR, YUKONPAOBJECT SCHEDULE " + 
											" WHERE DRL.DEVICEID = PAO.PAOBJECTID " +
											" AND DRL.DeviceReadRequestLogID = DRRL.DeviceReadRequestLogID " +
											" AND DRRL.DeviceReadJobLogID = DRJL.DeviceReadJobLogID " +
											" AND PAO.PAOBJECTID = DMG.DEVICEID " +
											" AND DMG.DEVICEID = DCS.DEVICEID " + 
											" AND PAO.PAOBJECTID = DR.DEVICEID " +
											" AND DR.ROUTEID = ROUTE.PAOBJECTID " +
											" AND SCHEDULE.PAOBJECTID = DRJL.ScheduleID " + 
											" AND TIMESTAMP > ? AND TIMESTAMP <= ? ");
		
        final StringBuilder sb = new StringBuilder();
//		Use paoIDs in query if they exist
		if( getFilterModelType().equals(ReportFilter.SCHEDULE)) {
			if( getPaoIDs() != null && getPaoIDs().length > 0) {
                for (final int i : getPaoIDs()) {
                    sb.append(i + ",");
                }
                sql.append(" AND DRJL.ScheduleID IN (" + StringUtils.chop(sb.toString()) + ") ");
			}
		} else if (getFilterModelType().equals(ReportFilter.METER) | getFilterModelType().equals(ReportFilter.DEVICE)) {
			if( getPaoIDs() != null && getPaoIDs().length > 0) {
                for (final int i : getPaoIDs()) {
                    sb.append(i + ",");
                }
                sql.append(" AND PAO.PAOBJECTID IN (" + StringUtils.chop(sb.toString()) + ") ");
			}
		}
		
        final DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
        final String[] groups = getBillingGroups();
        
        if (groups != null && groups.length > 0) {
            sql.append(" AND PAO.PAOBJECTID IN (");
            
            List<String> deviceGroupNames = Arrays.asList(groups);
            Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(deviceGroupNames);
            String deviceGroupSqlInClause = deviceGroupService.getDeviceGroupSqlInClause(deviceGroups);
            sql.append(deviceGroupSqlInClause);
            
            sql.append(")");
        }
        
		if( getStatusCodeType() == StatusCodeType.ERROR_METER_READ_TYPE)
			sql.append(" AND DRL.STATUSCODE > 0");
		else if( getStatusCodeType() == StatusCodeType.SUCCESS_METER_READ_TYPE)
			sql.append(" AND DRL.STATUSCODE = 0");

		sql.append(" ORDER BY DRJL.STARTTIME, DRJL.STOPTIME, SCHEDULE.PAONAME ");
		
		//Order by the GroupBy option, this order is necessary for the Report to group additionally by Request
		if (getGroupBy() == GroupBy.GROUP_BY_SCHEDULE_REQUESTS) {
			sql.append(" , DRRL.STARTTIME, DRRL.STOPTIME " );
		}			

		//Order by the OrderBy option, this order is necessary for the ordering of the item band
		if (getOrderBy() == OrderBy.ORDER_BY_DEVICE_NAME)
			sql.append(" , PAO.PAONAME, TIMESTAMP " );
		else if (getOrderBy() == OrderBy.ORDER_BY_METER_NUMBER)
			sql.append(" , DMG.METERNUMBER, TIMESTAMP " );
		else if (getOrderBy() == OrderBy.ORDER_BY_ROUTE_NAME)
			sql.append(" , ROUTE.PAONAME, PAO.PAONAME, TIMESTAMP " );
		else if (getOrderBy() == OrderBy.ORDER_BY_STATUS_CODE)
			sql.append(" , MRL.STATUSCODE, PAO.PAONAME, TIMESTAMP " );

		return sql;
	}	
	
	@Override
	public void collectData() {
		//Reset all objects, new data being collected!
		setData(null);
		totals = null;
				
		StringBuffer sql = buildSQLStatement();
		CTILogger.info("SQL for MeterReadModel: " + sql.toString());
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		try {
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null ) {
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else {
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime() ));				
				CTILogger.info("START DATE >= " + getStartDate() + " - STOP DATE < " + getStopDate());
				rset = pstmt.executeQuery();
				
				while( rset.next()) {
					addDataRow(rset);
				}
			}
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error(e);
		}
		finally
		{
			try {
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e ) {
				CTILogger.error(e);
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
		html += "  <tr>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;		
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>Scheduled Meter Read Status</td>" +LINE_SEPARATOR;
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
		html += "          <td class='TitleHeader'>&nbsp;Group By</td>" +LINE_SEPARATOR;
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
		html += "          <td class='TitleHeader'>&nbsp;Order By</td>" +LINE_SEPARATOR;
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
