package com.cannontech.web.api.schedules.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.schedules.model.SeasonSchedule;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.TransactionType;
import com.cannontech.web.api.schedules.service.SeasonScheduleService;

public class SeasonScheduleServiceImpl implements SeasonScheduleService{

    @Autowired private DBPersistentDao dbPersistentDao;
    @Override
    public SeasonSchedule create(SeasonSchedule seasonSchedule) {
        com.cannontech.database.data.season.SeasonSchedule dbPersistanceSeasonSchedule = new com.cannontech.database.data.season.SeasonSchedule();
        seasonSchedule.buildDBPersistent(dbPersistanceSeasonSchedule);
        
        dbPersistanceSeasonSchedule.setScheduleID(com.cannontech.database.db.season.SeasonSchedule.getNextSeasonScheduleID());
        dbPersistentDao.performDBChange(dbPersistanceSeasonSchedule, TransactionType.INSERT);
        seasonSchedule.buildModel(dbPersistanceSeasonSchedule);
        return seasonSchedule;
    }
}
