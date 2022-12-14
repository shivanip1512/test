package com.cannontech.core.users.model;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.google.common.collect.Ordering;

public class LiteUserGroup extends LiteBase {
    protected String userGroupName;
    protected String userGroupDescription;
    
    public LiteUserGroup() {
        setLiteType(LiteTypes.USER_GROUP);
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
    
    @Override
    public int compareTo(Object val) {
        LiteUserGroup userGroup = (LiteUserGroup) val;
        return userGroupNameComparator().compare(this, userGroup);
    }

    /**
     * This method returns a comparator that will order a list of user groups by their user group names 
     */
    public static Ordering<LiteUserGroup> userGroupNameComparator() {
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