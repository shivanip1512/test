package com.cannontech.dr.loadgroup.service;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.loadcontrol.data.LMGroupBase;

/**
 * Service used to get load group backing fields
 */
public interface LoadGroupFieldService {

    public DemandResponseBackingField<LMGroupBase> getBackingField(String fieldName);
    
}
