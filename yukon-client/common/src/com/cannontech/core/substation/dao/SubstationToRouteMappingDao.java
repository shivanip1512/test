package com.cannontech.core.substation.dao;

import java.util.List;

import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.YukonDevice;

public interface SubstationToRouteMappingDao {

    public boolean add(int substationId, int routeId, int ordering);

    public boolean remove(int substationId, int routeId);

    public boolean removeAllBySubstationId(int substationId);

    public boolean removeAllByRouteId(int routeId);

    public boolean update(int substationId, List<Integer> routeIdList);

    public List<Route> getRoutesBySubstationId(int substationId);
    
    public List<Integer> getRouteIdsBySubstationId(int substationId);

    public List<Route> getAvailableRoutesBySubstationId(int substationId);

    public List<Route> getAll();

	public List<Substation> getSubstationsForDevice(YukonDevice device);

}
