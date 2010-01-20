package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class DeviceTagGroupProvider extends BinningDeviceGroupProviderBase<PaoTag> {
    private PaoDefinitionDao paoDefinitionDao;
    
    @Override
    protected List<PaoTag> getAllBins() {
        PaoTag[] values = PaoTag.values();
        return Arrays.asList(values);
    }
    
    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(PaoTag bin) {
        Set<PaoDefinition> devicesThatSupportTag = paoDefinitionDao.getPaosThatSupportTag(bin);
        Collection<String> collection = Collections2.transform(devicesThatSupportTag, new Function<PaoDefinition, String>() {
            @Override
            public String apply(PaoDefinition paoDefinition) {
                return paoDefinition.getType().getPaoTypeName();
            }
        });
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.type in (").appendArgumentList(collection).append(")");
        return sql;
    }
    
    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        // assuming every type has at least one tag
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        return sql;
    }
    
    @Override
    protected String getGroupName(PaoTag bin) {
        return bin.getDescription();
    }

    @Override
    protected Set<PaoTag> getBinsForDevice(YukonDevice device) {
        Set<PaoTag> supportedTags = paoDefinitionDao.getSupportedTags(device.getPaoIdentifier().getPaoType());
        return supportedTags;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }

}
