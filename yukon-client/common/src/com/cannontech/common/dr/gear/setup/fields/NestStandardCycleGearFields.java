package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.database.data.device.lm.NestStandardCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;

public class NestStandardCycleGearFields implements ProgramGearFields {

    private PrepLoadShape preparationLoadShaping;
    private PeakLoadShape peakLoadShaping;
    private PostLoadShape postPeakLoadShaping;

    public PrepLoadShape getPreparationLoadShaping() {
        return preparationLoadShaping;
    }

    public void setPreparationLoadShaping(PrepLoadShape preparationLoadShaping) {
        this.preparationLoadShaping = preparationLoadShaping;
    }

    public PeakLoadShape getPeakLoadShaping() {
        return peakLoadShaping;
    }

    public void setPeakLoadShaping(PeakLoadShape peakLoadShaping) {
        this.peakLoadShaping = peakLoadShaping;
    }

    public PostLoadShape getPostPeakLoadShaping() {
        return postPeakLoadShaping;
    }

    public void setPostPeakLoadShaping(PostLoadShape postPeakLoadShaping) {
        this.postPeakLoadShaping = postPeakLoadShaping;
    }

    @Override
    public void buildModel(LMProgramDirectGear programDirectGear) {
        NestStandardCycleGear nestStandardCycleGear = (NestStandardCycleGear) programDirectGear;
        setPeakLoadShaping(nestStandardCycleGear.getPeakLoadShape());
        setPostPeakLoadShaping(nestStandardCycleGear.getPostLoadShape());
        setPreparationLoadShaping(nestStandardCycleGear.getPrepLoadShape());
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        NestStandardCycleGear nestStandardCycle = (NestStandardCycleGear) programDirectGear;
        PrepLoadShape prep = getPreparationLoadShaping();
        PeakLoadShape peak = getPeakLoadShaping();
        PostLoadShape post = getPostPeakLoadShaping();
        nestStandardCycle.setLoadShapingOptions(prep, peak, post);
    }

}
