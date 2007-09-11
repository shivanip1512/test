package com.cannontech.web.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;

public class InputGroup implements Input {

    private String field = null;
    private String renderer = null;
    private Map<String, Input> inputMap = new HashMap<String, Input>();

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

    public void doRegisterEditor(ServletRequestDataBinder binder) {
        for (InputSource input : getInputList()) {
            input.doRegisterEditor(binder);
        }
    }

    public void doValidate(String path, Object command, BindException errors) {

        if (!StringUtils.isEmpty(field)) {
            path += field;
        }

        for (Map.Entry<String, Input> entry : inputMap.entrySet()) {

            String currentPath = path;
            String field = entry.getKey();
            Input input = entry.getValue();

            if(input instanceof InputGroup){
                if (!StringUtils.isEmpty(currentPath)) {
                    currentPath += ".";
                }
                currentPath += field;
            }

            input.doValidate(currentPath, command, errors);

        }
    }

    public void setField(String field){
        this.field = field;
    }
    
    public String getField() {
        return this.field;
    }
}
