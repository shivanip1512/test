package com.cannontech.cc.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.database.data.lite.LiteEnergyCompany;

/**
 * This class attempts to abstract some operations that affect all events (i.e. BaseEvents).
 * For the time being, the DB knows nothing about BaseEvent. Many of these operations could
 * probably be sped up if that was changed.
 */
public class BaseEventDao implements CommonEventOperations {
    private Set<CommonEventOperations> childDaos = new TreeSet<CommonEventOperations>();

    public BaseEventDao() {
    }
    
    public List<BaseEvent> getAllForEnergyCompany(LiteEnergyCompany energyCompany) {
        List<BaseEvent> allEvents = new ArrayList<BaseEvent>(100);
        for (CommonEventOperations dao : childDaos) {
            List<? extends BaseEvent> events = dao.getAllForEnergyCompany(energyCompany);
            allEvents.addAll(events);
        }
        Collections.sort(allEvents);
        return allEvents;
    }
    
    /**
     * Returns all events for a given customer.
     * Events are sorted chronological order.
     */
    public List<BaseEvent> getAllForCustomer(CICustomerStub customer) {
        List<BaseEvent> allEvents = new ArrayList<BaseEvent>(100);
        for (CommonEventOperations dao : childDaos) {
            List<? extends BaseEvent> events = dao.getAllForCustomer(customer);
            allEvents.addAll(events);
        }
        Collections.sort(allEvents);
        return allEvents;
    }
    
    public List<BaseEvent> getRecentEvents(CICustomerStub customer) {
        List<BaseEvent> allForCustomer = getAllForCustomer(customer);
        filterForRecent(allForCustomer);
        return allForCustomer;
    }
    
    public List<BaseEvent> getCurrentEvents(CICustomerStub customer) {
        List<BaseEvent> allForCustomer = getAllForCustomer(customer);
        filterForCurrent(allForCustomer);
        return allForCustomer;
    }
    
    public List<BaseEvent> getPendingEvents(CICustomerStub customer) {
        List<BaseEvent> allForCustomer = getAllForCustomer(customer);
        filterForPending(allForCustomer);
        return allForCustomer;
    }
    
    

    public List<BaseEvent> getRecentEvents(LiteEnergyCompany energyCompany) {
        List<BaseEvent> allEvents = getAllForEnergyCompany(energyCompany);
        filterForRecent(allEvents);
        
        return allEvents;  // which have now been filtered
    }

    private void filterForRecent(List<BaseEvent> allEvents) {
        Date now = new Date();
        // get for last six months
        // used computer's TZ because the length of time is so long it won't matter
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MONTH, -6);
        Date sixMonthsAgo = calendar.getTime();
        
        for (Iterator iter = allEvents.iterator(); iter.hasNext();) {
            BaseEvent event = (BaseEvent) iter.next();
            if (event.getStopTime().before(sixMonthsAgo)) {
                iter.remove();
            } else if (event.getStopTime().after(now)) {
                iter.remove();
            }
        }
    }

    public List<BaseEvent> getCurrentEvents(LiteEnergyCompany energyCompany) {
        List<BaseEvent> allEvents = getAllForEnergyCompany(energyCompany);
        filterForCurrent(allEvents);
        
        return allEvents;  // which have now been filtered
    }

    private void filterForCurrent(List<BaseEvent> allEvents) {
        Date now = new Date();
        
        for (Iterator iter = allEvents.iterator(); iter.hasNext();) {
            BaseEvent event = (BaseEvent) iter.next();
            if (event.getStopTime().before(now)) {
                iter.remove();
            } else if (event.getStartTime().after(now)) {
                iter.remove();
            }
        }
    }

    public List<BaseEvent> getPendingEvents(LiteEnergyCompany energyCompany) {
        List<BaseEvent> allEvents = getAllForEnergyCompany(energyCompany);
        filterForPending(allEvents);
        
        return allEvents;  // which have now been filtered
    }

    private void filterForPending(List<BaseEvent> allEvents) {
        Date now = new Date();
        
        for (Iterator iter = allEvents.iterator(); iter.hasNext();) {
            BaseEvent event = (BaseEvent) iter.next();
            if (event.getStartTime().before(now)) {
                iter.remove();
            }
        }
    }
    
    /**
     * @param customer
     * @param strategies
     * @param from
     * @param to
     * @return total durration in minutes
     */
    public int getTotalEventDuration(CICustomerStub customer, Set<String> strategies, Date from, Date to) {
        List<BaseEvent> allEvents = getAllForCustomer(customer);
        int totalDuration = 0;
        for (BaseEvent event : allEvents) {
            String strategy = event.getProgram().getProgramType().getStrategy();
            
            if (strategies.contains(strategy) 
                && event.getStartTime().after(from) 
                && event.getStopTime().before(to)) {
                totalDuration += event.getDuration();
            }
        }
        return totalDuration;
    }

    

    // setters for dependency injection
    public void setChildDaos(Set<CommonEventOperations> childDaos) {
        this.childDaos = childDaos;
    }
    
}
