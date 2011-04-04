package com.cannontech.stars.service;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;

public interface DefaultRouteService {
    public int getDefaultRoute(LiteStarsEnergyCompany energyCompany);
    public void updateDefaultRoute(LiteStarsEnergyCompany energyCompany, int routeID, LiteYukonUser user);
    public void removeDefaultRoute(LiteStarsEnergyCompany energyCompany);
}
