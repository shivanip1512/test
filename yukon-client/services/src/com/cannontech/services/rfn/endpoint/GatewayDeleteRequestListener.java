package com.cannontech.services.rfn.endpoint;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayDeleteRequest;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;

public class GatewayDeleteRequestListener {
    private static final Logger log = YukonLogManager.getLogger(GatewayDeleteRequestListener.class);

    @Autowired private DeviceDao deviceDao;
    @Autowired private RfnGatewayDataCache dataCache;
    @Autowired private GatewayEventLogService gatewayEventLogService;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;

    public void handleGatewayDelete(GatewayDeleteRequest gatewayDeleteRequest) {

        RfnIdentifier rfnIdentifier = gatewayDeleteRequest.getRfnIdentifier();
        if ("_EMPTY_".equals(rfnIdentifier.getSensorSerialNumber())
            || "_EMPTY_".equals(rfnIdentifier.getSensorManufacturer())
            || "_EMPTY_".equals(rfnIdentifier.getSensorModel())) {
            if (log.isInfoEnabled()) {
                log.info("Serial Number:" + rfnIdentifier.getSensorSerialNumber() + " Sensor Manufacturer:"
                    + rfnIdentifier.getSensorManufacturer() + " Sensor Model:" + rfnIdentifier.getSensorModel());
            }
            return;
        }
        RfnDevice rfnDevice;
        try {
            rfnDevice = rfnDeviceLookupService.getDevice(rfnIdentifier);
        } catch (NotFoundException e1) {
            // We got a message for a gateway that is not in the database.
            log.warn("Received delete request for a gateway that's not in the database: " + rfnIdentifier);
            throw new RuntimeException("Gateway does not exist in System " + rfnIdentifier);
        }

        // Delete from yukon database and cache, and send DB change message
        deviceDao.removeDevice(rfnDevice);
        dataCache.remove(rfnDevice.getPaoIdentifier());
        gatewayEventLogService.deletedGatewayAutomatically(rfnDevice.getName(),
            gatewayDeleteRequest.getRfnIdentifier().getSensorSerialNumber());
    }

}
