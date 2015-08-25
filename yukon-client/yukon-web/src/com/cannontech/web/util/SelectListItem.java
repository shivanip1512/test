package com.cannontech.web.util;

import java.io.Serializable;

public class SelectListItem implements Serializable {

    private static final long serialVersionUID = -7412729609754233565L;
    private Object value;
    private String label;

    public SelectListItem(Object value, String label) {
        this.value = value;
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
