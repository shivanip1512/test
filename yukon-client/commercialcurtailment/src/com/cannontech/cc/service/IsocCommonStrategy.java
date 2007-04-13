package com.cannontech.cc.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.collections.iterators.ReverseListIterator;

import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.enums.CurtailmentEventState;
import com.cannontech.cc.service.enums.EconomicEventState;
import com.cannontech.common.exception.PointException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.support.CustomerPointTypeHelper;

public class IsocCommonStrategy extends StrategyGroupBase {
    private SimplePointAccessDao pointAccess;
    private CustomerPointTypeHelper pointTypeHelper;
    private BaseEventDao baseEventDao;

    private final class StopTimeComparator implements Comparator<BaseEvent> {
        public int compare(BaseEvent o1, BaseEvent o2) {
            return o1.getStopTime().compareTo(o2.getStopTime());
        }
    }
    private final Comparator<BaseEvent> stopTimeComparatot = new StopTimeComparator();
    private final List<EconomicEventState> excludedEconStates = Arrays.asList(EconomicEventState.CANCELLED, EconomicEventState.SUPPRESSED);
    private final List<CurtailmentEventState> excludedCurtailmentStates = Arrays.asList(CurtailmentEventState.CANCELLED);

    public IsocCommonStrategy() {
        super();
    }

    private double getTotalEventHours(CICustomerStub customer) {
        //get first day this year
        Date now = new Date();
        TimeZone timeZone = TimeZone.getTimeZone(customer.getLite().getTimeZone());
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(now);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date from = cal.getTime();
        cal.add(Calendar.YEAR, 1);
        Date to = cal.getTime();
        
        return getTotalEventHours(customer, from, to);
    }

    public double getTotalEventHours(CICustomerStub customer, Date from, Date to) {
        List<BaseEvent> allEvents = getBaseEventDao().getAllForCustomer(customer, from, to);
        int totalMinutes = 0;
        for (BaseEvent event : allEvents) {
            
            if (doesEventContributeToAllowedHours(event)) {
                totalMinutes += event.getDuration();
            }
        }
        double totalHours = (double)totalMinutes / 60;
        return totalHours;
    }
    
    public boolean hasCustomerExceededAllowedHours(CICustomerStub customer, int propossedEventLength) throws PointException {
        double allowedHours = getAllowedHours(customer);
        // applies to current year
        double actualHours = getTotalEventHours(customer);
        return (actualHours + propossedEventLength) > allowedHours;
    }

    public double getAllowedHours(CICustomerStub customer) throws PointException {
        double allowedHours = pointTypeHelper.getPointValue(customer, CICustomerPointType.InterruptHours);
        return allowedHours;
    }
    
    public void checkEventCustomer(VerifiedCustomer vCustomer, BaseEvent event) {
        try {
            checkRequiredPoints(vCustomer);
            checkEventOverlap(vCustomer, event);
            checkAllowedHours(vCustomer, event); 
            checkNoticeTime(vCustomer, event);
        } catch (PointException e) {
            handlePointException(vCustomer, e);
        }
    }

    private void checkRequiredPoints(VerifiedCustomer vCustomer) {
        boolean pointSatisfied = 
            pointTypeHelper.isPointGroupSatisfied(vCustomer.getCustomer(), getRequiredPointGroup());
        if (!pointSatisfied) {
            vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE, "all 'ISOC' points do not exist");
        }
    }

    public void checkEventOverlap(VerifiedCustomer vCustomer, BaseEvent event) {
        List<BaseEvent> forCustomer = baseEventDao.getAllForCustomer(vCustomer.getCustomer());
        Collections.sort(forCustomer, stopTimeComparatot);
        for (Iterator iter = new ReverseListIterator(forCustomer); iter.hasNext();) {
            BaseEvent otherEvent = (BaseEvent) iter.next();
            // rely on ordering to short circuit
            if (!otherEvent.getStopTime().after(event.getStartTime())) {
                // don't use before because stop and start could be equal
                // because of the event ordering, no more possible collisions exist
                break;
            }
            Date otherStart = otherEvent.getStartTime();
            Date thisStop = event.getStopTime();
            if (doEventsOverlap(event, otherEvent)
                && otherStart.before(thisStop)) {
                // we have a collision
                vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE, 
                "already in an event (" + otherEvent.getDisplayName() + ") at that time");
                break;
            }
            
        }
    }
    
    /**
     * The idea is to check if they COULD overlap (based on their state). This 
     * method shouldn't check the times.
     * @param propossedEvent
     * @param existingEvent
     * @return
     */
    public boolean doEventsOverlap(BaseEvent propossedEvent, BaseEvent existingEvent) {
        // for this implementation, the propossedEvent doesn't matter and we can just
        // delegate to the following method which checks the same thing
        return doesEventContributeToAllowedHours(existingEvent);
    }
    
    public boolean doesEventContributeToAllowedHours(BaseEvent event) {
        String strategy = event.getProgram().getProgramType().getStrategy();
        if (!getStrategyKeys().contains(strategy)) {
            return false;
        }
        if (event instanceof EconomicEvent) {
            EconomicEvent existingEventEcon = (EconomicEvent) event;
            if (excludedEconStates.contains(existingEventEcon.getState())) {
                return false;
            }
        } else if (event instanceof CurtailmentEvent) {
            CurtailmentEvent existingEventCurt = (CurtailmentEvent) event;
            if (excludedCurtailmentStates.contains(existingEventCurt.getState())) {
                return false;
            }
        }
        return true;
    }

    public void checkNoticeTime(VerifiedCustomer vCustomer, BaseEvent event) throws PointException {
        int notifMinutes = TimeUtil.differenceMinutes(event.getNotificationTime(), event.getStartTime());
        if (isEventNoticeTooShort(vCustomer.getCustomer(), notifMinutes)) {
            vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE_OVERRIDABLE, 
                                   "requires longer notification time");
        }
    }

    public void checkAllowedHours(VerifiedCustomer vCustomer, BaseEvent event) throws PointException {
        int durationHours = event.getDuration() / 60;
        if (hasCustomerExceededAllowedHours(vCustomer.getCustomer(), durationHours)) {
            vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE, 
                                   "has exceeded allowed hours");
        }
    }

    public void handlePointException(VerifiedCustomer vCustomer, PointException e) {
        vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE, 
                               "couldn't get point value (" + e.getMessage() + ")");
    }
    
    private boolean isEventNoticeTooShort(CICustomerStub customer, int notifMinutes) throws PointException {
        LitePoint minimumNoticeMinutesPoint = pointTypeHelper.getPoint(customer, CICustomerPointType.MinimumNotice);
        int minimumNoticeMinutes = (int) pointAccess.getPointValue(minimumNoticeMinutesPoint);
        return notifMinutes < minimumNoticeMinutes;
    }

    public void setPointAccess(SimplePointAccessDao pointAccess) {
        this.pointAccess = pointAccess;
    }

    public void setPointTypeHelper(CustomerPointTypeHelper pointTypeHelper) {
        this.pointTypeHelper = pointTypeHelper;
    }

    public BaseEventDao getBaseEventDao() {
        return baseEventDao;
    }

    public void setBaseEventDao(BaseEventDao baseEventDao) {
        this.baseEventDao = baseEventDao;
    }

}
