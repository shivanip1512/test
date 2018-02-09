package com.cannontech.common.tdc.service;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.tdc.dao.DisplayDataDao.SortBy;
import com.cannontech.common.tdc.model.AlarmFilter;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface TdcService {

    /**
     * Gets a point state.
     */
    String getPointState(int pointId);

    /**
     * Acknowledges an active alarm
     */
    void acknowledgeAlarm(int pointId, int condition, LiteYukonUser user);

    /**
     * Acknowledges all active alarms for a point
     */
    int acknowledgeAlarmsForPoint(int pointId, LiteYukonUser user);

    /**
     * Acknowledges all active alarms
     */
    int acknowledgeAllAlarms(LiteYukonUser user);

    /**
     * Acknowledges all active alarms for a display
     */
    int acknowledgeAlarmsForDisplay(Display display, LiteYukonUser user);

    /**
     * Get display data
     */
    List<DisplayData> getDisplayData(Display display);

    /**
     * Unacknowledged alarms for a point
     */
    List<DisplayData> getUnacknowledgedAlarms(int pointId);

    /**
     * Gets all alarms. If showActive is true the list of all unacknowledged or active
     * alarms will be returned otherwise the list of only unacknowledged alarms will be returned.
     */
    List<DisplayData> getAlarms(boolean showActive);

    /**
     * Returns true if manual control is enabled
     */
    boolean isManualControlEnabled(int pointId);

    /**
     * Gets an unacknowledged alarm count for a display
     */
    int getUnackAlarmCountForDisplay(int displayId);

    /**
     * Gets unacknowledged alarm count
     */
    int getUnackAlarmCount();

    /**
     * Gets an unacknowledged alarm count for a point
     */
    int getUnackAlarmCountForPoint(int pointId);

    /**
     * Gets an unacknowledged alarm count for a point and condition. Possible return values
     * are one or zero.
     */
    int getUnackAlarmCountForPoint(int pointId, int condition);

    /**
     * Gets an unacknowledged or active alarm color state box for a point and condition.
     */
    String getUnackOrActiveAlarmColor(int pointId, int condition);

    /**
     * Gets an unacknowledged or active alarm color state box for a point.
     */
    String getUnackOrActiveAlarmColor(int pointId);

    /**
     * Gets an unacknowledged alarm color state boxes for a display data. (<point id ,<condition, color state
     * box string>)
     **/
    Map<Integer, Map<Integer, String>> getUnackAlarmColorStateBoxes(Display display, List<DisplayData> displayData);

    /**
     * Returns true if manual entry is enabled
     **/

    boolean isManualEntryEnabled(int pointId, int pointTypeId, boolean hasPointValueColumn);


    /**
     * Creates a new display from existing display.
     * 
     * @param displayId
     * @param name - new display name
     * @return created display
     */
    Display copyCustomDisplay(int displayId, String name);

    /**
     * Creates custom display if one of the point ids is 0 inserts a blank line.
     */

    Display createCustomDisplayForPoints(String name, String title, String description, List<Integer> pointIds) throws DuplicateException;

    /**
     * Creates custom display with all the points for the devices. Blank line is inserted as a separator
     * between devices.
     */

    Display createCustomDisplayForDevices(String name, String title, String description, List<Integer> deviceIds);

    /**
     * Updates custom display.
     */
    Display updateCustomDisplay(int displayId, String name, String title, String description, List<Integer> pointIds) throws DuplicateException;

    /**
     * @param sortBy nullable.
     * @param direction nullable.
     */
    SearchResults<DisplayData> getSortedDisplayData(Display display, DateTimeZone timeZone,
                                                    PagingParameters paging, SortBy sortBy,
                                                    Direction direction, DateTime date,
                                                    AlarmFilter alarmFilter);

    /**
     * Delete custom display.
     */
    void deleteCustomDisplay(int displayId);

    List<DisplayData> getAlarms(AlarmFilter alarmFilter, DateTimeZone timeZone, DateTime date, SortBy sortBy,
                                Direction direction);

}
