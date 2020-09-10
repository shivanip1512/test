package com.cannontech.web.capcontrol.ivvc.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.AbstractZoneNotThreePhase;
import com.cannontech.capcontrol.model.AbstractZoneThreePhase;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class ZoneDtoValidator extends SimpleValidator<AbstractZone> {

    @Autowired ZoneDao zoneDao;

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
        
        Zone existingZone = zoneDao.findZoneByZoneName(zoneDto.getName());
        if (zoneDto.getZoneId() == null) {
            if (existingZone != null) {
                errors.rejectValue("name", "yukon.web.modules.capcontrol.ivvc.zoneWizard.error.duplicateZoneName");
            }
        } else {
            if (!existingZone.getId().equals(zoneDto.getZoneId())) {
                errors.rejectValue("name", "yukon.web.modules.capcontrol.ivvc.zoneWizard.error.duplicateZoneName");            }
        }
    }

}
