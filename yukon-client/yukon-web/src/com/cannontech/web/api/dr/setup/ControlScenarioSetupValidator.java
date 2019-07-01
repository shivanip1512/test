package com.cannontech.web.api.dr.setup;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.ControlScenarioBase;
import com.cannontech.common.dr.setup.ControlScenarioProgram;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;

public class ControlScenarioSetupValidator extends SimpleValidator<ControlScenarioBase> {
    private final static String key = "yukon.web.modules.dr.setup.controlScenario.error.";
    @Autowired private PaoDao paoDao;

    public ControlScenarioSetupValidator() {
        super(ControlScenarioBase.class);
    }

    @Override
    protected void doValidation(ControlScenarioBase controlScenarioBase, Errors errors) {
        // Control Scenario name
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "required",
            new Object[] { "Control Scenario Name" });
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", controlScenarioBase.getName(), 60);
        }
        if (!errors.hasFieldErrors("name")) {
            String id = ServletUtils.getPathVariable("id");
            Integer paoId = null;
            if (id != null) {
                paoId = Integer.valueOf(id);
            }
            // Check if a control scenario with this name already exists
            if (paoId == null || !(StringUtils.equals(paoDao.getYukonPAOName(paoId), controlScenarioBase.getName()))) {
                isPaoNameUnique(controlScenarioBase, paoId, errors);
            }
        }

        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(controlScenarioBase.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        if (controlScenarioBase.getAllPrograms() != null && controlScenarioBase.getAllPrograms().size() > 0) {
            for (int i = 0; i < controlScenarioBase.getAllPrograms().size(); i++) {
                ControlScenarioProgram program = controlScenarioBase.getAllPrograms().get(i);
                if (!errors.hasFieldErrors("startOffset")) {
                    YukonValidationUtils.checkRange(errors, "allPrograms[" + i + "].startOffset",
                        program.getStartOffset(), 0, 1439, true);
                }
                if (!errors.hasFieldErrors("stopOffset")) {
                    YukonValidationUtils.checkRange(errors, "allPrograms[" + i + "].stopOffset",
                        program.getStopOffset(), 0, 1439, true);
                }
                if (program.getGears() == null || program.getGears().size() < 1) {
                    errors.rejectValue("allPrograms[" + i + "].gears", "yukon.web.error.isBlank", "Cannot be blank.");
                } else if (program.getGears().size() > 1 || program.getGears().get(0).getId() == null) {
                    errors.rejectValue("allPrograms[" + i + "].gears", key + "oneGear",
                        "Should require only one valid gear.");
                }
            }
        }
    }

    private void isPaoNameUnique(ControlScenarioBase controlScenarioBase, Integer paoId, Errors errors) {
        if (paoId == null || !(StringUtils.equals(paoDao.getYukonPAOName(paoId), controlScenarioBase.getName()))) {
            LiteYukonPAObject unique = paoDao.findUnique(controlScenarioBase.getName(), PaoType.LM_SCENARIO);
            if (unique != null) {
                errors.rejectValue("name", key + "unique");
            }
        }
    }

}
