package com.cannontech.stars.service;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

public interface DefaultRouteService {
    
    int INVALID_ROUTE_ID = -1;  // Mark that a valid default route id is not found, and prevent futher attempts
    
    int getDefaultRouteId(EnergyCompany energyCompany);
    
    /**
     * Returns the pao for the default route or null if no default route can be determined
     * for this energy company.
     */
    LiteYukonPAObject getDefaultRoute(EnergyCompany energyCompany);
    
    /**
     * If current default route id is invalid (-1) this sets up a new default route.
     * If {@code newRouteId} is invalid (-1) this removes default route.
     * Otherwise this updates the default route
     * 
     * Does nothing if {@code newRouteId} equals the current default route id.
     */
    void updateDefaultRoute(EnergyCompany energyCompany, int newRouteId, LiteYukonUser user);
    
    void removeDefaultRoute(EnergyCompany energyCompany);
    
    /**
     * Does nothing if {@code newRouteId} equals the invalid (-1) route id.
     */
    void setupNewDefaultRoute(String ecName, LiteYukonUser ecUser, int newRouteId);
    
}