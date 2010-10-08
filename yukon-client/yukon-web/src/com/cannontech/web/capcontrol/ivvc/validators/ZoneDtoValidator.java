package com.cannontech.web.capcontrol.ivvc.validators;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.capcontrol.ivvc.models.ZoneDto;

public class ZoneDtoValidator extends SimpleValidator<ZoneDto>{

    public ZoneDtoValidator() {
        super(ZoneDto.class);
    }

    @Override
    protected void doValidation(ZoneDto target, Errors errors) {
        
        YukonValidationUtils.rejectIfEmpty(errors,"name","yukon.web.modules.capcontrol.ivvc.zoneEditor.error.required");
        
        if (target.getRegulatorId() == -1) {
            errors.rejectValue("regulator", "yukon.web.modules.capcontrol.ivvc.zoneEditor.error.required");
        }
        
        if (target.getSubstationBusId() == -1) {
            errors.rejectValue("substationBus", "yukon.web.modules.capcontrol.ivvc.zoneEditor.error.required");
        }
        
    }

}
