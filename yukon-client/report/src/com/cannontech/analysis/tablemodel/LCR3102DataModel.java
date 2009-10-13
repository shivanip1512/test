package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DeviceDao;
import com.google.common.collect.Lists;


    public class LCR3102DataModel extends BareDatedReportModelBase<LCR3102DataModel.ModelRow> {
        
        // dependencies
    private JdbcOperations jdbcOps;
    
    // member variables
    private List<ModelRow> data = Lists.newArrayList();
    private Logger log = YukonLogManager.getLogger(LCR3102DataModel.class);
    private List<String> deviceNames;
    private List<String> groupNames;

    private DeviceGroupService deviceGroupService;

    private DeviceDao deviceDao;
    
    public LCR3102DataModel() {
    }
    
    static public class ModelRow {
        public String deviceName;
        public String serialNumber;
        public String route;
        public String runTimeLoad;
        public String relayShedTime;
        public String timestamp;
        public String relay;
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
        return "LCR 3102 Data Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
            
        SqlFragmentSource sql = buildSQLStatement();
        
        jdbcOps.query(sql.getSql(), sql.getArguments(), new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                LCR3102DataModel.ModelRow row = new LCR3102DataModel.ModelRow();

                row.deviceName = rs.getString("deviceName");
                row.serialNumber = rs.getString("serialNumber");
                row.route = rs.getString("route");
                row.runTimeLoad = rs.getString("runTimeLoad");
                row.relayShedTime = rs.getString("relayShedTime");
                row.timestamp = rs.getString("timestamp");
                String pointAName = rs.getString("pointA_Name");
                String words[] = pointAName.split(" ");
                row.relay = "Relay " + words[2];
                data.add(row);
            }
        });
            
        log.info("Report Records Collected from Database: " + data.size());
    }
    
    public SqlStatementBuilder buildSQLStatement() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT pao.PAONAME deviceName");
        sql.append("  , lmhBase.ManufacturerSerialNumber serialNumber");
        sql.append("  , route.PAOName route");
        sql.append("  , pointA.POINTNAME pointA_Name");
        sql.append("  , rphA.VALUE runTimeLoad");
        sql.append("  , pointB.POINTNAME pointB_Name");
        sql.append("  , rphB.VALUE relayShedTime");
        sql.append("  , rphA.TIMESTAMP ");
        sql.append("FROM RawPointHistory rphA");
        sql.append("  JOIN Point pointA ON pointA.PointID = rphA.PointID");
        sql.append("  JOIN Point pointB ON pointB.PAObjectID = pointA.PAObjectID");
        sql.append("  JOIN RawPointHistory rphB on rphB.pointID = pointB.PointId AND rphB.TIMESTAMP = rphA.TIMESTAMP");
        sql.append("  JOIN YukonPAObject pao ON pao.PAObjectID = pointA.PAObjectID AND pao.type = 'LCR-3102'");
        sql.append("  JOIN InventoryBase invBase ON invBase.DeviceID = pao.PAObjectID");
        sql.append("  JOIN LMHardwareBase lmhBase ON lmhBase.InventoryID = invBase.InventoryID");
        sql.append("  JOIN DeviceRoutes dr on pao.PAObjectID = dr.DEVICEID");
        sql.append("  JOIN YukonPAObject route on route.PAObjectID = dr.ROUTEID");
        if(deviceNames != null && !deviceNames.isEmpty()){
            sql.append("WHERE pao.PAObjectID IN (").appendArgumentList(getDeviceNameIdList()).append(")");
            sql.append("AND");
        } else if (groupNames != null && !groupNames.isEmpty()){
            sql.append("WHERE ", getDeviceGroupWhereClause());
            sql.append("AND");
        } else {
            sql.append("WHERE");
        }
        sql.append("((pointA.POINTOFFSET = 5 AND pointB.POINTOFFSET = 9 )"); 
        sql.append("  OR (pointA.POINTOFFSET = 6 AND pointB.POINTOFFSET = 10 ) ");
        sql.append("  OR (pointA.POINTOFFSET = 7 AND pointB.POINTOFFSET = 11 ) ");
        sql.append("  OR (pointA.POINTOFFSET = 8 AND pointB.POINTOFFSET = 12 )) ");
        
        sql.append("AND rphA.timestamp > ").appendArgument(getStartDate());
        sql.append("AND rphA.timestamp <= ").appendArgument(getStopDate());
        
        sql.append("ORDER BY deviceName , pointA_Name, rphA.TIMESTAMP");
        
        return sql;
    }
    
    private SqlFragmentSource getDeviceGroupWhereClause() {
        Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(groupNames);
        SqlFragmentSource source = deviceGroupService.getDeviceGroupSqlWhereClause(deviceGroups, "pao.PAObjectId");
        return source;
    }
    
    private List<Integer> getDeviceNameIdList() {
        List<Integer> paoIds = Lists.newArrayList();
        for(String name : deviceNames){
            paoIds.add(deviceDao.getYukonDeviceObjectByName(name).getDeviceId());
        }
        return paoIds;
    }

    public void setGroupsFilter(List<String> groupNames) {
        this.groupNames = groupNames;
    }

    public void setDeviceFilter(List<String> deviceNames) {
        this.deviceNames = deviceNames;
    }
    
    @Required
    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }
    
    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

}