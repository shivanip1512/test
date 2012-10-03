package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonGroupDao {

    public List<LiteYukonGroup> getGroupsForUser(LiteYukonUser user);
    
    /**
     * Returns a list of yukon groups the user is in
     * @param userId
     * @param excludeYukonGroup
     * @return
     */
    public List<LiteYukonGroup> getGroupsForUser(int userId);
    
    public List<LiteYukonGroup> getAllGroups();

    public LiteYukonGroup getLiteYukonGroup(int groupID);

    public Map<Integer, LiteYukonGroup> getLiteYukonGroups(Iterable<Integer> groupIds);

    /**
     * This method uses the group name supplied to get the corresponding liteYukonGroup.
     */
    public LiteYukonGroup getLiteYukonGroupByName(String groupName);

    /**
     * This method tries to use the group name supplied to get the corresponding liteYukonGroup.
     */
    public LiteYukonGroup findLiteYukonGroupByName(String groupName);

    /** 
     * Saves a LiteYukonGroup, inserting it if is a new group or
     * updating it if is an existing group.
     * @param group
     */
    public void save(LiteYukonGroup group);

    public void delete(int groupId);
    
    /**
     * This method returns all of the role groups that are mapped to the user group id supplied.
     */
    public List<LiteYukonGroup> getRoleGroupsForUserGroupId(int userGroupId);

    /**
     * This method returns all of the role groups that are mapped to the user group ids supplied.
     */
    public List<LiteYukonGroup> getDistinctRoleGroupsForUserGroupIds(List<Integer> userGroupIds);
}