package com.cannontech.core.authorization.model;

import com.cannontech.core.authorization.support.Permission;

public interface PaoPermission {

    public int getId();

    public int getPaoId();

    public Permission getPermission();

}