/*
package com.cannontech.web.api.schedules.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.schedules.model.HolidaySchedule;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.web.api.schedules.service.HolidayScheduleService;

public class HolidayScheduleServiceImpl implements HolidayScheduleService {
    @Autowired private DBPersistentDao dbPersistentDao;

    @Override
    public HolidaySchedule create(HolidaySchedule holidaySchedule) {
        com.cannontech.database.data.holiday.HolidaySchedule dbPersistanceSchedule = new com.cannontech.database.data.holiday.HolidaySchedule();
        holidaySchedule.buildDBPersistent(dbPersistanceSchedule);
        dbPersistanceSchedule.setHolidayScheduleID(com.cannontech.database.db.holiday.HolidaySchedule.getNextHolidayScheduleID());
        dbPersistentDao.performDBChange(dbPersistanceSchedule, TransactionType.INSERT);
        holidaySchedule.buildModel(dbPersistanceSchedule);
        return holidaySchedule;
    }

}
*/