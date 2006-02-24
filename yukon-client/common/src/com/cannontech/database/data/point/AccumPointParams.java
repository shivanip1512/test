package com.cannontech.database.data.point;

public class AccumPointParams extends PointParams {

    private double  mult;
    
    public AccumPointParams(int offset, String name) {
        super(offset, name);
    }

    public AccumPointParams(double d, int offset, String name) {
       super(offset, name);
       mult = d;
    }

    @Override
    public int getType() {

        return PointTypes.PULSE_ACCUMULATOR_POINT;
    }

    public double getMult() {
        return mult;
    }

    public void setMult(double mult) {
        this.mult = mult;
    }

}
