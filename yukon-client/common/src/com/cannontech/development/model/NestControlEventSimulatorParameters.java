package com.cannontech.development.model;

import org.joda.time.DateTime;

import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.dr.nest.model.LoadShapingPeak;
import com.cannontech.dr.nest.model.LoadShapingPost;
import com.cannontech.dr.nest.model.LoadShapingPreparation;

public class NestControlEventSimulatorParameters {
    private GearControlMethod controlMethod;
    private DateTime startTime;
    private DateTime stopTime;
    private String groupName;
    private LoadShapingPreparation loadShapingPreparation;
    private LoadShapingPeak loadShapingPeak;
    private LoadShapingPost loadShapingPost;

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

    public LoadShapingPreparation getLoadShapingPreparation() {
        return loadShapingPreparation;
    }

    public void setLoadShapingPreparation(LoadShapingPreparation loadShapingPreparation) {
        this.loadShapingPreparation = loadShapingPreparation;
    }

    public LoadShapingPeak getLoadShapingPeak() {
        return loadShapingPeak;
    }

    public void setLoadShapingPeak(LoadShapingPeak loadShapingPeak) {
        this.loadShapingPeak = loadShapingPeak;
    }

    public LoadShapingPost getLoadShapingPost() {
        return loadShapingPost;
    }

    public void setLoadShapingPost(LoadShapingPost loadShapingPost) {
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
