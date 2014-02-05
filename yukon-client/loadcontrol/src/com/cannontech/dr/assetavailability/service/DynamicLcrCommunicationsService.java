package com.cannontech.dr.assetavailability.service;

import com.cannontech.message.dispatch.message.PointData;

/**
 * Analyzes point data to determine if it contains runtime values, or only generic communications, and calls the 
 * appropriate dao methods to update the LcrCommunications table.
 */
public interface DynamicLcrCommunicationsService {
    /**
     * Uses point data from a 2-way LCR to insert Asset Availability timestamps into the DynamicLcrCommunications table.
     * This method DOES NOT validate that the point data is from a 2-way LCR. That should be determined beforehand.
     */
    public void processPointData(Iterable<PointData> pointData);
}
