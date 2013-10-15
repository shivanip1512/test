package com.cannontech.amr.meter.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.PaoIdentifier;

/** 
 * This is the "PLC" meter.
 */
public class PlcMeter extends YukonMeter {

    private String route = "";
    private int routeId;
    private String address = "";

    public PlcMeter(PaoIdentifier paoIdentifier, String meterNumber, String name, boolean disabled,  
            String routeName, int routeId, String address) {
        super(paoIdentifier, meterNumber, name, disabled);
        this.route = routeName;
        this.routeId = routeId;
        this.address = address;
    }

    /**
     * @return the address or "" if none is assigned
     */
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address == null ? "" : address;
    }

    /**
     * @return the route name or "" if no route is assigned
     */
    @Override
    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route == null ? "" : route;
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
        tsc.append("meter", super.toString());
        tsc.append("address", getAddress());
        tsc.append("route", getRoute());
        return tsc.toString();
    }
}