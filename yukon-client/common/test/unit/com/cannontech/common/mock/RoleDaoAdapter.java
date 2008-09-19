package com.cannontech.common.mock;

import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;

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
            int rolePropertyID, String newVal)
            throws CommandExecutionException, TransactionException {
        return false;
    }
    
    @Override
    public <E extends Enum<E>> E getRolePropertyValue(Class<E> class1, int rolePropertyID) {
        throw new UnsupportedOperationException();
    }
}