package com.cannontech.web.api.dr.controlscenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.ControlScenario;
import com.cannontech.common.dr.setup.ProgramDetails;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.web.api.dr.setup.LMApiValidatorHelper;

public class ControlScenarioSetupValidator extends SimpleValidator<ControlScenario> {
    private final static String key = "yukon.web.modules.dr.setup.controlScenario.error.";
    @Autowired LMApiValidatorHelper lmApiValidatorHelper;
    public ControlScenarioSetupValidator() {
        super(ControlScenario.class);
    }

    @Override
    protected void doValidation(ControlScenario scenario, Errors errors) {

        lmApiValidatorHelper.validateNewPaoName(scenario.getName(), PaoType.LM_SCENARIO, errors, "Name");

        if (CollectionUtils.isNotEmpty(scenario.getAllPrograms())) {
            for (int i = 0; i < scenario.getAllPrograms().size(); i++) {
                ProgramDetails program = scenario.getAllPrograms().get(i);
                errors.pushNestedPath("allPrograms[" + i + "]");
                if (program.getProgramId() == null) {
                    lmApiValidatorHelper.checkIfFieldRequired("programId", errors, program.getProgramId(), "Program Id");
                }
                YukonApiValidationUtils.checkRange(errors, "startOffsetInMinutes", program.getStartOffsetInMinutes(), 0, 1439, true);
                YukonApiValidationUtils.checkRange(errors, "stopOffsetInMinutes", program.getStopOffsetInMinutes(), 0, 1439, true);

                if (CollectionUtils.isEmpty(program.getGears())) {
                    errors.reject(ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "Start Gear" }, "");
                } else if (program.getGears().size() > 1) {
                    errors.reject(ApiErrorDetails.ONE_GEAR.getCodeString(), new Object[] { 1, "Gear"}, "");
                    //errors.reject(key + "oneGear");
                } else if (program.getGears().get(0) == null) {
                    lmApiValidatorHelper.checkIfFieldRequired("gears", errors, program.getGears().get(0), "Gear");
                } else if(program.getGears().get(0).getGearNumber() == null) {
                    lmApiValidatorHelper.checkIfFieldRequired("gears[0].gearNumber", errors, program.getGears().get(0).getGearNumber(), "Gear Number");
                }
                errors.popNestedPath();
            }
        }
        if (CollectionUtils.isNotEmpty(scenario.getAllPrograms())) {
            List<Integer> programIds = new ArrayList<>();
            scenario.getAllPrograms().forEach(p -> programIds.add(p.getProgramId()));
            Set<Integer> duplicateProgramsIds = lmApiValidatorHelper.findDuplicates(programIds);
            if (CollectionUtils.isNotEmpty(duplicateProgramsIds)) {
                errors.reject(ApiErrorDetails.DUPLICATE_VALUE.getCodeString(), new Object[] { "Program Id", "Program Id", duplicateProgramsIds }, "");
            }
        }
    }

}
