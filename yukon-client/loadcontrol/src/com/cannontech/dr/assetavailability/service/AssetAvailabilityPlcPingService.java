package com.cannontech.dr.assetavailability.service;

import java.util.Set;

import com.cannontech.common.pao.YukonPao;

/**
 * Service that handles reading asset availability data from two-way PLC devices.
 */
public interface AssetAvailabilityPlcPingService {
    public void readDevices(Set<? extends YukonPao> devices, String resultId);
}
