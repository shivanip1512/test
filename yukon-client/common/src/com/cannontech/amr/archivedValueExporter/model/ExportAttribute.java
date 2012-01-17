package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.pao.attribute.model.Attribute;


public class ExportAttribute{
    
    private int attributeId;
    private int formatId;
    private Attribute attribute;
    private DataSelection dataSelection;
    private Integer daysPrevious = 1;
    
    public int getAttributeId() {
        return attributeId;
    }
    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }
    public int getFormatId() {
        return formatId;
    }
    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }
    public Attribute getAttribute() {
        return attribute;
    }
    public void setAttribute(Attribute attribute) {
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
}
