package com.cannontech.web.stars.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.gateway.model.WiFiMeterCommData;
import com.cannontech.web.stars.service.RfnWiFiCommDataService;
import com.cannontech.web.tools.commander.model.CommandParams;
import com.cannontech.web.tools.commander.model.CommandTarget;
import com.cannontech.web.tools.commander.service.CommanderService;
import com.google.common.collect.Maps;

public class RfnWiFiCommDataServiceImpl implements RfnWiFiCommDataService{

    private static final Logger log = YukonLogManager.getLogger(RfnWiFiCommDataServiceImpl.class);

    @Autowired private AttributeService attributeService;
    @Autowired private CommanderService commanderService;
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

    public void refreshWiFiMeterConnection(List<Integer> wiFiMeterIds, YukonUserContext userContext) {
        Map<String, Integer> commandCounts = Maps.newConcurrentMap();
        CommandParams params = new CommandParams();
        params.setTarget(CommandTarget.DEVICE);
        params.setRouteId(null);
        params.setSerialNumber(null);
        params.setCommand("GetStatus WiFi");
        params.setPriority(14);
        params.setQueueCommand(true);

        Iterator<Integer> iterator = wiFiMeterIds.iterator();
        while (iterator.hasNext()) {
            params.setPaoId(iterator.next());

            commandCounts = commanderService.parseCommand(params, userContext);
            commanderService.sendCommand(userContext, params, commandCounts);
        }
    }

}
