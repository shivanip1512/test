package com.cannontech.message.util;

/**
 * Fired when the state of our connection is toggled either
 *  Connected -> Disconnected
 *  or
 *  Disconnected -> Connected
 * 
 * Server does not serialize this.
 */
public class ConnStateChange extends Message
{
    //tells us if the connection is connected or not
    private boolean _isConnected = false;

    /**
     * ConnStateChange constructor comment.
     */
    public ConnStateChange( boolean isConnected )
    {
    	super();
        _isConnected = isConnected;
    }
    
    public boolean isConnected()
    {
        return _isConnected;
    }

}