package com.cannontech.database.data.point;

public class AccumPointParams extends PointParams {

    private double  mult;
    private int uofm;
    
    public AccumPointParams(int offset, String name) {
        super(offset, name);
    }

    public AccumPointParams(double d, int offset, String name, int uofm_) {
       super(offset, name);
       mult = d;
       uofm = uofm_;
    }


    public int getType() {

        return PointTypes.PULSE_ACCUMULATOR_POINT;
    }

    public double getMult() {
        return mult;
    }

    public void setMult(double mult) {
        this.mult = mult;
    }

    public int getUofm() {
        return uofm;
    }

    public void setUofm(int uofm) {
        this.uofm = uofm;
    }

}
