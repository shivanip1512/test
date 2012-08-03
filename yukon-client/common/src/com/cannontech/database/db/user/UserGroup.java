package com.cannontech.database.db.user;


public class UserGroup {

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
}