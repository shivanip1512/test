package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.PaoType;
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
                row.relayShedTime = rs.getString("relayShed");
                row.timestamp = rs.getString("maxDateTime");
                String runTimeRelay = rs.getString("runTimeRelay");
                String relayShedRelay = rs.getString("relayShedRelay");
                String relay = (runTimeRelay != null ? runTimeRelay : (relayShedRelay != null ? relayShedRelay : "-"));
                row.relay = "Relay " + relay;
                data.add(row);
            }
        });
        log.info("Report Records Collected from Database: " + data.size());
    }
    
    public SqlStatementBuilder buildSQLStatement() {

    	SqlStatementBuilder devicesWhereClause = new SqlStatementBuilder();
        if(deviceNames != null && !deviceNames.isEmpty()){
        	devicesWhereClause.append("AND pao.PAObjectID IN (").appendArgumentList(getDeviceNameIdList()).append(")");
        } else if (groupNames != null && !groupNames.isEmpty()){
        	devicesWhereClause.append("AND ", getDeviceGroupWhereClause());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAO.PAOName deviceName, LMHBASE.ManufacturerSerialNumber serialNumber, ROUTE.PAOName route,");
        sql.append(    "runTimePointName, runTimeLoad, runTimeLoadTime,");
        sql.append(    "relayShedPointName, relayShed, relayShedTime,");
        sql.append(    "maxDateTime, runTimeRelay, relayShedRelay");
        sql.append("FROM YukonPaobject PAO");
        sql.append(    "LEFT JOIN InventoryBase INVBASE ON INVBASE.DeviceID = PAO.PAObjectID");
        sql.append(    "LEFT JOIN LMHardwareBase LMHBASE ON LMHBASE.InventoryID = INVBASE.InventoryID");
        sql.append(    "JOIN DeviceRoutes DR ON pao.PAObjectID = DR.DEVICEID");
        sql.append(    "JOIN Yukonpaobject ROUTE ON ROUTE.PAObjectID = DR.ROUTEID");
        sql.append(    "JOIN (");
        sql.append(        "SELECT runTime.PAObjectID runTimePaoId, relayShed.paobjectid relayShedPaoId,"); 
        sql.append(            "runTimePointName, runtimeload, runtimeloadtime, relayShedPointName, relayShed, relayShedTime,");
        sql.append(            "CASE WHEN runTimeLoadTime IS NULL THEN relayShedTime");
        sql.append(                "ELSE runTimeLoadTime");
        sql.append(            "END maxDateTime,");
        sql.append(            "(runTime.POINTOFFSET - 4) runTimeRelay,");	//removing 4 to get to a 1-based relay value
        sql.append(            "(relayShed.POINTOFFSET - 8) relayShedRelay");	//removing 8 to get to a 1-based relay value
        sql.append(        "FROM (");
        sql.append(            "SELECT DISTINCT PAO.paobjectid, RTPOINT.POINTNAME runTimePointName, RTPOINT.POINTOFFSET,");
        sql.append(                "RTRPH.VALUE runTimeLoad, RTRPH.TIMESTAMP runTimeLoadTime");
        sql.append(            "FROM YukonPAObject PAO");
        sql.append(                "JOIN Point RTPOINT ON RTPOINT.PAObjectID = PAO.PAObjectID");
        sql.append(                "JOIN RawPointHistory RTRPH ON RTPOINT.PointID = RTRPH.PointID"); 
        sql.append(            "WHERE RTPOINT.POINTOFFSET IN (5, 6, 7, 8)");	// 5, 6, 7, 8 are defined point offsets for RunTime Load points on LCR-3102 devices
        sql.append(                "AND PAO.Type").eq_k(PaoType.LCR3102);
        sql.appendFragment(devicesWhereClause);
		sql.append(                "AND RTRPH.TIMESTAMP").gt(getStartDate());
		sql.append(                "AND RTRPH.TIMESTAMP").lte(getStopDate());
		sql.append(            ") runTime");
		sql.append(        "FULL OUTER JOIN (");
		sql.append(            "SELECT DISTINCT PAO.PAObjectID, rsPoint.POINTNAME relayShedPointName, rsPoint.POINTOFFSET,");
		sql.append(                "RSRPH.VALUE relayShed, RSRPH.TIMESTAMP relayShedTime");
		sql.append(            "FROM YukonPAObject PAO");
		sql.append(                "JOIN Point RSPOINT ON RSPOINT.PAObjectID = PAO.PAObjectID");
		sql.append(                "LEFT JOIN RawPointHistory RSRPH ON RSPOINT.PointID = RSRPH.PointID");
		sql.append(            "WHERE RSPOINT.POINTOFFSET IN (9, 10, 11, 12)");	// 9, 10, 11, 12 are defined point offsets for RelayShed points on LCR-3102 devices
        sql.append(                "AND PAO.Type").eq_k(PaoType.LCR3102);
        sql.appendFragment(devicesWhereClause);
		sql.append(                "AND RSRPH.TIMESTAMP").gt(getStartDate());
		sql.append(                "AND RSRPH.TIMESTAMP").lte(getStopDate());
		sql.append(            ") relayShed ON runTime.PAObjectID = relayShed.PAObjectID");

		/* These offsets correspond to point pairs of 'Runtime Load X' and 'Relay X Shed Time' */
        /* on LCR-3102 devices, there are 4 pairs total. */
		sql.append(                "AND ((runTime.POINTOFFSET = 5 AND relayShed.POINTOFFSET = 9 )");
		sql.append(                    "OR (runTime.POINTOFFSET = 6 AND relayShed.POINTOFFSET = 10 )");
		sql.append(                    "OR (runTime.POINTOFFSET = 7 AND relayShed.POINTOFFSET = 11 )");
		sql.append(                    "OR (runTime.POINTOFFSET = 8 AND relayShed.POINTOFFSET = 12 ))");
		sql.append(                "AND runTimeLoadTime = relayShedTime");
		sql.append(    ") rphstuff ON (rphstuff.RunTimePaoId = PAO.PAObjectID OR RelayShedPaoId = PAO.PAObjectID)");
		sql.append("WHERE NOT (runTimeLoad IS NULL AND relayShed IS NULL)");
		sql.append("ORDER BY deviceName, runTimePointName, maxDateTime");

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
        	try {
        		paoIds.add(deviceDao.getYukonDeviceObjectByName(name).getDeviceId());
            } catch (EmptyResultDataAccessException e) {
                log.error("Unable to find device with name: " + name + ". This device will be skipped.");
                continue;
            }
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