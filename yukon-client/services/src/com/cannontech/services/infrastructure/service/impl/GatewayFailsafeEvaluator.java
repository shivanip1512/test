package com.cannontech.services.infrastructure.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.gateway.AppMode;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureWarningEvaluator;

/**
 * Evaluates all RF Gateways. Warns when a gateway is in failsafe mode.
 */
public class GatewayFailsafeEvaluator implements InfrastructureWarningEvaluator {
    private static final Logger log = YukonLogManager.getLogger(GatewayColorWarningEvaluator.class);
    @Autowired RfnGatewayService rfnGatewayService;
    
    @Override
    public Set<PaoType> getSupportedTypes() {
        return PaoType.getRfGatewayTypes();
    }

    @Override
    public List<InfrastructureWarning> evaluate() {
        log.debug("Running RF Gateway failsafe evaluation");
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        
        return gateways.stream()
                       .filter(gwy -> gwy.getData() != null)
                       .filter(gwy -> gwy.getData().getMode() == AppMode.FAIL_SAFE)
                       .map(gwy -> new InfrastructureWarning(gwy.getPaoIdentifier(), 
                                                             InfrastructureWarningType.GATEWAY_FAILSAFE))
                       .collect(Collectors.toList());
    }

}
