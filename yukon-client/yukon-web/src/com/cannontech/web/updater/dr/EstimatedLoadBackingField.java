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
    
    /**
    * Retrieves the estimated load value based on the program id and scenario id when the scenario is inactive.  
    * This can use a different gear formula due to the selected scenario start gear.
    * @param programId - The program Id of the object a value is being retrieved for
    * @param scenarioId - The scenario Id associated to the object a value is being retrieved
    * @param userContext - Current userContext
    * @return Value of the requested field
    */
    default public String getValue(int programId, int scenarioId, YukonUserContext userContext) {
        return getValue(programId, userContext);
    }

}
