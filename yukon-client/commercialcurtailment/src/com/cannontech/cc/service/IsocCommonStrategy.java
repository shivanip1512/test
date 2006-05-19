package com.cannontech.cc.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.collections.iterators.ReverseListIterator;

import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.common.exception.PointException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.cache.functions.SimplePointAccess;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.support.CustomerPointTypeHelper;

public class IsocCommonStrategy extends StrategyGroupBase {
    private SimplePointAccess pointAccess;
    private CustomerPointTypeHelper pointTypeHelper;
    private BaseEventDao baseEventDao;

    public IsocCommonStrategy() {
        super();
    }

    @Override
    public String getRequiredPointGroup() {
        return "ISOC";
    }
    
    private int getTotalEventHours(CICustomerStub customer) {
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
        
        return getBaseEventDao().getTotalEventDuration(customer, getStrategyKeys(), from, to) / 60;
    }
    
    public boolean hasCustomerExceededAllowedHours(CICustomerStub customer, int propossedEventLength) throws PointException {
        LitePoint allowedHoursPoint = pointTypeHelper.getPoint(customer, CICustomerPointType.InterruptHours);
        int allowedHours = (int) pointAccess.getPointValue(allowedHoursPoint);
        // applies to current year
        int actualHours = getTotalEventHours(customer);
        return (actualHours + propossedEventLength) > allowedHours;
    }
    
    public BigDecimal getCurrentLoad(CICustomerStub customer) throws PointException {
        LitePoint point = pointTypeHelper.getPoint(customer, CICustomerPointType.CurrentLoad);
        double interruptLoad = pointAccess.getPointValue(point);
        
        return new BigDecimal(interruptLoad, new MathContext(0));
    }

    public void checkEventCustomer(VerifiedCustomer vCustomer, BaseEvent event) {
        CICustomerStub customer = vCustomer.getCustomer();
        List<BaseEvent> forCustomer = baseEventDao.getAllForCustomer(customer);
        //TODO reverse list here
        Iterator iterator = new ReverseListIterator(forCustomer);
        for (Iterator iter = new ReverseListIterator(forCustomer); iter.hasNext();) {
            BaseEvent otherEvent = (BaseEvent) iter.next();
            // rely on ordering from dao (reverse)
            if (otherEvent.getStopTime().before(event.getStartTime())) {
                // because of the event ordering, no more possible collisions exist
                break;
            }
            if (otherEvent.getStartTime().before(event.getStopTime())) {
                // we have a collision
                vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE, 
                "already in an event (" + otherEvent.getDisplayName() + ") at that time");
                break;
            }
            
        }
        try {
            int durationHours = event.getDuration() / 60;
            if (hasCustomerExceededAllowedHours(customer, durationHours)) {
                vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE, 
                                       "has exceeded allowed hours");
            } 
            
            int notifMinutes = TimeUtil.differenceMinutes(event.getNotificationTime(), event.getStartTime());
            if (isEventNoticeTooShort(customer, notifMinutes)) {
                vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE_OVERRIDABLE, 
                                       "requires longer notification time");
            }
        } catch (PointException e) {
            vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE, 
                                   "couldn't get point value (" + e.getMessage() + ")");
        }
    }
    
    private boolean isEventNoticeTooShort(CICustomerStub customer, int notifMinutes) throws PointException {
        LitePoint minimumNoticeMinutesPoint = pointTypeHelper.getPoint(customer, CICustomerPointType.MinimumNotice);
        int minimumNoticeMinutes = (int) pointAccess.getPointValue(minimumNoticeMinutesPoint);
        return notifMinutes < minimumNoticeMinutes;
    }

    public void setPointAccess(SimplePointAccess pointAccess) {
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
