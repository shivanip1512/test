package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

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
    
    private String rfTemplatePrefix;
    
    @PostConstruct
    public void initialize() {
         rfTemplatePrefix = configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, "*RfnTemplate_");

    }
    
    @Override
    public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlStatementBuilder inClause = new SqlStatementBuilder();
        inClause.append("SELECT ypo.paobjectid");
        inClause.append("FROM Device d");
        inClause.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        inClause.append("WHERE ypo.Type").in(PaoType.getWaterMeterTypes());
        inClause.append("AND ypo.PAOName NOT").startsWith(rfTemplatePrefix);
        return sql.append(identifier).in(inClause);
    }

    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        return Collections.emptyList();
    }

    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        return device.getPaoIdentifier().getPaoType().isWaterMeter()
                &&!dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName().startsWith(rfTemplatePrefix);
    }

}
