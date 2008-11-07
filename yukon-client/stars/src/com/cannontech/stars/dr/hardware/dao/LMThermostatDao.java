package com.cannontech.stars.dr.hardware.dao;

import java.util.List;

public interface LMThermostatDao {

    /**
     * 
     * @param eventIds
     */
    public void deleteManualEvents(Integer eventIds);

    /**
     * 
     * @param inventoryIds
     */
    public void deleteSchedulesForInventory(Integer inventoryId);

    /**
     * 
     * @param seasonIds
     */
    public void deleteSeasons(List<Integer> seasonIds);

    /**
     * 
     * @param seasonIds
     */
    public void deleteSeasonEntrys(List<Integer> seasonIds);

    /**
     * 
     * @param scheduleId
     * @return
     */
    public List<Integer> getSeasonsForSchedule(Integer scheduleId);
    
    /**
     * 
     * @param inventoryId
     * @return
     */
    public List<Integer> getAllManualEvents(Integer inventoryId);

    /**
     * 
     * @param accountId
     * @return
     */
    public List<Integer> getSchedulesForAccount(Integer accountId);

    /**
     * 
     * @param accountId
     */
    public void deleteSchedulesForAccount(Integer accountId);
    
}
