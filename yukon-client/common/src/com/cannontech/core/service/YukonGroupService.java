package com.cannontech.core.service;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonGroup;

public interface YukonGroupService {
    
    /**
     * This method return the group associated with the given yukonRole and user.
     * This method will return null if the user is not currently in the given role.
     */
    public LiteYukonGroup getGroupByYukonRoleAndUser(YukonRole yukonRole, int userId);
}