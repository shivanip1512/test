package com.cannontech.services.infrastructure.service.impl;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;

/**
 * Generates warnings for relays with a bad outage state.
 */
public class InfrastructureOutageStatusEvaluator extends InfrastructureStatusWarningEvaluator {
    
    @Override
    public Set<PaoType> getSupportedTypes() {
        return Stream.concat(PaoType.getRfRelayTypes().stream(), PaoType.getCellularMeterTypes().stream()).collect(Collectors.toSet());
    }
    
    @Override
    public InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.INFRASTRUCTURE_OUTAGE;
    }

    @Override
    public BuiltInAttribute getAttribute() {
        return BuiltInAttribute.OUTAGE_STATUS;
    }

    @Override
    public int getBadState() {
        return OutageStatus.BAD.getRawState();
    }
}
