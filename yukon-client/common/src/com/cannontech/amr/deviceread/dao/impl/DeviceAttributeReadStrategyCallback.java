package com.cannontech.amr.deviceread.dao.impl;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;

public interface DeviceAttributeReadStrategyCallback {
    /**
     * This method should be called zero or more times for each PAO (and thus, zero or more times overall).
     * Note the "PAO" is that PAO that the attribute is defined on, not necessarily the PAO
     * that corresponds to the PointValueHolder.
     */
    public void receivedValue(PaoIdentifier pao, PointValueHolder value);
    
    /**
     * This method will be called once per PAO and will indicate that receivedValue will not
     * be invoked any additional times for the given PAO.
     */
    public void receivedLastValue(PaoIdentifier pao, String value);
    
    /**
     * For a given PAO, this method should be called in those cases that the receivedValue method will NOT be called.
     */
    public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error);
    
    /**
     * This may be called zero or more times. If this is called, there is no guarantee that the other received* methods
     * will be called as described. Guarantees on the complete method will always be honored, however.
     */
    public void receivedException(SpecificDeviceErrorDescription error);
    
    /**
     * This is guaranteed to be called exactly once. Once this has been called, none of the received* methods will called.
     */
    public void complete();
}
