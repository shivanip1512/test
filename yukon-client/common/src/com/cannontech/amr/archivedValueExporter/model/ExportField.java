package com.cannontech.amr.archivedValueExporter.model;

import java.text.DecimalFormat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
    private YukonRoundingMode roundingMode;
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

    public YukonRoundingMode getRoundingMode() {
        return roundingMode;
    }

    public void setRoundingMode(YukonRoundingMode roundingMode) {
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
    
    /**
     * This method takes care of any modifying that needs to take place
     * on the valueString to output the wanted representation.
     *
     * @param valueString - string to be modified
     * @return valueString with padding and maxLength applied
     */
    public String padValue(String valueString) {
        // This if checks to see if we need more padding or
        // to truncate from the front of the value
        if (getMaxLength() > 0) {
            int neededPadSize = maxLength - valueString.length();
            if (neededPadSize > 0) {
                if (padChar != null && !padChar.equalsIgnoreCase("")) {
                    // This generates the padding string that will be added to
                    // the beginning/end of the string.
                    String paddedStr = "";
                    for (int i = 0; i < neededPadSize; i++) {
                        paddedStr += padChar;
                    }
                    // Put the padding on the left or right
                    if (padSide == PadSide.LEFT) {
                        valueString = paddedStr + valueString;
                    }
                    else if (padSide == PadSide.RIGHT) {
                        valueString += paddedStr;
                    }
                }
                // Too much padding truncating first part of the valueString
            } else {
                int desiredStartPos = -neededPadSize;
                valueString = valueString.substring(desiredStartPos, valueString.length());
            }
        }
        return valueString;
    }
    
    /**
     * Formats value by applying the pattern.
     *
     * @param value
     * @return formatted String
     */
    public String formatValue(double value) {
        DecimalFormat formatter = new DecimalFormat(pattern);
        formatter.setRoundingMode(roundingMode.getRoundingMode());
        String formatedValue = formatter.format(value);
        return formatedValue;
    }

    /**
     * Formats date by applying the pattern.
     *
     * @param dateTime
     * @return formatted String
     */
    public String formatTimestamp(DateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        String formatedDate = dateTime.toString(formatter);
        return formatedDate;
    }
}
