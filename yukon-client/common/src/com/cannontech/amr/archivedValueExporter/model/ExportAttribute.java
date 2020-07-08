package com.cannontech.amr.archivedValueExporter.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class ExportAttribute {
    
    private int attributeId;
    private int formatId;
    private String attribute;
    private String description;
    private DataSelection dataSelection;
    private Integer daysPrevious = 1;
    
    @JsonIgnore
    public int getAttributeId() {
        return attributeId;
    }
    
    @JsonIgnore
    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }
    
    @JsonIgnore
    public int getFormatId() {
        return formatId;
    }
    
    @JsonIgnore
    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }
    
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    
    public DataSelection getDataSelection() {
        return dataSelection;
    }
    
    public void setDataSelection(DataSelection dataSelection) {
        this.dataSelection = dataSelection;
    }
    
    public Integer getDaysPrevious() {
        return daysPrevious;
    }
    
    public void setDaysPrevious(Integer daysPrevious) {
        this.daysPrevious = daysPrevious;
    }
    
    /**
     * True is attribute is built in attribute, false is attribute is custom
     */
    public boolean isBuiltInAttribute() {
        try {
            BuiltInAttribute.valueOf(attribute);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public boolean isStatusType() {
        if(isBuiltInAttribute()) {
            return BuiltInAttribute.valueOf(attribute).isStatusType();
        }
        return false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + attributeId;
        result = prime * result + ((dataSelection == null) ? 0 : dataSelection.hashCode());
        result = prime * result + ((daysPrevious == null) ? 0 : daysPrevious.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + formatId;
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
        ExportAttribute other = (ExportAttribute) obj;
        if (attribute == null) {
            if (other.attribute != null)
                return false;
        } else if (!attribute.equals(other.attribute))
            return false;
        if (attributeId != other.attributeId)
            return false;
        if (dataSelection != other.dataSelection)
            return false;
        if (daysPrevious == null) {
            if (other.daysPrevious != null)
                return false;
        } else if (!daysPrevious.equals(other.daysPrevious))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (formatId != other.formatId)
            return false;
        return true;
    }
}