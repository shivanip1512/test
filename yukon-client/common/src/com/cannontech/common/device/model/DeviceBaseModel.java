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

    public static DeviceBaseModel of(LiteYukonPAObject pao) {
        Integer id = pao.getPaoIdentifier().getPaoId();
        PaoType type = pao.getPaoType();
        String name = pao.getPaoName();
        Boolean enable = (pao.getDisableFlag().equals("N") ? false : true);
        return new DeviceBaseModel(id, type, name, enable);
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
