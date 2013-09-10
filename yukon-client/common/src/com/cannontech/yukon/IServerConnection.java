package com.cannontech.yukon;

import java.util.Observer;

import org.springframework.jmx.export.annotation.ManagedResource;


/**
 * Base interface for all yukon server connections
 * @author alauinger
 */
@ManagedResource
public interface IServerConnection extends BasicServerConnection 
{
    public boolean getAutoReconnect();
	
	public 	int getNumOutMessages();
	
	public boolean isMonitorThreadAlive(); 

	public void setAutoReconnect(boolean val);

    public boolean isQueueMessages();
    public void setQueueMessages(boolean b);

    public void disconnect();

	//not sure if these will stay or go?
	public void addObserver(Observer obs);
	public void deleteObserver(Observer obs);

}