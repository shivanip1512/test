package com.cannontech.amr.archivedValueExporter.model;

public class Field {
    
    private int fieldId;
    private FieldType type;
    private ExportAttribute attribute;
    private String displayName;
    
    public Field(int fieldId, FieldType type, ExportAttribute attribute, String displayName){
        this.fieldId = fieldId;
        this.type = type;
        this.attribute = attribute;
        this.displayName = displayName;
        
    }
    public int getFieldId() {
        return fieldId;
    }
    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
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
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
