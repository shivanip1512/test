package com.cannontech.web.capcontrol.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.model.Season;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.service.StrategyService;

@Service
public class StrategyServiceImpl implements StrategyService {
    
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private StrategyDao strategyDao;
    @Autowired private SeasonScheduleDao seasonSchedules;

    @Override
    public int save(CapControlStrategy strategy) {
        
        DbChangeType changeType = DbChangeType.UPDATE;

        if (strategy.getId() == null) {
            int newId = strategyDao.add(strategy.getName());
            
            strategy.setId(newId);
            changeType = DbChangeType.ADD;
        }
        
        strategyDao.save(strategy);
        
        DBChangeMsg dbChange = new DBChangeMsg(
            strategy.getId(),
            DBChangeMsg.CHANGE_CBC_STRATEGY_DB,
            DBChangeMsg.CAT_CBC_STRATEGY,
            changeType);
        
        dbChangeManager.processDbChange(dbChange);
        
        return strategy.getId();
    }
    
    @Override
    public void delete(int id) {
        strategyDao.delete(id);
        
        DBChangeMsg dbChange = new DBChangeMsg(
            id,
            DBChangeMsg.CHANGE_CBC_STRATEGY_DB,
            DBChangeMsg.CAT_CBC_STRATEGY,
            DbChangeType.DELETE);
        
        dbChangeManager.processDbChange(dbChange);
    }
    
    @Override
    public List<String> getAllPaoNamesUsingStrategyAssignment(int id) {
        return strategyDao.getAllPaoNamesUsingStrategyAssignment(id);
    }
    
    @Override
    public Map<Season, LiteCapControlStrategy> getSeasonStrategyAssignments(int id) {
        
        Map<Season, Integer> seasonToStratId = seasonSchedules.getUserFriendlySeasonStrategyAssignments(id);
        Map<Integer, LiteCapControlStrategy> idToStrat = strategyDao.getLiteStrategies(seasonToStratId.values());
        
        Map<Season, LiteCapControlStrategy> seasonToStrat = new LinkedHashMap<>();
        
        for (Season season : seasonToStratId.keySet()) {
            Integer strategyId = seasonToStratId.get(season);
            if (strategyId == -1) {
                seasonToStrat.put(season, null);
            } else {
                LiteCapControlStrategy strategy = idToStrat.get(strategyId);
                seasonToStrat.put(season, strategy);
            }
        }
        
        return seasonToStrat;
    }

}
