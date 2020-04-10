package com.cannontech.web.api.commChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.validator.SimpleValidator;

public class PortValidator<T extends PortBase<?>> extends SimpleValidator<T> {

    @Autowired PortValidatorHelper portValidatorHelper;

    @SuppressWarnings("unchecked")
    public PortValidator() {
        super((Class<T>) PortBase.class);
    }

    public PortValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T port, Errors errors) {
        // Validate Name if present.
        if (port.getName() != null) {
            portValidatorHelper.validatePaoName(port.getName(), port.getType(), errors, "Name");
        }
    }
}
