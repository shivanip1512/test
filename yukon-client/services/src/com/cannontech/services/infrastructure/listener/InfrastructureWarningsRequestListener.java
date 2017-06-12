package com.cannontech.services.infrastructure.listener;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.infrastructure.model.InfrastructureWarningsRequest;
import com.cannontech.services.infrastructure.service.InfrastructureWarningsService;

/**
 * Listens for InfrastructureWarningsRequests and initiates a recalculation of the infrastructure warnings.
 */
public class InfrastructureWarningsRequestListener {
    @Autowired InfrastructureWarningsService infrastructureWarningsService;
    
    public void handleRequest(InfrastructureWarningsRequest request) {
        infrastructureWarningsService.calculateWarnings();
    }
    
}
