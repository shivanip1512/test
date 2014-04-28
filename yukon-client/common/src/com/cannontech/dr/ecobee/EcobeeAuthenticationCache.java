package com.cannontech.dr.ecobee;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores authentication tokens for Ecobee API access.
 */
public class EcobeeAuthenticationCache {
    private final Map<Integer, String> ecAuthKeys = new HashMap<>();
    
    public String put(Integer energyCompanyId, String authKey) {
        return ecAuthKeys.put(energyCompanyId, authKey);
    }
    
    public String get(Integer energyCompanyId) {
        return ecAuthKeys.get(energyCompanyId);
    }
}
