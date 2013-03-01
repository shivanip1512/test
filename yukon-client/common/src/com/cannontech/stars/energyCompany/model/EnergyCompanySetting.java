package com.cannontech.stars.energyCompany.model;

import static com.cannontech.core.roleproperties.InputTypeFactory.stringType;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;

import com.cannontech.stars.energyCompany.EnergyCompanySettingType;

public class EnergyCompanySetting {
    
    private Integer id;
    private Integer energyCompanyId;
    private EnergyCompanySettingType type;
    private Object value;
    private SettingStatus status;
    private String comments;
    private Instant lastChanged;
    
    public EnergyCompanySetting(EnergyCompanySetting other) {
        this.id = other.getId();
        this.comments = other.getComments();
        this.energyCompanyId = other.getEnergyCompanyId();
        this.lastChanged = other.getLastChanged();
        this.type = other.getType();
        this.value = other.getValue();
        this.status = other.getStatus();
    }

    public EnergyCompanySetting() {
        
    }
    
    public Integer getEnergyCompanyId() {
        return energyCompanyId;
    }
    
    public void setEnergyCompanyId(Integer energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    public EnergyCompanySettingType getType() {
        return type;
    }
    
    public void setType(EnergyCompanySettingType type) {
        this.type = type;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object obj) {
        this.value = obj;// InputTypeFactory.convertPropertyValue(value, type.getType());
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public Instant getLastChanged() {
        return lastChanged;
    }
    
    public void setLastChanged(Instant lastChanged) {
        this.lastChanged = lastChanged;
    }
    
    public boolean isNonDefault() {
        if (value == null) {
            return type.getDefaultValue() != null;
        }
        return !value.equals(type.getDefaultValue());
    }

    public SettingStatus getStatus() {
        return status;
    }

    public void setStatus(SettingStatus status) {
        this.status = status;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }

    /**
     * Checks this setting with setting other. 
     * 
     * Returns true if either value, set, or comments are different.
     */
    public boolean isChanged(EnergyCompanySetting other) {
        boolean changed = false;
        
        if (type.getType().equals(stringType())) {
            changed |= StringUtils.isBlank((String)other.getValue()) ? !StringUtils.isBlank((String)value) : !other.getValue().equals(value);
        } else {
            changed |= other.getValue() == null ? value !=null : !other.getValue().equals(value);
        }
        changed |= StringUtils.isBlank(other.getComments()) ? !StringUtils.isBlank(comments) : !other.getComments().equals(comments);
        changed |= other.getStatus() == null ? status !=null : !other.getStatus().equals(status);
        return changed;
    }
    
    public static EnergyCompanySetting getDefault(EnergyCompanySettingType type, int ecId) {
        EnergyCompanySetting defaultSetting = new EnergyCompanySetting();
        defaultSetting.setEnergyCompanyId(ecId);
        defaultSetting.setType(type);
        defaultSetting.setStatus(type.getDefaultStatus());
        defaultSetting.setValue(type.getDefaultValue());
        return defaultSetting;
    }
}
