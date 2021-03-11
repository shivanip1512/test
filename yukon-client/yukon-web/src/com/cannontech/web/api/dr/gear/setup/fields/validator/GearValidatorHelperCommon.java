package com.cannontech.web.api.dr.gear.setup.fields.validator;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.database.db.device.lm.GearControlMethod;

/**
 * Helper class for LM Gear validation
 */
public class GearValidatorHelperCommon {

    /**
     * Check for How To Stop Control
     */
    public boolean checkValidHowToStopControl(HowToStopControl howToStopControl, GearControlMethod gearType) {
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
     * Check for Command Resend Rate
     */
    public boolean validCommandResendRate(Integer sendRate) {
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
