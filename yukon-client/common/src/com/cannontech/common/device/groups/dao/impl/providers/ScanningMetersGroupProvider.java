
package com.cannontech.common.device.groups.dao.impl.providers;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.groups.dao.impl.providers.helpers.ScanIndicatingDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class ScanningMetersGroupProvider extends DeviceGroupProviderSqlBase {
    
    private ScanIndicatingDevice scanIndicatingDevice;
    
    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        // is this device scanning? if so, it belongs to the base group
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) AS c");
        sql.append("FROM (" , scanIndicatingDevice.getDeviceIdSql(), ") ypo ");
        sql.append("WHERE ypo.deviceid = ").appendArgument(device.getPaoIdentifier().getPaoId());
        
        Integer deviceCount = jdbcTemplate.queryForObject(sql.getSql(), sql.getArguments(), Integer.class);
        
        return deviceCount > 0;
    }

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        
        // scanning meter groups have no child groups
        return Collections.emptyList();
    }
    
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base, SimpleDevice device) {
        
        if (isDeviceInGroup(base, device)) {
            return Collections.singleton(base); 
        }
        
        return Collections.emptySet();
    }
    
	@Override
    public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append(identifier, "in (", scanIndicatingDevice.getDeviceIdSql(), ")");
	    return sql;
    }

	@Required
    public void setScanIndicatingDevice(ScanIndicatingDevice scanIndicatingDevice) {
        this.scanIndicatingDevice = scanIndicatingDevice;
    }
	
}
