package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.database.model.Season;
import com.cannontech.database.db.season.SeasonSchedule;

public interface SeasonScheduleDao {

    List<Season> getSeasonsForSchedule(Integer scheduleId);
    
    List<Season> getUserFriendlySeasonsForSchedule(Integer scheduleId);
    
    Map<Season, Integer> fixSeasonMapForEndOfYearJump(Map<Season, Integer> map, List<Season> actualSeasons);
    
    Map<Season,Integer> getSeasonStrategyAssignments(int paoId);
    
    /**
     * Returns a LinkedHashMap of season to strategy id assigment.  The
     * order of insertion is the same as they are retrieved from the db.
     */
    Map<Season, Integer> getUserFriendlySeasonStrategyAssignments(int paoId);
    
    void saveSeasonStrategyAssigment(int paoId, Map<Season,Integer> map, int scheduleId);
    
    SeasonSchedule getScheduleForPao(int paoId);
    
    /**
     * Returns all schedules except the "Default Season Schedule".
     * Apparently no one cares about him.
     */
    List<SeasonSchedule> getAllSchedules();
    
    void deleteStrategyAssigment(int paoId);

}
