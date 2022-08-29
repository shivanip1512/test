package com.cannontech.web.api.route.model;

import org.apache.commons.lang3.BooleanUtils;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.web.api.route.JsonDeserializeRouteTypeLookup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonPropertyOrder({ "deviceId", "deviceName", "deviceType", "signalTransmitterId", "defaultRoute", "carrierRoute", "repeaters" })
@JsonIgnoreProperties(value = { "id" }, allowGetters = true, ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializeRouteTypeLookup.class)
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
    public void buildModel(T routeBase) {
        setDeviceId(routeBase.getRouteID());
        setDeviceName(routeBase.getRouteName());
        setDefaultRoute(routeBase.getDefaultRoute().equals("N") ? false : true );
        setSignalTransmitterId(routeBase.getDeviceID());
        setDeviceType(routeBase.getPaoType());
    }

    @Override
    public void buildDBPersistent(T routeBase) {
        if (getDeviceId() != null) {
            routeBase.setRouteID(getDeviceId());
        }
        if (getDeviceName() != null) {
            routeBase.setRouteName(getDeviceName());
        }
        if (getSignalTransmitterId() != null) {
            routeBase.setDeviceID(getSignalTransmitterId());
        }
        routeBase.setDefaultRoute(BooleanUtils.isFalse(getDefaultRoute()) ? "N" : "Y");
    }

}