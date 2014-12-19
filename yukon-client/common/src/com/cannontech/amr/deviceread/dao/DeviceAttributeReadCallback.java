package com.cannontech.amr.deviceread.dao;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;

public interface DeviceAttributeReadCallback {
    /**
     * This method should be called zero or more times for each PAO (and thus, zero or more times overall).
     */
    void receivedValue(PaoIdentifier pao, PointValueHolder value);
    
    /**
     * Received the last value for the logical PAO (is this possible?).
     * @param pao
     */
    void receivedLastValue(PaoIdentifier pao, String value);
    
    /**
     * For a given PAO, this method should be called in those cases that the receivedValue method will NOT be called.
     */
    void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error);
    
    /**
     * This may be called zero or more times. If this is called, there is no guarantee that the other received* methods
     * will be called as described. Guarantees on the complete method will always be honored, however.
     */
    void receivedException(SpecificDeviceErrorDescription error);
    
    /**
     * This is guaranteed to be called exactly once. Once this has been called, none of the received* methods will called.
     */
    void complete();

}
