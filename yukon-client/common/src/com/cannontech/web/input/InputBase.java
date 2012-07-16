package com.cannontech.web.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.validate.DefaultValidator;
import com.cannontech.web.input.validate.InputValidator;

/**
 * Base implementation of InputSource
 */
public class InputBase<T> implements InputSource<T> {

    private String displayName = null;
    private String field = null;
    private String description = null;
    private InputType<T> type = null;
    private InputSecurity security = new SimpleInputSecurity();
    private List<InputValidator<T>> validatorList = new ArrayList<InputValidator<T>>();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public InputType<T> getType() {
        return type;
    }

    public void setType(InputType<T> type) {
        this.type = type;
    }

    public String getRenderer() {
        return this.type.getRenderer();
    }

    public InputSecurity getSecurity() {
        return security;
    }

    public void setSecurity(InputSecurity security) {
        this.security = security;
    }

    public List<InputValidator<T>> getValidatorList() {

        List<InputValidator<T>> fullValidatorList = new ArrayList<InputValidator<T>>();
        fullValidatorList.addAll(validatorList);
        
        if(type.getValidator() != DefaultValidator.getInstance()) {
            fullValidatorList.add(type.getValidator());
        }

        return fullValidatorList;
    }

    public void setValidatorList(List<InputValidator<T>> validatorList) {
        this.validatorList = validatorList;
    }

    public List<InputSource<T>> getInputList() {
        return Collections.singletonList((InputSource<T>) this);
    }

    public Map<String, InputSource<T>> getInputMap(String prefix) {
        
        Map<String, InputSource<T>> returnMap = new HashMap<String, InputSource<T>>();
        returnMap.put(prefix, this);
        
        return returnMap;
    }

}
