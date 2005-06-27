package com.cannontech.message.dispatch.message;

import com.cannontech.message.dispatch.ClientConnection;

/**
 * A class to help send System Log events in a straight forward fasion.
 * Instantiate the class with an existing ClientConnection (to dispatch)
 * or call the constructor with no arguments to create a default connection.
 */
public class SystemLogHelper {

    private final ClientConnection _dispatchConnection;
    private final int _signalPointType;

    /**
     * Create a SystemLogHelper with an existing dispatch connection.
     * @param dispatchConnection
     */
    public SystemLogHelper(int signalPointType, ClientConnection dispatchConnection) {
        _signalPointType = signalPointType;
        _dispatchConnection = dispatchConnection;
    }
    
    /**
     * Create a SystemLogHelper with a default dispatch connection.
     */
    public SystemLogHelper(int signalPointType) {
        this(signalPointType, ClientConnection.createDefaultConnection("generic system logger"));
    }

    /**
     * Send a log message to dispatch.
     * @param action
     * @param description
     */
    public void log(String action, String description) {
        Signal sig = new Signal();
        sig.setPointID(_signalPointType);
        sig.setDescription(description);
        sig.setAction(action);
        sig.setCategoryID(Signal.EVENT_SIGNAL);

        _dispatchConnection.write(sig);
    }

}
