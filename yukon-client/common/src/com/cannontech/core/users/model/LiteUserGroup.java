package com.cannontech.core.users.model;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.google.common.collect.Ordering;

public class LiteUserGroup extends LiteBase {
    protected String userGroupName;
    protected String userGroupDescription;
    
    public static int NULL_USER_GROUP_ID = -9999;
    
    public LiteUserGroup() {
        setLiteType(LiteTypes.USER_GROUP);
        setLiteID(NULL_USER_GROUP_ID);
    }

    public int getUserGroupId() {
        return getLiteID();
    }
    public void setUserGroupId(int userGroupId) {
        setLiteID(userGroupId);
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
    
    /**
     * This method returns a comparator that will order a list of user groups by their user group names 
     */
    public static Ordering<LiteUserGroup> userGroupNameComparotor() {
        return new Ordering<LiteUserGroup>() {
            @Override
            public int compare(LiteUserGroup left, LiteUserGroup right) {
                return left.getUserGroupName().compareTo(right.getUserGroupName());
            }
        };
    }
    
    @Override
    public String toString() {
        return userGroupName;
    }
}