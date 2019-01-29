package com.cannontech.services.infrastructure.service.impl;

import java.util.Collections;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;

public class GatewayTimeSyncFailedEvaluator extends InfrastructureStatusWarningEvaluator {
    
    private static int TIME_SYNC_FAILED = 1;
    
    @Override
    public InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.GATEWAY_TIME_SYNC_FAILED;
    }

    @Override
    public BuiltInAttribute getAttribute() {
        return BuiltInAttribute.TIME_SYNC_FAILED;
    }

    @Override
    public int getBadState() {
        return TIME_SYNC_FAILED;
    }

    @Override
    public Set<PaoType> getSupportedTypes() {
        return Collections.singleton(PaoType.GWY800);
    }
    
}
