package com.cannontech.yukon.conns;

import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;

import com.cannontech.cc.service.CurtailmentEventAction;
import com.cannontech.cc.service.EconomicEventAction;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.notif.CurtailmentEventDeleteMsg;
import com.cannontech.message.notif.CurtailmentEventMsg;
import com.cannontech.message.notif.EconomicEventDeleteMsg;
import com.cannontech.message.notif.EconomicEventMsg;
import com.cannontech.message.notif.EmailMsg;
import com.cannontech.message.notif.NotifCallEvent;
import com.cannontech.message.notif.NotifCompletedMsg;
import com.cannontech.message.notif.NotifEmailMsg;
import com.cannontech.message.notif.ProgramActionMsg;
import com.cannontech.message.notif.VoiceDataRequestMsg;
import com.cannontech.message.notif.VoiceDataResponseMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.CollectableBoolean;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.message.util.ServerRequestHelper;
import com.cannontech.message.util.ServerRequestImpl;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.yukon.INotifConnection;

public class NotifClientConnection extends ClientConnection implements INotifConnection {
    private static final Logger log = YukonLogManager.getLogger(NotifClientConnection.class);
    
    public NotifClientConnection(){
        super("Notification");
    }

    /**
     * Requests the String message from the server using the
     * given token.
     * 
     */
    @Override
    public synchronized String requestMessage( String token ) throws NotifRequestException {
        
        String retStr = null;
        if( token == null ) {
            return retStr;
        }
        
        ServerResponseMsg responseMsg = null;
        VoiceDataRequestMsg vdReqMsg = new VoiceDataRequestMsg();
        vdReqMsg.callToken = token;
        
        try {
            ServerRequest srvrReq = new ServerRequestImpl();
            //request the object from the server
            responseMsg = srvrReq.makeServerRequest(this, vdReqMsg);
        }
        catch(Exception e) {
            CTILogger.error( "No response received from server", e );
        }
        
        if( responseMsg != null 
                && responseMsg.getStatus() == ServerResponseMsg.STATUS_OK )  {
            
            VoiceDataResponseMsg vdRespMsg = (VoiceDataResponseMsg)responseMsg.getPayload();
            retStr = vdRespMsg.xmlData;
        }
        else {
            retStr = "Unable to get the requested voice " + 
            "message, responseCode= " +  responseMsg.getStatus() +
            ", token= " + vdReqMsg.callToken;
            CTILogger.info(retStr);
            throw new NotifRequestException(retStr);
        }
        
        return retStr;
    }
    
    @Override
    public synchronized int requestMessageContactId( String callToken ) throws NotifRequestException {

        Validate.notNull(callToken);
        
        ServerResponseMsg responseMsg = null;
        VoiceDataRequestMsg vdReqMsg = new VoiceDataRequestMsg();
        vdReqMsg.callToken = callToken;

        try {
            ServerRequest srvrReq = new ServerRequestImpl();
            //request the object from the server
            responseMsg = srvrReq.makeServerRequest(this, vdReqMsg);
        } catch (Exception e) {
            CTILogger.error( "No response received from server", e );
        }

        if ( responseMsg != null 
            && responseMsg.getStatus() == ServerResponseMsg.STATUS_OK )  {

            VoiceDataResponseMsg vdRespMsg = (VoiceDataResponseMsg)responseMsg.getPayload();
            return vdRespMsg.contactId;
        }
        String errorMsg = "Unable to get the requested voice " + 
        "message, responseCode= " +  responseMsg.getStatus() +
        ", token= " + vdReqMsg.callToken;
        CTILogger.info(errorMsg);
        throw new NotifRequestException(errorMsg);
    }

    /**
     * Send a confirmation message to the notification server
     * @param token the call token
     * @param success true if the person called was likely to have heard the notification
     */
    @Override
    public void sendCallEvent(String token, NotifCallEvent event) {
        NotifCompletedMsg msg = new NotifCompletedMsg();
        msg.token = token;
        msg.status = event;
        
        write(msg);
    }
    
    /**
     * Send a Curtailment Notification message to the notification
     * server.
     * @param the id of the event from the CCurtCurtailmentEvent table
     * @param action the action that's affecting the event
     */
    @Override
    public void sendCurtailmentNotification(Integer curtailmentEventId, CurtailmentEventAction action) {
        CurtailmentEventMsg msg = new CurtailmentEventMsg();
        msg.curtailmentEventId = curtailmentEventId;
        msg.action = action;
        write(msg);
    }
    
    @Override
    public boolean attemptDeleteCurtailmentNotification(Integer curtailmentEventId, boolean includeStart) {
        CurtailmentEventDeleteMsg deleteMsg = new CurtailmentEventDeleteMsg();
        deleteMsg.curtailmentEventId = curtailmentEventId;
        deleteMsg.deleteStart = includeStart;
        deleteMsg.deleteStop = true;
        CollectableBoolean wasCancelled;
        try {
            wasCancelled = (CollectableBoolean) ServerRequestHelper.makeServerRequest(this, deleteMsg);
        } catch (BadServerResponseException e) {
            return false;
        }
        return wasCancelled.getValue();
    }
    
    @Override
    public void sendEconomicNotification(Integer economicPricingRevisionId, Integer revision, EconomicEventAction action) {
        EconomicEventMsg msg = new EconomicEventMsg();
        msg.economicEventId = economicPricingRevisionId;
        msg.revisionNumber = revision;
        msg.action = action;
        write(msg);
    }

    @Override
    public boolean attemptDeleteEconomic(Integer eventId, boolean includeStart) {
        EconomicEventDeleteMsg msg = new EconomicEventDeleteMsg();
        msg.economicEventId = eventId;
        msg.deleteStart = includeStart;
        msg.deleteStop = true;
        CollectableBoolean wasCancelled;
        try {
            wasCancelled = (CollectableBoolean) ServerRequestHelper.makeServerRequest(this, msg);
        } catch (BadServerResponseException e) {
            return false;
        }
        return wasCancelled.getValue();
    }
    
    @Override
    public void sendProgramEventNotification(Integer programId, 
                                             String eventDisplayName, 
                                             String action, 
                                             Date startTime, 
                                             Date stopTime, 
                                             Date notificationTime, 
                                             int[] customerIds) {
        ProgramActionMsg msg = new ProgramActionMsg();
        msg.programId = programId;
        msg.eventDisplayName = eventDisplayName;
        msg.action = action;
        msg.startTime = startTime;
        msg.stopTime = stopTime;
        msg.notificationTime = notificationTime;
        msg.customerIds = customerIds;
        write(msg);
    }

    @Override
    public void sendNotification(Integer ngId, String subject,String body   ) {
        NotifEmailMsg msg = new NotifEmailMsg();
        msg.setNotifGroupID(ngId);
        msg.setSubject(subject);
        msg.setBody(body); 

        write(msg);         
    }
    
    public void sendEmail(EmailMessage message) {
        log.debug("Passing email message to notification for : ");
        log.debug(message);
        
        EmailMsg msg = new EmailMsg();
        msg.setMessage(message);
        write(msg);
    }

}