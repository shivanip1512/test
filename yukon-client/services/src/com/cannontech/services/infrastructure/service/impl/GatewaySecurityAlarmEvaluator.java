package com.cannontech.services.infrastructure.service.impl;

import java.util.Collections;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;

public class GatewaySecurityAlarmEvaluator extends InfrastructureStatusWarningEvaluator {
    
    private static int SECURITY_ALARM = 1;
    
    @Override
    public InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.GATEWAY_SECURITY_ALARM;
    }

    @Override
    public BuiltInAttribute getAttribute() {
        return BuiltInAttribute.SECURITY_ALARM;
    }

    @Override
    public int getBadState() {
        return SECURITY_ALARM;
    }

    @Override
    public Set<PaoType> getSupportedTypes() {
        return Collections.singleton(PaoType.GWY800);
    }
    
}
