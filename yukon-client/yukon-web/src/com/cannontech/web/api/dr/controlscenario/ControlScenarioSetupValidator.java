package com.cannontech.web.api.dr.controlscenario;

import org.apache.commons.collections4.CollectionUtils;
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
    private final static String requiredKey = "yukon.web.modules.dr.setup.error.required";
    @Autowired private LMValidatorHelper lmValidatorHelper;

    public ControlScenarioSetupValidator() {
        super(ControlScenario.class);
    }

    @Override
    protected void doValidation(ControlScenario scenario, Errors errors) {

        lmValidatorHelper.validateNewPaoName(scenario.getName(), PaoType.LM_SCENARIO, errors, "Scenario Name");

        if (CollectionUtils.isNotEmpty(scenario.getAllPrograms())) {
            for (int i = 0; i < scenario.getAllPrograms().size(); i++) {
                ProgramDetails program = scenario.getAllPrograms().get(i);
                errors.pushNestedPath("allPrograms[" + i + "]");
                if (program.getProgramId() == null) {
                    lmValidatorHelper.checkIfFieldRequired("programId", errors, program.getProgramId(), "Program Id");
                }
                YukonValidationUtils.checkRange(errors, "startOffsetInMinutes", program.getStartOffsetInMinutes(), 0, 1439, true);
                YukonValidationUtils.checkRange(errors, "stopOffsetInMinutes", program.getStopOffsetInMinutes(), 0, 1439, true);

                if (CollectionUtils.isEmpty(program.getGears())) {
                    errors.rejectValue("gears", requiredKey, new Object[] { "Start Gear" }, "");
                } else if (program.getGears().size() > 1) {
                    errors.reject(key + "oneGear");
                } else if (program.getGears().get(0) == null) {
                    lmValidatorHelper.checkIfFieldRequired("gears", errors, program.getGears().get(0), "Gear");
                } else if(program.getGears().get(0).getId() == null) {
                    lmValidatorHelper.checkIfFieldRequired("gears[0].id", errors, program.getGears().get(0).getId(), "Gear");
                }
                errors.popNestedPath();
            }
        }
    }

}
