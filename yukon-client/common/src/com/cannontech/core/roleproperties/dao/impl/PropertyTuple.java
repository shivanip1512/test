package com.cannontech.core.roleproperties.dao.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.core.roleproperties.NotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;

interface PropertyTuple {
    /**
     * Returns the YukonRolePropery value of the specific tuple we're looking for.
     */
    public YukonRoleProperty getYukonRoleProperty();
    
    /**
     * The query used to find the value of the supplied tuple.
     */
    public SqlFragmentSource getRolePropertyValueLookupQuery();

    /**
     * Created the suitable not found exception for the specific type of PropertyTuple 
     */
    public NotInRoleException notInRoleException();

    /**
     * Checks to see if we're looking for a system role property.
     * @throws IllegalArgumentException - The property is not a system role property and user or role group is null.
     */
    public boolean isSystemProperty();
    
}