package com.cannontech.database.data.point;

public class StatusPointParams extends PointParams {
    int controlType = 0;
    public StatusPointParams(int offset, String name) {
        super(offset, name);

    }

    public StatusPointParams(int offset, String name, int controltype) {
        super(offset, name);
        controlType = controltype;
    }

    @Override
    public int getType() {

        return PointTypes.STATUS_POINT;
    }

    public int getControlType() {
        return controlType;
    }

    public void setControlType(int controlType) {
        this.controlType = controlType;
    }

}
