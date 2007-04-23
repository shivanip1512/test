package com.cannontech.core.authorization.model;

import java.util.List;

/**
 * Data object which contains a list of user permissions and a list of group
 * permissions
 */
public class UserGroupPermissionList {

    private List<PaoPermission> userPermissionList = null;
    private List<PaoPermission> groupPermissionList = null;

    public List<PaoPermission> getGroupPermissionList() {
        return groupPermissionList;
    }

    public void setGroupPermissionList(List<PaoPermission> groupPermissionList) {
        this.groupPermissionList = groupPermissionList;
    }

    public List<PaoPermission> getUserPermissionList() {
        return userPermissionList;
    }

    public void setUserPermissionList(List<PaoPermission> userPermissionList) {
        this.userPermissionList = userPermissionList;
    }

}
