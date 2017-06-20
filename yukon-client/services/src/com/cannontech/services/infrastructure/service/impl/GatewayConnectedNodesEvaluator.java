package com.cannontech.services.infrastructure.service.impl;

import static com.cannontech.infrastructure.model.InfrastructureWarningSeverity.*;

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
import com.cannontech.infrastructure.model.InfrastructureWarningSeverity;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureWarningEvaluator;

public class GatewayConnectedNodesEvaluator implements InfrastructureWarningEvaluator {
    private static final Logger log = YukonLogManager.getLogger(GatewayConnectedNodesEvaluator.class);
    private static final int connectedNodesWarningLimit = 3500;
    private static final int connectedNodesHighWarningLimit = 5000;
    @Autowired RfnGatewayService rfnGatewayService;
    
    @Override
    public Set<PaoType> getSupportedTypes() {
        return PaoType.getRfGatewayTypes();
    }

    @Override
    public List<InfrastructureWarning> evaluate() {
        log.debug("Running RF Gateway connected nodes evaluator");
        
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        
        return gateways.stream()
                       .filter(gateway -> gateway.getData() != null)
                       .filter(gateway -> gateway.getData().getGwTotalNodes() >= connectedNodesWarningLimit)
                       .map(gateway -> {
                           int connectedNodes = gateway.getData().getGwTotalNodes();
                           InfrastructureWarningSeverity severity = 
                                   connectedNodes >= connectedNodesHighWarningLimit ? HIGH : LOW;
                                   
                           return new InfrastructureWarning(gateway.getPaoIdentifier(),
                                                            InfrastructureWarningType.GATEWAY_CONNECTED_NODES,
                                                            severity,
                                                            connectedNodes);
                       })
                       .collect(Collectors.toList());
    }

}
