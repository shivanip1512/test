package com.cannontech.common.exception;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;

public class NotAuthorizedException extends YukonSecurityException {
    public NotAuthorizedException(String msg) {
        super(msg);
    }


    public static NotAuthorizedException role(LiteYukonUser user, YukonRole role) {
        return new NotAuthorizedException("User " + user + " requires role " + role);
    }
    public static NotAuthorizedException trueProperty(LiteYukonUser user, YukonRoleProperty roleProperty) {
        return new NotAuthorizedException("User " + user + " requires true property for roleProperty " + roleProperty);
    }
    public static NotAuthorizedException trueProperty(LiteYukonUser user, int ... rolePropertyIds) {
        Integer[] intObjs = ArrayUtils.toObject(rolePropertyIds);
        return new NotAuthorizedException("User " + user + " requires true property for at least one of rolePropertyId=" + StringUtils.join(intObjs, ","));
    }
    public static NotAuthorizedException atLeastOneTrueProperty(LiteYukonUser user, YukonRoleProperty... roleProperties) {
    	return new NotAuthorizedException("User " + user + " requires true property for at least one of roleProperty: " + StringUtils.join(roleProperties, ","));
    }
    public static NotAuthorizedException falseProperty(LiteYukonUser user, YukonRoleProperty roleProperty) {
        return new NotAuthorizedException("User " + user + " requires false property for roleProperty " + roleProperty);
    }
    public static NotAuthorizedException adminUser(LiteYukonUser user) {
        return new NotAuthorizedException("User " + user + " is not an administrator");
    }
    public static NotAuthorizedException ecOperator(LiteYukonUser user) {
        return new NotAuthorizedException("User " + user + " is not an energy company operator.");
    }
    public static NotAuthorizedException category(LiteYukonUser user, YukonRoleCategory roleCategory) {
        return new NotAuthorizedException("User " + user + " requires a role in category " + roleCategory);
    }
    public static NotAuthorizedException hierarchicalProperty(LiteYukonUser user, HierarchyPermissionLevel minLevel) {
        return new NotAuthorizedException("User " + user + " requires the minimum permission level " + minLevel + " to access.");
    }
}
