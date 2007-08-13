package com.cannontech.yukon.conns;

import java.util.Date;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.enums.CurtailmentEventAction;
import com.cannontech.enums.EconomicEventAction;
import com.cannontech.message.notif.CurtailmentEventDeleteMsg;
import com.cannontech.message.notif.CurtailmentEventMsg;
import com.cannontech.message.notif.DefColl_CurtailmentEventDeleteMsg;
import com.cannontech.message.notif.DefColl_CurtailmentEventMsg;
import com.cannontech.message.notif.DefColl_EconomicEventDeleteMsg;
import com.cannontech.message.notif.DefColl_EconomicEventMsg;
import com.cannontech.message.notif.DefColl_NotifAlarmMsg;
import com.cannontech.message.notif.DefColl_NotifCompletedMsg;
import com.cannontech.message.notif.DefColl_NotifEmailMsg;
import com.cannontech.message.notif.DefColl_NotifLMControlMsg;
import com.cannontech.message.notif.DefColl_ProgramActionMsg;
import com.cannontech.message.notif.DefColl_VoiceDataRequestMsg;
import com.cannontech.message.notif.DefColl_VoiceDataResponseMsg;
import com.cannontech.message.notif.EconomicEventDeleteMsg;
import com.cannontech.message.notif.EconomicEventMsg;
import com.cannontech.message.notif.NotifCompletedMsg;
import com.cannontech.message.notif.NotifEmailMsg;
import com.cannontech.message.notif.ProgramActionMsg;
import com.cannontech.message.notif.VoiceDataRequestMsg;
import com.cannontech.message.notif.VoiceDataResponseMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.CollectableBoolean;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.message.util.ServerRequestHelper;
import com.cannontech.message.util.ServerRequestImpl;
import com.cannontech.yukon.INotifConnection;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.CollectableMappings;

public class NotifClientConnection extends ClientConnection implements INotifConnection
{
	//the rwav messages we listen for
	public static final DefineCollectable[] DEFAULT_MAPPINGS =
	{
		CollectableMappings.OrderedVector,
		
        new DefColl_NotifAlarmMsg(),
        new DefColl_NotifLMControlMsg(),
        new DefColl_VoiceDataRequestMsg(),
        new DefColl_VoiceDataResponseMsg(),
        new DefColl_NotifCompletedMsg(),
        new DefColl_NotifEmailMsg(),
        new DefColl_CurtailmentEventMsg(),
        new DefColl_CurtailmentEventDeleteMsg(),
        new DefColl_EconomicEventMsg(),
        new DefColl_EconomicEventDeleteMsg(),
        new DefColl_ProgramActionMsg()
	};
	    
	public NotifClientConnection() 
	{
		super("Notification");
		initialize();
	}

	/**
	 * Any special initing should be done here.
	 * 
	 */
	private void initialize() 
	{	
		setAutoReconnect( true );
	}	


	protected void registerMappings(CollectableStreamer polystreamer) 
	{
		super.registerMappings(polystreamer);
	
		for( int i = 0; i < DEFAULT_MAPPINGS.length; i++ )
		{
			polystreamer.register( DEFAULT_MAPPINGS[i] );
		}
	}



	/**
	 * Requests the String message from the server using the
	 * given token.
	 * 
	 */
	public synchronized String requestMessage( String token ) throws NotifRequestException {

		String retStr = null;
		if( token == null )
			return retStr;

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

    /**
     * Send a confirmation message to the notification server
     * @param token the call token
     * @param success true if the person called was likely to have heard the notification
     */
    public void sendConfirmation(String token, boolean success) {
        NotifCompletedMsg msg = new NotifCompletedMsg();
        msg.token = token;
        msg.gotConfirmation = success;
        
        write(msg);
    }
    
    /**
     * Send a Curtailment Notification message to the notification
     * server.
     * @param the id of the event from the CCurtCurtailmentEvent table
     * @param action the action that's affecting the event
     */
    public void sendCurtailmentNotification(Integer curtailmentEventId, CurtailmentEventAction action) {
        CurtailmentEventMsg msg = new CurtailmentEventMsg();
        msg.curtailmentEventId = curtailmentEventId;
        msg.action = action;
        write(msg);
    }
    
    public boolean attemptDeleteCurtailmentNotification(Integer curtailmentEventId, boolean includeStart) {
        CurtailmentEventDeleteMsg deleteMsg = new CurtailmentEventDeleteMsg();
        deleteMsg.curtailmentEventId = curtailmentEventId;
        deleteMsg.deleteStart = includeStart;
        deleteMsg.deleteStop = true;
        CollectableBoolean wasCancelled = (CollectableBoolean) ServerRequestHelper.makeServerRequest(this, deleteMsg);
        return wasCancelled.getValue();
    }
    
    public void sendEconomicNotification(Integer economicPricingRevisionId, Integer revision, EconomicEventAction action) {
        EconomicEventMsg msg = new EconomicEventMsg();
        msg.economicEventId = economicPricingRevisionId;
        msg.revisionNumber = revision;
        msg.action = action;
        write(msg);
    }

    public boolean attemptDeleteEconomic(Integer eventId, boolean includeStart) {
        EconomicEventDeleteMsg msg = new EconomicEventDeleteMsg();
        msg.economicEventId = eventId;
        msg.deleteStart = includeStart;
        msg.deleteStop = true;
        CollectableBoolean wasCancelled = (CollectableBoolean) ServerRequestHelper.makeServerRequest(this, msg);
        return wasCancelled.getValue();
    }
    
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

    public void sendNotification(Integer ngId, String subject,String body	) {
		NotifEmailMsg msg = new NotifEmailMsg();
		msg.setNotifGroupID(ngId);
		msg.setSubject(subject);
		msg.setBody(body); 

		write(msg);    		
    }

}
