package com.cannontech.core.roleproperties;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonGroup;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GroupRolePropertyValueCollection {
    private LiteYukonGroup liteYukonGroup;
    private List<RolePropertyValue> rolePropertyValues = Lists.newArrayList();
    
    public GroupRolePropertyValueCollection(LiteYukonGroup liteYukonGroup,
            Iterable<RolePropertyValue> rolePropertyValues) {
        this.liteYukonGroup = liteYukonGroup;
        this.rolePropertyValues = Lists.newArrayList(rolePropertyValues);
    }
    
    public LiteYukonGroup getLiteYukonGroup() {
        return liteYukonGroup;
    }
    
    public List<RolePropertyValue> getRolePropertyValues() {
        return rolePropertyValues;
    }

    public Map<YukonRoleProperty, Object> getValueMap() {
        Map<YukonRoleProperty, Object> result = Maps.newLinkedHashMap();
        for (RolePropertyValue rolePropertyValue : rolePropertyValues) {
            result.put(rolePropertyValue.getYukonRoleProperty(), rolePropertyValue.getValue());
        }
        
        return result;
    }
    
    public void putAll(Map<YukonRoleProperty, Object> valueMap) {
        ImmutableMap<YukonRoleProperty, RolePropertyValue> uniqueIndex = Maps.uniqueIndex(rolePropertyValues, new Function<RolePropertyValue, YukonRoleProperty>() {
            public YukonRoleProperty apply(RolePropertyValue from) {
                return from.getYukonRoleProperty();
            }
        });
        
        for (Map.Entry<YukonRoleProperty, Object> entry : valueMap.entrySet()) {
            RolePropertyValue rolePropertyValue = uniqueIndex.get(entry.getKey());
            if (rolePropertyValue != null) {
                rolePropertyValue.setValue(entry.getValue());
            } else {
                // not sure if this is needed or a good idea, could just throw exception
                rolePropertyValue = new RolePropertyValue(entry.getKey());
                rolePropertyValues.add(rolePropertyValue);
            }
        }
    }
}
