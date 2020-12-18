package com.cannontech.services.infrastructure.service.impl;

import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;

public class GatewayHighDiskUsageEvaluator extends InfrastructureStatusWarningEvaluator {

    @Override
    public InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.GATEWAY_HIGH_DISK_USAGE;
    }

    @Override
    public BuiltInAttribute getAttribute() {
        return BuiltInAttribute.HIGH_DISK_USAGE;
    }

    @Override
    public int getBadState() {
        return EventStatus.ACTIVE.getRawState();
    }

    @Override
    public Set<PaoType> getSupportedTypes() {
        return PaoType.getRfGatewayTypes();
    }
}
