package com.cannontech.dr.ecobee.message;

import java.util.List;

public class CriteriaSelector {
    String selector;
    List<String> values;

    public CriteriaSelector(String selector, List<String> values) {
        super();
        this.selector = selector;
        this.values = values;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

}
