package com.cannontech.common.tdc.service;

import java.util.List;

import org.joda.time.DateTimeZone;

import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface TdcService {
    
    /**
     * Method to get a point state.
     * @param pointId
     * @return 
     */
    public String getPointState(int pointId);
    /**
     * Method to acknowledge an active alarm 
     * @param pointId
     * @param condition
     * @return 
     */
    public void acknowledgeAlarm(int pointId, int condition, LiteYukonUser user);

    /**
     * Method to acknowledge all active alarms for a point
     * @param pointId
     * @return 
     */
    public int acknowledgeAlarmsForPoint(int pointId,  LiteYukonUser user);

    /**
     * Method to acknowledge all active alarms
     * @param user
     * @return the number of alarms request to acknowledge was send
     */
    public int acknowledgeAllAlarms(LiteYukonUser user);

    /**
     * Method to acknowledge all active alarms for a display
     * @param display
     * @param timeZone
     * @return the number of alarms request to acknowledge was send
     */
    public int acknowledgeAlarmsForDisplay(Display display, LiteYukonUser user);
    
    /**
     * Method to get display data
     * @param display
     * @param timeZone
     * @return the number of alarms request to acknowledge was send
     */
    public List<DisplayData> getDisplayData(Display display, DateTimeZone timeZone);

    /**
     * Method to get unacknowledged alarms for a point
     * @param pointId
     * @return
     */
    public List<DisplayData> getUnacknowledgedAlarms(int pointId);

    /**
     * Method to get all alarms. If showActive is true the list of all unacknowledged or active
     * alarms will be returned otherwise the list of only unacknowledged alarms will be returned.
     * @param showActive
     * @return
     */
    public List<DisplayData> getAlarms(boolean showActive);
    
    /**
     * Method to send point data.
     * @param pointId
     * @param value
     * @param user
     */
    public void sendPointData(int pointId, double value, LiteYukonUser user);
    
    /**
     * Returns true if manual control is enabled
     * @param pointId
     * @return
     */
    public boolean isManualControlEnabled(int pointId);
    
    /**
     * Method to get an unacknowledged alarm count for a display 
     * @param displayId
     * @return
     */
    public int getUnackAlarmCountForDisplay(int displayId);
    
    /**
     * Method to get an unacknowledged alarm count
     * @return
     */
    public int getUnackAlarmCount();
    
    /**
     * Method to get an unacknowledged alarm count for a point
     * @param pointId
     * @return
     */
    public int getUnackAlarmCountForPoint(int pointId);
    
    /**
     * Method to get an unacknowledged alarm count for a point and condition. Possible return values
     * are one or zero.
     * @param pointId
     * @param condition
     * @return
     */
    public int getUnackAlarmCountForPoint(int pointId, int condition);
}
