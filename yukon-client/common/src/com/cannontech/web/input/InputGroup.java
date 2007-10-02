package com.cannontech.web.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputGroup implements Input {

    private String field = null;
    private String renderer = null;
    private Map<String, Input> inputMap = new HashMap<String, Input>();
    private InputSecurity security = new SimpleInputSecurity();

    public List<InputSource> getInputList() {

        List<InputSource> inputList = new ArrayList<InputSource>();
        for (Input input : inputMap.values()) {
            inputList.addAll(input.getInputList());
        }

        return inputList;
    }

    public Map<String, Input> getInputMap() {
        return this.inputMap;
    }

    public void setInputMap(Map<String, Input> inputMap) {
        this.inputMap = inputMap;
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public InputSecurity getSecurity() {
        return security;
    }

    public void setSecurity(InputSecurity security) {
        this.security = security;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return this.field;
    }

    public Map<String, ? extends InputSource> getInputMap(String prefix) {
        prefix += ".";

        Map<String, InputSource> result = new HashMap<String, InputSource>();
        for (Map.Entry<String, Input> entry : inputMap.entrySet()) {
            Map<String, ? extends InputSource> temp = entry.getValue()
                                                           .getInputMap(prefix + entry.getKey());
            result.putAll(temp);
        }
        return result;
    }
}
