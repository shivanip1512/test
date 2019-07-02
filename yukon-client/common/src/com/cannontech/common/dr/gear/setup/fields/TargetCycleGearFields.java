package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.database.data.device.lm.TargetCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "noRamp", "controlPercent", "cyclePeriod", "cycleCountSendType", "maxCycleCount",
    "startingPeriodCount", "sendRate", "stopCommandRepeat", "howToStopControl", "capacityReduction", "kWReduction",
    "whenToChangeFields" })
public class TargetCycleGearFields extends TrueCycleGearFields {

    private Double kWReduction;

    public Double getkWReduction() {
        return kWReduction;
    }

    public void setkWReduction(Double kWReduction) {
        this.kWReduction = kWReduction;
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear lmProgramDirectGear) {
        super.buildDBPersistent(lmProgramDirectGear);
        TargetCycleGear targetCycleGear = (TargetCycleGear) lmProgramDirectGear;
        if (getkWReduction() != null) {
            targetCycleGear.setKWReduction(getkWReduction());
        } else {
            targetCycleGear.setKWReduction(0.0);
        }
    }

    @Override
    public void buildModel(LMProgramDirectGear lmProgramDirectGear) {
        super.buildModel(lmProgramDirectGear);
        TargetCycleGear targetCycleGear = (TargetCycleGear) lmProgramDirectGear;
        if (targetCycleGear.getKWReduction() != null) {
            setkWReduction(targetCycleGear.getKWReduction());
        } else {
            setkWReduction(0.0);
        }
    }

}
