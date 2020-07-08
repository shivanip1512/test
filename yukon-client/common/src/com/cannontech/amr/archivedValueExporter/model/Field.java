package com.cannontech.amr.archivedValueExporter.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Field {
    
    private FieldType type = FieldType.DEVICE_NAME;
    private ExportAttribute attribute;
    private String description;
    
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
    
    public boolean isAttributeType() {
        return type == FieldType.ATTRIBUTE;
    }
    
    public boolean isPlainTextType() {
        return type == FieldType.PLAIN_TEXT;
    }
    
    public boolean isDeviceType() {
        return type == FieldType.DEVICE_TYPE;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRuntimeType() {
        return type == FieldType.RUNTIME;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
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
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
    }
}