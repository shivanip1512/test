package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.impl.providers.helpers.ScanIndicatingDevice;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class ScanningMetersGroupProvider extends DeviceGroupProviderBase {
    
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;
    
    private ScanIndicatingDevice scanIndicatingDevice;
    
    @Override
    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        
        // return devices that are scanning load profile
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid, ypo.type");
        sql.append("FROM DeviceMeterGroup d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE d.deviceid IN (" + scanIndicatingDevice.getDeviceIdSql() + ")");
        
        YukonDeviceRowMapper mapper = new YukonDeviceRowMapper(paoGroupsWrapper);
        List<YukonDevice> devices = jdbcTemplate.query(sql.toString(), mapper);
        
        return Collections.unmodifiableList(devices);
    }

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        
        // scanning meter groups have no child groups
        return Collections.emptyList();
    }
    
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base, YukonDevice device) {
        
        if (isDeviceInGroup(base, device)) {
            return Collections.singleton(base); 
        }
        
        return Collections.emptySet();
    }
    

    public boolean isDeviceInGroup(DeviceGroup deviceGroup, YukonDevice device) {
        
        // is this device scanning? if so, it belongs to the base group
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) AS c");
        sql.append("FROM DeviceMeterGroup d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE d.deviceid IN (" + scanIndicatingDevice.getDeviceIdSql() + ")");
        sql.append("AND d.deviceid = ?");
        
        Integer deviceCount = jdbcTemplate.queryForInt(sql.toString(), device.getDeviceId());
        
        return deviceCount > 0;
    }

	@Override
    public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
	    
	    // gather pao id's for all devices that are scanning
	    SqlStatementBuilder paoIdSql = new SqlStatementBuilder();
	    paoIdSql.append("SELECT ypo.paobjectid");
	    paoIdSql.append("FROM DeviceMeterGroup d");
	    paoIdSql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
	    paoIdSql.append("WHERE d.deviceid IN (" + scanIndicatingDevice.getDeviceIdSql() + ")");
        
	    String whereString = identifier + " IN ( " + paoIdSql.toString() + ") ";
	    
	    return whereString;
    }

	// INJECTION SETTERS
	public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }

	@Required
    public void setScanIndicatingDevice(ScanIndicatingDevice scanIndicatingDevice) {
        this.scanIndicatingDevice = scanIndicatingDevice;
    }
	
}
