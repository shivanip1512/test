package com.cannontech.amr.meter.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.PaoIdentifier;

/** 
 * This is the "PLC" meter.
 */
public class PlcMeter extends YukonMeter {

    private String route = "";
    private int routeId;
    private String address = "";
    
    public PlcMeter() {
        super();
    }

    public PlcMeter(PaoIdentifier paoIdentifier, String meterNumber, String name, boolean disabled,  
            String routeName, int routeId, String address) {
        super(paoIdentifier, meterNumber, name, disabled);
        this.route = routeName;
        this.routeId = routeId;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address == null ? "" : address;
    }

    @Override
    public String getSerialOrAddress() {
        return getAddress();
    }

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
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        tsb.append("address", getAddress());
        tsb.append("route", getRoute());
        return tsb.toString();
    }
}