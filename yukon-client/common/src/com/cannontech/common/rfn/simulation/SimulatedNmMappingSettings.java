package com.cannontech.common.rfn.simulation;

import java.io.Serializable;

import com.cannontech.common.rfn.message.network.NeighborData;
import com.cannontech.common.rfn.message.network.ParentData;
import com.cannontech.common.rfn.message.network.RouteData;

public class SimulatedNmMappingSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private NeighborData neighborData;
    private ParentData parentData;
    private RouteData routeData;
    public NeighborData getNeighborData() {
        return neighborData;
    }
    public void setNeighborData(NeighborData neighborData) {
        this.neighborData = neighborData;
    }
    public ParentData getParentData() {
        return parentData;
    }
    public void setParentData(ParentData parentData) {
        this.parentData = parentData;
    }
    public RouteData getRouteData() {
        return routeData;
    }
    public void setRouteData(RouteData routeData) {
        this.routeData = routeData;
    }
}
