package com.cannontech.messaging.connection.amq;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.messaging.connection.transport.TransportException;
import com.cannontech.messaging.connection.transport.amq.AmqConsumerTransport;
import com.cannontech.messaging.connection.transport.amq.AmqProducerTransport;
import com.cannontech.messaging.connection.transport.amq.TwoWayTransport;

class HandShakeConnector {

    private static final long INITIAL_WAIT_REPLY_TIME_MILLIS = 1000 * 15;       // 15 seconds
    private static final long WAIT_REPLY_TIME_MILLIS         = 1000 * 60 * 60;  // 1 hour
    private static final long TIME_TO_LIVE_MILLIS            = 1000 * 60 * 120; // 2 hours

    public static final String HAND_SHAKE_REQ_MSG_TYPE  = "com.eaton.eas.yukon.clientinit";
    public static final String HAND_SHAKE_RESP_MSG_TYPE = "com.eaton.eas.yukon.serverresp";
    public static final String HAND_SHAKE_ACK_MSG_TYPE  = "com.eaton.eas.yukon.clientack";

    // create a logger for instances of this class and its subclasses
    private static org.apache.log4j.Logger logger = YukonLogManager.getLogger("com.cannontech.messaging.connection.amq.HandShakeConnector");

    static TwoWayTransport createClientConnectionTransport(AmqClientConnection clientConnection) {
        AmqConsumerTransport consumer = null;
        AmqProducerTransport producer = null;
        AmqProducerTransport reqProducer = null;

        ActiveMQConnection connection = clientConnection.getConnection();

        try {
            // Create the consumer. It will create its own temporary queue
            consumer = new AmqConsumerTransport(connection, null, null);
            consumer.setManagedDestination(true);
            consumer.start();

            // Create the request-producer to send a request to the listenerConnection (the server)
            reqProducer = new AmqProducerTransport(connection, new ActiveMQQueue(clientConnection.getQueueName()));
            reqProducer.start();
            reqProducer.getProducer().setTimeToLive(TIME_TO_LIVE_MILLIS);

            Message rspMessage = null;

            do {
                // Create, setup and send the request message to the listenerConnection (the server)
                Message reqMsg = reqProducer.getSession().createMessage();
                reqMsg.setJMSReplyTo(consumer.getDestination());
                reqMsg.setJMSType(HAND_SHAKE_REQ_MSG_TYPE);
                reqProducer.sendMessage(reqMsg);

                long replyTimeMillis = (clientConnection.isConnectionFailed()) ? 
                        WAIT_REPLY_TIME_MILLIS : 
                        INITIAL_WAIT_REPLY_TIME_MILLIS;

                // and wait <replyTimeMillis> seconds for the reply
                rspMessage = consumer.receiveMessage(replyTimeMillis);

                if (rspMessage == null) {
                    // log warning and fire disconnect state event change
                    clientConnection.warnConnectingFailure("Timeout while waiting for the server to reply to a connection request");
                }

            } while (rspMessage == null);

            if (!HAND_SHAKE_RESP_MSG_TYPE.equals(rspMessage.getJMSType())) {
                throw new TransportException("The server response message is not of the expected type: " +
                                             rspMessage.getJMSType());
            }

            reqProducer.close();

            // Create the producer based on the reply-to address we just received
            producer = new AmqProducerTransport(connection, (ActiveMQDestination) rspMessage.getJMSReplyTo());
            producer.start();

            logger.info("Connection request from "+reqProducer.getDestination()+" connected through "+producer.getDestination());
            logger.info("  via "+producer.getConnection());
            
            // Create the resulting 2 way transport
            TwoWayTransport transport = new TwoWayTransport(producer, consumer);
            
            // start monitoring after validating a reply message
            clientConnection.setupConnectionMonitor(transport);
            
            // Create and send client connection acknowledge message
            Message ackMsg = producer.getSession().createMessage();
            ackMsg.setJMSReplyTo(consumer.getDestination());
            ackMsg.setJMSType(HAND_SHAKE_ACK_MSG_TYPE);
            
            producer.sendMessage(ackMsg);
            
            // Connection established, return the resulting 2 way transport
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

            // Create the resulting 2 way transport
            TwoWayTransport transport = new TwoWayTransport(producer, consumer);
            
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

            // start monitoring after validating acknowledgment message
            serverConnection.setupConnectionMonitor(transport);

            // Connection established, return the resulting 2 way transport
            return transport;
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
