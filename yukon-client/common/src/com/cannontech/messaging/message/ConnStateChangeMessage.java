package com.cannontech.messaging.message;

/**
 * Fired when the state of our connection is toggled either Connected -> Disconnected or Disconnected -> Connected
 * Server does not serialize this.
 */
public class ConnStateChangeMessage extends BaseMessage {

    // tells us if the connection is connected or not
    private boolean _isConnected = false;

    /**
     * ConnStateChange constructor comment.
     */
    public ConnStateChangeMessage(boolean isConnected) {
        super();
        _isConnected = isConnected;
    }

    public boolean isConnected() {
        return _isConnected;
    }

}
