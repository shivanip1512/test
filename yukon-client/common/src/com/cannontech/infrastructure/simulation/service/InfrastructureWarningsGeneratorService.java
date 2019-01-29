package com.cannontech.infrastructure.simulation.service;

import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;

public interface InfrastructureWarningsGeneratorService {

    /**
     * Generates fake InfrastructureWarning
     * @param type of InfrastructureWarning
     * @return an InfrastructureWarning with the given type
     */
    InfrastructureWarning generate(InfrastructureWarningType type);

}
