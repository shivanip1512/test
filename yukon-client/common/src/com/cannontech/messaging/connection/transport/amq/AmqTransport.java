package com.cannontech.messaging.connection.transport.amq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSession;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.messaging.connection.transport.Transport;
import com.cannontech.messaging.connection.transport.TransportException;

public class AmqTransport implements Transport {
    private boolean started;
    private ActiveMQConnection connection;
    private ActiveMQSession session;
    private MessageListener listener;

    // create a logger for instances of this class and its subclasses
    protected Logger logger = YukonLogManager.getLogger(this.getClass());

    public AmqTransport(ActiveMQConnection connection) {
        started = false;
        this.connection = connection;
    }

    @Override
    public void start() {
        started = true;

        if (session == null) {
            session = createSession();
        }
    }

    @Override
    public void close() {
        if (session != null && !session.isClosed()) {
            try {
                session.close();
                session.dispose();
            }
            catch (JMSException e) {
                // Log it but don't throw as we don't want to interrupt the closing/disconnecting operation
                // throw new TransportException("Unable to close the session for this Transport", e);
                logger.debug("Unable to close the AMQ session for this Transport", e);
            }
            finally {
                session = null;
            }
        }

        setListener(null);
    }

    protected ActiveMQSession createSession() {
        try {
            return (ActiveMQSession) connection.createSession(false, ActiveMQSession.AUTO_ACKNOWLEDGE);
        }
        catch (JMSException e) {
            throw new TransportException("Unable to create a session for this Transport", e);
        }
    }

    protected final void checkStarted(String message) throws TransportException {
        if (started) {
            throw new TransportException(message);
        }
    }

    public void sendMessage(Message message) throws TransportException {
        throw new TransportException("Operation not supported on this transport");
    }

    public void sendMessage(Message message, long timeToLive) {
        throw new TransportException("Operation not supported on this transport");
    }

    public Message receiveMessage() throws TransportException {
        throw new TransportException("Operation not supported on this transport");
    }

    public Message receiveMessage(long timeout) throws TransportException {
        throw new TransportException("Operation not supported on this transport");
    }

    public final ActiveMQConnection getConnection() {
        return connection;
    }

    public final void setConnection(ActiveMQConnection connection) {
        checkStarted("Can not change connection after the transport has been started");
        this.connection = connection;
    }

    public final ActiveMQSession getSession() {
        return session;
    }

    public final void setSession(ActiveMQSession session) {
        checkStarted("Can not change session after the transport has been started");
        this.session = session;
    }

    public MessageListener getListener() {
        return listener;
    }

    public void setListener(MessageListener listener) throws TransportException {
        this.listener = listener;
    }
}
