package com.cannontech.services.infrastructure.service.impl;

import java.util.Collections;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;

public class GatewayNodeCountExceededEvaluator extends InfrastructureStatusWarningEvaluator {
    
    private static int NodeCountExceeded = 1;
    
    @Override
    public InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.GATEWAY_NODE_COUNT_EXCEEDED;
    }

    @Override
    public BuiltInAttribute getAttribute() {
        return BuiltInAttribute.NODE_COUNT_EXCEEDED;
    }

    @Override
    public int getBadState() {
        return NodeCountExceeded;
    }

    @Override
    public Set<PaoType> getSupportedTypes() {
        return Collections.singleton(PaoType.GWY800);
    }
    
}
