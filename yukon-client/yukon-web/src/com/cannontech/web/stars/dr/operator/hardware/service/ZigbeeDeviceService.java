package com.cannontech.web.stars.dr.operator.hardware.service;

import java.util.List;

import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.Pair;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.thirdparty.digi.model.GatewayDto;
import com.cannontech.stars.dr.thirdparty.digi.model.ZigbeeDeviceDto;
import com.cannontech.thirdparty.digi.model.DigiGateway;

public interface ZigbeeDeviceService {

    public GatewayDto createGatewayDto(DigiGateway digiGateway, HardwareDto hardwareDto);

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