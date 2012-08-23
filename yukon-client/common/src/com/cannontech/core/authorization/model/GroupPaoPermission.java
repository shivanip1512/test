package com.cannontech.core.authorization.model;

import com.cannontech.core.authorization.support.Permission;

public class GroupPaoPermission implements PaoPermission {

    private int groupId;
    private int paoId;
    private Permission permission = null;
    private boolean allow;
    
    public int getId() {
        return groupId;
    }
    public void setId(int id) {
        this.groupId = id;
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
        return groupId + ", " + paoId + ", " + permission + ", " + allow;
    }
    public boolean getAllow(){
        return allow;
    }
    public void setAllow( boolean allow )
    {
        this.allow = allow;
    }
}
