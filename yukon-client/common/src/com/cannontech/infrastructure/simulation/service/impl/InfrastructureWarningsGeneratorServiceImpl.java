package com.cannontech.infrastructure.simulation.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.infrastructure.simulation.service.InfrastructureWarningsGeneratorService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class InfrastructureWarningsGeneratorServiceImpl implements InfrastructureWarningsGeneratorService{
    
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private IDatabaseCache cache;
    
    @Override
    public InfrastructureWarning generate(InfrastructureWarningType type) {
        int threshold = globalSettingDao.getInteger(GlobalSettingType.GATEWAY_READY_NODES_THRESHOLD);
        List<RfnGateway> gateways = new ArrayList<>(rfnGatewayService.getAllGateways());
        if(gateways.isEmpty()){
            throw new RuntimeException("Create gateways.");
        }
        Collections.shuffle(gateways);
        RfnGateway warnableGateway = gateways.get(0);
        
        List<InfrastructureWarningSeverity> severities = Lists.newArrayList(InfrastructureWarningSeverity.values());
        Collections.shuffle(severities);
        InfrastructureWarningSeverity severity = severities.get(0);

        int randomNumber = ThreadLocalRandom.current().nextInt(1, 100);
        switch (type) {
        case CCU_COMM_STATUS:
            LiteYukonPAObject ccu =
                cache.getAllDevices().stream().filter(d -> d.getPaoType().isCcu()).findFirst().get();
            if (ccu != null) {
                return new InfrastructureWarning(ccu.getPaoIdentifier(), type);
            } else {
                throw new RuntimeException("Create CCUs.");
            }
        case GATEWAY_COLOR:
            return new InfrastructureWarning(warnableGateway.getPaoIdentifier(), type, severity, randomNumber);
        case GATEWAY_CONNECTED_NODES:
            return new InfrastructureWarning(warnableGateway.getPaoIdentifier(), type, severity, randomNumber);
        case GATEWAY_CONNECTION_STATUS:
            return new InfrastructureWarning(warnableGateway.getPaoIdentifier(), type, severity, new DateTime().toString("MM/dd/yyyy HH:mm:ss"));
        case GATEWAY_DATA_STREAMING_LOAD:
            return new InfrastructureWarning(warnableGateway.getPaoIdentifier(), type, severity, randomNumber);
        case GATEWAY_FAILSAFE:
            return new InfrastructureWarning(warnableGateway.getPaoIdentifier(), type);
        case GATEWAY_READY_NODES:
            return new InfrastructureWarning(warnableGateway.getPaoIdentifier(), type, severity, randomNumber, threshold);
        case RELAY_OUTAGE:
            LiteYukonPAObject relay = cache.getAllDevices().stream().filter(d-> d.getPaoType().isRfRelay()).findFirst().get();
            if (relay != null) {
                return new InfrastructureWarning(relay.getPaoIdentifier(), type);
            } else {
                throw new RuntimeException("Create relays.");
            }
        case REPEATER_COMM_STATUS:
            LiteYukonPAObject repeater = cache.getAllDevices().stream().filter(d-> d.getPaoType().isRepeater()).findFirst().get();
            if (repeater != null) {
                return new InfrastructureWarning(repeater.getPaoIdentifier(), type);
            } else {
                throw new RuntimeException("Create repeaters.");
            }
        case GATEWAY_TIME_SYNC_FAILED:
            return new InfrastructureWarning(warnableGateway.getPaoIdentifier(), type);
        }
        
        return null;
    }
}
