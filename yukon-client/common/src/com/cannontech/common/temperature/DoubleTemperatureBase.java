package com.cannontech.common.temperature;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A helper base class for Temperature objects that represent their value internally as a double.
 */
public abstract class DoubleTemperatureBase extends Temperature {
    private final TemperatureUnit unit;
    private final double temperature;
    
    protected DoubleTemperatureBase(double temperature, TemperatureUnit unit) {
        this.temperature = temperature;
        this.unit = unit;
    }
    
    /**
     * Returns the value of the temperature.
     * @return
     */
    public double getValue() {
        return temperature;
    }
    
    /**
     * Returns the int value of the temperature with a HALF_UP RoundingMode.
     * @return int
     */
    public int toIntValue() {
        return toBigDecimal(0).intValue();
    }
    
    /**
     * Returns the value of the temperature as a scaled BigDecimal with a HALF_UP RoundingMode.
     * @param scale
     * @return
     */
    public BigDecimal toBigDecimal(int scale) {
        return toBigDecimal(scale, RoundingMode.HALF_UP);
    }
    
    /**
     * Returns the value of the temperature as a scaled BigDecimal with the specified RoundingMode.
     * @param scale
     * @return
     */
    public BigDecimal toBigDecimal(int scale, RoundingMode roundingMode) {
        return new BigDecimal(temperature).setScale(scale, roundingMode);
    }
    
    /**
     * Returns the TemperatureUnit for this temperature object.
     * @return
     */
    public TemperatureUnit getUnit() {
        return unit;
    }
    
    /** 
     * Converts the temperature into a BigDecimal with scale 1 and combines it with the suffix
     * for the unit.
     * 
     * Examples:
     *    45.6°F
     *    100.4°C
     */
    @Override
    public String toString() {
        return toBigDecimal(1).toPlainString() + getUnit().getSuffix();
    }
}
