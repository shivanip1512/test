package com.cannontech.messaging.connection.amq;

import javax.jms.MessageListener;

import com.cannontech.messaging.connection.transport.amq.TwoWayTransport;

public class AmqClientConnection extends AmqConnectionBase<TwoWayTransport> {

    public AmqClientConnection(String name, String queueName) {
        super(name, queueName);
        setManagedConnection(true);
    }

    @Override
    protected TwoWayTransport createTransport() {
        return HandShakeConnector.createClientConnectionTransport(this);
    }

    @Override
    protected AmqConnectionMonitor createConnectionMonitor(TwoWayTransport transport, MessageListener listener) {
        AmqConnectionMonitor monitor =
            new AmqConnectionMonitor(transport.getInTransport(), transport.getOutTransport(), listener);        
        return monitor;
    }
    
    @Override
    public String toString() {
        return super.toString() + " (client side)";
    }
    
    public String getServerHostName() {
        return getConnectionService().getBrokerUrl() + " @ " + getQueueName();
    }
}
