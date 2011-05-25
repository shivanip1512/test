package com.cannontech.web.capcontrol.ivvc.validators;

import org.springframework.validation.Errors;

import com.cannontech.capcontrol.model.ZoneDto;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.data.pao.ZoneType;

public class ZoneDtoValidator extends SimpleValidator<ZoneDto> {

    public ZoneDtoValidator() {
        super(ZoneDto.class);
    }

    @Override
    protected void doValidation(ZoneDto zoneDto, Errors errors) {
        
        YukonValidationUtils.rejectIfEmpty(errors,"name","yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.zoneName");
        
        if (zoneDto.getZoneType() == ZoneType.GANG_OPERATED) {
            if (zoneDto.getRegulator().getRegulatorId() == 0) {
                errors.reject("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.regulator");
            }
        } else if (zoneDto.getZoneType() == ZoneType.THREE_PHASE) {
            if (zoneDto.getRegulatorA().getRegulatorId() == 0 ||
                    zoneDto.getRegulatorB().getRegulatorId() == 0 ||
                    zoneDto.getRegulatorC().getRegulatorId() == 0) {
                errors.reject("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.regulator");
            }
        } else if (zoneDto.getZoneType() == ZoneType.SINGLE_PHASE) {
            if (zoneDto.getRegulator().getRegulatorId() == 0) {
                errors.reject("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.regulator");
            }
        } else {
            errors.reject("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.regulator");
        }

        if (zoneDto.getSubstationBusId() == -1) {
            errors.rejectValue("substationBusId", "yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.substationBusId");
        }
        
    }

}
