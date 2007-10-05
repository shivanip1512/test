package com.cannontech.web.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.web.input.validate.InputValidator;

public class InputGroup implements Input {

    private String field = null;
    private String displayName = null;
    private String renderer = null;
    private Map<String, Input> inputMap = new HashMap<String, Input>();
    private InputSecurity security = new SimpleInputSecurity();
    private List<InputValidator> validatorList = new ArrayList<InputValidator>();

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<InputValidator> getValidatorList() {
        return validatorList;
    }

    public void setValidatorList(List<InputValidator> validatorList) {
        this.validatorList = validatorList;
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
