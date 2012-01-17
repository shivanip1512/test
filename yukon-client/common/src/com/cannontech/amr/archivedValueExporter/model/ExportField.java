package com.cannontech.amr.archivedValueExporter.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;


public class ExportField implements Displayable{
    private int fieldId;
    private int formatId;
    private FieldType fieldType;
    private ExportAttribute attribute;
    private AttributeField attributeField;
    private String pattern;
    private Integer maxLength = 0;
    private String padChar;
    private PadSide padSide;
    private String roundingMode;
    private MissingAttribute missingAttribute;
    private String missingAttributeValue;


    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getFormatId() {
        return formatId;
    }

    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
 
    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public String getPadChar() {
        return padChar;
    }

    public void setPadChar(String padChar) {
        this.padChar = padChar;
    }
    
    public String getMissingAttributeValue() {
        return missingAttributeValue;
    }

    public void setMissingAttributeValue(String missingAttributeValue) {
        this.missingAttributeValue = missingAttributeValue;
    }

    public String getRoundingMode() {
        return roundingMode;
    }

    public void setRoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public AttributeField getAttributeField() {
        return attributeField;
    }

    public void setAttributeField(AttributeField attributeField) {
        this.attributeField = attributeField;
    }

    public ExportAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(ExportAttribute attribute) {
        this.attribute = attribute;
    }

    public PadSide getPadSide() {
        return padSide;
    }

    public void setPadSide(PadSide padSide) {
        this.padSide = padSide;
    }

    public MissingAttribute getMissingAttribute() {
        return missingAttribute;
    }

    public void setMissingAttribute(MissingAttribute missingAttribute) {
        this.missingAttribute = missingAttribute;
    }

    @Override
    public MessageSourceResolvable getMessage() {
        MessageSourceResolvable  messageSourceResolvable = null;
        if(fieldType != null){
            if(fieldType == FieldType.ATTRIBUTE){
                messageSourceResolvable = attribute.getAttribute().getMessage();
            }else{
                messageSourceResolvable = fieldType.getMessage();
            }
        }
        return messageSourceResolvable;
    }
}
