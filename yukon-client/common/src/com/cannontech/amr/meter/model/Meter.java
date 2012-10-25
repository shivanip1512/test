package com.cannontech.amr.meter.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.PaoType;


/** 
 * This is the "PLC" meter.
 */
public class Meter extends YukonMeter { 
    private String route;
    private int routeId;
    private String address;

    public Meter() {
    }

    /**
     * @return the address or "" if none is assigned
     */
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the route name or "" if no route is assigned
     */
    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("name", getName());
        tsc.append("deviceId", getPaoIdentifier().getPaoId());
        tsc.append("type", getPaoIdentifier().getPaoType());
        return tsc.toString();
    }

    public int getDeviceId() {
        return getPaoIdentifier().getPaoId();
    }

    public PaoType getPaoType() {
        return getPaoIdentifier().getPaoType();
    }
}