package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonGroupDao {

    public List<LiteYukonGroup> getGroupsForUser(LiteYukonUser user);
    
    public List<LiteYukonGroup> getGroupsForUser(int userID);
    
    public LiteYukonGroup getLiteYukonGroup(int groupID);

    public LiteYukonGroup getLiteYukonGroupByName(String groupName);
}
