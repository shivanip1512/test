package com.cannontech.services.infrastructure.service.impl;

import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;
import com.google.common.collect.ImmutableSet;

public class GatewayTimeSyncFailedEvaluator extends InfrastructureStatusWarningEvaluator {
    
    
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
        return EventStatus.ACTIVE.getRawState();
    }

    @Override
    public Set<PaoType> getSupportedTypes() {
        return ImmutableSet.of(PaoType.GWY800, PaoType.VIRTUAL_GATEWAY);
    }
    
}
