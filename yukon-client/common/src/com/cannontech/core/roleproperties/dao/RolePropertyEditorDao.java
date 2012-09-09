package com.cannontech.core.roleproperties.dao;

import java.util.Map;

import javax.naming.ConfigurationException;

import com.cannontech.core.roleproperties.DescriptiveRoleProperty;
import com.cannontech.core.roleproperties.GroupRolePropertyValueCollection;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.google.common.base.Predicate;

public interface RolePropertyEditorDao {
    
    public Map<YukonRoleProperty, DescriptiveRoleProperty> getDescriptiveRoleProperties(YukonRole role);
    
    public GroupRolePropertyValueCollection getForGroupAndRole(LiteYukonGroup group, YukonRole role, boolean defaultForBlank);

    public void save(GroupRolePropertyValueCollection collection);

    /**
     * This method adds the supplied role to the supplied role group.  It also handles checking the roles of the user groups attached
     * to the role group.  If there would be a conflict on one of its role groups it will throw a ConfigurationException.   
     * 
     * @throws ConfigurationException - The role group has a user group attached to it, which already has this role.
     */
    public void addRoleToGroup(LiteYukonGroup group, YukonRole role) throws ConfigurationException;

    public void removeRoleFromGroup(int groupId, int roleId);
    
    public GroupRolePropertyValueCollection getForGroupAndPredicate(LiteYukonGroup group,
                                                                    boolean defaultForBlank,
                                                                    Predicate<YukonRoleProperty> predicate);
    
}
