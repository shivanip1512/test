package com.cannontech.development.model;

import org.joda.time.DateTime;

import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.nest.model.v3.PeakLoadShape;
import com.cannontech.dr.nest.model.v3.PostLoadShape;
import com.cannontech.dr.nest.model.v3.PrepLoadShape;

public class NestControlEventSimulatorParameters {
    private GearControlMethod controlMethod;
    private DateTime startTime;
    private DateTime stopTime;
    private String groupName;
    private PrepLoadShape loadShapingPreparation;
    private PeakLoadShape loadShapingPeak;
    private PostLoadShape loadShapingPost;

    public GearControlMethod getControlMethod() {
        return controlMethod;
    }

    public void setControlMethod(GearControlMethod controlMethod) {
        this.controlMethod = controlMethod;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public PrepLoadShape getLoadShapingPreparation() {
        return loadShapingPreparation;
    }

    public void setLoadShapingPreparation(PrepLoadShape loadShapingPreparation) {
        this.loadShapingPreparation = loadShapingPreparation;
    }

    public PeakLoadShape getLoadShapingPeak() {
        return loadShapingPeak;
    }

    public void setLoadShapingPeak(PeakLoadShape loadShapingPeak) {
        this.loadShapingPeak = loadShapingPeak;
    }

    public PostLoadShape getLoadShapingPost() {
        return loadShapingPost;
    }

    public void setLoadShapingPost(PostLoadShape loadShapingPost) {
        this.loadShapingPost = loadShapingPost;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(DateTime stopTime) {
        this.stopTime = stopTime;
    }

}
