package com.cannontech.common.device.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceBaseModel implements YukonDevice {
    private Integer deviceId;
    private PaoType deviceType;
    private String deviceName;
    private Boolean enable;

    public DeviceBaseModel of(LiteYukonPAObject pao) {
        deviceId = pao.getPaoIdentifier().getPaoId();
        deviceType = pao.getPaoType();
        deviceName = pao.getPaoName();
        enable = (pao.getDisableFlag().equals("N") ? true : false);
        return this;
    }

    public DeviceBaseModel(Integer deviceId, PaoType deviceType, String deviceName, Boolean enable) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
        this.enable = enable;
    }

    public DeviceBaseModel() {
        super();
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public PaoType getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(PaoType deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Override
    @JsonIgnore
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(deviceId, deviceType);
    }
}
