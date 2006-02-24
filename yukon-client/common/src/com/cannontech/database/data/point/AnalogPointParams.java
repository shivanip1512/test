package com.cannontech.database.data.point;

public class AnalogPointParams extends PointParams {
    private double mult;

    public AnalogPointParams(int offset, String name) {
        super(offset, name);
    }

    public AnalogPointParams(double d, int offset, String name) {
        super(offset, name);
        mult = d;
    }

    @Override
    public int getType() {
        return PointTypes.ANALOG_POINT;
    }

    public double getMult() {
        return mult;
    }

    public void setMult(double mult) {
        this.mult = mult;
    }

}
