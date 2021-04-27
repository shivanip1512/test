package com.cannontech.dr.ecobee.message;

import java.util.List;

public class CriteriaSelector {
    private String selector;
    private List<String> values;

    public CriteriaSelector() {
    }

    public CriteriaSelector(String selector, List<String> values) {
        this.selector = selector;
        this.values = values;
    }

    public String getSelector() {
        return selector;
    }

    public List<String> getValues() {
        return values;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

}
