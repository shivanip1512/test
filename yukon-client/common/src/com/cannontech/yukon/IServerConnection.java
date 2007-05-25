package com.cannontech.yukon;

import java.util.Observer;


/**
 * Base interface for all yukon server connections
 * @author alauinger
 */
public interface IServerConnection extends BasicServerConnection 
{
	boolean getAutoReconnect();
	String getHost();
	int getNumOutMessages();
	int getPort();

	int getTimeToReconnect();
	boolean isMonitorThreadAlive(); 

    void setAutoReconnect(boolean val);

	boolean isQueueMessages();
	void setQueueMessages(boolean b);

    public void disconnect();

	//not sure if these will stay or go?
	public void addObserver(Observer obs);
	public void deleteObserver(Observer obs);

}