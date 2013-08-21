package com.cannontech.messaging.connection.amq;

import javax.jms.MessageListener;

import org.apache.activemq.advisory.AdvisorySupport;

import com.cannontech.messaging.connection.transport.amq.AmqConsumerTransport;
import com.cannontech.messaging.connection.transport.amq.AmqProducerTransport;

public class AmqConnectionMonitor {
    private AmqConsumerTransport inputMonitorTransport;
    private AmqConsumerTransport outputMonitorTransport;

    AmqConnectionMonitor(AmqConsumerTransport consumer, AmqProducerTransport producer, MessageListener advisoryListener) {
        if (consumer != null) {

            inputMonitorTransport =
                new AmqConsumerTransport(consumer.getConnection(), AdvisorySupport.getProducerAdvisoryTopic(consumer
                    .getDestination()), advisoryListener);
            
            // Due to a potential bug in ActiveMQ we deactivate this monitor (by setting the the listener to null)
            // Instead we rely on the double consumer "trick" in the TwoWayTransport to notify the other end of the connection that we are closing
            inputMonitorTransport.setListener(null);
            
            inputMonitorTransport.setSelector("producerCount <> 1");
        }

        if (producer != null) {
            outputMonitorTransport =
                new AmqConsumerTransport(producer.getConnection(), AdvisorySupport.getConsumerAdvisoryTopic(producer
                    .getDestination()), advisoryListener);
            outputMonitorTransport.setSelector("consumerCount <> 1");
        }
    }

    void start() {
        if (outputMonitorTransport != null) {
            outputMonitorTransport.start();
        }

        if (inputMonitorTransport != null) {
            inputMonitorTransport.start();
        }
    }

    void close() {
        if (outputMonitorTransport != null) {
            outputMonitorTransport.close();
        }

        if (inputMonitorTransport != null) {
            inputMonitorTransport.close();
        }
    }

    void setListener(MessageListener advisoryListener) {
        if (outputMonitorTransport != null) {
            outputMonitorTransport.setListener(advisoryListener);
        }

        if (inputMonitorTransport != null) {
            inputMonitorTransport.setListener(advisoryListener);
        }
    }
}
