package com.cannontech.services.infrastructure.service.impl;

import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.infrastructure.model.InfrastructureWarningType;
import com.cannontech.services.infrastructure.service.InfrastructureStatusWarningEvaluator;

public class RepeaterCommStatusEvaluator extends InfrastructureStatusWarningEvaluator {

    private static int DISCONNECTED = 1;
   
    @Override
    public Set<PaoType> getSupportedTypes() {
        return PaoType.getRepeaterTypes();
    }
    
    @Override
    public InfrastructureWarningType getWarningType() {
        return InfrastructureWarningType.CCU_COMM_STATUS;
    }

    @Override
    public BuiltInAttribute getAttribute() {
        return BuiltInAttribute.COMM_STATUS;
    }

    @Override
    public int getBadState() {
        return DISCONNECTED;
    }
}
