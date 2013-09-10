package com.cannontech.messaging.serialization.thrift.test.serverclienttest;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.cannontech.message.util.Message;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.connection.Connection.ConnectionState;
import com.cannontech.messaging.connection.amq.AmqClientConnection;
import com.cannontech.messaging.connection.amq.AmqConnectionFactoryService;
import com.cannontech.messaging.connection.event.MessageEventHandler;
import com.cannontech.messaging.serialization.thrift.test.MessageSerializationTestBase;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.ValidationResult;

public abstract class ServerToClientMessageSerializationTestBase extends MessageSerializationTestBase implements
    MessageEventHandler {

    protected ValidationResult globalResult;

    protected Queue<Message> recievedMessages;
    protected Connection conn;

    public ServerToClientMessageSerializationTestBase() {
        recievedMessages = new LinkedList<Message>();

        AmqConnectionFactoryService.getDefaultService()
            .setFactory(new ActiveMQConnectionFactory("tcp://localhost:61616"));
    }

    @Override
    protected String[] getContextUri() {
        return new String[] { "com/cannontech/messaging/thriftConnectionContext.xml",
                             "com/cannontech/messaging/serialization/thrift/test/messagevalidator/messageValidatorContext.xml" };
    }

    @Before
    public void setupTest() {
        recievedMessages.clear();

        conn = createClientConnection();
        conn.getMessageEvent().registerHandler(this);
        conn.start();
    }

    @Test
    @Ignore("Requires server test to be run concurrently")
    public void messageSerializationValidationTest() {

        Message msg;
        globalResult = new ValidationResult("Client Server Valiation", Object.class);

        while ((msg = this.syncRead(500)) != null) {
            ValidationResult result = new ValidationResult(msg.getClass());

            AutoInitializedClassValidator validator =
                (AutoInitializedClassValidator) validatorSvc.findValidator(msg.getClass());

            if (validator == null) {
                result.addError("No validator found");
            }
            else {
                result.addNestedResults(validator.validate(msg));
            }
            
            globalResult.addNestedResults(result);
        }

        if (conn.getState() != ConnectionState.Connected) {
            globalResult.addError("Connection close unexpectedly");
        }

        checkResults(globalResult);
    }

    @After
    public void tearDown() {
        if (conn != null) {
            conn.getMessageEvent().unregisterHandler(this);
            conn.close();
        }
    }

    public Connection createClientConnection() {
        AmqClientConnection client = null;

        client = new AmqClientConnection("", "com.eaton.eas.yukon.conntest");
        client.setMessageFactory(messageFactory);

        return client;
    }

    @Override
    public synchronized void onMessageEvent(Connection connection, Message message) {
        if (message != null) {
            recievedMessages.add(message);
            this.notifyAll();
        }
    }

    public synchronized Message syncRead(long timeout) {
        if (recievedMessages.isEmpty()) {
            try {
                this.wait(timeout);
            }
            catch (InterruptedException e) {}
        }
        return recievedMessages.poll();
    }
}
