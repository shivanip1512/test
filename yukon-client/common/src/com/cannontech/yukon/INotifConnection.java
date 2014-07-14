package com.cannontech.yukon;

import java.util.Date;

import com.cannontech.cc.service.CurtailmentEventAction;
import com.cannontech.cc.service.EconomicEventAction;
import com.cannontech.message.notif.NotifCallEvent;
import com.cannontech.yukon.conns.NotifRequestException;

/**
 * Interface for connections to the Notifcation Server
 *
 */
public interface INotifConnection
{
	public String requestMessage( String token ) throws NotifRequestException;
	public int requestMessageContactId( String callToken ) throws NotifRequestException;
	
	public void sendCallEvent(String token, NotifCallEvent event);

    public void sendCurtailmentNotification(Integer curtailmentEventId, CurtailmentEventAction action);

    public boolean attemptDeleteCurtailmentNotification(Integer curtailmentEventId, boolean includeStart);

    public void sendEconomicNotification(Integer economicEventId, Integer revision, EconomicEventAction action);

    public boolean attemptDeleteEconomic(Integer eventId, boolean includeStart);
    
    public void sendProgramEventNotification(Integer programId,
                                             String eventDisplayName,
                                             String action,
                                             Date startTime,
                                             Date stopTime,
                                             Date notificationTime,
                                             int customerIds[]);
    
    public void sendNotification(Integer ngId, String subject, String body	);
}
