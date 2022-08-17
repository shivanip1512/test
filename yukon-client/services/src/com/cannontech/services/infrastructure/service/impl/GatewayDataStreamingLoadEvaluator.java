package com.cannontech.services.infrastructure.service.impl;

import static com.cannontech.infrastructure.model.InfrastructureWarningSeverity.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureWarningEvaluator;
import com.google.common.collect.ImmutableSet;

/**
 * Generates warnings for all GWY-800s whose data streaming load percentage is greater than 100% (yellow) or 120% (red).
 */
public class GatewayDataStreamingLoadEvaluator implements InfrastructureWarningEvaluator {
    private static final Logger log = YukonLogManager.getLogger(GatewayDataStreamingLoadEvaluator.class);
    @Autowired RfnGatewayService rfnGatewayService;
    
    @Override
    public Set<PaoType> getSupportedTypes() {
        return ImmutableSet.of(PaoType.GWY800, PaoType.VIRTUAL_GATEWAY);
    }

    @Override
    public List<InfrastructureWarning> evaluate() {
        log.debug("Running RF Gateway data streaming load evaluation");
        
        Set<RfnGateway> gateways = rfnGatewayService.getAllNonLegacyGateways();
        
        return gateways.stream()
                       .filter(gwy -> gwy.getData() != null && gwy.getData().getDataStreamingLoadingPercent() > 100)
                       .map(gwy -> {
                           double loadingPercent = gwy.getData().getDataStreamingLoadingPercent();
                           InfrastructureWarningSeverity severity = loadingPercent > 120 ? HIGH : LOW;
                           
                           return new InfrastructureWarning(gwy.getPaoIdentifier(), 
                                                            InfrastructureWarningType.GATEWAY_DATA_STREAMING_LOAD,
                                                            severity,
                                                            loadingPercent);
                       })
                       .collect(Collectors.toList());
    }
}