package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.database.model.Season;
import com.cannontech.database.db.season.SeasonSchedule;

public interface SeasonScheduleDao {

    public List<Season> getSeasonsForSchedule(Integer scheduleId);
    
    public List<Season> getUserFriendlySeasonsForSchedule(Integer scheduleId);
    
    public Map<Season, Integer> fixSeasonMapForEndOfYearJump(Map<Season, Integer> map, List<Season> actualSeasons);
    
    public Map<Season,Integer> getSeasonStrategyAssignments(int paoId);
    
    public Map<Season, Integer> getUserFriendlySeasonStrategyAssignments(int paoId);
    
    public void saveSeasonStrategyAssigment(int paoId, Map<Season,Integer> map, int scheduleId);
    
    public SeasonSchedule getScheduleForPao(int paoId);
    
    public void saveDefaultSeasonStrategyAssigment(int paoId);
    
    public void deleteStrategyAssigment(int paoId);

}
