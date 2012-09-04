package com.cannontech.core.users.service;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.db.user.UserGroup;
import com.google.common.collect.TreeMultimap;

/**
 * These methods return a UserGroup object that contains the Yukon user and Yukon group information attached to the UserGroup.  
 * If you are looking to update the information or just see the UserGroup table information use the UserGroupDao instead.
 */
public interface UserGroupService {

    /**
     * 
     */
    public TreeMultimap<LiteYukonGroup, UserGroup> getAssociations(YukonRole yukonRole);

}