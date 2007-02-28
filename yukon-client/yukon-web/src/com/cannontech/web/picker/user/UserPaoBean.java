package com.cannontech.web.picker.user;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.UserPaoOwner;
import com.cannontech.database.db.user.YukonUser;

public class UserPaoBean { 
    
    private List<LiteYukonGroup> groupList;
	private List<LiteYukonUser> userList;

    public List<LiteYukonGroup> getGroupList() {
        if(groupList == null) {
            groupList = new ArrayList<LiteYukonGroup>();
            groupList = DefaultDatabaseCache.getInstance().getAllYukonGroups();
        }
        return groupList;
    }
    
    public void setGroupList(List<LiteYukonGroup> groupList) {
        this.groupList = groupList;
    }
    
    public List<LiteYukonUser> getUserList() {
        if(userList == null) {
            userList = new ArrayList<LiteYukonUser>();
            userList.add(DaoFactory.getYukonUserDao().getLiteYukonUser("yukon"));
        }
        return userList;
    }

    public void setUserList(List<LiteYukonUser> userList) {
        this.userList = userList;
    }

    public void saveUserPaoOwner( int user, int pao ) {
        
    }
    
}
