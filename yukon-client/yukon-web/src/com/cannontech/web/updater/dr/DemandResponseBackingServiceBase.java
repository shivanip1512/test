package com.cannontech.web.updater.dr;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.service.DemandResponseFieldService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

/**
 * Abstract Base Class for DemandResponseBacking services.  Wraps up the common processing for 
 * demand response backing field finding 
 *
 * @param <T> - Type of DatedObject this service handles
 */
public abstract class DemandResponseBackingServiceBase<T> extends
        UpdateBackingServiceBase<T> {

    private DemandResponseFieldService<T> demandResponseFieldService;
    
    @Override
    public Object getValue(DatedObject<T> datedObject, String[] idBits,
                           YukonUserContext userContext) {
        String fieldName = idBits[1];

        DemandResponseBackingField<T> backingField = 
            demandResponseFieldService.getBackingField(fieldName);
        
        T object = null;
        if (datedObject != null) {
            object = datedObject.getObject();
        }
        
        return backingField.getValue(object, userContext);
    }

}
