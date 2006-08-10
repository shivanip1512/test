package com.cannontech.yukon;

import com.cannontech.enums.CurtailmentEventAction;
import com.cannontech.enums.EconomicEventAction;
import com.cannontech.yukon.conns.NotifRequestException;

/**
 * Interface for connections to the Notifcation Server
 *
 */
public interface INotifConnection
{
	public String requestMessage( String token ) throws NotifRequestException;

	public void sendConfirmation(String token, boolean success);

    public void sendCurtailmentNotification(Integer curtailmentEventId, CurtailmentEventAction action);

    public boolean attemptDeleteCurtailmentNotification(Integer curtailmentEventId, boolean includeStart);

    public void sendEconomicNotification(Integer economicEventId, Integer revision, EconomicEventAction action);

    public boolean attemptDeleteEconomic(Integer eventId, boolean includeStart);
}
