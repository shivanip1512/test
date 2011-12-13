package com.cannontech.web.amr.archivedValuesExporter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;

public class ExportPreviewFormatter {
    public static String defaultDecimalFormat = "#####.00";
    public static Double testValue = new Double("123456789");
    
    public static String format(ExportFormat format) {
        StringBuilder preview = new StringBuilder();
        if (format != null) {
            String value = "";
            preview.append(processHeader(format));
            for (ExportField field : format.getFields()) {
                if (field.getFieldType().equals(FieldType.PLAIN_TEXT)) {
                    value = processValueString(field, field.getPattern());
                }
                else if (field.getFieldType().equals(FieldType.ATTRIBUTE)) {
                    if (field.getAttributeField().equals(AttributeField.TIMESTAMP)) {
                        SimpleDateFormat formatter = new SimpleDateFormat(field.getPattern());
                        value = formatter.format(new Date());
                        value = processValueString(field, value);
                    } else if (field.getAttributeField().equals(AttributeField.VALUE)) {
                        DecimalFormat formatter = new DecimalFormat(defaultDecimalFormat);
                        if (!StringUtils.isEmpty(field.getPattern())) {
                            formatter = new DecimalFormat(field.getPattern());
                        }
                        formatter.setRoundingMode(RoundingMode.valueOf(field.getRoundingMode()));
                        value = formatter.format(testValue);
                        value = processValueString(field, value);
                    }
                } else {
                    value = processValueString(field, field.getPattern());
                }
            }
            preview.append(value);
            preview.append(processFooter(format));
        }
        return preview.toString().replaceAll("\r\n", "<BR>");

    }
    
 // This method takes care of any modifying that needs to take place
    // on the valueString to output the wanted representation 
    private static String processValueString(ExportField field, String valueString) {
        // This if checks to see if we need more padding or 
        // to truncate from the front of the value
        if(field.getMaxLength() > 0) {
            int neededPadSize = field.getMaxLength() - valueString.length();
            if (neededPadSize > 0) {
                if (!field.getPadChar().equalsIgnoreCase("")){
                    // This generates the padding string that will be added to
                    // the beginning/end of the string.
                    String paddedStr = "";
                    for (int i = 0; i < neededPadSize; i++){
                        paddedStr += field.getPadChar();
                    }
                    // Put the padding on the left or right
                    if (field.getPadSide().equalsIgnoreCase("left")) {
                        valueString = paddedStr + valueString;
                    }
                    if (field.getPadSide().equalsIgnoreCase("right")) {
                        valueString += paddedStr;
                    }
                }
                // Too much padding truncating first part of the valueString
            } else {
                int desiredStartPos = - neededPadSize;
                valueString = valueString.substring(desiredStartPos,valueString.length());
            }
        }
        if(valueString == null){
            valueString = "";
        }
        return valueString;
    }

    
    private static String  processHeader(ExportFormat format) {
        if (!StringUtils.isEmpty(format.getHeader())) {
            return format.getHeader() + System.getProperty("line.separator");
        }
        return "";
    }

    private static String  processFooter(ExportFormat format) {
        if (!StringUtils.isEmpty(format.getFooter())) {
            return  System.getProperty("line.separator") + format.getFooter();
        }
        return "";
    }
}
