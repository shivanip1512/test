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
import com.cannontech.analysis.data.device.PowerFail;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.spring.YukonSpringHook;

public class PowerFailModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;

	/** Enum values for column representation */
	public final static int GROUP_NAME_COLUMN = 0;
	public final static int DEVICE_NAME_COLUMN = 1;
	public final static int POINT_NAME_COLUMN = 2;
	public final static int DATE_COLUMN = 3;
	public final static int TIME_COLUMN = 4;
	public final static int POWER_FAIL_COUNT_COLUMN = 5;
    public final static int INTERVAL_DIFFERENCE_COLUMN = 6;
    public final static int TOTAL_DIFFERENCE_COLUMN = 7;

	/** String values for column representation */
	public final static String GROUP_NAME_STRING = "Group";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
	public final static String POWER_FAIL_COUNT_STRING = "PF Count";
    public final static String INTERVAL_DIFFERENCE_STRING = "Interval Diff";
    public final static String TOTAL_DIFFERENCE_STRING = "Total Diff";

	/** A string for the title of the data */
	private static String title = "Power Fail Count By Groups";
    
    public final static String ATT_MINIMUM_DIFFERENCE = "minimumDifference";
    
    /** Temporary counters */
    private Integer previousCount = null;
    private String previousDevice = null;
    private String previousGroup = null;
    private Integer totalDifference = 0;
    private Integer minimumDifference = 0;
    private Vector tempData = new Vector();
    
    
    private Map<Integer,DeviceGroup> deviceGroupsMap = new HashMap<Integer,DeviceGroup>();
	/**
	 * 
	 */
	public PowerFailModel()
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
	public PowerFailModel(Date start_, Date stop_)
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
            
            PowerFail powerFail;
            Integer groupId = rset.getInt(1);
            DeviceGroup deviceGroup = deviceGroupsMap.get(groupId);
            if( deviceGroup == null){
                deviceGroup = deviceGroupEditorDao.getGroupById(groupId);
                deviceGroupsMap.put(groupId, deviceGroup);
            }
            String groupName = deviceGroup.getFullName();
			String paoName = rset.getString(2);
            String pointName = rset.getString(3);
            Timestamp timestamp = rset.getTimestamp(4);
            Integer powerFailCount = new Integer(rset.getInt(5));
            
            if( previousGroup != null && previousDevice != null) {
                
                if( !previousGroup.equals(groupName) || !previousDevice.equals(paoName)) {
                    // new device or new collection grp or both
                    powerFail = new PowerFail(groupName, paoName, pointName, new Date(timestamp.getTime()), powerFailCount, null, 0);
                    previousCount = powerFailCount;
                    previousDevice = paoName;
                    previousGroup = groupName;
                    totalDifference = 0;
                }else {
                    //same device and collection grp
                    int intervalDif = powerFailCount - previousCount;
                    totalDifference = totalDifference + intervalDif;
                    powerFail = new PowerFail(groupName, paoName, 
                                              pointName, 
                                              new Date(timestamp.getTime()),
                                              powerFailCount, 
                                              new Integer(powerFailCount - previousCount), 
                                              totalDifference);
                    previousCount = powerFailCount;
                    previousGroup = groupName;
                    previousDevice = paoName;
                }
            }else {
                // first result
                powerFail = new PowerFail(groupName, paoName, pointName, new Date(timestamp.getTime()), powerFailCount, null, totalDifference);
                previousCount = powerFailCount;
                previousGroup = groupName;
                previousDevice = paoName;
            }
			

			tempData.add(powerFail);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}
    
    /**
     * This method will load data based on the minimumDifference selected by the user.
     * The First AND Last entry for each Group/Device pair will be added to the list.
     * Any intermediate entries will be added to the list IF their IntervalDifference is >= minimumDifferenc. 
     */
    @SuppressWarnings("unchecked")
    public void loadTempData() {
        
        PowerFail currentPF = null;
        for (int i = 0; i < tempData.size(); i ++) {
            
            currentPF = (PowerFail)tempData.get(i);
            boolean addEntry = false;
            
            if(i+1 < tempData.size()) {
                
                PowerFail nextPF = (PowerFail)tempData.get(i + 1);
                
                //New Group/Device unique entry
                if (!nextPF.getGroupName().equalsIgnoreCase(currentPF.getGroupName()) || !nextPF.getDeviceName().equalsIgnoreCase(currentPF.getDeviceName()))
                    addEntry = true;
                //Compare currentEntry with nextEntry
                else if(currentPF.getIntervalDifference() == null || currentPF.getIntervalDifference() >= minimumDifference)
                   addEntry = true;
                    
            }
            if( addEntry)
                getData().add(currentPF);
        }
        
        //Always add the last entry.
        if (currentPF != null)
            getData().add(currentPF);
    
    }
    
    @Override
    public String getHTMLOptionsTable()
    {
        String html = "";
        html += "      <table>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td>Minimum PF Difference: <input type='text' name='" + ATT_MINIMUM_DIFFERENCE +"' value='0' size='3'>";
        html += "          </td>" + LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
        html += "      </table>" + LINE_SEPARATOR;
        return html;
    }
    @Override
    public void setParameters( HttpServletRequest req )
    {
        super.setParameters(req);
        if( req != null)
        {
            String param = req.getParameter(ATT_MINIMUM_DIFFERENCE);
            if( param != null) {
                minimumDifference = (Integer.valueOf(param).intValue());
            }
        }
    }
    
    /**
	 * Build the SQL statement to retrieve PowerFail data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT DGM.DEVICEGROUPID, PAO.PAONAME, P.POINTNAME, RPH.TIMESTAMP, RPH.VALUE " + 
			" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, POINT P, RAWPOINTHISTORY RPH, "+
            " DEVICEGROUPMEMBER DGM " +
			" WHERE PAO.PAOBJECTID = DMG.DEVICEID  AND P.POINTID = RPH.POINTID"+
            " AND P.PAOBJECTID = DGM.YUKONPAOID " +
			" AND P.PAOBJECTID = PAO.PAOBJECTID AND P.POINTOFFSET = 20 ");
			
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

			 
			sql.append(" AND RPH.TIMESTAMP > ? AND TIMESTAMP <= ? " +
                       " ORDER BY DGM.DEVICEGROUPID, PAO.PAONAME, P.POINTNAME, TIMESTAMP");
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
        loadTempData();
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof PowerFail)
		{
			PowerFail meter = ((PowerFail)o); 
			switch( columnIndex)
			{
				case GROUP_NAME_COLUMN:
					return meter.getGroupName();
		
				case DEVICE_NAME_COLUMN:
					return meter.getDeviceName();
	
				case POINT_NAME_COLUMN:
					return meter.getPointName();
	
				case DATE_COLUMN:
					return meter.getTimestamp();

				case TIME_COLUMN:
					return meter.getTimestamp();
					
				case POWER_FAIL_COUNT_COLUMN:
					return meter.getPowerFailCount();
                    
                case INTERVAL_DIFFERENCE_COLUMN:
                    return meter.getIntervalDifference();
                    
                case TOTAL_DIFFERENCE_COLUMN:
                    return meter.getTotalDifference();
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
				POWER_FAIL_COUNT_STRING,
                INTERVAL_DIFFERENCE_STRING,
                TOTAL_DIFFERENCE_STRING
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
                new ColumnProperties(0, 1, 120, null),
                new ColumnProperties(120, 1, 100, null),
                new ColumnProperties(220, 1, 75, "MM/dd/yyyy"),
                new ColumnProperties(295, 1, 50, "HH:mm:ss"),
                new ColumnProperties(345, 1, 50, "#"),
                new ColumnProperties(395, 1, 70, "#"),
                new ColumnProperties(465, 1, 50, "#"),
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
}
