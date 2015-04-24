package com.cannontech.database.db.user;

import java.sql.SQLException;

import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public class UserGroup extends DBPersistent implements Comparable<UserGroup> {
    
    private Integer userGroupId;
    private String userGroupName;
    private String userGroupDescription;
    
    public Integer getUserGroupId() {
        return userGroupId;
    }
    
    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }
    
    public String getUserGroupName() {
        return userGroupName;
    }
    
    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }
    
    public String getUserGroupDescription() {
        return userGroupDescription;
    }
    
    public void setUserGroupDescription(String userGroupDescription) {
        this.userGroupDescription = userGroupDescription;
    }
    
    public static final Function<UserGroup, String> USER_GROUP_TO_NAME_FUNCTION = new Function<UserGroup, String>() {
        @Override
        public String apply(UserGroup userGroup) {
            return userGroup.getUserGroupName();
        }
    };
    
    public static Ordering<UserGroup> ORDER_BY_USER_GROUP_NAME = 
            Ordering.natural().onResultOf(USER_GROUP_TO_NAME_FUNCTION);
    
    @Override
    public int compareTo(UserGroup userGroup) {
        return ORDER_BY_USER_GROUP_NAME.compare(this, userGroup);
    }
    
    /**
     * Returns a LiteUserGroup object for this UserGroup if the user group id is not null.  
     * If it is null this method will return null.
     */
    public LiteUserGroup getLiteUserGroup() {
        if (userGroupId != null) {
            LiteUserGroup liteUserGroup = new LiteUserGroup();
            liteUserGroup.setUserGroupId(userGroupId);
            liteUserGroup.setUserGroupName(userGroupName);
            liteUserGroup.setUserGroupDescription(userGroupDescription);
            
            return liteUserGroup;
        }
        return null;
    }
    
    @Override
    public void add() throws SQLException {
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        userGroupDao.create(this);
    }
    
    @Override
    public void delete() throws SQLException {
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        userGroupDao.delete(userGroupId);
    }
    
    @Override
    public void retrieve() throws SQLException {
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        userGroupDao.getDBUserGroup(userGroupId);
    }
    
    @Override
    public void update() throws SQLException {
        UserGroupDao userGroupDao = YukonSpringHook.getBean("userGroupDao", UserGroupDao.class);
        userGroupDao.update(this);
    }
    
}