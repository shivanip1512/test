package com.cannontech.web.stars.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.stars.gateway.model.WiFiMeterCommData;
import com.cannontech.web.stars.service.RfnWiFiCommDataService;

public class RfnWiFiCommDataServiceImpl implements RfnWiFiCommDataService{

    private static final Logger log = YukonLogManager.getLogger(RfnWiFiCommDataServiceImpl.class);

    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceDao rfnDeviceDao;

    public List<WiFiMeterCommData> getWiFiMeterCommDataForGateways(List<Integer> gatewayIds) {
        // Select all the WiFi meters in DynamicRfnDeviceData
        List<RfnDevice> wiFiMeters = rfnDeviceDao.getDevicesForGateways(gatewayIds, PaoType.getWifiTypes());
        // Turn the list of RfnDevices into WiFiMeterCommData objects
        List<WiFiMeterCommData> wiFiMeterCommData = wiFiMeters.stream()
                                                              .map(this::buildWiFiMeterCommDataObject)
                                                              .filter(Objects::nonNull)
                                                              .collect(Collectors.toList());

        return wiFiMeterCommData;
    }

    public WiFiMeterCommData buildWiFiMeterCommDataObject(RfnDevice rfnDevice) {
        PaoIdentifier paoIdentifier = rfnDevice.getPaoIdentifier();
        LitePoint commStatusPoint = attributeService.findPointForAttribute(paoIdentifier, BuiltInAttribute.COMM_STATUS);
        LitePoint rssiPoint = attributeService.findPointForAttribute(paoIdentifier,
                BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR);

        WiFiMeterCommData wiFiMeterCommData = new WiFiMeterCommData(rfnDevice, commStatusPoint, rssiPoint);
        log.debug("Created WiFiMeterCommData object for {}", wiFiMeterCommData);

        return wiFiMeterCommData;
    }

}
