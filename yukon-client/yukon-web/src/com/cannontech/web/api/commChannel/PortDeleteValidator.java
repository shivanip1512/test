package com.cannontech.web.api.commChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortDelete;
import com.cannontech.common.validator.SimpleValidator;

public class PortDeleteValidator extends SimpleValidator<PortDelete> {

    @Autowired private PortValidatorHelper portValidatorHelper;

    public PortDeleteValidator() {
        super(PortDelete.class);
    }

    @Override
    protected void doValidation(PortDelete portDelete, Errors errors) {
        // Name
        portValidatorHelper.checkIfFieldRequired("name", errors, portDelete.getName(), "Name");
    }
}
