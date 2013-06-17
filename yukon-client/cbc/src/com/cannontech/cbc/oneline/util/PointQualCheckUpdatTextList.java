package com.cannontech.cbc.oneline.util;

import com.cannontech.cbc.oneline.model.UpdatableStats;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.message.capcontrol.streamable.PointQualityCheckable;

public class PointQualCheckUpdatTextList extends UpdatableTextList implements
        PointQualityCheckable, ExtraUpdatableTextElement {

    private PointQualityCheckable pointCheckable;

    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public PointQualityCheckable getPointCheckable() {
        return pointCheckable;
    }

    public void setPointCheckable(PointQualityCheckable pointCheckable) {
        this.pointCheckable = pointCheckable;
    }

    public PointQualCheckUpdatTextList(int roleID, UpdatableStats stats) {
        super(roleID, stats);
    }

    public Integer getCurrentPtQuality(int pointType) {
        if (pointCheckable != null)
            return pointCheckable.getCurrentPtQuality(pointType);
        return null;
    }

    public Integer getCurrentPtQuality() {
        if (pointCheckable != null && type != null)
            return pointCheckable.getCurrentPtQuality(getType());
        return null;
    }

    public boolean conditionToAddIsTrue() { //add the (*) to the display if the quality is NOT normal
        return !CapControlUtils.signalQualityNormal(getPointCheckable(), getType());
    }


}
