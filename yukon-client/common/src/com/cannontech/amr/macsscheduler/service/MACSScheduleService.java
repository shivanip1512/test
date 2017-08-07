package com.cannontech.amr.macsscheduler.service;

import java.util.Date;
import java.util.List;

import com.cannontech.amr.macsscheduler.model.MacsException;
import com.cannontech.amr.macsscheduler.model.MacsSchedule;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface MACSScheduleService {

    /**
     * Starts schedule. Doesn't wait for confirmation.
     * 
     * @throws MacsException - couldn't communicate with Macs Service.
     */
    void start(int scheduleId, Date startDate, Date stopDate, LiteYukonUser user) throws MacsException;
    
    /**
     * Stops schedule. Doesn't wait for confirmation.
     * 
     * @throws MacsException - couldn't communicate with Macs Service.
     */
    void stop(int scheduleId, Date stopDate, LiteYukonUser user) throws MacsException;

    /**
     * Deletes schedule. Waits for confirmation.
     * 
     * @throws MacsException - couldn't communicate with Macs Service or delete was unsuccessful.
     */
    void delete(int scheduleId, LiteYukonUser user) throws MacsException;

    /**
     * Returns a list of all categories.
     */
    List<String> getCategories();
    
    /**
     * Creates schedule. Waits for confirmation.
     * 
     * @throws DuplicateException - schedule with this name already exist
     * @throws MacsException - couldn't communicate with Macs Service or create was unsuccessful.
     */
    int createSchedule(MacsSchedule macsSchedule, LiteYukonUser user) throws DuplicateException, MacsException;

    /**
     * Updates schedule. Waits for confirmation.
     * 
     * @throws DuplicateException - schedule with this name already exist
     * @throws MacsException - couldn't communicate with Macs Service or update was unsuccessful.
     */
    void updateSchedule(MacsSchedule macsSchedule, LiteYukonUser user) throws DuplicateException, MacsException;

    /**
     * Toggles enable/disable schedule. Doesn't wait for confirmation.
     * 
     * @throws MacsException - couldn't communicate with Macs Service.
     */
    void enableDisableSchedule(int scheduleId, LiteYukonUser user) throws MacsException;

    /**
     * Returns true if the name already exists.
     * 
     * @param name - to check
     * @param id - id to ignore
     * @return
     */
    boolean isScheduleNameExists(String name, int id);

    /**
     * Returns schedules by category.
     */
    List<MacsSchedule> getSchedulesByCategory(String category);

    /**
     * Returns schedule.
     */
    MacsSchedule getMacsScheduleById(int scheduleId);

    /**
     * Returns all schedules.
     */
    List<MacsSchedule> getAllSchedules();

    /**
     * Returns script file contents.
     */
    String getScript(int scheduleId, LiteYukonUser user) throws MacsException;
}
