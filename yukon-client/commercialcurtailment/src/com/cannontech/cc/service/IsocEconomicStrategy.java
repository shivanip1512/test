package com.cannontech.cc.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.service.builder.EconomicBuilder;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.PointException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.customer.CICustomerPointType;

public class IsocEconomicStrategy extends BaseEconomicStrategy {
    private IsocCommonStrategy isocCommonStrategy;
    private AuthDao authDao;
    
    public IsocEconomicStrategy() {
        super();
    }


    @Override
    protected void 
    verifyCustomer(EventBuilderBase builder, 
                   VerifiedCustomer vCustomer) {
        EconomicBuilder myBuilder = (EconomicBuilder) builder;
        
        // check if participant has already been in an event today
        List<EconomicEventParticipant> allCustomersEvents = 
            getEconomicEventParticipantDao().getForCustomer(vCustomer.getCustomer());
        Date propossedEventDate = myBuilder.getEvent().getStartTime();
        Calendar calendar = Calendar.getInstance(myBuilder.getTimeZone());
        calendar.setTime(propossedEventDate);
        int propossedYear = calendar.get(Calendar.YEAR);
        int propossedDay = calendar.get(Calendar.DAY_OF_YEAR);
        for (EconomicEventParticipant participant : allCustomersEvents) {
            EconomicEvent event = participant.getEvent();
            Date startTime = event.getStartTime();
            calendar.setTime(startTime);
            if (calendar.get(Calendar.YEAR) == propossedYear
                && calendar.get(Calendar.DAY_OF_YEAR) == propossedDay
                && isocCommonStrategy.doEventsOverlap(myBuilder.getEvent(), event)) {
                String msg = "already in an economic event (" + event.getDisplayName() + ") that day";
                vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE, msg);
                break;
            }
        }
        
        
        isocCommonStrategy.checkEventCustomer(vCustomer, myBuilder.getEvent());
    }

    protected void checkPropossedSelections(EconomicEventParticipantSelection selection, 
                                            LiteYukonUser user, Date time) throws EventModificationException {
        TimeZone timeZone = authDao.getUserTimeZone(user);
        DateFormat dateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
        dateFormat.setTimeZone(timeZone);
        List<EconomicEventParticipantSelectionWindow> selectionWindows = selection.getSortedSelectionWindows();
        boolean curtailedLastWindow = false;
        for (EconomicEventParticipantSelectionWindow window : selectionWindows) {
            boolean curtailedThisWindow = isCurtailPrice(window.getEnergyToBuy());
            if (curtailedLastWindow && !curtailedThisWindow) {
                String date = dateFormat.format(window.getWindow().getStartTime());
                throw new EventModificationException("Illegal Buy-through kW; Once Interrupted (0kW) must stay interrupted: " 
                                                     + date);
            }
            curtailedLastWindow = curtailedThisWindow;
            if (!canPricingSelectionBeEdited(window, user, time)) {
                String date = dateFormat.format(window.getWindow().getStartTime());
                throw new EventModificationException("Prices cannont be entered for " + 
                                                     date + " at this time");
            }
        }
    }
    
    protected boolean canCustomerParticipateInExtension(EconomicBuilder builder,
                                                        CICustomerStub customer) {
        try {
            int duration = builder.getEvent().getDuration() / 60;
            boolean exceededAllowedHours = isocCommonStrategy.hasCustomerExceededAllowedHours(customer, duration);
            return !exceededAllowedHours;
        } catch (PointException e) {
            CTILogger.warn("Silently dropping " + customer + " from event extension because of point access error.", e);
            return false;
        }
    }
    
    public IsocCommonStrategy getIsocCommonStrategy() {
        return isocCommonStrategy;
    }

    public void setIsocCommonStrategy(IsocCommonStrategy isocCommonStrategy) {
        this.isocCommonStrategy = isocCommonStrategy;
    }
    
    public BigDecimal getCustomerElectionPrice(EconomicEventParticipant customer) throws PointException {
        return getPointValue(customer, CICustomerPointType.AdvBuyThrough$);
    }

    public BigDecimal getCustomerElectionBuyThrough(EconomicEventParticipant customer) throws PointException {
        return getPointValue(customer, CICustomerPointType.AdvBuyThroughKw);
    }

    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
}
