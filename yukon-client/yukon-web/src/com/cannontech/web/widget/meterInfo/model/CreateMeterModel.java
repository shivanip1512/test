package com.cannontech.web.widget.meterInfo.model;

import com.cannontech.common.pao.PaoType;

public class CreateMeterModel extends MeterModel {
    
    private PaoType type;
    private boolean createPoints = true;
    private Boolean copyPoints = false;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private Integer address;
    private Integer portId;
    private int routeId;

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public boolean isCreatePoints() {
        return createPoints;
    }

    public void setCreatePoints(boolean createPoints) {
        this.createPoints = createPoints;
    }

    public boolean isCopyPoints() {
        return copyPoints;
    }
    
    public void setCopyPoints(boolean copyPoints) {
        this.copyPoints = copyPoints;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }
    
    public Integer getPortId() {
        return portId;
    }

    public void setPortId(Integer portId) {
        this.portId = portId;
    }
}