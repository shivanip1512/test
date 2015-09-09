package com.cannontech.web.dr.cc.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import com.cannontech.cc.model.BaseParticipant;
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
import com.cannontech.cc.service.EconomicEventAction;
import com.cannontech.cc.service.EconomicEventState;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.PointException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.LMDirectCustomerListDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.database.db.web.LMDirectCustomerList;
import com.cannontech.loadcontrol.LoadManagementService;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.web.dr.cc.model.CiEventType;
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
    public int createEvent(CiInitEventModel event) throws EventCreationException, ConnectionException {
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
    
    @Override
    @Transactional
    public void adjustEvent(CurtailmentEvent originalEvent, CiInitEventModel newEventParams) {
        originalEvent.setDuration(newEventParams.getDuration());
        originalEvent.setMessage(newEventParams.getMessage());
        originalEvent.setState(CurtailmentEventState.MODIFIED);
        curtailmentEventDao.save(originalEvent);
        
        notifConnection.sendCurtailmentNotification(originalEvent.getId(), CurtailmentEventAction.ADJUSTING);
        
        List<CurtailmentEventParticipant> participants = curtailmentEventParticipantDao.getForEvent(originalEvent);
        int customerIds[] = new int[participants.size()];
        int index = 0;
        for (BaseParticipant participant : participants) {
            customerIds[index++] = participant.getCustomer().getId();
        }
        notifConnection.sendProgramEventNotification(originalEvent.getProgram().getId(), 
                                                     originalEvent.getDisplayName(), 
                                                     "adjusted", 
                                                     originalEvent.getStartTime(), 
                                                     originalEvent.getStopTime(), 
                                                     originalEvent.getNotificationTime(), 
                                                     customerIds);
        
        if (newEventParams.getEventType().isDirect()) {
            for (CurtailmentEventParticipant participant : participants) {
                CICustomerStub customer = participant.getCustomer();
                long[] lmProgramIds = LMDirectCustomerList.getProgramIDs(customer.getId());
                for (int i = 0; i < lmProgramIds.length; i++) {
                    long lmProgramId = lmProgramIds[i];
                    CTILogger.debug("Sending changeProgramStop for event: " + originalEvent);
                    CTILogger.debug("  lmProgramId=" + lmProgramId + ", stopTime=" + originalEvent.getStopTime());
                    loadManagementService.changeProgramStop((int)lmProgramId, originalEvent.getStopTime());
                }
            }
        }
    }
    
    @Override
    @Transactional
    public int splitEvent(CurtailmentEvent originalEvent, List<CICustomerStub> customersToRemove) {
        
        // Update original event
        originalEvent.setState(CurtailmentEventState.MODIFIED);
        curtailmentEventDao.save(originalEvent);
        
        // Create new event
        CurtailmentEvent splitEvent = new CurtailmentEvent();
        Integer identifier = programDao.incrementAndReturnIdentifier(originalEvent.getProgram());
        splitEvent.setIdentifier(identifier);
        
        // Copy fields from original event
        splitEvent.setProgram(originalEvent.getProgram());
        splitEvent.setMessage(originalEvent.getMessage() + "[Split-OrigId:" + originalEvent.getIdentifier() + "]");
        splitEvent.setNotificationTime(originalEvent.getNotificationTime());
        splitEvent.setStartTime(originalEvent.getStartTime());
        
        // Set duration to now - start
        int splitDuration = TimeUtil.differenceMinutes(originalEvent.getStartTime(), new Date());
        splitEvent.setDuration(splitDuration);
        
        // Set state
        splitEvent.setState(CurtailmentEventState.SPLIT);
        
        // Save new event (must happen before we can add participants)
        curtailmentEventDao.save(splitEvent);
        
        // Remove customers from original event, add to split event
        List<CurtailmentEventParticipant> allOrigEventParticipants = curtailmentEventParticipantDao.getForEvent(originalEvent);
        for (CurtailmentEventParticipant origEventParticipant : allOrigEventParticipants) {
            if (customersToRemove.contains(origEventParticipant.getCustomer())) {
                // Remove from original event
                curtailmentEventParticipantDao.delete(origEventParticipant);

                // Add to new event
                CurtailmentEventParticipant splitEventParticipant = new CurtailmentEventParticipant();
                splitEventParticipant.setCustomer(origEventParticipant.getCustomer());
                splitEventParticipant.setEvent(splitEvent);
                splitEventParticipant.setNotifAttribs(origEventParticipant.getNotifAttribs());
                curtailmentEventParticipantDao.save(splitEventParticipant);
            }
        }
        
        notifConnection.sendCurtailmentNotification(splitEvent.getId(), CurtailmentEventAction.ADJUSTING);
        
        int[] customerIds = new int[customersToRemove.size()];
        for (int i = 0; i < customersToRemove.size(); i++) {
            customerIds[i] = customersToRemove.get(i).getId();
        }
        Program program = splitEvent.getProgram();
        String eventDisplayName = program.getIdentifierPrefix() + identifier;
        notifConnection.sendProgramEventNotification(program.getId(), 
                                                     eventDisplayName, 
                                                     "split", 
                                                     splitEvent.getStartTime(), 
                                                     splitEvent.getStopTime(), 
                                                     splitEvent.getNotificationTime(), 
                                                     customerIds);
        
        // "Direct" events message load management
        String strategyString = program.getProgramType().getStrategy();
        CiEventType eventType = CiEventType.of(strategyString);
        if (eventType.isDirect()) {
            for (CICustomerStub customer : customersToRemove) {
                Set<Integer> lmProgramIds = lmDirectCustomerListDao.getLMProgramIdsForCustomer(customer.getId());
                for (Integer programId : lmProgramIds) {
                    log.debug("Sending changeProgramStop for event: " + splitEvent + " lmProgramId=" + programId 
                              + ", stopTime=" + splitEvent.getStopTime());
                    loadManagementService.stopProgram(programId);
                }
            }
        }
        
        return splitEvent.getId();
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
        
        sendNotifications(event, program, identifier, customerNotifs, "started");
        
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
        sendNotifications(event, program, identifier, customerNotifs, "started");
        
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
        
        // For event extension, set the initial event being extended.
        if (event.isEventExtension()) {
            EconomicEvent initialEvent = economicEventDao.getForId(event.getInitialEventId());
            economicEvent.setInitialEvent(initialEvent);
        }
        
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
        List<GroupCustomerNotif> customerNotifs = null;
        if (event.isEventExtension()) {
            EconomicEvent initialEvent = economicEventDao.getForId(event.getInitialEventId());
            List<EconomicEventParticipant> initialParticipants = economicEventParticipantDao.getForEvent(initialEvent);
            List<EconomicEventParticipant> extensionParticipants = initialParticipants.stream()
                    .filter(new Predicate<EconomicEventParticipant>() {
                        @Override
                        public boolean test(EconomicEventParticipant participant) {
                            return event.getSelectedCustomerIds().contains(participant.getCustomer().getId());
                        }
                    }).collect(Collectors.toList());
            
            for (EconomicEventParticipant oldParticipant : extensionParticipants) {
                EconomicEventParticipant participant = new EconomicEventParticipant();
                participant.setCustomer(oldParticipant.getCustomer());
                participant.setEvent(economicEvent);
                participant.setNotifAttribs(oldParticipant.getNotifAttribs());
                setUpSelection(participant, revision, creationTime, windows);
                economicEventParticipantDao.save(participant);
            }
            
        } else {
            customerNotifs = groupCustomerNotifDao.getByIds(event.getSelectedCustomerIds());
            for (GroupCustomerNotif notif : customerNotifs) {
                EconomicEventParticipant participant = new EconomicEventParticipant();
                participant.setCustomer(notif.getCustomer());
                participant.setEvent(economicEvent);
                participant.setNotifAttribs(notif.getAttribs());
                setUpSelection(participant, revision, creationTime, windows);
                economicEventParticipantDao.save(participant);
            }
        }
        
        EconomicEventAction action = event.isEventExtension() ? EconomicEventAction.EXTENDING : EconomicEventAction.STARTING;
        String actionString = event.isEventExtension() ? "extended" : "started";
        notifConnection.sendEconomicNotification(economicEvent.getId(), 1, action);
        
        if (event.isEventExtension()) {
            int[] customerIds = CtiUtilities.toArrayUnbox(event.getSelectedCustomerIds());
            sendNotifications(event, program, identifier, customerIds, actionString);
        } else {
            sendNotifications(event, program, identifier, customerNotifs, actionString);
        }
        
        return eventId;
    }
    
    private void setUpSelection(EconomicEventParticipant participant, EconomicEventPricing revision, Date creationTime, 
                                List<EconomicEventPricingWindow> windows) throws PointException {
        
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
                                   List<GroupCustomerNotif> customerNotifs, String action) throws ConnectionException {
        // Get notification info
        int[] customerIds = new int[customerNotifs.size()];
        for (int i = 0; i < customerNotifs.size(); i++) {
            customerIds[i] = customerNotifs.get(i).getCustomer().getId();
        }
        
        sendNotifications(event, program, programIdentifier, customerIds, action);
    }
    
    private void sendNotifications(CiInitEventModel event, Program program, Integer programIdentifier, 
                                   int[] customerIds, String action) throws ConnectionException {
        String eventDisplayName = program.getIdentifierPrefix() + programIdentifier;
        DateTime stopTime = event.getStartTime().plus(Duration.standardMinutes(event.getDuration()));
        
        // Send event notifications
        notifConnection.sendProgramEventNotification(program.getId(), 
                                                     eventDisplayName, 
                                                     action, 
                                                     event.getStartTime().toDate(), 
                                                     stopTime.toDate(), 
                                                     event.getNotificationTime().toDate(), 
                                                     customerIds);
    }
}
