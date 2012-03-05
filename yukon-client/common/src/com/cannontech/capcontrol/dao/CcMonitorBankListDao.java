package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.enums.Phase;

public interface CcMonitorBankListDao {
    
    /**
     * Given a zoneId, returns a list of VoltageLimitedDeviceInfo for every
     * voltage limited device attached to that zone.
     */
    public List<VoltageLimitedDeviceInfo> getDeviceInfoByZoneId(int zoneId);
    
    /**
     * Updates the Phase, Lower Limit, Upper Limit, and Override Strategy
     * values for a single device entry.
     */
    public void updateDeviceInfo(VoltageLimitedDeviceInfo deviceInfo);
    
    /**
     * Updates the Phase, Lower Limit, Upper Limit, and Override Strategy
     * values for multiple device entries.
     */
    public void updateDeviceInfo(List<VoltageLimitedDeviceInfo> deviceInfoList);
    
    /**
     * Updates the phase on the specified point.
     * @return 
     */
    public int updatePhase(int pointId, Phase phase);
    
    /**
     * Inserts a new additional monitor point entry, using the specified point id 
     * and phase, and inheriting limits from subbus strategy settings.
     * @return 
     */
    public int addAdditionalMonitorPoint(int pointId, int zoneId, Phase phase);
    
    /**
     * Inserts new entry for voltage Y point on the specified regulator.
     * @return 
     */
    public int addRegulatorPoint(int regulatorId);
    
    /**
     * Updates the entry for voltage Y point on the specified regulator.
     */
    public void updateRegulatorPoint(int regulatorId);
    
    /**
     * Removes the voltage Y point entry for the specified regulator unless
     * the voltage Y pointId matches the specified pointId.
     * @return true if a point was deleted, otherwise false
     */
    public boolean deleteNonMatchingRegulatorPoint(int regulatorId, int pointIdToMatch);
    
    /**
     * Removes all specified point entries from the table.
     * @return 
     */
    public int removePoints(List<Integer> pointIds);
    
    /**
     * Removes all regulator and additional monitor points associated with the
     * specified zone.
     */
    public void removePointsByZone(int zoneId);
    
    /**
     * Inserts an entry with the settings specified in the VoltageLimitedDeviceInfo
     * object. DisplayOrder is defaulted to 0, Scannable is defaulted to false, and
     * NINAvg is defaulted to 3.
     * @return 
     */
    public int addDeviceInfo(VoltageLimitedDeviceInfo info);
}
