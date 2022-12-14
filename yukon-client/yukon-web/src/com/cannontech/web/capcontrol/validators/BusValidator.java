package com.cannontech.web.capcontrol.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class BusValidator extends SimpleValidator<CapControlSubBus> {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;
    @Autowired private CapControlCache ccCache;

    public BusValidator() {
        super(CapControlSubBus.class);
    }

    @Override
    public void doValidation(CapControlSubBus bus, Errors errors) {

        validateName(bus, errors);
        YukonValidationUtils.checkExceedsMaxLength(errors, "geoAreaName", bus.getGeoAreaName(), 60);
        YukonValidationUtils.checkExceedsMaxLength(errors, "capControlSubstationBus.mapLocationID", bus.getCapControlSubstationBus().getMapLocationID(), 64);
        if (bus.getCapControlSubstationBus().isDualBusEnabledBoolean()
            && (bus.getCapControlSubstationBus().getAltSubPAOId() == null
            || bus.getCapControlSubstationBus().getAltSubPAOId() == 0)) {
            errors.rejectValue("capControlSubstationBus.altSubPAOId", "yukon.web.modules.capcontrol.bus.noAltBusError");
        }
        //Do not allow a bus to be enabled when in verification
        if (!bus.isDisabled()) {
            Integer busId = bus.getId();
            if (busId != null) {
                SubBus ccBus = ccCache.getSubBus(busId);
                if (ccBus.getVerificationFlag()) {
                    bus.setDisabled(true);
                    errors.rejectValue("disabled", "yukon.web.modules.capcontrol.bus.verificationStatusDisabled");
                }
            }
        }
    }

    private void validateName(CapControlSubBus bus, Errors errors) {

        Integer id = bus.getId();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");

        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(bus.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", bus.getName(), 60);
        if (!paoDao.isNameAvailable(bus.getName(), PaoType.CAP_CONTROL_SUBBUS)) {

            if (id == null) {
                //For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use the name it already has
                String existingName = dbCache.getAllPaosMap().get(id).getPaoName();
                if (!existingName.equals(bus.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
