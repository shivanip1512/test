package com.cannontech.web.tools.mapping.model;

import java.util.ArrayList;
import java.util.List;

import org.geojson.FeatureCollection;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.network.RouteData;

public class RouteInfo {

    private static final String nameKey= "yukon.web.modules.operator.mapNetwork.routeFlagType.";
    
    private int deviceId;
    private RouteData route;
    private FeatureCollection location;
    private String commaDelimitedRouteFlags;

    public RouteInfo(int deviceId, RouteData route, FeatureCollection location, MessageSourceAccessor accessor) {
        this.deviceId = deviceId;
        this.route = route;
        this.location = location;
        List<String> flags = new ArrayList<>();
        route.getRouteFlags().forEach(flag -> flags.add(accessor.getMessage(nameKey + flag.name())));
        commaDelimitedRouteFlags = String.join(", ", flags);
    }

    public int getDeviceId() {
        return deviceId;
    }

    public RouteData getRoute() {
        return route;
    }

    public FeatureCollection getLocation() {
        return location;
    }
    
    public String getCommaDelimitedRouteFlags() {
        return commaDelimitedRouteFlags;
    }
}
