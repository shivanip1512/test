package com.cannontech.web.capcontrol.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class RegulatorValidator extends SimpleValidator<Regulator> {

    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;
    @Autowired private ZoneDao zoneDao;

    private static final String basekey = "yukon.web.modules.capcontrol.regulator.error.";

    public RegulatorValidator() {
        super(Regulator.class);
    }

    @Override
    public void doValidation(Regulator regulator, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        
        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(regulator.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        boolean idSpecified = regulator.getId() != null;

        boolean nameAvailable = paoDao.isNameAvailable(regulator.getName(), regulator.getType());

        if (!nameAvailable) {
            if (!idSpecified) {
                //For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use our own existing name
                LiteYukonPAObject existingPao = dbCache.getAllPaosMap().get(regulator.getId());
                if (!existingPao.getPaoName().equals(regulator.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }

        LightDeviceConfiguration config = new LightDeviceConfiguration(regulator.getConfigId(), null, null);
        if (!deviceConfigurationDao.isTypeSupportedByConfiguration(config, regulator.getType())) {
            errors.rejectValue("configId", basekey + "invalidConfig");
        }

        if (idSpecified) {
            try {
                //Throws an exception if regulator is not attached to a zone.
                zoneDao.getZoneByRegulatorId(regulator.getId());

                Integer pointId = regulator.getMappings().get(RegulatorPointMapping.VOLTAGE_Y);
                if (pointId == null || pointId < 0) {
                    errors.rejectValue("mappings[" + RegulatorPointMapping.VOLTAGE_Y.name() + "]", basekey + "noVoltageY");
                }

                PaoType existingPaoType = dbCache.getAllPaosMap().get(regulator.getId()).getPaoType();

                if (existingPaoType != regulator.getType()) {
                    errors.rejectValue("type", basekey + "invalidType");
                }

            } catch (OrphanedRegulatorException e) {}
        }
    }

}
