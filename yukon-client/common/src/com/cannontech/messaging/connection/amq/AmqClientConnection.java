package com.cannontech.messaging.connection.amq;

import javax.jms.MessageListener;

import com.cannontech.messaging.connection.transport.amq.AmqConsumerTransport;
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
    protected AmqConsumerTransport createMonitorTransport(TwoWayTransport transport, MessageListener listener) {
        AmqConsumerTransport monitor =
            new AmqConsumerTransport(transport.getConnection(), getAdvisoryTopicName(transport.getOutQueue()), listener);
        monitor.setSelector("consumerCount <> 1");
        return monitor;
    }

    @Override
    public void disconnect() {
        TwoWayTransport transport = getTransport();

        if (!isCloseRequested() && transport != null) {
            try {
                transport.emptyConsumerMessage();
            }
            catch (Exception e) {}
        }
        super.disconnect();
    }
    
    @Override
    public String toString() {
        return super.toString() + " (client side)";
    }
}
