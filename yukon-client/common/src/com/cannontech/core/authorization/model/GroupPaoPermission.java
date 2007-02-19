package com.cannontech.core.authorization.model;

import com.cannontech.core.authorization.support.Permission;

public class GroupPaoPermission implements PaoPermission {

    private int id;
    private int groupId;
    private int paoId;
    private Permission permission = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String toString() {
        return groupId + ", " + paoId + ", " + permission;
    }
}
