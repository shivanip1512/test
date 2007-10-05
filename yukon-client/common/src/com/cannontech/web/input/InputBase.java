package com.cannontech.web.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.cannontech.web.input.type.InputType;
import com.cannontech.web.input.validate.InputValidator;

/**
 * Base implementation of InputSource
 */
public class InputBase implements InputSource {

    private String displayName = null;
    private String field = null;
    private String description = null;
    private InputType type = null;
    private InputSecurity security = new SimpleInputSecurity();
    private List<InputValidator> validatorList = new ArrayList<InputValidator>();

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
        this.description = description;
    }

    public InputType getType() {
        return type;
    }

    public void setType(InputType type) {
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

    public List<InputValidator> getValidatorList() {

        List<InputValidator> fullValidatorList = new ArrayList<InputValidator>();
        fullValidatorList.addAll(validatorList);
        fullValidatorList.add(type.getValidator());

        return fullValidatorList;
    }

    public void setValidatorList(List<InputValidator> validatorList) {
        this.validatorList = validatorList;
    }

    public List<InputSource> getInputList() {
        return Collections.singletonList((InputSource) this);
    }

    public Map<String, ? extends InputSource> getInputMap(String prefix) {
        return Collections.singletonMap(prefix, this);
    }

}
