package com.cannontech.services.infrastructure.service.impl;

import java.util.Collections;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;

public class GatewayDoorOpenEvaluator extends InfrastructureStatusWarningEvaluator {
    
    private static int DOOR_OPEN = 1;
    
    @Override
    public InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.GATEWAY_DOOR_OPEN;
    }

    @Override
    public BuiltInAttribute getAttribute() {
        return BuiltInAttribute.DOOR_OPEN;
    }

    @Override
    public int getBadState() {
        return DOOR_OPEN;
    }

    @Override
    public Set<PaoType> getSupportedTypes() {
        return Collections.singleton(PaoType.GWY800);
    }
    
}
