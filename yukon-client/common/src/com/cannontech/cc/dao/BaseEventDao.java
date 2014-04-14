package com.cannontech.cc.dao;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Program;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.google.common.collect.Sets;

/**
 * This class attempts to abstract some operations that affect all events (i.e. BaseEvents).
 * For the time being, the DB knows nothing about BaseEvent. Many of these operations could
 * probably be sped up if that was changed.
 */
public class BaseEventDao implements CommonEventOperations<BaseEvent> {
    private Set<CommonEventOperations<?>> childDaos = Sets.newHashSet();

    public BaseEventDao() {
    }
    
    public List<BaseEvent> getAllForEnergyCompany(EnergyCompany energyCompany, Predicate<BaseEvent> predicate) {
        List<BaseEvent> allEvents = new LinkedList<BaseEvent>();
        for (CommonEventOperations<?> dao : childDaos) {
            List<? extends BaseEvent> events = dao.getAllForEnergyCompany(energyCompany);
            for (BaseEvent event : events) {
                if (predicate.evaluate(event)) {
                    allEvents.add(event);
                }
            }
        }
        Collections.sort(allEvents);
        return allEvents;
    }
    
    @Override
    public List<BaseEvent> getAllForEnergyCompany(EnergyCompany energyCompany) {
        return getAllForEnergyCompany(energyCompany, new Predicate<BaseEvent>() {
            @Override
            public boolean evaluate(BaseEvent object) {
                return true;
            }
        });
    }
    
    /**
     * Returns all events for a given customer.
     * Events are sorted chronological order (by start time).
     */
    public List<BaseEvent> getAllForCustomer(CICustomerStub customer, Predicate<BaseEvent> predicate) {
        // uses a LinkedList because the result is often filtered by
        // removing unwanted elements
        List<BaseEvent> allEvents = new LinkedList<BaseEvent>();
        for (CommonEventOperations<?> dao : childDaos) {
            List<? extends BaseEvent> events = dao.getAllForCustomer(customer);
            for (BaseEvent event : events) {
                if (predicate.evaluate(event)) {
                    allEvents.add(event);
                }
            }
        }
        Collections.sort(allEvents);
        return allEvents;
    }
    
    @Override
    public List<BaseEvent> getAllForCustomer(CICustomerStub customer) {
        return getAllForCustomer(customer, new Predicate<BaseEvent>() {
            @Override
            public boolean evaluate(BaseEvent object) {
                return true;
            }
        });
    }
    
    @Override
    public List<BaseEvent> getAllForProgram(Program program) {
        List<BaseEvent> allEvents = new LinkedList<BaseEvent>();
        for (CommonEventOperations<?> dao : childDaos) {
            List<? extends BaseEvent> events = dao.getAllForProgram(program);
            allEvents.addAll(events);
        }
        return allEvents;
    }

    /**
     * Get all Events for a given customer between the supplied dates.
     * Includes all events that have a startTime OR stopTime between the supplied dates.
     * @param customer
     * @param from (inclusive)
     * @param to (inclusive)
     * @return
     */
    public List<BaseEvent> getAllForCustomerStartsOrStopsWithin(CICustomerStub customer, Date from, Date to) {
        List<BaseEvent> allEvents = getAllForCustomer(customer);
        for (Iterator<BaseEvent> iter = allEvents.iterator(); iter.hasNext();) {
            BaseEvent event = iter.next();

            if (event.getStopTime().before(from) || event.getStartTime().after(to)) {
            	iter.remove();
            }
        }
        return allEvents;  // which have now been filtered
    }
    /**
     * Get all Events for a given customer between the supplied dates.
     * Includes all events that have a stopTime between the supplied dates.  The startTime is ignored.
     * @param customer
     * @param from (exclusive for stopTime)
     * @param to (inclusive for stopTime)
     * @return
     */
    public List<BaseEvent> getAllForCustomerStopsWithin(CICustomerStub customer, Date from, Date to) {
        List<BaseEvent> allEvents = getAllForCustomer(customer);
        for (Iterator<BaseEvent> iter = allEvents.iterator(); iter.hasNext();) {
            BaseEvent event = iter.next();

            if (!event.getStopTime().after(from) || event.getStopTime().after(to)) {
            	iter.remove();
            }
        }
        return allEvents;  // which have now been filtered
    }
    // setters for dependency injection
    
    public void setChildDaos(Set<CommonEventOperations<?>> childDaos) {
        this.childDaos = childDaos;
    }

    
}
