package com.cannontech.common.rfn.model;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifingMessage;

/**
 * Use this interface on model objects.
 * For message classes use {@link RfnIdentifingMessage}
 */
public interface YukonRfn {
    
    /**
     * Returns the RFN device identifier
     * @return
     */
    public RfnIdentifier getRfnIdentifier();
}