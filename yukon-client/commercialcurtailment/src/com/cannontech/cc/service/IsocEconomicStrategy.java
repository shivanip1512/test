package com.cannontech.cc.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.service.builder.EconomicBuilder;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.enums.IsocPointTypes;
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.cc.service.exception.NoPointException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.data.lite.LiteYukonUser;

public class IsocEconomicStrategy extends BaseEconomicStrategy {
    IsocCommonStrategy isocCommonStrategy;
    
    public IsocEconomicStrategy() {
        super();
    }


    @Override
    protected void 
    verifyCustomer(EventBuilderBase builder, 
                   VerifiedCustomer vCustomer) {
        EconomicBuilder myBuilder = (EconomicBuilder) builder;
        
        int durationHours = (myBuilder.getNumberOfWindows() * myBuilder.getEvent().getWindowLengthMinutes()) / 60;
        Date notifTime = myBuilder.getEvent().getNotificationTime();
        Date startTime = myBuilder.getEvent().getStartTime();
        int notifMinutes = TimeUtil.differenceMinutes(notifTime, startTime);
        
        isocCommonStrategy.checkEventCustomer(vCustomer, durationHours, notifMinutes);
    }

    protected void checkPropossedSelections(EconomicEventParticipantSelection selection, 
                                            LiteYukonUser user, Date time) throws EventModificationException {
        DateFormat dateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
        List<EconomicEventParticipantSelectionWindow> selectionWindows = selection.getSelectionWindows();
        boolean curtailedLastWindow = false;
        for (EconomicEventParticipantSelectionWindow window : selectionWindows) {
            boolean curtailedThisWindow = isCurtailPrice(window.getEnergyToBuy());
            if (curtailedLastWindow && !curtailedThisWindow) {
                String date = dateFormat.format(window.getWindow().getStartTime());
                throw new EventModificationException("Must curtail for all segments after " 
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
    
    @Override
    public BigDecimal getInterruptibleLoad(CICustomerStub customer) throws NoPointException {
        return isocCommonStrategy.getInterruptibleLoad(customer);
    }
    
    public IsocCommonStrategy getIsocCommonStrategy() {
        return isocCommonStrategy;
    }

    public void setIsocCommonStrategy(IsocCommonStrategy isocCommonStrategy) {
        this.isocCommonStrategy = isocCommonStrategy;
    }


    public BigDecimal getCustomerElectionPrice(EconomicEventParticipant customer) {
        return getPointValue(customer, IsocPointTypes.AdvBuyThrough$.name());
    }


    public BigDecimal getCustomerElectionBuyThrough(EconomicEventParticipant customer) {
        return getPointValue(customer, IsocPointTypes.AdvBuyThroughKw.name());
    }

}
