package com.cannontech.dr.ecobee;

/**
 * Thrown when an Ecobee API query is unsuccessful due to an expired authentication token.
 */
public class EcobeeNotAuthenticatedException extends RuntimeException {
    private final int energyCompanyId;
    
    public EcobeeNotAuthenticatedException(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    public int getEnergyCompanyId() {
        return energyCompanyId;
    }
}
