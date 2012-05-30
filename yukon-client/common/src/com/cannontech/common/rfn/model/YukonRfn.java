package com.cannontech.common.rfn.model;

import com.cannontech.common.rfn.message.RfnIdentifier;

public interface YukonRfn {
    
    /**
     * Returns the RFN device identifier
     * @return
     */
    public RfnIdentifier getRfnIdentifier();
}