package com.cannontech.stars.service;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

public interface DefaultRouteService {
    int INVALID_ROUTE_ID = -1;  // Mark that a valid default route id is not found, and prevent futher attempts

    int getDefaultRouteId(EnergyCompany energyCompany);
    LiteYukonPAObject getDefaultRoute(EnergyCompany energyCompany);
    void updateDefaultRoute(EnergyCompany energyCompany, int routeID, LiteYukonUser user);
    void removeDefaultRoute(EnergyCompany energyCompany);
}
