package com.cannontech.common.tdc.service;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTimeZone;

import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface TdcService {
    
    /**
     * Gets a point state.
     */
    public String getPointState(int pointId);
    /**
     * Acknowledges an active alarm 
     */
    public void acknowledgeAlarm(int pointId, int condition, LiteYukonUser user);

    /**
     * Acknowledges all active alarms for a point
     */
    public int acknowledgeAlarmsForPoint(int pointId,  LiteYukonUser user);

    /**
     * Acknowledges all active alarms
     */
    public int acknowledgeAllAlarms(LiteYukonUser user);

    /**
     * Acknowledges all active alarms for a display
     */
    public int acknowledgeAlarmsForDisplay(Display display, LiteYukonUser user);
    
    /**
     * Get display data
     */
    public List<DisplayData> getDisplayData(Display display, DateTimeZone timeZone);

    /**
     * Unacknowledged alarms for a point
     */
    public List<DisplayData> getUnacknowledgedAlarms(int pointId);

    /**
     * Gets all alarms. If showActive is true the list of all unacknowledged or active
     * alarms will be returned otherwise the list of only unacknowledged alarms will be returned.
     */
    public List<DisplayData> getAlarms(boolean showActive);
    
    /**
     * Sends point data.
     */
    public void sendPointData(int pointId, double value, LiteYukonUser user);
    
    /**
     * Returns true if manual control is enabled
     */
    public boolean isManualControlEnabled(int pointId);
    
    /**
     * Gets an unacknowledged alarm count for a display 
     */
    public int getUnackAlarmCountForDisplay(int displayId);
    
    /**
     * Gets unacknowledged alarm count
     */
    public int getUnackAlarmCount();
    
    /**
     * Gets an unacknowledged alarm count for a point
     */
    public int getUnackAlarmCountForPoint(int pointId);
    
    /**
     * Gets an unacknowledged alarm count for a point and condition. Possible return values
     * are one or zero.
     */
    public int getUnackAlarmCountForPoint(int pointId, int condition);
    
    /**
     * Gets an unacknowledged or active alarm color state box  for a point and condition.
     */
    public String getUnackOrActiveAlarmColor(int pointId, int condition);
    
    /**
     * Gets an unacknowledged or active alarm color state box  for a point.
     */
    public String getUnackOrActiveAlarmColor(int pointId);
    
    /**
     * Gets an unacknowledged alarm color state boxes for a  display data. (<point id ,<condition, color state box string>)
     **/
    public Map<Integer, Map<Integer, String>> getUnackAlarmColorStateBoxes(Display display, List<DisplayData> displayData);
    
    /**
     * Returns true if manual entry is enabled
     **/
 
    boolean isManualEntryEnabled(int pointId, int pointTypeId, boolean hasPointValueColumn);
    
    
}
