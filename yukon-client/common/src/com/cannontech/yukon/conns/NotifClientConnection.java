package com.cannontech.yukon.conns;

import java.util.Date;

import org.apache.commons.lang.Validate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.enums.CurtailmentEventAction;
import com.cannontech.enums.EconomicEventAction;
import com.cannontech.messaging.message.BooleanData;
import com.cannontech.messaging.message.notif.CallStatus;
import com.cannontech.messaging.message.notif.CurtailmentEventDeleteMessage;
import com.cannontech.messaging.message.notif.CurtailmentEventMessage;
import com.cannontech.messaging.message.notif.EconomicEventDeleteMessage;
import com.cannontech.messaging.message.notif.EconomicEventMessage;
import com.cannontech.messaging.message.notif.EmailMessage;
import com.cannontech.messaging.message.notif.ProgramActionMessage;
import com.cannontech.messaging.message.notif.VoiceCompletedMessage;
import com.cannontech.messaging.message.notif.VoiceDataRequestMessage;
import com.cannontech.messaging.message.notif.VoiceDataResponseMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.messaging.util.BadServerResponseException;
import com.cannontech.messaging.util.ClientConnection;
import com.cannontech.messaging.util.ServerRequest;
import com.cannontech.messaging.util.ServerRequestHelper;
import com.cannontech.messaging.util.ServerRequestImpl;
import com.cannontech.yukon.INotifConnection;

public class NotifClientConnection extends ClientConnection implements INotifConnection {

    public NotifClientConnection() {
        super("Notification");
    }

    /**
     * Requests the String message from the server using the given token.
     */
    public synchronized String requestMessage(String token) throws NotifRequestException {

        String retStr = null;
        if (token == null)
            return retStr;

        ServerResponseMessage responseMsg = null;
        VoiceDataRequestMessage vdReqMsg = new VoiceDataRequestMessage();
        vdReqMsg.setCallToken(token);

        try {
            ServerRequest srvrReq = new ServerRequestImpl();
            // request the object from the server
            responseMsg = srvrReq.makeServerRequest(this, vdReqMsg);
        }
        catch (Exception e) {
            CTILogger.error("No response received from server", e);
        }

        if (responseMsg != null && responseMsg.getStatus() == ServerResponseMessage.STATUS_OK) {

            VoiceDataResponseMessage vdRespMsg = (VoiceDataResponseMessage) responseMsg.getPayload();
            retStr = vdRespMsg.getXmlData();
        }
        else {
            retStr =
                "Unable to get the requested voice " + "message, responseCode= " + responseMsg.getStatus() +
                    ", token= " + vdReqMsg.getCallToken();
            CTILogger.info(retStr);
            throw new NotifRequestException(retStr);
        }

        return retStr;
    }

    public synchronized int requestMessageContactId(String callToken) throws NotifRequestException {

        Validate.notNull(callToken);

        ServerResponseMessage responseMsg = null;
        VoiceDataResponseMessage vdReqMsg = new VoiceDataResponseMessage();
        vdReqMsg.setCallToken(callToken);

        try {
            ServerRequest srvrReq = new ServerRequestImpl();
            // request the object from the server
            responseMsg = srvrReq.makeServerRequest(this, vdReqMsg);
        }
        catch (Exception e) {
            CTILogger.error("No response received from server", e);
        }

        if (responseMsg != null && responseMsg.getStatus() == ServerResponseMessage.STATUS_OK) {

            VoiceDataResponseMessage vdRespMsg = (VoiceDataResponseMessage) responseMsg.getPayload();
            return vdRespMsg.getContactId();
        }
        String errorMsg =
            "Unable to get the requested voice " + "message, responseCode= " + responseMsg.getStatus() + ", token= " +
                vdReqMsg.getCallToken();
        CTILogger.info(errorMsg);
        throw new NotifRequestException(errorMsg);
    }

    /**
     * Send a confirmation message to the notification server
     * @param token the call token
     * @param success true if the person called was likely to have heard the notification
     */
    public void sendCallEvent(String token, CallStatus event) {
        VoiceCompletedMessage msg = new VoiceCompletedMessage();
        msg.setCallToken(token);
        msg.setCallStatus(event);

        write(msg);
    }

    /**
     * Send a Curtailment Notification message to the notification server.
     * @param the id of the event from the CCurtCurtailmentEvent table
     * @param action the action that's affecting the event
     */
    public void sendCurtailmentNotification(Integer curtailmentEventId, CurtailmentEventAction action) {
        CurtailmentEventMessage msg = new CurtailmentEventMessage();
        msg.setCurtailmentEventId(curtailmentEventId);
        msg.setAction(action);
        write(msg);
    }

    public boolean attemptDeleteCurtailmentNotification(Integer curtailmentEventId, boolean includeStart) {
        CurtailmentEventDeleteMessage deleteMsg = new CurtailmentEventDeleteMessage();
        deleteMsg.setCurtailmentEventId(curtailmentEventId);
        deleteMsg.setDeleteStart(includeStart);
        deleteMsg.setDeleteStop(true);
        BooleanData wasCancelled;
        try {
            wasCancelled = (BooleanData) ServerRequestHelper.makeServerRequest(this, deleteMsg);
        }
        catch (BadServerResponseException e) {
            return false;
        }
        return wasCancelled.getValue();
    }

    public void
        sendEconomicNotification(Integer economicPricingRevisionId, Integer revision, EconomicEventAction action) {
        EconomicEventMessage msg = new EconomicEventMessage();
        msg.setEconomicEventId(economicPricingRevisionId);
        msg.setRevisionNumber(revision);
        msg.setAction(action);
        write(msg);
    }

    public boolean attemptDeleteEconomic(Integer eventId, boolean includeStart) {
        EconomicEventDeleteMessage msg = new EconomicEventDeleteMessage();
        msg.setEconomicEventId(eventId);
        msg.setDeleteStart(includeStart);
        msg.setDeleteStop(true);
        BooleanData wasCancelled;
        try {
            wasCancelled = (BooleanData) ServerRequestHelper.makeServerRequest(this, msg);
        }
        catch (BadServerResponseException e) {
            return false;
        }
        return wasCancelled.getValue();
    }

    public void sendProgramEventNotification(Integer programId, String eventDisplayName, String action, Date startTime,
                                             Date stopTime, Date notificationTime, int[] customerIds) {
        ProgramActionMessage msg = new ProgramActionMessage();
        msg.setProgramId(programId);
        msg.setEventDisplayName(eventDisplayName);
        msg.setAction(action);
        msg.setStartTime(startTime);
        msg.setStopTime(stopTime);
        msg.setNotificationTime(notificationTime);
        msg.setCustomerIds(customerIds);
        write(msg);
    }

    public void sendNotification(Integer ngId, String subject, String body) {
        EmailMessage msg = new EmailMessage();
        msg.setNotifGroupId(ngId);
        msg.setSubject(subject);
        msg.setBody(body);

        write(msg);
    }

}
