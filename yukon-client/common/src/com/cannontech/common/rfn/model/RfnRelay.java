package com.cannontech.common.rfn.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RfnRelay implements YukonPao {
    
    private int deviceId;
    private String name;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private PaoType type;
    

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    
    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }
    
    public static RfnRelay of(RfnDevice device) {
        
        RfnRelay model = new RfnRelay();
        model.setDeviceId(device.getPaoIdentifier().getPaoId());
        model.setManufacturer(device.getRfnIdentifier().getSensorManufacturer());
        model.setModel(device.getRfnIdentifier().getSensorModel());
        model.setName(device.getName());
        model.setSerialNumber(device.getRfnIdentifier().getSensorSerialNumber());
        model.setType(device.getPaoIdentifier().getPaoType());
        
        return model;
    }

    @Override
    @JsonIgnore
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(deviceId, type);
    }


}