package com.cannontech.yukon;

/**
 * Base interface for all yukon server connections
 * @author alauinger
 */
public interface IServerConnection 
{

	void disconnect() throws java.io.IOException;

/*
public void connect() throws java.io.IOException;
public void connectWithoutWait() throws java.io.IOException;
public void disconnect() throws java.io.IOException;
public void doHandleMessage(Object message);
public boolean getAutoReconnect();
public String getHost();
public int getNumOutMessages();
public int getPort();
public Message getRegistrationMsg();
public int getTimeToReconnect();

// handleMessage should be defined by subclasses should they want
// a chance to handle a particular message when it comes in.
// Before a message is put in the inVector handleMessage is
// called.  If handleMEssage returns true then the message
// is considered handled otherwise the message will be put
// in the inVector to await processing.
// Do not actually handle the message here, handle it in doHandleMessage
// @param message CtiMessage 
public boolean handleMessage(Object message);
 
// Use this method to determine if this connection has been told
//   to connect() or connectWithoutWait(). If, for example, the user
//   wanted 1 instance of this ClientConnection() active and did not want
//   another monitorThread to be created if either connection methods were
//   called.   -- RWN 
public boolean isMonitorThreadAlive();
public boolean isValid();

// read blocks until an object is available in the in queue and
// then returns a reference to it.
// @return java.lang.Object
public Object read();

// read blocks until an object is available in the in queue or at least millis milliseconds have elapsed
// then returns a reference to it.  
// @return java.lang.Object
public Object read(long millis);

public void reconnect() throws java.io.IOException;
public void run();
public void setAutoReconnect(boolean val);
public void setHost(String host);
public void setPort(int port);
public void setRegistrationMsg(Message newValue); 
public void setTimeToReconnect(int secs);
public void write(Object o);
*/

}
