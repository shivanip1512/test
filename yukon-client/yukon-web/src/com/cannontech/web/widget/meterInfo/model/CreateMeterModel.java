package com.cannontech.web.widget.meterInfo.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoType;

public class CreateMeterModel extends MeterModel {
    
    private PaoType type;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private Integer address;
    private Integer portId;
    private int routeId;
    private PointCreation pointCreation;
    public static enum PointCreation implements DisplayableEnum {
        NONE,
        DEFAULT,
        COPY;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.amr.pointCreation." + name();
        }
    }
    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
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

    public PointCreation getPointCreation() {
        return pointCreation;
    }

    public void setPointCreation(PointCreation pointCreation) {
        this.pointCreation = pointCreation;
    }
    
    public boolean isCreatePoints() {
        if(PointCreation.DEFAULT == getPointCreation()) {
            return true;
        }
        return false;
    }

    public boolean isCopyPoints() {
        if(PointCreation.COPY == getPointCreation()) {
            return true;
        }
        return false;
    }
}