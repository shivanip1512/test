package com.cannontech.web.tools.mapping.model;

import java.util.ArrayList;
import java.util.List;

import org.geojson.FeatureCollection;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.network.RouteData;
import com.cannontech.common.rfn.model.RfnDevice;

public class RouteInfo {

    private static final String nameKey= "yukon.web.modules.operator.mapNetwork.routeFlagType.";
    
    private RfnDevice device;
    private RouteData route;
    private FeatureCollection location;
    private String commaDelimitedRouteFlags;

    public RouteInfo(RfnDevice device, RouteData route, FeatureCollection location, MessageSourceAccessor accessor) {
        this.device = device;
        this.route = route;
        this.location = location;
        List<String> flags = new ArrayList<>();
        route.getRouteFlags().forEach(flag -> flags.add(accessor.getMessage(nameKey + flag.name())));
        commaDelimitedRouteFlags = String.join(", ", flags);
    }

    public RfnDevice getDevice() {
        return device;
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
