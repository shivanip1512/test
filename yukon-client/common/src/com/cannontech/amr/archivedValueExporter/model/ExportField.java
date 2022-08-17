package com.cannontech.amr.archivedValueExporter.model;

import java.text.DecimalFormat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExportField implements Displayable {
    
    private int fieldId;
    private Field field = new Field();
    private int formatId;
    private AttributeField attributeField;
    private ReadingPattern readingPattern = ReadingPattern.FIVE_ZERO;
    private TimestampPattern timestampPattern = TimestampPattern.MONTH_DAY_YEAR; // MERICA!
    private Integer maxLength = 0;
    private String padChar;
    private PadSide padSide = PadSide.NONE;
    private YukonRoundingMode roundingMode;
    private MissingAttribute missingAttribute;
    private String missingAttributeValue;
    private String pattern;
    
    public int getFieldId() {
        return fieldId;
    }
    
    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }
    
    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public int getFormatId() {
        return formatId;
    }

    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }

    public AttributeField getAttributeField() {
        return attributeField;
    }

    public void setAttributeField(AttributeField attributeField) {
        this.attributeField = attributeField;
    }

    public ReadingPattern getReadingPattern() {
        boolean patternFound = false;
        if(isValue()){
            for (ReadingPattern type : ReadingPattern.values()) {
                if (type.getPattern().equals(pattern)) {
                    readingPattern = type;
                    patternFound = true;
                    break;
                }
            }
            if(!patternFound) readingPattern = ReadingPattern.valueOf("CUSTOM");
        }
        return readingPattern;
    }

    public void setReadingPattern(ReadingPattern readingPattern) {
        this.readingPattern = readingPattern;
    }

    public TimestampPattern getTimestampPattern() {
        boolean patternFound = false;
        if(isTimestamp()){
            for (TimestampPattern type : TimestampPattern.values()) {
                if (type.getPattern().equals(pattern)) {
                    timestampPattern = type;
                    patternFound = true;
                    break;
                }
            }
            if(!patternFound) timestampPattern = TimestampPattern.valueOf("CUSTOM");
        }
        return timestampPattern;
    }

    public void setTimestampPattern(TimestampPattern timestampPattern) {
        this.timestampPattern = timestampPattern;
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

    public PadSide getPadSide() {
        return padSide;
    }

    public void setPadSide(PadSide padSide) {
        this.padSide = padSide;
    }

    public YukonRoundingMode getRoundingMode() {
        return roundingMode;
    }

    public void setRoundingMode(YukonRoundingMode roundingMode) {
        this.roundingMode = roundingMode;
    }

    public MissingAttribute getMissingAttribute() {
        return missingAttribute;
    }

    public void setMissingAttribute(MissingAttribute missingAttribute) {
        this.missingAttribute = missingAttribute;
    }

    public String getMissingAttributeValue() {
        return missingAttributeValue;
    }

    public void setMissingAttributeValue(String missingAttributeValue) {
        this.missingAttributeValue = missingAttributeValue;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isValue() {
        return field.getType() == FieldType.POINT_VALUE || (field.isAttributeType() && attributeField == AttributeField.VALUE);
    }
    
    public boolean isTimestamp() {
        return field.getType() == FieldType.POINT_TIMESTAMP || 
                (field.isAttributeType() && attributeField == AttributeField.TIMESTAMP) ||
                field.getType() == FieldType.RUNTIME;
    }
    
    @JsonIgnore
    @Override
    public MessageSourceResolvable getMessage() {
        MessageSourceResolvable  messageSourceResolvable = null;
        if (field.getType() != null) {
            if (field.getType() == FieldType.ATTRIBUTE && field.getAttribute() != null
                && field.getAttribute().getAttribute() != null) {
                messageSourceResolvable = field.getAttribute().getAttribute().getMessage();
            } else {
                messageSourceResolvable = field.getType().getMessage();
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
        if (maxLength > 0) {
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
     * Formats value by applying the value pattern.
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
     * Formats date by applying the timestamp pattern.
     *
     * @param dateTime
     * @return formatted String
     */
    public String formatTimestamp(DateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        
        return dateTime.toString(formatter);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((attributeField == null) ? 0 : attributeField.hashCode());
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        result = prime * result + fieldId;
        result = prime * result + formatId;
        result = prime * result
                + ((maxLength == null) ? 0 : maxLength.hashCode());
        result = prime
                * result
                + ((missingAttribute == null) ? 0 : missingAttribute.hashCode());
        result = prime
                * result
                + ((missingAttributeValue == null) ? 0 : missingAttributeValue
                        .hashCode());
        result = prime * result + ((padChar == null) ? 0 : padChar.hashCode());
        result = prime * result + ((padSide == null) ? 0 : padSide.hashCode());
        result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
        result = prime * result
                + ((readingPattern == null) ? 0 : readingPattern.hashCode());
        result = prime * result
                + ((roundingMode == null) ? 0 : roundingMode.hashCode());
        result = prime
                * result
                + ((timestampPattern == null) ? 0 : timestampPattern.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExportField other = (ExportField) obj;
        if (attributeField != other.attributeField) {
            return false;
        }
        if (field == null) {
            if (other.field != null) {
                return false;
            }
        } else if (!field.equals(other.field)) {
            return false;
        }
        if (fieldId != other.fieldId) {
            return false;
        }
        if (formatId != other.formatId) {
            return false;
        }
        if (maxLength == null) {
            if (other.maxLength != null) {
                return false;
            }
        } else if (!maxLength.equals(other.maxLength)) {
            return false;
        }
        if (missingAttribute != other.missingAttribute) {
            return false;
        }
        if (missingAttributeValue == null) {
            if (other.missingAttributeValue != null) {
                return false;
            }
        } else if (!missingAttributeValue.equals(other.missingAttributeValue)) {
            return false;
        }
        if (padChar == null) {
            if (other.padChar != null) {
                return false;
            }
        } else if (!padChar.equals(other.padChar)) {
            return false;
        }
        if (padSide != other.padSide) {
            return false;
        }
        if (pattern == null) {
            if (other.pattern != null) {
                return false;
            }
        } else if (!pattern.equals(other.pattern)) {
            return false;
        }
        if (readingPattern != other.readingPattern) {
            return false;
        }
        if (roundingMode != other.roundingMode) {
            return false;
        }
        if (timestampPattern != other.timestampPattern) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String
                .format("ExportField [fieldId=%s, field=%s, formatId=%s, attributeField=%s, readingPattern=%s, timestampPattern=%s, maxLength=%s, padChar=%s, padSide=%s, roundingMode=%s, missingAttribute=%s, missingAttributeValue=%s, pattern=%s]",
                        fieldId, field, formatId, attributeField,
                        readingPattern, timestampPattern, maxLength, padChar,
                        padSide, roundingMode, missingAttribute,
                        missingAttributeValue, pattern);
    }
    
}