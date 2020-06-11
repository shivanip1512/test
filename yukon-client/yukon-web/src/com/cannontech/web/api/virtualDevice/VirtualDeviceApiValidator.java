package com.cannontech.web.api.virtualDevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.virtualDevice.VirtualDeviceBase;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.stars.util.ServletUtils;

@Service
public class VirtualDeviceApiValidator<T extends VirtualDeviceBase<?>> extends SimpleValidator<T> {

    @Autowired private YukonValidationHelper yukonValidationHelper;

    @SuppressWarnings("unchecked")
    public VirtualDeviceApiValidator() {
        super((Class<T>) VirtualDeviceBase.class);
    }

    public VirtualDeviceApiValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T virtualDevice, Errors errors) {
        String virtualDeviceId = ServletUtils.getPathVariable("id");
        if (virtualDevice.getName() != null) {
            yukonValidationHelper.validatePaoName(virtualDevice.getName(), virtualDevice.getType(), errors, "Name", virtualDeviceId);
        }
    }
}
