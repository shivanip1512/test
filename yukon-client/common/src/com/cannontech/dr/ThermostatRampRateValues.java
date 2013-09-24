package com.cannontech.dr;

public class ThermostatRampRateValues {

    int valueD;
    int valueTd;

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
     * @return The ramp rate in degrees per hour as a double value.
     */
    public double getRampRate() {
        return valueD / (valueTd * 60);
    }
}
