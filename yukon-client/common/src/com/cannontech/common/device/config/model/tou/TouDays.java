package com.cannontech.common.device.config.model.tou;

import java.util.LinkedHashMap;
import java.util.Map;

public class TouDays {

    private Map<String, String> values = new LinkedHashMap<String, String>();

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
