package com.cannontech.web.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.web.input.validate.InputValidator;

public class InputGroup<T> implements Input<T> {

    private String field = null;
    private String displayName = null;
    private String renderer = null;
    private Map<String, Input<T>> inputMap = new LinkedHashMap<String, Input<T>>();
    private InputSecurity security = new SimpleInputSecurity();
    private List<InputValidator<T>> validatorList = new ArrayList<InputValidator<T>>();

    public List<InputSource<T>> getInputList() {

        List<InputSource<T>> inputList = new ArrayList<InputSource<T>>();
        for (Input<T> input : inputMap.values()) {
            inputList.addAll(input.getInputList());
        }

        return inputList;
    }

    public Map<String, Input<T>> getInputMap() {
        return this.inputMap;
    }

    public void setInputMap(Map<String, Input<T>> inputMap) {
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

    public List<InputValidator<T>> getValidatorList() {
        return validatorList;
    }

    public void setValidatorList(List<InputValidator<T>> validatorList) {
        this.validatorList = validatorList;
    }

    public Map<String, InputSource<T>> getInputMap(String prefix) {
        prefix += ".";

        Map<String, InputSource<T>> result = new HashMap<String, InputSource<T>>();
        for (Map.Entry<String, Input<T>> entry : inputMap.entrySet()) {
            Map<String, InputSource<T>> temp = entry.getValue()
                                                           .getInputMap(prefix + entry.getKey());
            result.putAll(temp);
        }
        return result;
    }

}
