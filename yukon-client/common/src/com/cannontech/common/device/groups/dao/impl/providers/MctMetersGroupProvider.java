package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class MctMetersGroupProvider extends DeviceGroupProviderSqlBase {

    @Override
    public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(identifier +" IN (");
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.Type").in(PaoType.getMctTypes()).append(")");
        return sql;
    }

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        return Collections.emptyList();
    }

    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        return device.getPaoIdentifier().getPaoType().isMct();
    }
    
}
