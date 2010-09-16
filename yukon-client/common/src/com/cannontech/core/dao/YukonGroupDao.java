package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonGroupDao {

    public List<LiteYukonGroup> getGroupsForUser(LiteYukonUser user);
    
    public List<LiteYukonGroup> getGroupsForUser(int userID);
    
    public LiteYukonGroup getLiteYukonGroup(int groupID);

    public Map<Integer, LiteYukonGroup> getLiteYukonGroups(Iterable<Integer> groupIds);

    public LiteYukonGroup getLiteYukonGroupByName(String groupName);

    /**
     * Returns the first residential login group that the user is in.
     */
    public String getResidentialGroupNameForUser(int userId, List<LiteYukonGroup> ecResidentialGroups);
}
