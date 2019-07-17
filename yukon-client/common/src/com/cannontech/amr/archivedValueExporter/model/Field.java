package com.cannontech.amr.archivedValueExporter.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Field implements Displayable {
    
    private FieldType type = FieldType.DEVICE_NAME;
    private ExportAttribute attribute;
    
    public Field() {}
    
    public Field(FieldType type, ExportAttribute attribute) {
        this.type = type;
        this.attribute = attribute;
    }
    
    public FieldType getType() {
        return type;
    }
    
    public void setType(FieldType type) {
        this.type = type;
    }
    
    public ExportAttribute getAttribute() {
        return attribute;
    }
    
    public void setAttribute(ExportAttribute attribute) {
        this.attribute = attribute;
    }
    
    public String getDisplayName() {
        return type == FieldType.ATTRIBUTE? attribute.getAttribute().getKey(): type.getKey();
    }
    
    public boolean isAttributeType() {
        return type == FieldType.ATTRIBUTE;
    }
    
    public boolean isPlainTextType() {
        return type == FieldType.PLAIN_TEXT;
    }
    
    public boolean isDeviceType() {
        return type == FieldType.DEVICE_TYPE;
    }
    
    public boolean isRuntimeType() {
        return type == FieldType.RUNTIME;
    }

    @JsonIgnore
    @Override
    public MessageSourceResolvable getMessage() {
        return type == FieldType.ATTRIBUTE? attribute.getAttribute().getMessage() : type.getMessage();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        Field other = (Field) obj;
        if (attribute == null) {
            if (other.attribute != null)
                return false;
        } else if (!attribute.equals(other.attribute))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("Field [type=%s, attribute=%s]", type, attribute);
    }
    
}