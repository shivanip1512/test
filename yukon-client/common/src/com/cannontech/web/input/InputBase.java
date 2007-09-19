package com.cannontech.web.input;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;

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

    public void doRegisterEditor(ServletRequestDataBinder binder) {
        binder.registerCustomEditor(getType().getTypeClass(), field, getType().getPropertyEditor());
    }

    @SuppressWarnings("unchecked")
    public void doValidate(String path, Object command, BindException errors) {

        // Get typed field value
        BeanWrapper beanWrapper = new BeanWrapperImpl(command);

        if (!StringUtils.isEmpty(path)) {
            path += "." + field;
        } else {
            path = field;
        }
        Object value = beanWrapper.getPropertyValue(path);

        // Validate value
        InputValidator validator = type.getValidator();
        validator.validate(path, this, value, errors);

    }

    public List<InputSource> getInputList() {
        return Collections.singletonList((InputSource) this);
    }
    
    public Map<String, ? extends InputSource> getInputMap(String prefix) {
        return Collections.singletonMap(prefix + getField(), this);
    }

}
