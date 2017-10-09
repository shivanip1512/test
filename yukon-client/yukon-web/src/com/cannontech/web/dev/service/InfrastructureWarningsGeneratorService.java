package com.cannontech.web.dev.service;

import com.cannontech.infrastructure.model.InfrastructureWarning;
import com.cannontech.infrastructure.model.InfrastructureWarningType;

public interface InfrastructureWarningsGeneratorService {

    /**
     * Generates fake warning
     */
    InfrastructureWarning genarate(InfrastructureWarningType type);

}
