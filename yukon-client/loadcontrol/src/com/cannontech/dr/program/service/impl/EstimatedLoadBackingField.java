package com.cannontech.dr.program.service.impl;

import com.cannontech.user.YukonUserContext;

public interface EstimatedLoadBackingField {

    public String getFieldName();

    /**
     * Method to get this field's value from the object passed in
     * @param object - Object to get field value for
     * @param userContext - Current userContext
     * @return Value of this field
     */
    public Object getValue(int paoId, YukonUserContext userContext);

}
