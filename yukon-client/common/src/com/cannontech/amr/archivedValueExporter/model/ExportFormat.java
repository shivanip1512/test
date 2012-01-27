package com.cannontech.amr.archivedValueExporter.model;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.util.LazyList;

public class ExportFormat {
    private int formatId;
    private String formatName;
    private String delimiter;
    private String header;
    private String footer;
    
    private List<ExportAttribute> attributes =  LazyList.ofInstance(ExportAttribute.class);
    private List<ExportField> fields =  LazyList.ofInstance(ExportField.class);
    
    public int getFormatId() {
        return formatId;
    }
    public void setFormatId(int formatId) {
        this.formatId = formatId;
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
    public List<ExportField> getFields() {
        return fields;
    }
    public void setFields(List<ExportField> fields) {
        this.fields = fields;
    }
    public void addAttribute(ExportAttribute attribute) {
        attributes.add(attribute);
    }
    
    /**
     * Formats header for display in generated report
     * 
     * @return 
     */
  /*  public String formatHeader() {
        if (!StringUtils.isEmpty(header)) {
            return header + System.getProperty("line.separator");
        }
        return "";
    }*/

    /**
     * Formats footer for display in generated report
     * 
     * @return
     */
   /* public String formatFooter() {
        if (!StringUtils.isEmpty(footer)) {
            return  System.getProperty("line.separator") + footer;
        }
        return "";
    }*/
}
