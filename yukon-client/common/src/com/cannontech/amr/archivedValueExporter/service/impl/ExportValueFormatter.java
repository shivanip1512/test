package com.cannontech.amr.archivedValueExporter.service.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.PadSide;

public class ExportValueFormatter {
    
    // This method takes care of any modifying that needs to take place
       // on the valueString to output the wanted representation 
       public static String padValue(ExportField field, String valueString) {
           // This if checks to see if we need more padding or 
           // to truncate from the front of the value
           if(field.getMaxLength() > 0) {
               int neededPadSize = field.getMaxLength() - valueString.length();
               if (neededPadSize > 0) {
                   if (field.getPadChar() != null && !field.getPadChar().equalsIgnoreCase("")){
                       // This generates the padding string that will be added to
                       // the beginning/end of the string.
                       String paddedStr = "";
                       for (int i = 0; i < neededPadSize; i++){
                           paddedStr += field.getPadChar();
                       }
                       // Put the padding on the left or right
                       if (field.getPadSide() == PadSide.LEFT) {
                           valueString = paddedStr + valueString;
                       }
                       else if (field.getPadSide() == PadSide.RIGHT) {
                           valueString += paddedStr;
                       }
                   }
                   // Too much padding truncating first part of the valueString
               } else {
                   int desiredStartPos = - neededPadSize;
                   valueString = valueString.substring(desiredStartPos,valueString.length());
               }
           }
           return valueString;
       }

      
    public static String formatValue(double value, ExportField field) {
        DecimalFormat formatter = new DecimalFormat(field.getPattern());
        formatter.setRoundingMode(RoundingMode.valueOf(field.getRoundingMode()));
        String formatedValue = formatter.format(value);
        return formatedValue;
    }

    public static String formatTimestamp(DateTime timeStamp, ExportField field) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(field.getPattern());
        String formatedDate = timeStamp.toString(formatter);
        return formatedDate;
    }
       
       public static String  formatHeader(ExportFormat format) {
           if (!StringUtils.isEmpty(format.getHeader())) {
               return format.getHeader() + System.getProperty("line.separator");
           }
           return "";
       }

       public static String  formatFooter(ExportFormat format) {
           if (!StringUtils.isEmpty(format.getFooter())) {
               return  System.getProperty("line.separator") + format.getFooter();
           }
           return "";
       }
}
