package com.cannontech.stars.energyCompany.model;

import static com.cannontech.core.roleproperties.InputTypeFactory.*;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;

import com.cannontech.stars.energyCompany.EnergyCompanySettingType;

public class EnergyCompanySetting {

    private Integer id;
    private Integer energyCompanyId;
    private EnergyCompanySettingType type;
    private Object value;
    private boolean enabled;
    private String comments;
    private Instant lastChanged;
    
    public EnergyCompanySetting(EnergyCompanySetting other) {
        id = other.getId();
        comments = other.getComments();
        energyCompanyId = other.getEnergyCompanyId();
        lastChanged = other.getLastChanged();
        type = other.getType();
        value = other.getValue();
        enabled = other.isEnabled();
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
        value = obj;
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

    public boolean isEnabled() {
        if (type.isUsesEnabledField()) {
            return enabled;
        } else {
            return true;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
        changed |= isValueChanged(other);
        changed |= StringUtils.isBlank(other.getComments()) ? !StringUtils.isBlank(comments) : !other.getComments().equals(comments);
        changed |= isEnabled() != other.isEnabled();
        return changed;
    }

    public boolean isValueChanged(EnergyCompanySetting other) {
        boolean changed = false;

        if (type.getType().equals(stringType())) {
            changed = StringUtils.isBlank((String)other.getValue()) ? !StringUtils.isBlank((String)value) : !other.getValue().equals(value);
        } else {
            changed = other.getValue() == null ? value !=null : !other.getValue().equals(value);
        }

        return changed;
    }
    
    public static EnergyCompanySetting getDefault(EnergyCompanySettingType type, int ecId) {
        EnergyCompanySetting defaultSetting = new EnergyCompanySetting();
        defaultSetting.setEnergyCompanyId(ecId);
        defaultSetting.setType(type);
        // if this setting uses enabled field, default is false, else its true
        defaultSetting.setEnabled(!type.isUsesEnabledField());
        defaultSetting.setValue(type.getDefaultValue());
        return defaultSetting;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comments == null) ? 0 : comments.hashCode());
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((energyCompanyId == null) ? 0 : energyCompanyId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastChanged == null) ? 0 : lastChanged.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EnergyCompanySetting other = (EnergyCompanySetting) obj;
        if (comments == null) {
            if (other.comments != null) {
                return false;
            }
        } else if (!comments.equals(other.comments)) {
            return false;
        }
        if (enabled != other.enabled) {
            return false;
        }
        if (energyCompanyId == null) {
            if (other.energyCompanyId != null) {
                return false;
            }
        } else if (!energyCompanyId.equals(other.energyCompanyId)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (lastChanged == null) {
            if (other.lastChanged != null) {
                return false;
            }
        } else if (!lastChanged.equals(other.lastChanged)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
