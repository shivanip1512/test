package com.cannontech.web.api.schedules.service;

import com.cannontech.common.schedules.model.SeasonSchedule;

public interface SeasonScheduleService {

    /**
     * Create a notification group
     */
    public SeasonSchedule create(SeasonSchedule seasonSchedule);

}
