package com.cannontech.dr.ecobee;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores authentication tokens for Ecobee API access.
 */
public class EcobeeAuthenticationCache {
    private final Map<Integer, String> ecAuthKeys = new HashMap<>();
    
    public String put(Integer ecId, String authKey) {
        return ecAuthKeys.put(ecId, authKey);
    }
    
    public String get(Integer ecId) {
        return ecAuthKeys.get(ecId);
    }
}
