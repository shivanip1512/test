package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.spring.YukonSpringHook;

public class MeterOutageCountModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;

	/** Enum values for column representation */
	public final static int GROUP_NAME_COLUMN = 0;
	public final static int DEVICE_NAME_COLUMN = 1;
	public final static int POINT_NAME_COLUMN = 2;
	public final static int DATE_COLUMN = 3;
	public final static int TIME_COLUMN = 4;
	public final static int VALUE_COLUMN = 5;
    public final static int OUTAGE_COUNT_CALC_COLUMN = 6;

	/** String values for column representation */
	public final static String GROUP_NAME_STRING = "Group";
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
    private Integer previousCount = null;
    private String previousDevice = null;
    private String previousGroup = null;
    private Integer totalOutageCount = 0;
    private Integer minimumOutageCount = 0;
    private boolean incompleteDataReport = false;
    private Vector allData = new Vector();
    
    private Map<Integer,DeviceGroup> deviceGroupsMap = new HashMap<Integer,DeviceGroup>();
    
    static public class OutageCountRow
    {	
    	/** Number of columns */
    	private String groupName  = null;
    	private String deviceName = null;
    	private String pointName = null;
    	private Date timestamp = null;
    	private Integer reading = null;
        private Integer outageCountCalc = null;
        private Integer outageCountTotal = null;

    	public OutageCountRow(String groupName, String deviceName, String pointName, Date timestamp, Integer reading, Integer outageCountCalc, Integer outageCountTotal) {
    		super();
    		this.groupName = groupName;
    		this.deviceName = deviceName;
    		this.pointName = pointName;
    		this.timestamp = timestamp;
    		this.reading = reading;
    		this.outageCountCalc = outageCountCalc;
    		this.outageCountTotal = outageCountTotal;
    	}
    	public OutageCountRow(String groupName, String deviceName, String pointName, Date timestamp, Integer reading) {
    		super();
    		this.groupName = groupName;
    		this.deviceName = deviceName;
    		this.pointName = pointName;
    		this.timestamp = timestamp;
    		this.reading = reading;
    	}
    	public OutageCountRow(String groupName, String deviceName, String pointName) {
    		super();
    		this.groupName = groupName;
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
            final DeviceGroupEditorDao deviceGroupEditorDao = YukonSpringHook.getBean("deviceGroupEditorDao", DeviceGroupEditorDao.class);
            
            OutageCountRow outageCountRow;
            Integer groupId = rset.getInt(1);
            DeviceGroup deviceGroup = deviceGroupsMap.get(groupId);
            if( deviceGroup == null){
                deviceGroup = deviceGroupEditorDao.getGroupById(groupId);
                deviceGroupsMap.put(groupId, deviceGroup);
            }
            String groupName = deviceGroup.getFullName();
            
            final String[] groups = getBillingGroups();
            if (groups != null && groups.length > 0) {  //Device Groups were specified, only include groups that were selected 
                List<String> deviceGroupNames = Arrays.asList(groups);
                if( !deviceGroupNames.contains(groupName) )
                    return;
            }
            
			String paoName = rset.getString(2);
            String pointName = rset.getString(3);
            Timestamp timestamp = rset.getTimestamp(4);
            Integer reading = new Integer(rset.getInt(5));
            
            if( previousGroup != null && previousDevice != null) {
                
                if( !previousGroup.equals(groupName) || !previousDevice.equals(paoName)) {
                    // new device or new collection grp or both
                	if( timestamp == null )	//data from the outer join was returned
                		outageCountRow = new OutageCountRow(groupName, paoName, pointName);
                	else 
                		outageCountRow = new OutageCountRow(groupName, paoName, pointName, new Date(timestamp.getTime()), reading);

                	updatePreviousValues(reading, groupName, paoName);
                    resetTotalOutageCount();
                    
                }else {
                    //same device and collection grp
                    int intervalDif = reading - previousCount;
                    setTotalOutageCount(getTotalOutageCount() + intervalDif);
                    
                    outageCountRow = new OutageCountRow(groupName, paoName, 
                                              pointName, 
                                              new Date(timestamp.getTime()),
                                              reading, 
                                              new Integer(reading - previousCount), 
                                              totalOutageCount);
                    updatePreviousValues(reading, groupName, paoName);
                }
            }else {
                // first result
            	if( timestamp == null )	//data from the outer join was returned
            		outageCountRow = new OutageCountRow(groupName, paoName, pointName);
            	else 
            		outageCountRow = new OutageCountRow(groupName, paoName, pointName, new Date(timestamp.getTime()), reading);
            	
            	updatePreviousValues(reading, groupName, paoName);
                resetTotalOutageCount();
            }
			
			allData.add(outageCountRow);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void updatePreviousValues(Integer reading, String groupName, String paoName) {
        previousCount = reading;
        previousGroup = groupName;
        previousDevice = paoName;
	}
    private void resetTotalOutageCount() {
    	setTotalOutageCount(0);
    }
    
    public void setTotalOutageCount(Integer totalOutageCount) {
		this.totalOutageCount = totalOutageCount;
	}
    
    public Integer getTotalOutageCount() {
		return totalOutageCount;
	}
    
    /**
     * This method will load data based on the minimumDifference selected by the user.
     * The First AND Last entry for each Group/Device pair will be added to the list.
     * Any intermediate entries will be added to the list IF their IntervalDifference is >= minimumDifferenc. 
     */
    @SuppressWarnings("unchecked")
    public void loadOutageCountData() {
        
    	Vector<OutageCountRow> deviceData = new Vector<OutageCountRow>();
        for (int i = 0; i < allData.size(); i ++) {
            
        	OutageCountRow outageCountRow = (OutageCountRow)allData.get(i);
            deviceData.add(outageCountRow);
            boolean validate = false;
            
            if(i+1 < allData.size()) {                
                OutageCountRow nextOutageCountRow = (OutageCountRow)allData.get(i + 1);
                //New Group/Device unique entry
                if (!nextOutageCountRow.groupName.equalsIgnoreCase(outageCountRow.groupName) || 
                	!nextOutageCountRow.deviceName.equalsIgnoreCase(outageCountRow.deviceName) )
                		validate = true;
            } else	{// last one
            	validate = true;
            }
            
            if (validate) {

            	if(outageCountRow.outageCountTotal != null && 
                		outageCountRow.outageCountTotal >= getMinimumOutageCount()) {
            		//We have more than one entry so we can calculate outage counts
            		getData().addAll(deviceData);
            	}
        		deviceData.clear();
            }
        }
        
        //Always add the last entry.
//        if (currentPF != null)
//            getData().add(currentPF);
    
    }
    
    @SuppressWarnings("unchecked")
    public void loadIncompleteData() {
        
    	Vector<OutageCountRow> deviceData = new Vector<OutageCountRow>();
        for (int i = 0; i < allData.size(); i ++) {
            
        	OutageCountRow outageCountRow = (OutageCountRow)allData.get(i);
            deviceData.add(outageCountRow);
            boolean validate = false;
            
            if(i+1 < allData.size()) {                
                OutageCountRow nextOutageCountRow = (OutageCountRow)allData.get(i + 1);
                //New Group/Device unique entry
                if (!nextOutageCountRow.groupName.equalsIgnoreCase(outageCountRow.groupName) || 
                	!nextOutageCountRow.deviceName.equalsIgnoreCase(outageCountRow.deviceName) )
                		validate = true;
            } else	{// last one
            	validate = true;
            }
            
            if (validate) {

            	if(outageCountRow.outageCountTotal == null ) {
            		//We have more than one entry so we can calculate outage counts
            		getData().addAll(deviceData);
            	}
        		deviceData.clear();
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
        sb.append("          <td>* Click here for a report of meters having one or less archived reading for specified timeframe.</td>"+ LINE_SEPARATOR);		
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
		StringBuffer sql = new StringBuffer	("SELECT DGM.DEVICEGROUPID, PAO.PAONAME, P.POINTNAME, RPH.TIMESTAMP, RPH.VALUE " + 
			" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, DEVICEGROUPMEMBER DGM, " +
			" POINT P "  + (isIncompleteDataReport() ? " left outer " : "") + " join RAWPOINTHISTORY RPH "+
			" ON P.POINTID = RPH.POINTID AND RPH.TIMESTAMP > ? AND TIMESTAMP <= ? " + //this is the join clause
			" WHERE PAO.PAOBJECTID = DMG.DEVICEID "+
            " AND P.PAOBJECTID = DGM.YUKONPAOID " +
			" AND P.PAOBJECTID = PAO.PAOBJECTID " + 
			" AND P.POINTOFFSET = 20 " + 
			" AND P.POINTTYPE = 'PulseAccumulator' ");
			
//		Use paoIDs in query if they exist			
		if( getPaoIDs() != null && getPaoIDs().length > 0)
		{
			sql.append(" AND PAO.PAOBJECTID IN (" + getPaoIDs()[0]);
			for (int i = 1; i < getPaoIDs().length; i++)
				sql.append(", " + getPaoIDs()[i]);
			sql.append(") ");
		} 	
					
        final DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
        final String[] groups = getBillingGroups();
        
		if (groups != null && groups.length > 0) {
			sql.append(" AND PAO.PAOBJECTID IN (");
            
            List<String> deviceGroupNames = Arrays.asList(groups);
            Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(deviceGroupNames);
            String deviceGroupSqlInClause = deviceGroupService.getDeviceGroupSqlInClause(deviceGroups);
            sql.append(deviceGroupSqlInClause);
            
            sql.append(") ");
		}

			 
			sql.append("ORDER BY DGM.DEVICEGROUPID, PAO.PAONAME, P.POINTNAME, TIMESTAMP");
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
			
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
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
				case GROUP_NAME_COLUMN:
					return outageCountRow.groupName;
		
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
				GROUP_NAME_STRING,
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
                new ColumnProperties(0, 1, 200, null),
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
