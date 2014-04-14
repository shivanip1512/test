package com.cannontech.cc.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

public class EventService {
    public class CurrentEventPredicate implements Predicate<BaseEvent> {
        private Date now;
        public CurrentEventPredicate() {
            now = new Date();
        }
        @Override
        public boolean evaluate(BaseEvent event) {
            
            if (event.getStopTime().before(now)) {
                return false;
            } else if (event.getStartTime().after(now)) {
                return false;
            } else if (!isConsideredActive(event)) {
                return false;
            } else {
                return true;
            }
        }
    }
    public class PendingEventPredicate implements Predicate<BaseEvent> {
        private Date now = new Date();
        @Override
        public boolean evaluate(BaseEvent event) {
            if (event.getStartTime().before(now)) {
                return false;
            } else if (!isConsideredActive(event)) {
                return false;
            } else {
                return true;
            }
        }
    }
    
    public class RecentEventPredicate implements Predicate<BaseEvent> {
        private Date now;
        private Date sixMonthsAgo;
        public RecentEventPredicate() {
            now = new Date();
            // get for last six months
            // used computer's TZ because the length of time is so long it won't matter
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.MONTH, -6);
            sixMonthsAgo = calendar.getTime();
        }
        @Override
        public boolean evaluate(BaseEvent event) {
            if (event.getStopTime().before(sixMonthsAgo)) {
                return false;
            } else if (event.getStopTime().after(now)) {
                return !isConsideredActive(event);
            }
                return true;
        }
    }
    
    private BaseEventDao baseEventDao;
    private StrategyFactory strategyFactory;
    
    public List<BaseEvent> getCurrentEventList(EnergyCompany energyCompany) {
        List<BaseEvent> allEvents = baseEventDao.getAllForEnergyCompany(energyCompany, 
                                                                        new CurrentEventPredicate());
        return allEvents;
    }
    
    public List<BaseEvent> getPendingEventList(EnergyCompany energyCompany) {
        List<BaseEvent> allEvents = baseEventDao.getAllForEnergyCompany(energyCompany, new PendingEventPredicate());
        return allEvents;
    }
    
    public List<BaseEvent> getRecentEventList(EnergyCompany energyCompany) {
        List<BaseEvent> allEvents = baseEventDao.getAllForEnergyCompany(energyCompany, new RecentEventPredicate());
        return allEvents;
    }
    
    public StrategyFactory getStrategyFactory() {
        return strategyFactory;
    }

    public boolean isConsideredActive(BaseEvent event) {
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(event.getProgram());
        
        return strategy.isConsideredActive(event);
    }
    
    public void forceDelete(BaseEvent event) {
        CICurtailmentStrategy strategy = 
            strategyFactory.getStrategy(event.getProgram());
        strategy.forceDelete(event);
    }

    public void setStrategyFactory(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public void setBaseEventDao(BaseEventDao baseEventDao) {
        this.baseEventDao = baseEventDao;
    }
    
}
