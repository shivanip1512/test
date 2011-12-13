package com.cannontech.amr.archivedValueExporter.model;


public class ExportField {
    private int fieldId;
    private int formatId;
    private FieldType fieldType;
    private ExportAttribute attribute;
    private AttributeField attributeField;
    private String pattern;
    private int maxLength;
    private String padChar;
    private String padSide;
    private String roundingMode;
    private String missingAttributeValue;
    private boolean multiplierRemovedFlag;

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

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
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

    public boolean isMultiplierRemovedFlag() {
        return multiplierRemovedFlag;
    }

    public void setMultiplierRemovedFlag(boolean multiplierRemovedFlag) {
        this.multiplierRemovedFlag = multiplierRemovedFlag;
    }

    public String getPadSide() {
        return padSide;
    }

    public void setPadSide(String padSide) {
        this.padSide = padSide;
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
}
