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
        SqlStatementBuilder inClause = new SqlStatementBuilder();
        inClause.append("SELECT ypo.paobjectid");
        inClause.append("FROM Device d");
        inClause.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        inClause.append("WHERE ypo.Type").in(PaoType.getMctTypes());
        return sql.append(identifier).in(inClause);
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
