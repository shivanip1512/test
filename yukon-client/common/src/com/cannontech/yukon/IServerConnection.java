package com.cannontech.yukon;

import java.util.Observer;

import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Base interface for all yukon server connections
 */
@ManagedResource
public interface IServerConnection extends BasicServerConnection {
    
    public boolean getAutoReconnect();
    
    public int getNumOutMessages();
    
    public boolean isMonitorThreadAlive(); 

    public void setAutoReconnect(boolean val);

    public void disconnect();

    public void addObserver(Observer obs);
    
    public void deleteObserver(Observer obs);

}