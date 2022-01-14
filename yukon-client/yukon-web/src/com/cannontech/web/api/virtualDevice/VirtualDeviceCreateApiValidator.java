package com.cannontech.web.api.virtualDevice;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualMeterModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;

@Service
public class VirtualDeviceCreateApiValidator <T extends VirtualDeviceBaseModel<?>> extends SimpleValidator<T> {

    public VirtualDeviceCreateApiValidator() {
        super((Class<T>) VirtualDeviceBaseModel.class);
    }

    @Override
    protected void doValidation(VirtualDeviceBaseModel virtualDevice, Errors errors) {
        YukonApiValidationUtils.checkIfFieldRequired("deviceName", errors, virtualDevice.getDeviceName(), "Name");
        if (virtualDevice instanceof VirtualMeterModel)
        {
            VirtualMeterModel model = (VirtualMeterModel) virtualDevice;
            YukonApiValidationUtils.checkIfFieldRequired("meterNumber", errors, model.getMeterNumber(), "Meter Number");
        }
    }
}
