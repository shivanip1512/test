package com.cannontech.amr.meter.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;


public class Meter implements YukonDevice { 
    private int deviceId;
    private PaoType type;

    private String name;
    private String typeStr;

    private boolean disabled;

    private String route;
    private int routeId;
    private String address;

    private String meterNumber;
    private String manufacturer;
    private String model;
    private String serialNumber;

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
     * @return the meter number or "" if none is assigned
     */
    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
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
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(deviceId, type);
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("name", getName());
        tsc.append("deviceId", getDeviceId());
        tsc.append("type", getTypeStr());
        return tsc.toString();
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public PaoType getDeviceType() {
        return PaoType.getForId(getType());
    }

    public void setDeviceType(PaoType deviceType) {
        setType(deviceType.getDeviceTypeId());
    }

    public int getType() {
        return type.getDeviceTypeId();
    }

    public void setType(int type) {
        this.type = PaoType.getForId(type);
    }

    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}