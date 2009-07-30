package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceTag;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class DeviceTagGroupProvider extends BinningDeviceGroupProviderBase<DeviceTag> {
    private DeviceDefinitionDao deviceDefinitionDao;
    
    @Override
    protected List<DeviceTag> getAllBins() {
        DeviceTag[] values = DeviceTag.values();
        return Arrays.asList(values);
    }
    
    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(DeviceTag bin) {
        Set<DeviceDefinition> devicesThatSupportTag = deviceDefinitionDao.getDevicesThatSupportTag(bin);
        Collection<String> collection = Collections2.transform(devicesThatSupportTag, new Function<DeviceDefinition, String>() {
            @Override
            public String apply(DeviceDefinition deviceDefinition) {
                return deviceDefinition.getType().getPaoTypeName();
            }
        });
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM DeviceMeterGroup d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.type in (").appendArgumentList(collection).append(")");
        return sql;
    }
    
    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        // assuming every type has at least one tag
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM DeviceMeterGroup d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        return sql;
    }
    
    @Override
    protected String getGroupName(DeviceTag bin) {
        return bin.getDescription();
    }

    @Override
    protected Set<DeviceTag> getBinsForDevice(SimpleDevice device) {
        Set<DeviceTag> supportedTags = deviceDefinitionDao.getSupportedTags(device.getDeviceType());
        return supportedTags;
    }
    
    @Autowired
    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

}
