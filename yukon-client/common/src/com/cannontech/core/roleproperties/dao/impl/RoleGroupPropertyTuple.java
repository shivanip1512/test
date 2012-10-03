package com.cannontech.core.roleproperties.dao.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.NotInRoleException;
import com.cannontech.core.roleproperties.RoleGroupNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;

class RoleGroupPropertyTuple implements PropertyTuple{
    private Integer roleGroupId;
    private YukonRoleProperty yukonRoleProperty;

    public RoleGroupPropertyTuple(LiteYukonGroup liteYukonGroup, YukonRoleProperty yukonRoleProperty) {
        if (liteYukonGroup != null) {
            this.roleGroupId = liteYukonGroup.getGroupID();
        }
        
        this.yukonRoleProperty = yukonRoleProperty;
    }

    @Override
    public YukonRoleProperty getYukonRoleProperty() {
        return yukonRoleProperty;
    }
    public Integer getRoleGroupId() {
        return roleGroupId;
    }
    
    @Override
    public SqlFragmentSource getRolePropertyValueLookupQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YGR.Value");
        sql.append("FROM YukonGroupRole YGR");
        sql.append("WHERE YGR.RolePropertyId").eq(yukonRoleProperty.getPropertyId());
        sql.append("  AND YGR.GroupId").eq(roleGroupId);
        
        return sql;
    }
    
    @Override
    public NotInRoleException notInRoleException() {
        return new RoleGroupNotInRoleException(yukonRoleProperty, roleGroupId);
    }

    @Override
    public String toString() {
        return yukonRoleProperty + " for role group id " + roleGroupId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((roleGroupId == null) ? 0 : roleGroupId.hashCode());
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
        RoleGroupPropertyTuple other = (RoleGroupPropertyTuple) obj;
        if (roleGroupId == null) {
            if (other.roleGroupId != null)
                return false;
        } else if (!roleGroupId.equals(other.roleGroupId))
            return false;
        if (yukonRoleProperty != other.yukonRoleProperty)
            return false;
        return true;
    }
}