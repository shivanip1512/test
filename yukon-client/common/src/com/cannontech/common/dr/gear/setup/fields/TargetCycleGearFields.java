package com.cannontech.common.dr.gear.setup.fields;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.cannontech.database.data.device.lm.TargetCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "controlPercent", "cyclePeriod", "cycleCountSendType", "maxCycleCount",
    "startingPeriodCount", "sendRate", "noRamp", "stopCommandRepeat", "howToStopControl", "capacityReduction", "kWReduction",
    "whenToChangeFields" })
public class TargetCycleGearFields extends TrueCycleGearFields {

    public TargetCycleGearFields() {
    }
    
    private Double kWReduction;

    public Double getkWReduction() {
        return kWReduction;
    }

    public void setkWReduction(Double kWReduction) {
        if (kWReduction != null) {
            this.kWReduction = new BigDecimal(kWReduction).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
        } else {
            this.kWReduction = kWReduction;
        }
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear lmProgramDirectGear) {
        super.buildDBPersistent(lmProgramDirectGear);
        TargetCycleGear targetCycleGear = (TargetCycleGear) lmProgramDirectGear;
        targetCycleGear.setKWReduction(getkWReduction());
    }

    @Override
    public void buildModel(LMProgramDirectGear lmProgramDirectGear) {
        super.buildModel(lmProgramDirectGear);
        TargetCycleGear targetCycleGear = (TargetCycleGear) lmProgramDirectGear;
        setkWReduction(targetCycleGear.getKWReduction());
    }

}
