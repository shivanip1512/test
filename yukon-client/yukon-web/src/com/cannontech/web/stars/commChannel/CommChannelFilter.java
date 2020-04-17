package com.cannontech.web.stars.commChannel;

import com.cannontech.common.pao.PaoType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CommChannelFilter {

    private Integer id;
    private String name;
    private Boolean enable;
    private PaoType type;
    @JsonIgnore
    private boolean isWebSupportedType;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public PaoType getType() {
        return type;
    }
    public void setType(PaoType type) {
        this.type = type;
    }
    public boolean isWebSupportedType() {
        return isWebSupportedType;
    }
    public void setWebSupportedType(boolean isWebSupportedType) {
        this.isWebSupportedType = isWebSupportedType;
    }
    
}
