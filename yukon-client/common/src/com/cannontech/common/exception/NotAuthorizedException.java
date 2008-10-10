package com.cannontech.common.exception;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.cannontech.database.data.lite.LiteYukonUser;

public class NotAuthorizedException extends YukonSecurityException {
    public NotAuthorizedException(String msg) {
        super(msg);
    }


    public static NotAuthorizedException role(LiteYukonUser user, int roleId) {
        return new NotAuthorizedException("User " + user + " requires false property for roleId=" + roleId);
    }
    public static NotAuthorizedException trueProperty(LiteYukonUser user, int ... rolePropertyIds) {
        Integer[] intObjs = ArrayUtils.toObject(rolePropertyIds);
        return new NotAuthorizedException("User " + user + " requires true property for at least one of rolePropertyId=" + StringUtils.join(intObjs, ","));
    }
    public static NotAuthorizedException falseProperty(LiteYukonUser user, int rolePropertyId) {
        return new NotAuthorizedException("User " + user + " requires false property for rolePropertyId=" + rolePropertyId);
    }
    public static NotAuthorizedException adminUser(LiteYukonUser user) {
        return new NotAuthorizedException("User " + user + " is not an administrator");
    }
}
