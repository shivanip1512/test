package com.cannontech.cbc.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.cbc.model.Season;
import com.cannontech.database.db.season.SeasonSchedule;

public interface SeasonScheduleDao {

    public List<Season> getSeasonsForSchedule(Integer scheduleId);
    
    public Map<Season,Integer> getSeasonStrategyAssignments(int paoId);
    
    public void saveSeasonStrategyAssigment(int paoId, Map<Season,Integer> map, int scheduleId);
    
    public SeasonSchedule getScheduleForPao(int paoId);
    
}
