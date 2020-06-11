package com.cannontech.web.api.virtualDevice;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.virtualDevice.VirtualDeviceBase;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class VirtualDeviceCreateApiValidator <T extends VirtualDeviceBase<?>> extends SimpleValidator<T> {

    @SuppressWarnings("unchecked")
    public VirtualDeviceCreateApiValidator() {
        super((Class<T>) VirtualDeviceBase.class);
    }

    public VirtualDeviceCreateApiValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T virtualDevice, Errors errors) {
        YukonValidationUtils.checkIfFieldRequired("name", errors, virtualDevice.getName(), "Name");
    }
}
