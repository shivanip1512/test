package com.cannontech.web.amr.archivedValuesExporter;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;

public class ExportValueFormatter {
    
    // This method takes care of any modifying that needs to take place
       // on the valueString to output the wanted representation 
       public static String formatValue(ExportField field, String valueString) {
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
           return valueString;
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
