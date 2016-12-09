package com.cannontech.dr.honeywellWifi;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.joda.time.DateTime;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;

/**
 * This service uses stored runtime state data to calculate hourly runtimes for Honeywell wifi thermostats.
 */
public interface HoneywellWifiRuntimeCalcService {

    public List<YukonPao> getAllThermostats();
    
    public Map<Integer, DateTime> getLastRuntimes(List<YukonPao> thermostats);
    
    public DateTime getEndOfRuntimeCalcRange();
    
    public boolean insertRuntimes(YukonPao pao, Map<DateTime, Integer> hourlyRuntimeSeconds, 
                                  Predicate<Map.Entry<DateTime, Integer>> filter);
    
    public DatedRuntimeStatus getRuntimeStatusFromPoint(PointValueHolder pointValue);
    
    /**
     * Loads runtime status data for each Honeywell wifi thermostat, between the last recorded runtime for that device
     * and the start of the previous hour (or last time we received a Honeywell message). Uses this runtime status data
     * to calculate the hourly runtimes for that period, for each device.
     */
    public void calculateRuntimes();
}
