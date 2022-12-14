package com.cannontech.database.db.holiday;

import java.sql.SQLException;
import java.util.List;
import java.util.function.ToIntFunction;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteHolidaySchedule;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.yukon.IDatabaseCache;

public class HolidaySchedule extends DBPersistent {

    private Integer holidayScheduleId;
    private String holidayScheduleName;

    public static final String TABLE_NAME = "HolidaySchedule";
    public static final String SETTER_COLUMNS[] = { "HolidayScheduleName" };
    public static final String CONSTRAINT_COLUMNS[] = { "HolidayScheduleID" };

    public HolidaySchedule() {
    }

    public void add() throws SQLException {
        Object addValues[] = { getHolidayScheduleId(), getHolidayScheduleName() };

        add(TABLE_NAME, addValues);
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getHolidayScheduleId());
    }

    public Integer getHolidayScheduleId() {
        return holidayScheduleId;
    }
    
    public boolean isExists() {
        return holidayScheduleId >= 0;
    }

    public String getHolidayScheduleName() {
        return holidayScheduleName;
    }

    public final static Integer getNextHolidayScheduleID() {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            List<LiteHolidaySchedule> holidaySchedules = cache.getAllHolidaySchedules();

            int maxId = holidaySchedules.parallelStream()
                .mapToInt(new ToIntFunction<LiteHolidaySchedule>() {

                    @Override
                    public int applyAsInt(LiteHolidaySchedule schedule) {
                        return schedule.getLiteID();
                    }
                })
                .max()
                .orElse(0);

            /* TODO Java 8
             *
             * int maxId = holidaySchedules.parallelStream()
             *     .mapToInt(i -> i.getLiteID())
             *     .max()
             *     .orElse(0);
             *
             */

            return maxId + 1;
        }
    }

    public void retrieve() throws SQLException {
        Object constraintValues[] = { getHolidayScheduleId() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setHolidayScheduleName((String) results[0]);
        }

    }

    public void setHolidayScheduleId(Integer id) {
        holidayScheduleId = id;
    }

    public void setHolidayScheduleName(String name) {
        holidayScheduleName = name;

    }

    public void update() throws SQLException {
        Object setValues[] = { getHolidayScheduleName() };
        Object constraintValues[] = { getHolidayScheduleId() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

}