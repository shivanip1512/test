package com.cannontech.core.authorization.model;

import java.util.List;

/**
 * Data object which contains a list of user permissions and a list of group
 * permissions
 */
public class UserGroupPermissionList {

    private List<UserPaoPermission> userPermissionList = null;
    private List<GroupPaoPermission> groupPermissionList = null;

    public List<GroupPaoPermission> getGroupPermissionList() {
        return groupPermissionList;
    }

    public void setGroupPermissionList(List<GroupPaoPermission> groupPermissionList) {
        this.groupPermissionList = groupPermissionList;
    }

    public List<UserPaoPermission> getUserPermissionList() {
        return userPermissionList;
    }

    public void setUserPermissionList(List<UserPaoPermission> userPermissionList) {
        this.userPermissionList = userPermissionList;
    }

}
