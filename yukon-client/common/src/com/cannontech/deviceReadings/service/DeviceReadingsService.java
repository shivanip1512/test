package com.cannontech.deviceReadings.service;

import java.util.List;

import com.cannontech.deviceReadings.model.DeviceReadingRequest;
import com.cannontech.deviceReadings.model.DeviceReadingsResponse;

public interface DeviceReadingsService {

    /**
     * Get Latest Point Reading Values based on requested device and corresponding Attributes.
     */
    List<DeviceReadingsResponse> getLatestReading(DeviceReadingRequest deviceReadingRequest);

}
