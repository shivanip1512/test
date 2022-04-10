package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.gear.setup.GroupSelectionMethod;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.StopOrder;
import com.cannontech.common.dr.gear.setup.WhenToChange;
import com.cannontech.common.dr.gear.setup.fields.WhenToChangeFields;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;

/**
 * Api Helper class for LM Gear validation
 */
public class GearApiValidatorHelper {
    
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    /**
     * Check for How To Stop Control
     */
    public void checkHowToStopControl(HowToStopControl howToStopControl, GearControlMethod gearType, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("howToStopControl", errors, howToStopControl, "How To Stop Control");
        if (!errors.hasFieldErrors("howToStopControl")) {

            if (gearType == GearControlMethod.SmartCycle || gearType == GearControlMethod.TrueCycle
                    || gearType == GearControlMethod.MagnitudeCycle || gearType == GearControlMethod.TargetCycle) {
                if (howToStopControl != HowToStopControl.StopCycle && howToStopControl != HowToStopControl.Restore) {
                    errors.rejectValue("howToStopControl", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { "How To Stop Control" }, "");
                }
            } else if (gearType == GearControlMethod.EcobeeCycle || gearType == GearControlMethod.HoneywellCycle
                    || gearType == GearControlMethod.ItronCycle) {
                if (howToStopControl != HowToStopControl.Restore) {
                    errors.rejectValue("howToStopControl", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { "How To Stop Control" }, "");
                }
            } else {
                if (howToStopControl != HowToStopControl.TimeIn && howToStopControl != HowToStopControl.Restore) {
                    errors.rejectValue("howToStopControl", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { "How To Stop Control" }, "");
                }
            }
        }
    }

    /**
     * Check for How To Stop Control and Stop Order
     */
    public void checkStopControlAndOrder(HowToStopControl howToStopControl, StopOrder stopOrder, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("howToStopControl", errors, howToStopControl, "How To Stop Control");
        if (!errors.hasFieldErrors("howToStopControl") && (howToStopControl == HowToStopControl.RampOutRestore
            || howToStopControl == HowToStopControl.RampOutTimeIn)) {
            yukonApiValidationUtils.checkIfFieldRequired("stopOrder", errors, stopOrder, "Stop Order");
            if (!errors.hasFieldErrors("stopOrder")) {
                if (howToStopControl == HowToStopControl.StopCycle) {
                    errors.rejectValue("howToStopControl", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { "How To Stop Control" }, "");
                }
            }
        }
    }

    /**
     * Check for Ramp Out Percent and Ramp Our Interval
     */
    public void checkRampOutPercentAndInterval(Integer rampOutPercent, Integer rampOutIntervalInSeconds,
            Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("rampOutPercent", errors, rampOutPercent, "Ramp Out Percent");
        if (!errors.hasFieldErrors("rampOutPercent")) {
            yukonApiValidationUtils.checkRange(errors, "rampOutPercent", rampOutPercent, 0, 100, false);
        }
        yukonApiValidationUtils.checkIfFieldRequired("rampOutIntervalInSeconds", errors, rampOutIntervalInSeconds,
            "Ramp Out Interval");
        if (!errors.hasFieldErrors("rampOutIntervalInSeconds")) {
            yukonApiValidationUtils.checkRange(errors, "rampOutIntervalInSeconds", rampOutIntervalInSeconds, -99999, 99999,
                false);
        }
    }

    /**
     * Check for Group Capacity Reduction
     */
    public void checkGroupCapacityReduction(Integer groupCapacityReduction, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("capacityReduction", errors, groupCapacityReduction,
                "Group Capacity Reduction");
        if (!errors.hasFieldErrors("capacityReduction")) {
            yukonApiValidationUtils.checkRange(errors, "capacityReduction", groupCapacityReduction, 0, 100, false);
        }
    }

    public void checkSetpointOffset(Integer setpointOffset, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("setpointOffset", errors, setpointOffset, "Setpoint Offset");
        if (!errors.hasFieldErrors("setpointOffset")) {
            yukonApiValidationUtils.checkRange(errors, "setpointOffset", setpointOffset, -10, 10, true);
        }
    }
    
    /**
     * Check for When to Change
     */
    public void checkWhenToChange(WhenToChangeFields whenToChange, Errors errors) {
        // Check for When to Change Fields
        yukonApiValidationUtils.checkIfFieldRequired("whenToChangeFields", errors, whenToChange, "When To Change Fields");

        if (!errors.hasFieldErrors("whenToChangeFields")) {
            errors.pushNestedPath("whenToChangeFields");

            // Check for When to Change
            yukonApiValidationUtils.checkIfFieldRequired("whenToChange", errors, whenToChange.getWhenToChange(),
                "When To Change");
            if (!errors.hasFieldErrors("whenToChange")) {
                // Check for Fields for Trigger
                if (whenToChange.getWhenToChange() == WhenToChange.TriggerOffset) {

                    // Check for Trigger Number
                    yukonApiValidationUtils.checkIfFieldRequired("triggerNumber", errors, whenToChange.getTriggerNumber(),
                        "Trigger Number");
                    if (!errors.hasFieldErrors("triggerNumber")) {
                        yukonApiValidationUtils.checkRange(errors, "triggerNumber", whenToChange.getTriggerNumber(), 1,
                            99999, false);
                    }

                    // Check for Trigger Offset
                    yukonApiValidationUtils.checkIfFieldRequired("triggerOffset", errors, whenToChange.getTriggerOffset(),
                        "Trigger Offset");
                    if (!errors.hasFieldErrors("triggerOffset")) {
                        yukonApiValidationUtils.checkRange(errors, "triggerOffset", whenToChange.getTriggerOffset(),
                            -99999.9999, 99999.9999, false);
                    }

                }

                // Check for Fields for Priority
                if (whenToChange.getWhenToChange() == WhenToChange.Priority) {

                    // Check for Change Priority
                    yukonApiValidationUtils.checkIfFieldRequired("changePriority", errors, whenToChange.getChangePriority(),
                        "Change Priority");
                    if (!errors.hasFieldErrors("changePriority")) {
                        yukonApiValidationUtils.checkRange(errors, "changePriority", whenToChange.getChangePriority(), 0,
                            9999, false);
                    }
                }

                // Check for Fields for Duration
                if (whenToChange.getWhenToChange() == WhenToChange.Duration) {

                    // Check for Change Duration
                    yukonApiValidationUtils.checkIfFieldRequired("changeDurationInMinutes", errors,
                        whenToChange.getChangeDurationInMinutes(), "Change Duration");
                    if (!errors.hasFieldErrors("changeDurationInMinutes")) {
                        yukonApiValidationUtils.checkRange(errors, "changeDurationInMinutes",
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
        yukonApiValidationUtils.checkIfFieldRequired("controlPercent", errors, controlPercent, "Control Percent");
        if (!errors.hasFieldErrors("controlPercent")) {
            yukonApiValidationUtils.checkRange(errors, "controlPercent", controlPercent, 5, 100, false);
        }
    }

    /**
     * Check for Check for Cycle Period
     */
    public void checkCyclePeriod(Integer cyclePeriodInMinutes, GearControlMethod gearType, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("cyclePeriodInMinutes", errors, cyclePeriodInMinutes, "Cycle Period");
        if (!errors.hasFieldErrors("cyclePeriodInMinutes")) {
            if (gearType != GearControlMethod.MasterCycle) {
                yukonApiValidationUtils.checkRange(errors, "cyclePeriodInMinutes", cyclePeriodInMinutes, 1, 945, false);
            } else {
                yukonApiValidationUtils.checkRange(errors, "cyclePeriodInMinutes", cyclePeriodInMinutes, 5, 945, false);
            }

        }
    }

    /**
     * Check for Command Resend Rate
     */
    public void checkCommandResendRate(Integer sendRate, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("sendRate", errors, sendRate, "Command Resend Rate");
        if (!errors.hasFieldErrors("sendRate")) {
            TimeIntervals commandResendRate = TimeIntervals.fromSeconds(sendRate);
            if (!TimeIntervals.getCommandResendRate().contains(commandResendRate)) {
                errors.rejectValue("sendRate", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "Command Resend Rate" }, "");
            }
        }
    }

    /**
     * Check for Stop Command Repeat
     */
    public void checkStopCommandRepeat(Integer stopCommandRepeat, GearControlMethod gearType, Errors errors) {
        if (gearType == GearControlMethod.SmartCycle || gearType == GearControlMethod.TrueCycle
                || gearType == GearControlMethod.TimeRefresh) {
            yukonApiValidationUtils.checkIfFieldRequired("stopCommandRepeat", errors, stopCommandRepeat,
                "Stop Command Repeat");
            if (!errors.hasFieldErrors("stopCommandRepeat")) {
                yukonApiValidationUtils.checkRange(errors, "stopCommandRepeat", stopCommandRepeat, 0, 5, false);
            }
        }
    }

    /**
     * Check for Ramp In
     */
    public void checkRampIn(Boolean rampIn, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("rampIn", errors, rampIn, "Ramp In");
    }

    /**
     * Check for Ramp In Percent And Interval
     */
    public void checkRampInPercentAndInterval(Integer rampInPercent, Integer rampInIntervalInSeconds, Errors errors) {
        // Check for Ramp In Percent
        yukonApiValidationUtils.checkIfFieldRequired("rampInPercent", errors, rampInPercent, "Ramp In Percent");
        if (!errors.hasFieldErrors("rampInPercent")) {
            yukonApiValidationUtils.checkRange(errors, "rampInPercent", rampInPercent, 0, 100, false);
        }

        // Check for Ramp In Interval
        yukonApiValidationUtils.checkIfFieldRequired("rampInIntervalInSeconds", errors, rampInIntervalInSeconds,
            "Ramp In Interval");
        if (!errors.hasFieldErrors("rampInIntervalInSeconds")) {
            yukonApiValidationUtils.checkRange(errors, "rampInIntervalInSeconds", rampInIntervalInSeconds, -99999, 99999,
                false);
        }

    }

    /**
     * Check for Ramp Out
     */
    public void checkRampOut(Boolean rampOut, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("rampOut", errors, rampOut, "Ramp Out");
    }

    /**
     * Check for Criticality
     */
    public void checkCriticality(Integer criticality, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("criticality", errors, criticality, "Criticality");
        if (!errors.hasFieldErrors("criticality")) {
            yukonApiValidationUtils.checkRange(errors, "criticality", criticality, 1, 15, false);
        }
    }

    /**
     * Check for No. of Groups
     */
    public void checkNumberOfGroups(Integer numberOfGroups, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("numberOfGroups", errors, numberOfGroups, "Number Of Groups");
        if (!errors.hasFieldErrors("numberOfGroups")) {
            yukonApiValidationUtils.checkRange(errors, "numberOfGroups", numberOfGroups, 0, 25, false);
        }
    }

    /**
     * Check Group Selection Method
     */
    public void checkGroupSelectionMethod(GroupSelectionMethod groupSelectionMethod, Errors errors) {
        yukonApiValidationUtils.checkIfFieldRequired("groupSelectionMethod", errors, groupSelectionMethod,
            "Group Selection Method");
    }

}
