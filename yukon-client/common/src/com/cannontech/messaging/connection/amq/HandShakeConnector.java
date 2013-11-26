package com.cannontech.messaging.connection.amq;

import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import com.cannontech.messaging.connection.transport.TransportException;
import com.cannontech.messaging.connection.transport.amq.AmqConsumerTransport;
import com.cannontech.messaging.connection.transport.amq.AmqProducerTransport;
import com.cannontech.messaging.connection.transport.amq.TwoWayTransport;

class HandShakeConnector {

    private static final int WAIT_REPLY_TIME_MILLIS = 30000;
    private static final int TIME_TO_LIVE_MILLIS = WAIT_REPLY_TIME_MILLIS - 1000;
    private static final int MAX_HAND_SHAKE_MSG_COUNT = 120;
    private static final String HAND_SHAKE_REQ_MSG_TYPE = "com.eaton.eas.yukon.clientinit";
    private static final String HAND_SHAKE_RESP_MSG_TYPE = "com.eaton.eas.yukon.serverresp";
    private static final String HAND_SHAKE_ACK_MSG_TYPE  = "com.eaton.eas.yukon.clientack";
    

    static TwoWayTransport createClientConnectionTransport(AmqClientConnection clientConnection) {
        AmqConsumerTransport consumer = null;
        AmqProducerTransport producer = null;
        AmqProducerTransport reqProducer = null;

        ActiveMQConnection connection = clientConnection.getConnection();

        final int maxHandShakeMsgCount = ( clientConnection.isConnectionFailed() ) ? MAX_HAND_SHAKE_MSG_COUNT : 1;

        try {
            // Create the consumer. It will create its own temporary queue
            consumer = new AmqConsumerTransport(connection, null, null);
            consumer.setManagedDestination(true);
            consumer.start();

            // Create the request-producer to send a request to the listenerConnection (the server)
            reqProducer = new AmqProducerTransport(connection, new ActiveMQQueue(clientConnection.getQueueName()));
            reqProducer.start();
            reqProducer.getProducer().setTimeToLive(TIME_TO_LIVE_MILLIS);

            Message rspMessage;
            int connectionRequestCount = 0;
            do {
                // Create, setup and send the request message to the listenerConnection (the server)
                Message reqMsg = reqProducer.getSession().createMessage();
                reqMsg.setJMSReplyTo(consumer.getDestination());
                reqMsg.setJMSType(HAND_SHAKE_REQ_MSG_TYPE);
                reqProducer.sendMessage(reqMsg);

                // and wait <WAIT_REPLY_TIME_MILLIS> seconds for the reply
                rspMessage = consumer.receiveMessage(WAIT_REPLY_TIME_MILLIS);
                
            } while (connectionRequestCount++ < maxHandShakeMsgCount && rspMessage == null);

            // Validate
            if (rspMessage == null) {
                throw new TransportException("Timeout while waiting for the server to reply to a connection request");
            }

            if (!HAND_SHAKE_RESP_MSG_TYPE.equals(rspMessage.getJMSType())) {
                throw new TransportException("The server response message is not of the expected type: " +
                                             rspMessage.getJMSType());
            }

            // Connection established
            reqProducer.close();

            // Create the producer based on the reply-to address we just received
            producer = new AmqProducerTransport(connection, (ActiveMQDestination) rspMessage.getJMSReplyTo());
            producer.start();

            // Create and send client connection acknowledge message
            Message ackMsg = producer.getSession().createMessage();
            ackMsg.setJMSReplyTo(consumer.getDestination());
            ackMsg.setJMSType(HAND_SHAKE_ACK_MSG_TYPE);
            
            producer.sendMessage(ackMsg);
            
            // Create the resulting 2 way transport
            TwoWayTransport transport = new TwoWayTransport(producer, consumer);
            return transport;
        }
        catch (TransportException e) {
        	throw e;
        }        
        catch (Exception e) {
            try {
                if (producer != null) {
                    producer.close();
                }

                if (consumer != null) {
                    consumer.close();
                }
            }
            catch (Exception ignored) {}

            throw new TransportException("Error while establishing a connection with the server", e);
        }
    }

    static TwoWayTransport createServerConnectionTransport(AmqServerConnection serverConnection) {
        ActiveMQConnection connection = serverConnection.getConnection();
        AmqProducerTransport producer = null;
        AmqConsumerTransport consumer = null;

        try {
            // Create the producer bound to the address of this serverConnection
            producer = new AmqProducerTransport(connection, serverConnection.getDestination());
            producer.start();

            // Create the consumer. It will create its own temporary queue
            consumer = new AmqConsumerTransport(connection, null, null);
            consumer.setManagedDestination(true);
            consumer.start();

            // Create, setup and send the response message to the clientConnection
            Message rspMsg = producer.getSession().createMessage();
            rspMsg.setJMSReplyTo(consumer.getDestination());
            rspMsg.setJMSType(HAND_SHAKE_RESP_MSG_TYPE);
            producer.sendMessage(rspMsg);

            // wait for a reply from client connection
            Message ackMsg = consumer.receiveMessage(WAIT_REPLY_TIME_MILLIS);
            
            // Validate
            if (ackMsg == null) {
                throw new TransportException("Timeout while waiting client acknowledge message");
            }

            if (!HAND_SHAKE_ACK_MSG_TYPE.equals(ackMsg.getJMSType())) {
                throw new TransportException("The client acknowledge message is not of the expected type: " +
                                              ackMsg.getJMSType() + ", expected " + HAND_SHAKE_ACK_MSG_TYPE);
            }
            
            if ( ackMsg.getJMSReplyTo() == null )
            {
                throw new TransportException("The client acknowledge message replyto destination is null " +
                                             ", expected " + producer.getDestination().getPhysicalName());
            }
            
            ActiveMQDestination clientAckDest = (ActiveMQDestination) ackMsg.getJMSReplyTo();
            
            if ( ! producer.getDestination().getPhysicalName().equals(clientAckDest.getPhysicalName()))
            {
                throw new TransportException("The client acknowledge message does not have the expected replyto destination: " +
                                              clientAckDest.getPhysicalName() + ", expected " + producer.getDestination().getPhysicalName());
            }
            
            // Connection established, create the resulting 2 way transport
            return new TwoWayTransport(producer, consumer);
        }
        catch (Exception e) {
            try {
                if (producer != null) {
                    producer.close();
                }

                if (consumer != null) {
                    consumer.close();
                }
            }
            catch (Exception ignored) {}

            throw new TransportException("Error while establishing a connection with a client", e);
        }
    }

    static AmqConsumerTransport createAdvisoryMonitorTransport(TwoWayTransport transport,
                                                               MessageListener advisoryListener) {
        AmqConsumerTransport monitor =
            new AmqConsumerTransport(transport.getConnection(), transport.getInQueue(), advisoryListener);
        monitor.setSession(transport.getInTransport().getSession());
        return monitor;
    }
}
