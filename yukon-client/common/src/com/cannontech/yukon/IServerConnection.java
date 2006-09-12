package com.cannontech.yukon;

import java.util.Observer;

import com.cannontech.message.util.MessageListener;

/**
 * Base interface for all yukon server connections
 * @author alauinger
 */
public interface IServerConnection 
{
	boolean getAutoReconnect();
	String getHost();
	int getNumOutMessages();
	int getPort();

	int getTimeToReconnect();
	boolean isMonitorThreadAlive(); 
	boolean isValid();

//	Object read();
//	Object read(long millis);
//	Object readInQueue();

	void setAutoReconnect(boolean val);

	void write(Object o);
    public void queue(Object o);
	void addMessageListener(MessageListener l);
	void removeMessageListener(MessageListener l);

	boolean isQueueMessages();
	void setQueueMessages(boolean b);



	//not sure if these will stay or go?
	public void addObserver(Observer obs);
	public void deleteObserver(Observer obs);

}