package com.cannontech.web.capcontrol.models;

import com.cannontech.message.capcontrol.streamable.StreamableCapObject;

public class ViewableArea {

    private int ccId;
    private String ccName;
    private int stationCount;

    public void setAreaInfo(StreamableCapObject area) {
        ccId = area.getCcId();
        ccName = area.getCcName();
    }

    public final int getCcId() {
        return ccId;
    }

    public final String getCcName() {
        return ccName;
    }

    public int getStationCount() {
        return stationCount;
    }

    public void setStationCount(int stationCount) {
        this.stationCount = stationCount;
    }
}