package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroupPoint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(allowGetters = true, ignoreUnknown = true)
public class LoadGroupPoint extends LoadGroupBase<LMGroupPoint> {

    private Integer deviceIdUsage;
    private Integer pointIdUsage;
    private Integer startControlRawState;

    public Integer getDeviceIdUsage() {
        return deviceIdUsage;
    }

    public void setDeviceIdUsage(Integer deviceIdUsage) {
        this.deviceIdUsage = deviceIdUsage;
    }

    public Integer getPointIdUsage() {
        return pointIdUsage;
    }

    public void setPointIdUsage(Integer pointIdUsage) {
        this.pointIdUsage = pointIdUsage;
    }

    public Integer getStartControlRawState() {
        return startControlRawState;
    }

    public void setStartControlRawState(Integer startControlRawState) {
        this.startControlRawState = startControlRawState;
    }

    @Override
    public void buildModel(LMGroupPoint lmGroupPoint) {
        // Set parent fields
        super.buildModel(lmGroupPoint);

        // Set from LMGroupPoint fields
        setDeviceIdUsage(lmGroupPoint.getLMGroupPoint().getDeviceIDUsage());
        setPointIdUsage(lmGroupPoint.getLMGroupPoint().getPointIDUsage());
        setStartControlRawState(lmGroupPoint.getLMGroupPoint().getStartControlRawState());

    }

    @Override
    public void buildDBPersistent(LMGroupPoint group) {
        // Set parent fields
        super.buildDBPersistent(group);

        // Set LMGroupPoint fields
        com.cannontech.database.db.device.lm.LMGroupPoint lmGroupPoint = group.getLMGroupPoint();
        lmGroupPoint.setDeviceIDUsage(getDeviceIdUsage());
        lmGroupPoint.setPointIDUsage(getPointIdUsage());
        lmGroupPoint.setStartControlRawState(getStartControlRawState());

    }

}
