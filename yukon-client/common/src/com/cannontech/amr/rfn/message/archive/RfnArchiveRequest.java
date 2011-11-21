package com.cannontech.amr.rfn.message.archive;

import com.cannontech.amr.rfn.model.RfnMeterIdentifier;

/**
 * This interface allows the common code of all archive listener implementations (RfnArchiveRequestListenerBase.java)
 * to have access to the RFN device identifier in the archive request.
 */
public interface RfnArchiveRequest {
    
    /**
     * Returns the RFN device identifier.  Currently coded as a meter identifier,
     * may need to change when we support events/alarms for DR devices.
     * @return identifier
     */
    public RfnMeterIdentifier getIdentifier();
    
}