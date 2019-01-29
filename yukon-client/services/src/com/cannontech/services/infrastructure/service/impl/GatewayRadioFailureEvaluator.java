package com.cannontech.services.infrastructure.service.impl;

import java.util.Collections;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;

public class GatewayRadioFailureEvaluator extends InfrastructureStatusWarningEvaluator {
    
    private static int RADIO_FAILURE = 1;
    
    @Override
    public InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.GATEWAY_RADIO_FAILURE;
    }

    @Override
    public BuiltInAttribute getAttribute() {
        return BuiltInAttribute.RADIO_FAILURE;
    }

    @Override
    public int getBadState() {
        return RADIO_FAILURE;
    }

    @Override
    public Set<PaoType> getSupportedTypes() {
        return Collections.singleton(PaoType.GWY800);
    }
    
}
