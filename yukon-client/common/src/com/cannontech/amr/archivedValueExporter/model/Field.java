package com.cannontech.amr.archivedValueExporter.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;

public class Field implements Displayable {
    
    private int fieldId;
    private FieldType type;
    private ExportAttribute attribute;
    
    public Field(int fieldId, FieldType type, ExportAttribute attribute){
        this.fieldId = fieldId;
        this.type = type;
        this.attribute = attribute;
        
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
        return type == FieldType.ATTRIBUTE? attribute.getAttribute().getKey(): type.getKey();
    }

    @Override
    public MessageSourceResolvable getMessage() {
        return type == FieldType.ATTRIBUTE? attribute.getAttribute().getMessage() : type.getMessage();
    }
}
