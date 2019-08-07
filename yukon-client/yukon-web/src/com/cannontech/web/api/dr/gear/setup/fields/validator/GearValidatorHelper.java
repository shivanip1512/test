package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.WhenToChange;
import com.cannontech.common.dr.gear.setup.fields.WhenToChangeFields;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

/**
 * Helper class for LM Gear validation
 */
public class GearValidatorHelper {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    private final static String invalidKey = "yukon.web.modules.dr.setup.error.invalid";

    /**
     * Check for How To Stop Control
     */
    public void checkHowToStopControl(HowToStopControl howToStopControl, GearControlMethod gearType, Errors errors) {
        if (gearType == GearControlMethod.TimeRefresh || gearType == GearControlMethod.MasterCycle) {
            if (howToStopControl == HowToStopControl.StopCycle) {
                errors.rejectValue("howToStopControl", invalidKey, new Object[] { "How To Stop Control" }, "");
            }
        } else if (gearType == GearControlMethod.SmartCycle || gearType == GearControlMethod.TrueCycle
            || gearType == GearControlMethod.MagnitudeCycle || gearType == GearControlMethod.TargetCycle) {
            if (howToStopControl != HowToStopControl.StopCycle && howToStopControl != HowToStopControl.Restore) {
                errors.rejectValue("howToStopControl", invalidKey, new Object[] { "How To Stop Control" }, "");
            }
        } else {
            if (howToStopControl != HowToStopControl.TimeIn && howToStopControl != HowToStopControl.Restore) {
                errors.rejectValue("howToStopControl", invalidKey, new Object[] { "How To Stop Control" }, "");
            }
        }
    }

    /**
     * Check for Ramp Out Percent and Ramp Our Interval
     */
    public void checkRampOutPercentAndInterval(Integer rampOutPercent, Integer rampOutIntervalInSeconds,
            Errors errors) {
        lmValidatorHelper.checkIfFieldRequired("rampOutPercent", errors, rampOutPercent, "Ramp Out Percent");
        if (!errors.hasFieldErrors("rampOutPercent")) {
            YukonValidationUtils.checkRange(errors, "rampOutPercent", rampOutPercent, 0, 100, false);
        }
        lmValidatorHelper.checkIfFieldRequired("rampOutIntervalInSeconds", errors, rampOutIntervalInSeconds,
            "Ramp Out Interval");
        if (!errors.hasFieldErrors("rampOutIntervalInSeconds")) {
            YukonValidationUtils.checkRange(errors, "rampOutIntervalInSeconds", rampOutIntervalInSeconds, -99999, 99999,
                false);
        }
    }

    /**
     * Check for Group Capacity Reduction
     */
    public void checkGroupCapacityReduction(Integer groupCapacityReduction, Errors errors) {
        lmValidatorHelper.checkIfFieldRequired("capacityReduction", errors, groupCapacityReduction,
            "Group Capacity Reduction");
        if (!errors.hasFieldErrors("capacityReduction")) {
            YukonValidationUtils.checkRange(errors, "capacityReduction", groupCapacityReduction, 0, 100, false);
        }

    }

    /**
     * Check for When to Change
     */
    public void checkWhenToChange(WhenToChangeFields whenToChange, Errors errors) {
        // Check for When to Change Fields
        lmValidatorHelper.checkIfFieldRequired("whenToChangeFields", errors, whenToChange, "When To Change Fields");

        if (!errors.hasFieldErrors("whenToChangeFields")) {
            errors.pushNestedPath("whenToChangeFields");

            // Check for When to Change
            lmValidatorHelper.checkIfFieldRequired("whenToChange", errors, whenToChange.getWhenToChange(),
                "When To Change");
            if (!errors.hasFieldErrors("whenToChange")) {
                // Check for Fields for Trigger
                if (whenToChange.getWhenToChange() == WhenToChange.TriggerOffset) {

                    // Check for Trigger Number
                    lmValidatorHelper.checkIfFieldRequired("triggerNumber", errors, whenToChange.getTriggerNumber(),
                        "Trigger Number");
                    if (!errors.hasFieldErrors("triggerNumber")) {
                        YukonValidationUtils.checkRange(errors, "triggerNumber", whenToChange.getTriggerNumber(), 1,
                            99999, false);
                    }

                    // Check for Trigger Offset
                    lmValidatorHelper.checkIfFieldRequired("triggerOffset", errors, whenToChange.getTriggerOffset(),
                        "Trigger Offset");
                    if (!errors.hasFieldErrors("triggerOffset")) {
                        YukonValidationUtils.checkRange(errors, "triggerOffset", whenToChange.getTriggerOffset(),
                            -99999.9999, 99999.9999, false);
                    }

                }

                // Check for Fields for Priority
                if (whenToChange.getWhenToChange() == WhenToChange.Priority) {

                    // Check for Change Priority
                    lmValidatorHelper.checkIfFieldRequired("changePriority", errors, whenToChange.getChangePriority(),
                        "Change Priority");
                    if (!errors.hasFieldErrors("changePriority")) {
                        YukonValidationUtils.checkRange(errors, "changePriority", whenToChange.getChangePriority(), 0,
                            9999, false);
                    }
                }

                // Check for Fields for Duration
                if (whenToChange.getWhenToChange() == WhenToChange.Duration) {

                    // Check for Change Duration
                    lmValidatorHelper.checkIfFieldRequired("changeDurationInMinutes", errors,
                        whenToChange.getChangeDurationInMinutes(), "Change Duration In Minutes");
                    if (!errors.hasFieldErrors("changeDurationInMinutes")) {
                        YukonValidationUtils.checkRange(errors, "changeDurationInMinutes",
                            whenToChange.getChangeDurationInMinutes(), 0, 99999, false);
                    }
                }

            }
            errors.popNestedPath();
        }

    }

    /**
     * Check for Control Percent
     */
    public void checkControlPercent(Integer controlPercent, Errors errors) {
        lmValidatorHelper.checkIfFieldRequired("controlPercent", errors, controlPercent, "Control Percent");
        if (!errors.hasFieldErrors("controlPercent")) {
            YukonValidationUtils.checkRange(errors, "controlPercent", controlPercent, 5, 100, false);
        }
    }

    /**
     * Check for Check for Cycle Period
     */
    public void checkCyclePeriod(Integer cyclePeriodInMinutes, GearControlMethod gearType, Errors errors) {
        lmValidatorHelper.checkIfFieldRequired("cyclePeriodInMinutes", errors, cyclePeriodInMinutes, "Cycle Period");
        if (!errors.hasFieldErrors("cyclePeriodInMinutes")) {
            if (gearType != GearControlMethod.MasterCycle) {
                YukonValidationUtils.checkRange(errors, "cyclePeriodInMinutes", cyclePeriodInMinutes, 1, 945, false);
            } else {
                YukonValidationUtils.checkRange(errors, "cyclePeriodInMinutes", cyclePeriodInMinutes, 5, 945, false);
            }

        }
    }

    /**
     * Check for Command Resend Rate
     */
    public void checkCommandResendRate(Integer sendRate, Errors errors) {
        TimeIntervals commandResendRate = TimeIntervals.fromSeconds(sendRate);
        if (!TimeIntervals.getCommandResendRate().contains(commandResendRate)) {
            errors.rejectValue("sendRate", invalidKey, new Object[] { "Command Resend Rate" }, "");
        }
    }

    /**
     * Check for Stop Command Repeat
     */
    public void checkStopCommandRepeat(Integer stopCommandRepeat, GearControlMethod gearType, Errors errors) {
        if (gearType == GearControlMethod.SmartCycle || gearType == GearControlMethod.TrueCycle
            || gearType == GearControlMethod.TimeRefresh) {
            lmValidatorHelper.checkIfFieldRequired("stopCommandRepeat", errors, stopCommandRepeat,
                "Stop Command Repeat");
            if (!errors.hasFieldErrors("stopCommandRepeat")) {
                YukonValidationUtils.checkRange(errors, "stopCommandRepeat", stopCommandRepeat, 0, 5, false);
            }
        }
    }

    /**
     * Check for Ramp In
     */
    public void checkRampIn(Boolean rampIn, Errors errors) {
        lmValidatorHelper.checkIfFieldRequired("rampIn", errors, rampIn, "Ramp In");
    }

    /**
     * Check for Ramp In Percent And Interval
     */
    public void checkRampInPercentAndInterval(Integer rampInPercent, Integer rampInIntervalInSeconds, Errors errors) {
        // Check for Ramp In Percent
        lmValidatorHelper.checkIfFieldRequired("rampInPercent", errors, rampInPercent, "Ramp In Percent");
        if (!errors.hasFieldErrors("rampInPercent")) {
            YukonValidationUtils.checkRange(errors, "rampInPercent", rampInPercent, 0, 100, false);
        }

        // Check for Ramp In Interval
        lmValidatorHelper.checkIfFieldRequired("rampInIntervalInSeconds", errors, rampInIntervalInSeconds,
            "Ramp In Interval");
        if (!errors.hasFieldErrors("rampInIntervalInSeconds")) {
            YukonValidationUtils.checkRange(errors, "rampInIntervalInSeconds", rampInIntervalInSeconds, -99999, 99999,
                false);
        }

    }

    /**
     * Check for Ramp Out
     */
    public void checkRampOut(Boolean rampOut, Errors errors) {
        lmValidatorHelper.checkIfFieldRequired("rampOut", errors, rampOut, "Ramp Out");
    }

    /**
     * Check for Criticality
     */
    public void checkCriticality(Integer criticality, Errors errors) {
        lmValidatorHelper.checkIfFieldRequired("criticality", errors, criticality, "Criticality");
        if (!errors.hasFieldErrors("criticality")) {
            YukonValidationUtils.checkRange(errors, "criticality", criticality, 1, 15, false);
        }
    }

    /**
     * Check for No. of Groups
     */
    public void checkNumberOfGroups(Integer numberOfGroups, Errors errors) {
        lmValidatorHelper.checkIfFieldRequired("numberOfGroups", errors, numberOfGroups, "Number Of Groups");
        if (!errors.hasFieldErrors("numberOfGroups")) {
            YukonValidationUtils.checkRange(errors, "numberOfGroups", numberOfGroups, 0, 25, false);
        }
    }
}
