package com.cannontech.yukon.conns;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.RoleFuncs;
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
import com.cannontech.message.notif.DefColl_VoiceDataRequestMsg;
import com.cannontech.message.notif.DefColl_VoiceDataResponseMsg;
import com.cannontech.message.notif.EconomicEventDeleteMsg;
import com.cannontech.message.notif.EconomicEventMsg;
import com.cannontech.message.notif.NotifCompletedMsg;
import com.cannontech.message.notif.VoiceDataRequestMsg;
import com.cannontech.message.notif.VoiceDataResponseMsg;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.CollectableBoolean;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.message.util.ServerRequestHelper;
import com.cannontech.roles.yukon.SystemRole;
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
        new DefColl_EconomicEventDeleteMsg()
	};
	
	public NotifClientConnection() 
	{
		super();
		initialize();
		getResources();
	}

	/**
	 * Get our properties for this connection from the cache
	 *
	 */
	private void getResources() 
	{
		setHost( RoleFuncs.getGlobalPropertyValue( SystemRole.NOTIFICATION_HOST ) );

		setPort( Integer.parseInt(
			RoleFuncs.getGlobalPropertyValue( SystemRole.NOTIFICATION_PORT ) ) );	
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
		ServerRequest srvrReq =
			ServerRequest.makeServerRequest( this, vdReqMsg );

		try {
			//request the object from the server
			responseMsg = srvrReq.execute();
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
        switch (action) {
        case ADJUSTING:
            msg.action = CurtailmentEventMsg.ADJUSTING;
            break;
        case CANCELING:
            msg.action = CurtailmentEventMsg.CANCELLING;
            break;
        case STARTING: 
            msg.action = CurtailmentEventMsg.STARTING;
            break;
        default:
            throw new IllegalArgumentException("Invalid action: " + action);
        }
        write(msg);
    }
    
    public boolean attemptDeleteCurtailmentNotification(Integer curtailmentEventId) {
        CurtailmentEventDeleteMsg deleteMsg = new CurtailmentEventDeleteMsg();
        deleteMsg.curtailmentEventId = curtailmentEventId;
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

    public boolean attemptDeleteEconomic(Integer eventId) {
        EconomicEventDeleteMsg msg = new EconomicEventDeleteMsg();
        msg.economicEventId = eventId;
        CollectableBoolean wasCancelled = (CollectableBoolean) ServerRequestHelper.makeServerRequest(this, msg);
        return wasCancelled.getValue();
    }

}
