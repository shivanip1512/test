package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.HoneywellSetpointGearFields;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class HoneywellSetpointGearFieldsValidator extends ProgramGearFieldsValidator<HoneywellSetpointGearFields> {

    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

    public HoneywellSetpointGearFieldsValidator() {
        super(HoneywellSetpointGearFields.class);
    }

    public HoneywellSetpointGearFieldsValidator(Class<HoneywellSetpointGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.HoneywellSetpoint;
    }

    @Override
    protected void doValidation(HoneywellSetpointGearFields honeywellSetpointGear, Errors errors) {
        // Check Mandatory
        yukonApiValidationUtils.checkIfFieldRequired("mandatory", errors, honeywellSetpointGear.getMandatory(), "Mandatory");

        // Check Setpoint
        gearApiValidatorHelper.checkSetpointOffset(honeywellSetpointGear.getSetpointOffset(), errors);

        // Check for How to Stop Control
        gearApiValidatorHelper.checkHowToStopControl(honeywellSetpointGear.getHowToStopControl(), getControlMethod(), errors);

        // Check for Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(honeywellSetpointGear.getCapacityReduction(), errors);

        // Check for When To Change
        gearApiValidatorHelper.checkWhenToChange(honeywellSetpointGear.getWhenToChangeFields(), errors);
    }

}
