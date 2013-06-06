package com.cannontech.stars.core.dao;

import java.util.List;

public interface MeterHardwareBaseDao {
    /**
     * Returns true if the meter has any assigned switches.
     */
    boolean hasSwitchAssigned(int meterId);

    /**
     * Returns a list of switch inventory id's assigned to the meter.
     */
    List<Integer> getSwitchAssignmentsForMeter(int meterId);

    int getInventoryId(int accountId, String meterNumber, int ecId);
}
