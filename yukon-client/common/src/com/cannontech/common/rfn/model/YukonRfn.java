package com.cannontech.common.rfn.model;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;

/**
 * Use this interface on model objects.
 * For message classes use {@link RfnIdentifyingMessage}
 */
public interface YukonRfn {
    
    /**
     * Returns the RFN device identifier
     * @return
     */
    public RfnIdentifier getRfnIdentifier();
}