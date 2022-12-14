package com.cannontech.common.device.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DeviceBaseModel implements YukonDevice {

    private Integer id;
    private PaoType type;
    private String name;
    private Boolean enable;

    public DeviceBaseModel of(LiteYukonPAObject pao) {
        id = pao.getPaoIdentifier().getPaoId();
        type = pao.getPaoType();
        name = pao.getPaoName();
        enable = (pao.getDisableFlag().equals("N") ? true : false);
        return this;
    }

    public DeviceBaseModel(Integer id, PaoType type, String name, Boolean enable) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.enable = enable;
    }

    public DeviceBaseModel() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return new PaoIdentifier(id, type);
    }
}
