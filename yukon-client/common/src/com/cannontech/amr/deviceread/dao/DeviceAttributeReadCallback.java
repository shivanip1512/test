package com.cannontech.amr.deviceread.dao;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;

public interface DeviceAttributeReadCallback {
    /**
     * This method should be called zero or more times for each PAO (and thus, zero or more times overall).
     */
    public void receivedValue(PaoIdentifier pao, PointValueHolder value);
    /**
     * For a given PAO, this method should be called in those cases that the receivedValue method will NOT be called.
     */
    public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error);
    /**
     * This may be called zero or more times. If this is called, there is no guarantee that the other received* methods
     * will be called as described. Guarantees on the complete method will always be honored, however.
     */
    public void receivedException(DeviceAttributeReadError error);
    /**
     * This is guaranteed to be called exactly once. Once this has been called, none of the received* methods will called.
     */
    public void complete();
}
