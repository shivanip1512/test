package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

public class AttributeDefinedGroupProvider extends BinningDeviceGroupProviderBase<BuiltInAttribute> {

    private List<BuiltInAttribute> allBins = Arrays.asList(BuiltInAttribute.values());
    private PaoDefinitionDao paoDefinitionDao;
    
    @Override
    protected List<BuiltInAttribute> getAllBins() {
        return allBins;
    }

    @Override
    protected Set<BuiltInAttribute> getBinsForDevice(YukonDevice device) {
        Set<AttributeDefinition> definedAttributes = paoDefinitionDao.getDefinedAttributes(device.getPaoIdentifier().getPaoType());
        Set<BuiltInAttribute> result = Sets.newHashSet();
        for (AttributeDefinition attributeDefinition : definedAttributes) {
            Attribute attribute = attributeDefinition.getAttribute();
            BuiltInAttribute builtInAttribute = BuiltInAttribute.valueOf(attribute.getKey());
            result.add(builtInAttribute);
        }
        return result;
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(BuiltInAttribute bin) {
        Multimap<PaoType, Attribute> allDefinedAttributes = paoDefinitionDao.getPaoTypeAttributesMultiMap();
        Multimap<Attribute, PaoType> dest = HashMultimap.create();
        Multimaps.invertFrom(allDefinedAttributes, dest );
        Collection<PaoType> collection = dest.get(bin);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.type").in(collection);
        return sql;
    }

    @Override
    protected SqlFragmentSource getAllBinnedDeviceSqlSelect() {
        Multimap<PaoType, Attribute> allDefinedAttributes = paoDefinitionDao.getPaoTypeAttributesMultiMap();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.type").in(allDefinedAttributes.keySet());
        return sql;
    }

    @Override
    protected String getGroupName(BuiltInAttribute bin) {
        return bin.getDescription();
    }

    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }

}
