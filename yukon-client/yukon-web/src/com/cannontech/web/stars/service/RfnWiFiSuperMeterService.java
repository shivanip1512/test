package com.cannontech.web.stars.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.stars.gateway.model.SuperMeterInfo;

public class RfnWiFiSuperMeterService {

    private static final Logger log = YukonLogManager.getLogger(RfnWiFiSuperMeterService.class);

    @Autowired private AttributeService attributeService;
    @Autowired private RfnDeviceDao rfnDeviceDao;

    public List<SuperMeterInfo> getWiFiSuperMetersForVirtualGateway(List<Integer> virtualGatewayIds) {
        // Select all the devices in DynamicRfnDeviceData that have the virtualGateway as their gateway
        List<RfnDevice> superMeters = rfnDeviceDao.getDevicesForGateways(virtualGatewayIds, true);
        // Turn the list of RfnDevices into SuperMeterInfo objects
        List<SuperMeterInfo> superMeterInfo = superMeters.stream()
                                                         .map(this::buildSuperMeterInfoObject)
                                                         .filter(Objects::nonNull)
                                                         .collect(Collectors.toList());

        return superMeterInfo;
    }

    private SuperMeterInfo buildSuperMeterInfoObject(RfnDevice rfnDevice) {
        PaoIdentifier paoIdentifier = rfnDevice.getPaoIdentifier();
        LitePoint commStatusPointId = attributeService.findPointForAttribute(paoIdentifier, BuiltInAttribute.COMM_STATUS);
        LitePoint rssiPointId = attributeService.findPointForAttribute(paoIdentifier,
                BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR);

        SuperMeterInfo superMeterInfo = new SuperMeterInfo(paoIdentifier, commStatusPointId, rssiPointId);
        log.debug("Created SuperMeterInfo object for {}", superMeterInfo);

        return superMeterInfo;
    }

}
