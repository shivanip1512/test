package com.cannontech.services;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.messaging.serialization.MessageSerializer;

public abstract class ThriftServiceBase <Request, Reply> implements SessionAwareMessageListener<BytesMessage> {
    private Logger log = YukonLogManager.getLogger(ThriftServiceBase.class);
    
    @Autowired MessageSerializer<Request> requestDeserializer;
    @Autowired MessageSerializer<Reply> replySerializer;

    @Override
    public void onMessage(BytesMessage message, Session session) {
        try {
            
            //get bytes
            byte[] msgBytes = new byte[(int)message.getBodyLength()];
            message.readBytes(msgBytes);

            Reply reply;
            
            try {
                Request request = deserializeRequest(msgBytes);

                reply = handleRequest(request);
            } catch (Exception e) {
                reply = handleFailure();
            }

            BytesMessage outBytesMsg = serializeReply(session, reply);

            sendReply(message, session, outBytesMsg);
        } catch (Exception e) {
            log.error("Unable to send reply message", e);
        }
    }

    private Request deserializeRequest(byte[] msgBytes) {
        //deserialize message
        Request request = requestDeserializer.deserialize(null, msgBytes);
        log.debug("Received " + requestDeserializer.getClass() + " from yukon-server");
        return request;
    }

    protected abstract Reply handleRequest(Request request);

    protected abstract Reply handleFailure();

    private BytesMessage serializeReply(Session session, Reply reply) throws JMSException {
        BytesMessage outBytesMsg = session.createBytesMessage();
        outBytesMsg.writeBytes(replySerializer.serialize(null, reply));
        return outBytesMsg;
    }

    private void sendReply(BytesMessage message, Session session, BytesMessage outBytesMsg) throws JMSException {
        //send the reply to the temp queue
        MessageProducer producer = session.createProducer(message.getJMSReplyTo());
        producer.send(outBytesMsg);
    }
}
