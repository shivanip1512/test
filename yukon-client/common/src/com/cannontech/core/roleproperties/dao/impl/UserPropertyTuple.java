package com.cannontech.core.roleproperties.dao.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.NotInRoleException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;

final class UserPropertyTuple implements PropertyTuple {
    private final Integer userId;
    private final YukonRoleProperty yukonRoleProperty;

    public UserPropertyTuple(LiteYukonUser user, YukonRoleProperty yukonRoleProperty) {
        if (user == null) {
            this.userId = null;
        } else {
            this.userId = user.getUserID();
        }
        this.yukonRoleProperty = yukonRoleProperty;
    }

    @Override
    public YukonRoleProperty getYukonRoleProperty() {
        return yukonRoleProperty;
    }
    public Integer getUserId() {
        return userId;
    }

    @Override
    public NotInRoleException notInRoleException() {
        return new UserNotInRoleException(yukonRoleProperty, userId);
    }
    
    @Override
    public String toString() {
        return yukonRoleProperty + " for user id " + userId;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime
                 * result
                 + ((yukonRoleProperty == null) ? 0
                         : yukonRoleProperty.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserPropertyTuple other = (UserPropertyTuple) obj;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (yukonRoleProperty != other.yukonRoleProperty)
            return false;
        return true;
    }

    @Override
    public SqlFragmentSource getRolePropertyValueLookupQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YGR.Value");
        sql.append("FROM YukonGroupRole YGR");
        sql.append("  JOIN UserGroupToYukonGroupMapping UGYGM ON UGYGM.GroupId = YGR.GroupId");
        sql.append("  JOIN YukonUser YU ON YU.UserGroupId = UGYGM.UserGroupId");
        sql.append("WHERE YGR.RolePropertyId").eq(yukonRoleProperty.getPropertyId());
        sql.append("  AND YU.UserId").eq(userId);

        return sql;
    }
}