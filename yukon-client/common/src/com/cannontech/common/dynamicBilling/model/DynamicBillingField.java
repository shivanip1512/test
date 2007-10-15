package com.cannontech.common.dynamicBilling.model;

/**
 * Model class which represents a billing field for a dynamic format
 */
public class DynamicBillingField {

    private int id;
    private int formatId;
    private String name = null;
    private int order;
    private String format = null;
    private int maxLength = 0;

    public String getFormat() {
        return format;
    }

    public void setFormat(String fieldFormat) {
        this.format = fieldFormat;
    }

    public String getName() {
        return name;
    }

    public void setName(String fieldName) {
        this.name = fieldName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int fieldOrder) {
        this.order = fieldOrder;
    }

    public int getFormatId() {
        return formatId;
    }

    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String toString() {
        return name + ", " + order + ", " + format;
    }

}
