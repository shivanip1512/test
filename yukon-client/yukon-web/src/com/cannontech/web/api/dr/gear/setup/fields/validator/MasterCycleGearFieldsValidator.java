package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.fields.MasterCycleGearFields;
import com.cannontech.database.db.device.lm.GearControlMethod;

public class MasterCycleGearFieldsValidator extends ProgramGearFieldsValidator<MasterCycleGearFields> {
    @Autowired private GearApiValidatorHelper gearApiValidatorHelper;

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
        gearApiValidatorHelper.checkControlPercent(masterCycleGear.getControlPercent(), errors);

        // Check Cycle Period
        gearApiValidatorHelper.checkCyclePeriod(masterCycleGear.getCyclePeriodInMinutes(), getControlMethod(), errors);

        // Check Group Selection Method
        gearApiValidatorHelper.checkGroupSelectionMethod(masterCycleGear.getGroupSelectionMethod(), errors);

        // Check Ramp In
        if (masterCycleGear.getRampInPercent() != null || masterCycleGear.getRampInIntervalInSeconds() != null) {
            gearApiValidatorHelper.checkRampInPercentAndInterval(masterCycleGear.getRampInPercent(),
                masterCycleGear.getRampInIntervalInSeconds(), errors);
        }

        // Check How to Stop Control
        gearApiValidatorHelper.checkStopControlAndOrder(masterCycleGear.getHowToStopControl(),
            masterCycleGear.getStopOrder(), errors);

        // Check for Ramp Out and Ramp Out Interval
        if (!errors.hasFieldErrors("howToStopControl")
            && (masterCycleGear.getHowToStopControl() == HowToStopControl.RampOutRestore
                || masterCycleGear.getHowToStopControl() == HowToStopControl.RampOutTimeIn)) {
            gearApiValidatorHelper.checkRampOutPercentAndInterval(masterCycleGear.getRampOutPercent(),
                masterCycleGear.getRampOutIntervalInSeconds(), errors);
        }

        // Check Group Capacity Reduction
        gearApiValidatorHelper.checkGroupCapacityReduction(masterCycleGear.getCapacityReduction(), errors);

        // Check When To Change
        gearApiValidatorHelper.checkWhenToChange(masterCycleGear.getWhenToChangeFields(), errors);
    }

}