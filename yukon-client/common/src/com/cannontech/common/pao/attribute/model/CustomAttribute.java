package com.cannontech.common.pao.attribute.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"id"}, allowGetters = true, ignoreUnknown = true)
public class CustomAttribute implements Attribute {
    public static String i18Key = "yukon.common.attribute.customAttribute.";
    private Integer customAttributeId;
    private String name;

    public CustomAttribute() {

    }

    public CustomAttribute(Integer customAttributeId, String name) {
        this.customAttributeId = customAttributeId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getI18Key() {
        return i18Key + customAttributeId;
    }

    @Override
    @JsonIgnore
    public String getKey() {
        return String.valueOf(customAttributeId);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return name == null;
    }

    @Override
    @JsonIgnore
    public MessageSourceResolvable getMessage() {
        return YukonMessageSourceResolvable.createDefault(getI18Key(), name);
    }
    
    public Integer getCustomAttributeId() {
        return customAttributeId;
    }

    public void setCustomAttributeId(Integer customAttributeId) {
        this.customAttributeId = customAttributeId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customAttributeId == null) ? 0 : customAttributeId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomAttribute other = (CustomAttribute) obj;
        if (customAttributeId == null) {
            if (other.customAttributeId != null)
                return false;
        } else if (!customAttributeId.equals(other.customAttributeId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
