package com.cannontech.web.api.virtualDevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.stars.util.ServletUtils;

@Service
public class VirtualDeviceApiValidator <T extends VirtualDeviceBaseModel> extends SimpleValidator<T> {

    @Autowired private YukonValidationHelper yukonValidationHelper;

    public VirtualDeviceApiValidator() {
        super((Class<T>) VirtualDeviceBaseModel.class);
    }

    @Override
    protected void doValidation(VirtualDeviceBaseModel virtualDevice, Errors errors) {
        String virtualDeviceId = ServletUtils.getPathVariable("id");
        if (virtualDevice.getName() != null) {
            yukonValidationHelper.validatePaoName(virtualDevice.getName(), virtualDevice.getType(), errors, "Name", virtualDeviceId);
        }
    }
}
