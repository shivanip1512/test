package com.cannontech.web.api.virtualDevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualMeterModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationHelper;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.stars.util.ServletUtils;

@Service
public class VirtualDeviceApiValidator <T extends VirtualDeviceBaseModel> extends SimpleValidator<T> {

    @Autowired private YukonApiValidationHelper yukonApiValidationHelper;
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public VirtualDeviceApiValidator() {
        super((Class<T>) VirtualDeviceBaseModel.class);
    }

    @Override
    protected void doValidation(VirtualDeviceBaseModel virtualDevice, Errors errors) {
        String virtualDeviceId = ServletUtils.getPathVariable("id");
        if (virtualDevice.getDeviceName() != null) {
            yukonApiValidationHelper.validatePaoName(virtualDevice.getDeviceName(), virtualDevice.getDeviceType(), errors, "Name", virtualDeviceId);
        }
        if (virtualDevice instanceof VirtualMeterModel)
        {
            VirtualMeterModel meter = (VirtualMeterModel) virtualDevice;
            yukonApiValidationUtils.checkIsBlank(errors, "meterNumber", meter.getMeterNumber(), "Meter Number", true);
            yukonApiValidationUtils.checkExceedsMaxLength(errors, "meterNumber", meter.getMeterNumber(), 50);
        }
    }
}
