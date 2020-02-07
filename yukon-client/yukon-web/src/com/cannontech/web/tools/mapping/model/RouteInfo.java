package com.cannontech.web.tools.mapping.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.geojson.FeatureCollection;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.network.RouteData;
import com.cannontech.common.rfn.model.RfnDevice;

public class RouteInfo extends MappingInfo {

    private static final String nameKey = "yukon.web.modules.operator.mapNetwork.routeFlagType.";

    private RouteData route;
    private String commaDelimitedRouteFlags;
    private Integer descendantCount;

    public RouteInfo(RfnDevice device, RouteData route, FeatureCollection location, MessageSourceAccessor accessor) {
        super(device, location, accessor);
        this.route = route;
        List<String> flags = new ArrayList<>();
        if (route.getRouteFlags() != null && !route.getRouteFlags().isEmpty()) {
            route.getRouteFlags().forEach(flag -> flags.add(accessor.getMessage(nameKey + flag.name())));
            commaDelimitedRouteFlags = String.join(", ", flags);
        }
    }

    public RouteData getRoute() {
        return route;
    }

    public String getCommaDelimitedRouteFlags() {
        return commaDelimitedRouteFlags;
    }
    
    public Integer getDescendantCount() {
        return descendantCount;
    }
    
    public void setDescendantCount(Integer descendantCount) {
        this.descendantCount = descendantCount;
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append(super.toString());
        builder.append("route", route);
        return builder.toString();
    }
}
