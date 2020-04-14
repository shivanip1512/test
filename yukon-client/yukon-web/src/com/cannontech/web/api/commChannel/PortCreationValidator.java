package com.cannontech.web.api.commChannel;

import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.validator.SimpleValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
public class PortCreationValidator <T extends PortBase<?>> extends SimpleValidator<T> {

    @Autowired PortValidatorHelper portValidatorHelper;

    @SuppressWarnings("unchecked")
    public PortCreationValidator() {
        super((Class<T>) PortBase.class);
    }

    public PortCreationValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T port, Errors errors) {
        // Check if type is NULL
        portValidatorHelper.checkIfFieldRequired("type", errors, port.getType(), "Type");
        // Check if name is NULL
        portValidatorHelper.checkIfFieldRequired("name", errors, port.getName(), "Name");
        // Check if baudRate is NULL
        portValidatorHelper.checkIfFieldRequired("baudRate", errors, port.getBaudRate(), "Baud Rate");
    }
}
