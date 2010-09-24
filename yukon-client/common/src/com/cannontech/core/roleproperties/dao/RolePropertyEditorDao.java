package com.cannontech.core.roleproperties.dao;

import java.util.Map;

import com.cannontech.core.roleproperties.DescriptiveRoleProperty;
import com.cannontech.core.roleproperties.GroupRolePropertyValueCollection;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;

public interface RolePropertyEditorDao {
    
    public Map<YukonRoleProperty, DescriptiveRoleProperty> getDescriptiveRoleProperties(YukonRole role);
    
    public GroupRolePropertyValueCollection getForGroup(LiteYukonGroup group, boolean defaultForBlank);
    public GroupRolePropertyValueCollection getForGroupAndRole(LiteYukonGroup group, YukonRole role, boolean defaultForBlank);

    public void save(GroupRolePropertyValueCollection collection);
    
}
