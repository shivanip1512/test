package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class ExportAttribute {
    
    private int attributeId;
    private int formatId;
    private BuiltInAttribute attribute;
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
    
    public BuiltInAttribute getAttribute() {
        return attribute;
    }
    
    public void setAttribute(BuiltInAttribute attribute) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + attributeId;
        result = prime * result
                + ((dataSelection == null) ? 0 : dataSelection.hashCode());
        result = prime * result + daysPrevious;
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
        if (attribute != other.attribute)
            return false;
        if (attributeId != other.attributeId)
            return false;
        if (dataSelection != other.dataSelection)
            return false;
        if (daysPrevious != other.daysPrevious)
            return false;
        if (formatId != other.formatId)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
                .format("ExportAttribute [attributeId=%s, formatId=%s, attribute=%s, dataSelection=%s, daysPrevious=%s]",
                        attributeId, formatId, attribute, dataSelection,
                        daysPrevious);
    }
    
    public boolean isStatusType() {
        return attribute.isStatusType();
    }
}