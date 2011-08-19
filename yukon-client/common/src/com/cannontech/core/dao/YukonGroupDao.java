package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonGroupDao {

    public List<LiteYukonGroup> getGroupsForUser(LiteYukonUser user);
    
    public List<LiteYukonGroup> getGroupsForUser(int userID);
    
    /**
     * Returns a list of yukon groups the use is in excluding the 'Yukon Grp' if excludeYukonGroup is true
     * @param userId
     * @param excludeYukonGroup
     * @return
     */
    public List<LiteYukonGroup> getGroupsForUser(int userId, boolean excludeYukonGroup);
    
    public List<LiteYukonGroup> getAllGroups();

    public LiteYukonGroup getLiteYukonGroup(int groupID);

    public Map<Integer, LiteYukonGroup> getLiteYukonGroups(Iterable<Integer> groupIds);

    public LiteYukonGroup getLiteYukonGroupByName(String groupName);

    /** 
     * Saves a LiteYukonGroup, inserting it if is a new group or
     * updating it if is an existing group.
     * @param group
     */
    public void save(LiteYukonGroup group);

    public void delete(int groupId);

}
