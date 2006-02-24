package com.cannontech.database.data.point;

public class StatusPointParams extends PointParams {

    public StatusPointParams(int offset, String name) {
        super(offset, name);

    }

    @Override
    public int getType() {

        return PointTypes.STATUS_POINT;
    }

}
