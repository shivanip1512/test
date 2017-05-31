package com.cannontech.simulators.dao;

import org.joda.time.Instant;

import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.simulators.SimulatorType;


public interface YukonSimulatorSettingsDao {
    
    /**
     * Sets the system value as specified.
     * 
     * @param property a property
     * @param value a value that is compatible with the property's type
     */
    public boolean initYukonSimulatorSettings();
    
    /**
     * Checks to see if the table YukonSimulatorSettings exists in the 
     * user's db. If it doesn't it creates the calls createYukonSimulatorSettingsTable. 
     *
     * @return true if the table already exists, otherwise return value of
     * createYukonSimulatorSettingsTable();
     */
    public boolean createYukonSimulatorSettingsTable();
    
    /**
     * Attempts to create the YukonSimulatorSettings table.
     *
     * @return true if the table already exists, otherwise return value of
     * populateYukonSimulatorSettingsTable();
     */
    public SimulatorSettings getSimulatorSettings(SimulatorType simType);
    
    /**
     * @return the simulator settings of the given simulator type from the YukonSimulatorSetings table.
     */
public boolean setSimulatorSettings(SimulatorSettings settings, SimulatorType simType);
    
    /**
     * Send the simulator settings of the given simulator type to the YukonSimulatorSetings table.
     * 
     * @return true on success and false on failure.
     */
public boolean populateYukonSimulatorSettingsTable();
    
    /**
     * Attempts to populate the YukonSimulatorSettings table with the default values of each
     * the enumerated properties in YukonSimulatorSettingsKey. 
     *
     * @return true if all of the entries are properly initialized to their default values, and
     * false otherwise.
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
    public String getStringValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType);
    
    /**
     * Returns the user's value of the specified property as a boolean.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Boolean.
     * 
     * @param property any property with a Boolean return type
     * @return  the value of the property or the default if uninitialized
     */
    public boolean getBooleanValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType);
    
    /**
     * Returns the user's value of the specified property as a long.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an long.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or the default if uninitialized
     */
    public long getLongValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType);
    
    /**
     * Returns the user's value of the specified property as an integer.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an int.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or the default if uninitialized
     */
    public int getIntegerValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType);
    
    /**
     * Returns the user's value of the specified property as a float.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return an float.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or 0 the default if uninitialized
     */
    public float getFloatValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType);
    
    /**
     * Returns the user's value of the specified property as a double.
     * 
     * This method may only be called with properties that have a type return type 
     * that can be cast to Number. Rounding or truncation may occur to return a double.
     * 
     * @param property any property with a Number return type
     * @return  the value of the property or the default if uninitialized
     */
    public double getDoubleValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType);
    
    /**
     * Returns an Instant specified by the property key.
     * 
     * This method should only be called with keys based on the InstantType.
     */
    public Instant getInstantValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType);

}
