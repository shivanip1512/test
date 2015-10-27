package com.cannontech.web.updater.dr;

import com.cannontech.user.YukonUserContext;

public interface EstimatedLoadBackingField {

    public String getFieldName();

    /**
     * Method to get this field's value from the pao id passed in
     * @param paoId - The pao id of the object that a value is being retrieved for
     * @param userContext - Current userContext
     * @return Value of the requested field
     */
    public String getValue(int paoId, YukonUserContext userContext);
    
    default public String getValue(int programId, int scenarioId, YukonUserContext userContext) {
        return getValue(programId, userContext);
    }

}
