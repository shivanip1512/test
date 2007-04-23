package com.cannontech.core.authorization.model;

import com.cannontech.core.authorization.support.Permission;

public class UserPaoPermission implements PaoPermission {

    private int userId;
    private int paoId;
    private Permission permission = null;

    public int getId() {
        return userId;
    }

    public void setId(int id) {
        this.userId = id;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String toString() {
        return userId + ", " + paoId + ", " + permission;
    }
}
