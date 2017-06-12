package com.cannontech.services.infrastructure.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureWarningEvaluator;
import com.google.common.collect.Multimap;

/**
 * Evaluates all RF Gateways. Warns for each gateway that shares a "color" with one or more other gateways.
 */
public class GatewayColorWarningEvaluator implements InfrastructureWarningEvaluator {
    private static final Logger log = YukonLogManager.getLogger(GatewayColorWarningEvaluator.class);
    @Autowired RfnGatewayService rfnGatewayService;
    
    @Override
    public Set<PaoType> getSupportedTypes() {
        return PaoType.getRfGatewayTypes();
    }

    @Override
    public List<InfrastructureWarning> evaluate() {
        log.debug("Running RF Gateway color evaluation");
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        Multimap<Short, RfnGateway> duplicateColorGateways = rfnGatewayService.getDuplicateColorGateways(gateways);

        return duplicateColorGateways.entries()
                                     .stream()
                                     .map(entry -> new InfrastructureWarning(entry.getValue().getPaoIdentifier(), 
                                                                             InfrastructureWarningType.GATEWAY_COLOR, 
                                                                             entry.getKey()))
                                     .collect(Collectors.toList());
    }

}
