package com.cannontech.common.device.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceBaseModel implements YukonDevice {
    @JsonProperty("id")
    private Integer deviceId;
    @JsonProperty("type")
    private PaoType deviceType;
    @JsonProperty("name")
    private String deviceName;
    private Boolean enable;

    public DeviceBaseModel of(LiteYukonPAObject pao) {
        deviceId = pao.getPaoIdentifier().getPaoId();
        deviceType = pao.getPaoType();
        deviceName = pao.getPaoName();
        enable = (pao.getDisableFlag().equals("N") ? true : false);
        return this;
    }

    public DeviceBaseModel(Integer id, PaoType type, String name, Boolean enable) {
        this.deviceId = id;
        this.deviceType = type;
        this.deviceName = name;
        this.enable = enable;
    }

    public DeviceBaseModel() {
        super();
    }

    public Integer getId() {
        return deviceId;
    }

    public void setId(Integer id) {
        this.deviceId = id;
    }

    public PaoType getType() {
        return deviceType;
    }

    public void setType(PaoType type) {
        this.deviceType = type;
    }

    public String getName() {
        return deviceName;
    }

    public void setName(String name) {
        this.deviceName = name;
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
