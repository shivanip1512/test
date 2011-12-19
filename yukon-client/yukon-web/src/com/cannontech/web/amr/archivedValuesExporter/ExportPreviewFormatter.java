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
    public static String meterNumber = "Meter123456";
    public static String deviceName = "Meter";
    public static String route = "Route";
    public static String dlcAddress = "DLC Address";
    public static String rfManufacturer = "Manufacturer";
    public static String rfModel = "Model";
    public static String rfSerialNumber = "SerialNumber1234";
    public static Double testValue = new Double("123456789");
    
    // This method is for demo purposes, I will replace it with final version after I generate a report.
    public static String format(ExportFormat format) {
        StringBuilder preview = new StringBuilder();
        if (format != null) {
            String value = "";
            preview.append(ExportValueFormatter.formatHeader(format));
            for (int i = 0; i < format.getFields().size();i++) {
                ExportField field =  format.getFields().get(i);
                if (field.getFieldType().equals(FieldType.METER_NUMBER)) {
                    value = ExportValueFormatter.formatValue(field, meterNumber);
                }
                else if (field.getFieldType().equals(FieldType.DEVICE_NAME)) {
                    value = ExportValueFormatter.formatValue(field, deviceName);
                }
                else if (field.getFieldType().equals(FieldType.ROUTE)) {
                    value = ExportValueFormatter.formatValue(field, route);
                }
                else if (field.getFieldType().equals(FieldType.PLAIN_TEXT)) {
                    value = ExportValueFormatter.formatValue(field, field.getPattern());
                }
                else if (field.getFieldType().equals(FieldType.DLC_ADDRESS)) {
                    value = ExportValueFormatter.formatValue(field, dlcAddress );
                }
                else if (field.getFieldType().equals(FieldType.RF_MANUFACTURER)) {
                    value = ExportValueFormatter.formatValue(field, rfManufacturer);
                }
                else if (field.getFieldType().equals(FieldType.RF_MODEL)) {
                    value = ExportValueFormatter.formatValue(field, rfModel);
                }
                else if (field.getFieldType().equals(FieldType.RF_SERIAL_NUMBER)) {
                    value = ExportValueFormatter.formatValue(field, rfSerialNumber );
                }
                else if (field.getFieldType().equals(FieldType.PLAIN_TEXT)) {
                    value =ExportValueFormatter.formatValue(field, field.getPattern());
                }
                else if (field.getFieldType().equals(FieldType.ATTRIBUTE)) {
                    if (field.getAttributeField().equals(AttributeField.TIMESTAMP)) {
                        SimpleDateFormat formatter = new SimpleDateFormat(field.getPattern());
                        value = formatter.format(new Date());
                        value = ExportValueFormatter.formatValue(field, value);
                    } else if (field.getAttributeField().equals(AttributeField.VALUE)) {
                        DecimalFormat formatter = new DecimalFormat(defaultDecimalFormat);
                        if (!StringUtils.isEmpty(field.getPattern())) {
                            formatter = new DecimalFormat(field.getPattern());
                        }
                        formatter.setRoundingMode(RoundingMode.valueOf(field.getRoundingMode()));
                        value = formatter.format(testValue);
                        value = ExportValueFormatter.formatValue(field, value);
                    }
                } else {
                    value = ExportValueFormatter.formatValue(field, field.getPattern());
                }
                preview.append(value);
                if(i != format.getFields().size() - 1){
                    preview.append(format.getDelimiter());
                }
            }
            preview.append(ExportValueFormatter.formatFooter(format));
        }
        return preview.toString().replaceAll("\r\n", "<BR>");
    }
}
