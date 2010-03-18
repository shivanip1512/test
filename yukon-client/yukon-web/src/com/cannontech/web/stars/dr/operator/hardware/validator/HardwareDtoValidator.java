package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;

public class HardwareDtoValidator implements Validator {
    
    private static final char[] validSerialNumberChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz) {
        return HardwareDto.class.isAssignableFrom(clazz); 
    }

    @Override
    public void validate(Object target, Errors errors) {
        
        HardwareDto hardwareDto = (HardwareDto)target;
        
        /* Serial Number */
        if(!hardwareDto.getIsMct()){  /* Check serial numbers for switches and tstats */
            if (StringUtils.isBlank(hardwareDto.getSerialNumber())) {
                errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.serialNumberRequired");
            } else if(hardwareDto.getSerialNumber().length() > 30) {
                errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.serialNumberTooLong");
            } else if (!StringUtils.containsOnly(hardwareDto.getSerialNumber(), validSerialNumberChars)){
                errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.serialNumberInvalidChars");
            }
        }
        
        /* Device Label */
        if (StringUtils.isNotBlank(hardwareDto.getDeviceLabel())) {
            if(hardwareDto.getDeviceLabel().length() > 60){
                errors.rejectValue("deviceLabel", "yukon.web.modules.operator.hardwareEdit.error.deviceLabelTooLong");
            }
        }
        
        /* Alternate Tracking Number */
        if (StringUtils.isNotBlank(hardwareDto.getAltTrackingNumber())) {
            if(hardwareDto.getAltTrackingNumber().length() > 40){
                errors.rejectValue("altTrackingNumber", "yukon.web.modules.operator.hardwareEdit.error.altTrackingNumberTooLong");
            }
        }
        
        /* Device Info Notes */
        if (StringUtils.isNotBlank(hardwareDto.getDeviceNotes())) {
            if(hardwareDto.getDeviceNotes().length() > 500){
                errors.rejectValue("deviceNotes", "yukon.web.modules.operator.hardwareEdit.error.deviceNotesTooLong");
            }
        }
        
        /* Install Notes */
        if (StringUtils.isNotBlank(hardwareDto.getInstallNotes())) {
            if(hardwareDto.getInstallNotes().length() > 500){
                errors.rejectValue("installNotes", "yukon.web.modules.operator.hardwareEdit.error.installNotesTooLong");
            }
        }
    }

}