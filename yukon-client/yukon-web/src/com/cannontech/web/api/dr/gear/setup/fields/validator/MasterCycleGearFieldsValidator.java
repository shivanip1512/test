package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.MasterCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class MasterCycleGearFieldsValidator extends ProgramGearFieldsValidator<MasterCycleGearFields> {
    @Autowired private GearValidatorHelper gearValidatorHelper;

    public MasterCycleGearFieldsValidator() {
        super(MasterCycleGearFields.class);
    }

    public MasterCycleGearFieldsValidator(Class<MasterCycleGearFields> objectType) {
        super(objectType);
    }

    @Override
    public GearControlMethod getControlMethod() {
        return GearControlMethod.MasterCycle;
    }

    @Override
    protected void doValidation(MasterCycleGearFields masterCycleGear, Errors errors) {
        // Check Control Percent
        gearValidatorHelper.checkControlPercent(masterCycleGear.getControlPercent(), errors);

        // Check Cycle Period
        gearValidatorHelper.checkCyclePeriod(masterCycleGear.getCyclePeriodInMinutes(), errors);

        // Check Group Selection Method
        gearValidatorHelper.checkGroupSelectionMethod(masterCycleGear.getGroupSelectionMethod(), errors);

        // Check Ramp In
        if (masterCycleGear.getRampInPercent() != null || masterCycleGear.getRampInIntervalInSeconds() != null) {
            gearValidatorHelper.checkRampInPercentAndInterval(masterCycleGear.getRampInPercent(),
                masterCycleGear.getRampInIntervalInSeconds(), errors);
        }

        // Check How to Stop Control
        gearValidatorHelper.checkHowToStopControl(masterCycleGear.getHowToStopControl(), getControlMethod(), errors);

        // Check Group Capacity Reduction
        gearValidatorHelper.checkGroupCapacityReduction(masterCycleGear.getCapacityReduction(), errors);

        // Check When To Change
        gearValidatorHelper.checkWhenToChange(masterCycleGear.getWhenToChangeFields(), errors);
    }

}
