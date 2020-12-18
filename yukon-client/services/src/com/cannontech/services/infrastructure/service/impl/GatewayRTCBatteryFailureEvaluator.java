package com.cannontech.services.infrastructure.service.impl;

import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;
import com.google.common.collect.ImmutableSet;

public class GatewayRTCBatteryFailureEvaluator extends InfrastructureStatusWarningEvaluator {

    @Override
    public InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.GATEWAY_RTC_BATTERY_FAILURE;
    }

    @Override
    public BuiltInAttribute getAttribute() {
        return BuiltInAttribute.RTC_BATTERY_FAILURE;
    }

    @Override
    public int getBadState() {
        return EventStatus.ACTIVE.getRawState();
    }

    @Override
    public Set<PaoType> getSupportedTypes() {
        return ImmutableSet.of(PaoType.GWY800, PaoType.GWY801);
    }

}
