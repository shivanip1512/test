package com.cannontech.web.capcontrol.models;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;

public abstract class PaoModel implements YukonPao {
    
    protected Integer id;
    protected PaoType type;
    protected String name;
    protected String description;
    protected boolean disabled;
    
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isDisabled() {
        return disabled;
    }
    
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return PaoIdentifier.of(id, type);
    }
    
    @Override
    public String toString() {
        return String.format("PaoModel [id=%s, name=%s, description=%s, disabled=%s]", id, name, description, disabled);
    }
    
}