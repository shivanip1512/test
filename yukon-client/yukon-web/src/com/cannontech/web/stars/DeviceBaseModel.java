package com.cannontech.web.stars;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;

public class DeviceBaseModel implements YukonDevice {

    private Integer id;
    private PaoType type;
    private String name;
    private Boolean enable;

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
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(id, type);
    }
}
