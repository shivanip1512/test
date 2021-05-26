package com.cannontech.web.api.route.model;

import org.apache.commons.lang3.BooleanUtils;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.route.RouteBase;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RouteBaseModel<T extends RouteBase> extends DeviceBaseModel implements DBPersistentConverter<T> {

    private Integer signalTransmitterId;
    private Boolean defaultRoute;

    public Integer getSignalTransmitterId() {
        return signalTransmitterId;
    }

    public void setSignalTransmitterId(Integer signalTransmitterId) {
        this.signalTransmitterId = signalTransmitterId;
    }

    public Boolean getDefaultRoute() {
        return defaultRoute;
    }

    public void setDefaultRoute(Boolean defaultRoute) {
        this.defaultRoute = defaultRoute;
    }

    @Override
    public void buildModel(T route) {
        setId(route.getRouteID());
        setName(route.getRouteName());
        setDefaultRoute(route.getDefaultRoute() == "N" ? true : false);
        setSignalTransmitterId(route.getDeviceID());
    }

    @Override
    public void buildDBPersistent(T route) {
        if (getId() != null) {
            route.setRouteID(getId());
        }
        if (getName() != null) {
            route.setRouteName(getName());
        }
        if (getSignalTransmitterId() != null) {
            route.setDeviceID(getSignalTransmitterId());
        }
        route.setDefaultRoute(BooleanUtils.isFalse(getDefaultRoute()) ? "Y" : "N");
    }
}