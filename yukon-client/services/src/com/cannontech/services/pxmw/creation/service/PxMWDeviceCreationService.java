package com.cannontech.services.pxmw.creation.service;

/**
 * Service responsible for automatically creating PxMW Cloud LCR's.
 */
public interface PxMWDeviceCreationService {
    /**
     * Automatically create PxMW Cloud LCR's that are not in Yukon yet on a configurable interval.
     */
    void autoCreateCloudLCR();
}
