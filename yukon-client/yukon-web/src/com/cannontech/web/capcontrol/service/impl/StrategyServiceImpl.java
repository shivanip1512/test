package com.cannontech.web.capcontrol.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.capcontrol.service.StrategyService;

@Service
public class StrategyServiceImpl implements StrategyService {
    
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private StrategyDao strategyDao;

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

}
