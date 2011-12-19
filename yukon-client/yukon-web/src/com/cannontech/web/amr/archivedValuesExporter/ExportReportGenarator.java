package com.cannontech.web.amr.archivedValuesExporter;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.meter.model.Meter;
import com.google.common.collect.Lists;

public class ExportReportGenarator {
    
    private List<String> report = Lists.newArrayList();
    
    public ExportReportGenarator(List<Meter> meters, ExportFormat format){
        generateReport(meters, format);   
    }
    // I am still working on the report generation
    private void generateReport(List<Meter> meters, ExportFormat format) {
        if(StringUtils.isNotEmpty(format.getHeader())){
            report.add(format.getHeader());
        }
        for (Meter meter : meters) {
            String dataRow = getDataRow(format, meter);
            report.add(dataRow);
        }
        if(StringUtils.isNotEmpty(format.getFooter())){
            report.add(format.getFooter());
        }
    }
    
    private String getDataRow(ExportFormat format, Meter meter){
        StringBuilder dataRow = new StringBuilder();
        for (int i = 0; i < format.getFields().size(); i++) {
            ExportField field = format.getFields().get(i);
            String value = getValue(meter,field);
            value = ExportValueFormatter.formatValue(field, value);
            dataRow.append(value);
            if(i != format.getFields().size() - 1){
                dataRow.append(format.getDelimiter());
            }
        }
        return dataRow.toString();
    }
    private String getValue(Meter meter, ExportField field ){
        String value = "";    
        if (field.getFieldType().equals(FieldType.METER_NUMBER)) {
            value = meter.getMeterNumber();
        } else if (field.getFieldType().equals(FieldType.DEVICE_NAME)) {
            value = meter.getName();
        } else if (field.getFieldType().equals(FieldType.DLC_ADDRESS)) {
            value = meter.getAddress();
        } else if (field.getFieldType().equals(FieldType.PLAIN_TEXT)) {
            value = field.getPattern();
        } else if (field.getFieldType().equals(FieldType.ROUTE)) {
            value = meter.getRoute();
        } else if (field.getFieldType().equals(FieldType.RF_MANUFACTURER)) {

        } else if (field.getFieldType().equals(FieldType.RF_MODEL)) {

        } else if (field.getFieldType().equals(FieldType.RF_SERIAL_NUMBER)) {

        }
        return value;
    }
    
    public List<String> getReport() {
        return report;
    }
}
