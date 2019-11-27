package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroupPoint;

public class LoadGroupPoint extends LoadGroupBase<LMGroupPoint> {

    private LMDto deviceUsage;
    private LMDto pointUsage;
    private ControlRawState startControlRawState;

    public LMDto getDeviceUsage() {
        return deviceUsage;
    }

    public void setDeviceUsage(LMDto deviceUsage) {
        this.deviceUsage = deviceUsage;
    }

    public LMDto getPointUsage() {
        return pointUsage;
    }

    public void setPointUsage(LMDto pointUsage) {
        this.pointUsage = pointUsage;
    }

    public ControlRawState getStartControlRawState() {
        return startControlRawState;
    }

    public void setStartControlRawState(ControlRawState startControlRawState) {
        this.startControlRawState = startControlRawState;
    }

    @Override
    public void buildModel(LMGroupPoint lmGroupPoint) {
        // Set parent fields
        super.buildModel(lmGroupPoint);

        // Set from LMGroupPoint fields
        setDeviceUsage(new LMDto(lmGroupPoint.getLMGroupPoint().getDeviceIDUsage(), ""));
        setPointUsage(new LMDto(lmGroupPoint.getLMGroupPoint().getPointIDUsage(), ""));
        setStartControlRawState(new ControlRawState(lmGroupPoint.getLMGroupPoint().getStartControlRawState(), ""));

    }

    @Override
    public void buildDBPersistent(LMGroupPoint group) {
        // Set parent fields
        super.buildDBPersistent(group);

        // Set LMGroupPoint fields
        com.cannontech.database.db.device.lm.LMGroupPoint lmGroupPoint = group.getLMGroupPoint();
        lmGroupPoint.setDeviceIDUsage(getDeviceUsage().getId());
        lmGroupPoint.setPointIDUsage(getPointUsage().getId());
        lmGroupPoint.setStartControlRawState(getStartControlRawState().getRawState());
    }

}
