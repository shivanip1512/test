package com.cannontech.cc.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.cc.service.builder.CurtailmentBuilder;
import com.cannontech.cc.service.builder.CurtailmentChangeBuilder;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.cc.service.exception.EventModificationException;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.web.LMDirectCustomerList;
import com.cannontech.loadcontrol.LoadManagementService;

public abstract class BaseDirectStrategy extends BaseNotificationStrategy {
    private LoadManagementService loadManagementService;
    private CustomerLMProgramService customerLMProgramService;

    public BaseDirectStrategy() {
        super();
    }
    
    @Override
    public CurtailmentEvent createEvent(CurtailmentBuilder builder) throws EventCreationException {
        final CurtailmentEvent curtailmentEvent = super.createEvent(builder);
        sendMessages(curtailmentEvent, new DoWithId() {
            public void forProgram(int lmProgramId) {
                Date startTime = curtailmentEvent.getStartTime();
                Date stopTime = curtailmentEvent.getStopTime();
                CTILogger.debug("Sending startProgram for event: " + curtailmentEvent);
                CTILogger.debug("  lmProgramId=" + lmProgramId + ", startTime=" + startTime + ", stopTime=" + stopTime);
                loadManagementService.startProgram(lmProgramId, startTime, stopTime);
            }
        });
        return curtailmentEvent;
    }
    
    @Override
    public void cancelEvent(final CurtailmentEvent event, LiteYukonUser user) {
        super.cancelEvent(event, user);
        sendMessages(event, new DoWithId() {
            public void forProgram(int lmProgramId) {
                CTILogger.debug("Sending changeProgramStop for event: " + event);
                CTILogger.debug("  lmProgramId=" + lmProgramId + ", stopTime=" + event.getStopTime());
                loadManagementService.stopProgram(lmProgramId);
            }
        });
    }
    
    @Override
    public CurtailmentEvent adjustEvent(CurtailmentChangeBuilder builder, LiteYukonUser user) throws EventModificationException {
        final CurtailmentEvent event = super.adjustEvent(builder, user);
        sendMessages(event, new DoWithId() {
            public void forProgram(int lmProgramId) {
                CTILogger.debug("Sending changeProgramStop for event: " + event);
                CTILogger.debug("  lmProgramId=" + lmProgramId + ", stopTime=" + event.getStopTime());
                loadManagementService.changeProgramStop(lmProgramId, event.getStopTime());
            }
        });
        return event;
    }
    
    @Override
    protected void doBeforeDeleteEvent(final CurtailmentEvent event, LiteYukonUser user) {
        super.doBeforeDeleteEvent(event, user);
        sendMessages(event, new DoWithId() {
            public void forProgram(int lmProgramId) {
                CTILogger.debug("Sending stopProgram for event: " + event);
                CTILogger.debug("  lmProgramId=" + lmProgramId);
                loadManagementService.stopProgram(lmProgramId);
            }
        });
    }
    
    @Override
    protected void verifyCustomer(EventBuilderBase builder, VerifiedCustomer vCustomer) {
        CICustomerStub customer = vCustomer.getCustomer();
        Set<LiteYukonPAObject> programsForCustomer = customerLMProgramService.getProgramsForCustomer(customer);
        if (programsForCustomer.isEmpty()) {
            vCustomer.addExclusion(VerifiedCustomer.Status.EXCLUDE, "no direct program is attached");
        }
    }

    public LoadManagementService getLoadManagementService() {
        return loadManagementService;
    }

    public void setLoadManagementService(LoadManagementService loadManagementService) {
        this.loadManagementService = loadManagementService;
    }
    
    protected void sendMessages(CurtailmentEvent event, DoWithId action) {
        List<CurtailmentEventParticipant> participants = 
            getCurtailmentEventParticipantDao().getForEvent(event);
        
        for (CurtailmentEventParticipant participant : participants) {
            CICustomerStub customer = participant.getCustomer();
            long[] lmProgramIds = LMDirectCustomerList.getProgramIDs(customer.getId());
            for (int i = 0; i < lmProgramIds.length; i++) {
                long lmProgramId = lmProgramIds[i];
                action.forProgram((int) lmProgramId);
            }
        }
    }
    
    protected interface DoWithId {
        public void forProgram(int lmProgramId);
    }

    public CustomerLMProgramService getCustomerLMProgramService() {
        return customerLMProgramService;
    }

    public void setCustomerLMProgramService(CustomerLMProgramService customerLMProgramService) {
        this.customerLMProgramService = customerLMProgramService;
    }


}
