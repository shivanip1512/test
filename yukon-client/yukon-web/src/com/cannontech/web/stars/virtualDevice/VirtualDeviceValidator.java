package com.cannontech.web.stars.virtualDevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualMeterModel;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.common.validator.YukonValidationUtils;

public class VirtualDeviceValidator extends SimpleValidator<VirtualDeviceBaseModel> {
    
    @Autowired private YukonValidationHelper yukonValidationHelper;
    private MessageSourceAccessor accessor;

    public VirtualDeviceValidator() {
        super(VirtualDeviceBaseModel.class);
    }

    @Override
    protected void doValidation(VirtualDeviceBaseModel virtualDevice, Errors errors) {
        String paoId = null;
        if (virtualDevice.getDeviceId() != null) {
            paoId = Integer.toString(virtualDevice.getDeviceId());
        }
        String nameLabel = accessor.getMessage("yukon.common.name");
        yukonValidationHelper.validatePaoName(virtualDevice.getDeviceName(), virtualDevice.getDeviceType(), errors, nameLabel, paoId);
        if (virtualDevice instanceof VirtualMeterModel) {
            VirtualMeterModel meter = (VirtualMeterModel) virtualDevice;
            String meterNumberLabel = accessor.getMessage("yukon.common.meterNumber");
            YukonValidationUtils.checkIsBlank(errors, "meterNumber", meter.getMeterNumber(), meterNumberLabel, false);
            YukonValidationUtils.checkExceedsMaxLength(errors, "meterNumber", meter.getMeterNumber(), 50);
        }
    }
    
    public void setMessageAccessor(MessageSourceAccessor accessor) {
        this.accessor = accessor;
    }
    

}
