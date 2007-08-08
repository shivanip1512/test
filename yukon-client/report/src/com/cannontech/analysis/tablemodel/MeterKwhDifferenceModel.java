package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.spring.YukonSpringHook;

public class MeterKwhDifferenceModel extends BareDatedReportModelBase<MeterKwhDifferenceModel.ModelRow> {
    
    private static String title = "Meter Kwh Difference Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> deviceIds;
    private Set<Integer> deviceNames;
    private Set<String> groupNames;
    private Double previousKwh = null;
    private String previousGroup = null;
    private String previousDevice = null;
    
    static public class ModelRow {
        public String groupName;
        public String deviceName;
        public String meterNumber;
        public Timestamp date;
        public Double kWh;
        public Double previousKwh;
        public Double difference;
    }
    
    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        final DeviceGroupEditorDao deviceGroupEditorDao = YukonSpringHook.getBean("deviceGroupEditorDao", DeviceGroupEditorDao.class);
        final Map<Integer,DeviceGroup> deviceGroupsMap = new HashMap<Integer,DeviceGroup>();

        Timestamp[] dateRange = {new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime())};
        jdbcOps.query(sql.toString(), dateRange, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                MeterKwhDifferenceModel.ModelRow row = new MeterKwhDifferenceModel.ModelRow();

                Integer groupId = rs.getInt("grp");
                DeviceGroup deviceGroup = deviceGroupsMap.get(groupId);
                if( deviceGroup == null){
                    deviceGroup = deviceGroupEditorDao.getGroupById(groupId);
                    deviceGroupsMap.put(groupId, deviceGroup);
                }
                    
                String groupName = deviceGroup.getFullName();
                row.groupName = groupName;
                String deviceName = rs.getString("deviceName");
                row.deviceName = rs.getString("deviceName");
                row.meterNumber = rs.getString("meterNumber");
                row.date = rs.getTimestamp("date");
                Double kWh = rs.getDouble("kWh"); 
                row.kWh = kWh;
                
                if(previousGroup != null && previousDevice != null) {
                    if(!groupName.equalsIgnoreCase(previousGroup) || !deviceName.equals(previousDevice)) {
                        previousKwh = null;
                    }
                }
                
                row.previousKwh = previousKwh;
                
                if(previousKwh != null) {
                    row.difference = kWh - previousKwh;
                }else {
                    row.difference = null;
                }
                data.add(row);
                previousKwh = kWh;
                previousDevice = deviceName;
                previousGroup = groupName;
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("SELECT DGM.DEVICEGROUPID as grp, PAO.PAOBJECTID, PAO.PAONAME as deviceName, PAO.TYPE, DMG.METERNUMBER as meterNumber, "); 
        sql.append("P.POINTNAME, TIMESTAMP as date, VALUE as kwh ");
        sql.append("FROM RAWPOINTHISTORY RPH, POINT P, YUKONPAOBJECT PAO, ");
        sql.append("DEVICEGROUPMEMBER DGM, DEVICEMETERGROUP DMG ");
        sql.append("WHERE P.POINTID = RPH.POINTID ");
        sql.append("AND PAO.PAOBJECTID = DMG.DEVICEID ");
        sql.append("AND PAO.PAOBJECTID = DGM.YUKONPAOID ");
        sql.append("AND P.PAOBJECTID = PAO.PAOBJECTID ");
        sql.append("AND TIMESTAMP > ? ");
        sql.append("AND TIMESTAMP <= ? ");
        sql.append("AND P.POINTTYPE = 'PulseAccumulator' ");
        sql.append("AND P.POINTOFFSET = 1 ");
        sql.append(" ");
        
 
        String result = null;
        
        if(deviceIds != null && !deviceIds.isEmpty()) {
            result = "DMG.DEVICEID in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(deviceIds);
            result += wheres;
            result += " ) ";
        }
        else if(deviceNames != null && !deviceNames.isEmpty()) {
            result = "PAO.PAOBJECTID in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(deviceNames);
            result += wheres;
            result += " ) ";
        } else if (groupNames != null && !groupNames.isEmpty()) {
			
        	final DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
	        result = " PAO.PAOBJECTID IN (";
	        
            Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(groupNames);
            String deviceGroupSqlInClause = deviceGroupService.getDeviceGroupSqlInClause(deviceGroups);
            result += deviceGroupSqlInClause;
            
            result += ") ";
		}
        
        if (result != null) {
            sql.append(" and ");
            sql.append(result);
        }
        
        return sql;
    }

    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getTitle() {
        return title;
    }
    
    public void setDeviceIdsFilter(Set<Integer> deviceIds) {
        this.deviceIds = deviceIds;
    }
    
    public void setDeviceNamesFilter(Set<Integer> deviceNameIds) {
        this.deviceNames = deviceNameIds;
    }
    
    public void setGroupNames(Set<String> groupNames) {
		this.groupNames = groupNames;
	}
    
}
