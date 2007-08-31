package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.spring.YukonSpringHook;

public class MeterUsageModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 8;

	/** Enum values for column representation */
	public final static int GROUP_NAME_COLUMN = 0;
	public final static int DEVICE_NAME_COLUMN = 1;
	public final static int METER_NUMBER_COLUMN = 2;
	public final static int DATE_COLUMN = 3;
	public final static int TIME_COLUMN = 4;
	public final static int VALUE_COLUMN = 5;
    public final static int PREV_VALUE_COLUMN = 6;
    public final static int TOTAL_USAGE_COLUMN = 7;

	/** String values for column representation */
	public final static String GROUP_NAME_STRING = "Group";
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
    private String previousGroup = null;
    
    static public class MeterUsageRow {
        public String groupName;
        public String deviceName;
        public String meterNumber;
        public Timestamp timestamp;
        public Double reading;
        public Double previousReading;
        public Double totalUsage;
    }
    
    private Map<Integer,DeviceGroup> deviceGroupsMap = new HashMap<Integer,DeviceGroup>();
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
	 */
	@SuppressWarnings("unchecked")
    public void addDataRow(ResultSet rset)
	{
		try
		{
            final DeviceGroupEditorDao deviceGroupEditorDao = YukonSpringHook.getBean("deviceGroupEditorDao", DeviceGroupEditorDao.class);
            
            MeterUsageRow meterUsage;
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
            
            meterUsage = new MeterUsageRow();
            meterUsage.groupName = deviceGroup.getFullName();
            meterUsage.deviceName = rset.getString(2);
            meterUsage.meterNumber = rset.getString(3);
            meterUsage.timestamp = rset.getTimestamp(4);
            meterUsage.reading = new Double(rset.getInt(5));
            
            if(previousGroup != null && previousDevice != null) {
                if(!groupName.equalsIgnoreCase(previousGroup) || 
                		!meterUsage.deviceName.equals(previousDevice)) {
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
            previousGroup = groupName;
        }
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}
    
    /**
	 * Build the SQL statement to retrieve PowerFail data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT DGM.DEVICEGROUPID, PAO.PAONAME,  DMG.METERNUMBER, RPH.TIMESTAMP, RPH.VALUE" + 
			" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, DEVICEGROUPMEMBER DGM, " +
			" POINT P join RAWPOINTHISTORY RPH "+
			" ON P.POINTID = RPH.POINTID AND RPH.TIMESTAMP > ? AND TIMESTAMP <= ? " + //this is the join clause
			" WHERE PAO.PAOBJECTID = DMG.DEVICEID "+
            " AND P.PAOBJECTID = DGM.YUKONPAOID " +
			" AND P.PAOBJECTID = PAO.PAOBJECTID " + 
			" AND P.POINTOFFSET = 1 " + 
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
				case GROUP_NAME_COLUMN:
					return meterUsage.groupName;
		
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
				GROUP_NAME_STRING,
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
                new ColumnProperties(0, 1, 300, null),
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
}
