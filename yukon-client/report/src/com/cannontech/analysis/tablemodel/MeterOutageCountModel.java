package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

public class MeterOutageCountModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 6;

	/** Enum values for column representation */
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int POINT_NAME_COLUMN = 1;
	public final static int DATE_COLUMN = 2;
	public final static int TIME_COLUMN = 3;
	public final static int VALUE_COLUMN = 4;
    public final static int OUTAGE_COUNT_CALC_COLUMN = 5;

	/** String values for column representation */
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
	public final static String VALUE_STRING = "Reading";
    public final static String OUTAGE_COUNT_CALC_STRING = "Outage Count";

	/** A string for the title of the data */
	private static String title = "Outage Count Report";
	private static String incompleteDataTitle = "Outage Count - Incomplete Data Report";
    
    public final static String ATT_MINIMUM_DIFFERENCE = "minimumDifference";
    public final static String ATT_INCOMPLETE_DATA_REPORT= "incompleteDataReport";
    
    /** Temporary counters */
    private Integer previousReading = null;
    private Integer previousPaobjectId = null;
    private Integer totalOutageCount = 0;
    private Integer minimumOutageCount = 0;
    private boolean incompleteDataReport = false;
    /** Map of PaObjectId to GrandTotalOutageCount for each pao **/
    private Map<Integer, Integer> paoIdToGrandOutageTotalMap = new HashMap<Integer, Integer>();
    private Vector allData = new Vector();
    
    static public class OutageCountRow
    {	
    	/** Number of columns */
        private Integer paobjectId = null;
    	private String deviceName = null;
    	private String pointName = null;
    	private Date timestamp = null;
    	private Integer reading = null;
        private Integer outageCountCalc = null;

    	public OutageCountRow(Integer paobjectId, String deviceName, String pointName, Date timestamp, Integer reading, Integer outageCountCalc) {
    		this(paobjectId, deviceName, pointName, timestamp, reading);
    		this.outageCountCalc = outageCountCalc;
    	}
    	public OutageCountRow(Integer paobjectId, String deviceName, String pointName, Date timestamp, Integer reading) {
    	    this(paobjectId, deviceName, pointName);
    		this.timestamp = timestamp;
    		this.reading = reading;
    	}
    	public OutageCountRow(Integer paobjectId, String deviceName, String pointName) {
    		super();
    		this.paobjectId = paobjectId;
    		this.deviceName = deviceName;
    		this.pointName = pointName;
    	}
    }
	/**
	 * 
	 */
	public MeterOutageCountModel()
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
	public MeterOutageCountModel(Date start_, Date stop_)
	{
		super(start_, stop_);
		setFilterModelTypes(new ReportFilter[]{
                ReportFilter.METER,
                ReportFilter.DEVICE,
                ReportFilter.GROUPS}
                );
		
	}
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	@SuppressWarnings("unchecked")
    public void addDataRow(ResultSet rset)
	{
		try
		{
            OutageCountRow outageCountRow;
			String paoName = rset.getString(1);
            String pointName = rset.getString(2);
            Timestamp timestamp = rset.getTimestamp(3);
            Integer reading = new Integer(rset.getInt(4));
            Integer paobjectId = new Integer(rset.getInt(5));
            
            if( previousPaobjectId != null) {
                
                if( previousPaobjectId.intValue() != paobjectId.intValue()) {
                    // new device
                	if( timestamp == null )	//data from the outer join was returned
                		outageCountRow = new OutageCountRow(paobjectId, paoName, pointName);
                	else 
                		outageCountRow = new OutageCountRow(paobjectId, paoName, pointName, new Date(timestamp.getTime()), reading);

                	previousReading = reading;
                    previousPaobjectId = paobjectId;
                    totalOutageCount = 0; //reset the total                    
                }else {
                    //same device
                    int intervalDif = reading - previousReading;
                    totalOutageCount += intervalDif;
                    
                    outageCountRow = new OutageCountRow( paobjectId, paoName, pointName, 
                                              new Date(timestamp.getTime()),
                                              reading, intervalDif);
                    
                    //put the "new" grand total in the map every time we have more than one entry.
                    paoIdToGrandOutageTotalMap.put(paobjectId, totalOutageCount);
                    previousReading = reading;
                    previousPaobjectId = paobjectId;
                }
            }else {
                // first result
            	if( timestamp == null )	//data from the outer join was returned
            		outageCountRow = new OutageCountRow(paobjectId, paoName, pointName);
            	else 
            		outageCountRow = new OutageCountRow(paobjectId, paoName, pointName, new Date(timestamp.getTime()), reading);
            	
            	previousReading = reading;
                previousPaobjectId = paobjectId;
            	//Don't have an intervalDif to calculate...can't add it to lookup map
            }
			
			allData.add(outageCountRow);
		}
		catch(java.sql.SQLException e) {
		    CTILogger.error(e);
		}
	}
    
    /**
     * This method will load data based on the minimumDifference selected by the user.
     * The First AND Last entry for each Device pair will be added to the list.
     * Any intermediate entries will be added to the list IF their IntervalDifference is >= minimumDifferenc. 
     */
    @SuppressWarnings("unchecked")
    public void loadOutageCountData() {
        
        for (OutageCountRow outageCountRow : (Vector<OutageCountRow>)allData) {
            
            Integer total = paoIdToGrandOutageTotalMap.get(outageCountRow.paobjectId);
            if(total != null && 
                    total.intValue() >= getMinimumOutageCount().intValue()) {
                getData().add(outageCountRow);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public void loadIncompleteData() {

        for (OutageCountRow outageCountRow : (Vector<OutageCountRow>)allData) {
            
            Integer total = paoIdToGrandOutageTotalMap.get(outageCountRow.paobjectId);
            if(total == null ) {
                getData().add(outageCountRow);
            }
        }
    }
    
    @Override
    public String getHTMLOptionsTable()
    {
    	final StringBuilder sb = new StringBuilder();
        sb.append("<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("  <tr>" + LINE_SEPARATOR);
        sb.append("    <td valign='top'>" + LINE_SEPARATOR);		
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
    	sb.append("        <tr>" + LINE_SEPARATOR);
    	sb.append("          <td>Minimum Outage Count: <input type='text' name='" + ATT_MINIMUM_DIFFERENCE +"' value='0' size='3'>");
    	sb.append("          </td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);
        
        sb.append("    <td valign='middle'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='submit' name='" + ATT_INCOMPLETE_DATA_REPORT +"' value='Incomplete Data Report' onclick='document.reportForm.ACTION.value=\"DownloadReport\";'>"+ LINE_SEPARATOR);
        sb.append("          </td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td>* Click here for a report of meters with less than two archived readings for the timeframe selected.</td>"+ LINE_SEPARATOR);		
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);
        
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
		return sb.toString();
    }
    @Override
    public void setParameters( HttpServletRequest req )
    {
        super.setParameters(req);
        if( req != null)
        {
            String param = req.getParameter(ATT_MINIMUM_DIFFERENCE);
            if( param != null) {
                setMinimumOutageCount(Integer.valueOf(param).intValue());
            }
            
            param = req.getParameter(ATT_INCOMPLETE_DATA_REPORT);
            setIncompleteDataReport(param != null);
        }
    }
    
    /**
	 * Build the SQL statement to retrieve PowerFail data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT DISTINCT PAO.PAONAME, P.POINTNAME, RPH.TIMESTAMP, RPH.VALUE, PAO.PAOBJECTID " + 
			" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, " +
			" POINT P "  + (isIncompleteDataReport() ? " left outer " : "") + " join RAWPOINTHISTORY RPH "+
			" ON P.POINTID = RPH.POINTID AND RPH.TIMESTAMP > ? AND TIMESTAMP <= ? " + //this is the join clause
			" WHERE PAO.PAOBJECTID = DMG.DEVICEID "+
			" AND P.PAOBJECTID = PAO.PAOBJECTID " + 
			" AND P.POINTOFFSET = 20 " + 
			" AND P.POINTTYPE = 'PulseAccumulator' ");
			
		// RESTRICT BY DEVICE/METER PAOID (if any)
		String paoIdWhereClause = getPaoIdWhereClause("PAO.PAOBJECTID");
		if (!StringUtils.isBlank(paoIdWhereClause)) {
		    sql.append(" AND " + paoIdWhereClause);
		}
		
		// RESTRICT BY GROUPS (if any)
        final String[] groups = getBillingGroups();
        if (groups != null && groups.length > 0) {
            String deviceGroupSqlWhereClause = getGroupSqlWhereClause("PAO.PAOBJECTID");
            sql.append(" AND " + deviceGroupSqlWhereClause);
        }
					
		sql.append("ORDER BY PAO.PAONAME, P.POINTNAME, TIMESTAMP");
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
			
		catch( java.sql.SQLException e ) {
			CTILogger.error(e);
		}
		finally {
			SqlUtils.close(rset, pstmt, conn );
		}
		
		if (isIncompleteDataReport())
			loadIncompleteData();
		else
			loadOutageCountData();
		
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof OutageCountRow)
		{
			OutageCountRow outageCountRow = ((OutageCountRow)o); 
			switch( columnIndex)
			{
				case DEVICE_NAME_COLUMN:
					return outageCountRow.deviceName;
	
				case POINT_NAME_COLUMN:
					return outageCountRow.pointName;
	
				case DATE_COLUMN:
					return outageCountRow.timestamp;

				case TIME_COLUMN:
					return outageCountRow.timestamp;
					
				case VALUE_COLUMN:
					return outageCountRow.reading;
                    
                case OUTAGE_COUNT_CALC_COLUMN:
                    return outageCountRow.outageCountCalc;
                    
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
				POINT_NAME_STRING,
				DATE_STRING,
				TIME_STRING,
				VALUE_STRING,
                OUTAGE_COUNT_CALC_STRING
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
				Integer.class,
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
                new ColumnProperties(0, 1, 150, null),
                new ColumnProperties(150, 1, 120, null),
                new ColumnProperties(270, 1, 75, "MM/dd/yyyy"),
                new ColumnProperties(345, 1, 50, "HH:mm:ss"),
                new ColumnProperties(395, 1, 50, "#"),
                new ColumnProperties(445, 1, 70, "#")
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString() {
		if( isIncompleteDataReport())
			return incompleteDataTitle;
		return title;
	}

	public Integer getMinimumOutageCount() {
		return minimumOutageCount;
	}
	
	public void setMinimumOutageCount(Integer minimumOutageCount) {
		this.minimumOutageCount = minimumOutageCount;
	}
	
	public boolean isIncompleteDataReport() {
		return incompleteDataReport;
	}
	
	public void setIncompleteDataReport(boolean incompleteDataReport) {
		this.incompleteDataReport = incompleteDataReport;
	}
}