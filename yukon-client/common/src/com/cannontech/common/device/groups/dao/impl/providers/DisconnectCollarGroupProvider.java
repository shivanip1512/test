package com.cannontech.common.device.groups.dao.impl.providers;


import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class DisconnectCollarGroupProvider extends DeviceGroupProviderSqlBase {
    
    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        return Collections.emptyList();
    }
    
    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) AS c");
        sql.append("FROM YukonPAObject ypo");
        sql.append("  JOIN DeviceMCT400Series d ON ypo.PAObjectID = d.DEVICEID");
        sql.append("WHERE ypo.PAObjectID").eq(device.getPaoIdentifier().getPaoId());

        Integer deviceCount = jdbcTemplate.queryForInt(sql);
        return deviceCount > 0;
    }


    @Override
	public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        /**
         * This query assumes that an entry in the DeviceMCT400Series table means
         *  that a meter has a disconnect collar attached to it.
         */
	    SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(identifier, " IN ( ");
        sql.append("SELECT ypo.PAObjectID ");
        sql.append("FROM YukonPAObject ypo ");
        sql.append("JOIN DeviceMCT400Series d ON ypo.PAObjectID = d.DEVICEID) ");
        return sql;
	    
	}
}
