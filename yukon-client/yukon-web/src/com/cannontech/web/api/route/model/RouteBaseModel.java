package com.cannontech.web.api.route.model;

import org.apache.commons.lang3.BooleanUtils;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.web.api.route.JsonDeserializeRouteTypeLookup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(value = { "id" }, allowGetters = true, ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializeRouteTypeLookup.class)
public class RouteBaseModel<T extends RouteBase> extends DeviceBaseModel implements DBPersistentConverter<T> {

    private Integer routeId;
    private Integer signalTransmitterId;
    private Boolean defaultRoute;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

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
    public void buildModel(RouteBase routeBase) {
        setRouteId(routeBase.getRouteID());
        setId(routeBase.getRouteID());
        setName(routeBase.getRouteName());
        setDefaultRoute(routeBase.getDefaultRoute() == "N" ? true : false);
        setSignalTransmitterId(routeBase.getDeviceID());
    }

    @Override
    public void buildDBPersistent(RouteBase routeBase) {
        if (getId() != null) {
            routeBase.setRouteID(getId());
        }
        if (getName() != null) {
            routeBase.setRouteName(getName());
        }
        if (getSignalTransmitterId() != null) {
            routeBase.setDeviceID(getSignalTransmitterId());
        }
        routeBase.setDefaultRoute(BooleanUtils.isFalse(getDefaultRoute()) ? "Y" : "N");
    }

}