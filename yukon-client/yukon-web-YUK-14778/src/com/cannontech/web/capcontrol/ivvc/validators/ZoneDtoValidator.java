package com.cannontech.web.capcontrol.ivvc.validators;

import org.springframework.validation.Errors;

import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.AbstractZoneNotThreePhase;
import com.cannontech.capcontrol.model.AbstractZoneThreePhase;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class ZoneDtoValidator extends SimpleValidator<AbstractZone> {

    public ZoneDtoValidator() {
        super(AbstractZone.class);
    }

    @Override
    protected void doValidation(AbstractZone zoneDto, Errors errors) {
        
        YukonValidationUtils.rejectIfEmpty(errors,"name","yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.zoneName");
        
        if (zoneDto instanceof AbstractZoneNotThreePhase) {
            if (((AbstractZoneNotThreePhase)zoneDto).getRegulator().getRegulatorId() == 0) {
                errors.reject("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.regulator");
            }
        } else {
            AbstractZoneThreePhase abstractZoneThreePhase = (AbstractZoneThreePhase)zoneDto;
            for (RegulatorToZoneMapping regulator : abstractZoneThreePhase.getRegulators().values()) {
                if (regulator.getRegulatorId() == 0) {
                    errors.reject("yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.regulator");
                }
            }
        }

        if (zoneDto.getSubstationBusId() == -1) {
            errors.rejectValue("substationBusId", "yukon.web.modules.capcontrol.ivvc.zoneWizard.error.required.substationBusId");
        }
        
    }

}
