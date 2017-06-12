package com.cannontech.services.infrastructure.service;

/**
 * Service responsible for calculating the infrastructure warnings.
 */
public interface InfrastructureWarningsService {
    
    /**
     * Calculate infrastructure warnings via the warning evaluators. Old warnings will be deleted from the database,
     * and the new warnings inserted.
     */
    void calculateWarnings();
    
}
