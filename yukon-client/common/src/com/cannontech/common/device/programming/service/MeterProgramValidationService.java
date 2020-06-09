package com.cannontech.common.device.programming.service;

import java.util.UUID;

import com.cannontech.common.exception.ServiceCommunicationFailedException;

public interface MeterProgramValidationService {
    /**
     * @param guid
     * @return whether the program with the specified GUID is valid
     * @throws ServiceCommunicationFailedException if the call to Porter fails 
     */
    boolean isMeterProgramValid(UUID guid) throws ServiceCommunicationFailedException;
}
