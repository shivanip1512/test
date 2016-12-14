package com.cannontech.web.admin.substations.model;

import java.util.List;

import com.cannontech.common.model.Route;
import com.cannontech.common.util.LazyList;

public class SubstationRouteMapping {
    private Integer substationId;
    private String substationName;
    private List<Route> routeList = LazyList.ofInstance(Route.class);
    private List<Route> avList=LazyList.ofInstance(Route.class);
    private List<Integer> selectedRoutes;
    private List<Integer> orderedRoutes= LazyList.ofInstance(Integer.class);
    

    public Integer getSubstationId() {
        return substationId;
    }

    public void setSubstationId(Integer substationId) {
        this.substationId = substationId;
    }

    public List<Route> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<Route> routeList) {
        this.routeList = routeList;
    }

    public List<Route> getAvList() {
        return avList;
    }

    public void setAvList(List<Route> avList) {
        this.avList = avList;
    }

    public List<Integer> getSelectedRoutes() {
        return selectedRoutes;
    }

    public void setSelectedRoutes(List<Integer> selectedRoutes) {
        this.selectedRoutes = selectedRoutes;
    }
    
    public List<Integer> getOrderedRoutes() {
        return orderedRoutes;
    }

    public void setOrderedRoutes(List<Integer> orderedRoutes) {
        this.orderedRoutes = orderedRoutes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((avList == null) ? 0 : avList.hashCode());
        result = prime * result + ((routeList == null) ? 0 : routeList.hashCode());
        result = prime * result + substationId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SubstationRouteMapping other = (SubstationRouteMapping) obj;
        if (avList == null) {
            if (other.avList != null)
                return false;
        } else if (!avList.equals(other.avList))
            return false;
        if (routeList == null) {
            if (other.routeList != null)
                return false;
        } else if (!routeList.equals(other.routeList))
            return false;
        if (substationId != other.substationId)
            return false;
        return true;
    }

    
    public String getSubstationName() {
        return substationName;
    }

    public void setSubstationName(String substationName) {
        this.substationName = substationName;
    }

    @Override
    public String toString() {
        return String
                .format("SubstationRouteMapping [substationId=%s, routeList=%s, avList=%s]",
                    substationId, routeList, avList);
    }
}
