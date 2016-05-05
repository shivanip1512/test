package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class DisabledMeterGroupProvider extends DeviceGroupProviderSqlBase {

    @Autowired IDatabaseCache dbCache;

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        return Collections.emptyList();
    }

    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        boolean result = isDeviceDisabled(device);
        return result;
    }

    private boolean isDeviceDisabled(YukonDevice device) {
        LiteYukonPAObject devicePao = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId());
        return devicePao.getDisableFlag().equals(YNBoolean.YES.getDatabaseRepresentation());
    }

    @Override
    public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(identifier, " IN ( ");
        sql.append("SELECT ypo.PAObjectID ");
        sql.append("FROM YukonPAObject ypo ");
        sql.append("JOIN Device d ON ypo.PAObjectID = d.DEVICEID ");
        sql.append("JOIN DeviceMeterGroup dmg ON d.deviceId = dmg.deviceId ");
        sql.append("WHERE ypo.DisableFlag").eq_k(YNBoolean.YES).append(")");
        return sql;
    }
}