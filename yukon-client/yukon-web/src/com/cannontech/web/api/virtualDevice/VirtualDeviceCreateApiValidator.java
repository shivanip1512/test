package com.cannontech.web.api.virtualDevice;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class VirtualDeviceCreateApiValidator extends SimpleValidator<VirtualDeviceModel> {

    public VirtualDeviceCreateApiValidator() {
        super(VirtualDeviceModel.class);
    }

    @Override
    protected void doValidation(VirtualDeviceModel virtualDevice, Errors errors) {
        YukonValidationUtils.checkIfFieldRequired("name", errors, virtualDevice.getName(), "Name");
    }
}
