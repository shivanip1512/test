package com.cannontech.common.rfn.message.route;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

// Represent Network Tree
public class Route implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier myRfnIdentifier;
    
    private Route prevRoute;
    
    private Map<RfnIdentifier, Route> nextRoutes;
    
    private RouteData routeData; // the routeData is prevRoute based.
                                 // Me is the next hop.

    public RfnIdentifier getMyRfnIdentifier() {
        return myRfnIdentifier;
    }

    public void setMyRfnIdentifier(RfnIdentifier myRfnIdentifier) {
        this.myRfnIdentifier = myRfnIdentifier;
    }

    public Route getPrevRoute() {
        return prevRoute;
    }

    public void setPrevRoute(Route prevRoute) {
        this.prevRoute = prevRoute;
    }

    public Map<RfnIdentifier, Route> getNextRoutes() {
        return nextRoutes;
    }

    public void setNextRoutes(Map<RfnIdentifier, Route> nextRoutes) {
        this.nextRoutes = nextRoutes;
    }

    public RouteData getRouteData() {
        return routeData;
    }

    public void setRouteData(RouteData routeData) {
        this.routeData = routeData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((myRfnIdentifier == null) ? 0 : myRfnIdentifier.hashCode());
        result = prime * result + ((nextRoutes == null) ? 0 : nextRoutes.hashCode());
        result = prime * result + ((prevRoute == null) ? 0 : prevRoute.hashCode());
        result = prime * result + ((routeData == null) ? 0 : routeData.hashCode());
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
        Route other = (Route) obj;
        if (myRfnIdentifier == null) {
            if (other.myRfnIdentifier != null)
                return false;
        } else if (!myRfnIdentifier.equals(other.myRfnIdentifier))
            return false;
        if (nextRoutes == null) {
            if (other.nextRoutes != null)
                return false;
        } else if (!nextRoutes.equals(other.nextRoutes))
            return false;
        if (prevRoute == null) {
            if (other.prevRoute != null)
                return false;
        } else if (!prevRoute.equals(other.prevRoute))
            return false;
        if (routeData == null) {
            if (other.routeData != null)
                return false;
        } else if (!routeData.equals(other.routeData))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("Route [myRfnIdentifier=%s, prevRoute=%s, nextRoutes=%s, routeData=%s]",
                    myRfnIdentifier,
                    prevRoute,
                    nextRoutes,
                    routeData);
    }
}
