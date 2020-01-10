package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.device.range.DlcAddressRangeService;

public class MeterConfigValidator extends SimpleValidator<YukonMeter> {
    
    private @Autowired DlcAddressRangeService dlcAddressRangeService;
    
    public MeterConfigValidator() {
        super(YukonMeter.class);
    }
    
    @Override
    public void doValidation(YukonMeter meter, Errors errors) {
        
        /* Meter Number */
        if (StringUtils.isBlank(meter.getMeterNumber())) {
            errors.rejectValue("meterNumber", "yukon.web.modules.operator.meterConfig.error.required");
        } else {
            YukonValidationUtils.checkExceedsMaxLength(errors, "meterNumber", meter.getMeterNumber(), 50);
        }
        
        /* Physical Address */
        PaoType deviceType = meter.getPaoType();
        if (deviceType.isPlc()) {
            PlcMeter plcMeter = (PlcMeter) meter;
            if (!StringUtils.isNumeric(plcMeter.getAddress())) {
                errors.rejectValue("address", "yukon.web.modules.operator.meterConfig.error.nonNumeric");
            } else {
                try {
                    int physicalAddress = Integer.parseInt(plcMeter.getAddress());
                    if (!dlcAddressRangeService.isValidEnforcedAddress(deviceType, physicalAddress)) {
                        failAddress(plcMeter, errors);
                    }
                } catch (NumberFormatException e) {
                    failAddress(plcMeter, errors);
                }
            }
        }

    }
    
    private void failAddress(YukonMeter meter, Errors errors) {
        String paoTypeString = meter.getPaoType().getPaoTypeName();
        
        String rangeString = dlcAddressRangeService.rangeStringEnforced(meter.getPaoType());
        
        errors.rejectValue("address", "yukon.web.modules.operator.meterConfig.error.invalidRange", 
                new Object[]{ paoTypeString, rangeString }, null);
    }
    
}