package com.cannontech.cc.service;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.exception.NoPointException;
import com.cannontech.database.cache.functions.SimplePointAccess;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.support.CustomerPointTypeHelper;

public class IsocCommonStrategy extends StrategyGroupBase {
    private SimplePointAccess pointAccess;
    private CustomerPointTypeHelper pointTypeHelper;

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
    
    private boolean hasCustomerExceededAllowedHours(CICustomerStub customer, int propossedEventLength) throws NoPointException {
        LitePoint allowedHoursPoint = pointTypeHelper.getPoint(customer, "IsocMaxHours");
        int allowedHours = (int) pointAccess.getPointValue(allowedHoursPoint);
        // applies to current year
        int actualHours = getTotalEventHours(customer);
        return (actualHours + propossedEventLength) > allowedHours;
    }
    
    public void checkEventCustomer(VerifiedCustomer vCustomer, int durationHours, int notifMinutes) {
        CICustomerStub customer = vCustomer.getCustomer();
        try {
            if (hasCustomerExceededAllowedHours(customer, durationHours)) {
                vCustomer.setStatus(VerifiedCustomer.Status.EXCLUDE);
                vCustomer.setReasonForExclusion("has exceeded allowed hours");
            } else if (isEventNoticeTooShort(customer, notifMinutes)) {
                vCustomer.setStatus(VerifiedCustomer.Status.EXCLUDE_OVERRIDABLE);
                vCustomer.setReasonForExclusion("requires longer notification time");
            }
        } catch (NoPointException e) {
            vCustomer.setStatus(VerifiedCustomer.Status.EXCLUDE);
            vCustomer.setReasonForExclusion("does not have required points assigned (" + e.getPointType() + ")");
        }
    }
    
    private boolean isEventNoticeTooShort(CICustomerStub customer, int notifMinutes) throws NoPointException {
        LitePoint minimumNoticeMinutesPoint = pointTypeHelper.getPoint(customer, "IsocMinNoticeMin");
        int minimumNoticeMinutes = (int) pointAccess.getPointValue(minimumNoticeMinutesPoint);
        return notifMinutes < minimumNoticeMinutes;
    }

    public void setPointAccess(SimplePointAccess pointAccess) {
        this.pointAccess = pointAccess;
    }

    public void setPointTypeHelper(CustomerPointTypeHelper pointTypeHelper) {
        this.pointTypeHelper = pointTypeHelper;
    }

}
