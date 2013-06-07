package com.cannontech.messaging.message.server;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.messaging.message.BaseMessage;

/**
 * Request is used to send a request to a server application. A client submitting a request msg should keep track of the
 * id used in order to determine whether any subsequent requests are intended for them.
 * @author aaron
 */
public class ServerRequestMessage extends BaseMessage {

    private int id;
    private Object payload;

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object object) {
        payload = object;
    }

    public ServerResponseMessage createResponseMsg() {
        ServerResponseMessage responseMsg = new ServerResponseMessage(getId());
        return responseMsg;
    }

    @Override
    public String toString() {
        ToStringCreator toStringCreator = new ToStringCreator(this);
        toStringCreator.append("id", id);
        toStringCreator.append("payload", payload);
        return toStringCreator.toString();
    }
}
