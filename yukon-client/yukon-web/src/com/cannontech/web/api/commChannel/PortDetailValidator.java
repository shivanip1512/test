package com.cannontech.web.api.commChannel;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortDetailBase;
import com.cannontech.common.validator.SimpleValidator;

@Service
public class PortDetailValidator <T extends PortDetailBase> extends SimpleValidator<T>{

    public PortDetailValidator() {
        super((Class<T>) PortDetailBase.class);
    }
    
    public PortDetailValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T port, Errors errors) {
        // TODO : Add validation for edit/update here
    }
}
