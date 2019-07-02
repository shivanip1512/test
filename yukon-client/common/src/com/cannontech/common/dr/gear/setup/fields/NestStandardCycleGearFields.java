package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.database.data.device.lm.NestStandardCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;

public class NestStandardCycleGearFields implements ProgramGearFields {

    private PrepLoadShape prep;
    private PeakLoadShape peak;
    private PostLoadShape post;

    public PrepLoadShape getPrep() {
        return prep;
    }

    public void setPrep(PrepLoadShape prep) {
        this.prep = prep;
    }

    public PeakLoadShape getPeak() {
        return peak;
    }

    public void setPeak(PeakLoadShape peak) {
        this.peak = peak;
    }

    public PostLoadShape getPost() {
        return post;
    }

    public void setPost(PostLoadShape post) {
        this.post = post;
    }

    @Override
    public void buildModel(LMProgramDirectGear programDirectGear) {
        NestStandardCycleGear nestStandardCycleGear = (NestStandardCycleGear) programDirectGear;
        setPeak(nestStandardCycleGear.getPeakLoadShape());
        setPost(nestStandardCycleGear.getPostLoadShape());
        setPrep(nestStandardCycleGear.getPrepLoadShape());
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        NestStandardCycleGear nestStandardCycle = (NestStandardCycleGear) programDirectGear;
        PrepLoadShape prep = getPrep();
        PeakLoadShape peak = getPeak();
        PostLoadShape post = getPost();
        nestStandardCycle.setLoadShapingOptions(prep, peak, post);
    }

}
