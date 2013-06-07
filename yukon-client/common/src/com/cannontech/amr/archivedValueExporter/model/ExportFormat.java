package com.cannontech.amr.archivedValueExporter.model;

import java.util.List;

import com.cannontech.common.util.LazyList;

public class ExportFormat {
    private int formatId;
    private ArchivedValuesExportFormatType formatType;
    private String formatName;
    private String delimiter;
    private String header;
    private String footer;
    private TimeZoneFormat tzFormat = TimeZoneFormat.LOCAL;
    
    private List<ExportAttribute> attributes =  LazyList.ofInstance(ExportAttribute.class);
    private List<ExportField> fields =  LazyList.ofInstance(ExportField.class);
    
    public int getFormatId() {
        return formatId;
    }
    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }

    public ArchivedValuesExportFormatType getFormatType() {
        return formatType;
    }
    public void setFormatType(ArchivedValuesExportFormatType formatType) {
        this.formatType = formatType;
    }

    public String getFooter() {
        return footer;
    }
    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }

    public String getFormatName() {
        return formatName;
    }
    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public String getDelimiter() {
        return delimiter;
    }
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public List<ExportAttribute> getAttributes() {
        return attributes;
    }
    public void setAttributes(List<ExportAttribute> attributes) {
        this.attributes = attributes;
    }
    public void addAttribute(ExportAttribute attribute) {
        attributes.add(attribute);
    }

    public List<ExportField> getFields() {
        return fields;
    }
    public void setFields(List<ExportField> fields) {
        this.fields = fields;
    }

    public void setDateTimeZoneFormat(TimeZoneFormat tzFormat) {
        this.tzFormat = tzFormat;
    }
    public TimeZoneFormat getDateTimeZoneFormat() {
        return tzFormat;
    }

}