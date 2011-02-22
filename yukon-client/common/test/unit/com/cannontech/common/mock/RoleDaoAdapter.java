package com.cannontech.common.mock;

import java.util.Map;
import java.util.Set;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;

public class RoleDaoAdapter implements RoleDao {
    @Override
    public String getGlobalPropertyValue(int rolePropertyID_) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LiteYukonRole getLiteRole(Integer rolePropID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LiteYukonRoleProperty[] getRoleProperties(int roleID_) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LiteYukonGroup getGroup(String groupName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LiteYukonGroup getGroup(int grpID_) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRolePropValueGroup(LiteYukonGroup group_, int rolePropertyID, String defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRolePropValueGroup(int groupId, int rolePropertyId, String defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LiteYukonRoleProperty getRoleProperty(int propid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean updateGroupRoleProperty(LiteYukonGroup group, int roleID,
            int rolePropertyID, String newVal) {
        return false;
    }
    
    @Override
    public <E extends Enum<E>> E getGlobalRolePropertyValue(Class<E> class1, int rolePropertyID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkGlobalRoleProperty(int rolePropertyID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<YukonRole, LiteYukonGroup> getRolesAndGroupsForUser(LiteYukonUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<YukonRole> getRolesForUser(LiteYukonUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<YukonRole> getRolesForGroup(LiteYukonGroup liteYukonGroup) {
        throw new UnsupportedOperationException();
    }
}