package com.cannontech.cc.service;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.builder.EconomicBuilder;
import com.cannontech.cc.service.NotificationStatus;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

public interface EconomicStrategy extends CICurtailmentStrategy {

    public EconomicBuilder createBuilder(Program program, YukonUserContext yukonUserContext);

    public EconomicBuilder createExtensionBuilder(EconomicEvent previous, YukonUserContext yukonUserContext);

    public int getWindowLengthMinutes();

    public void verifyTimes(EconomicBuilder builder) throws EventCreationException;

    public void verifyPrices(EconomicBuilder builder) throws EventCreationException;

    public EconomicEvent createEvent(final EconomicBuilder builder) throws EventCreationException;

    public Boolean canEventBeCancelled(EconomicEvent event, LiteYukonUser user);

    public Boolean canEventBeDeleted(EconomicEvent event, LiteYukonUser user);

    public Boolean canEventBeSuppressed(EconomicEvent event, LiteYukonUser user);

    public Boolean canEventBeExtended(EconomicEvent event, LiteYukonUser user);

    public Boolean canEventBeRevised(EconomicEvent event, LiteYukonUser user);

    public EconomicEventPricing createEventRevision(EconomicEvent event, LiteYukonUser yukonUser)
        throws EventModificationException;

    @Transactional
    public void saveRevision(EconomicEventPricing nextRevision);

    public void saveParticipantSelection(EconomicEventParticipantSelection selection,
                                         LiteYukonUser user) throws EventModificationException;

    @Transactional
    public void deleteEvent(EconomicEvent event, LiteYukonUser user)
        throws EventModificationException;

    @Transactional
    public void cancelEvent(EconomicEvent event, LiteYukonUser user);

    @Transactional
    public void suppressEvent(EconomicEvent event, LiteYukonUser user);

    public boolean canPricingSelectionBeEdited(
                                               EconomicEventParticipantSelectionWindow selectionWindow,
                                               LiteYukonUser user, Date time);

    public List<EconomicEventParticipant> getRevisionParticipantForNotif(
                                                                         EconomicEventPricing eventRevision);

    public NotificationStatus getNotificationSuccessStatus(EconomicEventParticipant participant);
    
    public void setupPriceList(EconomicBuilder builder);
    
    public void setupExtensionCustomers(EconomicBuilder builder);
    
    public void setupCustomers(EconomicBuilder builder);
    
    public boolean isBeforeElectionCutoff(EconomicEventPricing revision, Date time);
}