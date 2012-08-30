package com.cannontech.core.dao;

import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;

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
    public void delete(int gearId);

}