package com.cannontech.simulators.dao;

import org.joda.time.Instant;

public interface YukonSimulatorSettingsDao {

    /**
     * Ensures that the YukonSimulatorSettings table exists and is populated with values.
     *
     * @return true iff initialization and population of YukonSimulatorSettings table is successful.
     */
    public boolean initYukonSimulatorSettings();
    
    /**
     * Sets the system value as specified.
     * 
     * @param property a property
     * @param value a value that is compatible with the property's type
     */
    public void setValue(YukonSimulatorSettingsKey property, Object value);

    /**
     * Returns the user's value of the specified property as a String.
     * 
     * Unlike the other methods, this can be called regardless of the type
     * of the property, it essentially calls a toString on the value and
     * returns that.
     * 
     * @param property any property
     * @return the value of the property the default if uninitialized
     */
    public String getStringValue(YukonSimulatorSettingsKey property);

    /**
     * Returns the user's value of the specified property as a boolean.
     * 
     * This method may only be called with properties that have a type return type
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @return the value of the property or the default if uninitialized
     */
    public boolean getBooleanValue(YukonSimulatorSettingsKey property);

    /**
     * Returns the user's value of the specified property as a long.
     * 
     * This method may only be called with properties that have a type return type
     * that can be cast to Number. Rounding or truncation may occur to return an long.
     * 
     * @param property any property with a Number return type
     * @return the value of the property or the default if uninitialized
     */
    public long getLongValue(YukonSimulatorSettingsKey property);

    /**
     * Returns the user's value of the specified property as an integer.
     * 
     * This method may only be called with properties that have a type return type
     * that can be cast to Number. Rounding or truncation may occur to return an int.
     * 
     * @param property any property with a Number return type
     * @return the value of the property or the default if uninitialized
     */
    public int getIntegerValue(YukonSimulatorSettingsKey property);

    /**
     * Returns the user's value of the specified property as a float.
     * 
     * This method may only be called with properties that have a type return type
     * that can be cast to Number. Rounding or truncation may occur to return an float.
     * 
     * @param property any property with a Number return type
     * @return the value of the property or 0 the default if uninitialized
     */
    public float getFloatValue(YukonSimulatorSettingsKey property);

    /**
     * Returns the user's value of the specified property as a double.
     * 
     * This method may only be called with properties that have a type return type
     * that can be cast to Number. Rounding or truncation may occur to return a double.
     * 
     * @param property any property with a Number return type
     * @return the value of the property or the default if uninitialized
     */
    public double getDoubleValue(YukonSimulatorSettingsKey property);

    /**
     * Returns an Instant specified by the property key.
     * 
     * This method should only be called with keys based on the InstantType.
     */
    public Instant getInstantValue(YukonSimulatorSettingsKey property);

}
