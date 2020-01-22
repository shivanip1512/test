package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.dr.ThermostatRampRateValues;
import com.cannontech.dr.itron.model.ItronCycleType;
import com.cannontech.dr.nest.model.v3.LoadShapingOptions;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;
import com.cannontech.loadcontrol.gear.model.EcobeeSetpointValues;
import com.cannontech.loadcontrol.gear.model.LMThermostatGear;

public interface LMGearDao {
    /**
     * Inserts BeatThePeak Gear data to a database (gearID/AlertLevel pair)
     * @param btpContainer - Beat The Peak Gear data to be inserted into database
     */
    public void insertContainer(BeatThePeakGearContainer btpContainer);

    /**
     * Updates BeatThePeak Gear data in a database (gearID/AlertLevel pair)
     * @param btpContainer - Beat The Peak Gear data to overwrite existing Beat the Peak data for that gear ID
     */
    public void updateContainer(BeatThePeakGearContainer btpContainer);

    /**
     * Retrieves BeatThePeak Gear data from a database (gearID/AlertLevel pair)
     * @return com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer - Data stored under ID
     * @param gearId - gearID of requested container
     */
    public BeatThePeakGearContainer getContainer(int gearId);

    /**
     * Removes BeatThePeak Gear data to a database (gearID/AlertLevel pair)
     * @param gearId - gear ID of the gear to be deleted
     */
    public void deleteBeatThePeakGear(int gearId);

    /**
     * Retrieves a single LMProgramDirectGear object from the database.
     * @param gearId The gear id of the requested gear.
     */
    public LMProgramDirectGear getByGearId(Integer gearId);

    /**
     * Retrieves multiple LMProgramDirectGear objects from the database in a map of gear id to gear.
     * @param gearIds The gear ids of the requested gears.
     */
    public Map<Integer, LMProgramDirectGear> getByGearIds(Iterable<Integer> gearIds);

    /**
     * Retrieves all LMProgramDirectGear objects from the database in a map of gear id to gear.
     */
    public Map<Integer, LMProgramDirectGear> getAllGears();

    /**
     * Retrieves the Delta D and Td values for a thermostat ramping gear. 
     * The ramp rate can then be calculated as:
     * Delta D / Td
     * It is assumed that the formula assigned to the gear has been constructed with the proper temperature
     * units in mind for that gear.  No conversion will be done between Fahrenheit and Celsius when retrieving 
     * input values. 
     *
     * @param gearId The gear Id for which values are requested.
     */
    public ThermostatRampRateValues getThermostatGearRampRateValues(int gearId);

    /**
     * Retrieves the ramp rate for a simple thermostat ramping gear.  The value is in degrees F per hour.
     * 
     * @param gearId The gear Id for which values are requested.
     */
    public Double getSimpleThermostatGearRampRate(int gearId);
    
    /**
     * Retrieves the name of the gear by gear id.
     * 
     * @param gearId Id of gear whose name is requested.
     * @return The name of the gear.
     */
    public String getGearName(int gearId);

    List<LiteGear> getAllLiteGears();
    /**
     * Retrieves all the gears for the program with specified programId.
     */
    List<LiteGear> getAllLiteGears(Integer programID);

    /**
     * Retrieves LoadShapingOptions by GearId
     */
    public LoadShapingOptions getLoadShapingOptions(Integer gearId);

    /**
     * Retrieves ItronCycleType by GearId
     */

    public ItronCycleType getItronCycleType(Integer gearId);

    /**
     * Retrieves EcobeeSetpointValues by GearId
     */

    public EcobeeSetpointValues getEcobeeSetpointValues(Integer gearId);

    /**
     * Retrieves LMThermostatGear by GearId
     */

    public LMThermostatGear getLMThermostatGear(Integer gearId);
}