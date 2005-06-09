package com.cannontech.yukon;

import com.cannontech.yukon.conns.NotifRequestException;

/**
 * Interface for connections to the Notifcation Server
 *
 */
public interface INotifConnection extends IServerConnection 
{
	
	public String requestMessage( String token ) throws NotifRequestException;

	public void sendConfirmation(String token, boolean success);

}
