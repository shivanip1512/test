package com.cannontech.web.api.virtualDevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.stars.util.ServletUtils;

@Service
public class VirtualDeviceApiValidator extends SimpleValidator<VirtualDeviceModel> {

    @Autowired private YukonValidationHelper yukonValidationHelper;

    public VirtualDeviceApiValidator() {
        super(VirtualDeviceModel.class);
    }

    @Override
    protected void doValidation(VirtualDeviceModel virtualDevice, Errors errors) {
        String virtualDeviceId = ServletUtils.getPathVariable("id");
        if (virtualDevice.getName() != null) {
            yukonValidationHelper.validatePaoName(virtualDevice.getName(), virtualDevice.getType(), errors, "Name", virtualDeviceId);
        }
    }
}
