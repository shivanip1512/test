package com.cannontech.services.infrastructure.service.impl;

import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;

/**
 * Generates warnings for relays with a bad outage state.
 */
public class RelayOutageStatusEvaluator extends InfrastructureStatusWarningEvaluator {

    private static int BAD_STATE = 2;
    
    @Override
    public Set<PaoType> getSupportedTypes() {
        return PaoType.getRfRelayTypes();
    }
    
    @Override
    public InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.RELAY_OUTAGE;
    }

    @Override
    public BuiltInAttribute getAttribute() {
        return BuiltInAttribute.OUTAGE_STATUS;
    }

    @Override
    public int getBadState() {
        return BAD_STATE;
    }
}
