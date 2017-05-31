package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.yukon.IDatabaseCache;

public class RfwMetersGroupProvider extends DeviceGroupProviderSqlBase {
    @Autowired IDatabaseCache dbCache;
    @Autowired ConfigurationSource configurationSource;
    
    @Override
    public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        String rfTemplatePrefix = configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, "*RfnTemplate_");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(identifier +" IN (");
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.Type").in(PaoType.getWaterMeterTypes());
        sql.append("AND ypo.PAOName NOT").startsWith(rfTemplatePrefix+"%").append(")");
        return sql;
    }

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        return Collections.emptyList();
    }

    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        String rfTemplatePrefix = configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, "*RfnTemplate_");
        dbCache.getAllPaosMap().get(device.getPaoIdentifier()).getPaoName().matches("*rfn");
        return device.getPaoIdentifier().getPaoType().isWaterMeter()
                &&!dbCache.getAllPaosMap().get(device.getPaoIdentifier()).getPaoName().matches(rfTemplatePrefix);
    }

}
