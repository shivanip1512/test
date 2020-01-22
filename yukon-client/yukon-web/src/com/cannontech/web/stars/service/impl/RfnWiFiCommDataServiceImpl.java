package com.cannontech.web.stars.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.gateway.model.WiFiMeterCommData;
import com.cannontech.web.stars.service.RfnWiFiCommDataService;

public class RfnWiFiCommDataServiceImpl implements RfnWiFiCommDataService{

    private static final Logger log = YukonLogManager.getLogger(RfnWiFiCommDataServiceImpl.class);

    @Autowired private AttributeService attributeService;
    @Autowired private CommandExecutionService commandExecutionService;
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

    private WiFiMeterCommData buildWiFiMeterCommDataObject(RfnDevice rfnDevice) {
        PaoIdentifier paoIdentifier = rfnDevice.getPaoIdentifier();
        LitePoint commStatusPoint = attributeService.findPointForAttribute(paoIdentifier, BuiltInAttribute.COMM_STATUS);
        LitePoint rssiPoint = attributeService.findPointForAttribute(paoIdentifier,
                BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR);

        WiFiMeterCommData wiFiMeterCommData = new WiFiMeterCommData(paoIdentifier, commStatusPoint, rssiPoint);
        log.debug("Created WiFiMeterCommData object for {}", wiFiMeterCommData);

        return wiFiMeterCommData;
    }

    public void refreshWiFiMeterConnection(List<PaoIdentifier> paoIdentifiers, LiteYukonUser user) {

        paoIdentifiers.stream()
                      .filter(pao -> pao.getPaoType().isWifiDevice())
                      .forEach(pao -> initiateWiFiStatusRefresh(pao, user));
    }

    private void initiateWiFiStatusRefresh(PaoIdentifier paoIdentifier, LiteYukonUser user) {
        CommandRequestDevice request = new CommandRequestDevice("getstatus wifi", new SimpleDevice(paoIdentifier));
        CommandResultHolder result = commandExecutionService.execute(request,
                DeviceRequestType.WIFI_METER_CONNECTION_STATUS_REFRESH, user);
        log.debug("WiFi Meter connection refresh result: {}", result);
    }

}
