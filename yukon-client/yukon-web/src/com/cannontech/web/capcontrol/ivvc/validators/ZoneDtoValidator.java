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
        
        YukonValidationUtils.rejectIfEmpty(errors,"name","yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.zoneName");
        
        if (target.getRegulatorId() == -1) {
            errors.rejectValue("regulatorId", "yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.regulatorId");
        }
        
        if (target.getSubstationBusId() == -1) {
            errors.rejectValue("substationBusId", "yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.substationBusId");
        }
        
    }

}
