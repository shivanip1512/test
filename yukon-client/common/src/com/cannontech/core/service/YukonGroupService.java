package com.cannontech.core.service;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonGroupService {
    
    /**
     * This method return the group associated with the given yukonRole and user.
     * This method will return null if the user is not currently in the given role.
     */
    public LiteYukonGroup getGroupByYukonRoleAndUser(YukonRole yukonRole, LiteYukonUser user);

    /**
     * This method adds the given user to the supplied group.  The user will be removed from
     * any groups that conflict with the new group to protect against role conflicts.
     */
    public void  addUserToGroup(LiteYukonGroup newUserGroup, LiteYukonUser user);
    
}