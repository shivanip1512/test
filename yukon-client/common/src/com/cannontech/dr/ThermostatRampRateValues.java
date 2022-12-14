package com.cannontech.dr;

public final class ThermostatRampRateValues {

    private final int valueD;
    private final int valueTd;

    public ThermostatRampRateValues(int valueD, int valueTd) {
        super();
        this.valueD = valueD;
        this.valueTd = valueTd;
    }

    public int getValueD() {
        return valueD;
    }

    public int getValueTd() {
        return valueTd;
    }

    /** Gets the ramp rate in degrees per hour.
     *  ValueD refers to the delta change in temperature over the given time.
     *  ValueTd represents that required time in minutes. 
     *  If ValueD is 1.0 and ValueTd is 60 the ramp rate will be:
     *      D / Td = 1.0 degree / (60 min / (60 min/hour)) 
     *             = 1.0 degree / 1 hour 
     *             = 1 degree per hour.
     * @return The ramp rate in degrees per hour as a double value.
     */
    public double getRampRate() {
        return ((double) valueD / valueTd / 60);
    }
}
