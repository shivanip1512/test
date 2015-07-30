package com.cannontech.web.dr.cc.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.AccountingEventDao;
import com.cannontech.cc.dao.AccountingEventParticipantDao;
import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventParticipantDao;
import com.cannontech.cc.dao.GroupCustomerNotifDao;
import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.service.CurtailmentEventAction;
import com.cannontech.cc.service.CurtailmentEventState;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.LMDirectCustomerListDao;
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
    @Autowired GroupCustomerNotifDao groupCustomerNotifDao;
    @Autowired INotifConnection notifConnection;
    @Autowired LMDirectCustomerListDao lmDirectCustomerListDao;
    @Autowired LoadManagementService loadManagementService;
    @Autowired ProgramDao programDao;
    @Autowired ProgramService programService;
    
    @Override
    public int createEvent(CiInitEventModel event) {
        int eventId;
        
        if (event.getEventType().isAccounting()) {
            return createAccountingEvent(event);
        } else if (event.getEventType().isNotification()) {
            return createCurtailmentEvent(event);
        } else if (event.getEventType().isEconomic()) {
            throw new IllegalArgumentException("Event type not supported yet: " + event.getEventType());
        } else {
            throw new IllegalArgumentException("Event type not supported: " + event.getEventType());
        }
    }
    
    @Transactional
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
    
    @Transactional
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
