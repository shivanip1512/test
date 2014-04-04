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

            inputMonitorTransport.setSelector("producerCount <> 1");
        }

        // In the activeMQ broker implementation, there is a race condition between the destination destruction and
        // the removal of consumer that prevents us from monitoring consumers on temporary destinations that we do
        // not own. Instead we will rely on monitoring the producers only, since we own the input destination.
        //  if (producer != null) {
        //      outputMonitorTransport =
        //          new AmqConsumerTransport(producer.getConnection(), AdvisorySupport.getConsumerAdvisoryTopic(producer
        //              .getDestination()), advisoryListener);
        //      outputMonitorTransport.setSelector("consumerCount <> 1");
        //  }
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
