package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareDto;

public class HardwareDtoValidator extends SimpleValidator<HardwareDto> {
    
    private static final char[] validSerialNumberChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    
    public HardwareDtoValidator() {
    	super(HardwareDto.class);
    }

    @Override
    public void doValidation(HardwareDto hardwareDto, Errors errors) {
        
        /* Serial Number */
        if(!hardwareDto.isMct()){  /* Check serial numbers for switches and tstats */
            if (StringUtils.isBlank(hardwareDto.getSerialNumber())) {
                errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.required");
            } else if(hardwareDto.getSerialNumber().length() > 30) {
                errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.tooLong");
            } else if (!StringUtils.containsOnly(hardwareDto.getSerialNumber(), validSerialNumberChars)){
                errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardwareEdit.error.invalid");
            }
        }
        
        /* Device Label */
        if (StringUtils.isNotBlank(hardwareDto.getDisplayLabel())) {
            if(hardwareDto.getDisplayLabel().length() > 60){
                errors.rejectValue("displayLabel", "yukon.web.modules.operator.hardwareEdit.error.tooLong");
            }
        }
        
        /* Alternate Tracking Number */
        if (StringUtils.isNotBlank(hardwareDto.getAltTrackingNumber())) {
            if(hardwareDto.getAltTrackingNumber().length() > 40){
                errors.rejectValue("altTrackingNumber", "yukon.web.modules.operator.hardwareEdit.error.tooLong");
            }
        }
        
        /* Device Info Notes */
        if (StringUtils.isNotBlank(hardwareDto.getDeviceNotes())) {
            if(hardwareDto.getDeviceNotes().length() > 500){
                errors.rejectValue("deviceNotes", "yukon.web.modules.operator.hardwareEdit.error.tooLong");
            }
        }
        
        /* Install Notes */
        if (StringUtils.isNotBlank(hardwareDto.getInstallNotes())) {
            if(hardwareDto.getInstallNotes().length() > 500){
                errors.rejectValue("installNotes", "yukon.web.modules.operator.hardwareEdit.error.tooLong");
            }
        }
        
        /* Two Way Device: only applicable for two way lcr's */
        if(hardwareDto.isTwoWayLcr() && !(hardwareDto.getDeviceId() > 0)){
            errors.rejectValue("deviceId", "yukon.web.modules.operator.hardwareEdit.error.invalid");
        }
    }

}