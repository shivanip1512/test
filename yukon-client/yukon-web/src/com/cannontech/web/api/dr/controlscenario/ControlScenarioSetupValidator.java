package com.cannontech.web.api.dr.controlscenario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.ControlScenario;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class ControlScenarioSetupValidator extends SimpleValidator<ControlScenario> {
    private final static String key = "yukon.web.modules.dr.setup.controlScenario.error.";
    @Autowired private LMValidatorHelper lmValidatorHelper;

    public ControlScenarioSetupValidator() {
        super(ControlScenario.class);
    }

    @Override
    protected void doValidation(ControlScenario scenario, Errors errors) {

        lmValidatorHelper.validateNewPaoName(scenario.getName(), PaoType.LM_SCENARIO, errors, "Scenario Name");

        if (scenario.getAllPrograms() != null && scenario.getAllPrograms().size() > 0) {
            for (int i = 0; i < scenario.getAllPrograms().size(); i++) {
                ProgramDetails program = scenario.getAllPrograms().get(i);

                YukonValidationUtils.checkRange(errors, "allPrograms[" + i + "].startOffsetInMinutes", program.getStartOffsetInMinutes(), 0, 1439, true);
                YukonValidationUtils.checkRange(errors, "allPrograms[" + i + "].stopOffsetInMinutes", program.getStopOffsetInMinutes(), 0, 1439, true);

                if (program.getGears() == null || program.getGears().size() < 1) {
                    errors.rejectValue("allPrograms[" + i + "].gears", "yukon.web.error.isBlank", "Cannot be blank.");
                } else if (program.getGears().size() > 1 || program.getGears().get(0).getId() == null) {
                    errors.rejectValue("allPrograms[" + i + "].gears", key + "oneGear", "Should require only one valid gear.");
                }
            }
        }
    }

}
