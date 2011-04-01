package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.device.range.v2.DeviceAddressRangeService;
import com.cannontech.device.range.v2.LongRange;

public class MeterConfigValidator extends SimpleValidator<Meter> {
    
    private DeviceAddressRangeService deviceAddressRangeService;
    
    public MeterConfigValidator() {
        super(Meter.class);
    }

    @Override
    public void doValidation(Meter meter, Errors errors) {
        
        /* Meter Number */
        if (StringUtils.isBlank(meter.getMeterNumber())) {
            errors.rejectValue("meterNumber", "yukon.web.modules.operator.meterConfig.error.required");
        } else {
            YukonValidationUtils.checkExceedsMaxLength(errors, "meterNumber", meter.getMeterNumber(), 50);
        }
        
        /* Physical Address */
        if(!StringUtils.isNumeric(meter.getAddress())) {
            errors.rejectValue("address", "yukon.web.modules.operator.meterConfig.error.nonNumeric");
        } else {
            PaoType deviceType = meter.getPaoType();
            try {
                Long physicalAddress = Long.parseLong(meter.getAddress()); 
                if(!deviceAddressRangeService.isValidAddress(deviceType, physicalAddress)) {
                    failAddress(meter, errors);
                }
            } catch (NumberFormatException e) {
                failAddress(meter, errors);
            }
        }
        
    }
    
    private void failAddress(Meter meter, Errors errors) {
        String paoTypeString = meter.getPaoType().getPaoTypeName();
        LongRange range = deviceAddressRangeService.getAddressRangeForDevice(meter.getPaoType());

        errors.rejectValue("address", "yukon.web.modules.operator.meterConfig.error.invalidRange", new Object[]{paoTypeString, range}, null);
    }

    @Autowired
    public void setDeviceAddressRangeService(DeviceAddressRangeService deviceAddressRangeService) {
        this.deviceAddressRangeService = deviceAddressRangeService;
    }
    
    
}