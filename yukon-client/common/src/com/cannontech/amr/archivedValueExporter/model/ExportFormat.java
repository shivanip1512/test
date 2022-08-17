package com.cannontech.amr.archivedValueExporter.model;

import java.util.List;

import com.cannontech.common.util.LazyList;
import com.cannontech.common.util.TimeZoneFormat;

public class ExportFormat {
    
    private int formatId;
    private ArchivedValuesExportFormatType formatType;
    private String formatName;
    private String delimiter = DataExportDelimiter.COMMA.getValue();
    private String header;
    private String footer;
    private TimeZoneFormat tzFormat = TimeZoneFormat.LOCAL;
    private boolean excludeAbnormal = false;
    
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
        this.footer = footer == null ? footer : footer.trim();
    }

    public String getHeader() {
        return header;
    }
    
    public void setHeader(String header) {
        this.header = header == null ? header : header.trim();
    }

    public String getFormatName() {
        return formatName;
    }
    
    public void setFormatName(String formatName) {
        this.formatName = formatName.trim();
    }

    public String getDelimiter() {
        return delimiter;
    }
    
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter == null ? delimiter : delimiter.trim();
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
    
    public boolean isExcludeAbnormal() {
        return excludeAbnormal;
    }
    
    public void setExcludeAbnormal(boolean excludeAbnormal) {
        this.excludeAbnormal = excludeAbnormal;
    }
    
    public DataExportDelimiter getDelimiterType() {
        return DataExportDelimiter.getForValue(delimiter);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result
                + ((delimiter == null) ? 0 : delimiter.hashCode());
        result = prime * result + (excludeAbnormal ? 1231 : 1237);
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
        result = prime * result + ((footer == null) ? 0 : footer.hashCode());
        result = prime * result + formatId;
        result = prime * result
                + ((formatName == null) ? 0 : formatName.hashCode());
        result = prime * result
                + ((formatType == null) ? 0 : formatType.hashCode());
        result = prime * result + ((header == null) ? 0 : header.hashCode());
        result = prime * result
                + ((tzFormat == null) ? 0 : tzFormat.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExportFormat other = (ExportFormat) obj;
        if (attributes == null) {
            if (other.attributes != null)
                return false;
        } else if (!attributes.equals(other.attributes))
            return false;
        if (delimiter == null) {
            if (other.delimiter != null)
                return false;
        } else if (!delimiter.equals(other.delimiter))
            return false;
        if (excludeAbnormal != other.excludeAbnormal)
            return false;
        if (fields == null) {
            if (other.fields != null)
                return false;
        } else if (!fields.equals(other.fields))
            return false;
        if (footer == null) {
            if (other.footer != null)
                return false;
        } else if (!footer.equals(other.footer))
            return false;
        if (formatId != other.formatId)
            return false;
        if (formatName == null) {
            if (other.formatName != null)
                return false;
        } else if (!formatName.equals(other.formatName))
            return false;
        if (formatType != other.formatType)
            return false;
        if (header == null) {
            if (other.header != null)
                return false;
        } else if (!header.equals(other.header))
            return false;
        if (tzFormat != other.tzFormat)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
                .format("ExportFormat [formatId=%s, formatType=%s, formatName=%s, delimiter=%s, header=%s, footer=%s, tzFormat=%s, excludeAbnormal=%s, attributes=%s, fields=%s]",
                        formatId, formatType, formatName, delimiter, header,
                        footer, tzFormat, excludeAbnormal, attributes, fields);
    }
    
}