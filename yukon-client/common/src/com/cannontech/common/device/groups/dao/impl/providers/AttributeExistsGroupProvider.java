package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class AttributeExistsGroupProvider extends AttributeGroupProviderBase {
    private AttributeService attributeService;
    
    @Override
    protected Set<BuiltInAttribute> getBinsForDevice(YukonDevice device) {
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(device);
        // this is a way of "casting" the above set into a set of BuildInAttributes
        // without actually casting
        SetView<BuiltInAttribute> intersection = 
            Sets.intersection(EnumSet.allOf(BuiltInAttribute.class), allExistingAttributes);
        return intersection;
    }

    @Override
    protected SqlFragmentSource getChildSqlSelectForBin(BuiltInAttribute bin) {
        SqlFragmentSource fragment = attributeService.getAttributeLookupSql(bin);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT exists_frag.paobjectId");
        sql.append("FROM (").appendFragment(fragment).append(") exists_frag");
        return sql;
    }

    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

}
