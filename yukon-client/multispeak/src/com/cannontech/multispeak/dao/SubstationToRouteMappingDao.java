package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.multispeak.db.Route;

public interface SubstationToRouteMappingDao {

    public boolean add(int substationId, int routeId, int ordering);

    public boolean remove(int substationId, int routeId);

    public boolean removeAllBySubstationId(int substationId);

    public boolean removeAllByRouteId(int routeId);

    public boolean update(int substationId, List<Route> routeList);

    public List<Route> getRoutesBySubstationId(int substationId);

    public List<Route> getAvailableRoutesBySubstationId(int substationId);

    public List<Route> getAll();

}
