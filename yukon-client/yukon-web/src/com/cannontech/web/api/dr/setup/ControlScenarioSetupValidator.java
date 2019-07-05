package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.ControlScenarioBase;
import com.cannontech.common.dr.setup.ControlScenarioProgram;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class ControlScenarioSetupValidator extends SimpleValidator<ControlScenarioBase> {
    private final static String key = "yukon.web.modules.dr.setup.controlScenario.error.";
    @Autowired private LMValidatorHelper lmValidatorHelper;

    public ControlScenarioSetupValidator() {
        super(ControlScenarioBase.class);
    }

    @Override
    protected void doValidation(ControlScenarioBase scenario, Errors errors) {

        lmValidatorHelper.validateNewPaoName(scenario.getName(), PaoType.LM_SCENARIO, errors, "Scenario Name");

        if (scenario.getAllPrograms() != null && scenario.getAllPrograms().size() > 0) {
            for (int i = 0; i < scenario.getAllPrograms().size(); i++) {
                ControlScenarioProgram program = scenario.getAllPrograms().get(i);
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

}
