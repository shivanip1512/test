package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;

public class MeterUsageModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;

	/** Enum values for column representation */
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int METER_NUMBER_COLUMN = 1;
	public final static int DATE_COLUMN = 2;
	public final static int TIME_COLUMN = 3;
	public final static int VALUE_COLUMN = 4;
    public final static int PREV_VALUE_COLUMN = 5;
    public final static int TOTAL_USAGE_COLUMN = 6;

	/** String values for column representation */
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String METER_NUMBER_STRING = "Meter Number";
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
	public final static String VALUE_STRING = "Reading";
    public final static String PREV_VALUE_STRING = "Prev Reading";
    public final static String TOTAL_USAGE_STRING = "Usage";

	/** A string for the title of the data */
	private static String title = "Meter Usage Report";
    
    /** Temporary counters */
    private Double previousReading = null;
    private String previousDevice = null;

    /** Class fields */
    private boolean excludeDisabledDevices = false; 
    
    //servlet attributes/parameter strings
    private static final String ATT_EXCLUDE_DISABLED_DEVICES = "excludeDisabledDevices";

    static public class MeterUsageRow {
        public String deviceName;
        public String meterNumber;
        public Timestamp timestamp;
        public Double reading;
        public Double previousReading;
        public Double totalUsage;
    }
    
	/**
	 * 
	 */
	public MeterUsageModel()
	{
		super();
		setFilterModelTypes(new ReportFilter[]{
				ReportFilter.METER,
                ReportFilter.DEVICE,
				ReportFilter.GROUPS}
				);
	}
	
	/**
	 * 
	 */
	public MeterUsageModel(Date start_, Date stop_)
	{
		super(start_, stop_);
	}
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
    public void addDataRow(ResultSet rset) throws SQLException
    {
	    MeterUsageRow meterUsage = new MeterUsageRow();
	    meterUsage.deviceName = rset.getString(1);
	    meterUsage.meterNumber = rset.getString(2);
	    meterUsage.timestamp = rset.getTimestamp(3);
	    meterUsage.reading = new Double(rset.getInt(4));

	    if(previousDevice != null) {
	        if( !meterUsage.deviceName.equals(previousDevice)) {
	            previousReading = null;
	        }
	    }

	    meterUsage.previousReading = previousReading;

	    if(previousReading != null) {
	        meterUsage.totalUsage = meterUsage.reading - previousReading;
	    }else {
	        meterUsage.totalUsage = null;
	    }

	    getData().add(meterUsage);
	    previousReading = meterUsage.reading;
	    previousDevice = meterUsage.deviceName;
    }
    
    /**
	 * Build the SQL statement to retrieve PowerFail data.
	 * @return StringBuffer  an sqlstatement
	 */
	public SqlFragmentSource buildSQLStatement()
	{
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DISTINCT PAO.PAONAME,  DMG.METERNUMBER, RPH.TIMESTAMP, RPH.VALUE");
		sql.append(" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, ");
		sql.append(" POINT P join RAWPOINTHISTORY RPH ");
		sql.append(" ON P.POINTID = RPH.POINTID AND RPH.TIMESTAMP > ").appendArgument(getStartDate());
		sql.append(" AND TIMESTAMP <= ").appendArgument(getStopDate()); //this is the join clause
		sql.append(" WHERE PAO.PAOBJECTID = DMG.DEVICEID ");
		sql.append(" AND P.PAOBJECTID = PAO.PAOBJECTID ");
		sql.append(" AND P.POINTOFFSET = 1 ");
		sql.append(" AND P.POINTTYPE = 'PulseAccumulator' ");
		if (excludeDisabledDevices) {
		    sql.append(" AND PAO.DISABLEFLAG").eq("N");
		}
			
		// Use paoIDs in query if they exist			
		if( getPaoIDs() != null && getPaoIDs().length > 0) {
			sql.append(" AND PAO.PAOBJECTID IN (", getPaoIDs(), ")");
		} 	
					
        final String[] groups = getBillingGroups();
        
		if (groups != null && groups.length > 0) {
            SqlFragmentSource deviceGroupSqlWhereClause = getGroupSqlWhereClause("PAO.PAOBJECTID");
            sql.append(" AND ", deviceGroupSqlWhereClause);
		}

		sql.append("ORDER BY PAO.PAONAME, TIMESTAMP");
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
		if ( o instanceof MeterUsageRow)
		{
			MeterUsageRow meterUsage = ((MeterUsageRow)o); 
			switch( columnIndex)
			{
				case DEVICE_NAME_COLUMN:
					return meterUsage.deviceName;
	
				case METER_NUMBER_COLUMN:
					return meterUsage.meterNumber;

				case DATE_COLUMN:
					return meterUsage.timestamp;

				case TIME_COLUMN:
					return meterUsage.timestamp;
					
				case VALUE_COLUMN:
					return meterUsage.reading;
					
				case PREV_VALUE_COLUMN:
					return meterUsage.previousReading;
                    
                case TOTAL_USAGE_COLUMN:
                    return meterUsage.totalUsage;
                    
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
				METER_NUMBER_STRING,
				DATE_STRING,
				TIME_STRING,
				VALUE_STRING,
				PREV_VALUE_STRING,
				TOTAL_USAGE_STRING
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
                Double.class,
                Double.class
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
                new ColumnProperties(180, 1, 70, null),
                new ColumnProperties(250, 1, 60, "MM/dd/yyyy"),
                new ColumnProperties(310, 1, 40, "HH:mm:ss"),
                new ColumnProperties(350, 1, 70, "0.00#"),
                new ColumnProperties(420, 1, 70, "0.00#"),
                new ColumnProperties(490, 1, 70, "0.00#")
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString() {
		return title;
	}
	
	@Override
    public String getHTMLOptionsTable()
    {
	    final StringBuilder sb = new StringBuilder();
	    sb.append("<table style='padding: 10px;' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("  <tr>" + LINE_SEPARATOR);

        sb.append("    <td valign='top'>" + LINE_SEPARATOR);        
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td valign='top' class='TitleHeader'>Disabled Devices</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='checkbox' name='" + ATT_EXCLUDE_DISABLED_DEVICES +"' value='true'> Exclude Disabled Devices" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);
        
        sb.append("  </tr>" + LINE_SEPARATOR);
        sb.append("</table>" + LINE_SEPARATOR);
        
	    return sb.toString();
    }
    
    @Override
    public void setParameters( HttpServletRequest req )
    {
        super.setParameters(req);
        if( req != null)
        {
            String param = req.getParameter(ATT_EXCLUDE_DISABLED_DEVICES);
            if( param != null) {
                excludeDisabledDevices = CtiUtilities.isTrue(param);
            }
        }       
    }
}
