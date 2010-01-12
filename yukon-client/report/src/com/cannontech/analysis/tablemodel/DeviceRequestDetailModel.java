package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DeviceDao;
import com.google.common.collect.Lists;

public class DeviceRequestDetailModel extends BareDatedReportModelBase<DeviceRequestDetailModel.ModelRow> {

    private Logger log = YukonLogManager.getLogger(DeviceRequestDetailModel.class);

    // dependencies
    private DeviceGroupService deviceGroupService;
    private DeviceDao deviceDao;
    private SimpleJdbcOperations simpleJdbcTemplate;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private List<String> groupNames;
    private List<String> deviceNames;
    private boolean lifetime = false;

    private DeviceGroupEditorDao deviceGroupEditorDao;

    static public class ModelRow {
        public String deviceName;
        public String type;
        public String route;
        public Integer requests;
        public String success;
        public Integer numberOfSuccesses;
        public String successPercent;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public String getTitle() {
        return "Device Request Detail Report";
    }

    public int getRowCount() {
        return data.size();
    }
    
    private List<Integer> getDeviceIds(){
        List<Integer> deviceIds = Lists.newArrayList();
        if(deviceNames != null && !deviceNames.isEmpty()){
            for(String name : deviceNames){
                deviceIds.add(deviceDao.getYukonDeviceObjectByName(name).getDeviceId());
            }
        } else if(groupNames != null && !groupNames.isEmpty()) {
            Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(groupNames);
            deviceIds = Lists.newArrayList(deviceGroupService.getDeviceIds(deviceGroups));
        } else { 
            /* Assume all devices, use contents of SystemGroupEnum.DEVICETYPES */
            DeviceGroup group = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DEVICETYPES);
            deviceIds = Lists.newArrayList(deviceGroupService.getDeviceIds(Collections.singletonList(group)));
        }
        
        return deviceIds;
    }
    
    private SqlFragmentSource getSqlSource(List<Integer> subList){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if(!lifetime){
            sql.append("select ypo.paoname deviceName, ypo.type type, route.paoname route, sum(requests) requests, sum(attempts) attempts, sum(completions) completions");
            sql.append("from DYNAMICPAOSTATISTICSHISTORY dpsh");
            sql.append("  join YukonPAObject ypo on ypo.PAObjectID = dpsh.PAObjectID");
            sql.append("  join DeviceRoutes dr on dr.DEVICEID = dpsh.PAObjectID");
            sql.append("join YukonPAObject route on route.PAObjectID = dr.ROUTEID");
            sql.append("WHERE dpsh.PAObjectID IN (").appendArgumentList(subList).append(")");
            sql.append("  and DateOffset > ").appendArgument(getStartDateOffset()).append("and DateOffset <= ").appendArgument(getStopDateOffset());
            sql.append("group by ypo.PAOName,ypo.type, route.PAOName");
        } else {
            sql.append("select ypo.paoname deviceName, ypo.type type, route.paoname route, requests, attempts, completions");
            sql.append("from DYNAMICPAOSTATISTICS dps");
            sql.append("  join YukonPAObject ypo on ypo.PAObjectID = dps.PAObjectID");
            sql.append("  join DeviceRoutes dr on dr.DEVICEID = dps.PAObjectID");
            sql.append("  join YukonPAObject route on route.PAObjectID = dr.ROUTEID");
            sql.append("WHERE dps.PAObjectID IN (").appendArgumentList(subList).append(")");
            sql.append("  and dps.StatisticType = 'Lifetime'");
            sql.append("group by ypo.PAOName,ypo.type, route.PAOName, Requests, Attempts, Completions");
        }
        return sql;
    }
    
    private long getStartDateOffset() {
        long startDateMillis = getStartDate().getTime();
        long startOffset = ((((startDateMillis / 1000) / 60) / 60) / 24);
        return startOffset;
    }
    
    private long getStopDateOffset(){
        long stopDateMillis = getStopDate().getTime();
        long stopOffset = ((((stopDateMillis / 1000) / 60) / 60) / 24);
        return stopOffset;
    }

    public void doLoadData() {
        List<Integer> deviceIds = Lists.newArrayList();
        
        deviceIds = getDeviceIds();
        
        ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);
        SqlFragmentGenerator<Integer> gen = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                return getSqlSource(subList);
            }
        };
        
        List<ModelRow> rows = template.query(gen, deviceIds, new ParameterizedRowMapper<ModelRow>() {
            @Override
            public ModelRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceRequestDetailModel.ModelRow row = new DeviceRequestDetailModel.ModelRow();
                row.deviceName = rs.getString("deviceName");
                row.type = rs.getString("type");
                row.route = rs.getString("route");
                int requests = rs.getInt("requests");
                row.requests = requests;
                int completions = rs.getInt("completions");
                if(completions > 0){
                    int attempts = rs.getInt("attempts");
                    double success =  new Double(attempts) / new Double(completions);
                    DecimalFormat formatter = new DecimalFormat("#####.##");
                    String successString = formatter.format(success);
                    row.success = successString;
                } else {
                    row.success = "---";
                }
                row.numberOfSuccesses = completions;
                double successPercent = completions / requests;
                successPercent = successPercent * 100.0;
                DecimalFormat df = new DecimalFormat("###.##");
                row.successPercent = df.format(successPercent) + "%";
                
                return row;
            }
        });
        
        data.addAll(rows);
            
        log.info("Report Records Collected from Database: " + data.size());
        
    }
    
    public void setGroupsFilter(List<String> groupNames) {
        this.groupNames = groupNames;
    }
    
    public void setDeviceFilter(List<String> deviceNames) {
        this.deviceNames = deviceNames;
    }
    
    public void setLifetime(boolean lifetime){
        this.lifetime = lifetime;
    }
    
    public boolean isLifetime(){
        return lifetime;
    }

    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Required
    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}