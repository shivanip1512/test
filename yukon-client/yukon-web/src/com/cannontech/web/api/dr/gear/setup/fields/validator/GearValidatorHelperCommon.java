package com.cannontech.web.api.dr.gear.setup.fields.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

/**
 * Helper class for LM Gear validation
 */
public class GearValidatorHelperCommon {

    @Autowired private LMValidatorHelper lmValidatorHelper;

    /**
     * Check for How To Stop Control
     */
    public boolean checkValidHowToStopControl(HowToStopControl howToStopControl, GearControlMethod gearType, Errors errors) {
            if (gearType == GearControlMethod.SmartCycle || gearType == GearControlMethod.TrueCycle
                || gearType == GearControlMethod.MagnitudeCycle || gearType == GearControlMethod.TargetCycle) {
                if (howToStopControl != HowToStopControl.StopCycle && howToStopControl != HowToStopControl.Restore) {
                    return false;
                }
            } else if (gearType == GearControlMethod.EcobeeCycle || gearType == GearControlMethod.HoneywellCycle
                || gearType == GearControlMethod.ItronCycle) {
                if (howToStopControl != HowToStopControl.Restore) {
                    return false;
                }
            } else {
                if (howToStopControl != HowToStopControl.TimeIn && howToStopControl != HowToStopControl.Restore) {
                    return false;
                }
            }
            return true;
    }

    /**
     * Check for How To Stop Control and Stop Order
     */
    public boolean checkStopControlAndOrder(HowToStopControl howToStopControl) {
        return (howToStopControl == HowToStopControl.StopCycle) ? true : false;
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
    public boolean validCommandResendRate(Integer sendRate, Errors errors) {
        TimeIntervals commandResendRate = TimeIntervals.fromSeconds(sendRate);
        return (!TimeIntervals.getCommandResendRate().contains(commandResendRate)) ? false : true;
    }

    /**
     * Check for supported gears for Stop Command Repeat
     */
    public boolean supportedGear(GearControlMethod gearType) {
        return (gearType == GearControlMethod.SmartCycle || gearType == GearControlMethod.TrueCycle
                || gearType == GearControlMethod.TimeRefresh) ? true : false;
    }
}
