package com.cannontech.web.dr.cc.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.AccountingEventDao;
import com.cannontech.cc.dao.AccountingEventParticipantDao;
import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventParticipantDao;
import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.dao.GroupCustomerNotifDao;
import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;
import com.cannontech.cc.model.CICustomerPointData;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.EconomicEventParticipantSelection;
import com.cannontech.cc.model.EconomicEventParticipantSelection.SelectionState;
import com.cannontech.cc.model.EconomicEventParticipantSelectionWindow;
import com.cannontech.cc.model.EconomicEventPricing;
import com.cannontech.cc.model.EconomicEventPricingWindow;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.CurtailmentEventAction;
import com.cannontech.cc.service.CurtailmentEventState;
import com.cannontech.cc.service.EconomicEventState;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.PointException;
import com.cannontech.core.dao.LMDirectCustomerListDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.loadcontrol.LoadManagementService;
import com.cannontech.web.dr.cc.model.CiInitEventModel;
import com.cannontech.web.dr.cc.service.CiEventCreationService;
import com.cannontech.yukon.INotifConnection;

public class CiEventCreationServiceImpl implements CiEventCreationService {
    
    private static final Logger log = YukonLogManager.getLogger(CiEventCreationServiceImpl.class);
    
    @Autowired AccountingEventDao accountingEventDao;
    @Autowired AccountingEventParticipantDao accountingEventParticipantDao;
    @Autowired CurtailmentEventDao curtailmentEventDao;
    @Autowired CurtailmentEventParticipantDao curtailmentEventParticipantDao;
    @Autowired EconomicEventDao economicEventDao;
    @Autowired GroupCustomerNotifDao groupCustomerNotifDao;
    @Autowired INotifConnection notifConnection;
    @Autowired LMDirectCustomerListDao lmDirectCustomerListDao;
    @Autowired LoadManagementService loadManagementService;
    @Autowired PointDao pointDao;
    @Autowired ProgramDao programDao;
    @Autowired ProgramService programService;
    @Autowired SimplePointAccessDao simplePointAccessDao;
    @Autowired EconomicEventParticipantDao economicEventParticipantDao;
    
    @Override
    @Transactional
    public int createEvent(CiInitEventModel event) throws EventCreationException {
        if (event.getEventType().isAccounting()) {
            return createAccountingEvent(event);
        } else if (event.getEventType().isNotification()) {
            return createCurtailmentEvent(event);
        } else if (event.getEventType().isEconomic()) {
            try {
                return createEconomicEvent(event);
            } catch (PointException e) {
                throw new EventCreationException("Error creating economic event.", e);
            }
        } else {
            throw new IllegalArgumentException("Event type not supported: " + event.getEventType());
        }
    }
    
    private int createAccountingEvent(CiInitEventModel event) {
        // Build accounting event object
        AccountingEvent accountingEvent = new AccountingEvent();
        accountingEvent.setDuration(event.getDuration());
        accountingEvent.setReason(event.getMessage());
        accountingEvent.setStartTime(event.getStartTime().toDate());
        Program program = programService.getProgramById(event.getProgramId());
        accountingEvent.setProgram(program);
        Integer identifier = programDao.incrementAndReturnIdentifier(program);
        accountingEvent.setIdentifier(identifier);
        
        // Save the event
        accountingEventDao.save(accountingEvent); 
        int eventId = accountingEvent.getId(); // Id is set during save
        
        // Save the participant customers for the event
        List<GroupCustomerNotif> customerNotifs = groupCustomerNotifDao.getByIds(event.getSelectedCustomerIds());
        for (GroupCustomerNotif notif : customerNotifs) {
            AccountingEventParticipant participant = new AccountingEventParticipant();
            participant.setCustomer(notif.getCustomer());
            participant.setEvent(accountingEvent);
            accountingEventParticipantDao.save(participant);
        }
        
        sendNotifications(event, program, identifier, customerNotifs);
        
        return eventId;
    }
    
    private int createCurtailmentEvent(CiInitEventModel event) {
        //Build Direct event object
        CurtailmentEvent curtailmentEvent = new CurtailmentEvent();
        curtailmentEvent.setState(CurtailmentEventState.INITIAL);
        curtailmentEvent.setDuration(event.getDuration());
        curtailmentEvent.setMessage(event.getMessage());
        curtailmentEvent.setStartTime(event.getStartTime().toDate());
        curtailmentEvent.setNotificationTime(event.getNotificationTime().toDate());
        Program program = programService.getProgramById(event.getProgramId());
        curtailmentEvent.setProgram(program);
        Integer identifier = programDao.incrementAndReturnIdentifier(program);
        curtailmentEvent.setIdentifier(identifier);
        
        // Save the event
        curtailmentEventDao.save(curtailmentEvent);
        int eventId = curtailmentEvent.getId();
        
        // Save the participant customers for the event
        List<GroupCustomerNotif> customerNotifs = groupCustomerNotifDao.getByIds(event.getSelectedCustomerIds());
        for (GroupCustomerNotif notif : customerNotifs) {
            CurtailmentEventParticipant participant = new CurtailmentEventParticipant();
            participant.setCustomer(notif.getCustomer());
            participant.setEvent(curtailmentEvent);
            participant.setNotifAttribs(notif.getAttribs());
            curtailmentEventParticipantDao.save(participant);
        }
        
        notifConnection.sendCurtailmentNotification(eventId, CurtailmentEventAction.STARTING);
        sendNotifications(event, program, identifier, customerNotifs);
        
        // "Direct" events message load management to start LM programs
        if (event.getEventType().isDirect()) {
            for (GroupCustomerNotif notif : customerNotifs) {
                CICustomerStub customer = notif.getCustomer();
                Set<Integer> lmProgramIds = lmDirectCustomerListDao.getLMProgramIdsForCustomer(customer.getId());
                for (Integer programId : lmProgramIds) {
                    Date startTime = curtailmentEvent.getStartTime();
                    Date stopTime = curtailmentEvent.getStopTime();
                    log.debug("Sending startProgram for event: " + curtailmentEvent + ", lmProgramId=" + programId + 
                              ", startTime=" + startTime + ", stopTime=" + stopTime);
                    loadManagementService.startProgram(programId, startTime, stopTime);
                }
            }
        }
        
        return eventId;
    }
    
    private int createEconomicEvent(CiInitEventModel event) throws PointException {
        Date creationTime = new Date();
        
        // Build economic event
        EconomicEvent economicEvent = new EconomicEvent();
        economicEvent.setStartTime(event.getStartTime().toDate());
        economicEvent.setNotificationTime(event.getNotificationTime().toDate());
        economicEvent.setState(EconomicEventState.INITIAL);
        economicEvent.setWindowLengthMinutes(60);
        Program program = programService.getProgramById(event.getProgramId());
        economicEvent.setProgram(program);
        Integer identifier = programDao.incrementAndReturnIdentifier(program);
        economicEvent.setIdentifier(identifier);
        
        // Create the revision
        EconomicEventPricing revision = new EconomicEventPricing();
        revision.setRevision(1);
        revision.setCreationTime(creationTime);
        
        // Add pricing windows to the revision
        int numberOfWindows = event.getNumberOfWindows();
        List<EconomicEventPricingWindow> windows = new ArrayList<>(numberOfWindows);
        for (int i = 0; i < numberOfWindows; i++) {
            EconomicEventPricingWindow window = new EconomicEventPricingWindow();
            window.setPricingRevision(revision);
            window.setEnergyPrice(event.getWindowPrices().get(i));
            window.setOffset(i);
            windows.add(window);
        }
        revision.setWindows(windows);
        economicEvent.addRevision(revision);
        
        // Save the event
        economicEventDao.save(economicEvent);
        int eventId = economicEvent.getId();
        
        // Save the participant customers for the event
        List<GroupCustomerNotif> customerNotifs = groupCustomerNotifDao.getByIds(event.getSelectedCustomerIds());
        for (GroupCustomerNotif notif : customerNotifs) {
            EconomicEventParticipant participant = new EconomicEventParticipant();
            participant.setCustomer(notif.getCustomer());
            participant.setEvent(economicEvent);
            participant.setNotifAttribs(notif.getAttribs());
            
            // create "default" selections for first revision
            EconomicEventParticipantSelection initialSelection = new EconomicEventParticipantSelection();
            initialSelection.setPricingRevision(revision);
            initialSelection.setSubmitTime(creationTime);
            initialSelection.setState(SelectionState.DEFAULT);
            initialSelection.setConnectionAudit("default entries");
            participant.addSelection(initialSelection);
            
            BigDecimal lastHourBuy = null;
            for (EconomicEventPricingWindow window : windows) {
                BigDecimal energyPrice = window.getEnergyPrice();
                BigDecimal buyThroughPrice = getCustomerElectionPrice(participant);
                BigDecimal energyToBuy = null;
                if (lastHourBuy != null && isCurtailPrice(lastHourBuy)) {
                    // you curtailed last hour, you shall curtail this hour
                    energyToBuy = BigDecimal.ZERO;
                } else if (energyPrice.compareTo(buyThroughPrice) > 0) {
                    // curtail
                    energyToBuy = BigDecimal.ZERO;
                } else {
                    // buy through
                    energyToBuy = getCustomerElectionBuyThrough(participant);
                }
                EconomicEventParticipantSelectionWindow selectionWindow = new EconomicEventParticipantSelectionWindow();
                selectionWindow.setEnergyToBuy(energyToBuy);
                selectionWindow.setWindow(window);
                initialSelection.addWindow(selectionWindow);

                lastHourBuy = energyToBuy;
            }
            economicEventParticipantDao.save(participant);
        }
        
        return eventId;
    }
    
    /**
     * @see http://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html#compareTo(java.math.BigDecimal)
     */
    private boolean isCurtailPrice(BigDecimal buyThrough) {
        return buyThrough.compareTo(BigDecimal.ZERO) == 0;
    }
    
    private BigDecimal getCustomerElectionPrice(EconomicEventParticipant customer) throws PointException {
        return getPointValue(customer, CICustomerPointType.AdvBuyThrough$);
    }

    private BigDecimal getCustomerElectionBuyThrough(EconomicEventParticipant customer) throws PointException {
        return getPointValue(customer, CICustomerPointType.AdvBuyThroughKw);
    }
    
    private BigDecimal getPointValue(EconomicEventParticipant customer, CICustomerPointType type) throws PointException {
        CICustomerPointData data = customer.getCustomer().getPointData().get(type);
        Validate.notNull(data, "Customer " + customer.getCustomer() + " does not have a point for " + type);
        LitePoint litePoint = pointDao.getLitePoint(data.getPointId());
        double pointValue = simplePointAccessDao.getPointValue(litePoint);
        // It is now somewhat important to round our double. I'm guessing that
        // ten digits of precision is a good number. This will ensure that
        // ==, <=, and >= operations on "normal" numbers will work as expected.
        return new BigDecimal(pointValue, new MathContext(10));
    }
    
    private void sendNotifications(CiInitEventModel event, Program program, Integer programIdentifier, 
                                   List<GroupCustomerNotif> customerNotifs) {
        // Get notification info
        int[] customerIds = new int[customerNotifs.size()];
        for (int i = 0; i < customerNotifs.size(); i++) {
            customerIds[i] = customerNotifs.get(i).getCustomer().getId();
        }
        String eventDisplayName = program.getIdentifierPrefix() + programIdentifier;
        DateTime stopTime = event.getStartTime().plus(Duration.standardMinutes(event.getDuration()));
        
        // Send event notifications
        notifConnection.sendProgramEventNotification(event.getProgramId(), 
                                                     eventDisplayName, 
                                                     "started", 
                                                     event.getStartTime().toDate(), 
                                                     stopTime.toDate(), 
                                                     event.getNotificationTime().toDate(), 
                                                     customerIds);
    }
}
