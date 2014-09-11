package com.cannontech.web.stars.dr.operator.hardware.service;

import java.util.List;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.Pair;
import com.cannontech.stars.dr.digi.model.ZigbeeDeviceDto;

public interface ZigbeeDeviceService {

    /**
     * Returns a list of zigbee devices for an account not including gateways.
     * Intended to be devices that could be connected to the gateway.
     */
    public List<Pair<InventoryIdentifier, ZigbeeDeviceDto>> buildZigbeeDeviceDtoList(int accountId);

    /**
     * Creates a single ZigbeeDeviceDto from the deviceId (paoId)
     */
    public ZigbeeDeviceDto buildZigbeeDeviceDto(int deviceId);

}